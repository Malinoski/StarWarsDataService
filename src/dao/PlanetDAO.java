package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import db.MongoDBConfig;
import entity.Planet;
import lib.SwapiHelper;

import static com.mongodb.client.model.Filters.*;

public class PlanetDAO {

	private MongoDBConfig mongoDBConfig;
	private static MongoCollection<BasicDBObject> collection;
	private static MongoCollection<BasicDBObject> collectionIndex;

	public PlanetDAO() {

		if (mongoDBConfig == null) {
			mongoDBConfig = new MongoDBConfig();
			collection = mongoDBConfig.getCollection("Planets");
			createPlanetIndex(mongoDBConfig);
		}

	}

	private void createPlanetIndex(MongoDBConfig mongoDBConfig) {
		collectionIndex = mongoDBConfig.getCollection("PlanetsIndex");
		try {
			BasicDBObject mongoDbObject = collectionIndex.find(eq("_id", "productid")).first();

			if (mongoDbObject == null) {
				// System.out.println("Create PlanetsIndex");
				mongoDbObject = new BasicDBObject("_id", "productid")
						.append("sequence_value", 0);
				collectionIndex.insertOne(mongoDbObject);

			} else {
				// System.out.println("DONT create PlanetsIndex");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getLastPlanetIndex() {
		BasicDBObject mongoDbObject = collectionIndex.find(eq("_id", "productid")).first();
		return (int) mongoDbObject.get("sequence_value");
	}

	public static boolean updateLastPlanetIndex(int nextPlanetIndex) {
		BasicDBObject mongoDbObject = new BasicDBObject("_id", "productid").append("sequence_value", nextPlanetIndex);
		UpdateResult result = collectionIndex.updateOne(eq("_id", "productid"), new Document("$set", mongoDbObject));

		if (result.getModifiedCount() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static int addPlanet(Planet planet) {

		// System.out.println("Adding planet: " + planet.getName());
		int lastPlanetIndex = getLastPlanetIndex();
		int nextPlanetIndex = lastPlanetIndex + 1;

		BasicDBObject mongoDbObject = new BasicDBObject("_id", nextPlanetIndex)
				.append("name", planet.getName())
				.append("climate", planet.getClimate())
				.append("terrain", planet.getTerrain())
				.append("countFilms", countFilmsFromPlanet(planet.getName()));

		try {
			collection.insertOne(mongoDbObject);
			if (updateLastPlanetIndex(nextPlanetIndex)) {

				return nextPlanetIndex;
			} else {
				return -1;
			}
		} catch (MongoWriteException ex) {
			// ex.printStackTrace();
			// result = "WARNING: DAO could not add an existing planet";
			return -1;
		}

	}
	
	public static int countFilmsFromPlanet(String planetName) {
		SwapiHelper helper = new SwapiHelper();
		return helper.getFilmsCountFromPlanet(planetName);
	}

	public List<Planet> listPlanets() {

		// System.out.println("List planets");

		MongoCursor<BasicDBObject> cursor = collection.find().iterator();
		List<Planet> planets = new ArrayList<Planet>();
		Planet planet = null;

		try {
			while (cursor.hasNext()) {
				// System.out.println(cursor.next().toJson());
				// System.out.println(cursor.next().toMap());
				Map<?, ?> map = cursor.next().toMap();
				planet = new Planet();
				planet.setId((Integer) map.get("_id"));
				planet.setName((String) map.get("name"));
				planet.setClimate((String) map.get("climate"));
				planet.setTerrain((String) map.get("terrain"));
				planet.setCountFilms((Integer) map.get("countFilms"));
				
				planets.add(planet);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			planets = null;
		} finally {
			cursor.close();
		}

		return planets;
	}

	public Planet getPlanet(int id) {

		// System.out.println("Get planet id=" + id);
		Planet planet = null;
		try {
			BasicDBObject mongoDbObject = collection.find(eq("_id", id)).first();

			if (mongoDbObject != null) {
				Map<?, ?> map = mongoDbObject.toMap();
				planet = new Planet();
				planet.setId((Integer) map.get("_id"));
				planet.setName((String) map.get("name"));
				planet.setClimate((String) map.get("climate"));
				planet.setTerrain((String) map.get("terrain"));
			}

		} catch (Exception e) {
			planet = null;
			e.printStackTrace();
		}

		return planet;

	}

	public String editPlanet(Planet planet, int id) {

		// System.out.println("Edit " + planet.getId() + " " + id);
		if (planet.getId() != 0) {
			if (planet.getId() != id) {
				return "ERROR: The URL id is different from id specifiend in json";
			}
		}

		String response = "";
		try {

			// Check if planet exists
			BasicDBObject mongoDbObject = collection.find(eq("_id", id)).first();
			if (mongoDbObject != null) {

				mongoDbObject = new BasicDBObject()
						.append("name", planet.getName())
						.append("climate", planet.getClimate())
						.append("terrain", planet.getTerrain());
				collection.updateOne(eq("_id", id), new Document("$set", mongoDbObject));
				response = "OK";
				
			} else {
				response = "WARNING: The planet " + planet.getName() + " alread exists";
			}

		} catch (Exception ex) {
			response = "ERROR: DAO has an error in edit planet " + planet.getName();
			ex.printStackTrace();
		}
		return response;
	}

	public String deletePlanet(String id) {
		// System.out.println("Delete planet "+id);
		String response = "";
		try {
			DeleteResult result = collection.deleteOne(eq("_id", Integer.parseInt(id)));
			if(result.getDeletedCount()==1) {
				response = "OK";
			}else {
				response = "Warning: DAO could not delete planet " + id + ". Maybe the planet dont exist";
			}
			
		} catch (Exception e) {
			response = "ERROR: DAO could not delete planet " + id;
			e.printStackTrace();
		}
		return response;
	}
	
	public List<Planet> listPlanetsByName(String name){
		
		// System.out.println("listPlanetsByName " + name);
		MongoCursor<BasicDBObject> cursor = collection.find(eq("name", name)).iterator();
		List<Planet> planets = new ArrayList<Planet>();
		Planet planet = null;

		try {
			while (cursor.hasNext()) {
				// System.out.println(cursor.next().toJson());
				// System.out.println(cursor.next().toMap());
				Map<?, ?> map = cursor.next().toMap();

				planet = new Planet();
				planet.setName((String) map.get("name"));
				planet.setClimate((String) map.get("climate"));
				planet.setTerrain((String) map.get("terrain"));
				planets.add(planet);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			planets = null;
		} finally {
			cursor.close();
		}
		// System.out.println(planets);
		return planets;
	}

}
