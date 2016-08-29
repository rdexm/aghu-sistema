package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmModoUsoPrescProcedId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;

/**
 * @author rcorvalao
 */
public class ModoUsoProcedimentoEspecialVO implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -863161803807280724L;

	private MpmModoUsoPrescProcedId itemId;
	
	private MpmModoUsoPrescProced umModoUso;
	
	private Boolean emEdicao;
	
	/**
	 * SuggestionBox
	 * Usado na SB de ModoUsoProcedimentoEspeciaisDiversos
	 * mpmModoUsoPrescProced.mpmTipoModoUsoProcedimento.descricao
	 */
	private MpmTipoModoUsoProcedimento tipoModoUsoProced;

	/**
	 * mpmModoUsoPrescProced.mpmTipoModoUsoProcedimento.mpmUnidadeMedidaMedica.descricao
	 */
	private String unidade;
	/**
	 * mpmModoUsoPrescProced.quantidade
	 */
	private Short quantidade;
	
	
	public ModoUsoProcedimentoEspecialVO() {
		super();
		this.emEdicao = Boolean.FALSE;
	}

	public ModoUsoProcedimentoEspecialVO(ModoUsoProcedimentoEspecialVO modoUsoVO) {

		this.copyProperties(this, modoUsoVO);

	}
	
	public void atualizarProperties(ModoUsoProcedimentoEspecialVO vo) {
		
		this.copyProperties(this, vo);
		
	}
	
	private void copyProperties(ModoUsoProcedimentoEspecialVO novoModoUsoVO, ModoUsoProcedimentoEspecialVO atualModoUsoVO) {
		
		novoModoUsoVO.setEmEdicao(atualModoUsoVO.getEmEdicao());
		novoModoUsoVO.setItemId(atualModoUsoVO.getItemId());
		novoModoUsoVO.setQuantidade(atualModoUsoVO.getQuantidade());
		novoModoUsoVO.setTipoModoUsoProced(atualModoUsoVO.getTipoModoUsoProced());
		novoModoUsoVO.setUmModoUso(atualModoUsoVO.getUmModoUso());
		novoModoUsoVO.setUnidade(atualModoUsoVO.getUnidade());
		novoModoUsoVO.setModel(atualModoUsoVO.getModel());
		
	}

	public MpmModoUsoPrescProcedId getItemId() {
		return this.itemId;
	}
	
	public void setItemId(MpmModoUsoPrescProcedId itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * mpmModoUsoPrescProced.mpmTipoModoUsoProcedimento.descricao
	 * 
	 * @return
	 */
	public String getModoUso() {
		if (this.getTipoModoUsoProced() != null && this.getTipoModoUsoProced().getDescricao() != null) {
			return this.getTipoModoUsoProced().getDescricao().toUpperCase();
		}
		return null; 
	}
	
	/**
	 * mpmModoUsoPrescProced.mpmTipoModoUsoProcedimento.mpmUnidadeMedidaMedica.descricao
	 * @return
	 */
	public String getUnidade() {
		return (this.unidade != null ? this.unidade.toUpperCase() : null);
	}
	
	/**
	 * mpmModoUsoPrescProced.mpmTipoModoUsoProcedimento.mpmUnidadeMedidaMedica.descricao
	 * @param unidade
	 */
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	/**
	 * mpmModoUsoPrescProced.quantidade
	 * @return
	 */
	public Short getQuantidade() {
		return this.quantidade;
	}
	
	/**
	 * mpmModoUsoPrescProced.quantidade
	 * @param quantidade
	 */
	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public void setTipoModoUsoProced(MpmTipoModoUsoProcedimento umTipoModoUsoProced) {
		this.tipoModoUsoProced = umTipoModoUsoProced;
		this.doSetUnidade(umTipoModoUsoProced);
	}
	
	private void doSetUnidade(MpmTipoModoUsoProcedimento umTipoModoUsoProced) {
		if (umTipoModoUsoProced != null) {
			this.setUnidade(umTipoModoUsoProced.getDescricaoUnidadeMedidaMedica());
		} else {
			this.setUnidade(null);
		}
	}

	public MpmTipoModoUsoProcedimento getTipoModoUsoProced() {
		return this.tipoModoUsoProced;
	}
	
	public boolean isValidaAdicaoEdicao() {
		return (this.getTipoModoUsoProced() != null);
	}
	
	public MpmModoUsoPrescProced getModel() {
		MpmModoUsoPrescProced itemNovo = this.umModoUso == null ? new MpmModoUsoPrescProced() : this.umModoUso;
		
		itemNovo.setId(this.getItemId());
		itemNovo.setQuantidade(this.getQuantidade());
		itemNovo.setTipoModUsoProcedimento(this.getTipoModoUsoProced());
		
		return itemNovo;
	}
	
	public void setModel(MpmModoUsoPrescProced umModoUso) {
		if (umModoUso == null ) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		
		this.setUmModoUso(umModoUso);
		this.setItemId(umModoUso.getId());
		this.setQuantidade(umModoUso.getQuantidade());
		// set da unidade eh feito junto com o set do tipo modo uso.
		this.setTipoModoUsoProced(umModoUso.getTipoModUsoProcedimento());
	}

	public Boolean getEmEdicao() {
		return this.emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public MpmModoUsoPrescProced getUmModoUso() {
		return this.umModoUso;
	}

	public void setUmModoUso(MpmModoUsoPrescProced umModoUso) {
		this.umModoUso = umModoUso;
	}

}