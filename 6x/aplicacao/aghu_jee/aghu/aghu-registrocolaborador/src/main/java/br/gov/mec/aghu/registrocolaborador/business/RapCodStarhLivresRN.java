package br.gov.mec.aghu.registrocolaborador.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.dao.RapCodStarhLivresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RapCodStarhLivresRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RapCodStarhLivresRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapCodStarhLivresDAO rapCodStarhLivresDAO;

@Inject
private RapServidoresDAO rapServidoresDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8118886504326473682L;

	public enum RapCodStarhLivresRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO, MENSAGEM_CODIGO_NAO_PODE_SER_REAPROVEITADO
	}

	public void delete(RapCodStarhLivres rapCodStarhLivres)
			throws ApplicationBusinessException {
		RapCodStarhLivresDAO rapCodStarhLivresDAO = this.getCodStarhLivresDAO();
		rapCodStarhLivresDAO.remover(rapCodStarhLivres);
		rapCodStarhLivresDAO.flush();
	}

	public void insert(RapCodStarhLivres rapCodStarhLivres)
			throws ApplicationBusinessException {
		this.beforeRowInsert(rapCodStarhLivres);
		RapCodStarhLivresDAO rapCodStarhLivresDAO = this.getCodStarhLivresDAO();
		rapCodStarhLivresDAO.persistir(rapCodStarhLivres);
		rapCodStarhLivresDAO.flush();
	}

	/**
	 * ORADB: Trigger rapt_cstr_bri
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowInsert(RapCodStarhLivres rapCodStarhLivres)
			throws ApplicationBusinessException {

		RapServidores servidor = getRapServidoresDAO().obterServidor(rapCodStarhLivres);

		if (servidor != null){
			throw new ApplicationBusinessException(
					RapCodStarhLivresRNExceptionCode.MENSAGEM_CODIGO_NAO_PODE_SER_REAPROVEITADO);
		}
	}

	public RapCodStarhLivres obterCodStarhLivre(Integer id)
			throws ApplicationBusinessException {

		if (id == null) {
			throw new ApplicationBusinessException(
					RapCodStarhLivresRNExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getCodStarhLivresDAO().obterPorChavePrimaria(id);
	}
	
	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}
	
	protected RapCodStarhLivresDAO getCodStarhLivresDAO() {
		return rapCodStarhLivresDAO;
	}
}