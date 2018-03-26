package com.youedata.daas.rest.common;


import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;

public class HdfsUtil {

	private ThreadLocal<FileSystem> fileSystemThreadLocal = new ThreadLocal<FileSystem>();

	private Configuration conf = null;

	static public String BasePath = ToolUtil.getCustomPropertyValue("HDFS_ROOT");

	private HdfsUtil() {
		System.setProperty("HADOOP_USER_NAME", ToolUtil.getCustomPropertyValue("HDFS_USER"));
		conf = new Configuration();
		conf.addResource("/core-site.xml");
		conf.addResource("/hdfs-site.xml");
	}

	public static HdfsUtil getInstance() {
		return HdfsUtilHold.instance;
	}

	private static class HdfsUtilHold {
		private static final HdfsUtil instance = new HdfsUtil();
	}


	private FileSystem getFileSystem() throws BussinessException {
		if (fileSystemThreadLocal.get() != null) {
			return fileSystemThreadLocal.get();
		} else {
			FileSystem fileSystem = connect(); // 构造一个FileSystem实例
			fileSystemThreadLocal.set(fileSystem);
			return fileSystem;
		}
	}

	private FileSystem connect() throws BussinessException {
		try {
			return FileSystem.get(conf);
		} catch (Exception e) {
			throw new BussinessException(BizExceptionEnum.HDFS_CONNECT_ERROR);
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
	public void createFile(String fileName, String fileContent) throws BussinessException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		try{
			FSDataOutputStream output = fileSystem.create(dst);
			if(fileContent !=null){
				byte[] bytes = fileContent.getBytes();
				output.write(bytes);
			}
			output.close();
		}catch(IOException ex){
			throw new BussinessException(BizExceptionEnum.HDFS_CREATE_ERROR);
		}
	}

	public void create(String fileName , InputStream inputStream) throws BussinessException{
		FSDataOutputStream outputStream = null;
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		try {
			outputStream = fileSystem.create(dst);
			IOUtils.copyBytes(inputStream, outputStream, fileSystem.getConf(), false);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BussinessException(BizExceptionEnum.HDFS_CREATE_ERROR);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				throw new BussinessException(BizExceptionEnum.HDFS_CREATE_ERROR);
			}
		}
	}

	//删除文件
	public boolean deleteFile(String fileName) throws BussinessException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		boolean ret = false;
		try{
			ret = fileSystem.delete(dst, false);
		}catch(IOException ex){
			throw new BussinessException(BizExceptionEnum.HDFS_DELETE_ERROR);
		}
		return ret;
	}

	//打开文件
	public InputStream openFile(String fileName) throws BussinessException {
		Path dst = new Path(BasePath + "/" +fileName);
		FileSystem fileSystem = getFileSystem();
		InputStream is = null;
		try{
			is = fileSystem.open(dst);
		}catch(Exception ex){
			throw new BussinessException(BizExceptionEnum.HDFS_OPEN_ERROR);
		}
		return is;
	}

	//重命名文件
	public boolean renameFile(String fileName, String newFileName) throws BussinessException {
		boolean ret = false;
		FileSystem fileSystem = getFileSystem();
		try{
			Path p = new Path(BasePath + "/" + fileName);
			Path newP = new Path(BasePath + "/" + newFileName);
			ret =  fileSystem.rename(p,newP);
		}catch(IOException e){
			throw new BussinessException(BizExceptionEnum.HDFS_RENAME_ERROR);
		}
		return ret;
	}

	/***
	 * 读取指定长度的字节
	 * @param ins
	 * @param sumLeng : 要读取的字节数
	 * @return
	 * @throws IOException
	 */
	public static byte[]readBytesFromInputStream(InputStream ins, long sumLeng) throws IOException {
		byte[] fileNameBytes = new byte[(int) sumLeng];
		int fileNameReadLength=0;
		int hasReadLength=0;//已经读取的字节数
		while((fileNameReadLength=ins.read(fileNameBytes,hasReadLength,(int)sumLeng-hasReadLength))>0){
			hasReadLength=hasReadLength+fileNameReadLength;
		}
		return fileNameBytes;
	}

	/**
	 * 获取前五行文件内容
	 * @param filePath
	 * @return
	 */
	public String getFileContent(String filePath, String ecode) {
		String txtFilePath = BasePath + "/" +filePath;
		String categoryFilterStrs = new String();
		BufferedReader br = null;
		try {
			FSDataInputStream inputStream = getFileSystem().open(new Path(txtFilePath));
			br = new BufferedReader(new InputStreamReader(inputStream, ecode));
			String line = null;
			int lineNo = 1;
			while (null != (line = br.readLine())) {
				String[] strs = line.split("\t");
				categoryFilterStrs += (strs[0] + "\n");
				if(lineNo++ == 5) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BussinessException(BizExceptionEnum.HDFS_READ_SPECIFIED_SIZE_ERROR);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BussinessException(BizExceptionEnum.HDFS_FILE_CLOSE_ERROR);
				}
			}
		}
		return categoryFilterStrs;
	}
}