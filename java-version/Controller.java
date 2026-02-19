import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Controller implements ActionListener, MouseListener, KeyListener, MouseMotionListener
{
	private boolean keepGoing;
	private View view;
	private Model model;

	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyUp;
	private boolean keyDown;

	public Controller(Model m)
	{
		model = m;
		keepGoing = true;
		Json loadObject = Json.load("map.json");
		model.unmarshal(loadObject);
		System.out.println("map.json loaded.");
	}

	//reference view
	public void setView(View v)
	{
		view = v;
	}

	//exit the game if keepGoing is false
	public boolean update()
	{	
		model.setPX();
		model.setPY();
		if (keyRight) //move Link/Hello Kitty
			model.tellLinkToMove("right");
		if (keyLeft)
			model.tellLinkToMove("left");
		if (keyDown)
			model.tellLinkToMove("down");
		if (keyUp)
			model.tellLinkToMove("up");

		//the Controller keeps track of whether or not we have quit the program and
		//returns this value to the Game engine of whether or not to continue the game loop
		return keepGoing;
	}

	//draw or remove an object
	public void mousePressed(MouseEvent e)
	{
		if(e.getY() <= 100 && e.getX() <= 100)
			model.iterateItemNum();
		else
		{
			model.drawObject(e.getX() + view.getCurrentRoomX(), e.getY() + view.getCurrentRoomY());
			model.removeObject(e.getX() + view.getCurrentRoomX(), e.getY() + view.getCurrentRoomY());
		}
	}

	//draw or remove objects 
	public void mouseDragged(MouseEvent e)
	{
		model.drawObject(e.getX() + view.getCurrentRoomX(), e.getY() + view.getCurrentRoomY());
		model.removeObject(e.getX() + view.getCurrentRoomX(), e.getY() + view.getCurrentRoomY());
	}

	//keys used for moving Link/Hello Kitty
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: 
				keyRight = true; 
				break;
			case KeyEvent.VK_LEFT: 
				keyLeft = true; 
				break;
			case KeyEvent.VK_UP: 
				keyUp = true; 
				break;
			case KeyEvent.VK_DOWN: 
				keyDown = true; 
				break;
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
				keyRight = false; 
				break;
			case KeyEvent.VK_LEFT: 
				keyLeft = false; 
				break;
			case KeyEvent.VK_UP: 
				keyUp = false; 
				break;
			case KeyEvent.VK_DOWN: 
				keyDown = false; 
				break;
			case KeyEvent.VK_SPACE:
				model.throwBoomerang();	//throw boomerang
				break;
			case KeyEvent.VK_ESCAPE:     //quit game
				keepGoing = false;
		}
		char c = Character.toLowerCase(e.getKeyChar());
		switch(c)
		{
			case('q'):	// quit game
				keepGoing = false;
				break;

			case('e'): //toggle edit mode
				if(model.getEditMode() == false)
				{
					model.setAddMapItem(true);
					model.setEditMode(true);
				}
				else if(model.getEditMode() == true)
				{
					model.setEditMode(false);
				}
				break;

			case('a'):	//turn on add mode
				model.setAddMapItem(true);
				break;

			case('r'):	//turn on remove mode
				model.setAddMapItem(false);
				break;

			case('c'):	//clear the map
				model.clearMap();
				break;
			
			case('l'): //load map
				Json loadObject = Json.load("map.json");
				model.unmarshal(loadObject);
				System.out.println("map.json loaded.");
				break;

			case('s'): //save map
				Json saveObject = model.marshal();
				saveObject.save("map.json");
				System.out.println("saved map.json file.");
				break;
		}

	}

	public void keyTyped(KeyEvent e)
	{    

	}

	public void actionPerformed(ActionEvent e)
	{

	}

	public void mouseMoved(MouseEvent e)
	{
		
	}

	public void mouseReleased(MouseEvent e)
	{

	}

	public void mouseEntered(MouseEvent e)
	{

	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mouseClicked(MouseEvent e)
	{
	
	}
}
