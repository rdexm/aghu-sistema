package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

public class MensagemLaudoExameVO implements Serializable {
	
	private static final long serialVersionUID = 3487495895819393L;
	
	private Integer seqSolicitacaoExame;
	private Integer seqExameInternetGrupo;
	private byte[] arquivoLaudo;
	private String xmlEnvio;
	private String localizador;
	
	public Integer getSeqSolicitacaoExame() {
		return seqSolicitacaoExame;
	}

	public void setSeqSolicitacaoExame(Integer seqSolicitacaoExame) {
		this.seqSolicitacaoExame = seqSolicitacaoExame;
	}

	public Integer getSeqExameInternetGrupo() {
		return seqExameInternetGrupo;
	}

	public void setSeqExameInternetGrupo(Integer seqExameInternetGrupo) {
		this.seqExameInternetGrupo = seqExameInternetGrupo;
	}

	public byte[] getArquivoLaudo() {
		return arquivoLaudo;
	}

	public void setArquivoLaudo(byte[] arquivoLaudo) {
		this.arquivoLaudo = arquivoLaudo;
	}

	public String getXmlEnvio() {
		return xmlEnvio;
	}

	public void setXmlEnvio(String xmlEnvio) {
		this.xmlEnvio = xmlEnvio;
	}

	public String getLocalizador() {
		return localizador;
	}

	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}	
	
}
