package simpledb;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {

    private int fields_num;
    private field_Thing[] field_things;
    public class field_Thing {
    	Type field_type;
    	String field_string;
    	
    	public field_Thing(Type type,String strings) {
    		this.field_type=type;
    		this.field_string=strings;
    	}
    }
	/**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields
     * fields, with the first td1.numFields coming from td1 and the remaining
     * from td2.
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc combine(TupleDesc td1, TupleDesc td2) {
        // some code goes here
    	int nums=td1.fields_num+td2.fields_num;
    	Type[] typeAr = new Type[nums];
    	String[] nameAr = new String[nums];
    	int num_td1=td1.fields_num;
    	for (int i=0;i<num_td1;i++) {
    		typeAr[i]=td1.field_things[i].field_type;
    		nameAr[i]=td1.field_things[i].field_string;
    	}
    	for (int i=num_td1;i<nums;i++) {
    		typeAr[i]=td2.field_things[i-num_td1].field_type;
    		nameAr[i]=td2.field_things[i-num_td1].field_string;
    	}
        return new TupleDesc(typeAr,nameAr);
    }

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        fields_num=typeAr.length;
        field_things = new field_Thing[fields_num];
        for(int i=0;i<fields_num;i++) {
        	field_things[i]= new field_Thing(typeAr[i],fieldAr[i]);
        }
    	// some code goes here
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
    	fields_num=typeAr.length;
        field_things = new field_Thing[fields_num];
        for(int i=0;i<fields_num;i++) {
        	field_things[i]= new field_Thing(typeAr[i],null);
        }
    	// some code goes here
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return fields_num;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
    	if(i<0 || i>=fields_num) {
    		throw new NoSuchElementException();
    	}
        return field_things[i].field_string;
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
        // some code goes here
    	if (name==null) {
    		throw new NoSuchElementException();
    	}
    	String temp;
    	for(int i=0;i<field_things.length;i++) {
    		temp=field_things[i].field_string;
    		if(!(temp==null)&&temp.equals(name)) {
    			return i;
    		}
    	}
        throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        // some code goes here
    	if(i<0 || i>=fields_num) {
    		throw new NoSuchElementException();
    	}
        return field_things[i].field_type;
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
    	int size_num=0;
    	for(int i=0;i<fields_num;i++) {
    		size_num+=field_things[i].field_type.getLen();
    	}
        return size_num;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
    	if (this == o) {
    		return true;
    	}
    	if (o instanceof TupleDesc) {
    	TupleDesc comp_one =(TupleDesc) o;
    	if(!(comp_one.fields_num==this.fields_num)) {
    		return false;
    	}
    	for(int i=0;i<fields_num;i++) {
    		boolean string_eq=(comp_one.field_things[i].field_string==field_things[i].field_string)
    				|| (comp_one.field_things[i].field_string==null && field_things[i].field_string==null);
    		if(!((comp_one.field_things[i].field_type==field_things[i].field_type)&&string_eq)) {
    			return false;
    		}
    	}
        return true;
    	}
    	else return false;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
    	String buffer=field_things[0].field_type+"("+field_things[0].field_string+")";
    	for(int i=1;i<fields_num;i++) {
    		buffer=buffer.concat(",");
    		String tmp=field_things[i].field_type+"("+field_things[i].field_string+")";
    		buffer=buffer.concat(tmp);
    	}
        return buffer;
    }
}
