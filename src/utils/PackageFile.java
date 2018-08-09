package utils;

public class PackageFile {
	private String fileDesc;
	private boolean isDir;

	public PackageFile(String fileDesc, boolean isDir) {
		super();
		this.fileDesc = fileDesc;
		this.isDir = isDir;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public String getAixFileDesc() {
		if (fileDesc.contains("$")) {
			String s = fileDesc.replaceAll("\\$", "\\\\\\$");
			return s;
		} else {
			return fileDesc;
		}
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}
}
