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

package org.terramagnetica.game.gui;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public final class ScreenMainMenu extends GameScreen {
	
	private static final double BUTTON_WIDTH = 1.5;
	private static final double BUTTON_HEIGHT = 0.2;
	
	private final double posNewPart = 0.2;
	private final double posLoadPart = -0.4;
	
	/** "Nouvelle partie"  */
	private GuiButtonText1 newPart = new GuiButtonText1("Nouvelle partie");
	/** "Charger une partie" */
	private GuiButtonText1 loadPart = new GuiButtonText1("Charger une partie");
	
	private static final int NORMAL_APPEARING = 0;
	private static final int FIRST_APPEARING = 1;
	
	private static int appearSelector = FIRST_APPEARING;
	
	public ScreenMainMenu() {
		setLocationButtonDefault();
		
		this.add(newPart);
		this.add(loadPart);
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.setSpeed(SPEED_DEFAULT);
		
		tt.add(newPart, GuiDefaultTransition.HAUT);
		tt.add(loadPart, GuiDefaultTransition.BAS);
		
		this.setTransition(tt);
	}
	
	@Override
	public int timeToAppear() {
		switch (appearSelector) {
		case NORMAL_APPEARING :
			return MILLIS_APPEAR_DEFAULT;
		case FIRST_APPEARING :
			return 1000;
		default :
			return MILLIS_APPEAR_DEFAULT;
		}
	}
	
	@Override
	public int timeToDestroy(){return 280;}
	
	@Override
	protected void drawComponent(){
		if (appearSelector != NORMAL_APPEARING) appearSelector = NORMAL_APPEARING;
		
		drawDefaultBackground();
		drawTitleImage(Painter.instance, new Color4f(1f, 1f, 1f));
	}
	
	private void drawTitleImage(Painter p, Color4f color) {
		TextureQuad titleScreen = TexturesLoader.getQuad(GameRessources.PATH_TITLE_SCREEN);
		
		double scale = this.theTextPainter.getRealScale() * 1d / 3d;
		
		double width = theWindow.getWidthOnGLOrtho(titleScreen.getWidth()) * scale;
		double height = theWindow.getHeightOnGLOrtho(titleScreen.getHeight()) * scale;
		
		final double hOffset = 0.2 * scale;
		
		p.setColor(color);
		p.setTexture(titleScreen);
		GLUtil.drawQuad2D(- width / 2, height / 2 + hOffset, width / 2, - height / 2 + hOffset, p);
	}
	
	@Override
	protected void drawComponentAppearing(){
		switch (appearSelector) {
		case FIRST_APPEARING :
			
			TextureQuad tex1 = TexturesLoader.getQuad(String.format(GameRessources.SPEC_PATH_TERRAIN, 1) + GameRessources.TEX_SOL);
			float colorUnit = (float)((double)getAppearCount() / timeToAppear() * 0.5);
			Color4f color = new Color4f(colorUnit, colorUnit, colorUnit);
			Color4f buttonColor = new Color4f(colorUnit * 2, colorUnit * 2, colorUnit * 2);
			
			Painter.instance.setColor(color);
			tex1.fillScreen2D(0.5, 0.5, true);
			
			drawTitleImage(Painter.instance, buttonColor);
			
			newPart.setColor(buttonColor);
			loadPart.setColor(buttonColor);
			break;
		
		default :
			super.drawComponentAppearing();
		}
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (getState() != VISIBLE){
			return super.processLogic();
		}
		
		if (newPart.processLogic() == GuiActionEvent.CLICK_EVENT){
			nextPanel(new ScreenNewGame());
		}
		if (loadPart.processLogic() == GuiActionEvent.CLICK_EVENT) {
			nextPanel(new ScreenLoadMenu());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	@Override
	public ScreenMainMenu clone() {
		ScreenMainMenu result = (ScreenMainMenu) super.clone();
		result.newPart = this.newPart.clone();
		result.loadPart = this.loadPart.clone();
		return result;
	}
	
	private final void setLocationButtonDefault() {
		newPart.setBoundsGL(new RectangleDouble(- (BUTTON_WIDTH / 2), posNewPart + (BUTTON_HEIGHT / 2),
				(BUTTON_WIDTH / 2), posNewPart - (BUTTON_HEIGHT / 2)));
		loadPart.setBoundsGL(new RectangleDouble(- (BUTTON_WIDTH / 2), posLoadPart + (BUTTON_HEIGHT / 2),
				(BUTTON_WIDTH / 2), posLoadPart - (BUTTON_HEIGHT / 2)));
	}
}
