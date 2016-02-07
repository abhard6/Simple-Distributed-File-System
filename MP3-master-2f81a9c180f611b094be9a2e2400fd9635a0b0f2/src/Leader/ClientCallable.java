package Leader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import Gossip.application;


public class ClientCallable implements Callable<Integer> {

	private String serverHostname;
	private int electionPort;
	private String electionMessage;
	ElectionTCPClient etc = new ElectionTCPClient();

	public ClientCallable(String serverHostname,int electionPort,String electionMessage)
	{
		this.serverHostname = serverHostname;
		this.electionPort = electionPort;
		this.electionMessage = electionMessage;
	}
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
     //   	System.out.println("Client Thread started");
     //   	System.out.println(Thread.currentThread().getId());
            echoSocket = new Socket(serverHostname, electionPort);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
 //       	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        	String userInput;
        	out.println(this.electionMessage);
        	String ServerReply;
		if(this.electionMessage == Election._coordinatorMessage)
        	{
        		System.out.println("It sent the coordinator message");
        		out.close();
        		in.close();
        		echoSocket.close();
        		System.out.println("Closed Socket");
        		synchronized(Election.lock){
        		try{
				Election.currentLeader = InetAddress.getLocalHost().getHostAddress().toString();
				application.leaderIP = InetAddress.getLocalHost().getHostAddress().toString();
				 Election._electionFlag = false;
				 Election._leaderElectedFlag = true;
				} 
        		catch (UnknownHostException ec) {
				// TODO Auto-generated catch block
				ec.printStackTrace();
				System.out.println(ec.getMessage());
				System.exit(1);
				}
        		}
        		return 1;
		}
		else{
    		while ((ServerReply = in.readLine()) != null && !Thread.currentThread().isInterrupted()) 
		       {        System.out.println(ServerReply);
    				if(ServerReply.equals(Election._okMessage))
    				{
    				System.out.println("It received the Ok  message"); 
    				break;
    				}
//    				if(ServerReply.equals(Election._coordinatorMessage))
//    				{
//    				//Stop sending any more messages
//    				break;
//    				}
		       }
		}
	//String ClientReply = "This is Machine acting as client" +  InetAddress.getLocalHost().getHostAddress().toString(); 

  //      System.out.println ("Type Message (\"Bye.\" to quit)");
//	while ((userInput = stdIn.readLine()) != null) 
//           {
//	    out.println(userInput);
//
//            // end loop
//            if (userInput.equals("Bye."))
//                break;
//
//	    System.out.println("echo: " + in.readLine());
//	   }
	
		out.close();
		in.close();
		echoSocket.close();
		System.out.println("Ok k bad wala terminate hua");
	//	etc.shutdownElection();
		return 1;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            return 0;
        //    System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: " + serverHostname);
    		return 0;
        }
	} 

}

