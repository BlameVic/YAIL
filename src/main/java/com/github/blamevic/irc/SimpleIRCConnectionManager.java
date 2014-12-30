package com.github.blamevic.irc;

import com.github.blamevic.irc.IRCMessageParser.PrivateMessage;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleIRCConnectionManager implements IIRCConnectionManager {
    IRCClient client;
    List<String> channels;

    Map<String, List<PrivateMessage>> queues;

    Lock queueLock;

    public SimpleIRCConnectionManager(IRCClient client) {
        this.client = client;
        this.queueLock = new ReentrantLock();
        this.channels = new ArrayList<>();
        this.queues = new HashMap<>();
    }

    @Override
    public List<String> getChannels() {
        return this.channels;
    }

    @Override
    public void joinChannel(String channel) {
        if (client.joinChannel(channel))
            channels.add(channel);
    }

    @Override
    public boolean leaveChannel(String channel) {
        if (channels.contains(channel)) {
            client.leaveChannel(channel);
            channels.remove(channel);
            return true;
        } else
            return false;
    }

    @Override
    public boolean leaveChannel(String channel, String reason) {
        if (channels.contains(channel)) {
            client.leaveChannel(channel, reason);
            channels.remove(channel);
            return true;
        } else
            return false;
    }

    @Override
    public Enumerator<PrivateMessage> getEnumeratorForChannel(final String channel) {
        return new Enumerator<PrivateMessage>() {
            PrivateMessage currentMessage;

            @Override
            public PrivateMessage getCurrent() {
                loadQueue();

                return currentMessage;
            }

            @Override
            public boolean moveNext() {
                loadQueue();

                queueLock.lock();
                if (queues.get(channel).size() > 0) {
                    currentMessage = queues.get(channel).remove(0);
                    queueLock.unlock();
                    return true;
                } else {
                    queueLock.unlock();
                    return false;
                }
            }
        };
    }

    private void loadQueue() {
        queueLock.lock();
        while (true) {
            String line = client.readLine();
            if (line == null) break;
            if (client.processPing(line)) continue;

            PrivateMessage message = IRCMessageParser.parsePrivateMessage(line);
            if (message == null) continue;
            if (!message.targetIsAChannel()) continue;

            String channel = message.target;
            if (!channels.contains(channel)) continue;
            if (!queues.containsKey(channel)) queues.put(channel, new LinkedList<>());
            queues.get(channel).add(message);
        }
        queueLock.unlock();
    }
}
