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
along with Terra Magnetica. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.opengl.gui;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.miscellaneous.GLFWUtil;
import org.terramagnetica.opengl.miscellaneous.Timer;

public class GuiWindow {
	
	private static int instanceCount = 0;
	private static GuiWindow instance = new GuiWindow();
	
	public static GuiWindow getInstance() {
		return GuiWindow.instance;
	}
	
	
	private long window;
	private Painter painter;
	
	private boolean created = false;
	private boolean closeRequested = false;
	private String title;
	private ByteBuffer[] icons = new ByteBuffer[0];
	
	private GuiComponent contentPane;
	private GuiComponent nextContentPane;
	
	/** Les dimensions réelles de la fenêtre, en pixels. Ces variables sont mises
	 * à jour à chaque tour de boucle.*/
	private int width, height;
	private GLOrtho ortho2D = new GLOrtho();
	private float scale = 1;
	
	public final Timer timer = new Timer();
	
	public MouseInput mainMouseInput = new MouseInput();
	public KeyboardInput mainKeyboardInput = new KeyboardInput();
	
	private GuiWindow() {
		setTitle(null);
	}
	
	public void setTitle(String title) {
		this.title = title == null ? "Game" : title;
		if (this.created) {
			GLFW.glfwSetWindowTitle(this.window, this.title);
		}
	}
	
	public void setIcon(ByteBuffer[] icons) {
		this.icons = icons == null ? new ByteBuffer[0] : icons;
	}
	
	public void createWindow() throws RuntimeException {
		this.closeRequested = false;
		
		if (instanceCount <= 0) {
			instanceCount = 0;
			GLFWErrorCallback.createPrint(System.err).set();
			
			// Initialize GLFW.
			if ( !GLFW.glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		instanceCount++;
		
		// Définition des paramètres de la fenêtre
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
	    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
	    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		
		GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8);
		
		//[Expérimental]
		if (GLConfiguration.GLProperty.MULTISAMPLE.defaultValue)
			GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
		
		// Création de la fenêtre
		this.window = GLFW.glfwCreateWindow(800, 600, this.title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (this.window == MemoryUtil.NULL) {
			throw new RuntimeException("the creation of Terra Magnetica main window failed.");
		}
		
		// Ajout des callbacks
		GLFW.glfwSetCursorPosCallback(this.window, new GLFWCursorPosCallback() {
			@Override public void invoke(long window, double xpos, double ypos) {
				mainMouseInput.addCursorEvent(xpos, height - ypos);
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(this.window, new GLFWMouseButtonCallback() {
			@Override public void invoke(long window, int button, int action, int mods) {
				mainMouseInput.addMouseButtonEvent(button, action, mods);
			}
		});
		
		GLFW.glfwSetScrollCallback(this.window, new GLFWScrollCallback() {
			@Override public void invoke(long window, double xoffset, double yoffset) {
				mainMouseInput.addMouseWheelEvent(xoffset, yoffset);
			}
		});
		
		GLFW.glfwSetKeyCallback(this.window, new GLFWKeyCallback() {
			@Override public void invoke(long window, int key, int scancode, int action, int mods) {
				mainKeyboardInput.addKeyEvent(key, scancode, action, mods);
			}
		});
		
		GLFW.glfwSetCharCallback(this.window, new GLFWCharCallback() {
			@Override public void invoke(long window, int codepoint) {
				mainKeyboardInput.addTextEvent((char) codepoint);
			}
		});
		
		// Ajout de l'icone
		GLFWImage.Buffer icons = null;
		try {
			icons = GLFWImage.malloc(this.icons.length);
			
			for (ByteBuffer icon : this.icons) {
				
			}
			
			//FIXME GLFW.glfwSetWindowIcon(this.window, icons);
		}
		finally {
			icons.free();
		}
		
		GLFW.glfwMakeContextCurrent(this.window);
		GL.createCapabilities();
		
		GLFW.glfwSwapInterval(0);
		
		GLFW.glfwShowWindow(this.window);
		
		this.created = true;
		
		//Préparation de la première frame
		enableInput(true);
		this.painter = new Painter(this);
	}
	
	public void enableInput(boolean input) {
		// FIXME DO ME !
	}
	
	/** Repeint la fenêtre et execute les actions dues aux entrées de l'utilisateur. */
	public void update() {
		
		GLFW.glfwMakeContextCurrent(this.window);
		
		int width[] = new int[1];
		int height[] = new int[1];
		GLFW.glfwGetFramebufferSize(this.window, width, height);
		this.width = width[0];
		this.height = height[0];
		
		GL11.glViewport(0, 0, this.width, this.height);
		this.painter.clearScreen();
		this.painter.initFrame();
		
		pollEvents();
		this.contentPane.processLogic();
		
		this.contentPane.draw(this.painter);
		this.painter.flush();
		
		this.flushContentPane();
		
		if (GLFW.glfwWindowShouldClose(this.window)) {
			this.closeRequested = true;
		}
		
		GLFW.glfwSwapBuffers(this.window);
	}
	
	public void destroy() {
		if (!this.created) return;
		
		enableInput(false);
		
		GLFW.glfwDestroyWindow(this.window);
		
		this.created = false;
		
		//Destruction du contexte GLFW
		instanceCount--;
		
		if (instanceCount <= 0) {
			instanceCount = 0;
			
			GLFW.glfwTerminate();
			GLFW.glfwSetErrorCallback(null).free();
		}
	}


	public void ignoreCloseRequestedOnce() {
		this.closeRequested = false;
	}
	
	public boolean isCloseRequested() {
		return this.closeRequested;
	}
	
	public boolean hasFocus() {
		return GLFW.glfwGetWindowAttrib(this.window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
	}
	
	void pollEvents() {
		GLFW.glfwPollEvents();
		
		mainMouseInput.sendEvents(this.contentPane);
		mainKeyboardInput.sendEvents(this.contentPane);
	}
	
	public void setContentPane(GuiComponent contentPane) {
		if (this.contentPane == null) {
			this.contentPane = contentPane;
		}
		else {
			this.nextContentPane = contentPane;
		}
	}
	
	public GuiComponent getContentPane() {
		return this.contentPane;
	}
	
	private void flushContentPane() {
		if (this.nextContentPane != null) {
			this.contentPane = this.nextContentPane;
			this.nextContentPane = null;
		}
	}
	
	public void setCursorHidden(boolean hidden) {
		GLFW.glfwSetInputMode(this.window, GLFW.GLFW_CURSOR, hidden ? GLFW.GLFW_CURSOR_HIDDEN : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	/** place le repère openGL par défaut. */
	public void setOrthoDefault() {
		if (this.width > 700 && this.height > 700) {
			this.scale = 3;
		}
		else if (this.width > 450 && this.height > 450) {
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
		if (!this.created){
			return;
		}
		
		double h = this.height;
		double w = this.width;
		
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
	
	/** Donne l'échelle du repère openGL. */
	public double getScale() {
		return this.scale;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/** donne une longueur sur le repere openGL à partir d'une longueur en pixel. */
	public double getWidthOnGLOrtho(int widthOnDisplay) {
		double x = widthOnDisplay;
		double scaleFactor = (ortho2D.right - ortho2D.left) / (double) this.width;
		
		return x * scaleFactor;
	}
	
	/** donne une hauteur sur le repere openGL à partir d'une longueur en pixel. */
	public double getHeightOnGLOrtho(int heightOnDisplay) {
		double y = heightOnDisplay;
		double scaleFactor = (ortho2D.top - ortho2D.bottom) / (double) this.height;
		
		return y * scaleFactor;
	}
	
	public int getWidthOnDisplay(double widthOnGLOrtho) {
		double x = widthOnGLOrtho;
		double scaleFactor = (double) this.width / (ortho2D.right - ortho2D.left);
		
		return (int) (x * scaleFactor);
	}
	
	public int getHeightOnDisplay(double heightOnGLOrtho) {
		double y = heightOnGLOrtho;
		double scaleFactor = (double) this.height / (ortho2D.top - ortho2D.bottom);
		
		return (int) (y * scaleFactor);
	}
	
	/** donne l'abscisse openGL à partir d'une abscisse basée sur les coordonnées à l'affichage */
	public double getXOnGLOrtho(int XOnDisplay){
		if (!this.created){
			return 0;
		}
		
		double x = XOnDisplay;
		double width = this.width;
		double result = x / width * (ortho2D.right - ortho2D.left) + ortho2D.left; 
		
		return result;
	}
	
	/** donne l'ordonnée openGL, à partir d'une ordonnée basée sur les
	 * coordonnées à l'affichage en pixels. */
	public double getYOnGLOrtho(int YOnDisplay){
		if (!this.created){
			return 0;
		}
		
		double y = YOnDisplay;
		double height = this.height;
		double result = 0;
		
		result = y / height * (ortho2D.top - ortho2D.bottom) + ortho2D.bottom; 
		
		return result;
	}
	
	/** donne l'abscisse sur l'affichage à partir de l'abscisse sur le repere openGL */
	public int getXOnDisplay(double XOnGLOrtho){
		if (!this.created) {
			return 0;
		}
		
		double x = XOnGLOrtho;
		double width = this.width;
		int result = 0;
		
		result = (int)((x - ortho2D.left) / (ortho2D.right - ortho2D.left) * width); 
		
		return result;
	}
	
	/** donne l'ordonnée sur l'affichage à partir de l'ordonnée sur le repere openGL */
	public int getYOnDisplay(double YOnGLOrtho) {
		if (!this.created) {
			return 0;
		}
		
		double y = YOnGLOrtho;
		double height = this.height;
		int result = 0;
		
		result = (int)((y - ortho2D.bottom) / (ortho2D.top - ortho2D.bottom) * height); 
		
		return result;
	}
	
	public Painter getPainter() {
		return this.painter;
	}
	
	public MouseInput getMouseInput() {
		return this.mainMouseInput;
	}
	
	public KeyboardInput getKeyboardInput() {
		return this.mainKeyboardInput;
	}
	
	public boolean isKeyPressed(int glfwCode) {
		if (!GLFWUtil.keyExists(glfwCode)) {
			return false;
		}
		return GLFW.glfwGetKey(this.window, glfwCode) == GLFW.GLFW_PRESS;
	}
	
	/** Donne le temps du systeme en nanosecondes. */
	public static long getTimeNanos() {
		return (long) (GLFW.glfwGetTime() * 1000000000L);
	}
	
	/** Donne le temps du systeme en millisecondes. */
	public static long getTimeMillis() {
		return (long) (GLFW.glfwGetTime() * 1000);
	}
}
