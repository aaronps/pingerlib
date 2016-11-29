package nettools.pinger;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author krom
 */
public final class PingManager implements Runnable
{
    
    private final static Logger logger = Logger.getLogger("PingManager");
    
    private final ScheduledExecutorService executor;
    private boolean active = false;
    private final ConcurrentLinkedQueue<String> to_remove = new ConcurrentLinkedQueue();
    private final ConcurrentLinkedQueue<String> to_add = new ConcurrentLinkedQueue();
    private final HashMap<String, PingStatus> pingstatus_map = new HashMap();
    
    public PingManager()
    {
        executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public boolean start()
    {
        if ( ! active )
        {
            executor.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
            active = true;
        }
        return active;
    }
    
    public void stop()
    {
        if ( active )
        {
            executor.shutdownNow();
            active = false;
            synchronized(pingstatus_map)
            {
                for ( PingStatus ps: pingstatus_map.values())
                {
                    ps.stop();
                }
                pingstatus_map.clear();
            }
        }
    }
    
    public void addIp(String ip)
    {
        to_add.add(ip);
    }
    
    public void removeIp(String ip)
    {
        to_remove.add(ip);
    }
    
    @Override
    public void run()
    {
        synchronized(pingstatus_map)
        {
            String ip;
            while ( (ip = to_remove.poll()) != null )
            {
                PingStatus ps = pingstatus_map.remove(ip);
                if ( ps != null )
                {
                    ps.stop();
                }
            }
            
            while ( (ip = to_add.poll()) != null )
            {
                PingStatus ps = new PingStatus(ip);
                ps.start();
                pingstatus_map.put(ip, ps);
            }
        }
    }
    
}
