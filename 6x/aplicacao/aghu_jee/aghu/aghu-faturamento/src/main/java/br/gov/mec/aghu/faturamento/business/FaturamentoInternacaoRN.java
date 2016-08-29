package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
//import javax.transaction.UserTransaction;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatPerdaItemContaDAO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatPerdaItensContaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FaturamentoInternacaoRN extends BaseBusiness {

	private static final long serialVersionUID = 5368121004924349293L;

	private static final Log LOG = LogFactory
			.getLog(FaturamentoInternacaoRN.class);

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade; 
	
	@EJB
	private FaturamentoON faturamentoON;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;

	@Inject
	private FatPerdaItemContaDAO fatPerdaItemContaDAO;
	
	/**
	 * ORADB: Function FATC_BUSCA_SSM
	 * 
	 * @param pCthSeq
	 * @param pCspCnvCodigo
	 * @param pCspSeq
	 * @param situacaoSSM
	 * @return
	 */
	public String buscaSSM(final Integer pCthSeq, final Short pCspCnvCodigo,
			final Byte pCspSeq, final DominioSituacaoSSM situacaoSSM) {

		try {
			final FatContasHospitalares fatContaHospitalar = fatContasHospitalaresDAO
					.obterPorChavePrimaria(pCthSeq);

			if (fatContaHospitalar != null) {
				final DominioSituacaoConta situacao = fatContaHospitalar
						.getIndSituacao();

				if (CoreUtil.igual(DominioSituacaoConta.E, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.O, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.R, situacao)) {
					if (CoreUtil.igual(DominioSituacaoSSM.S, situacaoSSM)) {
						// Busca ssm na fat_espelhos_aih quando for uma conta
						// encerrada ou cobrada/emitida ou rejeitada(situacao =
						// 'E' ou 'O' ou 'R')
						final String ssm = fatEspelhoAihDAO
								.buscaProcedimentoSolicitado(pCthSeq,
										pCspCnvCodigo, pCspSeq);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found - proced solic CTH" + pCthSeq);
							return null;
						}
					} else if (CoreUtil
							.igual(DominioSituacaoSSM.R, situacaoSSM)) {
						final String ssm = fatEspelhoAihDAO
								.buscaProcedimentoRealizado(pCthSeq,
										pCspCnvCodigo, pCspSeq);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found - proced realiz CTH" + pCthSeq);
							return null;
						}
					}
				}

				if (CoreUtil.igual(DominioSituacaoConta.A, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.F, situacao)) {
					Short tipoGrupoContaSUS = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

					if (CoreUtil.igual(DominioSituacaoSSM.S, situacaoSSM)) {
						// Busca ssm na fat_contas_hospitalares quando for uma
						// conta fechada (situacao = 'F')
						final String ssm = fatContasHospitalaresDAO
								.buscaProcedimentoSolicitado(pCthSeq,
										pCspCnvCodigo, pCspSeq,
										tipoGrupoContaSUS);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found2 - proced solic EAI" + pCthSeq);
							return null;
						}
					} else if (CoreUtil
							.igual(DominioSituacaoSSM.R, situacaoSSM)) {
						final String ssm = fatContasHospitalaresDAO
								.buscaProcedimentoRealizado(pCthSeq,
										pCspCnvCodigo, pCspSeq,
										tipoGrupoContaSUS);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found2 -proced realiz EAI" + pCthSeq);
							return null;
						}
					}
				}

				if (!CoreUtil.igual(DominioSituacaoConta.F, situacao)
						&& !CoreUtil.igual(DominioSituacaoConta.O, situacao)
						&& !CoreUtil.igual(DominioSituacaoConta.E, situacao)
						&& !CoreUtil.igual(DominioSituacaoConta.R, situacao)
						&& !CoreUtil.igual(DominioSituacaoConta.A, situacao)) {
					LOG.debug("situacao diferente de F, E, R, A ou O." + pCthSeq);
					return null;
				}
			}
			LOG.debug("not found - conta: " + pCthSeq);
		} catch (final Exception e) {
			LOG.debug("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: "
					+ e.getMessage());
		}

		return null;
	}
	
	/**
	 * ORADB: Function FATC_BUSCA_SSM_COMPL
	 * 
	 * @param pCthSeq
	 * @param pCspCnvCodigo
	 * @param pCspSeq
	 * @param pCthPhiSeq
	 * @param pCthPhiSeqRealizado
	 * @param situacaoSSM
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public String buscaSsmComplexidade(final Integer pCthSeq,
			final Short pCspCnvCodigo, final Byte pCspSeq,
			final Integer pCthPhiSeq, final Integer pCthPhiSeqRealizado,
			final DominioSituacaoSSM situacaoSSM) {

		try {
			final FatContasHospitalares fatContasHospitalares = fatContasHospitalaresDAO
					.obterPorChavePrimaria(pCthSeq);

			if (fatContasHospitalares != null) {
				final DominioSituacaoConta situacao = fatContasHospitalares
						.getIndSituacao();

				if (CoreUtil.igual(DominioSituacaoConta.E, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.O, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.R, situacao)) {
					if (CoreUtil.igual(DominioSituacaoSSM.S, situacaoSSM)) {
						// Busca ssm na fat_espelhos_aih quando for uma conta
						// encerrada ou cobrada/emitida ou rejeitada(situacao =
						// 'E' ou 'O' ou 'R')
						final String ssm = fatCaractFinanciamentoDAO
								.buscaProcedimentoSolicitadoComplexidade(
										pCthSeq, pCspCnvCodigo, pCspSeq,
										pCthPhiSeq);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found - proced solic CTH" + pCthSeq);
							return null;
						}
					} else if (CoreUtil.igual(DominioSituacaoSSM.R, situacaoSSM)) {
						final String ssm = fatCaractFinanciamentoDAO
								.buscaProcedimentoRealizadoComplexidade(
										pCthSeq, pCspCnvCodigo, pCspSeq,
										pCthPhiSeqRealizado);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug(
									"not found - proced realiz CTH" + pCthSeq);
							return null;
						}
					}
				} else if (CoreUtil.igual(DominioSituacaoConta.A, situacao)
						|| CoreUtil.igual(DominioSituacaoConta.F, situacao)) {
					Short tipoGrupoContaSUS = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

					if (CoreUtil.igual(DominioSituacaoSSM.S, situacaoSSM)) {
						final String ssm = fatCaractFinanciamentoDAO
								.buscaProcedimentoSolicitadoAbertaFechadaComplexidade(
										pCthSeq, pCspCnvCodigo, pCspSeq,
										pCthPhiSeq, tipoGrupoContaSUS);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found2 - proced solic EAI" + pCthSeq);
							return null;
						}
					} else if (CoreUtil
							.igual(DominioSituacaoSSM.R, situacaoSSM)) {
						final String ssm = fatCaractFinanciamentoDAO
								.buscaProcedimentoRealizadoAbertaFechadaComplexidade(
										pCthSeq, pCspCnvCodigo, pCspSeq,
										pCthPhiSeqRealizado, tipoGrupoContaSUS);
						if (ssm != null) {
							return ssm;
						} else {
							LOG.debug("not found2 -proced realiz EAI" + pCthSeq);
							return null;
						}
					} else if (!CoreUtil
							.igual(DominioSituacaoConta.F, situacao)
							&& !CoreUtil.igual(DominioSituacaoConta.O, situacao)
							&& !CoreUtil.igual(DominioSituacaoConta.E, situacao)
							&& !CoreUtil.igual(DominioSituacaoConta.R, situacao)
							&& !CoreUtil.igual(DominioSituacaoConta.A, situacao)) {
						LOG.debug("situacao diferente de F, E, R, A ou O." + pCthSeq);
						return null;
					}
				}
			}
		} catch (final Exception e) {
			LOG.debug("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Busca situação do gestor
	 * 
	 * ORADB: Function FATC_SITUACAO_SMS
	 * 
	 * @return
	 */
	public String situacaoSMS(final FatContasHospitalares contaHospitalar) {

		String indEnviadoSms = null;
		String indAutorizadoSms = null;

		if (contaHospitalar == null) {
			return null;
		}
		indEnviadoSms = contaHospitalar.getIndEnviadoSms();
		indAutorizadoSms = contaHospitalar.getIndAutorizadoSms();
		return situacaoSMS(indEnviadoSms, indAutorizadoSms);
	}
	
	public String situacaoSMS(String indEnviadoSms, String indAutorizadoSms) {

		if ((indEnviadoSms == null || CoreUtil.igual("N", indEnviadoSms))
				&& (indAutorizadoSms == null || CoreUtil.igual("N",
						indAutorizadoSms))) {
			return null;
		} else if (CoreUtil.igual("S", indEnviadoSms)
				&& (indAutorizadoSms == null || CoreUtil.igual("N",
						indAutorizadoSms))) {
			return ("Em Avaliação");
		} else if (CoreUtil.igual("S", indEnviadoSms)
				&& CoreUtil.igual("S", indAutorizadoSms)) {
			return ("Autorizada");
		}

		return "";
	}
	
	/**
	 * ORADB Procedure FATK_CTH2_RN_UN.RN_CTHC_ATU_INS_PIT
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public Boolean rnCthcAtuInsPit(Integer pCthSeq, Short pPhoSeq, Integer pIphSeq, Long pCodTabela, Long pQtdPerd, Long pQtdRealiz,
			BigDecimal pVlrSh, BigDecimal pVlrSp, BigDecimal pVlrSadt, BigDecimal pVlrProced, BigDecimal pVlrAnest, Long pPtAnest,
			Long pPtCirur, Long pPtSadt) throws BaseException {
		
		boolean result = Boolean.TRUE;
		final Short vPitSeqp = fatPerdaItemContaDAO.obterProximoSeqp(pCthSeq);
		
		try {
			FatPerdaItemConta fatPerdaItemConta = new FatPerdaItemConta();
			
			fatPerdaItemConta.setId(new FatPerdaItensContaId(vPitSeqp, pCthSeq));
			if(pIphSeq != null && pPhoSeq != null) {
				fatPerdaItemConta.setItemProcedimentoHospitalar(faturamentoFacade.obterItemProcedHospitalar(pPhoSeq, pIphSeq));
			}
			fatPerdaItemConta.setIphCodTabela(pCodTabela);
			fatPerdaItemConta.setQuantidadePerdida(pQtdPerd == null? null : pQtdPerd.shortValue());
			fatPerdaItemConta.setQuantidadeRealizada(pQtdRealiz == null ? null : pQtdRealiz.shortValue());
			fatPerdaItemConta.setValorSh(pVlrSh);
			fatPerdaItemConta.setValorSp(pVlrSp);
			fatPerdaItemConta.setValorSadt(pVlrSadt);
			fatPerdaItemConta.setValorProced(pVlrProced);
			fatPerdaItemConta.setValorAnest(pVlrAnest);
			fatPerdaItemConta.setPontosAnest(pPtAnest == null? null : pPtAnest.intValue());
			fatPerdaItemConta.setPontosCirur(pPtCirur == null? null : pPtCirur.intValue());
			fatPerdaItemConta.setPontosSadt(pPtSadt == null ? null : pPtSadt.intValue());
								
			inserirFatPerdaItemConta(fatPerdaItemConta, true);
			//faturamentoFacade.evict(fatPerdaItemConta);

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			
			FatMensagemLog fatMensagemLog= new FatMensagemLog();
			fatMensagemLog.setCodigo(74);
			
			faturamentoON.criarFatLogErrors(
					"ERRO AO INSERIR PERDA ITEM: " + e.getMessage(),
					DominioModuloCompetencia.INT.toString(), pCthSeq, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, "RN_CTHC_ATU_INS_PIT", null, null, fatMensagemLog);

			result = Boolean.FALSE;
		}

		return result;
	}
	
	/**
	 * Insere um PerdaItemConta executando suas respectivas triggers
	 * 
	 * @param fatPerdaItemConta
	 * @throws ApplicationBusinessException
	 */
	public void inserirFatPerdaItemConta(final FatPerdaItemConta fatPerdaItemConta,
			boolean flush) throws ApplicationBusinessException {
		fattPerdaItemContaBri(fatPerdaItemConta);
		fatPerdaItemContaDAO.persistir(fatPerdaItemConta);
		if (flush){
			fatPerdaItemContaDAO.flush();
		}
	}
	
	/**
	 * Método para implementar regras da trigger executada antes da inserção de
	 * <code>FatPerdaItemConta</code>.
	 * 
	 * ORADB Trigger FATT_PIT_BRI
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattPerdaItemContaBri(final FatPerdaItemConta fatPerdaItemConta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		fatPerdaItemConta.setCriadoEm(new Date());
		fatPerdaItemConta.setAlteradoEm(new Date());
		fatPerdaItemConta.setCriadoPor(servidorLogado.getUsuario());
		fatPerdaItemConta.setAlteradoPor(servidorLogado.getUsuario());
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}