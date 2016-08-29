package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelCadGuicheDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCadGuiche> {

	private static final long serialVersionUID = -2714728293349837780L;

	public AelCadGuiche obterAelCadGuiche(final Short seq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCadGuiche.class);
		criteria.add(Restrictions.eq(AelCadGuiche.Fields.SEQ.toString(), seq));
		criteria.createAlias(AelCadGuiche.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		return (AelCadGuiche) executeCriteriaUniqueResult(criteria);
	}

	public List<AelIdentificarGuicheVO> pesquisarAelCadGuiche(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AelCadGuiche aelCadGuiche) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(aelCadGuiche);
		
		criteria.createAlias(AelCadGuiche.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.ALA.toString(), "ALA", JoinType.LEFT_OUTER_JOIN);
		

		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(AelCadGuiche.Fields.SEQ.toString()), AelCadGuiche.Fields.SEQ.toString())
		.add(Projections.property(AelCadGuiche.Fields.DESCRICAO.toString()), AelCadGuiche.Fields.DESCRICAO.toString())
		.add(Projections.property(AelCadGuiche.Fields.UNIDADE_FUNCIONAL.toString()), AelCadGuiche.Fields.UNIDADE_FUNCIONAL.toString())
		.add(Projections.property(AelCadGuiche.Fields.IND_SITUACAO.toString()), AelCadGuiche.Fields.IND_SITUACAO.toString())
		.add(Projections.property(AelCadGuiche.Fields.OCUPADO.toString()), AelCadGuiche.Fields.OCUPADO.toString())
		.add(Projections.property(AelCadGuiche.Fields.USUARIO.toString()), AelCadGuiche.Fields.USUARIO.toString())
		.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "nmUnidade");

		criteria.setProjection(projection);	
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelIdentificarGuicheVO.class));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	
	public AelCadGuiche obterAelCadGuichePorUsuarioUnidadeExecutora(final AghUnidadesFuncionais unidade, final String usuario, final DominioSituacao situacao, Short seqGuiche){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCadGuiche.class);

		criteria.add(Restrictions.eq(AelCadGuiche.Fields.UNIDADE_FUNCIONAL.toString(), unidade));
		criteria.add(Restrictions.eq(AelCadGuiche.Fields.USUARIO.toString(), usuario));
		criteria.add(Restrictions.eq(AelCadGuiche.Fields.IND_SITUACAO.toString(), situacao));
		if(seqGuiche != null) {
			criteria.add(Restrictions.ne(AelCadGuiche.Fields.SEQ.toString(), seqGuiche));
		}
		
		return (AelCadGuiche) executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarAelCadGuicheCount(final AelCadGuiche aelCadGuiche) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(aelCadGuiche);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(final AelCadGuiche aelCadGuiche) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCadGuiche.class, "AEL");

		if (aelCadGuiche != null) {
			if (aelCadGuiche.getSeq() != null) {
				criteria.add(Restrictions.eq("AEL." + AelCadGuiche.Fields.SEQ.toString(), aelCadGuiche.getSeq()));
			}

			if (StringUtils.isNotBlank(aelCadGuiche.getDescricao())) {
				criteria.add(Restrictions.ilike("AEL." + AelCadGuiche.Fields.DESCRICAO.toString(), aelCadGuiche.getDescricao(), MatchMode.ANYWHERE));
			}

			if (aelCadGuiche.getIndSituacao() != null) {
				criteria.add(Restrictions.eq("AEL." + AelCadGuiche.Fields.IND_SITUACAO.toString(), aelCadGuiche.getIndSituacao()));
			}

			if (aelCadGuiche.getOcupado() != null) {
				criteria.add(Restrictions.eq("AEL." + AelCadGuiche.Fields.OCUPADO.toString(), aelCadGuiche.getOcupado()));
			}

			if (aelCadGuiche.getUnidadeFuncional() != null) {
				criteria.add(Restrictions.eq("AEL." + AelCadGuiche.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), aelCadGuiche.getUnidadeFuncional().getSeq()));
			}

			if (aelCadGuiche.getCriadoEm() != null) {
				criteria.add(Restrictions.eq("AEL." + AelCadGuiche.Fields.CRIADO_EM.toString(), aelCadGuiche.getCriadoEm()));
			}
		}

		return criteria;
	}
}
