package com.github.blamevic.irc;

import com.github.blamevic.irc.IRCMessageParser.PrivateMessage;

import java.util.List;

public interface IIRCConnectionManager {
    /**
     * @return the list of joined IRC channels
     */
    List<String> getChannels();

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
     * @return whether the channel was joined
     */
    boolean leaveChannel(String channel);

    /**
     * Leaves an IRC channel
     *
     * @param channel The channel to leave
     * @param reason The reason given to the leave message
     */
    boolean leaveChannel(String channel, String reason);

    /**
     * Gets an {@code Enumerator<PrivateMessage>} relating to a specific IRC channel
     *
     * @param channel the channel to get the enumerator for
     * @return an enumerator of messages in the channel
     */
    Enumerator<PrivateMessage> getEnumeratorForChannel(String channel);
}
