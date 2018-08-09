package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 自动生成打包及部署脚本配置信息装载
 * @author Wang.Pang 20170912
 */
public class PackageConfiger {
	
	// 基线版本目录
	private String baseLine;
	// 最新版本目录
	private String head;
	// Dep 基线目录
	private String baseLineDep;
	// Dep 最新版本目录
	private String headDep;
	// 脚本主目录
	private String workPath;
	// 升级版本
	private String version;
	// 记录忽略文件列表的文件
	private String ingnoreFiles;
	// 输出目录
	private String outputPath;
	
	public PackageConfiger() throws IOException{
		// 初始化配置
		Properties properties = new Properties();
		InputStream in = PackageConfiger.class.getResourceAsStream("/source/package.properties");
		properties.load(in);
		this.baseLine = properties.getProperty("baseLine");
		this.head = properties.getProperty("head");
		this.baseLineDep = properties.getProperty("baseLineDep");
		this.headDep = properties.getProperty("headDep");
		this.workPath = properties.getProperty("workPath");
		this.version = properties.getProperty("version");
		this.outputPath = properties.getProperty("outputPath");
		this.ingnoreFiles = properties.getProperty("ingnoreFiles");
		if(this.workPath != null && !this.workPath.equals("")){
			this.outputPath = this.workPath + File.separator + this.outputPath;
			this.ingnoreFiles = this.workPath + File.separator + this.ingnoreFiles;
		}
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

	public String getBaseLineDep() {
		return baseLineDep;
	}

	public void setBaseLineDep(String baseLineDep) {
		this.baseLineDep = baseLineDep;
	}

	public String getHeadDep() {
		return headDep;
	}

	public void setHeadDep(String headDep) {
		this.headDep = headDep;
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

	@Override
	public String toString() {
		return "AutoGenPackageScript [baseLine=" + baseLine + ", head=" + head
				+ ", ingnoreFiles=" + ingnoreFiles + ", outputPath="
				+ outputPath + ", version=" + version + ", workPath="
				+ workPath + "]";
	}
}
