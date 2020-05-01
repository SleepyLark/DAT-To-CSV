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
	private boolean largeFile;

	public DatViewer(AppController app)
	{
		this.app = app;

		io = new LucarIO(app);
		currentDat = new DatFile(app);
		loadedFile = "";
		largeFile = false;
	}

	public void loadFile()
	{
		largeFile = false;
		loadedFile = io.loadString();
		if (loadedFile != null)
		{
			if (!(findTag("!DOCTYPE", true) || findTag("datafile", true)))
				app.errorHandler("This is not a .dat file");

			currentDat.setTitle(getTag("name", false));
			currentDat.setVersion(getTag("version", false));
			currentDat.setGames(this.getTags("game", false, loadedFile));
			
			if(currentDat.getGames().size() >= 1000)
			{
				String[] option = {"Yes", "No"};
				int choice = JOptionPane.showOptionDialog(null,"There are "+ currentDat.getGames().size() + " entries listed.\n Preview is still possible, however it will be slower.\n Would you like to reduce the limit to 500?","Warning",JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
				if(choice == 0)
				{
					largeFile = true;
				}
			}
		}

	}

	public boolean hasReleaseNumber()
	{
		return currentDat.isNumbered();
	}

	public boolean findTag(String tag, boolean noEndTag)
	{
		return !(getTag(tag, noEndTag, true, loadedFile) == null);
	}

	public String getTag(String tag, boolean noEndTag)
	{
		return getTag(tag, noEndTag, true, loadedFile);
	}

	public String getTag(String tag, boolean noEndTag, boolean keepTag, String block)
	{

		String codeBlock = null;
		tag = "<" + tag.trim();
		String endTag = "</" + tag.substring(1) + ">";
		if (noEndTag)
			endTag = ">";

		Scanner reader = new Scanner(block);
		String currentLine = "";
		boolean endTagFound = false;
		boolean startPrint = false;

		while (reader.hasNext() && !endTagFound)
		{
			currentLine = reader.nextLine();
			if (currentLine.contains(tag))
			{

				currentLine = currentLine.substring(currentLine.indexOf(tag));
				startPrint = true;
			}

			if (currentLine.contains(endTag))
			{
				codeBlock = "";
				currentLine = currentLine.substring(0, currentLine.lastIndexOf(endTag)) + endTag;
				endTagFound = true;
			}

			if (startPrint)
			{

				codeBlock += currentLine;
			}

			if (!keepTag)
			{
				codeBlock = codeBlock.substring(tag.length() + 1, codeBlock.length() - endTag.length());
			}

		}
		reader.close();

		return codeBlock;
	}

	private ArrayList<String> getTags(String tag, boolean noEndTag, String block)
	{
		ArrayList<String> tags = new ArrayList<String>();
		tag = "<" + tag.trim();
		String endTag = "</" + tag.substring(1) + ">";
		if (noEndTag)
			endTag = ">";

		Scanner reader = new Scanner(block);
		String currentLine = "";

		while (reader.hasNext())
		{
			if (currentLine.contains(tag))
			{
				String codeBlock = "";
				boolean endTagFound = false;
				currentLine = currentLine.substring(currentLine.indexOf(tag));

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
				endTagFound = false;

			}
			else
			{
				currentLine = reader.nextLine();
			}
		}
		reader.close();

		return tags;
	}

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

	public void export(boolean includeNum, boolean includeSize, boolean convertBytes, boolean includeRegion, boolean removeRegionTag, boolean includeCRC, boolean includeMD5,
			boolean includeSHA1, boolean mergeHash, boolean removeLanguage, boolean removeNum)
	{
		largeFile = false;
		io.saveString(
				makePreview(includeNum, includeSize, convertBytes, includeRegion, removeRegionTag, includeCRC, includeMD5, includeSHA1, mergeHash, removeLanguage, removeNum));
	}

	private String convertGameToRow(String entry, boolean includeNum, boolean includeSize, boolean convertBytes, boolean includeRegion, boolean removeRegionTag, boolean includeCRC,
			boolean includeMD5, boolean includeSHA1, boolean mergeHash, boolean removeLanguage, boolean removeNum)
	{
		String rowData = "";

		String name = this.getTag("description", false, false, entry);
		String releaseNum = null;

		if (currentDat.isNumbered())
			releaseNum = name.substring(0, 5);
		if (includeNum)
			rowData += "\"" + releaseNum + "\",";

		String region = name.substring(name.indexOf("(") + 1, name.indexOf(")"));

		if (removeNum && currentDat.isNumbered())
			name = name.substring(7);

		name = name.replace("&amp;", "&");

		if (removeRegionTag)
			name = name.replace(" (" + region + ")", "");

		if (removeLanguage)
		{
			if (name.contains("("))
			{
				String endTag = name.substring(name.lastIndexOf("(") + 1, name.lastIndexOf(")"));
				if (!endTag.equals(region) && (endTag.contains(",") || endTag.contains("+")))
					name = name.replace("(" + endTag + ")", "");
			}
		}

		rowData += "\"" + name.trim() + "\",";

		if (includeRegion)
			rowData += "\"" + region + "\",";

		if (includeSize)
		{
			String size = findQuoteParameter(this.getTag("rom", false, true, entry), "size=");
			if (convertBytes)
				size = this.getSize(Long.parseLong(size));
			rowData += size + ",";
		}

		String crc = "CRC: ";
		String md5 = "MD5: ";
		String sha1 = "SHA1: ";
		String hash = "";

		if (includeCRC)
			crc += findQuoteParameter(this.getTag("rom", false, true, entry), "crc=");
		if (includeMD5)
			md5 += findQuoteParameter(this.getTag("rom", false, true, entry), "md5=");
		if (includeSHA1)
			sha1 += findQuoteParameter(this.getTag("rom", false, true, entry), "sha1=");

		if (mergeHash)
		{
			hash = "\"";
			if (includeCRC)
			{
				hash += crc;
				if (includeMD5)
					hash += " " + md5;
				if (includeSHA1)
					hash += " " + sha1;
			}
			else if (includeMD5)
			{
				hash += md5;
				if (includeSHA1)
					hash += " " + sha1;
			}
			hash += "\"";

		}
		else
		{
			
			if (includeCRC)
				hash += "\""+crc + "\",";
			if (includeMD5)
				hash += "\""+md5 + "\",";
			if (includeSHA1)
				hash += "\""+sha1 + "\",";
		}
		
		rowData += hash;

		String serialID = findQuoteParameter(this.getTag("rom", false, true, entry), "serial=").trim();
		if (serialID.isEmpty())
			serialID = "UNKNOWN";

		return rowData;
	}

	public String makePreview(boolean includeNum, boolean includeSize, boolean convertBytes, boolean includeRegion, boolean removeRegionTag, boolean includeCRC, boolean includeMD5,
			boolean includeSHA1, boolean mergeHash, boolean removeLanguage, boolean removeNum)
	{
		String preview = "";
		int limit = currentDat.getGames().size();
		if(largeFile)
			limit = 500;
		for (int index = 0; index < limit; index++)
			preview += convertGameToRow(currentDat.getGames().get(index), includeNum, includeSize, convertBytes, includeRegion, removeRegionTag, includeCRC, includeMD5,
					includeSHA1, mergeHash, removeLanguage, removeNum) + "\n";
		return preview;
	}

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

	public String getSize(long filesize)
	{

		String size = "";

		if (filesize >= 1024)
		{
			if (filesize >= 1048576)
			{

				size = toMegaBytes(filesize) + "MB";
			}
			else
			{
				size = toKiloBytes(filesize) + "KB";
			}
		}
		else
		{
			size = filesize + "bytes";
		}

		return size;
	}

	public long toKiloBytes(long fileBytes)
	{
		return fileBytes / 1024;
	}

	public long toMegaBytes(long fileBytes)
	{

		return toKiloBytes(fileBytes) / 1024;
	}

	public void setLoadedFile(String text)
	{
		loadedFile = text;
	}
}
