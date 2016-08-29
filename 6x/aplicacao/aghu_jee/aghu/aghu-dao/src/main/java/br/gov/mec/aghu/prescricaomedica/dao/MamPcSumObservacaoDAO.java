package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.model.MamPcSumObservacao;


public class MamPcSumObservacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcSumObservacao> {
	
	private static final long serialVersionUID = -3813506461676076780L;

	public MamPcSumObservacao obterSumarioObservacao(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido) {
		
		MamPcSumObservacao suo = null;
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());

		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.PIR_ATD_SEQ.toString(), atdSeq));
		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.PIR_DTHR_CRIACAO.toString(), dthrCriacao));
		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.PAC_CODIGO.toString(), pacCodigo));
		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.PERTENCE_SUMARIO.toString(), pertenceSumario));
		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.DTHR_EVENTO.toString(), dthrEvento));
		dc.add(Restrictions.eq(MamPcSumObservacao.Fields.RECEM_NASCIDO.toString(), recemNascido));
		dc.add(Restrictions.isNotNull(MamPcSumObservacao.Fields.CODIGO_MENSAGEM.toString()));
		
		Object obj = executeCriteriaUniqueResult(dc);
		
		if (obj != null) {
			suo = (MamPcSumObservacao) obj;
		}
		
		return suo;
	}

}
