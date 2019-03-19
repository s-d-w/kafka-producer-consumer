# Kafka Producer Consumer 

A demo showing how to use Spring Boot as a Kafka producer and consumer. 

The producer enqueues in Kafka
messages that are POSTed to one of its REST endpoints. Each consumer app is configured to run a
specified number of consumers in a thread pool to take advantage of system capacity. 

This demo covers setting up a full Kafka cluster as well as a full Zookeeper ensemble, but they aren't required.
You can provide your own topic implementation, just adjust the configuration properties accordingly.

---

## Table of contents
- [Requirements](#requirements)
- [Setup](#setup)
- [Running](#running)

---

### Requirements

* Apache Kafka - this demo uses version 2.12-2.1.0 which can be found at https://www.apache.org/dyn/closer.cgi?path=/kafka/2.1.0/kafka_2.12-2.1.0.tgz
* Apache Zookeeper - this demo uses version 3.4.13 which can be found at https://archive.apache.org/dist/zookeeper/

---

### Setup

The configs and directories are provided, all that's needed is to download Zookeeper & Kafka binaries and unpack into each node directory.

###### Zookeeper

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

###### Kafka

Download the kafka tar.gz and unpack it into each node directory in the kafka-cluster folder under the project's root directory.

`cd kafka-cluster/node1`

`tar xzf ~/Downloads/kafka_2.12-2.1.0.tgz`

Open up three terminals (or a terminal with three tabs) and cd into a different kafka node directory in each terminal, then run:

`./kafka_2.12-2.1.0/bin/kafka-server-start.sh config/server.properties`

This will take over the terminal window with logging. Open another terminal or tab and cd into any node so as to issue administration requests.

Create the 'demo' topic used by the producer and consumer:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181,localhost:2182,localhost:2183 --replication-factor 3 --partitions 3 --topic demo`

###### Useful Kafka stuff
Check the status of a topic:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --describe --topic demo --zookeeper localhost:2181,localhost:2182,localhost:2183`

List topics:

`./kafka_2.12-2.1.0/bin/kafka-topics.sh --list --zookeeper localhost:2181,localhost:2182,localhost:2183`

Display offsets for topic:

`./kafka_2.12-2.1.0/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --group group1 --describe`

Delete records from topic:

`echo '{ "partitions": [{ "topic": "demo", "partition": 0, "offset": 28 }, { "topic": "demo", "partition": 1, "offset": 29 }, { "topic": "demo", "partition": 2, "offset": 30 }], "version": 1 }' > delete.records.json`

`./kafka_2.12-2.1.0/bin/kafka-delete-records.sh  --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --offset-json-file ./delete.records.json`

- - -

To test out producing and consuming, start a consumer in a separate terminal:

`./kafka_2.12-2.1.0/bin/kafka-console-consumer.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --topic demo --from-beginning`

Start a producer in a separate terminal, then type some text and press enter to send

`./kafka_2.12-2.1.0/bin/kafka-console-producer.sh --broker-list localhost:9091,localhost:9092,localhost:9093 --topic demo`

To start several consumers in the same consumer group. Run this in several terminals:

`./kafka_2.12-2.1.0/bin/kafka-console-consumer.sh --bootstrap-server localhost:9091,localhost:9092,localhost:9093 --topic demo --group group1 --from-beginning`

----

### Running

Open up a two terminals (or two tabs) and with one cd into the producer directory and the other cd into the consumer directory. In each run:

`./gradlew bootrun`

To create messages, post the the record endpoint on the producer:

`curl -v POST localhost:8081/api/record -H "Content-Type: application/json" -d '{"body": "foo"}'`

You should see logging output in both producer and consumer:

`.producer.controller.MessageController       : Creating a new message: Message{body='foo'}`

`consumer.worker.KafkaConsumerWorker  : Consumer client1 received message: Message{body='foo'}`






