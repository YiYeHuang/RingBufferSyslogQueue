package api;

public interface ISyslog {
	void log(String message) throws InterruptedException;

	long getCount();

	boolean close();
}
