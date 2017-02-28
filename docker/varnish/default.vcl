vcl 4.0;

backend default {
    .host = "order";
    .port = "8080";
}

backend common {
    .host = "common";
    .port = "8180";
}

sub vcl_recv {
    /* Redirect all requests to /common to go to the common */
    if (req.url ~ "^/common") {
       set req.backend_hint = common;
    }
}

sub vcl_backend_response {
    set beresp.do_esi = true;
    set beresp.ttl = 30s;
    set beresp.grace = 15m;
}

