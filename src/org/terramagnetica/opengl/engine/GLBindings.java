package org.terramagnetica.opengl.engine;

import java.util.HashMap;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class GLBindings {
	Painter painter;
	
	private int vertexArrayBinding = 0;
	private HashMap<Integer, Integer> bufferBindings = new HashMap<Integer, Integer>();
	
	private int textureBinding = 0;
	
	GLBindings(Painter painter) {
		this.painter = painter;
		
		this.bufferBindings.put(GL15.GL_ARRAY_BUFFER, 0);
		this.bufferBindings.put(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void bindBuffer(int target, int buffer) {
		Integer current = this.bufferBindings.get(target);
		
		if (current == null) throw new IllegalArgumentException("bindBuffer : wrong target type");
		
		if (buffer != current) {
			this.bufferBindings.put(target, buffer);
			GL15.glBindBuffer(target, buffer);
		}
	}
	
	public void bindVertexArray(int array) {
		if (array != this.vertexArrayBinding) {
			this.vertexArrayBinding = array;
			GL30.glBindVertexArray(array);
		}
	}
}
