FROM ubuntu:22.04
RUN apt-get update && apt-get dist-upgrade -y -qq
RUN apt-get install -y -qq curl gnupg apt-transport-https
RUN curl -L https://packagecloud.io/varnishcache/varnish60lts/gpgkey | apt-key add - && \
echo "deb https://packagecloud.io/varnishcache/varnish60lts/ubuntu/ bionic main" \
>> /etc/apt/sources.list.d/varnishcache_varnish60.list && \
echo "deb-src https://packagecloud.io/varnishcache/varnish60lts/ubuntu/ bionic main" \
>> /etc/apt/sources.list.d/varnishcache_varnish60.list
RUN apt-get update
RUN apt-get install -y -qq varnish
COPY start /start
RUN chmod 0755 /start
CMD ["/start"]
COPY default.vcl /etc/varnish/default.vcl