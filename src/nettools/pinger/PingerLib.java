package nettools.pinger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krom
 */
public class PingerLib
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        PingManager pm = new PingManager();
        pm.start();
        
        pm.addIp("192.168.1.1");
        
        try
        {
            Thread.sleep(2500);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(PingerLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        pm.addIp("192.168.1.2");
        
        try
        {
            Thread.sleep(3500);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(PingerLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        pm.removeIp("192.168.1.1");
        
        try
        {
            Thread.sleep(30000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(PingerLib.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        Logger.getLogger(PingerLib.class.getName()).info("Finishing");
        pm.stop();
        Logger.getLogger(PingerLib.class.getName()).info("Finished");
        
        
        
    }
    
}
