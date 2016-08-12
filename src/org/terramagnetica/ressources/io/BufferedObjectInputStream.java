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

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.terramagnetica.game.Level;
import org.terramagnetica.game.Sauvegarde;
import org.terramagnetica.utile.GameException;
import org.terramagnetica.utile.RuntimeGameException;

/**
 * Le flux permettant de lire des objets précédemment
 * écrits dans un fichier avec le {@link BufferedObjectOutputStream}.
 * Les seuls objets supportés sont les objets implémentant
 * l'interface {@link Codable}.
 * @author Louis JEAN
 *
 */
public class BufferedObjectInputStream implements Closeable {
	
	private DataInputStream in;
	
	private ObjectNode toRead;
	private ObjectNode currentNode;
	
	public BufferedObjectInputStream(String path) throws FileNotFoundException {
		this.in = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(path)));
	}
	
	public BufferedObjectInputStream(InputStream in) {
		if (in instanceof DataInputStream) {
			this.in = (DataInputStream) in;
		}
		else {
			this.in = new DataInputStream(in);
		}
	}
	
	public BufferedObjectInputStream(URL url) throws IOException {
		this.in = new DataInputStream(
				new BufferedInputStream(
						url.openStream()));
	}
	
	public Level readLevel() throws FileNotFoundException, GameIOException, IOException {
		if (this.readID() != PrimitiveNode.getID("Object")) {
			throw new GameIOException("Impossible de lire l'objet : type");
		}
		String cn = readString0();
		this.toRead = new ObjectNode(cn);
		this.currentNode = this.toRead;
		this.readObjectNode();
		
		Level instance = null;
		try {
			
			Class<?> iClass = Class.forName(cn);
			instance = (Level) iClass.newInstance();
			instance.decode(this);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	
	public Sauvegarde readSauvegarde() throws FileNotFoundException, GameIOException, IOException {
		Sauvegarde result = null;
		try {
			result = new Sauvegarde();
		} catch (GameException e) {
			throw new RuntimeGameException("Impossible de créer la sauvegarde");
		}
		
		readCodable(result);
		
		return result;
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}
	
	public void readCodable(Codable readen) throws IOException {
		read();
		readen.decode(this);
	}
	
	public boolean hasField(int fid) {
		return this.currentNode.hasField((short) fid);
	}
	
	public <T extends Codable> T readCodableField(T field, int id) throws GameIOException {
		String tclass = field.getClass().getName();
		ObjectNode newNode = null;
		try {
			newNode = this.currentNode.getObjectField((short) id, tclass);
		} catch (GameIOException e) {
			return field;
		}
		
		if (newNode.getClassName().equals("null")) {
			return field;
		}
		
		this.currentNode = newNode;
		field.decode(this);
		this.currentNode = this.currentNode.parent;
		
		return field;
	}
	
	/** Lit une instance du type demandé. Contrairement à la méthode
	 * {@link #readCodableField(Codable, int)}, cette méthode instancie
	 * dynamiquement l'objet lu et supporte donc toutes les classes filles
	 * du type passé en paramètres.
	 * <p>L'objet lu doit en revanche pouvoir être créé via un constructeur
	 * sans arguments, sans quoi une {@link InstantiationException} est
	 * levée.
	 * @param type Le type d'objet qui doit être lu. L'objet lu portant
	 * l'indentifiant indiqué doit appartenir à cette classe, ou à une
	 * classe fille. */
	@SuppressWarnings("unchecked")
	public <T extends Codable> T readCodableInstanceField(Class<T> type, int id) 
			throws InstantiationException, IllegalAccessException, GameIOException {
		
		String tclass = type.getName();
		ObjectNode newNode = this.currentNode.getObjectField((short) id, tclass);
		if (newNode.getClassName().equals("null")) {
			return null;
		}
		
		Class<? extends T> realType = null;
		try {
			realType = (Class<? extends T>) Class.forName(newNode.getClassName());
		} catch (ClassNotFoundException e) {
			throw new GameIOException("erreur : ClassNotFoundException", e);
			//jamais levée normalement (mais possible si le fichier a été anormalement modifié)
		}
		T instance = realType.newInstance();
		
		this.currentNode = newNode;
		instance.decode(this);
		this.currentNode = this.currentNode.parent;
		
		return instance;
	}
	
	public <T extends Codable> T readCodableInstanceFieldWithDefaultValue(Class<T> type, int id, T defaultValue) 
			throws InstantiationException, IllegalAccessException {
		
		try {
			return readCodableInstanceField(type, id);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}
	
	/**
	 * Lit un champs de type primitif quelquonque.
	 * @param className - le nom de la classe du champs.
	 * @param id - L'identifiant du champs.
	 * @return Le champ, ou {@code null} si celui-ci n'est pas primitif.
	 * @throws GameIOException Si le champs de la classe indiquée,
	 * portant l'identifiant indiqué, n'existe pas.
	 */
	public Object readPrimitiveField(String className, int id) throws GameIOException {
		if (className.equals("boolean")) {
			return this.readBoolField(id);
		}
		else if (className.equals("byte")) {
			return this.readByteField(id);
		}
		else if (className.equals("short")) {
			return this.readShortField(id);
		}
		else if (className.equals("int")) {
			return this.readIntField(id);
		}
		else if (className.equals("float")) {
			return this.readFloatField(id);
		}
		else if (className.equals("long")) {
			return this.readLongField(id);
		}
		else if (className.equals("double")) {
			return this.readDoubleField(id);
		}
		else if (className.equals("String")) {
			return this.readStringField(id);
		}
		else {
			return null;
		}
	}
	
	/** Lit le champs de type boolean portant l'identifiant
	 * {@code fid}.
	 * @param fid - L'identifiant du champs.
	 * @throws GameIOException Si le champs boolean portant 
	 * l'identifiant {@code fid} n'existe pas. */
	public boolean readBoolField(int fid) throws GameIOException {
		return currentNode.getBoolField((short) fid);
	}
	
	public boolean readBoolFieldWithDefaultValue(int fid, boolean defaultValue) {
		
		try {
			return this.readBoolField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}

	/** Lit le champs de type byte portant l'identifiant 
	 * {@code fid}.
	 * @param fid - L'identifiant du champs.
	 * @throws GameIOException Si le champs byte portant 
	 * l'identifiant {@code fid} n'existe pas. */
	public byte readByteField(int fid) throws GameIOException {
		return currentNode.getByteField((short) fid);
	}
	
	/** Lit le champs de type short portant l'identifiant 
	 * {@code fid}.
	 * @param fid - L'identifiant du champs.
	 * @throws GameIOException Si le champs short portant 
	 * l'identifiant {@code fid} n'existe pas. */
	public short readShortField(int fid) throws GameIOException {
		return currentNode.getShortField((short) fid);
	}

	public int readIntField(int fid) throws GameIOException {
		return currentNode.getIntField((short) fid);
	}
	
	public int readIntFieldWithDefaultValue(int fid, int defaultValue) {
		
		try {
			return this.readIntField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}

	public float readFloatField(int fid) throws GameIOException {
		return currentNode.getFloatField((short) fid);
	}
	
	public float readFloatFieldWithDefaultValue(int fid, float defaultValue) {
		
		try {
			return this.readFloatField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}

	public long readLongField(int fid) throws GameIOException {
		return currentNode.getLongField((short) fid);
	}
	
	public long readLongFieldWithDefaultValue(int fid, long defaultValue) {
		
		try {
			return this.readLongField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}

	public double readDoubleField(int fid) throws GameIOException {
		return currentNode.getDoubleField((short) fid);
	}
	
	public double readDoubleFieldWithDefaultValue(int fid, double defaultValue) {
		try {
			return this.readDoubleField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}
	
	public String readStringField(int fid) throws GameIOException {
		return this.currentNode.getStringField((short) fid);
	}
	
	public String readStringFieldWithDefaultValue(int fid, String defaultValue) {
		
		try {
			return this.readStringField(fid);
		} catch (GameIOException e) {
			return defaultValue;
		}
	}
	
	public <T> T[] readArrayField(T[] array, int fid) 
			throws GameIOException, InstantiationException, IllegalAccessException {
		
		if (array == null) throw new NullPointerException("array == null");
		
		ArrayList<T> fields = new ArrayList<T>();
		this.readListField(fields, fid);
		array = fields.toArray(array);
		
		return array;
	}
	
	public int readArrayFieldLength(Class<?> type, int fid) throws GameIOException {
		ArrayNode aNode = this.currentNode.getArrayField((short) fid, type.getName());
		return aNode.length();
	}
	
	/**
	 * Lit une liste d'objets.
	 * @param list - La liste dans laquelle mettre tous les éléments.
	 * @param fid - L'ID de la liste.
	 * @throws GameIOException
	 * @throws InstantiationException Problème pour instancier un
	 * Codable, dans le cas où la liste est une liste d'objets.
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void readListField(List<T> list, int fid) 
			throws GameIOException, InstantiationException, IllegalAccessException {
		
		ArrayNode aNode;
		
		try {
			aNode = this.currentNode.getArrayField((short) fid, Object[].class.getName());
		} catch (GameIOException e) {
			return;
		}
		
		int length = aNode.length();
		Node[] content = aNode.getContent();
		
		for (int i = 0 ; i < length ; i++) {
			if (content[i] instanceof PrimitiveNode) {
				list.add((T) ((PrimitiveNode) content[i]).getValue());
			}
			else if (content[i] instanceof ObjectNode) {
				ObjectNode oNode = (ObjectNode) content[i];
				if (content[i].getClassName().equals("null")) {
					list.add(null);
					continue;
				}
				
				Class<? extends Codable> realType = null;
				try {
					realType = (Class<? extends Codable>) Class.forName(oNode.getClassName());
				} catch (ClassNotFoundException e) {
					throw new GameIOException("erreur : ClassNotFoundException", e);
					//jamais levée normalement.
				}
				Codable instance = realType.newInstance();
				
				ObjectNode arrayParent = this.currentNode;
				this.currentNode = oNode;
				instance.decode(this);
				this.currentNode = arrayParent;
				
				list.add((T) instance);
			}
		}
	}
	
	public <K, V> Map<K, V> readMapField(Class<K> keyClass, Class<V> valueClass, int fid) 
			throws GameIOException, InstantiationException, IllegalAccessException {
		
		Map<K, V> map = new HashMap<K, V>();
		List<MapEntry<K, V>> eList = new ArrayList<MapEntry<K, V>>();
		this.readListField(eList, fid);
		for (MapEntry<K, V> e : eList) {
			e.putInMap(map);
		}
		
		return map;
	}
	
	public boolean[] readBoolArrayField(boolean[] array, int fid) 
			throws GameIOException, InstantiationException, IllegalAccessException {
		
		Boolean[] wrapped = new Boolean[array.length];
		this.readArrayField(wrapped, fid);
		for (int i = 0 ; i < array.length ; i++) {
			if (wrapped[i] != null) array[i] = wrapped[i];
		}
		
		return array;
	}
	
	public byte[] readByteArrayField(byte[] array, int fid) 
			throws GameIOException, InstantiationException, IllegalAccessException {
		
		Byte[] wrapped = new Byte[array.length];
		this.readArrayField(wrapped, fid);
		for (int i = 0 ; i < array.length ; i++) {
			if (wrapped[i] != null) array[i] = wrapped[i];
		}
		
		return array;
	}

	private void read() throws IOException {
		if (this.readID() != PrimitiveNode.getID("Object")) {
			throw new GameIOException("Impossible de lire l'objet : type");
		}
		String cn = readString0();
		this.toRead = new ObjectNode(cn);
		this.currentNode = this.toRead;
		this.readObjectNode();
	}
	
	private void readObjectNode() throws IOException {
		while (true) {
			try {
				
				byte id = this.readID();
				
				if (id == PrimitiveNode.getID("Object")) {
					String cn = this.readString0();
					short fieldID = this.in.readShort();
					ObjectNode readen = new ObjectNode(cn);
					this.currentNode.addField(readen, fieldID);
					
					if (!readen.getClassName().equals("null")) {
						this.currentNode = readen;
						readObjectNode();
						this.currentNode = this.currentNode.parent;
					}
				}
				else if (id == PrimitiveNode.getID("end")) {
					break;
				}
				else if (PrimitiveNode.isPrimitive(id)) {
					short fid = this.in.readShort();
					this.currentNode.addField(this.readPrimitive(id), fid);
				}
				else if (id == PrimitiveNode.getID("array")) {
					short fid = this.in.readShort();
					ArrayNode aNode = readArray();
					this.currentNode.addField(aNode, fid);
				}
				else {
					throw new GameIOException("La lecture n'a pu être effectuée.\n" +
							"cause : byte inconnu.");
				}
				
			} catch (EOFException e) {
				throw new GameIOException("La lecture n'a pu être effectuée.\n" +
						"cause : fin du fichier.");
			}
		}
	}
	
	private ArrayNode readArray() throws IOException {
		byte id = this.in.readByte();
		ArrayNode readen = null;
		
		if (id == PrimitiveNode.getID("Object")) {
			try {
				readen = new ArrayNode(Class.forName(this.readString0()));
			} catch (ClassNotFoundException e) {
				readen = new ArrayNode(Object.class);
			}
		}
		else {
			readen = new ArrayNode(PrimitiveNode.defineWrapper(PrimitiveNode.getType(id)));
		}
		
		int length = this.in.readInt();
		
		for (int i = 0 ; i < length ; i++) {
			if (id == PrimitiveNode.getID("Object")) {
				ObjectNode oNode = new ObjectNode(this.readString0());
				ObjectNode arrayParent = this.currentNode;
				this.currentNode = oNode;
				
				this.readObjectNode();
				
				readen.addNode(this.currentNode);
				this.currentNode = arrayParent;
			}
			else {
				readen.addNode(readPrimitive(id));
			}
		}
		
		return readen;
	}
	
	private PrimitiveNode<?> readPrimitive(byte id) throws IOException {
		PrimitiveNode<?> result = null;
		if (id == PrimitiveNode.getID("boolean")) {
			result = new PrimitiveNode<Boolean>(this.in.readBoolean());
		}
		else if (id == PrimitiveNode.getID("byte")) {
			result = new PrimitiveNode<Byte>(this.in.readByte());
		}
		else if (id == PrimitiveNode.getID("short")) {
			result = new PrimitiveNode<Short>(this.in.readShort());
		}
		else if (id == PrimitiveNode.getID("int")) {
			result = new PrimitiveNode<Integer>(this.in.readInt());
		}
		else if (id == PrimitiveNode.getID("float")) {
			result = new PrimitiveNode<Float>(this.in.readFloat());
		}
		else if (id == PrimitiveNode.getID("long")) {
			result = new PrimitiveNode<Long>(this.in.readLong());
		}
		else if (id == PrimitiveNode.getID("double")) {
			result = new PrimitiveNode<Double>(this.in.readDouble());
		}
		else if (id == PrimitiveNode.getID("String")) {
			result = new PrimitiveNode<String>(this.readString0());
		}
		return result;
	}
	
	private String readString0() throws IOException {
		int length = this.in.readInt();
		char str[] = new char[length];
		for (int i = 0 ; i < length ; i++) {
			str[i] = this.in.readChar();
		}
		return new String(str);
	}
	
	private byte readID() throws IOException {
		return this.in.readByte();
	}
}
