package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class TributosVO implements Serializable {

	
	/**
	 * @author felipe.rocha
	 */
	private static final long serialVersionUID = 6880044075658093250L;
	private Integer codigoRecolhimento;
	private String periodoApuracao;
	private String dataVencimento;


	public String getPeriodoApuracao() {
		return periodoApuracao;
	}

	public void setPeriodoApuracao(String periodoApuracao) {
		this.periodoApuracao = periodoApuracao;
	}

	public String getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Integer getCodigoRecolhimento() {
		return codigoRecolhimento;
	}

	public void setCodigoRecolhimento(Integer codigoRecolhimento) {
		this.codigoRecolhimento = codigoRecolhimento;
	}
	
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TributosVO)) {
            return false;
        }
        TributosVO other = (TributosVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.codigoRecolhimento, other.getCodigoRecolhimento()); 
        return umEqualsBuilder.isEquals();
    }


}