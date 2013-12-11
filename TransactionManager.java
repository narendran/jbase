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
		// TODO : Handle NULL
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
		}
		// Get context from previous transaction and pass it to the new one.
		//System.out.println("Is there a previous trasn : "+this.getLastTransaction()==null);
		if(this.getLastTransaction()!=null){
			//System.out.println("*** Creating new transaction ***");
			transactions.push(new Transaction(this.getLastTransaction().getTransValues()));
		}
		else
			transactions.push(new Transaction(null));
	}
	
	public Transaction getLastTransaction(){
		// System.out.println("Number of transactions : "+transactions.size());
		Transaction trans = null;
		if(transactions==null || transactions.empty()){
			return null;
		}
		if(transactions.size()!=0){
			trans = transactions.peek();
		}
		return trans;
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
			if(this.getLastTransaction().getTransValues()==null || this.getLastTransaction().getTransValues().get(key)==null)
				return JBase.getInstance().getKvstore().get(key); // return value from original KVStore
			else if(this.getLastTransaction().getTransValues().get(key).equals("None"))
					return null; // The value has been set to null
			else return this.getLastTransaction().getTransValues().get(key);
		} 
		return JBase.getInstance().getKvstore().get(key);	
	}
}
