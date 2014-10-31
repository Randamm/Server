package com.randomm.debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DebugWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	public JTextField tx;
	JLabel label;
	JPanel panel;
	public Img img;
	GridBagConstraints txCnstr, labelCnstr;

	public void setInfo(String info) {
		if (!info.isEmpty())
			tx.setText(info);
	}

	public DebugWindow() {
		super("Server");
		setBounds(100, 100, 800, 600);

		labelCnstr = new GridBagConstraints();

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		label = new JLabel("Info: ");
		txCnstr = new GridBagConstraints();
		tx = new JTextField(1);
		img = new Img();

		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				((Window) e.getSource()).dispose();

			}
		});

		txCnstr.gridx = 1;
		txCnstr.gridy = 0;
		txCnstr.fill = GridBagConstraints.HORIZONTAL;
		txCnstr.weightx = 1.0f;
		txCnstr.ipady = 7;
		txCnstr.insets = new Insets(5, 0, 5, 0);
		labelCnstr.gridx = 0;
		labelCnstr.gridy = 0;
		labelCnstr.insets = new Insets(5, 5, 5, 0);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18));
		panel.add(label, labelCnstr);
		panel.add(tx, txCnstr);

		add(panel, "North");
		add(img, "Center");

	}

}

