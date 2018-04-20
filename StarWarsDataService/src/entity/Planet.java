package entity;

public class Planet {

	private int id;
	private String name;
	private String climate;
	private String terrain;
	private int countFilms;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClimate() {
		return climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

	public String getTerrain() {
		return terrain;
	}

	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}

	public int getCountFilms() {
		return countFilms;
	}

	public void setCountFilms(int countFilms) {
		this.countFilms = countFilms;
	}

}
