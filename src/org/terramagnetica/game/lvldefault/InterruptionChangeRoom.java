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

package org.terramagnetica.game.lvldefault;

import org.terramagnetica.game.GameInterruption;
import org.terramagnetica.game.gui.GuiConstants;
import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.opengl.miscellaneous.Timer;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.MathUtil;

/**
 * Interruption qui joue l'animation du joueur traversant le portail
 * qui mène à la salle dont l'identifiant est indiqué, change de
 * salle pour le jeu en cours et rend la main après.
 * @author Louis JEAN
 */
public class InterruptionChangeRoom extends GameInterruption {
	
	private GamePlayingDefault game;
	private int roomToGoID;
	/** Indique que la salle d'origine doit être cachée. Surtout utilisé au
	 * début du niveau, pour annoncer la salle de départ, il ne faut pas l'afficher
	 * en fondue avant d'afficher le nom. */
	private boolean hide = false;
	
	private Timer chrono = new Timer();
	public static final int TIME_1_STAGE = 500;
	public static final int TIME_2_STAGE = TIME_1_STAGE + 2000;
	public static final int TIME_3_STAGE = TIME_2_STAGE + TIME_1_STAGE;
	public static final int TIME_TOTAL = TIME_3_STAGE;
	public static final int TIME_BETWEEN_1_2 = TIME_2_STAGE - TIME_1_STAGE;
	public static final int TIME_TEXT_FONDUE = 500;
	
	public InterruptionChangeRoom(GamePlayingDefault game, int roomToGoID) {
		if (game == null) throw new IllegalArgumentException("game == null");
		this.game = game;
		this.roomToGoID = roomToGoID;
	}
	
	/**
	 * @param game
	 * @param roomToGoID
	 * @param hide - à true, ce paramètre indique qu'il faut cacher
	 * la salle d'origine
	 */
	public InterruptionChangeRoom(GamePlayingDefault game, int roomToGoID, boolean hide) {
		this(game, roomToGoID);
		this.hide = hide;
	}
	
	@Override
	public void start() {
		this.chrono.stop();
		this.chrono.start();
		this.finished = false;
	}
	
	@Override
	public void update() {
		long time = this.chrono.getTime();
		
		if (time >= TIME_TOTAL) {
			this.finished = true;
			return;
		}
		
		Painter painter = Painter.instance;
		GuiTextPainter textPainter = new GuiTextPainter(painter);
		
		painter.ensure2D();
		
		if (time >= 0 && time < TIME_1_STAGE) {
			if (this.hide) {
				drawSideAnimation((double) (TIME_1_STAGE) / (double) (TIME_1_STAGE), painter);
			}
			else {
				drawSideAnimation((double) (time) / (double) (TIME_1_STAGE), painter);
			}
		}
		if (time >= TIME_1_STAGE && time < TIME_2_STAGE) {
			if (this.game.getRoomID() != this.roomToGoID) {
				Room r = this.game.getLevel().getRoom(this.roomToGoID);
				if (r != null && this.game.getRoomID() != r.getID()) {
					this.game.setRoom(r.clone());
				}
			}
			
			painter.setPrimitive(Primitive.QUADS);
			painter.setTexture(null);
			painter.setColor(new Color4f(0, 0, 0));
			
			GLOrtho o = GuiWindow.getInstance().getOrtho();
			GLUtil.drawQuad2D(o.getBounds2D(), painter);
			
			Color4f textColor = GuiConstants.TEXT_COLOR_DEFAULT.clone();
			int prop = (int) Math.abs(time - TIME_1_STAGE);
			//calcul du alpha : au début et à la fin, augmentation ou diminution progressive pendant TIME_TEXT_FONDUE ms
			double alpha = (
					MathUtil.valueInRange_d(
							Math.abs(prop - (TIME_BETWEEN_1_2) / 2d),
							TIME_BETWEEN_1_2 / 2d - TIME_TEXT_FONDUE,
							TIME_BETWEEN_1_2 / 2d)
					- (TIME_BETWEEN_1_2 / 2d - TIME_TEXT_FONDUE)
					) / (double) TIME_TEXT_FONDUE;
			textColor.setAlphaf(Math.abs((float) alpha - 1));
			textPainter.setColor(textColor);
			textPainter.drawCenteredString2D(this.game.getRoomName(this.roomToGoID), 0, 0, 32);
		}
		if (time >= TIME_2_STAGE && time < TIME_3_STAGE) {
			drawSideAnimation(1d - (double) (time - TIME_2_STAGE) / (double) (TIME_3_STAGE - TIME_2_STAGE), painter);
		}
	}
	
	/** dessine l'animation entre l'affichage du nom de la salle et le
	 * début / la fin (Ici c'est une transition de type "fondue"). L'argument
	 * sert à indiquer la proportion de temps écoulé ou restant à écouler
	 * (Animation "miroir") */
	private void drawSideAnimation(double prop, Painter p) {
		p.setPrimitive(Primitive.QUADS);
		p.setTexture(null);
		p.setColor(new Color4f(0, 0, 0, (float) prop));
		
		GLOrtho o = GuiWindow.getInstance().getOrtho();
		GLUtil.drawQuad2D(o.getBounds2D(), p);
	}
}
