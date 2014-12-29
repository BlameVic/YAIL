package com.github.blamevic.irc;

public class IRCMessageSender {
    IRCClient client;

    public IRCMessageSender(IRCClient client) {
        this.client = client;
    }

    public boolean command(String... sends) {
        return client.writeLine(String.join(" ", sends));
    }

    public boolean privateMessage(String message, String destination) {
        return command("PRIVMSG", destination, ":" + message);
    }

    public boolean msg(String message, String destination) {
        return privateMessage(message, destination);
    }

    public boolean join(String... channels) {
        return command("JOIN", String.join(",", channels));
    }

    public boolean join(String channel, String key) {
        return command("JOIN", channel, key);
    }

    public boolean part(String... channels) {
        return command("PART", String.join(",", channels));
    }

    public boolean part(String channel, String reason) {
        return command("PART", channel, ":" + reason);
    }

    public boolean pass(String password) {
        return command("PASS", password);
    }

    public boolean nick(String nickname) {
        return command("NICK", nickname);
    }

    public boolean user(String username, int mode, String realname) {
        return command("USER", username, Integer.toString(mode), "*", ":" + realname);
    }
}
