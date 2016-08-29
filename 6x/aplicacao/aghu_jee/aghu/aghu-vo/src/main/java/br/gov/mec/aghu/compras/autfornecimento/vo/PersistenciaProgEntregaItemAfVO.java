package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;


/**
 * 
 * 
 * 
 */
public class PersistenciaProgEntregaItemAfVO implements Serializable {

	private static final long serialVersionUID = -260135187653694605L;

	private Boolean existeEntregaProgramada;
	private Boolean persistirParcela;
	private ScoProgEntregaItemAutorizacaoFornecimento progEntrega;
	
	public Boolean getExisteEntregaProgramada() {
		return existeEntregaProgramada;
	}
	public void setExisteEntregaProgramada(Boolean existeEntregaProgramada) {
		this.existeEntregaProgramada = existeEntregaProgramada;
	}
	public Boolean getPersistirParcela() {
		return persistirParcela;
	}
	public void setPersistirParcela(Boolean persistirParcela) {
		this.persistirParcela = persistirParcela;
	}
	public ScoProgEntregaItemAutorizacaoFornecimento getProgEntrega() {
		return progEntrega;
	}
	public void setProgEntrega(ScoProgEntregaItemAutorizacaoFornecimento progEntrega) {
		this.progEntrega = progEntrega;
	}
}