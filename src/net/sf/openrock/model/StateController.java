
package net.sf.openrock.model;

import net.sf.openrock.game.MatchController;
import net.sf.openrock.game.WorldController;
import net.sf.openrock.ui.UIProvider;
import java.util.Stack;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StateController
{
	public static StateControllerIF createNullStateController()
	{
		return new NullStateController();
	}

	public static StateControllerIF createRealStateController(
				MatchController mctl, WorldController wctl,
				UIProvider ui)
	{
		return new RealStateController(mctl, wctl, ui);
	}

	public static final Logger logger = Logger.getLogger(StateController.class.getName());

}

// used for network games -- no undo/redo allowed
class NullStateController implements StateControllerIF
{
	public boolean isUndoPossible() { return false; }
	public boolean isRedoPossible() { return false; }

	public void saveState() {}
	public void destroyRedoState() {}

	public void Undo() {}
	public void Redo() {}
}

class StateBlob
{
	public Object worldstate;
	public Object matchstate;
}

class RealStateController implements StateControllerIF
{
	private WorldController wctl;
	private MatchController mctl;
	private UIProvider ui;
	private static final Logger logger = StateController.logger;


	// how to limit size?
	private Stack <Object> undostack = new Stack <Object>();
	private Stack <Object> redostack = new Stack <Object>();

	RealStateController(MatchController mctl,
			    WorldController wctl,
			    UIProvider ui)
	{
		this.wctl = wctl;
		this.mctl = mctl;
		this.ui   = ui;
		logger.info("constructor");
	}

	private StateBlob currentState()
	{
		StateBlob state = new StateBlob();
		state.worldstate = wctl.getWorldState();
		state.matchstate = mctl.getMatchState();

		logger.info("currentState()");
		return state;
	}

	private void restoreState(StateBlob state)
	{
		wctl.restoreWorldState(state.worldstate);
		mctl.restoreMatchState(state.matchstate);
		logger.info("restoreState()");
	}


	/* these two functions allow the GUI to disable the buttons
	   if action is not possible */
	public boolean isUndoPossible()
	{
		boolean poss = !undostack.empty();
		logger.info("isUndoPossible: " + poss);
		return !undostack.empty();
	}

	public boolean isRedoPossible()
	{
		boolean poss = !redostack.empty();
		logger.info("isRedoPossible: " + poss);
		return !redostack.empty();
	}

	public void saveState()
	{
		logger.info("saveState()");
		undostack.push(currentState());
	}

	public void destroyRedoState()
	{
		logger.info("destroyRedoState()");
		redostack.clear();
	}

	public void Undo()
	{
		/*
		* pushes current state onto redo stack
		* pops state off of undo stack and restores it
		  to world and match
		  */

		logger.info("Undo()");
		redostack.push(currentState());
		restoreState((StateBlob)undostack.pop());
		ui.enableStateButtons();
	}

	public void Redo()
	{
	/* 

	  for redo:
	  	* push current state onto undo stack
	  	* pop state off of redo stack and restore it to
		  world and match
	 */

		logger.info("Redo()");
		undostack.push(currentState());
		restoreState((StateBlob)redostack.pop());
		ui.enableStateButtons();
	}
}
