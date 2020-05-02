#!/bin/bash

rm -rf /var/log/mongodb/{cfg{0,1,2},rs{0,1}-{0,1,2}}.log

su -s /bin/bash -c 'mongod --quiet --fork --configsvr --replSet cfgReplSet --port 27917 --logpath /var/log/mongodb/cfg0.log --dbpath /var/lib/mongodb/cfg0' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --configsvr --replSet cfgReplSet --port 27918 --logpath /var/log/mongodb/cfg1.log --dbpath /var/lib/mongodb/cfg1' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --configsvr --replSet cfgReplSet --port 27919 --logpath /var/log/mongodb/cfg2.log --dbpath /var/lib/mongodb/cfg2' mongodb

sleep 10

su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs0 --port 27117 --logpath /var/log/mongodb/rs0-0.log --dbpath /var/lib/mongodb/rs0-0' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs0 --port 27118 --logpath /var/log/mongodb/rs0-1.log --dbpath /var/lib/mongodb/rs0-1' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs0 --port 27119 --logpath /var/log/mongodb/rs0-2.log --dbpath /var/lib/mongodb/rs0-2' mongodb

su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs1 --port 27217 --logpath /var/log/mongodb/rs1-0.log --dbpath /var/lib/mongodb/rs1-0' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs1 --port 27218 --logpath /var/log/mongodb/rs1-1.log --dbpath /var/lib/mongodb/rs1-1' mongodb
su -s /bin/bash -c 'mongod --quiet --fork --shardsvr --replSet rs1 --port 27219 --logpath /var/log/mongodb/rs1-2.log --dbpath /var/lib/mongodb/rs1-2' mongodb

sleep 10

su -s /bin/bash -c 'mongos --quiet --fork --configdb cfgReplSet/localhost:27917,localhost:27918,localhost:27919 --port 27017 --logpath /var/log/mongodb/mongos0.log' mongodb
su -s /bin/bash -c 'mongos --quiet --fork --configdb cfgReplSet/localhost:27917,localhost:27918,localhost:27919 --port 27018 --logpath /var/log/mongodb/mongos1.log' mongodb

sleep 10

mongo mongodb://localhost:27017,localhost:27018 --quiet --eval 'sh.startBalancer()'
