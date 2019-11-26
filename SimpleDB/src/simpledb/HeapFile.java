package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection
 * of tuples in no particular order.  Tuples are stored on pages, each of
 * which is a fixed size, and the file is simply a collection of those
 * pages. HeapFile works closely with HeapPage.  The format of HeapPages
 * is described in the HeapPage constructor.
 *
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f the file that stores the on-disk backing store for this heap file.
     */
	private File file;
	private TupleDesc tuple_desc;
	
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
    	this.file = f;
    	this.tuple_desc=td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.file;
    }

    /**
    * Returns an ID uniquely identifying this HeapFile. Implementation note:
    * you will need to generate this tableid somewhere ensure that each
    * HeapFile has a "unique id," and that you always return the same value
    * for a particular HeapFile. We suggest hashing the absolute file name of
    * the file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
    *
    * @return an ID uniquely identifying this HeapFile.
    */
    public int getId() {
        // some code goes here
    	int id=this.file.getAbsoluteFile().hashCode();
    	return id;
        //throw new UnsupportedOperationException("implement this");
    }
    
    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
    	// some code goes here
    	return this.tuple_desc;
    	//throw new UnsupportedOperationException("implement this");
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
    	byte[] Page_data=new byte[BufferPool.PAGE_SIZE];
    	int pos_data=pid.pageno()*BufferPool.PAGE_SIZE;
    	try {
			FileInputStream file_data=new FileInputStream(this.file);
			file_data.skip(pos_data);
			file_data.read(Page_data);
			return new HeapPage(new HeapPageId(pid.getTableId(),pid.pageno()),Page_data);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException();
		}
    	
        //return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    	PageId pid =page.getId();
    	int page_num=pid.pageno();
    	int pageSize=BufferPool.PAGE_SIZE;
    	byte[] page_data=page.getPageData();
    	RandomAccessFile dbfile=new RandomAccessFile(this.file,"rws");
    	dbfile.skipBytes(page_num*pageSize);
    	dbfile.write(page_data);
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
    	int nums=(int)this.file.length()/BufferPool.PAGE_SIZE;
        return nums;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for lab1
    	ArrayList<Page> aff=new ArrayList<>(1);
    	int Page_num=this.numPages();
    	for(int pag_n=0;pag_n<Page_num+1;pag_n++) {
    		HeapPageId p_id=new HeapPageId(getId(),pag_n);
    		HeapPage page;
    		if(pag_n<Page_num) {
    			page=(HeapPage) Database.getBufferPool().getPage(tid, p_id, Permissions.READ_WRITE);
    		}
    		else {
    			page=new HeapPage(p_id,HeapPage.createEmptyPageData(t.getRecordId().getPageId().getTableId()));
    		}
    		if(page.getNumEmptySlots()>0) {
    			page.addTuple(t);
    			if(pag_n<Page_num) {
    				aff.add(page);
    			}
    			else {
    				this.writePage(page);
    			}
    			return aff;
    		}
    	}
    	throw new DbException("can't add");
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for lab1
    	RecordId rid=t.getRecordId();
    	HeapPageId pid=(HeapPageId) rid.getPageId();
    	if(pid.getTableId() == getId()) {
    		//int page_num=pid.pageno();
    		HeapPage page=(HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
    		page.deleteTuple(t);
    		return page;
    	}
    	throw new DbException("can't find");
    }
    
    class File_Iterator implements DbFileIterator{
    	
    	private TransactionId tid;
    	private int table_id;
    	private int page_num;
    	private Iterator<Tuple> tuples;
    	private int data_pos;
    	
    	public File_Iterator(TransactionId id) {
    		this.tid=id;
    		this.table_id=getId();
    		this.page_num=numPages();
    		this.tuples=null;
    		this.data_pos=-1;
    	}
    	
    	public Iterator<Tuple> getTuple(HeapPageId p_id) throws TransactionAbortedException, DbException{
    		HeapPage new_one=(HeapPage) Database.getBufferPool().getPage(this.tid, p_id, Permissions.READ_ONLY);
    		return new_one.iterator();
    	}

		@Override
		public void open() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			this.data_pos=0;
			PageId page_id=new HeapPageId(table_id,data_pos);
			tuples=this.getTuple((HeapPageId)page_id);
		}

		@Override
		public boolean hasNext() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			if(data_pos==-1) {
				return false;
			}
			while(data_pos<page_num-1) {
				if(this.tuples.hasNext()) {
					return true;
				}
				else {
					data_pos+=1;
					tuples=this.getTuple(new HeapPageId(table_id,data_pos));
				}
			}
			return this.tuples.hasNext();
		}

		@Override
		public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
			// TODO Auto-generated method stub
			if (this.hasNext()) {
				return tuples.next();
			}
			else
				throw new NoSuchElementException();
		}

		@Override
		public void rewind() throws DbException, TransactionAbortedException {
			// TODO Auto-generated method stub
			this.open();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			this.data_pos=-1;
			this.tuples=null;
		}
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new File_Iterator(tid);
    }
    
}

