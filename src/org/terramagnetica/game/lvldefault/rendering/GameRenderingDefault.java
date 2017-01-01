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

import org.lwjgl.opengl.GL11;
import org.terramagnetica.game.TerraMagneticaGL;
import org.terramagnetica.game.gui.GameRendering;
import org.terramagnetica.game.lvldefault.GameBufferDefault;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.MapUpdater;
import org.terramagnetica.opengl.engine.Camera3D;
import org.terramagnetica.opengl.engine.Camera3DFlying;
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.Light;
import org.terramagnetica.opengl.engine.Light.LightColor;
import org.terramagnetica.opengl.engine.Light.LightType;
import org.terramagnetica.opengl.engine.LightModel;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Program;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.miscellaneous.GlobalAnimationManager;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class GameRenderingDefault extends GameRendering implements TerraMagneticaGL {
	
	public static final float DEFAULT_FOV = 90;
	
	private GamePlayingDefault game;
	private GameBufferDefault gameBuf;
	private GlobalAnimationManager animationManager = new GlobalAnimationManager();
	
	private GLConfiguration sharedConfiguration3D = GLConfiguration.default3DConfiguration();
	private Camera3DFlying camera;
	
	private GuiFrameContainer gameInfosCont = new GuiFrameContainer();
	private PortalNameDisplayer dispPortalElement = new PortalNameDisplayer("");
	
	private List<RenderGameDefaultElement> renderingList = new ArrayList<RenderGameDefaultElement>();
	
	// Variable d'états
	private boolean isScrolling = false;
	
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
	protected void drawComponent(Painter painter) {
		GamePlayingDefault toDraw = null;
		
		if (!modeAuto) {
			toDraw = game;
		}
		else {
			toDraw = gameBuf.read();
		}
		
		render(toDraw, painter);
	}
	
	public void render(GamePlayingDefault toDraw, Painter painter) {
		
		if (toDraw != null) {
			painter.flush();
			Program program = painter.getCurrentProgram();
			
			//placement de la caméra
			if (this.trackPoint == null && toDraw.getPlayer() != null) {
				this.trackPoint = toDraw.getPlayer().getCameraTrackPoint();
			}
			
			this.camera.moveCenterPoint(this.trackPoint.getX(), - this.trackPoint.getY(), this.trackPoint.getZ());
			this.camera.setFOV(DEFAULT_FOV);
			
			// Lumière
			LightModel lightModel = painter.getLightModel();
			Light light0 = lightModel.getLight0();
			
			if (toDraw.hasLimitedVision()) {
				//TODO en fonction de la couleur du niveau
				light0.setLightColor(LightColor.AMBIENT, new Color4f(0.2f, 0.2f, 0.2f));
				light0.setLightColor(LightColor.DIFFUSE, new Color4f(1f, 1f, 1f));
				light0.setAttenuation(new Vec3d(1, 0.3, 0.05));
				
				light0.setPosition(new Vec3d(this.trackPoint.getX(), -this.trackPoint.getY(), 0.5));
				light0.setType(LightType.POINT);
				program.setUniform1f(VISION_SIZE, MapUpdater.MAX_DISTANCE_LIMITED);
			}
			else {
				light0.setLightColor(LightColor.AMBIENT, new Color4f(0.5f, 0.5f, 0.5f));
				light0.setLightColor(LightColor.DIFFUSE, new Color4f(0.8f, 0.8f, 0.8f));
				
				light0.setPosition(new Vec3d(0, 0, 1));
				light0.setType(LightType.DIRECTIONNAL);
				program.setUniform1f(VISION_SIZE, -1);
			}
			
			// Définition des uniforms propres à Terra Magnetica
			program.setUniform3f(PLAYER_POS, (float) this.trackPoint.getX(), (float) (- this.trackPoint.getY()), (float) this.trackPoint.getZ());
			program.setUniform3f(LEVEL_COLOR, 1, 1, 1);
			
			// Rendu de chaque unité
			for (RenderGameDefaultElement render : this.renderingList) {
				program.setUniform1i(IN_GAME_FLAG, render.isInGame() ? GL11.GL_TRUE : GL11.GL_FALSE);
				render.render(toDraw, painter);
			}

			program.setUniform1i(IN_GAME_FLAG, GL11.GL_FALSE);
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
	
	public void setScrolling(boolean scrolling) {
		this.isScrolling = scrolling;
	}

	public boolean isScrolling() {
		return this.isScrolling;
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
