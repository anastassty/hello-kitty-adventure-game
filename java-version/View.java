import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import javax.swing.JButton;

public class View extends JPanel
{
	private Model model;

	private int currentRoomX; 
	private int currentRoomY;

	public View(Controller c, Model m)
	{
		c.setView(this);
		model = m;

		//variables used to switch rooms
		currentRoomX = 0;
		currentRoomY = 0;
	}

	public int getCurrentRoomX()
	{
		return currentRoomX;
	}

	public int getCurrentRoomY()
	{
		return currentRoomY;
	}

	//load image
	public static BufferedImage loadImage(String filename)
	{
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(new File(filename));
		}
		catch(Exception e)
		{
			System.out.println(filename + " can't be found in this directory");
			e.printStackTrace(System.err);
			System.exit(1);
		}		
		return image;
	}

	//move camera if Link goes to another room
	public void moveCamera()
	{
		if(model.getTurtleX() < currentRoomX)
			currentRoomX -= Game.WINDOW_WIDTH;

		if(model.getTurtleX() >= currentRoomX + Game.WINDOW_WIDTH)
			currentRoomX += Game.WINDOW_WIDTH;

		if(model.getTurtleY() >= currentRoomY + Game.WINDOW_HEIGHT)
			currentRoomY += Game.WINDOW_HEIGHT;

		if(model.getTurtleY() < currentRoomY)
			currentRoomY -= Game.WINDOW_HEIGHT;
	}

	//GUI
	public void paintComponent(Graphics g)
	{
		moveCamera();
		//bg
		g.setColor(new Color(127, 228, 165));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for(int i = 0; i < model.getNumSprites(); i++) //draw all sprites
		{
			Sprite sprite = model.getSprite(i);
			sprite.drawYourself(g, currentRoomX, currentRoomY);
		}

		//turn on edit mode
		if(model.getEditMode() == true)
		{
			if(model.getAddMapItem() == true)	//add mode
			{
				g.setColor(new Color(0, 255, 0));
				g.fillRect(0, 0, 100, 100);
			}
			else if(model.getAddMapItem() == false)		//remove mode
			{
				g.setColor(new Color(255, 0, 0));
				g.fillRect(0, 0, 100, 100);
			}

			Sprite icon = model.getIcon(model.getItemNum()); //show icon
			g.drawImage(icon.getImage(), 12, 12, 75, 75, null);
		}
	}
}
