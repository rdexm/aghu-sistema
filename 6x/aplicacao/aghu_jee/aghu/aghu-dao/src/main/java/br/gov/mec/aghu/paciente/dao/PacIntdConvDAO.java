package br.gov.mec.aghu.paciente.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PacIntdConv;

public class PacIntdConvDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PacIntdConv> {

	private static final long serialVersionUID = 2548256208734977877L;

	public List<PacIntdConv> obterListaPacIntdConvPorProntuario(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PacIntdConv.class);
		criteria.add(Restrictions.eq(PacIntdConv.Fields.COD_PRNT.toString(), prontuario));
		return this.executeCriteria(criteria);
	}

	public List<PacIntdConv> pesquisarPorCodPrntDataInt(Integer codPrnt, Date dataInt) {
		DetachedCriteria cri = DetachedCriteria.forClass(PacIntdConv.class);
		cri.add(Restrictions.eq(PacIntdConv.Fields.COD_PRNT.toString(), codPrnt));
		cri.add(Restrictions.eq(PacIntdConv.Fields.DATA_INT.toString(), dataInt));
		return this.executeCriteria(cri);
	}

	public List<PacIntdConv> obterListaPacIntdConvPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PacIntdConv.class);
		criteria.add(Restrictions.eq(PacIntdConv.Fields.ATD_SEQ.toString(), atdSeq));
		return this.executeCriteria(criteria);
	}
}
