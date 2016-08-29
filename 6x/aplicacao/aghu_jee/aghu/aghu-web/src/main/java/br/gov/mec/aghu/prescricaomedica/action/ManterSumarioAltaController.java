package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.SumarioAltaDiagnosticosCidVOComparator;
import br.gov.mec.aghu.prescricaomedica.vo.AltaEvolucaoEstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import net.sf.jasperreports.engine.JRException;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ManterSumarioAltaController extends ActionController  {

	private static final String PAGE_AMBULATORIO_MANTER_ATESTADOS = "prescricaomedica-manterAtestados";

	private static final long serialVersionUID = 5394941367206293549L;

	private enum ManterSumarioAltaControllerExceptionCode implements BusinessExceptionCode {
		MSG_INCLUSAO_MOTIVO_INTERNACAO, 
		MSG_INCLUSAO_DIAGNOSTICO_PRINCIPAL, 
		MSG_INCLUSAO_DIAGNOSTICO_SECUNDARIO,
		MSG_ALTERACAO_MOTIVO_INTERNACAO, 
		MSG_ALTERACAO_DIAGNOSTICO_PRINCIPAL, 
		MSG_ALTERACAO_DIAGNOSTICO_SECUNDARIO,
		MSG_EXCLUSAO_MOTIVO_INTERNACAO, 
		MSG_EXCLUSAO_DIAGNOSTICO_PRINCIPAL, 
		MSG_EXCLUSAO_DIAGNOSTICO_SECUNDARIO,
		USUARIO_SEM_REGISTRO;
	}

	private final static String ANTECIPAR_SUMARIO = "ANTECIPAR SUMARIO";
	
	private final static String ALTA = "ALTA";

//	private static final String TAB_DIAG_0 = "tabDiag_0";
//	private static final String TAB_DIAG_1 = "tabDiag_1";
//	private static final String TAB_DIAG_2 = "tabDiag_2";

	private SumarioAltaDiagnosticosCidVO itemMotivosInternacaoSelecionado;
	private SumarioAltaDiagnosticosCidVO itemDiagnosticoPrincipalSelecionado;
	private SumarioAltaDiagnosticosCidVO itemDiagnosticoSecundarioSelecionado;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private ManterSumarioAltaProcedimentosController procedimentosController;
	@Inject
	private ManterSumarioAltaPosAltaController sumarioAltaPosAltaController;
	@Inject
	private RelatorioSumarioAltaController relatorioSumarioAltaController;
	@Inject
	private ManterSumarioAltaConclusaoController manterSumarioAltaConclusaoController;
	@Inject
	private ManterSumarioAltaSeguimentoAtendimentoController sumarioAltaSeguimentoAtendimentoController;
	@Inject
	private ManterSumarioAltaConclusaoController manterSumarioAltaConclusao;
	@Inject
	private ManterSumarioAltaInformacoesObitoController manterSumarioAltaInformacoesObitoController;
	@Inject
	private ManterSumarioAltaPosAltaController manterSumarioAltaPosAltaController;
	@Inject
	private ManterReceitaController manterReceitaController;
	
	//TODO:
	//private final String PAGE_LISTAR_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";
	//private final String PAGE_LANCAR_ITENS_CONTA_HOSPITALAR = "faturamento-lancarItensContaHospitalarList";
	private final String PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO = "pesquisaCidCapituloDiagnostico";
	//private final String PAGE_VERIFICAR_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	//private final String PAGE_MANTER_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	//private final String PAGE_MANTER_SUMARIO_ALTA_RECEITAS = "prescricaomedica-manterSumarioAltaReceitas";
	
	private String prontuario, responsavel, leito, nome, idade, unidade;
	private MpmAltaSumario altaSumario;
	private List<FatSituacaoSaidaPaciente> listaEstadosClinicos;
	private Integer pacCodigo;
	/**
	 * Seq de AGH_ATENDIMENTOS passado por parâmetro
	 */
	private Integer altanAtdSeq;
	/**
	 * Seq de AGH_ATENDIMENTO_PACIENTES passado por parâmetro
	 */
	private Integer altanApaSeq;
	/**
	 * Seqp de mpm_alta_sumarios
	 */
	private Short altanAsuSeqp;
	/**
	 * Verifica se é ANTECIPAR SUMARIO ou ALTA
	 */
	private String altanListaOrigem;
	/**
	 * Retorno
	 */
	private String voltarPara;
	/**
	 * VO contendo os dados de identificação
	 */
	private AltaSumarioVO altaSumarioVO;
	
	private AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoPaciente = new AltaEvolucaoEstadoPacienteVO();
	/**
	 * VO para controlar os itens do slider "Motivos Internação".
	 */
	private SumarioAltaDiagnosticosVO motivosInternacaoVO;
	/**
	 * VO para controlar os itens do slider "Diagnóstico Principal".
	 */
	private SumarioAltaDiagnosticosVO diagnosticoPrincipalVO;
	/**
	 * VO para controlar os itens do slider "Diagnósticos Secundários".
	 */
	private SumarioAltaDiagnosticosVO diagnosticosSecundariosVO;
	/**
	 * Cid recebido por parâmetro
	 */
	private Integer cidSeqPesquisado;
	
	private Boolean voltarCidSeqPesquisado = true;
	/**
	 * Slider corrente
	 */
	private Integer currentSlider = 0;
	/**
	 * Slider corrente da aba diagnósticos
	 */
	private Integer currentSliderDiagnosticos;
	/**
	 * Flag para controle da apresentacao da aba Atestado
	 */
	private Boolean renderAtestado = false;	
	private Boolean mostrarPanelComboMotivo;
	private Boolean mostrarPanelOutrosMotivos;
	private Boolean mostrarPanelComboDiagnostico;
	private Boolean mostrarPanelOutrosDiagnosticos;
	private Boolean mostrarPanelComboDiagSecundario;
	private Boolean mostrarPanelOutrosDiagSecundarios;

	private Integer togglePanelMotivosInternacao = -1;
	private Integer togglePanelDiagnosticoPrincipal = -1;
	private Integer togglePanelDiagnosticosSecundarios = -1;
	private Integer togglePanelCirurgiasRealizadas = -1;
	private Integer togglePanelProcedimentos = -1;
	private Integer togglePanelOutrosProcedimentos = -1;
	private Integer togglePanelConsultorias = -1;
	private Integer togglePanelPrincipaisFarmacos = -1;
	private Integer togglePanelInformacoesComplementares = -1;
	private Integer togglePanelMotivoAlta = -1;
	private Integer togglePanelPosAlta = -1;
	private Integer togglePanelRecomendacaoAltaCadastrada = -1;
	private Integer togglePanelAltaItemPrescricao = -1;
	private Integer togglePanelAltaNaoCadastrada = -1;
	private Integer togglePanelReceitaGeral = -1;
	private Integer togglePanelReceitaEspecial = -1;
	
	private static final String RELATORIO_SUMARIO_ALTA_PDF = "manterSumarioAltaConclusao";
	
	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;
	
	public void acaoOutrosMotivos() {
		this.setMostrarPanelOutrosMotivos(!this.getMostrarPanelOutrosMotivos());
	}
	
	public void acaoOutrosDiagnosticos() {
		this.setMostrarPanelOutrosDiagnosticos(!this.getMostrarPanelOutrosDiagnosticos());
	}
	
	public void acaoOutrosDiagSecundarios() {
		this.setMostrarPanelOutrosDiagSecundarios(!this.getMostrarPanelOutrosDiagSecundarios());
	}
	/**
	 * Flag responsável pelo controle da aba seguimento do atendimento
	 */
	private Boolean mostrarSeguimentoAtendimento = false;
	/**
	 * Flag responsável pelo controle do retorno da tela Recomendações 
	 */
	private boolean retornoRecomendacoesAlta = false;
	private boolean retornoTelaReceitas = false;
	private boolean primeiraVezExecutouSumarioAlta = true;
	
	private boolean gravouEvo;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	/**
	 * 	Inicializa variaveis da controller.
	 * @throws BaseException 
	 */
	private void initController() throws BaseException {
		this.setMostrarSeguimentoAtendimento(false);
		this.retornoRecomendacoesAlta = false;
		this.retornoTelaReceitas = false;
		primeiraVezExecutouSumarioAlta = false;
		
		final Boolean existeAmbulatorio = this.prescricaoMedicaFacade.existeAmbulatorio();
		
		if (existeAmbulatorio) {
			setMostrarSeguimentoAtendimento(Boolean.TRUE);
		}
		
		if (this.pesquisaCidRetorno != null ) {
			PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
			if (pesquisaCidVO.getCid() !=null) {
			    this.cidSeqPesquisado = pesquisaCidVO.getCid().getSeq();
			}					
		}
	}

	/**
	 * Método de entrada do sumário de alta.<br>
	 * Chamado apenas uma vez na requisicao inicial da pagina.
	 */
	public String inicio() {
	 
		try {
			Boolean executaBuscaMedicamentosPosAlta = primeiraVezExecutouSumarioAlta || retornoTelaReceitas;
			
			this.altanApaSeq = this.prescricaoMedicaFacade
					.recuperarAtendimentoPaciente(this.altanAtdSeq);
			
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogadoSemCache();
			
			// Verifica se o usuário que está logado tem registro no CRM
			// Somente com esse registro o usuário poderá acessar as telas
			// de sumário de alta e óbito.
			if (!ANTECIPAR_SUMARIO.equals(altanListaOrigem) && 
					!prescricaoMedicaFacade.verificarServidorMedico(
							servidorLogado.getId().getMatricula(),
							servidorLogado.getId().getVinCodigo())){
				
				this.apresentarExcecaoNegocio(new ApplicationBusinessException(
						ManterSumarioAltaControllerExceptionCode.USUARIO_SEM_REGISTRO,
						servidorLogado.getPessoaFisica().getNome()));
				
				return this.voltarPara();
			}
			
			pacCodigo = this.pacienteFacade.recuperarCodigoPaciente(this.altanAtdSeq);
			altaSumario = this.prescricaoMedicaFacade.recuperarSumarioAlta(this.altanAtdSeq, this.altanApaSeq);

			// Essa verificação ocorre para saber se o usuário não acessou
			// a arvore de CIDs na Aba Diagonósticos Botão Pesquisar CIDs
			// Para que no caso de estar retornando desta tela o sumário
			// não seja versionado novamente.
			if (this.cidSeqPesquisado == null && voltarCidSeqPesquisado) {
				
				if ((altaSumario == null) || ((altaSumario != null) && this.possuiEmergencia(this.altanAtdSeq, this.altanApaSeq, altaSumario.getId().getSeqp()))) {
					altaSumario = this.prescricaoMedicaFacade.gerarAltaSumario(this.altanAtdSeq, this.altanApaSeq, this.altanListaOrigem);
				
				} else {
					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						nomeMicrocomputador = null;
					}
				
					//Verifica se está retornando da tela de cadastro de recomendações na alta ou receitas, para não versionar novamente
					if(!retornoRecomendacoesAlta && !retornoTelaReceitas) {
						altaSumario = this.prescricaoMedicaFacade.versionarAltaSumario(altaSumario, this.altanListaOrigem, nomeMicrocomputador);
					}
					if(retornoTelaReceitas){
						this.manterSumarioAltaPosAltaController.obterListaMedicamentos(altaSumario, true);
					}
				}
				if(altaSumario.getId() != null && altaSumario.getId().getApaSeq()!=null){
					this.altanApaSeq = altaSumario.getId().getApaSeq();
				}
				this.prescricaoMedicaFacade.gerarAltaOtrProcedimento(altaSumario);
				this.prescricaoMedicaFacade.gerarAltaOutraEquipeSumr(altaSumario);
				this.prescricaoMedicaFacade.gerarAltaCirgRealizada(altaSumario, pacCodigo);
				this.prescricaoMedicaFacade.gerarAltaConsultoria(altaSumario);
				
			}
			this.initController();
			this.altaSumarioVO = null;
			this.renderTela();
			
			if(executaBuscaMedicamentosPosAlta){
				this.sumarioAltaPosAltaController.setAtdSeq(altanAtdSeq);
				this.manterSumarioAltaPosAltaController.obterListaMedicamentos(altaSumario, true);
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}	
		return null;
	
	}
	
    public boolean validaAltanListaOrigemAlta() {
        return "ALTA".equalsIgnoreCase(altanListaOrigem);
    }
	
//	public void onTabChangeDiagnostico(TabChangeEvent event) {
//		if(event != null && event.getTab() != null) {
//			if(TAB_DIAG_0.equals(event.getTab().getId())) {
//				this.currentSliderDiagnosticos = 0;
//			} else if(TAB_DIAG_1.equals(event.getTab().getId())) {
//				this.currentSliderDiagnosticos = 1;
//			} else if(TAB_DIAG_2.equals(event.getTab().getId())) {
//				this.currentSliderDiagnosticos = 2;
//			}
//		}
//	}
	
	public Boolean getExibirBotaoConcluir(){
		return !ANTECIPAR_SUMARIO.equals(altanListaOrigem);
	}
	
	public String carregarDadosConclusao() {
		try {
			manterSumarioAltaConclusao.renderConclusao(this.altaSumario, this.altanListaOrigem, this.voltarPara, true);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return RELATORIO_SUMARIO_ALTA_PDF;
	}
	
	public String concluirSumario() throws JRException, SystemException, IOException {
		String returnValue = null;
		try {
			manterSumarioAltaConclusao.renderConclusao(this.altaSumario, this.altanListaOrigem, this.voltarPara, false);
			returnValue = manterSumarioAltaConclusao.concluirSumarioAlta();
			if (returnValue != null ) {
				apresentarMsgNegocio("SUCESSO_CONCLUIR_SUMARIO_ALTA");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return returnValue;
	}

	public void renderTela() {
		try {
			if (this.altaSumario == null) {
				throw new IllegalStateException("Este metodo foi chamado em um momento invalido da controller.");
			}
			// IDENTIFICACAO:
			this.renderIdentificacao(altaSumario);
			// DIAGNOSTICOS:
			this.renderDiagnostico(altaSumario);
			// PROCEDIMENTOS:
			this.procedimentosController.renderProcedimentos(this.altaSumario);
			// EVOLUCAO:
			this.renderEvolucao(altaSumario);
			// POS_ALTA:
			this.sumarioAltaPosAltaController.setAtdSeq(altanAtdSeq);
			this.sumarioAltaPosAltaController.renderPosAlta(altaSumario);
			// SEGUIMENTO_ATENDIMENTO:
			if (this.mostrarSeguimentoAtendimento) {
				this.sumarioAltaSeguimentoAtendimentoController.renderSeguimentoAtendimento(altaSumario);
			}
			// INFORMACOES_OBITO:
			this.manterSumarioAltaInformacoesObitoController.renderInformacoesObito(altaSumario, null);

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * @param altaSumario2
	 */
	private void renderDiagnostico(MpmAltaSumario altaSumario2) throws ApplicationBusinessException {
		this.inicializaDadosDiagnosticos(altaSumario.getId());
		this.restaurarDadosDiagnostigos();
	}

	/**
	 * Executa a carga de dados necessaria para a apresentacao da aba.<br>
	 * Chamado toda a vez que ocorrer um clique na aba de Identificacao da tela Sumario de Alta.<br>
	 * 
	 * @param as
	 */
	public void renderIdentificacao(MpmAltaSumario as) throws BaseException {
		if (this.altaSumarioVO == null) {
			this.altaSumarioVO = this.prescricaoMedicaFacade.populaAltaSumarioVO(as, this.altanListaOrigem);
		}
	}
	
	public void listarEstadoClinicoPacienteAtivos() {
		DominioSexoDeterminante sexo = DominioSexoDeterminante.Q;
		if (altaSumario.getSexo().equals(DominioSexo.M)) {
			sexo = DominioSexoDeterminante.M;
		} else {
			sexo = DominioSexoDeterminante.F;
		}
		if(ALTA.equals(altanListaOrigem)) {
			this.listaEstadosClinicos = prescricaoMedicaFacade.obterListaEstadosClinicos(altaSumario.getIdadeAnos(), sexo);
			
		} else {
			this.listaEstadosClinicos = prescricaoMedicaFacade.listarEstadoClinicoPacienteObitoAtivos(altaSumario.getIdadeAnos(), sexo);
		}
	}
		
	/**
	 * Busca as informacoes pra o preenchimento da tela de Sumario de alta<br>
	 *
	 * @param altaSumario <code>MpmAltaSumario</code>
	 * @throws ApplicationBusinessException
	 */
	public void renderEvolucao(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		listarEstadoClinicoPacienteAtivos();
		setAltaEvolucaoEstadoPaciente(prescricaoMedicaFacade.buscaAltaSumarioEvolucaoEstado(altaSumario));
	}

	private void inicializaDadosDiagnosticos(MpmAltaSumarioId id) throws ApplicationBusinessException {
		this.prepararListasMotivosInternacao(id);
		this.prepararListasDiagPrincipal(id);
		this.prepararListasDiagSecundarios(id);
	}

	private void prepararListasMotivosInternacao(MpmAltaSumarioId id) throws ApplicationBusinessException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();
		
		List<MpmAltaDiagMtvoInternacao> listaDiagMvtoInternacao = null;		
		listaDiagMvtoInternacao = this.prescricaoMedicaFacade.obterMpmAltaDiagMtvoInternacao(apaAtdSeq, apaSeq, seqp);
		

		List<SumarioAltaDiagnosticosCidVO> listaComboMotivo = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaComboMotivo = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		for(MpmAltaDiagMtvoInternacao diagMvtoInternacao: listaDiagMvtoInternacao) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSADiagCidVO(id, diagMvtoInternacao);
			listaGrid.add(itemVO);	
		}	
		listaComboMotivo.removeAll(listaGrid);
		Collections.sort(listaGrid, new SumarioAltaDiagnosticosCidVOComparator());
		
		// Marcar outros itens para exibicaoMotivos da Internação (Preenchimento obrigatório)
		boolean hasItemCombo = (!listaComboMotivo.isEmpty());
		this.setMostrarPanelOutrosMotivos( !hasItemCombo );
		this.setMostrarPanelComboMotivo(hasItemCombo);

		this.motivosInternacaoVO = new SumarioAltaDiagnosticosVO(id, null);
		this.motivosInternacaoVO.setListaCombo(listaComboMotivo);
		this.motivosInternacaoVO.setListaGrid(listaGrid);
		
		if(this.getItemMotivosInternacaoSelecionado() != null){
			this.getItemMotivosInternacaoSelecionado().setEmEdicao(Boolean.FALSE);
		}
		
	}

	private void prepararListasDiagPrincipal(MpmAltaSumarioId id) throws ApplicationBusinessException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		// Carrega combos de cada slider e remover itens duplicados da grid
		MpmAltaDiagPrincipal diagPrincipal = null;
		diagPrincipal = this.prescricaoMedicaFacade.obterAltaDiagPrincipal(apaAtdSeq, apaSeq, seqp);

		List<SumarioAltaDiagnosticosCidVO> listaComboDiag = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaComboDiag = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		if (diagPrincipal != null) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSADiagCidVO(id,diagPrincipal);
			listaGrid.add(itemVO);
			listaComboDiag.removeAll(listaGrid);
		}
		
		// Marcar outros itens para exibicao
		boolean hasItemCombo = (!listaComboDiag.isEmpty());
		this.setMostrarPanelOutrosDiagnosticos( !hasItemCombo );
		this.setMostrarPanelComboDiagnostico(hasItemCombo);

		this.diagnosticoPrincipalVO = new SumarioAltaDiagnosticosVO(id, 1);
		this.diagnosticoPrincipalVO.setListaCombo(listaComboDiag);
		this.diagnosticoPrincipalVO.setListaGrid(listaGrid);
		
		if(this.getItemDiagnosticoPrincipalSelecionado() != null){
			this.getItemDiagnosticoPrincipalSelecionado().setEmEdicao(Boolean.FALSE);
		}
	}

	private void prepararListasDiagSecundarios(MpmAltaSumarioId id) throws ApplicationBusinessException {
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		// Carrega combos de cada slider e remover itens duplicados da grid
		List<MpmAltaDiagSecundario> listaDiagSecundarios = null;
		listaDiagSecundarios = this.prescricaoMedicaFacade.obterAltaDiagSecundario(apaAtdSeq, apaSeq, seqp);

		List<SumarioAltaDiagnosticosCidVO> listaComboDiagSecundario = null;
		List<SumarioAltaDiagnosticosCidVO> listaGrid = new ArrayList<SumarioAltaDiagnosticosCidVO>();
		listaComboDiagSecundario = this.prescricaoMedicaFacade.pesquisarMotivosInternacaoCombo(id);

		for(MpmAltaDiagSecundario diagSecundario: listaDiagSecundarios) {
			SumarioAltaDiagnosticosCidVO itemVO = this.criarSADiagCidVO(id, diagSecundario);
			listaGrid.add(itemVO);
		}

		listaComboDiagSecundario.removeAll(listaGrid);
		Collections.sort(listaGrid, new SumarioAltaDiagnosticosCidVOComparator());
		
		// Marcar outros itens para exibicao
		boolean hasItemCombo = (!listaComboDiagSecundario.isEmpty());
		this.setMostrarPanelOutrosDiagSecundarios( !hasItemCombo );
		this.setMostrarPanelComboDiagSecundario(hasItemCombo);

		this.diagnosticosSecundariosVO = new SumarioAltaDiagnosticosVO(id, null);
		this.diagnosticosSecundariosVO.setListaCombo(listaComboDiagSecundario);
		this.diagnosticosSecundariosVO.setListaGrid(listaGrid);
		
		if(this.getItemDiagnosticoSecundarioSelecionado() != null){
			this.getItemDiagnosticoSecundarioSelecionado().setEmEdicao(Boolean.FALSE);
		}
	}

	private SumarioAltaDiagnosticosCidVO criarSADiagCidVO(MpmAltaSumarioId id, MpmAltaDiagMtvoInternacao diagMvtoInternacao) {
		Short seqp = diagMvtoInternacao.getId().getSeqp().shortValue();
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, seqp);

		if (diagMvtoInternacao.getCidAtendimento() != null) {
			itemVO.setCiaSeq(diagMvtoInternacao.getCidAtendimento().getSeq());
		}

		itemVO.setCid(diagMvtoInternacao.getCid());
		itemVO.setComplementoEditado(diagMvtoInternacao.getComplCid());

		if (diagMvtoInternacao.getDiagnostico() != null) {
			itemVO.setDiaSeq(diagMvtoInternacao.getDiagnostico().getSeq());
		}

		return itemVO;
	}

	private SumarioAltaDiagnosticosCidVO criarSADiagCidVO(MpmAltaSumarioId id, MpmAltaDiagPrincipal diagPrincipal) {
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, null);

		if (diagPrincipal.getCidAtendimento() != null) {
			itemVO.setCiaSeq(diagPrincipal.getCidAtendimento().getSeq());
		}

		itemVO.setCid(diagPrincipal.getCid());
		itemVO.setComplementoEditado(diagPrincipal.getComplCid());

		if (diagPrincipal.getDiagnostico() != null) {
			itemVO.setDiaSeq(diagPrincipal.getDiagnostico().getSeq());
		}

		return itemVO;
	}

	private SumarioAltaDiagnosticosCidVO criarSADiagCidVO(MpmAltaSumarioId id, MpmAltaDiagSecundario diagSecundario) {
		Short seqp = diagSecundario.getId().getSeqp();
		SumarioAltaDiagnosticosCidVO itemVO = new SumarioAltaDiagnosticosCidVO(id, seqp);

		if (diagSecundario.getMpmCidAtendimentos() != null) {
			itemVO.setCiaSeq(diagSecundario.getMpmCidAtendimentos().getSeq());
		}

		itemVO.setCid(diagSecundario.getCidSeq());
		itemVO.setComplementoEditado(diagSecundario.getComplCid());

		if (diagSecundario.getDiaSeq() != null) {
			itemVO.setDiaSeq(diagSecundario.getDiaSeq().getSeq());
		}

		return itemVO;
	}

	private void restaurarDadosDiagnostigos() {
		try {
			if (this.cidSeqPesquisado != null && this.currentSliderDiagnosticos!=null) {
				
				AghCid cidPorCapitulo = this.aghuFacade.obterCid(this.cidSeqPesquisado);
				this.cidSeqPesquisado = null;
				this.voltarCidSeqPesquisado=false;
				SumarioAltaDiagnosticosCidVO item = null;
				
				switch (this.currentSliderDiagnosticos) {
				case 0:
					item = this.motivosInternacaoVO.novoItem();
					item.setCid(cidPorCapitulo);
					this.prescricaoMedicaFacade.inserirAltaDiagMtvoInternacao(item);
					this.prepararListasMotivosInternacao(item.getId());
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_MOTIVO_INTERNACAO.toString());
					break;
				case 1:
					item = this.diagnosticoPrincipalVO.novoItem();
					item.setCid(cidPorCapitulo);
					boolean insert = this.prescricaoMedicaFacade.inserirAltaDiagPrincipal(item);
					this.prepararListasDiagPrincipal(item.getId());
					if(insert) {
						apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_PRINCIPAL.toString());
					} else {
						apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_DIAGNOSTICO_PRINCIPAL.toString());
					}
					break;
				case 2:
					item = this.diagnosticosSecundariosVO.novoItem();
					item.setCid(cidPorCapitulo);
					this.prescricaoMedicaFacade.inserirAltaDiagSecundario(item);
					this.prepararListasDiagSecundarios(item.getId());
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_SECUNDARIO.toString());
					break;
				}
				
			} else {
				this.currentSliderDiagnosticos = 0;
			}
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void moverDiagItemSelecionadoComboParaGrid(Integer currentSlider) {
		try {
			this.setCurrentSliderDiagnosticos(currentSlider);
			switch (this.currentSliderDiagnosticos) {
			case 0:
				this.prescricaoMedicaFacade.inserirAltaDiagMtvoInternacao(itemMotivosInternacaoSelecionado);
				this.prepararListasMotivosInternacao(this.motivosInternacaoVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_MOTIVO_INTERNACAO.toString());
				this.setItemMotivosInternacaoSelecionado(null);
				break;
			case 1:
				boolean insert = this.prescricaoMedicaFacade.inserirAltaDiagPrincipal(itemDiagnosticoPrincipalSelecionado);
				this.prepararListasDiagPrincipal(this.diagnosticoPrincipalVO.getId());
				if(insert) {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_PRINCIPAL.toString());
				} else {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_DIAGNOSTICO_PRINCIPAL.toString());
				}
				this.setItemDiagnosticoPrincipalSelecionado(null);
				break;
			case 2:
				this.prescricaoMedicaFacade.inserirAltaDiagSecundario(itemDiagnosticoSecundarioSelecionado);
				this.prepararListasDiagSecundarios(this.diagnosticosSecundariosVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_SECUNDARIO.toString());
				this.setItemDiagnosticoSecundarioSelecionado(null);
				break;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void gravarDiagItemEmEdicao() {
		try {
			switch (this.currentSliderDiagnosticos) {
			case 0:
				this.motivosInternacaoVO.validarItemEmEdicao();
				this.prescricaoMedicaFacade.inserirAltaDiagMtvoInternacao(this.motivosInternacaoVO.getItemEmEdicao());
				this.prepararListasMotivosInternacao(this.motivosInternacaoVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_MOTIVO_INTERNACAO.toString());
				this.setItemMotivosInternacaoSelecionado(null);
				break;
			case 1:
				this.diagnosticoPrincipalVO.validarItemEmEdicao();
				boolean insert = this.prescricaoMedicaFacade.inserirAltaDiagPrincipal(this.diagnosticoPrincipalVO.getItemEmEdicao());
				this.prepararListasDiagPrincipal(this.diagnosticoPrincipalVO.getId());
				if(insert) {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_PRINCIPAL.toString());
				} else {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_DIAGNOSTICO_PRINCIPAL.toString());
				}
				this.setItemDiagnosticoPrincipalSelecionado(null);
				break;
			case 2:
				this.diagnosticosSecundariosVO.validarItemEmEdicao();
				this.prescricaoMedicaFacade.inserirAltaDiagSecundario(this.diagnosticosSecundariosVO.getItemEmEdicao());
				this.prepararListasDiagSecundarios(this.diagnosticosSecundariosVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_SECUNDARIO.toString());
				this.setItemDiagnosticoSecundarioSelecionado(null);
				break;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void atualizarDiagItemEmEdicao() {
		try {
			switch (this.currentSliderDiagnosticos) {
			case 0:
				this.motivosInternacaoVO.validarItemEmEdicao();
				this.prescricaoMedicaFacade.atualizarMpmAltaDiagMtvoInternacao(this.motivosInternacaoVO.getItemEmEdicao());
				this.prepararListasMotivosInternacao(this.motivosInternacaoVO.getId());
				if(this.mostrarPanelComboMotivo){
					this.setMostrarPanelOutrosMotivos(false);
				}
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_MOTIVO_INTERNACAO.toString());
				this.setItemMotivosInternacaoSelecionado(null);
				break;
			case 1:
				this.diagnosticoPrincipalVO.validarItemEmEdicao();
				boolean insert = this.prescricaoMedicaFacade.inserirAltaDiagPrincipal(this.diagnosticoPrincipalVO.getItemEmEdicao());
				this.prepararListasDiagPrincipal(this.diagnosticoPrincipalVO.getId());
				if(this.mostrarPanelComboDiagnostico){
					this.setMostrarPanelOutrosDiagnosticos(false);
				}
				if(insert) {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_INCLUSAO_DIAGNOSTICO_PRINCIPAL.toString());
				} else {
					apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_DIAGNOSTICO_PRINCIPAL.toString());
				}
				this.setItemDiagnosticoPrincipalSelecionado(null);
				break;
			case 2:
				this.diagnosticosSecundariosVO.validarItemEmEdicao();
				this.prescricaoMedicaFacade.atualizarAltaDiagSecundario(this.diagnosticosSecundariosVO.getItemEmEdicao());
				this.prepararListasDiagSecundarios(this.diagnosticosSecundariosVO.getId());
				if(this.mostrarPanelComboDiagSecundario){
					this.setMostrarPanelOutrosDiagSecundarios(false);
				}
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_ALTERACAO_DIAGNOSTICO_SECUNDARIO.toString());
				this.setItemDiagnosticoSecundarioSelecionado(null);
				break;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void excluirDiagItemGrid() {
		try{
			switch (this.currentSliderDiagnosticos) {
			case 0:
				this.prescricaoMedicaFacade.removerAltaDiagMtvoInternacao(getItemMotivosInternacaoSelecionado());
				this.prepararListasMotivosInternacao(this.motivosInternacaoVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_EXCLUSAO_MOTIVO_INTERNACAO.toString());
				this.setItemMotivosInternacaoSelecionado(null);
				break;
			case 1:
				this.prescricaoMedicaFacade.removerAltaDiagPrincipal(getItemDiagnosticoPrincipalSelecionado());
				this.prepararListasDiagPrincipal(this.diagnosticoPrincipalVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_EXCLUSAO_DIAGNOSTICO_PRINCIPAL.toString());
				this.setItemDiagnosticoPrincipalSelecionado(null);
				break;
			case 2:
				this.prescricaoMedicaFacade.removerAltaDiagSecundario(getItemDiagnosticoSecundarioSelecionado());
				this.prepararListasDiagSecundarios(this.diagnosticosSecundariosVO.getId());
				apresentarMsgNegocio(Severity.INFO, ManterSumarioAltaControllerExceptionCode.MSG_EXCLUSAO_DIAGNOSTICO_SECUNDARIO.toString());
				this.setItemDiagnosticoSecundarioSelecionado(null);
				break;
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void editarItemDiagnostico() {
		
		switch (this.currentSliderDiagnosticos) {
		case 0:
			for (SumarioAltaDiagnosticosCidVO item : this.motivosInternacaoVO.getListaGrid()) {
				item.setEmEdicao(Boolean.FALSE);
			}
			getItemMotivosInternacaoSelecionado().setEmEdicao(Boolean.TRUE);
			this.motivosInternacaoVO.setCidSuggestion(getItemMotivosInternacaoSelecionado().getCid());
			this.motivosInternacaoVO.setComplementoCidSuggestion(getItemMotivosInternacaoSelecionado().getComplementoEditado());
			this.motivosInternacaoVO.setItemEmEdicao(getItemMotivosInternacaoSelecionado());		
			break;
		case 1:
			for (SumarioAltaDiagnosticosCidVO item : this.diagnosticoPrincipalVO.getListaGrid()) {
				item.setEmEdicao(Boolean.FALSE);
			}
			getItemDiagnosticoPrincipalSelecionado().setEmEdicao(Boolean.TRUE);
			this.diagnosticoPrincipalVO.setCidSuggestion(getItemDiagnosticoPrincipalSelecionado().getCid());
			this.diagnosticoPrincipalVO.setComplementoCidSuggestion(getItemDiagnosticoPrincipalSelecionado().getComplementoEditado());
			this.diagnosticoPrincipalVO.setItemEmEdicao(getItemDiagnosticoPrincipalSelecionado());
			break;
		case 2:
			for (SumarioAltaDiagnosticosCidVO item : this.diagnosticosSecundariosVO.getListaGrid()) {
				item.setEmEdicao(Boolean.FALSE);
			}
			getItemDiagnosticoSecundarioSelecionado().setEmEdicao(Boolean.TRUE);
			this.diagnosticosSecundariosVO.setCidSuggestion(getItemDiagnosticoSecundarioSelecionado().getCid());
			this.diagnosticosSecundariosVO.setComplementoCidSuggestion(getItemDiagnosticoSecundarioSelecionado().getComplementoEditado());
			this.diagnosticosSecundariosVO.setItemEmEdicao(getItemDiagnosticoSecundarioSelecionado());
			break;
		}
		
	}
		
	public void cancelarItemEmEdicao() {
		switch (this.currentSliderDiagnosticos) {
		case 0:
			this.getItemMotivosInternacaoSelecionado().setEmEdicao(Boolean.FALSE);
			this.motivosInternacaoVO.setCidSuggestion(null);
			this.motivosInternacaoVO.novoItem();
			this.setItemMotivosInternacaoSelecionado(null);
			this.motivosInternacaoVO.setComplementoCidSuggestion(null);
			break;
		case 1:
			this.getItemDiagnosticoPrincipalSelecionado().setEmEdicao(Boolean.FALSE);
			this.diagnosticoPrincipalVO.setCidSuggestion(null);
			this.diagnosticoPrincipalVO.novoItem();
			this.setItemDiagnosticoPrincipalSelecionado(null);
			this.diagnosticoPrincipalVO.setComplementoCidSuggestion(null);
			break;
		case 2:
			this.getItemDiagnosticoSecundarioSelecionado().setEmEdicao(Boolean.FALSE);
			this.diagnosticosSecundariosVO.setCidSuggestion(null);
			this.diagnosticosSecundariosVO.novoItem();
			this.diagnosticosSecundariosVO.setComplementoCidSuggestion(null);
			this.setItemDiagnosticoSecundarioSelecionado(null);
			break;
		}
		
	}

	/**
	 * Atualiza os campos Idade e Permanência de Manter Sumário Alta
	 */
	public void atualizarIdadePermanencia() {
		try {
			if (DominioIndTipoAltaSumarios.TRF.equals(altaSumario.getTipo())){
				this.prescricaoMedicaFacade.verificarDataTRF(this.altanAtdSeq, this.altaSumarioVO.getDataAlta());				
			}
			this.prescricaoMedicaFacade.atualizarIdade(this.altaSumarioVO);
			this.prescricaoMedicaFacade.atualizarDiasPermanencia(this.altaSumarioVO);

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Acao Gravar da aba de Identificacao do Sumario de Alta.
	 * 
	 */
	public void gravarIdentificacao() {
		try {
			MpmAltaSumarioId id = this.altaSumarioVO.getId();
			Short diasPermanencia = this.altaSumarioVO.getDiasPermanencia();
			Integer idadeDias = this.altaSumarioVO.getIdadeDias();
			Integer idadeMeses = this.altaSumarioVO.getIdadeMeses();
			Short idadeAnos = this.altaSumarioVO.getIdadeAnos();
			Date dataAlta = this.altaSumarioVO.getDataAlta();
			
			if(dataAlta	!=	null){
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					nomeMicrocomputador = null;
				}
				this.prescricaoMedicaFacade.atualizarAltaSumario(id, diasPermanencia, idadeDias, idadeMeses, idadeAnos, dataAlta, nomeMicrocomputador);
                this.altaSumario.setDthrAlta(dataAlta);
				apresentarMsgNegocio(Severity.INFO, "Data atualizada com sucesso.");
			}else{
				if (DominioIndTipoAltaSumarios.ALT.equals(altaSumario.getTipo())) {
					apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Data de Alta");
				}else if (DominioIndTipoAltaSumarios.OBT.equals(altaSumario.getTipo())) {
					apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Data Óbito");
				}else{
					apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Data");
				}
			}

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * VERIFICA SE O SUMÁRIO É DA EMERGENCIA
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param seqp
	 * @return
	 */
	private boolean possuiEmergencia(Integer altanAtdSeq, Integer altanApaSeq, Short seqp) {
		if (seqp != null) {
			try {
				return this.prescricaoMedicaFacade.verificarEmergencia(altanAtdSeq, altanApaSeq, seqp);
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return false;
	}

	/**
	 * Botão Cancelar
	 * @return
	 */
	public String cancelar() {
		if (StringUtils.isBlank(this.voltarPara)) {
			this.voltarPara = null;
		}
		
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				nomeMicrocomputador = null;
			}
			
			this.ambulatorioFacade.cancelarAtestadosAltaSumario(this.altaSumario.getId().getApaAtdSeq(), 
					this.altaSumario.getId().getApaSeq(), this.altaSumario.getId().getSeqp(), null);
			
			prescricaoMedicaFacade.cancelarSumario(this.altaSumario, nomeMicrocomputador);
			this.limparParametros();
			return this.voltarPara;
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			this.limparParametros();
			return this.voltarPara;
		}
	}
	
	/**
	 * Este método é executado pela ação do
	 * botão de confirmar da aba de conclusão
	 * quando o sumário alta for uma antecipação.
	 */
	public String voltarPara() {
		this.limparParametros();
		return this.voltarPara;
	}
	
	/**
	 * Limpa todos os parâmetros/atributos do sumário
	 */
	private void limparParametros() {
		/*
		 * Limpa parâmetros/atributos gerais do sumário
		 */
		this.altaSumario = null;
		this.pacCodigo = null;
		this.altanAtdSeq = null;
		this.altanApaSeq = null;
		this.altanAsuSeqp = null;
		this.altanListaOrigem = null;
		this.altaSumarioVO = null;
		this.altaEvolucaoEstadoPaciente = new AltaEvolucaoEstadoPacienteVO();
		this.motivosInternacaoVO = null;
		this.diagnosticoPrincipalVO = null;
		this.diagnosticosSecundariosVO = null;
		this.cidSeqPesquisado = null;
		this.voltarCidSeqPesquisado = true;
		this.currentSlider = null;
		this.currentSliderDiagnosticos = 0;
		this.renderAtestado = false;
		this.mostrarPanelComboMotivo = false;
		this.setMostrarPanelOutrosMotivos(false);
		this.mostrarPanelComboDiagnostico = false;
		this.mostrarPanelOutrosDiagnosticos = false;
		this.mostrarPanelComboDiagSecundario = false;
		this.mostrarPanelOutrosDiagSecundarios = false;
		this.retornoRecomendacoesAlta = false;
		this.retornoTelaReceitas = false;
		this.primeiraVezExecutouSumarioAlta = true;
		/*
		 * /Limpa parâmetros/atributos das controllers/telas filhas (relacionadas com as GUIAS)
		 */
		this.manterSumarioAltaConclusaoController.limparParametros(); // Limpa o relatório de conclusão parte 1
		this.relatorioSumarioAltaController.limparParametros(); // Limpa o relatório de conclusão parte 2
		this.manterSumarioAltaPosAltaController.limparParametros();
	}
	
	/**
	 * Acao gravar da aba de Evolucao da tela de Sumario de Alta
	 */
	public void gravarEvolucaoEstado() {
		try {
			this.setAltaEvolucaoEstadoPaciente(this.prescricaoMedicaFacade.gravarAltaSumarioEvolucaoEstado(this.getAltaEvolucaoEstadoPaciente(), this.altanListaOrigem));
			apresentarMsgNegocio(Severity.INFO, "Evolução do Paciente salva com sucesso.");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarEstadoPaciente(AjaxBehaviorEvent event) {
		try {
			this.setAltaEvolucaoEstadoPaciente(prescricaoMedicaFacade.gravarAltaSumarioEstado(this.getAltaEvolucaoEstadoPaciente(), this.altanListaOrigem));
			//apresentarMsgNegocio(Severity.INFO, "Estado do Paciente salvo com sucesso.");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public void gravarDescricaoEvo(AjaxBehaviorEvent event) {
		try {
			this.setAltaEvolucaoEstadoPaciente(prescricaoMedicaFacade.gravarAltaSumarioEvolucao(this.getAltaEvolucaoEstadoPaciente()));
			//apresentarMsgNegocio(Severity.INFO, "Evolução do Paciente salva com sucesso.");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AghCid> pesquisarCids(String param) {
		return aghuFacade.obterCidPorNomeCodigoAtivaPaginado(param);
	}
	
	public String chamarManterReceitas() {
		return "prescricaomedica-manterReceitas";
	}

	public String chamarAtestados() {
		return PAGE_AMBULATORIO_MANTER_ATESTADOS;
	}
	
	public String getProntuarioFormatado() {
		return this.getAltaSumario() != null ? CoreUtil.formataProntuario(this.getAltaSumario().getProntuario()) : null;
	}
	
	public AltaSumarioVO getAltaSumarioVO() {
		return this.altaSumarioVO;
	}

	public void setAltaSumarioVO(AltaSumarioVO altaSumarioVO) {
		this.altaSumarioVO = altaSumarioVO;
	}

	public Integer getAltanAtdSeq() {
		return this.altanAtdSeq;
	}

	public void setAltanAtdSeq(Integer altanAtdSeq) {
		this.altanAtdSeq = altanAtdSeq;
	}

	public Integer getAltanApaSeq() {
		return this.altanApaSeq;
	}

	public void setAltanApaSeq(Integer altanApaSeq) {
		this.altanApaSeq = altanApaSeq;
	}

	public Short getAltanAsuSeqp() {
		return this.altanAsuSeqp;
	}

	public void setAltanAsuSeqp(Short altanAsuSeqp) {
		this.altanAsuSeqp = altanAsuSeqp;
	}

	public String getAltanListaOrigem() {
		return this.altanListaOrigem;
	}

	public void setAltanListaOrigem(String altanListaOrigem) {
		this.altanListaOrigem = altanListaOrigem;
	}

	public String getVoltarPara() {
		return this.voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setAltaEvolucaoEstadoPaciente(AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoPaciente) {
		this.altaEvolucaoEstadoPaciente = altaEvolucaoEstadoPaciente;
	}

	public AltaEvolucaoEstadoPacienteVO getAltaEvolucaoEstadoPaciente() {
		return this.altaEvolucaoEstadoPaciente;
	}

	public SumarioAltaDiagnosticosVO getMotivosInternacaoVO() {
		return this.motivosInternacaoVO;
	}

	public void setMotivosInternacaoVO(SumarioAltaDiagnosticosVO motivosInternacaoVO) {
		this.motivosInternacaoVO = motivosInternacaoVO;
	}

	public SumarioAltaDiagnosticosVO getDiagnosticoPrincipalVO() {
		return this.diagnosticoPrincipalVO;
	}

	public void setDiagnosticoPrincipalVO(
			SumarioAltaDiagnosticosVO diagnosticoPrincipalVO) {
		this.diagnosticoPrincipalVO = diagnosticoPrincipalVO;
	}

	public SumarioAltaDiagnosticosVO getDiagnosticosSecundariosVO() {
		return this.diagnosticosSecundariosVO;
	}

	public void setDiagnosticosSecundariosVO(
			SumarioAltaDiagnosticosVO diagnosticosSecundariosVO) {
		this.diagnosticosSecundariosVO = diagnosticosSecundariosVO;
	}

	public String pesquisarCidCapitulo() {	
		return PAGE_PESQUISA_CID_CAPITULO_DIAGNOSTICO;
	}

	public void setCidSeqPesquisado(Integer cidSeqPesquisado) {
		this.cidSeqPesquisado = cidSeqPesquisado;
	}

	public Integer getCidSeqPesquisado() {
		return this.cidSeqPesquisado;
	}

	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

	public Integer getCurrentSlider() {
		return this.currentSlider;
	}

	public void setCurrentSliderDiagnosticos(Integer currentSliderDiagnosticos) {
		this.currentSliderDiagnosticos = currentSliderDiagnosticos;
	}

	public Integer getCurrentSliderDiagnosticos() {
		return this.currentSliderDiagnosticos;
	}

	public void setSumarioAltaPosAltaController(
			ManterSumarioAltaPosAltaController sumarioAltaPosAltaController) {
		this.sumarioAltaPosAltaController = sumarioAltaPosAltaController;
	}

	public ManterSumarioAltaPosAltaController getSumarioAltaPosAltaController() {
		return sumarioAltaPosAltaController;
	}

	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setMostrarPanelComboMotivo(Boolean mostrarPanelComboMotivo) {
		this.mostrarPanelComboMotivo = mostrarPanelComboMotivo;
	}

	public Boolean getMostrarPanelComboMotivo() {
		return mostrarPanelComboMotivo;
	}

	public void setMostrarPanelComboDiagnostico(
			Boolean mostrarPanelComboDiagnostico) {
		this.mostrarPanelComboDiagnostico = mostrarPanelComboDiagnostico;
	}

	public Boolean getMostrarPanelComboDiagnostico() {
		return mostrarPanelComboDiagnostico;
	}

	public void setMostrarPanelOutrosDiagnosticos(
			Boolean mostrarPanelOutrosDiagnosticos) {
		this.mostrarPanelOutrosDiagnosticos = mostrarPanelOutrosDiagnosticos;
	}

	public Boolean getMostrarPanelOutrosDiagnosticos() {
		return mostrarPanelOutrosDiagnosticos;
	}

	public void setMostrarPanelComboDiagSecundario(
			Boolean mostrarPanelComboDiagSecundario) {
		this.mostrarPanelComboDiagSecundario = mostrarPanelComboDiagSecundario;
	}

	public Boolean getMostrarPanelComboDiagSecundario() {
		return mostrarPanelComboDiagSecundario;
	}

	public void setMostrarPanelOutrosDiagSecundarios(
			Boolean mostrarPanelOutrosDiagSecundarios) {
		this.mostrarPanelOutrosDiagSecundarios = mostrarPanelOutrosDiagSecundarios;
	}

	public Boolean getMostrarPanelOutrosDiagSecundarios() {
		return mostrarPanelOutrosDiagSecundarios;
	}

	public Boolean getMostrarSeguimentoAtendimento() {
		return mostrarSeguimentoAtendimento;
	}

	public void setMostrarSeguimentoAtendimento(Boolean mostrarSeguimentoAtendimento) {
		this.mostrarSeguimentoAtendimento = mostrarSeguimentoAtendimento;
	}
	
	public Boolean getVoltarCidSeqPesquisado() {
		return voltarCidSeqPesquisado;
	}
	
	public void setVoltarCidSeqPesquisado(Boolean voltarCidSeqPesquisado) {
		this.voltarCidSeqPesquisado = voltarCidSeqPesquisado;
	}
	
	public boolean isRetornoRecomendacoesAlta() {
		return retornoRecomendacoesAlta;
	}
	
	public void setRetornoRecomendacoesAlta(boolean retornoRecomendacoesAlta) {
		this.retornoRecomendacoesAlta = retornoRecomendacoesAlta;
	}
	
	public void setProcedimentosController(
			ManterSumarioAltaProcedimentosController procedimentosController) {
		this.procedimentosController = procedimentosController;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public ManterSumarioAltaProcedimentosController getProcedimentosController() {
		return procedimentosController;
	}

	public Integer getTogglePanelCirurgiasRealizadas() {
		return togglePanelCirurgiasRealizadas;
	}

	public void setTogglePanelCirurgiasRealizadas(
			Integer togglePanelCirurgiasRealizadas) {
		this.togglePanelCirurgiasRealizadas = togglePanelCirurgiasRealizadas;
	}

	public Integer getTogglePanelProcedimentos() {
		return togglePanelProcedimentos;
	}

	public void setTogglePanelProcedimentos(Integer togglePanelProcedimentos) {
		this.togglePanelProcedimentos = togglePanelProcedimentos;
	}

	public Integer getTogglePanelOutrosProcedimentos() {
		return togglePanelOutrosProcedimentos;
	}

	public void setTogglePanelOutrosProcedimentos(
			Integer togglePanelOutrosProcedimentos) {
		this.togglePanelOutrosProcedimentos = togglePanelOutrosProcedimentos;
	}

	public Integer getTogglePanelConsultorias() {
		return togglePanelConsultorias;
	}

	public void setTogglePanelConsultorias(Integer togglePanelConsultorias) {
		this.togglePanelConsultorias = togglePanelConsultorias;
	}

	public Integer getTogglePanelPrincipaisFarmacos() {
		return togglePanelPrincipaisFarmacos;
	}

	public void setTogglePanelPrincipaisFarmacos(
			Integer togglePanelPrincipaisFarmacos) {
		this.togglePanelPrincipaisFarmacos = togglePanelPrincipaisFarmacos;
	}

	public Integer getTogglePanelInformacoesComplementares() {
		return togglePanelInformacoesComplementares;
	}

	public void setTogglePanelInformacoesComplementares(
			Integer togglePanelInformacoesComplementares) {
		this.togglePanelInformacoesComplementares = togglePanelInformacoesComplementares;
	}

	public Integer getTogglePanelMotivosInternacao() {
		return togglePanelMotivosInternacao;
	}

	public void setTogglePanelMotivosInternacao(
			Integer togglePanelMotivosInternacao) {
		this.togglePanelMotivosInternacao = togglePanelMotivosInternacao;
	}

	public Integer getTogglePanelDiagnosticoPrincipal() {
		return togglePanelDiagnosticoPrincipal;
	}

	public void setTogglePanelDiagnosticoPrincipal(
			Integer togglePanelDiagnosticoPrincipal) {
		this.togglePanelDiagnosticoPrincipal = togglePanelDiagnosticoPrincipal;
	}

	public Integer getTogglePanelDiagnosticosSecundarios() {
		return togglePanelDiagnosticosSecundarios;
	}

	public void setTogglePanelDiagnosticosSecundarios(
			Integer togglePanelDiagnosticosSecundarios) {
		this.togglePanelDiagnosticosSecundarios = togglePanelDiagnosticosSecundarios;
	}

	public Integer getTogglePanelMotivoAlta() {
		return togglePanelMotivoAlta;
	}

	public void setTogglePanelMotivoAlta(Integer togglePanelMotivoAlta) {
		this.togglePanelMotivoAlta = togglePanelMotivoAlta;
	}

	public Integer getTogglePanelPosAlta() {
		return togglePanelPosAlta;
	}

	public void setTogglePanelPosAlta(Integer togglePanelPosAlta) {
		this.togglePanelPosAlta = togglePanelPosAlta;
	}

	public Integer getTogglePanelRecomendacaoAltaCadastrada() {
		return togglePanelRecomendacaoAltaCadastrada;
	}

	public void setTogglePanelRecomendacaoAltaCadastrada(
			Integer togglePanelRecomendacaoAltaCadastrada) {
		this.togglePanelRecomendacaoAltaCadastrada = togglePanelRecomendacaoAltaCadastrada;
	}

	public Integer getTogglePanelAltaItemPrescricao() {
		return togglePanelAltaItemPrescricao;
	}

	public void setTogglePanelAltaItemPrescricao(
			Integer togglePanelAltaItemPrescricao) {
		this.togglePanelAltaItemPrescricao = togglePanelAltaItemPrescricao;
	}

	public Integer getTogglePanelAltaNaoCadastrada() {
		return togglePanelAltaNaoCadastrada;
	}

	public void setTogglePanelAltaNaoCadastrada(
			Integer togglePanelAltaNaoCadastrada) {
		this.togglePanelAltaNaoCadastrada = togglePanelAltaNaoCadastrada;
	}
	
	public Integer getTogglePanelReceitaEspecial() {
		return togglePanelReceitaEspecial;
	}

	public void setTogglePanelReceitaEspecial(Integer togglePanelReceitaEspecial) {
		this.togglePanelReceitaEspecial = togglePanelReceitaEspecial;
	}
	
	public SumarioAltaDiagnosticosCidVO getItemMotivosInternacaoSelecionado() {
		return itemMotivosInternacaoSelecionado;
	}

	public void setItemMotivosInternacaoSelecionado(
			SumarioAltaDiagnosticosCidVO itemMotivosInternacaoSelecionado) {
		this.itemMotivosInternacaoSelecionado = itemMotivosInternacaoSelecionado;
	}

	public SumarioAltaDiagnosticosCidVO getItemDiagnosticoPrincipalSelecionado() {
		return itemDiagnosticoPrincipalSelecionado;
	}

	public void setItemDiagnosticoPrincipalSelecionado(
			SumarioAltaDiagnosticosCidVO itemDiagnosticoPrincipalSelecionado) {
		this.itemDiagnosticoPrincipalSelecionado = itemDiagnosticoPrincipalSelecionado;
	}

	public SumarioAltaDiagnosticosCidVO getItemDiagnosticoSecundarioSelecionado() {
		return itemDiagnosticoSecundarioSelecionado;
	}

	public void setItemDiagnosticoSecundarioSelecionado(
			SumarioAltaDiagnosticosCidVO itemDiagnosticoSecundarioSelecionado) {
		this.itemDiagnosticoSecundarioSelecionado = itemDiagnosticoSecundarioSelecionado;
	}

	public Boolean getMostrarPanelOutrosMotivos() {
		return mostrarPanelOutrosMotivos;
	}

	public void setMostrarPanelOutrosMotivos(Boolean mostrarPanelOutrosMotivos) {
		this.mostrarPanelOutrosMotivos = mostrarPanelOutrosMotivos;
	}

	public Integer getTogglePanelReceitaGeral() {
		return togglePanelReceitaGeral;
	}

	public void setTogglePanelReceitaGeral(Integer togglePanelReceitaGeral) {
		this.togglePanelReceitaGeral = togglePanelReceitaGeral;
	}
	
	public List<FatSituacaoSaidaPaciente> getListaEstadosClinicos() {
		return listaEstadosClinicos;
	}

	public void setListaEstadosClinicos(
			List<FatSituacaoSaidaPaciente> listaEstadosClinicos) {
		this.listaEstadosClinicos = listaEstadosClinicos;
	}
	
	public Boolean getRenderAtestado() {
		return renderAtestado;
	}

	public void setRenderAtestado(Boolean renderAtestado) {
		this.renderAtestado = renderAtestado;
	}
	public ManterReceitaController getManterReceitaController() {
		return manterReceitaController;
	}

	public void setManterReceitaController(ManterReceitaController manterReceitaController) {
		this.manterReceitaController = manterReceitaController;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	
	public boolean isGravouEvo() {
		return gravouEvo;
	}
	
	public void setGravouEvo(boolean gravouEvo) {
		this.gravouEvo = gravouEvo;
	}

	public boolean isRetornoTelaReceitas() {
		return retornoTelaReceitas;
	}

	public void setRetornoTelaReceitas(boolean retornoTelaReceitas) {
		this.retornoTelaReceitas = retornoTelaReceitas;
	}

	public boolean isPrimeiraVezExecutouSumarioAlta() {
		return primeiraVezExecutouSumarioAlta;
	}

	public void setPrimeiraVezExecutouSumarioAlta(
			boolean primeiraVezExecutouSumarioAlta) {
		this.primeiraVezExecutouSumarioAlta = primeiraVezExecutouSumarioAlta;
	}
}