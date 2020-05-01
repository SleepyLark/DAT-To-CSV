package dat.model;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import dat.controller.AppController;

import java.util.Calendar;
import java.util.Scanner;

/**
 * I/O handler reused from other projects
 * @author Skylark
 *
 */
public class LucarIO
{
	private AppController app;
	private String currentOS;
	private String startPath;
	private String recentPath;

	public enum ChooseOption
	{
		FILE, DIRECTORY;
	}

	public LucarIO(AppController app)
	{
		this.app = app;

		currentOS = System.getProperty("os.name");
		startPath = System.getProperty("user.dir");
		recentPath = startPath;
	}

	private String getTime()
	{
		Calendar date = Calendar.getInstance();
		return date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH) + "-" + date.get(Calendar.HOUR) + date.get(Calendar.MINUTE);
	}

	public void saveString(String textToSave)
	{
		try
		{
			String path = this.fileChooser(ChooseOption.DIRECTORY, "Where do you want to save?");
			if (path != null)
			{
				String name = JOptionPane.showInputDialog(null, "Enter filename");
				File temp = new File(path + "/" + name + ".csv");
				Scanner reader = new Scanner(textToSave);
				PrintWriter output = new PrintWriter(temp);

				while (reader.hasNext())
				{
					output.println(reader.nextLine());
				}

				JOptionPane.showMessageDialog(null, "File saved to: " + path + " successfully.");
				output.close();
				reader.close();
			}

		}
		catch (IOException error)
		{
			JOptionPane.showMessageDialog(null, "Error: Couldn't save.");
			app.errorHandler(error);
		}

	}

	public String loadString()
	{
		String data = null;
		try
		{
			String path = this.fileChooser(ChooseOption.FILE, "Choose .DAT");

			if (path != null)
			{
				File fileChoosen = new File(path);
				Scanner reader = new Scanner(fileChoosen);
				Scanner loader = new Scanner(fileChoosen);
				int maxLineSize = 0;
				int counter = 0;
				data = "";

				while (loader.hasNext())
				{
					maxLineSize++;
					loader.nextLine();
				}
				loader.close();

				while (reader.hasNextLine())
				{
					counter++;
					data += reader.nextLine() + "\n";
					int lastNumber = 0;
					int current = (int) (Math.round(((double) counter) / (maxLineSize) * 100));
					if (current != lastNumber)
					{
						// app.print((current + "%"));
						lastNumber = current;
					}
				}

				reader.close();
				JOptionPane.showMessageDialog(null, "File loaded successfully");
				app.print("Done!");
			}
		}
		catch (Exception e)
		{
			app.errorHandler(e);
		}

		return data;
	}

	public void setRecentPath(String path)
	{
		if (doesExists(path))
			recentPath = path;
	}

	public String fileChooser(ChooseOption choice, String message)
	{
		String path = null;
		int result = -99;
		JFileChooser explore = new JFileChooser(recentPath);
		explore.setFileFilter(new ShowFilter());
		if (message != null)
			explore.setDialogTitle(message);

		if (choice == ChooseOption.FILE)
		{
			result = explore.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				path = explore.getSelectedFile().getAbsolutePath();
				recentPath = explore.getCurrentDirectory().getPath();

			}
		}
		else if (choice == ChooseOption.DIRECTORY)
		{
			explore.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			result = explore.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				path = explore.getSelectedFile().getAbsolutePath();
				recentPath = explore.getCurrentDirectory().getPath();
				System.out.println(path);
			}
		}

		return path;
	}

	public boolean doesExists(String path)
	{
		boolean exists = false;

		File temp = new File(path);

		exists = temp.exists();

		return exists;
	}

	public boolean isEmpty(File folder)
	{
		boolean empty = false;

		if (folder.isDirectory())
		{
			if (folder.list().length < 0)
			{
				empty = true;
			}
		}

		return empty;
	}

	public boolean isHidden(File filename)
	{
		boolean hidden = false;

		if (filename.getName().startsWith("."))
		{
			hidden = true;
		}

		return hidden;
	}

	public String getReadableName(String path)
	{
		int startIndex = 0;
		int endIndex = path.lastIndexOf(".");
		if (path.contains(File.separator))
		{
			startIndex = path.lastIndexOf(File.separator) + 1;
		}
		if (endIndex < 0)
		{
			endIndex = path.length();
		}

		return path.substring(startIndex, endIndex);

	}

	public String getReadableName(File temp)
	{
		return getReadableName(temp.getPath());
	}

	public String getSize(String path)
	{
		File temp = new File(path);
		return getSize(temp);
	}

	public String getSize(File fileToCheck)
	{

		long filesize = fileToCheck.length();
		String size = "";

		if (filesize >= 1024)
		{
			if (filesize >= 1048576)
			{
				if (filesize >= 1073741824)
				{
					size = toGigaBytes(filesize) + " GB";
				}
				else
				{
					size = toMegaBytes(filesize) + " MB";
				}
			}
			else
			{
				size = toKiloBytes(filesize) + " KB";
			}
		}
		else
		{
			size = filesize + " bytes";
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

	public long toGigaBytes(long fileBytes)
	{
		return toMegaBytes(fileBytes) / 1024;
	}

	public String removeExtension(String path, String extension)
	{
		File temp = new File(path);

		String list = "";
		try
		{
			Scanner read = new Scanner(temp);
			while (read.hasNext())
			{
				String current = read.nextLine();
				if (current.contains(extension))
				{
					current = current.substring(0, current.indexOf(extension));
				}
				list += current + "\n";
			}
			read.close();
		}
		catch (IOException error)
		{
			app.errorHandler(error);
		}

		return list;
	}

	public String getExtension(String path)
	{
		int startIndex = path.lastIndexOf(".");
		String end = "No Extension";
		if (startIndex > 0)
		{
			end = path.substring(startIndex);
		}

		return end;
	}

	public long getLastModified(File currentFile)
	{
		return currentFile.lastModified();

	}

	public long getLastModified(String path)
	{
		long time = -99;
		if (doesExists(path))
		{
			time = getLastModified(new File(path));
		}

		return time;
	}
}

class ShowFilter extends FileFilter
{
	private String[] fileExtensions = { ".dat", ".txt", ".xml" };

	/**
	 * A filter that contains only acceptable image files
	 */
	public ShowFilter()
	{
		super();
	}

	public boolean accept(File pathname)
	{
		boolean fileOk = false;

		for (String extension : fileExtensions)
		{
			if (pathname.getName().toLowerCase().endsWith(extension) || pathname.isDirectory())
			{
				fileOk = true;
			}
		}

		return fileOk;
	}

	/**
	 * converts the list of extensions to a human-readable String
	 * 
	 * @return a readable String of extensions
	 */
	private String printExtensions()
	{
		String list = "";

		for (int index = 0; index < fileExtensions.length; index++)
		{
			list += "*" + fileExtensions[index];
			if (index < fileExtensions.length - 1)
			{
				list += ", ";
			}
		}

		return list;
	}

	public String getDescription()
	{
		return printExtensions();
	}

}
