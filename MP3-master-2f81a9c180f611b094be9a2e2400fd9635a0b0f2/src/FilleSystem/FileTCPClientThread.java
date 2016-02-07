package FilleSystem;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class FileTCPClientThread implements Runnable {

	private String serverHostname;
	private int portNo;
	private String Filename;
	private String sendMeesage;
	public FileTCPClientThread(String ipAddress, int portNo,String Filename,String sendMeesage){
		
		this.serverHostname = ipAddress;
		this.portNo = portNo;
		this.Filename = Filename;
		this.sendMeesage = sendMeesage;	
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("Inside Run method");
		System.out.println(this.serverHostname);
		System.out.println(this.portNo);
		Socket sock = null;
		OutputStream os = null;
		InputStream is = null;
		try {
		sock = new Socket(serverHostname, portNo);
		System.out.println("Socket Created");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Thread started Client 1111");
		if(sendMeesage.equalsIgnoreCase("Insert")){		
		try {
			
			System.out.println("Reached inside insert");
			 is = sock.getInputStream();
			System.out.println("Ashu ka comment");
		    	 os = sock.getOutputStream();
      	//    DataInputStream responseMessage  = new DataInputStream(is);
      	  	 DataOutputStream requestMessage = new DataOutputStream(os);
      	   	 requestMessage.writeUTF(Filename+":"+sendMeesage);
      	   	 requestMessage.flush();
      	    System.out.println("requestMessageSent");
      	  
			//For sending the File
			
            File myFile = new File(("/home/upadhyy3/MP3_final/Localfolder/"+Filename));
            byte[] mybytearray = new byte[(int) myFile.length()];
	    System.out.println("File read into bytesream");
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
	    System.out.println("dis created");
            dis.readFully(mybytearray, 0, mybytearray.length);       
            requestMessage.writeLong(mybytearray.length);
            requestMessage.write(mybytearray, 0, mybytearray.length);
            requestMessage.flush();
            System.out.println("File "+Filename+" sent to Server.");
            fis.close();
            bis.close();
            dis.close();
            os.close();
            requestMessage.close();
            sock.close();
            return;
            
        } catch (Exception e) {
            System.err.println("File does not exist!");
        }
	}
		
	if(sendMeesage.equalsIgnoreCase("Receive")){	
		 try {
			 
			 //for receiving a file from Server
				System.out.println("Reached inside insert");
				is = sock.getInputStream();
				System.out.println("Ashu ka comment");
			    os = sock.getOutputStream();
	      	//  DataInputStream responseMessage  = new DataInputStream(is);
	      	  	 DataOutputStream requestMessage = new DataOutputStream(os);
	      	   	 requestMessage.writeUTF(Filename+":"+sendMeesage);
	      	   	 requestMessage.flush();
	      	    
	      	    	System.out.println("requestMessageSent");
			 
	            int bytesRead;
	            is = sock.getInputStream();

	            DataInputStream clientData = new DataInputStream(is);

	           // String fileName = clientData.readUTF();
	            FileOutputStream output = new FileOutputStream(("/home/upadhyy3/MP3_final/Localfolder/" + Filename));
	            long size = clientData.readLong();
	            byte[] buffer = new byte[1024];
	            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
	                output.write(buffer, 0, bytesRead);
	                size -= bytesRead;
	            }

	            output.close();
	            is.close();

	            System.out.println("File "+Filename+" received from Server.");
	        } catch (IOException ex) {
	           // Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }
	if(sendMeesage.equalsIgnoreCase("Remove")){
        
        try{
                
        //is = sock.getInputStream();
    os = sock.getOutputStream();
    DataOutputStream requestMessage = new DataOutputStream(os);
    requestMessage.writeUTF(Filename+":"+sendMeesage);
    requestMessage.flush();
    
    
}catch(Exception e){
        
        e.printStackTrace();
}
	}	
		
}
}	
		
		

	
	
