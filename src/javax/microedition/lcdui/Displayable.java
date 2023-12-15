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

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

public abstract class Displayable
{

	public PlatformImage platformImage;
	public PlatformGraphics gc;

	public int width = 0;

	public int height = 0;
	
	protected String title = "";

	protected ArrayList<Command> commands = new ArrayList<Command>();

	protected ArrayList<Item> items = new ArrayList<Item>();

	protected CommandListener commandlistener;

	protected boolean listCommands = false;
	
	protected int currentCommand = 0;

	public Ticker ticker;

	protected Font uiFont;
	protected int uiLineHeight;

	private volatile boolean insideInvalidate = false;

	public Displayable()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;
		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
		uiFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE__INTERNAL_UI);
		uiLineHeight = uiFont.getHeight();
	}

	public void addCommand(Command cmd)
	{ 
		commands.add(cmd);
		_invalidate();
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
		_invalidate();
	}
	
	public int getWidth() { return width; }

	public int getHeight() { return height; }
	
	public String getTitle() { return title; }

	public void setTitle(String text) { title = text; }        

	public boolean isShown() { return true; }

	public Ticker getTicker() { return ticker; }

	public void setTicker(Ticker tick) { ticker = tick; }
	
	public void setCommandListener(CommandListener listener) { commandlistener = listener; }

	protected void sizeChanged(int width, int height) { }

	public Display getDisplay() { return Mobile.getDisplay(); }

	public ArrayList<Command> getCommands() { return commands; }

	public void keyPressed(int platKey) {}
	public void keyReleased(int platKey) {}
	public void keyRepeated(int platKey) {}

	public void keyPressed(int platKey, KeyEvent keyEvent) {
		int key = Mobile.normalizeKey(platKey);

		if (listCommands==true) {
			keyPressedCommands(key);
		} else {
			boolean handled = screenKeyPressed(key, platKey, keyEvent);

			if (!handled) {
				if (key == Mobile.NOKIA_SOFT1) {
					doLeftCommand();
				} else if (key == Mobile.NOKIA_SOFT2) {
					doRightCommand();
				} else if (key == Mobile.NOKIA_SOFT3) {
					doDefaultCommand();
				}
			}
		}
	}
	public void keyReleased(int key, KeyEvent keyEvent) { }
	public void keyRepeated(int platKey, KeyEvent keyEvent) {
		int key = Mobile.normalizeKey(platKey);
		// allow arrows for navigation
		if (key == Mobile.NOKIA_DOWN || key == Mobile.NOKIA_UP || key == Mobile.NOKIA_LEFT || key == Mobile.NOKIA_RIGHT) {
			keyPressed(platKey, keyEvent);
		}
	}

	public boolean screenKeyPressed(int key, int platKey, KeyEvent keyEvent) { return false; }
	public void screenKeyReleased(int key, KeyEvent keyEvent) { }
	public void screenKeyRepeated(int key, KeyEvent keyEvent) { }


	public void pointerDragged(int x, int y) { }
	public void pointerPressed(int x, int y) { }
	public void pointerReleased(int x, int y) { }
	public void showNotify() { }
	public void hideNotify() { }

	public void notifySetCurrent() {
		render();
	}

	protected void render()
	{
		gc.setFont(uiFont);

		// Draw Background:
		gc.setColor(0xFFFFFF);
		gc.fillRect(0,0,width,height);
		gc.setColor(0x000000);

		String currentTitle = listCommands ? "Options" : title;

		int titlePadding = uiLineHeight / 10;
		int titleHeight = uiLineHeight + 2*titlePadding;

		int xPadding = uiLineHeight/5;

		int commandsBarHeight = titleHeight - 1;

		int contentHeight = height - titleHeight - commandsBarHeight - 2; // 1px for line
		
		// Draw Title:
		gc.drawString(currentTitle, width/2, titlePadding, Graphics.HCENTER);
		gc.drawLine(0, titleHeight, width, titleHeight);
		gc.drawLine(0, height-commandsBarHeight-1, width, height-commandsBarHeight-1);

		int currentY = titleHeight + 1;

		if (listCommands)
		{
			if(commands.size()>0)
			{
				if(currentCommand<0) { currentCommand = 0; }
				// Draw commands //

				int listPadding = uiLineHeight/5;
				int itemHeight = uiLineHeight;

				int ah = contentHeight - 2*listPadding; // allowed height
				int max = (int)Math.floor(ah / itemHeight); // max items per page			
				if(commands.size()<max) { max = commands.size(); }

				int page = 0;
				page = (int)Math.floor(currentCommand/max); // current page
				int first = page * max; // first item to show
				int last = first + max - 1;

				if(last>=commands.size()) { last = commands.size()-1; }
				
				int y = currentY + listPadding;
				for(int i=first; i<=last; i++)
				{	
					if(currentCommand == i)
					{
						gc.fillRect(0,y,width,itemHeight);
						gc.setColor(0xFFFFFF);
					}
					
					gc.drawString(commands.get(i).getLabel(), width/2, y, Graphics.HCENTER);
					
					gc.setColor(0x000000);
					y += itemHeight;
				}
			}

			currentY += contentHeight;

			gc.drawString("Okay", xPadding, currentY+titlePadding, Graphics.LEFT);
			gc.drawString("Back", width-xPadding, currentY+titlePadding, Graphics.RIGHT);
		}
		else
		{
			gc.setClip(0, currentY, width, contentHeight);
			String status = renderScreen(0, currentY, width, contentHeight);

			currentY += contentHeight;

			gc.reset();
			gc.setFont(uiFont);

			Command itemCommand = null;
			if (this instanceof Form) {
				itemCommand = ((Form)this).getItemCommand();
			}

			// Draw Commands
			switch(commands.size())
			{
				case 0: break;
				case 1:
					gc.drawString(commands.get(0).getLabel(), xPadding, currentY+titlePadding, Graphics.LEFT);
					if (status != null)
					{
						gc.drawString(status, width-xPadding, currentY+titlePadding, Graphics.RIGHT);
					}
					
					break;
				case 2:
					gc.drawString(commands.get(0).getLabel(), xPadding, currentY+titlePadding, Graphics.LEFT);
					gc.drawString(commands.get(1).getLabel(), width-xPadding, currentY+titlePadding, Graphics.RIGHT);

					if (status != null && itemCommand == null)
					{
						gc.drawString(status, width/2, currentY+titlePadding, Graphics.HCENTER);
					}
					break;
				default:
					gc.drawString("Options", xPadding, currentY+titlePadding, Graphics.LEFT);
			}

			if (itemCommand != null) {
				gc.drawString(itemCommand.getLabel(), width/2, currentY+titlePadding, Graphics.HCENTER);
			}
		}
	
		if(this.getDisplay().getCurrent() == this)
		{
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}

	protected void renderCommands(PlatformGraphics gc)
	{

	}

	protected String renderScreen(int x, int y, int width, int height) {
		return null;
	}

	protected void keyPressedCommands(int key)
	{
		if (key == Mobile.NOKIA_UP || key == Mobile.NOKIA_DOWN) {
			currentCommand += (key == Mobile.NOKIA_UP) ? -1 : 1;
			if(currentCommand>=commands.size()) { currentCommand = 0; }
			if(currentCommand<0) { currentCommand = commands.size()-1; }
		} else if (key == Mobile.NOKIA_SOFT1 || key == Mobile.NOKIA_SOFT2 ||
				   key == Mobile.NOKIA_SOFT3) {
			listCommands = false;
			
			if (key != Mobile.NOKIA_SOFT2) {
				doCommand(currentCommand);
			}

			currentCommand = 0;
		} else {
			return;
		}

		// we've either exited listCommands or changed selection
		// this is intentionally done after command execution to prevent flashes
		_invalidate(); 
	}

	protected void doCommand(int index)
	{
		if(index>=0 && commands.size()>index)
		{
			if(commandlistener!=null)
			{
                commandlistener.commandAction(commands.get(index), this);
			}
		}
	}

	protected void doDefaultCommand()
	{
		doCommand(0);
	}

	protected void doLeftCommand()
	{
		if(commands.size()>2)
		{
			listCommands = true;
			_invalidate();
		}
		else
		{
			if(commands.size()>0 && commands.size()<=2)
			{
				doCommand(0);
			}
		}
	}

	protected void doRightCommand()
	{
		if(commands.size()>0 && commands.size()<=2)
		{
			doCommand(1);
		}
	}

	protected void _invalidate() {
		// TODO: consider queuing this
		// the code below ensures this function is not reentrant

		synchronized (Display.LCDUILock) {
			if (getDisplay().current != this) {
				// we'll render again on notifySetCurrent
				return;
			}

			if (insideInvalidate) {
				System.out.println("BUG: recursive invalidate attempt");
				Thread.dumpStack();
			} else {
				insideInvalidate = true;

				try {
					render();
				} finally {
					insideInvalidate = false;
				}
			}
		}
	}
}
