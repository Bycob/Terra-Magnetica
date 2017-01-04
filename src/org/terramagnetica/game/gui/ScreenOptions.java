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

import org.terramagnetica.game.Options;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiContainer;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.ressources.ExternalFilesManager;

import net.bynaryscode.util.Color4f;

public class ScreenOptions extends GameScreen {
	
	private Options options = TerraMagnetica.theGame != null ? TerraMagnetica.theGame.options : new Options();
	
	private GuiContainer optPanel;
	private OptionPanelFactory optionFactory;
	private GuiFrameContainer container;
	
	private GuiButtonText1 retour;
	
	public ScreenOptions() {
		this.container = new GuiFrameContainer();
		GuiBorderLayout bLayout = new GuiBorderLayout();
		this.container.setLayout(bLayout);
		
		GuiContainer panTitle = new GuiContainer(), panButton = new GuiContainer();
		panTitle.setBackground(new Color4f(0, 0, 0, 32));
		panButton.setBackground(new Color4f(0, 0, 0, 32));
		
		this.container.add(panTitle, GuiBorderLayout.TOP);
		this.container.add(panButton, GuiBorderLayout.BOTTOM);
		
		this.retour = new GuiButtonText1(0.1, 0.1, 1.1, -0.1, "Menu");
		GuiLabel lblTitle = new GuiLabel("Options", 20, 0, 0);
		lblTitle.setColor(TEXT_COLOR_DEFAULT);
		panButton.add(this.retour);
		panTitle.add(lblTitle);
		
		setOptionPanel(new OptionMainPanel());
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.setSpeed(SPEED_DEFAULT);
		tt.add(this.retour, GuiDefaultTransition.DROITE);
		this.setTransition(tt);
		
		this.add(this.container);
	}
	
	private void setOptionPanel(OptionPanelFactory o) {
		if (this.optPanel != null) this.container.remove(this.optPanel);
		
		this.optionFactory = o;
		this.optPanel = this.optionFactory.getOptPanel();
		this.container.add(this.optPanel, GuiBorderLayout.CENTER);
	}
	
	protected void onClose() {
		StringBuilder sb = new StringBuilder(1024);
		this.options.writeOptions(sb);
		ExternalFilesManager.saveOptions(sb.toString().replace("\n", "\r\n"));
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}

	@Override
	public int timeToDestroy() {
		return MILLIS_DESTROY_DEFAULT;
	}
	
	@Override
	public void drawComponent(Painter painter) {
		this.drawDefaultBackground(painter);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (this.getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (this.optionFactory != null) {
			GuiActionEvent ev = this.optionFactory.processLogic();
			if (ev != GuiActionEvent.NULL_EVENT) return ev;
		}
		
		if (this.retour.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenInGameMenu());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	protected abstract class OptionPanelFactory {
		
		protected GuiContainer panel = new GuiContainer();
		protected GuiButtonText1 backButton;
		
		protected OptionPanelFactory() {
			this.backButton = new GuiButtonText1(0.1, 0.1, 1, -0.1, "Options générales");
		}
		
		public GuiContainer getOptPanel() {
			return this.panel;
		}
		
		public GuiActionEvent processLogic() {
			if (this.backButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
				setOptionPanel(new OptionMainPanel());
			}
			
			return GuiActionEvent.NULL_EVENT;
		}
	}
	
	protected class OptionMainPanel extends OptionPanelFactory {
		
		private GuiButtonText1 controls;
		private GuiButtonText1 graphics;
		
		public OptionMainPanel() {
			this.controls = new GuiButtonText1(-1.1, 0.6, -0.1, 0.4, "Contrôles");
			this.graphics = new GuiButtonText1(-1.1, 0.2, -0.1, 0, "Options graphiques");
			
			this.panel.add(this.controls);
			this.panel.add(this.graphics);
		}
		
		@Override
		public GuiActionEvent processLogic() {
			if (this.controls.processLogic() == GuiActionEvent.CLICK_EVENT) {
				setOptionPanel(new OptionControlsPanel());
			}
			if (this.graphics.processLogic() == GuiActionEvent.CLICK_EVENT) {
				setOptionPanel(new OptionGraphicsPanel());
			}
			
			return GuiActionEvent.NULL_EVENT;
		}
	}
	
	protected class OptionControlsPanel extends OptionPanelFactory {
		
		private GuiControlsComponent controlsSetter;
		
		public OptionControlsPanel() {
			this.controlsSetter = new GuiControlsComponent(TerraMagnetica.theGame != null ? TerraMagnetica.theGame.options : new Options());
			GuiContainer c1 = new GuiContainer();
			c1.add(this.backButton);
			
			this.panel.setLayout(new GuiBorderLayout());
			this.panel.add(c1, GuiBorderLayout.BOTTOM);
			this.panel.add(this.controlsSetter, GuiBorderLayout.CENTER);
		}
		
		@Override
		public GuiActionEvent processLogic() {
			super.processLogic();
			
			return GuiActionEvent.NULL_EVENT;
		}
	}
	
	protected class OptionGraphicsPanel extends OptionPanelFactory {
		
		public OptionGraphicsPanel() {
			this.panel.add(this.backButton);
		}
	}
}
