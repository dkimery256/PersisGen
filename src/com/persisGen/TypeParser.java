package com.persisGen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeParser extends CodeGen {
	
	private String generated = "";
	
	/**
	 * Enum place holder.
	 * Key - Enum name
	 * Value - List of properties 
	 */
	Map<String, List<String>> enums = new HashMap<String, List<String>>();
	
	// Place holders
	private final String PACKAGE = "{package}";
	private final String TYPE = "{type}";
	private final String TABLE = "{table}";
	private final String PROPERTIES = "{properties}";
	private final String ENUM_TYPE = "{eType}";
	private final String ADD_ENUMS = "{addEnums}";
	private final String ENUMS = "{enums}";
	
	// Fillers
	private final String TEMPORAL = "\t@Column\n\t@Temporal(TemporalType.TIMESTAMP)\n\tprivate ";
	private final String COLUMN = "\t@Column\n\tprivate ";
	private final String ID_AUTO = "\t@Id\n\t@GeneratedValue\n\tprivate long ";
	private final String nl = "\n";
	private final String colReplace = "@Column";
	private final String noNullCol = "@Column(insertable = false, updatable = false)";
	private final String enumAnno = "\t@Enumerated(EnumType.STRING)\n\tprivate ";
	
	// Hibernate Entity
	private final String[] hibernateImports = {
		"import java.io.Serializable;",
		"import java.util.Date;",
		"",
		"import javax.persistence.Column;",
		"import javax.persistence.Entity;",
		"import javax.persistence.GeneratedValue;",
		"import javax.persistence.Id;",
		"import javax.persistence.Table;",
		"import javax.persistence.Temporal;",
		"import javax.persistence.TemporalType;",
		"",
		"import lombok.Data;",
		""
	};
	
	private final String[] hibernateClass = {
		"@Data",
		"@Entity",
		"@Table(name=\"{table}\")",
		"public class {type} implements Serializable {",
		"",
		"{addEnums}",
		"{properties}",
		"}"
	};
	
	// MyBatis Type
	private final String[] batisClass = {
		"@Data",
		"public class {type} implements Serializable {",
		"",
		"{addEnums}",
		"{properties}",
		"}"
	};
	
	private final String[] batisImports = {
		"import java.io.Serializable;",
		"import java.util.Date;",
		"",
		"import lombok.Data;",
		"",
	};
	
	// enum builder
	private String publicEnum() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tpublic enum {eType} {\n");
		sb.append("{enums}" + nl);
		sb.append("\t}" + nl);
		
		return sb.toString();
	}
	
	
	// String Builders
	private String typeWithHibernate() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append(String.join(nl, hibernateImports));
		sb.append(nl);
		sb.append(String.join(nl, hibernateClass));
		return sb.toString();
	}
	
	private String typeWithoutHibernate() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append(String.join(nl, batisImports));
		sb.append(nl);
		sb.append(String.join(nl, batisClass));
		return sb.toString();
	}
	
	public TypeParser(int sql, String script, boolean makeRepo, String typePath, String typeName) {
		super(sql, script, makeRepo, typePath, typeName);
	}
	
	public TypeParser() { super(); }
	
	public String getGenerated() {
		return this.generated;
	}
	
	/**
	 * generate the code for our types
	 * @return boolean
	 */
	public boolean run() {
		try {
			// get our entity or basic type
			generated = this.isMakeRepo() ? typeWithHibernate() : typeWithoutHibernate();
			
			// get our package
			generated = generated.replace(PACKAGE, this.getPackage(this.getTypePath()));
			
			// if we are using hibernate then we need the table name
			if (this.isMakeRepo()) {			
				generated = generated.replace(TABLE, this.getTableName());
			}
			
			// set type name
			generated = generated.replace(TYPE, this.getTypeName());
			
			// set properties
			String props = String.join(nl, parseScript());
			generated = generated.replace(PROPERTIES, props);
			
			// see if it needs any enums
			if (enums.size() > 0) {
				StringBuilder addEnums = new StringBuilder();
				String enumClass = publicEnum();
				for (Map.Entry<String,List<String>> eClass : enums.entrySet()) {
					String eType = eClass.getKey();
					List<String> eProps = eClass.getValue();
					for(int i = 0; i < eProps.size(); i++) {
						eProps.set(i,"\t\t" + eProps.get(i));
					}
					enumClass = enumClass.replace(ENUM_TYPE, eType);
					enumClass = enumClass.replace(ENUMS, String.join("," + nl, eProps));
					addEnums.append(enumClass);
				}
				generated = generated.replace(ADD_ENUMS, addEnums);
			} else { // remove the placeholder -- this is not my fav way, but it is late af
				String[] removeEnums = generated.split(nl);
				StringBuilder sb = new StringBuilder();
				for(String line : removeEnums) {
					if (!line.equals(ADD_ENUMS)) {
						sb.append(nl + line);
					}
				}
				generated = sb.toString();
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	// Parse out the fields to java properties
	private List<String> parseScript() {
		List<String> properties = new ArrayList<String>();
		String[] table = this.getScript().split(nl);
		List<String> fields = new ArrayList<String>();
		for (int i = 0; i < table.length; i++) {
			if (i == 0) continue;
			if (i == table.length - 1) continue;
			fields.add(table[i]);			
		}
		
		String property = "";
		
		// Parse MySql
		if (this.getSql() == SqlLang.MYSQL) {
			for(String field : fields) {
				property = parseMySql(field);
				properties.add(checkForDefaultDate(property));				
			}
		} 
		
		// Parse Postgres
		if (this.getSql() == SqlLang.POSTGRES) {
			for(String field : fields) {
				property = parsePostgres(field);
				properties.add(checkForDefaultDate(property));
			}
		}		
		return properties;
	}
	
	// TODO Fix repo logic for id/uid
	
	/**
	 * Convert field name into java property via mysql
	 * @param field
	 * @return string property
	 */
	private String parseMySql(String field) {
		// remove any \t, \r, and whitespace
		field = field.replace("\r", "");
		field = field.replace("\t", "");
		field = field.trim();
		String property = "";
		
		// separate field name and data type
		int pos = field.indexOf(" ");		
		String fieldName = this.inflector.lowerCamelCase(field.substring(0, pos));
		String fieldType = field.substring(pos + 1);
		
		// id or uid see if we are int or character
		// also check for auto increment
		if (fieldName.equals("uid") || fieldName.equals("id")) {
			ServiceParser.idType = "long";
			ServiceParser.idName = inflector.capitalize(fieldName);
			if (this.isMakeRepo()) {
				if (fieldType.contains("int")) {
					if (fieldType.toUpperCase().contains("AUTO_INCREMENT")) {
						property = ID_AUTO + fieldName + ";";
					} else {
						if (fieldType.contains("int")) {
							property = "\t@Id\n\tprivate long " + fieldName + " = 0;";
						} else {
							property = "\t@Id\n\tprivate String " + fieldName + " = \"\";";
							ServiceParser.idType = "String";
						}
					}					
				} else {
					property = "\t@Id\n\tprivate String " + fieldName + " = \"\";";
					ServiceParser.idType = "String";
				}
			} else {
				if (fieldType.contains("int")) {
					String intProp = "int";
					if (fieldType.contains("big"))
						intProp = "long";
					property = "\tprivate " + intProp + " " + fieldName + " = 0;";
				} else {
					property = "\tprivate String " + fieldName + " = \"\";";
					ServiceParser.idType = "String";
				}
			}
		} else {  // non id columns
			
			// varchar and text
			if (fieldType.toLowerCase().contains("varchar") || 
					fieldType.toLowerCase().contains("text")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "String " + fieldName + " = \"\";"; 
				} else {
					property = "\tprivate String " + fieldName + " = \"\";";
				}
			}
			
			// double and ints
			if (fieldType.toLowerCase().contains("double")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "double " + fieldName + " = 0;"; 
				} else {
					property = "\tprivate double " + fieldName + " = 0;";
				}
			}
			
			if (fieldType.toLowerCase().contains("int")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "long " + fieldName + " = 0;"; 
				} else {
					property = "\tprivate long " + fieldName + " = 0;";
				}
			}			
			
			// booleans
			if (fieldType.toLowerCase().contains("boolean")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "boolean " + fieldName + " = false;"; 
				} else {
					property = "\tprivate boolean " + fieldName + " = false;";
				}
			}
			
			// date
			if (fieldType.toLowerCase().contains("time")) {
				if (this.isMakeRepo()) {
					property = TEMPORAL + "Date " + fieldName + " = null;"; 
				} else {
					property = "\tprivate Date " + fieldName + " = null;";
				}
			}
			
			// enum, this will also add to the enum map
			if (fieldType.toLowerCase().contains("enum")) {
				property = parseEnum(fieldName, field);
			}
		}		
		return property;
	}
	
	/**
	 * Convert field name into java property via postgres
	 * @param field
	 * @return string property
	 */
	private String parsePostgres(String field) {
		// remove any \t, \r, and whitespace
		field = field.replace("\r", "");
		field = field.replace("\t", "");
		field = field.trim();
		String property = "";
		
		// separate field name and data type
		int pos = field.indexOf(" ");		
		String fieldName = this.inflector.lowerCamelCase(field.substring(0, pos));
		String fieldType = field.substring(pos + 1);
		
		// id or uid see if we are int or character
		// also check for auto increment
		if (fieldName.equals("uid") || fieldName.equals("id")) {
			ServiceParser.idName = inflector.capitalize(fieldName);
			ServiceParser.idType = "long";
			if (this.isMakeRepo()) {
				if (fieldType.toUpperCase().contains("SERIAL")) {							
					property = ID_AUTO + fieldName + ";";
				} else {
					if (fieldType.contains("int")) {
						property = "\t@Id\n\tprivate long " + fieldName + " = 0;";
					} else {
						property = "\t@Id\n\tprivate String " + fieldName + " = \"\";";
						ServiceParser.idType = "String";
					}
				}
			} else {
				if (fieldType.contains("int")) {
					String intProp = "int";
					if (fieldType.contains("big"))
						intProp = "long";
					property = "\tprivate " + intProp + " " + fieldName + " = 0;";
				} else {
					property = "\tprivate String " + fieldName + " = \"\";";
					ServiceParser.idType = "String";
				}
			}
		} else {  // non id columns
			
			// varchar and text
			if (fieldType.toLowerCase().contains("character") || 
					fieldType.toLowerCase().contains("text")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "String " + fieldName + " = \"\";"; 
				} else {
					property = "\tprivate String " + fieldName + " = \"\";";
				}
			}
			
			// double and ints
			if (fieldType.toLowerCase().contains("double")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "double " + fieldName + " = 0;"; 
				} else {
					property = "\tprivate double " + fieldName + " = 0;";
				}
			}
			
			if (fieldType.toLowerCase().contains("int")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "long " + fieldName + " = 0;"; 
				} else {
					property = "\tprivate long " + fieldName + " = 0;";
				}
			}			
			
			// booleans
			if (fieldType.toLowerCase().contains("boolean")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "boolean " + fieldName + " = false;"; 
				} else {
					property = "\tprivate boolean " + fieldName + " = false;";
				}
			}
			
			// date
			if (fieldType.toLowerCase().contains("time")) {
				if (this.isMakeRepo()) {
					property = TEMPORAL + "Date " + fieldName + " = null;";							
				} else {
					property = "\tprivate Date " + fieldName + " = null;";
				}
			}
			
			// enum, this will also add to the enum map
			if (fieldType.toLowerCase().contains("enum")) {
				property = parseEnum(fieldName, field);
			}
		}	
		return property;
	}
	
	// see if we have date fields for jpa to not update because the database will do it
	private String checkForDefaultDate(String property) {
		String newProperty = "";		
		// TODO add a menu option for this functionality
		if (this.isMakeRepo()) {
			if (property.toLowerCase().contains("modified")) {
				newProperty = property.replace(colReplace, noNullCol);
			}
			if (property.toLowerCase().contains("createdat")) {
				newProperty = property.replace(colReplace, noNullCol);
			}
			if (property.toLowerCase().contains("updatedat")) {
				newProperty = property.replace(colReplace, noNullCol);
			}
		}
		return newProperty.equals("") ? property : newProperty;
	}
	
	private String parseEnum(String enumName, String field) {
		// parse out the enum values
		int start = field.indexOf("(") + 1;
		int end = field.indexOf(")");
		field = field.substring(start, end);
		field = field.replace(" ", "");
		field = field.replace("'", "");
		
		// add to the enum to add to the type class later
		String[] enumArray = field.split(",");
		String eType = inflector.camelCase(enumName, true);
		List<String> list = Arrays.asList(enumArray);	
		
		enums.put(eType, list);
		
		// build enum prop for the class type
		String eName = inflector.lowerCamelCase(enumName);		
		return String.format("%s%s %s;", enumAnno, eType, eName);
	}
}