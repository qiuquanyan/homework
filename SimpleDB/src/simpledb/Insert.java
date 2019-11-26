package simpledb;
import java.io.IOException;
import java.util.*;

/**
 * Inserts tuples read from the child operator into
 * the tableid specified in the constructor
 */
public class Insert extends AbstractDbIterator {

    private TransactionId tid;
    private DbIterator Child;
    private int Tableid;
    private boolean it;
    private int count;
	/**
     * Constructor.
     * @param t The transaction running the insert.
     * @param child The child operator from which to read tuples to be inserted.
     * @param tableid The table in which to insert tuples.
     * @throws DbException if TupleDesc of child differs from table into which we are to insert.
     */
    public Insert(TransactionId t, DbIterator child, int tableid)
        throws DbException {
        // some code goes here
    	this.tid=t;
    	this.Child=child;
    	this.Tableid=tableid;
    	this.count=0;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return new TupleDesc(new Type[] {Type.INT_TYPE},new String[] {null});
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	it=false;
    	this.Child.open();
    	super.open();
    	while(this.Child.hasNext()) {
    		Tuple tup=Child.next();
    		try {
				Database.getBufferPool().insertTuple(tid, Tableid, tup);
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public void close() {
        // some code goes here
    	super.close();
    	Child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	this.it=false;
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool.
     * An instances of BufferPool is available via Database.getBufferPool().
     * Note that insert DOES NOT need check to see if a particular tuple is
     * a duplicate before inserting it.
     *
     * @return A 1-field tuple containing the number of inserted records, or
    * null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple readNext()
            throws TransactionAbortedException, DbException {
        // some code goes here
    	if(it==true) {
    		return null;
    	}
    	it=true;
    	Tuple inset_count=new Tuple(getTupleDesc());
    	inset_count.setField(0, new IntField(count));
    	return inset_count;
    }
}
