package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class ConsultarExamesHistController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8577151165931146997L;
	private static final Log LOG = LogFactory.getLog(ConsultarExamesHistController.class);
	
	private final static int ABA_LIBERADOS = 0, ABA_PENDENTES = 1, ABA_CANCELADOS = 2;
	private final static String PAGE_DETALHAR_ITEM = "exames-detalharItemSolicitacaoExame";
	private final static String PAGE_VISUALIZAR_RESULTADO = "exames-visualizarResultadoHist";
	private final static String PAGE_RELATORIO_ATOS_ANESTESICOS = "pol-relatorioAtosAnestesicosPdf";
	private static final String CONSULTAR_RESULTADO_NOTA_ADICIONAL = "exames-consultarResultadoNotaAdicional";
	private static final String POL_EXAMES_HIST = "pol-consultarExames-hist";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SecurityController securityController;

	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;

	private Boolean origemPOL;

	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer prontuario;

	private Map<Integer, Vector<Short>> itensSelecionados;
	private List<ItemSolicitacaoExamePolVO> listaExamesLiberados;
	private List<ItemSolicitacaoExamePolVO> listaExamesPendentes;
	private List<ItemSolicitacaoExamePolVO> listaExamesCancelados;

	private Short unidadeExame;

	private Date data;

	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer gmaSeq;
	private Integer currentTab;

	private Boolean acessoAdminPOL;

	private String origemLabel;
	
	@Inject @Paginator
	private DynamicDataModel<ItemSolicitacaoExamePolVO> dataModel;

	private Boolean botaoAtoAnestesicoDisabled = Boolean.TRUE;

	private Long seqMbcFichaAnestesia;
	private String vSessao;
	private Boolean permiteImpressaoRelAtoAnestesico;

	private String situacaoExameLiberado;
	private String situacaoExameExecutando;
	

	private Map<Integer, Vector<Short>> examesSelecionados = new HashMap<Integer, Vector<Short>>();

	@Inject
	@SelectionQualifier
	@RequestScoped
	private NodoPOLVO itemPOL;

	public enum EnumConsultarExamesAmostrasColetadasPOL {
		ORIGEM_LABEL_AMOSTRAS_COLETADAS, ORIGEM_LABEL_AMOSTRAS_COLETADAS_GRUPO, VISUALIZAR_RESULTADO_EXAME, ERRO_CHAMADA_MOMENTO_INVALIDO_EXAMES_POL_AMOSTRAS_COLETADAS, ORIGEM_LABEL_DATA_AMOSTRAS_COLETADAS, ORIGEM_LABEL_OUTROS
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		
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

	public Boolean hasAcessoAdminPol() {
		return acessoAdminPOL;
	}

	public String abrirDetalhamento() {
		return PAGE_DETALHAR_ITEM;
	}

	public String abrirVisualizarResultado() {
		return PAGE_VISUALIZAR_RESULTADO;
	}

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void persistirVisualizacaoDownloadAnexo() {
		// Persiste o download ou visualizacao do documento anexado
		try {
			this.examesFacade.persistirVisualizacaoDownloadAnexoHist(this.iseSoeSeq, this.iseSeqp);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws DocumentException, IOException {
		ByteArrayOutputStream byteArrayOS = null;
		try {
			byteArrayOS = this.prontuarioOnlineFacade.buscarArquivoResultadoExameHist(iseSoeSeq, iseSeqp);
			return ActionReport.criarStreamedContentPdfPorByteArray(byteArrayOS.toByteArray());
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void directPrint() {
		try {
			ByteArrayOutputStream byteArrayOS = this.prontuarioOnlineFacade.buscarArquivoResultadoExameHist(iseSoeSeq, iseSeqp);
			this.sistemaImpressao.imprimir(byteArrayOS, super.getEnderecoIPv4HostRemoto(), "resultadoExamesHist");

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public void inicio() {

		if (EnumConsultarExamesAmostrasColetadasPOL.ORIGEM_LABEL_AMOSTRAS_COLETADAS.toString().equals(origemLabel)) {
			setData(null);
			setGmaSeq(null);
		} else if (EnumConsultarExamesAmostrasColetadasPOL.ORIGEM_LABEL_AMOSTRAS_COLETADAS_GRUPO.toString().equals(origemLabel)) {
			setData(null);
		} else if (!EnumConsultarExamesAmostrasColetadasPOL.ORIGEM_LABEL_DATA_AMOSTRAS_COLETADAS.toString().equals(origemLabel)) {
			setGmaSeq(null);
		}

		if (this.gmaSeq != null && this.gmaSeq == 0) {
			setGmaSeq(null);
		}

		if (itemPOL != null) {
			unidadeExame = (Short) itemPOL.getParametros().get("unfSeq");
			if (itemPOL.getParametros().containsKey("dtExame")) {
				data = DateUtils.truncate((Date) itemPOL.getParametros().get("dtExame"), Calendar.DATE);
			}
			gmaSeq = (Integer) itemPOL.getParametros().get("gmaSeq");
		}

		// Inicializa Aba Default
		currentTab = ABA_LIBERADOS;

		// Inicializa listas
		listaExamesLiberados = null;
		listaExamesPendentes = null;
		listaExamesCancelados = null;

		this.renderAbas();
	}

	/**
	 * Marca todas as abas como nao selecionadas.
	 */
	public String redirecionarVisualizarResultadoExame(Integer iseSoeSeq, Short iseSeqp) {
		if (iseSoeSeq != null && iseSeqp != null) {
			setIseSoeSeq(iseSoeSeq);
			setIseSeqp(iseSeqp);

			return PAGE_VISUALIZAR_RESULTADO;
		}

		return null;
	}
	
	
	public String voltar() {
		return POL_EXAMES_HIST;
	}

	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {

		if (this.currentTab == null) {
			throw new IllegalStateException("Este metodo foi chamado em um momento invalido da controller.");
		}

		setBotaoAtoAnestesicoDisabled(Boolean.TRUE);
		examesSelecionados = new HashMap<Integer, Vector<Short>>();

		examesSelecionados.clear();

		try {
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

		dataModel.reiniciarPaginator();
	}

	/**
	 * Busca Exames Liberados.
	 */
	private void renderAbaLiberados() throws BaseException, ParseException {
		if(listaExamesLiberados == null){
			Short unidadeExame = this.verificarUnidadeExame();
			listaExamesLiberados = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacaoHist( ((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())), 
																										unidadeExame, data, 
																										DominioSituacaoItemSolicitacaoExame.LI,
																										getGmaSeq());
		}
	}

	private Short verificarUnidadeExame() {

		Short unidadeExame = null;
		if (this.unidadeExame != null && this.unidadeExame != 0) {
			unidadeExame = this.unidadeExame;
		}

		return unidadeExame;

	}

	/**
	 * Busca Exames Pendentes.
	 */
	private void renderAbaPendentes() throws BaseException, ParseException {

		if(listaExamesPendentes == null){
			Short unidadeExame = this.verificarUnidadeExame();
			listaExamesPendentes = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacaoHist( ((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())), 
																										unidadeExame, data, 
																										DominioSituacaoItemSolicitacaoExame.PE,
																										getGmaSeq() );
		}

	}

	/**
	 * Busca Exames Cancelados.
	 */
	private void renderAbaCancelados() throws BaseException, ParseException {
		
		if(listaExamesCancelados == null){
			
			Short unidadeExame = this.verificarUnidadeExame();
			
			listaExamesCancelados = prontuarioOnlineFacade.buscaExamesPeloCodigoDoPacienteESituacaoHist( ((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE.toString())), 
																										 unidadeExame, data, 
																										 DominioSituacaoItemSolicitacaoExame.CA,
																										 getGmaSeq() );
		}
	}

	/**
	 * Seleciona um exame na lista
	 */
	public void selecionarItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado, boolean isGuiaExamesPendentes) {

		// Verifica se o exame selecionado tem permissão para visualizar resultados
		boolean isPermissaoVerResultados = false;
		if (isGuiaExamesPendentes) {
			isPermissaoVerResultados = this.prontuarioOnlineFacade.verificarPermissaoVisualizarResultadoExamesHist(codigoSoeSelecionado, iseSeqSelecionado);
		} else {
			// exame liberado não tem regra de liberação da undiade executora, sempre pode ver
			isPermissaoVerResultados = true;
		}

		// Valida situação dos exames selecionados
		boolean isSituacaoValida = this.validaSituacaoExamesSelecionados(codigoSoeSelecionado, iseSeqSelecionado);

		if (isPermissaoVerResultados && isSituacaoValida) {

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
		}

		if (examesSelecionados != null && examesSelecionados.size() > 1 && examesSelecionados.get(examesSelecionados.keySet().toArray()[0]).size() > 1) {// Somente um exame deve
																																							// estar selecionado
			Integer soeSeqAnestesico = (Integer) examesSelecionados.keySet().toArray()[0];
			Short seqpAnestesico = examesSelecionados.get(soeSeqAnestesico).get(1);
			// necessário processamento porque pode ocorrer do selecionado não
			// ser oque sobrou na lista
			botaoAtoAnestesicoDisabled = blocoCirurgicoFacade.listarFichasAnestesiasPorItemSolicExame(soeSeqAnestesico, seqpAnestesico, DominioIndPendenteAmbulatorio.V, Boolean.TRUE).isEmpty();
		} else {
			botaoAtoAnestesicoDisabled = Boolean.TRUE;
		}

		// fim processarSelecaoExameParaAtoAnestesico
	}

	/**
	 * Valida situação dos exames selecionados
	 * 
	 * @param codigoSoeSelecionado
	 * @param iseSeqSelecionado
	 * @return
	 */
	public boolean validaSituacaoExamesSelecionados(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		AelItemSolicExameHist itemSolicitacaoExames = this.examesFacade.buscaItemSolicitacaoExamePorIdHistOrigemPol(codigoSoeSelecionado, iseSeqSelecionado);

		if (itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExameLiberado)
				|| itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExameExecutando)) {
			return true;
		}

		return false;
	}

	public boolean verificarOcorrenciaExameSelecionado() {
		return this.examesSelecionados != null && this.examesSelecionados.size() > 0 ? true : false;
	}

	/**
	 * Visualiza resultado dos exames selecionados na lista
	 */
	public String consultarResultadoNotaAdicional() throws BaseException, ParseException {
		if (examesSelecionados != null && examesSelecionados.size() > 0) {
			consultarResultadosNotaAdicionalController.setSolicitacoes(examesSelecionados);
			consultarResultadosNotaAdicionalController.setIsHist(true);
			consultarResultadosNotaAdicionalController.setOrigemProntuarioOnline(Boolean.TRUE);
			consultarResultadosNotaAdicionalController.setVoltarPara(POL_EXAMES_HIST);
			
			// Limpa a seleção
			limpaSelecao(currentTab);
			// Fim limpeza
			return CONSULTAR_RESULTADO_NOTA_ADICIONAL;
		}
		return null;
	}

	private void limpaSelecao(Integer currentTab) {
		examesSelecionados = new HashMap<Integer, Vector<Short>>();

		if (currentTab.equals(ABA_LIBERADOS)) {
			for (ItemSolicitacaoExamePolVO exa : listaExamesLiberados) {
				exa.setItemSelecionadoLista(false);
			}
		} else if (currentTab.equals(ABA_PENDENTES)) {
			for (ItemSolicitacaoExamePolVO exa : listaExamesPendentes) {
				exa.setItemSelecionadoLista(false);
			}
		} else if (currentTab.equals(ABA_CANCELADOS)) {
			for (ItemSolicitacaoExamePolVO exa : listaExamesCancelados) {
				exa.setItemSelecionadoLista(false);
			}
		}
	}

	/**
	 * Verifica se o exame tem permissão para visualizar Notas Adicionais
	 * 
	 * @param codigoSoeSelecionado
	 * @param iseSeqSelecionado
	 * @return
	 */
	public boolean verificarPermissaoVisualizarNotasAdicionais(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		return this.examesFacade.possuiNotasAdicionaisItemSolicitacaoExameHist(codigoSoeSelecionado, iseSeqSelecionado);
	}

	/**
	 * Verificar se usuário não possui permissão “cessoAdminPOL e se o valor data do parâmetro P_DATA_IMPL_MEDIMSERVER é menor que data atual.
	 * 
	 * @return
	 */
	public boolean verificarPermissaoVisualizarImagensExame(boolean isGuiaExamesPendentes) {

		AghParametros pDataImplMedimServer = null;

		try {
			pDataImplMedimServer = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DATA_IMPL_MEDIMSERVER);

			final Date dataImplMedimServer = pDataImplMedimServer.getVlrData();
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
	 * 
	 * @return
	 */
	public String visualizarImagensExame(boolean isGuiaExamesPendentes) {

		if (examesSelecionados != null && examesSelecionados.size() > 0) {
			try {
				// Verifica se os exames selecionados contem imagens associadas
				this.prontuarioOnlineFacade.verificarListaExamesPossuemImagemHistOrigemPol(examesSelecionados);
			} catch (BaseException e) {
				// Exceção é não é lançada porque este método é utilizado em um
				// atributo disabled de um botão
				// não tem sentido lançar exceção
				// super.apresentarExcecaoNegocio(e);
				return null;
			}

			// Monta as duas primeiras partes da URL IMPAX
			String parte1UrlImpax = this.getParte1UrlImpax(examesSelecionados);
			if (parte1UrlImpax == null) {
				return null;
			}
			String parte2UrlImpax = this.getParte2UrlImpax(examesSelecionados);

			// Retorna URL IMPAX
			return parte1UrlImpax + parte2UrlImpax;

		}

		return null;
	}

	/**
	 * Monta a primeira parte da URL IMPAX. Nesta parte são considerados somente os dados do paciente
	 * 
	 * @param itensSelecionados
	 * @return
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

		AelItemSolicExameHist itemSolicitacaoExames = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(id.getSoeSeq(), id.getSeqp());

		Integer codPaciente = null;
		String url = null;

		try {

			AghParametros paramverImgImpax = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
			AghParametros paramEndWebImagens = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);

			if (itemSolicitacaoExames != null && paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))) {

				codPaciente = itemSolicitacaoExames.getSolicitacaoExame().getAtendimento().getPaciente().getCodigo();

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
	 * 
	 * @param itensSelecionados
	 * @return
	 */
	public String getParte2UrlImpax(Map<Integer, Vector<Short>> itensSelecionados) {

		Set<Integer> solicitacoes = itensSelecionados.keySet();

		StringBuilder accessioms = new StringBuilder();

		int countAccessioms = 0;

		for (Integer soeSeq : solicitacoes) {

			Vector<Short> seqps = itensSelecionados.get(soeSeq);

			for (Short seqp : seqps) {

				// AelItemSolicitacaoExames itemSolicitacaoExames = null;

				AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
				id.setSoeSeq(soeSeq);
				id.setSeqp(seqp);
				AelItemSolicExameHist itemSolicitacaoExames = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(id.getSoeSeq(), id.getSeqp());

				String pacOruAccNumber = itemSolicitacaoExames.getPacOruAccNumber();

				if (pacOruAccNumber != null) {
					if (countAccessioms > 0) {
						// Quando há mais de um PAC_ORU_ACC_NUMBER o delimitador IMPAX '|' deve ser utilizado
						accessioms.append('|');
					}
					accessioms.append(pacOruAccNumber);
					countAccessioms++;
				}
			}
		}

		// Retorna o parâmetro accession no seguint foramato: &accession=<PAC_ORU_ACC_NUMBER1>|<PAC_ORU_ACC_NUMBER2>|...', 'IMPAX');
		return "%26accession%3D" + accessioms;
	}

	public String visualizarRelatorioAtoAnestesico() {

		if (examesSelecionados != null && examesSelecionados.size() > 1 && examesSelecionados.get(examesSelecionados.keySet().toArray()[0]).size() > 1) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_REL_ATOANESTESICO_VARIOS_EXAMES_SELECIONADOS");
			return null;
		}

		try {

			Integer soeSeqAnestesico = (Integer) examesSelecionados.keySet().toArray()[0];
			Short seqpAnestesico = examesSelecionados.get(soeSeqAnestesico).get(1);

			FacesContext context = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			String sessionId = session.getId();

			Object[] relAtoAnest = prontuarioOnlineFacade.processarRelatorioAtosAnestesicos(null, soeSeqAnestesico, seqpAnestesico, sessionId);
			seqMbcFichaAnestesia = (Long) relAtoAnest[0];
			vSessao = (String) relAtoAnest[1];
			permiteImpressaoRelAtoAnestesico = (Boolean) relAtoAnest[2];

			consultarResultadosNotaAdicionalController.setItensSelecionados(this.getItensSelecionados());
			renderAbas();
			return PAGE_RELATORIO_ATOS_ANESTESICOS;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public List<ItemSolicitacaoExamePolVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		if (currentTab.equals(ABA_LIBERADOS)) {
			return listaExamesLiberados;

		} else if (currentTab.equals(ABA_PENDENTES)) {
			return listaExamesPendentes;

		} else if (currentTab.equals(ABA_CANCELADOS)) {
			return listaExamesCancelados;
		}
		return new LinkedList<ItemSolicitacaoExamePolVO>();

	}

	@Override
	public Long recuperarCount() {
		Long count = 0L;
		if (currentTab.equals(ABA_LIBERADOS)) {
			count = Long.valueOf(listaExamesLiberados.size());
		} else if (currentTab.equals(ABA_PENDENTES)) {
			count = Long.valueOf(listaExamesPendentes.size());
		} else if (currentTab.equals(ABA_CANCELADOS)) {
			count = Long.valueOf(listaExamesCancelados.size());
		}
		return count;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	public DynamicDataModel<ItemSolicitacaoExamePolVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ItemSolicitacaoExamePolVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(Integer currentTab) {
		this.currentTab = currentTab;
	}

	public Map<Integer, Vector<Short>> getItensSelecionados() {
		return itensSelecionados;
	}

	public void setItensSelecionados(
			Map<Integer, Vector<Short>> itensSelecionados) {
		this.itensSelecionados = itensSelecionados;
	}

	public Boolean getOrigemPOL() {
		return origemPOL;
	}

	public void setOrigemPOL(Boolean origemPOL) {
		this.origemPOL = origemPOL;
	}

	public String getSituacaoExameLiberado() {
		return situacaoExameLiberado;
	}

	public void setSituacaoExameLiberado(String situacaoExameLiberado) {
		this.situacaoExameLiberado = situacaoExameLiberado;
	}

	public String getSituacaoExameExecutando() {
		return situacaoExameExecutando;
	}

	public void setSituacaoExameExecutando(String situacaoExameExecutando) {
		this.situacaoExameExecutando = situacaoExameExecutando;
	}
}