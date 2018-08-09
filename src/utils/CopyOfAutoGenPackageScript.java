package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import utils.FileUtil;

/**
 * 自动生成打包及部署脚本
 * 
 * @author Liuxiaoyong
 * 
 */
public class CopyOfAutoGenPackageScript {
	
	// 忽略文件名列表
	public static final Set<String> SKIP_FILE_NAMES = new HashSet<String>(Arrays.asList(new String[] {//

			}));

	public static void main(String[] args) throws IOException {
		List<List<PackageFile>> rs = directoryCompare(".");
		outputResult(rs);
	}

	/**
	 * 输出结果
	 * 
	 * @param rs
	 * @throws IOException
	 */
	private static void outputResult(List<List<PackageFile>> rs) throws IOException {
		File outputFile = new File(OUTPUT_FILE);
		if (outputFile.exists()) {
			outputFile.delete();
		}
		outputFile.createNewFile();
		PrintWriter pw = new PrintWriter(outputFile);
		pw.println("=============================");
		pw.println("== Local has , base hasn`t ==");
		pw.println("=============================");
		printData(pw, rs.get(0));
		pw.println();
		pw.println("=============================");
		pw.println("== Base has , local hasn`t ==");
		pw.println("=============================");
		printData(pw, rs.get(1));
		pw.println();
		pw.println("===============");
		pw.println("== Different ==");
		pw.println("===============");
		printData(pw, rs.get(2));
		pw.close();

		// 生成打包脚本
		List<PackageFile> diff = new ArrayList<PackageFile>();
		diff.addAll(rs.get(0));
		diff.addAll(rs.get(2));
		genPackageScript(diff);

		// 生成备份脚本
		List<PackageFile> backup = new ArrayList<PackageFile>();
		backup.addAll(rs.get(1));
		backup.addAll(rs.get(2));
		genBackupScript(backup);

		// 生成升级脚本
		genUpdateScript(rs.get(1));

		// 生成回退脚本
		genRollbackScript(rs.get(0));
	}

	/**
	 * 查找dir1与dir2的不一致
	 * 
	 * @param dir1
	 * @param dir2
	 * @return
	 */
	public static List<List<PackageFile>> directoryCompare(String baseDir) {
		List<PackageFile> local = new ArrayList<PackageFile>();
		List<PackageFile> base = new ArrayList<PackageFile>();
		List<PackageFile> diff = new ArrayList<PackageFile>();

		File localFile = new File(LOCAL + baseDir);
		File baseFile = new File(BASELINE + baseDir);

		for (File localSubFile : localFile.listFiles()) {
			String localSubFileName = localSubFile.getName();
			File baseSubFile = new File(baseFile, localSubFileName);

			String currFile = baseDir + SEPRATOR + localSubFileName;
			if (SKIP_FILES.contains(currFile)) {
				continue;
			}

			if (baseSubFile.exists() == false) {
				local.add(new PackageFile(currFile, localSubFile.isDirectory()));
			} else {
				if (localSubFile.isDirectory() && baseSubFile.isDirectory()) {
					List<List<PackageFile>> result = directoryCompare(currFile);
					local.addAll(result.get(0));
					base.addAll(result.get(1));
					diff.addAll(result.get(2));
				} else if (localSubFile.isFile() && baseSubFile.isFile()) {
					if (fileCompare(localSubFile, baseSubFile) == false) {
						diff.add(new PackageFile(currFile, false));
					}
				} else {
					local.add(new PackageFile(currFile, localSubFile.isDirectory()));
					base.add(new PackageFile(currFile, localSubFile.isDirectory()));
				}
			}
		}

		for (File baseSubFile : baseFile.listFiles()) {
			String baseSubFileName = baseSubFile.getName();
			File localSubFile = new File(localFile, baseSubFileName);

			String currFile = baseDir + SEPRATOR + baseSubFileName;
			if (SKIP_FILES.contains(currFile)) {
				continue;
			}
			if (localSubFile.exists() == false) {
				base.add(new PackageFile(currFile, baseSubFile.isDirectory()));
			}
		}

		List<List<PackageFile>> result = new ArrayList<List<PackageFile>>();
		result.add(local);
		result.add(base);
		result.add(diff);
		return result;
	}

	private static void printData(PrintWriter pw, List<PackageFile> data) {
		for (PackageFile str : data) {
			pw.println(str.getFileDesc());
		}
	}

	private static boolean fileCompare(File file1, File file2) {
		if (file1.length() == file2.length()) {
			return FileUtil.getFileMD5Code(file1).equals(FileUtil.getFileMD5Code(file2));
		} else {
			return false;
		}
	}

	private static void genPackageScript(List<PackageFile> files) throws IOException {
		File file = new File(PACKAGE_FILE);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);
		pw.println("@echo off");
		pw.println("set app_name=" + UPDATE_PACKAGE_FILE_NAME);

		for (int i = 0; i < files.size(); i++) {
			if (i == 0) {
				pw.println("jar -cf %app_name% " + files.get(i).getFileDesc());
			} else {
				pw.println("jar -fu %app_name% " + files.get(i).getFileDesc());
			}
		}
		pw.close();
	}

	private static void genBackupScript(List<PackageFile> files) throws IOException {
		File file = new File(BACKUP_SCRIPT);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);

		pw.print("cd " + DEPLOY_DIR + "\n");

		for (int i = 0; i < files.size(); i++) {
			if (i == 0) {
				pw.print("/home/bea/jdk1.7.0_75/bin/jar -cvf ./" + BACKUP_FILE_NAME + " " + files.get(i).getAixFileDesc() + "\n");
			} else {
				pw.print("/home/bea/jdk1.7.0_75/bin/jar -fvu ./" + BACKUP_FILE_NAME + " " + files.get(i).getAixFileDesc() + "\n");
			}
		}

		pw.print("mv ./" + BACKUP_FILE_NAME + " " + UPDATE_DIR + SEPRATOR + "backup" + SEPRATOR + BACKUP_FILE_NAME + "\n");
		pw.close();
	}

	private static void genUpdateScript(List<PackageFile> files) throws IOException {
		File file = new File(UPDATE_SCRIPT);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);
		pw.print("cp " + UPDATE_DIR + "/resource/" + UPDATE_PACKAGE_FILE_NAME + " " + DEPLOY_DIR + "\n");
		pw.print("cd " + DEPLOY_DIR + "\n");
		for (PackageFile pf : files) {
			if (pf.isDir()) {
				pw.print("rm -rf " + pf.getAixFileDesc() + "\n");
			} else {
				pw.print("rm " + pf.getAixFileDesc() + "\n");
			}
		}
		pw.print("/home/bea/jdk1.7.0_75/bin/jar -xvf " + UPDATE_PACKAGE_FILE_NAME + "\n");
		pw.print("rm " + UPDATE_PACKAGE_FILE_NAME + "\n");
		pw.close();
	}

	private static void genRollbackScript(List<PackageFile> files) throws IOException {
		File file = new File(ROLLBACK_SCRIPT);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);

		pw.print("cp ./backup" + SEPRATOR + BACKUP_FILE_NAME + " " + DEPLOY_DIR + "\n");
		pw.print("cd " + DEPLOY_DIR + "\n");
		for (PackageFile f : files) {
			if (f.isDir()) {
				pw.print("rm -rf " + DEPLOY_DIR + f.getAixFileDesc().substring(1) + "\n");
			} else {
				pw.print("rm " + DEPLOY_DIR + f.getAixFileDesc().substring(1) + "\n");
			}
		}

		pw.print("/home/bea/jdk1.7.0_75/bin/jar -xvf ./" + BACKUP_FILE_NAME + "\n");
		pw.print("rm ./" + BACKUP_FILE_NAME + "\n");
		pw.close();
	}
	
	// 基线版本目录
	private String baseLine;
	// 最新版本目录
	private String head;
	// 脚本主目录
	private String workPath;
	// 升级版本
	private static String version="dfdfd";
	// 记录忽略文件列表的文件
	private String ingnoreFiles;
	// 输出目录
	private String outputPath;
	
	
	// 本地部署地址
	public static final String LOCAL = "D:/buShuST/LOCAL/CIM";
	// 已上线部署基线
	public static final String BASELINE = "D:/buShuST/BASELINE/CIM";
	// 目录分隔符
	public static final String SEPRATOR = "/";
	// 输出结果
	public static final String OUTPUT_FILE = "C:/RESULT.txt";
	// 打包脚本
	public static final String PACKAGE_FILE = LOCAL + "/package.bat";

	// 系统版本
//	public static final String version = "v2.5.0";
	// 服务器升级路径
	public static final String UPDATE_DIR = "/home/bea/update/`date +%Y%m%d`_" + version;
	// 服务器部署路径
	public static final String DEPLOY_DIR = "/home/bea/cim_web/CIM";

	// 备份脚本
	public static final String BACKUP_SCRIPT = "C:/UPDATE/" + version + "/服务器/backup.sh";
	// 备份文件名称0000000000000000000000000000000000000000000000000000
	public static final String BACKUP_FILE_NAME = "backup.jar";
	// 升级脚本
	public static final String UPDATE_SCRIPT = "C:/UPDATE/" + version + "/服务器/update.sh";
	// 升级包名称
	public static final String UPDATE_PACKAGE_FILE_NAME = "update_CIM_" + version + ".jar";
	// 回退脚本
	public static final String ROLLBACK_SCRIPT = "C:/UPDATE/" + version + "/服务器/rollback.sh";

	// 忽略文件列表
	public static final Set<String> SKIP_FILES = new HashSet<String>(Arrays.asList(new String[] { //
			"./" + UPDATE_PACKAGE_FILE_NAME,//
			"./WEB-INF/classes/jsp_servlet", //
			"./WEB-INF/REDEPLOY",//
			"./etc/cfg/cfgdefine.txt",//
			"./META-INF",//
			//"./WEB-INF/web.xml",//
			// "./WEB-INF/classes/com/css/cfets/common/cfg/CFG.class",//
			"./package.bat",//
			"./WEB-INF/classes/config/config.prop",//
			"./WEB-INF/lib/junit.jar",//
			"./WEB-INF/lib/cnaps.jar",//
	"./WEB-INF/lib/org.hamcrest.core_1.1.0.v20090501071000.jar" }));
	
	public CopyOfAutoGenPackageScript() throws IOException{
		Properties properties = new Properties();
		InputStream in = CopyOfAutoGenPackageScript.class.getResourceAsStream("/source/package.properties");
		properties.load(in);
		this.baseLine = properties.getProperty("baseLine");
		this.head = properties.getProperty("head");
		this.workPath = properties.getProperty("workPath");
		this.version = properties.getProperty("version");
		this.outputPath = properties.getProperty("outputPath");
		this.ingnoreFiles = properties.getProperty("ingnoreFiles");
	}

	public String getBaseLine() {
		return baseLine;
	}

	public void setBaseLine(String baseLine) {
		this.baseLine = baseLine;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIngnoreFiles() {
		return ingnoreFiles;
	}

	public void setIngnoreFiles(String ingnoreFiles) {
		this.ingnoreFiles = ingnoreFiles;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
}
