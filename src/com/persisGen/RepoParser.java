package com.persisGen;

public class RepoParser extends CodeGen {
	
	private String generated = "";
	
	// Place holders and fillers
	private final String PACKAGE = "{package}";
	private final String TYPE = "{type}";
	private final String TYPE_IMPORT = "{typeImport}";
	private final String nl = "\n";
		
	public RepoParser(String repoPath, String typePath, String typeName) {
		super(repoPath, typePath, typeName);
	}
	
	public String getGenerated() {
		return this.generated;
	}
	
	private String repo() {
		StringBuilder sb = new StringBuilder();
		sb.append("package {package};" + nl);
		sb.append(nl);
		sb.append("import org.springframework.data.jpa.repository.JpaRepository;" + nl);
		sb.append("import org.springframework.stereotype.Repository;" + nl);
		sb.append(nl);
		sb.append("import {typeImport}.{type};" + nl);
		sb.append(nl);
		sb.append("@Repository" + nl);
		sb.append("public interface {type}Repository extends JpaRepository<{type}, Long> {}");
		return sb.toString();
	}
	
	public boolean run() {
		try {
			// get our repo code
			generated = repo();
			
			// get our package
			generated = generated.replace(PACKAGE, this.getPackage(this.getRepoPath()));
			
			// get our import type
			generated = generated.replace(TYPE_IMPORT, this.getPackage(this.getTypePath()));
			
			// set type name
			generated = generated.replace(TYPE, this.getTypeName());
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	 
}
