# After changes have been pushed, use this script to re-run the server.
# Assumption: current / previous server was already shut down with ctrl-c
git pull
build
run
