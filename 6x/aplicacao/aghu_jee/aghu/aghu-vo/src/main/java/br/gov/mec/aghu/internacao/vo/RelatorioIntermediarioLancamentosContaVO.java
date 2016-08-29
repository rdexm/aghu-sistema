package br.gov.mec.aghu.internacao.vo;


public class RelatorioIntermediarioLancamentosContaVO {

	private Long codsus;
	private Integer codphi;
	private String descricao;
	private Integer quantidade;
	private String dataHoraRealizado;
	private String unidadeRealizadora;
	private Integer matriculaResponsavel;
	private Short vinCodResponsavel;
	private Integer matriculaAnestesista;
	private Short vinCodAnestesista;

	public enum Fields {
		COD_SUS("codsus"), 
		COD_PHI("codphi"), 
		DESCRICAO("descricao"), 
		QUANTIDADE("quantidade"), 
		DATA_HORA_REALIZADO("dataHoraRealizado"),
		UNIDADE_REALIZADORA("unidadeRealizadora"),
		MATRICULA_RESPONSAVEL("matriculaResponsavel"),
		VIN_COD_RESPONSAVEL("vinCodResponsavel"),
		MATRICULA_ANESTESISTA("matriculaAnestesista"),
		VIN_COD_ANESTESISTA("vinCodAnestesista");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Long getCodsus() {
		return codsus;
	}

	public void setCodsus(Long codsus) {
		this.codsus = codsus;
	}

	public Integer getCodphi() {
		return codphi;
	}

	public void setCodphi(Integer codphi) {
		this.codphi = codphi;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getDataHoraRealizado() {
		return dataHoraRealizado;
	}

	public void setDataHoraRealizado(String dataHoraRealizado) {
		this.dataHoraRealizado = dataHoraRealizado;
	}

	public Integer getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public Short getVinCodResponsavel() {
		return vinCodResponsavel;
	}

	public void setVinCodResponsavel(Short vinCodResponsavel) {
		this.vinCodResponsavel = vinCodResponsavel;
	}

	public String getUnidadeRealizadora() {
		return unidadeRealizadora;
	}

	public void setUnidadeRealizadora(String unidadeRealizadora) {
		this.unidadeRealizadora = unidadeRealizadora;
	}

	public Integer getMatriculaAnestesista() {
		return matriculaAnestesista;
	}

	public void setMatriculaAnestesista(Integer matriculaAnestesista) {
		this.matriculaAnestesista = matriculaAnestesista;
	}

	public Short getVinCodAnestesista() {
		return vinCodAnestesista;
	}

	public void setVinCodAnestesista(Short vinCodAnestesista) {
		this.vinCodAnestesista = vinCodAnestesista;
	}

}