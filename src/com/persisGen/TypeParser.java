package com.persisGen;

import java.util.ArrayList;
import java.util.List;

public class TypeParser extends CodeGen {
	
	private String generated = "";
	
	// Place holders
	private final String PACKAGE = "{package}";
	private final String TYPE = "{type}";
	private final String TABLE = "{table}";
	private final String PROPERTIES = "{properties}";
	private final String nl = "\n";
	
	// Fillers
	private final String TEMPORAL = "\t@Column\n\t@Temporal(TemporalType.TIMESTAMP)\n\tprivate ";
	private final String COLUMN = "\t@Column\n\tprivate ";
	private final String ID_AUTO = "\t@Id\n\t@GeneratedValue\n\tprivate long ";
	
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
		"{properties}",
		"}"
	};
	
	// MyBatis Type
	private final String[] batisClass = {
		"@Data",
		"public class {type} {",
		"",
		"{properties}",
		"}"
	};
	
	
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
		sb.append("import lombok.Data;" + nl);
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
		
		// Parse MySql
		if (this.getSql() == SqlLang.MYSQL) {
			for(String field : fields) {
				properties.add(parseMySql(field));
			}
		} 
		
		// Parse Postgres
		if (this.getSql() == SqlLang.POSTGRES) {
			for(String field : fields) {
				properties.add(parsePostgres(field));
			}
		}
		
		return properties;
	}
	
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
			ServiceParser.idName = inflector.capitalize(fieldName);
			if (fieldType.contains("int")) {
				if (fieldType.toUpperCase().contains("AUTO_INCREMENT") && this.isMakeRepo()) {
					property = ID_AUTO + fieldName + ";";
				} else {
					property = "\tprivate long " + fieldName + ";";
					ServiceParser.idType = "long";
				}
			} else {
				property = "\tprivate String " + fieldName + ";";
				ServiceParser.idType = "String";
			}
		} else {  // non id columns
			
			// varchar and text
			if (fieldType.toLowerCase().contains("varchar") || 
					fieldType.toLowerCase().contains("text")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "String " + fieldName + ";"; 
				} else {
					property = "\tprivate String " + fieldName + ";";
				}
			}
			
			// double and ints
			if (fieldType.toLowerCase().contains("double")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "double " + fieldName + ";"; 
				} else {
					property = "\tprivate double " + fieldName + ";";
				}
			}
			
			if (fieldType.toLowerCase().contains("int")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "long " + fieldName + ";"; 
				} else {
					property = "\tprivate long " + fieldName + ";";
				}
			}			
			
			// booleans
			if (fieldType.toLowerCase().contains("boolean")) {
				if (this.isMakeRepo()) {
					property = COLUMN + "boolean " + fieldName + ";"; 
				} else {
					property = "\tprivate boolean " + fieldName + ";";
				}
			}
			
			// date
			if (fieldType.toLowerCase().contains("time")) {
				if (this.isMakeRepo()) {
					property = TEMPORAL + "Date " + fieldName + ";"; 
				} else {
					property = "\tprivate Date " + fieldName + ";";
				}
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
					if (fieldType.toUpperCase().contains("SERIAL") && this.isMakeRepo()) {
						property = ID_AUTO + fieldName + ";";
						ServiceParser.idType = "long";
					} else {
						property = "\t@Id\n\tprivate String " + fieldName + ";";
						ServiceParser.idType = "String";
					}
				} else {  // non id columns
					
					// varchar and text
					if (fieldType.toLowerCase().contains("character") || 
							fieldType.toLowerCase().contains("text")) {
						if (this.isMakeRepo()) {
							property = COLUMN + "String " + fieldName + ";"; 
						} else {
							property = "\tprivate String " + fieldName + ";";
						}
					}
					
					// double and ints
					if (fieldType.toLowerCase().contains("double")) {
						if (this.isMakeRepo()) {
							property = COLUMN + "double " + fieldName + ";"; 
						} else {
							property = "\tprivate double " + fieldName + ";";
						}
					}
					
					if (fieldType.toLowerCase().contains("int")) {
						if (this.isMakeRepo()) {
							property = COLUMN + "long " + fieldName + ";"; 
						} else {
							property = "\tprivate long " + fieldName + ";";
						}
					}			
					
					// booleans
					if (fieldType.toLowerCase().contains("boolean")) {
						if (this.isMakeRepo()) {
							property = COLUMN + "boolean " + fieldName + ";"; 
						} else {
							property = "\tprivate boolean " + fieldName + ";";
						}
					}
					
					// date
					if (fieldType.toLowerCase().contains("time")) {
						if (this.isMakeRepo()) {
							property = TEMPORAL + "Date " + fieldName + ";"; 
						} else {
							property = "\tprivate Date " + fieldName + ";";
						}
					}
				}
				
				return property;
	}	
}
