package br.gov.mec.aghu.blococirurgico.dao;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaImplantacao;
import br.gov.mec.aghu.model.MbcAgendaImplantacaoId;


public class MbcAgendaImplantacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaImplantacao> {

	private static final long serialVersionUID = 3191623341939539319L;
	
	public Long obterAgendaImplantacaoPorId(MbcAgendaImplantacaoId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaImplantacao.class);
		
		criteria.add(Restrictions.eq(MbcAgendaImplantacao.Fields.PUC_IND_FUNCAO_PROF.toString(), id.getPucIndFuncaoProf()));
		criteria.add(Restrictions.eq(MbcAgendaImplantacao.Fields.PUC_SER_MATRICULA.toString(), id.getPucSerMatricula()));
		criteria.add(Restrictions.eq(MbcAgendaImplantacao.Fields.PUC_SER_VIN_CODIGO.toString(), id.getPucSerVinCodigo()));
		criteria.add(Restrictions.eq(MbcAgendaImplantacao.Fields.PUC_UNF_SEQ.toString(), id.getPucUnfSeq()));
		
		return executeCriteriaCount(criteria);
	}
}