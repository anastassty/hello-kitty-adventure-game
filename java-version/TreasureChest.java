import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class TreasureChest extends Sprite
{
    public final static int WIDTH = 50;
    public final static int HEIGHT = 50;
    private static BufferedImage chestImage;
    private static BufferedImage lootImage;
    private String state;
    private boolean canBeAdded;
    private int frameNum;
    private boolean added;

    TreasureChest(int x, int y)
    {
        super(x, y, WIDTH, HEIGHT);

        //load chest image
        if(chestImage == null)
		    chestImage = View.loadImage("images/treasurechest.png");
        
        //load loot image
        if(lootImage == null)
		    lootImage = View.loadImage("images/loot.png");
        canBeAdded = false;
        frameNum =  0;
        state = "chest";
        added = false;
    }

    //marshal a treasure chest
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }

    //unmarshalling contrustuctor
    public TreasureChest(Json ob)
    {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), WIDTH, HEIGHT);

        //load chest image
        if(chestImage == null)
		    chestImage = View.loadImage("images/treasurechest.png");
        canBeAdded = false;
        state = "chest";
        frameNum = 0;
        added = false;
    }

    public boolean update()
    {
        if(added) //remove treasure chest after hello kitty collects it
            return false;

        if(state == "loot")
        {
            if(frameNum >= 5) //wait before collecting it
                canBeAdded = true;
            if(frameNum >= 40) //rupee disappears if not collected
            {
                System.out.println("Rupee disappeared");
                return false;
            }
            frameNum++;
            System.out.println("Frame: " + frameNum);
        }
        return true;
    }

    public boolean getCanBeAdded()
    {
        return canBeAdded;
    }

    public String getState()
    {
        return state;
    }

    public boolean getAdded()
    {
        return added;
    }

    public void setAdded(boolean b)
    {
        added = b;
        System.out.println("You collected a rupee!");
    }

    public BufferedImage getImage()
    {
        return chestImage;
    }

    @Override
    public String toString()
    {
        return "Treasure chest (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
    }

    public void drawYourself(Graphics g, int currentRoomX, int currentRoomY) //draw a treasure chest
    {
        if(state == "chest")
            g.drawImage(chestImage, x - currentRoomX, y - currentRoomY, WIDTH, HEIGHT, null);
        if(state == "loot") //show rupee upon collision
            g.drawImage(lootImage, x - currentRoomX, y - currentRoomY, WIDTH, HEIGHT, null);

    }

    public void change()
    {
        state = "loot";
        frameNum = 0;
    }

    @Override
    public boolean isTreasureChest()
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
