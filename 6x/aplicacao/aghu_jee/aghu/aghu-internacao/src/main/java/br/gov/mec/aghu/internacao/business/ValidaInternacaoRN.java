package br.gov.mec.aghu.internacao.business;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.InternacaoUtilRN.InternacaoUtilRNExceptionCode;
import br.gov.mec.aghu.internacao.business.vo.AtualizarContaInternacaoVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarPacienteTipo;
import br.gov.mec.aghu.internacao.business.vo.InternacaoAtendimentoUrgenciaPacienteVO;
import br.gov.mec.aghu.internacao.business.vo.LeitoQuartoUnidadeFuncionalVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AelProjetoIntercorrenciaInternacao;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AtualizarLocalAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Essa classe deve implementar todas procedures e functions referentes a
 * package AINK_INT_RN do banco de dados Oracle. Essa package está separada
 * nesta classe, pois é muito extensa.
 */
@SuppressWarnings({"deprecation","PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class ValidaInternacaoRN extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final String CALL_STM = "{call ";

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private InternacaoRN internacaoRN;
	
	private static final Log LOG = LogFactory.getLog(ValidaInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@Inject
	private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5072929833978483356L;

//	private static final String ENTITY_MANAGER = "entityManager";

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	public enum ValidaInternacaoRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, AIN_00846, AIN_00847, AIN_00764, AIN_00818, AIN_00851, AIN_00852, AIN_00871, AGH_00163, AIP_00013, AIN_00755, AIN_00857, AIN_00810, AIN_00858, AIN_00859, ERRO_ATUALIZAR_SOLICITACAO_EXAME, FAT_00702, AIN_00794, AIN_00660, AIN_00838, FAT_00926, AIN_00347, AIN_00391, AGH_00185, AGH_00186, AGH_00187, ERRO_CLONAR_CONTA_HOSPITALAR, ERRO_ATUALIZAR_ALTA, AIN_00756, AIN_00757, AIN_00758;
	}

	/**
	 * Método para validar o paciente da internação.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_VER_PRNT
	 * 
	 * @param codigoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarPacienteInternacao(Integer codigoPaciente) throws ApplicationBusinessException {
		AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);

		if (paciente == null) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00846);
		}

		if (paciente.getProntuario() == null) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00847);
		}
	}

	/**
	 * Método para validar se data prevista de alta é válida.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INT_VER_PREV_ALTA
	 * 
	 * @param dataAlta
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataPrevisaoAlta(Date dataAlta) throws ApplicationBusinessException {

		if (dataAlta != null) {
			Calendar dtAlta = Calendar.getInstance();
			dtAlta.setTime(dataAlta);

			Calendar dataAtual = Calendar.getInstance();
			dataAtual = DateUtils.truncate(dataAtual, Calendar.DAY_OF_MONTH);

			if (dtAlta.before(dataAtual)) {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00764);
			}
		}
	}

	/**
	 * Método para atualizar o valor do convenioSaude e convenioSaudePlano.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_PROJ
	 * 
	 * @param seqProjetoPesquisa
	 * @return
	 */
	public FatConvenioSaudePlano atualizarConvenioProjetoPesquisa(
			Integer seqProjetoPesquisa) {

		if (seqProjetoPesquisa != null) {
			AelProjetoPesquisas projetoPesquisa = this.getExamesLaudosFacade()
					.obterProjetoPesquisa(seqProjetoPesquisa);

			if (projetoPesquisa != null && projetoPesquisa.getConvenioSaudePlano() != null
					&& projetoPesquisa.getConvenioSaudePlano().getId() != null) {
				return projetoPesquisa.getConvenioSaudePlano();
			}
		}

		return null;
	}

	/**
	 * Método atualiza tabelas do módulo FAT
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_ALT_CNTA
	 * 
	 * @param internacao
	 * @param dthrAltaMedica
	 * @throws BaseException 
	 */
	public void atualizaAltaConta(final AinInternacao internacao, final Date dthrAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		try {
			FatContasInternacao contaInternacao = this.getFaturamentoFacade().obterContaHospitalarePorInternacao(internacao.getSeq());
	
			if (contaInternacao == null) {
				throw new ApplicationBusinessException(
						ValidaInternacaoRNExceptionCode.AIN_00391);
			} else {
				try {
					FatContasHospitalares contaHospitalar = contaInternacao
							.getContaHospitalar();
					FatContasHospitalares contaHospitalarOld = this
							.getFaturamentoFacade().clonarContaHospitalar(
									contaHospitalar);
					Date dataAltaMedica = null;
					if(dthrAlta != null){
						dataAltaMedica = DateUtils.truncate(dthrAlta, Calendar.SECOND);
					}
					Date dataAltaAdministrativa = null;
					if (contaHospitalar.getDtAltaAdministrativa() != null) {
						dataAltaAdministrativa = DateUtils.truncate(
								contaHospitalar.getDtAltaAdministrativa(),
								Calendar.SECOND);
					}
	
					if (!DateUtil.isDatasIguais(dataAltaMedica, dataAltaAdministrativa)) {
						if (!DominioSituacaoConta.C.equals(contaHospitalar
								.getIndSituacao())
								&& !DominioSituacaoConta.E.equals(contaHospitalar
										.getIndSituacao())) {
							contaHospitalar.setDtAltaAdministrativa(dataAltaMedica);
							this.getFaturamentoFacade().persistirContaHospitalar(
									contaHospitalar, contaHospitalarOld, true, nomeMicrocomputador, dataFimVinculoServidor);
	
							if (dataAltaMedica != null) {
								// Chamada de FATK_CTH_RN.RN_CTHP_ATU_DIAR_UTI
								this.getFaturamentoFacade().rnCthpAtuDiarUti(
										contaHospitalar.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
							}
						} else if (DominioSituacaoConta.E.equals(contaHospitalar
								.getIndSituacao())) {
							throw new ApplicationBusinessException(
									ValidaInternacaoRNExceptionCode.AIN_00391);
						}
					}
				} catch (BaseException e) {
					logError("Exceção BaseException capturada, lançada para cima.");
					throw e;
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
						throw new ApplicationBusinessException(
								ValidaInternacaoRNExceptionCode.ERRO_ATUALIZAR_ALTA);
				}
			}
		}
		catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			logWarn(e.getMessage());
			//faz chamada nativa
			getObjetosOracleDAO().executaAtualizacaoContaHospitalarAlta(internacao.getSeq(), dthrAlta, servidorLogado);
		}
	}

	/**
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_ALT_MCI
	 * 
	 * Método deve ser implementado para o módulo Controle de Infecção (MCI)
	 * 
	 * @param atendimento
	 * @param tipoAltaMedica
	 * @param dataAltaMedica
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizaAltaControleInfeccao(final AghAtendimentos atendimento,
			final AinTiposAltaMedica tipoAltaMedica, final Date dataAltaMedica)
			throws ApplicationBusinessException {

		if (!objetosOracleDAO.isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_ALT_MCI;

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					cs = connection.prepareCall(CALL_STM + nomeObjeto
											+ "(?,?,?)}");
			
					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER,
							atendimento == null ? null : atendimento.getSeq());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.VARCHAR,
							tipoAltaMedica == null ? null : tipoAltaMedica.getCodigo());
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dataAltaMedica);

					cs.execute();
				} finally {
					if(cs != null){
						cs.close();
					}
				}
			}
		};
		
		try {
			ainSolicTransfPacientesDAO.doWork(work);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(
							atendimento == null ? null : atendimento.getSeq(),
					tipoAltaMedica == null ? null : tipoAltaMedica.getCodigo(), dataAltaMedica);
			this.logError(
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true,
							valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto,
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e,
							false, valores));
		}

		if (work.getException() != null) {
			String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							atendimento == null ? null : atendimento.getSeq(),
							tipoAltaMedica == null ? null : tipoAltaMedica
									.getCodigo(), dataAltaMedica);
			this.logError(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
	}

	/**
	 * Método para verificar a situação do leito
	 * 
	 * Teste unitário deste método:
	 * InternacaoEnforceTest.testVerificaSituacaoLeito()
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_VER_SIT_LTO
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificaSituacaoLeito(AinLeitos leito) throws ApplicationBusinessException {

		Short codTipoMovLeitoDesocupado;
		Short codTipoMovLeitoAlta;
		Short codTipoMovLeitoBloqueioLimpeza;
		Short codTipoMovLeitoPertencesPaciente;

		try {
			AghParametros parametro = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
			codTipoMovLeitoDesocupado = parametro.getVlrNumerico() == null ? null : parametro
					.getVlrNumerico().shortValue();
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(InternacaoUtilRNExceptionCode.AIN_00422);
		}

		try {
			AghParametros parametro = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_ALTA);
			codTipoMovLeitoAlta = parametro.getVlrNumerico() == null ? null : parametro
					.getVlrNumerico().shortValue();
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(InternacaoUtilRNExceptionCode.AIN_00422);
		}

		try {
			AghParametros parametro = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA);
			codTipoMovLeitoBloqueioLimpeza = parametro.getVlrNumerico() == null ? null : parametro
					.getVlrNumerico().shortValue();
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(InternacaoUtilRNExceptionCode.AIN_00422);
		}

		try {
			AghParametros parametro = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_PERTENCES_PAC);
			codTipoMovLeitoPertencesPaciente = parametro.getVlrNumerico() == null ? null
					: parametro.getVlrNumerico().shortValue();
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(InternacaoUtilRNExceptionCode.AIN_00422);
		}

		Short codTipoMovLeito = leito.getTipoMovimentoLeito().getCodigo();

		if ( // IF cur_lto%notfound
		!codTipoMovLeito.equals(codTipoMovLeitoDesocupado)
				&& !codTipoMovLeito.equals(codTipoMovLeitoAlta)
				&& !codTipoMovLeito.equals(codTipoMovLeitoBloqueioLimpeza)
				&& !codTipoMovLeito.equals(codTipoMovLeitoPertencesPaciente)) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00818);
		}
	}

	/**
	 * Método para verificar documento de óbito. Se motivo de alta for óbito
	 * deve ser informado o número da declaração de óbito (documentoObito)
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INT_VER_DOC_OBITO
	 * 
	 * @param tipoAltaMedica
	 * @param documentoObito
	 * @throws ApplicationBusinessException
	 */
	public void verificarDocumentoObito(String tipoAltaMedica, Integer documentoObito)
			throws ApplicationBusinessException {

		AghParametros parametroObitoMaior24hs = null;
		AghParametros parametroObitoMenor24hs = null;

		// Busca parametro P_AGHU_ALTA_OBITO_MAIOR_24HS
		try {
			parametroObitoMaior24hs = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_ALTA_OBITO_MAIOR_24HS);
		} catch (Exception e) {
			this.logError("Erro ao buscar parametro 'P_AGHU_ALTA_OBITO_MAIOR_24HS'.", e);
		}

		// Busca parametro P_AGHU_ALTA_OBITO_MENOR_24HS
		try {
			parametroObitoMenor24hs = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_ALTA_OBITO_MENOR_24HS);
		} catch (Exception e) {
			this.logError("Erro ao buscar parametro 'P_AGHU_ALTA_OBITO_MENOR_24HS'.", e);
		}

		// Validação de documento de óbito
		if (tipoAltaMedica != null
				&& (tipoAltaMedica.equals(parametroObitoMaior24hs.getVlrTexto()) || tipoAltaMedica
						.equals(parametroObitoMenor24hs.getVlrTexto())) && documentoObito == null) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00851);
		}

		if (tipoAltaMedica != null && !tipoAltaMedica.equals(parametroObitoMaior24hs.getVlrTexto())
				&& !tipoAltaMedica.equals(parametroObitoMenor24hs.getVlrTexto())
				&& documentoObito != null) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00852);
		}
	}

	/**
	 * Método para incluir um atendimento na internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_AGH_ATD
	 * 
	 * @param codigoPaciente
	 * @param dataInicio
	 * @param seq
	 * @param dataFim
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @param seqEspecialidade
	 * @param matricula
	 * @param vinCodigo
	 * @param matriculaDigitador
	 * @param vinCodigoDigitador
	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	public LeitoQuartoUnidadeFuncionalVO incluirAtendimetnoInternacao(final AipPacientes paciente,
			final Date dataInicio, final AinInternacao internacao, final Date dataFim,
			final AinLeitos leito, final AinQuartos quarto,
			final AghUnidadesFuncionais unidadeFuncional, final AghEspecialidades especialidade,
			final RapServidores servidor, final RapServidores digitador, String nomeMicrocomputador)
			throws ApplicationBusinessException {

		// Chamada de AINK_INT_RN.RN_INTP_ATU_LOCAL
		final LeitoQuartoUnidadeFuncionalVO vo = this.atualizarLeitoQuartoUnidadeFuncional(leito,
				quarto, unidadeFuncional);
		
		try {
			// Chamada de AGHK_ATD_RN.RN_ATDP_ATU_INS_ATD
			InclusaoAtendimentoVO inclusaoAtendimentoVO = this.getPacienteFacade().inclusaoAtendimento(
					paciente == null ? null : paciente.getCodigo(), dataInicio, null,
					internacao == null ? null : internacao.getSeq(), null, dataFim,
					vo.getLeito() == null ? null : vo.getLeito().getLeitoID(),
					vo.getQuarto() == null ? null : vo.getQuarto().getNumero(),
					vo.getUnidadeFuncional() == null ? null : vo.getUnidadeFuncional().getSeq(),
					especialidade == null ? null : especialidade.getSeq(), servidor, null,
					digitador, null, null, null, null, null, null, nomeMicrocomputador);

			if (inclusaoAtendimentoVO != null) {
				String leitoId = inclusaoAtendimentoVO.getLeitoId();
				Short quartoNumero = inclusaoAtendimentoVO.getNumeroQuarto();
				Short unidadeFuncionalSeq = inclusaoAtendimentoVO.getSeqUnidadeFuncional();

				vo.setLeito(StringUtils.isBlank(leitoId) ? null : this.getCadastrosBasicosInternacaoFacade()
						.obterLeitoPorId(leitoId));
				vo.setQuarto(quartoNumero == null ? null : this.getCadastrosBasicosInternacaoFacade().obterQuarto(quartoNumero));
				vo.setUnidadeFuncional(unidadeFuncionalSeq == null ? null : this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unidadeFuncionalSeq));
			}
		} catch(ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		} 
		return vo;
	}

	/**
	 * Método para atualizar o atendimento de urgencia da internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_ATLZ_ATD
	 * 
	 * @param atendimentoUrgencia
	 * @param internacao
	 * @param paciente
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @param especialidade
	 * @param professor
	 * @return
	 * @throws BaseException 
	 */
	public LeitoQuartoUnidadeFuncionalVO atualizarAtendimentoUrgenciaInternacao(
			AinAtendimentosUrgencia atendimentoUrgencia, AinInternacao internacao,
			AipPacientes paciente, AinLeitos leito, AinQuartos quarto,
			AghUnidadesFuncionais unidadeFuncional, AghEspecialidades especialidade,
			RapServidores professor, String nomeMicrocomputador) throws BaseException {
		if (especialidade == null) { // if (c_agh_especialidades%notfound ) then
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00163);
		}
		if (paciente == null) { // c_aip_pacientes%notfound
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIP_00013);
		}
		Integer codClinicaPed = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CLINICA_PEDIATRIA).getVlrNumerico()
				.intValueExact();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		LeitoQuartoUnidadeFuncionalVO voLQUF = atualizarLeitoQuartoUnidadeFuncional(leito, quarto,
				unidadeFuncional);

		List<AghAtendimentos> atendimentos;
		if (atendimentoUrgencia != null) {
			atendimentos = new ArrayList<AghAtendimentos>();
			atendimentos.add(atendimentoUrgencia.getAtendimento());
		} else {
			atendimentos = this.getPacienteFacade().obterAtendimentoNacimentoEmAndamento(paciente);
			
			//Determina a data-hora de ingresso na unidade como o valor da internação
			for(AghAtendimentos atendimento : atendimentos) {
				atendimento.setDthrIngressoUnidade(internacao.getDthrInternacao());
				atendimento.setOrigem(DominioOrigemAtendimento.I);
			}
		}

		// provavelmente só deve existir um
		for (AghAtendimentos atendimento : atendimentos) {

			atendimento.setPaciente(paciente);
			atendimento.setInternacao(internacao);
			atendimento.setLeito(voLQUF.getLeito());
			atendimento.setQuarto(voLQUF.getQuarto());
			atendimento.setUnidadeFuncional(voLQUF.getUnidadeFuncional());
			atendimento.setEspecialidade(especialidade);
			if ( // atd.ind_pac_pediatrico = decode(...)
			Objects.equals(especialidade.getClinica() == null ? null : especialidade
					.getClinica().getCodigo(), codClinicaPed)) {
				atendimento.setIndPacPediatrico(true);
			} else {
				atendimento.setIndPacPediatrico(especialidade.getIndEspPediatrica());
			}
			atendimento.setIndPacAtendimento(DominioPacAtendimento.S);
			atendimento.setDthrFim(null);
			atendimento.setProntuario(paciente.getProntuario());
			atendimento.setServidor(professor);
			
			AghAtendimentos atendimentoOld = getAghuFacade().obterOriginal(atendimento);
			
			getPacienteFacade().atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, new Date());
		}

		return voLQUF;
	}

	/**
	 * Método para atualizar o convênio/plano das cirurgias
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_CIRG
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConvenioPlanoCirurgias(Integer seqAtendimento, Short codigoConvenio,
			Byte seqConvenioSaudePlano, Date data) throws ApplicationBusinessException {

		try {
			List<MbcCirurgias> cirurgias = this.getBlocoCirurgicoFacade().pesquisarCirurgiasPorAtendimento(
					seqAtendimento, data);
	
			if (cirurgias.size() > 0) {
				FatConvenioSaudePlano convenioSaudePlano = this.getFaturamentoApoioFacade()
						.obterConvenioSaudePlano(codigoConvenio,
								seqConvenioSaudePlano);
	
				for (MbcCirurgias cirurgia : cirurgias) {
					cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
					cirurgia.setConvenioSaudePlano(convenioSaudePlano);
					this.getBlocoCirurgicoFacade().atualizarCirurgia(cirurgia);
				}
			}
		}
		catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().executaAtualizarConvenioPlanoCirurgias(seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data, servidorLogado);
			logError(e.getMessage());
		}
	}

	/**
	 * Método para atualizar o convênio/plano dos exames
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_EXME
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConvenioPlanoExames(Integer seqAtendimento, Short codigoConvenio,
			Byte seqConvenioSaudePlano, Date data, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
			IExamesLaudosFacade examesLaudosFacade = getExamesLaudosFacade();

			List<AelSolicitacaoExames> solicitacoesExames = examesLaudosFacade.listarSolicitacoesExames(seqAtendimento,
					data);
			if (solicitacoesExames != null && !solicitacoesExames.isEmpty()) {
				for (AelSolicitacaoExames solicitacaoExame : solicitacoesExames) {
					List<FatProcedAmbRealizado> procedimentosAmbulatoriaisRealizados = this.getFaturamentoFacade()
							.listarProcedAmbRealizados(
									solicitacaoExame.getSeq(),
									new DominioSituacaoProcedimentoAmbulatorio[] { DominioSituacaoProcedimentoAmbulatorio.ENCERRADO,
											DominioSituacaoProcedimentoAmbulatorio.APRESENTADO });

					if (procedimentosAmbulatoriaisRealizados == null
							|| procedimentosAmbulatoriaisRealizados.isEmpty()) {
						FatConvenioSaudePlano csp = faturamentoFacade.obterConvenioSaudePlanoPorChavePrimaria(new FatConvenioSaudePlanoId(
										codigoConvenio, seqConvenioSaudePlano));
						solicitacaoExame.setConvenioSaudePlano(csp);
						
						this.getSolicitacaoExameFacade().atualizar(solicitacaoExame, null, nomeMicrocomputador, servidorLogado);
				
					}
				}
			}
			
		} catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().executaAtualizarConvenioPlanoExames(seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data, servidorLogado);		
			logWarn(e.getMessage());
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_ATUALIZAR_SOLICITACAO_EXAME);
		}
	}

	/**
	 * Método para atualizar a data da última alta do paciente
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_DTALTA
	 * 
	 * @param codigoPaciente
	 * @param dataAtendimentoAntiga
	 * @param dataAtendimentoNova
	 * @throws ApplicationBusinessException
	 */
	public void atualizarDataAltaPaciente(Integer codigoPaciente, Date dataAtendimentoAntiga,
			Date dataAtendimentoNova, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			AipPacientes paciente = this.getPacienteFacade().obterPacientePorCodigoDtUltAltaEProntuarioNotNull(codigoPaciente,
					dataAtendimentoAntiga);

			if (paciente != null) {
				paciente.setDtUltAlta(dataAtendimentoNova);
				this.getCadastroPacienteFacade().persistirPaciente(paciente, nomeMicrocomputador);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00810);
		}
	}

	/**
	 * Método para atualizar a especialidade no atendimento
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_ESP_ATD
	 * 
	 * @param seqInternacao
	 * @param seqEspecialidade
	 * @throws BaseException 
	 */
	public void atualizarAtendimentoEspecialidade(final Integer seqInternacao,
			final Short seqEspecialidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getPacienteFacade().atualizarEspecialidadeNoAtendimento(seqInternacao, null, seqEspecialidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Atualiza primeiro e/ou último evento da internação.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_EVEN
	 * 
	 * @param seqInternacao
	 * @param data
	 * @param dataPrimeiroEvento
	 * @param dataUltimoEvento
	 */
	@SuppressWarnings("ucd")
	public void atualizarDatasEventosInternacao(Integer seqInternacao, Date data,
			Date dataPrimeiroEvento, Date dataUltimoEvento, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if (seqInternacao != null) {
			//AinInternacaoDAO ainInternacaoDAO = this.getAinInternacaoDAO();
			IPesquisaInternacaoFacade pesquisaInternacaoFacade = getPesquisaInternacaoFacade();
			
			if (dataPrimeiroEvento == null || dataPrimeiroEvento.after(data)) {
				// Atualiza o primeiro evento da internação se maior que o
				// parametro data
				AinInternacao internacao = pesquisaInternacaoFacade.obterInternacao(seqInternacao);
				internacao.setDthrPrimeiroEvento(data);

				this.getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, false,servidorLogado, false);
			}

			if (dataUltimoEvento == null || dataUltimoEvento.before(data)) {
				// Atualiza o último evento da internação se menor que o
				// parametro data
				AinInternacao internacao = pesquisaInternacaoFacade.obterInternacao(seqInternacao);
				internacao.setDthrUltimoEvento(data);

				this.getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, false,servidorLogado, false);
			}
		}
	}

	/**
	 * Método para atualizar a data final do atendimento
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_FIM_ATD
	 * 
	 * @param internacao
	 * @param dataSaida
	 * @throws BaseException 
	 */
	public void atualizarFimAtendimento(AinInternacao internacao, Date dataSaida, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getPacienteFacade().atualizarFimAtendimento(internacao, null, null, dataSaida, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Método para estornar alta. É permitido estornar somente a alta da
	 * internação mais recente do paciente
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_VER_EST_ALTA
	 * 
	 * Teste unitário deste método:
	 * InternacaoEnforceTest.testVerificaEstornaAlta()
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void verificaEstornaAlta(AinInternacao internacao) throws ApplicationBusinessException {		
		
		//for (AinInternacao outraInternacao: pesquisaInternacaoFacade.obterInternacaoPacientePorCodPac(internacao.getPacCodigo())){

		for (AinInternacao outraInternacao : pesquisaInternacaoFacade.pesquisarInternacaoPorPaciente(internacao.getPaciente().getCodigo())) {
			if (!outraInternacao.equals(internacao)
					&& outraInternacao.getDthrInternacao().after(internacao.getDthrInternacao())) {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00871);
			}
		}
	}

	/**
	 * Método para atualizar a data/hora de início quando alterada a data/hora
	 * da internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_INIC_ATD
	 * 
	 * @param seqInternacao
	 * @param dataInternacao
	 */
	public void atualizarDataInicioAtendimentoInternacao(final Integer seqInternacao, final Integer seqAtendUrgencia,
			final Date dataInternacao, final String nomeMicrocomputador) throws BaseException {

			
		if (!objetosOracleDAO.isOracle()) {
			atualizarDataInicioAtendimentoInternacaoPostgres(seqInternacao, seqAtendUrgencia, dataInternacao, nomeMicrocomputador);
		}
		else {
			final String nomeObjeto = EsquemasOracleEnum.AGH + "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_INIC_ATD;
	
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
					
			AghWork work = new AghWork(servidorLogado.getUsuario()) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL_STM + nomeObjeto
												+ "(?,?,?,?)}");
	
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER,
								seqInternacao);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, null);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dataInternacao);
	
						cs.execute();
					} finally {
						if(cs != null){
							cs.close();
						}
					}
				}
			};
			
			try {
				ainSolicTransfPacientesDAO.doWork(work);
			} catch (Exception e) {
				if (e.getCause().getMessage().indexOf("AGH-00185") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00185);
				} else if (e.getCause().getMessage().indexOf("AGH-00186") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00186);
				} else if (e.getCause().getMessage().indexOf("AGH-00187") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00187);
				} else {
					String valores = CoreUtil.configurarValoresParametrosCallableStatement(
									seqInternacao, null, null, dataInternacao);
					this.logError(
							CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true,
									valores));
					throw new ApplicationBusinessException(
							ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
							nomeObjeto, valores,
							CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e,
									false, valores));
				}
			}
			if (work.getException() != null){
				if (work.getException().getMessage().indexOf("AGH-00185") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00185);
				} else if (work.getException().getMessage().indexOf("AGH-00186") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00186);
				} else if (work.getException().getMessage().indexOf("AGH-00187") > -1) {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AGH_00187);
				} else {
					String valores = CoreUtil.configurarValoresParametrosCallableStatement(
									seqInternacao, null, null, dataInternacao);
					this.logError(
							CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true,
									valores));
					throw new ApplicationBusinessException(
							ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
							nomeObjeto, valores,
							CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(),
									false, valores));
				}
			}
		}
	}	

	/**
	 * Este método foi implementado para executar o código da procedure AGHK_ATD_RN.RN_ATDP_ATU_INIC_ATD
	 * quando o AGHU estiver rodando em banco postgres.
	 * @param seqInternacao
	 * @param dataInternacao
	 * @param usuarioLogado
	 * ORADB procedure AGHK_ATD_RN.RN_ATDP_ATU_INIC_ATD
	 * @throws MECBaseException 
	 */
	private void atualizarDataInicioAtendimentoInternacaoPostgres(
			Integer seqInternacao, Integer seqAtendUrgencia,
			Date dataInternacao, final String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (seqInternacao != null){
			List<AghAtendimentos> listaAtendimentosInternacao = getAghuFacade().pesquisarAtendimentosInternacaoNaoUrgencia(seqInternacao);
			for (AghAtendimentos atendimentoInternacao: listaAtendimentosInternacao){
				atendimentoInternacao.setDthrInicio(dataInternacao);
				
				AghAtendimentos atendimentoOld = getAghuFacade().obterOriginal(atendimentoInternacao);
				getPacienteFacade().atualizarAtendimento(atendimentoInternacao, atendimentoOld, nomeMicrocomputador, servidorLogado, new Date());
			}
		}
		if (seqAtendUrgencia != null){
			List<AghAtendimentos> listaAtendimentosUrgencia = getAghuFacade().listarAtendimentosPorSeqAtendimentoUrgencia(seqAtendUrgencia);
			for (AghAtendimentos atendimentoUrgencia: listaAtendimentosUrgencia){
				atendimentoUrgencia.setDthrInicio(dataInternacao);
				
				AghAtendimentos atendimentoOld = getAghuFacade().obterOriginal(atendimentoUrgencia);
				getPacienteFacade().atualizarAtendimento(atendimentoUrgencia, atendimentoOld, nomeMicrocomputador, servidorLogado, new Date());
			}
		}
		
		
	}
	
	
	
	
	/**
	 * Método para atualizar a conta da internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_INT_CNTA
	 * 
	 * @param seqInternacao
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	public void atualizarContaInternacao(Integer seqInternacao, final Date dataInternacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		try {
			IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
			
			List<AtualizarContaInternacaoVO> listaVO = faturamentoFacade.listarAtualizarContaInternacaoVO(seqInternacao, DominioSituacaoConta.C);
			
			if (listaVO != null && !listaVO.isEmpty()) {
				// Pega o primeiro registro, conforme cursor migrado
				final AtualizarContaInternacaoVO atualizarContaInternacaoVO = listaVO.get(0);
				
				if (DominioSituacaoConta.A.equals(atualizarContaInternacaoVO
						.getIndSituacaoContaHospitalar())
						|| DominioSituacaoConta.F.equals(atualizarContaInternacaoVO
								.getIndSituacaoContaHospitalar())) {
					FatContasHospitalares contaHospitalar = getFaturamentoFacade().obterContaHospitalar(
							atualizarContaInternacaoVO.getSeqContaHospitalar());
					
					// Clonando a conta hospitalar.
					FatContasHospitalares contaHospitalarClone = null;
					try {
						contaHospitalarClone = this.getFaturamentoFacade().clonarContaHospitalar(contaHospitalar);
					} catch (Exception e) {
						logError(EXCECAO_CAPTURADA, e);
						throw new ApplicationBusinessException(
								ValidaInternacaoRNExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
					}
	
					contaHospitalar.setDataInternacaoAdministrativa(dataInternacao);
					
					this.getFaturamentoFacade().persistirContaHospitalar(contaHospitalar, contaHospitalarClone, nomeMicrocomputador, dataFimVinculoServidor);
					
					this.executarAtualizacaoDiariasUti(atualizarContaInternacaoVO);
					
					if (!DominioGrupoConvenio.S.equals(atualizarContaInternacaoVO
							.getGrupoConvenioConvenioSaude())) {
						List<CntaConv> contasConvenio = this.getFaturamentoFacade().listarCntaConvPorSeqAtendimento(atualizarContaInternacaoVO.getSeqAtendimento());
						if (contasConvenio != null && !contasConvenio.isEmpty()) {
							final CntaConv contaConvenio = contasConvenio.get(0);
							
							if (contaConvenio.getIndEcrt().equals('N')) {
								this.executarAtualizacaoDatas(contaConvenio, dataInternacao);
							} else {
								throw new ApplicationBusinessException(
										ValidaInternacaoRNExceptionCode.FAT_00702);
							}
						}
					}
				} else {
					throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.FAT_00702);
				}
			}
		}
		catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			logWarn(e.getMessage());
			//faz chamada nativa
			getObjetosOracleDAO().executaAtualizarContaInternacao(seqInternacao, dataInternacao, servidorLogado);
		}
	}
	
	/**
	 * Método para chamar procedure de BD de forma nativa.
	 * 
	 * ORADB Procedure FATK_CTH_RN.RN_CTHP_ATU_DIAR_UTI
	 * 
	 * @param atualizarContaInternacaoVO
	 * @throws ApplicationBusinessException
	 */
	private void executarAtualizacaoDiariasUti(
			final AtualizarContaInternacaoVO atualizarContaInternacaoVO)
			throws ApplicationBusinessException {

		if (!objetosOracleDAO.isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH.toString() + "."
				+ ObjetosBancoOracleEnum.FATK_CTH_RN_RN_CTHP_ATU_DIAR_UTI;

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto).append("(?)}");
					cs = connection.prepareCall(sbCall.toString());
					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER,
							atualizarContaInternacaoVO == null ? null
									: atualizarContaInternacaoVO.getSeqContaHospitalar());
					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			ainSolicTransfPacientesDAO.doWork(work);
		} catch (Exception e) {
			String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(atualizarContaInternacaoVO == null ? null
							: atualizarContaInternacaoVO.getSeqContaHospitalar());
			this.logError(
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true,
							valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto,
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e,
							false, valores));
		}
		
		if (work.getException() != null){
			String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(atualizarContaInternacaoVO == null ? null
							: atualizarContaInternacaoVO.getSeqContaHospitalar());
			this.logError(
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true,
							valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto,
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(),
							false, valores));
		}
	}

	/**
	 * Método para chamar procedure de BD de forma nativa.
	 * 
	 * ORADB Procedure FFC_ALTERA_DATA
	 * 
	 * @param contaConvenio
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	private void executarAtualizacaoDatas(final CntaConv contaConvenio, final Date dataInternacao)
			throws ApplicationBusinessException {

		if (!objetosOracleDAO.isOracle()) {
			return;
		}
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_ALTERA_DATA.toString();

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					StringBuilder sbCall = new StringBuilder(CALL_STM);
					sbCall.append(nomeObjeto).append("(?,?)}");

					cs = connection.prepareCall(sbCall.toString());
					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER,
							contaConvenio == null ? null : contaConvenio.getNro());
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.VARCHAR,
							DATE_FORMAT.format(dataInternacao));

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			ainSolicTransfPacientesDAO.doWork(work);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(
					contaConvenio == null ? null : contaConvenio.getNro(),
					DATE_FORMAT.format(dataInternacao));
			this.logError(
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true,
							valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto,
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e,
							false, valores));
		}
		
		if (work.getException() != null){
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(
					contaConvenio == null ? null : contaConvenio.getNro(),
					DATE_FORMAT.format(dataInternacao));
			this.logError(
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true,
							valores));
			throw new ApplicationBusinessException(
					ValidaInternacaoRNExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto,
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(),
							false, valores));
		}
	}

	/**
	 * Método para atualizar o local de atendimento da internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_LCAL_ATD
	 * 
	 * @param seqInternacao
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public LeitoQuartoUnidadeFuncionalVO atualizarLocalAtendimento(final AinInternacao internacao,
			final AinLeitos leito, final AinQuartos quarto,
			final AghUnidadesFuncionais unidadeFuncional, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		// Chamada de AINK_INT_RN.RN_INTP_ATU_LOCAL
		final LeitoQuartoUnidadeFuncionalVO vo = this.atualizarLeitoQuartoUnidadeFuncional(leito,
				quarto, unidadeFuncional);

		AtualizarLocalAtendimentoVO atualizarLocalAtendimentoVO = this.getPacienteFacade()
				.atualizarLocalAtendimento(internacao.getSeq(), null, null,
						vo.getLeito() != null ? vo.getLeito().getLeitoID() : null,
						vo.getQuarto() != null ? vo.getQuarto().getNumero() : null,
						vo.getUnidadeFuncional() != null ? vo.getUnidadeFuncional().getSeq() : null, 
						nomeMicrocomputador, dataFimVinculoServidor);
		
		if (atualizarLocalAtendimentoVO != null) {
			String leitoId = atualizarLocalAtendimentoVO.getLeitoId();
			Short numeroQuarto = atualizarLocalAtendimentoVO.getNumeroQuarto();
			Short seqUnidadeFuncional = atualizarLocalAtendimentoVO.getSeqUnidadeFuncional();

			vo.setLeito(leitoId != null ? this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId) : null);
			vo.setQuarto(numeroQuarto != null ? this.getCadastrosBasicosInternacaoFacade().obterQuarto(numeroQuarto) : null);
			vo.setUnidadeFuncional(seqUnidadeFuncional != null ? this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional) : null);
		}
		
		return vo;
	}

	/**
	 * Método para gravar leito, quarto e unidade funcional para todos os
	 * movimentos
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_LOCAL
	 * 
	 * @param leito
	 * @param numero
	 * @param unidadeFuncional
	 * @return
	 */
	public LeitoQuartoUnidadeFuncionalVO atualizarLeitoQuartoUnidadeFuncional(AinLeitos leito,
			AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional) {
		LeitoQuartoUnidadeFuncionalVO leitoQuartoUnidadeFuncionalVO = new LeitoQuartoUnidadeFuncionalVO(
				leito, quarto, unidadeFuncional);

		if (leito != null) {
			leitoQuartoUnidadeFuncionalVO.setQuarto(leito.getQuarto());
			leitoQuartoUnidadeFuncionalVO.setUnidadeFuncional(leito.getUnidadeFuncional());
		} else {
			if (quarto != null) {
				leitoQuartoUnidadeFuncionalVO.setUnidadeFuncional(quarto.getUnidadeFuncional());
			}
		}

		return leitoQuartoUnidadeFuncionalVO;
	}

	/**
	 * Update no pac_codigo chamada a partir do update na internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_PAC_ATD
	 * 
	 * @param seqAtendimentoUrgencia
	 * @param seqInternacao
	 * @param codigoPaciente
	 * @throws BaseException 
	 */
	public void atualizarPacienteAtendimento(AinAtendimentosUrgencia atendimentoUrgencia,
			AinInternacao internacao, AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario) throws BaseException {
		this.getPacienteFacade().atualizarPacienteAtendimento(atendimentoUrgencia, internacao, null, null,
				paciente, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
	}

	/**
	 * Método para atualizar dados do paciente referentes a sua internação.
	 * Utilizado por padrão como 'INT' (internação) no tipo de atualização.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_PACIENTE
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor, final DominioTransacaoAltaPaciente transacao) throws ApplicationBusinessException {
		// Para as chamadas de atualizarPaciente sem o tipo, o padrão é 'INT'
		atualizarPaciente(paciente, AtualizarPacienteTipo.INT, nomeMicrocomputador, dataFimVinculoServidor, transacao);
	}

	/**
	 * Método para atualizar dados do paciente referentes a sua internação
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_PACIENTE
	 * 
	 * @param paciente
	 * @param tipo
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPaciente(AipPacientes paciente, AtualizarPacienteTipo tipo, String nomeMicrocomputador, final Date dataFimVinculoServidor, final DominioTransacaoAltaPaciente transacao)
			throws ApplicationBusinessException {



		if (paciente != null) {
			AghParametros parametroObito = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);
			AghParametros parametroObito48hs = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H);

			if (parametroObito == null || parametroObito48hs == null) {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00347);
			}
			
			//Este flush foi colocado aqui para que a internação
			//que foi a recém feita já possa ser obtida na pesquisa
			//de internações seguinte à chamada do método
			//verificarAtualizacaoPaciente abaixo
			this.flush();

			List<InternacaoAtendimentoUrgenciaPacienteVO> voList = this.getPesquisaInternacaoFacade()
					.pesquisarInternacaoAtendimentoUrgenciaPorPaciente(paciente.getCodigo());

			InternacaoAtendimentoUrgenciaPacienteVO vo = new InternacaoAtendimentoUrgenciaPacienteVO();
			if (voList.size() == 0) {
				// Se dtUltAlta for diferente de NULL, mantém o mesmo valor
				if (paciente.getDtUltAlta() == null) {
					vo.setDataAltaMedica(null);
				}
				// Se dtUltInternacao for diferente de NULL, mantém o mesmo
				// valor
				if (paciente.getDtUltInternacao() == null) {
					vo.setDataInicio(null);
				}
				vo.setLeitoId(null);
				vo.setNumeroQuarto(null);
				vo.setSeqUnidadeFuncional(null);
				this.atualizarDadosPaciente(paciente, vo, nomeMicrocomputador, dataFimVinculoServidor, transacao);

			} else if (voList.size() > 0) {
				this.verificarAtualizacaoPaciente(paciente, parametroObito, parametroObito48hs,
						voList, nomeMicrocomputador, dataFimVinculoServidor, transacao);
			}
		}
	}

	/**
	 * Método para fazer validações nos dados do paciente antes de atualizá-lo
	 * 
	 * @param paciente
	 * @param parametroObito
	 * @param parametroObito48hs
	 * @param voList
	 * @throws ApplicationBusinessException
	 */
	private void verificarAtualizacaoPaciente(AipPacientes paciente, AghParametros parametroObito,
			AghParametros parametroObito48hs, List<InternacaoAtendimentoUrgenciaPacienteVO> voList, String nomeMicrocomputador, final Date dataFimVinculoServidor, final DominioTransacaoAltaPaciente transacao)
			throws ApplicationBusinessException {
		
		// VO com dados que serão gravados no paciente
		InternacaoAtendimentoUrgenciaPacienteVO voPaciente = new InternacaoAtendimentoUrgenciaPacienteVO();

		// VO utilizado para simular os FETCH do oracle, já que não foi
		// implementado utilizando nenhum loop e a lógica não está clara
		InternacaoAtendimentoUrgenciaPacienteVO vo = voList.get(0);
		
		voPaciente.setLeitoId(vo.getLeitoId());
		voPaciente.setNumeroQuarto(vo.getNumeroQuarto());
		voPaciente.setSeqUnidadeFuncional(vo.getSeqUnidadeFuncional());
		
		arrumarLocal(vo, voPaciente);
		
		if (vo.getSeqAtendimentoUrgencia() == null) {
			voPaciente.setDataInicio(vo.getDataInicio());
			voPaciente.setDataAltaMedica(vo.getDataAltaMedica());
			
			if (vo.getDataAltaMedica() == null) {
				if (voList.size() > 1) {
					voPaciente.setDataAltaMedica(voList.get(1).getDataAltaMedica());
				} else {
					// Chamado método para ajuste de dados conforme seria feito
					// se buscados dados do BULL
					this.ajustarDados(paciente, voPaciente);
				}
			} else if (parametroObito.getVlrTexto().equals(vo.getCodigoTipoAltaMedica())
					|| parametroObito48hs.getVlrTexto().equals(vo.getCodigoTipoAltaMedica())) {
				voPaciente.setDataObito(DateUtils.truncate(vo.getDataAltaMedica(),
						Calendar.DAY_OF_MONTH));
			}
			this.atualizarDadosPaciente(paciente, voPaciente, nomeMicrocomputador, dataFimVinculoServidor, transacao);

		} else if (vo.getDataAltaMedica() != null) {
			voPaciente.setDataAltaMedica(vo.getDataAltaMedica());
			voPaciente.setDataInicio(vo.getDataInicio());
			
			if (parametroObito.getVlrTexto().equals(vo.getCodigoTipoAltaMedica())
					|| parametroObito48hs.getVlrTexto().equals(vo.getCodigoTipoAltaMedica())) {
				voPaciente.setDataObito(DateUtils.truncate(vo.getDataAltaMedica(),
						Calendar.DAY_OF_MONTH));
			}

			if (!AtualizarPacienteTipo.ATU.equals(vo.getSigla())&& voList.size() > 1) {
				voPaciente.setDataInicio(voList.get(1).getDataInicio());
			}
			this.atualizarDadosPaciente(paciente, voPaciente, nomeMicrocomputador, dataFimVinculoServidor, transacao);
		} else {
			if (AtualizarPacienteTipo.ATU.equals(vo.getSigla())) {
				// ATU
				voPaciente.setDataInicio(vo.getDataInicio());
				
				if (voList.size() > 1 && voList.get(1) != null) {
					voPaciente.setDataAltaMedica(voList.get(1).getDataAltaMedica());
				} else {
					// Chamado método para ajuste de dados conforme seria feito
					// se buscados dados do BULL
					this.ajustarDados(paciente, voPaciente);
					voPaciente.setDataAltaMedica(null);
				}
			} else {
				// INT
				if (voList.size() > 1 && voList.get(1) != null) {
					voPaciente.setDataInicio(voList.get(1).getDataInicio());
				}
				if (voList.size() > 2 && voList.get(2) != null) {
					voPaciente.setDataAltaMedica(voList.get(2).getDataAltaMedica());
				} else {
					// Chamado método para ajuste de dados conforme seria feito
					// se buscados dados do BULL
					this.ajustarDados(paciente, voPaciente);
					voPaciente.setDataAltaMedica(null);
				}
			}
			
			this.atualizarDadosPaciente(paciente, voPaciente, nomeMicrocomputador, dataFimVinculoServidor, transacao);
		}
	}

	/**
	 * Método para ajustar o voPaciente com valores equivalentes aos que seriam
	 * setados caso os registros do BULL fossem utilizados.
	 * 
	 * @param paciente
	 * @param voPaciente
	 */
	private void ajustarDados(AipPacientes paciente,
			InternacaoAtendimentoUrgenciaPacienteVO voPaciente) {
		if (voPaciente != null) {
			// Seta a data de internação a ser gravada em AipPacientes
			if (voPaciente.getDataInicio() == null) {
				voPaciente.setDataInicio(paciente.getDtUltInternacao());
			}
			
			// Seta o leito a ser gravado em AipPacientes
			if (voPaciente.getLeitoId() == null && voPaciente.getNumeroQuarto() == null
					&& voPaciente.getSeqUnidadeFuncional() == null) {
				voPaciente.setLeitoId(paciente.getLtoLtoId());
			}
			
			// Seta a data de alta do paciente se a mesma estiver nula
			if (voPaciente.getDataAltaMedica() == null) {
				voPaciente.setDataAltaMedica(paciente.getDtUltAlta());
			}
		}
	}

	/**
	 * Método para atualizar daods do paciente (data de alta médica, data
	 * internação, data óbito, leito, unidade funcional, quarto etc)
	 * 
	 * @param paciente
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void atualizarDadosPaciente(AipPacientes paciente,
			InternacaoAtendimentoUrgenciaPacienteVO vo, String nomeMicrocomputador, final Date dataFimVinculoServidor, final DominioTransacaoAltaPaciente transacao) throws ApplicationBusinessException {
		
		paciente.setDtUltInternacao(vo.getDataInicio());

		if(transacao != null && transacao.equals(DominioTransacaoAltaPaciente.ESTORNA_ALTA)){
			paciente.setDtUltAlta(null);
		}else{
			paciente.setDtUltAlta(vo.getDataAltaMedica());
		}

		if (vo.getLeitoId() != null) {
			paciente.setLeito(this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(vo.getLeitoId()));
		} else {
			paciente.setLeito(null);
		}
		
		if (vo.getNumeroQuarto() != null) {
			paciente.setQuarto(this.getCadastrosBasicosInternacaoFacade().obterQuarto(vo.getNumeroQuarto()));
		} else {
			paciente.setQuarto(null);
		}

		if (vo.getSeqUnidadeFuncional() != null) {
			paciente.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(vo.getSeqUnidadeFuncional()));
		} else {
			paciente.setUnidadeFuncional(null);
		}

		Date dataObito = null;
		if (AtualizarPacienteTipo.INT.equals(vo.getSigla())) {
			dataObito = vo.getDataObito();
		} else {
			// vo.dataObito é setado no método "verificarAtualizacaoPaciente"
			// quando o tipo de alta médica for "O" (óbito) e igual ao parâmetro
			// P_COD_TIPO_ALTA_OBITO ou P_COD_TIPO_ALTA_OBITO_MAIS_48H. Se o
			// mesmo não for nulo, atualiza a data de óbito
			if (vo.getDataObito() != null) {
				dataObito = paciente.getDtObito() == null ? vo.getDataAltaMedica() : paciente
						.getDtObito();
			}
		}

		paciente.setDtObito(dataObito);

		this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Método para ajustar o local de internação do paciente (leito, quarto ou
	 * unidadeFuncional)
	 * 
	 * @param vo
	 */
	private void arrumarLocal(InternacaoAtendimentoUrgenciaPacienteVO vo,
			InternacaoAtendimentoUrgenciaPacienteVO voPaciente) {
		if (vo != null) {
			if (vo.getLocalPaciente() != null
					&& DominioLocalPaciente.U.equals(vo.getLocalPaciente())) {
				voPaciente.setLeitoId(null);
				voPaciente.setNumeroQuarto(null);
			} else if (vo.getLocalPaciente() != null
					&& DominioLocalPaciente.Q.equals(vo.getLocalPaciente())) {
				voPaciente.setSeqUnidadeFuncional(null);
				voPaciente.setLeitoId(null);
			} else {
				voPaciente.setNumeroQuarto(null);
				voPaciente.setSeqUnidadeFuncional(null);
			}
		}
	}

	/**
	 * Método para atualizar o servidor responsável pelo atendimento
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_PROF_ATD
	 * 
	 * @param seqInternacao
	 * @param matricula
	 * @param vinCodigo
	 * @throws BaseException 
	 */
	public void atualizarProfessorAtendimento(final Integer seqInternacao,
			final RapServidores servidor, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getPacienteFacade().atualizarProfissionalResponsavelPeloAtendimento(seqInternacao, null,
				servidor, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Método para efetuar solicitação de transferencia. Atualiza as
	 * solicitações efetuadas.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_SOL_TRAN
	 * 
	 * @param seqInternacao
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSolicitacaoTransferencia(Integer seqInternacao, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional) throws ApplicationBusinessException {
		try {
			AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
			
			// Atualiza Solicitacoes Efetuadas
			DominioSituacaoSolicitacaoInternacao indSolicitacaoAtendida = null;
			DominioSituacaoSolicitacaoInternacao indSolicitacaoEfetuada = null;

			AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_ATENDIDA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				indSolicitacaoAtendida = DominioSituacaoSolicitacaoInternacao.valueOf(aghParametros
						.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00660);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_EFETUADA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				indSolicitacaoEfetuada = DominioSituacaoSolicitacaoInternacao.valueOf(aghParametros
						.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00660);
			}

			List<AinSolicTransfPacientes> solicitacoesTransferenciaPacientes = ainSolicTransfPacientesDAO
					.listarSolicTransPacientes(seqInternacao, leitoId, numeroQuarto, seqUnidadeFuncional, indSolicitacaoAtendida);
			if (solicitacoesTransferenciaPacientes != null
					&& !solicitacoesTransferenciaPacientes.isEmpty()) {
				for (AinSolicTransfPacientes solicitacaoTransferenciaPaciente : solicitacoesTransferenciaPacientes) {
					
					AinSolicTransfPacientes solicitacaoTransferenciaPacienteOriginal = ainSolicTransfPacientesDAO.obterOriginal(solicitacaoTransferenciaPaciente.getSeq());

					solicitacaoTransferenciaPaciente.setIndSitSolicLeito(indSolicitacaoEfetuada);

					this.getInternacaoRN().atualizarSolicitacoesTransferenciaPacientes(
							solicitacaoTransferenciaPaciente,
							solicitacaoTransferenciaPacienteOriginal);
					
					ainSolicTransfPacientesDAO.flush();
				}
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00794);
		}
	}

	/**
	 * Método para atualizar dados de SSM da conta do paciente no faturamento
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_SSM_CNTA
	 * 
	 * @param seqInternacao
	 * @param seqPho
	 * @param seqIph
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSsmConta(final Integer seqInternacao, final Short seqPho,
			final Integer seqIph, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException { // ApplicationBusinessException
																// {

		List<FatContasInternacao> contasInternacao = this.getFaturamentoFacade()
				.pesquisarNumeroAihContaHospitalarAtendimento(seqInternacao);

		if (contasInternacao != null && !contasInternacao.isEmpty()) {
			FatContasInternacao contaInternacao = contasInternacao.get(0);

			if (contaInternacao.getContaHospitalar().getAih() != null) {
				throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.FAT_00926);
			}

			VFatAssociacaoProcedimento associacaoProcedimento = this.getFaturamentoFacade()
					.obterAssociacaoProcedimento(seqPho, seqIph);

			if (associacaoProcedimento != null
					&& associacaoProcedimento.getProcedimentoHospitalarInterno() != null) {

				// Clonando a conta hospitalar.
				FatContasHospitalares contaHospitalarClone = null;
				try {
					contaHospitalarClone = this.getFaturamentoFacade().clonarContaHospitalar(contaInternacao
							.getContaHospitalar());
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
						throw new ApplicationBusinessException(
							ValidaInternacaoRNExceptionCode.ERRO_CLONAR_CONTA_HOSPITALAR);
				}

				// Atualiza a conta hospitalar
				contaInternacao.getContaHospitalar().setProcedimentoHospitalarInterno(
						associacaoProcedimento.getProcedimentoHospitalarInterno());
				// Persiste conta hospitalar
				this.getFaturamentoFacade().persistirContaHospitalar(contaInternacao.getContaHospitalar(),
						contaHospitalarClone, nomeMicrocomputador, dataFimVinculoServidor);
			}

			} else {
			// fatk_cth4_rn.v_atu_cth_ssm_sol := FALSE;
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00838);
			}
		}

	/**
	 * Método para verificar previsão de alta
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_VER_PREV_ALT
	 * 
	 * @param dataPrevisaoAltaNova
	 * @param dataPrevisaoAltaAntiga
	 * @throws ApplicationBusinessException
	 */
	public void verificarPrevisaoAlta(Date dataPrevisaoAltaNova, Date dataPrevisaoAltaAntiga)
			throws ApplicationBusinessException {

		if (dataPrevisaoAltaNova == null && dataPrevisaoAltaAntiga != null) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00755);
		}
	}

	/**
	 * Método para validações quando o saldo de internação de projeto de
	 * pesquisa é menor ou igual a zero e permiteIntercorrencia é verdadeiro.
	 * Caso a validação seja satisfeita e o voucherEletronico seja false, o
	 * método é finalizado, não fazendo validação do saldo. Caso a validação
	 * seja satisfeita e o voucherEletronico seja true, o método busca o
	 * AelProjetoIntercorrenciaInternacao e atualiza sua internação com a
	 * internação corrente e seu atributo efetivado para true.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_VER_SAL_PROJ
	 * 
	 * @param projetoPesquisa
	 * @param paciente
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarSaldoProjeto(AelProjetoPesquisas projetoPesquisa, AipPacientes paciente,
			AinInternacao internacao) throws ApplicationBusinessException {

		if (projetoPesquisa != null) {
			if (projetoPesquisa.getVoucherEletronico()) {
				return;
			} else {
				List<AinInternacao> internacaoList = this.getPesquisaInternacaoFacade()
						.pesquisarInternacaoPorProjetoPesquisa(projetoPesquisa.getSeq());

				Integer quantidadeInternacoes = internacaoList.size();
				Integer quantidadeInternacaoProjetoPesquisa = projetoPesquisa.getQtdeInternacoes() == null ? 0
						: projetoPesquisa.getQtdeInternacoes().intValue();
				Integer saldo = quantidadeInternacaoProjetoPesquisa - quantidadeInternacoes;

				if (saldo <= 0) {
					if (projetoPesquisa.getPermiteIntercorInternacao()) {
						AelProjetoIntercorrenciaInternacao projetoIntercorrenciaInternacao = this.getExamesLaudosFacade()
								.obterProjetoIntercorrenciaInternacao(paciente.getCodigo(),
										projetoPesquisa.getSeq(), null);
						if (projetoIntercorrenciaInternacao == null
								|| projetoIntercorrenciaInternacao.getJustificativa() == null
								|| projetoIntercorrenciaInternacao.getEfetivado()) {
							throw new ApplicationBusinessException(
									ValidaInternacaoRNExceptionCode.AIN_00858);
						} else {
							try {
								projetoIntercorrenciaInternacao.setEfetivado(true);
								projetoIntercorrenciaInternacao.setInternacao(internacao);

								// Esse flush representa o UPDATE na rotina que
								// atualiza a entidade
								// AelProjetoIntercorrenciaInternacao
								this.getExamesLaudosFacade()
										.persistirProjetoIntercorrenciaInternacao(projetoIntercorrenciaInternacao);

							} catch (Exception e) {
								logError(EXCECAO_CAPTURADA, e);
								throw new ApplicationBusinessException(
										ValidaInternacaoRNExceptionCode.AIN_00859);
							}
						}
					} else {
						// Não é permitido internar Paciente para Projeto de
						// Pesquisa quando Saldo do Projeto é menor ou igual a
						// zero
						throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00857);
					}
				}
			}
		}
	}

	/**
	 * Atualiza datas eventos na internação. Atualiza primeiro e/ou último
	 * evento da internação.
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_EVEN_INT
	 * 
	 * @param seqAtendimento
	 * @param novaData
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void atualizarDatasEventosInternacao(Integer seqAtendimento, Date novaData, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		List<AinInternacao> listaVO = this.getAghuFacade().listarInternacoesDoAtendimento(seqAtendimento, novaData);
		if (listaVO == null || listaVO.isEmpty()) {
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00756);
		}
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		// Pega o primeiro registro, conforme cursor migrado
		AinInternacao internacao = listaVO.get(0);

		try {
			if (internacao.getDthrPrimeiroEvento() == null
					|| internacao.getDthrPrimeiroEvento().after(novaData)) {
				// Atualiza primeiro evento se maior que novaData
				internacao.setDthrPrimeiroEvento(novaData);
				this.getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, false, servidorLogado, false);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00757);
		}

		try {
			if (internacao.getDthrUltimoEvento() == null
					|| internacao.getDthrUltimoEvento().before(novaData)) {
				// Atualiza ultimo evento se menor que novaData
				internacao.setDthrUltimoEvento(novaData);
				this.getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, false, servidorLogado, false);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ValidaInternacaoRNExceptionCode.AIN_00758);
		}
	}


	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO() {
		return ainSolicTransfPacientesDAO;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}