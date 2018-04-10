Build:
gradle build


Run:
./gradlew bootRun

go to browser or curl:
http://localhost:8004/users?search=suggested,scenario-suggested&filter=&with=contacts,questions,scenarios,user.publicMoments,relationships&user_id=1&limit=10

return
{"users":[{"id":1,"score":3.0,"popularity":22.0,"distance":1.0,"lastactivity":"none","type":"success"}]}

Log data in ./l2-mt.log

Model parameters are hard coded in ABTest treatment AbTestClient
run ./gradlew bootRun --debug to show print out log

You also need to run ranker:
https://github.com/messingliu/ranker/blob/master/README.md




Steps for kafka develop and test:
1. Follow https://kafka.apache.org/quickstart to setup kafka in local machine
2. Add or update avro schema files in src/main/resources/avro
3. Build and generate java file in build/generated/avro
4. Import the java file and develop
5. Follow the example of topic test/test.avsc/Test.java to develop the producer and consumer
6. Produce the kafka event by run the app
7. Use the kafka consumer script to verify the producer
8. Use the kafka consumer in L2 to consume the event to test the consumer
9. To test the kafka producer with Kafka cluster, change kafka.producer.bootstrap in application.properties

# l2-mt
