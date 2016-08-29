package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RegistrarControlePacienteVO implements BaseBean {

	/**#41711**/
	private static final long serialVersionUID = 8728393494777989043L;
	
	private String tpiDescricao;
	private String intDescricao;
	
	public enum Fields {
		TPI_DESCRICAO("tpiDescricao"),
		INT_DESCRICAO("intDescricao");
		
		private String fields;
	
		private Fields(String fields) {
			this.fields = fields;
		}
	
		@Override
		public String toString() {
			return fields;
		}
	}
	
	//Getters e Setters
	public String getTpiDescricao() {
		return tpiDescricao;
	}
	public void setTpiDescricao(String tpiDescricao) {
		this.tpiDescricao = tpiDescricao;
	}
	public String getIntDescricao() {
		return intDescricao;
	}
	public void setIntDescricao(String intDescricao) {
		this.intDescricao = intDescricao;
	}

}
