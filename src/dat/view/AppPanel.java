package dat.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import dat.controller.AppController;
import java.awt.Font;

public class AppPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7314113183193373715L;
	private AppController app;
	private SpringLayout appLayout;
	private JPanel ioPanel;
	private JPanel regionSelect;
	private JButton loadFile;
	private JButton saveFile;
	private JCheckBox includeNumber;
	private JCheckBox removeNum;
	private JPanel numPanel;
	private JCheckBox removeLanguage;
	private JCheckBox region;
	private JCheckBox removeRegionTag;
	private JCheckBox size;
	private JCheckBox convertBytes;
	private JCheckBox includeSerial;
	private JCheckBox removeMissingSerial;
	private JPanel serialPanel;
	private JPanel sizePanel;
	private JPanel hashPanel;
	private JCheckBox crc;
	private JCheckBox md5;
	private JCheckBox sha1;
	private JCheckBox mergeHash;
	private JTextArea preview;
	private JScrollPane previewPane;
	private JPanel mainPanel;

	public AppPanel(AppController app)
	{
		super();
		this.app = app;
		appLayout = new SpringLayout();
		GridLayout gl_mainPanel = new GridLayout(0, 4);
		gl_mainPanel.setVgap(5);
		gl_mainPanel.setHgap(5);
		mainPanel = new JPanel(gl_mainPanel);
		appLayout.putConstraint(SpringLayout.WEST, mainPanel, 10, SpringLayout.WEST, this);
		appLayout.putConstraint(SpringLayout.SOUTH, mainPanel, -10, SpringLayout.SOUTH, this);
		ioPanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.NORTH, ioPanel, 10, SpringLayout.NORTH, this);
		regionSelect = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.WEST, regionSelect, 38, SpringLayout.WEST, this);
		region = new JCheckBox("Include region");
		region.setToolTipText("");
		size = new JCheckBox("Include size\r\n");
		size.setSelected(true);
		includeNumber = new JCheckBox("Include release number\r\n");
		removeNum = new JCheckBox("Keep release number in name\r\n");
		numPanel = new JPanel(new GridLayout(0, 1));
		numPanel.setVisible(false);
		convertBytes = new JCheckBox("Convert bytes");
		convertBytes.setSelected(true);
		sizePanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.EAST, sizePanel, -148, SpringLayout.EAST, this);
		appLayout.putConstraint(SpringLayout.NORTH, sizePanel, 78, SpringLayout.NORTH, this);
		includeSerial = new JCheckBox("Include serial");
		removeMissingSerial = new JCheckBox("Remove missing/unknown");
		serialPanel = new JPanel(new GridLayout(0,1));
		serialPanel.setVisible(false);
		loadFile = new JButton("Load");
		saveFile = new JButton("Convert");
		saveFile.setEnabled(false);
		preview = new JTextArea("Preview");
		preview.setWrapStyleWord(true);
		preview.setLineWrap(true);
		preview.setEditable(false);
		previewPane = new JScrollPane();
		appLayout.putConstraint(SpringLayout.EAST, mainPanel, 0, SpringLayout.EAST, previewPane);
		appLayout.putConstraint(SpringLayout.NORTH, previewPane, 10, SpringLayout.NORTH, this);
		appLayout.putConstraint(SpringLayout.WEST, previewPane, 10, SpringLayout.WEST, this);
		appLayout.putConstraint(SpringLayout.SOUTH, previewPane, 0, SpringLayout.NORTH, mainPanel);
		appLayout.putConstraint(SpringLayout.EAST, previewPane, -5, SpringLayout.WEST, ioPanel);
		appLayout.putConstraint(SpringLayout.WEST, ioPanel, 6, SpringLayout.EAST, preview);
		appLayout.putConstraint(SpringLayout.SOUTH, preview, 0, SpringLayout.SOUTH, ioPanel);
		appLayout.putConstraint(SpringLayout.WEST, preview, 10, SpringLayout.WEST, this);
		appLayout.putConstraint(SpringLayout.EAST, preview, -87, SpringLayout.EAST, this);
		appLayout.putConstraint(SpringLayout.NORTH, preview, 10, SpringLayout.NORTH, this);
		preview.setFont(new Font("Arial", Font.PLAIN, 13));

		setupPanel();
		setupLayout();
		setupListeners();
		setupScrollPane();
	}

	private void setupPanel()
	{
		this.setLayout(appLayout);
		this.add(mainPanel);
		this.add(ioPanel);
		mainPanel.add(regionSelect);
		mainPanel.add(sizePanel);
		hashPanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.SOUTH, hashPanel, -101, SpringLayout.SOUTH, this);
		appLayout.putConstraint(SpringLayout.WEST, hashPanel, 6, SpringLayout.EAST, sizePanel);
		crc = new JCheckBox("Include CRC");
		md5 = new JCheckBox("Include MD5");
		sha1 = new JCheckBox("Include SHA1");
		mergeHash = new JCheckBox("Merge into one column");
		mergeHash.setEnabled(false);
		mainPanel.add(hashPanel);
		hashPanel.add(crc);
		hashPanel.add(md5);
		hashPanel.add(sha1);
		hashPanel.add(mergeHash);
		mainPanel.add(numPanel);
		mainPanel.add(serialPanel);
		serialPanel.add(includeSerial);
		serialPanel.add(removeMissingSerial);
		numPanel.add(includeNumber);
		numPanel.add(removeNum);
		sizePanel.add(size);
		sizePanel.add(convertBytes);
		regionSelect.add(region);
		removeRegionTag = new JCheckBox("Remove region from name");
		removeRegionTag.setToolTipText("(i.e. \"Super Mario 64 (USA)\" -> \"Super Mario 64\")");
		removeRegionTag.setEnabled(false);
		regionSelect.add(removeRegionTag);
		removeLanguage = new JCheckBox("Remove language tag\r\n");
		removeLanguage.setToolTipText("Not 100% accurate, but will detect most tags");
		appLayout.putConstraint(SpringLayout.NORTH, removeLanguage, 131, SpringLayout.NORTH, this);
		appLayout.putConstraint(SpringLayout.SOUTH, regionSelect, -39, SpringLayout.NORTH, removeLanguage);
		appLayout.putConstraint(SpringLayout.WEST, removeLanguage, 67, SpringLayout.WEST, this);
		regionSelect.add(removeLanguage);
		ioPanel.add(loadFile);
		ioPanel.add(saveFile);
		this.add(previewPane);

	}

	private void setupScrollPane()
	{

		previewPane.setViewportView(preview);
	}

	private void setupLayout()
	{

	}

	private void setupListeners()
	{
		loadFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				preview.setText("Loading...");
				app.loadFile();
				numPanel.setVisible(app.hasReleaseNumber());
				includeNumber.setSelected(app.hasReleaseNumber());
				serialPanel.setVisible(app.hasSerial());
				includeSerial.setSelected(app.hasSerial());
				updateDisplay();
				saveFile.setEnabled(true);
			}
		});

		saveFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				app.saveFile();
			}
		});

		includeNumber.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});

		removeNum.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});

		removeLanguage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});

		region.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				removeRegionTag.setEnabled(region.isSelected());
				removeRegionTag.setSelected(false);
				updateDisplay();
			}
		});

		removeRegionTag.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});
		
		includeSerial.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				removeMissingSerial.setEnabled(includeSerial.isSelected());
				removeMissingSerial.setSelected(false);
				updateDisplay();
			}
		});
		removeMissingSerial.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});

		size.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				convertBytes.setEnabled(size.isSelected());
				convertBytes.setSelected(true);
				updateDisplay();
			}

		});

		convertBytes.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});

		crc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				mergeHash.setEnabled((crc.isSelected() && (sha1.isSelected() || md5.isSelected())) || (sha1.isSelected() && md5.isSelected()));
				if (!mergeHash.isEnabled())
				{
					mergeHash.setSelected(false);
				}
				updateDisplay();
			}
		});

		md5.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				mergeHash.setEnabled((md5.isSelected() && (crc.isSelected() || sha1.isSelected())) || (crc.isSelected() && sha1.isSelected()));
				if (!mergeHash.isEnabled())
				{
					mergeHash.setSelected(false);
				}
				updateDisplay();
			}
		});

		sha1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				mergeHash.setEnabled((sha1.isSelected() && (crc.isSelected() || md5.isSelected())) || (crc.isSelected() && md5.isSelected()));
				if (!mergeHash.isEnabled())
				{
					mergeHash.setSelected(false);
				}
				updateDisplay();
			}
		});
		
		mergeHash.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				updateDisplay();
			}
		});
	}

	/**
	 * takes boolean values from checkboxes and put it into the array
	 */
	private void parseBooleanData()
	{
		app.setSelectionData(app.INCLUDE_NUM, includeNumber.isSelected());
		app.setSelectionData(app.INCLUDE_SIZE, size.isSelected());
		app.setSelectionData(app.CONVERT_BYTE, convertBytes.isSelected());
		app.setSelectionData(app.INCLUDE_REGION, region.isSelected());
		app.setSelectionData(app.REMOVE_REGION_TAG, removeRegionTag.isSelected());
		app.setSelectionData(app.INCLUDE_CRC, crc.isSelected());
		app.setSelectionData(app.INCLUDE_MD5, md5.isSelected());
		app.setSelectionData(app.INCLUDE_SHA1, sha1.isSelected());
		app.setSelectionData(app.MERGE_HASH, mergeHash.isSelected());
		app.setSelectionData(app.REMOVE_NUM, !removeNum.isSelected());//inverse
		app.setSelectionData(app.INCLUDE_SERIAL, includeSerial.isSelected());
		app.setSelectionData(app.REMOVE_MISSING_SERIAL, removeMissingSerial.isSelected());
		app.setSelectionData(app.REMOVE_LANGUAGE, removeLanguage.isSelected());
	}
	protected void updateDisplay()
	{
		parseBooleanData();
		preview.setText(app.getPreview());
		repaint();
	}
}
