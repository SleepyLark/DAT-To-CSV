package dat.view;

import java.awt.Dimension;

import javax.swing.JFrame;

import dat.controller.AppController;

public class AppFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4033452967001426656L;
	private AppController app;
	private AppPanel appPanel;
	
	public AppFrame(AppController app)
	{
		super();
		this.app = app;
		appPanel = new AppPanel(app);
		setupFrame();
	}
	
	/**
	 * sets up frame properties such as size and current panel
	 */
	private void setupFrame()
	{
		this.setContentPane(appPanel);
		this.setTitle("DAT to CSV");
		this.setResizable(true);
		this.setVisible(true);
		this.setSize(new Dimension(600,350));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * resizes the frame to fit the current image size and calls the panel's {@link pix.view.GlitchMasterPanel#updateDisplay() updateDisplay()}
	 */
	public void updateDisplay()
	{
		appPanel.updateDisplay();
	}
	
	
	/**
	 * puts the frame back to the center of the screen
	 */
	public void recenter()
	{
		this.setLocationRelativeTo(null);
	}
}
