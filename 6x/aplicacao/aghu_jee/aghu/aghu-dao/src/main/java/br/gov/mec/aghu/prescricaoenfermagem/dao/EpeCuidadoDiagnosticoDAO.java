package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;

public class EpeCuidadoDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCuidadoDiagnostico> {

	private static final long serialVersionUID = 5837118471078019985L;

	/**
	 * 
	 * @param cuidadoSeq
	 * @return
	 */
	public List<EpeCuidadoDiagnostico> obterEpeCuidadoDiagnosticoPorEpeCuidadoSeq(Short cuidadoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoDiagnostico.class);
		criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.CUIDADO.toString() + "." + EpeCuidados.Fields.SEQ.toString(), cuidadoSeq));
		return executeCriteria(criteria);
	}

	// #4960 (Manter diagnósticos x cuidados)
	// C7
	public List<EpeCuidadoDiagnostico> pesquisarCuidadosDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
		criteria.addOrder(Order.asc(EpeCuidadoDiagnostico.Fields.CUI_SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	// #4960 (Manter diagnósticos x cuidados)
	// C7 Count
	public Long pesquisarCuidadosDiagnosticosCount(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = montarCriteriaDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
		return executeCriteriaCount(criteria);
	}

	public DetachedCriteria montarCriteriaDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoDiagnostico.class, "rel");

		criteria.createAlias("rel." + EpeCuidadoDiagnostico.Fields.CUIDADO.toString(), "cui");
		criteria.createAlias("rel." + EpeCuidadoDiagnostico.Fields.FAT_REL_DIAGNOSTICO.toString(), "frd");
		criteria.createAlias("frd." + EpeFatRelDiagnostico.Fields.DIAGNOSTICO.toString(), "dng");
		criteria.createAlias("dng." + EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString(), "snb");
		criteria.createAlias("snb." + EpeSubgrupoNecesBasica.Fields.GRUPO_NECES_BASICA.toString(), "gnb");
		criteria.createAlias("frd." + EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString(), "fre");

		if (dgnSnbGnbSeq != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));
		}
		if (dgnSnbSequencia != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));
		}
		if (dgnSequencia != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SEQUENCIA.toString(), dgnSequencia));
		}
		if (freSeq != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_FRE_SEQ.toString(), freSeq));
		}

		return criteria;
	}

	public DetachedCriteria montarCriteriaDiagnosticosCuidados(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoDiagnostico.class, "rel");

		if (dgnSnbGnbSeq != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));
		}
		if (dgnSnbSequencia != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));
		}
		if (dgnSequencia != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_DGN_SEQUENCIA.toString(), dgnSequencia));
		}
		if (freSeq != null) {
			criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.FDG_FRE_SEQ.toString(), freSeq));
		}

		return criteria;
	}
	
	public List<EpeCuidadoDiagnostico> obterEpeCuidadoDiagnosticoAtivo(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		
		DetachedCriteria criteria = montarCriteriaDiagnosticosCuidados(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
		
		criteria.add(Restrictions.eq(EpeCuidadoDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
		
	}

	public List<EpeCuidadoDiagnostico> obterEpeCuidadoDiagnostico(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		
		DetachedCriteria criteria = montarCriteriaDiagnosticosCuidados(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
		
		return executeCriteria(criteria);
	}
}