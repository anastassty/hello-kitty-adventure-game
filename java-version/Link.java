import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Link extends Sprite
{
    public final static int WIDTH = 55;
    public final static int HEIGHT = 55;

    private int px, py;
    private double speed;

    //variables for animation
    private static BufferedImage Images[][];
    private int currentFrame = 0;
	private int currentDirection = 0;

    private final int NUM_DIRECTIONS = 4, MAX_IMAGES_PER_DIRECTION = 4;

    Link(int x, int y)
    {
        super(x, y, WIDTH, HEIGHT);
        speed = 15;

        int index = 1;
        Images = new BufferedImage[NUM_DIRECTIONS][MAX_IMAGES_PER_DIRECTION]; //load images
        for(int i = 0; i < NUM_DIRECTIONS; i++)
            for(int j = 0; j < MAX_IMAGES_PER_DIRECTION; j++)
                Images[i][j] = View.loadImage("images/kitty" + (index++) + ".png");
    }

    public int getPX()
    {
        return px;
    }

    public int getPY()
    {
        return py;
    }

    public void setPX()
    {
        px = x;
    }

    public void setPY()
    {
        py = y;
    }

    public BufferedImage getImage()
    {
        return Images[0][0];
    }

    public int getCurrentDirection()
    {
        return currentDirection;
    }

    public void move(String dir)
    {
        if(++currentFrame >= MAX_IMAGES_PER_DIRECTION) //reset animation
			currentFrame = 0;

        if(dir == "left")
        {
            x -= speed;
            currentDirection = 1;
        }
        if(dir == "right")
        {
            x += speed;
            currentDirection = 2;
        }
        if(dir == "up")
        {
            y -= speed;
            currentDirection = 3;
        }
        if(dir == "down")
        {
            y += speed;
            currentDirection = 0;
        }
    }

    public boolean update()
    {
        
        return true;
    }

    @Override
    public String toString()
    {
        return "Link (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
    }

    public void getOut(Sprite s) //fix collision
    {
        if((px + w <= s.getX()) && (x + w >= s.getX())) //left
            x = s.getX() - w -2;
        else if((px >= s.getX() + s.getW()) && (x <= s.getX() + s.getW())) //right
            x = s.getX() + s.getW() + 2;
        else if((py + h <= s.getY()) && (y + h >= s.getY())) //above
            y = s.getY() - h - 2;
        else if((py >= s.getY() + s.getW()) && (y <= s.getY() + s.getH())) //below
            y = s.getY() + s.getH() + 2;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) //draw Link/Hello Kitty
    {
        g.drawImage(Images[currentDirection][currentFrame], x - currentRoomX, y - currentRoomY, WIDTH, HEIGHT, null);
    }

    @Override
    public boolean isLink()
    {
        return true;
    }

    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }



}
