package Leader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientThread implements Runnable{
	
	private String serverHostname;
	private int electionPort;
	private String electionMessage;
	ElectionTCPClient etc = new ElectionTCPClient();

	public ClientThread(String serverHostname,int electionPort,String electionMessage)
	{
		this.serverHostname = serverHostname;
		this.electionPort = electionPort;
		this.electionMessage = electionMessage;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	System.out.println("Client Thread started");
		System.out.println(Thread.currentThread().getId());
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
        		return;
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
		return;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: " + serverHostname);
        }
	} 
	}


