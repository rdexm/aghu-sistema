package br.gov.mec.aghu.core.dominio;

/**
 * Domínio com os MIME TYPES suportados pelo AGHU.
 * 
 * Mapear outros tipos e Implementar em <class>MECRelatorioController</class>.
 * 
 * @author riccosta
 */
public enum DominioMimeType {

	/**
	 * PDF
	 */
	PDF("pdf", "application/pdf"),

	/**
	 * XLS
	 */
	XLS("xls", "application/vnd.ms-excel"),
	
	/**
	 * XLS
	 */
	CSV("csv", "text/csv"),
	
	/**
     * TXT
     */
    TXT("txt", "text/plain");

	private String extension;

	private String contentType;

	private DominioMimeType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

}
