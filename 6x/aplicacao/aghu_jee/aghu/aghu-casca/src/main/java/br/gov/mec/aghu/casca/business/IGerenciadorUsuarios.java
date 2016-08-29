package br.gov.mec.aghu.casca.business;

import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Interface para classe de gerenciamento de usuários, responsável por operações
 * de segurança sobre os usuários do sistema,
 * 
 * @author geraldo
 * 
 */
public interface IGerenciadorUsuarios {

	List<String> listarLoginsRegistrados(String filtro);

	void changePassword(String login, String password)
			throws ApplicationBusinessException;

	boolean authenticate(String username, String password)
			throws ApplicationBusinessException;

}
