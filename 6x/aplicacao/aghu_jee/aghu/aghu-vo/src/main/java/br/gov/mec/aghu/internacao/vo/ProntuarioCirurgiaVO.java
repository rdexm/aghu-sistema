package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * 
 * @author ihaas
 * 
 */
public class ProntuarioCirurgiaVO implements BaseBean {

	
	private static final long serialVersionUID = -5218190517309939942L;
	private Integer seq;
	private Integer crgSeq;
	private String prontuario;
	private String paciente;
	private String dtHrPrevisaoInicio;
	private String solicitante;
	private Boolean selecionado;

	public ProntuarioCirurgiaVO() {

	}

	public ProntuarioCirurgiaVO(Integer seq, Integer crgSeq, Integer prontuario, String paciente,
			Date dtHrPrevisaoInicio, String solicitante) {
		this.seq = seq;
		this.crgSeq = crgSeq;
		this.prontuario = CoreUtil.formataProntuario(prontuario);
		this.paciente = paciente;
		this.dtHrPrevisaoInicio = DateUtil.obterDataFormatada(dtHrPrevisaoInicio, "dd/MM/yyyy HH:mm");
		this.solicitante = solicitante;
		this.selecionado = Boolean.FALSE;
	}

	public enum Fields {

		SEQ("seq"),
		CRG_SEQ("crgSeq"),
		PRONTUARIO("prontuario"),
		PACIENTE("paciente"),
		DT_HR_PREVISAO_INICIO("dtHrPrevisaoInicio"),
		SOLICITANTE("solicitante");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	//Getters and Setters
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = CoreUtil.formataProntuario(prontuario);
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getDtHrPrevisaoInicio() {
		return dtHrPrevisaoInicio;
	}

	public void setDtHrPrevisaoInicio(Date dtHrPrevisaoInicio) {
		this.dtHrPrevisaoInicio = DateUtil.obterDataFormatada(dtHrPrevisaoInicio, "dd/MM/yyyy HH:mm");
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crgSeq == null) ? 0 : crgSeq.hashCode());
		result = prime * result
				+ ((prontuario == null) ? 0 : prontuario.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProntuarioCirurgiaVO)) {
			return false;
		}
		ProntuarioCirurgiaVO other = (ProntuarioCirurgiaVO) obj;
		if (crgSeq == null) {
			if (other.crgSeq != null) {
				return false;
			}
		} else if (!crgSeq.equals(other.crgSeq)) {
			return false;
		}
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
