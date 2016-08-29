package br.gov.mec.aghu.protocolos.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CadIntervaloTempoVO implements BaseBean {

	private static final long serialVersionUID = 2367144236932785744L;
	
	private Short vpaSeqp;
	private Short vpaPtaSeq;
	private Short seqp;
	private Short diaReferencia;
	private Date horaInicioReferencia;
	private Date horaFimReferencia;
	private Date qtdeHoras;
	private Short ciclo;
	// #41689
	private Integer sesSeq;
	private Integer pteSeq;
	private Short tempoAdministracao;
	// #49432
	private boolean agendar;
	
	public Short getVpaSeqp() {
		return vpaSeqp;
	}

	public void setVpaSeqp(Short vpaSeqp) {
		this.vpaSeqp = vpaSeqp;
	}

	public Short getVpaPtaSeq() {
		return vpaPtaSeq;
	}

	public void setVpaPtaSeq(Short vpaPtaSeq) {
		this.vpaPtaSeq = vpaPtaSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Short getDiaReferencia() {
		return diaReferencia;
	}

	public void setDiaReferencia(Short diaReferencia) {
		this.diaReferencia = diaReferencia;
	}

	public Date getHoraInicioReferencia() {
		return horaInicioReferencia;
	}

	public void setHoraInicioReferencia(Date horaInicioReferencia) {
		this.horaInicioReferencia = horaInicioReferencia;
	}

	public Date getHoraFimReferencia() {
		return horaFimReferencia;
	}

	public void setHoraFimReferencia(Date horaFimReferencia) {
		this.horaFimReferencia = horaFimReferencia;
	}

	public Date getQtdeHoras() {
		return qtdeHoras;
	}

	public void setQtdeHoras(Date qtdeHoras) {
		this.qtdeHoras = qtdeHoras;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}

	public Short getTempoAdministracao() {
		return tempoAdministracao;
	}

	public void setTempoAdministracao(Short tempoAdministracao) {
		this.tempoAdministracao = tempoAdministracao;
	}

	public boolean isAgendar() {
		return agendar;
	}

	public void setAgendar(boolean agendar) {
		this.agendar = agendar;
	}

	public enum Fields {

		VPA_SEQP("vpaSeqp"), 
		VPA_PTA_SEQ("vpaPtaSeq"),
		SEQP("seqp"),
		DIA_REFERENCIA("diaReferencia"),
		HR_INICIO_REFERENCIA("horaInicioReferencia"),
		HR_FIM_REFERENCIA("horaFimReferencia"),
		QTDE_HORAS("qtdeHoras"),
		CICLO("ciclo"),
		SES_SEQ("sesSeq"),
		TEMPO_ADMINISTRACAO("tempoAdministracao"),
		PTE_SEQ("pteSeq");

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
        
        hashCodeBuilder.append(this.vpaSeqp);
        hashCodeBuilder.append(this.vpaPtaSeq);
        hashCodeBuilder.append(this.seqp);
        hashCodeBuilder.append(this.diaReferencia);
        hashCodeBuilder.append(this.horaInicioReferencia);
        hashCodeBuilder.append(this.horaFimReferencia);
        hashCodeBuilder.append(this.qtdeHoras);
        hashCodeBuilder.append(this.ciclo);
        hashCodeBuilder.append(this.sesSeq);
        hashCodeBuilder.append(this.tempoAdministracao);
        hashCodeBuilder.append(this.agendar);
        
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
        
        CadIntervaloTempoVO other = (CadIntervaloTempoVO) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.vpaSeqp, other.vpaSeqp);
        umEqualsBuilder.append(this.vpaPtaSeq, other.vpaPtaSeq);
        umEqualsBuilder.append(this.seqp, other.seqp);
        umEqualsBuilder.append(this.diaReferencia, other.diaReferencia);
        umEqualsBuilder.append(this.horaInicioReferencia, other.horaInicioReferencia);
        umEqualsBuilder.append(this.horaFimReferencia, other.horaFimReferencia);
        umEqualsBuilder.append(this.qtdeHoras, other.qtdeHoras);
        umEqualsBuilder.append(this.ciclo, other.ciclo);
        umEqualsBuilder.append(this.sesSeq, other.sesSeq);
        umEqualsBuilder.append(this.tempoAdministracao, other.tempoAdministracao);
        umEqualsBuilder.append(this.agendar, other.agendar);
        
        return umEqualsBuilder.isEquals();
    }

	public Integer getPteSeq() {
		return pteSeq;
	}

	public void setPteSeq(Integer pteSeq) {
		this.pteSeq = pteSeq;
	}
}
