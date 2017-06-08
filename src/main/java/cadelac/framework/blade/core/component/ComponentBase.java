package cadelac.framework.blade.core.component;

import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.pack.Pack;

public class ComponentBase implements Component {

	public ComponentBase(final String id_, final Shell shell_) {
		_id = id_;
		_shell = shell_;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public Shell getShell() {
		return _shell;
	}

	@Override
	public Rack getRack() {
		return _shell.getRack();
	}
	
	@Override
	public Pack getRootPack() {
		return _shell.getRack().getRootPack();
	}

	
	private final String _id;
	private final Shell _shell;
}
