package simpledb;
import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */

public class Filter extends AbstractDbIterator {
    private Predicate pre;
    private DbIterator db_ite;
	//private boolean open=false;
    //private TupleDesc td;
    
    //private TupleIterator ans_fielter;
    /**
     * Constructor accepts a predicate to apply and a child
     * operator to read tuples to filter from.
     *
     * @param p The predicate to filter tuples with
     * @param child The child operator
     */
    public Filter(Predicate p, DbIterator child) {
        // some code goes here
    	this.pre=p;
    	this.db_ite=child;
    }

    
    public TupleDesc getTupleDesc() {
        // some code goes here
    	
        return this.db_ite.getTupleDesc();
    }

    public void open()
        throws DbException, NoSuchElementException, TransactionAbortedException {

    	super.open();
    	this.db_ite.open();

    	// some code goes here
    }

    public void close() {
        // some code goes here
    	
    	this.db_ite.close();
    	super.close();
    }
    


    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	this.db_ite.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation.
     * Iterates over tuples from the child operator, applying the predicate
     * to them and returning those that pass the predicate (i.e. for which
     * the Predicate.filter() returns true.)
     *
     * @return The next tuple that passes the filter, or null if there are no more tuples
     * @see Predicate#filter
     */
    protected Tuple readNext()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        // some code goes here
    	
    	while(this.db_ite.hasNext()) {
    		Tuple next=this.db_ite.next();
    		if(this.pre.filter(next)) {
    			return next;
    		}
    	}
		return null;
    }
}
