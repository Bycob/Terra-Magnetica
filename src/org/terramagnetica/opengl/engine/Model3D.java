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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.terramagnetica.opengl.engine.Painter.Primitive;

import net.bynaryscode.util.FileFormatException;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * Un modèle en trois dimensions.
 * @author Louis JEAN
 *
 */
public class Model3D {
	
	public static class FaceVertex implements Cloneable {
		public FaceVertex() {this(0, 0, 0);}
		
		public FaceVertex(int vertex, int texCoord, int normale) {
			this.vertex = vertex;
			this.texCoord = texCoord;
			this.normale = normale;
		}
		
		public int vertex;
		public int texCoord;
		public int normale;
		
		@Override
		protected FaceVertex clone() {
			FaceVertex clone = null;
			try {
				clone = (FaceVertex) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return clone;
		}
	}
	
	private List<Vec3d> vertices = new ArrayList<Vec3d>();
	private List<Vec2d> texCoords = new ArrayList<Vec2d>();
	private List<Vec3d> normales = new ArrayList<Vec3d>();
	private List<FaceVertex> faces = new ArrayList<FaceVertex>();
	
	private final int vertPerFace = 3;
	private int facesCount = 0;
	
	private Material material = new Material();
	
	private List<Model3D> children = new ArrayList<Model3D>();
	
	private VAO myVAO;
	
	/**
	 * Determine le fichier contenant les informations sur les matériaux
	 * de l'objet codé dans le fichier passé en paramètres.
	 * @param fileObjContent - le contenu du fichier {@code .obj}.
	 * @param fileObjPath - le chemin du fichier {@code .obj}.
	 * @return Le chemin du fichier {@code .mtl} correspondant.
	 */
	public static String mtlPath(String fileObjContent, String fileObjPath) {
		int lio = fileObjPath.lastIndexOf('/');
		if (lio == -1) {
			lio = fileObjPath.lastIndexOf('\\');
		}
		String path = fileObjPath.substring(0, lio + 1);
		String[] fileObjLines = fileObjContent.split("\n");
		
		for (String line : fileObjLines) {
			if (line.startsWith("mtllib")) {
				return path + line.substring(7);
			}
		}
		
		return "";
	}
	
	/**
	 * Lit un fichier {@code .obj} et le fichier correspondant
	 * {@code .mtl}, et le convertit en objet {@link Model3D}.
	 * <p> {@literal <!>} Cette méthode peut lancer les exceptions
	 * de la méthode {@link #addFace(FaceVertex[])}.
	 * @param fileObj - le contenu du fichier {@code .obj}.
	 * @param fileMtl - le contenu du fichier {@code .mtl}.
	 * @param allowChild - indique si le modèle créé doit comprendre
	 * tous les objets trouvés dans le fichier (sous forme de modèles
	 * enfants) ou s'il doit être un modèle simple correspondant au 
	 * premier objet trouvé dans le fichier.
	 * @return Le modèle lu dans le fichier {@code .obj}, simple ou
	 * composé.
	 * @throws FileFormatException si les fichiers passés en paramètres
	 * ne sont pas codés comme des fichiers {@code .obj} ou {@code .mtl}
	 */
	public static Model3D parse(String fileObj, String fileMtl, boolean allowChild)
		throws FileFormatException {
		Model3D parsed = new Model3D();
		
		//Analyse du fichier "obj".
		String[] fileObjLines = fileObj.split("\n");
		ArrayList<Model3D> children = new ArrayList<Model3D>();
		int objCount = 0;
		int offsetVert = 0;
		int offsetTex = 0;
		int offsetNorm = 0;
		
		for (String line : fileObjLines) {
			if (line.startsWith("# ")) {//commentaires
				continue;
			}
			if (line.startsWith("o ")) {//objet
				objCount++;
				if (objCount == 1) {//premier objet analysé
					continue;
				}
				
				if (!allowChild && objCount > 1) {
					break;
				}
				else {
					offsetVert += parsed.vertices.size();
					offsetTex += parsed.texCoords.size();
					offsetNorm += parsed.normales.size();
					children.add(parsed);
					parsed = new Model3D();
					continue;
				}
			}
			
			String fragments[] = line.split(" ");
			if (fragments.length == 0) {
				continue;
			}
			
			if (fragments[0].equals("v")) {//coordonnées de sommets
				if (fragments.length < 4) {
					throw new FileFormatException("fichier .obj : coordonnées de vertex incomplètes");
				}
				try {
					parsed.vertices.add(new Vec3d(
							Double.parseDouble(fragments[1]), 
							Double.parseDouble(fragments[2]),
							Double.parseDouble(fragments[3])));
				} catch (NumberFormatException e) {
					throw new FileFormatException("fichier .obj : coordonnées de vertex -> nombres attendus", e);
				}
			}
			if (fragments[0].equals("vt")) {//texture UV
				if (fragments.length < 3) {
					throw new FileFormatException("fichier .obj : texture UV incomplètes");
				}
				try {
					parsed.texCoords.add(new Vec2d(
							Double.parseDouble(fragments[1]),
							1 - Double.parseDouble(fragments[2])));// 1 - value car le TextureLoader charge les textures à l'envers dans openGL
				} catch (NumberFormatException e) {
					throw new FileFormatException("fichier .obj : texture UV -> nombres attendus", e);
				}
			}
			if (fragments[0].equals("vn")) {//normales
				if (fragments.length < 4) {
					throw new FileFormatException("fichier .obj : normales incomplètes");
				}
				try {
					parsed.normales.add(new Vec3d(
							Double.parseDouble(fragments[1]),
							Double.parseDouble(fragments[2]),
							Double.parseDouble(fragments[3])));
				} catch (NumberFormatException e) {
					throw new FileFormatException("fichier .obj : normales -> nombres attendus");
				}
			}
			if (fragments[0].equals("usemtl")) {//materiau
				if (fragments.length < 2) {
					throw new FileFormatException("fichier .obj : materiau non indiqué.");
				}
				try {
					parsed.material = Material.parseMtl(fileMtl, fragments[1]);
				} catch (FileFormatException e) {
					e.printStackTrace();
					parsed.material = new Material();
				}
			}
			if (fragments[0].equals("f")) {//informations sur les faces, vient normalement en dernier.
				
				//Première étape : lire les sommets des faces.
				FaceVertex vertices[] = new FaceVertex[fragments.length - 1];
				for (int j = 0 ; j < vertices.length ; j++) {
					int j2 = j + 1;
					int data[] = Util.extractIntegers(fragments[j2]);
					if (data.length == 0) {
						throw new FileFormatException("fichier .obj : informations manquantes sur les faces");
					}
					vertices[j] = new FaceVertex();
					
					if (fragments[j2].contains("//")) {
						if (data.length >= 2) {
							vertices[j].vertex = data[0] - offsetVert;
							vertices[j].normale = data[1] - offsetNorm;
						}
						else {
							throw new FileFormatException("fichier .obj : informations manquantes sur les faces");
						}
					}
					else {
						if (data.length >= 1) {
							vertices[j].vertex = data[0] - offsetVert;
						}
						if (data.length >= 2) {
							vertices[j].texCoord = data[1] - offsetTex;
						}
						if (data.length >= 3) {
							vertices[j].normale = data[2] - offsetNorm;
						}
					}
				}
				
				//Deuxième étape : transformation en triangles et ajout au modèle.
				try {
					parsed.addFace(vertices);
				}
				catch (NullPointerException e) {
					throw new FileFormatException(e);
				}
			}
		}
		
		if (allowChild) {//Si tous les objets sont lus, crée un modèle parent à tous.
			children.add(parsed);
			parsed = new Model3D();
			for (Model3D child : children) {
				parsed.addChild(child);
			}
		}
		
		return parsed;
	}
	
	public Model3D() {
		
	}
	
	public void draw(Painter painter) {
		if (this.facesCount == 0 && this.children.size() == 0) return;
		
		if (this.myVAO == null) {
			generateBuffer(painter);
		}
		
		painter.setPrimitive(Primitive.TRIANGLES);
		this.material.use(painter);
		painter.getCurrentProgram().setUniform1i(StdUniform.USE_COLOR, 0); // FIXME intégrer ça à la configuration
		
		painter.drawVAO(this.myVAO, this.faces.size());
		
		this.material.unset(painter);
		painter.getCurrentProgram().setUniform1i(StdUniform.USE_COLOR, 1);
		
		for (Model3D child : this.children) {
			child.draw(painter);
		}
	}
	
	private void generateBuffer(Painter painter) {
		this.myVAO = new VAO();
		
		VBO vbo = new VBO();
		// [x(f)][y(f)][z(f)] [s(f)][t(f)] [nx(f)][ny(f)][nz(f)]
		int stride = 3 * 4 + 2 * 4 + 3 * 4;
		int capacity = this.facesCount * this.vertPerFace * stride;
		ByteBuffer buffer = BufferUtils.createByteBuffer(capacity);
		
		for (FaceVertex vert : this.faces) {
			Vec3d pos = this.vertices.get(vert.vertex - 1);
			buffer.putFloat((float) pos.x).putFloat((float) pos.y).putFloat((float) pos.z);

			Vec2d texCoord = new Vec2d();
			if (vert.texCoord != 0) texCoord = this.texCoords.get(vert.texCoord - 1);
			buffer.putFloat((float) texCoord.x).putFloat((float) texCoord.y);
			
			Vec3d normale = new Vec3d();
			if (vert.normale != 0) normale = this.normales.get(vert.normale - 1);
			buffer.putFloat((float) normale.x).putFloat((float) normale.y).putFloat((float) normale.z);
		}
		
		buffer.flip();
		vbo.setData(buffer);
		
		this.myVAO.setAttrib(StdAttrib.VERTEX, vbo, 3, GL11.GL_FLOAT, false, stride, 0);
		this.myVAO.setAttrib(StdAttrib.TEX_COORD, vbo, 2, GL11.GL_FLOAT, false, stride, 3 * 4);
		this.myVAO.setAttrib(StdAttrib.NORMAL, vbo, 3, GL11.GL_FLOAT, false, stride, 3 * 4 + 2 * 4);
	}
	
	/**
	 * Ajoute une face.
	 * @param vertexList - les sommets de faces, contenant toutes les
	 * informations necessaires.
	 * @throws NullPointerException si les indices que comporte un sommet
	 * de face sont invalides et ne correspondent pas à un sommet existant.
	 * @throws UnsupportedOperationException si la face contient un nombre
	 * trop peu important de sommets.
	 */
	public void addFace(Collection<FaceVertex> vertexList) {
		this.addFace(vertexList.toArray(new FaceVertex[vertexList.size()]));
	}
	
	/**
	 * Ajoute une face. La face passée en paramètres est divisée en
	 * triangles avant d'être ajoutée. Attention, cette méthode ne gère
	 * pas les faces rentrantes.
	 * @param vertexArray - les sommets de faces, contenant toutes les
	 * informations necessaires.
	 * @throws NullPointerException si les indices que comporte un sommet
	 * de face sont invalides et ne correspondent pas à un sommet existant.
	 * @throws UnsupportedOperationException si la face contient un nombre
	 * trop peu important de sommets.
	 */
	public void addFace(FaceVertex vertexArray[]) {
		if (vertexArray.length < 3) {
			throw new UnsupportedOperationException("impossible d'avoir une face à moins de 3 sommets");
		}
		for (FaceVertex v : vertexArray) {
			checkFaceVertex(v, this.facesCount);
		}
		for (int i = 1 ; i < vertexArray.length - 1 ; i++) {
			//Transforme en triangles pour avoir un seul type de faces.
			this.faces.add(vertexArray[0].clone());
			this.faces.add(vertexArray[i].clone());
			this.faces.add(vertexArray[i + 1].clone());
			
			this.facesCount++;
		}
		
		onModelEdit();
	}
	
	/** vérifie si le sommet de face indiqué est valide */
	private void checkFaceVertex(FaceVertex v, int count) throws NullPointerException {
		if (v.vertex < 0 || v.vertex - 1 >= this.vertices.size()) {
			throw new NullPointerException("Le sommet de face " + count + " désigne un point inexistant");
		}
		if (v.texCoord < 0 && v.texCoord - 1 >= this.texCoords.size()) {
			throw new NullPointerException("Le sommet de face désigne une position de texture inexistante");
		}
		if (v.normale < 0 && v.normale - 1 >= this.normales.size()) {
			throw new NullPointerException("Le sommet de face désigne une position de normale inexistante.");
		}
	}
	
	public void addChild(Model3D child) {
		if (child == null) return;
		this.children.add(child);
		
		onModelEdit();
	}
	
	public List<Model3D> getChildren() {
		return this.children;
	}
	
	public String getTexturePath() {
		return this.material.getTexPath();
	}
	
	public void setTextureID(int id) {
		this.material.setTextureID(id);
		
		this.onModelEdit();
	}
	
	public int getTextureID() {
		return this.material.getTextureID();
	}
	
	public boolean hasTextures() {
		return this.material.hasTextures();
	}
	
	public boolean isEmpty() {
		return this.faces.isEmpty();
	}
	
	private AxisAlignedBox3D boundingBox;
	public AxisAlignedBox3D getBoundingBox() {
		if (boundingBox == null) {
			this.boundingBox = AxisAlignedBox3D.createBoxFromList(this.vertices);
			
			//Ajout des boundingbox des enfants
			if (!this.children.isEmpty()) {
				ArrayList<AxisAlignedBox3D> boxes = new ArrayList<AxisAlignedBox3D>(this.children.size() + 1);
				if (!isEmpty()) boxes.add(this.boundingBox);
				
				for (int i = 0 ; i < this.children.size() ; i++) {
					if (!this.children.get(i).isEmpty()) 
						boxes.add(this.children.get(i).getBoundingBox());
				}
				
				this.boundingBox = AxisAlignedBox3D.merge(boxes.toArray(new AxisAlignedBox3D[0]));
			}
		}
		return this.boundingBox.clone();
	}
	
	private void onModelEdit() {
		this.myVAO = null;
		this.boundingBox = null;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "Modèle 3D avec :\n";
		result += " - nombres de faces : " + this.facesCount + "\n";
		result += this.vertPerFace + " sommets par faces\n";
		return result;
	}
}
