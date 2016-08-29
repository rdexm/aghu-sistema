package br.gov.mec.aghu.mensageiro;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghJobDetail;

/**
 * @author felipe.palma
 * 
 */
@Stateless
public class MensageiroFacade extends BaseFacade implements IMensageiroFacade {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5288718950135710209L;
	
	@EJB
	private InternacoesExcedentesON internacoesExcedentesON;


	@Override
	public boolean enviarWhatsappDeInternacoesExcedentes(AghJobDetail job) throws ApplicationBusinessException {
		return getInternacoesExcedentesON().preEnviarMensagemWhatsApp(job);
	}

	
	protected InternacoesExcedentesON getInternacoesExcedentesON() {
		return internacoesExcedentesON;
	}
}
