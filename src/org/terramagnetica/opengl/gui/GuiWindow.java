/* <LICENSE>
Copyright (C) 2013-2016 Louis JEAN

This file is part of Terra Magnetica.

Terra Magnetica is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Terra Magnetica is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.opengl.gui;

import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.miscellaneous.Timer;

public class GuiWindow {
	
	private static GuiWindow instance = new GuiWindow();
	
	public static GuiWindow getInstance() {
		return GuiWindow.instance;
	}
	
	
	
	private boolean created = false;
	private boolean closeRequested = false;
	private String title;
	private ByteBuffer[] icons = new ByteBuffer[0];
	
	private GuiComponent contentPane;
	
	private GLOrtho ortho2D = new GLOrtho();
	private float scale = 1;
	
	public final Timer timer = new Timer();
	
	protected InputThread inputThread = new InputThread();
	public MouseInput mainMouseInput = new MouseInput();
	public KeyboardInput mainKeyboardInput = new KeyboardInput();
	
	private GuiWindow() {
		
	}
	
	protected class InputThread extends Thread {
		
		private boolean running = false;
		
		public InputThread() {
			super("Input-Thread");
			this.setDaemon(true);
			this.setPriority(1);
		}
		
		
		@Override
		public void run() {
			this.running = true;
			
			while (this.running) {
				mainMouseInput.registerInput();
				mainKeyboardInput.registerInput();
				
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setTitle(String title) {
		this.title = title == null ? "Game" : title;
	}
	
	public void setIcon(ByteBuffer[] icons) {
		this.icons = icons == null ? new ByteBuffer[0] : icons;
	}
	
	public void createWindow() throws LWJGLException {
		this.closeRequested = false;
		
		DisplayMode displayMode = new DisplayMode(800, 600);
		PixelFormat format = new PixelFormat(8, 8, 8);
		
		Display.setDisplayMode(displayMode);
		Display.setTitle(this.title);
		Display.setIcon(this.icons);
		Display.setResizable(true);
		
		Display.create(format);
		Keyboard.create();
		Mouse.create();
		
		enableInput(true);
	}
	
	public void enableInput(boolean input) {
		if (input) {
			if (!this.inputThread.running) {
				this.inputThread.start();
			}
		}
		else {
			if (this.inputThread.running) {
				this.inputThread.running = false;
				this.inputThread = new InputThread();
			}
		}
	}
	
	/** Repeint la fenêtre et execute les actions dues aux entrées de l'utilisateur. */
	public void update() {
		
		Display.update();
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		Painter.instance.clearScreen();
		
		this.contentPane.draw();
		
		sendEvents();
		this.contentPane.processLogic();
		//TODO mettre le dessin de l'interruption dans la méthode #draw() et pas dans listen c'est sale.
		Painter.instance.flush();
		
		if (Display.isCloseRequested()) {
			this.closeRequested = true;
		}
	}
	
	public void destroy() {
		Display.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		
		enableInput(false);
	}


	public void ignoreCloseRequestedOnce() {
		this.closeRequested = false;
	}
	
	public boolean isCloseRequested() {
		return this.closeRequested;
	}
	
	void sendEvents() {
		mainMouseInput.sendEvents(this.contentPane);
		mainKeyboardInput.sendEvents(this.contentPane);
		
		WriterInput.allSendEvents(); 
	}
	
	public void setContentPane(GuiComponent contentPane) {
		this.contentPane = contentPane;
	}
	
	public GuiComponent getContentPane() {
		return this.contentPane;
	}
	
	/** place le repère openGL par défaut. */
	public void setOrthoDefault() {
		int width = Display.getWidth();
		int height = Display.getHeight();
		
		if (width > 700 && height > 700) {
			this.scale = 3;
		}
		else if (width > 450 && height > 450) {
			this.scale = 2;
		}
		else {
			this.scale = 1;
		}
		
		setOrthoRelative(this.scale * 100, this.scale * 100, 1, -1);
	}
	
	/**
	 * (2D) place le repere openGL de façon relative à la taille de l'affichage.
	 * Le point 0;0;0 est situé au centre de l'écran
	 * @param gradX - combien de pixels pour une graduation du repere opengl en abscisse
	 * @param gradY - combien de pixels pour une graduation du repere opengl en ordonnée
	 * @param zNear - glOrtho(zNear)
	 * @param zFar - glOrtho(zFar)
	 */
	public void setOrthoRelative(double gradX, double gradY, double zNear, double zFar) {
		if (!Display.isCreated()){
			return;
		}
		
		double h = Display.getHeight();
		double w = Display.getWidth();
		
		ortho2D.left = - w / (gradX * 2.0);
		ortho2D.right = w / (gradX * 2.0);
		ortho2D.bottom = - h / (gradY * 2.0);
		ortho2D.top = h / (gradY * 2.0);
		ortho2D.near = zNear;
		ortho2D.far = zFar;
	}
	
	/** (2D) place le repere openGL en fonction des côtés de l'affichage. */
	public void setOrtho(double left, double right, double bottom, double top,
			double zNear, double zFar) {
		ortho2D = new GLOrtho(left, right, bottom, top, zNear, zFar);
	}
	
	public GLOrtho getOrtho() {
		return ortho2D;
	}
	
	/** (2D) raffraichit la vue openGL de façon à ce que le repère réel
	 * corresponde bien au repère que l'on peut obtenir à l'aide de 
	 * {@link #getOrtho()} */
	public void refreshOrtho() {
		//TODO Intégrer cette méthode dans #setOrtho
		GL11.glOrtho(ortho2D.left, ortho2D.right, ortho2D.bottom, ortho2D.top,
				ortho2D.near, ortho2D.far);
	}
	
	/** Donne l'échelle du repère openGL. */
	public double getScale() {
		return this.scale;
	}
	
	/** donne une longueur sur le repere openGL à partir d'une longueur en pixel. */
	public double getWidthOnGLOrtho(int widthOnDisplay) {
		double x = widthOnDisplay;
		double scaleFactor = (ortho2D.right - ortho2D.left) / (double) Display.getWidth();
		
		return x * scaleFactor;
	}
	
	/** donne une hauteur sur le repere openGL à partir d'une longueur en pixel. */
	public double getHeightOnGLOrtho(int heightOnDisplay) {
		double y = heightOnDisplay;
		double scaleFactor = (ortho2D.top - ortho2D.bottom) / (double) Display.getHeight();
		
		return y * scaleFactor;
	}
	
	public int getWidthOnDisplay(double widthOnGLOrtho) {
		double x = widthOnGLOrtho;
		double scaleFactor = (double) Display.getWidth() / (ortho2D.right - ortho2D.left);
		
		return (int) (x * scaleFactor);
	}
	
	public int getHeightOnDisplay(double heightOnGLOrtho) {
		double y = heightOnGLOrtho;
		double scaleFactor = (double) Display.getHeight() / (ortho2D.top - ortho2D.bottom);
		
		return (int) (y * scaleFactor);
	}
	
	/** donne l'abscisse openGL à partir d'une abscisse basée sur les coordonnées à l'affichage */
	public double getXOnGLOrtho(int XOnDisplay){
		if (!Display.isCreated()){
			return 0;
		}
		
		double x = XOnDisplay;
		double width = Display.getWidth();
		double result = x / width * (ortho2D.right - ortho2D.left) + ortho2D.left; 
		
		return result;
	}
	
	/** donne l'ordonnée openGL, à partir d'une ordonnée basée sur les
	 * coordonnées à l'affichage en pixels. */
	public double getYOnGLOrtho(int YOnDisplay){
		if (!Display.isCreated()){
			return 0;
		}
		
		double y = YOnDisplay;
		double height = Display.getHeight();
		double result = 0;
		
		result = y / height * (ortho2D.top - ortho2D.bottom) + ortho2D.bottom; 
		
		return result;
	}
	
	/** donne l'abscisse sur l'affichage à partir de l'abscisse sur le repere openGL */
	public int getXOnDisplay(double XOnGLOrtho){
		if (!Display.isCreated()){
			return 0;
		}
		
		double x = XOnGLOrtho;
		double width = Display.getWidth();
		int result = 0;
		
		result = (int)((x - ortho2D.left) / (ortho2D.right - ortho2D.left) * width); 
		
		return result;
	}
	
	/** donne l'ordonnée sur l'affichage à partir de l'ordonnée sur le repere openGL */
	public int getYOnDisplay(double YOnGLOrtho) {
		if (!Display.isCreated()){
			return 0;
		}
		
		double y = YOnGLOrtho;
		double height = Display.getHeight();
		int result = 0;
		
		result = (int)((y - ortho2D.bottom) / (ortho2D.top - ortho2D.bottom) * height); 
		
		return result;
	}
	
	public MouseInput getMouseInput() {
		return this.mainMouseInput;
	}
	
	public KeyboardInput getKeyboardInput() {
		return this.mainKeyboardInput;
	}
	
}
