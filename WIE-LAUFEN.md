# Beispiel starten

Die ist eine Schritt-für-Schritt-Anleitung zum Starten der Beispiele.
Informationen zu Maven und Docker finden sich im
[Cheatsheet-Projekt](https://github.com/ewolff/cheatsheets-DE).


## Installation

* Die Beispiele sind in Java implementiert. Daher muss Java
  installiert werden. Die Anleitung findet sich unter
  https://www.java.com/en/download/help/download_options.xml . Da die
  Beispiele kompiliert werden müssen, muss ein JDK (Java Development
  Kit) installiert werden. Das JRE (Java Runtime Environment) reicht
  nicht aus. Nach der Installation sollte sowohl `java` und `javac` in
  der Eingabeaufforderung möglich sein.

* Die Beispiele laufen in Docker Containern. Dazu ist eine
  Installation von Docker Community Edition notwendig, siehe
  https://www.docker.com/community-edition/ . Docker kann mit
  `docker` aufgerufen werden. Das sollte nach der Installation ohne
  Fehler möglich sein.

* Die Beispiele benötigen zum Teil sehr viel Speicher. Daher sollte
  Docker ca. 4 GB zur Verfügung haben. Sonst kann es vorkommen, dass
  Docker Container aus Speichermangel beendet werden. Unter Windows
  und macOS findet sich die Einstellung dafür in der Docker-Anwendung
  unter Preferences/ Advanced.

* Nach der Installation von Docker sollte `docker-compose` aufrufbar
  sein. Wenn Docker Compose nicht aufgerufen werden kann, ist es nicht
  als Teil der Docker Community Edition installiert worden. Dann ist
  eine separate Installation notwendig, siehe
  https://docs.docker.com/compose/install/ .

## Build

Wechsel in das Verzeichnis `scs-demo-esi-order` und starte `./mvnw
clean package` oder `mvnw.cmd clean package` (Windows). Das wird
einige Zeit dauern:

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

Weitere Information zu Maven gibt es im
[Maven Cheatsheet](https://github.com/ewolff/cheatsheets-DE/blob/master/MavenCheatSheet.md).

Falls es dabei zu Fehlern kommt:

* Stelle sicher, dass die Datei `settings.xml` im Verzeichnis  `.m2`
in deinem Heimatverzeichnis keine Konfiguration für ein spezielles
Maven Repository enthalten. Im Zweifelsfall kannst du die Datei
einfach löschen.

* Die Tests nutzen einige Ports auf dem Rechner. Stelle sicher, dass
  im Hintergrund keine Server laufen.

* Führe die Tests beim Build nicht aus: `./mvnw clean package
  -Dmaven.test.skip=true` bzw. `mvnw.cmd clean package
  -Dmaven.test.skip=true` (Windows).

* In einigen selten Fällen kann es vorkommen, dass die Abhängigkeiten
  nicht korrekt heruntergeladen werden. Wenn du das Verzeichnis
  `repository` im Verzeichnis `.m2` löscht, werden alle Abhängigkeiten
  erneut heruntergeladen.

## Docker Container mit Docker Compose starten

Weitere Information zu Docker gibt es im
[Docker Cheatsheet](https://github.com/ewolff/cheatsheets-DE/blob/master/DockerCheatSheet.md).

Zunächst musst du die Docker Images bauen. Wechsel in das Verzeichnis 
`docker` und starte `docker-compose build`. Das lädt die Basis-Images
herunter und installiert die Software in die Docker Images:

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

Wenn der Build nicht klappt, dann kann man mit  `docker-compose build
--no-cache` die Container komplett neu bauen.

Danach sollten die Docker Images erzeugt worden sein. Sie haben das
Präfix `scsesi`:

```
[~/SCS-ESI/docker]docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED              SIZE
scsesi_varnish                                          latest              f7e4c455f136        35 seconds ago      328MB
scsesi_order                                            latest              c6111b6e47f2        39 seconds ago      205MB
scsesi_common                                           latest              476e076c6294        6 weeks ago         6.91MB
```

Nun kannst Du die Container mit `docker-compose up -d` starten. Die
Option `-d` bedeutet, dass die Container im Hintergrund gestartet
werden und keine Ausgabe auf der Kommandozeile erzeugen.

```
[~/SCS-ESI/docker]docker-compose up -d
Creating scsesi_common_1  ... done
Creating scsesi_order_1   ... done
Creating scsesi_varnish_1 ... done
```

Du kannst nun überprüfen, ob alle Docker Container laufen:

```
[~/SCS-ESI/docker]docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
1a13f1daddcf        scsesi_varnish      "/start"                 19 seconds ago      Up 18 seconds       0.0.0.0:8080->8080/tcp   scsesi_varnish_1
c6eb5d68b795        scsesi_order        "/bin/sh -c '/usr/..."   21 seconds ago      Up 19 seconds       0.0.0.0:8090->8080/tcp                 scsesi_order_1
1fb2aecbe10e        scsesi_common       "/common"                6 weeks ago         Up 20 seconds       8180/tcp                 scsesi_common_1
```

`docker ps -a`  zeigt auch die terminierten Docker Container an. Das
ist nützlich, wenn ein Docker Container sich sofort nach dem Start
wieder beendet..

Wenn einer der Docker Container nicht läuft, kannst du dir die Logs
beispielsweise mit `docker logs scsesi_order_1` anschauen. Der Name
der Container steht in der letzten Spalte der Ausgabe von `docker
ps`. Das Anzeigen der Logs funktioniert auch dann, wenn der Container
bereits beendet worden ist. Falls im Log steht, dass der Container
`killed` ist, dann hat Docker den Container wegen Speichermangel
beendet. Du solltest Docker mehr RAM zuweisen z.B. 4GB. Unter Windows
und macOS findet sich die RAM-Einstellung in der Docker application
unter Preferences/ Advanced.

Um einen Container genauer zu untersuchen, kannst du eine Shell in dem
Container starten. Beispielsweise mit `docker exec -it
scsesi_order_1 /bin/sh` oder du kannst in dem Container ein
Kommando mit `docker exec  scsesi_order_1 /bin/ls` ausführen.
Leider funktioniert das nur in dem Order- und Varnish-Container. Der
Common-Container enthält nur ein Go Binary und daher keine weiteren
Werkzeuge.

Das Beispiel steht unter http://localhost:8080/ zur Verfügung.
In der Version hat Varnish die ESI-Includes prozessiert. Unter http://localhost:8090/ steht die Version mit dem HTML mit ESI-Includes zur Verfügung, wie es eigentlich von dem Order SCS ausgeliefert wird.


Mit `docker-compose down` kannst Du alle Container beenden.


## Beispiel auf Minikube ausführen

* Installiere
[minikube](https://github.com/kubernetes/minikube/releases). Minikube
bietet eine Kubernetes-Umgebung in einer virtuellen Maschine. Minikube
ist einfach zu benutzen und zu installieren. Es soll keine
Produktionsumgebung sein, sondern dient nur dazu, Kubernetes
auszuprobieren oder Entwicklermaschinen aufzubauen.


* Installiere eine Minikube-Instanz mit `minikube start
--memory=5000`. Die Instanz hat dann 5.000 MB RAM. Das sollte für das
Beispiel ausreichend sein. Die Anzahl der CPUs kann je nach genutzter
Hardware geändert werden. Lösche vor der Installation gegebenenfalls
bereits vorhandene Minikube-Instanzen mit `minikube delete`, da
Minikube anderenfalls die Einstellungen für Speicher und CPU nicht
beachtet.

```
[~/SCS-ESI]minikube start --cpus=2 --memory=5000
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

* Installiere
  [kubectl](https://kubernetes.io/docs/tasks/kubectl/install/). Das
  ist das Kommandozeilenwerkzeug für den Umgang mit Kubernetes.

* Der Java-Code muss übersetzt worden sein, wie oben beschrieben.

* Installiere Ingress auf minikube: `minikube addons enable ingress`


* Konfiguriere Docker so, dass es den Kubernetes Cluster nutzt. Nur so
  können die Docker Images in den Kubernetes Cluster übertragen
  werden: `minikube docker-env`(macOS / Linux) oder `minikube.exe
  docker-env`(Windows) beschreibt, wie man dafür vorgehen muss.

* Danach sollte `docker images` die Kubernetes Docker Images anzeigen:

```
[~/SCS-ESI/docker]docker images
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

* Starte `docker-build.sh` im Verzeichnis
`docker`. Das Skript erzeugt die Docker
Images.

```
[~/SCS-ESI/docker]./docker-build.sh 
...
Step 5/10 : RUN apt-get update
---> 58b78486b7d0
Step 6/10 : RUN apt-get install -y -qq varnish                                                             ---> 8b55d00245f3                                                                                         Step 7/10 : COPY default.vcl /etc/varnish/default.vcl                                                     ---> d7670ed17c62                                                                                         Step 8/10 : COPY start /start                                                                             ---> 9047ecb1d5e3                                                                                         Step 9/10 : RUN chmod 0755 /start                                                                         ---> f3cc3ceed8c4                                                                                         Step 10/10 : CMD ["/start"]                                                                               ---> 429b0893f00a                                                                                         Successfully built 429b0893f00a                                                                           Successfully tagged scs-demo-esi-varnish:latest  
```


* Manchmal können die Images nicht heruntergeladen werden. Versuche
  dann, `docker logout` einzugeben und das Skript erneut zu starten.

* Die Images sollten nun verfügbar sein:

```
[~/microservice-istio/microservice-istio-demo]docker images
REPOSITORY                                TAG                 IMAGE ID            CREATED              SIZE
...
scs-demo-esi-order                        latest              18da0f33925f        2 hours ago         318MB                                                                                                     scs-demo-esi-varnish                      latest              429b0893f00a        2 hours ago         310MB
scs-demo-esi-common                       latest              ba1a226c150c        2 hours ago         8.41MB
...
```

* Deploye die Microservices mit `kubectl`:

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

* Starte `kubectl get services`, um die Kubernetes-Services zu sehen:

```
[~/SCS-ESI/docker]kubectl get services
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
common       NodePort    10.98.59.22      <none>        8180:30227/TCP   30m
kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP          37d
order        NodePort    10.102.235.215   <none>        8080:30454/TCP   30m
varnish      NodePort    10.100.151.221   <none>        8080:31833/TCP   30m
```

* Finde die IP-Addresse der minikube-Instanz heraus:

```
[~/SCS-ESI/docker]minikube ip
192.168.99.143
```

Das Beispiel steht unter dieser Addresse zur Verfügung, also beispielsweise http://192.168.99.143/ . 
In der Version hat Varnish die ESI-Includes prozessiert. Unter http://192.168.99.143/order-raw/ steht die Version mit dem HTML mit ESI-Includes zur Verfügung, wie es eigentlich von dem Order SCS ausgeliefert wird.