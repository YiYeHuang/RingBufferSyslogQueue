package ringbufferimpl;


public enum WaitMethod {
    BLOCKING,
    BUSYSPIN,
    SLEEPING,
    YIELDING
}