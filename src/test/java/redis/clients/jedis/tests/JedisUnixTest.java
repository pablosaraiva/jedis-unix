package redis.clients.jedis.tests;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisUnix;

public class JedisUnixTest {
  @Test
  public void testUnixSocketsConnection() {
    Jedis jedis = new JedisUnix("/var/run/redis/redis.sock");
    jedis.connect();
    String pong = jedis.ping();
    Assert.assertEquals("PONG", pong);
  }
}