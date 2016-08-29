package br.gov.mec.aghu.registrocolaborador.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.CentroCustoAtuacaoON.ServidorONExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CentroCustoAtuacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CentroCustoAtuacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3296856595269281312L;

	public enum ServidorRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_CENTRO_CUSTO_INATIVO;
	}

	public void validarCCAtuacao(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ServidorONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		if (servidor.getCentroCustoAtuacao() != null && ! servidor.getCentroCustoAtuacao().isAtivo()) {
			throw new ApplicationBusinessException(ServidorRNExceptionCode.MENSAGEM_CENTRO_CUSTO_INATIVO);
		}
	}
}
