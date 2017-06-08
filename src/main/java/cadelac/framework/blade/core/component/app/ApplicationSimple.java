package cadelac.framework.blade.core.component.app;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.ComponentBase;
import cadelac.framework.blade.core.component.shell.ShellSimple;

public abstract class ApplicationSimple extends ComponentBase implements Application {

	public ApplicationSimple(final String applicationName_, final String[] args_) {
		super(applicationName_, new ShellSimple(args_));
		Framework.setShell(getShell());
		getShell().setApplication(this); // associate App and Shell
	}
	/**
	 * this method used to be mandatory but is now made optional -- 2016-10-14
	 * moved from interface 'Application' to this class. it used to be a
	 * requirement for the application to override this. but since code 
	 * generation, compilation and class loading is now on-the-fly, it is
	 * now optional.
	 * 
	 * @throws Exception
	 */
	public void registerClasses() throws Exception {}
}
