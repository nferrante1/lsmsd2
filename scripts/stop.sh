#!/bin/bash

# Root not needed

# Stop balancer
mongo mongodb://localhost:27017,localhost:27018,localhost:27019 --quiet --eval 'sh.stopBalancer()'

# Stop mongos instances
mongo admin --port 27017 --quiet --eval 'db.shutdownServer();'
mongo admin --port 27018 --quiet --eval 'db.shutdownServer();'
mongo admin --port 27019 --quiet --eval 'db.shutdownServer();'

sleep 5

# Stop servers of replica set 0 (first stop secondaries, then primary)
RS00=`mongo --port 27117 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
RS01=`mongo --port 27118 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
RS02=`mongo --port 27119 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
PRIMARYRS0=
if [ "$RS00" != "true" ]; then
	mongo admin --port 27117 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS0=27117
fi
if [ "$RS01" != "true" ]; then
	mongo admin --port 27118 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS0=27118
fi
if [ "$RS02" != "true" ]; then
	mongo admin --port 27119 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS0=27119
fi
sleep 20
mongo admin --port $PRIMARYRS0 --quiet --eval 'db.shutdownServer();'

# Stop servers of replica set 1 (first stop secondaries, then primary)
RS10=`mongo --port 27217 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
RS11=`mongo --port 27218 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
RS12=`mongo --port 27219 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
PRIMARYRS1=
if [ "$RS10" != "true" ]; then
	mongo admin --port 27217 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS1=27217
fi
if [ "$RS11" != "true" ]; then
	mongo admin --port 27218 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS1=27218
fi
if [ "$RS12" != "true" ]; then
	mongo admin --port 27219 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYRS1=27219
fi
sleep 20
mongo admin --port $PRIMARYRS1 --quiet --eval 'db.shutdownServer();'

# Stop config servers (first stop secondaries, then primary)
CFG0=`mongo --port 27917 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
CFG1=`mongo --port 27918 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
CFG2=`mongo --port 27919 --quiet --eval "d=db.isMaster(); print(d['ismaster']);"`
PRIMARYCFG=
if [ "$CFG0" != "true" ]; then
	mongo admin --port 27917 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYCFG=27917
fi
if [ "$CFG1" != "true" ]; then
	mongo admin --port 27918 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYCFG=27918
fi
if [ "$CFG2" != "true" ]; then
	mongo admin --port 27919 --quiet --eval 'db.shutdownServer();'
else
	PRIMARYCFG=27919
fi
sleep 20
mongo admin --port $PRIMARYCFG --quiet --eval 'db.shutdownServer();'
