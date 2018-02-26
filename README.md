# Spring

* Used spring batch framework because it is extendable and provides lot of functionalities out of the box.
* Spring batch itself provides multi threading capabilities however i have used custom multi threading just because for the additional points.
* Added unit tests for couple of methods to prove that i am familier with writing unit test cases.If it is required to write unit test cases for any specific method please let me know.
* This application assumes that each event is in one line on the log file so currently using FlatFileStreamReader. We can extend spring batch to implement any custom logic.
* Please feel free to reach out to me for any questions. 

FUTURE ROADMAP:

If we know the application is going to be on linux environment then we can use split command to split the log file and process individul files using mutlithreading approach. If we need to design for all the environments then we may need to design the source system to write the logs in multipart rather than writing to a large file.





