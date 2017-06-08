package cadelac.framework.blade.code;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.ColumnName;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.JsonMessageException;
import cadelac.lib.primitive.util.Utilities;

public class CodeGenHelper {

	// original...
	public static String generateSourceCode1(final Class<?> aClass_) {
		Map<String,String> attributes = new HashMap<String,String>(); // map from attribute-name to type
		Map<String,String> imports = new HashMap<String,String>();

		StringBuilder importDecl = new StringBuilder();
		StringBuilder mutators = new StringBuilder();
		StringBuilder dataMembers = new StringBuilder();
		
		for (Method method : aClass_.getMethods()) {
			final String methodName = method.getName();
			final String fieldName = methodName.substring(3);

			if (methodName.startsWith("get")) {
				Class<?> returnType = method.getReturnType();
				String returnTypeStr = returnType.getName();
				
				if (returnType.isArray()) {
					Class<?> componentType = returnType.getComponentType();
					if (!componentType.isPrimitive() && !imports.containsKey(componentType.getName() /*+"[]"*/ )) {
						imports.put(componentType.getName() /*+"[]"*/, "dontcare");
						importDecl.append("import " + componentType.getName() + ";\n");
					}
					returnTypeStr = componentType.getName()+"[]";
				}
				else if (!returnType.isPrimitive()) {
					if (!imports.containsKey(returnTypeStr)) {
						imports.put(returnTypeStr, "dontcare");
						importDecl.append("import " + returnTypeStr + ";\n");
					}
				}

				mutators.append(
						"    @Override\n" +
						"    public " + returnTypeStr + " " + methodName + "() {\n" +
						"        return " + fieldName + ";\n" +
						"    }\n\n");

				if (!attributes.containsKey(fieldName))
					attributes.put(fieldName, returnTypeStr);
			}
			else if (methodName.startsWith("set")) {
				Class<?>[] args = method.getParameterTypes();
				String typeStr = null;
				if (args[0].isArray()) {
					typeStr = args[0].getComponentType().getName()+"[]";
				}
				else {
					typeStr = args[0].getName();					
				}
				Annotation[] annotations = method.getAnnotations();
				if (annotations.length > 0) {
					// we support only @ColumnName
					final ColumnName columnName = (ColumnName) annotations[0];
					mutators.append(String.format("    @ColumnName(\"%s\")\n", columnName.value()));
				}
				mutators.append(
						"    @Override\n" +
						"    public void " + methodName + "(" + typeStr + " " + fieldName + ") {\n" +
						"        this." + fieldName + " = " + fieldName + ";\n" +
						"    }\n\n");
			}
		}

		// declare data members
		for (String key : attributes.keySet()) {
			dataMembers.append("    private " + attributes.get(key) + " " + key + ";\n");
		}
		
		Package pkg = aClass_.getPackage();
		final String packageName = pkg.getName(); // name of package
		final String protoClassName = aClass_.getSimpleName(); // name of interface
		final String concreteClassName = protoClassName + "Simple"; // name of concreteClass

		final StringBuilder body = new StringBuilder();
		body.append(
				"package " + packageName + ";\n\n"
				+ "import " + Field.class.getPackage().getName()              + "." + Field.class.getSimpleName()              + ";\n"
				+ "import " + Framework.class.getPackage().getName()          + "." + Framework.class.getSimpleName()          + ";\n"
				+ "import " + FrameworkException.class.getPackage().getName() + "." + FrameworkException.class.getSimpleName() + ";\n"
				+ "import " + Message.class.getPackage().getName()            + "." + Message.class.getSimpleName()            + ";\n"
				+ "import " + Marshallable.class.getPackage().getName()       + "." + Marshallable.class.getSimpleName()       + ";\n"
				
				+ "import " + ColumnName.class.getPackage().getName()         + "." + ColumnName.class.getSimpleName()         + ";\n"
				
				+ importDecl.toString() + 
				"\npublic class " + concreteClassName + " extends " + Marshallable.class.getSimpleName() + " implements " + protoClassName + " {\n\n" +		
				mutators.toString() +
				"    protected Object getField(final Field field_) throws IllegalArgumentException, IllegalAccessException { return field_.get(this); }\n" +
				"    protected void setField(final Field field_, final Object msg_) throws IllegalAccessException { field_.set(this, msg_); }\n\n" +
				dataMembers.toString() +
				"}\n");

		return body.toString();
	}

	public static String generateSourceCode(final Class<?> protoClass_) {
		// choose...
		return generateSourceCode2(protoClass_);
	}
	
	

	

	
	private static class SymbolTable {
		
		public SymbolTable() {
			imports = new HashMap<String,String>();
			symbols = new HashMap<String,SymbolEntry>();
		}

		public Set<String> getImports() {
			return imports.keySet();
		}
		
		public Collection<SymbolEntry> getSymbols() {
			return symbols.values();
		}

		public void populate(final Class<?> protoClass_) {
			for (Method method : protoClass_.getMethods()) {
				final String methodName = method.getName();
				if (methodName.startsWith("get")) {
					processGetter(method);
				}
				else if (methodName.startsWith("set")) {
					processSetter(method);
				}
			}
		}
		
		public void processGetter(final Method method) {

			final String dataMemberName = method.getName().substring(3);
			
			SymbolEntry symbolEntry = symbols.get(dataMemberName);
			if (symbolEntry == null) {
				symbolEntry = new SymbolEntry();
				symbols.put(dataMemberName, symbolEntry);
			}
			
			symbolEntry._getMethodName             = method.getName();
			symbolEntry._returnType                = method.getReturnType();
			symbolEntry._dataMemberType            = symbolEntry._returnType.getName();
			symbolEntry._dataMemberName            = dataMemberName;
			
			if (symbolEntry._returnType.isArray()) {
				final Class<?> componentType = symbolEntry._returnType.getComponentType();
				if (!componentType.isPrimitive() && !imports.containsKey(componentType.getName() )) {
					imports.put(componentType.getName(), "dontcare");
				}
				// modify, to take into account that it is an array
				symbolEntry._dataMemberType = componentType.getName()+"[]";
			}
			else if (!symbolEntry._returnType.isPrimitive()) {
				if (!imports.containsKey(symbolEntry._dataMemberType)) {
					imports.put(symbolEntry._dataMemberType, "dontcare");
				}
			}

			if (!symbols.containsKey(symbolEntry._dataMemberName))
				symbols.put(symbolEntry._dataMemberName, symbolEntry);
		}
		
		
		public void processSetter(final Method method) {
			
			final String dataMemberName = method.getName().substring(3);
			
			SymbolEntry symbolEntry = symbols.get(dataMemberName);
			if (symbolEntry == null) {
				symbolEntry = new SymbolEntry();
				symbols.put(dataMemberName, symbolEntry);
			}
			symbolEntry._setMethodName = method.getName();

			final Annotation[] annotations = method.getAnnotations();
			if (annotations.length > 0) {
				// for now, we support only @ColumnName
				final ColumnName columnName = (ColumnName) annotations[0];
				symbolEntry._annotationColumnNameValue = columnName.value();
			}
		}
		
		private static class SymbolEntry {
			String _dataMemberName;
			Class<?> _returnType;
			String _dataMemberType;
			String _getMethodName;
			String _setMethodName;
			String _annotationColumnNameValue;
		}
		
		private final Map<String,String> imports;
		// key: data-member, value: symbolEntry
		private final Map<String,SymbolEntry> symbols;
	}
	
	
	
	// new...
	public static String generateSourceCode2(final Class<?> protoClass_) {
		
		final SymbolTable symbolTable = new SymbolTable();
		symbolTable.populate(protoClass_);

		final CodeGenHelper cg = new CodeGenHelper();
		cg
		
		// package declaration
		.addPackage(protoClass_)
		.addLinebreak(1)
		
		// standard imports
		.addImport(Field.class)
		.addImport(Framework.class)
		.addImport(FrameworkException.class)
		.addImport(JsonMessageException.class)
		.addImport(Message.class)
		.addImport(Marshallable.class)
		.addLinebreak(1)
		
		// annotation
		.addImport(ColumnName.class)
		.addLinebreak(1)
		
		// add imports for types used
		.addCode(code -> {
			symbolTable.getImports().stream().forEach(
					importString -> { 
						cg.addImport(importString); 
					}
			);
		})
		.addLinebreak(1)
		
		// add start-of-class declaration
		.addCode(code -> {
			final String protoClassName = protoClass_.getSimpleName(); // name of interface
			final String concreteClassName = protoClassName + "Simple"; // name of concreteClass
			code.append("\npublic class " + concreteClassName + " extends " + Marshallable.class.getSimpleName() + " implements " + protoClassName + " {\n\n");
		})
		
		// add get/set methods
		.addCode(code -> {
			symbolTable.getSymbols().stream().forEach(entry -> {
				cg.addGetAndSetMethods(entry);
			});
		})
		
		// add protected methods
		.addCode(code -> {
			code
			.append("    protected Object getField(final Field field_) throws IllegalArgumentException, IllegalAccessException { return field_.get(this); }\n")
			.append("    protected void setField(final Field field_, final Object msg_) throws JsonMessageException, IllegalAccessException { try {field_.set(this, msg_); } catch (Exception e) { throw new JsonMessageException(String.format(\"field: %s: %s\", field_.getName(), e.getMessage())); } }\n\n");
		})
		
		// add data-members
		.addCode(code -> {
			symbolTable.getSymbols().stream().forEach(
					entry -> {
						// key: data-member, value: data-member-type
						cg.addDataMember(entry);
					}
			);
		})
		
		// add end-of-class declaration
		.addCode(code -> {
			code.append("}\n");
		})
		.addLinebreak(1)
		;
		
		//logger.info("\n***  code generator:\n" + cg.getCode() );
		return cg.getCode();
	}

	
	public static File writeSourceFile(final File sourceDir_, final File classDir_, final String className_, final String sourceCode_) throws IOException {
		final String normalizedClassName = className_.replace('.', File.separatorChar);
		final File sourceFile = new File(sourceDir_, normalizedClassName + ".java");
		final File sourceParentFile = sourceFile.getParentFile();
		if (sourceParentFile != null && !sourceParentFile.exists()) 
			sourceParentFile.mkdirs();
		if (sourceFile.exists())
			sourceFile.delete();
		
		final File classFile = new File(classDir_, normalizedClassName + ".class");
		File classParentFile = classFile.getParentFile();
		if (classParentFile != null && !classParentFile.exists())	
			classParentFile.mkdirs();
		
		Utilities.writeFile(sourceFile, sourceCode_);
		return sourceFile;
	}

	

	
	public CodeGenHelper() {
		_code = new StringBuilder();
	}
	
	public String getCode() {
		return _code.toString();
	}
	
	private CodeGenHelper addPackage(final Class<?> protoClass_) {
		_code.append(String.format(
				"package %s;\n"
				, protoClass_.getPackage().getName()));
		return this;
	}
	
	private CodeGenHelper addImport(final Class<?> importClass_) {
		_code.append(String.format(
				"import %s.%s;\n"
				, importClass_.getPackage().getName()
				, importClass_.getSimpleName()));
		return this;
	}
	
	private CodeGenHelper addImport(final String importString_) {
		_code.append(String.format(
				"import %s;\n"
				, importString_));
		return this;
	}
	
	private CodeGenHelper addCode(final Consumer<StringBuilder> consumer_) {
		consumer_.accept(_code);
		return this;
	}

	private CodeGenHelper addGetAndSetMethods(final SymbolTable.SymbolEntry symbolEntry) {
		//final SymbolTable.SymbolEntry symbolEntry = entry_.getValue();
		
		// add get method
		_code.append(
				"    @Override\n" +
				"    public " + /*symbolEntry._dataMemberType*/ symbolEntry._returnType.getSimpleName() + " " + symbolEntry._getMethodName + "() {\n" +
				"        return " + symbolEntry._dataMemberName + ";\n" +
				"    }\n");
		
		
		// add set method
		if (symbolEntry._annotationColumnNameValue != null && symbolEntry._annotationColumnNameValue.length()>0)
			_code.append(String.format("    @ColumnName(\"%s\")\n", symbolEntry._annotationColumnNameValue));
		
		_code.append(
				"    @Override\n" +
				"    public void " + symbolEntry._setMethodName + "(" + /*symbolEntry._dataMemberType*/ symbolEntry._returnType.getSimpleName() + " " + symbolEntry._dataMemberName + ") {\n" +
				"        this." + symbolEntry._dataMemberName + " = " + symbolEntry._dataMemberName + ";\n" +
				"    }\n\n");
		return this;
	}

	private CodeGenHelper addDataMember(
			final SymbolTable.SymbolEntry symbolEntry_) {
		_code.append(String.format(
				"    private %s %s;\n"
				, /*symbolEntry_._dataMemberType*/ symbolEntry_._returnType.getSimpleName()
				, symbolEntry_._dataMemberName));		
		return this;
	}
	
	private CodeGenHelper addLinebreak(final int count_) {
		for (int i=0; i<count_; ++i)
			_code.append("\n");
		return this;
	}
	
	static final Logger logger = Logger.getLogger(CodeGenHelper.class);
			
	private final StringBuilder _code;
}
