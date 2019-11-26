package simpledb;

/**
 * The delete operator.  Delete reads tuples from its child operator and
 * removes them from the table they belong to.
 */
public class Delete extends AbstractDbIterator {

    private TransactionId tid;
    private DbIterator Child;
    private boolean it;
    private int count;
	/**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * @param t The transaction this delete runs in
     * @param child The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
    	this.tid=t;
    	this.Child=child;
    	this.count=0;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return new TupleDesc(new Type[] {Type.INT_TYPE},new String[] {null});
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	this.Child.open();
    	super.open();
    	this.it=false;
    	while(this.Child.hasNext()) {
    		Tuple tup=this.Child.next();
    		Database.getBufferPool().deleteTuple(tid, tup);
    		count++;
    	}
    	
    }

    public void close() {
        // some code goes here
    	super.close();
    	this.Child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	it=false;
    	
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be access via the
     * Database.getBufferPool() method.
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	if(it==true)
    		return null;
    	it=true;
    	Tuple del_tup=new Tuple(getTupleDesc());
    	del_tup.setField(0, new IntField(count));
        return del_tup;
    }
}
