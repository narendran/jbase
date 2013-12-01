/*
 * Enum containing list of possible commands
 * Add more commands as requirements change
 */
public enum Commands {
	SET("SET"),
	GET("GET"),
	UNSET("UNSET"),
	NUMEQUALTO("NUMEQUALTO"),
	END("END"),
	BEGIN("BEGIN"),
	ROLLBACK("ROLLBACK"),
	COMMIT("COMMIT"),
	NULL("NULL");
	
	private String value;
	
	Commands(String value){
		this.setValue(value);
	}
	
	public static Commands getCommand(String input){
		for(Commands command : Commands.values()){
			if(command.getValue().equals(input))
				return command;
		}
		return Commands.NULL;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
