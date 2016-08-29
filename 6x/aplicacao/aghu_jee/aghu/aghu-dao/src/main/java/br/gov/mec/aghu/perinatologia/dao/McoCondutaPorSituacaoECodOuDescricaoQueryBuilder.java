package br.gov.mec.aghu.perinatologia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class McoCondutaPorSituacaoECodOuDescricaoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 879270275657324458L;
	private static final String PONTO = ".";
	private static final String ALIAS_COND = "MCO_CONDUTAS";
	
	private DetachedCriteria criteria;
	private String strPesquisa;
	private DominioSituacao indSituacao;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(McoConduta.class , ALIAS_COND);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
	}

	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_COND + PONTO + McoConduta.Fields.IND_SITUACAO.toString(), indSituacao));
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(ALIAS_COND + PONTO + McoConduta.Fields.COD.toString(), Integer.valueOf(strPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(ALIAS_COND + PONTO + McoConduta.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
	}

	public DetachedCriteria build(String strPesquisa, DominioSituacao indSituacao) {
		this.strPesquisa = strPesquisa;
		this.indSituacao = indSituacao;
		return super.build();
	}

}
