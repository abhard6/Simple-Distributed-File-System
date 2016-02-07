package Gossip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
 
 
public class Receiver implements Runnable {

// Logger	
public static final Logger LOGGER = Logger.getLogger(Receiver.class);
 
//    private volatile boolean running;
    private AtomicBoolean running = new AtomicBoolean();
    private Member self;
    private DatagramSocket server;
	public long tfail;
	public long tdelete;
     
    Receiver(Member self)
    {
    	this.tfail = 2000;
		this.tdelete = 4000;
        this.running.set(true);
        this.self = self;
        try {
            server = new DatagramSocket(this.self.getport());
        } catch (SocketException e) {
            e.printStackTrace();
        }
         
    }
    public void terminate()
    {
    	
    	this.running.set(false);
    	System.out.println("Receiver Thread Stopped");
    	System.out.println(running.get());
    }
    public long idParser(String parser)     
    {
        String[] parts = parser.split(":");
        String ip = parts[0];
        long timeStamp = Long.valueOf(parts[1]).longValue();
        return timeStamp;
    }
     
     class Merger extends Thread{

    	 private Member member;
    	 public Merger(Member member)
    	 {
    		 this.member = member;
    	 }
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!member.getMarkedFail())
			{
				//add the new member
				if(!application.activeNodes.contains(member))
				{
					Member newLocalMember = new Member(member.getAddress(),member.getport(),member.getHeartBeat(),member.getId(),member.getTimestamp(),member.getMarkedFail(),member.getMarkedDeleted());
//        	 		System.out.println("New Member added");
        	 		//System.out.println(newLocalMember.getAddress());
        	 		application.activeNodes.add(newLocalMember);
        	 		newLocalMember.setTimestamp();
        	 		
                    		LOGGER.info("A New Member has been added to the List" + newLocalMember); 
                    	//	newLocalMember.setTimestamp();
                   
                    		LOGGER.info("A new Timestamp has been set for New Member" + String.valueOf(newLocalMember.getTimestamp()));
				}
				
				else if(member.getHeartBeat() > (application.activeNodes.get(application.activeNodes.indexOf(member)).getHeartBeat()))
				{
					application.activeNodes.get(application.activeNodes.indexOf(member)).setHeartBeat(member.getHeartBeat());
					application.activeNodes.get(application.activeNodes.indexOf(member)).setTimestamp();
					application.activeNodes.get(application.activeNodes.indexOf(member)).setId(member.getId());
//            		System.out.println("Heartbeat updated");
            		
            		LOGGER.info("This is change in HeartBeat " + String.valueOf(member.getHeartBeat()));
                    	LOGGER.info("This is change in Timestamp" + String.valueOf(member.getTimestamp()));
				}
				// check for this process. is it inactive for a long time? Should I declare it dead?
				else if(System.currentTimeMillis() - application.activeNodes.get(application.activeNodes.indexOf(member)).getTimestamp()
						> Receiver.this.tfail)
				{
					application.activeNodes.get(application.activeNodes.indexOf(member)).setMarkedFail();
					application.activeNodes.get(application.activeNodes.indexOf(member)).setTimestamp();
					// use the above set time to delete the member from the list. This should be done by a different class or thread possibly.
				}

			}
			// case when the received list has the member as dead.
			else
			{
				
				
				// Just in case a dead member hasn't introduce to you before, we need to make sure we will not update our member list with this info
				if(application.activeNodes.contains(member))
				{
					Member localmember = application.activeNodes.get(application.activeNodes.indexOf(member));
					if((!localmember.getMarkedFail()) & (localmember.getHeartBeat() < member.getHeartBeat()))
					{
						// TODO clash of thoughts here. Piyush wants an additional
						// check on the heartbeat, Kevin disagrees.
						LOGGER.info("Marking machine id: "+member.getAddress()+ " as Inactive (dead)");
						application.activeNodes.get(application.activeNodes.indexOf(member)).setMarkedFail();
						application.activeNodes.get(application.activeNodes.indexOf(member)).setTimestamp();
						// We are updating this so that we can compare it with _TCleanUp.
					}
				}
			}
		}
	}
		
   
     


    // extract the member arraylist out of the packet
    
    @Override
    public void run() {
    	ExecutorService executorService = Executors.newFixedThreadPool(2);
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
                List<Member> list = (List<Member>) readObject;
            //    ArrayList<Member> list = (List<Member>) readObject;
 
            //    System.out.println("Received member list:");
                for (Member member : list)
                {
                    String id = member.getId();
                    if(!member.getAddress().equals(this.self.getAddress()))
                    {
                    	Thread mergerThread = new Merger(member);
                    	//mergerThread.start();
                    	executorService.execute(mergerThread);
                    }
                   
                }
             //   MergeWithRemote(list);

 // Calling the merge method to update the local list with new heart beat and Id values
                     
                }
               
             
                //Thread.sleep(1000);
            
        } catch (IOException e) 
        {
            e.printStackTrace();
            this.running.set(false);
        } catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
            this.running.set(false);
        }
//        catch(InterruptedException ie)
//        {
//                 
//            ie.printStackTrace();
//            this.running.set(false);
//        }
     
        }
    }
} 
