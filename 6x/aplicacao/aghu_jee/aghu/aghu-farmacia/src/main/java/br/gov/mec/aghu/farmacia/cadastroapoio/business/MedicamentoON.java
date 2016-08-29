package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudOn;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @author fgka
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class MedicamentoON extends AbstractAGHUCrudOn<AfaMedicamento> {


@EJB
private MedicamentoRN medicamentoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6000234300594527538L;

	private static final Log LOG = LogFactory.getLog(MedicamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Override
	public BaseDao<AfaMedicamento> getEntidadeDAO() {
		
		return afaMedicamentoDAO;
	}

	@Override
	public AbstractAGHUCrudRn<AfaMedicamento> getRegraNegocio() {

		return medicamentoRN;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaMedicamento entidade) {

		return (entidade != null ? entidade.getMatCodigo() : null);
	}

	/**
	 * Retorna uma instância de AfaMedicamento pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 */
	public AfaMedicamento obterPorChavePrimaria(Integer chavePrimaria) {
		return getEntidadeDAO().obterPorChavePrimaria(chavePrimaria);
	}

	/**
	 * Busca na base uma lista de AfaMedicamento pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<AfaMedicamento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento elemento) {

		return ((AfaMedicamentoDAO) getEntidadeDAO()).pesquisarMedicamentos(
				firstResult, maxResult, orderProperty, asc, elemento);
	}

	/**
	 * Busca na base o número de elementos da lista de AfaMedicamento pelo
	 * filtro
	 * 
	 * @param elemento
	 * @return
	 */
	public Long pesquisarCount(AfaMedicamento elemento) {
		return ((AfaMedicamentoDAO) getEntidadeDAO())
				.pesquisarMedicamentosCount(elemento);
	}
	
	public AfaMedicamento obterMedicamentoEdicao(final Integer matCodigo) {
		return getEntidadeDAO().obterPorChavePrimaria(matCodigo, true, 
				AfaMedicamento.Fields.SCO_MATERIAL, 
				AfaMedicamento.Fields.MPM_TIPO_FREQ_APRAZAMENTOS,
				AfaMedicamento.Fields.MPM_UNIDADE_MEDIDA_MEDICAS, 
				AfaMedicamento.Fields.TPR, 
				AfaMedicamento.Fields.TUM);
	}
}
