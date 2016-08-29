package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de Dias Agendados para C7 em #44228 – Realizar Manutenção de Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 *
 */
public class DiasAgendadosVO implements BaseBean {

	private static final long serialVersionUID = 2059693376797467145L;

	private Short dia; // HRS.DIA
	private Date tempo; // HRS.TEMPO
	private Date dataInicio; // HRS.DATA_INICIO
	private Date dataFim; // HRS.DATA_FIM
	private String sala; // SAL.DESCRICAO SALA
	private String acomodacao; // LOA.DESCRICAO ACOMODACAO
	private Integer sesSeq; // HRS.SES_SEQ

	/*
	 * Atributos criado para Exclusão e justificativa na tela
	 */
	private Short seqAgendamento; // AGS.SEQ
	private DominioTipoAcomodacao tipoLocal; // LOA.SEQ
	private Short seqHorario; // HRS.SEQ
	private Short tpsSeq; // AGS.TPS_SEQ

	/*
	 * Atributos adicionais
	 */
	private Short seqSala;
	private Short seqAcomodacao;
	private Short seqCiclo;

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
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

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(String acomodacao) {
		this.acomodacao = acomodacao;
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

	public Short getSeqSala() {
		return seqSala;
	}

	public void setSeqSala(Short seqSala) {
		this.seqSala = seqSala;
	}

	public DominioTipoAcomodacao getTipoLocal() {
		return tipoLocal;
	}

	public void setTipoLocal(DominioTipoAcomodacao tipoLocal) {
		this.tipoLocal = tipoLocal;
	}

	public Short getSeqCiclo() {
		return seqCiclo;
	}

	public void setSeqCiclo(Short seqCiclo) {
		this.seqCiclo = seqCiclo;
	}

	public Short getSeqAcomodacao() {
		return seqAcomodacao;
	}

	public void setSeqAcomodacao(Short seqAcomodacao) {
		this.seqAcomodacao = seqAcomodacao;
	}

	public Short getSeqHorario() {
		return seqHorario;
	}

	public void setSeqHorario(Short seqHorario) {
		this.seqHorario = seqHorario;
	}

	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public enum Fields {

		DIA("dia"), TEMPO("tempo"), DATA_INICIO("dataInicio"), DATA_FIM("dataFim"), SALA("sala"), ACOMODACAO("acomodacao"), SES_SEQ("sesSeq"), SEQ_AGENDAMENTO("seqAgendamento"), SEQ_SALA("seqSala"), TIPO_LOCAL("tipoLocal"), SEQ_ACOMODACAO("seqAcomodacao"), SEQ_HORARIO("seqHorario"), SEQ_CICLO(
				"seqCiclo"), TPS_SEQ("tpsSeq");

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
		umHashCodeBuilder.append(this.getAcomodacao());
		umHashCodeBuilder.append(this.getDataFim());
		umHashCodeBuilder.append(this.getDataInicio());
		umHashCodeBuilder.append(this.getDia());
		umHashCodeBuilder.append(this.getSala());
		umHashCodeBuilder.append(this.getSesSeq());
		umHashCodeBuilder.append(this.getTempo());
		umHashCodeBuilder.append(this.getSeqAgendamento());
		umHashCodeBuilder.append(this.getSeqSala());
		umHashCodeBuilder.append(this.getTipoLocal());
		umHashCodeBuilder.append(this.getSeqAcomodacao());
		umHashCodeBuilder.append(this.getSeqHorario());
		umHashCodeBuilder.append(this.getTpsSeq());
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
		if (!(obj instanceof DiasAgendadosVO)) {
			return false;
		}
		DiasAgendadosVO other = (DiasAgendadosVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getAcomodacao(), other.getAcomodacao());
		umEqualsBuilder.append(this.getDataFim(), other.getDataFim());
		umEqualsBuilder.append(this.getDataInicio(), other.getDataInicio());
		umEqualsBuilder.append(this.getDia(), other.getDia());
		umEqualsBuilder.append(this.getSala(), other.getSala());
		umEqualsBuilder.append(this.getSesSeq(), other.getSesSeq());
		umEqualsBuilder.append(this.getTempo(), other.getTempo());
		umEqualsBuilder.append(this.getSeqAgendamento(), other.getSeqAgendamento());
		umEqualsBuilder.append(this.getSeqSala(), other.getSeqSala());
		umEqualsBuilder.append(this.getTipoLocal(), other.getTipoLocal());
		umEqualsBuilder.append(this.getSeqAcomodacao(), other.getSeqAcomodacao());
		umEqualsBuilder.append(this.getSeqHorario(), other.getSeqHorario());
		umEqualsBuilder.append(this.getTpsSeq(), other.getTpsSeq());
		return umEqualsBuilder.isEquals();

	}

}
