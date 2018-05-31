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
public interface Callable1<A, R> {
    R call(A arg) throws Exception;
}
