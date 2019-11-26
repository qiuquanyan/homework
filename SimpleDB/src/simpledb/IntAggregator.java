package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntAggregator implements Aggregator {

    private int Gbfield;
    private Type Gbfieldtype;
    private int Afield;
    private Op What;
    
    private Object agte;
	/**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what the aggregation operator
     */

    public IntAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	this.Gbfield=gbfield;
    	this.Gbfieldtype=gbfieldtype;
    	this.Afield=afield;
    	this.What=what;
    	if(this.Gbfield==Aggregator.NO_GROUPING) {
    		agte=(Object) new ArrayList<Integer>();
    	}
    	else {
    		assert this.Gbfieldtype!=null;
    		if(this.Gbfieldtype==Type.INT_TYPE) {
    			agte=(Object) new HashMap<Integer,ArrayList<Integer>>();
    			
    		}
    		else
    			agte=(Object) new HashMap<String,ArrayList<Integer>>();

    	}
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void merge(Tuple tup) {
        // some code goes here
    	if(this.Gbfield==Aggregator.NO_GROUPING) {
    		((ArrayList<Integer>) agte).add(((IntField)tup.getField(Afield)).getValue());
    	}
    	else {
    		assert this.Gbfieldtype!=null;
    		if(this.Gbfieldtype==Type.INT_TYPE) {
       			HashMap<Integer,ArrayList<Integer>> groupagte=(HashMap<Integer,ArrayList<Integer>>) agte;
    			Integer keys=((IntField)tup.getField(Gbfield)).getValue();
    			Integer values=((IntField)tup.getField(Afield)).getValue();
    			if(!groupagte.containsKey(keys)) {
    				groupagte.put(keys, new ArrayList<>(1));
    			}
    			groupagte.get(keys).add(values);
    		}
    		else {
    			HashMap<String,ArrayList<Integer>> groupagte=(HashMap<String,ArrayList<Integer>>) agte;
    			String gkeys=((StringField) tup.getField(Gbfield)).getValue();
    			Integer avalue=((IntField) tup.getField(Afield)).getValue();
    			if(!groupagte.containsKey(gkeys)) {
    				groupagte.put(gkeys, new ArrayList<>(1));
    			}
    			groupagte.get(gkeys).add(avalue);
    		}
    	}
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        return new IntAggregateIterator();
    	//throw new UnsupportedOperationException("implement me");
    }
    
    private class IntAggregateIterator implements DbIterator{
    	
    	private ArrayList<Tuple> tuples;
    	private Iterator<Tuple> it;
		
    	public int cal_Agtetuple(ArrayList<Integer> s) {
    		assert !s.isEmpty();
    		int cur=0;
    		switch(What) {
    		case MIN:
    			cur=s.get(0);
    			for(int i=1;i<s.size();i++ ) {
    				if(cur>s.get(i)) {
    					cur=s.get(i);
    				}
    			}
    			break;
    		case MAX:
    			cur=s.get(0);
    			for(int i=1;i<s.size();i++ ) {
    				if(cur<s.get(i)) {
    					cur=s.get(i);
    				}
    			}
    			break;
	    	case SUM:
				cur=s.get(0);
				for(int i=1;i<s.size();i++ ) {
					cur+=s.get(i);
				}
				break;
	    	case AVG:
    			cur=s.get(0);
    			for(int i=1;i<s.size();i++ ) {
    				cur+=s.get(i);
    			}
    			cur=cur/s.size();
    			break;
	    	case COUNT:
	    		cur=s.size();
	    		break;
	    	default:
	    		break;
    		}
    		return cur;
    	}
    	
    	public IntAggregateIterator() {
    		tuples=new ArrayList<Tuple>();
    		if(Gbfield==Aggregator.NO_GROUPING) {
    			Tuple tup=new Tuple(getTupleDesc());
    			Field agte_value=new IntField(this.cal_Agtetuple((ArrayList<Integer>)agte));
    			tup.setField(0, agte_value);
    			tuples.add(tup);
    		}
    		else {
    			Iterator iter =((HashMap<Integer,ArrayList<Integer>>) agte).entrySet().iterator();
    			while(iter.hasNext()) {
    				Map.Entry entry =(Map.Entry) iter.next();
    				Tuple tup=new Tuple(getTupleDesc());
    				Field group_value=null;
    				if(Gbfieldtype==Type.INT_TYPE) {
    					group_value=new IntField((int) entry.getKey());
    				}
    				else {
    					String str =(String) entry.getKey();
    					group_value=new StringField(str,str.length());
    				}
    				Field agte_value=new IntField(this.cal_Agtetuple((ArrayList<Integer>) entry.getValue()));
    				tup.setField(0, group_value);
    				tup.setField(1, agte_value);
    				tuples.add(tup);
    			}
    		}
    	}
    	@Override
		public void open() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			it=tuples.iterator();
		}

		@Override
		public boolean hasNext() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			if(it==null) {
				throw new IllegalStateException("it's null");
			}
			return it.hasNext();
		}

		@Override
		public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
			// TODO Auto-generated method stub
			if(it==null) {
				throw new IllegalStateException("it's null");
			}
			return it.next();
		}

		@Override
		public void rewind() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub

			it=tuples.iterator();
		}

		@Override
		public TupleDesc getTupleDesc() {
			// TODO Auto-generated method stub
			if(Gbfield==Aggregator.NO_GROUPING) {
				return new TupleDesc(new Type[] {Type.INT_TYPE});
			}
			else {
				return new TupleDesc(new Type[] {Gbfieldtype,Type.INT_TYPE});
			}
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			it=null;
		}
    	
    }

}
