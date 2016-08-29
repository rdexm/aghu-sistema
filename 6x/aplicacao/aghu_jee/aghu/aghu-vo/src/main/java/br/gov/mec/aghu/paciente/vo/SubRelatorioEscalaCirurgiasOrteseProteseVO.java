package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

/**
 * Os dados armazenados nesse objeto representam as escalas de cirurgias utilizadas na geração de relatório.
 * 
 * @author lalegre
 */

public class SubRelatorioEscalaCirurgiasOrteseProteseVO implements Serializable {

	private static final long serialVersionUID = -2047178099786822060L;
	
	private String orteseProt;
	private String qtdSolic; // Quantidade de material solicitada
	
	public SubRelatorioEscalaCirurgiasOrteseProteseVO() {
		super();
	}
	
	public enum Fields {
		 ORTESE_PROTESE("orteseProt")
		,QTD_SOLIC("qtdSolic");	

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

	public String getQtdSolic() {
		return qtdSolic;
	}

	public void setQtdSolic(String qtdSolic) {
		this.qtdSolic = qtdSolic;
	}
}
