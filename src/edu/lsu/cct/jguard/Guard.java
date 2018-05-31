/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sbrandt
 */
public class Guard implements Comparable<Guard> {

    final static AtomicInteger seq = new AtomicInteger();
    final int id = seq.getAndIncrement();
    public final static ForkJoinPool POOL = new ForkJoinPool(3);

    Lock lock = new ReentrantLock();

    public static void runGuarded(Guard g, Runnable r) {
        GuardWatcher gw = null;
        runGuarded(g, r, gw);
    }

    public static void runGuarded(Guard g, Runnable r, GuardWatcher gw) {
        if(gw != null) {
            gw.incr();
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    g.lock.lock();
                    r.run();
                } finally {
                    g.lock.unlock();
                }
                if (gw != null) {
                    gw.decr();
                }
            }
        };
        POOL.execute(task);
    }
    public static void runGuarded(Guard g, Runnable r, Set<GuardWatcher> fs) {
        if (fs != null) {
            for (GuardWatcher f : fs) {
                f.incr();
            }
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    g.lock.lock();
                    r.run();
                } finally {
                    g.lock.unlock();
                }
                if (fs != null) {
                    for (GuardWatcher f : fs) {
                        f.decr();
                    }
                }
            }
        };
        POOL.execute(task);
    }

    public static void runGuarded(TreeSet<Guard> gs, Runnable r) {
        runGuarded(gs, r, null);
    }

    public static void runGuarded(TreeSet<Guard> gs, Runnable r, Set<GuardWatcher> fs) {
        if (fs != null) {
            for (GuardWatcher f : fs) {
                f.incr();
            }
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    for (Guard g : gs) {
                        g.lock.lock();
                    }
                    r.run();
                } finally {
                    for (Guard g : gs) {
                        g.lock.unlock();
                    }
                    if (fs != null) {
                        for (GuardWatcher f : fs) {
                            f.decr();
                        }
                    }
                }
            }
        };
        POOL.execute(task);
    }

    @Override
    public int compareTo(Guard that) {
        return this.id - that.id;
    }

    static int counter = 0, counter1 = 0, counter2 = 0;

    public static void main(String[] args) throws InterruptedException {
        try {
            assert false;
            throw new Error("Enable assertions");
        } catch (AssertionError ae) {
        }
        Guard g = new Guard();
        final int N = 10000;

        for (int i = 0; i < N; i++) {
            POOL.submit(() -> {
                runGuarded(g, () -> {
                    counter++;
                });
            });
        }
        POOL.awaitQuiescence(1, TimeUnit.DAYS);
        assert N == counter;

        Guard g1 = new Guard();
        Guard g2 = new Guard();
        TreeSet<Guard> gs = new TreeSet<>();
        gs.add(g1);
        gs.add(g2);
        counter1 = counter2 = 0;
        for (int i = 0; i < N; i++) {
            POOL.submit(() -> {
                runGuarded(gs, () -> {
                    counter1++;
                    counter2++;
                });
            });
        }
        POOL.awaitQuiescence(1, TimeUnit.DAYS);
        Thread.sleep(1);
        System.out.println("counter1=" + counter1 + " counter2=" + counter2);
        assert N == counter1;
        assert N == counter2;

        counter1 = counter2 = 0;
        for (int i = 0; i < N; i++) {
            POOL.submit(() -> {
                runGuarded(g1, () -> {
                    counter1++;
                });
            });
            POOL.submit(() -> {
                runGuarded(g2, () -> {
                    counter2++;
                });
            });
            POOL.submit(() -> {
                runGuarded(gs, () -> {
                    counter1++;
                    counter2++;
                });
            });
        }
        POOL.awaitQuiescence(1, TimeUnit.DAYS);
        Thread.sleep(1);
        System.out.println("counter1=" + counter1 + " counter2=" + counter2);
        assert 2 * N == counter1;
        assert 2 * N == counter2;

        System.out.println("done");
    }
}
