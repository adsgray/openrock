

package net.sf.openrock.model;


public interface StateControllerIF
{
	/* these two functions allow the GUI to disable the buttons
	   if action is not possible */
	public boolean isUndoPossible();
	public boolean isRedoPossible();

	public void saveState();
	public void destroyRedoState(); // called when a shot is begun

	public void Undo();
	public void Redo();
}
