package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MciTopografiaInfeccaoDAO extends BaseDao<MciTopografiaInfeccao> {

	private static final long serialVersionUID = 1337132820788725653L;

	public Long listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(String descricao, DominioSituacao situacao) {
		MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder queryBuilder = new MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder();
		return executeCriteriaCount(queryBuilder.build(Boolean.TRUE, null, descricao, situacao));
	}

	public List<TopografiaInfeccaoVO> listarMciTopografiaInfeccaoPorDescricaoESituacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao, DominioSituacao situacao) {
		MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder queryBuilder = new MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder();
		return executeCriteria(queryBuilder.build(Boolean.FALSE, null, descricao, situacao), firstResult, maxResult, orderProperty, asc);
	}

	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(String strPesquisa, DominioSituacao situacao) {
		MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder queryBuilder = new MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder();
		return executeCriteria(queryBuilder.build(Boolean.FALSE, null, strPesquisa, situacao), 0, 100, MciTopografiaInfeccao.Fields.DESCRICAO.toString(), true);
	}

	public DetachedCriteria obterCriteriaTopografiaInfeccaoPorSeqSituacao(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciTopografiaInfeccao.class);
		String strParametro = (String) parametro;
		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro)) {
			criteria.add(Restrictions.eq(MciTopografiaInfeccao.Fields.SEQ.toString(), Short.valueOf(strParametro)));
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(MciTopografiaInfeccao.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MciTopografiaInfeccao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<MciTopografiaInfeccao> pesquisarTopografiaInfeccaoPatologiaInfeccao(Object parametro) {
		return this.executeCriteria(obterCriteriaTopografiaInfeccaoPorSeqSituacao(parametro));
	}

	public Long pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(Object parametro) {
		return this.executeCriteriaCount(obterCriteriaTopografiaInfeccaoPorSeqSituacao(parametro));
	}

}
