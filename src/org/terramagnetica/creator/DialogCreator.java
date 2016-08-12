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

package org.terramagnetica.creator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** {@link DialogCreator} est la classe parente des boites de dialogue
 * de l'éditeur de niveau. Elle contient par défaut un panneau de titre
 * et un bouton OK muni d'un listener.
 * <p>Pour adapter cette boite de dialogue à l'utilisation que vous voulez
 * en faire, ajoutez des composants dans le constructeur et héritez la
 * méthode {@link #onOKButton()} pour récupérer la réponse donnée par
 * l'utilisateur. 
 * <p>Attention !!!! Toutes les boites de dialogues n'héritent pas encore
 * de celle-ci (TODO affiliation des boites de dialogues de l'éditeur
 * de niveau).  */
@SuppressWarnings("serial")
public abstract class DialogCreator extends JDialog {
	
	private boolean closeByOkBut = false;
	
	public DialogCreator(Frame parent, String title) {
		super(parent, title, true);
		
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		

		this.setLayout(new BorderLayout());
		
		//Panneau titre
		JPanel panTitle = new JPanel();
		
		panTitle.setBackground(Color.WHITE);
		panTitle.setBorder(BorderFactory.createRaisedBevelBorder());
		
		panTitle.add(new JLabel(title));
		
		//Bouton OK
		JButton okBut = new JButton("OK");
		okBut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dispose();
				closeByOkBut = true;
				onOKButton();
			}
		});
		
		//Ajout des composants
		this.add(panTitle, BorderLayout.NORTH);
		this.add(okBut, BorderLayout.SOUTH);
	}
	
	/** Affiche la fenêtre */
	public void ask() {
		this.setVisible(true);
	}
	
	public boolean didCloseByOkButton() {
		return this.closeByOkBut;
	}
	
	/** Effectue toutes les actions lorsque l'utilisateur appuie sur le bouton
	 * "OK", après la fermeture de la fenêtre. */
	protected void onOKButton() {
		
	}
}
