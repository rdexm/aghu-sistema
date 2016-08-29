package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.vo.AtendPacExternPorColetasRealizadasVO;
import br.gov.mec.aghu.faturamento.vo.AtendimentoCargaColetaVO;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.faturamento.vo.DoadorSangueTriagemClinicaVO;
import br.gov.mec.aghu.faturamento.vo.ProcedHospInternosTriagemClinicaVO;
import br.gov.mec.aghu.faturamento.vo.TriagemRealizadaEmergenciaVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * ORADB Package FATK_AMB_RN
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.AtributoEmSeamContextManager"})
@Stateless
public class FaturamentoFatkAmbRN extends AbstractFatDebugLogEnableRN {

	private static final String FATK_PMR_RN_V_PMR_JOURNAL = "FATK_PMR_RN_V_PMR_JOURNAL";

	private static final String AND = " AND ";

	private static final String FINAL_0_ = "final  {0}";

	private static final String INICIO_0_ = "inicio {0}";

	@EJB
	private FaturamentoFatkPmrRN faturamentoFatkPmrRN;
	
	@EJB
	private FaturamentoFatkCpeRN faturamentoFatkCpeRN;
	
	@EJB
	private FaturamentoAtualizaExamesPreTransplantesRN faturamentoAtualizaExamesPreTransplantesRN;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

	private static final Log LOG = LogFactory.getLog(FaturamentoFatkAmbRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private FatCompetenciaRN fatCompetenciaRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private FatEspelhoProcedAmbDAO fatEspelhoProcedAmbDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@Inject
	private FatArqEspelhoProcedAmbDAO fatArqEspelhoProcedAmbDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 404530798515208068L;

	enum FaturamentoFatkAmbRNExceptionCode implements BusinessExceptionCode {
		MSG_NAO_ENCONTROU_COMPETENCIA,
		MSG_NAO_ENCONTRADA_COMPETENCIA_ABERTA,
		;
	}	
	
	private FatProcedHospInternosDAO procedHospInternosDAO = getFatProcedHospInternosDAO();
	
	
	
	/**
	 * ORADB Procedure FATP_CARGA_ABS_AMB
	 * 
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	void fatpCargaAbsAmb(Date pCpeDtFim, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		/*-- *-------------------------------------------------------*
		  -- *    SISTEMA  - ABS                                     *
		  -- *    PROGRAMA - QABSFFA1_SQL                            *
		  -- *    FUNCAO   - ATUALIZA TABELA PARA FATURAMENTO        *
		  -- *               AMBULATORIAL DE SANGUE                  *
		  -- *    AUTOR    - LIZIANI EM 08/12/98                     *
		  -- *    ALTERACAO- FERNANDO ARRUDA EM ABRIL/2000           *
		  -- *               PARA LEITURA TABELAS ENTERPRISE         *
		  -- *    ALTERACAO- FABIO GIORDANI - GENESE - EM MAIO/2000  *
		  -- *               PARA ATUALIZACAO TABELAS FAT ENTERPRISE *
		  -- *   TEMPO ESTIMADO PARA RODAR = 15min PARA O MES INTEIRO*
		  -- *-------------------------------------------------------**/
		
		Date vDtHrInicio;
		Date vDtHrFim;
		
		//-- Constantes --//
		final String PLASMA_NORMAL   	   = "PC"; //COMPONENTE PC - PLASMA NORMAL(CONCENTRADO)
		final String PLASMA_FRESCO   	   = "PF"; //COMPONENTE PF - PLASMA FRESCO
		final String CRIOPRECIPITADO 	   = "CR"; //COMPONENTE CR - CRIOPRECIPITADO
		final String SANGUE_TOTAL    	   = "ST"; //COMPONENTE ST - SANGUE TOTAL
		final String LEUCOCITOS 	 	   = "LE"; //COMPONENTE LE - LEUCOCITOS   
		final String TDO_CODIGO      	   = "F";  //TDO_CODIGO - F
		final String CONCENTRADO_HEMACIAS  = "CH"; //CONCENTRADO DE HEMACIAS  
		final String CONCENTRADO_PLAQUETAS = "CP"; //CONCENTRADO DE PLAQUETAS  
		
		final Short QTDE_UM = Short.valueOf("1");
		
		
		//-- DAOs 
		FatProcedAmbRealizadoDAO procedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		IAghuFacade aghuFacade = getAghuFacade();
		FatConvenioSaudePlanoDAO convenioSaudePlanoDAO = getFatConvenioSaudePlanoDAO();
		IBancoDeSangueFacade bancoSangueFacade = getBancoSangueFacade();
		ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
		
		//-- busca a competência aberta de faturamento de ambulatório
		List<FatCompetencia> listaCompetencias = getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);
		
		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		
		FatCompetencia competencia = listaCompetencias.get(0);
		vDtHrInicio = DateUtil.truncaData(competencia.getId().getDtHrInicio());
		if (pCpeDtFim != null) {
			vDtHrFim = pCpeDtFim;
		} else {
			vDtHrFim = DateUtil.adicionaDias(
					   DateUtil.truncaData(
					   DateUtil.obterDataFimCompetencia(
					   DateUtil.obterData(
					   competencia.getId().getAno(), competencia.getId().getMes(), 01))), 1);
		}
		//-- Milena jan/2008
		Short vSerVin = buscarVlrShortAghParametro(AghuParametrosEnum.P_VIN_CHEFE_HEMATO);
		Integer vSerMatr = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_MATR_CHEFE_HEMATO);
		
		logar(INICIO_0_, vDtHrInicio);
		logar(FINAL_0_, vDtHrFim);
		
		//-- Consultas gerais
		Integer matriculaAgh =  buscarVlrInteiroAghParametro(AghuParametrosEnum.P_SER_MATRICULA_PADRAO); //9999999
		Short vinCodigoAgh   =  buscarVlrShortAghParametro(AghuParametrosEnum.P_SER_VIN_CODIGO_PADRAO); //955
		
		AghUnidadesFuncionais unidadeFuncional = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(buscarVlrInteiroAghParametro(AghuParametrosEnum.P_UNF_BANCO_SANGUE).shortValue()); //41 ---UNF_SEQ = BANCO DE SANGUE/HEMATOLOGIA
		
		FatConvenioSaudePlano convenioSaudePlano = convenioSaudePlanoDAO.
		obterPorChavePrimaria(new FatConvenioSaudePlanoId(
				buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1---CSP_CNV_CODIGO = SUS
				buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO) //2---CSP_SEQ= SUS AMBULATORIO,
			));  
		
		List<DoacaoColetaSangueVO> listaVO = getBancoSangueFacade().listarDoacaoColetaSangue(vDtHrInicio, vDtHrFim, TDO_CODIGO, matriculaAgh, vinCodigoAgh); 
		RapServidores servidorResp = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(vSerMatr, vSerVin));
		
		/*--  *-------------------------------------------------------*
  		  --  *  [02]                                                 *
		  --  *  DELETA REGISTROS INCLUIDOS NO MES PARA O CASO DE     *
		  --  *  REPROCESSAMENTO                                      *
		  --  *-------------------------------------------------------**/
		//UserTransaction userTransaction = obterUserTransaction(null);
		List<FatProcedAmbRealizado> listaProcedAmbRealizado = procedAmbRealizadoDAO.listarRegistrosMesProcessamento(
				new DominioSituacaoProcedimentoAmbulatorio[] {DominioSituacaoProcedimentoAmbulatorio.ABERTO, DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO}, 
				new DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] {DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, DominioLocalCobrancaProcedimentoAmbulatorialRealizado.A}, 
				new Integer[] {
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRIAGEM_CLINICA), //5967 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_COLETA_SANGUE),//5968, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSADORA_AUTOMATICA_SANGUE),//5969, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_PLAQUETAS),//5770, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_EXAMES_IMUNOHEMATOLOGICOS),//5971, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_SOROLOGIA_I),//5973, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSAMENTO_SANGUE),//5976, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II),//5979, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_UNIDADE_SANGUE_TOTAL),//5981, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CRIOPRECIPITADO),//5983, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_FRESCO),//5984, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_NORMAL),//5985,
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_LEUCOCITOS),//5986, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_HEMACIAS),//5987, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_IRRADIACAO_SANGUE_COMP_TRANSFUSAO),//5993, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLAQUETAS_AFERESE),//5771, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_COMP_TRANSFUSAO),//5995, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_DEST_TRANSFUSAO),//5994, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSIONAL_I)//5978
		                       },
               buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO), //2
               buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1
               DominioOrigemProcedimentoAmbulatorialRealizado.BSA, 
               vDtHrInicio, vDtHrFim, Boolean.FALSE);

		excluirProcedimentoAmbulatorialRealizado(nomeMicrocomputador,
				dataFimVinculoServidor, ambRealizadosON,
				listaProcedAmbRealizado);
		
		/*--  *-------------------------------------------------------*
		--  *  [03]                                                 *
		--  *  TRIAGEM CLINICA DE DOADOR DE SANGUE                  *
		--  *  SAO TODOS OS CADASTRAMENTOS DO PERIODO.              *
		--  *  CADASTROS QUE TEM NUMERO DE BOLSA BOL_NUMERO > 0  *
		--  *  (DOACOES EFETIVADAS) + OS QUE NAO POSSUEM BOLSA      *
		--  *  (REJEITADOS NA TRIAGEM)                              *
		--  *  ITEM - 3019.8                                        *
		--  *-------------------------------------------------------*
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		--  Phi 5967 cod 306010038*/
		
		inserirTriagemClinica(vDtHrInicio, vDtHrFim, matriculaAgh, vinCodigoAgh, nomeMicrocomputador, dataFimVinculoServidor,
				unidadeFuncional, convenioSaudePlano, servidorResp);
		
		/*--  *-------------------------------------------------------*
		--  *  [04]                                                 *
		--  *  COLETA DE SANGUE E PROCESSAMENTO DO SANGUE           *
		--  *  SAO AS DOACOES DO PERIODO MENOS(-) DOACOES DO TIPO   *
		--  *  FERESE                                               *
		--  *  ITENS - 3020.1  3021.0                               *
		--  *-------------------------------------------------------*
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		-- Phi 5968 Cod 306010011*/
		
		Short[] unfSeqs = {
							buscarVlrShortAghParametro(AghuParametrosEnum.P_UNID_FUNC_HEMODIALISE), //20,
							buscarVlrShortAghParametro(AghuParametrosEnum.P_UNF_BANCO_SANGUE), //41,
							buscarVlrShortAghParametro(AghuParametrosEnum.P_UNID_FUNC_SALA_OBS_ADULTO), //102,
							buscarVlrShortAghParametro(AghuParametrosEnum.P_UNID_FUNC_SALA_OBS_PEDIATRICA), //103,
							buscarVlrShortAghParametro(AghuParametrosEnum.P_UNID_FUNC_EMERGENCIA_OBSTETRICA) //124
						}; 
		
		Integer pPhiSeqColetaSangue = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_COLETA_SANGUE);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				pPhiSeqColetaSangue, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--- PROCESAMENTO DE SANGUE
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		-- Phi 5976 Cod 212020064*/
		
		//usa mesma lista da anterior pq a consulta é igual
		
		Integer pPhiSeqProcessamentoSangue = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSAMENTO_SANGUE);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				pPhiSeqProcessamentoSangue, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [05]                                                 *
		--  *  COLETA POR PROCESSADORA AUTOMATICA DE SANGUE-FERESE  *
		--  *  SAO TODAS AS DOACOES DO TIPO FERESE - F              *
		--  *  ITEM - 3023.6                                        *
		--  *-------------------------------------------------------*
		--- COLETA POR PROCESSADORA AUTOMATICA DE SANGUE 
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		-- phi 5969 cod 306010020*/
	
		listaVO = getBancoSangueFacade().listarDoacaoPorTipo(vDtHrInicio, vDtHrFim, TDO_CODIGO, matriculaAgh, vinCodigoAgh);

		Integer pPhiSeqProcessadoraAutomaticaSangue = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSADORA_AUTOMATICA_SANGUE);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				pPhiSeqProcessadoraAutomaticaSangue, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [06]                                                 *
		--  *  EXAMES IMUNOHEMATOLOGICOS E SOROLOGIA I              *
		--  *  SAO AS DOACOES DO PERIODO                            *
		--  *  ITENS - 3022.8  3027.9                               *
		--  *-------------------------------------------------------*
		--- EXAMES IMUNOHEMATOLOGICOS
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		-- phi 5971 Cod 212010018*/
		
		
		listaVO = getBancoSangueFacade().listarDoacaoPorPeriodo(vDtHrInicio, vDtHrFim, matriculaAgh, vinCodigoAgh);

		Integer pPhiSeqExamesImunohematologicos = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_EXAMES_IMUNOHEMATOLOGICOS);						
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				pPhiSeqExamesImunohematologicos, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--- SOROLOGIA I
		-- Este procedimento não será individualizado (Ney, 21/01/2011)
		-- Phi 5973 cod 212010050*/

		//mesma consulta da anterior
		Integer pPhiSeqSorologiaI = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_SOROLOGIA_I);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				pPhiSeqSorologiaI, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [07]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE SANGUE TOTAL                *
		--  *  PRE-TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE ST - SANGUE TOTAL                         *
		--  *  ITENS - 3036.8    3025.2                             *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DA UNIDADE DE SANGUE TOTAL
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5981 cod 306020149*/
		
		listaVO = getBancoSangueFacade().listarTransfusaoComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, SANGUE_TOTAL, DominioOrigemAtendimento.I);

		Integer pPhiSeqTransfusaoUnidadeSangueTotal = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_UNIDADE_SANGUE_TOTAL);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqTransfusaoUnidadeSangueTotal, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 cod 212010034*/
		
		//mesma consulta que anterior
		Integer pPhiSeqPreTransfusionalII = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqPreTransfusionalII, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [08]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE SANGUE TOTAL                *
		--  *  PRE-TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE ST - SANGUE TOTAL                         *
		--  *  ITENS - 3036.8 3025.2                                *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DA UNIDADE DE SANGUE TOTAL
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5981 Cod 306020149*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, SANGUE_TOTAL);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqTransfusaoUnidadeSangueTotal, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);

		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 Cod 212010034*/
		
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqPreTransfusionalII, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [09]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CRIOPRECIPITADO             *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE CR - CRIOPRECIPITADO                      *
		--  *  ITENS - 3037.6                                       *
		--  *-------------------------------------------------------*
		---  TRANSFUSAO DE CRIOPRECIPITADO
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5983 Cod 306020084*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CRIOPRECIPITADO, DominioOrigemAtendimento.I);

		Integer pPhiSeqTransfusaoCrioprecipitado = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CRIOPRECIPITADO);
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqTransfusaoCrioprecipitado, unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [10]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CRIOPRECIPITADO             *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE CR - CRIOPRECIPITADO                      *
		--  *  ITENS - 3037.6                                       *
		--  *-------------------------------------------------------*
		---  TRANSFUSAO DE CRIOPRECIPITADO
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5983 Cod 306020084*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CRIOPRECIPITADO);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				pPhiSeqTransfusaoCrioprecipitado, 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [11]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE PLASMA FRESCO               *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE PF - PLASMA FRESCO                        *
		--  *  ITENS - 3040.6                                       *
		--  *-------------------------------------------------------*
		---  TRANSFUSAO DE PLASMA FRESCO
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5984 Cod 306020106*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, PLASMA_FRESCO, DominioOrigemAtendimento.I);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_FRESCO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [12]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE PLASMA FRESCO               *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE PF - PLASMA FRESCO                        *
		--  *  ITENS - 3040.6                                       *
		--  *-------------------------------------------------------*
		---  TRANSFUSAO DE PLASMA FRESCO
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5984 Cod 306020106*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, PLASMA_FRESCO);
		
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_FRESCO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [13]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE PLASMA NORMAL               *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE PC - PLASMA NORMAL(CONCENTRADO)           *
		--  *  ITENS - 3016.3                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE PLASMA NORMAL
		--  Phi 5985 não encontrado relacionamento com tabela sus*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsComAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, PLASMA_NORMAL, DominioOrigemAtendimento.I);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_NORMAL), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [14]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE PLASMA NORMAL               *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE PC - PLASMA NORMAL (CONCENTRADO)          *
		--  *  ITENS - 3016.3                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE PLASMA NORMAL*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsComAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, PLASMA_NORMAL);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_NORMAL), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [15]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE LEUCOCITOS                  *
		--  *  PRE-TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE LE - LEUCOCITOS                           *
		--  *  ITENS - 3017.1 3025.2                                *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE LEUCOCITOS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5986 Cod 306020050*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, LEUCOCITOS, DominioOrigemAtendimento.I);
		
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_LEUCOCITOS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);		
				
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 Cod 212010034*/

		//mesma consulta que anterior
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [16]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE LEUCOCITOS                  *
		--  *  PRE-TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE LE - LEUCOCITOS                           *
		--  *  ITENS - 3017.1 3025.2                                *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE LEUCOCITOS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5986 306020050*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, LEUCOCITOS);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_LEUCOCITOS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 cod 212010034*/
		
		//mesma consulta que anterior
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [17]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CONCENTRADO DE HEMACIAS     *
		--  *  PRE-TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE CH - CONCENTRADO DE HEMACIAS              *
		--  *  ITENS - 3018.0  3025.2                               *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE HEMACIAS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5987 Cod 306020068*/

		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_HEMACIAS, DominioOrigemAtendimento.I);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_HEMACIAS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 cod 212010034*/

		//mesma consulta que anterior		
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [18]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CONCENTRADO DE HEMACIAS     *
		--  *  PRE TRANSFUSAO II                                    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  COMPONENTE CH - CONCENTRADO DE HEMACIAS              *
		--  *  ITENS - 3018.0  3025.2                               *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE HEMACIAS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5987 Cod 306020068*/
		

		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_HEMACIAS);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_HEMACIAS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5979 cod 212010034*/
		
		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [19]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CONCENTRADO DE PLAQUETAS    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  EXCLUI-SE BOLSAS TRANSFUNDIDAS DO TIPO FERESE        *
		--  *  COMPONENTE CP - CONCENTRADO DE PLAQUETAS             *
		--  *  ITENS - 3038.4                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE PLAQUETAS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5770 Cod 306020076*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamentoComDoacao(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_PLAQUETAS, 
				DominioOrigemAtendimento.I, TDO_CODIGO, false);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_PLAQUETAS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [20]                                                 *
		--  *  TRANSFUSAO DE UNIDADE DE CONCENTRADO DE PLAQUETAS    *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  EXCLUI-SE TRANSFUSAO DE BOLSAS TIPO FERESE           *
		--  *  COMPONENTE CP - CONCENTRADO DE PLAQUETAS             *
		--  *  ITENS - 3038.4                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE CONCENTRADO DE PLAQUETAS
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5770 Cod 306020076*/
		
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamentoComDoacao(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_PLAQUETAS, TDO_CODIGO, false);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_PLAQUETAS), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [21]                                                 *
		--  *  TRANSFUSAO DE PLAQUETAS POR AFERESE                  *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE COM PRONTUARIO (TIPO_PAC 1  E  5) E         *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  SOMENTE   BOLSAS TRANSFUNDIDAS DO TIPO FERESE        *
		--  *  COMPONENTE CP - CONCENTRADO DE PLAQUETAS             *
		--  *  ITENS - 3039.2                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE PLAQUETAS POR AFERESE
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5771 Cod 306020092*/
		
		listaVO = bancoSangueFacade.listarTransfusaoComExistsSemAgrupamentoComDoacao(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_PLAQUETAS, 
				DominioOrigemAtendimento.I, TDO_CODIGO, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLAQUETAS_AFERESE), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--  *-------------------------------------------------------*
		--  *  [22]                                                 *
		--  *  TRANSFUSAO DE PLAQUETAS POR AFERESE                  *
		--  *  SAO TODOS OS COMPONENTES FORNECIDOS NO PERIODO PARA  *
		--  *  UNIDADES AMBULATORIAIS (UNID. 20,41,102,103,124)     *
		--  *  E QUE O PACIENTE NAO TENHA INTERNADO NO MESMO DIA.   *
		--  *  VAI SER CONTADO SEPARADAMENTE OS FORNECIMENTOS PARA  *
		--  *  PACIENTE SEM PRONTUARIO (TIPO_PAC 2, 3 E 4)          *
		--  *  SOMENTE   TRANSFUSAO DE BOLSAS TIPO FERESE           *
		--  *  COMPONENTE CP - CONCENTRADO DE PLAQUETAS             *
		--  *  ITENS - 3039.2                                       *
		--  *-------------------------------------------------------*
		--- TRANSFUSAO DE PLAQUETAS POR AFERESE
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5771 Cod 306020092*/
		listaVO = bancoSangueFacade.listarTransfusaoSemExistsSemAgrupamentoComDoacao(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, unfSeqs, DominioMcoType.FPA, false, CONCENTRADO_PLAQUETAS, TDO_CODIGO, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLAQUETAS_AFERESE), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [23]                                                 *
		--  *  IRRADIACAO                                           *
		--  *  SAO TODAS AS TRANSFUSOES DE COMPONENTES IRRADIADOS   *
		--  *  COM INDICACAO MEDICA (SOLICITACAO DE IRRADIADO)      *
		--  *  OBS. IND_IRRD = 'S' SANGUE IRRADIADO E FORNECIDO     *
		--  *                      SEM SOLICITACAO MEDICA(NAO CONTA)*
		--  *       IND_IRRD = 'X' SANGUE IRRADIADO E FORNECIDO     *
		--  *                      COM SOLICITACAO MEDICA           *
		--  *  ITEM - 3029.5                                        *
		--  *-------------------------------------------------------*
		--- IRRADIACAO DE SANGUE E COMPENENTES DESTINADOS A TRANSFUSAO
		-- Este procedimento NÃO será individualizado (Ney, 21/01/2011)
		-- Phi 5993 Cod 212020030*/
		
		listaVO = bancoSangueFacade.listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, DominioMcoType.FPA, false, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_IRRADIACAO_SANGUE_COMP_TRANSFUSAO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*-- PRE-TRANSFUSIONAL I
		-- Este procedimento será individualizado (Ney, 21/01/2011)
		-- Phi 5993 cod 212010026*/
		
		listaVO = getBancoSangueFacade().listarRegSanguineoPacienteComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, DominioOrigemAtendimento.I);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSIONAL_I), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--- PRE-TRANSFUSIONAL II
		-- Este procedimento NÂO será individualizado (Ney, 21/01/2011)
		-- Phi 5993 Cod 212020030*/
		
		listaVO = bancoSangueFacade.listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, DominioMcoType.FOB, false, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_IRRADIACAO_SANGUE_COMP_TRANSFUSAO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
						
		/*--  *-------------------------------------------------------*
		--  *  [24]                                                 *
		--  *  DELEUCOTIZAÇÃO                                          *
		--  *  SAO TODAS AS TRANSFUSOES DE COMPONENTES DELEUCOT.   *
		--  *  COM INDICACAO MEDICA (SOLICITACAO DE DELEUCOTIZADO)     *
		--  *  OBS. IND_FILTRADO = 'S' NA TABELA ABS_COMPONENTE_           *
		--  *	MOVIMENTADOS   SIGNIFICA SOLICITADO E FORNECIDO       *
		--  *            FILTRADO
		--  *-------------------------------------------------------*
		--- DELEUCOTIZACAO  FORNECIDO A PACIENTES
		-- Este procedimento NÂO será individualizado (Ney, 21/01/2011)
		--  Phi 5995 Cod 212020013*/
		
		listaVO = bancoSangueFacade.listarMovimentosComponentesSemExistsComAgrupamentoFiltrado(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, DominioMcoType.FPA, false, CONCENTRADO_HEMACIAS, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_COMP_TRANSFUSAO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
				
		/*--- DELEUCOTIZACAO  FORNECIDO A OUTROS BANCOS
		-- Este procedimento NÂO será individualizado (Ney, 21/01/2011)
		--  Phi 5995 Cod 212020013*/
		
		listaVO = bancoSangueFacade.listarMovimentosComponentesSemExistsComAgrupamentoFiltrado(vDtHrInicio, vDtHrFim, 
				matriculaAgh, vinCodigoAgh, DominioMcoType.FOB, false, CONCENTRADO_HEMACIAS, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, null, null,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_COMP_TRANSFUSAO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		/*--  *-------------------------------------------------------*
		--  *  [25]                                                 *
		--  *  DELEUCOTIZAÇÃO                                          *
		--  *  SAO TODAS AS TRANSFUSOES DE CP                         DELEUCOT.   *
		--  *  COM INDICACAO MEDICA (SOLICITACAO DE DELEUCOTIZADO)     *
		--  *  OBS. IND_FILTRADO = 'S' NA TABELA ABS_COMPONENTE_           *
		--  *	MOVIMENTADOS   SIGNIFICA SOLICITADO E FORNECIDO       *
		--  *            FILTRADO
		--  *-------------------------------------------------------*
		--- DELEUCOTIZACAO  FORNECIDO A PACIENTES
		-- Este procedimento NÂO será individualizado (Ney, 21/01/2011)
		--  Phi 5994 Cod 212020021*/
		
		listaVO = bancoSangueFacade.listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado(vDtHrInicio, vDtHrFim, 
				DominioMcoType.FPA, false, CONCENTRADO_PLAQUETAS, true);

		inserirFatProcedAmbRealizado(listaVO,
				DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 1, QTDE_UM,
				buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_DEST_TRANSFUSAO), 
				unidadeFuncional, convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
				servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor);
		
		listaProcedAmbRealizado = procedAmbRealizadoDAO.listarRegistrosMesComQtdeZero(
				DominioSituacaoProcedimentoAmbulatorio.ABERTO, 
				DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, 
				new Integer[] {
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRIAGEM_CLINICA), //5967 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_COLETA_SANGUE),//5968, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSADORA_AUTOMATICA_SANGUE),//5969, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_PLAQUETAS),//5770, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_EXAMES_IMUNOHEMATOLOGICOS),//5971, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_SOROLOGIA_I),//5973, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PROCESSAMENTO_SANGUE),//5976, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_PRE_TRANSFUSIONAL_II),//5979, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_UNIDADE_SANGUE_TOTAL),//5981, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CRIOPRECIPITADO),//5983, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_FRESCO),//5984, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLASMA_NORMAL),//5985,
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_LEUCOCITOS),//5986, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_CONCENTRADO_HEMACIAS),//5987, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_IRRADIACAO_SANGUE_COMP_TRANSFUSAO),//5993, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSAO_PLAQUETAS_AFERESE),//5771, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_COMP_TRANSFUSAO),//5995, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_DELEUCOTIZACAO_SANGUE_DEST_TRANSFUSAO),//5994, 
								buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRANSFUSIONAL_I)//5978
		                       },
               buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO), //2
               buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1
               DominioOrigemProcedimentoAmbulatorialRealizado.BSA);		
		
		excluirProcedimentoAmbulatorialRealizado(nomeMicrocomputador,
				dataFimVinculoServidor, ambRealizadosON,
				listaProcedAmbRealizado);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void inserirFatProcedAmbRealizado(
			List<DoacaoColetaSangueVO> listaVO, DominioSituacaoProcedimentoAmbulatorio situacao,
			DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, Integer pacCodigo, Short quantidade,
			Integer phiSeq, AghUnidadesFuncionais unidadeFuncional,
			FatConvenioSaudePlano convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado indOrigem,
			RapServidores servidorResp, AghEspecialidades especialidade, String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws ApplicationBusinessException, BaseException {
		
		for (DoacaoColetaSangueVO doacaoSangueVO : listaVO) {
			inserirRegistro(doacaoSangueVO.getBolData(),
					   situacao,
					   localCobranca,
					   pacCodigo == null ? null : doacaoSangueVO.getPacCodigo(),
					   quantidade == null ? doacaoSangueVO.getQuantidade().shortValue() : quantidade,
					   phiSeq,
					   doacaoSangueVO.getSerMatricula(),
					   doacaoSangueVO.getSerVinCodigo().shortValue(),
					   unidadeFuncional,
					   convenioSaudePlano,
					   indOrigem,
					   servidorResp, especialidade, nomeMicrocomputador, dataFimVinculoServidor
				);						
		}
		
	}	

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void inserirTriagemClinica(Date vDtHrInicio, Date vDtHrFim, Integer matriculaAgh, Short vinCodigoAgh, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			AghUnidadesFuncionais unidadeFuncional,
			FatConvenioSaudePlano convenioSaudePlano, RapServidores servidorResp)
			throws ApplicationBusinessException, BaseException {
		
		Integer pPhiSeqTriagemClinica = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_TRIAGEM_CLINICA); 
		
		List<DoadorSangueTriagemClinicaVO> listaProcedAmbRealizTriagemCli = getBancoSangueFacade().
				listarDoadorSangueTriagemClinica(vDtHrInicio, vDtHrFim, matriculaAgh, vinCodigoAgh);
		
		for (DoadorSangueTriagemClinicaVO procedAmbRealizadosTriagemClinicaVO : listaProcedAmbRealizTriagemCli) {
			inserirRegistro(procedAmbRealizadosTriagemClinicaVO.getBoldataAsDate(),
					   DominioSituacaoProcedimentoAmbulatorio.ABERTO,
					   DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
					   null,
					   procedAmbRealizadosTriagemClinicaVO.getQuantidade().shortValue(),
					   pPhiSeqTriagemClinica, //5967, 
					   procedAmbRealizadosTriagemClinicaVO.getSermatricula(),
					   procedAmbRealizadosTriagemClinicaVO.getServincodigo().shortValue(),
					   unidadeFuncional,
					   convenioSaudePlano,
					   DominioOrigemProcedimentoAmbulatorialRealizado.BSA,
					   servidorResp, null, nomeMicrocomputador, dataFimVinculoServidor
				);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void excluirProcedimentoAmbulatorialRealizado(
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			ProcedimentosAmbRealizadosON ambRealizadosON,
			List<FatProcedAmbRealizado> listaProcedAmbRealizado)
			throws BaseException {
		for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {
			ambRealizadosON.excluirProcedimentoAmbulatorialRealizado(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
		}
	}

	protected void inserirRegistro(Date dataHoraBolData, DominioSituacaoProcedimentoAmbulatorio situacao,
			DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, Integer pacCodigo, Short quantidade,
			Integer phiSeq, Integer serMatricula, Short serVinCodigo, AghUnidadesFuncionais unidadeFuncional,
			FatConvenioSaudePlano convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado indOrigem,
			RapServidores servidorResp, AghEspecialidades especialidade, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		
		FatProcedAmbRealizado elemento = new FatProcedAmbRealizado();
		
		elemento.setDthrRealizado(dataHoraBolData);
		elemento.setSituacao(situacao);
		elemento.setLocalCobranca(localCobranca);
		
		if (pacCodigo != null) {	
			AipPacientes paciente = pacienteFacade.obterNomePacientePorCodigo(pacCodigo);
			elemento.setPaciente(paciente);
		}
		
		elemento.setQuantidade(quantidade);

		FatProcedHospInternos procedHospInternos = procedHospInternosDAO.obterPorChavePrimaria(phiSeq); //--- PHI_SEQ = COLETA DE SANGUE
		elemento.setProcedimentoHospitalarInterno(procedHospInternos);

		RapServidores servidor = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(serMatricula, serVinCodigo));
		elemento.setServidor(servidor);
		
		elemento.setUnidadeFuncional(unidadeFuncional);
		
		elemento.setConvenioSaudePlano(convenioSaudePlano);
		
		elemento.setIndOrigem(indOrigem); //---IND_ORIGEM = DOACAO DE SANGUE
		
		elemento.setServidorResponsavel(servidorResp);
		
		if(especialidade != null) {
			elemento.setEspecialidade(especialidade);
		}
		
		getProcedimentosAmbRealizadosON().inserirFatProcedAmbRealizado(elemento, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	protected void inserirRegistro(Date dthrRealizado, DominioSituacaoProcedimentoAmbulatorio situacao,
			DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, Short quantidade, 
			Integer iseSoeSeq, Short iseSeqp, Integer phiSeq, Integer serMatricula, Short serVinCodigo, 
			AghUnidadesFuncionais unidadeFuncional, Short espSeq, Integer pacCodigo, 
			FatConvenioSaudePlano convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado indOrigem, 
			Integer atdSeq, Integer cidSeq,	RapServidores servidorResp, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		FatProcedAmbRealizado elemento = new FatProcedAmbRealizado();
		
		elemento.setDthrRealizado(dthrRealizado);
		elemento.setSituacao(situacao);
		elemento.setLocalCobranca(localCobranca);
		elemento.setQuantidade(quantidade);
		
		if (iseSoeSeq != null && iseSeqp != null) {
			AelItemSolicitacaoExames itemSolicitacaoExames = 
				this.getExamesFacade().obterItemSolicitacaoExameOriginal(new AelItemSolicitacaoExamesId(iseSoeSeq, iseSeqp));
			elemento.setItemSolicitacaoExame(itemSolicitacaoExames);
		}
		
		FatProcedHospInternos procedHospInternos = procedHospInternosDAO.obterPorChavePrimaria(phiSeq);
		elemento.setProcedimentoHospitalarInterno(procedHospInternos);

		if (serMatricula != null && serVinCodigo != null) {
			RapServidores servidor = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(serMatricula, serVinCodigo));
			elemento.setServidor(servidor);
		}
		
		elemento.setUnidadeFuncional(unidadeFuncional);
		
		if (espSeq != null) {
			AghEspecialidades especialidade = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeq);
			if(especialidade != null) {
				elemento.setEspecialidade(especialidade);
			}
		}
		
		if (pacCodigo != null) {	
			AipPacientes paciente = pacienteFacade.obterNomePacientePorCodigo(pacCodigo);
			elemento.setPaciente(paciente);
		}
		
		elemento.setConvenioSaudePlano(convenioSaudePlano);
		
		elemento.setIndOrigem(indOrigem); //---IND_ORIGEM = DOACAO DE SANGUE
		
		if(atdSeq != null) {
			elemento.setAtendimento(this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq));
		}
		
		if (cidSeq != null) {
			AghCid cid = getAghuFacade().obterAghCidsPorChavePrimaria(cidSeq);
			elemento.setCid(cid);
		}
		
		elemento.setServidorResponsavel(servidorResp);
		
		procedimentosAmbRealizadosON.inserirFatProcedAmbRealizado(elemento, nomeMicrocomputador, dataFimVinculoServidor);
	}
	

	protected void inserirRegistro(Date dthrConsistenciaOk, DominioSituacaoProcedimentoAmbulatorio situacao,
			DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, Integer pacCodigo, Short quantidade,
			Integer phiSeq, Integer serMatricula, Short serVinCodigo, AghUnidadesFuncionais unidadeFuncional,
			FatConvenioSaudePlano convenioSaudePlano, DominioOrigemProcedimentoAmbulatorialRealizado indOrigem,
			RapServidores servidorResp, AghEspecialidades especialidade, Long tdcTrgSeq, Short tdcSeqP, 
			final Long txaTrgSeq, final Short txaSeqp, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		
		FatProcedAmbRealizado elemento = new FatProcedAmbRealizado();
		
		elemento.setDthrRealizado(dthrConsistenciaOk);
		elemento.setSituacao(situacao);
		elemento.setLocalCobranca(localCobranca);
		
		if (pacCodigo != null) {	
			AipPacientes paciente = pacienteFacade.obterNomePacientePorCodigo(pacCodigo);
			elemento.setPaciente(paciente);
		}
		
		elemento.setQuantidade(quantidade);

		FatProcedHospInternos procedHospInternos = procedHospInternosDAO.obterPorChavePrimaria(phiSeq);
		elemento.setProcedimentoHospitalarInterno(procedHospInternos);

		RapServidores servidor = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(serMatricula, serVinCodigo));
		elemento.setServidor(servidor);
		
		elemento.setUnidadeFuncional(unidadeFuncional);
		
		elemento.setConvenioSaudePlano(convenioSaudePlano);
		
		elemento.setIndOrigem(indOrigem); //---IND_ORIGEM = DOACAO DE SANGUE
		
		elemento.setServidorResponsavel(servidorResp);
		
		if(especialidade != null) {
			elemento.setEspecialidade(especialidade);
		}
		// MamTrgMedicacoes
		elemento.setTdcTrgSeq(tdcTrgSeq);
		elemento.setTdcSeqp(tdcSeqP);
		// MamTrgExames
		elemento.setTxaTrgSeq(txaTrgSeq);
		elemento.setTxaSeqp(txaSeqp);
		
		procedimentosAmbRealizadosON.inserirFatProcedAmbRealizado(elemento, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	
	/**
	 * ORADB Procedure FATP_CARGA_TRIAGENS
	 * 
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	void fatpCargaTriagens(Date pCpeDtFim, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
	
		Date vDtHrInicio;
		Date vDtHrFim;
		
		final Short QTDE_UM = Short.valueOf("1");
				
		ProcedimentosAmbRealizadosON ambRealizadosON = getProcedimentosAmbRealizadosON();
		
		//-- busca a competência aberta de faturamento de ambulatório
		List<FatCompetencia> listaCompetencias = getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);
		
		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		FatCompetencia competencia = listaCompetencias.get(0);
		vDtHrInicio = DateUtil.truncaData(competencia.getId().getDtHrInicio());
		if (pCpeDtFim != null) {
			vDtHrFim = pCpeDtFim;
		} else {
			vDtHrFim = DateUtil.adicionaDias(
					   DateUtil.truncaData(
					   DateUtil.obterDataFimCompetencia(
					   DateUtil.obterData(
					   competencia.getId().getAno(), competencia.getId().getMes(), 01))), 1);
		}
		
		Short vSerVin = buscarVlrShortAghParametro(AghuParametrosEnum.P_VIN_CHEFE_ENF_EM);
		Integer vSerMatr = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_MATR_CHEFE_ENF_EM);
		
		logar(INICIO_0_, vDtHrInicio);
		logar(FINAL_0_, vDtHrFim);
		
		/*--  *-------------------------------------------------------*
		  --  *  DELETA REGISTROS INCLUIDOS NO MES PARA O CASO DE     *
		  --  *  REPROCESSAMENTO                                     *
		  --  *-------------------------------------------------------**/
		
		List<FatProcedAmbRealizado> listaProcedAmbRealizado = getFatProcedAmbRealizadoDAO()
				.listarRegistrosMesProcessamento(
						new DominioSituacaoProcedimentoAmbulatorio[] {
								DominioSituacaoProcedimentoAmbulatorio.ABERTO,
								DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO },
						new DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] {
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.A },
						new Integer[] {buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CONSULTA_ATENDTO_ATENCAO_BASICA_ENFERMEIRO)},//6050
						buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO), //2
						buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1
						DominioOrigemProcedimentoAmbulatorialRealizado.CON,
						vDtHrInicio, vDtHrFim, Boolean.TRUE);
		
		excluirProcedimentoAmbulatorialRealizado(nomeMicrocomputador,
				dataFimVinculoServidor, ambRealizadosON,
				listaProcedAmbRealizado);
		
		/*--*-------------------------------------------------------*
		--  *  TRIAGENS REALIZADAS NA EMERGENCIA                    *
		--  *-------------------------------------------------------* */
		FatConvenioSaudePlano convenioSaudePlano = getFatConvenioSaudePlanoDAO().
		obterPorChavePrimaria(new FatConvenioSaudePlanoId(
				buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1---CSP_CNV_CODIGO = SUS
				buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO) //2---CSP_SEQ= SUS AMBULATORIO,
			)); 
		
		RapServidores servidorResp = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(vSerMatr, vSerVin));
		AghEspecialidades especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(
				this.buscarAghParametro(AghuParametrosEnum.P_CODIGO_ESPECIALIDADE_EMERGENCIA).getVlrNumerico().shortValue());
		
		List<TriagemRealizadaEmergenciaVO> listaRealizadaEmergenciaVO = this.
				getAmbulatorioFacade().listarTriagemRealizadaEmergencia(vDtHrInicio, vDtHrFim);
		
		Integer pConsultaAtendtoAtencaoBasicaEnfermeiro = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CONSULTA_ATENDTO_ATENCAO_BASICA_ENFERMEIRO);
		
		for (TriagemRealizadaEmergenciaVO triagemRealizadaEmergenciaVO : listaRealizadaEmergenciaVO) {
			inserirRegistro(triagemRealizadaEmergenciaVO.getCriadoEm(),
					   DominioSituacaoProcedimentoAmbulatorio.ABERTO,
					   DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
					   triagemRealizadaEmergenciaVO.getPacCodigo(),
					   QTDE_UM,
					   pConsultaAtendtoAtencaoBasicaEnfermeiro, //6050
					   triagemRealizadaEmergenciaVO.getSerMatricula(),
					   triagemRealizadaEmergenciaVO.getSerVinCodigo().shortValue(),
					   getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(triagemRealizadaEmergenciaVO.getUnfSeq()),
					   convenioSaudePlano,
					   DominioOrigemProcedimentoAmbulatorialRealizado.CON,
					   servidorResp, especialidade, nomeMicrocomputador, dataFimVinculoServidor
				);
		}
		
		//commitUserTransaction(userTransaction);
	}

	/**
	 * ORADB Procedure RN_FATP_ENC_AMB_NEW
	 * 	
	 * @param pPrevia
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rnFatpEncAmbNew(Boolean pPrevia, Date pCpeDtFim, final AghJobDetail log, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		//-- DAOs 
		FatCompetenciaDAO competenciaDAO = getFatCompetenciaDAO();
		FatEspelhoProcedAmbDAO espelhoProcedAmbDAO = getFatEspelhoProcedAmbDAO();
		FatArqEspelhoProcedAmbDAO arqEspelhoProcedAmbDAO = getFatArqEspelhoProcedAmbDAO();
		
		List<FatCompetencia> listaCompetencias = competenciaDAO.obterCompetenciasPorModuloESituacoes(
				DominioModuloCompetencia.AMB, DominioSituacaoCompetencia.A);
		
		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		
		FatCompetencia competenciaAmb = listaCompetencias.get(0);
		
	
	   /* TODO APAC FORA DO ESCOPO
	    * 
	    * listaCompetencias = competenciaDAO.obterCompetenciasPorModuloESituacoes(
				DominioModuloCompetencia.APAC, DominioSituacaoCompetencia.A);
		
		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		
		FatCompetencia competenciaApac = listaCompetencias.get(0);*/
	
	
		//aghp_grava_mensagem ('Início Processo Enc/Prévia BPA/BPI'
        //,'fat_log_enc_previa.txt','N');
		//logar("Início Processo Enc/Prévia BPA/BPI, fat_log_enc_previa.txt, N");
		
		arqEspelhoProcedAmbDAO.removeArqEspelhoProcedAmb(competenciaAmb.getId().getDtHrInicio(), 
				competenciaAmb.getId().getAno(), competenciaAmb.getId().getMes(), DominioModuloCompetencia.AMB, null);
		
		espelhoProcedAmbDAO.removeEspelhoProcedAmb(competenciaAmb.getId().getDtHrInicio(), 
				competenciaAmb.getId().getAno(), competenciaAmb.getId().getMes(), DominioModuloCompetencia.AMB, null);
		
		//  -- executa carga do banco de sangue
		fatpCargaAbsAmb(pCpeDtFim, nomeMicrocomputador, dataFimVinculoServidor); //--JRS(13/11/2000)
		getSchedulerFacade().adicionarLog(log, "##### AGHU - Execução do processo de carga ABS OK!");
		logDebug("Chamando Clear carga ABS");
		getFaturamentoFacade().clear();
		logDebug("Fim Clear carga ABS");

		//-- executa carga triagens
		fatpCargaTriagens(pCpeDtFim, nomeMicrocomputador, dataFimVinculoServidor);
		getSchedulerFacade().adicionarLog(log, "##### AGHU - Execução do processo de carga Triagens OK!");
		logDebug("Chamando Clear carga Triagens");
		getFaturamentoFacade().clear();
		logDebug("Fim Clear carga Triagens");
		
		//-- executa carga dos procedimentos oriundos das triagens emergência
		fatpCargaProcTrg(pCpeDtFim, nomeMicrocomputador, dataFimVinculoServidor);
		getSchedulerFacade().adicionarLog(log, "##### AGHU - Execução carga procedimentos Triagens Emrg. OK!");
		logDebug("Chamando Clear carga Triagens Emrg.");
		getFaturamentoFacade().clear();
		logDebug("Fim Clear carga Triagens Emrg.");
		
		//-- executa carga coleta de redome -- Milena outubro/2006
		fatpCargaColetaRD(pCpeDtFim, nomeMicrocomputador, dataFimVinculoServidor);
		getSchedulerFacade().adicionarLog(log, "##### AGHU - Execução carga coleta de redome OK!");
		logDebug("Chamando Clear carga redome");
		getFaturamentoFacade().clear();
		logDebug("Fim Clear carga redome");
		
		//-- Alterado para gerar journal quando rotina de apac somente nas prévias
		//-- de final de semana e no encerramento. Milena agosto/2003
		atribuirContextoSessao(FATK_PMR_RN_V_PMR_JOURNAL, Boolean.TRUE);
		
		LocalDateTime dt = new LocalDateTime(new Date());
		
		if (pPrevia && 
			dt.dayOfWeek().get() != DateTimeConstants.SUNDAY   &&
			dt.dayOfWeek().get() != DateTimeConstants.SATURDAY &&
			dt.dayOfWeek().get() != DateTimeConstants.MONDAY) {
			atribuirContextoSessao(FATK_PMR_RN_V_PMR_JOURNAL, Boolean.FALSE);
		}
		
		/** 
		 *  TODO APAC está fora do escopo de desenvolvimento em 27/12/2011, ao se desenvolver tal escopo, descomentar tal código
		 */ 
		/*
		if (!pPrevia) {
			//-- -------------------------------------------------------------
			//-- Não encerrar BPA se apap, apan e APAC não estiver encerrada
			//-- -------------------------------------------------------------
			

			FatCompetencia competenciaApap = competenciaDAO.obterCompetenciaModuloMesAno(
					DominioModuloCompetencia.APAP, competenciaAmb.getId().getMes(), competenciaAmb.getId().getAno());
			
			if (competenciaApap == null) {
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
			}
			
			FatCompetencia competenciaApac2 = competenciaDAO.obterCompetenciaModuloMesAno(
					DominioModuloCompetencia.APAC, competenciaAmb.getId().getMes(), competenciaAmb.getId().getAno());
			
			if (competenciaApac2 == null) {
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
			}
			
			FatCompetencia competenciaApan = competenciaDAO.obterCompetenciaModuloMesAno(
					DominioModuloCompetencia.APAN, competenciaAmb.getId().getMes(), competenciaAmb.getId().getAno());
			
			if (competenciaApan == null) {
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
			}
			
			//-- IF reg_comp_apap.ind_situacao = 'A' THEN
			//-- Milena maio/2004 - não pode testar 'A', porque no início do processamento é
			//-- alterada para 'M' e se houver cancelamento, fica 'M'. Se término OK, fica 'P'
			
			if (!competenciaApap.getIndSituacao().equals(DominioSituacaoCompetencia.P)) {
				logar("Execução do processo de encerramento/prévia BPA CANCELADO! APAP AINDA NÃO FOI ENCERRADA, fat_log_enc_previa.txt");
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_MODULO_AMB_ENCERRADO_ANTES_APAP);
			}

			if (!competenciaApac2.getIndSituacao().equals(DominioSituacaoCompetencia.P)) {
				logar("Execução do processo de encerramento/prévia BPA CANCELADO! APAC AINDA NÃO FOI ENCERRADA, fat_log_enc_previa.txt");
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_MODULO_AMB_ENCERRADO_ANTES_APAC);
			}
			
			if (!competenciaApan.getIndSituacao().equals(DominioSituacaoCompetencia.P)) {
				logar("Execução do processo de encerramento/prévia BPA CANCELADO! APAN AINDA NÃO FOI ENCERRADA, fat_log_enc_previa.txt");
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_MODULO_AMB_ENCERRADO_ANTES_APAN);
			}
		}
		*/
		
		//ALTERAÇÃO REALIZADA PARA MELHORAR A PERFORMANCE DO AGENDAMENTO DO ENCERRAMENTO DO FATURAMENTO DE AMBULATÓRIO
		//SEGUNDO A MILENA A GRAVAÇÃO DA JOURNAL NÃO PRECISA ACONTECER DURANTE O PROCESSAMENTO DO BPA/BPI, 
		//atribuirContextoSessao(VariaveisSessaoEnum.FATK_PMR_RN_V_PMR_JOURNAL,Boolean.FALSE);
		
		// Ney 18/02/2013 
		// Atualiza exames de pré transplantes para serem cobrados dentro de um único código de procedimentos.
		getFaturamentoAtualizaExamesPreTransplantesRN().atualizaPmrKitsPre(competenciaAmb, nomeMicrocomputador, dataFimVinculoServidor);
		
		
		getFaturamentoFatkPmrRN().geraEspelhoFaturamentoAmbulatorio(competenciaAmb, pCpeDtFim, pPrevia, log, nomeMicrocomputador, dataFimVinculoServidor);
		logar("Execução dos processos de encerramento/prévia BPA OK!, fat_log_enc_previa.txt");
		
		// executa encerramento de ambulatorio
		
		//-- CFV(02/04/2001) - Atualiza competencia para faturada e apresentada(amb)

		if (!pPrevia) {
			/**
			 * TODO APAC fora do escopo
			 */
			//-- altera pmr para situacao 'P' a partir da fat_itens_conta_apac - Milena set/2002
			/*getFaturamentoFatkPmrRN().rnPmrpAtuEncPmr(DominioModuloFatContaApac.APAC, 
					competenciaApac.getId().getDtHrInicio(), 
					competenciaApac.getId().getMes().byteValue(),
					competenciaApac.getId().getAno().shortValue(), 
					pPrevia);*/
			listaCompetencias = competenciaDAO.obterCompetenciasPorModuloESituacoes(
					DominioModuloCompetencia.AMB, DominioSituacaoCompetencia.M);
			
			if (listaCompetencias.isEmpty()) {
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
			}
			
			competenciaAmb = listaCompetencias.get(0);
			
			//UserTransaction userTransaction = obterUserTransaction(null);
			if (!getFaturamentoFatkCpeRN().rnCpecAtuFaturado(competenciaAmb)) { //--se erro na funcao
				logar("Nao atualizou comp AMB p/ faturada e apres. ! fat_log_enc_previa.txt");
			}
			//commitUserTransaction(userTransaction);			
		}
	}
	
	
	
	/**
	 * ORADB PROCEDURE FATP_CARGA_PROC_TRG
	 * 
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	void fatpCargaProcTrg(Date pCpeDtFim, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		
		Date vDtHrInicio;
		Date vDtHrFim;
				
		//-- busca a competência aberta de faturamento de ambulatório
		List<FatCompetencia> listaCompetencias = getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);
		
		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		
		FatCompetencia competencia = listaCompetencias.get(0);
		vDtHrInicio = DateUtil.truncaData(competencia.getId().getDtHrInicio());
		if (pCpeDtFim != null) {
			vDtHrFim = pCpeDtFim;
		} else {
			vDtHrFim = DateUtil.adicionaDias(
					   DateUtil.truncaData(
					   DateUtil.obterDataFimCompetencia(
					   DateUtil.obterData(
					   competencia.getId().getAno(), competencia.getId().getMes(), 01))), 1);
		}
		
		Short vSerVin = buscarVlrShortAghParametro(AghuParametrosEnum.P_VIN_CHEFE_ENF_EM);
		Integer vSerMatr = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_MATR_CHEFE_ENF_EM);
		
		logar(INICIO_0_, vDtHrInicio);
		logar(FINAL_0_, vDtHrFim);
		
		
		
		/*--  *-------------------------------------------------------*
		  --  *  DELETA REGISTROS INCLUIDOS NO MES PARA O CASO DE     *
		  --  *  REPROCESSAMENTO                                     *
		  --  *-------------------------------------------------------**/
		
		List<FatProcedAmbRealizado> listaProcedAmbRealizado = getFatProcedAmbRealizadoDAO()
				.listarRegistrosMesProcessamento(
						new DominioSituacaoProcedimentoAmbulatorio[] {
								DominioSituacaoProcedimentoAmbulatorio.ABERTO,
								DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO },
						new DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] {
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.A },
						buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO), //2
						buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1
						DominioOrigemProcedimentoAmbulatorialRealizado.TRG,
						vDtHrInicio, vDtHrFim);
		ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON = getProcedimentosAmbRealizadosON();

		excluirProcedimentoAmbulatorialRealizado(nomeMicrocomputador,
				dataFimVinculoServidor, procedimentosAmbRealizadosON,
				listaProcedAmbRealizado);
		
		/*--*----------------------------------------------------------------*
		  --* PROCEDIMENTOS EXECUTADOS NAS TRIAGENS REALIZADAS NA EMERGENCIA *
		  --*----------------------------------------------------------------*
		*/
		
		FatConvenioSaudePlano convenioSaudePlano = getFatConvenioSaudePlanoDAO().
		obterPorChavePrimaria(new FatConvenioSaudePlanoId(
				buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1---CSP_CNV_CODIGO = SUS
				buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO) //2---CSP_SEQ= SUS AMBULATORIO,
			)); 
		
		RapServidores servidorResp = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(vSerMatr, vSerVin));
		AghEspecialidades especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(this.buscarAghParametro(AghuParametrosEnum.P_CODIGO_ESPECIALIDADE_EMERGENCIA).getVlrNumerico().shortValue());
		
		//-- Consultas gerais
		Integer matriculaAgh =  buscarVlrInteiroAghParametro(AghuParametrosEnum.P_SER_VIN_CODIGO_PADRAO); //9999999
		Short vinCodigoAgh   =  buscarVlrShortAghParametro(AghuParametrosEnum.P_SER_VIN_CODIGO_PADRAO); //955
		
		List<ProcedHospInternosTriagemClinicaVO> listaHospIntTriagemClinicaVO = 
			this.getFatProcedHospInternosDAO().listarProcedHospInternosTriagemClinica(vDtHrInicio, vDtHrFim, matriculaAgh, vinCodigoAgh);		
		
		//UserTransaction userTransaction = obterUserTransaction(null);
		
		for (ProcedHospInternosTriagemClinicaVO hospIntTriagemClinicaVO : listaHospIntTriagemClinicaVO) {
			inserirRegistro(hospIntTriagemClinicaVO.getDthrConsistenciaOk(),
					   DominioSituacaoProcedimentoAmbulatorio.ABERTO,
					   DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
					   hospIntTriagemClinicaVO.getPacCodigo(),
					   Short.valueOf("1"),
					   hospIntTriagemClinicaVO.getPhiSeq(),
					   hospIntTriagemClinicaVO.getSerMatricula(),
					   hospIntTriagemClinicaVO.getSerVinCodigo().shortValue(),
					   getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(hospIntTriagemClinicaVO.getUnfSeq()),
					   convenioSaudePlano,
					   DominioOrigemProcedimentoAmbulatorialRealizado.TRG,
					   servidorResp, 
					   especialidade,
					   hospIntTriagemClinicaVO.getTrgSeq(),
					   hospIntTriagemClinicaVO.getSeqp(),
					   hospIntTriagemClinicaVO.getTxaTrgSeq(),
					   hospIntTriagemClinicaVO.getTxaSeqp(), 
					   nomeMicrocomputador,
					   dataFimVinculoServidor
				);
		}
		
		//commitUserTransaction(userTransaction);
		logar("fim ");
	}
	
	/**
	 * ORADB Procedure RN_FATP_EXEC_FAT_NEW
	 * 
	 * OBS.: Implementado parcialmente
	 * 
	 * @param pModulos
	 * @param pPrevia
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	void rnFatpExecFatNew(final DominioOpcaoEncerramentoAmbulatorio pModulos, final Boolean pPrevia, final Date pCpeDtFim, final AghJobDetail job, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		/* Executa processos de fatura ambulatorial ordenadamente */ 		
		// Retirado pois não faz parte do escopo. Quando implementado APAC deve ser reativado e testado. 
		//getFaturamentoFatkPmrRN().fatpApacDiaria(); //-- cfv(01/11/2001)
		//--
		//-- Alterado para gerar journal pmr, quando rotina de apac, somente nas prévias
		//-- de final de semana e no encerramento. Milena agosto/2003
		//UserTransaction userTransaction = obterUserTransaction(null);
		String inicio = "..............Inicio FATURAMENTO AMBULATORIO " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date())+" ..............";
		
		logar(inicio);
		getSchedulerFacade().adicionarLog(job, inicio);
		getFaturamentoFacade().enviaEmailResultadoEncerramentoAmbulatorio(inicio + "  <br /> Executando: " + (pPrevia ? " Prévia " : " Encerramento "));
		//userTransaction = commitUserTransaction(userTransaction); //commita a parte do log
		atribuirContextoSessao(FATK_PMR_RN_V_PMR_JOURNAL,Boolean.TRUE);
		
		LocalDateTime dt = new LocalDateTime(new Date());
		
		if (pPrevia && 
			dt.dayOfWeek().get() != DateTimeConstants.SUNDAY   &&
			dt.dayOfWeek().get() != DateTimeConstants.SATURDAY &&
			dt.dayOfWeek().get() != DateTimeConstants.MONDAY) {
			atribuirContextoSessao(FATK_PMR_RN_V_PMR_JOURNAL, Boolean.FALSE);
		}
		
		if (!pPrevia) {
			pAtualizaParametro(pModulos);
		}
		
		//ALTERAÇÃO REALIZADA PARA MELHORAR A PERFORMANCE DO AGENDAMENTO DO ENCERRAMENTO DO FATURAMENTO DE AMBULATÓRIO
		//SEGUNDO A MILENA A GRAVAÇÃO DA JOURNAL NÃO PRECISA ACONTECER DURANTE O PROCESSAMENTO DO BPA/BPI, 
		atribuirContextoSessao(FATK_PMR_RN_V_PMR_JOURNAL,Boolean.FALSE);
		
		final ObjetosOracleDAO ooDAO = getObjetosOracleDAO();
		if(DominioOpcaoEncerramentoAmbulatorio.TODOS.equals(pModulos)){
			
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_ACO_NEW); // APAP
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_EXM_NEW); // APEX
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_PRE);	 // APRE
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_FOT_NEW); // APAF
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ENC_SISCOLO); // SISCOLO
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_RAD_NEW); // APAR
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_OTO_NEW); // APAT
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ENC_SISMAMA); // SISMAMA
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_APAC_NEF); // APAN
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_APAC_QUI); // APAC
			rnFatpEncAmbNew(pPrevia, pCpeDtFim, job, nomeMicrocomputador, dataFimVinculoServidor);	// AMB
			
		} else {
			switch (pModulos) {
				case APAP: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_ACO_NEW); break;
				case APEX: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_EXM_NEW); break;
				
				// Daniel Castro - 20/05/2010 - Inclusão da APAC de pré-transplante
				case APRE: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_PRE); break;
				
				// liberar dia 02,abril,2002
				// cancelado em jan/2008
				// volta a execução em agosto/2008 - Milena
				case APAF: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_FOT_NEW); break;
				
				// Para Siscolo foi mantida a rotina atual - verificar
				case SISCOLO: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ENC_SISCOLO); break;
				
				// Milena agosto/2002
				case APAR: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_RAD_NEW); break;
				
				// liberar apacs otorrino novembro/2003
				case APAT: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_OTO_NEW); break;
				
				// MARINA 21/09/2009
				case SISMAMA: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ENC_SISMAMA); break;
				
				// Milena jan/2008 separando apac nefro
				case APAN: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_APAC_NEF); break;
				case APAC: executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_AMB_RN_RN_FATP_ENC_APAC_QUI); break;
				case AMB: rnFatpEncAmbNew(pPrevia, pCpeDtFim, job, nomeMicrocomputador, dataFimVinculoServidor); break;
			}
		}
		
		//userTransaction = commitUserTransaction(userTransaction);
		
		final FatCompetenciaDAO competenciaDAO = getFatCompetenciaDAO();
		
		//--
		//-- Milena 09/2007 separação faturamento teto/extra teto
		if (pPrevia) {
			List<FatCompetencia> listaCompetencia = competenciaDAO.obterCompetenciasPorModuloESituacaoEFaturadoEMesEAno(
					DominioModuloCompetencia.AMB, DominioSituacaoCompetencia.A, null, null, false, null, null); //-- procura competência aberta
			
			if (listaCompetencia.isEmpty()) {
				throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTRADA_COMPETENCIA_ABERTA);
			}
			
		} else {
			List<FatCompetencia> listaCompetenciaAnt = competenciaDAO.obterCompetenciasPorModuloESituacaoEFaturadoEMesEAno(
					DominioModuloCompetencia.AMB, DominioSituacaoCompetencia.P, true, null, false, null, null); //-- procura última competência encerrada módulo amb
			
			if (listaCompetenciaAnt.isEmpty()) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00807);
			}
		}
		
		//--   	fatp_calcula_teto_uni(v_comp_ant_inicio, v_comp_ant_ano, v_comp_ant_mes,p_previa);
		//--	rn_pmrp_agrubpa_teto(v_comp_ant_inicio,v_comp_ant_mes, v_comp_ant_ano, p_previa);
		//-- fim Milena 09/2007

		//CÓDIGO COMENTADO ATÉ QUE SEJAM MIGRADOS OS PROCESSAMENTOS DE APAC
		//		if (!pPrevia) {
		//			getFaturamentoFatkPmrRN().rnPmrpAtuApacFut(pPrevia, nomeMicrocomputador, dataFimVinculoServidor);
		//		}
		if(Boolean.FALSE.equals(pPrevia)){
			executeAghProcedure(pPrevia, pCpeDtFim, ooDAO, ObjetosBancoOracleEnum.FATK_PMR_RN_RN_PMRP_ATU_APAC_FUT);
		}
		
		//-- Execucao OK de todo encerramento
		//userTransaction = commitUserTransaction(userTransaction);
		logar("Execução dos processos de encerramento APACs e BPA OK !, fat_log_enc_previa.txt");
		String logFim =  "..............Finalizou FATURAMENTO AMBULATORIO " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " ..............";
		getSchedulerFacade().adicionarLog(job, logFim);
		//userTransaction = commitUserTransaction(userTransaction); //commita a parte do log
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void executeAghProcedure( final Boolean pPrevia,
									  final Date pCpeDtFim,
									  final ObjetosOracleDAO ooDAO, ObjetosBancoOracleEnum enumPrc 
									  ) throws ApplicationBusinessException {
		
		if(ooDAO.isOracle() && isHCPA()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			logar("Inciando em {0}, processamento de '{1}' para data '{2}', prévia '{3}' ", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()), enumPrc.toString(), pCpeDtFim, pPrevia);
			ooDAO.executaProceduresRNFatpExecFatNew(enumPrc, pCpeDtFim, pPrevia, servidorLogado);
			logar("Finalizado em {0}, processamento de '{1}' para data '{2}', prévia '{3}' ", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()), enumPrc.toString(), pCpeDtFim, pPrevia);
		}
		//userTransaction = commitUserTransaction(userTransaction);
	}

	/**
	 * OBS.: Implementado parcialmente, faz parte da Procedure RN_FATP_EXEC_FAT_NEW 
	 * 
	 * ORADB: PROCEDURE: ATUALIZA_PARAMETRO
	 * @param pOpcao
	 *  
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void pAtualizaParametro(DominioOpcaoEncerramentoAmbulatorio pOpcao) throws ApplicationBusinessException {
		final IParametroFacade parametroFacade = getParametroFacade();
		
		// Milena - setembro/2004

		//--	Milena jan/2008
		if(DominioOpcaoEncerramentoAmbulatorio.TODOS.equals(pOpcao)){
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_ACO, AghuParametrosEnum.P_DT_FIM_COMP_ACO);	// APAP
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APE, AghuParametrosEnum.P_DT_FIM_COMP_APE);	// APEX
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_PRE, AghuParametrosEnum.P_DT_FIM_COMP_PRE);	// APRE
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APF, AghuParametrosEnum.P_DT_FIM_COMP_APF);	// APAF
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APR, AghuParametrosEnum.P_DT_FIM_COMP_APR);	// APAR
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APT, AghuParametrosEnum.P_DT_FIM_COMP_APT);	// APAT
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APN, AghuParametrosEnum.P_DT_FIM_COMP_APN); 	// APAN
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APAC, AghuParametrosEnum.P_DT_FIM_COMP_APO); 	// APAC
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_AMB, AghuParametrosEnum.P_DT_FIM_COMP_AMB); 	// AMB
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_SIS, AghuParametrosEnum.P_DT_FIM_COMP_SIS); 	// SISCOLO
			atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_MAMA, AghuParametrosEnum.P_DT_FIM_COMP_MAMA); 	// SISMAMA
			
		} else {
			switch (pOpcao) {
				case APAP: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_ACO, AghuParametrosEnum.P_DT_FIM_COMP_ACO); break;
				case APEX: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APE, AghuParametrosEnum.P_DT_FIM_COMP_APE); break;
				
				// Daniel Castro - 20/05/2010 - Inclusão da APAC de pré-transplante
				case APRE: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_PRE, AghuParametrosEnum.P_DT_FIM_COMP_PRE); break;
				case APAF: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APF, AghuParametrosEnum.P_DT_FIM_COMP_APF); break;
				case APAR: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APR, AghuParametrosEnum.P_DT_FIM_COMP_APR); break;
				case APAT: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APT, AghuParametrosEnum.P_DT_FIM_COMP_APT); break;
				
				// Milena 15/12/2007 - Separando a Nefro para janeiro/2008
				case APAN: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APN, AghuParametrosEnum.P_DT_FIM_COMP_APN); break;
				case APAC: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_APAC, AghuParametrosEnum.P_DT_FIM_COMP_APO); break;
				case AMB: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_AMB, AghuParametrosEnum.P_DT_FIM_COMP_AMB); break;
				case SISCOLO: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_SIS, AghuParametrosEnum.P_DT_FIM_COMP_SIS); break;

				// MARINA 21/09/2009
				case SISMAMA: atualizarParametro(parametroFacade, AghuParametrosEnum.P_DT_PARA_MAMA, AghuParametrosEnum.P_DT_FIM_COMP_MAMA); break;
				// FIM MARINA
			}
		}
		
	}
	
	private void atualizarParametro( final IParametroFacade parametroFacade, 
									 final AghuParametrosEnum enumPrmOrigem, 
									 final AghuParametrosEnum enumPrmDestino) throws ApplicationBusinessException{
		
		final AghParametros prmOrigem = parametroFacade.buscarAghParametro(enumPrmOrigem);
		
		final AghParametros prmDestino = parametroFacade.buscarAghParametro(enumPrmDestino);
		prmDestino.setVlrData(prmOrigem.getVlrData());
		prmDestino.setVlrNumerico(prmOrigem.getVlrNumerico());
		prmDestino.setVlrTexto(prmOrigem.getVlrTexto());
		prmDestino.setTipoDado(prmOrigem.getTipoDado());
		parametroFacade.setAghpParametro(prmDestino);
	}
	
	
	/**
	 * ORADB PROCEDURE FATP_CARGA_COLETA_RD
	 * 
	 * @param pCpeDtFim
	 * @param dataFimVinculoServidor 
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	void fatpCargaColetaRD(Date pCpeDtFim, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		Date vDtHrInicio;
		Date vDtHrFim;
		
		final Short QTDE_UM = Short.valueOf("1");
		
		AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(buscarVlrInteiroAghParametro(AghuParametrosEnum.P_UNF_BANCO_SANGUE).shortValue()); //41 ---UNF_SEQ = BANCO DE SANGUE/HEMATOLOGIA
		
		//-- busca a competência aberta de faturamento de ambulatório
		List<FatCompetencia> listaCompetencias = getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);

		if (listaCompetencias.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoFatkAmbRNExceptionCode.MSG_NAO_ENCONTROU_COMPETENCIA);
		}
		
		FatCompetencia competencia = listaCompetencias.get(0);
		vDtHrInicio = competencia.getId().getDtHrInicio();
		if (pCpeDtFim != null) {
			vDtHrFim = pCpeDtFim;
		} else {
			vDtHrFim = DateUtil.adicionaDias(
					// Marina 13/02/2013 -- retirado o trunc para ficar coerente com a ael
					   DateUtil.obterDataFimCompetencia(
					   DateUtil.obterData(competencia.getId().getAno(), competencia.getId().getMes(), 01)
					   								   ), 1);
		}
		
		// Milena FEV/2008
		Short vSerVin = buscarVlrShortAghParametro(AghuParametrosEnum.P_VIN_CHEFE_HEMATO);
		Integer vSerMatr = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_MATR_CHEFE_HEMATO);
		
		logar(INICIO_0_, vDtHrInicio);
		logar(FINAL_0_, vDtHrFim);
		
		excluirProcedimentoAmbMes(nomeMicrocomputador, dataFimVinculoServidor,
				vDtHrInicio, vDtHrFim);
		
		//userTransaction = commitUserTransaction(userTransaction);
		
		incluirAtendPacExternPorColetasRealizadas(nomeMicrocomputador,
				dataFimVinculoServidor, vDtHrInicio, vDtHrFim, QTDE_UM,
				unidadeFuncional, vSerVin, vSerMatr);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void incluirAtendPacExternPorColetasRealizadas(
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			Date vDtHrInicio, Date vDtHrFim, final Short QTDE_UM,
			AghUnidadesFuncionais unidadeFuncional, Short vSerVin,
			Integer vSerMatr) throws ApplicationBusinessException,
			BaseException {
		//Inclusão
		RapServidores servidorResp = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(vSerMatr, vSerVin));
		FatConvenioSaudePlano convenioSaudePlano = getFatConvenioSaudePlanoDAO().
		obterPorChavePrimaria(new FatConvenioSaudePlanoId(
				buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1---CSP_CNV_CODIGO = SUS
				buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO) //2---CSP_SEQ= SUS AMBULATORIO,
			));
		
		Integer codSangue = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_SANGUE); //67
		Integer codHemocentro = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_AGHU_HEMOCENTRO_COLETA_REDOME); //21
		String situacaoCancelado = buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO); //CA
		
		List<AtendPacExternPorColetasRealizadasVO> listaPacExternPorColetasRealizadasVO = 
			this.getExamesFacade().listarAtendPacExternPorColetasRealizadas(vDtHrInicio, vDtHrFim, codSangue, codHemocentro, situacaoCancelado);
		

		Integer pPhiColetaSangueHistocDoador = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_COLETA_SANGUE_HISTOC_DOADOR);
		Integer pCidDoadorMedulaOssea        = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CID_DOADOR_MEDULA_OSSEA);
		
		for (AtendPacExternPorColetasRealizadasVO atdPacExtColetaRealizadaVO : listaPacExternPorColetasRealizadasVO) {
			AtendimentoCargaColetaVO atendimentoVO = 
				this.getExamesFacade().listarAtendimentoParaCargaColetaRD(atdPacExtColetaRealizadaVO.getSeq(), Boolean.FALSE);
			
			if(atendimentoVO == null) {
				//c_atendimento_div
				atendimentoVO = 
					this.getExamesFacade().listarAtendimentoParaCargaColetaRD(atdPacExtColetaRealizadaVO.getSeq(), Boolean.TRUE);
			}
			
			inserirRegistro(atdPacExtColetaRealizadaVO.getDthrProgramada(),
					DominioSituacaoProcedimentoAmbulatorio.ABERTO,
					DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
					QTDE_UM,
					atdPacExtColetaRealizadaVO.getSeq(),
					atdPacExtColetaRealizadaVO.getSeqp(),
					pPhiColetaSangueHistocDoador,//14452
					atdPacExtColetaRealizadaVO.getMatricula(),
					atdPacExtColetaRealizadaVO.getVinCodigo() == null ? null : atdPacExtColetaRealizadaVO.getVinCodigo().shortValue(),
					unidadeFuncional,
					atendimentoVO.getEspSeq(),
					atendimentoVO.getPacCodigo(),
					convenioSaudePlano,
					DominioOrigemProcedimentoAmbulatorialRealizado.EXA,
					atendimentoVO.getAtdSeq(),
					pCidDoadorMedulaOssea,//13865
					servidorResp, 
					nomeMicrocomputador,
					dataFimVinculoServidor
			);
		}
		
		//commitUserTransaction(userTransaction);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void excluirProcedimentoAmbMes(String nomeMicrocomputador,
			final Date dataFimVinculoServidor, Date vDtHrInicio, Date vDtHrFim)
			throws ApplicationBusinessException, BaseException {
		/*--  *-------------------------------------------------------*
		  --  *  DELETA REGISTROS INCLUIDOS NO MES PARA O CASO DE     *
		  --  *  REPROCESSAMENTO                                     *
		  --  *-------------------------------------------------------**/
				
		List<FatProcedAmbRealizado> listaProcedAmbRealizado = getFatProcedAmbRealizadoDAO()
				.listarRegistrosMesProcessamento(
						new DominioSituacaoProcedimentoAmbulatorio[] {
								DominioSituacaoProcedimentoAmbulatorio.ABERTO,
								DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO },
						new DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] {
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
								DominioLocalCobrancaProcedimentoAmbulatorialRealizado.A },
						buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO), //2
						buscarVlrShortAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO), //1
						DominioOrigemProcedimentoAmbulatorialRealizado.EXA,
						buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_COLETA_SANGUE_HISTOC_DOADOR),//14452
						vDtHrInicio, vDtHrFim);
		
		//UserTransaction userTransaction = obterUserTransaction(null);

		// Marina 20/06/2011
		for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {
			getProcedimentosAmbRealizadosON().excluirProcedimentoAmbulatorialRealizado(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
		}
	}

	/**
	 * ORADB FATK_PMR_RN.RN_PMRP_TRC_ESP_ATD
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @throws BaseException 
	 */
	public void rnPmrpTrcEspAtd(Integer atdSeq, Short espSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		FatProcedAmbRealizadoDAO procedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		List<FatProcedAmbRealizado> listaFatProcedAmbRealizado = procedAmbRealizadoDAO.listarFatProcedAmbRealizadoPorAtdSeq(atdSeq);
		
		if (!listaFatProcedAmbRealizado.isEmpty()) {
			AghEspecialidades especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeq);
			IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
			List<FatProcedAmbRealizado> listaFatProcedAmbRealizadoUpdate = procedAmbRealizadoDAO.listarFatProcedAmbRealizadoPorAtdSeqSemPrhConNumero(atdSeq);

			for (FatProcedAmbRealizado procedAmbRealizado : listaFatProcedAmbRealizadoUpdate) {
				FatProcedAmbRealizado procedAmbRealizadoOld = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);
				procedAmbRealizado.setEspecialidade(especialidade);
				
				faturamentoFacade.persistirProcedimentoAmbulatorialRealizado(procedAmbRealizado, procedAmbRealizadoOld, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
	}
	
	//TODO usuarioLogado
	public void extornaCompetenciaAmbulatorio(String usuarioLogado) throws ApplicationBusinessException{
		final FatArqEspelhoProcedAmbDAO faepDao = getFatArqEspelhoProcedAmbDAO();
		
		final FatCompetenciaDAO compDao = getFatCompetenciaDAO();
		final Order[] ordens = {Order.desc(FatCompetencia.Fields.ANO.toString()),Order.desc(FatCompetencia.Fields.MES.toString())};
		final List<FatCompetencia> competencias = compDao.
				obterCompetenciasPorModuloESituacoes(DominioModuloCompetencia.AMB, ordens, DominioSituacaoCompetencia.M, DominioSituacaoCompetencia.P);

		
		if(!competencias.isEmpty()){
			FatCompetencia comp = competencias.get(0);
			final FatCompetencia compAtual = compDao.buscarCompetenciasDataHoraFimNula(DominioModuloCompetencia.AMB, DominioSituacaoCompetencia.A);

			// SQL para voltar a competencia dos procedimentos que foram movidos para a nova competencia:	
			String sql = "update AGH."+FatProcedAmbRealizado.class.getAnnotation(Table.class).name()
					+ " set "+FatProcedAmbRealizado.Fields.CPE_MES.name()+" = :PRM_MES, "
							+ FatProcedAmbRealizado.Fields.CPE_ANO.name()+" = :PRM_ANO, "
							+ FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.name()+" = :PRM_DT_INICIO"
					+ " where 1=1"
					+ AND+FatProcedAmbRealizado.Fields.CPE_MODULO.name()+" = :PRM_MODULO "
					+ AND+FatProcedAmbRealizado.Fields.CPE_MES.name()+"= :PRM_MES_ATUAL"
					+ AND+FatProcedAmbRealizado.Fields.CPE_ANO.name()+" =  :PRM_ANO_ATUAL";
			
			faepDao.executaSQLVoltaCompetenciaProcedimentos(sql, comp.getId().getMes(),
															comp.getId().getAno(), comp.getId().getDtHrInicio(),
															comp.getId().getModulo(), compAtual.getId().getMes(), compAtual.getId().getAno());
			
			String sqlDeleteFatEspelhoProcedAmb = "delete from AGH."+FatEspelhoProcedAmb.class.getAnnotation(Table.class).name()
													+ " where 1=1"
													+ AND+FatEspelhoProcedAmb.Fields.CPE_MODULO.name()+" = :PRM_MODULO "
													+ AND+FatEspelhoProcedAmb.Fields.CPE_MES.name()+"= :PRM_MES_ATUAL"
													+ AND+FatEspelhoProcedAmb.Fields.CPE_ANO.name()+" =  :PRM_ANO_ATUAL"
													+ AND+FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.name()+" =  :PRM_DT_HR_INICIO_ATUAL";
			
			faepDao.executaSQLVoltaCompetenciaProcedimentosDeletes(sqlDeleteFatEspelhoProcedAmb, compAtual.getId().getModulo(), 
					compAtual.getId().getMes(), compAtual.getId().getAno(), compAtual.getId().getDtHrInicio());

			String sqlDeleteFatArqEspelhoProcedAmb = "delete from AGH."+FatArqEspelhoProcedAmb.class.getAnnotation(Table.class).name()
													+ " where 1=1"
													+ AND+FatArqEspelhoProcedAmb.Fields.CPE_MODULO.name()+" = :PRM_MODULO "
													+ AND+FatArqEspelhoProcedAmb.Fields.CPE_MES.name()+"= :PRM_MES_ATUAL"
													+ AND+FatArqEspelhoProcedAmb.Fields.CPE_ANO.name()+" =  :PRM_ANO_ATUAL"
													+ AND+FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.name()+" =  :PRM_DT_HR_INICIO_ATUAL";

			faepDao.executaSQLVoltaCompetenciaProcedimentosDeletes(sqlDeleteFatArqEspelhoProcedAmb, compAtual.getId().getModulo(), 
					compAtual.getId().getMes(), compAtual.getId().getAno(), compAtual.getId().getDtHrInicio());

			FatCompetenciaRN compRN = getFatCompetenciaRN();
			Integer mes = compAtual.getId().getMes();
			Integer ano = compAtual.getId().getAno();
			
			if(mes == 1){
				--ano;
				mes = 12;
			} else {
				--mes;
			}
			// Reabre a competencia anterior
			comp = compDao.obterCompetenciaModuloMesAno(DominioModuloCompetencia.AMB, mes, ano);
			comp.setDtHrFim(null);
			comp.setDthrEncerraEstatistica(null);
			comp.setIndFaturado(Boolean.FALSE);
			comp.setIndSituacao(DominioSituacaoCompetencia.A);
			compRN.atualizarFatCompetencia(comp);
			
			String sqlUpdate = "update AGH."+FatProcedAmbRealizado.class.getAnnotation(Table.class).name()+
						       " set "+FatProcedAmbRealizado.Fields.IND_SITUACAO.name()+" = :PRM_IND_SITUACAO " +
							   " where 1=1 " +
							    AND+FatProcedAmbRealizado.Fields.IND_SITUACAO.name()+" IN (:PRM_IND_SITUACAO_LIST)"+
							    AND+FatProcedAmbRealizado.Fields.CPE_MES.name()+" = :PRM_MES"+
							    AND+FatProcedAmbRealizado.Fields.CPE_ANO.name()+" = :PRM_ANO"+
							    AND+FatProcedAmbRealizado.Fields.CPE_MODULO.name()+" = :PRM_MODULO";
			
			faepDao.executaSQLVoltaSituacaoProcedimentos(sqlUpdate, mes, ano, DominioSituacaoProcedimentoAmbulatorio.ABERTO.getCodigo(), 
														 DominioModuloCompetencia.AMB.toString(), 
														 DominioSituacaoProcedimentoAmbulatorio.ENCERRADO.getCodigo(),
														 DominioSituacaoProcedimentoAmbulatorio.APRESENTADO.getCodigo());

			// Exclui nova competencia criada	
			compRN.excluirFatCompetencia(compAtual);
		}
	}

	protected FatCompetenciaRN getFatCompetenciaRN() {
		return fatCompetenciaRN;
	}
	
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	protected IBancoDeSangueFacade getBancoSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected FatEspelhoProcedAmbDAO getFatEspelhoProcedAmbDAO() {
		return fatEspelhoProcedAmbDAO;
	}
	
	protected FatArqEspelhoProcedAmbDAO getFatArqEspelhoProcedAmbDAO() {
		return fatArqEspelhoProcedAmbDAO;
	}
	
	protected FaturamentoFatkPmrRN getFaturamentoFatkPmrRN() {
		return faturamentoFatkPmrRN;
	}
	
	protected FaturamentoFatkCpeRN getFaturamentoFatkCpeRN() {
		return faturamentoFatkCpeRN;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}
	
	protected FaturamentoAtualizaExamesPreTransplantesRN getFaturamentoAtualizaExamesPreTransplantesRN() {
		return faturamentoAtualizaExamesPreTransplantesRN;
	}
	
	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
