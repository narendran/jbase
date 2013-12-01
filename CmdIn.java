import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Cmdline thread to get the inputs from command line and pass it to the JBase key value store.
 * @author narendran
 *
 */
public class CmdIn implements Runnable{
	
	@Override
	public void run() {
		JBase parser = JBase.getInstance();
		Commands command = Commands.NULL;
		while(!command.equals(Commands.END)){
			Scanner in = new Scanner(System.in);
			String line = in.nextLine();
			parser.parse(line);
		}
	}

}
