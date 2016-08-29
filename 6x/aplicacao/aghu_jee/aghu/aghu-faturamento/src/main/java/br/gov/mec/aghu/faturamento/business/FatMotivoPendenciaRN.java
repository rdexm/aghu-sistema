package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatMotivoPendenciaDAO;
import br.gov.mec.aghu.model.FatMotivoPendencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatMotivoPendenciaRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3109361129731865838L;

	private static final Log LOG = LogFactory
			.getLog(FatMotivoPendenciaRN.class);

	private enum FatMotivoPendenciaRNRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DADOS_INCOMPLETOS_MOTIVO_PENDENCIA;
	}

	@Inject
	private FatMotivoPendenciaDAO fatMotivoPendenciaDAO;

	/**
	 * 
	 * @param fatMotivoPendencia
	 * @throws ApplicationBusinessException
	 */
	public void persistirFatMotivoPendencia(
			final FatMotivoPendencia fatMotivoPendencia)
			throws ApplicationBusinessException {

		if (StringUtils.isEmpty(fatMotivoPendencia.getDescricao())) {
			throw new ApplicationBusinessException(
					FatMotivoPendenciaRNRNExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_MOTIVO_PENDENCIA);
		}

		final FatMotivoPendencia original = this.fatMotivoPendenciaDAO
				.obterOriginal(fatMotivoPendencia.getSeq());

		if (original == null) { // INSERIR

			this.fatMotivoPendenciaDAO.persistir(fatMotivoPendencia);

		} else { // ALTERAR

			this.fatMotivoPendenciaDAO.merge(fatMotivoPendencia);

		}
	}

	public void removerFatMotivoPendencia(final Short seq) throws BaseException {

		FatMotivoPendencia fatMotivoPendencia = this.fatMotivoPendenciaDAO
				.obterPorChavePrimaria(seq);
		this.fatMotivoPendenciaDAO.remover(fatMotivoPendencia);
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
