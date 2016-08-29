package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ConsultarExamesPOLController
 * @author aghu
 * 
 */
public class ConsultarExamesPOLController extends ActionController {

	private static final long serialVersionUID = 8577151165931146997L;

		
	private static final Log LOG = LogFactory.getLog(ConsultarExamesPOLController.class);
	private final static int ABA_LIBERADOS=0, ABA_PENDENTES=1, ABA_CANCELADOS=2;
	private final static String PAGE_DETALHAR_ITEM="exames-detalharItemSolicitacaoExame";
	private final static String PAGE_VISUALIZAR_RESULTADO="exames-visualizarResultado";
	private final static String PAGE_RESULTADO_NOTA_ADICIONAL="exames-consultarResultadoNotaAdicional";
	private final static String PAGE_RELATORIO_ATOS_ANESTESICOS="pol-relatorioAtosAnestesicosPdf";
	private static final String EXAMES_RESULTADO_NOTA_ADICIONAL = "exames-resultadoNotaAdicional";
	private static final String VISUALIZAR_RESULTADO_EXAME = "visualizarResultadoExame";
	private static final String POL_EXAME = "pol-exame";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SecurityController securityController;

	@Inject
	private RelatorioAtosAnestesicosController relatorioAtosAnestesicosController;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;

	private List<ItemSolicitacaoExamePolVO> listaExamesLiberados;
	private List<ItemSolicitacaoExamePolVO> listaExamesPendentes;
	private List<ItemSolicitacaoExamePolVO> listaExamesCancelados;

	private Short unidadeExame;

	private Date dataExame;

	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer gmaSeq;
	
	private boolean permissaoVerResultados = false;
	private boolean permissaoVerImagens = false;


	private String situacaoExameLiberado;
	private String situacaoExameExecutando;
	
	/** Flag para controle da apresentacao da aba Exames Liberados */
	private Boolean renderLiberados = false;
	
	/**  Flag para controle da apresentacao da aba Exames Pendentes */
	private Boolean renderPendentes = false;
	
	/**  Flag para controle da apresentacao da aba Exames Cancelados */
	private Boolean renderCancelados = false;

	private Map<Integer, Vector<Short>> itensSelecionadosConsulta;

	/** Aba corrente */
	private Integer currentTab;

	private Boolean acessoAdminPOL;
	private Boolean botaoAtoAnestesicoDisabled = Boolean.TRUE;
	private MpmPrescricaoMedica prescricaoMedica;
	
	private Long seqMbcFichaAnestesia; 
	private String vSessao;
	private Boolean permiteImpressaoRelAtoAnestesico;
	private Map<Integer, Vector<Short>> examesSelecionados = new HashMap<Integer, Vector<Short>>();
	private Map<Integer, Vector<Short>> examesSelecionadosImagem = new HashMap<Integer, Vector<Short>>();
	
	// origem da pesquisa dos exames
	private String origemLabel;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;
	
	public Boolean hasAcessoAdminPol(){
		return acessoAdminPOL;
	}

	public enum EnumConsultarExamesAmostrasColetadasPOL {
		ORIGEM_LABEL_AMOSTRAS_COLETADAS, ORIGEM_LABEL_AMOSTRAS_COLETADAS_GRUPO, VISUALIZAR_RESULTADO_EXAME, ERRO_CHAMADA_MOMENTO_INVALIDO_EXAMES_POL_AMOSTRAS_COLETADAS
	}
	
	
	@PostConstruct
	protected void inicializar(){
		begin(conversation);

		// Inicializa listas
		listaExamesLiberados = null;
		listaExamesPendentes = null;
		listaExamesCancelados = null;
		
		try {
			situacaoExameLiberado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			situacaoExameExecutando = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
			acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}	
	
	public void inicio() {

		if (EnumConsultarExamesAmostrasColetadasPOL.ORIGEM_LABEL_AMOSTRAS_COLETADAS.toString().equals(origemLabel)) {
			setDataExame(null);
			setGmaSeq(null);
		}
		if (EnumConsultarExamesAmostrasColetadasPOL.ORIGEM_LABEL_AMOSTRAS_COLETADAS_GRUPO.toString().equals(origemLabel)) {
			setDataExame(null);
		}
		
		if (itemPOL!=null){
			unidadeExame=(Short) itemPOL.getParametros().get("unfSeq");
			if (itemPOL.getParametros().containsKey("dtExame")){
				dataExame=DateUtils.truncate((Date) itemPOL.getParametros().get("dtExame"), Calendar.DATE);
			}	
			gmaSeq=(Integer) itemPOL.getParametros().get("gmaSeq");
		}

		// Inicializa Aba Default
		if(currentTab == null){
			currentTab = ABA_LIBERADOS;
		}

		this.renderAbas(null);
		
	}


	public String redirecionarVisualizarResultadoExame(Integer iseSoeSeq, Short iseSeqp) {
		if (iseSoeSeq != null && iseSeqp != null) {
			setIseSoeSeq(iseSoeSeq);
			setIseSeqp(iseSeqp);
			return VISUALIZAR_RESULTADO_EXAME;
		}
		return null;
	}
	
	public String abrirDetalhamento(){
		return PAGE_DETALHAR_ITEM;
	}
	
	public String abrirVisualizarResultado(){
		return PAGE_VISUALIZAR_RESULTADO;
	}
	
	public String abrirResultadoNotaAdicional(){
		return EXAMES_RESULTADO_NOTA_ADICIONAL;
	}
	
	
	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 */
	public void renderAbas(TabChangeEvent event) {

		this.setRenderLiberados(false);
		this.setRenderPendentes(false);
		this.setRenderCancelados(false);
		setBotaoAtoAnestesicoDisabled(Boolean.TRUE);
		setPrescricaoMedica(null);
		examesSelecionados = new HashMap<Integer, Vector<Short>>();
		examesSelecionadosImagem = new HashMap<Integer, Vector<Short>>();

		try{
			switch (currentTab) {
				case ABA_LIBERADOS: renderAbaLiberados(); break;
				case ABA_PENDENTES: renderAbaPendentes(); break;
				case ABA_CANCELADOS: renderAbaCancelados(); break;
			} 
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}

	}

	/**
	 * Busca Exames Liberados.
	 */
	private void renderAbaLiberados() throws ApplicationBusinessException, ParseException  {
		if(listaExamesLiberados == null){
			listaExamesLiberados = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacao(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())), 
																									unidadeExame, dataExame,	DominioSituacaoItemSolicitacaoExame.LI, getGmaSeq());
		}else{
			for (ItemSolicitacaoExamePolVO liberados : listaExamesLiberados) {
				liberados.setItemSelecionadoLista(false);
			}
		}
	}

	/**
	 * Busca Exames Pendentes.
	 */
	private void renderAbaPendentes() throws BaseException, ParseException {
		if(listaExamesPendentes == null){
			listaExamesPendentes = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacao(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())),
																									unidadeExame, dataExame, DominioSituacaoItemSolicitacaoExame.PE, getGmaSeq());
		}else{
			for (ItemSolicitacaoExamePolVO liberados : listaExamesPendentes) {
				liberados.setItemSelecionadoLista(false);
			}
		}
	}

	/**
	 * Busca Exames Cancelados.
	 */
	private void renderAbaCancelados() throws BaseException, ParseException {
		if(listaExamesCancelados == null){
			listaExamesCancelados = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacao(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())),
									 															    unidadeExame, dataExame, DominioSituacaoItemSolicitacaoExame.CA, getGmaSeq());
		}else{
			for (ItemSolicitacaoExamePolVO liberados : listaExamesCancelados) {
				liberados.setItemSelecionadoLista(false);
			}
		}
	}

	/**
	 * Seleciona um exame na lista
	 */
	public void selecionarItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado, boolean isGuiaExamesPendentes, boolean examePossuiImagem) {
		prescricaoMedica = null;

		if (isGuiaExamesPendentes) {
			permissaoVerResultados = this.prontuarioOnlineFacade.verificarPermissaoVisualizarResultadoExames(codigoSoeSelecionado,
					iseSeqSelecionado);
		} else {
			// exame liberado não tem regra de liberação da undiade executora, sempre pode ver
			permissaoVerResultados = true;
		}

		// Valida situação dos exames selecionados. Situações válidas: LIBERADO OU EXECUTANDO
		boolean isSituacaoValida = this.validaSituacaoExamesSelecionados(codigoSoeSelecionado, iseSeqSelecionado);

		if (permissaoVerResultados && isSituacaoValida) {
			if (examesSelecionados.containsKey(codigoSoeSelecionado)) {
				if (examesSelecionados.get(codigoSoeSelecionado).contains(iseSeqSelecionado)) {

					examesSelecionados.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

					if (examesSelecionados.get(codigoSoeSelecionado).size() == 0) {
						examesSelecionados.remove(codigoSoeSelecionado);
					}
				} else {
					examesSelecionados.get(codigoSoeSelecionado).add(iseSeqSelecionado);
				}
			} else {
				examesSelecionados.put(codigoSoeSelecionado, new Vector<Short>());
				examesSelecionados.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		}else if(examePossuiImagem){
			if (examesSelecionadosImagem.containsKey(codigoSoeSelecionado)) {
				if (examesSelecionadosImagem.get(codigoSoeSelecionado).contains(iseSeqSelecionado)) {

					examesSelecionadosImagem.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

					if (examesSelecionadosImagem.get(codigoSoeSelecionado).size() == 0) {
						examesSelecionadosImagem.remove(codigoSoeSelecionado);
					}
				} else {
					examesSelecionadosImagem.get(codigoSoeSelecionado).add(iseSeqSelecionado);
				}
			} else {
				examesSelecionadosImagem.put(codigoSoeSelecionado, new Vector<Short>());
				examesSelecionadosImagem.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		}

		selecionarItemExamePt2(codigoSoeSelecionado, iseSeqSelecionado, isGuiaExamesPendentes, examePossuiImagem);
	}

	private void selecionarItemExamePt2(Integer codigoSoeSelecionado, Short iseSeqSelecionado, boolean isGuiaExamesPendentes, boolean examePossuiImagem) {
		if (examesSelecionados != null && examesSelecionados.size() > 1
				&& examesSelecionados.get(examesSelecionados.keySet().toArray()[0]).size() > 1) {// Somente um exame deve estar selecionado
			
			Integer soeSeqAnestesico = (Integer) examesSelecionados.keySet().toArray()[0];
			Short seqpAnestesico = examesSelecionados.get(soeSeqAnestesico).get(1);
			// necessário processamento porque pode ocorrer do selecionado não ser oque sobrou na lista
			botaoAtoAnestesicoDisabled = blocoCirurgicoFacade.listarFichasAnestesiasPorItemSolicExame(soeSeqAnestesico, seqpAnestesico,
																									  DominioIndPendenteAmbulatorio.V, Boolean.TRUE).isEmpty();
		} else {
			botaoAtoAnestesicoDisabled = Boolean.TRUE;
		}
		
		if(examePossuiImagem){
			if (examesSelecionadosImagem != null && examesSelecionadosImagem.size() > 1
					&& examesSelecionadosImagem.get(examesSelecionadosImagem.keySet().toArray()[0]).size() > 1) {// Somente um exame deve estar selecionado
				
				Integer soeSeqAnestesico = (Integer) examesSelecionadosImagem.keySet().toArray()[0];
				Short seqpAnestesico = examesSelecionadosImagem.get(soeSeqAnestesico).get(1);
				// necessário processamento porque pode ocorrer do selecionado não ser oque sobrou na lista
				botaoAtoAnestesicoDisabled = blocoCirurgicoFacade.listarFichasAnestesiasPorItemSolicExame(soeSeqAnestesico, seqpAnestesico,
																										  DominioIndPendenteAmbulatorio.V, Boolean.TRUE).isEmpty();
			} else {
				botaoAtoAnestesicoDisabled = Boolean.TRUE;
			}
		}
		
		ItemSolicitacaoExamePolVO itemExameSelec = verificaSePossuiApenasUmExameSelecionado(isGuiaExamesPendentes);
		if(itemExameSelec != null){
			List<AghAtendimentos> atdAmbulatoriais = aghuFacade.pesquisarAtendimentosPorItemSolicitacaoExame(codigoSoeSelecionado, iseSeqSelecionado,DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
		
			if(!atdAmbulatoriais.isEmpty()){
				List<MpmPrescricaoMedica> presrs = prescricaoMedicaFacade.pesquisarPrescricoesMedicaNaoPendentes(atdAmbulatoriais.get(0).getSeq(), null); 
				this.prescricaoMedica = !presrs.isEmpty() ? presrs.get(0) : null;
			}
		}
	}

	/**
	 * Varre os exames selecionados na lista, só retorna quando possui apenas um selecionado
	 */
	private ItemSolicitacaoExamePolVO verificaSePossuiApenasUmExameSelecionado(boolean isGuiaExamesPendentes) {
		ItemSolicitacaoExamePolVO voF = null;
		Integer countSelecionados = 0;
		if(isGuiaExamesPendentes){
			for(ItemSolicitacaoExamePolVO vo : listaExamesPendentes){
				if(vo.isItemSelecionadoLista()){
					countSelecionados++;
					voF = vo;
				}
			}
		}else{
			for(ItemSolicitacaoExamePolVO vo : listaExamesLiberados){
				if(vo.isItemSelecionadoLista()){
					countSelecionados++;
					voF = vo;
				}
			}
		}
		if(countSelecionados == 1){
			return voF;
		}else{
			return null;
		}
	}


	/**
	 * Valida situação dos exames selecionados
	 */
	public boolean validaSituacaoExamesSelecionados(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		AelItemSolicitacaoExames itemSolicitacaoExames = this.examesFacade.buscaItemSolicitacaoExamePorId(codigoSoeSelecionado, iseSeqSelecionado);

		if (itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExameLiberado)
				|| itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExameExecutando)) {
			return true;
		}

		return false;
	}
	
	public boolean verificarOcorrenciaExameSelecionado() {
		return (this.examesSelecionados != null && this.examesSelecionados.size() > 0) || (this.examesSelecionadosImagem != null && this.examesSelecionadosImagem.size() > 0) ? true : false;
	}

	/**
	 * Verifica se o exame tem permissão para visualizar Notas Adicionais
	 */
	public boolean verificarPermissaoVisualizarNotasAdicionais(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		return this.prontuarioOnlineFacade.verificarPermissaoVisualizarNotasAdicionais(codigoSoeSelecionado, iseSeqSelecionado);
	}

	/**
	 * Visualiza resultado dos exames selecionados na lista
	 */
	public String consultarResultadoNotaAdicional() throws BaseException, ParseException {
		if (examesSelecionados != null && examesSelecionados.size() > 0) {
			consultarResultadosNotaAdicionalController.setSolicitacoes(examesSelecionados);
			consultarResultadosNotaAdicionalController.setOrigemProntuarioOnline(Boolean.TRUE);
			consultarResultadosNotaAdicionalController.setVoltarPara(POL_EXAME);
			
			// Limpa a seleção
			limpaSelecao();
			// Fim limpeza
			return PAGE_RESULTADO_NOTA_ADICIONAL;
		}
		return null;
	}

	private void limpaSelecao() {
		examesSelecionados = new HashMap<Integer, Vector<Short>>();
		examesSelecionadosImagem = new HashMap<Integer, Vector<Short>>();
		switch (currentTab) {
			case ABA_LIBERADOS:  for(ItemSolicitacaoExamePolVO exa : listaExamesLiberados) {  exa.setItemSelecionadoLista(false); } break;
			case ABA_PENDENTES:  for(ItemSolicitacaoExamePolVO exa : listaExamesPendentes) {  exa.setItemSelecionadoLista(false); } break;
			case ABA_CANCELADOS: for(ItemSolicitacaoExamePolVO exa : listaExamesCancelados) { exa.setItemSelecionadoLista(false); } break;
		}
	}
		

	/**
	 * Verificar se usuário não possui permissão “cessoAdminPOL e se o valor data do parâmetro P_DATA_IMPL_MEDIMSERVER é menor que data atual.
	 */
	public boolean verificarPermissaoVisualizarImagensExame(boolean isGuiaExamesPendentes) {

		try {
			
			final Date dataImplMedimServer = parametroFacade.buscarValorData(AghuParametrosEnum.P_DATA_IMPL_MEDIMSERVER);
			final Date dataAtual = new Date();

			boolean isDataImplMedimServerMenorDataAtual = true;

			if (!isGuiaExamesPendentes) {
				isDataImplMedimServerMenorDataAtual = DateUtil.validaDataMenorIgual(dataImplMedimServer, dataAtual);
			}

			// Verifica se o perfil é diferente de 'acessoAdminPol' e o valor data do parâmetro P_DATA_IMPL_MEDIMSERVER é menor que a data atual
			return Boolean.FALSE.equals(this.acessoAdminPOL) && isDataImplMedimServerMenorDataAtual;

		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}

		return false;
	}

	/**
	 * Visualiza images dos exames exames selecionados na lista
	 */
	public String visualizarImagensExame() {
		
		Map<Integer, Vector<Short>> exames = null;
		if (examesSelecionados != null && examesSelecionados.size() > 0 ) {
			exames = examesSelecionados;
		}else if (examesSelecionadosImagem != null && examesSelecionadosImagem.size() > 0 ) {
			exames = examesSelecionadosImagem;
		}

		if(exames != null && exames.size() > 0 ) {
			try {
				// Verifica se os exames selecionados contem imagens associadas
				this.prontuarioOnlineFacade.verificarListaExamesPossuemImagem(exames);
			} catch (BaseException e) {
				// Exceção é não é lançada porque este método é utilizado em um
				// atributo disabled de um botão
				// não tem sentido lançar exceção
				// super.apresentarExcecaoNegocio(e);
				return null;
			}

			// Monta as duas primeiras partes da URL IMPAX
			String parte1UrlImpax = this.getParte1UrlImpax(exames); // window.open('agfahc://impax-client-epr/?user=pacshcpa&password=hcpapacs&domain=Agfa%20Healthcare&patientid=1572459
			if (parte1UrlImpax == null) {
				return null;
			}
			String parte2UrlImpax = this.getParte2UrlImpax(exames); // &accession=384024',
																				// 'IMPAX')

			// Retorna URL IMPAX
			return parte1UrlImpax + parte2UrlImpax;

		}

		return null;
	}

	/**
	 * Monta a primeira parte da URL IMPAX. Nesta parte são considerados somente os dados do paciente
	 */
	public String getParte1UrlImpax(Map<Integer, Vector<Short>> itensSelecionados) {

		Set<Integer> keys = itensSelecionados.keySet();

		Integer primeiraChave = null;
		for (Integer integer : keys) {
			primeiraChave = integer;
			break;
		}
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSoeSeq(primeiraChave);
		id.setSeqp(itensSelecionados.get(primeiraChave).get(0));
		
		AelItemSolicitacaoExames itemSolicitacaoExames = this.examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(id);
		return montaBaseUrlImpax(itemSolicitacaoExames);
	}

	private String montaBaseUrlImpax(AelItemSolicitacaoExames itemSolicitacaoExames) {

		Integer codPaciente = null;
		String url = null;

		try {

			AghParametros paramverImgImpax = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
			AghParametros paramEndWebImagens = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);

			if (itemSolicitacaoExames != null && paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))) {

				AelSolicitacaoExames solicit = examesFacade.obterAelSolicitacaoExamePorChavePrimaria(itemSolicitacaoExames.getSolicitacaoExame().getSeq(), AelSolicitacaoExames.Fields.ATENDIMENTO);
				
				if(solicit != null){
					if(solicit.getAtendimento() != null){
						codPaciente = solicit.getAtendimento().getPaciente().getCodigo();
					}else {
						codPaciente = solicit.getAtendimentoDiverso().getAipPaciente().getCodigo();
					}
				}

				if (codPaciente != null) {
					url = paramEndWebImagens.getVlrTexto() + codPaciente;
				}

			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}

		return url;
	}

	/**
	 * Monta a segunda parte da URL IMPAX. Nesta parte são considerados os itens de solicitação de exames e seus respectivos PAC_ORU_ACC_NUMBER
	 */
	public String getParte2UrlImpax(Map<Integer, Vector<Short>> itensSelecionados) {

		Set<Integer> solicitacoes = itensSelecionados.keySet();

		StringBuilder accessioms = new StringBuilder();

		int countAccessioms = 0;

		for (Integer soeSeq : solicitacoes) {

			Vector<Short> seqps = itensSelecionados.get(soeSeq);

			for (Short seqp : seqps) {

				AelItemSolicitacaoExames itemSolicitacaoExames = null;

				AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
				id.setSoeSeq(soeSeq);
				id.setSeqp(seqp);
				itemSolicitacaoExames = this.examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(id);

				String pacOruAccNumber = itemSolicitacaoExames.getPacOruAccNumber();

				if (pacOruAccNumber != null) {
					if (countAccessioms > 0) {
						// Quando há mais de um PAC_ORU_ACC_NUMBER o delimitador IMPAX '|' deve ser utilizado
						accessioms.append("%7C");
					}
					accessioms.append(pacOruAccNumber);
					countAccessioms++;
				}
			}
		}

		// Retorna o parâmetro accession no seguint foramato: &accession=<PAC_ORU_ACC_NUMBER1>|<PAC_ORU_ACC_NUMBER2>|...', 'IMPAX');
		return "%26accession%3D" + accessioms;
	}
	
	public String getUrlImpaxPorISE(Integer soeSeq, Short seqp) {
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSoeSeq(soeSeq);
		id.setSeqp(seqp);

		AelItemSolicitacaoExames itemSolicitacaoExames = this.examesFacade
				.obteritemSolicitacaoExamesPorChavePrimaria(id);
		String baseUrl = montaBaseUrlImpax(itemSolicitacaoExames);
		if (baseUrl != null) {
			StringBuffer url = new StringBuffer(100);
			url.append(baseUrl).append("%26accession%3D").append(itemSolicitacaoExames.getPacOruAccNumber());
			return url.toString();
		}
		return null;
	}

	public String visualizarRelatorioAtoAnestesico(){
		if (examesSelecionados != null && examesSelecionados.size() > 1
				&& examesSelecionados.get(examesSelecionados.keySet().toArray()[0]).size() > 1) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_REL_ATOANESTESICO_VARIOS_EXAMES_SELECIONADOS");
			return null;
		}
		try{
			Integer soeSeqAnestesico = (Integer) examesSelecionados.keySet().toArray()[0];
			Short seqpAnestesico = examesSelecionados.get(soeSeqAnestesico).get(1);
			String idSessao = ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
			Object[] relAtoAnest = prontuarioOnlineFacade.processarRelatorioAtosAnestesicos(null,
					soeSeqAnestesico, seqpAnestesico, idSessao);

			seqMbcFichaAnestesia = (Long) relAtoAnest[0];
			vSessao = (String) relAtoAnest[1];
			permiteImpressaoRelAtoAnestesico = (Boolean) relAtoAnest[2];

			relatorioAtosAnestesicosController.setSeqMbcFichaAnestesia(seqMbcFichaAnestesia);
			relatorioAtosAnestesicosController.setvSessao(vSessao);
			
			return PAGE_RELATORIO_ATOS_ANESTESICOS;
			
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  null;
	}
	
	/*
	 * Getters e setters
	 */

	public Boolean getRenderLiberados() {
		return renderLiberados;
	}

	public void setRenderLiberados(Boolean renderLiberados) {
		this.renderLiberados = renderLiberados;
	}

	public Boolean getRenderPendentes() {
		return renderPendentes;
	}

	public void setRenderPendentes(Boolean renderPendentes) {
		this.renderPendentes = renderPendentes;
	}

	public Boolean getRenderCancelados() {
		return renderCancelados;
	}

	public void setRenderCancelados(Boolean renderCancelados) {
		this.renderCancelados = renderCancelados;
	}

	public List<ItemSolicitacaoExamePolVO> getListaExamesLiberados() {
		return listaExamesLiberados;
	}

	public void setListaExamesLiberados(
			List<ItemSolicitacaoExamePolVO> listaExamesLiberados) {
		this.listaExamesLiberados = listaExamesLiberados;
	}

	public List<ItemSolicitacaoExamePolVO> getListaExamesPendentes() {
		return listaExamesPendentes;
	}

	public void setListaExamesPendentes(
			List<ItemSolicitacaoExamePolVO> listaExamesPendentes) {
		this.listaExamesPendentes = listaExamesPendentes;
	}

	public List<ItemSolicitacaoExamePolVO> getListaExamesCancelados() {
		return listaExamesCancelados;
	}

	public void setListaExamesCancelados(
			List<ItemSolicitacaoExamePolVO> listaExamesCancelados) {
		this.listaExamesCancelados = listaExamesCancelados;
	}

	public void setUnidadeExame(Short unidadeExame) {
		this.unidadeExame = unidadeExame;
	}

	public Short getUnidadeExame() {
		return unidadeExame;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Integer getGmaSeq() {
		return gmaSeq;
	}

	public void setGmaSeq(Integer gmaSeq) {
		this.gmaSeq = gmaSeq;
	}

	public String getOrigemLabel() {
		return origemLabel;
	}

	public void setOrigemLabel(String origemLabel) {
		this.origemLabel = origemLabel;
	}

	public Boolean getBotaoAtoAnestesicoDisabled() {
		return botaoAtoAnestesicoDisabled;
	}

	public void setBotaoAtoAnestesicoDisabled(Boolean botaoAtoAnestesicoDisabled) {
		this.botaoAtoAnestesicoDisabled = botaoAtoAnestesicoDisabled;
	}

	public Long getSeqMbcFichaAnestesia() {
		return seqMbcFichaAnestesia;
	}

	public String getvSessao() {
		return vSessao;
	}

	public void setSeqMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		this.seqMbcFichaAnestesia = seqMbcFichaAnestesia;
	}

	public void setvSessao(String vSessao) {
		this.vSessao = vSessao;
	}

	public Boolean getPermiteImpressaoRelAtoAnestesico() {
		return permiteImpressaoRelAtoAnestesico;
	}

	public void setPermiteImpressaoRelAtoAnestesico(
			Boolean permiteImpressaoRelAtoAnestesico) {
		this.permiteImpressaoRelAtoAnestesico = permiteImpressaoRelAtoAnestesico;
	}

	public Date getDataExame() {
		return dataExame;
	}

	public void setDataExame(Date dataExame) {
		this.dataExame = dataExame;
	}

	public Map<Integer, Vector<Short>> getItensSelecionadosConsulta() {
		return itensSelecionadosConsulta;
	}

	public void setItensSelecionadosConsulta(
			Map<Integer, Vector<Short>> itensSelecionadosConsulta) {
		this.itensSelecionadosConsulta = itensSelecionadosConsulta;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public boolean isPermissaoVerResultados() {
		return permissaoVerResultados;
	}

	public void setPermissaoVerResultados(boolean permissaoVerResultados) {
		this.permissaoVerResultados = permissaoVerResultados;
	}

	public boolean isPermissaoVerImagens() {
		return permissaoVerImagens;
	}

	public void setPermissaoVerImagens(boolean permissaoVerImagens) {
		this.permissaoVerImagens = permissaoVerImagens;
	}

	public Integer getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(Integer currentTab) {
		this.currentTab = currentTab;
	}

	public Map<Integer, Vector<Short>> getExamesSelecionadosImagem() {
		return examesSelecionadosImagem;
	}

	public void setExamesSelecionadosImagem(Map<Integer, Vector<Short>> examesSelecionadosImagem) {
		this.examesSelecionadosImagem = examesSelecionadosImagem;
	}
}
