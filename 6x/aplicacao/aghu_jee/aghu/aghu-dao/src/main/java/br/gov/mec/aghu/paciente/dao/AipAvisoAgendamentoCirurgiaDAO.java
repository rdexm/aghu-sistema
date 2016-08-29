package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipAvisoAgendamentoCirurgia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;

public class AipAvisoAgendamentoCirurgiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipAvisoAgendamentoCirurgia> {

	private static final long serialVersionUID = -6407233068053889518L;
	
	public List<ProntuarioCirurgiaVO> pesquisarDesarquivamentoProntuariosCirurgia(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = obterCriteriaPesquisarDesarquivamentoProntuariosCirurgia();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("AAC." + AipAvisoAgendamentoCirurgia.Fields.SEQ.toString()),
						ProntuarioCirurgiaVO.Fields.SEQ.toString())
				.add(Projections.property("AAC." + AipAvisoAgendamentoCirurgia.Fields.MBC_CIRURGIAS_SEQ.toString()),
						ProntuarioCirurgiaVO.Fields.CRG_SEQ.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()),
						ProntuarioCirurgiaVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()),
						ProntuarioCirurgiaVO.Fields.PACIENTE.toString())
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_PREV_INICIO.toString()),
						ProntuarioCirurgiaVO.Fields.DT_HR_PREVISAO_INICIO.toString())
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),
						ProntuarioCirurgiaVO.Fields.SOLICITANTE.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProntuarioCirurgiaVO.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long obterCountPesquisaDesarquivamentoProntuariosCirurgia() {
		DetachedCriteria criteria = obterCriteriaPesquisarDesarquivamentoProntuariosCirurgia();
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarDesarquivamentoProntuariosCirurgia() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAvisoAgendamentoCirurgia.class, "AAC");
		criteria.createAlias("AAC." + AipAvisoAgendamentoCirurgia.Fields.MBC_CIRURGIAS.toString(), "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("AAC." + AipAvisoAgendamentoCirurgia.Fields.STATUS.toString(), "0"));
		
		return criteria;
	}

}
