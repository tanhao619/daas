package com.youedata.daas.rest.util;

import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class HdfsUtil {

	private ThreadLocal<FileSystem> fileSystemThreadLocal = new ThreadLocal<FileSystem>();

	private Configuration conf = null;

	static public String BasePath = ToolUtil.getCustomPropertyValue("HDFS_ROOT");

	private HdfsUtil() {
		System.setProperty("HADOOP_USER_NAME", ToolUtil.getCustomPropertyValue("HDFS_USER"));
		conf = new Configuration();
		conf.addResource("core-site.xml");
		conf.addResource("hdfs-site.xml");
	}

	public static HdfsUtil getInstance() {
		return HdfsUtilHold.instance;
	}

	private static class HdfsUtilHold {
		private static final HdfsUtil instance = new HdfsUtil();
	}


	private FileSystem getFileSystem() throws IOException {
		if (fileSystemThreadLocal.get() != null) {
			return fileSystemThreadLocal.get();
		} else {
			FileSystem fileSystem = connect(); // 构造一个FileSystem实例
			fileSystemThreadLocal.set(fileSystem);
			return fileSystem;
		}
	}

	private FileSystem connect() throws IOException {

		try {
			return FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void disconnect() throws BussinessException {
		try {
			FileSystem fileSystem = getFileSystem();
			if (fileSystem != null) {
				fileSystem = null;
			}
		} catch (Exception e) {
			throw new BussinessException(BizExceptionEnum.HDFS_DISCONNECT_ERROR);
		} finally {
			fileSystemThreadLocal.remove();
		}
	}
	
	//创建文件
	public void createFile(String fileName, String fileContent) throws IOException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		FSDataOutputStream output = fileSystem.create(dst);
		if(fileContent !=null){
			byte[] bytes = fileContent.getBytes();
			output.write(bytes);
		}
		output.close();
	}

	public void create(String fileName , InputStream inputStream) throws IOException {
		Path dst = new Path(BasePath +fileName);
		FileSystem fileSystem = getFileSystem();
		FSDataOutputStream outputStream = fileSystem.create(dst);
		IOUtils.copyBytes(inputStream, outputStream, fileSystem.getConf(), false);
		if (outputStream != null) {
			outputStream.close();
		}
	}
	
	//删除文件
	public boolean deleteFile(String fileName) throws IOException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		boolean ret = fileSystem.delete(dst, false);
		return ret;
	}
	
	//打开文件
	public InputStream openFile(String fileName) throws IOException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		InputStream is = fileSystem.open(dst);
		return is;
	}
	
	//重命名文件
	public boolean renameFile(String fileName, String newFileName) throws IOException {
		boolean ret = false;
		FileSystem fileSystem = getFileSystem();
		Path p = new Path(BasePath + "/" + fileName);
		Path newP = new Path(BasePath + "/" + newFileName);
		ret =  fileSystem.rename(p,newP);
		return ret;
	}
	
}