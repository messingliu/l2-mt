Build:
gradle build


Run:
./gradlew bootRun

go to browser or curl:
http://localhost:8080/users?search=suggested,scenario-suggested&filter=&with=contacts,questions,scenarios,user.publicMoments,relationships&id=1&limit=10

return
{"users":[{"id":1,"score":3.0,"popularity":22.0,"distance":1.0,"lastactivity":"none","type":"success"}]}

Log data in ./l2-mt.log
run ./gradlew bootRun --debug to show print out log

# l2-mt
