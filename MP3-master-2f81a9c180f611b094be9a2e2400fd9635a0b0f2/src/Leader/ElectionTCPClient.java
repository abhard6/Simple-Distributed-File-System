package Leader;

import Gossip.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import Gossip.Member;

public class ElectionTCPClient {

//	public List<String> application.activeNodes = new ArrayList<String>();
	private static int electionPort = 6969;
	private AtomicInteger numberOfOkmeesages = new AtomicInteger();

	public ElectionTCPClient()
	{    
	}

	
	// Parse IP parts into an int array
	public int ipParser(String ip)
	{
		String[] Parts;
		Parts = ip.split("\\.");
		int lastTwoDigits = Integer.parseInt(Parts[3]);
		return lastTwoDigits;
	}

	
	//This method will be invoked when some process is marked fail
	synchronized public void initiateElection()
	{
		//int numberOfOkmeesages =0;
		int numberofHigherIdprocesses=0;
		int numberofLowerIdprocesses =0;
		ExecutorService executorService = Executors.newFixedThreadPool(2);
	    List<Future<Integer>> futures= new ArrayList<Future<Integer>>();
		System.out.println("initiate election");
		//ExecutorService executorService = Executors.newFixedThreadPool(100);
		int localIpLastDigit = 0;
		int remoteIpLastDigit =0;
		String ElectionMessage;
		try {
			
			localIpLastDigit = ipParser(InetAddress.getLocalHost().getHostAddress().toString());
			System.out.println(localIpLastDigit);
			} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
			} 
		int max = localIpLastDigit;
		for(int i=0;i<application.activeNodes.size();i++)
		{
			// Parse IP parts into an int array
			Member member = application.activeNodes.get(i);
			remoteIpLastDigit = ipParser(member.getAddress());
			System.out.println(remoteIpLastDigit);
			if (remoteIpLastDigit > localIpLastDigit)
				{
						max = remoteIpLastDigit;
						numberofHigherIdprocesses++;
						
				}
		}
		
		numberofLowerIdprocesses = application.activeNodes.size()-numberofHigherIdprocesses -1;
		CountDownLatch doneSignal;
		if(max == localIpLastDigit)
		{	 doneSignal = new CountDownLatch(numberofLowerIdprocesses);
			for(int i=0;i<application.activeNodes.size();i++)
			{	
				Member member = application.activeNodes.get(i);
				System.out.println("yaha aara hei harami thread");
				remoteIpLastDigit = ipParser(member.getAddress());
				 synchronized (Election.lock) {
						try {
							
							Election.currentLeader = InetAddress.getLocalHost().getHostAddress().toString();
							application.leaderIP = InetAddress.getLocalHost().getHostAddress().toString();
							 Election._electionFlag = false;
							 Election._leaderElectedFlag = true;
							} 
						catch (UnknownHostException ec) {
							// TODO Auto-generated catch block
							ec.printStackTrace();
							System.out.println(ec.getMessage());
							System.exit(1);
							}
				 }
				if (remoteIpLastDigit < localIpLastDigit)  //(remoteIpLastDigit > localIpLastDigit)
				{
					Election._electionFlag = true;
					Future<Integer> future = executorService.submit(new ClientCallable(member.getAddress(),electionPort,Election._coordinatorMessage));
		//			Future<?> future = executorService.submit(new ClientThread(application.activeNodes.get(i),electionPort,Election._coordinatorMessage),doneSignal);
					futures.add(future);
				
				
				}
			}
		}
		else{		
			doneSignal = new CountDownLatch(numberofHigherIdprocesses);
			for(int i=0;i<application.activeNodes.size();i++)
				{	
					Member member = application.activeNodes.get(i);
					remoteIpLastDigit = ipParser(member.getAddress());
					if (remoteIpLastDigit > localIpLastDigit)  //(remoteIpLastDigit > localIpLastDigit)
					{
						Future<Integer> future = executorService.submit(new ClientCallable(member.getAddress(),electionPort,Election._electionMessage));
			//			Future future = executorService.submit(new ClientThread(application.activeNodes.get(i),electionPort,Election._electionMessage),doneSignal);	
						futures.add(future);
					}
				}		
			System.out.println("Cam out of the loop");
			}
		
	
		for(Future future : futures)
		{
			try {
			System.out.println(new Date() + "::" + future.get());
			numberOfOkmeesages.addAndGet((int) future.get(5, TimeUnit.SECONDS));
		//	numberOfOkmeesages=+ (int) future.get(1, TimeUnit.SECONDS);
			System.out.println(numberOfOkmeesages);
			} catch (InterruptedException | ExecutionException
				| TimeoutException e) {
			// TODO Auto-generated catch block
				future.cancel(true);
				if(numberOfOkmeesages.doubleValue()==0)
				{
					Election._electionFlag = true;
					for(int i=0;i<application.activeNodes.size();i++)
					{
						Member member = application.activeNodes.get(i);
						remoteIpLastDigit = ipParser(member.getAddress());
						System.out.println("I am leader from exception");
						if (remoteIpLastDigit < localIpLastDigit)  //(remoteIpLastDigit > localIpLastDigit)
						{
						executorService.submit(new ClientCallable(member.getAddress(),electionPort,Election._coordinatorMessage));
						}
					}
					 synchronized (Election.lock) {
							try {
								
								Election.currentLeader = InetAddress.getLocalHost().getHostAddress().toString();
								application.leaderIP = InetAddress.getLocalHost().getHostAddress().toString();
								 Election._electionFlag = false;
								 Election._leaderElectedFlag = true;
								} 
							catch (UnknownHostException ec) {
								// TODO Auto-generated catch block
								ec.printStackTrace();
								System.out.println(e.getMessage());
								System.exit(1);
								}	
						
				 }
				}
				if(numberOfOkmeesages.doubleValue()>0 && numberOfOkmeesages.doubleValue() < numberofHigherIdprocesses)
				{
					//shutdownElection();
					executorService.shutdown();
					System.out.println("Task timed out waiting for ok messages restarting election");
				//	initiateElection();
				}

			}
	//	executorService.shutdown();
		}
		
		executorService.shutdown();
		if(numberOfOkmeesages.doubleValue()==0)
		{
			for(int i=0;i<application.activeNodes.size();i++)
			{
				Election._electionFlag = true;
				Member member = application.activeNodes.get(i);
				remoteIpLastDigit = ipParser(member.getAddress());
				System.out.println("I am leader");
				if (remoteIpLastDigit < localIpLastDigit)  //(remoteIpLastDigit > localIpLastDigit)
				{
				executorService.submit(new ClientCallable(member.getAddress(),electionPort,Election._coordinatorMessage));
				}
			}
			 synchronized (Election.lock) {
					try {
						
						Election.currentLeader = InetAddress.getLocalHost().getHostAddress().toString();
						application.leaderIP = InetAddress.getLocalHost().getHostAddress().toString();
						 Election._electionFlag = false;
						 Election._leaderElectedFlag = true;
						} 
					catch (UnknownHostException ec) {
						// TODO Auto-generated catch block
						ec.printStackTrace();
						System.out.println(ec.getMessage());
						System.exit(1);
						}	
				
		 }
		}
		if(numberOfOkmeesages.doubleValue()>0 && numberOfOkmeesages.doubleValue() < numberofHigherIdprocesses)
		{
			//shutdownElection();
			executorService.shutdown();
			System.out.println("Task timed out waiting for ok messages restarting election");
		//	initiateElection();
		}
	System.out.println("Never Reached here");	
	executorService.shutdown();
//	shutdownElection();
	return;
	}
	
	
}
    
