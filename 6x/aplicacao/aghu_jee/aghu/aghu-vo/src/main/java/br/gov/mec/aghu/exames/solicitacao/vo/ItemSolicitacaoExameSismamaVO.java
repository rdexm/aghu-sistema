package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public class ItemSolicitacaoExameSismamaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9204345409547813887L;
	
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	private String descricaoUsualExame;
	private String descricaoMaterialAnalise;
	
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}
	
	public void setItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	
	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}
	
	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}
	
	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}
	
	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}
}
