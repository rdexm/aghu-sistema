package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ClassificacaoVO implements BaseBean{
	
	private Long codigo;
	private String descricao;
	private Integer codGrupoMaterial;
	
	public enum Fields{
		
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		CODIGO_GRUPO_MATERIAL("codGrupoMaterial");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public ClassificacaoVO() {
		super();
	}

	public Long getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	
	
	public Integer getCodGrupoMaterial() {
		return codGrupoMaterial;
	}
	
	public void setCodGrupoMaterial(Integer codGrupoMaterial) {
		this.codGrupoMaterial = codGrupoMaterial;
	}
	
}
