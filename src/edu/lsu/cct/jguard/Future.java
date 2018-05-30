/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

/**
 *
 * @author sbrandt
 */
public class Future {
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
}
