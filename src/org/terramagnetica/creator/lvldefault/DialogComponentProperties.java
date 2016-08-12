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

package org.terramagnetica.creator.lvldefault;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.terramagnetica.creator.DialogCreator;

import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.swing.SwingUtil;
import net.bynaryscode.util.swing.VerticalLayout;

/**
 * Boîte de dialogue ouverte lorsque l'utilisateur fait un
 * clic droit sur une entité ou un décor, et choisit l'option
 * "propriétés".<p>
 * Par défaut il contient seulement un champ "apparence de
 * l'entité".
 * @author Louis JEAN
 */
@SuppressWarnings("serial")
public abstract class DialogComponentProperties extends DialogCreator {
	
	public static final int DEFAULT_WIDTH = 450;
	
	protected static class ComponentInfos {
		protected Vec2 loc;
		protected PanelEntityProperties extension;
		
		protected ComponentInfos() {
			this.loc = new Vec2i();
		}
		
		protected ComponentInfos(Vec2 c) {
			this.loc = c;
		}
		
		protected ComponentInfos(Vec2 c, PanelEntityProperties extension) {
			this(c);
			this.extension = extension;
		}
	}
	
	protected JTextField skinField;
	
	protected DialogComponentProperties(ComponentInfos i, String name, Frame parent) {
		super(parent, name);
		
		this.skinField = new JTextField();
		JLabel lblTextField = new JLabel("Texture de l'objet : ");
		JLabel lblLoc = new JLabel("L'objet est en " + String.valueOf(i.loc));
		
		JPanel panTextField = new JPanel();
		this.skinField.setPreferredSize(new Dimension(200, 25));
		panTextField.add(lblTextField);
		panTextField.add(this.skinField);
		
		JPanel panLblLoc = new JPanel();
		panLblLoc.add(lblLoc);
		
		JPanel panMain = new JPanel();
		panMain.setLayout(new VerticalLayout(panMain));
		panMain.add(panLblLoc);
		panMain.add(panTextField);
		
		if (i.extension != null) {
			/* Si une extension est présente, le panneau principal est élargi : son layout manager
			 * devient un BoxLayout, et il comprend l'extension en plus des paramètres habituels. */
			i.extension.reset();
			
			JPanel classic = panMain;
			panMain = new JPanel();
			panMain.setLayout(new VerticalLayout(panMain, 7));
			panMain.add(classic);
			panMain.add(SwingUtil.getSeparator());
			panMain.add(i.extension);
		}
		
		this.add(panMain, BorderLayout.CENTER);
		
		final int w = DEFAULT_WIDTH, h = 200;
		if (i.extension != null) {
			DimensionsInt dims = i.extension.getDimensions();
			this.setSize(Math.max(w, dims.getWidth()), h + dims.getHeight());
		} 
		else {
			this.setSize(w, h);
		}
		
		this.setLocationRelativeTo(parent);
	}
}
