package net.spy.memcached;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.spy.memcached.TestConfig.PORT_NUMBER;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

public class MemcachedTimeoutListenerTest extends TestCase {

  private MemcachedClient client = null;
  private int timeoutCount;

  @Override
  protected void setUp() throws IOException {
    client = new MemcachedClient(AddrUtil.getAddresses("1.1.1.1:" + PORT_NUMBER));
    client.addTimeoutListener(new TimeoutListener() {

      public void onTimeout(Future<?> future) {
        timeoutCount++;
      }
    });
  }

  @Override
  protected void tearDown() {
    if (client != null) {
      try {
        client.shutdown();
      } catch (NullPointerException e) {
        // This is a workaround for a disagreement betweewn how things
        // should work in eclipse and buildr. My plan is to upgrade to
        // junit4 all around and write some tests that are a bit easier
        // to follow.

        // The actual problem here is a client that isn't properly
        // initialized is attempting to be shut down.
      }
    }
  }

  public void testTimeoutListener() throws ExecutionException, InterruptedException {
    try {
      client.get("test");
      fail();
    } catch (OperationTimeoutException e) {
    }
    assertEquals(1, timeoutCount);
    try {
      client.set("test", 1, 1).get(1, MILLISECONDS);
      fail();
    } catch (TimeoutException e) {
    }
    assertEquals(2, timeoutCount);
    try {
      client.getBulk("test1", "test2");
      fail();
    } catch (OperationTimeoutException e) {
    }
    assertEquals(3, timeoutCount);
    client.asyncGetBulk("test3").getSome(1, MILLISECONDS);
    assertEquals(4, timeoutCount);
  }
}
