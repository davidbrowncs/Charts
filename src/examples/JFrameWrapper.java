package examples;

import java.awt.Dimension;

import javax.swing.JFrame;

import graphs.Graph;

@SuppressWarnings("serial")
public class JFrameWrapper extends JFrame{
	
	public JFrameWrapper(Graph<?> g) {
		this.add(g);
		setPreferredSize(new Dimension(900, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
