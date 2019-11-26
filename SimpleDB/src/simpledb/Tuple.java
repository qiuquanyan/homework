package simpledb;

import java.util.ArrayList;

/**
 * Tuple maintains information about the contents of a tuple.
 * Tuples have a specified schema specified by a TupleDesc object and contain
 * Field objects with the data for each field.
 */
public class Tuple {

    private TupleDesc tupledesc;
    private ArrayList<Field> field;
    private RecordId record_id;
	/**
     * Create a new tuple with the specified schema (type).
     *
     * @param td the schema of this tuple. It must be a valid TupleDesc
     * instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here
    	this.tupledesc=td;
    	field=new ArrayList<Field>(td.numFields());
    	for(int i=0;i<td.numFields();i++) {
    		this.field.add(null);
    	}
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.tupledesc;
    }

    /**
     * @return The RecordId representing the location of this tuple on
     *   disk. May be null.
     */
    public RecordId getRecordId() {
        // some code goes here
    	return this.record_id;
    }

    /**
     * Set the RecordId information for this tuple.
     * @param rid the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here
    	this.record_id=rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i index of the field to change. It must be a valid index.
     * @param f new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here
    	this.field.set(i,f);
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
        if(i<=this.tupledesc.numFields())
        	return this.field.get(i);
        else
        	return null;
    }

    /**
     * Returns the contents of this Tuple as a string.
     * Note that to pass the system tests, the format needs to be as
     * follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     *
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here
    	String cont=String.valueOf(this.field.get(0));
    	for(int i=1;i<this.tupledesc.numFields();i++) {
    		cont=cont+"\t"+String.valueOf(this.field.get(i));
    	}
    	cont=cont+"\n";
    	return cont;
        //throw new UnsupportedOperationException("Implement this");
    }
}
