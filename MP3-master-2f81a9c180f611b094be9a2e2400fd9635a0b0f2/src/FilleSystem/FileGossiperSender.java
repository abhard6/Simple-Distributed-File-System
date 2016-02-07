package FilleSystem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import Gossip.Member;
import Gossip.application;

public class FileGossiperSender implements Runnable{



		//Two randomly selected members to which data will be sent to
//		private volatile boolean running;
		private AtomicBoolean running = new AtomicBoolean();
		private Member self;
		private int portNo = 7777;

		
		public FileGossiperSender(Member self)
		{
			this.self = self;
			this.running.set(true);

		}
		
	    public void terminate()
	    {
	    	this.running.set(false);
	    	System.out.println("FG Snder Thread Stopped");
	    	System.out.println(running.get());
	    }
	    

			
		@Override
		public void run() {
			System.out.println("File gossiper started");
			// implement getRandomNode method to get random node from startuplist
			
		 while(this.running.get() )
		 {	
			while(!application.fileList.isEmpty() && this.self.getAddress().equals(application.leaderIP))  // Check for the condition that only leader sends the gossip periodically
			{  		
			//	System.out.println("Inside the inner while loop of file gossiper");
				try{
					for(int i=0;i<application.activeNodes.size();i++)
					{
						
											if(application.activeNodes.get(i).getAddress().equals(this.self.getAddress()))
							{
								continue;
							}

					//	System.out.println("File List sent");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);
						oos.writeObject(application.fileList);
						byte[] buf = baos.toByteArray();
						String ip = application.activeNodes.get(i).getAddress();
						InetAddress destip = InetAddress.getByName(ip);
						int port = portNo; //application.activeNodes.get(i).getport();
						DatagramSocket socket = new DatagramSocket();
						DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, destip, portNo);
						socket.send(datagramPacket);
						socket.close();
					}
				}catch(IOException io)
				{
					io.printStackTrace();
					this.running.set(false);
				}				
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	


		 }
		}
		
}
