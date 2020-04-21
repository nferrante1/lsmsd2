package app.common.net.entities;

public class StrategyInfo extends Entity {

	private static final long serialVersionUID = 8337583857656650614L;
	
	protected String name;

	protected String author;
	protected boolean canDelete;
	
	public StrategyInfo(String name) {
		
		this(name, null, false);
	}
	
	public StrategyInfo(String name, String author, boolean canDelete) {
		
		this.name = name;
		this.author = author;
		this.canDelete = canDelete;
	}
	
	public String getName() {
		return name;
	}
	public String getAuthor() {
		return author;
	}
	public boolean isCanDelete() {
		return canDelete;
	}

}
