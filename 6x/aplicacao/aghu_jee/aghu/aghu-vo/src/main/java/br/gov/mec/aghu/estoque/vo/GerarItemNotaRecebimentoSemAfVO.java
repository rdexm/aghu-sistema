package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class GerarItemNotaRecebimentoSemAfVO {
	
	private Integer afnNumero;
	private Integer numero;
	
	private ScoMaterial material;
	private ScoMarcaComercial marcaComercial;
	private Integer quantidadeReceber;
	private ScoUnidadeMedida unidadeMedida;
	private Integer quantidadeEmbalagemFatorConversao;
	private Integer quantidadeConvertida;
	private BigDecimal valorTotal;
	
	private String materialCodigoNome;
	private String marcaComercialCodigoNome;
	private String unidadeMedidaCodigoDescricao;
	
	
	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public Integer getQuantidadeReceber() {
		return quantidadeReceber;
	}

	public void setQuantidadeReceber(Integer quantidadeReceber) {
		this.quantidadeReceber = quantidadeReceber;
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getQuantidadeEmbalagemFatorConversao() {
		return quantidadeEmbalagemFatorConversao;
	}

	public void setQuantidadeEmbalagemFatorConversao(
			Integer quantidadeEmbalagemFatorConversao) {
		this.quantidadeEmbalagemFatorConversao = quantidadeEmbalagemFatorConversao;
	}

	public Integer getQuantidadeConvertida() {
		return quantidadeConvertida;
	}

	public void setQuantidadeConvertida(Integer quantidadeConvertida) {
		this.quantidadeConvertida = quantidadeConvertida;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getMaterialCodigoNome() {
		if(this.material != null){
			this.materialCodigoNome = this.material.getCodigo() + " - " + this.material.getNome();	
		}
		return materialCodigoNome;
	}
	
	public void setMaterialCodigoNome(String materialCodigoNome) {
		this.materialCodigoNome = materialCodigoNome;
	}
	
	public String getMarcaComercialCodigoNome() {
		if(this.marcaComercial != null){
			this.marcaComercialCodigoNome = this.marcaComercial.getCodigo() + " - " + this.marcaComercial.getDescricao();	
		}
		return marcaComercialCodigoNome;
	}
	
	public void setMarcaComercialCodigoNome(String marcaComercialCodigoNome) {
		this.marcaComercialCodigoNome = marcaComercialCodigoNome;
	}
	
	public String getUnidadeMedidaCodigoDescricao() {
		if(this.unidadeMedida != null){
			this.unidadeMedidaCodigoDescricao = this.unidadeMedida.getCodigo() + " - " + this.unidadeMedida.getDescricao();
		}
		return unidadeMedidaCodigoDescricao;
	
	}
	
	public void setUnidadeMedidaCodigoDescricao(String unidadeMedidaCodigoDescricao) {
		this.unidadeMedidaCodigoDescricao = unidadeMedidaCodigoDescricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((afnNumero == null) ? 0 : afnNumero.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GerarItemNotaRecebimentoSemAfVO other = (GerarItemNotaRecebimentoSemAfVO) obj;
		if (afnNumero == null) {
			if (other.afnNumero != null) {
				return false;
			}
		} else if (!afnNumero.equals(other.afnNumero)) {
			return false;
		}
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else if (!numero.equals(other.numero)) {
			return false;
		}
		return true;
	}

}	
	