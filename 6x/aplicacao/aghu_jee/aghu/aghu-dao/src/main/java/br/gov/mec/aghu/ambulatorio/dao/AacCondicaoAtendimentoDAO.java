package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;

import br.gov.mec.aghu.dominio.DominioConsultaGenerica;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;

public class AacCondicaoAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacCondicaoAtendimento> {

	private static final long serialVersionUID = -451020070381376232L;

	public List<AacCondicaoAtendimento> listarCondicaoAtendimento() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);
		criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<AacCondicaoAtendimento> pesquisarCondicoesAtendimento(String filtro) {
		DetachedCriteria criteria = createPesquisaCondicoesAtendimentoCriteria(filtro);
		return executeCriteria(criteria, 0, 25, null, false);
	}

	public Long pesquisarCondicoesAtendimentoCount(String filtro) {
		DetachedCriteria criteria = createPesquisaCondicoesAtendimentoCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaCondicoesAtendimentoCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion descricaoCriterion = Restrictions.ilike(AacCondicaoAtendimento.Fields.DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(AacCondicaoAtendimento.Fields.SEQ.toString(), Short.valueOf(filtro)),
						descricaoCriterion));
			} else {
				criteria.add(descricaoCriterion);
			}
		}

		return criteria;
	}
	
	/**
	 * 
	 * @param filtroSeq
	 * @param filtroDescricao
	 * @param filtroSigla
	 * @param filtroGenericaAmb
	 * @param filtroGenericaInternacao
	 * @param filtroCriticaApac
	 * @param filtroSituacaoCondicaoAtendimento
	 * @return
	 */
	public List<AacCondicaoAtendimento> pesquisarCondicaoAtendimentoPaginado(Integer firstResult, Integer maxResult, 
		String orderProperty, boolean asc, Short filtroSeq, String filtroDescricao, String filtroSigla, 
		DominioConsultaGenerica filtroGenericaAmb, DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac, 
		DominioSituacao filtroSituacaoCondicaoAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);
		criteria = this.obterCriteriaPesquisaCondicaoAtendimentoPaginado(filtroSeq, filtroDescricao, filtroSigla, filtroGenericaAmb, filtroGenericaInternacao,
		filtroCriticaApac, filtroSituacaoCondicaoAtendimento, criteria);
		
		criteria.addOrder(Order.asc(AacCondicaoAtendimento.Fields.SEQ.toString()));
				
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
		
	public Long countPesquisarCondicaoAtendimentoPaginado(Short filtroSeq, String filtroDescricao, String filtroSigla, 
		DominioConsultaGenerica filtroGenericaAmb, DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac, 
		DominioSituacao filtroSituacaoCondicaoAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);
			
		criteria = this.obterCriteriaPesquisaCondicaoAtendimentoPaginado(filtroSeq, filtroDescricao, filtroSigla, filtroGenericaAmb, filtroGenericaInternacao,
				filtroCriticaApac, filtroSituacaoCondicaoAtendimento, criteria);
			
		return executeCriteriaCount(criteria);
	}
		
	protected DetachedCriteria obterCriteriaPesquisaCondicaoAtendimentoPaginado(Short filtroSeq, String filtroDescricao, String filtroSigla, 
		DominioConsultaGenerica filtroGenericaAmb, DominioConsultaGenerica filtroGenericaInternacao, Boolean filtroCriticaApac, 
		DominioSituacao filtroSituacaoCondicaoAtendimento, DetachedCriteria criteria) {
			
		if(filtroSeq != null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.SEQ.toString(), filtroSeq));
		}
		if(filtroDescricao != null){
			criteria.add(Restrictions.ilike(AacCondicaoAtendimento.Fields.DESCRICAO.toString(), filtroDescricao, MatchMode.ANYWHERE));
		}
		if(filtroSigla != null){
			criteria.add(Restrictions.ilike(AacCondicaoAtendimento.Fields.SIGLA.toString(), filtroSigla, MatchMode.ANYWHERE));
		}
		if(filtroGenericaAmb != null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.IND_GENERICA_AMB.toString(), filtroGenericaAmb));
		}
		if(filtroGenericaInternacao != null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.IND_GENERICA_INT.toString(), filtroGenericaInternacao));
		}
		if(filtroCriticaApac != null){	
			if(filtroCriticaApac==true) {
				criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.CRITICA_APAC.toString(), filtroCriticaApac));
			}
		}
		if(filtroSituacaoCondicaoAtendimento != null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.SITUACAO.toString(), filtroSituacaoCondicaoAtendimento));
		}
		return criteria;
		
	}
	
	public List<AacCondicaoAtendimento> obterListaCondicaoAtendimento(String parametro) throws ApplicationBusinessException{
		DetachedCriteria criteria = montarCriteriaObterListaCondicaoAtendimento(parametro);
		return executeCriteria(criteria,0,100,AacCondicaoAtendimento.Fields.DESCRICAO.toString(),true);
	}
	public Long obterListaCondicaoAtendimentoCount(String parametro) throws ApplicationBusinessException{
		DetachedCriteria criteria = montarCriteriaObterListaCondicaoAtendimento(parametro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaObterListaCondicaoAtendimento(
			String parametro) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AacCondicaoAtendimento.Fields.SEQ.toString()))
				.add(Projections.property(AacCondicaoAtendimento.Fields.DESCRICAO.toString())));
		
		if(StringUtils.isNotBlank(parametro)){
			if(CoreUtil.isNumeroShort(parametro)){
				criteria.add(Restrictions.or(Restrictions.eq(AacCondicaoAtendimento.Fields.SEQ.toString(), Short.valueOf(parametro)),
						Restrictions.ilike(AacCondicaoAtendimento.Fields.DESCRICAO.toString(), parametro,MatchMode.ANYWHERE)));
			}
			else{
				criteria.add(Restrictions.ilike(AacCondicaoAtendimento.Fields.DESCRICAO.toString(), parametro,MatchMode.ANYWHERE));
			}
		}
		try {
			criteria.setResultTransformer(
					new AliasToBeanConstructorResultTransformer(
							AacCondicaoAtendimento.class.getConstructor(Short.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		return criteria;
	}

}