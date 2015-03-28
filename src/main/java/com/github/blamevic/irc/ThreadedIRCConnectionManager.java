package com.github.blamevic.irc;

import com.github.blamevic.enumerators.Enumerator;
import com.github.blamevic.enumerators.Enumerators;
import com.github.blamevic.irc.IRCMessageParser.PrivateMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadedIRCConnectionManager implements IRCConnectionManager {
    final IRCClient client;
    final Map<String, BlockingQueue<PrivateMessage>> queues;

    public ThreadedIRCConnectionManager(IRCClient client) {
        this.client = client;
        this.queues = new ConcurrentHashMap<>();
        new Thread(new ChannelRunner()).run();
    }

    @Override
    public Set<String> channels() {
        return queues.keySet();
    }

    @Override
    public void joinChannel(String channel) {
        if (client.joinChannel(channel))
            queues.put(channel, new LinkedBlockingQueue<>());
    }

    @Override
    public boolean leaveChannel(String channel) {
        return leaveChannel(channel, "");
    }

    @Override
    public boolean leaveChannel(String channel, String reason) {
        if (queues.containsKey(channel) && client.leaveChannel(channel, reason)) {
            queues.remove(channel);
            return true;
        } else
            return false;
    }

    @Override
    public Enumerator<PrivateMessage> channelEnumerator(String channel) {
        return Enumerators.fromIterator(queues.get(channel).iterator());
    }

    @Override
    public IRCClient client() {
        return client;
    }

    class ChannelRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                String line = client.readLine();
                if (line == null) break;
                if (client.processPing(line)) continue;

                PrivateMessage message = IRCMessageParser.parsePrivateMessage(line);
                if (message == null) continue;
                if (!message.targetIsAChannel()) continue;

                String channel = message.target;
                if (!queues.containsKey(channel)) continue;
                queues.get(channel).add(message);
            }
        }
    }
}
