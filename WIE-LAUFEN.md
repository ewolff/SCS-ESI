# Beispiel starten

Die ist eine Schritt-für-Schritt-Anleitung zum Starten der Beispiele.

## Installation

* Die Beispiele sind in Java implementiert. Daher muss Java
  installiert werden. Die Anleitung findet sich unter
  https://www.java.com/en/download/help/download_options.xml . Da die
  Beispiele kompiliert werden müssen, muss ein JDK (Java Development
  Kit) installiert werden. Das JRE (Java Runtime Environment) reicht
  nicht aus. Nach der Installation sollte sowohl `java` und `javac` in
  der Eingabeaufforderung möglich sein.

* Die Projekte baut Maven. Zur Installation siehe
  https://maven.apache.org/download.cgi>. Nun sollte `mvn` in der
  Eingabeaufforderung eingegeben werden können.

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

Wechsel in das Verzeichnis `scs-demo-esi-order` und starte `mvn clean
package`. Das wird einige Zeit dauern:

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

Falls es dabei zu Fehlern kommt:

* Stelle sicher, dass die Datei `settings.xml` im Verzeichnis  `.m2`
in deinem Heimatverzeichnis keine Konfiguration für ein spezielles
Maven Repository enthalten. Im Zweifelsfall kannst du die Datei
einfach löschen.

* Die Tests nutzen einige Ports auf dem Rechner. Stelle sicher, dass
  im Hintergrund keine Server laufen.

* Führe die Tests beim Build nicht aus: `mvn clean package package
  -Dmaven.test.skip=true`.

* In einigen selten Fällen kann es vorkommen, dass die Abhängigkeiten
  nicht korrekt heruntergeladen werden. Wenn du das Verzeichnis
  `repository` im Verzeichnis `.m2` löscht, werden alle Abhängigkeiten
  erneut heruntergeladen.

## Docker Container starten

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
[~/microservice/docker]docker-compose up -d
Recreating ms_eureka_1 ... 
Recreating ms_eureka_1 ... done
Recreating ms_zuul_1 ... 
Recreating ms_customer_1 ... 
Recreating ms_zuul_1
Recreating ms_order_1 ... 
Recreating ms_customer_1
Recreating ms_turbine_1 ... 
Creating ms_catalog_1 ... 
Recreating ms_order_1
Recreating ms_turbine_1
Recreating ms_customer_1 ... done
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


Mit `docker-compose down` kannst Du alle Container beenden.

