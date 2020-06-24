package com.persisGen;

import java.util.ArrayList;
import java.util.List;

public class ServiceParser extends CodeGen {
	
	public static String idType = "";
	public static String idName = "";
	public static String idNameLC = "";
	
	// Place holders and Fillers	
	private String generatedService = "";
	private String generatedServiceImpl = "";
	private final String nl = "\n";
	private final String PACKAGE = "{package}";
	private final String MAPPER_IMPORT = "{mapperImport}";
	private final String TYPE_IMPORT = "{typeImport}";
	private final String REPO_IMPORT = "{repositoryImport}";
	private final String TYPE = "{type}";
	private final String TYPE_SINGILAR = "{typeSingular}";
	private final String TYPE_PLURAL = "{typePlural}";
	private final String IMPORTS = "{imports}";
	private final String AUTOWIRES = "{autowires}";
	private final String HIBERNATE_METHODS = "{hibernateMethods}";
	private final String HIBERNATE_INTERFACE_METHODS = "{hibernateInterfaceMethods}";
	private final String PROPERTY = "{property}";
	private final String UPDATE_PROPS = "{updateProperties}";
	private final String ID_NAME = "{idName}";
	private final String ID_NAME_LC = "{idNameLC}";
	private final String ID_TYPE = "{idType}";
	
	// TODO Add mybatis code for mappers only
	// private final String MYBATIS_METHODS = "{myBatisMethods}";
	
	private final String AUTOWIRED_HIBERNATE = "\t@Autowired\n\tprivate {type}Repository repository;";
	private final String AUTOWIRED_MYBATIS = "\t@Autowired\n\tprivate {type}Mapper mapper;";
	private final String ALL = "{all}";
	private final String ID = "{id}";
	private final String SAVE_ID = "{saveId}";
	private final String GET_ALL = "mapper.getAll();";
	private final String GET_BY = "mapper.getBy{idName}({idNameLC});";
	private final String GET_BY_SAVE = "mapper.getBy{idName}(entity.get{idName}());";
	// private final String FIND_ALL = "repository.findAll();";
	// private final String FIND_BY = "repository.findBy{idName}(id);";
	private final String UPDATE = "\t\t\t\tupdate{type}.set{property}(entity.get{property}());";
	
	private final String[] importsHibernate = {
		"import java.util.Date;",
		"import java.util.List;",
		"import java.util.Optional;",
		"",	
		"import org.springframework.beans.factory.annotation.Autowired;",
		"import org.springframework.stereotype.Service;",
		"",
		"import {typeImport}.{type};",
		"import {repositoryImport}.{type}Repository;"
	};
	
	private final String[] importsMyBatis = {
		"import java.util.Date;",
		"import java.util.List;",
		"import java.util.Optional;",
		"",	
		"import org.springframework.beans.factory.annotation.Autowired;",
		"import org.springframework.stereotype.Service;",
		"",
		"import {typeImport}.{type};",
		"import {mapperImport}.{type}Mapper;"		
	};
	
	private final String[] importBoth = {
		"import java.util.Date;",
		"import java.util.List;",
		"import java.util.Optional;",
		"",	
		"import org.springframework.beans.factory.annotation.Autowired;",
		"import org.springframework.stereotype.Service;",
		"",
		"import {typeImport}.{type};",
		"import {mapperImport}.{type}Mapper;",
		"import {repositoryImport}.{type}Repository;"
	};
	
	public ServiceParser(int sql, String script, boolean makeRepo, boolean makeMapper, String servicePath, String repoPath, String mapperPath, String typePath, String typeName) {
		super(sql, script, makeRepo, makeMapper, servicePath, repoPath, mapperPath, typePath, typeName);
	}
	
	public String getGeneratedService() {
		return this.generatedService;
	}
	
	public String getGeneratedServiceImpl() {
		return this.generatedServiceImpl;
	}
	
	private String service() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append("import java.util.List;" + nl);
		sb.append(nl);
		sb.append("import {typeImport}.{type};" + nl);
		sb.append(nl);
		sb.append("public interface {type}Service {" + nl);
		sb.append(nl);
		sb.append("\tList<{type}> getAll();" + nl);
		sb.append(nl);
		sb.append("\t{type} getBy{idName}({idType} {idNameLC});" + nl);
		sb.append("{hibernateInterfaceMethods}");
		sb.append("}");
		return sb.toString();
	}
	
	private String serviceImpl() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append("{imports}" + nl);
		sb.append(nl);
		sb.append("@Service" + nl);
		sb.append("public class {type}ServiceImpl implements {type}Service {"+ nl);
		sb.append(nl);
		sb.append("{autowires}" + nl);
		sb.append(nl);
		sb.append("\tpublic List<{type}> getAll() {" + nl);
		sb.append("\t\tList<{type}> {typePlural} = {all}" + nl);
		sb.append(nl);
		sb.append("\t\tif({typePlural}.size() > 0)" + nl);
		sb.append("\t\t\treturn {typePlural};" + nl);
		sb.append("\t\telse" + nl);
		sb.append("\t\t\treturn null;" + nl);
		sb.append("\t}" + nl);
		sb.append(nl);
		sb.append("\tpublic {type} getBy{idName}({idType} {idNameLC}) {" + nl);
		sb.append("\t\tOptional<{type}> {typeSingular} = {id}" + nl);
		sb.append(nl);
		sb.append("\t\tif({typeSingular}.isPresent())" + nl);
		sb.append("\t\t\treturn {typeSingular}.get();" + nl);
		sb.append("\t\telse" + nl);
		sb.append("\t\t\treturn null;" + nl);
		sb.append("\t}" + nl);
		sb.append("{hibernateMethods}" + nl);
		sb.append("}");
		
		
		return sb.toString();
	}
	
	private String hibernateInterfaceMethods() {
		StringBuilder sb = new StringBuilder();
		sb.append(nl + "\t{type} save({type} {typeSingular});" + nl);
		sb.append(nl);
		sb.append("\tboolean delete({type} {typeSingular});" + nl);
		sb.append(nl);
		sb.append("\tboolean purge(long id);" + nl);
		return sb.toString();
	}
	
	private String hibernateMethods() {
		StringBuilder sb = new StringBuilder();
		sb.append(nl + "\tpublic {type} save({type} {typeSingular}) {" + nl);
		sb.append("\t\treturn createOrUpdate{type}({typeSingular});" + nl);
		sb.append("\t}" + nl);
		sb.append(nl);
		sb.append("\tpublic boolean delete({type} {typeSingular}) {" + nl);
		sb.append("\t\t// set the deleted field" + nl);
		sb.append("\t\t{typeSingular}.setDeletedAt(new Date());" + nl);
		sb.append("\t\treturn createOrUpdate{type}({typeSingular}) != null ? true : false;" + nl);
		sb.append("\t}" + nl);
		sb.append(nl);
		sb.append("\tpublic boolean purge({type} {typeSingular}) {" + nl);
		sb.append("\t\ttry {" + nl);
		sb.append("\t\t\trepository.delete({typeSingular});" + nl);
		sb.append("\t\t\treturn true;" + nl);
		sb.append("\t\t} catch(Exception e) {" + nl);
		sb.append("\t\t\treturn false;" + nl);
		sb.append("\t\t}" + nl);
		sb.append("\t}" + nl);
		sb.append(nl);
		sb.append("\t// Create or update record" + nl);
		sb.append("\tprivate {type} createOrUpdate{type}({type} entity) {" + nl);
		sb.append("\t\tOptional<{type}> {typeSingular} = {saveId}" + nl);
		sb.append(nl);		
		sb.append("\t\ttry {" + nl);
		sb.append("\t\t\tif({typeSingular}.isPresent()) {" + nl);
		sb.append("\t\t\t\t{type} update{type} = {typeSingular}.get();" + nl);
		sb.append(nl);	
		sb.append("{updateProperties}" + nl);
		sb.append(nl);	
		sb.append("\t\t\t\treturn repository.save(update{type});" + nl);
		sb.append("\t\t\t} else {" + nl);
		sb.append("\t\t\t\treturn repository.save(entity);" + nl);
		sb.append("\t\t\t}" + nl);
		sb.append("\t\t} catch(Exception e) {" + nl);
		sb.append("\t\t\treturn null;" + nl);
		sb.append("\t\t}" + nl);
		sb.append("\t}");
		
		return sb.toString();
	}
	
	public boolean run() {
		try {
			// lets get our variables to fill the place holders first
			generatedService = service();
			generatedServiceImpl = serviceImpl(); 
			Inflector inflector = new Inflector();
			String type = this.getTypeName();
			String typeImport = this.getPackage(this.getTypePath());
			String typeSingular = type.toLowerCase();
			String typePlural = inflector.pluralize(typeSingular);			

			String imports = "";
			String methods = "";
			String interfaceMethods = "";
			String autowires = "";			
				
			// for now just use mybatis for selects
			String all = GET_ALL;
			String id = GET_BY;
			String saveId = GET_BY_SAVE;
			
			// lowercase the id
			idNameLC = idName.toLowerCase();
			
			// set imports, autowires and repo/mapper method calls
			// both mapper and repo
			if (this.isMakeRepo() && this.isMakeMapper()) {
				String[] importsArray = importBoth;
				autowires = AUTOWIRED_HIBERNATE + nl + nl + AUTOWIRED_MYBATIS;				
				interfaceMethods = hibernateInterfaceMethods();
				methods = hibernateMethods();
				imports = String.join(nl, importsArray);
				imports = imports.replace(REPO_IMPORT, this.getPackage(this.getRepoPath()));
				imports = imports.replace(MAPPER_IMPORT, this.getPackage(this.getMapperPath()));
			}
			
			// only mapper
			if (this.isMakeMapper() && !this.isMakeRepo()) {
				String[] importsArray = importsMyBatis;
				autowires = AUTOWIRED_MYBATIS;			
				imports = String.join(nl, importsArray);
				imports = imports.replace(MAPPER_IMPORT, this.getPackage(this.getMapperPath()));
			}
			
			// only hibernate			
			if (!this.isMakeMapper() && this.isMakeRepo()) {
				String[] importsArray = importsHibernate;
				autowires = AUTOWIRED_HIBERNATE;
				interfaceMethods = hibernateInterfaceMethods();
				methods = hibernateMethods();
				imports = String.join(nl, importsArray);
				imports = imports.replace(REPO_IMPORT, this.getPackage(this.getRepoPath()));
			}
			
			// fill in place holders, start with the service interface
			generatedService = generatedService.replace(PACKAGE, this.getPackage(this.getServicePath()));
			generatedService = generatedService.replace(IMPORTS, imports);
			generatedService = generatedService.replace(TYPE_IMPORT, typeImport);
			generatedService = generatedService.replace(HIBERNATE_INTERFACE_METHODS, interfaceMethods);
			generatedService = generatedService.replace(TYPE, type);
			generatedService = generatedService.replace(TYPE_SINGILAR, typeSingular);
			generatedService = generatedService.replace(ID_NAME, idName);
			generatedService = generatedService.replace(ID_NAME_LC, idNameLC);
			generatedService = generatedService.replace(ID_TYPE, idType);
			
			
			// start filling in the service impl
			generatedServiceImpl = generatedServiceImpl.replace(PACKAGE, this.getPackage(this.getServicePath()));
			generatedServiceImpl = generatedServiceImpl.replace(IMPORTS, imports);
			generatedServiceImpl = generatedServiceImpl.replace(TYPE_IMPORT, typeImport);
			generatedServiceImpl = generatedServiceImpl.replace(AUTOWIRES, autowires);
			generatedServiceImpl = generatedServiceImpl.replace(HIBERNATE_METHODS, methods);
			generatedServiceImpl = generatedServiceImpl.replace(ALL, all);
			generatedServiceImpl = generatedServiceImpl.replace(ID, id);
			generatedServiceImpl = generatedServiceImpl.replace(SAVE_ID, saveId);
			generatedServiceImpl = generatedServiceImpl.replace(ID_NAME, idName);
			generatedServiceImpl = generatedServiceImpl.replace(ID_NAME_LC, idNameLC);
			generatedServiceImpl = generatedServiceImpl.replace(ID_TYPE, idType);
			generatedServiceImpl = generatedServiceImpl.replace(TYPE, type);
			generatedServiceImpl = generatedServiceImpl.replace(TYPE_SINGILAR, typeSingular);
			generatedServiceImpl = generatedServiceImpl.replace(TYPE_PLURAL, typePlural);
			String props = String.join(nl, parseScript());
			generatedServiceImpl = generatedServiceImpl.replace(UPDATE_PROPS, props);
			
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
			String property = UPDATE;
			
			// separate field name and data type
			int pos = field.indexOf(" ");
			String fieldName = field.substring(0, pos);
			String propName = this.inflector.camelCase(fieldName, true);
			
			property = property.replace(PROPERTY, propName);
			property = property.replace(TYPE, this.getTypeName());
			
			return property;
		}
}
