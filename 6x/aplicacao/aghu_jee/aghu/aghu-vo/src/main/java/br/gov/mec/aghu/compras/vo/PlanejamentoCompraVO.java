package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class PlanejamentoCompraVO {

	private Integer slcNumero;
	private ScoItemAutorizacaoForn itemAf;
	private Boolean mostrarLinkParcelas;
	private Boolean mostrarLinkLibRef;
	private Boolean mostrarLinkAf;
	private Boolean mostraLinkLibAss;
	private Boolean protegerQtdeAprovRef;
	
    public PlanejamentoCompraVO() {
    }
    
	public Integer getSlcNumero() {
		return slcNumero;
	}
	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}
	public ScoItemAutorizacaoForn getItemAf() {
		return itemAf;
	}
	public void setItemAf(ScoItemAutorizacaoForn itemAf) {
		this.itemAf = itemAf;
	}
	public Boolean getMostrarLinkParcelas() {
		return mostrarLinkParcelas;
	}
	public void setMostrarLinkParcelas(Boolean mostrarLinkParcelas) {
		this.mostrarLinkParcelas = mostrarLinkParcelas;
	}
	public Boolean getMostrarLinkLibRef() {
		return mostrarLinkLibRef;
	}
	public void setMostrarLinkLibRef(Boolean mostrarLinkLibRef) {
		this.mostrarLinkLibRef = mostrarLinkLibRef;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((slcNumero == null) ? 0 : slcNumero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		PlanejamentoCompraVO other = (PlanejamentoCompraVO) obj;
		if (slcNumero == null) {
			if (other.getSlcNumero() != null){
				return false;
			}
		} else if (!slcNumero.equals(other.getSlcNumero())){
			return false;
		}
		return true;
	}

	public Boolean getMostrarLinkAf() {
		return mostrarLinkAf;
	}

	public void setMostrarLinkAf(Boolean mostrarLinkAf) {
		this.mostrarLinkAf = mostrarLinkAf;
	}

	public Boolean getMostraLinkLibAss() {
		return mostraLinkLibAss;
	}

	public void setMostraLinkLibAss(Boolean mostraLinkLibAss) {
		this.mostraLinkLibAss = mostraLinkLibAss;
	}

	public Boolean getProtegerQtdeAprovRef() {
		return protegerQtdeAprovRef;
	}

	public void setProtegerQtdeAprovRef(Boolean protegerQtdeAprovRef) {
		this.protegerQtdeAprovRef = protegerQtdeAprovRef;
	}

}
