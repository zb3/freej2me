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
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
public abstract class GameCanvas extends Canvas
{
	public static final int UP_PRESSED = 1 << Canvas.UP;
	public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
	public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
	public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
	public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
	public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
	public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
	public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
	public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;

	protected PlatformImage canvasImage;
	private boolean suppressKeyEvents;

	protected GameCanvas(boolean suppressKeyEvents)
	{
		this.suppressKeyEvents = suppressKeyEvents;
		canvasImage = new PlatformImage(width, height);
	}

	protected Graphics getGraphics()
	{
		return canvasImage.getGraphics();
	}

	public void paint(Graphics g) {
		// at this point the canvasImage might not yet be "ready"
		// this could cause a flash
		// g.drawImage(canvasImage, 0, 0, Graphics.TOP | Graphics.LEFT);
	}

	public void flushGraphics(int x, int y, int width, int height)
	{
		// we paint this on the main canvas image so that when we repaint the main canvas
		// it draws the "ready" image
		height = Math.min(getHeight()-y, height-y);
		gc.getGraphics2D().drawImage(canvasImage.getCanvas().getSubimage(x, y, width, height), x, y, null);
		repaint(x, y, width, height);
	}

	public void flushGraphics()
	{
		flushGraphics(0, 0, width, height);
	}

	public int getKeyStates() // found in use
	{
		int t = Mobile.getPlatform().keyState;
		return t;
	}
}
