//Name: Anastasiia Tykina
//Student ID: 100433562
//Date: 10/15/2025
//Program Description: this program creates a simple game with 4 rooms surrounded by trees. Additional trees and treasure chests can be added/removed using the mouse/keyboard. 
//The player controls Hello Kitty. Hello Kitty can move around, throw boomerangs and collect rupees. Boomerangs disappear when they hit trees/treasure chests.

import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Boomerang extends Sprite
{
    public final static int WIDTH = 25;
    public final static int HEIGHT = 25;
    //private static BufferedImage image;
    private static BufferedImage Images[];
    private final int NUM_FRAMES = 4;
    private int currentFrame = 0;
    private double speed;
    private int direction;
    private boolean remove;

    public Boomerang(int x, int y)
    {
        super(x, y, WIDTH, HEIGHT);

        //load boomerang image
        if(Images == null)
        {
            int index = 1;
            Images = new BufferedImage[NUM_FRAMES]; //load images
            for(int i = 0; i < NUM_FRAMES; i++)
                Images[i] = View.loadImage("images/boomerang" + (index++) + ".png");
        }

        speed = 25;
        remove = false;
    }

    //marshal a boomerang
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }

    public boolean update()
    {
        if(remove)
            return false;

        if(++currentFrame >= NUM_FRAMES) //reset animation
			currentFrame = 0;

        if(direction == 2)
            x += speed;
        else if(direction == 1)
            x -= speed;
        else if(direction == 0)
            y += speed;
        else if(direction == 3)
            y -= speed;
        
        return true;
    }

    public BufferedImage getImage()
    {
        return Images[0];
    }

    public void setRemove(boolean r)
    {
        remove = r;
    }

    @Override
    public String toString()
    {
        return "Boomerang (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) //draw a boomerang
    {
        g.drawImage(Images[currentFrame], x - currentRoomX, y - currentRoomY, WIDTH, HEIGHT, null);
    }

    public void fly(int dir) //set direction of the boomerang
    {
        direction = dir;
    }

    @Override
    public boolean isBoomerang()
    {
        return true;
    }

    public void getOut(Sprite s)
    {

    }

    public int getPY()
    {
        return y;
    }

    public int getPX()
    {
        return x;
    }
}