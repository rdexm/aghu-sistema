package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferenciaId;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class ItemTransferenciaAutomaticaVO {
	
	private SceItemTransferenciaId id;
	private SceTransferencia transferencia;
	private SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem;
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;
	private ScoUnidadeMedida unidadeMedida;
	private Integer qtdeEnviada;
	private Integer quantidade;
	
	public SceItemTransferenciaId getId() {
		return id;
	}
	public void setId(SceItemTransferenciaId id) {
		this.id = id;
	}
	public SceTransferencia getTransferencia() {
		return transferencia;
	}
	public void setTransferencia(SceTransferencia transferencia) {
		this.transferencia = transferencia;
	}
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifadoOrigem() {
		return estoqueAlmoxarifadoOrigem;
	}
	public void setEstoqueAlmoxarifadoOrigem(
			SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem) {
		this.estoqueAlmoxarifadoOrigem = estoqueAlmoxarifadoOrigem;
	}
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}
	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	public Integer getQtdeEnviada() {
		return qtdeEnviada;
	}
	public void setQtdeEnviada(Integer qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	

}
