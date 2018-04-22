package lib;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SwapiHelper {

	Client c;

	public SwapiHelper() {
		if (c == null) {
			c = Client.create();
		}
	}

	// This method check if a planet apper in a film in https://swapi.co/
	public int getFilmsCountFromPlanet(String name) {

		int response = 0;

		try {

			WebResource wr = c.resource("https://swapi.co/api/planets/?search=" + name);
			ClientResponse resp = wr.accept("application/json").header("user-agent", "").get(ClientResponse.class);

			if (resp.getStatus() == 200) {

				String output = resp.getEntity(String.class);
				// System.out.println(output);

				JSONObject jsnobject = new JSONObject(output);
				JSONArray results = jsnobject.getJSONArray("results");
				if (results.length() == 1) {
					JSONObject first = results.getJSONObject(0);
					JSONArray films = first.getJSONArray("films");
					// System.out.println(films.length());
					response = films.length();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return response;
	}

}
