import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class CmdParser 
 * @author narendran
 * Singleton that gets the input line from CmdIn, checks syntax and delegates execution to JBase
 */
public class JBase {
	
	// Max capacity of this database is 2^30 (Assuming sufficient memory) - Since the MAX_CAPACITY of HashMap's Entry table is "1<<30" as of Java 6.
	private Map<String,String> kvstore = null;

	private static JBase s_instance = null;
	private JBase(){
		 kvstore = new HashMap<String,String>();
	}
	
	public static JBase getInstance(){
		if(s_instance==null){
			s_instance = new JBase();
		}
		return s_instance;
	}
	
	public Map<String, String> getKvstore() {
		return kvstore;
	}
	
	public void parse(String line){
		String[] input = line.split(" ");
		Commands command = Commands.getCommand(input[0]);			
		switch(command){
		
		case SET:
			if(input.length<3){
				System.err.println("USAGE : SET [name] [value]\n");
				break;
			}
			TransactionManager.getInstance().writeToTrans(input[1], input[2]);
			break;
			
		case GET:
			if(input.length<2){
				System.err.println("USAGE : GET [name]\n");
				break;
			}
			System.out.println(TransactionManager.getInstance().getValue(input[1]));
			break;
			
		case UNSET:
			if(input.length<2){
				System.err.println("USAGE : UNSET [name]\n");
				break;
			}
			if(TransactionManager.getInstance().getLastTransaction()==null)
				kvstore.remove(input[1]);
			else
				TransactionManager.getInstance().writeToTrans(input[1], null);
			break;
			
		// This particular operation is O(N) -> iterating over entry sets. 
		// Can be optimized to O(log(N)) if writes are made in sorted order of values. 
		// So writes will also become O(log(N)) after that optimization (Eg: Writing to a Heap)
		case NUMEQUALTO:
			if(input.length<2){
				System.err.println("USAGE : NUMEQUALTO [value]\n");
				break;
			}
			long count = 0;
			// Memory Optimization when there is no ongoing transaction
			if(TransactionManager.getInstance().getLastTransaction()==null){
				for(String value : kvstore.values()){
					if(value.equals(input[1])){
						count++;
					}
				}
			}
			else {
				Map<String,String> temp = TransactionManager.getInstance().getLastTransaction().getTransValues();
				for(Entry<String,String> entry : temp.entrySet()){
					if(entry.getValue().equals(input[1])){
						count++; // TODO : Edge case counting nulls will not work. None -> null translation required.
					}
				}
				for(Entry<String,String> entry: kvstore.entrySet()){
					if(entry.getValue().equals(input[1]) && !temp.keySet().contains(entry.getKey())){
						count++;
					}
				}
			}
			
			System.out.println(count);
			break;
			
		case BEGIN:
			TransactionManager.getInstance().createTransaction();
			break;
			
		case ROLLBACK:
			TransactionManager.getInstance().rollbackTransaction();
			break;
			
		case COMMIT:
			TransactionManager.getInstance().commit();
			break;
			
		case END:
			System.exit(0);
			
		default :
			System.out.println("Please Enter a valid command.");	
		}
	}
	

}
