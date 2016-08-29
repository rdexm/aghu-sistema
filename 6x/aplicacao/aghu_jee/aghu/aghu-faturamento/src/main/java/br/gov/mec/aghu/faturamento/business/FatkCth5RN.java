package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerAgrupadaNfVO;
import br.gov.mec.aghu.faturamento.vo.RnCthpVerInsCidVO;
import br.gov.mec.aghu.faturamento.vo.RnSutcAtuSaldoVO;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatProcedHospIntCidId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Refere-se a package FATK_CTH5_RN_UN
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatkCth5RN extends AbstractFatDebugLogEnableRN {

	private static final String INT = "INT";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final String MSG_EXCECAO_IGNORADA = "A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ";

	private static final Log LOG = LogFactory.getLog(FatkCth5RN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -2954506763935167266L;

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.FATC_BUSCA_CID_SUMAR
	 * 
	 * </br><b>Ignorado parametro de saída, pois sempre igual ao retorno. Portanto, foi
	 * alterado o retorno para Integer</b>
	 * 
	 * @throws BaseException
	 */
	public Integer fatcBuscaCidSumar(Integer pCthSeq) throws BaseException {
		return getPrescricaoMedicaFacade().buscarCidAlta(pCthSeq);
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHP_VER_INS_CID
	 * 
	 * @throws BaseException
	 */
	public RnCthpVerInsCidVO rnCthpVerInsCid(Integer pCthSeq,Integer pPhiSeqRzdo, final Boolean pContaOK, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RnCthpVerInsCidVO retorno = null;
		
		// busca na prescrição
		if(Boolean.TRUE.equals(pContaOK)){
			logar("entrada cth5 ok ");
			
		} else {
			logar("entrada cth5 não ok ");
		}

		final Short cpgGrcSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		final String codRegistro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC);
		
		final List<Integer> ichRealizados = getVFatAssociacaoProcedimentoDAO().obterMenorIchRealizados( pCthSeq, 
																									    cpgGrcSeq, 
					 																				    Boolean.TRUE, 
					 																				    DominioSituacaoItenConta.A, 
					 																				    codRegistro);
		if(ichRealizados != null && !ichRealizados.isEmpty()){
			for (Integer phi : ichRealizados) {
				logar("valida_por_procedimento , phi:{0}", phi);
				retorno = validaPorProcedimento(pCthSeq, phi, nomeMicrocomputador, dataFimVinculoServidor);
				if(retorno != null && Boolean.TRUE.equals(retorno.getContaOk())){
					break;
				}
			}
		} else {
			logar("valida_por_procedimento da conta, phi:{0}", pPhiSeqRzdo);
			retorno = validaPorProcedimento(pCthSeq, pPhiSeqRzdo, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		if (retorno != null && Boolean.TRUE.equals(retorno.getContaOk())){
			logar("cth5 ok ");
		} else {
			logar("cth5 não ok ");
		}
		
		return retorno;
	}
	
	/**
	 * ORADB: FATK_CTH5_RN_UN procedure valida_por_procedimento
	 * @param pCthSeq
	 * @param pPhiSeq
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private RnCthpVerInsCidVO validaPorProcedimento(Integer pCthSeq, Integer pPhiSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		final IAghuFacade aghuFacade = this.getAghuFacade();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		final FatProcedHospIntCidDAO fatProcedHospIntCidDAO = getFatProcedHospIntCidDAO();
		
		final RnCthpVerInsCidVO retorno = new RnCthpVerInsCidVO();
		
		Integer cidSumar = fatcBuscaCidSumar(pCthSeq);
		Integer vCidSumario = (cidSumar != null) ? cidSumar : 0; 
		
		logar("buscou da prescrição {0}", vCidSumario);
		
		Integer vCidSeq = fatProcedHospIntCidDAO.obterSeqPorFatProcedHospIntCidId(new FatProcedHospIntCidId(pPhiSeq, vCidSumario));
		String vCidIni = null;
		if(vCidSeq != null){
			logar("compat prescrição {0}", vCidSeq);
			vCidIni = aghuFacade.buscaPrimeiroCidContaSituacao(vCidSeq, DominioSituacao.A);
			if(vCidIni != null){
				fatCidContaHospitalarDAO.removerPorCthPrioridade(pCthSeq, DominioPrioridadeCid.P);
				this.flush();
				FatCidContaHospitalarId idCCH = new FatCidContaHospitalarId(pCthSeq, vCidSeq, DominioPrioridadeCid.P);
				FatCidContaHospitalar cidConta = new FatCidContaHospitalar();
				cidConta.setId(idCCH);
				try{
					faturamentoFacade.inserirCidContaHospitalar(cidConta, nomeMicrocomputador, dataFimVinculoServidor);
				} catch (Exception e) {
					logar(MSG_EXCECAO_IGNORADA, e);
				}
				logar("buscou da prescrição e inseriu fat_cids {0}", vCidIni);
				
				retorno.setCidIni(vCidIni.replaceAll("\\.", ""));
				retorno.setErroCid(null);
				retorno.setContaOk(Boolean.TRUE); // ETB 01022008
			} else {
				retorno.setCidIni(" ");
				retorno.setErroCid("CONTA POSSUI CID INVALIDO NO AGH");
				retorno.setContaOk(Boolean.FALSE);
				retorno.setCodigo(39);
				logar("buscou da prescrição e invalido AGH {0}", vCidIni);
			} // c_cid%notfound
		} else { // não compatível com ssm
			logar("não compat prescrição {0}", vCidSeq);

			vCidIni = aghuFacade.buscaPrimeiroCidContaSituacao(vCidSumario, DominioSituacao.A);

			Long vQtdCidsIni = fatCidContaHospitalarDAO.buscaCountQtdCids(pCthSeq, DominioPrioridadeCid.P);
			if(vQtdCidsIni == null){
				vQtdCidsIni = 0l;
			}
			logar("qtd cids {0}", vQtdCidsIni);
			if(vQtdCidsIni != null && vQtdCidsIni.intValue() == 0){
				retorno.setCidIni(" ");
				retorno.setErroCid("CID SUMARIO " + vCidIni + " NÃO COMP E NAO INFORMADO NA CONTA");
				retorno.setContaOk(Boolean.FALSE);
				logar("0 buscou da prescrição e incomp ssm {0}", vCidIni);
			} else if(vQtdCidsIni != null && CoreUtil.maior(vQtdCidsIni, 1)) {
				logar("mais de um cid ");
				retorno.setErroCid("CID SUMARIO " + vCidIni + " INVAL E CTH POSSUI MAIS DE UM CID PRIMARIO.");
				retorno.setContaOk(Boolean.FALSE);
				retorno.setCidIni(" ");
			} else { // busca CID PRIMARIO da conta
				Integer vCidSeqFat = null;
				vCidSeqFat = fatCidContaHospitalarDAO.buscarPrimeiroCidSeq(pCthSeq, DominioPrioridadeCid.P);
				if (vCidSeqFat == null) {
					retorno.setErroCid("CID SUMARIO " + vCidIni + " INVALIDO E CONTA SEM CID VALIDO.");
					retorno.setContaOk(Boolean.FALSE);
					retorno.setCidIni(" ");
					logar("cth possui 1 e não conseguiu recup ");
				} else {
		              // valida o cid contra o ssm realizado
					vCidSeq = fatProcedHospIntCidDAO.obterSeqPorFatProcedHospIntCidId(new FatProcedHospIntCidId(pPhiSeq, vCidSeqFat));

					if(vCidSeq != null){
						// busca o cid na tabela agh_cids
						vCidIni = aghuFacade.buscaPrimeiroCidContaSituacao(vCidSeq, DominioSituacao.A);
						if(vCidIni != null){
							retorno.setCidIni(vCidIni.replaceAll("\\.", ""));
							logar("cth possui 1 e recup {0}", vCidIni);							
							retorno.setErroCid(null);
							retorno.setContaOk(Boolean.TRUE); // ETB 01022008
						} else {
							retorno.setErroCid("CONTA POSSUI CID INVALIDO NO AGH");
							retorno.setContaOk(Boolean.FALSE);
							retorno.setCidIni(" ");
							retorno.setCodigo(39);
							logar("cth possui 1 e invalido {0}", vCidIni);
						} // c_cid%notfound
						logar("fechou");
					} else {// cid realizado incompatível com o sltdo ( da internação ...)
						retorno.setErroCid("CID DA CONTA NÃO COMPATIVEL COM SSM RZDO");
						retorno.setContaOk(Boolean.FALSE);
						retorno.setCidIni(" ");
						retorno.setCodigo(11);
						logar("cid da conta incompat {0}", vCidSeq);
					} // c_cid_ssm a partir do cid da prescrição %notfound
				}
			} // v_qtd_cids
		}
		
		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_DESFAZ_REAPR
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Boolean rnCthcDesfazReapr(Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		// verifica se a conta hospitalar existe e se situacao=Rejeitada
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		
		Boolean result = Boolean.FALSE;
		logar("conta hospitalar existe e se situacao=Rejeitada ");
		FatContasHospitalares regConta = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
		if(regConta == null || !CoreUtil.igual(DominioSituacaoConta.R,regConta.getIndSituacao()) || regConta.getMotivoRejeicao() == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.CONTA_HOSPITALAR_NAO_ENCONTRADA);
		}

		// verifica se existe a conta reapresentada
		// (ié, conta com campo cth_seq_reapresentada = seq  conta hospitalar)
		final List<FatContasHospitalares> listaCthReap = fatContasHospitalaresDAO.listarPorCthReapresentadaSituacao(pCthSeq, new DominioSituacaoConta[]{DominioSituacaoConta.A, DominioSituacaoConta.F});
		if(listaCthReap == null || listaCthReap.isEmpty()){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.CONTA_NAO_REAPRESENTADA);
		}		
		FatContasHospitalares vCthReap = listaCthReap.get(0);
		
		logar("conta reapresentada existe ");
		  // RELACIONAMENTO MÃE/FILHA DE DESDOBRAMENTO
		  //verifica se conta reapresentada é desdobrada (possui filha)
		  //verifica se existe alguma conta onde o campo cth_seq = seq
		FatContasHospitalares vCthFilha = fatContasHospitalaresDAO.primeiraContaPorFilha(vCthReap.getSeq());
		if(vCthFilha != null){ //conta reapresentada possui filha
			logar("conta reapresentada possui filha ");
			FatContasHospitalares contaToUpdate = fatContasHospitalaresDAO.obterPorChavePrimaria(vCthFilha.getSeq());			
			if(contaToUpdate != null && (CoreUtil.igual(DominioSituacaoConta.A,contaToUpdate.getIndSituacao()) ||
											CoreUtil.igual(DominioSituacaoConta.F,contaToUpdate.getIndSituacao()) ||
											CoreUtil.igual(DominioSituacaoConta.E,contaToUpdate.getIndSituacao()) ||
											CoreUtil.igual(DominioSituacaoConta.O,contaToUpdate.getIndSituacao()))){				
				FatContasHospitalares contaOld = null;
				try{
					contaOld = faturamentoFacade.clonarContaHospitalar(contaToUpdate);
				}catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}	
				contaToUpdate.setContaHospitalar(regConta);
				faturamentoFacade.persistirContaHospitalar(contaToUpdate, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
			}			
		}		
		
		logar("conta reapresentada não possui filha ");
		// desfaz a conta internação - coi
		try{
			Integer deletados = getFatContasInternacaoDAO().removerPorCth(vCthReap.getSeq());
			this.flush();
			if(deletados == null || deletados == 0){
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CONTA_INTERNACAO_NAO_ENCONTRADA);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_DESFAZER_INTERNACAO_CONTA_REAPRESENTADA);
		}
		logar("desfez reinternação ");

		// desfaz CID (campo cth_seq = conta_reapresentada)
		try{
			Integer deletados = getFatCidContaHospitalarDAO().removerPorCth(vCthReap.getSeq());
			this.flush();
			if(deletados == null || deletados == 0){
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CIDS_NAO_ENCONTRADAS);
			}		
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_DESFAZER_CID_REAPRESENTACAO);
		}
		
		logar("desfez cid ");
		
		getFatContaSugestaoDesdobrDAO().removerPorCth(pCthSeq);
		this.flush();

		// ETB 30062008  Excluir pendências inativas
		getFatPendenciaContaHospDAO().removerPorCthSituacao(vCthReap.getSeq(), DominioSituacao.I);
		this.flush();
		// Fim ETB 30062008  Excluir pendências inativas
		
		List<FatItemContaHospitalar> listaItens = getFatItemContaHospitalarDAO().listarPorCth(vCthReap.getSeq());
		if(listaItens == null || listaItens.isEmpty()){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ITENS_CONTA_HOSPITALAR_NAO_ENCONTRADA);
		}
		// desfaz os itens_conta_hospitalar da conta_reapresentada
		try{
			for (FatItemContaHospitalar fatItensContaHospitalar : listaItens) {
				getItemContaHospitalarON().removerContaHospitalar(fatItensContaHospitalar, null);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_DESFAZER_ITENS_CONTA_REAPRESENTADA);
		}
		// desfaz a conta reapresentada
		logar("deletou itens ");
		
		// verifica se a conta a ser desfeita é uma conta desdobrada
		if (vCthReap.getAih() != null && regConta.getAih() != null
				&& !CoreUtil.igual(vCthReap.getAih().getNroAih(), regConta.getAih().getNroAih())) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.CONTA_REAPRESENTADA_CONTA_MAE_AIH_DIFERENTES);
		}

		// atualiza o campo ind_situacao da conta original na fat_contas_hospitalares
		FatContasHospitalares regContaOld = null;
		try{
			regContaOld = faturamentoFacade.clonarContaHospitalar(regConta);
		}catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}
		regConta.setIndSituacao(DominioSituacaoConta.O);
		regConta.setMotivoRejeicao(null);
		faturamentoFacade.persistirContaHospitalar(regConta, regContaOld, nomeMicrocomputador, dataFimVinculoServidor);
		
		logar("conta emitida ");
		
		// Atualizar o campo ind_situacao na fat_aihs
		if(regConta.getAih() == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.AIH_NAO_ENCONTRADO);
		}		
		try{
			FatAih oldfatAih = getFaturamentoRN().clonarFatAih(regConta.getAih());
			regConta.getAih().setIndSituacao(DominioSituacaoAih.A);
			regConta.getAih().setContaHospitalar(regConta);		
			faturamentoFacade.atualizarFatAih(regConta.getAih(), oldfatAih);
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.AIH_NAO_ENCONTRADO);
		}

		try{
			for (FatContasHospitalares  contaReapresentada : listaCthReap) {
				FatContasHospitalares contaReapresentadaOld = faturamentoFacade.clonarContaHospitalar(contaReapresentada);
				faturamentoFacade.removerContaHospitalar(contaReapresentada, contaReapresentadaOld, dataFimVinculoServidor);
			}
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_DESFAZER_REAPRESENTACAO);
		}
		
		logar("conta cancelada ");
		
		if(this.fatcValidaProcUti(pCthSeq)){
			
			RnSutcAtuSaldoVO rnSutcAtuSaldoVO = getSaldoUtiAtualizacaoRN().atualizarSaldoDiariasUti(DominioAtualizacaoSaldo.I, 
																									regConta.getDtAltaAdministrativa(), 
																									regConta.getDiasUtiMesInicial(), 
																									regConta.getDiasUtiMesAnterior(), 
																									regConta.getDiasUtiMesAlta(),
																									regConta.getIndIdadeUti(), 
																									nomeMicrocomputador);
			if(rnSutcAtuSaldoVO == null || !rnSutcAtuSaldoVO.isRetorno()){
				logar("problema UTI");
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_ATUALIZAR_SALDO_DIARIAS_UTI);
			}
			
			// Atualiza Banco de Capacidades - Desfaz Reapresentação (+)
			FatEspelhoAih rBuscaConta = getFatEspelhoAihDAO().obterPorChavePrimaria(new FatEspelhoAihId(pCthSeq, 1));
			if(rBuscaConta != null && CoreUtil.igual(DominioModuloCompetencia.INT,rBuscaConta.getDciCpeModulo())){
				getSaldoBancoCapacidadeAtulizacaoRN().atualizarSaldoDiariasBancoCapacidade(
																							rBuscaConta.getDataInternacao(), 
																							rBuscaConta.getDataSaida(), 
																							rBuscaConta.getEspecialidadeAih() != null ? rBuscaConta.getEspecialidadeAih().intValue() : null, 
																							rBuscaConta.getId().getCthSeq(), 
																							DominioAtualizacaoSaldo.I, 
																							true, nomeMicrocomputador, dataFimVinculoServidor);				
			}
			//faturamentoFacade.evict(rBuscaConta);
		}
		result = Boolean.TRUE;
		//super.commitTransaction();
		//super.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
		return result;
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHP_VER_RJC_SIT
	 * 
	 * @throws BaseException
	 * 
	 * Foram retirados argumentos que existiam originalmente na procedure pois não eram utilizados.
	 */
	public void rnCthpVerRjcSit(Short oldRjcSeq
			 ,Short newRjcSeq
			 ,DominioSituacaoConta newIndSituacao) throws BaseException {
		logar("entrou na regra ");
		
		if(CoreUtil.modificados(newRjcSeq, oldRjcSeq)){
			logar("situação 1");
			if(newRjcSeq != null && !CoreUtil.igual(DominioSituacaoConta.R,newIndSituacao)){
				//"Somente Conta Rejeitada possui Motivo de Rejeição"
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CTA_REJEITADA_MOTIVO);
			}else if(newRjcSeq == null && CoreUtil.igual(DominioSituacaoConta.R,newIndSituacao)){
				//"Conta Rejeitada deve possuir Motivo de Rejeição"
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CTA_REJEITADA);
			}
		}else{
			logar("situação 2");
			if(newRjcSeq == null && CoreUtil.igual(DominioSituacaoConta.R,newIndSituacao)){
				//"Conta Rejeitada deve possuir Motivo Rejeição"
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CTA_REJEITADA);
			}else if(newRjcSeq != null && !CoreUtil.igual(DominioSituacaoConta.R,newIndSituacao)){
				//"Somente Conta Rejeitada possui Motivo de Rejeição"
				throw new ApplicationBusinessException(FaturamentoExceptionCode.CTA_REJEITADA_MOTIVO);
			}
		}
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_VER_SISPRENATAL
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerSisprenatal(Date pDataPrevia
			 ,Integer pCthSeq
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
			 ,Long pCodSusRealiz)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		
		// verifica se a conta hospitalar exige que seja preenchido dados parto
		FatItensProcedHospitalar itemProcedHospitalar = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(new FatItensProcedHospitalarId(pPhoRealiz, pIphRealiz)); 
		Boolean vExigeDados = (itemProcedHospitalar != null && itemProcedHospitalar.getDadosParto() != null) ? itemProcedHospitalar.getDadosParto() : Boolean.FALSE;
		if(vExigeDados){ // precisa dos dados parto na conta
			// verifica se a conta hospitalar possui os dados de parto preenchidos
			Boolean vDadosPartoPreenchido = fatContasHospitalaresDAO.isDadosPartoPreenchidos(pCthSeq);
			if(!vDadosPartoPreenchido){ // dados parto nao informados				
	    		try{
		    		FatLogError erro = new FatLogError();
		    		erro.setModulo(INT);
		    		erro.setPrograma("RN_CTHC_VER_SISPRENATAL");
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
	    			erro.setErro("DADOS DE PARTO NÃO CADASTRADOS.");
	    			faturamentoFacade.persistirLogError(erro);
	    			//faturamentoFacade.evict(erro);
	    		} catch (Exception e) {
	    			logar(MSG_EXCECAO_IGNORADA, e);
				}
	    		return Boolean.FALSE;
			} else {
				Boolean vDadosSisprenatalPreenchido = fatContasHospitalaresDAO.isDadosSisprenatalPreenchidos(pCthSeq);
				if(!vDadosSisprenatalPreenchido){ // nro sisprenatal não informado
					try{
			    		FatLogError erro = new FatLogError();
			    		erro.setModulo(INT);
			    		erro.setPrograma("RN_CTHC_VER_SISPRENATAL");
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
		    			erro.setErro("NRO_SISPRENATAL NÃO INFORMADO.");
		    			erro.setFatMensagemLog(new FatMensagemLog(261));
		    			
		    			faturamentoFacade.persistirLogError(erro);
		    			//faturamentoFacade.evict(erro);
		    		} catch (Exception e) {
		    			logar(MSG_EXCECAO_IGNORADA, e);
					}
					return Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			}
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_VER_PEND_ADM
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcVerPendAdm(Integer pCth) throws BaseException {
		final Long numPend = getFatPendenciaContaHospDAO().listarPorCthSituacaoCount(pCth, DominioSituacao.A);
		if(numPend != null && CoreUtil.maior(numPend, 0)) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.FATC_VALIDA_PROC_UTI
	 * 
	 * @param pCthSeq
	 * @return <code>true</code> onde era <code>'S'</code> e <code>false</code> onde era <code>'N'</code>
	 * @throws BaseException
	 */
	public Boolean fatcValidaProcUti(Integer pCthSeq) throws BaseException {
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		
		final Integer vCaracNum = this.getTipoCaracteristicaItemRN().obterTipoCaractItemSeq(DominioFatTipoCaractItem.DIARIA_UTI);
		
		final List<FatCaractItemProcHosp> listaCaractItemProcHosp = getFatCaractItemProcHospDAO().listarPorFatTipoCaractItens(vCaracNum);
		
		if(listaCaractItemProcHosp != null && !listaCaractItemProcHosp.isEmpty()){
			for (FatCaractItemProcHosp fatCaractItemProcHosp : listaCaractItemProcHosp) {
				Short iphPhoSeq = fatCaractItemProcHosp.getItemProcedimentoHospitalar().getId().getPhoSeq(); 
				Integer iphSeq = fatCaractItemProcHosp.getItemProcedimentoHospitalar().getId().getSeq();				
				Long listaEspelhosCount = fatEspelhoItemContaHospDAO.listarEspelhosPorItemProcedHospCount(iphPhoSeq, iphSeq, pCthSeq);
				if(listaEspelhosCount != null && CoreUtil.maior(listaEspelhosCount, 0)) {
					//proced pago pelo sus
					return Boolean.TRUE;
				}				
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_ATU_LANCACOMP
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Boolean rnCthcAtuLancacomp(Date pDataPrevia
			 ,Boolean pPrevia
			 ,Integer pCth
			 ,Integer pPacCodigo
			 ,Integer pPacProntuario
			 ,Integer pPhiRealiz
			 ,Short pPhoRealiz
			 ,Integer pIphRealiz
			 ,Long pCodSusRealiz
			 ,Date pPacDtNasc
			 ,Date pDtAltaAdm
			 ,Short pDias)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		Boolean retorno = Boolean.TRUE;
		
		// verifica limite de idade paciente pediatrico p/ diaria acompanhante
		final int vIdadeLimite = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_IDADE_PEDIATR_ACOMP);
		// verifica limite de idade paciente adulto p/ diaria acompanhante
		final int vIdadeLimiteA = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_IDADE_ADULTO_ACOMP);
		// verifica idade do paciente
		Integer vIdadePac = getItemContaHospitalarRN().fatcBuscaIdadePac(pCth);
		logar("Diarias de Acompanhante {0}", pDias);
		
		logar("DtNasc {0} IdPac {1} IdLim {2} acompanhante ", pPacDtNasc, vIdadePac, vIdadeLimite);

		Long paramCodAcomp = null;		
		if(CoreUtil.menorOuIgual(vIdadePac, vIdadeLimite)){ // pediatrico
			paramCodAcomp = this.buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_PEDIATRIA);
		} else if(CoreUtil.menorOuIgual(vIdadePac, vIdadeLimiteA)) { // paciente adulto
			paramCodAcomp = this.buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_ADULTO);
		} else { // paciente idoso
			paramCodAcomp = this.buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_ACOMP_IDOSO);
		}
		
		long vCodAcomp = paramCodAcomp != null ? paramCodAcomp.longValue() : null;
		logar("Cód acompanhante {0}",  vCodAcomp);
		
		FatItensProcedHospitalar fatItensProcedHospitalarAcomp = getFatItensProcedHospitalarDAO().buscarPrimeiraPorAcompanhanteAtivo(vCodAcomp);
		
		int vPtAnestUTI = 0;
		int vPtCirUTI = 0;
		int vPtSadtUTI = 0;
		if(fatItensProcedHospitalarAcomp != null){
			vCodAcomp = fatItensProcedHospitalarAcomp.getCodTabela() != null ? fatItensProcedHospitalarAcomp.getCodTabela().longValue() : 0;
			vPtAnestUTI = fatItensProcedHospitalarAcomp.getPontoAnestesista() != null ? fatItensProcedHospitalarAcomp.getPontoAnestesista().intValue() : 0;
			vPtCirUTI = fatItensProcedHospitalarAcomp.getPontosCirurgiao() != null ? fatItensProcedHospitalarAcomp.getPontosCirurgiao().intValue() : 0;
			vPtSadtUTI = fatItensProcedHospitalarAcomp.getPontosSadt() != null ? fatItensProcedHospitalarAcomp.getPontosSadt().intValue() : 0;
		    // busca valores pro item			
			BigDecimal vVlrServHospUTI = BigDecimal.ZERO;
			BigDecimal vVlrSadtUTI     = BigDecimal.ZERO;
			BigDecimal vVlrProcedUTI   = BigDecimal.ZERO;
			BigDecimal vVlrAnestUTI    = BigDecimal.ZERO;
			BigDecimal vVlrServProfUTI = BigDecimal.ZERO;
			FatVlrItemProcedHospComps fatVlrItemProcedHospComps = getVerificacaoItemProcedimentoHospitalarRN().obterValoresItemProcHospPorModuloCompetencia(fatItensProcedHospitalarAcomp.getId().getPhoSeq(), fatItensProcedHospitalarAcomp.getId().getSeq(), DominioModuloCompetencia.INT, null);
			if(fatVlrItemProcedHospComps != null){
				vVlrServHospUTI = fatVlrItemProcedHospComps.getVlrServHospitalar() != null ? fatVlrItemProcedHospComps.getVlrServHospitalar() : BigDecimal.ZERO;
				vVlrSadtUTI     = fatVlrItemProcedHospComps.getVlrSadt() != null ? fatVlrItemProcedHospComps.getVlrSadt() : BigDecimal.ZERO;
				vVlrProcedUTI   = fatVlrItemProcedHospComps.getVlrProcedimento() != null ? fatVlrItemProcedHospComps.getVlrProcedimento() : BigDecimal.ZERO;
				vVlrAnestUTI    = fatVlrItemProcedHospComps.getVlrAnestesista() != null ? fatVlrItemProcedHospComps.getVlrAnestesista() : BigDecimal.ZERO;
				vVlrServProfUTI = fatVlrItemProcedHospComps.getVlrServProfissional() != null ? fatVlrItemProcedHospComps.getVlrServProfissional() : BigDecimal.ZERO;
			}

			// Busca proxima seq da tabela de espelho
			Short vEicSeqp = getFatEspelhoItemContaHospDAO().buscaProximaSeqTabelaEspelho(pCth);
		    // lanca diária de acompanhante		
			
			logar("******** fatk_cth5_rn_un ************");
			logar("v_cod_acomp:  {0}",vCodAcomp);
			logar("  ******************** ");
			
		    try{
		    	FatEspelhoItemContaHosp espelho = new FatEspelhoItemContaHosp(new FatEspelhoItemContaHospId(pCth, vEicSeqp));
		    	espelho.setItemProcedimentoHospitalar(fatItensProcedHospitalarAcomp);
		    	
		    	Byte taoSeq = (fatItensProcedHospitalarAcomp.getTipoAto() != null ? fatItensProcedHospitalarAcomp.getTipoAto().getCodigoSus() : null); 
		    	espelho.setTaoSeq(taoSeq);
		    	
		    	Integer tivSeq = (fatItensProcedHospitalarAcomp.getTiposVinculo() != null ? fatItensProcedHospitalarAcomp.getTiposVinculo().getCodigoSus() : null); 
		    	espelho.setTivSeq(tivSeq != null ? tivSeq.byteValue() : null);
		    	
		    	espelho.setQuantidade(pDias);
		    	espelho.setProcedimentoHospitalarSus(vCodAcomp);
		    	espelho.setPontosAnestesista(vPtAnestUTI * pDias);
		    	espelho.setPontosCirurgiao(vPtCirUTI * pDias);
		    	espelho.setPontosSadt(vPtSadtUTI * pDias);
		    	espelho.setIndConsistente(Boolean.TRUE);
		    	espelho.setIndModoCobranca(DominioModoCobranca.V);
		    	espelho.setValorAnestesista(vVlrAnestUTI != null ? vVlrAnestUTI.multiply(BigDecimal.valueOf(pDias)) : BigDecimal.ZERO);
		    	espelho.setValorProcedimento(vVlrProcedUTI != null ? vVlrProcedUTI.multiply(BigDecimal.valueOf(pDias)) : BigDecimal.ZERO);
		    	espelho.setValorSadt(vVlrSadtUTI != null ? vVlrSadtUTI.multiply(BigDecimal.valueOf(pDias)) : BigDecimal.ZERO);
		    	espelho.setValorServHosp(vVlrServHospUTI != null ? vVlrServHospUTI.multiply(BigDecimal.valueOf(pDias)) : BigDecimal.ZERO);
		    	espelho.setValorServProf(vVlrServProfUTI != null ? vVlrServProfUTI.multiply(BigDecimal.valueOf(pDias)) : BigDecimal.ZERO);
		    	espelho.setDataPrevia(pDataPrevia);
		    	espelho.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.D);
		    	espelho.setIndGeradoEncerramento(Boolean.TRUE);
		    	
		    	espelho.setNotaFiscal(null);
		    	espelho.setCompetenciaUti(null);
		    	
		    	getFaturamentoRN().inserirFatEspelhoItemContaHosp(espelho, true);
		    	//faturamentoFacade.evict(espelho);
		    	
		    } catch (Exception e) {		    	
		    	logar("ERRO INSERT uti espec EM fat_espelhos_itens_conta_hosp");
		    	String vMsg = e.getMessage();
		    	try{
		    		
		    		FatLogError erro = new FatLogError();
		    	  
		    		erro.setModulo(INT);
		    		erro.setPrograma("RN_CTHC_ATU_LANCACOMP");
		    		erro.setCriadoEm(new Date());
		    		erro.setCriadoPor(servidorLogado.getUsuario());
	    			erro.setDataPrevia(pDataPrevia);
	    			erro.setCthSeq(pCth);
	    			erro.setPacCodigo(pPacCodigo);
	    			erro.setProntuario(pPacProntuario);
	    			erro.setPhiSeqRealizado(pPhiRealiz);
	    			erro.setIphPhoSeqRealizado(pPhoRealiz);
	    			erro.setIphSeqRealizado(pIphRealiz);
	    			erro.setCodItemSusRealizado(pCodSusRealiz);
	    			erro.setIphPhoSeqItem1(fatItensProcedHospitalarAcomp.getId().getPhoSeq());
	    			erro.setIphSeqItem1(fatItensProcedHospitalarAcomp.getId().getSeq());
	    			erro.setCodItemSus1(vCodAcomp);
	    			erro.setErro("ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR PARA ACOMPANHAMENTO: " + vMsg);		    	  
	    			faturamentoFacade.persistirLogError(erro);
	    			//faturamentoFacade.evict(erro);
		    	} catch (Exception e2) {
		    		logar(MSG_EXCECAO_IGNORADA, e2);
		    	}
		    	retorno = Boolean.FALSE;
		    }
		} else {// if c_cod_uti%found
			try{
		    	FatLogError erro = new FatLogError();
		    	  
		    	erro.setModulo(INT);
		    	erro.setPrograma("RN_CTHC_ATU_LANCACOMP");
		    	erro.setCriadoEm(new Date());
		    	erro.setCriadoPor(servidorLogado.getUsuario());
		    	erro.setDataPrevia(pDataPrevia);
		    	erro.setCthSeq(pCth);
		    	erro.setPacCodigo(pPacCodigo);
		    	erro.setProntuario(pPacProntuario);
		    	erro.setPhiSeqRealizado(pPhiRealiz);
		    	erro.setIphPhoSeqRealizado(pPhoRealiz);
		    	erro.setIphSeqRealizado(pIphRealiz);
		    	erro.setCodItemSusRealizado(pCodSusRealiz);
		    	erro.setErro("NAO ENCONTROU CODIGO DO PROCEDIMENTO DE DIARIAS DE ACOMPANHAMENTO.");
		    	faturamentoFacade.persistirLogError(erro);
		    	//faturamentoFacade.evict(erro);
		    } catch (Exception e) {				
		    	logar(MSG_EXCECAO_IGNORADA, e);
		    }
		    retorno = Boolean.FALSE;
		} // c_cod_acomp%found

		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_VER_AGRUPADA_NF
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public RnCthcVerAgrupadaNfVO rnCthcVerAgrupadaNf(Integer pCthSeq
			 ,Integer pPhi
			 ,Integer pIpsRmpSeq
			 ,Integer pIpsNumero)
			throws BaseException {
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		
		final RnCthcVerAgrupadaNfVO retorno = new RnCthcVerAgrupadaNfVO();

		final List<FatProcedHospInternos> listaFatProcedHospInternos = getFatProcedHospInternosDAO().listarPorPhi(pPhi); 
		logar("Executou agrupada {0} phi {1}", pCthSeq, pPhi);
		   	
		if(listaFatProcedHospInternos == null || listaFatProcedHospInternos.isEmpty()){
			retorno.setRetorno(Boolean.FALSE);
			return retorno;
		}
			
		for (FatProcedHospInternos fatProcedHospInternos : listaFatProcedHospInternos) {
			Integer phi = fatProcedHospInternos.getProcedimentoHospitalarInterno() != null ? fatProcedHospInternos.getProcedimentoHospitalarInterno().getSeq() : null;

			FatItemContaHospitalar itemContaHospitalar = fatItemContaHospitalarDAO.buscarPrimeiroPorCthPhiSituacao(pCthSeq, phi, DominioSituacaoItenConta.A);
			if(itemContaHospitalar != null){
				retorno.setIpsRmpSeq(itemContaHospitalar.getItemRmps() != null ? itemContaHospitalar.getItemRmps().getId().getRmpSeq() : null);
				retorno.setIpsNumero(itemContaHospitalar.getItemRmps() != null ? itemContaHospitalar.getItemRmps().getId().getNumero() : null);
				retorno.setRetorno(Boolean.TRUE);
				return retorno;
			}
		}
		retorno.setRetorno(Boolean.FALSE);
		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH5_RN_UN.RN_CTHC_OBRIGA_CBO
	 * 
	 * @throws BaseException
	 */
	public Boolean rnCthcObrigaCbo(Short pPhoSeq ,Integer pIphSeq) throws BaseException {
		
		final String valorParametroServEspelho = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SERVICOS_PROFISSIONAIS_ESPELHO); // 03,04,05
		final String[] codigosRegistro = valorParametroServEspelho != null ? valorParametroServEspelho.split(",") : null;
		final FatProcedimentoRegistro fatProcedimentoRegistro = getFatProcedimentoRegistroDAO().buscarPrimeiroPorCodigosRegistroEPorIph(codigosRegistro, pPhoSeq, pIphSeq);
		if(fatProcedimentoRegistro == null){
			return Boolean.FALSE;
		}
		final String codTabela = fatProcedimentoRegistro.getItemProcedHospitalar().getCodTabela() != null ? fatProcedimentoRegistro.getItemProcedHospitalar().getCodTabela().toString() : null;
		
		if(CoreUtil.igual(fatProcedimentoRegistro.getFatRegistro().getCodigo(),codigosRegistro[0])){ //03
			
			return Boolean.TRUE;
			
		} else if(CoreUtil.igual(fatProcedimentoRegistro.getFatRegistro().getCodigo(),codigosRegistro[1])){ //04
			
			if(codTabela != null && (codTabela.charAt(0) == '3' || codTabela.charAt(0) == '4' || codTabela.charAt(0) == '5')){
				
				return Boolean.TRUE;
				
			} else if(codTabela != null && (codTabela.charAt(0) == '2')){
				
				FatVlrItemProcedHospComps fatVlrItemProcedHospComps = getVerificacaoItemProcedimentoHospitalarRN().obterValoresItemProcHospPorModuloCompetencia(pPhoSeq, pIphSeq, DominioModuloCompetencia.INT, null);
				
				if(fatVlrItemProcedHospComps != null && fatVlrItemProcedHospComps.getVlrServProfissional() != null && fatVlrItemProcedHospComps.getVlrServProfissional().doubleValue() > 0){
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;	
				}
				
			} else if(codTabela != null && CoreUtil.igual("802020011",codTabela)){
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
			
		} else { // cod registro 5 -- AIH secundário
			
			if(CoreUtil.maior(fatProcedimentoRegistro.getItemProcedHospitalar().getPontosSadt(), 0) || CoreUtil.igual("301010048",codTabela)){
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
