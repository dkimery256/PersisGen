package com.persisGen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JFileChooser;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class PersisGen {

	protected Shell shlPersisgen;
	private Text textProjectRoot;
	private Label lblMybatisMappers;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text textMappers;
	private Text textRepos;
	private Text textTypes;
	private Text textScript;	
	private Text textTypeName;
	private Text textServices;
	private Text textTypePreview;
	private Text textMapperJavaPreview;
	private Text textMapperXmlPreview;
	private Text textRepoPreview;
	private Text textServicePreview;
	private Text textServiceImplPreview;
	//private Inflector inflector = new Inflector();

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PersisGen window = new PersisGen();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPersisgen.open();
		shlPersisgen.layout();
		while (!shlPersisgen.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPersisgen = new Shell();
		shlPersisgen.setImage(SWTResourceManager.getImage(PersisGen.class, "/com/persisGen/code-32.ico"));
		shlPersisgen.setSize(685, 705);
		shlPersisgen.setText("PersisGen");
		
		// get app settings				
		if (!appProperties.load()) {
			MessageDialog.openError(shlPersisgen, "Error", "Could not load app properites");
		}   	
		
		Label lblProjectRoot = new Label(shlPersisgen, SWT.NONE);
		lblProjectRoot.setBounds(10, 25, 82, 20);
		lblProjectRoot.setText("Project Root");
		
		textProjectRoot = new Text(shlPersisgen, SWT.BORDER);
		textProjectRoot.setBounds(98, 22, 463, 26);
		textProjectRoot.setText(appProperties.get("path"));
		
		Button btnBrowse = new Button(shlPersisgen, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int option = fileChooser.showOpenDialog(null);
	            if(option == JFileChooser.APPROVE_OPTION){
	            	File file = fileChooser.getSelectedFile();
	            	String path = file.getAbsolutePath();
	            	textProjectRoot.setText(path);
	            	appProperties.set("path", path);           	
	            	if (!appProperties.save()) {
	            		MessageDialog.openError(shlPersisgen, "Error", "Could not save app properites");
	            	}        	
	            }
			}
		});
		btnBrowse.setBounds(567, 20, 90, 30);
		btnBrowse.setText("Browse...");
		
		Group grpPackages = new Group(shlPersisgen, SWT.NONE);
		grpPackages.setText("Packages");
		grpPackages.setBounds(10, 65, 277, 202);
		formToolkit.adapt(grpPackages);
		formToolkit.paintBordersFor(grpPackages);
		
		textMappers = new Text(grpPackages, SWT.BORDER);
		textMappers.setBounds(141, 27, 123, 26);
		formToolkit.adapt(textMappers, true, true);
		textMappers.setText(appProperties.get("mapper"));
		
		lblMybatisMappers = new Label(grpPackages, SWT.NONE);
		lblMybatisMappers.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMybatisMappers.setBounds(10, 30, 123, 20);
		lblMybatisMappers.setText("MyBatis Mappers");
		
		Label lblHibernateRepos = new Label(grpPackages, SWT.NONE);
		lblHibernateRepos.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblHibernateRepos.setBounds(10, 62, 123, 20);
		lblHibernateRepos.setText("Hibernate Repos");
		
		textRepos = new Text(grpPackages, SWT.BORDER);
		textRepos.setBounds(141, 59, 123, 26);
		formToolkit.adapt(textRepos, true, true);
		textRepos.setText(appProperties.get("repo"));
		
		Label lblTypes = new Label(grpPackages, SWT.NONE);
		lblTypes.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTypes.setBounds(10, 95, 57, 20);
		lblTypes.setText("Types");
		
		textTypes = new Text(grpPackages, SWT.BORDER);
		textTypes.setBounds(141, 92, 123, 26);
		formToolkit.adapt(textTypes, true, true);
		textTypes.setText(appProperties.get("type"));
		
		Label lblTypeName = new Label(grpPackages, SWT.NONE);
		lblTypeName.setBounds(10, 132, 81, 20);
		formToolkit.adapt(lblTypeName, true, true);
		lblTypeName.setText("Type Name");
		
		textTypeName = new Text(grpPackages, SWT.BORDER);
		textTypeName.setBounds(141, 126, 123, 26);
		formToolkit.adapt(textTypeName, true, true);
		
		Label lblServices = new Label(grpPackages, SWT.NONE);
		lblServices.setBounds(10, 164, 70, 20);
		formToolkit.adapt(lblServices, true, true);
		lblServices.setText("Services");
		
		Label lblCreateTableScript = new Label(shlPersisgen, SWT.NONE);
		lblCreateTableScript.setBounds(10, 311, 107, 15);
		formToolkit.adapt(lblCreateTableScript, true, true);
		lblCreateTableScript.setText("Create Table Script");
		
		textServices = new Text(grpPackages, SWT.BORDER);
		textServices.setBounds(141, 158, 123, 26);
		formToolkit.adapt(textServices, true, true);
		textServices.setText(appProperties.get("service"));
		
		Group grpOptions = new Group(shlPersisgen, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setBounds(293, 65, 181, 162);
		formToolkit.adapt(grpOptions);
		formToolkit.paintBordersFor(grpOptions);
		
		Button btnMybatis = new Button(grpOptions, SWT.CHECK);
		btnMybatis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnMybatis.getSelection()) {
					textMappers.setEnabled(true);
				} else {
					textMappers.setEnabled(false);
				}
			}
		});
		btnMybatis.setBounds(10, 41, 111, 20);
		formToolkit.adapt(btnMybatis, true, true);
		btnMybatis.setText("MyBatis");
		if (appProperties.get("batis").equals("true")) {
			btnMybatis.setSelection(true);
		}
		if (appProperties.get("batis").equals("false")) {
			btnMybatis.setSelection(false);
		}
		
		Button btnHibernate = new Button(grpOptions, SWT.CHECK);
		btnHibernate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnHibernate.getSelection()) {
					textRepos.setEnabled(true);
				} else {
					textRepos.setEnabled(false);
				}
			}
		});
		btnHibernate.setBounds(10, 78, 111, 20);
		formToolkit.adapt(btnHibernate, true, true);
		btnHibernate.setText("Hibernate");
		if (appProperties.get("hibernate").equals("true")) {
			btnHibernate.setSelection(true);
		}
		if (appProperties.get("hibernate").equals("false")) {
			btnHibernate.setSelection(false);
		}
		
		Button btnServices = new Button(grpOptions, SWT.CHECK);
		btnServices.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnServices.getSelection()) {
					textServices.setEnabled(true);
				} else {
					textServices.setEnabled(false);
				}
			}
		});
		btnServices.setBounds(10, 112, 111, 20);
		formToolkit.adapt(btnServices, true, true);
		btnServices.setText("Services");
		if (appProperties.get("serviceImpl").equals("true")) {
			btnServices.setSelection(true);
		}
		if (appProperties.get("serviceImpl").equals("false")) {
			btnServices.setSelection(false);
		}
		
		Group grpSql = new Group(shlPersisgen, SWT.NONE);
		grpSql.setText("SQL");
		grpSql.setBounds(480, 65, 177, 162);
		formToolkit.adapt(grpSql);
		formToolkit.paintBordersFor(grpSql);
		
		Combo combo = new Combo(grpSql, SWT.READ_ONLY);
		combo.setItems(new String[] {"MySQL", "Postgres"});
		combo.setBounds(10, 37, 157, 28);
		formToolkit.adapt(combo);
		formToolkit.paintBordersFor(combo);
		combo.select(0);
		String selection = appProperties.get("sql");
		if (!selection.equals("")) {
			combo.select(Integer.parseInt(selection));
		}
		
		Label label = new Label(grpSql, SWT.NONE);
		label.setBounds(10, 65, 70, 20);
		formToolkit.adapt(label, true, true);
		
		textScript = new Text(shlPersisgen, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textScript.setBounds(10, 330, 647, 280);
		formToolkit.adapt(textScript, true, true);		
		
		TabFolder tabFolder = new TabFolder(shlPersisgen, SWT.NONE);
		tabFolder.setBounds(10, 291, 647, 319);
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);
		tabFolder.setVisible(false);
		
		TabItem tabType = new TabItem(tabFolder, SWT.NONE);
		tabType.setText("Type");
		
		TabItem tabMpperJava = new TabItem(tabFolder, SWT.NONE);
		tabMpperJava.setText("Mapper - Java");
		
		TabItem tabMapperXml = new TabItem(tabFolder, SWT.NONE);
		tabMapperXml.setText("Mapper - XML");		
		
		TabItem tabRepo= new TabItem(tabFolder, SWT.NONE);
		tabRepo.setText("Repository");
		
		TabItem tabService = new TabItem(tabFolder, SWT.NONE);
		tabService.setText("Service Interface");
		
		TabItem tabServiceImpl = new TabItem(tabFolder, SWT.NONE);
		tabServiceImpl.setText("Service Implmentation");
		
		Button btnPreview = new Button(shlPersisgen, SWT.NONE);
		btnPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnPreview.getText().toLowerCase().equals("close")) {					
					btnPreview.setText("Preview");
					textScript.setVisible(true);
					tabFolder.setVisible(false);
				} else {
					btnPreview.setText("Close");
					textScript.setVisible(false);
					tabFolder.setVisible(true);
				}
			}
		});
		btnPreview.setEnabled(false);
		btnPreview.setBounds(107, 616, 90, 30);
		formToolkit.adapt(btnPreview, true, true);
		btnPreview.setText("Preview");
		
		textTypePreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabType.setControl(textTypePreview);
		
		textMapperJavaPreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabMpperJava.setControl(textMapperJavaPreview);
		
		textMapperXmlPreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabMapperXml.setControl(textMapperXmlPreview);
		
		textRepoPreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabRepo.setControl(textRepoPreview);
		
		textServicePreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabService.setControl(textServicePreview);
		
		textServiceImplPreview = new Text(tabFolder, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		tabServiceImpl.setControl(textServiceImplPreview);
		
		Button btnSave = new Button(shlPersisgen, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// get paths
				String root = textProjectRoot.getText().trim();
				String mapperPath = root + "\\" + textMappers.getText().trim().replace(".", "\\");
				String repoPath = root + "\\" + textRepos.getText().trim().replace(".", "\\");
				String typePath = root + "\\" + textTypes.getText().trim().replace(".", "\\");
				String servicePath = root + "\\" + textServices.getText().trim().replace(".", "\\");
				
				// file names
				// Auto Format or Not?
				// String type = inflector.capitalize(inflector.singularize(textTypeName.getText().trim()));
				String type = textTypeName.getText().trim();
				if (type.equals("")) {
					MessageDialog.openError(shlPersisgen, "Error", "Your Type is missing the name.");
					return;
				}
				
				String mapperJavaName = type + "Mapper.java";
				String mapperXmlName = type + "Mapper.xml";
				String repoName = type + "Repository.java";
				String typeName = type + ".java";
				String serviceName = type + "Service.java";
				String serviceImplName = type + "ServiceImpl.java";
				
				try {
					if (!textTypePreview.getText().equals("")) {
						Files.write(Paths.get(typePath + "\\" + typeName), textTypePreview.getText().getBytes());
					}
					
					if (btnMybatis.getSelection()) {
						if (!textMapperJavaPreview.getText().equals("")) {
							Files.write(Paths.get(mapperPath + "\\" + mapperJavaName), textMapperJavaPreview.getText().getBytes());
							Files.write(Paths.get(mapperPath + "\\" + mapperXmlName), textMapperXmlPreview.getText().getBytes());
						}
					}
					
					if (btnHibernate.getSelection()) {
						if (!textRepoPreview.getText().equals("")) {
							Files.write(Paths.get(repoPath + "\\" + repoName), textRepoPreview.getText().getBytes());
						}
					}
					
					if (btnServices.getSelection()) {
						if (!textServicePreview.getText().equals("")) {
							Files.write(Paths.get(servicePath + "\\" + serviceName), textServicePreview.getText().getBytes());
						}
						if (!textServiceImplPreview.getText().equals("")) {
							Files.write(Paths.get(servicePath + "\\" + serviceImplName), textServiceImplPreview.getText().getBytes());
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(shlPersisgen, "Error", "Error saving a file to one of the directories.");
				}				
			}
		});		
		btnSave.setEnabled(false);
		btnSave.setBounds(567, 616, 90, 30);
		formToolkit.adapt(btnSave, true, true);
		btnSave.setText("Save");
		
		Button btnGenerate = new Button(shlPersisgen, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// lets do some error handling before passing our data off to the code gen
				boolean go = true;
				String msg = "";
				if (textRepos.getText().equals("") && btnHibernate.getSelection()) {
					go = false;
					msg = "Please enter a Package name for your Hibernate Repositories";
				} else if (textMappers.getText().equals("") && btnMybatis.getSelection()) {
					go = false;
					msg = "Please enter a Package name for your MyBatis Mappers";
				}else if (textServices.getText().equals("") && btnServices.getSelection()) {
					go = false;
					msg = "Please enter a Package name for your Services";
				} else if (textProjectRoot.getText().equals("")) {
					go = false;
					msg = "Please enter a Project Root Path";
				} else if (textTypes.getText().equals("")) {
					go = false;
					msg = "Please enter a Package name for your Types";
				} else if (textScript.getText().equals("")) {
					go = false;
					msg = "Please enter a \"CREATE TABLE\" Script to Generate your code";
				} else if (textTypeName.getText().equals("")) {
					go = false;
					msg = "Type Name is Required";
				}
				
				// verify paths
				String root = textProjectRoot.getText().trim();
				String mapper = root + "\\" + textMappers.getText().trim().replace(".", "\\");
				String repo = root + "\\" + textRepos.getText().trim().replace(".", "\\");
				String type = root + "\\" + textTypes.getText().trim().replace(".", "\\");
				String service = root + "\\" + textServices.getText().trim().replace(".", "\\");
				
				
				if (!Files.isDirectory(Paths.get(root)) && go) {
					go = false;
					msg = "Root path is invalid";
				} 
				if (!Files.isDirectory(Paths.get(mapper)) && go) {
					if (btnMybatis.getSelection()) {
						go = false;
						msg = "Mapper package is invalid";
					}					
				} 
				if (!Files.isDirectory(Paths.get(repo)) && go) {
					if (btnHibernate.getSelection()) {
						go = false;
						msg = "Repository package is invalid";
					}					
				}
				if (!Files.isDirectory(Paths.get(type)) && go) {
					go = false;
					msg = "Type package is invalid";
				} 
				if (!Files.isDirectory(Paths.get(service)) && go) {
					if (btnServices.getSelection()) {
						go = false;
						msg = "Services package is invalid";
					}					
				}
				
				// Show user their mistake
				if(!go) {
					MessageDialog.openError(shlPersisgen, "Error", msg);
				} else { // we have some information to gather before we start code gen
					
					// save all the inputs for next usage since we know everything is valid as of now
					appProperties.set("path", textProjectRoot.getText());
					appProperties.set("mapper", textMappers.getText());
					appProperties.set("repo", textRepos.getText());
					appProperties.set("type", textTypes.getText());
					appProperties.set("service", textServices.getText());
					appProperties.set("sql", ""+combo.getSelectionIndex());
					appProperties.set("batis", ""+btnMybatis.getSelection());
					appProperties.set("hibernate", ""+btnHibernate.getSelection());
					appProperties.set("serviceImpl", ""+btnServices.getSelection());
					appProperties.save();
					
					// Set our parser objects
					TypeParser typeParser = new TypeParser(					
						combo.getSelectionIndex(),
						textScript.getText(),
						btnHibernate.getSelection(),
						type,
						textTypeName.getText()
					);
					
					MapperParser mapperParser = new MapperParser(
						combo.getSelectionIndex(),
						textScript.getText(),
						mapper,
						type,
						textTypeName.getText()						
					);
					
					RepoParser repoParser = new RepoParser(
						repo,
						type,
						textTypeName.getText()
					);
					
					ServiceParser serviceParser = new ServiceParser(
						combo.getSelectionIndex(),
						textScript.getText(),
						btnHibernate.getSelection(),
						btnMybatis.getSelection(),
						service,
						repo,
						mapper,
						type,
						textTypeName.getText()
					);
					
					// run parsers
					if (typeParser.run()) {
						textTypePreview.setText(typeParser.getGenerated());
					} else {
						textTypePreview.setText("");
						MessageDialog.openError(shlPersisgen, "Error", "Error Parsing Type, please check SQL.");
					}
					
					if (btnMybatis.getSelection()) {
						if (mapperParser.run()) {
							textMapperJavaPreview.setText(mapperParser.getGeneratedJava());
							textMapperXmlPreview.setText(mapperParser.getGeneratedXml());
						} else {
							textMapperJavaPreview.setText("");
							MessageDialog.openError(shlPersisgen, "Error", "Error Parsing Mappers, please check SQL.");
						}
					}					
					
					if (btnHibernate.getSelection()) {
						if (repoParser.run()) {
							textRepoPreview.setText(repoParser.getGenerated());							
						} else {
							textRepoPreview.setText("");
							MessageDialog.openError(shlPersisgen, "Error", "Error Parsing Repository.");
						}
					}
					
					if (btnServices.getSelection()) {
						if (serviceParser.run()) {
							textServicePreview.setText(serviceParser.getGeneratedService());
							textServiceImplPreview.setText(serviceParser.getGeneratedServiceImpl());
							
						} else {
							textServicePreview.setText("");
							textServiceImplPreview.setText("");
							MessageDialog.openError(shlPersisgen, "Error", "Error Parsing Services.");
						}
					}
					
					// update view for preview
					lblCreateTableScript.setVisible(false);
					btnPreview.setEnabled(true);
					btnPreview.setText("Close");
					textScript.setVisible(false);
					tabFolder.setVisible(true);
					btnSave.setEnabled(true);
				}
			}
		});
		btnGenerate.setBounds(10, 616, 90, 30);
		formToolkit.adapt(btnGenerate, true, true);
		btnGenerate.setText("Generate");		
	}
	
	// Helper class for app properties
	public static class appProperties {
		private static Properties appProps = new Properties();
		private static FileInputStream input;
		private static FileOutputStream output;
		
		public static boolean load() {
			try {
	    		input = new FileInputStream("app.properties");
	    		appProps.load(input);
	    		input.close();
	    		input = null;
	    		return true;
	    	} catch (Exception e) {
	    		return false;
	    	}
		}
		
		public static void set(String key, String value) {
			appProps.setProperty(key, value);
		}
		
		public static String get(String key) {
			return appProps.getProperty(key) != null ? appProps.getProperty(key) : "";
		}
		
		public static boolean save() {
			try {
				output = new FileOutputStream("app.properties");
				appProps.store(output, "");
				output.close();
				output = null;
				return true;
			} catch (Exception e) {
				return false;
			}			
		}
	}
}

