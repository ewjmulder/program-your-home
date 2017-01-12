# TODO:
# - git pull
# - build prereq projects (or get from nexus)
mvn exec:java -Dbarcodescanner.properties.location=./src/main/resources/barcodescanner.properties.prod -Dmaven.repo.local=/home/pi/.m2/repository >> server.log
