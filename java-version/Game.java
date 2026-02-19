//Name: Anastasiia Tykina
//Student ID: 100433562
//Date: 10/15/2025
//Program Description: this program creates a simple game with 4 rooms surrounded by trees. Additional trees and treasure chests can be added/removed using the mouse/keyboard. 
//The player controls Hello Kitty. Hello Kitty can move around, throw boomerangs and collect rupees. Boomerangs disappear when they hit trees/treasure chests.

import javax.swing.JFrame;
import java.awt.Toolkit;

public class Game extends JFrame
{
	//window size
	public final static int WINDOW_WIDTH = 950;
	public final static int WINDOW_HEIGHT = 700;

	private boolean keepGoing;
	
	Model model = new Model();
	Controller controller = new Controller(model);
	View view = new View(controller, model);

	public Game()
	{	
		keepGoing = true;
		
		this.setTitle("A4 - Hello Kitty");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		view.addMouseListener(controller);
		view.addMouseMotionListener(controller);
		this.addKeyListener(controller);
	}

	public void run()
	{
		do
		{
			keepGoing = controller.update();
			model.update();
			view.repaint(); // This will indirectly call View.paintComponent
			Toolkit.getDefaultToolkit().sync(); // Updates screen

			// Go to sleep for 50 milliseconds
			try
			{
				Thread.sleep(50);
			} 
			catch(Exception e) 
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		while(keepGoing);
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
		System.exit(0);
	}
}
