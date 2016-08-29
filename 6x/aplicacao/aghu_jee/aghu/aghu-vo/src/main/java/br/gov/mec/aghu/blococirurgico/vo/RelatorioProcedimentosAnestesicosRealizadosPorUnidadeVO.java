package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2092934877216207332L;
	
	private String unidade; 				//5
	private String tipoAnestesia; 			//6
	private Long qtdeTipoAnestesia;	    //7
	private String branco;					//coluna em branco no relatório

	public RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO() {
		
	}
	
	public enum Fields {

		UNIDADE("unidade"),
		TIPO_ANESTESIA("tipoAnestesia"),
		QTDE_TIPO_ANESTESIA("qtdeTipoAnestesia");

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
	
	public String getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(String tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public Long getQtdeTipoAnestesia() {
		return qtdeTipoAnestesia;
	}

	public void setQtdeTipoAnestesia(Long qtdeTipoAnestesia) {
		this.qtdeTipoAnestesia = qtdeTipoAnestesia;
	}

	public String getBranco() {
		return branco;
	}

	public void setBranco(String branco) {
		this.branco = branco;
	}
	
}