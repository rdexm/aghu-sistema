package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.utils.DateUtil;


public class CursorCTrpVO implements Serializable {
	
	private static final long serialVersionUID = 2741572703345759113L;
	
	private Date dtInicioAgendaSessao;
	private Short nroDiasAgendaSessao;
	
	public Date obterDataFimAut(){
		
		return DateUtil.adicionaDias(this.dtInicioAgendaSessao, (nroDiasAgendaSessao-1));
	}

	public enum Fields {
		DATA_INICIO_AGENDA_SESSAO("dtInicioAgendaSessao"),
		NRO_AGENDA_SESSAO("nroDiasAgendaSessao")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Date getDtInicioAgendaSessao() {
		return dtInicioAgendaSessao;
	}

	public void setDtInicioAgendaSessao(Date dtInicioAgendaSessao) {
		this.dtInicioAgendaSessao = dtInicioAgendaSessao;
	}

	public Short getNroDiasAgendaSessao() {
		return nroDiasAgendaSessao;
	}

	public void setNroDiasAgendaSessao(Short nroDiasAgendaSessao) {
		this.nroDiasAgendaSessao = nroDiasAgendaSessao;
	}
}