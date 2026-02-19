package com.example.game2;

import java.util.ArrayList;
import java.util.Iterator;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.DisplayMetrics;
/*
Name: Anastasiia Tykina
Student ID: 100433562
Date: 12/01/2025
Program Description: this program creates a simple game with 4 rooms surrounded by trees.
The player controls Hello Kitty using on screen buttons. Hello Kitty can move around, throw shadow shurikens and collect bats.
Shurikens disappear when they hit trees/treasure chests.
The game has been tested on a Pixel 4a emulator.
*/

public class MainActivity extends AppCompatActivity
{
    Model model;
    GameView view;
    GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new Model();
        view = new GameView(this, model);
        controller = new GameController(model, view);
        setContentView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        controller.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        controller.pause();
    }

    abstract static class Sprite
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

        public abstract void drawYourself(Canvas g, int currentRoomX, int currentRoomY, Paint p);

        public boolean isOnScreen(int x, int y, int w, int h, int roomX, int roomY, int screenW, int screenH)
        {
            boolean onScreen = true;
            if(x + w < roomX)
                onScreen = false;
            else if(x > roomX + screenW)
                onScreen = false;
            else if(y + h < roomY)
                onScreen = false;
            else if(y > roomY + screenH)
                onScreen = false;
            return onScreen;
        }
    }

    static class Link extends Sprite
    {
        public static int WIDTH = 80;
        public static int HEIGHT = 80;

        private int px, py;
        private double speed;

        //variables for animation
        private int currentFrame = 0;
        private int currentDirection = 0;

        public final static int NUM_DIRECTIONS = 4, MAX_IMAGES_PER_DIRECTION = 4;

        Link(int x, int y)
        {
            super(x, y, WIDTH, HEIGHT);
            w = WIDTH;
            h = HEIGHT;
            speed = 10;
        }

        public void setPX()
        {
            px = x;
        } //set previous position

        public void setPY()
        {
            py = y;
        }

        public int getCurrentDirection()
        {
            return currentDirection;
        }

        public void move(String dir)
        {
            if(++currentFrame >= MAX_IMAGES_PER_DIRECTION) //reset animation
                currentFrame = 0;

            if(dir == "left") //move hello kitty
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
            else if((py >= s.getY() + s.getH()) && (y <= s.getY() + s.getH())) //below
                y = s.getY() + s.getH() + 2;
        }

        public void drawYourself(Canvas g, int currentRoomX, int currentRoomY, Paint p) //draw Link/Hello Kitty
        {
            g.drawBitmap(GameView.kittyFrames[currentDirection][currentFrame], x - currentRoomX, y - currentRoomY, p);
        }

        @Override
        public boolean isLink()
        {
            return true;
        }
    }

    static class Tree extends Sprite
    {
        public final static int WIDTH = (int) (75*GameView.getScale()); //width and height set and scaled based on screen resolution
        public final static int HEIGHT = (int) (75*GameView.getScale());

        public Tree(int x, int y)
        {
            super(x, y, WIDTH, HEIGHT);
        }

        public boolean update()
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Tree (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
        }

        public void drawYourself(Canvas g, int currentRoomX, int currentRoomY, Paint p) //draw a tree
        {
            g.drawBitmap(GameView.tree, x - currentRoomX, y - currentRoomY, p);
        }

        @Override
        public boolean isTree()
        {
            return true;
        }
    }

    static class TreasureChest extends Sprite
    {
        public final static int WIDTH = (int) (50*GameView.getScale()); //width and height set and scaled based on screen resolution
        public final static int HEIGHT = (int) (50*GameView.getScale());
        private String state;
        private boolean canBeAdded;
        private int frameNum;
        private boolean added;

        TreasureChest(int x, int y)
        {
            super(x, y, WIDTH, HEIGHT);
            canBeAdded = false;
            frameNum =  0;
            state = "chest";
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
                if(frameNum >= 40) //rouge disappears if not collected
                    return false;
                frameNum++;
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

        public void setAdded(boolean b)
        {
            added = b;
        }

        @Override
        public String toString()
        {
            return "Treasure chest (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
        }

        public void drawYourself(Canvas g, int currentRoomX, int currentRoomY, Paint p) //draw a treasure chest
        {
            if(state == "chest")
                g.drawBitmap(GameView.treasureChest, x - currentRoomX, y - currentRoomY, p);
            if(state == "loot") //show rouge upon collision
                g.drawBitmap(GameView.loot, x - currentRoomX, y - currentRoomY, p);

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
    }

    static class Boomerang extends Sprite
    {
        public final static int WIDTH = 50;
        public final static int HEIGHT = 50;
        public final static int NUM_FRAMES = 4;
        private int currentFrame = 0;
        private double speed;
        private int direction;
        private boolean remove;

        public Boomerang(int x, int y)
        {
            super(x, y, WIDTH, HEIGHT);
            speed = 25;
            remove = false;
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

        public void setRemove(boolean r)
        {
            remove = r;
        }

        @Override
        public String toString()
        {
            return "Boomerang (x,y) = (" + x + ", " + y + ") , w = " + w + ", h = " + h;
        }

        public void drawYourself(Canvas g, int currentRoomX, int currentRoomY, Paint p) //draw a boomerang
        {
            g.drawBitmap(GameView.boomerangFrames[currentFrame], x - currentRoomX, y - currentRoomY, p);
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
    }

    static class GameButton {
        private int x, y;
        private static final int w = 150;
        private static final int h = 150;
        private boolean pressed;
        private static final int colorNormal = Color.YELLOW; //original color of buttons
        private static final int colorPressed = Color.BLACK; //color when pressed
        private String text;

        public GameButton(int x, int y, String text) {
            this.x = x;
            this.y = y;
            this.text = text;
            pressed = false;
        }

        public boolean isPressed(float coordX, float coordY) {
            if(coordX >= x && coordX <= x + w && coordY >= y && coordY <= y + h) //return true if button is pressed
                return true;
            return false;
        }

        public void draw(Canvas c, Paint p) {
            //set color
            p.setColor(pressed ? colorPressed : colorNormal);
            p.setStyle(Paint.Style.FILL);

            //draw buttons
            c.drawRoundRect(x, y, x + w, y + h, 30, 30, p);

            //draw text
            p.setColor(Color.BLACK);
            p.setTextSize(100);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText(text, x + w / 2f, y + h / 2f + 15, p); //do float division to center text on buttons
        }
    }

    static class Model
    {
        //instead of creating a new json class I just removed "x": and "y": with ctrl+h and created double arrays for chests and trees using data from the original map.json file
        int[][] chestData = {{617,90},{165,914},{1665,-135},{1597,-592},{773,468},{1217,842},{93,-588},{4,-1073},{88,-136}};
        int[][] treeData = {{75,0},{150,0},{225,0},{300,0},{375,0},{450,0},{525,0},{600,0},{675,0},{750,0},{825,0},{900,0},{900,75},{900,600},{825,600},{750,600},{675,600},{600,600},{525,600},{450,600},{375,600},{0,75},{0,150},{0,225},{0,300},{0,375},{0,450},{0,525},{0,600},{300,600},{225,600},{225,675},{300,675},{375,675},{450,675},{525,675},{600,675},{675,675},{750,675},{825,675},{900,675},{0,675},{0,825},{0,900},{0,975},{0,1050},{0,1125},{0,1200},{0,1275},{75,1275},{675,1275},{750,1275},{825,1275},{900,1275},{900,1200},{900,1125},{900,900},{900,825},{900,750},{975,600},{1050,600},{1125,600},{1200,600},{1275,600},{1350,600},{1425,600},{1800,-75},{1800,-150},{1800,-225},{1800,-300},{1800,-375},{1800,-600},{1800,-675},{1650,-675},{1725,-675},{1575,-675},{1650,-600},{1725,-600},{975,-675},{1050,-675},{1125,-675},{1200,-675},{1275,-675},{1350,-675},{1425,-675},{1500,-675},{900,-75},{900,-150},{900,-225},{900,-300},{900,-375},{900,-600},{900,-675},{975,-75},{975,-150},{975,-300},{975,-375},{975,-225},{975,-600},{1050,-600},{1500,-75},{1575,-75},{1650,-75},{1725,-75},{1050,-75},{825,1200},{750,1200},{825,1125},{75,1125},{75,1200},{675,75},{675,150},{600,150},{525,150},{1575,-150},{1575,-225},{1575,-300},{1575,-375},{1500,-375},{1725,-150},{1425,225},{75,825},{150,825},{300,825},{225,825},{300,900},{375,975},{375,1050},{300,1050},{300,975},{75,900},{225,900},{0,0},{0,750},{1425,0},{1425,300},{1425,375},{1425,450},{1425,525},{1350,0},{1275,0},{675,525},{675,450},{750,375},{825,375},{900,375},{975,300},{1050,300},{975,375},{1125,300},{1200,300},{1050,375},{750,525},{1125,375},{1200,375},{675,375},{1350,525},{1425,675},{1425,750},{1425,825},{1425,900},{1425,975},{1425,1275},{1425,1200},{1425,1125},{1425,1050},{975,675},{975,750},{1050,750},{1050,675},{1125,675},{1125,750},{1125,825},{1050,825},{975,825},{975,900},{825,900},{825,825},{825,750},{1200,675},{1275,675},{1350,675},{1350,750},{1275,750},{1275,825},{1350,825},{1200,750},{1425,150},{1425,75},{0,-75},{0,-150},{0,-225},{0,-300},{0,-375},{0,-450},{0,-525},{0,-600},{0,-675},{75,-675},{150,-675},{225,-675},{300,-675},{375,-675},{450,-675},{525,-675},{600,-675},{675,-675},{750,-675},{825,-675},{0,-750},{75,-750},{150,-750},{225,-750},{300,-750},{375,-750},{450,-750},{525,-750},{600,-750},{675,-750},{750,-750},{825,-750},{900,-750},{975,-750},{1050,-750},{1125,-750},{1200,-750},{1275,-750},{1350,-750},{1425,-750},{1500,-750},{1575,-750},{1650,-750},{1725,-750},{1800,-750},{1425,-75},{1425,-150},{1425,-225},{1425,-300},{1425,-375},{1425,-525},{300,-150},{375,-150},{450,-150},{450,-225},{450,-300},{525,-150},{600,-150},{675,-225},{675,-300},{600,-375},{225,-225},{225,-300},{300,-375},{375,-525},{525,-525},{1500,-150},{1500,-225},{1500,-300},{1500,-525},{1500,-600},{1425,-600},{1875,-750},{1950,-750},{2025,-750},{2025,-675},{2100,-675},{2100,-600},{2175,-600},{2250,-600},{2325,-600},{2325,-525},{2400,-525},{2400,-450},{2475,-450},{2475,-375},{2475,-300},{2475,-225},{2475,-150},{2400,-150},{2400,-225},{2325,-150},{2325,-75},{2250,-75},{2250,-150},{2175,-75},{2100,-75},{2100,-150},{2025,-75},{2025,-150},{1950,-150},{1875,-150},{1950,-75},{2325,-375},{2325,-450},{2250,-450},{2175,-450},{2100,-450},{2100,-375},{2100,-300},{2025,-375},{1950,-375},{1950,-450},{1950,-525},{1950,-600},{1875,-600},{2100,-525},{2025,-525},{2250,-375},{2250,-300},{2175,-300},{2175,-225},{2250,-225},{2100,-225},{2025,-225},{1950,-225},{1950,-300},{2025,-300},{2400,-300},{975,0},{1050,0},{825,-75},{750,-75},{675,-75},{150,-75},{75,-75},{1275,-75},{1350,-75},{225,-75},{1050,75},{975,75},{1275,-225},{1350,-225},{0,1350},{75,1350},{150,1350},{225,1350},{300,1350},{375,1350},{450,1350},{525,1350},{600,1350},{675,1350},{750,1350},{825,1350},{900,1350},{975,1350},{1050,1350},{1125,1350},{1200,1350},{1275,1350},{1350,1350},{1425,1350}};
        private ArrayList<Sprite> sprites;

        private Link link;

        private int score;

        public Model()
        {
            sprites = new ArrayList<Sprite>();
            link = new Link(150, 150);
            sprites.add(link);
            score = 0;
        }

        //add all trees and treasure chests
        public void unmarshal()
        {
            sprites.clear();
            sprites.add(link);

            for(int[] t : treeData)
                sprites.add(new Tree((int)(t[0]*GameView.scale), (int)(t[1]*GameView.scale)));
            //scale coordinates of trees and chests based on screen resolution
            for(int[] c : chestData)
                sprites.add(new TreasureChest((int)(c[0]*GameView.scale),(int)(c[1]*GameView.scale)));
        }

        void update()
        {
            ArrayList<Sprite> safeArray = new ArrayList<>(sprites); //a copy of sprites array to avoid errors
            Iterator<Sprite> outer = safeArray.iterator();
            while(outer.hasNext())
            {
                Sprite spriteA = outer.next();
                Iterator<Sprite> inner = safeArray.iterator();
                while(inner.hasNext())
                {
                    Sprite spriteB = inner.next();
                    if(spriteA == spriteB)
                        continue;

                    if(collisionDetected(spriteA, spriteB)) //collision detection
                    {
                        if(spriteA.isLink() && spriteB.isTree())
                        {
                            ((Link)spriteA).getOut(spriteB);
                            continue;
                        }

                        if(spriteA.isLink() && spriteB.isTreasureChest())
                        {
                            TreasureChest chest = (TreasureChest)spriteB;
                            if(chest.getState() == "chest")
                                chest.change(); //open treasure chest

                            if(chest.getState() == "loot" && !(chest.getCanBeAdded()))
                                ((Link)spriteA).getOut(spriteB);
                            else if(chest.getCanBeAdded()) {
                                chest.setAdded(true); //collect bat
                                score++;
                            }
                            continue;
                        }

                        if(spriteA.isBoomerang() && !(spriteB.isLink() || spriteB.isBoomerang()))
                        {
                            Boomerang boomerang = (Boomerang)spriteA;
                            boomerang.setRemove(true); //remove boomerang upon collision
                            if(spriteB.isTreasureChest())
                            {
                                TreasureChest chest = (TreasureChest)spriteB;
                                if(chest.getState() == "chest")
                                    chest.change(); //open treasure chest
                                else if(chest.getCanBeAdded()) {
                                    chest.setAdded(true); //collect bat
                                    score++;
                                }
                            }
                        }
                    }
                }
                if(!(spriteA.update()))
                    sprites.remove(spriteA); //remove sprites
            }
        }

        public int getNumSprites()
        {
            return sprites.size();
        }

        public Sprite getSprite(int i)
        {
            return sprites.get(i);
        }

        public void tellLinkToMove(String dir) //move Link/Hello Kitty
        {
            link.move(dir);
        }

        public int getTurtleX()
        {
            return link.getX();
        }

        public int getTurtleY()
        {
            return link.getY();
        }

        public boolean collisionDetected(Sprite spriteA, Sprite spriteB) //return true if 2 objects are overlapping
        {
            boolean collision = true;
            if((spriteA.getX() + spriteA.getW()) < spriteB.getX())
                collision = false;
            else if(spriteA.getX() > (spriteB.getX() + spriteB.getW()))
                collision = false;
            else if(spriteA.getY() > (spriteB.getY() + spriteB.getH()))
                collision = false;
            else if((spriteA.getY() + spriteA.getH()) < spriteB.getY())
                collision = false;
            return collision;
        }

        public void throwBoomerang() //throw a boomerang in Link's current direction
        {
            Boomerang b = new Boomerang(link.getX() + link.getW()/2, link.getY() + link.getH()/2);
            sprites.add(b);
            b.fly(link.getCurrentDirection());
        }

        public void setPX()
        {
            link.setPX();
        }

        public void setPY()
        {
            link.setPY();
        }

        public int getScore() {
            return score;
        }
    }




    static class GameView extends SurfaceView
    {
        SurfaceHolder ourHolder;
        Canvas canvas;
        Paint paint;
        Paint textPaint;
        Model model;
        GameController controller;

        public static Bitmap[][] kittyFrames;
        public static Bitmap[] boomerangFrames;
        public static Bitmap loot;
        public static Bitmap treasureChest;
        static Bitmap tree;

        private int currentRoomX;
        private int currentRoomY;

        private static int screenWidth;
        private static int screenHeight;

        private final int designWidth = 950;
        private final int designHeight = 700;
        private static float scale;

        public GameView(Context context, Model m)
        {
            super(context);
            model = m;

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint(); //paint for sprites
            textPaint = new Paint(); //paint for score

            DisplayMetrics metrics = context.getResources().getDisplayMetrics(); //get screen resolution
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            scale = Math.min((float) screenWidth / designWidth, (float) screenHeight / designHeight); //scale based on the original design

            // Load the images
            int index = 1;
            kittyFrames = new Bitmap[Link.NUM_DIRECTIONS][Link.MAX_IMAGES_PER_DIRECTION];
            for(int i = 0; i < Link.NUM_DIRECTIONS; i++)
                for(int j = 0; j < Link.MAX_IMAGES_PER_DIRECTION; j++) //load and scale hello kitty images
                {
                    int id = getResources().getIdentifier("kitty" + (index++), "drawable", context.getPackageName());
                    Bitmap wall = BitmapFactory.decodeResource(getResources(), id);
                    kittyFrames[i][j] = Bitmap.createScaledBitmap(wall, Link.WIDTH, Link.HEIGHT, false);
                }

            index = 1;
            boomerangFrames = new Bitmap[Boomerang.NUM_FRAMES];
            for(int i = 0; i < Boomerang.NUM_FRAMES; i++) //load and scale shadow images
            {
                int id = getResources().getIdentifier("shadow" + (index++), "drawable", context.getPackageName());
                Bitmap wall = BitmapFactory.decodeResource(getResources(), id);
                boomerangFrames[i] = Bitmap.createScaledBitmap(wall, (int)(Boomerang.WIDTH*scale), (int)(Boomerang.HEIGHT*scale), false);
            }

            Bitmap c = BitmapFactory.decodeResource(getResources(), R.drawable.treasurechest);
            treasureChest = Bitmap.createScaledBitmap(c, TreasureChest.WIDTH, TreasureChest.HEIGHT, false);

            Bitmap l = BitmapFactory.decodeResource(getResources(), R.drawable.rouge);
            loot = Bitmap.createScaledBitmap(l, TreasureChest.WIDTH, TreasureChest.HEIGHT, false);

            Bitmap t = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
            tree = Bitmap.createScaledBitmap(t, Tree.WIDTH, Tree.HEIGHT, false);

            //variables used to switch rooms
            currentRoomX = 0;
            currentRoomY = 0;
        }

        void setController(GameController c)
        {
            controller = c;
        }

        public static float getScale()
        {
            return scale;
        }

        //move camera if hello kitty goes to another room
        public void moveCamera()
        {
            if(model.getTurtleX() < currentRoomX)
                currentRoomX -= screenWidth;

            if(model.getTurtleX() >= currentRoomX + screenWidth)
                currentRoomX += screenWidth;

            if(model.getTurtleY() >= currentRoomY + screenHeight)
                currentRoomY += screenHeight;

            if(model.getTurtleY() < currentRoomY)
                currentRoomY -= screenHeight;
        }

        public void update()
        {
            if (!ourHolder.getSurface().isValid())
                return;
            canvas = ourHolder.lockCanvas();
            moveCamera();

            //Draw the background color
            canvas.drawColor(Color.argb(255, 127, 228, 165));

            for(int i = 0; i < model.getNumSprites(); i++) //draw all sprites
            {
                Sprite sprite = model.getSprite(i);
                if((sprite.isTree() || sprite.isTreasureChest()) && !(sprite.isOnScreen(sprite.getX(), sprite.getY(), sprite.getW(), sprite.getH(), currentRoomX, currentRoomY, screenWidth, screenHeight)))
                    continue; //skip trees and treasure chests in other rooms to avoid drawing off screen objects
                sprite.drawYourself(canvas, currentRoomX, currentRoomY, paint);
            }
            //draw buttons
            controller.btnUp.draw(canvas, paint);
            controller.btnDown.draw(canvas, paint);
            controller.btnLeft.draw(canvas, paint);
            controller.btnRight.draw(canvas, paint);
            controller.btnBoomerang.draw(canvas, paint);

            //Draw the score
            textPaint.setColor(Color.BLACK);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setTextSize(80);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setStrokeWidth(6);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Score:" + model.getScore(), screenWidth-50, 120, textPaint); //black outline

            textPaint.setColor(Color.YELLOW);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Score:" + model.getScore(), screenWidth-50, 120, textPaint); //yellow text

            ourHolder.unlockCanvasAndPost(canvas);
        }

        // The SurfaceView class (which GameView extends) already
        // implements onTouchListener, so we override this method
        // and pass the event to the controller.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            controller.onTouchEvent(motionEvent);
            return true;
        }
    }




    static class GameController implements Runnable
    {
        volatile boolean playing;
        Thread gameThread = null;
        Model model;
        GameView view;

        GameButton btnUp, btnDown, btnLeft, btnRight, btnBoomerang;

        GameController(Model m, GameView v)
        {
            model = m;
            view = v;
            view.setController(this);
            playing = true;

            model.unmarshal(); //add all treasure chests and trees

            btnUp = new GameButton(GameView.screenWidth-340, 425, "↑"); //create buttons
            btnDown = new GameButton(GameView.screenWidth-340, 675, "↓");
            btnLeft = new GameButton(GameView.screenWidth-465, 550,"←");
            btnRight = new GameButton(GameView.screenWidth-215, 550, "→");
            btnBoomerang = new GameButton(150, 550, "@");
        }

        void update()
        {
            model.setPX();
            model.setPY(); //set hello kitty's previous position

            if(btnUp.pressed) //move hello kitty if a button is pressed
                model.tellLinkToMove("up");
            if(btnDown.pressed)
                model.tellLinkToMove("down");
            if(btnLeft.pressed)
                model.tellLinkToMove("left");
            if(btnRight.pressed)
                model.tellLinkToMove("right");
        }

        @Override
        public void run()
        {
            while(playing)
            {
                this.update();
                model.update();
                view.update();

                try {
                    Thread.sleep(20);
                } catch(Exception e) {
                    Log.e("Error:", "sleeping");
                    System.exit(1);
                }
            }
        }

        void onTouchEvent(MotionEvent motionEvent)
        {
            float x = motionEvent.getX();
            float y = motionEvent.getY();

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    btnUp.pressed = btnUp.isPressed(x, y);
                    btnDown.pressed = btnDown.isPressed(x, y);
                    btnLeft.pressed = btnLeft.isPressed(x, y);
                    btnRight.pressed = btnRight.isPressed(x, y);
                    if (btnBoomerang.isPressed(x, y)) {
                        model.throwBoomerang();
                        btnBoomerang.pressed = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    btnUp.pressed = false;
                    btnDown.pressed = false;
                    btnLeft.pressed = false;
                    btnRight.pressed = false;
                    btnBoomerang.pressed = false;
                    break;
            }
        }

        // Shut down the game thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
                System.exit(1);
            }

        }

        // Restart the game thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
}