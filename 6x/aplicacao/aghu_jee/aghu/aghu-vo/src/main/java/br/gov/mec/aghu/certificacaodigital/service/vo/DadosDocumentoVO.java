package br.gov.mec.aghu.certificacaodigital.service.vo;

import java.io.Serializable;

/**
 * Representa dados de AGH_VERSOES_DOCUMENTOS e AGH_DOCUMENTOS
 * 
 * @author luismoura
 * 
 */
public class DadosDocumentoVO implements Serializable {
	private static final long serialVersionUID = 277579350706646583L;

	private Integer docSeq;
	private Integer verSeq;

	public Integer getDocSeq() {
		return docSeq;
	}

	public void setDocSeq(Integer docSeq) {
		this.docSeq = docSeq;
	}

	public Integer getVerSeq() {
		return verSeq;
	}

	public void setVerSeq(Integer verSeq) {
		this.verSeq = verSeq;
	}
}
