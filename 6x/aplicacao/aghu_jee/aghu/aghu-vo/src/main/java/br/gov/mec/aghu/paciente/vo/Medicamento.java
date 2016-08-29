package br.gov.mec.aghu.paciente.vo;


import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author marcelo.corati
 *
 */

public class Medicamento implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3783527077946488451L;
	private String descricao;
	private Integer codigo;

	
	public Medicamento(){}
	
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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
}
