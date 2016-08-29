package br.gov.mec.aghu.cups.business;

import java.io.Serializable;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.model.cups.ImpUsuarioVisualiza;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ICupsFacade extends Serializable {

	/**
	 * Busca permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param usuario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public ImpUsuarioVisualiza buscarModalAtivoCups(Usuario usuario)
			throws ApplicationBusinessException;

	/**
	 * Inclui permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param impUsuarioVisualiza
	 * @throws ApplicationBusinessException
	 */
	public void incluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza)
			throws ApplicationBusinessException;

	/**
	 * Exclui permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param impUsuarioVisualiza
	 * @throws ApplicationBusinessException
	 */
	public void excluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza)
			throws ApplicationBusinessException;

	/**
	 * Método invocado para verificar se o redirecionamento para o CUPS está
	 * ativo.
	 * 
	 * @return true se ativado
	 */
	public boolean isCupsAtivo();

	/**
	 * Verifica se o servidor cups está ativo.
	 * 
	 * @throws ApplicationBusinessException
	 *             se servidor cups desativado.
	 */
	public void verificarCupsAtivo() throws ApplicationBusinessException;

}