package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatProcedTratamento;
import br.gov.mec.aghu.model.FatTipoTratamentos;

public class FatProcedTratamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedTratamento>{
	
	private static final long serialVersionUID = 7344320288302188396L;

	public List<FatProcedTratamento> listarProcedTratamentoPorPhiSeqEDescricao(Integer phiSeq,
			String... descricao){

		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedTratamento.class);
		criteria.createAlias(FatProcedTratamento.Fields.TIPO_TRATAMENTO.toString(), FatProcedTratamento.Fields.TIPO_TRATAMENTO.toString());
		criteria.add(Restrictions.eq(FatProcedTratamento.Fields.PHI_SEQ.toString(),	phiSeq));
		criteria.add(Restrictions.in(FatProcedTratamento.Fields.TIPO_TRATAMENTO.toString() + "." + 
				FatTipoTratamentos.Fields.DESCRICAO.toString(),	descricao));
		return executeCriteria(criteria);
	}	
}
