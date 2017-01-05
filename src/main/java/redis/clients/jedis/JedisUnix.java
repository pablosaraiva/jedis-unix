package redis.clients.jedis;

public class JedisUnix extends Jedis {

    public JedisUnix(final String unixDomainSocket) {
        client = new ClientUnix(unixDomainSocket);
    }
}
