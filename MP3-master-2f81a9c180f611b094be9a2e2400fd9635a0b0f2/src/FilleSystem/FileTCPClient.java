package FilleSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Gossip.application;


public class FileTCPClient {

	private static List<String> myList = new ArrayList<String>();
	private static int fileTransferPort = 4444;
	private String leaderIp = "172.22.151.29";
	private static String Message;
	
	public FileTCPClient(){
	myList.add("172.22.151.28");
	myList.add("172.22.151.29");
	myList.add("172.22.151.30");
	

	
	}
	
	public HashSet<String> requestLeaderforIpList(String Filename, String Message)
	{	
		Socket sock;
		HashSet<String> receivedList = new HashSet<String>();
		try {
			sock = new Socket(application.leaderIP,fileTransferPort);
			InputStream is = sock.getInputStream();
		System.out.println("After Socket");
	        OutputStream os = sock.getOutputStream();
		System.out.println("After od");
	        //Sending file name and file size to the serverMessage
		System.out.println("After oObjeOS");
	       // DataInputStream dis = new DataInputStream(is);
	        DataOutputStream dos = new DataOutputStream(os);
		System.out.println("First Socket established");
	        dos.writeUTF(Filename+":" + Message);
		ObjectInputStream ois = new ObjectInputStream(is);
		boolean done = true;
		while(done)
		{
				System.out.println("First Socket established");
				try {
			        Object readObject = ois.readObject();
			        receivedList = (HashSet<String>) readObject;
					if(receivedList.size()>0)
					{
					System.out.println(receivedList);
					done = false;
					}
				}
				catch (ClassNotFoundException e)
				{
					done = false;
					System.out.println("File doesn't exist in the system");
				}

		}
	        dos.close();
	        ois.close();
	        os.close();
	        is.close();
	        sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("host Excception");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IOexception");	
		} 
		
		

		
		return receivedList;
	}
	
	public void putfile(String Filename){
		System.out.println("reached inside put");
		
		Message = "put";

		HashSet<String> receivedList=requestLeaderforIpList(Filename, Message);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		String sendMessage = "Insert";

		for(String ip:receivedList){
			System.out.println(ip);
			FileTCPClientThread ftc = new FileTCPClientThread(ip, fileTransferPort ,Filename,sendMessage);	
			Thread client = new Thread(ftc);
			System.out.println("After creating thread");

			//client.start();	
			executorService.submit(client);
			
			
		}
	}
		
		
		public void getfile(String Filename){
			
			Message = "get";
			
			HashSet<String> receivedList=requestLeaderforIpList(Filename, Message);
			ExecutorService executorService = Executors.newFixedThreadPool(2);	
			String sendMessage = "Receive";
			for(String ip:receivedList){
				
				FileTCPClientThread ftc = new FileTCPClientThread(ip, fileTransferPort ,Filename,sendMessage);	
				Thread client = new Thread(ftc);
				executorService.submit(client);
			
			
		}
		}
			public void deletefile(String Filename){
                
                Message = "delete";
                
                HashSet<String> receivedList=requestLeaderforIpList(Filename, Message);
                ExecutorService executorService = Executors.newFixedThreadPool(2);      
                String sendMessage = "Remove";
                for(String ip:receivedList){
                        
                        FileTCPClientThread ftc = new FileTCPClientThread(ip, fileTransferPort ,Filename,sendMessage); 
                        Thread client = new Thread(ftc);
                        executorService.submit(client);
        }
		
		
	}
	
	
}
