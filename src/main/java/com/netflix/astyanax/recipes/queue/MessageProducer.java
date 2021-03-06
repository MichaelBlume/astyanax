package com.netflix.astyanax.recipes.queue;

import java.util.Collection;
import java.util.Map;

public interface MessageProducer {
    /**
     * Schedule a job for execution
     * @param message
     * @return UUID assigned to this message 
     * 
     * @throws MessageQueueException
     */
    String sendMessage(Message message) throws MessageQueueException;

    /**
     * Schedule a batch of jobs
     * @param messages
     * @return Map of messages to their assigned UUIDs
     * 
     * @throws MessageQueueException
     */
    Map<Message, String> sendMessages(Collection<Message> messages) throws MessageQueueException;
}
