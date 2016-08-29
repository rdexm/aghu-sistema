package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.io.Serializable;

public class PacientesInternadosUltrapassadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2600364979252404880L;
	
	
	private String descricaoUnidade;
	private Long nroPacientes;
	
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
	public Long getNroPacientes() {
		return nroPacientes;
	}
	public void setNroPacientes(Long nroPacientes) {
		this.nroPacientes = nroPacientes;
	}
	
	public enum Fields {
		DESCRICAO_UNIDADE("descricaoUnidade"),
		NRO_PACIENTES("nroPacientes");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
