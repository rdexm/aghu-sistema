package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.emergencia.perinatologia.action.RegistrarGestacaoController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RedirectVO;

public class VerificarPrescricaoMedicaController extends ActionController {

	private static final long serialVersionUID = -2712707147450913465L;
	private static final Log LOG = LogFactory.getLog(VerificarPrescricaoMedicaController.class);
	private static final String REDIRECIONA_LISTAR_CIRURGIAS = "/blococirurgico/listarCirurgias.xhtml";//TODO Módulo ainda não foi migrado
	private static final String PAGE_SOLICITAR_EXAME = "exames-solicitacaoExameCRUD";
	private static final String ANTECIPAR_SUMARIO = "prescricaomedica-anteciparSumario";
	private static final String SUMARIO_OBITO = "prescricaomedica-manterSumarioObito";
	private static final String SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 
	private static final String REDIRECIONA_LISTA_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";
	private static final String REDIRECIONA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final String REDIRECIONA_ATENDIMENTO_EXTERNO_CRUD = "exames-atendimentoExternoCRUD";
	//private static final String REDIRECIONA_ATUALIZA_DIAGNOSTICO =  "prescricaomedica-manterDiagnosticoPacienteCti"; 
	private static final String REDIRECIONA_PESQUISA_FORMULARIO_CONSULTORIA = "pesquisarFormularioConsultoria";
	private static final String REGISTRAR_GESTACAO = "/pages/perinatologia/registrarGestacao.xhtml";
	
	public enum VerificarPrescricaoMedicaControllerExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE, AIP_PACIENTE_NAO_ENCONTRADO 
		, ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO, ERRO_PRESCRICAO_MEDICA_NAO_POSSUI_ATENDIMENTO, MPM_04174
	}	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@EJB 
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICupsFacade cupsFacade;

	@EJB
    private IParametroFacade parametroFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject 
	private ManterPrescricaoMedicaController manterPrescricaoMedicaController;
	
	//@Inject 
	//private ManterDiagnosticoPacienteCtiController manterDiagnosticoPacienteCtiController;
	
	@Inject 
	private RelatorioPrescricaoMedicaController relatorioPrescricaoMedicaController;
	
	@Inject 
	private ModalCentralMensagensController modalCentralMensagensController;
	
	@EJB
    private ICascaFacade cascaFacade;
	
	@Inject
	private PreencherPim2Controller preencherPim2Controller;
	
	@Inject PreencherEscoreGravidadeController preencherEscoreGravidadeController;
	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;
	
	private List<MpmPrescricaoMedica> prescricaoMedicas = new ArrayList<MpmPrescricaoMedica>();

	private AipPacientes paciente;
	private String nomeSocial;
	private Integer codPac;
	private Integer prontPac;
	private Integer pacCodigoFonetica;//Parâmetro codigoPaciente
	
	private AinLeitos leito;

	private AghAtendimentos atendimento;

	private AghUnidadesFuncionais unidadeFuncional;

	private Date dtPrescricao;

	private AinQuartos quarto;

	private Date dtPendente;
	
	private boolean enableButtonAnamneseEvolucao;

	private String mensagemModal;

	private Integer atendimentoSeq;

	private MpmPrescricaoMedica prescricaoMedica;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;

	private Boolean habilitaBotaoCriarPrescricao;

	private String mensagemBotaoCriarPrescricao;

	private String voltarPara;//Parâmetro voltar_para

	private Boolean habilitaVoltar;

	private Integer apaSeq;
	
	private List<AghAtendimentos> atendimentosPassiveisDePrescricao;

	// Estória 28133 parametros para retornar a tela do emergencia
	private Boolean origemEmergencia;
	private Integer cid;
	private Short seqp; 
	private Integer codigoPaciente;
	private String abaOrigem;
	private Integer numeroConsulta;
	private Integer prontuario; 
	private String nomePaciente;
	private String idadeFormatada;
	private Long trgSeq;
	private Short unfSeq;
	private Integer ateSeq;
    private String banco;
    private String UrlBaseWebForms;
    private boolean fichaApachePendente = false;
	private boolean redirecionarAghWebSumarioAlta = false;
	private boolean redirecionarAghWebSumarioObito = false;
	private boolean redirecionarAghWebFichaApache = false;
	private boolean redirecionarAghWebAnteciparSumario = false;
	private boolean redirecionarReimprimir = false;
	private boolean habilitaAltaSumario = false;
	private boolean habilitaDesbloquearAlta = false;
	private String aghuUsoSumario;
	private boolean aghuBotoesExameHemoterapia;
	
    @PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		// #28133
		obterParametrosIniciacao();
		obterParametrosRetorno();
	}
	private void obterParametrosRetorno() {
		String nomeParm = getRequestParameter("nome");
		if(StringUtils.isNotBlank(nomeParm)){
			this.nomePaciente = nomeParm;
		}
		String idadeFormatadaParm = getRequestParameter("idade");
		if(StringUtils.isNotBlank(idadeFormatadaParm)){
			this.idadeFormatada = idadeFormatadaParm;
		}
		String voltarParaParm = getRequestParameter("voltarPara");
		if(StringUtils.isNotBlank(voltarParaParm)){
			this.voltarPara = voltarParaParm;
		}
		String seqpParm = getRequestParameter("seqp");
		if(StringUtils.isNotBlank(seqpParm)){
			this.seqp = Short.valueOf(seqpParm);
		}
		String abaOrigemParm = getRequestParameter("abaOrigem");
		if(StringUtils.isNotBlank(abaOrigemParm)){
			this.setAbaOrigem(abaOrigemParm);
		}
		String unfSeqParm = getRequestParameter("unfSeq");
		if(StringUtils.isNotBlank(unfSeqParm)){
			this.unfSeq = Short.valueOf(unfSeqParm);
		}
		String ateSeqpParm = getRequestParameter("ateSeq");
		if(StringUtils.isNotBlank(ateSeqpParm)){
			this.setAteSeq(Integer.valueOf(ateSeqpParm));
		}
	}
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				setarDataReferencia();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	private void obterParametrosIniciacao() {
		String prontPacParm = getRequestParameter("prontPac");
		if(StringUtils.isNotBlank(prontPacParm)){
			this.prontuario = Integer.valueOf(prontPacParm);
			this.prontPac = this.prontuario;
		}
		String codPacParm = getRequestParameter("codPac");
		if(StringUtils.isNotBlank(codPacParm)){
			this.codigoPaciente = Integer.valueOf(codPacParm);
			this.codPac = this.codigoPaciente;
		}
		String cid = getRequestParameter("param_cid");
		if(StringUtils.isNotBlank(cid)){		
			this.cid = Integer.valueOf(cid);
		}
		String origemEmergenciaParm = getRequestParameter("origemEmergencia");
		if(StringUtils.isNotBlank(origemEmergenciaParm)){
			this.origemEmergencia = Boolean.valueOf(origemEmergenciaParm);
		}
		String numeroConsultaParm = getRequestParameter("numeroConsulta");
		if(StringUtils.isNotBlank(numeroConsultaParm)){		
			this.numeroConsulta = Integer.valueOf(numeroConsultaParm);
		}
	}
	
	private void buscarParametroDesabilitarBotoesExameHemoterapia() throws ApplicationBusinessException {
		AghParametros aghParametroDesabilitarBotoes = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA);
		if(aghParametroDesabilitarBotoes != null && aghParametroDesabilitarBotoes.getVlrTexto() != null){
			if(aghParametroDesabilitarBotoes.getVlrTexto().equalsIgnoreCase("S")){
				setAghuBotoesExameHemoterapia(true);
			}else{
				setAghuBotoesExameHemoterapia(false);
			}
		}
	}
	
	public void inicio() throws ApplicationBusinessException {
		try{
			atendimentosPassiveisDePrescricao = null;
			limpaVariaveisRedirecionaAghWeb();
			buscaParametroUsoSumario();
			buscarParametroDesabilitarBotoesExameHemoterapia();
			if (atendimentoSeq != null) {
				this.setAtendimento(this.aghuFacade
						.obterAghAtendimentoPorChavePrimaria(atendimentoSeq));
				if (getAtendimento() == null){
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um errou ao carregar dados do atendimento. Contate o administrador do sistema.", "Ocorreu um errou ao carregar dados do atendimento. Contate o administrador do sistema.");
					FacesContext.getCurrentInstance().addMessage("Messages", message);
					return;
				} else {
					this.carregarAtendimento();
					this.setDtPrescricao(prescricaoMedicaFacade
							.obterDataReferenciaProximaPrescricao(this.getAtendimento()));
					this.habilitaBotoesSumarioEDesbloquearAlta();
				}
			}
			
			// Busca as informações do paciente caso já tenha sido feita a pesquisa fonética 
			if (codPac != null || prontPac != null || pacCodigoFonetica != null) {
				// #28133 Carregar as informações do paciente quando quando vir da tela consultaCo ou abaAtendimento (Módulo Emergncia).
				if (Boolean.TRUE.equals(origemEmergencia)) {
					obterInformacoesPaciente(false);
				} else {
					obterInformacoesPaciente(true);
				}
			}
			
	    	//setarDataReferencia();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
		
		if ((habilitaVoltar != null && habilitaVoltar) || verificaOrigemHabilitaBotaoVoltar()) {
			habilitaVoltar = true;
		} else {
			habilitaVoltar = false;
		}
	}
	
	private boolean verificaOrigemHabilitaBotaoVoltar() {
		return !StringUtils.isBlank(voltarPara)
		&& (
			voltarPara.equalsIgnoreCase("prescricaomedica-pesquisarListaPacientesInternados") ||
			voltarPara.equalsIgnoreCase("blococirurgico-listaCirurgias") ||
			voltarPara.equalsIgnoreCase("ambulatorio-atenderPacientesAgendados") ||
			voltarPara.equalsIgnoreCase("atendimentoExternoCRUD") ||
			voltarPara.equalsIgnoreCase("/pages/perinatologia/registrarGestacao.xhtml") ||
			voltarPara.equalsIgnoreCase("/pages/emergencia/listaPacientesEmergencia.xhtml") ||
			voltarPara.equalsIgnoreCase("ambulatorio-atenderPacientesEvolucao")
		   );
	}

	private void buscaParametroUsoSumario() throws ApplicationBusinessException{
		AghParametros aghParametrosUsoSumario = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_USO_SUMARIO);
		if (aghParametrosUsoSumario != null) {
			aghuUsoSumario = aghParametrosUsoSumario.getVlrTexto();
		}
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	public String redirecionarPesquisaFormularioConsultoria(){
		return REDIRECIONA_PESQUISA_FORMULARIO_CONSULTORIA;
	}
	
	public Boolean verificarModuloExameAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());
	}
	
    public String realizarChamadaSolicitarExame() {
    	String retorno = null;
    	try {
    		if (atendimentoSeq != null) {
    			this.solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(this.atendimentoSeq);
    			retorno = PAGE_SOLICITAR_EXAME;
    		}
    	} catch (BaseException e) {
    		apresentarExcecaoNegocio(e);
    	}
    	return retorno;
    }
    
    
	public String realizarChamadaAnamneseEvolucoes() {
		String retorno = null;
		if (atendimentoSeq != null) {
			retorno = "prescricaomedica-redirecionarListarAnamneseEvolucoes";
		}
		return retorno;
	}
	
	/**
	 * Obtem as informações do paciente (nome, codigo e prontuario) e retorna se foi possivel obter
	 * 
	 * @return true caso consiga obter as informações, false caso contrário
	 *  
	 */
	private boolean obterInformacoesPaciente(boolean inicio) throws ApplicationBusinessException {
		if (codPac == null && prontPac == null && pacCodigoFonetica == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					VerificarPrescricaoMedicaControllerExceptionCode.MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE.toString());
		} else if (prontPac != null && !inicio) {
    		paciente = pacienteFacade.obterPacientePorProntuario(prontPac);
    		if(paciente != null){	
    			setNomeSocial(paciente.getNomeSocial());
    			codPac = paciente.getCodigo();
    			prontPac = paciente.getProntuario();
    			setarDataReferencia();
    		} else {
    			this.apresentarMsgNegocio(Severity.ERROR, 
    					VerificarPrescricaoMedicaControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());   
    			return false;
    		}
    		return true;
    	}
		else if (codPac != null  && !inicio) {
			paciente = pacienteFacade.obterPacientePorCodigo(codPac);
			if(paciente != null){
				codPac = paciente.getCodigo();
				prontPac = paciente.getProntuario();
				setarDataReferencia();
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, 
						VerificarPrescricaoMedicaControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());
    			return false;
			}
    		return true;
    	} else if (pacCodigoFonetica != null && inicio) {
			paciente = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			codPac = paciente.getCodigo();
			prontPac = paciente.getProntuario();
			setarDataReferencia();
		}
		return false;
	}
	
	/**
	 * Realizar as consistências antes de chamar a tela de Sumário Alta
	 * 
	 * @return
	 */
	public String realizarChamadaSumarioAlta() {
		try {
			if (this.getAtendimento() != null
					&& this.getAtendimento().getSeq() != null) {
				this.prescricaoMedicaFacade.realizarConsistenciasSumarioAlta(this.getAtendimento().getSeq());
				this.apaSeq = this.prescricaoMedicaFacade
				.recuperarAtendimentoPaciente(this.getAtendimento()
						.getSeq());
				return SUMARIO_ALTA;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Desbloquear Alta
	 * 
	 * @return
	 */
	public void realizarChamadaDesbloquearAlta() {
		// 7624174
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			this.prescricaoMedicaFacade.desbloquearAlta(this.getAtendimento().getSeq(), nomeMicrocomputador);
			this.habilitaBotoesSumarioEDesbloquearAlta();
			this.limpaVariaveisRedirecionaAghWeb();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,
					e.toString());
		}
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Sumário Óbito
	 * 
	 * @return
	 */
	public String realizarChamadaSumarioObito() {
		try {
			if (this.getAtendimento() != null
					&& this.getAtendimento().getSeq() != null) {
				// this.prescricaoMedicaFacade.realizarConsistenciasSumarioObito(atdSeq);
				this.apaSeq = this.prescricaoMedicaFacade
				.recuperarAtendimentoPaciente(this.getAtendimento()
						.getSeq());
				return SUMARIO_OBITO;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Antecipar Sumário
	 * 
	 * @return
	 */
	public String realizarChamadaAnteciparSumario() {
		try {
			if (this.getAtendimento() != null
					&& this.getAtendimento().getSeq() != null) {
				// this.prescricaoMedicaFacade.realizarConsistenciasSumarioObito(atdSeq);
				this.apaSeq = this.prescricaoMedicaFacade
				.recuperarAtendimentoPaciente(this.getAtendimento()
						.getSeq());
				return ANTECIPAR_SUMARIO;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void carregarAtendimento() throws ApplicationBusinessException {
		limpaVariaveisRedirecionaAghWeb();
		if(this.getAtendimento().getSeq() != null){
			this.apaSeq = this.prescricaoMedicaFacade
					.recuperarAtendimentoPaciente(this.getAtendimento()
							.getSeq());
			this.fichaApachePendente = prescricaoMedicaFacade
					.verificaPendenciaFichaApache(this.getAtendimento()
							.getSeq());
			this.setPrescricaoMedicas(this.prescricaoMedicaFacade
					.pesquisarPrescricoesMedicasNaoEncerradasPorAtendimento(this
							.getAtendimento().getSeq()));
			this.setPrescricaoMedicaVO(new PrescricaoMedicaVO(this.getPrescricaoMedica()));
			this.setPaciente(this.getAtendimento().getPaciente());
			this.setNomeSocial(this.getAtendimento().getPaciente().getNomeSocial());
			this.setProntPac(this.getPaciente().getProntuario());
			this.setCodPac(this.getPaciente().getCodigo());
			this.setLeito(this.getAtendimento().getLeito());
			this.setQuarto(this.getAtendimento().getQuarto());
			this.setUnidadeFuncional(this.getAtendimento().getUnidadeFuncional());
			this.verificarCriarPrescricao();
			this.atendimentoSeq = this.getAtendimento().getSeq();
			this.habilitaBotoesSumarioEDesbloquearAlta();
		}
	}

	private void habilitaBotoesSumarioEDesbloquearAlta(){
		if(this.getAtendimento() != null){
			this.habilitaAltaSumario = DominioOrigemAtendimento.getOrigensPrescricaoInternacao().contains(this.getAtendimento().getOrigem())
					&& this.prescricaoMedicaFacade.habilitarAltaSumario(this.getAtendimento().getSeq());
		}
		if (this.prescricaoMedicaFacade
				.obterSumarioAltaSemMotivoAltaPeloAtendimento(this.atendimentoSeq) == null) {
			MpmAltaSumario altaSumario = this.prescricaoMedicaFacade
			.obterAltaSumarioConcluidaPeloAtendimento(this.atendimentoSeq);
			if (altaSumario == null) {
				habilitaDesbloquearAlta = false;
			} else {
				habilitaDesbloquearAlta = true;
			}
		} else {
			habilitaDesbloquearAlta = true;
		}
	}
	public void setarDataReferencia() throws ApplicationBusinessException {
		try {
			if (paciente != null) {
				atendimentosPassiveisDePrescricao = ambulatorioFacade.pesquisarAtendimentoParaPrescricaoMedica(paciente.getCodigo(), null);
				if(atendimentosPassiveisDePrescricao.isEmpty()){
					apresentarMsgNegocio(Severity.ERROR, 						
							VerificarPrescricaoMedicaControllerExceptionCode.ERRO_PRESCRICAO_MEDICA_NAO_POSSUI_ATENDIMENTO.toString());
					limparCampos();
				} else {
					if(atendimentosPassiveisDePrescricao.size() == 1){
						AghAtendimentos atendimento = atendimentosPassiveisDePrescricao.get(0);
						if (atendimento != null) {
							atendimento.getUnidadeFuncional().setCaracteristicas
							(new HashSet<>(aghuFacade.listarCaracteristicasUnidadesFuncionais(
									atendimento.getUnidadeFuncional().getSeq(),	null, 0, 1000)));
							this.setAtendimento(atendimento);
							this.carregarAtendimento();
							this.setDtPrescricao(prescricaoMedicaFacade.obterDataReferenciaProximaPrescricao(atendimento));
						}
					}else {
						openDialog("modalAtendimentosPrescricaoWG");
					}
				}
			} else {
				limparCampos();
			}
		} catch (ApplicationBusinessException e) {
			limparCampos();
			this.apresentarExcecaoNegocio(e);
		}
	}
	public void processarSelecaoAtendimento() throws ApplicationBusinessException{
		try{
			this.carregarAtendimento();
			this.setDtPrescricao(prescricaoMedicaFacade.obterDataReferenciaProximaPrescricao(atendimento));
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			limparCampos();
		}
	}
	public List<AinLeitos> pesquisarLeito(String objParam)
	throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		try {
			String strPesquisa = (String) objParam;
			if (!StringUtils.isBlank(strPesquisa)) {
				strPesquisa = strPesquisa.toUpperCase();
			}
			AghAtendimentos atendimento = prescricaoMedicaFacade.obterAtendimentoPorLeito(strPesquisa);
			this.setAtendimento(atendimento);
			this.setPaciente(atendimento.getPaciente());
			this.carregarAtendimento();
			leitos.add(atendimento.getLeito());
			return leitos;
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	// public Integer pesquisarCountLeitos(Object objParam) throws
	// ApplicationBusinessException {
	// String strPesquisa = (String) objParam;
	// if(!StringUtils.isBlank(strPesquisa)){
	// strPesquisa = strPesquisa.toUpperCase();
	// }
	// List<AghAtendimentos> listaAtendimentos =
	// prescricaoMedicaFacade.pesquisarAtendimentoPorLeito(strPesquisa);
	// return listaAtendimentos.size();
	// }
	public void limparCampos() {
		limpaVariaveisRedirecionaAghWeb();
		atendimentoSeq = null;
		this.setLeito(null);
		this.setQuarto(null);
		this.setUnidadeFuncional(null);
		this.setAtendimento(null);
		this.prescricaoMedicas.clear();
		this.setDtPrescricao(new Date());
		this.verificarCriarPrescricao();
		this.habilitaAltaSumario = false;
		this.habilitaDesbloquearAlta = false;
		this.setPaciente(null);
		pacCodigoFonetica = null;
		prontPac = null;
		codPac = null;
	}
	private void verificarCriarPrescricao() {
		if (this.getAtendimento() != null) {
			try {
				this.prescricaoMedicaFacade.verificarCriarPrescricao(this
						.getAtendimento());
				this.setMensagemBotaoCriarPrescricao(WebUtil.initLocalizedMessage("LABEL_NOVA_PRESCRICAO", null));
				this.setHabilitaBotaoCriarPrescricao(true);
				try{
				// Melhoria #40184 e correção de homologação #47659
				this.setHabilitaBotaoCriarPrescricao(this.prescricaoMedicaFacade.verificarAnamneseCriarPrescricao(this.getAtendimento()));
			} catch (ApplicationBusinessException e) {
					this.setMensagemBotaoCriarPrescricao(WebUtil.initLocalizedMessage(e.getLocalizedMessage(), null));
				}
			} catch (ApplicationBusinessException e) {
				if(e.getCode().toString().equals(VerificarPrescricaoMedicaControllerExceptionCode.MPM_04174.toString())){
					this.apresentarExcecaoNegocio(e);
					this.setHabilitaBotaoCriarPrescricao(true);
				}else {
					this.setHabilitaBotaoCriarPrescricao(false);
				}
			}
		} else {
			this.setMensagemBotaoCriarPrescricao("");
			this.setHabilitaBotaoCriarPrescricao(false);
		}
	}
	public String criarPrescricao() {
		if (verificaSumarioAltaObitoConcluido()){
			return null;
		} else {
			try {
	
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}
				
				Object retorno = this.prescricaoMedicaFacade
						.criarPrescricao(atendimento, this.dtPrescricao, nomeMicrocomputador);
	
				modalCentralMensagensController.setAtdSeq(atendimento.getSeq());
				modalCentralMensagensController.iniciarModal();
				
				if(retorno instanceof MpmPrescricaoMedica) {
					this.setPrescricaoMedica((MpmPrescricaoMedica)retorno);
				} else {
					preencherPim2Controller.setAtdSeq((Integer)((RedirectVO)retorno).getParametros()[0]);
					preencherEscoreGravidadeController.setSeq((Integer)((RedirectVO)retorno).getParametros()[0]);
				manterPrescricaoMedicaController.setPmeSeqAtendimento(atendimento.getSeq());
					return ((RedirectVO)retorno).getPagina();
				}
				
				/* #73946 - Retirado esta parte por que atualmente não é utilizado desta forma na versão 6.
				 * if (!prescricaoMedicaFacade.verificarPrimeiraEntradaUnidadeFuncional(atendimentoSeq)) {
					this.manterDiagnosticoPacienteCtiController.setPmeSeq(prescricaoMedica.getId().getSeq());
					this.manterDiagnosticoPacienteCtiController.setPmeSeqAtendimento(prescricaoMedica.getId().getAtdSeq());
					return REDIRECIONA_ATUALIZA_DIAGNOSTICO; 
				} */
				this.habilitaBotoesSumarioEDesbloquearAlta();
				return this.redirecionarParaMantanterPrescricaoMedica();
			} catch (BaseException e) {
				//this.carregarAtendimento();
				this.apresentarExcecaoNegocio(e);
				return null;
			}
		}
	}
	protected String redirecionarParaMantanterPrescricaoMedica(){
		this.manterPrescricaoMedicaController.setPmeSeq(prescricaoMedica.getId().getSeq());
		this.manterPrescricaoMedicaController.setPmeSeqAtendimento(prescricaoMedica.getId().getAtdSeq());
		modalCentralMensagensController.setAtdSeq(atendimento.getSeq());
		modalCentralMensagensController.iniciarModal();
		this.manterPrescricaoMedicaController.setMostrarMensagemAlergia(Boolean.TRUE);
		return REDIRECIONA_MANTER_PRESCRICAO_MEDICA;
	}
	public String cancelar() throws ApplicationBusinessException {
		this.limparCampos();
		if(voltarPara != null){
			if ("pesquisarListaPacientesInternados".equalsIgnoreCase(voltarPara)) {
				return REDIRECIONA_LISTA_PACIENTES_INTERNADOS;
			} else if ("listarCirurgias".equalsIgnoreCase(voltarPara)) {
				return REDIRECIONA_LISTAR_CIRURGIAS;
			} else if ("manterPrescricaoMedica".equalsIgnoreCase(voltarPara)) {
				return REDIRECIONA_MANTER_PRESCRICAO_MEDICA;
			} else if ("atendimentoExternoCRUD".equalsIgnoreCase(voltarPara)) {
				return REDIRECIONA_ATENDIMENTO_EXTERNO_CRUD;	
			} else if (REGISTRAR_GESTACAO.equalsIgnoreCase(voltarPara)) {
				registrarGestacaoController.prepararTela(codigoPaciente, seqp, numeroConsulta, nomePaciente, idadeFormatada, prontuario);
				registrarGestacaoController.setTrgSeq(trgSeq);
				registrarGestacaoController.setAbaDestino(RegistrarGestacaoController.ABA_CONSULTA_CO);
				return REGISTRAR_GESTACAO;
			} else if ("/pages/emergencia/listaPacientesEmergencia.xhtml".equalsIgnoreCase(voltarPara)) {
				final String jsExecutaPrescricaoMedica = "parent.tab.loadPage(window.name, '/aghu/pages/emergencia/listaPacientesEmergencia.xhtml?"
						+ "codPac=" + this.codPac + ";"
						+ "prontPac=" + this.prontPac + ";"
						+ "origemEmergencia=true;"
						+ "abaDestino=abaEmAtendimento"+";"
						+ "retornoTelaPrescricao=" + Boolean.TRUE + ";"
						+ "unfSeq=" + this.unfSeq + ";"
						+ "ateSeq=" + this.ateSeq + ";"
						+ "param_cid=#{javax.enterprise.context.conversation.id}')";
				
				RequestContext.getCurrentInstance().execute(jsExecutaPrescricaoMedica);
				
			} 
		}
		return voltarPara;
	}
	public String getTruncProntuarioNomePaciente(Long size){
		if(paciente != null){
			String pront = paciente.getProntuario() != null ? paciente.getProntuario() + " - " : "";
			return StringUtil.trunc(pront + paciente.getNome(), Boolean.TRUE, size);
		}
		return "";
		
	}
	// editar prescricao
	public String editarPrescricao(MpmPrescricaoMedica prescricaoMedica)
	throws ApplicationBusinessException {
		if(!verificaSumarioAltaObitoConcluido()){
			try {
				this.setPrescricaoMedica(prescricaoMedica);
				this.prescricaoMedicaFacade.editarPrescricao(prescricaoMedica,false);
				return this.redirecionarParaMantanterPrescricaoMedica(); 
			} catch (ApplicationBusinessException e) {
				this.openDialog("modalPrescricaoEmUsoWG");
				this.setMensagemModal(WebUtil.initLocalizedMessage(e.getLocalizedMessage(),null, e.getParameters()));
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	public void reimprimir(MpmPrescricaoMedica prescricaoMedica) {
		if (!verificaSumarioAltaObitoConcluido()){
			redirecionarReimprimir = true;
			this.setPrescricaoMedica(prescricaoMedica);
		}
	}
	public Boolean validarReimprimir(MpmPrescricaoMedica prescricaoMedica) {
		if(prescricaoMedica != null && prescricaoMedica.getDtReferencia() != null && prescricaoMedica.getDthrInicioMvtoPendente() == null) {
			return true;
		}
		return false;
	}
	public void reimprimirPrescricaoMedica() {
		try {
			
			if(cupsFacade.isCupsAtivo()){
				getPrescricaoMedicaVO().setPrescricaoMedica(getPrescricaoMedica());
				prescricaoMedicaVO.setPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica());
				relatorioPrescricaoMedicaController.setTipoImpressao(EnumTipoImpressao.REIMPRESSAO);
				relatorioPrescricaoMedicaController.setDataMovimento(new Date());
				relatorioPrescricaoMedicaController.setServidorValido(prescricaoMedicaVO.getPrescricaoMedica().getServidorValida());
				relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
				relatorioPrescricaoMedicaController.observarEventoReimpressaoPrescricao();
			}else{
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_REDIRECIONAMENTO_CUPS_DESATIVADO");
			}
		} catch (SistemaImpressaoException  e) {
			this.apresentarExcecaoNegocio(e);
		}
		catch (BaseException | JRException | SystemException | IOException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	// Clique da modal
	public String editarPrescricaoEmUso() throws ApplicationBusinessException {
		try {
			this.prescricaoMedicaFacade
			.editarPrescricao(prescricaoMedica, true);
			return this.redirecionarParaMantanterPrescricaoMedica(); 
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public Boolean verificaSumarioAltaObitoConcluido(){
		limpaVariaveisRedirecionaAghWeb();
		Boolean retorno = false;
		if(atendimentoSeq != null){
			if(prescricaoMedicaFacade.validarSumarioConcluidoAltaEObitoPorAtdSeq(atendimentoSeq)){
				apresentarMsgNegocio("MSG_PRESCREVER_SUMARIO_ALTA_OBITO_CONCLUIDO");
				retorno = true;
			}
		}
		return retorno;
	}
	private void limpaVariaveisRedirecionaAghWeb(){
		redirecionarAghWebSumarioAlta = false;
		redirecionarAghWebSumarioObito = false;
		redirecionarAghWebFichaApache = false;
		redirecionarAghWebAnteciparSumario = false;
		redirecionarReimprimir = false;
	}
	public void preparaRedirecionarAghWebSumarioAlta(){redirecionarAghWebSumarioAlta = !verificaSumarioAltaObitoConcluido();}
	public void preparaRedirecionarAghWebSumarioObito(){redirecionarAghWebSumarioObito = !verificaSumarioAltaObitoConcluido();}
	public void preparaRedirecionarAghWebFichaApache(){redirecionarAghWebFichaApache = !verificaSumarioAltaObitoConcluido();}
	public void preparaRedirecionarAghWebAnteciparSumario(){redirecionarAghWebAnteciparSumario = !verificaSumarioAltaObitoConcluido();}
	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {this.prescricaoMedicaFacade = prescricaoMedicaFacade;}
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {return prescricaoMedicaFacade;}
	public IPacienteFacade getPacienteFacade() {return pacienteFacade;}
	public void setPacienteFacade(IPacienteFacade pacienteFacade) {this.pacienteFacade = pacienteFacade;}
	public List<MpmPrescricaoMedica> getPrescricaoMedicas() {return prescricaoMedicas;}
	public void setPrescricaoMedicas(List<MpmPrescricaoMedica> prescricaoMedicas) {this.prescricaoMedicas = prescricaoMedicas;}
	public AipPacientes getPaciente() {return paciente;}
	public void setPaciente(AipPacientes paciente) {this.paciente = paciente;}
	public AinLeitos getLeito() {return leito;}
	public void setLeito(AinLeitos leito) {this.leito = leito;}
	public AghAtendimentos getAtendimento() {return atendimento;}
	public void setAtendimento(AghAtendimentos atendimento) {this.atendimento = atendimento;}
	public AghUnidadesFuncionais getUnidadeFuncional() {return unidadeFuncional;}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {this.unidadeFuncional = unidadeFuncional;}
	public Date getDtPrescricao() {	return dtPrescricao;}
	public void setDtPrescricao(Date dtPrescricao) {this.dtPrescricao = dtPrescricao;}
	public AinQuartos getQuarto() {return quarto;}
	public void setQuarto(AinQuartos quarto) {this.quarto = quarto;}
	public Date getDtPendente() {return dtPendente;}
	public void setDtPendente(Date dtPendente) {this.dtPendente = dtPendente;}
	public String getMensagemModal() {return mensagemModal;}
	public void setMensagemModal(String mensagemModal) {this.mensagemModal = mensagemModal;}
	public Integer getAtendimentoSeq() {return atendimentoSeq;}
	public void setAtendimentoSeq(Integer atendimentoSeq) {this.atendimentoSeq = atendimentoSeq;}
	public MpmPrescricaoMedica getPrescricaoMedica() {return prescricaoMedica;}
	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
		//this.setPrescricaoMedicaVO(prescricaoMedicaVO);
		this.setPrescricaoMedicaVO(new PrescricaoMedicaVO(this.getPrescricaoMedica()));
		this.getPrescricaoMedicaVO().setDthrFim(this.prescricaoMedica.getDthrFim());
		this.getPrescricaoMedicaVO().setDthrInicio(this.prescricaoMedica.getDthrInicio());
		
	}
	public Boolean getDisableAltaSumario(){
		Boolean retorno = false;
		if(prescricaoMedicas != null && !prescricaoMedicas.isEmpty()){
			for (MpmPrescricaoMedica prescricao : prescricaoMedicas) {
				if (DominioSituacaoPrescricao.U.equals(prescricao.getSituacao()) && DateUtil.validaDataMaior(prescricao.getDthrFim(), new Date())){
					retorno = true;
					break;
				}
			}
		}
		return retorno;
	}
	public Boolean getHabilitaAltaSumario() {return habilitaAltaSumario;}
	public void setHabilitaAltaSumario(boolean habilitaAltaSumario) {this.habilitaAltaSumario = habilitaAltaSumario;}
	public Boolean getHabilitaDesbloquearAlta() {return habilitaDesbloquearAlta;}
	public void setHabilitaDesbloquearAlta(boolean habilitaDesbloquearAlta) {this.habilitaDesbloquearAlta = habilitaDesbloquearAlta;}
//	public String voltarEmergencia(){	
//		//TODO
//		return this.voltarPara;
//	}
	public String getUrlBaseWebForms() {
		if (StringUtils.isBlank(UrlBaseWebForms)) {
		    try {
				AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
		
				if (aghParametros != null) {
				    UrlBaseWebForms = aghParametros.getVlrTexto();
				}
		    } catch (BaseException e) {
		    	apresentarExcecaoNegocio(e);
		    }
		}
		return UrlBaseWebForms;
    }
    public String getBanco() {
		if (StringUtils.isBlank(banco)) {
		    AghParametros aghParametrosBanco;
			try {
				aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			} catch (ApplicationBusinessException e) {
		    	apresentarExcecaoNegocio(e);
			}
		}
		return banco;
    }
    public Boolean getAtendimentoAmbulatorial() {
    	return getAtendimento() != null && DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().contains(getAtendimento().getOrigem());
    }
	public boolean isEnableButtonAnamneseEvolucao() {return enableButtonAnamneseEvolucao;}	
	public void setEnableButtonAnamneseEvolucao(boolean enableButtonAnamneseEvolucao) {	this.enableButtonAnamneseEvolucao = enableButtonAnamneseEvolucao;}
	public String getVoltarPara() {return voltarPara;	}
	public void setVoltarPara(String voltarPara) {this.voltarPara = voltarPara;	}
	public Boolean getHabilitaVoltar() {return habilitaVoltar;	}
	public void setHabilitaVoltar(Boolean habilitaVoltar) {this.habilitaVoltar = habilitaVoltar;	}
	public void setMensagemBotaoCriarPrescricao(String mensagemBotaoCriarPrescricao) {this.mensagemBotaoCriarPrescricao = mensagemBotaoCriarPrescricao;}
	public String getMensagemBotaoCriarPrescricao() {return mensagemBotaoCriarPrescricao;	}
	public void setHabilitaBotaoCriarPrescricao(Boolean habilitaBotaoCriarPrescricao) {this.habilitaBotaoCriarPrescricao = habilitaBotaoCriarPrescricao;}
	public Boolean getHabilitaBotaoCriarPrescricao() {return habilitaBotaoCriarPrescricao;	}
	public Integer getApaSeq() {return apaSeq;	}
	public void setApaSeq(Integer apaSeq) {	this.apaSeq = apaSeq;	}
	public Integer getCodPac() {return codPac;	}
	public void setCodPac(Integer codPac) {this.codPac = codPac;	}
	public Integer getProntPac() {return prontPac;	}
	public void setProntPac(Integer prontPac) {this.prontPac = prontPac;	}
	public Integer getPacCodigoFonetica() {return pacCodigoFonetica;	}
	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {this.pacCodigoFonetica = pacCodigoFonetica;	}
	public PrescricaoMedicaVO getPrescricaoMedicaVO() {return prescricaoMedicaVO;	}
	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {this.prescricaoMedicaVO = prescricaoMedicaVO;	}
	public IAmbulatorioFacade getAmbulatorioFacade() {return ambulatorioFacade;}
	public List<AghAtendimentos> getAtendimentosPassiveisDePrescricao() {return atendimentosPassiveisDePrescricao;	}
	public void setAtendimentosPassiveisDePrescricao(List<AghAtendimentos> atendimentosPassiveisDePrescricao) {this.atendimentosPassiveisDePrescricao = atendimentosPassiveisDePrescricao;	}
	public Boolean getOrigemEmergencia() {return origemEmergencia;	}
	public void setOrigemEmergencia(Boolean origemEmergencia) {this.origemEmergencia = origemEmergencia;	}
	public Integer getCid() {return cid;	}
	public void setCid(Integer cid) {this.cid = cid;	}
	public Short getSeqp() {return seqp;	}
	public void setSeqp(Short seqp) {this.seqp = seqp;	}
	public Integer getCodigoPaciente() {return codigoPaciente;	}
	public void setCodigoPaciente(Integer codigoPaciente) {this.codigoPaciente = codigoPaciente;	}
	public String getAbaOrigem() {return abaOrigem;	}
	public void setAbaOrigem(String abaOrigem) {this.abaOrigem = abaOrigem;	}
	public Integer getNumeroConsulta() {return numeroConsulta;	}
	public void setNumeroConsulta(Integer numeroConsulta) {this.numeroConsulta = numeroConsulta;	}
	public Integer getProntuario() {return prontuario;	}
	public void setProntuario(Integer prontuario) {this.prontuario = prontuario;	}
	public String getNomePaciente() {return nomePaciente;	}
	public void setNomePaciente(String nomePaciente) {this.nomePaciente = nomePaciente;	}
	public String getIdadeFormatada() {	return idadeFormatada;	}
	public void setIdadeFormatada(String idadeFormatada) {this.idadeFormatada = idadeFormatada;	}
	public Long getTrgSeq() {return trgSeq;	}
	public void setTrgSeq(Long trgSeq) {this.trgSeq = trgSeq;	}
	public Short getUnfSeq() {return unfSeq;	}
	public void setUnfSeq(Short unfSeq) {this.unfSeq = unfSeq;	}
	public Integer getAteSeq() {return ateSeq;	}
	public void setAteSeq(Integer ateSeq) {this.ateSeq = ateSeq;	}
    public boolean isFichaApachePendente() {return fichaApachePendente;	}
	public void setFichaApachePendente(boolean fichaApachePendente) {this.fichaApachePendente = fichaApachePendente;	}
	public boolean isRedirecionarAghWebSumarioAlta() {return redirecionarAghWebSumarioAlta;}
	public void setRedirecionarAghWebSumarioAlta(boolean redirecionarAghWebSumarioAlta) {this.redirecionarAghWebSumarioAlta = redirecionarAghWebSumarioAlta;	}
	public boolean isRedirecionarAghWebSumarioObito() {return redirecionarAghWebSumarioObito;}
	public void setRedirecionarAghWebSumarioObito(boolean redirecionarAghWebSumarioObito) {this.redirecionarAghWebSumarioObito = redirecionarAghWebSumarioObito;}
	public boolean isRedirecionarAghWebFichaApache() {return redirecionarAghWebFichaApache;}
	public void setRedirecionarAghWebFichaApache(boolean redirecionarAghWebFichaApache) {this.redirecionarAghWebFichaApache = redirecionarAghWebFichaApache;}
	public boolean isRedirecionarAghWebAnteciparSumario() {return redirecionarAghWebAnteciparSumario;}
	public void setRedirecionarAghWebAnteciparSumario(boolean redirecionarAghWebAnteciparSumario) {this.redirecionarAghWebAnteciparSumario = redirecionarAghWebAnteciparSumario;}
	public boolean isRedirecionarReimprimir() {return redirecionarReimprimir;}
	public void setRedirecionarReimprimir(boolean redirecionarReimprimir) {this.redirecionarReimprimir = redirecionarReimprimir;}
	public String getAghuUsoSumario() {return aghuUsoSumario;}
	public void setAghuUsoSumario(String aghuUsoSumario) {this.aghuUsoSumario = aghuUsoSumario;}
	public String getNomeSocial() { return nomeSocial; }
	public void setNomeSocial(String nomeSocial) { this.nomeSocial = nomeSocial; }
	public boolean isAghuBotoesExameHemoterapia() {
		return aghuBotoesExameHemoterapia;
	}
	public void setAghuBotoesExameHemoterapia(boolean aghuBotoesExameHemoterapia) {
		this.aghuBotoesExameHemoterapia = aghuBotoesExameHemoterapia;
	}

}