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

package debug;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.lvl2.ControlPane;
import org.terramagnetica.game.physic.Force;
import org.terramagnetica.game.physic.Hitbox;
import org.terramagnetica.game.physic.HitboxCircle;
import org.terramagnetica.openal.MusicStreaming;
import org.terramagnetica.openal.ThreadMusicStreaming;
import org.terramagnetica.opengl.engine.Camera3D;
import org.terramagnetica.opengl.engine.Model3D;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.ModelLoader;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class CodeTest {
	
	public static Model3D model;
	public static Camera3D camera = new Camera3D(
			3, -3, 3,
			0, 0, 0,
			0, 0, 1);
	
	public static Entity entity = new ControlPane(90f, new Color4f(1f, 0, 0));
	
	public static void main(String[] args) {
		testHitbox();
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
	
	public static void appliGL() {
		GuiWindow window = GuiWindow.getInstance();
		try {
			window.createWindow();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GuiTextPainter.init();
		window.setContentPane(new DrawPanel());
		
		ArrayList<String> set = new ArrayList<String>(); set.add(GameRessources.PATH_MODEL_LVL2_CONTROLPANE);
		ModelLoader.loadModelSet(set, true);
		
		TexturesLoader.loadTextureSet(GameRessources.gameTextureSet);
		
		while (!window.isCloseRequested()) {
			window.update();
			Display.sync(60);
		}
	}
	
	public static void toDo() {
		Painter.instance.addTransform(Transform.newRotation((System.currentTimeMillis() % 3000) / 3000f * 360f, new Vec3d(0, 0, 1)));
		entity.getRender().renderEntity3D(0, 0, Painter.instance);
		
		//DU COUP : Regarde le obj de la jungle ! ça vaut le coup !
	}
	
	public static class DrawPanel extends GuiFrameContainer {
		
		@Override
		public void drawComponent() {
			Painter painter = Painter.instance;
			painter.ensure3D();
			painter.getConfiguration().setCamera(camera);
			
			toDo();
		}
	}
}
