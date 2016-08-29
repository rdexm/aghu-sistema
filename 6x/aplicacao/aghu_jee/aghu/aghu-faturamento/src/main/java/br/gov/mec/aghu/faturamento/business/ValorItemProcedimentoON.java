package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ValorItemProcedimentoON extends BaseBusiness {


@EJB
private ValorItemProcedimentoRN valorItemProcedimentoRN;

private static final Log LOG = LogFactory.getLog(ValorItemProcedimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;

	private enum ValorItemProcedimentoONExceptionCode implements
			BusinessExceptionCode {
		VALOR_ITEM_PROCEDIMENTO_DATA_INVALIDA, VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_ANTES_INICIAL_INVALIDA, VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_INVALIDA

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2574489481607606250L;

	/**
	 * Metodo para persistir um FatVlrItemProcedHospComps.
	 * 
	 * @param FatItensProcedHospitalar
	 * @throws BaseException
	 */
	public FatVlrItemProcedHospComps persistirFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps fatVlrItemProcedHospComps,
			final Integer seq, final Short phoSeq) throws BaseException {
		FatVlrItemProcedHospComps retorno;
		if (fatVlrItemProcedHospComps.getId() == null
				|| (fatVlrItemProcedHospComps.getId() != null && (fatVlrItemProcedHospComps
						.getId().getIphPhoSeq() == null || fatVlrItemProcedHospComps
						.getId().getIphSeq() == null))) {
			fatVlrItemProcedHospComps.getId().setIphPhoSeq(phoSeq);
			fatVlrItemProcedHospComps.getId().setIphSeq(seq);
			retorno = this
					.inserirFatVlrItemProcedHospComps(fatVlrItemProcedHospComps);
		} else {
			retorno = this
					.atualizarFatVlrItemProcedHospComps(fatVlrItemProcedHospComps);
		}
		getFatVlrItemProcedHospCompsDAO().flush();
		return retorno;
	}

	/**
	 * Atualiza um fatVlrItemProcedHospComps (Valor Item Procedimento) existente
	 * 
	 * @param FatVlrItemProcedHospComps
	 * @throws BaseException
	 */
	private FatVlrItemProcedHospComps atualizarFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps fatVlrItemProcedHospComps)
			throws BaseException {
		getValorItemProcedimentoRN().processarAntesAtualizarFattIpcAsu(
				fatVlrItemProcedHospComps);
		getValorItemProcedimentoRN().processarAntesAtualizarFattIpcBru(
				fatVlrItemProcedHospComps);
		FatVlrItemProcedHospComps retorno = getFatVlrItemProcedHospCompsDAO().merge(
				fatVlrItemProcedHospComps);
		getFatVlrItemProcedHospCompsDAO().flush();
		return retorno;
	}

	/**
	 * Insere um novo fatVlrItemProcedHospComps (Valor Item Procedimento)
	 * 
	 * @param FatVlrItemProcedHospComps
	 * @throws BaseException
	 */
	private FatVlrItemProcedHospComps inserirFatVlrItemProcedHospComps(
			final FatVlrItemProcedHospComps fatVlrItemProcedHospComps)
			throws BaseException {
		getValorItemProcedimentoRN().processarAntesInserirFattIpcBri(
				fatVlrItemProcedHospComps);
		getFatVlrItemProcedHospCompsDAO()
				.persistir(fatVlrItemProcedHospComps);
		getValorItemProcedimentoRN().processarAposInserirFattIpcAsi(
				fatVlrItemProcedHospComps);
		return fatVlrItemProcedHospComps;
	}

	protected ValorItemProcedimentoRN getValorItemProcedimentoRN() {
		return valorItemProcedimentoRN;
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

	public void validarCamposFatVlrItemProcedHospComps(
			FatVlrItemProcedHospComps vlrItemProcedHospComps,
			FatVlrItemProcedHospComps vigente) throws ApplicationBusinessException {
		if (vlrItemProcedHospComps.getId().getDtInicioCompetencia() != null
				&& vlrItemProcedHospComps.getDtFimCompetencia() != null
				&& vlrItemProcedHospComps.getId().getDtInicioCompetencia()
						.after(vlrItemProcedHospComps.getDtFimCompetencia())) {
			throw new ApplicationBusinessException(
					ValorItemProcedimentoONExceptionCode.VALOR_ITEM_PROCEDIMENTO_DATA_INVALIDA);
		}
		if (vigente != null
				&& !vlrItemProcedHospComps.getId().equals(vigente.getId())) {
			if (vlrItemProcedHospComps.getId().getDtInicioCompetencia()
					.before(vigente.getId().getDtInicioCompetencia())) {
				throw new ApplicationBusinessException(
						ValorItemProcedimentoONExceptionCode.VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_ANTES_INICIAL_INVALIDA);
			}
			if (vigente.getDtFimCompetencia() != null
					&& vlrItemProcedHospComps.getId().getDtInicioCompetencia()
							.before(vigente.getDtFimCompetencia())) {
				throw new ApplicationBusinessException(
						ValorItemProcedimentoONExceptionCode.VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_INVALIDA);
			}
		}
	}
}
