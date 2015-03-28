package com.github.blamevic.irc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractIRCConnectionManagerTest {

    /**
     * @return a new IIRCConnectionManager
     */
    public abstract IRCConnectionManager getManager(IRCClient client);

    IRCConnectionManager manager;

    @Before
    public void setUp() throws Exception {
        //manager = getManager();
    }

    @Test
    public void testGetChannels() throws Exception {
        manager.joinChannel("#test");
        manager.joinChannel("#foo");

        assertTrue("can read back channels", manager.channels().contains("#test"));

        manager.leaveChannel("#test");

        assertFalse("can remove channels", manager.channels().contains("#test"));

        manager.leaveChannel("#foo", "I'm leaving because of #bar");

        assertFalse("can remove channels with reason", manager.channels().contains("#foo"));
        assertTrue("after the test, i have left all channels", manager.channels().isEmpty());
    }

    @Test
    public void testJoinChannel() throws Exception {

    }

    @Test
    public void testLeaveChannel() throws Exception {

    }

    @Test
    public void testLeaveChannel1() throws Exception {

    }

    @Test
    public void testGetEnumeratorForChannel() throws Exception {

    }
}
