package org.terramagnetica.opengl.engine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VBO {
	
	private int target;
	private int id;
	
	private int usage = GL15.GL_STATIC_DRAW;
	
	public VBO() {
		this(GL15.GL_ARRAY_BUFFER);
	}
	
	public VBO(int target) {
		if (target != GL15.GL_ARRAY_BUFFER && target != GL15.GL_ELEMENT_ARRAY_BUFFER) {
			throw new IllegalArgumentException("Invalid target");
		}
		
		this.target = target;
		this.id = GL15.glGenBuffers();
	}
	
	@Override
	protected void finalize() {
		GL15.glDeleteBuffers(this.id);
	}
	
	public void bind() {
		if (!isBound()) GL15.glBindBuffer(this.target, this.id);
	}
	
	public static void unbindArrayBuffer() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public boolean isBound() {
		int target = this.target == GL15.GL_ARRAY_BUFFER ? GL15.GL_ARRAY_BUFFER_BINDING : GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING;
		return GL11.glGetInteger(target) == this.id;
	}
	
	public int getTarget() {
		return this.target;
	}
	
	public void setData(ByteBuffer data) {
		bind();
		GL15.glBufferData(this.target, data, this.usage);
	}
	
	public void setData(FloatBuffer data) {
		bind();
		GL15.glBufferData(this.target, data, this.usage);
	}
	
	public void setData(ShortBuffer data) {
		bind();
		GL15.glBufferData(this.target, data, this.usage);
	}
	
	public void setData(IntBuffer data) {
		bind();
		GL15.glBufferData(this.target, data, this.usage);
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
