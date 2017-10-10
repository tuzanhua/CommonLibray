package com.kekejl.lib_util;

/**
 * add by sxming
 *
 * @author Administrator
 * 线程池的管理类
 */

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocalThreadPool {
    private static LocalThreadPool threadPool;
    //    private static final int count = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private ThreadPoolExecutor executor = null;
    //线程池的基础参数 实际使用可写入到配置文件中
    private static int corePoolSize = 1;            //核心池的大小 运行线程的最大值 当线程池中的线程数目达到corePoolSize后，就会把多余的任务放到缓存队列当中；
    private static int maximumPoolSize = Integer.MAX_VALUE;        //创建线程最大值
    private long keepAliveTime = 200;     //线程没有执行任务时 被保留的最长时间 超过这个时间就会被销毁 直到线程数等于 corePoolSize
    /**
     * 参数keepAliveTime的时间单位，有7种取值，在TimeUnit类中有7种静态属性：
     * TimeUnit.DAYS;               天
     * TimeUnit.HOURS;             小时
     * TimeUnit.MINUTES;           分钟
     * TimeUnit.SECONDS;           秒
     * TimeUnit.MILLISECONDS;      毫秒
     * TimeUnit.MICROSECONDS;      微妙
     * TimeUnit.NANOSECONDS;       纳秒
     ***/
    private TimeUnit unit = TimeUnit.MILLISECONDS;
    /**
     * 用来储存等待中的任务的容器
     * <p>
     * 几种选择：
     * ArrayBlockingQueue;
     * LinkedBlockingQueue;
     * SynchronousQueue;
     * http://blog.csdn.net/mn11201117/article/details/8671497
     */
    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
//    private SynchronousQueue<Runnable> workQueue = new SynchronousQueue<Runnable>();

    /**
     * 单例
     *
     * @return
     */
    public static LocalThreadPool init() {
        if (threadPool == null) {
            synchronized (LocalThreadPool.class) {
                if (threadPool == null) {
                    threadPool = new LocalThreadPool();
                }
            }
        }
        return threadPool;
    }


    /**
     * 私有构造方法
     */
    private LocalThreadPool() {
        //实现线程池
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, Executors.defaultThreadFactory(), //线程工厂
                new ThreadPoolExecutor.AbortPolicy());
        System.out.println("线程池初始化成功");
    }

    /**
     * 线程池获取方法
     *
     * @return
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    /**
     * 准备执行 抛入线程池
     *
     * @param t
     */
    public void execute(Thread t) {
        executor.execute(t);
    }

    public void execute(Runnable t) {
        executor.execute(t);
    }

    /**
     * 返回 Future
     * Future.get()可获得返回结果
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Future<?> submit(Callable t) {
        return getExecutor().submit(t);
    }

    /**
     * 销毁线程池
     */
    public void shutdown() {
        getExecutor().shutdown();
        if (threadPool != null) {
            threadPool = null;
        }
    }
}
