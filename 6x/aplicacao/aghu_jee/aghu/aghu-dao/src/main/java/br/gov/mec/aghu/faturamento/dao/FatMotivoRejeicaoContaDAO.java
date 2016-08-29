package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatMotivoRejeicaoContaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoRejeicaoConta> {

	private static final long serialVersionUID = 6923362962092975918L;

	public List<FatMotivoRejeicaoConta> pesquisarMotivosRejeicaoConta(String filtro, DominioSituacao situacao, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaPesquisarMotivosRejeicaoConta(filtro, situacao);
		
		criteria.addOrder(Order.asc(FatMotivoRejeicaoConta.Fields.SEQ.toString()));
		
		return executeCriteria(criteria); //executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarMotivosRejeicaoContaCount(String filtro, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaPesquisarMotivosRejeicaoConta(filtro, situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarMotivosRejeicaoConta(String filtro, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoRejeicaoConta.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion criteriaDescricao = Restrictions.ilike(FatMotivoRejeicaoConta.Fields.DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(FatMotivoRejeicaoConta.Fields.SEQ.toString(), Short.valueOf(filtro)),
						criteriaDescricao));
			} else {
				criteria.add(criteriaDescricao);
			}
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(FatMotivoRejeicaoConta.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	public List<FatMotivoRejeicaoConta> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FatMotivoRejeicaoConta filtro) {
		
		DetachedCriteria criteria = criarPesquisaCriteria(filtro);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(FatMotivoRejeicaoConta.Fields.SEQ.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarCount(FatMotivoRejeicaoConta filtro) {
		
		return executeCriteriaCount(criarPesquisaCriteria(filtro));
	}

	private DetachedCriteria criarPesquisaCriteria(FatMotivoRejeicaoConta filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoRejeicaoConta.class);
		
		if (filtro != null) {
			
			if (filtro.getSeq() != null) {
				criteria.add(Restrictions.eq(FatMotivoRejeicaoConta.Fields.SEQ.toString(), filtro.getSeq()));
			}
			
			if (filtro.getCodigoSus() != null) {
				criteria.add(Restrictions.eq(FatMotivoRejeicaoConta.Fields.CODIGO_SUS.toString(), filtro.getCodigoSus()));
			}
			
			if (StringUtils.isNotBlank(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike(FatMotivoRejeicaoConta.Fields.DESCRICAO.toString(), 
						this.replaceCaracterEspecial(filtro.getDescricao()), MatchMode.ANYWHERE));
			}
			
			if (filtro.getSituacao() != null) {
				criteria.add(Restrictions.eq(FatMotivoRejeicaoConta.Fields.SITUACAO.toString(), filtro.getSituacao()));
			}
		}

		return criteria;
	}

	/**
	 * Formata as ocorrencias de "%" e "_" para que as mesmas sejam submetidas à pesquisa.
	 * 
	 * @param descricao {@link String}
	 * @return {@link String}
	 */
	private String replaceCaracterEspecial(String descricao) {
		
		return descricao.replace("_", "\\_").replace("%", "\\%");
	}
}
