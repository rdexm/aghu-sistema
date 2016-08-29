package br.gov.mec.aghu.paciente.vo;


import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author marcelo.corati
 *
 */

public class ComponenteSanguineo implements BaseBean{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8984931045652975781L;
	private String descricao;
	private String codigo;

	
	public ComponenteSanguineo(){}
	
	public enum Fields {
		
		CODIGO("codigo"),
		DESCRICAO("descricao");

		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
