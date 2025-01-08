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

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformGraphics;

public abstract class Canvas extends Displayable
{
	public static final int UP = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 5;
	public static final int DOWN = 6;
	public static final int FIRE = 8;

	public static final int GAME_A = 9;
	public static final int GAME_B = 10;
	public static final int GAME_C = 11;
	public static final int GAME_D = 12;

	public static final int KEY_NUM0 = 48;
	public static final int KEY_NUM1 = 49;
	public static final int KEY_NUM2 = 50;
	public static final int KEY_NUM3 = 51;
	public static final int KEY_NUM4 = 52;
	public static final int KEY_NUM5 = 53;
	public static final int KEY_NUM6 = 54;
	public static final int KEY_NUM7 = 55;
	public static final int KEY_NUM8 = 56;
	public static final int KEY_NUM9 = 57;
	public static final int KEY_STAR = 42;
	public static final int KEY_POUND = 35;

	private int barHeight;
	private int barPadding;
	private boolean fullscreen = false;
	private boolean shouldRepaintBar = true;
	private boolean isInsidePaint = false;


	protected Canvas()
	{
		barPadding = uiLineHeight / 5;
		barHeight = uiLineHeight + barPadding;
		setFullScreenInternal(Boolean.getBoolean("freej2me.forceFullscreen"));
	}

	protected Canvas(boolean fullscreen)
	{
		this();
		setFullScreenInternal(fullscreen);
	}

	public int getGameAction(int platKeyCode)
	{
		int keyCode = Mobile.normalizeKey(platKeyCode);
		switch(keyCode)
		{
			case Mobile.KEY_NUM2: return UP;
			case Mobile.KEY_NUM8: return DOWN;
			case Mobile.KEY_NUM4: return LEFT;
			case Mobile.KEY_NUM6: return RIGHT;
			case Mobile.KEY_NUM5: return FIRE;
			case Mobile.KEY_NUM1: return GAME_A;
			case Mobile.KEY_NUM3: return GAME_B;
			case Mobile.KEY_NUM7: return GAME_C;
			case Mobile.KEY_NUM9: return GAME_D;
			case Mobile.NOKIA_UP: return UP;
			case Mobile.NOKIA_DOWN: return DOWN;
			case Mobile.NOKIA_LEFT: return LEFT;
			case Mobile.NOKIA_RIGHT: return RIGHT;
			case Mobile.NOKIA_SOFT3: return FIRE;
		}
		return 0;
	}

	public int getKeyCode(int gameAction)
	{
		switch(gameAction)
		{
			//case Mobile.GAME_UP: return Mobile.KEY_NUM2;
			//case Mobile.GAME_DOWN: return Mobile.KEY_NUM8;
			//case Mobile.GAME_LEFT: return Mobile.KEY_NUM4;
			//case Mobile.GAME_RIGHT: return Mobile.KEY_NUM6;
			//case Mobile.GAME_FIRE: return Mobile.KEY_NUM5;
			case Mobile.GAME_UP: return Mobile.NOKIA_UP;
			case Mobile.GAME_DOWN: return Mobile.NOKIA_DOWN;
			case Mobile.GAME_LEFT: return Mobile.NOKIA_LEFT;
			case Mobile.GAME_RIGHT: return Mobile.NOKIA_RIGHT;
			case Mobile.GAME_FIRE: return Mobile.NOKIA_SOFT3;
			case Mobile.GAME_A: return Mobile.KEY_NUM1;
			case Mobile.GAME_B: return Mobile.KEY_NUM3;
			case Mobile.GAME_C: return Mobile.KEY_NUM7;
			case Mobile.GAME_D: return Mobile.KEY_NUM9;
		}
		return Mobile.NOKIA_SOFT3;
	}

	public String getKeyName(int keyCode)
	{
		if(keyCode<0) { keyCode=0-keyCode; }
		switch(keyCode)
		{
			case 1: return "UP";
			case 2: return "DOWN";
			case 5: return "LEFT";
			case 6: return "RIGHT";
			case 8: return "FIRE";
			case 9: return "A";
			case 10: return "B";
			case 11: return "C";
			case 12: return "D";
			case 48: return "0";
			case 49: return "1";
			case 50: return "2";
			case 51: return "3";
			case 52: return "4";
			case 53: return "5";
			case 54: return "6";
			case 55: return "7";
			case 56: return "8";
			case 57: return "9";
			case 42: return "*";
			case 35: return "#";
		}
		return "-";
	}

	public boolean hasPointerEvents() { return true; }

	public boolean hasPointerMotionEvents() { return false; }

	public boolean hasRepeatEvents() { return true; }

	public void hideNotify() { }

	public boolean isDoubleBuffered() { return true; }

	public void keyPressed(int keyCode) { }

	public void keyReleased(int keyCode) { }

	public void keyRepeated(int keyCode) { }

	public void keyPressed(int platKey, KeyEvent keyEvent) {
		// technically commands should be supported
		// but no default command

		int key = Mobile.normalizeKey(platKey);

		if (listCommands) {
			keyPressedCommands(key);
		} else {
			if (key == Mobile.NOKIA_SOFT1 && commands.size()>0) {
				doLeftCommand();
			} else if (key == Mobile.NOKIA_SOFT2 && commands.size()>1) {
				doRightCommand();
			} else {
				keyPressed(platKey);
			}
		}
	}

	public void keyReleased(int keyCode, KeyEvent keyEvent) {
		if (!listCommands) {
			keyReleased(keyCode);
		}
	}

	public void keyRepeated(int keyCode, KeyEvent keyEvent) {
		if (!listCommands) {
			keyRepeated(keyCode);
		}
	}

	protected abstract void paint(Graphics g);

	public void pointerDragged(int x, int y) { }

	public void pointerPressed(int x, int y) { }

	public void pointerReleased(int x, int y) { }

	public void repaint()
	{
		repaint(0, 0, width, height);
	}

	public void repaint(int x, int y, int width, int height)
	{
		Display.LCDUILock.lock();
		try {
			if (getDisplay().current != this) {
				// we'll render again on notifySetCurrent
				return;
			}

			if (listCommands) {
				return;
			}

			if (isInsidePaint) {
				// we need this to avoid stackoverflow
				// but it seems the underlying problem is that when paint calls
				// repaint, we shouldn't even land here...
				Mobile.getDisplay().callSerially(() -> {
					repaint(x, y, width, height);
				});
				return;
			}

			gc.reset();

			isInsidePaint = true;
			try {
				paint(gc);
			} catch(Exception e) {
				System.out.println("WARN: exception in paint" + e);
				e.printStackTrace();
			}  finally {
				isInsidePaint = false;
			}

			if (shouldRepaintBar) {
				paintCommandsBar();
				shouldRepaintBar = false;
			}

			Mobile.getPlatform().repaint(platformImage, x, y, width, height);
		} finally {
			Display.LCDUILock.unlock();
		}
	}

	private void paintCommandsBar() {
		System.out.println("koko: paintin bar");
		if (fullscreen) {
			return;
		}

		gc.reset();

		gc.setFont(uiFont);
		gc.setColor(0xcccccc);
		gc.fillRect(0, height-barHeight, width, barHeight);

		if (!commands.isEmpty()) {
			gc.setColor(0x222222);
			gc.drawString(commands.size() > 2 ? "Options" : commands.get(0).getLabel(), barPadding, height-barHeight+barPadding, Graphics.LEFT);
		}

		if (commands.size() == 2) {
			gc.drawString(commands.get(1).getLabel(), width-barPadding, height-barHeight+barPadding, Graphics.RIGHT);
		}
	}

	public void serviceRepaints()
	{
		if (Mobile.getDisplay().getCurrent() == this)
		{
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}

	private final void setFullScreenInternal(boolean mode) {
		fullscreen = mode;
		gc.setBarHeight(mode ? 0 : barHeight);
		shouldRepaintBar = true;
	}

	public void setFullScreenMode(boolean mode)
	{
		//System.out.print("Set Canvas Full Screen Mode ");
		if (mode != fullscreen) {
			setFullScreenInternal(mode);
			_invalidate();
		}
	}

	public void showNotify() { }

	protected void sizeChanged(int w, int h) { } // ??

	public void notifySetCurrent() { _invalidate(); }

	public int getWidth() { return width; }

	public int getHeight() {
		// this is quite problematic because games check this before adding commands
		// so if we'd only include the bar if there are commands, games could
		// get invalid resolution
		return fullscreen ? height : height - barHeight;
	}

	public void addCommand(Command cmd)	{
		commands.add(cmd);
		shouldRepaintBar = true;
		_invalidate();
	}

	public void removeCommand(Command cmd) {
		commands.remove(cmd);
		shouldRepaintBar = true;
		_invalidate();
	}

	protected void render() {
		if (listCommands) {
			shouldRepaintBar = true;
			super.render();
		} else {
			repaint();
		}
	}
}
