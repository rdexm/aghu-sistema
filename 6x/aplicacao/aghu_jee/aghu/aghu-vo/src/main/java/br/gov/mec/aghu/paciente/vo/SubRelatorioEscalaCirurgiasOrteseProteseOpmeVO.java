package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

/**
 * Os dados armazenados nesse objeto representam as escalas de cirurgias utilizadas na geração de relatório.
 * 
 * @author rpanassolo
 */

public class SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO implements Serializable {

	private static final long serialVersionUID = -2047178099786822060L;
	
	private String orteseProt;
	private Integer qtdSolic; 
	private Integer agendaSeq;
	private String qtdUnidSolic;
	
	
	public SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO() {
		super();
	}
	
	
	public enum Fields {
		 ORTESE_PROTESE("orteseProt")
		,QTD_SOLIC("qtdSolic")
		,AGENDA_SEQ("agendaSeq");
		 
		 private String fields;
		 
		 private Fields(String fields) {
			this.fields = fields;
		}
		 
		@Override
		public String toString() {
			return fields;
		}
	}

	public String getOrteseProt() {
		return orteseProt;
	}

	public void setOrteseProt(String orteseProt) {
		this.orteseProt = orteseProt;
	}

	public Integer getQtdSolic() {
		return qtdSolic;
	}

	public void setQtdSolic(Integer qtdSolic) {
		this.qtdSolic = qtdSolic;
	}

	public Integer getAgendaSeq() {
		return agendaSeq;
	}

	public void setAgendaSeq(Integer agendaSeq) {
		this.agendaSeq = agendaSeq;
	}

	public String getQtdUnidSolic() {
		if(qtdSolic==null){
			qtdSolic = 0;
		}
		this.qtdUnidSolic = qtdSolic+" Unid";
		return qtdUnidSolic;
	}

	public void setQtdUnidSolic(String qtdUnidSolic) {
		this.qtdUnidSolic = qtdUnidSolic;
	}
}
