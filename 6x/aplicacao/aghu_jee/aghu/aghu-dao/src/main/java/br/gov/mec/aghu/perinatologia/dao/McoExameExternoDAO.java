package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoExameExternoDAO extends BaseDao<McoExameExterno> {
	private static final long serialVersionUID = 875525466722641212L;

	public List<McoExameExterno> pesquisarListarExamesExternos(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, String nome,
			DominioSituacao situacao) {

		DetachedCriteria criteria = montarCriteriaPesquisarExamesExternos(nome, situacao);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public McoExameExterno pesquisarExamesExternos(String nome) {

		DetachedCriteria criteria = montarCriteriaPesquisarExamesExternos(nome);
		List<McoExameExterno> lista = executeCriteria(criteria, 0, 1, null, false);
		
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		
		return null;
	}
	
	private DetachedCriteria montarCriteriaPesquisarExamesExternos(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameExterno.class);

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.eq(McoExameExterno.Fields.DESCRICAO.toString(), nome));
		}

		return criteria;
	}

	public Long pesquisarIndicacoesNascimentoCount(String nome, DominioSituacao situacao) {

		DetachedCriteria criteria = montarCriteriaPesquisarExamesExternos(nome, situacao);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaPesquisarExamesExternos(String nome, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameExterno.class);

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(McoExameExterno.Fields.DESCRICAO.toString(), nome, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(McoExameExterno.Fields.IND_SITUACAO.toString(), situacao));
		}

		return criteria;
	}
	
	/**
	 * Parte de C5 de #25644
	 * 
	 * @param descricao
	 * @return
	 */
	public List<McoExameExterno> pesquisarMcoExameExternoAtivosPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameExterno.class);
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(McoExameExterno.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(McoExameExterno.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return super.executeCriteria(criteria);
	}

}
