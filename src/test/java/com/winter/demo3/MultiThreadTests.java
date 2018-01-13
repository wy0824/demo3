package com.winter.demo3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class MyThread extends Thread{
    private int tid;
    public MyThread(int tid){
        this.tid = tid;
    }
    @Override
    public void run() {
        try{
            for(int i = 0; i < 10; ++i){
                Thread.sleep(1000);
                System.out.println(String.format("%d:%d",tid,i));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
public class MultiThreadTests {
    public static void testThread(){
        for(int i = 0; i < 10 ;++i){
            new MyThread(i).start();
        }
    }
    private static Object obj = new Object();

    public static void testSynchronized(){

    }

    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal(){
        for(int i = 0; i < 10; ++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testExecutor(){
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; ++i){
                    try{
                        Thread.sleep(1000);
                        System.out.println("Executor1: "+i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; ++i){
                    try{
                        Thread.sleep(1000);
                        System.out.println("Executor2: "+i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        service.shutdown();
        while(!service.isTerminated()){
            try{
                Thread.sleep(1000);
                System.out.println("Wait for termination");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception{
                Thread.sleep(1000);
                return 1;
            }
        });
        service.shutdown();
        try{
            System.out.println(future.get());
        }catch(Exception e){
            e.printStackTrace();;
        }
    }
    public static void main(String[] args){
//        testThread();
//        testThreadLocal();
//        testExecutor();
        testFuture();
    }
}
