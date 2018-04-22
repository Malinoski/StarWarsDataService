package test.java;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import entity.Planet;
import rest.PlanetService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

public class TestPlanetService extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(PlanetService.class);
	}
	
	@Test
	public void testAddSelectEditListDeletePlanet() {
		
		String name = generatePlanetTestName();
		
		// Uses REST service: addPlanet
		int id = addUniquePlanet(name, "SomeClimate","SomeTerrain");
		
		// Uses REST service: getPlanet
		getPlanetById(id);
		
		// Uses REST service: listPlanetsByName
		listOnePlanetByName(name);
		
		// Uses REST service: listPlanets service
		listOneOrMorePlanets();
		
		// Uses REST service: editPlanet
		editExistentPlanetTest(id);
		
		// Uses REST service: deletePlanet
		deleteExistentPlanet(id);
		
		// Uses REST service: deletePlanet
		deleteNonExistentPlanet(id);
		
		// Uses REST service: getPlanet
		getNonexistentPlanet(id);
	}
	
	public List<Planet> listOnePlanetByName(String name) {
		List<Planet> planets = target("planets/search/name="+name).request(MediaType.APPLICATION_JSON).get(new GenericType<List<Planet>>() {});
		Assert.assertTrue(planets.size()==1);
		return planets;	
	}
	
	public String generatePlanetTestName() {
		int randomNum = ThreadLocalRandom.current().nextInt(1000, 1000000 + 1);
		String name = "PlanetTest"+randomNum;
		Assert.assertTrue(findPlanetByName(name).size()==0);
		return name;
	}
	
	public List<Planet> findPlanetByName(String name) {
		List<Planet> planets = target("planets/search/name="+name).request(MediaType.APPLICATION_JSON).get(new GenericType<List<Planet>>() {});
		return planets;	
	}
	
	
	public int addUniquePlanet(String name, String climate, String terrain) {
		
		// Request
		String json = "{ \"name\": \"" + name + "\", \"climate\":\""+climate+"\", \"terrain\":\""+terrain+"\" }";
		String response = target("planets/").request().post(Entity.entity(json, MediaType.APPLICATION_JSON),String.class);
		// Check response
		int newId = -1;
		try {
			newId = Integer.parseInt(response);
		}catch(NumberFormatException ex) {
			ex.printStackTrace();			
		}
		
		// Check if element was inluded
		Assert.assertTrue(newId>-1);
		return newId;
		
	}
	
	private void editExistentPlanetTest(int id) {
		// Request
		String json = "{ \"name\": \"NewPlanetName\", \"climate\":\"NewClimate\", \"terrain\":\"NewTerrain\" }";
		String response = target("planets/"+id).request().put(Entity.entity(json, MediaType.APPLICATION_JSON),String.class);
		
		Assert.assertTrue(response.equals("OK"));	
		
	}
	
	public void deleteExistentPlanet(int id) {		
		String response = target("planets/"+id).request().delete(String.class);
		Assert.assertTrue(response.equals("OK"));
	}
	
	public void deleteNonExistentPlanet(int id) {		
		String response = target("planets/"+id).request().delete(String.class);
		Assert.assertFalse(response.equals("OK"));
	}
	
	public void getPlanetById(int id) {		
		Planet result = target("planets/"+id).request(MediaType.APPLICATION_JSON).get(new GenericType<Planet>() {});
		Assert.assertTrue(id == result.getId());		
	}
	
	public void getNonexistentPlanet(int id) {
		String idString = String.valueOf(id);
		Planet result = target("planets/"+idString).request(MediaType.APPLICATION_JSON).get(new GenericType<Planet>() {});
		Assert.assertNull(result);			
	}
	
	public void listOneOrMorePlanets() {
		List<Planet> result = target("planets").request(MediaType.APPLICATION_JSON).get(new GenericType<List<Planet>>() {});
		Assert.assertTrue(result.size()>0);
	}
	
}