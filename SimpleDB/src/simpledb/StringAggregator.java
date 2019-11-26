package simpledb;

import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import simpledb.Aggregator.Op;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

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
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	/*if(what!=Op.COUNT) {
    		throw new IllegalArgumentException("only support count");
    	}*/
    	this.Gbfield=gbfield;
    	this.Gbfieldtype=gbfieldtype;
    	this.Afield=afield;
    	this.What=what;
    	if(this.Gbfield==Aggregator.NO_GROUPING) {
    		agte=(Object) new Integer(0);
    	}
    	else {
    		assert this.Gbfieldtype!=null;
    		if(this.Gbfieldtype==Type.STRING_TYPE) {
    			agte=(Object) new HashMap<String,ArrayList<Integer>>();
    			
    		}
    		else
    			agte=(Object) new HashMap<Integer,ArrayList<Integer>>();

    	}
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void merge(Tuple tup) {
        // some code goes here
    	if(this.Gbfield==Aggregator.NO_GROUPING) {
    		agte=(((Integer) agte)+1);
    	}
    	else {
    		assert this.Gbfieldtype!=null;
    		if(this.Gbfieldtype==Type.STRING_TYPE) {
       			HashMap<String,Integer> groupagte=(HashMap<String,Integer>) agte;
    			String keys=((StringField)tup.getField(Gbfield)).getValue();
    			if(!groupagte.containsKey(keys)) {
    				groupagte.put(keys,1);
    			}
    			else {
    				groupagte.replace(keys, groupagte.get(keys)+1);
    			}
    		}
    		else {
    			HashMap<Integer,Integer> groupagte=(HashMap<Integer,Integer>) agte;
    			Integer keys=((IntField)tup.getField(Gbfield)).getValue();
    			//Integer values=((IntField)tup.getField(Afield)).getValue();
    			if(!groupagte.containsKey(keys)) {
    				groupagte.put(keys,1);
    			}
    			else {
    				groupagte.replace(keys, groupagte.get(keys)+1);
    			}
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
        return new StringAggretorIterator();
    	//throw new UnsupportedOperationException("implement me");
    }
    
    private class StringAggretorIterator implements DbIterator {

		private Iterator<Tuple> it;
		private ArrayList<Tuple> tuples;
    	/*
		public int calculate(ArrayList<Integer> s) {
			assert !s.isEmpty();
			int cur=0;
			switch(What) {
			case SUM:
				cur=s.get(0);
				for(int i=1;i<s.size();i++) {
					cur+=s.get(i);
				}
				break;
			case COUNT:
				cur=s.size();
				break;
			default:
				break;
			}
			return cur;
		}
		*/
		public StringAggretorIterator() {
			//assert What == Op.COUNT;
			this.it=null;
			tuples=new ArrayList<Tuple>();
			if(Gbfield == Aggregator.NO_GROUPING) {
				Tuple tup=new Tuple(getTupleDesc());
				Field values=new IntField((Integer) agte);
				tup.setField(0, values);
				tuples.add(tup);
			}
			else {
				Iterator  iter=((HashMap<Integer,ArrayList<Integer>> )agte).entrySet().iterator();
				
				while(iter.hasNext()) {
					Map.Entry entry=(Map.Entry)iter.next();
					Tuple tup=new Tuple(getTupleDesc());
					Field groupvalue=null;
					//Field agtevalue=null;
					if(Gbfieldtype==Type.STRING_TYPE) {
						String str=(String) entry.getKey();
						groupvalue=new StringField(str,str.length());
						//agtevalue=new IntField((Integer) entry.getValue());
					}
					else {
						groupvalue=new IntField((int) entry.getKey());
						//agtevalue=new IntField(this.calculate((ArrayList<Integer>) entry.getValue()));
					}
					Field agtevalue=new IntField((Integer) entry.getValue());
					//Field agtevalue=new IntField(this.calculate((ArrayList<Integer>) entry.getValue()));
					tup.setField(0, groupvalue);
					tup.setField(1, agtevalue);
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
