/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sbrandt
 */
public class Pseudo {
    volatile static int counter;
    
    static void incr() {
        counter++;
    }
    
    static Guard g = new Guard();
    static GuardWatcher f = new GuardWatcher();

    public static void main(String[] args) {
        GuardWatcher f = new GuardWatcher();
        
        final int N = 10000;

        for(int i=0;i<N;i++) {
            // guarded g { incr(); } -> f;
            Guard.runGuarded(g, ()->{ incr(); },f);
        }

        // await f
        f.await(()->{
            System.out.println("counter="+counter);
        });
        
        Guard.POOL.awaitQuiescence(1, TimeUnit.DAYS);
    }
}
