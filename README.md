# API Rate Limiter

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

- [Statement of purpose](#statement-of-purpose)
- [API](#api)
- [Rate limiting](#rate-limiting)
- [Demonstration](#demonstration)

## Statement of Purpose

This project aims to demonstrate how to implement an HTTP rate limiter in Scala.
It uses http4s for the web server, Circe for JSON handling, and is written in Scala 2.12.
This project is built using SBT. As such, it can be run by using `sbt run`, and the specs can be
run using `sbt test`.

## API

This web serever binds itself to port 8080, and exposes two endpoints:

- `/city/:city-name`: this one will return a list of the hotels of a given city ;
- `/room/:room-name`: this one will return a list of hotels fulfilling to a room criteria.

Both endpoints allow the user to add an optional `price-sorting` parameter that takes either `ASC`
or `DESC` and will sort the output hotels according to their prices and the order required.

Note: all the parameters of both endpoints are case-insensitive.

## Rate limiting

The main goal of this project is to implement and showcase a rate limiter in Scala.
The code for the rate limiter itself can be found here: [RateLimiter.scala](src/main/scala/com/agoda/ratelimiting/RateLimiter.scala).

It works as follows: first, we wrap a function with wrapUnary. If that function's type was `A => B`,
the wrapped function has a type of `A => Option[B]`. This is because that wrapped function will
return a `None` if the rate limit has been reached and we're currently in the cooldown period.
Otherwise, it will return a `Some` containing the output of the function.
Thus, the wrapped function is only called if the rate limit hasn't been reached, preventing any
DOS attack.

Internally, the `wrapUnary` method maintains a mutable `Queue`.
With `k` being the rate limiting time window and `n` being the number of calls not to exceed
during `k` seconds:

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
$> node ./dosScript.js
```
