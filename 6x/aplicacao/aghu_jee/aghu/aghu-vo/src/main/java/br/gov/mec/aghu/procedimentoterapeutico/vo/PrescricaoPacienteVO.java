package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PrescricaoPacienteVO implements BaseBean {

	private static final long serialVersionUID = -108691492568448404L;
	
	private Integer pteSeq;
	private Short ciclo;
	private Date dataSugerida;
	private String responsavel1;
	private String responsavel2;
	private Integer cloSeq;
	private Integer lote;
	private String protocolo;
	
	public PrescricaoPacienteVO() {
	}
	
	public Integer getPteSeq() {
		return pteSeq;
	}

	public void setPteSeq(Integer pteSeq) {
		this.pteSeq = pteSeq;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Date getDataSugerida() {
		return dataSugerida;
	}

	public void setDataSugerida(Date dataSugerida) {
		this.dataSugerida = dataSugerida;
	}

	public String getResponsavel1() {
		return responsavel1;
	}

	public void setResponsavel1(String responsavel1) {
		this.responsavel1 = responsavel1;
	}

	public String getResponsavel2() {
		return responsavel2;
	}

	public void setResponsavel2(String responsavel2) {
		this.responsavel2 = responsavel2;
	}

	public Integer getCloSeq() {
		return cloSeq;
	}

	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}

	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public enum Fields {

		PTE_SEQ("pteSeq"), 
		CICLO("ciclo"), 
		DATA_SUGERIDA("dataSugerida"),
		RESPONSAVEL_1("responsavel1"),
		RESPONSAVEL_2("responsavel2"),
		CLO_SEQ("cloSeq"),
		LOTE("lote");
		
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
        
        hashCodeBuilder.append(this.pteSeq);
        hashCodeBuilder.append(this.ciclo);
        hashCodeBuilder.append(this.dataSugerida);
        hashCodeBuilder.append(this.responsavel1);
        hashCodeBuilder.append(this.responsavel2);
        hashCodeBuilder.append(this.cloSeq);
        hashCodeBuilder.append(this.lote);
        hashCodeBuilder.append(this.protocolo);
        
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
        
        PrescricaoPacienteVO other = (PrescricaoPacienteVO) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.pteSeq, other.pteSeq);
        umEqualsBuilder.append(this.ciclo, other.ciclo);
        umEqualsBuilder.append(this.dataSugerida, other.dataSugerida);
        umEqualsBuilder.append(this.responsavel1, other.responsavel1);
        umEqualsBuilder.append(this.responsavel2, other.responsavel2);
        umEqualsBuilder.append(this.cloSeq, other.cloSeq);
        umEqualsBuilder.append(this.lote, other.lote);
        umEqualsBuilder.append(this.protocolo, other.protocolo);
        
        return umEqualsBuilder.isEquals();
    }
}
