package redis.clients.jedis;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;

public class ClientUnix extends Client {
    private String unixDomainSocket;

    public ClientUnix(String unixDomainSocket) {

        this.unixDomainSocket = unixDomainSocket;
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            try {
                setSocket(AFUNIXSocket.newInstance());
                getSocket().connect(new AFUNIXSocketAddress(new File(unixDomainSocket)), getConnectionTimeout());
                getSocket().setSoTimeout(getSoTimeout());

                setOutputStream(new RedisOutputStream(getSocket().getOutputStream()));
                setInputStream(new RedisInputStream(getSocket().getInputStream()));
            } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
                throw new JedisConnectionException("Failed connecting to unix domain socket " + unixDomainSocket, e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return getSocket() != null && !getSocket().isClosed() && getSocket().isConnected()
                && !getSocket().isInputShutdown() && !getSocket().isOutputShutdown();
    }

    private void setSocket(Socket socket) throws NoSuchFieldException, IllegalAccessException {
        setByReflection(Connection.class, "socket", this, socket);
    }

    private void setOutputStream(RedisOutputStream redisOutputStream) throws NoSuchFieldException, IllegalAccessException {
        setByReflection(Connection.class, "outputStream", this, redisOutputStream);
    }

    private void setInputStream(RedisInputStream redisInputStream) throws NoSuchFieldException, IllegalAccessException {
        setByReflection(Connection.class, "inputStream", this, redisInputStream);
    }

    private void setByReflection(Class clazz, String fieldName, Object obj, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }
}
