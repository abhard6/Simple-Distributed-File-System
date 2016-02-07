package FilleSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application {
	
	private static String FileName;
	
	

	public static void startup(){
		
//		AtomicBoolean acceptMore= new AtomicBoolean(true);
		FileTCPServer fts = new FileTCPServer();
		
		Thread ftsServer = new Thread(fts);
		
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		executorService.submit(ftsServer);
		
		
		
		while(true){
			
			System.out.println("1 to put the file");
			System.out.println("2 to get the file");
			System.out.println("3 to delete the file");
			int userInput;
	    	Scanner a = new Scanner(System.in);
	    	userInput=a.nextInt();
	    	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch(userInput)
        	{
        	case 1 :				System.out.println("Please Enter the file name");
        							BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        							try {
									FileName = stdin.readLine();
        							} catch (IOException e) {
        								// TODO Auto-generated catch block
        								e.printStackTrace();
        							}
        							FileTCPClient put = new FileTCPClient();
        							put.putfile(FileName);
        							continue;
        							
        	case 2 :				
        							System.out.println("Please Enter the file name");
        							BufferedReader stdinget = new BufferedReader(new InputStreamReader(System.in));
        							try {
        								FileName = stdinget.readLine();
        							} catch (IOException e) {
        								e.printStackTrace();
        							}
        							FileTCPClient get = new FileTCPClient();
        							get.getfile(FileName);
        							continue;
        							
        	case 3:						System.out.println("Did not stop");
        							fts.stop();						
        							executorService.shutdown();
        							break;

        	}
			
			
		}
		
	}
	
	 public static void main(String[] args){
		 
		 
		 
		startup();
		
		
	}
	
	
}