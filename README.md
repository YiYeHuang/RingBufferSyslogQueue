# RingBuffer SyslogQueue
Moved from [My Java notes repo](https://github.com/YiYeHuang/JavaKeyStonesOneAtATime)

##Intention
This repo is the experiment notes for one of my current oncall ticket issue. The root cause is that for case logging is
set to be pushed to a syslog server, when the syslog server is dead, all logging thread is blocked and parked. After clock
is ticking for a while, all thread are set to be restart, and the server goes down.

Here are the problems for the current design
- This is a typical producer and consumer problem, the old implementation is using BlockingQueue
- Queue size 10 years ago cannot handle current microservices cluster logging power
- Although the fix can be done very easily by implementing a fall back method to store log locally when syslog server is dead.
the key to this problem is unblocked the thread.


## Implementation
### Blocking: [BlockingQueue](https://github.com/YiYeHuang/SyslogQueue/tree/master/src/main/java/blockingqueueImpl)
The blocking queue implementation is a simple simulation of current logging system structure. In the original project, 
the [ArrayBlcokingQueue](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ArrayBlockingQueue.html) is used to handle the producer and consumer problem, which is very very very horriable. The initial
fix change the structure to [LinkedBlockingDqueue](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/LinkedBlockingQueue.html) to separate the put lock and take lock. 

### Non-Blocking: [LMAX Disruptor Framework or A.K.A the RingBuffer](https://github.com/YiYeHuang/SyslogQueue/tree/master/src/main/java/ringbufferimpl)
Since the business logic for this case is pretty simple, I did not directly used the Disruptor framework, instead, I used
the core ringbuffer to handle most of the work. The key difference is that the Disruptor handle the producer work with a 
sequence model and hand over to multiple consumer. The consumer is implement as a EventHandler. A wait strategy can be 
choose to define the thread waiting pattern

                                                        Eventhandler1 \
    producer --- log ----> ringbuffer --worker pool---> Eventhandler2  ------> Syslog Server
                                                        Eventhandler3 /

For more complicate business logic, more levels of event handler can be implemented to decouple the thread usage, which 
is the same idea of actors in [Akka framework](https://doc.akka.io/docs/akka/2.5/actors.html).

                                           Eventhandler1 \                         EventFinalStageHandler1
    producer --- work ----> Disruptor ---> Eventhandler2 - Eventlevel2handle ----> EventFinalStageHandler2
                                           Eventhandler3 /                         EventFinalStageHandler3                                        


                    

#### Wait Strategy                                      
| Option                 | Compare |
| -----------------------| :------------- |
| BlockingWaitStategy    | defalt. Similar to blockingqueue, very conservative CPU use |
| SleepingWaitStrategy   | busy wait loop. Use LockSupport.parkNanos(1), not great for high reactive system |
| YieldingWaitStrategy   | Use Thread.yield(), allow other thread in line get to work, opt for # of eventhandle < cpu core|
| BusySpinWaitStrategy   | Most high performance option, opt for total operation thread < CPU core|
