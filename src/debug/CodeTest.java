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

package debug;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.lvl2.ControlPane;
import org.terramagnetica.openal.MusicStreaming;
import org.terramagnetica.openal.ThreadMusicStreaming;
import org.terramagnetica.opengl.engine.Camera3D;
import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;
import org.terramagnetica.opengl.engine.Light;
import org.terramagnetica.opengl.engine.LightModel;
import org.terramagnetica.opengl.engine.Model3D;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.physics.Force;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ModelLoader;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class CodeTest {
	
	public static Model3D model;
	public static Camera3D camera = new Camera3D(
			0, -1, 1,
			0, 0, 0,
			0, 0, 1);
	
	public static Entity entity = new ControlPane(90f, new Color4f(1f, 0, 0));
	
	public static void main(String[] args) {
		appliGL();
	}
	
	private static void testHitbox() {
		Hitbox hb1 = new HitboxCircle(0.5f);
		Hitbox hb2 = new HitboxCircle(0.2f);
		
		hb1.setPosition(0, 0);
		hb2.setPosition(1, 0);
		
		hb1.setLinearSpeed(5, 4);
		hb1.addForce(new Force(2, 1));
		hb1.applyVelocity(5);
		
		System.out.println(hb1.getPosition());
	}
	
	private static void testMusique() {
		MusicStreaming mus = SoundManager.loadMusic("level1.ogg");
		ThreadMusicStreaming t = new ThreadMusicStreaming();
		t.setMusic(mus);
		t.startMusic();
	}
	
	//----------- Pour les tests openGL ----------------------------------
	
	private static String modelName = "decor/terrain3/mur.obj";
	
	public static void appliGL() {
		GuiWindow window = GuiWindow.getInstance();
		try {
			window.createWindow();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GuiTextPainter.init();
		window.setContentPane(new DrawPanel());
		
		ArrayList<String> set = new ArrayList<String>(); set.add(modelName);
		ModelLoader.loadModelSet(set, true);
		
		TexturesLoader.loadTextureSet(GameRessources.gameTextureSet);
		
		while (!window.isCloseRequested()) {
			window.update();
			Display.sync(60);
		}
	}
	
	private static float rotation = 0f;
	private static float zoom = 1f;
	
	public static void toDo() {
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			rotation += 1f;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			rotation -= 1f;
		}
		
		Painter.instance.addTransform(Transform.newRotation(rotation, new Vec3d(0, 0, 1)));
		
		LightModel lightModel = Painter.instance.getLightModel();
		Light l = lightModel.getLight0();
		l.setPosition(0, 0, 1);
		
		Model3D model = ModelLoader.getNotNull(modelName);
		model.draw(Painter.instance);
		
		Painter.instance.clearTransforms();
	}
	
	public static class DrawPanel extends GuiFrameContainer {
		
		@Override
		public void drawComponent() {
			Painter painter = Painter.instance;
			painter.ensure3D();
			
			zoom -= Mouse.getDWheel() / 1000f;
			camera.setEye(new Vec3d(0, -zoom, zoom));
			
			painter.getConfiguration().setCamera(camera);
			painter.getConfiguration().setPropertieEnabled(GLProperty.LIGHTING, true);
			
			toDo();
		}
	}
}
