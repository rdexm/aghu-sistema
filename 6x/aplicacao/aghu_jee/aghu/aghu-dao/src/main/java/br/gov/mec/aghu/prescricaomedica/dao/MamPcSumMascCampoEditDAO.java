package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamPcSumMascCampoEdit;


public class MamPcSumMascCampoEditDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcSumMascCampoEdit> {
	
	private static final long serialVersionUID = 5287105875754340198L;

	public MamPcSumMascCampoEdit obterSumarioMascCampoEdit(Integer atdSeq, Date dthrCriacao, Integer nroLinha, Integer ordemRelatorio) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());

		dc.add(Restrictions.eq(MamPcSumMascCampoEdit.Fields.PIR_ATD_SEQ.toString(), atdSeq));
		dc.add(Restrictions.eq(MamPcSumMascCampoEdit.Fields.PIR_DTHR_CRIACAO.toString(), dthrCriacao));
		dc.add(Restrictions.eq(MamPcSumMascCampoEdit.Fields.NRO_LINHA.toString(), nroLinha));
		dc.add(Restrictions.eq(MamPcSumMascCampoEdit.Fields.ORDEM_RELATORIO.toString(), ordemRelatorio));

		dc.addOrder(Order.asc(MamPcSumMascCampoEdit.Fields.ORDEM_RELATORIO.toString()));
		dc.addOrder(Order.asc(MamPcSumMascCampoEdit.Fields.NRO_LINHA.toString()));
		dc.addOrder(Order.asc(MamPcSumMascCampoEdit.Fields.NRO_CAMPO_EDIT.toString()));
		
		return (MamPcSumMascCampoEdit) executeCriteriaUniqueResult(dc);
	}
}
