package br.gov.mec.aghu.estoque.vo;

public class PosicaoFinalEstoqueVO{
	
	private Integer quantidade;
	private Double valorMedioPonderado;
	private String valorMedioPonderadoFormatado;
	private Double valorAtual;
	private Double valorAnterior;
	private String indEstocavel;
	private String unidadeMedida;
	private String nomeMaterial;
	private Integer codMaterial;
	private Integer codCatmat;
	private Double variacao;
	
	
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public Double getValorMedioPonderado() {
		return valorMedioPonderado;
	}
	public void setValorMedioPonderado(Double valorMedioPonderado) {
		this.valorMedioPonderado = valorMedioPonderado;
	}
	public String getValorMedioPonderadoFormatado() {
		return valorMedioPonderadoFormatado;
	}
	public void setValorMedioPonderadoFormatado(String valorMedioPonderadoFormatado) {
		this.valorMedioPonderadoFormatado = valorMedioPonderadoFormatado;
	}
	public Double getValorAtual() {
		return valorAtual;
	}
	public void setValorAtual(Double valorAtual) {
		this.valorAtual = valorAtual;
	}
	public Double getValorAnterior() {
		return valorAnterior;
	}
	public void setValorAnterior(Double valorAnterior) {
		this.valorAnterior = valorAnterior;
	}
	public String getIndEstocavel() {
		return indEstocavel;
	}
	public void setIndEstocavel(String indEstocavel) {
		this.indEstocavel = indEstocavel;
	}
	public String getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getCodMaterial() {
		return codMaterial;
	}
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}
	public Double getVariacao() {
		return variacao;
	}
	public void setVariacao(Double variacao) {
		this.variacao = variacao;
	}
	public Integer getCodCatmat() {
		return codCatmat;
	}
	public void setCodCatmat(Integer codCatmat) {
		this.codCatmat = codCatmat;
	}
	
}