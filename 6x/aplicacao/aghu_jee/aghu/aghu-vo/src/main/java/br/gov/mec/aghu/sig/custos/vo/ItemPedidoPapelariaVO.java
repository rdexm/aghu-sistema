package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ItemPedido")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemPedidoPapelariaVO implements Serializable {

	private static final long serialVersionUID = 1531606780960706046L;

	@XmlElement(name="CodigoProduto")
	private Integer codigoProduto;
	
	@XmlElement(name="DescricaoProduto")
	private String descricaoProduto;
	
	@XmlElement(name="MarcaProduto")
	private String marcaProduto;
	
	@XmlElement(name="Quantidade")
	private Integer quantidade;
	
	@XmlElement(name="ValorUnitario")
	private BigDecimal valorUnitario;
	
	public Integer getCodigoProduto() {
		if(codigoProduto != null) {
			return codigoProduto ==  0 ? null : codigoProduto;
		} else {
			return codigoProduto;
		}
	}

	public void setCodigoProduto(Integer codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

	public String getMarcaProduto() {
		return marcaProduto;
	}

	public void setMarcaProduto(String marcaProduto) {
		this.marcaProduto = marcaProduto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

}
