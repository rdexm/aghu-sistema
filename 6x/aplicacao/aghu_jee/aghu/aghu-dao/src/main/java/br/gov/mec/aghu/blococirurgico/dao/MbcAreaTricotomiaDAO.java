package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcAreaTricotomiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAreaTricotomia> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3144497357459621315L;
	

	public List<MbcAreaTricotomia> pesquisarAreaTricotomia(Short filtroSeq, String filtroDescricao, DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAreaTricotomia.class);
		
		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(MbcAreaTricotomia.Fields.SEQ.toString(), filtroSeq));
		}
		
		if (StringUtils.isNotEmpty(filtroDescricao)) {
			criteria.add(Restrictions.ilike(MbcAreaTricotomia.Fields.DESCRICAO.toString(), filtroDescricao, MatchMode.ANYWHERE));
		}
		
		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(MbcAreaTricotomia.Fields.SITUACAO.toString(), filtroSituacao));
		}
		
		criteria.addOrder(Order.asc(MbcAreaTricotomia.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	private void pesquisarPorSeqOuDescricao(final Object parametro,
			final DetachedCriteria criteria) {

		final String srtPesquisa = (String) parametro;

		if (CoreUtil.isNumeroShort(parametro)) {

			criteria.add(Restrictions.eq(MbcAreaTricotomia.Fields.SEQ.toString(),
					Short.valueOf(srtPesquisa)));

		} else if (StringUtils.isNotEmpty(srtPesquisa)) {
			criteria.add(Restrictions.ilike(MbcAreaTricotomia.Fields.DESCRICAO.toString(),
					srtPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(MbcAreaTricotomia.Fields.SITUACAO.toString(),
				DominioSituacao.A));
	}

	public List<MbcAreaTricotomia> pesquisarPorSeqOuDescricao(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAreaTricotomia.class);
		pesquisarPorSeqOuDescricao(parametro, criteria); 
		criteria.addOrder(Order.asc(MbcAreaTricotomia.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria);
	}
	
}
