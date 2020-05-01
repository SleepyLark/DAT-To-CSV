package dat.model;

import java.util.ArrayList;

import dat.controller.AppController;

public class DatFile
{
	private ArrayList<String> game;
	private String title;
	private String version;
	private boolean hasSerial;
	private boolean hasReleaseNumber;
	
	public DatFile(AppController app)
	{
		game = new ArrayList<String>();
		title = "UNKNOWN";
		version = "0";
		hasSerial = false;
		hasReleaseNumber = false;
	}
	
	//=====[GET/SET]=====
	public ArrayList<String> getGames()
	{
		return game;
	}
	public String getTitle()
	{
		return title;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public boolean hasSerial()
	{
		return hasSerial;
	}
	
	public boolean isNumbered()
	{
		return hasReleaseNumber;
	}
	
	public void setGames(ArrayList<String> games)
	{
		this.game = games;
		hasReleaseNumber = game.get(0).contains("0001 - ");
		hasSerial = game.get(0).contains("serial=");
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public void setHasSerial(boolean state)
	{
		hasSerial = state;
	}
	
	
}
