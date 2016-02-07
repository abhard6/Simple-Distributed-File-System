package Gossip;
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


public class Sender implements Runnable {

	//Two randomly selected members to which data will be sent to
//	private volatile boolean running;
	private AtomicBoolean running = new AtomicBoolean();
	private ArrayList<Member> selctedNodes;
	private Member self;
	
	private Random rand;
	
	Sender(Member self)
	{
		this.self = self;
		this.running.set(true);
		rand = new Random();
	}
	
    public void terminate()
    {
    	this.running.set(false);
    	System.out.println("Snder Thread Stopped");
    	System.out.println(running.get());
    }
	private ArrayList<Member> getTwoMembersToSend()
	{   int index;
		ArrayList<Member> randomNodes = new ArrayList<Member>();
		//randomNodes will act as temporary nodes and will go out of scope out of the function call $$Could do better$$
//	try{
		
		
			 
		if(application.activeNodes.size() >= 3)
		{
			do {
			index = rand.nextInt(application.activeNodes.size());
			if(randomNodes.contains(application.activeNodes.get(index)))
			{
				randomNodes.clear();
			}
			randomNodes.add(application.activeNodes.get(index));
			if(randomNodes.contains(this.self))
			{
				randomNodes.clear();
			}
			}while(!(randomNodes.size() == 2));
		}
		else if(application.activeNodes.size() == 2)
		{
			do {
				index = rand.nextInt(application.activeNodes.size());
				randomNodes.add(application.activeNodes.get(index));
				if(application.activeNodes.get(index).getAddress()== this.self.getAddress())
				{
					randomNodes.clear();
				}
				}while(!(randomNodes.size() == 1));	
		}
		else
		{
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//			System.out.println("I am alone and there is nothing to send");
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}
	//}
		
//	}
//		catch(UnknownHostException e)
//		{
//		e.printStackTrace();
//		System.exit(0);
        // extract the member arraylist out of the packet
//		}
//	System.out.println("$$$$$$$$$");
//	for(int i =0; i<randomNodes.size();i++)
//	{
//	
//		System.out.println(randomNodes.get(i).getAddress());
//	//	System.out.println(randomNodes.get(i).getId());
//		System.out.println(randomNodes.get(i).getHeartBeat());
//	}
//	System.out.println("$$$$$$$$$");
	return randomNodes;
	}

	@Override
	public void run() {
		// implement getRandomNode method to get random node from startuplist
	 while(this.running.get())
	 {	
		try{
			selctedNodes = new ArrayList<Member>(getTwoMembersToSend());
			for(int i=0;i<selctedNodes.size();i++)
		{
		//	this.self.setHeartBeat(this.self.getHeartBeat()+1);
		//	this.self.setId(this.self.getAddress() + ":"+System.currentTimeMillis());
			if(application.activeNodes.contains(this.self))
			{
				application.activeNodes.get(application.activeNodes.indexOf(self)).setHeartBeat(application.activeNodes.get(application.activeNodes.indexOf(self)).getHeartBeat()+1);
				application.activeNodes.get(application.activeNodes.indexOf(self)).
				setId(application.activeNodes.get(application.activeNodes.indexOf(self)).getAddress()+":"+System.currentTimeMillis());
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(application.activeNodes);
			byte[] buf = baos.toByteArray();
			String ip = selctedNodes.get(i).getAddress();
			InetAddress destip = InetAddress.getByName(ip);
			int port = selctedNodes.get(i).getport();
	// Code used for simulating the packet loss in the network 		
	//		int packetLossRate = rand.nextInt(100);
	//		if(packetLossRate > 10)
	//		{
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, destip, port);
			socket.send(datagramPacket);
			socket.close();
	//		}
		}

		
		//Clearing the list of selected nodes in order to populate it with new nodes
		selctedNodes.clear();
		Thread.sleep(400);	

		}catch(IOException io)
		{
			io.printStackTrace();
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
