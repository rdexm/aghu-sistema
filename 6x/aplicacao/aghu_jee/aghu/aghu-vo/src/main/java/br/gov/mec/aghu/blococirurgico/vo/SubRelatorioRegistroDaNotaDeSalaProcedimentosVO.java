package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioRegistroDaNotaDeSalaProcedimentosVO implements Serializable {

	private static final long serialVersionUID = 5551828222782225798L;
	
	private String descricaoProcedimento; 		//12
	
	public SubRelatorioRegistroDaNotaDeSalaProcedimentosVO() {
		super();
	}
	
	public SubRelatorioRegistroDaNotaDeSalaProcedimentosVO(String descricaoProcedimento) {
		super();
		this.descricaoProcedimento = descricaoProcedimento;
	}
	
	public enum Fields {
		
		PROCEDIMENTO_DESCRICAO("descricaoProcedimento");
		
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
	
	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}
}