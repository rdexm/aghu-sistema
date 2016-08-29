package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;

public class AipSinonimosOcupacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipSinonimosOcupacao> {
	
	private static final long serialVersionUID = -1573727539821389410L;

	public List<AipSinonimosOcupacao> pesquisarSinonimosPorOcupacao(AipOcupacoes ocupacao){
		DetachedCriteria criteria = obterCriteriaSinonimosPorOcupacao(ocupacao);
		return executeCriteria(criteria);
	}
	
	public List<AipSinonimosOcupacao> pesquisarSinonimosExcetoPelaOcupacao(AipOcupacoes ocupacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSinonimosOcupacao.class);
		if (ocupacao.getCodigo() != null){
			criteria.add(Restrictions.ne(AipSinonimosOcupacao.Fields.OCP_CODIGO.toString(), ocupacao.getCodigo()));			
		}
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaSinonimosPorOcupacao(AipOcupacoes ocupacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSinonimosOcupacao.class);
		criteria.add(Restrictions.eq(AipSinonimosOcupacao.Fields.OCP_CODIGO.toString(), ocupacao.getCodigo()));
		return criteria;
	}

	public Short obterMaxCodigoAipSinonimosOcupacao(AipOcupacoes ocupacao){
		DetachedCriteria criteria = obterCriteriaSinonimosPorOcupacao(ocupacao);
		
		criteria.setProjection(Projections.max(AipSinonimosOcupacao.Fields.SEQUENCIAL.toString()));
		
		Short result = (Short) executeCriteriaUniqueResult(criteria);
		
		if(result != null){
			return result;
		} else {
			return Short.valueOf("0");
		}
	}
}
