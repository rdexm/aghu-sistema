package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;


public class VerificaAtendimentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 635066592495985486L;

	private Date dthrInicio;

	private Date dthrFim;

	private Integer seqAtendimento;

	private Integer seqHospitalDia;

	private Integer seqInternacao;

	private Integer seqAtendimentoUrgencia;

	private Short seqUnidadeFuncional;

	public VerificaAtendimentoVO() {
	}

	public VerificaAtendimentoVO(Date dthrInicio, Date dthrFim,
			Integer seqAtendimento, Integer seqHospitalDia,
			Integer seqInternacao, Integer seqAtendimentoUrgencia,
			Short seqUnidadeFuncional) {
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.seqAtendimento = seqAtendimento;
		this.seqHospitalDia = seqHospitalDia;
		this.seqInternacao = seqInternacao;
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getSeqHospitalDia() {
		return seqHospitalDia;
	}

	public void setSeqHospitalDia(Integer seqHospitalDia) {
		this.seqHospitalDia = seqHospitalDia;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}
	
}
