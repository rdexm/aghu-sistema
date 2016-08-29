package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RarAssociaPrograma;
import br.gov.mec.aghu.model.RarPrograma;
import br.gov.mec.aghu.model.RarProgramaDuracao;

public class RarProgramaDuracaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RarProgramaDuracao> {
	
	private static final long serialVersionUID = -134836975324193585L;

	protected RarProgramaDuracaoDAO() {
	}
	
	public RarProgramaDuracao obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(RarPrograma programa, Date dataInicio, Boolean utilizaSysDate) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "PDU");
		
		dc.createAlias("PDU.".concat(RarProgramaDuracao.Fields.RAR_PROGRAMAS.toString()), "PGA");
		dc.createAlias("PGA.".concat(RarPrograma.Fields.RAR_ASSOCIA_PROGRAMASES_FOR_PGA_SUPERIOR.toString()), "ASP");
		
		dc.add(Restrictions.eq("ASP.".concat(RarAssociaPrograma.Fields.RAR_PROGRAMAS_BY_PGA_AREA_ATUACAO.toString()), programa));
		
		Criterion restricao1 = Restrictions.le("PDU.".concat(RarProgramaDuracao.Fields.DT_INICIO.toString()), dataInicio);
		Criterion restricao2 = Restrictions.ge("PDU.".concat(RarProgramaDuracao.Fields.DT_FIM.toString()), dataInicio);
		Criterion restricao3 = Restrictions.and(restricao1, restricao2);
		
		Criterion restricao4;
		
		if (utilizaSysDate) {
			restricao4 = Restrictions.ge("PDU.".concat(RarProgramaDuracao.Fields.DT_FIM.toString()), new Date());
		} else {
			restricao4 = Restrictions.isNull("PDU.".concat(RarProgramaDuracao.Fields.DT_FIM.toString()));			
		}
		
		Criterion restricao5 = Restrictions.and(restricao1, restricao4);

		dc.add(Restrictions.or(restricao3, restricao5));
		
		return (RarProgramaDuracao) executeCriteriaUniqueResult(dc);
	}
}