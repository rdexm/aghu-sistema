package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.action.PesquisaCidController;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtGravidezAnterior;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.SumarioAltaDiagnosticosCidVOComparator;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterSumarioAltaInformacoesObitoController  extends ActionController{
	
	private static final long serialVersionUID = -9178594260393604792L;

	/* Referências para Ambos Sliders */
	private String voltarPara; // Retorno
 	private Integer cidSeqPesquisadoInformacaoObito; // Cid recebido por parâmetro
 	
 	private Integer currentSliderInformacoesObito; // Slider corrente da aba "Informações de Óbito"
 	
 	private MpmAltaSumario altaSumario; // Sumário de Alta
	
 	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
 	
 	@EJB
	protected ICentroCustoFacade centroCustoFacade;
 	
 	@EJB
	private IAghuFacade aghuFacade;
 	
	/* Referências do Slider 0: Causa direta da morte */
	private MpmObtCausaDireta causaDireta; 
	private SumarioAltaDiagnosticosVO cidsCausaDiretaVO;
	private String complementoEditado;
	
	
	
	//TODO:
//	@Out(required=false)
//	@In(required=false)
	private Boolean fromPageObitoCausasDiretas = false;
	
	/* Referências do Slider 1: Causas antecedentes que produziram a causa da morte */
	private SumarioAltaDiagnosticosVO cidsAntecedentesVO;
	
	//TODO:
//	@Out(required=false)
//	@In(required=false)
	private Boolean fromPageObitoCausaAntecedentes = false;
	
	/* Referências do Slider 2: Outras condições patológicas/Outras Causas que contribuíram para a morte */
	private MpmObtOutraCausa outraCausa; 
	private SumarioAltaDiagnosticosVO cidsOutrasCausasVO;
	
	//TODO:
//	@Out(required=false)
//	@In(required=false)
	private Boolean fromPageObitoOutrasCausas = false;
	
	/* Referências do Slider 3: Informações Complementares */
	private DominioSimNao solicitadaRealizacaoNecropsia;
	private DominioSimNao esteveGravidaUltimos12Meses;

	private Integer ultimoIndice;
	/*
	 * Métodos para Ambos Sliders
	 */
	
	private Integer pacCodigo;
	
	private final String PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO = "pesquisaCidCapituloDiagnostico";
	
	@Inject
	private PesquisaCidController pesquisaCidController;
	
	
	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;
	
	private SumarioAltaDiagnosticosCidVO itemCausaDiretaSelecionado;
	private SumarioAltaDiagnosticosCidVO itemAntecedentesSelecionado;
	private SumarioAltaDiagnosticosCidVO itemOutrasSelecionado;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	/**
	 * @param altaSumario
	 */
	public void renderInformacoesObito(MpmAltaSumario altaSumario, Integer pacCodigo) throws BaseException {
		this.altaSumario = altaSumario;
		this.setPacCodigo(pacCodigo);
		
		if (this.pesquisaCidRetorno != null ) {
			PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
			if (pesquisaCidVO.getCid() !=null) {
			    this.cidSeqPesquisadoInformacaoObito = pesquisaCidVO.getCid().getSeq();
			}					
		}
		
		if (this.cidSeqPesquisadoInformacaoObito == null ||
			this.currentSliderInformacoesObito == null){
			this.currentSliderInformacoesObito = 0;
		}
				
		this.inicializarInformacoesObito(altaSumario.getId());		
		this.restaurarDadosInformacoesObitos();
		this.prepararInformacoesComplementares();
	}
	

	/**
	 * Inicializa componentes do controller
	 * @param id
	 */
	private void inicializarInformacoesObito(MpmAltaSumarioId id) throws BaseException{
		this.prepararListasCausaDireta(id);
		this.prepararListasCausaAntecedente(id);
		this.prepararListasOutrasCausas(id);
	}
	
	/**
	 * Pesquisa Geral por CIDs
	 * @param param
	 * @return
	 */
	public List<AghCid> pesquisarCids(String param) {
		return aghuFacade.obterCidPorNomeCodigoAtivaPaginado(param);
	}
	
	/*
	 * Métodos da pesuisa por capítulo para ambos Sliders
	 */
	
	private void restaurarDadosInformacoesObitos() {
		try {
			if (this.cidSeqPesquisadoInformacaoObito != null) {
				AghCid cidPorCapitulo = this.aghuFacade.obterCid(this.cidSeqPesquisadoInformacaoObito);
				SumarioAltaDiagnosticosCidVO item = null;
				String retorno = "";
				switch (this.currentSliderInformacoesObito) {
				case 0:
					this.cidsCausaDiretaVO.setId(altaSumario.getId());
					item = this.cidsCausaDiretaVO.novoItem();
					item.setCid(cidPorCapitulo);
					retorno = this.prescricaoMedicaFacade.inserirObtCausaDireta(item);
					apresentarMsgNegocio(Severity.INFO, retorno);
					cidSeqPesquisadoInformacaoObito=null;
					this.pesquisaCidRetorno = null;
					break;
				case 1:
					this.cidsAntecedentesVO.setId(altaSumario.getId());
					item = this.cidsAntecedentesVO.novoItem();
					item.setCid(cidPorCapitulo);
					retorno = this.prescricaoMedicaFacade.persistirCausaAntecedente(item);
					apresentarMsgNegocio(Severity.INFO, retorno);
					cidSeqPesquisadoInformacaoObito=null;
					this.pesquisaCidRetorno = null;
					break;
				case 2:
					this.cidsOutrasCausasVO.setId(altaSumario.getId());
					item = this.cidsOutrasCausasVO.novoItem();
					item.setCid(cidPorCapitulo);
					retorno = this.prescricaoMedicaFacade.persistirOutraCausa(item);
					apresentarMsgNegocio(Severity.INFO, retorno);
					cidSeqPesquisadoInformacaoObito=null;
					this.pesquisaCidRetorno = null;
					break;
				}
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Chamada do botão de pesquisa por capítulo das Causas Diretas de Morte
	 * @return
	 */
	public String pesquisarCidCapituloCausasDiretas() {
		this.fromPageObitoCausasDiretas=true;
		this.currentSliderInformacoesObito = 0;
		this.pesquisaCidController.setFromPageObitoCausasDiretas(true);
		this.pesquisaCidController.setFromPageManterSumarioObito(true);
		return PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO;
	}
	
	/**
	 * Chamada do botão de pesquisa por capítulo das Causas Atecedentes de Morte
	 * @return
	 */
	public String pesquisarCidCapituloCausaAntecendentes() {
		this.fromPageObitoCausaAntecedentes = true;
		this.currentSliderInformacoesObito = 1;
		this.pesquisaCidController.setFromPageObitoCausaAntecedentes(true);
		this.pesquisaCidController.setFromPageManterSumarioObito(true);
		return PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO;
	}	
	
	/**
	 * Chamada do botão de pesquisa por capítulo das Outras Causas de Morte (Outras Condições Patológicas) 
	 * @return
	 */
	public String pesquisarCidCapituloOutrasCausas() {
		this.fromPageObitoOutrasCausas=true;
		this.currentSliderInformacoesObito = 2;
		this.pesquisaCidController.setFromPageObitoOutrasCausas(true);
		this.pesquisaCidController.setFromPageManterSumarioObito(true);
		return PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO;
	}
	
	/*
	 * Métodos do slider 0: Causa direta da morte
	 */
	
	/**
	 * Prepara a Sugestion Box contendo CIDS para Causa Direta da Morte
	 * @param id
	 */
	private void prepararListasCausaDireta(MpmAltaSumarioId id) throws BaseException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		// Carrega combos de cada slider e remover itens duplicados da grid
		MpmObtCausaDireta causaDireta = null;
		causaDireta = this.prescricaoMedicaFacade.obterObtCausaDireta(apaAtdSeq, apaSeq, seqp);
		

		List<SumarioAltaDiagnosticosCidVO> listaCombo = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaCombo = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		if (causaDireta != null) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSACausaDiretaCidVO(id,causaDireta);
			listaGrid.add(itemVO);
			listaCombo.removeAll(listaGrid);
		}
		
		this.cidsCausaDiretaVO = new SumarioAltaDiagnosticosVO(id, 1);
		this.cidsCausaDiretaVO.setListaCombo(listaCombo);
		this.cidsCausaDiretaVO.setListaGrid(listaGrid);
	}
	
	/**
	 * Cria um VO de Causa direta da morte
	 * @param id
	 * @param causaDireta
	 * @return
	 */
	private SumarioAltaDiagnosticosCidVO criarSACausaDiretaCidVO(MpmAltaSumarioId id, MpmObtCausaDireta causaDireta) {
		Short seqp = causaDireta.getId().getAsuSeqp();
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, seqp);
		
		if (causaDireta.getMpmCidAtendimentos() != null) {
			itemVO.setCiaSeq(causaDireta.getMpmCidAtendimentos().getSeq());
		}
		
		AghCid cid = this.aghuFacade.obterCid(causaDireta.getCid().getCodigo());
		itemVO.setCid(cid);
		itemVO.setComplementoEditado(causaDireta.getComplCid());
		itemVO.setObitoCausaDireta(causaDireta);
		
		if (causaDireta.getDiagnostico() != null) {
			itemVO.setDiaSeq(causaDireta.getDiagnostico().getSeq());
		}
		
		return itemVO;
	}
	
	/**
	 * Move um item selecionado na Sugestion Box de Causa Direta da Morte para o Grid de Causa Direta da Morte
	 * Insere a Causa direta da Morte no Sumário de Alta
	 */
	public void moverCausaDiretaItemSelecionadoComboParaGrid() {
		try {
			String retorno = this.prescricaoMedicaFacade.inserirObtCausaDireta(itemCausaDiretaSelecionado);
			apresentarMsgNegocio(Severity.INFO, retorno);
			this.prepararListasCausaDireta(this.cidsCausaDiretaVO.getId());
			this.setItemCausaDiretaSelecionado(null);
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Grava item selecionado na Sugestion Box de CID para o Grid de Causa Direta da Morte
	 * Insere a Causa direta da Morte no Sumário de Alta
	 */
	public void gravarCausaDiretaItemEmEdicao() {
		try {
			this.cidsCausaDiretaVO.validarItemEmEdicao();
			final String retorno = this.prescricaoMedicaFacade.inserirObtCausaDireta(this.cidsCausaDiretaVO.getItemEmEdicao());
			apresentarMsgNegocio(Severity.INFO, retorno);
		    this.prepararListasCausaDireta(this.cidsCausaDiretaVO.getId());
		    this.setItemCausaDiretaSelecionado(null);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Atualiza um item selecionado no Grid de Causa Direta da Morte
	 * Atualiza a Causa direta da Morte do Sumário de Alta
	 */
	public void atualizarCausaDiretaItemEmEdicao() {
		String complementoEditado = null;
		SumarioAltaDiagnosticosCidVO itemVO = null;
		try{
			itemVO = this.cidsCausaDiretaVO.getItemEmEdicao();
			complementoEditado = itemVO.getComplemento();
			this.cidsCausaDiretaVO.validarItemEmEdicao();
			this.prescricaoMedicaFacade.atualizarObtCausaDireta(this.cidsCausaDiretaVO.getItemEmEdicao());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_CAUSA_DIRETA_MORTE");
			this.prepararListasCausaDireta(this.cidsCausaDiretaVO.getId());
			this.setItemCausaDiretaSelecionado(null);
			this.cidsCausaDiretaVO.setComplementoCidSuggestion(null);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			if (itemVO != null) {
				itemVO.setComplementoEditado(complementoEditado);
			}
		}	
	}
	
	/**
	 * Remove um item selecionado no Grid de Causa Direta da Morte
	 * Remove a Causa direta da Morte do Sumário de Alta
	 * @param itemGrid
	 */
	public void excluirCausaDiretaItemGrid() {
		try{
			if(itemCausaDiretaSelecionado != null){
				this.prescricaoMedicaFacade.removerObtCausaDireta(itemCausaDiretaSelecionado);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_CAUSA_DIRETA_MORTE");
				this.prepararListasCausaDireta(this.cidsCausaDiretaVO.getId());
				this.itemCausaDiretaSelecionado = null;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void editarCausaDiretaItemGrid(){
		for (SumarioAltaDiagnosticosCidVO item : this.cidsCausaDiretaVO.getListaGrid()) {
			item.setEmEdicao(Boolean.FALSE);
		}
		getItemCausaDiretaSelecionado().setEmEdicao(Boolean.TRUE);
		this.cidsCausaDiretaVO.setCidSuggestion(getItemCausaDiretaSelecionado().getCid());
		this.cidsCausaDiretaVO.setComplementoCidSuggestion(getItemCausaDiretaSelecionado().getComplementoEditado());
		this.cidsCausaDiretaVO.setItemEmEdicao(getItemCausaDiretaSelecionado());		
		
	}
	
	public void cancelarCausaDiretaItemGrid(){
		this.getItemCausaDiretaSelecionado().setEmEdicao(Boolean.FALSE);
		this.cidsCausaDiretaVO.setCidSuggestion(null);
		this.cidsCausaDiretaVO.novoItem();
		this.setItemCausaDiretaSelecionado(null);
		this.cidsCausaDiretaVO.setComplementoCidSuggestion(null);
	}
	
	/*
	 * Métodos do slider 1: Causas antecedentes que produziram a causa da morte
	 */

	/**
	 * Prepara a Sugestion Box contendo CIDS para Causas Antecedentes da Morte
	 * @param id
	 */
	private void prepararListasCausaAntecedente(MpmAltaSumarioId id) throws BaseException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		// Carrega combos de cada slider e remover itens duplicados da grid
		List<MpmObtCausaAntecedente> listaObitoCausaAntecedentes = null;
		listaObitoCausaAntecedentes = this.prescricaoMedicaFacade.obterMpmObtCausaAntecedente(apaAtdSeq, apaSeq, seqp);

		List<SumarioAltaDiagnosticosCidVO> listaCombo = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaCombo = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		for(MpmObtCausaAntecedente diagMvtoInternacao: listaObitoCausaAntecedentes) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSACausaAntecedentesCidVO(id, diagMvtoInternacao);
			listaGrid.add(itemVO);
		}

		listaCombo.removeAll(listaGrid);
		Collections.sort(listaGrid, new SumarioAltaDiagnosticosCidVOComparator(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 7926551893774913930L;

			@Override
			public int compare(SumarioAltaDiagnosticosCidVO o1,	SumarioAltaDiagnosticosCidVO o2) {
				return o1.getPrioridade().compareTo(o2.getPrioridade());
			}
		});
		this.ultimoIndice = listaGrid.size() - 1;
		this.cidsAntecedentesVO = new SumarioAltaDiagnosticosVO(id, null);
		this.cidsAntecedentesVO.setListaCombo(listaCombo);
		this.cidsAntecedentesVO.setListaGrid(listaGrid);
		
	}	
	
	/**
	 * Move um item selecionado na Sugestion Box de Causas Antecedentes de Morte para o Grid de Causas Antecedentes da Morte
	 * Insere a Causa Antecedente de Morte no Sumário de Alta
	 */
	public void moverCausaAntecedenteSelecionadoComboParaGrid() {
		try {
				final String retorno = this.prescricaoMedicaFacade.persistirCausaAntecedente(itemAntecedentesSelecionado);
				apresentarMsgNegocio(Severity.INFO,retorno);
				this.prepararListasCausaAntecedente(this.cidsAntecedentesVO.getId());
				this.setItemAntecedentesSelecionado(null);
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void ordenarListaItensReceitaCima(SumarioAltaDiagnosticosCidVO item,
			Integer indiceTabela) throws BaseException {
		if(!this.ordenarListaItensReceita(item, true, indiceTabela)){
			apresentarMsgNegocio(Severity.INFO,
			"MENSAGEM_ERRO_ORDENACAO_EM_EDICAO");
		}
		atualizarNovaPrioridade();
	}

	public void ordenarListaItensReceitaBaixo(SumarioAltaDiagnosticosCidVO item,
			Integer indiceTabela) throws BaseException {
		if(!this.ordenarListaItensReceita(item, false, indiceTabela)){
			apresentarMsgNegocio(Severity.INFO,
			"MENSAGEM_ERRO_ORDENACAO_EM_EDICAO");
		}
		atualizarNovaPrioridade();
	}

	private void atualizarNovaPrioridade() throws BaseException {
		String retorno = "";
		for(SumarioAltaDiagnosticosCidVO causaAntecedente : this.cidsAntecedentesVO.getListaGrid()){
			if(causaAntecedente.getObitoCausaAntecendente().getIndCarga().equals(DominioSimNao.S)){
				causaAntecedente.getObitoCausaAntecendente().setIndCarga(DominioSimNao.N);
			}
			retorno = this.prescricaoMedicaFacade.persistirCausaAntecedente(causaAntecedente);
			
		}
		if(!retorno.equals("")) {
			apresentarMsgNegocio(Severity.INFO,retorno);
		}
	}
	
	/**
	 * 
	 * @param item
	 *            Item(VO) da receita
	 * @param sobe
	 *            Se for true o item deve subir uma posicao na lista
	 * @param indice
	 *            Recebe o indice da linha do item na tabela
	 */
	private boolean ordenarListaItensReceita(SumarioAltaDiagnosticosCidVO item, boolean sobe,
			int indice) {
				
			
			// Obtem a ordem de apresentacao do item
			Short ordem = item.getPrioridade();
	
			SumarioAltaDiagnosticosCidVO aux = null;
			this.ultimoIndice = this.cidsAntecedentesVO.getListaGrid().size() - 1;
	
			if (sobe) {
				// Se for o primeiro da lista nao deve subir o item na lista
				if (indice != 0) {
	
					aux = this.cidsAntecedentesVO.getListaGrid().get(indice - 1);
	
					item.setPrioridade(Short.valueOf((short) (ordem - 1)));
					//item.setEdicao(true);
					this.cidsAntecedentesVO.getListaGrid().set(indice - 1, item);
	
					aux.setPrioridade(ordem);
					//aux.getItemReceituario().setOrdem(ordem);
					//aux.setEdicao(true);
					this.cidsAntecedentesVO.getListaGrid().set(indice, aux);
				}
	
			} else {
				// Se for o ultimo da lista nao deve descer o elemento na lista
				if (indice != this.cidsAntecedentesVO.getListaGrid().size() - 1) {
	
					aux = this.cidsAntecedentesVO.getListaGrid().get(indice + 1);
	
					item.setPrioridade(Short.valueOf((short) (ordem + 1)));
					//item.getItemReceituario().setOrdem((byte) (ordem + 1));
					//item.setEdicao(true);
					this.cidsAntecedentesVO.getListaGrid().set(indice + 1, item);
	
					aux.setPrioridade(ordem);
					//aux.getItemReceituario().setOrdem(ordem);
					//aux.setEdicao(true);
					this.cidsAntecedentesVO.getListaGrid().set(indice, aux);
				}
			
			}
	
			return true;
			
	}
	
	/**
	 * Grava item selecionado na Sugestion Box de CID para o Grid de Causas Antecedentes da Morte
	 * Insere a Causa Antecedente da Morte no Sumário de Alta
	 */
	public void gravarCausaAntecedenteEmEdicao(){
		try {
			this.cidsAntecedentesVO.getItemEmEdicao().setId(altaSumario.getId());
			this.cidsAntecedentesVO.getItemEmEdicao().setCid(this.cidsAntecedentesVO.getCidSuggestion()); 
			this.cidsAntecedentesVO.getItemEmEdicao().setComplementoEditado(this.cidsAntecedentesVO.getComplementoCidSuggestion()); 
			this.cidsAntecedentesVO.validarItemEmEdicao();
			final String retorno = this.prescricaoMedicaFacade.persistirCausaAntecedente(this.cidsAntecedentesVO.getItemEmEdicao());
			apresentarMsgNegocio(Severity.INFO,retorno);
			this.prepararListasCausaAntecedente(this.cidsAntecedentesVO.getId());
			this.setItemAntecedentesSelecionado(null);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Remove um item selecionado no Grid de Causa Antecedente da Morte
	 * Remove a Causa Antecedente da Morte do Sumário de Alta
	 * @param itemGrid
	 */
	public void excluirCausaAntecedenteItemGrid() {
		try{
			if(itemAntecedentesSelecionado != null){
				this.prescricaoMedicaFacade.removerObtCausaAntecedente(itemAntecedentesSelecionado);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_CAUSA_ANTECEDENTE_MORTE");
				this.prepararListasCausaAntecedente(this.cidsAntecedentesVO.getId());
				itemAntecedentesSelecionado = null;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void editarCausaAntecedenteItemGrid(){
		for (SumarioAltaDiagnosticosCidVO item : this.cidsAntecedentesVO.getListaGrid()) {
			item.setEmEdicao(Boolean.FALSE);
		}
		getItemAntecedentesSelecionado().setEmEdicao(Boolean.TRUE);
		this.cidsAntecedentesVO.setCidSuggestion(getItemAntecedentesSelecionado().getCid());
		this.cidsAntecedentesVO.setComplementoCidSuggestion(getItemAntecedentesSelecionado().getComplementoEditado());
		this.cidsAntecedentesVO.setItemEmEdicao(getItemAntecedentesSelecionado());		
		
	}

	public void cancelarCausaAntecedenteItemGrid(){
		this.getItemAntecedentesSelecionado().setEmEdicao(Boolean.FALSE);
		this.cidsAntecedentesVO.setCidSuggestion(null);
		this.cidsAntecedentesVO.novoItem();
		this.setItemAntecedentesSelecionado(null);
		this.cidsAntecedentesVO.setComplementoCidSuggestion(null);
	}

	/**
	 * Cria um VO de Causa Antecedente da Morte
	 * @param id
	 * @param obitoCausaAntecendente
	 * @return
	 */
	private SumarioAltaDiagnosticosCidVO criarSACausaAntecedentesCidVO(MpmAltaSumarioId id, MpmObtCausaAntecedente obitoCausaAntecendente) {
		Short seqp = obitoCausaAntecendente.getId().getSeqp().shortValue();
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, seqp);

		if (obitoCausaAntecendente.getMpmCidAtendimentos() != null) {
			itemVO.setCiaSeq(obitoCausaAntecendente.getMpmCidAtendimentos().getSeq());
		}

		AghCid cid = this.aghuFacade.obterCid(obitoCausaAntecendente.getCid().getCodigo());
		itemVO.setCid(cid);		
		itemVO.setComplementoEditado(obitoCausaAntecendente.getComplCid());
		itemVO.setObitoCausaAntecendente(obitoCausaAntecendente);
		itemVO.setPrioridade(obitoCausaAntecendente.getPrioridade());

		if (obitoCausaAntecendente.getDiagnostico() != null) {
			itemVO.setDiaSeq(obitoCausaAntecendente.getDiagnostico().getSeq());
		}

		return itemVO;
	}
	
	/*
	 * Métodos do slider 2: Outras condições patológicas (Outras Causas) que contribuíram para a morte
	 */
	
	/**
	 * Prepara a Sugestion Box contendo CIDS para Outras Causas de Morte
	 * @param id
	 */
	private void prepararListasOutrasCausas(MpmAltaSumarioId id) throws BaseException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		// Carrega combos de cada slider e remover itens duplicados da grid
		List<MpmObtOutraCausa> listaOutrasCausas = null;
		listaOutrasCausas = this.prescricaoMedicaFacade.obterMpmObtOutraCausa(apaAtdSeq, apaSeq, seqp);

		List<SumarioAltaDiagnosticosCidVO> listaCombo = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaCombo = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		for(MpmObtOutraCausa diagMvtoInternacao: listaOutrasCausas) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSAOutraCausaCidVO(id, diagMvtoInternacao);
			listaGrid.add(itemVO);
		}

		listaCombo.removeAll(listaGrid);
		Collections.sort(listaGrid, new SumarioAltaDiagnosticosCidVOComparator());

		this.cidsOutrasCausasVO = new SumarioAltaDiagnosticosVO(id, null);
		this.cidsOutrasCausasVO.setListaCombo(listaCombo);
		this.cidsOutrasCausasVO.setListaGrid(listaGrid);
		
	}
	
	private SumarioAltaDiagnosticosCidVO criarSAOutraCausaCidVO(MpmAltaSumarioId id, MpmObtOutraCausa obtOutraCausa) {
		Short seqp = obtOutraCausa.getId().getSeqp().shortValue();
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, seqp);

		if (obtOutraCausa.getMpmCidAtendimentos() != null) {
			itemVO.setCiaSeq(obtOutraCausa.getMpmCidAtendimentos().getSeq());
		}
		
		AghCid cid = this.aghuFacade.obterCid(obtOutraCausa.getCid().getCodigo());
		itemVO.setCid(cid);	
		itemVO.setComplementoEditado(obtOutraCausa.getComplCid());
		itemVO.setObtOutraCausa(obtOutraCausa);

		if (obtOutraCausa.getDiagnostico() != null) {
			itemVO.setDiaSeq(obtOutraCausa.getDiagnostico().getSeq());
		}

		return itemVO;
	}
	
	
	/**
	 * Move um item selecionado na Sugestion Box de Outras Causas de Morte (Outras Condições Patológicas) para o Grid de Outras Causas de Morte (Outras Condições Patológicas)
	 * Insere a Outra Causa de Morte (Outra Condição Patológica) no Sumário de Alta
	 */
	public void moverOutraCausaItemSelecionadoComboParaGrid() {
		try {
				final String retorno = this.prescricaoMedicaFacade.persistirOutraCausa(itemOutrasSelecionado);
				apresentarMsgNegocio(Severity.INFO,retorno);
				this.prepararListasOutrasCausas(this.cidsOutrasCausasVO.getId());
				this.setItemOutrasSelecionado(null);
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Grava item selecionado na Sugestion Box de CID para o Grid de Outras Causas de Morte (Outras Condições Patológicas)
	 * Insere a Outra Causa de Morte (Outra Condição Patológica) no Sumário de Alta
	 */
	public void gravarOutraCausaEmEdicao(){
		try {
			this.cidsOutrasCausasVO.getItemEmEdicao().setId(altaSumario.getId());
			this.cidsOutrasCausasVO.getItemEmEdicao().setCid(this.cidsOutrasCausasVO.getCidSuggestion()); 
			this.cidsOutrasCausasVO.getItemEmEdicao().setComplementoEditado(this.cidsOutrasCausasVO.getComplementoCidSuggestion()); 
			this.cidsOutrasCausasVO.validarItemEmEdicao();
			final String retorno = this.prescricaoMedicaFacade.persistirOutraCausa(this.cidsOutrasCausasVO.getItemEmEdicao());
			apresentarMsgNegocio(Severity.INFO,retorno);
			this.prepararListasOutrasCausas(this.cidsOutrasCausasVO.getId());
			this.setItemOutrasSelecionado(null);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Remove um item selecionado no Grid de Outras Causas de Morte (Outras Condições Patológicas)
	 * Remove a Outra Causa de Morte (Outra Condição Patológica) do Sumário de Alta
	 * @param itemGrid
	 */
	public void excluirOutraCausaItemGrid() {
		try{
			if(itemOutrasSelecionado != null){
				this.prescricaoMedicaFacade.removerObtOutraCausa(itemOutrasSelecionado);
				this.prepararListasOutrasCausas(this.cidsOutrasCausasVO.getId());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_OUTRA_CAUSA_MORTE");
				itemOutrasSelecionado = null;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void editarOutraCausaItemGrid(){
		for (SumarioAltaDiagnosticosCidVO item : this.cidsOutrasCausasVO.getListaGrid()) {
			item.setEmEdicao(Boolean.FALSE);
		}
		getItemOutrasSelecionado().setEmEdicao(Boolean.TRUE);
		this.cidsOutrasCausasVO.setCidSuggestion(getItemOutrasSelecionado().getCid());
		this.cidsOutrasCausasVO.setComplementoCidSuggestion(getItemOutrasSelecionado().getComplementoEditado());
		this.cidsOutrasCausasVO.setItemEmEdicao(getItemOutrasSelecionado());		
		
	}

	public void cancelarOutraCausaItemGrid(){
		this.getItemOutrasSelecionado().setEmEdicao(Boolean.FALSE);
		this.cidsOutrasCausasVO.setCidSuggestion(null);
		this.cidsOutrasCausasVO.novoItem();
		this.setItemOutrasSelecionado(null);
		this.cidsOutrasCausasVO.setComplementoCidSuggestion(null);
	}

	/*
	 * Métodos do slider 3: Informações complementares
	 */
	
	/**
	 * Configura as Combos da informação complementar
	 */
	private void prepararInformacoesComplementares() throws BaseException {
		
		// Popula Combo Box do tipo SimNao com as informações da solicitação/realização de necrópsia
		MpmObitoNecropsia obitoNecropsia = this.prescricaoMedicaFacade.obterMpmObitoNecropsia(this.altaSumario);
		this.solicitadaRealizacaoNecropsia = obitoNecropsia!=null ? obitoNecropsia.getNecropsia(): null;
		
		// Popula Combo Box do tipo SimNao com as informações da gravidez anterior nos últimos 12 meses
		MpmObtGravidezAnterior obtGravidezAnterior = this.prescricaoMedicaFacade.obterMpmObtGravidezAnterior(this.altaSumario);
	    this.esteveGravidaUltimos12Meses = obtGravidezAnterior !=null ? obtGravidezAnterior.getGravidezAnterior() : null;
	}
	
	/**
	 * Grava e atualiza as informações contidas nas Combos da informação complementar
	 */
	public void gravarInformacoesComplementares(){
		try {
			this.prescricaoMedicaFacade.gravarObitoNecropsia(this.altaSumario,solicitadaRealizacaoNecropsia);
			this.prescricaoMedicaFacade.gravarObtGravidezAnterior(this.altaSumario,esteveGravidaUltimos12Meses);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_INFORMACOES_COMPLEMENTARES");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

//	public void onTabChangeInformacoesObito(TabChangeEvent event) {
//		
//		if(event != null && event.getTab() != null) {
//			if(TABINFOBITO_0.equals(event.getTab().getId())) {
//				this.currentSliderInformacoesObito = 0;
//			} else if(TABINFOBITO_1.equals(event.getTab().getId())) {
//				this.currentSliderInformacoesObito = 1;
//			} else if(TABINFOBITO_2.equals(event.getTab().getId())) {
//				this.currentSliderInformacoesObito = 2;
//			} else if(TABINFOBITO_3.equals(event.getTab().getId())) {
//				this.currentSliderInformacoesObito = 3;
//			}
//		}
//		
//	}	
	/*
	 *  Getters and Setters...
	 */

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getCidSeqPesquisadoInformacaoObito() {
		return cidSeqPesquisadoInformacaoObito;
	}

	public void setCidSeqPesquisadoInformacaoObito(Integer cidSeqPesquisadoInformacaoObito) {
		this.cidSeqPesquisadoInformacaoObito = cidSeqPesquisadoInformacaoObito;
	}

	public Integer getCurrentSliderInformacoesObito() {
		return currentSliderInformacoesObito;
	}

	public void setCurrentSliderInformacoesObito(
			Integer currentSliderInformacoesObito) {
		this.currentSliderInformacoesObito = currentSliderInformacoesObito;
	}

	public MpmObtCausaDireta getCausaDireta() {
		return causaDireta;
	}

	public void setCausaDireta(MpmObtCausaDireta causaDireta) {
		this.causaDireta = causaDireta;
	}

	public SumarioAltaDiagnosticosVO getCidsCausaDiretaVO() {
		return cidsCausaDiretaVO;
	}

	public void setCidsCausaDiretaVO(SumarioAltaDiagnosticosVO cidsCausaDiretaVO) {
		this.cidsCausaDiretaVO = cidsCausaDiretaVO;
	}

	public String getComplementoEditado() {
		return complementoEditado;
	}

	public void setComplementoEditado(String complementoEditado) {
		this.complementoEditado = complementoEditado;
	}

	public SumarioAltaDiagnosticosVO getCidsAntecedentesVO() {
		return cidsAntecedentesVO;
	}

	public void setCidsAntecedentesVO(SumarioAltaDiagnosticosVO cidsAntecedentesVO) {
		this.cidsAntecedentesVO = cidsAntecedentesVO;
	}

	public DominioSimNao getSolicitadaRealizacaoNecropsia() {
		return solicitadaRealizacaoNecropsia;
	}

	public void setSolicitadaRealizacaoNecropsia(
			DominioSimNao solicitadaRealizacaoNecropsia) {
		this.solicitadaRealizacaoNecropsia = solicitadaRealizacaoNecropsia;
	}

	public DominioSimNao getEsteveGravidaUltimos12Meses() {
		return esteveGravidaUltimos12Meses;
	}

	public void setEsteveGravidaUltimos12Meses(
			DominioSimNao esteveGravidaUltimos12Meses) {
		this.esteveGravidaUltimos12Meses = esteveGravidaUltimos12Meses;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
	
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}
	
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public boolean isFromPageObitoCausaAntecedentes() {
		return fromPageObitoCausaAntecedentes;
	}

	public void setFromPageObitoCausaAntecedentes(
			boolean fromPageObitoCausaAntecedentes) {
		this.fromPageObitoCausaAntecedentes = fromPageObitoCausaAntecedentes;
	}

	public void setOutraCausa(MpmObtOutraCausa outraCausa) {
		this.outraCausa = outraCausa;
	}

	public MpmObtOutraCausa getOutraCausa() {
		return outraCausa;
	}
	
	public SumarioAltaDiagnosticosVO getCidsOutrasCausasVO() {
		return cidsOutrasCausasVO;
	}
	
	public void setCidsOutrasCausasVO(
			SumarioAltaDiagnosticosVO cidsOutrasCausasVO) {
		this.cidsOutrasCausasVO = cidsOutrasCausasVO;
	}

	public void setFromPageObitoCausasDiretas(Boolean fromPageObitoCausasDiretas) {
		this.fromPageObitoCausasDiretas = fromPageObitoCausasDiretas;
	}

	public Boolean getFromPageObitoCausasDiretas() {
		return fromPageObitoCausasDiretas;
	}

	public void setFromPageObitoOutrasCausas(Boolean fromPageObitoOutrasCausas) {
		this.fromPageObitoOutrasCausas = fromPageObitoOutrasCausas;
	}

	public Boolean getFromPageObitoOutrasCausas() {
		return fromPageObitoOutrasCausas;
	}


	public Integer getUltimoIndice() {
		return ultimoIndice;
	}

	public void setUltimoIndice(Integer ultimoIndice) {
		this.ultimoIndice = ultimoIndice;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Instance<PesquisaCidVO> getPesquisaCidRetorno() {
		return pesquisaCidRetorno;
	}
	public void setPesquisaCidRetorno(Instance<PesquisaCidVO> pesquisaCidRetorno) {
		this.pesquisaCidRetorno = pesquisaCidRetorno;
	}
	public SumarioAltaDiagnosticosCidVO getItemCausaDiretaSelecionado() {
		return itemCausaDiretaSelecionado;
	}
	public void setItemCausaDiretaSelecionado(
			SumarioAltaDiagnosticosCidVO itemCausaDiretaSelecionado) {
		this.itemCausaDiretaSelecionado = itemCausaDiretaSelecionado;
	}
	public SumarioAltaDiagnosticosCidVO getItemAntecedentesSelecionado() {
		return itemAntecedentesSelecionado;
	}
	public void setItemAntecedentesSelecionado(
			SumarioAltaDiagnosticosCidVO itemAntecedentesSelecionado) {
		this.itemAntecedentesSelecionado = itemAntecedentesSelecionado;
	}
	public SumarioAltaDiagnosticosCidVO getItemOutrasSelecionado() {
		return itemOutrasSelecionado;
	}
	public void setItemOutrasSelecionado(
			SumarioAltaDiagnosticosCidVO itemOutrasSelecionado) {
		this.itemOutrasSelecionado = itemOutrasSelecionado;
	}

}
