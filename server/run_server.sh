#!/bin/sh
SERVER_PORT=1993
echo "start the server at port ${SERVER_PORT}"
java FtpServer ${SERVER_PORT}
