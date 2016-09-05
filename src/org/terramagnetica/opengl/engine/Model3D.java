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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.terramagnetica.opengl.engine.Painter.Primitive;

import net.bynaryscode.util.FileFormatException;
import net.bynaryscode.util.Util;
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
	
	private DisplayList displayList = new DisplayList();
	private boolean shouldRecompile = true;
	
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
							-Double.parseDouble(fragments[2])));
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
				parsed.addFace(vertices);
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
		
		if (!this.displayList.isCompiled() || this.shouldRecompile) {
			compileDisplayList(painter);
		}
		
		painter.drawList(this.displayList);
		
		for (Model3D child : this.children) {
			child.draw(painter);
		}
	}
	
	private void compileDisplayList(Painter painter) {
		painter.startRecordList(this.displayList);
		
		painter.setPrimitive(Primitive.TRIANGLES);
		this.material.use(painter);
		
		for (FaceVertex v : this.faces) {
			Vec2d texCoord = new Vec2d();
			if (v.texCoord != 0) texCoord = this.texCoords.get(v.texCoord - 1);
			Vec3d normal = new Vec3d();
			if (v.normale != 0) normal = this.normales.get(v.normale - 1);
			painter.addVertex(this.vertices.get(v.vertex - 1), normal, texCoord.x, texCoord.y);
		}

		this.material.unset(painter);
		painter.endRecordList();
		
		this.shouldRecompile = false;
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
		
		this.shouldRecompile = true;
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
	}
	
	public List<Model3D> getChildren() {
		return this.children;
	}
	
	public String getTexturePath() {
		return this.material.getTexPath();
	}
	
	public void setTextureID(int id) {
		this.material.setTextureID(id);
	}
	
	public int getTextureID() {
		return this.material.getTextureID();
	}
	
	public boolean hasTextures() {
		return this.material.hasTextures();
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
