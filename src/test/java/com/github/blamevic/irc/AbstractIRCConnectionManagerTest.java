package com.github.blamevic.irc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Abstract class that can be extended/used for all {@link com.github.blamevic.irc.IRCConnectionManager}
 *
 * @author rx14
 * @author shadowfacts
 */
public abstract class AbstractIRCConnectionManagerTest {

    /**
     * @return The {@link com.github.blamevic.irc.IRCConnectionManager} to be used for this subclass of {@link com.github.blamevic.irc.AbstractIRCConnectionManagerTest}
     */
    public abstract IRCConnectionManager getManager(IRCClient client);

	IRCClient client;
    IRCConnectionManager manager;

    @Before
    public void setUp() throws IOException {
		client = new IRCClient("localhost", 6667, "shadowfacts", "shadowfacts", true);
		client.connect();
		manager = getManager(client);
	}

	@After
	public void exit() {
		manager.channels().forEach(manager::leaveChannel);
	}

	@Test
	public void testGetChannels() {
		manager.joinChannel("#test");
		assertTrue("can get channel back", manager.channels().contains("#test"));
	}

	@Test
	public void testLeaveChannel() {
		manager.joinChannel("#test");
		manager.leaveChannel("#test");
		assertFalse("can leave channel", manager.channels().contains("#test"));
	}

	@Test
	public void testLeaveChannelWithReason() {
		manager.joinChannel("#test");
		manager.leaveChannel("#test", "RX14-chibi told me to");
		assertFalse("can leave channel with reason", manager.channels().contains("#test"));
	}

	@Test
	public void testLeaveAllChannels() {
		manager.joinChannel("#test");
		manager.joinChannel("#foo");
		manager.leaveChannel("#test");
		manager.leaveChannel("#foo");
		assertTrue("can leave all channels", manager.channels().isEmpty());
	}
}
