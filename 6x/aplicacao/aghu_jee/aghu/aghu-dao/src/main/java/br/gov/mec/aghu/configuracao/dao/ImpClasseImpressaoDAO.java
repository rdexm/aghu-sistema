package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;

public class ImpClasseImpressaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpClasseImpressao> {

	private static final long serialVersionUID = 6503989705482446840L;

	/**
	 * Pesquisa count.
	 * 
	 * @param classeImpressao
	 * @param tipoImpressora
	 * @param descricao
	 * @param tipoCups
	 * @return
	 */
	public Long pesquisarClasseImpressaoCount(String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {
		return executeCriteriaCount(criarCriteriaPesquisaClasseImpressao(
				classeImpressao, tipoImpressora, descricao, tipoCups));
	}

	/**
	 * Pesquisa.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param classeImpressao
	 * @param tipoImpressora
	 * @param descricao
	 * @param tipoCups
	 * @return
	 */
	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {

		return executeCriteria(
				criarCriteriaPesquisaClasseImpressao(classeImpressao,
						tipoImpressora, descricao, tipoCups), firstResult,
				maxResult, orderProperty, asc);
	}

	/**
	 * Verifica se Classe de Impressão já existe.
	 * 
	 * @param IdClasseImpressao
	 * @param classeImpressao
	 * @return
	 */
	public boolean isClasseImpressaoExistente(Integer IdClasseImpressao,
			String classeImpressao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpClasseImpressao.class);

		criteria.add(Restrictions.eq(
				ImpClasseImpressao.Fields.CLASSEIMPRESSAO.toString(),
				classeImpressao));

		ImpClasseImpressao objImpClasseImpressao = (ImpClasseImpressao) executeCriteriaUniqueResult(criteria);

		// validacao para alteracao
		if (objImpClasseImpressao != null && IdClasseImpressao != null) {
			if (objImpClasseImpressao.getId().equals(IdClasseImpressao)) {
				return false;
			}
			return true;
		}
		return objImpClasseImpressao == null ? false : true;
	}

	/**
	 * Cria criteria para pesquisa de classe de impressao.
	 * 
	 * @param String
	 *            classeImpressao,DominioTipoImpressoraCups tipoImpressora,
	 *            String descricao, DominioTipoCups tipoCups
	 * @return Um DetachedCriteria pronto pra pesquisa.
	 */
	private DetachedCriteria criarCriteriaPesquisaClasseImpressao(
			String classeImpressao, DominioTipoImpressoraCups tipoImpressora,
			String descricao, DominioTipoCups tipoCups) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpClasseImpressao.class);

		if (!StringUtils.isBlank(classeImpressao)) {
			criteria.add(Restrictions.like("classeImpressao", classeImpressao,
					MatchMode.ANYWHERE));
		}

		if (tipoImpressora != null) {
			criteria.add(Restrictions.eq("tipoImpressora", tipoImpressora));
		}

		if (!StringUtils.isBlank(descricao)) {
			criteria.add(Restrictions.like("descricao", descricao,
					MatchMode.ANYWHERE));
		}

		if (tipoCups != null) {
			criteria.add(Restrictions.eq("tipoCups", tipoCups));
		}
		return criteria;
	}

	public List<ImpClasseImpressao> pesquisarClasseImpressaoPorDescricao(
			Object paramPesquisa) {
		String valor = paramPesquisa.toString();

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpClasseImpressao.class);

		if (StringUtils.isNotBlank(valor)) {

			criteria.add(Restrictions.eq(
					ImpClasseImpressao.Fields.CLASSEIMPRESSAO.toString(),
					valor.toUpperCase()));
		}
		
		criteria.addOrder(Order.asc(ImpClasseImpressao.Fields.CLASSEIMPRESSAO.toString()));

		List<ImpClasseImpressao> li = executeCriteria(criteria);

		return li;
	}
	
}
