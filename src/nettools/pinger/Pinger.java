package nettools.pinger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krom
 */
public class Pinger implements Runnable
{
    private static final Logger logger = Logger.getLogger("rmypinger-thread");
    private static final long TIME_BETWEEN_RESTARTS = 5000;
    
    public static enum Command { Run, Stop }
    public static enum RunStatus { Idle, Starting, Running, Restarting, Stopping }
    
    volatile Command command = Command.Run;
    RunStatus runstatus = RunStatus.Idle;
    
    String alias;
    String ip;
    Process process;
    long lastReceived;

    Pinger(String alias, String ip)
    {
        this.alias = alias;
        this.ip = ip;
        this.process = null;
        this.lastReceived = 0;
    }
    
    private static class AutoResetStringBuilder implements Appendable
    {
        private final StringBuilder sb;
        public AutoResetStringBuilder()
        {
            sb = new StringBuilder();
        }
        
        public AutoResetStringBuilder(int len)
        {
            sb = new StringBuilder(len);
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException
        {
            sb.append(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException
        {
            sb.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException
        {
            sb.append(c);
            return this;
        }

        @Override
        public String toString()
        {
            final String r = sb.toString();
            sb.setLength(0);
            return r; 
        }
    }
    

    @Override
    public void run()
    {
        Formatter formatter = new Formatter(new AutoResetStringBuilder(1024));
        while ( command == Command.Run )
        {
            runstatus = RunStatus.Starting;

            ProcessBuilder pb = new ProcessBuilder("/bin/ping", ip);
            pb.redirectErrorStream(true);
            BufferedReader reader = null;
            try
            {
                logger.info(formatter.format("[%s@%s] Start ping", alias, ip).toString());

                process = pb.start();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                runstatus = RunStatus.Running;

                String line;
                while ( (line = reader.readLine()) != null )
                {
                    if ( line.startsWith("64") )
                    {
                        lastReceived = System.currentTimeMillis();
                    }
                }
                logger.info(formatter.format("[%s@%s] Reader exit", alias, ip).toString());
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "IOException", ex);
            }
            catch (Exception ex)
            {
                logger.log(Level.SEVERE, "some other exception", ex);
            }
            finally
            {
                logger.info("Finnally");
                if ( reader != null ) { try { reader.close(); } catch (Exception e) {}}
                
                if ( command == Command.Run )
                {
                    runstatus = RunStatus.Restarting;
                    process.destroy();
                    process = null;
                }
                else
                {
                    runstatus = RunStatus.Stopping;
                }
            }
            
            if ( command == Command.Run )
            {
                try { Thread.sleep(TIME_BETWEEN_RESTARTS); } catch (InterruptedException ex) {}
            }
            
        }
    }
    
    void stop()
    {
        command = Command.Stop;
        try
        {
            if ( process != null )
            {
                process.destroy();
                process = null;
            }
            runstatus = RunStatus.Idle;
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Error killing ping process", ex);
        }
    }

}
