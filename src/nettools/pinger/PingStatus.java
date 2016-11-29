package nettools.pinger;

/**
 *
 * @author krom
 */
public class PingStatus
{
    public final String ip;
    public long timestamp;

    private final Pinger pinger;
    private Thread thread;
    
    public PingStatus(String ip)
    {
        this.ip = ip;
        this.timestamp = 0;
        pinger = new Pinger(ip, ip);
    }
    
    public void start()
    {
        if ( thread == null )
        {
            thread = new Thread(pinger);
            thread.start();
        }
    }
    
    public void stop()
    {
        if ( thread != null && thread.isAlive() )
        {
            pinger.stop();
            thread = null;
        }
    }
    
}
