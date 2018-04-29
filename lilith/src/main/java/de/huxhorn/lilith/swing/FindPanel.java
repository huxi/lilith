/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.swing;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import de.huxhorn.lilith.conditions.GroovyCondition;
import de.huxhorn.lilith.conditions.LevelCondition;
import de.huxhorn.lilith.conditions.SearchStringCondition;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.Not;
import de.huxhorn.sulky.swing.KeyStrokes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindPanel<T extends Serializable>
	extends JPanel
{
	private static final long serialVersionUID = 4365760593809731152L;

	private final Logger logger = LoggerFactory.getLogger(FindPanel.class);

	private static final String GROOVY_IDENTIFIER = "#groovy#";
	private static final String SAVED_CONDITION_IDENTIFIER = "#condition#";
	private static final Color ERROR_COLOR = new Color(0x990000);
	private static final Color NO_ERROR_COLOR = Color.BLACK;

	static final String CONDITION_PROPERTY = "condition";

	private final MainFrame mainFrame;
	private final EventWrapperViewPanel<T> eventWrapperViewPanel;

	private final JButton closeFindButton;
	private final JToggleButton findNotButton;

	private final ApplicationPreferences applicationPreferences;

	private final JComboBox findTypeCombo;
	private final BasicEventList<String> findTypeEventList;

	private final JComboBox findTextCombo;
	private final BasicEventList<String> findTextEventList;

	private Condition condition;
	private List<String> previousSearchStrings;
	private List<String> conditionNames;

	FindPanel(EventWrapperViewPanel<T> eventWrapperViewPanel)
	{
		this.eventWrapperViewPanel = eventWrapperViewPanel;
		this.mainFrame = this.eventWrapperViewPanel.getMainFrame();
		this.applicationPreferences = mainFrame.getApplicationPreferences();
		this.previousSearchStrings = applicationPreferences.getPreviousSearchStrings();
		this.conditionNames = applicationPreferences.getConditionNames();

		CloseFindAction closeFindAction = new CloseFindAction();
		closeFindButton = new JButton(closeFindAction);
		closeFindButton.setMargin(new Insets(0, 0, 0, 0));
		GridBagConstraints gbc=new GridBagConstraints();
		setLayout(new GridBagLayout());
		gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.LINE_START; // like WEST
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(closeFindButton, gbc);

		gbc.gridx = 1;
		add(new JLabel(" Find: "), gbc);

		ActionListener findTypeModifiedListener = new FindTypeSelectionActionListener();
		findTypeCombo = new JComboBox();
		// AUTO-COMPLETION
		findTypeEventList = new BasicEventList<>();
		AutoCompleteSupport<String> findTypeComboAutoSupport = AutoCompleteSupport.install(findTypeCombo, findTypeEventList);
		findTypeComboAutoSupport.setFirstItem("");
		findTypeComboAutoSupport.setStrict(true);
		findTypeComboAutoSupport.setCorrectsCase(true);
		findTypeComboAutoSupport.setTextMatchingStrategy(TextMatcherEditor.IDENTICAL_STRATEGY);
		findTypeComboAutoSupport.setFilterMode(TextMatcherEditor.CONTAINS);
		findTypeComboAutoSupport.setBeepOnStrictViolation(false);
		findTypeCombo.addActionListener(findTypeModifiedListener);
		findNotButton = new JToggleButton("!");
		findNotButton.addActionListener(findTypeModifiedListener);
		findNotButton.setToolTipText("Not - inverts condition");
		findNotButton.setMargin(new Insets(0, 0, 0, 0));

		// AUTO-COMPLETION
		findTextCombo = new JComboBox();
		findTextEventList = new BasicEventList<>();
		AutoCompleteSupport<String> findTextComboAutoSupport = AutoCompleteSupport.install(findTextCombo, findTextEventList);
		findTextComboAutoSupport.setFirstItem("");
		findTextComboAutoSupport.setStrict(false);
		findTextComboAutoSupport.setCorrectsCase(false);
		findTextComboAutoSupport.setTextMatchingStrategy(TextMatcherEditor.NORMALIZED_STRATEGY);
		findTextComboAutoSupport.setFilterMode(TextMatcherEditor.CONTAINS);
		gbc.gridx = 2;
		gbc.fill=GridBagConstraints.VERTICAL;
		add(findNotButton, gbc);

		gbc.gridx = 3;
		gbc.fill=GridBagConstraints.VERTICAL;
		add(findTypeCombo, gbc);

		gbc.gridx = 4;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill=GridBagConstraints.BOTH;
		add(findTextCombo, gbc);

		FindTextFieldListener findTextFieldListener = new FindTextFieldListener();
		JTextComponent findEditorComponent = getFindEditorComponent();
		if(findEditorComponent instanceof JTextField)
		{
			((JTextField)findEditorComponent).addActionListener(findTextFieldListener);
		}
		else
		{
			if(logger.isWarnEnabled()) logger.warn("findEditorComponent ({}) is not instanceof JTextField!",
					findEditorComponent == null ? "null" : findEditorComponent.getClass().getName());
		}
		if(findEditorComponent != null)
		{
			findEditorComponent.getDocument().addDocumentListener(findTextFieldListener);
			findEditorComponent.setForeground(NO_ERROR_COLOR);
		}
		ReplaceFilterAction replaceFilterAction = new ReplaceFilterAction();

		KeyStrokes.registerCommand(this, closeFindAction, "CLOSE_FIND_ACTION");
		KeyStrokes.registerCommand(findTextCombo, replaceFilterAction, "REPLACE_FILTER_ACTION");

		FocusTraversalPolicy focusTraversalPolicy = new MyFocusTraversalPolicy();
		setFocusTraversalPolicy(focusTraversalPolicy);
		setFocusCycleRoot(false);
		setFocusTraversalPolicyProvider(true);
		setFocusable(true);
	}

	private void setCondition(Condition condition)
	{
		Object oldValue=getCondition();
		try
		{
			if(condition != null)
			{
				this.condition = condition.clone();
			}
			else
			{
				this.condition = null;
			}
		}
		catch (CloneNotSupportedException e)
		{
			this.condition = null;
			if(logger.isWarnEnabled()) logger.warn("Condition {} does not support cloning!", condition, e);
		}
		Object newValue=getCondition();
		firePropertyChange(CONDITION_PROPERTY, oldValue, newValue);
	}

	public Condition getCondition()
	{
		if(condition == null)
		{
			return null;
		}
		try
		{
			return condition.clone();
		}
		catch (CloneNotSupportedException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Condition {} does not support cloning!", condition, e);
		}
		return null;
	}

	private void updateCondition()
	{
		setCondition(createCondition());
	}

	private Condition createCondition()
	{
		String text=null;
		JTextComponent findEditorComponent = getFindEditorComponent();
		if(findEditorComponent != null)
		{
			text=findEditorComponent.getText();
		}

		Condition condition=null;

		String errorMessage = null;
		if(text == null)
		{
			text = "";
		}
		if(text.startsWith(GROOVY_IDENTIFIER))
		{
			String scriptName = text.substring(GROOVY_IDENTIFIER.length());

			int idx = scriptName.indexOf('#');
			if(idx > -1)
			{
				if(idx + 1 < scriptName.length())
				{
					text = scriptName.substring(idx + 1);
				}
				else
				{
					text = "";
				}
				scriptName = scriptName.substring(0, idx);
			}
			else
			{
				text = "";
			}
			if(logger.isDebugEnabled())
			{
				logger.debug("GroovyCondition with scriptName '{}' and searchString '{}'", scriptName, text);
			}
			File resolvedScriptFile = applicationPreferences.resolveGroovyConditionScriptFile(scriptName);
			if(resolvedScriptFile != null)
			{
				// there is a file...
				condition = new GroovyCondition(resolvedScriptFile.getAbsolutePath(), text);
			}
			else
			{
				errorMessage = "Couldn't find groovy script '" + scriptName + "'.";
				condition = null;
			}
		}
		else if(text.startsWith(SAVED_CONDITION_IDENTIFIER))
		{
			String conditionName = text.substring(SAVED_CONDITION_IDENTIFIER.length());
			SavedCondition savedCondition = applicationPreferences.resolveSavedCondition(conditionName);
			if(savedCondition != null)
			{
				condition = savedCondition.getCondition();
			}
			else
			{
				errorMessage = "Couldn't find saved condition named '" + conditionName + "'.";
				condition = null;
			}
		}
		else
		{
			// create condition matching the selected type
			String selectedType = (String) findTypeCombo.getSelectedItem();
			try
			{
				condition = applicationPreferences.createCondition(selectedType, text);
			}
			catch(IllegalArgumentException ex)
			{
				errorMessage = ex.getMessage();
			}
		}
		if(findEditorComponent != null)
		{
			if(errorMessage != null)
			{
				// problem with condition
				findEditorComponent.setForeground(ERROR_COLOR);
				findEditorComponent.setToolTipText(errorMessage);
			}
			else
			{
				findEditorComponent.setForeground(NO_ERROR_COLOR);
				findEditorComponent.setToolTipText(null);
			}
		}

		if(condition != null && findNotButton.isSelected())
		{
			// wrap in Not if not is selected.
			condition = new Not(condition);
		}

		return condition;
	}

	void resetFind()
	{
		JTextComponent findEditorComponent = getFindEditorComponent();
		if(findEditorComponent != null)
		{
			findEditorComponent.setText("");
		}
	}

	void updateUi()
	{
		initTypeCombo();
		// select correct type in combo
		Condition condition = eventWrapperViewPanel.getFilterCondition(); // TODO: check!
		boolean not = false;
		if(condition instanceof Not)
		{
			Not notCondition = (Not) condition;
			not = true;
			condition = notCondition.getCondition();
		}
		if(condition != null)
		{
			String conditionName = applicationPreferences.resolveConditionName(condition);

			if(conditionName != null)
			{
				findTypeCombo.setSelectedItem(conditionName);
			}
		}
		findNotButton.setSelected(not);
		updateFindCombo();
	}

	private void initTypeCombo()
	{
		List<String> itemsVector = applicationPreferences.retrieveAllConditions();

		findTypeEventList.clear();
		findTypeEventList.addAll(itemsVector);
		findTypeCombo.setSelectedItem(applicationPreferences.getDefaultConditionName());
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if(logger.isDebugEnabled()) logger.debug("Visible: {}", visible);
	}

	private JTextComponent getFindEditorComponent()
	{
		Component findComponent = findTextCombo.getEditor().getEditorComponent();
		if(findComponent instanceof JTextComponent)
		{
			return (JTextComponent) findComponent;
		}
		if(logger.isWarnEnabled()) logger.warn("findComponent ({}) is not instanceof JTextComponent!", findComponent.getClass().getName());
		return null;
	}

	void requestComboFocus()
	{
		findTextCombo.requestFocusInWindow();
		findTextCombo.getEditor().selectAll();
	}

	private void updateFindCombo()
	{
		String selectedType = (String) findTypeCombo.getSelectedItem();

		switch (selectedType) {
			case LevelCondition.DESCRIPTION:
				findTextEventList.clear();
				findTextEventList.addAll(applicationPreferences.retrieveLevelValues());
				break;
			case ApplicationPreferences.SAVED_CONDITION:
				findTextEventList.clear();
				findTextEventList.addAll(conditionNames);
				break;
			default:
				String prev = (String) findTextCombo.getSelectedItem(); // save...

				findTextEventList.clear();
				findTextEventList.addAll(previousSearchStrings);
				findTextCombo.setSelectedItem(prev); // ...and restore

				break;
		}
	}

	void setPreviousSearchStrings(List<String> previousSearchStrings)
	{
		this.previousSearchStrings=new ArrayList<>(previousSearchStrings);
		this.previousSearchStrings.add(0, ""); // always add an empty string as first
		updateFindCombo();
	}

	public void setConditionNames(List<String> conditionNames)
	{
		this.conditionNames=conditionNames;
		updateFindCombo();
	}

	class MyFocusTraversalPolicy
		extends FocusTraversalPolicy
	{
		private final Logger logger = LoggerFactory.getLogger(MyFocusTraversalPolicy.class);

		private Component resolveComponent(Component component)
		{
			Container container = component.getParent();
			//noinspection Duplicates
			while(container != null)
			{
				if(container == findTypeCombo) // NOPMD
				{
					return findTypeCombo;
				}
				if(container == findTextCombo) // NOPMD
				{
					return findTextCombo;
				}
				container = container.getParent();
			}
			return null;
		}

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(closeFindButton))
			{
				return findNotButton;
			}
			if(aComponent.equals(findNotButton))
			{
				return findTypeCombo;
			}
			if(aComponent.equals(findTypeCombo))
			{
				return findTextCombo;
			}
			if(aComponent.equals(findTextCombo))
			{
				return closeFindButton;
			}

			// not found, try to resolve it...
			Component c = resolveComponent(aComponent);
			if(findTypeCombo.equals(c))
			{
				return findTextCombo;
			}
			if(findTextCombo.equals(c))
			{
				return closeFindButton;
			}

			if(aContainer == aComponent) // NOPMD
			{
				// prevent useless warning
				return null;
			}

			if(logger.isWarnEnabled()) logger.warn("Moving focus forward was not explicitly handled.\ncontainer={}\ncomponent={}", aContainer, aComponent);

			return null;
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent)
		{
			if(aComponent.equals(closeFindButton))
			{
				return findTextCombo;
			}
			if(aComponent.equals(findNotButton))
			{
				return closeFindButton;
			}
			if(aComponent.equals(findTypeCombo))
			{
				return findNotButton;
			}
			if(aComponent.equals(findTextCombo))
			{
				return findTypeCombo;
			}

			// not found, try to resolve it...
			Component c = resolveComponent(aComponent);
			if(findTypeCombo.equals(c))
			{
				return findNotButton;
			}
			if(findTextCombo.equals(c))
			{
				return findTypeCombo;
			}

			if(aContainer == aComponent) // NOPMD
			{
				// prevent useless warning
				return null;
			}

			if(logger.isWarnEnabled()) logger.warn("Moving focus backward was not explicitly handled.\ncontainer={}\ncomponent={}", aContainer, aComponent);

			return null;
		}

		@Override
		public Component getFirstComponent(Container aContainer)
		{
			return closeFindButton;
		}

		@Override
		public Component getLastComponent(Container aContainer)
		{
			return findTextCombo;
		}

		@Override
		public Component getDefaultComponent(Container aContainer)
		{
			return findTextCombo;
		}
	}

	private class FindTextFieldListener
		implements ActionListener, DocumentListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			updateCondition();
			if(logger.isDebugEnabled()) logger.debug("modifiers: " + e.getModifiers());
			JTextComponent findEditorComponent = getFindEditorComponent();
			if(findEditorComponent != null)
			{
				findEditorComponent.selectAll();
			}
			String selectedType = (String) findTypeCombo.getSelectedItem();

			if(!LevelCondition.DESCRIPTION.equals(selectedType)
				&& !ApplicationPreferences.SAVED_CONDITION.equals(selectedType)
				&& condition instanceof SearchStringCondition)
			{
				mainFrame.getApplicationPreferences().addPreviousSearchString(((SearchStringCondition)condition).getSearchString());
			}

			eventWrapperViewPanel.createFilteredView();
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updateCondition();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updateCondition();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updateCondition();
		}
	}

	private class CloseFindAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7757686292973276423L;

		CloseFindAction()
		{
			super();
			putValue(Action.SMALL_ICON, Icons.CLOSE_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Close");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ViewContainer container = eventWrapperViewPanel.resolveContainer();
			if(container != null)
			{
				ProgressGlassPane progressPanel = container.getProgressPanel();
				progressPanel.getFindCancelAction().cancelSearch();
				setVisible(false);
			}
		}
	}

	private class ReplaceFilterAction
		extends AbstractLilithAction
	{
		private static final long serialVersionUID = -8465877386933116956L;

		ReplaceFilterAction()
		{
			super(LilithActionId.REPLACE_FILTER);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Replace filter.");
			ViewContainer<T> container = eventWrapperViewPanel.resolveContainer();
			if(container == null)
			{
				return;
			}
			Condition filter = eventWrapperViewPanel.resolveCombinedCondition();
			if (filter == null)
			{
				return;
			}

			container.replaceFilteredView(eventWrapperViewPanel, filter);
		}
	}

	private class FindTypeSelectionActionListener
		implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			updateFindCombo();

			updateCondition();
		}
	}
}
