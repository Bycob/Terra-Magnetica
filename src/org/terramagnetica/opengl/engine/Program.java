package org.terramagnetica.opengl.engine;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.bynaryscode.util.maths.geometric.Vec3d;

public class Program {
	
	private int programID;
	
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
		System.out.println(attribName + " : " + GL20.glGetAttribLocation(this.programID, attribName));
		return GL20.glGetAttribLocation(this.programID, attribName);
	}
	
	public int uniformID(String uniformName) {
		return GL20.glGetUniformLocation(this.programID, uniformName);
	}
	
	public void setUniform1i(String uniformName, int value) {
		checkInUse();
		GL20.glUniform1i(uniformID(uniformName), value);
	}
	
	public void setUniform1f(String uniformName, float value) {
		checkInUse();
		GL20.glUniform1f(uniformID(uniformName), value);
	}
	
	public void setUniform3f(String uniformName, float value1, float value2, float value3) {
		checkInUse();
		GL20.glUniform3f(uniformID(uniformName), value1, value2, value3);
	}
	
	public void setUniformVec3d(String uniformName, Vec3d vec) {
		checkInUse();
		GL20.glUniform3f(uniformID(uniformName), (float) vec.x, (float) vec.y, (float) vec.z);
	}
	
	public void setUniformMatrix4f(String uniformName, Matrix4f matrix) {
		checkInUse();
		FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
		matrix.get(matrixBuf);
		GL20.glUniformMatrix4fv(uniformID(uniformName), false, matrixBuf);
	}
}
