package br.gov.mec.aghu.cups.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.cups.ImpUsuarioVisualiza;

/**
 * Porta de entrada do módulo do CUPS.
 * 
 * @author riccosta
 * 
 */
@Modulo(ModuloEnum.CONFIGURACAO)
@Stateless
public class CupsFacade extends BaseFacade implements ICupsFacade {

	private static final long serialVersionUID = -783411148399000824L;

	@EJB
	private CupsON cupsON;

	protected CupsON getCupsON() {
		return cupsON;
	}

	/**
	 * Busca permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param usuario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public ImpUsuarioVisualiza buscarModalAtivoCups(Usuario usuario)
			throws ApplicationBusinessException {
		return getCupsON().buscarModalAtivoCups(usuario);
	}

	/**
	 * Inclui permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param impUsuarioVisualiza
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void incluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza)
			throws ApplicationBusinessException {
		getCupsON().incluirUsuarioVisualiza(impUsuarioVisualiza);
	}

	/**
	 * Exclui permissão de usuário de visualizar modal de redirecionamento.
	 * 
	 * @param impUsuarioVisualiza
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void excluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza)
			throws ApplicationBusinessException {
		getCupsON().excluirUsuarioVisualiza(impUsuarioVisualiza);
	}

	/**
	 * Método invocado para verificar se o redirecionamento para o CUPS está
	 * ativo.
	 * 
	 * @return true se ativo
	 */
	@Override
	public boolean isCupsAtivo() {
		return getCupsON().isCupsAtivo();
	}

	@Override
	public void verificarCupsAtivo() throws ApplicationBusinessException {
		getCupsON().verificarCupsAtivo();		
	}
	
	

}
