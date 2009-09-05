package org.bd.banglasms.bangla;

import javax.microedition.lcdui.Image;

/**
 * This class knows Bangla Font images and other drawing related informations.
 * 
 */
public class BanglaFont {
	public Image image;
	public int xFactor = 0;
	public int widthFactor = 0;

	BanglaFont(Image image, int key, int times, int pos) {
		this.image = image;
		if (key == 10) {
			if (times == 0 && pos == 1) {
				// hrosso ii-kar
				this.widthFactor = 6;
			} else if (times == 0 && pos == 2) {
				// dhirgo ii-kar, ou-kar right frag
				this.xFactor = 6;
				this.widthFactor = 6;
			} else if (times == 1) {
				if (pos == 0 || pos == 1) {
					// hrosso uu-kar, dhirgo uu-kar
					this.xFactor = 4;
					this.widthFactor = 5;
				} else if (pos == 2) {
					// hri-kar
					this.xFactor = 4;
					this.widthFactor = 3;
				}
			} else if (times == 2 && pos == 2) {
				// ou-kar right frag.
				this.xFactor = 5;
				this.widthFactor = 5;
			} else if (times == 3) {
				if (pos == 0) {
					// hosonto
					this.widthFactor = 3;
				} else if (pos == 1) {
					// ref
					this.xFactor = 2;
					this.widthFactor = 3;
				} else if (pos == 3) {
					// rofola
					this.xFactor = 8;
					this.widthFactor = 7;
				} else if (pos == 4) {
					// bofola
					this.xFactor = 5;
					this.widthFactor = 4;
				}
			}
		}

		else if (key == 1) {
			if (times == 0) {
				if (pos == 0) {
					// dari
					this.xFactor = -2;
					this.widthFactor = -5;
				}
				if (pos == 1) {
					// koma
					this.xFactor = -1;
					this.widthFactor = -2;
				}
				if (pos == 2) {
					// prosno
					this.xFactor = -2;
					this.widthFactor = -4;
				}

			} else if (times == 1) {
				if (pos == 1) {
					this.xFactor = 8;
					this.widthFactor = 9;
				}
			}
		} else if (key == 0 && times == 2) // for new line
		{
			this.xFactor = 99;
		}
	}

	BanglaFont(Image image, boolean newLine, boolean toggle)// for English mode
	{
		this.image = image;

		if (toggle)
			this.xFactor = 98;// toggle value
		else if (newLine)
			this.xFactor = 99;

	}
}