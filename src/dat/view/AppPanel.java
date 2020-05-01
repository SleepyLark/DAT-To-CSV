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
	private JPanel namePanel;
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
		mainPanel = new JPanel(new GridLayout(2, 0));
		appLayout.putConstraint(SpringLayout.WEST, mainPanel, 10, SpringLayout.WEST, this);
		appLayout.putConstraint(SpringLayout.SOUTH, mainPanel, -10, SpringLayout.SOUTH, this);
		ioPanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.NORTH, ioPanel, 10, SpringLayout.NORTH, this);
		regionSelect = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.WEST, regionSelect, 38, SpringLayout.WEST, this);
		region = new JCheckBox("Include Region");
		removeRegionTag = new JCheckBox("Remove Region from name");
		removeRegionTag.setEnabled(false);
		hashPanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.SOUTH, hashPanel, -101, SpringLayout.SOUTH, this);
		size = new JCheckBox("Include Size");
		size.setSelected(true);
		includeNumber = new JCheckBox("Include Release Number");
		removeNum = new JCheckBox("Keep Release Number in name");
		numPanel = new JPanel(new GridLayout(0, 1));
		numPanel.setVisible(false);
		convertBytes = new JCheckBox("Convert bytes");
		convertBytes.setSelected(true);
		sizePanel = new JPanel(new GridLayout(0, 1));
		appLayout.putConstraint(SpringLayout.EAST, sizePanel, -148, SpringLayout.EAST, this);
		appLayout.putConstraint(SpringLayout.WEST, hashPanel, 6, SpringLayout.EAST, sizePanel);
		appLayout.putConstraint(SpringLayout.NORTH, sizePanel, 78, SpringLayout.NORTH, this);
		crc = new JCheckBox("Include CRC");
		md5 = new JCheckBox("Include md5");
		sha1 = new JCheckBox("Include sha1");
		mergeHash = new JCheckBox("Merge into one column");
		mergeHash.setEnabled(false);
		namePanel = new JPanel(new GridLayout(0, 1));
		removeLanguage = new JCheckBox("Remove Language tag if found");
		appLayout.putConstraint(SpringLayout.NORTH, removeLanguage, 131, SpringLayout.NORTH, this);
		appLayout.putConstraint(SpringLayout.SOUTH, regionSelect, -39, SpringLayout.NORTH, removeLanguage);
		appLayout.putConstraint(SpringLayout.WEST, removeLanguage, 67, SpringLayout.WEST, this);

		loadFile = new JButton("Load");
		saveFile = new JButton("Convert");
		preview = new JTextArea("Preview");
		preview.setWrapStyleWord(true);
		preview.setLineWrap(true);
		preview.setEditable(false);
		previewPane = new JScrollPane();
		appLayout.putConstraint(SpringLayout.NORTH, previewPane, 10, SpringLayout.NORTH, this);
		appLayout.putConstraint(SpringLayout.WEST, previewPane, 0, SpringLayout.WEST, mainPanel);
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
		mainPanel.add(hashPanel);
		mainPanel.add(sizePanel);
		mainPanel.add(namePanel);
		mainPanel.add(numPanel);
		numPanel.add(includeNumber);
		numPanel.add(removeNum);
		namePanel.add(removeLanguage);
		sizePanel.add(size);
		sizePanel.add(convertBytes);
		hashPanel.add(crc);
		hashPanel.add(md5);
		hashPanel.add(sha1);
		hashPanel.add(mergeHash);
		regionSelect.add(region);
		regionSelect.add(removeRegionTag);
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
				updateDisplay();
			}
		});

		saveFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent click)
			{
				app.saveFile(includeNumber.isSelected(), size.isSelected(), convertBytes.isSelected(), region.isSelected(), removeRegionTag.isSelected(), crc.isSelected(), md5.isSelected(),
						sha1.isSelected(), mergeHash.isSelected(), removeLanguage.isSelected(), removeNum.isSelected());
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

	protected void updateDisplay()
	{
		preview.setText(app.getPreview(includeNumber.isSelected(), size.isSelected(), convertBytes.isSelected(), region.isSelected(), removeRegionTag.isSelected(),
				crc.isSelected(), md5.isSelected(), sha1.isSelected(), mergeHash.isSelected(), removeLanguage.isSelected(), !removeNum.isSelected()));
		repaint();
	}
}
