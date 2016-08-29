package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class SumarioAltaDiagnosticosVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5145531172178019836L;

	private enum SumarioAltaDiagnosticosVOExceptionCode implements BusinessExceptionCode {
		ERRO_SUMARIO_ALTA_CID_NAO_INFORMADO, ERRO_SUMARIO_ALTA_NRO_MAX_ITENS_LISTA;
	}
	
	private MpmAltaSumarioId id = null; 
	
	// 2 - Lista do combo de CIDs
	private List<SumarioAltaDiagnosticosCidVO> listaCombo;
	// 2.1 - Item selecionado do combo de CIDs (VO de Cid - colunas cid_seq , cia_seq, dia_seq, desc_cid, ind_montado)
	private Integer indiceSelecionadoCombo;	
	// 3 - Retorno do "botão Por Capítulo"
	private AghCid cidPorCapitulo = null;
	// 4 - Método de pesquisa da suggestion de CID - OK (pesquisarCids)
	// 4.1 - Item selecionado na suggestion de CID
	private AghCid cidSuggestion = null;
	// 5 - Complemento do item da suggestion de CID
	private String complementoCidSuggestion;
	// 6 - Método Gravar do Motivo de Internação, adiciona na lista do item 7. 
	// 7 - Lista de MPM_ALTA_DIAG_MTVO_INTERNACOES, concatenando o item da suggestion com o seu complemento.
	private List<SumarioAltaDiagnosticosCidVO> listaGrid;	
	private SumarioAltaDiagnosticosCidVO itemEmEdicao;
	private Integer maxItemsListaCids;
	//private SumarioAltaDiagnosticosCidVOComparator comparator;
	
	public SumarioAltaDiagnosticosVO(MpmAltaSumarioId id, Integer maxItemsListaCids) {
		this.id = id;
		this.maxItemsListaCids = maxItemsListaCids;
		this.listaCombo = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		this.listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		this.novoItemEmEdicao();
		//this.comparator = new SumarioAltaDiagnosticosCidVOComparator();
	}

	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}

	public MpmAltaSumarioId getId() {
		return this.id;
	}
	
	public SumarioAltaDiagnosticosCidVO novoItem() {
		return new SumarioAltaDiagnosticosCidVO(this.id, null);
	}
	
	private void novoItemEmEdicao() {
		this.itemEmEdicao = novoItem();
		this.cidSuggestion = null;
		this.complementoCidSuggestion = null;
	}
	
	/*
	@Deprecated
	public void gravarItemEmEdicao() throws BaseRuntimeException {
		if (this.cidSuggestion == null) {
			throw new BaseRuntimeException(
					SumarioAltaDiagnosticosVOExceptionCode.ERRO_SUMARIO_ALTA_CID_NAO_INFORMADO);
		}
		this.itemEmEdicao.setCid(this.cidSuggestion);
		this.itemEmEdicao.setComplementoEditado(this.complementoCidSuggestion);

		this.adicionarItemListaGrid(this.itemEmEdicao);

		this.itemEmEdicao.setEmEdicao(Boolean.FALSE);
		this.novoItemEmEdicao();

	}*/
	
	public void validarItemEmEdicao() throws BaseException {
		if (this.cidSuggestion == null) {
			throw new BaseException(SumarioAltaDiagnosticosVOExceptionCode.ERRO_SUMARIO_ALTA_CID_NAO_INFORMADO);
		}
		this.itemEmEdicao.setCid(this.cidSuggestion);
		this.itemEmEdicao.setComplementoEditado(this.complementoCidSuggestion);
	}
	/*
	@Deprecated
	public void atualizarItemEmEdicao() {
		this.itemEmEdicao.setComplementoEditado(this.complementoCidSuggestion);
		this.cancelarItemEmEdicao();		
	}*/
	
	/*public void cancelarItemEmEdicao() {
		this.itemEmEdicao.setEmEdicao(Boolean.FALSE);
		this.novoItemEmEdicao();
	}*/
	
	public SumarioAltaDiagnosticosCidVO getItemSelecionadoCombo() {
		SumarioAltaDiagnosticosCidVO itemSelecionadoCombo = null;
		
		if (this.indiceSelecionadoCombo != null
				&& this.indiceSelecionadoCombo >= 0
				&& this.indiceSelecionadoCombo < this.listaCombo.size()) {
			itemSelecionadoCombo = this.listaCombo.get(this.indiceSelecionadoCombo);
		}
		
		return itemSelecionadoCombo;
	}
	
	/*@Deprecated
	public void moverItemSelecionadoComboParaGrid() throws BaseRuntimeException {
		if (this.indiceSelecionadoCombo != null) {
			SumarioAltaDiagnosticosCidVO itemSelecionadoCombo = this.listaCombo
					.get(this.indiceSelecionadoCombo);
			adicionarItemListaGrid(itemSelecionadoCombo);
		}

		this.indiceSelecionadoCombo = null;
	}
	
	@Deprecated
	public void adicionarCidPorCapituloNaGrid()
			throws BaseRuntimeException {
		SumarioAltaDiagnosticosCidVO item = novoItem();
		item.setCid(this.cidPorCapitulo);
		this.adicionarItemListaGrid(item);

		this.cidPorCapitulo = null;
	}*/
	
	/*private void adicionarItemListaGrid(SumarioAltaDiagnosticosCidVO item) throws BaseRuntimeException {
		if (this.maxItemsListaCids != null && this.listaGrid.size() >= this.maxItemsListaCids) {
			throw new BaseRuntimeException(SumarioAltaDiagnosticosVOExceptionCode.ERRO_SUMARIO_ALTA_NRO_MAX_ITENS_LISTA, this.maxItemsListaCids);
		}
		
		if (item.getCid() == null) {
			throw new BaseRuntimeException(SumarioAltaDiagnosticosVOExceptionCode.ERRO_SUMARIO_ALTA_CID_NAO_INFORMADO);
		}
		
		if (item.getOrigemListaCombo() && this.listaCombo.contains(item)) {
			this.listaCombo.remove(item);
		}		
			
		if (this.listaGrid.contains(item)) {
			int index = this.listaGrid.indexOf(item);
			this.listaGrid.get(index).setComplementoEditado(item.getComplementoEditado());
		} else {
			this.listaGrid.add(item);
		}
		Collections.sort(this.listaGrid, this.comparator);
	}*/
	
	/*@Deprecated
	public void excluirItemGrid(SumarioAltaDiagnosticosCidVO itemGrid) {
		itemGrid.setEmEdicao(Boolean.FALSE);
		if (itemGrid.equals(this.itemEmEdicao)) {
			this.novoItemEmEdicao();
		}
		this.listaGrid.remove(itemGrid);
		
		if (itemGrid.getOrigemListaCombo() && !this.listaCombo.contains(itemGrid)) {
			this.listaCombo.add(itemGrid);
		}
		Collections.sort(this.listaCombo, this.comparator);
	}*/
	
	/*public void editarItemGrid(SumarioAltaDiagnosticosCidVO itemGrid) {
		for (SumarioAltaDiagnosticosCidVO item : this.listaGrid) {
			item.setEmEdicao(Boolean.FALSE);
		}
		itemGrid.setEmEdicao(Boolean.TRUE);
		this.cidSuggestion = itemGrid.getCid();
		this.complementoCidSuggestion = itemGrid.getComplementoEditado();
		this.setItemEmEdicao(itemGrid);		
	}*/
	
	public List<SumarioAltaDiagnosticosCidVO> getListaCombo() {
		return this.listaCombo;
	}

	public void setListaCombo(List<SumarioAltaDiagnosticosCidVO> listaCombo) {
		this.listaCombo = listaCombo;
	}

	public AghCid getCidPorCapitulo() {
		return this.cidPorCapitulo;
	}

	public void setCidPorCapitulo(AghCid cidPorCapitulo) {
		this.cidPorCapitulo = cidPorCapitulo;
	}

	public AghCid getCidSuggestion() {
		return this.cidSuggestion;
	}

	public void setCidSuggestion(AghCid cidSuggestion) {
		this.cidSuggestion = cidSuggestion;
	}

	public String getComplementoCidSuggestion() {
		return this.complementoCidSuggestion;
	}

	public void setComplementoCidSuggestion(String complementoCidSuggestion) {
		this.complementoCidSuggestion = complementoCidSuggestion;
	}

	public List<SumarioAltaDiagnosticosCidVO> getListaGrid() {
		return this.listaGrid;
	}

	public void setListaGrid(List<SumarioAltaDiagnosticosCidVO> listaGrid) {
		this.listaGrid = listaGrid;
	}

	public SumarioAltaDiagnosticosCidVO getItemEmEdicao() {
		return this.itemEmEdicao;
	}

	public void setItemEmEdicao(SumarioAltaDiagnosticosCidVO itemEmEdicao) {
		this.itemEmEdicao = itemEmEdicao;
	}

	public Integer getMaxItemsListaCids() {
		return this.maxItemsListaCids;
	}

	public void setMaxItemsListaCids(Integer maxItemsListaCids) {
		this.maxItemsListaCids = maxItemsListaCids;
	}

	public void setIndiceSelecionadoCombo(Integer indiceSelecionadoCombo) {
		this.indiceSelecionadoCombo = indiceSelecionadoCombo;
	}

	public Integer getIndiceSelecionadoCombo() {
		return this.indiceSelecionadoCombo;
	}
	
}