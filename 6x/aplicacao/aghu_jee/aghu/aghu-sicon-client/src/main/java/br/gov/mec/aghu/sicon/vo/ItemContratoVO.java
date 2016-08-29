package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ItemContratoVO implements Serializable{
	
	private static final long serialVersionUID = -8096452331432695207L;
	private Integer numeroItem;
	private String descricaoItem;
	private Integer quantidade;
	private BigDecimal valor;
	private String unidade;
	private String marca;
	private String afCp;
	private String origemContrato;  // Para contratos automáticos, sua origem pode ser da proposta ou da Autorização de Fornecimento
	                                // Informação será usada para definir label do campo Valor: Valor unitário (origem = proposta)
	                                //                                                          Valor Efetivado (origem = AF)  
	
	public Integer getNumeroItem() {
		return numeroItem;
	}
	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}
	public String getDescricaoItem() {
		return descricaoItem;
	}
	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String string) {
		this.unidade = string;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	public String getAfCp() {
		return afCp;
	}
	
	public void setAfCp(String afCp) {
		this.afCp = afCp;
	}
	
	public String getOrigemContrato() {
		return origemContrato;
	}
	public void setOrigemContrato(String origemContrato) {
		this.origemContrato = origemContrato;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroItem == null) ? 0 : numeroItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		ItemContratoVO other = (ItemContratoVO) obj;
		return new EqualsBuilder().append(this.numeroItem, other.numeroItem).isEquals();
	}
	

}
