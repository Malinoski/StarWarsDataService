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

	/**
	 * The method create the planet index document in MondoDB.
	 * This document was designed to store the last planet id and to be incremented in every planet insertion.
	 */
	private void createPlanetIndex(MongoDBConfig mongoDBConfig) {
		
		collectionIndex = mongoDBConfig.getCollection("PlanetsIndex");
		try {
			
			// Try to get the last id
			BasicDBObject mongoDbObject = collectionIndex.find(eq("_id", "productid")).first();

			// If not, create the first id 
			if (mongoDbObject == null) {
				// System.out.println("Create PlanetsIndex");
				mongoDbObject = new BasicDBObject("_id", "productid")
						.append("sequence_value", 0);
				collectionIndex.insertOne(mongoDbObject);

			} 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the last planetIndex
	 * @return Returns the last planet id > 0
	 */
	public static int getLastPlanetIndex() {
		BasicDBObject mongoDbObject = collectionIndex.find(eq("_id", "productid")).first();
		return (int) mongoDbObject.get("sequence_value");
	}

	/**
	 * Add a planet
	 * This method must be static, because when a planet is added, the planet index must be incremented.
	 * For this reason, all the queue request (nem planets to be added) must wait the new id.
	 * @param The new planet
	 * @return Returns a new planet id
	 */
	public static int addPlanet(Planet planet) {

		// Create the planet object
		int lastPlanetIndex = getLastPlanetIndex();
		int nextPlanetIndex = lastPlanetIndex + 1;
		BasicDBObject mongoDbObject = new BasicDBObject("_id", nextPlanetIndex)
				.append("name", planet.getName())
				.append("climate", planet.getClimate())
				.append("terrain", planet.getTerrain())
				.append("countFilms", countFilmsFromPlanet(planet.getName()));

		try {
			
			// Insert the new planet in db
			collection.insertOne(mongoDbObject);
			
			// Update the planet id in collectionIndex
			if (updateLastPlanetIndex(nextPlanetIndex)) {
				
				// Return the new planet id
				return nextPlanetIndex;
			} else {
				
				// Return ERROR
				return -1;
			}
		} catch (Exception ex) {
			
			// Return ERROR
			return -1;
		}

	}
	
	/**
	 * Update the last planet index in MongoDB
	 * @param New planet id
	 * @return Returns a confirmation (true/false)
	 */
	public static boolean updateLastPlanetIndex(int nextPlanetIndex) {
		BasicDBObject mongoDbObject = new BasicDBObject("_id", "productid").append("sequence_value", nextPlanetIndex);
		UpdateResult result = collectionIndex.updateOne(eq("_id", "productid"), new Document("$set", mongoDbObject));

		if (result.getModifiedCount() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method check if a planet apper in a film (https://swapi.co/)
	 * @param The planet name (must be exactly)
	 * @return Return >= 0
	 */
	public static int countFilmsFromPlanet(String planetName) {
		SwapiHelper helper = new SwapiHelper();
		return helper.getFilmsCountFromPlanet(planetName);
	}

	/**
	 * Get all planets
	 * @return A list of planets
	 */
	public List<Planet> listPlanets() {

		MongoCursor<BasicDBObject> cursor = collection.find().iterator();
		List<Planet> planets = new ArrayList<Planet>();
		Planet planet = null;

		try {
			while (cursor.hasNext()) {
				
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

	/**
	 * Get a planet by id
	 * @param The planet id
	 * @return Returns the planet or null if it does not find
	 */
	public Planet getPlanet(int id) {

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

	/**
	 * Edit a planet by id. 
	 * @param The planet id and the planet object
	 * @return Returns "OK", "ERROR [message]" or "WARNING [message]"
	 */
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

	/**
	 * Delete a planet by id. 
	 * @param The planet id
	 * @return Returns "OK", "ERROR [message]" or "WARNING [message]"
	 */
	public String deletePlanet(String id) {
		// System.out.println("Delete planet "+id);
		String response = "";
		try {
			DeleteResult result = collection.deleteOne(eq("_id", Integer.parseInt(id)));
			if(result.getDeletedCount()==1) {
				response = "OK";
			}else {
				response = "WARNING: DAO could not delete planet " + id + ". Maybe the planet dont exist";
			}
			
		} catch (Exception e) {
			response = "ERROR: DAO could not delete planet " + id;
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * List planets by name (can exist more than on planet with the same name, but different by id)
	 * @param The planet name
	 * @return Returns a list of planet
	 */
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
