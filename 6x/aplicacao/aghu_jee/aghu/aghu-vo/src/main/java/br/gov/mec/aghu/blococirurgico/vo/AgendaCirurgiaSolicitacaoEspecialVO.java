package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AgendaCirurgiaSolicitacaoEspecialVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 180879148215930135L;
	
	private Short nciSeq;
	private Short unfSeq;
	private Short seqp;
	private Integer identificador;
	private Integer agdSeq;
	
	private String nciDescricao;
	private String unfDescricao;
	private String mseDescricao;
	
	private Date criadoEm;

	public Short getNciSeq() {
		return nciSeq;
	}

	public void setNciSeq(Short nciSeq) {
		this.nciSeq = nciSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getIdentificador() {
		return identificador;
	}

	public void setIdentificador(Integer identificador) {
		this.identificador = identificador;
	}

	public String getNciDescricao() {
		return nciDescricao;
	}

	public void setNciDescricao(String nciDescricao) {
		this.nciDescricao = nciDescricao;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getMseDescricao() {
		return mseDescricao;
	}

	public void setMseDescricao(String mseDescricao) {
		this.mseDescricao = mseDescricao;
	}
	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}
	
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getIdentificador());
		builder.append(this.getNciSeq());
		builder.append(this.getSeqp());
		builder.append(this.getAgdSeq());
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AgendaCirurgiaSolicitacaoEspecialVO)) {
			return false;
		}
		
		AgendaCirurgiaSolicitacaoEspecialVO other = (AgendaCirurgiaSolicitacaoEspecialVO) obj;

		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.getIdentificador(), other.getIdentificador());
		builder.append(this.getNciSeq(), other.getNciSeq());
		builder.append(this.getSeqp(), other.getSeqp());
		return builder.isEquals();
	}
	

	public enum Fields {
		CRIADO_EM("criadoEm"), 
		MSE_DESCRICAO("mseDescricao"), 
		NCI_DESCRICAO("nciDescricao"), 
		NCI_SEQ("nciSeq"), 
		SEQP("seqp"), 
		UNF_DESCRICAO("unfDescricao"), 
		UNF_SEQ("unfSeq"),
		AGD_SEQ("agdSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}