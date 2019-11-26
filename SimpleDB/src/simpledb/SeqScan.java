package simpledb;
import java.util.*;

import simpledb.TupleDesc.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private TransactionId Tid;
    private int table_id;
    private String table_Alias;
    private DbFileIterator tuple_Iter;
	/**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid The transaction this scan is running as a part of.
     * @param tableid the table to scan.
     * @param tableAlias the alias of this table (needed by the parser);
     *         the returned tupleDesc should have fields with name tableAlias.fieldName
     *         (note: this class is not responsible for handling a case where tableAlias
     *         or fieldName are null.  It shouldn't crash if they are, but the resulting
     *         name can be null.fieldName, tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
    	this.Tid=tid;
    	this.table_id=tableid;
    	this.table_Alias=tableAlias;
    	tuple_Iter=Database.getCatalog().getDbFile(this.table_id).iterator(this.Tid);
    }

    public void open()
        throws DbException, TransactionAbortedException {
        // some code goes here
    	tuple_Iter.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor.
     * @return the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
    	TupleDesc tuple_desc=Database.getCatalog().getTupleDesc(this.table_id);
    	int fields_num=tuple_desc.numFields();
    	Type[] field_type=new Type[fields_num];
    	String[] field_string= new String[fields_num];
    	//field_Thing[] field_things=new field_Thing[fields_num];
    	for (int i=0;i<fields_num;i++) {
    		field_type[i]=tuple_desc.getType(i);
    		String field_name=tuple_desc.getFieldName(i);
    		field_string[i]=this.table_Alias+field_name;
    	}
        return new TupleDesc(field_type,field_string);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
        return this.tuple_Iter.hasNext();
    }

    public Tuple next()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        // some code goes here
        return this.tuple_Iter.next();
    }

    public void close() {
        // some code goes here
    	this.tuple_Iter.close();
    }

    public void rewind()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        // some code goes here
    	this.tuple_Iter.rewind();
    }
}
