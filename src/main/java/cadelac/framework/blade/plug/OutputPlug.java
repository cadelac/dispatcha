package cadelac.framework.blade.plug;

import cadelac.framework.blade.core.component.Component;
import cadelac.lib.primitive.plug.InputPlug;
import cadelac.lib.primitive.plug.Plug;

public interface OutputPlug extends Plug {
	/**
	 * The input plug connected to this output plug.
	 * @return
	 */
	public InputPlug getInputPlug();
	public void setInputPlug(final InputPlug inputPlug_);
	/**
	 * The handler that owns/contains this output plug.
	 * @return
	 */
	public Component getOwner();
}
