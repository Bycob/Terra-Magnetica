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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class Light {
	
	public enum LightType {
		POINT,
		DIRECTIONNAL;
	}
	
	public enum LightColor {
		AMBIENT(GL11.GL_AMBIENT),
		DIFFUSE(GL11.GL_DIFFUSE),
		SPECULAR(GL11.GL_SPECULAR);
		
		public final int glID;
		
		LightColor(int glID) {
			this.glID = glID;
		}
	}
	
	int id;
	LightModel model;
	boolean activated;
	
	private Vec3d position = new Vec3d(0, 0, 1);
	private LightType type = LightType.DIRECTIONNAL;
	private Color4f colors[] = new Color4f[] {new Color4f(), new Color4f(), new Color4f()};
	private Vec3d attenuation = new Vec3d(1, 0.1, 0.001);
	
	private FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(4);
	private FloatBuffer colorBuffers[] = new FloatBuffer[3];
	
	{
		for (int i = 0 ; i < colorBuffers.length ; i++) {
			this.colorBuffers[i] = BufferUtils.createFloatBuffer(4);
		}
	}
	
	Light(int id, LightModel model) {
		this.id = id;
		this.model = model;
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
	}
	
	public void setLightColor(LightColor type, Color4f color) {
		if (color == null) throw new NullPointerException("color == null");
		
		notifyBeforeChanges();
		this.colors[type.ordinal()] = color.clone();
	}
	
	public Vec3d getAttenuation() {
		return attenuation.clone();
	}
	
	public void setAttenuation(Vec3d attenuation) {
		notifyBeforeChanges();
		this.attenuation = attenuation.clone();
	}
	
	public void setAttenuation(double constant, double linear, double quadratic) {
		setAttenuation(new Vec3d(constant, linear, quadratic));
	}
	
	void setActive(boolean active) {
		int glID = GL11.GL_LIGHT0 + id;
		
		if (active && ! this.activated) {
			GL11.glEnable(glID);
			
			//Position
			this.positionBuffer.clear();
			bufferPutVec(positionBuffer, this.position);
			positionBuffer.put(this.type == LightType.DIRECTIONNAL ? 0 : 1);
			positionBuffer.flip();
			
			GL11.glLight(glID, GL11.GL_POSITION, positionBuffer);
			
			//Couleurs
			for (LightColor colorType : LightColor.values()) {
				Color4f color = this.colors[colorType.ordinal()];
				FloatBuffer colors = this.colorBuffers[colorType.ordinal()];
				colors.clear();
				colors.put(new float[] {color.getRedf(), color.getGreenf(), color.getBluef(), color.getAlphaf()});
				colors.flip();
				
				GL11.glLight(glID, colorType.glID, colors);
			}
			
			//Atténuation
			GL11.glLightf(glID, GL11.GL_CONSTANT_ATTENUATION, (float) this.attenuation.x);
			GL11.glLightf(glID, GL11.GL_LINEAR_ATTENUATION, (float) this.attenuation.y);
			GL11.glLightf(glID, GL11.GL_QUADRATIC_ATTENUATION, (float) this.attenuation.z);
		}
		else if (!active && this.activated) {
			GL11.glDisable(glID);
		}
	}
	
	
	Painter painter;
	
	private void notifyBeforeChanges() {
		if (this.painter != null) {
			this.painter.notifyExternalChanges();
		}
	}
	
	private void bufferPutVec(FloatBuffer buffer, Vec3d vec) {
		buffer.put(new float[] {(float) vec.x, (float) vec.y, (float) vec.z});
	}
}
