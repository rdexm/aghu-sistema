package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class FsoFontesRecursoFinancDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoFontesRecursoFinanc> {

	private static final String FRF = "FRF.";
	private static final long serialVersionUID = 6056056689527506541L;

	public List<FsoFontesRecursoFinanc> listaPesquisaFontesRecursoFinanc(
			Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc,
			FsoFontesRecursoFinanc fontesRecursoFinanc) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(fontesRecursoFinanc);
		
		criteria.addOrder(Order.asc(FRF + FsoFontesRecursoFinanc.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);

	}	

	public Long countPesquisaFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(fontesRecursoFinanc);

		return executeCriteriaCount(criteria);
	}

	
	private DetachedCriteria obterCriteriaBasica(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FsoFontesRecursoFinanc.class, "FRF");
		

		if (fontesRecursoFinanc.getCodigo() != null) {
			criteria.add(Restrictions.eq(FRF +
					FsoFontesRecursoFinanc.Fields.CODIGO.toString(),
					fontesRecursoFinanc.getCodigo()));
		}

		if (StringUtils.isNotBlank(fontesRecursoFinanc.getDescricao())) {
			criteria.add(Restrictions.ilike(FRF + FsoFontesRecursoFinanc.Fields.DESCRICAO
					.toString(), fontesRecursoFinanc.getDescricao().trim(),
					MatchMode.ANYWHERE));
		}

		if (fontesRecursoFinanc.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(FRF +
					FsoFontesRecursoFinanc.Fields.IND_SITUACAO.toString(),
					fontesRecursoFinanc.getIndSituacao()));
		}
		
		return criteria;
	}
	
	public List<FsoFontesRecursoFinanc> pesquisarFonteRecursoPorCodigoOuDescricao(Object parametro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FsoFontesRecursoFinanc.class, "FRF");

		String strPesquisa = (String) parametro;
		if (CoreUtil.isNumeroLong(strPesquisa)) {
			criteria.add(Restrictions.eq(FRF + FsoFontesRecursoFinanc.Fields.CODIGO.toString(), Long.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(FRF + FsoFontesRecursoFinanc.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(FRF + FsoFontesRecursoFinanc.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}
	
	public Boolean verificarFonteRecursoFinancUsadaEmVerbaGestao(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		return this.pesquisarFonteRecursoFinancUsadaVerbaGestao(fontesRecursoFinanc).size() > 0;
	}
	
	public Boolean verificarFonteRecursoDuplicada(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoFontesRecursoFinanc.class, "FRF");
	
		if (fontesRecursoFinanc != null) {
			criteria.add(Restrictions.eq(FRF + 
					FsoFontesRecursoFinanc.Fields.CODIGO.toString(),
					fontesRecursoFinanc.getCodigo()));
			
			return (executeCriteriaCount(criteria) > 0);
		}
		
		return false;
	}
	
	public List<FsoFontesXVerbaGestao> pesquisarFonteRecursoFinancUsadaVerbaGestao(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FsoFontesXVerbaGestao.class);
	
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		if (fontesRecursoFinanc != null) {
			criteria.add(Restrictions.eq(FsoFontesXVerbaGestao.Fields.RECURSO.toString(), fontesRecursoFinanc));
		}
		
		return executeCriteria(criteria,0,10,null);
	}
	
}
