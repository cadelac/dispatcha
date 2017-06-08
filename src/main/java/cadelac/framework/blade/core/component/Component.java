package cadelac.framework.blade.core.component;

import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.pack.Pack;
import cadelac.lib.primitive.concept.Identified;

public interface Component extends Identified {
	public Shell getShell();
	public Pack getRootPack();
	public Rack getRack();
}
