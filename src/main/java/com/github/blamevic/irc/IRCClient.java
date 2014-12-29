package com.github.blamevic.irc;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IRCClient {
    public String hostname;
    public int    port;

    public String username;
    public String realname;

    public boolean debug;

    public IRCMessageSender sender = new IRCMessageSender(this);

    private Socket         sock;
    private BufferedReader in;
    private BufferedWriter out;

    private Date lastDebug;

    private NumberFormat diffFormat = new DecimalFormat("#000.00");
    private DateFormat   timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    /**
     * Initiates an IRCClient
     *
     * @param hostname
     * @param port
     * @param username
     * @param debug
     */
    public IRCClient(String hostname, int port, String username, String realname, boolean debug) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.debug = debug;
        this.realname = realname;
    }

    public IRCClient(String hostname, int port, String username, String realname) {
        this(hostname, port, username, realname, false);
    }

    public IRCClient(String hostname, String username, String realname, boolean debug) {
        this(hostname, 6667, username, realname, debug);
    }

    public IRCClient(String hostname, String username, String realname) {
        this(hostname, username, realname, false);
    }

    public void connect() throws IOException {
        this.sock = new Socket(hostname, port);
        this.in = new BufferedReader(new InputStreamReader(
                sock.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(
                sock.getOutputStream()));
    }

    public void login(String password) {
        sender.pass(password);
        login();
    }

    public void login() {
        sender.nick(username);
        sender.user(username, 0, realname);
    }

    public boolean joinChannel(String channel) {
        return sender.join(channel);
    }

    public boolean leaveChannel(String... channels) {
        return sender.part(channels);
    }

    public boolean leaveChannel(String channel, String reason) {
        return sender.part(channel, reason);
    }

    public boolean sendMessage(String message, String target) {
        return sender.privateMessage(message, target);
    }

    /**
     * Blocks until the next command specified
     *
     * @param command The command to watch for e.g. PING or 001
     * @return All of the messages recieved, including the one that triggered
     * the return
     */
    public List<String> waitForCommand(String command, boolean processping) {
        final List<String> lines = new ArrayList<String>();
        while (true) {
            String line = readLine();
            if (line != null) {
                lines.add(line);
                if (processping)
                    processPing(line);
                if (IRCMessageParser.getCommand(line).equals(command))
                    break;
            }
        }
        return lines;
    }

    public List<String> waitForCommand(String command) {
        return waitForCommand(command, true);
    }

    /**
     * Checks a string to see if it is a PING command and if so replies to it.
     *
     * @param message A single message with no line endings.
     * @return True if the message is a PING, else false.
     */
    public boolean processPing(String message) {
        if (message.startsWith("PING")) {
            writeLine(message.replace("PING", "PONG"));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Writes a line to IRC and then flushes the buffer
     *
     * @param text A non-terminated string
     * @return True if no IOException was thrown.
     */
    public boolean writeLine(String text) {
        try {
            out.write(text + "\r\n");
            out.flush();
            debug(">>", text);
            return true;
        } catch (IOException e) {
            System.out.println(">!> Failed to write to socket " + hostname
                    + " :");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads a line from IRC
     *
     * @return The line read, or null if an exception was thrown or the end of
     * the stream was reached.
     */
    public String readLine() {
        String line;
        try {
            if (in.ready()) {
                line = in.readLine();
                debug("<<",line);
            } else {
                line = null;
            }
        } catch (IOException e) {
            line = null;
            System.out.println("<!< Failed to read from socket " + hostname
                    + " :");
            e.printStackTrace();
        }
        return line;
    }

    public void debug(String prefix, String message) {
        if (debug) {
            double diff;
            Date currentDebug = new Date();
            if (lastDebug != null) {
                diff = ((double)(currentDebug.getTime() - lastDebug.getTime()) / 1000);
            } else {
                diff = 0;
            }
            lastDebug = currentDebug;
            System.out.println(timeFormat.format(currentDebug) + " +" + diffFormat.format(diff) + " " + prefix + " " + message);
        }
    }
}
