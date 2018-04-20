package rest;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dao.PlanetDAO;
import entity.Planet;

@Path("/planets")
public class PlanetService {
	
	private static final String CHARSET_UTF8 = ";charset=utf-8";
	private static PlanetDAO planetDAO;
	
	@PostConstruct
	private void ini(){
		planetDAO = new PlanetDAO();
	}
	
	/**
	 * Operation List all planets
	 * 
	 * @param None
	 * @return json array (ex.: [{"name": "PlanetA"},{"name": "PlanetB"}]) or null for error
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Planet> listPlanets(){
		List<Planet> planets = planetDAO.listPlanets();
		return planets;
	}
	
	/**
	 * Operation: Get one planet
	 * @param PLanet ID
	 * @return json (ex.: {"name": "PlanetA"}) or null for error
	 */
	@GET
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Planet getPlanet(@PathParam("id") String id){
		Planet planet = planetDAO.getPlanet(Integer.parseInt(id));			
		return planet;
	}
	
	/**
	 * Operation: Include one. Uses POST for non-idempotency (many requests CAN affect each other)
	 * @param Body param, ex.: { "name": "PlanetName", "climate":"ClimateName", "terrain":"TerrainName" }
	 * @return Planet ID
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF8)
	@Produces(MediaType.TEXT_PLAIN)
	public String addPlanet (Planet planet) {
		String planetId = String.valueOf(PlanetDAO.addPlanet(planet));
		return planetId;
	}
	
	/**
	 * Operation: Edit one Planet. Uses PUT for idempotency (many requests not affect each other).
	 * Modify the element according to URL id (ex.: planets/1) and the body json (ex.: {"name": "PlanetName","climate":"NewClimate"}). 
	 * If the json has the id (ex.: {"_id":99, "name": "PlanetName","climate":"NewClimate"}), the URL id must be equals (ex.: planets/99).
	 * 
	 * @param Body param, ex.: {"name": "PlanetNameA", "climate":"ClimateNovo3", "terrain":"TerrainA" }
	 * @return "OK", "ERROR: [message error]" or "WARNING: [message]"
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF8)
	@Produces(MediaType.TEXT_PLAIN)
	public String editPlanet(@PathParam("id") int id, Planet planet) {
		String response = planetDAO.editPlanet(planet, id);
		return response;
	}
	
	/**
	 * Operation: Delete one
	 * @param By URL, ex.: /planetId
	 * @return "OK", "ERROR: [message error]" or "WARNING: [message]"
	 */
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String deletePlanet(@PathParam("id") String id) {
		return planetDAO.deletePlanet(id);		
	}
	
	/**
	 * Search by exactly planet name
	 * 
	 * @param Planet name 
	 * @return json array (ex.: [{"id":1,"name": "PlanetA"},{"id":1,"name": "PlanetA"}]) or null for error
	 */
	@GET
	@Path("/search/name={name}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Planet> listPlanetsByName(@PathParam("name") String name){
		List<Planet> planets = planetDAO.listPlanetsByName(name);
		return planets;
	}
	
}
