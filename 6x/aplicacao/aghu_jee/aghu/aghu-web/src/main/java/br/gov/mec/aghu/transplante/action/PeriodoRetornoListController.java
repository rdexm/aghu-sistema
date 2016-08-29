package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.PeriodosRetornoVO;
import br.gov.mec.aghu.core.action.ActionController;

public class PeriodoRetornoListController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1983280417265871203L;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	private boolean exibirGrid = Boolean.FALSE;
	private boolean gridRepeticoes = false;
	private boolean exibirDescricao = Boolean.FALSE;
	private boolean btNovo;
	private String descricao;
	private DominioRepeticaoRetorno repeticao;
	private DominioSimNao dominioSimNao;
	private List<PeriodosRetornoVO> listaPeriodosRetornoVO;
	private MtxPeriodoRetorno selecionado = null;
	private List<MtxPeriodoRetorno> listaPeriodosRetorno;
	private MtxPeriodoRetorno mtxPeriodoRetorno = new MtxPeriodoRetorno();
	private static final String PERIODO_RETORNO_CRUD = "transplante-periodoRetornoCRUD";
	private MtxTipoRetorno selecaoDescricao;
	private List<MtxItemPeriodoRetorno> listaItemPeriodoRetorno;
	private MtxItemPeriodoRetorno selecionadoIPR;
	

  
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		if(exibirGrid){
			pesquisarPais();
		}else{
			btNovo = false;
		}
		
	}

	public void pesquisarFilhos(){
			controleExibicaoGridPesquisa(true,true);
			List<MtxPeriodoRetorno> listaPeriodoRetorno = this.transplanteFacade.consultarPeriodoRetorno(mtxPeriodoRetorno, repeticao, selecionado);
			if(listaPeriodoRetorno !=null && !listaPeriodoRetorno.isEmpty()){
				listaItemPeriodoRetorno = this.transplanteFacade.consultarItensPeriodoRetorno(listaPeriodoRetorno.get(0));
				
			}
			
	}
	
	public void pesquisarPais() {
		controleExibicaoGridPesquisa(true,false);
		selecionado = null;
		btNovo = true;	
		mtxPeriodoRetorno.setIndSituacao(dominioSimNao != null ? DominioSituacao.getInstance(dominioSimNao.isSim()) : null);
		mtxPeriodoRetorno.getTipoRetorno().setDescricao(selecaoDescricao != null ? selecaoDescricao.getDescricao():null);
		listaPeriodosRetorno = sortMtxPR(this.transplanteFacade.consultarPeriodoRetorno(mtxPeriodoRetorno, repeticao, selecionado));
	}
	
	private void controleExibicaoGridPesquisa(final boolean isExibirGrid, final boolean isGridRepeticoes ) {
		this.exibirGrid = isExibirGrid;
		this.gridRepeticoes = isGridRepeticoes;
	}
	
	private List<MtxPeriodoRetorno> sortMtxPR(List<MtxPeriodoRetorno> lista) {
		Set<MtxPeriodoRetorno> setListaMtxPR = new TreeSet<MtxPeriodoRetorno>();
		setListaMtxPR.addAll(lista);
		return new ArrayList<MtxPeriodoRetorno>(setListaMtxPR);
	}
	
	public List<MtxTipoRetorno> pesquisarTipoRetorno(String objParam) {		
		if( mtxPeriodoRetorno.getTipoRetorno().getIndTipo() != null){
			List<MtxTipoRetorno> listaTipoRetorno = new ArrayList<MtxTipoRetorno>();
			Long countTipoRetorno = (long) 0;
			listaTipoRetorno = transplanteFacade.listarPeriodoRetorno(mtxPeriodoRetorno.getTipoRetorno().getIndTipo(), objParam.trim());
			countTipoRetorno = transplanteFacade.listarPeriodoRetornoCount(mtxPeriodoRetorno.getTipoRetorno().getIndTipo(), objParam.trim());
			return this.returnSGWithCount(listaTipoRetorno,countTipoRetorno);			
		}
		return null;		
	}
	
	public Long pesquisarTipoRetornoCount(DominioTipoRetorno indTipo, String param) {
		return transplanteFacade.listarPeriodoRetornoCount(indTipo, param);
	}
	
	
	public void habilitarDescricao(){
		limparCampo();
		if(mtxPeriodoRetorno.getTipoRetorno().getIndTipo() != null){
			this.exibirDescricao = Boolean.TRUE;
		}else{
			this.exibirDescricao = Boolean.FALSE;
		}
	}
	
	public void limpar(){
		this.exibirDescricao = Boolean.FALSE;
		this.exibirGrid = Boolean.FALSE;
		this.repeticao = null;
		this.dominioSimNao = null;
		this.selecionado = null;
		this.listaPeriodosRetorno = null;
		this.mtxPeriodoRetorno = new MtxPeriodoRetorno( );
		this.descricao = null;
		this.selecaoDescricao=null;
		this.listaItemPeriodoRetorno = null;
		this.selecionadoIPR = null;
		this.gridRepeticoes = true;
		btNovo = false;
	}
	
	public void limparCampo(){
		this.selecaoDescricao=null;
	}
	
	public String novo(){
		return PERIODO_RETORNO_CRUD;
	}
	
	public String truncar(String descricao, int tamMax) {
		if(descricao.length() > tamMax){
			return StringUtils.abbreviate(descricao, tamMax); 
		}
		return descricao;
	}
	
	public String editar(){
		return PERIODO_RETORNO_CRUD;
	}
	


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioRepeticaoRetorno getRepeticao() {
		return repeticao;
	}

	public void setRepeticao(DominioRepeticaoRetorno repeticao) {
		this.repeticao = repeticao;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}

	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}

	public List<PeriodosRetornoVO> getListaPeriodosRetornoVO() {
		return listaPeriodosRetornoVO;
	}

	public void setListaPeriodosRetornoVO(List<PeriodosRetornoVO> listaPeriodosRetornoVO) {
		this.listaPeriodosRetornoVO = listaPeriodosRetornoVO;
	}

	public MtxPeriodoRetorno getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MtxPeriodoRetorno selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isExibirGrid() {
		return exibirGrid;
	}

	public void setExibirGrid(boolean exibirGrid) {
		this.exibirGrid = exibirGrid;
	}

	public List<MtxPeriodoRetorno> getListaPeriodosRetorno() {
		return listaPeriodosRetorno;
	}

	public void setListaPeriodosRetorno(List<MtxPeriodoRetorno> listaPeriodosRetorno) {
		this.listaPeriodosRetorno = listaPeriodosRetorno;
	}
	
	public MtxPeriodoRetorno getMtxPeriodoRetorno() {
		return mtxPeriodoRetorno;
	}

	public void setMtxPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno) {
		this.mtxPeriodoRetorno = mtxPeriodoRetorno;
	}

	public boolean isExibirDescricao() {
		return exibirDescricao;
	}

	public void setExibirDescricao(boolean exibirDescricao) {
		this.exibirDescricao = exibirDescricao;
	}

	public MtxTipoRetorno getSelecaoDescricao() {
		return selecaoDescricao;
	}

	public void setSelecaoDescricao(MtxTipoRetorno selecaoDescricao) {
		this.selecaoDescricao = selecaoDescricao;
	}

	public List<MtxItemPeriodoRetorno>getListaItemPeriodoRetorno() {
		return listaItemPeriodoRetorno;
	}

	public void setListaItemPeriodoRetorno(List<MtxItemPeriodoRetorno> listaItemPeriodoRetorno) {
		this.listaItemPeriodoRetorno = listaItemPeriodoRetorno;
	}

	public MtxItemPeriodoRetorno getSelecionadoIPR() {
		return selecionadoIPR;
	}

	public void setSelecionadoIPR(MtxItemPeriodoRetorno selecionadoIPR) {
		this.selecionadoIPR = selecionadoIPR;
	}

	public boolean isGridRepeticoes() {
		return gridRepeticoes;
	}

	public void setGridRepeticoes(boolean gridRepeticoes) {
		this.gridRepeticoes = gridRepeticoes;
	}

	public boolean isBtNovo() {
		return btNovo;
	}

	public void setBtNovo(boolean btNovo) {
		this.btNovo = btNovo;
	}
}
