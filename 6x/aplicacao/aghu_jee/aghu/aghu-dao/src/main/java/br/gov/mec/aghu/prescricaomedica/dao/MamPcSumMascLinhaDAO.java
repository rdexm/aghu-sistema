package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamPcSumMascLinha;


public class MamPcSumMascLinhaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcSumMascLinha> {

	private static final long serialVersionUID = -4357981792195929984L;

	public List<MamPcSumMascLinha> pesquisarSumarioMascLinha(Integer atdSeq, Date dthrCriacao, Integer ordemRelatorio) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
		
		dc.add(Restrictions.eq(MamPcSumMascLinha.Fields.PIR_ATD_SEQ.toString(), atdSeq));
		dc.add(Restrictions.eq(MamPcSumMascLinha.Fields.PIR_DTHR_CRIACAO.toString(), dthrCriacao));
		dc.add(Restrictions.eq(MamPcSumMascLinha.Fields.ORDEM_RELATORIO.toString(), ordemRelatorio));
		
		dc.addOrder(Order.asc(MamPcSumMascLinha.Fields.ORDEM_RELATORIO.toString()));		
		dc.addOrder(Order.asc(MamPcSumMascLinha.Fields.NRO_LINHA.toString()));
		
		return executeCriteria(dc);
	}
	
}
