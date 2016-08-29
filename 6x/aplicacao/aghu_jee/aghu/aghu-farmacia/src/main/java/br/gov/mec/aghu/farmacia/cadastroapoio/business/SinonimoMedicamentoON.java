package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaSinonimoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class SinonimoMedicamentoON extends
		AbstractCrudMedicamento<AfaSinonimoMedicamento> {


@EJB
private SinonimoMedicamentoRN sinonimoMedicamentoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -145923913250022302L;

	private static final Log LOG = LogFactory.getLog(SinonimoMedicamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	public enum SinonimoMedicamentoONExceptionCode implements BusinessExceptionCode {
		EXCEPTION_SINONIMO_JA_CADASTRADO;
	}
	
	@Inject
	private AfaSinonimoMedicamentoDAO afaSinonimoMedicamentoDAO;
	
	
	/**
	 * Retorna um sinonimo de medicamento pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 */
	public AfaSinonimoMedicamento obterPorChavePrimaria(
			AfaSinonimoMedicamentoId chavePrimaria) {
		return getEntidadeDAO().obterPorChavePrimaria(chavePrimaria);
	}

	/**
	 * Realiza a exclusão de um sinonimo de medicamento
	 * 
	 * @param elemento
	 */
	public void removerSinonimoMedicamento(AfaSinonimoMedicamento elemento)
			throws ApplicationBusinessException {
		elemento = getEntidadeDAO().obterPorChavePrimaria(elemento.getId());
		getEntidadeDAO().remover(elemento);
		getEntidadeDAO().flush();
		getSinonimoMedicamentoRN().posDeleteSinonimoMedicamento(elemento);
	}

	/**
	 * Realiza a inclusão/alteração de um sinonimo de medicamento
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public AfaSinonimoMedicamento inserirAtualizarSinonimoMedicamento(AfaSinonimoMedicamento elemento) throws ApplicationBusinessException {
	
		if (existeSinonimoMedicamento(elemento)) {
			throw new ApplicationBusinessException(SinonimoMedicamentoONExceptionCode.EXCEPTION_SINONIMO_JA_CADASTRADO);
		}
		
		if (elemento.getId().getSeqp() != null) {

			getEntidadeDAO().desatachar(elemento);
			AfaSinonimoMedicamento afaSinonimoMedicamentoOld = getEntidadeDAO().obterPorChavePrimaria(elemento.getId());
			getEntidadeDAO().desatachar(afaSinonimoMedicamentoOld);
			getSinonimoMedicamentoRN().preUpdateSinonimoMedicamento(elemento);
			elemento = getEntidadeDAO().atualizar(elemento);
			getEntidadeDAO().flush();
			getSinonimoMedicamentoRN().posUpdateSinonimoMedicamento(afaSinonimoMedicamentoOld, elemento);

		} else {
			getSinonimoMedicamentoRN().preInsertSinonimoMedicamento(elemento);
			getEntidadeDAO().persistir(elemento);
			getEntidadeDAO().flush();
		}
		return elemento;
	}
	
	protected SinonimoMedicamentoRN getSinonimoMedicamentoRN() {
		return sinonimoMedicamentoRN;
	}
	
	public boolean existeSinonimoMedicamento(AfaSinonimoMedicamento sinonimoMedicamento) {
		return getAfaSinonimoMedicamentoDAO().pesquisaSinonimoMedicamentoCount(sinonimoMedicamento) != 0 ? true : false;
	}
	
	public AfaSinonimoMedicamentoDAO getAfaSinonimoMedicamentoDAO() {
		return afaSinonimoMedicamentoDAO;
	}

	public void setAfaSinonimoMedicamentoDAO(
			AfaSinonimoMedicamentoDAO afaSinonimoMedicamentoDAO) {
		this.afaSinonimoMedicamentoDAO = afaSinonimoMedicamentoDAO;
	}

	@Override
	public AbstractMedicamentoDAO<AfaSinonimoMedicamento> getEntidadeDAO() {
		return afaSinonimoMedicamentoDAO;
	}

	@Override
	public AbstractAGHUCrudRn<AfaSinonimoMedicamento> getRegraNegocio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaSinonimoMedicamento entidade) {

		return (entidade != null ? entidade.getId() : null);
	}
}
