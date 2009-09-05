package org.bd.banglasms.ui.lcdui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

/**
 * A helper class that can be used to notify different events to other
 * {@link CustomCanvasComponent} s held by o CustomCanvasComponent.
 *
 */
public class CustomCanvasComponentHelper {
	private Vector components = new Vector();

	public void addCustomComponent(CustomCanvasComponent component){
		components.addElement(component);
	}

	public void removeCustomComponent(CustomCanvasComponent component){
		components.removeElement(component);
	}

	public void keyPressed(int keyCode, int gameActionCode){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].keyPressed(keyCode, gameActionCode);
			}
		}
	}

	public void keyRepeated(int keyCode, int gameActionCode){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].keyRepeated(keyCode, gameActionCode);
			}
		}
	}

	public void keyReleased(int keyCode, int gameActionCode){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].keyReleased(keyCode, gameActionCode);
			}
		}
	}

	public void paint(Graphics g){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].paint(g);
			}
		}
	}

	public void showNotify(){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].showNotify();
			}
		}
	}

	public void hideNotify(){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].hideNotify();
			}
		}
	}

	public void sizeChanged(int width, int height){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].sizeChanged(width, height);
			}
		}
	}

	public void commandAction(Command command){
		if(components.size() > 0){
			CustomCanvasComponent[] componentsToNotify = new CustomCanvasComponent[components.size()];
			components.copyInto(componentsToNotify);
			for(int i = 0; i < componentsToNotify.length; i++){
				componentsToNotify[i].commandAction(command);
			}
		}
	}
}
