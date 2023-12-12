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

import java.util.ArrayList;
import java.util.List;

import org.recompile.mobile.PlatformGraphics;

import java.awt.FontMetrics;


public class StringItem extends Item
{

	private String text;
	private int appearance;
	protected List<String> lines;
	protected int lineSpacing;
	protected int height = 0;
	private int buttonMargin;
	private int buttonPadding;

	public StringItem(String label, String textvalue)
	{
		setLabel(label);
		text = textvalue;
		buttonMargin = lineHeight / 5;
		buttonPadding = lineHeight / 3;
	}

	public StringItem(String label, String textvalue, int appearanceMode)
	{
		this(label, textvalue);
		appearance = appearanceMode;
	}

	public int getAppearanceMode() { return appearance; }

	public Font getFont() { return itemFont; }

	public String getText() { return text; }

	public void setFont(Font newfont) {  }

	public void setText(String textvalue) { text = textvalue; height = 0; this._invalidateContents(); }

	protected int getContentHeight(int width) {
		if (appearance == Item.BUTTON) {
			height = lineHeight + 2*buttonMargin + 2*buttonPadding;
		} else if (height == 0 && !text.isEmpty()) {
			lines = wrapText(text, width, itemFont);
			lineSpacing = 1;

			height = lines.size() > 0 ? (lines.size()*lineHeight + (lines.size()-1)*lineSpacing) : 0;
		} else if (text.isEmpty() && lines == null) {
			lines = new ArrayList<>();
		}

		return height;
	}

	protected static List<String> wrapText(String text, int width, Font font) {
		String[] lines = text.split("\n");
		List<String> wrappedLines = new ArrayList<>();
	
		for (String line : lines) {
			String[] words = line.split(" ");
			String wrappedLine = "";
	
			for (String word : words) {
				String candidate = wrappedLine.isEmpty() ? word : wrappedLine + " " + word;
				int candidateWidth = font.stringWidth(candidate);
	
				if (candidateWidth > width) {
					wrappedLines.add(wrappedLine);
					wrappedLine = word;
				} else {
					wrappedLine = candidate;
				}
			}
	
			wrappedLines.add(wrappedLine);
		}
	
		return wrappedLines;
	  }

	protected void renderItem(PlatformGraphics gc, int x, int y, int width, int height) {
		if (appearance == Item.BUTTON) {
			gc.setColor(0xdddddd);
			gc.fillRect(x+buttonMargin, y+buttonMargin, width-2*buttonMargin, height-2*buttonMargin);

			gc.setColor(0x888888);
			gc.drawRect(x+buttonMargin, y+buttonMargin, width-2*buttonMargin, height-2*buttonMargin);

			gc.setColor(0x000000);
			gc.drawString(text, x+buttonMargin+buttonPadding, y+buttonMargin+buttonPadding, 0);
		} else {
			for(int l=0;l<lines.size();l++) {
				gc.drawString(
					lines.get(l),
					x,
					y + l*lineHeight + (l > 0 ? (l-1)*lineSpacing : 0),
					Graphics.LEFT);
			}
		}

	}

}
