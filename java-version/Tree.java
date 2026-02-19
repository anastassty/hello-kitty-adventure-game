import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Tree extends Sprite
{
    public final static int WIDTH = 75;
    public final static int HEIGHT = 75;
    private static BufferedImage image;

    public Tree(int x, int y)
    {
        super(x, y, WIDTH, HEIGHT);

        //load tree image
        if(image == null)
		    image = View.loadImage("images/tree.png");
    }

    //marshal a tree
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }

    //unmarshalling contrustuctor
    public Tree(Json ob)
    {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), WIDTH, HEIGHT);

        //load tree image
        if(image == null)
		    image = View.loadImage("images/tree.png");
    }

    public boolean update()
    {
        return true;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    @Override
    public String toString()
    {
        return "Tree (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) //draw a tree
    {
        g.drawImage(image, x - currentRoomX, y - currentRoomY, WIDTH, HEIGHT, null);
    }

    @Override
    public boolean isTree()
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
