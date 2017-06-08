package cadelac.framework.blade.plug;

import cadelac.framework.blade.core.component.Component;
import cadelac.lib.primitive.plug.InputPlug;
import cadelac.lib.primitive.plug.PlugBase;

public class OutputPlugBase extends PlugBase implements OutputPlug {

	public OutputPlugBase(final String id_, final Component owner_) {
		super(id_);
		_owner = owner_;
	}

	@Override
	public InputPlug getInputPlug() {
		return _inputPlug;
	}

	@Override
	public void setInputPlug(InputPlug inputPlug_) {
		_inputPlug = inputPlug_;
	}
	
	@Override
	public Component getOwner() {
		return _owner;
	}
	
	private InputPlug _inputPlug;
	private final Component _owner;
}
