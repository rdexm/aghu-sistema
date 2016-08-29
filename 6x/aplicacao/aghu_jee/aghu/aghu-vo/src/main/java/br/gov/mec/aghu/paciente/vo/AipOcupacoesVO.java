package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AipOcupacoesVO implements BaseBean {

	private static final long serialVersionUID = -332323675086662952L;

	private Integer codigo;

	private String descricao;

	private Integer qtSinonimos;
	
	public enum Fields {

		CODIGO("codigo"), 
		DESCRICAO("descricao"),
		QT_SINONIMOS("qtSinonimos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQtSinonimos() {
		return qtSinonimos;
	}

	public void setQtSinonimos(Integer qtSinonimos) {
		this.qtSinonimos = qtSinonimos;
	}

}
