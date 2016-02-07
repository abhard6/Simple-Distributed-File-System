package FilleSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class FileTCPServer implements Runnable {

	private static int portno;
	private AtomicBoolean acceptMore= new AtomicBoolean();

	public void stop()
	{
		this.acceptMore.set(false); 
	}
	
	public FileTCPServer(){
		
		this.acceptMore.set(true);
		this.portno = 4444;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 ServerSocket serverSocket = null;
		 ExecutorService executorService = Executors.newFixedThreadPool(4);
		 try {
			 
			 
			 
	            serverSocket = new ServerSocket(portno);
	            System.out.println("Server started.");	 
		 while (true) {
	         
	                Socket socket = serverSocket.accept();
	                FTServerThread ftd = new FTServerThread(socket);
	                Thread abc = new Thread(ftd);  
	        		executorService.submit(abc);
	                System.out.println("Accepted connection : ");
		
		 }
	}catch (IOException exp) {
        exp.printStackTrace();
    } finally {
        try {
            serverSocket.close();
        } catch (Exception e) {
        	//executorService.shutdownNow();
        }
    }

}


}
	
	
