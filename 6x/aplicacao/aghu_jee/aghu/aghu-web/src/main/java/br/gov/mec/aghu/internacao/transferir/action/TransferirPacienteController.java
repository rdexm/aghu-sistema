package br.gov.mec.aghu.internacao.transferir.action;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.transferir.business.ITransferirPacienteFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioRegistrosControlesPacienteController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class TransferirPacienteController extends ActionController {

	private static final String ESPECIALIDADE = "especialidade";

	private static final long serialVersionUID = -9222085235587843466L;

	private static final Log LOG = LogFactory.getLog(TransferirPacienteController.class);

	private static final String MENSAGEM_CONFIRMAR_ALTERACAO_SOLICITACAO_TRANSFERENCIA = "Existe uma solicitação de transferência com leito(s) concedido(s): {0}. Ao transferir o paciente para o leito {1} deseja concluir esta solicitação?";
	private static final String TRANSFERIR_PACIENTE_LIST = "internacao-transferirPacienteList";
	private static final String TRANSFERIR_PACIENTE_CRUD = "internacao-transferirPacienteCRUD";
	private static final String RELATORIO_CONTROLES_PACIENTE_PDF = "paciente-relatorioControlesPaciente";
	private static final String PESQUISAR_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";
	private static final String PESQUISAR_PACIENTE_INTERNADO = "internacao-pesquisarPacienteInternado";
	private static final String PESQUISAR_DISPONIBILIDADE_LEITO = "internacao-pesquisarDisponibilidadeLeito";
	private static final String ATENDER_TRANSFERENCIA_PACIENTE_LIST = "atenderTransferenciaPacienteList";
	private static final String PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST = "paciente-atenderTransferenciaPacienteList";

	// @Inject
	// @MessagesBundle
	// private ResourceBundle bundle;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ITransferirPacienteFacade transferirPacienteFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IControlePacienteFacade controlePacienteFacade;

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@Inject
	private RelatorioRegistrosControlesPacienteController relatorioRegistrosControlesPacienteController;

	private AinInternacao internacao;

	private Integer internacaoSeq;

	private AghEspecialidades especialidade;

	private ProfessorCrmInternacaoVO professor;

	private AghUnidadesFuncionais unidade;

	private AinLeitos leito;

	private AinQuartos quarto;

	private FatConvenioSaudePlano convenioSaudePlano;

	private Date dtTransferencia;

	private String descricaoQuartoTransferencia;

	private boolean validar = true;

	private boolean exibirConfirmacao = false;

	private String cameFrom = "";

	private Integer prontuario;

	private String nome;

	private Date dtInternacao;

	private String descConvenio;

	private String leitoInt;

	private String descricaoQuarto;

	private String unidadeInt;

	private String siglaEspInt;

	private String descEspInt;

	private AinInternacao internacaoOld;

	private AghUnidadesFuncionais unidadeOrigem = null;
	private AghUnidadesFuncionais unidadeDestino = null;
	private Date dataInicialImpressao;
	private Date dataFinalImpressao;

	private String leitoConcedido;

	private boolean atualizarTransferencia = false;
	private boolean validarTransferencia = false;
	private String leitoIDSolicitacaoTransferencia = null;

	private boolean mostrarAlerta = false;
	private String mensagemModal;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public void inicio() {
	 

		exibirConfirmacao = false;
		atualizarTransferencia = false;
		validarTransferencia = false;
		validar = true;
		if (mostrarAlerta) {
			return;
		}
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		if (internacaoSeq != null) {
			descricaoQuartoTransferencia = null;
			internacao = transferirPacienteFacade.obterInternacao(internacaoSeq);
			prontuario = internacao.getPaciente().getProntuario();
			nome = internacao.getPaciente().getNome();
			dtInternacao = internacao.getDthrInternacao();
			descConvenio = internacao.getConvenioSaude().getDescricao() + " - " + internacao.getConvenioSaudePlano().getDescricao();
			leitoInt = (internacao.getLeito() != null) ? internacao.getLeito().getLeitoID() : null;
			descricaoQuarto = (internacao.getQuarto() != null) ? internacao.getQuarto().getDescricao() : null;
			unidadeInt = (internacao.getUnidadesFuncionais() != null) ? internacao.getUnidadesFuncionais().getLPADAndarAlaDescricao() : null;
			siglaEspInt = (internacao.getEspecialidade() != null) ? internacao.getEspecialidade().getSigla() : null;
			descEspInt = (internacao.getEspecialidade() != null) ? internacao.getEspecialidade().getNomeReduzido() : null;
			
			//TODO Pendência
			session.setAttribute("paciente", internacao.getPaciente());
			session.setAttribute(ESPECIALIDADE, internacao.getEspecialidade());
			session.setAttribute("convenio", internacao.getConvenioSaude());
			session.setAttribute("internacao", internacaoSeq);
			
			especialidade = internacao.getEspecialidade();
			unidade = internacao.getUnidadesFuncionais();
			quarto = internacao.getQuarto();
			convenioSaudePlano = internacao.getConvenioSaudePlano();
			if (quarto != null) {
				descricaoQuartoTransferencia = quarto.getDescricao();
			}
			// #11479
			// if(internacao.getDthrUltimoEvento() == null)
			dtTransferencia = new Date();
			// else
			// dtTransferencia = internacao.getDthrUltimoEvento();

			// Se unidade origem for uma unidade com caracteristica de emergencia, traz em branco unidade, especialidade e professor. #17409
			ConstanteAghCaractUnidFuncionais constanteUnidadeEmergencia = ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA;
			if (unidade != null && this.internacaoFacade.verificarCaracteristicaUnidadeFuncional(unidade.getSeq(), constanteUnidadeEmergencia)) {
				unidade = null;
				especialidade = null;
				professor = null;
			}

			if (especialidade != null) {
				final AghProfEspecialidadesId idProfEsp = new AghProfEspecialidadesId();
				idProfEsp.setEspSeq(especialidade.getSeq());
				idProfEsp.setSerMatricula(internacao.getServidorProfessor().getId().getMatricula());
				idProfEsp.setSerVinCodigo(internacao.getServidorProfessor().getId().getVinCodigo());
				if (internacao.getServidorProfessor() != null && internacao.getEspecialidade() != null) {
					professor = transferirPacienteFacade.obterProfessorCrmInternacaoVO(internacao.getServidorProfessor(), internacao.getEspecialidade(),
							internacao.getConvenioSaudePlano().getId().getCnvCodigo());
				}
			}
			
			// verifica a unidade de internação de origem - estória #6950
			if (unidade != null) {
				unidadeOrigem = unidade;
			} else if (internacao.getLeito() != null) {
				unidadeOrigem = internacao.getLeito().getUnidadeFuncional();
			} else if (quarto != null) {
				unidadeOrigem = quarto.getUnidadeFuncional();
			}

			// #17414 - Se vier da tela "Atender Solicitação de Tranferência", exibe o ID do leito concedido na field "Transferir".
			setLeito(null);
			setUnidade(null);
			if (ATENDER_TRANSFERENCIA_PACIENTE_LIST.equalsIgnoreCase(cameFrom)) {
				if (leitoConcedido != null) {
					AinLeitos concedido = this.transferirPacienteFacade.pesquisarApenasLeitoConcedido(leitoConcedido, internacaoSeq);
					
					if(concedido == null){
						apresentarMsgNegocio(Severity.WARN, "LEITO_CONCEDIDO_OCUPADO", leitoConcedido);
					}else{
						this.setLeito(concedido);
					}
				}
			} else {
				this.setLeito(null);
				this.setDescricaoQuartoTransferencia(null);
				this.setUnidade(null);
			}
			
			
		} else {
			session.removeAttribute("paciente");
			session.removeAttribute("convenio");
			session.removeAttribute(ESPECIALIDADE);
			session.removeAttribute("internacao");
			convenioSaudePlano = null;
			prontuario = null;
			nome = null;
			dtInternacao = null;
			descConvenio = null;
			leitoInt = null;
			descricaoQuarto = null;
			unidadeInt = null;
			siglaEspInt = null;
			descEspInt = null;
		}
	
	}

	public void limparProfessor() {
		professor = null;
	}

	public String gravarConfirmacao() {
		validar = false;
		return gravar();
	}

	public String gravarValidarTransferencia() {
		validarTransferencia = true;
		return gravar();
	}

	public String gravarAtualizarTransferencia() {
		validarTransferencia = false;
		atualizarTransferencia = true;
		return gravar();
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public String gravar() {		
		try {
			if (descricaoQuartoTransferencia != null) {
				transferirPacienteFacade.validarQuartoInexistente(descricaoQuartoTransferencia);
			}
			// Necessário para poder se comparar valores na ON/RN
			AinInternacao oldInternacao = null;

			try {
				if(oldInternacao == null){
					oldInternacao = transferirPacienteFacade.obterInternacao(internacao.getSeq());
				}
				
				exibirConfirmacao = false;
				internacao.setEspecialidade(especialidade);
				internacao.setUnidadesFuncionais(unidade);
				internacao.setLeito(leito);
	
				AinQuartos quartoNovo = null;
				if (descricaoQuartoTransferencia != null) {
					quartoNovo = transferirPacienteFacade.obterQuarto(descricaoQuartoTransferencia);
				}
				internacao.setQuarto(quartoNovo);
	
				if (professor != null) {
					final RapServidoresId idServidor = new RapServidoresId();
					idServidor.setMatricula(professor.getSerMatricula());
					idServidor.setVinCodigo(professor.getSerVinCodigo());
					internacao.setServidorProfessor(registroColaboradorFacade.obterRapServidoresPorChavePrimaria(idServidor));
				}

				internacaoOld = new AinInternacao();
				PropertyUtils.copyProperties(internacaoOld, internacao);
			} catch (final IllegalArgumentException e1) {
				LOG.error(e1.getMessage(), e1);
			} catch (final IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (final InvocationTargetException e) {
				LOG.error(e.getMessage(), e);
			} catch (final NoSuchMethodException e) {
				LOG.error(e.getMessage(), e);
			}

			transferirPacienteFacade.validarPacienteJaPossuiAlta(internacao.getAtendimento(),dtTransferencia);

			transferirPacienteFacade.validarDestino(internacao);

			transferirPacienteFacade.validaEspecialidade(internacao);

			// Não deve mais ser chamado, pois em caso de sucesso, já tera feito o FLUSH
			// internacao = transferirPacienteFacade.atualizarInternacaoSemFlush(internacao);

			if (transferirPacienteFacade.validarDataTransferencia(dtTransferencia)) {
				internacao.setDthrUltimoEvento(dtTransferencia);

				transferirPacienteFacade.validarDataTransferenciaPosteriorAlta(internacao);

				transferirPacienteFacade.validarDataInternacao(internacao);

				if (validar) {
					exibirConfirmacao = transferirPacienteFacade.consisteClinicaEEspecialidade(internacao);
				}

				if (!exibirConfirmacao || !validar) {
					exibirConfirmacao = false;

					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
					} catch (UnknownHostException e) {
						LOG.error("Exceção caputada:", e);
					}

					transferirPacienteFacade.atualizarInternacao(internacao, oldInternacao, nomeMicrocomputador);
					oldInternacao = null;
					
					if (exibirModalConfirmacaoTransferencia()) {
						return null;
					}

					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_TRANSFERENCIA", this.internacao.getPaciente().getNome());
					// estória #6950 – IMPRIMIR CONTROLES DO PACIENTE NA
					// TRANSFERÊNCIA
					final boolean deveImprimir = this.deveImprimirControlesPaciente();
					LOG.debug("TransferirPacienteController.gravar(): deve imprimir controles paciente = [" + deveImprimir + "].");
					
					if (deveImprimir) {
						relatorioRegistrosControlesPacienteController.setInternacaoSeq(internacao.getSeq());
						relatorioRegistrosControlesPacienteController.setDataInicialImpressaoInMillis(getDataInicialImpressaoInMillis());
						relatorioRegistrosControlesPacienteController.setDataFinalImpressaoInMillis(getDataFinalImpressaoInMillis());
						relatorioRegistrosControlesPacienteController.setFromTransferenciaPaciente(true);
						limparFields();
						return RELATORIO_CONTROLES_PACIENTE_PDF;
					}
					
					limparFields();
				} else {
					return null; 
				}
			}

		} catch (BaseRuntimeException e) {
			copiarPropriedades(internacao, internacaoOld);
			apresentarExcecaoNegocio(e);
			return TRANSFERIR_PACIENTE_CRUD;
		} catch (final ApplicationBusinessException e) {
			copiarPropriedades(internacao, internacaoOld);
			apresentarExcecaoNegocio(e);
			return TRANSFERIR_PACIENTE_CRUD;
		} catch (final BaseException e) {
			copiarPropriedades(internacao, internacaoOld);
			apresentarExcecaoNegocio(e);
			return null; // TRANSFERIR_PACIENTE_CRUD;
		} catch (Exception e) {
			copiarPropriedades(internacao, internacaoOld);
			LOG.error(e.getMessage());
			return TRANSFERIR_PACIENTE_CRUD;
		}

		if (PESQUISAR_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_CENSO_DIARIO_PACIENTES;
			
		} else if (PESQUISAR_DISPONIBILIDADE_LEITO.equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_PACIENTE_INTERNADO;
			
		} else if (ATENDER_TRANSFERENCIA_PACIENTE_LIST.equalsIgnoreCase(cameFrom)){
			this.setLeitoConcedido(null);
			return PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST;
			
		}else {
			return TRANSFERIR_PACIENTE_LIST;
		}
	}

	private void copiarPropriedades(AinInternacao internacao, AinInternacao internacaoOld) {
		try {
			PropertyUtils.copyProperties(internacao, internacaoOld);
		} catch (final IllegalArgumentException e1) {
			LOG.error(e1.getMessage(), e1);
		} catch (final IllegalAccessException e1) {
			LOG.error(e1.getMessage(), e1);
		} catch (final InvocationTargetException e1) {
			LOG.error(e1.getMessage(), e1);
		} catch (final NoSuchMethodException e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}

	private Date buscarDataInicioRelatorio() {
		Date result = null;
		try {
			result = transferirPacienteFacade.buscarDataInicioRelatorio(internacaoSeq, unidadeOrigem.getSeq());
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return result;
	}

	private boolean deveImprimirControlesPaciente() {
		boolean vaiImprimir = false;
		LOG.debug("TransferirPacienteController.deveImprimirControlesPaciente(): Entrando");
		if (unidade != null) {
			unidadeDestino = unidade;
		} else if (leito != null) {
			LOG.debug("TransferirPacienteController.deveImprimirControlesPaciente(): leito = [" + leito.getLeitoID() + "].");
			unidadeDestino = leito.getUnidadeFuncional();
		} else if (internacao.getQuarto() != null) {
			LOG.debug("TransferirPacienteController.deveImprimirControlesPaciente(): quarto = [" + internacao.getQuarto().getNumero() + "].");
			unidadeDestino = internacao.getQuarto().getUnidadeFuncional();
		}
		if (unidadeOrigem != null && unidadeDestino != null) {
			vaiImprimir = transferirPacienteFacade.deveImprimirControlesPaciente(unidadeOrigem.getSeq(), unidadeDestino.getSeq());
		}
		// satisfez os critérios da impressão - verifica se há itens a serem
		// exibidos no relatório
		if (vaiImprimir) {
			// busca a data da ultima internacao [RN02]
			setDataInicialImpressao(buscarDataInicioRelatorio());
			LOG.debug("TransferirPacienteController.deveImprimirControlesPaciente(): DataInicialImpressao = [" + getDataInicialImpressao() + "]");
			setDataFinalImpressao(Calendar.getInstance().getTime());
			LOG.debug("TransferirPacienteController.deveImprimirControlesPaciente(): DataFinalImpressao = [" + getDataFinalImpressao() + "]");
			// pesquisa a lista de itens - se estiver vazia, retorna falso
			final List<EcpItemControle> itensControle = cadastrosBasicosControlePacienteFacade.buscarItensControlePorPacientePeriodo(getInternacao()
					.getPaciente(), getDataInicialImpressao(), getDataFinalImpressao(), null, null);
			if (itensControle != null && !itensControle.isEmpty()) {
				try {
					final List<RegistroControlePacienteVO> registros = controlePacienteFacade.pesquisarRegistrosPaciente(getInternacao().getAtendimento()
							.getSeq(), getInternacao().getPaciente(), (leito == null ? null : leito.getLeitoID()), getDataInicialImpressao(),
							getDataFinalImpressao(), itensControle, null);
					vaiImprimir = registros != null && !registros.isEmpty();
				} catch (final ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			} else {
				vaiImprimir = false;
			}
		}
		return vaiImprimir;
	}
	
	public void limparFields(){
		leito = null;
		quarto = null;
		unidade = null;
		unidadeDestino = null;
		unidadeOrigem = null;
		especialidade = null;
		professor = null;
		prontuario = null;
		descricaoQuartoTransferencia = null;
		dtTransferencia = null;
		exibirConfirmacao = false;
		mostrarAlerta = false;
		
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de Transferência de
	 * paciente
	 */
	public String cancelar() {
		limparFields();
		LOG.info("Cancelado");
		if (PESQUISAR_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_CENSO_DIARIO_PACIENTES;
		} else if (PESQUISAR_DISPONIBILIDADE_LEITO.equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_DISPONIBILIDADE_LEITO;
		} else if (ATENDER_TRANSFERENCIA_PACIENTE_LIST.equalsIgnoreCase(cameFrom)) {
			return PAGE_ATENDER_TRANSFERENCIA_PACIENTE_LIST;
		}

		return TRANSFERIR_PACIENTE_LIST;
	}

	/**
	 * Método que verifica se deve sugerir um professor de acordo com a
	 * especialidade e o convênio selecionados
	 */
	public void sugerirProfessorCRM() {
		AghProfEspecialidades profEspecialidade = null;
		if (especialidade != null && convenioSaudePlano.getId() != null && convenioSaudePlano.getConvenioSaude().getSelecaoAutomaticaProf()
				&& convenioSaudePlano.getConvenioSaude().getVerificaEscalaProfInt() && DominioSimNao.S.equals(especialidade.getIndSugereProfInternacao())) {
			profEspecialidade = internacaoFacade.sugereProfessorEspecialidade(especialidade.getSeq(), convenioSaudePlano.getId().getCnvCodigo());

			if (profEspecialidade != null) {
				this.professor = internacaoFacade.obterProfessorCrmInternacaoVO(profEspecialidade.getRapServidor(), profEspecialidade.getAghEspecialidade(),
						convenioSaudePlano.getId().getCnvCodigo());
			}
		}
	}

	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(final String strPesq) {
		return transferirPacienteFacade.pesquisarProfessor(strPesq, especialidade, internacao);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(final String strPesquisa) {
		return transferirPacienteFacade.pesquisarEspecialidades(strPesquisa, internacao.getPaciente().getIdade());
	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(final String strPesquisa) {
		return transferirPacienteFacade.pesquisarUnidades(strPesquisa);
	}

	public List<AinLeitos> pesquisarLeitos(final String strPesquisa) {
		return transferirPacienteFacade.pesquisarLeitos(strPesquisa, internacaoSeq);
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setInternacaoSeq(final Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(final AinInternacao internacao) {
		this.internacao = internacao;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(final AghEspecialidades especialidade) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		if (especialidade == null) {
			session.removeAttribute(ESPECIALIDADE);
		} else {
			session.setAttribute(ESPECIALIDADE, especialidade);
		}

		if (especialidade == null) {
			limparProfessor();
		}

		this.especialidade = especialidade;
	}

	public ProfessorCrmInternacaoVO getProfessor() {
		return professor;
	}

	public void setProfessor(final ProfessorCrmInternacaoVO professor) {
		this.professor = professor;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(final AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public boolean isExibirConfirmacao() {
		return exibirConfirmacao;
	}

	public void setExibirConfirmacao(final boolean exibirConfirmacao) {
		this.exibirConfirmacao = exibirConfirmacao;
	}

	public boolean isValidar() {
		return validar;
	}

	public void setValidar(final boolean validar) {
		this.validar = validar;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(final AinLeitos leito) {
		this.leito = leito;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(final AinQuartos quarto) {
		this.quarto = quarto;
	}

	public Date getDtTransferencia() {
		return dtTransferencia;
	}

	public void setDtTransferencia(final Date dtTransferencia) {
		this.dtTransferencia = dtTransferencia;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(final Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public Date getDtInternacao() {
		return dtInternacao;
	}

	public void setDtInternacao(final Date dtInternacao) {
		this.dtInternacao = dtInternacao;
	}

	public String getDescConvenio() {
		return descConvenio;
	}

	public void setDescConvenio(final String descConvenio) {
		this.descConvenio = descConvenio;
	}

	public String getLeitoInt() {
		return leitoInt;
	}

	public void setLeitoInt(final String leitoInt) {
		this.leitoInt = leitoInt;
	}

	public String getUnidadeInt() {
		return unidadeInt;
	}

	public void setUnidadeInt(final String unidadeInt) {
		this.unidadeInt = unidadeInt;
	}

	public String getSiglaEspInt() {
		return siglaEspInt;
	}

	public void setSiglaEspInt(final String siglaEspInt) {
		this.siglaEspInt = siglaEspInt;
	}

	public String getDescEspInt() {
		return descEspInt;
	}

	public void setDescEspInt(final String descEspInt) {
		this.descEspInt = descEspInt;
	}

	public AinInternacao getInternacaoOld() {
		return internacaoOld;
	}

	public void setInternacaoOld(final AinInternacao internacaoOld) {
		this.internacaoOld = internacaoOld;
	}

	public void setDataInicialImpressao(final Date dataInicialImpressao) {
		this.dataInicialImpressao = dataInicialImpressao;
	}

	public Date getDataInicialImpressao() {
		return dataInicialImpressao;
	}

	public long getDataInicialImpressaoInMillis() {
		if (dataInicialImpressao != null) {
			return dataInicialImpressao.getTime();
		}
		return -1;
	}

	public void setDataFinalImpressao(final Date dataFinalImpressao) {
		this.dataFinalImpressao = dataFinalImpressao;
	}

	public Date getDataFinalImpressao() {
		return dataFinalImpressao;
	}

	public long getDataFinalImpressaoInMillis() {
		if (dataFinalImpressao != null) {
			return dataFinalImpressao.getTime();
		}
		return -1;
	}

	public String getDescricaoQuarto() {
		return descricaoQuarto;
	}

	public void setDescricaoQuarto(final String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}

	public String getDescricaoQuartoTransferencia() {
		return descricaoQuartoTransferencia;
	}

	public void setDescricaoQuartoTransferencia(final String descricaoQuartoTransferencia) {
		this.descricaoQuartoTransferencia = descricaoQuartoTransferencia;
	}

	private boolean exibirModalConfirmacaoTransferencia() throws ApplicationBusinessException {
		if (!ATENDER_TRANSFERENCIA_PACIENTE_LIST.equalsIgnoreCase(cameFrom)) {
			// atualizarTransferencia: realiza a tranferência e NÃO atualiza a
			// solicitação de tranferência
			// validarTransferencia: realiza a tranferência e atualiza a
			// solicitação
			if (!atualizarTransferencia) {
				if (internacao.getPaciente().getProntuario() != null && leito != null && leito.getLeitoID() != null) {
					leitoIDSolicitacaoTransferencia = transferirPacienteFacade.atualizarSolicitacaoTransferencia(internacao.getPaciente().getProntuario(),
							leito.getLeitoID(), validarTransferencia);
					mostrarAlerta = leitoIDSolicitacaoTransferencia != null;
				}
				if (mostrarAlerta) {
					String s = MENSAGEM_CONFIRMAR_ALTERACAO_SOLICITACAO_TRANSFERENCIA; // getResourceBundleValue(MENSAGEM_CONFIRMAR_ALTERACAO_SOLICITACAO_TRANSFERENCIA);
					s = s.replace("{0}", leitoIDSolicitacaoTransferencia);
					s = s.replace("{1}", leito.getLeitoID());
					mensagemModal = s;
					return true;
				}
			}
		}
		return false;
	}

	public void confirmarAlteracaoEsp() {
		validar = false;
		gravar();
	}

	public void confirmarAlteracaoTransferenciaSim() {
		validarTransferencia = true;
		gravar();
	}

	public void confirmarAlteracaoTransferenciaNao() {
		validarTransferencia = false;
		atualizarTransferencia = true;
		gravar();
	}

	public boolean isAtualizarTransferencia() {
		return atualizarTransferencia;
	}

	public void setAtualizarTransferencia(boolean atualizarTransferencia) {
		this.atualizarTransferencia = atualizarTransferencia;
	}

	public boolean isValidarTransferencia() {
		return validarTransferencia;
	}

	public void setValidarTransferencia(boolean validarTransferencia) {
		this.validarTransferencia = validarTransferencia;
	}

	public String getLeitoIDSolicitacaoTransferencia() {
		return leitoIDSolicitacaoTransferencia;
	}

	public void setLeitoIDSolicitacaoTransferencia(String leitoIDSolicitacaoTransferencia) {
		this.leitoIDSolicitacaoTransferencia = leitoIDSolicitacaoTransferencia;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public boolean isMostrarAlerta() {
		return mostrarAlerta;
	}

	public void setMostrarAlerta(boolean mostrarAlerta) {
		this.mostrarAlerta = mostrarAlerta;
	}

	public String getLeitoConcedido() {
		return leitoConcedido;
	}

	public void setLeitoConcedido(String leitoConcedido) {
		this.leitoConcedido = leitoConcedido;
	}

	// protected String getResourceBundleValue(String key) {
	// return (String) bundle.getString(key);
	// }
	//
	// protected String getResourceBundleValue(String key, Object... params) {
	// String msg = (String) bundle.getString(key);
	// return MessageFormat.format(msg, params);
	// }

}
