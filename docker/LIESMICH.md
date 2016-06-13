Microservice Demo mit Docker Machine und Dokcer 
============================

Docker Machine kann virtuelle Machine erstellen, auf denen dann Docker
Container laufen können.

Um das Beispiel ablaufen zu lassen:

- Installiere Docker Compose, siehe
https://docs.docker.com/compose/#installation-and-set-up
- Installiere Docker Machine, siehe
https://docs.docker.com/machine/#installation
- Install Docker, see https://docs.docker.com/installation/
- Installiere Virtual Box von https://www.virtualbox.org/wiki/Downloads
- Gehe zum Verzeichnis `scs-demo-esi`  und führe dort `mvn package` aus
- Führe `docker-machine create  --virtualbox-memory "4096" --driver
  virtualbox dev` aus. Das erzeugt eine neue Umgebung names`dev`mit Docker
  Machine. Es wird eine virtuelle Machine in Virtual Box mit 4GB RAM sein.
 - Führe `eval "$(docker-machine env dev)"` (Linux / Mac OS X) oder
    `docker-machine.exe env --shell powershell dev` (Windows,
    Powershell) /  `docker-machine.exe env --shell cmd dev` (Windows,
    cmd.exe) aus. Das docker Kommando nutzt nun die neue virtuelle Maschine als Umgebung.
 - Führe im Verzeichnis `docker` das Kommando `docker-compose
   build` aus und dann `docker-compose up -d`. 

Das Ergebnis:

- Docker Compose erzeugt die Docker Images und startet sie.
- Find mit `docker-machine ip dev`die IP address der vrituellen Maschine
- Eine VirtualBox VM wird von Vagrant gestartet
- Docker wird in der VM gestartet
- Das Order SCS steht unter http://ipadresss:8080/ zur
Verfügung.
- Das Catalog SCS steht unter http://ipadresss:8080/catalog/ zur
Verfügung.
- Nutze `docker-machine rm dev`, um die virtuelle Maschine wieder zu zerstören.
