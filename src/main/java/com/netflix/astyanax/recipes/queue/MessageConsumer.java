package com.netflix.astyanax.recipes.queue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.netflix.astyanax.recipes.locks.BusyLockException;

public interface MessageConsumer {
    /**
     * Acquire up to N items from the queue.  Each item must be released by calling ackMessage.
     * 
     * TODO: Items since last process time
     * 
     * @param itemsToRead
     * @return
     * @throws InterruptedException 
     * @throws Exception 
     */
    Collection<Message> readMessages(int itemsToRead) throws MessageQueueException, BusyLockException, InterruptedException;

    /**
     * Acquire up to N items from the queue.  Each item must be released by calling ackMessage.
     * 
     * @param itemsToRead
     * @param timeout
     * @param units
     * @return
     */
    Collection<Message> readMessages(int itemsToRead, long timeout, TimeUnit units) throws MessageQueueException, BusyLockException, InterruptedException;
    
    /**
     * Peek into messages from the queue.  The queue state is not altered by this operation.
     * @param itemsToPop
     * @return
     * @throws MessageQueueException
     */
    Collection<Message> peekMessages(int itemsToPop) throws MessageQueueException;

    /**
     * Release a job after completion
     * @param item
     * @throws Exception 
     */
    void ackMessage(Message message) throws MessageQueueException;

    /**
     * Release a set of jobs
     * @param items
     */
    void ackMessages(Collection<Message> messages) throws MessageQueueException;
    
}
