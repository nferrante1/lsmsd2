package app.scraper.datamodel.mongo;

public abstract class NestedDataObject extends DataObject
{
	private DataObject container;

	protected DataObject getContainer()
	{
		return container;
	}
	
	public void setContainer(DataObject container)
	{
		this.container = container;
	}
}
