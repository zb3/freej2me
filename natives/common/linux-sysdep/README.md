## What?
These are taken from centos 7 RPMs

## Why?
By default, your library will link against newest versions of symbols you use and will therefore likely be incompatible with older versions of GLIBC. 

Usually the solution is to build the library on an older system, but here I just manually link with older GLIBC / libstdc++.

```
gcc test.c -o test -L/library/path
```