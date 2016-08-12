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

package org.terramagnetica.game.lvldefault;

import java.util.LinkedList;
import java.util.ListIterator;

public class DialogInGame {
	
	public static class Statement {
		
		private String text;
		
		public Statement(String text) {
			this.text = text;
		}
		
		public String getText() {
			return this.text;
		}
	}
	
	private LinkedList<Statement> words = new LinkedList<Statement>();
	private ListIterator<Statement> wordsIterator = this.words.listIterator();
	
	public DialogInGame() {
		
	}
	
	public void addStatement(Statement statement) {
		this.wordsIterator.add(statement);
	}
	
	public void begin() {
		this.wordsIterator = this.words.listIterator();
		nextStatement();
	}
	
	public void goToIndex(int index) {
		if (index < 0 || index >= this.words.size()) 
			throw new IllegalArgumentException("index out of range");
		this.wordsIterator = this.words.listIterator(index + 1);
	}
	
	public int getCurrentStatementIndex() {
		return this.wordsIterator.nextIndex() - 1;
	}
	
	public Statement getCurrentStatement() {
		if (this.words.size() == 0) return null;
		
		this.wordsIterator.previous();
		return this.wordsIterator.next();
	}
	
	public Statement nextStatement() {
		if (!hasNextStatement()) return null;
		return this.wordsIterator.next();
	}
	
	public boolean hasNextStatement() {
		return this.wordsIterator.hasNext();
	}
	
	public Statement previousStatement() {
		if (this.wordsIterator.nextIndex() <= 2) return null;
		this.wordsIterator.previous();
		return getCurrentStatement();
	}
}
