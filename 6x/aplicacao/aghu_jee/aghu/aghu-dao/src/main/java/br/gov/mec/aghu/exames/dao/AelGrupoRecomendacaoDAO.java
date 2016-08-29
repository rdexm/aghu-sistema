package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AelGrupoRecomendacao;

public class AelGrupoRecomendacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoRecomendacao> {
	
	private static final long serialVersionUID = 959962713356588565L;
	private static final String STR_AND = " and ";
	private static final String STR_OBJECT = " o.";
//	private static final String STR_OBJECT_PROJECTION = ", " + STR_OBJECT;
	
	

	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. NAO deve ser usado nos metodos merge, persist, etc do entitymanager.
	 * TODO Objeto populado parcialmente, setar os valores necessarios por demanda.<br>
	 * 
	 * @param id
	 * @return
	 */
	/*public AelGrupoRecomendacao obterOriginal(AelGrupoRecomendacao entity) {
		if (entity == null || entity.getSeq() == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelGrupoRecomendacao.Fields.SEQ.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.DESCRICAO.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.INDSITUACAO.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.CRIADOEM.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.SERVIDOR.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.RESPONSAVEL.toString());
		hql.append(STR_OBJECT_PROJECTION).append(AelGrupoRecomendacao.Fields.ABRANGENCIA.toString());
		hql.append(" from ").append(AelGrupoRecomendacao.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelGrupoRecomendacao.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", entity.getSeq());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		AelGrupoRecomendacao returnValue = null; 
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AelGrupoRecomendacao();
			for (Object[] listFileds : lista) {
				returnValue.setSeq( (Integer) listFileds[0]);
				returnValue.setDescricao( (String) listFileds[1]);
				returnValue.setIndSituacao( (DominioSituacao) listFileds[2]);
				returnValue.setCriadoEm( (Date) listFileds[3]);
				returnValue.setServidor( (RapServidores) listFileds[4]);
				returnValue.setResponsavel( (DominioResponsavelGrupoRecomendacao) listFileds[5]);
				returnValue.setAbrangencia( (DominioAbrangenciaGrupoRecomendacao) listFileds[6]);
			}
		}		
		
		return returnValue;
	}*/

	public List<AelGrupoRecomendacao> pesquisaGrupoRecomendacaoPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelGrupoRecomendacao grupoRecomendacao) {
		StringBuilder hql = new StringBuilder(50);
		hql.append(" select o ");
		hql.append(" from ");
		hql.append(AelGrupoRecomendacao.class.getSimpleName()).append(" o ");

		Query query = this.getQueryPesquisaPaginada(hql, grupoRecomendacao, true, orderProperty, asc);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		@SuppressWarnings("unchecked")
		List<AelGrupoRecomendacao> list = query.getResultList();
		
		List<AelGrupoRecomendacao> voList = new ArrayList<AelGrupoRecomendacao>(list.size());
		for (AelGrupoRecomendacao e : list) {
			voList.add(e);
		}
		
		return voList;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private Query getQueryPesquisaPaginada(StringBuilder hql,
			AelGrupoRecomendacao grupoRecomendacao, boolean isOrder,
			String orderProperty, boolean asc) {
		
		if (grupoRecomendacao != null) {
			boolean buff = false;
			if (grupoRecomendacao.getSeq() != null 
					|| StringUtils.isNotBlank(grupoRecomendacao.getDescricao())
					|| grupoRecomendacao.getResponsavel() != null
					|| grupoRecomendacao.getAbrangencia() != null
					|| grupoRecomendacao.getIndSituacao() != null
			) {
				hql.append(" where ");
	
				if (grupoRecomendacao.getSeq() != null) {
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.SEQ).append(" = :pSeq ");
					buff = true;
				}
				if (StringUtils.isNotBlank(grupoRecomendacao.getDescricao())) {
					if (buff) {
						hql.append(STR_AND);  
					}
					hql.append(" ( ");  
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.DESCRICAO).append(" like :pDesc1 ");
					hql.append(" or ");  
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.DESCRICAO).append(" like :pDesc2 ");
					hql.append(" ) ");  
					buff = true;
				}
				if (grupoRecomendacao.getResponsavel() != null) {
					if (buff) {
						hql.append(STR_AND);  
					}
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.RESPONSAVEL).append(" like :pResp ");
					buff = true;					
				}
				if (grupoRecomendacao.getAbrangencia() != null) {
					if (buff) {
						hql.append(STR_AND);  
					}
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.ABRANGENCIA).append(" like :pAbrangencia ");
					buff = true;					
				}
				if (grupoRecomendacao.getIndSituacao() != null) {
					if (buff) {
						hql.append(STR_AND);  
					}
					hql.append(STR_OBJECT).append(AelGrupoRecomendacao.Fields.INDSITUACAO).append(" like :pIndSituacao ");
					buff = true;					
				}
			}
		}
		
		if (isOrder) {
			hql.append(" order by o.");
			if ( StringUtils.isNotBlank(orderProperty) ) {
				hql.append(orderProperty);				
			} else {
				hql.append("descricao");
			}
			
			if (asc) {
				hql.append(" asc ");
			} else {
				hql.append(" desc ");				
			}
		}
		
		Query query = this.createQuery(hql.toString());
		
		if (grupoRecomendacao != null) {
			if (grupoRecomendacao.getSeq() != null) {
				query.setParameter("pSeq", grupoRecomendacao.getSeq());
			}
			if (StringUtils.isNotBlank(grupoRecomendacao.getDescricao())) {
				query.setParameter("pDesc1", "%".concat(grupoRecomendacao.getDescricao().toUpperCase()).concat("%"));
				query.setParameter("pDesc2", "%".concat(grupoRecomendacao.getDescricao()).concat("%"));
			}
			if (grupoRecomendacao.getResponsavel() != null) {
				query.setParameter("pResp", grupoRecomendacao.getResponsavel());
			}
			if (grupoRecomendacao.getAbrangencia() != null) {
				query.setParameter("pAbrangencia", grupoRecomendacao.getAbrangencia());
			}
			if (grupoRecomendacao.getIndSituacao() != null) {
				query.setParameter("pIndSituacao", grupoRecomendacao.getIndSituacao());
			}
		}
		
		return query;
	}

	public Long pesquisaGrupoRecomendacaoPaginadaCount(AelGrupoRecomendacao grupoRecomendacao) {
		StringBuilder hql = new StringBuilder(50);
		
		hql.append(" select ");
		hql.append(" count (*) ");
		hql.append(" from ");
		hql.append(AelGrupoRecomendacao.class.getSimpleName()).append(" o ");

		Query query = this.getQueryPesquisaPaginada(hql, grupoRecomendacao, false, null, false);
		
		return Long.valueOf(query.getSingleResult().toString());
	}
	
	
	

}
