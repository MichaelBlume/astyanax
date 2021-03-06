package com.netflix.astyanax.recipes.queue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Message {
    /**
     * Last execution time, this value changes as the task state is transitioned.  
     * The token is a timeUUID and represents the next execution/expiration time
     * within the queue.
     */
    private UUID  token;
    
    /**
     * Data associated with the task
     */
    private String  data;
    
    /**
     * Execution time for the task in milliseconds
     */
    private long    triggerTime = 0;

    /**
     * Map of task arguments.
     */
    private Map<String, Object> parameters;
    
    /**
     * Lower value priority tasks get executed first
     */
    private short priority = 0;
    
    /**
     * Timeout value in seconds
     */
    private Integer timeout = 10;
    
    public Message() {
        
    }
    
    public Message(String data) {
        this.data = data;
    }
    
    public Message(UUID token, String data) {
        this.token = token;
        this.data   = data;
    }
    
    public UUID getToken() {
        return token;
    }

    public String getData() {
        return data;
    }

    public Message setToken(UUID token) {
        this.token = token;
        return this;
    }

    public Message setData(String data) {
        this.data = data;
        return this;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public Message setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
        return this;
    }
    
    public Message setDelay(Long delay, TimeUnit units) {
        this.triggerTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay,  units);
        return this;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public short getPriority() {
        return priority;
    }

    public Integer getTimeout() {
        if (timeout == null)
            return 0;
        return timeout;
    }

    /**
     * Setting priority will NOT work correctly with a future trigger time due to 
     * internal data model implementations.
     * @param priority
     * @return
     */
    public Message setPriority(short priority) {
        this.priority = priority;
        return this;
    }

    public Message setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }
    
    public Message setTimeout(Long timeout, TimeUnit units) {
        this.timeout = (int)TimeUnit.SECONDS.convert(timeout, units);
        return this;
    }

    @Override
    public String toString() {
        return "Message [token=" + token + ", data=" + data + ", triggerTime=" + triggerTime + ", parameters=" + parameters
                + ", priority=" + priority + ", timeout=" + timeout + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + priority;
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + (int) (triggerTime ^ (triggerTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (priority != other.priority)
            return false;
        if (timeout == null) {
            if (other.timeout != null)
                return false;
        } else if (!timeout.equals(other.timeout))
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        if (triggerTime != other.triggerTime)
            return false;
        return true;
    }
    
    
}
