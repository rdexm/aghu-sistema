package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class ParametrosAghEspecialidadesAtestadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1088743626028106098L;

	private Short vEspSeq;
	
	private Short vEspPai;
	
	private Short vUnfSeq;

	public Short getvEspSeq() {
		return vEspSeq;
	}

	public void setvEspSeq(Short vEspSeq) {
		this.vEspSeq = vEspSeq;
	}

	public Short getvEspPai() {
		return vEspPai;
	}

	public void setvEspPai(Short vEspPai) {
		this.vEspPai = vEspPai;
	}

	public Short getvUnfSeq() {
		return vUnfSeq;
	}

	public void setvUnfSeq(Short vUnfSeq) {
		this.vUnfSeq = vUnfSeq;
	}
	
	public enum Fields {
		V_ESP_SEQ("vEspSeq"),
		V_ESP_PAI("vEspPai"),
		V_UNF_SEQ("vUnfSeq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
}
