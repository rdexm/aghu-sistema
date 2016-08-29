package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.search.Lucene;

public class AipTipoLogradourosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipTipoLogradouros> {
    @Inject
    private Lucene lucene;

	
	private static final long serialVersionUID = -4154034853600220700L;


	public List<AipTipoLogradouros> pesquisarTipoLogradouro(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipTipoLogradouros.class);

			_criteria.add(Restrictions.eq(
					AipTipoLogradouros.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			
			List<AipTipoLogradouros> list = executeCriteria(_criteria);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipTipoLogradouros.class);
				
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipTipoLogradouros.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(AipTipoLogradouros.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AipTipoLogradouros> pesquisarTipoLogradouro(
			Integer firstResult, Integer maxResult, String abreviatura,
			String descricao) {
		return executeCriteria(createCriteriaAipTipoLogradouros(abreviatura,
				descricao, true), firstResult, maxResult, null, true);
	}
	

	public Long obterTipoLogradouroCount(String abreviatura, String descricao) {
		return executeCriteriaCount(createCriteriaAipTipoLogradouros(
				abreviatura, descricao, false));
	}

	private DetachedCriteria createCriteriaAipTipoLogradouros(
			String abreviatura, String descricao, boolean order) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AipTipoLogradouros.class);

		if (abreviatura != null && !abreviatura.trim().equals("")) {
			cri.add(Restrictions.ilike("abreviatura", abreviatura,
					MatchMode.ANYWHERE));
		}

		if (descricao != null && !descricao.trim().equals("")) {
			cri.add(Restrictions.ilike("descricao", descricao,
					MatchMode.ANYWHERE));
		}
		if (order) {
			cri.addOrder(Order.asc("descricao"));
		}
		return cri;
	}
	
	public List<AipTipoLogradouros> getTiposLogradouroComMesmaAbreviatura(AipTipoLogradouros tipoLogradouro) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AipTipoLogradouros.class);
		
		criteria.add(Restrictions.ilike(
				AipTipoLogradouros.Fields.ABREVIATURA.toString(),
				tipoLogradouro.getAbreviatura(), MatchMode.EXACT));
		
		// Garante que não será comparado a descrição da ocupação sendo editada
		if (tipoLogradouro.getCodigo() != null) {
			criteria.add(Restrictions.ne(AipTipoLogradouros.Fields.CODIGO
					.toString(), tipoLogradouro.getCodigo()));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public List<AipTipoLogradouros> getTiposLogradouroComMesmaDescricao(AipTipoLogradouros tipoLogradouro) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipTipoLogradouros.class);

		criteria.add(Restrictions.ilike(
				AipTipoLogradouros.Fields.DESCRICAO.toString(),
				tipoLogradouro.getDescricao(), MatchMode.EXACT));

		// Garante que não será comparado a descrição da ocupação sendo editada
		if (tipoLogradouro.getCodigo() != null) {
			criteria.add(Restrictions.ne(AipTipoLogradouros.Fields.CODIGO
					.toString(), tipoLogradouro.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}
	

	public List<AipTipoLogradouros> pesquisarTiposLogradouro(String campoAnalisado, String campoFonetico, String valor, Class<AipTipoLogradouros> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, sortFields);
	}
	
}
