# JMutex

In the future, this will be a revolutionary programming idea. Right now, it barely works and isn't documented. Please come back later.

For GuardWatchers: GuardWatchers are used when you want to keep track of when one or more blocks of guarded code have finished.

```
Guard a,b;
GuardWatcher gw;

guarded a {
    // do stuff
} -> gw;

guarded b {
    // do more stuff
} -> gw;

await gw;

System.out.println("All done!");
```

When values are returned from guarded regions, they come back as futures. To get
at those values, an await is needed.
```
Future<int> f = guarded a {
    return 3;
};

await f -> f_; // the type of f_ is "int"

System.out.println("f="+f_);

Future<int> add = guarded a {
    Future<int> v1 = guarded b {
        return 2;
    };
    Future<int> v2 = guarded c {
        return 3;
    }
    await v1 -> v1_, v2 -> v2_;
    return v1_ + v2_;
}

await add -> add_;

System.out.println("add = "+add_);
```
