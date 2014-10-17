call mvn install:install-file -Dfile="%CD%/huelocalsdk.jar" -DgroupId=philips-hue -DartifactId=huelocalsdk -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile="%CD%/huesdkresources.jar" -DgroupId=philips-hue -DartifactId=huesdkresources -Dversion=2.0 -Dpackaging=jar
