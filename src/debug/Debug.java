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

package debug;

import org.terramagnetica.creator.Creator;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.lvldefault.Aimant;
import org.terramagnetica.game.lvldefault.DialogInGame;
import org.terramagnetica.game.lvldefault.DialogInGame.Statement;

public class Debug {
	

	
	public static void main(String[] args) {
		Debug.debug = true;
		/*FontMachine machine = new FontMachine("Century");
		machine.generate("default", 128);*/
		DialogInGame d = new DialogInGame();
		d.addStatement(new Statement("Jean claude"));
		d.goToIndex(0);
		System.out.println(d.getCurrentStatement());
		System.out.println(d.getCurrentStatementIndex());
	}
	
	
	//Proc�dure de test
	private static void testCollision(float moveX1, float moveY1, float moveX2, float moveY2) {
		Aimant aimant1 = new Aimant(129, 129);
		Aimant aimant2 = new Aimant(130, 129);
		
		aimant1.setMovement(moveX1, moveY1);
		aimant2.setMovement(moveX2, moveY2);
		
		System.out.println("#########################");
		System.out.println("vitesse de d�part aimant 1 : (" + aimant1.getMovementX() + " ;" + aimant1.getMovementY() + ")");
		System.out.println("vitesse de d�part aimant 2 : (" + aimant2.getMovementX() + " ;" + aimant2.getMovementY() + ")");
		System.out.println();
		aimant1.bounceEntity(null, aimant2, 20);
		aimant2.bounceEntity(null, aimant1, 20);
		System.out.println("vitesse apr�s collision aimant 1 : (" + aimant1.getMovementX() + " ;" + aimant1.getMovementY() + ")");
		System.out.println("vitesse apr�s collision aimant 2 : (" + aimant2.getMovementX() + " ;" + aimant2.getMovementY() + ")");
	}
	
	
	private static boolean debug = false;

	public static final boolean debug() {
		return debug;
	}
	
	private static final int GAME_APP = 0;
	private static final int CREATOR_APP = 1;
	
	
	
	private static LampObserver lampObs;
	
	public static int compteur = 0;
	
	
	
	private static void init() {
		Debug.lampObs = new LampObserver(LampObserver.SELECTION_MAGNET_POINTS);
	}
	
	private static void result() {
		System.out.println(Debug.lampObs);
	}
	

	private static void debugApp(int app, String[] args) {
		Debug.debug = true;
		
		init();
		
		switch (app) {
		case GAME_APP :
			TerraMagnetica.main(args);
			break;
		case CREATOR_APP :
			Creator.main(args);
			break;
		}
		
		result();
	}
	
	
	
	public static LampObserver getLampObserver() {
		return Debug.lampObs;
	}
}
