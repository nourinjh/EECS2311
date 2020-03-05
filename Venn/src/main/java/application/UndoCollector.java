package application;

import java.util.ArrayDeque;
import java.util.Deque;

import application.Controller.Undoable;

public class UndoCollector {
	/** The default undo/redo collector. */
	public static final UndoCollector INSTANCE = new UndoCollector();

	/** Contains the undoable objects. */
	private static Deque<Undoable> undo;

	/** Contains the redoable objects. */
	private static Deque<Undoable> redo;

	/** The maximal number of undo. */
	private int sizeMax;

	private UndoCollector() {
		super();
		undo = new ArrayDeque<>();
		redo = new ArrayDeque<>();
		sizeMax = 30;
	}

	/**
	 * Adds an undoable object to the collector.
	 * 
	 * @param undoable The undoable object to add.
	 */
	public void add(final Undoable undoable) {
		if (undoable != null && sizeMax > 0) {
			if (undo.size() == sizeMax) {
				undo.removeLast();
			}

			undo.push(undoable);
			redo.clear(); /* The redoable objects must be removed. */
		}
	}

	/**
	 * Undoes the last undoable object.
	 */
	public static void undo() {
		if (!undo.isEmpty()) {
			final Undoable undoable = undo.pop();
			undoable.undo();
			redo.push(undoable);
		}
	}

	/**
	 * Redoes the last undoable object.
	 */
	public static void redo() {
		if (!redo.isEmpty()) {
			final Undoable undoable = redo.pop();
			undoable.redo();
			undo.push(undoable);
		}
	}

	
	/**
	 * @return The last undoable object or null.
	 */
	public static Undoable getLastUndo() {
		return undo.isEmpty() ? null : undo.peek();
	}

	/**
	 * @return The last redoable object or null.
	 */
	public Undoable getLastRedo() {
		return redo.isEmpty() ? null : redo.peek();
	}

	/**
	 * @param max The max number of saved undoable objects. Must be great than 0.
	 */
	public void setSizeMax(final int max) {
		if (max >= 0) {
			for (int i = 0, nb = undo.size() - max; i < nb; i++) {
				undo.removeLast();
			}
			this.sizeMax = max;
		}
	}
}
