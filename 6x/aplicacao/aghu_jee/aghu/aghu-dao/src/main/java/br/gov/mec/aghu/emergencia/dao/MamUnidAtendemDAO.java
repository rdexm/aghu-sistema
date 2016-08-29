package br.gov.mec.aghu.emergencia.dao;



import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidAtendemDAO extends BaseDao<MamUnidAtendem> {

	private static final long serialVersionUID = -6979290576544243609L;
	
	public List<MamUnidAtendem> pesquisarUnidadesFuncionaisEmergencia(
			Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			Short unidadeFuncionalSeq, 
			String descricao,
			DominioSituacao indSituacao)
			
	{
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendem.class, "MamUnidAtend");
		criteria.createAlias(MamUnidAtendem.Fields.MAM_PROT_CLASSIF_RISCO.toString(), "mamProtClassifRisco",JoinType.LEFT_OUTER_JOIN);

		if (unidadeFuncionalSeq != null) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.UNF_SEQ.toString(), unidadeFuncionalSeq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MamUnidAtendem.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), indSituacao));
			
		}
			

		criteria.addOrder(Order.asc(MamUnidAtendem.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}	
	
	
	public Long pesquisarUnidadesFuncionaisEmergenciaCount(		
			Short unidadeFuncionalSeq, 
			String descricao,
			DominioSituacao indSituacao)
			
	{
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendem.class, "MamUnidAtend");
		criteria.createAlias(MamUnidAtendem.Fields.MAM_PROT_CLASSIF_RISCO.toString(), "mamProtClassifRisco",JoinType.LEFT_OUTER_JOIN);

		if (unidadeFuncionalSeq != null) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.UNF_SEQ.toString(), unidadeFuncionalSeq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MamUnidAtendem.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), indSituacao));
			
		}
			
		
		return this.executeCriteriaCount(criteria);
	}
	
	public MamUnidAtendem pesquisarUnidadeFuncionalAtivaPorUnfSeq(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendem.class);
		
		criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MamUnidAtendem.Fields.UNF_SEQ.toString(), unfSeq));
		
		return (MamUnidAtendem) executeCriteriaUniqueResult(criteria);
	}
	
	public List<Short> pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(boolean isTriagem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendem.class);
		
		criteria.setProjection(Projections.property(MamUnidAtendem.Fields.UNF_SEQ.toString()));
		
		criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (isTriagem) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_TRIAGEM.toString(), Boolean.TRUE));
			
		} else {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_RECEPCAO.toString(), Boolean.TRUE));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<MamUnidAtendem> listarUnidadesFuncionais(final Object parametro, boolean isRecepcao, String orderBy, boolean somenteAtivas) {
		final DetachedCriteria criteria = montarCriteriaMamUnidadesFuncionais(parametro);
		
		if (isRecepcao) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_RECEPCAO.toString(), Boolean.TRUE));
			
		} else {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_TRIAGEM.toString(), Boolean.TRUE));
		}
		if (somenteAtivas) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		if (orderBy.equalsIgnoreCase("unfSeq")) {
			criteria.addOrder(Order.asc(MamUnidAtendem.Fields.UNF_SEQ.toString()));
		} else if (orderBy.equalsIgnoreCase("descricao")) {
			criteria.addOrder(Order.asc(MamUnidAtendem.Fields.DESCRICAO.toString()));
		}
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarUnidadesFuncionaisCount(final Object parametro, boolean isRecepcao, boolean somenteAtivas) {
		final DetachedCriteria criteria = montarCriteriaMamUnidadesFuncionais(parametro);
		
		if (isRecepcao) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_RECEPCAO.toString(), Boolean.TRUE));
			
		} else {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_TRIAGEM.toString(), Boolean.TRUE));
		}
		if (somenteAtivas) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		return executeCriteriaCount(criteria);
	}


	private DetachedCriteria montarCriteriaMamUnidadesFuncionais(final Object parametro) {
		final String srtPesquisa = (String) parametro;
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendem.class);
		if (CoreUtil.isNumeroShort(srtPesquisa)) {
			criteria.add(Restrictions.eq(MamUnidAtendem.Fields.UNF_SEQ.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(StringUtils.trim(srtPesquisa))) {
			criteria.add(Restrictions.ilike(MamUnidAtendem.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		return criteria;
	}
}