package simpledb;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;

    public int maxpage_num;
    //public HashMap<PageId,Page> page_cache;
    
    private class pageBuffer{
    	private int nums;
    	private ConcurrentHashMap<PageId,Page> pagecache;
    	private ArrayList<Page> pagebuffer;
    	
    	public pageBuffer(int num) {
    		this.nums=num;
    		this.pagecache=new ConcurrentHashMap<>(num);
    		this.pagebuffer=new ArrayList<>(num);
    		
    	}
    	public Page put(PageId pid,Page page) {
    		if(pagebuffer.size()<this.nums) {
    			if(this.pagecache.containsKey(pid)) {
    				pagebuffer.remove(page);
    				pagebuffer.add(page);
    			}
    			else {
    				pagecache.put(pid, page);
    				pagebuffer.add(page);
    			}
    		}
    		return null;
    	}
    	public Page evictPage() throws DbException{
    		int size=pagebuffer.size();
    		if(size>0 &&size==nums) {
    			Page pg=pagebuffer.get(0);
    			return pg;
    		}
    		return null;
    	}
    	public int size() {
    		assert pagebuffer.size()==pagecache.size();
    		return pagebuffer.size();
    	}
    }
    
    private pageBuffer pagepool;
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here
    	this.maxpage_num=numPages;
    	this.pagepool = new pageBuffer(numPages);
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public synchronized Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
    	if(this.pagepool.pagecache.containsKey(pid)) {
    		return this.pagepool.pagecache.get(pid);
    	}
    	else {
    		Page new_page=Database.getCatalog().getDbFile(pid.getTableId()).readPage(pid);
    		if(pagepool.size()>=this.maxpage_num) {
    			this.evictPage();
    		}
    		pagepool.put(pid,new_page);
    		return new_page;
    	}
        //return null;
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public synchronized void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public synchronized void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public  synchronized boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public  synchronized void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to(Lock 
     * acquisition is not needed for lab2). May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public synchronized void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
    	DbFile db_file=Database.getCatalog().getDbFile(tableId);
    	ArrayList<Page> aff_pages=db_file.addTuple(tid, t);
    	for(Page page : aff_pages) {
    		page.markDirty(true, tid);
    		this.pagepool.put(page.getId(), page);
    	}
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit.  Does not need to update cached versions of any pages that have 
     * been dirtied, as it is not possible that a new page was created during the deletion
     * (note difference from addTuple).
     *
     * @param tid the transaction adding the tuple.
     * @param t the tuple to add
     */
    public synchronized void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
    	DbFile db_file=Database.getCatalog().getDbFile(t.getRecordId().getPageId().getTableId());
    	Page aff_page=db_file.deleteTuple(tid, t);
    	aff_page.markDirty(true, tid);
    	this.pagepool.put(aff_page.getId(),aff_page);
    	
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for lab1
    	Enumeration<PageId> it =  this.pagepool.pagecache.keys();
    	while(it.hasMoreElements()) {
    		this.flushPage(it.nextElement());
    	}

    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
        // only necessary for lab4
    	if(this.pagepool.pagecache.containsKey(pid)) {
    		Page pag=pagepool.pagecache.remove(pid);
    		pagepool.pagebuffer.remove(pag);
    	}
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private  synchronized void flushPage(PageId pid) throws IOException {
        // some code goes here
        // some code goes here
        // not necessary for lab1
    	if(this.pagepool.pagecache.containsKey(pid)) {
    		Page page=this.pagepool.pagecache.get(pid);
    		if(page.isDirty()!=null) {
    			Database.getCatalog().getDbFile(page.getId().getTableId()).writePage(page);
    		}
    	}
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private  synchronized void evictPage() throws DbException {
        // some code goes here
        // not necessary for lab1
    	Page pag=pagepool.evictPage();
    	PageId Pid=pag.getId();
    	if(pag!=null) {
    		try {
				flushPage(Pid);
				discardPage(Pid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }

}
