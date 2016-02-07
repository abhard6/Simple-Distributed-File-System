package Leader;
public class Election {

	public static String _electionMessage = "Election";
	public static String _okMessage = "Ok";
	public static String _coordinatorMessage = "Coordinator";
//	public static String _terminateProtocol = "terminate" ;
	public static final Object lock = new Object(); 
	public static boolean _electionFlag = false;
	public static String currentLeader ;
	public static boolean _leaderElectedFlag = true;
}
