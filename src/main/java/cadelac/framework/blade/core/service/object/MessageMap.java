package cadelac.framework.blade.core.service.object;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;

public class MessageMap {

	public static Class<? extends Message> getMatchingConcreteClass(final Class<? extends Message> protoType_) 
			throws Exception {
		final Shell shell = Framework.getShell();
		final Rack rack = shell.getRack();
		final Prototype2ConcreteMap p2c = rack.getPrototype2ConcreteMap();
		Class<? extends Message> concreteClass = p2c.get(protoType_);
		if (concreteClass == null) {
			// class not yet registered,
			shell.registerClass(protoType_); //register now
			concreteClass = p2c.get(protoType_); // try again
		}
		return concreteClass;
	}

}
