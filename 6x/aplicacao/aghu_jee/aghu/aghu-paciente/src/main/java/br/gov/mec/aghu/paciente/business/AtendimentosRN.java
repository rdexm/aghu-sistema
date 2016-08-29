package br.gov.mec.aghu.paciente.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoApache;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioAvisoPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmFichaApacheId;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmListaServResponsavel;
import br.gov.mec.aghu.model.MpmListaServSumrAltaId;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.vo.AtualizarLocalAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class AtendimentosRN extends BaseBusiness {

	private static final String MSG_MODULO_INATIVO = "Modulo inativo, ira propagar para fazer chamada nativa ";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@EJB
	private AtendimentosON atendimentosON;
	
	@EJB
	private PacienteON pacienteON;
	
	@EJB
	private AtendimentoEnforceRN atendimentoEnforceRN;
	
	@EJB
	private AtendimentoJournalRN atendimentoJournalRN;
	
	private static final Log LOG = LogFactory.getLog(AtendimentosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7822198543144253579L;
	
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade(){
		return this.prescricaoEnfermagemFacade;
	}
	
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum AtendimentosRNExceptionCode implements BusinessExceptionCode {
		// messages_pacientes_pt.properties
		AIP_00013, AGH_00185, AGH_00186, AGH_00187, AGH_00340, AGH_00283, AGH_00163, AGH_00182, AGH_00202, RAP_00175, AGH_00439, AGH_00523, AGH_00524, AGH_00492, AGH_00478, AGH_00441, AIN_00345, CSE_00192, AGH_00577, AGH_00573, AGH_00574, AGH_00575, AGH_00576, AGH_00662, AGH_00663, AGH_00664, AGH_00458, AGH_00465, MSG_ERRO_ATUALIZAR_AGH_ATENDIMENTOS, MSG_ERRO_ATUALIZAR_MAM_DIAGNOSTICO, MSG_RN_PMRP_TRC_ESP_ATD;
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * inserção de um registro da tabela AGH_ATENDIMENTOS
	 * 
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException 
	 */
	public void inserirAtendimento(final AghAtendimentos atendimento, String nomeMicrocomputador) throws BaseException {
				
		this.executarAghtAtdBri(atendimento);

		this.getAghuFacade().inserirAghAtendimentos(atendimento, true);

		this.executarEnforce(atendimento, null, DominioOperacoesJournal.INS, nomeMicrocomputador);
	}

	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB Trigger AGHT_ATD_ASI, AGHT_ATD_ASD, AGHT_ATD_ASU
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException 
	 */
	public void executarEnforce(final AghAtendimentos atendimento, final AghAtendimentos atendimentoOld,
			final DominioOperacoesJournal operacao, String nomeMicrocomputador) throws BaseException {

		this.getAtendimentoEnforceRN().enforceAtdRules(atendimento,
				atendimentoOld, operacao, nomeMicrocomputador);
	}

	public Boolean verificarUnidadeChecagem(Integer atdSeq) {
		return getAtendimentoEnforceRN().verificarUnidadeChecagem(atdSeq);
	}
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB Trigger AGHT_ATD_BRI
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void executarAghtAtdBri(AghAtendimentos atendimento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		
		// Servidor Logado
		if (atendimento.getServidorMovimento() == null) {
			atendimento.setServidorMovimento(servidorLogado);
			if (atendimento.getServidorMovimento() == null) {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.RAP_00175);
			}
		}

		// verifica se data fim não é maior que data inicio
		verificarDataInicioDataFimAtendimento(atendimento.getDthrInicio(), atendimento.getDthrFim());

		// Radioterapia (28)
		final AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_AGHU_TIPO_TRATAMENTO_RADIOTERAPIA;
		final AghParametros parametroTratamentoRadioterapia = this.getParametroFacade().buscarAghParametro(parametroEnum);
		final Byte seqTratRadio = parametroTratamentoRadioterapia.getVlrNumerico().byteValue();
		if (atendimento.getIndTipoTratamento() != null
				&& seqTratRadio.equals(atendimento.getIndTipoTratamento().getCodigoByte())) {
			verificarDataNascimentoAtendimento(atendimento.getPaciente().getCodigo(), atendimento.getDthrInicio());
			verificarRadioterapia(atendimento.getPaciente().getCodigo(), atendimento.getIndTipoTratamento()
					.getCodigoByte());
		}

		// Origem
		if (atendimento.getInternacao() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.I);
			atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
		} else if (atendimento.getAtendimentoUrgencia() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.U);
			atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
		} else if (atendimento.getHospitalDia() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.H);
		} else if (atendimento.getAtendimentoPacienteExterno() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.X);
		} else if (atendimento.getDcaBolNumero() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.D);
		} else if (atendimento.getConsulta() != null) {
			atendimento.setOrigem(DominioOrigemAtendimento.A);
		} else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.N)) {
			atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
		}
		
		String leitoID = atendimento.getLeito() != null ? atendimento.getLeito().getLeitoID() : null;
	
		this.controleInfeccaoFacade.enviaEmailGmr(atendimento.getOrigem(), atendimento.getPaciente()
					.getCodigo(), atendimento.getProntuario(), leitoID, 
					atendimento.getUnidadeFuncional().getSeq());
	}

	/**
	 * ORADB Procedure RN_ATDP_VER_DT_FIM
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataInicioDataFimAtendimento(final Date dtInicio, final Date dtFim) throws ApplicationBusinessException {
		if (dtFim != null) {
			if (dtInicio != null && dtFim.compareTo(dtInicio) < 0) {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00439);
			}
		}
	}

	/**
	 * ORADB Procedure RN_ATDP_VER_DT_NASC
	 * 
	 * @param pacCodigo
	 * @param dtInicio
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataNascimentoAtendimento(final Integer pacCodigo, final Date dtInicio) throws ApplicationBusinessException {
		final AipPacientes paciente = this.getPacienteON().obterPaciente(pacCodigo);
		if (dtInicio.compareTo(paciente.getDtNascimento()) < 0) {
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00523);
		}
	}

	/**
	 * ORADB Procedure RN_ATDP_VER_ATD_RAD
	 * 
	 * @param pacCodigo
	 * @param seqTipoTratamento
	 * @throws ApplicationBusinessException
	 */
	private void verificarRadioterapia(final Integer pacCodigo, final Byte seqTipoTratamento) throws ApplicationBusinessException {
		final List<AghAtendimentos> listaAtendimentos = this.getAghuFacade().listarAtendimentos(pacCodigo, seqTipoTratamento);
		if (listaAtendimentos.size() > 0) {
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00524);
		}
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * atualização de um registro da tabela AGH_ATENDIMENTOS
	 * 
	 * 
	 * @param atendimento
	 * @param servidorLogado 
	 * @return
	 * @throws BaseException 
	 */
	public void atualizarAtendimento(final AghAtendimentos atendimento, final AghAtendimentos atendimentoOld, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor)
			throws BaseException {
		this.executarAghtAtdBru(atendimento, atendimentoOld, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor);
		
		this.getAghuFacade().atualizarAghAtendimentos(atendimento, false);

		// Geração de Journal
		this.executarAghtAtdAru(atendimento, atendimentoOld);

		this.executarEnforce(atendimento, atendimentoOld, DominioOperacoesJournal.UPD, nomeMicrocomputador);

		this.flush();
	}

	/**
	 * Método que implementa a trigger de geração de journal de UPDATE para o
	 * objeto AghAtendimentos.
	 * 
	 * ORADB Trigger AGHT_ATD_ARU
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void executarAghtAtdAru(final AghAtendimentos atendimento, final AghAtendimentos atendimentoOld) {
		this.getAtendimentoJournalRN().gerarJournalAtendimento(DominioOperacoesJournal.UPD, atendimento, atendimentoOld);
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela remoção
	 * de um registro da tabela AGH_ATENDIMENTOS
	 * 
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException 
	 */
	public void removerAtendimento(final AghAtendimentos atendimentoOld, String nomeMicrocomputador) throws BaseException {
		//TODO remover todos os AAC_CID_ATENDIMENTOS
		
		try{
			this.getAmbulatorioFacade().excluirConsultasAghAtendimento(atendimentoOld.getSeq(), nomeMicrocomputador, false);
			this.getBancoDeSangueFacade().excluirSolicitacoesHemoterapicasPorAtendimeto(atendimentoOld);
			
			this.getExamesFacade().excluirPedidosExamesPorAtendimento(atendimentoOld);
			
			this.getSolicitacaoExameFacade().excluirSolicitacaoExamesPorAtendimento(atendimentoOld);
			
			this.getFaturamentoFacade().excluirProcedimentosAmbulatoriaisRealizadosPorAtendimento(atendimentoOld);
			
			//TODO remover todos os AFA_PREPARO_MDTOS
			
			this.getAmbulatorioFacade().excluirDiagnosticosPorCidAtendimentos(atendimentoOld);
			
			// Chamadas para criação de Journal de DELETE
			this.executarAghtAtdArd(atendimentoOld);
			
			this.getAghuFacade().removerAghAtendimentos(atendimentoOld);
			
			// Chamadas para enforce de DELETE
			this.executarEnforce(null, atendimentoOld, DominioOperacoesJournal.DEL, nomeMicrocomputador);
		} catch(Exception e) {
			LOG.error("Exceção lançada: ", e);
		}
		
	}

	

	/**
	 * Método que implementa a trigger "after row delete" criando o journal do
	 * objeto AghAtendimentos que será removido
	 * 
	 * ORADB Trigger AGHT_ATD_ARD
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void executarAghtAtdArd(final AghAtendimentos atendimento) {
		this.getAtendimentoJournalRN().gerarJournalAtendimento(DominioOperacoesJournal.DEL, atendimento, null);
	}

	/*
	 * Procedures da package AGHK_ATD_RN
	 */

	/**
	 * Atualizar dthr_fim atendimento a partir da dhr_saida da internação
	 * 
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_ATU_FIM_ATD
	 * 
	 * @param internacao
	 * @param hospitalDia
	 * @param atendimentoUrgencia
	 * @param dthrSaida
	 * @return
	 * @throws BaseException 
	 */
	public void atualizarFimAtendimento(final AinInternacao internacao, final AhdHospitaisDia hospitalDia,
			final AinAtendimentosUrgencia atendimentoUrgencia, final Date dthrSaida, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AghAtendimentos atendimento = null;
		AghAtendimentos atendimentoOld = null;
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (internacao != null) {
			atendimento = internacao.getAtendimento();
			atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para esta internação
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00185);
			}
			atendimento.setDthrFim(dthrSaida);
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
		if (hospitalDia != null) {
			atendimento = hospitalDia.getAtendimento();
			atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para este tratamento hospital dia
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00186);
			}
			atendimento.setDthrFim(dthrSaida);
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
		if (atendimentoUrgencia != null) {
			atendimento = atendimentoUrgencia.getAtendimento();
			atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para este atendimento de urgência
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00187);
			}
			atendimento.setDthrFim(dthrSaida);
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
	}

	/**
	 * Update no pac_codigo chamada a partir do update na internação
	 * 
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_ATU_PAC_ATD
	 * 
	 * @param atendimentoUrgencia
	 * @param internacao
	 * @param hospitalDia
	 * @param consulta
	 * @param paciente
	 * @return
	 * @throws BaseException 
	 */
	public void atualizarPacienteAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia, final AinInternacao internacao,
			final AhdHospitaisDia hospitalDia, final AacConsultas consulta, final AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario) throws BaseException {
		if (paciente == null) {
			throw new IllegalArgumentException("Paciente obrigatório");
		}
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (atendimentoUrgencia != null) {
			final AghAtendimentos atendimento = atendimentoUrgencia.getAtendimento();
			final AghAtendimentos atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para este atendimento de urgência
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00187);
			}
			atendimento.setPaciente(paciente);
			atendimento.setProntuario(paciente.getProntuario());
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
		if (internacao != null) {
			final AghAtendimentos atendimento = internacao.getAtendimento();
			final AghAtendimentos atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para esta internação
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00185);
			}
			atendimento.setPaciente(paciente);
			atendimento.setProntuario(paciente.getProntuario());
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
		if (hospitalDia != null) {
			final AghAtendimentos atendimento = hospitalDia.getAtendimento();
			final AghAtendimentos atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			if (atendimento == null) {
				// Atendimento não encontrado para este tratamento hospital dia
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00186);
			}
			atendimento.setPaciente(paciente);
			atendimento.setProntuario(paciente.getProntuario());
			this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
		if (consulta != null) {
			final Set<AghAtendimentos> atendimentos = consulta.getAtendimentos();
			if (atendimentos.size() == 0) {
				if(substituirProntuario) {
					return;
				}
				// Atendimento não encontrado para esta consulta
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00340);
			}
			AghAtendimentos atendimentoOld = null;
			for (final AghAtendimentos atendimento : atendimentos) {
				atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
				atendimento.setPaciente(paciente);
				atendimento.setProntuario(paciente.getProntuario());

				this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
			}
		}
	}

	/**
	 * Método que atualiza o prontuário no atendimento quando gerado
	 * 
	 * ORADB Procedure aghk_atd_rn.rn_atdp_atu_pront
	 * 
	 * @param codigoPaciente
	 *            , prontuario
	 * @return
	 */
	public void atualizarProntuarioNoAtendimento(final Integer codigoPaciente, final Integer prontuario, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		
		final List<AghAtendimentos> listaAtendimentos = this.getCadastroPacienteFacade().pesquisarAtendimentosPorPaciente(codigoPaciente);
		if (listaAtendimentos != null) {
			AghAtendimentos atendimentoOld = null;
			for (final AghAtendimentos atendimento : listaAtendimentos) {
				atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
				atendimento.setProntuario(prontuario);
				try {
					this.getAtendimentosON().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				} catch (final Exception e) { //essa não seria a forma ideal de tratar a exceção, contudo não tenho como propagar a exceção no momento, pois causou mais que 50 erros após colocar a chamada adequada as triggers
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.MSG_ERRO_ATUALIZAR_AGH_ATENDIMENTOS);
				}
			}
		}
	}

	/**
	 * Atualiza o profissional responsável pelo atendimento.
	 * 
	 * ORADB AGHK_ATD_RN.RN_ATDP_ATU_PROF
	 * 
	 * @param seqInternacao
	 * @param seqHospitalDia
	 * @param servidor
	 * @throws BaseException 
	 */
	public void atualizarProfissionalResponsavelPeloAtendimento(final Integer seqInternacao, final Integer seqHospitalDia,
			final RapServidores servidor, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (seqInternacao != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqInternacao(seqInternacao);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setServidor(servidor);
					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00185);
			}
		}

		if (seqHospitalDia != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqHospitalDia(seqHospitalDia);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setServidor(servidor);
					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00186);
			}
		}
	}

	/**
	 * Atualiza a especialidade no atendimento.
	 * 
	 * ORADB AGHK_ATD_RN.RN_ATDP_ATU_ESP
	 * 
	 * @param seqInternacao
	 * @param seqHospitalDia
	 * @param seqEspecialidade
	 * @throws BaseException 
	 */
	public void atualizarEspecialidadeNoAtendimento(final Integer seqInternacao, final Integer seqHospitalDia,
			final Short seqEspecialidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final AghEspecialidades especialidade = seqEspecialidade != null ? this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade) : null;
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (seqInternacao != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqInternacao(seqInternacao);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setEspecialidade(especialidade);
					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00185);
			}

		}

		if (seqHospitalDia != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqHospitalDia(seqHospitalDia);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setEspecialidade(especialidade);
					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00186);
			}
		}
	}

	/**
	 * Atualiza o local do atendimento (quarto,leito,unidade)
	 * 
	 * ORADB AGHK_ATD_RN.RN_ATDP_ATU_LOCAL
	 * 
	 * @param seqInternacao
	 * @param seqHospitalDia
	 * @param seqAtendimentoUrgencia
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public AtualizarLocalAtendimentoVO atualizarLocalAtendimento(final Integer seqInternacao, final Integer seqHospitalDia,
			final Integer seqAtendimentoUrgencia, final String leitoId, final Short numeroQuarto, final Short seqUnidadeFuncional, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final AinLeitos leito = StringUtils.isNotBlank(leitoId) ? this.getInternacaoFacade().obterAinLeitosPorChavePrimaria(leitoId) : null;
		final AinQuartos quarto = numeroQuarto != null ? this.getInternacaoFacade().obterAinQuartosPorChavePrimaria(numeroQuarto) : null;
		final AghUnidadesFuncionais unidadeFuncional = seqUnidadeFuncional != null ? this.getAghuFacade()
				.obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional) : null;

		if (seqInternacao != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqInternacao(seqInternacao);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					// Não é gerado journal, pois não existe informações de
					// leito, quarto e unidade funcional nos journals
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setLeito(leito);
					atendimento.setQuarto(quarto);
					atendimento.setUnidadeFuncional(unidadeFuncional);

					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00185);
			}
		}

		if (seqHospitalDia != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqHospitalDia(seqHospitalDia);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					// Não é gerado journal, pois não existe informações de
					// unidade funcional nos journals
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setUnidadeFuncional(unidadeFuncional);
					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00186);
			}
		}

		if (seqAtendimentoUrgencia != null) {
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosPorSeqAtendimentoUrgencia(seqAtendimentoUrgencia);
			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					// Não é gerado journal, pois não existe informações de
					// leito, quarto e unidade funcional nos journals
					atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
					atendimento.setLeito(leito);
					atendimento.setQuarto(quarto);
					atendimento.setUnidadeFuncional(unidadeFuncional);

					this.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				}
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00283);
			}
		}

		return new AtualizarLocalAtendimentoVO(leitoId, numeroQuarto, seqUnidadeFuncional);
	}

	/**
	 * Inclusão de ocorrência na tabela agh_atendimentos.
	 * 
	 * ORADB AGHK_ATD_RN.RN_ATDP_ATU_INS_ATD
	 * 
	 * @param codigoPaciente
	 * @param dataHoraInicio
	 * @param seqHospitalDia
	 * @param seqInternacao
	 * @param seqAtendimentoUrgencia
	 * @param dataHoraFim
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @param seqEspecialidade
	 * @param servidor
	 * @param codigoClinica
	 * @param digitador
	 * @param dcaBolNumero
	 * @param dcaBolBsaCodigo
	 * @param dcaBolData
	 * @param apeSeq
	 * @param numeroConsulta
	 * @param seqGradeAgendamenConsultas
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public InclusaoAtendimentoVO inclusaoAtendimento(final Integer codigoPaciente, final Date dataHoraInicio,
			final Integer seqHospitalDia, final Integer seqInternacao, final Integer seqAtendimentoUrgencia, final Date dataHoraFim,
			final String leitoId, final Short numeroQuarto, final Short seqUnidadeFuncional, final Short seqEspecialidade,
			final RapServidores servidor, final Integer codigoClinica, final RapServidores digitador, final Integer dcaBolNumero,
			final Short dcaBolBsaCodigo, final Date dcaBolData, final Integer apeSeq, final Integer numeroConsulta,
			final Integer seqGradeAgendamenConsultas, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			Integer clinicaPed = null;
			Integer clinicaPac = null;
			DominioSimNao indEspecialidadePediatrica = DominioSimNao.N;
			Integer prontuario = null;
			Date dataHoraIngressoUnidade = null;
			DominioPacAtendimento indPacAtendimento = null;

			if (seqEspecialidade != null) {
				final AghEspecialidades especialidade = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);

				if (especialidade != null) {
					clinicaPac = especialidade.getClinica() != null ? especialidade.getClinica().getCodigo() : null;
					indEspecialidadePediatrica = DominioSimNao.getInstance(especialidade.getIndEspPediatrica());
				} else {
					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00163);
				}
			} else {
				clinicaPac = codigoClinica;
			}

			final AipPacientes paciente = this.getAipPacientesDAO().obterPorChavePrimaria(codigoPaciente);
			if (paciente != null) {
				prontuario = paciente.getProntuario();
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AIP_00013);
			}

			if (seqHospitalDia != null || seqAtendimentoUrgencia != null || seqInternacao != null || apeSeq != null) {
				dataHoraIngressoUnidade = dataHoraInicio;
			}

			if (numeroConsulta != null && seqAtendimentoUrgencia != null) {
				final AacConsultas consulta = this.getAmbulatorioFacade().obterConsultaPorNumero(numeroConsulta);

				if (consulta == null) {
					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00202);
				}
			}

			if (apeSeq != null
					|| (numeroConsulta != null && (seqHospitalDia == null && seqInternacao != null && seqAtendimentoUrgencia == null))) {
				indPacAtendimento = DominioPacAtendimento.N;
			} else {
				indPacAtendimento = DominioPacAtendimento.S;
			}

			final AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CLINICA_PEDIATRIA);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				clinicaPed = aghParametros.getVlrNumerico().intValue();
			} else {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00182);
			}

			final AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setPaciente(codigoPaciente != null ? this.getAipPacientesDAO().obterPorChavePrimaria(codigoPaciente) : null);
			atendimento.setDthrInicio(dataHoraInicio);
			if (Objects.equals(clinicaPac == null ? null : clinicaPac, clinicaPed)){
				atendimento.setIndPacPediatrico(true);
			}
			else{
				atendimento.setIndPacPediatrico(indEspecialidadePediatrica.isSim());
			}
			atendimento.setIndPacPrematuro(false);
			atendimento.setHospitalDia(seqHospitalDia != null ? this.getInternacaoFacade().obterAhdHospitaisDiaPorChavePrimaria(seqHospitalDia) : null);
			atendimento.setInternacao(seqInternacao != null ? this.getInternacaoFacade().obterAinInternacaoPorChavePrimaria(seqInternacao) : null);
			atendimento.setAtendimentoUrgencia(seqAtendimentoUrgencia != null ? this.getInternacaoFacade().obterAinAtendimentosUrgenciaPorChavePrimaria(seqAtendimentoUrgencia) : null);
			atendimento.setDthrFim(dataHoraFim);
			atendimento.setIndPacAtendimento(indPacAtendimento);
			atendimento.setLeito(StringUtils.isNotBlank(leitoId) ? this.getInternacaoFacade().obterAinLeitosPorChavePrimaria(leitoId)
					: null);
			atendimento
					.setQuarto(numeroQuarto != null ? this.getInternacaoFacade().obterAinQuartosPorChavePrimaria(numeroQuarto) : null);
			atendimento.setUnidadeFuncional(seqUnidadeFuncional != null ? this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional) : null);
			atendimento.setEspecialidade(seqEspecialidade != null ? this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade) : null);
			atendimento.setServidor(servidor);
			atendimento.setServidorMovimento(digitador);
			atendimento.setProntuario(prontuario);
			atendimento.setDthrIngressoUnidade(dataHoraIngressoUnidade);
			atendimento.setDcaBolNumero(dcaBolNumero);
			atendimento.setDcaBolBsaCodigo(dcaBolBsaCodigo);
			atendimento.setDcaBolData(dcaBolData);

			if (apeSeq != null) {
				atendimento.setAtendimentoPacienteExterno(this.getExamesFacade().obterAghAtendimentosPacExternPorChavePrimaria(apeSeq));
			}
			atendimento.setConsulta(numeroConsulta != null ? this.getAmbulatorioFacade().obterConsultaPorNumero(numeroConsulta) : null);

			this.inserirAtendimento(atendimento, nomeMicrocomputador);

			return new InclusaoAtendimentoVO(leitoId, numeroQuarto, seqUnidadeFuncional, atendimento.getSeq());
		} catch (final Exception e) {
			logError(e.getMessage());
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00202);
		}
	}

	/**
	 * ORADB AGHK_ATD_RN.RN_ATDP_VER_FIM_ATD
	 * 
	 * Nota: no oracle, era esperado um segundo parâmetro, o qual seria apenas
	 * para ser setado. Agora, o mesmo deve ser setado na chamada desta função.
	 * 
	 * @param dataHoraFim
	 * @return
	 */
	public DominioPacAtendimento verificaFimAtendimento(final Date dataHoraFim) {
		return dataHoraFim != null ? DominioPacAtendimento.N : DominioPacAtendimento.S;
	}

	/**
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_GERA_DIAG_CT
	 * 
	 * @param seqAtendimento
	 * @param seqUnidadeFuncional
	 * @param dataIngressoUnidade
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void gerarDiagnosticoCti(final Integer seqAtendimento, final Short seqUnidadeFuncional, final Date dataIngressoUnidade)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final MpmMotivoIngressoCti motivoIngressoCti = new MpmMotivoIngressoCti();

		motivoIngressoCti.setAtendimento(this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(seqAtendimento, null, null));
		motivoIngressoCti.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional));
		motivoIngressoCti.setDthrIngressoUnid(dataIngressoUnidade);

		try {
			this.getPrescricaoMedicaFacade().inserirMotivoIngressoCti(motivoIngressoCti);
			
		} catch (final InactiveModuleException e) {
			logWarn(e.getMessage());
			getObjetosOracleDAO().gerarDiagnosticoCti(seqAtendimento, seqUnidadeFuncional, dataIngressoUnidade, servidorLogado);
		}
	}

	/**
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_GERA_APACHE
	 * 
	 * @param seqAtendimento
	 * @param seqUnidadeFuncional
	 * @param dataIngressoUnidade
	 * @throws ApplicationBusinessException
	 */
	public void gerarFichaApache(final Integer seqAtendimento, final Short seqUnidadeFuncional, final Date dataIngressoUnidade)
			throws ApplicationBusinessException {

		// Gera um novo apache a cada vez que o paciente for transferido ou
		// internado direto em uma unidade de CTI, caso ainda não tenha um
		// apache EFETIVADO
		try {
			final Short seqp = this.getPrescricaoMedicaFacade().obterProximoSeqPMpmFichaApache(seqAtendimento);
	
			final MpmFichaApacheId fichaApacheId = new MpmFichaApacheId();
			fichaApacheId.setAtdSeq(seqAtendimento);
			fichaApacheId.setSeqp(seqp);
	
			final MpmFichaApache fichaApache = new MpmFichaApache();
			fichaApache.setId(fichaApacheId);
			fichaApache.setDthrRealizacao(new Date());
			fichaApache.setEnvioAviso(DominioSituacaoEnvioAvisoPrescricao.I);
			fichaApache.setSituacaoApache(DominioSituacaoApache.P);
			fichaApache.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional));
			fichaApache.setDthrIngressoUnidade(dataIngressoUnidade);

			this.getPrescricaoMedicaFacade().inserirFichaApache(fichaApache);
		}
		catch (final InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			logWarn(e.getMessage());
			getObjetosOracleDAO().gerarFichaApache(seqAtendimento, seqUnidadeFuncional, dataIngressoUnidade, servidorLogado);

		} catch (final Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00492);
		}
	}

	/**
	 * ORADB PROCEDURE AGHK_ATD_RN.RN_ATDP_ATU_PAC_PROF
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarPacientesAtendProf(final Integer atdSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			this.getPrescricaoMedicaFacade().removerAtendProf(atdSeq);
			this.getPrescricaoMedicaFacade().removerSumariosPendentes(atdSeq);
		}
		catch (final InactiveModuleException e) {
			logWarn(e.getMessage());
			getObjetosOracleDAO().atualizarPacientesAtendProf(atdSeq, servidorLogado);
		}			
	}

	/**
	 * ORADB: Procedure AGHK_ATD_RN.RN_ATDP_ATU_APACHE
	 * 
	 * @param seqAtendimento
	 * @throws ApplicationBusinessException
	 */
	public void atualizarFichaApache(final Integer seqAtendimento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		getObjetosOracleDAO().atualizarFichaApache(seqAtendimento, servidorLogado);
		//TODO Deve ser utilizado somente na V2 da prescrição
		/*
		List<MpmFichaApache> fichasApache = this.getFichaApacheDAO().pesquisarFichaApachePorAtendimentoSituacaoP(
				seqAtendimento);

		try {
			MpmFichaApache fichaApacheOld;
			for (MpmFichaApache fichaApache : fichasApache) {
				fichaApacheOld = null;
				fichaApacheOld = this.getFichaApacheJournalRN().clonarFichaApache(fichaApache);
				fichaApache.setSituacaoApache(DominioSituacaoApache.A);

				// atualiza MpmFichaApache
				this.getPrescricaoMedicaFacade().atualizarFichaApache(fichaApache, fichaApacheOld);
			}
		} catch (Exception e) {
			logError(e);
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00478);
		}
		*/
	}

	/**
	 * ORADB: Procedure AGHK_ATD_RN.RN_ATDP_ATU_AEL
	 * 
	 * @param seqInternacao
	 * @param seqUnidadeFuncional
	 * @throws ApplicationBusinessException
	 */
	public void atualizarUnidadeSolicitanteExame(final Integer seqInternacao, final Short seqUnidadeFuncional)
			throws ApplicationBusinessException {

		// atualizar no sistemas ael a unidade solicitante dos exames
		// de um paciente quando da transferência. Só para solicitacoes
		// ('AC','EC','AX','AG','CO','CS') seus parametros são:
		// P_SITUACAO_A_COLETAR,
		// P_SITUACAO_EM_COLETA,
		// P_SITUACAO_A_EXECUTAR,
		// P_SITUACAO_AGENDADO,
		// P_SITUACAO_COLETADO,
		// P_SITUACAO_COLETADO_SOLIC

		final Set<AelSolicitacaoExames> solicitacoesExames = this.pesquisarSolicitacaoExames(seqInternacao);

		try {
			for (final AelSolicitacaoExames solicitacaoExame : solicitacoesExames) {
				solicitacaoExame.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional));

				// Atualiza alteração em AelSolicitacaoExame
				this.getExamesLaudosFacade().atualizarSolicitacaoExame(solicitacaoExame);
			}
		}
		catch (final InactiveModuleException e) {
			logWarn(e.getMessage());
		} catch (final Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00441);
		}

	}

	/**
	 * Método para aplicar restrição de trazer somente os registros de
	 * solicitação de exames com situação igual aos parâmetros a seguir:
	 * P_SITUACAO_A_COLETAR, P_SITUACAO_EM_COLETA, P_SITUACAO_A_EXECUTAR,
	 * P_SITUACAO_AGENDADO, P_SITUACAO_COLETADO, P_SITUACAO_COLETADO_SOLIC
	 * 
	 * @param seqInternacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Set<AelSolicitacaoExames> pesquisarSolicitacaoExames(final Integer seqInternacao) throws ApplicationBusinessException {
		final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentosPorInternacao(seqInternacao);

		final AghParametros param1 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		final AghParametros param2 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
		final AghParametros param3 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
		final AghParametros param4 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
		final AghParametros param5 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO);
		final AghParametros param6 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);

		final String separador = ";";

		final String parametros = separador + param1.getVlrTexto() + separador + param2.getVlrTexto() + separador
				+ param3.getVlrTexto() + separador + param4.getVlrTexto() + separador + param5.getVlrTexto()
				+ separador + param6.getVlrTexto() + separador;

		final Set<AelSolicitacaoExames> solicitacoesExames = new HashSet<AelSolicitacaoExames>();

		proximoAtendimento: for (final AghAtendimentos atendimento : atendimentos) {

			for (final AelSolicitacaoExames solicitacaoExame : atendimento.getAelSolicitacaoExames()) {

				for (final AelItemSolicitacaoExames itemSolicitacaoExame : solicitacaoExame.getItensSolicitacaoExame()) {
					if (parametros.indexOf(separador + itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo() + separador) > -1) {
						solicitacoesExames.add(solicitacaoExame);
						break proximoAtendimento;
					}
				}
			}
		}

		return solicitacoesExames;
	}

	/**
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_CA_EXME_AMB
	 * 
	 * TODO finalizar implementação
	 * @throws BaseException 
	 */
	public void cancelarExamesNaoRealizados(final Integer seqAtendimento, final DominioPacAtendimento pacienteAtendimento, String nomeMicrocomputador)
			throws BaseException {

		if (DominioPacAtendimento.O.equals(pacienteAtendimento)) {

			AghParametros parametroSituacaoCancelado;
			AghParametros parametroMocCancelaObito;
			AghParametros parametroSituacaoColetar;
			AghParametros parametroSituacaoExecutar;
			AghParametros parametroSituacaoColetadoSolic;
			AghParametros parametroSituacaoAgendado;
			AghParametros parametroSituacaoEmColeta;

			final List<String> parametros = new ArrayList<String>();

			try {
				parametroSituacaoCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
				// parametros.add(parametroSituacaoCancelado.getVlrTexto());

				parametroMocCancelaObito = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOC_CANCELA_OBITO);
				// parametros.add(parametroSituacaoCancelado.getVlrTexto());

				parametroSituacaoColetar = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
				parametros.add(parametroSituacaoColetar.getVlrTexto());

				parametroSituacaoExecutar = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
				parametros.add(parametroSituacaoExecutar.getVlrTexto());

				parametroSituacaoColetadoSolic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
				parametros.add(parametroSituacaoColetadoSolic.getVlrTexto());

				parametroSituacaoAgendado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
				parametros.add(parametroSituacaoAgendado.getVlrTexto());

				parametroSituacaoEmColeta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
				parametros.add(parametroSituacaoEmColeta.getVlrTexto());

			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AIN_00345);
			}

			if (parametroMocCancelaObito != null) {
				final ISolicitacaoExameFacade solicitacaoExameFacade = this.getSolicitacaoExameFacade();

				final List<AelItemSolicitacaoExames> aelItemSolicitacaoExames = solicitacaoExameFacade.buscarItensSolicitacaoExamePorAtendimentoParamentro(
						seqAtendimento, parametros);
				if (aelItemSolicitacaoExames != null && !aelItemSolicitacaoExames.isEmpty()) {
					final IExamesFacade examesFacade = this.getExamesFacade();

					final AelSitItemSolicitacoes situacao = examesFacade.pesquisaSituacaoItemExame(parametroSituacaoCancelado.getVlrTexto());
					final AelMotivoCancelaExames aelMotivoCancelaExames = examesFacade.obterMotivoCancelamentoPeloId(parametroMocCancelaObito.getVlrNumerico()
							.shortValue());

					for (final AelItemSolicitacaoExames aelItemSolicitacaoExamesAtualizar : aelItemSolicitacaoExames) {
						aelItemSolicitacaoExamesAtualizar.setSituacaoItemSolicitacao(situacao);
						aelItemSolicitacaoExamesAtualizar.setAelMotivoCancelaExames(aelMotivoCancelaExames);
						solicitacaoExameFacade.atualizar(aelItemSolicitacaoExamesAtualizar, nomeMicrocomputador, null);
					}
				}
			}
		}
	}

	/**
	 * Método para inserir extrato leito quando alterado leito no atendimento
	 * consulta
	 * 
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_ATU_EXT_LTO
	 * 
	 * @param leitoAntigo
	 * @param leitoNovo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirExtratoLeito(final String leitoAntigo, final String leitoNovo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		
		AinLeitos leito = null;

		if (leitoAntigo == null && leitoNovo != null) {
			leito = this.getInternacaoFacade().obterAinLeitosPorChavePrimaria(leitoNovo);

			internacaoFacade.insereExtratoLeito(leito, servidorLogado, servidorLogado, new Date(), new Date(), null, true);

		} else if (leitoAntigo != null && leitoNovo == null) {
			internacaoFacade.registraExtratoLeito(leitoAntigo, null, new Date(), new Date(),
					DominioTransacaoAltaPaciente.PROCESSA_ALTA);

		} else if (leitoAntigo != null && leitoNovo != null) {
			leito = this.getInternacaoFacade().obterAinLeitosPorChavePrimaria(leitoNovo);

			internacaoFacade.insereExtratoLeito(leito, servidorLogado, servidorLogado, new Date(), new Date(), null, true);
			internacaoFacade.registraExtratoLeito(leitoAntigo, null, new Date(), new Date(),
					DominioTransacaoAltaPaciente.PROCESSA_ALTA);
		}
	}

	/**
	 * Método para descobrir os atendimentos com tratamento terapêutico
	 * 
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_GET_ATD_TRP
	 * 
	 * @param tipoOperacao
	 * @param codigoPacienteNovo
	 * @param oldOrigem
	 * @param newOrigem
	 * @param dataInicioNova
	 * @param dataFimAntiga
	 * @param dataFimNova
	 * @param unfSeqAntiga
	 * @param unfSeqNova
	 *  
	 */
	public void pesquisarAtendimentosTratamentoTerapeutico(final String tipoOperacao, final Integer codigoPacienteNovo,
			final DominioOrigemAtendimento oldOrigem, final DominioOrigemAtendimento newOrigem, final Date dataInicioNova,
			final Date dataFimAntiga, final Date dataFimNova, final Short unfSeqAntiga, final Short unfSeqNova) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentoPacienteTipoAtendimento(
				codigoPacienteNovo);
		
		for (final AghAtendimentos atendimento : atendimentos) {
			// TODO implementar AGHK_ATD_RN.rn_atdp_atu_agen_trp e chamá-la daqui
			this.getObjetosOracleDAO().atualizaAgendaControleDispensacaoAtendimento(atendimento.getSeq(), tipoOperacao, codigoPacienteNovo,
					oldOrigem, newOrigem, dataInicioNova, dataFimAntiga, dataFimNova, unfSeqAntiga, unfSeqNova, servidorLogado);
		}
	}

	/**
	 * ORADB Procedure AGHK_ATD_RN.RN_ATDP_VER_ATD_DUPL
	 * 
	 * @param codigoPaciente
	 * @param tipoTratamento
	 * @param dataInicio
	 * @param dataFim
	 * @throws ApplicationBusinessException
	 */
	public void verificarPacienteTipoTratamento(final Integer codigoPaciente,
			final DominioTipoTratamentoAtendimento tipoTratamento, final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {

		final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarTipoAtendimento(codigoPaciente,
				tipoTratamento, dataInicio, dataFim);

		if (atendimentos != null && !atendimentos.isEmpty()) {

			for (final AghAtendimentos atendimento : atendimentos) {

				// Aplica restrição a query (truncado os segundos para as
				// comparações). A restrição feita abaixo corresponde a:
				//
				// (dthr_inicio > c_dthr_inicio AND (c_dthr_fim IS NULL OR
				// (c_dthr_fim IS NOT NULL AND c_dthr_fim > dthr_inicio)))
				// OR
				// (dthr_inicio <= c_dthr_inicio AND (dthr_fim IS NULL OR
				// (dthr_fim IS NOT NULL AND dthr_fim > c_dthr_inicio)))

				if ((DateUtils.truncate(atendimento.getDthrInicio(), Calendar.MINUTE).compareTo(
						DateUtils.truncate(dataInicio, Calendar.MINUTE)) > 0 && (dataFim == null || (dataFim != null && DateUtils
						.truncate(dataFim, Calendar.MINUTE).compareTo(
								DateUtils.truncate(atendimento.getDthrInicio(), Calendar.MINUTE)) > 0)))
						|| (DateValidator.validaDataMenorIgual(DateUtils.truncate(atendimento.getDthrInicio(),
								Calendar.MINUTE), DateUtils.truncate(dataInicio, Calendar.MINUTE)) && (atendimento
								.getDthrFim() == null || (atendimento.getDthrFim() != null && DateUtils.truncate(
								atendimento.getDthrFim(), Calendar.MINUTE).compareTo(
								DateUtils.truncate(dataInicio, Calendar.MINUTE)) > 0)))) {

					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00524);
				}
			}
		}
	}

	/**
	 * Método para verificar se servidor tem qualificação para acessar registros
	 * no AGHU.
	 * 
	 * ORADB Procedure CSEC_VER_ACAO_QUA_MA
	 * 
	 * @param descricao
	 * @param login
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarAcaoQualificacaoMatricula(final String descricao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/*
		 * Esta função verifica se um usuário do AGHU está
		 * associado a uma ação a qual o habilita a executá-la.
		 * 
		 * Os parâmetros são: descrição = descrição da ação (obrigatório); +
		 * login = login do servidor (obrigatório) no sistema AGHU
		 * 
		 * Se existir qualificação associada à ação, o servidor deve possuir
		 * esta qualificação. Se a ação exigir conselho profissional, a
		 * qualificação do servidor deve possuir numero de registro de conselho
		 * profissional.
		 * 
		 * Ela substitui as funções: + CSEC_VER_ACAO (DESCRICAO, VINCULO,
		 * MATRICULA) + CSEC_VER_ACAO_PERFIL (DESCRICAO) + CSEC_VER_ACAO_QUALIF
		 * (DESCRICAO) + A antiga CSEC_VER_ACAO_QUA_MA (DESCRICAO, VINCULO,
		 * MATRICULA)
		 */
		
		/**
		 * FIXME Avaliar a possibilidade de substituir o uso parâmetro AghuParametrosEnum.P_ACAO_ADMIN_AGH
		 * pela criação de uma permissão no CASCA
		 * 
		 * Vicente 20/08/2011
		 */
		AghParametros parametro = null;
		parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ACAO_ADMIN_AGH);

		if (this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), parametro.getVlrTexto())
				&& this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), descricao)) {
			return Boolean.TRUE;
		} 
		
		return Boolean.FALSE;
	}
	
	
	/**
	 * Método para realizar busca implementada no cursor c_serv_esp da procedure
	 * RN_ATDP_ATU_LIST_PAC
	 * 
	 * @param seqEspecialidade
	 * @param dataCriacao
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private List<MpmListaServEspecialidade> pesquisarListaServidorEspecialidade(final Short seqEspecialidade, final Date dataFim)
			throws ApplicationBusinessException {

		final List<MpmListaServEspecialidade> lista = this.getPrescricaoMedicaFacade()
				.pesquisarListaServidorEspecialidadePorEspecialiade(seqEspecialidade, dataFim);

		final List<MpmListaServEspecialidade> retorno = new ArrayList<MpmListaServEspecialidade>();
		for (final MpmListaServEspecialidade item : lista) {
			if (this.getPrescricaoMedicaFacade().verificarServidorMedico(
					item.getServidor().getId().getMatricula(), 
					item.getServidor().getId().getVinCodigo())) {
				retorno.add(item);
			}
		}

		return retorno;
	}

	/**
	 * Método para realizar busca implementada no cursor c_serv_eqp da procedure
	 * RN_ATDP_ATU_LIST_PAC
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @param dataFim
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private List<MpmListaServEquipe> pesquisarListaServidorEquipe(final Integer matricula, final Short vinCodigo, final Date dataFim)
			throws ApplicationBusinessException {

		final List<MpmListaServEquipe> lista = this.getPrescricaoMedicaFacade().pesquisarListaServidorEquipePorServidor(
				matricula, vinCodigo, dataFim);

		final List<MpmListaServEquipe> retorno = new ArrayList<MpmListaServEquipe>();
		for (final MpmListaServEquipe item : lista) {
			if (this.getPrescricaoMedicaFacade().verificarServidorMedico(
					item.getServidor().getId().getMatricula(), 
					item.getServidor().getId().getVinCodigo())) {
				retorno.add(item);
			}
		}

		return retorno;
	}

	/**
	 * * Método para realizar busca implementada no cursor c_serv_unf da
	 * procedure RN_ATDP_ATU_LIST_PAC
	 * 
	 * @param seqUnidadeFuncional
	 * @param dataFim
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private List<MpmServidorUnidFuncional> pesquisarServidorUnidadeFuncional(final Short seqUnidadeFuncional, final Date dataFim)
			throws ApplicationBusinessException {

		final List<MpmServidorUnidFuncional> lista = this.getPrescricaoMedicaFacade().pesquisarServidorUnidadeFuncional(
				seqUnidadeFuncional, dataFim);

		final List<MpmServidorUnidFuncional> retorno = new ArrayList<MpmServidorUnidFuncional>();
		for (final MpmServidorUnidFuncional item : lista) {
			if (this.getPrescricaoMedicaFacade().verificarServidorMedico(
					item.getServidor().getId().getMatricula(), 
					item.getServidor().getId().getVinCodigo())) {
				retorno.add(item);
			}
		}

		return retorno;
	}

	/**
	 * Método para realizar busca implementada no cursor c_serv_atd da procedure
	 * RN_ATDP_ATU_LIST_PAC
	 * 
	 * @param seqAtendimento
	 * @param dataFim
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private List<MpmPacAtendProfissional> pesquisarPacienteAtendimentoProfissional(final Integer seqAtendimento, final Date dataFim)
			throws ApplicationBusinessException {

		final List<MpmPacAtendProfissional> lista = this.getPrescricaoMedicaFacade()
				.pesquisarPacienteAtendimentoProfissional(seqAtendimento, dataFim);

		final List<MpmPacAtendProfissional> retorno = new ArrayList<MpmPacAtendProfissional>();
		for (final MpmPacAtendProfissional item : lista) {
			if (this.getPrescricaoMedicaFacade().verificarServidorMedico(
					item.getServidor().getId().getMatricula(), 
					item.getServidor().getId().getVinCodigo())) {
				retorno.add(item);
			}
		}

		return retorno;
	}

	/**
	 * @ORADB Procedure AGHK_ATD_RN.RN_ATDP_ATU_LIST_PAC
	 * 
	 * @param origem
	 * @param dataFim
	 * @param seqAtendimento
	 * @param seqUnidadeFuncional
	 * @param seqEspecialidade
	 * @param matricula
	 * @param vinCodigo
	 * @param situacaoSumarioAlta
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarListaPacientes(final DominioOrigemAtendimento origem, final Date dataFim, final Integer seqAtendimento,
			final Short seqUnidadeFuncional, final Short seqEspecialidade, final Integer matricula, final Short vinCodigo,
			final DominioSituacaoSumarioAlta situacaoSumarioAlta) throws ApplicationBusinessException {

		if (DominioOrigemAtendimento.I.equals(origem) || DominioOrigemAtendimento.U.equals(origem)
				|| DominioOrigemAtendimento.H.equals(origem) || DominioOrigemAtendimento.N.equals(origem)) {

			if (dataFim != null) {
				if (DominioSituacaoSumarioAlta.P.equals(situacaoSumarioAlta)) {
					this.inserirSumariosPendentesServidor(seqAtendimento, matricula, vinCodigo, seqEspecialidade,
							dataFim, seqUnidadeFuncional);
				}
			} else {
				this.excluirSumariosPendentesServidor(seqAtendimento);
			}
		}
	}

	/**
	 * @ORADB PACKAGE AGHK_ATD_RN PROCEDURE RN_ATDP_ATU_LIST_PAC SUBPROCEDURE INSERE_SERV_SUMARIOS_PENDENTES
	 * 
	 * @param seqAtendimento
	 * @param matricula
	 * @param vinCodigo
	 * @param seqEspecialidade
	 * @param dataFim
	 * @param seqUnidadeFuncional
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void inserirSumariosPendentesServidor(final Integer seqAtendimento, final Integer matricula, final Short vinCodigo,
			final Short seqEspecialidade, final Date dataFim, final Short seqUnidadeFuncional) throws ApplicationBusinessException {
		
		final ConvenioExamesLaudosVO vo = this.buscarConvenioExamesLaudos(seqAtendimento);

		final AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoPeloSeq(seqAtendimento);

		if (vo != null) {
			FatConvenioSaude convenioSaude = this.getFaturamentoFacade().obterFatConvenioSaudePorId(
					vo.getCodigoConvenioSaude());

			// Aplica restricao da query referente a "grupo_convenio <> 'S'"
			if (convenioSaude != null && DominioGrupoConvenio.S.equals(convenioSaude.getGrupoConvenio())) {
				convenioSaude = null;
			}

			if (convenioSaude != null) {
				try {
					final RapServidoresId idServidor = new RapServidoresId(matricula, vinCodigo);
					final RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(idServidor);
					//this.getMpmListaServSumrAltaDAO().inserirListaServidorSumarioAlta(servidor, atendimento, null);
					
					this.verificarRegistroDuplicadoInsercao(
							servidor.getServidor().getId().getMatricula(),
							servidor.getServidor().getId().getVinCodigo(), 
							atendimento.getSeq(), null);
				} catch (final InactiveModuleException e) {
					logError(MSG_MODULO_INATIVO, e);
					throw e;
				} catch (final Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00577);
				}
			}
		}

//		c_serv_esp
		final List<MpmListaServEspecialidade> listaServidorEspecialidade = this.pesquisarListaServidorEspecialidade(
				seqEspecialidade, dataFim);
		for (final MpmListaServEspecialidade servidorEspecialidade : listaServidorEspecialidade) {
			try {
				this.verificarRegistroDuplicadoInsercao(
						servidorEspecialidade.getServidor().getId().getMatricula(),
						servidorEspecialidade.getServidor().getId().getVinCodigo(), 
						atendimento.getSeq(), null);
			} catch (final InactiveModuleException e) {
				logError(MSG_MODULO_INATIVO, e);
				throw e;
			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00573);
			}
		}

//		c_serv_eqp
		final List<MpmListaServEquipe> listaServidorEquipe = this.pesquisarListaServidorEquipe(matricula, vinCodigo, dataFim);
		for (final MpmListaServEquipe servidorEquipe : listaServidorEquipe) {
			try {
				this.verificarRegistroDuplicadoInsercao(
						servidorEquipe.getServidor().getId().getMatricula(),
						servidorEquipe.getServidor().getId().getVinCodigo(), 
						atendimento.getSeq(), null);
			} catch (final InactiveModuleException e) {
				logError(MSG_MODULO_INATIVO, e);
				throw e;
			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00574);
			}
		}

		//c_serv_unf
		final List<MpmServidorUnidFuncional> listaServidorUnidadeFuncional = this.pesquisarServidorUnidadeFuncional(
				seqUnidadeFuncional, dataFim);
		for (final MpmServidorUnidFuncional servidorUnidadeFuncional : listaServidorUnidadeFuncional) {
			try {
				this.verificarRegistroDuplicadoInsercao(
						servidorUnidadeFuncional.getServidor().getId().getMatricula(),
						servidorUnidadeFuncional.getServidor().getId().getVinCodigo(), 
						atendimento.getSeq(), null);
			} catch (final InactiveModuleException e) {
				logError(MSG_MODULO_INATIVO, e);
				throw e;
			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00575);
			}
		}

		//c_serv_atd
		final List<MpmPacAtendProfissional> atendimentosProfissionais = this.pesquisarPacienteAtendimentoProfissional(
				seqAtendimento, dataFim);
		for (final MpmPacAtendProfissional pacienteAtendimentoProfissional : atendimentosProfissionais) {
			try {
				this.verificarRegistroDuplicadoInsercao(
						pacienteAtendimentoProfissional.getServidor().getId().getMatricula(),
						pacienteAtendimentoProfissional.getServidor().getId().getVinCodigo(), 
						atendimento.getSeq(), null);
			} catch (final InactiveModuleException e) {
				logError(MSG_MODULO_INATIVO, e);
				throw e;
			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00576);
			}
		}
		
		// Funcionalidade adicionada segundo orientação do Vacaro: #12944 
		// MPM_LISTA_SERV_RESPONSAVEIS
		final List<MpmListaServResponsavel> listaServidorResponsavel = this.pesquisarServidorResponsavel(getRegistroColaboradorFacade().obterServidor(vinCodigo, matricula), dataFim);
		for (final MpmListaServResponsavel pacienteAtendimentoProfissional : listaServidorResponsavel) {
			try {
				this.verificarRegistroDuplicadoInsercao(pacienteAtendimentoProfissional.getServidor().getId().getMatricula(), pacienteAtendimentoProfissional
						.getServidor().getId().getVinCodigo(), atendimento.getSeq(), null);
			} catch (final InactiveModuleException e) {
				logError(MSG_MODULO_INATIVO, e);
				throw e;
			} catch (final Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00576);
			}
		}
	}
	
	private List<MpmListaServResponsavel> pesquisarServidorResponsavel(final RapServidores servidorResp, final Date dataFim) throws ApplicationBusinessException {
		final List<MpmListaServResponsavel> lista = this.getPrescricaoMedicaFacade().pesquisarServidorResponsavel(servidorResp, dataFim);

		final List<MpmListaServResponsavel> retorno = new ArrayList<MpmListaServResponsavel>();
		for (final MpmListaServResponsavel item : lista) {
			if (this.getPrescricaoMedicaFacade().verificarServidorMedico(item.getServidor().getId().getMatricula(), item.getServidor().getId().getVinCodigo())) {
				retorno.add(item);
			}
		}

		return retorno;
	}

	private void verificarRegistroDuplicadoInsercao(final Integer matricula, final Short vinCodigo, final Integer seqAtendimento, final Date criadoEm) {
		final MpmListaServSumrAltaId id = new MpmListaServSumrAltaId();
		id.setAtdSeq(seqAtendimento);
		id.setSerMatricula(matricula);
		id.setSerVinCodigo(vinCodigo);
		
		// Verifica se já existe um objeto com o ID no banco de dados. Caso não exista, insere o mesmo.
		// Esse tratamento é análogo a exception 'dup_val_on_index' capturada no Oracle
		if (this.getPrescricaoMedicaFacade().obterMpmListaServSumrAltaPorChavePrimaria(id) == null) {
			this.getPrescricaoMedicaFacade().inserirListaServidorSumarioAlta(id);
		}
		
	}

	/**
	 * Método para remover os objetos de lista sumário alta através do SEQ do
	 * atendimento
	 * 
	 * @param seqAtendimento
	 * @throws ApplicationBusinessException
	 */
	private void excluirSumariosPendentesServidor(final Integer seqAtendimento) throws ApplicationBusinessException {
		this.getPrescricaoMedicaFacade().removerListaServidorSumarioAltaPorAtendimento(seqAtendimento);
	}

	/**
	 * ORADB Procedure AELK_AEL_RN.RN_AELP_BUSCA_CONV
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenioSaudePlano
	 * @param codigoConvenio
	 * @param descricaoConvenio
	 * @return
	 */
	public ConvenioExamesLaudosVO buscarConvenioExamesLaudos(final Integer seqAtendimentoParam) {
		ConvenioExamesLaudosVO vo = null;
		
		final AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoPeloSeq(seqAtendimentoParam);
		
		FatConvenioSaudePlano csp = null;
		if (atendimento != null) {
			csp = atendimento.getConvenioSaudePlano();
			
			if (csp != null) {
				vo = new ConvenioExamesLaudosVO();
				vo.setCodigoConvenioSaude(csp.getId().getCnvCodigo());
				vo.setCodigoConvenioSaudePlano(csp.getId().getSeq());
				vo.setDescricaoConvenio(csp.getConvenioSaude().getDescricao());
				
			} else if (DominioOrigemAtendimento.N.equals(atendimento.getOrigem())) {
				vo = new ConvenioExamesLaudosVO(); // ConvenioPlanoSaude fake: PLANO AMBULATORIO
				vo.setCodigoConvenioSaude((short)1);
				vo.setCodigoConvenioSaudePlano((byte)2);
				
			} else if (DominioOrigemAtendimento.A.equals(atendimento.getOrigem()) 
					|| DominioOrigemAtendimento.C.equals(atendimento.getOrigem())) {
				// Foi colocado provisóriamente até o aac for implantado
				// Foi colocado provisóriamente até o mbc for implantado
				vo = new ConvenioExamesLaudosVO();
				vo.setCodigoConvenioSaude((short)1);
				vo.setCodigoConvenioSaudePlano((byte)2);
				return vo;
			}
			if (DominioOrigemAtendimento.C.equals(atendimento.getOrigem())
					&& csp != null 
					&& csp.getId().getCnvCodigo().intValue() == 1
					&& csp.getId().getSeq().intValue() == 1	){
				vo.setCodigoConvenioSaudePlano((byte) 2);
			}
		}
		
		return vo;
	}

	/**
	 * Verifica se a origem do atendimento foi alterada para I (internação) ou U
	 * (urgência), inicializar situação do sumário de alta com P (pendente).
	 * 
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_VER_SUMARIO
	 * 
	 * @param newOrigem
	 * @param newIndSitSumAlta
	 * @return
	 */
	private DominioSituacaoSumarioAlta verificarSumario(final DominioOrigemAtendimento newOrigem,
			final DominioSituacaoSumarioAlta newIndSitSumAlta) {
		if (newOrigem != null
				&& (newOrigem.equals(DominioOrigemAtendimento.I) || newOrigem.equals(DominioOrigemAtendimento.U)
						|| newOrigem.equals(DominioOrigemAtendimento.H) || newOrigem.equals(DominioOrigemAtendimento.N))) {
			return DominioSituacaoSumarioAlta.P;
		}

		return newIndSitSumAlta;
	}

	/**
	 * Se pac_codigo do antendimento é alterado atualiza tabela mam_diagnostico.
	 * 
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_ATU_PAC_COD
	 * 
	 * @param oldPacCodigo
	 * @param newPacCodigo
	 * @param newOrigem
	 * @param newAtdSeq
	 * @throws BaseException 
	 */
	public void atualizarDiagnostico(final Integer oldPacCodigo, final Integer newPacCodigo, final DominioOrigemAtendimento newOrigem,
			final Integer newAtdSeq) throws ApplicationBusinessException {
		try {
			if (newPacCodigo != oldPacCodigo) {
				if (newOrigem.equals(DominioOrigemAtendimento.I) || newOrigem.equals(DominioOrigemAtendimento.U)
						|| newOrigem.equals(DominioOrigemAtendimento.N)) {
	
					//A chamada deve ser via fachada pq é outro módulo!!!
					//MamDiagnosticoDAO mamDiagnosticoDAO =  this.getMamDiagnosticoDAO();
					final IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
					
					final List<Integer> seqs = this.getPrescricaoMedicaFacade().listarSeqsCidAtendimentoPorCodigoAtendimento(newAtdSeq);
					
					for (final Integer ciaSeq : seqs) {
						final List<MamDiagnostico> diagnosticos = ambulatorioFacade.listarDiagnosticosPorCidAtendimentoSeq(ciaSeq);
						
						for(final MamDiagnostico diag : diagnosticos) {
							// Atualizando MamDiagnostico
							if (diag != null) {
								final MamDiagnostico diagnosticoOld = ambulatorioFacade.obterDiagnosticoPorChavePrimaria(diag.getSeq());
								diag.setPaciente(this.getAipPacientesDAO().obterPorChavePrimaria(newPacCodigo));
								
								//mamDiagnosticoDAO.atualizar(diag, true);
								ambulatorioFacade.atualizarDiagnostico(diag, diagnosticoOld);
								this.flush();
							}
						}
					}
				}
			}
		}
		catch (final InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().executaAtualizarDiagnostico(oldPacCodigo, newPacCodigo, newOrigem, newAtdSeq, servidorLogado);
			
			logWarn(e.getMessage());
		} catch (final BaseException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.MSG_ERRO_ATUALIZAR_MAM_DIAGNOSTICO);
		}
	}

	/**
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_VER_ALTERA
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void verificarAlteracaoAtendimento() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00187);
	}

	/**
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_VER_DT_INI
	 * 
	 * @param newAtdSeq
	 * @param newDtHrInicio
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataInicioAtendimento(final Integer newAtdSeq, final Date newDtHrInicio) throws ApplicationBusinessException {
		Date vDthrInicio = null;

		List<Date> l = getPrescricaoMedicaFacade().executarCursorPrCr(newAtdSeq);

		if (l != null && !l.isEmpty()) {
			vDthrInicio = l.get(0);

			if (newDtHrInicio != null && vDthrInicio.before(newDtHrInicio)) {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00662, vDthrInicio);
			}
		}

		l = getPrescricaoEnfermagemFacade().executarCursorEpe(newAtdSeq);

		if (l != null && !l.isEmpty()) {
			vDthrInicio = l.get(0);

			if (newDtHrInicio != null && vDthrInicio.before(newDtHrInicio)) {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00663, vDthrInicio);
			}
		}

		l = getNutricaoFacade().executarCursorHaa(newAtdSeq);

		if (l != null && !l.isEmpty()) {
			vDthrInicio = l.get(0);

			if (newDtHrInicio != null && vDthrInicio.before(newDtHrInicio)) {
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00664, vDthrInicio);
			}
		}
	}

	/**
	 * Atualiza a data/hora do ingresso na unidade
	 * 
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_ATU_DTHR_UNF
	 * 
	 * @param intSeq
	 * @param hodSeq
	 * @param atuSeq
	 * @return
	 */
	private Date atualizarDtHrIngressoUnidade(final Integer intSeq, final Integer atuSeq, final Integer hodSeq) {

		List<Date> l = null;

		if (intSeq != null) {
			l = getInternacaoFacade().executarCursorInt2(intSeq);

			if (l != null && !l.isEmpty()) {
				return l.get(0);
			}
		} else if (atuSeq != null) {
			l = getInternacaoFacade().executarCursorAtu2(atuSeq);

			if (l != null && !l.isEmpty()) {
				return l.get(0);
			}
		} else if (hodSeq != null) {
			l = getInternacaoFacade().executarCursorHod2(hodSeq);

			if (l != null && !l.isEmpty()) {
				return l.get(0);
			}
		}

		return new Date();
	}

	/**
	 * Se situação do sumário de alta for alterada de P (pendente) para M
	 * (manual), verificar se o perfil do usuário tem ação de
	 * "autorizar sumario de alta manual" associado, se tiver permito alterar ou
	 * se a unidade tem caracteristica de Permite sumario alta manual.
	 * 
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_VER_PERF_SUM
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void verificarPerfilSumario(final DominioSituacaoSumarioAlta newIndSitSumAlta,
			final DominioSituacaoSumarioAlta oldIndSitSumAlta, final Short newUnfSeq, final Integer newAtdSeq,
			final DominioOrigemAtendimento newOrigem) throws ApplicationBusinessException {

		if ((oldIndSitSumAlta != null && oldIndSitSumAlta.equals(DominioSituacaoSumarioAlta.P))
				&& (newIndSitSumAlta != null && newIndSitSumAlta.equals(DominioSituacaoSumarioAlta.M))) {
			if (newOrigem.equals(DominioOrigemAtendimento.U)) {
				return;
			}
		}

		if ((oldIndSitSumAlta != null && oldIndSitSumAlta.equals(DominioSituacaoSumarioAlta.P))
				&& (newIndSitSumAlta != null && newIndSitSumAlta.equals(DominioSituacaoSumarioAlta.M))) {
			if (!this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
					ConstanteAghCaractUnidFuncionais.PERMITE_SUMARIO_ALTA_MANUAL)
					&& !newOrigem.equals(DominioOrigemAtendimento.N)) {
				if (newOrigem.equals(DominioOrigemAtendimento.U)) {
					if (!getPrescricaoMedicaFacade().executarCursorPrescricao(newAtdSeq)) {
						return;
					}
				}

				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				/**
				 * O teste abaixo equivale ao cursor:
				 * 
				 * ORADB CURSOR c_perfil
				 */
				if (!getCascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "sumarioAlta", "autorizarManual")) {
					throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00458);
				}
			}
		}
	}

	private Integer verificarSumarioCompleto(final Integer newAtdSeq) {

		Date vDthrAlta = null;
		Short vMamSeq = null;
		DominioIndConcluido vIndConcluido = null;

		Integer vSumarioCompleto = 0;

		List l = getPrescricaoMedicaFacade().executarCursorSumario(newAtdSeq);

		if (l != null && !l.isEmpty()) {
			final Object[] obj = (Object[]) l.get(0);
			vDthrAlta = (Date) obj[0];
			vMamSeq = (Short) obj[1];
		}

		if ((l == null || l.isEmpty()) || ((l != null && !l.isEmpty()) && vDthrAlta == null && vMamSeq == null)) {
			vSumarioCompleto = 0;
		} else {
			final Object[] obj = (Object[]) l.get(0);
			vDthrAlta = (Date) obj[0];
			vMamSeq = (Short) obj[1];

			vSumarioCompleto = 1;
		}

		l = getPrescricaoMedicaFacade().executarCursorAltaSumario(newAtdSeq);

		if (l != null && !l.isEmpty()) {
			vIndConcluido = (DominioIndConcluido) l.get(0);
		}

		if (!((l == null || l.isEmpty()) || ((l != null && !l.isEmpty()) && vIndConcluido.equals(DominioIndConcluido.N)))) {
			vSumarioCompleto = 2;
		}

		return vSumarioCompleto;
	}

	/**
	 * 
	 * Ao registrar a saida do paciente (dthr_fim), verificar se tem sumário de
	 * alta e atualizar situação do sumário para I (informatizado). Se não tiver
	 * sumário, verificar se a unidade permite alta sem sumário e atualizar
	 * situação para M(manual). Se o atendimento for de urgencia e o paciente
	 * deu alta por óbito ou foi feita alguma prescrição para este atendimento,
	 * atualizar situacao para P (pendente) e o controle do sumário para N (não
	 * elaborado). Ao estornar a saída do paciente, atualizar a situação do
	 * sumário novamente para P (pendente) e o controle para null.
	 * 
	 * ORADB package AGHK_ATD_RN procedure RN_ATDP_VER_FIM_SUM
	 * 
	 * @param newOrigem
	 * @param newDtHrFim
	 * @param atdSeq
	 * @param newIndSitSumAlta
	 * @param newUnfSeq
	 * @param newCtrlSumrAlta
	 * @param newAtuSeq
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void verificarPerfilSumario(final AghAtendimentos newAtendimento, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();

		String vAltaObito;
		String vAltaObito48;
		String vTamCodigo;
		
		try {
			if (newAtendimento.getOrigem().equals(DominioOrigemAtendimento.I)
					|| newAtendimento.getOrigem().equals(DominioOrigemAtendimento.U)
					|| newAtendimento.getOrigem().equals(DominioOrigemAtendimento.H)
					|| newAtendimento.getOrigem().equals(DominioOrigemAtendimento.N)) {
				if (newAtendimento.getDthrFim() != null) {
					final int res = this.verificarSumarioCompleto(newAtendimento.getSeq());
	
					if (res == 0) {
						if (this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newAtendimento
								.getUnidadeFuncional().getSeq(),
								ConstanteAghCaractUnidFuncionais.PERMITE_SUMARIO_ALTA_MANUAL)
								|| newAtendimento.getOrigem().equals(DominioOrigemAtendimento.N)) {
							newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.M);
							newAtendimento.setCtrlSumrAltaPendente(null);
						} else {
							// quando for alta sem sumário da emergência faz
							// tratamento diferenciado.
							if (newAtendimento.getOrigem().equals(DominioOrigemAtendimento.U)) {
								AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO;
								AghParametros parametro = this.getParametroFacade().buscarAghParametro(parametroEnum);
	
								vAltaObito = parametro.getVlrTexto();
	
								parametroEnum = AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H;
								parametro = this.getParametroFacade().buscarAghParametro(parametroEnum);
	
								vAltaObito48 = parametro.getVlrTexto();
	
								final List l = getInternacaoFacade().executarCursorUrgencia(newAtendimento.getAtendimentoUrgencia().getSeq());
	
								if (l != null && !l.isEmpty()) {
									vTamCodigo = (String) l.get(0);
	
									if (vTamCodigo.equalsIgnoreCase(vAltaObito)
											|| vTamCodigo.equalsIgnoreCase(vAltaObito48)) {
										newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
										newAtendimento.setCtrlSumrAltaPendente(DominioControleSumarioAltaPendente.N);
									} else {
										newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.M);
										newAtendimento.setCtrlSumrAltaPendente(null);
									}
								} else {
									throw new ApplicationBusinessException(AtendimentosRNExceptionCode.AGH_00465);
								}
							} else {
								newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
								newAtendimento.setCtrlSumrAltaPendente(DominioControleSumarioAltaPendente.N);
							}
						}
					} else {
						newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.I);
						newAtendimento.setCtrlSumrAltaPendente(null);
					}
				} else {
					if (DominioSituacaoSumarioAlta.M.equals(newAtendimento.getIndSitSumarioAlta())) {
						newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
						newAtendimento.setCtrlSumrAltaPendente(null);
						
						final MpmSumarioAlta sumAlta = prescricaoMedicaFacade.obterMpmSumarioAltaPorChavePrimaria(newAtendimento.getSeq());
	
						if (sumAlta != null) {
							sumAlta.setComplMotivoAlta(null);
							sumAlta.setDthrSumarioPosAlta(null);
							sumAlta.setServidorPosAlta(null);
							
							//mpmSumarioAltaDAO.atualizar(sumAlta, true);
							prescricaoMedicaFacade.atualizarSumarioAlta(sumAlta, nomeMicrocomputador, dataFimVinculoServidor);
							this.flush();
							
						}
	
						final List<MpmAltaSumario> altasSumario = prescricaoMedicaFacade.listarAltasSumario(newAtendimento.getSeq(),
								DominioSituacao.A, new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT,
										DominioIndTipoAltaSumarios.OBT });
						for (final MpmAltaSumario item : altasSumario) {
							item.setPermiteManuscrito(null);
							item.setDthrSumarioPosAlta(null);
							item.setServidorPosAlta(null);
	
							//mpmAltaSumarioDAO.atualizar(item, true);
							prescricaoMedicaFacade.atualizarSumarioAlta(sumAlta, nomeMicrocomputador, dataFimVinculoServidor);
							this.flush();
						}
	
					} else if (DominioSituacaoSumarioAlta.I.equals(newAtendimento.getIndSitSumarioAlta())) {
						newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
						newAtendimento.setCtrlSumrAltaPendente(null);
	
						final MpmSumarioAlta sumAlta = prescricaoMedicaFacade.obterMpmSumarioAltaPorChavePrimaria(newAtendimento.getSeq());
	
						if (sumAlta != null) {
							sumAlta.setDthrSumarioPosAlta(null);
							sumAlta.setServidorPosAlta(null);
							
							//mpmSumarioAltaDAO.atualizar(sumAlta, true);
							prescricaoMedicaFacade.atualizarSumarioAlta(sumAlta, nomeMicrocomputador, dataFimVinculoServidor);
							this.flush();
						}
	
						final List<MpmAltaSumario> altasSumario = prescricaoMedicaFacade.listarAltasSumario(newAtendimento.getSeq(),
								DominioSituacao.A, new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT,
										DominioIndTipoAltaSumarios.OBT });
						for (final MpmAltaSumario item : altasSumario) {
							item.setDthrSumarioPosAlta(null);
							item.setServidorPosAlta(null);
	
							//mpmAltaSumarioDAO.atualizar(item, true);
							prescricaoMedicaFacade.atualizarAltaSumario(item, nomeMicrocomputador);
							this.flush();
						}
					} else {
						newAtendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
						newAtendimento.setCtrlSumrAltaPendente(null);
					}
				}
			}
		}
		catch (final InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().executaVerificarPerfilSumario(newAtendimento.getOrigem().toString(), 
					newAtendimento.getDthrFim(), 
					newAtendimento.getSeq(), 
					newAtendimento.getIndSitSumarioAlta() != null ? newAtendimento.getIndSitSumarioAlta().toString() : null , 
					newAtendimento.getUnidadeFuncional().getSeq(), 
					newAtendimento.getCtrlSumrAltaPendente() != null ? newAtendimento.getCtrlSumrAltaPendente().toString() : null,
					newAtendimento.getAtendimentoUrgencia() != null ? newAtendimento.getAtendimentoUrgencia().getSeq() : null, 
					nomeMicrocomputador, 
					servidorLogado);
			
			logWarn(e.getMessage());		
			
		} catch (final Exception e) { //essa não seria a forma ideal de tratar a exceção, contudo não tenho como propagar a exceção no momento, pois causou mais que 50 erros após colocar a chamada adequada as triggers
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.MSG_ERRO_ATUALIZAR_AGH_ATENDIMENTOS);
		}
	}

	/**
	 * ORADB TRIGGER AGH.AGHT_ATD_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela AGH_ATENDIMENTOS
	 * 
	 * @param newAtendimento
	 * @param oldAtendimento
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void executarAghtAtdBru(final AghAtendimentos newAtendimento, final AghAtendimentos oldAtendimento, String nomeMicrocomputador, 
			RapServidores servidorLogado, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		// Verifica se data fim não é maior que data inicio
		this.verificarDataInicioDataFimAtendimento(newAtendimento.getDthrInicio(), newAtendimento.getDthrFim());

		// Para atendimentos de radioterapia a data de início não pode ser menor
		// que a data de nascimento do paciente
		if (DominioTipoTratamentoAtendimento.VALOR_28.equals(newAtendimento.getIndTipoTratamento())) {
			if (CoreUtil.modificados(oldAtendimento.getDthrInicio(), newAtendimento.getDthrInicio())
					|| CoreUtil.modificados(oldAtendimento.getPaciente().getCodigo(), newAtendimento.getPaciente()
							.getCodigo())) {
				this.verificarDataNascimentoAtendimento(newAtendimento.getPaciente().getCodigo(), newAtendimento
						.getDthrInicio());
			}
		}

		// critica data de inicio do atendimento, não pode ser menor que data
		// hora de prescrição.
		if (!DateUtil.isDatasIguais(oldAtendimento.getDthrInicio(),
				newAtendimento.getDthrInicio())) {
			switch (newAtendimento.getOrigem()) {
			case I:
			case U:
			case N:
				this.verificarDataInicioAtendimento(newAtendimento.getSeq(), newAtendimento.getDthrInicio());
				break;
			default:
				break;
			}
		}

		// atualiza ind_pac_atendimento
		if (!DateUtil.isDatasIguais(oldAtendimento.getDthrFim(), newAtendimento.getDthrFim())) {
			newAtendimento.setIndPacAtendimento(this.verificaFimAtendimento(newAtendimento.getDthrFim()));
		}

		if ( (CoreUtil.modificados(oldAtendimento.getIndPacPediatrico(), newAtendimento.getIndPacPediatrico())
				|| CoreUtil.modificados(oldAtendimento.getIndPacPrematuro(), newAtendimento.getIndPacPrematuro())
				|| CoreUtil.modificados(oldAtendimento.getTipoLaminaLaringo(), newAtendimento.getTipoLaminaLaringo()))
			  && newAtendimento.getIndPacAtendimento() == DominioPacAtendimento.N) {
			this.verificarAlteracaoAtendimento();
		}

		if (CoreUtil.modificados(oldAtendimento.getUnidadeFuncional().getSeq(), newAtendimento.getUnidadeFuncional()
				.getSeq())) {
			if ((newAtendimento.getInternacao() != null && newAtendimento.getInternacao().getSeq() != null)
					|| (newAtendimento.getAtendimentoUrgencia() != null && newAtendimento.getAtendimentoUrgencia()
							.getSeq() != null)
					|| (newAtendimento.getAtendimentoPacienteExterno() != null && newAtendimento
							.getAtendimentoPacienteExterno().getSeq() != null)
					|| (newAtendimento.getHospitalDia() != null && newAtendimento.getHospitalDia().getSeq() != null)) {
				final Integer seqHospitalDia = newAtendimento.getHospitalDia() == null ? null : newAtendimento.getHospitalDia().getSeq();
				final Integer seqAtendimentoUrgencia = newAtendimento.getAtendimentoUrgencia() == null ? null : newAtendimento.getAtendimentoUrgencia().getSeq();
				final Integer seqInternacao = newAtendimento.getInternacao() == null ? null : newAtendimento.getInternacao().getSeq();
				newAtendimento.setDthrIngressoUnidade(this.atualizarDtHrIngressoUnidade(seqInternacao, seqHospitalDia, seqAtendimentoUrgencia));
			}
		}

//		newAtendimento.getServidorMovimento().getId().setVinCodigo(this.getAghuFacade().getVinculo());
//		newAtendimento.getServidorMovimento().getId().setMatricula(this.getAghuFacade().getMatricula());
		
		newAtendimento.setServidorMovimento(servidorLogado);
		
		if (newAtendimento.getServidorMovimento().getId().getMatricula() == null) {
			throw new ApplicationBusinessException(AtendimentosRNExceptionCode.RAP_00175);
		}

		/* Atualiza a origem conforme a fk do arco */
		if (newAtendimento.getInternacao() != null && newAtendimento.getInternacao().getSeq() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.I);
		} else if (newAtendimento.getAtendimentoUrgencia() != null
				&& newAtendimento.getAtendimentoUrgencia().getSeq() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.U);
		} else if (newAtendimento.getHospitalDia() != null && newAtendimento.getHospitalDia().getSeq() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.H);
		} else if (newAtendimento.getAtendimentoPacienteExterno() != null
				&& newAtendimento.getAtendimentoPacienteExterno().getSeq() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.X);
		} else if (newAtendimento.getDcaBolNumero() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.D);
		} else if (newAtendimento.getConsulta() != null && newAtendimento.getConsulta().getNumero() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.A);
		} else if (newAtendimento.getConsulta() != null && newAtendimento.getConsulta().getNumero() != null) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.A);
		} else if (!newAtendimento.getOrigem().equals(DominioOrigemAtendimento.C)
				&& !newAtendimento.getOrigem().equals(DominioOrigemAtendimento.N)) {
			newAtendimento.setOrigem(DominioOrigemAtendimento.A);
		}

		/*
		 * atualiza ind_sit_sumario_alta quando a origem do atendimento é
		 * modificada para I ou U ou H
		 */
		if (CoreUtil.modificados(oldAtendimento.getOrigem(), newAtendimento.getOrigem())) {
			newAtendimento.setIndSitSumarioAlta(this.verificarSumario(newAtendimento.getOrigem(), newAtendimento
					.getIndSitSumarioAlta()));
			
			String leitoID = newAtendimento.getLeito() != null ? newAtendimento.getLeito().getLeitoID() : null;
				
			this.controleInfeccaoFacade.enviaEmailGmr(newAtendimento.getOrigem(), newAtendimento.getPaciente()
						.getCodigo(), newAtendimento.getProntuario(), leitoID, 
						newAtendimento.getUnidadeFuncional().getSeq());
			}

		/*
		 * atualiza ind_sit_sumario_alta quando termina o atendimento, ou quando
		 * a alta é estornada se a origem for I ou U ou H
		 */
		if (!DateUtil.isDatasIguais(oldAtendimento.getDthrFim(), newAtendimento.getDthrFim())) {
			this.verificarPerfilSumario(newAtendimento, nomeMicrocomputador, dataFimVinculoServidor);
		}

		/*
		 * verifica perfil para alterar ind_sit_sumario_alta de P (pendente)
		 * para M (manual)
		 */
		if (CoreUtil.modificados(newAtendimento.getIndSitSumarioAlta(), oldAtendimento.getIndSitSumarioAlta())) {
			this.verificarPerfilSumario(newAtendimento.getIndSitSumarioAlta(), oldAtendimento.getIndSitSumarioAlta(),
					newAtendimento.getUnidadeFuncional().getSeq(), newAtendimento.getSeq(), newAtendimento.getOrigem());
		}

		/* verifica troca de especialidade do atendimento para QUIMIO */
		if (newAtendimento.getEspecialidade() != null && oldAtendimento.getEspecialidade() != null && CoreUtil
				.modificados(newAtendimento.getEspecialidade().getSeq(), oldAtendimento.getEspecialidade().getSeq())
				&& DominioTipoTratamentoAtendimento.VALOR_29.equals(newAtendimento.getIndTipoTratamento())) {
			try {
				getFaturamentoFacade().rnPmrpTrcEspAtd(newAtendimento.getSeq(), newAtendimento.getEspecialidade().getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
			}
			catch (final InactiveModuleException e) {
				getObjetosOracleDAO().executaAtualizacaoContaHospitFatAmbulatorio(newAtendimento.getSeq(), newAtendimento.getEspecialidade().getSeq(), nomeMicrocomputador, servidorLogado);
				logWarn(e.getMessage());
			} catch (final BaseException e) {
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(AtendimentosRNExceptionCode.MSG_RN_PMRP_TRC_ESP_ATD);
			}
		}

		/* atualiza mam_diagnostico quando altera pac_codigo */
		if (CoreUtil.modificados(newAtendimento.getPaciente().getCodigo(), oldAtendimento.getPaciente().getCodigo())) {
			this.atualizarDiagnostico(oldAtendimento.getPaciente().getCodigo(), newAtendimento.getPaciente()
					.getCodigo(), newAtendimento.getOrigem(), newAtendimento.getSeq());
		}

		// atualiza as informações do PIM2 (escore de gravidade da Utip) no caso
		// de alteração da unidade de atendimento do paciente (unf_seq) ou no
		// caso alta da Utip
		if (CoreUtil.modificados(oldAtendimento.getUnidadeFuncional().getSeq(), newAtendimento.getUnidadeFuncional()
				.getSeq())
				|| !DateUtil.isDatasIguais(oldAtendimento.getDthrFim(), newAtendimento.getDthrFim())) {
			this.getAtendimentoEnforceRN().atualizarInformacoesFormularioPim2("A", newAtendimento.getOrigem(),
					newAtendimento.getSeq(), oldAtendimento.getUnidadeFuncional().getSeq(), newAtendimento
							.getUnidadeFuncional().getSeq(), oldAtendimento.getDthrFim(), newAtendimento.getDthrFim(),
					newAtendimento.getDthrIngressoUnidade());
		}

	}
	
	/**
	 * ORADB Package aelk_ael_rn.rn_aelp_busca_conv nao esta exatamente igual.
	 * Mas faz a mesma regra. ORADB function AELC_GET_CONVENIO nao esta
	 * exatamente igual. Mas faz a mesma regra.
	 * 
	 * @param seq
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioSaudePlanoAtendimento(Integer seq) {
		
		CoreUtil.validaParametrosObrigatorios(seq);
		
		AghAtendimentos atendimento = this.getAghAtendimentoDAO().obterPorChavePrimaria(seq);
		
		FatConvenioSaudePlano convSaudePlano = null;
		
		if (DominioOrigemAtendimento.N == atendimento.getOrigem()) {
			// Qdo o paciente for um RN(Recém Nascido), buscar o convênio da mãe
			// para popular o campo convênio.
			//McoGestacoes atdMae = this.getMcoGestacoes();
					
			if (atendimento.getAtendimentoMae() != null) {
				
				convSaudePlano = this.obterFatConvenioSaudePlano(atendimento.getAtendimentoMae());
				
			}
			
		} else {
			
			convSaudePlano = this.obterFatConvenioSaudePlano(atendimento);
			
		}
		
		/*
		 * Pra visualizar os dados da navegacao atendimento -> gestacao ->
		 * atendimento. select atd.seq, atd.dthr_inicio, gestacao.pac_codigo,
		 * gestacao.seqp, atdo.seq gso_atd_seq, atdo.pac_codigo gso_pac_codigo
		 * from agh.agh_atendimentos atd inner join agh.mco_gestacoes gestacao
		 * on gestacao.pac_codigo = atd.gso_pac_codigo and gestacao.seqp =
		 * atd.gso_seqp left outer join agh.agh_atendimentos atdo on
		 * atdo.gso_pac_codigo = gestacao.pac_codigo AND atdo.gso_seqp =
		 * gestacao.seqp --AND pac_codigo = gso_pac_codigo; where atd.seq in
		 * (9288185, 742469, 1765986, 2062732, 2117581, 2109945, 2117634) order
		 * by dthr_inicio desc
		 */
		if (convSaudePlano != null) {
			convSaudePlano = this.faturamentoFacade.obterConvenioSaudePlanoPorChavePrimaria(convSaudePlano.getId());
		}
		
		return convSaudePlano;
	}

	private FatConvenioSaudePlano obterFatConvenioSaudePlano(AghAtendimentos atd) {
		
		FatConvenioSaudePlano convenioPlano = null;

		if (DominioOrigemAtendimento.I == atd.getOrigem()
				&& atd.getInternacao() != null) {
			convenioPlano = atd.getInternacao().getConvenioSaudePlano();
		}

		if (DominioOrigemAtendimento.U == atd.getOrigem()
				&& atd.getAtendimentoUrgencia() != null) {
			convenioPlano = atd.getAtendimentoUrgencia()
					.getConvenioSaudePlano();
		}

		if (DominioOrigemAtendimento.H == atd.getOrigem()
				&& atd.getHospitalDia() != null) {
			convenioPlano = atd.getHospitalDia().getConvenioSaudePlano();
		}

		if (DominioOrigemAtendimento.X == atd.getOrigem()
				&& atd.getAtendimentoPacienteExterno() != null) {
			convenioPlano = atd.getAtendimentoPacienteExterno()
					.getConvenioSaudePlano();
		}

		if (DominioOrigemAtendimento.A == atd.getOrigem()
				&& atd.getConsulta() != null) {
			convenioPlano = atd.getConsulta().getConvenioSaudePlano();
		}

		if (DominioOrigemAtendimento.C == atd.getOrigem()
				&& atd.getCirurgias() != null && !atd.getCirurgias().isEmpty()) {
			Iterator<MbcCirurgias> it = atd.getCirurgias().iterator();
			convenioPlano = it.next().getConvenioSaudePlano();
		}

		return convenioPlano;
	}
	
	protected ICascaFacade getICascaFacade() {
		return this.cascaFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected AtendimentosON getAtendimentosON() {
		return atendimentosON;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected PacienteON getPacienteON() {
		return pacienteON;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected AtendimentoJournalRN getAtendimentoJournalRN() {
		return atendimentoJournalRN;
	}

	protected AtendimentoEnforceRN getAtendimentoEnforceRN() {
		return atendimentoEnforceRN;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected INutricaoFacade getNutricaoFacade() {
		return nutricaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public AghAtendimentoDAO getAghAtendimentoDAO() {
		return aghAtendimentoDAO;
	}
		
}
