/*
 * AppView.java
 */
package com.github.mefi.jkuuza.app;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The application's main frame.
 */
public class AppView extends FrameView {

	public AppView(SingleFrameApplication app) {
		// <editor-fold defaultstate="collapsed" desc="...">
		super(app);

		initComponents();

		crawlerUrlsListModel = new DefaultListModel();
		jlstCrawlerUrls.setModel(crawlerUrlsListModel);
		jpHeaderPanel.setVisible(false);		
		flashMessagesList = new ArrayList<JLabel>();

		// status bar initialization - message timeout, idle icon and busy animation, etc
		ResourceMap resourceMap = getResourceMap();
		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});
		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if ("started".equals(propertyName)) {
					if (!busyIconTimer.isRunning()) {
						statusAnimationLabel.setIcon(busyIcons[0]);
						busyIconIndex = 0;
						busyIconTimer.start();
					}
					progressBar.setVisible(true);
					progressBar.setIndeterminate(true);
				} else if ("done".equals(propertyName)) {
					busyIconTimer.stop();
					statusAnimationLabel.setIcon(idleIcon);
					progressBar.setVisible(false);
					progressBar.setValue(0);
				} else if ("message".equals(propertyName)) {
					String text = (String) (evt.getNewValue());
					statusMessageLabel.setText((text == null) ? "" : text);
					messageTimer.restart();
				} else if ("progress".equals(propertyName)) {
					int value = (Integer) (evt.getNewValue());
					progressBar.setVisible(true);
					progressBar.setIndeterminate(false);
					progressBar.setValue(value);
				}
			}
		});


		// </editor-fold>
	}

	/**
	 * Shows About dialog
	 */
	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = App.getApplication().getMainFrame();
			aboutBox = new AboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		App.getApplication().show(aboutBox);
	}

	/**
	 * Opens FileChooser dialog and loads selected file
	 */
	@Action
	public void loadCrawlerUrlsFromFile() {

		int returnVal = jfchCrawlerUrlsChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = jfchCrawlerUrlsChooser.getSelectedFile();
			setCrawlerUrlsListFromFile(file);
		} else {
			//System.out.println("File access cancelled by user.");
		}
	}


	/**
	 * Loads URLs from File and adds them to ListModel
	 *
	 * @param file
	 */
	private void setCrawlerUrlsListFromFile(File file) {
		String url = "";
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				url = scanner.nextLine();
				crawlerUrlsListModel.addElement(new URL(url));

			}
			if (!crawlerUrlsListModel.isEmpty()) {
				jlstCrawlerUrls.setSelectedValue(crawlerUrlsListModel.lastElement(), true);
			}
		} catch (FileNotFoundException ex) {
			displayFlashMessage("CHYBA: zvolený soubor neexistuje.", FlashMessageType.ERROR);
		} catch (MalformedURLException ex) {
			displayFlashMessage("CHYBA: neplatná URL [" + url + "] - " + ex.getMessage() + ".", FlashMessageType.ERROR);
		}
	}

	/**
	 * Creates JTextField with message and displays it in panel
	 *
	 * @param message text to display in message
	 * @param type enum type of message
	 */
	public void displayFlashMessage(String message, FlashMessageType type) {
		ResourceMap resourceMap = getResourceMap();
		JLabel jlbFlashMessage = new JLabel(message, resourceMap.getIcon("FlashMessage.flashIcons[" + type + "]"), JLabel.RIGHT);
		jlbFlashMessage.setForeground(type.getForegroundColor());
		jlbFlashMessage.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		flashMessagesList.add(jlbFlashMessage);
		redrawFlashMessages();
	}

	/**
	 * Removes all flash messages from panel and hides him.
	 */
	private void redrawFlashMessages() {
		jpFlashMessages.removeAll();
		
		ListIterator<JLabel> it = flashMessagesList.listIterator(flashMessagesList.size());

		while (it.hasPrevious()) {
			jpFlashMessages.add(it.previous());
		}
		jpHeaderPanel.setVisible(true);
		jpFlashMessages.revalidate();
	}

	/**
	 * Removes all messages from panel
	 */
	public void clearFlashMessages() {
		jpFlashMessages.removeAll();
		jpFlashMessages.revalidate();
		jpHeaderPanel.setVisible(false);
		jpHeaderPanel.revalidate();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jpMainPanel = new javax.swing.JPanel();
                jpHeaderPanel = new javax.swing.JPanel();
                jScrollPane1 = new javax.swing.JScrollPane();
                jpFlashMessages = new javax.swing.JPanel();
                jlbClearFlashMessages = new javax.swing.JLabel();
                jtpTabbedPane = new javax.swing.JTabbedPane();
                jpCrawler = new javax.swing.JPanel();
                jtaCrawlerFlashMessages = new javax.swing.JTextArea();
                jpCrawlerBodyLeft = new javax.swing.JPanel();
                jspCrawlerBodyLeft = new javax.swing.JScrollPane();
                jlstCrawlerUrls = new javax.swing.JList();
                jbtCrawlertAddUrls = new javax.swing.JButton();
                jbtCrawlerRemoveUrls = new javax.swing.JButton();
                jbtCrawlerAddUrlsFromFile = new javax.swing.JButton();
                jbtCrawlerRun = new javax.swing.JButton();
                jpCrawlerBodyRight = new javax.swing.JPanel();
                jsplpCrawlerConsoleSplitPane = new javax.swing.JSplitPane();
                jspCrawlerConsole = new javax.swing.JScrollPane();
                jtaCrawlerConsole = new javax.swing.JTextArea();
                jpCrawlerBodyBottom = new javax.swing.JPanel();
                jmbMenuBar = new javax.swing.JMenuBar();
                javax.swing.JMenu fileMenu = new javax.swing.JMenu();
                javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
                javax.swing.JMenu helpMenu = new javax.swing.JMenu();
                javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
                jpStatusPanel = new javax.swing.JPanel();
                javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
                statusMessageLabel = new javax.swing.JLabel();
                statusAnimationLabel = new javax.swing.JLabel();
                progressBar = new javax.swing.JProgressBar();
                jfchCrawlerUrlsChooser = new javax.swing.JFileChooser();

                jpMainPanel.setName("jpMainPanel"); // NOI18N
                jpMainPanel.setPreferredSize(new java.awt.Dimension(800, 527));

                jpHeaderPanel.setBorder(null);
                jpHeaderPanel.setName("jpHeaderPanel"); // NOI18N

                jScrollPane1.setBorder(null);
                jScrollPane1.setAlignmentX(2.0F);
                jScrollPane1.setName("jScrollPane1"); // NOI18N

                jpFlashMessages.setBorder(null);
                jpFlashMessages.setName("jpFlashMessages"); // NOI18N
                jpFlashMessages.setLayout(new javax.swing.BoxLayout(jpFlashMessages, javax.swing.BoxLayout.PAGE_AXIS));
                jScrollPane1.setViewportView(jpFlashMessages);

                org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.github.mefi.jkuuza.app.App.class).getContext().getResourceMap(AppView.class);
                jlbClearFlashMessages.setFont(resourceMap.getFont("jlbClearFlashMessages.font")); // NOI18N
                jlbClearFlashMessages.setText(resourceMap.getString("jlbClearFlashMessages.text")); // NOI18N
                jlbClearFlashMessages.setName("jlbClearFlashMessages"); // NOI18N
                jlbClearFlashMessages.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                jlbClearFlashMessagesMouseClicked(evt);
                        }
                });

                org.jdesktop.layout.GroupLayout jpHeaderPanelLayout = new org.jdesktop.layout.GroupLayout(jpHeaderPanel);
                jpHeaderPanel.setLayout(jpHeaderPanelLayout);
                jpHeaderPanelLayout.setHorizontalGroup(
                        jpHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jpHeaderPanelLayout.createSequentialGroup()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                                .add(18, 18, 18)
                                .add(jlbClearFlashMessages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                );
                jpHeaderPanelLayout.setVerticalGroup(
                        jpHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jlbClearFlashMessages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                );

                jtpTabbedPane.setName("jtpTabbedPane"); // NOI18N

                jpCrawler.setName("jpCrawler"); // NOI18N

                jtaCrawlerFlashMessages.setBackground(resourceMap.getColor("jtaCrawlerFlashMessages.background")); // NOI18N
                jtaCrawlerFlashMessages.setColumns(20);
                jtaCrawlerFlashMessages.setEditable(false);
                jtaCrawlerFlashMessages.setLineWrap(true);
                jtaCrawlerFlashMessages.setRows(5);
                jtaCrawlerFlashMessages.setText(resourceMap.getString("jtaCrawlerFlashMessages.text")); // NOI18N
                jtaCrawlerFlashMessages.setWrapStyleWord(true);
                jtaCrawlerFlashMessages.setName("jtaCrawlerFlashMessages"); // NOI18N

                jpCrawlerBodyLeft.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jpCrawlerBodyLeft.setName("jpCrawlerBodyLeft"); // NOI18N

                jspCrawlerBodyLeft.setName("jspCrawlerBodyLeft"); // NOI18N

                jlstCrawlerUrls.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "http://example1.com", "http://example2.com", "http://example3.com", "http://example4.com", "http://example5.com" };
                        public int getSize() { return strings.length; }
                        public Object getElementAt(int i) { return strings[i]; }
                });
                jlstCrawlerUrls.setName("jlstCrawlerUrls"); // NOI18N
                jspCrawlerBodyLeft.setViewportView(jlstCrawlerUrls);

                jbtCrawlertAddUrls.setText(resourceMap.getString("jbtCrawlertAddUrls.text")); // NOI18N
                jbtCrawlertAddUrls.setName("jbtCrawlertAddUrls"); // NOI18N

                jbtCrawlerRemoveUrls.setText(resourceMap.getString("jbtCrawlerRemoveUrls.text")); // NOI18N
                jbtCrawlerRemoveUrls.setName("jbtCrawlerRemoveUrls"); // NOI18N

                javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.github.mefi.jkuuza.app.App.class).getContext().getActionMap(AppView.class, this);
                jbtCrawlerAddUrlsFromFile.setAction(actionMap.get("loadCrawlerUrlsFromFile")); // NOI18N
                jbtCrawlerAddUrlsFromFile.setText(resourceMap.getString("jbtCrawlerAddUrlsFromFile.text")); // NOI18N
                jbtCrawlerAddUrlsFromFile.setName("jbtCrawlerAddUrlsFromFile"); // NOI18N

                jbtCrawlerRun.setFont(resourceMap.getFont("jbtCrawlerRun.font")); // NOI18N
                jbtCrawlerRun.setText(resourceMap.getString("jbtCrawlerRun.text")); // NOI18N
                jbtCrawlerRun.setName("jbtCrawlerRun"); // NOI18N

                org.jdesktop.layout.GroupLayout jpCrawlerBodyLeftLayout = new org.jdesktop.layout.GroupLayout(jpCrawlerBodyLeft);
                jpCrawlerBodyLeft.setLayout(jpCrawlerBodyLeftLayout);
                jpCrawlerBodyLeftLayout.setHorizontalGroup(
                        jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jbtCrawlerRun, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                        .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                                .add(jbtCrawlertAddUrls, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jbtCrawlerRemoveUrls)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jbtCrawlerAddUrlsFromFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                        .add(jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .add(jspCrawlerBodyLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addContainerGap()))
                );

                jpCrawlerBodyLeftLayout.linkSize(new java.awt.Component[] {jbtCrawlerAddUrlsFromFile, jbtCrawlerRemoveUrls, jbtCrawlertAddUrls}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

                jpCrawlerBodyLeftLayout.setVerticalGroup(
                        jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                .addContainerGap(389, Short.MAX_VALUE)
                                .add(jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jbtCrawlerAddUrlsFromFile)
                                        .add(jbtCrawlerRemoveUrls)
                                        .add(jbtCrawlertAddUrls))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtCrawlerRun)
                                .add(23, 23, 23))
                        .add(jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .add(jspCrawlerBodyLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .add(101, 101, 101)))
                );

                jpCrawlerBodyRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jpCrawlerBodyRight.setName("jpCrawlerBodyRight"); // NOI18N

                jsplpCrawlerConsoleSplitPane.setBorder(null);
                jsplpCrawlerConsoleSplitPane.setDividerLocation(200);
                jsplpCrawlerConsoleSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
                jsplpCrawlerConsoleSplitPane.setName("jsplpCrawlerConsoleSplitPane"); // NOI18N

                jspCrawlerConsole.setBorder(null);
                jspCrawlerConsole.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                jspCrawlerConsole.setName("jspCrawlerConsole"); // NOI18N

                jtaCrawlerConsole.setBackground(resourceMap.getColor("jtaCrawlerConsole.background")); // NOI18N
                jtaCrawlerConsole.setColumns(20);
                jtaCrawlerConsole.setFont(resourceMap.getFont("jtaCrawlerConsole.font")); // NOI18N
                jtaCrawlerConsole.setForeground(resourceMap.getColor("jtaCrawlerConsole.foreground")); // NOI18N
                jtaCrawlerConsole.setRows(6);
                jtaCrawlerConsole.setText(resourceMap.getString("jtaCrawlerConsole.text")); // NOI18N
                jtaCrawlerConsole.setDoubleBuffered(true);
                jtaCrawlerConsole.setDragEnabled(true);
                jtaCrawlerConsole.setName("jtaCrawlerConsole"); // NOI18N
                jspCrawlerConsole.setViewportView(jtaCrawlerConsole);

                jsplpCrawlerConsoleSplitPane.setLeftComponent(jspCrawlerConsole);

                jpCrawlerBodyBottom.setBorder(null);
                jpCrawlerBodyBottom.setName("jpCrawlerBodyBottom"); // NOI18N

                org.jdesktop.layout.GroupLayout jpCrawlerBodyBottomLayout = new org.jdesktop.layout.GroupLayout(jpCrawlerBodyBottom);
                jpCrawlerBodyBottom.setLayout(jpCrawlerBodyBottomLayout);
                jpCrawlerBodyBottomLayout.setHorizontalGroup(
                        jpCrawlerBodyBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 445, Short.MAX_VALUE)
                );
                jpCrawlerBodyBottomLayout.setVerticalGroup(
                        jpCrawlerBodyBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 380, Short.MAX_VALUE)
                );

                jsplpCrawlerConsoleSplitPane.setRightComponent(jpCrawlerBodyBottom);

                org.jdesktop.layout.GroupLayout jpCrawlerBodyRightLayout = new org.jdesktop.layout.GroupLayout(jpCrawlerBodyRight);
                jpCrawlerBodyRight.setLayout(jpCrawlerBodyRightLayout);
                jpCrawlerBodyRightLayout.setHorizontalGroup(
                        jpCrawlerBodyRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jsplpCrawlerConsoleSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                );
                jpCrawlerBodyRightLayout.setVerticalGroup(
                        jpCrawlerBodyRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jsplpCrawlerConsoleSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                );

                org.jdesktop.layout.GroupLayout jpCrawlerLayout = new org.jdesktop.layout.GroupLayout(jpCrawler);
                jpCrawler.setLayout(jpCrawlerLayout);
                jpCrawlerLayout.setHorizontalGroup(
                        jpCrawlerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpCrawlerLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpCrawlerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jtaCrawlerFlashMessages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 714, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jpCrawlerLayout.createSequentialGroup()
                                                .add(jpCrawlerBodyLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jpCrawlerBodyRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
                );
                jpCrawlerLayout.setVerticalGroup(
                        jpCrawlerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpCrawlerLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jtaCrawlerFlashMessages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jpCrawlerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jpCrawlerBodyRight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(jpCrawlerBodyLeft, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                );

                jtpTabbedPane.addTab(resourceMap.getString("jpCrawler.TabConstraints.tabTitle"), jpCrawler); // NOI18N

                org.jdesktop.layout.GroupLayout jpMainPanelLayout = new org.jdesktop.layout.GroupLayout(jpMainPanel);
                jpMainPanel.setLayout(jpMainPanelLayout);
                jpMainPanelLayout.setHorizontalGroup(
                        jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jtpTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jpHeaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jpMainPanelLayout.setVerticalGroup(
                        jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpMainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpHeaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtpTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                                .addContainerGap())
                );

                jmbMenuBar.setName("jmbMenuBar"); // NOI18N

                fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
                fileMenu.setName("fileMenu"); // NOI18N

                exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
                exitMenuItem.setName("exitMenuItem"); // NOI18N
                fileMenu.add(exitMenuItem);

                jmbMenuBar.add(fileMenu);

                helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
                helpMenu.setName("helpMenu"); // NOI18N

                aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
                aboutMenuItem.setName("aboutMenuItem"); // NOI18N
                helpMenu.add(aboutMenuItem);

                jmbMenuBar.add(helpMenu);

                jpStatusPanel.setName("jpStatusPanel"); // NOI18N

                statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

                statusMessageLabel.setName("statusMessageLabel"); // NOI18N

                statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

                progressBar.setName("progressBar"); // NOI18N

                org.jdesktop.layout.GroupLayout jpStatusPanelLayout = new org.jdesktop.layout.GroupLayout(jpStatusPanel);
                jpStatusPanel.setLayout(jpStatusPanelLayout);
                jpStatusPanelLayout.setHorizontalGroup(
                        jpStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                        .add(jpStatusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(statusMessageLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 652, Short.MAX_VALUE)
                                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(statusAnimationLabel)
                                .addContainerGap())
                );
                jpStatusPanelLayout.setVerticalGroup(
                        jpStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpStatusPanelLayout.createSequentialGroup()
                                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jpStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(statusMessageLabel)
                                        .add(statusAnimationLabel)
                                        .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(3, 3, 3))
                );

                jfchCrawlerUrlsChooser.setName("jfchCrawlerUrlsChooser"); // NOI18N

                setComponent(jpMainPanel);
                setMenuBar(jmbMenuBar);
                setStatusBar(jpStatusPanel);
        }// </editor-fold>//GEN-END:initComponents

	/**
	 * handles mouse click event
	 *
	 * @param evt
	 */
	private void jlbClearFlashMessagesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbClearFlashMessagesMouseClicked
		clearFlashMessages();
	}//GEN-LAST:event_jlbClearFlashMessagesMouseClicked
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JButton jbtCrawlerAddUrlsFromFile;
        private javax.swing.JButton jbtCrawlerRemoveUrls;
        private javax.swing.JButton jbtCrawlerRun;
        private javax.swing.JButton jbtCrawlertAddUrls;
        private javax.swing.JFileChooser jfchCrawlerUrlsChooser;
        private javax.swing.JLabel jlbClearFlashMessages;
        private javax.swing.JList jlstCrawlerUrls;
        private javax.swing.JMenuBar jmbMenuBar;
        private javax.swing.JPanel jpCrawler;
        private javax.swing.JPanel jpCrawlerBodyBottom;
        private javax.swing.JPanel jpCrawlerBodyLeft;
        private javax.swing.JPanel jpCrawlerBodyRight;
        private javax.swing.JPanel jpFlashMessages;
        private javax.swing.JPanel jpHeaderPanel;
        private javax.swing.JPanel jpMainPanel;
        private javax.swing.JPanel jpStatusPanel;
        private javax.swing.JScrollPane jspCrawlerBodyLeft;
        private javax.swing.JScrollPane jspCrawlerConsole;
        private javax.swing.JSplitPane jsplpCrawlerConsoleSplitPane;
        private javax.swing.JTextArea jtaCrawlerConsole;
        private javax.swing.JTextArea jtaCrawlerFlashMessages;
        private javax.swing.JTabbedPane jtpTabbedPane;
        private javax.swing.JProgressBar progressBar;
        private javax.swing.JLabel statusAnimationLabel;
        private javax.swing.JLabel statusMessageLabel;
        // End of variables declaration//GEN-END:variables
	private final Timer messageTimer;
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;
	private JDialog aboutBox;
	private DefaultListModel crawlerUrlsListModel;
	private List<JLabel> flashMessagesList;
}
