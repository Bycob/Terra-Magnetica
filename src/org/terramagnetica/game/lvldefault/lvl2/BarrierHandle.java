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

package org.terramagnetica.game.lvldefault.lvl2;

import java.util.ArrayList;

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.CodableColor;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;

public class BarrierHandle implements Codable {
	
	private Color4f color;
	private boolean state = true;
	
	private ArrayList<BarrierStateListener> listeners = new ArrayList<BarrierStateListener>();
	
	/**
	 * @deprecated Utilis� pour le d�codage
	 */
	public BarrierHandle() {
		this(new Color4f());
	}
	
	public BarrierHandle(Color4f color) {
		setColor(color);
	}
	
	public void setColor(Color4f color) {
		this.color = color;
	}
	
	public Color4f getColor() {
		return this.color;
	}
	
	public void callListeners() {
		for (BarrierStateListener listener : this.listeners) {
			listener.setState(this.state);
		}
	}
	
	public void setState(boolean state) {
		if (this.state != state) {
			this.state = state;
			callListeners();
		}
	}
	
	public boolean getState() {
		return this.state;
	}
	
	public void addListener(BarrierStateListener l) {
		if (l == null) throw new NullPointerException();
		if (!this.color.equals(l.getColor())) {
			throw new IllegalArgumentException("Les couleurs doivent correspondre !");
		}
		
		this.listeners.add(l);
		l.setState(this.state);
	}
	
	public void removeAllListeners() {
		this.listeners.clear();
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeBoolField(this.state, 0);
		writeBarrierColor(out, this.color, 1);
	}

	@Override
	public Codable decode(BufferedObjectInputStream in) throws GameIOException {
		this.state = in.readBoolField(0);
		this.color = readBarrierColor(in, 1);
		return this;
	}
	
	/** Ecrit la couleur de la barri�re dans le fichier de fa�on
	 * � ce qu'elle soit d�finie non pas par les objets mais par
	 * le programme. Ainsi les couleurs des barri�res peuvent �tre
	 * modifi�es facilement, et les objets lus s'adapteront.
	 * <p>Si la couleur est �crite de cette fa�on, alors le champ
	 * la repr�sentant dans le fichier sera une cha�ne de caract�re.
	 * @param out - L'objet qui permet d'�crire la couleur.
	 * @param color - La couleur � �crire.
	 * @param fid - L'id de l'objet.
	 * @return {@code true} si la couleur �tait connue, dans ce cas
	 * elle sera soit {@code "blue"}, soit {@code "red"}, soit
	 * {@code "green"}. Sinon, retourne {@code false} et la couleur
	 * sera �crite dans le fichier avec ses composantes rgba, via
	 * un objet  */
	public static boolean writeBarrierColor(BufferedObjectOutputStream out, Color4f color, int fid)
		throws GameIOException {
		
		if (ControlPaneSystemManager.BLUE.equals(color)) {
			out.writeStringField("blue", fid);
			return true;
		}
		else if (ControlPaneSystemManager.RED.equals(color)) {
			out.writeStringField("red", fid);
			return true;
		}
		else if (ControlPaneSystemManager.GREEN.equals(color)) {
			out.writeStringField("green", fid);
			return true;
		}
		else {
			out.writeCodableField(new CodableColor(color), fid);
		}
		
		return false;
	}
	
	/**
	 * Lit une couleur de barri�re. Voir
	 * {@link #writeBarrierColor(BufferedObjectOutputStream, Color4f, int)}
	 * pour plus de d�tails.
	 * @param in
	 * @param fid
	 * @return
	 * @throws GameIOException
	 */
	public static Color4f readBarrierColor(BufferedObjectInputStream in, int fid) throws GameIOException {
		
		//utilis� si le champ est une chaine de caract�re ne correspondant � aucune couleur connue.
		//le code #6402 est com-pl�-te-ment al�atoire et arbitraire, mais n�cessaire pour identifier le message.
		String exceptionText = "#6402 couleur inconnue";
		
		try {
			
			String colorStr = in.readStringField(fid);
			if ("blue".equals(colorStr)) {
				return ControlPaneSystemManager.BLUE;
			}
			else if ("red".equals(colorStr)) {
				return ControlPaneSystemManager.RED;
			}
			else if ("green".equals(colorStr)) {
				return ControlPaneSystemManager.GREEN;
			}
			else throw new GameIOException(exceptionText);
			
		} catch (GameIOException e) {
			
			if (exceptionText.equals(e.getMessage())) {
				throw e;//cas particulier d�crit au d�but de la m�thode.
			}
			
			return in.readCodableField(new CodableColor(new Color4f()), fid);
		}
	}
}
