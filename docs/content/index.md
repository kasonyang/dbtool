title=DBTool
date=2017-10-11
type=post
tags=
status=published
~~~~~~

# Overview

DBTool is a table comparing tool.

# Installation

* Download the latest [release](https://github.com/kasonyang/dbtool/releases) and unpack.
* Add the absolute path of `bin` to your environment variable `PATH`.


# Usage

    > dbtool compare db1 db2 -s1 localhost -s2 localhost -u1 USER1 -p1 PASSOWRD1 -u2 USER2 -p2 PASSWORD2

output example:

    db1.user            db2.user            PASS                   
    db1.article         db2.article         PASS     
    ...
    Compare PASSED (total tables:10 , total fields:83).





