/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sbrandt
 */
public class Two {

    static Guard a = new Guard();
    static Guard b = new Guard();

    public static void main(String[] args) {
        Set<GuardWatcher> _f_local_set = new HashSet<>();
        GuardWatcher _f_local = new GuardWatcher(); // generated
        _f_local_set.add(_f_local); // generated
//        int n1 = guarded a {
//            int inner = guarded b {
//                return 3;
//            };
//            await inner
//            return 4 + inner;
//        };
        Future<Integer> n1 = new Future<>();
        Callable<Integer> c1 = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Set<GuardWatcher> _f_local_set1 = new HashSet<>();
                GuardWatcher _f_local1 = new GuardWatcher(); // generated
                _f_local_set1.add(_f_local1); // generated
                
                Future<Integer> inner = new Future<>();
                Callable<Integer> cinner = new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return 3;
                    }
                };
                _f_local_set1.add(inner.watcher);
                delay();
                Guard.runGuarded(b, cinner, inner, _f_local_set1);
                inner.watcher.await();
                return 4 + inner.get();
            }
        };
        _f_local_set.add(n1.watcher);
        Guard.runGuarded(a,c1,n1,_f_local_set);
//        Guard.runGuarded(a, c1, n1, _f_local_set);
//        
//                int n2 = guarded b {
//            int inner = guarded a {
//                return 4;
//            };
//            await inner
//            return 3 + inner;
//        };
        Future<Integer> n2 = new Future<>();
        Callable<Integer> c2 = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Set<GuardWatcher> _f_local_set1 = new HashSet<>();
                GuardWatcher _f_local1 = new GuardWatcher(); // generated
                _f_local_set1.add(_f_local1); // generated
                
                Future<Integer> inner = new Future<>();
                Callable<Integer> cinner = new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return 3;
                    }
                };
                _f_local_set1.add(inner.watcher);
                delay();
                Guard.runGuarded(a, cinner, inner, _f_local_set1);
                inner.watcher.await();
                return 4 + inner.get();
            }
        };
        _f_local_set.add(n2.watcher);
        Guard.runGuarded(b,c2,n2,_f_local_set);
        // await n1, n2
        n1.watcher.await();
        n2.watcher.await();
        //System.out.println(n1+n2);
        System.out.println(n1.get()+n2.get());
    }
    
    static void delay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
    }
}
