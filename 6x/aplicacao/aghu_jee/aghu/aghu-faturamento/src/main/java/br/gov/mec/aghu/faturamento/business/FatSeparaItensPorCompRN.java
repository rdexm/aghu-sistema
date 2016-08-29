package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihTempDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.RapDeParaCboDAO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoMedicoAihVO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoObrigatorioVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaQtdCompVO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoItemContaHospVO;
import br.gov.mec.aghu.faturamento.vo.CursorMaximasAtosMedicoAihTempVO;
import br.gov.mec.aghu.faturamento.vo.RnIphcVerAlteracaoVO;
import br.gov.mec.aghu.faturamento.vo.VerificaAtoObrigatorioVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAtoMedicoAihId;
import br.gov.mec.aghu.model.FatAtoMedicoAihTemp;
import br.gov.mec.aghu.model.FatAtoMedicoAihTempId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.model.FatValorContaHospitalar;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapDeParaCbo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * ORADB Procedure FATP_SEPARA_ITENS_POR_COMP
 * 
 * Essa é a principal Procedure da portaria 203 para separar os dados por
 * competência possui várias sub-procedures, por isso foi criada uma classe
 * específica para ela.
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta","PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.AtributoEmSeamContextManager", "PMD.AghuTooManyMethods"})
@Stateless
public class FatSeparaItensPorCompRN extends AbstractFatDebugLogEnableRN {


@EJB
private RelatorioResumoCobrancaAihRN relatorioResumoCobrancaAihRN;

@EJB
private FatkCthnRN fatkCthnRN;

private static final Log LOG = LogFactory.getLog(FatSeparaItensPorCompRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatAtoMedicoAihTempDAO fatAtoMedicoAihTempDAO;

@Inject
private RapDeParaCboDAO rapDeParaCboDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7270118170723094134L;
	private Short vQtd = 0;
	private Integer vCont = null;
	private Byte vSeqp = 0;
	private Byte vSeqpPrincVinculado=0;
	private Integer vSeqpIec = null;
	private Integer vEaiSeq = 1;
	private String vVlrTexto;
	private Date vVlrData;
	private Boolean alterarValorConta;
	private String vCtrIntAlta = "S";
	private Integer vDtComp = null;
	private Short vDiaAlta = null;
	private List<CursorEspelhoItemContaHospVO> tabIecVO;
	private Map<Long, CursorAtoMedicoAihVO> tabAtosMedicosAih; // Long é um indice = iphCodSus

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Integer CONST_201104_PORTARIA_203 = 201104;
	private static final Integer CONST_201105 = 201105;
	private static final Integer CONST_201201 = 201201;
	private static final Integer CONST_201108_PORTARIA_203 = 201108;
	private static final String CONST_YYYY_MM = "yyyyMM";
	private static final String CONST_DD = "dd";
	/*
	 * Segundo Marina Delazzeri: Como usamos a tabela FAT_ATOS_MEDICOS_TEMP para
	 * guardar os dados do encerramento da conta e a geração dos novos dados,
	 * estamos usando o seq_arq_sus para diferenciar os arquivos que geramos dos
	 * dados que foram gerados pelo encerramento.
	 */
	private static final Short CONST_999 = 999;

	/**
	 * ORADB Procedure FATP_SEPARA_ITENS_POR_COMP
	 * 
	 * @param pCthSeq
	 * @throws BaseException 
	 */
	public void fatpSeparaItensPorComp(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		logar("Iniciando execucao para a conta {0}", pCthSeq);
		
		// -- carrega itens espelho em tab temporaria
		carregaIec(pCthSeq);
		logar("carrega_iec");

		// -- delete atos médicos da tabela temporaria
		rnDeletaAtosMedicosTemp(pCthSeq);
		logar("rn_deleta_atos_medicos_temp");
		
		// -- copia os procedimentos da atos médicos aih para a atos medicos temp
		rnCopyAtosMedicosAihTemp(pCthSeq);
		logar("rn_copy_atos_medicos_aih_temp");
		
		//Limpa variavel da classe - Incidente - AGHU #51247
		vSeqp = 0;
		
		//Processa primeiro procedimento realizado, no caso de mutilpla são desdobradas as quantidades
		logar("grupo 4 com quantidade maior que 2");
		
		final ResultadoRnDesdobraMultipla result = rnDesdobraMultipla(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);
		logar("v_multipla: {0}", result.vMultipla.toString());
		
		if(result.vQtdeDesdobrada.intValue() > 1){
			logar("Conta {0} teve realizados desdobrados", pCthSeq);	
		}
		
		processaAtosMedicos(pCthSeq, result.vMultipla, nomeMicrocomputador, dataFimVinculoServidor);

		logar("v_cont: {0}", vCont);
		
//		if (vCont != null) {
		logar("verifica_final");
		verificaFinal();
		logar("verifica_iec_final");
		verificaIecFinal(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);

//			Garante que a vinculação dos seqs de cirurgião e anestesista esteja corretos
		logar("vincula os seqs - cirg e anestesista");
		rnVinculaSeq(pCthSeq);
		
		// deleta atos médicos aih para poder receber os itens por competência
		rnDeletaAtosMedicosAih(pCthSeq);

		// copia a atos médicos temp para a atos médicos aih.
		rnCopyAtosMedicosTempAih(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);
		//	} else {
		//		logar("Conta sem atos médicos");
//		}
	}

	private class ResultadoRnDesdobraMultipla{
		DominioSimNao vMultipla;
		Integer vQtdeDesdobrada;
	}
	
	/**
	 * ORADB: rn_desdobra_multipla
	 */
	private ResultadoRnDesdobraMultipla rnDesdobraMultipla(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		logar("verifica_realizados");

		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		
		final Byte taoSeq = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO); // 1
		//final Integer tivSeq = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_AGHU_COD_SUS_PROFISSIONAL_VINC_EMPREGATICIO); // 4
		final String codRegistro = buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC); // 03

		logar("function verifica_realizados return varchar2 is");
		final List<CursorAtoMedicoAihVO> listaAtoMedicoAihVOIni = fatItensProcedHospitalarDAO.listarAtosMedicosAih(pCthSeq, taoSeq);
		final List<CursorAtoMedicoAihVO> listaAtoMedicoAihVO = new ArrayList<CursorAtoMedicoAihVO>();

		// ajusta resultado da pesquisa com chamada de função
		for (CursorAtoMedicoAihVO vo : listaAtoMedicoAihVOIni) { 
			final Boolean fatcBuscaInstrReg = getRelatorioResumoCobrancaAihRN().buscaInstrRegistro(vo.getIphSeq(), vo.getIphPhoSeq(), codRegistro);
			
			/*  AND FATC_BUSCA_INSTR_REG(IPH.SEQ,IPH.PHO_SEQ,'03') = 'S' */
			if( Boolean.TRUE.equals(fatcBuscaInstrReg) ){
				
				// DECODE(FATC_VER_CARACT_IPH(NULL, NULL, IPH.PHO_SEQ, IPH.SEQ,'ADMITE LIBERAÇÃO DE QTD'),'S','N',IPH.IND_COBRANCA_DIARIAS) IND_COBRANCA_DIARIAS,
				if (Boolean.TRUE.equals(
										  faturamentoRN.verificarCaracteristicaExame( vo.getIphSeq(), 
												  									  vo.getIphPhoSeq(), 
												  									  DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD
												  									)
										)
				   ){
					vo.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
				}
				
				vo.setComp(0);
				
				// BUSCA CARACTERISTICA DE COBRANÇA DIARIAS POR ITEM LANÇADO
				// FATC_VER_CARACT_IPH(NULL,NULL, IPH.PHO_SEQ, IPH.SEQ, 'COBRA DIARIA POR ITEM LANÇADO')  CARACT,
				vo.setCaract(faturamentoRN.verificarCaracteristicaExame(vo.getIphSeq(), vo.getIphPhoSeq(), DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO));
				
				listaAtoMedicoAihVO.add(vo);
			}
		}

		return rnDesdobraMultiplaSpliteded(listaAtoMedicoAihVO, pCthSeq, faturamentoFacade, taoSeq, faturamentoRN, fatItensProcedHospitalarDAO, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Dividi método acima para evitar violação de Regra de PMD (método muito longo) 
	 * @param fatItensProcedHospitalarDAO 
	 * @throws BaseException 
	 */
	public ResultadoRnDesdobraMultipla rnDesdobraMultiplaSpliteded(List<CursorAtoMedicoAihVO> listaAtoMedicoAihVO, Integer pCthSeq, IFaturamentoFacade faturamentoFacade, Byte taoSeq, FaturamentoRN faturamentoRN, FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		final ResultadoRnDesdobraMultipla result = new ResultadoRnDesdobraMultipla();
		
		int vQtde=0;
		Byte vSeqpVinculado = null;
		boolean vFound = false;
		for (CursorAtoMedicoAihVO rRealizado : listaAtoMedicoAihVO) {
			logar("1 - Procedimento realizado {0} qtd={1}", rRealizado.getAtoMedicoAih().getIphCodSus(),rRealizado.getAtoMedicoAih().getQuantidade());
			logar("r_realizado.quantidade: {0} " + rRealizado.getAtoMedicoAih().getQuantidade());
			
			if(rRealizado.getAtoMedicoAih().getQuantidade().intValue() > 1){
				vQtde = rRealizado.getAtoMedicoAih().getQuantidade().intValue();
			}
			vFound = true;
			
			if(vQtde >= 2){

				rnInsereItens(rRealizado.getAtoMedicoAih().getQuantidade(), Integer.valueOf(rRealizado.getAtoMedicoAih().getCompetenciaUti()), rRealizado, 
						pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				vSeqpVinculado = vSeqp; 
				
				logar("r_realizado.iph_pho_seq: {0}", rRealizado.getIphPhoSeq());
				logar("r_realizado.iph_seq: {0}", rRealizado.getIphSeq());
				logar("r_realizado.seqp: {0}", rRealizado.getAtoMedicoAih().getId().getSeqp());

				final List<CursorAtoMedicoAihVO> cAtoIni = fatItensProcedHospitalarDAO.listarAtosMedicosAih( pCthSeq, 
						 taoSeq, 
						 rRealizado.getAtoMedicoAih().getId().getSeqp(), 
						 rRealizado.getIphSeq(), 
						 rRealizado.getIphPhoSeq()
						);

				if(cAtoIni != null && !cAtoIni.isEmpty()){ //Abre o curso c_ato
					// No cursor não há loop, presumo que pegará apenas o 1. reg. caso traga vários 
					CursorAtoMedicoAihVO cAtoVO = cAtoIni.get(0);
					
					// ajusta resultado da pesquisa com chamada de função
					final Integer iphSeq = cAtoVO.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getSeq();
					final Short phoSeq = cAtoVO.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getPhoSeq();

					// DECODE(FATC_VER_CARACT_IPH(NULL, NULL, AMA.IPH_PHO_SEQ, AMA.IPH_SEQ,'ADMITE LIBERAÇÃO DE QTD'),'S','N','N') IND_COBRANCA_DIARIAS,
					if (Boolean.TRUE.equals(
											  faturamentoRN.verificarCaracteristicaExame( iphSeq, 
													 								      phoSeq, 
													  									  DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD
													  									)
											)
					   ){
						cAtoVO.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
						
					} else {
						cAtoVO.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
					}
						
					// Busca caracteristica de cobrança diarias por item lançado
				    // FATC_VER_CARACT_IPH(NULL,NULL, AMA.IPH_PHO_SEQ, AMA.IPH_SEQ, 'COBRA DIARIA POR ITEM LANÇADO')  CARACT,
					cAtoVO.setCaract(faturamentoRN.verificarCaracteristicaExame(iphSeq, phoSeq, DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO));
					
					logar("2  %%%%%%% - seq_vvinc {0}" , vSeqpVinculado);
					logar("r_ato.quantidade: {0}" , rRealizado.getAtoMedicoAih().getQuantidade());
					logar("r_ato.competencia_uti: {0}" , rRealizado.getAtoMedicoAih().getCompetenciaUti());
					logar("v_seqp_vinculado: {0}" , vSeqpVinculado);
	     	    	
					rnInsereItens( cAtoVO.getAtoMedicoAih().getQuantidade(), 
								   Integer.valueOf(cAtoVO.getAtoMedicoAih().getCompetenciaUti()), 
								   cAtoVO, pCthSeq, 
								   (short) vSeqpVinculado, 
								   faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				}
				
			
			} else {
				for(int i=0; i< rRealizado.getAtoMedicoAih().getQuantidade().intValue(); i++){
					try {
						logar("QUANTIDADE MENOR QUE 2");
						rnInsereItens(Short.valueOf("1"), Integer.valueOf(rRealizado.getAtoMedicoAih().getCompetenciaUti()), rRealizado, 
										pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						
						vSeqpVinculado = vSeqp; 

						logar("r_realizado.iph_pho_seq: {0}", rRealizado.getIphPhoSeq());
						logar("r_realizado.iph_seq: {0}", rRealizado.getIphSeq());
						logar("r_realizado.seqp: {0}", rRealizado.getAtoMedicoAih().getId().getSeqp());

						final List<CursorAtoMedicoAihVO> cAtoIni = fatItensProcedHospitalarDAO.listarAtosMedicosAih( pCthSeq, 
																													 taoSeq, 
																													 rRealizado.getAtoMedicoAih().getId().getSeqp(), 
																													 rRealizado.getIphSeq(), 
																													 rRealizado.getIphPhoSeq()
																													);
						if(cAtoIni != null && !cAtoIni.isEmpty()){
							// No cursor não há loop, presumo que pegará apenas o 1. reg. caso traga vários 
							CursorAtoMedicoAihVO cAtoVO = cAtoIni.get(0);
							
							// ajusta resultado da pesquisa com chamada de função
							final Integer iphSeq = cAtoVO.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getSeq();
							final Short phoSeq = cAtoVO.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getPhoSeq();

							// DECODE(FATC_VER_CARACT_IPH(NULL, NULL, AMA.IPH_PHO_SEQ, AMA.IPH_SEQ,'ADMITE LIBERAÇÃO DE QTD'),'S','N','N') IND_COBRANCA_DIARIAS,
							if (Boolean.TRUE.equals(
													  faturamentoRN.verificarCaracteristicaExame( iphSeq, 
															 								      phoSeq, 
															  									  DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD
															  									)
													)
							   ){
								cAtoVO.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
								
							} else {
								cAtoVO.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
							}
								
							// Busca caracteristica de cobrança diarias por item lançado
						    // FATC_VER_CARACT_IPH(NULL,NULL, AMA.IPH_PHO_SEQ, AMA.IPH_SEQ, 'COBRA DIARIA POR ITEM LANÇADO')  CARACT,
							cAtoVO.setCaract(faturamentoRN.verificarCaracteristicaExame(iphSeq, phoSeq, DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO));
							
							logar("2  %%%%%%%");
							rnInsereItens( Short.valueOf("1"), 
										   Integer.valueOf(cAtoVO.getAtoMedicoAih().getCompetenciaUti()), 
										   cAtoVO, pCthSeq, 
										   (short) vSeqpVinculado, 
										   faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						}
						
					} catch (NumberFormatException e) {
						throw new ApplicationBusinessException(	FaturamentoExceptionCode.FAT_00527);
					}
				}
			}
			
			
			
		}
		
		if(vFound && vQtde == 0){
			result.vQtdeDesdobrada = 1;
		} else {
			result.vQtdeDesdobrada = vQtde;
		}
		
		result.vMultipla = vFound ? DominioSimNao.S : DominioSimNao.N;
		return result;
	}

	/**
	 * Procedure CARREGA_IEC
	 */
	protected void carregaIec(Integer pCthSeq) {
		final List<FatEspelhoItemContaHosp> listaEspelhoItemContaHosp = getFatEspelhoItemContaHospDAO().listarEspelhoItemContaHospPorCthSeq(pCthSeq);
		tabIecVO = new ArrayList<CursorEspelhoItemContaHospVO>();

		for (FatEspelhoItemContaHosp espelhoItemContaHosp : listaEspelhoItemContaHosp) {
			tabIecVO.add(new CursorEspelhoItemContaHospVO( espelhoItemContaHosp, 
														  (short) 0, (short) 0, 
														  espelhoItemContaHosp.getItemProcedimentoHospitalar().getInternacao())
														 );
		}
	}

	/**
	 * Procedure RN_DELETA_ATOS_MEDICOS_TEMP
	 */
	protected void rnDeletaAtosMedicosTemp(Integer pCth) {
		getFatAtoMedicoAihTempDAO().removeAtoMedicoAihTemp(pCth);
	}

	/**
	 * Procedure RN_COPY_ATOS_MEDICOS_AIH_TEMP
	 * 
	 * @param pCth
	 */
	protected void rnCopyAtosMedicosAihTemp(Integer pCth) {
		FatAtoMedicoAihDAO atoMedicoAihDAO = getFatAtoMedicoAihDAO();
		List<FatAtoMedicoAih> listaAtoMedicoAih = atoMedicoAihDAO.listarPorCth(pCth);
		FatAtoMedicoAihTempDAO atoMedicoAihTempDAO = getFatAtoMedicoAihTempDAO();

		for (FatAtoMedicoAih aih : listaAtoMedicoAih) {
			FatAtoMedicoAihTemp elemento = new FatAtoMedicoAihTemp();

			FatAtoMedicoAihTempId id = new FatAtoMedicoAihTempId(aih.getId().getEaiSeq() + 50, aih.getId().getEaiCthSeq(), aih.getId()
					.getSeqp());
			elemento.setId(id);

			elemento.setQuantidade(aih.getQuantidade());
			elemento.setCriadoPor(aih.getCriadoPor());
			elemento.setCriadoEm(aih.getCriadoEm());
			elemento.setAlteradoPor(aih.getAlteradoPor());
			elemento.setAlteradoEm(aih.getAlteradoEm());
			elemento.setNotaFiscal(aih.getNotaFiscal());

			if (aih.getItemProcedimentoHospitalar() != null){
				elemento.setIphSeq(aih.getItemProcedimentoHospitalar().getId().getSeq());
			}

			if (aih.getItemProcedimentoHospitalar() != null){
				elemento.setIphPhoSeq(aih.getItemProcedimentoHospitalar().getId().getPhoSeq());
			}

			if (aih.getFatTipoAto() != null){
				elemento.setTaoSeq(aih.getFatTipoAto().getCodigoSus());
			}

			if (aih.getFatTiposVinculo() != null){
				elemento.setTivSeq(aih.getFatTiposVinculo().getCodigoSus().byteValue());
			}

			elemento.setPontosAnestesista(aih.getPontosAnestesista());
			elemento.setPontosCirurgiao(aih.getPontosCirurgiao());
			elemento.setPontosSadt(aih.getPontosSadt());
			elemento.setDataPrevia(aih.getDataPrevia());
			elemento.setValorAnestesista(aih.getValorAnestesista());
			elemento.setValorProcedimento(aih.getValorProcedimento());
			elemento.setValorSadt(aih.getValorSadt());
			elemento.setValorServHosp(aih.getValorServHosp());
			elemento.setValorServProf(aih.getValorServProf());
			elemento.setIndConsistente(aih.getIndConsistente());
			elemento.setIndModoCobranca(aih.getIndModoCobranca());
			elemento.setIphCodSus(aih.getIphCodSus());
			elemento.setCompetenciaUti(aih.getCompetenciaUti());
			elemento.setIndEquipe(aih.getIndEquipe());
			elemento.setCpfCns(aih.getCpfCns());
			elemento.setSerieOpm(aih.getSerieOpm());
			elemento.setLoteOpm(aih.getLoteOpm());
			elemento.setRegAnvisaOpm(aih.getRegAnvisaOpm());
			elemento.setCnpjRegAnvisa(aih.getCnpjRegAnvisa());
			elemento.setCgc(aih.getCgc());
			elemento.setSeqArqSus(aih.getSeqArqSus());
			elemento.setCbo(aih.getCbo());

			atoMedicoAihTempDAO.persistir(elemento);
			atoMedicoAihTempDAO.flush();
			//faturamentoFacade.evict(aih);
			//faturamentoFacade.evict(elemento);
		}
	}

	/**
	 * Procedure PROCESSA_ATOS_MEDICOS
	 * @throws BaseException 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void processaAtosMedicos(final Integer pCthSeq, final DominioSimNao vMultipla, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws NumberFormatException, BaseException {
		logar("Processa Atos Medicos");

		FaturamentoRN faturamentoRN = getFaturamentoRN();
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		FatItemContaHospitalarDAO itemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		tabAtosMedicosAih = new HashMap<Long, CursorAtoMedicoAihVO>();

		final String codRegistro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC); // 03
		
		final List<CursorAtoMedicoAihVO> listaAtoMedicoAihVOIni = getFatItensProcedHospitalarDAO().listarAtosMedicosAih(pCthSeq);
		final List<CursorAtoMedicoAihVO> listaAtoMedicoAihVO = new ArrayList<CursorAtoMedicoAihVO>();
		final Byte taoSeq_1 = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO); // 1
		final Byte taoSeq_6 = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA); // 1

		// ajusta resultado da pesquisa com chamada de função
		for (CursorAtoMedicoAihVO vo : listaAtoMedicoAihVOIni) { 
			final Boolean fatcBuscaInstrReg;
			
			// Só realiza a busca caso vMultipla for verdadeiro, senão, desconsidera isso no teste
			if(DominioSimNao.S.equals(vMultipla)){
				fatcBuscaInstrReg = getRelatorioResumoCobrancaAihRN().buscaInstrRegistro(vo.getIphSeq(), vo.getIphPhoSeq(), codRegistro);
			} else {
				fatcBuscaInstrReg = Boolean.FALSE;
			}
			
			// 20111229 Ney, procedimento realizado é tratado previamente, em rotina que redistribui quantidades de cirurgia multipla:
			// (rn_desdobra_multipla) 
			/* and ( ( c_ind_multipla ='S' 
					   and not ( fatc_busca_instr_reg(iph.SEQ,IPH.PHO_SEQ,'03') = 'S') 
			     	  ) or c_ind_multipla ='N' ) */
			if( ( DominioSimNao.S.equals(vMultipla) && 
					!( Boolean.TRUE.equals(fatcBuscaInstrReg) )
				 ) || DominioSimNao.N.equals(vMultipla)){
				
				if (Boolean.TRUE.equals(faturamentoRN.verificarCaracteristicaExame(vo.getIphSeq(), vo.getIphPhoSeq(), DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD))){
					vo.setIndCobrancaDiarias(DominioCobrancaDiaria.N);
				}
				
				vo.setComp(0);
				vo.setCaract(faturamentoRN.verificarCaracteristicaExame(vo.getIphSeq(), vo.getIphPhoSeq(), DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO));
				
				listaAtoMedicoAihVO.add(vo);
			}
		}

		for (CursorAtoMedicoAihVO rAtosMedicosAih : listaAtoMedicoAihVO) {
			tabAtosMedicosAih.put(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus(), rAtosMedicosAih);

			logar("r_atos_medicos_aih.iph_seq: {0}", rAtosMedicosAih.getIphSeq());
			logar("r_atos_medicos_aih.iph_pho_seq : {0}", rAtosMedicosAih.getIphPhoSeq());
			logar("r_atos_medicos_aih.iph_cod_sus: {0}", rAtosMedicosAih.getAtoMedicoAih().getIphCodSus());
			logar("r_atos_medicos_aih.quantidade: {0}", rAtosMedicosAih.getAtoMedicoAih().getQuantidade());
			
			// -- Controla a quantidade a ser inserida, pois pode ter diferença
			// entre a atos médicos com a itens.
			vCont = 0;
			vQtd = rAtosMedicosAih.getAtoMedicoAih().getQuantidade();
			vCtrIntAlta = "0";

			logar("v_qtd total: {0}", vQtd);
			logar("v_ctr_int_alta: {0}", vCtrIntAlta);
			// -- Diarias de Acompanhantes
			logar("******* DIARIAS DE ACOMPANHANTES *********");

			 /* #49259
			    14/04/2015 - Marina
			    Diarias de Acompanhantes */
			if(Boolean.FALSE.equals(faturamentoRN.verificarCaracteristicaExame(rAtosMedicosAih.getIphSeq(), rAtosMedicosAih.getIphPhoSeq(), DominioFatTipoCaractItem.NAO_SEPARA_COMPETENCIA))) {
				if(vCont.intValue() == 0) {
					vCont = rnDiariaAcompanhantes(rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
			
			/*--------------------------------------------------------------------------
			* Marina 13/10/2014 - PORTARIA 789 - separa por comp as diárias de cuidados
			*--------------------------------------------------------------------------*/
			if(vCont.intValue() == 0){
				vCont = this.rnDiariasCuidadosInter(rAtosMedicosAih, nomeMicrocomputador, dataFimVinculoServidor);
			}
			
			
			if(vCont.intValue() == 0 && rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null){

				// c_cod_sus parametro não utilizado dentro do cursor.
				List<Long> vCodTabelaRealizados = this.obterCursorCodigoTabela(pCthSeq);
				
				if (vCodTabelaRealizados.isEmpty()) {
					vCodTabelaRealizados.add(0L);
				}
				
					//------------------------------------------------------------------
				 	//-- Marina 06/12/2013 - Portaria 1151 - chamado: 115247
		         	//------------------------------------------------------------------		
		  			logar("v_cod_tabela_realizado: {0}", vCodTabelaRealizados.get(0));
		  			if (StringUtils.substring(vCodTabelaRealizados.get(0).toString(), 0, 1).equalsIgnoreCase(
		  					buscarVlrShortAghParametro(AghuParametrosEnum.P_AGHU_COD_GRUPO_PROC_CIRURGICOS).toString())) { //and p_cth_seq <> 518926
		     			
		  				if(rAtosMedicosAih.getAtoMedicoAih().getQuantidade() >=2){
		  					logar("25");
		  				} else {
		  					logar("26");
							vCont = rnQuantidadesMaximas(rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
							if(vCont.intValue() > 0){
								logar("Procedimento {0} com redistribuicao de quantidades.", rAtosMedicosAih.getAtoMedicoAih().getIphCodSus());
							}
		  				}
		  			}else{
		  				vCont = rnQuantidadesMaximas(rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						if(vCont.intValue() > 0){
							logar("Procedimento {0} com redistribuicao de quantidades.", rAtosMedicosAih.getAtoMedicoAih().getIphCodSus());
						}
		  			}
					
				}

			if(vCont.intValue() == 0){
				final Boolean fatcBuscaInstrReg = getRelatorioResumoCobrancaAihRN().buscaInstrRegistro( rAtosMedicosAih.getIphSeq(), 
																									    rAtosMedicosAih.getIphPhoSeq(), 
																									    codRegistro);
				if(Boolean.TRUE.equals(fatcBuscaInstrReg)){
					vCont = 1;
					if(rAtosMedicosAih.getAtoMedicoAih() != null && rAtosMedicosAih.getAtoMedicoAih().getFatTipoAto() != null &&
							CoreUtil.igual(rAtosMedicosAih.getAtoMedicoAih().getFatTipoAto().getCodigoSus(), taoSeq_1)){
						logar("9");
						rnInsereItens( rAtosMedicosAih.getAtoMedicoAih().getQuantidade(), 	
									   ( rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null ? Integer.valueOf(rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()) : null),
									   rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						vSeqpPrincVinculado = vSeqp;
						
					} else if(rAtosMedicosAih.getAtoMedicoAih() != null && rAtosMedicosAih.getAtoMedicoAih().getFatTipoAto() != null &&
							CoreUtil.igual(rAtosMedicosAih.getAtoMedicoAih().getFatTipoAto().getCodigoSus(), taoSeq_6)){
						logar("10");
						rnInsereItens( rAtosMedicosAih.getAtoMedicoAih().getQuantidade(), 	
								   	   ( rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null ? Integer.valueOf(rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()) : null),
								   	   rAtosMedicosAih,
								   	   pCthSeq, 
								   	   (vSeqpPrincVinculado != null ? Short.valueOf(vSeqpPrincVinculado) : null),
								   	   faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor
								   	   );
						vSeqpPrincVinculado = null;
						
					} else {
						logar("11");
						rnInsereItens( rAtosMedicosAih.getAtoMedicoAih().getQuantidade(), 	
									  (rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null ? Integer.valueOf(rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()) : null),
								       rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
					}
				}
			}
			
			// -- Se for diarias de UTI, não faz nada, simplesmente insere o que está vindo dos atos médicos
			if (rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null && vCont == 0) {
				logar("******* DIARIAS DE UTI *********");
				vCont = 1;
				logar("12");
				this.rnInsereItens(rAtosMedicosAih.getAtoMedicoAih().getQuantidade(),
								   ( rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti() != null ? Integer.valueOf(rAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()) : null), 
								   rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
			}

			// -- Busca o phi, correspondente.
			if (vCont == 0) {
				vCtrIntAlta = "S";
				vDiaAlta = 0; // -- Marina 22/08/2011
				List<CursorBuscaQtdCompVO> listaBuscaQtdCompVO = itemContaHospitalarDAO.buscarQuantidadePorCompetencia(pCthSeq,
						rAtosMedicosAih.getIphPhoSeq(), rAtosMedicosAih.getIphSeq(), rAtosMedicosAih.getAtoMedicoAih().getIphCodSus());
				for (CursorBuscaQtdCompVO rBuscaQtdPorComp : listaBuscaQtdCompVO) {
					// -- Se achou phi correspondente no caminho feliz, já
					// insere na FAT_ATOS_MEDICOS_AIH_TEMP
					logar("******************* PHI CORRESPONDENTE *******************");
					logar("v_qtd: {0}", vQtd);
					logar("r_atos_medicos_aih.eai_cth_seq: {0}", rAtosMedicosAih.getAtoMedicoAih().getId().getEaiCthSeq());
					logar("v_cont {0}", vCont);
					logar("r_busca_qtd_por_comp.qtd: {0}", rBuscaQtdPorComp.getQtd());

					vCont = 1;

					if (vQtd.shortValue() > rBuscaQtdPorComp.getQtd().shortValue()) {
						logar("IF: ");
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaQtdPorComp.getAnomes());
						// -- Marina 22/08/2011- chamado 51546
						logar("r_atos_medicos_aih.motivo: {0}", rAtosMedicosAih.getMotivo());

						if (rBuscaQtdPorComp.getAnomes().equals(rAtosMedicosAih.getAnoMesAlta())
								&& (!rAtosMedicosAih.getMotivo().equals(
										buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA))
										|| !rAtosMedicosAih.getMotivo().equals(
												buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA)) || !rAtosMedicosAih
										.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO)))) {
							vDiaAlta = (short) (rBuscaQtdPorComp.getQtd() - 1);
							if (vDiaAlta > 0){
								this.rnInsereItens(vDiaAlta.shortValue(), rBuscaQtdPorComp.getAnomes(), rAtosMedicosAih, pCthSeq, 
												   faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
							}
							
							vQtd = (short) (vQtd - vDiaAlta);
						} else {
							this.rnInsereItens(rBuscaQtdPorComp.getQtd(), rBuscaQtdPorComp.getAnomes(), rAtosMedicosAih, 
											   pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
							vQtd = (short) (vQtd - rBuscaQtdPorComp.getQtd());

						}
					} else {
						logar("ELSE");
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaQtdPorComp.getAnomes());
						logar("r_atos_medicos_aih.motivo: {0}", rAtosMedicosAih.getMotivo());
						logar("v_ctr_int_alta: {0}", vCtrIntAlta);
						logar("13");
						this.rnInsereItens(vQtd, rBuscaQtdPorComp.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, 
										   nomeMicrocomputador, dataFimVinculoServidor);
					}
				}// for
			} // vCont == 0

			logar("SAÍDA  PHI: {0}", vCont);

			if (vCont == 0) {
				// -- Busca excecão por auto relacionamento do phi
				List<CursorBuscaQtdCompVO> listaBuscaQtdCompVO = itemContaHospitalarDAO.buscarExcecaoPorAutoRelacionamentoPhi(pCthSeq,
						rAtosMedicosAih.getIphPhoSeq(), rAtosMedicosAih.getIphSeq());

				for (CursorBuscaQtdCompVO rBuscaExcPhi : listaBuscaQtdCompVO) {
					logar("******* BUSCA EXCECÃO POR AUTO RELACIONAMENTO DO PHI ***********" + vCont);
					vCont = 1;
					vDiaAlta = 0;

					// -- Se achou phi correspondente, já insere na
					// FAT_ATOS_MEDICOS_AIH_TEMP
					if (vQtd.shortValue() > rBuscaExcPhi.getQtd().shortValue()) {
						// -- Marina 22/08/2011
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaExcPhi.getAnomes());

						if (rBuscaExcPhi.getAnomes().equals(rAtosMedicosAih.getAnoMesAlta())
								&& (!rAtosMedicosAih.getMotivo().equals(
										buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA))
										|| !rAtosMedicosAih.getMotivo().equals(
												buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA)) || !rAtosMedicosAih
										.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO)))) {
							vDiaAlta = (short) (rBuscaExcPhi.getQtd() - 1);
							vQtd = (short) (vQtd - vDiaAlta);
							
							if (vDiaAlta > 0){

								logar("14");
								this.rnInsereItens(vDiaAlta.shortValue(), rBuscaExcPhi.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, 
												   nomeMicrocomputador, dataFimVinculoServidor);
							}
							
						} else {
							logar("15");
							vQtd = (short) (vQtd - rBuscaExcPhi.getQtd());
							this.rnInsereItens( rBuscaExcPhi.getQtd(), rBuscaExcPhi.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, 
											    nomeMicrocomputador, dataFimVinculoServidor);

						}
					} else {
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaExcPhi.getAnomes());
						this.rnInsereItens(vQtd, rBuscaExcPhi.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador,
										   dataFimVinculoServidor);
					}
				} // for
			}

			logar("SAIDA BUSCA EXCECÃO POR AUTO RELACIONAMENTO DO PHI: {0}", vCont);

			if (vCont == 0) {
				List<CursorBuscaQtdCompVO> listaBuscaQtdCompVO = itemContaHospitalarDAO.buscarExcecao(pCthSeq, rAtosMedicosAih.getIphPhoSeq(),
						rAtosMedicosAih.getIphSeq());

				for (CursorBuscaQtdCompVO rBuscaExc : listaBuscaQtdCompVO) {
					logar("******* BUSCA EXCECÃO ***********");
					vCont = 1;
					vDiaAlta = 0;

					// -- Se achou phi correspondente, já insere na FAT_ATOS_MEDICOS_AIH_TEMP
					if (vQtd.shortValue() > rBuscaExc.getQtd().shortValue()) {
						// -- Marina 22/08/2011
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaExc.getAnomes());

						if (rBuscaExc.getAnomes().equals(rAtosMedicosAih.getAnoMesAlta())
								&& (!rAtosMedicosAih.getMotivo().equals(
										buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA))
										|| !rAtosMedicosAih.getMotivo().equals(
												buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA)) || !rAtosMedicosAih
										.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO)))) {
							vDiaAlta = (short) (rBuscaExc.getQtd() - 1);
							vQtd = (short) (vQtd - vDiaAlta);
							if (vDiaAlta > 0){
								this.rnInsereItens(vDiaAlta.shortValue(), rBuscaExc.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, 
													nomeMicrocomputador, dataFimVinculoServidor);
							}
						} else {
							vQtd = (short) (vQtd - rBuscaExc.getQtd());
							this.rnInsereItens(rBuscaExc.getQtd(), rBuscaExc.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, 
												nomeMicrocomputador, dataFimVinculoServidor);
						}
					} else {
						tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rBuscaExc.getAnomes());
						this.rnInsereItens(vQtd, rBuscaExc.getAnomes(), rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, 
										   dataFimVinculoServidor);
					}
				} // for
			}

			logar("SAIDA BUSCA EXCECÃO: {0}", vCont);

			/*
		      if v_cont = 0 then
		      	-- Busca permanência maior
		      	v_cont := rn_permanencia_maior (r_atos_medicos_aih);
	          end if;
	          dbms_output.put_line('v_cont  rn_permanencia_maior: ' || v_cont); 
	          */
			logar("v_cont  rn_permanencia_maior: {0}", vCont); 
			
			if (vCont == 0) {
				// -- Marina 22/08/2011
				logar("*** VERIFICA SE É ATO OBRIGATORIO ****");
				VerificaAtoObrigatorioVO verificaAtoObrigatorioVO = verificaAtoObrigatorio(rAtosMedicosAih.getIphPhoSeq(),
						rAtosMedicosAih.getIphSeq(), pCthSeq, vVlrTexto, vDtComp);
				if (verificaAtoObrigatorioVO != null) {
					vVlrTexto = verificaAtoObrigatorioVO.getDescricao();
					vVlrData = verificaAtoObrigatorioVO.getDthrRealizado();
					if (verificaAtoObrigatorioVO.getDthrRealizado() != null) {
						vDtComp = Integer.parseInt(DateUtil.obterDataFormatada(verificaAtoObrigatorioVO.getDthrRealizado(), CONST_YYYY_MM));
					}
				}
				if (Boolean.TRUE.equals(verificaAtoObrigatorioVO.getRetorno())) {
					logar("*** É ATO OBRIGATORIO **** {0}" + vVlrData);
					tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(vDtComp);
					this.rnInsereItens(rAtosMedicosAih.getAtoMedicoAih().getQuantidade(), vDtComp, rAtosMedicosAih, pCthSeq, faturamentoFacade,
										nomeMicrocomputador, dataFimVinculoServidor);
				} else {
					logar("*** NÃO É ATO OBRIGATORIO ****  {0}" , vVlrData);
					tabAtosMedicosAih.get(rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()).setComp(rAtosMedicosAih.getAnoMesAlta());
					logar("16");
					this.rnInsereItens(rAtosMedicosAih.getAtoMedicoAih().getQuantidade(), rAtosMedicosAih.getAnoMesAlta(), 
										rAtosMedicosAih, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} // for
		logar("Saiu do Processa Atos Médicos");
	}
	
	// Simula function SQL: to_date(p_atos_medicos.competencia_uti,'yyyymm')
	private Date getCompetencia(final CursorAtoMedicoAihVO pAtosMedicosAih){
		final Date day = DateUtil.obterData(Integer.parseInt(pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti().substring(0,4)), 
											Integer.parseInt(pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti().substring(4)), 
											01);
		
		final Date competencia = DateUtil.obterDataInicioCompetencia(day);
		
		return competencia;
	}

	private Integer rnQuantidadesMaximas(final CursorAtoMedicoAihVO pAtosMedicosAih, final Integer cthSeq, final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final Byte atoCirur = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO);
		
		int found = 0;
		int vQtdeInserir = 0;
		int vQtdeInserida = 0;
		
		Date competencia = null;
		SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy");
		try {
			//problemas com o Calendar para horário de verão
			competencia = fd.parse(MessageFormat.format("{0}/{1}/{2}", "01",
					pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti().substring(4),
					pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti().substring(0, 4)));
		} catch (ParseException e1) {
			competencia = getCompetencia(pAtosMedicosAih);
		}
		
		// cursor c_maximas
		List<CursorMaximasAtosMedicoAihTempVO> vos = getFatAtoMedicoAihTempDAO().obterQtMaximasAtosMedicoAihTemp( pAtosMedicosAih.getIphPhoSeq(), 
																												  pAtosMedicosAih.getIphSeq(), 
																												  cthSeq, 
																												  atoCirur, 
																												  CONST_999, 
																												  competencia
																												 );
		
		final int pAtosMedicosQuantidade = pAtosMedicosAih.getAtoMedicoAih().getQuantidade();
		logar("**** verifica_qtd_maxima ***");
		logar("p_atos_medicos.iph_pho_seq: {0}", pAtosMedicosAih.getIphPhoSeq()); 
		logar("p_atos_medicos.iph_seq: {0}", pAtosMedicosAih.getIphSeq());
		logar("Competencia {0}: ", pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti());
		for (CursorMaximasAtosMedicoAihTempVO rMaximas : vos) {
			logar("Procedimento {0} qtd={1} p_atos_medicos.quantidade={2}", rMaximas.getIphCodSus(),rMaximas.getQuantidadeMaxima(),pAtosMedicosQuantidade);
			logar("Procedimento"+ rMaximas.getIphCodSus() +" || qtd="+ rMaximas.getQuantidadeMaxima() +"'||' p_atos_medicos.quantidade="+ pAtosMedicosQuantidade);
			if(rMaximas.getQuantidadeMaxima() != null && (pAtosMedicosQuantidade  >= rMaximas.getQuantidadeMaxima().intValue())){
				if(vQtdeInserir == 0){
					vQtdeInserir = pAtosMedicosQuantidade;
				} /*else if(vQtdeInserir > vQtdeInserida){
					vQtdeInserir -= vQtdeInserida;
				} else if(vQtdeInserir > vQtdeInserida) {
					vQtdeInserir = vQtdeInserir - vQtdeInserida;
				}*/
			}

		    logar("v_qtde_inserida: {0}", vQtdeInserida);
		    logar("v_qtde_inserir: {0}", vQtdeInserir);		    
		    
			if(vQtdeInserida < vQtdeInserir) { // teste Marina
				vQtdeInserida += rMaximas.getQuantidadeMaxima().intValue();
				logar("3");
				logar("r_maximas.QUANTIDADE_MAXIMA: ", rMaximas.getQuantidadeMaxima());
				
				try {
					rnInsereItens( rMaximas.getQuantidadeMaxima(), 
								   Integer.valueOf(pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()), 
								   pAtosMedicosAih, 
								   cthSeq,
								   (short) rMaximas.getSeqp(),
								   faturamentoFacade, nomeMicrocomputador,
								   dataFimVinculoServidor
								 );
					found = 1;

					
				} catch (NumberFormatException e) {
					throw new ApplicationBusinessException(	FaturamentoExceptionCode.FAT_00527);
				}
			} /*else if(vQtdeInserida != 0 && vQtdeInserir != 0) { 	// Marina 10/09/2013
				try {
					logar("entrou no elseif");
						rnInsereItens( (short) vQtdeInserir, 
										Integer.valueOf(pAtosMedicosAih.getAtoMedicoAih().getCompetenciaUti()), 
										pAtosMedicosAih, 
										cthSeq,
										(short) rMaximas.getSeqp(),
										faturamentoFacade, nomeMicrocomputador,
										dataFimVinculoServidor
									);
						found = 1;
				} catch (NumberFormatException e) {
					throw new ApplicationBusinessException(	FaturamentoExceptionCode.FAT_00527);
				}
			}*/
		}
		
		if(vQtdeInserida < vQtdeInserir){
			logar("Redestribuição de quantidades máximas com erro, cod:{0} qtde realizada={1} qtde inserida={2}", 
					pAtosMedicosAih.getAtoMedicoAih().getIphCodSus(),vQtdeInserir,vQtdeInserida);
		}
		
		return found;
	}

	/**
	 * Function RN_DIARIAS_ACOMPANHANTES
	 * 
	 * @param pDiariasAcomp
	 * @param faturamentoFacade 
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected Integer rnDiariaAcompanhantes(CursorAtoMedicoAihVO pDiariasAcomp, Integer pCthSeq, final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		Short vDiariasAcomp = 0;
		Long vCodAcomp;
		Integer result = 0;
		Integer vAnoMes = null;
		Integer vDiasMes = null;
		Integer vDiminuiMes = 1;
		DominioCobrancaDiaria vCtlQtdMaior = DominioCobrancaDiaria.N;

		if (!nvl(pDiariasAcomp.getIndCobrancaDiarias(), DominioCobrancaDiaria.N).equals(DominioCobrancaDiaria.S)
				&& !pDiariasAcomp.getCaract().equals(Boolean.TRUE)) {
			// -- Busca parâmetro para ver se existe diarias de acompanhantes.
			// Pediatrico

			vCodAcomp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_PEDIATRIA);
			logar("v_cod_acomp: {0}", vCodAcomp);

			if (!vCodAcomp.equals(pDiariasAcomp.getAtoMedicoAih().getIphCodSus())) { // --1
				// --paciente adulto
				vCodAcomp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_ADULTO);

				if (!vCodAcomp.equals(pDiariasAcomp.getAtoMedicoAih().getIphCodSus())) { // --2
					vCodAcomp = buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_IDOSO);

					if (!vCodAcomp.equals(pDiariasAcomp.getAtoMedicoAih().getIphCodSus())) { // --3
						if (!pDiariasAcomp.getAtoMedicoAih().getIphCodSus()
								.equals(buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_SUS_TRATAMENTO_EM_PSIQUIATRIA_HOSPITAL_GERAL_POR_DIA))) {
							// TRATAMENTO EM PSIQUIATRIA (EM HOSPITAL GERAL)
							// (POR DIA)
							// -- Marina 14/06/2011
							result = 0;
							return result;
						}
					} // 3
				} // 2
			} // 1
		}

		vDiariasAcomp = pDiariasAcomp.getAtoMedicoAih().getQuantidade();
		// -- Se ele chegou até aqui porque achou um cod_sus correspondente a diárias de acompanhantes
		logar("p_diarias_acomp.quantidade: {0}", pDiariasAcomp.getAtoMedicoAih().getQuantidade());
		logar("p_diarias_acomp.DIA_DA_ALTA: {0}", pDiariasAcomp.getDiaDaAlta());

		vCtlQtdMaior = DominioCobrancaDiaria.S;

		if (pDiariasAcomp.getDiaDaAlta().shortValue() > pDiariasAcomp.getAtoMedicoAih().getQuantidade().shortValue()
				&& pDiariasAcomp.getAnoMesAlta().intValue() >= CONST_201105.intValue()) {
			tabAtosMedicosAih.get(pDiariasAcomp.getAtoMedicoAih().getIphCodSus()).setComp(pDiariasAcomp.getAnoMesAlta());
			logar("p_diarias_acomp.motivo: {0}", pDiariasAcomp.getMotivo());
			Integer competenciaUti = pDiariasAcomp.getAtoMedicoAih().getCompetenciaUti() != null ? Integer.valueOf(StringUtils.trim(pDiariasAcomp.getAtoMedicoAih().getCompetenciaUti())) : null;
			logar("p_diarias_acomp.COMPETENCIA_UTI: {0}", competenciaUti);
			logar("4");

			// MARINA 25/02/2014 -  CHAMADO: 123510
			if (competenciaUti == null){
				this.rnInsereItens(pDiariasAcomp.getAtoMedicoAih().getQuantidade(), pDiariasAcomp.getAnoMesAlta(), pDiariasAcomp, pCthSeq, 
						faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
			} else {
				this.rnInsereItens(pDiariasAcomp.getAtoMedicoAih().getQuantidade(), competenciaUti, pDiariasAcomp, pCthSeq,
						faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
			}
			result = 1;
		} else if (pDiariasAcomp.getAnoMesAlta().intValue() <= CONST_201104_PORTARIA_203.intValue()) {
			tabAtosMedicosAih.get(pDiariasAcomp.getAtoMedicoAih().getIphCodSus()).setComp(pDiariasAcomp.getAnoMesAlta());
			logar("p_diarias_acomp.motivo: {0}", pDiariasAcomp.getMotivo());
			logar("5");
			this.rnInsereItens(pDiariasAcomp.getAtoMedicoAih().getQuantidade(), CONST_201104_PORTARIA_203, pDiariasAcomp, pCthSeq, 
								faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
			logar("depois v_dias_mes: {0}", vDiasMes);
			result = 1;
		} else if (pDiariasAcomp.getAnoMesAlta().equals(pDiariasAcomp.getAnoMesInt())) {
			if (Boolean.TRUE.equals(pDiariasAcomp.getIndQtdMaiorInternacao())) {
				this.rnInsereItens(pDiariasAcomp.getAtoMedicoAih().getQuantidade(), pDiariasAcomp.getAnoMesAlta(), pDiariasAcomp, pCthSeq, 
									faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				result = 1;
			}
		} else {
			tabAtosMedicosAih.get(pDiariasAcomp.getAtoMedicoAih().getIphCodSus()).setComp(pDiariasAcomp.getAnoMesAlta());

			// -- Marina 22/08/2011
			if (!pDiariasAcomp.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA)) // 2
					&& !pDiariasAcomp.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA)) // 3
					&& !pDiariasAcomp.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO)) // 10
					&& !pDiariasAcomp.getMotivo().equals(buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OUTROS_MOTIVOS))) { // 11)
				vDiaAlta = (short) (pDiariasAcomp.getDiaDaAlta() - 1);
				logar("v_dia_alta: {0}", vDiaAlta);

				if (vDiaAlta > 0) {
					logar("** 1***");
					this.rnInsereItens(vDiaAlta, pDiariasAcomp.getAnoMesAlta(), pDiariasAcomp, pCthSeq, faturamentoFacade,
										nomeMicrocomputador, dataFimVinculoServidor);
					vDiariasAcomp = (short) (pDiariasAcomp.getAtoMedicoAih().getQuantidade() - vDiaAlta);
				}
			} else {
				logar("2");
				this.rnInsereItens(pDiariasAcomp.getDiaDaAlta().shortValue(), pDiariasAcomp.getAnoMesAlta(), pDiariasAcomp, pCthSeq, 
									faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				vDiariasAcomp = (short) (pDiariasAcomp.getAtoMedicoAih().getQuantidade() - pDiariasAcomp.getDiaDaAlta());
			}
			// Fim Marina 22/08/2011
			logar("v_diarias_acomp: {0}", vDiariasAcomp);
			logar(" *** loop *** ");
			BREAK_WHILE:
			while (true) {
				logar(" p_cth_seq: {0}",pCthSeq);
				logar(" ESTOU NO LOOP");
				
				if (vDiariasAcomp == 0 || (vAnoMes != null && vAnoMes.equals(pDiariasAcomp.getAnoMesInt()))) {
					break BREAK_WHILE;
				}

				logar("x - v_diminui_mes: {0}", vDiminuiMes);
				logar("x - v_ano_mes: {0}", vAnoMes);

				String auxAnoMes = DateUtil.dataToString(DateUtil.adicionaMeses(pDiariasAcomp.getDtAltaAdministrativa(), - vDiminuiMes), CONST_YYYY_MM);
				vAnoMes = Integer.parseInt(auxAnoMes);

				logar("depois v_ano_mes: {0}", vAnoMes);

				if (vAnoMes.intValue() >= CONST_201105.intValue()) {
					Date auxDataDiasMes = DateUtil.obterDataFimCompetencia(obterDataCompetencia(vAnoMes));
					if (vAnoMes.intValue() > pDiariasAcomp.getAnoMesInt().intValue()) {
						vDiasMes = Integer.parseInt(DateUtil.dataToString(auxDataDiasMes, CONST_DD));
					} else if (vAnoMes.equals(pDiariasAcomp.getAnoMesInt())) {
						logar("#### mes e ano iguais ########");
						vDiasMes = Integer.parseInt(DateUtil.dataToString(auxDataDiasMes, CONST_DD)) - pDiariasAcomp.getDiaMesInt();
						vDiasMes++;
						vCtlQtdMaior = DominioCobrancaDiaria.S;
					}

					if (Boolean.TRUE.equals(pDiariasAcomp.getIndQtdMaiorInternacao()) && DominioCobrancaDiaria.S.equals(vCtlQtdMaior)) {
						this.rnInsereItens(vDiariasAcomp, vAnoMes, pDiariasAcomp, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						result = 1;
						break;
					}

					logar("v_dias_mes: {0}", vDiasMes);
					logar("v_diarias_acomp: {0}", vDiariasAcomp);

					if (vDiariasAcomp.intValue() <= vDiasMes.intValue()) {
						this.rnInsereItens(vDiariasAcomp, vAnoMes, pDiariasAcomp, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor
								);
						vDiariasAcomp = 0;
					} else {
						logar("7");
						this.rnInsereItens(vDiasMes.shortValue(), vAnoMes, pDiariasAcomp, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
						vDiariasAcomp = (short) (vDiariasAcomp - vDiasMes);
					}
				} else {
					logar("8");
					this.rnInsereItens(vDiariasAcomp, CONST_201104_PORTARIA_203, pDiariasAcomp, pCthSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
					vDiariasAcomp = 0;
				}
				vDiminuiMes++;
				logar("y - v_diminui_mes: {0}", vDiminuiMes);
			} // while true
			result = 1;
		} // if

		logar(" ********* FIM DIARIAS DE ACOMPANHANTES: {0}", result);

		return result;
	}

	
	// Simula alteração levantada em #20458: p_seqp_vinculado in number default null)
	protected void rnInsereItens(Short pQtde, Integer pComp, CursorAtoMedicoAihVO pCursor, Integer pCthSeq, final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, 
			final Date dataFimVinculoServidor) throws BaseException {
		rnInsereItens(pQtde, pComp, pCursor, pCthSeq, null, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * Function RN_INSERE_ITENS
	 * 
	 * @param pQtde
	 * @param pComp
	 * @param pCursor
	 * @param faturamentoFacade 
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void rnInsereItens(Short pQtde, Integer pComp, CursorAtoMedicoAihVO pCursor, Integer pCthSeq, final Short pSeqpVinculado, 
								 final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, 
								 final Date dataFimVinculoServidor) throws BaseException {

		Boolean alterouValor = Boolean.FALSE;
		BigDecimal vVlrSh = null;
		BigDecimal vVlrSadt = null;
		BigDecimal vVlrProc = null;
		BigDecimal vVlrAnest = null;
		BigDecimal vVlrSp = null;
		Byte vMaxAam = null;
		Integer vComp = null;
		String vCbo = null;
		Long vCpfCns = null;

		// -- Busca quantidade maxima de atos medicos por página de espelho de AIH
		vMaxAam = buscarVlrByteAghParametro(AghuParametrosEnum.P_MAX_ATO_MEDICO_AIH);
		vSeqp++;

		if (vSeqp > vMaxAam) {
			vEaiSeq++;
			vSeqp = 1;
		}

		this.verificaEspelho(pCursor.getAtoMedicoAih().getId().getEaiCthSeq(), vEaiSeq, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);

		if (pCursor.getAtoMedicoAih().getQuantidade().shortValue() > 0) {
			logar("p_cursor.quantidade > 0 ");
			vVlrSh = pCursor.getAtoMedicoAih().getValorServHosp()
							.multiply(new BigDecimal(pQtde))
							.divide(new BigDecimal(pCursor.getAtoMedicoAih().getQuantidade()))
							.setScale(2, RoundingMode.HALF_UP);
			
			vVlrSadt = pCursor.getAtoMedicoAih().getValorSadt()
							  .multiply(new BigDecimal(pQtde))
							  .divide(new BigDecimal(pCursor.getAtoMedicoAih().getQuantidade()))
							  .setScale(2, RoundingMode.HALF_UP);
			
			vVlrProc = pCursor.getAtoMedicoAih().getValorProcedimento()
							  .multiply(new BigDecimal(pQtde))
							  .divide(new BigDecimal(pCursor.getAtoMedicoAih().getQuantidade()))
							  .setScale(2, RoundingMode.HALF_UP);
			
			vVlrAnest = pCursor.getAtoMedicoAih().getValorAnestesista()
							   .multiply(new BigDecimal(pQtde))
							   .divide(new BigDecimal(pCursor.getAtoMedicoAih().getQuantidade()))
							   .setScale(2, RoundingMode.HALF_UP);
			
			vVlrSp = pCursor.getAtoMedicoAih().getValorServProf()
							.multiply(new BigDecimal(pQtde))
							.divide(new BigDecimal(pCursor.getAtoMedicoAih().getQuantidade()))
							.setScale(2, RoundingMode.HALF_UP);
		}
		logar("p_comp: {0}", pComp);

		if (pComp != null && DominioModoCobranca.V.equals(pCursor.getAtoMedicoAih().getIndModoCobranca())) {
			RnIphcVerAlteracaoVO rnIphcVerAlteracaoVO = rnIphcVerAlteracao(pCursor.getIphPhoSeq(), pCursor.getIphSeq(), vVlrSh, vVlrSadt, vVlrProc,
					vVlrAnest, vVlrSp, pComp, pQtde, pCursor);
			if (rnIphcVerAlteracaoVO != null) {
				alterouValor = rnIphcVerAlteracaoVO.getRetorno();
				vVlrSh = rnIphcVerAlteracaoVO.getvVlrServHospitalar();
				vVlrSadt = rnIphcVerAlteracaoVO.getvVlrSadt();
				vVlrProc = rnIphcVerAlteracaoVO.getvVlrProcedimento();
				vVlrAnest = rnIphcVerAlteracaoVO.getvVlrAnestesista();
				vVlrSp = rnIphcVerAlteracaoVO.getvVlrServProfissional();
			}

			if (Boolean.TRUE.equals(alterouValor)) {
				logar("Valor do procedimento {0} alterado", pCursor.getAtoMedicoAih().getIphCodSus());
				alterarValorConta = Boolean.TRUE;
			}
		}

		logar("p_cursor.ANO_MES_INT: {0}", pCursor.getAnoMesInt());
		logar("p_cursor.ANO_MES_alta: {0}", pCursor.getAnoMesAlta());
		
		if (pCursor.getAtoMedicoAih().getCompetenciaUti() != null && 
		   (pCursor.getAtoMedicoAih().getIphCodSus().equals(buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_ADULTO_INI))) || //802010091
		   (pCursor.getAtoMedicoAih().getIphCodSus().equals(buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_PEDIATRIA_INI))) || //802010075
		   (pCursor.getAtoMedicoAih().getIphCodSus().equals(buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_3_NEONATAL_INI))) || //802010130
		   (pCursor.getAtoMedicoAih().getIphCodSus().equals(buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_UTI_BUSCA_ATIVA_MAIOR_2ANOS)))) { //503040045
			vComp = pComp;
		} else if (pCursor.getAnoMesAlta().intValue() < CONST_201105.intValue() || pComp == null || pComp.intValue() < CONST_201105.intValue()) {
			vComp = null;
		} else if (pCursor.getAnoMesAlta().intValue() >= CONST_201105.intValue() || pComp.intValue() >= CONST_201105.intValue()) {
			vComp = pComp;
		}
		
		logar("XXXX v_seqp: {0}", vSeqp);
		logar("XXXX v_comp: {0}", vComp);
		logar("XXXX p_cursor.IPH_COD_SUS: {0}", pCursor.getAtoMedicoAih().getIphCodSus());
		logar("XXXX p_comp: {0}", pComp);
		logar("XXXX p_qtde: {0}", pQtde);
		logar("XXXX p_cursor.tao_seq: {0}", 
				pCursor.getAtoMedicoAih().getFatTipoAto() == null ? null : pCursor.getAtoMedicoAih().getFatTipoAto().getCodigoSus());
		
		vCbo = verificaCbo(pCursor.getAtoMedicoAih().getCbo(), vComp);

		// Marina 17/01/2012
		if(pCursor.getAtoMedicoAih().getCpfCns() != null && pComp.intValue() >= CONST_201201.intValue()){
			final String cpfCns = getFatkCthnRN().aipcGetCnsResp(pCursor.getAtoMedicoAih().getCpfCns());
			vCpfCns = cpfCns != null ? Long.valueOf(cpfCns) : null;
		} else {
			vCpfCns = pCursor.getAtoMedicoAih().getCpfCns();
		}

		logar("CNS: {0}", vCpfCns);
		
		FatAtoMedicoAihTemp atoMedicoAihTemp = new FatAtoMedicoAihTemp();
		FatAtoMedicoAihTempId id = new FatAtoMedicoAihTempId();
		id.setEaiSeq(vEaiSeq);
		id.setEaiCthSeq(pCursor.getAtoMedicoAih().getId().getEaiCthSeq());
		id.setSeqp(vSeqp);
		atoMedicoAihTemp.setId(id);

		atoMedicoAihTemp.setQuantidade(pQtde);
		atoMedicoAihTemp.setCriadoPor(pCursor.getAtoMedicoAih().getCriadoPor());
		atoMedicoAihTemp.setCriadoEm(pCursor.getAtoMedicoAih().getCriadoEm());
		atoMedicoAihTemp.setAlteradoPor(pCursor.getAtoMedicoAih().getAlteradoPor());
		atoMedicoAihTemp.setAlteradoEm(pCursor.getAtoMedicoAih().getAlteradoEm());
		atoMedicoAihTemp.setNotaFiscal(pCursor.getAtoMedicoAih().getNotaFiscal());
		atoMedicoAihTemp.setIphSeq(pCursor.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getSeq());
		atoMedicoAihTemp.setIphPhoSeq(pCursor.getAtoMedicoAih().getItemProcedimentoHospitalar().getId().getPhoSeq());
		atoMedicoAihTemp.setTaoSeq(pCursor.getAtoMedicoAih().getFatTipoAto() == null ? null : pCursor.getAtoMedicoAih().getFatTipoAto().getCodigoSus());
		atoMedicoAihTemp.setTivSeq(pCursor.getAtoMedicoAih().getFatTiposVinculo() == null ? null : pCursor.getAtoMedicoAih().getFatTiposVinculo().getCodigoSus().byteValue());
		atoMedicoAihTemp.setPontosAnestesista(pCursor.getAtoMedicoAih().getPontosAnestesista());
		atoMedicoAihTemp.setPontosCirurgiao(pCursor.getAtoMedicoAih().getPontosCirurgiao());
		atoMedicoAihTemp.setPontosSadt(pCursor.getAtoMedicoAih().getPontosSadt());
		atoMedicoAihTemp.setDataPrevia(pCursor.getAtoMedicoAih().getDataPrevia());
		atoMedicoAihTemp.setValorAnestesista(vVlrAnest);
		atoMedicoAihTemp.setValorProcedimento(vVlrProc);
		atoMedicoAihTemp.setValorSadt(vVlrSadt);
		atoMedicoAihTemp.setValorServProf(vVlrSp);
		atoMedicoAihTemp.setValorServHosp(vVlrSh);
		atoMedicoAihTemp.setIndConsistente(pCursor.getAtoMedicoAih().getIndConsistente());
		atoMedicoAihTemp.setIndModoCobranca(pCursor.getAtoMedicoAih().getIndModoCobranca());
		atoMedicoAihTemp.setIphCodSus(pCursor.getAtoMedicoAih().getIphCodSus());
		atoMedicoAihTemp.setCompetenciaUti(vComp == null ? null : vComp.toString());
		atoMedicoAihTemp.setIndEquipe(pCursor.getAtoMedicoAih().getIndEquipe());
		atoMedicoAihTemp.setCpfCns(vCpfCns);
		atoMedicoAihTemp.setSerieOpm(pCursor.getAtoMedicoAih().getSerieOpm());
		atoMedicoAihTemp.setLoteOpm(pCursor.getAtoMedicoAih().getLoteOpm());
		atoMedicoAihTemp.setRegAnvisaOpm(pCursor.getAtoMedicoAih().getRegAnvisaOpm());
		atoMedicoAihTemp.setCnpjRegAnvisa(pCursor.getAtoMedicoAih().getCnpjRegAnvisa());
		atoMedicoAihTemp.setCgc(pCursor.getAtoMedicoAih().getCgc());
		atoMedicoAihTemp.setSeqArqSus(CONST_999);
		atoMedicoAihTemp.setSeqpVinculado(pSeqpVinculado);
		atoMedicoAihTemp.setCbo(vCbo);

		getFatAtoMedicoAihTempDAO().persistir(atoMedicoAihTemp);
		getFatAtoMedicoAihTempDAO().flush();

		// Ajusta os novos valores  em itens espelho:
		verificaItemEspelho( pCursor.getAtoMedicoAih().getIphCodSus(), 
							 pCursor.getAtoMedicoAih().getFatTiposVinculo() == null ? 0 : pCursor.getAtoMedicoAih().getFatTiposVinculo().getCodigoSus().byteValue(), 
							 pCursor.getAtoMedicoAih().getFatTipoAto() == null ? 0 : pCursor.getAtoMedicoAih().getFatTipoAto().getCodigoSus(), 
							 pCursor.getIphSeq(), vVlrSh, vVlrSadt, vVlrProc, vVlrAnest, vVlrSp, vComp, pQtde,
							 pCthSeq, faturamentoFacade);
	}

	/**
	 * Procedure RN_IPHC_VER_ALTERACAO
	 * 
	 * @param pPhoSeq
	 * @param pIphSeq
	 * @param pVlrServHospitalar
	 * @param pVlrSadt
	 * @param pVlrProcedimento
	 * @param pVlrAnestesista
	 * @param pVlrServProfissional
	 * @param pComp
	 * @param pQtde
	 * @param pCursor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RnIphcVerAlteracaoVO rnIphcVerAlteracao(Short pPhoSeq, Integer pIphSeq, BigDecimal pVlrServHospitalar, BigDecimal pVlrSadt,
			BigDecimal pVlrProcedimento, BigDecimal pVlrAnestesista, BigDecimal pVlrServProfissional, Integer pComp, Short pQtde,
			CursorAtoMedicoAihVO pCursor) throws ApplicationBusinessException {

		RnIphcVerAlteracaoVO retorno = new RnIphcVerAlteracaoVO();

		VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN = getVerificacaoItemProcedimentoHospitalarRN();

		Boolean vRet = Boolean.FALSE;
		BigDecimal vVlrSh = pVlrServHospitalar;
		BigDecimal vVlrSadt = pVlrSadt;
		BigDecimal vVlrProc = pVlrProcedimento;
		BigDecimal vVlrAnest = pVlrAnestesista;
		BigDecimal vVlrSp = pVlrServProfissional;


		// Ao incluir com competencia anterior ao mes alta
		if (pComp.intValue() < pCursor.getAnoMesAlta().intValue()) {
			
			FatVlrItemProcedHospComps vlrAnt = verificacaoItemProcedimentoHospitalarRN.obterValoresItemProcHospPorModuloCompetencia(pPhoSeq, pIphSeq,
					DominioModuloCompetencia.INT, obterDataCompetencia(pComp));
			FatVlrItemProcedHospComps vlrAtu = null;

			if (vlrAnt != null){
				vlrAtu = verificacaoItemProcedimentoHospitalarRN.obterValoresItemProcHospPorModuloCompetencia(pPhoSeq, pIphSeq,
						DominioModuloCompetencia.INT, obterDataCompetencia(pCursor.getAnoMesAlta()));
			}

			if (vlrAtu != null) {
				if (CoreUtil.modificados(vlrAnt.getVlrServHospitalar(), vlrAtu.getVlrServHospitalar())
						|| CoreUtil.modificados(vlrAnt.getVlrSadt(), vlrAtu.getVlrSadt())
						|| CoreUtil.modificados(vlrAnt.getVlrProcedimento(), vlrAtu.getVlrProcedimento())
						|| CoreUtil.modificados(vlrAnt.getVlrAnestesista(), vlrAtu.getVlrAnestesista())
						|| CoreUtil.modificados(vlrAnt.getVlrServProfissional(), vlrAtu.getVlrServProfissional())) {
					vRet = Boolean.TRUE;
				}
			}
			
			// -- Ao incluir com competencia anterior ao mes alta
			// -- Verificar se houve alteração de valores comparando com mes anterior
			if (Boolean.TRUE.equals(vRet)) {
				BigDecimal divisor = null;
				if (pVlrServHospitalar.intValue() > 0){
					divisor = vlrAnt.getVlrServHospitalar().divide(vlrAtu.getVlrServHospitalar()).setScale(2,RoundingMode.HALF_UP);
					vVlrSh = pVlrServHospitalar.multiply(divisor).setScale(2,RoundingMode.HALF_UP);
				}
				
				if (pVlrSadt.intValue() > 0){
					divisor = vlrAnt.getVlrSadt().divide(vlrAtu.getVlrSadt()).setScale(2,RoundingMode.HALF_UP);
					vVlrSadt = pVlrSadt.multiply(divisor).setScale(2, RoundingMode.HALF_UP);
				}

				if (pVlrProcedimento.intValue() > 0){
					divisor = vlrAnt.getVlrProcedimento().divide(vlrAtu.getVlrProcedimento()).setScale(2,RoundingMode.HALF_UP);
					vVlrProc = pVlrProcedimento.multiply(divisor).setScale(2,RoundingMode.HALF_UP);
				}

				if (pVlrAnestesista.intValue() > 0){
					divisor = vlrAnt.getVlrAnestesista().divide(vlrAtu.getVlrAnestesista()).setScale(2,RoundingMode.HALF_UP);
					vVlrAnest = pVlrAnestesista.multiply(divisor).setScale(2,RoundingMode.HALF_UP);
				}

				if (pVlrServProfissional.intValue() > 0){
					divisor = vlrAnt.getVlrServProfissional().divide(vlrAtu.getVlrServProfissional()).setScale(2,RoundingMode.HALF_UP);
					vVlrSp = pVlrServProfissional.multiply(divisor).setScale(2,RoundingMode.HALF_UP);
				}
			}
		}

		retorno.setRetorno(vRet);
		retorno.setvVlrAnestesista(vVlrAnest);
		retorno.setvVlrProcedimento(vVlrProc);
		retorno.setvVlrSadt(vVlrSadt);
		retorno.setvVlrServHospitalar(vVlrSh);
		retorno.setvVlrServProfissional(vVlrSp);

		return retorno;
	}

	/**
	 * Procedure VERIFICA_ESPELHO
	 * 
	 * @param pCthSeq
	 * @param pSeqp
	 * @param faturamentoFacade 
	 * @throws BaseException 
	 */
	protected void verificaEspelho(Integer pCthSeq, Integer pSeqp, final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		FatEspelhoAihDAO espelhoAihDAO = getFatEspelhoAihDAO();
		FatEspelhoAih regEspelho = espelhoAihDAO.obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, pSeqp));
		if (regEspelho == null) {
			regEspelho = espelhoAihDAO.obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, pSeqp - 1));

			if (regEspelho == null){
				logar("Não entrou espelho para inserir, p_seqp={0}", pSeqp);
			} else {
				logar("insere espelho aih: eai_seqp {0}", pSeqp);

				FatEspelhoAih elemento = new FatEspelhoAih();
				elemento.setId(new FatEspelhoAihId(regEspelho.getId().getCthSeq(), pSeqp));
				elemento.setNroDiasMesInicialUti(regEspelho.getNroDiasMesInicialUti());
				elemento.setNroDiasMesAnteriorUti(regEspelho.getNroDiasMesAnteriorUti());
				elemento.setNroDiasMesAltaUti(regEspelho.getNroDiasMesAltaUti());
				elemento.setNroDiariasAcompanhante(regEspelho.getNroDiariasAcompanhante());
				elemento.setTahSeq(regEspelho.getTahSeq());
				elemento.setDataPrevia(regEspelho.getDataPrevia());
				elemento.setDciCodigoDcih(regEspelho.getDciCodigoDcih());
				elemento.setDciCpeDtHrInicio(regEspelho.getDciCpeDtHrInicio());
				elemento.setDciCpeModulo(regEspelho.getDciCpeModulo());
				elemento.setDciCpeMes(regEspelho.getDciCpeMes());
				elemento.setDciCpeAno(regEspelho.getDciCpeAno());
				elemento.setEspecialidadeDcih(regEspelho.getEspecialidadeDcih());
				elemento.setPacProntuario(regEspelho.getPacProntuario());
				elemento.setPacNome(regEspelho.getPacNome());
				elemento.setEndLogradouroPac(regEspelho.getEndLogradouroPac());
				elemento.setEndNroLogradouroPac(regEspelho.getEndNroLogradouroPac());
				elemento.setEndCmplLogradouroPac(regEspelho.getEndCmplLogradouroPac());
				elemento.setEndCidadePac(regEspelho.getEndCidadePac());
				elemento.setEndUfPac(regEspelho.getEndUfPac());
				elemento.setEndCepPac(regEspelho.getEndCepPac());
				elemento.setPacDtNascimento(regEspelho.getPacDtNascimento());
				elemento.setPacSexo(regEspelho.getPacSexo());
				elemento.setNumeroAih(regEspelho.getNumeroAih());
				elemento.setEnfermaria(regEspelho.getEnfermaria());
				elemento.setLeito(regEspelho.getLeito());
				elemento.setNomeResponsavelPac(regEspelho.getNomeResponsavelPac());
				elemento.setPacCpf(regEspelho.getPacCpf());
				elemento.setPacNroCartaoSaude(regEspelho.getPacNroCartaoSaude());
				elemento.setCpfMedicoSolicRespons(regEspelho.getCpfMedicoSolicRespons());
				elemento.setIphPhoSeqSolic(regEspelho.getIphPhoSeqSolic());
				elemento.setIphSeqSolic(regEspelho.getIphSeqSolic());
				elemento.setIphCodSusSolic(regEspelho.getIphCodSusSolic());
				elemento.setIphPhoSeqRealiz(regEspelho.getIphPhoSeqRealiz());
				elemento.setIphSeqRealiz(regEspelho.getIphSeqRealiz());
				elemento.setIphCodSusRealiz(regEspelho.getIphCodSusRealiz());
				elemento.setTciCodSus(regEspelho.getTciCodSus());
				elemento.setAihDthrEmissao(regEspelho.getAihDthrEmissao());
				elemento.setEspecialidadeAih(regEspelho.getEspecialidadeAih());
				elemento.setDataInternacao(regEspelho.getDataInternacao());
				elemento.setDataSaida(regEspelho.getDataSaida());
				elemento.setCidPrimario(regEspelho.getCidPrimario());
				elemento.setCidSecundario(regEspelho.getCidSecundario());
				elemento.setNascidosVivos(regEspelho.getNascidosVivos());
				elemento.setNascidosMortos(regEspelho.getNascidosMortos());
				elemento.setSaidasAlta(regEspelho.getSaidasAlta());
				elemento.setSaidasTransferencia(regEspelho.getSaidasTransferencia());
				elemento.setSaidasObito(regEspelho.getSaidasObito());
				elemento.setNumeroAihPosterior(regEspelho.getNumeroAihPosterior());
				elemento.setNumeroAihAnterior(regEspelho.getNumeroAihAnterior());
				elemento.setCodIbgeCidadePac(regEspelho.getCodIbgeCidadePac());
				elemento.setGrauInstrucaoPac(regEspelho.getGrauInstrucaoPac());
				elemento.setInfeccaoHospitalar(regEspelho.getInfeccaoHospitalar());
				elemento.setMotivoCobranca(regEspelho.getMotivoCobranca());
				elemento.setNacionalidadePac(regEspelho.getNacionalidadePac());
				elemento.setExclusaoCritica(regEspelho.getExclusaoCritica());
				elemento.setCpfMedicoAuditor(regEspelho.getCpfMedicoAuditor());
				elemento.setCnsMedicoAuditor(regEspelho.getCnsMedicoAuditor()); // Marina 17/01/2012
				elemento.setIndDocPac(regEspelho.getIndDocPac());
				elemento.setPacRG(regEspelho.getPacRG());	// Marina 08/11/2013  Melhoria AGHU 31577
				
				faturamentoFacade.inserirFatEspelhoAih(elemento, true, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}

	}

	/**
	 * Function VERIFICA_ATO_OBRIGATORIO
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param pConta
	 * @param pDescricao
	 * @param pDthrRealizado
	 * @return
	 */
	protected VerificaAtoObrigatorioVO verificaAtoObrigatorio(Short iphPhoSeq, Integer iphSeq, Integer pConta, String pDescricao,
			Integer pDthrRealizado) {

		VerificaAtoObrigatorioVO retorno = new VerificaAtoObrigatorioVO();
		retorno.setRetorno(Boolean.FALSE);
		List<CursorAtoObrigatorioVO> listarAtoObrigatorio = getFatItemContaHospitalarDAO().listarAtoObrigatorio(iphPhoSeq, iphSeq, pConta);
		if (!listarAtoObrigatorio.isEmpty()) {
			logar("Ato obrigatorio {0} iph_seq={1} {2}", listarAtoObrigatorio.get(0).getCodTabelaAto(), listarAtoObrigatorio.get(0)
					.getIphSeqCobrado(), listarAtoObrigatorio.get(0).getDescricaoAto());
			logar("Procedimento do ato {0} iph_seq={1} {2}", listarAtoObrigatorio.get(0).getCodTabela(), listarAtoObrigatorio.get(0)
					.getIphSeq(), listarAtoObrigatorio.get(0).getDescricao());
			retorno.setDthrRealizado(listarAtoObrigatorio.get(0).getDthrRealizado());
			retorno.setRetorno(Boolean.TRUE);
		} else {
			FatItensProcedHospitalar itemProcedHosp = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(
					new FatItensProcedHospitalarId(iphPhoSeq, iphSeq));
			if (itemProcedHosp != null){
				retorno.setDescricao(itemProcedHosp.getDescricao());
			}
		}

		return retorno;
	}

	/**
	 * Function VERIFICA_CBO
	 * 
	 * @param pCbo
	 * @param pComp
	 * @return
	 */
	protected String verificaCbo(final String pCbo, final Integer pComp) {
		final RapDeParaCboDAO rapDeParaCboDAO = getRapDeParaCboDAO();
		String retorno = pCbo;
		if (pComp == null || pComp.intValue() < CONST_201108_PORTARIA_203.intValue()) {
			List<RapDeParaCbo> rapDeParaCbos = rapDeParaCboDAO.obterCodigoCboNovo(pCbo);
			if (rapDeParaCbos != null && !rapDeParaCbos.isEmpty()) {
				retorno = rapDeParaCbos.get(0).getCodigoCboAntigo();
			}
		} else {
			List<RapDeParaCbo> rapDeParaCbos = rapDeParaCboDAO.obterCodigoCboAntigo(pCbo);
			if (rapDeParaCbos != null && !rapDeParaCbos.isEmpty()) {
				retorno = rapDeParaCbos.get(0).getCodigoCboNovo();
			}
		}
		return retorno;
	}

	private class ResultPesquisaIndIec{
		public ResultPesquisaIndIec(Integer vQtd,
				CursorEspelhoItemContaHospVO indIec) {
			super();
			this.vQtd = vQtd;
			this.indIec = indIec;
		}
		
		private Integer vQtd;
		private CursorEspelhoItemContaHospVO indIec;
	}

	public ResultPesquisaIndIec pesquisaIndIec(final Long pCodSus, final Byte pTivSeq, final Byte pTaoSeq, Integer pIphSeq,
			final BigDecimal pVlrServHospitalar, final BigDecimal pVlrSadt, final BigDecimal pVlrProcedimento, final BigDecimal pVlrAnestesista,
			final BigDecimal vVlrServProfissional, final Integer pComp, final Short pQtde, final Integer pCthSeq){

		Integer vQtd = 0;
		CursorEspelhoItemContaHospVO vInd = null;
		
		// pesquisa iec com codigo sus, tiv e ato a partir do ama
		LOOP:
		for (final CursorEspelhoItemContaHospVO indIec : tabIecVO) {
			if (indIec.getEspelhoItemContaHosp().getProcedimentoHospitalarSus().longValue() > pCodSus.longValue()) {
				break LOOP;
				
			} else if (indIec.getEspelhoItemContaHosp().getProcedimentoHospitalarSus().equals(pCodSus) &&
						(indIec.getEspelhoItemContaHosp().getTivSeq() != null && CoreUtil.igual(indIec.getEspelhoItemContaHosp().getTivSeq(), pTivSeq)) &&
							(indIec.getEspelhoItemContaHosp().getTaoSeq() != null && CoreUtil.igual(indIec.getEspelhoItemContaHosp().getTaoSeq(), pTaoSeq))
					){
			
				vInd = indIec; // -- ponteiro do ultimo iec obtido
				
				// Desprezar itens com competencia UTI (diarias de uti já estão com competencia
				if (indIec.getEspelhoItemContaHosp().getCompetenciaUti() != null) {
					break LOOP;
					
				} else {
					if (Short.valueOf("0").equals(indIec.getAtualizado())){
						indIec.setQtdAma(pQtde);
						 
						// acumulada quantidade de iec
						vQtd += indIec.getEspelhoItemContaHosp().getQuantidade();
						
						// se quantidade acumulada iec menor ou igual quantidade do ama
						if (vQtd.intValue() <= pQtde.intValue()) {
							indIec.setAtualizado(Short.valueOf("1"));
							
							this.atualizaIec(pCthSeq, pCodSus, (pTivSeq != null ? pTivSeq.intValue() : null), pTaoSeq, null, 
									pVlrServHospitalar, pVlrSadt, pVlrProcedimento,
									pVlrAnestesista, vVlrServProfissional, pComp, pQtde, indIec);
						} else {
							// quantidade de iec é maior que quantidade do ama, deverá ser desdobrada
							break LOOP;
						}
					}
				}
			}
		}
		
		return new ResultPesquisaIndIec(vQtd, vInd);
	}
	
	/**
	 * 
	 * Procedure VERIFICA_ITEM_ESPELHO
	 * 
	 * @param pCodSus
	 * @param pTivSeq
	 * @param pTaoSeq
	 * @param pIphSeq
	 * @param pVlrServHospitalar
	 * @param pVlrSadt
	 * @param pVlrProcedimento
	 * @param pVlrAnestesista
	 * @param vVlrServProfissional
	 * @param pComp
	 * @param pQtde
	 */
	protected void verificaItemEspelho(final Long pCodSus, final Byte pTivSeq, final Byte pTaoSeq, Integer pIphSeq,
			final BigDecimal pVlrServHospitalar, final BigDecimal pVlrSadt, final BigDecimal pVlrProcedimento, final BigDecimal pVlrAnestesista,
			final BigDecimal vVlrServProfissional, final Integer pComp, final Short pQtde, final Integer pCthSeq, final IFaturamentoFacade faturamentoFacade) {
		
		// pesquisa iec com codigo sus, tiv e ato a partir do ama, posicionando no indice do registro na tabela,
		// atualizando a competencia de cada iec
		// recebe em v_qtde a quantidade acumulada de dos iec encontrados e atualizados
		ResultPesquisaIndIec pesq = pesquisaIndIec( pCodSus, pTivSeq, pTaoSeq, 
												    pIphSeq, pVlrServHospitalar, 
												    pVlrSadt, pVlrProcedimento, 
												    pVlrAnestesista, vVlrServProfissional, 
												    pComp, pQtde, pCthSeq);
		
		Integer vQtde = pesq.vQtd;
		CursorEspelhoItemContaHospVO indIec = pesq.indIec;
		
		// -- localizou iec na tabela
		if (indIec != null) {
			if (indIec.getEspelhoItemContaHosp().getCompetenciaUti() == null) {
				if (vQtde.intValue() < pQtde.intValue()) {
					logar("Item espelho cod_sus={0} {1} {2} qtde encontrada={3}, qtde ama={4}, incluindo diferenca", pCodSus,pTivSeq,pTaoSeq,vQtde,pQtde);
					vQtde = pQtde - vQtde;
					
					this.insereIec( pCthSeq, pCodSus, pTivSeq, pTaoSeq, 
									vQtde.shortValue(), pVlrServHospitalar, pVlrSadt, pVlrProcedimento,
									pVlrAnestesista, vVlrServProfissional, pComp, pQtde, faturamentoFacade);

					
				// -- quantidade de iec encontrada maior que qtd do ama, atualiza iec com qtde do ama
				} else if (vQtde.intValue() > pQtde.intValue() ) {
					logar("Item espelho cod_sus={0} {1} {2} qtde encontrada={3} maior que qtde ama={4}, incluindo qtde do ama", pCodSus,pTivSeq,pTaoSeq,vQtde,pQtde);

					indIec.setAtualizado(Short.valueOf("1"));
					
					// atualiza competencia de iec e quantidade do ama
					this.atualizaIec( pCthSeq, pCodSus, (pTivSeq != null ? pTivSeq.intValue() : null), 
									  pTaoSeq, pQtde, pVlrServHospitalar, pVlrSadt, pVlrProcedimento,
									  pVlrAnestesista, vVlrServProfissional, pComp, pQtde, indIec);
				}
			}
		} else {
			logar("Item espelho cod_sus={0} não encontrado.", pCodSus);
		} 
	}

	/**
	 * Procedure INSERE_IEC
	 * 
	 * @param pCthSeq
	 * @param pCodSus
	 * @param pTivSeq
	 * @param pTaoSeq
	 * @param pQtDein
	 * @param pVlrServHospitalar
	 * @param pVlrSadt
	 * @param pVlrProcedimento
	 * @param pVlrAnestesista
	 * @param vVlrServProfissional
	 * @param pComp
	 * @param pQtde
	 * @param faturamentoFacade
	 */
	protected void insereIec(final Integer pCthSeq, final Long pCodSus, final Byte pTivSeq, final Byte pTaoSeq, final Short pQtDein,
			final BigDecimal pVlrServHospitalar, final BigDecimal pVlrSadt, final BigDecimal pVlrProcedimento, final BigDecimal pVlrAnestesista,
			final BigDecimal vVlrServProfissional, final Integer pComp, final Short pQtde, final IFaturamentoFacade faturamentoFacade) {
		BigDecimal nrQtDein;
		if (pQtDein == null) {
			nrQtDein = BigDecimal.ONE;
		} else {
			nrQtDein = new BigDecimal(pQtDein);
		}
		MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
		BigDecimal bigPQtde = new BigDecimal(pQtde);
		BigDecimal vVlrSh = pVlrServHospitalar.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrSadt = pVlrSadt.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrProc = pVlrProcedimento.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrAnest = pVlrAnestesista.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrSp = vVlrServProfissional.divide(bigPQtde, mathContext).multiply(nrQtDein);
		if (vSeqpIec == null) {
			vSeqpIec = cProxItem(pCthSeq);
		}
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		List<FatEspelhoItemContaHosp> itensContaHosp = fatEspelhoItemContaHospDAO.buscarPorCthSeqSeqpTivTaoCodSus(pCthSeq, pCodSus, pTivSeq, pTaoSeq);
		for (FatEspelhoItemContaHosp fatEspelhoItemContaHosp : itensContaHosp) {
			logar("*** insert into FAT_ESPELHOS_ITENS_CONTA_HOSP ******* dentro da separa itens por comp");
			logar("r.PROCEDIMENTO_HOSPITALAR_SUS:  ", fatEspelhoItemContaHosp.getProcedimentoHospitalarSus());
			logar("r.TAO_SEQ:  ", fatEspelhoItemContaHosp.getTaoSeq());
			logar("  ******************** ");
			vSeqpIec++;
			FatEspelhoItemContaHosp novo = new FatEspelhoItemContaHosp(new FatEspelhoItemContaHospId(fatEspelhoItemContaHosp.getId().getIchCthSeq(),
					vSeqpIec.shortValue()), fatEspelhoItemContaHosp.getIchSeq(), fatEspelhoItemContaHosp.getTivSeq(),
					fatEspelhoItemContaHosp.getTaoSeq(), pQtDein, fatEspelhoItemContaHosp.getNotaFiscal(),
					fatEspelhoItemContaHosp.getPontosAnestesista(), fatEspelhoItemContaHosp.getPontosCirurgiao(),
					fatEspelhoItemContaHosp.getPontosSadt(), fatEspelhoItemContaHosp.getProcedimentoHospitalarSus(),
					fatEspelhoItemContaHosp.getIndConsistente(), fatEspelhoItemContaHosp.getIndModoCobranca(), vVlrAnest, vVlrProc, vVlrSadt, vVlrSh,
					vVlrSp, fatEspelhoItemContaHosp.getDataPrevia(), fatEspelhoItemContaHosp.getIndTipoItem(),
					fatEspelhoItemContaHosp.getIndConsistente(), fatEspelhoItemContaHosp.getIndLocalSoma(), fatEspelhoItemContaHosp.getCgc(),
					pComp == null ? null : pComp.toString(), fatEspelhoItemContaHosp.getIndEquipe(), fatEspelhoItemContaHosp.getCpfCns(),
					fatEspelhoItemContaHosp.getSerieOpm(), fatEspelhoItemContaHosp.getLoteOpm(), fatEspelhoItemContaHosp.getRegAnvisaOpm(),
					fatEspelhoItemContaHosp.getCnpjRegAnvisa(), fatEspelhoItemContaHosp.getCbo());
			faturamentoFacade.inserirFatEspelhoItemContaHospSemFlush(novo);
		}
		fatEspelhoItemContaHospDAO.flush();
	}

	/**
	 * Procedure VERIFICA_FINAL
	 */
	protected void verificaFinal() {
		logar("Procedura Verifica Final");
		Set<Long> chaves = tabAtosMedicosAih.keySet();
		
		for (Long key : chaves) {
			if (tabAtosMedicosAih.get(key).getComp() == 0){
				logar("Código ssm {0} não foi carregado", tabAtosMedicosAih.get(key).getAtoMedicoAih().getIphCodSus());
			}
		}
		logar("Fim do verifica Final");
	}

	/**
	 * Procedure VERIFICA_IEC_FINAL
	 * 
	 * Procedure de verificação final dos itens espelho
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void verificaIecFinal(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		BigDecimal vVlrSh = BigDecimal.ZERO;
		BigDecimal vVlrSp = BigDecimal.ZERO;
		BigDecimal vVlrSadt = BigDecimal.ZERO;
		BigDecimal vVlrAnest = BigDecimal.ZERO;
		BigDecimal vVlrProced = BigDecimal.ZERO;
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Integer vPtosAnest = 0;
		Integer vPtosCirur = 0;
		Integer vPtosSadt = 0;
		// -- totais
		BigDecimal vVlrUtie = BigDecimal.ZERO;
		BigDecimal vVlrRn = BigDecimal.ZERO;
		BigDecimal vVlrHemat = BigDecimal.ZERO;
		BigDecimal vVlrTransp = BigDecimal.ZERO;
		BigDecimal vVlrOpm = BigDecimal.ZERO;
		// -- SH
		BigDecimal vVlrShUtie = BigDecimal.ZERO;
		BigDecimal vVlrShRn = BigDecimal.ZERO;
		BigDecimal vVlrShHemat = BigDecimal.ZERO;
		BigDecimal vVlrShTransp = BigDecimal.ZERO;
		BigDecimal vVlrShOpm = BigDecimal.ZERO;
		// -- SP
		BigDecimal vVlrSpUtie = BigDecimal.ZERO;
		BigDecimal vVlrSpRn = BigDecimal.ZERO;
		BigDecimal vVlrSpHemat = BigDecimal.ZERO;
		BigDecimal vVlrSpTransp = BigDecimal.ZERO;
		BigDecimal vVlrSpOpm = BigDecimal.ZERO;
		// -- SADT
		BigDecimal vVlrSadtUtie = BigDecimal.ZERO;
		BigDecimal vVlrSadtRn = BigDecimal.ZERO;
		BigDecimal vVlrSadtHemat = BigDecimal.ZERO;
		BigDecimal vVlrSadtTransp = BigDecimal.ZERO;
		BigDecimal vVlrSadtOpm = BigDecimal.ZERO;
		// -- PROCED
		BigDecimal vVlrProcUtie = BigDecimal.ZERO;
		BigDecimal vVlrProcRn = BigDecimal.ZERO;
		BigDecimal vVlrProcHemat = BigDecimal.ZERO;
		BigDecimal vVlrProcTransp = BigDecimal.ZERO;
		BigDecimal vVlrProcOpm = BigDecimal.ZERO;
		// -- ANEST
		BigDecimal vVlrAnestUtie = BigDecimal.ZERO;
		BigDecimal vVlrAnestRn = BigDecimal.ZERO;
		BigDecimal vVlrAnestHemat = BigDecimal.ZERO;
		BigDecimal vVlrAnestTransp = BigDecimal.ZERO;
		BigDecimal vVlrAnestOpm = BigDecimal.ZERO;

		Byte vCodAtoUtie = null;
		Byte vCodAtoRn = null;
		Byte vCodAtoConsPed = null;
		Byte vCodAtoHemat = null;
		Byte vCodAtoOpm = null;
		Byte vCodAtoSalaTransp = null;
		Byte vCodAtoSadtTransp = null;
		Byte vCodAtoCirurTransp = null;

		Long vCodSus = 0l;
		Byte vTivSeq = 0;
		Byte vTaoSeq = 0;
		Short vQtdeIec = 0;
//		Short vQtdeAma = 0;
//		Boolean vIndInternacao;

	  	
	  	//-- BUSCA_PARAMETROS_TIPO_ATO --// 

		logar("procedure verifica_iec_final");
	  	logar("PROCEDURE busca_parametros_tipo_ato is");
	  	vCodAtoUtie = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_UTIE);
	  	vCodAtoRn = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_RN);
	  	vCodAtoConsPed = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_CONS_PED);
	  	vCodAtoHemat = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_HEMAT);
	  	vCodAtoOpm = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_OPM);
	  	vCodAtoSalaTransp = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_SALA_TRANSP);
	  	vCodAtoSadtTransp = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_SADT_TRANSP);
	  	vCodAtoCirurTransp = buscarVlrByteAghParametro(AghuParametrosEnum.P_COD_ATO_CIRUR_TRANSP);
	  	//-- FIM BUSCA_PARAMETROS_TIPO_ATO --//
	  	
	  	for (CursorEspelhoItemContaHospVO tabIec : tabIecVO) {
	  		if (!tabIec.getEspelhoItemContaHosp().getProcedimentoHospitalarSus().equals(vCodSus)
			 || !tabIec.getEspelhoItemContaHosp().getTivSeq().equals(vTivSeq)
			 || !tabIec.getEspelhoItemContaHosp().getTaoSeq().equals(vTaoSeq)) {
	  			/*if v_cod_sus <> 0 then
			           if v_qtde_iec != v_qtde_ama then
			           null;
			            --  grava_mensagem(
			            -- 'Código ssm ' || v_cod_sus||' com erros de ajuste, qtde='||
			            --  v_qtde_iec||' qtde_ama='||v_qtde_ama||' ind_internacao='||v_IND_INTERNACAO);
			           end if;
			        end if;           */
	  			vCodSus = tabIec.getEspelhoItemContaHosp().getProcedimentoHospitalarSus();
	  			vTivSeq = tabIec.getEspelhoItemContaHosp().getTivSeq();
	  			vTaoSeq = tabIec.getEspelhoItemContaHosp().getTaoSeq();
	  			vQtdeIec = tabIec.getEspelhoItemContaHosp().getQuantidade();
//	  			vQtdeAma = tabIec.getQtdAma();
//	  			vIndInternacao = tabIec.getIndInternacao();
	  		}
	  		else {
	  			vQtdeIec = (short) (vQtdeIec + tabIec.getEspelhoItemContaHosp().getQuantidade());
	  		}
	  		
	  		logar("tabula_valores; ");
	  		logar("procedure tabula_valores is");
	  		/////////////////////////////-- TABULA VALORES --/////////////////////////////
	  		if (vCodAtoUtie.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq())) { //-- DIARIA DE UTI ESPECIALIZADA
	  			vVlrUtie = vVlrUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO))
	  						.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO))
	  						.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO))
	  						.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO))
	  						.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
	  			
	  			vVlrShUtie = vVlrShUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
	  			vVlrSpUtie = vVlrSpUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
	  			vVlrSadtUtie = vVlrSadtUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
	  			vVlrProcUtie = vVlrProcUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));
	  			vVlrAnestUtie = vVlrAnestUtie.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));
	  		}
	  		else if (vCodAtoRn.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq()) || 
	  				 vCodAtoConsPed.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq())) { //-- ATENDIMENTO AO RN EM SALA DE PARTO, 1a. CONSULTA PEDIATRIA
	  			
	  			vVlrRn = vVlrRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
		
				vVlrShRn = vVlrShRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
				vVlrSpRn = vVlrSpRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
				vVlrSadtRn = vVlrSadtRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
				vVlrProcRn = vVlrProcRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));
				vVlrAnestRn = vVlrAnestRn.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));

	  		}
	  		else if (vCodAtoHemat.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq())) { //-- HEMOTERAPIA
	  			
	  			vVlrHemat = vVlrHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
		
				vVlrShHemat = vVlrShHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
				vVlrSpHemat = vVlrSpHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
				vVlrSadtHemat = vVlrSadtHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
				vVlrProcHemat = vVlrProcHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));
				vVlrAnestHemat = vVlrAnestHemat.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));

	  		}
	  		else if (vCodAtoSalaTransp.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq()) || 
	  				 vCodAtoSadtTransp.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq()) ||
	  				 vCodAtoCirurTransp.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq())) { //-- TAXA DE SALA DE TRANSPLANTE,SADT TRANSPLANTE,CIRURGIAO TRANSPLANTE
	  			
	  			vVlrTransp = vVlrTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
	
				vVlrShTransp = vVlrShTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
				vVlrSpTransp = vVlrSpTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
				vVlrSadtTransp = vVlrSadtTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
				vVlrProcTransp = vVlrProcTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));
				vVlrAnestTransp = vVlrAnestTransp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));

	  		}
	  		else if (vCodAtoOpm.equals(tabIec.getEspelhoItemContaHosp().getTaoSeq())) { //-- OPM
	  			
	  			vVlrOpm = vVlrOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO))
					.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
	
				vVlrShOpm = vVlrShOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
				vVlrSpOpm = vVlrSpOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
				vVlrSadtOpm = vVlrSadtOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
				vVlrProcOpm = vVlrProcOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));
				vVlrAnestOpm = vVlrAnestOpm.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));

	  		}	
	  		else if (DominioModoCobranca.V.equals(tabIec.getEspelhoItemContaHosp().getIndModoCobranca())) { //-- VALOR
	  			
				vVlrSh = vVlrSh.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServHosp(), BigDecimal.ZERO));
				vVlrSp = vVlrSp.add(nvl(tabIec.getEspelhoItemContaHosp().getValorServProf(), BigDecimal.ZERO));
				vVlrSadt = vVlrSadt.add(nvl(tabIec.getEspelhoItemContaHosp().getValorSadt(), BigDecimal.ZERO));
				vVlrAnest = vVlrAnest.add(nvl(tabIec.getEspelhoItemContaHosp().getValorAnestesista(), BigDecimal.ZERO));
				vVlrProced = vVlrProced.add(nvl(tabIec.getEspelhoItemContaHosp().getValorProcedimento(), BigDecimal.ZERO));

	  		}	  		  		
	  		else if (DominioModoCobranca.P.equals(tabIec.getEspelhoItemContaHosp().getIndModoCobranca())) { //-- PONTOS, SOMA PONTOS DO PROCEDIMENTO 
	  			
				vPtosAnest = vPtosAnest + nvl(tabIec.getEspelhoItemContaHosp().getPontosAnestesista(), 0);
				vPtosCirur = vPtosCirur + nvl(tabIec.getEspelhoItemContaHosp().getPontosCirurgiao(), 0);
				vPtosSadt  = vPtosSadt + nvl(tabIec.getEspelhoItemContaHosp().getPontosSadt(), 0);

	  		}	  		  		
	  		///////////////////////////-- FIM TABULA VALORES --///////////////////////////
	  	}
	  	
	  	if (Boolean.TRUE.equals(alterarValorConta)) {
	  		logar("altera_valores_conta; ");
	  		logar("procedure altera_valores is");
	  		/////////////////////////////-- ALTERA_VALORES_CONTA --/////////////////////////////
	  		FatContasHospitalares contaHospitalarOld = getFatContasHospitalaresDAO().obterPorChavePrimaria(pCthSeq);
	  		FatContasHospitalares contaHospitalarNew = getFatContasHospitalaresDAO().obterPorChavePrimaria(pCthSeq);
	  		contaHospitalarNew.setValorSh(vVlrSh);
	  		contaHospitalarNew.setValorSp(vVlrSp);
	  		contaHospitalarNew.setValorSadt(vVlrSadt);
	  		contaHospitalarNew.setValorAnestesista(vVlrAnest);
	  		contaHospitalarNew.setValorProcedimento(vVlrProced);
	  		contaHospitalarNew.setValorUti(vVlrUtie);
	  		contaHospitalarNew.setValorRn(vVlrRn);
	  		contaHospitalarNew.setValorHemat(vVlrHemat);
	  		contaHospitalarNew.setValorTransp(vVlrTransp);
	  		contaHospitalarNew.setValorOpm(vVlrOpm);
	  		contaHospitalarNew.setPontosAnestesista(vPtosAnest);
	  		contaHospitalarNew.setPontosCirurgiao(vPtosCirur);
	  		contaHospitalarNew.setPontosSadt(vPtosSadt);
	  		
	  		getContaHospitalarON().persistirContaHospitalar(contaHospitalarNew, contaHospitalarOld, true, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
	  		/////////////////////////////-- FIM ALTERA_VALORES_CONTA --/////////////////////////////

	  		/////////////////////////////-- ALTERA_VALORES_CONTA --/////////////////////////////
	  		FatValorContaHospitalar valorContaHospitalar = getFatValorContaHospitalarDAO().obterPorChavePrimaria(pCthSeq);
	  		logar("procedure altera_valores_conta is");
	  		valorContaHospitalar.setValorShOpm(nvl(vVlrShOpm, valorContaHospitalar.getValorShOpm()));
	  		valorContaHospitalar.setValorSpOpm(nvl(vVlrSpOpm, valorContaHospitalar.getValorSpOpm()));
	  		valorContaHospitalar.setValorSadtOpm(nvl(vVlrSadt, valorContaHospitalar.getValorSadtOpm()));
	  		valorContaHospitalar.setValorShUtie(nvl(vVlrShUtie, valorContaHospitalar.getValorShUtie()));
	  		valorContaHospitalar.setValorSpUtie(nvl(vVlrSpUtie, valorContaHospitalar.getValorSpUtie()));
	  		valorContaHospitalar.setValorSadtUtie(nvl(vVlrSadtUtie, valorContaHospitalar.getValorSadtUtie()));
	  		valorContaHospitalar.setValorShRn(nvl(vVlrShRn, valorContaHospitalar.getValorShRn()));
	  		valorContaHospitalar.setValorSpRn(nvl(vVlrSpRn, valorContaHospitalar.getValorSpRn()));
	  		valorContaHospitalar.setValorSadtRn(nvl(vVlrSadtRn, valorContaHospitalar.getValorSadtRn()));
	  		valorContaHospitalar.setValorShHemat(nvl(vVlrShHemat, valorContaHospitalar.getValorShHemat()));
	  		valorContaHospitalar.setValorSpHemat(nvl(vVlrSpHemat, valorContaHospitalar.getValorSpHemat()));
	  		valorContaHospitalar.setValorSadtHemat(nvl(vVlrSadtHemat, valorContaHospitalar.getValorSadtHemat()));
	  		valorContaHospitalar.setValorShTransp(nvl(vVlrShTransp, valorContaHospitalar.getValorShTransp()));
	  		valorContaHospitalar.setValorSpTransp(nvl(vVlrSpTransp, valorContaHospitalar.getValorSpTransp()));
	  		valorContaHospitalar.setValorSadtTransp(nvl(vVlrSadtTransp, valorContaHospitalar.getValorSadtTransp()));
	  		valorContaHospitalar.setValorProcOpm(nvl(vVlrProcOpm, valorContaHospitalar.getValorProcOpm()));
	  		
	  		getFaturamentoFacade().atualizarFatValorContaHospitalar(valorContaHospitalar, true);
	  		logar("altera_valores;");
	  		/////////////////////////////-- FIM ALTERA_VALORES_CONTA --/////////////////////////////
	  	}
	}

	/**
	 * Procedure RN_DELETA_ATOS_MEDICOS_AIH
	 * 
	 * @param pCthSeq
	 */
	protected void rnDeletaAtosMedicosAih(Integer pCthSeq) {
		getFatAtoMedicoAihDAO().removerPorCth(pCthSeq);
	}

	/**
	 * Procedure RN_COPY_ATOS_MEDICOS_TEMP_AIH
	 * 
	 * @param pCthSeq
	 * @throws BaseException 
	 */
	protected void rnCopyAtosMedicosTempAih(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		final List<FatAtoMedicoAihTemp> aihTemps = getFatAtoMedicoAihTempDAO().buscarFatAtoMedicoAihTempPorEaiCthSeqSeqArqSus(pCthSeq, CONST_999);
		if (aihTemps != null && !aihTemps.isEmpty()) {
			final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
			faturamentoFacade.clear();
			
			final FatAtoMedicoAihDAO fatAtoMedicoAihDAO = getFatAtoMedicoAihDAO();
			final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
			final FatTipoAtoDAO fatTipoAtoDAO = getFatTipoAtoDAO();
			final FatTiposVinculoDAO fatTiposVinculoDAO = getFatTiposVinculoDAO();
			final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
			
			for (FatAtoMedicoAihTemp fatAtoMedicoAihTemp : aihTemps) {

				FatAtoMedicoAih novo = new FatAtoMedicoAih();
				
				novo.setId(new FatAtoMedicoAihId(fatAtoMedicoAihTemp.getId().getEaiSeq(), fatAtoMedicoAihTemp.getId().getEaiCthSeq(),
						fatAtoMedicoAihTemp.getId().getSeqp()));
				
				novo.setAlteradoEm(fatAtoMedicoAihTemp.getAlteradoEm());
				novo.setAlteradoPor(fatAtoMedicoAihTemp.getAlteradoPor());
				novo.setCbo(fatAtoMedicoAihTemp.getCbo());
				novo.setCgc(fatAtoMedicoAihTemp.getCgc());
				novo.setCnpjRegAnvisa(fatAtoMedicoAihTemp.getCnpjRegAnvisa());
				novo.setCompetenciaUti(fatAtoMedicoAihTemp.getCompetenciaUti());
				novo.setCpfCns(fatAtoMedicoAihTemp.getCpfCns());
				novo.setCriadoEm(fatAtoMedicoAihTemp.getCriadoEm());
				novo.setCriadoPor(fatAtoMedicoAihTemp.getCriadoPor());
				novo.setDataPrevia(fatAtoMedicoAihTemp.getDataPrevia());
				
				FatEspelhoAih fatEspelhoAih = fatEspelhoAihDAO.obterPorChavePrimaria(
						new FatEspelhoAihId(fatAtoMedicoAihTemp.getId().getEaiCthSeq(), fatAtoMedicoAihTemp.getId().getEaiSeq()));
				novo.setFatEspelhoAih(fatEspelhoAih);
				
				if (fatAtoMedicoAihTemp.getTaoSeq() != null) {
					FatTipoAto fatTipoAto = fatTipoAtoDAO.getTipoAtoPeloCodigoSus(fatAtoMedicoAihTemp.getTaoSeq());
					novo.setFatTipoAto(fatTipoAto);
				}
				
				if (fatAtoMedicoAihTemp.getTivSeq() != null) {
					FatTiposVinculo tiposVinculo = fatTiposVinculoDAO.getTiposVinculoPeloCodigoSus(fatAtoMedicoAihTemp.getTivSeq().intValue());
					novo.setFatTiposVinculo(tiposVinculo);
				}
				
				novo.setIndConsistente(fatAtoMedicoAihTemp.getIndConsistente());
				novo.setIndEquipe(fatAtoMedicoAihTemp.getIndEquipe());
				novo.setIndModoCobranca(fatAtoMedicoAihTemp.getIndModoCobranca());
				novo.setIphCodSus(fatAtoMedicoAihTemp.getIphCodSus());
				
				FatItensProcedHospitalar iph = fatItensProcedHospitalarDAO.obterPorChavePrimaria(
						new FatItensProcedHospitalarId(fatAtoMedicoAihTemp.getIphPhoSeq(), fatAtoMedicoAihTemp.getIphSeq()));
				
				novo.setItemProcedimentoHospitalar(iph);
				novo.setLoteOpm(fatAtoMedicoAihTemp.getLoteOpm());
				novo.setNotaFiscal(fatAtoMedicoAihTemp.getNotaFiscal());
				novo.setPontosAnestesista(fatAtoMedicoAihTemp.getPontosAnestesista());
				novo.setPontosCirurgiao(fatAtoMedicoAihTemp.getPontosCirurgiao());
				novo.setPontosSadt(fatAtoMedicoAihTemp.getPontosSadt());
				novo.setQuantidade(fatAtoMedicoAihTemp.getQuantidade());
				novo.setRegAnvisaOpm(fatAtoMedicoAihTemp.getRegAnvisaOpm());
				novo.setSeqArqSus(Short.valueOf("0"));
				
				novo.setSerieOpm(fatAtoMedicoAihTemp.getSerieOpm());
				novo.setValorAnestesista(fatAtoMedicoAihTemp.getValorAnestesista());
				novo.setValorProcedimento(fatAtoMedicoAihTemp.getValorProcedimento());
				novo.setValorSadt(fatAtoMedicoAihTemp.getValorSadt());
				
				novo.setValorServHosp(fatAtoMedicoAihTemp.getValorServHosp());
				novo.setValorServProf(fatAtoMedicoAihTemp.getValorServProf());
				novo.setSeqpVinculado(fatAtoMedicoAihTemp.getSeqpVinculado());
				novo.setVersion(fatAtoMedicoAihTemp.getVersion());

				faturamentoFacade.inserirAtoMedicoAih(novo, true, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.evict(novo);
			}
			fatAtoMedicoAihDAO.flush();
		}
	}

	private Integer cProxItem(final Integer pCthSeq) {
		Short retorno = getFatEspelhoItemContaHospDAO().obterUltimaSeqTabelaEspelho(pCthSeq);
		if (retorno == null) {
			return Integer.valueOf(0);
		}
		return retorno.intValue();
	}

	/**
	 * Procedure ATUALIZA_IEC
	 * 
	 * @param pCthSeq
	 * @param pCodSus
	 * @param pTivSeq
	 * @param pTaoSeq
	 * @param pQtDein
	 * @param pVlrServHospitalar
	 * @param pVlrSadt
	 * @param pVlrProcedimento
	 * @param pVlrAnestesista
	 * @param vVlrServProfissional
	 * @param pComp
	 * @param pQtde
	 * @param indIec
	 */
	protected void atualizaIec(final Integer pCthSeq, final Long pCodSus, final Integer pTivSeq, final Byte pTaoSeq, final Short pQtDein,
			final BigDecimal pVlrServHospitalar, final BigDecimal pVlrSadt, final BigDecimal pVlrProcedimento, final BigDecimal pVlrAnestesista,
			final BigDecimal vVlrServProfissional, final Integer pComp, final Short pQtde, final CursorEspelhoItemContaHospVO indIec) {
		BigDecimal nrQtDein;
		if (pQtDein == null) {
			nrQtDein = BigDecimal.ONE;
		} else {
			nrQtDein = new BigDecimal(pQtDein);
		}
		MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
		BigDecimal bigPQtde = new BigDecimal(pQtde);
		BigDecimal vVlrSh = pVlrServHospitalar.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrSadt = pVlrSadt.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrProc = pVlrProcedimento.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrAnest = pVlrAnestesista.divide(bigPQtde, mathContext).multiply(nrQtDein);
		BigDecimal vVlrSp = vVlrServProfissional.divide(bigPQtde, mathContext).multiply(nrQtDein);
		/*
		 * 
		 * update FAT_ESPELHOS_ITENS_CONTA_HOSP iec set quantidade =
		 * nvl(p_qtdein,quantidade), competencia_uti = p_comp, VALOR_ANESTESISTA
		 * = v_vlr_anest, VALOR_PROCEDIMENTO = v_vlr_proc, VALOR_SADT =
		 * v_vlr_sadt, VALOR_SERV_HOSP = v_vlr_sh, VALOR_SERV_PROF = v_vlr_sp
		 * where ich_cth_seq = p_cth_seq and procedimento_hospitalar_sus =
		 * p_cod_sus and tiv_seq = p_tiv_seq and tao_seq = p_tao_seq and seqp =
		 * tab_iec(ind_iec).seqp;
		 */
		FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		FatEspelhoItemContaHosp fatEspelhoItemContaHosp = fatEspelhoItemContaHospDAO.obterFatEspelhoItemContaHospPorCthSeqSeqpTivTaoCodSus(pCthSeq,
				indIec.getEspelhoItemContaHosp().getId().getSeqp(), pCodSus, (pTivSeq != null ? pTivSeq.byteValue() : null), pTaoSeq);
		if (fatEspelhoItemContaHosp != null) {
			if (pQtDein != null) {
				fatEspelhoItemContaHosp.setQuantidade(pQtDein);
			}
			fatEspelhoItemContaHosp.setCompetenciaUti(pComp == null ? null : pComp.toString());
			fatEspelhoItemContaHosp.setValorAnestesista(vVlrAnest);
			fatEspelhoItemContaHosp.setValorProcedimento(vVlrProc);
			fatEspelhoItemContaHosp.setValorSadt(vVlrSadt);
			fatEspelhoItemContaHosp.setValorServHosp(vVlrSh);
			fatEspelhoItemContaHosp.setValorServProf(vVlrSp);
			
			fatEspelhoItemContaHospDAO.atualizar(fatEspelhoItemContaHosp);
			fatEspelhoItemContaHospDAO.flush();
		}

	}
	
	private List<Long> obterCursorCodigoTabela(final Integer pCthSeq) throws BaseException {

		final String codRegistro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC);
		final Byte taoSeq = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO); 
		final Byte[] codigoSusTaoSeq = { Byte.valueOf("0"), taoSeq };
		
		return getFatEspelhoAihDAO().obterCursorCodigoTabela(pCthSeq, DominioSituacao.A, codRegistro, codigoSusTaoSeq);
	}
	
//	private List<CursorGrupo4VO> obterCursorGrupo4(final Integer pCthSeq) throws BaseException {
//		
//		final String codRegistro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC);
//		final Byte taoSeq = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO); 
//		final Byte[] codigoSusTaoSeq = { Byte.valueOf("0"), taoSeq };
//		final Short iphCodSus = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_AGHU_COD_GRUPO_PROC_CIRURGICOS);
//		final Short quantidade = 1;
//		final Boolean cirurgiaMultipla = Boolean.TRUE;
//		
//		return getFatEspelhoAihDAO().obterCursorGrupo4(pCthSeq, quantidade, cirurgiaMultipla, DominioSituacao.A, iphCodSus, codRegistro, codigoSusTaoSeq);
//		
//	}
	
	/**
	 * Inicio Marina 15/10/2014
	 * ==========================================================================
	 * function rn_diarias_cuidados_inter(p_diarias in c_atos_medicos_aih%rowtype)
	 */
	public Integer rnDiariasCuidadosInter(CursorAtoMedicoAihVO rAtosMedicosAih, String nomeMicrocomputador,	
			Date dataFimVinculoServidor) throws BaseException {
		
		Integer result = Integer.valueOf(0);
		Integer vDiarias = rAtosMedicosAih.getQtdeDiasMes();
		Integer vDiasMes = rAtosMedicosAih.getDiaDaAlta().intValue() - Integer.valueOf(1);

		logar("*******INICIO ROTINA DE DIARIAS DE CUIDADOS INTERMEDIARIO");  
		
		logar("p_diarias.IPH_COD_SUS: {0}", rAtosMedicosAih.getAtoMedicoAih().getIphCodSus()); 


		if (rAtosMedicosAih.getIphSeq().intValue() == 802010237 || rAtosMedicosAih.getIphSeq().intValue() == 802010245) {

			if (rAtosMedicosAih.getDiaDaAlta().intValue() > rAtosMedicosAih.getQtdeDiasMes().intValue()) {
				
				this.callRnInsereItens(rAtosMedicosAih, nomeMicrocomputador, dataFimVinculoServidor);
				result = Integer.valueOf(1);
				
			} else {
				
				while (vDiarias.intValue() != Integer.valueOf(0)) {
				
					result = Integer.valueOf(1);

					if (vDiasMes.intValue() >= rAtosMedicosAih.getQtdeDiasMes()) {
						this.callRnInsereItens(rAtosMedicosAih, nomeMicrocomputador, dataFimVinculoServidor);
						break;
					} else if (vDiasMes.intValue() < rAtosMedicosAih.getQtdeDiasMes()) {
						this.callRnInsereItens(rAtosMedicosAih,	nomeMicrocomputador, dataFimVinculoServidor);
					}
				}
			}
		}
		logar("********FIM ROTINA DE DIARIAS DE CUIDADOS INTERMEDIARIO");  
		return result;
	}

	/**
	 * @ORADB rn_vincula_seq
	 */
	public void rnVinculaSeq(Integer pCthSeq) {
		List<FatAtoMedicoAihTemp> cAto = this.getFatAtoMedicoAihTempDAO().cAto(pCthSeq);

		for (FatAtoMedicoAihTemp rAto : cAto) {
			logar("r_ato.iph_cod_sus:", rAto.getIphCodSus());
			List<FatAtoMedicoAihTemp> atoMed = this.getFatAtoMedicoAihTempDAO().cVin(pCthSeq, rAto.getIphCodSus());
			for(FatAtoMedicoAihTemp rAtoMed : atoMed) {
				if(rAtoMed.getId().getSeqp() != null){
					logar("v_seqp_vin:", rAto.getId().getSeqp());
					this.getFatAtoMedicoAihTempDAO().updateSeqp(pCthSeq, rAto.getIphCodSus(), rAtoMed.getId().getSeqp());
				}
			}
		}
	}

	/**
	 * Método criado para diminuir a quantidade de linhas em cada chamada a
	 * rnInsereItens
	 */
	private void callRnInsereItens(CursorAtoMedicoAihVO vo, String nomeMicro, Date dtFimVinc) throws BaseException {
		this.rnInsereItens(this.vQtd, vo.getComp(), vo, this.vCont,	Short.valueOf(this.vSeqpPrincVinculado), getFaturamentoFacade(), nomeMicro, dtFimVinc);
	}

	private FatkCthnRN getFatkCthnRN(){
		return fatkCthnRN;
	}
	
	protected FatAtoMedicoAihTempDAO getFatAtoMedicoAihTempDAO() {
		return fatAtoMedicoAihTempDAO;
	}

	// esse dao ficará no faturamento
	protected RapDeParaCboDAO getRapDeParaCboDAO() {
		return rapDeParaCboDAO;
	}
	
	protected Date obterDataCompetencia(Integer anoMes) {
		return DateUtil.obterData(Integer.parseInt(anoMes.toString().substring(0, 4)), Integer.parseInt(anoMes.toString().substring(4)) - 1, 01);
	}
	
	protected RelatorioResumoCobrancaAihRN getRelatorioResumoCobrancaAihRN() {
		return relatorioResumoCobrancaAihRN;
	}
}
