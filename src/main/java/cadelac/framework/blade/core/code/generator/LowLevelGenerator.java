package cadelac.framework.blade.core.code.generator;

import java.util.function.Consumer;

public abstract class LowLevelGenerator {

	public abstract String generateSourceCode(final Class<?> protoClass_);

	protected LowLevelGenerator() {
		_code = new StringBuilder();
	}

 	protected String getCode() {
		return _code.toString();
	}
	
	protected LowLevelGenerator addPackage(final Class<?> protoClass_) {
		_code.append(String.format(
				"package %s;\n"
				, protoClass_.getPackage().getName()));
		return this;
	}
	
	protected LowLevelGenerator addLinebreak(final int count_) {
		for (int i=0; i<count_; ++i)
			_code.append("\n");
		return this;
	}
	
	protected LowLevelGenerator addImport(final Class<?> importClass_) {
		addImport(String.format(
				"%s.%s"
				, importClass_.getPackage().getName()
				, importClass_.getSimpleName()));
		return this;
	}
	
	protected LowLevelGenerator addImport(final String importString_) {
		_code.append(String.format(
				"import %s;\n"
				, importString_));
		return this;
	}
	
	protected LowLevelGenerator addCode(final Consumer<StringBuilder> consumer_) {
		consumer_.accept(_code);
		return this;
	}
	
	protected final StringBuilder _code;
}
