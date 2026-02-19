//Name: Anastasiia Tykina
//Student ID: 100433562
//Date: 10/15/2025
//Program Description: this program creates a simple game with 4 rooms surrounded by trees. Additional trees and treasure chests can be added/removed using the mouse/keyboard. 
//The player controls Hello Kitty. Hello Kitty can move around, throw boomerangs and collect rupees. Boomerangs disappear when they hit trees/treasure chests.

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Graphics;

public class Model
{
	private ArrayList<Sprite> sprites;
	private boolean editMode;
	private boolean addMapItem;

	private ArrayList<Sprite> itemsICanAdd;
	private int itemNum;

	private Link link;

	public Model()
	{
		sprites = new ArrayList<Sprite>();
		editMode = false;
		addMapItem = true;

		link = new Link(100, 100);
		sprites.add(link);

		itemsICanAdd = new ArrayList<Sprite>();
		itemsICanAdd.add(new Tree(0, 0));
		itemsICanAdd.add(new TreasureChest(0, 0));
	}

	//marshal model and all trees and treasure chests
	public Json marshal()
	{
		Json ob = Json.newObject();
		Json treeList = Json.newList();
		ob.add("trees", treeList);
		for(int i = 0; i < sprites.size(); i++)
		{
			if(sprites.get(i).isTree())
				treeList.add(((Tree)sprites.get(i)).marshal());
		}

		Json treasureChestList = Json.newList();
		ob.add("chests", treasureChestList);
		for(int i = 0; i < sprites.size(); i++)
		{
			if(sprites.get(i).isTreasureChest())
				treasureChestList.add(((TreasureChest)sprites.get(i)).marshal());
		}
		return ob;
	}

	//unmarhsal model and all trees and treasure chests
	public void unmarshal(Json ob)
	{
		sprites.clear();
		sprites.add(link);
		Json treeList = ob.get("trees");
		for(int i = 0; i < treeList.size(); i++)
		{
			Tree t = new Tree(treeList.get(i));
			sprites.add(t);
		}

		Json treasureChestList = ob.get("chests");
		for(int i = 0; i < treasureChestList.size(); i++)
		{
			TreasureChest c = new TreasureChest(treasureChestList.get(i));
			sprites.add(c);
		}
	}

	public void update()
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
						spriteA.getOut(spriteB);
						continue;
					}

					if(spriteA.isLink() && spriteB.isTreasureChest())
					{
						TreasureChest chest = (TreasureChest)spriteB;
						if(chest.getState() == "chest")
							chest.change(); //open treasure chest

						if(chest.getState() == "loot" && !(chest.getCanBeAdded()))
							spriteA.getOut(spriteB);
						else if(chest.getCanBeAdded())
							chest.setAdded(true); //collect rupee
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
							else if(chest.getCanBeAdded())
								chest.setAdded(true); //collect rupee
						}
					}
				}
			}
			if(!(spriteA.update()))
				sprites.remove(spriteA); //remove sprites
		}
	}

	//getters and setters for private attributes
	public boolean getEditMode()
	{
		return this.editMode;
	}

	public void setEditMode(boolean m)
	{
		this.editMode = m;
		itemNum = 0;
	}

	public boolean getAddMapItem()
	{
		return this.addMapItem;
	}

	public void setAddMapItem(boolean m)
	{
		this.addMapItem = m;
	}

	public int getNumSprites()
	{
		return sprites.size();
	}

	public Sprite getSprite(int i)
	{
		return sprites.get(i);
	}

	public int getItemNum()
	{
		return itemNum;
	}

	public void iterateItemNum() //iterate between objects we can add
	{
		if((editMode == true))
		{
			if(itemNum == itemsICanAdd.size() - 1)
			{
				itemNum = 0;
				return;
			}
			itemNum++;
		}
	}

	public Sprite getIcon(int i) //return current item in edit mode
	{
		return itemsICanAdd.get(i);
	}

	//draw an object based on the mouse input (actually just add to the array)
	public void drawObject(int mouseX, int mouseY)
	{
		if((editMode == true) && (addMapItem == true)) //only works when the edit mode and add mode are on
		{
			Sprite sprite;
			if(itemsICanAdd.get(itemNum).isTree())
			{
				int x = Math.floorDiv(mouseX, Tree.WIDTH) * Tree.WIDTH;
				int y = Math.floorDiv(mouseY, Tree.HEIGHT) * Tree.HEIGHT;
				sprite = new Tree(x, y);
				for(int i = sprites.size() - 1; i>=0; i--) //check if there is already an object
				{
					if(sprites.get(i).isTree() && sprites.get(i).clickedOn(mouseX, mouseY)) //checking tree vs tree
						return;
					else if((sprites.get(i).isTreasureChest() || sprites.get(i).isLink()) && collisionDetected(sprite, sprites.get(i)))
						return;
				}
			}	
			else if(itemsICanAdd.get(itemNum).isTreasureChest())
			{
				sprite = new TreasureChest(mouseX - TreasureChest.WIDTH/2, mouseY-TreasureChest.HEIGHT/2);
				for(int i = sprites.size() - 1; i>=0; i--)
				{
					if(collisionDetected(sprite, sprites.get(i))) //checking treasure chest vs any object
					{
						return;
					}
				}
			}
			else
				return;

			sprites.add(sprite);
		}
	}

	//clear the map
	public void clearMap()
	{
		if((editMode == true) && (addMapItem == false))
		{
			sprites.clear();
			sprites.add(link);
		}
	}

	//remove an object when clicked on
	public void removeObject(int x, int y)
	{
		if((editMode == true) && (addMapItem == false)) //only works when the edit mode and remove mode are on
		{
			Iterator<Sprite> iterator = sprites.iterator();
			while(iterator.hasNext())
			{
				Sprite sprite = iterator.next();
				if(sprite.isLink())
					continue;
				if(sprite.clickedOn(x, y))
					if((itemNum == 0 && sprite.isTree()) || (itemNum == 1 && sprite.isTreasureChest()))
						iterator.remove();
			}

		}
	}

	public void tellLinkToMove(String dir) //move Link
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

}