package Leader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class ElectionTCPServer implements Runnable{

	
private static int electionPort;
private AtomicBoolean acceptMore= new AtomicBoolean();
public ElectionTCPServer()
{
	this.acceptMore.set(true); 
	electionPort = 6969;

}

public void stop()
{
	this.acceptMore.set(false); 
}

public void run()
{
    ServerSocket serverSocket = null;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    try {
         serverSocket = new ServerSocket(electionPort, 100);
        while (acceptMore.get()) {
            Socket socket = serverSocket.accept();
            executorService.submit(new ServerThread(socket));
           // new Thread(new SocketThread(socket)).start();    
        }
    } catch (IOException exp) {
        exp.printStackTrace();
    } finally {
        try {
            serverSocket.close();
        } catch (Exception e) {
        	executorService.shutdownNow();
        }
    }

}


}
