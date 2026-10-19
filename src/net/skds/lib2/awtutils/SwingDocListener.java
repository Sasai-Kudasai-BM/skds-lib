package net.skds.lib2.awtutils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.util.function.Consumer;

public class SwingDocListener implements DocumentListener {
	public SwingDocListener(Consumer<DocumentEvent> action) {
		this.action = action;
	}

	private Consumer<DocumentEvent> action;

	public static SwingDocListener addListener(JTextComponent component, Consumer<DocumentEvent> action) {
		SwingDocListener listener = new SwingDocListener(action);
		component.getDocument().addDocumentListener(listener);
		return listener;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (action != null) {
			action.accept(e);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (action != null) {
			action.accept(e);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (action != null) {
			action.accept(e);
		}
	}

	public void setAction(final Consumer<DocumentEvent> action) {
		this.action = action;
	}
}
