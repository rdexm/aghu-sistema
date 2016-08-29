package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;

public class ConsumoSinteticoMaterialVO {

	private Integer codigoCentroCusto;
	private Boolean indEstorno;
	private Integer quantidade;
	private DominioIndOperacaoBasica indOperacaoBasica;
	private BigDecimal valor;
	
	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}
	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}
	public Boolean getIndEstorno() {
		return indEstorno;
	}
	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public DominioIndOperacaoBasica getIndOperacaoBasica() {
		return indOperacaoBasica;
	}
	public void setIndOperacaoBasica(DominioIndOperacaoBasica indOperacaoBasica) {
		this.indOperacaoBasica = indOperacaoBasica;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

}
