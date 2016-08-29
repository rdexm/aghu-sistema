package br.gov.mec.aghu.prescricaomedica.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SubRelatorioMudancaProcedimentosVO {
	
	private String dataSaidaFormatada;
	private Integer mudProcSolic;
	private Integer mudProcRealiz;
	private String mudDescrSolic;
	private String mudDescrRealiz;

	public SubRelatorioMudancaProcedimentosVO() {
	}

	public String getDataSaidaFormatada() {
		return dataSaidaFormatada;
	}

	public void setDataSaidaFormatada(String dataSaidaFormatada) {
		this.dataSaidaFormatada = dataSaidaFormatada;
	}

	public Integer getMudProcSolic() {
		return mudProcSolic;
	}

	public void setMudProcSolic(Integer mudProcSolic) {
		this.mudProcSolic = mudProcSolic;
	}

	public Integer getMudProcRealiz() {
		return mudProcRealiz;
	}

	public void setMudProcRealiz(Integer mudProcRealiz) {
		this.mudProcRealiz = mudProcRealiz;
	}

	public String getMudDescrSolic() {
		return mudDescrSolic;
	}

	public void setMudDescrSolic(String mudDescrSolic) {
		this.mudDescrSolic = mudDescrSolic;
	}

	public String getMudDescrRealiz() {
		return mudDescrRealiz;
	}

	public void setMudDescrRealiz(String mudDescrRealiz) {
		this.mudDescrRealiz = mudDescrRealiz;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDataSaidaFormatada());
		umHashCodeBuilder.append(this.getMudProcSolic());
		umHashCodeBuilder.append(this.getMudProcRealiz());
		umHashCodeBuilder.append(this.getMudDescrSolic());
		umHashCodeBuilder.append(this.getMudDescrRealiz());
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
		if (!(obj instanceof SubRelatorioMudancaProcedimentosVO)) {
            return false;
        }
		SubRelatorioMudancaProcedimentosVO other = (SubRelatorioMudancaProcedimentosVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDataSaidaFormatada(), other.getDataSaidaFormatada());
		umEqualsBuilder.append(this.getMudProcSolic(), other.getMudProcSolic());
		umEqualsBuilder.append(this.getMudProcRealiz(), other.getMudProcRealiz());
		umEqualsBuilder.append(this.getMudDescrSolic(), other.getMudDescrSolic());
		umEqualsBuilder.append(this.getMudDescrRealiz(), other.getMudDescrRealiz());
		return umEqualsBuilder.isEquals();
	}

}
