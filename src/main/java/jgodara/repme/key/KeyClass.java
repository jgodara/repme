package jgodara.repme.key;

import java.sql.SQLException;

public class KeyClass {
	
	public static final int haveKey = 3;
	public static final int needNewKey = 4;
	public static final int needNewKeyBeforeNext = 5;
	public static final int getKey = 2;
	public static final int setKey = 1;
	/**
	 * table name
	 */
	public String table;
	
	/**
	 * how many keys will be generated once, batch size
	 */
	public int count = 0;
	
	private int precount = 0;
	
	/**
	 * key value
	 */
	private int key = 0;
	
	private int prekey = 0;
	
	/**
	 * if the  key/count*100>level, more key will be generated from database. high level ratio
	 */
	public int level = 50;
	
	/**
	 * determine if it is in the process of generating the new key
	 */
	private boolean inProcess = false;
	
	/**
	 * the real key will be basekey+key
	 */
	public long basekey = 0;
	
	private long prebasekey = 0;
	
	/**
	 * the query used to select max id value of the table
	 */
	private String selectMaxQuery;
	
	public KeyClass(String table)
	{
		this.table = table;
	}
	
	public KeyClass(String table, int batchsize, int highLevelRatio, String selectMaxQuery)
	{
		this.table = table;
		this.count = batchsize;
		this.level = highLevelRatio;
		this.selectMaxQuery = selectMaxQuery;
	}
	
	public synchronized long nextval() throws Exception
	{
		Thread thread = null;
		/*
		 * determine if without cache (count==0) or did not sync key info from database(baskey==0)
		 * generate key directly 
		 */

		if (operateKey(0, 0, 0, needNewKeyBeforeNext)>0)
		{
			KeyGeneratorThread kgt= new KeyGeneratorThread(this);
			kgt.generateKey();
		}
		/*
		 * if key resouce is short, start thread to get it.
		 */
		if (operateKey(0, 0, 0, needNewKey)>0)
		{
			KeyGeneratorThread kgt= new KeyGeneratorThread(this);
			thread = new Thread(kgt);
			thread.start();
		}
		
		/*
		 * waiting 1 sec for the thread of requirement of key info
		 */
		int i=10;
		while(operateKey(0, 0, 0, haveKey)<0&&i<10000)
		{
			i=i*10;
			try {
				wait(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		if (i>=10000&&key>=count)
		{
			if (thread==null || !thread.isAlive())	inProcess = false;
			throw new SQLException("key generator error!"+i);
		}

		return operateKey(0, 0, 0, getKey);
	}

	public String getSelectMaxQuery() {
		return selectMaxQuery;
	}

	public void setSelectMaxQuery(String selectMaxQuery) {
		this.selectMaxQuery = selectMaxQuery;
	}

	/*
	 * 
	 */
	public synchronized long operateKey(int count1, int level1, long basekey1, int action) throws SQLException
	{
		switch(action)
		{
		case setKey: // set key;
			prekey = key;
			precount = count;
			prebasekey = basekey;

			this.key = 0;
			this.count = count1;
			this.basekey = basekey1;
			this.level = level1;
			inProcess = false;
			return 0;
		case getKey: // get key
			if (count==0)
				return basekey+1;
			else if (prekey<precount&&prebasekey!=0)
			{
				this.prekey++;
				return this.prebasekey+(this.prekey);
			}
			else if(key<count)
			{
				this.key++;
				return this.basekey+(this.key);
			}
			else
				throw new SQLException("key generator error!"+basekey+":"+key);
		case haveKey: // check key determine if it have key
			if (count==0) 
				return 1;
			else if (this.key>=this.count)
			{
				if (inProcess)
					return -1;
				else
					return -2;
			}
			else
				return 1;
		case needNewKey: // check key if need generate new key
			if (count==0) 
				return -1;
			else if (count==1)
			{
				if (inProcess)
					return -1;   
				else   
				{//create thread
					inProcess = true;
					return 1; 
				}
			}
			else if ((key*100/count)>=level)
			{
				if (inProcess)
					return -1;   
				else   
				{//create thread
					inProcess = true;
					return 1; 
				}
			}
			else
				return -1; // dont need
		case needNewKeyBeforeNext:
			if (!inProcess && (count==0 || basekey==0 || count<=key))
			{
				inProcess = true;
				return 1;
			}
			else
				return -1;
		}
		
		return 0;
	}

	public boolean isInProcess() {
		return inProcess;
	}

	public void setInProcess(boolean inProcess) {
		this.inProcess = inProcess;
	}

}
