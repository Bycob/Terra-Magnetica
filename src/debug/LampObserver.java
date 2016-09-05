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

package debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.Lampe;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class LampObserver {
	
	private Lampe theLamp;
	
	public static final int SELECTION_MAGNET_POINTS = 1;
	
	private int mode = 0;
	
	private boolean[] geoSituation;
	private List<Boussole> positionsCrystal = new ArrayList<Boussole>();
	private List<Boussole> positionsTheoriques = new ArrayList<Boussole>();
	private List<Double> directions = new ArrayList<Double>();
	
	public LampObserver(int mode) {
		this.mode = mode;
	}
	
	public void initWithLamp(Lampe l, boolean[] s) {
		if (this.theLamp != null) {
			return;
		}
		
		Vec2i c = l.getCoordonnéesCase();
		if (c.x != 4 || c.y != 61) {
			return;
		}
		
		this.theLamp = l;
		this.geoSituation = Arrays.copyOf(s, s.length);
	}
	
	public void placeCrystal(Lampe lamp, EntityMoving crystal, Boussole place, double dirNow, double dirBut) {
		if (this.theLamp != lamp) {
			return;
		}
		
		this.positionsCrystal.add(Boussole.getPointCardinalPourAngle(lamp.getDirection(crystal)));
		this.positionsTheoriques.add(place);
		this.directions.add(dirBut);
		this.directions.add(dirNow);
	}
	
	@Override
	public String toString() {
		if (this.theLamp == null) return "No data";
		
		String objstr = "";
		objstr += "Situation géographique : \n";
		objstr += this.theLamp.getCoordonnéesCase().toString() + "\n";
		
		for (int i = 0 ; i < 4 ; i++) {
			objstr += "-";
			objstr += Boussole.values()[i].name();
			objstr += " : ";
			
			if (this.geoSituation[i]) {
				objstr += "ok";
			}
			else {
				objstr += "impraticable";
			}
			objstr += "\n";
		}
		
		objstr += "\n";
		
		if (this.mode == LampObserver.SELECTION_MAGNET_POINTS) {
			objstr += "Statistiques sur l'arrêt des aimants : \n";
			objstr += "\tnombre de placement observés : " + this.positionsCrystal.size() + "\n";
			objstr += "\tpositions théoriques : ";
			for (Boussole pc : Boussole.values()) {
				if (this.positionsTheoriques.contains(pc)) {
					objstr += pc.name() + ", ";
				}
			}
			
			objstr += "\n\tpositions réelles : ";
			for (Boussole pc : Boussole.values()) {
				if (this.positionsCrystal.contains(pc)) {
					objstr += pc.name() + ", ";
				}
			}
			
			objstr += "\n\n\tdétail lorsque la position théorique est en désaccord avec la position réelle : \n";
			
			for (int i = 0 ; i < this.positionsCrystal.size() ; i++) {
				if (this.positionsCrystal.get(i) != this.positionsTheoriques.get(i)) {
					objstr += "\t\t";
					objstr += this.positionsCrystal.get(i).name() + " au lieu de " + this.positionsTheoriques.get(i).name();
					objstr += ", direction à atteindre : " + this.directions.get(i * 2);
					objstr += ", direction actuelle : " + this.directions.get(i * 2 + 1);
					objstr += "\n";
				}
			}
		}
		
		return objstr;
	}
}
