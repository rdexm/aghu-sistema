package br.gov.mec.aghu.controleinfeccao;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VMciMvtoPacsVO {

	private Short unfSeq;
	private String ltoLtoId;
	private Short qrtNumero;
	private Date dthrLancamento;
	private Integer pacodigo;

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public Integer getPacodigo() {
		return pacodigo;
	}

	public void setPacodigo(Integer pacodigo) {
		this.pacodigo = pacodigo;
	}
	
	public enum Fields {
		
		PAC_CODIGO("pacodigo"),
		DTHR_LANCAMENTO("dthrLancamento"),
		QUARTO_NUMERO("qrtNumero"),
		LEITO_ID("ltoLtoId"),
		UNF_SEQ("unfSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
       
        hashCodeBuilder.append(this.unfSeq);
        hashCodeBuilder.append(this.ltoLtoId);
        hashCodeBuilder.append(this.qrtNumero);
        hashCodeBuilder.append(this.dthrLancamento);
        hashCodeBuilder.append(this.pacodigo);
       
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
       
        VMciMvtoPacsVO other = (VMciMvtoPacsVO) obj;
       
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.unfSeq, other.unfSeq);
        umEqualsBuilder.append(this.ltoLtoId, other.ltoLtoId);
        umEqualsBuilder.append(this.qrtNumero, other.qrtNumero);
        umEqualsBuilder.append(this.dthrLancamento, other.dthrLancamento);
        umEqualsBuilder.append(this.pacodigo, other.pacodigo);
       
        return umEqualsBuilder.isEquals();
    }

}
