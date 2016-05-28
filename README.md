SCS ESI Sample
==============

[German / Deutsch](LIESMICH.md)

For [Self-contained Systems](http://scs-architecture.org) multiple
frontends need to be integrated. This sample shows how to
this. Varnish serves as a cache and also ESI (Edge Side Includes) are
used to integrate multiple backends into one HTTP site.

This project creates a Docker setup with the complete systems based on
Docker Compose and Docker Machine. The services are implemented in
Java using Spring and Spring Cloud.

It uses two very simple SCS:
- Order to enter orders.
- Catalog to handle the items in the catalog.

How To Run
----------

The demo can be run with
[Docker Machine and Docker Compose](docker/README.md).

Remarks on the Code
-------------------

The microservices are: 
- microservice-demo-catalog is the application to take care of
  items. You can access it at `/customer`. Also it is used by order to
  display the choice of items and the text for each item in an order.
- microservice-demo-order does order processing. It outputs `esi:include`
- Varnish interprets the ESIs. The `default.vcl` in the directory
  `docker/varnish` includes the configuration to enable this. It
  defines two backends - one for each SCS. HTTP requests to `/catalog`
  are mapped to the catalog SCS. The time to live for each item in the
  cache is 30s i.e. after 30 seconds an entry in the cache in
  invalidated and the call goes to the backend. The grace is 15m: Even
  if the entry in the cache is invalid it will be served for 15 more
  minutes of the backend is not accessible. So if the backend crashes
  the cache can still provide some resilience.


The microservices have an Java main application in src/test/java to
run them stand alone. microservice-demo-order uses a stub for the
other services then. Also there are tests that use customer driven
contracts. That is why it is ensured that the services provide the
correct interface. These CDC tests are used in microservice-demo-order
to verify the stubs. In microserivce-demo-customer and
microserivce-demo-catalog they are used to verify the implemented REST
services.
