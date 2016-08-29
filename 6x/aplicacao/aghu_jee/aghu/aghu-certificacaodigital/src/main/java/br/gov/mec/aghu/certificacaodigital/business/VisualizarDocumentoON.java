package br.gov.mec.aghu.certificacaodigital.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VisualizarDocumentoON extends BaseBusiness {

	private static final Log LOG = LogFactory
			.getLog(VisualizarDocumentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2568579935654164636L;

	public enum VisualizarDocumentoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_DOCUMENTO_ORIGINAL_NULO, MENSAGEM_DOCUMENTO_ASSINADO_NULO;
	}

	/**
	 * Realiza a busca e validação de um documento original. O documento pode
	 * estar pendente ou assinado.
	 * 
	 * @param seqAghVersaoDocumentos
	 * 
	 */
	public AghVersaoDocumento visualizarDocumentoOriginal(
			Integer seqAghVersaoDocumentos) throws ApplicationBusinessException {

		if (seqAghVersaoDocumentos == null) {
			throw new ApplicationBusinessException(
					VisualizarDocumentoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		AghVersaoDocumento doc = this.getAghVersaoDocumentoDAO()
				.obterDocumentoOriginal(seqAghVersaoDocumentos);

		if (doc == null) {
			throw new ApplicationBusinessException(
					VisualizarDocumentoONExceptionCode.MENSAGEM_DOCUMENTO_ORIGINAL_NULO);
		}

		return doc;
	}

	public AghVersaoDocumento visualizarDocumentoAssinado(
			Integer seqAghVersaoDocumentos) throws ApplicationBusinessException {

		if (seqAghVersaoDocumentos == null) {
			throw new ApplicationBusinessException(
					VisualizarDocumentoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		AghVersaoDocumento doc = this.getAghVersaoDocumentoDAO()
				.obterDocumentoAssinado(seqAghVersaoDocumentos);

		if (doc == null) {
			throw new ApplicationBusinessException(
					VisualizarDocumentoONExceptionCode.MENSAGEM_DOCUMENTO_ASSINADO_NULO);
		}

		if (doc.getEnvelope() == null) {
			return null;
		}

		return doc;
	}

	protected AghVersaoDocumentoDAO getAghVersaoDocumentoDAO() {
		return aghVersaoDocumentoDAO;
	}

}
