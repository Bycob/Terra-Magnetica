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

package org.terramagnetica.game.lvldefault.rendering;

import static org.terramagnetica.game.GameRessources.*;

import org.terramagnetica.game.GameInputBuffer;
import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.game.gui.GuiConstants;
import org.terramagnetica.game.lvldefault.DialogInGame.Statement;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

/** Cette classe gère le rendu des textes de dialogues dans le jeu. */
public class RenderElementDialog extends RenderGameDefaultElement {
	
	private Statement currentStatement = null;
	private RenderStatement currentRender = null;
	
	/** Un pourcentage représentant la progression de l'animation d'apparition
	 * du texte, puis celle qui se déroule lorsque le texte est apparu. */
	private float animationCursor = 0;
	/** La vitesse d'animation en pourcent par secondes. Cette vitesse peut être
	 * négative : l'animation se rembobinera. */
	private float animationSpeed = 0;
	private long lastTick = 0;
	
	public RenderElementDialog() {
		
	}
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		long time = GameWindow.getSystemTime();
		
		//Initialisation du contexte openGL
		painter.ensure2D();
		
		//Rendu du texte
		if (this.currentRender != null) {
			//On incrémente le curseur d'animation. Celui-ci reste tout de même entre 0 et 1.
			this.animationCursor += this.animationSpeed * (time - this.lastTick) / 1000f;
			
			if (this.animationCursor > 1) this.animationCursor = 1;
			else if (this.animationCursor < 0) this.animationCursor = 0;
			
			//Rendu, analyse des entrées.
			this.currentRender.render(game, painter);
			this.currentRender.parseInput(game, game.getInput());
			
			if (this.currentRender.isOver()) {
				this.currentRender = createRenderStatement(this.currentStatement, this.currentRender);
				this.animationCursor = 0;
			}
		}
		
		this.lastTick = time;
	}
	
	public void setStatement(Statement statement) {
		this.currentStatement = statement;
		this.currentRender = createRenderStatement(statement, null);
	}
	
	/** Indique s'il est temps de passer à la réplique suivante. */
	public boolean isOver() {
		return this.currentRender == null;
	}
	
	/** Renvoie un {@link RenderStatement} adapté à la réplique passée en
	 * paramètres.
	 * @param from - Le RenderStatement qu'il y avait juste avant. */
	private RenderStatement createRenderStatement(Statement statement, RenderStatement from) {
		if (statement == null) return null;
		if (from != null) return from.next();
		
		if (statement.getClass() == Statement.class) {
			return new RenderDefaultStatementAppearing(statement);
		}
		
		return null;
	}
	
	
	
	/** Donne les dimensions et la position de l'espace de dialogue in game */
	private RectangleDouble getDialogBounds() {
		RectangleDouble result = GuiWindow.getInstance().getOrtho().getBounds2D();
		
		final double gap = GuiConstants.STANDART_GAP;
		
		result.xmin += gap;
		result.ymin = result.ymax + 25d / 100d * (result.ymin - result.ymax);
		result.xmax -= gap;
		result.ymax += gap;
		return result;
	}
	
	
	
	private abstract class RenderStatement {
		
		Statement statement;
		
		RenderStatement(Statement statement) {
			this.statement = statement;
		}
		
		abstract void render(GamePlayingDefault game, Painter painter);
		abstract void parseInput(GamePlayingDefault game, GameInputBuffer inputBuffer);
		
		/** Indique si  */
		boolean isOver() {
			return animationCursor == 1;
		}
		
		RenderStatement next() {
			return null;
		}
		
		
		//méthodes utilitaires d'affichage
		/** Affiche le cadre de texte. Sa position est déterminée avec la méthode
		 * {@link RenderElementDialog#getDialogBounds()}. */
		protected void renderBackground(Painter painter) {
			painter.setPrimitive(Primitive.QUADS);
			painter.setColor(new Color4f(255, 255, 255));
			
			RectangleDouble bounds = getDialogBounds();
			
			GLUtil.drawHorizontalTexturedRectangle(bounds,
					TexturesLoader.getQuad(PATH_DIALOG_BACKGROUND + TEX_DIALOG_BACKGROUND_LEFT),
					TexturesLoader.getQuad(PATH_DIALOG_BACKGROUND + TEX_DIALOG_BACKGROUND_CENTER),
					TexturesLoader.getQuad(PATH_DIALOG_BACKGROUND + TEX_DIALOG_BACKGROUND_RIGHT),
					painter);
		}
		
		/** Affiche le texte indiqué dans le cadre de texte, en partant du début.
		 * Le texte revient automatiquement à la ligne à la fin d'une ligne. */
		protected void renderText(String text, Painter painter) {
			GuiTextPainter textPainter = new GuiTextPainter(painter);
			textPainter.setColor(GuiConstants.TEXT_COLOR_DEFAULT);
			
			RectangleDouble bounds = getDialogBounds();

			final double gap = GuiConstants.STANDART_GAP;
			final int fontSize = 16;
			
			bounds.xmin += gap;
			bounds.xmax -= gap;
			bounds.ymin -= gap;
			
			textPainter.drawPlainText2D(text, bounds, fontSize);
		}
	}
	
	
	
	private class RenderDefaultStatementAppearing extends RenderStatement {
		
		RenderDefaultStatementAppearing(Statement statement) {
			super(statement);
		}
		
		@Override
		void render(GamePlayingDefault game, Painter painter) {
			renderBackground(painter);
			
			String textRendered = this.statement.getText();
			textRendered = textRendered.substring(0, (int) (animationCursor * textRendered.length()));
			renderText(textRendered, painter);
		}
		
		@Override
		void parseInput(GamePlayingDefault game, GameInputBuffer inputBuffer) {
			//L'utilisateur peut accélerer l'appartition du texte.
			
			float letterPerSecond = 75;
			float baseSpeed = letterPerSecond / currentStatement.getText().length();
			
			if (inputBuffer.isKeyPressed(InputKey.KEY_TALK)) {
				animationSpeed = baseSpeed * 2;
			}
			else {
				animationSpeed = baseSpeed;
			}
		}
		
		@Override
		RenderStatement next() {
			return new RenderDefaultStatementWaiting(this.statement);
		}
	}
	
	
	
	private class RenderDefaultStatementWaiting extends RenderStatement {
		boolean OK;
		
		RenderDefaultStatementWaiting(Statement statement) {
			super(statement);
		}
		
		@Override
		void render(GamePlayingDefault game, Painter painter) {
			renderBackground(painter);
			renderText(currentStatement.getText(), painter);
		}
		
		@Override
		void parseInput(GamePlayingDefault game, GameInputBuffer inputBuffer) {
			if (inputBuffer.isKeyPressed(InputKey.KEY_TALK)) {
				this.OK = true;
			}
		}
		
		@Override
		boolean isOver() {
			return this.OK;
		}
	}
}
