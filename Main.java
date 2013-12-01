/**
 * Main class to start the CmdLine thread
 * 
 * @author narendran
 * 
 */
public class Main {
	
	public static void main(String args[]){
		new Thread(new CmdIn()).start();;
	}

}
