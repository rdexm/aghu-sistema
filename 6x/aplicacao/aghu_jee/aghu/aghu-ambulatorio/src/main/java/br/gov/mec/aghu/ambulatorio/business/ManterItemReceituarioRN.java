package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterItemReceituarioRN extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(ManterItemReceituarioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MamReceituariosDAO mamReceituariosDAO;

@Inject
private MamItemReceituarioDAO mamItemReceituarioDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4980795335965837708L;

	public enum ManterItemReceituarioRNExceptionCode implements
	BusinessExceptionCode {

		MAM_00618, MAM_00619, MAM_00620;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * Remove objeto MamItemReceituario.
	 * 
	 * @param {MamItemReceituario} itemReceituario
	 * @throws ApplicationBusinessException
	 */
	public void removerItemReceituario(MamItemReceituario itemReceituario)
			throws ApplicationBusinessException {

		this.preRemoverItemReceituario(itemReceituario);
		this.getMamItemReceituarioDAO().remover(itemReceituario);
		this.getMamItemReceituarioDAO().flush();

	}


	/**
	 * @ORADB Trigger MAMT_IRC_BRD
	 * 
	 * @param {MamItemReceituario} itemReceituario
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverItemReceituario(MamItemReceituario itemReceituario)
			throws ApplicationBusinessException {

		this.verificaReceituarioJaFoiValidado('D', itemReceituario);

	}
	
	
	/**
	 * @ORADB mamk_irc_rn.rn_ircp_ver_validado
	 * 
	 * @param {String} operacao
	 * @param {MamItemReceituario} itemReceituario
	 * @throws ApplicationBusinessException 
	 */
	public void verificaReceituarioJaFoiValidado(char operacao,
			MamItemReceituario itemReceituario) throws ApplicationBusinessException {

		MamReceituarios rec = this.getMamReceituarioDAO().obterReceituarioIndPendente(itemReceituario.getReceituario());

		if (rec != null) {

			DominioIndPendenteAmbulatorio pendente = rec.getPendente();
			if (DominioIndPendenteAmbulatorio.V.equals(pendente)) {

				switch (operacao) {
				case 'I':
					ManterItemReceituarioRNExceptionCode.MAM_00618.throwException();
					break;

				case 'U':
					ManterItemReceituarioRNExceptionCode.MAM_00619.throwException();
					break;

				case 'D':
					ManterItemReceituarioRNExceptionCode.MAM_00620.throwException();
					break;	

				default:
					break;
				}

			}

		}


	}

	private MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}
	
	private MamReceituariosDAO getMamReceituarioDAO() {
		return mamReceituariosDAO;
	}

}
