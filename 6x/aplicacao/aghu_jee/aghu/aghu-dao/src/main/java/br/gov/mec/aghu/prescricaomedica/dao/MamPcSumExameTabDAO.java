package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.model.MamPcSumExameTab;


public class MamPcSumExameTabDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcSumExameTab> {
	
	private static final long serialVersionUID = -3471675705689754028L;

	public MamPcSumExameTab obterSumarioExameTabela(Integer atdSeq, Date dthrCriacao) {
		
		MamPcSumExameTab sue = null;
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());

		dc.add(Restrictions.isNotNull(MamPcSumExameTab.Fields.PAC_CODIGO.toString()));
		dc.add(Restrictions.eq(MamPcSumExameTab.Fields.PIR_ATD_SEQ.toString(), atdSeq));
		dc.add(Restrictions.eq(MamPcSumExameTab.Fields.PIR_DTHR_CRIACAO.toString(), dthrCriacao));
		
		dc.addOrder(Order.asc(MamPcSumExameTab.Fields.PRONTUARIO.toString()));
		
		List<MamPcSumExameTab> result =  executeCriteria(dc);
		
		if (!result.isEmpty()) {
			sue = result.get(0);
		}
		return sue;
	}
	
	public List<MamPcSumExameTab> pesquisarSumarioExameTabela(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, Boolean recemNascido) {

		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "SUE2");
		
		dc.add(Restrictions.eq("SUE2.".concat(MamPcSumExameTab.Fields.PIR_ATD_SEQ.toString()), atdSeq));
		dc.add(Restrictions.eq("SUE2.".concat(MamPcSumExameTab.Fields.PIR_DTHR_CRIACAO.toString()), dthrCriacao));
		dc.add(Restrictions.eq("SUE2.".concat(MamPcSumExameTab.Fields.PAC_CODIGO.toString()), pacCodigo));
		dc.add(Restrictions.eq("SUE2.".concat(MamPcSumExameTab.Fields.RECEM_NASCIDO.toString()), recemNascido));
		
		DominioSumarioExame[] arrayDominio = new DominioSumarioExame[]{
				DominioSumarioExame.B, DominioSumarioExame.E,
				DominioSumarioExame.G, DominioSumarioExame.H};
		
		dc.add(Restrictions.in("SUE2.".concat(MamPcSumExameTab.Fields.PERTENCE_SUMARIO.toString()), arrayDominio));

		dc.addOrder(Order.asc("SUE2.".concat(MamPcSumExameTab.Fields.PERTENCE_SUMARIO.toString())));
		dc.addOrder(Order.asc("SUE2.".concat(MamPcSumExameTab.Fields.ORDEM.toString())));
		dc.addOrder(Order.asc("SUE2.".concat(MamPcSumExameTab.Fields.DTHR_EVENTO_AREA_EXEC.toString())));
		
		return executeCriteria(dc);
	}

}
