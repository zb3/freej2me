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

public class Gauge extends Item
{

	public static final int CONTINUOUS_IDLE = 0;
	public static final int CONTINUOUS_RUNNING = 2;
	public static final int INCREMENTAL_IDLE = 1;
	public static final int INCREMENTAL_UPDATING = 3;
	public static final int INDEFINITE = -1;


	private boolean interactive;
	private int maxValue;
	private int value;


	public Gauge(String label, boolean isInteractive, int maxvalue, int initialvalue)
	{
		setLabel(label);
		interactive = isInteractive;
		maxValue = maxvalue;
		value = initialvalue;
	}

	public int getMaxValue() { return maxValue; }

	public int getValue() { return value; }

	public boolean isInteractive() { return interactive; }

	public void setMaxValue(int maxvalue) { 
		if (maxvalue < 0) {
			maxvalue = 0;
		}

		maxValue = maxvalue;
	
		if (value > maxValue) {
			value = maxValue;
		}
		_invalidateContents();
	}

	public void setValue(int newValue) { 
		if (newValue < 0) {
			newValue = 0;
		}
		if (newValue > maxValue) {
			newValue = maxValue;
		}
		value = newValue;
		_invalidateContents();
	}

	protected int getContentHeight(int width) {
		return lineHeight + lineHeight/5; // padding
	}

	protected boolean keyPressed(int key, int platKey, KeyEvent keyEvent) { 
		boolean handled = true;

		if (key == Mobile.NOKIA_LEFT && value > 0) {
			value--;
		} else if (key == Mobile.NOKIA_RIGHT && value < maxValue) {
			value++;
		} else {
			handled = false;
		}

		if (handled) {
			notifyStateChanged();
			_invalidateContents();
		}

		return handled;
	}

	protected void renderItem(PlatformGraphics gc, int x, int y, int width, int height) {
		gc.getGraphics2D().translate(x, y);
		

		int arrowSpacing = _drawArrow(gc, -1,  value > 0, 0, 0, width, lineHeight);

		gc.setColor(0x000000);
		gc.drawRect(arrowSpacing, 0, width-2*arrowSpacing, lineHeight);

		int barWidth = maxValue == 0 ? 0 : ((value * (width-2*arrowSpacing))/maxValue);

		gc.fillRect(arrowSpacing, 0, barWidth, lineHeight);
		
		_drawArrow(gc, 1,  value < maxValue, 0, 0, width, lineHeight);
	
	
		gc.getGraphics2D().translate(-x, -y);
	}



}
