package net.w3e.lib.awtutils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import lombok.AllArgsConstructor;

public class SwingKeyListener {

	private final int keyCode;

	private final JComponent component;
	private final Object pressedKey = new Object();
	private final KeyStroke pressedKeyStroke;

	private final KeyStroke releasedKeyStroke;
	private final Object releaseddKey = new Object();

	private final KeyListener listener;

	public SwingKeyListener(Object jComponent, int keyCode, KeyListener listener) {
		this(jComponent, keyCode, 0, listener);
	}

	public SwingKeyListener(Object jComponent, int keyCode, int modifiers, KeyListener listener) {
		this(jComponent, keyCode, modifiers, modifiers, listener);
	}

	public SwingKeyListener(Object jComponent, int keyCode, int pressModifiers, int releaseModifiers, KeyListener listener) {
		this.keyCode = keyCode;
		this.pressedKeyStroke = KeyStroke.getKeyStroke(keyCode, pressModifiers, false);
		this.releasedKeyStroke = KeyStroke.getKeyStroke(keyCode, releaseModifiers, true);
		this.component = (JComponent)jComponent;
		this.listener = listener;
	}
	
	public final SwingKeyListener install() {
		this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(this.pressedKeyStroke, this.pressedKey);
		this.component.getActionMap().put(this.pressedKey, new ActionListener(true));

		this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(this.releasedKeyStroke, this.releaseddKey);
		this.component.getActionMap().put(this.releaseddKey, new ActionListener(false));

		return this;
	}

	public final SwingKeyListener uninstall() {
		this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(this.pressedKeyStroke);
		this.component.getActionMap().remove(this.pressedKey);

		this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(this.releasedKeyStroke);
		this.component.getActionMap().remove(this.releaseddKey);

		return this;
	}

	private void perform(ActionEvent e, boolean press) {
		KeyEvent event = new KeyEvent(this.component, press ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED, e.getWhen(), e.getModifiers(), this.keyCode, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_UNKNOWN);
		if (press) {
			this.listener.keyPressed(event);
		} else {
			this.listener.keyReleased(event);
		}
	}

	/*public interface KeyAction {
		void perform(boolean wasPressed, boolean released);
	}*/

	@AllArgsConstructor
	private class ActionListener extends AbstractAction {

		private final boolean press;

		@Override
		public void actionPerformed(ActionEvent e) {
			perform(e, this.press);
		}
	}

}
