package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class DocumentoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DocumentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8369061036276794959L;

	public List<AghDocumento> buscarDocumentosPeloAtendimento(Integer seqAtendimento) {
		return getCertificacaoDigitalFacade().buscarDocumentosPeloAtendimento(seqAtendimento);
	}
	
	public void atualizarVersaoDocumento(AghVersaoDocumento versao) {
		getCertificacaoDigitalFacade().atualizarAghVersaoDocumento(versao, true);		
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}
}
