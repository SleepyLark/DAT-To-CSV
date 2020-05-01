package dat.view;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import dat.controller.AppController;

public class AppFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4033452967001426656L;
	private AppController app;
	private ImageIcon icon;
	private AppPanel appPanel;
	
	public AppFrame(AppController app)
	{
		super();
		this.app = app;
		icon = new ImageIcon(getClass().getResource("/dat/view/assets/icon.png"));
		appPanel = new AppPanel(app);
		setupFrame();
	}
	
	/**
	 * sets up frame properties such as size and current panel
	 */
	private void setupFrame()
	{
		this.setContentPane(appPanel);
		this.setIconImage(icon.getImage());
		this.setTitle("DAT to CSV [v0.2]");
		this.setResizable(true);
		this.setVisible(true);
		this.setSize(new Dimension(600,350));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public void updateDisplay()
	{
		appPanel.updateDisplay();
	}
	
	
}
