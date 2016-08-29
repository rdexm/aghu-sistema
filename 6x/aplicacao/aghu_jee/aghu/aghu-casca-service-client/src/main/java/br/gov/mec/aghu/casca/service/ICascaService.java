package br.gov.mec.aghu.casca.service;

import javax.ejb.Local;

import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Local
@Deprecated
public interface ICascaService {

	boolean verificarSeModuloEstaAtivo(String nomeModulo);

	/**
	 * Serviço para autenticar usuário
	 * 
	 * Web Service #41995
	 * 
	 * @param login
	 * @param senhaAtual
	 * @throws ServiceBusinessException
	 * @throws ServiceException
	 */
	void validarSenha(String login, String senhaAtual) throws ServiceBusinessException, ServiceException;
	
	
//	/**
//	 * #39000 - Serviço que retorna existe servidor categoria prof medicos
//	 * 
//	 * Web Service #39000
//	 * 
//	 * @param matricula
//	 * @param vinculo
//	 * @throws ServiceBusinessException
//	 * @throws ServiceException
//	 */
//	Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ServiceBusinessException, ServiceException;
}
