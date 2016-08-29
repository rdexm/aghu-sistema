package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de Alteração de Horário para C1 em #44228 – Realizar Manutenção de
 * Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 *
 */
public class AlterarHorariosVO implements BaseBean {

	private static final long serialVersionUID = -462903433407149494L;

	private Integer serMatricula; // PTE.SER_MATRICULA EM C1
	private Short serVinCodigo; // PTE.SER_VIN_CODIGO EM C1
	private Integer serMatriculaValida; // PTE.SER_MATRICULA_VALIDA EM C1
	private Short serVinCodigoValida; // PTE.SER_VIN_CODIGO_VALIDA EM C1
	private String responsavel1; // PES1.NOME_PESSOA RESPOSAVEL1 EM C1
	private String responsavel2; // PES2.NOME_PESSOA RESPOSAVEL2 EM C1

	private Integer cloSeq; // SES.CLO_SEQ EM C1
	private Integer pteSeq; // SES.PTE_SEQ EM C1
	private Integer pteAtdSeq; // SES.PTE_ATD_SEQ EM C1
	private Integer sesSeq; // SES.SEQ SES_SEQ EM C1

	private Short seqAgendamento; // AGS.SEQ SEQ_AGENDAMENTO EM C1
	private Short cicloReserva; // HRS.CICLO EM C13
	/**
	 * Campos da tela
	 */
	private Date dataSugerida; // TODO SOLICITAR DADA COM O ANALISTA
	private Integer ciclo; // PTE.CICLO EM C1
	private String protocolo; // // PTC.DESCRICAO, PTA.TITULO EM C6
	private String responsavel; // NOME_PESSOA PES1.RESPOSAVEL1 OU

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
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

	public Integer getPteSeq() {
		return pteSeq;
	}

	public void setPteSeq(Integer pteSeq) {
		this.pteSeq = pteSeq;
	}

	public Integer getPteAtdSeq() {
		return pteAtdSeq;
	}

	public void setPteAtdSeq(Integer pteAtdSeq) {
		this.pteAtdSeq = pteAtdSeq;
	}

	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}

	public Short getSeqAgendamento() {
		return seqAgendamento;
	}

	public void setSeqAgendamento(Short seqAgendamento) {
		this.seqAgendamento = seqAgendamento;
	}

	public Date getDataSugerida() {
		return dataSugerida;
	}

	public void setDataSugerida(Date dataSugerida) {
		this.dataSugerida = dataSugerida;
	}

	public Integer getCiclo() {
		return ciclo;
	}

	public void setCiclo(Integer ciclo) {
		this.ciclo = ciclo;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	
	public Short getCicloReserva() {
		return cicloReserva;
	}

	public void setCicloReserva(Short cicloReserva) {
		this.cicloReserva = cicloReserva;
	}


	public enum Fields {

		CICLO("ciclo"), DATA_SUGERIDA("dataSugerida"), PROTOCOLO("protocolo"), RESPONSAVEL("responsavel"), SER_MATRICULA("serMatricula"), SER_VIN_CODIGO("serVinCodigo"), SER_MATRICULA_VALIDA("serMatriculaValida"), SER_VIN_CODIGO_VALIDA("serVinCodigoValida"), RESPONSAVEL_1("responsavel1"), RESPONSAVEL_2(
				"responsavel2"), CLO_SEQ("cloSeq"), PTE_SEQ("pteSeq"), PTE_ATD_SEQ("pteAtdSeq"), SES_SEQ("sesSeq"), SEQ_AGENDAMENTO("seqAgendamento"), CICLO_RESERVA("cicloReserva");

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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCloSeq());
		umHashCodeBuilder.append(this.getDataSugerida());
		umHashCodeBuilder.append(this.getDataSugerida());
		umHashCodeBuilder.append(this.getProtocolo());
		umHashCodeBuilder.append(this.getPteAtdSeq());
		umHashCodeBuilder.append(this.getPteSeq());
		umHashCodeBuilder.append(this.getResponsavel());
		umHashCodeBuilder.append(this.getResponsavel1());
		umHashCodeBuilder.append(this.getResponsavel2());
		umHashCodeBuilder.append(this.getSeqAgendamento());
		umHashCodeBuilder.append(this.getSerMatricula());
		umHashCodeBuilder.append(this.getSerMatriculaValida());
		umHashCodeBuilder.append(this.getSerVinCodigo());
		umHashCodeBuilder.append(this.getSerVinCodigoValida());
		umHashCodeBuilder.append(this.getSesSeq());
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
		if (!(obj instanceof AlterarHorariosVO)) {
			return false;
		}
		AlterarHorariosVO other = (AlterarHorariosVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCiclo(), other.getCiclo());
		umEqualsBuilder.append(this.getCloSeq(), other.getCloSeq());
		umEqualsBuilder.append(this.getDataSugerida(), other.getDataSugerida());
		umEqualsBuilder.append(this.getProtocolo(), other.getProtocolo());
		umEqualsBuilder.append(this.getPteAtdSeq(), other.getPteAtdSeq());
		umEqualsBuilder.append(this.getPteSeq(), other.getPteSeq());
		umEqualsBuilder.append(this.getResponsavel(), other.getResponsavel());
		umEqualsBuilder.append(this.getResponsavel1(), other.getResponsavel1());
		umEqualsBuilder.append(this.getResponsavel2(), other.getResponsavel2());
		umEqualsBuilder.append(this.getSeqAgendamento(), other.getSeqAgendamento());
		umEqualsBuilder.append(this.getSerMatricula(), other.getSerMatricula());
		umEqualsBuilder.append(this.getSerMatriculaValida(), other.getSerMatriculaValida());
		umEqualsBuilder.append(this.getSerVinCodigo(), other.getSerVinCodigo());
		umEqualsBuilder.append(this.getSerVinCodigoValida(), other.getSerVinCodigoValida());
		umEqualsBuilder.append(this.getSesSeq(), other.getSesSeq());
		umEqualsBuilder.append(this.getSeqAgendamento(), other.getSeqAgendamento());
		umEqualsBuilder.append(this.getCicloReserva(), other.getCicloReserva());
		return umEqualsBuilder.isEquals();
	}

	
}
