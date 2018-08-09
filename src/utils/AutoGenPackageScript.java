package utils;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 自动生成打包及部署脚本
 * @author Wang.Pang 20170912
 */
public class AutoGenPackageScript {
	
	private static MessageDigest md;
	
	/**
	 * 程序入口
	 */
	public static void main(String[] args) {
		try {
			// 1. 初始化类和配置
			PackageConfiger configer = new PackageConfiger();
			System.out.println("加载配置信息：");
			System.out.println(configer);
			
			// 2. 对比文件，得出差异文件列表
			Map<String,List<File>> result = getDifferentFile(configer);
			
			
			// 3. 将差异文件复制到一个路径下
			handleDifferentFiles(result);
			
			// 4. 打包差异文件
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 使用非递归的方式遍历对比文件（包含子文件）
	 * create by Wang.Pang 20171016
	 * @param configer {@code PackageConfiger}
	 * @return 差异文件map<*,List<File>>  *=A:新增  , *=D:删除 ,*=M:修改
	 */
	public static Map<String,List<File>> getDifferentFile(PackageConfiger configer){
		// 0. 申请保存对比结果的变量
		Map<String,List<File>> result = new HashMap<String,List<File>>();
		result.put("A", new ArrayList<File>());//head 新增
		result.put("D", new ArrayList<File>());//head 删除
		result.put("M", new ArrayList<File>());//head 修改
		// 1. 获取对比文件夹
		File baseFile = new File(configer.getBaseLine());
		File headFile = new File(configer.getHead());
		// 2. 添加到对比对象集合中
		LinkedHashMap<File, File> subFolders = new LinkedHashMap<File, File>();
		subFolders.put(baseFile, headFile);
	    
		// 3. 申请临时变量
	    File tempBase;
	    File tempHead;
	    File[] tempBaseForder;
	    File[] tempHeadForder;
	    File tempComperFile;
	    
	    // 4. 非递归的方式进行文件夹对比
	    while(!subFolders.isEmpty()){
	    	for (Entry<File, File> subForderPair : subFolders.entrySet()) {
	    		tempBase = subForderPair.getKey();
	    		tempHead = subForderPair.getValue();
	    		// 4.1.   文件夹对比处理
		    	if(tempBase.isDirectory()){
		    		tempBaseForder = tempBase.listFiles();
		    		tempHeadForder = tempHead.listFiles();
		    		
		    		// 4.1.1. 对于base
		    		for (File file : tempBaseForder) {
		    			tempComperFile = new File(tempHead.getAbsoluteFile() + file.getName());
		    			// base 和head 中存在相同的文件夹，比较两个文件夹是否一致，不一致添加到链表中，进行下一级对比，一致不做处理
		    			if(file.isDirectory() && tempComperFile.isDirectory()){
		    				if(!compareFileWithMd5(file,tempComperFile)){
		    					subFolders.put(file, tempComperFile);
		    				}
		    		    // base 和head 中存在相同的文件，比较两个文件是否一致，不一致添加差异文件列表，一致不做处理
		    			}else if(file.isFile() && tempComperFile.isFile()){
		    				if(!compareFileWithMd5(file,tempComperFile)){
		    					result.get("M").add(tempComperFile);
		    				}
		    			// base 比head 多出的文件，添加到差异文件列表
		    			}else if(file.exists() && !tempComperFile.exists()){
		    				result.get("D").add(file);
		    			}
					}
		    		// 4.1.2. 对于head
		    		for (File file : tempHeadForder) {
		    			tempComperFile = new File(tempBase.getAbsoluteFile() + tempBase.getName());
		    			// head 比base 多出的文件，添加到差异文件列表
		    			if(file.exists() && !tempComperFile.exists()){
		    				result.get("A").add(file);
		    			}
					}
		    		
		        // 4.2. 文件对比处理
		    	}else{
		    		// base 和head 中存在相同的文件，比较两个文件是否一致，不一致添加差异文件列表，一致不做处理
		    		if(!compareFileWithMd5(tempBase,tempHead)){
    					result.get("M").add(tempHead);
    				}
		    	}
		    	// 4.3. 移除对比过的文件对
		    	subFolders.remove(tempBase);
			}
	    }
		// 5. 返回对比结果
		return result;
	}
	
	/**
	 * 使用md5对比两个文件是否一致
	 * create by Wang.Pang 20171017
	 * @param sourcefile {@code File} 源文件
	 * @param targerFile {@code File} 目标文件
	 * @return true：一致 false：不一致
	 */
	public static boolean compareFileWithMd5(File sourcefile,File targerFile){
		return true;
	}
	
	public static String getFileMD5(File file){
		String mdString = null;
		if(file.isFile()){
			try {
				md = MessageDigest.getInstance("MD5");
				
				return mdString;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	public static boolean handleDifferentFiles(Map<String,List<File>> differentFiles){
		
		return true;
	}
}
