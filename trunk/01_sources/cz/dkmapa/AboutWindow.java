package cz.dkmapa;
/*

    DKMapa is program for world data visualization for Tribal Wars game.
    Copyright (C) 2008-2009, Jiri Svoboda

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * "About..." dialog class
 * 
 * @author Jiri Svoboda (http://jirkasuv.duch.cz/)
 */
public class AboutWindow extends JDialog {
    
    
    /** Eclipse generated UID */
    private static final long serialVersionUID = -2672519966307126786L;

// GUI variables
  private JPanel mainPanel;
  private JPanel leftPanel;
    private JLabel imageLabel;
  private JPanel rightPanel;
    private JLabel appTitleLabel;
    private JLabel appAuthorNameLabel;
    private JLabel appHomepageLabel;
    private JScrollPane notesScrollPane;
      private JTextArea notesTextArea;
    private JButton closeButton;


  // constructor
  public AboutWindow() {
    try {
      initComponents();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setTitle(Messages.getMessage(Messages.ABOUT) + "...");
    this.setResizable(false);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screensize.width-getWidth())/2, (screensize.height-getHeight())/2); // in center of the screen
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setModal(true);
    this.setVisible(true);
  }


  private void initComponents() {
    mainPanel = (JPanel)getContentPane();
    mainPanel.setLayout(new BorderLayout(10, 10));
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    leftPanel = new JPanel();
    leftPanel.setPreferredSize(new Dimension(180, 180));
    imageLabel = new JLabel();
    imageLabel.setIcon(new ImageIcon(DKMapa.class.getResource("AboutIcon.png")));
    leftPanel.add(imageLabel);

    rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.setPreferredSize(new Dimension(300, 180));
    appTitleLabel = new JLabel();
    appTitleLabel.setPreferredSize(new Dimension(300, 50));
    appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+4));
    appTitleLabel.setText("DKMapa " + Messages.getMessage(Messages.VERSION_NUMBER));
    appTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    rightPanel.add(appTitleLabel);
    appAuthorNameLabel = new JLabel();
    appAuthorNameLabel.setText(Messages.getMessage(Messages.AUTHOR_NAME));
    appAuthorNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    rightPanel.add(appAuthorNameLabel);
    appHomepageLabel = new JLabel();
    appHomepageLabel.setPreferredSize(new Dimension(300, 25));
    appHomepageLabel.setVerticalAlignment(SwingConstants.NORTH);
    appHomepageLabel.setText("<html><a href>" + Messages.getMessage(Messages.HOMEPAGE_URL) + "</a></html>");
    appHomepageLabel.setHorizontalAlignment(SwingConstants.CENTER); // html is left aligned by default
    appHomepageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        try {
          Desktop.getDesktop().browse(new java.net.URI(Messages.getMessage(Messages.HOMEPAGE_URL)));
        }
        catch(Exception e) {
          e.printStackTrace();
        }
      }
    });
    appHomepageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    rightPanel.add(appHomepageLabel);
    notesTextArea = new JTextArea();
    notesTextArea.setEditable(false);
    //notesTextArea.setFont(notesTextArea.getFont().deriveFont(notesTextArea.getFont().getStyle(), notesTextArea.getFont().getSize()-1));
    notesTextArea.setLineWrap(true);
    notesTextArea.setWrapStyleWord(true);
    notesTextArea.setText(Messages.getMessage(Messages.NOTES));
    notesTextArea.setCaretPosition(0); // to view from begin
    notesScrollPane = new JScrollPane();
    notesScrollPane.setViewportView(notesTextArea);
    notesScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    rightPanel.add(notesScrollPane);
    closeButton = new JButton();
    closeButton.setText(Messages.getMessage(Messages.CLOSE));
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dispose();
      }
    });
    closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    rightPanel.add(closeButton);

    mainPanel.add(leftPanel, BorderLayout.WEST);
    mainPanel.add(rightPanel, BorderLayout.EAST);

    pack();
  }

}
