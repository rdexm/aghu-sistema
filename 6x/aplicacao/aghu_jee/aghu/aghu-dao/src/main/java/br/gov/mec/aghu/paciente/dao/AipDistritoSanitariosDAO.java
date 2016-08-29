package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipDistritoSanitariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipDistritoSanitarios>{
	
	private static final long serialVersionUID = 5447419453177880756L;

	public List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorCodigos(List<Short> ids){
		DetachedCriteria cri = DetachedCriteria.forClass(AipDistritoSanitarios.class);
		cri.add(Restrictions.in(AipDistritoSanitarios.Fields.CODIGO.toString(), ids));
		cri.addOrder(Order.asc(AipDistritoSanitarios.Fields.DESCRICAO.toString()));
		
		List<AipDistritoSanitarios> li = executeCriteria(cri);
		
		return li;
	}
	
	/**
	 * Busca distritos sanitários para paginação
	 * 
	 * @return Lista de distritos sanitários
	 */	
	//@Restrict("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public List<AipDistritoSanitarios> pesquisarDistritoSanitario(
			Integer firstResult, Integer maxResult, Short codigo, String descricao) {
		return executeCriteria(createCriteriaAipDistritoSanitarios(codigo, descricao, 
				true), firstResult, maxResult, null, false);
	}	
	
	/**
	 * Busca quantidade de registros de distritos sanitários encontrados
	 * para o filtro aplicado
	 * 
	 * @return numero de distritos encontrados
	 */	
	public Long obterDistritoSanitarioCount(Short codigo, String descricao) {
		return executeCriteriaCount(createCriteriaAipDistritoSanitarios(codigo,
				descricao, false));
	}
	
	/**
	 * Método de criação da pesquisa
	 * 
	 * @return DetachedCriteria
	 */	
	private DetachedCriteria createCriteriaAipDistritoSanitarios(Short codigo,
			String descricao, boolean order) {
		
		String aliasAds = "ads";
		String aliasAci = "aci";
		String aliasAuf = "auf";
		String ponto = ".";
		
		DetachedCriteria cri = DetachedCriteria
				.forClass(AipDistritoSanitarios.class, aliasAds);
		
		cri.createAlias(aliasAds + ponto + AipDistritoSanitarios.Fields.CIDADES.toString(), 
				aliasAci, JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(aliasAci + ponto + AipCidades.Fields.UF.toString(), aliasAuf, JoinType.LEFT_OUTER_JOIN);
		
		if (codigo != null) {
			cri.add(Restrictions.eq(
					AipDistritoSanitarios.Fields.CODIGO.toString(), codigo
							.shortValue()));
		}

		if (descricao != null && !descricao.trim().equals("")) {
			cri.add(Restrictions.ilike(AipDistritoSanitarios.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));

		}
		if (order) {
			cri.addOrder(Order.asc(AipDistritoSanitarios.Fields.DESCRICAO.toString()));
		}

		cri.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return cri;
	}
	
	public List<AipDistritoSanitarios> pesquisarDistritoSanitarioPorCidadeCodigo(Integer cddCodigo) {
		String aliasAds = "ads";
		String aliasAci = "aci";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipDistritoSanitarios.class, aliasAds);
		
		criteria.createAlias(aliasAds + ponto + AipDistritoSanitarios.Fields.CIDADES.toString(), 
				aliasAci, JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(aliasAci + ponto + AipCidades.Fields.CODIGO.toString(), cddCodigo));
		
		criteria.addOrder(Order.asc(AipDistritoSanitarios.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca distritos sanitários por código e descrição
	 * 
	 * @return Lista de distritos sanitários
	 */
	//@Restrict("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public List<AipDistritoSanitarios> pesquisarPorCodigoDescricao(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroShort(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipDistritoSanitarios.class);

			_criteria.add(Restrictions.eq(
					AipDistritoSanitarios.Fields.CODIGO.toString(),
					Short.valueOf(strPesquisa)));
			
			List<AipDistritoSanitarios> list = executeCriteria(_criteria);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipDistritoSanitarios.class);
				
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipDistritoSanitarios.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(AipDistritoSanitarios.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria);
	}

}
