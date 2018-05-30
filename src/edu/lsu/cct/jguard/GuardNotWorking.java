/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sbrandt
 */
public class GuardNotWorking implements Comparable<GuardNotWorking> {
    final static AtomicInteger seq = new AtomicInteger();
    final int id = seq.getAndIncrement();

    public final static ForkJoinPool POOL = new ForkJoinPool(3);

    Lock lock = new ReentrantLock();
    boolean is_running;
    LinkedList<Runnable> waiting = new LinkedList<>();

    public static void runGuarded(GuardNotWorking g, Runnable r) {
        runGuarded(g, r, true);
    }

    private boolean lock(Runnable r) {
        try {
            lock.lock();
            if (is_running) {
                waiting.addLast(r);
                return false;
            }
            assert waiting.size()==0;
            is_running = true;
        } finally {
            lock.unlock();
        }
        return true;
    }

    private void unlock() {
        assert is_running;
        Runnable r = null;
        while (true) {
            try {
                lock.lock();
                if (waiting.size() == 0) {
                    is_running = false;
                    return;
                } else {
                    r = waiting.removeFirst();
                }
            } finally {
                lock.unlock();
            }
            r.run();
        }
    }

    static void runGuarded(GuardNotWorking g, Runnable r, boolean unlock) {
        if (g.lock(r)) {
            r.run();
            if (unlock) {
                g.unlock();
            }
        }
    }

    public static void runGuarded(TreeSet<GuardNotWorking> gs, Runnable r) {
        List<GuardNotWorking> guards = new ArrayList<>();
        guards.addAll(gs);
        runGuarded(0, guards, r);
    }

    static void runGuarded(int index, List<GuardNotWorking> guards, Runnable r) {
        if (index + 1 == guards.size()) {
            runGuarded(guards.get(index), () -> {
                r.run();
                for (int i = 0; i < guards.size(); i++) {
                    guards.get(i).unlock();
                }
            }, false);
        } else {
            assert guards.get(index).id < guards.get(index+1).id;
            runGuarded(guards.get(index), () -> {
                runGuarded(index + 1, guards, r);
            }, false);
        }
    }

    @Override
    public int compareTo(GuardNotWorking that) {
        return this.id - that.id;
    }

    static int counter = 0, counter1 = 0, counter2 = 0;

    public static void main(String[] args) throws InterruptedException {
        try {
            assert false;
            throw new Error("Enable assertions");
        } catch (AssertionError ae) {
        }
        GuardNotWorking g = new GuardNotWorking();
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

        GuardNotWorking g1 = new GuardNotWorking();
        GuardNotWorking g2 = new GuardNotWorking();
        counter1 = counter2 = 0;
        TreeSet<GuardNotWorking> gs = new TreeSet<>();
        gs.add(g1);
        gs.add(g2);
        for (int i = 0; i < N; i++) {
//            POOL.submit(()->{
//                runGuarded(g1,()->{
//                    counter1++;
//                });
//            });
//            POOL.submit(()->{
//                runGuarded(g2,()->{
//                    counter2++;
//                });
//            });
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

        System.out.println("done");
    }
}
