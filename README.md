# API Rate Limiter

```
MMMMMMMMMMMMMMMMMMMMMMMMWXOxoc;'..            ..';coxOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMN0dc,.                            .,cd0NMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWXkc'.         ..',;:cccccc:;,'..         .'ckXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMNkc.       .':lxOKXNWMMMMMMMMMMWNXKOxl:'.       .ckNMMMMMMMMMMMMMM
MMMMMMMMMMMWKo'      .'cx0NWMMMMMMMNkllookNMMMMMMMMMWN0xc'.      'oKWMMMMMMMMMMM
MMMMMMMMMWKl.     .,o0NWWXNMMMMMMMM0,    ;KMMMMMMMMNXWMMMN0o,.     .l0WMMMMMMMMM
MMMMMMMMXo.     .cONMW0o;.cKMMMMMMMK,    ;KMMMMMMMXl.,o0WMMMNOc.     .oXMMMMMMMM
MMMMMMNk'     .oKWMMMNo.   ;0MMMMMM0,    ;KMMMMMMK:    lNMMMMMWKo.     'kNMMMMMM
MMMMMXl.    .lKWMMMMMMNo.   ,OWMMMMNxllllkNMMMMM0,   .lXMMMMMMMMWKl.    .lXMMMMM
MMMM0;     ,OWMMMMMMMMMNd. .;OWMMMMMMMMMMMMMMMMWO:. .oNMWXKNMMMMMMWO;     ;0MMMM
MMMO,    .lXMMWXXWMMMMMMW0kKWMMMMMMMMMMMMMMMMMMMMWKkOWNOdkXWMMNKNMMMXl.    ,0MMM
MM0,    .dNMMNo..cxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKd:cOWWXkl,.:KMMMNd.    ,0MM
MK;    .dNMMWd.    .;xNMMMMMMMMMMMMMMMMMMMMMMMMMMNOl''dXWOc.     :XMMMWd.    ;KM
Nl     lNMMMWXkc'.  .oNMMMMMMMMMMMMMMMMMMMMMMMWXx;..c0WMWO'   .:d0WMMMMNl     lN
O.    ;XMMMMMMMMN0ocxNMMMMMMMMMMMMMMMMMMMMMMW0l.  ,kNMMMMW0llkXWMMMMMMMMX;    .O
c    .kWMMMMMMMMMMMMMMMMMMMMMMMMMMWKkdd0XNXx;.  .oXMMMMMMMMMMMMMMMMMMMMMMk.    c
.    :XMMMMMMMMMMMMMMMMMMMMMMMMMNx;..,oxdl'   .:0WMMMMMMMMMMMMMMMMMMMMMMMX:    .
     oWMMWWWWWWWWMMMMMMMMMMMMMMXc..cdd:.     ,kNMMMMMMMMMMMMMMWWWWWWWWWMMWo
    .xMMXl,,,,,,cKMMMMMMMMMMMMWo.;0WO,     .o00XMMMMMMMMMMMMMMO:,,,,,,dNMMk.
    .kMM0'      .OMMMMMMMMMMMMNc .:kNKl. .:OO;,OMMMMMMMMMMMMMMx.      :NMMk.
    .kMMXxcccccldXMMMMMMMMMMMMWk.   ,dKKO00o. cXMMMMMMMMMMMMMM0occccclkWMMk.
    .xMMMMMMMMMMMMMMMMMMMMMMMMMWk'    'oxd, .lKMMMMMMMMMMMMMMMMMMMMMMMMMMMd.
.    lNMMMMMMMMMMMMMMMMMMMMMMMMMMXxc,...'':o0WMMMMMMMMMMMMMMMMMMMMMMMMMMMNc    .
,    '0MMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXXXXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM0'    ,
d.    ,llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll,    .d
K;                                                                            ;K
Wk.                                                                          .kW
MNc                                                                          cNM
```

- [Statement of purpose](#statement-of-purpose)
- [API](#api)
- [Rate limiting](#rate-limiting)
- [Demonstration](#demonstration)
- [Installation](#installation)
  - [Scala and SBT](#scala-and-sbt)
  - [Packaging for production](#packaging-for-production)

## Statement of Purpose

This project aims to demonstrate how to implement an HTTP rate limiter in Scala.
It uses http4s for the web server, Circe for JSON handling, and is written in Scala 2.12.

## API

This web serever binds itself to port 8080, and exposes two endpoints:

- `/city/:city-name`: this one will return a list of the hotels of a given city ;
- `/room/:room-name`: this one will return a list of hotels fulfilling to a room criteria.

Both endpoints allow the user to add an optional `price-sorting` parameter that takes either `ASC`
or `DESC` and will sort the output hotels according to their prices and the order required.

Both endpoints are throttled. The first one will allow a maximum of 10 calls per 5 seconds, while
the second one allow 100 calls per 10 seconds.

Note: all the parameters of both endpoints are case-insensitive.

## Rate limiting

The main goal of this project is to implement and showcase a rate limiter in Scala.
The code for the rate limiter itself can be found here: [RateLimiter.scala](src/main/scala/ratelimiting/RateLimiter.scala).

It works as follows: first, we wrap a function with `wrapUnary`. If that function's type was
`A => B`, the wrapped function has a type of `A => Option[B]`. This is because that wrapped function
will return a `None` if the rate limit has been reached and we're currently in the cooldown period.
Otherwise, it will return a `Some` containing the output of the function.
Thus, the wrapped function is only called if the rate limit hasn't been reached, preventing any
DOS attack.

`wrapUnary` takes three arguments: the first is the unary function we want to throttle, the second
is the rate limiting time window (we'll refer to it as `k`), and the third is the number of calls
not to exceed during the time window `k` (we will refer to that number as `n`).

If not value is provided for `k`, it will fall back to *10 seconds*. If not value is provided for `n`, it will fall back to *100*.

Internally, the `wrapUnary` method maintains a mutable `Queue`.
At any given time, said queue will contain the timestamps corresponding to all the successful calls
to the wrapped function within the last `k` seconds.

Every time the wrapped function is called, we first dequeue all timestemps older than `k` seconds.
Then, we compare the length of the queue with `n`.
If the length of the queue is superior or equal to `n`, the rate limit has been reached within the
last `k` seconds ; `None` is returned.

Otherwise, the current timestamp is added to said queue, the underlying function actually gets
executed, and a `Some` is returned.

## Demonstration

This project comes with a simple JS script to demonstrate the rate limiting of the city endpoint.
To run it, make sure you have NodeJS installed and run the following:
```
$> node ./demonstrationScript.js
```
This script was written in JavaScript and with no external dependencies so that it could easily be
run and modified without any other setup than a NodeJS install.

## Installation

This project is built using SBT. As such, it can be run by using `sbt run`, and the specs can be
run using `sbt test`.

### Scala and SBT

Please refer to the [SBT docs](https://www.scala-sbt.org/1.0/docs/Setup.html) for directions on
installation.

### Packaging for production

First, clone the Harvester repo to a directory of your choice and `cd` into it.
Then, in order to generate a binary, run the following:
```
sbt universal:packageBin
```
It will generate a zip here:
```
./target/universal/rate-limiting-%VERSION_NUMBER%.zip
```
This zip can now be shipped on a server, or used locally.
Once it is unzipped, the rate-limiter server can be started from inside the resulting folder with:
```
./bin/rate-limiting
```
