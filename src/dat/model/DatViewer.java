package dat.model;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import dat.controller.AppController;

public class DatViewer
{

	private AppController app;
	private String loadedFile;
	private LucarIO io;
	private DatFile currentDat;
	private String[][] values;
	// switching to array instead of having to enter 13 parameters
	private boolean[] selectionData;
	private boolean largeFile;

	public DatViewer(AppController app)
	{
		this.app = app;

		io = new LucarIO(app);
		selectionData = new boolean[13];
		currentDat = new DatFile(app);
		loadedFile = "";
		largeFile = false;
	}

	/**
	 * Calls the IO to load the data and scans to see if it's a valid file <br>
	 * (NOTE: Data is loaded and stored as a String, which may not be the best way
	 * to store it especially for larger files)
	 */
	public void loadFile()
	{
		largeFile = false;
		loadedFile = io.loadString();

		// check to see if the there is data
		if (loadedFile != null && !loadedFile.isEmpty())
		{
			// validate that it's a usable file
			// TODO: May want to find a better way than to solely rely on the "!DOCTYPE" tag
			// or "datafile"

			if (!(findTag("!DOCTYPE", true) || findTag("datafile", true)))
				app.errorHandler("This is not a .dat file");

			// convert data information into an object
			currentDat.setTitle(getTag("name", false));
			currentDat.setVersion(getTag("version", false));
			currentDat.setHasSerial(loadedFile.contains("serial="));

			// determine if it's cartridge-based or disc-based
			// TODO: figure out if there's a way to auto-detect without needing the user's
			// input. Add fail safe in case user accidentally clicks the wrong button
			String[] selectType = { "Cartridge", "Disc" };
			int choice = JOptionPane.showOptionDialog(null, "Select ROM type\n(Failure to select the correct type will lead to improper results)", "Select Type",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, selectType, selectType[0]);
			if (choice == 0)
			{
				currentDat.setGames(this.getTags("game", false, loadedFile));

				// determine if preview is possible
				// TODO: find a good number that isn't too slow, or allow the user to input a
				// selected range
				if (currentDat.getGames().size() >= 2000)
				{
					String[] option = { "Yes", "No" };
					choice = JOptionPane.showOptionDialog(null,
							"There are " + currentDat.getGames().size()
									+ " entries listed.\n Preview is still possible, however it will be slower.\n Would you like to reduce the limit to 500?",
							"Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
					// if yes, shorten the display size
					if (choice == 0)
					{
						largeFile = true;
					}
				}
			}
			else
			{

			}
		}
	}

	public boolean hasReleaseNumber()
	{
		return currentDat.isNumbered();
	}

	public boolean hasSerial()
	{
		return currentDat.hasSerial();
	}

	/**
	 * Calls {@link #getTag(String, boolean, boolean, String) getTag()} and sees if
	 * it exist <br>
	 * (May change it since it's pretty much the same as
	 * "String.contains(keyword)").
	 * 
	 * @param tag
	 *            the tag without '< >'
	 * @param noEndTag
	 *            doesn't have an ending tag '< / >'
	 * @return if it can find the tag or not
	 */
	public boolean findTag(String tag, boolean noEndTag)
	{
		return !(getTag(tag, noEndTag, true, loadedFile) == null);
	}

	/**
	 * Calls {@link #getTag(String, boolean, boolean, String) getTag()}
	 * 
	 * @param tag
	 *            the tag without '< >'
	 * @param noEndTag
	 *            doesn't have an ending tag '< / >'
	 * @return The block within the start and end tag
	 */
	public String getTag(String tag, boolean noEndTag)
	{
		return getTag(tag, noEndTag, true, loadedFile);
	}

	/**
	 * Finds the lines of String within a given tag <br>
	 * Example: "< tag >Words_in_block< /tag>" -> "Words_in_block"
	 * 
	 * @param tag
	 *            the tag without '< >'
	 * @param noEndTag
	 *            doesn't have an ending tag '< / >'
	 * @param keepTag
	 *            keep tag in returned String or remove them from the start and end
	 * @param block
	 *            the section of string to extract the tag from
	 * @return the lines of String within a tag
	 */
	public String getTag(String tag, boolean noEndTag, boolean keepTag, String block)
	{

		String codeBlock = null;
		// adds "<" to tag, which is why you can't enter "<tag>" into the parameter
		// TODO: maybe add a check in case "<" is in the parameter?
		tag = "<" + tag.trim();
		String endTag = "</" + tag.substring(1) + ">";
		if (noEndTag)
			endTag = ">";

		Scanner reader = new Scanner(block);
		String currentLine = "";
		boolean endTagFound = false;
		boolean startPrinting = false;

		// While there's still stuff to read and haven't reached the end...
		while (reader.hasNext() && !endTagFound)
		{
			currentLine = reader.nextLine();

			if (currentLine.contains(tag))
			{

				currentLine = currentLine.substring(currentLine.indexOf(tag));
				// once the tag has been found, start saving the string
				startPrinting = true;
			}

			if (currentLine.contains(endTag))
			{
				codeBlock = ""; // is this necessary? Like do I need to initialize it? I forgot why it is here
				currentLine = currentLine.substring(0, currentLine.lastIndexOf(endTag)) + endTag;
				endTagFound = true;
			}

			if (startPrinting)
			{

				codeBlock += currentLine;
			}

		}

		if (!keepTag && codeBlock != null)
		{
			codeBlock = codeBlock.substring(tag.length() + 1, codeBlock.length() - endTag.length());
		}

		reader.close();

		return codeBlock;
	}

	/**
	 * Returns a list of the contents of every tag it finds <br>
	 * <i>VERY</i> similar to {@link #getTag(String, boolean, boolean, String)
	 * getTag()}, probably should tweak it
	 * 
	 * @param tag
	 *            the tag without '< >'
	 * @param noEndTag
	 *            doesn't have an ending tag '< / >'
	 * @param block
	 *            the section of string to extract the tag from
	 * @return An ArrayList that contains a list of every tag
	 */
	private ArrayList<String> getTags(String tag, boolean noEndTag, String block)
	{
		ArrayList<String> tags = new ArrayList<String>();
		tag = "<" + tag.trim();
		String endTag = "</" + tag.substring(1) + ">";
		if (noEndTag)
			endTag = ">";

		Scanner reader = new Scanner(block);
		String currentLine = "";
		boolean endTagFound = false;

		while (reader.hasNext())
		{
			// if it finds a tag...
			if (currentLine.contains(tag))
			{
				String codeBlock = "";
				// resets endpoint since this is a new tag
				endTagFound = false;

				currentLine = currentLine.substring(currentLine.indexOf(tag));

				// repeat until endpoint
				while (!endTagFound)
				{
					if (currentLine.contains(endTag))
					{
						currentLine = currentLine.substring(0, currentLine.lastIndexOf(endTag)) + endTag;
						endTagFound = true;
					}

					codeBlock += currentLine;

					currentLine = reader.nextLine();
				}

				tags.add(codeBlock);

			}
			// else move on to the next line...
			else
			{
				currentLine = reader.nextLine();
			}
		}

		reader.close();

		return tags;
	}

	/**
	 * Returns the section String within a quoted parameter <br>
	 * (Example: "<a href="www.shrek.org">" -> "www.shrek.org")
	 * 
	 * @param block
	 *            the block that contains the parameter quote
	 * @param keyword
	 *            the starting parameter (ex. "ref=")
	 * @return whatever is inside the quotes of a given parameter
	 */
	private String findQuoteParameter(String block, String keyword)
	{
		String result = "";
		if (block.contains(keyword))
		{
			block = block.substring(block.indexOf(keyword) + keyword.length() + 1);
			result = block.substring(0, block.indexOf("\""));
		}
		return result;
	}

	/**
	 * Generates a CSV list using {@link #makePreview()} and saves it to a file
	 * using {@link #LucarIO.saveString()}
	 * 
	 */
	public void export()
	{
		largeFile = false;
		io.saveString(makePreview());
	}

	/**
	 * Analyze the data and convert the information into a CSV row
	 * 
	 * @param entry
	 *            The block of code for the entry
	 * 
	 * @return a row with all the information in CSV format
	 */
	private String convertGameToRow(String entry)
	{
		// I made it so the order of booleans in the parameter matches the order in
		// which the data is added.
		// May not be that great of an idea
		String rowData = "";

		// Get name of entry
		String name = this.getTag("description", false, false, entry);
		String releaseNum = null;

		// if the DAT has release numbers...
		if (currentDat.isNumbered())
		{
			// release numbers should be 5 char long (but knowing me there's probably some
			// edge case that will completely break this)
			// (Ex. "0001 - Cool Game -> "0001")
			releaseNum = name.substring(0, 5);

			if (selectionData[app.INCLUDE_NUM])
				rowData += "\"" + releaseNum + "\",";
		}

		// Get region tag, which should be the first set of parenthesis
		// (Ex. "Super Mario 64 (USA)" -> "USA")
		String region = name.substring(name.indexOf("(") + 1, name.indexOf(")"));

		// If there is a release number and that number should be removed
		if (selectionData[app.REMOVE_NUM] && currentDat.isNumbered())
			// Release numbers usually have this format: "0001 - Cool Game", meaning the
			// name starts on the index 7
			name = name.substring(7);

		// Replaces special characters back to their readable format
		// TODO: Find and replace every special character option
		name = name.replace("&amp;", "&");

		if (selectionData[app.REMOVE_REGION_TAG])
			name = name.replace(" (" + region + ")", "");

		if (selectionData[app.REMOVE_LANGUAGE])
		{
			if (name.contains("("))
			{
				// the language tag is normally at the end of the name and contains a comma
				// since there's usually multiple languages
				String endTag = name.substring(name.lastIndexOf("(") + 1, name.lastIndexOf(")"));
				if (!endTag.equals(region) && (endTag.contains(",") || endTag.contains("+")))
					name = name.replace("(" + endTag + ")", "");
			}
		}

		// add name to row
		rowData += "\"" + name.trim() + "\",";

		// adds region to row
		if (selectionData[app.INCLUDE_REGION])
			rowData += "\"" + region + "\",";

		// Try and find serial ID if found
		if (selectionData[app.INCLUDE_SERIAL])
		{
			String serialID = findQuoteParameter(this.getTag("rom", false, true, entry), "serial=").trim();
			if (serialID.isEmpty())
				serialID = "UNKNOWN";

			// Make missing serial IDs empty instead of "UNKNOWN"
			if (selectionData[app.REMOVE_MISSING_SERIAL]
					&& (serialID.toLowerCase().contains("missing") || serialID.toLowerCase().contains("none") || serialID.toLowerCase().contains("unknown")))
			{
				serialID = "";
			}

			// adds serialID to row
			rowData += serialID + ",";
		}

		if (selectionData[app.INCLUDE_SIZE])
		{
			// find byte size
			String size = findQuoteParameter(this.getTag("rom", false, true, entry), "size=");

			// convert bytes to human readable format, else it will stay as it's exact byte
			// value
			if (selectionData[app.CONVERT_BYTE])
				size = this.getSize(Long.parseLong(size));

			// adds size to row
			rowData += size + ",";
		}

		String crc = "CRC: ";
		String md5 = "MD5: ";
		String sha1 = "SHA1: ";
		String hash = "";

		if (selectionData[app.INCLUDE_CRC])
			crc += findQuoteParameter(this.getTag("rom", false, true, entry), "crc=");
		if (selectionData[app.INCLUDE_MD5])
			md5 += findQuoteParameter(this.getTag("rom", false, true, entry), "md5=");
		if (selectionData[app.INCLUDE_SHA1])
			sha1 += findQuoteParameter(this.getTag("rom", false, true, entry), "sha1=");

		if (selectionData[app.MERGE_HASH])
		{
			hash = "\"";
			if (selectionData[app.INCLUDE_CRC])
			{
				hash += crc;
				if (selectionData[app.INCLUDE_MD5])
					hash += " " + md5;
				if (selectionData[app.INCLUDE_SHA1])
					hash += " " + sha1;
			}
			if (selectionData[app.INCLUDE_MD5])
			{
				hash += md5;
				if (selectionData[app.INCLUDE_SHA1])
					hash += " " + sha1;
			}
			hash += "\"";

		}
		else
		{

			if (selectionData[app.INCLUDE_CRC])
				hash += "\"" + crc + "\",";
			if (selectionData[app.INCLUDE_MD5])
				hash += "\"" + md5 + "\",";
			if (selectionData[app.INCLUDE_SHA1])
				hash += "\"" + sha1 + "\",";
		}

		// adds hash to row
		rowData += hash;

		return rowData;
	}

	/**
	 * Loops {@link #convertGameToRow()} for every entry and prints the entire list
	 * 
	 * @return a String with every entry as CSV
	 */
	public String makePreview()
	{
		String preview = "";
		int limit = currentDat.getGames().size();
		// If there's a lot of entries, only show a certain amount
		// TODO: Add option to have user change what the make amount to be.
		// Maybe add option to select which entries to see.
		if (largeFile)
			limit = 500;
		for (int index = 0; index < limit; index++)
			preview += convertGameToRow(currentDat.getGames().get(index)) + "\n";
		return preview;
	}

	/**
	 * Converts it into a CSV specifically for NDS-Bootstrap's compatibility list
	 * <br>
	 * (May remove as there's no purpose for it and was only used as a reference for
	 * {@link #convertGameToRow()} )
	 */
	public void datomaticNDSToCSV()
	{
		String csv = "";
		String currentCompat = io.loadString();
		ArrayList<String> tags = this.getTags("game", false, currentCompat);
		values = new String[tags.size()][6];

		for (int row = 0; row < values.length; row++)
		{
			String name = this.getTag("description", false, false, tags.get(row));
			String releaseNum = name.substring(0, 5);
			String cartType = "NTR";
			String region = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
			name = name.substring(7);
			name = name.replace("&amp;", "&");
			name = name.replace("(" + region + ")", "");
			region = region.replace(",", " +");
			region = regionID(region);

			String size = findQuoteParameter(this.getTag("rom", false, true, tags.get(row)), "size=");
			size = this.getSize(Long.parseLong(size));

			String TID = findQuoteParameter(this.getTag("rom", false, true, tags.get(row)), "serial=").trim();
			if (TID.isEmpty())
				TID = "UNKNOWN";

			if (name.contains("(NDSi Enhanced)"))
			{
				cartType = "TWL";
				name = name.replace(" (NDSi Enhanced)", "");
			}

			if (name.contains("("))
			{
				String language = name.substring(name.indexOf("("), name.indexOf(")") + 1);
				if (language.contains(","))
				{
					name = name.replace(" " + language, "");
				}
			}

			name = "\"" + name.trim() + "\"";
			values[row][0] = "\"" + releaseNum.trim() + "\",";
			values[row][1] = name + ",";
			values[row][2] = cartType + ",";
			values[row][3] = TID + ",";
			values[row][4] = region + ",";
			values[row][5] = size + ",";

			for (int col = 0; col < values[0].length; col++)
			{

				csv += values[row][col];
				if (col == values[0].length - 1)
					csv += "\n";
			}

		}

		io.saveString(csv);
	}

	/**
	 * Carryover attempt of the previous project to try and delete duplicates by
	 * comparing two CSV files <br>
	 * (May remove as there's no use anymore and didn't work in the first place)
	 * 
	 * @return a list with no duplicates (at least it's suppose to)
	 */
	public String removeDuplicateCSV()
	{
		String currentCompat = io.loadString();
		String master = io.loadString();
		Scanner csvRead = new Scanner(currentCompat);
		Scanner masterRead = new Scanner(master);
		ArrayList<String> csvList = new ArrayList<String>();
		ArrayList<String> masterList = new ArrayList<String>();
		String finalList = "";

		while (csvRead.hasNext())
		{
			csvList.add(csvRead.nextLine());
		}
		while (masterRead.hasNext())
		{
			masterList.add(masterRead.nextLine());
		}

		for (int index = csvList.size() - 1; index >= 0; index--)
		{
			String compatNum = "null";
			if (csvList.get(index).length() > 5)
				compatNum = csvList.get(index).substring(0, 4);
			String currentCompatItem = csvList.get(index);

			for (int index2 = masterList.size() - 1; index2 >= 0; index2--)
			{
				if (masterList.get(index2).contains(compatNum))
				{
					csvList.remove(currentCompatItem);
					app.print(masterList.remove(index2));
				}
			}
		}

		for (int index = 0; index < masterList.size(); index++)
		{
			finalList += masterList.get(index) + "\n";
		}

		csvRead.close();
		masterRead.close();

		return finalList;

	}

	/**
	 * Used for NDS-Bootstrap, has the region ID for Nintendo cartridges
	 * 
	 * @param region
	 * @return a three letter ID
	 */
	private String regionID(String region)
	{
		String id = region;

		switch (region)
		{
		case ("Europe"):
			id = "EUR";
			break;
		case ("Japan"):
			id = "JPN";
			break;
		case ("World"):
			id = "N/A";
			break;
		case ("China"):
			id = "CHN";
			break;
		case ("Korea"):
			id = "KOR";
			break;
		case ("Australia"):
			id = "AUS";
			break;
		case ("Norway"):
			id = "NOR";
			break;
		case ("Denmark"):
			id = "DEN";
			break;
		case ("France"):
			id = "FRA";
			break;
		case ("Germany"):
			id = "GER";
			break;
		case ("Italy"):
			id = "ITA";
			break;
		case ("Russia"):
			id = "RUS";
			break;
		case ("Spain"):
			id = "SPA";
			break;
		case ("Netherlands"):
			id = "HOL";
			break;
		default:
			id = region;
		}

		// if (region.contains("+"))
		// {
		// id = "Multi-Region";
		// }

		return id;
	}

	/**
	 * Converts bytes to a human readable format
	 * 
	 * @param filesize
	 *            size in bytes
	 * @return a String with the simplified value
	 */
	public String getSize(long filesize)
	{

		String size = "";

		if (filesize >= 1024)
		{
			if (filesize >= 1048576)
			{
				if (filesize >= 1073741824)
				{
					size = io.toGigaBytes(filesize) + " GB";
				}
				else
				{
					size = io.toMegaBytes(filesize) + " MB";
				}
			}
			else
			{
				size = io.toKiloBytes(filesize) + " KB";
			}
		}
		else
		{
			size = filesize + " bytes";
		}

		return size;
	}

	public void setLoadedFile(String text)
	{
		loadedFile = text;
	}

	public void setSelectionData(int index, boolean state)
	{
		selectionData[index] = state;
	}

	public boolean[] getSelectionData()
	{
		return selectionData;
	}
}
