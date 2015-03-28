package com.github.blamevic.irc;


public class ThreadedIRCConnectionManagerTest extends AbstractIRCConnectionManagerTest {
    @Override
    public IRCConnectionManager getManager(IRCClient client) {
        return new ThreadedIRCConnectionManager(client);
    }
}