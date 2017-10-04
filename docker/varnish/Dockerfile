FROM ubuntu:14.04
RUN apt-get update && apt-get dist-upgrade -y -qq
RUN apt-get install -y -qq apt-transport-https
RUN apt-get install -y -qq curl && \
curl https://repo.varnish-cache.org/GPG-key.txt | apt-key add - && \
echo "deb https://repo.varnish-cache.org/ubuntu/ trusty varnish-4.1" \
     >> /etc/apt/sources.list.d/varnish-cache.list
RUN apt-get update
RUN apt-get install -y -qq varnish
COPY default.vcl /etc/varnish/default.vcl
COPY start /start
RUN chmod 0755 /start
CMD ["/start"]
