package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;

/**
 * Vo utilizado na duplicação do PAC
 * 
 * @author frutkowski
 */
public class DupItensPACVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;
	
	private Boolean naoDuplicar;
	private Short loteNumero;
	private Integer numeroLicitacao;
	private Short numeroItem;
	private DominioTipoSolicitacao tipoSolicitacao;
    private Integer codigoMaterialServico;	
	private String nomeMaterialServico;
	private Long qtdeSolicitada;
	private String unidadeMedidaCodigo;
	private BigDecimal valorUnitPrevisto;
	private Integer numeroSolicitacao;
	private Boolean exclusao;
	
	
	public Boolean getNaoDuplicar() {
		return naoDuplicar;
	}
	public void setNaoDuplicar(Boolean naoDuplicar) {
		this.naoDuplicar = naoDuplicar;
	}
	public Short getLoteNumero() {
		return loteNumero;
	}
	public void setLoteNumero(Short loteNumero) {
		this.loteNumero = loteNumero;
	}
	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}
	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}
	public Short getNumeroItem() {
		return numeroItem;
	}
	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}
	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}
	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	public Integer getCodigoMaterialServico() {
		return codigoMaterialServico;
	}
	public void setCodigoMaterialServico(Integer codigoMaterialServico) {
		this.codigoMaterialServico = codigoMaterialServico;
	}
	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}
	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}
	public Long getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Long qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	
	
	public String getUnidadeMedidaCodigo() {
		return unidadeMedidaCodigo;
	}
	public void setUnidadeMedidaCodigo(String unidadeMedidaCodigo) {
		this.unidadeMedidaCodigo = unidadeMedidaCodigo;
	}
	public BigDecimal getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}
	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}
	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}
	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}
	public Boolean getExclusao() {
		return exclusao;
	}
	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}
		
	
			
}
