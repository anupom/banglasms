package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * GridBox shows user a list of selectable grids and notifies
 * {@link GridBoxHandler} on selection.
 *
 */
public class GridBox implements CustomCanvasComponent {

	private int maxRows = 0;
	private int maxCols = 0;

	private final int SHADOW_WIDTH = 4;
	private final int OFFSET = 4;
	private int currentPage = 0;// we divide the display into pages, 1 page is displayed in 1 screen
	private int currentIndex = 0;// index in the array
	private int rows = 0;// number of rows in a page,dynamically decided by canvas size in init()
	private int cols = 0;

	private int gridCellWidth = 0;
	private int gridCellHeight = 0;
	private int pageTotal = 0;// total number of page, dynamically decided from array size in init
	private int start = 0;
	private int end = 0;
	/** Creates a new instance of GridBox */
	private Image pageImages[] = null;
	private GridBoxHandler handler;
	private CustomCanvasComponent parent;
	private int width;
	private int height;
	private Command commandAdd;

	public GridBox(Image[] images, int gridCellWidth, int gridCellHeight,
			int maxNumOfRows, int maxNumOfCols, int width, int height) { //TODO let this decide for itself max row/col size
		if (images == null || images.length == 0) {
			throw new IllegalArgumentException("GridBox must have some images");
		}
		this.pageImages = images;
		this.gridCellWidth = gridCellWidth;
		this.gridCellHeight = gridCellHeight;
		this.maxRows = maxNumOfRows;
		this.maxCols = maxNumOfCols;
		sizeChanged(width, height);
		init();
	}

	public void setGridBoxHandler(GridBoxHandler handler) {
		this.handler = handler;
	}

	private void init() {
		this.currentIndex = 0;
		this.currentPage = 0;
		this.rows = (this.getHeight() - OFFSET * 2) / this.gridCellHeight;
		if (this.rows > this.maxRows) {
			this.rows = maxRows;
		}
		this.cols = (this.getWidth() - OFFSET * 2) / this.gridCellWidth;
		if (this.cols > this.maxCols) {
			this.cols = maxCols;
		}
		this.pageTotal = this.pageImages.length / (rows * cols);
		if (this.pageImages.length % (rows * cols) != 0) {
			this.pageTotal++;
		}
		this.start = this.currentPage * this.rows * this.cols;
		this.end = (this.currentPage == this.pageTotal - 1) ? this.pageImages.length - 1
				: start + rows * cols;
	}

	public void paint(Graphics g) {

		// super.repaint(); // NEVER USE THIS.. PROB WITH SIEMENS :(

		int i = 0;
		this.start = this.currentPage * this.rows * this.cols;
		this.end = (this.currentPage == this.pageTotal - 1) ? this.pageImages.length - 1
				: start + rows * cols - 1;
		int drawX = OFFSET;
		int drawY = this.getHeight() - rows * this.gridCellHeight
				- this.SHADOW_WIDTH;
		g.setColor(-1);// White
		g.fillRect(drawX, drawY, cols * this.gridCellWidth, rows
				* this.gridCellHeight);

		// shadow
		g.setColor(50, 50, 50);
		g.fillRect(drawX + SHADOW_WIDTH, drawY + rows * this.gridCellHeight,
				cols * this.gridCellWidth, SHADOW_WIDTH);
		g.fillRect(drawX + cols * this.gridCellWidth, drawY + SHADOW_WIDTH,
				SHADOW_WIDTH, rows * this.gridCellHeight);
		final int halfWidth = this.gridCellWidth / 2;
		final int halfHeight = this.gridCellHeight / 2;
		
		for (i = start; i <= start + rows * cols - 1; i++) {
			// background
			g.setColor(220, 220, 220);
			g.fillRect(drawX, drawY, this.gridCellWidth, this.gridCellHeight);			

			if (i == this.currentIndex)// for selected cell
			{
				g.setColor(255, 255, 255);
				g.fillRect(drawX, drawY, this.gridCellWidth,
						this.gridCellHeight);				
			}

			if (i <= end)// draw image
			{
				if (this.pageImages[i] != null) {
					g.drawImage(this.pageImages[i], drawX + halfWidth, drawY
							+ halfHeight, Graphics.HCENTER | Graphics.VCENTER);
				}
			}
			// go to next line
			if ((i + 1) % cols == 0) {
				drawX = OFFSET;
				drawY += this.gridCellHeight;
			} else {
				drawX += this.gridCellWidth;
			}
		}
		
		// grid loop
		g.setColor(100, 100, 100);
		drawX = OFFSET;
		drawY = this.getHeight() - rows * this.gridCellHeight - this.SHADOW_WIDTH;
		for (i = start; i <= end; i++) {
			g.drawRect(drawX, drawY, this.gridCellWidth, this.gridCellHeight);
			if ((i + 1) % cols == 0) {
				drawX = OFFSET;
				drawY += this.gridCellHeight;
			} else {
				drawX += this.gridCellWidth;
			}
		}//TODO well, i m sure we can use only one loop, someone has time to remove this loop and merge with the below one
	}

	public void keyPressed(int keyCode, int gameActionCode) {
		if (gameActionCode == Canvas.LEFT) {
			if (this.currentIndex - 1 >= start) {
				this.currentIndex--;
			} else {
				goPrevPage();
			}
		} else if (gameActionCode == Canvas.RIGHT) {

			if (this.currentIndex + 1 <= end) {
				this.currentIndex++;
				parent.repaintNeeded();
			} else {
				this.goNextPage();
			}
		} else if (gameActionCode == Canvas.UP) {

			if (this.currentIndex - cols >= start) {
				this.currentIndex -= cols;
			} else {
				goPrevPage();
			}
		} else if (gameActionCode == Canvas.DOWN) {
			if (this.currentIndex + cols <= end) {
				this.currentIndex += cols;
			}
			else {
				goNextPage();
			}
		} else if (keyCode == Canvas.KEY_POUND) {
			this.goNextPage();
		} else if (gameActionCode == Canvas.FIRE) {
			notifyGridSelected();
		}
		parent.repaintNeeded();
	}

	private void notifyGridSelected(){
		if (this.handler != null) {
			this.handler.gridSelected(currentIndex);
		}
	}

	void goPrevPage() {
		if (this.currentPage - 1 >= 0) {
			this.currentPage--;
		} else {
			currentPage = pageTotal - 1;
		}
		this.currentIndex = currentPage * rows * cols;
		parent.repaintNeeded();
	}

	void goNextPage() {

		if (this.currentPage + 1 <= this.pageTotal - 1) {
			this.currentPage++;
		} else {
			this.currentPage = 0;
		}
		this.currentIndex = currentPage * rows * cols;
		parent.repaintNeeded();
	}

	public CustomCanvasComponent getParent() {
		return this.parent;
	}

	public void keyReleased(int keyCode, int gameActionCode) {
	}

	public void keyRepeated(int keyCode, int gameActionCode) {
		this.keyPressed(keyCode, gameActionCode);
	}

	public void setParent(CustomCanvasComponent parent) {
		this.parent = parent;
	}

	public void sizeChanged(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void showNotify() {

	}

	public void hideNotify() {

	}
	
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void commandAction(Command command) {
		if(command == this.commandAdd){
			notifyGridSelected();
		}
	}

	public void setAddCommand(Command commandAddLetter) {
		this.commandAdd = commandAddLetter;
	}

	public void addCommand(Command command) { // this class wont have any sub component, so no worries to implement
	}

	public void addComponent(CustomCanvasComponent component) {	// this class wont have any sub component, so no worries to implement	
	}

	public void removeCommand(Command command) {// this class wont have any sub component, so no worries to implement
	}

	public void removeComponent(CustomCanvasComponent component) {	// this class wont have any sub component, so no worries to implement
	}

	public void repaintNeeded() {// this class wont have any sub component, so no worries to implement
	}

	public void repaintNeeded(int x, int y, int width, int height) {// this class wont have any sub component, so no worries to implement
	}
}
