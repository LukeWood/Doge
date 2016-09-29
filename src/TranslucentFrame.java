package goat;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;

public class TranslucentFrame {

    public TranslucentFrame() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
				    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
				}

				JWindow frame = new JWindow();
				frame.setAlwaysOnTop(true);
				Doge d = new Doge(frame);

				frame.addMouseListener(new MouseAdapter() {
				    @Override
				    public void mousePressed(MouseEvent e)
				    {
				    	d.setAction(Doge.Action.DRAG);
				    }
					@Override
				    public void mouseReleased(MouseEvent e) 
				    {
				    	d.setAction(Doge.Action.WALKING);
				    }
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if(e.getClickCount() == 2)
						{
							System.exit(0);
						}
					}
				});
				frame.setBackground(new Color(0,0,0,0));
				frame.setContentPane(new TranslucentPane());
				frame.add(d);
				frame.setLayout(null);
				frame.setSize(d.getWidth(),d.getHeight());
				
				frame.setVisible(true);
            }
        });
    }

    @SuppressWarnings("serial")
	public class TranslucentPane extends JPanel {

        public TranslucentPane() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.0f));
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());

        }

    }

}