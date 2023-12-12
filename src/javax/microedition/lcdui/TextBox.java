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

import org.recompile.mobile.PlatformGraphics;

public class TextBox extends Screen
{
	private String text;
	private int max;
	private int constraints;
	private String mode;
	private int caretPosition;
	private int padding;
	private int margin;


	public TextBox(String Title, String value, int maxSize, int Constraints)
	{
		title = Title;
		text = value;
		max = maxSize;
		constraints = Constraints;


		padding = uiLineHeight / 5;
		margin = uiLineHeight / 5;
	}

	public void delete(int offset, int length)
	{
		text = text.substring(0, offset) + text.substring(offset+length);
		if (caretPosition > text.length()) {
			caretPosition = text.length();
		}
		_invalidate();
	}

	public int getCaretPosition() { return caretPosition; }

	public int getChars(char[] data)
	{
		for(int i=0; i<text.length(); i++)
		{
			data[i] = text.charAt(i);
		}
		return text.length();
	}

	public int getConstraints() { return constraints; }

	public int getMaxSize() { return max; }

	public String getString() { return text; }

	public void insert(char[] data, int offset, int length, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(data, offset, length);
		out.append(text.substring(position));
		text = out.toString();

		_invalidate();
	}

	public void insert(String src, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(src);
		out.append(text.substring(position));
		text = out.toString();

		_invalidate();
	}

	public void setChars(char[] data, int offset, int length)
	{
		StringBuilder out = new StringBuilder();
		out.append(data, offset, length);
		text = out.toString();
		caretPosition = text.length();
		_invalidate();
	}

	public void setConstraints(int Constraints) { constraints = Constraints;  }

	public void setInitialInputMode(String characterSubset) { mode = characterSubset; }

	public int setMaxSize(int maxSize) { max = maxSize; return max; }

	public void setString(String value) { 
		text = value;
		caretPosition = text.length();
		_invalidate();
	}

	public int size() { return text.length(); }

	
	public boolean screenKeyPressed(int key, int platKey, KeyEvent e) {
		boolean handled = true;
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_BACK_SPACE && caretPosition > 0) {
			text = text.substring(0, caretPosition-1) + text.substring(caretPosition);
			caretPosition--;
		} else if (code == KeyEvent.VK_DELETE && caretPosition < text.length()) {
			text = text.substring(0, caretPosition) + text.substring(caretPosition+1);
		} else if (code == KeyEvent.VK_LEFT && caretPosition > 0) {
			caretPosition--;
		} else if (code == KeyEvent.VK_RIGHT && caretPosition < text.length()) {
			caretPosition++;
		} else if (e.getKeyChar() > ' ' && e.getKeyChar() < 0x7f) {
			char chr = e.getKeyChar();
			boolean ok = true;

			if (constraints == TextField.NUMERIC && !((chr >= '0' && chr <= '9') || chr == '-')) {
				ok = false;
			} else if (constraints == TextField.DECIMAL && !((chr >= '0' && chr <= '9') || chr == '-' || chr == '.' || chr == ',')) {
				ok = false;
			}

			if (ok) {
				text = text.substring(0, caretPosition) + String.valueOf(chr) + text.substring(caretPosition);
				caretPosition++;
			} else {
				handled = false;
			}
		} else {
			handled = false;
		}
		
		if (handled) {
			_invalidate();
		}

		return handled;
	}

	protected String renderScreen(int x, int y, int width, int height) {
		gc.getGraphics2D().translate(x, y);

		gc.setColor(0x000000);
		gc.drawRect(margin, margin, width-2*margin, uiLineHeight+2*padding);

		gc.drawString(text, margin+padding, margin+padding, 0);

		int cwidth = uiFont.stringWidth(text.substring(0, caretPosition));

			gc.drawRect(margin+padding+cwidth, margin+padding, 0, uiLineHeight);
		
		gc.getGraphics2D().translate(-x, -y);
		return null;
	}

 }
