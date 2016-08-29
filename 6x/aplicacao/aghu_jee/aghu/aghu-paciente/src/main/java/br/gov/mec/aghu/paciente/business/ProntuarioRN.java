package br.gov.mec.aghu.paciente.business;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelDataRespostaProtocolos;
import br.gov.mec.aghu.model.AelDataRespostaProtocolosId;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPacientesId;
import br.gov.mec.aghu.model.AelRespostaQuesitos;
import br.gov.mec.aghu.model.AelRespostaQuesitosId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipAlturaPacientesId;
import br.gov.mec.aghu.model.AipHistoriaFamiliares;
import br.gov.mec.aghu.model.AipPaDiastPacientes;
import br.gov.mec.aghu.model.AipPaDiastPacientesId;
import br.gov.mec.aghu.model.AipPaSistPacientes;
import br.gov.mec.aghu.model.AipPaSistPacientesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPerimCefalicoPacientes;
import br.gov.mec.aghu.model.AipPerimCefalicoPacientesId;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipPesoPacientesId;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.AipRegSanguineosId;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatPacienteTransplantes;
import br.gov.mec.aghu.model.FatPacienteTransplantesId;
import br.gov.mec.aghu.model.MamAlergias;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamTmpAlturas;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;
import br.gov.mec.aghu.model.MamTmpPerimCefalicos;
import br.gov.mec.aghu.model.MamTmpPesos;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.McoAchadoExameFisicos;
import br.gov.mec.aghu.model.McoAchadoExameFisicosId;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoAnamneseEfsJn;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoApgarsId;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoGestacaoPacientes;
import br.gov.mec.aghu.model.McoGestacaoPacientesId;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIddGestBallards;
import br.gov.mec.aghu.model.McoIddGestBallardsId;
import br.gov.mec.aghu.model.McoIddGestCapurros;
import br.gov.mec.aghu.model.McoIddGestCapurrosId;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoesId;
import br.gov.mec.aghu.model.McoIntercorrenciaNascs;
import br.gov.mec.aghu.model.McoIntercorrenciaNascsId;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoLogImpressoesId;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartosId;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.model.McoPlanoIniciaisId;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.model.McoReanimacaoRns;
import br.gov.mec.aghu.model.McoReanimacaoRnsId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoRecemNascidosId;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.McoResultadoExameSignifsId;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoSnappesId;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipHistoriaFamiliaresDAO;
import br.gov.mec.aghu.paciente.dao.AipPaDiastPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPaSistPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPerimCefalicoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.paciente.dao.PacIntdConvDAO;
import br.gov.mec.aghu.paciente.prontuario.vo.MatriculaVinculoVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@SuppressWarnings({"deprecation","PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
@Stateless
public class ProntuarioRN extends BaseBusiness {
    
    	private static final String V_VEIO_TROCA_PAC = "v_veio_troca_pac";

	@EJB
	private McokSnaRN mcokSnaRN;
	
	@EJB
	private McokIdcRN mcokIdcRN;
	
	@EJB
	private McokInnRN mcokInnRN;
	
	@EJB
	private McokEfiRN mcokEfiRN;
	
	@EJB
	private McokRnaRN mcokRnaRN;
	
	@EJB
	private McokBalRN mcokBalRN;
	
	@EJB
	private McokBsrRN mcokBsrRN;
	
	@EJB
	private McokMcoRN mcokMcoRN;
	
	@EJB
	private PacienteON pacienteON;
	
	@EJB
	private McokNasRN mcokNasRN;
	
	private static final Log LOG = LogFactory.getLog(ProntuarioRN.class);
	
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
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private PacIntdConvDAO pacIntdConvDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AipRegSanguineosDAO aipRegSanguineosDAO;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@Inject
	private AipHistoriaFamiliaresDAO aipHistoriaFamiliaresDAO;
	
	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AipPaDiastPacientesDAO aipPaDiastPacientesDAO;
	
	@Inject
	private AipPerimCefalicoPacientesDAO aipPerimCefalicoPacientesDAO;
	
	@Inject
	private AipPaSistPacientesDAO aipPaSistPacientesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6846670556853681563L;

	//private static final String ENTITY_MANAGER = "entityManager";

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	public enum ProntuarioRNExceptionCode implements BusinessExceptionCode {
		AIP_00444, AIP_00445, AIP_00446, AIP_00447, AIP_00448, AIP_00449, AIP_00450, AIP_00451, AIP_00452, AIP_00453, AIP_00454, AIP_00455, MAM_02157, MAM_02158, MAM_02159, MAM_02160, MAM_02161, MAM_03379, MAM_03380, MAM_04415, MAM_05086, MAM_05087, MAM_05088, MAM_02175, MAM_02152, MAM_02173, MAM_02153, MAM_02151, MAM_02156, MAM_02155, MAM_02154, MAM_02178, MAM_02177, MAM_02176, SUBS_PRONT_0001, SUBS_PRONT_0002, SUBS_PRONT_0003, SUBS_PRONT_0004, SUBS_PRONT_0005, SUBS_PRONT_0006, SUBS_PRONT_0007, SUBS_PRONT_0008, SUBS_PRONT_0009, SUBS_PRONT_0010, SUBS_PRONT_0011, SUBS_PRONT_0012, SUBS_PRONT_0013, SUBS_PRONT_0014, SUBS_PRONT_0015, SUBS_PRONT_0016, SUBS_PRONT_0017, SUBS_PRONT_0018, SUBS_PRONT_0019, SUBS_PRONT_0020, SUBS_PRONT_0021, SUBS_PRONT_0022, SUBS_PRONT_0023, SUBS_PRONT_0024, SUBS_PRONT_0025, SUBS_PRONT_0026, SUBS_PRONT_0027, SUBS_PRONT_0028, SUBS_PRONT_0029, SUBS_PRONT_0030, SUBS_PRONT_0031, SUBS_PRONT_0032, SUBS_PRONT_0033, SUBS_PRONT_0034, SUBS_PRONT_0035, SUBS_PRONT_0036, SUBS_PRONT_0037, SUBS_PRONT_0038, SUBS_PRONT_0039, SUBS_PRONT_0040, SUBS_PRONT_0041, SUBS_PRONT_0042, SUBS_PRONT_0043, SUBS_PRONT_0044, SUBS_PRONT_0045, SUBS_PRONT_0046, SUBS_PRONT_0047, SUBS_PRONT_0048, SUBS_PRONT_0049, SUBS_PRONT_0050, SUBS_PRONT_0051, SUBS_PRONT_0052, SUBS_PRONT_0053, SUBS_PRONT_0054, SUBS_PRONT_0055, SUBS_PRONT_0056, SUBS_PRONT_0057, SUBS_PRONT_0058, SUBS_PRONT_0059, SUBS_PRONT_0060, SUBS_PRONT_0061, SUBS_PRONT_0062, SUBS_PRONT_0063, SUBS_PRONT_0064, SUBS_PRONT_0065, SUBS_PRONT_0070, SUBS_PRONT_0071;
	}

	public static enum FlagsSubstituiProntuario {
		AACK_CON_3_RN("v_subst_prontuario"), // TRUE, FALSE
		FATK_DCS_RN("v_subst_pront_dcs"), // S, N
		MCOK_EFI_RN(V_VEIO_TROCA_PAC), // S, N
		MCOK_BSR_RN("v_ver_dt_romp"), // TRUE, FALSE
		MCOK_NAS_RN(V_VEIO_TROCA_PAC), // S, N
		MCOK_INN_RN(V_VEIO_TROCA_PAC), // S, N
		MCOK_RNA_RN(V_VEIO_TROCA_PAC), // S, N
		MCOK_SNA_RN("v_veio_subs_gesta"), // S, N
		MCOK_BAL_RN("v_veio_subs_gesta_bal"), // S, N
		MCOK_IDC_RN("v_veio_subs_gesta_idc"), // S, N
		AIPK_PAC_ATU("flag_usuario_recadastro"); // TRUE, FALSE

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private String flag;

		private FlagsSubstituiProntuario(String flag) {
			this.flag = flag;
		}

		public String getNomePackage() {
			return this.toString();
		}

		public String getNomeFlag() {
			return this.flag;
		}
	}

	public static enum ValorFlagsSubstituiProntuario {
		S, N, TRUE, FALSE;
	}

	/**
	 * ORADB Function AGHK_UTIL.MODIFICADOS.
	 * 
	 * Implementa a função modificados da package aghk_util.<br/>
	 * Obs.: A classe dos objetos informados por parâmetro deve implementar o
	 * método equals.
	 * 
	 * @param <T>
	 *            Tipo genérico para indicar que os valores informados por
	 *            parâmetro devem ser do mesmo tipo.
	 * @param newValue
	 *            Valor novo.
	 * @param oldValue
	 *            Valor antigo.
	 * @return Booleano indicando se o valor foi modificado, ou seja, se os
	 *         valores dos parâmetros são diferentes.
	 */
	public <T> Boolean modificados(T newValue, T oldValue) {
		Boolean result = Boolean.FALSE;

		if (newValue != null && oldValue != null) {
			result = !newValue.equals(oldValue);
		} else if (newValue != null || oldValue != null) {
			result = Boolean.TRUE;
		}

		return result;
	}

	public void setarFlagProntuario(final FlagsSubstituiProntuario flag, final ValorFlagsSubstituiProntuario valor, String servidorLogado)
			throws ApplicationBusinessException {
		if (!aipPacientesDAO.isOracle()) {
			this.logDebug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
			return;
		}

		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AIPP_FLAG_SUBSTITUI_PRONT.toString();
		final String p1 = flag.getNomePackage();
		final String p2 = flag.getNomeFlag();
		final String p3 = valor.toString();

		AghWork work = new AghWork(servidorLogado) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					StringBuilder sbCall = new StringBuilder("{call ");
					sbCall.append(nomeObjeto)
					.append("(?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.VARCHAR, p1);
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.VARCHAR, p2);
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.VARCHAR, p3);

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		try {
			this.logDebug("Setando a flag mapeada por " + flag.toString());
			
			aipAlturaPacientesDAO.doWork(work);			
			
		} catch (Exception e) {
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(p1, p2, p3);
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			// Tratar erro com mensagem padrão para problemas genéricos em
			// execução de procedures/functions do Oracle
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(p1, p2, p3);
			this.logError(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores));
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
		}
		
		this.logDebug("Flag setada com sucesso");

	}

	/**
	 * ORADB Procedure AIPK_SUBST.AIPP_SUBS_PAC_PIP.
	 * 
	 * Implementa a procedure AIPP_SUBS_PAC_PIP da package AIPK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	protected void aippSubsPacPip(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		AipPaSistPacientesDAO aipPaSistPacientesDAO = this.getAipPaSistPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		
		
		List<AipPaSistPacientes> sistPacientes = aipPaSistPacientesDAO.listarPaSistPacientes(codigoOrigem);

		Short seqp = aipPaSistPacientesDAO.buscaMaiorSeqp(codigoOrigem);
		if (seqp == null) {
			seqp = 0;
		}

		for (AipPaSistPacientes sistPaciente : sistPacientes) {
			seqp++;

			try {
				AipPaSistPacientesId id = new AipPaSistPacientesId(codigoDestino, seqp);

				AipPaSistPacientes sistPacienteInsert = new AipPaSistPacientes();
				sistPacienteInsert.setId(id);
				sistPacienteInsert.setRapServidor(sistPaciente.getRapServidor());
				sistPacienteInsert.setCriadoEm(sistPaciente.getCriadoEm());
				sistPacienteInsert.setSistolica(sistPaciente.getSistolica());

				aipPaSistPacientesDAO.persistir(sistPacienteInsert);
				aipPaSistPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00444, e.getCause());
			}

			try {
				// atualiza FKs
				List<MamRespostaAnamneses> anamneses = ambulatorioFacade.listarRespostasAnamnesesPip(codigoOrigem, sistPaciente.getId().getSeqp());

				for (MamRespostaAnamneses anamnese : anamneses) {
					anamnese.setPipPacCodigo(codigoDestino);
					anamnese.setPipSeqp(seqp);

					ambulatorioFacade.atualizarAnamnese(anamnese);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00445, e.getCause());
			}

			try {
				List<MamRespostaEvolucoes> evolucoes = ambulatorioFacade.listarRespostasEvolucoesPip(codigoOrigem, sistPaciente
						.getId().getSeqp());

				for (MamRespostaEvolucoes evolucao : evolucoes) {
					evolucao.setPipPacCodigo(codigoDestino);
					evolucao.setPipSeqp(seqp);

					ambulatorioFacade.atualizarRespostaEvolucao(evolucao);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00446, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipPaSistPacientesDAO.remover(sistPaciente);
				aipPaSistPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00447, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure AIPK_SUBST.AIPP_SUBS_PAC_PDP
	 * 
	 * Implementa a procedure AIPP_SUBS_PAC_PDP da package AIPK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	protected void aippSubsPacPdp(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		AipPaDiastPacientesDAO aipPaDiastPacientesDAO = this.getAipPaDiastPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		
		List<AipPaDiastPacientes> diastPacientes = aipPaDiastPacientesDAO.listarPaDiastPacientes(codigoOrigem);

		Short seqp = aipPaDiastPacientesDAO.buscaMaiorSeqp(codigoOrigem);
		if (seqp == null) {
			seqp = 0;
		}

		for (AipPaDiastPacientes diastPaciente : diastPacientes) {
			seqp++;

			try {
				AipPaDiastPacientesId id = new AipPaDiastPacientesId(codigoDestino, seqp);

				AipPaDiastPacientes diastPacienteInsert = new AipPaDiastPacientes();
				diastPacienteInsert.setId(id);
				diastPacienteInsert.setRapServidor(diastPaciente.getRapServidor());
				diastPacienteInsert.setDiastolica(diastPaciente.getDiastolica());
				diastPacienteInsert.setCriadoEm(diastPaciente.getCriadoEm());

				aipPaDiastPacientesDAO.persistir(diastPacienteInsert);
				aipPaDiastPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00448, e.getCause());
			}

			try {
				// atualiza FKs
				List<MamRespostaAnamneses> anamneses = ambulatorioFacade.listarRespostasAnamnesesPdp(codigoOrigem, diastPaciente
						.getId().getSeqp());

				for (MamRespostaAnamneses anamnese : anamneses) {
					anamnese.setPdpPacCodigo(codigoDestino);
					anamnese.setPdpSeqp(seqp);

					ambulatorioFacade.atualizarAnamnese(anamnese);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00449, e.getCause());
			}

			try {
				List<MamRespostaEvolucoes> evolucoes = ambulatorioFacade.listarRespostasEvolucoesPdp(codigoOrigem, diastPaciente.getId().getSeqp());

				for (MamRespostaEvolucoes evolucao : evolucoes) {
					evolucao.setPdpPacCodigo(codigoDestino);
					evolucao.setPdpSeqp(seqp);

					ambulatorioFacade.atualizarRespostaEvolucao(evolucao);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00450, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipPaDiastPacientesDAO.remover(diastPaciente);
				aipPaDiastPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00451, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure AIPK_SUBST.AIPP_SUBS_PAC_PLP
	 * 
	 * Implementa a procedure AIPP_SUBS_PAC_PLP da package AIPK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	protected void aippSubsPacPlp(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		AipPerimCefalicoPacientesDAO aipPerimCefalicoPacientesDAO = this.getAipPerimCefalicoPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		
		List<AipPerimCefalicoPacientes> cefalicos = aipPerimCefalicoPacientesDAO.listarPerimCefalicosPacientes(codigoOrigem);

		Short seqp = aipPerimCefalicoPacientesDAO.buscaMaiorSeqp(codigoOrigem);
		if (seqp == null) {
			seqp = 0;
		}

		for (AipPerimCefalicoPacientes cefalico : cefalicos) {
			seqp++;

			try {
				AipPerimCefalicoPacientesId id = new AipPerimCefalicoPacientesId(codigoDestino, seqp);

				AipPerimCefalicoPacientes cefalicoInsert = new AipPerimCefalicoPacientes();
				cefalicoInsert.setId(id);
				cefalicoInsert.setRapServidor(cefalico.getRapServidor());
				cefalicoInsert.setCriadoEm(cefalico.getCriadoEm());
				cefalicoInsert.setPerimetroCefalico(cefalico.getPerimetroCefalico());

				aipPerimCefalicoPacientesDAO.persistir(cefalicoInsert);
				aipPerimCefalicoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00452, e.getCause());
			}

			try {
				// atualiza FKs
				List<MamRespostaAnamneses> anamneses = ambulatorioFacade.listarRespostasAnamnesesPlp(codigoOrigem, cefalico.getId().getSeqp());
				for (MamRespostaAnamneses anamnese : anamneses) {
					anamnese.setPlpPacCodigo(codigoDestino);
					anamnese.setPlpSeqp(seqp);

					ambulatorioFacade.atualizarAnamnese(anamnese);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00453, e.getCause());
			}

			try {
				List<MamRespostaEvolucoes> evolucoes = ambulatorioFacade.listarRespostasEvolucoesPlp(codigoOrigem, cefalico.getId().getSeqp());
				for (MamRespostaEvolucoes evolucao : evolucoes) {
					evolucao.setPlpPacCodigo(codigoDestino);
					evolucao.setPlpSeqp(seqp);

					ambulatorioFacade.atualizarRespostaEvolucao(evolucao);
				}
				this.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00454, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipPerimCefalicoPacientesDAO.remover(cefalico);
				aipPerimCefalicoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.AIP_00455, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure MAMK_SUBST.MAMP_ATU_ALTURA_REV
	 * 
	 * Implementa a procedure MAMP_ATU_ALTURA_REV da package MAMK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	public void mampAtuAlturaRev(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();

		List<MamRespostaEvolucoes> evolucoes = ambulatorioFacade.listarRespostasEvolucoesPorAtpPacCodigo(codigoOrigem);

		AipAlturaPacientes altura = null;
		for (MamRespostaEvolucoes evolucao : evolucoes) {
			try {
				// insere nova altura com o código correto do paciente
				altura = aipAlturaPacientesDAO.obterPorChavePrimaria(new AipAlturaPacientesId(codigoOrigem, evolucao
						.getAipAlturaPaciente().getId().getCriadoEm()));
				AipAlturaPacientesId id = new AipAlturaPacientesId(codigoDestino, altura.getId().getCriadoEm());

				AipAlturaPacientes alturaInsert = new AipAlturaPacientes();
				alturaInsert.setId(id);
				alturaInsert.setAltura(altura.getAltura());
				alturaInsert.setRapServidor(altura.getRapServidor());
				alturaInsert.setRealEstimado(altura.getRealEstimado());
				alturaInsert.setIndMomento(altura.getIndMomento());
				alturaInsert.setConNumero(altura.getConNumero());

				aipAlturaPacientesDAO.persistir(alturaInsert);
				aipAlturaPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02176, e.getCause());
			}

			try {
				// atualiza o código correto do paciente na FK da altura
				List<MamRespostaEvolucoes> _evolucoes = ambulatorioFacade.listarRespostasEvolucoesPorAtpPacCodigoEAtpCriadoEm(codigoOrigem, evolucao.getAipPesoPaciente()
						.getId().getCriadoEm());
				
				if (_evolucoes != null && !_evolucoes.isEmpty()) {
					MamRespostaEvolucoes evolucaoUpdate = _evolucoes.get(0);
					AipAlturaPacientesId idUpdate = evolucaoUpdate.getAipAlturaPaciente().getId();
					idUpdate.setPacCodigo(codigoDestino); // não sei se isso vai
															// funcionar, acho que
															// teremos que fazer via
															// JPQL.
	
					ambulatorioFacade.atualizarRespostaEvolucao(evolucaoUpdate);
					this.flush();
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02177, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipAlturaPacientesDAO.remover(altura);
				aipAlturaPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02178, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure MAMK_SUBST.MAMP_ATU_PESO_REV
	 * 
	 * Implementa a procedure MAMP_ATU_PESO_REV da package MAMK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @param servidorLogado 
	 */
	public void mampAtuPesoRev(Integer codigoOrigem, Integer codigoDestino, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();

		List<MamRespostaEvolucoes> evolucoes = ambulatorioFacade.listarRespostasEvolucoesPorPepPacCodigo(codigoOrigem);

		AipPesoPacientes peso = null;
		for (MamRespostaEvolucoes evolucao : evolucoes) {
			try {
				// insere novo peso com o código correto do paciente
				peso = aipPesoPacientesDAO.obterPorChavePrimaria(new AipPesoPacientesId(codigoOrigem, evolucao.getAipPesoPaciente()
						.getId().getCriadoEm()));

				AipPesoPacientesId id = new AipPesoPacientesId(codigoDestino, peso.getId().getCriadoEm());

				AipPesoPacientes pesoInsert = new AipPesoPacientes();
				pesoInsert.setId(id);
				pesoInsert.setPeso(peso.getPeso());
				pesoInsert.setRapServidor(peso.getRapServidor());
				pesoInsert.setRealEstimado(peso.getRealEstimado());
				pesoInsert.setIndMomento(peso.getIndMomento());
				pesoInsert.setConNumero(peso.getConNumero());

				this.getCadastroPacienteFacade().persistirPesoPaciente(pesoInsert, servidorLogado);
				// raise_application_error (-20000,'MAM-02154#1'||SQLERRM); or
				// -20000
				aipPesoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						ProntuarioRNExceptionCode.MAM_02154, e.getCause());
			}

			try {
				// atualiza o código correto do paciente no FK do peso
				List<MamRespostaEvolucoes> _evolucoes = ambulatorioFacade.listarRespostasEvolucoesPorPepPacCodigoEPepCriadoEm(
						codigoOrigem, evolucao.getAipPesoPaciente().getId().getCriadoEm());
				if (_evolucoes != null && !_evolucoes.isEmpty()) {
					MamRespostaEvolucoes evolucaoUpdate = _evolucoes.get(0);
					AipPesoPacientesId idUpdate = evolucaoUpdate.getAipPesoPaciente().getId();
					idUpdate.setPacCodigo(codigoDestino); // não sei se isso vai
															// funcionar, acho
															// que
															// teremos que fazer
															// via
															// JPQL.

					//#23337 - Implementar aqui triggers de update de MamRespostaEvolucoes
					ambulatorioFacade.atualizarRespostaEvolucao(evolucaoUpdate);
					this.flush();
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02155, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipPesoPacientesDAO.remover(peso);
				aipPesoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						ProntuarioRNExceptionCode.MAM_02156, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure MAMK_SUBST.MAMP_ATU_PESO_REA
	 * 
	 * Implementa a procedure MAMP_ATU_PESO_REA da package MAMK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	public void mampAtuPesoRea(Integer codigoOrigem, Integer codigoDestino)
			throws ApplicationBusinessException {
		AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();

		List<MamRespostaAnamneses> anamneses = ambulatorioFacade.listarRespostasAnamnesesPorPepPacCodigo(codigoOrigem);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AipPesoPacientes peso = null;
		for (MamRespostaAnamneses anamnese : anamneses) {
			try {
				// insere novo peso com o código correto do paciente
				peso = aipPesoPacientesDAO.obterPorChavePrimaria(new AipPesoPacientesId(codigoOrigem, anamnese.getAipPesoPaciente()
						.getId().getCriadoEm()));

				AipPesoPacientesId id = new AipPesoPacientesId(codigoDestino, peso.getId().getCriadoEm());

				AipPesoPacientes pesoInsert = new AipPesoPacientes();
				pesoInsert.setId(id);
				pesoInsert.setPeso(peso.getPeso());
				pesoInsert.setRapServidor(peso.getRapServidor());
				pesoInsert.setRealEstimado(peso.getRealEstimado());
				pesoInsert.setIndMomento(peso.getIndMomento());
				pesoInsert.setConNumero(peso.getConNumero());

				this.getCadastroPacienteFacade().persistirPesoPaciente(pesoInsert, servidorLogado);
				// raise_application_error (-20000,'MAM-02151#1'||SQLERRM);
				aipPesoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						ProntuarioRNExceptionCode.MAM_02151, e.getCause());
			}

			try {
				// atualiza o código correto do paciente no FK do peso
				List<MamRespostaAnamneses> listaAnamneses = ambulatorioFacade.listarRespostasAnamnesesPorPepPacCodigoEPepCriadoEm(
						codigoOrigem, anamnese.getAipPesoPaciente().getId().getCriadoEm());
				if (listaAnamneses != null && !listaAnamneses.isEmpty()) {
					MamRespostaAnamneses anamneseUpdate = listaAnamneses.get(0);
					AipPesoPacientesId idUpdate = anamneseUpdate.getAipPesoPaciente().getId();
					idUpdate.setPacCodigo(codigoDestino); // não sei se isso vai
															// funcionar, acho
															// que
															// teremos que fazer
															// via
															// JPQL.

					//#23337 - Aqui é preciso implementar as triggers de update de MamRespostaAnamneses
					ambulatorioFacade.atualizarAnamnese(anamneseUpdate);
					this.flush();
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						ProntuarioRNExceptionCode.MAM_02152, e.getCause());
			}

			// exclui o peso registrado com o código errado do paciente
			try {
				aipPesoPacientesDAO.remover(peso);
				aipPesoPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						ProntuarioRNExceptionCode.MAM_02153, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure MAMK_SUBST.MAMP_ATU_ALTURA_REA
	 * 
	 * Implementa a procedure MAMP_ATU_ALTURA_REA da package MAMK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	public void mampAtuAlturaRea(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();

		List<MamRespostaAnamneses> anamneses = ambulatorioFacade.listarRespostasAnamnesesPorAtpPacCodigo(codigoOrigem);

		AipAlturaPacientes altura = null;
		for (MamRespostaAnamneses anamnese : anamneses) {
			try {
				// insere nova altura com o código correto do paciente
				altura = aipAlturaPacientesDAO.obterPorChavePrimaria(new AipAlturaPacientesId(codigoOrigem, anamnese
						.getAipAlturaPaciente().getId().getCriadoEm()));

				AipAlturaPacientesId id = new AipAlturaPacientesId(codigoDestino, altura.getId().getCriadoEm());

				AipAlturaPacientes alturaInsert = new AipAlturaPacientes();
				alturaInsert.setId(id);
				alturaInsert.setAltura(altura.getAltura());
				alturaInsert.setRapServidor(altura.getRapServidor());
				alturaInsert.setRealEstimado(altura.getRealEstimado());
				alturaInsert.setIndMomento(altura.getIndMomento());
				alturaInsert.setConNumero(altura.getConNumero());

				aipAlturaPacientesDAO.persistir(alturaInsert);
				aipAlturaPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02173, e.getCause());
			}

			try {
				// atualiza o código correto do paciente na FK da altura
				List<MamRespostaAnamneses> _anamneses = ambulatorioFacade.listarRespostasAnamnesesPorAtpPacCodigoEAtpCriadoEm(codigoOrigem, anamnese.getAipPesoPaciente()
						.getId().getCriadoEm());
				
				if (_anamneses != null && !_anamneses.isEmpty()) {
					MamRespostaAnamneses anamneseUpdate = _anamneses.get(0);
					AipAlturaPacientesId idUpdate = anamneseUpdate.getAipAlturaPaciente().getId();
					idUpdate.setPacCodigo(codigoDestino); // não sei se isso vai
															// funcionar, acho que
															// teremos que fazer via
															// JPQL.
	
					ambulatorioFacade.atualizarAnamnese(anamneseUpdate);
					this.flush();
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02152, e.getCause());
			}

			try {
				// exclui o peso registrado com o código errado do paciente
				aipAlturaPacientesDAO.remover(altura);
				aipAlturaPacientesDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02175, e.getCause());
			}
		}
	}

	/**
	 * ORADB Procedure MAMK_SUBST.MAMP_ATU_PAC_TMP
	 * 
	 * Implementa a procedure MAMP_ATU_PAC_TMP da package MAMK_SUBST.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	public void mampAtuPacTmp(Integer codigoOrigem, Integer codigoDestino) throws ApplicationBusinessException {
		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			// atualiza o código correto do paciente das FKs do peso

			List<MamTmpPesos> pesos = ambulatorioFacade.listaTmpPesosPorPacCodigo(codigoOrigem);
			for (MamTmpPesos peso : pesos) {
				peso.setPacCodigo(codigoDestino);

				ambulatorioFacade.atualizarTmpPeso(peso);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02157, e.getCause());
		}

		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			List<MamTmpAlturas> alturas = ambulatorioFacade.listaTmpAlturasPorPacCodigo(codigoOrigem);
			for (MamTmpAlturas altura : alturas) {
				altura.setPacCodigo(codigoDestino);

				ambulatorioFacade.atualizarTmpAltura(altura);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02158, e.getCause());
		}

		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			List<MamTmpPerimCefalicos> cefalicos = ambulatorioFacade.listaTmpPerimCefalicosPorPacCodigo(codigoOrigem);

			for (MamTmpPerimCefalicos cefalico : cefalicos) {
				cefalico.setPacCodigo(codigoDestino);

				ambulatorioFacade.atualizarTmpPerimCefalicos(cefalico);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02159, e.getCause());
		}

		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			List<MamTmpPaSistolicas> sistolicas = ambulatorioFacade.listaTmpPaSistolicasPorPacCodigo(codigoOrigem);

			for (MamTmpPaSistolicas sistolica : sistolicas) {
				sistolica.setPacCodigo(codigoDestino);

				ambulatorioFacade.atualizarTmpPaSistolicas(sistolica);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02160, e.getCause());
		}

		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			List<MamTmpPaDiastolicas> diastolicas = ambulatorioFacade.listaTmpPaDiastolicasPorPacCodigo(codigoOrigem);

			for (MamTmpPaDiastolicas diastolica : diastolicas) {
				diastolica.setPacCodigo(codigoDestino);

				ambulatorioFacade.atualizarTmpPaDiastolicas(diastolica);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02161, e.getCause());
		}
	}

	/**
	 * ORADB Procedure FATP_SUBS_APAC_TRANS
	 * 
	 * Implementa a procedure FATP_SUBS_APAC_TRANS.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	protected void fatpSubsApacTrans(Integer codigoOrigem, Integer codigoDestino) {
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		List<FatPacienteTransplantes> transplantes = faturamentoFacade.listarPacientesTransplantesPorPacCodigo(codigoOrigem);
		for (FatPacienteTransplantes transplante : transplantes) {
			FatPacienteTransplantesId id = new FatPacienteTransplantesId();
			id.setDtInscricaoTransplante(transplante.getId().getDtInscricaoTransplante());
			id.setPacCodigo(codigoDestino);
			id.setTtrCodigo(transplante.getId().getTtrCodigo());

			FatPacienteTransplantes transplanteInsert = new FatPacienteTransplantes();
			transplanteInsert.setId(id);
			transplanteInsert.setDtTransplante(transplante.getDtTransplante());
			transplanteInsert.setIndSituacao(transplante.getIndSituacao());
			transplanteInsert.setIphPhoSeq(transplante.getIphPhoSeq());
			transplanteInsert.setIphSeq(transplante.getIphSeq());
			transplanteInsert.setNroAihTransplante(transplante.getNroAihTransplante());

			faturamentoFacade.persistirFatPacienteTransplantes(transplanteInsert); // raise_application_error
															// (-20000,'Erro ao
															// inserir PACIENTE
															// TRANSPLANTE'||SQLERRM);
			faturamentoFacade.removerFatPacienteTransplantes(transplante); // raise_application_error
													// (-20000,'Erro ao deletar
													// PACIENTE TRANSPLANTES
													// '||SQLERRM);
		}
		this.flush();

		List<AacAtendimentoApacs> atendimentos = ambulatorioFacade.listarAtendimentoApacPorPtrPacCodigo(codigoOrigem);
		for (AacAtendimentoApacs atendimento : atendimentos) {
			atendimento.setPtrPacCodigo(codigoDestino);

			ambulatorioFacade.persistirAacAtendimentoApacs(atendimento); // raise_application_error
														// (-20000,'Erro no
														// update APAC
														// TRANSPLANTE'||SQLERRM);
		}
		this.flush();
	}
	
	protected List<MamAlergias> pesquisarMamAlergias(Integer pacCodigo) {
		return this.getAmbulatorioFacade().pesquisarMamAlergiasPorPaciente( pacCodigo );
	}

	/**
	 * ORADB Procedure MAMP_SUBSTITUI_PRONT
	 * 
	 * Implementa a procedure MAMP_SUBSTITUI_PRONT.
	 * 
	 * MÓDULO AMBULATÓRIO
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws ApplicationBusinessException 
	 */
	public void mampSubstituiPront(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ApplicationBusinessException {
		PacienteON pacienteON = this.getPacienteON();
		
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		//#23337 - Bloco comentado - Não utilizado nos HUs implantados
		//MamAlergiasDAO mamAlergiasDAO = this.getMamAlergiasDAO();
		//MamTrgAlergiasDAO mamTrgAlergiasDAO = this.getMamTrgAlergiasDAO();
		//MamTriagensDAO mamTriagensDAO = this.getMamTriagensDAO();
		
		AipPacientes pacienteOrigem = pacienteON.obterPaciente(codigoOrigem);
		AipPacientes pacienteDestino = aipPacientesDAO.obterPorChavePrimaria(codigoDestino);
		
		IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();

		//#23337 - Bloco comentado - Não utilizado nos HUs implantados
//		try {
//			List<MamTriagens> triagens = mamTriagensDAO.listarTriagensPorPacCodigo(codigoOrigem);
//			for (MamTriagens triagem : triagens) {
//				triagem.setPacCodigo(codigoDestino);
//
//				mamTriagensDAO.atualizar(triagem, false);
//			}
//			mamTriagensDAO.flush();
//		} catch (Exception e) {
//			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02815, e.getCause());
//		}

		
		//#23337 - Bloco comentado - Não utilizado em UFU
//		try {
//			List<MamTrgAlergias> trgAlergias = mamTrgAlergiasDAO.listarTrgAlergiasPorPacCodigo(codigoOrigem);
//
//			for (MamTrgAlergias trgAlergia : trgAlergias) {
//				trgAlergia.setPacCodigo(codigoDestino);
//
//				mamTrgAlergiasDAO.atualizar(trgAlergia, false);
//			}
//			mamTrgAlergiasDAO.flush();
//		} catch (Exception e) {
//			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_02816, e.getCause());
//		}

		//#23337 - OK
		try {
			List<MamReceituarios> receituarios = ambulatorioFacade.listarReceituariosPorPaciente(pacienteOrigem);

			for (MamReceituarios receituario : receituarios) {
				receituario.setPaciente(pacienteDestino);
				ambulatorioFacade.atualizarReceituario(receituario);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_03379, e.getCause());
		}

		//#23337 - Modificado, trigger MAMT_ATE_BRU (movida de MarcacaoConsultaRN para MamAtestadosRN ao implementar a estoria #46226)
		try {
			List<MamAtestados> atestados = ambulatorioFacade.listarAtestadosPorCodigoPaciente(codigoOrigem);
			for (MamAtestados atestado : atestados) {
				MamAtestados atestadoOld = new MamAtestados();
				copiarPropriedades(atestadoOld, atestado);
				AipPacientes paciente = pacienteFacade.obterPacientePorCodigo(codigoDestino);
				atestado.setAipPacientes(paciente);
				ambulatorioFacade.validarPreAtualizarAtestado(atestado, atestadoOld);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_03380, e.getCause());
		}

		//#23337 - Modificado, trigger MAMT_NEV_BRU implementada
		try {
			List<MamNotaAdicionalEvolucoes> notas = ambulatorioFacade.listarNotasAdicinaisEvolucoesPorCodigoPaciente(codigoOrigem);
			for (MamNotaAdicionalEvolucoes notaEvolucao : notas) {
				MamNotaAdicionalEvolucoes notaEvolucaoOld = new MamNotaAdicionalEvolucoes();
				copiarPropriedades(notaEvolucaoOld, notaEvolucao);
				notaEvolucao.setPaciente(pacienteDestino);
				ambulatorioFacade.atualizarNotaAdicionalEvolucoes(notaEvolucao, notaEvolucaoOld);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_04415, e.getCause());
		}

		//#23337 - Modificado, trigger MAMT_ANA_BRU implementada
		try {
			List<MamAnamneses> anamneses = ambulatorioFacade.listarAnamnesesPorCodigoPaciente(codigoOrigem);
			for (MamAnamneses anamnese : anamneses) {
				anamnese.setPaciente(pacienteDestino);

				ambulatorioFacade.atualizarAnamnese(anamnese);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_05086, e.getCause());
		}

		//#23337 - Modificado, trigger MAMT_EVO_BRU implementada
		try {
			List<MamEvolucoes> evolucoes = ambulatorioFacade.listarEvolucoesPorCodigoPaciente(codigoOrigem);
			for (MamEvolucoes evolucao : evolucoes) {
				evolucao.setPaciente(pacienteDestino);

				ambulatorioFacade.atualizarEvolucao(evolucao);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_05087, e.getCause());
		}

		//#23337 - Modificado, trigger MAMT_ALG_BRU implementada
		try {
			List<MamAlergias> alergias = pesquisarMamAlergias(codigoOrigem);
			for (MamAlergias alergia : alergias) {
				MamAlergias alergiaOld = new MamAlergias();
				copiarPropriedades(alergiaOld, alergia);
				alergia.setPaciente(pacienteDestino);
				ambulatorioFacade.atualizarAlergias(alergia, alergiaOld);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.MAM_05088, e.getCause());
		}
	}

	/**
	 * ORADB Procedure AIPP_SUBST_PRONT_CONV
	 * 
	 * Implementa a procedure aipp_subst_pront_conv.
	 * 
	 * @param prontuarioOrigem
	 * @param prontuarioDestino
	 */
	public void aippSubstProntConv(Integer prontuarioOrigem, Integer prontuarioDestino) throws ApplicationBusinessException {
		List<CntaConv> contas = this.getFaturamentoFacade().listarCntaConvPorIntdCodPrnt(prontuarioOrigem);
		for (CntaConv conta : contas) {
			ffcAlteraProntuarioConta(conta, prontuarioDestino);
		}
	}

	/**
	 * ORADB FFC_ALTERA_PRONTUARIO_CONTA.ver_cnta_dif
	 * 
	 * Implementa a procedure ver_cnta_dif.
	 * 
	 * @param prontuario
	 * @param data
	 */
	protected void verCntaDif(Integer prontuario, Date data) throws ApplicationBusinessException {
		try {
			IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
			IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
			
			CntaConv cnta = faturamentoFacade.obterCntaConv(prontuario, data, (short) 76);

			if (cnta != null) {
				Set<ProcEfet> procs = new HashSet<ProcEfet>(internacaoFacade.listarProcEfetPorCtacvNro(cnta.getNro()));
				for (ProcEfet procEfet : procs) {
					ProcEfet procEfetDestino = new ProcEfet();
					copiarPropriedades(procEfetDestino, procEfet);
					procEfetDestino.getId().setIntdCodPrnt(prontuario);
					internacaoFacade.removerProcEfet(procEfet, true);
					internacaoFacade.desatacharProcEfet(procEfet);
					internacaoFacade.inserirProcEfet(procEfetDestino, true);
				}
				cnta.setIntdCodPrnt(prontuario);

				faturamentoFacade.persistirCntaConv(cnta);
			}
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0071);
		}
	}

	/**
	 * Implementa a procedure ffc_altera_PRONTUARIO_conta.
	 * 
	 * @param conta
	 * @param prontuarioDestino
	 */
	protected void ffcAlteraProntuarioConta(CntaConv conta, Integer prontuarioDestino) throws ApplicationBusinessException {
		try {
			IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
			IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
			PacIntdConvDAO pacIntdConvDAO = this.getPacIntdConvDAO();
			
			Set<ProcEfet> procs = new HashSet<ProcEfet>(internacaoFacade.listarProcEfetPorCtacvNro(conta.getNro()));
			for (ProcEfet procEfet : procs) {
				ProcEfet procEfetDestino = new ProcEfet();
				copiarPropriedades(procEfetDestino, procEfet);
				procEfetDestino.getId().setIntdCodPrnt(prontuarioDestino);
				internacaoFacade.removerProcEfet(procEfet, true);
				internacaoFacade.desatacharProcEfet(procEfet);
				internacaoFacade.inserirProcEfet(procEfetDestino, true);
			}

			if (conta.getPacIntdConv() != null) {
				PacIntdConv pac = pacIntdConvDAO.obterPorChavePrimaria(conta.getPacIntdConv().getSeq());
				pac.setCodPrnt(prontuarioDestino);
				pac.setNomePac(conta.getPacIntdConv().getNomePac());

				pacIntdConvDAO.atualizar(pac);
				pacIntdConvDAO.flush();
			}

			conta.setIntdCodPrnt(prontuarioDestino);

			faturamentoFacade.atualizarCntaConv(conta, true);

			verCntaDif(conta.getIntdCodPrnt(), conta.getIntdDataInt());
			this.flush();
			// dbms_output.put_line('Conta '||conta||'
			// '||to_char(xdata_int,'dd/mm')||' alterada, pront='||pRONT);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0070);
		}
	}

	/**
	 * @ORADB Procedure AELK_PPJ_RN.RN_PPJP_SUBST_PRONT.
	 * 
	 *        Implementa a procedure RN_PPJP_SUBST_PRONT da package AELK_PPJ_RN
	 * @param codigoOrigem
	 * @param codigoDestino
	 */
	@SuppressWarnings("ucd")
	protected void rnPpjpSubstPront(Integer codigoOrigem, Integer codigoDestino) {
		IExamesLaudosFacade examesLaudosFacade = this.getExamesLaudosFacade();
	
		List<AelProjetoPacientes> projetos = examesLaudosFacade.listarProjetosPaciente(codigoOrigem);

		for (AelProjetoPacientes projeto : projetos) {
			AelProjetoPacientesId id = new AelProjetoPacientesId(codigoDestino, projeto.getId().getPjqSeq());

			AelProjetoPacientes projetoInsert = new AelProjetoPacientes();
			projetoInsert.setId(id);
			projetoInsert.setCriadoEm(projeto.getCriadoEm());
			projetoInsert.setSerMatricula(projeto.getSerMatricula());
			projetoInsert.setSerVinCodigo(projeto.getSerVinCodigo());
			projetoInsert.setDtInicio(projeto.getDtInicio());
			projetoInsert.setDtFim(projeto.getDtFim());
			projetoInsert.setIndSituacao(projeto.getIndSituacao());
			projetoInsert.setNumero(projeto.getNumero());
			projetoInsert.setComplementoJustificativa(projeto.getComplementoJustificativa());
			// projetoInsert.setJexSeq(projeto.getJexSeq());

			examesLaudosFacade.persistirAelProjetoPacientes(projetoInsert);
			examesLaudosFacade.removerAelProjetoPacientes(projeto);
		}
		this.flush();

		List<AelDataRespostaProtocolos> protocolos = examesLaudosFacade.listarDatasRespostasProtocolosPorPpjPacCodigo(codigoOrigem);
		
		for (AelDataRespostaProtocolos protocolo : protocolos) {
			AelDataRespostaProtocolosId id = new AelDataRespostaProtocolosId();
			id.setPpjPacCodigo(codigoDestino);
			id.setPpjPjqSeq(protocolo.getId().getPpjPjqSeq());
			id.setPpoPjqSeq(protocolo.getId().getPpoPjqSeq());
			id.setPpoSeqp(protocolo.getId().getPpoSeqp());
			id.setSeqp(protocolo.getId().getSeqp());

			AelDataRespostaProtocolos protocoloInsert = new AelDataRespostaProtocolos();
			protocoloInsert.setId(id);
			protocoloInsert.setDthrResposta(protocolo.getDthrResposta());
			protocoloInsert.setSerMatricula(protocolo.getSerMatricula());
			protocoloInsert.setSerVinCodigo(protocolo.getSerVinCodigo());
			protocoloInsert.setIndSituacao(protocolo.getIndSituacao());
			protocoloInsert.setJustificativa(protocolo.getJustificativa());

			examesLaudosFacade.inserirAelDataRespostaProtocolos(protocoloInsert, true); // raise_application_error
															// (-20001,'Erro ao
															// inserir
															// AEL_DATA_RESPOSTA_PROTOCOLOS
															// subst
															// pront'||SQLERRM);
			examesLaudosFacade.removerAelDataRespostaProtocolos(protocolo); // raise_application_error
													// (-20002,
													// 'AEL-02580#1'||SQLERRM);
		}
		this.flush();

		List<AelRespostaQuesitos> quesitos = examesLaudosFacade.listarRespostasQuisitorPorDroPpjPacCodigo(codigoOrigem);
		
		for (AelRespostaQuesitos quesito : quesitos) {
			AelRespostaQuesitosId id = new AelRespostaQuesitosId();
			id.setDroPpjPacCodigo(codigoDestino);
			id.setDroPpjPjqSeq(quesito.getId().getDroPpjPjqSeq());
			id.setDroPpoPjqSeq(quesito.getId().getDroPpoPjqSeq());
			id.setDroPpoSeqp(quesito.getId().getDroPpoSeqp());
			id.setDroSeqp(quesito.getId().getDroSeqp());
			id.setPpqPpoPjqSeq(quesito.getId().getPpqPpoPjqSeq());
			id.setPpqPpoSeqp(quesito.getId().getPpqPpoSeqp());
			id.setPpqSeqp(quesito.getId().getPpqSeqp());

			AelRespostaQuesitos quesitoInsert = new AelRespostaQuesitos();
			quesitoInsert.setId(id);
			quesitoInsert.setResposta(quesito.getResposta());
			quesitoInsert.setAelDataRespostaProtocolos(quesito.getAelDataRespostaProtocolos());

			examesLaudosFacade.inserirAelRespostaQuesitos(quesitoInsert, true); // raise_application_error
														// (-20001,'Erro ao
														// inserir
														// AEL_DATA_RESPOSTA_QUESITOS
														// subst
														// pront'||SQLERRM);
			examesLaudosFacade.removerAelRespostaQuesitos(quesito); // raise_application_error
												// (-20001,
												// 'AEL-02580#1'||SQLERRM);
		}
		this.flush();

		List<AelAtendimentoDiversos> atendimentos = examesLaudosFacade.pesquisarAtendimentosDiversorPorPacCodigo(codigoOrigem);
		
		AipPacientes pac = getPacienteFacade().obterPacientePorCodigo(codigoDestino); 
		
		for (AelAtendimentoDiversos atendimento : atendimentos) {
			atendimento.setAipPaciente(pac);

			examesLaudosFacade.atualizarAelAtendimentoDiversos(atendimento, false);
		}
		this.flush();

		// aelk_ppj_rn.rn_ppjp_autoriza_del('N');
	}

	/*
	 * ##########################################################################
	 * ########## Início da conversão da procedure AIPP_SUBS_PAC_GESTA e seus
	 * cursores.
	 * 
	 * @author mtocchetto
	 * #######################################################
	 * ############################
	 */

	/**
	 * ORADB Procedure AIPP_SUBS_PAC_GESTA.
	 */

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity", "ucd"})
	public void procedureAippSubsPacGesta(Integer pPacCodigoDe, Integer pPacCodigoPara, String nomeMicrocomputador, RapServidores servidorLogado) throws ApplicationBusinessException {
		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
		
		// busca o maior seqp no prontuario mais antigo
		Short vSeqp = obterMaxSeqMcoGestacoes(pPacCodigoPara);

		// faz o loop buscando todas as gestações do prontuario que se quer
		// unificar
		List<McoGestacoes> listaCorigem = pesquisarMcoGestacoes(pPacCodigoDe);
		for (McoGestacoes rOrigem : listaCorigem) {
			// verifica se o paciente com o prontuario mais antigo que se quer
			// unificar,
			// já possui dados sobre esta mesma gestação. Se possuir deve
			// unificar nesta gestação já informada
			Byte cGesta = rOrigem.getGesta();
			Short vSeqpAchou = obterMaxSeqMcoGestacoesComGravidezConfirmada(pPacCodigoPara, cGesta);
			if (vSeqpAchou != null) {
				vSeqp = vSeqpAchou;

				// Verifica se já existe a gestação que se quer gravar no
				// prontuário mais antigo
				List<McoGestacoes> listaGestacoes = perinatologiaFacade.listarGestacoesPorCodigoPacienteEGestacao(pPacCodigoPara, cGesta);
				
				for (McoGestacoes mcoGestacoesUpdate : listaGestacoes) {
					try {
						mcoGestacoesUpdate.setPara(rOrigem.getPara());
						mcoGestacoesUpdate.setCesarea(rOrigem.getCesarea());
						mcoGestacoesUpdate.setAborto(rOrigem.getAborto());
						mcoGestacoesUpdate.setDtUltMenstruacao(rOrigem.getDtUltMenstruacao());
						mcoGestacoesUpdate.setDtPrimEco(rOrigem.getDtPrimEco());
						mcoGestacoesUpdate.setIgPrimEco(rOrigem.getIgPrimEco());
						mcoGestacoesUpdate.setDtInformadaIg(rOrigem.getDtInformadaIg());
						mcoGestacoesUpdate.setIgAtualSemanas(rOrigem.getIgAtualSemanas());
						mcoGestacoesUpdate.setIgAtualDias(rOrigem.getIgAtualDias());
						mcoGestacoesUpdate.setDtProvavelParto(rOrigem.getDtProvavelParto());
						mcoGestacoesUpdate.setNumConsPrn(rOrigem.getNumConsPrn());
						mcoGestacoesUpdate.setDtPrimConsPrn(rOrigem.getDtPrimConsPrn());
						mcoGestacoesUpdate.setIndMesmoPai(rOrigem.getIndMesmoPai());
						mcoGestacoesUpdate.setGrupoSanguineoPai(rOrigem.getGrupoSanguineoPai());
						mcoGestacoesUpdate.setFatorRhPai(rOrigem.getFatorRhPai());
						mcoGestacoesUpdate.setObsExames(rOrigem.getObsExames());
						mcoGestacoesUpdate.setGemelar(rOrigem.getGemelar());
						mcoGestacoesUpdate.setVatCompleta(rOrigem.getVatCompleta());
						mcoGestacoesUpdate.setUsoMedicamentos(rOrigem.getUsoMedicamentos());
						mcoGestacoesUpdate.setCriadoEm(rOrigem.getCriadoEm());
						mcoGestacoesUpdate.setSerMatricula(rOrigem.getSerMatricula());
						mcoGestacoesUpdate.setSerVinCodigo(rOrigem.getSerVinCodigo());
						mcoGestacoesUpdate.setIgPrimEcoDias(rOrigem.getIgPrimEcoDias());
						mcoGestacoesUpdate.setDthrSumarioAltaMae(rOrigem.getDthrSumarioAltaMae());
						mcoGestacoesUpdate.setGravidez(rOrigem.getGravidez());
						mcoGestacoesUpdate.setEctopica(rOrigem.getEctopica());

						perinatologiaFacade.atualizarMcoGestacoes(mcoGestacoesUpdate, true);
					} catch (Exception e) {
						throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0001, e);
					}
				}
				listaGestacoes.clear();
			} else {
				vSeqp++;
				persistirMcoGestacoes(pPacCodigoPara, vSeqp, rOrigem);
			}

			persistirMcoAnamneseEfs(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, nomeMicrocomputador, servidorLogado);
			persistirMcoPlanoIniciais(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoLogImpressoes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoBolsaRotas(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, servidorLogado != null ? servidorLogado.getUsuario() : null);
			persistirMcoResultadoExameSignifs(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoIntercorrenciaGestacoes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoMedicamentoTrabPartos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoAtendTrabPartos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoTrabPartos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoNascimentos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, servidorLogado != null ? servidorLogado.getUsuario() : null);
			persistirMcoIntercorrenciaNascs(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, servidorLogado != null ? servidorLogado.getUsuario() : null);
			persistirMcoProfNascs(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoForcipes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoCesarianas(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			atualizarMcoNascIndicacoes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			atualizarMcoRecemNascidos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, servidorLogado != null ? servidorLogado.getUsuario() : null);
			persistirMcoApgars(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoExameFisicoRns(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoAchadoExameFisicos(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			persistirMcoReanimacaoRns(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			atualizarAipPacientes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem, nomeMicrocomputador, servidorLogado);
			atualizarAghAtendimentosPorMcoGestacoes(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
			atualizarMbcFichaAnestesias(pPacCodigoDe, pPacCodigoPara, vSeqp, rOrigem);
		}
		listaCorigem.clear();

		/*
		 * ----------------------------------------------------------------------
		 * --------------------------------- --atualizações FORA DO LOOP DO
		 * GESTAÇÕES
		 * ------------------------------------------------------------
		 * -------------------------------------------
		 */

		atualizarMcoApgars(pPacCodigoDe, pPacCodigoPara);
		atualizarMcoRecemNascidos(pPacCodigoDe, pPacCodigoPara);
		atualizarMcoPlacar(pPacCodigoDe, pPacCodigoPara);
		persistirMcoSnappes(pPacCodigoDe, pPacCodigoPara, servidorLogado != null ? servidorLogado.getUsuario() : null);
		persistirMcoIddGestBallards(pPacCodigoDe, pPacCodigoPara, servidorLogado != null ? servidorLogado.getUsuario() : null);
		persistirMcoIddGestCapurros(pPacCodigoDe, pPacCodigoPara, servidorLogado != null ? servidorLogado.getUsuario() : null);
		persistirAipAlturaPacientes(pPacCodigoDe, pPacCodigoPara);
		persistirAipRegSanguineos(pPacCodigoDe, pPacCodigoPara);
		persistirAipPesoPacientes(pPacCodigoDe, pPacCodigoPara);
		persistirAipHistoriaFamiliares(pPacCodigoDe, pPacCodigoPara);
		persistirMcoGestacaoPacientes(pPacCodigoDe, pPacCodigoPara);

		delecoesAippSubsPacGesta(pPacCodigoDe, servidorLogado != null ? servidorLogado.getUsuario() : null);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	protected void delecoesAippSubsPacGesta(Integer pPacCodigoDe, String usuarioLogado) throws ApplicationBusinessException {
		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
	//	AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		
		/*----------------------------------------------------------------------------------------------------------------
		DELEÇÕES
		------------------------------------------------------------------------------------------------------------------*/
		this.getMcokIdcRN().setvVeioSubsGestaIdc(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_IDC_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);
		try {
			List<McoIddGestCapurros> listRemove = perinatologiaFacade.listarIddGestCapurrosPorCodigoPaciente(pPacCodigoDe);
			for (McoIddGestCapurros obj : listRemove) {
				perinatologiaFacade.removerMcoIddGestCapurros(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0005, e);
		} finally {
			this.getMcokIdcRN().setvVeioSubsGestaIdc(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_IDC_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
		
		this.getMcokSnaRN().setvVeioSubsGesta(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_SNA_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);
		try {
			List<McoSnappes> listRemove = perinatologiaFacade.listarSnappesPorCodigoPaciente(pPacCodigoDe);
			for (McoSnappes obj : listRemove) {
				perinatologiaFacade.removerMcoSnappes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0006, e);
		} finally {
			this.getMcokSnaRN().setvVeioSubsGesta(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_SNA_RN, ValorFlagsSubstituiProntuario.N,  usuarioLogado);
		}
		
		this.getMcokBalRN().setvVeioSubsGestaBal(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BAL_RN, ValorFlagsSubstituiProntuario.S,  usuarioLogado);
		try {
			List<McoIddGestBallards> listRemove = perinatologiaFacade.listarIddGestBallardsPorCodigoPaciente(pPacCodigoDe);
			for (McoIddGestBallards obj : listRemove) {
				perinatologiaFacade.removerMcoIddGestCapurros(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0007, e);
		} finally {
			this.getMcokBalRN().setvVeioSubsGestaBal(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BAL_RN, ValorFlagsSubstituiProntuario.N,  usuarioLogado);
		}
		
		try {
			List<McoLogImpressoes> listRemove = perinatologiaFacade.listarLogImpressoesPorCodigoPaciente(pPacCodigoDe);
			for (McoLogImpressoes obj : listRemove) {
				perinatologiaFacade.removerMcoLogImpressoes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0008, e);
		}
		
		try {
			AipHistoriaFamiliaresDAO aipHistoriaFamiliaresDAO = this.getAipHistoriaFamiliaresDAO();

			List<AipHistoriaFamiliares> listRemove = aipHistoriaFamiliaresDAO.pesquisarAipHistoriaFamiliares(pPacCodigoDe);
			for (AipHistoriaFamiliares obj : listRemove) {
				aipHistoriaFamiliaresDAO.remover(obj);
			}
			aipHistoriaFamiliaresDAO.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0009, e);
		}
		
		try {
			List<McoPlanoIniciais> listRemove = perinatologiaFacade.listarPlanosIniciaisPorCodigoPaciente(pPacCodigoDe);
			for (McoPlanoIniciais obj : listRemove) {
				perinatologiaFacade.removerMcoPlanoIniciais(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0010, e);
		}
		
		/*
		 * Chamadas ao entityManager.clear() adicionadas para eliminar o erro:
		 * 
		 * Caused by: java.lang.IllegalStateException:
		 * org.hibernate.TransientObjectException: object references an unsaved
		 * transient instance - save the transient instance before flushing:
		 * br.gov.mec.aghu.model.McoPlanoIniciais.mcoAnamneseEfs ->
		 * br.gov.mec.aghu.model.McoAnamneseEfs
		 */
		this.clear();

		// removerObjetos(clazz, removeCriterion, code);
		removerMcoAnamneseEfs(pPacCodigoDe);

		this.clear();

		try {
			List<McoGestacaoPacientes> listRemove = perinatologiaFacade.listarGestacoesPacientePorCodigoPaciente(pPacCodigoDe);
			for (McoGestacaoPacientes obj : listRemove) {
				perinatologiaFacade.removerMcoGestacaoPacientes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0012, e);
		}
		
		try {
			List<McoBolsaRotas> listRemove = perinatologiaFacade.listarBolsasRotasPorCodigoPaciente(pPacCodigoDe);
			for (McoBolsaRotas obj : listRemove) {
				perinatologiaFacade.removerMcoBolsaRotas(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0013, e);
		}
		
		try {
			AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();

			List<AipPesoPacientes> listRemove = aipPesoPacientesDAO.listarPesosPacientesPorCodigoPaciente(pPacCodigoDe);
			for (AipPesoPacientes obj : listRemove) {
				aipPesoPacientesDAO.remover(obj);
			}
			aipPesoPacientesDAO.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0014, e);
		}
		
		try {
			AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();

			List<AipAlturaPacientes> listRemove = aipAlturaPacientesDAO.listarAlturarPacientesPorCodigoPaciente(pPacCodigoDe);
			for (AipAlturaPacientes obj : listRemove) {
				aipAlturaPacientesDAO.remover(obj);
			}
			aipAlturaPacientesDAO.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0015, e);
		}

		try {
			AipRegSanguineosDAO aipRegSanguineosDAO = this.getAipRegSanguineosDAO();

			List<AipRegSanguineos> listRemove = aipRegSanguineosDAO.listarRegSanguineosPorCodigoPaciente(pPacCodigoDe);
			for (AipRegSanguineos obj : listRemove) {
				aipRegSanguineosDAO.remover(obj);
			}
			aipRegSanguineosDAO.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0016, e);
		}
		
		try {
			List<McoResultadoExameSignifs> listRemove = perinatologiaFacade.listarResultadosExamesSignifsPorCodigoPaciente(pPacCodigoDe);
			for (McoResultadoExameSignifs obj : listRemove) {
				perinatologiaFacade.removerMcoResultadoExameSignifs(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0017, e);
		}
		
		try {
			List<McoIntercorrenciaGestacoes> listRemove = perinatologiaFacade.listarIntercorrenciasGestacoesPorCodigoPaciente(pPacCodigoDe);
			for (McoIntercorrenciaGestacoes obj : listRemove) {
				perinatologiaFacade.removerMcoIntercorrenciaGestacoes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0018, e);
		}
		
		try {
			List<McoMedicamentoTrabPartos> listRemove = perinatologiaFacade.listarMedicamentosTrabPartosPorCodigoPaciente(pPacCodigoDe);
			for (McoMedicamentoTrabPartos obj : listRemove) {
				perinatologiaFacade.removerMcoMedicamentoTrabPartos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0019, e);
		}
		
		try {
			List<McoTrabPartos> listRemove = perinatologiaFacade.listarTrabPartosPorCodigoPaciente(pPacCodigoDe);
			for (McoTrabPartos obj : listRemove) {
				perinatologiaFacade.removerMcoTrabPartos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0020, e);
		}
		
		try {
			List<McoAtendTrabPartos> listRemove = perinatologiaFacade.listarAtendTrabPartosPorCodigoPaciente(pPacCodigoDe);
			for (McoAtendTrabPartos obj : listRemove) {
				perinatologiaFacade.removerMcoAtendTrabPartos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0021, e);
		}

		try {
			List<McoIntercorrenciaNascs> listRemove = perinatologiaFacade
					.listarIntercorrenciasNascsPorCodigoPaciente(pPacCodigoDe);
			for (McoIntercorrenciaNascs obj : listRemove) {
				perinatologiaFacade.removerMcoIntercorrenciaNascs(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0022, e);
		}
		
		try {
			List<McoProfNascs> listRemove = perinatologiaFacade.listarProfNascsPorCodigoPaciente(pPacCodigoDe);
			for (McoProfNascs obj : listRemove) {
				perinatologiaFacade.removerMcoProfNascs(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0023, e);
		}

		try {
			List<McoForcipes> listRemove = perinatologiaFacade.listarForcipesPorCodigoPaciente(pPacCodigoDe);
			for (McoForcipes obj : listRemove) {
				perinatologiaFacade.removerMcoForcipes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0024, e);
		}
		
		try {
			List<McoCesarianas> listRemove = perinatologiaFacade.listarCesarianasPorCodigoPaciente(pPacCodigoDe);
			for (McoCesarianas obj : listRemove) {
				perinatologiaFacade.removerMcoCesarianas(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0025, e);
		}

		try {
			List<McoNascimentos> listRemove = perinatologiaFacade.listarNascimentosPorCodigoPaciente(pPacCodigoDe);
			for (McoNascimentos obj : listRemove) {
				perinatologiaFacade.removerMcoNascimentos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0026, e);
		}
		
		try {
			List<McoReanimacaoRns> listRemove = perinatologiaFacade.listarReanimacoesRnsPorCodigoPaciente(pPacCodigoDe);
			for (McoReanimacaoRns obj : listRemove) {
				perinatologiaFacade.removerMcoReanimacaoRns(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0027, e);
		}
		
		try {
			List<McoAchadoExameFisicos> listRemove = perinatologiaFacade.listarAchadosExamesFisicosPorCodigoPaciente(pPacCodigoDe);
			for (McoAchadoExameFisicos obj : listRemove) {
				perinatologiaFacade.removerMcoAchadoExameFisicos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0028, e);
		}

		try {
			List<McoExameFisicoRns> listRemove = perinatologiaFacade.listarExamesFisicosRnsPorGestacoesCodigoPaciente(pPacCodigoDe);
			for (McoExameFisicoRns obj : listRemove) {
				perinatologiaFacade.removerMcoExameFisicoRns(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0029, e);
		}

		try {
			List<McoApgars> listRemove = perinatologiaFacade.listarApgarsPorRecemNascidoCodigoPaciente(pPacCodigoDe);
			for (McoApgars obj : listRemove) {
				perinatologiaFacade.removerMcoApgars(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0030, e);
		}

		try {
			List<McoRecemNascidos> listRemove = perinatologiaFacade.listarRecemNascidosPorGestacaoCodigoPaciente(pPacCodigoDe);
			for (McoRecemNascidos obj : listRemove) {
				perinatologiaFacade.removerMcoRecemNascidos(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0031, e);
		}

		try {
			List<McoGestacoes> listRemove = perinatologiaFacade.pesquisarMcoGestacoes(pPacCodigoDe);
			for (McoGestacoes obj : listRemove) {
				perinatologiaFacade.removerMcoGestacoes(obj, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0032, e);
		}
	}

	/**
	 * Método auxiliar para copiar as propriedades de um objeto a outro,
	 * removendo as coleções presentes no objeto de destino, evitando que ao
	 * persistir o objeto de destino seja lançada a exceção
	 * "org.hibernate.HibernateException: Found shared references to a collection"
	 * .
	 * 
	 * @param destino
	 * @param origem
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("rawtypes")
	public void copiarPropriedades(Object destino, Object origem) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Class clazz = destino.getClass();

		this.logDebug("Copiando propriedades de um objeto da classe " + clazz.getSimpleName());
		// Obs.: Não usar BeanUtils.copyProperties, pois aplica conversoes sobre
		// as propriedades copiadas.
		PropertyUtils.copyProperties(destino, origem);

		this.logDebug("Propriedades copiadas.");

		this.logDebug("Removendo referencias a colecoes...");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Class fieldClazz = field.getType();
			if (Collection.class.isAssignableFrom(fieldClazz)) {
				this.logDebug("Setando valor null para a colecao " + field.getName());
				field.setAccessible(true);
				field.set(destino, null);
			}
		}

		this.logDebug("Referencias a colecoes removidas...");
	}

	protected void persistirMcoGestacaoPacientes(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
		
		/*---------------------------
		INSERT MCO_GESTACAO_PACIENTES
		-----------------------------*/
		// -- Deixar fora do loop das gestações
		// -- Nesta tabela tem as gestações anteriores da paciente, a chave do
		// gestações é opcional (gso_pac_codigo + gso_seqp)
		try {
			List<McoGestacaoPacientes> listaMcoGestacaoPacientes = perinatologiaFacade.listarGestacoesPacientePorCodigoPaciente(pPacCodigoDe);
			for (McoGestacaoPacientes mcoGestacaoPacientes : listaMcoGestacaoPacientes) {
				McoGestacaoPacientes mcoGestacaoPacientesInsert = new McoGestacaoPacientes();
				copiarPropriedades(mcoGestacaoPacientesInsert, mcoGestacaoPacientes);

				McoGestacoes mcoGestacoes = mcoGestacaoPacientesInsert.getMcoGestacoes();
				if (mcoGestacoes != null) {
					McoGestacoesId mcoGestacoesId = new McoGestacoesId(pPacCodigoPara, mcoGestacoes.getId().getSeqp());
					mcoGestacoes =  perinatologiaFacade.obterMcoGestacoesPorChavePrimaria(mcoGestacoesId);
					mcoGestacaoPacientesInsert.setMcoGestacoes(mcoGestacoes);
				}

				McoGestacaoPacientesId id = new McoGestacaoPacientesId(pPacCodigoPara, mcoGestacaoPacientes.getId().getSeqp());
				mcoGestacaoPacientesInsert.setId(id);

				perinatologiaFacade.persistirMcoGestacaoPacientes(mcoGestacaoPacientesInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_GPA_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0065);
		}
	}

	protected List<AipHistoriaFamiliares> pesquisarAipHistoriaFamiliares(Integer pacCodigo) {
		return this.getAipHistoriaFamiliaresDAO().pesquisarAipHistoriaFamiliares(pacCodigo);
	}

	protected void persistirAipHistoriaFamiliares(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		AipHistoriaFamiliaresDAO aipHistoriaFamiliaresDAO = this.getAipHistoriaFamiliaresDAO();
		
		/*----------------------------
		INSERT AIP_HISTORIA_FAMILIARES
		------------------------------*/
		try {
			List<AipHistoriaFamiliares> listaAipHistoriaFamiliares = pesquisarAipHistoriaFamiliares(pPacCodigoDe);
			for (AipHistoriaFamiliares aipHistoriaFamiliares : listaAipHistoriaFamiliares) {
				AipHistoriaFamiliares aipHistoriaFamiliaresInsert = new AipHistoriaFamiliares();
				copiarPropriedades(aipHistoriaFamiliaresInsert, aipHistoriaFamiliares);

				aipHistoriaFamiliaresInsert.setPacCodigo(pPacCodigoPara);

				aipHistoriaFamiliaresDAO.persistir(aipHistoriaFamiliaresInsert);
			}
			aipHistoriaFamiliaresDAO.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AIP_HIF_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0064);
		}
	}

	protected List<AipPesoPacientes> pesquisarAipPesoPacientes(Integer pacCodigo) {
		return this.getAipPesoPacientesDAO().listarPesosPacientesPorCodigoPaciente(pacCodigo);
	}

	protected void persistirAipPesoPacientes(Integer pPacCodigoDe,
			Integer pPacCodigoPara) throws ApplicationBusinessException {
		ICadastroPacienteFacade cadastroPacienteFacade = this.getCadastroPacienteFacade();
		/*-----------------------
		INSERT AIP_PESO_PACIENTES
		-------------------------*/
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		try {
			List<AipPesoPacientes> listaAipPesoPacientes = pesquisarAipPesoPacientes(pPacCodigoDe);
			for (AipPesoPacientes aipPesoPacientes : listaAipPesoPacientes) {
				AipPesoPacientes aipPesoPacientesInsert = new AipPesoPacientes();
				copiarPropriedades(aipPesoPacientesInsert, aipPesoPacientes);

				AipPesoPacientesId id = new AipPesoPacientesId(pPacCodigoPara, aipPesoPacientes.getId().getCriadoEm());

				aipPesoPacientesInsert.setId(id);
				
				cadastroPacienteFacade.persistirPesoPaciente(aipPesoPacientesInsert,servidorLogado);
			}
			this.getAipPesoPacientesDAO().flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AIP_PEP_PK",
					ProntuarioRNExceptionCode.SUBS_PRONT_0063);
		}
	}

	protected void persistirAipRegSanguineos(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		/*-----------------------
		INSERT AIP_REG_SANGUINEOS
		-------------------------*/
		try {
			AipRegSanguineosDAO aipRegSanguineosDAO = this.getAipRegSanguineosDAO();
			
			Byte maxSeqp = aipRegSanguineosDAO.buscaMaxSeqp(pPacCodigoPara);
			maxSeqp = maxSeqp == null ? 0 : maxSeqp;

			List<AipRegSanguineos> listaAipRegSanguineos = aipRegSanguineosDAO.listarRegSanguineosPorCodigoPaciente(pPacCodigoDe);
			for (AipRegSanguineos aipRegSanguineos : listaAipRegSanguineos) {
				AipRegSanguineos aipRegSanguineosInsert = new AipRegSanguineos();
				copiarPropriedades(aipRegSanguineosInsert, aipRegSanguineos);

				AipRegSanguineosId id = new AipRegSanguineosId();
				id.setPacCodigo(pPacCodigoPara);
				id.setSeqp(++maxSeqp);
				id.setSerMatricula(aipRegSanguineos.getId().getSerMatricula());
				id.setSerVinCodigo(aipRegSanguineos.getId().getSerVinCodigo());

				aipRegSanguineosInsert.setId(id);

				aipRegSanguineosDAO.persistir(aipRegSanguineosInsert);
			}
			aipRegSanguineosDAO.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AIP_REG_SANGUINEOS_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0062);
		}
	}

	protected void persistirAipAlturaPacientes(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		/*-------------------------
		INSERT AIP_ALTURA_PACIENTES
		---------------------------*/
		try {
			AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();
			
			List<AipAlturaPacientes> listaAipAlturaPacientes =aipAlturaPacientesDAO.listarAlturarPacientesPorCodigoPaciente(pPacCodigoDe);
			for (AipAlturaPacientes aipAlturaPacientes : listaAipAlturaPacientes) {
				AipAlturaPacientes aipAlturaPacientesInsert = new AipAlturaPacientes();
				copiarPropriedades(aipAlturaPacientesInsert, aipAlturaPacientes);

				AipAlturaPacientesId id = new AipAlturaPacientesId(pPacCodigoPara, aipAlturaPacientes.getId().getCriadoEm());

				aipAlturaPacientesInsert.setId(id);

				aipAlturaPacientesDAO.persistir(aipAlturaPacientesInsert);
			}
			aipAlturaPacientesDAO.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AIP_ATP_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0061);
		}
	}

	protected void persistirMcoIddGestCapurros(Integer pPacCodigoDe, Integer pPacCodigoPara, String usuarioLogado) throws ApplicationBusinessException {
		/* SUBSTITUI O PAC CODIGO DO RECEM NASCIDO */
		this.getMcokIdcRN().setvVeioSubsGestaIdc(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_IDC_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoIddGestCapurros> listaMcoIddGestCapurros = perinatologiaFacade
					.listarIddGestCapurrosPorCodigoPaciente(pPacCodigoDe);
			for (McoIddGestCapurros mcoIddGestCapurros : listaMcoIddGestCapurros) {
				McoIddGestCapurros mcoIddGestCapurrosInsert = new McoIddGestCapurros();
				copiarPropriedades(mcoIddGestCapurrosInsert, mcoIddGestCapurros);

				McoIddGestCapurrosId id = new McoIddGestCapurrosId();
				id.setPacCodigo(pPacCodigoPara);
				id.setSerMatricula(mcoIddGestCapurros.getId().getSerMatricula());
				id.setSerVinCodigo(mcoIddGestCapurros.getId().getSerVinCodigo());

				mcoIddGestCapurrosInsert.setId(id);

				perinatologiaFacade.persistirMcoIddGestCapurros(mcoIddGestCapurrosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_IDC_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0060);
		} finally {
			this.getMcokIdcRN().setvVeioSubsGestaIdc(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_IDC_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void persistirMcoIddGestBallards(Integer pPacCodigoDe, Integer pPacCodigoPara, String usuarioLogado) throws ApplicationBusinessException {
		/* SUBSTITUI O PAC_CODIGO NA MCO_IDD_GEST_BALLARDS */
		this.getMcokBalRN().setvVeioSubsGestaBal(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BAL_RN, ValorFlagsSubstituiProntuario.S,usuarioLogado);

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoIddGestBallards> listaMcoIddGestBallards = perinatologiaFacade.listarIddGestBallardsPorCodigoPaciente(pPacCodigoDe);
			for (McoIddGestBallards mcoIddGestBallards : listaMcoIddGestBallards) {
				McoIddGestBallards mcoIddGestBallardsInsert = new McoIddGestBallards();
				copiarPropriedades(mcoIddGestBallardsInsert, mcoIddGestBallards);

				McoIddGestBallardsId id = new McoIddGestBallardsId();
				id.setPacCodigo(pPacCodigoPara);
				id.setSerMatricula(mcoIddGestBallards.getId().getSerMatricula());
				id.setSerVinCodigo(mcoIddGestBallards.getId().getSerVinCodigo());

				mcoIddGestBallardsInsert.setId(id);

				perinatologiaFacade.persistirMcoIddGestBallards(mcoIddGestBallardsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_BAL_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0059);
		} finally {
			this.getMcokBalRN().setvVeioSubsGestaBal(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BAL_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void persistirMcoSnappes(Integer pPacCodigoDe, Integer pPacCodigoPara, String usuarioLogado) throws ApplicationBusinessException {
		/* SUBSTITUI O PAC_CODIGO NA MCO_SNAPPES */
		this.getMcokSnaRN().setvVeioSubsGesta(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_SNA_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoSnappes> listaMcoSnappes = perinatologiaFacade.listarSnappesPorCodigoPaciente(pPacCodigoDe);
			for (McoSnappes mcoSnappes : listaMcoSnappes) {
				McoSnappes mcoSnappesInsert = new McoSnappes();
				copiarPropriedades(mcoSnappesInsert, mcoSnappes);

				McoSnappesId id = new McoSnappesId(pPacCodigoPara, mcoSnappes.getId().getSeqp());

				mcoSnappesInsert.setId(id);

				perinatologiaFacade.persistirMcoSnappes(mcoSnappesInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_SNA_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0058);
		} finally {
			this.getMcokSnaRN().setvVeioSubsGesta(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_SNA_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void atualizarMcoPlacar(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		/*---------------------------------
		UPDATE MCO_PLACAR
		-----------------------------------*/
		try {

			McoPlacar mcoPlacarUpdate = this.getPerinatologiaFacade()
					.buscarPlacar(pPacCodigoDe);
				mcoPlacarUpdate.setPacCodigo(pPacCodigoPara);
				this.getPerinatologiaFacade().persistirPlacar(mcoPlacarUpdate);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ProntuarioRNExceptionCode.SUBS_PRONT_0004, e);
		}
	}

	protected void atualizarMcoRecemNascidos(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		/* SUBSTITUI O PAC_CODIGO DO RECEM NASCIDO */
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			AipPacientes pacientePara =  getPacienteFacade().obterPaciente(pPacCodigoPara);

			List<McoRecemNascidos> listaMcoRecemNascidos = perinatologiaFacade.listarRecemNascidosPorCodigoPaciente(pPacCodigoDe);
			for (McoRecemNascidos mcoRecemNascidosUpdate : listaMcoRecemNascidos) {
				mcoRecemNascidosUpdate.setPaciente(pacientePara);
				
				perinatologiaFacade.atualizarMcoRecemNascidos(mcoRecemNascidosUpdate, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0003, e);
		}
	}

	protected void atualizarMcoApgars(Integer pPacCodigoDe, Integer pPacCodigoPara) throws ApplicationBusinessException {
		/*---------------------------------
		 UPDATE MCO_APGARS PELO FILHO
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoApgars> listaMcoApgars = perinatologiaFacade.listarApgarsPorCodigoPaciente(pPacCodigoDe);
			for (McoApgars mcoApgarsUpdate : listaMcoApgars) {
				mcoApgarsUpdate.setPacCodigo(pPacCodigoPara);
				
				perinatologiaFacade.atualizarMcoApgars(mcoApgarsUpdate, false);
			}
			this.flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0002, e);
		}
	}

	protected void atualizarMbcFichaAnestesias(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		// substituindo ficha de anestesia, caso exista, para pacientes
		// gestantes
		try {
			IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();

			List<MbcFichaAnestesias> listaMbcFichaAnestesias = blocoCirurgicoFacade.listarFichasAnestesias(pPacCodigoDe,
					pPacCodigoDe, rOrigem.getId().getSeqp());
			for (MbcFichaAnestesias mbcFichaAnestesias : listaMbcFichaAnestesias) {
				AipPacientes paciente = this.getAipPacientesDAO().obterPorChavePrimaria(pPacCodigoPara);
				mbcFichaAnestesias.setPaciente(paciente);
				mbcFichaAnestesias.setGsoPacCodigo(pPacCodigoPara);
				mbcFichaAnestesias.setGsoSeqp(vSeqp);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MBC_FIC_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0057);
		}
	}

	protected void atualizarAghAtendimentosPorMcoGestacoes(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp,
			McoGestacoes rOrigem) throws ApplicationBusinessException {
		/*---------------------------------
		UPDATE AGH_ATENDIMENTOS (ATD RECEM-NASCIDOS DA GESTANTE QUE SE ESTA TROCANDO O PAC_CODIGO)
		-----------------------------------*/
		try {
	//		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<AghAtendimentos> listaAghAtendimentos = this
					.getAghuFacade().listarAtendimentosPorMcoGestacoes(
							pPacCodigoDe, rOrigem.getId().getSeqp());
			if (listaAghAtendimentos != null && !listaAghAtendimentos.isEmpty()) {
				for (AghAtendimentos aghAtendimentosUpdate : listaAghAtendimentos) {
					aghAtendimentosUpdate.setGsoPacCodigo(pPacCodigoPara);
					aghAtendimentosUpdate.setGsoSeqp(vSeqp);
				}
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AGH_ATD_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0056);
		}
	}

	protected void atualizarAipPacientes(Integer pPacCodigoDe,
			Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String nomeMicrocomputador, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		/*---------------------------------
		UPDATE AIP_PACIENTES (RECEM-NASCIDOS DA GESTANTE QUE SE ESTA TROCANDO O PAC_CODIGO PARA CADA GESTAÇÃO)
		-----------------------------------*/
		try {
			ICadastroPacienteFacade cadastroPacienteFacade = this.getCadastroPacienteFacade();
			AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();

			List<AipPacientes> listaAipPacientes = aipPacientesDAO.listarPacientesPorDadosRecemNascido(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (AipPacientes aipPacientesUpdate : listaAipPacientes) {
				AipPacientes maePaciente = aipPacientesDAO.obterPorChavePrimaria(pPacCodigoPara);
				aipPacientesUpdate.setMaePaciente(maePaciente);
				//aipPacientesUpdate.getRecemNascidoPaciente(maePaciente);

				// UPDATE
				cadastroPacienteFacade.atualizarPaciente(aipPacientesUpdate, nomeMicrocomputador, servidorLogado, false);
			}
			aipPacientesDAO.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "AIP_PAC_PK",
					ProntuarioRNExceptionCode.SUBS_PRONT_0055);
		}
	}

	protected void persistirMcoReanimacaoRns(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_REANIMACAO_RNS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoReanimacaoRns> listaMcoReanimacaoRns = perinatologiaFacade.listarReanimacoesRns(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoReanimacaoRns mcoReanimacaoRns : listaMcoReanimacaoRns) {
				McoReanimacaoRns mcoReanimacaoRnsInsert = new McoReanimacaoRns();
				copiarPropriedades(mcoReanimacaoRnsInsert, mcoReanimacaoRns);

				McoReanimacaoRnsId id = new McoReanimacaoRnsId();
				id.setRnaGsoPacCodigo(pPacCodigoPara);
				id.setRnaGsoSeqp(vSeqp);
				id.setRnaSeqp(mcoReanimacaoRns.getId().getRnaSeqp());
				id.setPniSeq(mcoReanimacaoRns.getId().getPniSeq());

				mcoReanimacaoRnsInsert.setId(id);

				perinatologiaFacade.persistirMcoReanimacaoRns(mcoReanimacaoRnsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_RNR_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0054);
		}
	}

	protected void persistirMcoAchadoExameFisicos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT  MCO_ACHADO_EXAME_FISICOS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoAchadoExameFisicos> listaMcoAchadoExameFisicos = perinatologiaFacade.listarAchadosExamesFisicos(pPacCodigoDe,
					rOrigem.getId().getSeqp());
			for (McoAchadoExameFisicos mcoAchadoExameFisicos : listaMcoAchadoExameFisicos) {
				McoAchadoExameFisicos mcoExameFisicoRnsInsert = new McoAchadoExameFisicos();
				copiarPropriedades(mcoExameFisicoRnsInsert, mcoAchadoExameFisicos);

				McoAchadoExameFisicosId id = new McoAchadoExameFisicosId();
				id.setEfrRnaGsoPacCodigo(pPacCodigoPara);
				id.setEfrRnaGsoSeqp(vSeqp);
				id.setEfrRnaSeqp(mcoAchadoExameFisicos.getId().getEfrRnaSeqp());
				id.setAcdSeq(mcoAchadoExameFisicos.getId().getAcdSeq());

				mcoExameFisicoRnsInsert.setId(id);

				perinatologiaFacade.persistirMcoAchadoExameFisicos(mcoExameFisicoRnsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_AEF_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0053);
		}
	}

	protected void persistirMcoExameFisicoRns(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_EXAME_FISICO_RNS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoExameFisicoRns> listaMcoExameFisicoRns = perinatologiaFacade.listarExamesFisicosRns(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoExameFisicoRns mcoExameFisicoRns : listaMcoExameFisicoRns) {
				McoExameFisicoRns mcoExameFisicoRnsInsert = new McoExameFisicoRns();
				copiarPropriedades(mcoExameFisicoRnsInsert, mcoExameFisicoRns);

				McoRecemNascidosId id = new McoRecemNascidosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoExameFisicoRns.getId().getSeqp());

				mcoExameFisicoRnsInsert.setId(id);

				perinatologiaFacade.persistirMcoExameFisicoRns(mcoExameFisicoRnsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_EFR_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0052);
		}
	}

	protected void persistirMcoApgars(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_APGARS  PELA MAE
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoApgars> listaMcoApgars = perinatologiaFacade.listarApgarsPorRecemNascido(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoApgars mcoApgars : listaMcoApgars) {
				McoApgars mcoApgarsInsert = new McoApgars();
				copiarPropriedades(mcoApgarsInsert, mcoApgars);

				McoApgarsId id = new McoApgarsId();
				id.setRnaGsoPacCodigo(pPacCodigoPara);
				id.setRnaGsoSeqp(vSeqp);
				id.setRnaSeqp(mcoApgars.getId().getRnaSeqp());
				id.setSerMatricula(mcoApgars.getId().getSerMatricula());
				id.setSerVinCodigo(mcoApgars.getId().getSerVinCodigo());

				mcoApgarsInsert.setId(id);

				perinatologiaFacade.persistirMcoApgars(mcoApgarsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_APG_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0051);
		}
	}

	protected void atualizarMcoRecemNascidos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String usuarioLogado)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT  MCO_RECEM_NASCIDOS
		-----------------------------------*/
		this.getMcokRnaRN().setvVeioTrocaPac(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_RNA_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);
		
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoRecemNascidos> listaMcoRecemNascidos = perinatologiaFacade.listarPorGestacao(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoRecemNascidos mcoRecemNascidos : listaMcoRecemNascidos) {
				McoRecemNascidos mcoRecemNascidosInsert = new McoRecemNascidos();
				copiarPropriedades(mcoRecemNascidosInsert, mcoRecemNascidos);

				McoRecemNascidosId id = new McoRecemNascidosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoRecemNascidos.getId().getSeqp());

				mcoRecemNascidosInsert.setId(id);

				perinatologiaFacade.persistirMcoRecemNascidos(mcoRecemNascidosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_RNA_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0050);
		} finally {
			this.getMcokRnaRN().setvVeioTrocaPac(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_RNA_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void atualizarMcoNascIndicacoes(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		UPDATE MCO_NASC_INDICACOES (TEM PK= SEQ)
		-----------------------------------*/
		// --indicações para uso de fórcipes

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoNascIndicacoes> listaMcoNascIndicacoes = perinatologiaFacade.listarNascIndicacoesPorForcipes(pPacCodigoDe,
					rOrigem.getId().getSeqp());
			for (McoNascIndicacoes mcoNascIndicacoesUpdate : listaMcoNascIndicacoes) {
				McoForcipes mcoForcipes = mcoNascIndicacoesUpdate.getMcoForcipes();
				if (mcoForcipes != null) {
					final Integer nasSeqp = mcoForcipes.getId().getSeqp();
					McoNascimentosId mcoForcipesId = new McoNascimentosId();
					mcoForcipesId.setGsoPacCodigo(pPacCodigoPara);
					mcoForcipesId.setGsoSeqp(vSeqp);
					mcoForcipesId.setSeqp(nasSeqp);

					mcoForcipes = perinatologiaFacade.obterMcoForcipesPorChavePrimaria(mcoForcipesId);
					mcoNascIndicacoesUpdate.setMcoForcipes(mcoForcipes);
				}
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_NAI_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0048);
		}
		// --indicações para cesarianas
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoNascIndicacoes> listaMcoNascIndicacoes = perinatologiaFacade.listarNascIndicacoesPorCesariana(pPacCodigoDe,
					rOrigem.getId().getSeqp());
			for (McoNascIndicacoes mcoNascIndicacoesUpdate : listaMcoNascIndicacoes) {
				McoCesarianas mcoCesarianas = mcoNascIndicacoesUpdate.getMcoCesarianas();
				if (mcoCesarianas != null) {
					final Integer nasSeqp = mcoCesarianas.getId().getSeqp();
					McoNascimentosId mcoCesarianasId = new McoNascimentosId();
					mcoCesarianasId.setGsoPacCodigo(pPacCodigoPara);
					mcoCesarianasId.setGsoSeqp(vSeqp);
					mcoCesarianasId.setSeqp(nasSeqp);

					mcoCesarianas = perinatologiaFacade.obterMcoCesarianasPorChavePrimaria(mcoCesarianasId);
					mcoNascIndicacoesUpdate.setMcoCesarianas(mcoCesarianas);
					perinatologiaFacade.atualizarMcoCesarianas(mcoCesarianas, false);
				}
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_NAI_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0049);
		}
	}

	protected void persistirMcoCesarianas(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_CESARIANAS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoCesarianas> listaMcoCesarianas = perinatologiaFacade.listarCesarianas(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoCesarianas mcoCesarianas : listaMcoCesarianas) {
				McoCesarianas mcoCesarianasInsert = new McoCesarianas();
				copiarPropriedades(mcoCesarianasInsert, mcoCesarianas);

				McoNascimentosId id = new McoNascimentosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoCesarianas.getId().getSeqp());

				mcoCesarianasInsert.setId(id);

				perinatologiaFacade.persistirMcoCesarianas(mcoCesarianasInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_CSR_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0047);
		}
	}

	protected void persistirMcoForcipes(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_FORCIPES
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoForcipes> listaMcoForcipes = perinatologiaFacade.listarForcipes(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoForcipes mcoForcipes : listaMcoForcipes) {
				McoForcipes mcoForcipesInsert = new McoForcipes();
				copiarPropriedades(mcoForcipesInsert, mcoForcipes);

				McoNascimentosId id = new McoNascimentosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoForcipes.getId().getSeqp());

				mcoForcipesInsert.setId(id);

				perinatologiaFacade.persistirMcoForcipes(mcoForcipesInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_FCP_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0046);
		}
	}

	protected void persistirMcoProfNascs(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT  MCO_PROF_NASCS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoProfNascs> listaMcoProfNascs = perinatologiaFacade.listarProfNascs(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoProfNascs mcoProfNascs : listaMcoProfNascs) {
				McoProfNascs mcoProfNascsInsert = new McoProfNascs();
				copiarPropriedades(mcoProfNascsInsert, mcoProfNascs);

				McoProfNascsId id = new McoProfNascsId();
				id.setNasGsoPacCodigo(pPacCodigoPara);
				id.setNasGsoSeqp(vSeqp);
				id.setNasSeqp(mcoProfNascs.getId().getNasSeqp());
				id.setSerMatriculaNasc(mcoProfNascs.getId().getSerMatriculaNasc());
				id.setSerVinCodigoNasc(mcoProfNascs.getId().getSerVinCodigoNasc());

				mcoProfNascsInsert.setId(id);

				perinatologiaFacade.persistirMcoProfNascs(mcoProfNascsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_PNA_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0045);
		}
	}

	protected void persistirMcoIntercorrenciaNascs(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String usuarioLogado)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_INTERCORRENCIA_NASCS
		-----------------------------------*/
		// -- seta variável de package para não chamar regra de validação da
		// data qdo. vier dessa procedure
		this.getMcokInnRN().setvVeioTrocaPac(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_INN_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoIntercorrenciaNascs> listaMcoIntercorrenciaNascs = perinatologiaFacade.listarIntercorrenciasNascs(
					pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoIntercorrenciaNascs mcoIntercorrenciaNascs : listaMcoIntercorrenciaNascs) {
				McoIntercorrenciaNascs mcoIntercorrenciaNascsInsert = new McoIntercorrenciaNascs();
				copiarPropriedades(mcoIntercorrenciaNascsInsert, mcoIntercorrenciaNascs);

				McoIntercorrenciaNascsId id = new McoIntercorrenciaNascsId();
				id.setNasGsoPacCodigo(pPacCodigoPara);
				id.setNasGsoSeqp(vSeqp);
				id.setNasSeqp(mcoIntercorrenciaNascs.getId().getNasSeqp());
				id.setSeqp(mcoIntercorrenciaNascs.getId().getSeqp());

				mcoIntercorrenciaNascsInsert.setId(id);

				perinatologiaFacade.persistirMcoIntercorrenciaNascs(mcoIntercorrenciaNascsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_INN_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0044);
		} finally {
			this.getMcokInnRN().setvVeioTrocaPac(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_INN_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void persistirMcoNascimentos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String usuarioLogado)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT  MCO_NASCIMENTOS
		-----------------------------------*/
		this.getMcokNasRN().setvVeioTrocaPac(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_NAS_RN, ValorFlagsSubstituiProntuario.S, usuarioLogado);
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoNascimentos> listaMcoTrabPartos = perinatologiaFacade.listarNascimentos(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoNascimentos mcoNascimentos : listaMcoTrabPartos) {
				McoNascimentos mcoNascimentosInsert = new McoNascimentos();
				copiarPropriedades(mcoNascimentosInsert, mcoNascimentos);

				McoNascimentosId id = new McoNascimentosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoNascimentos.getId().getSeqp());

				mcoNascimentosInsert.setId(id);

				perinatologiaFacade.persistirMcoNascimentos(mcoNascimentosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_NAS_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0043);
		} finally {
			this.getMcokNasRN().setvVeioTrocaPac(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_NAS_RN, ValorFlagsSubstituiProntuario.N, usuarioLogado);
		}
	}

	protected void persistirMcoTrabPartos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_TRAB_PARTOS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoTrabPartos> listaMcoTrabPartos = perinatologiaFacade.listarTrabPartos(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoTrabPartos mcoTrabPartos : listaMcoTrabPartos) {
				McoTrabPartos mcoTrabPartosInsert = new McoTrabPartos();
				copiarPropriedades(mcoTrabPartosInsert, mcoTrabPartos);

				McoGestacoesId id = new McoGestacoesId();
				id.setPacCodigo(pPacCodigoPara);
				id.setSeqp(vSeqp);

				mcoTrabPartosInsert.setId(id);

				perinatologiaFacade.persistirMcoTrabPartos(mcoTrabPartosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_TPA_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0042);
		}
	}

	protected void persistirMcoAtendTrabPartos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_ATEND_TRAB_PARTOS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoAtendTrabPartos> listaMcoMedicamentoTrabPartos = perinatologiaFacade.listarAtendTrabPartos(pPacCodigoDe, rOrigem
					.getId().getSeqp(), null);
			for (McoAtendTrabPartos mcoAtendTrabPartos : listaMcoMedicamentoTrabPartos) {
				McoAtendTrabPartos mcoAtendTrabPartosInsert = new McoAtendTrabPartos();
				copiarPropriedades(mcoAtendTrabPartosInsert, mcoAtendTrabPartos);

				McoAtendTrabPartosId id = new McoAtendTrabPartosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoAtendTrabPartos.getId().getSeqp());

				mcoAtendTrabPartosInsert.setId(id);

				perinatologiaFacade.persistirMcoAtendTrabPartos(mcoAtendTrabPartosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_TBP_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0041);
		}
	}

	protected void persistirMcoMedicamentoTrabPartos(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_MEDICAMENTO_TRAB_PARTOS
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoMedicamentoTrabPartos> listaMcoMedicamentoTrabPartos = perinatologiaFacade.listarMedicamentosTrabPartos(
					pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoMedicamentoTrabPartos mcoMedicamentoTrabPartos : listaMcoMedicamentoTrabPartos) {
				McoMedicamentoTrabPartos mcoMedicamentoTrabPartosInsert = new McoMedicamentoTrabPartos();
				copiarPropriedades(mcoMedicamentoTrabPartosInsert, mcoMedicamentoTrabPartos);

				McoMedicamentoTrabPartosId id = new McoMedicamentoTrabPartosId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setMedMatCodigo(mcoMedicamentoTrabPartos.getId().getMedMatCodigo());

				mcoMedicamentoTrabPartosInsert.setId(id);

				perinatologiaFacade.persistirMcoMedicamentoTrabPartos(mcoMedicamentoTrabPartosInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_MTR_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0040);
		}
	}

	protected void persistirMcoIntercorrenciaGestacoes(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------------------
		INSERT MCO_INTERCORRENCIA_GESTACOES
		-----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoIntercorrenciaGestacoes> listaMcoIntercorrenciaGestacoes = perinatologiaFacade
					.listarIntercorrenciasGestacoes(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoes : listaMcoIntercorrenciaGestacoes) {
				McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoesInsert = new McoIntercorrenciaGestacoes();
				copiarPropriedades(mcoIntercorrenciaGestacoesInsert, mcoIntercorrenciaGestacoes);

				McoIntercorrenciaGestacoesId id = new McoIntercorrenciaGestacoesId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setOpaSeq(mcoIntercorrenciaGestacoes.getId().getOpaSeq());

				mcoIntercorrenciaGestacoesInsert.setId(id);

				perinatologiaFacade.persistirMcoIntercorrenciaGestacoes(mcoIntercorrenciaGestacoesInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_ING_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0039);
		}
	}

	protected void persistirMcoGestacoes(Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem) throws ApplicationBusinessException {
		/*------------------
		 INSERT MCO_GESTACOES
		--------------------*/
		try {
			McoGestacoes mcoGestacoesInsert = new McoGestacoes();
			copiarPropriedades(mcoGestacoesInsert, rOrigem);

			McoGestacoesId id = new McoGestacoesId(pPacCodigoPara, vSeqp);
			mcoGestacoesInsert.setId(id);

			this.getPerinatologiaFacade().inserirMcoGestacoes(mcoGestacoesInsert, true);
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_GSO_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0033);
		}
	}

	protected void persistirMcoResultadoExameSignifs(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*--------------------------------
		INSERT MCO_RESULTADO_EXAME_SIGNIFS
		----------------------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
			
			List<McoResultadoExameSignifs> listaMcoResultadoExameSignifs = perinatologiaFacade.listarResultadosExamesSignifs(
					pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoResultadoExameSignifs mcoResultadoExameSignifs : listaMcoResultadoExameSignifs) {
				McoResultadoExameSignifs mcoResultadoExameSignifsInsert = new McoResultadoExameSignifs();
				copiarPropriedades(mcoResultadoExameSignifsInsert, mcoResultadoExameSignifs);

				McoResultadoExameSignifsId id = new McoResultadoExameSignifsId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoResultadoExameSignifs.getId().getSeqp());

				mcoResultadoExameSignifsInsert.setId(id);

				perinatologiaFacade.persistirMcoResultadoExameSignifs(mcoResultadoExameSignifsInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_RXS_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0038);
		}
	}

	protected void persistirMcoBolsaRotas(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String usuarioLogado)
			throws ApplicationBusinessException {
		/*---------------------
		INSERT MCO_BOLSA_ROTAS
		-----------------------*/
		this.getMcokBsrRN().setvVerDtRomp(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BSR_RN, ValorFlagsSubstituiProntuario.TRUE, usuarioLogado);

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoBolsaRotas> listaMcoBolsaRotas = perinatologiaFacade.listarBolsasRotas(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoBolsaRotas mcoBolsaRotas : listaMcoBolsaRotas) {
				McoBolsaRotas mcoBolsaRotasInsert = new McoBolsaRotas();
				copiarPropriedades(mcoBolsaRotasInsert, mcoBolsaRotas);

				McoGestacoesId id = new McoGestacoesId(pPacCodigoPara, vSeqp);

				mcoBolsaRotasInsert.setId(id);

				perinatologiaFacade.persistirMcoBolsaRotas(mcoBolsaRotasInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_BSR_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0037);
		} finally {
			this.getMcokBsrRN().setvVerDtRomp(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_BSR_RN, ValorFlagsSubstituiProntuario.FALSE, usuarioLogado);
		}
	}

	protected void persistirMcoLogImpressoes(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*-----------------------
		INSERT MCO_LOG_IMPRESSOES
		-------------------------*/

		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoLogImpressoes> listaMcoLogImpressoes = perinatologiaFacade.listarLogImpressoes(pPacCodigoDe, rOrigem.getId()
					.getSeqp());
			for (McoLogImpressoes mcoLogImpressoes : listaMcoLogImpressoes) {
				McoLogImpressoes mcoLogImpressoesInsert = new McoLogImpressoes();
				copiarPropriedades(mcoLogImpressoesInsert, mcoLogImpressoes);

				McoLogImpressoesId id = new McoLogImpressoesId();
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				id.setSeqp(mcoLogImpressoes.getId().getSeqp());

				mcoLogImpressoesInsert.setId(id);

				perinatologiaFacade.persistirMcoLogImpressoes(mcoLogImpressoesInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_LOG_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0036);
		}
	}

	protected void persistirMcoPlanoIniciais(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem)
			throws ApplicationBusinessException {
		/*---------------------
		INSERT MCO_PLANO_INICIAIS
		-----------------------*/
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoPlanoIniciais> listaMcoPlanoIniciais = perinatologiaFacade.listarPlanosIniciais(pPacCodigoDe, rOrigem.getId()
					.getSeqp());
			for (McoPlanoIniciais mcoPlanoIniciais : listaMcoPlanoIniciais) {
				McoPlanoIniciais mcoPlanoIniciaisInsert = new McoPlanoIniciais();
				copiarPropriedades(mcoPlanoIniciaisInsert, mcoPlanoIniciais);

				McoPlanoIniciaisId id = new McoPlanoIniciaisId();
				id.setEfiConNumero(mcoPlanoIniciais.getId().getEfiConNumero());
				id.setEfiGsoSeqp(vSeqp);
				id.setEfiGsoPacCodigo(pPacCodigoPara);
				id.setCdtSeq(mcoPlanoIniciais.getId().getCdtSeq());

				mcoPlanoIniciaisInsert.setId(id);

				perinatologiaFacade.persistirMcoPlanoIniciais(mcoPlanoIniciaisInsert);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_PLI_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0035);
		}
	}

	protected void persistirMcoAnamneseEfs(Integer pPacCodigoDe, Integer pPacCodigoPara, Short vSeqp, McoGestacoes rOrigem, String nomeMicrocomputador, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		/*---------------------
		INSERT MCO_ANAMNESE_EFS
		-----------------------*/
		// seta variável de package para não chamar regra de validação da data
		// qdo. vier dessa procedure
		this.getMcokEfiRN().setvVeioTrocaPac(true);
		setarFlagProntuario(FlagsSubstituiProntuario.MCOK_EFI_RN, ValorFlagsSubstituiProntuario.S,servidorLogado != null ? servidorLogado.getUsuario() : null);
		try {
			IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();

			List<McoAnamneseEfs> listaMcoAnamneseEfs = perinatologiaFacade.listarAnamnesesEfs(pPacCodigoDe, rOrigem.getId().getSeqp());
			for (McoAnamneseEfs mcoAnamneseEfs : listaMcoAnamneseEfs) {
				McoAnamneseEfs mcoAnamneseEfsInsert = new McoAnamneseEfs();
				copiarPropriedades(mcoAnamneseEfsInsert, mcoAnamneseEfs);

				McoAnamneseEfsId id = new McoAnamneseEfsId();
				id.setConNumero(mcoAnamneseEfs.getId().getConNumero());
				id.setGsoPacCodigo(pPacCodigoPara);
				id.setGsoSeqp(vSeqp);
				mcoAnamneseEfsInsert.setId(id);

				persistirMcoAnamneseEfs(mcoAnamneseEfsInsert, nomeMicrocomputador);
			}
			this.flush();
		} catch (Exception e) {
			catchDupValOnIndex(e, "MCO_EFI_PK", ProntuarioRNExceptionCode.SUBS_PRONT_0034);
		} finally {
			this.getMcokEfiRN().setvVeioTrocaPac(false);
			setarFlagProntuario(FlagsSubstituiProntuario.MCOK_EFI_RN, ValorFlagsSubstituiProntuario.N, servidorLogado != null ? servidorLogado.getUsuario() : null);
		}
	}

	// FIXME Tocchetto Eliminar este método de tratamento baseado em nome da
	// constraint.
	// FIXME Tocchetto Os métodos que chamam catchDupValOnIndex devem ser
	// alterados para não permitir que ocorram
	// erros de PK, consultando os registros no BD para não tentar persistir
	// registros duplicados.
	protected void catchDupValOnIndex(Exception e, String constraintName, BusinessExceptionCode code) throws ApplicationBusinessException {
		boolean supressError = false;
		Throwable cause = ExceptionUtils.getCause(e);
		if (cause instanceof ConstraintViolationException) {
			if (StringUtils.containsIgnoreCase(((ConstraintViolationException) cause).getConstraintName(), constraintName)) {
				supressError = true;
			}
		}
		if (!supressError) {
			throw new ApplicationBusinessException(code, e);
		}
	}

	/**
	 * Conversão de trigger. ORADB: Trigger MCOT_EFI_BRI (INSERT, BEFORE EACH
	 * ROW), tabela MCO_ANAMNESE_EFS.
	 * 
	 * @author Marcelo Tocchetto
	 * @throws BaseException 
	 */
	protected void persistirMcoAnamneseEfs(McoAnamneseEfs novo, String nomeMicrocomputador) throws BaseException {
		Date dataAtual = new Date();

		/* Este If é devido a troca de prontuario */
		if (novo.getCriadoEm() == null) {
			novo.setCriadoEm(dataAtual);
		}

		if (novo.getSerMatricula() == null) {
			MatriculaVinculoVO matriculaVinculoVO = new MatriculaVinculoVO(novo.getSerMatricula(), novo.getSerVinCodigo());
			this.getMcokMcoRN().rnMcopAtuServidor(matriculaVinculoVO);
			novo.setSerMatricula(matriculaVinculoVO.getMatricula());
			novo.setSerVinCodigo(matriculaVinculoVO.getViniculo());
		}

		// Removido teste do AGH por
		// Arrays.asList("LALLEGRETTI","AGH").contains(user)
		if (novo.getDthrConsulta() != null) {
			String sDthrConsulta = DateFormatUtils.format(novo.getDthrConsulta(), "dd/MM/yyyy");
			this.getMcokEfiRN().rnEfipVerDtCons(sDthrConsulta);
		}

		this.getMcokEfiRN().rnEfipVerDilat(novo.getDilatacao());

		// Atualiza AGH_ATENDIMENTO
		McoAnamneseEfsId id = novo.getId();
		this.getMcokEfiRN().rnEfipAtuAtend(id.getConNumero(), id.getGsoPacCodigo(), id.getGsoSeqp(), nomeMicrocomputador);

		this.getPerinatologiaFacade().persistirMcoAnamneseEfs(novo);
	}

	/**
	 * Conversão de triggers da tabela MCO_ANAMNESE_EFS.
	 * 
	 * ORADB: Trigger MCOT_EFI_BRU (UPDATE, BEFORE EACH ROW) ORADB: Trigger
	 * MCOT_EFI_ARU (UPDATE, AFTER EACH ROW)
	 * 
	 * @throws ApplicationBusinessException
	 * @author Marcelo Tocchetto
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	protected void atualizarMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs) throws ApplicationBusinessException {
		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
		
		perinatologiaFacade.desatacharMcoAnamneseEfs(mcoAnamneseEfs);
		McoAnamneseEfs antigo = perinatologiaFacade.obterMcoAnamneseEfsPorChavePrimaria(mcoAnamneseEfs.getId());

		/*
		 * MCOT_EFI_BRU
		 */
		// Removida conversão do AGH que realiza o teste --> &&
		// !"AGH".equals(user)
		if (mcoAnamneseEfs.getDthrConsulta() != null && !mcoAnamneseEfs.getDthrConsulta().equals(antigo.getDthrConsulta())) {
			String sDthrConsulta = DateFormatUtils.format(mcoAnamneseEfs.getDthrConsulta(), "dd/MM/yyyy");
			this.getMcokEfiRN().rnEfipVerDtCons(sDthrConsulta);
		}

		/*
		 * Este If permite que seja alterado o empr/servidor BUG da troca de
		 * prontuarios
		 */
		if (mcoAnamneseEfs.getSerMatricula().equals(antigo.getSerMatricula())
				&& mcoAnamneseEfs.getSerVinCodigo().equals(antigo.getSerVinCodigo())) {
			MatriculaVinculoVO matriculaVinculoVO = new MatriculaVinculoVO(mcoAnamneseEfs.getSerMatricula(),
					mcoAnamneseEfs.getSerVinCodigo());
			this.getMcokMcoRN().rnMcopAtuServidor(matriculaVinculoVO);
			mcoAnamneseEfs.setSerMatricula(matriculaVinculoVO.getMatricula());
			mcoAnamneseEfs.setSerVinCodigo(matriculaVinculoVO.getViniculo());
		}

		this.getMcokEfiRN().rnEfipVerDilat(mcoAnamneseEfs.getDilatacao());

		/**
		 * MCOT_EFI_ARU
		 */
		observarPersistenciaMcoAnamneseEfs(mcoAnamneseEfs, antigo, DominioOperacoesJournal.UPD);
	}

	/**
	 * Método para exclusão das McoAnamneseEfs de um paciente, utilizando o
	 * método que implementa conversão da trigger de exclusão (cria o journal).
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @throws ApplicationBusinessException
	 */
	protected void removerMcoAnamneseEfs(Integer pacCodigo) throws ApplicationBusinessException {
		try {
			List<McoAnamneseEfs> lista = this.getPerinatologiaFacade().listarAnamnesesEfsPorPacCodigo(pacCodigo);
			for (McoAnamneseEfs mcoAnamneseEfs : lista) {
				removerMcoAnamneseEfs(mcoAnamneseEfs);
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(ProntuarioRNExceptionCode.SUBS_PRONT_0011, e);
		}
	}

	/**
	 * Conversão de trigger da tabela MCO_ANAMNESE_EFS.
	 * 
	 * ORADB: Trigger MCOT_EFI_ARD (DELETE, AFTER EACH ROW).
	 * 
	 * @throws ApplicationBusinessException
	 * @author Marcelo Tocchetto
	 */
	protected void removerMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs) {
		observarPersistenciaMcoAnamneseEfs(mcoAnamneseEfs, null, DominioOperacoesJournal.DEL);
		this.getPerinatologiaFacade().removerMcoAnamneseEfs(mcoAnamneseEfs);
	}

	protected void observarPersistenciaMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs, McoAnamneseEfs antigo,
			DominioOperacoesJournal operacaoJournal) {
		if (DominioOperacoesJournal.UPD.equals(operacaoJournal)) {
			if (validarGeracaoJournalAnamneseEfsAtualizacao(mcoAnamneseEfs, antigo)) {
				gerarJournalMcoAnamneseEfs(mcoAnamneseEfs, operacaoJournal);
			}
		} else if (DominioOperacoesJournal.DEL.equals(operacaoJournal)) {
			gerarJournalMcoAnamneseEfs(mcoAnamneseEfs, operacaoJournal);
		}
	}

	protected boolean validarGeracaoJournalAnamneseEfsAtualizacao(McoAnamneseEfs novo, McoAnamneseEfs antigo) {
		boolean gerarJournal = false;
		gerarJournal = modificados(antigo.getDthrConsulta(), novo.getDthrConsulta())
				|| modificados(antigo.getMotivo(), novo.getMotivo())
				|| modificados(antigo.getPressaoArtSistolica(), novo.getPressaoArtSistolica())
				|| modificados(antigo.getPressaoArtDiastolica(), novo.getPressaoArtDiastolica())
				|| modificados(antigo.getPressaoSistRepouso(), novo.getPressaoSistRepouso())
				|| modificados(antigo.getPressaoDiastRepouso(), novo.getPressaoDiastRepouso())
				|| modificados(antigo.getFreqCardiaca(), novo.getFreqCardiaca())
				|| modificados(antigo.getFreqRespiratoria(), novo.getFreqRespiratoria())
				|| modificados(antigo.getTemperatura(), novo.getTemperatura()) || modificados(antigo.getEdema(), novo.getEdema())
				|| modificados(antigo.getAlturaUterina(), novo.getAlturaUterina())
				|| modificados(antigo.getDinamicaUterina(), novo.getDinamicaUterina())
				|| modificados(antigo.getIntensidadeDinUterina(), novo.getIntensidadeDinUterina())
				|| modificados(antigo.getBatimentoCardiacoFetal(), novo.getBatimentoCardiacoFetal())
				|| modificados(antigo.getBatimentoCardiacoFetal2(), novo.getBatimentoCardiacoFetal2())
				|| modificados(antigo.getIndAcelTrans(), novo.getIndAcelTrans())
				|| modificados(antigo.getSitFetal(), novo.getSitFetal())
				|| modificados(antigo.getExameEspecular(), novo.getExameEspecular())
				|| modificados(antigo.getPosicaoCervice(), novo.getPosicaoCervice())
				|| modificados(antigo.getEspessuraCervice(), novo.getEspessuraCervice())
				|| modificados(antigo.getApagamento(), novo.getApagamento())
				|| modificados(antigo.getDilatacao(), novo.getDilatacao())
				|| modificados(antigo.getApresentacao(), novo.getApresentacao())
				|| modificados(antigo.getPlanoDelee(), novo.getPlanoDelee())
				|| modificados(antigo.getIndPromontorioAcessivel(), novo.getIndPromontorioAcessivel())
				|| modificados(antigo.getIndEspCiaticaSaliente(), novo.getIndEspCiaticaSaliente())
				|| modificados(antigo.getIndSubPubicoMenor90(), novo.getIndSubPubicoMenor90())
				|| modificados(antigo.getAcv(), novo.getAcv()) || modificados(antigo.getAr(), novo.getAr())
				|| modificados(antigo.getObservacao(), novo.getObservacao()) || modificados(antigo.getCriadoEm(), novo.getCriadoEm())
				|| modificados(antigo.getId().getConNumero(), novo.getId().getConNumero())
				|| modificados(antigo.getId().getGsoSeqp(), novo.getId().getGsoSeqp())
				|| modificados(antigo.getId().getGsoPacCodigo(), novo.getId().getGsoPacCodigo())
				|| modificados(antigo.getDigSeq(), novo.getDigSeq()) || modificados(antigo.getCidSeq(), novo.getCidSeq())
				|| modificados(antigo.getIndMovFetal(), novo.getIndMovFetal())
				|| modificados(antigo.getSerMatricula(), novo.getSerMatricula())
				|| modificados(antigo.getSerVinCodigo(), novo.getSerVinCodigo())
				|| modificados(antigo.getExFisicoGeral(), novo.getExFisicoGeral());

		return gerarJournal;
	}

	protected void gerarJournalMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs, DominioOperacoesJournal operacao) {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		McoAnamneseEfsJn jn = BaseJournalFactory.getBaseJournal(operacao, McoAnamneseEfsJn.class, servidorLogado.getUsuario());

		jn.setDthrConsulta(mcoAnamneseEfs.getDthrConsulta());
		jn.setMotivo(mcoAnamneseEfs.getMotivo());
		jn.setPressaoArtSistolica(mcoAnamneseEfs.getPressaoArtSistolica());
		jn.setPressaoArtDiastolica(mcoAnamneseEfs.getPressaoArtDiastolica());
		jn.setPressaoSistRepouso(mcoAnamneseEfs.getPressaoSistRepouso());
		jn.setPressaoDiastRepouso(mcoAnamneseEfs.getPressaoDiastRepouso());
		jn.setFreqCardiaca(mcoAnamneseEfs.getFreqCardiaca());
		jn.setFreqRespiratoria(mcoAnamneseEfs.getFreqRespiratoria());
		jn.setTemperatura(mcoAnamneseEfs.getTemperatura());
		jn.setEdema(mcoAnamneseEfs.getEdema());
		jn.setAlturaUterina(mcoAnamneseEfs.getAlturaUterina());
		jn.setDinamicaUterina(mcoAnamneseEfs.getDinamicaUterina());
		jn.setIntensidadeDinUterina(mcoAnamneseEfs.getIntensidadeDinUterina());
		jn.setBatimentoCardiacoFetal(mcoAnamneseEfs.getBatimentoCardiacoFetal());
		jn.setBatimentoCardiacoFetal2(mcoAnamneseEfs.getBatimentoCardiacoFetal2());
		jn.setIndAcelTrans(mcoAnamneseEfs.getIndAcelTrans());
		jn.setSitFetal(mcoAnamneseEfs.getSitFetal());
		jn.setExameEspecular(mcoAnamneseEfs.getExameEspecular());
		jn.setPosicaoCervice(mcoAnamneseEfs.getPosicaoCervice());
		jn.setEspessuraCervice(mcoAnamneseEfs.getEspessuraCervice());
		jn.setApagamento(mcoAnamneseEfs.getApagamento());
		jn.setDilatacao(mcoAnamneseEfs.getDilatacao());
		jn.setApresentacao(mcoAnamneseEfs.getApresentacao());
		jn.setPlanoDelee(mcoAnamneseEfs.getPlanoDelee());
		jn.setIndPromontorioAcessivel(mcoAnamneseEfs.getIndPromontorioAcessivel());
		jn.setIndEspCiaticaSaliente(mcoAnamneseEfs.getIndEspCiaticaSaliente());
		jn.setIndSubPubicoMenor90(mcoAnamneseEfs.getIndSubPubicoMenor90());
		jn.setAcv(mcoAnamneseEfs.getAcv());
		jn.setAr(mcoAnamneseEfs.getAr());
		jn.setObservacao(mcoAnamneseEfs.getObservacao());
		jn.setCriadoEm(mcoAnamneseEfs.getCriadoEm());
		jn.setConNumero(mcoAnamneseEfs.getId().getConNumero());
		jn.setGsoSeqp(mcoAnamneseEfs.getId().getGsoSeqp());
		jn.setGsoPacCodigo(mcoAnamneseEfs.getId().getGsoPacCodigo());
		jn.setDigSeq(mcoAnamneseEfs.getDigSeq());
		jn.setCidSeq(mcoAnamneseEfs.getCidSeq());
		jn.setIndMovFetal(mcoAnamneseEfs.getIndMovFetal());
		jn.setSerMatricula(mcoAnamneseEfs.getSerMatricula());
		jn.setSerVinCodigo(mcoAnamneseEfs.getSerVinCodigo());
		jn.setExFisicoGeral(mcoAnamneseEfs.getExFisicoGeral());

		this.getPerinatologiaFacade().inserirMcoAnamneseEfsJn(jn, true);
	}

	/**
	 * <h1>Conversão do cursor c_max_gesta</h1> Busca na tabela MCO_GESTACOES a
	 * sequence da última gestação registrada para o prontuario utilizando como
	 * filtro de pesquisa o código do paciente. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * SELECT
	 *     MAX(seqp)
	 * FROM
	 *      mco_gestacoes
	 * WHERE
	 *      pac_codigo  = c_pac_codigo_para;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Valor da sequence da última gestação registrada para o
	 *         prontuario, se não encontrar retorna 0.
	 * @author mtocchetto
	 */
	protected Short obterMaxSeqMcoGestacoes(Integer pacCodigo) {
		return this.getPerinatologiaFacade().obterMaxSeqMcoGestacoes(pacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_ver_gesta</h1> Busca na tabela MCO_GESTACOES a
	 * sequence da gestação registrada para o prontuario utilizando como filtro
	 * de pesquisa o código do paciente, o valor da coluna gesta e a coluna
	 * gravidez com o valor 'GCO' (confirmada). <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * SELECT
	 *      seqp
	 * FROM
	 *      mco_gestacoes
	 * WHERE
	 *      pac_codigo  = c_pac_codigo_para
	 * AND  gesta       = c_gesta
	 * AND  gravidez    = 'GCO'; --Gravidez Confirmada
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @param gesta
	 *            Valor da coluna gesta.
	 * @return Valor encontrado da sequence da gestação para o prontuario, se
	 *         não encontrar retorna null.
	 * @author mtocchetto
	 */
	protected Short obterMaxSeqMcoGestacoesComGravidezConfirmada(Integer pacCodigo, Byte gesta) {
		return this.getPerinatologiaFacade().obterMaxSeqMcoGestacoesComGravidezConfirmada(pacCodigo, gesta);
	}

	/**
	 * <h1>Conversão do cursor c_origem</h1> Realiza a busca de uma lista de
	 * registros na tabela MCO_GESTACOES utilizando como filtro de pesquisa o
	 * código do paciente. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * SELECT
	 * 		SEQP,GESTA, PARA,CESAREA,ABORTO,DT_ULT_MENSTRUACAO,DT_PRIM_ECO,
	 * 		IG_PRIM_ECO,DT_INFORMADA_IG,IG_ATUAL_SEMANAS,IG_ATUAL_DIAS,
	 * 		DT_PROVAVEL_PARTO,NUM_CONS_PRN,DT_PRIM_CONS_PRN,
	 * 		IND_MESMO_PAI,GRUPO_SANGUINEO_PAI,FATOR_RH_PAI,OBS_EXAMES,
	 * 		GEMELAR,VAT_COMPLETA, USO_MEDICAMENTOS,CRIADO_EM,
	 * 		P_PAC_CODIGO_PARA, SER_MATRICULA,SER_VIN_CODIGO,
	 * 		IG_PRIM_ECO_DIAS,DTHR_SUMARIO_ALTA_MAE,GRAVIDEZ,ECTOPICA
	 * FROM 	mco_gestacoes
	 * WHERE    PAC_CODIGO = C_PAC_CODIGO_DE;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 */
	protected List<McoGestacoes> pesquisarMcoGestacoes(Integer pacCodigo) {
		// Busca todas as gestações do prontuario que se quer unificar
		return this.getPerinatologiaFacade().pesquisarMcoGestacoes(pacCodigo);
	}

	/*
	 * ##########################################################################
	 * ########## Fim da conversão da procedure AIPP_SUBS_PAC_GESTA e seus
	 * cursores.
	 * ################################################################
	 * ###################
	 */



	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

	private IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected PacienteON getPacienteON() {
		return pacienteON;
	}
	
	protected AipPaSistPacientesDAO getAipPaSistPacientesDAO() {
		return aipPaSistPacientesDAO;
	}	

	protected AipPaDiastPacientesDAO getAipPaDiastPacientesDAO() {
		return aipPaDiastPacientesDAO;
	}

	protected AipPerimCefalicoPacientesDAO getAipPerimCefalicoPacientesDAO() {
		return aipPerimCefalicoPacientesDAO;
	}

	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}	

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}		

	protected IInternacaoFacade getInternacaoFacade() {
		return this
				.internacaoFacade;
	}

	protected PacIntdConvDAO getPacIntdConvDAO() {
		return pacIntdConvDAO;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected AipHistoriaFamiliaresDAO getAipHistoriaFamiliaresDAO() {
		return aipHistoriaFamiliaresDAO;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected AipRegSanguineosDAO getAipRegSanguineosDAO() {
		return aipRegSanguineosDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected McokBalRN getMcokBalRN() {
		return mcokBalRN;
	}

	protected McokBsrRN getMcokBsrRN() {
		return mcokBsrRN;
	}

	protected McokEfiRN getMcokEfiRN() {
		return mcokEfiRN;
	}

	protected McokIdcRN getMcokIdcRN() {
		return mcokIdcRN;
	}

	protected McokInnRN getMcokInnRN() {
		return mcokInnRN;
	}

	protected McokMcoRN getMcokMcoRN() {
		return mcokMcoRN;
	}

	protected McokNasRN getMcokNasRN() {
		return mcokNasRN;
	}

	protected McokRnaRN getMcokRnaRN() {
		return mcokRnaRN;
	}

	protected McokSnaRN getMcokSnaRN() {
		return mcokSnaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
