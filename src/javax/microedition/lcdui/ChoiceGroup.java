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
import org.recompile.mobile.PlatformGraphics;



public class ChoiceGroup extends Item implements Choice
{
    private int type;

	private ArrayList<String> strings = new ArrayList<String>();

	private ArrayList<Image> images = new ArrayList<Image>();

	private int fitPolicy;

	private int selectedIndex = -1;
	private int hilightedIndex = -1;
	private ArrayList<Boolean> selectedElements = new ArrayList<Boolean>();

	private int paddingBottom;

	public ChoiceGroup(String choiceLabel, int choiceType)
	{
		setLabel(choiceLabel);
		type = choiceType;
		paddingBottom = lineHeight / 6;
	}

	public ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements)
	{
		this(choiceLabel, choiceType);
		for(int i=0; i<stringElements.length; i++) {
			strings.add(stringElements[i]);
			images.add((imageElements != null && i<imageElements.length) ? imageElements[i] : null);
			selectedElements.add(false);
		}

		if (!strings.isEmpty()) {
			selectedIndex = 0;
		}
	}

	ChoiceGroup(String choiceLabel, int choiceType, boolean validateChoiceType)
	{
		this(choiceLabel, choiceType);
	}

	ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements, boolean validateChoiceType)
	{
		this(choiceLabel, choiceType, stringElements, imageElements);
	}

	public int append(String stringPart, Image imagePart) { 
		strings.add(stringPart);
		images.add(imagePart);
		selectedElements.add(false);

		if (!strings.isEmpty() && selectedIndex == -1) {
			selectedIndex = 0;
		}
		invalidate();

		return strings.size() - 1;
		
	}

	public void delete(int itemNum) {
		strings.remove(itemNum);
		images.remove(itemNum);
		selectedElements.remove(itemNum);

		if (strings.isEmpty()) {
			selectedIndex = hilightedIndex = -1;
		} else {
			if (selectedIndex > itemNum) {
				selectedIndex--;
			}
			
			if (hilightedIndex > itemNum) {
				hilightedIndex--;
			}
		}


		invalidate();
	}

	public void deleteAll() { 
		strings.clear(); images.clear(); selectedElements.clear();
		selectedIndex = hilightedIndex = -1;
		invalidate();
	}

	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		strings.add(elementNum, stringPart);
		images.add(elementNum, imagePart);
		selectedElements.add(elementNum, false);

		if (selectedIndex >= elementNum) {
			selectedIndex++;
		}
		
		if (hilightedIndex >= elementNum) {
			hilightedIndex++;
		}

		if (!strings.isEmpty() && selectedIndex == -1) {
			selectedIndex = 0;
		}

		invalidate();
	}


	public int getFitPolicy() { return fitPolicy; }

	public Font getFont(int itemNum) { return Font.getDefaultFont(); }

	public Image getImage(int elementNum) { return images.get(elementNum); }

	public int getSelectedFlags(boolean[] selectedArray) { 
		boolean[] ret = new boolean[selectedElements.size()];
		int number = 0;

		for (int t=0; t<ret.length; t++) {
			ret[t] = selectedElements.get(t);
			if (ret[t]) number++;
		}

		return number;
	}

  	public int getSelectedIndex() { 
		return selectedIndex;
	}

	public String getString(int elementNum) { return strings.get(elementNum); }

  	public boolean isSelected(int elementNum) { return selectedElements.get(elementNum); }

  	public void set(int elementNum, String stringPart, Image imagePart)
	{
		strings.set(elementNum, stringPart);
		images.set(elementNum, imagePart);

		_invalidateContents();
	}

	public void setFitPolicy(int policy) { fitPolicy = policy; }

	public void setFont(int itemNum, Font font) { }

  	public void setSelectedFlags(boolean[] selectedArray) { 
		for (int t=0; t<selectedArray.length && t<size(); t++) {
			selectedElements.set(t, selectedArray[t]);
		}

		_invalidateContents();
	}

  	public void setSelectedIndex(int elementNum, boolean selected) {
		if (type != Choice.MULTIPLE && selected) {
			selectedIndex = elementNum;
		}
		selectedElements.set(elementNum, selected);
		_invalidateContents();
	}

	public int size() { return strings.size(); }

	protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
		if (type == Choice.POPUP) {
			return false;
		}
		
		// intial traverse
		if (hilightedIndex == -1) {
			if (!strings.isEmpty()) {
				hilightedIndex = dir == Canvas.UP ? strings.size() - 1 : 0;
				return true;
			} else {
				return false;
			}
		} else {
			if (dir == Canvas.UP && hilightedIndex > 0) {
				hilightedIndex--;
			} else if (dir == Canvas.DOWN && hilightedIndex < size()-1) {
				hilightedIndex++;
			} else {
				return false;
			}

			visRect_inout[1] = lineHeight * hilightedIndex;
			visRect_inout[3] = lineHeight;

			_invalidateContents();
			return true;
		}
	}

	protected void traverseOut() { 
		if (hilightedIndex != -1) {
			hilightedIndex = -1;
			_invalidateContents();
		}
	}


	protected boolean keyPressed(int key, int platKey, KeyEvent keyEvent) { 
		boolean handled = true;

		if (type == Choice.POPUP) {
			if (key == Mobile.NOKIA_LEFT && selectedIndex > 0) {
				selectedIndex--;
			} else if (key == Mobile.NOKIA_RIGHT && selectedIndex < size()-1) {
				selectedIndex++;
			} else {
				handled = false;
			}
		} else if (key == Mobile.XKEY_SELECT && hilightedIndex != -1) {
			// space key
			if (type == Choice.EXCLUSIVE) {
				selectedIndex = hilightedIndex;
			} else {
				selectedElements.set(hilightedIndex, !selectedElements.get(hilightedIndex));
			}

			handled = true;
		} else {
			handled = false;
		}

		if (handled) {
			notifyStateChanged();
			_invalidateContents();
		}

		return handled;
	}


	protected int getContentHeight(int width) {
		if (type == Choice.POPUP) {
			return lineHeight + paddingBottom;
		} else {
			return size() * lineHeight + paddingBottom;
		}
	}

	protected void renderItem(PlatformGraphics gc, int x, int y, int width, int height) {
		gc.getGraphics2D().translate(x, y);
		
		if (type == Choice.POPUP) {
			int arrowSpacing = _drawArrow(gc, -1,  selectedIndex > 0, 0, 0, width, height);

			gc.setColor(0x000000);
			gc.drawString(strings.get(selectedIndex), arrowSpacing, 0, 0);

			_drawArrow(gc, 1, selectedIndex < size()-1, 0, 0, width, height);
		} else {
			int tickOffset = lineHeight*4/3;
			int textPadding = lineHeight/5;

			for (int t=0; t<strings.size(); t++) {
				if (type == Choice.MULTIPLE) {
					_drawTick(gc, t, lineHeight, selectedElements.get(t).booleanValue(), false);					
				} else {
					_drawTick(gc, t, lineHeight, t == selectedIndex, true);					
				}

				if (hilightedIndex == t) {
					gc.fillRect(tickOffset, t*lineHeight, width-tickOffset, lineHeight);
					gc.setColor(0xffffff);
				}

				if (images.get(t) != null) {
					gc.drawImage(images.get(t), tickOffset+textPadding, t*lineHeight, 0);

					gc.drawString(strings.get(t), tickOffset+textPadding+lineHeight, t*lineHeight, 0);
				} else {
					gc.drawString(strings.get(t), tickOffset+textPadding, t*lineHeight, 0);
				}

				gc.setColor(0x000000);
			}
		}

		gc.getGraphics2D().translate(-x, -y);
	}

	private void _drawTick(PlatformGraphics gc, int index, int height, boolean filled, boolean isCircle) {
		int tickMargin = height/2;
		int tickWidth = height/2;
	  
		if (isCircle) {
			if (filled) {
				gc.fillArc(tickMargin, index * height + height/2 - tickWidth/2,  
					tickWidth, tickWidth, 0, 360);
			} else {
				gc.drawArc(tickMargin, index * height + height/2 - tickWidth/2,
					tickWidth, tickWidth, 0, 360);
			}
		} else {
			if (filled) {
				gc.fillRect(tickMargin, index * height + height/2 - tickWidth/2,
					tickWidth, tickWidth);
			} else {
				gc.drawRect(tickMargin, index * height + height/2 - tickWidth/2,
					tickWidth, tickWidth);
			}
		}  
	}
}