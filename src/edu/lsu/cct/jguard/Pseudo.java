/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.HashSet;
import java.util.Set;

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
        Set<GuardWatcher> _f_local_set = new HashSet<>(); // generated
        GuardWatcher _f_local = new GuardWatcher(); // generated
        _f_local_set.add(_f_local); // generated
        
        final int N = 10000;

        for(int i=0;i<N;i++) {
            // guarded g { incr(); } -> f;
            _f_local_set.add(f); // generated
            Guard.runGuarded(g, ()->{ incr(); },_f_local_set);
        }

        // await guards, f
        _f_local.await();
        f.await();
        
        System.out.println("counter="+counter);
    }
}
