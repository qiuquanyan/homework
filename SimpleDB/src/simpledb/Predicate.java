package simpledb;

/** Predicate compares tuples to a specified Field value.
 */
public class Predicate {

    private int field_num;
    private Op op_;
    private Field field_op;
	/** Constants used for return codes in Field.compare */
    public enum Op {
        EQUALS, GREATER_THAN, LESS_THAN, LESS_THAN_OR_EQ, GREATER_THAN_OR_EQ, LIKE;

        /**
         * Interface to access operations by a string containing an integer
         * index for command-line convenience.
         *
         * @param s a string containing a valid integer Op index
         */
        public static Op getOp(String s) {
            return getOp(Integer.parseInt(s));
        }

        /**
         * Interface to access operations by integer value for command-line
         * convenience.
         *
         * @param i a valid integer Op index
         */
        public static Op getOp(int i) {
            return values()[i];
        }
    }

    /**
     * Constructor.
     *
     * @param field field number of passed in tuples to compare against.
     * @param op operation to use for comparison
     * @param operand field value to compare passed in tuples to
     */
    public Predicate(int field, Op op, Field operand) {
        this.field_num=field;
        this.field_op=operand;
        this.op_=op;
    	// some code goes here
    }

    /**
     * Compares the field number of t specified in the constructor to the
     * operand field specified in the constructor using the operator specific
     * in the constructor.  The comparison can be made through Field's
     * compare method.
     *
     * @param t The tuple to compare against
     * @return true if the comparison is true, false otherwise.
     */
    public boolean filter(Tuple t) {
        // some code goes here
    	//boolean Y1=this.field_op.compare(this.op_, t.getField(field_num));
    	boolean Y=t.getField(this.field_num).compare(this.op_, this.field_op);
        return Y;
    }

    /**
     * Returns something useful, like
     * "f = field_id op = op_string operand = operand_string
     */
    public String toString() {
        // some code goes here
    	StringBuffer strings=new StringBuffer();
    	strings.append("f = ").append(this.field_num).append(" op = ").append(this.op_).append(" operand = ")
    	.append(this.field_op);
        return strings.toString();
    }
}
