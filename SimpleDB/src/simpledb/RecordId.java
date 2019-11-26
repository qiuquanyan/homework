package simpledb;

/**
 * A RecordId is a reference to a specific tuple on a specific page of a
 * specific table.
 */
public class RecordId {

    private PageId page_id;
    private int tuple_num;
	/** Creates a new RecordId refering to the specified PageId and tuple number.
     * @param pid the pageid of the page on which the tuple resides
     * @param tupleno the tuple number within the page.
     */
    public RecordId(PageId pid, int tupleno) {
        this.page_id=pid;
        this.tuple_num=tupleno;
    	// some code goes here
    }

    /**
     * @return the tuple number this RecordId references.
     */
    public int tupleno() {
        // some code goes here
        return this.tuple_num;
    }

    /**
     * @return the page id this RecordId references.
     */
    public PageId getPageId() {
        // some code goes here
        return this.page_id;
    }
    
    /**
     * Two RecordId objects are considered equal if they represent the same tuple.
     * @return True if this and o represent the same tuple
     */
    @Override
    public boolean equals(Object o) {
    	// some code goes here
    	if (this == o) {
    		return true;
    	}
    	if(o instanceof RecordId) {
    		RecordId tmp=(RecordId) o;
    		if(tmp.page_id.equals(this.page_id)&&(tmp.tuple_num==this.tuple_num)) {
    			return true;
    		}
    	}
    	 return false;
    	//throw new UnsupportedOperationException("implement this");
    }
    
    /**
     * You should implement the hashCode() so that two equal RecordId instances
     * (with respect to equals()) have the same hashCode().
     * @return An int that is the same for equal RecordId objects.
     */
    @Override
    public int hashCode() {
    	// some code goes here
    	int id_hash=this.page_id.hashCode();
    	id_hash+=this.tuple_num;
    	String hashnum=String.valueOf(id_hash);
    	return hashnum.hashCode();
    	//throw new UnsupportedOperationException("implement this");
    	
    }
    
}
