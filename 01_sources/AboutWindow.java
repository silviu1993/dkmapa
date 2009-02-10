/*
 *
 * "About..." dialog class
 *
 * (c)2008-2009, Jiri Svoboda
 * this code is under GNU GPL v3 license
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class AboutWindow extends JDialog {
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
