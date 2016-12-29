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

package org.terramagnetica.opengl.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GLConfiguration implements Cloneable {
	
	public static GLConfiguration default2DConfiguration() {
		GLConfiguration config = new GLConfiguration();
		
		config.setPropertieEnabled(GLProperty.DEPTH_TEST, false);
		
		return config;
	}
	
	public static GLConfiguration default3DConfiguration() {
		GLConfiguration config = new GLConfiguration();
		
		config.setPropertieEnabled(GLProperty.STENCIL_TEST, false);
		
		config.camera = new Camera3D();
		
		return config;
	}
	
	
	public static enum GLProperty {
		DEPTH_TEST(GL11.GL_DEPTH_TEST, true),
		STENCIL_TEST(GL11.GL_STENCIL_TEST, true),
		BLEND(GL11.GL_BLEND, true),
		MULTISAMPLE(GL13.GL_MULTISAMPLE, true),
		LIGHTING(-1, false),
		COLOR(-1, true);
		
		public final int glConst;
		public final boolean defaultValue;
		
		GLProperty(int glConst, boolean defaultValue) {
			this.glConst = glConst;
			this.defaultValue = defaultValue;
		}
	}
	
	Painter painter;
	
	private HashMap<GLProperty, Boolean> properties = new HashMap<GLProperty, Boolean>();
	private Camera camera = new Camera2D();
	
	private int blendSFactor = GL11.GL_SRC_ALPHA;
	private int blendDFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;
	private int depthFunc = GL11.GL_LESS;
	
	public GLConfiguration() {
		for (GLProperty prop : GLProperty.values()) {
			this.properties.put(prop, prop.defaultValue);
		}
	}
	
	public void setPropertieEnabled(GLProperty property, boolean enabled) {
		if (this.properties.get(property) == enabled) return;
		notifyBeforeChanges();
		this.properties.put(property, enabled);
		
		glUpdateState(property, enabled);
	}
	
	private void glUpdateState(GLProperty prop, boolean activated) {
		if (this.painter != null) {
			Program currentProgram = this.painter.getCurrentProgram();
			
			//Propriétés openGL gérées par glEnable
			if (prop.glConst != -1) {
				if (activated && !GL11.glIsEnabled(prop.glConst)) {
					GL11.glEnable(prop.glConst);
				}
				else if (!activated && GL11.glIsEnabled(prop.glConst)) {
					GL11.glDisable(prop.glConst);
				}
			}
			//Toutes les autres
			else {
				switch (prop) {
				case LIGHTING :
					if (activated) {
						currentProgram.setUniform1i(StdUniform.USE_LIGHTS, GL11.GL_TRUE);
						this.painter.getLightModel().sendLightsToGL();
					}
					else {
						currentProgram.setUniform1i(StdUniform.USE_LIGHTS, GL11.GL_FALSE);
					}
					break;
				case COLOR :
					if (activated) {
						currentProgram.setUniform1i(StdUniform.USE_COLOR, GL11.GL_TRUE);
					}
					else {
						currentProgram.setUniform1i(StdUniform.USE_COLOR, GL11.GL_FALSE);
					}
				default:;
				}
			}
		}
	}
	
	public boolean isPropertyEnabled(GLProperty property) {
		return this.properties.get(property);
	}
	
	public void setCamera(Camera camera) {
		if (camera == null) throw new NullPointerException("camera == null");
		
		notifyBeforeChanges();
		this.camera = camera;
		
		if (this.painter != null) {
			camera.pushCamera(this.painter);
		}
	}
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public Camera3D getCamera3D() {
		return this.camera instanceof Camera3D ? (Camera3D) this.camera : null;
	}
	
	public Camera2D getCamera2D() {
		return this.camera instanceof Camera2D ? (Camera2D) this.camera : null;
	}
	
	public boolean isCamera3D() {
		return this.camera instanceof Camera3D;
	}
	
	public void setup() {
		if (this.painter == null) return;
		
		//Activation ou desactivation des différentes propriétés.
		for (GLProperty prop : GLProperty.values()) {
			boolean activated = this.properties.get(prop);
			glUpdateState(prop, activated);
		}
		
		//Fonctions de test
		if (this.properties.get(GLProperty.BLEND)) {
			GL11.glBlendFunc(blendSFactor, blendDFactor);
		}
		if (this.properties.get(GLProperty.DEPTH_TEST)) {
			GL11.glDepthFunc(depthFunc);
		}
		
		//Caméra
		this.camera.pushCamera(this.painter);
	}
	
	public void clearConfig() {
		if (this.properties.get(GLProperty.STENCIL_TEST)) {
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		}
	}
	
	private void notifyBeforeChanges() {
		if (this.painter != null) {
			this.painter.notifyExternalChanges();
		}
	}
	
	@Override
	public GLConfiguration clone() {
		GLConfiguration clone = null;
		try {
			clone = (GLConfiguration) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.properties = new HashMap<GLConfiguration.GLProperty, Boolean>();
		for (Entry<GLProperty, Boolean> e : this.properties.entrySet()) {
			clone.properties.put(e.getKey(), e.getValue());
		}
		
		return clone;
	}
}
