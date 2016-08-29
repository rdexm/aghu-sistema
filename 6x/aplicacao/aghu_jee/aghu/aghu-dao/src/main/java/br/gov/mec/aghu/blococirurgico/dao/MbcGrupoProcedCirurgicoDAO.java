package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.ProcedimentoCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcGrupoProcedCirurgicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcGrupoProcedCirurgico> {
	
	private static final long serialVersionUID = 729483442524708319L;

	public List<MbcGrupoProcedCirurgico> pesquisarMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico grupoProcedCirurgico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoProcedCirurgico.class);
		
		if (grupoProcedCirurgico.getSeq() != null) {
			criteria.add(Restrictions.eq(MbcGrupoProcedCirurgico.Fields.SEQ.toString(), grupoProcedCirurgico.getSeq()));
		}
		
		if (StringUtils.isNotEmpty(grupoProcedCirurgico.getDescricao())) {
			criteria.add(Restrictions.ilike(MbcGrupoProcedCirurgico.Fields.DESCRICAO.toString(), grupoProcedCirurgico.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (grupoProcedCirurgico.getSituacao() != null) {
			criteria.add(Restrictions.eq(MbcGrupoProcedCirurgico.Fields.SITUACAO.toString(), grupoProcedCirurgico.getSituacao()));
		}
		
		criteria.addOrder(Order.asc(MbcGrupoProcedCirurgico.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaProcCirurgicosPorGrupo(Object filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoProcedCirurgico.class, "mgp");
		criteria.createAlias("mgp." + MbcGrupoProcedCirurgico.Fields.MBC_PROCEDIMENTO_POR_GRUPOES.toString(), "mpg", JoinType.INNER_JOIN);
		criteria.createAlias("mpg." + MbcProcedimentoPorGrupo.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "mpc", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("mpc." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentoCirurgicoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("mpc." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()), ProcedimentoCirurgicoVO.Fields.CODIGO.toString())
				.add(Projections.property("mgp." + MbcGrupoProcedCirurgico.Fields.DESCRICAO.toString()), ProcedimentoCirurgicoVO.Fields.DESCRICAO_GRUPO.toString()));
		
		if(filtro != null && StringUtils.isNotBlank(filtro.toString())){
			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.eq("mpc." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), Integer.valueOf(filtro.toString())));
			} else {
				criteria.add(Restrictions.ilike("mpc." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), (String)filtro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq("mpc." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoCirurgicoVO.class));
		
		return criteria;
	}
	
	public List<ProcedimentoCirurgicoVO> listarProcCirurgicosPorGrupo(String filtro) {
		DetachedCriteria criteria = montarCriteriaProcCirurgicosPorGrupo(filtro);
		criteria.addOrder(Order.asc("mpc." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarProcCirurgicosPorGrupoCount(Object filtro) {
		return executeCriteriaCount(montarCriteriaProcCirurgicosPorGrupo(filtro));
	}
}