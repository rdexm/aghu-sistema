package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AghTiposUnidadeFuncionalDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghTiposUnidadeFuncional> {
	

	private static final long serialVersionUID = -9149131537210164161L;
	
	private static final Log LOG = LogFactory.getLog(AghTiposUnidadeFuncionalDAO.class);

	/**
	 * 
	 * @dbtables AghTiposUnidadeFuncional select
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao) {
		DetachedCriteria criteria = createPesquisaTiposUnidadeFuncionalCriteria(
				codigo, descricao);

		criteria.addOrder(Order.asc(AghTiposUnidadeFuncional.Fields.DESCRICAO
				.toString()));
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			String descricao) {
		DetachedCriteria criteria = createPesquisaTiposUnidadeFuncionalCriteria(descricao);

		criteria.addOrder(Order.asc(AghTiposUnidadeFuncional.Fields.DESCRICAO
				.toString()));
		return executeCriteria(criteria);
	}

	@SuppressWarnings({ "unchecked" })
	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(String strPesquisa) {
		try {
			DetachedCriteria cri = null;

			LinkedHashSet<AghTiposUnidadeFuncional> li = new LinkedHashSet<AghTiposUnidadeFuncional>();

			if (StringUtils.isNotBlank(strPesquisa)
					&& CoreUtil.isNumeroInteger(strPesquisa)) {
				cri = criarCriteriaUF();

				cri.add(Restrictions.eq(
						AghTiposUnidadeFuncional.Fields.CODIGO.toString(),
						Integer.valueOf(strPesquisa)));

				List<AghTiposUnidadeFuncional> objList = executeCriteria(cri,
						0, 25, null, false);
				li.addAll(objList);

				if (li.size() > 0) {
					return new ArrayList(li);
				}
			}

			cri = criarCriteriaUF();
			if (StringUtils.isNotBlank(strPesquisa)) {
				cri.add(Restrictions.ilike(
						AghTiposUnidadeFuncional.Fields.CODIGO.toString(),
						strPesquisa, MatchMode.ANYWHERE));
			}

			List<AghTiposUnidadeFuncional> objList = executeCriteria(cri, 0,
					25, null, false);
			li.addAll(objList);

			return new ArrayList(li);

		} catch (Exception e) {
			LOG.error("Erro ao buscar unidades Funcionais", e.getCause());
			return new ArrayList<AghTiposUnidadeFuncional>(0);
		}

	}

	private DetachedCriteria criarCriteriaUF() {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AghTiposUnidadeFuncional.class);

		cri.addOrder(Order.asc(AghTiposUnidadeFuncional.Fields.DESCRICAO
				.toString()));

		return cri;
	}

	public Long pesquisaTiposUnidadeFuncionalCount(Integer codigo,
			String descricao) {
		return executeCriteriaCount(createPesquisaTiposUnidadeFuncionalCriteria(
				codigo, descricao));
	}

	private DetachedCriteria createPesquisaTiposUnidadeFuncionalCriteria(
			Integer codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghTiposUnidadeFuncional.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AghTiposUnidadeFuncional.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AghTiposUnidadeFuncional.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	private DetachedCriteria createPesquisaTiposUnidadeFuncionalCriteria(
			String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghTiposUnidadeFuncional.class);

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AghTiposUnidadeFuncional.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * @dbtables AghTiposUnidadeFuncional select
	 * @param aghTiposUnidadeFuncionalCodigo
	 * @return
	 */
	public AghTiposUnidadeFuncional obterTiposUnidadeFuncional(Integer aghTiposUnidadeFuncionalCodigo) {
		AghTiposUnidadeFuncional retorno = super.obterPorChavePrimaria(aghTiposUnidadeFuncionalCodigo);
		return retorno;
	}
	
	public boolean validarTipoUnidadeFuncionalComUnidadeFuncional(
			AghTiposUnidadeFuncional tiposUnidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghUnidadesFuncionais.class);

		criteria.add(Restrictions.eq("tiposUnidadeFuncional",
				tiposUnidadeFuncional));

		List<AghUnidadesFuncionais> li = executeCriteria(criteria);
		if (li == null || li.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

}
