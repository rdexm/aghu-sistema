package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UnidadeExamesSignificativoPerinatologiaVO implements Serializable {
	private static final long serialVersionUID = -7794774483228565156L;

	private Short unfSeq;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer eexSeq;
	private String descricao;

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getEexSeq() {
		return eexSeq;
	}

	public void setEexSeq(Integer eexSeq) {
		this.eexSeq = eexSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

    @Override
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getEexSeq());
        umHashCodeBuilder.append(this.getEmaExaSigla());
        umHashCodeBuilder.append(this.getEmaManSeq());
        umHashCodeBuilder.append(this.getUnfSeq());
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
        if (!(obj instanceof UnidadeExamesSignificativoPerinatologiaVO)) {
            return false;
        }
        UnidadeExamesSignificativoPerinatologiaVO other = (UnidadeExamesSignificativoPerinatologiaVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getEexSeq(), other.getEexSeq());
        umEqualsBuilder.append(this.getEmaExaSigla(), other.getEmaExaSigla());
        umEqualsBuilder.append(this.getEmaManSeq(), other.getEmaManSeq());
        umEqualsBuilder.append(this.getUnfSeq(), other.getUnfSeq());
        return umEqualsBuilder.isEquals();
    }

}
