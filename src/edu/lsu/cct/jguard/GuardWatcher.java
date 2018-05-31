/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.Set;

/**
 *
 * @author sbrandt
 */
public class GuardWatcher {
    int count;
    public synchronized void incr() {
        count++;
    }
    public synchronized void decr() {
        count--;
        if(count == 0)
            notifyAll();
    }
    private synchronized void await() {
        while(count > 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    public static void await(Set<GuardWatcher> gset, Runnable r) {
        Guard.POOL.execute(() -> {
            for (GuardWatcher gw : gset) {
                gw.await();
            }
            r.run();
        });
    }

    public void await(Runnable r) {
        Guard.POOL.execute(() -> {
            await();
            try {
                r.run();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
