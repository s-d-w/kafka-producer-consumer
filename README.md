# Kafka Producer Consumer 

A demo showing how to use Spring Boot as a Kafka producer and consumer. 

The producer enqueues in Kafka
messages that are POSTed to one of its REST endpoints. Each consumer app is configured to run a
specified number of consumers in a thread pool to take advantage of system capacity. 

This demo covers setting up a full Kafka cluster as well as a full Zookeeper ensemble, but they aren't required.
You can provide your own topic implementation, just adjust the configuration properties accordingly.



## Table of contents
- [Overview](#overview)
- [Requirements](#requirements)
- [Setup](#setup)
- [Running](#running)

- - - 

## Overview

In this demo you'll:

- create a 3 node Kafka cluster running locally
- create a 3 node Zookeeper ensemble running locally
- create a topic called 'demo'
- partition the 'demo' topic into 3 partitions
- set the replication factor to 3, so each partition will be replicated to 2 other nodes
- run a Spring Boot producer and POST content to its 'record' rest endpoint
- run a Sprint Boot consumer which runs three consumer threads internally - each configured to run in the same consumer group 'group1'

Messages are *ordered* on a partition level only. Currently, the partitioning strategy is round robin robin - uses the default partitioner.

Depending on the amount of processing that's done during consumption, it may be advantageous to use
additional cores and run more than one consumer thread per app (as in the case of this demo).
A scalability goal for this demo's design is to make the number of consumer threads per consumer app 
a divisor of the number of partitions. 

Just be aware that additional consumer threads - in the same consumer group - beyond the number of partitions 
will sit idle and do nothing. If there are fewer consumers - in the same consumer group - than the number of partitions, consumers will consume
from more than one partition. You'll never see more than one consumer - in the same consumer group - consuming from the same partition.

For example, let's say you have a topic with 32 partitions (a particular partitioning strategy). Given the processing load for each consumer and the cost per VM instance, you've decided to run 2 consumer threads per 
app. You'll want to keep 16 consumer app instances running *with the goal* that each partition has its own consumer.

- - - 

## Requirements

* Apache Kafka - this demo uses version 2.12-2.1.0 which can be found at https://www.apache.org/dyn/closer.cgi?path=/kafka/2.1.0/kafka_2.12-2.1.0.tgz
* Apache Zookeeper - this demo uses version 3.4.13 which can be found at https://archive.apache.org/dist/zookeeper/
* curl - `brew install curl`

- - - 

## Setup

The configs and directories are provided, all that's needed is to download Zookeeper & Kafka binaries and unpack into each node directory.

##### Zookeeper

Download the zookeeper tar.gz and unpack it into each node directory in the zookeeper folder under the project's root directory.

`cd zookeeper/node1`

`tar xzf ~/Downloads/zookeeper-3.4.13.tar.gz`

An example of the path will look like `zookeeper/node1/zookeeper-3.4.13/`
 
Open up three terminals (or a terminal with three tabs) and cd into a different zookeeper node directory in each terminal, then run:

`./zookeeper-3.4.13/bin/zkServer.sh start conf/zookeeper.cfg`

Check the status of a particular node with:

`./zookeeper-3.4.13/bin/zkServer.sh status conf/zookeeper.cfg`

To show the log in the console:

`tail -f zookeeper.out`

##### Kafka

Download the kafka tar.gz and unpack it into each node directory in the kafka-cluster folder under the project's root directory.

`cd kafka-cluster/node1`

`tar xzf ~/Downloads/kafka_2.12-2.1.0.tgz`

Open up three terminals (or a terminal with three tabs) and cd into a different kafka node directory in each terminal, then run:

`./kafka_2.12-2.1.0/bin/kafka-server-start.sh config/server.properties`

This will take over the terminal window with logging. Open another terminal or tab and cd into any node so as to issue administration requests.

Create the 'demo' topic used by the producer and consumer:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181,localhost:2182,localhost:2183 --replication-factor 3 --partitions 3 --topic demo`

##### Useful Kafka stuff
Check the status of a topic:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --describe --topic demo --zookeeper localhost:2181,localhost:2182,localhost:2183`

List topics:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --list --zookeeper localhost:2181,localhost:2182,localhost:2183`

Display offsets for topic:

`./kafka_2.12-2.1.0/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --group group1 --describe`

Delete records from topic:

`echo '{ "partitions": [{ "topic": "demo", "partition": 0, "offset": 28 }, { "topic": "demo", "partition": 1, "offset": 29 }, { "topic": "demo", "partition": 2, "offset": 30 }], "version": 1 }' > delete.records.json`

`./kafka_2.12-2.1.0/bin/kafka-delete-records.sh  --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --offset-json-file ./delete.records.json`

##### Test the cluster with command line producers and consumers (optional)

To test out producing and consuming, start a consumer in a separate terminal:

`./kafka_2.12-2.1.0/bin/kafka-console-consumer.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --topic demo --from-beginning`

Start a producer in a separate terminal, then type some text and press enter to send

`./kafka_2.12-2.1.0/bin/kafka-console-producer.sh --broker-list localhost:9091,localhost:9092,localhost:9093 --topic demo`

To start several consumers in the same consumer group. Run this in several terminals:

`./kafka_2.12-2.1.0/bin/kafka-console-consumer.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --topic demo --group group1 --from-beginning`

----

## Running

Open up a two terminals (or two tabs) and with one cd into the producer directory and the other cd into the consumer directory. In each run:

`./gradlew bootrun`

To create messages, open up another terminal (or tab) and post to the record endpoint on the producer:

`curl -v POST localhost:8081/api/record -H "Content-Type: application/json" -d '{"body": "foo"}'`

You should see logging output in both producer and consumer terminals:

`producer.controller.MessageController : Creating a new message: Message{body='foo'}`

`consumer.worker.KafkaConsumerWorker : Consumer client1 received message: Message{body='foo'}`

The default partitioning strategy is round robin. If you POST several times, you should see each consumer consume a message.

```
2019-03-18 19:41:33.910  INFO 2787 --- [pool-1-thread-3] c.k.consumer.worker.KafkaConsumerWorker  : Consumer client2 received message: Message{body='foo'}
2019-03-18 19:41:34.093  INFO 2787 --- [pool-1-thread-2] c.k.consumer.worker.KafkaConsumerWorker  : Consumer client1 received message: Message{body='foo'}
2019-03-18 19:41:34.278  INFO 2787 --- [pool-1-thread-1] c.k.consumer.worker.KafkaConsumerWorker  : Consumer client0 received message: Message{body='foo'}
```






