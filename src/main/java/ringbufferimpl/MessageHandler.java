package ringbufferimpl;

import api.SyslogMessage;
import com.lmax.disruptor.WorkHandler;

import java.util.Random;

public class MessageHandler implements WorkHandler<SyslogMessage> {

	private Random seed = new Random();
	private final int SIM_SENDING_COST_MAX = 10;

	@Override
	public void onEvent(SyslogMessage message) throws Exception {
		doPushSyslogServer(message.message);
	}

	/**
	 * 	simulation pushing to syslog server cost here
	 */
	private void doPushSyslogServer(String message) throws InterruptedException {
		long cost = 0;
		while(cost < 1000000l) {
			cost ++;
		}
		//System.out.println("Consume sequence: " + sequence + " sending: " + message);
	}
}

