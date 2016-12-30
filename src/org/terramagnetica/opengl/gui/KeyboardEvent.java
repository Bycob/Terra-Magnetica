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

package org.terramagnetica.opengl.gui;

import org.lwjgl.glfw.GLFW;

public class KeyboardEvent {
	
	private char character;
	private int key;
	private KeyState keyState;
	
	public enum KeyState {
		PRESS,
		RELEASE,
		REPEAT;
		
		public static KeyState getForGLFWEnum(int glfwEnum) {
			switch (glfwEnum) {
			case GLFW.GLFW_PRESS :
				return PRESS;
			case GLFW.GLFW_RELEASE :
				return RELEASE;
			case GLFW.GLFW_REPEAT :
				return REPEAT;
			}
			
			throw new IllegalArgumentException("Invalid GLFW enum");
		}
	}
	
	public KeyboardEvent(char character, int key, int keyStateGLFWEnum) {
		this(character, key, KeyState.getForGLFWEnum(keyStateGLFWEnum));
	}
	
	public KeyboardEvent(char character, int key, KeyState keyState) {
		this.character = character;
		this.key = key;
		this.keyState = keyState;
	}
	
	public char getCharacter() {
		return character;
	}
	
	public int getKey() {
		return key;
	}
	
	/** @returns L'état de la touche, décrit par les constantes GLFW (GLFW_PRESS,
	 * GLFW_RELEASED, GLFW_REPEAT) */
	public KeyState getKeyState() {
		return keyState;
	}
}
