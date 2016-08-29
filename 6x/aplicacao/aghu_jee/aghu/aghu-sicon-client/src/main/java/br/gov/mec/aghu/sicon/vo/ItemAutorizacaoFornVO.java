package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

@SuppressWarnings("ucd")
public class ItemAutorizacaoFornVO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7468004122874334159L;
	private Integer numItem;
	private ScoMaterial material;
	private ScoServico servico;
	private ScoUnidadeMedida unidade;
	private Integer quant;
	private Integer freq;
	private BigDecimal valorUnit;
	
	
	public ItemAutorizacaoFornVO() {
	}
	
	
	public ItemAutorizacaoFornVO(Integer numItem, ScoMaterial material,
			ScoServico servico, ScoUnidadeMedida unidade, Integer quant, Integer freq,
			BigDecimal valorUnit) {
		super();
		this.numItem = numItem;
		this.material = material;
		this.servico = servico;
		this.unidade = unidade;
		this.quant = quant;
		this.freq = freq;
		this.valorUnit = valorUnit;
	}


	public Integer getNumItem() {
		return numItem;
	}
	public void setNumItem(Integer numItem) {
		this.numItem = numItem;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoServico getServico() {
		return servico;
	}
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	public ScoUnidadeMedida getUnidade() {
		return unidade;
	}
	public void setUnidade(ScoUnidadeMedida scoUnidadeMedida) {
		this.unidade = scoUnidadeMedida;
	}
	public Integer getQuant() {
		return quant;
	}
	public void setQuant(Integer quant) {
		this.quant = quant;
	}
	public Integer getFreq() {
		return freq;
	}
	public void setFreq(Integer freq) {
		this.freq = freq;
	}
	public BigDecimal getValorUnit() {
		return valorUnit;
	}
	public void setValorUnit(BigDecimal valorUnit) {
		this.valorUnit = valorUnit;
	}
	
	
	
}
