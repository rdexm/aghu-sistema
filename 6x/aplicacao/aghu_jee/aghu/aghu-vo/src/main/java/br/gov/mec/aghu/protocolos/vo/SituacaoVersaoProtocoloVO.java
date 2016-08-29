package br.gov.mec.aghu.protocolos.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;

public class SituacaoVersaoProtocoloVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700889142555309393L;
	private String tituloProtocoloSessao;
	private Integer versao;
	private DominioSituacaoProtocolo indSituacaoVersaoProtocoloSessao;
	private String nome;
	private Date criadoEm;
	private Integer seq;
	
	private Integer seqVersaoProtocoloSessao;
	
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		CRIADO_EM("criadoEm"),
		NOME("nome"),
		VERSAO("versao"),
		IND_SITUACAO("indSituacaoVersaoProtocoloSessao"),
		TITULO_PROTOCOLO("tituloProtocoloSessao"),
		SEQ("seq"),
		SEQ_VERSAO_PROTOCOLO_SESSAO("seqVersaoProtocoloSessao");
		
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

	public Integer getSeqVersaoProtocoloSessao() {
		return seqVersaoProtocoloSessao;
	}

	public void setSeqVersaoProtocoloSessao(Integer seqVersaoProtocoloSessao) {
		this.seqVersaoProtocoloSessao = seqVersaoProtocoloSessao;
	}
	
}
