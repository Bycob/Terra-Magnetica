package org.terramagnetica.opengl.engine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VBO {
	
	private int id;
	
	private int usage = GL15.GL_STATIC_DRAW;
	
	public VBO() {
		this.id = GL15.glGenBuffers();
	}
	
	@Override
	protected void finalize() {
		GL15.glDeleteBuffers(this.id);
	}
	
	public void bind() {
		if (!isBound()) GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.id);
	}
	
	public boolean isBound() {
		return GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING) == this.id;
	}
	
	public void setData(ByteBuffer data) {
		bind();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, this.usage);
	}
	
	public void setData(FloatBuffer data) {
		bind();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, this.usage);
	}
	
	public void setDataUsage(int usage) {
		this.usage = usage;
	}
	
	public VBO withDataUsage(int usage) {
		setDataUsage(usage);
		return this;
	}
	
	public int getDataUsage() {
		return this.usage;
	}
}
