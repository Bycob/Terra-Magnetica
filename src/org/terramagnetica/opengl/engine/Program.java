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
import java.util.HashMap;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class Program {
	
	private class Uniform {
		public String name;
		public int id;
		public Object value;
		
		public Uniform(String name, int id, Object value) {
			this.name = name;
			this.id = id;
			this.value = value;
		}
	}
	

	
	private int programID;
	private HashMap<String, Uniform> uniforms = new HashMap<String, Uniform>();
	
	public Program(Shader... shaders) throws ProgramLinkingException {
		if (shaders.length == 0) throw new NullPointerException("no shaders to create the program.");
		this.programID = GL20.glCreateProgram();
		
		if (this.programID == 0) {
			throw new RuntimeException("Impossible de créer le programme. Pourquoi ? Je ne sais pas...");
		}
		
		for (Shader shader : shaders) {
			if (shader.getShaderID() == 0) {
				throw new IllegalArgumentException("Program linking : some shaders do not exist.");
			}
			
			GL20.glAttachShader(this.programID, shader.getShaderID());
		}
		
		GL20.glLinkProgram(this.programID);
		
		int status = GL20.glGetProgrami(this.programID, GL20.GL_LINK_STATUS);
		
		if (status == GL11.GL_FALSE) {
			String infoLog = GL20.glGetProgramInfoLog(this.programID, 1024);
			throw new ProgramLinkingException(infoLog);
		}
	}
	
	public void use() {
		GL20.glUseProgram(this.programID);
	}
	
	public static void useNoProgram() {
		GL20.glUseProgram(0);
	}
	
	public boolean isInUse() {
		return GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) == this.programID;
	}
	
	private void checkInUse() {
		if (!isInUse()) throw new IllegalStateException("Impossible de définir un uniform sur un programme qui n'est pas en cours !");
	}
	
	public int attribID(String attribName) {
		return GL20.glGetAttribLocation(this.programID, attribName);
	}
	
	private Uniform uniform(String uniformName) {
		Uniform uniform = this.uniforms.get(uniformName);
		
		if (uniform == null) {
			int id = GL20.glGetUniformLocation(this.programID, uniformName);
			uniform = new Uniform(uniformName, id, null);
			
		}
		return uniform;
	}
	
	/** Définit la valeur de la variable uniforme. Renvoie <tt>true</tt> si cette
	 * valeur a été modifiée, <tt>false</tt> si elle était identique.*/
	private boolean setIfDifferent(Uniform uniform, Object value) {
		if (!Util.equals(uniform.value, value)) {
			uniform.value = value;
			return true;
		}
		return false;
	}
	
	public int uniformID(String uniformName) {
		return uniform(uniformName).id;
	}
	
	public void setUniform1i(String uniformName, int value) {
		checkInUse();
		Uniform uniform = uniform(uniformName);
		
		if (setIfDifferent(uniform, value)) {
			GL20.glUniform1i(uniform.id, value);
		}
	}
	
	public void setUniform1f(String uniformName, float value) {
		checkInUse();
		Uniform uniform = uniform(uniformName);
		
		if (setIfDifferent(uniform, value)) {
			GL20.glUniform1f(uniform.id, value);
		}
	}
	
	public void setUniform3f(String uniformName, float value1, float value2, float value3) {
		checkInUse();
		Uniform uniform = uniform(uniformName);
		
		if (setIfDifferent(uniform, new Vec3d(value1, value2, value3))) {
			GL20.glUniform3f(uniform.id, value1, value2, value3);
		}
	}
	
	public void setUniformVec3d(String uniformName, Vec3d vec) {
		checkInUse();
		Uniform uniform = uniform(uniformName);
		
		if (setIfDifferent(uniform, vec)) {
			GL20.glUniform3f(uniform.id, (float) vec.x, (float) vec.y, (float) vec.z);
		}
	}
	
	public void setUniformMatrix4f(String uniformName, Matrix4f matrix) {
		checkInUse();
		Uniform uniform = uniform(uniformName);
		
		if (setIfDifferent(uniform, matrix)) {
			FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
			matrix.get(matrixBuf);
			GL20.glUniformMatrix4fv(uniform.id, false, matrixBuf);
		}
	}
}
