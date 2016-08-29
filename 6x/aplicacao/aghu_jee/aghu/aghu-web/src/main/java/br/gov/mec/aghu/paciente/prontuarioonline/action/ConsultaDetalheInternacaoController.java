package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.RelatorioAnaEvoInternacaoController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.certificacaodigital.action.VisualizarDocumentoController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por exibir as detalhes de uma internação
 * 
 * @author tfelini
 */

public class ConsultaDetalheInternacaoController extends ActionController {


	private static final Log LOG = LogFactory.getLog(ConsultaDetalheInternacaoController.class);

	private static final long serialVersionUID = 2175770723653572327L;

	private String tipoRelatorio;

	private String origem;

	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;	
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private RelatorioAtendimentoRNController relatorioAtendimentoRNController;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@Inject
	private RelatorioAnaEvoInternacaoController relatorioAnaEvoInternacaoController;
	
	@Inject
	private RelatorioRegistrosControlesPacienteController relatorioRegistrosControlesPacienteController;

	@Inject
	private VisualizarDocumentoController visualizarDocumentoController;
	
	private InternacaoVO internacaoVO;

	private Boolean moduloPrescricaoEnfermagemAtivo = Boolean.FALSE;

	@Inject
	private RelatorioPIM2Controller relatorioPIM2Controller;

	@Inject
	private VisualizacaoFichaApacheController visualizacaoFichaApacheController;
	
	@Inject
	private ConsultarInternacoesPOLController consultarInternacoesPOLController;
	
	@Inject
	private RelatorioExameFisicoRNController relatorioExameFisicoRNController;

	@Inject
	private SecurityController securityController;

	
	
	private AghVersaoDocumento aghVersaoDocumento;

	private String indImpPrevia = "D";

	private Boolean exibirModalGestacao = Boolean.FALSE;

	private Boolean acessoAdminPOL;

	private Boolean btVoltar;

	private Integer numeroProntuario;

	private List<McoRecemNascidos> recemNasc;
	private McoRecemNascidos recemNascSel;

	private Long itemSelecionado;
	
	private static final String POL_DETALHE_INTERNACAO = "pol-detalheInternacao";
	private static final String AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO = "ambulatorio-relatorioAmamneseEvolucaoInternacao";
	private static final String RELATORIO_EXAME_FISICO_RN = "relatorioExameFisicoRNPdf";
	private static final String RELATORIO_ATENDIMENTO_RECEM_NASCIDO = "relatorioAtendimentoRecemNascidoPdf";
	private static final String VISUALIZAR_DOCUMENTO_ASSINADO_POL = "certificacaodigital-visualizarDocumentoAssinadoPOL";
	private static final String POL_INTERNACAO = "pol-internacao";

	
	private Date dataInicial;

	private Date dataFinal;
	
	private Integer seq;
	private String tipo;
	private boolean fromConsultarInternacoes;
	
	protected enum EnumTargetConsultaDetalheInternacao {
		RELATORIO_EVOLUCAO_PERIODO, RELATORIO_ANAMNESES,  VISUALIZAR_RELATORIO_PIM2;
	}
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
		acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
	}

	public void inicio() {
	 
		
		try {
			if(!fromConsultarInternacoes){
				if (itemPOL != null){
					seq = (Integer)itemPOL.getParametros().get("seq");
					tipo = (String)itemPOL.getParametros().get("tipo");
				}else{
					seq = consultarInternacoesPOLController.getDetalheSeq();
					tipo = consultarInternacoesPOLController.getDetalheTipo();
					consultarInternacoesPOLController.setDetalheSeq(null);
					consultarInternacoesPOLController.setDetalheTipo(null);
					
					btVoltar=true;
				}
			} else {
				btVoltar=true;
			}
			
			AghParametros enfermagemAtivo;
			enfermagemAtivo = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_ENFERMAGEM_ATIVO);
			if (enfermagemAtivo.getVlrNumerico().equals(BigDecimal.ONE)) {
				setModuloPrescricaoEnfermagemAtivo(Boolean.TRUE);
			}
			this.internacaoVO = prontuarioOnlineFacade.buscaDetalhes(tipo,	seq);
			this.numeroProntuario = internacaoVO.getProntuario();
			setTipoRelatorio(null);

			obterInternacaoVOVerificarAcaoPermitida();

			//Incidente - AGHU #53377
			//Date today = DateUtil.truncaData(new Date());
			//this.setDataFinal(today);
			//this.setDataInicial(DateUtil.adicionaDias(today, -29));
			
			consultarInternacoesPOLController.setModoVoltar("D");
			consultarInternacoesPOLController.setInternacao(internacaoVO);
			consultarInternacoesPOLController.detalharInternacao();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		consultarInternacoesPOLController.setInternacao(null);
		return POL_INTERNACAO;
	}

	public Boolean getUsuarioAdministrativo() {
		return acessoAdminPOL;
	}

	public InternacaoVO getInternacaoVO() {
		return internacaoVO;
	}

	public void setInternacaoVO(InternacaoVO internacaoVO) {
		this.internacaoVO = internacaoVO;
	}

	public Boolean getModuloPrescricaoEnfermagemAtivo() {
		return moduloPrescricaoEnfermagemAtivo;
	}

	public void setModuloPrescricaoEnfermagemAtivo(
			Boolean moduloPrescricaoEnfermagemAtivo) {
		this.moduloPrescricaoEnfermagemAtivo = moduloPrescricaoEnfermagemAtivo;
	}

	/**
	 * Verifica se o botão Ananmese deverá estar habilitado.
	 * 
	 * @return
	 */
	public Boolean habilitarBotaoAnanmese() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacaoVO() != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioAnamneses(getInternacaoVO()
							.getAtdSeq());
		}
		return habilitar;
	}

	/**
	 * Verifica se o botão Evolução deverá estar habilitado.
	 * 
	 * @return
	 */
	public Boolean habilitarBotaoEvolucao() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacaoVO() != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioEvolucao(getInternacaoVO()
							.getAtdSeq());
		}
		return habilitar;
	}

	/**
	 * Acionado pelo botão Anamnese na tela consultaDetalhesInternacao
	 * 
	 * @return
	 */
	public String pesquisarAnamnese() {
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaDetalheInternacao.RELATORIO_ANAMNESES.toString());
		return gerarRelatorioAnaEvoInternacao();
	}

	public String pesquisarEvolucaoPeriodo() {

		String retorno = null;
		boolean dataNula = false;
		if (getDataInicial() == null) {
			this.apresentarMsgNegocio("dataInicio", Severity.ERROR,
					"RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA",
					"Data Inicial");

			dataNula = true;
		}
		if (getDataFinal() == null) {
			this.apresentarMsgNegocio("dataFim", Severity.ERROR,
					"RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA", "Data Final");
			dataNula = true;
		}

		if(getDataInicial() != null && getDataFinal() != null){
			if (getDataInicial().after(getDataFinal())) {
				apresentarMsgNegocio(Severity.ERROR,
						"RELATORIO_EVOLUCAO_PERIODO_DATA_FINAL_MENOR_DATA_INICIAL");
	
				return null;
			}
		}

		if (dataNula) {
			return null;
		}
		if (DateUtil.calcularDiasEntreDatas(getDataInicial(), getDataFinal()) > 30) {
			apresentarMsgNegocio(Severity.ERROR,
					"RELATORIO_EVOLUCAO_PERIODO_MAIOR_30_DIAS");
			return null;
		}
		relatorioAnaEvoInternacaoController.setDataInicial(getDataInicial());
		relatorioAnaEvoInternacaoController.setDataFinal(getDataFinal());
		relatorioAnaEvoInternacaoController
				.setTipoRelatorio(EnumTargetConsultaDetalheInternacao.RELATORIO_EVOLUCAO_PERIODO
						.toString());

		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		gerarRelatorioAnaEvoInternacao(servidorLogado);

		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			retorno = AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO;
		}

		return retorno;
	}

	private void gerarRelatorioAnaEvoInternacao(RapServidores servidorLogado) {

		relatorioAnaEvoInternacaoController.setSeqInternacao(seq);
		relatorioAnaEvoInternacaoController.setTipoInternacao(tipo);

		relatorioAnaEvoInternacaoController
				.setOrigem(POL_DETALHE_INTERNACAO);
		relatorioAnaEvoInternacaoController.setAtdSeq(getInternacaoVO()
				.getAtdSeq());

		relatorioAnaEvoInternacaoController.gerarDados();

	}
	
	
	private String gerarRelatorioAnaEvoInternacao() {
		String retorno = "";
		relatorioAnaEvoInternacaoController.setOrigem(POL_DETALHE_INTERNACAO);
		relatorioAnaEvoInternacaoController.setSeqInternacao(seq);
		relatorioAnaEvoInternacaoController.setTipoInternacao(tipo);

		relatorioAnaEvoInternacaoController.setAtdSeq(getInternacaoVO().getAtdSeq());

		relatorioAnaEvoInternacaoController.gerarDados();
		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			retorno = "ambulatorio-relatorioAmamneseEvolucaoInternacao";
		}

		return retorno;
	}
	
	
	/**
	 * Acionado pelo botão Visualizar Todos na tela consultaInternacoesPOL
	 */
	public void pesquisarEvolucaoTodos() {

		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			relatorioAnaEvoInternacaoController.imprimirRelatorioEvolucaoTodos(
					getInternacaoVO().getAtdSeq(), servidorLogado,
					this.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Evoluções");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Verifica se o botao exame fisicoRN deverá ser habilitado.
	 */
	public Boolean habilitarExameFisicoRecemNascido() throws ApplicationBusinessException {
		Boolean retorno = Boolean.FALSE;
		if (seq != null) {
			if (getInternacaoVO() != null
					&& getInternacaoVO().getNumeroConsulta() != null
					&& getInternacaoVO().getCodigoPaciente() != null
					&& prontuarioOnlineFacade.habilitarBotaoExameFisico(
							DominioOrigemAtendimento.I, getInternacaoVO()
									.getCodigoPaciente(), getInternacaoVO()
									.getGsoSeqp())) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}
	
	public String obterRetornoRelatorioRN(){
		
		relatorioExameFisicoRNController.setPacCodigo(getInternacaoVO().getCodigoPaciente());
		relatorioExameFisicoRNController.setGsoSeqp(getInternacaoVO().getGsoSeqp());
		relatorioExameFisicoRNController.setSeqp(recemNascSel.getId().getSeqp());
		relatorioExameFisicoRNController.setConNumero(getInternacaoVO().getNumeroConsulta());
		relatorioExameFisicoRNController.setIndImpPrevia(indImpPrevia);
		relatorioExameFisicoRNController.setVoltarPara(POL_DETALHE_INTERNACAO);
		
		return RELATORIO_EXAME_FISICO_RN;
	}
	
	public String visualizarRelatorioExameFisicoRecemNascido() {
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(getInternacaoVO().getAtdSeq(),
																											  DominioTipoDocumento.EF);
		if (documento != null) {
			setAghVersaoDocumento(documento);
			return obterRetornoRelatorioRN();
		}

		recemNasc = prontuarioOnlineFacade.listarSeqpRecemNascido(internacaoVO.getCodigoPaciente(),internacaoVO.getNumeroConsulta());
		
		if (recemNasc != null && recemNasc.size() == 1) {
			recemNascSel = recemNasc.get(0);
			return obterRetornoRelatorioRN();
			
		} else {
			openDialog("modalExameFisicoRecemNascidoWG");
			recemNascSel = null;
			return null;
		}
	}

	/**
	 * Verifica se o botao Nascimento(RN) deverá ser habilitado.
	 */
	public Boolean habilitarBotaoNascimento() {
		Boolean ret = false;
		if (habilitarBotaoPadrao()) {
			ret = prontuarioOnlineFacade
					.habilitarBotaoNascimentoComAtendimento(getInternacaoVO()
							.getCodigoPaciente(), getInternacaoVO()
							.getNumeroConsulta(), getInternacaoVO()
							.getGsoSeqp());
		}
		return ret;
	}

	/**
	 * Verifica se o botao(botão padrão) deverá ser habilitado.
	 * 
	 * @return
	 */
	private Boolean habilitarBotaoPadrao() {
		Boolean retorno = Boolean.FALSE;
		if (getInternacaoVO() != null) {
			if (acessoAdminPOL == false) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}

	public void obterInternacaoVOVerificarAcaoPermitida() {
		try {
			recemNasc = new ArrayList<McoRecemNascidos>();
			if (seq != null) {
				setInternacaoVO(prontuarioOnlineFacade.obterInternacao(
						seq, tipo));
				carregaRecemNascido();
			} else {
				setInternacaoVO(null);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			setInternacaoVO(null);
		}
	}

	private void carregaRecemNascido() {
		if (getInternacaoVO().getCodigoPaciente() != null && getInternacaoVO().getGsoSeqp() != null) {
			recemNasc = emergenciaFacade
					.pesquisarMcoRecemNascidoPorGestacaoOrdenado(getInternacaoVO()
							.getCodigoPaciente(), getInternacaoVO().getGsoSeqp());
			
			if (recemNasc != null) {
				if (recemNasc.size() > 1) {
					exibirModalGestacao = Boolean.TRUE;
				} else if (recemNasc.size() == 1) {
					recemNascSel = recemNasc.get(0);
					exibirModalGestacao = Boolean.FALSE;
				}
			}
		} 
	}

	public String abrirRelatorioNascimento() throws ApplicationBusinessException {
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ARN);
		if (retorno == null) {
			relatorioAtendimentoRNController.setPacCodigo(getInternacaoVO().getCodigoPaciente());
			relatorioAtendimentoRNController.setConNumero(getInternacaoVO().getNumeroConsulta());
			relatorioAtendimentoRNController.setGsoSeqp(getInternacaoVO().getGsoSeqp());
			relatorioAtendimentoRNController.setAtdSeq(getInternacaoVO().getAtdSeq());
			if (recemNascSel!=null){
				relatorioAtendimentoRNController.setRnaSeqp(recemNascSel.getId().getSeqp());
			}	
			relatorioAtendimentoRNController.setSeqInternacao(seq);
			relatorioAtendimentoRNController.setTipoInternacao(tipo);
			relatorioAtendimentoRNController.gerarDados();
			relatorioAtendimentoRNController.setOrigem(POL_DETALHE_INTERNACAO);
			retorno = RELATORIO_ATENDIMENTO_RECEM_NASCIDO;
		}
		return retorno;
	}

	private String obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento tipoDocumento) {
		
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(getInternacaoVO().getAtdSeq(), tipoDocumento);
		if (documento != null) {
			setAghVersaoDocumento(documento);
			visualizarDocumentoController.setOrigem(POL_DETALHE_INTERNACAO);
			visualizarDocumentoController.setSeqAghVersaoDocumento(documento.getSeq());
			
			return VISUALIZAR_DOCUMENTO_ASSINADO_POL;
		} else {
			return null;
		}
	}

	public Boolean habilitarComboCTIUTI() {
		return !acessoAdminPOL && hasPIM2FichaApache();
	}

	private boolean hasPIM2FichaApache() {
		if (getInternacaoVO() != null) {

			List<DominioSituacaoPim2> situacoes = new ArrayList<DominioSituacaoPim2>();
			situacoes.add(DominioSituacaoPim2.A);
			situacoes.add(DominioSituacaoPim2.E);

			List<MpmPim2> pim2 = prescricaoMedicaFacade
					.pesquisarPim2PorAtendimentoSituacao(getInternacaoVO()
							.getAtdSeq(), situacoes);

			List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade
					.pesquisarFichasApachePorAtendimento(getInternacaoVO()
							.getAtdSeq());

			return (pim2 != null && pim2.size() > 0)
					|| (!listaFichaApache.isEmpty());
		} else {
			return false;
		}
	}

	public void selecionarCTIUTI() {
		if (itemSelecionado != -1) {
			List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade.pesquisarFichasApachePorAtendimento(getInternacaoVO().getAtdSeq());
			if (!listaFichaApache.isEmpty()) { // se for ficha apache
				visualizacaoFichaApacheController.setAtdSeq(getInternacaoVO().getAtdSeq());
				visualizacaoFichaApacheController.setSeqp(itemSelecionado.shortValue());
				visualizacaoFichaApacheController.setVoltarPara(POL_DETALHE_INTERNACAO);
				setItemSelecionado(null);
				visualizacaoFichaApacheController.inicio();
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("visualizacaoFichaApache.xhtml?cid="+super.conversation.getId());
				} catch (IOException e) {
					LOG.error("A página visualizacaoFichaApache.xhtml não foi encontrada: ", e);
				}
			} else {
				if (itemSelecionado != -1) {
					relatorioPIM2Controller.setSeqPim2(itemSelecionado);
					relatorioPIM2Controller.gerarDados();
					relatorioPIM2Controller.setOrigem(POL_DETALHE_INTERNACAO);
					setItemSelecionado(null);
					try {
						FacesContext.getCurrentInstance().getExternalContext().redirect("relatorioPIM2Pdf.xhtml?cid="+super.conversation.getId());
					} catch (IOException e) {
						LOG.error("A página relatorioPIM2Pdf.xhtml não foi encontrada: ", e);
					}
				}
			}
		}
	}

	public List<SelectItem> listarCTIUTIP() {

		List<SelectItem> result = new ArrayList<SelectItem>();

		if (getInternacaoVO() != null && getInternacaoVO().getAtdSeq() != null) {

			List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade
					.pesquisarFichasApachePorAtendimento(getInternacaoVO()
							.getAtdSeq());

			if (!listaFichaApache.isEmpty()) {
				SelectItem item = new SelectItem();
				item.setDescription("APACHE");
				item.setLabel("APACHE");
				item.setValue(-1L);
				result.add(item);

				for (MpmFichaApache fichaApache : listaFichaApache) {
					item = new SelectItem();
					item.setDescription(DateUtil.dataToString(
							fichaApache.getDthrIngressoUnidade(),
							"dd/MM/yyyy HH:mm"));
					item.setLabel(item.getDescription());
					item.setValue(fichaApache.getId().getSeqp());
					result.add(item);
				}
			} else {
				if (getInternacaoVO() != null
						&& getInternacaoVO().getAtdSeq() != null) {
					SelectItem item = new SelectItem();
					item.setDescription("PIM2");
					item.setLabel("PIM2");
					item.setValue(-1L);
					result.add(item);
					List<DominioSituacaoPim2> situacoes = new ArrayList<DominioSituacaoPim2>();
					situacoes.add(DominioSituacaoPim2.A);
					situacoes.add(DominioSituacaoPim2.E);
					List<MpmPim2> pim2 = prescricaoMedicaFacade
							.pesquisarPim2PorAtendimentoSituacao(
									getInternacaoVO().getAtdSeq(), situacoes);
					for (MpmPim2 mpmPim2 : pim2) {
						item = new SelectItem();
						item.setDescription(DateUtil.dataToString(
								mpmPim2.getDthrIngressoUnidade(),
								"dd/MM/yyyy HH:mm"));
						item.setLabel(item.getDescription());// Precisa ser
																// definido,
																// senao dá
																// nullpointer
						item.setValue(mpmPim2.getSeq());
						result.add(item);
					}
				}
			}
		}

		return result;
	}


	public String prepararRelatorioControles(){
		
		try {
			InternacaoVO internacaoVORel = (InternacaoVO) BeanUtils.cloneBean(internacaoVO);
			relatorioRegistrosControlesPacienteController.setInternacao(internacaoVORel);
			relatorioRegistrosControlesPacienteController.setVoltarParaPolDetalhesInternacoes(true);
			relatorioRegistrosControlesPacienteController.montaIntervaloPesquisa();
			openDialog("i_controlesPaciente_modalWG");
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CLONAR_VO");
		}
		return null;
	}
	public String visualizarDiretoRelatorioExameFisicoRecemNascido() {
		return obterRetornoRelatorioRN();
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	

	public AghVersaoDocumento getAghVersaoDocumento() {
		return aghVersaoDocumento;
	}

	public void setAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento) {
		this.aghVersaoDocumento = aghVersaoDocumento;
	}

	public void setIndImpPrevia(String indImpPrevia) {
		this.indImpPrevia = indImpPrevia;
	}

	public String getIndImpPrevia() {
		return indImpPrevia;
	}

	public void cleanParamRecemNascido() {
		recemNascSel=null;
	}

	public Boolean getExibirModalGestacao() {
		return exibirModalGestacao;
	}

	public void setExibirModalGestacao(Boolean exibirModalGestacao) {
		this.exibirModalGestacao = exibirModalGestacao;
	}

	public List<McoRecemNascidos> getRecemNasc() {
		return recemNasc;
	}

	public void setRecemNasc(List<McoRecemNascidos> recemNasc) {
		this.recemNasc = recemNasc;
	}

	public Boolean habilitarBotaoVisualizar() {
		return recemNascSel != null;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public void setBtVoltar(Boolean btVoltar) {
		this.btVoltar = btVoltar;
	}

	public Boolean getBtVoltar() {
		return btVoltar;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public Long getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(Long itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isFromConsultarInternacoes() {
		return fromConsultarInternacoes;
	}

	public void setFromConsultarInternacoes(boolean fromConsultarInternacoes) {
		this.fromConsultarInternacoes = fromConsultarInternacoes;
	}

	public McoRecemNascidos getRecemNascSel() {
		return recemNascSel;
	}

	public void setRecemNascSel(McoRecemNascidos recemNascSel) {
		this.recemNascSel = recemNascSel;
	}

}
