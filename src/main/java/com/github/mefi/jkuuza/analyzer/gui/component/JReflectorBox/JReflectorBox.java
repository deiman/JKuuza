package com.github.mefi.jkuuza.analyzer.gui.component.JReflectorBox;

import com.github.mefi.jkuuza.analyzer.Condition;
import com.github.mefi.jkuuza.analyzer.Method;
import com.github.mefi.jkuuza.analyzer.Methods;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Component which allows to setting parameters and values to methods
 *
 * @author Marek Pilecky
 */
public class JReflectorBox extends JPanel implements ActionListener {

	private IReflectorBoxModel model;
	private ArrayList<JTextField> paramFields;
	private JComboBox jcbMethods;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel jlbExpectedValueError;
	private JLabel jlbExpectedValue;
	private JLabel jlbParameters;
	private JLabel jlbMethodDescription;
	private JTextField jtfExpectedValue;
	private int paramOrder = 0;
	private String[] defaultParametersValues = {};
	private String defaultExpectedValue = null;

	/**
	 * Create instance of component. Uses default model, another can be set via setter.
	 *
	 * @param methods instance of Methods class
	 */
	public JReflectorBox(Methods methods) {
		super();

		this.model = new DefaultReflectorBoxModel(methods);

		initComponent();

		setBasicComponentsVisibility(false);

	}

	/**
	 * Listens to the combo box.
	 * @param e event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		JComboBox cb = (JComboBox) e.getSource();

		jlbExpectedValueError.setText("");

		// remove all param textfilds from JPanel
		for (Iterator<JTextField> it = paramFields.iterator(); it.hasNext();) {
			topPanel.remove(it.next());
		}
		// remove all param textfileds from collection
		paramFields.clear();

		// clear text of description
		jlbMethodDescription.setText("");

		//get selected value from combobox
		String selectedItem = cb.getSelectedItem().toString();

		// if isn't selected first (default, empty) value
		if (!selectedItem.equals("")) {
			//display components
			setBasicComponentsVisibility(true);

			String className = selectedItem.substring(0, selectedItem.indexOf("."));
			String methodName = selectedItem.substring(selectedItem.lastIndexOf(".") + 1);
			Method method = model.getMethods().get(methodName);

			this.model.setClassName(className);
			this.model.setMethodName(methodName);

			repaintBox(method);
		} else {
			// hide components
			setBasicComponentsVisibility(false);
			jlbMethodDescription.setText("");
		}
	}

	protected void setBasicComponentsVisibility(boolean visibility) {
		jlbExpectedValue.setVisible(visibility);
		jtfExpectedValue.setVisible(visibility);
		jlbParameters.setVisible(visibility);

		repaint();
	}

	protected void repaintBox(Method method) {
		paramFields.clear();

		jtfExpectedValue.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				if (jtfExpectedValue.getText().equals(defaultExpectedValue)) {
					jtfExpectedValue.setText("");
				}
			}

			public void mouseExited(MouseEvent e) {
				if (jtfExpectedValue.getText().equals("")) {
					jtfExpectedValue.setText(defaultExpectedValue);
				}
			}
		});
		jtfExpectedValue.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				model.setExpected(jtfExpectedValue.getText());
			}
		});

		// init param order to zero
		paramOrder = 0;
		int paramsCount = method.getParameters().size();
		// temporary array to hold default values of textfileds, init size to count of parameters
		defaultParametersValues = new String[paramsCount];

		Iterator it = method.getParameters().entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry) it.next();

			// save default value to temporary variable
			defaultParametersValues[paramOrder] = pairs.getValue() + " " + pairs.getKey();
			JTextField tempTextField = new JTextField(defaultParametersValues[paramOrder]);
			tempTextField.setColumns(15);

			// handles displaying and hiding of default values when mouse over
			tempTextField.addMouseListener(new MouseListener() {

				private int order = paramOrder;

				public void mouseClicked(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
					if (paramFields.get(order).getText().equals(defaultParametersValues[order])) {
						paramFields.get(order).setText("");
					}
				}

				public void mouseExited(MouseEvent e) {
					if (paramFields.get(order).getText().equals("")) {
						paramFields.get(order).setText(defaultParametersValues[order]);
					}
				}
			});
			tempTextField.addKeyListener(new KeyListener() {

				private int count = paramOrder;

				public void keyTyped(KeyEvent e) {
				}

				public void keyPressed(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {

					if (model.getParams().size() == count) {
						model.getParams().add(paramFields.get(count).getText());
					} else {
						model.getParams().set(count, paramFields.get(count).getText());
					}
				}
			});
			// add this textFiels into collection
			paramFields.add(tempTextField);
			paramOrder++;
		}

		// add all collection into JPanel
		for (Iterator<JTextField> itx = paramFields.iterator(); itx.hasNext();) {
			topPanel.add(itx.next());
		}
		bottomPanel.add(jlbMethodDescription);

		jtfExpectedValue.setText(method.getReturnType());
		defaultExpectedValue = method.getReturnType();
		jlbMethodDescription.setText(method.getDescription());

		//getParent().repaint();
		revalidate();
	}

	/**
	 * Creates and add to panel used components
	 */
	protected void initComponent() {
		paramFields = new ArrayList<JTextField>();
		jlbParameters = new JLabel("Param:");
		jtfExpectedValue = new JTextField(10);
		jlbExpectedValue = new JLabel("Hodnota:");
		jlbExpectedValueError = new JLabel("");
		jlbExpectedValueError.setForeground(Color.red);
		jlbMethodDescription = new JLabel();
		topPanel = new JPanel(new FlowLayout());
		bottomPanel = new JPanel(new FlowLayout());

		List<Method> methodsList = model.getMethods().getList();

		//setup combobox
		jcbMethods = new JComboBox(createComboBoxArray(methodsList));
		jcbMethods.addActionListener(this);

		topPanel.add(jcbMethods); // combobox
		topPanel.add(jlbExpectedValue); // label
		topPanel.add(jlbExpectedValueError); // error label
		topPanel.add(jtfExpectedValue); // textField
		topPanel.add(jlbParameters); // label parameters
		// prameters textfields will be created in action of combobox

		add(topPanel);
		add(bottomPanel);
	}

	/**
	 * convert list to array, used by comboBox
	 *
	 * @param methodsList list with Method instances
	 * @return array where first element has empty string value
	 */
	protected String[] createComboBoxArray(List<Method> methodsList) {
		int arraySize = methodsList.size() + 1;
		String[] array = new String[arraySize];
		array[0] = "";

		for (int i = 0; i < methodsList.size(); i++) {
			Method method = methodsList.get(i);
			String className = method.getClassName().substring(method.getClassName().lastIndexOf(".") + 1);
			// +1 because of first empty element in array
			array[i + 1] = className + "." + methodsList.get(i).getName();
		}
		return array;
	}

	/**
	 * Checks, if component hasn't empty fields
	 *
	 * @return true if all fields are filled and if boolean gets "true" or "false"
	 */
	public boolean isFilled() {
		if (jcbMethods.getSelectedIndex() == 0) {
			return false;
		}

		if (defaultExpectedValue.equals("boolean")) {
			String value = jtfExpectedValue.getText().trim().toLowerCase();
			if (!value.equals("true") && !value.equals("false")) {
				jlbExpectedValueError.setText(" true/false!! ");
				return false;
			}
		}

		if (jtfExpectedValue.getText().equals(defaultExpectedValue)) {
			return false;
		}

		List list = model.getParams();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(defaultParametersValues[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get this component's model
	 *
	 * @return IReflectorBoxModel model
	 */
	public IReflectorBoxModel getModel() {
		return model;
	}

	/**
	 * Set model to different then default
	 *
	 * @param model instance of IReflectorBoxModel
	 */
	public void setModel(IReflectorBoxModel model) {
		this.model = model;
	}

	public void showValuesFromModel() {

		//Methods methods = Reflector.getDeclaredMethodsWithInfo(Class.forName(model.getClassName()));
		//Method method = methods.get(model.getMethodName());
		//repaintBox(method);
		
		//jtfExpectedValue.setText(model.getExpected());


	}

	public void setDefaultValues(Condition condition) {
		String className = condition.getConditionObject().getClass().getName();
		String selectedItem = className.substring(className.lastIndexOf(".") + 1) + "." + condition.getFunctionName();

		jcbMethods.setSelectedItem(selectedItem);
		jtfExpectedValue.setText(condition.getExpectedValue());
		model.setExpected(jtfExpectedValue.getText());
		model.setParams(condition.getParams());
		
		for (int i = 0; i < paramFields.size(); i++) {
			JTextField jTextField = paramFields.get(i);
			jTextField.setText(condition.getParams().get(i));

		}
	}
}
