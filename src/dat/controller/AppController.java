package dat.controller;

import javax.swing.JOptionPane;
import dat.model.*;
import dat.view.AppFrame;

public class AppController
{
	private DatViewer reader;
	private AppFrame appFrame;
	
	public final int INCLUDE_NUM = 0;
	public final int INCLUDE_SIZE = 1;
	public final int CONVERT_BYTE = 2;
	public final int INCLUDE_REGION = 3;
	public final int REMOVE_REGION_TAG = 4;
	public final int INCLUDE_CRC = 5;
	public final int INCLUDE_MD5= 6;
	public final int INCLUDE_SHA1 = 7;
	public final int MERGE_HASH = 8;
	public final int REMOVE_LANGUAGE = 9;
	public final int REMOVE_NUM = 10;
	public final int INCLUDE_SERIAL = 11;
	public final int REMOVE_MISSING_SERIAL = 12;

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

	public void saveFile()
	{
		reader.export();
	}

	public String getPreview()
	{
		return reader.makePreview();
	}

	public boolean hasReleaseNumber()
	{
		return reader.hasReleaseNumber();
	}

	public boolean hasSerial()
	{
		return reader.hasSerial();
	}

	public void setSelectionData(int index, boolean state)
	{
		reader.setSelectionData(index, state);
	}
	
	public boolean[] getSelectionData()
	{
		return reader.getSelectionData();
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
