package org.terramagnetica.opengl.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VAO {

	class Attrib {
		public String name;
		public VBO buffer;
		public int count;
		public int type;
		
		public Attrib() {
			
		}
		
		public Attrib(String name, VBO buffer, int count, int type) {
			this.name = name; this.count = count; this.type = type;
		}
	}
	
	private int id;
	private boolean computed;
	private HashMap<String, Attrib> attribs = new HashMap<String, Attrib>();
	
	public VAO() {
		this.id = GL30.glGenVertexArrays();
	}
	
	@Override
	protected void finalize() {
		GL30.glDeleteVertexArrays(this.id);
	}
	
	public boolean isBound() {
		return GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING) == this.id;
	}
	
	public void bind() {
		if (!isBound()) GL30.glBindVertexArray(this.id);
	}
	
	public void unbind() {
		GL30.glBindVertexArray(0);
	}
	
	public void setAttrib(String name, VBO buffer, int count, int type) {
		if (buffer == null) throw new NullPointerException("buffer == null !");
		this.attribs.put(name, new Attrib(name, buffer, count, type));
	}
	
	public void bind(Program program) {
		bind();
		for (Entry<String, Attrib> entry : this.attribs.entrySet()) {
			Attrib attrib = entry.getValue();
			attrib.buffer.bind();
			
			int attribID = program.attribID(attrib.name);
			GL20.glEnableVertexAttribArray(this.id);
			GL20.glVertexAttribPointer(attribID, attrib.count, GL11.GL_FLOAT, false, 0, null);
		}
		
	}
}
