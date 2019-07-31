# How to Run

This is a step-by-step guide how to run the example:

## Installation

* The example is implemented in Java. See
   https://www.java.com/en/download/help/download_options.xml . The
   examples need to be compiled so you need to install a JDK (Java
   Development Kit). A JRE (Java Runtime Environment) is not
   sufficient. After the installation you should be able to execute
   `java` and `javac` on the command line.

* The example run in Docker Containers. You need to install Docker
  Community Edition, see https://www.docker.com/community-edition/
  . You should be able to run `docker` after the installation.

* The example need a lot of RAM. You should configure Docker to use 4
  GB of RAM. Otherwise Docker containers might be killed due to lack
  of RAM. On Windows and macOS you can find the RAM setting in the
  Docker application under Preferences/ Advanced.
  
* After installing Docker you should also be able to run
  `docker-compose`. If this is not possible, you might need to install
  it separately. See https://docs.docker.com/compose/install/ .

## Build

Change to the directory `scs-demo-esi-order` and run `./mvnw clean
package` or `mvnw.cmd clean package` (Windows). This will take a
while:

```
[~/SCS-ESI/scs-demo-esi-order]./mvnw clean package
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 18.954 s
[INFO] Finished at: 2017-09-08T15:34:24+02:00
[INFO] Final Memory: 41M/480M
[INFO] ------------------------------------------------------------------------
```

If this does not work:

* Ensure that `settings.xml` in the directory `.m2` in your home
directory contains no configuration for a specific Maven repo. If in
doubt: delete the file.

* The tests use some ports on the local machine. Make sure that no
server runs in the background.

* Skip the tests: `./mvnw clean package -Dmaven.test.skip=true` or
  `mvnw.cmd clean package -Dmaven.test.skip=true` (Windows).

* In rare cases dependencies might not be downloaded correctly. In
  that case: Remove the directory `repository` in the directory `.m2`
  in your home directory. Note that this means all dependencies will
  be downloaded again.

## Run the Containers on Docker Compose

First you need to build the Docker images. Change to the directory
`docker` and run `docker-compose build`. This will download some base
images, install software into Docker images and will therefore take
its time:

```
[~/SCS-ESI/docker]docker-compose build 
...
Step 10/10 : CMD /start
 ---> Running in 516ea265f045
 ---> f7e4c455f136
Removing intermediate container 516ea265f045
Successfully built f7e4c455f136
Successfully tagged scsesi_varnish:latest
```

If the build fails, you can use  `docker-compose build --no-cache` to
build them from scratch.

Afterwards the Docker images should have been created. They have the prefix
`scsesi`:

```
[~/SCS-ESI/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED              SIZE
scsesi_varnish                                          latest              f7e4c455f136        35 seconds ago      328MB
scsesi_order                                            latest              c6111b6e47f2        39 seconds ago      205MB
scsesi_common                                           latest              476e076c6294        6 weeks ago         6.91MB
```

Now you can start the containers using `docker-compose up -d`. The
`-d` option means that the containers will be started in the
background and won't output their stdout to the command line:

```
[~/SCS-ESI/docker]docker-compose up -d
Starting scsesi_common_1 ... 
Starting scsesi_common_1
Recreating scsesi_order_1 ... 
Recreating scsesi_order_1 ... done
Recreating scsesi_varnish_1 ... 
Recreating scsesi_varnish_1 ... done
```

Check wether all containers are running:

```
[~/SCS-ESI/docker]docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
1a13f1daddcf        scsesi_varnish      "/start"                 19 seconds ago      Up 18 seconds       0.0.0.0:8080->8080/tcp   scsesi_varnish_1
c6eb5d68b795        scsesi_order        "/bin/sh -c '/usr/..."   21 seconds ago      Up 19 seconds       0.0.0.0:8090->8080/tcp0.0.0.0:8090->8080/tcp                 scsesi_order_1
1fb2aecbe10e        scsesi_common       "/common"                6 weeks ago         Up 20 seconds       8180/tcp                 scsesi_common_1
```

`docker ps -a`  also shows the terminated Docker containers. That is
useful to see Docker containers that crashed rigth after they started.

If one of the containers is not running, you can look at its logs using
e.g.  `docker logs scsesi_order_1`. The name of the container is
given in the last column of the output of `docker ps`. Looking at the
logs even works after the container has been
terminated. If the log says that the container has been `killed`, you
need to increase the RAM assigned to Docker to e.g. 4GB. On Windows
and macOS you can find the RAM setting in the Docker application under
Preferences/ Advanced.
  
If you need to do more trouble shooting open a shell in the container
using e.g. `docker exec -it scsesi_order_1 /bin/sh` or execute
command using `docker exec scsesi_order_1 /bin/ls`. This only works in
the order and varnish containers. The common container contains just a
Go binary and no other tools.

The example can be accessed at http://localhost:8080/ .
Vanish has processed the ESI includes in that version. If you use http://localhost:8090/ instead, you will see the raw HTML without the expanded ESI includes as it is provided by the order SCS.

You can terminate all containers using `docker-compose down`.

## Run the Containers on Minikube

* Install
[minikube](https://github.com/kubernetes/minikube/releases). Minikube
is a Kubernetes environment in a virtual machine that is easy to use
and install. It is not meant for production but to test Kubernetes or
for developer environments.

* Create a Minikube instance with `minikube start --cpus=2 --memory=5000`. This
  will set the memory of the Kubernetes VM to 6.000 MB - which should
  be enough for most experiments. You might want to adjust the number of CPUs
  depending on your local machine. Please make sure that you deleted any pre-existing 
  minikube instance using `minikube delete` as the memory and cpu values would otherwise have no effect. 

```
[~/microservice-istio]minikube start --cpus=2 --memory=5000
Starting local Kubernetes v1.12.4 cluster...
Starting VM...
Getting VM IP address...
Moving files into cluster...
Setting up certs...
Connecting to cluster...
Setting up kubeconfig...
Starting cluster components...
Kubectl is now configured to use the cluster.
```

* Install [kubectl](https://kubernetes.io/docs/tasks/kubectl/install/). This
  is the command line interface for Kubernetes.

* Make sure you compiled the Java code as explained above.

* Install Ingress on minikube: `minikube addons enable ingress`

* Configure Docker so that it uses the Kubernetes cluster. This is
required to install the Docker images: `minikube docker-env`(macOS / Linux) or `minikube.exe docker-env`(Windows) tells you how to do that. 

* Minikube only: Afterwards you should see the Docker images of Kubernetes if you do `docker images`:

```
[SCS-ESI/docker]docker images
REPOSITORY                                TAG                 IMAGE ID            CREATED             SIZE
k8s.gcr.io/kubernetes-dashboard-amd64     v1.10.1             f9aed6605b81        3 weeks ago         122MB
k8s.gcr.io/kube-proxy                     v1.12.4             6d393e89739f        3 weeks ago         96.5MB
k8s.gcr.io/kube-apiserver                 v1.12.4             c04b373449d3        3 weeks ago         194MB
k8s.gcr.io/kube-controller-manager        v1.12.4             51b2a8e5ff78        3 weeks ago         164MB
k8s.gcr.io/kube-scheduler                 v1.12.4             c1b5e63c0b56        3 weeks ago         58.4MB
istio/sidecar_injector                    1.0.5               091fd902183a        4 weeks ago         52.9MB
istio/servicegraph                        1.0.5               cef5bb589599        4 weeks ago         16.5MB
istio/proxyv2                             1.0.5               e393f805ceac        4 weeks ago         380MB
istio/pilot                               1.0.5               68f5cc3a87ff        4 weeks ago         313MB
istio/mixer                               1.0.5               582d5c76010e        4 weeks ago         70MB
istio/galley                              1.0.5               e35efbcb45ed        4 weeks ago         73.1MB
istio/citadel                             1.0.5               3e6285f52cd0        4 weeks ago         56.1MB
k8s.gcr.io/etcd                           3.2.24              3cab8e1b9802        3 months ago        220MB
k8s.gcr.io/coredns                        1.2.2               367cdc8433a4        4 months ago        39.2MB
grafana/grafana                           5.2.3               17a5ba3b1216        4 months ago        245MB
prom/prometheus                           v2.3.1              b82ef1f3aa07        6 months ago        119MB
jaegertracing/all-in-one                  1.5                 93f16463fee4        7 months ago        48.4MB
k8s.gcr.io/kube-addon-manager             v8.6                9c16409588eb        10 months ago       78.4MB
k8s.gcr.io/pause                          3.1                 da86e6ba6ca1        12 months ago       742kB
gcr.io/k8s-minikube/storage-provisioner   v1.8.1              4689081edb10        14 months ago       80.8MB
quay.io/coreos/hyperkube                  v1.7.6_coreos.0     2faf6f7a322f        15 months ago       699MB
```
* Run `docker-build.sh` in the directory `docker`. It builds the Docker images.

```
[~/SCS-ESI/docker]./docker-build.sh 
...
Step 5/10 : RUN apt-get update
---> 58b78486b7d0
Step 6/10 : RUN apt-get install -y -qq varnish                                                             ---> 8b55d00245f3                                                                                         Step 7/10 : COPY default.vcl /etc/varnish/default.vcl                                                     ---> d7670ed17c62                                                                                         Step 8/10 : COPY start /start                                                                             ---> 9047ecb1d5e3                                                                                         Step 9/10 : RUN chmod 0755 /start                                                                         ---> f3cc3ceed8c4                                                                                         Step 10/10 : CMD ["/start"]                                                                               ---> 429b0893f00a                                                                                         Successfully built 429b0893f00a                                                                           Successfully tagged scs-demo-esi-varnish:latest  
```

* Sometime pulling the images does not work. Try `docker logout` then
  and rerun the script.

* The images should now be available:

```
[~/SCS-ESI/docker]docker images
REPOSITORY                                TAG                 IMAGE ID            CREATED              SIZE
...
scs-demo-esi-order                        latest              18da0f33925f        2 hours ago         318MB                                                                                                     scs-demo-esi-varnish                      latest              429b0893f00a        2 hours ago         310MB
scs-demo-esi-common                       latest              ba1a226c150c        2 hours ago         8.41MB
...
```

* Deploy the microservices using `kubectl` in the directory `docker` :

```
[~/SCS-ESI/docker]kubectl apply -f scs-esi.yaml
deployment.apps/common configured
deployment.apps/order configured
deployment.apps/varnish configured
service/common configured
service/order configured
service/varnish configured
ingress.networking.k8s.io/scs-esi-ingress created
```

* Run `kubectl get services` to see all services:

```
[~/SCS-ESI/docker]kubectl get services
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
common       NodePort    10.98.59.22      <none>        8180:30227/TCP   30m
kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP          37d
order        NodePort    10.102.235.215   <none>        8080:30454/TCP   30m
varnish      NodePort    10.100.151.221   <none>        8080:31833/TCP   30m
```

* Figure out the IP adress of the minikube instance:

```
[~/SCS-ESI/docker]minikube ip
192.168.99.143
```

The example can be accessed at that IP adress e.g. http://192.168.99.143/ .
Vanish has processed the ESI includes in that version. If you use http://192.168.99.143/order-raw/ instead, you will see the raw HTML without the expanded ESI includes as it is provided by the order SCS.
