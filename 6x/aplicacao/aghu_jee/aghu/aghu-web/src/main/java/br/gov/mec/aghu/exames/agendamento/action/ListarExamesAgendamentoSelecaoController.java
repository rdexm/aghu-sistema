package br.gov.mec.aghu.exames.agendamento.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.action.RelatorioTicketExamesPacienteController;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller Listar Exames Para Agendamento Por Seleção
 * 
 * @author gzapalaglio
 *
 */

public class ListarExamesAgendamentoSelecaoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory
			.getLog(ListarExamesAgendamentoSelecaoController.class);

	private static final long serialVersionUID = 5358242748411206884L;

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private ConsultaHorarioLivreController consultaHorarioLivreController;

	@Inject
	private RelatorioTicketExamesPacienteController relatorioTicketExamesPacienteController;

	private static final String MSG_UNF_1 = "A Solicitação ";
	private static final String MSG_UNF_2 = " possui exames das seguintes Unidades Executoras:";
	private static final String MSG_UNF_3 = "Selecione uma unidade executora para agendar os exames correspondentes.";

	private AelUnidExecUsuario usuarioUnidadeExecutora;

	// Filtros utilizados na pesquisa
	private VAelSolicAtendsVO exameVO;

	private AipPacientes paciente;
	private Integer pacCodigoFonetica;
	private String pacNome;
	private Integer soeSeq;

	private AghUnidadesFuncionais unidadeExecutora;
	private Short seqUnidadeExecutora;
	private List<AgendamentoExameVO> solicitacoesExamesChecked;

	// Voltar
	private String voltarPara;

	// Solicitacao Selecionada
	private Integer solicitacaoSelecionada;
	private VAelSolicAtendsVO solicitacao;

	// Resultado da Pesquisa
	private List<VAelSolicAtendsVO> solicitacoesExames;

	private List<AgendamentoExameVO> examesAgendamentoSelecao;

	private Boolean todosExamesSelecionados = false;
	private Boolean habilitaBotaoPesquisar = false;
	private Boolean usuarioTemPermissaoTela = false;

	private List<Short> listaSeqUnidExamesSolicitados;

	private boolean origemSolicitacaoExames;
	private List<AgendamentoExameVO> examesDevemSerAgendados;
	private List<AgendamentoExameVO> examesDevemSerCanceladosJuntos;
	private List<AgendamentoExameVO> listaExamesSolicitacao;
	private boolean exibeConfirmacaoSelecao;
	private boolean exibeConfirmacaoSelecaoCancelamento;
	private Boolean exibeAdvExameNaoAgendado = false;
	private Boolean exibeAdvImpressaoTicket = null;
	private AghParametros parametroCancelado;
	private Boolean permitirHorarioExtra;
	private final static String PESQUISA_SOLICITACAO_INTERNACAO_UNIDADES_FECHADAS = "exames-pesquisaSolicitacaoInternacaoUnidadesFechadas";

	private SolicitacaoExameVO solicitacaoExameVO;

	public void inicio() {
		try {
			parametroCancelado = parametroFacade.buscarAghParametro(AghuParametrosEnum.	P_SITUACAO_CANCELADO);
			
			this.tratarOrigemSolicitacaoExames();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);		
			LOG.error(e.getMessage(), e);
		}
		
		try {
			// Obtem o usuario da unidade executora
			this.usuarioUnidadeExecutora = this.examesFacade
					.obterUnidExecUsuarioPeloId(registroColaboradorFacade
							.obterServidorAtivoPorUsuario(
									this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}

		if (unidadeExecutora == null && this.usuarioUnidadeExecutora != null) {
			// Resgata a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
		
		if(unidadeExecutora != null){
			seqUnidadeExecutora = unidadeExecutora.getSeq();
		}
		
		verificaPermissoesTelaListarExames();

		if (soeSeq != null) {
			this.obterSolicitacaoExame();
			this.pesquisar();
		}

		if (exameVO == null) {
			exameVO = new VAelSolicAtendsVO();

			if (soeSeq != null) {
				this.obterSolicitacaoExame();
				this.pesquisar();
			}
		}
		if (pacCodigoFonetica != null) {
			this.exameVO.setCodPaciente(pacCodigoFonetica);
			this.exameVO.setProntuario(null);
			this.selecionarPacienteConsulta();
			this.pacCodigoFonetica = null;
		}
		if (solicitacaoSelecionada != null) {
			this.obterExamesDaSolicitacao();
		}
	}
	
	private void verificaPermissoesTelaListarExames() {
		
		if(voltarPara == null){
			if (cascaFacade.temPermissao(this.obterLoginUsuarioLogado(), "elaborarSolicitacaoExameConsultaAntiga", "executor")) {
				setUsuarioTemPermissaoTela(true);
			}else {
				apresentarMsgNegocio(Severity.INFO, "AGENDAMENTO_EXAME_RESTRITO_UNIDADE_EXECUTORA");
				setUsuarioTemPermissaoTela(false);
			}
		} else {
			setUsuarioTemPermissaoTela(true);
		}
		
	}

	private void tratarOrigemSolicitacaoExames() {
		if (this.voltarPara != null && !this.voltarPara.equals(PESQUISA_SOLICITACAO_INTERNACAO_UNIDADES_FECHADAS)) {			
			origemSolicitacaoExames = true;
			
			setPermitirHorarioExtra(false);
					
			if (exibeAdvImpressaoTicket == null){
				exibeAdvImpressaoTicket = true;
			}				
			if (listaSeqUnidExamesSolicitados == null) {
				this.solicitacaoSelecionada = soeSeq;
				listaExamesSolicitacao = new ArrayList<AgendamentoExameVO>();
				listaExamesSolicitacao.addAll(examesAgendamentoSelecao);
				
				listaSeqUnidExamesSolicitados = obterListaSeqUnFExamesAgendaveis();
	
				if (listaSeqUnidExamesSolicitados != null
						&& listaSeqUnidExamesSolicitados.size() > 1) {
					this.mostrarMensagemAdvertenciaOutrasUnidExecutoras();
				}
			}
			
			inicializaUnidadeExecutora();
		} else {
			setPermitirHorarioExtra(true);
			origemSolicitacaoExames = false;
			exibeAdvImpressaoTicket = false;
		}
	}

	/*
	 * Inicializa o campo Unidade Funcional com a unidade do primeiro item não agendado.
	 * Caso todos os itens da solicitação já estejam agendados, inicializa com a unidade do primeiro item da lista
	 */
	private void inicializaUnidadeExecutora(){    
    	List<AelItemSolicitacaoExames> itensNaoAgendados = agendamentoExamesFacade.buscarItensSolicitacaoExameNaoAgendados(soeSeq, listaSeqUnidExamesSolicitados);
    	
    	if (itensNaoAgendados != null && !itensNaoAgendados.isEmpty() && itensNaoAgendados.get(0).getUnidadeFuncional() != null){
    		unidadeExecutora = itensNaoAgendados.get(0).getUnidadeFuncional();
    	} else {
    		unidadeExecutora = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(listaSeqUnidExamesSolicitados.get(0));
    	}
    }

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.limparSelecao();
		try {
			this.agendamentoExamesFacade.validarExamesAgendamentoSelecao(
					exameVO, paciente);
			solicitacoesExames = this.agendamentoExamesFacade.obterSolicitacoesExame(exameVO);
			if (solicitacoesExames.size() == 1) {
				this.solicitacaoSelecionada = solicitacoesExames.get(0).getNumero();
				this.obterExamesDaSolicitacao();
				this.solicitacoesExames = null;
			}
		} catch (ApplicationBusinessException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(), exception);
			return;
		}
	}

	/**
	 * Método executado ao clicar no botão Cancelar Agendamento
	 */
	public void cancelarAgendamento() {
		String labelCancelar = WebUtil.initLocalizedMessage(
				"LABEL_AGENDAMENTO_EXAMES_CANCELAR_AGENDAMENTO", null,
				(Object[]) null);
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			examesDevemSerCanceladosJuntos = agendamentoExamesFacade
					.verificarExamesNaoSelecComMesmaAmostra(examesAgendamentoSelecao, parametroCancelado, labelCancelar);
			
			if (examesDevemSerCanceladosJuntos != null) { 
				exibeConfirmacaoSelecaoCancelamento = true;
			} else {
				exibeConfirmacaoSelecaoCancelamento = false;
			}
			if (!exibeConfirmacaoSelecaoCancelamento){
				
				for (AgendamentoExameVO agendamentoExameVO : examesAgendamentoSelecao){
					AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade.obterItemSolicitacaoExameOriginal(agendamentoExameVO.getItemExame().getId().getSoeSeq(), agendamentoExameVO.getItemExame().getId().getSeqp());
					agendamentoExameVO.setItemExameOriginal(itemSolicitacaoExameOriginal);
				}
				
				this.agendamentoExamesFacade
						.cancelarItemHorarioAgendadoMarcadoPorSelecaoExames(
								examesAgendamentoSelecao,
								unidadeExecutora.getSeq(), nomeMicrocomputador);
	
				this.obterExamesDaSolicitacao();
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CANCELAMENTO_HORARIOS_EXAME");
				this.pesquisar();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void obterExamesDaSolicitacao() {
		try {
			solicitacao = this.agendamentoExamesFacade
					.obterVAelSolicAtendsPorSoeSeq(solicitacaoSelecionada);
			solicitacao.setOrigemSolicitacaoExames(this.origemSolicitacaoExames);
			examesAgendamentoSelecao = this.agendamentoExamesFacade
					.obterExamesParaAgendamento(solicitacao, unidadeExecutora.getSeq());
			if (this.solicitacoesExamesChecked != null){
				this.solicitacoesExamesChecked.clear();
			}else{
				this.solicitacoesExamesChecked = new  ArrayList<AgendamentoExameVO>();
			}
			this.solicitacoesExamesChecked.addAll(this.examesAgendamentoSelecao) ;
			this.marcarTodos(); 
		} catch (ApplicationBusinessException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(), exception);
			this.examesAgendamentoSelecao = null;
			this.solicitacoesExamesChecked = null;
		}
	}

	public boolean destacaAelUnfExecutaExamesDesativada(
			AgendamentoExameVO itemAgendamento) {
		if (itemAgendamento != null
				&& itemAgendamento.getItemExame() != null
				&& itemAgendamento.getItemExame().getAelUnfExecutaExames() != null
				&& itemAgendamento.getItemExame().getAelUnfExecutaExames()
						.getIndDesativaTemp() != null) {
			return itemAgendamento.getItemExame().getAelUnfExecutaExames()
					.getIndDesativaTemp();
		} else {
			return false;
		}
	}

	/**
	 * Método para obter o nome do Paciente
	 */
	public String obterNomePacientePorPacCodigo(Integer codPaciente) {
		AipPacientes paciente = this.pacienteFacade.obterNomePacientePorCodigo(codPaciente);
		if (paciente != null) {
			return paciente.getNome();
		} else {
			return null;
		}
	}

	/**
	 * Método executado para carregar as informações da solicitacão
	 */
	public void obterSolicitacaoExame() {
		if (this.soeSeq !=null && (exameVO==null || exameVO.getNumero()==null) || !this.soeSeq.equals(exameVO.getNumero())){
			this.limparSelecao();
			this.solicitacoesExames = null;
			this.paciente = null;
			this.exameVO = null;
			this.pacNome = null;
			try {
				if (soeSeq != null) {
					exameVO = this.agendamentoExamesFacade.obterVAelSolicAtendsPorSoeSeq(soeSeq);
					this.habilitaBotaoPesquisar = true;
					this.selecionarPacienteConsulta();
				} else {
					this.limparDadosSolicitacao();
				}
			} catch (ApplicationBusinessException exception) {
				apresentarExcecaoNegocio(exception);
				LOG.error(exception.getMessage(), exception);
				this.limparDadosSolicitacao();
			}
		}
	}

	/**
	 * Método executado para agendar exames
	 * 
	 */
	public String agendarExames() throws ApplicationBusinessException {
		try {
			// String labelAgendar =
			// ResourceBundle.instance().getString("LABEL_AGENDAMENTO_EXAMES_AGENDAR");
			String labelAgendar = WebUtil.initLocalizedMessage(
					"LABEL_AGENDAMENTO_EXAMES_AGENDAR", null, (Object[]) null);
			
			examesDevemSerAgendados = this.agendamentoExamesFacade.permiteAgendarExames(
					examesAgendamentoSelecao, obterLoginUsuarioLogado(),labelAgendar);
			if (examesDevemSerAgendados != null){
				exibeConfirmacaoSelecao = true;
			} else {
				exibeConfirmacaoSelecao = false;
			}

			if (!exibeConfirmacaoSelecao) {
				consultaHorarioLivreController.setExamesAgendamentoSelecao(examesAgendamentoSelecao);
				consultaHorarioLivreController.setOrigemSolicitacao(origemSolicitacaoExames);
				return "consultaHorariosLivresExame";
			}
		} catch (ApplicationBusinessException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(), exception);
		}
		return null;
	}

	public void selecionarExamesAmostrasComum(List<AgendamentoExameVO> listaExames) {
		for (AgendamentoExameVO itemExame : listaExames) {
			for (AgendamentoExameVO agendamentoExame : examesAgendamentoSelecao) {
				if (agendamentoExame.getItemExame().getId().getSeqp().equals(itemExame.getItemExame().getId().getSeqp())) {
					agendamentoExame.setSelecionado(true);
					this.solicitacoesExamesChecked.add(agendamentoExame);
				}
			}
		}
	}

	/**
	 * Busca o paciente atráves do código e/ou prontuário
	 */
	public void selecionarPacienteConsulta() {
		if (this.exameVO.getCodPaciente() != null
				|| this.exameVO.getProntuario() != null) {
			if (this.exameVO.getCodPaciente() != null) {
				this.paciente = pacienteFacade.obterPacientePorCodigo(exameVO.getCodPaciente());
			} else if (this.exameVO.getProntuario() != null) {
				this.paciente = pacienteFacade.obterPacientePorProntuario(exameVO.getProntuario());
			}
		}

		if (this.paciente == null) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"AIP_PACIENTE_NAO_ENCONTRADO");
		} else {
			this.exameVO.setCodPaciente(this.paciente.getCodigo());
			this.exameVO.setProntuario(this.paciente.getProntuario());
			this.pacNome = this.paciente.getNome();
			this.habilitaBotaoPesquisar = true;
		}
	}

	/**
	 * Retorna o dia da semana da data do agendamento do exame
	 */
	public String obterDiaSemanaData(Date data) {
		if (data != null) {
			DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(data);
			return diaSemana.getDescricao();
		} else {
			// return
			// SeamResourceBundle.getBundle().getString("MENSAGEM_EXAME_NAO_AGENDADO");
			return WebUtil.initLocalizedMessage("MENSAGEM_EXAME_NAO_AGENDADO",
					null, (Object[]) null);
		}
	}

	/**
	 * Retorna a data formatada da reativação
	 */
	public String obterDataFormatadaReativacao(Date data) {
		String dataFormat = null;
		if (data != null) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			dataFormat = df.format(data);
			return dataFormat;
		} else {
			return null;
		}
	}
	
	// metodos de auxílio
	public void marcarTodos() {
		for(AgendamentoExameVO itemListaExames : examesAgendamentoSelecao) {
			if (this.solicitacoesExamesChecked.contains(itemListaExames)){
				itemListaExames.setSelecionado(Boolean.TRUE);
			}else{
				itemListaExames.setSelecionado(Boolean.FALSE);
			}
		}
	}
	
	public void selecionarLinha(SelectEvent event) {
		AgendamentoExameVO exameSelecionado = (AgendamentoExameVO)event.getObject();
		this.solicitacoesExamesChecked.clear();
		
		for(AgendamentoExameVO itemListaExames : examesAgendamentoSelecao) {
			if (itemListaExames.equals(exameSelecionado)){
				itemListaExames.setSelecionado(Boolean.TRUE);
				this.solicitacoesExamesChecked.add(itemListaExames);
			}else{
				itemListaExames.setSelecionado(Boolean.FALSE);
			}
		}
	}

	public void marcarLinha(SelectEvent event) {
		AgendamentoExameVO exameSelecionado = (AgendamentoExameVO)event.getObject();
		for(AgendamentoExameVO itemListaExames : examesAgendamentoSelecao) {
			if (itemListaExames.equals(exameSelecionado)){
				itemListaExames.setSelecionado(Boolean.TRUE);
			}
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		AgendamentoExameVO exameSelecionado = (AgendamentoExameVO)event.getObject();
		for(AgendamentoExameVO itemListaExames : examesAgendamentoSelecao) {
			if (itemListaExames.equals(exameSelecionado)){
				itemListaExames.setSelecionado(Boolean.FALSE);
			}
		}
	}
	

	/**
	 * Seleciona todos os Exames.
	 */
	/*
	public void todosExamesSelecionados(Boolean todosSelecionados) {
		if (examesAgendamentoSelecao != null) {
			for (AgendamentoExameVO agendamentoVO : examesAgendamentoSelecao) {
				agendamentoVO.setSelecionado(todosSelecionados);
			}
		}
	}*/

	/**
	 * Quando a tela não for chamada da Solicitação de Exames deve-se persistir
	 * identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		if (!origemSolicitacaoExames) {
			try {
				// Persiste identificacao da unidade executora do usuario
				this.cadastrosApoioExamesFacade
						.persistirIdentificacaoUnidadeExecutora(
								this.usuarioUnidadeExecutora,
								this.unidadeExecutora);
				seqUnidadeExecutora = unidadeExecutora.getSeq();
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Imprime o ticket de exame do paciente
	 */
	public void imprimirTicketExames() {
		try {
			//Verifica a Unidade Funcional de Coleta
			this.examesFacade.pesquisarUnidadeFuncionalColeta();
			
			Set<Short> listaUnfSeq = new HashSet<Short>();			

			if (!origemSolicitacaoExames) {
				for(AgendamentoExameVO itemListaExames : examesAgendamentoSelecao){
					if (itemListaExames.getItemExame()!=null && itemListaExames.getItemExame().getUnidadeFuncional()!=null){
						listaUnfSeq.add(itemListaExames.getItemExame().getUnidadeFuncional().getSeq());
					}
				}
			}
			relatorioTicketExamesPacienteController.directPrint(solicitacao.getNumero(), null, listaUnfSeq, true);

			exibeAdvImpressaoTicket = false;
			
			// cupsFacade.raiseCupsEvent("imprimirTicketExamesPaciente",
			// solicitacao.getNumero(), unidadeExecutora.getSeq(), true);
		} catch (BaseException exception) {
			if(exception.getMessage().equals("P_COD_UNIDADE_COLETA_DEFAULT_NÃO_É_UMA_UND_FUNCIONAL_VÁLIDA")){
				this.apresentarMsgNegocio(Severity.ERROR,"O valor do parâmetro P_COD_UNIDADE_COLETA_DEFAULT não é uma Unidade Funcional de Coleta válida");
				LOG.error(exception.getMessage(), exception);
			}else{
				apresentarExcecaoNegocio(exception);
				LOG.error(exception.getMessage(), exception);
			}
		} catch (JRException | SystemException | IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Pesquisa Unidade Executora Quando a tela for chamada da Solicitação de
	 * Exames deve-se listar apenas Unidades Executoras dos exames solicitados
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(
			String parametro) {
		List<AghUnidadesFuncionais> listaUnidExecutora;

		if (origemSolicitacaoExames) {
			listaUnidExecutora = this.aghuFacade.obterUnidadesFuncionaisListaUnidadesSolicitacao(parametro, listaSeqUnidExamesSolicitados);
		} else {
			listaUnidExecutora = this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
		}

		return listaUnidExecutora;
	}

	private List<Short> obterListaSeqUnFExamesAgendaveis() {
		List<Short> listaSeqUnidadaExecutoraHabilitadas =  new ArrayList<Short>();
		
		List<Short> listaSeqUnidadaExecutora = this.agendamentoExamesFacade.obterListaSeqUnFExamesAgendaveis(soeSeq);
		for (Short seqUnF : listaSeqUnidadaExecutora) {
			if (!listaSeqUnidadaExecutoraHabilitadas.contains(seqUnF) && verificarUnidadePossuiExameAgendavel(seqUnF)) {
				listaSeqUnidadaExecutoraHabilitadas.add(seqUnF);
			}
		}

		return listaSeqUnidadaExecutoraHabilitadas;
	}
	
	private boolean verificarUnidadePossuiExameAgendavel(Short seqUnidade){
		List<AgendamentoExameVO> listaExames = null;
		
		try {
			solicitacao = this.agendamentoExamesFacade.obterVAelSolicAtendsPorSoeSeq(solicitacaoSelecionada);
			listaExames = this.agendamentoExamesFacade.obterExamesParaAgendamento(solicitacao, seqUnidade);			
		} catch (ApplicationBusinessException exception) {
			LOG.error(exception.getMessage(), exception);
		}		
		
		if (listaExames != null && !listaExames.isEmpty()){
			return true;
		} else {
			return false;
		}
	}

	private void mostrarMensagemAdvertenciaOutrasUnidExecutoras() {
		List<Short> listaSeqUnidadaExecutora = new ArrayList<Short>();

		String msg;

		StringBuilder str = new StringBuilder();
		str.append(MSG_UNF_1).append(this.soeSeq.toString()).append(MSG_UNF_2);
		msg = str.toString();
		this.apresentarMsgNegocio(Severity.INFO, msg);

		for (AgendamentoExameVO itemAgendamento : examesAgendamentoSelecao) {
			AghUnidadesFuncionais unidadeFuncional = itemAgendamento
					.getItemExame().getAelUnfExecutaExames()
					.getUnidadeFuncional();
			
			if (listaSeqUnidExamesSolicitados.contains(unidadeFuncional.getSeq())){			
				if (!listaSeqUnidadaExecutora.contains(unidadeFuncional.getSeq())) {
					str = new StringBuilder();
					listaSeqUnidadaExecutora.add(unidadeFuncional.getSeq());
	
					str.append(unidadeFuncional.getSeq().toString());
					str.append(" - ");
					str.append(unidadeFuncional.getDescricao());
	
					msg = str.toString();
					this.apresentarMsgNegocio(Severity.INFO, msg);
				}
			}
		}

		str = new StringBuilder();
		str.append(MSG_UNF_3);
		msg = str.toString();
		this.apresentarMsgNegocio(Severity.INFO, msg);
	}

	// Limpa os dados da pesquisa
	public void limpar() {
		if (!origemSolicitacaoExames) {
			this.soeSeq = null;
			this.pacNome = null;
			this.paciente = null;
			this.solicitacoesExames = null;
			this.exameVO = new VAelSolicAtendsVO();
			this.habilitaBotaoPesquisar = false;
			if (this.voltarPara!=null){
				this.unidadeExecutora = null;
			}
		}
		this.solicitacoesExamesChecked = null;
		this.examesAgendamentoSelecao = null;
		this.solicitacaoSelecionada = null;
	}

	public void limparDadosSolicitacao() {
		this.soeSeq = null;
		this.exameVO = new VAelSolicAtendsVO();
		this.pacNome = null;
		this.paciente = null;
		this.solicitacaoSelecionada = null;
		this.solicitacoesExames = null;
		this.examesAgendamentoSelecao = null;
		this.habilitaBotaoPesquisar = false;
	}

	public void limparSelecao() {
		this.solicitacaoSelecionada = null;
		this.examesAgendamentoSelecao = null;
		this.solicitacoesExamesChecked = null;
	}

	// Redireciona para a Pesquisa Fonética
	public String redirecionarPesquisaFonetica() {
		return "paciente-pesquisaPacienteComponente";
	}

	private boolean isPaginaChamadoraAtendimentoAmbulatorio() {
		return voltarPara != null 
				&& (voltarPara.equals("ambulatorio-atenderPacientesAgendados")
						|| voltarPara.equals("ambulatorio-atenderPacientesEvolucao"));
	}
	
	// Voltar
	public String retornarTelaAnterior() {
		
		if (isPaginaChamadoraAtendimentoAmbulatorio()){
			try {
				solicitacaoExameFacade.atualizarItensPendentesAmbulatorio(solicitacaoExameVO, null, getEnderecoRedeHostRemoto(), new Date());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			} catch (Exception e) {
				apresentarMsgNegocio("Erro ao atualizarItensPendentesAmbulatorio");
				LOG.error("Erro ao atualizarItensPendentesAmbulatorio",e);
				return null;
			}
		}
		
		String retorno = this.voltarPara;
		solicitacaoExameVO = null;
		exameVO = null;
		paciente = null;
		pacCodigoFonetica = null;
		pacNome = null;
		soeSeq = null;
		unidadeExecutora = null;
		seqUnidadeExecutora = null;
		voltarPara = null;
		solicitacaoSelecionada = null;
		solicitacao = null;
		solicitacoesExames = null;
		examesAgendamentoSelecao = null;
		todosExamesSelecionados = false;
		habilitaBotaoPesquisar = false;
		listaSeqUnidExamesSolicitados = null;
		return retorno;
	}
	
	public String voltar(){
			List<AelItemSolicitacaoExames> itensNaoAgendados = agendamentoExamesFacade.buscarItensSolicitacaoExameNaoAgendados(soeSeq, listaSeqUnidExamesSolicitados);
		if (itensNaoAgendados != null && !itensNaoAgendados.isEmpty()){
			exibeAdvExameNaoAgendado = true;
		}
		
		//exibeAdvExameNaoAgendado = agendamentoExamesFacade.verificarExistenciaItensSolicitacaoExameNaoAgendados(soeSeq);
		
		if (exibeAdvExameNaoAgendado ){
			openDialog("modalAdverteExameNaoAgendadoWG");
			return null;
		} 
		
		if (!isPaginaChamadoraAtendimentoAmbulatorio() && exibeAdvImpressaoTicket){
			openDialog("modalAdverteImpressaoTicketWG");
			return null;
		}
		
		return retornarTelaAnterior();		
	}
	
	public String verificarImpressaoTicketVoltar(){
		if (exibeAdvImpressaoTicket){
			openDialog("modalAdverteImpressaoTicketWG");
			return null;
		} else {
			return retornarTelaAnterior();
		}
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AgendamentoExameVO> getExamesAgendamentoSelecao() {
		return examesAgendamentoSelecao;
	}

	public void setExamesAgendamentoSelecao(
			List<AgendamentoExameVO> examesAgendamentoSelecao) {
		this.examesAgendamentoSelecao = examesAgendamentoSelecao;
	}

	public VAelSolicAtendsVO getExameVO() {
		return exameVO;
	}

	public void setExameVO(VAelSolicAtendsVO exameVO) {
		this.exameVO = exameVO;
	}

	public Integer getSolicitacaoSelecionada() {
		return solicitacaoSelecionada;
	}

	public void setSolicitacaoSelecionada(Integer solicitacaoSelecionada) {
		this.solicitacaoSelecionada = solicitacaoSelecionada;
	}

	public List<VAelSolicAtendsVO> getSolicitacoesExames() {
		return solicitacoesExames;
	}

	public void setSolicitacoesExames(List<VAelSolicAtendsVO> solicitacoesExames) {
		this.solicitacoesExames = solicitacoesExames;
	}

	public void setSolicitacao(VAelSolicAtendsVO solicitacao) {
		this.solicitacao = solicitacao;
	}

	public VAelSolicAtendsVO getSolicitacao() {
		return solicitacao;
	}

	public void setTodosExamesSelecionados(Boolean todosExamesSelecionados) {
		this.todosExamesSelecionados = todosExamesSelecionados;
	}

	public Boolean getTodosExamesSelecionados() {
		return todosExamesSelecionados;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public Short getSeqUnidadeExecutora() {
		return seqUnidadeExecutora;
	}

	public void setSeqUnidadeExecutora(Short seqUnidadeExecutora) {
		this.seqUnidadeExecutora = seqUnidadeExecutora;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setHabilitaBotaoPesquisar(Boolean habilitaBotaoPesquisar) {
		this.habilitaBotaoPesquisar = habilitaBotaoPesquisar;
	}

	public Boolean getHabilitaBotaoPesquisar() {
		return habilitaBotaoPesquisar;
	}

	public boolean isOrigemSolicitacaoExames() {
		return origemSolicitacaoExames;
	}

	public void setOrigemSolicitacaoExames(boolean origemSolicitacaoExames) {
		this.origemSolicitacaoExames = origemSolicitacaoExames;
	}

	public List<AgendamentoExameVO> getExamesDevemSerAgendados() {
		return examesDevemSerAgendados;
	}

	public void setExamesDevemSerAgendados(
			List<AgendamentoExameVO> examesDevemSerAgendados) {
		this.examesDevemSerAgendados = examesDevemSerAgendados;
	}

	public boolean isExibeConfirmacaoSelecao() {
		return exibeConfirmacaoSelecao;
	}

	public void setExibeConfirmacaoSelecao(boolean exibeConfirmacaoSelecao) {
		this.exibeConfirmacaoSelecao = exibeConfirmacaoSelecao;
	}

	public boolean isExibeConfirmacaoSelecaoCancelamento() {
		return exibeConfirmacaoSelecaoCancelamento;
	}

	public void setExibeConfirmacaoSelecaoCancelamento(
			boolean exibeConfirmacaoSelecaoCancelamento) {
		this.exibeConfirmacaoSelecaoCancelamento = exibeConfirmacaoSelecaoCancelamento;
	}

	public Boolean getExibeAdvExameNaoAgendado() {
		return exibeAdvExameNaoAgendado;
	}

	public void setExibeAdvExameNaoAgendado(Boolean exibeAdvExameNaoAgendado) {
		this.exibeAdvExameNaoAgendado = exibeAdvExameNaoAgendado;
	}

	public Boolean getExibeAdvImpressaoTicket() {
		return exibeAdvImpressaoTicket;
	}

	public void setExibeAdvImpressaoTicket(Boolean exibeAdvImpressaoTicket) {
		this.exibeAdvImpressaoTicket = exibeAdvImpressaoTicket;
	}

	public List<AgendamentoExameVO> getExamesDevemSerCanceladosJuntos() {
		return examesDevemSerCanceladosJuntos;
	}

	public void setExamesDevemSerCanceladosJuntos(
			List<AgendamentoExameVO> examesDevemSerCanceladosJuntos) {
		this.examesDevemSerCanceladosJuntos = examesDevemSerCanceladosJuntos;
	}

	public Boolean getPermitirHorarioExtra() {
		return permitirHorarioExtra;
	}

	public void setPermitirHorarioExtra(Boolean permitirHorarioExtra) {
		this.permitirHorarioExtra = permitirHorarioExtra;
	}

	public Boolean getUsuarioTemPermissaoTela() {
		return usuarioTemPermissaoTela;
	}

	public void setUsuarioTemPermissaoTela(Boolean usuarioTemPermissaoTela) {
		this.usuarioTemPermissaoTela = usuarioTemPermissaoTela;
	}

	
	public SolicitacaoExameVO getSolicitacaoExameVO() {
		return solicitacaoExameVO;
	}

	
	public void setSolicitacaoExameVO(SolicitacaoExameVO solicitacaoExameVO) {
		this.solicitacaoExameVO = solicitacaoExameVO;
	}

	public List<AgendamentoExameVO> getSolicitacoesExamesChecked() {
		return solicitacoesExamesChecked;
	}

	public void setSolicitacoesExamesChecked(List<AgendamentoExameVO> solicitacoesExamesChecked) {
		this.solicitacoesExamesChecked = solicitacoesExamesChecked;
	}

}
