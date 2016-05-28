# See the VCL chapters in the Users Guide at https://www.varnish-cache.org/docs/
# and http://varnish-cache.org/trac/wiki/VCLExamples for more examples.

# Marker to tell the VCL compiler that this VCL has been adapted to the
# new 4.0 format.
vcl 4.0;

# Default backend definition.
backend default {
    .host = "order";
    .port = "8080";
}

backend catalog {
        .host = "catalog";
        .port = "8080";
}

sub vcl_recv {
    # Happens before we check if we have this in cache already.
    /* Rewrite all requests to /catalog to go to the catalog SCS */
    if (req.url ~ "^/catalog") {
       set req.url = regsub(req.url, "^/catalog", "/");
       set req.backend_hint = catalog;
    }
}

sub vcl_backend_response {
    # Happens after we have read the response headers from the backend.
    /* Enable ESI */
    set beresp.do_esi = true;
    set beresp.ttl = 30s;
    set beresp.grace = 15m;
}

sub vcl_deliver {
    # Happens when we have all the pieces we need, and are about to send the
    # response to the client.
    #
    # You can do accounting or modifying the final object here.
}