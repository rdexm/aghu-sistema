package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioStatusNotificacaoTecnica;
import br.gov.mec.aghu.dominio.DominioStatusTicket;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.cadastroapoio.AnexosArquivosListPaginatorController;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.NomeUsuarioVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ItemRecebimentoController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4317044114744962555L;
	
	private static final String BARRA = " / ";

	private static final String TRACO = " - ";
	
	private static final String PATRIMONIO_RE_IMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE = "patrimonio-reImprimirProtocoloRetiradaBemPermanente";
	
	private static final String ITEM_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";
	
	private final String PAGINA_CADASTRAR_NOTIFICACOES_TECNICAS = "cadastroNotificacaoTecnica";
	
	private final String ANEXOS_ARQUIVOS_LIST = "patrimonio-anexosArquivosList";
		
	private final String PAGINA_DESIGNAR_TECNICO_RESPONSAVEL = "patrimonio-designarTecnicoResponsavel";
	
	private final String EM_AVALIACAO_TECNICA = "Em Avaliação Técnica";
	
	private final String PAGE_PESQUISAR_ACEITE_TECNICO = "patrimonio-registrarAceiteTecnicoList";
	
	private final String MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO = "MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO";
	
	private final String PAGINA_PATRIMONIO_ITEM_RECEBIMENTO = "patrimonio-itemRecebimento";
	
	private final String PAGINA_RETIRADA_BEM_PERMANENTE = "patrimonio-retiradaBemPermanente";
	
	private final String STATUS_NÃO_CONFORME = "Não Conforme";
	
	private final String STATUS_CONCLUIDA = "Concluída";
	
	private final String STATUS_PARCIALMENTE_ACEITO = "Parcialmente Aceito";
	
	private final String STATUS_ACEITO = "Aceito";
	
	private final String PAGINA_REGISTRAR_DEVOLUCAO_BEM_PERMANENTE = "patrimonio-registrarDevolucaoBemPermanente";
	

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ArquivosAnexosController arquivosAnexosController;

	@Inject
	private HistoricoEncaminhamentoController historicoEncaminhamentoController;

	@Inject
	private AnaliseTecnicoController analiseTecnicoController;
	
	@Inject
	private EncaminharSolicitacaoAnaliseTecnicaController encaminharSolicitacaoAnaliseTecnicaController;
	
	@Inject
	private AnexosArquivosListPaginatorController anexosArquivosListPaginatorController;
	
	@Inject
	private DesignaTecnicoResponsavelController designaTecnicoResponsavelController;
	
	@Inject
	private CadastroNotificacaoTecnicaController cadastroNotificacaoTecnicaController;
	
	@Inject
	private RegistrarAceiteTecnicoPaginatorController registrarAceiteTecnicoPaginatorController;
	
	@Inject
	private RelatorioNotifTecRecebimentoProvisorioController relatorioNotifTecRecebimentoProvisorioController;
	
	@Inject
	private RetiradaBemPermanenteController retiradaBemPermanenteController;
	
	@Inject
	private RegistrarDevolucaoBemPermanenteController registrarDevolucaoBemPermanenteController;

	@Inject
	private ReImprimirProtocoloRetiradaBemPermanenteController reImprimirProtocoloRetiradaBemPermanenteController;
	
	private ItemRecebimentoVO itemRecebimento;

	private NomeUsuarioVO nomeUsuarioVO;
	
	private RapServidores servidorLogado;
	
	private PtmNotificacaoTecnica itemSelecionado;
	
	private AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO;
	
	private Integer indice;
	
	@Inject	@Paginator
	private DynamicDataModel<PtmNotificacaoTecnica> dataModel;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		
		
		List<ItemRecebimentoVO> listaTudo =  patrimonioFacade.carregarItemRecebimento(aceiteTecnicoParaSerRealizadoVO.getRecebimento(), aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
		
		if (listaTudo != null && !listaTudo.isEmpty()){
			for (ItemRecebimentoVO itemRecebimentoVO : listaTudo) {				
				itemRecebimento = itemRecebimentoVO;
				itemRecebimento.setRecebItem(itemRecebimentoVO.getRecebimento().toString().concat(BARRA).concat(itemRecebimentoVO.getItemRecebimento().toString()));				
				itemRecebimento.setAfComplemento(itemRecebimentoVO.getaF().toString().concat(BARRA).concat(itemRecebimentoVO.getComplemento().toString()));
				if (itemRecebimentoVO.getCnpj() != null){
					itemRecebimento.setCnpjCpfRazaoSocial(itemRecebimentoVO.getCnpj().toString().concat(TRACO).concat(itemRecebimentoVO.getRazaoSocial()));
				}else{
					itemRecebimento.setCnpjCpfRazaoSocial(itemRecebimentoVO.getCpf().toString().concat(TRACO).concat(itemRecebimentoVO.getRazaoSocial()));
				}
				itemRecebimento.setCodigoMaterial(itemRecebimentoVO.getCodigo().toString().concat(BARRA).concat(itemRecebimentoVO.getMaterial().toString()));
				
				itemRecebimento.setStatusFormatada(DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(itemRecebimento.getStatus()));
			}
			nomeUsuarioVO = patrimonioFacade.obterNomeMatriculaUsuario(itemRecebimento.getIrpSeq());
			if (nomeUsuarioVO != null){
				nomeUsuarioVO.setResponsavelTecnico(nomeUsuarioVO.getMatricula().getId().getMatricula().toString().concat(TRACO).concat(nomeUsuarioVO.getMatricula().getId().getVinCodigo().toString()).concat(TRACO).concat(nomeUsuarioVO.getNome()));				
			}			
			
			historicoEncaminhamentoController.setItemRecebimento(itemRecebimento);	
			historicoEncaminhamentoController.setIndice(1);
			
			analiseTecnicoController.setItemRecebimento(itemRecebimento);		
			analiseTecnicoController.setIndice(1);
			
			arquivosAnexosController.setItemRecebimento(itemRecebimento);
			arquivosAnexosController.setIndice(1);
		}
		
		this.indice = 1;

		
	}
	
	public void carregarNotificacaoTecnico() {
		if (indice.equals(0)){
			this.dataModel.reiniciarPaginator();
		}else{			
			this.dataModel.limparPesquisa();
		}
	}
	
	
	@Override
	public List<PtmNotificacaoTecnica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.patrimonioFacade.obterqNotificacoesTecnica(firstResult, maxResult, orderProperty, asc, this.itemRecebimento.getIrpSeq());
	}
	
	public void encaminharSolicitacao() {
		this.encaminharSolicitacaoAnaliseTecnicaController.setAceiteTecnicoParaSerRealizadoVO(this.aceiteTecnicoParaSerRealizadoVO);
		this.encaminharSolicitacaoAnaliseTecnicaController.iniciar();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('modalEncaminharSolicitacaoAnaliseTecWG').show();");
	}
	
	/**
	 * Ação de confirmação do modal de atender aceite técnico.  
	 */
	public String atenderAceiteTecnico() {		
		this.patrimonioFacade.atenderAceiteTecnico(aceiteTecnicoParaSerRealizadoVO.getRecebimento(), aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());					
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ASSUMIR_TAREFA");
					
		return null;
	}
	
	/**
	 * Ação do botão Cadastrar Notas Técnica 
	 * @throws ApplicationBusinessException 
	 */
	public String cadastrarNotificacoesTecnicas() throws ApplicationBusinessException{
		String resultado=null;
		List<Long> lista = new ArrayList<Long>();
		List<PtmItemRecebProvisorios> ptmItemRecebProvisorio = this.patrimonioFacade.pesquisarItemRecebProvisorios(this.aceiteTecnicoParaSerRealizadoVO.getRecebimento(), this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento(), null);		
		for(PtmItemRecebProvisorios ptmItem: ptmItemRecebProvisorio){
			lista.add(ptmItem.getSeq());			
		}
		Integer  item = this.patrimonioFacade.verificarResponsavelAceiteTecnico(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()), lista);
		if(item == 0){
			List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
			listaAceiteTecnicoSelecionadosVO.add(this.aceiteTecnicoParaSerRealizadoVO);
			cadastroNotificacaoTecnicaController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
			cadastroNotificacaoTecnicaController.setListaAceiteTecnicoCadastroNotificacaoVO(listaAceiteTecnicoSelecionadosVO);
			resultado = PAGINA_CADASTRAR_NOTIFICACOES_TECNICAS;			
		}else if(item == 1){
			this.apresentarMsgNegocio(Severity.INFO, "USUARIO_NAO_RESP_NOTIFICACAO");
		}else if(item == 2){
			this.apresentarMsgNegocio(Severity.INFO, "ITEM_RECEB_DISTINTOS_USUARIOS");
		}
		return resultado;
	}
	
	public String anexarDocumentos() {
		
		PtmItemRecebProvisorios ptmItemRecebProvisorios = new PtmItemRecebProvisorios();
		
		ptmItemRecebProvisorios.setNrpSeq(this.aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		ptmItemRecebProvisorios.setNroItem(this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
		
		this.anexosArquivosListPaginatorController.setRecebimentoItem(ptmItemRecebProvisorios);
		
		return ANEXOS_ARQUIVOS_LIST;
	}
	
	/**
	 * Ação do botão Designar Técnico.
	 */
	public String designarTecnico() {
		
		boolean possuiTecnicoDesignado = false;
		
		List<RapServidores> lista = this.registroColaboradorFacade.obterServidorTecnicoPorItemRecebimento(
				this.aceiteTecnicoParaSerRealizadoVO.getRecebimento(), this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
		if (lista != null && !lista.isEmpty()) {
			possuiTecnicoDesignado = true;				
		}
		
		if (possuiTecnicoDesignado) {
			RequestContext.getCurrentInstance().execute("PF('modalConfirmacaoAlteracaoTecnico').show()");
			return null;
		} else {
			return designarTecnicoResponsavelMultiple();
		}		
	}
	
	/**
	 * Ação de confirmação do modal de alteração de tecnico responsavel pela avaliação do bem  
	 */
	public String designarTecnicoResponsavelMultiple() {
		List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		listaAceiteTecnicoSelecionadosVO.add(this.aceiteTecnicoParaSerRealizadoVO);
		this.designaTecnicoResponsavelController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
		this.designaTecnicoResponsavelController.setListaAceiteTecnico(listaAceiteTecnicoSelecionadosVO);
		this.designaTecnicoResponsavelController.setAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO);
		this.designaTecnicoResponsavelController.setModoEdicao(true);
		
		return PAGINA_DESIGNAR_TECNICO_RESPONSAVEL;
	}
	
	public String registrarAceiteTecnico(){
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		PtmItemRecebProvisorios pirp = patrimonioFacade.pesquisarItemRecebSeq(this.aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
				this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
		if(this.patrimonioFacade.verificarUsuarioLogadoResponsavelPorAceite(pirp.getSeq(), servidor)){
			return verificarStatusRealizarAceite();
		}else{
			RapServidoresVO obj2 = this.patrimonioFacade.verificarUsuarioLogadoResponsavelPorAreaTecnica(
					this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento(), this.aceiteTecnicoParaSerRealizadoVO.getRecebimento());
			if(obj2 != null){
				if(obj2.getMatricula() != null && obj2.getVinculo() != null){
					if(CoreUtil.igual(obj2.getMatricula(), servidor.getId().getMatricula()) &&
							CoreUtil.igual(obj2.getVinculo(), servidor.getId().getVinCodigo())){
						return verificarStatusRealizarAceite();
					}else{
						apresentarMsgNegocio(Severity.WARN, "ACAO_NAO_PERMITIDA");
						return null;
					}
				}
			}
		}
			
		
		return null;
	}
	
	private String verificarStatusRealizarAceite() {
		if(!DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao().equals(EM_AVALIACAO_TECNICA)) {
			apresentarMsgNegocio(Severity.WARN, "STATUS_NAO_VALIDO");
			return null;
		} else {
			this.registrarAceiteTecnicoPaginatorController.setAceiteTecRealizadoVO(this.aceiteTecnicoParaSerRealizadoVO);
			return PAGE_PESQUISAR_ACEITE_TECNICO;
		}
	}
	
	/**
	 * Ação do botão Visualizar Detalhes Solicitação Analise Tecnica. 
	 */
	public String designarTecnicoResponsavelSingle() {

		if(!this.aceiteTecnicoParaSerRealizadoVO.getStatus().equals(7)){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO);
			return null;
		}
		this.designaTecnicoResponsavelController.setAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO);
		this.designaTecnicoResponsavelController.setModoEdicao(false);
		this.designaTecnicoResponsavelController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
		return PAGINA_DESIGNAR_TECNICO_RESPONSAVEL;
	}
	
	public String imprimirNotificacoes() throws ApplicationBusinessException {
		
		relatorioNotifTecRecebimentoProvisorioController.setRecebimento(this.aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		relatorioNotifTecRecebimentoProvisorioController.setItemRecebimento(this.aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
		
		relatorioNotifTecRecebimentoProvisorioController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
		
		return relatorioNotifTecRecebimentoProvisorioController.visualizarImpressao();
	}
	
	/**
	 * Ação do Botão Retirada do Bem.
	 * 
	 * @return caminho para a tela de Retirada de Bem Permanente.
	 */
	public String iniciarRetirada() {

		retiradaBemPermanenteController.setItensSelecionados(new ArrayList<AceiteTecnicoParaSerRealizadoVO>());
		retiradaBemPermanenteController.setItensRetiradaList(new ArrayList<AceiteTecnicoParaSerRealizadoVO>());
		retiradaBemPermanenteController.setItensDetalhamentoList(new ArrayList<DetalhamentoRetiradaBemPermanenteVO>());
		retiradaBemPermanenteController.setItensDetalhamentoListCompleta(new ArrayList<DetalhamentoRetiradaBemPermanenteVO>());
		retiradaBemPermanenteController.setDetalhamento(Boolean.FALSE);

		retiradaBemPermanenteController.getItensRetiradaList().add(this.aceiteTecnicoParaSerRealizadoVO);		

		try {
			patrimonioFacade.validarInicializacaoRetiradaBemPermanente(retiradaBemPermanenteController.getItensRetiradaList());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		retiradaBemPermanenteController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
		
		return PAGINA_RETIRADA_BEM_PERMANENTE;
	}
	
	/**
	 * Ação do botão Devolução do Bem
	 * @return String de navegação
	 * @throws ApplicationBusinessException
	 */
	public String iniciarDevolucao() throws ApplicationBusinessException {
		registrarDevolucaoBemPermanenteController.setPermitirDevolucao(true);
		registrarDevolucaoBemPermanenteController.setAllChecked(false);
		registrarDevolucaoBemPermanenteController.setItensDevolucaoSelecionados(new DevolucaoBemPermanenteVO[0]);
		registrarDevolucaoBemPermanenteController.setListaAceiteTecnico(new ArrayList<AceiteTecnicoParaSerRealizadoVO>());
		
		if (DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao().equals(STATUS_CONCLUIDA)
				|| DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao().equals(STATUS_NÃO_CONFORME)
				|| DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao().equals(STATUS_PARCIALMENTE_ACEITO)
				|| DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao().equals(STATUS_ACEITO)) {
			registrarDevolucaoBemPermanenteController.getListaAceiteTecnico().add(this.aceiteTecnicoParaSerRealizadoVO);
		} else {
			registrarDevolucaoBemPermanenteController.setPermitirDevolucao(false);					
		}
			
		
		if (registrarDevolucaoBemPermanenteController.isPermitirDevolucao()) {			
			registrarDevolucaoBemPermanenteController.setDevBemPermanDataModel(new RegistrarDevolucaoBemPermanenteDataModel());
			registrarDevolucaoBemPermanenteController.setListaDevolucaoBemPermanente(new ArrayList<DevolucaoBemPermanenteVO>());
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PATRIMONIO);
			for(AceiteTecnicoParaSerRealizadoVO itemBemPermanente : registrarDevolucaoBemPermanenteController.getListaAceiteTecnico()){
				List<DevolucaoBemPermanenteVO> listaDevolucoes = this.patrimonioFacade.carregarListaBemPermanente(itemBemPermanente.getSeqItemPatrimonio(), parametro.getVlrNumerico().intValue());
				if(listaDevolucoes != null && !listaDevolucoes.isEmpty()){
					for(DevolucaoBemPermanenteVO devolucao : listaDevolucoes){
						registrarDevolucaoBemPermanenteController.getListaDevolucaoBemPermanente().add(devolucao);
					}
					registrarDevolucaoBemPermanenteController.setDevBemPermanDataModel(new RegistrarDevolucaoBemPermanenteDataModel(registrarDevolucaoBemPermanenteController.getListaDevolucaoBemPermanente()));
				}
			}
			
			registrarDevolucaoBemPermanenteController.setPaginaRetorno(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
			
			return PAGINA_REGISTRAR_DEVOLUCAO_BEM_PERMANENTE;
		}else{
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DEVOLUCAO_NAO_PERMITIDA");
			return null;
		}
	}
	
	public Boolean desabilitarBotaoNotasTecnicas() {
		Boolean retorno = true;
        
    	RapServidores servidor = null;
        try {
        	servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
        } catch (ApplicationBusinessException e) {
        	apresentarExcecaoNegocio(e);
        }

    	if (servidor != null && servidor.getId() != null && this.aceiteTecnicoParaSerRealizadoVO.getTecnicoResponsavel() != null 
    			&& this.aceiteTecnicoParaSerRealizadoVO.getStatus() != null 
    			&& DominioStatusAceiteTecnico.EM_AVALIACAO_TECNICA.getDescricao().equals(DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(this.aceiteTecnicoParaSerRealizadoVO.getStatus()).getDescricao())
                && this.aceiteTecnicoParaSerRealizadoVO.getTecnicoResponsavel().equals(servidor.getId().getMatricula())) {
    		retorno = false;
        } else {
        	retorno = true;                     
        }
                     
        return retorno;
	}
	
	public String statusNotificacaoTecnico(int status) {
		
		return DominioStatusNotificacaoTecnica.obterDominioStatusNotificacaoTecnica(status);
	}
	
	public String statusNotificacaoTecnicoHistorico(int status) {
		
		return DominioStatusTicket.obterDominioStatusNotificacaoTecnica(status);
	}
	
	public String obterTipoDocumento(String i){
		return DominioTipoDocumentoPatrimonio.getDescricao(Integer.parseInt(i));
	}

	@Override
	public Long recuperarCount() {
		return this.patrimonioFacade.obterqNotificacoesTecnicaCount(this.itemRecebimento.getIrpSeq());
	}
	
	public String reImprimirProtocolo() {		
		reImprimirProtocoloRetiradaBemPermanenteController.setCameFrom(PAGINA_PATRIMONIO_ITEM_RECEBIMENTO);
		reImprimirProtocoloRetiradaBemPermanenteController.getListaAceiteTecnico().add(aceiteTecnicoParaSerRealizadoVO);
		
		return PATRIMONIO_RE_IMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE;
	}

	public String voltar() {
		itemRecebimento = null;
		itemSelecionado = null;
		nomeUsuarioVO = null;
		aceiteTecnicoParaSerRealizadoVO = null;
		this.dataModel.limparPesquisa();
		historicoEncaminhamentoController.getDataModel().limparPesquisa();
		arquivosAnexosController.getDataModel().limparPesquisa();
		
		return ITEM_LISTAR_ACEITES_TECNICOS;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public AceiteTecnicoParaSerRealizadoVO getAceiteTecnicoParaSerRealizadoVO() {
		return aceiteTecnicoParaSerRealizadoVO;
	}

	public void setAceiteTecnicoParaSerRealizadoVO(
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) {
		this.aceiteTecnicoParaSerRealizadoVO = aceiteTecnicoParaSerRealizadoVO;
	}

	public ItemRecebimentoVO getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(ItemRecebimentoVO itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public NomeUsuarioVO getNomeUsuarioVO() {
		return nomeUsuarioVO;
	}

	public void setNomeUsuarioVO(NomeUsuarioVO nomeUsuarioVO) {
		this.nomeUsuarioVO = nomeUsuarioVO;
	}

	public DynamicDataModel<PtmNotificacaoTecnica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PtmNotificacaoTecnica> dataModel) {
		this.dataModel = dataModel;
	}

	public PtmNotificacaoTecnica getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PtmNotificacaoTecnica itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}
}
