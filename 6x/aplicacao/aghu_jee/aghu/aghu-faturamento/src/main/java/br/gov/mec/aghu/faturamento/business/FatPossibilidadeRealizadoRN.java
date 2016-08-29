package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatPossibilidadeRealizadoDAO;
import br.gov.mec.aghu.model.FatPossibilidadeRealizado;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatPossibilidadeRealizadoRN extends BaseBusiness {

	@Inject
	private FatPossibilidadeRealizadoDAO fatPossibilidadeRealizadoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory
			.getLog(FatPossibilidadeRealizadoRN.class);

	private enum FatPossibilidadeRealizadoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_RESTRICAO_CAMPOS_IGUAIS;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Metodo que persiste a possibilidade de faturamento realizada.
	 * 
	 * @param possibilidadeRealizado
	 * @throws ApplicationBusinessException
	 */
	public void persistirPossibilidadeRealizado(
			final FatPossibilidadeRealizado possibilidadeRealizado)
			throws ApplicationBusinessException {

		final FatPossibilidadeRealizado original = this.fatPossibilidadeRealizadoDAO
				.obterOriginal(possibilidadeRealizado);

		if (original != null && original.equals(possibilidadeRealizado)) {
			throw new ApplicationBusinessException(
					FatPossibilidadeRealizadoRNExceptionCode.MENSAGEM_RESTRICAO_CAMPOS_IGUAIS);
		} else {

			possibilidadeRealizado.setCriadoEm(new Date());
			possibilidadeRealizado.setCriadoPor(servidorLogadoFacade
					.obterServidorLogado().getUsuario());
			this.fatPossibilidadeRealizadoDAO.persistir(possibilidadeRealizado);
		}

	}

}