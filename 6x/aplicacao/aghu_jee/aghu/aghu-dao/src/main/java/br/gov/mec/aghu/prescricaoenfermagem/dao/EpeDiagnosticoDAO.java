package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class EpeDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeDiagnostico> {
	
	private static final long serialVersionUID = -4919820378314903886L;

	public List<EpeDiagnostico> pesquisarDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String dgnDescricao, Short dgnSequencia) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(dgnSnbGnbSeq,
				dgnSnbSequencia);
		if(StringUtils.isNotEmpty(dgnDescricao)){
			criteria.add(Restrictions.ilike(EpeDiagnostico.Fields.DESCRICAO.toString(), dgnDescricao, MatchMode.ANYWHERE));	
		}
		if(dgnSequencia!=null){
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SEQUENCIA.toString(), dgnSequencia));	
		}
		criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public List<EpeDiagnostico> pesquisarDiagnosticos(String parametro, Short gnbSeq, Short snbSequencia) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		if(gnbSeq!=null){
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_GNB_SEQ.toString(), gnbSeq));	
		}
		if(snbSequencia!=null){
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_SEQUENCIA.toString(), snbSequencia));	
		}
		
		criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaParaSeqOuDescricao(String parametro) {
		String seqOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpeDiagnostico.class);
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				seq = Short.parseShort(seqOuDescricao);
			}			
			if (seq != -1) {
				criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SEQUENCIA.toString(),
						seq));
			} else {
				criteria.add(Restrictions.ilike(
						EpeDiagnostico.Fields.DESCRICAO.toString(),
						seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	// #4957
	// C3
	public List<EpeDiagnostico> pesquisarDiagnosticos(Short gnbSeq, Short snbSequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(gnbSeq, snbSequencia);
		
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SNB_GNB_SEQ.toString()));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SNB_SEQUENCIA.toString()));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SEQUENCIA.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	// #4957
	// C3
	public Long pesquisarDiagnosticosCount(Short gnbSeq, Short snbSequencia) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(gnbSeq, snbSequencia);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaDiagnosticos(Short gnbSeq,
			Short snbSequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeDiagnostico.class, "dng" );
		if(gnbSeq!=null){
			criteria.add(Restrictions.eq("dng." + EpeDiagnostico.Fields.SNB_GNB_SEQ.toString(), gnbSeq));	
		}
		if(snbSequencia!=null){
			criteria.add(Restrictions.eq("dng." + EpeDiagnostico.Fields.SNB_SEQUENCIA.toString(), snbSequencia));	
		}
		return criteria;
	}
	
	// #4957
	// C4
	public Short pesquisarMaxSequenciaDiagnosticos(Short gnbSeq, Short snbSequencia) {
		Short retorno;
		DetachedCriteria criteria = montarCriteriaDiagnosticos(gnbSeq,
				snbSequencia);
		
		criteria.setProjection(Projections.max(EpeDiagnostico.Fields.SEQUENCIA.toString()));
		
		retorno =  (Short) executeCriteriaUniqueResult(criteria);
		if(retorno == null){
			retorno = 0;
		}
		return retorno;
	}
	
	public List<EpeDiagnostico> pesquisarDiagnosticoAtivoPorGnbSeqESnbSequencia(Short gnbSeq, Short snbSequencia){
		DetachedCriteria criteria = montarCriteriaDiagnosticos(gnbSeq, snbSequencia);
		criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C3
	public List<EpeDiagnostico> pesquisarDiagnosticosTodos(String parametro, Short gnbSeq, Short snbSequencia) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		if (gnbSeq != null) {
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_GNB_SEQ.toString(), gnbSeq));
		}
		if (snbSequencia != null) {
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_SEQUENCIA.toString(), snbSequencia));
		}
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SEQUENCIA.toString())).addOrder(Order.asc(EpeDiagnostico.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	// #4960 (Manter diagnósticos x cuidados)
	// C3 Count
	public Long pesquisarDiagnosticosTodosCount(String parametro, Short gnbSeq, Short snbSequencia) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		if (gnbSeq != null) {
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_GNB_SEQ.toString(), gnbSeq));
		}
		if (snbSequencia != null) {
			criteria.add(Restrictions.eq(EpeDiagnostico.Fields.SNB_SEQUENCIA.toString(), snbSequencia));
		}
		return executeCriteriaCount(criteria);
	}

	public List<EpeDiagnostico> pesquisarDiagnosticosPorGrpSgrpDiag(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarDiagnosticosPorGrpSgrpDiag(snbGnbSeq,	snbSequencia, dgnSequencia);
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SNB_GNB_SEQ.toString()));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SNB_SEQUENCIA.toString()));
		criteria.addOrder(Order.asc(EpeDiagnostico.Fields.SEQUENCIA.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}

	private DetachedCriteria montarDiagnosticosPorGrpSgrpDiag(Short snbGnbSeq,
			Short snbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(snbGnbSeq, snbSequencia);
		
		criteria.createAlias("dng." + EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString(), "snb");
		criteria.createAlias("snb." + EpeSubgrupoNecesBasica.Fields.GRUPO_NECES_BASICA.toString(), "gnb");
		
		if(dgnSequencia!=null){
			criteria.add(Restrictions.eq("dng." + EpeDiagnostico.Fields.SEQUENCIA.toString(), dgnSequencia));	
		}
		return criteria;
	}

	public Long pesquisarDiagnosticosPorGrpSgrpDiagCount(Short snbGnbSeq,
			Short snbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = montarDiagnosticosPorGrpSgrpDiag(snbGnbSeq,	snbSequencia, dgnSequencia);

		return executeCriteriaCount(criteria);
	}

}