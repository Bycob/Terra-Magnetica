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

package debug.levelconsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.terramagnetica.creator.NiveauSaver;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.game.lvldefault.Portal;
import org.terramagnetica.game.lvldefault.Room;

public class Main {
	
	public static void script() {
		LevelDefault lvl = null;
		NiveauSaver ns = new NiveauSaver(null);
		lvl = (LevelDefault) ns.open();
		
		Room r = lvl.getMainRoom();
		List<Portal> p = new ArrayList<Portal>();
		
		for (Entity e : r.getEntities()) {
			if (e instanceof Portal) p.add((Portal)e);
		}
		
		int i = 0;
		for (Portal p2 : p) {
			output(i + p2.getCoordonnéesCase().toString() + " , ");
			i++;
		}
		
		output("\n");
		
		int index = Integer.parseInt(inputStr());
		
		output("Aller à :");
		
		try {
			output(lvl.getRoom(1).getPlayer().getCoordonnéesCase().toString());
		} catch (Exception e) {
			
		}
		
		int caseX = Integer.parseInt(inputStr());
		int caseY = Integer.parseInt(inputStr());
		
		p.get(index).setWherePlayerSent(caseX, caseY);
		
		ns.save(lvl);
	}
	
	
	
	private static Scanner sc;
	
	public static void main(String args[]) {
		sc = new Scanner(System.in);
		script();
	}
	
	public static String inputStr() {
		return sc.nextLine();
	}
	
	public static void output(String out) {
		System.out.print(out);
	}
}
