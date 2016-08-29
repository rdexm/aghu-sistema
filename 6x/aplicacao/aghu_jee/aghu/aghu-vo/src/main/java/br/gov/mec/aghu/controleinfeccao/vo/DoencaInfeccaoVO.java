package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class DoencaInfeccaoVO implements BaseBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5461455482898210548L;
	private Integer seqPai;
	private String descPalavraChave;
	private String descPatologia;
	private String palavraChavePatologia;
	private Short seqP;
	private Short toiSeq;
	private DominioSituacao situacaoPatologia;
	private DominioSituacao situacaoPalavraChave;
	private Boolean indImpNotificacao;
	private Boolean indPermiteNotificacao;
	
	public static enum Fields {
		  SEQ_PAI("seqPai")
		, DESC_PALAVRA_CHAVE("descPalavraChave")
		, DESC_PATOLOGIA("descPatologia")
		, SEQP("seqP")
		, TOI_SEQ("toiSeq")
		, SITUACAO_PATOLOGIA("situacaoPatologia")
		, SITUACAO_PALAVRA_CHAVE("situacaoPalavraChave")
		, IND_IMP_NOTIFICACAO("indImpNotificacao")
		, IND_PERMITE_NOTIFICACAO("indPermiteNotificacao")
		, PALAVRA_CHAVE_PATOLOGIA("palavraChavePatologia")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
		
	}
	
	public Integer getSeqPai() {
		return seqPai;
	}
	public String getDescPalavraChave() {
		return descPalavraChave;
	}
	public String getDescPatologia() {
		return descPatologia;
	}
	public String getPalavraChavePatologia() {
		return palavraChavePatologia;
	}
	public Short getSeqP() {
		return seqP;
	}
	public Short getToiSeq() {
		return toiSeq;
	}
	public DominioSituacao getSituacaoPatologia() {
		return situacaoPatologia;
	}
	public DominioSituacao getSituacaoPalavraChave() {
		return situacaoPalavraChave;
	}
	public Boolean getIndImpNotificacao() {
		return indImpNotificacao;
	}
	public Boolean getIndPermiteNotificacao() {
		return indPermiteNotificacao;
	}
	public void setSeqPai(Integer seqPai) {
		this.seqPai = seqPai;
	}
	public void setDescPalavraChave(String descPalavraChave) {
		this.descPalavraChave = descPalavraChave;
	}
	public void setDescPatologia(String descPatologia) {
		this.descPatologia = descPatologia;
	}
	public void setPalavraChavePatologia(String palavraChavePatologia) {
		this.palavraChavePatologia = palavraChavePatologia;
	}
	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}
	public void setToiSeq(Short toiSeq) {
		this.toiSeq = toiSeq;
	}
	public void setSituacaoPatologia(DominioSituacao situacaoPatologia) {
		this.situacaoPatologia = situacaoPatologia;
	}
	public void setSituacaoPalavraChave(DominioSituacao situacaoPalavraChave) {
		this.situacaoPalavraChave = situacaoPalavraChave;
	}
	public void setIndImpNotificacao(Boolean indImpNotificacao) {
		this.indImpNotificacao = indImpNotificacao;
	}
	public void setIndPermiteNotificacao(Boolean indPermiteNotificacao) {
		this.indPermiteNotificacao = indPermiteNotificacao;
	}

}
