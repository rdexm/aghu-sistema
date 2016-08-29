package br.gov.mec.aghu.certificacaodigital.action;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStoreException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.bouncycastle.cms.CMSException;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.ufrgs.hcpa.crypto.CMSValidator;
import br.ufrgs.hcpa.crypto.CMSValidatorResult;



public class ValidarCertificadoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4857267206316118694L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	private Integer versaoDocumentoSeq;
	private AghVersaoDocumento aghVersaoDocumento;
	private List<CMSValidatorResult> result;

	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}	
	
	private enum ValidarCertificadoControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO;
	}

	public void validarAssinaturas() throws ApplicationBusinessException,
			NoSuchAlgorithmException, NoSuchProviderException,
			CertStoreException, CMSException {
		if (this.versaoDocumentoSeq == null) {
			apresentarMsgNegocio(
							Severity.ERROR,
							ValidarCertificadoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
			return;
		}

		this.aghVersaoDocumento = this.certificacaoDigitalFacade
				.visualizarDocumentoAssinado(this.versaoDocumentoSeq);

		byte[] is = this.aghVersaoDocumento.getEnvelope();

		this.result = CMSValidator.validate(is);
	}

	public Integer getVersaoDocumentoSeq() {
		return versaoDocumentoSeq;
	}

	public void setVersaoDocumentoSeq(Integer versaoDocumentoSeq) {
		this.versaoDocumentoSeq = versaoDocumentoSeq;
	}

	public AghVersaoDocumento getAghVersaoDocumento() {
		return aghVersaoDocumento;
	}

	public void setAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento) {
		this.aghVersaoDocumento = aghVersaoDocumento;
	}

	public List<CMSValidatorResult> getResult() {
		return result;
	}

	public void setResult(List<CMSValidatorResult> result) {
		this.result = result;
	}

}
