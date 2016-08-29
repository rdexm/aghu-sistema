package br.gov.mec.aghu.controleinfeccao.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.FatorPredisponenteVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MciFatorPredisponentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciFatorPredisponentes> {

    private static final long serialVersionUID = -3382902716342106970L;


    public MciFatorPredisponentes obterFatorPredisponentesPorSeq(Short seq) {
        DetachedCriteria criteria = DetachedCriteria.forClass(MciFatorPredisponentes.class);
        criteria.createAlias(MciFatorPredisponentes.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
        criteria.createAlias(MciFatorPredisponentes.Fields.SERVIDOR_MOVIMENTADO.toString(), "SMOV", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(MciFatorPredisponentes.Fields.SEQ.toString(), seq));
        return (MciFatorPredisponentes) this.executeCriteriaUniqueResult(criteria);
    }

	public MciFatorPredisponentes obterFatorPredisponentesPorPeso(BigDecimal valorPeso) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciFatorPredisponentes.class);
		criteria.add(Restrictions.le(MciFatorPredisponentes.Fields.PESO_INICIAL.toString(), valorPeso));
		criteria.add(Restrictions.ge(MciFatorPredisponentes.Fields.PESO_FINAL.toString(), valorPeso));
		return (MciFatorPredisponentes) this.executeCriteriaUniqueResult(criteria);
	}

	public DetachedCriteria montarCriteriaFatorPredisponentes(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciFatorPredisponentes.class);
        criteria.createAlias(MciFatorPredisponentes.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
        criteria.createAlias(MciFatorPredisponentes.Fields.SERVIDOR_MOVIMENTADO.toString(), "SMOV", JoinType.INNER_JOIN);

		if(strPesquisa != null && !strPesquisa.isEmpty()){
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(MciFatorPredisponentes.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			}else {
				criteria.add(Restrictions.ilike(MciFatorPredisponentes.Fields.DESCRICAO.toString(), strPesquisa , MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MciFatorPredisponentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public Long obterFatorPredisponentesPorCodigoDescricaoCount(String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaFatorPredisponentes(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	public List<MciFatorPredisponentes> obterFatorPredisponentesPorCodigoDescricao(String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaFatorPredisponentes(strPesquisa);
		return this.executeCriteria(criteria,0,100,MciFatorPredisponentes.Fields.DESCRICAO.toString(),true);
	}
	
	public List<FatorPredisponenteVO> pesquisarFatorPredisponente(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaFatorPredisponente(codigo, descricao, situacao);
		criteria.addOrder(Order.asc(MciFatorPredisponentes.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	private DetachedCriteria montarCriteriaFatorPredisponente(Short codigo,
			String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciFatorPredisponentes.class, "fpd");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MciFatorPredisponentes.Fields.SEQ.toString()), FatorPredisponenteVO.Fields.SEQ.toString())
				.add(Projections.property(MciFatorPredisponentes.Fields.DESCRICAO.toString()), FatorPredisponenteVO.Fields.DESCRICAO.toString())
				.add(Projections.property(MciFatorPredisponentes.Fields.GRAU_RISCO.toString()), FatorPredisponenteVO.Fields.GRAU_RISCO.toString())
				.add(Projections.property(MciFatorPredisponentes.Fields.SITUACAO.toString()), FatorPredisponenteVO.Fields.SITUACAO.toString())
				.add(Projections.property(MciFatorPredisponentes.Fields.PESO_INICIAL.toString()), FatorPredisponenteVO.Fields.PESO_INICIAL.toString())
				.add(Projections.property(MciFatorPredisponentes.Fields.PESO_FINAL.toString()), FatorPredisponenteVO.Fields.PESO_FINAL.toString())
				);
		if (codigo != null) {
			criteria.add(Restrictions.eq(MciFatorPredisponentes.Fields.SEQ.toString(), codigo));
		}
		if (descricao != null && StringUtils.isNotEmpty(descricao)) {
			criteria.add(Restrictions.ilike(MciFatorPredisponentes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(MciFatorPredisponentes.Fields.SITUACAO.toString(), situacao));
		}
    	criteria.setResultTransformer(Transformers.aliasToBean(FatorPredisponenteVO.class));
		return criteria;
	}

	public Long pesquisarFatorPredisponenteCount(Short codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaFatorPredisponente(codigo, descricao, situacao);
		return this.executeCriteriaCount(criteria);
	}
}
