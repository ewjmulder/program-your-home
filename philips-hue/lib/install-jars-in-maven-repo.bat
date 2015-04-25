call mvn install:install-file -Dfile="%CD%/huelocalsdk.jar" -DgroupId=philips-hue -DartifactId=huelocalsdk -Dversion=3.0 -Dpackaging=jar
call mvn install:install-file -Dfile="%CD%/huesdkresources.jar" -DgroupId=philips-hue -DartifactId=huesdkresources -Dversion=3.0 -Dpackaging=jar
