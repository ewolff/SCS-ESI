vcl 4.0;

backend default {
    .host = "order";
    .port = "8080";
}

backend common {
    .host = "common";
    .port = "8180";
}

probe default {
    .url = "/";
    .timeout = 1s;
    .interval = 2s;
    .window = 3;
    .threshold = 2;
}

sub vcl_recv {
    if (req.url ~ "^/common") {
       set req.backend_hint = common;
       set req.ttl = 30s;
    } 
}

sub vcl_backend_response {
    set beresp.do_esi = true;
    set beresp.ttl = 2s;
    set beresp.grace = 15m;   
}