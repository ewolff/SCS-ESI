# How to Run

This is a step-by-step guide how to run the example:

## Installation

* The example is implemented in Java. See
   https://www.java.com/en/download/help/download_options.xml . The
   examples need to be compiled so you need to install a JDK (Java
   Development Kit). A JRE (Java Runtime Environment) is not
   sufficient. After the installation you should be able to execute
   `java` and `javac` on the command line.

* Maven is needed to build the examples. See
  https://maven.apache.org/download.cgi for installation . You should be
  able to execute `mvn`on the command line after the installation.

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

Change to the directory `scs-demo-esi-order` and run `mvn clean
package`. This will take a while:

```
[~/SCS-ESI/scs-demo-esi-order]mvn clean package
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

* Skip the tests: `mvn clean package package -Dmaven.test.skip=true`.

* In rare cases dependencies might not be downloaded correctly. In
  that case: Remove the directory `repository` in the directory `.m2`
  in your home directory. Note that this means all dependencies will
  be downloaded again.

## Run the containers

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
1a13f1daddcf        scsesi_varnish      "/start"                 19 seconds ago      Up 18 seconds       0.0.0.0:8090->8080/tcp   scsesi_varnish_1
c6eb5d68b795        scsesi_order        "/bin/sh -c '/usr/..."   21 seconds ago      Up 19 seconds       8080/tcp                 scsesi_order_1
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

You can terminate all containers using `docker-compose down`.
