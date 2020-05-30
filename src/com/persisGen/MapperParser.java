package com.persisGen;

import java.util.ArrayList;
import java.util.List;

public class MapperParser extends CodeGen {

	private String nl = "\n";
	
	private String generatedJava = "";
	private String generatedXml = "";
	
	// Place holders and Fillers
	private final String PACKAGE = "{package}";
	private final String TYPE = "{type}";
	private final String TABLE = "{table}";
	private final String TYPE_IMPORT = "{typeImport}";
	private final String RESULT = "<result property=\"{property}\" column=\"{column}\"/>";
	private final String COLUMN = "{column}";
	private final String PROPERTY = "{property}";
	private final String RESULTS = "{results}";
	
	private final String[] imports = {
		"import java.util.List;",
		"import java.util.Optional;",
		"",
		"import org.apache.ibatis.annotations.Mapper;",
		"import org.apache.ibatis.annotations.ResultMap;",
		"import org.apache.ibatis.annotations.Select;",
		"",
		"import {typeImport}.{type};"
	};
	
	private final String[] mapperProperties = {
		"\tfinal String getAll = \"SELECT * FROM {table}\";", 
		"\tfinal String getById = \"SELECT * FROM {table} WHERE id = #{id}\";"
	};
	
	private final String[] mapperMethods = {
		"\t@Select(getAll)",
		"\t@ResultMap(\"{package}.{type}Mapper.{type}Result\")",
		"\tList<{type}> getAll();",
		"",
		"\t@Select(getById)",
		"\t@ResultMap(\"{package}.{type}Mapper.{type}Result\")",
		"\tOptional<{type}> getById(long id);"
	};
	
	/**
	 * Constructor
	 * @param script
	 * @param mapperPath
	 * @param typePath
	 * @param typeName
	 */
	public MapperParser(int sql, String script, String mapperPath, String typePath, String typeName) {
		super(sql, script, mapperPath, typePath, typeName);
	}
	
	public MapperParser() { super(); }
	
	public String getGeneratedJava() {
		return this.generatedJava;
	}
	
	public String getGeneratedXml() {
		return this.generatedXml;
	}
	
	// Mapper Java String builder
	private String mapperJava() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append(String.join(nl, imports) + nl);
		sb.append(nl);
		sb.append("@Mapper" + nl);
		sb.append("public interface {type}Mapper {" + nl);
		sb.append(nl);
		sb.append(String.join(nl, mapperProperties) + nl);
		sb.append(nl);
		sb.append(String.join(nl, mapperMethods));
		sb.append(nl + "}");
		
		return sb.toString();
	}
	
	// Mapper xml String Builder
	private String mapperXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + nl);
		sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">" + nl);
		sb.append("<mapper namespace=\"{package}.{type}Mapper\">" + nl);
		sb.append("\t<resultMap type=\"{type}\" id=\"{type}Result\">" + nl);
		sb.append("{results}" + nl);
		sb.append("\t</resultMap>" + nl);
		sb.append("</mapper>");
		
		return sb.toString();
	}
	
	/**
	 * generate the code for our mappers
	 * @return boolean
	 */
	public boolean run() {
		try {
			// get our java code
			generatedJava = mapperJava();
			
			// get our package
			generatedJava = generatedJava.replace(PACKAGE, this.getPackage(this.getMapperPath()));
			
			// get our import type
			generatedJava = generatedJava.replace(TYPE_IMPORT, this.getPackage(this.getTypePath()));
			
			// Get our table name
			generatedJava = generatedJava.replace(TABLE, this.getTableName());
			
			// set type name
			generatedJava = generatedJava.replace(TYPE, this.getTypeName());
			
			// get our xml
			generatedXml = mapperXml();
			
			// get our package
			generatedXml = generatedXml.replace(PACKAGE, this.getPackage(this.getMapperPath()));
			
			// set type name
			generatedXml = generatedXml.replace(TYPE, this.getTypeName());
			
			// build our result mapping
			String results = String.join(nl, parseScript());
			generatedXml = generatedXml.replace(RESULTS, results);
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Parse out the fields to java properties
	private List<String> parseScript() {
		List<String> results = new ArrayList<String>();
		String[] table = this.getScript().split(nl);
		List<String> fields = new ArrayList<String>();
		for (int i = 0; i < table.length; i++) {
			if (i == 0) continue;
			if (i == table.length - 1) continue;
			fields.add(table[i]);			
		}			
				
		
		// Parse fields to results			
		for(String field : fields) {
			results.add(parseField(field));
		}		
		
		return results;
	}
	
	private String parseField(String field) {
		// remove any \t, \r, and whitespace
		field = field.replace("\r", "");
		field = field.replace("\t", "");
		field = field.trim();
		String result = "\t\t" + RESULT;
		
		// separate field name and data type
		int pos = field.indexOf(" ");
		String fieldName = field.substring(0, pos);
		String propName = this.inflector.lowerCamelCase(field.substring(0, pos));
		
		result = result.replace(COLUMN, fieldName);
		result = result.replace(PROPERTY, propName);
		
		return result;
	}
}
