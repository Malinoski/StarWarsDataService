package test.java;

import org.junit.Assert;
import org.junit.Test;

import db.MongoDBConfig;

public class MongoDBConfigTest {

	@Test
	public void testNewMongoDBConfig() {
		
		try {
			MongoDBConfig mongoDBConfig = new MongoDBConfig();
			mongoDBConfig.getMongoClient().getAddress(); // This will wait for 30000 ms before timing out. 
			Assert.assertTrue(true);
		}catch(Exception ex) {
			Assert.assertTrue(false);
		}
	}

}
