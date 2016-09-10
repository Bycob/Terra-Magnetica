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

package org.terramagnetica.game.lvldefault.rendering;

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.game.gui.GameRendering;
import org.terramagnetica.game.lvldefault.GameBufferDefault;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.opengl.engine.Camera3D;
import org.terramagnetica.opengl.engine.Camera3DFlying;
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.miscellaneous.GlobalAnimationManager;

public class GameRenderingDefault extends GameRendering {
	
	public static final float DEFAULT_FOV = 90;
	
	public final Painter painter = Painter.instance;
	
	private GamePlayingDefault game;
	private GameBufferDefault gameBuf;
	private GlobalAnimationManager animationManager = new GlobalAnimationManager();
	
	private GLConfiguration sharedConfiguration3D = GLConfiguration.default3DConfiguration();
	private Camera3DFlying camera;
	
	private GuiFrameContainer gameInfosCont = new GuiFrameContainer();
	private PortalNameDisplayer dispPortalElement = new PortalNameDisplayer("");
	
	private List<RenderGameDefaultElement> renderingList = new ArrayList<RenderGameDefaultElement>();
	
	public GameRenderingDefault(GamePlayingDefault game) {
		this.camera = new Camera3DFlying(0, -1.8, 2.5,
				0, 0, 0,
				Camera3D.Verticale.Z_AXIS);
		
		this.game = game;
		this.gameBuf = game.getBuffer();
		
		this.gameInfosCont.setLayout(new GuiBorderLayout(0.25));
		this.gameInfosCont.add(this.dispPortalElement, GuiBorderLayout.BOTTOM);
		
		this.add(this.gameInfosCont);
		
		//indexation des animations.
		this.animationManager.add(this.dispPortalElement);
		
		this.animationManager.start();
		
		//rendu
		addRendering(new RenderFullLandscape());
		addRendering(new RenderEntities());
		
		addRendering(new RenderElementAlarm());
		addRendering(new RenderElementDirectionCircle());
		addRendering(new RenderElementInventory());
		addRendering(new RenderElementDialog());
		
		//Configuration 3D
		this.sharedConfiguration3D.setCamera(this.camera);
	}
	
	@Override
	protected void drawComponent() {
		GamePlayingDefault toDraw = null;
		
		if (!modeAuto) {
			toDraw = game;
		}
		else {
			toDraw = gameBuf.read();
		}
		
		render(toDraw);
	}
	
	public void render(GamePlayingDefault toDraw) {
		
		if (toDraw != null) {
			this.painter.flush();
			
			//placement de la caméra
			if (this.trackPoint == null && toDraw.getPlayer() != null) {
				this.trackPoint = toDraw.getPlayer().getCameraTrackPoint();
			}
			
			this.camera.moveCenterPoint(this.trackPoint.getX(), - this.trackPoint.getY(), this.trackPoint.getZ());
			this.camera.setFOV(DEFAULT_FOV);
			
			for (RenderGameDefaultElement render : this.renderingList) {
				render.render(toDraw, this.painter);
			}
		}
	}
	
	public void addRendering(RenderGameDefaultElement render) {
		if (render == null) return;
		render.sharedConfiguration = this.sharedConfiguration3D;
		this.renderingList.add(render);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends RenderGameDefaultElement> T getRendering(Class<T> clazz) {
		for (RenderGameDefaultElement renderElement : this.renderingList) {
			if (renderElement.getClass() == clazz) {
				return (T) renderElement;
			}
		}
		
		return null;
	}
	
	public void onChangingRoom() {
		this.trackPoint = null;
	}
	
	public void updateAnimations() {
		
	}
	
	@Override
	public void pause() {
		super.pause();
		this.animationManager.stop();
	}

	@Override
	public void resume() {
		super.resume();
		this.animationManager.start();
	}
	
	
	
	//------------------------ CAMERA ---------------------
	
	
	
	private CameraTrackPoint3D trackPoint;
	
	public void setCameraLooks(CameraTrackPoint3D trackPoint) {
		this.trackPoint = trackPoint;
	}
	
	public CameraTrackPoint3D getCameraTrackPoint() {
		return this.trackPoint;
	}
	
	
	
	public void displayPortalName(String name) {
		this.dispPortalElement.setPortalName(name);
	}
}
