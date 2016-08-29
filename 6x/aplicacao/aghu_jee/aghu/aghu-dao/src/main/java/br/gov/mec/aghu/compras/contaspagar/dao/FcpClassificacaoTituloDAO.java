package br.gov.mec.aghu.compras.contaspagar.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpClassificacaoTituloDAO extends BaseDao<FcpClassificacaoTitulo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3786119736074086659L;

	/**
	 * #46298 - SB1 - Lista de Classificação do Titulo
	 * @param parametro
	 * @return
	 */
	public List<FcpClassificacaoTitulo> obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpClassificacaoTitulo.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(FcpClassificacaoTitulo.Fields.CODIGO.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(FcpClassificacaoTitulo.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FcpClassificacaoTitulo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, FcpClassificacaoTitulo.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #46298 - SB1 - Count de Classificação do Titulo
	 * @param parametro
	 * @return
	 */
	public Long obterCountClassificacaoTituloAtivosPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpClassificacaoTitulo.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(FcpClassificacaoTitulo.Fields.CODIGO.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(FcpClassificacaoTitulo.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FcpClassificacaoTitulo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
}
