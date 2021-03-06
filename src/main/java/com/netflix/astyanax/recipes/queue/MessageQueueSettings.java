package com.netflix.astyanax.recipes.queue;

/**
 * MessageQueueSettings settings that are persisted to cassandra
 */
class MessageQueueSettings {
    private long  partitionDuration   = ShardedDistributedMessageQueue.DEFAULT_BUCKET_DURATION;
    private int   partitionCount      = ShardedDistributedMessageQueue.DEFAULT_BUCKET_COUNT;
    private long  visibilityTimeout   = ShardedDistributedMessageQueue.DEFAULT_VISIBILITY_TIMEOUT;
    private int   shardCount          = ShardedDistributedMessageQueue.DEFAULT_SHARD_COUNT;
    
    public long getPartitionDuration() {
        return partitionDuration;
    }
    public int getPartitionCount() {
        return partitionCount;
    }
    public long getVisibilityTimeout() {
        return visibilityTimeout;
    }
    public int getShardCount() {
        return shardCount;
    }
    public void setPartitionDuration(long partitionDuration) {
        this.partitionDuration = partitionDuration;
    }
    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }
    public void setVisibilityTimeout(long visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }
    public void setShardCount(int shardCount) {
        this.shardCount = shardCount;
    }
    
    @Override
    public String toString() {
        return "MessageQueueSettings [partitionDuration=" + partitionDuration + ", partitionCount=" + partitionCount
                + ", visibilityTimeout=" + visibilityTimeout + ", shardCount=" + shardCount + "]";
    }
}