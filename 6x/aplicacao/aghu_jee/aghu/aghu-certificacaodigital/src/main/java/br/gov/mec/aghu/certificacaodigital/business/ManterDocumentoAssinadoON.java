package br.gov.mec.aghu.certificacaodigital.business;

import java.text.Normalizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.certificacaodigital.dao.AghDocumentoCertificadoDAO;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterDocumentoAssinadoON extends BaseBusiness {


private static final Log LOG = LogFactory.getLog(ManterDocumentoAssinadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghDocumentoCertificadoDAO aghDocumentoCertificadoDAO;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4183468444189060580L;

	public enum ManterDocumentoAssinadoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_CERTIFICACAO_DIGITAL_NAO_HABILITADA_DOCUMENTO_ASSINADO;
	}

	public AghDocumentoCertificado persistirAghDocumentoCertificado(
			AghDocumentoCertificado aghDocumentoCertificado)
			throws BaseException {

		// verificar se o HU possui parâmetro para certificação digital
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL);
		if (aghParametros.getVlrTexto().equals("N")) {
			throw new ApplicationBusinessException(
					ManterDocumentoAssinadoON.ManterDocumentoAssinadoONExceptionCode.MENSAGEM_CERTIFICACAO_DIGITAL_NAO_HABILITADA_DOCUMENTO_ASSINADO);
		}

		AghDocumentoCertificado retorno = null;

		String temp = Normalizer.normalize(aghDocumentoCertificado.getNome(), java.text.Normalizer.Form.NFD);
		temp = temp.replaceAll("[^\\p{ASCII}]","");
		temp = temp.replaceAll(" ", "");
		aghDocumentoCertificado.setNome(temp); 
		
		
		if (aghDocumentoCertificado.getSeq() == null) {
			// Realiza inserção
			getAghDocumentoCertificadoDAO().persistir(
					aghDocumentoCertificado);
			retorno = aghDocumentoCertificado;
		} else {
			// Realiza atualização
			retorno = getAghDocumentoCertificadoDAO().atualizar(
					aghDocumentoCertificado);
		}

		return retorno;
	}


	protected AghDocumentoCertificadoDAO getAghDocumentoCertificadoDAO() {
		return aghDocumentoCertificadoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

}
