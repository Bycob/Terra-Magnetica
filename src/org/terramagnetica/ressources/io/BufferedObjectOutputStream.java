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
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.ressources.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.terramagnetica.game.Level;
import org.terramagnetica.game.Sauvegarde;
import org.terramagnetica.utile.RuntimeGameException;

public class BufferedObjectOutputStream implements Closeable {
	
	private DataOutputStream out;
	private ObjectNode toWrite;
	private ObjectNode currentNode;
		
	public BufferedObjectOutputStream(String path) throws FileNotFoundException {
		this.out = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path)));
	}
	
	public BufferedObjectOutputStream(OutputStream out){
		if (out instanceof DataOutputStream) {
			this.out = (DataOutputStream) out;
		}
		else {
			this.out = new DataOutputStream(out);
		}
	}
	
	public BufferedObjectOutputStream(URL url) throws IOException{
		this.out = new DataOutputStream(
				new BufferedOutputStream(
						url.openConnection().getOutputStream()));
	}
	
	public void writeLevel(Level lvl) 
			throws FileNotFoundException, GameIOException, IOException {
		
		writeCodable(lvl);
	}
	
	public void writeSauvegarde(Sauvegarde save)
			throws FileNotFoundException, GameIOException, IOException {
		
		writeCodable(save);
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
	
	public void writeCodable(Codable codable) throws IOException {
		this.toWrite = new ObjectNode(codable.getClass().getName());
		this.currentNode = this.toWrite;
		codable.code(this);
		
		this.writeID("Object");
		this.writeString0(this.toWrite.getClassName());
		writeObjectNode(this.toWrite);
	}
	
	public void writeCodableField(Codable field, int id) throws GameIOException {
		checkWriting();
		if (field == null) {
			this.currentNode.addField(new ObjectNode("null"), (short) id);
			return;
		}
		ObjectNode newNode = new ObjectNode(field.getClass().getName());
		this.currentNode.addField(newNode, (short) id);
		this.currentNode = newNode;
		
		field.code(this);
		
		this.currentNode = newNode.parent;
	}
	
	public void writePrimitiveField(Object writed, String className, int id) {
		if (className.equals("boolean")) {
			this.writeBoolField((Boolean) writed, id);
		}
		else if (className.equals("byte")) {
			this.writeByteField((Byte) writed, id);
		}
		else if (className.equals("short")) {
			this.writeShortField((Short) writed, id);
		}
		else if (className.equals("int")) {
			this.writeIntField((Integer) writed, id);
		}
		else if (className.equals("float")) {
			this.writeFloatField((Float) writed, id);
		}
		else if (className.equals("long")) {
			this.writeLongField((Long) writed, id);
		}
		else if (className.equals("double")) {
			this.writeDoubleField((Double) writed, id);
		}
		else if (className.equals("String")) {
			this.writeStringField((String) writed, id);
		}
	}
	
	public void writeBoolField(boolean field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Boolean>(field), (short) id);
	}
	
	public void writeByteField(byte field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Byte>(field), (short) id);
	}
	
	public void writeShortField(short field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Short>(field), (short) id);
	}
	
	public void writeIntField(int field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Integer>(field), (short) id);
	}
	
	public void writeFloatField(float field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Float>(field), (short) id);
	}
	
	public void writeLongField(long field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Long>(field), (short) id);
	}
	
	public void writeDoubleField(double field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<Double>(field), (short) id);
	}
	
	public void writeStringField(String field, int id) {
		checkWriting();
		this.currentNode.addField(new PrimitiveNode<String>(field), (short) id);
	}
	
	/**
	 * Ecrit un tableau d'objet dans le buffer. Ce tableau sera pris en compte
	 * comme champs de l'objet en cours d'écriture.
	 * <p>Seuls les tableaux de variables primitives (ex : nombres) et les 
	 * tableau d'objet implémentant l'interface {@link Codable} peuvent être
	 * écris dans le buffer.
	 * @param field - Le champs à écrire. 
	 * @param id - l'identifiant par lequel on peut retrouver le champs à la
	 * lecture.
	 * @throws GameIOException si le tableau n'est pas un tableau de variables
	 * primitives ou un tableau d'implémentation de {@link Codable}.
	 */
	public void writeArrayField(Object[] field, int id) throws GameIOException {
		checkWriting();
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for (Object f : field) {
			
			if (!PrimitiveNode.definePrimitiveName(field.getClass().getComponentType()).equals("")) {
				if (f == null) continue;
				PrimitiveNode<Object> pNode = new PrimitiveNode<Object>(f);
				nodes.add(pNode);
			}
			else {
				if (!(field instanceof Codable[])) {
					throw new GameIOException(
							"Les objets n'implémentant pas l'interface \"codable\" ne sont pas supportés");
				}
				
				if (f == null) {
					nodes.add(new ObjectNode("null"));
					continue;
				}
				
				ObjectNode oNode = new ObjectNode(f.getClass().getName());
				ObjectNode arrayParent = this.currentNode;
				this.currentNode = oNode;
				((Codable) f).code(this);
				this.currentNode = arrayParent;
				nodes.add(oNode);
			}
		}
		
		ArrayNode aNode = new ArrayNode(field.getClass());
		
		for (Node n : nodes) {
			aNode.addNode(n);
		}
		
		this.currentNode.addField(aNode, (short) id);
	}
	
	public void writeBoolArrayField(boolean[] array, int id) throws GameIOException {
		Boolean[] inter = new Boolean[array.length];
		for (int i = 0 ; i < array.length ; i++) {
			inter[i] = array[i];
		}
		this.writeArrayField(inter, id);
	}
	
	public void writeByteArrayField(byte[] array, int id) throws GameIOException {
		Byte[] inter = new Byte[array.length];
		for (int i = 0 ; i < array.length ; i++) {
			inter[i] = array[i];
		}
		this.writeArrayField(inter, id);
	}
	
	public <K, V> void writeMapField(Map<K, V> map, int fid) throws GameIOException {
		MapEntry<?, ?>[] eArray = new MapEntry<?, ?>[map.size()];
		
		int i = 0;
		for (Entry<K, V> e : map.entrySet()) {
			eArray[i] = new MapEntry<K, V>(e.getKey(), e.getValue());
			i++;
		}
		
		this.writeArrayField(eArray, fid);
	}
	
	/**
	 * Ecrit dans le flux l'objet passé en paramètre. N'écrit pas 
	 * l'identifiant, le nom de la classe et l'id de champs.
	 * @param node - La représentation de l'objet à écrire.
	 * @throws IOException erreur de flux.
	 */
	private void writeObjectNode(ObjectNode node) throws IOException {
		
		for (Entry<Short, Node> e : node.getFieldSet()) {
			
			if (e.getValue() instanceof ObjectNode) {
				
				ObjectNode oNode = (ObjectNode) e.getValue();
				this.writeID("Object");
				this.writeString0(oNode.getClassName());
				this.out.writeShort(oNode.getFieldID());
				if (!oNode.getClassName().equals("null")) {
					writeObjectNode(oNode);
				}
			}
			else if (e.getValue() instanceof PrimitiveNode) {
				
				PrimitiveNode<?> pNode = (PrimitiveNode<?>) e.getValue();
				String cn = pNode.getClassName();
				this.writeID(cn);
				this.out.writeShort(pNode.getFieldID());
				this.writePrimitive(pNode);
			}
			else if (e.getValue() instanceof ArrayNode) {
				this.writeID("array");
				this.out.writeShort(e.getValue().getFieldID());
				this.writeArray((ArrayNode) e.getValue());
			}
		}
		
		this.writeID("end");
	}
	
	private void writeArray(ArrayNode aNode) throws IOException {
		if (PrimitiveNode.getID(aNode.getContentClassName()) != -1) {
			this.writeID(aNode.getContentClassName());
		}
		else {
			this.writeID("Object");
			this.writeString0(aNode.getClassName());
		}
		this.out.writeInt(aNode.length());
		
		for (Node n : aNode.getContent()) {
			if (n instanceof PrimitiveNode) {
				this.writePrimitive((PrimitiveNode<?>) n);
			}
			else if (n instanceof ObjectNode) {
				this.writeString0(n.getClassName());
				this.writeObjectNode((ObjectNode) n);
			}
		}
	}
	
	/**
	 * Ecrit dans le flux une chaine de caractère.
	 * @param toWrite - La chaine à écrire.
	 * @throws IOException erreur de flux.
	 */
	private void writeString0(String toWrite) throws IOException {
		this.out.writeInt(toWrite.length());
		this.out.writeChars(toWrite);
	}
	
	/**
	 * Ecris une variable primitive dans le flux. N'écrit pas l'identifiant,
	 * ni l'id de champs.
	 * @param pNode - La représentation de la variable primitive à écrire.
	 * @throws IOException erreur de flux.
	 */
	private void writePrimitive(PrimitiveNode<?> pNode) throws IOException {
		String cn = pNode.getClassName();
		if (cn.equals("boolean")) {
			this.out.writeBoolean((Boolean) pNode.getValue());
		}
		else if (cn.equals("byte")) {
			this.out.writeByte((Byte) pNode.getValue());
		}
		else if (cn.equals("short")) {
			this.out.writeShort((Short) pNode.getValue());
		}
		else if (cn.equals("int")) {
			this.out.writeInt((Integer) pNode.getValue());
		}
		else if (cn.equals("float")) {
			this.out.writeFloat((Float) pNode.getValue());
		}
		else if (cn.equals("long")) {
			this.out.writeLong((Long) pNode.getValue());
		}
		else if (cn.equals("double")) {
			this.out.writeDouble((Double) pNode.getValue());
		}
		else if (cn.equals("String")) {
			this.writeString0((String) pNode.getValue());
		}
	}
	
	private void writeID(String pName) throws IOException {
		this.out.writeByte(PrimitiveNode.getID(pName));
	}
	
	private void checkWriting() {
		if (this.toWrite == null) throw new RuntimeGameException(
				"Impossible d'appeler cette méthode quand la lecture n'est pas en cours");
	}
}
