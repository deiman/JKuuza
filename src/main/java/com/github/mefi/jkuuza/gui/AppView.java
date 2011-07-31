/*
 * AppView.java
 */
package com.github.mefi.jkuuza.gui;

import com.github.mefi.jkuuza.analyzer.Case;
import com.github.mefi.jkuuza.analyzer.CaseResolver;
import com.github.mefi.jkuuza.analyzer.Condition;
import com.github.mefi.jkuuza.analyzer.ConditionsResolver;
import com.github.mefi.jkuuza.analyzer.ExtractionResolver;
import com.github.mefi.jkuuza.analyzer.Rules;
import com.github.mefi.jkuuza.analyzer.Methods;
import com.github.mefi.jkuuza.analyzer.Reflector;
import com.github.mefi.jkuuza.analyzer.gui.component.JReflectorBox.JReflectorBox;
import com.github.mefi.jkuuza.app.db.CouchDbConnectionException;
import com.github.mefi.jkuuza.parser.ContentAnalyzer;
import com.github.mefi.jkuuza.gui.model.FlashMessageType;
import com.github.mefi.jkuuza.app.App;
import com.github.mefi.jkuuza.app.db.DbConnector;
import com.github.mefi.jkuuza.app.db.DefaultDbParams;
import com.github.mefi.jkuuza.crawler.SimpleCrawler;
import com.github.mefi.jkuuza.crawler.gui.CrawlerConsole;
import com.github.mefi.jkuuza.gui.model.FlashMessage;
import com.github.mefi.jkuuza.gui.model.FlashMessagesDisplayer;
import com.github.mefi.jkuuza.model.PageRepository;
import com.github.mefi.jkuuza.utils.ValueComparator;
import com.github.mefi.jkuuza.data.AnalyzerCasesLoader;
import com.github.mefi.jkuuza.data.ConfigLoader;
import com.github.mefi.jkuuza.data.ConfigSaver;
import com.github.mefi.jkuuza.model.BasicProductProperties;
import com.github.mefi.jkuuza.model.Product;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * The application's main frame.
 */
public class AppView extends FrameView {

	private AppView(SingleFrameApplication app) {
		// <editor-fold defaultstate="collapsed" desc="...">
		super(app);

		initComponents();
		settingProperties = new Properties();
		loadSettingsFromProperties();

		initAnalyzerSampleConditions();

		crawlerQueueModel = new DefaultListModel();
		jlstCrawlerQueue.setModel(crawlerQueueModel);
		jpHeaderPanel.setVisible(false);
		flashMessagesList = new ArrayList<JLabel>();

		repaintAnalyzerStep(actualAnalyzerStep);

		//init of Analyzer step2
		analyzerStep2Components = new ArrayList<JReflectorBox>();
		analyzerMethods = Reflector.getDeclaredMethodsWithInfo(ContentAnalyzer.class);

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
			setCrawlerQueueFromFile(file);
		} else {
			//jtaAnalyzerStep3Preview.append("File access cancelled by user.");
		}
	}

	@Action
	public void loadCrawlerUrlFromDialog() {
		String defaultText = "http://";
		String url = jtfCrawlerAddUrl.getText();

		if (!url.equals(defaultText)) {
			addUrlToCrawlerQueue(url);
		}
		jdCrawlerAddUrl.setVisible(false);
		jtfCrawlerAddUrl.setText(defaultText);

	}

	private void addUrlToCrawlerQueue(String u) {
		URL url = null;
		int responseCode = 0;

		try {
			url = new URL(u);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			responseCode = connection.getResponseCode();

		} catch (MalformedURLException ex) {
			displayFlashMessage("CHYBA: neplatná URL [" + url.toString() + "] - " + ex.getMessage() + ".", FlashMessageType.ERROR);
		} catch (IOException ex) {
			displayFlashMessage("CHYBA: nepodařilo se připojit k URL [" + url.toString() + "] " + ".", FlashMessageType.ERROR);
		}

		if (responseCode != 0 && responseCode != 404) {
			crawlerQueueModel.addElement(url);
			CrawlerConsole.print("Přidána URL " + url.toString() + " [status " + responseCode + "]");
		} else {
			displayFlashMessage("CHYBA: Stránka [" + url.toString() + "] nenalezena.", FlashMessageType.ERROR);
		}

		if (!crawlerQueueModel.isEmpty()) {
			jlstCrawlerQueue.setSelectedValue(crawlerQueueModel.lastElement(), true);
		}
	}

	@Action
	public void showCrawlerUrlDialog() {
		if (jdCrawlerAddUrl == null) {
			JFrame mainFrame = App.getApplication().getMainFrame();
			jdCrawlerAddUrl = new JDialog(mainFrame);
			jdCrawlerAddUrl.setLocationRelativeTo(mainFrame);
		}
		App.getApplication().show(jdCrawlerAddUrl);
	}

	/**
	 * Loads URLs from File and adds them to ListModel
	 *
	 * @param file
	 */
	private void setCrawlerQueueFromFile(File file) {
		String url = "";
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				url = scanner.nextLine();
				addUrlToCrawlerQueue(url);
			}
		} catch (FileNotFoundException ex) {
			displayFlashMessage("CHYBA: zvolený soubor neexistuje.", FlashMessageType.ERROR);
		}
	}

	@Action
	public void removeSelectedUrlFromCrawlerQueue() {
		URL url = (URL) crawlerQueueModel.remove(jlstCrawlerQueue.getSelectedIndex());
		CrawlerConsole.print("Smazána URL " + url.toString() + ".");
	}

	@Action
	public void runCrawler() {
		CrawlerConsole.print("running..");
		try {
			SimpleCrawler crawler = new SimpleCrawler();
			if (crawlerQueueModel.isEmpty()) {
				displayFlashMessage("CHYBA: žádné url ke stahování.", FlashMessageType.ERROR);
			} else {
				List list = new ArrayList();
				for (int i = 0; i < crawlerQueueModel.size(); i++) {
					list.add(crawlerQueueModel.get(i).toString());
				}
				crawler.execute(list);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Action
	public void nextAnalyzerStep() {
		if (actualAnalyzerStep <= analyzerStepsCount) {
			repaintAnalyzerStep(++actualAnalyzerStep);
		}
	}

	@Action
	public void prevAnalyzerStep() {
		if (actualAnalyzerStep > 1) {
			repaintAnalyzerStep(--actualAnalyzerStep);
		}
	}

	public void repaintAnalyzerStep(int step) {

		if (actualAnalyzerStepPanel != null) {
			jpAnalyzer.remove(actualAnalyzerStepPanel);
		}

		switch (actualAnalyzerStep) {
			case 1:
				handleAnalyzerStepStatus(1);
				actualAnalyzerStepPanel = jpAnalyzerStep1;
				jlbAnalyzerStep.setText("Krok 1/" + analyzerStepsCount);
				break;
			case 2:
				if (jcbAnalyzerStep1DomainsToAnalyze.getSelectedItem().equals("")) {
					handleAnalyzerStepStatus(1);
					actualAnalyzerStepPanel = jpAnalyzerStep1;
					--actualAnalyzerStep;
					displayFlashMessage("Není vybrána doména k extrakci!", FlashMessageType.ERROR);
				} else {
					handleAnalyzerStepStatus(2);
					actualAnalyzerStepPanel = jpAnalyzerStep2;
					jlbAnalyzerStep.setText("Krok 2/" + analyzerStepsCount);
					if (!analyzerLoadConditionsLocked) {
						if (jcbAnalyzerStep1SampleConditions.getSelectedIndex() != 0) {
							loadSavedConditions(jcbAnalyzerStep1SampleConditions.getSelectedItem().toString());
							analyzerLoadConditionsLocked = true;
						} else {
							addReflectorBoxToPane(jpAnalyzerStep2TopMain, analyzerMethods);
							analyzerLoadConditionsLocked = true;
						}
					}
				}
				break;
			case 3:
				if (jcbAnalyzerStep1SampleConditions.getSelectedIndex() != 0) {
					loadSavedRules(jcbAnalyzerStep1SampleConditions.getSelectedItem().toString());
				}
				handleAnalyzerStepStatus(3);
				actualAnalyzerStepPanel = jpAnalyzerStep3;
				jlbAnalyzerStep.setText("Krok 3/" + analyzerStepsCount);
				break;
			case 4:
				if (checkRequiredExtractionRulesFiledsFilled()) {
					actualAnalyzerStepPanel = jpAnalyzerStep4;
					jlbAnalyzerStep.setText("Probíhá analýza");
					jbtAnalyzerStepPrev.setEnabled(false);
					runAnalyzerProcess();
					jbtAnalyzerStepPrev.setEnabled(true);

				} else {
					handleAnalyzerStepStatus(3);
					actualAnalyzerStepPanel = jpAnalyzerStep3;
					--actualAnalyzerStep;
				}
				break;
			default:
		}

		actualAnalyzerStepPanel.setBorder(new EtchedBorder());
		jpAnalyzer.add(actualAnalyzerStepPanel, BorderLayout.CENTER);
		actualAnalyzerStepPanel.setVisible(true);
		jpAnalyzer.repaint();
	}

	protected void runAnalyzerProcess() {
		Case casex = createCase();

		if (!jcbAnalyzerStep1DomainsToAnalyze.getSelectedItem().equals("")) {
			try {
				CaseResolver caseResolver = new CaseResolver(casex);
				caseResolver.resolve(jcbAnalyzerStep1DomainsToAnalyze.getSelectedItem().toString());

				//TODO: GONNA CATCH EM ALL!
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			displayFlashMessage("CHYBA: Není vybrána doména k extrakci!", FlashMessageType.ERROR);
		}


	}

	protected Case createCase() {

		Rules rules = createRulesFromTextFields();

		List<Condition> conditions = new ArrayList<Condition>();
		Component[] comps = jpAnalyzerStep2TopMain.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JReflectorBox) {
				JReflectorBox reflectorBox = (JReflectorBox) comps[i];

				Condition condition = new Condition(new ContentAnalyzer(), reflectorBox.getModel().getMethodName(), reflectorBox.getModel().getExpected(), reflectorBox.getModel().getParams());
				conditions.add(condition);
			}
		}

		return new Case(conditions, rules);
	}

	private void initAnalyzerSampleConditions() {
		Map<String, Case> map = AnalyzerCasesLoader.load();
		ArrayList sampleHosts = new ArrayList();
		sampleHosts.add("-empty-");
		for (Map.Entry<String, Case> entry : map.entrySet()) {
			sampleHosts.add(entry.getKey());
		}
		jcbAnalyzerStep1SampleConditions.setModel(new DefaultComboBoxModel(sampleHosts.toArray()));
	}

	private void initAnalyzerDomainsToAnalyze() {

		TreeMap<String, Integer> map = getCrawledDomains();
		List<String> hosts = new ArrayList<String>();
		hosts.add("");
		for (Map.Entry<String, Integer> en : map.entrySet()) {
			hosts.add(en.getKey());
		}
		jcbAnalyzerStep1DomainsToAnalyze.setModel(new DefaultComboBoxModel(hosts.toArray()));
		jcbAnalyzerStep1DomainsToAnalyze.revalidate();
	}

	public void handleAnalyzerStepStatus(int step) {
		jbtAnalyzerStepPrev.setEnabled(step > 1);
		jbtAnalyzerStepNext.setEnabled(step <= analyzerStepsCount);
		if (step == analyzerStepsCount) {
			jbtAnalyzerStepNext.setText("Spustit extrakci");
		} else {
			jbtAnalyzerStepNext.setText("DÁLE >");
		}
	}

	public void loadSavedConditions(String host) {
		Map<String, Case> map = AnalyzerCasesLoader.load();
		loadReflectorBoxes(map.get(host).getConditions());
		setPreviewUrls(map.get(host));
	}

	public void loadReflectorBoxes(List<Condition> conditions) {

		for (Iterator<Condition> it = conditions.iterator(); it.hasNext();) {
			Condition condition = it.next();
			JReflectorBox box = new JReflectorBox(Reflector.getDeclaredMethodsWithInfo(condition.getConditionObject().getClass()));
			box.setDefaultValues(condition);

			analyzerStep2Components.add(box);
			jpAnalyzerStep2TopMain.add(box);
		}
	}

	public void loadSavedRules(String host) {
		Map<String, Case> map = AnalyzerCasesLoader.load();
		Rules rules = map.get(host).getRules();
		setRulesFileds(rules);
	}

	public void setPreviewUrls(Case casex) {
		jtfAnalyzerStep2Url.setText(casex.getPreviewUrl());
		jtfAnalyzerStep3Url.setText(casex.getPreviewUrl());
	}

	public void setRulesFileds(Rules rule) {

		jtfAnalyzerStep3ProductName.setText(rule.getSelector(BasicProductProperties.NAME));
		jtfAnalyzerStep3ProductDescription.setText(rule.getSelector(BasicProductProperties.DESCRIPTION));
		jtfAnalyzerStep3ProductPrice.setText(rule.getSelector(BasicProductProperties.PRICE));
		jtfAnalyzerStep3ProductPriceDPH.setText(rule.getSelector(BasicProductProperties.PRICE_DPH));
		jtfAnalyzerStep3ProductType.setText(rule.getSelector(BasicProductProperties.TYPE));
		jtfAnalyzerStep3ProductProducer.setText(rule.getSelector(BasicProductProperties.PRODUCER));
		jtfAnalyzerStep3ProductParameterName.setText(rule.getSelector(BasicProductProperties.PARAMETER_NAME));
		jtfAnalyzerStep3ProductParameterValue.setText(rule.getSelector(BasicProductProperties.PARAMETER_VALUE));
	}

	public void addReflectorBoxToPane(JPanel pane, Methods methods) {
		JReflectorBox box = new JReflectorBox(methods);
		analyzerStep2Components.add(box);
		pane.add(box);
		analyzerStep2countOfComponents++;
		pane.revalidate();
		pane.repaint();
	}

	@Action
	public void addNextReflectorComponent() {
		if (checkAllReflectorComponentsFilled()) {
			addReflectorBoxToPane(jpAnalyzerStep2TopMain, analyzerMethods);
		} else {
			displayFlashMessage("Pravidlo není správně vyplněno!", FlashMessageType.ERROR);
		}
	}

	public boolean checkRequiredExtractionRulesFiledsFilled() {
		List<JTextField> requiredFields = new ArrayList<JTextField>();
		requiredFields.add(jtfAnalyzerStep3ProductName);
		requiredFields.add(jtfAnalyzerStep3ProductPrice);

		for (Iterator<JTextField> it = requiredFields.iterator(); it.hasNext();) {
			JTextField jTextField = it.next();
			if (jTextField.getText().equals("")) {
				displayFlashMessage("Nejsou vyplněna povinná pravidla pro extrakci!", FlashMessageType.ERROR);
				return false;
			}
		}
		return true;
	}

	public boolean checkAllReflectorComponentsFilled() {
		Component[] comps = jpAnalyzerStep2TopMain.getComponents();
		boolean okStatus = true;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof JReflectorBox) {
				JReflectorBox reflectorBox = (JReflectorBox) comps[i];
				if (!reflectorBox.isFilled()) {
					okStatus = false;
				}
			}
		}
		return okStatus;
	}

	@Action
	public void removeLastReflectorComponent() {
		if (analyzerStep2Components.size() > 1) {
			JReflectorBox box = analyzerStep2Components.get(analyzerStep2Components.size() - 1);
			analyzerStep2Components.remove(box);
			jpAnalyzerStep2TopMain.remove(box);
		} else {
			displayFlashMessage("Upozornění: musí zůstat alespoň jedno pravidlo!", FlashMessageType.INFO);
		}
		jpAnalyzerStep2TopMain.repaint();
	}

	@Action
	public void previewProductExtracting() {
		if (jtfAnalyzerStep3Url.getText().equals("http://") || jtfAnalyzerStep3Url.getText().equals("")) {
			displayFlashMessage("CHYBA: Vyplňte prosím URL!", FlashMessageType.ERROR);
		} else {
			try {
				URL url = new URL(jtfAnalyzerStep3Url.getText());
				Document doc = Jsoup.parse(url, 5000);
				Rules rules = createRulesFromTextFields();
				ExtractionResolver extractionResolver = new ExtractionResolver(rules);

				//TODO:
				Product product = extractionResolver.resolve(doc);

				clearFlashMessages();
				jepAnalyzerStep3Preview.setText("");

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(stringBuilder);

				stringBuilder.append("<strong>Název:</strong> ").append(product.getName()).append("<br>");
				stringBuilder.append("<strong>Popis:</strong> ").append(product.getDescription()).append("<br>");
				stringBuilder.append("<strong>Cena:</strong> ").append(product.getPrice()).append("<br>");
				stringBuilder.append("<strong>Cena s DPH:</strong> ").append(product.getPriceDPH()).append("<br>");
				stringBuilder.append("<strong>Typ:</strong> ").append(product.getType()).append("<br>");
				stringBuilder.append("<strong>Výrobce:</strong> ").append(product.getProducer()).append("<br>");
				stringBuilder.append("<br>");
				stringBuilder.append("<strong> --- PARAMETRY --- </strong><br>");
				for (Map.Entry<String, String> en : product.getParams().entrySet()) {
					stringBuilder.append("<strong>").append(en.getKey()).append("</strong>" + " - ").append(en.getValue()).append("<br>");
				}

				jepAnalyzerStep3Preview.setText(stringBuilder.toString());

			} catch (MalformedURLException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Action
	public void previewProductAnalyzing() {
		if (jtfAnalyzerStep2Url.getText().equals("http://") || jtfAnalyzerStep2Url.getText().equals("")) {
			displayFlashMessage("CHYBA: Vyplňte prosím URL!", FlashMessageType.ERROR);
		} else {
			try {
				URL url = new URL(jtfAnalyzerStep2Url.getText());
				Document doc = Jsoup.parse(url, 10000);
				List<Condition> conditions = createConditionsFromReflectorBoxes(analyzerStep2Components);
				try {
					ConditionsResolver resolver = new ConditionsResolver(conditions);
					if (resolver.resolve(doc)) {
						clearFlashMessages();
						displayFlashMessage("Všechny podmínky byly splněny.", FlashMessageType.INFO);
					} else {
						clearFlashMessages();
						List<Condition> failedConditions = resolver.getFailedConditions();
						for (int i = 0; i < failedConditions.size(); i++) {
							Condition condition = failedConditions.get(i);
							String parameters = "";
							for (int j = 0; j < condition.getParams().size(); j++) {
								parameters += condition.getParams().get(j);
								if (j < condition.getParams().size() - 1) {
									parameters += ", ";
								}
							}
							String message = condition.getFunctionName() + "( " + parameters + " ) " + "- [ " + condition.getExpectedValue() + " ]";
							displayFlashMessage("Nesplněna podmínka: " + message, FlashMessageType.INFO);
						}

					}
				} catch (InstantiationException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IllegalAccessException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IllegalArgumentException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InvocationTargetException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				} catch (NoSuchMethodException ex) {
					Logger.getLogger(AppView.class.getName()).log(Level.SEVERE, null, ex);
				}


			} catch (IOException ex) {
				displayFlashMessage("CHYBA: Nepodařilo se připojit k URL! Zkontrolujte, prosím, zda je adresa platná.", FlashMessageType.ERROR);
			}

		}
	}

	public List<Condition> createConditionsFromReflectorBoxes(List<JReflectorBox> boxes) {
		List<Condition> list = new ArrayList<Condition>();
		for (Iterator<JReflectorBox> it = boxes.iterator(); it.hasNext();) {
			JReflectorBox box = it.next();
			Condition condition = new Condition(new ContentAnalyzer(), box.getModel().getMethodName(), box.getModel().getExpected(), box.getModel().getParams());
			list.add(condition);
		}
		return list;
	}

	public Rules createRulesFromTextFields() {
		Rules rules = new Rules();
		rules.add(BasicProductProperties.NAME, jtfAnalyzerStep3ProductName.getText());
		rules.add(BasicProductProperties.DESCRIPTION, jtfAnalyzerStep3ProductDescription.getText());
		rules.add(BasicProductProperties.PRICE, jtfAnalyzerStep3ProductPrice.getText());
		rules.add(BasicProductProperties.PRICE_DPH, jtfAnalyzerStep3ProductPriceDPH.getText());
		rules.add(BasicProductProperties.TYPE, jtfAnalyzerStep3ProductType.getText());
		rules.add(BasicProductProperties.PRODUCER, jtfAnalyzerStep3ProductProducer.getText());
		rules.add(BasicProductProperties.PARAMETER_NAME, jtfAnalyzerStep3ProductParameterName.getText());
		rules.add(BasicProductProperties.PARAMETER_VALUE, jtfAnalyzerStep3ProductParameterValue.getText());

		return rules;
	}

	public TreeMap<String, Integer> getCrawledDomains() {

		DbConnector conn = new DbConnector(settingProperties);

		PageRepository pageRepository = new PageRepository(conn.getConnection());

		HashMap<String, Integer> map = pageRepository.getCountOfRecordsPerHost();
		ValueComparator comparator = new ValueComparator(map);
		TreeMap<String, Integer> sortedMap = new TreeMap(comparator);

		sortedMap.putAll(map);
			
		return sortedMap;
	}

	/**
	 * Load values from properties and set them into setting fields
	 */
	public void loadSettingsFromProperties() {
		ConfigLoader configLoader = new ConfigLoader();
		try {
			settingProperties = configLoader.load();
			jtfSettingsHost.setText(settingProperties.getProperty("db_host"));
			jtfSettingsPort.setText(settingProperties.getProperty("db_port"));
			jtfSettingsDatabase.setText(settingProperties.getProperty("db_database"));
			jtfSettingsUsername.setText(settingProperties.getProperty("db_username"));
			jpfSettingsPassword.setText(settingProperties.getProperty("db_password"));
		} catch (IOException ex) {
			displayFlashMessage("Nepodařilo se načíst nastavení.", FlashMessageType.ERROR);
		}

	}

	/**
	 *  Get values from setting fields and save them into properties
	 */
	@Action
	public void saveSettingsToProperties() {
		clearFlashMessages();
		if (jtfSettingsHost.getText().equals("") || jtfSettingsPort.getText().equals("") || jtfSettingsDatabase.getText().equals("")) {
			displayFlashMessage("Nejsou nastaveny některé povinné údaje, budou použity výchozí!", FlashMessageType.INFO);
		}

		String host = jtfSettingsHost.getText().equals("") ? DefaultDbParams.HOST.toString() : jtfSettingsHost.getText();
		String port = jtfSettingsPort.getText().isEmpty() ? DefaultDbParams.PORT.toString() : jtfSettingsPort.getText();
		String database = jtfSettingsDatabase.getText().isEmpty() ? DefaultDbParams.DATABASE.toString() : jtfSettingsDatabase.getText();

		try {
			Integer.parseInt(port);
		} catch( Exception e) {
			displayFlashMessage("Port musí být číslo! Byl nastaven výchozí.", FlashMessageType.ERROR);
			port = DefaultDbParams.PORT.toString();
		}

		jtfSettingsHost.setText(host);
		jtfSettingsPort.setText(port);
		jtfSettingsDatabase.setText(database);

		settingProperties.put("db_host", host);
		settingProperties.put("db_port", port);
		settingProperties.put("db_database", database);

		settingProperties.put("db_username", jtfSettingsUsername.getText());
		settingProperties.put("db_password", String.valueOf(jpfSettingsPassword.getPassword()));

		ConfigSaver configSaver = new ConfigSaver();
		try {
			configSaver.save(settingProperties);
			displayFlashMessage("Nastavení bylo uloženo.", FlashMessageType.SUCCESS);
		} catch (IOException ex) {
			displayFlashMessage("Nepodařilo se uložit nastavení.", FlashMessageType.ERROR);
		}
	}

	/**
	 * Creates FlashMessage and displays it
	 *
	 * @param message text to display in message
	 * @param type enum type of message
	 */
	public void displayFlashMessage(String message, FlashMessageType type) {
		ResourceMap resourceMap = getResourceMap();

		FlashMessage flashMessage = new FlashMessage(message, type, resourceMap.getIcon("FlashMessage.flashIcons[" + type + "]"));
		FlashMessagesDisplayer displayer = FlashMessagesDisplayer.getInstance();
		displayer.add(flashMessage);
		displayer.repaint();
	}

	/**
	 * Removes all FlashMessages from panel and hides him
	 */
	public void clearFlashMessages() {
		FlashMessagesDisplayer displayer = FlashMessagesDisplayer.getInstance();
		displayer.removeAll();
		displayer.hide();
	}

	/**
	 * Returns instance of JTextArea for crawler Console
	 *
	 * @return JTextArea
	 */
	public JTextArea getCrawlerConsole() {
		return jtaCrawlerConsole;
	}

	/**
	 * Returns instance of JPanel for FlashMessages
	 *
	 * @return JPanel
	 */
	public JPanel getFlashMessagesPanel() {
		return jpFlashMessages;
	}

	/**
	 * Returns instance of header JPanel
	 *
	 * @return JPanel
	 */
	public JPanel getHeaderPanel() {
		return jpHeaderPanel;
	}

	/**
	 * get settingProperties
	 *
	 * @return
	 */
	public Properties getSettingProperties() {
		return settingProperties;
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
                jlstCrawlerQueue = new javax.swing.JList();
                jbtShowAddCrawlerUrlDialog = new javax.swing.JButton();
                jbtCrawlerRemoveUrls = new javax.swing.JButton();
                jbtCrawlerAddUrlsFromFile = new javax.swing.JButton();
                jbtCrawlerRun = new javax.swing.JButton();
                jpCrawlerBodyRight = new javax.swing.JPanel();
                jsplpCrawlerConsoleSplitPane = new javax.swing.JSplitPane();
                jspCrawlerConsole = new javax.swing.JScrollPane();
                jtaCrawlerConsole = new javax.swing.JTextArea();
                jpCrawlerBodyBottom = new javax.swing.JPanel();
                jpAnalyzer = new javax.swing.JPanel();
                jlbAnalyzerStep = new javax.swing.JLabel();
                jpAnalyzerNavigation = new javax.swing.JPanel();
                jbtAnalyzerStepPrev = new javax.swing.JButton();
                jbtAnalyzerStepNext = new javax.swing.JButton();
                jpSettings = new javax.swing.JPanel();
                jPanel1 = new javax.swing.JPanel();
                jlbSettingsHost = new javax.swing.JLabel();
                jlbSettingsPort = new javax.swing.JLabel();
                jlbSettingsDatabase = new javax.swing.JLabel();
                jlbSettingsUsername = new javax.swing.JLabel();
                jlbSettingsPassword = new javax.swing.JLabel();
                jtfSettingsHost = new javax.swing.JTextField();
                jtfSettingsPort = new javax.swing.JTextField();
                jtfSettingsUsername = new javax.swing.JTextField();
                jtfSettingsDatabase = new javax.swing.JTextField();
                jpfSettingsPassword = new javax.swing.JPasswordField();
                jButton1 = new javax.swing.JButton();
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
                jdCrawlerAddUrl = new javax.swing.JDialog();
                jlbCrawlerAddUrlDialog = new javax.swing.JLabel();
                jtfCrawlerAddUrl = new javax.swing.JTextField();
                jbtCrawlerAddUrl = new javax.swing.JButton();
                jpAnalyzerStep1 = new javax.swing.JPanel();
                jTextArea1 = new javax.swing.JTextArea();
                jcbAnalyzerStep1SampleConditions = new javax.swing.JComboBox();
                jlbAnalyzerStep1Sample = new javax.swing.JLabel();
                jcbAnalyzerStep1DomainsToAnalyze = new javax.swing.JComboBox();
                jpAnalyzerStep2 = new javax.swing.JPanel();
                jtaAnalyzerStep2TopDescription = new javax.swing.JTextArea();
                jspAnalyzerStep2Top = new javax.swing.JScrollPane();
                jpAnalyzerStep2TopMain = new javax.swing.JPanel();
                jbtAnalyzerStep2TopAddComponent = new javax.swing.JButton();
                jbtAnalyzerStep2RemoveLast = new javax.swing.JButton();
                jlbAnalyzerStep2Url = new javax.swing.JLabel();
                jtfAnalyzerStep2Url = new javax.swing.JTextField();
                jlbAnalyzerStep2Preview = new javax.swing.JButton();
                jtfAnalyzerStep2PreviewDescription = new javax.swing.JLabel();
                jpAnalyzerStep3 = new javax.swing.JPanel();
                jlbAnalyzerStep3ProductName = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductName = new javax.swing.JTextField();
                jlbAnalyzerStep3ProductPrice = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductPrice = new javax.swing.JTextField();
                jtfAnalyzerStep3ProductDescription = new javax.swing.JTextField();
                jlbAnalyzerStep3ProductDescription = new javax.swing.JLabel();
                jlbAnalyzerStep3ProductParameterName = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductParameterName = new javax.swing.JTextField();
                jlbAnalyzerStep3Url = new javax.swing.JLabel();
                jtfAnalyzerStep3PreviewDescription = new javax.swing.JLabel();
                jtfAnalyzerStep3Url = new javax.swing.JTextField();
                jtfAnalyzerStep3Preview = new javax.swing.JButton();
                jlbAnalyzerStep3ProductParameterValue = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductParameterValue = new javax.swing.JTextField();
                jlbAnalyzerStep3ProductPriceDPH = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductPriceDPH = new javax.swing.JTextField();
                jtaAnalyzerStep3Description = new javax.swing.JEditorPane();
                jlbAnalyzerStep3Preview = new javax.swing.JLabel();
                jlbAnalyzerStep3ProductType = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductType = new javax.swing.JTextField();
                jlbAnalyzerStep3ProductProducer = new javax.swing.JLabel();
                jtfAnalyzerStep3ProductProducer = new javax.swing.JTextField();
                jspAnalyzerStep3Preview = new javax.swing.JScrollPane();
                jepAnalyzerStep3Preview = new javax.swing.JEditorPane();
                jpAnalyzerStep4 = new javax.swing.JPanel();

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
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
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

                jlstCrawlerQueue.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "http://example1.com", "http://example2.com", "http://example3.com", "http://example4.com", "http://example5.com" };
                        public int getSize() { return strings.length; }
                        public Object getElementAt(int i) { return strings[i]; }
                });
                jlstCrawlerQueue.setName("jlstCrawlerQueue"); // NOI18N
                jspCrawlerBodyLeft.setViewportView(jlstCrawlerQueue);

                javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.github.mefi.jkuuza.app.App.class).getContext().getActionMap(AppView.class, this);
                jbtShowAddCrawlerUrlDialog.setAction(actionMap.get("showCrawlerUrlDialog")); // NOI18N
                jbtShowAddCrawlerUrlDialog.setText(resourceMap.getString("jbtShowAddCrawlerUrlDialog.text")); // NOI18N
                jbtShowAddCrawlerUrlDialog.setName("jbtShowAddCrawlerUrlDialog"); // NOI18N

                jbtCrawlerRemoveUrls.setAction(actionMap.get("removeSelectedUrlFromCrawlerQueue")); // NOI18N
                jbtCrawlerRemoveUrls.setText(resourceMap.getString("jbtCrawlerRemoveUrls.text")); // NOI18N
                jbtCrawlerRemoveUrls.setName("jbtCrawlerRemoveUrls"); // NOI18N

                jbtCrawlerAddUrlsFromFile.setAction(actionMap.get("loadCrawlerUrlsFromFile")); // NOI18N
                jbtCrawlerAddUrlsFromFile.setText(resourceMap.getString("jbtCrawlerAddUrlsFromFile.text")); // NOI18N
                jbtCrawlerAddUrlsFromFile.setName("jbtCrawlerAddUrlsFromFile"); // NOI18N

                jbtCrawlerRun.setAction(actionMap.get("runCrawler")); // NOI18N
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
                                                .add(jbtShowAddCrawlerUrlDialog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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

                jpCrawlerBodyLeftLayout.linkSize(new java.awt.Component[] {jbtCrawlerAddUrlsFromFile, jbtCrawlerRemoveUrls, jbtShowAddCrawlerUrlDialog}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

                jpCrawlerBodyLeftLayout.setVerticalGroup(
                        jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpCrawlerBodyLeftLayout.createSequentialGroup()
                                .addContainerGap(348, Short.MAX_VALUE)
                                .add(jpCrawlerBodyLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jbtCrawlerAddUrlsFromFile)
                                        .add(jbtCrawlerRemoveUrls)
                                        .add(jbtShowAddCrawlerUrlDialog))
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
                jspCrawlerConsole.setAutoscrolls(true);
                jspCrawlerConsole.setName("jspCrawlerConsole"); // NOI18N

                jtaCrawlerConsole.setBackground(resourceMap.getColor("jtaCrawlerConsole.background")); // NOI18N
                jtaCrawlerConsole.setColumns(20);
                jtaCrawlerConsole.setFont(resourceMap.getFont("jtaCrawlerConsole.font")); // NOI18N
                jtaCrawlerConsole.setForeground(resourceMap.getColor("jtaCrawlerConsole.foreground")); // NOI18N
                jtaCrawlerConsole.setRows(6);
                jtaCrawlerConsole.setText(resourceMap.getString("jtaCrawlerConsole.text")); // NOI18N
                jtaCrawlerConsole.setAutoscrolls(true);
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
                        .add(0, 441, Short.MAX_VALUE)
                );
                jpCrawlerBodyBottomLayout.setVerticalGroup(
                        jpCrawlerBodyBottomLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 231, Short.MAX_VALUE)
                );

                jsplpCrawlerConsoleSplitPane.setRightComponent(jpCrawlerBodyBottom);

                org.jdesktop.layout.GroupLayout jpCrawlerBodyRightLayout = new org.jdesktop.layout.GroupLayout(jpCrawlerBodyRight);
                jpCrawlerBodyRight.setLayout(jpCrawlerBodyRightLayout);
                jpCrawlerBodyRightLayout.setHorizontalGroup(
                        jpCrawlerBodyRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jsplpCrawlerConsoleSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                );
                jpCrawlerBodyRightLayout.setVerticalGroup(
                        jpCrawlerBodyRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jsplpCrawlerConsoleSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
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

                jpAnalyzer.setName("jpAnalyzer"); // NOI18N
                jpAnalyzer.setLayout(new java.awt.BorderLayout());

                jlbAnalyzerStep.setText(resourceMap.getString("jlbAnalyzerStep.text")); // NOI18N
                jlbAnalyzerStep.setName("jlbAnalyzerStep"); // NOI18N
                jpAnalyzer.add(jlbAnalyzerStep, java.awt.BorderLayout.PAGE_START);

                jpAnalyzerNavigation.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jpAnalyzerNavigation.setName("jpAnalyzerNavigation"); // NOI18N
                jpAnalyzerNavigation.setLayout(new java.awt.GridLayout(1, 0));

                jbtAnalyzerStepPrev.setAction(actionMap.get("prevAnalyzerStep")); // NOI18N
                jbtAnalyzerStepPrev.setText(resourceMap.getString("jbtAnalyzerStepPrev.text")); // NOI18N
                jbtAnalyzerStepPrev.setToolTipText(resourceMap.getString("jbtAnalyzerStepPrev.toolTipText")); // NOI18N
                jbtAnalyzerStepPrev.setName("jbtAnalyzerStepPrev"); // NOI18N
                jpAnalyzerNavigation.add(jbtAnalyzerStepPrev);

                jbtAnalyzerStepNext.setAction(actionMap.get("nextAnalyzerStep")); // NOI18N
                jbtAnalyzerStepNext.setText(resourceMap.getString("jbtAnalyzerStepNext.text")); // NOI18N
                jbtAnalyzerStepNext.setToolTipText(resourceMap.getString("jbtAnalyzerStepNext.toolTipText")); // NOI18N
                jbtAnalyzerStepNext.setName("jbtAnalyzerStepNext"); // NOI18N
                jpAnalyzerNavigation.add(jbtAnalyzerStepNext);

                jpAnalyzer.add(jpAnalyzerNavigation, java.awt.BorderLayout.PAGE_END);

                jtpTabbedPane.addTab(resourceMap.getString("jpAnalyzer.TabConstraints.tabTitle"), jpAnalyzer); // NOI18N

                jpSettings.setName("jpSettings"); // NOI18N

                jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
                jPanel1.setName("jPanel1"); // NOI18N

                jlbSettingsHost.setText(resourceMap.getString("jlbSettingsHost.text")); // NOI18N
                jlbSettingsHost.setName("jlbSettingsHost"); // NOI18N

                jlbSettingsPort.setText(resourceMap.getString("jlbSettingsPort.text")); // NOI18N
                jlbSettingsPort.setName("jlbSettingsPort"); // NOI18N

                jlbSettingsDatabase.setText(resourceMap.getString("jlbSettingsDatabase.text")); // NOI18N
                jlbSettingsDatabase.setName("jlbSettingsDatabase"); // NOI18N

                jlbSettingsUsername.setText(resourceMap.getString("jlbSettingsUsername.text")); // NOI18N
                jlbSettingsUsername.setName("jlbSettingsUsername"); // NOI18N

                jlbSettingsPassword.setText(resourceMap.getString("jlbSettingsPassword.text")); // NOI18N
                jlbSettingsPassword.setName("jlbSettingsPassword"); // NOI18N

                jtfSettingsHost.setText(resourceMap.getString("jtfSettingsHost.text")); // NOI18N
                jtfSettingsHost.setName("jtfSettingsHost"); // NOI18N

                jtfSettingsPort.setText(resourceMap.getString("jtfSettingsPort.text")); // NOI18N
                jtfSettingsPort.setName("jtfSettingsPort"); // NOI18N

                jtfSettingsUsername.setText(resourceMap.getString("jtfSettingsUsername.text")); // NOI18N
                jtfSettingsUsername.setName("jtfSettingsUsername"); // NOI18N

                jtfSettingsDatabase.setText(resourceMap.getString("jtfSettingsDatabase.text")); // NOI18N
                jtfSettingsDatabase.setName("jtfSettingsDatabase"); // NOI18N

                jpfSettingsPassword.setText(resourceMap.getString("jpfSettingsPassword.text")); // NOI18N
                jpfSettingsPassword.setName("jpfSettingsPassword"); // NOI18N

                org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel1Layout.createSequentialGroup()
                                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jlbSettingsHost)
                                                        .add(jlbSettingsPort)
                                                        .add(jlbSettingsDatabase))
                                                .add(71, 71, 71)
                                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jtfSettingsDatabase, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                                                        .add(jtfSettingsPort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                                                        .add(jtfSettingsHost, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)))
                                        .add(jPanel1Layout.createSequentialGroup()
                                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jlbSettingsUsername)
                                                        .add(jlbSettingsPassword))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jpfSettingsPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                                                        .add(jtfSettingsUsername, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE))))
                                .addContainerGap())
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbSettingsHost)
                                        .add(jtfSettingsHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbSettingsPort)
                                        .add(jtfSettingsPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbSettingsDatabase)
                                        .add(jtfSettingsDatabase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbSettingsUsername)
                                        .add(jtfSettingsUsername, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbSettingsPassword)
                                        .add(jpfSettingsPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jButton1.setAction(actionMap.get("saveSettingsToProperties")); // NOI18N
                jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
                jButton1.setName("jButton1"); // NOI18N

                org.jdesktop.layout.GroupLayout jpSettingsLayout = new org.jdesktop.layout.GroupLayout(jpSettings);
                jpSettings.setLayout(jpSettingsLayout);
                jpSettingsLayout.setHorizontalGroup(
                        jpSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 179, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                );
                jpSettingsLayout.setVerticalGroup(
                        jpSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpSettingsLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 231, Short.MAX_VALUE)
                                .add(jButton1)
                                .addContainerGap())
                );

                jtpTabbedPane.addTab(resourceMap.getString("jpSettings.TabConstraints.tabTitle"), jpSettings); // NOI18N

                org.jdesktop.layout.GroupLayout jpMainPanelLayout = new org.jdesktop.layout.GroupLayout(jpMainPanel);
                jpMainPanel.setLayout(jpMainPanelLayout);
                jpMainPanelLayout.setHorizontalGroup(
                        jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jtpTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jpHeaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jpMainPanelLayout.setVerticalGroup(
                        jpMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpMainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jpHeaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtpTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
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
                        .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
                        .add(jpStatusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(statusMessageLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 648, Short.MAX_VALUE)
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

                jdCrawlerAddUrl.setName("jdCrawlerAddUrl"); // NOI18N

                jlbCrawlerAddUrlDialog.setText(resourceMap.getString("jlbCrawlerAddUrlDialog.text")); // NOI18N
                jlbCrawlerAddUrlDialog.setName("jlbCrawlerAddUrlDialog"); // NOI18N

                jtfCrawlerAddUrl.setText(resourceMap.getString("jtfCrawlerAddUrl.text")); // NOI18N
                jtfCrawlerAddUrl.setName("jtfCrawlerAddUrl"); // NOI18N
                jtfCrawlerAddUrl.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                                jtfCrawlerAddUrlKeyReleased(evt);
                        }
                });

                jbtCrawlerAddUrl.setAction(actionMap.get("loadCrawlerUrlFromDialog")); // NOI18N
                jbtCrawlerAddUrl.setText(resourceMap.getString("jbtCrawlerAddUrl.text")); // NOI18N
                jbtCrawlerAddUrl.setName("jbtCrawlerAddUrl"); // NOI18N

                org.jdesktop.layout.GroupLayout jdCrawlerAddUrlLayout = new org.jdesktop.layout.GroupLayout(jdCrawlerAddUrl.getContentPane());
                jdCrawlerAddUrl.getContentPane().setLayout(jdCrawlerAddUrlLayout);
                jdCrawlerAddUrlLayout.setHorizontalGroup(
                        jdCrawlerAddUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jdCrawlerAddUrlLayout.createSequentialGroup()
                                .add(jdCrawlerAddUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jdCrawlerAddUrlLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jlbCrawlerAddUrlDialog)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jtfCrawlerAddUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
                                        .add(jdCrawlerAddUrlLayout.createSequentialGroup()
                                                .add(182, 182, 182)
                                                .add(jbtCrawlerAddUrl)))
                                .addContainerGap())
                );
                jdCrawlerAddUrlLayout.setVerticalGroup(
                        jdCrawlerAddUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jdCrawlerAddUrlLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jdCrawlerAddUrlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbCrawlerAddUrlDialog)
                                        .add(jtfCrawlerAddUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtCrawlerAddUrl)
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jpAnalyzerStep1.setName("jpAnalyzerStep1"); // NOI18N

                jTextArea1.setColumns(20);
                jTextArea1.setRows(5);
                jTextArea1.setText(resourceMap.getString("jTextArea1.text")); // NOI18N
                jTextArea1.setName("jTextArea1"); // NOI18N

                jcbAnalyzerStep1SampleConditions.setName("jcbAnalyzerStep1SampleConditions"); // NOI18N
                jcbAnalyzerStep1SampleConditions.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jcbAnalyzerStep1SampleConditionsActionPerformed(evt);
                        }
                });

                jlbAnalyzerStep1Sample.setText(resourceMap.getString("jlbAnalyzerStep1Sample.text")); // NOI18N
                jlbAnalyzerStep1Sample.setName("jlbAnalyzerStep1Sample"); // NOI18N

                jcbAnalyzerStep1DomainsToAnalyze.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "vyberte doménu" }));
                jcbAnalyzerStep1DomainsToAnalyze.setName("jcbAnalyzerStep1DomainsToAnalyze"); // NOI18N
                jcbAnalyzerStep1DomainsToAnalyze.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mousePressed(java.awt.event.MouseEvent evt) {
                                jcbAnalyzerStep1DomainsToAnalyzeMousePressed(evt);
                        }
                });

                org.jdesktop.layout.GroupLayout jpAnalyzerStep1Layout = new org.jdesktop.layout.GroupLayout(jpAnalyzerStep1);
                jpAnalyzerStep1.setLayout(jpAnalyzerStep1Layout);
                jpAnalyzerStep1Layout.setHorizontalGroup(
                        jpAnalyzerStep1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep1Layout.createSequentialGroup()
                                .add(jpAnalyzerStep1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jpAnalyzerStep1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jpAnalyzerStep1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jTextArea1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                                                        .add(jpAnalyzerStep1Layout.createSequentialGroup()
                                                                .add(jlbAnalyzerStep1Sample)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jcbAnalyzerStep1SampleConditions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                                        .add(jpAnalyzerStep1Layout.createSequentialGroup()
                                                .add(73, 73, 73)
                                                .add(jcbAnalyzerStep1DomainsToAnalyze, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                );
                jpAnalyzerStep1Layout.setVerticalGroup(
                        jpAnalyzerStep1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep1Layout.createSequentialGroup()
                                .add(jTextArea1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbAnalyzerStep1DomainsToAnalyze, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 259, Short.MAX_VALUE)
                                .add(jpAnalyzerStep1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbAnalyzerStep1Sample)
                                        .add(jcbAnalyzerStep1SampleConditions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                );

                jpAnalyzerStep2.setName("jpAnalyzerStep2"); // NOI18N
                jpAnalyzerStep2.setPreferredSize(new java.awt.Dimension(421, 321));

                jtaAnalyzerStep2TopDescription.setColumns(20);
                jtaAnalyzerStep2TopDescription.setEditable(false);
                jtaAnalyzerStep2TopDescription.setLineWrap(true);
                jtaAnalyzerStep2TopDescription.setRows(5);
                jtaAnalyzerStep2TopDescription.setText(resourceMap.getString("jtaAnalyzerStep2TopDescription.text")); // NOI18N
                jtaAnalyzerStep2TopDescription.setWrapStyleWord(true);
                jtaAnalyzerStep2TopDescription.setName("jtaAnalyzerStep2TopDescription"); // NOI18N

                jspAnalyzerStep2Top.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jspAnalyzerStep2Top.setName("jspAnalyzerStep2Top"); // NOI18N

                jpAnalyzerStep2TopMain.setBorder(null);
                jpAnalyzerStep2TopMain.setName("jpAnalyzerStep2TopMain"); // NOI18N
                jpAnalyzerStep2TopMain.setLayout(new java.awt.GridLayout(0, 1));
                jspAnalyzerStep2Top.setViewportView(jpAnalyzerStep2TopMain);

                jbtAnalyzerStep2TopAddComponent.setAction(actionMap.get("addNextReflectorComponent")); // NOI18N
                jbtAnalyzerStep2TopAddComponent.setText(resourceMap.getString("jbtAnalyzerStep2TopAddComponent.text")); // NOI18N
                jbtAnalyzerStep2TopAddComponent.setName("jbtAnalyzerStep2TopAddComponent"); // NOI18N

                jbtAnalyzerStep2RemoveLast.setAction(actionMap.get("removeLastReflectorComponent")); // NOI18N
                jbtAnalyzerStep2RemoveLast.setText(resourceMap.getString("jbtAnalyzerStep2RemoveLast.text")); // NOI18N
                jbtAnalyzerStep2RemoveLast.setName("jbtAnalyzerStep2RemoveLast"); // NOI18N

                jlbAnalyzerStep2Url.setText(resourceMap.getString("jlbAnalyzerStep2Url.text")); // NOI18N
                jlbAnalyzerStep2Url.setName("jlbAnalyzerStep2Url"); // NOI18N

                jtfAnalyzerStep2Url.setText(resourceMap.getString("jtfAnalyzerStep2Url.text")); // NOI18N
                jtfAnalyzerStep2Url.setName("jtfAnalyzerStep2Url"); // NOI18N

                jlbAnalyzerStep2Preview.setAction(actionMap.get("previewProductAnalyzing")); // NOI18N
                jlbAnalyzerStep2Preview.setText(resourceMap.getString("jlbAnalyzerStep2Preview.text")); // NOI18N
                jlbAnalyzerStep2Preview.setName("jlbAnalyzerStep2Preview"); // NOI18N

                jtfAnalyzerStep2PreviewDescription.setText(resourceMap.getString("jtfAnalyzerStep2PreviewDescription.text")); // NOI18N
                jtfAnalyzerStep2PreviewDescription.setName("jtfAnalyzerStep2PreviewDescription"); // NOI18N

                org.jdesktop.layout.GroupLayout jpAnalyzerStep2Layout = new org.jdesktop.layout.GroupLayout(jpAnalyzerStep2);
                jpAnalyzerStep2.setLayout(jpAnalyzerStep2Layout);
                jpAnalyzerStep2Layout.setHorizontalGroup(
                        jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep2Layout.createSequentialGroup()
                                .add(jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jpAnalyzerStep2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jbtAnalyzerStep2TopAddComponent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jbtAnalyzerStep2RemoveLast, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jpAnalyzerStep2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jlbAnalyzerStep2Url)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jpAnalyzerStep2Layout.createSequentialGroup()
                                                                .add(jtfAnalyzerStep2Url, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jlbAnalyzerStep2Preview))
                                                        .add(jtfAnalyzerStep2PreviewDescription)))
                                        .add(jpAnalyzerStep2Layout.createSequentialGroup()
                                                .add(12, 12, 12)
                                                .add(jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jtaAnalyzerStep2TopDescription)
                                                        .add(jspAnalyzerStep2Top, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))))
                                .addContainerGap())
                );
                jpAnalyzerStep2Layout.setVerticalGroup(
                        jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep2Layout.createSequentialGroup()
                                .add(jtaAnalyzerStep2TopDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jspAnalyzerStep2Top, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jbtAnalyzerStep2TopAddComponent)
                                        .add(jbtAnalyzerStep2RemoveLast))
                                .add(8, 8, 8)
                                .add(jpAnalyzerStep2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep2Url, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep2Preview)
                                        .add(jlbAnalyzerStep2Url))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtfAnalyzerStep2PreviewDescription)
                                .addContainerGap())
                );

                jpAnalyzerStep3.setName("jpAnalyzerStep3"); // NOI18N

                jlbAnalyzerStep3ProductName.setFont(resourceMap.getFont("jlbAnalyzerStep3ProductName.font")); // NOI18N
                jlbAnalyzerStep3ProductName.setText(resourceMap.getString("jlbAnalyzerStep3ProductName.text")); // NOI18N
                jlbAnalyzerStep3ProductName.setName("jlbAnalyzerStep3ProductName"); // NOI18N

                jtfAnalyzerStep3ProductName.setText(resourceMap.getString("jtfAnalyzerStep3ProductName.text")); // NOI18N
                jtfAnalyzerStep3ProductName.setName("jtfAnalyzerStep3ProductName"); // NOI18N

                jlbAnalyzerStep3ProductPrice.setFont(resourceMap.getFont("jlbAnalyzerStep3ProductPrice.font")); // NOI18N
                jlbAnalyzerStep3ProductPrice.setText(resourceMap.getString("jlbAnalyzerStep3ProductPrice.text")); // NOI18N
                jlbAnalyzerStep3ProductPrice.setName("jlbAnalyzerStep3ProductPrice"); // NOI18N

                jtfAnalyzerStep3ProductPrice.setText(resourceMap.getString("jtfAnalyzerStep3ProductPrice.text")); // NOI18N
                jtfAnalyzerStep3ProductPrice.setName("jtfAnalyzerStep3ProductPrice"); // NOI18N

                jtfAnalyzerStep3ProductDescription.setText(resourceMap.getString("jtfAnalyzerStep3ProductDescription.text")); // NOI18N
                jtfAnalyzerStep3ProductDescription.setName("jtfAnalyzerStep3ProductDescription"); // NOI18N

                jlbAnalyzerStep3ProductDescription.setText(resourceMap.getString("jlbAnalyzerStep3ProductDescription.text")); // NOI18N
                jlbAnalyzerStep3ProductDescription.setName("jlbAnalyzerStep3ProductDescription"); // NOI18N

                jlbAnalyzerStep3ProductParameterName.setText(resourceMap.getString("jlbAnalyzerStep3ProductParameterName.text")); // NOI18N
                jlbAnalyzerStep3ProductParameterName.setName("jlbAnalyzerStep3ProductParameterName"); // NOI18N

                jtfAnalyzerStep3ProductParameterName.setText(resourceMap.getString("jtfAnalyzerStep3ProductParameterName.text")); // NOI18N
                jtfAnalyzerStep3ProductParameterName.setName("jtfAnalyzerStep3ProductParameterName"); // NOI18N

                jlbAnalyzerStep3Url.setText(resourceMap.getString("jlbAnalyzerStep3Url.text")); // NOI18N
                jlbAnalyzerStep3Url.setName("jlbAnalyzerStep3Url"); // NOI18N

                jtfAnalyzerStep3PreviewDescription.setText(resourceMap.getString("jtfAnalyzerStep3PreviewDescription.text")); // NOI18N
                jtfAnalyzerStep3PreviewDescription.setName("jtfAnalyzerStep3PreviewDescription"); // NOI18N

                jtfAnalyzerStep3Url.setText(resourceMap.getString("jtfAnalyzerStep3Url.text")); // NOI18N
                jtfAnalyzerStep3Url.setName("jtfAnalyzerStep3Url"); // NOI18N

                jtfAnalyzerStep3Preview.setAction(actionMap.get("previewProductExtracting")); // NOI18N
                jtfAnalyzerStep3Preview.setText(resourceMap.getString("jtfAnalyzerStep3Preview.text")); // NOI18N
                jtfAnalyzerStep3Preview.setName("jtfAnalyzerStep3Preview"); // NOI18N

                jlbAnalyzerStep3ProductParameterValue.setText(resourceMap.getString("jlbAnalyzerStep3ProductParameterValue.text")); // NOI18N
                jlbAnalyzerStep3ProductParameterValue.setName("jlbAnalyzerStep3ProductParameterValue"); // NOI18N

                jtfAnalyzerStep3ProductParameterValue.setText(resourceMap.getString("jtfAnalyzerStep3ProductParameterValue.text")); // NOI18N
                jtfAnalyzerStep3ProductParameterValue.setName("jtfAnalyzerStep3ProductParameterValue"); // NOI18N

                jlbAnalyzerStep3ProductPriceDPH.setText(resourceMap.getString("jlbAnalyzerStep3ProductPriceDPH.text")); // NOI18N
                jlbAnalyzerStep3ProductPriceDPH.setName("jlbAnalyzerStep3ProductPriceDPH"); // NOI18N

                jtfAnalyzerStep3ProductPriceDPH.setText(resourceMap.getString("jtfAnalyzerStep3ProductPriceDPH.text")); // NOI18N
                jtfAnalyzerStep3ProductPriceDPH.setName("jtfAnalyzerStep3ProductPriceDPH"); // NOI18N

                jtaAnalyzerStep3Description.setBackground(resourceMap.getColor("jtaAnalyzerStep3Description.background")); // NOI18N
                jtaAnalyzerStep3Description.setContentType(resourceMap.getString("jtaAnalyzerStep3Description.contentType")); // NOI18N
                jtaAnalyzerStep3Description.setText(resourceMap.getString("jtaAnalyzerStep3Description.text")); // NOI18N
                jtaAnalyzerStep3Description.setName("jtaAnalyzerStep3Description"); // NOI18N

                jlbAnalyzerStep3Preview.setText(resourceMap.getString("jlbAnalyzerStep3Preview.text")); // NOI18N
                jlbAnalyzerStep3Preview.setName("jlbAnalyzerStep3Preview"); // NOI18N

                jlbAnalyzerStep3ProductType.setText(resourceMap.getString("jlbAnalyzerStep3ProductType.text")); // NOI18N
                jlbAnalyzerStep3ProductType.setName("jlbAnalyzerStep3ProductType"); // NOI18N

                jtfAnalyzerStep3ProductType.setText(resourceMap.getString("jtfAnalyzerStep3ProductType.text")); // NOI18N
                jtfAnalyzerStep3ProductType.setName("jtfAnalyzerStep3ProductType"); // NOI18N

                jlbAnalyzerStep3ProductProducer.setText(resourceMap.getString("jlbAnalyzerStep3ProductProducer.text")); // NOI18N
                jlbAnalyzerStep3ProductProducer.setName("jlbAnalyzerStep3ProductProducer"); // NOI18N

                jtfAnalyzerStep3ProductProducer.setText(resourceMap.getString("jtfAnalyzerStep3ProductProducer.text")); // NOI18N
                jtfAnalyzerStep3ProductProducer.setName("jtfAnalyzerStep3ProductProducer"); // NOI18N

                jspAnalyzerStep3Preview.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jspAnalyzerStep3Preview.setName("jspAnalyzerStep3Preview"); // NOI18N

                jepAnalyzerStep3Preview.setContentType(resourceMap.getString("jepAnalyzerStep3Preview.contentType")); // NOI18N
                jepAnalyzerStep3Preview.setName("jepAnalyzerStep3Preview"); // NOI18N
                jspAnalyzerStep3Preview.setViewportView(jepAnalyzerStep3Preview);

                org.jdesktop.layout.GroupLayout jpAnalyzerStep3Layout = new org.jdesktop.layout.GroupLayout(jpAnalyzerStep3);
                jpAnalyzerStep3.setLayout(jpAnalyzerStep3Layout);
                jpAnalyzerStep3Layout.setHorizontalGroup(
                        jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jtaAnalyzerStep3Description, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
                                                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                                        .add(jlbAnalyzerStep3ProductProducer)
                                                                        .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                                .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                        .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                                                                .add(jlbAnalyzerStep3ProductPrice)
                                                                                                .add(jlbAnalyzerStep3ProductName)
                                                                                                .add(jlbAnalyzerStep3ProductPriceDPH)
                                                                                                .add(jlbAnalyzerStep3ProductParameterValue)))
                                                                                .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                                                        .add(20, 20, 20)
                                                                                        .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                                                                .add(jlbAnalyzerStep3ProductParameterName)
                                                                                                .add(jlbAnalyzerStep3ProductDescription)
                                                                                                .add(jlbAnalyzerStep3ProductType)))))
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                        .add(jtfAnalyzerStep3ProductParameterValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductParameterName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jtfAnalyzerStep3ProductDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductPrice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductPriceDPH, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                                                                        .add(jtfAnalyzerStep3ProductProducer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)))
                                                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                                .add(jlbAnalyzerStep3Url)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jtfAnalyzerStep3Url, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(jtfAnalyzerStep3Preview))))
                                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                .add(100, 100, 100)
                                                .add(jlbAnalyzerStep3Preview)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jspAnalyzerStep3Preview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE))
                                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jtfAnalyzerStep3PreviewDescription)))
                                .addContainerGap())
                );
                jpAnalyzerStep3Layout.setVerticalGroup(
                        jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jpAnalyzerStep3Layout.createSequentialGroup()
                                .add(jtaAnalyzerStep3Description, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductName))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbAnalyzerStep3ProductPrice)
                                        .add(jtfAnalyzerStep3ProductPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jlbAnalyzerStep3ProductPriceDPH)
                                        .add(jtfAnalyzerStep3ProductPriceDPH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductDescription))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductParameterName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductParameterName))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductParameterValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductParameterValue))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductType))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jtfAnalyzerStep3ProductProducer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(jlbAnalyzerStep3ProductProducer))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jspAnalyzerStep3Preview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .add(jlbAnalyzerStep3Preview))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jpAnalyzerStep3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jlbAnalyzerStep3Url)
                                        .add(jtfAnalyzerStep3Preview)
                                        .add(jtfAnalyzerStep3Url, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtfAnalyzerStep3PreviewDescription)
                                .addContainerGap())
                );

                jpAnalyzerStep4.setName("jpAnalyzerStep4"); // NOI18N

                org.jdesktop.layout.GroupLayout jpAnalyzerStep4Layout = new org.jdesktop.layout.GroupLayout(jpAnalyzerStep4);
                jpAnalyzerStep4.setLayout(jpAnalyzerStep4Layout);
                jpAnalyzerStep4Layout.setHorizontalGroup(
                        jpAnalyzerStep4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 405, Short.MAX_VALUE)
                );
                jpAnalyzerStep4Layout.setVerticalGroup(
                        jpAnalyzerStep4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(0, 254, Short.MAX_VALUE)
                );

                setComponent(jpMainPanel);
                setMenuBar(jmbMenuBar);
                setStatusBar(jpStatusPanel);
        }// </editor-fold>//GEN-END:initComponents

	/**
	 * Returns AppView singleton
	 *
	 * @return AppView
	 */
	public static synchronized AppView getInstance() {
		if (appView == null) {
			appView = new AppView(App.getApplication());
		}
		return appView;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * handles mouse click event
	 *
	 * @param evt
	 */
	private void jlbClearFlashMessagesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlbClearFlashMessagesMouseClicked
		clearFlashMessages();
	}//GEN-LAST:event_jlbClearFlashMessagesMouseClicked

	private void jtfCrawlerAddUrlKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfCrawlerAddUrlKeyReleased
		// TODO add your handling code here:
	}//GEN-LAST:event_jtfCrawlerAddUrlKeyReleased

	private void jcbAnalyzerStep1SampleConditionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAnalyzerStep1SampleConditionsActionPerformed
		jpAnalyzerStep2TopMain.removeAll();
		analyzerStep2Components.clear();
		analyzerLoadConditionsLocked = false;
	}//GEN-LAST:event_jcbAnalyzerStep1SampleConditionsActionPerformed

	private void jcbAnalyzerStep1DomainsToAnalyzeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcbAnalyzerStep1DomainsToAnalyzeMousePressed
		initAnalyzerDomainsToAnalyze();
	}//GEN-LAST:event_jcbAnalyzerStep1DomainsToAnalyzeMousePressed
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton jButton1;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JTextArea jTextArea1;
        private javax.swing.JButton jbtAnalyzerStep2RemoveLast;
        private javax.swing.JButton jbtAnalyzerStep2TopAddComponent;
        private javax.swing.JButton jbtAnalyzerStepNext;
        private javax.swing.JButton jbtAnalyzerStepPrev;
        private javax.swing.JButton jbtCrawlerAddUrl;
        private javax.swing.JButton jbtCrawlerAddUrlsFromFile;
        private javax.swing.JButton jbtCrawlerRemoveUrls;
        private javax.swing.JButton jbtCrawlerRun;
        private javax.swing.JButton jbtShowAddCrawlerUrlDialog;
        private javax.swing.JComboBox jcbAnalyzerStep1DomainsToAnalyze;
        private javax.swing.JComboBox jcbAnalyzerStep1SampleConditions;
        private javax.swing.JDialog jdCrawlerAddUrl;
        private javax.swing.JEditorPane jepAnalyzerStep3Preview;
        private javax.swing.JFileChooser jfchCrawlerUrlsChooser;
        private javax.swing.JLabel jlbAnalyzerStep;
        private javax.swing.JLabel jlbAnalyzerStep1Sample;
        private javax.swing.JButton jlbAnalyzerStep2Preview;
        private javax.swing.JLabel jlbAnalyzerStep2Url;
        private javax.swing.JLabel jlbAnalyzerStep3Preview;
        private javax.swing.JLabel jlbAnalyzerStep3ProductDescription;
        private javax.swing.JLabel jlbAnalyzerStep3ProductName;
        private javax.swing.JLabel jlbAnalyzerStep3ProductParameterName;
        private javax.swing.JLabel jlbAnalyzerStep3ProductParameterValue;
        private javax.swing.JLabel jlbAnalyzerStep3ProductPrice;
        private javax.swing.JLabel jlbAnalyzerStep3ProductPriceDPH;
        private javax.swing.JLabel jlbAnalyzerStep3ProductProducer;
        private javax.swing.JLabel jlbAnalyzerStep3ProductType;
        private javax.swing.JLabel jlbAnalyzerStep3Url;
        private javax.swing.JLabel jlbClearFlashMessages;
        private javax.swing.JLabel jlbCrawlerAddUrlDialog;
        private javax.swing.JLabel jlbSettingsDatabase;
        private javax.swing.JLabel jlbSettingsHost;
        private javax.swing.JLabel jlbSettingsPassword;
        private javax.swing.JLabel jlbSettingsPort;
        private javax.swing.JLabel jlbSettingsUsername;
        private javax.swing.JList jlstCrawlerQueue;
        private javax.swing.JMenuBar jmbMenuBar;
        private javax.swing.JPanel jpAnalyzer;
        private javax.swing.JPanel jpAnalyzerNavigation;
        private javax.swing.JPanel jpAnalyzerStep1;
        private javax.swing.JPanel jpAnalyzerStep2;
        private javax.swing.JPanel jpAnalyzerStep2TopMain;
        private javax.swing.JPanel jpAnalyzerStep3;
        private javax.swing.JPanel jpAnalyzerStep4;
        private javax.swing.JPanel jpCrawler;
        private javax.swing.JPanel jpCrawlerBodyBottom;
        private javax.swing.JPanel jpCrawlerBodyLeft;
        private javax.swing.JPanel jpCrawlerBodyRight;
        private javax.swing.JPanel jpFlashMessages;
        private javax.swing.JPanel jpHeaderPanel;
        private javax.swing.JPanel jpMainPanel;
        private javax.swing.JPanel jpSettings;
        private javax.swing.JPanel jpStatusPanel;
        private javax.swing.JPasswordField jpfSettingsPassword;
        private javax.swing.JScrollPane jspAnalyzerStep2Top;
        private javax.swing.JScrollPane jspAnalyzerStep3Preview;
        private javax.swing.JScrollPane jspCrawlerBodyLeft;
        private javax.swing.JScrollPane jspCrawlerConsole;
        private javax.swing.JSplitPane jsplpCrawlerConsoleSplitPane;
        private javax.swing.JTextArea jtaAnalyzerStep2TopDescription;
        private javax.swing.JEditorPane jtaAnalyzerStep3Description;
        private javax.swing.JTextArea jtaCrawlerConsole;
        private javax.swing.JTextArea jtaCrawlerFlashMessages;
        private javax.swing.JLabel jtfAnalyzerStep2PreviewDescription;
        private javax.swing.JTextField jtfAnalyzerStep2Url;
        private javax.swing.JButton jtfAnalyzerStep3Preview;
        private javax.swing.JLabel jtfAnalyzerStep3PreviewDescription;
        private javax.swing.JTextField jtfAnalyzerStep3ProductDescription;
        private javax.swing.JTextField jtfAnalyzerStep3ProductName;
        private javax.swing.JTextField jtfAnalyzerStep3ProductParameterName;
        private javax.swing.JTextField jtfAnalyzerStep3ProductParameterValue;
        private javax.swing.JTextField jtfAnalyzerStep3ProductPrice;
        private javax.swing.JTextField jtfAnalyzerStep3ProductPriceDPH;
        private javax.swing.JTextField jtfAnalyzerStep3ProductProducer;
        private javax.swing.JTextField jtfAnalyzerStep3ProductType;
        private javax.swing.JTextField jtfAnalyzerStep3Url;
        private javax.swing.JTextField jtfCrawlerAddUrl;
        private javax.swing.JTextField jtfSettingsDatabase;
        private javax.swing.JTextField jtfSettingsHost;
        private javax.swing.JTextField jtfSettingsPort;
        private javax.swing.JTextField jtfSettingsUsername;
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
	private DefaultListModel crawlerQueueModel;
	private List<JLabel> flashMessagesList;
	private static AppView appView;
	private int actualAnalyzerStep = 1;
	private int analyzerStepsCount = 3;
	private JPanel actualAnalyzerStepPanel;
	private int analyzerStep2countOfComponents = 0;
	private List<JReflectorBox> analyzerStep2Components;
	private Methods analyzerMethods;
	private String analyzerComponents;
	private boolean analyzerLoadConditionsLocked = false;
	private Properties settingProperties;
}
