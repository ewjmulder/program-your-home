call mvn install:install-file -Dfile="%CD%/huelocalsdk.jar" -DgroupId=philips-hue -DartifactId=huelocalsdk -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile="%CD%/sdkresources.jar" -DgroupId=philips-hue -DartifactId=sdkresources -Dversion=1.0 -Dpackaging=jar
