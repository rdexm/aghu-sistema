package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatExclusaoCritica;

public class FatExclusaoCriticaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatExclusaoCritica> {

	private static final long serialVersionUID = -8737599175773363578L;

	private DetachedCriteria obterCriteriaPorCodigo(String codigo) {
 		DetachedCriteria criteria = DetachedCriteria.forClass(FatExclusaoCritica.class);
		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CODIGO.toString(), codigo));
		return criteria;
	}
	
	public FatExclusaoCritica obterPorCodigo(String codigo) {
		
		DetachedCriteria criteria = obterCriteriaPorCodigo(codigo);
		return (FatExclusaoCritica) this.executeCriteriaUniqueResult(criteria);
	}
		 
	/*#31004*/
	public FatExclusaoCritica obterPorCodigoESituacao(String codigo, DominioSituacao indSituacao) {
			
		DetachedCriteria criteria = obterCriteriaPorCodigo(codigo);
		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.IND_SITUACAO.toString(), indSituacao));
				
		return (FatExclusaoCritica) this.executeCriteriaUniqueResult(criteria);
	}
	
	public FatExclusaoCritica buscarPrimeiraExclusaoCritica(DominioSituacao idMenor, DominioSituacao idMaior, DominioSituacao cbo, DominioSituacao qtd, DominioSituacao permMenor, DominioSituacao cns,
			DominioSituacao indSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExclusaoCritica.class);

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.ID_MENOR.toString(), idMenor));

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.ID_MAIOR.toString(), idMaior));

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CBO.toString(), cbo));

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.QTD.toString(), qtd));

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.PERM_MENOR.toString(), permMenor));

		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CNS.toString(), cns));
		
		criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.IND_SITUACAO.toString(), indSituacao)); //MARINA 23/07/2013		
		
		List<FatExclusaoCritica> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @return lista com todas as exclusoes criticas de acordo com os parametros de pesquisa
	 */
	public List<FatExclusaoCritica> listarExclusoesCriticas(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, final FatExclusaoCritica fatExclusaoCriticafiltro){
		DetachedCriteria criteria = montarQueryExclusaoCritica(fatExclusaoCriticafiltro, true);
		if (StringUtils.isEmpty(orderProperty)) {
			orderProperty = FatExclusaoCritica.Fields.SEQ.toString();
			asc = true;
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);		
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para excluir uma exclusao critica da tabela
	 */
	public void excluirExclusaoCritica(FatExclusaoCritica fatExclusaoCritica){
		excluirExclusaoCritica(fatExclusaoCritica);
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para editar uma linha da tabela
	 */
	public void atualizarExclusaoCritica(FatExclusaoCritica fatExclusaoCritica){
		atualizarExclusaoCritica(fatExclusaoCritica);
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para retornar o count da tebela
	 */
	public Long pesquisarExclusaoCriticaCount(FatExclusaoCritica fatExclusaoCritica){
		DetachedCriteria criteria = montarQueryExclusaoCritica(fatExclusaoCritica, false);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo que retorna a criteria com os filtros certos
	 */
	private DetachedCriteria montarQueryExclusaoCritica(final FatExclusaoCritica fatExclusaoCriticafiltro, boolean ordenado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExclusaoCritica.class);
		// Evita NPath complexity
		inserirFiltros(criteria, fatExclusaoCriticafiltro, ordenado);		
		return criteria;
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para editar os filtros utilizados na pesquisa
	 */
	private void inserirFiltros(DetachedCriteria criteria, final FatExclusaoCritica fatExclusaoCriticafiltro, boolean ordenado) {
		if(StringUtils.isNotBlank(fatExclusaoCriticafiltro.getCodigo())) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CODIGO.toString(), fatExclusaoCriticafiltro.getCodigo()));
		}
		if(fatExclusaoCriticafiltro.getCbo() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CBO.toString(), fatExclusaoCriticafiltro.getCbo()));
		}
		if(fatExclusaoCriticafiltro.getCns() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CNS.toString(), fatExclusaoCriticafiltro.getCns()));
		}
		if(fatExclusaoCriticafiltro.getQtd() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.QTD.toString(), fatExclusaoCriticafiltro.getQtd()));
		}
		inserirFiltrosIdade(criteria, fatExclusaoCriticafiltro);
		if(fatExclusaoCriticafiltro.getPermMenor() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.PERM_MENOR.toString(), fatExclusaoCriticafiltro.getPermMenor()));
		}
		if(fatExclusaoCriticafiltro.getTelefone() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.TELEFONE.toString(), fatExclusaoCriticafiltro.getTelefone()));
		}
		if(fatExclusaoCriticafiltro.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.IND_SITUACAO.toString(), fatExclusaoCriticafiltro.getIndSituacao()));
		}
		inserirOrdenacao(criteria, ordenado);
	}

	/**
	 * Ordenacao da pesquisa
	 * @param criteria
	 * @param ordenado
	 */
	private void inserirOrdenacao(DetachedCriteria criteria, boolean ordenado) {
		if(ordenado){
			criteria.addOrder(Order.asc(FatExclusaoCritica.Fields.CODIGO.toString()));
		}
	}

	/**
	 * Filtro de idade
	 * @param criteria
	 * @param fatExclusaoCriticafiltro
	 */
	private void inserirFiltrosIdade(DetachedCriteria criteria,
			final FatExclusaoCritica fatExclusaoCriticafiltro) {
		if(fatExclusaoCriticafiltro.getIdMaior() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.ID_MAIOR.toString(), fatExclusaoCriticafiltro.getIdMaior()));
		}
		if(fatExclusaoCriticafiltro.getIdMenor() != null) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.ID_MENOR.toString(), fatExclusaoCriticafiltro.getIdMenor()));
		}
	}
	
	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para pesquisar codigos duplicados
	 */
	public FatExclusaoCritica pesquisarCodigoExclusaoCriticaDuplicado(FatExclusaoCritica fatExclusaoCritica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExclusaoCritica.class);
		if (StringUtils.isNotBlank(fatExclusaoCritica.getCodigo())) {
			criteria.add(Restrictions.eq(FatExclusaoCritica.Fields.CODIGO.toString(), fatExclusaoCritica.getCodigo()));
		} else {
			return null;
		}
		return (FatExclusaoCritica) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #2153
	 * @author marcelo.deus
	 * @param fatExclusaoCritica
	 * metodo para retornar uma exclusao critica unica
	 */
	public FatExclusaoCritica obterFatExclusaoCritica(FatExclusaoCritica fatExclusaoCritica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExclusaoCritica.class);
		inserirFiltros(criteria, fatExclusaoCritica, false);
		List<FatExclusaoCritica> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}
	}
}
