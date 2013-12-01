import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * Singleton that keeps track of ongoing transactions
 * @author narendran
 *
 */
public class TransactionManager {
	
	private Stack<Transaction> transactions = null;
	
	private static TransactionManager s_instance = null;
	private TransactionManager(){
		
	}
	
	public static TransactionManager getInstance(){
		if(s_instance==null){
			s_instance = new TransactionManager();
		}
		return s_instance;
	}
	
	public void rollbackTransaction(){
		if(transactions==null || transactions.empty())
			System.out.println("NO TRANSACTION"); 
		else 
			transactions.pop(); // Thats it! 
	}
	
	public void commit(){
		Map<String,String> temp = this.getLastTransaction().getTransValues();
		for(Entry<String,String> entry : temp.entrySet()){
			if(entry.getValue().equals("None")){
				entry.setValue(null);
			}
		}
		JBase.getInstance().getKvstore().putAll(temp); // Merged Transaction store to Master store
		transactions = null;
	}
	
	public void createTransaction(){
		if(transactions==null){
			transactions = new Stack<Transaction>();
			transactions.push(new Transaction(null)); // First level, no context to be passed.
		}
		// Get context from previous transaction and pass it to the new one.
		transactions.push(new Transaction(this.getLastTransaction().getTransValues()));
	}
	
	public Transaction getLastTransaction(){
		if(transactions==null || transactions.empty()){
			return null;
		}
		return transactions.peek();
	}
	
	/**
	 * To differentiate between non-existent values and values whose value is NULL, we internall map value "NULL" to "None"
	 * @param key
	 * @param value
	 */
	public void writeToTrans(String key, String value){
		if(value==null) // Edge case
			value="None";
		if(this.getLastTransaction()!=null)
			this.getLastTransaction().getTransValues().put(key, value);
		else // No ongoing transactions - writing directly to KVStore
			JBase.getInstance().getKvstore().put(key, value);
	}
	
	/**
	 * To differentiate between non-existent values and values whose value is NULL, we internall map value "NULL" to "None"
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		if(this.getLastTransaction()!=null){
			if(this.getLastTransaction().getTransValues().get(key)==null)
				return JBase.getInstance().getKvstore().get(key); // return value from original KVStore
			else if(this.getLastTransaction().getTransValues().get(key).equals("None"))
					return null; // The value has been set to null
			else return this.getLastTransaction().getTransValues().get(key);
		} 
		return JBase.getInstance().getKvstore().get(key);	
	}
}
