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
	public void testAllPlanetServicesWithAddSelectEditListDelete() {
		
		String name = generatePlanetTestName();
		
		// Add a planet (with a ramdom name) and check if the planet has a valid id (between 0 and N
		int id = addUniquePlanet(name, "SomeClimate","SomeTerrain");
		Assert.assertTrue(id>-1);
		
		// Get the planet by id and check if the given id is equal to returned id
		Assert.assertTrue(id == getPlanetById(id).getId());
		
		// List all planets with the given name (the test planet). This test must return only ONE planet
		Assert.assertTrue(findPlanetByName(name).size()==1);
		
		// Check if one or more planets are returned
		Assert.assertTrue(listPlanets().size()>0);
		
		// Edit a planet by id and check if was done
		Assert.assertTrue(editPlanet(id).equals("OK"));
		
		// Edit a planet where dont exist (id > -1)
		Assert.assertTrue(editPlanet(-1).startsWith("WARNING"));
		
		// Delete the planet and check if was done
		Assert.assertTrue(deletePlanet(id).equals("OK"));
		
		// Delete a planet where dont exist and check if was dont (assert false)
		Assert.assertTrue(deletePlanet(id).startsWith("WARNING"));
		
		// Get the planet where dont exist and check is null
		Assert.assertNull(getPlanetById(id));
	}
	
	public String generateStringJsonTest(String name, String climate, String terrain) {
		return "{ \"name\": \"" + name + "\", \"climate\":\""+climate+"\", \"terrain\":\""+terrain+"\" }";
	}
	
	public int addUniquePlanet(String name, String climate, String terrain) {
		
		// Data request
		String json = generateStringJsonTest(name, climate, terrain);
		String response = target("planets/").request().post(Entity.entity(json, MediaType.APPLICATION_JSON),String.class);
		
		// Check response
		int newId = -1;
		try {
			newId = Integer.parseInt(response);
		}catch(NumberFormatException ex) {
			ex.printStackTrace();			
		}
		
		return newId;
		
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
	
	private String editPlanet(int id) {
		String json = generateStringJsonTest("NewPlanetName", "NewClimate", "NewTerrain");
		String response = target("planets/"+id).request().put(Entity.entity(json, MediaType.APPLICATION_JSON),String.class);
		return response;
	}
	
	public String deletePlanet(int id) {		
		String response = target("planets/"+id).request().delete(String.class);		
		return response;
	}
	
	public Planet getPlanetById(int id) {		
		Planet result = target("planets/"+id).request(MediaType.APPLICATION_JSON).get(new GenericType<Planet>() {});
		return result;		
	}
	
	public List<Planet> listPlanets() {
		List<Planet> result = target("planets").request(MediaType.APPLICATION_JSON).get(new GenericType<List<Planet>>() {});
		return result;
	}
	
}