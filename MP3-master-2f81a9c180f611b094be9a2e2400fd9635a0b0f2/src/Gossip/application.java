package Gossip;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import java.util.concurrent.ExecutionService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutionException;
import FilleSystem.FileGossipReceiver;
import FilleSystem.FileGossiperSender;
import FilleSystem.FileList;
import FilleSystem.FileTCPClient;
import FilleSystem.FileTCPServer;
import Leader.ElectionTCPServer;



public class application {

//    public static ArrayList<Member> activeNodes;
    public static List<Member> activeNodes;
    public static List<Member> deadlist;  
    public static List<FileList> fileList;
    public  Member self;
   // public boolean MainFlag;
    private AtomicBoolean mainFlag = new AtomicBoolean();
    //String selfIp;
    public static String introducerIp;
    public static int introducerPort;
    public static String myIpAddress;
    public static int myPort;
    public static String leaderIP;
   
    public application() throws UnknownHostException
    {
    	this.mainFlag.set(true);
        this.introducerIp = "172.22.151.28";
        this.leaderIP = "172.22.151.30";
        this.myIpAddress =  InetAddress.getLocalHost().getHostAddress().toString();   
        this.introducerPort = 2000;
        this.myPort = 2000;
       
    }
   
    public void terminate()
    {
    	mainFlag.set(false);
    	System.out.println("Checker Thread Stopped");
    	}
    
    public Member getSelf() {
        return self;
    }

    public void setSelf(Member self) {
        this.self = self;
    }
   
    public void print()
    {
    	for(int i =0 ;i<application.activeNodes.size();i++)
    	{
    		String id = activeNodes.get(i).getId();
    		String status = Integer.toString(activeNodes.get(i).getHeartBeat());
    		String format = "%-40s%s%n";
    		System.out.printf(format,"ID(IP:TimeStamp","CurrentHeartBeat");
    		System.out.printf(format,id,status);
    	}
    }
   
    public void printFileList()
    {
    	for(int i =0 ;i<application.fileList.size();i++)
    	{
    		String fileName = fileList.get(i).get_filename();
    		HashSet<String> storeAddress =  fileList.get(i).getStoreAddress();
    		String format = "%-40s%s%n";
    		System.out.printf(format,"FileName","StoredAT");
    		System.out.printf(format,fileName,storeAddress);
    	}
    }
    public void iniateSenderReceiver() throws InterruptedException
    {
    	ExecutorService executorService = Executors.newFixedThreadPool(10);	
    	String Filename = null;
    	
    	
        Receiver receiver = new Receiver(this.self);
        Thread receiverThread = new Thread(receiver);
    //  receiver thread for gossip started
        Sender sender = new Sender(this.self);
        Thread senderThread = new Thread(sender);
  //     sender thread for gossip started
        
        Checker checker = new Checker(this.self);
        Thread checkerThread = new Thread(checker);
   //    checker thread for gossip started
        
        ElectionTCPServer ets = new ElectionTCPServer();
        Thread electionThread = new Thread(ets);
    //  election TCP server started  	
        
        FileGossiperSender fgs = new FileGossiperSender(this.self);
        Thread fileGossiperThread = new Thread(fgs);
    //   FileList Gossiper started    
        
        FileGossipReceiver fgr = new FileGossipReceiver(this.self);
        Thread fileGossiperReceiverThread = new Thread(fgr);
    //  File Gossiper receiver started     
		FileTCPServer fts = new FileTCPServer();		
		Thread ftsServer = new Thread(fts);
    //  File transfer server started
		
        while(this.mainFlag.get())
        {
        	System.out.println("Welcome!! What do you want to with the application");
        	System.out.println("Press number corresponding to key");
        	System.out.println("1 See the current membership list");
        	System.out.println("2 See the seld id");
        	System.out.println("3 join the group");
        	System.out.println("4 Voluntarily Leave the group");
        	System.out.println("5 See the current leader");
        	System.out.println("6 Put File");
        	System.out.println("7 Get File");
        	System.out.println("8 Delete file");
        	System.out.println("9 Show current file list");
        	int userInput;
        	Scanner a = new Scanner(System.in);
        	userInput=a.nextInt();
        	Thread.sleep(200);
        	switch(userInput)
        	{
        	case 1 :				print();
        							continue;
        							
        	case 2 :				System.out.printf("%-40s%s%n","Self Id:",activeNodes.get(application.activeNodes.indexOf(this.self)).getId());
        							continue;

        	case 3 :				executorService.submit(receiverThread);
									executorService.submit(senderThread);
									executorService.submit(checkerThread);
									executorService.submit(electionThread);
									executorService.submit(fileGossiperThread);
									executorService.submit(fileGossiperReceiverThread);
									executorService.submit(ftsServer);
        							continue;
        							
        	case 4 :         		checker.terminate();
    								sender.terminate();
    								receiver.terminate();
    								terminate();
    								ets.stop();
    								fgs.terminate();
    								fgr.terminate();
    								fts.stop();	
    								executorService.shutdown();
    								break;
    								
    		case 5 : 				System.out.printf("%-40s%s%n","CurrentLeader:",leaderIP);						
    								continue;
    		case 6 :				System.out.println("Please Enter the file name");
									BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
									try {
									Filename = stdin.readLine();
									FileTCPClient put = new FileTCPClient();
									put.putfile(Filename);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
    								continue;
    								
    		case 7 :                System.out.println("Please Enter the file name");
									BufferedReader stdinget = new BufferedReader(new InputStreamReader(System.in));
									try {
										Filename = stdinget.readLine();
									} catch (IOException e) {
										e.printStackTrace();
									}
									FileTCPClient get = new FileTCPClient();
									get.getfile(Filename);
									continue;
    		case 8 :				System.out.println("Please Enter the file name");
									BufferedReader stdingetd = new BufferedReader(new InputStreamReader(System.in));
									try {
										Filename = stdingetd.readLine();
									} catch (IOException e) {
										e.printStackTrace();
									}
									FileTCPClient getd = new FileTCPClient();
									getd.deletefile(Filename);
    								continue;
    		case 9 : 				printFileList();						
									continue;
    		default:				System.out.println("You are fired !! It's not a correct option");
    								break;
        	}
        	
        	System.exit(0);

        	
        	//return;
        }

       
    }
   
    public void startup()
    {

        activeNodes = Collections.synchronizedList((new ArrayList<Member>()));
        //Checks whether the Member is the Introducer or Not
        deadlist = Collections.synchronizedList((new ArrayList<Member>()));
        
        fileList = Collections.synchronizedList((new ArrayList<FileList>()));
        
        // For testing whether the file list is sent
//        List<String> storeAddress = new ArrayList<String>();
//        storeAddress.add("127.0.0.1");
//        fileList.add(new FileList("Shivam",storeAddress));
        
        
        if(introducerIp.compareTo(myIpAddress)==0)
        {
            System.out.println("Initialised");
           
            this.self = new Member(myIpAddress,myPort,0,myIpAddress+":"+System.currentTimeMillis(),System.currentTimeMillis(), false, false);
            activeNodes.add(this.self);
           
        }
        else
        {
            System.out.println("Initialised Both");
            this.self = new Member(myIpAddress,myPort,0,myIpAddress+":"+System.currentTimeMillis(),System.currentTimeMillis(), false, false);
            activeNodes.add(this.self);
            Member Introducer = new Member(introducerIp,introducerPort,0,introducerIp+":"+System.currentTimeMillis(), System.currentTimeMillis(), false, false);
            activeNodes.add(Introducer);
        }
       
    }
   
    public static void main(String [] args)
    {
        application ap;
        System.out.println(" Enter the Choices ");
        try {
            ap = new application();
            ap.startup();
            ap.iniateSenderReceiver();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        

    }


   
}
