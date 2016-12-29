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

import org.lwjgl.opengl.GL11;

/**
 * Cet objet repr�sente une liste d'affichage openGL, g�r�e automatiquement.
 * @author Louis JEAN
 */
//FIXME d�molir cette merde
public class DisplayList {
	
	private int id = 0;
	private boolean compiled = false;
	private boolean compilation = false;
	
	public DisplayList() {
		
	}
	
	/**
	 * D�bute l'enregistrement de la liste d'affichage.
	 * <br>La liste pr�c�dente est d�truite.
	 * @return L'id obtenu lors de la cr�ation de la liste.
	 */
	int startCompilation() {
		if (this.compiled) {
			destroyList();
		}
		
		this.compiled = false;
		
		this.id = GL11.glGenLists(1);
		GL11.glNewList(this.id, GL11.GL_COMPILE);
		
		this.compilation = true;
		
		return this.id;
	}
	
	/**
	 * Lorsqu'une compilation est en cours, cette m�thode y met fin. La liste est
	 * alors pr�te � �tre utilis�e.
	 * @return L'id obtenu lors de la cr�ation de la liste.
	 */
	int endOfCompilation() {
		
		if (this.compilation) {
			GL11.glEndList();
			
			this.compiled = true;
			this.compilation = false;
		}
		
		return this.id;
	}
	
	/**
	 * Cette m�thode permet de d�truire la liste d�j� existante, pour pouvoir
	 * en cr�er une nouvelle. Si la compilation �tait en cours, celle-ci est
	 * arr�t�e.
	 * @return L'id de l'ancienne liste, d�truite.
	 */
	int destroyList() {
		int oldID = this.id;
		
		if (this.compilation) endOfCompilation();
		
		if (this.compiled) {
			if (GL11.glIsList(this.id)) {
				GL11.glDeleteLists(this.id, 1);
				this.id = 0;
				this.compiled = false;
			}
		}
		
		return oldID;
	}
	
	/**
	 * Appelle la liste. Si la compilation �tait en cours, elle s'arr�te
	 * avant l'appel.
	 */
	void callList() {
		if (this.compilation) {
			endOfCompilation();
		}
		if (this.compiled) {
			GL11.glCallList(this.id);
		}
	}
	
	/**
	 * @return L'id de la liste openGL.
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * @return {@code true} si la liste est compil�e, {@code false} sinon.
	 */
	public boolean isCompiled() {
		return this.compiled;
	}
	
	/**
	 * @return {@code true} si la liste est en cours de compilation,
	 * {@code false} sinon.
	 */
	public boolean isCompiling() {
		return this.compilation;
	}
	
	@Override
	protected void finalize() {
		destroyList();
	}
}
