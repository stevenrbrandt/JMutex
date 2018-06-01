/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
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
//        int n1 = guarded a {
//            int inner = guarded b {
//                return 3;
//            };
//            await inner -> inner_
//            return 4 + inner_;
//        };

        Callable<Future<Integer>> n1c = ()->{
            Callable<Integer> c = ()->{
                return 3;
            };
            Future<Integer> inner = new Future<>();
            Guard.runGuarded(b,inner.run(c));
            
            // await inner
            return inner.then((Integer inner_)->{
                return 4 + inner_;
            });
        };
        Future<Integer> n1 = new Future<>();
        Guard.runGuarded(a,n1.runFut(n1c),n1.watcher);
        
//        Guard.runGuarded(a, c1, n1, _f_local_set);
//        
//                int n2 = guarded b {
//            int inner = guarded a {
//                return 4;
//            };
//            await inner -> inner_
//            return 3 + inner_;
//        };

        Callable<Future<Integer>> n2c = ()->{
            Callable<Integer> c = ()->{
                return 4;
            };
            Future<Integer> inner = new Future<>();
            Guard.runGuarded(a,inner.run(c));
            
            return inner.then((Integer inner_)->{
                return inner_ + 3;
            });
        };
        Future<Integer> n2 = new Future<>();
        Guard.runGuarded(b,n2.runFut(n2c),n2.watcher);
        
        // await n1 -> n1_, n2 -> n2_;
        n1.then((Integer n1_)->{
            n2.then((Integer n2_)->{
                // System.out.println(n1 + n2);
                System.out.println(n1_+n2_);
            });
        });
        Guard.POOL.awaitQuiescence(1, TimeUnit.DAYS);
    }
    
    static void delay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
    }
}
