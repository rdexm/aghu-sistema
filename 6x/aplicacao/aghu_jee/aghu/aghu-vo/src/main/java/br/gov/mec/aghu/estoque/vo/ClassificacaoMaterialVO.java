package br.gov.mec.aghu.estoque.vo;

public class ClassificacaoMaterialVO {

	private String descricao;
	private Long numero;
	private Integer nivel1;
	private Integer nivel2;
	private Integer nivel3;
	private Integer nivel4;
	private Integer nivel5;
	private Integer codGrupo;
	private Boolean selecionado;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Integer getNivel1() {
		return nivel1;
	}

	public void setNivel1(Integer nivel1) {
		this.nivel1 = nivel1;
	}

	public Integer getNivel2() {
		return nivel2;
	}

	public void setNivel2(Integer nivel2) {
		this.nivel2 = nivel2;
	}

	public Integer getNivel3() {
		return nivel3;
	}

	public void setNivel3(Integer nivel3) {
		this.nivel3 = nivel3;
	}

	public Integer getNivel4() {
		return nivel4;
	}

	public void setNivel4(Integer nivel4) {
		this.nivel4 = nivel4;
	}

	public Integer getNivel5() {
		return nivel5;
	}

	public void setNivel5(Integer nivel5) {
		this.nivel5 = nivel5;
	}

	public Integer getCodGrupo() {
		return codGrupo;
	}

	public void setCodGrupo(Integer codGrupo) {
		this.codGrupo = codGrupo;
	}

	public enum Fields {

		DESCRICAO("descricao"), NUMERO("numero"), NIVEL1("nivel1"), NIVEL2("nivel2"), NIVEL3("nivel3"), NIVEL4("nivel4"), NIVEL5("nivel5");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

}
