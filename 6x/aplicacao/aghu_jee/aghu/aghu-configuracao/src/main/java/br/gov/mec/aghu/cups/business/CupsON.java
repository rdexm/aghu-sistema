package br.gov.mec.aghu.cups.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.configuracao.dao.ImpUsuarioVisualizaDAO;
import br.gov.mec.aghu.model.cups.ImpUsuarioVisualiza;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio para impressão via CUPS
 * 
 * @author Roberto HC-UFPR. Refatorado por Ricardo Costa (HCPA).
 * 
 */
@Stateless
public class CupsON extends BaseBusiness {

	private static final long serialVersionUID = 4174014523981606840L;

	private static final Log LOG = LogFactory.getLog(CupsON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private enum CupsONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_REDIRECIONAMENTO_CUPS_DESATIVADO;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ImpUsuarioVisualizaDAO impUsuarioVisualizaDAO;

	protected ImpUsuarioVisualizaDAO getImpUsuarioVisualizaDAO() {
		return impUsuarioVisualizaDAO;
	}

	private IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}

	/**
	 * Buscar situacao ativa do modal CUPS.
	 * 
	 * @param usuario
	 * @return
	 */
	public ImpUsuarioVisualiza buscarModalAtivoCups(Usuario usuario) {
		return getImpUsuarioVisualizaDAO().buscarModalAtivoCups(usuario);
	}

	/**
	 * Incluir ImpUsuarioVisualiza.
	 * 
	 * @param impUsuarioVisualiza
	 */
	public void incluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza) {
		getImpUsuarioVisualizaDAO().persistir(impUsuarioVisualiza);
		getImpUsuarioVisualizaDAO().flush();
	}

	/**
	 * Remover ImpUsuarioVisualiza.
	 * 
	 * @param impUsuarioVisualiza
	 */
	public void excluirUsuarioVisualiza(ImpUsuarioVisualiza impUsuarioVisualiza) {
		impUsuarioVisualiza = getImpUsuarioVisualizaDAO().merge(
				impUsuarioVisualiza);
		getImpUsuarioVisualizaDAO().remover(impUsuarioVisualiza);
		getImpUsuarioVisualizaDAO().flush();
	}

	/**
	 * Método que verifica se CUPS deve ser utilizado.
	 * 
	 * @return Boolean
	 */
	public boolean isCupsAtivo() {
		try {
			String prm = getParametroFacade().buscarValorTexto(
					AghuParametrosEnum.P_CUPS_REDIRECIONAMENTO);

			return ("1").equals(prm);

		} catch (ApplicationBusinessException e) {
			LOG.error("Registro em AGH_PARAMETROS não encontrado.");
		}

		return false;
	}

	/**
	 * Método que verifica se CUPS deve ser utilizado.
	 * 
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public void verificarCupsAtivo() throws ApplicationBusinessException {
		if (!this.isCupsAtivo()) {
			throw new ApplicationBusinessException(
					CupsONExceptionCode.MENSAGEM_REDIRECIONAMENTO_CUPS_DESATIVADO);
		}
	}

}