# Spring

* Used spring batch framework because it is extendable for future use cases and provides lot of functionalities out of the box.
* Spring batch itself provides multi threading capabilities however i have used custom multi threading due to the requirement in the assignment.
* Added unit tests for couple of methods to prove that i am familiar with writing unit test cases. If it is required to write unit test cases for any specific method please let me know.
* This application assumes that each event is in one line on the log file so currently using FlatFileStreamReader. We can extend spring batch to implement any custom logic.
* Please feel free to reach out to me for any questions. 


CONFIGURATIONS:

* Number of threads to be spanned should be decided based up on the number of cores of processor.
* For better performance we should decide chunk size based on the velocity of each event.


FUTURE ROADMAP:

If we know the application is going to be on Unix environment then we can use split command to split the log file and process individual files using multi threading approach. If we need to design for all Windows and Unix environments then we may need to design the source system to write the logs in to multiple files rather than writing to a large file.






