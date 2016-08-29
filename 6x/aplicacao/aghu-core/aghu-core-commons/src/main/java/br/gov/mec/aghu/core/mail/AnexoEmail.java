package br.gov.mec.aghu.core.mail;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author dlaks
 * 
 */
public class AnexoEmail implements Serializable {

	private static final long serialVersionUID = 804293698094761109L;
	
	private String fileName;
	
	private File file;

	private byte[] byteArray;

	private InputStream inputStream;

	private String mimeType;

	public AnexoEmail(String fileName, File file) {
		this.fileName = fileName;
		this.file = file;
	}

	public AnexoEmail(String fileName, byte[] byteArray, String mimeType) {
		this.fileName = fileName;
		this.byteArray = byteArray;
		this.mimeType = mimeType;
	}

	public AnexoEmail(String fileName, InputStream inputStream, String mimeType) {
		this.fileName = fileName;
		this.inputStream = inputStream;
		this.mimeType = mimeType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Object getValue() {
		if (this.file != null) {
			return this.file;
		} else if (this.byteArray != null) {
			return this.byteArray;
		} else if (this.inputStream != null) {
			return this.inputStream;
		} else {
			return null;
		}
	}

	public String getContentType() {
		return this.mimeType != null ? this.mimeType : "";
	}

}
