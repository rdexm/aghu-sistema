package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioConselho;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioModoNascimento;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.DataNascimentoVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * @author israel.haas
 */

@SuppressWarnings("PMD.ExcessiveClassLength")
public class RegistrarGestacaoAbaNascimentoController extends ActionController {

	private static final long serialVersionUID = -1712613170642413556L;
	
	private static final String MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO = "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO";
	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	private static final Integer ABA_NASCIMENTO = 4;
	private final String PARTO = "PARTO";
	private final String CESAREANA = "CESAREANA";

	@EJB
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private RegistrarGestacaoController registrarGestacaoController;

	@Inject
	private NotaAdicionalController notaAdicionalController;

	@Inject
	private RegistrarGestacaoAbaNascimentoFieldsetPartoController registrarGestacaoAbaNascimentoFieldsetPartoController;

	@Inject
	private RegistrarGestacaoAbaNascimentoFieldsetCesarianaController registrarGestacaoAbaNascimentoFieldsetCesarianaController;

	@Inject
	private RegistrarGestacaoAbaNascimentoFieldsetInstrumentadoController registrarGestacaoAbaNascimentoFieldsetInstrumentadoController;

	@Inject
	private RegistrarGestacaoAbaNascimentoFieldsetIntercorrenciasController registrarGestacaoAbaNascimentoFieldsetIntercorrenciasController;

	@Inject
	private RegistrarGestacaoAbaNascimentoFieldsetAgendamentoController registrarGestacaoAbaNascimentoFieldsetAgendamentoController;

	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IPermissionService permissionService;

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private boolean ativoBlocoCirurgico;

	private Boolean semMensagem = Boolean.FALSE;

	private Boolean okParaSumarioDefinitivo;

	private ServidorIdVO servidorIdVO;

	private Boolean podeExecutarRn02;

	private String hostName;
	private Integer pacCodigo;
	private Short gsoSeqp;
	private Integer numeroConsulta;

	private List<DadosNascimentoVO> listaNascimentos;
	private DadosNascimentoVO nascimentoSelecionado;
	private DadosNascimentoVO nascimento;
	private DadosNascimentoVO nascimentoExcluir;
	private TipoAnestesiaVO tipoAnestesia;

	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionado;
	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal;

	private McoForcipes mcoForcipesOriginal;
	private McoCesarianas mcoCesarianasOriginal;
	private McoNascimentos mcoNascimentosOriginal;

	private boolean permManterNascimentos;
	private boolean permExecutarIndicacoesAbaNascimento;
	private boolean permManterIntercorrenciaNascimento;

	private Boolean isParto;
	private Boolean isCesarea;
	private Boolean isModoInstrumentado;

	private Boolean modoEdicao = Boolean.FALSE;

	private Boolean mostraModalDtHrNascimento;
	private Boolean mostraModalExclusao;
	private Boolean mostraModalGravarDadosNascimento;

	private Boolean houveAlteracao = Boolean.FALSE;

	private Boolean mostraModalSolicitarVdrl = Boolean.FALSE;
	private static final String REDIRECT_VOLTAR_PARA = "/emergencia/pages/perinatologia/registrarGestacao.xhtml";
	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "pesquisaGestacoesList";
	private static final String ABA_DESTINO = "abaNascimento";
	private String voltarPara;
	private String abaDestino;
	private Integer atdSeq;
	private Boolean desabilitarCampos = Boolean.FALSE;
	private Boolean habilitaPesoAborto = Boolean.TRUE;
	private Boolean desabilitaConfirmarProcedimentos;
	private String nomeBotaoProcedimento;
	private Boolean ehBotaoConfirmarProcedimento = Boolean.TRUE;

	public void inicio() throws ReflectiveOperationException {
		okParaSumarioDefinitivo = Boolean.FALSE;
		this.setNomeBotaoProcedimento(this.getBundle().getString(
				"LABEL_CONFIRMAR_PROCEDIMENTO"));
		try {
			this.hostName = this.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,
					"Não foi possivel pegar o servidor logado");
		}
		setIsModoInstrumentado(Boolean.FALSE);
		setMostraModalDtHrNascimento(Boolean.FALSE);
		setMostraModalGravarDadosNascimento(Boolean.FALSE);
		this.registrarGestacaoController.setAbaDestino(ABA_NASCIMENTO);
		setHouveAlteracao(Boolean.FALSE);
		String usuarioLogado = super.obterLoginUsuarioLogado();
		this.permManterNascimentos = permissionService
				.usuarioTemPermissao(usuarioLogado, "manterNascimento",
						"executar");
		this.permExecutarIndicacoesAbaNascimento = permissionService
				.usuarioTemPermissao(usuarioLogado,
						"executarIndicacoesAbaNascimento", "executar");
		this.permManterIntercorrenciaNascimento = permissionService
				.usuarioTemPermissao(usuarioLogado,
						"manterIntercorrenciaNascimento", "executar");
		this.pesquisarNascimentos();
		this.setVoltarPara(REDIRECT_VOLTAR_PARA);
		this.setAbaDestino(ABA_DESTINO);
		setAtivoBlocoCirurgico(cascaFacade
				.verificarSeModuloEstaAtivo(DominioModulo.BLOCO_CIRURGICO
						.getDescricao()));
		try {
			this.validarRegrasBotaoConfirmarProcedimento();
		} catch (ApplicationBusinessException e) {
			this.desabilitaConfirmarProcedimentos = Boolean.TRUE;
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	private void validarRegrasBotaoConfirmarProcedimento()
			throws ApplicationBusinessException {
		if (this.ativoBlocoCirurgico) {
			List<Short> listaSeqp = bucarCaracteristicasUnidadesFuncionais();
			emergenciaFacade.validarListaUnidadesFuncionais(listaSeqp);

			validarDatasDaCirurgia(listaSeqp);
		}
	}

	private void validarDatasDaCirurgia(List<Short> listaSeqp) {
		List<DataNascimentoVO> datasNascimento = this.emergenciaFacade
				.buscarDatasDoNascimentos(this.getPacCodigo(),
						this.getGsoSeqp());
		Boolean c11RetornouResultado = (datasNascimento == null || datasNascimento
				.size() == 0);
		// 4. Se a consulta C11 retornar resultados executar consulta C2
		// passando a lista de datas encontradas na consulta C11 e o código do
		// paciente
		if (c11RetornouResultado) {
			List<Date> listaDatas = bindDatas(datasNascimento);
			if (listaDatas.size() > 0) {
				this.validarLabelBotaoDescricaoCirurgica(listaSeqp, listaDatas);
			}
			// 3. Se a consulta C11 não retornar resultados habilitar o botão
			// Confirmar Procedimento e manter o label Confirmar Procedimento.
		} else {
			this.desabilitaConfirmarProcedimentos = Boolean.FALSE;
		}
	}

	private void validarLabelBotaoDescricaoCirurgica(List<Short> listaSeqp,
			List<Date> listaDatas) {
		List<MbcCirurgiaVO> cirurgias = this.blocoCirurgicoFacade
				.obterCirurgiasPorPacienteEDatasGestacao(listaDatas,
						this.getPacCodigo(), listaSeqp);

		if (cirurgias != null && cirurgias.size() > 0) {
			this.setDesabilitaConfirmarProcedimentos(Boolean.FALSE);
			this.setNomeBotaoProcedimento(this.getBundle().getString(
					"LABEL_DERCRICAO_CIRURGICA"));
			ehBotaoConfirmarProcedimento = Boolean.FALSE;
		}
	}

	private List<Date> bindDatas(List<DataNascimentoVO> datasNascimento) {
		List<Date> listaDatas = new ArrayList<Date>();
		if (datasNascimento != null) {
			for (DataNascimentoVO vo : datasNascimento) {
				listaDatas.add(vo.getDtNascimento());
			}
		}
		return listaDatas;
	}

	private List<Short> bucarCaracteristicasUnidadesFuncionais() {
		return this.aghuFacade.pesquisarUnidFuncExecutora();
	}

	public void confirmarProcedimento() throws NumberFormatException, ReflectiveOperationException {
		this.gravarDadosAbaNascimento();
		try {
			this.emergenciaFacade
					.validarProsseguirComGravarNascimento(
							this.nascimentoSelecionado,
							this.registrarGestacaoAbaNascimentoFieldsetAgendamentoController
									.getDadosNascimentoSelecionado()
									.getMcoCesariana());
			this.emergenciaFacade.validarAnestesia(nascimentoSelecionado
					.getTanSeq());
			this.emergenciaFacade
					.validarFieldSetNascimento(this.registrarGestacaoAbaNascimentoFieldsetAgendamentoController
							.getDadosNascimentoSelecionado());
			this.emergenciaFacade
					.validarAgendamento(registrarGestacaoAbaNascimentoFieldsetAgendamentoController
							.getDadosNascimentoSelecionado());
			this.emergenciaFacade
					.validarEquipe(registrarGestacaoAbaNascimentoFieldsetAgendamentoController
							.getEquipe());

			executarInsercaoCirurgiaPeloConfirmarProcedimento();
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_CONFIRMACAO_PROCEDIMENTO");
			this.setNomeBotaoProcedimento(this.getBundle().getString(
					"LABEL_DERCRICAO_CIRURGICA"));
		} catch (ServiceBusinessException e) {
			this.apresentarMsgNegocio(e.getMessage());
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(e.getMessage());
		}
	}

	private void executarInsercaoCirurgiaPeloConfirmarProcedimento()
			throws NumberFormatException, ServiceException,
			ServiceBusinessException {
		if (nascimentoSelecionado != null) {
			String[] tempo = registrarGestacaoAbaNascimentoFieldsetAgendamentoController
					.getDadosNascimentoSelecionado().getTempoProcedimento()
					.split(":");
			Short hora = Short.valueOf(tempo[0]);
			if (DominioTipoNascimento.P == nascimentoSelecionado
					.getTipoNascimento()) {
				this.emergenciaFacade.confirmarProcedimento(this.pacCodigo,
						this.gsoSeqp,
						"N",
						registrarGestacaoAbaNascimentoFieldsetAgendamentoController
								.getDadosNascimentoSelecionado()
								.getDthrInicioProcedimento(),
						// nascimentoSelecionado.getSeqp().shortValue(),
						registrarGestacaoAbaNascimentoFieldsetAgendamentoController
								.getSalaCirurgica().getSeqp(), Short
								.parseShort(hora.toString() + tempo[1]),
						nascimentoSelecionado.getTanSeq(),
						registrarGestacaoAbaNascimentoFieldsetAgendamentoController
								.getEquipe().getSeq().shortValue(), PARTO);
			} else {
				this.emergenciaFacade
						.confirmarProcedimento(
								this.pacCodigo,
								this.gsoSeqp,
								this.registrarGestacaoAbaNascimentoFieldsetAgendamentoController
										.getDadosNascimentoSelecionado()
										.getMcoCesariana().getContaminacao()
										.toString(),
								registrarGestacaoAbaNascimentoFieldsetAgendamentoController
										.getDadosNascimentoSelecionado()
										.getDthrInicioProcedimento(),
								// nascimentoSelecionado.getSeqp().shortValue(),
								registrarGestacaoAbaNascimentoFieldsetAgendamentoController
										.getSalaCirurgica().getSeqp(),
								Short.parseShort(hora.toString() + tempo[1]),
								nascimentoSelecionado.getTanSeq(),
								registrarGestacaoAbaNascimentoFieldsetAgendamentoController
										.getEquipe().getSeq().shortValue(),
								CESAREANA);
			}
		}
	}

	public void pesquisarNascimentos() throws ReflectiveOperationException {
		setIsParto(Boolean.FALSE);
		setIsCesarea(Boolean.FALSE);
		setIsModoInstrumentado(Boolean.FALSE);
		this.nascimento = new DadosNascimentoVO();
		try {
			this.listaNascimentos = this.emergenciaFacade
					.pesquisarMcoNascimentoPorId(this.pacCodigo, this.gsoSeqp);
			if (!this.listaNascimentos.isEmpty()) {
				setNascimentoSelecionado(this.listaNascimentos.get(0));
				this.atualizaDadosNascimento();
			} else {
				this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
						.limparFieldset();
				this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
						.limparFieldset();
				this.registrarGestacaoAbaNascimentoFieldsetIntercorrenciasController
						.limparFieldset();
				setNascimentoSelecionado(null);
				this.nascimento.setDtHrNascimento(this.emergenciaFacade
						.obterDtHrNascimento(this.pacCodigo, this.gsoSeqp));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparNascimento(DadosNascimentoVO nascimentoPersistido) throws ReflectiveOperationException {
		setMostraModalDtHrNascimento(Boolean.FALSE);
		setModoEdicao(Boolean.FALSE);
		this.nascimento = new DadosNascimentoVO();
		this.tipoAnestesia = null;
		try {
			this.listaNascimentos = this.emergenciaFacade
					.pesquisarMcoNascimentoPorId(this.pacCodigo, this.gsoSeqp);
			for (DadosNascimentoVO item : this.listaNascimentos) {
				if (item.getSeqp().equals(nascimentoPersistido.getSeqp())) {
					setNascimentoSelecionado(item);
				}
			}
			this.atualizaDadosNascimento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<TipoAnestesiaVO> pesquisarTiposAnestesiasAtivas(String param) {
		return  this.returnSGWithCount(this.emergenciaFacade.pesquisarTiposAnestesiasAtivas(param),pesquisarTiposAnestesiasAtivasCount(param));
	}

	public Long pesquisarTiposAnestesiasAtivasCount(String param) {
		return this.emergenciaFacade
				.pesquisarTiposAnestesiasAtivasCount(param);
	}

	public void atualizaDadosNascimento() throws ApplicationBusinessException {
		if (this.nascimentoSelecionado != null) {
			this.dadosNascimentoSelecionado = this.emergenciaFacade
					.obterDadosNascimentoSelecionado(
							this.nascimentoSelecionado.getSeqp(),
							this.pacCodigo, this.gsoSeqp);

			try {
				atualizarDadosNascimentoSelecionadoVOOriginal();
				atualizarMcoForcipesOriginal();
				atualizarMcoCesarianasOriginal();
				atualizarMcoNascimentosOriginal();
			} catch (ReflectiveOperationException e) {
				throw new IllegalArgumentException();
			}
			

			if (this.nascimentoSelecionado.getTipoNascimento().equals(
					DominioTipoNascimento.P)) {
				setIsParto(Boolean.TRUE);
				this.registrarGestacaoAbaNascimentoFieldsetPartoController
						.prepararFieldset(this.pacCodigo, this.gsoSeqp,
								this.nascimentoSelecionado,
								this.dadosNascimentoSelecionado);

				setIsCesarea(Boolean.FALSE);
				this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
						.limparFieldset();

			} else if (this.nascimentoSelecionado.getTipoNascimento().equals(
					DominioTipoNascimento.C)) {
				setIsParto(Boolean.FALSE);

				setIsCesarea(Boolean.TRUE);
				this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
						.prepararFieldset(pacCodigo, gsoSeqp,
								nascimentoSelecionado.getSeqp(),
								this.dadosNascimentoSelecionado,
								this.mcoCesarianasOriginal);
			}

			if (DominioModoNascimento.F.equals(this.nascimentoSelecionado
					.getModoNascimento())) {
				setIsModoInstrumentado(Boolean.TRUE);
				this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
						.prepararFieldset(pacCodigo, gsoSeqp,
								nascimentoSelecionado.getSeqp(),
								this.dadosNascimentoSelecionado,
								this.mcoForcipesOriginal);
			} else {
				setIsModoInstrumentado(Boolean.FALSE);
				this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
						.limparFieldset();
			}

			this.registrarGestacaoAbaNascimentoFieldsetAgendamentoController
					.preparaTela(getGsoSeqp(), getPacCodigo(),
							getNascimentoSelecionado().getSeqp(),
							getNumeroConsulta(),
							this.dadosNascimentoSelecionado);
			registrarGestacaoAbaNascimentoFieldsetIntercorrenciasController
					.prepararFieldset(pacCodigo, gsoSeqp,
							nascimentoSelecionado.getSeqp(), numeroConsulta);
		}
	}

	public void atualizarDadosNascimentoSelecionadoVOOriginal()
			throws ReflectiveOperationException {
		this.dadosNascimentoSelecionadoOriginal = new DadosNascimentoSelecionadoVO();

		PropertyUtils.copyProperties(this.dadosNascimentoSelecionadoOriginal,
				this.dadosNascimentoSelecionado);

	}

	public void atualizarMcoForcipesOriginal()
			throws ReflectiveOperationException {
		this.mcoForcipesOriginal = new McoForcipes();

		PropertyUtils.copyProperties(this.mcoForcipesOriginal,
				this.dadosNascimentoSelecionado.getMcoForcipe());

	}

	public void atualizarMcoCesarianasOriginal()
			throws ReflectiveOperationException {
		this.mcoCesarianasOriginal = new McoCesarianas();

		PropertyUtils.copyProperties(this.mcoCesarianasOriginal,
				this.dadosNascimentoSelecionado.getMcoCesariana());

	}

	public void atualizarMcoNascimentosOriginal()
			throws ReflectiveOperationException {
		this.mcoNascimentosOriginal = new McoNascimentos();

		PropertyUtils.copyProperties(this.mcoNascimentosOriginal,
				this.dadosNascimentoSelecionado.getMcoNascimento());

	}

	public void editarNascimento() {
		setModoEdicao(Boolean.TRUE);
		if (this.nascimento.getTanSeq() != null) {
			try {
				setTipoAnestesia(this.emergenciaFacade
						.obterAnestesiaAtiva(this.nascimento.getTanSeq()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void cancelarEdicaoNascimento() {
		setModoEdicao(Boolean.FALSE);
		try {
			this.pesquisarNascimentos();
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException();
		}
	}

	public void prepararExclusaoNascimento() {
		this.setMostraModalExclusao(Boolean.TRUE);
		openDialog("modalConfirmacaoExclusaoWG");
	}

	public void excluirNascimento() throws ReflectiveOperationException {
		try {
			this.emergenciaFacade.excluirNascimento(this.nascimentoExcluir,
					this.pacCodigo, this.gsoSeqp);
			this.setMostraModalExclusao(Boolean.FALSE);
			this.pesquisarNascimentos();
			this.apresentarMsgNegocio(Severity.INFO,
					"EXCLUSAO_NASCIMENTO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			this.setMostraModalExclusao(Boolean.FALSE);
			if (e.getCode().toString().equals("ERRO_EXCLUIR_ULTIMO_NASCIMENTO")) {
				pesquisarNascimentos();
			}
			apresentarExcecaoNegocio(e);
		}
	}

	public void preGravarNascimento() throws ReflectiveOperationException {
		setMostraModalDtHrNascimento(Boolean.FALSE);
		try {
			boolean exibirModalDtHrNascimento = this.emergenciaFacade
					.preGravarNascimento(this.pacCodigo, this.gsoSeqp,
							this.nascimento);
			if (exibirModalDtHrNascimento) {
				setMostraModalDtHrNascimento(Boolean.TRUE);
				openDialog("modalDtHrNascimentoWG");
			} else {
				this.gravarNascimento();
			}

		} catch (ApplicationBusinessException e) {
			if (e.getCode().toString()
					.equals("ERRO_EXISTE_DATA_HORA_NASCIMENTO")
					|| e.getCode().toString().equals("ERRO_GESTACAO_GEMELAR")) {
				if (modoEdicao) {
					cancelarEdicaoNascimento();
				} else {
					pesquisarNascimentos();
				}
			}
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravarNascimento() throws ReflectiveOperationException {
		setMostraModalDtHrNascimento(Boolean.FALSE);
		boolean alteracao = this.nascimento.getSeqp() != null;
		try {
			Short anestesiaSeq = this.tipoAnestesia != null ? this.tipoAnestesia
					.getSeq() : null;
			DadosNascimentoVO nascimentoPersistido = this.emergenciaFacade
					.gravarNascimento(this.pacCodigo, this.gsoSeqp,
							this.nascimento, anestesiaSeq);
			if (!this.getSemMensagem()) {
				if (alteracao) {
					this.apresentarMsgNegocio(Severity.INFO,
							"MENSAGEM_ALTERACAO_NASCIMENTO_SUCESSO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO,
							"MENSAGEM_INCLUSAO_NASCIMENTO_SUCESSO");
				}
			}
			this.limparNascimento(nascimentoPersistido);
			setMostraModalGravarDadosNascimento(Boolean.FALSE);
			this.registrarGestacaoController.preparaAbaDestino();
		} catch (ApplicationBusinessException e) {
			if (!alteracao) {
				this.nascimento.setSeqp(null);
			}
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravarDadosAbaNascimento() throws ReflectiveOperationException {
		if (this.modoEdicao) {
			this.gravarNascimento();
		} else if (this.houveAlteracao) {
			// Seta no VO os dados das grades.
			this.atualizarDadosNascimentoVO();
			if (validarPreenchimentoCampos()) {
				try {
					this.emergenciaFacade.gravarDadosAbaNascimento(
							this.numeroConsulta, this.hostName,
							this.nascimentoSelecionado,
							this.dadosNascimentoSelecionado,
							this.dadosNascimentoSelecionadoOriginal);
					setHouveAlteracao(Boolean.FALSE);
					setMostraModalGravarDadosNascimento(Boolean.FALSE);
					this.registrarGestacaoController.preparaAbaDestino();
					if (!this.getSemMensagem()) {
						this.apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_SUCESSO_REGISTRO_NASCIMENTO");
					}

				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	private void atualizarDadosNascimentoVO() {
		if (this.isCesarea) {
			// Cesariana
			this.dadosNascimentoSelecionado
					.setListaNascIndicacoesCesariana(this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
							.getListaNascIndicacoes());
			this.dadosNascimentoSelecionado
					.setMcoCesarianaExcluir(this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
							.getMcoCesarianaExcluir());
			this.dadosNascimentoSelecionado
					.setListaNascIndicacoesCesarianaRemover(this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
							.getListaNascIndicacoesRemover());
			this.dadosNascimentoSelecionado
					.setIndicacaoPartoVOCesarianaRemover(this.registrarGestacaoAbaNascimentoFieldsetCesarianaController
							.getIndicacaoPartoVORemover());
		}
		if (this.isModoInstrumentado) {
			// Instrumentado
			this.dadosNascimentoSelecionado
					.setListaNascIndicacoesForcipe(this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
							.getListaNascIndicacoes());
			this.dadosNascimentoSelecionado
					.setMcoForcipesExcluir(this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
							.getMcoForcipesExcluir());
			this.dadosNascimentoSelecionado
					.setListaNascIndicacoesForcipeRemover(this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
							.getListaNascIndicacoesRemover());
			this.dadosNascimentoSelecionado
					.setIndicacaoPartoVOForcipeRemover(this.registrarGestacaoAbaNascimentoFieldsetInstrumentadoController
							.getIndicacaoPartoVORemover());
		}
		// Atualiza o VO original
		this.dadosNascimentoSelecionadoOriginal
				.setMcoForcipe(this.mcoForcipesOriginal);
		this.dadosNascimentoSelecionadoOriginal
				.setMcoCesariana(this.mcoCesarianasOriginal);
		this.dadosNascimentoSelecionadoOriginal
				.setMcoNascimento(this.mcoNascimentosOriginal);
	}

	private boolean validarPreenchimentoCampos() {
		boolean validou = true;
		if (this.isParto
				&& (this.nascimentoSelecionado.getPeriodoExpulsivo() == null
						|| this.nascimentoSelecionado.getPeriodoExpulsivo()
								.isEmpty() || this.nascimentoSelecionado
						.getPeriodoExpulsivo().length() < 4)) {
			this.apresentarMsgNegocio("periodoExpulsivo", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Período Expulsivo");
			validou = false;
		}
		McoCesarianas cesariana = this.dadosNascimentoSelecionado
				.getMcoCesariana();
		if (this.isCesarea && cesariana != null) {
			if (cesariana.getDthrIndicacao() == null) {
				this.apresentarMsgNegocio("dthrIndicacao", Severity.ERROR,
						CAMPO_OBRIGATORIO, "Hora da Indicação");
				validou = false;
			}
			if (cesariana.getDthrPrevInicio() == null) {
				this.apresentarMsgNegocio("dthrPrevInicio", Severity.ERROR,
						CAMPO_OBRIGATORIO, "Hora de Início");
				validou = false;
			}
			if (cesariana.getDthrIncisao() == null) {
				this.apresentarMsgNegocio("dthrIncisao", Severity.ERROR,
						CAMPO_OBRIGATORIO, "Hora da Incisão");
				validou = false;
			}
		}
		McoForcipes forcipe = this.dadosNascimentoSelecionado.getMcoForcipe();
		if (this.isModoInstrumentado && forcipe != null
				&& forcipe.getTipoForcipe() == null) {
			this.apresentarMsgNegocio("tipoForcipe", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Tipo");
			validou = false;
		}
		return validou;
	}

	public void prepararTela(Integer pacCodigo, Short gsoSeqp,
			Integer numeroConsulta)  {
		this.pacCodigo = pacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.numeroConsulta = numeroConsulta;
		try {
			this.inicio();
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException();
		}
	}

	public DominioModoNascimento[] getValoresDominioModoNascimento() {
		return new DominioModoNascimento[] { //
		DominioModoNascimento.E, //
				DominioModoNascimento.D, //
				DominioModoNascimento.F,//
				DominioModoNascimento.P, //
				DominioModoNascimento.N };
	}

	public void validarDadosSumarioDefinitvo() throws ServiceException, ReflectiveOperationException {
		this.setSemMensagem(Boolean.TRUE);
		Boolean moduloBlocoCirurgicoAtivo = Boolean.FALSE;
		if (this.cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.BLOCO_CIRURGICO
				.getDescricao())) {
			moduloBlocoCirurgicoAtivo = Boolean.TRUE;
		}

		setPodeExecutarRn02(Boolean.FALSE);
		try {
			if (moduloBlocoCirurgicoAtivo) {
				setPodeExecutarRn02(this.emergenciaFacade
						.gravarSumarioDefinitivo(this.pacCodigo, this.gsoSeqp,
								this.numeroConsulta, this.hostName,
								this.nascimentoSelecionado,
								this.dadosNascimentoSelecionado,
								this.dadosNascimentoSelecionadoOriginal));
			}
			this.gravarDadosAbaNascimento();
			Boolean acompanhante = this.obterInformacaoAcompanhante();
			this.emergenciaFacade.validarDados(acompanhante, this.pacCodigo,
					this.gsoSeqp, this.registrarGestacaoController
							.getDadosGestacaoVO(),
					registrarGestacaoAbaNascimentoFieldsetAgendamentoController
							.getEquipe(), this.nascimentoSelecionado);
			okParaSumarioDefinitivo = Boolean.TRUE;
			this.setAtdSeq(this.emergenciaFacade
					.solicitarExameVdrl(this.numeroConsulta));
			openDialog("autenticacaoSumarioDefinitivoWG");
		} catch (ApplicationBusinessException e) {
			okParaSumarioDefinitivo = Boolean.FALSE;
			this.apresentarExcecaoNegocio(e);
		}
	}

	private Boolean obterInformacaoAcompanhante() {
		if (registrarGestacaoAbaNascimentoFieldsetAgendamentoController
				.getDadosNascimentoSelecionado() != null
				&& registrarGestacaoAbaNascimentoFieldsetAgendamentoController
						.getDadosNascimentoSelecionado().getMcoNascimento() != null) {
			return registrarGestacaoAbaNascimentoFieldsetAgendamentoController
					.getDadosNascimentoSelecionado().getMcoNascimento()
					.getIndAcompanhante();
		}
		return null;
	}

	public void finalizarSumarioDefinitivo() {
		this.okParaSumarioDefinitivo = Boolean.FALSE;
		setPodeExecutarRn02(Boolean.FALSE);

		RapServidorConselhoVO conselhoVO;
		try {
			conselhoVO = this.emergenciaFacade.verificarPermissaoUsuario(
					this.servidorIdVO.getMatricula(),
					this.servidorIdVO.getSerVinCodigo());

			Integer atdSeq = emergenciaFacade
					.obterSeqAtendimentoPorConNumero(numeroConsulta);
			this.mostraModalSolicitarVdrl = !emergenciaFacade
					.verificarExameVDRLnaoSolicitado(atdSeq);

			if (!this.mostraModalSolicitarVdrl) {
				this.naoSolicitarExameVdrl(conselhoVO.getSigla());
			}
			this.emergenciaFacade.atualizarSituacaoPaciente(
					this.numeroConsulta, obterLoginUsuarioLogado());

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			mostraModalSolicitarVdrl = false;
			FacesContext.getCurrentInstance().validationFailed();
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
			mostraModalSolicitarVdrl = false;
		}
	}

	public void naoSolicitarExameVdrl() {
		Object[] objConselho = this.emergenciaFacade
				.obterConselhoESiglaVRapServidorConselho();
		if (objConselho != null
				&& objConselho[0].equals(DominioConselho.COREN.getDescricao())) {
			this.emergenciaFacade
					.executaImpressaoGeracaoPendenciaAssinatura(this.pacCodigo,
							this.gsoSeqp);
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO);

		} else if (objConselho != null
				&& objConselho[0]
						.equals(DominioConselho.CREMERS.getDescricao())) {

			this.emergenciaFacade
					.executaImpressaoGeracaoPendenciaAssinatura(this.pacCodigo,
							this.gsoSeqp);
			try {
				this.desabilitarCampos = this.emergenciaFacade.gerarPendenciaAssinaturaDigital();
			} catch (ServiceException | ApplicationBusinessException e) {
				this.apresentarMsgNegocio(e.getMessage());
				FacesContext.getCurrentInstance().validationFailed();
			}
			this.desabilitarCampos = true;
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO);
			this.apresentarMsgNegocio(Severity.INFO,
					"PENDENCIA_ASSINATURA_GERADA_SUCESSO");
		} else {
			FacesContext.getCurrentInstance().validationFailed();
		}
	}

	public void naoSolicitarExameVdrl(String sigla) {

		if (DominioConselho.COREN.getDescricao().equals(sigla)) {
			this.emergenciaFacade
					.executaImpressaoGeracaoPendenciaAssinatura(this.pacCodigo,
							this.gsoSeqp);
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO);
		} else if (DominioConselho.CREMERS.getDescricao().equals(sigla)) {
			this.emergenciaFacade
					.executaImpressaoGeracaoPendenciaAssinatura(this.pacCodigo,
							this.gsoSeqp);
			try {
				this.desabilitarCampos = this.emergenciaFacade.gerarPendenciaAssinaturaDigital();
			} catch (ServiceException | ApplicationBusinessException e) {
				this.apresentarMsgNegocio(e.getMessage());
				FacesContext.getCurrentInstance().validationFailed();
			}
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO_SUMARIO_DEFINITIVO);
			this.apresentarMsgNegocio(Severity.INFO,
					"PENDENCIA_ASSINATURA_GERADA_SUCESSO");
		} else {
			FacesContext.getCurrentInstance().validationFailed();
		}
	}

	public void prepararTelaNotaAdicional() {
		Integer pacCodigo = this.pacCodigo;
		Short seqp = this.gsoSeqp;
		Integer numeroConsulta = this.numeroConsulta;
		notaAdicionalController.setPacCodigo(pacCodigo);
		notaAdicionalController.setGsoSeqp(seqp);
		notaAdicionalController.setConNumero(numeroConsulta);
		notaAdicionalController
				.setEvento(DominioEventoNotaAdicional.MCOR_NASCIMENTO);
	}

	// Fluxo Alternativo [FA11] – Classificação igual à Nativivo
	public void validarClassificacaoNascimento() {
		if (this.nascimento.getClassificacao() != null
				&& this.nascimento.getClassificacao().equals(
						DominioRNClassificacaoNascimento.NAV)) {
			this.nascimento.setPesoAborto(null);
			this.habilitaPesoAborto = Boolean.FALSE;
		} else {
			this.habilitaPesoAborto = Boolean.TRUE;
		}
	}

	public String confirmarVoltar() {
		if (this.houveAlteracao) {
			this.setMostraModalGravarDadosNascimento(Boolean.TRUE);
			return null;
		}
		return voltar();
	}

	public String voltar() {
		this.setMostraModalGravarDadosNascimento(Boolean.FALSE);
		this.setHouveAlteracao(Boolean.FALSE);
		this.registrarGestacaoController.setAbaSelecionada(ABA_NASCIMENTO);

		return REDIRECIONA_PESQUISAR_GESTACOES;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public List<DadosNascimentoVO> getListaNascimentos() {
		return listaNascimentos;
	}

	public void setListaNascimentos(List<DadosNascimentoVO> listaNascimentos) {
		this.listaNascimentos = listaNascimentos;
	}

	public DadosNascimentoVO getNascimentoSelecionado() {
		return nascimentoSelecionado;
	}

	public void setNascimentoSelecionado(DadosNascimentoVO nascimentoSelecionado) {
		this.nascimentoSelecionado = nascimentoSelecionado;
	}

	public DadosNascimentoVO getNascimento() {
		return nascimento;
	}

	public void setNascimento(DadosNascimentoVO nascimento) {
		this.nascimento = nascimento;
	}

	public DadosNascimentoVO getNascimentoExcluir() {
		return nascimentoExcluir;
	}

	public void setNascimentoExcluir(DadosNascimentoVO nascimentoExcluir) {
		this.nascimentoExcluir = nascimentoExcluir;
	}

	public TipoAnestesiaVO getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(TipoAnestesiaVO tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionado() {
		return dadosNascimentoSelecionado;
	}

	public void setDadosNascimentoSelecionado(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
	}

	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionadoOriginal() {
		return dadosNascimentoSelecionadoOriginal;
	}

	public void setDadosNascimentoSelecionadoOriginal(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) {
		this.dadosNascimentoSelecionadoOriginal = dadosNascimentoSelecionadoOriginal;
	}

	public McoForcipes getMcoForcipesOriginal() {
		return mcoForcipesOriginal;
	}

	public void setMcoForcipesOriginal(McoForcipes mcoForcipesOriginal) {
		this.mcoForcipesOriginal = mcoForcipesOriginal;
	}

	public McoCesarianas getMcoCesarianasOriginal() {
		return mcoCesarianasOriginal;
	}

	public void setMcoCesarianasOriginal(McoCesarianas mcoCesarianasOriginal) {
		this.mcoCesarianasOriginal = mcoCesarianasOriginal;
	}

	public McoNascimentos getMcoNascimentosOriginal() {
		return mcoNascimentosOriginal;
	}

	public void setMcoNascimentosOriginal(McoNascimentos mcoNascimentosOriginal) {
		this.mcoNascimentosOriginal = mcoNascimentosOriginal;
	}

	public boolean isPermManterNascimentos() {
		return permManterNascimentos;
	}

	public void setPermManterNascimentos(boolean permManterNascimentos) {
		this.permManterNascimentos = permManterNascimentos;
	}

	public Boolean getIsParto() {
		return isParto;
	}

	public void setIsParto(Boolean isParto) {
		this.isParto = isParto;
	}

	public Boolean getIsCesarea() {
		return isCesarea;
	}

	public void setIsCesarea(Boolean isCesarea) {
		this.isCesarea = isCesarea;
	}

	public Boolean getIsModoInstrumentado() {
		return isModoInstrumentado;
	}

	public void setIsModoInstrumentado(Boolean isModoInstrumentado) {
		this.isModoInstrumentado = isModoInstrumentado;
	}

	public boolean isPermExecutarIndicacoesAbaNascimento() {
		return permExecutarIndicacoesAbaNascimento;
	}

	public void setPermExecutarIndicacoesAbaNascimento(
			boolean permExecutarIndicacoesAbaNascimento) {
		this.permExecutarIndicacoesAbaNascimento = permExecutarIndicacoesAbaNascimento;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getMostraModalDtHrNascimento() {
		return mostraModalDtHrNascimento;
	}

	public void setMostraModalDtHrNascimento(Boolean mostraModalDtHrNascimento) {
		this.mostraModalDtHrNascimento = mostraModalDtHrNascimento;
	}

	public Boolean getMostraModalExclusao() {
		return mostraModalExclusao;
	}

	public void setMostraModalExclusao(Boolean mostraModalExclusao) {
		this.mostraModalExclusao = mostraModalExclusao;
	}

	public Boolean getMostraModalGravarDadosNascimento() {
		return mostraModalGravarDadosNascimento;
	}

	public void setMostraModalGravarDadosNascimento(
			Boolean mostraModalGravarDadosNascimento) {
		this.mostraModalGravarDadosNascimento = mostraModalGravarDadosNascimento;
	}

	public Boolean getHouveAlteracao() {
		return houveAlteracao;
	}

	public void setHouveAlteracao(Boolean houveAlteracao) {
		this.houveAlteracao = houveAlteracao;
	}

	public Boolean getMostraModalSolicitarVdrl() {
		return mostraModalSolicitarVdrl;
	}

	public void setMostraModalSolicitarVdrl(Boolean mostraModalSolicitarVdrl) {
		this.mostraModalSolicitarVdrl = mostraModalSolicitarVdrl;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public boolean isPermManterIntercorrenciaNascimento() {
		return permManterIntercorrenciaNascimento;
	}

	public void setPermManterIntercorrenciaNascimento(
			boolean permManterIntercorrenciaNascimento) {
		this.permManterIntercorrenciaNascimento = permManterIntercorrenciaNascimento;
	}

	public boolean isAtivoBlocoCirurgico() {
		return ativoBlocoCirurgico;
	}

	public void setAtivoBlocoCirurgico(boolean ativoBlocoCirurgico) {
		this.ativoBlocoCirurgico = ativoBlocoCirurgico;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getAbaDestino() {
		return abaDestino;
	}

	public void setAbaDestino(String abaDestino) {
		this.abaDestino = abaDestino;
	}

	public Boolean getDesabilitarCampos() {
		return desabilitarCampos;
	}

	public void setDesabilitarCampos(Boolean desabilitarCampos) {
		this.desabilitarCampos = desabilitarCampos;
	}

	public Boolean getHabilitaPesoAborto() {
		return habilitaPesoAborto;
	}

	public void setHabilitaPesoAborto(Boolean habilitaPesoAborto) {
		this.habilitaPesoAborto = habilitaPesoAborto;
	}

	public String getNomeBotaoProcedimento() {
		return nomeBotaoProcedimento;
	}

	public void setNomeBotaoProcedimento(String nomeBotaoProcedimento) {
		this.nomeBotaoProcedimento = nomeBotaoProcedimento;
	}

	public Boolean getDesabilitaConfirmarProcedimentos() {
		return desabilitaConfirmarProcedimentos;
	}

	public void setDesabilitaConfirmarProcedimentos(
			Boolean desabilitaConfirmarProcedimentos) {
		this.desabilitaConfirmarProcedimentos = desabilitaConfirmarProcedimentos;
	}

	public Boolean getEhBotaoConfirmarProcedimento() {
		return ehBotaoConfirmarProcedimento;
	}

	public void setEhBotaoConfirmarProcedimento(
			Boolean ehBotaoConfirmarProcedimento) {
		this.ehBotaoConfirmarProcedimento = ehBotaoConfirmarProcedimento;
	}

	public Boolean getSemMensagem() {
		return semMensagem;
	}

	public void setSemMensagem(Boolean semMensagem) {
		this.semMensagem = semMensagem;
	}

	public Boolean getOkParaSumarioDefinitivo() {
		return okParaSumarioDefinitivo;
	}

	public void setOkParaSumarioDefinitivo(Boolean okParaSumarioDefinitivo) {
		this.okParaSumarioDefinitivo = okParaSumarioDefinitivo;
	}

	public ServidorIdVO getServidorIdVO() {
		return servidorIdVO;
	}

	public void setServidorIdVO(ServidorIdVO servidorIdVO) {
		this.servidorIdVO = servidorIdVO;
	}

	public Boolean getPodeExecutarRn02() {
		return podeExecutarRn02;
	}

	public void setPodeExecutarRn02(Boolean podeExecutarRn02) {
		this.podeExecutarRn02 = podeExecutarRn02;
	}
}