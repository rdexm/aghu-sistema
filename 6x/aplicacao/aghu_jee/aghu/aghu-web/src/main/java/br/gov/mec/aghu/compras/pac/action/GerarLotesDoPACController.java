package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Controller de Localização de Processos de Compra
 * 
 * @author rpanassolo
 */

public class GerarLotesDoPACController extends ActionController {

	private static final long serialVersionUID = -5774965552335019985L;
	
	@EJB
	private IPacFacade pacFacade;
	
	
	private Integer nroPac;
	
	private String modalidade;
	
	private String descricao;
		
	private Date dataGeracao;
	
	private String comprador;
	
	private ScoLicitacao licitacao; 
	
	private List<ScoLoteLicitacao> lotesSolicitacao;
	
	private Short numeroLote;
	
	private String descricaoLote;
	
	private Boolean modoVisualizacaoLote;
	
	private Boolean modoEdicaoLote;
	
	private ScoLoteLicitacaoId idLoteDelecao;
	
	private Integer lctNumeroLoteDelecao;
	
	private Short numeroLoteDelecao;
	
	private Boolean naoPossuiItensAssociados;
	
	private ScoLoteLicitacao itemSelecionado;
	
	private ScoLoteLicitacao loteSelecionado; 
	
	private List<ItensPACVO> itensLicitacao; 
	
	private List<ItensPACVO> itensLicitacaoAtualizar;
	
	private Boolean pesquisou;
	
	private Boolean parecer;
	
	private Boolean semParecer;
	
	private boolean validouLote = false;

	final static String msgModalExclusao = "EXCLUIR";
	
	final static String COMPRA_MATERIAL = "Compra de Material";
	
	final static String PARECER_DESFAVORAVEL = "PD";
	
	private String voltarParaUrl;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		
		modoEdicaoLote = false;
		pesquisou = Boolean.FALSE;
		itemSelecionado = new ScoLoteLicitacao();
		ScoLoteLicitacaoId id = new ScoLoteLicitacaoId();
		naoPossuiItensAssociados = Boolean.FALSE;
		itemSelecionado.setId(id);
		loteSelecionado = null;
		//this.//setIgnoreInitPageConfig(true);
		
		if (nroPac != null){
			this.pesquisar();
		}
		
	
	}
	
	
	public void pesquisar(){
		
		pesquisou = Boolean.TRUE;
		licitacao = pacFacade.obterLicitacaoPorNroPAC(nroPac);
		popularLicitacao();
		lotesSolicitacao = pacFacade.listarLotesPorPac(nroPac);
		modoEdicaoLote = false;
		itemSelecionado = new ScoLoteLicitacao();
		ScoLoteLicitacaoId  id = new ScoLoteLicitacaoId(nroPac,null);
		itemSelecionado.setId(id);
		itensLicitacao = pacFacade.listarItensLictacaoPorPac(nroPac);
		itensLicitacaoAtualizar = new ArrayList<ItensPACVO>();
		loteSelecionado = null;
	}
	
	private void popularLicitacao(){
		if(licitacao!=null){
			setModalidade(licitacao.getModalidadeLicitacao().getDescricao());
			setDescricao(licitacao.getDescricao());
			setDataGeracao(DateUtil.truncaData(licitacao.getDtDigitacao()));
			if(licitacao.getServidorDigitado()!=null && licitacao.getServidorDigitado().getPessoaFisica()!=null){
				setComprador(licitacao.getServidorDigitado().getPessoaFisica().getNome());
			}
		}
	}
	
	public Boolean verificarPesquisaPac(){
		if(lotesSolicitacao!=null && !lotesSolicitacao.isEmpty()){
			return true;
		}
		if(itensLicitacao!=null && !itensLicitacao.isEmpty()){
			return true;
		}
		return false;
	}
	
	public void editar(ScoLoteLicitacao lote){
		itemSelecionado= pacFacade.obterLote(lote);
		modoEdicaoLote = Boolean.TRUE;
		
	}
	
	public Boolean verificarDependenciasDoItem(String tipo, Integer materialCod, Short nroLote ){
		if(tipo.equals("C")){
			return pacFacade.verificarDependenciasDoItem(nroPac, materialCod, nroLote);
		}
		return false;
	}
	
	public Boolean verificarParecerTecnico(String tipo, Integer codigo){
		if(tipo.equals(DominioTipoFaseSolicitacao.C.toString())){
			String parecer = pacFacade.obterParecerAtivo(codigo);
			if(parecer!=null && !parecer.isEmpty()){
				if(parecer.equals(PARECER_DESFAVORAVEL)){
					return true;
				}
			}
		}
		return false;
	}
	
	public Boolean verificarSemParecerTecnico(String tipo, Integer codigo){
		if(tipo.equals(DominioTipoFaseSolicitacao.C.toString())){
			String parecer = pacFacade.obterParecerAtivo(codigo);
			if(!(parecer!=null && !parecer.isEmpty())){
				return true;
			}
		}
		return false;
	}
	
	public void verificarExclusao(Integer lctNumero, Short numero){
		try {
			naoPossuiItensAssociados =  pacFacade.verificarExisteItensAssociados(lctNumero,numero);
			if(naoPossuiItensAssociados){
				ScoLoteLicitacaoId id = new ScoLoteLicitacaoId();
				id.setLctNumero(lctNumero);
				id.setNumero(numero);
				this.setIdLoteDelecao(id);
				this.openDialog("modalConfirmacaoExclusaoWG");
			}
		} catch (ApplicationBusinessException e) {
			naoPossuiItensAssociados = Boolean.FALSE;
		}
		if (!naoPossuiItensAssociados){
			lancarExcecaoExclusao(lctNumero,numero);
	}
	
	}
	
	public void lancarExcecaoExclusao(Integer lctNumero, Short numero){
		try {
			pacFacade.verificarExisteItensAssociados(lctNumero,numero);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	public Boolean verificarExisteLotes(){
		if(nroPac!=null){
			lotesSolicitacao = pacFacade.listarLotesPorPac(nroPac);
			if(lotesSolicitacao!=null && !lotesSolicitacao.isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	public void gerarLotesSemExcluir(){
		if(nroPac!=null){
			if(!verificarExisteLotes()){
				gerarLotesByPacItens();
			}
		}
	}
	
	
	
	public void desfazerExcluirGerarLotes(){
		try {
		pacFacade.desfazerExcluirLotes(lotesSolicitacao, nroPac);
		pesquisar();
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_GERAR_LOTE_ITEM_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void gerarLotesByPacItens(){
		try {
			pacFacade.gerarLotesByPacItens(nroPac);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_GERAR_LOTE_ITEM_SUCESSO");
			pesquisar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
	}
	
	public void cancelarGeracao(){
		this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_GERAR_LOTE_ITEM_GERACAO_CANCELADA");
	}
	
	public void excluirLote(){
		try {
			pacFacade.excluirLoteSolicitacao(idLoteDelecao);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_GERAR_LOTES_SUCESSO_EXCLUIR_LOTE");
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void excluirTodosLotes(){		 
		try {
			pacFacade.excluirTodosLoteSolicitacao(lotesSolicitacao, getNroPac());
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarLotes(){
		try {
			Boolean novo = Boolean.TRUE;
			if(itemSelecionado.getId()!=null && itemSelecionado.getId().getNumero()!=null ){
				novo = Boolean.FALSE;
			}else{
				itemSelecionado.getId().setLctNumero(nroPac);
			}
			pacFacade.gravarLoteSolicitacao(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_GERAR_LOTES_SUCESSO_GRAVAR_LOTE" : "MENSAGEM_GERAR_LOTES_SUCESSO_ALTERAR_LOTE");
			limparDados();
			if(novo){
				pesquisar();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void getSelecionarLote(ScoLoteLicitacao lote){
		loteSelecionado = lote;
	}
	
	public String mostrarCompraServico(DominioTipoFaseSolicitacao tipo){
		if(tipo.equals(DominioTipoFaseSolicitacao.C)){
			return COMPRA_MATERIAL;
		}
		return tipo.getDescricao();
	}
	
	public void validarSeExisteLote(ItensPACVO item){
		try {
			if(item.getNumeroLote()!=null){
				pacFacade.validarSeExisteLote(nroPac, item.getNumeroLote());
			}
			item.setNumeroLicitacao(nroPac);
			validouLote = false;
		} catch (ApplicationBusinessException e) {
			validouLote = true;			
			apresentarExcecaoNegocio(e);
			
		}
		
	}
	
	public void gravar(){
		try {
			List<ItensPACVO> itensLicitacaoNovo = new ArrayList<ItensPACVO>();
			List<ItensPACVO> itensLicitacaoOld = pacFacade.listarItensLictacaoPorPac(nroPac);
			//itensLicitacao 
			verificarAlteracao(itensLicitacaoNovo, itensLicitacaoOld);
			if(!itensLicitacaoNovo.isEmpty()){
				for (ItensPACVO itensPACVO2 : itensLicitacaoNovo) {
					if(itensPACVO2.getNumeroLote()!=null){
						pacFacade.validarSeExisteLote(nroPac, itensPACVO2.getNumeroLote());
					}
					itensLicitacaoAtualizar.add(itensPACVO2);
				}
			}
			if(itensLicitacaoAtualizar!=null && !itensLicitacaoAtualizar.isEmpty()){
				pacFacade.associarItensLote(itensLicitacaoAtualizar);
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_GERAR_LOTES_SUCESSO_ALTERAR_ITENS");
			}else{
				pesquisar();
			}	
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		
	}
	
	private void verificarAlteracao(List<ItensPACVO> itensLicitacaoNovo,
			List<ItensPACVO> itensLicitacaoOld) {
		for (int i = 0; i < itensLicitacaoOld.size(); i++) {
			ItensPACVO old = itensLicitacaoOld.get(i);
			ItensPACVO atual = itensLicitacao.get(i);
			old.setNumeroLicitacao(nroPac);
			atual.setNumeroLicitacao(nroPac);
			Short oldLote = old.getNumeroLote();
			Short atualLote = atual.getNumeroLote();
			if(oldLote == null){
				if(atualLote!=null){
					itensLicitacaoNovo.add(itensLicitacao.get(i));
					
				}
			}else{
				if(!oldLote.equals(atualLote)){
					itensLicitacaoNovo.add(itensLicitacao.get(i));
				}
			}
		}
	}
	
	public void cancelarEdicaoLote(){
		limparDados();
	}
	
	private void limparDados(){
		modoEdicaoLote = false;
		itemSelecionado = new ScoLoteLicitacao();
		itemSelecionado.setDescricao("");
		ScoLoteLicitacaoId id = new ScoLoteLicitacaoId();
		itemSelecionado.setId(id);
	}
	
	
	public Boolean pintarLinha(Short nroLote){
		if(loteSelecionado!=null && loteSelecionado.getId()!=null && loteSelecionado.getId().getNumero()!=null){
			if(loteSelecionado.getId().getNumero().equals(nroLote)){
				return true; 
			}
		}
		return false;
	}
	
	public void adicionarLotesSolicitacao(ItensPACVO itensPac) {
		try {
			ScoLoteLicitacao loteSolicitacao = new ScoLoteLicitacao();
			ScoLoteLicitacaoId id = new ScoLoteLicitacaoId();
			loteSolicitacao.setId(id);
			loteSolicitacao.getId().setLctNumero(nroPac);
			loteSolicitacao.setDescricao(itensPac.getNomeMaterial());
			pacFacade.gravarLoteSolicitacao(loteSolicitacao);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_GERAR_LOTES_SUCESSO_ALTERAR_ITENS");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Limpa
	 */
	public void limpar() {
		nroPac = null;
		modalidade = null;
		descricao = null;
		dataGeracao = null;
		comprador = null;
		licitacao = null;
		lotesSolicitacao=null;
		itemSelecionado = new ScoLoteLicitacao();
		pesquisou = Boolean.FALSE;
	}
	
		// Getters/Setters
	public Integer getNroPac() {
		return nroPac;
	}

	public void setNroPac(Integer nroPac) {
		this.nroPac = nroPac;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getComprador() {
		return comprador;
	}

	public void setComprador(String comprador) {
		this.comprador = comprador;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public List<ScoLoteLicitacao> getLotesSolicitacao() {
		return lotesSolicitacao;
	}

	public void setLotesSolicitacao(List<ScoLoteLicitacao> lotesSolicitacao) {
		this.lotesSolicitacao = lotesSolicitacao;
	}

	public Short getNumeroLote() {
		return numeroLote;
	}

	public void setNumeroLote(Short numeroLote) {
		this.numeroLote = numeroLote;
	}

	public String getDescricaoLote() {
		return descricaoLote;
	}

	public void setDescricaoLote(String descricaoLote) {
		this.descricaoLote = descricaoLote;
	}

	public Boolean getModoVisualizacaoLote() {
		return modoVisualizacaoLote;
	}

	public void setModoVisualizacaoLote(Boolean modoVisualizacaoLote) {
		this.modoVisualizacaoLote = modoVisualizacaoLote;
	}

	public Boolean getModoEdicaoLote() {
		return modoEdicaoLote;
	}

	public void setModoEdicaoLote(Boolean modoEdicaoLote) {
		this.modoEdicaoLote = modoEdicaoLote;
	}

	public ScoLoteLicitacao getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ScoLoteLicitacao itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ScoLoteLicitacaoId getIdLoteDelecao() {
		return idLoteDelecao;
	}

	public void setIdLoteDelecao(ScoLoteLicitacaoId idLoteDelecao) {
		this.idLoteDelecao = idLoteDelecao;
	}

	public List<ItensPACVO> getItensLicitacao() {
		return itensLicitacao;
	}

	public void setItensLicitacao(List<ItensPACVO> itensLicitacao) {
		this.itensLicitacao = itensLicitacao;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public Boolean getParecer() {
		return parecer;
	}

	public void setParecer(Boolean parecer) {
		this.parecer = parecer;
	}

	public Boolean getSemParecer() {
		return semParecer;
	}

	public void setSemParecer(Boolean semParecer) {
		this.semParecer = semParecer;
	}

	public Boolean getValidouLote() {
		return validouLote;
	}

	public void setValidouLote(Boolean validouLote) {
		this.validouLote = validouLote;
	}

	public ScoLoteLicitacao getLoteSelecionado() {
		return loteSelecionado;
	}

	public void setLoteSelecionado(ScoLoteLicitacao loteSelecionado) {
		this.loteSelecionado = loteSelecionado;
	}
	

	public Integer getLctNumeroLoteDelecao() {
		return lctNumeroLoteDelecao;
	}

	public void setLctNumeroLoteDelecao(Integer lctNumeroLoteDelecao) {
		this.lctNumeroLoteDelecao = lctNumeroLoteDelecao;
	}

	public Short getNumeroLoteDelecao() {
		return numeroLoteDelecao;
	}

	public void setNumeroLoteDelecao(Short numeroLoteDelecao) {
		this.numeroLoteDelecao = numeroLoteDelecao;
	}

	public Boolean getNaoPossuiItensAssociados() {
		return naoPossuiItensAssociados;
	}

	public void setNaoPossuiItensAssociados(Boolean naoPossuiItensAssociados) {
		this.naoPossuiItensAssociados = naoPossuiItensAssociados;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}	
}
