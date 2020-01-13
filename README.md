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

# API Rate Limiter

This project aims to demonstrate how to implement an HTTP rate limiter in Scala.
It uses http4s for the web server, Circe for JSON handling, and is written in Scala 2.12.
This project is built using SBT. As such, it can be run by using `sbt run`, and the specs can be
run using `sbt test`.

## API

This web serever binds itself to port 8080, and exposes two endpoints:

- `/city/:city-name`: this one will return a list of hotels of a given city ;
- `/room/:room-name`: this one will return a list of hotels according to a room criteria.

Both endpoints allow the user to add an optional `price-sorting` parameter that takes either ASC or
DESC and will sort the output hotels according to their prices and the order required.

Note: all the parameters of both endpoints are case-insensitive.
