package db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDBConfig {
	
	private static MongoClient mongoClient;
	private static MongoDatabase database;
	
	public MongoDBConfig() {
		if (mongoClient == null) {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            database = mongoClient.getDatabase("StarWarsDatabase");            
        }
	}
	
	public MongoCollection<BasicDBObject> getCollection(String collection) {
		return database.getCollection(collection, BasicDBObject.class);
	}
	
	public boolean checkIfCollectionExist(String collectionName) {
		MongoIterable<String> list = database.listCollectionNames();
		for (String name : list) {
			if(name.equals(collectionName)) {
				System.out.println("sim");
				return true;
			}
		}
		System.out.println("nao");
		return false;
	}
	
	public MongoDatabase getMongoDatabase() {
		return database;
	}
	
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	
	
	
}
