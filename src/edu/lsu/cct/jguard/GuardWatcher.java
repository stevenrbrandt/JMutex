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
    public synchronized void await() {
        while(count > 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
    }
    public static void await(Set<GuardWatcher> gset) {
        for(GuardWatcher gw : gset) {
            gw.await();
        }
    }
}
