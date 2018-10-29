package ringbufferimpl;

import com.lmax.disruptor.EventFactory;
import concurrency.queue.syslogsimulation.SyslogMessage;

public class MessageFactory implements EventFactory {

	@Override
	public Object newInstance() {
		return new SyslogMessage();
	}
}
