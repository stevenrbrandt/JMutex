/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lsu.cct.jguard;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author sbrandt
 */
public class Pass {

    static Guard a = new Guard();
    static Guard b = new Guard();

    public static Future<Integer> add() {
        // int f1 = guarded a { return 3; }
        Future<Integer> f1 = new Future<>();
        Guard.runGuarded(a, f1.run(() -> {
            return 3;
        }));
        // int f2 = guarded b { return 4; }
        Future<Integer> f2 = new Future<>();
        Guard.runGuarded(b, f2.run(() -> {
            return 4;
        }));
        // await f1, f2;
        return f1.thenFut((Integer f1_)->{
            return f2.then((Integer f2_)->{
                // return f1 + f2;
                return f1_+f2_;
            });
        });
    }

    public static void main(String[] args) {
        // int r = add();
        Future<Integer> r = add();
        // await r
        r.then((Integer r_)->{
            // System.out.println("r="+r);
            System.out.println("r="+r_);
        });
        Guard.POOL.awaitQuiescence(1, TimeUnit.DAYS);
    }
}
