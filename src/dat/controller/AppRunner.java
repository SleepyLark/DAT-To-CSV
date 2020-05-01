package dat.controller;

public class AppRunner
{

	public static void main(String args[])
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				AppController app = new AppController();
				app.start();
			}
		});
		
	}
}
