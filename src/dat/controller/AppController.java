package dat.controller;

import javax.swing.JOptionPane;
import dat.model.*;
import dat.view.AppFrame;

public class AppController
{
	private DatViewer reader;
	private AppFrame appFrame;

	public AppController()
	{
		reader = new DatViewer(this);
		appFrame = new AppFrame(this);
	}

	public void start()
	{

	}

	public void loadFile()
	{
		reader.loadFile();
		appFrame.updateDisplay();
	}

	public void saveFile(boolean includeNum, boolean includeSize, boolean convertBytes, boolean includeRegion, boolean removeRegionTag, boolean includeCRC, boolean includeMD5,
			boolean includeSHA1, boolean mergeHash, boolean removeLanguage, boolean removeNum, boolean includeSerial, boolean removeMissingSerial)
	{
		reader.export(includeNum, includeSize, convertBytes, includeRegion, removeRegionTag, includeCRC, includeMD5, includeSHA1, mergeHash, removeLanguage, removeNum, includeSerial, removeMissingSerial);
	}

	public String getPreview(boolean includeNum, boolean includeSize, boolean convertBytes, boolean includeRegion, boolean removeRegionTag, boolean includeCRC, boolean includeMD5,
			boolean includeSHA1, boolean mergeHash, boolean removeLanguage, boolean removeNum, boolean includeSerial, boolean removeMissingSerial)
	{
		return reader.makePreview(includeNum, includeSize, convertBytes, includeRegion, removeRegionTag, includeCRC, includeMD5, includeSHA1, mergeHash, removeLanguage, removeNum, includeSerial, removeMissingSerial);
	}
	
	public boolean hasReleaseNumber()
	{
		return reader.hasReleaseNumber();
	}
	public boolean hasSerial()
	{
		return reader.hasSerial();
	}

	public void errorHandler(Exception problem)
	{
		JOptionPane.showMessageDialog(null, problem.getMessage(), "[ERROR]: ", JOptionPane.ERROR_MESSAGE);
	}

	public void errorHandler(String message)
	{
		JOptionPane.showMessageDialog(null, message, "[ERROR] ", JOptionPane.ERROR_MESSAGE);
	}

	public void print(String word)
	{
		System.out.println(word);
	}

	
}
