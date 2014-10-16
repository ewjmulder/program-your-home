# Note: this batch file is just for easy building (and later running) on a machine that just has a checkout of the repo.
cd ..
mvn clean install -Dmaven.test.skip
cd bootstrap
