#!/bin/sh
docker build --tag=scs-demo-esi-common ../scs-demo-esi-common/
docker build --tag=scs-demo-esi-order ../scs-demo-esi-order/
docker build --tag=scs-demo-esi-varnish varnish