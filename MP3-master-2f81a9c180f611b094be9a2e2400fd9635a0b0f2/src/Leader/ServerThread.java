package Leader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Gossip.application;





public class ServerThread implements Runnable {

    private Socket socket;
    ElectionTCPClient etc;
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Process socket...
    	System.out.println("Thread Started");
        try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
			 BufferedReader reader = new BufferedReader(new InputStreamReader(
		                socket.getInputStream()));
			 String ClientMessage; 
			 socket.getRemoteSocketAddress().toString();
			 while((ClientMessage = reader.readLine())!=null && !Thread.currentThread().isInterrupted())
			 {	
				System.out.println("Reached inside server while loop");
				
				 //Check if other thread is already running a election algorithm and there is a leader already elected 
				 if(ClientMessage.equals(Election._electionMessage)) //&& Election._electionFlag == false)
				 {
							 synchronized (Election.lock) {
									Election._electionFlag = true;
									ElectionTCPClient etc = new ElectionTCPClient();
									System.out.println("Sendin the reply to election message");	
									writer.println(Election._okMessage);
									etc.initiateElection();
								//	writer.println(Election._okMessage);
									Election._electionFlag = false;
									}			
					//break;
				 }
				 
				 //Waits in the loop till coordinator message is received
				 if(ClientMessage.equals(Election._coordinatorMessage))
				 {
					 synchronized (Election.lock) {
					 	InetSocketAddress sockaddr = (InetSocketAddress)socket.getRemoteSocketAddress();
					 	 InetAddress inaddr = sockaddr.getAddress();
					 	 Inet4Address in4addr = (Inet4Address)inaddr;
					 	 String temp = in4addr.toString().substring(1, 14);
						 Election.currentLeader = temp;//in4addr.toString();
						 application.leaderIP = temp;//L	in4addr.toString();
						 Election._electionFlag = false;
						 Election._leaderElectedFlag = true;
					//	 etc.shutdownElection();
					//	System.out.println("Coordinator is" );
					//	 System.out.print(Election.currentLeader);	
						
				 }		
					break;
				 }
			 }
			 //writer.println(ServerMessage);
			 writer.close();
			 reader.close();
			 socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception");
			
		//	e.printStackTrace();
		}
            
    }
}
