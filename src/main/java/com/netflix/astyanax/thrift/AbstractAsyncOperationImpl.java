/*******************************************************************************
 * Copyright 2011 Netflix
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.netflix.astyanax.thrift;

import com.netflix.astyanax.connectionpool.ConnectionPool;
import com.netflix.astyanax.connectionpool.ExecuteWithFailover;
import com.netflix.astyanax.connectionpool.Operation;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.OperationResultImpl;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncMethodCall;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractAsyncOperationImpl<R, A extends TAsyncMethodCall> implements
        Callable<OperationResult<R>>,
        AsyncMethodCallback<A>,
        AsyncOperation<R,A> {
    private final ExecutorService executorService;
    private final String keyspaceName;
    private final BigInteger key;
    private final BlockingQueue<AsyncResult<R>> queue = new ArrayBlockingQueue<AsyncResult<R>>(1);
    private final ExecuteWithFailover<Cassandra.AsyncClient, R> executeWithFailover;
    private final long startTicks;
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);

    public AbstractAsyncOperationImpl(ConnectionPool<Cassandra.AsyncClient> connectionPool,
                                      ExecutorService executorService,
                                      String keyspaceName,
                                      BigInteger key) throws ConnectionException {
        this.executorService = executorService;
        this.keyspaceName = keyspaceName;
        this.key = key;
        startTicks = System.currentTimeMillis();
        executeWithFailover = connectionPool.newExecuteWithFailover();
    }

    public AbstractAsyncOperationImpl(ConnectionPool<Cassandra.AsyncClient> connectionPool,
                                      ExecutorService executorService,
                                      String keyspaceName) throws ConnectionException {
        this(connectionPool, executorService, keyspaceName, null);
    }

    public void     start()
    {
        executorService.submit(
            new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    executeWithFailover.tryOperation(
                        new Operation<Cassandra.AsyncClient, R>() {
                            @Override
                            public R execute(Cassandra.AsyncClient client) throws ConnectionException {
                                startOperation(client);
                                return null;
                            }

                            @Override
                            public BigInteger getKey() {
                                return key;
                            }

                            @Override
                            public String getKeyspace() {
                                return keyspaceName;
                            }
                        }
                    );
                    return null;
                }
            }
        );
    }

    @Override
    public OperationResult<R> call() throws Exception {
        AsyncResult<R>      asyncResult = queue.take();
        if ( asyncResult.exception != null ) {
            throw asyncResult.exception;
        }
        return new OperationResultImpl<R>(executeWithFailover.getHost(), asyncResult.result, System.currentTimeMillis() - startTicks);
    }

    @Override
    public void onComplete(A response) {
        try {
            R result = finishOperation(response);
            offerResult(result);
        }
        catch (ConnectionException e) {
            offerException(e);
        }
    }

    @Override
    public void onError(Exception e) {
        offerException(ThriftConverter.ToConnectionPoolException(e));
    }

    public void cancel() {
        isCanceled.set(true);
    }

    protected void offerException(ConnectionException e) {
        try {
            if ( !isCanceled.get() && !Thread.currentThread().isInterrupted() ) {
                executeWithFailover.informException(e);
                start();
            }
            else {
                queue.offer(new AsyncResult<R>(null, e));
            }
        }
        catch (Exception finalException) {
            queue.offer(new AsyncResult<R>(null, finalException));
        }
    }

    protected void offerResult(R result) {
        queue.offer(new AsyncResult<R>(result, null));
    }

}