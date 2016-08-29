package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PercentualOcupacaoVO implements BaseBean {

	private static final long serialVersionUID = 8270412908938724846L;
	
	private String dataInicio;
	private Integer percentual;
	private Integer numeroPacientes;
	private Integer minutosDisponiveis;
	private Date dataEvento;
	private String tempoDisponivelFormatado;

	public PercentualOcupacaoVO() {
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Integer getPercentual() {
		return percentual;
	}

	public void setPercentual(Integer percentual) {
		this.percentual = percentual;
	}

	public Integer getNumeroPacientes() {
		return numeroPacientes;
	}

	public void setNumeroPacientes(Integer numeroPacientes) {
		this.numeroPacientes = numeroPacientes;
	}

	public Integer getMinutosDisponiveis() {
		return minutosDisponiveis;
	}

	public void setMinutosDisponiveis(Integer minutosDisponiveis) {
		this.minutosDisponiveis = minutosDisponiveis;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public String getTempoDisponivelFormatado() {
		return tempoDisponivelFormatado;
	}

	public void setTempoDisponivelFormatado(String tempoDisponivelFormatado) {
		this.tempoDisponivelFormatado = tempoDisponivelFormatado;
	}

	public enum Fields {

		DATA_INICIO("dataInicio"),
		PERCENTUAL("percentual"),
		NUMERO_PACIENTES("numeroPacientes"),
		MINUTOS_DISPONIVEIS("minutosDisponiveis");
		
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
        
        hashCodeBuilder.append(this.dataInicio);
        hashCodeBuilder.append(this.percentual);
        hashCodeBuilder.append(this.numeroPacientes);
        hashCodeBuilder.append(this.minutosDisponiveis);
        
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
        
        PercentualOcupacaoVO other = (PercentualOcupacaoVO) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.dataInicio, other.dataInicio);
        umEqualsBuilder.append(this.percentual, other.percentual);
        umEqualsBuilder.append(this.numeroPacientes, other.numeroPacientes);
        umEqualsBuilder.append(this.minutosDisponiveis, other.minutosDisponiveis);
        
        return umEqualsBuilder.isEquals();
    }
}
