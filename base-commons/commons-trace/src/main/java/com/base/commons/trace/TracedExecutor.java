package com.base.commons.trace;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
public class TracedExecutor implements ListeningExecutorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ListeningExecutorService executorService;

    public TracedExecutor(ListeningExecutorService executorService) {
        this.executorService = executorService;

        // 注册钩子函数，在JVM接收到停止指令后会运行该线程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("invoke shutdown hook ....");

            executorService.shutdown();
            long time = System.currentTimeMillis();
            while (true) {
                if (executorService.isTerminated()) {
                    logger.info("thread pool is shutdown ...");
                    break;
                }

                if (System.currentTimeMillis() - time > 5 * 1000) {
                    executorService.shutdownNow();
                }
            }
        }));
    }

    public TracedExecutor(ExecutorService executorService) {
        this(MoreExecutors.listeningDecorator(executorService));
    }

    public static TracedExecutor of(ListeningExecutorService executorService) {
        return new TracedExecutor(executorService);
    }

    public static TracedExecutor of(ExecutorService executorService) {
        return new TracedExecutor(executorService);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> ListenableFuture<T> submit(Callable<T> task) {
        return executorService.submit(CallableWrapper.of(task));
    }

    @Override
    public ListenableFuture<?> submit(Runnable task) {
        return executorService.submit(RunnableWrapper.of(task));
    }

    @Override
    public <T> ListenableFuture<T> submit(Runnable task, T result) {
        return executorService.submit(RunnableWrapper.of(task), result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService.invokeAll(tasks.stream().map(CallableWrapper::of).collect(Collectors.toList()));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return executorService.invokeAll(tasks.stream().map(CallableWrapper::of).collect(Collectors.toList()),
                timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executorService.invokeAny(tasks.stream().map(CallableWrapper::of).collect(Collectors.toList()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.invokeAny(tasks.stream().map(CallableWrapper::of).collect(Collectors.toList()),
                timeout, unit);
    }

    @Override
    public void execute(Runnable runnable) {
        executorService.execute(RunnableWrapper.of(runnable));
    }
}
