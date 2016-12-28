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
		public boolean normalize;
		
		public Attrib() {
			
		}
		
		public Attrib(String name, VBO buffer, int count, int type, boolean normalize) {
			this.name = name; this.buffer = buffer; this.count = count; this.type = type; this.normalize = normalize;
		}
	}
	
	private int id;
	private Program currentProgram;
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
		setAttrib(name, buffer, count, type, false);
	}
	
	public void setAttrib(String name, VBO buffer, int count, int type, boolean normalize) {
		if (buffer == null) throw new NullPointerException("buffer == null !");
		this.attribs.put(name, new Attrib(name, buffer, count, type, normalize));
	}
	
	public VBO getAttribBuffer(String name) {
		Attrib attrib = this.attribs.get(name);
		if (attrib == null) throw new IllegalArgumentException("Le nom d'attribut n'est pas reconnu.");
		return attrib.buffer;
	}
	
	public void bind(Program program) {
		bind();
		
		if (program == this.currentProgram) return;
		this.currentProgram = program;
		
		for (Entry<String, Attrib> entry : this.attribs.entrySet()) {
			Attrib attrib = entry.getValue();
			attrib.buffer.bind();
			
			int attribID = program.attribID(attrib.name);
			GL20.glEnableVertexAttribArray(this.id);
			GL20.glVertexAttribPointer(attribID, attrib.count, attrib.type, attrib.normalize, 0, 0);
		}
		
		VBO.unbind();
	}
}
