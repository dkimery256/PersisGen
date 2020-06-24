package com.persisGen;

public class CodeGen {
	
	private SqlLang sql;
	private String script;
	private boolean makeRepo;
	private boolean makeMapper;
	private boolean makeServices;
	private String mapperPath;
	private String repoPath;
	private String typePath;
	private String servicePath;
	private String typeName;
	public Inflector inflector = new Inflector();
	
	private String nl = "\n";
	
	/**
	 * Constructor for Type Parser
	 * @param sql
	 * @param script
	 * @param makeRepo
	 * @param typePath
	 * @param typeName
	 */
	public CodeGen(int sql, String script, boolean makeRepo, String typePath, String typeName) {
		switch(sql) {
			case 0:
				this.sql = SqlLang.MYSQL;
				break;
			case 1:
				this.sql = SqlLang.POSTGRES;
		}
		this.script = script;		
		this.makeRepo = makeRepo;		
		this.typePath = typePath;
		// this.typeName = inflector.capitalize(inflector.singularize(typeName));
		this.typeName = typeName;
	}
	
	/**
	 * Constructor for MyBatis Mapper Parser
	 * @param sql
	 * @param script
	 * @param mapperPath
	 * @param typePath
	 * @param typeName
	 */
	public CodeGen(int sql, String script, String mapperPath, String typePath, String typeName) {
		switch(sql) {
			case 0:
				this.sql = SqlLang.MYSQL;
				break;
			case 1:
				this.sql = SqlLang.POSTGRES;
		}
		this.script = script;		
		this.mapperPath = mapperPath;		
		this.typePath = typePath;
		// this.typeName = inflector.capitalize(inflector.singularize(typeName));
		this.typeName = typeName;
	}
	
	/**
	 * Constructor for Hibernate Repo
	 * @param repoPath
	 * @param typePath
	 * @param typeName
	 */
	public CodeGen(String repoPath, String typePath, String typeName) {
		this.repoPath = repoPath;		
		this.typePath = typePath;
		// this.typeName = inflector.capitalize(inflector.singularize(typeName));
		this.typeName = typeName;
	}
	
	/**
	 * Constructor for Services
	 * @param sql
	 * @param script
	 * @param makeRepo
	 * @param makeMapper
	 * @param repoPath
	 * @param mapperPath
	 * @param typePath
	 * @param typeName
	 */
	public CodeGen(int sql, String script, boolean makeRepo, boolean makeMapper, 
			 String servicePath, String repoPath, String mapperPath, String typePath, String typeName) {
		switch(sql) {
		case 0:
			this.sql = SqlLang.MYSQL;
			break;
		case 1:
			this.sql = SqlLang.POSTGRES;
		}
		this.script = script;
		this.servicePath = servicePath;
		this.makeRepo = makeRepo;		
		this.makeMapper = makeMapper;
		this.repoPath = repoPath;
		this.mapperPath = mapperPath;
		this.typePath = typePath;
		// this.typeName = inflector.capitalize(inflector.singularize(typeName));
		this.typeName = typeName;
	}
	
	public CodeGen() {}

	public SqlLang getSql() {
		return sql;
	}

	public void setSql(SqlLang sql) {
		this.sql = sql;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean isMakeRepo() {
		return makeRepo;
	}

	public void setMakeRepo(boolean makeRepo) {
		this.makeRepo = makeRepo;
	}

	public boolean isMakeMapper() {
		return makeMapper;
	}

	public void setMakeMpper(boolean makeMpper) {
		this.makeMapper = makeMpper;
	}

	public boolean isMakeServices() {
		return makeServices;
	}

	public void setMakeServices(boolean makeServices) {
		this.makeServices = makeServices;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = inflector.capitalize(inflector.singularize(typeName));
	}
	
	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	protected String getPackage(String path) {
		String packageName = "";
		
		// check for org
		int pos = path.indexOf("org");
		if (pos > 0) {
			packageName = path.substring(pos);
		}
		
		// check for com
		pos = path.indexOf("com");
		if (pos > 0) {
			packageName = path.substring(pos);
		}
		
		if (packageName != "" ) {
			packageName = packageName.replace("\\", ".");
			packageName = packageName.replace("/", ".");
		}
		
		return packageName;
	}
	
	/**
	 * 
	 * @return string table name
	 */
	protected String getTableName() {
		String createLine = this.getScript().split(nl)[0];
		if (this.sql == SqlLang.POSTGRES) {
			createLine = createLine.replace("public.", "");
		}
		int pos = createLine.indexOf("TABLE ") + "TABLE ".length();
		String subName = createLine.substring(pos);
		String name = "";
		char ch;
		for (int i = 0; i < subName.length(); i++) {
			ch = subName.charAt(i);
			if (Character.isLetter(ch)) {
				name += ch;
			}
			
			if (ch == '_') {
				name += ch;
			}
		}		
		return name;
	}
}
