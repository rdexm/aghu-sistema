package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoDcih;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatDocumentoCobrancaAihsDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.vo.RnSutcAtuSaldoVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ReapresentarContaHospitalarRN extends AbstractFatDebugLogEnableRN {


private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

@EJB
private FatkCthRN fatkCthRN;

@EJB
private FatkCth5RN fatkCth5RN;

private static final Log LOG = LogFactory.getLog(ReapresentarContaHospitalarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 810518267895311327L;

	public void reapresentarContaHospitalar(Integer cthSeq, FatMotivoRejeicaoConta motivoRejeicaoConta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.pMotivo(cthSeq, motivoRejeicaoConta, null, true, null, null, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void desfazerReapresentacaoContaHospitalar(Integer cthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		try {
			Boolean retorno = this.getFatkCth5RN().rnCthcDesfazReapr(cthSeq, nomeMicrocomputador, dataFimVinculoServidor);
			if (!Boolean.TRUE.equals(retorno)) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_CONTA_REAPRESENTADA_NAO_PODE_SER_DESFEITA);
			}
		} catch (BaseException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw e;
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_ERRO_DESFAZER_REAPRESENTACAO, e.getMessage());
		}
	}

	public void rejeitarContaHospitalar(Integer cthSeq, FatMotivoRejeicaoConta motivoRejeicaoConta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.pMotivo(cthSeq, motivoRejeicaoConta, null, null, true, null, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void pMotivo(Integer cthSeq, FatMotivoRejeicaoConta motivoRejeicaoConta, FatDocumentoCobrancaAihs documentoCobrancaAih,
			Boolean reapresenta, Boolean rejeita, Boolean reapresentaDcih, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		FatContasHospitalaresDAO fatContasHospitalaresDAO = this.getFatContasHospitalaresDAO();

		FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(cthSeq);

		if (Boolean.TRUE.equals(rejeita)) {
			IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
			ContaHospitalarON contaHospitalarON = this.getContaHospitalarON();
			SaldoBancoCapacidadeAtulizacaoRN saldoBancoCapacidadeAtulizacaoRN = this.getSaldoBancoCapacidadeAtulizacaoRN();
			SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN = this.getSaldoUtiAtualizacaoRN();
			FatEspelhoAihDAO fatEspelhoAihDAO = this.getFatEspelhoAihDAO();

			if (motivoRejeicaoConta != null) {
				Boolean retorno = getFatkCth5RN().fatcValidaProcUti(cthSeq);
				if (Boolean.TRUE.equals(retorno)) {
					if (contaHospitalar != null) {
						RnSutcAtuSaldoVO retornoVO = saldoUtiAtualizacaoRN.atualizarSaldoDiariasUti(DominioAtualizacaoSaldo.D,
								contaHospitalar.getDtAltaAdministrativa(), contaHospitalar.getDiasUtiMesInicial(),
								contaHospitalar.getDiasUtiMesAnterior(), contaHospitalar.getDiasUtiMesAlta(),
								contaHospitalar.getIndIdadeUti(), nomeMicrocomputador);

						if (retornoVO == null || !Boolean.TRUE.equals(retornoVO.isRetorno())) {
							throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_ERRO_ATUALIZAR_UTI);
						}

						FatEspelhoAih espelhoAih = fatEspelhoAihDAO.buscarPrimeiroEspelhoAIH(cthSeq, 1,DominioModuloCompetencia.INT);
						if (espelhoAih != null) {
							saldoBancoCapacidadeAtulizacaoRN.atualizarSaldoDiariasBancoCapacidade(espelhoAih.getDataInternacao(),
									espelhoAih.getDataSaida(), espelhoAih.getEspecialidadeAih() != null ? espelhoAih
											.getEspecialidadeAih().intValue() : null, espelhoAih.getId().getCthSeq(),
									DominioAtualizacaoSaldo.D, true, nomeMicrocomputador, dataFimVinculoServidor);
							faturamentoFacade.evict(espelhoAih);
						}
					}
				}

				FatContasHospitalares oldContaHospitalar = null;
				try {
					oldContaHospitalar = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
				} catch (Exception e) {
					this.logError(EXCECAO_CAPTURADA, e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				contaHospitalar.setIndSituacao(DominioSituacaoConta.R);
				contaHospitalar.setMotivoRejeicao(motivoRejeicaoConta);

				contaHospitalarON.atualizarContaHospitalar(contaHospitalar, oldContaHospitalar, true, nomeMicrocomputador, dataFimVinculoServidor);
			} else {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_INFORME_MOTIVO_REJEICAO_CONTA_HOSPITALAR);
			}
		} else if (Boolean.TRUE.equals(reapresenta)) {
			FatkCthRN fatkCthRN = this.getFatkCthRN();

			if (motivoRejeicaoConta != null) {
				try {
					Boolean retorno = fatkCthRN.rnCthcAtuReapresenta(cthSeq, motivoRejeicaoConta.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
					if (!Boolean.TRUE.equals(retorno)) {
						throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_CONTA_HOSPITALAR_NAO_PODE_SER_REAPRESENTADA);
					}
				} catch (Exception e) {
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_ERRO_REAPRESENTAR, e.getMessage());
				}
			} else {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_INFORME_MOTIVO_REJEICAO_CONTA_HOSPITALAR);
			}
		} else if (Boolean.TRUE.equals(reapresentaDcih)) {
			FaturamentoRN faturamentoRN = this.getFaturamentoRN();
			FatkCthRN fatkCthRN = this.getFatkCthRN();
			FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO = this.getFatDocumentoCobrancaAihsDAO();

			if (motivoRejeicaoConta != null) {
				Boolean vOk = false;

				FatContasHospitalares regDcih = null;
				FatContasHospitalares regDcihComp = null;
				String vDcih = documentoCobrancaAih != null ? documentoCobrancaAih.getCodigoDcih() : null;
				DominioSituacaoConta[] situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.O, DominioSituacaoConta.R };

				regDcihComp = fatContasHospitalaresDAO.buscarPrimeiraContaHospitalar(vDcih, situacoes);
				if (regDcihComp != null && regDcihComp.getSeq() != null) {
					throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_DCIH_NAO_PODE_SER_REAPRESENTADA);
				} else {
					List<FatContasHospitalares> contasHospitalares = fatContasHospitalaresDAO.listarContasHospitalaresIgnorandoSituacoes(vDcih, situacoes);
					if (contasHospitalares != null && !contasHospitalares.isEmpty()) {
						for (int i = 0; i < contasHospitalares.size(); i++) {
							regDcih = contasHospitalares.get(i);
							if (DominioSituacaoConta.R.equals(regDcih.getIndSituacao())) {
								vOk = true;
							} else {
								vOk = fatkCthRN.rnCthcAtuReapresenta(regDcih.getSeq(), motivoRejeicaoConta.getSeq(), nomeMicrocomputador, dataFimVinculoServidor);
								if (Boolean.FALSE.equals(vOk)) {
									throw new ApplicationBusinessException(FaturamentoExceptionCode.MENSAGEM_DCIH_NAO_PODE_SER_REAPRESENTADA);
								}
							}
						}
					}
				}

				if (Boolean.TRUE.equals(vOk) && regDcih != null) {
					List<FatDocumentoCobrancaAihs> documentosCobrancaAihs = fatDocumentoCobrancaAihsDAO
							.listarDocumentosCobranca(regDcih.getDocumentoCobrancaAih() != null ? regDcih.getDocumentoCobrancaAih()
									.getCodigoDcih() : null);
					if (documentosCobrancaAihs != null && !documentosCobrancaAihs.isEmpty()) {
						for (int i = 0; i < documentosCobrancaAihs.size(); i++) {
							FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs = documentosCobrancaAihs.get(0);

							FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs = null;
							try {
								oldFatDocumentoCobrancaAihs = getFaturamentoRN().clonarFatDocumentoCobrancaAihs(
										fatDocumentoCobrancaAihs);
							} catch (Exception e) {
								this.logError(EXCECAO_CAPTURADA, e);
								FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
							}

							fatDocumentoCobrancaAihs.setIndSituacao(DominioSituacaoDcih.R);
							faturamentoRN.atualizarFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs, oldFatDocumentoCobrancaAihs);
						}
					}
				}
			} else {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_INFORME_MOTIVO_REJEICAO_CONTA_HOSPITALAR);
			}
		}
	}

	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}

	protected FatkCth5RN getFatkCth5RN() {
		return fatkCth5RN;
	}

}
