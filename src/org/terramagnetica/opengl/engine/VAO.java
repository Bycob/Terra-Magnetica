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
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VAO {

	class Attrib {
		public String name;
		public VBO buffer;
		public int count;
		public int type;
		public boolean normalize;
		public int stride;
		public long pointer;
		
		public Attrib() {
			
		}
		
		public Attrib(String name, VBO buffer, int count, int type, boolean normalize, int stride, long pointer) {
			this.name = name; this.buffer = buffer; this.count = count; this.type = type;
			this.normalize = normalize; this.pointer = pointer; this.stride = stride;
		}
	}
	
	private int id;
	private Program currentProgram;
	private HashMap<String, Attrib> attribs = new HashMap<String, Attrib>();
	private VBO indices = null;
	
	public VAO() {
		this.id = GL30.glGenVertexArrays();
	}
	
	public void destroyVAO() {
		GL30.glDeleteVertexArrays(this.id);
	}
	
	public void destroyAll() {
		destroyVAO();
		
		for (Entry<String, Attrib> e : this.attribs.entrySet()) {
			e.getValue().buffer.destroy();
		}
	}
	
	public boolean isBound(Painter painter) {
		return GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING) == this.id;
	}
	
	public static void unbind(Painter painter) {
		GL30.glBindVertexArray(0);
	}
	
	public void setAttrib(String name, VBO buffer, int count, int type) {
		setAttrib(name, buffer, count, type, false, 0, 0);
	}
	
	public void setAttrib(String name, VBO buffer, int count, int type, boolean normalize) {
		setAttrib(name, buffer, count, type, normalize, 0, 0);
	}
	
	public void setAttrib(String name, VBO buffer, int count, int type, boolean normalize, int stride, long pointer) {
		if (buffer == null) throw new NullPointerException("buffer == null !");
		this.attribs.put(name, new Attrib(name, buffer, count, type, normalize, stride, pointer));
	}
	
	public VBO getAttribBuffer(String name) {
		Attrib attrib = this.attribs.get(name);
		if (attrib == null) throw new IllegalArgumentException("Le nom d'attribut n'est pas reconnu.");
		return attrib.buffer;
	}
	
	public void setIndicesBuffer(VBO indices) {
		if (indices == null) throw new NullPointerException("indices == null !");
		if (indices.getTarget() != GL15.GL_ELEMENT_ARRAY_BUFFER) {
			throw new IllegalArgumentException("Bad target for indices");
		}
		
		this.indices = indices;
	}
	
	public boolean hasIndices() {
		return this.indices != null;
	}
	
	public VBO getIndicesBuffer() {
		return this.indices;
	}
	
	public void bind(Painter painter) {

		painter.getBindings().bindVertexArray(this.id);
		if (this.indices != null) this.indices.bind(painter);
		
		Program program = painter.getCurrentProgram();
		
		if (program == this.currentProgram) return;
		this.currentProgram = program;
		
		for (Entry<String, Attrib> entry : this.attribs.entrySet()) {
			Attrib attrib = entry.getValue();
			attrib.buffer.bind(painter);
			
			int attribID = program.attribID(attrib.name);
			GL20.glEnableVertexAttribArray(attribID);
			GL20.glVertexAttribPointer(attribID, attrib.count, attrib.type, attrib.normalize, attrib.stride, attrib.pointer);
		}
	}
}
