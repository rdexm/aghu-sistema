package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;

/**
 * @modulo sicon.cadastrosbasicos
 * @author cvagheti
 *
 */
public class ScoServicoSiconDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoServicoSicon> {

	private static final long serialVersionUID = 292674094499407067L;

	public Long validarCodigoSiconServicoUnico(Integer seq,
			Integer codigoSicon, ScoServico servico) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoServicoSicon.class);

		if (seq != null) {
			criteria.add(Restrictions.ne(ScoServicoSicon.Fields.SEQ.toString(),
					seq));
		}

		if (codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoServicoSicon.Fields.CODIGO_SICON.toString(), codigoSicon));
		}

		if (servico != null) {
			criteria.add(Restrictions.eq(
					ScoServicoSicon.Fields.SERVICO.toString(), servico));
		}

		criteria.add(Restrictions.eq(
				ScoServicoSicon.Fields.SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteriaCount(criteria);
	}

	public Long pesquisarServicoSiconCount(Integer codigoSicon,
			ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {

		DetachedCriteria criteria = this.pesquisarCriteria(codigoSicon,
				servico, situacao, grupoServico);
		return this.executeCriteriaCount(criteria);
	}

	public List<ScoServicoSicon> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoSicon, ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {

		DetachedCriteria criteria = this.pesquisarCriteria(codigoSicon, servico, situacao, grupoServico);
		
		criteria.addOrder(Order.asc(ScoServicoSicon.Fields.SERVICO.toString()+"." + ScoServico.Fields.NOME.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	public ScoServicoSicon pesquisarServicoSicon(Integer codigoSicon,
			ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {
		
		DetachedCriteria criteria = this.pesquisarCriteria(codigoSicon, servico, situacao, grupoServico);
		
		return (ScoServicoSicon) this.executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria pesquisarCriteria(Integer codigoSicon,
			ScoServico servico, DominioSituacao situacao,
			ScoGrupoServico grupoServico) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoServicoSicon.class);
		
		criteria.createAlias(ScoServicoSicon.Fields.SERVICO.toString(), ScoServicoSicon.Fields.SERVICO.toString());

		if (codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoServicoSicon.Fields.CODIGO_SICON.toString(), codigoSicon));
		}

		if (servico != null) {
			criteria.add(Restrictions.eq(
					ScoServicoSicon.Fields.SERVICO.toString(), servico));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					ScoServicoSicon.Fields.SITUACAO.toString(), situacao));
		}

		if (grupoServico != null) {
			criteria.add(Restrictions.eq(ScoServicoSicon.Fields.SERVICO.toString() + "." + ScoServico.Fields.GRUPO_SERVICO.toString(), grupoServico));
		}

		return criteria;
	}

	
	public List<ScoServicoSicon> obterPorCodigoServico(ScoServico servico) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoServicoSicon.class);

		criteria.add(Restrictions.eq(ScoServicoSicon.Fields.SERVICO.toString(),
				servico));

		return executeCriteria(criteria);
	}
	
	public ScoServicoSicon obterServicoCodigoSicon(Integer codSicon) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoServicoSicon.class);

		criteria.add(Restrictions.eq(ScoServicoSicon.Fields.CODIGO_SICON.toString(), codSicon));

		List<ScoServicoSicon> result = executeCriteria(criteria);
		
		if(result != null &&
		   result.size() > 0){
			return result.get(0);
		}
		
		return null;
	}
	

}
