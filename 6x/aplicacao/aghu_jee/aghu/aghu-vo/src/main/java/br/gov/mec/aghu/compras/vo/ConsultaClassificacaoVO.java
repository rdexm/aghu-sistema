package br.gov.mec.aghu.compras.vo;

public class ConsultaClassificacaoVO {
	
	private Long codigo;
	private Integer codigoGrupoMaterial;
	private Integer classificacao5;
	private Integer classificacao4;
	private Integer classificacao3;
	private Integer classificacao2;
	private Integer classificacao1;
	private String descricao5;
	private String descricao4;
	private String descricao3;
	private String descricao2;
	private String descricao1;
	private String descricao0;
	
	public enum Fields{
		
		CODIGO("codigo"),
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"),
		CLASSIFICACAO5("classificacao5"),
		CLASSIFICACAO4("classificacao4"),
		CLASSIFICACAO3("classificacao3"),
		CLASSIFICACAO2("classificacao2"),
		CLASSIFICACAO1("classificacao1"),
		DESCRICAO5("descricao5"),
		DESCRICAO4("descricao4"),		
		DESCRICAO3("descricao3"),
		DESCRICAO2("descricao2"),
		DESCRICAO1("descricao1"),
		DESCRICAO0("descricao0");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}
	
	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}
	
	public Integer getClassificacao5() {
		return classificacao5;
	}

	public void setClassificacao5(Integer classificacao5) {
		this.classificacao5 = classificacao5;
	}

	public Integer getClassificacao4() {
		return classificacao4;
	}

	public void setClassificacao4(Integer classificacao4) {
		this.classificacao4 = classificacao4;
	}

	public Integer getClassificacao3() {
		return classificacao3;
	}

	public void setClassificacao3(Integer classificacao3) {
		this.classificacao3 = classificacao3;
	}

	public Integer getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(Integer classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public Integer getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(Integer classificacao1) {
		this.classificacao1 = classificacao1;
	}

	public String getDescricao5() {
		return descricao5;
	}

	public void setDescricao5(String descricao5) {
		this.descricao5 = descricao5;
	}

	public String getDescricao4() {
		return descricao4;
	}

	public void setDescricao4(String descricao4) {
		this.descricao4 = descricao4;
	}

	public String getDescricao3() {
		return descricao3;
	}

	public void setDescricao3(String descricao3) {
		this.descricao3 = descricao3;
	}

	public String getDescricao2() {
		return descricao2;
	}

	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}

	public String getDescricao1() {
		return descricao1;
	}

	public void setDescricao1(String descricao1) {
		this.descricao1 = descricao1;
	}

	public String getDescricao0() {
		return descricao0;
	}

	public void setDescricao0(String descricao0) {
		this.descricao0 = descricao0;
	}
	
}
