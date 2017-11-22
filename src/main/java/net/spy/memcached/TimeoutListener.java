package net.spy.memcached;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface TimeoutListener extends EventListener {

  void onTimeout(Future<?> future) throws Exception;
}
