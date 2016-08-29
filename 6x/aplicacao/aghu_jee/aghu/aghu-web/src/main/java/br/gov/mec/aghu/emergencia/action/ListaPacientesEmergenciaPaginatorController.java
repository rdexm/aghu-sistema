package br.gov.mec.aghu.emergencia.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.vo.MicroComputador;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.AtenderPacientesEvolucaoController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.perinatologia.action.PesquisaGestacaoController;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaUnidadeVO;
import br.gov.mec.aghu.emergencia.vo.IntegracaoEmergenciaAghuAGHWebVO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.service.ServiceException;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class ListaPacientesEmergenciaPaginatorController  extends ActionController implements ActionPaginator {
	
	private static final Integer ABA_RECEPCAO = 0;
	private static final Integer ABA_ACOLHIMENTO = 1;
	private static final Integer ABA_AGUARDANDO = 2;
	private static final Integer ABA_EM_ATENDIMENTO = 3;
	private static final Integer ABA_ATENDIDOS = 4;
	private static final String TAB_0 = "abaRecepcao";
	private static final String TAB_1 = "abaAcolhimento";
	private static final String TAB_2 = "abaAguardando";
	private static final String TAB_3 = "abaEmAtendimento";
	private static final String TAB_4 = "abaAtendidos";
	private static final long serialVersionUID = -1712613170642413556L;
	private static final String REDIRECIONA_REALIZAR_ACOLHIMENTO = "realizarAcolhimentoPacienteCRUD";
	private static final String REDIRECIONA_INCLUIR_PACIENTE = "paciente-cadastroPaciente";
	private static final String PESQUISAR_GESTACAO = "/pages/perinatologia/pesquisaGestacoesList.xhtml";
	private static final String PAGE_PESQUISAR_PACIENTES_EVOLUCAO = "ambulatorio-atenderPacientesEvolucao";
	private static final String PAGE_PRESCRICAOMEDICA_VERIFICA_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";

	private static final Enum[] leftJoin = { AacConsultas.Fields.PACIENTE, AacConsultas.Fields.ATENDIMENTOS};
	
	private Integer nroConsulta;
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@Inject
	private SistemaImpressao sistemaImpressao;
	@Inject
	private RealizarAcolhimentoPacienteController realizarAcolhimentoPacienteController;
	
	@Inject
	private PesquisaGestacaoController pesquisaGestacaoController;
	
	@Inject
	private PacientesEmergenciaAbaEmAtendimentoController pacientesEmergenciaAbaEmAtendimentoController;
	
	@Inject
	private PacientesEmergenciaAbaAtendidosController pacientesEmergenciaAbaAtendidosController;
	
	@Inject
	private AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	@Inject @Paginator
	private DynamicDataModel<Paciente> dataModel;
	
	//selecao aba recepcao
	private Paciente selecao;
	//selecao aba acolhimento
	private PacienteAcolhimentoVO selecaoAcolhimento;
	
	private MamUnidAtendem mamUnidAtendem;
	private Paciente paciente;
	private Integer abaSelecionada;
	private MicroComputador computador;
	private Short unfSeqMicroComputador;
	private String hostName = "Não foi possível obter o hostName.";
	private PacienteFiltro filtro = new PacienteFiltro();
	private boolean unidadeRecepcao;
	//Permissões
	private boolean permConsultarAcolhimento;
	private boolean permRealizarAcolhimento;
	private boolean permVisualizarAcolhimento;
	private boolean permPesquisarPacientesRecepcaoEmergencia;
	private boolean permListarPacientesAguardandoAtendimentoEmergencia;
	private boolean permListarPacientesDoCentroObstetrico;
	private boolean permAtenderPacienteCentroObstetrico;
	private boolean permEmergenciaObstetrica;
	private boolean permVisualizarPacientesAtendidos;
	private boolean permAtenderPacienteEmergencia;
	private boolean exibirModalResponsavelPaciente = false;
	private boolean validarExibirModalResponsavelPaciente;
	private boolean fecharModal = true;
	private String nomeResponsavel;
	private List<PacienteAcolhimentoVO> pacientesAcolhimentoList = new ArrayList<PacienteAcolhimentoVO>();
	private Integer pacientesAcolhimentoListSize;
	private List<MamPacientesAguardandoAtendimentoVO> pacientesAguardandoAtendimentoList = new ArrayList<MamPacientesAguardandoAtendimentoVO>();
	private Integer pacientesAguardandoAtendimentoListSize;
	private EspecialidadeEmergenciaUnidadeVO especialidadeVO;
	private Short espSeq;
	private Boolean unidadeInformatizada = false;
	private Boolean primeiraExecucaoTela = true;
	private IntegracaoEmergenciaAghuAGHWebVO integracaoAghWeb;
	private Boolean integrarAghuAghWeb = Boolean.FALSE;
	List<EspecialidadeEmergenciaUnidadeVO> listaEspecialidadeEmergencia = new ArrayList<EspecialidadeEmergenciaUnidadeVO>();
	//# 28133
	private Boolean retornoTelaPrescricao = Boolean.FALSE; 
	private Short unfSeq;
	private Integer ateSeq; 
	
	private AacConsultas consultaSelecionada;
	
	private boolean emergenciaCustom;
	
	private Boolean prescricaoAmbulatorialAtiva = false;
	private Short unfSeqRedirecionaPM = -1;
	private Integer atdSeqRedirecionaPM = -1;

	@PostConstruct
	public void init() {
		begin(conversation);
		this.filtro.setRespeitarOrdem(Boolean.TRUE);
		this.setAbaSelecionada(ABA_RECEPCAO);

		this.permConsultarAcolhimento = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),
				"consultarPacientesAcolhimentoEmergencia", "visualizar");
		this.permRealizarAcolhimento = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "realizarAcolhimento", "executar");
		this.permVisualizarAcolhimento = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarAcolhimento", "visualizar");
		this.permPesquisarPacientesRecepcaoEmergencia = false;
		this.permPesquisarPacientesRecepcaoEmergencia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"pesquisarPacientesRecepcaoEmergencia", "pesquisar");

		this.permListarPacientesAguardandoAtendimentoEmergencia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"listarPacientesAguardandoAtendimentoEmergencia", "listar");

		this.permListarPacientesAguardandoAtendimentoEmergencia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"listarPacientesAguardandoAtendimentoEmergencia", "listar");

		this.permListarPacientesDoCentroObstetrico = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"listarPacientesDoCentroObstetrico", "pesquisar");

		this.permAtenderPacienteCentroObstetrico = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"atenderPacienteCentroObstetrico", "executar");

		this.permVisualizarPacientesAtendidos = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),"visualizarListaPacientesAtendidosEmergencia", "visualizar");

		this.permAtenderPacienteEmergencia = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "atenderPacienteEmergencia","executar");
		
		try {
			this.emergenciaCustom = (parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM) != null && parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EMERG_CUSTOM).equals("S"));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		String abaDestinoParm = getRequestParameter("abaDestino");
		if (StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase("abaEmAtendimento")) {
			this.setAbaSelecionada(ABA_EM_ATENDIMENTO);

			String retornoTelaPrescricaoParm = getRequestParameter("retornoTelaPrescricao");
			if (StringUtils.isNotBlank(retornoTelaPrescricaoParm)) {
				this.retornoTelaPrescricao = Boolean.valueOf(retornoTelaPrescricaoParm);
			}

			String unfSeqParm = getRequestParameter("unfSeq");
			if (StringUtils.isNotBlank(unfSeqParm)) {
				this.unfSeq = Short.valueOf(unfSeqParm);
			}

			String ateSeqParm = getRequestParameter("ateSeq");
			if (StringUtils.isNotBlank(ateSeqParm)) {
				this.ateSeq = Integer.valueOf(ateSeqParm);
			}

			if (retornoTelaPrescricao) {
				this.mamUnidAtendem = emergenciaFacade.pesquisarUnidadeFuncionalAtivaPorUnfSeq(this.unfSeq);
				pacientesEmergenciaAbaEmAtendimentoController.setMamUnidAtendemEmAtendimento(this.mamUnidAtendem);
				pesquisarPacientesEmAtendimento();
			}

		} else {
			this.setAbaSelecionada(ABA_RECEPCAO);
			
	    	try {
	    		hostName = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				Log.error("Não foi possível obter o hostname. Exceção capturada:" + e.getCause()
						+ " " + e.getMessage());
			}
		    
	    	FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) fc
					.getExternalContext().getRequest();
			try {
				InetAddress addr = InetAddress.getByName(request.getRemoteAddr());
				hostName = addr.getHostName();
				if(hostName != null && hostName.contains((".hcpa"))) {
					hostName = hostName.substring(0,
							hostName.lastIndexOf('.'));
				}
			} catch (UnknownHostException e) {
				Log.error("Não foi possível obter o hostname. Exceção capturada:" + e.getCause()
						+ " " + e.getMessage());
			}

			try {
				this.computador = this.emergenciaFacade.obterMicroComputadorPorNomeOuIPException(hostName);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return;
			}
			this.unfSeqMicroComputador = computador.getUnfSeq();

			if (this.unfSeqMicroComputador != null) {

				MamUnidAtendem unidadeFuncional = this.emergenciaFacade.pesquisarUnidadeFuncionalAtivaPorUnfSeq(this.unfSeqMicroComputador);

				if (unidadeFuncional != null) {
					this.mamUnidAtendem = unidadeFuncional;
					if (this.mamUnidAtendem.getIndTriagem().equals(Boolean.TRUE)) {
						this.unidadeRecepcao = false;
						if (this.permConsultarAcolhimento) {
							this.setAbaSelecionada(ABA_ACOLHIMENTO);
							pesquisarPacientesAcolhimento();
						}
					} else {
						this.unidadeRecepcao = true;
						this.setAbaSelecionada(ABA_RECEPCAO);
					}
				} else {
					List<Short> listUnfSeqTriagem = this.emergenciaFacade.pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(true);

					Short unidadeTriagem = null;
					try {
						unidadeTriagem = this.emergenciaFacade
								.pesquisarUnidadeFuncionalTriagemRecepcao(listUnfSeqTriagem, this.unfSeqMicroComputador);
					} catch (ApplicationBusinessException e1) {
						apresentarExcecaoNegocio(e1);
						return;
					}

					if (unidadeTriagem != null) {
						this.mamUnidAtendem = this.emergenciaFacade.pesquisarUnidadeFuncionalAtivaPorUnfSeq(unidadeTriagem);
						this.unidadeRecepcao = false;
						if (this.permConsultarAcolhimento) {
							this.setAbaSelecionada(ABA_ACOLHIMENTO);
							pesquisarPacientesAcolhimento();
						}

					} else {
						this.unidadeRecepcao = true;
						this.setAbaSelecionada(ABA_RECEPCAO);

						List<Short> listUnfSeqRecepcao = this.emergenciaFacade.pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(true);

						Short unidadeRecepcao = null;
						try {
							unidadeRecepcao = this.emergenciaFacade.pesquisarUnidadeFuncionalTriagemRecepcao(listUnfSeqRecepcao,
									this.unfSeqMicroComputador);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
							return;
						}

						if (unidadeRecepcao != null) {
							this.mamUnidAtendem = this.emergenciaFacade.pesquisarUnidadeFuncionalAtivaPorUnfSeq(unidadeRecepcao);
						}
					}
				}
			}
		}

		this.obterPermissaoEmergenciaObtetrica();
	}
	
	public void refreshPesquisar() {
		if(!primeiraExecucaoTela) {
			if (this.getAbaSelecionada().equals(ABA_RECEPCAO)) {
				pesquisar();
				
			} else if (this.getAbaSelecionada().equals(ABA_ACOLHIMENTO)) {
				pesquisarPacientesAcolhimento();
				
			} else if (this.getAbaSelecionada().equals(ABA_AGUARDANDO)){
				pesquisarPacientesAguardandoAtendimento();
				
			} else if (this.getAbaSelecionada().equals(ABA_EM_ATENDIMENTO)){
				pesquisarPacientesEmAtendimento();
			} else if (this.getAbaSelecionada().equals(ABA_ATENDIDOS)){
				pesquisarPacientesAtendidos();
				
			}
		}
		primeiraExecucaoTela = false;
		if(mamUnidAtendem != null){
			posSelecionarUnidade();
		}
	}
	
	public void posSelecionarUnidade() {
		pacientesEmergenciaAbaEmAtendimentoController.setMamUnidAtendemEmAtendimento(this.mamUnidAtendem);
		if (this.getAbaSelecionada().equals(ABA_RECEPCAO)) {
			obterPermissaoEmergenciaObtetrica();
		} else if (this.getAbaSelecionada().equals(ABA_ACOLHIMENTO)) {
			pesquisarPacientesAcolhimento();
		} else if (this.getAbaSelecionada().equals(ABA_AGUARDANDO)) {
			pesquisarPacientesAguardandoAtendimento();
		} else if (this.getAbaSelecionada().equals(ABA_EM_ATENDIMENTO)) {
			pesquisarPacientesEmAtendimento();
		} else if (this.getAbaSelecionada().equals(ABA_ATENDIDOS)) {
			pesquisarPacientesAtendidos();
		}
	}
	
	public void posDeletarUnidade() {
		if (this.getAbaSelecionada().equals(ABA_ACOLHIMENTO)) {
			pesquisarPacientesAcolhimento();
		} else if (this.getAbaSelecionada().equals(ABA_AGUARDANDO)) {
			pesquisarPacientesAguardandoAtendimento();
		} else if (this.getAbaSelecionada().equals(ABA_EM_ATENDIMENTO)) {
			pesquisarPacientesEmAtendimento();
		} else if (this.getAbaSelecionada().equals(ABA_ATENDIDOS)) {
			pesquisarPacientesAtendidos();
		}
	}
	
	public void pesquisar() {
		if (this.filtro.getProntuario() == null && this.filtro.getCodigo() == null
				&& (this.filtro.getNome() == null || this.filtro.getNome().isEmpty())
				&& (this.filtro.getNomeMae() == null || this.filtro.getNomeMae().isEmpty())) {
			
			limparPesquisa();
			
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ERRO_PESQUISA_RECEPCAO_OBRIGATORIO");
			return;
		}
		this.dataModel.reiniciarPaginator();
	}
	
	public void pesquisarPacientesAcolhimento() {
		if (this.mamUnidAtendem != null) {
			try {
				pacientesAcolhimentoList = this.emergenciaFacade.listarPacientesAcolhimento(mamUnidAtendem.getSeq());
				pacientesAcolhimentoListSize = pacientesAcolhimentoList.size();
				unidadeInformatizada = this.emergenciaFacade.validaUnidadeFuncionalInformatizada(mamUnidAtendem.getSeq());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			pacientesAcolhimentoList = new ArrayList<PacienteAcolhimentoVO>();
			pacientesAcolhimentoListSize = 0;
			unidadeInformatizada = false;
		}
		this.obterPermissaoEmergenciaObtetrica();
	}
	
	public void pesquisarPacientesEmAtendimento() {
		this.obterPermissaoEmergenciaObtetrica();
		this.pacientesEmergenciaAbaEmAtendimentoController.setRetornoTelaPrescricao(retornoTelaPrescricao);
		this.pacientesEmergenciaAbaEmAtendimentoController.pesquisarPacientesEmAtendimento();
	}

	public void pesquisarPacientesAtendidos() {
		listaEspecialidadeEmergencia();
		this.pacientesEmergenciaAbaAtendidosController.pesquisarPacientesAtendidos();
	}

	public void obterPermissaoEmergenciaObtetrica(){
		
		try {
				if(this.mamUnidAtendem != null){
					if(this.emergenciaFacade.verificarUnfEmergObstetrica(this.mamUnidAtendem.getUnfSeq())){
						this.setPermEmergenciaObstetrica(Boolean.TRUE);
					} else {
						this.setPermEmergenciaObstetrica(Boolean.FALSE);
					}
				} else {
					this.setPermEmergenciaObstetrica(Boolean.FALSE);
				}
		
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
		}
	}

	public void limparPesquisa() {
		this.filtro = new PacienteFiltro();
		this.filtro.setRespeitarOrdem(Boolean.TRUE);
		this.selecao = null;
		this.selecaoAcolhimento = null;
		this.dataModel.limparPesquisa();
		unfSeqRedirecionaPM = -1;
		atdSeqRedirecionaPM = -1;
	}

	// ### Paginação ###
	@Override
	public Long recuperarCount() {
		if (this.filtro.getProntuario() != null || this.filtro.getCodigo() != null) {
			List<Paciente> listPac = null;
			try {
				listPac = this.emergenciaFacade.obterPacientePorCodigoOuProntuario(this.filtro);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			if (listPac != null && !listPac.isEmpty()) {
				return 1L;
			}
			
		} else if ((this.filtro.getNome() != null && !this.filtro.getNome().isEmpty())
				|| (this.filtro.getNomeMae() != null && !this.filtro.getNomeMae().isEmpty())) {
			try {
				return this.emergenciaFacade.pesquisarPorFonemasCount(this.filtro);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Paciente> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		this.filtro.setFirstResult(firstResult);
		this.filtro.setMaxResults(maxResults);
		if (this.filtro.getProntuario() != null || this.filtro.getCodigo() != null) {
			try {
				return this.emergenciaFacade.obterPacientePorCodigoOuProntuario(filtro);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else if ((this.filtro.getNome() != null && !this.filtro.getNome().isEmpty())
				|| (this.filtro.getNomeMae() != null && !this.filtro.getNomeMae().isEmpty())) {
			try {
				return this.emergenciaFacade.pesquisarPorFonemas(this.filtro);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	
	public List<MamUnidAtendem> pesquisarUnidadeFuncional(String objPesquisa) {
		return  this.returnSGWithCount(this.emergenciaFacade.listarUnidadesFuncionais(objPesquisa, this.unidadeRecepcao, "unfSeq", true),pesquisarUnidadeFuncionalCount(objPesquisa));
	}
	
	public Long pesquisarUnidadeFuncionalCount(String objPesquisa) {
		return this.emergenciaFacade.listarUnidadesFuncionaisCount(objPesquisa, this.unidadeRecepcao, true);
	}
	
	public String obterProntuarioFormatado(Integer prontuario) {
		if (prontuario != null && prontuario > 0) {
			return CoreUtil.formataProntuario(prontuario);
		}
		return null;
	}
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	

	public void encaminharParaAcolhimento() throws ApplicationBusinessException {
		setExibirModalResponsavelPaciente(false);
		if (isValidarExibirModalResponsavelPaciente()) {
			setNomeResponsavel(null);
			// valida se deve exibir a modal
			try {
				if (exibirModalPacienteMenor()) {
					setExibirModalResponsavelPaciente(true);
					setValidarExibirModalResponsavelPaciente(false);
					this.openDialog("modalResponsavelPacienteWG");
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				this.fecharModal = false;
				return;
				
			}
		}
		
		// encaminha paciente para acolhimento
		if (!isExibirModalResponsavelPaciente()) {
			Boolean imprimirPulseira = false;
			try {
				this.emergenciaFacade
						.encaminharPacienteAcolhimento(this.paciente, this.mamUnidAtendem, this.unfSeqMicroComputador, this.hostName, this.nomeResponsavel);
				//this.fecharModal = true;
				this.closeDialog("modalResponsavelPacienteWG");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				this.fecharModal = false;
				return;
			}
			if (imprimirPulseira.equals(Boolean.TRUE)) { // Chama a estória #29814 //TODO
				imprimeEtiquetas();
			}
			pesquisar();
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_RECEPCAO_PACIENTE");
		}
	}
	
	public void reimpressaoPulseira(Integer codigoPaciente) throws ApplicationBusinessException{
		Paciente pacAcolhido = null;
		try {
			pacAcolhido = this.emergenciaFacade
					.reimpressaoPulseiraPacEmergencia(codigoPaciente, this.mamUnidAtendem);
			this.paciente = pacAcolhido;
			imprimeEtiquetas();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	/**
	 * Verifica se deve exibir modal para paciente menor
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private Boolean exibirModalPacienteMenor() throws ApplicationBusinessException{
		return this.emergenciaFacade.exibirModalPacienteMenor(this.paciente,  this.mamUnidAtendem);
	}
	
	/**
	 * Chamada da geração do codigo para Zebra.
	 * @throws ApplicationBusinessException 
	 */
	private void imprimeEtiquetas() throws ApplicationBusinessException {
		String zpl = this.emergenciaFacade.gerarEtiquetaPulseira(this.paciente.getCodigo(), null);
		
		try {
			this.sistemaImpressao.imprimir(zpl,  aghuFacade.obterAghUnidFuncionaisPorUnfSeq(this.mamUnidAtendem.getSeq()),TipoDocumentoImpressao.ETIQUETA_PULSEIRA_PACIENTE);

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS");
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String colorirTabela(PacienteAcolhimentoVO item) {
		if (item != null && item.getIndGermesMulti() != null) {
			if (item.getIndGermesMulti().equals(Boolean.TRUE)) {
				return "#01FFFF";
			} else if (item.getIndTransferido().equals(Boolean.TRUE)) {
				return "#4FC259";
			}
		}
		return "";
	}
	
	public String realizarAcolhimento(Long trgSeq, Integer codigo) throws ApplicationBusinessException {
		try {
			this.emergenciaFacade.validarAcolhimentoPaciente(trgSeq, hostName, mamUnidAtendem);
			realizarAcolhimentoPacienteController.setTrgSeq(trgSeq);
			realizarAcolhimentoPacienteController.setPacCodigo(codigo);
			realizarAcolhimentoPacienteController.setPaciente(paciente);
			realizarAcolhimentoPacienteController.setReclassificar(Boolean.FALSE);
			realizarAcolhimentoPacienteController.preparaTela();
			return REDIRECIONA_REALIZAR_ACOLHIMENTO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
	
	public String realizarReclassificar(Long trgSeq, Integer codigo) throws ApplicationBusinessException {
		try {
			this.emergenciaFacade.validarAcolhimentoPaciente(trgSeq, hostName, mamUnidAtendem);
			realizarAcolhimentoPacienteController.setTrgSeq(trgSeq);
			realizarAcolhimentoPacienteController.setPacCodigo(codigo);
			realizarAcolhimentoPacienteController.setPaciente(paciente);
			realizarAcolhimentoPacienteController.setReclassificar(Boolean.TRUE);
			realizarAcolhimentoPacienteController.preparaTela();
			return REDIRECIONA_REALIZAR_ACOLHIMENTO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
	
	public String redirecionarIncluirPaciente() {
		return REDIRECIONA_INCLUIR_PACIENTE;
	}
	
	public void retornoAcolhimento(Severity severity, String msgKey, String val, Boolean isServicoCPIndisponivel) {
		apresentarMsgNegocio(severity, msgKey, val);	
		if (isServicoCPIndisponivel.equals(Boolean.TRUE)) {
			apresentarMsgNegocio(Severity.WARN, "MAM_ERRO_SERVICO_CONTROLE_PACIENTE_INDISPONIVEL");
		}
	}
	
	public boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo){
		try {
			return this.emergenciaFacade.verificarExisteSinalVitalPorPaciente(pacCodigo);
		} catch (ServiceException e) {
			return false;
		}
	}
	
	public void tabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(TAB_0.equals(event.getTab().getId())) {
				abaSelecionada = ABA_RECEPCAO;
				obterPermissaoEmergenciaObtetrica();
			} else if(TAB_1.equals(event.getTab().getId())) {
				abaSelecionada = ABA_ACOLHIMENTO;
				pesquisarPacientesAcolhimento();
			}  else if(TAB_2.equals(event.getTab().getId())) {
				abaSelecionada = ABA_AGUARDANDO;
				pesquisarPacientesAguardandoAtendimento();
			} else if(TAB_3.equals(event.getTab().getId())) {
				abaSelecionada = ABA_EM_ATENDIMENTO;
				pesquisarPacientesEmAtendimento();
			} else if(TAB_4.equals(event.getTab().getId())) {
				abaSelecionada = ABA_ATENDIDOS;
				pesquisarPacientesAtendidos();
			}
		}
	}
	
	public String manterControlesPaciente() {
		return "controlepaciente-manterRegistros";
	}
	
	public DynamicDataModel<Paciente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Paciente> dataModel) {
		this.dataModel = dataModel;
	}

	public MamUnidAtendem getMamUnidAtendem() {
		return mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}

	public IEmergenciaFacade getEmergenciaFacade() {
		return emergenciaFacade;
	}

	public void setEmergenciaFacade(IEmergenciaFacade emergenciaFacade) {
		this.emergenciaFacade = emergenciaFacade;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public MicroComputador getComputador() {
		return computador;
	}

	public void setComputador(MicroComputador computador) {
		this.computador = computador;
	}

	public Short getUnfSeqMicroComputador() {
		return unfSeqMicroComputador;
	}

	public void setUnfSeqMicroComputador(Short unfSeqMicroComputador) {
		this.unfSeqMicroComputador = unfSeqMicroComputador;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public PacienteFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(PacienteFiltro filtro) {
		this.filtro = filtro;
	}

	public boolean isUnidadeRecepcao() {
		return unidadeRecepcao;
	}

	public void setUnidadeRecepcao(boolean unidadeRecepcao) {
		this.unidadeRecepcao = unidadeRecepcao;
	}

	public boolean isPermConsultarAcolhimento() {
		return permConsultarAcolhimento;
	}

	public void setPermConsultarAcolhimento(boolean permConsultarAcolhimento) {
		this.permConsultarAcolhimento = permConsultarAcolhimento;
	}

	public boolean isPermRealizarAcolhimento() {
		return permRealizarAcolhimento;
	}

	public void setPermRealizarAcolhimento(boolean permRealizarAcolhimento) {
		this.permRealizarAcolhimento = permRealizarAcolhimento;
	}

	public boolean isPermVisualizarAcolhimento() {
		return permVisualizarAcolhimento;
	}

	public void setPermVisualizarAcolhimento(boolean permVisualizarAcolhimento) {
		this.permVisualizarAcolhimento = permVisualizarAcolhimento;
	}

	public boolean isPermPesquisarPacientesRecepcaoEmergencia() {
		return permPesquisarPacientesRecepcaoEmergencia;
	}

	public void setPermPesquisarPacientesRecepcaoEmergencia(
			boolean permPesquisarPacientesRecepcaoEmergencia) {
		this.permPesquisarPacientesRecepcaoEmergencia = permPesquisarPacientesRecepcaoEmergencia;
	}

	public List<PacienteAcolhimentoVO> getPacientesAcolhimentoList() {
		return pacientesAcolhimentoList;
	}

	public void setPacientesAcolhimentoList(
			List<PacienteAcolhimentoVO> pacientesAcolhimentoList) {
		this.pacientesAcolhimentoList = pacientesAcolhimentoList;
	}

	public Integer getPacientesAcolhimentoListSize() {
		return pacientesAcolhimentoListSize;
	}

	public void setPacientesAcolhimentoListSize(Integer pacientesAcolhimentoListSize) {
		this.pacientesAcolhimentoListSize = pacientesAcolhimentoListSize;
	}
	
	public boolean isPermListarPacientesAguardandoAtendimentoEmergencia() {
		return permListarPacientesAguardandoAtendimentoEmergencia;
	}

	public void setPermListarPacientesAguardandoAtendimentoEmergencia(
			boolean permListarPacientesAguardandoAtendimentoEmergencia) {
		this.permListarPacientesAguardandoAtendimentoEmergencia = permListarPacientesAguardandoAtendimentoEmergencia;
	}
		
	public boolean isPermAtenderPacienteCentroObstetrico() {
		return permAtenderPacienteCentroObstetrico;
	}

	public void setPermAtenderPacienteCentroObstetrico(
			boolean permAtenderPacienteCentroObstetrico) {
		this.permAtenderPacienteCentroObstetrico = permAtenderPacienteCentroObstetrico;
	}

	public boolean isPermListarPacientesDoCentroObstetrico() {
		return permListarPacientesDoCentroObstetrico;
	}

	public void setPermListarPacientesDoCentroObstetrico(
			boolean permListarPacientesDoCentroObstetrico) {
		this.permListarPacientesDoCentroObstetrico = permListarPacientesDoCentroObstetrico;
	}

	public boolean isPermEmergenciaObstetrica() {
		return permEmergenciaObstetrica;
	}

	public void setPermEmergenciaObstetrica(boolean permEmergenciaObstetrica) {
		this.permEmergenciaObstetrica = permEmergenciaObstetrica;
	}

	public void listaEspecialidadeEmergencia(){
		if (this.mamUnidAtendem != null) {
			try {
				listaEspecialidadeEmergencia = this.emergenciaFacade.obterEspecialidadesEmergenciaAssociadasUnidades(this.mamUnidAtendem.getSeq());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		}
	
	}
	
	public void pesquisarPacientesAguardandoAtendimento() {
		if (this.mamUnidAtendem != null) {
			try {
				integrarAghuAghWeb = this.emergenciaFacade.verificaIntegracaoAghuAghWebEmergencia();
				listaEspecialidadeEmergencia();
				this.abaSelecionada = ABA_AGUARDANDO;
				if(getEspSeq() == null){
					pacientesAguardandoAtendimentoList = this.emergenciaFacade.listarPacientesAguardandoAtendimentoEmergencia(this.mamUnidAtendem.getSeq(), null);
				}else{
					pacientesAguardandoAtendimentoList = this.emergenciaFacade.listarPacientesAguardandoAtendimentoEmergencia(this.mamUnidAtendem.getSeq(), getEspSeq());
				}
				this.pacientesAguardandoAtendimentoListSize = this.pacientesAguardandoAtendimentoList.size();
				unidadeInformatizada = this.emergenciaFacade.validaUnidadeFuncionalInformatizada(mamUnidAtendem.getSeq());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			pacientesAguardandoAtendimentoList = new ArrayList<MamPacientesAguardandoAtendimentoVO>();
			pacientesAguardandoAtendimentoListSize = 0;
			unidadeInformatizada = false;
		}
		this.obterPermissaoEmergenciaObtetrica();
	}
	
	public String iniciarAtendimento(MamPacientesAguardandoAtendimentoVO pacienteAguardando) {
		try {
			// #54033 - Permitir atender paciente aguardando conforme parametrização
			if(emergenciaCustom) {
				this.emergenciaFacade.validarSeTemPermissaoRealizarEvolucaoAmbulatorio();
				Long retorno = this.emergenciaFacade.iniciaAtendimento(pacienteAguardando.getTrgSeq(), pacienteAguardando.getConNumero(), pacienteAguardando.getAtdSeq(), 
						pacienteAguardando.getDthrInicio(), pacienteAguardando.getEspSeq(), false, hostName);
				if(retorno!=null) {
					consultaSelecionada = ambulatorioFacade.obterConsultaPorNumero(pacienteAguardando.getConNumero(), null, leftJoin);
					List<AghAtendimentos> listaAtendimentoPorConsulta = aghuFacade.listarAtendimentosPorNumeroConsultaOrdenado(pacienteAguardando.getConNumero());
					AghAtendimentos atd = null;
					if (!listaAtendimentoPorConsulta.isEmpty()) {
						atd = listaAtendimentoPorConsulta.get(0);
						unfSeqRedirecionaPM = atd.getUnidadeFuncional().getSeq();
						atdSeqRedirecionaPM = atd.getSeq();
					}
					if (atd!= null){
						List<AghAtendimentos> atendimentosPaciente = ambulatorioFacade
								.pesquisarAtendimentoParaPrescricaoMedica(
										consultaSelecionada.getPaciente().getCodigo(),atd.getSeq());
						prescricaoAmbulatorialAtiva = !atendimentosPaciente.isEmpty();
					}
					atenderPacientesEvolucaoController.setConsultaSelecionada(consultaSelecionada);
					atenderPacientesEvolucaoController.setPaciente(consultaSelecionada.getPaciente());
					atenderPacientesEvolucaoController.setIdadeFormatada(emergenciaFacade.getIdadeFormatada(consultaSelecionada.getPaciente().getDtNascimento()));
					atenderPacientesEvolucaoController.setCameFrom("listarPacientesEmergenciaAbaAguardando");
					this.emergenciaFacade.atualizarSituacaoPacienteEmConsulta(pacienteAguardando.getTrgSeq(),hostName);
					return PAGE_PESQUISAR_PACIENTES_EVOLUCAO;
				}
			}
						
			Boolean atenderMedicoAghWeb = (parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ATENDER_MEDICO_AGHWEB) != null && parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ATENDER_MEDICO_AGHWEB).equals("S"));
			
			integracaoAghWeb = this.emergenciaFacade.iniciarAtendimentoPaciente(pacienteAguardando.getAtdSeq(), 
			pacienteAguardando.getConNumero(), pacienteAguardando.getTrgSeq(), pacienteAguardando.getDthrInicio(), pacienteAguardando.getUnfSeq(), 
			pacienteAguardando.getEspSeq(), pacienteAguardando.getPacCodigo(), hostName, atenderMedicoAghWeb);
			this.pesquisarPacientesAguardandoAtendimento();			
			
			
			 if(integracaoAghWeb.getRedirecionaPesquisaGestacao() && !atenderMedicoAghWeb){
				return irGestacao(pacienteAguardando.getConNumero(), pacienteAguardando.getTrgSeq(),pacienteAguardando.getPacProntuario());
			}
			 return null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (ServiceException e) {
			apresentarMsgNegocio(e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
		
	}
	
	public String efetuarPrescricaoAmbulatorial(){
		Long unfPmeInf =  aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				unfSeqRedirecionaPM,	null,Boolean.TRUE,	Boolean.FALSE, Boolean.TRUE,
					ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
		if(unfPmeInf > 0){
				return PAGE_PRESCRICAOMEDICA_VERIFICA_PRESCRICAO_MEDICA;
		}else{
			apresentarMsgNegocio(
					Severity.ERROR,	"UNIDADE_FUNCIONAL_NAO_POSSUI_CARACTERISTICA",
					ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA.getDescricao());
		}
		return null;
	}
	
	public String obterTokenUsuarioLogado() {
		return super.obterTokenUsuarioLogado().toString();
	}
	
	public List<MamPacientesAguardandoAtendimentoVO> getPacientesAguardandoAtendimentoList() {
		return pacientesAguardandoAtendimentoList;
	}

	public void setPacientesAguardandoAtendimentoList(List<MamPacientesAguardandoAtendimentoVO> pacientesAguardandoAtendimentoList) {
		this.pacientesAguardandoAtendimentoList = pacientesAguardandoAtendimentoList;
	}

	public Integer getPacientesAguardandoAtendimentoListSize() {
		return pacientesAguardandoAtendimentoListSize;
	}

	public void setPacientesAguardandoAtendimentoListSize(Integer pacientesAguardandoAtendimentoListSize) {
		this.pacientesAguardandoAtendimentoListSize = pacientesAguardandoAtendimentoListSize;
	}

	public EspecialidadeEmergenciaUnidadeVO getEspecialidadeVO() {
		return especialidadeVO;
	}

	public void setEspecialidadeVO(EspecialidadeEmergenciaUnidadeVO especialidadeVO) {
		this.especialidadeVO = especialidadeVO;
	}
	
	public List<EspecialidadeEmergenciaUnidadeVO> getListaEspecialidadeEmergencia() {
		return listaEspecialidadeEmergencia;
	}

	public void setListaEspecialidadeEmergencia(List<EspecialidadeEmergenciaUnidadeVO> listaEspecialidadeEmergencia) {
		this.listaEspecialidadeEmergencia = listaEspecialidadeEmergencia;
	}

	public String colorirTabelaAguardando(boolean cor) {
		if (cor) {
			return "#01FFFF";
		} else {
			return "";
		}

	}
	
	public void editarPaciente() {
		this.redirecionarPaginaPorAjax("/pages/paciente/cadastroPaciente.xhtml");
	}
	
	private void redirecionarPaginaPorAjax(String caminhoPagina){
		try{
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, caminhoPagina)); 
			extContext.redirect(url);
		}
		catch (IOException e) {
			this.apresentarMsgNegocio("Erro ao editar paciente");			
		}
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	
	public String irGestacao(Integer numConsulta, Long trgSeq, Integer prontuario) {
		pesquisaGestacaoController.setConNumeroParam(numConsulta);
		pesquisaGestacaoController.setNroProntuario(prontuario);
		pesquisaGestacaoController.setAbaAguardando(true);
		pesquisaGestacaoController.setVoltarPara("listaPacientesEmergencia");
		pesquisaGestacaoController.setTrgSeq(trgSeq);
		return PESQUISAR_GESTACAO;
	}

	public Integer getNroConsulta() {
		return nroConsulta;
	}
	public void setNroConsulta(Integer nroConsulta) {
		this.nroConsulta = nroConsulta;
	}
	public Boolean getUnidadeInformatizada() {
		return unidadeInformatizada;
	}
	public void setUnidadeInformatizada(Boolean unidadeInformatizada) {
		this.unidadeInformatizada = unidadeInformatizada;
	}
	public boolean isExibirModalResponsavelPaciente() {
		return exibirModalResponsavelPaciente;
	}
	public void setExibirModalResponsavelPaciente(
			boolean exibirModalResponsavelPaciente) {
		this.exibirModalResponsavelPaciente = exibirModalResponsavelPaciente;
	}
	public boolean isValidarExibirModalResponsavelPaciente() {
		return validarExibirModalResponsavelPaciente;
	}
	public void setValidarExibirModalResponsavelPaciente(
			boolean validarExibirModalResponsavelPaciente) {
		this.validarExibirModalResponsavelPaciente = validarExibirModalResponsavelPaciente;
	}
	public boolean isFecharModal() {
		return fecharModal;
	}
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	public boolean isPermVisualizarPacientesAtendidos() {
		return permVisualizarPacientesAtendidos;
	}
	public void setPermVisualizarPacientesAtendidos(
			boolean permVisualizarPacientesAtendidos) {
		this.permVisualizarPacientesAtendidos = permVisualizarPacientesAtendidos;
	}
	public IntegracaoEmergenciaAghuAGHWebVO getIntegracaoAghWeb() {
		return integracaoAghWeb;
	}

	public void setIntegracaoAghWeb(
			IntegracaoEmergenciaAghuAGHWebVO integracaoAghWeb) {
		this.integracaoAghWeb = integracaoAghWeb;
	}
	public Boolean getIntegrarAghuAghWeb() {
		return integrarAghuAghWeb;
	}
	public void setIntegrarAghuAghWeb(Boolean integrarAghuAghWeb) {
		this.integrarAghuAghWeb = integrarAghuAghWeb;
	}

	public boolean isPermAtenderPacienteEmergencia() {
		return permAtenderPacienteEmergencia;
	}

	public void setPermAtenderPacienteEmergencia(
			boolean permAtenderPacienteEmergencia) {
		this.permAtenderPacienteEmergencia = permAtenderPacienteEmergencia;
	}
	public Boolean getRetornoTelaPrescricao() {
		return retornoTelaPrescricao;
	}
	public void setRetornoTelaPrescricao(Boolean retornoTelaPrescricao) {
		this.retornoTelaPrescricao = retornoTelaPrescricao;
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
	public Paciente getSelecao() {
		return selecao;
	}
	public void setSelecao(Paciente selecao) {
		this.selecao = selecao;
	}
	public PacienteAcolhimentoVO getSelecaoAcolhimento() {
		return selecaoAcolhimento;
	}
	public void setSelecaoAcolhimento(PacienteAcolhimentoVO selecaoAcolhimento) {
		this.selecaoAcolhimento = selecaoAcolhimento;
	}
	
	public boolean isEmergenciaCustom() {
		return emergenciaCustom;
	}

	public void setEmergenciaCustom(boolean emergenciaCustom) {
		this.emergenciaCustom = emergenciaCustom;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	
	public Boolean getPrescricaoAmbulatorialAtiva() {
		return prescricaoAmbulatorialAtiva;
	}

	
	public void setPrescricaoAmbulatorialAtiva(Boolean prescricaoAmbulatorialAtiva) {
		this.prescricaoAmbulatorialAtiva = prescricaoAmbulatorialAtiva;
	}

	
	public Short getUnfSeqRedirecionaPM() {
		return unfSeqRedirecionaPM;
	}

	
	public void setUnfSeqRedirecionaPM(Short unfSeqRedirecionaPM) {
		this.unfSeqRedirecionaPM = unfSeqRedirecionaPM;
	}

	
	public Integer getAtdSeqRedirecionaPM() {
		return atdSeqRedirecionaPM;
	}

	
	public void setAtdSeqRedirecionaPM(Integer atdSeqRedirecionaPM) {
		this.atdSeqRedirecionaPM = atdSeqRedirecionaPM;
	}

}