package app.datamodel.pojos.enums;

public enum StorablePojoState
{
	INIT,		/* The MongoDB driver has created the POJO class and it is filling its fields */
	IGNORED,	/* Ignored (detached) POJO. Modifications will not be registered and saved */
	UNTRACKED,	/* Newly created POJO, still not saved */
	STAGED,		/* POJO contains some unsaved modifications */
	COMMITTED	/* POJO is up-to-date with the document in the db */
}
