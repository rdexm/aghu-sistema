package br.gov.mec.aghu.exames.contratualizacao.vo;


public class TotalItemSolicitacaoExamesVO {

	private String unfDescricao;
	private String exameDescricao;
	private String materialAnaliseDescricao;
	private String situacaoCodigo;
	private String dataProgramada;
	private Integer totalSituacao;
	private Integer totalData;
	private Integer totalExameMaterial; 
	private Integer totalArea;
	
	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getExameDescricao() {
		return exameDescricao;
	}

	public void setExameDescricao(String exameDescricao) {
		this.exameDescricao = exameDescricao;
	}

	public String getMaterialAnaliseDescricao() {
		return materialAnaliseDescricao;
	}

	public void setMaterialAnaliseDescricao(String materialAnaliseDescricao) {
		this.materialAnaliseDescricao = materialAnaliseDescricao;
	}

	public String getSituacaoCodigo() {
		return situacaoCodigo;
	}

	public void setSituacaoCodigo(String situacaoCodigo) {
		this.situacaoCodigo = situacaoCodigo;
	}

	public String getDataProgramada() {
		return dataProgramada;
	}

	public void setDataProgramada(String dataProgramada) {
		this.dataProgramada = dataProgramada;
	}

	public Integer getTotalSituacao() {
		return totalSituacao;
	}

	public void setTotalSituacao(Integer totalSituacao) {
		this.totalSituacao = totalSituacao;
	}

	public Integer getTotalExameMaterial() {
		return totalExameMaterial;
	}

	public void setTotalExameMaterial(Integer totalExameMaterial) {
		this.totalExameMaterial = totalExameMaterial;
	}

	public Integer getTotalData() {
		return totalData;
	}

	public void setTotalData(Integer totalData) {
		this.totalData = totalData;
	}

	public Integer getTotalArea() {
		return totalArea;
	}

	public void setTotalArea(Integer totalArea) {
		this.totalArea = totalArea;
	}

}
