package NetWork.TaskManager;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class TaskWrapper<V> implements RunnableFuture<V>,Comparable<TaskWrapper<?>> {
    private final Runnable RunnableWorkThread;
    private final FutureTask<V> CallableWorkThread;
    private Thread workThread;
    private TaskType targetType;

    public TaskWrapper(Runnable target,TaskType targetType) {
        this.RunnableWorkThread = target;
        this.workThread=new Thread(this.RunnableWorkThread);
        this.CallableWorkThread =null;
        this.targetType = targetType;
    }

    public TaskWrapper(Callable<V> target, TaskType targetType) {
        this.RunnableWorkThread = null;
        this.CallableWorkThread = new FutureTask<V>(target);
        this.workThread = new Thread(this.CallableWorkThread);
        this.targetType = targetType;
    }

    @Override
    public int compareTo(@NotNull TaskWrapper<?> o) {
        return Integer.compare(this.targetType.getPriority(),o.targetType.getPriority());
    }
    @Override
    public void run() {
        workThread.start();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(!mayInterruptIfRunning){
            waitUntilDone();
            return false;
        }
        workThread.interrupt();
        return true;
    }

    public void waitUntilDone() {
        if(this.workThread.isAlive()){
            try {
                this.workThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return workThread.isInterrupted();
    }

    @Override
    public boolean isDone() {
        return !workThread.isInterrupted() && !workThread.isAlive();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (this.CallableWorkThread != null) {
            return this.CallableWorkThread.get();
        }
        return null;
    }

    @Override
    public V get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.CallableWorkThread != null) {
            return this.CallableWorkThread.get(timeout,unit);
        }
        return null;
    }

}
