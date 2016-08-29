package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class ParametrosAghPerfilProcessoVO implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1770602349612534328L;


	
	private Boolean IndConsulta;
	
	private Boolean vEspPai;
	
	private Boolean vUnfSeq;
	
	

	public Boolean getIndConsulta() {
		return IndConsulta;
	}

	public void setIndConsulta(Boolean indConsulta) {
		IndConsulta = indConsulta;
	}

	public Boolean getvEspPai() {
		return vEspPai;
	}

	public void setvEspPai(Boolean vEspPai) {
		this.vEspPai = vEspPai;
	}

	public Boolean getvUnfSeq() {
		return vUnfSeq;
	}

	public void setvUnfSeq(Boolean vUnfSeq) {
		this.vUnfSeq = vUnfSeq;
	}
	
	
	
	
	public enum Fields {
		IND_CONSULTA("IndConsulta"),
		IND_EXECUTA("vEspPai"),
		IND_ASSINA("vUnfSeq");

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
