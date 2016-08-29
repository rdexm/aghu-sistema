package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * 
 * @author fgka
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.ClassesOnRnNaoDevemSerPublicas"})
public abstract class AbstractCrudMedicamento<E extends BaseEntity> extends AbstractAGHUCrudPersist<E> {
	
	private static final long serialVersionUID = 8085851877534252521L;

	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private AfaTipoApresentacaoMedicamentoDAO afaTipoApresentacaoMedicamentoDAO;
	
	protected AbstractCrudMedicamento() {
		super();
	}

	public AfaMedicamentoDAO getAfaMedicamentoDAO() {
		return afaMedicamentoDAO;
	}	
	
	public AfaTipoApresentacaoMedicamentoDAO getApresentacaoDao() {
		
		return afaTipoApresentacaoMedicamentoDAO;
	}
	
	/**
	 * Cast necessario do tipo do retorno do metodo de {@link GenericDAO} para {@link AbstractMedicamentoDAO} 
	 */
	@Override
	public abstract AbstractMedicamentoDAO<E> getEntidadeDAO();
	
	/**
	 * 
	 * @param strObject
	 * @return
	 * @throws IllegalArgumentException
	 * @see AfaMedicamentoDAO#pesquisarMedicamentos(String)
	 */
	public List<AfaMedicamento> pesquisarMedicamentos(String strObject) {
		
		List<AfaMedicamento> result = null;
		
		if (strObject == null) {
			throw new IllegalArgumentException();
		}
		result = this.getAfaMedicamentoDAO().pesquisarMedicamentos(strObject);
		
		return result;
	}
	
	/**
	 * 
	 * @param strObject
	 * @return
	 * @throws IllegalArgumentException
	 * @see AfaMedicamentoDAO#pesquisarMedicamentosCount(String)
	 */
	public Long pesquisarMedicamentosCount(String strObject) {
		
		Long result = null;
		
		if (strObject == null) {
			throw new IllegalArgumentException();
		}
		result = this.getAfaMedicamentoDAO().pesquisarMedicamentosCount(strObject);
		
		return result;
	}
	
	/**
	 * 
	 * @param medicamento
	 * @return
	 * @throws IllegalArgumentException
	 * @see {@link AbstractMedicamentoDAO#pesquisarCount(AfaMedicamento)}
	 */
	public Long pesquisarCount(AfaMedicamento medicamento) {
		
		Long result = null;
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		result = this.getEntidadeDAO().pesquisarCount(
				medicamento);
		
		return result;
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return
	 * @throws IllegalArgumentException
	 * @see {@link AbstractMedicamentoDAO#pesquisar(Integer, Integer, String, boolean, AfaMedicamento)}
	 */
	public List<E> pesquisar(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento) {
		
		List<E> result = null;
		
		if (firstResult == null) {
			throw new IllegalArgumentException();
		}
		if (maxResult == null) {
			throw new IllegalArgumentException();
		}		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		result = this.getEntidadeDAO().pesquisar(firstResult,
				maxResult, orderProperty, asc, medicamento);
		
		return result;
	}
	@Override
	public void remover(E entidade, String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException {
		entidade = getEntidadeDAO().obterPorChavePrimaria(getChavePrimariaEntidade(entidade));
		super.remover(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
}
