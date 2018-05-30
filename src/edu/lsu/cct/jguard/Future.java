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
public class Future<T> {
    private volatile T data;
    private volatile Exception ex = new Exception("Not set");
    
    void set(T data) {
        ex = null;
        this.data = data;
    }
    void setEx(Exception ex) {
        this.ex = ex;
        this.data = null;
    }
    GuardWatcher watcher = new GuardWatcher();
    public T get() { 
        if(ex != null)
            throw new RuntimeException(ex);
        return data;
    }
}
