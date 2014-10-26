# Simple way to run the server from a maven command (when moved to plugin use exec:exec if extra VM args are still needed)
mvn exec:java -Dexec.mainClass="com.programyourhome.bootstrap.ProgramYourHome" -Dexec.classpathScope=runtime -Djava.net.preferIPv4Stack=true
