package simpledb;

/**
 * JoinPredicate compares fields of two tuples using a predicate.
 * JoinPredicate is most likely used by the Join operator.
 */
public class JoinPredicate {

    private int num1;
    private int num2;
    private Predicate.Op op_;
	/**
     * Constructor -- create a new predicate over two fields of two tuples.
     *
     * @param field1 The field index into the first tuple in the predicate
     * @param field2 The field index into the second tuple in the predicate
     * @param op The operation to apply (as defined in Predicate.Op); either
     *   Predicate.Op.GREATER_THAN, Predicate.Op.LESS_THAN, Predicate.Op.EQUAL,
     *   Predicate.Op.GREATER_THAN_OR_EQ, or Predicate.Op.LESS_THAN_OR_EQ
     * @see Predicate
     */
    public JoinPredicate(int field1, Predicate.Op op, int field2) {
        // some code goes here
    	this.num1=field1;
    	this.num2=field2;
    	this.op_=op;
    }
    public Predicate.Op getop(){
    	return this.op_;
    }
    /**
     * Apply the predicate to the two specified tuples.
     * The comparison can be made through Field's compare method.
     * @return true if the tuples satisfy the predicate.
     */
    public boolean filter(Tuple t1, Tuple t2) {
        // some code goes here
    	boolean Y=t1.getField(this.num1).compare(this.op_, t2.getField(this.num2));
        return Y;
    }
    public int getField1()
    {
        // some code goes here
        return num1;
    }
    
    public int getField2()
    {
        // some code goes here
        return num2;
    }
}
