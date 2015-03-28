package com.github.blamevic.irc;

import com.github.blamevic.enumerators.Enumerator;
import com.github.blamevic.irc.IRCMessageParser.PrivateMessage;

import java.util.List;
import java.util.Set;

public interface IRCConnectionManager {
    /**
     * @return the list of joined IRC channels
     */
    Set<String> channels();

    /**
     * Joins an IRC channel
     *
     * @param channel The channel to join.
     */
    void joinChannel(String channel);

    /**
     * Leaves an IRC channel
     *
     * @param channel The channel to leave
     * @return true if the channel was left
     */
    boolean leaveChannel(String channel);

    /**
     * Leaves an IRC channel
     *
     * @param channel The channel to leave
     * @param reason The reason given to the leave message
     * @return true if the channel was left
     */
    boolean leaveChannel(String channel, String reason);

    /**
     * Gets an {@code Enumerator<PrivateMessage>} relating to a specific IRC channel
     *
     * @param channel the channel to get the enumerator for
     * @return an enumerator of messages in the channel
     */
    Enumerator<PrivateMessage> channelEnumerator(String channel);

    /**
     * Gets the IRC Client
     *
     * @return an IRC Client.
     */
    IRCClient client();
}
