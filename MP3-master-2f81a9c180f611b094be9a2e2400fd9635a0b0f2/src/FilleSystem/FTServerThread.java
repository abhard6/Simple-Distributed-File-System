package FilleSystem;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class FTServerThread implements Runnable {
	
	private Socket socket;
	private String Filename;

	FTServerThread(Socket socket){
		
		this.socket = socket;
	}	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
			

				try {	
				InputStream is = socket.getInputStream();
			    OutputStream os = socket.getOutputStream();
	       	    DataInputStream requestMessage = new DataInputStream(is);
//	       	    
	       	    //DataOutputStram
		    boolean done = true;
		    while(done)
		    {
		     System.out.println("Inside the while loop");
	       	    String a = requestMessage.readUTF();
	       	    String delims = "[:]";
		    System.out.println(a);
	       	    String[] tokens = a.split(delims);
	       		
	       		String Filename = tokens[0];
	       		String Actiontype = tokens[1];
	       		System.out.println(Filename);
			System.out.println(Actiontype);
	       		
	       	    
	       	    if(Actiontype.equalsIgnoreCase("Put"))
	       	    {
	       	    	
	       	    	HashSet<String> ipaddress;   
	       	    	FileListOperation flo = new FileListOperation();
	       	    	System.out.println("reached inside server put");
	       	    	
	       	    	ipaddress = flo.putInsideFileList(Filename);
	       	    	System.out.println("putfile of fileList executed");
	       	    	if(ipaddress.isEmpty())
	       	    	{
	                    System.out.println("list is empty");	    	  //Send the error message	
	       	    	}
	       	    	else{
	       	    	ObjectOutputStream op = new ObjectOutputStream(os);
	       	    	System.out.println("Reached inside firs if");
	       	    	op.writeObject(ipaddress);
	       	    	done = false;
	       	    	op.flush();
	       	    	op.close();
	       	    	}
	       	    	socket.close();
	       	    	break;
	       	    	//process
	       	    }
	       	    
		    
	       	    if(Actiontype.equalsIgnoreCase("get") || Actiontype.equalsIgnoreCase("delete"))
	       	    {
	       	    	
	       	    	HashSet<String> ipaddress;  
	       	    	FileListOperation flo = new FileListOperation();
	       	    	ipaddress = flo.getFromFileList(Filename);
	       	    	if(ipaddress.isEmpty())
	       	    	{
	       	    	  //Send the error message	
	       	    	}
	       	    	else{
	       	    	ObjectOutputStream op = new ObjectOutputStream(os);
	       	    	System.out.println("Reached inside firs if");
	       	    	op.writeObject(ipaddress);
	       	    	done = false;
	       	    	op.flush();
	       	    	op.close();
	       	    	}
	       	    	socket.close();
	       	    	break;
	       	    	//process
	       	    }
	       	    
	       	    
	       	    
	       	    if(Actiontype.equalsIgnoreCase("Insert"))
	       	    {	
			            
		            try {
		       			 
		       			 
		       			 System.out.println("aaaaaaaaaaaaaa");
		       	            			int bytesRead;
					            OutputStream output = new FileOutputStream(("/home/upadhyy3/MP3_final/sdfs_folder/" + Filename));
					            long size = requestMessage.readLong();
					            byte[] buffer = new byte[1024];
					            while (size > 0 && (bytesRead = requestMessage.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
					                output.write(buffer, 0, bytesRead);
					                size -= bytesRead;
					            }
				
					            output.close();
					            requestMessage.close();
					            socket.close();
						    done = false;
					            System.out.println("File "+Filename +" received from client.");
					           // return;
					            break;
					        } catch (IOException ex) {
					            System.err.println("Client error. Connection closed.");
					        }
						 
					 }
	       	 if(Actiontype.equalsIgnoreCase("Receive"))
	       	 {
	     		try {
	    			

	          	    
	          	    	System.out.println("requestMessageSent");
	          	  
	    			//For sending the File
	    			
	                File myFile = new File("/home/upadhyy3/MP3_final/sdfs_folder/"+Filename);
	                byte[] mybytearray = new byte[(int) myFile.length()];
	                System.out.println("File read into bytesream");
	                FileInputStream fis = new FileInputStream(myFile);
	                BufferedInputStream bis = new BufferedInputStream(fis);
	                //bis.read(mybytearray, 0, mybytearray.length);

	                DataInputStream dis = new DataInputStream(bis);
	               
	                System.out.println("dis created");
	                dis.readFully(mybytearray, 0, mybytearray.length);    
	                DataOutputStream responseMessage = new DataOutputStream(os);
	                responseMessage.writeLong(mybytearray.length);
	                responseMessage.write(mybytearray, 0, mybytearray.length);
	                responseMessage.flush();
	                System.out.println("File "+Filename+" sent to Server.");
//	                fis.close();
//	                bis.close();
//	                dis.close();
//	                os.close();
//	                dos.close();
	                socket.close();
	                done = false;
	                break;
	    
	                
	            } catch (Exception e) {
	                System.err.println("File does not exist!");
	            }
	       		 
	       	 }
	       	if(Actiontype.equalsIgnoreCase("Remove")){
                
                
                File file = new File("/home/upadhyy3/MP3_file/sdfs_folder/" + Filename);
        boolean exists = file.exists();

        if (!exists) 
        {

         System.out.println("File does not exist");

       }

             else{

       Runtime.getRuntime().exec("rm -r -f  " + "/home/upadhyy3/MP3_file/sdfs_folder/"+ Filename);
       System.out.println("This file has been removed"+ Filename);
       done = false;
             }

       }
		    
		    
		    }
		    
		    

	}catch (Exception e)
	{
		System.err.println("Data Input Stream exception");
	}

	}
}
