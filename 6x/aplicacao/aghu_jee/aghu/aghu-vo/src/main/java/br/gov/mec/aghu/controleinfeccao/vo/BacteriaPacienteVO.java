package br.gov.mec.aghu.controleinfeccao.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de #37928 - Bactéria (suggestionBox) da lista de Germes Multirresistentes
 * do Paciente
 * 
 * @author aghu
 *
 */

public class BacteriaPacienteVO implements BaseBean {

	private static final long serialVersionUID = -9199565746133919464L;

	private Integer seq; // BMR.SEQ
	private String descricao; // BMR.DESCRICAO
	private Integer bmnSeq; // BMN.SEQ
	private String bmnDescricao; // BMN.DESCRICAO

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getBmnSeq() {
		return bmnSeq;
	}

	public void setBmnSeq(Integer bmnSeq) {
		this.bmnSeq = bmnSeq;
	}

	public String getBmnDescricao() {
		return bmnDescricao;
	}

	public void setBmnDescricao(String bmnDescricao) {
		this.bmnDescricao = bmnDescricao;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getBmnSeq());
		umHashCodeBuilder.append(this.getBmnDescricao());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BacteriaPacienteVO)) {
			return false;
		}
		BacteriaPacienteVO other = (BacteriaPacienteVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getBmnSeq(), other.getBmnSeq());
		umEqualsBuilder.append(this.getBmnDescricao(), other.getBmnDescricao());
		return umEqualsBuilder.isEquals();
	}

}
