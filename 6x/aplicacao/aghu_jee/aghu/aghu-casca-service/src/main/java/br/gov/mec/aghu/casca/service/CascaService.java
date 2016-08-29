package br.gov.mec.aghu.casca.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class CascaService implements ICascaService {

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
    private MessagesUtils messagesUtils;

	@Override
	public boolean verificarSeModuloEstaAtivo(String nomeModulo) {
		return cascaFacade.verificarSeModuloEstaAtivo(nomeModulo);
	}

	@Override
	public void validarSenha(String login, String senhaAtual) throws ServiceBusinessException, ServiceException {
		try {
			this.cascaFacade.validarSenha(login, senhaAtual);
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
