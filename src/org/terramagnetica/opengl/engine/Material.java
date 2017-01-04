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

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.FileFormatException;

public class Material {
	
	private TextureImpl tex = new TextureImpl();
	private String texPath = "";
	
	private String name = "";
	
	private Color4f diffuse;
	private Color4f specular;
	private Color4f ambient;
	private float specularShininess = 10;
	
	public Material() {
		this.diffuse = new Color4f(1f, 1f, 1f);
		this.specular = new Color4f(1f, 1f, 1f);
		this.ambient = new Color4f(0f, 0f, 0f);
	}
	
	public static Material parseMtl(String mtlFile, String mtlName) throws FileFormatException {
		Material ret = new Material();
		
		String lines[] = mtlFile.split("\n");
		boolean found = false;
		
		for (String line : lines) {
			if (line.startsWith("newmtl ")) {
				if (line.length() < 8) {
					throw new FileFormatException("fichier .mtl : nom de materiau attendu.");
				}
				String matName = line.substring(7);
				if (matName.equals(mtlName)) {
					found = true;
					continue;
				}
				else if (found) {
					break;
				}
			}
			
			String[] fragments = line.split(" ");
			if (fragments.length == 0 || !found) continue;
			
			if (fragments[0].equals("map_Kd")) {
				if (fragments.length < 2) {
					throw new FileFormatException("fichier .mtl : chemin de la texture attendu");
				}
				ret.texPath = line.substring(7);
			}
			else if (fragments[0].equals("Kd") || fragments[0].equals("Ka") || fragments[0].equals("Ks")) {
				if (fragments.length < 4) {
					throw new FileFormatException("fichier .mtl : couleur attendue");
				}
				
				double colorArray[] = new double[3];
				for (int i = 0 ; i < 3 ; i++) {
					try {
						colorArray[i] = Double.parseDouble(fragments[i + 1]);
					} catch (NumberFormatException e) {
						throw new FileFormatException("fichier .mtl : Kd / Ka / Ks -> nombres attendus");
					}
				}
				
				Color4f color = new Color4f((float) colorArray[0], (float) colorArray[1], (float) colorArray[2]);
				if (fragments[0].equals("Kd")) {
					ret.diffuse = color;
				}
				else if (fragments[0].equals("Ks")) {
					ret.specular = color;
				}
				else if (fragments[0].equals("Ka")) {
					ret.ambient = color;
				}
			}
			else if (fragments[0].equals("Ns")) {
				if (fragments.length < 2) {
					throw new FileFormatException("fichier .mtl : Ns -> paramètre attendu");
				}
				
				try {
					ret.specularShininess = (float) Double.parseDouble(fragments[1]);
				} catch (NumberFormatException e) {
					throw new FileFormatException("fichier .mtl : Ns -> nombre attendu");
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Applique le matériau au painter.
	 * @param painter
	 */
	public void use(Painter painter) {
		Program program = painter.getCurrentProgram();
		program.setUniform3f(StdUniform.Material.DIFFUSE, this.diffuse.getRedf(), this.diffuse.getGreenf(), this.diffuse.getBluef());
		program.setUniform3f(StdUniform.Material.SPECULAR, this.specular.getRedf(), this.specular.getGreenf(), this.specular.getBluef());
		program.setUniform3f(StdUniform.Material.AMBIENT, this.ambient.getRedf(), this.ambient.getGreenf(), this.ambient.getBluef());
		program.setUniform1f(StdUniform.Material.SHININESS, this.specularShininess);
		
		if (this.tex.getGLTextureID() == 0) {
			painter.setTexture(null);
		}
		else {
			painter.setTexture(this.tex);
		}
	}
	
	/**
	 * Permet de désactiver, après avoir dessiné le modèle, certaines
	 * options utilisées par le matériau.
	 * @param painter
	 */
	public void unset(Painter painter) {
		
	}
	
	public String getTexPath() {
		return this.texPath;
	}
	
	public void setTextureID(int id) {
		this.tex.setTextureID(id);
	}

	public int getTextureID() {
		return this.tex.getGLTextureID();
	}
	
	public boolean hasTextures() {
		return !"".equals(this.texPath);
	}
}
