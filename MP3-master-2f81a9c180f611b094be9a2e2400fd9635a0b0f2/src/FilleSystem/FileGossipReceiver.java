package FilleSystem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import Gossip.Member;
import Gossip.application;

public class FileGossipReceiver implements Runnable{
	
    private AtomicBoolean running = new AtomicBoolean();
    private Member self;
    private DatagramSocket server;
    private int portNo = 7777;					//Fixed for FileListGossiper
     
    public FileGossipReceiver(Member self)
    {
        this.running.set(true);
        this.self = self;
        try {
            server = new DatagramSocket(portNo);
        } catch (SocketException e) {
            e.printStackTrace();
        }
         
    }
    public void terminate()
    {
    	
    	this.running.set(false);
    	System.out.println("FG Receiver Thread Stopped");
    	System.out.println(running.get());
    }
    
    public synchronized void MergeWithRemote(List<FileList> receivedList) throws InterruptedException
    {
    	System.out.println("Merge method called");
    	// application.fileList.clear();
    	//
    		
    	application.fileList.clear();
    	
    	for (FileList item : receivedList) {
    		System.out.println("came inside loop");			
    		application.fileList.add(item);
      	}

    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Receiver  gossiper started");
		System.out.println(portNo);
        while(running.get())
        {
        try {
        byte[] buf = new byte[1024];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        server.receive(p);


        // extract the member arraylist out of the packet
        // TODO: maybe abstract this out to pass just the bytes needed
        ByteArrayInputStream bais = new ByteArrayInputStream(p.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object readObject = ois.readObject();
        if(readObject instanceof List<?>) {
            List<FileList> list = (List<FileList>) readObject;
//            for (Member member : list)
//            {
//                String id = member.getId();
//                 
//            }
//	    System.out.println("object received");
            MergeWithRemote(list);
         
            //Thread.sleep(1000);
        }
    } catch (IOException e) 
    {
        e.printStackTrace();
        this.running.set(false);
    } catch (ClassNotFoundException e) 
    {
        e.printStackTrace();
        this.running.set(false);
    }
    catch(InterruptedException ie)
    {
             
        ie.printStackTrace();
        this.running.set(false);
    }
 
    }
		
	}

}
