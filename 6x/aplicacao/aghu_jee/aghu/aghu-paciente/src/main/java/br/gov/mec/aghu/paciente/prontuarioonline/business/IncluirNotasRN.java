package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class IncluirNotasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(IncluirNotasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	private static final long serialVersionUID = -8829302134959571973L;

	/**
	 * #15882
	 * @ORADB AIPP_INATIVA_DOC_CERTIF 
	 * @author Cristiano Alexandre Moretti
	 * @since 23/08/2012
	 */
	protected void aippInativaDocCertif(Integer seq) {
		List<AghVersaoDocumento> versoesDocumentos = getCertificacaoDigitalFacade().pesquisarVersoesDocumentosNotasAdicionais(seq);
		for (AghVersaoDocumento aghVersaoDocumento : versoesDocumentos) {
			aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
			getCertificacaoDigitalFacade().persistirAghVersaoDocumento(aghVersaoDocumento);
		}
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}
	
}
