package cadelac.framework.blade.core.code.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.JsonMessageException;
import cadelac.framework.blade.core.message.MarshallableBase;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.facility.db.annotation.ColumnName;
import cadelac.framework.blade.facility.db.annotation.FlattenAs;
import cadelac.framework.blade.facility.db.annotation.InflateAs;
import cadelac.framework.blade.facility.db.annotation.MarshallNo;
import cadelac.framework.blade.facility.db.annotation.TableName;

public class CodeGenerator {

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
				if (methodName.startsWith("get") || methodName.startsWith("set")) {
					// without the "get" and "set"
					final String dataMemberName = method.getName().substring(3); 
					SymbolEntry symbolEntry = symbols.get(dataMemberName);
					if (symbolEntry == null) {
						symbolEntry = new SymbolEntry();
						symbolEntry._dataMemberName = dataMemberName;
						symbols.put(dataMemberName, symbolEntry);
					}
					
					for (Annotation annotation : method.getAnnotations()) {
						// we support annotations: @ColumnName, @MarshallNo, @FlattenAs, @InflateAs
						if (annotation instanceof ColumnName) {
							final ColumnName columnName = (ColumnName) annotation;
							symbolEntry._annotationColumnNameValue = columnName.value();
						}
						else if (annotation instanceof MarshallNo) {
							symbolEntry._annotationMarshallNo = true;
						}
						else if (annotation instanceof FlattenAs) {
							final FlattenAs flatten = (FlattenAs) annotation;
							symbolEntry._flattenAs = flatten.value();
						}
						else if (annotation instanceof InflateAs) {
							final InflateAs inflate = (InflateAs) annotation;
							symbolEntry._inflateAs = inflate.value();
						}
					}
					
					if (methodName.startsWith("get")) {
						processGetter(method, symbolEntry);
					}
					else if (methodName.startsWith("set")) {
						processSetter(method, symbolEntry);
					}
				}
			}
		}
		
		private void processGetter(final Method method, SymbolEntry symbolEntry) {

			symbolEntry._getMethodName             = method.getName();
			symbolEntry._returnType                = method.getReturnType();
			symbolEntry._dataMemberType            = symbolEntry._returnType.getName();

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
		}
		
		
		private void processSetter(final Method method, SymbolEntry symbolEntry) {
			symbolEntry._setMethodName = method.getName();
		}
		
		private static class SymbolEntry {
			String _dataMemberName;
			Class<?> _returnType;
			String _dataMemberType;
			String _getMethodName;
			String _setMethodName;
			String _annotationColumnNameValue;
			boolean _annotationMarshallNo;
			String _flattenAs;
			String _inflateAs;
		}
		
		private final Map<String,String> imports;
		// key: data-member, value: symbolEntry
		private final Map<String,SymbolEntry> symbols;
	}

	public static String generateSourceCode(final Class<?> protoClass_) {
		
		final String protoClassName = protoClass_.getSimpleName(); // name of interface
		final String concreteClassName = protoClassName + "Simple"; // name of concreteClass

		final SymbolTable symbolTable = new SymbolTable();
		symbolTable.populate(protoClass_);

		final CodeGenerator cg = new CodeGenerator();
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
		.addImport(MarshallableBase.class)
		.addLinebreak(1)
		
		// annotation
		.addImport(MarshallNo.class)
		.addImport(ColumnName.class)
		.addImport(TableName.class)
		.addImport(FlattenAs.class)
		.addImport(InflateAs.class)
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
			// get annotations on the prototype class
			final TableName tableName = protoClass_.getAnnotation(TableName.class);
			if (tableName != null) {
				final String tableNameValue = tableName.value();
				logger.debug(String.format("Prototype class %s has annotation TableName: %s", protoClassName, tableNameValue));
				code.append(String.format("\n@TableName(\"%s\")", tableNameValue));
			}
			
			code.append("\npublic class " + concreteClassName + " extends " + MarshallableBase.class.getSimpleName() + " implements " + protoClassName + " {\n\n");
		})
		
		// add no-arg constructor
		.addCode(code -> {
			code.append("\n    public " + concreteClassName + "(){}\n\n");
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
			.append(
"    protected Object getField(final Field field_)\n" +
"            throws IllegalArgumentException, IllegalAccessException {\n" +
"        return field_.get(this);\n" + 
"    }\n")
			.append(
"    protected void setField(final Field field_, final Object msg_)\n" +
"            throws JsonMessageException, IllegalAccessException {\n" + 
"        try {\n" + 
"            field_.set(this, msg_);\n" + 
"        }\n" + 
"        catch (Exception e) {\n" + 
"            throw new JsonMessageException(String.format(\"field: %s: %s\", field_.getName(), e.getMessage()));\n" + 
"        }\n" + 
"    }\n\n");
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

		return cg.getCode();
	}

	public CodeGenerator() {
		_code = new StringBuilder();
	}
	
	public String getCode() {
		return _code.toString();
	}
	
	private CodeGenerator addPackage(final Class<?> protoClass_) {
		_code.append(String.format(
				"package %s;\n"
				, protoClass_.getPackage().getName()));
		return this;
	}
	
	private CodeGenerator addImport(final Class<?> importClass_) {
		_code.append(String.format(
				"import %s.%s;\n"
				, importClass_.getPackage().getName()
				, importClass_.getSimpleName()));
		return this;
	}
	
	private CodeGenerator addImport(final String importString_) {
		_code.append(String.format(
				"import %s;\n"
				, importString_));
		return this;
	}
	
	private CodeGenerator addCode(final Consumer<StringBuilder> consumer_) {
		consumer_.accept(_code);
		return this;
	}

	private CodeGenerator addGetAndSetMethods(final SymbolTable.SymbolEntry symbolEntry) {
		// add get method
		_code.append(
				"    @Override\n" +
				"    public " + symbolEntry._returnType.getSimpleName() + " " + symbolEntry._getMethodName + "() {\n" +
				"        return " + symbolEntry._dataMemberName + ";\n" +
				"    }\n");
		
		
		// add set method
		/*
		// no longer any need to set annotation @ColumnName on set method
		// because code generator now adds annotation on the data-member declaration
		if (symbolEntry._annotationColumnNameValue != null && symbolEntry._annotationColumnNameValue.length()>0)
			_code.append(String.format("    @ColumnName(\"%s\")\n", symbolEntry._annotationColumnNameValue));
		*/
		_code.append(
				"    @Override\n" +
				"    public void " + symbolEntry._setMethodName + "(" + /*symbolEntry._dataMemberType*/ symbolEntry._returnType.getSimpleName() + " " + symbolEntry._dataMemberName + ") {\n" +
				"        this." + symbolEntry._dataMemberName + " = " + symbolEntry._dataMemberName + ";\n" +
				"    }\n\n");
		return this;
	}

	private CodeGenerator addDataMember(final SymbolTable.SymbolEntry symbolEntry_) {
		if (symbolEntry_._annotationColumnNameValue!=null &&
				!symbolEntry_._annotationColumnNameValue.isEmpty()) {
			_code.append(String.format(
					"    @ColumnName(\"%s\")\n"
					, symbolEntry_._annotationColumnNameValue));
		}
		if (symbolEntry_._annotationMarshallNo) {
			_code.append(String.format(
					"    @MarshallNo\n"));
		}
		if (symbolEntry_._flattenAs!=null &&
				!symbolEntry_._flattenAs.isEmpty()) {
			_code.append(String.format(
					"    @FlattenAs(\"%s\")\n"
					, symbolEntry_._flattenAs));
		}
		if (symbolEntry_._inflateAs!=null &&
				!symbolEntry_._inflateAs.isEmpty()) {
			_code.append(String.format(
					"    @InflateAs(\"%s\")\n"
					, symbolEntry_._inflateAs));
		}
		_code.append(String.format(
				"    private %s %s;\n\n"
				, symbolEntry_._returnType.getSimpleName()
				, symbolEntry_._dataMemberName));		
		return this;
	}
	
	private CodeGenerator addLinebreak(final int count_) {
		for (int i=0; i<count_; ++i)
			_code.append("\n");
		return this;
	}
	
	static final Logger logger = Logger.getLogger(CodeGenerator.class);
			
	private final StringBuilder _code;
}
