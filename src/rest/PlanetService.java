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
	private PlanetDAO planetDAO;
	
	@PostConstruct
	private void ini(){
		planetDAO = new PlanetDAO();
	}
	
	/**
	 * List all planets
	 * @return Return json array of planets
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Planet> listPlanets(){
		List<Planet> planets = planetDAO.listPlanets();
		return planets;
	}
	
	/**
	 * Operation: Get one planet
	 * @param PLanet id
	 * @return Planet
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
	 * Include one planet by non-idempotency POST. 
	 * Many requests CAN affect each other, and for this reason, this service consumes the static method PlanetDAO.addPlanet.
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
	 * Edit a planet by id. 
	 * @param The planet id and the planet json
	 * @return Returns "OK", "ERROR [message]" or "WARNING [message]"
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
	 * Delete a planet by id. 
	 * @param The planet id
	 * @return Returns "OK", "ERROR [message]" or "WARNING [message]"
	 */
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String deletePlanet(@PathParam("id") String id) {
		return planetDAO.deletePlanet(id);		
	}
	
	/**
	 * List planets by name (can exist more than on planet with the same name, but different by id)
	 * @param The planet name
	 * @return Returns a list of planet
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
