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
import java.awt.FontMetrics;


public class StringItem extends Item
{

	private String text;
	private int appearance;
	private Font font;
	protected List<String> lines;
	protected int lineHeight;
	protected int lineSpacing;
	protected int height;


	public StringItem(String label, String textvalue)
	{
		setLabel(label);
		text = textvalue;
		font = Font.getDefaultFont();
	}

	public StringItem(String label, String textvalue, int appearanceMode)
	{
		setLabel(label);
		text = textvalue;
		appearance = appearanceMode;
		font = Font.getDefaultFont();
	}

	public int getAppearanceMode() { return appearance; }

	public Font getFont() { return font; }

	public String getText() { return text; }

	public void setFont(Font newfont) { font = newfont; }

	public void setText(String textvalue) { text = textvalue; }

	protected void generateLayout(FontMetrics fm, int width) {
		lines = wrapText(fm, this.getText(), width);
		lineHeight = fm.getHeight();
		lineSpacing = 1;

		height = lines.size() > 0 ? (lines.size()*lineHeight + (lines.size()-1)*lineSpacing) : 0;

	}

	private List<String> wrapText(FontMetrics fm, String text, int width) {
		String[] lines = text.split("\n");
		List<String> wrappedLines = new ArrayList<>();
	
		for (String line : lines) {
			String[] words = line.split(" ");
			String wrappedLine = "";
	
			for (String word : words) {
				String candidate = wrappedLine.isEmpty() ? word : wrappedLine + " " + word;
				int candidateWidth = fm.stringWidth(candidate);
	
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

}
