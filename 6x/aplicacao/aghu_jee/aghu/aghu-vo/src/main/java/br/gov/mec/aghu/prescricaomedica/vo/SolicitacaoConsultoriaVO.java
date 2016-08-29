package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;

public class SolicitacaoConsultoriaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8907225032039835947L;

	private Integer atdSeq;
	private Integer seq;
	private DominioTipoSolicitacaoConsultoria tipo;
	private String especialidade;
	private Date dtHrSolicitada;
	private Date dtHrInicio;
	private Date dtHrFim;
	private DominioIndPendenteItemPrescricao indPendente;
	private DominioSimNao indUrgencia;

	public enum Fields {
		ATD_SEQ("atdSeq"), 
		SEQ("seq"), 
		TIPO("tipo"), 
		NOME_ESPECIALIDADE("especialidade"), 
		DTHR_SOLICITADA("dtHrSolicitada"), 
		DTHR_INICIO("dtHrInicio"), 
		DTHR_FIM("dtHrFim"), 
		PENDENCIA("indPendente"),
		IND_URGENCIA("indUrgencia");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public DominioTipoSolicitacaoConsultoria getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoSolicitacaoConsultoria tipo) {
		this.tipo = tipo;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDtHrSolicitada() {
		return dtHrSolicitada;
	}

	public void setDtHrSolicitada(Date dtHrSolicitada) {
		this.dtHrSolicitada = dtHrSolicitada;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public Date getDtHrFim() {
		return dtHrFim;
	}

	public void setDtHrFim(Date dtHrFim) {
		this.dtHrFim = dtHrFim;
	}

	public DominioSimNao getIndUrgencia() {
		return indUrgencia;
	}

	public void setIndUrgencia(DominioSimNao indUrgencia) {
		this.indUrgencia = indUrgencia;
	}

	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}

	public void setIndSituacao(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}
}
