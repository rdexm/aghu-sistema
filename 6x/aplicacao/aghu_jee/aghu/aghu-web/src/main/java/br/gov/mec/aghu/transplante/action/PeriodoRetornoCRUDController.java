package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.PeriodosRetornoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class PeriodoRetornoCRUDController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1983280037265815203L;
	
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	

	
	private boolean ocultarGrid = Boolean.TRUE;
	private boolean exibirDescricao = Boolean.TRUE;
	private boolean edicao = Boolean.FALSE;
	private boolean ativarBtEditar= Boolean.TRUE;
	private boolean podeGravar;
	private static final String MS01 = "LABEL_MS_QTDE_INVALIDA";
	
	


	private String descricao;
	private DominioRepeticaoRetorno repeticao;
	private DominioSimNao dominioSimNao;
	private List<PeriodosRetornoVO> listaPeriodosRetornoVO;
	private PeriodosRetornoVO selecionado;
	private List<MtxPeriodoRetorno> listaPeriodosRetorno;
	private MtxPeriodoRetorno mtxPeriodoRetorno = new MtxPeriodoRetorno();
	private static final String PERIODO_RETORNO_LIST = "transplante-periodoRetornoList";
	private Integer quantidadeRepeticao;
	private Integer quantidade;
	private Integer ordem;
	private List<MtxItemPeriodoRetorno> listaItensPeriodoRetorno = new ArrayList<MtxItemPeriodoRetorno>();
	private List<MtxItemPeriodoRetorno> listaItensPeriodoRetornoExcluir = new ArrayList<MtxItemPeriodoRetorno>();
	private MtxTipoRetorno selecionaDescricao;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		
		
	}
	
	public void iniciar(){
		quantidadeRepeticao = null;
		quantidade = null;
		if(edicao){
			this.ocultarGrid = true;
			
			dominioSimNao  = DominioSimNao.getInstance(mtxPeriodoRetorno.getIndSituacao() != null ? mtxPeriodoRetorno.getIndSituacao().isAtivo() : true);
				List<MtxItemPeriodoRetorno> listaPeriodoRetorno = this.transplanteFacade.consultarItensPeriodoRetorno(mtxPeriodoRetorno);
				if(listaPeriodoRetorno != null){
					listaItensPeriodoRetorno = listaPeriodoRetorno;
				}
				if(selecionaDescricao == null){
				selecionaDescricao=mtxPeriodoRetorno.getTipoRetorno();
				}
			ordem= listaItensPeriodoRetorno.size() +1;
			
		}else{
			dominioSimNao  = DominioSimNao.getInstance(mtxPeriodoRetorno.getIndSituacao() != null ? mtxPeriodoRetorno.getIndSituacao().isAtivo() : true);
			this.mtxPeriodoRetorno.getTipoRetorno().setIndTipo(DominioTipoRetorno.A);
			this.setRepeticao(DominioRepeticaoRetorno.S);
			this.setDominioSimNao(DominioSimNao.S);
			this.ordem = 1;
		}
	}

	public void adcionar(){
		if(contemDominioRepeticaoRetorno(repeticao)){
			apresentarMsgNegocio(Severity.FATAL, "LABEL_MS_REPETICAO_DUPLICADA");
			
		}else{
			
			MtxItemPeriodoRetorno mtxItemPeriodoRetorno = new MtxItemPeriodoRetorno();			
			mtxItemPeriodoRetorno.setIndRepeticao(repeticao);
			mtxItemPeriodoRetorno.setQuantidade(quantidadeRepeticao);
			mtxItemPeriodoRetorno.setOrdem(ordem);
			
			listaItensPeriodoRetorno.add(mtxItemPeriodoRetorno);
			this.ordem = ordem + 1;
			this.ativarBtEditar = true;
			this.quantidadeRepeticao = null;
			this.setRepeticao(repeticao);
		}
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
			this.exibirDescricao = Boolean.FALSE;
		}else{
			this.exibirDescricao = Boolean.TRUE;
		}
	}
	
	public void limparCampo(){
		this.selecionaDescricao=null;		
	}
	
	public void limpar(){
		this.ocultarGrid = Boolean.TRUE;
		this.repeticao = null;
		this.dominioSimNao = null;
		this.selecionado = null;
		this.listaPeriodosRetorno = null;
		this.mtxPeriodoRetorno = new MtxPeriodoRetorno( );
		this.descricao = null;
		this.ativarBtEditar = true;
		this.quantidadeRepeticao = null;
		this.quantidade = null;
		listaItensPeriodoRetorno = new ArrayList<MtxItemPeriodoRetorno>();
		listaItensPeriodoRetornoExcluir = new ArrayList<MtxItemPeriodoRetorno>();
		this.ordem = 1;
		this.edicao = false;
		podeGravar = true;
		limparCampo();
	}
	
	public boolean contemDominioRepeticaoRetorno(DominioRepeticaoRetorno repeticao){
		for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItensPeriodoRetorno) {
			if(mtxItemPeriodoRetorno.getIndRepeticao().equals(repeticao)){
				return true;
			}
		}
		return false;
	}
	
	public String cancelar(){
		limpar();
		return PERIODO_RETORNO_LIST;
	}
	
	public void verificarQtd(){
		if(quantidadeRepeticao == null){
			apresentarMsgNegocio(Severity.FATAL, MS01);
			ativarBtEditar = true;
		}else if (quantidadeRepeticao < 1){
			apresentarMsgNegocio(Severity.FATAL, MS01);
			ativarBtEditar = true;
		}else{
			ativarBtEditar = false;	
		}
	}
	
	public void reordenar(){
		List<Integer> listaAux = new ArrayList<Integer>();
		for( MtxItemPeriodoRetorno lista : listaItensPeriodoRetorno) {
			listaAux.add(lista.getOrdem());
		}
		
		Collections.sort(listaAux);
		
		for(int i = 0 ; i < listaItensPeriodoRetorno.size();i++) {
			listaItensPeriodoRetorno.get(i).setOrdem(listaAux.get(i));
		}
				
	}
	public Integer reordenarExclusao(){
		Integer index = 1;
		for(int i = 0 ; i < listaItensPeriodoRetorno.size();i++) {
			listaItensPeriodoRetorno.get(i).setOrdem(index);
			index = index+1;
		}
		return index;
	}

	public String gravar(){
		if(listaItensPeriodoRetorno.isEmpty()) {
			apresentarMsgNegocio(Severity.FATAL, "LABEL_MS_NENHUMA_REPETICAO_SELECIONADA");
			return null;
		}else if(verificaQuantidadeInvalida(listaItensPeriodoRetorno)){
			apresentarMsgNegocio(Severity.FATAL, MS01);
			ativarBtEditar = true;
			return null;
		}else {
			if(edicao){
			transplanteFacade.editarRegistroPeriodoRetorno(mtxPeriodoRetorno, selecionaDescricao,dominioSimNao,listaItensPeriodoRetorno,listaItensPeriodoRetornoExcluir);
			}else{
			transplanteFacade.gravarRegistroPeriodoRetorno(mtxPeriodoRetorno, selecionaDescricao,dominioSimNao,listaItensPeriodoRetorno);
			}
		}
		limpar();
		return PERIODO_RETORNO_LIST;
	}
	
	public void upItem(Integer ordem) {
		Collections.swap(listaItensPeriodoRetorno, ordem, ordem-1);
		reordenar();
	}

	public void downItem(Integer ordem) {
		Collections.swap(listaItensPeriodoRetorno, ordem, ordem + 1);
		reordenar();
	}
	
	public void excluirItem(Integer index){
		listaItensPeriodoRetornoExcluir.add(listaItensPeriodoRetorno.get(index));
		listaItensPeriodoRetorno.remove(index.intValue());
		this.ordem=reordenarExclusao();		
	}
	
	
	public Boolean verificaQuantidadeInvalida(List<MtxItemPeriodoRetorno> listaItensPeriodoRetorno){
		
		for(int i = 0 ; i < listaItensPeriodoRetorno.size();i++) {
			if(listaItensPeriodoRetorno.get(i).getQuantidade() == null){
				return true;
			}else if(listaItensPeriodoRetorno.get(i).getQuantidade() < 1){
				return true;
			}
		}
		
		return false;	
	}
	
	public void atualizarQuantidade(Integer index, Integer qtd ){
		listaItensPeriodoRetorno.get(index).setQuantidade(qtd);
	}
	

	public Integer getSizeItensPeriodoRetorno() {
		return listaItensPeriodoRetorno.size();
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

	public PeriodosRetornoVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(PeriodosRetornoVO selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isOcultarGrid() {
		return ocultarGrid;
	}

	public void setOcultarGrid(boolean ocultarGrid) {
		this.ocultarGrid = ocultarGrid;
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

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getQuantidadeRepeticao() {
		return quantidadeRepeticao;
	}

	public void setQuantidadeRepeticao(Integer quantidadeRepeticao) {
		this.quantidadeRepeticao = quantidadeRepeticao;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public boolean isAtivarBtEditar() {
		return ativarBtEditar;
	}

	public void setAtivarBtEditar(boolean ativarBtEditar) {
		this.ativarBtEditar = ativarBtEditar;
	}

	public List<MtxItemPeriodoRetorno> getListaItensPeriodoRetorno() {
		return listaItensPeriodoRetorno;
	}

	public void setListaItensPeriodoRetorno(List<MtxItemPeriodoRetorno> listaItensPeriodoRetorno) {
		this.listaItensPeriodoRetorno = listaItensPeriodoRetorno;
	}

	public MtxTipoRetorno getSelecionaDescricao() {
		return selecionaDescricao;
	}

	public void setSelecionaDescricao(MtxTipoRetorno selecionaDescricao) {
		this.selecionaDescricao = selecionaDescricao;
	}

	public List<MtxItemPeriodoRetorno> getListaItensPeriodoRetornoExcluir() {
		return listaItensPeriodoRetornoExcluir;
	}

	public void setListaItensPeriodoRetornoExcluir(
			List<MtxItemPeriodoRetorno> listaItensPeriodoRetornoExcluir) {
		this.listaItensPeriodoRetornoExcluir = listaItensPeriodoRetornoExcluir;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public boolean isPodeGravar() {
		return podeGravar;
	}

	public void setPodeGravar(boolean podeGravar) {
		this.podeGravar = podeGravar;
	}
}

