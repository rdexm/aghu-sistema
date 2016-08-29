package br.gov.mec.aghu.internacao.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.action.DiariaAutorizadaAtualizarController;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.action.DisponibilidadeLeitoPaginatorController;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.responsaveispaciente.action.ResponsaveisPacienteController;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinCidsInternacaoId;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.ParametrosTelaVO;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class CadastroInternacaoController extends ActionController {

	private static final String MODAL_CONFIRMACAO_WG = "modalConfirmacaoWG";

	private static final String _HIFEN_ = " - ";

	private static final String SIM = "Sim";

	private static final String PAGE_LISTA_CIRURGIAS = "blococirurgico-listaCirurgias";

	private static final String PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE = "internacao-pesquisarDisponibilidadeUnidade";
	
	private static final long serialVersionUID = -2127525982894283396L;

	private static final Log LOG = LogFactory.getLog(CadastroInternacaoController.class);

	private static final String PAGE_PESQUISAR_PACIENTES = "paciente-pesquisaPaciente";
	private static final String ATUALIZAR_DIARIAS_INTERNACAO_CRUD = "atualizarDiariasInternacaoCRUD";
	private static final String PAGE_CONVENIO_INTERNACAO = "convenioInternacao";
	private static final String PESQUISA_CID = "pesquisaCid";
	private static final String PAGE_LISTA_RESPONSAVEIS_PACIENTE = "internacao-listaResponsaveisPaciente";
	private static final String PAGE_ATUALIZAR_ACOMPANHANTES = "atualizarAcompanhantes";
	private static final String PAGE_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";
	private static final String PAGE_PESQUISAR_SOLICITACAO_INTERNACAO = "internacao-pesquisaSolicitacaoInternacao";
	private static final String PAGE_PESQUISAR_PACIENTES_ADMITIDOS = "internacao-pesquisaPacientesAdmitidos";
	private static final String PAGE_CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private static final String PAGE_CONVENIOS_PACIENTE = "paciente-conveniosPaciente";
	private static final String PAGE_ATUALIZAR_ACOMODACAO_AUTORIZADA = "internacao-atualizarAcomodacaoAutorizada";
	private static final String PAGE_PESQUISAR_DISPONIBILIDADE_LEITO = "internacao-pesquisarDisponibilidadeLeito";
	private static final String INTERNACAO = "Internacao";
	private static final String MEDICO_ATENDIMENTO_EXTERNO_CRUD = "exames-medicoAtendimentoExternoCRUD";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private IResponsaveisPacienteFacade responsaveisPacienteFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private DiariaAutorizadaAtualizarController diariaAutorizadaAtualizarController;

	@Inject
	private PesquisaCidController pesquisaCidController;

	@Inject
	private ResponsaveisPacienteController responsaveisPacienteController;
	
	@Inject
	private AtualizarAcomodacaoController atualizarAcomodacaoController;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	
	/**
	 * Codigo do paciente, obtido via page parameter.
	 */
	private Integer aipPacCodigo;

	private Integer aghUniFuncSeq;

	private String ainLeitoId;

	private Integer ainQuartoNumero;

	private String quartoDescricao;

	private Integer ainAtendimentoUrgenciaSeq;

	private Integer ainSolicitacaoInternacaoSeq;

	private AipPacientes paciente;

	private AinInternacao internacao;

	private Boolean modoEdicao = false;
	
	private Boolean gravouCirurgia = false;

	private String cameFrom;
	
	private String cameFromBegin;

	private String labelBotaoConfirmacao;

	private String descricaoUnidadeFuncional;

	private Integer seqInternacao;
	
	/**
	 * Lista de responsáveis associados a este paciente.
	 */
	private List<AinResponsaveisPaciente> listaResponsaveisPaciente;

	/**
	 * Lista de responsáveis do paciente que excluídos.
	 */
	private List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos = new ArrayList<AinResponsaveisPaciente>();

	private String mensagemModal;

	/**
	 * Atributos adicionais para LOVs de pesquisa
	 */

	/* ESPECIALIDADES */
	private AghEspecialidades especialidadePesq;

	/* PROJETO DE PESQUISA */
	private AelProjetoPesquisas projetoPesquisaPesq;

	/* CRM PROFESSOR */
	private ProfessorCrmInternacaoVO professorPesq;

	/* CARATER INTERNACAO */
	private AinTiposCaraterInternacao caraterInternacaoPesq;

	/* ORIGEM DE INTERNACAO */
	private AghOrigemEventos origemInternacaoPesq;

	/* MEDICO EXTERNO */
	private AghMedicoExterno medicoExternoPesq = null;
	
	/* HOSPITAL DE ORIGEM */
	private AghInstituicoesHospitalares hospitalOrigemPesq;

	/* PROCEDIMENTOS */
	private FatItensProcedHospitalar itemProcedHospitalar;

	/* CONVÊNIO/PLANO */
	// private ConvenioPlanoVO convenioPlanoPesq;
	private FatConvenioSaudePlano convenioSaudePlano;
	private Short convenioId;
	private Byte planoId;
	
	private List<AinLeitos> listaLeitos = new ArrayList<AinLeitos>();
	private AinLeitos leitos = null;
	

	/* CIDS */

	private AghCid itemCid;

	

	private List<AinCidsInternacao> cidsInternacao = new ArrayList<AinCidsInternacao>();
	private List<AinCidsInternacao> cidsInternacaoExcluidos = new ArrayList<AinCidsInternacao>();
	private Integer cidInternacaoSeq;
	private Object retornouTelaAssociada;

	private Integer codigoClinicaPesq;
	private List<AghClinicas> listaClinicasPesq;
	private String descricaoClinicaBuscaLov;

	// Variáveis de controle de mensagens de confirmação da tela
	private Boolean mostrarAlerta = false;

	private Date dataCirurgia = null;

	private Boolean perguntouCirurgia = false;
	private Boolean verificouProcedimento = false;
	private Boolean verificouUF = false;
	private Boolean verificouClinica = false;
	private Boolean verificouEspecialidade = false;
	private Boolean verificouCERIH = false;
	private Boolean verificouAtualizarAcomodacao = false;
	private Boolean verificouMatriculaConvenio = false;
	private Boolean verificouCnrac = false;

	private Boolean isInclusao = true;
	private AghUnidadesFuncionais unidadeFuncional;

	private boolean considerarCidFiltro = true;

	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;

	@Inject
	private DisponibilidadeLeitoPaginatorController disponibilidadeLeitoPaginatorController;

	private ParametrosTelaVO parametrosTela;
	
	private Boolean indDifClasse = Boolean.FALSE;
	private Boolean indEnvProntUnidInt = Boolean.FALSE;
	private String justificativaAltDel;
	
	

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void inicio() {
	 

		if (INTERNACAO.equalsIgnoreCase(disponibilidadeLeitoPaginatorController.getCameFrom())) {

			if(this.aipPacCodigo == null) {
				this.aipPacCodigo = disponibilidadeLeitoPaginatorController.getPacienteInternacao().getCodigo();
			}
			
			if (disponibilidadeLeitoPaginatorController.getLeitoAtribuido() != null) {
				this.ainLeitoId = disponibilidadeLeitoPaginatorController.getLeitoAtribuido().getLeitoID();
			}
			// this.ainAtendimentoUrgenciaSeq =
			// disponibilidadeLeitoPaginatorController.get;
			if (!this.cameFrom.equalsIgnoreCase(PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE)) {
				this.cameFrom = disponibilidadeLeitoPaginatorController.getCameFrom();
			}
		}	

		if (this.pesquisaCidRetorno != null) {
			PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
			this.itemCid = pesquisaCidVO.getCid();
		}

		// Verifica se retornou da tela de inclusão de CIDs
		if (retornouTelaAssociada == null) {
			// Busca internação de paciente internado
			if (internacao == null || (internacao != null && internacao.getSeq() != null)) {

				if (PAGE_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom) || PAGE_PESQUISAR_PACIENTES_ADMITIDOS.equalsIgnoreCase(cameFrom)) {

					if (seqInternacao != null) {
						this.internacao = pesquisaInternacaoFacade.obterInternacao(seqInternacao);
					} else {
						this.internacao = pesquisaInternacaoFacade.obterInternacaoPacientePorCodPac(aipPacCodigo);
					}

				} else {
					this.internacao = pesquisaInternacaoFacade.obterInternacaoPacienteInternado(aipPacCodigo);
				}
			}

			if ((this.internacao == null || internacao.getSeq() == null) && cidInternacaoSeq == null) {
				/* Se for criação de uma nova internação */

				if (this.ainLeitoId != null) {
					leitos = cadastrosBasicosInternacaoFacade.obterLeitoPorId(this.ainLeitoId);
				}
				
				/*
				 * Obtém os parâmetros que indicam convênio e plano padrões para
				 * o HU e sugere no campo
				 */
				AghuParametrosEnum enumConvenioPadrao = AghuParametrosEnum.P_AGHU_CONVENIO_PADRAO;
				AghuParametrosEnum enumPlanoPadrao = AghuParametrosEnum.P_AGHU_PLANO_CONVENIO_PADRAO;

				if (parametroFacade.verificarExisteAghParametroValor(enumConvenioPadrao)
						&& parametroFacade.verificarExisteAghParametroValor(enumPlanoPadrao)) {

					try {
						AghParametros parametroConvenioPadrao = this.parametroFacade.buscarAghParametro(enumConvenioPadrao);
						AghParametros parametroPlanoPadrao = this.parametroFacade.buscarAghParametro(enumPlanoPadrao);

						FatConvenioSaudePlano convenioSaude = internacaoFacade.obterConvenioPlanoInternacao(parametroPlanoPadrao
								.getVlrNumerico().shortValue());

						if (convenioSaude != null) {
							this.setConvenioSaudePlano(convenioSaude);
							this.setPlanoId(parametroConvenioPadrao.getVlrNumerico().byteValue());
							this.setConvenioId(parametroPlanoPadrao.getVlrNumerico().shortValue());
						}

					} catch (ApplicationBusinessException e) {
						LOG.error(e.getMessage(), e);
						apresentarExcecaoNegocio(e);
						return;
					}
				}

				/* ---------------------------------- */

				isInclusao = true;
				paciente = this.pacienteFacade.obterPacientePorCodigo(aipPacCodigo);
				Date dthrInternacao = null;
				if(internacao != null){
					dthrInternacao = this.internacao.getDthrInternacao();
				}
				
				this.internacao = new AinInternacao();
				
				/* #35548 - Recuperar os valores do agendamento */
				this.recuperarValoresAgendamento();
				
				// this.internacao.setPacCodigo(paciente.getCodigo());
				this.internacao.setPaciente(paciente);

				Short unfSeq = null;
				// TODO Fazer um refactoring neste trecho de código
				if (leitos!=null) {
					AinLeitos leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitos.getLeitoID());
					unidadeFuncional = leito.getUnidadeFuncional();
					unfSeq = leito.getUnidadeFuncional().getSeq();
					internacao.setLeito(leito);
					internacao.setIndLocalPaciente(DominioLocalPaciente.L);
				}
				if (ainQuartoNumero != null) {
					AinQuartos quarto = cadastrosBasicosInternacaoFacade.obterQuarto(ainQuartoNumero.shortValue());
					unidadeFuncional = quarto.getUnidadeFuncional();
					quartoDescricao = quarto.getDescricao();

					unfSeq = quarto.getUnidadeFuncional().getSeq();
					internacao.setQuarto(quarto);
					internacao.setIndLocalPaciente(DominioLocalPaciente.Q);
				}
				if (aghUniFuncSeq != null) {
					unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(aghUniFuncSeq.shortValue());
					descricaoUnidadeFuncional = aghUniFuncSeq + _HIFEN_ + unidadeFuncional.getAndarAlaDescricao();
					unfSeq = unidadeFuncional.getSeq();
					internacao.setUnidadesFuncionais(unidadeFuncional);
					internacao.setIndLocalPaciente(DominioLocalPaciente.U);
				}

				this.internacao.setIndDifClasse(this.indDifClasse);
				this.internacao.setEnvProntUnidInt(this.indEnvProntUnidInt);
				this.internacao.setJustificativaAltDel(this.justificativaAltDel);
				
				if (this.internacao.getDthrInternacao() == null) {
					if(dthrInternacao == null){
						this.internacao.setDthrInternacao(new Date());
					}else{
						this.internacao.setDthrInternacao(dthrInternacao);
					}
				}
				this.modoEdicao = false;

				// Recupera valores quando for Atendimento de Urgência
				if (ainAtendimentoUrgenciaSeq != null) {
					try {
						AinAtendimentosUrgencia atendimentoUrgencia = internacaoFacade.obterAtendimentoUrgencia(ainAtendimentoUrgenciaSeq);
						internacao.setAtendimentoUrgencia(atendimentoUrgencia);

						unfSeq = carregaQuartoUnidadeEspecialidadeLeito(unfSeq,
								atendimentoUrgencia);

						if (atendimentoUrgencia.getConvenioSaude() != null) {

							this.setConvenioSaudePlano(internacaoFacade.obterConvenioPlanoInternacao(atendimentoUrgencia.getConvenioSaude()
									.getCodigo()));
							this.setPlanoId(convenioSaudePlano.getId().getSeq());
							this.setConvenioId(convenioSaudePlano.getId().getCnvCodigo());
						}

						if (origemInternacaoPesq == null) {
							origemInternacaoPesq = internacaoFacade.obterOrigemInternacaoAtendimentoEmergencia(unfSeq);
						}

						adicionarCidInternacao(atendimentoUrgencia.getCid());

					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
						return;

					}
				}
				// Recupera valores quando for Solicitacao de internacao
				if (ainSolicitacaoInternacaoSeq != null) {
					AinSolicitacoesInternacao solicitacaoInternacao = recuperarSolicitacaoInternacao();
						AinLeitos leito = solicitacaoInternacao.getLeito();
						if (leito != null) {
							internacao.setLeito(leito);
							unidadeFuncional = leito.getUnidadeFuncional();
							unfSeq = leito.getUnidadeFuncional().getSeq();
							internacao.setIndLocalPaciente(DominioLocalPaciente.L);
							leitos = leito;
						} else {
							AinQuartos quarto = solicitacaoInternacao.getQuarto();
							if (quarto != null) {
								internacao.setQuarto(quarto);
								unidadeFuncional = quarto.getUnidadeFuncional();
								unfSeq = quarto.getUnidadeFuncional().getSeq();
								internacao.setIndLocalPaciente(DominioLocalPaciente.Q);
								ainQuartoNumero = quarto.getNumero().intValue();
							} else {
								AghUnidadesFuncionais unidadeFuncionalSolInternacao = solicitacaoInternacao.getUnidadeFuncional();
								internacao.setUnidadesFuncionais(unidadeFuncionalSolInternacao);
								unidadeFuncional = unidadeFuncionalSolInternacao;
								unfSeq = unidadeFuncionalSolInternacao.getSeq();
								internacao.setIndLocalPaciente(DominioLocalPaciente.U);
								aghUniFuncSeq = unidadeFuncionalSolInternacao.getSeq().intValue();
								descricaoUnidadeFuncional = aghUniFuncSeq + " - " + unidadeFuncional.getAndarAlaDescricao();
							}
						}
						especialidadePesq = solicitacaoInternacao.getEspecialidade();
						// Os 3 parametros passados ao metodo abaixo sao not
						// null no pojo de ainSolicitacoesInternacao.
						FatConvenioSaudePlano convenio = solicitacaoInternacao.getConvenio();
						professorPesq = internacaoFacade.obterProfessorCrmInternacaoVO(
										solicitacaoInternacao.getServidor(),
										solicitacaoInternacao .getEspecialidade(),
										convenio.getId().getCnvCodigo());
					if (convenio != null && convenio.getConvenioSaude() != null) {
						this.setConvenioSaudePlano(internacaoFacade.obterConvenioPlanoInternacao(
								convenio.getConvenioSaude() .getCodigo()));
						this.setPlanoId(convenioSaudePlano.getId().getSeq());
						this.setConvenioId(convenioSaudePlano.getId().getCnvCodigo());
					}
				}

				// Seta a variável de controle para verificar cirurgia
				// this.perguntouCirurgia = false;
			} else {
				/* Se for edição de uma internação existente */
				isInclusao = false;

				// Associa o paciente da internação ao objeto utilizado para uso
				// na
				// classe controller
				this.paciente = this.internacao.getPaciente();
				if (internacao.getLeito() != null) {
					this.ainLeitoId = internacao.getLeito().getLeitoID();
					leitos = cadastrosBasicosInternacaoFacade.obterLeitoPorId(this.ainLeitoId);
					this.unidadeFuncional = internacao.getLeito().getUnidadeFuncional();
				}
				if (internacao.getQuarto() != null) {
					this.ainQuartoNumero = internacao.getQuarto().getNumero().intValue();
					this.quartoDescricao = internacao.getQuarto().getDescricao();
					this.unidadeFuncional = internacao.getQuarto().getUnidadeFuncional();
				}
				if (internacao.getUnidadesFuncionais() != null) {
					this.aghUniFuncSeq = internacao.getUnidadesFuncionais().getSeq().intValue();
					this.unidadeFuncional = internacao.getUnidadesFuncionais();
					descricaoUnidadeFuncional = aghUniFuncSeq + _HIFEN_ + unidadeFuncional.getAndarAlaDescricao();
				}
				
				this.modoEdicao = true;
				this.especialidadePesq = internacao.getEspecialidade();

				this.projetoPesquisaPesq = internacao.getProjetoPesquisa();

				if (internacao.getConvenioSaudePlano() != null) {
					this.convenioSaudePlano = internacao.getConvenioSaudePlano();
					this.setPlanoId(convenioSaudePlano.getId().getSeq());
					this.setConvenioId(convenioSaudePlano.getId().getCnvCodigo());
				}
				if (convenioSaudePlano != null) {
					this.professorPesq = internacaoFacade.obterProfessorCrmInternacaoVO(internacao.getServidorProfessor(),
							internacao.getEspecialidade(), convenioSaudePlano.getId().getCnvCodigo());
				}
				
				if(internacao.getJustificativaAltDel() !=  null &&
				!internacao.getJustificativaAltDel().equalsIgnoreCase("")){
					setJustificativaAltDel(internacao.getJustificativaAltDel());
				}

				this.caraterInternacaoPesq = internacao.getTipoCaracterInternacao();

				this.origemInternacaoPesq = internacao.getOrigemEvento();

				if (internacao.getMedicoExterno() != null) {
					medicoExternoPesq = this.examesFacade.obterMedicoExternoPorId(internacao.getMedicoExterno());
				}
				
				this.hospitalOrigemPesq = internacao.getInstituicaoHospitalar();

				this.itemProcedHospitalar = internacaoFacade.obterItemProcedimentoHospitalar(internacao.getIphPhoSeq(),
						internacao.getIphSeq());

				// Recupera a lista de Cids da internação
				if (cidInternacaoSeq == null && cidsInternacao.size() == 0) {
					// this.cidsInternacao.addAll(internacao.getCidsInternacao());
					this.cidsInternacao.addAll(aghuFacade.pesquisarCidsInternacao(internacao.getSeq()));

				}

			}
		} else {

			// Se receber por parametro o cidSeq, deve adicioná-lo a lista de
			// CIDs
			// da internação, independente de estar editando ou criando uma
			// internação.

			if (this.cidInternacaoSeq != null) {
				Boolean adicionaCid = true;

				// Verifica se cidSeq (recebido por parametro) já existe na
				// lista
				// "cidsInternacao" da internacao
				for (AinCidsInternacao cidInt : this.cidsInternacao) {
					if (this.cidInternacaoSeq.equals(cidInt.getCid().getSeq())) {
						adicionaCid = false;
						break;
					}
				}

				if (adicionaCid) {
					AghCid cid = aghuFacade.obterCid(this.cidInternacaoSeq);
					this.adicionarCidInternacao(cid);
					cidInternacaoSeq = null;
				}

			}

			// Verifica novamente o convênio, caso tenha sido alterado.
			if (internacao.getConvenioSaudePlano() != null) {
				this.convenioSaudePlano = internacao.getConvenioSaudePlano();
				this.setPlanoId(convenioSaudePlano.getId().getSeq());
				this.setConvenioId(convenioSaudePlano.getId().getCnvCodigo());
			}
			if (convenioSaudePlano != null) {
				if (professorPesq == null) {
					this.professorPesq = internacaoFacade.obterProfessorCrmInternacaoVO(internacao.getServidorProfessor(),
							internacao.getEspecialidade(), convenioSaudePlano.getId().getCnvCodigo());
				}
			}
		}
		
		if(this.internacao != null) {
			this.indEnvProntUnidInt = internacao.getEnvProntUnidInt();
		}
	
	}

	private Short carregaQuartoUnidadeEspecialidadeLeito(Short unfSeq,
			AinAtendimentosUrgencia atendimentoUrgencia)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(leitos.getLeitoID()) && ainQuartoNumero == null && aghUniFuncSeq == null) {
			if (atendimentoUrgencia.getLeito() != null) {
				AinLeitos leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(atendimentoUrgencia.getLeito().getLeitoID());
				internacao.setLeito(leito);
				unidadeFuncional = leito.getUnidadeFuncional();
				unfSeq = leito.getUnidadeFuncional().getSeq();
				internacao.setIndLocalPaciente(DominioLocalPaciente.L);
				leitos.setLeitoID(leito.getLeitoID());

			} else if (atendimentoUrgencia.getQuarto() != null) {
				AinQuartos quarto = cadastrosBasicosInternacaoFacade.obterQuartosLeitosPorId(atendimentoUrgencia.getQuarto().getNumero());
				internacao.setQuarto(quarto);
				unidadeFuncional = quarto.getUnidadeFuncional();
				unfSeq = quarto.getUnidadeFuncional().getSeq();
				internacao.setIndLocalPaciente(DominioLocalPaciente.Q);
				ainQuartoNumero = quarto.getNumero().intValue();

			} else {
				AghUnidadesFuncionais unidade = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(atendimentoUrgencia.getUnidadeFuncional().getSeq());
				internacao.setUnidadesFuncionais(atendimentoUrgencia.getUnidadeFuncional());
				unidadeFuncional = unidade;
				unfSeq = unidade.getSeq();
				internacao.setIndLocalPaciente(DominioLocalPaciente.U);
				aghUniFuncSeq = unidade.getSeq().intValue();
				descricaoUnidadeFuncional = aghUniFuncSeq + _HIFEN_ + unidadeFuncional.getAndarAlaDescricao();
			}

			verificarPopularEspecialidadeAtendimentoObstetrico(unfSeq);
			if (especialidadePesq == null) {
				Short espSeq = atendimentoUrgencia.getEspecialidade().getSeq();
				especialidadePesq = cadastrosBasicosInternacaoFacade.obterEspecialidade(espSeq);
			}
		}
		return unfSeq;
	}
	
	private AinSolicitacoesInternacao recuperarSolicitacaoInternacao() {
		return solicitacaoInternacaoFacade
				.obterAinSolicitacoesInternacao(ainSolicitacaoInternacaoSeq);
	}

	/**
	 * Este método carrega a especialidade e a origem do atendimento para
	 * atendimentos de urgência com base na procedure AINP_POPULA_ORIGEM da
	 * AINF_INTERNAR_PAC
	 * 
	 * @param unfSeq
	 * @throws AGHUNegocioException
	 */
	private void verificarPopularEspecialidadeAtendimentoObstetrico(Short unfSeq) throws ApplicationBusinessException {
		BigDecimal seqOrigemEmergencia = null;
		BigDecimal espSeq = null;
		AghuParametrosEnum parametroEnum = null;
		if (DominioSimNao.S.equals(pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional(unfSeq,
				ConstanteAghCaractUnidFuncionais.CO))) {
			parametroEnum = AghuParametrosEnum.P_COD_EVENTO_CO;
		} else if (DominioSimNao.S.equals(pesquisaInternacaoFacade.verificarCaracteristicaDaUnidadeFuncional(unfSeq,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA))) {
			parametroEnum = AghuParametrosEnum.P_ORIGEM_EMERGENCIA;
		}

		if (parametroEnum != null) {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(parametroEnum);
			seqOrigemEmergencia = parametro.getVlrNumerico();

			if ((AghuParametrosEnum.P_COD_EVENTO_CO.toString()).equals(parametro.getNome())) {

				AghParametros parametroEspPrn = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_ESP_PRN);
				espSeq = parametroEspPrn.getVlrNumerico();
				especialidadePesq = aghuFacade.obterEspecialidadePorChavePrimaria(espSeq.shortValue());
			}
			origemInternacaoPesq = aghuFacade.obterOrigemEventos(seqOrigemEmergencia.shortValue());
		}
	}

	/**
	 * Método que adiciona um novo cidInternacao na lista
	 * 
	 * @param cid
	 */
	private void adicionarCidInternacao(AghCid cidP) {
		AghCid cid = aghuFacade.obterAghCidPorChavePrimaria(cidP.getSeq());
		
		boolean cidDuplicado = false;
		for (AinCidsInternacao cidInt : cidsInternacao) {
			if (cidInt.getId().getCidSeq().equals(cid.getSeq())) {
				cidDuplicado = true;
				break;
			}
		}

		if (!cidDuplicado) {
			AinCidsInternacao cidInt = new AinCidsInternacao();
			AinCidsInternacaoId idCidInternacao = new AinCidsInternacaoId();
			idCidInternacao.setCidSeq(cid.getSeq());
			cidInt.setId(idCidInternacao);

			cidInt.setCid(cid);
			cidInt.setInternacao(this.internacao);
			if (jaPossuiCidPrincipal()) {
				cidInt.setPrioridadeCid(DominioPrioridadeCid.S);
			} else {
				cidInt.setPrioridadeCid(DominioPrioridadeCid.P);
			}
			this.cidsInternacao.add(cidInt);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CID_INTERNACAO_DUPLICADO", cid.getCodigo());
		}
	}

	public void adicionarCidNaLista() {
		if (itemCid != null) {
			adicionarCidInternacao(itemCid);
			itemCid = null;
		}

	}

	private boolean jaPossuiCidPrincipal() {
		boolean retorno = false;
		for (AinCidsInternacao cid : cidsInternacao) {
			if (cid.getPrioridadeCid().equals(DominioPrioridadeCid.P)) {
				retorno = true;
				break;
			}
		}

		return retorno;
	}

	public String cancelar() {
		
		String retorno = PAGE_PESQUISAR_PACIENTES;
		if (PAGE_LISTA_CIRURGIAS.equalsIgnoreCase(cameFromBegin) && this.gravouCirurgia) {
			retorno = PAGE_LISTA_CIRURGIAS;
		}else if ("pesquisarPaciente".equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_PESQUISAR_PACIENTES;
		} else if (PAGE_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_CENSO_DIARIO_PACIENTES;
		} else if ("pesquisarSolicitacaoInternacao".equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_PESQUISAR_SOLICITACAO_INTERNACAO;
		} else if ("internacao-pesquisaPacientesAdmitidos".equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_PESQUISAR_PACIENTES_ADMITIDOS;
		} else if (PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE.equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE; 
		}else if ("paciente-cadastroPaciente".equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_CADASTRO_PACIENTE;
		} else if (PAGE_PESQUISAR_DISPONIBILIDADE_LEITO.equalsIgnoreCase(cameFrom)) {
			retorno = PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
		} else if (this.internacao == null || this.internacao.getSeq() == null) {
			 if (ainQuartoNumero != null) {
				retorno = "canceladoIntQuarto";
			} else {
				retorno = PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
			}
		}

		// Limpa Suggestions
		especialidadePesq = null;
		projetoPesquisaPesq = null;
		professorPesq = null;
		caraterInternacaoPesq = null;
		origemInternacaoPesq = null;
		hospitalOrigemPesq = null;
		itemProcedHospitalar = null;
		convenioSaudePlano = null;

		// Limpa os parâmetros de entrada
		aipPacCodigo = null;
		aghUniFuncSeq = null;
		ainLeitoId = null;
		ainQuartoNumero = null;
		ainAtendimentoUrgenciaSeq = null;
		ainSolicitacaoInternacaoSeq = null;

		descricaoUnidadeFuncional = null;

		// Limpa campos do Convenio
		convenioId = null;
		planoId = null;
		
		// #54656 - Limpa o leito/quarto/unidade selecionada ao voltar para a lista de pacientes após internar um paciente.
		if(internacao!=null && internacao.getSeq()!=null) {
			pesquisaPacienteController.setIdLeito(null);
			pesquisaPacienteController.setSeqUnidadeFuncional(null);
			pesquisaPacienteController.setQuartoNumero(null);
		}

		// Limpa objetos Internação e Paciente
		internacao = null;
		paciente = null;

		// Limpa Cids e Responsáveis
		cidsInternacao.clear();
		cidsInternacaoExcluidos.clear();

		pesquisaCidRetorno = null;
		itemCid = null;

		clearListaResponsaveisPaciente();
		
		limparListaResponsaveisPacienteExcluidos();

		// Variáveis de Controle
		retornouTelaAssociada = null;
		cidInternacaoSeq = null;
		gravouCirurgia = false;
		
		return retorno;
	}
	
	private void limparListaResponsaveisPacienteExcluidos() {
		if (listaResponsaveisPacienteExcluidos != null){
			listaResponsaveisPacienteExcluidos.clear();
		}
	}
	
	/**
	 * Métodos que limpa a lista de Responsaveis do Pacientes.
	 */	
	private void clearListaResponsaveisPaciente() {

		if (listaResponsaveisPaciente != null){
			listaResponsaveisPaciente.clear();			
		}
		limparListaResponsaveisPacienteExcluidos();
	}

	
	/**
	 * Métodos que realiza a internação do paciente.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public String internarPaciente() {

		try {
			Boolean isEdicao = internacao.getSeq() != null;
			// Limpa variáveis de controle de mensagens de alerta
			this.mostrarAlerta = false;
			this.verificouProcedimento = false;
			this.verificouUF = false;
			this.verificouClinica = false;
			this.verificouEspecialidade = false;
			this.verificouCERIH = false;
			this.verificouAtualizarAcomodacao = false;
			this.verificouMatriculaConvenio = false;
			this.verificouCnrac = false;

			// Associa especialidade
			internacao.setEspecialidade(especialidadePesq);
			// Associa projeto de pesquisa
			internacao.setProjetoPesquisa(projetoPesquisaPesq);
			// Associa convênio/plano
			if (convenioSaudePlano != null) {

				FatConvenioSaudePlanoId idFatConvenio = new FatConvenioSaudePlanoId();
				idFatConvenio.setCnvCodigo(convenioSaudePlano.getId().getCnvCodigo());
				idFatConvenio.setSeq(convenioSaudePlano.getId().getSeq());
				FatConvenioSaudePlano fatConvenioSaudePlano = internacaoFacade.obterConvenioSaudePlano(idFatConvenio);
				internacao.setConvenioSaudePlano(fatConvenioSaudePlano);
			} else {
				internacao.setConvenioSaudePlano(null);
			}

			// Associa CRM Professor
			if (professorPesq != null) {
				RapServidoresId idRapServidorProfessor = new RapServidoresId();
				idRapServidorProfessor.setMatricula(professorPesq.getSerMatricula());
				idRapServidorProfessor.setVinCodigo(professorPesq.getSerVinCodigo());
				RapServidores servidorProfessor = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(idRapServidorProfessor);
				internacao.setServidorProfessor(servidorProfessor);
			} else {
				internacao.setServidorProfessor(null);
			}

			// Associa caráter de internação
			internacao.setTipoCaracterInternacao(caraterInternacaoPesq);
			// Associa origem de internação
			internacao.setOrigemEvento(origemInternacaoPesq);
			// Medico Externo
			verificarMedicoExternoSeq();
			// Associa hospital de origem
			internacao.setInstituicaoHospitalar(hospitalOrigemPesq);
			
			internacao.setJustificativaAltDel(justificativaAltDel);
			
			internacao.setEnvProntUnidInt(indEnvProntUnidInt);

			// Associa procedimento
			if (itemProcedHospitalar != null && itemProcedHospitalar.getId() != null) {
				internacao.setIphSeq(itemProcedHospitalar.getId().getSeq());
				internacao.setIphPhoSeq(itemProcedHospitalar.getId().getPhoSeq());
				// TODO Fazer um refactoring para setar somente o objeto e não
				// mais os atributos acima
				internacao.setItemProcedimentoHospitalar(itemProcedHospitalar);
			} else {
				internacao.setIphSeq(null);
				internacao.setIphPhoSeq(null);
			}

			// Recupera a lista de Responsáveis de Paciente da internação
			if (listaResponsaveisPaciente == null || listaResponsaveisPaciente.isEmpty() && (listaResponsaveisPacienteExcluidos == null || listaResponsaveisPacienteExcluidos.isEmpty())) {
				listaResponsaveisPaciente = new ArrayList<AinResponsaveisPaciente>();
				if (internacao.getSeq() != null) {
					List<AinResponsaveisPaciente> listaResponsaveisBanco = responsaveisPacienteFacade
							.pesquisarResponsaveisPaciente(internacao.getSeq());
					listaResponsaveisPaciente.addAll(listaResponsaveisBanco);
				}
				limparListaResponsaveisPacienteExcluidos();
			}

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().toString();
			} catch (UnknownHostException e) {
				LOG.error(e.getMessage(), e);
			}

			final Date dataFimVinculoServidor = new Date();

			internacao = this.internacaoFacade.persistirInternacao(internacao, cidsInternacao, cidsInternacaoExcluidos,
					listaResponsaveisPaciente, listaResponsaveisPacienteExcluidos, nomeMicrocomputador, dataFimVinculoServidor, false);

			cidsInternacaoExcluidos.clear();

			if (isEdicao) {
				this.isInclusao = false;
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_OPERACAO_REALIZADA");
			this.gravouCirurgia= true;
			clearListaResponsaveisPaciente();
			
			
			// Verifica se deve mostrar mensagem de incluir CNRAC para paciente
			// de outro Estado
			if (!internacaoFacade.validarPacienteOutroEstado(internacao.getPaciente(), internacao.getIphSeq(), internacao.getIphPhoSeq())) {

				this.labelBotaoConfirmacao = "OK";
				this.mostrarAlerta = true;
				this.verificouCnrac = true;
				this.mensagemModal = getBundle().getString("FAT_01168");
			}
			// Verifica se deve mostrar mensagem de informar a acomodação
			// autorizada
			else {
				this.verificouCnrac = false;
				this.mostrarAlerta = false;
				this.verificarAcomodacaoAutorizada();
			}

			retornouTelaAssociada = null;
			this.modoEdicao = true;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		//this.dataModel.reiniciarPaginator();

		return "internacao-cadastroInternacao";
	}

	private void verificarMedicoExternoSeq() {
		
		if(medicoExternoPesq != null){
			internacao.setMedicoExterno(medicoExternoPesq.getSeq());
		}
	}

	/**
	 * Método que verifica se o botão contrato deve aparecer na tela de
	 * internação.
	 * 
	 * @return
	 */
	public boolean deveAparecerBotaoContrato() {
		boolean retorno = false;
		try {
			AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_AGHU_DEVE_APARECER_BOTAO_CONTRATO;
			AghParametros parametroAparecerBotaoContrato = this.parametroFacade.buscarAghParametro(parametroEnum);
			if ("S".equals(parametroAparecerBotaoContrato.getVlrTexto())) {
				if (this.possuiResponsavelContratante()) {
					retorno = true;
				}
			}
		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);

		} catch (OptimisticLockException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
		return retorno;
	}

	/**
	 * Método que verifica se a internação possui o responsavel Contratante
	 * 
	 * @return boolean
	 */
	public boolean possuiResponsavelContratante() {
		boolean retorno = false;
		if (internacao != null && internacao.getSeq() != null) {
			AinResponsaveisPaciente responsavelContratante = responsaveisPacienteFacade.obterResponsaveisPacienteTipoConta(internacao
					.getSeq());
			if (responsavelContratante != null) {
				retorno = true;
			}
		}
		return retorno;
	}

	/**
	 * Método que obtém a descrição de confirmação da emissão do contrato
	 * 
	 * @return String
	 */
	public String obterDescricaoResponsaveisPacienteSalvos() {
		String retorno = null;
		if (internacao != null) {
			String nome = responsaveisPacienteFacade.obterNomeResponsavelPacienteTipoConta(internacao.getSeq());
			retorno = "O responsável que está salvo no banco e irá constar no contrato é: "+nome+". Deseja prosseguir?";
		}

		return retorno;
	}

	/**
	 * Métodos para LOVs de pesquisa
	 */

	public List<ConvenioPlanoVO> pesquisarConvenioPlano(Object strParam) {
		List<ConvenioPlanoVO> retorno;

		retorno = internacaoFacade.pesquisarConvenioPlanoInternacao(strParam);

		return retorno;
	}

	public List<AghEspecialidades> pesquisarEspecialidade(String objParam) {
		List<AghEspecialidades> retorno;

		String strPesquisa = (String) objParam;
		retorno = cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeInternacao(strPesquisa, paciente.getIdade().shortValue(),
				unidadeFuncional.getIndUnidEmergencia());
		return retorno;
	}

	public List<AelProjetoPesquisas> pesquisarProjetoPesquisa(String objParam) {
		List<AelProjetoPesquisas> retorno;

		String strPesquisa = (String) objParam;
		retorno = internacaoFacade.pesquisarProjetosPesquisaInternacao(strPesquisa, paciente.getCodigo());

		return retorno;

	}

	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(String strParam) {
		List<ProfessorCrmInternacaoVO> retorno;

		if (especialidadePesq == null || convenioSaudePlano == null) {
			retorno = new ArrayList<ProfessorCrmInternacaoVO>();
		} else {
			retorno = internacaoFacade.pesquisarProfessoresCrm(especialidadePesq.getSeq(), especialidadePesq.getSigla(), convenioSaudePlano
					.getId().getCnvCodigo(), convenioSaudePlano.getConvenioSaude().getVerificaEscalaProfInt(), strParam, null, null);
		}
		return retorno;
	}

	public void limparProfessor() {
		if (professorPesq != null) {
			this.professorPesq = null;
		}
	}

	public List<AinTiposCaraterInternacao> pesquisarCaraterInternacao(String objParam) {
		List<AinTiposCaraterInternacao> retorno;

		String strPesquisa = (String) objParam;
		retorno = internacaoFacade.pesquisarCaraterInternacao(strPesquisa);
		return retorno;

	}

	public List<AghOrigemEventos> pesquisarOrigemInternacao(String objParam) {
		List<AghOrigemEventos> retorno;

		String strPesquisa = (String) objParam;
		retorno = cadastrosBasicosInternacaoFacade.pesquisarOrigemEventoPorCodigoEDescricao(strPesquisa);
		return retorno;
	}

	public List<AghMedicoExterno> pesquisarMedicoExterno(String objParam) {
		List<AghMedicoExterno> retorno;
		String strPesquisa = (String) objParam;
		retorno = this.examesFacade.obterMedicoExternoList(strPesquisa); 
		return retorno;
	}
	
	public List<AghInstituicoesHospitalares> pesquisarHospitalOrigem(String objParam) {
		List<AghInstituicoesHospitalares> retorno;

		String strPesquisa = (String) objParam;
		retorno = aghuFacade.pesquisarInstituicaoHospitalarPorCodigoOuDescricaoOrdenado(strPesquisa);

		return retorno;

	}

	/**
	 * Pesquisa para lista de valores.
	 * 
	 * @throws AGHUNegocioException
	 */
	public List<FatItensProcedHospitalar> pesquisarSsm(String descricaoSsm) {
		Integer cidSeq = null;
		// Verifica se possui cid principal associado para levar em conta na
		// pesquisa
		if (considerarCidFiltro) {
			if (cidsInternacao.size() > 0) {
				for (AinCidsInternacao cid : cidsInternacao) {
					if (DominioPrioridadeCid.P.equals(cid.getPrioridadeCid())) {
						cidSeq = cid.getId().getCidSeq();
						break;
					}
				}
			}
		}

		List<FatItensProcedHospitalar> retorno = null;
		try {
			retorno = solicitacaoInternacaoFacade.pesquisarFatItensProcedHospitalar(descricaoSsm, this.getPaciente(), cidSeq);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_POR_PARAMETRO", e.getMessage());
		}
		return retorno;
	}

	/**
	 * Este método faz o mesmo que o método pesquisarSsm (acima), porém, aqui
	 * ele é chamado do converter, situação em que não é preciso levar em conta
	 * o CID. Pesquisa para lista de valores.
	 * 
	 * @throws AGHUNegocioException
	 */
	public List<FatItensProcedHospitalar> pesquisarSsmConverter(Object descricaoSsm) throws ApplicationBusinessException {
		return solicitacaoInternacaoFacade.pesquisarFatItensProcedHospitalar(descricaoSsm, this.getPaciente(), null);
	}

	/**
	 * Método para associar todos os CIDs da internação ao atributo
	 * "cidsInternacao".
	 * 
	 * @param seqInt
	 */
	public void pesquisarCidsInternacao(Integer seqInt) {
		this.cidsInternacao = aghuFacade.pesquisarCidsInternacao(seqInt);
	}

	/**
	 * Método para pesquisar CIDs na suggestion da tela. São pesquisados somente
	 * os 300 primeiros registros.
	 * 
	 * @param param
	 * @return Lista de objetos AghCids
	 */
	public List<AghCid> pesquisarCids(String param) {
		return aghuFacade.pesquisarCidsPorDescricaoOuId(param, Integer.valueOf(300));
	}

	/**
	 * Método para excluir um CID da lista de CIDs da internação.
	 */
	public void excluirCidInternacao(AinCidsInternacao cidInternacao) {
		if (cidInternacao != null) {
			this.cidsInternacaoExcluidos.add(cidInternacao);
			this.cidsInternacao.remove(cidInternacao);
		}
	}

	/**
	 * Método para alterar a prioridade do AinCidInternacao que foi alterado na
	 * tela.
	 * 
	 * @param cidInternacao
	 */
	public void alterarPrioridadeCid(AinCidsInternacao cidInternacao) {
		if (cidInternacao != null) {
			for (AinCidsInternacao cidInt : this.cidsInternacao) {
				if (cidInternacao.getCid().getSeq().equals(cidInt.getCid().getSeq())) {
					cidInt.setPrioridadeCid(cidInternacao.getPrioridadeCid());
					break;
				}
			}
		}
	}

	/**
	 * Método que verifica se deve sugerir um professor de acordo com a
	 * especialidade e o convênio selecionados
	 */
	public void sugerirProfessorCRM() {

		AghProfEspecialidades profEspecialidade = null;
		if (this.isInclusao && especialidadePesq != null && convenioSaudePlano != null) {
			if (convenioSaudePlano.getConvenioSaude().getSelecaoAutomaticaProf()
					&& convenioSaudePlano.getConvenioSaude().getVerificaEscalaProfInt()) {
				if (DominioSimNao.S.equals(especialidadePesq.getIndSugereProfInternacao())) {
					profEspecialidade = internacaoFacade.sugereProfessorEspecialidade(especialidadePesq.getSeq(), convenioSaudePlano
							.getId().getCnvCodigo());

					if (profEspecialidade != null) {
						this.professorPesq = internacaoFacade.obterProfessorCrmInternacaoVO(profEspecialidade.getRapServidor(),
								profEspecialidade.getAghEspecialidade(), convenioSaudePlano.getId().getCnvCodigo());
					}
				}
			}
		}

	}

	/**
	 * Método que redireciona para a página de Documentos
	 */
	public String redirecionarDocumentos() {
		return "documentos";
	}

	/**
	 * Método que redireciona para a página de Responsáveis de Paciente
	 */
	public String redirecionarResponsaveisPaciente() {
		if(convenioSaudePlano==null) {
			this.apresentarMsgNegocio(Severity.ERROR, "CADASTRO_RESPONSAVEL_PLANO");
			return null;
		}
		this.responsaveisPacienteController.setAinInternacaoSeq(this.internacao.getSeq());
		this.responsaveisPacienteController.setAipPacCodigo(this.aipPacCodigo);
		this.responsaveisPacienteController.setIsSus(convenioSaudePlano.getConvenioSaude().getGrupoConvenio().equals(DominioGrupoConvenio.S));
		this.responsaveisPacienteController.setRecarregar(true);
		
		return PAGE_LISTA_RESPONSAVEIS_PACIENTE;
	}

	/**
	 * Método que redireciona para a página de Diárias Autorizadas
	 */
	public String redirecionarDiariasAutorizadas() {
		diariaAutorizadaAtualizarController.setCodigoInternacao(internacao != null ? internacao.getSeq() : null);
		diariaAutorizadaAtualizarController.inicio();
		return ATUALIZAR_DIARIAS_INTERNACAO_CRUD;
	}

	public String redirecionarPesquisaCID() {
		pesquisaCidController.setFromPageCadastroInternacao(true);
		return PESQUISA_CID;
	}

	/**
	 * Método que redireciona para a página de Acompanhantes
	 */
	public String redirecionarAcompanhantes() {
		return PAGE_ATUALIZAR_ACOMPANHANTES;
	}

	/**
	 * Método que redireciona para a página de Observações
	 */
	public String redirecionarObservacoes() {
		return "observacoes";
	}

	/**
	 * Método que redireciona para a página de Acomodação Autorizada.
	 */
	public String redirecionarAcomodacaoAutorizada() {
		this.mostrarAlerta = false;
		this.verificouAtualizarAcomodacao = false;
		atualizarAcomodacaoController.setAinInternacaoSeq(internacao != null ? internacao.getSeq() : null);
		return PAGE_ATUALIZAR_ACOMODACAO_AUTORIZADA;
	}

	/**
	 * Método que redireciona para a página de Matrícula Convênio
	 */
	public String redirecionarMatriculaConvenio() {
		return PAGE_CONVENIOS_PACIENTE;
	}

	/**
	 * Método que redireciona para a página de Adiantamento
	 */
	public String redirecionarAdiantamento() {
		return "adiantamento";
	}

	/**
	 * Método que redireciona para a página de Boletim de Internação
	 */
	public String redirecionarBoletimInternacao() {
		return "boletimInternacao";
	}

	/**
	 * Método que redireciona para a página de Alterar Convênio
	 */
	public String redirecionarAlterarConvenio() {
		return PAGE_CONVENIO_INTERNACAO;
	}

	/**
	 * Método que redireciona para a página de Imprimir Pulseira
	 */
	public String redirecionarImprimirPulseira() {
		return "imprimirPulseira";
	}

	/**
	 * Método que verifica se já existe uma data de uma cirurgia realizada pelo
	 * paciente que pode ser utilizada como data de internação.
	 */
	public String verificarCirurgia() {
		String retorno = null;
		if (!perguntouCirurgia && internacao.getDthrInternacao() != null) {
			MbcCirurgias cirurgia = internacaoFacade.obterProcedimentoCirurgicoInternacao(paciente.getCodigo(),
					internacao.getDthrInternacao());
			if (cirurgia != null && cirurgia.getDataInicioCirurgia() != null
					&& cirurgia.getDataInicioCirurgia().compareTo(internacao.getDthrInternacao()) != 0) {
				boolean gerouMensagem = false;
				if (cirurgia.getSituacao() == DominioSituacaoCirurgia.PREP || cirurgia.getSituacao() == DominioSituacaoCirurgia.TRAN) {
					//#44845
					List<MbcExtratoCirurgia> listaMbcExtratoCirurgia = blocoCirurgicoFacade.pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(cirurgia.getSeq(), DominioSituacaoCirurgia.PREP);
					if (listaMbcExtratoCirurgia != null && !listaMbcExtratoCirurgia.isEmpty()) {
						//Pega o ultimo registro da lista, pois a ordenação é asc.
						MbcExtratoCirurgia mbcExtratoCirurgia = listaMbcExtratoCirurgia.get(listaMbcExtratoCirurgia.size() - 1);
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						this.dataCirurgia = mbcExtratoCirurgia.getCriadoEm();
						String strDataCirurgia = df.format(cirurgia.getDataInicioCirurgia());
						String strDataSalaPreparo = df.format(mbcExtratoCirurgia.getCriadoEm());
						this.mensagemModal = "Existe procedimento cirúrgico em " + strDataCirurgia + 
								". Paciente entrou em sala de preparo em " + strDataSalaPreparo + ". Deseja utilizar data/hora compatí­vel com o procedimento ?";
						gerouMensagem = true;
					}
					//#44845
				} 
				
				if (!gerouMensagem) {
					this.dataCirurgia = cirurgia.getDataInicioCirurgia();
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					String strDataCirurgia = null;
					strDataCirurgia = df.format(cirurgia.getDataInicioCirurgia());
					this.mensagemModal = "Existe procedimento cirurgico realizado em " + strDataCirurgia
							+ ". Deseja utilizar data/hora compatível com o procedimento ?";
				}
				this.mostrarAlerta = true;
				this.openDialog(MODAL_CONFIRMACAO_WG);
			} else {
				perguntouCirurgia = true;
				retorno = this.verificarCidProcedimento();
			}
		} else {
			retorno = this.verificarCidProcedimento();
		}

		return retorno;
	}

	/**
	 * Atribui a data de cirurgia à data de internação do paciente.
	 */
	public void atribuirDataCirurgia() {
		this.perguntouCirurgia = true;
		this.mostrarAlerta = false;
		if (dataCirurgia != null) {
			this.internacao.setDthrInternacao(dataCirurgia);
		}
	}

	/**
	 * Método que verifica se o CID principal é compatível com o procedimento
	 * escolhido, solicitando uma mensagem de confirmação em caso negativo.
	 */
	public String verificarCidProcedimento() {
		String retorno = null;
		this.labelBotaoConfirmacao = SIM;
		this.verificouProcedimento = true;
		this.mensagemModal = "O CID não é compatível com o procedimento escolhido. Deseja Prosseguir?";
		Integer cidSeq = null;
		boolean achou = false;

		for (AinCidsInternacao cid : cidsInternacao) {
			if (DominioPrioridadeCid.P.equals(cid.getPrioridadeCid())) {
				LOG.debug("verificando prioridade do cid {0} : {1}" + "," + cid.getCid().getDescricao() + "," + cid.getPrioridadeCid());
				cidSeq = cid.getId().getCidSeq();
			}
		}

		if (cidSeq != null && itemProcedHospitalar != null) {
			List<Long> listaProcedimentosAssociados = internacaoFacade.pesquisarFatAssociacaoProcedimentos(cidSeq);

			if (listaProcedimentosAssociados.size() > 0) {
				for (Long codTabela : listaProcedimentosAssociados) {
					if (itemProcedHospitalar.getCodTabela().equals(codTabela)) {
						achou = true;
					}
				}
			} else {
				achou = true;
			}
		} else {
			achou = true;
		}

		if (!achou) {
			mostrarAlerta = true;
			this.openDialog(MODAL_CONFIRMACAO_WG);
		} else {
			retorno = this.verificarPacienteOutroEstado();
			// this.internarPaciente();
		}
		return retorno;
	}

	/**
	 * Método que verifica se o paciente a ser internado reside no RS,
	 * solicitando uma mensagem de confirmação em caso negativo.
	 */
	public String verificarPacienteOutroEstado() {
		String retorno = null;
		this.labelBotaoConfirmacao = SIM;
		this.verificouUF = true;
		this.mensagemModal = "Este paciente reside fora do estado. Deseja Prosseguir?";
		try {
			AipPacientes pacienteInternacao = this.pacienteFacade.obterPacientePorCodigo(aipPacCodigo);
			AipEnderecosPacientes enderecoResidencial = cadastroPacienteFacade.obterEnderecoResidencialPaciente(pacienteInternacao);
			// Obtém a UF sede do HU
			String ufSede = internacaoFacade.obterUfSedeHU();

			if (enderecoResidencial == null || enderecoResidencial.getUfEndereco() == null
					|| !enderecoResidencial.getUfEndereco().equalsIgnoreCase(ufSede)) {
				mostrarAlerta = true;
				this.openDialog(MODAL_CONFIRMACAO_WG);
			} else {
				retorno = this.verificarConvenioSaudePaciente();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);

		}
		return retorno;
	}

	/**
	 * Método que verifica se a clínica da especialidade escolhida confere com a
	 * clínica da unidade funcional, solicitando uma mensagem de confirmação em
	 * caso negativo.
	 */
	public String verificarClinica() {
		this.labelBotaoConfirmacao = SIM;
		String retorno = null;
		this.verificouClinica = true;
		try {
			if (verificarClinaPacienteVaiInternar()) {
				apresentarMsgNegocio(Severity.ERROR,	"AIN_00314");
				return retorno;
			}
			
			if (leitos != null){
				this.mensagemModal = "Clínica do leito difere da clínica da especialidade. Deseja continuar?";
			} else if (internacao.getQuarto() != null) {
				this.mensagemModal = "Clínica do quarto difere da clínica da especialidade. Deseja continuar?";
			} else {
				this.mensagemModal = "Clínica da unidade difere da clínica da especialidade. Deseja continuar?";
			}

			AinLeitos leito = null;
			AinQuartos quarto = null;
			AghUnidadesFuncionais unidadeFuncional = null;
			if (leitos != null) {
				if(StringUtils.isNotBlank(leitos.getLeitoID())){
					leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitos.getLeitoID());
					internacao.setLeito(leito);	
				}else{
					leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitos.getLeitoID());
				}	
			} else if (ainQuartoNumero != null) {
				quarto = cadastrosBasicosInternacaoFacade.obterQuarto(ainQuartoNumero.shortValue());
			} else if (aghUniFuncSeq != null) {
				unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(aghUniFuncSeq.shortValue());
				
			}
			if (especialidadePesq != null
					&& internacaoFacade.consistirClinicaMensagemConfirmacao(leito, quarto, unidadeFuncional, especialidadePesq)) {
				mostrarAlerta = true;
				this.openDialog(MODAL_CONFIRMACAO_WG);
			} else {
				retorno = this.verificarEspecialidade();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);

		}
		return retorno;

	}
	
	private Boolean verificarClinaPacienteVaiInternar() {
		return leitos == null && ainQuartoNumero == null && aghUniFuncSeq == null;
	}

	/**
	 * Método que verifica se a clínica da especialidade escolhida confere com a
	 * clínica da unidade funcional, solicitando uma mensagem de confirmação em
	 * caso negativo.
	 */
	public String verificarEspecialidade() {
		this.labelBotaoConfirmacao = SIM;
		String retorno = null;
		this.verificouEspecialidade = true;
		this.mensagemModal = "Especialidade difere da especialidade do leito. Deseja continuar?";
		try {
			AinLeitos leito = null;
			if (leitos != null) {
				leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitos.getLeitoID());
			}
			if (especialidadePesq != null && leito != null
					&& internacaoFacade.consistirEspecialidadeMensagemConfirmacao(leito, especialidadePesq)) {
				mostrarAlerta = true;
				this.openDialog(MODAL_CONFIRMACAO_WG);
			} else {
				retorno = this.verificarInformarNumeroCERIH();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;

	}

	/**
	 * Método que verifica e informa o usuário se o número do CERIH precisará
	 * ser informado
	 */
	public String verificarInformarNumeroCERIH() {
		this.labelBotaoConfirmacao = "OK";
		this.verificouCERIH = true;
		this.mensagemModal = "Esta internação exige informação do número do CERIH na tela de diárias autorizadas";
		String retorno = null;

		try {
			if (this.isInclusao
					&& itemProcedHospitalar != null
					&& convenioSaudePlano != null
					&& internacaoFacade.verificarInformarNumeroCERIH(itemProcedHospitalar.getId().getSeq(), itemProcedHospitalar.getId()
							.getPhoSeq(), convenioSaudePlano.getId().getCnvCodigo(), null)) {
				mostrarAlerta = true;
				this.openDialog(MODAL_CONFIRMACAO_WG);

			} else {
				retorno = this.internarPaciente();
			}

		} catch (BaseException e) {
			// Mudança para capturar BaseException gmneto 12/02/2010
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	/**
	 * Método que verifica se deve ser informada a acomodação.
	 */
	public void verificarAcomodacaoAutorizada() {

		// Verifica se deve informar acomodação
		this.verificouAtualizarAcomodacao = true;
		if (internacaoFacade.verificarAcomodacaoInternacaoConvenio(internacao)) {
			this.labelBotaoConfirmacao = "OK";
			this.mostrarAlerta = true;
			this.mensagemModal = "AIN-00868: Para internações de convênio deve ser informada a acomodação autorizada.";
		} else {
			this.executarAcaoBotaoConfirmacao();
		}

	}

	public String verificarConvenioSaudePaciente() {
		// Verifica se deve mostrar a mensagem para incluir matrícula do
		// convênio da internação
		String retorno = null;
		this.verificouMatriculaConvenio = true;
		if (convenioSaudePlano != null
				&& !internacaoFacade.verificarConvenioSaudePaciente(paciente, convenioSaudePlano.getId().getCnvCodigo())) {
			this.labelBotaoConfirmacao = "Continuar";
			this.mostrarAlerta = true;
			this.mensagemModal = "Número da matrícula deve ser informado para este convênio. Clique no botão MATRÍCULA CONVÊNIO.";
			this.openDialog(MODAL_CONFIRMACAO_WG);		
		} else {
			retorno = this.verificarClinica();
		}
		return retorno;
		// Removido bloco try-catch desnecessário gmneto 12/02/2010

	}

	/**
	 * 
	 * Este método carrega as informações do agendamento
	 */

	private void recuperarValoresAgendamento() {
		List<MbcCirurgias> listaCirurgias = this.blocoCirurgicoFacade.listarCirurgiasPorCodigoPaciente(aipPacCodigo);
		MbcAgendas agenda = null;
		RapServidores profResp = null;
		DominioNaturezaFichaAnestesia natureza = null;

		if (listaCirurgias != null && listaCirurgias.size() > 0) {
			for (MbcCirurgias cirurgia : listaCirurgias) {
				if (cirurgia.getSituacao().equals(DominioSituacaoCirurgia.TRAN)
						&& !cirurgia.getOrigemPacienteCirurgia().equals(DominioOrigemPacienteCirurgia.A)) {
					agenda = cirurgia.getAgenda();
					profResp = this.blocoCirurgicoFacade.buscaRapServidorDeMbcProfCirurgias(cirurgia.getSeq(), DominioSimNao.S);
					natureza = cirurgia.getNaturezaAgenda();

					// Data da Internação
					// Trazer a data qe entrou em preparo(PREP), caso não tiver
					// esta stuação, trazer a data que entrou em TRAN
					List<MbcExtratoCirurgia> listaExtratoCirurgias = this.blocoCirurgicoFacade.listarMbcExtratoCirurgiaPorCirurgia(cirurgia
							.getSeq());
					if (listaExtratoCirurgias != null && listaExtratoCirurgias.size() > 0) {
						for (MbcExtratoCirurgia extrato : listaExtratoCirurgias) {
							if (extrato.getSituacaoCirg().equals(DominioSituacaoCirurgia.PREP)) {
								this.internacao.setDthrInternacao(extrato.getCriadoEm());
								break;
							} else if (extrato.getSituacaoCirg().equals(DominioSituacaoCirurgia.TRAN)) {
								this.internacao.setDthrInternacao(extrato.getCriadoEm());
								break;
							}
						}
					}
				}
			}
		}

		if (agenda != null) {
			// Especialidade
			especialidadePesq = agenda.getEspecialidade();

			// Convenio Plano
			convenioId = agenda.getConvenioSaudePlano().getId().getCnvCodigo();
			planoId = agenda.getConvenioSaudePlano().getId().getSeq();
			this.convenioSaudePlano = agenda.getConvenioSaudePlano();

			// CRM Profissional
			// Verficiar se o professor da agenda está na lista de pesquisa da
			// tela
			if (profResp != null && profResp.getId() != null) {
				List<ProfessorCrmInternacaoVO> listProfessores = this.pesquisarProfessor(null);
				for (ProfessorCrmInternacaoVO prof : listProfessores) {
					if (prof.getSerMatricula().equals(profResp.getId().getMatricula())
							&& prof.getSerVinCodigo().equals(profResp.getId().getVinCodigo())) {
						this.professorPesq = prof;
						break;
					}
				}
			}

			// Carater Internação
			// Verificar se a natureza da agenda existe na lista do cadastro
			if (natureza != null) {
				List<AinTiposCaraterInternacao> listaTiposCarater = this.pesquisarCaraterInternacao(null);
				for (AinTiposCaraterInternacao tipoCarater : listaTiposCarater) {
					Collator collator = Collator.getInstance(new Locale("pt", "BR"));
					collator.setStrength(Collator.PRIMARY);
					if (collator.compare(tipoCarater.getDescricao().toLowerCase(), natureza.getDescricao().toLowerCase()) == 0) {
						this.caraterInternacaoPesq = tipoCarater;
						break;
					}
				}
			}
		}
	}

	/**
	 * Método responsável por executar as verificações de tela quando o botão
	 * "confirmar" da modal é acionado.
	 * 
	 * @return
	 */
	public String executarAcaoBotaoConfirmacao() {
		String retorno = null;
		if (verificouCnrac) {
			this.verificouCnrac = false;
			this.mostrarAlerta = false;
			this.verificarAcomodacaoAutorizada();
		} else if (verificouAtualizarAcomodacao) {
			retorno = this.redirecionarAcomodacaoAutorizada();
		} else {
			if (verificouProcedimento && !verificouUF) {
				retorno = this.verificarPacienteOutroEstado();
			} else if (verificouUF && !verificouMatriculaConvenio) {
				retorno = this.verificarConvenioSaudePaciente();
			} else if (verificouMatriculaConvenio && !verificouClinica) {
				retorno = this.verificarClinica();
			} else if (verificouClinica && !verificouEspecialidade) {
				retorno = this.verificarEspecialidade();
			} else if (verificouEspecialidade && !verificouCERIH) {
				retorno = this.verificarInformarNumeroCERIH();
			} else if (verificouCERIH) {
				retorno = this.internarPaciente();
			}
		}
		return retorno;
	}

	public String obterLabelBotaoConfirmacao() {
		return this.getLabelBotaoConfirmacao();
	}

	/**
	 * Método que cancela a modal de confirmações
	 */
	public void cancelarModal() {
		mostrarAlerta = false;
		verificouUF = false;
		verificouMatriculaConvenio = false;
		verificouClinica = false;
		verificouEspecialidade = false;
		perguntouCirurgia = true;
		verificouCERIH = false;
		this.verificouAtualizarAcomodacao = false;
		this.verificouCnrac = false;
	}

	// MÉTODOS DA NOVA SUGGESTION DE CONVÊNIOS

	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			FatConvenioSaudePlano convenioPlano = this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId);
			if (convenioPlano == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.convenioId, this.planoId);
			}
			if (convenioPlano != null) {
				if (convenioPlano.getConvenioSaude().getPermissaoInternacao() && DominioTipoPlano.I.equals(convenioPlano.getIndTipoPlano())) {
					if (DominioSituacao.A.equals(convenioPlano.getConvenioSaude().getSituacao())
							&& DominioSituacao.A.equals(convenioPlano.getIndSituacao())) {
						this.atribuirPlano(convenioPlano);
						sugerirProfessorCRM();
					} else {
						atribuirPlano(null);
					}
				} else {
					atribuirPlano(null);
				}
			} else {
				atribuirPlano(null);
			}
		}
	}

	/**
	 * Método utilizado para quando o usuário escolher o convênio direto pela
	 * suggestion
	 */
	public void processarSelectSuggestionConvenio() {
		this.atribuirPlano();
		this.sugerirProfessorCRM();
	}

	public void atribuirPlano(FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.convenioSaudePlano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void atribuirPlano() {

		if (this.convenioSaudePlano != null) {
			this.convenioId = this.convenioSaudePlano.getConvenioSaude().getCodigo();
			this.planoId = this.convenioSaudePlano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String parametro) {

		List<FatConvenioSaudePlano> retorno = null;
		retorno = this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosInternacao((String) parametro);
		return retorno;
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosFromConverter(Object parametro) {
		List<FatConvenioSaudePlano> retorno = null;
		List<FatConvenioSaudePlano> listaConvenioJaAssociado = new ArrayList<FatConvenioSaudePlano>();
		if (this.convenioSaudePlano != null) {
			listaConvenioJaAssociado.add(convenioSaudePlano);
			retorno = listaConvenioJaAssociado;
		} else {
			retorno = this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosInternacao((String) parametro);
		}
		if (retorno != null && retorno.size() == 1) {
			this.convenioId = retorno.get(0).getConvenioSaude().getCodigo();
			this.planoId = retorno.get(0).getId().getSeq();
		}
		return retorno;
	}

	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(Short espSeq, String espSigla, Short cnvCodigo,
			Boolean indVerfEscalaProfInt, String strParam, Integer matriculaProfessor, Short vinCodigoProfessor) {
		return internacaoFacade.pesquisarProfessoresCrm(espSeq, espSigla, cnvCodigo, indVerfEscalaProfInt, strParam, matriculaProfessor,
				vinCodigoProfessor);
	}
	
	public void pesquisarLeitos() {
		listaLeitos = cadastrosBasicosInternacaoFacade.pesquisarLeitosDesocupados(this.ainLeitoId);
	}

	public List<AinLeitos> pesquisarLeitosDesocupados(String param){
		this.listaLeitos = this.cadastrosBasicosInternacaoFacade.pesquisarLeitosDesocupados(param);
		return this.listaLeitos; 
	}
	
	public String linkParaAddMedicoExterno() {
		return MEDICO_ATENDIMENTO_EXTERNO_CRUD;
	}
	
	// GETTERS AND SETTERS
	public AghEspecialidades getEspecialidadePesq() {
		return especialidadePesq;
	}

	public void setEspecialidadePesq(AghEspecialidades especialidadePesq) {
		this.especialidadePesq = especialidadePesq;
	}

	public AghOrigemEventos getOrigemInternacaoPesq() {
		return origemInternacaoPesq;
	}

	public void setOrigemInternacaoPesq(AghOrigemEventos origemInternacaoPesq) {
		this.origemInternacaoPesq = origemInternacaoPesq;
	}
	
	public AghMedicoExterno getMedicoExternoPesq() {
		return medicoExternoPesq;
	}

	public void setMedicoExternoPesq(AghMedicoExterno medicoExternoPesq) {
		this.medicoExternoPesq = medicoExternoPesq;
	}

	public AghInstituicoesHospitalares getHospitalOrigemPesq() {
		return hospitalOrigemPesq;
	}

	public void setHospitalOrigemPesq(AghInstituicoesHospitalares hospitalOrigemPesq) {
		this.hospitalOrigemPesq = hospitalOrigemPesq;
	}

	public AelProjetoPesquisas getProjetoPesquisaPesq() {
		return projetoPesquisaPesq;
	}

	public void setProjetoPesquisaPesq(AelProjetoPesquisas projetoPesquisaPesq) {
		this.projetoPesquisaPesq = projetoPesquisaPesq;
	}

	public Integer getAipPacCodigo() {
		return aipPacCodigo;
	}

	public void setAipPacCodigo(Integer aipPacCodigo) {
		this.aipPacCodigo = aipPacCodigo;
	}

	public Integer getAghUniFuncSeq() {
		return aghUniFuncSeq;
	}

	public void setAghUniFuncSeq(Integer aghUniFuncSeq) {
		this.aghUniFuncSeq = aghUniFuncSeq;
	}

	public String getAinLeitoId() {
		return ainLeitoId;
	}

	public void setAinLeitoId(String ainLeitoId) {
		this.ainLeitoId = ainLeitoId;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getAinQuartoNumero() {
		return ainQuartoNumero;
	}

	public void setAinQuartoNumero(Integer ainQuartoNumero) {
		this.ainQuartoNumero = ainQuartoNumero;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public List<AinCidsInternacao> getCidsInternacao() {
		return cidsInternacao;
	}

	public void setCidsInternacao(List<AinCidsInternacao> cidsInternacao) {
		this.cidsInternacao = cidsInternacao;
	}

	public Integer getCidInternacaoSeq() {
		return cidInternacaoSeq;
	}

	public void setCidInternacaoSeq(Integer cidInternacaoSeq) {
		this.cidInternacaoSeq = cidInternacaoSeq;
	}

	public ProfessorCrmInternacaoVO getProfessorPesq() {
		return professorPesq;
	}

	public void setProfessorPesq(ProfessorCrmInternacaoVO professorPesq) {
		this.professorPesq = professorPesq;
	}

	public String getStyleProntuario() {
		String retorno = "";

		if (this.paciente != null && this.paciente.isProntuarioVirtual()) {
			retorno = "background-color:#0000ff";
		}
		return retorno;

	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public List<AinResponsaveisPaciente> getListaResponsaveisPaciente() {
		return listaResponsaveisPaciente;
	}

	public void setListaResponsaveisPaciente(List<AinResponsaveisPaciente> listaResponsaveisPaciente) {
		this.listaResponsaveisPaciente = listaResponsaveisPaciente;
	}

	public List<AinResponsaveisPaciente> getListaResponsaveisPacienteExcluidos() {
		return listaResponsaveisPacienteExcluidos;
	}

	public void setListaResponsaveisPacienteExcluidos(List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos) {
		this.listaResponsaveisPacienteExcluidos = listaResponsaveisPacienteExcluidos;
	}

	public Integer getCodigoClinicaPesq() {
		return codigoClinicaPesq;
	}

	public void setCodigoClinicaPesq(Integer codigoClinicaPesq) {
		this.codigoClinicaPesq = codigoClinicaPesq;
	}

	public List<AghClinicas> getListaClinicasPesq() {
		return listaClinicasPesq;
	}

	public void setListaClinicasPesq(List<AghClinicas> listaClinicasPesq) {
		this.listaClinicasPesq = listaClinicasPesq;
	}

	public String getDescricaoClinicaBuscaLov() {
		return descricaoClinicaBuscaLov;
	}

	public void setDescricaoClinicaBuscaLov(String descricaoClinicaBuscaLov) {
		this.descricaoClinicaBuscaLov = descricaoClinicaBuscaLov;
	}

	public AinTiposCaraterInternacao getCaraterInternacaoPesq() {
		return caraterInternacaoPesq;
	}

	public void setCaraterInternacaoPesq(AinTiposCaraterInternacao caraterInternacaoPesq) {
		this.caraterInternacaoPesq = caraterInternacaoPesq;
	}

	public List<AinCidsInternacao> getCidsInternacaoExcluidos() {
		return cidsInternacaoExcluidos;
	}

	public void setCidsInternacaoExcluidos(List<AinCidsInternacao> cidsInternacaoExcluidos) {
		this.cidsInternacaoExcluidos = cidsInternacaoExcluidos;
	}

	public Object getRetornouTelaAssociada() {
		return retornouTelaAssociada;
	}

	public void setRetornouTelaAssociada(Object retornouTelaAssociada) {
		this.retornouTelaAssociada = retornouTelaAssociada;
	}

	public FatItensProcedHospitalar getItemProcedHospitalar() {
		return itemProcedHospitalar;
	}

	public void setItemProcedHospitalar(FatItensProcedHospitalar itemProcedHospitalar) {
		this.itemProcedHospitalar = itemProcedHospitalar;
	}

	public Boolean getMostrarAlerta() {
		return mostrarAlerta;
	}

	public void setMostrarAlerta(Boolean mostrarAlerta) {
		this.mostrarAlerta = mostrarAlerta;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public String getModalMessage() {
		return this.mensagemModal;
	}

	public Integer getAinAtendimentoUrgenciaSeq() {
		return ainAtendimentoUrgenciaSeq;
	}

	public void setAinAtendimentoUrgenciaSeq(Integer ainAtendimentoUrgenciaSeq) {
		this.ainAtendimentoUrgenciaSeq = ainAtendimentoUrgenciaSeq;
	}

	public Integer getAinSolicitacaoInternacaoSeq() {
		return ainSolicitacaoInternacaoSeq;
	}

	public void setAinSolicitacaoInternacaoSeq(Integer ainSolicitacaoInternacaoSeq) {
		this.ainSolicitacaoInternacaoSeq = ainSolicitacaoInternacaoSeq;
	}

	public Boolean getVerificouUF() {
		return verificouUF;
	}

	public void setVerificouUF(Boolean verificouUF) {
		this.verificouUF = verificouUF;
	}

	public Boolean getVerificouClinica() {
		return verificouClinica;
	}

	public void setVerificouClinica(Boolean verificouClinica) {
		this.verificouClinica = verificouClinica;
	}

	public Boolean getVerificouEspecialidade() {
		return verificouEspecialidade;
	}

	public void setVerificouEspecialidade(Boolean verificouEspecialidade) {
		this.verificouEspecialidade = verificouEspecialidade;
	}

	public Boolean getVerificouProcedimento() {
		return verificouProcedimento;
	}

	public void setVerificouProcedimento(Boolean verificouProcedimento) {
		this.verificouProcedimento = verificouProcedimento;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Boolean getPerguntouCirurgia() {
		return perguntouCirurgia;
	}

	public void setPerguntouCirurgia(Boolean perguntouCirurgia) {
		this.perguntouCirurgia = perguntouCirurgia;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Boolean getVerificouCERIH() {
		return verificouCERIH;
	}

	public void setVerificouCERIH(Boolean verificouCERIH) {
		this.verificouCERIH = verificouCERIH;
	}

	public Boolean getIsInclusao() {
		return isInclusao;
	}

	public void setIsInclusao(Boolean isInclusao) {
		this.isInclusao = isInclusao;
	}

	public String getLabelBotaoConfirmacao() {
		return labelBotaoConfirmacao;
	}

	public void setLabelBotaoConfirmacao(String labelBotaoConfirmacao) {
		this.labelBotaoConfirmacao = labelBotaoConfirmacao;
	}

	public Boolean getVerificouAtualizarAcomodacao() {
		return verificouAtualizarAcomodacao;
	}

	public void setVerificouAtualizarAcomodacao(Boolean verificouAtualizarAcomodacao) {
		this.verificouAtualizarAcomodacao = verificouAtualizarAcomodacao;
	}

	public Boolean getVerificouMatriculaConvenio() {
		return verificouMatriculaConvenio;
	}

	public void setVerificouMatriculaConvenio(Boolean verificouMatriculaConvenio) {
		this.verificouMatriculaConvenio = verificouMatriculaConvenio;
	}

	public Boolean getVerificouCnrac() {
		return verificouCnrac;
	}

	public void setVerificouCnrac(Boolean verificouCnrac) {
		this.verificouCnrac = verificouCnrac;
	}

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public AghCid getItemCid() {
		return itemCid;
	}

	public void setItemCid(AghCid itemCid) {
		this.itemCid = itemCid;
	}

	public boolean isConsiderarCidFiltro() {
		return considerarCidFiltro;
	}

	public void setConsiderarCidFiltro(boolean considerarCidFiltro) {
		this.considerarCidFiltro = considerarCidFiltro;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}

	public String getQuartoDescricao() {
		return quartoDescricao;
	}

	public void setQuartoDescricao(String quartoDescricao) {
		this.quartoDescricao = quartoDescricao;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public ParametrosTelaVO getParametrosTela() {
		return parametrosTela;
	}

	public void setParametrosTela(ParametrosTelaVO parametrosTela) {
		this.parametrosTela = parametrosTela;
	}

	public Boolean getIndDifClasse() {
		return indDifClasse;
	}

	public void setIndDifClasse(Boolean indDifClasse) {
		this.indDifClasse = indDifClasse;
	}

	public Boolean getIndEnvProntUnidInt() {
		return indEnvProntUnidInt;
	}

	public void setIndEnvProntUnidInt(Boolean indEnvProntUnidInt) {
		this.indEnvProntUnidInt = indEnvProntUnidInt;
	}

	public String getJustificativaAltDel() {
		return justificativaAltDel;
	}

	public void setJustificativaAltDel(String justificativaAltDel) {
		this.justificativaAltDel = justificativaAltDel;
	}
	
	public AinLeitos getLeitos() {
		return leitos;
	}

	public void setLeitos(AinLeitos leitos) {
		this.leitos = leitos;
	}
	public String getCameFromBegin() {
		return cameFromBegin;
	}

	public void setCameFromBegin(String cameFromBegin) {
		this.cameFromBegin = cameFromBegin;
	}
}