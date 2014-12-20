package com.github.blamevic.irc;

public class IRCMessageParser {
    public static Prefix getPrefix(String message) {
        return parseMessage(message).prefix;
    }

    public static String getCommand(String message) {
        return parseMessage(message).command;
    }

    public static String getParams(String message) {
        return parseMessage(message).params;
    }

    public static Message parseMessage(String message) {
        if (message == null) return null;

        String prefix = null;
        String command;
        String params;

        String theRest = message;

        if (message.startsWith(":")) {
            int prefixEnd = message.indexOf(" ");
            prefix = message.substring(1, prefixEnd);
            theRest = message.substring(prefixEnd + 1);
        }

        int commandEnd = theRest.indexOf(" ");
        command = theRest.substring(0, commandEnd);

        params = theRest.substring(commandEnd + 1);

        return new Message(parsePrefix(prefix), command, params);
    }

    public static class Message {
        public Prefix prefix;
        public String command;
        public String params;

        public Message(Prefix prefix, String command, String params) {
            this.prefix = prefix;
            this.command = command;
            this.params = params;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "prefix='" + prefix + '\'' +
                    ", command='" + command + '\'' +
                    ", params='" + params + '\'' +
                    '}';
        }
    }

    public static String getName(String prefix) {
        return parsePrefix(prefix).name;
    }

    public static String getUser(String prefix) {
        return parsePrefix(prefix).user;
    }

    public static String getHost(String prefix) {
        return parsePrefix(prefix).host;
    }

    public static Prefix parsePrefix(String prefix) {
        if (prefix == null) return null;

        String name = null;
        String user = null;
        String host = null;

        int type = 0;
        if (prefix.contains("!")) {
            type = 1;
            if (prefix.contains("@")) {
                type = 2;
            }
        }

        switch (type) {
            case 0:
                name = prefix;
                break;

            case 2:
                int hostIndex = prefix.indexOf("@");
                host = prefix.substring(hostIndex + 1);
                prefix = prefix.substring(0, hostIndex);
                //Fall through to next case statement

            case 1:
                int userIndex = prefix.indexOf("!");
                name = prefix.substring(0, userIndex);
                user = prefix.substring(userIndex + 1);
                break;
        }

        return new Prefix(name, user, host);
    }

    public static class Prefix {
        public String name; //Either server name or nick
        public String user;
        public String host;

        public Prefix(String name, String user, String host) {
            this.name = name;
            this.user = user;
            this.host = host;
        }

        @Override
        public String toString() {
            return "Prefix{" +
                    "name='" + name + '\'' +
                    ", user='" + user + '\'' +
                    ", host='" + host + '\'' +
                    '}';
        }
    }

    public static PrivateMessage parsePrivateMessage(String message) {
        Message theMessage = parseMessage(message);
        return parsePrivateMessage(theMessage);
    }

    public static PrivateMessage parsePrivateMessage(Message theMessage) {
        if (theMessage == null) return null;
        if (!theMessage.command.equals("PRIVMSG")) return null;

        Prefix prefix = theMessage.prefix;
        String target;
        String message;

        int splitPoint = theMessage.params.indexOf(":");
        target = theMessage.params.substring(0, splitPoint).trim();
        message = theMessage.params.substring(splitPoint + 1);

        return new PrivateMessage(prefix, target, message);
    }

    public static class PrivateMessage {
        public Prefix prefix;
        public String target;
        public String message;

        public PrivateMessage(Prefix prefix, String target, String message) {
            this.prefix = prefix;
            this.target = target;
            this.message = message;
        }

        public void reply(String message, IRCClient client) {
            String target;
            if (targetIsAChannel()) {
                target = this.target;
            } else {
                target = this.prefix.user;
            }
            new IRCMessageSender(client).privateMessage(message, target);
        }

        public boolean targetIsAChannel() {
            return IRCMessageParser.stringIsAChannel(this.target);
        }

        public boolean isTargetSelf(IRCClient client) {
            return (client.username.equals(this.target));
        }

        @Override
        public String toString() {
            return "PrivateMessage{" +
                    "prefix=" + prefix +
                    ", target='" + target + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static boolean stringIsAChannel(String theString) {
        char first = theString.charAt(0);
        if      (first == '#') return true;
        else if (first == '&') return true;
        else if (first == '+') return true;
        else if (first == '!') return true;
        else return false;
    }
}
