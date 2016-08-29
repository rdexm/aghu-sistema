package br.gov.mec.aghu.faturamento.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioBoletimAmbulatorio;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloFatContaApac;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvFxEtariaItemDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedTratamentosDAO;
import br.gov.mec.aghu.faturamento.dao.FatResumoApacsDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.vo.CnsResponsavelVO;
import br.gov.mec.aghu.faturamento.vo.FatConsultaPrhVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoProcedAmbVO;
import br.gov.mec.aghu.faturamento.vo.FatGrupoSubGrupoVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.faturamento.vo.RnCpecAtuEncCompVO;
import br.gov.mec.aghu.faturamento.vo.RnFatcVerItprocexcVO;
import br.gov.mec.aghu.faturamento.vo.TabelaQuantidadeVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedTratamento;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB Package FATK_PMR_RN
 * 
 * @author gandriotti
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta","PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.AtributoEmSeamContextManager"})
@Stateless
public class FaturamentoFatkPmrRN extends AbstractFatDebugLogEnableRN {
	
	private static final String RN_PMRP_GERA_NEW = "RN_PMRP_GERA_NEW";

	private static final String RN_PMRC_ATU_REGRAS = "RN_PMRC_ATU_REGRAS";

	@EJB
	private VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN;
	
	@EJB
	private FaturamentoFatkInterfaceAacRN faturamentoFatkInterfaceAacRN;
	
	@EJB
	private CaracteristicaItemProcedimentoHospitalarRN caracteristicaItemProcedimentoHospitalarRN;
	
	@EJB
	private FaturamentoON faturamentoON;
	
	@EJB
	private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;
	
	@EJB
	private FaturamentoFatkCpeRN faturamentoFatkCpeRN;
	
	@EJB
	private FaturamentoFatkCapUniRN faturamentoFatkCapUniRN;
	
	@EJB
	private FatItemContaApacON fatItemContaApacON;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;
	
	@EJB
	private FatCandidatosApacOtorrinoON fatCandidatosApacOtorrinoON;
	
	private static final Log LOG = LogFactory.getLog(FaturamentoFatkPmrRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@Inject
	private FatProcedTratamentosDAO fatProcedTratamentosDAO;
	
	@Inject
	private FatContaApacDAO fatContaApacDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatEspelhoProcedAmbDAO fatEspelhoProcedAmbDAO;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatConvFxEtariaItemDAO fatConvFxEtariaItemDAO;
	
	@Inject
	private FatItemContaApacDAO fatItemContaApacDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private FatResumoApacsDAO fatResumoApacsDAO;

	private static final long serialVersionUID = 954977845146336106L;
	private final String REGEX_BR = "\\<br\\s*\\/>";
	private final String QUEBRA_LINHA = "\n";
	private static final int TAMANHO_MAXIMO_LOTE_COMPETENCIA = 2000;

	
	enum FaturamentoFatkPmrRNExceptionCode implements BusinessExceptionCode {
		MSG_NAO_ENCONTRADA_COMPETENCIA_ANTERIOR, ERRO_DURANTE_GERA_ESPELHO_FATURAMENTO_AMBULATORIO;
	}

	/**
	 * <p>
	 * <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>NOK</b><br/>
	 * Linhas: 17 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 2 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_PMRP_CANCELA_PMR</code>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatProcedAmbRealizado} <br/>
	 * </p>
	 * 
	 * @param pSeq
	 * @param isPrevia
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException
	 * @see FatProcedAmbRealizado
	 */
	public void rnPmrpCancelaPmr(final Long pPmrSeq, final boolean isPrevia, String nomeMicrocomputador, Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO)
			throws BaseException {
		if (!isPrevia) {
			final FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
			final ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
			final FatProcedAmbRealizado procedAmbRealizado = ambRealizadoDAO.obterPorChavePrimaria(pPmrSeq);
			FatProcedAmbRealizado old = ambRealizadosON.clonarFatProcedAmbRealizado(procedAmbRealizado);
			procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
			
			ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado( procedAmbRealizado, 
																		old, 
																		nomeMicrocomputador, 
																		dataFimVinculoServidor,
																		fatVariaveisVO);
		}
	}

	/**
	 * <p>
	 * <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 154 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 6 tabelas <br/>
	 * Metodos externos: 4 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_PMRC_ATU_REGRAS</code>
	 * </p>
	 * <p>
	 * <b>INSERT:</b> {@link FatLogError} <br/>
	 * </p>
	 * 
	 * @param pPmrSeq
	 * @param pCnvCodigo
	 * @param pPacCodigo
	 * @param pIphPhoSeq
	 * @param pIphSeq
	 * @param pCpeCompetencia
	 * @param isPrevia
	 * @param pModulo
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException
	 * @see FatLogError
	 * @see VerificacaoItemProcedimentoHospitalarRN#verificarPossibilidadeCobranca(Short,
	 *      Integer, String)
	 * @see VerificacaoItemProcedimentoHospitalarRN#getHcpaCadastrado(Integer,
	 *      Integer)
	 * @see VerificacaoItemProcedimentoHospitalarRN#verificarCompatibilidadeSexoPacienteSexoProcedimento(Short,
	 *      Integer, Integer)
	 * @see VerificacaoItemProcedimentoHospitalarRN#verificarRestricaoFaixaEtariaParaProcedimentoConvenio(Short,
	 *      Integer, Integer, Short)
	 * @see #getFatkIphRn()
	 * @see #rnPmrpCancelaPmr(Integer, boolean)
	 */
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public boolean rnPmrpAtuRegras(final Long pPmrSeq, final Short pCnvCodigo,
			final Integer pPacCodigo, final Short pIphPhoSeq,
			final Integer pIphSeq, final Date pCpeCompetencia,
			final boolean isPrevia, final String pModulo, final String mama, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO)
			throws BaseException {
		boolean retorno = true;
		// -- verifica se o procedimento e' cobravel em AMBULATORIO
		final String resultVerCobranca = getFatkIphRn()
				.verificarPossibilidadeCobranca(pIphPhoSeq, pIphSeq,
						VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_B); // Ambulatório
		final FaturamentoON faturamentoON = getFaturamentoON();
		if (resultVerCobranca == null && !mama.equalsIgnoreCase(pModulo)) {
			faturamentoON
					.criarFatLogErrors(
							"NAO FOI POSSIVEL IDENTIFICAR PERMISSAO DE COBRANCA DO ITEM PROCEDIMENTO HOSPITALAR.",
							DominioModuloCompetencia.AMB.toString(), null,
							null, null, null, null, new Date(), null,
							null, pIphPhoSeq, null, pIphSeq, null, pPacCodigo,
							null, null, pPmrSeq, null, null, null, null, null,
							null, null, null, null, RN_PMRC_ATU_REGRAS, null,
							null, new FatMensagemLog(176));
			// -- Marina 11/10/2009
			this.rnPmrpCancelaPmr(pPmrSeq, isPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			retorno = false;
		} else {
			if (VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_N
					.equals(resultVerCobranca)) {
				faturamentoON.criarFatLogErrors(
						"PROCEDIMENTO HOSPITALAR NAO PODE SER COBRADO",
						DominioModuloCompetencia.AMB.toString(), null, null,
						null, null, null, new Date(), null, null,
						pIphPhoSeq, null, pIphSeq, null, pPacCodigo, null,
						null, pPmrSeq, null, null, null, null, null, null,
						null, null, null, "RN_PMRC_ATU_REGRAS", null, null, new FatMensagemLog(206));
				// -- Marina 11/10/2009
				this.rnPmrpCancelaPmr(pPmrSeq, isPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
				retorno = false;
			}
		}
		// -- verifica se o HOSPITAL esta habilitado a executar o procedimento
		if (!getFatkIphRn().getHcpaCadastrado(pIphPhoSeq, pIphSeq)) {
			faturamentoON
					.criarFatLogErrors(
							"HOSPITAL NAO CADASTRADO PARA EXECUTAR O ITEM PROCEDIMENTO HOSPITALAR.",
							DominioModuloCompetencia.AMB.toString(), null,
							null, null, null, null, new Date(), null,
							null, pIphPhoSeq, null, pIphSeq, null, pPacCodigo,
							null, null, pPmrSeq, null, null, null, null, null,
							null, null, null, null, RN_PMRC_ATU_REGRAS, null,
							null, new FatMensagemLog(90));
			// -- Marina 11/10/2009
			this.rnPmrpCancelaPmr(pPmrSeq, isPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			retorno = false;
		}
		// -- verifica se o procedimento possui valor cadastrado p/competencia
		// -- verifica se sexo do paciente e' compativel c/sexo do procedimento
		if (pPacCodigo != null) {
			if (!getFatkIphRn()
					.verificarCompatibilidadeSexoPacienteSexoProcedimento(
							pIphPhoSeq, pIphSeq, pPacCodigo)) {
				faturamentoON
						.criarFatLogErrors(
								"SEXO DO PACIENTE INCOMPATIVEL COM O ITEM PROCEDIMENTO HOSPITALAR.",
								DominioModuloCompetencia.AMB.toString(), null,
								null, null, null, null, new Date(),
								null, null, pIphPhoSeq, null, pIphSeq, null,
								pPacCodigo, null, null, pPmrSeq, null, null,
								null, null, null, null, null, null, null,
								RN_PMRC_ATU_REGRAS, null, null,new FatMensagemLog(250));
				retorno = false;
			}
			// -- verifica se a idade do paciente e' compativel
			// -- com a idade permitida para o procedimento
			if (!getFatkIphRn()
					.verificarRestricaoFaixaEtariaParaProcedimentoConvenio(
							pIphPhoSeq, pIphSeq, pPacCodigo, pCnvCodigo)) {
				
				faturamentoON
						.criarFatLogErrors(
								"IDADE DO PACIENTE INCOMPATIVEL COM AS FAIXAS ETARIAS CADASTRADAS PARA O ITEM PROCEDIMENTO HOSPITALAR.",
								DominioModuloCompetencia.AMB.toString(), null,
								null, null, null, null, new Date(),
								null, null, pIphPhoSeq, null, pIphSeq, null,
								pPacCodigo, null, null, pPmrSeq, null, null,
								null, null, null, null, null, null, null,
								RN_PMRC_ATU_REGRAS, null, null, new FatMensagemLog(95));
				retorno = false;
			}
		}
		return retorno;
	}

	/**
	 * <p>
	 * ORADB: <code>FATP_APAC_DIARIA</code>
	 * </p>
	 * PROCEDURE FATP_APAC_DIARIA
	 * @param dataFimVinculoServidor 
	 * 
	 * 
	 */
	@SuppressWarnings("ucd")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void fatpApacDiaria(String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		final FatCompetenciaDAO competenciaDAO = getFatCompetenciaDAO();
		final FatContaApacDAO contaApacDAO = getFatContaApacDAO();
		List<FatCompetencia> competencias = competenciaDAO
				.obterListaAtivaPorModulo(DominioModuloCompetencia.APAN);
		armazenarAtributoApacDiaria(true);
		int contador = 0;
		// -- Associa as consultas as APACS
		for (FatCompetencia fatCompetencia : competencias) {
			List<FatContaApac> fatContaApacs = contaApacDAO
					.buscarFatContaApacAtivaPorModuloDtInicioMesAno(
							fatCompetencia.getId().getModulo(), fatCompetencia
									.getId().getDtHrInicio(), fatCompetencia
									.getId().getMes().byteValue(),
							fatCompetencia.getId().getAno().shortValue());
			for (FatContaApac fatContaApac : fatContaApacs) {
				int associadas = getFatkSusRn().associarConsultaApac(
						fatContaApac.getId(),
						fatCompetencia.getId().getDtHrInicio(),
						fatCompetencia.getId().getModulo(),
						fatCompetencia.getId().getMes().byteValue(),
						fatCompetencia.getId().getAno().shortValue(),
						nomeMicrocomputador, dataFimVinculoServidor);
				LOG.debug("associou " + associadas + " consultas.");
				contador += associadas;
			}
		}
		// --dbms_output.put_line('associou '||v_contador||' consultas.');
		//UserTransaction userTransaction = this.obterUserTransaction(null);

		// -- Insere principal da apac de acomp. a partir das consultas
		competencias = competenciaDAO
				.obterListaAtivaPorModulo(DominioModuloCompetencia.APAP);
		final Integer codigoApacCompanhamento = this
				.buscarAghParametro(
						AghuParametrosEnum.P_NUMERO_APACS_ACOMPANHAMENTO)
				.getVlrNumerico().intValue();
		for (FatCompetencia fatCompetencia : competencias) {
			List<FatContaApac> fatContaApacs = contaApacDAO
					.buscarFatContaApacAtivaPorModuloDtInicioMesAno(
							fatCompetencia.getId().getModulo(), fatCompetencia
									.getId().getDtHrInicio(), fatCompetencia
									.getId().getMes().byteValue(),
							fatCompetencia.getId().getAno().shortValue());
			for (FatContaApac fatContaApac : fatContaApacs) {
				fatpInserePrh(fatContaApac.getPaciente().getCodigo(),
						fatContaApac.getAacAtendimentoApacs().getNumero(),
						fatContaApac.getAacAtendimentoApacs()
								.getIndTipoTratamento().intValue(),
						fatContaApac.getDtInicioValidade(),
						fatContaApac.getDtFimValidade(),
						codigoApacCompanhamento, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			}
		}

		armazenarAtributoApacDiaria(true);
		//this.commitUserTransaction(userTransaction);
	}

	private FatContaApacDAO getFatContaApacDAO() {
		return fatContaApacDAO;
	}

	private void armazenarAtributoApacDiaria(Boolean valor) {
//		atribuirContextoSessao(VariaveisSessaoEnum.AACK_PRH_RN_V_APAC_DIARIA,
//				valor == null ? Boolean.FALSE : valor);
	}

	// private Boolean obterAtributoExecAgrupamento() {
	// Object obj =
	// obterContextoSessao(VariaveisSessaoEnum.AACK_PRH_RN_V_APAC_DIARIA);
	// if (obj == null) {
	// return Boolean.FALSE;
	// }
	// return (Boolean) obj;
	// }
	//
	/**
	 * <p>
	 * ORADB: <code>FATP_INSERE_PRH</code>
	 * </p>
	 * PROCEDURE FATP_INSERE_PRH (P_PAC_CODIGO IN NUMBER ,P_ATM_NUMERO IN NUMBER
	 * ,P_TIPO_TRATAMENTO IN NUMBER ,P_DT_INI_CONTA IN DATE ,P_DT_FIM_CONTA IN
	 * DATE )
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws BaseException
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void fatpInserePrh(final Integer codigoPaciente,
			final Long atmNumero, final Integer tipoTratamento,
			final Date dataInicio, final Date dataFim,
			final Integer codigoApacCompanhamento, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			FatVariaveisVO fatVariaveisVO) throws BaseException {
		
		final FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
		final IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
		final ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
		//int contador = 0;
		//UserTransaction userTransaction = obterUserTransaction(null);
		// int contadorErros = 0;
		if (codigoApacCompanhamento == tipoTratamento) {
			// -- para apacs de acompanhamento
			List<FatConsultaPrhVO> consultaPrhVOs = ambulatorioFacade
					.buscarFatConsultaPrhVOAcompanhamento(codigoPaciente,
							atmNumero, dataInicio, dataFim);

			for (FatConsultaPrhVO fatConsultaPrhVO : consultaPrhVOs) {
				final AacConsultaProcedHospitalar consultaProcedHospitalar = ambulatorioFacade
						.obterConsultaProcedHospitalar(new AacConsultaProcedHospitalarId(
								fatConsultaPrhVO.getNumeroConsulta(),
								fatConsultaPrhVO.getPhiSeq()));
				if (consultaProcedHospitalar != null) {
					//contador++;
					// --dbms_output.put_line('vai incluir prh');
					try {
						final AacConsultaProcedHospitalar aacConsultaProcedHospitalar = new AacConsultaProcedHospitalar(
								new AacConsultaProcedHospitalarId(
										fatConsultaPrhVO.getNumeroConsulta(),
										fatConsultaPrhVO.getPhiSeq()),
								(byte) 1, false);
						ambulatorioFacade
								.inserirAacConsultaProcedHospitalar(aacConsultaProcedHospitalar, nomeMicrocomputador);
					} catch (Exception e) {
						LOG.debug("nao inseriu na prh" + e.getMessage());
						// contadorErros++;
						inserirMsgErro(
								"Procedimento " + fatConsultaPrhVO.getPhiSeq()
										+ " não incluido para "
										+ fatConsultaPrhVO.getNumeroConsulta(),
								atmNumero, codigoPaciente,
								fatConsultaPrhVO.getPhiSeq());
					}
					final Integer phiSeq = ambulatorioFacade
							.obterPhiSeqPorNumeroConsulta(
									fatConsultaPrhVO.getNumeroConsulta(), true);
					final List<FatProcedAmbRealizado> ambRealizados = ambRealizadoDAO
							.buscarPorAacConsultaProcedHospitalarOrigemSituacao(
									fatConsultaPrhVO.getNumeroConsulta(),
									phiSeq,
									DominioSituacaoProcedimentoAmbulatorio.APRESENTADO,
									DominioOrigemProcedimentoAmbulatorialRealizado.CON);
					for (FatProcedAmbRealizado fatProcedAmbRealizado : ambRealizados) {
						final FatProcedAmbRealizado old = ambRealizadosON
								.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
						fatProcedAmbRealizado
								.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
						ambRealizadosON
								.atualizarProcedimentoAmbulatorialRealizado(
										fatProcedAmbRealizado, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
					}
				}
			}
		} else {
			List<FatConsultaPrhVO> consultaPrhVOs = ambulatorioFacade
					.buscarFatConsultaPrhVO(codigoPaciente, atmNumero,
							tipoTratamento, dataInicio, dataFim);
			for (FatConsultaPrhVO fatConsultaPrhVO : consultaPrhVOs) {
				final AacConsultaProcedHospitalar consultaProcedHospitalar = ambulatorioFacade
						.obterConsultaProcedHospitalar(new AacConsultaProcedHospitalarId(
								fatConsultaPrhVO.getNumeroConsulta(),
								fatConsultaPrhVO.getPhiSeq()));
				if (consultaProcedHospitalar != null) {
					//contador++;
					LOG.debug("vai incluir prh");
					final AacConsultaProcedHospitalar aacConsultaProcedHospitalar = new AacConsultaProcedHospitalar(
							new AacConsultaProcedHospitalarId(
									fatConsultaPrhVO.getNumeroConsulta(),
									fatConsultaPrhVO.getPhiSeq()), (byte) 1,
							false,
							getAghuFacade().obterAghCidsPorChavePrimaria(fatConsultaPrhVO
									.getCidSeq()));
					ambulatorioFacade
							.inserirAacConsultaProcedHospitalar(aacConsultaProcedHospitalar, nomeMicrocomputador);
				}
			}
		}
		//if (contador > 0) {
		//	commitUserTransaction(userTransaction);
		//}
		// IF v_conta_erro > 0 THEN
		// aghp_envia_email ('T','Verificar log erros rotina diária',
		// 'Erro rotina diária!',
		// 'mpacheco@hcpa.ufrgs.br mdelazzeri@hcpa.ufrgs.br
		// ncorrea@hcpa.ufrgs.br');
		// v_conta_erro := 0;
		// END IF;
	}

	private void inserirMsgErro(final String msgError, final Long atmNumero,
			final Integer codigoPaciente, final Integer phiSeq) {
		getFaturamentoON().criarFatLogErrors(msgError, "DIAR", null, atmNumero,
				null, null, null, null, null, null, null, null, null, null,
				codigoPaciente, phiSeq, null, null, null, null, null, null,
				null, null, null, null, null, "fatp_insere_prh", null, null, null);
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_PMR_RN.RN_PMRP_ATU_COMP_FUT</code>
	 * </p>
	 * PROCEDURE RN_PMRP_ATU_COMP_FUT (P_CPE_DTHR_INICIO IN DATE, P_CPE_MES IN
	 * NUMBER, P_CPE_ANO IN NUMBER, P_CPE_DTHR_FIM IN DATE,
	 * P_NEW_CPE_DTHR_INICIO IN DATE, P_NEW_CPE_MES IN NUMBER, P_NEW_CPE_ANO IN
	 * NUMBER )
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws BaseException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rnPmrpAtuCompetenciaFutura(final Date dataInicio,
			final Date cpeDthrFim, final Byte mes, final Short ano,
			final Date novaDataInicio, final Byte novoMes, final Short novoAno, String nomeMicrocomputador, 
			Date dataFimVinculoServidor,
			final AghJobDetail log, FatVariaveisVO fatVariaveisVO)
			throws BaseException {
		
		String logTxt = "##### AGHU - Movendo procedimentos para outra competência " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS); 
		LOG.debug(logTxt);
		getSchedulerFacade().adicionarLog(log, logTxt);
		
		// -- seleciona os procedimentos com data de realizacao futura
		//UserTransaction userTransaction = obterUserTransaction(null);

		Long nrRegistros = getFatProcedAmbRealizadoDAO().buscarQuantidadeProcAmbPorDataRealizacaoConvenio(dataInicio, cpeDthrFim, mes,
				ano);
		Integer count = 0;
				
		LOG.debug("p_cpe_dthr_inicio = " + DateUtil.obterDataFormatada(dataInicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS) + " p_cpe_dthr_fim = " + DateUtil.obterDataFormatada(cpeDthrFim, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS) + " cpe_mes = " + mes + " cpe_ano = " + ano);    
		LOG.debug("p_new_cpe_dthr_inicio = " + DateUtil.obterDataFormatada(novaDataInicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS) + " p_new_cpe_mes = " + novoMes + " p_new_cpe_ano = " + novoAno);
		FatCompetencia competencia =  getFatCompetenciaDAO().obterPorChavePrimaria(new FatCompetenciaId(DominioModuloCompetencia.AMB, novoMes.intValue(), novoAno.intValue(), novaDataInicio));

		for(count=0;count<nrRegistros;count+=TAMANHO_MAXIMO_LOTE_COMPETENCIA) {
						
			atualizarCompetenciaProcedRealizados(dataInicio, cpeDthrFim, mes, ano, nomeMicrocomputador,
					dataFimVinculoServidor, fatVariaveisVO, competencia);
			LOG.debug("Iniciou clear da movimentação para bloco de "+TAMANHO_MAXIMO_LOTE_COMPETENCIA+" reg.");
			getFaturamentoFacade().clearSemFlush(); 
			LOG.debug("fim do clear da movimentação para bloco de "+TAMANHO_MAXIMO_LOTE_COMPETENCIA+" reg.");
		}
			
		logTxt = "##### AGHU - Fim do movimento dos procedimentos " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS); 
		LOG.debug(logTxt);
		getSchedulerFacade().adicionarLog(log, logTxt);

		LOG.debug("atualizou no total = " + nrRegistros);
		//commitUserTransaction(userTransaction);
		getFaturamentoFacade().clearSemFlush();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void atualizarCompetenciaProcedRealizados(
			final Date dataInicio, final Date cpeDthrFim, final Byte mes, final Short ano,
			String nomeMicrocomputador, Date dataFimVinculoServidor,
			FatVariaveisVO fatVariaveisVO, FatCompetencia competencia) throws BaseException {
		
		List<FatProcedAmbRealizado> fatProcedAmbRealizadosPendentes = getFatProcedAmbRealizadoDAO()
				.buscarPorDataRealizacaoConvenio(dataInicio, cpeDthrFim, mes,ano,0,TAMANHO_MAXIMO_LOTE_COMPETENCIA);
		
		for (FatProcedAmbRealizado fatProcedAmbRealizado : fatProcedAmbRealizadosPendentes) {
			final FatProcedAmbRealizado old = getProcedimentosAmbRealizadosON()
					.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);

			// -- passa os procedimentos para a proxima competencia
			fatProcedAmbRealizado.setFatCompetencia(competencia);
			fatProcedAmbRealizado.setIndPendente(null);
			getProcedimentosAmbRealizadosON().atualizarProcedimentoAmbulatorialRealizado(
					fatProcedAmbRealizado, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			//LOG.debug("atualizou seq = " + fatProcedAmbRealizado.getSeq());
			// -- where CURRENT OF c_proc_amb;RN_PMRP_ATU_GERA_ESP
		}
		// -- commit a cada TAMANHO_MAXIMO_LOTE_COMPETENCIA
		LOG.debug("Commit do bloco de "+TAMANHO_MAXIMO_LOTE_COMPETENCIA+" reg.");
		//userTransaction = commitUserTransaction(userTransaction);
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_PMR_RN.RN_PMRP_ATU_SITUACAO</code>
	 * </p>
	 * PROCEDURE RN_PMRP_ATU_SITUACAO (P_PREVIA IN VARCHAR2 ,P_SITUACAO IN
	 * VARCHAR2 ,P_SEQ IN NUMBER )
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws BaseException
	 */

	public void rnPmrpAtuSituacao( final Long seq, final Boolean previa,
								   final DominioSituacaoProcedimentoAmbulatorio situacaoProcedimentoAmbulatorio, 
								   final String nomeMicrocomputador, 
								   final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		
		if (!previa) {
			FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
			ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
			FatProcedAmbRealizado ambRealizado = ambRealizadoDAO
					.buscarPorSeqSituacao(seq, DominioSituacaoProcedimentoAmbulatorio.ABERTO);
			if (ambRealizado != null) {
				final FatProcedAmbRealizado old = ambRealizadosON.clonarFatProcedAmbRealizado(ambRealizado);

				ambRealizado.setSituacao(situacaoProcedimentoAmbulatorio);
				ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado(
						ambRealizado, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
				ambRealizadoDAO.flush();
			}
		}
		if (DominioSituacaoProcedimentoAmbulatorio.CANCELADO.equals(situacaoProcedimentoAmbulatorio)) {
			FatEspelhoProcedAmbDAO ambDAO = getFatEspelhoProcedAmbDAO();
			List<FatEspelhoProcedAmb> ambs = ambDAO.buscarPorPmrSeq(seq);
			if (ambs != null && !ambs.isEmpty()) {
				for (FatEspelhoProcedAmb fatEspelhoProcedAmb : ambs) {
					ambDAO.remover(fatEspelhoProcedAmb);
				}
				ambDAO.flush();
			}
		}
	}

	/**
	 * 
	 *	  1. Consultas Emergência:
	 *	  Buscar a Unidade Funcional da grade da consulta.
	 *	  Se    característica da UF = Emergência
	 *	  e  data realizado > data OK consulta então:
	 *	  - a partir do atendimento da consulta (AGH_ATENDIMENTOS) deixar pendentes
	 *	  de faturamento os demais itens deste atendimento - ver item 3
	 *	  2. Pacientes em S.O.:
	 *	  Buscar os atendimentos de urgência (AIN_ATENDIMENTO_URGENCIA) sem data de alta
	 *	  e IND_PACIENTE_EM_ATENDIMENTO='S' e
	 *	  - a partir do atendimento (AGH_ATENDIMENTOS) deixar pendentes de faturamento
	 *	  os demais itens deste atendimento - ver item 3
	 *	  3. Deixar pendentes os procedimentos do atendimento
	 *	  A partir da FK de atendimentos na FAT_PROCED_AMB_REALIZADOS, deixar pendentes
	 *	  todos procedimentos que possuem mesmo atendimento.
	 * 
	 * 
	 * ORADB: <code>FATK_PMR_RN.RN_PMRP_ATU_PENDENTES</code>
	 * </p>
	 * PROCEDURE RN_PMRP_ATU_PENDENTES (P_CPE_DT_HR_INICIO IN DATE ,P_CPE_MES IN
	 * NUMBER ,P_CPE_ANO IN NUMBER
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws BaseException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rnPmrpAtuPendentes(final Date dataInicio, final Byte mes,
			final Short ano, String nomeMicrocomputador, Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		
		LOG.debug("Liberando dados marcados como pendente");
		FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
		ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();

		List<FatProcedAmbRealizado> fatProcedAmbRealizadosPendentes = ambRealizadoDAO.buscarPendentesPorDtInicioAnoMes(dataInicio, mes, ano);
		
		for (FatProcedAmbRealizado fatProcedAmbRealizado : fatProcedAmbRealizadosPendentes) {
			final FatProcedAmbRealizado old = ambRealizadosON.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
			fatProcedAmbRealizado.setIndPendente(null);
			ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
		}

		LOG.debug("##### AGHU - update fat_proced_amb_realizados afetou = " + fatProcedAmbRealizadosPendentes.size() + " usando p_cpe_dt_hr_inicio = " + DateUtil.obterDataFormatada(dataInicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS) + " p_cpe_mes = " + mes + " p_cpe_ano = " + ano);  

		FatCompetencia fatCompetencia = getFatCompetenciaDAO().obterCompetenciaModuloMesAnoDtHoraInicioSemHora(DominioModuloCompetencia.AMB, mes.intValue(), ano.intValue(), dataInicio);
		
		Date dataConsulta = fatCompetencia.getDthrLiberadoEmerg() == null ? new Date() : fatCompetencia.getDthrLiberadoEmerg();

		LOG.debug("Data de ok ==>" + DateUtil.obterDataFormatada(dataConsulta, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
		LOG.debug("marca como pendente as consultas de emergencia");
		
		//final IParametroFacade parametroFacade = getParametroFacade();
		//final String caracteristica = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_FUNCIONAL_EMERGENCIA).getVlrTexto();
		
		List<FatProcedAmbRealizado> fatProcedAmbRealizados = ambRealizadoDAO.buscarAbertosPorDtInicioAnoMesUnid(dataInicio, mes, ano, dataConsulta, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		for (FatProcedAmbRealizado fatProcedAmbRealizado : fatProcedAmbRealizados) {
			final List<FatProcedAmbRealizado> fatProcedAmbRealizadosAtualiza = ambRealizadoDAO.buscarPorAtendimento(fatProcedAmbRealizado.getAtendimentoSeq());
			for (FatProcedAmbRealizado fatProcedAmbRealizado2 : fatProcedAmbRealizadosAtualiza) {
				final FatProcedAmbRealizado old = ambRealizadosON.clonarFatProcedAmbRealizado(fatProcedAmbRealizado2);
				fatProcedAmbRealizado2.setIndPendente(true);
				ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado2, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			}
		}

		LOG.debug("##### AGHU - c_proced_amb_emrg loop = " + fatProcedAmbRealizados.size() + " v_data_lib_emerg = " + DateUtil.obterDataFormatada(dataConsulta, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));  

		LOG.debug("marca como pendente itens de pacs em atendimento");

		fatProcedAmbRealizados = ambRealizadoDAO.buscarPorPacienteEmAtendimento(dataInicio, mes, ano, dataConsulta);
		int cont = 0;
		for (FatProcedAmbRealizado fatProcedAmbRealizado : fatProcedAmbRealizados) {
			List<FatProcedAmbRealizado> fatProcedAmbRealizadosAtualiza = ambRealizadoDAO.buscarPorAtendimento(fatProcedAmbRealizado.getAtendimento().getSeq());
			for (FatProcedAmbRealizado fatProcedAmbRealizado2 : fatProcedAmbRealizadosAtualiza) {
				cont++;
				final FatProcedAmbRealizado old = ambRealizadosON.clonarFatProcedAmbRealizado(fatProcedAmbRealizado2);
				fatProcedAmbRealizado2.setIndPendente(true);
				ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado2, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			}
		}

		LOG.debug("##### AGHU - c_proced_amb_em_atd loop = " + fatProcedAmbRealizados.size() + " v_data_lib_emerg = " + DateUtil.obterDataFormatada(dataConsulta, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));  
		LOG.debug("##### AGHU - c_proced_amb_em_atd atualizou no loop = " + cont);  
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_PMR.PROCESS_PMR_ROWS('UPDATE')</code>
	 * </p>
	 * PROCEDURE process_pmr_rows(p_event IN VARCHAR2 )
	 * 
	 * @throws BaseException
	 */

	public void processPmrUpdate(final FatProcedAmbRealizado novo,
			final FatProcedAmbRealizado antigo) throws BaseException {
		fatpEnforcePmrRules(novo, antigo);
	}

	/**
	 * <p>
	 * ORADB: <code>FATP_ENFORCE_PMR_RULES</code>
	 * </p>
	 * PROCEDURE FATP_ENFORCE_PMR_RULES( l_pmr_saved_row IN
	 * fatk_pmr.pmr_saved_row_type , l_pmr_row_new IN fatk_pmr.pmr_row_type ,
	 * p_event IN VARCHAR2 )
	 * 
	 * @throws BaseException
	 */
	private void fatpEnforcePmrRules(final FatProcedAmbRealizado novo,
			final FatProcedAmbRealizado antigo) throws BaseException {
		if (DominioSituacaoProcedimentoAmbulatorio.CANCELADO.equals(novo
				.getSituacao())
				&& !novo.getSituacao().equals(antigo.getSituacao())) {
			FatItemContaApacON fatItemContaApacON = getFatItemContaApacON();

			FatItemContaApacDAO itemContaApacDAO = getFatItemContaApacDAO();
			List<FatItemContaApac> itens = itemContaApacDAO
					.obterItemContaApacPorPmrSeq(novo.getSeq());

			for (FatItemContaApac fatItemContaApac : itens) {
				FatItemContaApac oldItemCtaApac = fatItemContaApacON
						.cloneFatItemContaApac(fatItemContaApac);
				fatItemContaApac.setIndSituacao(DominioSituacaoItemContaApac.C);
				fatItemContaApacON.persistirItemContaApac(fatItemContaApac,
						oldItemCtaApac, false);
			}
		}
	}

	/**
	 * ORADB Procedure RN_PMRP_ATU_APAC_FUT
	 * 
	 * @param pPrevia
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Boolean rnPmrpAtuApacFut(Boolean pPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		// vars
		Integer vCont;
		Integer vCount;
		Date vCompInicio;
		Integer vCompAno;
		Integer vCompMes;
		Date vCompAntInicio;
		Integer vCompAntAno;
		Integer vCompAntMes;
		DominioModuloCompetencia vCompModulo;
		Integer vMesTodos;
		// Integer vIphPhoSeq;
		// Integer vIphQtdItem;
		// Integer vIphSeq;
		// Integer vQtdItens;
		Integer vIndice;
		// DominioModuloCompetencia vModulo;
		Boolean vPrincApac;
		// Date vDataNova;
		Integer vPhiDuploJ = null;
		Boolean vCobraBpa = null;
		Boolean vCobraBpi = false;
		Boolean vExigeCns = false;
		Boolean vCobraApac = false;
		Integer vTctTipoApac;
		// Integer valNum;
		String valChar;
		// Date valData;
		Boolean result = false;
		//Short vSerVinCodigoRet = null;
		//Integer vSerMatrRet = null;
		Long vCnsMedico = null;

		// DAOs
		FatCompetenciaDAO competenciaDAO = getFatCompetenciaDAO();
		FatProcedAmbRealizadoDAO procedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		FatItensProcedHospitalarDAO itensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		FaturamentoRN faturamentoRN = getFaturamentoRN();
		ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON = getProcedimentosAmbRealizadosON();
		FatProcedTratamentosDAO procedTratamentosDAO = getFatProcedTratamentosDAO();

		// -- busca codigo do cateter duplo J
		vPhiDuploJ = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_CATETER_DUPLO_J);

		if (pPrevia == null) {
			pPrevia = true;
		}

		LOG.debug("previa " + pPrevia.booleanValue());

		if (!pPrevia) { // -- deverá ser chamada somente no encerramento,teste
						// feito por segurança
			LOG.debug("Processo atualiza princ. apac/BPI próxima competência, fat_log_enc_previa.txt");

			// -- procura competência aberta apacs
			List<FatCompetencia> listaCompetencias = competenciaDAO
					.listarCompetenciasPorModulosESituacao(
							DominioSituacaoCompetencia.A,
							DominioModuloCompetencia.APAP,
							DominioModuloCompetencia.APAN,
							DominioModuloCompetencia.APEX,
							DominioModuloCompetencia.APAC,
							DominioModuloCompetencia.APAR,
							DominioModuloCompetencia.APAT,
							DominioModuloCompetencia.AMB);

			vCount = 0;
			for (FatCompetencia competencia : listaCompetencias) {
				vCompModulo = null;
				vCompInicio = null;
				vCompAno = null;
				vCompMes = null;
				vCompAntInicio = null;
				vCompAntAno = null;
				vCompAntMes = null;

				vCompModulo = competencia.getId().getModulo();
				vCompInicio = competencia.getId().getDtHrInicio();
				vCompAno = competencia.getId().getAno();
				vCompMes = competencia.getId().getMes();

				vMesTodos = competencia.getId().getMes() - 1;
				vCount++;
				LOG.debug("modulo " +  vCompModulo + " " + vCount);

				List<FatCompetencia> listaCompetAnterior = competenciaDAO
						.obterCompetenciasPorModuloESituacaoEFaturadoEMesEAno(
								vCompModulo, DominioSituacaoCompetencia.P,
								true,
								FatCompetencia.Fields.DT_HR_INICIO.toString(),
								false, vMesTodos == 0 ? 12 : vMesTodos,
								vMesTodos == 0 ? vCompAno - 1 : vCompAno);

				if (listaCompetAnterior.isEmpty()) {
					throw new ApplicationBusinessException(
							FaturamentoFatkPmrRNExceptionCode.MSG_NAO_ENCONTRADA_COMPETENCIA_ANTERIOR,
							vCompModulo);
				} else {
					FatCompetencia comptAnterior = listaCompetAnterior.get(0);
					vCompAntInicio = comptAnterior.getId().getDtHrInicio();
					vCompAntAno = comptAnterior.getId().getAno();
					vCompAntMes = comptAnterior.getId().getMes();
				}
			}

			if (vCount == 0) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00808);
			}

			vCompModulo = null;
			vCompInicio = null;
			vCompAno = null;
			vCompMes = null;
			vCompAntInicio = null;
			vCompAntAno = null;
			vCompAntMes = null;

			listaCompetencias = competenciaDAO
					.obterCompetenciasPorModuloESituacoes(
							DominioModuloCompetencia.AMB,
							DominioSituacaoCompetencia.A); // --
															// procura
															// última
															// competência
															// aberta
															// módulo
															// amb

			if (listaCompetencias.isEmpty()) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00806);
			} else {
				FatCompetencia competencia = listaCompetencias.get(0);
				vCompModulo = competencia.getId().getModulo();
				vCompInicio = competencia.getId().getDtHrInicio();
				vCompAno = competencia.getId().getAno();
				vCompMes = competencia.getId().getMes();
			}

			List<FatCompetencia> listaCompetAnterior = competenciaDAO
					.obterCompetenciasPorModuloESituacaoEFaturadoEMesEAno(
							DominioModuloCompetencia.AMB,
							DominioSituacaoCompetencia.P, true,
							FatCompetencia.Fields.DT_HR_INICIO.toString(),
							false, null, null); // --
												// procura
												// última
												// competência
												// encerrada
												// módulo
												// amb

			if (listaCompetAnterior.isEmpty()) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_00807);
			} else {
				FatCompetencia comptAnterior = listaCompetAnterior.get(0);
				vCompAntInicio = comptAnterior.getId().getDtHrInicio();
				vCompAntAno = comptAnterior.getId().getAno();
				vCompAntMes = comptAnterior.getId().getMes();
			}

			List<FatProcedAmbRealizado> listaProcedAmbRealizado = procedAmbRealizadoDAO
					.listarProcedAmbRealizadoPorSituacaoModuloDthrInicioMesAnoGrupoConvenio(
							DominioSituacaoProcedimentoAmbulatorio.ABERTO,
							DominioModuloCompetencia.AMB, vCompAntInicio,
							vCompAntMes, vCompAntAno, DominioGrupoConvenio.S);

			LOG.debug("rows pmr " + listaProcedAmbRealizado.size());
			vCont = 0;
			//UserTransaction userTransaction = obterUserTransaction(null);
			for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {
				// userTransaction = null;
				// -- verifica se é principal de apac ou se é o cateter duplo j
				// -- e somente neste caso transfere competência
				vIndice = -1;

				List<RnFatcVerItprocexcVO> listaRnFatcVerItprocexcVO = getVerificacaoFaturamentoSusRN()
						.verificarItemProcHosp(
								procedAmbRealizado
										.getProcedimentoHospitalarInterno()
										.getSeq(),
								procedAmbRealizado.getQuantidade(),
								procedAmbRealizado.getConvenioSaudePlano()
										.getId().getCnvCodigo(),
								procedAmbRealizado.getConvenioSaudePlano()
										.getId().getSeq());

				if (listaRnFatcVerItprocexcVO != null
						&& listaRnFatcVerItprocexcVO.size() > 0) {
					vIndice++;
					if (listaRnFatcVerItprocexcVO.size() == vIndice.intValue()) {
						continue;
					}
					List<FatItensProcedHospitalar> listarItemProcedHosp = itensProcedHospitalarDAO
							.listarItemProcedHospPorPhoSeqPorSeqPorPorSituacao(
									listaRnFatcVerItprocexcVO.get(vIndice)
											.getPhoSeq(),
									listaRnFatcVerItprocexcVO.get(vIndice)
											.getSeq(), DominioSituacao.A);

					for (FatItensProcedHospitalar itemProcedHospitalar : listarItemProcedHosp) {
						vPrincApac = itemProcedHospitalar
								.getProcPrincipalApac();
						vCobraBpa = false;
						vCobraBpi = false;
						vExigeCns = false;
						vCobraApac = false;

						vCobraBpa = faturamentoRN
								.verificarCaracteristicaExame(
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getSeq(),
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getPhoSeq(),
										DominioFatTipoCaractItem.COBRA_BPA);

						vCobraBpi = faturamentoRN
								.verificarCaracteristicaExame(
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getSeq(),
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getPhoSeq(),
										DominioFatTipoCaractItem.COBRA_BPI);

						vExigeCns = faturamentoRN
								.verificarCaracteristicaExame(
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getSeq(),
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getPhoSeq(),
										DominioFatTipoCaractItem.EXIGE_CNS_PACIENTE);

						vCobraApac = faturamentoRN
								.verificarCaracteristicaExame(
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getSeq(),
										listaRnFatcVerItprocexcVO.get(vIndice)
												.getPhoSeq(),
										DominioFatTipoCaractItem.COBRA_APAC);

						if (Boolean.TRUE.equals(vCobraBpi) && Boolean.TRUE.equals(vExigeCns)) {
							LOG.debug("vai passar " + listaRnFatcVerItprocexcVO.get(vIndice).getPhoSeq() + " pmr " + listaRnFatcVerItprocexcVO.get(vIndice).getSeq());
						}
						
						//vSerVinCodigoRet = null;
						//vSerMatrRet = null;
						vCnsMedico = null; 
						if (procedAmbRealizado.getServidorResponsavel() != null) {
							CnsResponsavelVO cnsResponsavelVO = faturamentoRN.fatcBuscaCnsResp(
									procedAmbRealizado.getServidorResponsavel()
											.getId().getMatricula(),
									procedAmbRealizado.getServidorResponsavel()
											.getId().getVinCodigo(),
									procedAmbRealizado
											.getItemSolicitacaoExame().getId()
											.getSoeSeq(), procedAmbRealizado
											.getItemSolicitacaoExame().getId()
											.getSeqp(), procedAmbRealizado
											.getConsultaProcedHospitalar()
											.getId().getConNumero(),
									procedAmbRealizado.getProcEspPorCirurgia()
											.getCirurgia().getSeq());

							if (cnsResponsavelVO != null) {
								//vSerVinCodigoRet = cnsResponsavelVO.getVinCodigo();
								//vSerMatrRet = cnsResponsavelVO.getMatricula();
								vCnsMedico = cnsResponsavelVO.getItemCns();
							}
						}
						 //-- Transferir também os BPI que possuem esta característica. Milena-fev/2008

						if (Boolean.TRUE.equals(vPrincApac) || 
						   (Boolean.TRUE.equals(vCobraBpi) && Boolean.TRUE.equals(vExigeCns))  || // --2 Milena fev/2008
						   (Boolean.TRUE.equals(vCobraBpi) && vCnsMedico == null) ||
						   (Boolean.FALSE.equals(vCobraBpa) && Boolean.TRUE.equals(vCobraApac))	|| 
						   (procedAmbRealizado.getProcedimentoHospitalarInterno().getSeq().equals(vPhiDuploJ))) {

							vCont++;
							LOG.debug("proc a alterar " + procedAmbRealizado.getSeq() + " " + listaRnFatcVerItprocexcVO.get(vIndice).getSeq());
							// -- passa os procedimentos para a proxima
							// competencia

							Calendar c1 = Calendar.getInstance();
							c1.setTime(new Date());

							Calendar c2 = Calendar.getInstance();
							c2.setTime(procedAmbRealizado.getDthrRealizado());

							// v_data_nova :=
							// TO_DATE(TO_CHAR(TO_DATE(v_comp_mes,'MM'),'ddmmyyyy')||
							// TO_CHAR(reg_proc_amb.dthr_realizado,'hh24miss'),'ddmmyyyyhh24miss');
							// vDataNova =
							// DateUtil.obterData(c1.get(Calendar.YEAR),
							// vCompMes - 1, 01, c2.get(Calendar.HOUR_OF_DAY),
							// c2.get(Calendar.MINUTE));
							//
							// -- Milena - nov/2006 -- Se principal apac
							// acompanhamento ou
							// -- principal de apac = 'C' (ex. ciclosporina)
							// -- Transferir para o mês seguinte somente se foi
							// criado
							// -- na competência que está encerrando. Depois,
							// cancelá-los.
							vTctTipoApac = getTipoCaracteristicaItemRN()
									.obterTipoCaractItemSeq(
											DominioFatTipoCaractItem.PROCEDIMENTO_PRINCIPAL_APAC);

							FatCaractItemProcHosp caractItemProcHosp = getFatkCihRn()
									.obterCaracteristicaProcHospPorId(
											new FatCaractItemProcHospId(
													listaRnFatcVerItprocexcVO
															.get(vIndice)
															.getPhoSeq(),
													listaRnFatcVerItprocexcVO
															.get(vIndice)
															.getSeq(),
													vTctTipoApac));

							FatProcedAmbRealizado procedUpdate = procedAmbRealizadoDAO
									.obterPorChavePrimaria(procedAmbRealizado.getSeq());
							final FatProcedAmbRealizado old = procedimentosAmbRealizadosON
									.clonarFatProcedAmbRealizado(procedUpdate);
							result = (caractItemProcHosp != null);
							if (caractItemProcHosp != null) { // --3
								valChar = caractItemProcHosp.getValorChar();
								// valNum =
								// caractItemProcHosp.getValorNumerico();
								// valData = caractItemProcHosp.getValorData();
								if (valChar
										.equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO
												.toString())) {
									if (procedAmbRealizado.getCriadoEm()
											.before(vCompAntInicio)) {
										procedUpdate
												.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
									} else {
										procedUpdate.getFatCompetencia().getId().setDtHrInicio(vCompInicio);
										procedUpdate.getFatCompetencia().getId().setMes(vCompMes);
										procedUpdate.getFatCompetencia().getId().setAno(vCompAno);
										procedUpdate.setIndPendente(null);
									}
								} else {
									List<FatProcedTratamento> listarProcedTratamento = procedTratamentosDAO
											.listarProcedTratamentoPorPhiSeqEDescricao(
													procedAmbRealizado
															.getProcedimentoHospitalarInterno()
															.getSeq(),
													DominioModuloCompetencia.APAP
															.toString(),
													DominioModuloCompetencia.APAT
															.toString());

									if (!listarProcedTratamento.isEmpty()
											&& procedAmbRealizado.getCriadoEm()
													.before(vCompAntInicio)) {
										procedUpdate
												.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
									} else {
										procedUpdate.getFatCompetencia()
												.getId()
												.setDtHrInicio(vCompInicio);
										procedUpdate.getFatCompetencia()
												.getId().setMes(vCompMes);
										procedUpdate.getFatCompetencia()
												.getId().setAno(vCompAno);
										procedUpdate.setIndPendente(null);
									}
								}
								procedimentosAmbRealizadosON
										.atualizarProcedimentoAmbulatorialRealizado(
												procedUpdate, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							} else { // --3
//								if (vCobraBpi && vExigeCns) {
//									LOG.debug("vai passar else 3 pmr seq {0}",
//											procedAmbRealizado.getSeq());
//								}
								List<FatProcedTratamento> listarProcedTratamento = procedTratamentosDAO
										.listarProcedTratamentoPorPhiSeqEDescricao(
												procedAmbRealizado
														.getProcedimentoHospitalarInterno()
														.getSeq(),
												DominioModuloCompetencia.APAP
														.toString(),
												DominioModuloCompetencia.APAT
														.toString());

								if (!listarProcedTratamento.isEmpty()
										&& procedAmbRealizado.getCriadoEm()
												.before(vCompAntInicio)) {
									procedUpdate
											.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
								} else {
//									if (vCobraBpi && vExigeCns) {
//										LOG.debug("vai passar atualiza pmr seq {0}",
//												procedAmbRealizado.getSeq());
//									}
									procedUpdate.getFatCompetencia().getId()
											.setDtHrInicio(vCompInicio);
									procedUpdate.getFatCompetencia().getId()
											.setMes(vCompMes);
									procedUpdate.getFatCompetencia().getId()
											.setAno(vCompAno);
									procedUpdate.setIndPendente(null);
								}
								procedimentosAmbRealizadosON
										.atualizarProcedimentoAmbulatorialRealizado(
												procedUpdate, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							} // --3
						} // --2
					} // for
				} // if
				if (vCont.equals(Integer.valueOf(1000))) {
					//userTransaction = commitUserTransaction(userTransaction);
					//this.commitTransaction();
					vCont = 0;
					LOG.debug("commit de 1000 registros");
				}
			} // for
			//userTransaction = commitUserTransaction(userTransaction);
			//this.commitTransaction();
			LOG.debug("quantidade UPDATE " + vCont);
			LOG.debug("Fim atualiza princ. apac próxima competência, fat_log_enc_previa.txt");
		} // if

		return result;
	}

	/**
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>NOK</b><br/>
	 * Linhas: 68 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 1 <br/>
	 * Consultas: 1 tabelas <br/>
	 * Alteracoes: 3 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_PMRP_ATU_ENC_PMR</code>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatProcedAmbRealizado} <br/>
	 * </p>
	 * 
	 * @param pModulo
	 * @param pDthrInicio
	 * @param pMes
	 * @param pAno
	 * @param isPrevia
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException
	 * @see FatItemContaApac
	 * @see FatProcedAmbRealizado
	 */
	@SuppressWarnings("ucd")
	public void rnPmrpAtuEncPmr(final DominioModuloFatContaApac pModulo,
			final Date pDthrInicio, final Byte pMes, final Short pAno,
			final boolean isPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		LOG.debug("previa " + isPrevia);
		// -- deverá ser chamada somente no encerramento,teste feito por
		// segurança
		if (!isPrevia) {
			final FatItemContaApacDAO itemContaApacDAO = getFatItemContaApacDAO();
			// aghp_grava_mensagem ('Processo encerra PMR(P)competência
			// '||p_modulo
			// ,'fat_log_enc_previa.txt','N');
			// aghp_envia_email ('A','fat_log_enc_previa.txt',
			// 'Processo encerra PMR(P)competência '||p_modulo,ListaEmails);
			final List<FatItemContaApac> itemContaApacs = itemContaApacDAO
					.buscarItensConta(pModulo, pDthrInicio, pMes, pAno);
			if (itemContaApacs != null && !itemContaApacs.isEmpty()) {
				//final UserTransaction userTx = obterUserTransaction(null);
				final FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
				final ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
				for (final FatItemContaApac fatItemContaApac : itemContaApacs) {
					final FatProcedAmbRealizado ambRealizado = ambRealizadoDAO
							.buscarEncerramentoPmr(fatItemContaApac
									.getProcedimentoAmbRealizado().getSeq(),
									DominioLocalCobrancaProcedimentoAmbulatorialRealizado
											.valueOf(fatItemContaApac
													.getLocalCobranca()),
									pDthrInicio, pMes, pAno);
					final FatProcedAmbRealizado old = ambRealizadosON
							.clonarFatProcedAmbRealizado(ambRealizado);

					ambRealizado
							.setSituacao(DominioSituacaoProcedimentoAmbulatorio.APRESENTADO);
					ambRealizadosON.atualizarProcedimentoAmbulatorialRealizado(
							ambRealizado, old, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
				}
				//commitUserTransaction(userTx);
				//this.commitTransaction();
			}
			// aghp_grava_mensagem ('Processo encerra PMR(P)competência - FIM
			// '||p_modulo
			// ,'fat_log_enc_previa.txt','N');
			// aghp_envia_email ('A','fat_log_enc_previa.txt',
			// 'Processo encerra PMR(P)competência - fim
			// '||p_modulo,ListaEmails);
		}
	}

	/**
	 * ORADB FATK_PMR_RN.RN_PMRP_GERA_NEW
	 * 
	 * @param competenciaParam
	 * @param pCpeDtFim
	 * @param pPrevia
	 * @param log
	 * @param dataFimVinculoServidor 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount","PMD.NPathComplexity", "ucd"})
	void geraEspelhoFaturamentoAmbulatorio(final FatCompetencia competenciaParam, Date pCpeDtFim, Boolean pPrevia, final AghJobDetail log, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//-- Milena outubro/2002 setando segmento rbs_large para on-line
		//--   fatk_sus_rn.rn_fatp_seta_segment; -- comentado por Milena junho/2004

		//tamanho do bloco de registros que irá buscar no banco de dados, padrão 1000 registros
		final Integer FETCH_SIZE = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_TAMANHO_BLOCO_PROCESS_FAT_AMB);
		final String DEFAULT_NULL = "default null";
		final String DIA = "DIA";
		final String MES = "MES";
		final String CBO = "CBO";
		
		final Date pCpeDtHrInicio = competenciaParam.getId().getDtHrInicio();
		final Integer pCpeMes = competenciaParam.getId().getMes();
		final Integer pCpeAno = competenciaParam.getId().getAno();
		
		//Vars
		Date vDataPrevia = null;
		Date vDtFimCpe = null;
		Date vMaxDthrRealiz = null;
		Date vCpeDthrFim = null;
		Date vNewCpeDthrInicio = null;
		Date vCpeCompetencia = null;
		
		Integer vNewCpeMes = null;
		Integer vNewCpeAno = null;
		Integer vContador = 0;
		Short vIdade = null;
		Integer vPhiDuploJ = null;
		Integer vIndice = null;
		Short vQtdItens = null;
		Short vIphQtdItem = null;
		Short vIphPhoSeq = null;
		Integer vIphSeq = null;
		Integer vCfeCodSus = null;
		Integer vCfeCodSusItem = null;
		Integer vCaraterAtendimento = null;
		Integer vQtdeMaxDia = null;
		Integer vQtdeMaxMes = null;
		Integer vQtdeEspelho = 0;
		Integer vAcumulaDia = null;
		Integer vAcumulaMes = null;
		//Integer ind = 0;
		Integer vPhiTerapia = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_TERAPIA);
		Integer vDiasApacAparelho = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_DIAS_APAC_APARELHO);
		Integer vCompetencia = Integer.parseInt(pCpeAno.toString().concat(pCpeMes < 10 ? "0" + pCpeMes.toString() : pCpeMes.toString()));
		Short cnvCodigo = buscarVlrShortAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		BigDecimal vCodIbge = null;
		
		Long vIphCodTab = null;
		Integer vIphFccSeq = null;
		Integer vIphFcfSeq = null;
		DominioSexoDeterminante vSexo = null;
		Integer vIdadeMin = null;
		Integer vIdadeMax = null;
		Integer vCavCodSus = null;
		Byte vGraCodSus = null;
		
		Boolean vConsistente = null;
		Boolean vPrimVez = true;
		Boolean vIndConsulta = null;
		Boolean vPrincApac = null;
		Boolean vCobraBpa = null;
		Boolean vCobraApac = null;
		Boolean vCobraBpi = null;
		Boolean regrasOk = null;
		Boolean vAchou = Boolean.FALSE;
		Boolean vAchouMes = Boolean.FALSE;
		Boolean vExigeCnsPac = null;
		Boolean vProcExigeCid = null;
		Boolean vCalculaIdade = null;
		Boolean idadePac = null;
		DominioSexoDeterminante vRestricaoSexo = null;
		
		String vErroLog = null;
		Short vSerVinCodigoRet = null;
		Integer vSerMatrRet = null;
		String vItemCbo = null;
		Long vCnsMedico = null;
		String vCidade = null;
		
		final String mama = buscarAghParametro(AghuParametrosEnum.P_SIGLA_SISMAMA).getVlrTexto();
		final Integer implanteCoclear = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR);
		final String[] listaCnsMed = buscarVlrArrayAghParametro(AghuParametrosEnum.P_CNS_MED_GRUPO_PROC_FINALID_DIAGNOSTICA);
		final Short grupoProcedimentosFinalidadeDiagnostica = buscarVlrShortAghParametro(AghuParametrosEnum.P_GRUPO_PROCEDIMENTOS_FINALIDADE_DIAGNOSTICA);
		final Byte subgrupoDiagnosticoPorUltraSonografia = buscarVlrByteAghParametro(AghuParametrosEnum.P_SUBGRUPO_DIAGNOSTICO_POR_ULTRA_SONOGRAFIA);
		final Long cnsMedGrupoProcFinalidDiagnosticaResp = buscarVlrLongAghParametro(AghuParametrosEnum.P_CNS_MED_GRUPO_PROC_FINALID_DIAGNOSTICA_RESP);
		final Integer cnsMedGrupoProcFinalidDiagnosticaRespItemCbo = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CNS_MED_GRUPO_PROC_FINALID_DIAGNOSTICA_RESP_ITEM_CBO);
		final Integer pQuestaoCid10 = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_QUESTAO_CID10);
		final Date vSysdate = new Date();
		final Date ultimoDiaDoMes = DateUtil.truncaData(CoreUtil.obterUltimoDiaDoMes(new Date()));
		
		//usador para LOG.debug
		String vHeader  = "Início log FATURAMENTO: " + DateFormatUtil.obterDataAtualHoraMinutoSegundo() + " Usuário: APRADO";
		String vArqMens = "FAT_" + DateUtil.obterDataFormatada(new Date(), "yyyymmddHHmm") + ".log";

		List<TabelaQuantidadeVO> listaTabelaQuantVO = new ArrayList<TabelaQuantidadeVO>();
		listaTabelaQuantVO.add(new TabelaQuantidadeVO(0, 0, 0, null, 0, 0, 0, null)); //inicializa com um elemento
		
		//DAOs
		FatLogErrorDAO logErrorDAO = getFatLogErrorDAO();
		FatEspelhoProcedAmbDAO espelhoProcedAmbDAO = getFatEspelhoProcedAmbDAO();
		FatCompetenciaDAO competenciaDAO = getFatCompetenciaDAO();
		FatProcedAmbRealizadoDAO procedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		FatItensProcedHospitalarDAO itemProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		FatVlrItemProcedHospCompsDAO vlrItemProcedHospCompsDAO = getFatVlrItemProcedHospCompsDAO();
		FatConvFxEtariaItemDAO convFxEtariaItemDAO = getFatConvFxEtariaItemDAO();
	    FatResumoApacsDAO resumoApacsDAO = getFatResumoApacsDAO();
	    IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		
		//RN
		VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN = getVerificacaoFaturamentoSusRN();
		FaturamentoRN faturamentoRN = getFaturamentoRN();
		FaturamentoFatkCapUniRN faturamentoFatkCapUniRN = getFaturamentoFatkCapUniRN();
		
		//Facade
		ICadastroPacienteFacade cadastroPacienteFacade = getCadastroPacienteFacade();
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		ICadastrosBasicosFacade cadastrosBasicosFacade = this.getCadastrosBasicosFacade();
		FatVariaveisVO fatVariaveisVO = new FatVariaveisVO();
		
		//atribuirContextoSessao(VariaveisSessaoEnum.FATK_PMR_RN_V_PMR_JOURNAL, Boolean.FALSE);
		fatVariaveisVO.setvPmrJournal(Boolean.FALSE);
		List<Integer> resultSeqTipoInformacao = cadastrosBasicosFacade.pesquisarTipoInformacoesPorDescricao(CBO);
		
		List<Short> resultSeqTipoInformacaoShort = new ArrayList<Short>(); 
		for (Integer obj : resultSeqTipoInformacao) {
			resultSeqTipoInformacaoShort.add(Short.valueOf(obj.toString()));
		}
		
		//UserTransaction userTransaction = obterUserTransaction(null);
		
		logErrorDAO.removerPorModulo(DominioModuloCompetencia.AMB);
		espelhoProcedAmbDAO.removeEspelhoProcedAmb(pCpeDtHrInicio, pCpeAno, pCpeMes, DominioModuloCompetencia.AMB, null);
		
		//userTransaction = commitUserTransaction(userTransaction); //--COMMIT;
		getFaturamentoFacade().clearSemFlush();
		
		if (Boolean.TRUE.equals(pPrevia)) {
			vDataPrevia = DateUtil.truncaData(new Date());
			vDtFimCpe = nvl(pCpeDtFim, new Date());
			vMaxDthrRealiz = nvl(pCpeDtFim, new Date());
			
			LOG.debug("FAZENDO PREVIA!!!!!!!!!");
			
		} else {
		//	atribuirContextoSessao(VariaveisSessaoEnum.FATK_PMR_RN_V_PMR_ENCERRAMENTO, Boolean.TRUE); //-- Milena - em função dos grupos julho/2004
			fatVariaveisVO.setvPmrEncerramento(Boolean.TRUE);
			LOG.debug("ENCERRANDO COMPETENCIA!!!!!!!!!");
			if (competenciaParam != null) {
				vDtFimCpe = competenciaParam.getDtHrFim();
			}
			if (vDtFimCpe == null) {
				LOG.debug("DT COMP NULA! ");
				//-- vai atualizar competencia com dt fim
			    //-- e situacao = M
				vCpeDthrFim = nvl(pCpeDtFim, new Date());

				RnCpecAtuEncCompVO vo = getFaturamentoFatkCpeRN().rnCpecAtuEncComp(
						DominioModuloCompetencia.AMB.toString(), 
						pCpeDtHrInicio, 
						pCpeMes, 
						pCpeAno, 
						vCpeDthrFim, 
						vNewCpeMes, 
						vNewCpeAno);
				vNewCpeMes = vo.getpNewMes();
				vNewCpeAno = vo.getpNewAno();
				vNewCpeDthrInicio = vo.getpDthrInicio();
				vCpeDthrFim = vo.getpDthrFim();
				
				if (Boolean.TRUE.equals(vo.getResult())) {
					rnPmrpAtuPendentes(pCpeDtHrInicio, pCpeMes.byteValue(), pCpeAno.shortValue(), nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
					//userTransaction = commitUserTransaction(userTransaction); // --COMMIT; --Milena 12/12/2001
					LOG.debug("Clear antes da movimentacao");
					faturamentoFacade.clearSemFlush();
					LOG.debug("Fim do clear antes da movimentacao");
					//-- Passa para a proxima competencia os procedim
			        //-- que estao c/dthr_realizado posterior ao fim
			        //-- da competencia que esta sendo encerrada ou pendentes
					
					LOG.debug("Atualiza Competencia Futura");
					rnPmrpAtuCompetenciaFutura(
							pCpeDtHrInicio, 
							vCpeDthrFim, 
							pCpeMes.byteValue(), 
							pCpeAno.shortValue(),
							vNewCpeDthrInicio, 
							vNewCpeMes.byteValue(), 
							vNewCpeAno.shortValue(), 
							nomeMicrocomputador, dataFimVinculoServidor,
							log, fatVariaveisVO);
					
					vDtFimCpe = vCpeDthrFim;
					//userTransaction = commitUserTransaction(userTransaction); // --COMMIT; --Milena 12/12/2001
				}
				else {
					//-- Erro ao encerrar competência e criar nova competência aberta
					throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00495);
				}
			}
			vMaxDthrRealiz = vDtFimCpe;
		}

		//busca a competencia que foi atualizada (se for encerramento) no banco
		FatCompetencia fatCompetencia = competenciaDAO.obterPorChavePrimaria(
				new FatCompetenciaId(DominioModuloCompetencia.AMB, pCpeMes, pCpeAno, pCpeDtHrInicio));
		
		
		//-- INCLUIR CHAMADA PROCEDURE QUE ATUALIZA
		//-- CONSULTAS EM GRUPO!!!!!!!----- Incluída em 10/07/2000 as 1:45 a.m.
		LOG.debug("Atualizando Consultas em Grupo");
		getFaturamentoFatkInterfaceAacRN().rnFatpPmpConsultaGrupo(pCpeDtHrInicio ,vMaxDthrRealiz, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
		
		//-- busca phi  de cateter duplo J para não ser cobrado no BPA caso
		//-- não esteja associado a apac de exame
		
		vPhiDuploJ = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_CATETER_DUPLO_J);

		//userTransaction = commitUserTransaction(userTransaction);
		
		List<FatEspelhoProcedAmbVO> listaEspelhoProcedAmbVO = procedAmbRealizadoDAO.buscarFatEspelhoProcedAmbVO(
				vMaxDthrRealiz, 
				DominioModuloCompetencia.AMB, 
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.M, 
				DominioSituacaoProcedimentoAmbulatorio.ABERTO, 
				pCpeDtHrInicio, 
				pCpeMes, 
				pCpeAno, 
				DominioGrupoConvenio.S);

		Integer nroRegistrosAux = listaEspelhoProcedAmbVO.size();
		
		getFaturamentoFacade().enviaEmailResultadoEncerramentoAmbulatorio(
				".............. FATURAMENTO AMBULATORIO  .............. <br />" +
				" Processando " + nroRegistrosAux + " registros."
																		 );
		
		int contNroReg = 0;
		
		FaturamentoON faturamentoON = getFaturamentoON();
		
		String logTxt = "##### AGHU - Processando " + contNroReg + " de " + nroRegistrosAux + " " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS); 
		LOG.debug(logTxt);
		getSchedulerFacade().adicionarLog(log, logTxt);
		getFaturamentoFacade().clear();

		for (FatEspelhoProcedAmbVO regProcAmb : listaEspelhoProcedAmbVO) {	
			//calcula idade do paciente na data do atendimento
			if (regProcAmb.getDtNascimento() != null && regProcAmb.getDthrRealizado() != null) {
				regProcAmb.setIdade(faturamentoON.calculaIdade(regProcAmb.getDtNascimento(), regProcAmb.getDthrRealizado()));
			}
			
			contNroReg++;

			if (contNroReg % FETCH_SIZE == 0) {
				//Para evitar java.lang.OutOfMemoryError: GC overhead limit exceeded e Java Heap Space
				faturamentoFacade.clear();
				logTxt = "##### AGHU - Processando " + contNroReg + " de " + nroRegistrosAux + " " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS); 
				LOG.debug(logTxt);
				getSchedulerFacade().adicionarLog(log, logTxt);
			}

			vConsistente = Boolean.TRUE;
			//-- FAZER COMMIT A CADA 10000 REGISTROS  -- hco
			vContador++;
			//-- rotina da base de dados 27/05/2002
			if (vPrimVez || vContador % 10000 == 0) {
				if (vPrimVez) {
					vPrimVez = false;
					LOG.debug(vHeader + vArqMens);
				}
			}
			//-- fim rotina base
			//if (vContador % FETCH_SIZE == 0) {
				//userTransaction = commitUserTransaction(userTransaction);
				//this.commitTransaction();
			//}
			
			if (regProcAmb.getCodigoPac() != null) {
				vIdade = getVerificacaoFaturamentoSusRN().obterIdadePacienteParaDataAtual(regProcAmb.getCodigoPac());
				
				if (vIdade == null) {
					criarFatLogErrors("NAO ENCONTROU IDADE DO PACIENTE.",
									  DominioModuloCompetencia.AMB.toString(), 
									  null, null, null, null, null, vDataPrevia, 
									  null, null, null, null, null, null, 
									  regProcAmb.getCodigoPac(),  
									  regProcAmb.getPhiSeq(), 
									  null, regProcAmb.getSeq(), 
									  null, null, null, null, null, null, null, null, null,
									  RN_PMRP_GERA_NEW, 
									  null, null, new FatMensagemLog(135));

					if (DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B.equals(regProcAmb.getLocalCobranca())) {
							//-- If colocado por Milena - fev/2011 - para não cancelar indevidamente itens de apac
							rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.CANCELADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
					}
					vConsistente = Boolean.FALSE;
				}
			}
			
			vIndice = 1;
			vIndConsulta = Boolean.FALSE;
			
			List<RnFatcVerItprocexcVO> listaVO = verificacaoFaturamentoSusRN.verificarItemProcHosp( regProcAmb.getPhiSeq(), 
																									regProcAmb.getQuantidade(), 
																									regProcAmb.getCspCnvCodigo(), 
																									regProcAmb.getCspSeq());

			vQtdItens = (short) listaVO.size();

			//isso está fora do loop pq se o size for zero nunca iria LOG.debug essa mensagem dentro do foreach
			if (vQtdItens <= 0) { 
				//-- nao encontrou item procedimento hospitalar
				criarFatLogErrors("NAO ENCONTROU ITEM PROCEDIMENTO HOSPITALAR.",
								  DominioModuloCompetencia.AMB.toString(), 
								  null, null, null, null, null, vDataPrevia, 
								  null, null, null, null, null, null,
								  regProcAmb.getCodigoPac(), 
								  regProcAmb.getPhiSeq(), null, 
								  regProcAmb.getSeq(), 
								  null, null, null, null, null, null, null, null, null,
								  RN_PMRP_GERA_NEW, null,  null, new FatMensagemLog(137));
				
				if (DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B.equals(regProcAmb.getLocalCobranca()) &&
					regProcAmb.getQuantidade().shortValue() > 0) {
					//-- If colocado por Milena - fev/2011 - para não cancelar indevidamente itens de apac
					rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.CANCELADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
				}
				vConsistente = Boolean.FALSE;				

			}			
			
			for (RnFatcVerItprocexcVO vo : listaVO) {
				vIphQtdItem = vo.getQtdItem();
				vIphPhoSeq = vo.getPhoSeq();
				vIphSeq = vo.getSeq();
				
				if (Boolean.FALSE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
					break;
				}
				if (vQtdItens > 0) {
					//-- busca cod tabela
					FatItensProcedHospitalar itemProcedHospitalar = itemProcedHospitalarDAO.obterAtivosPorId(vIphPhoSeq, vIphSeq, true);
					
					if (itemProcedHospitalar != null) {
						vIphCodTab 	 = itemProcedHospitalar.getCodTabela();
						vPrincApac   = itemProcedHospitalar.getProcPrincipalApac();
						if (itemProcedHospitalar.getCaracteristicaComplexidade() != null) {
							vIphFccSeq 	 = itemProcedHospitalar.getCaracteristicaComplexidade().getSeq();
						}
						if (itemProcedHospitalar.getFatCaracteristicaFinanciamento() != null) {
							vIphFcfSeq 	 = itemProcedHospitalar.getFatCaracteristicaFinanciamento().getSeq();
						}
						vIndConsulta = itemProcedHospitalar.getConsulta();
						vSexo 		 = itemProcedHospitalar.getSexo();
						vIdadeMin 	 = itemProcedHospitalar.getIdadeMin();
						vIdadeMax 	 = itemProcedHospitalar.getIdadeMax();
					}
				
					if (Boolean.TRUE.equals(vPrincApac)) {
						break;
					}
					
					//-- teste abaixo colocado visando implantação do siscolo baseado em características.
			        //--cvagheti(08/01/2008)
					vCobraBpa = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.COBRA_SISCOLO);
					
					if (Boolean.TRUE.equals(vCobraBpa)) {
						break;
					}
					
					//-- Milena 29/11/2002. Para não processar os ítens de apac que serão transferidos para próxima competência
					vCobraBpa  = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.COBRA_BPA);
					vCobraApac = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.COBRA_APAC);
					vCobraBpi  = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.COBRA_BPI);
					
					if (Boolean.FALSE.equals(vCobraBpa) && Boolean.FALSE.equals(vCobraBpi) && Boolean.TRUE.equals(vCobraApac)) {
						break;
					}
					//-- Final alteração Milena
					
					if (vPhiDuploJ.equals(regProcAmb.getPhiSeq())) {
						vConsistente = Boolean.FALSE;
					}
					
					//-- não cobra nem aplica regras em itens associados ao código 1
					if (vIphCodTab != null && vIphCodTab.equals(Long.valueOf(1l))) {
						vConsistente = Boolean.FALSE;
					}
					
					if (Boolean.FALSE.equals(vConsistente)) {
						break;
					}
					
					regrasOk = rnPmrpAtuRegras(
								regProcAmb.getSeq(), 
								regProcAmb.getCspCnvCodigo(), 
								regProcAmb.getCodigoPac(), 
								vIphPhoSeq, 
								vIphSeq, 
								vDtFimCpe,
								pPrevia, 
								DEFAULT_NULL,
								mama, 
								nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
					
					if (Boolean.FALSE.equals(regrasOk)) {
						//-- nao passou pelas regras de validação
						//-- a insercao de erros quando não se encontra regra
						//-- é feita na rotina de validacao das regras
						rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.CANCELADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
						vConsistente = Boolean.FALSE;
					}
					
					if (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
						break;
					}
						
					vGraCodSus = 0;
					vGraCodSus = espelhoProcedAmbDAO.obterGrupoAtendimento(vIphSeq, vIphPhoSeq, regProcAmb.getCspCnvCodigo(), regProcAmb.getEspSeq());
					vGraCodSus = nvl(vGraCodSus, 0);
					
					vCfeCodSus = 0;
					if (regProcAmb.getCodigoPac() != null) {
						vCfeCodSus = 0;
			            //-- caso o item tenha alguma faixa etária cadastrada
			            //-- o paciente do item lançado deverá enquadrar-se em alguma delas
			            //-- (o fato de ter uma faixa significa que exige faixa etária)
						
						List<Byte> listaFaixaEtariaItem = convFxEtariaItemDAO.obterListaCodSusPorIphCnvDataFaixaEtariaAtiva(regProcAmb.getCspCnvCodigo(), vIphPhoSeq, vIphSeq, vSysdate);
						
						if (!listaFaixaEtariaItem.isEmpty()) { //-- caso não encontre a faixa do paciente pega a primeira
							//-- existem faixas etarias cadastradas pro item
							vCfeCodSusItem = listaFaixaEtariaItem.get(0).intValue();
							List<Byte> listaFaixaEtaria = convFxEtariaItemDAO.obterListaCodSusPorIphCnvIdadeDataFaixaEtariaAtiva(regProcAmb.getCspCnvCodigo(), vIphPhoSeq, vIphSeq, vSysdate, vIdade);
							if (listaFaixaEtaria.isEmpty()) {
								//-- caso não encontre a faixa do paciente pega a primeira
								vCfeCodSus = vCfeCodSusItem;
							}
							else {
								vCfeCodSus = listaFaixaEtaria.get(0).intValue();									
							}
								
							vCfeCodSus = nvl(vCfeCodSus, 0);
						}
					} else {
						vCfeCodSus = 0;
					}
					if (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
						break;
					}
					
					vCpeCompetencia = DateUtil.obterData(regProcAmb.getAno(), regProcAmb.getMes(), 01);
					
					FatVlrItemProcedHospComps regValor = vlrItemProcedHospCompsDAO.obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(vIphPhoSeq, vIphSeq, vCpeCompetencia);
	
				    //-- Milena abril/2008 -- abaixo
					vErroLog = null;
					vSerVinCodigoRet = null;
					vSerMatrRet = null;
					vItemCbo = null;
					
					LOG.debug(" vai ver o cbo ");
					RnCapcCboProcResVO rnCapcCboProcRes = faturamentoFatkCapUniRN.rnCapcCboProcRes(
							regProcAmb.getMatriculaResp(),
							regProcAmb.getVinCodigoResp(),
							regProcAmb.getIseSoeSeq(),
							regProcAmb.getIseSeqp(),
							regProcAmb.getPrhConNumero(), 
							regProcAmb.getPpcCrgSeq(),
							vIphPhoSeq, 
							vIphSeq, 
							vCpeCompetencia,
							resultSeqTipoInformacaoShort,
							ultimoDiaDoMes);
					
					if (rnCapcCboProcRes != null) {
						vErroLog = rnCapcCboProcRes.getpErro();
						vSerVinCodigoRet = rnCapcCboProcRes.getpSerVinCodigo();
						vSerMatrRet = rnCapcCboProcRes.getpSerMatr();
						vItemCbo = rnCapcCboProcRes.getRetorno();
					}
					
					if ("NTC".equals(vErroLog)) {
						LOG.debug("NTC cbo");
						
						criarFatLogErrors("NAO ENCONTROU CBO DO RESPONSAVEL",
										  DominioModuloCompetencia.AMB.toString(), 
										  null, null, null, null, null, vDataPrevia,
										  null, null, null, null, null, null,
										  regProcAmb.getCodigoPac(), 
										  regProcAmb.getPhiSeq(),
										  null,  regProcAmb.getSeq(), 
										  null, null, null, null, null, null, null, null, null, 
										  RN_PMRP_GERA_NEW, vSerVinCodigoRet, vSerMatrRet, null);
					}
					
					if ("INC".equals(vErroLog)) { // -- Profissional não tem cbo compatível com o procedimento
						LOG.debug("INC cbo");
						
						criarFatLogErrors("CBO PROFISSIONAL INCOMPATIVEL COM CBO PROCEDIMENTO",
										  DominioModuloCompetencia.AMB.toString(), 
										  null, null, null, null, null, vDataPrevia,
										  null, null, null, null, null, null, 
										  regProcAmb.getCodigoPac(),
										  regProcAmb.getPhiSeq(), null,
										  regProcAmb.getSeq(),
										  null, null, null, null, null, null, null, null, null,
										  RN_PMRP_GERA_NEW, vSerVinCodigoRet, vSerMatrRet, new FatMensagemLog(10) );					
					}
					
					//-- Trecho que representa a chamada de função da query --//
					AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(regProcAmb.getUnfSeq());
					
					DominioSimNao caractAtendimentoSimNao = DominioSimNao.N;
					
					if (unidadeFuncional != null) {
						caractAtendimentoSimNao =  getFarmaciaApoioFacade().existeCaractUnFuncional(unidadeFuncional, new String[] {ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA.getCodigo()}); //S = 2, N = 1;
					}
					
					if (DominioSimNao.S.equals(caractAtendimentoSimNao)) {
						vCaraterAtendimento = 2; //S
						
					} else {
						vCaraterAtendimento = 1; //N
					}
					//-- Fim trecho que representa a chamada de função da query --//
					
					//-- BPI
					if (Boolean.TRUE.equals(vCobraBpi)) {
						//-- marcia 18/08/08
						//------------------------ REPRESENTA A PROCEDURE P_QTDE_MAX_BPI ------------------------//
						vQtdeMaxDia  = 0;
						vQtdeEspelho = 0;
						vAchou = Boolean.FALSE;
						vAchouMes = Boolean.FALSE;
	
						List<FatCaractItemProcHosp> listarCaracteristica = getFatCaractItemProcHospDAO().listarPorIphCaracteristica(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.QTD_MAXIMA_COBRAVEL_BPI);
						if (!listarCaracteristica.isEmpty()) {
							vQtdeMaxDia = listarCaracteristica.get(0).getValorNumerico();
						}
						
						if (vQtdeMaxDia != null && vQtdeMaxDia > 0) {
							vAcumulaDia = 0;
							
							Integer cont = 0;
							for (TabelaQuantidadeVO tabelaQuantVO : listaTabelaQuantVO) { //-- varrer a tabela inteira, inicialmente tem 1 elemento
								LOG.debug("loop i =  " + cont++);
								
								if (CoreUtil.igual(tabelaQuantVO.getPacCodigo(), regProcAmb.getCodigoPac())
									&& CoreUtil.igual(vIphPhoSeq, tabelaQuantVO.getPhoSeq())
									&& CoreUtil.igual(vIphSeq, tabelaQuantVO.getIphSeq())
									&& CoreUtil.igual(DateUtil.truncaData(regProcAmb.getDthrRealizado()), DateUtil.truncaData(tabelaQuantVO.getDtRealiz()))
									&& DIA.equals(tabelaQuantVO.getTipo())
									) {
									vAchou = Boolean.TRUE;
									vAcumulaDia = nvl(regProcAmb.getQuantidade(),0) + tabelaQuantVO.getQtd();
									
									if (tabelaQuantVO.getQtdInsDia().equals(nvl(vQtdeMaxDia,0))) {
										vConsistente = Boolean.FALSE;
										vQtdeEspelho = nvl(regProcAmb.getQuantidade(),0).intValue();
										
										//-- P_INSERE_LOG --//
										criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/DIA COBRÁVEL EM BPI",
													      DominioModuloCompetencia.AMB.toString(), 
													      null, null, null, null, null, null, null, null,
													      vIphPhoSeq, null, vIphSeq, null,
													      regProcAmb.getCodigoPac(), 
													      regProcAmb.getPhiSeq(), 
													      null, regProcAmb.getSeq(), 
													      null,  null, null, null, null, null, null, null, null, 
													      RN_PMRP_GERA_NEW,  null, null, new FatMensagemLog(204));
										
										//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
										rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
										
									}
									else {
										if (CoreUtil.menorOuIgual(vAcumulaDia, nvl(vQtdeMaxDia,0))) { //--posso inserir
											vConsistente = Boolean.TRUE;
											vQtdeEspelho = nvl(regProcAmb.getQuantidade(),0).intValue();
											tabelaQuantVO.setQtdInsDia(vAcumulaDia);
										}
										else { //--acumula > max
											vConsistente = Boolean.FALSE;
											vQtdeEspelho = nvl(vQtdeMaxDia, 0).shortValue() - tabelaQuantVO.getQtdInsDia().shortValue();
											tabelaQuantVO.setQtdInsDia(tabelaQuantVO.getQtdInsDia() + vQtdeEspelho);
											
											//-- P_INSERE_LOG --//
											criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/DIA COBRÁVEL EM BPI " + tabelaQuantVO.getQtdInsDia(),
															  DominioModuloCompetencia.AMB.toString(), 
															  null,  null, null, null, null, null, null, null, 
															  vIphPhoSeq, null, vIphSeq, null,
															  regProcAmb.getCodigoPac(), 
															  regProcAmb.getPhiSeq(), 
															  null, regProcAmb.getSeq(),
															  null, null, null, null, null, null, null, null, null, 
															  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(204));
											
											//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
											rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
										}
									}
								}
								//ind = i;
								//i++;
							} //end for
							
							if (Boolean.FALSE.equals(vAchou)) { // --registro ainda não existe na temporária ou primeira vez
								//-- alimenta a temporária com os registros vigentes
								//i = ++ind;
								//ind = i;
								LOG.debug("Alimenta tabela temporária " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
								TabelaQuantidadeVO elemento = new TabelaQuantidadeVO();
								elemento.setPacCodigo(regProcAmb.getCodigoPac());
								if (vIphPhoSeq != null) {
									elemento.setPhoSeq(vIphPhoSeq.intValue());
								}
								elemento.setIphSeq(vIphSeq);
								elemento.setDtRealiz(regProcAmb.getDthrRealizado());
								elemento.setQtd(nvl(regProcAmb.getQuantidade(),0).intValue());
								elemento.setTipo(DIA);
								
								if (CoreUtil.menorOuIgual(nvl(regProcAmb.getQuantidade(), 0), nvl(vQtdeMaxDia, 0)))  {
									vConsistente = Boolean.TRUE;
									vQtdeEspelho = nvl(regProcAmb.getQuantidade(), 0).intValue();
									elemento.setQtdInsDia(vQtdeEspelho);
								}
								else { //--qtd > máx
									vConsistente = Boolean.FALSE;	// Chamado 57926 Marina 01/11/2011
									vQtdeEspelho = nvl(vQtdeMaxDia, 0);
									elemento.setQtdInsDia(vQtdeEspelho);
									//p_insere_log
									criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/DIA COBRÁVEL EM BPI " + elemento.getQtdInsDia(),
													  DominioModuloCompetencia.AMB.toString(), 
													  null, null, null, null, null, null, null, null,
													  vIphPhoSeq, null, vIphSeq, null,
													  regProcAmb.getCodigoPac(),
													  regProcAmb.getPhiSeq(), 
													  null,  regProcAmb.getSeq(),
													  null, null, null, null, null, null, null, null, null,
													  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(204));
									
									//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
									rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
								}
								listaTabelaQuantVO.add(elemento);
								LOG.debug("Fim da alimentação da tabela temporária " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							}
						}
						
						//-- 17/09/08 - IDEM PARA A QTD/MÊS
						//-- Se existe qtde máxima para cobrnaça no cadastro, acumular a qtde
						//-- por data do realizado. Verificar se existe qtde/dia é menor do
						//-- que o limite do cadastro. Se for maior, colocar no espelho como
						//-- inconsistente e gerar na log como perda.
						//-- Se a qtd for maior que a máxima permitida do mes, inserir no espelho a qtd permitida
						//-- Se no teste DIA foi consistente, verificar também no MÊS, senão não.
						
						if (((vQtdeMaxDia != null && vQtdeMaxDia.intValue() > 0) && Boolean.TRUE.equals(vConsistente))
							|| (vQtdeMaxDia == null || vQtdeMaxDia.equals(Integer.valueOf(0)))) { //--se não tem qtd máx p Dia, verificar Mês
							
							LOG.debug("Início P_QTDE_MAX_BPI " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							vQtdeMaxMes = 0;
							
							listarCaracteristica = getFatCaractItemProcHospDAO().listarPorIphCaracteristica(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.QTD_MAXIMA_COBRAVEL_BPI_MES);
							if (!listarCaracteristica.isEmpty()) {
								vQtdeMaxMes = listarCaracteristica.get(0).getValorNumerico();
							}
							
							if (vQtdeMaxMes != null && vQtdeMaxMes.intValue() > 0) {
								vAcumulaMes = 0;
								//i = 0;
								////////////////////////////////////////// for
								for (TabelaQuantidadeVO tabelaQuantVO : listaTabelaQuantVO) {
									if (CoreUtil.igual(tabelaQuantVO.getPacCodigo(), regProcAmb.getCodigoPac())
										&& CoreUtil.igual(vIphPhoSeq, tabelaQuantVO.getPhoSeq())
										&& CoreUtil.igual(vIphSeq, tabelaQuantVO.getIphSeq())
										//&& regProcAmb.getDthrRealizado().equals(tabelaQuantVO.getDtRealiz()) //  Marina 03/12/2009
										&& MES.equals(tabelaQuantVO.getTipo())
										) {
										vAchouMes = Boolean.TRUE;
										vAcumulaMes = nvl(regProcAmb.getQuantidade(),0) + tabelaQuantVO.getQtd();
										
										if (tabelaQuantVO.getQtdInsMes().equals(nvl(vQtdeMaxMes,0))) {
											vConsistente = Boolean.FALSE;
											vQtdeEspelho = nvl(regProcAmb.getQuantidade(),0).intValue();
											
											//-- P_INSERE_LOG --//
											criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/MÊS COBRÁVEL EM BPI",
															  DominioModuloCompetencia.AMB.toString(), 
															  null, null, null, null, null, null, null, null, 
															  vIphPhoSeq, null, vIphSeq, null, 
															  regProcAmb.getCodigoPac(),  
															  regProcAmb.getPhiSeq(), null, 
															  regProcAmb.getSeq(), 
															  null, null, null, null, null, null, null, null, null, 
															  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(205));
											
											//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
											rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador,dataFimVinculoServidor, fatVariaveisVO);
											
										}
										else {
											if (CoreUtil.menorOuIgual(vAcumulaMes, nvl(vQtdeMaxMes,0))) { //--posso inserir
												vConsistente = Boolean.TRUE;
												vQtdeEspelho = nvl(regProcAmb.getQuantidade(),0).intValue();
												tabelaQuantVO.setQtdInsMes(vAcumulaMes);
											}
											else { //--acumula > max
												vConsistente = Boolean.FALSE;	// Chamado 57.926 - Marina 01/11/2011
												vQtdeEspelho = nvl(vQtdeMaxMes, 0) - tabelaQuantVO.getQtdInsMes();
												tabelaQuantVO.setQtdInsMes(tabelaQuantVO.getQtdInsMes() + vQtdeEspelho);
												
												//-- P_INSERE_LOG --//
												criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/MÊS COBRÁVEL EM BPI",
																  DominioModuloCompetencia.AMB.toString(), 
																  null, null, null, null, null, null, null, null, 
																  vIphPhoSeq, null, vIphSeq, null, 
																  regProcAmb.getCodigoPac(),  
																  regProcAmb.getPhiSeq(), null, 
																  regProcAmb.getSeq(), 
																  null, null, null, null, null, null, null, null, null, 
																  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(205));
												
												//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
												rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
											}
										}
									}
									//ind = i;
									//i++;
								} ////////////////////////////////// end for
								
								if (Boolean.FALSE.equals(vAchouMes)) { //--registro ainda não existe na temporária ou primeira vez
									//-- alimenta a temporária com os registros vigentes
									//i = ++ind;
									//ind = i;
									
									TabelaQuantidadeVO elemento = new TabelaQuantidadeVO();
									elemento.setPacCodigo(regProcAmb.getCodigoPac());
									if (vIphPhoSeq != null) {
										elemento.setPhoSeq(vIphPhoSeq.intValue());
									}
									elemento.setIphSeq(vIphSeq);
									elemento.setDtRealiz(regProcAmb.getDthrRealizado());
									elemento.setQtd(nvl(regProcAmb.getQuantidade(),0).intValue());
									elemento.setTipo(MES);
									
									if (CoreUtil.menorOuIgual(nvl(regProcAmb.getQuantidade(),0), nvl(vQtdeMaxMes,0))) {
										vConsistente = Boolean.TRUE;
										vQtdeEspelho = nvl(regProcAmb.getQuantidade() ,0).intValue();
										elemento.setQtdInsMes(vQtdeEspelho);
									}
									else { //--acumula > max
										vConsistente = Boolean.FALSE;
										vQtdeEspelho = nvl(vQtdeMaxMes,0);
										elemento.setQtdInsMes(vQtdeEspelho);
										//-- P_INSERE_LOG --//
										criarFatLogErrors("PROCEDIMENTO EXCEDEU A QTDE/MÊS COBRÁVEL EM BPI",
														  DominioModuloCompetencia.AMB.toString(), 
														  null, null, null, null, null, null, null, null, 
														  vIphPhoSeq, null, vIphSeq, null, 
														  regProcAmb.getCodigoPac(),  
														  regProcAmb.getPhiSeq(), null, 
														  regProcAmb.getSeq(), 
														  null, null, null, null, null, null, null, null, null, 
														  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(205));
										
										//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
										rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
										
									}
									listaTabelaQuantVO.add(elemento);
								}
							}
							LOG.debug("Fim P_QTDE_MAX_BPI " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
						}						
						if (vQtdeEspelho == null || vQtdeEspelho.equals(Integer.valueOf(0))) {
							if (vIphQtdItem != null) {
								vQtdeEspelho = vIphQtdItem.intValue();
							}
						}
						//------------------------ FIM DA PROCEDURE P_QTDE_MAX_BPI ------------------------//
						
						//------------------------ P_VERIFICA_TERAPIA ------------------------//
						
						if (vPhiTerapia.equals(regProcAmb.getPhiSeq())) {
							
							List<Date> listaFinalResumoApac = resumoApacsDAO.buscarDataFinalResumosApacsAtivos(regProcAmb.getCodigoPac(), 
									DateUtil.truncaData(DateUtil.obterDataFimCompetencia(DateUtil.adicionaDias(vDtFimCpe, -vDiasApacAparelho))), 
									DominioSimNao.S);
							
							if (listaFinalResumoApac.isEmpty()) {
								List<Date> listaDatasCirurgias = blocoCirurgicoFacade.buscarDataCirurgias(regProcAmb.getCodigoPac(), 
																	vDtFimCpe,
																	DateUtil.truncaData(DateUtil.obterDataFimCompetencia(DateUtil.adicionaDias(vDtFimCpe, -vDiasApacAparelho))), 
																	DominioSituacaoCirurgia.RZDA,
																	Boolean.TRUE, 
																	cnvCodigo, 
																	DominioIndRespProc.NOTA, 
																	DominioSituacao.A, 
																	implanteCoclear );
								
								if (listaDatasCirurgias.isEmpty()) {
									vConsistente = Boolean.FALSE;
									
									criarFatLogErrors("PROCEDIMENTO DE TERAPIA FORA DO PRAZO",
													  DominioModuloCompetencia.AMB.toString(), 
													  null, null, null, null, null, null, null, null, 
													  vIphPhoSeq, null, vIphSeq, null, 
													  regProcAmb.getCodigoPac(),  
													  regProcAmb.getPhiSeq(), null, 
													  regProcAmb.getSeq(), 
													  null, null, null, null, null, null, null, null, null, 
													  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(203) ); 
									
									//-- Marina 11/10/2009 - Habilitado por Marina em 27/07/2011 - chamado 50488
									rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
								}
							}
						}
						//------------------------ FIM P_VERIFICA_TERAPIA ------------------------//
						
						vSerVinCodigoRet = null;
						vSerMatrRet = null;
						LOG.debug("vai ser cns");
						
						CnsResponsavelVO cnsResponsavelVO  = faturamentoRN.fatcBuscaCnsResp(
								regProcAmb.getMatriculaResp(), 
								regProcAmb.getVinCodigoResp(), 
								regProcAmb.getIseSoeSeq(), 
								regProcAmb.getIseSeqp(), 
								regProcAmb.getPrhConNumero(), 
								regProcAmb.getPpcCrgSeq()); 
						
						vCnsMedico = cnsResponsavelVO.getItemCns();
						vSerMatrRet = cnsResponsavelVO.getMatricula();
						vSerVinCodigoRet = cnsResponsavelVO.getVinCodigo();
						
						LOG.debug("voltou com cns " + vCnsMedico);
						
			            //-- Milena - set/2008
			            //-- Por definição dos responsáveis (Adriana Rosa), procedimentos do grupo 2, subgrupo 5, quando executados
			            //-- pelos médicos listados, deve ser enviado cns e cbo do Dr. Barbosa como responsável.
						FatGrupoSubGrupoVO grupoSubGrupoVO = itemProcedHospitalarDAO.obterFatGrupoSubGrupoVOPorCodTabela(vIphCodTab); //201565893830004, 201566131440006, 201566908580004, 201567124280008, 201565954720009
						if (grupoSubGrupoVO != null) {
							if (grupoProcedimentosFinalidadeDiagnostica.equals(grupoSubGrupoVO.getGrupo()) && //grupo = 2 
								subgrupoDiagnosticoPorUltraSonografia.equals(grupoSubGrupoVO.getSubGrupo())) { //subgrupo = 5
								for (String cnsMed : listaCnsMed) {
									if (cnsMed.equals(vCnsMedico)) {
										vCnsMedico = cnsMedGrupoProcFinalidDiagnosticaResp; //-- Dr. Gilberto Venossi Barbosa = 201565896850006
										vItemCbo = cnsMedGrupoProcFinalidDiagnosticaRespItemCbo.toString();  //223107
									}
								}
							}
						}
					
						if (vCnsMedico == null) {
							vConsistente = Boolean.FALSE;
		
							criarFatLogErrors("NAO ENCONTROU CNS DO RESPONSAVEL",
											  DominioModuloCompetencia.AMB.toString(), 
											  null, null, null, null, null, 
											  vDataPrevia, null, null, null, null, null, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, vSerVinCodigoRet, vSerMatrRet, new FatMensagemLog(119));
						}
						
						LOG.debug("ver cid");
						if (regProcAmb.getCodigoCid() == null && regProcAmb.getIseSoeSeq() != null) {
							regProcAmb.setCodigoCid(getAghuFacade().obterCidExamePorItemSolicitacaoExames(regProcAmb.getIseSoeSeq(), regProcAmb.getIseSeqp(), pQuestaoCid10));
						}
						
						LOG.debug("passou do cid parametro");
						//-- Milena 30/01/2008. cid null após exame. definição Adriana.
						if (regProcAmb.getCodigoCid() == null) {
							LOG.debug("busca para cid nulo");
							regProcAmb.setCodigoCid(getAghuFacade().obterCodigoAghCidsPorPhiSeq(regProcAmb.getPhiSeq()));
						}
						
						LOG.debug("busca cidade");
						
						vCidade = null;
						vCodIbge = null;
						
						VAipEnderecoPaciente enderecoPaciente = cadastroPacienteFacade.obterEndecoPaciente(regProcAmb.getCodigoPac());
						if (enderecoPaciente != null) {
							if (enderecoPaciente.getCodIbge() != null) {	
								// MARINA 03/10/2012 - SUS NÃO SUPORTA MAIS DE 6 CARACTERES
								vCodIbge = new BigDecimal(StringUtils.substring(enderecoPaciente.getCodIbge().toString(),0,6));
							}
							vCidade  = enderecoPaciente.getCidade();
						}
						
						LOG.debug("ver cns do paciente");
						
						if (regProcAmb.getNroCartaoSaude() == null) {
					        // Marina 29/02/2012 - Chamado 64880 -  Voltei a versão do Ney porque tem procedimentos que não exige cns
					        // Ney, 2012/02/12:
					        // Exigencia do CNS (Responsavel e paciente) para BPI independente da caracteristica do procedimento
							vExigeCnsPac = Boolean.TRUE;

							vExigeCnsPac = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.EXIGE_CNS_PACIENTE);
							
							if (Boolean.TRUE.equals(vExigeCnsPac)) {
								vConsistente = Boolean.FALSE;
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(221);
								criarFatLogErrors("NAO ENCONTROU CNS PACIENTE", 
												  DominioModuloCompetencia.AMB.toString(),  
												  null, null, null, null, null, 
												  vDataPrevia, null, null, null, null, null, null, 
												  regProcAmb.getCodigoPac(),  
												  regProcAmb.getPhiSeq(), null, 
												  regProcAmb.getSeq(), 
												  null, null, null, null, null, null, null, null, null, 
												  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(121));
							}
						}
						
			            //-- Marcia 31/07/08
			            //-- Marina 20/11/2009 - Def Adriana, para pacientes sem cadastro no AGH, emitir msg na log errors e deixar faturar.
						if (StringUtils.isEmpty(vCidade) || CoreUtil.igual(vCodIbge, BigDecimal.ZERO)) {
							//-- Marina 20/11/2009 - Def Adriana, para pacientes sem cadastro no AGH, emitir msg na log errors e deixar faturar.
				            //-- v_consistente := 'N'; -- Milena abril/2009. Def Adriana.
							
							criarFatLogErrors("CODIGO DO MUNICIPIO NAO INFORMADO", 
											  DominioModuloCompetencia.AMB.toString(),  
											  null, null, null, null, null, null, null, null, null, null, 
											  vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, null);
						}
						
						if (regProcAmb.getSexo() == null) {
							criarFatLogErrors("SEXO DO PACIENTE NÃO INFORMADO", 
											  DominioModuloCompetencia.AMB.toString(), 
											  null, null, null, null, null, null, null, null, null, null, 
											  vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(251));
						}
						
						if (!(regProcAmb.getIdade() == null || vIdadeMin == null || vIdadeMax == null || (regProcAmb.getIdade() >= vIdadeMin && regProcAmb.getIdade() <= vIdadeMax))) {
							criarFatLogErrors("IDADE INCOMPATIVEL COM PROCEDIMENTO REALIZADO", 
											  DominioModuloCompetencia.AMB.toString(),  
											  null, null, null, null, null, null, null, null, null, null, 
											  vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(97));
		
							//-- Marina 11/10/2009
							rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							
							vConsistente = Boolean.FALSE;
						}
						
						if (!(vSexo == null || DominioSexoDeterminante.Q.equals(vSexo) || regProcAmb.getSexo() == null || regProcAmb.getSexo().toString().equals(vSexo.toString()))) {
							criarFatLogErrors("SEXO INCOMPATIVEL COM PROCEDIMENTO REALIZADO", 
											  DominioModuloCompetencia.AMB.toString(),  
											  null, null, null, null, null, null, null, null, null, null, 
											  vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(252));
						}
						
			            //-- marcia 01/09/08
			            //-- Sexo do Cid do paciente deve ser compatível com o sexo
						vRestricaoSexo = null;
						
						vRestricaoSexo = getAghuFacade().obterRestricaoSexo(regProcAmb.getCodigoCid());						
						if (!(vRestricaoSexo == null || DominioSexoDeterminante.Q.equals(vRestricaoSexo) || regProcAmb.getSexo() == null || regProcAmb.getSexo().toString().equals(vRestricaoSexo.toString()))) {
							criarFatLogErrors("CID DO PACIENTE INCOMPATIVEL COM O SEXO", 
											  DominioModuloCompetencia.AMB.toString(),  
											  null, null, null, null, null, null, null, null, null, null, 
											  vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(), null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(12));
						}
						
						//-- Se cid não informado e o procedimento exige cid, gerar erro
						vProcExigeCid = Boolean.FALSE;
						if (regProcAmb.getCodigoCid() == null) {
							vProcExigeCid = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.EXIGE_CID);
							if (Boolean.TRUE.equals(vProcExigeCid)) {
								criarFatLogErrors("CID INEXISTENTE QUANDO EXIGIDO PELO PROCEDIMENTO", 
												  DominioModuloCompetencia.AMB.toString(),  
												  null, null, null, null, null, null, null, null, null, null, 
												  vIphSeq, null, 
												  regProcAmb.getCodigoPac(),  
												  regProcAmb.getPhiSeq(), null, 
												  regProcAmb.getSeq(), 
												  null, null, null, null, null, null, null, null, null, 
												  RN_PMRP_GERA_NEW, null, null, new FatMensagemLog(13));
							}
						}
						
						if (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
							break;
						}
						FatEspelhoProcedAmb espelho = new FatEspelhoProcedAmb();
						try {

							LOG.debug("Insere Espelho BPI " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							LOG.debug("v_item_cbo="+vItemCbo+" v_erro_log="+vErroLog+" pmr="+regProcAmb.getSeq());
							
							espelho.setDataPrevia(vDataPrevia);
							espelho.setIndConsistente(vConsistente);
							unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(regProcAmb.getUnfSeq());
							espelho.setUnidadeFuncional(unidadeFuncional);
							
							espelho.setCompetencia(vCompetencia);
							espelho.setProcedimentoHosp(vIphCodTab);
							
							if (vCavCodSus != null) {
								espelho.setAtvProfissional(vCavCodSus.byteValue());
							}
							
							espelho.setCodAtvProf(vItemCbo);
							espelho.setGrupoAtendimento(vGraCodSus);
							
							espelho.setFaixaEtaria(nvl(vCfeCodSus, 0).byteValue());
							espelho.setQuantidade(vQtdeEspelho);
							if (vQtdeEspelho != null) {
								if (regValor.getVlrServHospitalar() != null) {
									espelho.setVlrServHosp(new BigDecimal(vQtdeEspelho).multiply(regValor.getVlrServHospitalar()));
								}
								if (regValor.getVlrServProfisAmbulatorio() != null) {
									espelho.setVlrServProf(new BigDecimal(vQtdeEspelho).multiply(regValor.getVlrServProfisAmbulatorio()));
								}
								else {
									espelho.setVlrServProf(BigDecimal.ZERO);
								}									
								if (regValor.getVlrSadt() != null) {
									espelho.setVlrSadt(new BigDecimal(vQtdeEspelho).multiply(regValor.getVlrSadt()));
								}
								if (regValor.getVlrProcedimento() != null) {
									espelho.setVlrProc(new BigDecimal(vQtdeEspelho).multiply(regValor.getVlrProcedimento()));
								}
								if (regValor.getVlrAnestesista() != null) {
									espelho.setVlrAnestes(new BigDecimal(vQtdeEspelho).multiply(regValor.getVlrAnestesista()));
								}
							}
							espelho.setPmrSeq(regProcAmb.getSeq());
							
							FatItensProcedHospitalar itensProcedHosp = itemProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vIphPhoSeq, vIphSeq));
							espelho.setItensProcedHospitalar(itensProcedHosp);
							
							espelho.setIndConsulta(vIndConsulta);
							espelho.setFatCompetencia(fatCompetencia);
							espelho.setCriadoEm(new Date());
							espelho.setCriadoPor(servidorLogado.getUsuario());
							espelho.setFccSeq(vIphFccSeq);
							espelho.setFcfSeq(vIphFcfSeq);
							espelho.setCnsMedico(vCnsMedico);
							espelho.setDataAtendimento(regProcAmb.getDthrRealizadoTruncado());
							
							if (regProcAmb.getNroCartaoSaude() != null) {
								espelho.setCnsPaciente(regProcAmb.getNroCartaoSaude().longValue());
							}
							
							espelho.setSexo(regProcAmb.getSexo());
							espelho.setCid10(regProcAmb.getCodigoCid());
							
							if (regProcAmb.getIdade() != null) {
								espelho.setIdade(regProcAmb.getIdade().shortValue());
							}
							
							if (vCaraterAtendimento != null) {
								espelho.setCaraterAtendimento(vCaraterAtendimento.byteValue());
							}
							
							espelho.setNroAutorizacao((long) 0);
							espelho.setOrigemInf(DominioBoletimAmbulatorio.BPI);
							espelho.setNomePaciente(regProcAmb.getNomePac());
							
							if (vCodIbge != null) {
								espelho.setCodIbge(vCodIbge.intValue());
							}
							
							espelho.setTipoFormulario("I");
							
							if (regProcAmb.getCor() != null) {
								espelho.setRaca((byte) regProcAmb.getCor().getCodigo());
								
							} else {
								espelho.setRaca((byte) DominioCor.O.getCodigo()); //outro
							}								
					
							if (regProcAmb.getCodigoNac() != null) {
								AipNacionalidades nacionalidade = pacienteFacade.obterNacionalidade(regProcAmb.getCodigoNac()); 
								espelho.setNacionalidade(nacionalidade);
							}
							
							espelho.setDtNascimento(regProcAmb.getDtNascimento());
							
							// MARINA - 14/05/2013
						    // bU8SCA DADOS CADASTRAIS DO PACIENTE
							populaEnderecoPacienteEmEspelho(enderecoPaciente, espelho);

							espelhoProcedAmbDAO.persistir(espelho);
							espelhoProcedAmbDAO.flush();
							
							LOG.debug("Fim da Inserção do Espelho BPI " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							LOG.debug("Fim da Atualização da Situação BPI " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
						}
						catch (Exception e) {							
							//userTransaction = reIniciarTransacao(userTransaction);
							//this.rollbackTransaction();
							//userTransaction = obterUserTransaction(null);
							
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							String stringException = sw.toString();								
							
							logTxt = "##### AGHU - Exception - erro ao tentar inserir FAT_ESPELHOS_PROCED_AMB " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " <br /> " 
								   + espelho.toString() + " <br /> <br /> "
								   + stringException;
							LOG.debug("erro: " + logTxt.replaceAll(REGEX_BR, QUEBRA_LINHA));
							
							logError(e);
							
							criarFatLogErrors("ERRO AO INSERIR ESPELHO BPI: "  + e.getCause() + " " + e.getMessage(), 
										      DominioModuloCompetencia.AMB.toString(),  
										      null, null, null, null, null, 
										      vDataPrevia, null, null, 
										      vIphPhoSeq, null, vIphSeq, null, 
										      regProcAmb.getCodigoPac(),  
										      regProcAmb.getPhiSeq(), null, 
										      regProcAmb.getSeq(), 
										      null, null, null, null, null, null, null, null, null, 
										      RN_PMRP_GERA_NEW, null, null, null);
							
							rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.CANCELADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							
							if (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
								break;
							}
						}
					} //IF v_cobra_bpi = 'S'
					else { //-- BPA
						vCalculaIdade = faturamentoRN.verificarCaracteristicaExame(vIphSeq, vIphPhoSeq, DominioFatTipoCaractItem.CALCULA_IDADE_BPA);
						idadePac = Boolean.TRUE;
						if (Boolean.FALSE.equals(vCalculaIdade)) {
							idadePac = Boolean.FALSE;
						}
						else { //-- marcia 04/09/08 Validar idade do paciente com a do proced para proc com caracteristica 63 no BPA
							if (!(vIdadeMin == null || vIdadeMax == null || regProcAmb.getIdade() == null || (regProcAmb.getIdade() >= vIdadeMin && regProcAmb.getIdade() <= vIdadeMax))) {
								criarFatLogErrors("IDADE INCOMPATIVEL COM PROCEDIMENTO REALIZADO", 
												  DominioModuloCompetencia.AMB.toString(),  
												  null, null, null, null, null, null, null, null, null, null, 
												  vIphSeq, null, 
												  regProcAmb.getCodigoPac(),  
												  regProcAmb.getPhiSeq(), null, 
												  regProcAmb.getSeq(), 
												  null, null, null, null, null, null, null, null, null, 
												  RN_PMRP_GERA_NEW, 
												  null, null, new FatMensagemLog(97));
								
								//-- Marina 11/10/2009
								rnPmrpCancelaPmr(regProcAmb.getSeq(), pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
								
								vConsistente = Boolean.FALSE;
							}
						} //--fim marcia
						
						FatEspelhoProcedAmb espelho = new FatEspelhoProcedAmb();
						try {
							
							LOG.debug("Insere Espelho BPA " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							espelho.setDataPrevia(vDataPrevia);
							espelho.setIndConsistente(vConsistente);
							unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(regProcAmb.getUnfSeq());
							espelho.setUnidadeFuncional(unidadeFuncional);
							
							espelho.setCompetencia(vCompetencia);
							espelho.setProcedimentoHosp(vIphCodTab);
							
							if (vCavCodSus != null) {
								espelho.setAtvProfissional(vCavCodSus.byteValue());
							}
							
							espelho.setCodAtvProf(vItemCbo);
							espelho.setGrupoAtendimento(vGraCodSus);
							espelho.setFaixaEtaria(nvl(vCfeCodSus, 0).byteValue());
							espelho.setQuantidade(vIphQtdItem.intValue());
							
							if (vIphQtdItem != null) {
								if (regValor.getVlrServHospitalar() != null) {
									espelho.setVlrServHosp(new BigDecimal(vIphQtdItem).multiply(regValor.getVlrServHospitalar()));
								}
								
								if (regValor.getVlrServProfisAmbulatorio() != null) {
									espelho.setVlrServProf(new BigDecimal(vIphQtdItem).multiply(regValor.getVlrServProfisAmbulatorio()));
								} else {
									espelho.setVlrServProf(BigDecimal.ZERO);
								}
								
								if (regValor.getVlrSadt() != null) {
									espelho.setVlrSadt(new BigDecimal(vIphQtdItem).multiply(regValor.getVlrSadt()));
								}
								
								if (regValor.getVlrProcedimento() != null) {
									espelho.setVlrProc(new BigDecimal(vIphQtdItem).multiply(regValor.getVlrProcedimento()));
								}
								
								if (regValor.getVlrAnestesista() != null) {
									espelho.setVlrAnestes(new BigDecimal(vIphQtdItem).multiply(regValor.getVlrAnestesista()));
								}
							}								
							
							espelho.setPmrSeq(regProcAmb.getSeq());
							
							FatItensProcedHospitalar itensProcedHosp = itemProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vIphPhoSeq, vIphSeq));
							
							espelho.setItensProcedHospitalar(itensProcedHosp);
							espelho.setIndConsulta(vIndConsulta);
							espelho.setFatCompetencia(fatCompetencia);
							espelho.setCriadoEm(new Date());
							espelho.setCriadoPor(servidorLogado.getUsuario());
							espelho.setFccSeq(vIphFccSeq);
							espelho.setFcfSeq(vIphFcfSeq);
							
							if (idadePac && regProcAmb.getIdade() != null) {
								espelho.setIdade(regProcAmb.getIdade().shortValue());
							}
											
							espelho.setTipoFormulario("C");
							espelho.setOrigemInf(DominioBoletimAmbulatorio.BPA);
							
							if (regProcAmb.getCor() != null) {
								espelho.setRaca((byte) regProcAmb.getCor().getCodigo());
								
							} else {
								espelho.setRaca((byte) DominioCor.O.getCodigo()); //outro
							}								
							
							if (regProcAmb.getCodigoNac() != null) {
								AipNacionalidades nacionalidade = pacienteFacade.obterNacionalidade(regProcAmb.getCodigoNac()); 
								espelho.setNacionalidade(nacionalidade);
							}
							
							// MARINA - 14/05/2013
						    // bU8SCA DADOS CADASTRAIS DO PACIENTE
							VAipEnderecoPaciente enderecoPaciente = cadastroPacienteFacade.obterEndecoPaciente(regProcAmb.getCodigoPac());
							populaEnderecoPacienteEmEspelho(enderecoPaciente,espelho);

							espelhoProcedAmbDAO.persistir(espelho);
							espelhoProcedAmbDAO.flush();
							LOG.debug("Fim da Inserção do Espelho BPA " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							LOG.debug("Fim da Atualização da Situação BPA " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS));
							
						} catch (ConstraintViolationException e) {
							logError(e.getMessage());
						    for (ConstraintViolation<?> invalidValue : e.getConstraintViolations()) {
						        logError("Instance of bean class: " + invalidValue.getLeafBean().toString()  +
						                 " has an invalid property: " + invalidValue.getPropertyPath() +
						                 " with message: " + invalidValue.getMessage());
						    }
						    
						/* eSchweigert 02/09/2013
						 * Caso o erro seja de NEGÓCIO deve criar o FatLogError e prosseguir a operação
						 */
						} catch (BaseException e) {
//							userTransaction = reIniciarTransacao(userTransaction);
//							userTransaction = obterUserTransaction(null);
							
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							String stringException = sw.toString();								
							
							logTxt = "##### AGHU - BaseException - erro ao tentar inserir FAT_ESPELHOS_PROCED_AMB " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " <br /> " 
								   + espelho.toString() + " <br /> <br /> " 
								   + stringException;
							LOG.debug("erro: " + logTxt.replaceAll(REGEX_BR, QUEBRA_LINHA));
							
							logError(e);
							
							criarFatLogErrors("ERRO AO INSERIR ESPELHO BPA: " + e.getCause() + " " + e.getMessage(), 
											  DominioModuloCompetencia.AMB.toString(),  
											  null, null, null, null, null,
											  vDataPrevia, null, null, 
											  vIphPhoSeq,  null, vIphSeq, null, 
											  regProcAmb.getCodigoPac(),  
											  regProcAmb.getPhiSeq(),  null, 
											  regProcAmb.getSeq(), 
											  null, null, null, null, null, null, null, null, null, 
											  RN_PMRP_GERA_NEW, null, null, null);
							
							rnPmrpAtuSituacao(regProcAmb.getSeq(), pPrevia, DominioSituacaoProcedimentoAmbulatorio.CANCELADO, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
							
							if (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente)) {
								break;
							}
							
						// Caso contrário, deve abortar toda a operação
						} catch (Exception e) {
							/*try {
								userTransaction.rollback();
								this.rollbackTransaction();
							} catch (Exception e1) {
								logError(e1.getMessage());
							}*/
							
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							String stringException = sw.toString();								
							
							logTxt = "##### AGHU - Exception - erro ao executar a rotina geraEspelhoFaturamentoAmbulatorio " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " <br /> " + stringException;
							LOG.debug("erro: " + logTxt.replaceAll(REGEX_BR, QUEBRA_LINHA));
							
							logError(e);
							
							getFaturamentoFacade().enviaEmailResultadoEncerramentoAmbulatorio(logTxt);
							throw new BaseException(FaturamentoFatkPmrRNExceptionCode.ERRO_DURANTE_GERA_ESPELHO_FATURAMENTO_AMBULATORIO);
						}
					}
				}
				else { //-- if v_qtd_itens > 0
					vIphCodTab  = 0L;
					vCavCodSus  = 0;
					vGraCodSus  = 0;
					vCfeCodSus  = 0;
					vIphPhoSeq  = 0;
					vIphSeq 	= 0;
					vIphQtdItem = 0;
					vIphFccSeq  = null;
					vIphFcfSeq  = null;
				}
				if (vQtdItens == 0 || CoreUtil.igual(vIndice, vQtdItens) || (!Boolean.TRUE.equals(pPrevia) && Boolean.FALSE.equals(vConsistente))) {
					vIndice++;
					//break;
				}
			}//loop VO
		} //loop regProcAmb
		
		//caso ficou alguma transação pendente commita
		//userTransaction = commitUserTransaction(userTransaction);
		//this.commitTransaction();
		
		logTxt = "##### AGHU - Processou " + contNroReg + " de " + nroRegistrosAux + " " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date());
		LOG.debug(logTxt);
		getSchedulerFacade().adicionarLog(log, logTxt);

		//-- Ney, 27/04/2011, criada nova procedure para agrupar BPA e BPI na mesma rotina, permitindo
		//-- que a primeira pagina de BPI seja gerada a partir da ultima do BPA
		//-- Esta alteração foi feita para se adequar ao limite máximo de 3 digitos para o numero de páginas
		//-- gera espelho agrupado dos itens do ambulatorio
		//--fatk_pmr_rn.RN_PMRP_AGRU_BPA_new (
		//--           p_cpe_dt_hr_inicio, p_cpe_mes, p_cpe_ano, p_previa );
		//--fatk_pmr_rn.RN_PMRP_AGRU_BPI_new (
		//--           p_cpe_dt_hr_inicio, p_cpe_mes, p_cpe_ano, p_previa );
		//--
		//-- Ney, 27/04/2011, nova procedure para agrupar BPA e BPI na mesma rotina
		faturamentoRN.agrupaBpaBpi(pPrevia, fatCompetencia, nomeMicrocomputador, dataFimVinculoServidor);
		//userTransaction = commitUserTransaction(userTransaction);
		
		//atribuirContextoSessao(VariaveisSessaoEnum.FATK_PMR_RN_V_PMR_JOURNAL, Boolean.TRUE);
	//	atribuirContextoSessao(VariaveisSessaoEnum.FATK_PMR_RN_V_PMR_ENCERRAMENTO, Boolean.FALSE);
	}

	protected void populaEnderecoPacienteEmEspelho(
			VAipEnderecoPaciente enderecoPaciente, FatEspelhoProcedAmb espelho) {
		// Marina 27/05/2013
		if (enderecoPaciente != null) {
			
			if (enderecoPaciente.getCep() != null) {
				espelho.setEndCepPaciente(enderecoPaciente.getCep().intValue());	// substr(v_end.CEP,1,8)
			}
			
			espelho.setEndCodLogradouroPaciente(enderecoPaciente.getTlgCodigo() != null ?
												enderecoPaciente.getTlgCodigo() : 58
											   ); // , NVL(v_end.TLG_CODIGO,58)

			// , SUBSTR(v_end.LOGRADOURO,1,30)
			if(enderecoPaciente.getLogradouro() != null){
				espelho.setEndLogradouroPaciente(StringUtils.substring(enderecoPaciente.getLogradouro(),0,29));
			}
			
			// , SUBSTR(v_end.COMPL_LOGRADOURO,1,10)
			if(enderecoPaciente.getComplLogradouro() != null){
				espelho.setEndComplementoPaciente(StringUtils.substring(enderecoPaciente.getComplLogradouro(),0,9));
			}
			
			if(enderecoPaciente.getNroLogradouro() != null){
				espelho.setEndNumeroPaciente(enderecoPaciente.getNroLogradouro());
			} else {
				espelho.setEndNumeroPaciente(0);
			}
			
			// , SUBSTR(decode(v_end.BAIRRO, null, 'CENTRO',v_end.BAIRRO),1,30)
			if(enderecoPaciente.getBairro() != null){
				espelho.setEndBairroPaciente(StringUtils.substring(enderecoPaciente.getBairro(),0,29));
			} else {
				espelho.setEndBairroPaciente("CENTRO");
			}
		}
	}
	
	/**
	 * ORADB Procedure RN_PMRP_VER_UNI_PINT
	 * 
	 * Procedimento Hospitalar Interno do Item deve ser igual ao Procedimento
	 * Hospitalar Interno da Consulta Proced Hospitalar associada.
	 * 
	 * @param phiSeq
	 * @param prhPhiSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void rnPmrpVerUniPint(final Integer phiSeq,
			final Integer prhPhiSeq) throws ApplicationBusinessException {
		if (phiSeq != null && prhPhiSeq != null && !phiSeq.equals(prhPhiSeq)) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00265);
			// raise_application_error(-20000, 'FAT-00467 ' || '
			// RN_PMRP_VER_UNI_PINT');
		}
	}

	/**
	 * ORADB Procedure RN_PMRP_ATU_OTORRINO
	 * 
	 * @param pPmrSeq
	 * @throws ApplicationBusinessException  
	 */
	public void rnPmrpAtuOtorrino(Long pPmrSeq, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		final FatCandidatosApacOtorrino candidatosApacOtorrino = getFatCandidatosApacOtorrinoDAO()
				.obterCandidatosApacOtorrinoPorPmrSeqESituacao(pPmrSeq,
						DominioSituacao.A);

		if (candidatosApacOtorrino != null) {
			candidatosApacOtorrino.setIndSituacao(DominioSituacao.I);
			getFatCandidatosApacOtorrinoON().persistirCandidatosApacOtorrino(
					candidatosApacOtorrino, true, dataFimVinculoServidor);
		}
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void criarFatLogErrors(String erro, String modulo, Integer cthSeq,
			Long capAtmNumero, Byte capSeqp, Long codItemSus1,
			Long codItemSus2, Date dataPrevia, Byte icaSeqp, Short ichSeqp,
			Short iphPhoSeq, Short iphPhoSeqRealizado, Integer iphSeq,
			Integer iphSeqRealizado, Integer pacCodigo, Integer phiSeq,
			Integer phiSeqRealizado, Long pmrSeq, Integer prontuario,
			Long codItemSusRealizado, Long codItemSusSolicitado,
			Short iphPhoSeqItem1, Short iphPhoSeqItem2, Integer iphSeqItem1,
			Integer iphSeqItem2, Integer phiSeqItem1, Integer phiSeqItem2,
			String programa, Short serVinCodigoProf, Integer serMatriculaProf, FatMensagemLog fatMensagemLog) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		LOG.debug("Inicío da inclusão em FAT_LOG_ERROS " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		
		FatLogError fatLogError = new FatLogError();
		fatLogError.setCriadoEm(new Date());
		 
		 // eSchweigert 02/09/2013 adicionado substring para evitar InvalidStateException (valor muito grande)
		fatLogError.setCriadoPor(servidorLogado.getUsuario());
		fatLogError.setErro(erro);
		fatLogError.setModulo(modulo);
		 
		fatLogError.setCthSeq(cthSeq);
		fatLogError.setCapAtmNumero(capAtmNumero);
		fatLogError.setCapSeqp(capSeqp);
		fatLogError.setCodItemSus1(codItemSus1);
		fatLogError.setCodItemSus2(codItemSus2);
		fatLogError.setDataPrevia(dataPrevia);
		fatLogError.setIcaSeqp(icaSeqp);
		fatLogError.setIchSeqp(ichSeqp);
		fatLogError.setIphPhoSeq(iphPhoSeq);
		fatLogError.setIphPhoSeqRealizado(iphPhoSeqRealizado);
		fatLogError.setIphSeq(iphSeq);
		fatLogError.setIphSeqRealizado(iphSeqRealizado);
		fatLogError.setPacCodigo(pacCodigo);
		fatLogError.setPhiSeq(phiSeq);
		fatLogError.setPhiSeqRealizado(phiSeqRealizado);
		fatLogError.setPmrSeq(pmrSeq);
		fatLogError.setProntuario(prontuario);
		fatLogError.setCodItemSusRealizado(codItemSusRealizado);
		fatLogError.setCodItemSusSolicitado(codItemSusSolicitado);
		fatLogError.setIphPhoSeqItem1(iphPhoSeqItem1);
		fatLogError.setIphPhoSeqItem2(iphPhoSeqItem2);
		fatLogError.setIphSeqItem1(iphSeqItem1);
		fatLogError.setIphSeqItem2(iphSeqItem2);
		fatLogError.setPrograma(programa);
		fatLogError.setSerVinCodigoProf(serVinCodigoProf);
		fatLogError.setSerMatriculaProf(serMatriculaProf);
		getFaturamentoFacade().persistirLogError(fatLogError, false);
		LOG.debug("Fim da inclusão em FAT_LOG_ERROS " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
	}
	
	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected FatItemContaApacON getFatItemContaApacON() {
		return fatItemContaApacON;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}

	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected FatEspelhoProcedAmbDAO getFatEspelhoProcedAmbDAO() {
		return fatEspelhoProcedAmbDAO;
	}

	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	/**
	 * 
	 * @return
	 */
	protected FaturamentoON getFaturamentoON() {
		return faturamentoON;
	}

	/**
	 * 
	 * @return
	 */
	protected VerificacaoItemProcedimentoHospitalarRN getFatkIphRn() {

		return verificacaoItemProcedimentoHospitalarRN;
	}

	/**
	 * 
	 * @return
	 */
	protected VerificacaoFaturamentoSusRN getFatkSusRn() {

		return verificacaoFaturamentoSusRN;
	}

	/**
	 * 
	 * @return
	 */
	protected CaracteristicaItemProcedimentoHospitalarRN getFatkCihRn() {

		return caracteristicaItemProcedimentoHospitalarRN;
	}

	protected FatProcedTratamentosDAO getFatProcedTratamentosDAO() {
		return fatProcedTratamentosDAO;
	}

	protected FaturamentoFatkCpeRN getFaturamentoFatkCpeRN() {
		return faturamentoFatkCpeRN;
	}

	protected FaturamentoFatkInterfaceAacRN getFaturamentoFatkInterfaceAacRN() {
		return faturamentoFatkInterfaceAacRN;
	}

	protected FaturamentoFatkCapUniRN getFaturamentoFatkCapUniRN() {
		return faturamentoFatkCapUniRN;
	}

	protected FatConvFxEtariaItemDAO getFatConvFxEtariaItemDAO() {
		return fatConvFxEtariaItemDAO;
	}

	protected FatResumoApacsDAO getFatResumoApacsDAO() {
		return fatResumoApacsDAO;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected FatCandidatosApacOtorrinoDAO getFatCandidatosApacOtorrinoDAO() {
		return fatCandidatosApacOtorrinoDAO;
	}

	protected FatCandidatosApacOtorrinoON getFatCandidatosApacOtorrinoON() {
		return fatCandidatosApacOtorrinoON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
