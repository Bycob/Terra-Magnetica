package org.terramagnetica.opengl.engine;

public final class RenderableFactory {

	/**
	 * Cr�e un {@link RenderableCompound} compos� d'un m�me rendu d'une case,
	 * r�p�t� un certain nombre de fois. Cela peut servir par exemple pour
	 * des objets repr�sentant une petite partie d'un mur � longueur variable.
	 * <p>L'objet de rendu obtenu sera centr� sur les coordonn�es de l'entit�.
	 * Le Renderable situ� au centre du dessin sera en position (0, 0).
	 * @param render - Le rendu � r�peter.
	 * @param size - La quantit� de rendu � concatener.
	 * @param horizontal - {@code true} si les rendus sont rang�s de gauche �
	 * droite, {@code false} s'ils sont rang�s de haut en bas.
	 * @return Un {@link RenderableCompound} correspondant � la description ci-dessus.
	 */
	public static RenderableCompound createCaseArrayRender(Renderable render, int size, boolean horizontal) {
		
		RenderableCompound r = new RenderableCompound();
		
		int startIndex = - (size / 2);
		
		for (int i = startIndex ; i < startIndex + size ; i++) {
			Renderable unit = render.clone();
			unit.setPositionOffset(horizontal ? i : 0, horizontal ? 0 : i, 0);
			
			r.addRenders(unit);
		}
		
		return r;
	}
}
