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

package org.terramagnetica.game.gui;

import java.util.ArrayList;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiComponent;
import org.terramagnetica.opengl.gui.GuiDialog;
import org.terramagnetica.opengl.gui.KeyboardListener;
import org.terramagnetica.opengl.gui.MouseListener;
import org.terramagnetica.opengl.miscellaneous.Timer;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;

public abstract class GameScreen extends GuiComponent implements Cloneable, GuiConstants {
	
	/** Dessin du fond d'�cran : texture de sol sombre en d�cor
	 * de montagnes */
	protected void drawDefaultBackground(Painter painter) {
		painter.set2DConfig();
		TextureQuad tex = TexturesLoader.getQuad(Util.formatDecimal(GameRessources.SPEC_IMG_TERRAIN, 1) + GameRessources.TEX_SOL);
		painter.setColor(new Color4f(0.5f, 0.5f, 0.5f));
		tex.fillScreen2D(0.5, 0.5, true, painter);
	}
	
	public GameWindow window = GameWindow.getInstance();
	
	private int state = NULL;
	
	private Timer appearChrono = new Timer();
	private int appearChronoTime;
	private Timer destroyChrono = new Timer();
	private int destroyChronoTime;
	
	private TransitionManager transition = null;
	private GameScreen nextPanel;
	private String soundPlayed = GameRessources.SOUND_GUI_BUTTONS;
	
	/** Le GamePanel n'est pas visible. */
	public static final int NULL = 0;
	/** Le GamePanel est en train d'apparaitre. */
	public static final int APPEARING = 1;
	/** Le GamePanel est visible et apparu totalement. */
	public static final int VISIBLE = 2;
	/** Le GamePanel est en train de disparaitre. */
	public static final int DESTROYING = 3;
	
	protected GameScreen() {
		
	}
	
	@Override
	public void draw(Painter painter){
		painter.set2DConfig();
		selectDrawingMode(painter);
		drawChildren(painter);
	}
	
	/** Permet de d�finir le mode de dessin du {@link GameScreen}, selon
	 * son �tat. */
	protected void selectDrawingMode(Painter painter) {
		
		switch (getState()) {
		case VISIBLE :
			drawComponent(painter);
			break;
		case APPEARING :
			appearChronoTime = (int) appearChrono.getTime();
			
			drawComponentAppearing(painter);
			
			if (appearChronoTime >= timeToAppear()) {
				setState(VISIBLE);
				appearChrono.stop();
			}
			break;
		case DESTROYING :
			destroyChronoTime = (int) destroyChrono.getTime();
			
			drawComponentDestroying(painter);
			
			if (destroyChronoTime >= timeToDestroy()){
				setState(NULL);
				if (this.nextPanel != null) window.setPanel(this.nextPanel);
				onDestroy();
				destroyChrono.stop();
			}
			break;
		default :
			break;
		}
	}
	
	/** Dessine le {@link GameScreen} en train d'apparaitre.<br>
	 * Normalement cette m�thode est appel�e par l'interm�diaire de 
	 * {@link GameScreen#selectDrawingMode()}. Par d�faut, elle appelle
	 * la m�thode {@link GuiComponent#drawComponent()} puis joue la 
	 * transition, s'il y en a une. */
	protected void drawComponentAppearing(Painter painter) {
		drawComponent(painter);
		if (transition != null) {
			transition.appear(appearChronoTime);
		}
	}
	
	/** Effectue les action n�cessaires � l'apparition de l'�cran.
	 * N'est appel�e qu'une seule fois par apparition */
	protected void onAppear() {}
	
	/** Dessine le {@link GameScreen} en train de disparaitre.<br>
	 * Normalement cette m�thode est appel�e par l'interm�diaire de 
	 * {@link GameScreen#selectDrawingMode()}. Par d�faut, elle appelle
	 * la m�thode {@link GuiComponent#drawComponent()} puis joue la 
	 * transition, s'il y en a une. */
	protected void drawComponentDestroying(Painter painter) {
		drawComponent(painter);
		if (transition != null) {
			transition.destroy(destroyChronoTime);
		}
	}
	
	/** effectue les actions n�cessaires � la destruction de cet �cran.
	 * N'est appel�e qu'une seule fois par destruction
	 * <p>NB : l'�cran peut r�apparaitre par la suite, la m�thode 
	 * {@link #onAppear()} peut alors �tre � nouveau appel�e pour cet
	 *  �cran. Il en va de m�me pour cette m�thode. */
	protected void onDestroy() {}
	
	/** indique le temps que prend le {@link GameScreen} pour
	 * apparaitre compl�tement, en millisecondes. */
	public abstract int timeToAppear();
	/** indique le temps que prend le {@link GameScreen} pour
	 * disparaitre compl�tement, en millisecondes. */
	public abstract int timeToDestroy();
	
	public int getAppearCount() {
		return (int) appearChrono.getTime();
	}

	public int getDestroyCount() {
		return (int) destroyChrono.getTime();
	}
	
	/** 
	 * Pr�pare cet �cran afin qu'il laisse la place � l'�cran suivant.
	 * Autrement dit, d�clenche l'animation de fermeture de cet �cran.
	 * <p>Cette animation prend souvent plusieurs frames, c'est pourquoi
	 * il est n�cessaire d'informer la fen�tre principale de la fin de
	 * l'animation, lorsqu'elle se termine.
	 * @param o - L'observateur de destruction de l'�cran. Il sera appel�
	 * � la fin de l'animation de destruction de l'�cran.
	 * Celui-ci est normalement la fen�tre qui g�re les diff�rents
	 * tableau de jeu.
	 * @param following - L'�v�nement indiquant ce qu'il faudra faire
	 * apr�s la fermeture de cet �cran. Il sera envoy� � l'observateur
	 * lors de son appel.
	 */
	public void destroy(){
		if (getState() == VISIBLE){
			setState(DESTROYING);
			destroyChrono.start();
			
			if (!"".equals(this.soundPlayed)) SoundManager.playSound(this.soundPlayed);
		}
	}
	
	public void appear(){
		if (getState() == NULL){
			setState(APPEARING);
			appearChrono.start();
			onAppear();
		}
	}
	
	/** @return l'�tat du {@link GameScreen}, c'est � dire s'il est
	 * <code>VISIBLE</code> ou non, s'il est en train d'apparaitre ou
	 * de disparaitre.
	 * @see GameScreen#VISIBLE
	 * @see GameScreen#NULL
	 * @see GameScreen#APPEARING
	 * @see GameScreen#DESTROYING  */
	public int getState() {
		return state;
	}

	protected void setState(int state) {
		this.state = state;
	}
	
	@Override
	public void setVisible(boolean flag) {
		if (flag) this.state = VISIBLE;
		else this.state = NULL;
	}
	
	private ArrayList<GuiComponent> getListenedChildren() {
		ArrayList<GuiComponent> listenedChildren = new ArrayList<GuiComponent>();
		
		for (GuiComponent component : this.children) {
			if (component instanceof GuiDialog) {
				listenedChildren.add(component);
			}
		}
		
		if (listenedChildren.size() == 0) listenedChildren = this.children;
		
		return listenedChildren;
	}
	
	@Override
	public MouseListener[] getMouseListeners() {
		ArrayList<GuiComponent> save = this.children;
		this.children = getListenedChildren();
		
		MouseListener[] result = super.getMouseListeners();
		
		this.children = save;
		return result;
	}
	
	public KeyboardListener[] getKeyboardListener() {
		ArrayList<GuiComponent> save = this.children;
		this.children = getListenedChildren();
		
		KeyboardListener[] result = super.getKeyboardListeners();
		
		this.children = save;
		return result;
	}
	
	public TransitionManager getTransition() {
		return this.transition;
	}
	
	public void setTransition(TransitionManager transition) {
		this.transition = transition;
	}
	
	public void nextPanel(GameScreen screen) {
		this.nextPanel = screen;
		this.destroy();
	}
	
	public String getSoundPlayed() {
		return this.soundPlayed;
	}
	
	/**
	 * Permet de d�finir le son qui sera jou� lors du passage de cet
	 * �cran � l'�cran suivant.
	 * @param soundPlayed - Le son qui doit �tre jou� lors du changement
	 * d'�cran.
	 */
	public void setSoundPlayed(String soundPlayed) {
		this.soundPlayed = soundPlayed == null ? "" : soundPlayed;
	}
	
	/**  
	 * {@inheritDoc}
	 * <p>Par d�faut cette m�thode appelle la m�thode
	 * {@link GuiComponent#processLogic()} de tous les composants enfants,
	 * puis returne l'�venement nul.
	 * <p>Attention, maintenant presque aucune action n'est prise en charge
	 * par les �v�nement retourn�s : toutes les actions effectu�es se font
	 * dans la m�thode (par exemple, le changement d'�cran)
	 * @return {@link GuiActionEvent#NULL_EVENT}
	 */
	@Override
	public GuiActionEvent processLogic() {
		if (state != VISIBLE) {
			for (GuiComponent child : children) {
				child.processLogic();
			}
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	@Override
	public GameScreen clone() {
		GameScreen result = null;
		result = (GameScreen) super.clone();
		
		return result;
	}
}
