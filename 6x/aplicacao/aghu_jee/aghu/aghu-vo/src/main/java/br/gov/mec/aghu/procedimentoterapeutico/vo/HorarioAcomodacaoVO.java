package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class HorarioAcomodacaoVO implements BaseBean {

	private static final long serialVersionUID = 4378230285393118904L;
	
	private Integer id;
	private Date dataInicio;
	private Date dataFim;
	private String protocolo;
	private Integer pacCodigo;
	private String descricaoHorario;
	private String hintHorario;
	private Short ciclo;
	private Short dia;
	
	private Short agsSeq;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getDescricaoHorario() {
		return descricaoHorario;
	}

	public void setDescricaoHorario(String descricaoHorario) {
		this.descricaoHorario = descricaoHorario;
	}

	public String getHintHorario() {
		return hintHorario;
	}

	public void setHintHorario(String hintHorario) {
		this.hintHorario = hintHorario;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Short getAgsSeq() {
		return agsSeq;
	}

	public void setAgsSeq(Short agsSeq) {
		this.agsSeq = agsSeq;
	}

	public enum Fields {

		DATA_INICIO("dataInicio"), 
		DATA_FIM("dataFim"),
		PROTOCOLO("protocolo"),
		PAC_CODIGO("pacCodigo"),
		CICLO("ciclo"),
		DIA("dia"),
		AGS_SEQ("agsSeq");
		
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
        
        hashCodeBuilder.append(this.id);
        hashCodeBuilder.append(this.dataInicio);
        hashCodeBuilder.append(this.dataFim);
        hashCodeBuilder.append(this.protocolo);
        hashCodeBuilder.append(this.pacCodigo);
        hashCodeBuilder.append(this.descricaoHorario);
        hashCodeBuilder.append(this.hintHorario);
        hashCodeBuilder.append(this.ciclo);
        hashCodeBuilder.append(this.dia);
        hashCodeBuilder.append(this.agsSeq);
        
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
        
        HorarioAcomodacaoVO other = (HorarioAcomodacaoVO) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.id, other.id);
        umEqualsBuilder.append(this.dataInicio, other.dataInicio);
        umEqualsBuilder.append(this.dataFim, other.dataFim);
        umEqualsBuilder.append(this.protocolo, other.protocolo);
        umEqualsBuilder.append(this.pacCodigo, other.pacCodigo);
        umEqualsBuilder.append(this.descricaoHorario, other.descricaoHorario);
        umEqualsBuilder.append(this.hintHorario, other.hintHorario);
        umEqualsBuilder.append(this.ciclo, other.ciclo);
        umEqualsBuilder.append(this.dia, other.dia);
        umEqualsBuilder.append(this.agsSeq, other.agsSeq);
        
        return umEqualsBuilder.isEquals();
    }
}
