import java.util.HashMap;
import java.util.Map;

public class Transaction {
	
	// This map will store only values
	Map<String,String> transValues = null;

	public Transaction(Map<String,String> prevTransValues){
		if(prevTransValues==null)
			transValues = new HashMap<String,String>();
		else 
			transValues = new HashMap<String,String>(prevTransValues); // Unless cloned, a change made to transaction will be reflected in all previous transactions. 
	}
	
	public Map<String, String> getTransValues() {
		return transValues;
	}

	public void setTransValues(Map<String, String> transValues) {
		this.transValues = transValues;
	}
	
	

}
