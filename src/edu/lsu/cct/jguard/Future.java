/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sbrandt
 */
public class Future<T> {
    private volatile T data;
    private volatile Exception ex;// = new Exception("Not set");
    
    void set(T data) {
        ex = null;
        this.data = data;
    }
    void setEx(Exception ex) {
        this.ex = ex;
        this.data = null;
    }
    GuardWatcher watcher = new GuardWatcher();
    public T get() { 
        if(ex != null)
            throw new RuntimeException(ex);
        return data;
    }
    
    public void then(Runnable1<T> r) {
        watcher.await(()->{
            if(ex == null)
                r.run(data);
            else
                throw new RuntimeException(ex);
        });
    }
    
    public <R> Future<R> then(Callable1<T,R> c) {
        Future<R> f = new Future<>();
        f.watcher.incr();
        watcher.await(()->{
            try {
                f.set(c.call(data));
            } catch (Exception ex) {
                f.setEx(ex);
            } finally {
                f.watcher.decr();
            }
        });
        return f;
    }
    
    public Runnable run(Callable<T> c) {
        watcher.incr();
        return () -> {
            try {
                set(c.call());
            } catch (Exception ex) {
                setEx(ex);
            } finally {
                watcher.decr();
            }
        };
    }
    
    public Runnable runFut(Callable<Future<T>> c) {
        watcher.incr();
        return ()->{
            try {
                Future<T> f = c.call();
                f.watcher.await(()->{
                    this.data = f.data;
                    this.ex = f.ex;
                    watcher.decr();
                });
            } catch (Exception ex) {
                this.ex = ex;
                watcher.decr();
            }
        };
    }
    
    public static void main(String[] args) {
        Guard g = new Guard();
        Future<Integer> f1 = new Future<>();
        Runnable r = f1.run(()->{
            return 3;
        });
        Guard.runGuarded(g, r);
        f1.then((Integer v)->{
            System.out.println("v="+v);
        });
        Guard.POOL.awaitQuiescence(1, TimeUnit.DAYS);
    }
}
