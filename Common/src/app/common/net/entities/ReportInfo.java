package app.common.net.entities;

public class ReportInfo extends Entity {

	private static final long serialVersionUID = 7958042541743504580L;
	
	protected String Marketname;
	protected String start;
	protected String end;
	protected String author;
	protected boolean canDelete;
	
	public ReportInfo(String Marketname, String start, String end, String author, boolean canDelete) {
		this.Marketname = Marketname;
		this.start = start;
		this.end = end;
		this.author = author;
		this.canDelete = canDelete;
	}
	
	public String getMarketName() {
		return Marketname;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isCanDelete() {
		return canDelete;
	}
	
}
