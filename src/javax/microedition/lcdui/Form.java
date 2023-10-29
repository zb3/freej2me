/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package javax.microedition.lcdui;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import java.util.ArrayList;
import java.awt.Rectangle;


public class Form extends Screen
{

	public ItemStateListener listener;

	int focusedItem = 0;
	boolean needsLayout = true;
	int scrollY = 0;
	Rectangle[] itemBounds = null;
	int clientHeight;
	int scrollHeight = 0;

	public Form(String title)
	{
		setTitle(title);
		platformImage = new PlatformImage(width, height);
		render();
	}

	public Form(String title, Item[] itemarray)
	{
		setTitle(title);

		if (items != null)
		{
			for (int i=0; i<itemarray.length; i++)
			{
				items.add(itemarray[i]);
			}
		}
		platformImage = new PlatformImage(width, height);
		render();
	}

	public int append(Image img) { items.add(new ImageItem("",img,0,"")); needsLayout = true; render(); return items.size()-1;  }

	public int append(Item item) { items.add(item); needsLayout = true;  render(); return items.size()-1; }

	public int append(String str) { items.add(new StringItem("",str)); needsLayout = true; render(); return items.size()-1;  }

	public void delete(int itemNum) { items.remove(itemNum); needsLayout = true; render(); }

	public void deleteAll() { items.clear(); needsLayout = true;  render(); }

	public Item get(int itemNum) { return items.get(itemNum); }

	public int getHeight() { return 128; }

	public int getWidth() { return 64; }

	public void insert(int itemNum, Item item) { items.add(itemNum, item); needsLayout = true; render(); }

	public void set(int itemNum, Item item) { items.set(itemNum, item); needsLayout = true; render(); }

	public void setItemStateListener(ItemStateListener iListener) { listener = iListener; }

	public int size() { return items.size(); }

	/*
		Draw form, handle input
	*/

	public void keyPressed(int key)
	{
		if(listCommands==true)
		{
			keyPressedCommands(key);
			return;
		}

		if(items.size()<1) { return; }

		int reasonablePadding = 10;
		int scrollAmount = clientHeight/4;

		switch(key)
		{
			case Mobile.KEY_NUM2:
			case Mobile.NOKIA_UP:
			{
				Rectangle reasonableViewport = new Rectangle(0, scrollY+reasonablePadding, width, clientHeight-reasonablePadding);

				if (focusedItem > 0 && itemBounds[focusedItem].intersects(reasonableViewport))
				{
					focusedItem--;
				}
				else if (scrollY > 0)
				{
					scrollY = Math.max(0, scrollY - scrollAmount);
				}
				break;
			}
			case Mobile.KEY_NUM8:
			case Mobile.NOKIA_DOWN:
			{
				Rectangle reasonableViewport = new Rectangle(0, scrollY+reasonablePadding, width, clientHeight-reasonablePadding);

				int maxScroll = scrollHeight - clientHeight;

				if (focusedItem < items.size()-1 && itemBounds[focusedItem+1].intersects(reasonableViewport))
				{
					focusedItem++;
				}
				else if (scrollY < maxScroll)
				{
					scrollY = Math.min(maxScroll, scrollY + scrollAmount);
				}
				break;
				
			}
			case Mobile.NOKIA_SOFT1: doLeftCommand(); break;
			case Mobile.NOKIA_SOFT2: doRightCommand(); break;
			case Mobile.NOKIA_SOFT3: doDefaultCommand(); break;
			case Mobile.KEY_NUM5: doDefaultCommand(); break;
		}
		
		render();
	}

	public void notifySetCurrent()
	{
		render();
	}

	private void computeLayout(PlatformGraphics gc, int height)
	{
		this.clientHeight = height;
		scrollY = 0;
		focusedItem = 0;
		itemBounds = new Rectangle[items.size()];

		int spaceBetweenItems = 2;
		int scrollbarWidth = 4;
		int padding = 5;

		int currentY = padding;

		int itemWidth = width - padding*2 - scrollbarWidth;
		int itemX = padding;

		for (int i=0; i<items.size(); i++)
		{
			if (i > 0)
			{
				currentY += spaceBetweenItems;
			}
			int itemHeight = getItemHeight(gc, items.get(i), width-scrollbarWidth-2*padding);

			itemBounds[i] = new Rectangle(itemX, currentY, itemWidth, itemHeight);
			currentY += itemHeight;
		}

		currentY += padding;
		scrollHeight = currentY;	
	}

	private int getItemHeight(PlatformGraphics gc, Item item, int width)
	{
		int height = 15;

		if (item instanceof CustomItem)
		{
			height = ((CustomItem)item).getPrefContentHeight(width);
		}
		else if (item instanceof StringItem)
		{
			StringItem it = (StringItem)item;
			it.generateLayout(gc.getGraphics2D().getFontMetrics(), width);
			height = it.height;
		}

		return height;

	}


	public String renderItems(int x, int y, int width, int height)
	{
		PlatformGraphics gc = platformImage.getGraphics();

		if (needsLayout)
		{
			computeLayout(gc, height);
			needsLayout = false;
		}

		if(items.size()>0)
		{
			gc.setClip(x, y, width, height);
			int scrollbarWidth = 4;

			Rectangle viewport = new Rectangle(0, scrollY, width, height);

			for (int t=0;t<items.size();t++)
			{
				Item item = items.get(t);

				if (!viewport.intersects(itemBounds[t]))
				continue;

				int thisX = x + itemBounds[t].x;
				int thisY = y + itemBounds[t].y - scrollY;

				if (t == focusedItem && items.size() > 1)
				{
					gc.setColor(150, 150, 150);
					// drawRect needs size - 1
					gc.drawRect(thisX - 3, thisY - 3, itemBounds[t].width + 6 - 1, itemBounds[t].height + 6 - 1);
					gc.setColor(0, 0, 0);
				}

				// paint...
				if (item instanceof StringItem)
				{
					StringItem si = (StringItem) item;

					for(int l=0;l<si.lines.size();l++)
					{
						gc.drawString(
							si.lines.get(l),
							thisX,
						  	thisY + l*si.lineHeight + (l > 0 ? (l-1)*si.lineSpacing : 0),
						 	Graphics.LEFT);
					}
				}
			}

			double fact = (double)height/scrollHeight;
			int yscrollStart = (int)Math.round(scrollY * fact);
			int yscrollHeight = (int)Math.min(height, Math.round(height * fact));
		
			if (height < scrollHeight)
			{
				gc.setColor(150, 150, 150);
				gc.fillRect(x + width - scrollbarWidth, y+yscrollStart, scrollbarWidth, yscrollHeight);
			}
		
			gc.reset();
		}

		return null;
	}

}
