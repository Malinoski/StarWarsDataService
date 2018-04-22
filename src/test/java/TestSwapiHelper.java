package test.java;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import lib.SwapiHelper;

public class TestSwapiHelper extends JerseyTest{

	@Override
	protected Application configure() {
		return new ResourceConfig();
	}
	
	@Test
	public void testSwapiSearchFilmCountFromAlderaanPlanet() {
		SwapiHelper helper = new SwapiHelper();
		int count = helper.getFilmsCountFromPlanet("Alderaan");
		Assert.assertTrue(count==2); // Alderaan planet appear in 2 films		
	}
	
	@Test
	public void testSwapiSearchFilmCountFromUnknown() {
		SwapiHelper helper = new SwapiHelper();
		int count = helper.getFilmsCountFromPlanet("Unknown");
		Assert.assertTrue(count==0); // Alderaan planet appear in 2 films		
	}

}
