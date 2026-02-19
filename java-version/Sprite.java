import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Sprite
{
    protected int x, y, w, h;

    public Sprite(int x , int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public abstract boolean update();

    public abstract Json marshal();

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getW()
    {
        return w;
    }

    public int getH()
    {
        return h;
    }

    public abstract int getPX();

    public abstract int getPY();
    
    public boolean isLink()
    {
        return false;
    }

    public boolean isTree()
    {
        return false;
    }

    public boolean isTreasureChest()
    {
        return false;
    }

    public boolean isBoomerang()
    {
        return false;
    }

    //return true if you click on a sprite
    public boolean clickedOn(int mouseX, int mouseY)
    {
        if(mouseX >= this.x && mouseX <= (this.x + this.w) && 
           mouseY >= this.y && mouseY <= (this.y + this.h))
            return true;
        return false;
    }

    public abstract void drawYourself(Graphics g, int currentRoomX, int currentRoomY);

    public abstract void getOut(Sprite s);

    public abstract BufferedImage getImage();

}
