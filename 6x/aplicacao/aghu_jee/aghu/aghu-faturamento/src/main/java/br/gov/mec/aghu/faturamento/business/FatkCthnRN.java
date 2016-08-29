package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioFideps;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAutorizadoCmaDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorDiariaInternacaoDAO;
import br.gov.mec.aghu.faturamento.vo.CursorCAutorizadoCMSVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoItemContaHospVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuGeraAihVO;
import br.gov.mec.aghu.faturamento.vo.RnIphcVerExcPercVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAtoMedicoAihId;
import br.gov.mec.aghu.model.FatAutorizadoCma;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAihId;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.model.FatValorContaHospitalar;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Refere-se a antiga package FATK_CTH3_RN atual FATK_CTHN_RN_UN
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.HierarquiaONRNIncorreta","PMD.NcssTypeCount"})
@Stateless
public class FatkCthnRN extends AbstractFatDebugLogEnableRN {

	private static final String MSG_EXCECAO_IGNORADA = "A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ";

	private static final String _MAIOR_ = " > ";

	private static final String RN_CTHC_ATU_GERA_AIH = "RN_CTHC_ATU_GERA_AIH";

	private static final String QUANTIDADE_NAO_PODE_SER_MAIOR_QUE_DIAS_DA_CONTA = "QUANTIDADE NAO PODE SER MAIOR QUE DIAS DA CONTA: ";

	private static final String INT = "INT";

	private static final String RN_CTHC_ATU_INS_AAM = "RN_CTHC_ATU_INS_AAM";

	@EJB
	private FatkCthRN fatkCthRN;
	
	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;
	
	@EJB
	private FaturamentoInternacaoRN faturamentoInternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(FatkCthnRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 6718843682273274790L;

	private static final int CONST_201201 = 201201;
	

	/**
	 * ORADB Cursor FATK_CTHN_RN_UN.C_ITEM_REALIZ
	 * 
	 * @throws BaseException
	 */
	protected List<FatEspelhoItemContaHospVO> cItemRealiz(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia){
		return getFatEspelhoItemContaHospDAO().cItemRealiz(pCth, pTipo, pPrevia);
	}
	
	/**
	 * ORADB Cursor FATK_CTHN_RN_UN.C_ITEM_AGRUP
	 * 
	 * @throws BaseException
	 */
	protected List<FatEspelhoItemContaHospVO> cItemAgrup(final Integer pCth, final DominioTipoItemEspelhoContaHospitalar pTipo,
//														final Byte pUtie, final Byte pRn, final Byte pConsPed, 
//														final Byte pHemat, final Byte pOpm, final Byte pSalaTransp,
//														final Byte pSadtTransp, final Byte pCirurTransp,
														final Boolean pPrevia){
		
		return getFatEspelhoItemContaHospDAO().cItemAgrup(pCth, pTipo, pPrevia);
	}
	
	
	/**
	 * ORADB Cursor FATK_CTHN_RN_UN.C_ITEM_AGRUP_POR_PROC
	 * 
	 * @throws BaseException
	 */
	protected List<FatEspelhoItemContaHospVO> cItemAgrupPorProc(final Integer pCth, final DominioTipoItemEspelhoContaHospitalar pTipo,
														final Boolean pPrevia){
		
		return getFatEspelhoItemContaHospDAO().cItemAgrupPorProc(pCth, pTipo, pPrevia);
	}
	
	
	/**
	 * ORADB Cursor FATK_CTHN_RN_UN.C_ITEM_AGRUP_POR_PROC_COMP
	 * 
	 * @throws BaseException
	 */
	protected List<FatEspelhoItemContaHospVO> cItemAgrupPorProcComp(final Integer pCth, final DominioTipoItemEspelhoContaHospitalar pTipo,
														final Long procHospSus, final Byte tivSeq, final Byte taoSeq,
														final Short iphPhoSeq, final Integer iphSeq, 
														final Boolean pPrevia){
		
		return getFatEspelhoItemContaHospDAO().cItemAgrupPorProcComp(pCth, pTipo, pPrevia, procHospSus, tivSeq, taoSeq, iphPhoSeq, iphSeq);
	}
		

	/**
	 * ORADB Procedure FATK_CTHN_RN_UN.RN_CTHC_ATU_GERA_AIH
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public Boolean rnCthcAtuGeraAih( Integer pCthSeq, 	Integer pDiasConta,
									 	Boolean pPrevia,	Date pDataPrevia,
									 	Integer pPacCodigo,	Integer pPacProntuario,
									 	Integer pIntSeq,	Integer pPhiSolic,
									 	Short pPhoSolic,	Integer pIphSolic,
									 	Long pCodSusSolic,	Integer pPhiRealiz,
									 	Short pPhoRealiz,	Integer pIphRealiz, 
									 	Long pCodSusRealiz, String nomeMicrocomputador,
									 	final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO = getFatCampoMedicoAuditAihDAO();
		final FatAtoMedicoAihDAO fatAtoMedicoAihDAO = getFatAtoMedicoAihDAO();
		final FatAutorizadoCmaDAO fatAutorizadoCmaDAO = getFatAutorizadoCmaDAO();
		final VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN = getVerificacaoItemProcedimentoHospitalarRN();
		final FatkCthRN fatkCthRN = getFatkCthRN();
		final Byte atoCirur = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO);
		final Byte atoAnes = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);
		final Byte pTaoSeq = Byte.valueOf("0");
		
		//-- Ney 31/08/2011
//		Integer vMaxDiasLancados = pDiasConta;
//		Integer vMaxQtdLancados = 0;  
		
		// "inicializa variaveis globais"
		RnCthcAtuGeraAihVO rnCthcAtuGeraAihVO = new RnCthcAtuGeraAihVO();
		
		// busca numero maximo de AIHs de continuacao (tipo '3') p/considerar a primeira AIH (tipo '1')
		// Ney 31/08/2011 Atenção, alterei para passar
		rnCthcAtuGeraAihVO.setMaxEai(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_MAX_AIH_CONTINUACAO).add(BigDecimal.ONE).intValue());
 
		// Busca quantidade maxima de procedimentos no CMA por espelho de AIH
		final Integer vMaxCah = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_MAX_PROCED_CMA_AIH).intValue();
		
		// Busca quantidade maxima de atos medicos por página de espelho de AIH
		rnCthcAtuGeraAihVO.setMaxAam(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_MAX_ATO_MEDICO_AIH).intValue());
		
		// Busca Código do tipo do Ato de DIARIA DE UTI ESPECIALIZADA
		rnCthcAtuGeraAihVO.setCodAtoUtie(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_UTIE).byteValue());

		// Busca Código do tipo do Ato de ATENDIMENTO AO RN EM SALA DE PARTO
		rnCthcAtuGeraAihVO.setCodAtoRn(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_RN).byteValue());

		// Busca Código do tipo do Ato de 1a. CONSULTA PEDIATRIA
		rnCthcAtuGeraAihVO.setCodAtoConsPed(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_CONS_PED).byteValue());

		// Busca Código do tipo do Ato de HEMOTERAPIA
		rnCthcAtuGeraAihVO.setCodAtoHemat(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_HEMAT).byteValue());

		// Busca Código do tipo do Ato de OPM (Ortese/Protese)
		rnCthcAtuGeraAihVO.setCodAtoOpm(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_OPM).byteValue());

		// Busca Código do tipo do Ato de TAXA DE SALA DE TRANSPLANTE
		rnCthcAtuGeraAihVO.setCodAtoSalaTransp(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_SALA_TRANSP).byteValue());

		// Busca Código do tipo do Ato de SADT TRANSPLANTE
		rnCthcAtuGeraAihVO.setCodAtoSadtTransp(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_SADT_TRANSP).byteValue());

		// Busca Código do tipo do Ato de CIRURGIAO TRANSPLANTE
		rnCthcAtuGeraAihVO.setCodAtoCirurTransp(buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_ATO_CIRUR_TRANSP).byteValue());

		// verifica qtde maxima p/campos de qtd e diarias
		final Long vMaxQtd = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_MAX_QTD_CAMPO_QTD_INT).longValue();
		
		// Representa o codigo do grupo de Procedimentos Cirurgicos. Referencia o campo seq da tabela Fat_Grupos.
		final Integer codGrupoProcCirurgicos = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_AGHU_COD_GRUPO_PROC_CIRURGICOS).intValue();
		
		Long vCodSusCma = null;
		
		Boolean vCmaNaoAutorizado = Boolean.FALSE;
		
		Integer vPrimeiroEaiSeqp = null;
		
		Byte vCahSeqp = null;

		Byte cahVlrSeqp = null;
		
		BigDecimal vlrCalcCma = null;
		
		BigDecimal vVlrShRealiz = null;
		BigDecimal vVlrSpRealiz = null;
		BigDecimal vVlrSadtRealiz = null;
		BigDecimal vVlrAnestRealiz = null;
		BigDecimal vVlrProcedRealiz = null;
				
		// Ney, 20111229 Var de packpage qtd maxima permitida, para permitir duplicidade qdo max permitida > 1
		Long vMaxQtdePermitida = Long.valueOf("0");
		
		Boolean atingiuLimiteCma = Boolean.FALSE;
		
		// busca dados da CONTA hospitalar
		final FatContasHospitalares regConta = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
		if(regConta != null && CoreUtil.in(regConta.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)){

			// busca dados do espelho aih
			FatEspelhoAih regEspelho = fatEspelhoAihDAO.buscarPrimeiroPorCthOrderBySeqp(pCthSeq);	
			
			if(regEspelho != null){
				rnCthcAtuGeraAihVO.setRegEspelho(regEspelho);
//				faturamentoFacade.evict(regEspelho);
				this.flush();
				rnCthcAtuGeraAihVO.setEaiSeqp(rnCthcAtuGeraAihVO.getRegEspelho().getId().getSeqp());
				vPrimeiroEaiSeqp = rnCthcAtuGeraAihVO.getRegEspelho().getId().getSeqp();				
				logar("primeiro espelho: {0}", rnCthcAtuGeraAihVO.getEaiSeqp());
				
				FatItensProcedHospitalar fatItensProcedHospitalarRealizado = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnCthcAtuGeraAihVO.getRegEspelho().getIphPhoSeqRealiz(),rnCthcAtuGeraAihVO.getRegEspelho().getIphSeqRealiz()));
				Boolean vPsiquiatria = Boolean.FALSE;
				DominioFideps vFidepsRealiz = DominioFideps.N;
				if(fatItensProcedHospitalarRealizado != null){
					//verifica se o procedimento realizado e' de tratamento psiquiatria
					vPsiquiatria = nvl(fatItensProcedHospitalarRealizado.getPsiquiatria(), Boolean.FALSE);
					
					// verifica se o procedimento realizado e' FIDEPS
					vFidepsRealiz = (DominioFideps) CoreUtil.nvl(fatItensProcedHospitalarRealizado.getFideps(), DominioFideps.N);
				}
				
				final String ufSede = buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
				
				BigDecimal vPercFideps = rnCthcAtuGeraAihVO.getPercFideps();
				
				// fideps  ou  fideps so' p/paciente de fora do estado
				if(DominioFideps.S.equals(vFidepsRealiz) || 
						//  OR ( (v_fideps_realiz = 'F') AND (NVL(reg_espelho.end_uf_pac,'RS')<>'RS') ) THEN
						(DominioFideps.F.equals(vFidepsRealiz) &&
								!CoreUtil.igual(
												(CoreUtil.nvl(rnCthcAtuGeraAihVO.getRegEspelho().getEndUfPac(), ufSede)), ufSede
											   )
//						eSchweigert, alterado em 12/09/2012 #20681											   
//						  (rnCthcAtuGeraAihVO.getRegEspelho().getEndUfPac() == null || 
//								  CoreUtil.igual(ufSede,rnCthcAtuGeraAihVO.getRegEspelho().getEndUfPac())
//						  )
					    )
				   ){
					// Busca percentual a adicionar para FIDEPS			
					vPercFideps = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_PERCENT_FIDEPS);
					vPercFideps = vPercFideps.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);							        
				} else {
					vPercFideps = BigDecimal.ONE;					
				}
				rnCthcAtuGeraAihVO.setPercFideps(vPercFideps);
				logar("v_perc_fideps: {0}", rnCthcAtuGeraAihVO.getPercFideps());
				
				// pega os valores do realizado calculados no encerramento:
				rnCthcAtuGeraAihVO.getRegEspelho().setValorShRealiz(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegEspelho().getValorShRealiz(), vPercFideps));
				rnCthcAtuGeraAihVO.getRegEspelho().setValorSpRealiz(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegEspelho().getValorSpRealiz(), vPercFideps));
				rnCthcAtuGeraAihVO.getRegEspelho().setValorSadtRealiz(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegEspelho().getValorSadtRealiz(), vPercFideps));
				rnCthcAtuGeraAihVO.getRegEspelho().setValorAnestRealiz(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegEspelho().getValorAnestRealiz(), vPercFideps));
				rnCthcAtuGeraAihVO.getRegEspelho().setValorProcedRealiz(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegEspelho().getValorProcedRealiz(), vPercFideps));

				vVlrShRealiz 	 = rnCthcAtuGeraAihVO.getRegEspelho().getValorShRealiz();
				vVlrSpRealiz 	 = rnCthcAtuGeraAihVO.getRegEspelho().getValorSpRealiz();
				vVlrSadtRealiz   = rnCthcAtuGeraAihVO.getRegEspelho().getValorSadtRealiz();
				vVlrAnestRealiz  = rnCthcAtuGeraAihVO.getRegEspelho().getValorAnestRealiz();
				vVlrProcedRealiz = rnCthcAtuGeraAihVO.getRegEspelho().getValorProcedRealiz();
				
				faturamentoFacade.atualizarFatEspelhoAih(rnCthcAtuGeraAihVO.getRegEspelho());
				
				// verifica percentual a cobrar do valor SH
				// em funcao da posicao do procedim no cma				
				Short[] percent = new Short[]{100, 100, 100, 100, 100};
				RnIphcVerExcPercVO rnIphcVerExcPercVO = getVerificacaoItemProcedimentoHospitalarRN().obterExcecoesPercentuais(rnCthcAtuGeraAihVO.getRegEspelho().getIphPhoSeqRealiz(), rnCthcAtuGeraAihVO.getRegEspelho().getIphSeqRealiz());
				if(rnIphcVerExcPercVO != null){
					for (byte i = 0; i < percent.length; i++) {
						percent[i] = rnIphcVerExcPercVO.getPercentual(i);
					}
				}
				
				//verifica se proced realizado (do espelho AIH)
				//permite cobranca de proceds especiais
				Boolean vRealizCobraEspecial = nvl(fatItensProcedHospitalarRealizado.getCobraProcedimentoEspecial(), Boolean.FALSE);
				logar("v_cobr_esp = N{0} IPH: {1}", fatItensProcedHospitalarRealizado.getId().getPhoSeq(), fatItensProcedHospitalarRealizado.getId().getSeq());
		      
				// busca proced realizado nos itens do espelho:
				final List<FatEspelhoItemContaHospVO> listaRegItemRealiz = cItemRealiz(pCthSeq, DominioTipoItemEspelhoContaHospitalar.R, pPrevia);
		      
				if(listaRegItemRealiz != null && !listaRegItemRealiz.isEmpty()){
		    	  for(FatEspelhoItemContaHospVO regItemRealiz : listaRegItemRealiz) {
						
				      // verifica quantidade maxima permitida para cobranca na conta
				      logar("1 MAX DA CONTA - PHO {0} IPH: {1}", regItemRealiz.getIphPhoSeq(), regItemRealiz.getIphSeq());
				      
				      Long vMaxCta = this.getFatVlrItemProcedHospCompsDAO().obterQtdMaximaExecucaoPorPhoIphParaCompetencia(regItemRealiz.getIphPhoSeq(), regItemRealiz.getIphSeq(), regConta.getDataInternacaoAdministrativa());
				      logar("MAX DA CONTA - v_max_cta  {0} QTDE REALIZ: {1}", vMaxCta, regItemRealiz.getQuantidade());
				      
				      if (vMaxCta == 0) {
				    	  vMaxCta = regItemRealiz.getQuantidade();
				      }
				      
				      if(CoreUtil.maior(regItemRealiz.getQuantidade(), vMaxCta)){
				    	  regItemRealiz.setValorAnestesista(correcaoValorQtd(regItemRealiz.getValorAnestesista(), regItemRealiz.getQuantidade(), vMaxCta));
				          regItemRealiz.setValorProcedimento(correcaoValorQtd(regItemRealiz.getValorProcedimento(), regItemRealiz.getQuantidade(), vMaxCta));
				          regItemRealiz.setValorSadt(correcaoValorQtd(regItemRealiz.getValorSadt(), regItemRealiz.getQuantidade(), vMaxCta));
				          regItemRealiz.setValorServHosp(correcaoValorQtd(regItemRealiz.getValorServHosp(), regItemRealiz.getQuantidade(), vMaxCta));
				          regItemRealiz.setValorServProf(correcaoValorQtd(regItemRealiz.getValorServProf(), regItemRealiz.getQuantidade(), vMaxCta));
				          regItemRealiz.setPontosAnestesista(correcaoValorQtd(regItemRealiz.getPontosAnestesista(), regItemRealiz.getQuantidade(), vMaxCta).longValue());
				          regItemRealiz.setPontosCirurgiao(correcaoValorQtd(regItemRealiz.getPontosCirurgiao(), regItemRealiz.getQuantidade(), vMaxCta).longValue());
				          regItemRealiz.setPontosSadt(correcaoValorQtd(regItemRealiz.getPontosSadt(), regItemRealiz.getQuantidade(), vMaxCta).longValue());
	
				          long vQtdRealiz = regItemRealiz.getQuantidade();
				          long vQtdPerd =  (nvl(regItemRealiz.getQuantidade(), 0) - vMaxCta);
				          
				          // altera a quantidade
				          regItemRealiz.setQuantidade(vMaxCta);
				          logar("NOVA QTD DEVIDO A MAX QTD CONTA : {0}", regItemRealiz.getQuantidade());
				          
				          BigDecimal vVlrShPerd   	= correcaoValorQtd(regItemRealiz.getValorServHosp(), regItemRealiz.getQuantidade(), vQtdPerd);
				          BigDecimal vVlrSpPerd   	= correcaoValorQtd(regItemRealiz.getValorServProf(), regItemRealiz.getQuantidade(), vQtdPerd);
				          BigDecimal vVlrSadtPerd 	= correcaoValorQtd(regItemRealiz.getValorSadt(), regItemRealiz.getQuantidade(), vQtdPerd);
				          BigDecimal vVlrAnestPerd	= correcaoValorQtd(regItemRealiz.getValorAnestesista(), regItemRealiz.getQuantidade(), vQtdPerd);
				          BigDecimal vVlrProcPerd	= correcaoValorQtd(regItemRealiz.getValorProcedimento(), regItemRealiz.getQuantidade(), vQtdPerd);
				          Long vPtAnestPerd 		= correcaoValorQtd(regItemRealiz.getPontosAnestesista(), regItemRealiz.getQuantidade(), vQtdPerd).longValue();
				          Long vPtCirurPerd 		= correcaoValorQtd(regItemRealiz.getPontosCirurgiao(), regItemRealiz.getQuantidade(), vQtdPerd).longValue();
				          Long vPtSadtPerd  		= correcaoValorQtd(regItemRealiz.getPontosSadt(), regItemRealiz.getQuantidade(), vQtdPerd).longValue();
				          
				          getFaturamentoInternacaoRN().rnCthcAtuInsPit( pCthSeq,  regItemRealiz.getIphPhoSeq(), 
						        		  			    regItemRealiz.getIphSeq(), regItemRealiz.getProcedimentoHospitalarSus(), 
						        		  			    vQtdPerd, vQtdRealiz, vVlrShPerd, vVlrSpPerd, vVlrSadtPerd, vVlrProcPerd, 
						        		  			    vVlrAnestPerd, vPtAnestPerd, vPtCirurPerd, vPtSadtPerd);
	
				      }

				      regItemRealiz.setValorAnestesista(correcaoValorQtd(regItemRealiz.getValorAnestesista(), regItemRealiz.getQuantidade(), vPercFideps));
				      regItemRealiz.setValorProcedimento(correcaoValorQtd(regItemRealiz.getValorProcedimento(), regItemRealiz.getQuantidade(), vPercFideps));
				      regItemRealiz.setValorSadt(correcaoValorQtd(regItemRealiz.getValorSadt(), regItemRealiz.getQuantidade(), vPercFideps));		          
				      regItemRealiz.setValorServHosp(correcaoValorQtd(regItemRealiz.getValorServHosp(), regItemRealiz.getQuantidade(), vPercFideps));
				      regItemRealiz.setValorServProf(correcaoValorQtd(regItemRealiz.getValorServProf(), regItemRealiz.getQuantidade(), vPercFideps));
	
				      logar("+++++++++++++++++ testa cma {0}", regItemRealiz.getProcedimentoHospitalarSus());
				      logar("+++++++++++++++++ testa cma t {0} - {1}", regItemRealiz.getIphPhoSeq(), regItemRealiz.getIphSeq());
				      
				      if(!CoreUtil.igual(regItemRealiz.getProcedimentoHospitalarSus(), pCodSusSolic)){
				          
				    	  // verifica se proced realizado deve ser lancado no CMA
				          // nos casos de mudanca de procedimento
				    	  
				    	  //fatk_iph_rn.rn_iphc_ver_preencma( reg_item_realiz.iph_pho_seq ,reg_item_realiz.iph_seq );
				    	  final FatItensProcedHospitalar vRealizPreencheCma = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(regItemRealiz.getIphPhoSeq(), regItemRealiz.getIphSeq()));

				    	  // fatk_iph_rn.rn_iphc_ver_preencma --> ( return nvl(v_preenche,'S') )
				    	  boolean realizPreencheCma = vRealizPreencheCma != null ? vRealizPreencheCma.getPreencheCma().booleanValue() : true;
	
				    	  if(realizPreencheCma){
				    		  // Lancar tantas vezes quanto for a quantidade do item:				    		  
				    		  for (int i = 0; i < regItemRealiz.getQuantidade(); i++) {
					              //------------->LANCA PROCEDIMENTO NO CMA:
				    			  // calcula seq p/ campo medico
				    			  vCahSeqp = fatCampoMedicoAuditAihDAO.buscarProxSeq(pCthSeq,vPrimeiroEaiSeqp);
				    			  
				    			  logar("next cah_seqp: {0}", vCahSeqp);
				    			  logar("cma: {0} max {1}", vCahSeqp, vMaxCah);
				    			  
				    			  if(vCahSeqp != null && CoreUtil.maior(vCahSeqp, vMaxCah)){
				    				  // atingiu a quantidade maxima de campos medicos
				    				  // e deve parar de lancar procedimentos no cma
				    				  // (nao abre nova aih de continuacao por causa do cma)
				    				  logar("cma: Atingiu qtd max {0}", vCahSeqp);
				    				  atingiuLimiteCma = Boolean.TRUE;
				    			  } //(v_cah_seqp is not null) and (v_cah_seqp > v_max_cah)
				    			  
				    			  // ETB 271006 verifica se o valor é > que já existente no CMA
				    			  if(Boolean.TRUE.equals(atingiuLimiteCma)){
				    				  vlrCalcCma = 	nvl(regItemRealiz.getValorAnestesista(), BigDecimal.ZERO)
			    				  			   .add(nvl(regItemRealiz.getValorProcedimento(),BigDecimal.ZERO))
			    				  			   .add(nvl(regItemRealiz.getValorSadt(),	     BigDecimal.ZERO))
			    				  			   .add(nvl(regItemRealiz.getValorServHosp(),	 BigDecimal.ZERO))
			    				  			   .add(nvl(regItemRealiz.getValorServProf(),	 BigDecimal.ZERO));
				    				  
				    				  logar("cma - apos limite vlr: {0}", vlrCalcCma);
				    				  
				    				  Object[] cma = fatCampoMedicoAuditAihDAO.buscarChaMaxVlr(pCthSeq, vPrimeiroEaiSeqp, DominioTipoItemConta.R, vlrCalcCma);				    				  
				    				  if(cma != null){
				    					  cahVlrSeqp = (Byte)cma[0];
				    				  }
				    				  logar("cma - encontrou {0}", cahVlrSeqp);
				    				  
				    				  if(cahVlrSeqp != null){
				    					  fatCampoMedicoAuditAihDAO.removerPorCthSeqp(pCthSeq, cahVlrSeqp);
				    					  this.flush();
				    					  atingiuLimiteCma = Boolean.FALSE;
				    					  logar("cma - deletou {0}", cahVlrSeqp);
				    				  }
				    			  } // if atingiu_limite_cma then
				    			  
				    			  
				    			  // ETB 271006 verifica se o valor é > que já existente no CMA
				    			  if(vCahSeqp != null && Boolean.FALSE.equals(atingiuLimiteCma)){
				    				  logar("vai lancar campo medico auditor: cah_seqp {0} SUS {1} PHO {2} IPH {3} loc soma {4}",
				    						  nvl(cahVlrSeqp,vCahSeqp), 
				    						  regItemRealiz.getProcedimentoHospitalarSus(),
				    						  regItemRealiz.getIphPhoSeq(),
				    						  regItemRealiz.getIphSeq(),
				    						  regItemRealiz.getIndLocalSoma());
				    				  
				    				  FatCampoMedicoAuditAih fatCampoMedicoAuditAih = new FatCampoMedicoAuditAih();
				    				  try{
					    				  fatCampoMedicoAuditAih.setId(new FatCampoMedicoAuditAihId(vPrimeiroEaiSeqp, pCthSeq, nvl(cahVlrSeqp,vCahSeqp)));
					    				  fatCampoMedicoAuditAih.setDataPrevia(pDataPrevia);
					    				  fatCampoMedicoAuditAih.setIndModoCobranca(regItemRealiz.getIndModoCobranca());
					    				  fatCampoMedicoAuditAih.setIphCodSus(regItemRealiz.getProcedimentoHospitalarSus());
					    				  fatCampoMedicoAuditAih.setItemProcedimentoHospilatar(vRealizPreencheCma);
					    				  fatCampoMedicoAuditAih.setValorAnestesista(regItemRealiz.getValorAnestesista());
					    				  fatCampoMedicoAuditAih.setValorProcedimento(regItemRealiz.getValorProcedimento());
					    				  fatCampoMedicoAuditAih.setValorSadt(regItemRealiz.getValorSadt());
					    				  fatCampoMedicoAuditAih.setValorServHosp(regItemRealiz.getValorServHosp());
					    				  fatCampoMedicoAuditAih.setValorServProf(regItemRealiz.getValorServProf());
					    				  fatCampoMedicoAuditAih.setPontosAnestesista(regItemRealiz.getPontosAnestesista().intValue());
					    				  fatCampoMedicoAuditAih.setPontosCirurgiao(regItemRealiz.getPontosCirurgiao().intValue());
					    				  fatCampoMedicoAuditAih.setPontosSadt(regItemRealiz.getPontosSadt().intValue());
					    				  fatCampoMedicoAuditAih.setIndConsistente(DominioTipoItemConta.valueOf(regItemRealiz.getIndLocalSoma().toString()));
					    				  faturamentoFacade.inserirFatCampoMedicoAuditAih(fatCampoMedicoAuditAih, true, nomeMicrocomputador, dataFimVinculoServidor);
					    				 // faturamentoFacade.evict(fatCampoMedicoAuditAih);
				    				  } catch (Exception e) {									
				    					  logar("erro no insert cah (mudanca proced)");
				    					  String msg = e.getMessage();				    					  
				    					  try{
				    						  FatLogError erro = new FatLogError();
				    						  erro.setModulo(INT);
				    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
				    						  erro.setCriadoEm(new Date());
				    						  erro.setCriadoPor(servidorLogado.getUsuario());
				    						  erro.setDataPrevia(pDataPrevia);
				    						  erro.setCthSeq(pCthSeq);
				    						  erro.setPacCodigo(pPacCodigo);
				    						  erro.setProntuario(pPacProntuario);			    			
				    						  erro.setPhiSeq(pPhiSolic);
				    						  erro.setIphPhoSeq(pPhoSolic);
				    						  erro.setIphSeq(pIphSolic);
				    						  erro.setCodItemSusSolicitado(pCodSusSolic);
				    						  erro.setPhiSeqRealizado(pPhiRealiz);
				    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
				    						  erro.setIphSeqRealizado(pIphRealiz);    			
				    						  erro.setCodItemSusRealizado(pCodSusRealiz);
				    						  erro.setIphPhoSeqItem1(regItemRealiz.getIphPhoSeq());
				    						  erro.setIphSeqItem1(regItemRealiz.getIphSeq());
				    						  erro.setCodItemSus1(regItemRealiz.getProcedimentoHospitalarSus());
				    						  erro.setErro("ERRO AO INSERIR CAMPO MEDICO AUDITOR AIH: " + msg);
				    						  erro.setFatMensagemLog(new FatMensagemLog(64));
				    						  persistirLogErroCarregandoFatMensagemLog(
													faturamentoFacade, erro);
				    					  } catch (Exception e2) {
				    						  logar(MSG_EXCECAO_IGNORADA, e);
				    					  }
				    					  return Boolean.FALSE;
				    				  }

				    				  // verifica se foi autorizado pela SMS 29/11/2004				    				  
				    				  final FatAutorizadoCma autorizado = fatAutorizadoCmaDAO.buscarPrimeiraPorCthCodSus(pCthSeq, regItemRealiz.getProcedimentoHospitalarSus());
				    				  if(autorizado != null){
				    					  vCodSusCma = autorizado.getId().getCodSusCma();
				    				  }
				    				  
				    				  if (fatVariaveisVO.getvMaiorValor()
				    						  && Boolean.FALSE.equals(pPrevia)
				    						  && CoreUtil.igual("S",regConta.getIndAutorizadoSms())
				    						  && regConta.getContaHospitalarReapresentada() == null  // não consiste reapres
				    					  	  && !CoreUtil.igual(regItemRealiz.getProcedimentoHospitalarSus(), pCodSusRealiz)){
				    					  
				    					  logar("verifica autorização cma maior valor parte 1 {0}", vCodSusCma);
				    					  if(autorizado == null){
				    						  logar("verifica autorização cma parte 1 não achou");
				    						  vCmaNaoAutorizado = Boolean.TRUE;
				    						  
				    						  try{
					    						  FatLogError erro = new FatLogError();
					    						  erro.setModulo(INT);
					    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
					    						  erro.setCriadoEm(new Date());
					    						  erro.setCriadoPor(servidorLogado.getUsuario());
					    						  erro.setDataPrevia(pDataPrevia);
					    						  erro.setCthSeq(pCthSeq);
					    						  erro.setPacCodigo(pPacCodigo);
					    						  erro.setProntuario(pPacProntuario);			    			
					    						  erro.setPhiSeq(pPhiSolic);
					    						  erro.setIphPhoSeq(pPhoSolic);
					    						  erro.setIphSeq(pIphSolic);

					    						  erro.setCodItemSusSolicitado(regItemRealiz.getProcedimentoHospitalarSus());
					    						  erro.setCodItemSusRealizado(pCodSusRealiz);
					    						  erro.setCodItemSus1(regItemRealiz.getProcedimentoHospitalarSus());
					    						  
					    						  erro.setPhiSeqRealizado(pPhiRealiz);
					    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
					    						  erro.setIphSeqRealizado(pIphRealiz);    			
					    						  erro.setIphPhoSeqItem1(regItemRealiz.getIphPhoSeq());
					    						  erro.setIphSeqItem1(regItemRealiz.getIphSeq());
					    						  erro.setFatMensagemLog(new FatMensagemLog(19));
					    						  erro.setErro("CMA NÃO AUTORIZADO PELA SMS");
					    						  persistirLogErroCarregandoFatMensagemLog(
														faturamentoFacade, erro);
					    					  } catch (Exception e2) {
					    						  logar(MSG_EXCECAO_IGNORADA, e2);
					    					  }
				    					  }
				    				  }
				    				  // fim verifica se foi autorizado pela SMS  29/11/2004
				    			} else {
				    				
				    				getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, regItemRealiz.getIphPhoSeq(), regItemRealiz.getIphSeq(), 
				    						regItemRealiz.getProcedimentoHospitalarSus(), regItemRealiz.getQuantidade(), regItemRealiz.getQuantidade(), 
				    						regItemRealiz.getValorServHosp(), regItemRealiz.getValorServProf(), regItemRealiz.getValorSadt(), 
				    						regItemRealiz.getValorProcedimento(), regItemRealiz.getValorAnestesista(), 
				    						regItemRealiz.getPontosAnestesista(), regItemRealiz.getPontosCirurgiao(), regItemRealiz.getPontosSadt());
				    			} // v_cah_seqp is not null
				              	//<-------------FIM DO LANCAMENTO DO PROCEDIMENTO NO CMA
				    		  } //  for i in 1..reg_item_realiz.quantidade
				    	  } // v_realiz_preenche_cma = 'S'
				      } //reg_item_realiz.procedimento_hospitalar_sus<>p_cod_sus_solic
		    	  }
		      } //end loop;
		      
		      // busca atos obrigatorios nos itens do espelho:
		      rnCthcAtuGeraAihVO.setRegItem(null);
		      
		      // rever ordenaçao...
		      List<FatEspelhoItemContaHospVO> listaItemAgrup = cItemAgrup(pCthSeq, DominioTipoItemEspelhoContaHospitalar.O, 
//		    		  														rnCthcAtuGeraAihVO.getCodAtoUtie(), rnCthcAtuGeraAihVO.getCodAtoRn(), 
//		    		  														rnCthcAtuGeraAihVO.getCodAtoConsPed(),rnCthcAtuGeraAihVO.getCodAtoHemat(), 
//		    		  														rnCthcAtuGeraAihVO.getCodAtoOpm(), rnCthcAtuGeraAihVO.getCodAtoSalaTransp(), 
//		    		  														rnCthcAtuGeraAihVO.getCodAtoSadtTransp(), rnCthcAtuGeraAihVO.getCodAtoCirurTransp(),
		    		  														pPrevia);
		      
		      
		      Iterator<FatEspelhoItemContaHospVO> iteratorItemAgrup = null;
		      if(listaItemAgrup != null && !listaItemAgrup.isEmpty()){
		    	  iteratorItemAgrup = listaItemAgrup.iterator();
		      }
		      while (iteratorItemAgrup!= null && 
		    		  iteratorItemAgrup.hasNext() && 
		    		  CoreUtil.menorOuIgual(rnCthcAtuGeraAihVO.getEaiSeqp(), rnCthcAtuGeraAihVO.getMaxEai())){
		    	  		    	  
		    	  FatEspelhoItemContaHospVO fatEspelhoItemContaHospVO = (FatEspelhoItemContaHospVO) iteratorItemAgrup.next();
		    	  
		    	  rnCthcAtuGeraAihVO.setRegItem(fatEspelhoItemContaHospVO);
		    	  FatItensProcedHospitalar itemProcedHospitalarRegItem = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphSeq()));
		    	  
		    	  logar("vai lancar demais itens");
		    	  
		    	  // verifica se a qtd pode ser maior que o nro de diarias
		    	  logar("quantidade: {0}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
		    	  if (!CoreUtil.igual(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pCodSusRealiz) &&
		    			  (itemProcedHospitalarRegItem == null || !itemProcedHospitalarRegItem.getQuantidadeMaiorInternacao())){
		    		  
		    		  pDiasConta = (Integer) CoreUtil.nvl(pDiasConta, 0);
		    		  
		    		  // verifica se qtde do item e' maior que qtd diarias da conta
		    		  if(CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta)){
		    			  
		    			  // dias ao inves de diarias (JRS-13/07/2000)		    			  
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
		    			  try {
		    				  FatLogError erro = new FatLogError();
    						  erro.setModulo(INT);
    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
    						  erro.setCriadoEm(new Date());
    						  erro.setCriadoPor(servidorLogado.getUsuario());
    						  erro.setDataPrevia(pDataPrevia);
    						  erro.setCthSeq(pCthSeq);
    						  erro.setPacCodigo(pPacCodigo);
    						  erro.setProntuario(pPacProntuario);			    			
    						  erro.setPhiSeq(pPhiSolic);
    						  erro.setIphPhoSeq(pPhoSolic);
    						  erro.setIphSeq(pIphSolic);
    						  erro.setCodItemSusSolicitado(pCodSusSolic);
    						  erro.setPhiSeqRealizado(pPhiRealiz);
    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
    						  erro.setIphSeqRealizado(pIphRealiz);    			
    						  erro.setCodItemSusRealizado(pCodSusRealiz);
    						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
    						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
    						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
    						  erro.setErro(QUANTIDADE_NAO_PODE_SER_MAIOR_QUE_DIAS_DA_CONTA + rnCthcAtuGeraAihVO.getRegItem().getQuantidade() + _MAIOR_ + pDiasConta);
    						  erro.setFatMensagemLog(new FatMensagemLog(234));
    						  persistirLogErroCarregandoFatMensagemLog(
									faturamentoFacade, erro);
		    			  } catch (Exception e) {
		    				  logar(MSG_EXCECAO_IGNORADA, e);
		    			  }

		    			  long vQtdRealiz = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
				          long vQtdPerd = (nvl(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0) - pDiasConta);
				          logar("NOVA QTD DEVIDO A MAIORINT: {0} > {1}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta);
				          
				          BigDecimal vVlrShPerd = null;
			              BigDecimal vVlrSpPerd = null;
			              BigDecimal vVlrSadtPerd = null;
			              BigDecimal vVlrAnestPerd = null;
			              BigDecimal vVlrProcPerd = null;
			              Long vPtAnestPerd = null;
			              Long vPtCirurPerd = null;
			              Long vPtSadtPerd = null;
			              
				          // Marina 30/11/2010
				          if(CoreUtil.maior(pDiasConta, 0)){
				        	  // altera a quantidade
				        	  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(nvl(pDiasConta,0).longValue());
				        	  
				              vVlrShPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              vVlrSpPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              vVlrSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              vVlrAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              vVlrProcPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              vPtAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
				              vPtCirurPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
				              vPtSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
				          } //  Marina 30/11/2010
				          
				          getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, 
				        		  				rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
				        		  				rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
				        		  				rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
				        		  				vQtdPerd, vQtdRealiz, vVlrShPerd, vVlrSpPerd, vVlrSadtPerd, vVlrProcPerd,
				        		  				vVlrAnestPerd, vPtAnestPerd, vPtCirurPerd, vPtSadtPerd);
		    		  } // rnCthcAtuGeraAihVO.getRegItem().quantidade > p_dias_conta
		    	  } // not fatk_iph_rn.rn_iphc_ver_maiorint

		    	  // verifica quantidade maxima permitida para cobranca na conta
		    	  logar("X - reg_item.iph_pho_seq: {0} reg_item.iph_seq: {1} reg_item.quantidade: {2} reg_item.procedimento_hospitalar_sus: {3}",
		    			  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
		    			  rnCthcAtuGeraAihVO.getRegItem().getIphSeq(),
		    			  rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 
		    			  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());

		    	  // Marina 06/12/2013 - Portaria 1151 - chamado: 115247
		    	  //dbms_output.put_line('reg_item_realiz.quantidade: ' || reg_item_realiz.quantidade);
		    	  logar("reg_item.quantidade: {0}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
		    	  logar("PROCEDIMENTO SUS: {0}",  StringUtils.substring(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus().toString(), 0, 1));
		    	  
		    	  Long vMaxCta = this.getFatVlrItemProcedHospCompsDAO().obterQtdMaximaExecucaoPorPhoIphParaCompetencia(
		    			  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), regConta.getDataInternacaoAdministrativa());
		    	  
			      if (vMaxCta == 0) {
			    	  vMaxCta = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
			      }
			      
			      if (StringUtils.substring(
							rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus().toString(), 0, 1).equals(codGrupoProcCirurgicos.toString()) && vMaxCta > 1) {
						
						// Atualiza a Quantidade
						// open c_busca_nova_qtde (reg_item.procedimento_hospitalar_sus, p_cth_seq);
						// final Byte atoCirur = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO);
						final Short vNovaQtde = getFatEspelhoItemContaHospDAO().obterCursorBuscaNovaQtde(pCthSeq, rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pTaoSeq);
						
						logar("v_nova_qtde: {0}", vNovaQtde);
						
						rnCthcAtuGeraAihVO.getRegItem().setQuantidade(vNovaQtde.longValue());
						rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), vNovaQtde));
						rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), vNovaQtde));
						rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), vNovaQtde));
						rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), vNovaQtde));
						rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), vNovaQtde));
						
						rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), vNovaQtde).longValue());
						rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), vNovaQtde).longValue());
						rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), vNovaQtde).longValue());
					
						logar(" {0} + {1} + {2} + {3} ", 
								rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista().toString(),
								rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento().toString(),
								rnCthcAtuGeraAihVO.getRegItem().getValorServHosp().toString(),
								rnCthcAtuGeraAihVO.getRegItem().getValorServProf().toString());
						
						logar("**** FIM DA PORTARIA *****");
					}
					// Fim Marina 
					
					vMaxCta = (Long) nvl(vMaxCta, rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
					logar("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					logar("v_max_cta: {0}", vMaxCta);
					logar("reg_item.procedimento_hospitalar_sus: {0}", rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
					logar("p_cod_sus_realiz: {0}", pCodSusRealiz);
					logar("v_max_cta: {0}", vMaxCta);
					logar("reg_item.quantidade: {0}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
					logar("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		    	  if(!CoreUtil.igual(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pCodSusRealiz) &&
		    			CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta)){
		    		  
		    		  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
	    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
	    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
	    			  
	    			  try {
	    				  FatLogError erro = new FatLogError();
						  erro.setModulo(INT);
						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
						  erro.setCriadoEm(new Date());
						  erro.setCriadoPor(servidorLogado.getUsuario());
						  erro.setDataPrevia(pDataPrevia);
						  erro.setCthSeq(pCthSeq);
						  erro.setPacCodigo(pPacCodigo);
						  erro.setProntuario(pPacProntuario);			    			
						  erro.setPhiSeq(pPhiSolic);
						  erro.setIphPhoSeq(pPhoSolic);
						  erro.setIphSeq(pIphSolic);
						  erro.setCodItemSusSolicitado(pCodSusSolic);
						  erro.setPhiSeqRealizado(pPhiRealiz);
						  erro.setIphPhoSeqRealizado(pPhoRealiz);
						  erro.setIphSeqRealizado(pIphRealiz);    			
						  erro.setCodItemSusRealizado(pCodSusRealiz);
						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
						  erro.setErro("QUANTIDADE MAIOR QUE MAXIMO PERMITIDO PARA O PROCEDIMENTO: " + rnCthcAtuGeraAihVO.getRegItem().getQuantidade() + _MAIOR_ + vMaxCta);
						  erro.setFatMensagemLog(new FatMensagemLog(231));
						  persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
	    			  } catch (Exception e) {
	    				  logar(MSG_EXCECAO_IGNORADA, e);
	    			  }

	    			  long vQtdRealiz = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
			          long vQtdPerd =  (nvl(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0) - vMaxCta);
			          logar("NOVA QTD DEVIDO A MAX QTD CONTA: {0} > {1}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta);
		      
			          // Marina 30/11/2010
//			          if(CoreUtil.maior(vMaxCta, 0)){
			        	  // altera a quantidade
			        	  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(nvl(vMaxCta,0));
		      
			        	  BigDecimal vVlrShPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
			        	  BigDecimal vVlrSpPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
			        	  BigDecimal vVlrSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
			        	  BigDecimal vVlrAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
			        	  BigDecimal vVlrProcPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
			        	  Long vPtAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
			        	  Long vPtCirurPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
			        	  Long vPtSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
//			          } //  Marina 30/11/2010
		      
			        	  getFaturamentoInternacaoRN().rnCthcAtuInsPit( pCthSeq,  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
				        		  					rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
				        		  					rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
				        		  					vQtdPerd, vQtdRealiz, vVlrShPerd, vVlrSpPerd, vVlrSadtPerd, vVlrProcPerd,
				        		  					vVlrAnestPerd, vPtAnestPerd, vPtCirurPerd, vPtSadtPerd);
		      
		    	  } // rnCthcAtuGeraAihVO.getRegItem().quantidade > v_max_cta

		    	  // verifica se o procedimento e' especial
		    	  Boolean vProcedEspecial = verificacaoItemProcedimentoHospitalarRN.getProcedimentoEspecial(itemProcedHospitalarRegItem);
		    	  if(Boolean.FALSE.equals(vProcedEspecial) || 
		    			  (Boolean.TRUE.equals(vProcedEspecial) && Boolean.TRUE.equals(vRealizCobraEspecial))) {
		    		  
		    		  logar("vai lancar item nos SERV PROF: SUS {0} PHO {1} IPH {2} reg_item.quantidade: {3} v_max_qtd: {4}",
		    				  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
		    				  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
		    				  rnCthcAtuGeraAihVO.getRegItem().getIphSeq(),
		    				  rnCthcAtuGeraAihVO.getRegItem().getQuantidade(),
		    				  vMaxQtd);

		    		  // Marina 03/12/2009 - Alterado o parâmetro para aceitar 3 dígitos na quantidade conforme novo layout do SISAIH
			          // verifica se a quantidade e' maior que 99(parametro de sistema) - ETB
			          // pois campo qtd na AIH tem 2 digitos
		    		  if(CoreUtil.menorOuIgual(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd)){

		    			  // Marina 03/12/2009 - Alterado o parâmetro para aceitar 3 dígitos conforme novo layout do SISAIH
		    			  logar("quantidade menor que 99");

		    			  // lanca o item com qtd = rnCthcAtuGeraAihVO.getRegItem().quantidade:
		    			  // Lanca procedimento nos ATOS MEDICOS:
		    			  logar("1 - reg_item.tao_seq: {0}", rnCthcAtuGeraAihVO.getRegItem().getTaoSeq());
		      
		    			  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia, 
		    					  						 pDataPrevia, pPacCodigo, pPacProntuario, 
		    					  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
		    					  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, 
		    					  						 pIphRealiz, pCodSusRealiz, rnCthcAtuGeraAihVO, 
    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
		      
		    			  if(CoreUtil.menor(res, 0)){ // deu erro no lancamento AAM
		    				  return Boolean.FALSE;
		    			  } else if(CoreUtil.igual(res, 0)){ // estourou qtd aihs, deve sair do loop
		    				  break;
		    			  }
		    		  } else { // if rnCthcAtuGeraAihVO.getRegItem().quantidade <= v_max_qtd
		    			  logar("quantidade maior que 99");
		    			  // guarda os valores originais
		      
		    			  Long vQtdIni = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
		    			  BigDecimal vVlrAnestIni = rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista();
		    			  BigDecimal vVlrProcIni = rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento();
		    			  BigDecimal vVlrSadtIni  = rnCthcAtuGeraAihVO.getRegItem().getValorSadt();
		    			  BigDecimal vVlrShIni = rnCthcAtuGeraAihVO.getRegItem().getValorServHosp();
		    			  BigDecimal vVlrSpIni = rnCthcAtuGeraAihVO.getRegItem().getValorServProf();
		    			  Long vPtAnestIni = rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista();
		    			  Long vPtCirurIni = rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao();
		    			  Long vPtSadtIni = rnCthcAtuGeraAihVO.getRegItem().getPontosSadt();
		      
		    			  // quebra qtde em varias partes de v_max_qtd(acerta vlrs/ptos)		    			  
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),					rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
		    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
		    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),				rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
		    			  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(vMaxQtd.longValue());
		      
		    			  Integer vAux = BigDecimal.valueOf(vQtdIni).divide(BigDecimal.valueOf(vMaxQtd)).intValue(); 
		    			  logar("trunc(v_qtd_ini/v_max_qtd): {0}", vAux);
		    			  for (int i = 1; i <= vAux; i++) {
				              // Marina 03/12/2009 - Alterado o parâmetro para aceitar 3 dígitos na quantidade conforme novo layout do SISAIH
				              // lanca varias vezes qtd 99 ETB
				              // lanca o item com qtd = v_max_qtd:
				              // Lanca procedimento nos ATOS MEDICOS:
		    				  logar("2");
		    				  
		    				  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia, 
		    						  						 pDataPrevia, pPacCodigo, pPacProntuario, 
		    						  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
		    						  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, 
		    						  						 pIphRealiz, pCodSusRealiz, rnCthcAtuGeraAihVO, 
	    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, 
	    							  						 dataFimVinculoServidor,fatVariaveisVO);
		    				  if(CoreUtil.menor(res, 0)){
		    					  return Boolean.FALSE;
		    				  } else if(CoreUtil.igual(res, 0)){ // estourou qtd aihs, deve sair do loop
		    					  break;
		    				  }
		    			  }
		    			  
		    			  if(nvl(vQtdIni,0) % nvl(vMaxQtd,0) > 0){ // se tem resto
		    				  // lanca o item com qtd = mod(v_qtd_ini,v_max_qtd)
		    				  Long vQtdAux = nvl(vQtdIni,0) % nvl(vMaxQtd,0);

		    				  logar("mod(v_qtd_ini,v_max_qtd): {0}", vQtdAux);
		      
		    				  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(vVlrAnestIni, vQtdIni, vQtdAux));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(vVlrProcIni, vQtdIni, vQtdAux));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(vVlrSadtIni, 		 vQtdIni, vQtdAux));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(vVlrShIni, 		 vQtdIni, vQtdAux));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(vVlrSpIni, 		 vQtdIni, vQtdAux));
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(vPtAnestIni, vQtdIni, vQtdAux).longValue());
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(vPtCirurIni, 	 vQtdIni, vQtdAux).longValue());
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(vPtSadtIni, 		 vQtdIni, vQtdAux).longValue());
			    			  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(vQtdAux);

			    			  //Lanca procedimento nos ATOS MEDICOS:
			    			  logar("3");
			    			  
			    			  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia, 
			    					  						 pDataPrevia, pPacCodigo, pPacProntuario, 
			    					  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
			    					  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, 
			    					  						 pIphRealiz, pCodSusRealiz, rnCthcAtuGeraAihVO, 
	    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, 
	    							  						 dataFimVinculoServidor, fatVariaveisVO);
			    			  if(CoreUtil.menor(res, 0)){
			    				  return Boolean.FALSE;	  
			    			  } else if(CoreUtil.igual(res,0)) { // estourou qtd aihs, deve sair do loop
			    				  break;
			    			  }
		    			  } // mod(qtdIni,v_max_qtd) > 0
		    		  }   // rnCthcAtuGeraAihVO.getRegItem().quantidade <= v_max_qtd
		    	  } else {
		    		  getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq,
							    		 rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getIphSeq(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getQuantidade(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getQuantidade(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),
							    		 rnCthcAtuGeraAihVO.getRegItem().getPontosSadt());
		    	  } // (v_proced_especial and v_realiz_cobra_especial)
		      }

		      // busca demais itens do espelho:
		      rnCthcAtuGeraAihVO.setRegItem(null);
		      
		      logar("open c_item_agrup_por_proc");

		      List<FatEspelhoItemContaHospVO> listaItemAgrupD = this.cItemAgrupPorProc( pCthSeq, DominioTipoItemEspelhoContaHospitalar.D,
																		    		    pPrevia);
		      String competenciaUti = null;
		      ITERATOR_ITEM_AGRUPD:
		      for(FatEspelhoItemContaHospVO fatEspelhoItemContaHospVO : listaItemAgrupD){
		    	  rnCthcAtuGeraAihVO.setRegItem(fatEspelhoItemContaHospVO);		      
		    	  FatItensProcedHospitalar itemProcedHospitalarRegItem = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphSeq()));
		    	  
		    	  if(fatEspelhoItemContaHospVO != null){
			    	  logar("found c_item_agrup_por_proc=" + rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
			      } else {
			    	  logar("NOT found c_item_agrup_por_proc");
			      }
		    	  
		    	  if(!CoreUtil.menorOuIgual(rnCthcAtuGeraAihVO.getEaiSeqp(), rnCthcAtuGeraAihVO.getMaxEai())){
		    		  break ITERATOR_ITEM_AGRUPD;
		    	  }
		    	  
		    	  Long vMaxQtdeLong = null;
		    	  if(regConta.getDtAltaAdministrativa() != null){
		    		  vMaxQtdeLong = fatcBuscaProcedDoItem( pCthSeq, rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
															rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), atoCirur, competenciaUti, 
															// TRUNC(reg_conta.DT_ALTA_ADMINISTRATIVA,'month')
															DateUtil.obterDataInicioCompetencia(regConta.getDtAltaAdministrativa()));
		    	  }else{
		    		  vMaxQtdeLong = fatcBuscaProcedDoItem( pCthSeq, rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
								rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), atoCirur, competenciaUti, 
								// TRUNC(reg_conta.DT_ALTA_ADMINISTRATIVA,'month')
								null);
		    	  }
		    	  
		    	  Long vMaxQtde = vMaxQtdeLong != null ? vMaxQtdeLong : null; 
		    	  logar("v_max_qtde: {0}", vMaxQtde);
		    	  logar("r_item_agrup_por_proc.quantidade: {0}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
		    	  
		    	  // Ney, 20111229 Guarda em var de packpage qtd maxima permitida, para permitir duplicidade qdo max permitida > 1
		    	  //vMaxQtdePermitida = vMaxQtde; // Marina 20/05/2013 

		    	  long vMaxCta = nvl(vMaxQtde, rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
	    	      long vMaxCtaLogErro = vMaxCta;
	    	      vMaxQtdePermitida = vMaxCta; // Marina 20/05/2013
	    	      long vTotalLancado = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
	    	      
	    	      logar("v_max_cta: {0}", vMaxCta);
	    	      logar("v_max_qtde_permitida: {0}", vMaxQtdePermitida);
	    	      logar("v_max_cta_log_erro: {0}", vMaxCtaLogErro);
	    	      logar("v_total_lancado: {0}", vTotalLancado);
		    	  
	    	      List<FatEspelhoItemContaHospVO> listaItemAgrupPorProcedimentoCompetencia = 
	    	    	  this.cItemAgrupPorProcComp(pCthSeq, DominioTipoItemEspelhoContaHospitalar.D, rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
	    	    		  rnCthcAtuGeraAihVO.getRegItem().getTivSeq(), rnCthcAtuGeraAihVO.getRegItem().getTaoSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
	    	    		  rnCthcAtuGeraAihVO.getRegItem().getIphSeq(),
	    	    		  pPrevia);
	    	      
	    	      ITERATOR_ITEM_AGRUPD_COMP:
	    	      for (FatEspelhoItemContaHospVO itemAgrupPorProcComp : listaItemAgrupPorProcedimentoCompetencia) {
	    	    	  rnCthcAtuGeraAihVO.setRegItem(itemAgrupPorProcComp);
	    	    	  
	    	    	  if(listaItemAgrupPorProcedimentoCompetencia == null || listaItemAgrupPorProcedimentoCompetencia.isEmpty()) {
	    	    		  logar("NOT found c_item_agrup_por_proc_comp");
	    	    	  } else {
	    	    		  logar("found c_item_agrup_por_proc_comp");
	    	    	  }
	    	    	  
	    	    	  if(!CoreUtil.menorOuIgual(rnCthcAtuGeraAihVO.getEaiSeqp(), rnCthcAtuGeraAihVO.getMaxEai())){
			    		  break ITERATOR_ITEM_AGRUPD_COMP;
			    	  }
	    	    	  
	    	    	  competenciaUti = itemAgrupPorProcComp.getCompetenciaUti();
	    	    	  
	    	    	  logar("teste Marina:{0}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade());	    	    	  
			    	  logar("vai lancar demais itens tipo D {0} p_dias_conta: {1} reg_item.quantidade: {2}", rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pDiasConta, rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
			    	  
			    	  // verifica se a qtd pode ser maior que o nro de diarias
			    	  if (itemProcedHospitalarRegItem == null || !itemProcedHospitalarRegItem.getQuantidadeMaiorInternacao()){
			    		  
			    		  // verifica se qtde do item e' maior que qtd diarias da conta
			    		  if(CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta)){
			    	  		  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),					rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta));
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
			    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),				rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta).longValue());
			    			  try {
			    				  FatLogError erro = new FatLogError();
	    						  erro.setModulo(INT);
	    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
	    						  erro.setCriadoEm(new Date());
	    						  erro.setCriadoPor(servidorLogado.getUsuario());
	    						  erro.setDataPrevia(pDataPrevia);
	    						  erro.setCthSeq(pCthSeq);
	    						  erro.setPacCodigo(pPacCodigo);
	    						  erro.setProntuario(pPacProntuario);			    			
	    						  erro.setPhiSeq(pPhiSolic);
	    						  erro.setIphPhoSeq(pPhoSolic);
	    						  erro.setIphSeq(pIphSolic);
	    						  erro.setCodItemSusSolicitado(pCodSusSolic);
	    						  erro.setPhiSeqRealizado(pPhiRealiz);
	    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
	    						  erro.setIphSeqRealizado(pIphRealiz);    			
	    						  erro.setCodItemSusRealizado(pCodSusRealiz);
	    						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
	    						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
	    						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
	    						  erro.setErro(QUANTIDADE_NAO_PODE_SER_MAIOR_QUE_DIAS_DA_CONTA + rnCthcAtuGeraAihVO.getRegItem().getQuantidade() + _MAIOR_ + pDiasConta);
	    						  erro.setFatMensagemLog(new FatMensagemLog(234));
	    						  persistirLogErroCarregandoFatMensagemLog(
										faturamentoFacade, erro);
			    			  } catch (Exception e) {
			    				  logar(MSG_EXCECAO_IGNORADA, e);
			    			  }
			    			  
			    			  Long vQtdRealiz = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
					          Long vQtdPerd =  (nvl(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0) - pDiasConta);
					          logar("NOVA QTD DEVIDO A MAIOR INT: {0} > {1}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), pDiasConta);
					          
				        	  // altera a quantidade
				        	  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(nvl(pDiasConta,0).longValue());
				        	  
				        	  BigDecimal vlrShPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), 		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				        	  BigDecimal vlrSpPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), 		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				        	  BigDecimal vlrSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), 		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				        	  BigDecimal vlrAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				        	  BigDecimal vlrProcPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd);
				              Long ptAnestPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), 	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
				              Long ptCirurPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), 		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
				              Long ptSadtPerd = correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), 			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vQtdPerd).longValue();
					          
				              getFaturamentoInternacaoRN().rnCthcAtuInsPit( pCthSeq, 
							        		  				rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
							        		  				rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
							        		  				rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
							        		  				vQtdPerd, vQtdRealiz, vlrShPerd, vlrSpPerd, vlrSadtPerd, vlrProcPerd,
							        		  				vlrAnestPerd, ptAnestPerd, ptCirurPerd, ptSadtPerd);
			    		  } // rnCthcAtuGeraAihVO.getRegItem().quantidade > p_dias_conta
			    	  } // not fatk_iph_rn.rn_iphc_ver_maiorint
	
			    	  logar("Ney - reg_item.iph_pho_seq: {0} reg_item.iph_seq: {1} reg_item.procedimento_hospitalar_sus: {2} p_cth_seq: {3} comp={4} reg_item.quantidade={5} v_max_cta={6}",
					    	  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
					    	  rnCthcAtuGeraAihVO.getRegItem().getIphSeq(),
					    	  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(),
					    	  pCthSeq,
					    	  rnCthcAtuGeraAihVO.getRegItem().getCompetenciaUti(),
					    	  rnCthcAtuGeraAihVO.getRegItem().getQuantidade(),
					    	  vMaxCta);
	
			    	  if(CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta)){
			    		  // Busca caracteristica
			    		  logar("Valida se existe EXCLUSAO CRITICA para quantidade");
			    		  if(getFaturamentoRN().verificarCaracteristicaExame(rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD)){
			    			  logar("Chama exclusão de critica para liberação da Qtde");
			    			  fatkCthRN.rnFatpExclusaoCritica(DominioSituacao.I, DominioSituacao.I, DominioSituacao.I, DominioSituacao.A, DominioSituacao.I, pCthSeq, DominioSituacao.I, nomeMicrocomputador, dataFimVinculoServidor);
			    		  } else {
			    			//Arthur 06/05/2014 - Portaria 1379
			    			  Integer vClicRealizado = this.getVFatAssociacaoProcedimentoDAO().obterClinica(pPhiRealiz);			    			    
			    			  Integer vClinicaPsiquiatrica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_PSIQUIATRICA);
			    			  
			    			  if(vClicRealizado != null && !vClicRealizado.equals(vClinicaPsiquiatrica)) {
				    			  // Ney 14/08/2012
					              // v_vlr_anest_perd :=  reg_item.valor_anestesista  - (reg_item.valor_anestesista/reg_item.quantidade)*v_max_cta;
					        	  BigDecimal vlrAnestPerd = rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista().subtract(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
					        	  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
					        	  
					        	  // v_vlr_proc_perd            := reg_item.valor_procedimento - (reg_item.valor_procedimento/reg_item.quantidade)*v_max_cta;
					        	  BigDecimal vVlrProcPerd = rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento().subtract(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
	
				    			  //v_vlr_sadt_perd            := reg_item.valor_sadt         - (reg_item.valor_sadt/reg_item.quantidade)*v_max_cta;
				    			  BigDecimal vVlrSadtPerd = rnCthcAtuGeraAihVO.getRegItem().getValorSadt().subtract(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),					rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  
				    			  // v_vlr_sh_perd              := reg_item.valor_serv_hosp    - (reg_item.valor_serv_hosp/reg_item.quantidade)*v_max_cta;
				    			  BigDecimal vVlrShPerd= rnCthcAtuGeraAihVO.getRegItem().getValorServHosp().subtract(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  
				    			  // v_vlr_sp_perd              := reg_item.valor_serv_prof    - (reg_item.valor_serv_prof/reg_item.quantidade)*v_max_cta;
				    			  BigDecimal vVlrSpPerd = rnCthcAtuGeraAihVO.getRegItem().getValorServProf().subtract(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta));
				    			  
				    			  // v_pt_anest_perd            := reg_item.pontos_anestesista - (reg_item.pontos_anestesista/reg_item.quantidade)*v_max_cta;
				    			  Long vPtAnestPerd = rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista().intValue() - correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue();
				    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
	
				    			  // v_pt_cirur_perd            := reg_item.pontos_cirurgiao   - (reg_item.pontos_cirurgiao/reg_item.quantidade)*v_max_cta;
				    			  Long vPtCirurPerd = rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao().intValue() - correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue();
				    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
				    			  
				    			  // v_pt_sadt_perd             := reg_item.pontos_sadt        - (reg_item.pontos_sadt/reg_item.quantidade)*v_max_cta;
				    			  Long vPtSadtPerd = rnCthcAtuGeraAihVO.getRegItem().getPontosSadt().intValue() - correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue();
				    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),				rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta).longValue());
		
				    			  try {
				    				  FatLogError erro = new FatLogError();
		    						  erro.setModulo(INT);
		    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
		    						  erro.setCriadoEm(new Date());
		    						  erro.setCriadoPor(servidorLogado.getUsuario());
		    						  erro.setDataPrevia(pDataPrevia);
		    						  erro.setCthSeq(pCthSeq);
		    						  erro.setPacCodigo(pPacCodigo);
		    						  erro.setProntuario(pPacProntuario);			    			
		    						  erro.setPhiSeq(pPhiSolic);
		    						  erro.setIphPhoSeq(pPhoSolic);
		    						  erro.setIphSeq(pIphSolic);
		    						  erro.setCodItemSusSolicitado(pCodSusSolic);
		    						  erro.setPhiSeqRealizado(pPhiRealiz);
		    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
		    						  erro.setIphSeqRealizado(pIphRealiz);    			
		    						  erro.setCodItemSusRealizado(pCodSusRealiz);
		    						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
		    						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
		    						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
		    						  erro.setErro("QUANTIDADE MAIOR QUE MAXIMO PERMITIDO PARA O PROCEDIMENTO: " + vTotalLancado + _MAIOR_ + vMaxCtaLogErro);
		    						  erro.setFatMensagemLog(new FatMensagemLog(231));
		    						  persistirLogErroCarregandoFatMensagemLog(
											faturamentoFacade, erro);
				    			  } catch (Exception e) {
				    				  logar(MSG_EXCECAO_IGNORADA, e);
				    			  }
				    			  
				    			  Long vQtdRealiz = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
						          Long vQtdPerd = (nvl(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0) - vMaxCta);
						          logar("NOVA QTD DEVIDO A MAX QTD POR PROCEDIMENTO:{0} > {1}", rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxCta);
						          
						          // Ney 14/08/2012
						          // altera a quantidade 
						          rnCthcAtuGeraAihVO.getRegItem().setQuantidade(nvl(vMaxCta,0));
						          vMaxCta = 0;
	
						          getFaturamentoInternacaoRN().rnCthcAtuInsPit( pCthSeq, 
								        		  				rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
								        		  				rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
								        		  				rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
								        		  				vQtdPerd, vQtdRealiz, vVlrShPerd, vVlrSpPerd, vVlrSadtPerd, vVlrProcPerd,
								        		  				vlrAnestPerd, vPtAnestPerd, vPtCirurPerd, vPtSadtPerd);
				        	  
			    			  }
			    		 } //if fatc_ver_caract_iph(null, null, rnCthcAtuGeraAihVO.getRegItem().iph_pho_seq, rnCthcAtuGeraAihVO.getRegItem().iph_seq, 'Admite liberação de Qtd') = 'S' then
			    	  
			    	  } else {
			    		 vMaxCta = (short) (vMaxCta - rnCthcAtuGeraAihVO.getRegItem().getQuantidade());
			    	  }//if rnCthcAtuGeraAihVO.getRegItem().quantidade > v_max_cta then
			    	  
			          // Ney 31/08/2011
			          // exclui aqui um trecho que parecia estar duplicado
			    	  // verifica se o procedimento e' especial
			    	  logar("Unificada: verifica se é especial: {0}", rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
			    	  Boolean vProcedEspecial = verificacaoItemProcedimentoHospitalarRN.getProcedimentoEspecial(itemProcedHospitalarRegItem);
	
			    	  logar("Unificada: é especial: {0}", rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
			    	  if(!vProcedEspecial || (vProcedEspecial && vRealizCobraEspecial)) {
			    		  // verifica se o procedimento pode ser lancado no CMA
			    		  Boolean cobrancaCma = itemProcedHospitalarRegItem.getCobrancaCma();
			    		  
			    		  if(cobrancaCma){
			    			  logar("vai lancar item no CMA: SUS {0} PHO {1} IPH {2}",
			    					  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(),
			    					  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
			    					  rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
			    			  
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getPercFideps()));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getPercFideps()));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),					rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getPercFideps()));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getPercFideps()));
			    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getPercFideps()));
			    			  
			    			  // Lancar tantas vezes quanto for a quantidade do item:
			    			  for (int i = 1; i <= rnCthcAtuGeraAihVO.getRegItem().getQuantidade(); i++) {
			    				  // --------------->LANCA PROCEDIMENTO NO CMA:
			    				  // calcula seq p/ campo medico
			    				  vCahSeqp = fatCampoMedicoAuditAihDAO.buscarProxSeq(pCthSeq, vPrimeiroEaiSeqp);
			    				  logar("next cah_seqp: {0}", vCahSeqp);
			    				  if(CoreUtil.maior(vCahSeqp, vMaxCah)){
			    					  // atingiu a quantidade maxima de campos medicos
			    					  // e deve parar de lancar procedimentos no cma
			    					  // (nao abre nova aih de continuacao por causa do cma)
			    					  
			    					  // atingiu_limite_cma := true;	eSchweigert 13/09/2012
			    					  atingiuLimiteCma = Boolean.TRUE;
			    				  } // (vCahSeqp is not null)and(vCahSeqp > vMaxCah)
			    				  
			    				  // ETB 271006 verifica se o valor é > que já existente no CMA
			    				  if(Boolean.TRUE.equals(atingiuLimiteCma)){
			    					  vlrCalcCma = 		 nvl(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	BigDecimal.ZERO)
			    					  				.add(nvl(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),BigDecimal.ZERO))
			    					  				.add(nvl(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),		BigDecimal.ZERO))
			    					  				.add(nvl(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),	BigDecimal.ZERO))
			    					  				.add(nvl(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),	BigDecimal.ZERO));
			    					  
			    					  logar("cma2 - apos limite vlr: {0}", vlrCalcCma);
	
				    				  Object[] cma = fatCampoMedicoAuditAihDAO.buscarChaMaxVlr(pCthSeq, vPrimeiroEaiSeqp, DominioTipoItemConta.R, vlrCalcCma);				    				  
				    				  if(cma != null){
				    					  cahVlrSeqp = (Byte)cma[0];  
				    				  }
				    				  logar("cma2 - encontrou {0}", cahVlrSeqp);
				    				  
				    				  if(cahVlrSeqp != null){
				    					  fatCampoMedicoAuditAihDAO.removerPorCthSeqp(pCthSeq, cahVlrSeqp);
				    					  this.flush();
				    					  atingiuLimiteCma = Boolean.FALSE;
				    					  logar("cma2 - deletou {0}", cahVlrSeqp);
				    					  vCahSeqp = cahVlrSeqp; // recebe o seqp a ser inserido de novo
				    				  }
			    				  } // if atingiuLimiteCma then
			    				  
			    				  //  Fim ETB 271006 verifica se o valor é > que já existente no CMA
			    				  if(vCahSeqp != null && Boolean.FALSE.equals(atingiuLimiteCma)){
	
			    					  logar("vai lancar campo medico auditor: cah_seqp {0} SUS {1} PHO {2} IPH {3}", 
			    							  vCahSeqp,
			    							  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(),
			    							  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
			    							  rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
			    					  		    					  
			    					  if(DominioModoCobranca.V.equals(rnCthcAtuGeraAihVO.getRegItem().getIndModoCobranca())){ // valor
			    						  if(DominioLocalSoma.R.equals(rnCthcAtuGeraAihVO.getRegItem().getIndLocalSoma())){ // soma p/valor do realizado
	
			    							  // vai somar nos valores totais do realizado
			    							  if(nvl(rnCthcAtuGeraAihVO.getRegItem().getIphInternacao(), Boolean.FALSE)){
			    								  // soma conforme excecoes percentuais do realizado:
			    								  if(CoreUtil.in(vCahSeqp, 1,2,3,4,5)){
			    									  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(percent[vCahSeqp-1], 100, rnCthcAtuGeraAihVO.getRegItem().getValorServHosp()));
			    									  vVlrShRealiz = vVlrShRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp());
			    								  }		    								  
			    							  } else {
			    								  // nao eh SSM,nao precisa obedecer os percentuais
			    								  vVlrShRealiz = vVlrShRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp());
			    							  } // rnCthcAtuGeraAihVO.getRegItem().ind_internacao = 'S'
			    							 
			    							  vVlrSpRealiz = vVlrSpRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorServProf());
			    							  vVlrSadtRealiz = vVlrSadtRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorSadt());
			    							  vVlrAnestRealiz = vVlrAnestRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista());
			    							  vVlrProcedRealiz = vVlrProcedRealiz.add(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento());
			    							  
			    						  } else if(DominioLocalSoma.D.equals(rnCthcAtuGeraAihVO.getRegItem().getIndLocalSoma())){
			    							  // soma p/valor dos demais itens
			    							  // vai somar nos valores totais :		    							  
			    							  if(nvl(rnCthcAtuGeraAihVO.getRegItem().getIphInternacao(), Boolean.FALSE)){
			    								  // soma conforme excecoes percentuais do realizado:
			    								  if(CoreUtil.in(vCahSeqp, 1,2,3,4,5)){
			    									  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(percent[vCahSeqp-1], 100, rnCthcAtuGeraAihVO.getRegItem().getValorServHosp()));
			    									  rnCthcAtuGeraAihVO.setVlrSh(rnCthcAtuGeraAihVO.getVlrSh().add(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp()));
			    								  }
			    							  } else { // nao eh SSM, nao precisa obedecer os percentuais
			    								  rnCthcAtuGeraAihVO.setVlrSh(rnCthcAtuGeraAihVO.getVlrSh().add(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp()));
			    							  } // rnCthcAtuGeraAihVO.getRegItem().ind_internacao = 'S'
			    							  rnCthcAtuGeraAihVO.setVlrSp(rnCthcAtuGeraAihVO.getVlrSp().add(rnCthcAtuGeraAihVO.getRegItem().getValorServProf()));
			    							  rnCthcAtuGeraAihVO.setVlrSadt(rnCthcAtuGeraAihVO.getVlrSadt().add(rnCthcAtuGeraAihVO.getRegItem().getValorSadt()));
			    							  rnCthcAtuGeraAihVO.setVlrAnest(rnCthcAtuGeraAihVO.getVlrAnest().add(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista()));
			    							  rnCthcAtuGeraAihVO.setVlrProced(rnCthcAtuGeraAihVO.getVlrProced().add(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento()));
			    						  } // rnCthcAtuGeraAihVO.getRegItem().ind_local_soma = 'R' , = 'D'
			    						  
			    					  } else if(CoreUtil.igual(DominioModoCobranca.P,rnCthcAtuGeraAihVO.getRegItem().getIndModoCobranca())){ //pontos
			    						  rnCthcAtuGeraAihVO.setPtosAnest(nvl(rnCthcAtuGeraAihVO.getPtosAnest(), 0) + nvl(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), 0).intValue());
			    						  rnCthcAtuGeraAihVO.setPtosCirur(nvl(rnCthcAtuGeraAihVO.getPtosCirur(), 0) + nvl(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), 0).intValue());
			    						  rnCthcAtuGeraAihVO.setPtosSadt(nvl(rnCthcAtuGeraAihVO.getPtosSadt(), 0) + nvl(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(), 0).intValue());
			    					  }
	
				    				  FatCampoMedicoAuditAih fatCampoMedicoAuditAih = new FatCampoMedicoAuditAih();
				    				  try{
				    					  fatCampoMedicoAuditAih.setId(new FatCampoMedicoAuditAihId(vPrimeiroEaiSeqp, pCthSeq, vCahSeqp));
					    				  fatCampoMedicoAuditAih.setDataPrevia(pDataPrevia);
					    				  fatCampoMedicoAuditAih.setIndModoCobranca(rnCthcAtuGeraAihVO.getRegItem().getIndModoCobranca());
					    				  fatCampoMedicoAuditAih.setIphCodSus(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
					    				  fatCampoMedicoAuditAih.setItemProcedimentoHospilatar(itemProcedHospitalarRegItem);
					    				  fatCampoMedicoAuditAih.setValorAnestesista(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista());
					    				  fatCampoMedicoAuditAih.setValorProcedimento(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento());
					    				  fatCampoMedicoAuditAih.setValorSadt(rnCthcAtuGeraAihVO.getRegItem().getValorSadt());
					    				  fatCampoMedicoAuditAih.setValorServHosp(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp());
					    				  fatCampoMedicoAuditAih.setValorServProf(rnCthcAtuGeraAihVO.getRegItem().getValorServProf());
					    				  fatCampoMedicoAuditAih.setPontosAnestesista(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista().intValue());
					    				  fatCampoMedicoAuditAih.setPontosCirurgiao(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao().intValue());
					    				  fatCampoMedicoAuditAih.setPontosSadt(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt().intValue());
					    				  fatCampoMedicoAuditAih.setIndConsistente(DominioTipoItemConta.valueOf(rnCthcAtuGeraAihVO.getRegItem().getIndLocalSoma().toString()));
					    				  faturamentoFacade.inserirFatCampoMedicoAuditAih(fatCampoMedicoAuditAih, true, nomeMicrocomputador, dataFimVinculoServidor);
					    				 // faturamentoFacade.evict(fatCampoMedicoAuditAih);
				    				  } catch (Exception e) {									
				    					  logar("erro no insert cah");
				    					  String msg = e.getMessage();				    					  
				    					  try{
				    						  FatLogError erro = new FatLogError();
				    						  erro.setModulo(INT);
				    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
				    						  erro.setCriadoEm(new Date());
				    						  erro.setCriadoPor(servidorLogado.getUsuario());
				    						  erro.setDataPrevia(pDataPrevia);
				    						  erro.setCthSeq(pCthSeq);
				    						  erro.setPacCodigo(pPacCodigo);
				    						  erro.setProntuario(pPacProntuario);			    			
				    						  erro.setPhiSeq(pPhiSolic);
				    						  erro.setIphPhoSeq(pPhoSolic);
				    						  erro.setIphSeq(pIphSolic);
				    						  erro.setCodItemSusSolicitado(pCodSusSolic);
				    						  erro.setPhiSeqRealizado(pPhiRealiz);
				    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
				    						  erro.setIphSeqRealizado(pIphRealiz);    			
				    						  erro.setCodItemSusRealizado(pCodSusRealiz);
				    						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
				    						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
				    						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
				    						  erro.setErro("ERRO AO INSERIR CAMPO MEDICO AUDITOR AIH: : " + msg);
				    						  erro.setFatMensagemLog(new FatMensagemLog(64));
				    						  persistirLogErroCarregandoFatMensagemLog(
													faturamentoFacade, erro);
				    					  } catch (Exception e2) {
				    						  logar(MSG_EXCECAO_IGNORADA, e);
				    					  }
				    				  }
	
				    				  // verifica se foi autorizado pela SMS 29/11/2004
				    				  logar("verifica autorização cma ");
				    				  FatAutorizadoCma autorizado = fatAutorizadoCmaDAO.buscarPrimeiraPorCthCodSus(pCthSeq, rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
				    				  if(autorizado != null){
				    					  vCodSusCma = autorizado.getId().getCodSusCma();
				    				  }
	
				    				  if (fatVariaveisVO.getvMaiorValor()
				    						  && !pPrevia
				    						  && CoreUtil.igual("S",regConta.getIndAutorizadoSms())
				    						  && regConta.getContaHospitalarReapresentada() == null  // não consiste reapres
				    					  	  && !CoreUtil.igual(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pCodSusRealiz)){
				    					  
				    					  logar("verifica autorização cma maior valor {0}", vCodSusCma + " " + vCmaNaoAutorizado);


				    					  if(autorizado == null){
				    						  logar("verifica autorização cma chou");
				    						  vCmaNaoAutorizado = Boolean.TRUE;
	
				    						  try{
					    						  FatLogError erro = new FatLogError();
					    						  erro.setModulo(INT);
					    						  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
					    						  erro.setCriadoEm(new Date());
					    						  erro.setCriadoPor(servidorLogado.getUsuario());
					    						  erro.setDataPrevia(pDataPrevia);
					    						  erro.setCthSeq(pCthSeq);
					    						  erro.setPacCodigo(pPacCodigo);
					    						  erro.setProntuario(pPacProntuario);			    			
					    						  erro.setPhiSeq(pPhiSolic);
					    						  erro.setIphPhoSeq(pPhoSolic);
					    						  erro.setIphSeq(pIphSolic);
					    						  erro.setCodItemSusSolicitado(pCodSusSolic);
					    						  erro.setPhiSeqRealizado(pPhiRealiz);
					    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
					    						  erro.setIphSeqRealizado(pIphRealiz);    			
					    						  erro.setCodItemSusRealizado(pCodSusRealiz);
					    						  erro.setIphPhoSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq());
					    						  erro.setIphSeqItem1(rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
					    						  erro.setCodItemSus1(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus());
					    						  erro.setErro("CMA NÃO AUTORIZADO PELA SMS");
					    						  erro.setFatMensagemLog(new FatMensagemLog(19));
					    						  persistirLogErroCarregandoFatMensagemLog(
														faturamentoFacade, erro);
					    					  } catch (Exception e2) {
					    						  logar(MSG_EXCECAO_IGNORADA, e2);
					    					  }
				    					  }
				    				  }
				    				  // fim verifica se foi autorizado pela SMS  29/11/2004
			    				  } else {
			    					  getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), 
								    						rnCthcAtuGeraAihVO.getRegItem().getPontosSadt());
			    				  } //(vCahSeqp is not null)and(not atingiuLimiteCma)
			    				  //<-------------FIM DO LANCAMENTO DO PROCEDIMENTO NO CMA
			    			  }// for i in 1..rnCthcAtuGeraAihVO.getRegItem().quantidade
			    			  
			    		  } else { //if v_cobranca_cma
			    			  // MARINA AQUI ------------------------------------
			    			  logar("vai lancar item nos SERV PROF: SUS TIPO D {0} PHO {1} IPH {2}",
			    					  rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(),
			    					  rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(),
			    					  rnCthcAtuGeraAihVO.getRegItem().getIphSeq());
			    			  
			    			  // Marina 26/01/2010 - Verifica se já foram inseridos os atos 1 e 6, não deixa inserir o tipo 0
	
			    			  Long vCont = fatAtoMedicoAihDAO.countPorCthCodSusTaoSeq(rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), pCthSeq, atoAnes, atoCirur);
			    			  if(vCont == null){
			    				  vCont = 0l;
			    			  }
			    			  logar("v_cont {0}", vCont);
			    			  
			    			  if(!CoreUtil.igual(vCont, 2)){
					              // Marina 03/12/2009 - Alterado o parâmetro para aceitar 3 dígitos na quantidade conforme novo layout do SISAIH
					              // verifica se a qtde e' maior que 99 (parametro de sistema)- ETB
					              // pois campo qtd na AIH tem 2 digitos
			    				  if(CoreUtil.menorOuIgual(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd)
			    						  // && CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0) Ney 01/09/2011
			    						  && CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vCont) // Marina 06/08/2012
			    				  ){
			    					  // lanca o item com qtd = rnCthcAtuGeraAihVO.getRegItem().quantidade:
			    					  // Lanca procedimento nos ATOS MEDICOS:
			    					  logar("4");
			    					  
			    					  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia, 
			    							  						 pDataPrevia, pPacCodigo, pPacProntuario, 
			    							  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
			    							  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, 
			    							  						 pIphRealiz, pCodSusRealiz, rnCthcAtuGeraAihVO, 
			    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, 
			    							  						 dataFimVinculoServidor, fatVariaveisVO);
			    					  
			    					  if(CoreUtil.menor(res, 0)){ // deu erro no lancamento AAM
					    				  return Boolean.FALSE;
					    			  } else if(CoreUtil.igual(res, 0)){ // estourou qtd aihs, deve sair do loop
					    				  break;
					    			  }
			    					  
			    				  // Ney 01/09/2011  
			    				  } else if(CoreUtil.maior(rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 0)) {
			    					  // guarda os valores originais		  
			    					  Long vQtdIni = rnCthcAtuGeraAihVO.getRegItem().getQuantidade();
					    			  BigDecimal vVlrAnestIni = rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista();
					    			  BigDecimal vVlrProcIni  = rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento();
					    			  BigDecimal vVlrSadtIni  = rnCthcAtuGeraAihVO.getRegItem().getValorSadt();
					    			  BigDecimal vVlrShIni 	  = rnCthcAtuGeraAihVO.getRegItem().getValorServHosp();
					    			  BigDecimal vVlrSpIni 	  = rnCthcAtuGeraAihVO.getRegItem().getValorServProf();
					    			  Long vPtAnestIni 	  = rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista();
					    			  Long vPtCirurIn 	  = rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao();
					    			  Long vPtSadtIni 	  = rnCthcAtuGeraAihVO.getRegItem().getPontosSadt();
					    			  
					    			  // quebra qtde em varias partes de v_max_qtd(acerta vlrs/ptos)		    			  
					    			  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
					    			  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
					    			  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorSadt(),					rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
					    			  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
					    			  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getValorServProf(),			rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd));
					    			  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(),	rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
					    			  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(),		rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
					    			  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(rnCthcAtuGeraAihVO.getRegItem().getPontosSadt(),				rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), vMaxQtd).longValue());
					    			  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(vMaxQtd);
	
					    			  // Marina 03/12/2009 - Alterado o parâmetro para aceitar 3 dígitos na quantidade conforme novo layout do SISAIH
					    			  // lanca varias vezes qtd 99 - ETB
					    			  int vAux = BigDecimal.valueOf(vQtdIni).divide(BigDecimal.valueOf(vMaxQtd), BigDecimal.ROUND_FLOOR).intValue(); 
					    			  for (int i = 1; i <= vAux; i++) {
							              // lanca o item com qtd = v_max_qtd:
							              // Lanca procedimento nos ATOS MEDICOS:
					    				  logar("5");
					    				  
					    				  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia, 
					    						  						 pDataPrevia, pPacCodigo, pPacProntuario, 
					    						  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
					    						  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, pIphRealiz, 
					    						  						 pCodSusRealiz, rnCthcAtuGeraAihVO, 
				    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, 
				    							  						 dataFimVinculoServidor, fatVariaveisVO);
					    				  if(CoreUtil.menor(res, 0)){
					    					  return Boolean.FALSE;
					    				  } else if(CoreUtil.igual(res, 0)){ // estourou qtd aihs, deve sair do loop
					    					  break;
					    				  }
					    			  }
					    			  
					    			  logar("v_qtd_ini: {0}", vQtdIni);
					    			  logar("v_max_qtd: {0}", vMaxQtd);
					    			  logar("v_cont: {0}", vCont);
					    			  
					    			  // marina 06/08/2012 - chamado 462805
					    			  if(vQtdIni > vMaxQtd){
					    				  if(nvl(vQtdIni,0) % nvl(vMaxQtd,0) > 0){ // se tem resto
					    					  // lanca o item com qtd = mod(v_qtd_ini,v_max_qtd)
					    					  Long vQtdAux = nvl(vQtdIni,0) % nvl(vMaxQtd,0);
					    					  rnCthcAtuGeraAihVO.getRegItem().setValorAnestesista(correcaoValorQtd(vVlrAnestIni, vQtdIni, vQtdAux));
					    					  rnCthcAtuGeraAihVO.getRegItem().setValorProcedimento(correcaoValorQtd(vVlrProcIni, vQtdIni, vQtdAux));
					    					  rnCthcAtuGeraAihVO.getRegItem().setValorSadt(correcaoValorQtd(vVlrSadtIni, 		 vQtdIni, vQtdAux));
					    					  rnCthcAtuGeraAihVO.getRegItem().setValorServHosp(correcaoValorQtd(vVlrShIni, 		 vQtdIni, vQtdAux));
					    					  rnCthcAtuGeraAihVO.getRegItem().setValorServProf(correcaoValorQtd(vVlrSpIni, 		 vQtdIni, vQtdAux));
					    					  rnCthcAtuGeraAihVO.getRegItem().setPontosAnestesista(correcaoValorQtd(vPtAnestIni, vQtdIni, vQtdAux).longValue());
					    					  rnCthcAtuGeraAihVO.getRegItem().setPontosCirurgiao(correcaoValorQtd(vPtCirurIn,	 vQtdIni, vQtdAux).longValue());
					    					  rnCthcAtuGeraAihVO.getRegItem().setPontosSadt(correcaoValorQtd(vPtSadtIni, 		 vQtdIni, vQtdAux).longValue());
					    					  rnCthcAtuGeraAihVO.getRegItem().setQuantidade(vQtdAux);
					    					  
					    					  //Lanca procedimento nos ATOS MEDICOS:
					    					  logar("6");
					    					  
					    					  Integer res = rnCthcAtuInsAam( pCthSeq, pDiasConta, pPrevia,
					    							  						 pDataPrevia, pPacCodigo, pPacProntuario, 
					    							  						 pIntSeq, pPhiSolic, pPhoSolic, pIphSolic, 
					    							  						 pCodSusSolic, pPhiRealiz, pPhoRealiz, 
					    							  						 pIphRealiz, pCodSusRealiz, rnCthcAtuGeraAihVO, 
					    							  						 regConta, vMaxQtdePermitida, nomeMicrocomputador, 
					    							  						 dataFimVinculoServidor, fatVariaveisVO);
					    					  if(CoreUtil.menor(res, 0)){
					    						  return Boolean.FALSE;	  
					    					  } else if(CoreUtil.igual(res,0)) { // estourou qtd aihs, deve sair do loop
					    						  break;
					    					  }
					    				  } // mod(qtdIni,v_max_qtd) > 0
					    			  } // marina 06/08/2012 - chamado 462805
			    				  }// rnCthcAtuGeraAihVO.getRegItem().quantidade <= v_max_qtd
			    			  } // IF v_cont <> 2 then  Marina 29/01/2010
			    		  } // v_cobranca_cma
			    	  } else {
			    		  getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, 
			    				  				rnCthcAtuGeraAihVO.getRegItem().getIphPhoSeq(), 
			    				  				rnCthcAtuGeraAihVO.getRegItem().getIphSeq(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getProcedimentoHospitalarSus(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getQuantidade(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getValorServHosp(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getValorServProf(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getValorSadt(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getValorProcedimento(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getValorAnestesista(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getPontosAnestesista(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getPontosCirurgiao(), 
					    						rnCthcAtuGeraAihVO.getRegItem().getPontosSadt());
			    	  } // (v_proced_especial and v_realiz_cobra_especial) or(not v_proced_especial)
		      	}// fim for itemAgrupPorProcComp	  
		      }//fim while iteratorItemAgrupD
		      		      
		      if(Boolean.TRUE.equals(vPsiquiatria)){
		        // Se SSM Realizado for de tratamento em psiquiatria
		        //    o valor SADT deve ser somado
		        //    no valor Serv.Hospitalar e deve aparecer zerado.
		    	  vVlrShRealiz = vVlrShRealiz.add(vVlrSadtRealiz);
		    	  vVlrSadtRealiz = BigDecimal.ZERO;
		    	  rnCthcAtuGeraAihVO.setVlrSh(rnCthcAtuGeraAihVO.getVlrSh().add(rnCthcAtuGeraAihVO.getVlrSadt()));
		    	  rnCthcAtuGeraAihVO.setVlrSadt(BigDecimal.ZERO);
		      } // v_psiquiatria = 'S'

		      // Atualiza valores totais do realizado no espelho AIH:
		      logar("vai fazer update valores do realizado no espelho AIH");
		      logar(" valor_sh_realiz: {0} valor_sp_realiz: {1}", vVlrShRealiz, vVlrSpRealiz);
		      logar(" valor_sadt_realiz: {0} valor_anestesista_realiz: {1} valor_procedimento_realiz: {2}", vVlrSadtRealiz, vVlrAnestRealiz, vVlrProcedRealiz);
		      List<FatEspelhoAih> listaFatEspelhoAih = fatEspelhoAihDAO.listarPorCth(pCthSeq);
		      if(listaFatEspelhoAih != null && !listaFatEspelhoAih.isEmpty()){
		    	  for (FatEspelhoAih fatEspelhoAih : listaFatEspelhoAih) {
		    		  fatEspelhoAih.setValorShRealiz(vVlrShRealiz);
		    		  fatEspelhoAih.setValorSpRealiz(vVlrSpRealiz);
		    		  fatEspelhoAih.setValorSadtRealiz(vVlrSadtRealiz);
		    		  fatEspelhoAih.setValorAnestRealiz(vVlrAnestRealiz);
		    		  fatEspelhoAih.setValorProcedRealiz(vVlrProcedRealiz);
		    		  faturamentoFacade.atualizarFatEspelhoAih(rnCthcAtuGeraAihVO.getRegEspelho());
		    		 // faturamentoFacade.evict(fatEspelhoAih);
		    	  }
		      }
		      
		      // Atualiza totais na conta
		      logar("vai fazer update dos valores totais na conta...");
		      logar("valor_sh: {0}+{1}={2}", rnCthcAtuGeraAihVO.getVlrSh(), vVlrSadtRealiz, (rnCthcAtuGeraAihVO.getVlrSh().add(vVlrSadtRealiz)));
		      logar("valor_sp: {0}+{1}={2}", rnCthcAtuGeraAihVO.getVlrSp(), vVlrSpRealiz, (rnCthcAtuGeraAihVO.getVlrSp().add(vVlrSpRealiz)));
		      logar("valor_sadt: {0}+{1}={2}", rnCthcAtuGeraAihVO.getVlrSadt(), vVlrSadtRealiz, (rnCthcAtuGeraAihVO.getVlrSadt().add(vVlrSadtRealiz)));
		      logar("valor_anestesist: {0}+{1}={2}", rnCthcAtuGeraAihVO.getVlrAnest(), vVlrAnestRealiz, (rnCthcAtuGeraAihVO.getVlrAnest().add(vVlrAnestRealiz)));
		      logar("valor_procedimento: {0}+{1}={2}", rnCthcAtuGeraAihVO.getVlrProced(), vVlrProcedRealiz, (rnCthcAtuGeraAihVO.getVlrProced().add(vVlrProcedRealiz)));
		      logar("valor_utie: {0} valor_rn: {1} valor_hemat: {2}", rnCthcAtuGeraAihVO.getVlrUtie(), rnCthcAtuGeraAihVO.getVlrRn(), rnCthcAtuGeraAihVO.getVlrHemat());
		      logar("vlor_transp: {0} valor_opm: {1}", rnCthcAtuGeraAihVO.getVlrTransp(), rnCthcAtuGeraAihVO.getVlrOpm());
		      logar("pontos: anestesista {0} cirurgiao {1} sadt {2}", rnCthcAtuGeraAihVO.getPtosAnest(), rnCthcAtuGeraAihVO.getPtosCirur(), rnCthcAtuGeraAihVO.getPtosSadt());
		      
		      FatContasHospitalares contaHospitalarToUpdate = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
		      FatContasHospitalares contaOld = null;
		      try{
		    	  contaOld = faturamentoFacade.clonarContaHospitalar(contaHospitalarToUpdate);
		      }catch (Exception e) {
		    	  logError("Exceção capturada: ", e);
		    	  throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		      }				
		      contaHospitalarToUpdate.setValorSh(rnCthcAtuGeraAihVO.getVlrSh().add(vVlrShRealiz));
		      contaHospitalarToUpdate.setValorSp(rnCthcAtuGeraAihVO.getVlrSp().add(vVlrSpRealiz));
		      contaHospitalarToUpdate.setValorSadt(rnCthcAtuGeraAihVO.getVlrSadt().add(vVlrSadtRealiz));
		      contaHospitalarToUpdate.setValorAnestesista(rnCthcAtuGeraAihVO.getVlrAnest().add(vVlrAnestRealiz));
		      contaHospitalarToUpdate.setValorProcedimento(rnCthcAtuGeraAihVO.getVlrProced().add(vVlrProcedRealiz));
		      contaHospitalarToUpdate.setValorUtie(rnCthcAtuGeraAihVO.getVlrUtie());
		      contaHospitalarToUpdate.setValorRn(rnCthcAtuGeraAihVO.getVlrRn());
		      contaHospitalarToUpdate.setValorHemat(rnCthcAtuGeraAihVO.getVlrHemat());
		      contaHospitalarToUpdate.setValorTransp(rnCthcAtuGeraAihVO.getVlrTransp());
		      contaHospitalarToUpdate.setValorOpm(rnCthcAtuGeraAihVO.getVlrOpm());
		      contaHospitalarToUpdate.setPontosAnestesista(rnCthcAtuGeraAihVO.getPtosAnest());
		      contaHospitalarToUpdate.setPontosCirurgiao(rnCthcAtuGeraAihVO.getPtosCirur());
		      contaHospitalarToUpdate.setPontosSadt(rnCthcAtuGeraAihVO.getPtosSadt());
		      		       
		      // insere demais valores da conta
		      try{
		    	  boolean inserir = false;
		    	  FatValorContaHospitalar valoresConta = contaHospitalarToUpdate.getValorContaHospitalar();
		    	  if(valoresConta == null){
		    		  valoresConta = new FatValorContaHospitalar();
		    		  inserir = true;

		    	  }
		    	  valoresConta.setCthSeq(pCthSeq);
		    	  valoresConta.setContaHospitalar(contaHospitalarToUpdate);
		    	  valoresConta.setValorShUtie(rnCthcAtuGeraAihVO.getVlrShUtie());
		    	  valoresConta.setValorSpUtie(rnCthcAtuGeraAihVO.getVlrSpUtie());
		    	  valoresConta.setValorSadtUtie(rnCthcAtuGeraAihVO.getVlrSadtUtie());
		    	  valoresConta.setValorShRn(rnCthcAtuGeraAihVO.getVlrShRn());
		    	  valoresConta.setValorSpRn(rnCthcAtuGeraAihVO.getVlrSpRn());
		    	  valoresConta.setValorSadtRn(rnCthcAtuGeraAihVO.getVlrSadtRn());
		    	  valoresConta.setValorShHemat(rnCthcAtuGeraAihVO.getVlrShHemat());
		    	  valoresConta.setValorSpHemat(rnCthcAtuGeraAihVO.getVlrSpHemat());
		    	  valoresConta.setValorSadtHemat(rnCthcAtuGeraAihVO.getVlrSadtHemat());
		    	  valoresConta.setValorShTransp(rnCthcAtuGeraAihVO.getVlrShTransp());
		    	  valoresConta.setValorSpTransp(rnCthcAtuGeraAihVO.getVlrSpTransp());
		    	  valoresConta.setValorSadtTransp(rnCthcAtuGeraAihVO.getVlrSadtTransp());
		    	  valoresConta.setValorShOpm(rnCthcAtuGeraAihVO.getVlrShOpm());
		    	  valoresConta.setValorSpOpm(rnCthcAtuGeraAihVO.getVlrSpOpm());
		    	  valoresConta.setValorSadtOpm(rnCthcAtuGeraAihVO.getVlrSadtOpm());
		    	  valoresConta.setValorProcOpm(rnCthcAtuGeraAihVO.getVlrProcOpm());		 
		    	  valoresConta.setValorShUti(BigDecimal.ZERO);
		    	  valoresConta.setValorSpUti(BigDecimal.ZERO);
		    	  valoresConta.setValorSadtUti(BigDecimal.ZERO);
		    	  valoresConta.setValorShAcomp(BigDecimal.ZERO);
		    	  valoresConta.setValorSpAcomp(BigDecimal.ZERO);
		    	  valoresConta.setValorSadtAcomp(BigDecimal.ZERO);
		    	  
		    	  if(inserir){
		    		  faturamentoFacade.inserirFatValorContaHospitalar(valoresConta, false);
		    	  } else {
		    		  faturamentoFacade.atualizarFatValorContaHospitalar(valoresConta, false);
		    	  }
		    	  contaHospitalarToUpdate.setValorContaHospitalar(valoresConta);
		      } catch (Exception e) {
		    	  logar("erro no insert vlr_cth");
		    	  String msg = e.getMessage();
		    	  try{
					  FatLogError erro = new FatLogError();
					  erro.setModulo(INT);
					  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
					  erro.setCriadoEm(new Date());
					  erro.setCriadoPor(servidorLogado.getUsuario());
					  erro.setDataPrevia(pDataPrevia);
					  erro.setCthSeq(pCthSeq);
					  erro.setPacCodigo(pPacCodigo);
					  erro.setProntuario(pPacProntuario);			    			
					  erro.setPhiSeq(pPhiSolic);
					  erro.setIphPhoSeq(pPhoSolic);
					  erro.setIphSeq(pIphSolic);
					  erro.setCodItemSusSolicitado(pCodSusSolic);
					  erro.setPhiSeqRealizado(pPhiRealiz);
					  erro.setIphPhoSeqRealizado(pPhoRealiz);
					  erro.setIphSeqRealizado(pIphRealiz);    			
					  erro.setCodItemSusRealizado(pCodSusRealiz);					  
					  erro.setErro("ERRO AO INSERIR VALOR CONTA HOSPITALAR: " + msg);
					  erro.setFatMensagemLog(new FatMensagemLog(76));
					  persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
							erro);
				  } catch (Exception e2) {
					  logar(MSG_EXCECAO_IGNORADA, e2);
				  }
				  return Boolean.FALSE;
		      }
		      
		      faturamentoFacade.persistirContaHospitalar(contaHospitalarToUpdate, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
		      
		      // Ney 20111229, para garantir saida de variavel de package inicilizada
		      vMaxQtdePermitida = Long.valueOf("0");
		      
		      // calcula valores de diarias (acompanhante e UTI nao especial)
		      logar("vai calcular valores de diarias...");
		      
		      if(!this.rnCthcAtuVlrDiar(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor)){
		    	  logar("RETORNOU FALSO NAS DIARIAS...");
		    	  return Boolean.FALSE;
		      }		      
			} else {
				try{
				  FatLogError erro = new FatLogError();
				  erro.setModulo(INT);
				  erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
				  erro.setCriadoEm(new Date());
				  erro.setCriadoPor(servidorLogado.getUsuario());
				  erro.setDataPrevia(pDataPrevia);
				  erro.setCthSeq(regConta.getSeq());
				  erro.setErro("NAO ENCONTROU DADOS DO PRIMEIRO ESPELHO AIH PARA MONTAGEM DOS ESPELHOS.");
				  erro.setFatMensagemLog(new FatMensagemLog(133));
				  persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
				} catch (Exception e2) {
				  logar(MSG_EXCECAO_IGNORADA, e2);
				} 
				return Boolean.FALSE;
			} // reg_espelho.seqp is not null
		} else {
			try{
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma(RN_CTHC_ATU_GERA_AIH);
				erro.setCriadoEm(new Date());
		  		erro.setCriadoPor(servidorLogado.getUsuario());
		  		erro.setDataPrevia(pDataPrevia);
		  		erro.setCthSeq(pCthSeq);
		  		erro.setErro("NAO ENCONTROU DADOS DA CONTA PARA MONTAGEM DO ESPELHO AIH.");
		  		erro.setFatMensagemLog(new FatMensagemLog(131));
		  		persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e2) {
				logar(MSG_EXCECAO_IGNORADA, e2);
			} 
			return Boolean.FALSE;
		} // reg_conta.seq is not null
		
		if(Boolean.FALSE.equals(pPrevia)){
			if(CoreUtil.igual("S", (String) CoreUtil.nvl(regConta.getIndAutorizadoSms(),"N")) && 
					Boolean.TRUE.equals(vCmaNaoAutorizado)){
				vCmaNaoAutorizado = Boolean.FALSE;				
				logar("vai retornar falso no cma ");
				return Boolean.FALSE;
			}
		}
		logar("vai retornar verdadeiro no cma ");
		vCmaNaoAutorizado = Boolean.FALSE;
		return Boolean.TRUE;
	}

	/**
	 * ORADB Procedure FATK_CTHN_RN_UN.RN_CTHC_ATU_INS_AAM
	 * @param fatVariaveisVO 
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	protected Integer rnCthcAtuInsAam(Integer pCthSeq
			 ,Integer pDiasConta
			 ,Boolean pPrevia
			 ,Date pDataPrevia
			 ,Integer pPacCodigo
			 ,Integer pPacProntuario
			 ,Integer pIntSeq
			 ,Integer pPhiSolic
			 ,Short pPhoSolic
			 ,Integer pIphSolic
			 ,Long pCodSusSolic
			 ,Integer pPhiRealiz
			 ,Short pPhoRealiz
			 ,Integer pIphRealiz
			 ,Long pCodSusRealiz
			 ,RnCthcAtuGeraAihVO valores
			 ,FatContasHospitalares regConta
			 ,Long vMaxQtdePermitida, 
			 String nomeMicrocomputador, 
			 final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatAtoMedicoAihDAO fatAtoMedicoAihDAO = getFatAtoMedicoAihDAO();
		final FatCaractItemProcHospDAO fatCaractItemProcHospDAO = getFatCaractItemProcHospDAO();
		final FatAutorizadoCmaDAO fatAutorizadoCmaDAO = getFatAutorizadoCmaDAO();
		
		//------------->LANCA PROCEDIMENTO NOS SERV PROFS:
		// calcula seq p/ ato medico
		Byte vAamSeqp = fatAtoMedicoAihDAO.buscaProximaSeq(pCthSeq, valores.getEaiSeqp());
		logar("next aam_seqp: {0}", vAamSeqp);		
		if(vAamSeqp != null && CoreUtil.maior(vAamSeqp, valores.getMaxAam())){
		    // atingiu a quantidade maxima de atos medicos
		    // e deve inserir outro espelho aih:
		    // calcula seq p/espelho aih
			valores.setEaiSeqp(fatEspelhoAihDAO.buscaProximaSeqTabelaEspelho(pCthSeq));
		    logar("next eai_seqp: {0}", valores.getEaiSeqp());
			
		    if(valores.getEaiSeqp() != null && CoreUtil.maior(valores.getEaiSeqp(), valores.getMaxEai())){
		    	// atingiu nro maximo de aihs de continuacao
		    	// deve parar de lancar procedimentos
		    	logar("2 atingiu numero maximo de aihs de continuacao");
		    	return 0; // sair do loop na funcao que chamou
		    } // (vEaiSeqp is not null) and (vEaiSeqp > vMaxEai)
		    
		    if(valores.getEaiSeqp() != null){
		    	try {
		    		logar("insere espelho aih: eai_seqp {0}", valores.getEaiSeqp());
		    		// insere novo espelho aih
		    		FatEspelhoAih espelhoAih = new FatEspelhoAih();
		    		espelhoAih.setId(new FatEspelhoAihId(valores.getRegEspelho().getId().getCthSeq(), valores.getEaiSeqp()));
		    		espelhoAih.setTahSeq(buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_AIH_CONTINUACAO));
		    		espelhoAih.setDataPrevia(pDataPrevia);			    		
		    		espelhoAih.setNroDiasMesInicialUti(valores.getRegEspelho().getNroDiasMesInicialUti());
		    		espelhoAih.setNroDiasMesAnteriorUti(valores.getRegEspelho().getNroDiasMesAnteriorUti());
		    		espelhoAih.setNroDiasMesAltaUti(valores.getRegEspelho().getNroDiasMesAltaUti());
		    		espelhoAih.setNroDiariasAcompanhante(valores.getRegEspelho().getNroDiariasAcompanhante());	
		    		espelhoAih.setDciCodigoDcih(valores.getRegEspelho().getDciCodigoDcih());
		    		espelhoAih.setDciCpeDtHrInicio(valores.getRegEspelho().getDciCpeDtHrInicio());
		    		espelhoAih.setDciCpeModulo(valores.getRegEspelho().getDciCpeModulo());
		    		espelhoAih.setDciCpeMes(valores.getRegEspelho().getDciCpeMes());
		    		espelhoAih.setDciCpeAno(valores.getRegEspelho().getDciCpeAno());
		            espelhoAih.setEspecialidadeDcih(valores.getRegEspelho().getEspecialidadeDcih());
		            espelhoAih.setPacProntuario(valores.getRegEspelho().getPacProntuario());
		            espelhoAih.setPacNome(valores.getRegEspelho().getPacNome());
		            espelhoAih.setEndLogradouroPac(valores.getRegEspelho().getEndLogradouroPac());
		            espelhoAih.setEndNroLogradouroPac(valores.getRegEspelho().getEndNroLogradouroPac());
		            espelhoAih.setEndCmplLogradouroPac(valores.getRegEspelho().getEndCmplLogradouroPac());
		            espelhoAih.setEndCidadePac(valores.getRegEspelho().getEndCidadePac());
		            espelhoAih.setEndUfPac(valores.getRegEspelho().getEndUfPac());
		            espelhoAih.setEndCepPac(valores.getRegEspelho().getEndCepPac());
		            espelhoAih.setPacDtNascimento(valores.getRegEspelho().getPacDtNascimento());
		            espelhoAih.setPacSexo(valores.getRegEspelho().getPacSexo());
		            espelhoAih.setNumeroAih(valores.getRegEspelho().getNumeroAih());
		            espelhoAih.setEnfermaria(valores.getRegEspelho().getEnfermaria());
		            espelhoAih.setLeito(valores.getRegEspelho().getLeito());
		            espelhoAih.setNomeResponsavelPac(valores.getRegEspelho().getNomeResponsavelPac());
		            espelhoAih.setPacCpf(valores.getRegEspelho().getPacCpf());
		            espelhoAih.setPacNroCartaoSaude(valores.getRegEspelho().getPacNroCartaoSaude());
		            espelhoAih.setCpfMedicoSolicRespons(valores.getRegEspelho().getCpfMedicoSolicRespons());
		            espelhoAih.setIphPhoSeqSolic(valores.getRegEspelho().getIphPhoSeqSolic());
		            espelhoAih.setIphSeqSolic(valores.getRegEspelho().getIphSeqSolic());
		            espelhoAih.setIphCodSusSolic(valores.getRegEspelho().getIphCodSusSolic());
		            espelhoAih.setIphPhoSeqRealiz(valores.getRegEspelho().getIphPhoSeqRealiz());
		            espelhoAih.setIphSeqRealiz(valores.getRegEspelho().getIphSeqRealiz());
		            espelhoAih.setIphCodSusRealiz(valores.getRegEspelho().getIphCodSusRealiz());
		            espelhoAih.setTciCodSus(valores.getRegEspelho().getTciCodSus());
		            espelhoAih.setAihDthrEmissao(valores.getRegEspelho().getAihDthrEmissao());
		            espelhoAih.setEspecialidadeAih(valores.getRegEspelho().getEspecialidadeAih());
		            espelhoAih.setDataInternacao(valores.getRegEspelho().getDataInternacao());
		            espelhoAih.setDataSaida(valores.getRegEspelho().getDataSaida());
		            espelhoAih.setCidPrimario(valores.getRegEspelho().getCidPrimario());
		            espelhoAih.setCidSecundario(valores.getRegEspelho().getCidSecundario());
		            espelhoAih.setNascidosVivos(valores.getRegEspelho().getNascidosVivos());
		            espelhoAih.setNascidosMortos(valores.getRegEspelho().getNascidosMortos());
		            espelhoAih.setSaidasAlta(valores.getRegEspelho().getSaidasAlta());
		            espelhoAih.setSaidasTransferencia(valores.getRegEspelho().getSaidasTransferencia());
		            espelhoAih.setSaidasObito(valores.getRegEspelho().getSaidasObito());
		            espelhoAih.setNumeroAihPosterior(valores.getRegEspelho().getNumeroAihPosterior());
		            espelhoAih.setNumeroAihAnterior(valores.getRegEspelho().getNumeroAihAnterior());
		            espelhoAih.setCodIbgeCidadePac(valores.getRegEspelho().getCodIbgeCidadePac());
		            espelhoAih.setGrauInstrucaoPac(valores.getRegEspelho().getGrauInstrucaoPac());
		            espelhoAih.setInfeccaoHospitalar(valores.getRegEspelho().getInfeccaoHospitalar());
		            espelhoAih.setMotivoCobranca(valores.getRegEspelho().getMotivoCobranca());
		            espelhoAih.setNacionalidadePac(valores.getRegEspelho().getNacionalidadePac());
		            espelhoAih.setExclusaoCritica(valores.getRegEspelho().getExclusaoCritica());
		            espelhoAih.setCpfMedicoAuditor(valores.getRegEspelho().getCpfMedicoAuditor());
		            espelhoAih.setIndDocPac(valores.getRegEspelho().getIndDocPac());
		            espelhoAih.setCnsMedicoAuditor(valores.getRegEspelho().getCnsMedicoAuditor()); // MARINA 17/01/2012
		            espelhoAih.setPacRG(valores.getRegEspelho().getPacRG()); // Marina 07/11/2013 -  Melhoria AGHU 31577		           
		            faturamentoFacade.inserirFatEspelhoAih(espelhoAih, true, nomeMicrocomputador, dataFimVinculoServidor);
//		            faturamentoFacade.evict(espelhoAih);
		            this.flush();
		    	} catch (Exception e) {
					logar("erro no insert eai");
					String msg = e.getMessage();
					try{
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_ATU_INS_AAM);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
					  	erro.setCthSeq(pCthSeq);
					  	erro.setPacCodigo(pPacCodigo);
					  	erro.setProntuario(pPacProntuario);
					  	erro.setPhiSeq(pPhiSolic);
					  	erro.setIphPhoSeq(pPhoSolic);
					  	erro.setIphSeq(pIphSolic);
					  	erro.setCodItemSusSolicitado(pCodSusSolic);
					  	erro.setPhiSeqRealizado(pPhiRealiz);
					  	erro.setIphPhoSeqRealizado(pPhoRealiz);
					  	erro.setIphSeqRealizado(pIphRealiz);
					  	erro.setCodItemSusRealizado(pCodSusRealiz);
					  	erro.setErro("ERRO AO INSERIR ESPELHO AIH: " + msg);
					  	erro.setFatMensagemLog(new FatMensagemLog(66));
					  	persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e2) {
						logar(MSG_EXCECAO_IGNORADA, e2);
					} 
		    		return -1; // sair com erro da funcao que chamou
		    	}
		    	
		    	// calcula seq p/ato medico
		    	vAamSeqp = fatAtoMedicoAihDAO.buscaProximaSeq(pCthSeq, valores.getEaiSeqp());
				logar("next aam_seqp: {0}", vAamSeqp);
		    } // (vEaiSeqp is not null)
		} // (vAamSeqp is not null) and (vAamSeqp > vMaxAam)

		if(vAamSeqp != null){
			try {
				logar("insere ato medico: aam_seqp {0} reg_item.tao_seq: {1}", vAamSeqp, valores.getRegItem().getTaoSeq());
				
				if(valores.getRegItem().getTaoSeq() != null && CoreUtil.notIn(valores.getRegItem().getTaoSeq(), valores.getCodAtoHemat(), valores.getCodAtoOpm())){
					// procedimento nao e' HEMOTERAPIA / OPM
			        valores.getRegItem().setValorAnestesista(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO).multiply(nvl(valores.getPercFideps(), BigDecimal.ZERO)));
			        valores.getRegItem().setValorProcedimento(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO).multiply(nvl(valores.getPercFideps(), BigDecimal.ZERO)));
			        valores.getRegItem().setValorSadt(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO).multiply(nvl(valores.getPercFideps(), BigDecimal.ZERO)));
			        valores.getRegItem().setValorServHosp(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO).multiply(nvl(valores.getPercFideps(), BigDecimal.ZERO)));
			        valores.getRegItem().setValorServProf(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO).multiply(nvl(valores.getPercFideps(), BigDecimal.ZERO)));
				}

		      // Marina 29/01/2010
		      // Solicitação do Gilberto
		      // Para procedimentos especiais e ato Obrigatório.
		      // os valores do tipo de ato igual a 6 deve ser gerado zerado.
		      logar("MARINA {0}{1}  {2}  {3}  {4}  {5}", 
		    		  valores.getRegItem().getTaoSeq(), 
		    		  valores.getRegItem().getValorAnestesista(), 
		    		  valores.getRegItem().getValorProcedimento(), 
		    		  valores.getRegItem().getValorSadt(), 
		    		  valores.getRegItem().getValorServHosp(), 
		    		  valores.getRegItem().getValorServProf());
		      
		      final Byte atoAnes = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);
		      if(CoreUtil.igual(atoAnes,valores.getRegItem().getTaoSeq())){
		    	  valores.getRegItem().setValorAnestesista(BigDecimal.ZERO);
		    	  valores.getRegItem().setValorProcedimento(BigDecimal.ZERO);
		    	  valores.getRegItem().setValorSadt(BigDecimal.ZERO);
		    	  valores.getRegItem().setValorServHosp(BigDecimal.ZERO);
		    	  valores.getRegItem().setValorServProf(BigDecimal.ZERO);
		      }
		      
		      
		      // Chamado - 73613
		      // Marina 26/06/2012
		      if(valores.getRegItem().getCpfCns() != null){
		    	  // Testa se o responsável tem cns preenchido, se não tem, retorna erro.
		    	  
		    	  logar(" Passa CPF para retornar CNS: {0}", valores.getRegItem().getCpfCns());		    	  
		    	  
		    	  String vCns = aipcGetCnsResp(valores.getRegItem().getCpfCns());
		    	  logar(" retorno da função v_cns: {0}", vCns);
		    	  final int dt  =  Integer.parseInt(DateUtil.obterDataFormatada(regConta.getDataInternacaoAdministrativa(), "yyyyMM"));
		    	  logar(" DATA_INT: {0}", dt);
		    	  
		    	  if(vCns == null && dt > CONST_201201){
		    		  try{
							FatLogError erro = new FatLogError();
							erro.setModulo(INT);
							erro.setPrograma(RN_CTHC_ATU_INS_AAM);
							erro.setCriadoEm(new Date());
							erro.setCriadoPor(servidorLogado.getUsuario());
							erro.setDataPrevia(pDataPrevia);
							erro.setCthSeq(pCthSeq);
							erro.setPacCodigo(pPacCodigo);
							erro.setProntuario(pPacProntuario);
							erro.setPhiSeq(pPhiSolic);
							erro.setIphPhoSeq(pPhoSolic);
							erro.setIphSeq(pIphSolic);
							erro.setCodItemSusSolicitado(pCodSusSolic);
							erro.setPhiSeqRealizado(pPhiRealiz);
							erro.setIphPhoSeqRealizado(pPhoRealiz);
							erro.setIphSeqRealizado(pIphRealiz);
							erro.setCodItemSusRealizado(pCodSusRealiz);
						  	erro.setErro("NAO ENCONTROU CNS DO RESPONSAVEL");
						  	erro.setFatMensagemLog(new FatMensagemLog(119));
						  	persistirLogErroCarregandoFatMensagemLog(
									faturamentoFacade, erro);
						} catch (Exception e2) {
							logar(MSG_EXCECAO_IGNORADA, e2);
						} 
						
						if(Boolean.FALSE.equals(pPrevia)){
							return -1; // sair com erro da funcao que chamou
						}
		    	  }
		      }
		      
		      logar("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX reg_item.iph_seq: {0} SUS {1} PHO {2} IPH {3}PHI {4}",
		    		  valores.getRegItem().getIphSeq(), 
		    		  valores.getRegItem().getProcedimentoHospitalarSus(),
		    		  valores.getRegItem().getIphPhoSeq(),
		    		  valores.getRegItem().getIphSeq(),
		    		  pPhiRealiz);
		    	
		      final List<Integer> listAtoProt = fatAtoMedicoAihDAO.listarPorIphCodSusCthGrpSit( buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO), 
									    		  												valores.getRegItem().getIphSeq(), 
									    		  												DominioSituacao.A, 
									    		  												pCthSeq, 
									    		  												buscarVlrShortAghParametro(AghuParametrosEnum.P_GRUPO_OPM));
		      

		      // if c_ato_prot%notfound or c_ato_prot%found and v_max_qtde_permitida > 1
		      if( (listAtoProt == null || listAtoProt.isEmpty()) ||
		    		  // Ney, 20111229 Permite duplicidade se qtd maxima permitida > 1  
		    		  (listAtoProt != null && !listAtoProt.isEmpty() && vMaxQtdePermitida > 1) 	
		      		){
		    	  logar("vai lancar ato medico: aam_seqp {0} SUS {1} PHO {2} IPH {3}PHI {4} quantidade Marina : {5}", 
		    			  vAamSeqp, 
		    			  valores.getRegItem().getProcedimentoHospitalarSus(),
		    			  valores.getRegItem().getIphPhoSeq(),
		    			  valores.getRegItem().getIphSeq(),
		    			  pPhiRealiz,
		    			  valores.getRegItem().getQuantidade());

		    	  FatAtoMedicoAih atoMedAih = new FatAtoMedicoAih();
		    	  atoMedAih.setId(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedAih.setDataPrevia(pDataPrevia);
		    	  atoMedAih.setIphCodSus(valores.getRegItem().getProcedimentoHospitalarSus());

		    	  
		    	  if(valores.getRegItem().getIphPhoSeq()!=null && valores.getRegItem().getIphSeq()!=null){
		    		  FatItensProcedHospitalar fatItensProcedHospitalar = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(new FatItensProcedHospitalarId(valores.getRegItem().getIphPhoSeq(), valores.getRegItem().getIphSeq()));
		    		  atoMedAih.setItemProcedimentoHospitalar(fatItensProcedHospitalar);
		    	  }
		    	 
		    	  if(valores.getRegItem().getTivSeq()!=null){
			    	  FatTiposVinculo tiposVinculo = getFatTiposVinculoDAO().getTiposVinculoPeloCodigoSus(valores.getRegItem().getTivSeq().intValue());		    	  
			    	  atoMedAih.setFatTiposVinculo(tiposVinculo);		    	  
		    	  }
		    	  
		    	  if(valores.getRegItem().getTaoSeq()!=null){
		    		  FatTipoAto tipoAto = getFatTipoAtoDAO().getTipoAtoPeloCodigoSus(valores.getRegItem().getTaoSeq());		    	  
		    		  atoMedAih.setFatTipoAto(tipoAto);
		    	  }		    	  
		    	  atoMedAih.setQuantidade(valores.getRegItem().getQuantidade().shortValue());
		    	  atoMedAih.setNotaFiscal(valores.getRegItem().getNotaFiscal());		    	  
		    	  atoMedAih.setValorAnestesista(valores.getRegItem().getValorAnestesista());
		    	  atoMedAih.setValorProcedimento(valores.getRegItem().getValorProcedimento());
		    	  atoMedAih.setValorSadt(valores.getRegItem().getValorSadt());
		    	  atoMedAih.setValorServHosp(valores.getRegItem().getValorServHosp());
		    	  atoMedAih.setValorServProf(valores.getRegItem().getValorServProf());		    	  
		    	  atoMedAih.setPontosAnestesista(valores.getRegItem().getPontosAnestesista().intValue());
		    	  atoMedAih.setPontosCirurgiao(valores.getRegItem().getPontosCirurgiao().intValue());
		    	  atoMedAih.setPontosSadt(valores.getRegItem().getPontosSadt().intValue());		    	  
		    	  atoMedAih.setIndModoCobranca(valores.getRegItem().getIndModoCobranca());
		    	  atoMedAih.setIndConsistente(DominioTipoItemConta.valueOf(valores.getRegItem().getIndLocalSoma().toString()));
		    	  atoMedAih.setCompetenciaUti(valores.getRegItem().getCompetenciaUti());
		    	  atoMedAih.setIndEquipe(valores.getRegItem().getIndEquipe());
		    	  atoMedAih.setCbo(valores.getRegItem().getCbo()); 		 // ETB 06012008
		    	  atoMedAih.setCpfCns(valores.getRegItem().getCpfCns()); // ETB 06012008
		    	  atoMedAih.setSerieOpm(valores.getRegItem().getSerieOpm());
		    	  atoMedAih.setLoteOpm(valores.getRegItem().getLoteOpm());
		    	  atoMedAih.setRegAnvisaOpm(valores.getRegItem().getRegAnvisaOpm());
		    	  atoMedAih.setCnpjRegAnvisa(valores.getRegItem().getCnpjRegAnvisa());
		    	  atoMedAih.setCgc(valores.getRegItem().getCgc());
		    	  faturamentoFacade.inserirAtoMedicoAih(atoMedAih, true, nomeMicrocomputador, dataFimVinculoServidor);
		    	//  faturamentoFacade.evict(atoMedAih);
		    	  faturamentoFacade.clear();
		      } else {
					try{
						FatLogError erro = new FatLogError();
						erro.setModulo(INT);
						erro.setPrograma(RN_CTHC_ATU_INS_AAM);
						erro.setCriadoEm(new Date());
						erro.setCriadoPor(servidorLogado.getUsuario());
						erro.setDataPrevia(pDataPrevia);
					  	erro.setCthSeq(pCthSeq);
					  	erro.setPacCodigo(pPacCodigo);
					  	erro.setProntuario(pPacProntuario);
					  	erro.setPhiSeq(pPhiSolic);
					  	erro.setIphPhoSeq(pPhoSolic);
					  	erro.setIphSeq(pIphSolic);
					  	erro.setCodItemSusSolicitado(valores.getRegItem().getProcedimentoHospitalarSus());
					  	erro.setPhiSeqRealizado(pPhiRealiz);
					  	erro.setIphPhoSeqRealizado(pPhoRealiz);
					  	erro.setIphSeqRealizado(pIphRealiz);
					  	erro.setCodItemSusRealizado(pCodSusRealiz);
					  	erro.setIphPhoSeqItem1(valores.getRegItem().getIphPhoSeq());
					  	erro.setIphSeqItem1(valores.getRegItem().getIphSeq());
					  	erro.setCodItemSus1(valores.getRegItem().getProcedimentoHospitalarSus());					  	
					  	erro.setErro("PROCEDIMENTO ORTESE E PROTESE EM DUPLICIDADE");
					  	erro.setFatMensagemLog(new FatMensagemLog(210));
					  	persistirLogErroCarregandoFatMensagemLog(
								faturamentoFacade, erro);
					} catch (Exception e2) {
						logar(MSG_EXCECAO_IGNORADA, e2);
					}
					return -1; // sair com erro da funcao que chamou
		      }

		      
		      // Inicio Marina 06/05/2009 - Verifica se tem caracteristica -'Deve ser autorizado pela SMS' para autorização SMS
		      List<FatCaractItemProcHosp> listaCaract = fatCaractItemProcHospDAO.listarPorIphCaracteristica(valores.getRegItem().getIphSeq(), valores.getRegItem().getIphPhoSeq(), DominioFatTipoCaractItem.DEVE_SER_AUTORIZADO_PELA_SMS);
		      if(listaCaract != null && !listaCaract.isEmpty()){
		    	  
		    	  // ETB 17/01/2008 Verifica autorização SMS
		    	  /*
		    	   * eSchweigert 22/11/2012
		    	  final Short grupoOpm = buscarVlrShortAghParametro(AghuParametrosEnum.P_GRUPO_OPM);
		    	  Short primCaracPHS = valores.getRegItem().getProcedimentoHospitalarSus() != null ? Short.valueOf(StringUtils.substring(valores.getRegItem().getProcedimentoHospitalarSus().toString(),0,1)) : null;
		    	   */
		    	  
		    	  BigDecimal somatorio = nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
		    	  							.add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
		    	  							.add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
		    	  							.add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
		    	  							.add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO));
		    	  
		    	  
		    	  Long codSusCma = null;
		    	  Long vQtdProcCma = null;
		    	  String indAutorizadoSms = null; 
		    	  Integer cthSeqReapresentada = null;
		    	  if(CoreUtil.maior(somatorio, BigDecimal.ZERO)){
		    		  	/* eSchweigert 22/11/2012
		    		  	 * 	            -- and substr(reg_item.procedimento_hospitalar_sus,1,1) <>
	            						-- AGHC_OBTEM_PARAMETRO('P_GRUPO_OPM')
	            						
		    		  			&& !CoreUtil.igual(primCaracPHS, grupoOpm)){
		    		  		*/
		    		  
		    		  // Deve ser autorizado
		    		  logar("Deve ser autorizada {0}", codSusCma);
		    		  
		    		  final CursorCAutorizadoCMSVO autorizado = fatAutorizadoCmaDAO.buscarPrimeiraPorCthCodSusComSomatorio(pCthSeq, valores.getRegItem().getProcedimentoHospitalarSus());
    				  if(autorizado != null){
    					  codSusCma = autorizado.getCodSusCMA();
    					  vQtdProcCma = autorizado.getQtdeProcCma().longValue();
    				  }
    				  
    				  FatContasHospitalares regContaAut = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
    				  if(regContaAut != null){
    					  indAutorizadoSms = regContaAut.getIndAutorizadoSms(); 
    			    	  cthSeqReapresentada = regContaAut.getContaHospitalarReapresentada() != null ? regContaAut.getContaHospitalarReapresentada().getSeq() : null;
    				  }

    				  logar("reg_item.quantidade: {0} v_qtd_proc_cma: {1} reg_item.procedimento_hospitalar_sus: {2} competência {3}",
    						  valores.getRegItem().getQuantidade(), vQtdProcCma,
    						  valores.getRegItem().getProcedimentoHospitalarSus(), 
    						  valores.getRegItem().getCompetenciaUti());
    				  
    				  if(vQtdProcCma != null && CoreUtil.maior(valores.getRegItem().getQuantidade(), vQtdProcCma) && 
    						  !getFaturamentoRN().verificarCaracteristicaExame(valores.getRegItem().getIphSeq(), valores.getRegItem().getIphPhoSeq(), DominioFatTipoCaractItem.DIARIA_UTI) &&
    						  CoreUtil.igual("S",indAutorizadoSms)){
    					  try{
    						  FatLogError erro = new FatLogError();
    						  erro.setModulo(INT);
    						  erro.setPrograma(RN_CTHC_ATU_INS_AAM);
    						  erro.setCriadoEm(new Date());
    						  erro.setCriadoPor(servidorLogado.getUsuario());
    						  erro.setDataPrevia(pDataPrevia);
    						  erro.setCthSeq(pCthSeq);
    						  erro.setPacCodigo(pPacCodigo);
    						  erro.setProntuario(pPacProntuario);
    						  erro.setPhiSeq(pPhiSolic);
    						  erro.setIphPhoSeq(pPhoSolic);
    						  erro.setIphSeq(pIphSolic);
    						  erro.setCodItemSusSolicitado(pCodSusSolic);
    						  erro.setPhiSeqRealizado(pPhiRealiz);
    						  erro.setIphPhoSeqRealizado(pPhoRealiz);
    						  erro.setIphSeqRealizado(pIphRealiz);
    						  erro.setCodItemSusRealizado(pCodSusRealiz);
    						  erro.setIphPhoSeqItem1(valores.getRegItem().getIphPhoSeq());
    						  erro.setIphSeqItem1(valores.getRegItem().getIphSeq());
    						  erro.setCodItemSus1(valores.getRegItem().getProcedimentoHospitalarSus());					  	
    						  erro.setErro("QUANTIDADE MAIOR QUE MAXIMO AUTORIZADO PARA O PROCEDIMENTO: " + valores.getRegItem().getQuantidade() + _MAIOR_ + vQtdProcCma);
    						  erro.setFatMensagemLog(new FatMensagemLog(230));
    						  persistirLogErroCarregandoFatMensagemLog(faturamentoFacade, erro);
    						 // faturamentoFacade.evict(erro);
    					  } catch (Exception e2) {
    						  logar(MSG_EXCECAO_IGNORADA, e2);
    					  }
    					  return -1; // sair com erro da funcao que chamou
    				  }

    				  if (( fatVariaveisVO.getvMaiorValor()) && 
    						  Boolean.FALSE.equals(pPrevia) && CoreUtil.igual("S",indAutorizadoSms) && 
    						  cthSeqReapresentada == null && // não consiste reapres
    						  !CoreUtil.igual(valores.getRegItem().getProcedimentoHospitalarSus(), pCodSusRealiz)) { 
    					  
    					  logar("verifica autorização cma maior valor {0}", codSusCma);

    					  /* Complementação da estória #7040 */
    					                                                
    					  final DominioSimNao parametroAutorizacaoManual = DominioSimNao.valueOf(this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_INTEGRACAO_SISTEMA_SMS_PARA_AUTORIZACAO_ITENS_DA_CONTA));
    					  
							if (autorizado == null && DominioSimNao.S.equals(parametroAutorizacaoManual)) {
    						  logar("verifica autorização cma parte 1a não achou");
    						  try{
        						  FatLogError erro = new FatLogError();
        						  erro.setModulo(INT);
        						  erro.setPrograma(RN_CTHC_ATU_INS_AAM);
        						  erro.setCriadoEm(new Date());
        						  erro.setCriadoPor(servidorLogado.getUsuario());
        						  erro.setDataPrevia(pDataPrevia);
        						  erro.setCthSeq(pCthSeq);
        						  erro.setPacCodigo(pPacCodigo);
        						  erro.setProntuario(pPacProntuario);
        						  erro.setPhiSeq(pPhiSolic);
        						  erro.setIphPhoSeq(pPhoSolic);
        						  erro.setIphSeq(pIphSolic);
        						  erro.setCodItemSusSolicitado(pCodSusSolic);
        						  erro.setPhiSeqRealizado(pPhiRealiz);
        						  erro.setIphPhoSeqRealizado(pPhoRealiz);
        						  erro.setIphSeqRealizado(pIphRealiz);
        						  erro.setCodItemSusRealizado(valores.getRegItem().getProcedimentoHospitalarSus());
        						  erro.setIphPhoSeqItem1(valores.getRegItem().getIphPhoSeq());
        						  erro.setIphSeqItem1(valores.getRegItem().getIphSeq());
        						  erro.setCodItemSus1(valores.getRegItem().getProcedimentoHospitalarSus());					  	
        						  erro.setErro("PROCEDIMENTO NÃO AUTORIZADO PELA SMS");
        						  persistirLogErroCarregandoFatMensagemLog(faturamentoFacade, erro);
        					  } catch (Exception e2) {
        						  logar(MSG_EXCECAO_IGNORADA, e2);
        					  }
        					  return -1; // sair com erro da funcao que chamou
    					  }
    				  }
		    	  }
		    	  // FIM 17/01/2008 Verifica autorização SMS
		      }
		      
		      // verifica tipo de ato para somar valores:
		      if(CoreUtil.igual(valores.getRegItem().getTaoSeq(), valores.getCodAtoUtie())){ // DIARIA DE UTI ESPECIALIZADA

		    	  valores.setVlrUtie(valores.getVlrUtie().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
														 .add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
														 .add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
														 .add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
														 .add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO))));
		    	  
		    	  valores.setVlrShUtie(valores.getVlrShUtie().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSpUtie(valores.getVlrSpUtie().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadtUtie(valores.getVlrSadtUtie().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProcUtie(valores.getVlrProcUtie().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnestUtie(valores.getVlrAnestUtie().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		    	  // marca o item recem inserido como cobranca de valor
		    	  FatAtoMedicoAih atoMedicoAihToUpdate = fatAtoMedicoAihDAO.obterPorChavePrimaria(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedicoAihToUpdate.setIndModoCobranca(DominioModoCobranca.V);
		    	  if(atoMedicoAihToUpdate != null){
		    		  faturamentoFacade.atualizarAtoMedicoAih(atoMedicoAihToUpdate, true, nomeMicrocomputador, dataFimVinculoServidor);		    		  
		    	  }
		    	  
		    	  
		      } else if(CoreUtil.in(valores.getRegItem().getTaoSeq(), valores.getCodAtoRn(), valores.getCodAtoConsPed())){
		    	  //ATENDIMENTO AO RN EM SALA DE PARTO, 1a. CONSULTA PEDIATRIA

		    	  valores.setVlrRn(valores.getVlrRn().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
													 .add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
													 .add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
													 .add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
													 .add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO))));
		    	  
		    	  valores.setVlrShRn(valores.getVlrShRn().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSpRn(valores.getVlrSpRn().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadtRn(valores.getVlrSadtRn().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProcRn(valores.getVlrProcRn().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnestRn(valores.getVlrAnestRn().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		    	  // marca o item recem inserido como cobranca de valor
		    	  final FatAtoMedicoAih atoMedicoAihToUpdate = fatAtoMedicoAihDAO.obterPorChavePrimaria(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedicoAihToUpdate.setIndModoCobranca(DominioModoCobranca.V);
		    	  if(atoMedicoAihToUpdate != null){
		    		  faturamentoFacade.atualizarAtoMedicoAih(atoMedicoAihToUpdate, true, nomeMicrocomputador, dataFimVinculoServidor);		    		  
		    	  }
		      } else if(CoreUtil.igual(valores.getRegItem().getTaoSeq(), valores.getCodAtoHemat())){ // HEMOTERAPIA

		    	  valores.setVlrHemat(valores.getVlrHemat().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
														   .add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
														   .add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
														   .add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
														   .add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO))));
		    	  
		    	  valores.setVlrShHemat(valores.getVlrShHemat().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSpHemat(valores.getVlrSpHemat().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadtHemat(valores.getVlrSadtHemat().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProcHemat(valores.getVlrProcHemat().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnestHemat(valores.getVlrAnestHemat().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		    	  // marca o item recem inserido como cobranca de valor
		    	  FatAtoMedicoAih atoMedicoAihToUpdate = fatAtoMedicoAihDAO.obterPorChavePrimaria(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedicoAihToUpdate.setIndModoCobranca(DominioModoCobranca.V);
		    	  if(atoMedicoAihToUpdate != null){
		    		  faturamentoFacade.atualizarAtoMedicoAih(atoMedicoAihToUpdate, true, nomeMicrocomputador, dataFimVinculoServidor);		    		  
		    	  }
		      } else if(CoreUtil.in(valores.getRegItem().getTaoSeq(), valores.getCodAtoSalaTransp(), valores.getCodAtoSadtTransp(), valores.getCodAtoCirurTransp())){ //TAXA DE SALA DE TRANSPLANTE,SADT TRANSPLANTE,CIRURGIAO TRANSPLANTE

		    	  valores.setVlrTransp(valores.getVlrTransp().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
															 .add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
															 .add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
															 .add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
															 .add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO))));
		    	  
		    	  valores.setVlrShTransp(valores.getVlrShTransp().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSpTransp(valores.getVlrSpTransp().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadtTransp(valores.getVlrSadtTransp().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProcTransp(valores.getVlrProcTransp().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnestTransp(valores.getVlrAnestTransp().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		    	  // marca o item recem inserido como cobranca de valor
		    	  FatAtoMedicoAih atoMedicoAihToUpdate = fatAtoMedicoAihDAO.obterPorChavePrimaria(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedicoAihToUpdate.setIndModoCobranca(DominioModoCobranca.V);
		    	  if(atoMedicoAihToUpdate != null){
		    		  faturamentoFacade.atualizarAtoMedicoAih(atoMedicoAihToUpdate, true, nomeMicrocomputador, dataFimVinculoServidor);		    		  
		    	  }
		    	  
		      } else if(CoreUtil.igual(valores.getRegItem().getTaoSeq(), valores.getCodAtoOpm())){ // HEMOTERAPIA

		    	  valores.setVlrOpm(valores.getVlrOpm().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)
													   .add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO))
													   .add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO))
													   .add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO))
													   .add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO))));
		    	  
		    	  valores.setVlrShOpm(valores.getVlrShOpm().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSpOpm(valores.getVlrSpOpm().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadtOpm(valores.getVlrSadtOpm().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProcOpm(valores.getVlrProcOpm().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnestOpm(valores.getVlrAnestOpm().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		    	  // marca o item recem inserido como cobranca de valor
		    	  FatAtoMedicoAih atoMedicoAihToUpdate = fatAtoMedicoAihDAO.obterPorChavePrimaria(new FatAtoMedicoAihId(valores.getEaiSeqp(), pCthSeq, vAamSeqp));
		    	  atoMedicoAihToUpdate.setIndModoCobranca(DominioModoCobranca.V);
		    	  if(atoMedicoAihToUpdate != null){
		    		  faturamentoFacade.atualizarAtoMedicoAih(atoMedicoAihToUpdate, true, nomeMicrocomputador, dataFimVinculoServidor);		    		  
		    	  }
		    	  
		      } else if(DominioModoCobranca.V.equals(valores.getRegItem().getIndModoCobranca())){
		    	  valores.setVlrSh(valores.getVlrSh().add(nvl(valores.getRegItem().getValorServHosp(), BigDecimal.ZERO)));
		    	  valores.setVlrSp(valores.getVlrSp().add(nvl(valores.getRegItem().getValorServProf(), BigDecimal.ZERO)));
		    	  valores.setVlrSadt(valores.getVlrSadt().add(nvl(valores.getRegItem().getValorSadt(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrProced(valores.getVlrProced().add(nvl(valores.getRegItem().getValorProcedimento(), BigDecimal.ZERO)));		    	  
		    	  valores.setVlrAnest(valores.getVlrAnest().add(nvl(valores.getRegItem().getValorAnestesista(), BigDecimal.ZERO)));
		    	  
		      } else if(DominioModoCobranca.P.equals(valores.getRegItem().getIndModoCobranca())){ // pontos
		    	  // soma pontos do procedimento
		    	  valores.setPtosAnest(nvl(valores.getPtosAnest(), 0) + nvl(valores.getRegItem().getPontosAnestesista().intValue(), 0));
		    	  valores.setPtosCirur(nvl(valores.getPtosCirur(), 0) + nvl(valores.getRegItem().getPontosCirurgiao().intValue(), 0));
		    	  valores.setPtosSadt(nvl(valores.getPtosSadt(), 0) + nvl(valores.getRegItem().getPontosSadt().intValue(), 0));
		      }
		} catch (Exception e) {
			logar("erro no insert aam");
			String msg = e.getMessage();
			try{
				  FatLogError erro = new FatLogError();
				  erro.setModulo(INT);
				  erro.setPrograma(RN_CTHC_ATU_INS_AAM);
				  erro.setCriadoEm(new Date());
				  erro.setCriadoPor(servidorLogado.getUsuario());
				  erro.setDataPrevia(pDataPrevia);
				  erro.setCthSeq(pCthSeq);
				  erro.setPacCodigo(pPacCodigo);
				  erro.setProntuario(pPacProntuario);
				  erro.setPhiSeq(pPhiSolic);
				  erro.setIphPhoSeq(pPhoSolic);
				  erro.setIphSeq(pIphSolic);
				  erro.setCodItemSusSolicitado(pCodSusSolic);
				  erro.setPhiSeqRealizado(pPhiRealiz);
				  erro.setIphPhoSeqRealizado(pPhoRealiz);
				  erro.setIphSeqRealizado(pIphRealiz);
				  erro.setCodItemSusRealizado(valores.getRegItem().getProcedimentoHospitalarSus());
				  erro.setIphPhoSeqItem1(valores.getRegItem().getIphPhoSeq());
				  erro.setIphSeqItem1(valores.getRegItem().getIphSeq());
				  erro.setCodItemSus1(valores.getRegItem().getProcedimentoHospitalarSus());					  	
				  erro.setFatMensagemLog(new FatMensagemLog(62));
				  erro.setErro("ERRO AO INSERIR ATO MEDICO AIH: " + msg);
				  persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			  } catch (Exception e2) {
				  logar(MSG_EXCECAO_IGNORADA, e2);
			  }
			  return -1; // sair com erro da funcao que chamou
			}
		} // vAamSeqp is not null
		//--<-------------FIM DO LANCAMENTO DO PROCEDIMENTO NOS SERV PROFS
		
		return 1; // deu certo o lancamento
	}

	private void persistirLogErroCarregandoFatMensagemLog(final IFaturamentoFacade faturamentoFacade, FatLogError erro) {
		if(erro.getFatMensagemLog() != null && erro.getFatMensagemLog().getCodigo() != null){
			erro.setFatMensagemLog(fatMensagemLogDAO.obterFatMensagemLogPorCodigo(erro.getFatMensagemLog().getCodigo()));
		}
		faturamentoFacade.persistirLogError(erro);
	}
	
	/**
	 * ORADB: AIPC_GET_CNS_RESP
	 * @since 13/09/2012
	 * @author eSchweigert
	 */
	public String aipcGetCnsResp(final Long pCpf) throws ApplicationBusinessException{
		
		if(pCpf == null){
			return null;
			
		} else if(pCpf.toString().length() == 15){
			return pCpf.toString();
			
		} else {
			final Short vTipoCns = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_INF_CNS);
			final String cCns = getRegistroColaboradorFacade().obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(pCpf, vTipoCns);
			
			return cCns;
		}
	}
	

	/**
	 * ORADB Procedure FATK_CTHN_RN_UN.RN_CTHC_ATU_VLR_DIAR
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected Boolean rnCthcAtuVlrDiar(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatValorDiariaInternacaoDAO fatValorDiariaInternacaoDAO = getFatValorDiariaInternacaoDAO();
		
		BigDecimal vDiarAcomp = BigDecimal.ZERO;
		BigDecimal vTotalDiarUti = BigDecimal.ZERO;
		BigDecimal vDiarAte3Dias = BigDecimal.ZERO;
		BigDecimal vDiarMais3Dias = BigDecimal.ZERO;
	
	    BigDecimal vVlrSh = BigDecimal.ZERO;
	    BigDecimal vVlrSp = BigDecimal.ZERO;
	    BigDecimal vVlrSadt = BigDecimal.ZERO;

	    BigDecimal vVlrAcomp = BigDecimal.ZERO;
	    BigDecimal vVlrShAcomp = BigDecimal.ZERO;
	    BigDecimal vVlrSpAcomp = BigDecimal.ZERO;
	    BigDecimal vVlrSadtAcomp = BigDecimal.ZERO;

	    BigDecimal vVlrUti = BigDecimal.ZERO;
	    BigDecimal vVlrShUti = BigDecimal.ZERO;
	    BigDecimal vVlrSpUti = BigDecimal.ZERO;
	    BigDecimal vVlrSadtUti = BigDecimal.ZERO;

		// busca espelhos AIH da conta
		FatEspelhoAih regEai = fatEspelhoAihDAO.obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, 1));
		if(regEai != null){
//			faturamentoFacade.evict(regEai);
			this.flush();
			vDiarAcomp = BigDecimal.valueOf(nvl(regEai.getNroDiariasAcompanhante(),0));
			logar("total diarias acompanhante: {0}", vDiarAcomp);
			
			if(CoreUtil.maior(vDiarAcomp, 0)){
				FatValorDiariaInternacao vlrsDiaria = fatValorDiariaInternacaoDAO.buscarPrimeiraValidaPorTipo("A");
				if(vlrsDiaria != null){
					vVlrSh   = nvl(vlrsDiaria.getValorServHosp(), BigDecimal.ZERO);
					vVlrSp   = nvl(vlrsDiaria.getValorServProf(), BigDecimal.ZERO);
					vVlrSadt = nvl(vlrsDiaria.getValorSadt(),     BigDecimal.ZERO);
				}

				vVlrAcomp =      vVlrSh.multiply(vDiarAcomp)
							.add(vVlrSp.multiply(vDiarAcomp))
							.add(vVlrSadt.multiply(vDiarAcomp));
				
				vVlrShAcomp   = vVlrShAcomp.add(vVlrSh.multiply(vDiarAcomp));
				vVlrSpAcomp   = vVlrSpAcomp.add(vVlrSp.multiply(vDiarAcomp));
				vVlrSadtAcomp = vVlrSadtAcomp.add(vVlrSadt.multiply(vDiarAcomp));

			} // v_diar_acomp > 0
			
			vTotalDiarUti = BigDecimal.valueOf(nvl(regEai.getNroDiasMesInicialUti(), 0) + nvl(regEai.getNroDiasMesAnteriorUti(), 0) + nvl(regEai.getNroDiasMesAltaUti(), 0));
			logar("total diarias UTI nao especial: {0}", vTotalDiarUti);

			if(CoreUtil.maior(vTotalDiarUti, 3)){
				vDiarAte3Dias = BigDecimal.valueOf(3);
				
				FatValorDiariaInternacao vlrsDiaria = fatValorDiariaInternacaoDAO.buscarPrimeiraValidaPorTipo("U");
				if(vlrsDiaria != null){
					vVlrSh   = nvl(vlrsDiaria.getValorServHosp(), BigDecimal.ZERO);
					vVlrSp   = nvl(vlrsDiaria.getValorServProf(), BigDecimal.ZERO);
					vVlrSadt = nvl(vlrsDiaria.getValorSadt(),     BigDecimal.ZERO);
				}
				
				vVlrUti = vVlrUti.add(vVlrSh.multiply(vDiarAte3Dias))
							     .add(vVlrSp.multiply(vDiarAte3Dias))
							     .add(vVlrSadt.multiply(vDiarAte3Dias));
				
				vVlrShUti   = vVlrShUti.add(vVlrSh.multiply(vDiarAte3Dias));
				vVlrSpUti   = vVlrSpUti.add(vVlrSp.multiply(vDiarAte3Dias));
				vVlrSadtUti = vVlrSadtUti.add(vVlrSadt.multiply(vDiarAte3Dias));
				
			    vVlrSh = BigDecimal.ZERO;
			    vVlrSp = BigDecimal.ZERO;
			    vVlrSadt = BigDecimal.ZERO;
				vDiarMais3Dias = vTotalDiarUti.subtract(vDiarAte3Dias);				
				
				vlrsDiaria = fatValorDiariaInternacaoDAO.buscarPrimeiraValidaPorTipo("M");
				if(vlrsDiaria != null){
					vVlrSh = nvl(vlrsDiaria.getValorServHosp(), BigDecimal.ZERO);
					vVlrSp = nvl(vlrsDiaria.getValorServProf(), BigDecimal.ZERO);
					vVlrSadt = nvl(vlrsDiaria.getValorSadt(), BigDecimal.ZERO);
				}
				
				vVlrUti = vVlrUti.add(vVlrSh.multiply(vDiarMais3Dias))
								 .add(vVlrSp.multiply(vDiarMais3Dias))
								 .add(vVlrSadt.multiply(vDiarMais3Dias));
				
				vVlrShUti = vVlrShUti.add(vVlrSh.multiply(vDiarMais3Dias));
				vVlrSpUti = vVlrSpUti.add(vVlrSp.multiply(vDiarMais3Dias));
				vVlrSadtUti = vVlrSadtUti.add(vVlrSadt.multiply(vDiarMais3Dias));

				logar("diarias ate 3 dias: {0} diarias mais de 3 dias: {1}", vDiarAte3Dias, vDiarMais3Dias);
			
			} else if(CoreUtil.maior(vTotalDiarUti, 0)) {
				FatValorDiariaInternacao vlrsDiaria = fatValorDiariaInternacaoDAO.buscarPrimeiraValidaPorTipo("U");
				if(vlrsDiaria != null){
					vVlrSh = nvl(vlrsDiaria.getValorServHosp(), BigDecimal.ZERO);
					vVlrSp = nvl(vlrsDiaria.getValorServProf(), BigDecimal.ZERO);
					vVlrSadt = nvl(vlrsDiaria.getValorSadt(), BigDecimal.ZERO);
				}
				
				vVlrUti = vVlrUti.add(vVlrSh.multiply(vTotalDiarUti))
								 .add(vVlrSp.multiply(vTotalDiarUti))
								 .add(vVlrSadt.multiply(vTotalDiarUti));
				
				vVlrShUti = vVlrShUti.add(vVlrSh.multiply(vTotalDiarUti));
				vVlrSpUti = vVlrSpUti.add(vVlrSp.multiply(vTotalDiarUti));
				vVlrSadtUti = vVlrSadtUti.add(vVlrSadt.multiply(vTotalDiarUti));
			} // v_total_diar_uti > 0

			
			// Atualiza valores das diarias na conta			
			logar("vai fazer update dos valores de diarias na conta...");
			logar("valor_acomp: {0}", vVlrAcomp);
			logar("valor_uti: {0}", vVlrUti);
			FatContasHospitalares contaHospToUpdate = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);			
			if(contaHospToUpdate != null){
				FatContasHospitalares contaOld = null;
				try{
					contaOld = faturamentoFacade.clonarContaHospitalar(contaHospToUpdate);
				}catch (Exception e) {
					logError("Exceção capturada: ", e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				contaHospToUpdate.setValorUti(vVlrUti);
				contaHospToUpdate.setValorAcomp(vVlrAcomp);
				
				// Atualiza demais valores de diarias da conta
				logar("vai fazer update dos demais valores diarias da conta");
				FatValorContaHospitalar valorContaHospToUpdate = contaHospToUpdate.getValorContaHospitalar();
				if(valorContaHospToUpdate != null){
					valorContaHospToUpdate.setValorShUti(vVlrShUti);
					valorContaHospToUpdate.setValorSpUti(vVlrSpUti);
					valorContaHospToUpdate.setValorSadtUti(vVlrSadtUti);
					valorContaHospToUpdate.setValorShAcomp(vVlrShAcomp);
					valorContaHospToUpdate.setValorSpAcomp(vVlrSpAcomp);
					valorContaHospToUpdate.setValorSadtAcomp(vVlrSadtAcomp);
					faturamentoFacade.atualizarFatValorContaHospitalar(valorContaHospToUpdate, false);
				}
				faturamentoFacade.persistirContaHospitalar(contaHospToUpdate, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
			}
			return Boolean.TRUE;
		} else { // if c_eai%found
			try{
				FatLogError erro = new FatLogError();
				erro.setModulo(INT);
				erro.setPrograma("RN_CTHP_ATU_VLR_DIAR");
				erro.setCriadoEm(new Date());
		  		erro.setCriadoPor(servidorLogado.getUsuario());
		  		erro.setCthSeq(pCthSeq);
		  		erro.setErro("NAO ENCONTROU DADOS DO PRIMEIRO ESPELHO AIH PARA CALCULAR VALORES DAS DIARIAS.");
		  		erro.setFatMensagemLog(new FatMensagemLog(132));
		  		persistirLogErroCarregandoFatMensagemLog(faturamentoFacade,
						erro);
			} catch (Exception e) {
				logar(MSG_EXCECAO_IGNORADA, e);
			} 
			return Boolean.FALSE;
		} // c_eai%found
	}
	
	/**
	 * ORADB Function FATC_BUSCA_PROCED_DO_ITEM_NEW
	 * 
	 * @since 12/09/2012
	 * @author eSchweigert
	 */
	protected Long fatcBuscaProcedDoItem(final Integer eaiCthSeq, final Short iphPhoSeq,
								final Integer iphSeq, final Byte taoSeq, 
								final String competenciaUTI, final Date competencia) {
		
		final Byte codigoSus = taoSeq;
		Long vRetorno = null;
		
		logar("Buscando compatibilidade da conta:{0} do iph:{1}/{2}", eaiCthSeq, iphPhoSeq,iphSeq);
		logar("p_competencia:{0} p_competencia_uti:{1}", (competencia != null ? DateUtil.obterDataFormatada(competencia, "dd/MM/yy") : null), competenciaUTI);

		// tipo ato 1 para restringir para 1-cirurgiao, 7-consulta clinica
		Object[] cRegistro = getFatAtoMedicoAihDAO().obterQtdMaximaExecucao( iphPhoSeq, iphSeq, eaiCthSeq, competencia, codigoSus, 1,
																   		   DominioIndCompatExclus.ICP, DominioIndCompatExclus.PCI);
		if(cRegistro != null){
			
			if(cRegistro[1] != null){	//[1] EAI_CTH_SEQ
				
				if(Long.valueOf(0l).equals(cRegistro[2])){
					final List<FatVlrItemProcedHospComps> result = getFatVlrItemProcedHospCompsDAO().
									obterFatVlrItemProcedHospCompsPorProcedimentoECompetencia(iphPhoSeq, iphSeq, competencia);
					
					if(!result.isEmpty()){
						vRetorno = Long.valueOf(result.get(0).getQtdMaximaExecucao());
						logar("FAT_VLR_ITEM_PROCED_HOSP_COMPS, qtde: {0}", vRetorno);
					}
				} else {
					vRetorno = (Long) cRegistro[2];
					logar("Total fat_compat_exclus_itens qtde: {0}", vRetorno);
				}

			} else {
				logar("cursor c_registro found, não retornou compatibilidade");
				// tipo 0 para não restringir
				cRegistro = getFatAtoMedicoAihDAO().obterQtdMaximaExecucao( iphPhoSeq, iphSeq, eaiCthSeq, competencia, codigoSus, 0,
				   		   													DominioIndCompatExclus.ICP, DominioIndCompatExclus.PCI);
				
				if(cRegistro != null){
					if(cRegistro[1] != null){ //[1] EAI_CTH_SEQ
						logar("cursor c_registro found, retornou compatibilidade com tipo:{0}", cRegistro[0]);

						if(!Long.valueOf(0l).equals(cRegistro[2])){
							final List<FatVlrItemProcedHospComps> result = getFatVlrItemProcedHospCompsDAO().
											obterFatVlrItemProcedHospCompsPorProcedimentoECompetencia(iphPhoSeq, iphSeq, competencia);
							
							if(!result.isEmpty()){
								vRetorno = Long.valueOf(result.get(0).getQtdMaximaExecucao());
								logar("2 FAT_VLR_ITEM_PROCED_HOSP_COMPS, qtde: {0}", vRetorno);
							}
						} else {
							vRetorno = (Long) cRegistro[2];
							logar("Total 2 fat_compat_exclus_itens qtde: {0}", vRetorno);
						}
					} else {
						logar("cursor 2 c_registro found, não retornou compatibilidade");
						vRetorno = obtervRetornoByCIPH(iphPhoSeq, iphSeq, competencia);
					}
				}
			} 
		} else {
			logar("cursor c_registro notfound");
			vRetorno = obtervRetornoByCIPH(iphPhoSeq, iphSeq, competencia);
		}
		
		return vRetorno;
	}

	private Long obtervRetornoByCIPH(Short iphPhoSeq, Integer iphSeq, Date competencia) {
		Long vRetorno = null;
		
		final List<FatVlrItemProcedHospComps> result = getFatVlrItemProcedHospCompsDAO().
				obterFatVlrItemProcedHospCompsPorProcedimentoECompetencia(iphPhoSeq, iphSeq, competencia);
		
		if(!result.isEmpty()){
			//Evita NPE #36862
			if(result.get(0).getQtdMaximaExecucao() != null){
				vRetorno = Long.valueOf(result.get(0).getQtdMaximaExecucao());
			}
			if(getFaturamentoRN().verificarCaracteristicaExame(iphSeq, iphPhoSeq, 
					DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD)
					){
				vRetorno = Long.valueOf(result.get(0).getQtdMaximaExecucao());
				logar("Admite liberação de Qtd, qtde_max: {0}", vRetorno);
				
			} else {
				if(Boolean.TRUE.equals(result.get(0).getFatItensProcedHospitalar().getQuantidadeMaiorInternacao())){
					logar("IND_QTD_MAIOR_INTERNACAO = S, v_retorno fica null para liberar quantidade lançada.");
					
				} else {
					vRetorno = Long.valueOf(result.get(0).getQtdMaximaExecucao());
					logar("IND_QTD_MAIOR_INTERNACAO = N,FAT_VLR_ITEM_PROCED_HOSP_COMPS, qtde: {0}", vRetorno);
				}
			}
		} 
		
		return vRetorno;
	}
	
	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public FaturamentoInternacaoRN getFaturamentoInternacaoRN() {
		return faturamentoInternacaoRN;
	}	
}