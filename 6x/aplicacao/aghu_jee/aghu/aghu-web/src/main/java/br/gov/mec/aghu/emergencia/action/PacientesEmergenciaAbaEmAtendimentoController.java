package br.gov.mec.aghu.emergencia.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.AtenderPacientesEvolucaoController;
import br.gov.mec.aghu.ambulatorio.action.RelatorioReceitaCuidadosController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.perinatologia.action.RegistrarGestacaoController;
import br.gov.mec.aghu.emergencia.vo.PacienteEmAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioAdmissaoObstetricaController;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioConclusaoAbaReceituario;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.controller.AutenticacaoController;

/**
 * Classe responsável por controlar as ações da aba Em Atendimento
 * 
 * @author israel.haas
 */
public class PacientesEmergenciaAbaEmAtendimentoController  extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;
	
	private static final String TELA_ORIGEM = "/pages/emergencia/listaPacientesEmergencia.xhtml";
	private static final String REDIRECT_PENDENCIA_ASSINATURA = "/pages/certificacaodigital/listarPendenciasAssinatura.xhtml";
	private static final String REDIRECT_IMPRESSAO_RELATORIO = "/pages/paciente/prontuarioonline/relatorioAdmissaoObstetricaPdf.xhtml";
	private static final String PAGE_PACIENTES_AGENDADOS_RECEITAS = "ambulatorio-receitasPaciente";
	private static final String PAGE_PESQUISAR_PACIENTES_EVOLUCAO = "ambulatorio-atenderPacientesEvolucao";
	private static final String ABA_ORIGEM = "abaEmAtendimento";
	private static final Integer ABA_GESTACAO_ATUAL = 0;
	//private static final String REDIRECT_GESTACAO_ATUAL = "/pages/perinatologia/registrarGestacao.xhtml";
	private static final Log LOG = LogFactory.getLog(PacientesEmergenciaAbaEmAtendimentoController.class);
	
	private static final Enum[] innerJoin = { AacConsultas.Fields.GRADE_AGENDA_CONSULTA, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_ESPECIALIDADE, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_UNF_SALA, 
		AacConsultas.Fields.GRADE_AGENDA_CONSULTA_EQUIPE};
	private static final Enum[] leftJoin = { AacConsultas.Fields.PACIENTE, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_PROF_ESPECIALIDADE, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_PROF_ESPECIALIDADE_SERVIDOR,
		AacConsultas.Fields.GRADE_AGENDA_CONSULTA_PROF_ESPECIALIDADE_SERVIDOR_PESSOA_FISICA};
	
	@Inject
	RelatorioAdmissaoObstetricaController relatorioAdmissaoObstetricaController;
	
	@Inject
	private AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;
	
	@Inject
	private GerarBoletimAtendimentoReportGenerator gerarBoletimAtendimentoReportGenerator;
	
	@Inject
	private RelatorioConclusaoAbaReceituario relatorioConclusaoAbaReceituario;
	
//	@Inject
//	private RelatorioAtestadosController relatorioAtestadosController;
	
	@Inject
	private RelatorioReceitaCuidadosController relatorioReceitaCuidadosController;
	
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB 
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB 
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;
	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;
	
	@Inject
	private AutenticacaoController autenticacaoController;
	
	private List<PacienteEmAtendimentoVO> pacientesEmAtendimentoList = new ArrayList<PacienteEmAtendimentoVO>();
	private PacienteEmAtendimentoVO pacienteSelecionado;
	private Short seqpSelecionado;
	private String dthrMovimento;
	private Integer matricula;
	private Short vinculo;
	private Boolean visualizarPrescricaoMedicaPerinatologia = Boolean.FALSE;
	private Boolean executarPrescricaoMedicaPerinatologia = Boolean.FALSE;
	private Short unfSeq;
	private Integer ateSeq;
	private Integer numeroConsulta;
	private Integer pacCodigo;
	private Boolean retornoTelaPrescricao;
	private boolean exibeModalAutenticacao;
	private ServidorIdVO servidorIdVO;
	
	private MamUnidAtendem mamUnidAtendemEmAtendimento;
	
	private AacConsultas consultaSelecionada;
	
	private String idadeFormatadaPaciente;
	
	private String labelZona;
	
	private boolean emergenciaCustom;
	
	private List<DocumentosPacienteVO> listaDocumentosPaciente;
	
	private PacienteEmAtendimentoVO pacienteSelecionadoAtendimento;

	public void init() {
		begin(conversation, true);

		this.visualizarPrescricaoMedicaPerinatologia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),
				"visualizarPrescricaoMedicaPerinatologia", "visualizar");
		this.executarPrescricaoMedicaPerinatologia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),
				"executarPrescricaoMedicaPerinatologia", "executar");	
		
		carregarParametros();
	}
	
	public boolean habilitarSolicitarExames() {
		return this.listaPacientesEmergenciaPaginatorController.isPermAtenderPacienteCentroObstetrico() && verificaSeModuloEstaAtivo("agendamentoExames") && this.pacienteSelecionado != null;
	}
	
	private boolean verificaSeModuloEstaAtivo(String nome) {
		boolean ativo = false;
		try{
			ativo = this.emergenciaFacade.verificarSeModuloEstaAtivo(nome);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return ativo;		
	}
	
	public  String pesquisarPacientesEmAtendimento() {
		///MamUnidAtendem mamUnidAtendem = this.listaPacientesEmergenciaPaginatorController.getMamUnidAtendem();
		MamUnidAtendem mamUnidAtendem = this.mamUnidAtendemEmAtendimento;
		this.pacienteSelecionado = null;
		if (mamUnidAtendem != null) {
			try {
				this.unfSeq = mamUnidAtendem.getSeq();
				this.pacientesEmAtendimentoList = this.emergenciaFacade.listarPacientesEmAtendimento(mamUnidAtendem.getSeq());

				this.verificarSituacaoPacientes(this.pacientesEmAtendimentoList, mamUnidAtendem.getUnfSeq());
				if(retornoTelaPrescricao != null && retornoTelaPrescricao){
					this.buscarAtendimentoPaciente(pacientesEmAtendimentoList, ateSeq);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.info("Paciente Dados:"+pacienteSelecionado+"/n");
			}
		} else {
			this.pacientesEmAtendimentoList = new ArrayList<PacienteEmAtendimentoVO>();
		}
		return "/pages/emergencia/pacientesEmergenciaAbaEmAtendimento.xhtml";
	}
	
	private void buscarAtendimentoPaciente(List<PacienteEmAtendimentoVO> listaPaciente, Integer seqAtendimento) {
		if(seqAtendimento != null){
			for (PacienteEmAtendimentoVO pacienteEmAtendimentoVO : listaPaciente) {
				if(pacienteEmAtendimentoVO.getAtdSeq().equals(seqAtendimento)){
					pacienteSelecionado = pacienteEmAtendimentoVO;
					break;
				}
			}
			selecionarPaciente();
		}
	}

	private void verificarSituacaoPacientes(List<PacienteEmAtendimentoVO> listaPacientes, Short unfSeq) throws ApplicationBusinessException{
		try {
			this.emergenciaFacade.verificarSituacaoPacientes(listaPacientes, unfSeq);
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
		}
	}

	public void selecionarPaciente(){
		try {
			this.emergenciaFacade.verificarNotificacaoGmr(pacienteSelecionado);
			this.setSeqpSelecionado(this.emergenciaFacade.obterUltimaGestacaoRegistrada(pacienteSelecionado.getPacCodigo()));
			this.setDthrMovimento(this.emergenciaFacade.verificaImpressaoAdmissaoObstetrica(pacienteSelecionado, this.seqpSelecionado));
			this.setMatricula(this.emergenciaFacade.obterMatricula());
			this.setVinculo(this.emergenciaFacade.obterVinculo().shortValue());
			this.setIdadeFormatadaPaciente(emergenciaFacade.getIdadeFormatada(pacienteSelecionado.getDtNascimentoPac()));
			if(pacienteSelecionado.getConNumero()!=null) {
				selecionarConsulta(pacienteSelecionado.getConNumero());
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarConsulta(Integer conNumero) {
		if(conNumero!=null) {
			consultaSelecionada =  ambulatorioFacade.obterConsultaPorNumero(conNumero, innerJoin, leftJoin);
		}
	}
	
	public void solicitarExames(){
	
		LOG.info("Redirecionar para Estória #28365");
		
		
		/*	
		if(pacienteSelecionado != null &&
		   pacienteSelecionado.getProntuarioPac() != null&&
		   pacienteSelecionado.getDtConsulta() != null&&
		   pacienteSelecionado.getPacNome() != null){
			
		return "/pages/exames/solicitacao/solicitacaoExameList.xhtml";
			
			
		}
		return TELA_ORIGEM;
	*/
		
	}
	
	
	public String irRegistroPerinatal(){
		
		try {
			this.emergenciaFacade.verificarNotificacaoGmr(pacienteSelecionado);
			this.registrarGestacaoController.setNumeroConsulta(pacienteSelecionado.getConNumero());
			this.registrarGestacaoController.setNomePaciente(pacienteSelecionado.getPacNome());
			this.registrarGestacaoController.setIdadeFormatada(pacienteSelecionado.getIdade().toString());
			this.registrarGestacaoController.setProntuario(pacienteSelecionado.getProntuarioPac());
			this.registrarGestacaoController.setSeqp(this.emergenciaFacade.obterUltimaGestacaoRegistrada(pacienteSelecionado.getPacCodigo()));
			this.registrarGestacaoController.setAbaSelecionada(ABA_GESTACAO_ATUAL);
			this.registrarGestacaoController.setAbaDestino(ABA_GESTACAO_ATUAL);
			this.registrarGestacaoController.setPacCodigo(pacienteSelecionado.getPacCodigo());
			
			return "/pages/perinatologia/registrarGestacao.xhtml";
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String irRegistroEvolucao() throws UnknownHostException, ServiceException{
		try {
			// #54034 - Permitir atender paciente na aba Em atendimento conforme parametrização
			this.emergenciaCustom = (parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM) != null && parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM).equals("S"));
			if(emergenciaCustom) {
				this.emergenciaFacade.validarSeTemPermissaoRealizarEvolucaoAmbulatorio();
				if(pacienteSelecionado.getConNumero()!=null) {
					selecionarConsulta(pacienteSelecionado.getConNumero());
				}
				Long retorno = this.emergenciaFacade.geraRegistroAtendimento(pacienteSelecionado.getTrgSeq(), pacienteSelecionado.getAtdSeq(), getEnderecoRedeHostRemoto());
				if(retorno!=null) {
					List<AghAtendimentos> listaAtendimentoPorConsulta = aghuFacade.listarAtendimentosPorNumeroConsultaOrdenado(consultaSelecionada.getNumero());
					AghAtendimentos atd = null;
					if (!listaAtendimentoPorConsulta.isEmpty()) {
						atd = listaAtendimentoPorConsulta.get(0);
						listaPacientesEmergenciaPaginatorController.setUnfSeqRedirecionaPM(atd.getUnidadeFuncional().getSeq());
						listaPacientesEmergenciaPaginatorController.setAtdSeqRedirecionaPM(atd.getSeq());
					}
					if (atd!= null){
						List<AghAtendimentos> atendimentosPaciente = ambulatorioFacade
								.pesquisarAtendimentoParaPrescricaoMedica(
										consultaSelecionada.getPaciente().getCodigo(),atd.getSeq());
						listaPacientesEmergenciaPaginatorController.setPrescricaoAmbulatorialAtiva(!atendimentosPaciente.isEmpty());
					}
					
					
					atenderPacientesEvolucaoController.setConsultaSelecionada(consultaSelecionada);
					atenderPacientesEvolucaoController.setPaciente(consultaSelecionada.getPaciente());
					atenderPacientesEvolucaoController.setIdadeFormatada(emergenciaFacade.getIdadeFormatada(consultaSelecionada.getPaciente().getDtNascimento()));
					atenderPacientesEvolucaoController.setCameFrom("listarPacientesEmergenciaAbaEmAtendimento");
				
					return PAGE_PESQUISAR_PACIENTES_EVOLUCAO;
				}
			}
			return null;
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void prescrever(){		
		try {
			MamUnidAtendem mamUnidAtendem = this.listaPacientesEmergenciaPaginatorController.getMamUnidAtendem();
			if (mamUnidAtendem != null) {
				this.setUnfSeq(mamUnidAtendem.getSeq());
				this.emergenciaFacade.verificarUnfPmeInformatizada(mamUnidAtendem.getSeq(), this.getPacienteSelecionado());
			}		
			
			final String jsExecutaPrescricaoMedica = "parent.tab.loadPage(window.name, '/aghu/pages/prescricaomedica/verificaprescricao/verificaPrescricaoMedica.xhtml?"
					+ "codPac=" + this.pacienteSelecionado.getPacCodigo() + ";"
					+ "prontPac=" + this.pacienteSelecionado.getProntuarioPac() + ";"
					+ "origemEmergencia=true;"
					+ "voltarPara=" + this.getTelaOrigem() + ";"
					+ "abaOrigem=" + this.getAbaOrigem() + ";"
					+ "unfSeq=" + this.getUnfSeq() + ";"
					+ "ateSeq=" + this.pacienteSelecionado.getAtdSeq() + ";"
					+ "param_cid=#{javax.enterprise.context.conversation.id}')";
			
			RequestContext.getCurrentInstance().execute(jsExecutaPrescricaoMedica);
			
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			this.apresentarExcecaoNegocio(e);
		} 
	}
	
	public Boolean habilitaPrescricao(){
		if((pacienteSelecionado != null && listaPacientesEmergenciaPaginatorController.isPermAtenderPacienteCentroObstetrico()) && (visualizarPrescricaoMedicaPerinatologia || executarPrescricaoMedicaPerinatologia)){
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	public void verificarPermissaoParaSolicitarExames(){
		try {
			this.numeroConsulta = this.pacienteSelecionado != null ? this.pacienteSelecionado.getConNumero() : null;
			this.ateSeq = this.pacienteSelecionado != null ? this.pacienteSelecionado.getAtdSeq() : null;
			this.pacCodigo = this.pacienteSelecionado != null ? this.pacienteSelecionado.getPacCodigo() : null;
			this.matricula = this.servidorIdVO.getMatricula();
			this.vinculo = this.servidorIdVO.getSerVinCodigo();
			this.emergenciaFacade.verificarPermissaoParaSolicitarExames(this.matricula, this.vinculo);
			setExibeModalAutenticacao(false);
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			apresentarExcecaoNegocio(e);			
		}catch (ServiceException e) {
			FacesContext.getCurrentInstance().validationFailed();
			this.apresentarMsgNegocio(e.getMessage());
		}
	}
	
	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();

			if (this.labelZona == null) {
				this.labelZona = "Zona";
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
		}
	}
	
	public String incluirPacientes(){
			return "/pages/certificacaodigital/listarPendenciasAssinatura.xhtml";

	}
	
	// #54037 - Adicionar ação finalizar atendimento conforme parametrizacao
	public void finalizarAtendimento() {
		RapServidores servidorLogado = null;
		String hostName;
		try {
			if(pacienteSelecionadoAtendimento.getConNumero()!=null) {
				selecionarConsulta(pacienteSelecionadoAtendimento.getConNumero());
			}
			hostName = getEnderecoIPv4HostRemoto().getHostName();
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			
			emergenciaFacade.finalizarConsulta(pacienteSelecionadoAtendimento.getConNumero(), null, null, servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo(), 
					null, hostName, true);
			
			if(emergenciaFacade.imprimirEmitirBoletimAtendimento(unfSeq)) {
				this.imprimirBoletimEncaminhamentoInterno();
			}
			
			listaDocumentosPaciente = emergenciaFacade.obterListaReceitasAtestadosPaciente(pacienteSelecionadoAtendimento.getConNumero());
			
			for (DocumentosPacienteVO documento : listaDocumentosPaciente) {
				if (documento.getReceituarios() != null) {
					relatorioConclusaoAbaReceituario.imprimirReceitaMedica(documento.getReceituarios().getSeq(), documento.getImprimiu());
					this.ambulatorioFacade.atualizarIndImpressaoReceituario(documento.getReceituarios().getSeq());
				}
				
//				if (documento.getAtestado() != null) {
//					relatorioAtestadosController.setAtestado(documento.getAtestado());
//					relatorioAtestadosController.setDescricaoDocumento(documento.getDescricao());
//					relatorioAtestadosController.directPrint();
//					if (!documento.getImprimiu()) {
//						this.ambulatorioFacade.atualizarIndImpressaoAtestado(documento.getAtestado().getSeq());
//					}
//				}
				
				if (documento.getReceituarioCuidado() != null) {			
					relatorioReceitaCuidadosController.setrCuidado(documento.getReceituarioCuidado());
					relatorioReceitaCuidadosController.setDescricaoDocumento(documento.getDescricao());
					relatorioReceitaCuidadosController.imprimir();
					if (!documento.getImprimiu()) {
						for(MamReceituarioCuidadoVO rCuidado: documento.getReceituarioCuidado()){
							this.ambulatorioFacade.atualizarIndImpressaoReceituarioCuidado(rCuidado.getSeq());
					}
				}
				
			  }
		    }
			this.pesquisarPacientesEmAtendimento();
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_FINALIZACAO_ATENDIMENTO_EMERGENCIA");

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (SistemaImpressaoException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (JRException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (SystemException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	private void imprimirBoletimEncaminhamentoInterno() throws ServiceException {
		try {
			gerarBoletimAtendimentoReportGenerator.setConsulta(consultaSelecionada.getNumero());
			gerarBoletimAtendimentoReportGenerator.gerarBoletim(super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_IMPRESSAO_BA");
		} catch (UnknownHostException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (ServiceException e) {
			super.apresentarMsgNegocio(e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			super.apresentarMsgNegocio(e.getMessage());
		}
	}
	
	public void prepararTelaAcolhimento() {	   
		final String jsExecutaVisualizarAcolhimento = "parent.tab.addNewTab('redirect_#{id}', 'Acolhimento', "
			+ "'/aghu/pages/emergencia/realizarAcolhimentoPacienteCRUD.xhtml?tudoDesabilitado=true;"
			+ "trgSeq=" + this.pacienteSelecionado.getTrgSeq().toString() + ";"
			+ "pacCodigo=" + this.pacienteSelecionado.getPacCodigo().toString() + "', null, 1, true)";
		  
		RequestContext.getCurrentInstance().execute(jsExecutaVisualizarAcolhimento);		
	}
	
	/* Getters ans Setter
	 * 
	 * */

	public String getUsuarioSolicitante() {
		return this.autenticacaoController.getUsername();
	}	
	
	public String redirecionarListarPendenciasAssinatura() {
		return "certificacaodigital-listarPendenciasAssinatura";
	}	
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public void consultarExames(){
		// TODO - Chamar Modal da Estória #36882 - Sem sprint definida até o momento.
		LOG.info("Chamar Modal para Estória #36882");
	}
	
	public void evolucao(){
		// TODO - Redirecionar para Estória #28134 - Sem sprint definida até o momento.
		LOG.info("Redirecionar para Estória #28134");
	}
	
	public void placar(){
		// TODO - Redirecionar para Estória #25494 - Sem sprint definida até o momento.
		LOG.info("Redirecionar para Estória #25494");
	}
	
	public ListaPacientesEmergenciaPaginatorController getListaPacientesEmergenciaPaginatorController() {
		return listaPacientesEmergenciaPaginatorController;
	}

	public void setListaPacientesEmergenciaPaginatorController(
			ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController) {
		this.listaPacientesEmergenciaPaginatorController = listaPacientesEmergenciaPaginatorController;
	}

	public List<PacienteEmAtendimentoVO> getPacientesEmAtendimentoList() {
		return pacientesEmAtendimentoList;
	}

	public void setPacientesEmAtendimentoList(
			List<PacienteEmAtendimentoVO> pacientesEmAtendimentoList) {
		this.pacientesEmAtendimentoList = pacientesEmAtendimentoList;
	}

	public PacienteEmAtendimentoVO getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(PacienteEmAtendimentoVO pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}
	
	public Short getSeqpSelecionado() {
		return seqpSelecionado;
	}

	public void setSeqpSelecionado(Short seqpSelecionado) {
		this.seqpSelecionado = seqpSelecionado;
	}

	public String getDthrMovimento() {
		return dthrMovimento;
	}

	public void setDthrMovimento(String dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public static String getTelaOrigem() {
		return TELA_ORIGEM;
	}

	public static String getAbaOrigem() {
		return ABA_ORIGEM;
	}
	
	protected Boolean getVisualizarPrescricaoMedicaPerinatologia() {
		return visualizarPrescricaoMedicaPerinatologia;
	}

	protected void setVisualizarPrescricaoMedicaPerinatologia(Boolean visualizarPrescricaoMedicaPerinatologia) {
		this.visualizarPrescricaoMedicaPerinatologia = visualizarPrescricaoMedicaPerinatologia;
	}

	protected Boolean getExecutarPrescricaoMedicaPerinatologia() {
		return executarPrescricaoMedicaPerinatologia;
	}

	protected void setExecutarPrescricaoMedicaPerinatologia(Boolean executarPrescricaoMedicaPerinatologia) {
		this.executarPrescricaoMedicaPerinatologia = executarPrescricaoMedicaPerinatologia;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getAteSeq() {
		return ateSeq;
	}

	public void setAteSeq(Integer ateSeq) {
		this.ateSeq = ateSeq;
	}

	public Boolean getRetornoTelaPrescricao() {
		return retornoTelaPrescricao;
	}

	public void setRetornoTelaPrescricao(Boolean retornoTelaPrescricao) {
		this.retornoTelaPrescricao = retornoTelaPrescricao;
	}

	public boolean isExibeModalAutenticacao() {
		return exibeModalAutenticacao;
	}

	public void setExibeModalAutenticacao(boolean exibeModalAutenticacao) {
		this.exibeModalAutenticacao = exibeModalAutenticacao;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}	
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public ServidorIdVO getServidorIdVO() {
		return servidorIdVO;
	}
	
	public void setServidorIdVO(ServidorIdVO servidorIdVO) {
		this.servidorIdVO = servidorIdVO;
	}

	public static String getRedirectPendenciaAssinatura() {
		return REDIRECT_PENDENCIA_ASSINATURA;
	}

	public static String getRedirectImpressaoRelatorio() {
		return REDIRECT_IMPRESSAO_RELATORIO;
	}
	
	public String redirecionaReceitas(){
		return PAGE_PACIENTES_AGENDADOS_RECEITAS;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public MamUnidAtendem getMamUnidAtendemEmAtendimento() {
		return mamUnidAtendemEmAtendimento;
	}

	public void setMamUnidAtendemEmAtendimento(
			MamUnidAtendem mamUnidAtendemEmAtendimento) {
		this.mamUnidAtendemEmAtendimento = mamUnidAtendemEmAtendimento;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getIdadeFormatadaPaciente() {
		return idadeFormatadaPaciente;
	}

	public void setIdadeFormatadaPaciente(String idadeFormatadaPaciente) {
		this.idadeFormatadaPaciente = idadeFormatadaPaciente;
	}
	
	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public boolean isEmergenciaCustom() {
		return emergenciaCustom;
	}

	public void setEmergenciaCustom(boolean emergenciaCustom) {
		this.emergenciaCustom = emergenciaCustom;
	}

	public PacienteEmAtendimentoVO getPacienteSelecionadoAtendimento() {
		return pacienteSelecionadoAtendimento;
	}

	public void setPacienteSelecionadoAtendimento(
			PacienteEmAtendimentoVO pacienteSelecionadoAtendimento) {
		this.pacienteSelecionadoAtendimento = pacienteSelecionadoAtendimento;
	}
}