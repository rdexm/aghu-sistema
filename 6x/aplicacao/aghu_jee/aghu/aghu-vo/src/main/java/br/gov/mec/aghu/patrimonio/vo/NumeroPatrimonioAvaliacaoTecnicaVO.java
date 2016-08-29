package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
/**
 * @author rafael.nascimento
 */
public class NumeroPatrimonioAvaliacaoTecnicaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7595081989690369935L;

	private Long numeroBem;
	

	public enum Fields{
		NUMERO_BEM("numeroBem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	public Long getNumeroBem() {
		return numeroBem;
	}


	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}
}