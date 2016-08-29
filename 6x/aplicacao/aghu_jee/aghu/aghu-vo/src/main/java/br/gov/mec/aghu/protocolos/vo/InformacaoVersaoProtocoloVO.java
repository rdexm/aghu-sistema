package br.gov.mec.aghu.protocolos.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;

public class InformacaoVersaoProtocoloVO implements Serializable {

	private String tituloProtocoloSessao;
	private Integer versao;
	private DominioSituacaoProtocolo indSituacaoVersaoProtocoloSessao;
	private Integer qdtCiclo;
	private Short tpsSeq;
	private Integer seq;
	
	
	public Integer getQdtCiclo() {
		return qdtCiclo;
	}

	public void setQdtCiclo(Integer qdtCiclo) {
		this.qdtCiclo = qdtCiclo;
	}

	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public String getTituloProtocoloSessao() {
		return tituloProtocoloSessao;
	}

	public void setTituloProtocoloSessao(String tituloProtocoloSessao) {
		this.tituloProtocoloSessao = tituloProtocoloSessao;
	}

	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}

	public DominioSituacaoProtocolo getIndSituacaoVersaoProtocoloSessao() {
		return indSituacaoVersaoProtocoloSessao;
	}

	public void setIndSituacaoVersaoProtocoloSessao(
			DominioSituacaoProtocolo indSituacaoVersaoProtocoloSessao) {
		this.indSituacaoVersaoProtocoloSessao = indSituacaoVersaoProtocoloSessao;
	}


	public enum Fields {	
		TPS_SEQ("tpsSeq"),
		QTD_SEQ("qdtCiclo"),
		VERSAO("versao"),
		IND_SITUACAO("indSituacaoVersaoProtocoloSessao"),
		TITULO_PROTOCOLO("tituloProtocoloSessao"),
		SEQ("seq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
}
