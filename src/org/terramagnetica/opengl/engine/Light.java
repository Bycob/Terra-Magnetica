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

import static org.terramagnetica.opengl.engine.StdUniform.Light.*;

import org.lwjgl.opengl.GL11;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class Light {
	
	public enum LightType {
		POINT,
		DIRECTIONNAL;
	}
	
	public enum LightColor {
		AMBIENT,
		DIFFUSE,
		SPECULAR;
	}
	
	public static String getLightParam(int lightID, String param) {
		return param.replace("[0]", "[" + lightID + "]");
	}
	
	int id;
	LightModel model;
	boolean activated;
	
	private Vec3d position = new Vec3d(0, 0, 1);
	private LightType type = LightType.DIRECTIONNAL;
	private Color4f colors[] = new Color4f[] {new Color4f(), new Color4f(), new Color4f()};
	private Vec3d attenuation = new Vec3d(1, 0.1, 0.001);
	
	Light(int id, LightModel model) {
		this.id = id;
		this.model = model;
		this.painter = this.model.painter;
	}
	
	public int getID() {
		return this.id;
	}
	
	public LightModel getModel() {
		return this.model;
	}
	
	public Vec3d getPosition() {
		return position.clone();
	}
	
	public void setPosition(Vec3d position) {
		notifyBeforeChanges();
		this.position = position.clone();
		sendLightParamsToGL();
	}
	
	public void setPosition(double x, double y, double z) {
		setPosition(new Vec3d(x, y, z));
	}
	
	public LightType getType() {
		return type;
	}
	
	public void setType(LightType type) {
		if (type == null) throw new NullPointerException("type == null");
		
		notifyBeforeChanges();
		this.type = type;
		sendLightParamsToGL();
	}
	
	public void setLightColor(LightColor type, Color4f color) {
		if (color == null) throw new NullPointerException("color == null");
		
		notifyBeforeChanges();
		this.colors[type.ordinal()] = color.clone();
		sendLightParamsToGL();
	}
	
	public Vec3d getAttenuation() {
		return attenuation.clone();
	}
	
	public void setAttenuation(Vec3d attenuation) {
		notifyBeforeChanges();
		this.attenuation = attenuation.clone();
		sendLightParamsToGL();
	}
	
	public void setAttenuation(double constant, double linear, double quadratic) {
		setAttenuation(new Vec3d(constant, linear, quadratic));
	}
	
	void setActive(boolean active) {
		if (active ^ this.activated) {
			this.activated = active;
			sendLightParamsToGL();
		}
	}
	
	void sendLightParamsToGL() {
		
		if (this.painter == null) return;
		Program currentProgram = this.painter.getCurrentProgram();
		int glID = id;
		
		if (!this.activated) {
			currentProgram.setUniform1i(uniformID(ACTIVATED, glID), GL11.GL_FALSE);
		}
		else {
			currentProgram.setUniform1i(uniformID(ACTIVATED, glID), GL11.GL_TRUE);
			
			//Position
			currentProgram.setUniform1i(uniformID(TYPE, glID), this.type.ordinal());
			currentProgram.setUniformVec3d(uniformID(POSITION, glID), this.position);
			
			//Couleurs
			for (LightColor colorType : LightColor.values()) {
				Color4f color = this.colors[colorType.ordinal()];
				String colorID = new String[] {AMBIENT, DIFFUSE, SPECULAR}[colorType.ordinal()];
				currentProgram.setUniform3f(uniformID(colorID, glID), color.getRedf(), color.getGreenf(), color.getBluef());
			}
			
			//Atténuation
			currentProgram.setUniformVec3d(uniformID(ATTENUATION, glID), this.attenuation);
		}
	}
	
	
	Painter painter;
	
	private void notifyBeforeChanges() {
		if (this.painter != null) {
			this.painter.notifyExternalChanges();
		}
	}
	
	private String uniformID(String stdID, int lightID) {
		return stdID.replaceAll("%d", String.valueOf(lightID));
	}
}
