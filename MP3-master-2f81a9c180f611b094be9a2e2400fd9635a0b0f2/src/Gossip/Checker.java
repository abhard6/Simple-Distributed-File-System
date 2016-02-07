package Gossip;

import Leader.Election;
import Leader.ElectionTCPClient;

import java.util.concurrent.atomic.AtomicBoolean;


public class Checker implements Runnable {
	
	private long tfail;
	private long tdelete;
	private Member self;
//	private volatile boolean running;
	private AtomicBoolean running = new AtomicBoolean();
	Checker(Member self)
	{
		this.tfail = 2000;
		this.tdelete = 4000;
		this.self = self;
		this.running.set(true);
	}
	
    public void terminate()
    {
    	System.out.println("Checker Thread Stopped");
    	//this.running = false;
    	running.set(false);
    	System.out.println("Checker Thread Stopped");
    	System.out.println(running.get());
    }
	@Override
	public void run() {
		while(running.get()){
		for(int i =0 ;i<application.activeNodes.size();i++)
		{
			Member localMember = application.activeNodes.get(i);
			if((!localMember.getMarkedFail()) && (System.currentTimeMillis()-localMember.getTimestamp() > this.tfail) && !(localMember.getAddress().compareTo(this.self.getAddress())==0))
			{
				localMember.setMarkedFail();
				localMember.setTimestamp();
				Receiver.LOGGER.info("Marked the Member as Fail" + String.valueOf(localMember.getMarkedFail()));
			//	System.out.println("Marked");
			}
			if((localMember.getMarkedFail()) && (System.currentTimeMillis()-localMember.getTimestamp() > this.tdelete)&& !(localMember.getAddress().compareTo(this.self.getAddress())==0) && !(localMember.getAddress().compareTo(application.introducerIp)==0))
			{
				application.activeNodes.remove(localMember);
				
				Receiver.LOGGER.info("Time for receiving the notification of removing the local Member" + String.valueOf(localMember.getTimestamp()));
				Receiver.LOGGER.info("Removing the Member" + String.valueOf(localMember));
				
				
				application.deadlist.add(localMember);
				Receiver.LOGGER.info("Adding the Member to the Delete List" + String.valueOf(localMember));
				
			//	String[] ip = application.leaderIP.split(":");
				
			//	if(localMember.getAddress().equals(ip[0]))
				if(localMember.getAddress().equals(application.leaderIP))
				{
					
				//	 synchronized (Election.lock) {
							Election._electionFlag = true;
							ElectionTCPClient etc = new ElectionTCPClient();
							etc.initiateElection();
							Election._electionFlag = false;
					//		}
				}
				
				System.out.println("Node Deleted");
			}
		
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}

}
