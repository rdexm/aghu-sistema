package br.gov.mec.aghu.internacao.pesquisa.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoCensoDiarioPacientes;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.action.RelatorioCensoDiarioPacientesController;
import br.gov.mec.aghu.internacao.leitos.action.BloqueiaLeitoController;
import br.gov.mec.aghu.internacao.leitos.action.LiberaLeitoController;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.VAinCensoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinObservacoesCenso;
import br.gov.mec.aghu.model.AinObservacoesCensoId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionArrayPaginator;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.etc.ArrayPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarCensoDiarioPacientesPaginatorController extends ActionController implements ActionArrayPaginator {

	private static final long serialVersionUID = 5974903752151869070L;

	private static final String PAGE_TRANSFERIR_PACIENTE_CRUD = "internacao-transferirPacienteCRUD";
	private static final String PAGE_RELATORIO_CENSO_DIARIO = "internacao-imprimir-censo-diario";
	private static final String PAGE_DADOS_ALTA_PACIENTE = "internacao-dadosDaAltaPaciente";
	private static final String PAGE_BLOQUEIA_LEITO = "internacao-bloqueiaLeito";
	private static final String PAGE_LIBERA_LEITO = "internacao-liberaLeito";
	private static final String PAGE_INTERNAR_PACIENTE = "internacao-cadastroInternacao";
	private static final String PAGE_PESQUISAR_EXTRATO_PACIENTE = "internacao-pesquisarExtratoPaciente";
	private static final String PAGE_PESQUISAR_EXTRATO_LEITO = "internacao-pesquisarExtratoLeito";
	private static final String PAGE_PESQUISAR_PACIENTE = "paciente-pesquisaPaciente";
	private static final String PAGE_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";
	private static final String PAGE_CADASTRAR_PACIENTE = "paciente-cadastroPaciente";
	private static final String PAGE_INTERNACAO_SOLICITA_TRANSFERENCIA_PACIENTE_LIST = "internacao-solicitaTransferenciaPacienteList";

	private static final String PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO = "blococirurgico-relatorioEtiquetasIdentificacao";

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private BloqueiaLeitoController bloqueiaLeitoController;

	@Inject
	private LiberaLeitoController liberaLeitoController;

	@Inject
	private CadastroInternacaoController cadastroInternacaoController;

	@Inject
	private PesquisaExtratoLeitoController pesquisaExtratoLeitoController;

	@Inject
	private PesquisaExtratoPacientePaginatorController pesquisaExtratoPacientePaginatorController;

	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	
	@Inject
	private RelatorioCensoDiarioPacientesController relatorioCensoDiarioPacientesController;

	@Inject @ArrayPaginator
	private DynamicDataModel<VAinCensoVO> dataModel;
	private VAinCensoVO censoSelecionado;
	private List<VAinCensoVO> listaCensoVO;
	
	private AghAtendimentos aghAtendimentos;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghUnidadesFuncionais unidadeFuncionalMae;
	private Date data;
	private DominioSituacaoUnidadeFuncional status;
	private Long count;

	private String observacaoCensoInternacao = "";
	private boolean exibirModalConfirmacaoInternacao;
	private boolean exibirBotaoInternacao;

	private Integer internacaoSeq;
	private AinObservacoesCenso observacaoCenso;
	private boolean permitirAlterarObservacao;
	private String nomeAlterouObs;
	private String nomeCriouObs;
	private String dataCriacaoObs;
	private Integer quantPaginacao;

	private Integer prontuario;
	private Integer pacCodigo;
	private String codigoLeitos;
	private Date dthrLancamento;
	private boolean atualizarCenso;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.status = DominioSituacaoUnidadeFuncional.PACIENTES;
		this.data = new Date();
		this.exibirModalConfirmacaoInternacao = false;
		this.exibirBotaoInternacao = true;// Por padrao habilita o botao
		this.atualizarCenso = false;
		
		try {
			AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_QTD_PAGINACAO_CENSO_PAC);
			quantPaginacao = param.getVlrNumerico().intValue();			
		} catch (ApplicationBusinessException e) {
			quantPaginacao = 50;
		}
		this.dataModel.setDefaultMaxRow(quantPaginacao);
		
	}

	public String transferirPaciente() {
		return PAGE_TRANSFERIR_PACIENTE_CRUD;
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.unidadeFuncional = null;
		this.unidadeFuncionalMae = null;
		this.data = new Date();
		this.status = DominioSituacaoUnidadeFuncional.PACIENTES;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Limpa os campos da modal de observacao.
	 */
	public void limparModalObs() {
		this.nomeAlterouObs = "";
		this.nomeCriouObs = "";
		this.dataCriacaoObs = "";
		this.observacaoCensoInternacao = "";
	}

	/**
	 * Desabilita a exibição da modal de confirmar internação
	 */
	public void cancelarModalConfirmacao() {
		this.exibirModalConfirmacaoInternacao = false;
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.exibirModalConfirmacaoInternacao = false;

		if (pesquisaCensoInvalida()){
			return;
		} else if (this.unidadeFuncional != null && this.unidadeFuncionalMae != null) {
			// Se informar as duas unidades, pesquisar somente pela unidade do
			// pac.
			// Funcionalidade identica ao agh, implementada com o aval do
			// analista.
			this.unidadeFuncionalMae = null;
		}
		// Alterado o numero de registros exibidos a pedido do analista e
		// gerente.
		dataModel.reiniciarPaginator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			Object[] array = this.pesquisaInternacaoFacade.pesquisarCensoDiarioPacientes(firstResult, quantPaginacao, orderProperty, asc, this.unidadeFuncional != null ? this.unidadeFuncional.getSeq() : null,
				this.unidadeFuncionalMae != null ? this.unidadeFuncionalMae.getSeq() : null, this.data, this.status);
			count = (Long) array[0];
			this.listaCensoVO = (List<VAinCensoVO>) array [1];  

			return new Object[]{count, listaCensoVO};
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public Integer recuperarCount() throws ApplicationBusinessException {

		return this.pesquisaInternacaoFacade.pesquisarCensoDiarioPacientesCount(this.unidadeFuncional != null ? this.unidadeFuncional
				.getSeq() : null,this.unidadeFuncionalMae != null ? this.unidadeFuncionalMae.getSeq() : null,this.data,
				this.status);
	}

	// ### Metodos para a suggestion de unidades funcionais do paciente ###
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String param) {
		return this.pacienteFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(param, true, true, new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA });
	}

	// ### Metodos para a suggestion de unidades funcionais da mae do paciente
	// ###
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalMae(String param) {
		return this.pacienteFacade.pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(param, true, false, new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA });
	}


	// ########################################//
	// ### CONTROLE DE NAVEGACAO DOS BOTOES ###//
	// ########################################//
	/**
	 * Controla o redirecionamento de pagina do botao 'ALTA'
	 */
	public String redirecionarBotaoAlta() {
		this.internacaoSeq = censoSelecionado.getInternacaoSeq();

		// String retorno = "redirecionarAltaPacienteObservacao";
		String retorno = "";// ### POR ENQUANTO, NAO FAZ ESSE REDIRECIONAMENTO
							// PQ A TELA NAO EXISTE AINDA.
		if (censoSelecionado.getTipo() != null && DominioTipoCensoDiarioPacientes.I.equals(censoSelecionado.getTipo())) {
			retorno = PAGE_DADOS_ALTA_PACIENTE;
		}
		return retorno;
	}

	public String redirecionarSolicitarTransferencia() {
		return PAGE_INTERNACAO_SOLICITA_TRANSFERENCIA_PACIENTE_LIST;
	}

	public String redirecionarImprimirEtiquetasMedicamentos(){
		return PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO;
	}
	/**
	 * Controla o redirecionamento de pagina do botao 'INTERNACAO'
	 */
	public String redirecionarBotaoInternacao() {
		String retorno = "";
		this.codigoLeitos = censoSelecionado.getQrtoLto();	// usado para passagem de parametro para outra tela
		this.pacCodigo = censoSelecionado.getPacCodigo();

		this.exibirModalConfirmacaoInternacao = false;
		if (DominioTipoCensoDiarioPacientes.L.equals(censoSelecionado.getTipo())) {
			if (DominioMovimentoLeito.L.equals(censoSelecionado.getGrupoMvtoLeito())) {
				bloqueiaLeitoController.setCodigoLeitos(codigoLeitos);
				bloqueiaLeitoController.setVoltarPara(PAGE_CENSO_DIARIO_PACIENTES);
				retorno = PAGE_BLOQUEIA_LEITO;
			} else {
				if (DominioMovimentoLeito.D.equals(censoSelecionado.getGrupoMvtoLeito()) || DominioMovimentoLeito.BL.equals(censoSelecionado.getGrupoMvtoLeito()) || DominioMovimentoLeito.B.equals(censoSelecionado.getGrupoMvtoLeito())
						|| DominioMovimentoLeito.R.equals(censoSelecionado.getGrupoMvtoLeito()) || DominioMovimentoLeito.BI.equals(censoSelecionado.getGrupoMvtoLeito())) {
					liberaLeitoController.setCodigoLeitos(codigoLeitos);
					liberaLeitoController.setCameFrom(PAGE_CENSO_DIARIO_PACIENTES);
					retorno = PAGE_LIBERA_LEITO;
				}
			}
		} else {
			if (DominioTipoCensoDiarioPacientes.I.equals(censoSelecionado.getTipo())) {
				cadastroInternacaoController.setAipPacCodigo(pacCodigo);
				cadastroInternacaoController.setCameFrom(PAGE_CENSO_DIARIO_PACIENTES);
				retorno = PAGE_INTERNAR_PACIENTE;
			} else {
				if (StringUtils.isNotBlank(censoSelecionado.getTamCodigo())) {
					retorno = "";// REDIRECIONAMENTO DESABILITADO
									// PROVISORIAMENTE POIS A TELA AINDA NAO
									// EXISTE
					// retorno = "redirecionarIngressarPacienteSalaObservacao";
				} else {
					this.exibirBotaoInternacao = false;
					this.exibirModalConfirmacaoInternacao = true;
					return null;
				}
			}
		}
		return retorno;
	}

	// ### O BOTAO DE DETALHAR (MODAL) SEMPRE REDIRECIONA PARA A ESTORIA
	// "INGRESSAR PACIENTE SO" ###
	public String redirecionarIngressarPacienteSalaObservacao(Integer pacCodigo) {
		this.exibirModalConfirmacaoInternacao = false;
		return PAGE_PESQUISAR_PACIENTE;
	}

	// ### O BOTAO DE INTERNAR (MODAL) SEMPRE REDIRECIONA PARA A ESTORIA
	// "PESQUISAR PACIENTE " ###
	// ### O BOTAO DE DISP. LEITO (MODAL) SEMPRE REDIRECIONA PARA A ESTORIA
	// "PESQUISAR PACIENTE " ###
	public String redirecionarPesquisarPaciente(Integer pacCodigo) {
		this.exibirModalConfirmacaoInternacao = false;
		return PAGE_PESQUISAR_PACIENTE;
	}

	// ### O BOTAO DE TRANSFERENCIA SEMPRE REDIRECIONA PARA A ESTORIA
	// "TRANSFERIR PACIENTE" ###
	// ### O BOTAO DE CADASTRO DE PACIENTES SEMPRE REDIRECIONA PARA A ESTORIA
	// "CADASTRAR PACIENTE" ###

	/**
	 * Controla o redirecionamento de pagina do botao 'EXTRATO'
	 */
	public String redirecionarBotaoExtrato() {
		this.prontuario = censoSelecionado.getProntuario();
		this.dthrLancamento = censoSelecionado.getDthrLancamento();
		this.codigoLeitos = censoSelecionado.getQrtoLto();

		String retorno = "";
		if (censoSelecionado.getTipo() != null && (DominioTipoCensoDiarioPacientes.L.equals(censoSelecionado.getTipo()) || DominioTipoCensoDiarioPacientes.A.equals(censoSelecionado.getTipo()))) {
			pesquisaExtratoLeitoController.setLeitoPesquisa(codigoLeitos);
			pesquisaExtratoLeitoController.setCameFrom(PAGE_CENSO_DIARIO_PACIENTES);
			pesquisaExtratoLeitoController.iniciarPesquisa();
			retorno = PAGE_PESQUISAR_EXTRATO_LEITO;
		} else {
			pesquisaExtratoPacientePaginatorController.setProntuario(prontuario);
			pesquisaExtratoPacientePaginatorController.setCameFrom(PAGE_CENSO_DIARIO_PACIENTES);
			retorno = PAGE_PESQUISAR_EXTRATO_PACIENTE;
		}
		return retorno;
	}

	public String cadastrarPaciente() {
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(censoSelecionado.getPacCodigo());
		cadastrarPacienteController.setCameFrom(PAGE_CENSO_DIARIO_PACIENTES);
		cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
		return PAGE_CADASTRAR_PACIENTE;
	}

	/**
	 * Verifica se a data informada no filtro da tela é igual a data atual. Em
	 * caso positivo, permite a alteração da observação.
	 * 
	 * @param data
	 * @return boolean
	 */
	private boolean permitirAlteracaoObservacao() {
		boolean retorno = false;
		Calendar auxCalData = Calendar.getInstance();
		Calendar auxCal = Calendar.getInstance();

		if (this.data != null) {
			auxCalData.setTime(this.data);
			auxCalData.set(Calendar.HOUR_OF_DAY, 0);
			auxCalData.set(Calendar.MINUTE, 0);
			auxCalData.set(Calendar.SECOND, 0);
			auxCalData.set(Calendar.MILLISECOND, 0);

			auxCal.setTime(new Date());
			auxCal.set(Calendar.HOUR_OF_DAY, 0);
			auxCal.set(Calendar.MINUTE, 0);
			auxCal.set(Calendar.SECOND, 0);
			auxCal.set(Calendar.MILLISECOND, 0);

			if (auxCalData.compareTo(auxCal) == 0) {
				retorno = true;
			}
		}
		return retorno;
	}
	
	
	public String obterEstiloOrigemDestino(VAinCensoVO vAinCensoVO) throws ApplicationBusinessException {
		String retorno = "";
		boolean bPrevisaoDeAltaNasProximasHoras = (vAinCensoVO != null ? vAinCensoVO.isPrevisaoDeAltaNasProximasHoras() : false);

		if (bPrevisaoDeAltaNasProximasHoras) {
			if (vAinCensoVO != null  && vAinCensoVO.getNroCartaoSaude() == null) {
				retorno = "font-weight:bolder;background-color:lightgreen;";
			} else {
				retorno = "background-color:lightgreen;";
			}
		}
		return retorno;
	}	
	
	
	public String getObterEstiloNomeSituacao(VAinCensoVO vAinCensoVO) throws ApplicationBusinessException {
		String retorno = "";
		
		boolean bPacienteNotifGMR = (vAinCensoVO != null ? vAinCensoVO.isPacienteNotifGMR(): false);

		if (bPacienteNotifGMR) {
			retorno = "background-color:#00FFFF;";
			if (vAinCensoVO != null && vAinCensoVO.getNroCartaoSaude() == null) {
				retorno = "background-color:#00FFFF;font-weight:bolder;";
			}
		} else {
			if (vAinCensoVO != null && vAinCensoVO.isPrevisaoDeAltaNasProximasHoras()) {
				if (vAinCensoVO != null && vAinCensoVO.getNroCartaoSaude() == null) {
					retorno = "font-weight:bolder;background-color:lightgreen;";
				} else {
					retorno = "background-color:lightgreen;";
				}
			} else {
				if (vAinCensoVO != null && vAinCensoVO.getNroCartaoSaude() == null) {
					retorno = "font-weight:bolder;";
				}
			}
		}
		return retorno;

	}		

	/**
	 * Obtem o a descricao da obsevacao censo
	 * 
	 * @param intSeq
	 */
	public void obterObservacaoDaInternacao() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.internacaoSeq = censoSelecionado.getInternacaoSeq();
		observacaoCenso = this.pesquisaInternacaoFacade.obterObservacaoDaInternacao(censoSelecionado.getInternacaoSeq(), this.data);
		if (observacaoCenso != null) {
			observacaoCensoInternacao = observacaoCenso.getDescricao();
			this.nomeCriouObs = obterNomeAutorObservacao(observacaoCenso.getSerMatricula(), observacaoCenso.getSerVinCodigo());
			this.dataCriacaoObs = sdf.format(observacaoCenso.getCriadoEm());

			if (observacaoCenso.getSerMatriculaAltera() != null && observacaoCenso.getSerVinCodigoAltera() != null) {
				this.nomeAlterouObs = obterNomeAutorObservacao(observacaoCenso.getSerMatriculaAltera(), observacaoCenso.getSerVinCodigoAltera());
			}
		} else {
			observacaoCensoInternacao = "";
		}
		this.permitirAlterarObservacao = this.permitirAlteracaoObservacao();
	}

	public String obterObservacaoDaInternacaoTooltip(VAinCensoVO censo) {
		String obs = "";
		observacaoCenso = this.pesquisaInternacaoFacade.obterObservacaoDaInternacao(censo.getInternacaoSeq(), this.data);
		if (observacaoCenso != null) {
			obs = observacaoCenso.getDescricao();
		}
		return obs;
	}

	public void excluirObservacao() {
		if (StringUtils.isNotBlank(this.observacaoCensoInternacao)) {

			this.pesquisaInternacaoFacade.excluirObservacaoDaInternacao(this.observacaoCenso);
			limparModalObs();
			this.dataCriacaoObs = null;
			this.nomeCriouObs = null;
			this.nomeAlterouObs = null;
			getDataModel().reiniciarPaginator();
		}

	}

	/**
	 * Obtem o nome do usuario
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public String obterNomeAutorObservacao(Integer matricula, Short vinCodigo) {
		String nome = "";
		RapServidores servidor = null;
		if (matricula != null && vinCodigo != null) {
			servidor = this.registroColaboradorFacade.obterServidor(vinCodigo, matricula);
			nome = servidor.getPessoaFisica().getNome();
		}
		return nome;
	}

	/**
	 * Grava (inclui ou edita) a observacao no banco de dados.
	 */
	public void gravarObservacao() {
		try {
			if (StringUtils.isNotBlank(this.observacaoCensoInternacao)) {
				if (this.observacaoCenso == null) {
					observacaoCenso = new AinObservacoesCenso();
					AinObservacoesCensoId id = new AinObservacoesCensoId();
					id.setIntSeq(this.internacaoSeq);
					observacaoCenso.setId(id);
					observacaoCenso.setAinInternacao(this.pesquisaInternacaoFacade.obterInternacao(this.internacaoSeq));
				}

				this.observacaoCenso.setDescricao(this.observacaoCensoInternacao);
				this.pesquisaInternacaoFacade.persistirObservacao(this.observacaoCenso);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	public Boolean getMostrarEstadoSaude() {
		if (!DateUtils.isSameDay(this.data, new Date())) {
			return false;
		} else {
			Short seqUnidadeFuncional = 0;

			if (this.unidadeFuncionalMae != null) {
				seqUnidadeFuncional = this.unidadeFuncionalMae.getSeq();
			} else if (this.unidadeFuncional != null) {
				seqUnidadeFuncional = this.unidadeFuncional.getSeq();
			}

			return this.pesquisaInternacaoFacade.mostrarEstadoSaude(seqUnidadeFuncional);
		}
	}
	
	public String imprimirCensoDiario() throws ApplicationBusinessException{

		if (pesquisaCensoInvalida()) {
			return null;
		}
		
		if(listaEhVazia()){
			this.apresentarMsgNegocio(Severity.WARN, "LISTA_VAZIA");
			return null;
		}		
		
		relatorioCensoDiarioPacientesController.setUnidadeFuncionalSeq(this.unidadeFuncional != null ? this.unidadeFuncional.getSeq() : null);
		relatorioCensoDiarioPacientesController.setUnidadeFuncionalPai(this.unidadeFuncionalMae != null ? this.unidadeFuncionalMae.getSeq() : null);
		relatorioCensoDiarioPacientesController.setData(data);
		relatorioCensoDiarioPacientesController.setStatus(status);

		return PAGE_RELATORIO_CENSO_DIARIO;
	}
	
	private boolean listaEhVazia() throws ApplicationBusinessException {

		if (recuperarCount() == 0 || recuperarCount() == null) {
			return true;
		}
		return false;
	}

	private Boolean pesquisaCensoInvalida() {

		if (pesquisaUnidadeFuncionalInvalida()) {

			this.apresentarMsgNegocio(Severity.ERROR, "AIN_00784");
			return Boolean.TRUE;

		} else if (this.unidadeFuncional != null && this.unidadeFuncionalMae != null) {
			this.unidadeFuncionalMae = null;
		}

		return Boolean.FALSE;
	}

	private Boolean pesquisaUnidadeFuncionalInvalida() {
		return this.unidadeFuncional == null && this.unidadeFuncionalMae == null;
	}

	// ### GETs e SETs ###
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalMae() {
		return unidadeFuncionalMae;
	}

	public void setUnidadeFuncionalMae(AghUnidadesFuncionais unidadeFuncionalMae) {
		this.unidadeFuncionalMae = unidadeFuncionalMae;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public DominioSituacaoUnidadeFuncional getStatus() {
		return status;
	}

	public void setStatus(DominioSituacaoUnidadeFuncional status) {
		this.status = status;
	}

	public String getObservacaoCensoInternacao() {
		return observacaoCensoInternacao;
	}

	public void setObservacaoCensoInternacao(String observacaoCensoInternacao) {
		this.observacaoCensoInternacao = observacaoCensoInternacao;
	}

	public boolean isExibirModalConfirmacaoInternacao() {
		return exibirModalConfirmacaoInternacao;
	}

	public void setExibirModalConfirmacaoInternacao(boolean exibirModalConfirmacaoInternacao) {
		this.exibirModalConfirmacaoInternacao = exibirModalConfirmacaoInternacao;
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public AinObservacoesCenso getObservacaoCenso() {
		return observacaoCenso;
	}

	public void setObservacaoCenso(AinObservacoesCenso observacaoCenso) {
		this.observacaoCenso = observacaoCenso;
	}

	public boolean isPermitirAlterarObservacao() {
		return permitirAlterarObservacao;
	}

	public void setPermitirAlterarObservacao(boolean permitirAlterarObservacao) {
		this.permitirAlterarObservacao = permitirAlterarObservacao;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}

	public boolean isExibirBotaoInternacao() {
		return exibirBotaoInternacao;
	}

	public void setExibirBotaoInternacao(boolean exibirBotaoInternacao) {
		this.exibirBotaoInternacao = exibirBotaoInternacao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public String getNomeAlterouObs() {
		return nomeAlterouObs;
	}

	public void setNomeAlterouObs(String nomeAlterouObs) {
		this.nomeAlterouObs = nomeAlterouObs;
	}

	public String getNomeCriouObs() {
		return nomeCriouObs;
	}

	public void setNomeCriouObs(String nomeCriouObs) {
		this.nomeCriouObs = nomeCriouObs;
	}

	public String getDataCriacaoObs() {
		return dataCriacaoObs;
	}

	public void setDataCriacaoObs(String dataCriacaoObs) {
		this.dataCriacaoObs = dataCriacaoObs;
	}

	public boolean isAtualizarCenso() {
		return atualizarCenso;
	}

	public void setAtualizarCenso(boolean atualizarCenso) {
		this.atualizarCenso = atualizarCenso;
	}

	public DynamicDataModel<VAinCensoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAinCensoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AghAtendimentos getAghAtendimentos() {
		return aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	public VAinCensoVO getCensoSelecionado() {
		return censoSelecionado;
	}

	public void setCensoSelecionado(VAinCensoVO censoSelecionado) {
		this.censoSelecionado = censoSelecionado;
	}
}