package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MamPcSumExameMasc;


public class MamPcSumExameMascDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPcSumExameMasc> {
	
	private static final long serialVersionUID = -3278186119023813247L;

	public List<MamPcSumExameMasc> pesquisarSumarioExameMasc(Integer atdSeq, Date dthrCriacao, Integer pacCodigo, Boolean recemNascido) {
		//query principal
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "SEM");
		
		//criação de subquerys
		DetachedCriteria subExames = DetachedCriteria.forClass(AelExames.class, "EXA");
		DetachedCriteria subMateriaisAnalise = DetachedCriteria.forClass(AelMateriaisAnalises.class, "MAN");
		DetachedCriteria subUnidadesFuncionais = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");

		//restrict da query principal
		dc.add(Restrictions.eq("SEM.".concat(MamPcSumExameMasc.Fields.PAC_CODIGO.toString()), pacCodigo));
		dc.add(Restrictions.eq("SEM.".concat(MamPcSumExameMasc.Fields.PIR_ATD_SEQ.toString()), atdSeq));
		dc.add(Restrictions.eq("SEM.".concat(MamPcSumExameMasc.Fields.PIR_DTHR_CRIACAO.toString()), dthrCriacao));
		dc.add(Restrictions.eq("SEM.".concat(MamPcSumExameMasc.Fields.RECEM_NASCIDO.toString()), recemNascido));
		
		//restricts das subquerys
		subExames.add(Restrictions.eq("EXA.".concat(AelExames.Fields.IND_SUMARIO_PARADA.toString()), Boolean.TRUE));
		subExames.add(Restrictions.eqProperty("EXA.".concat(AelExames.Fields.SIGLA.toString()), "SEM.".concat(MamPcSumExameMasc.Fields.UFE_EMA_EXA_SIGLA.toString())));
		subMateriaisAnalise.add(Restrictions.eqProperty("MAN.".concat(AelMateriaisAnalises.Fields.SEQ.toString()), "SEM.".concat(MamPcSumExameMasc.Fields.UFE_EMA_MAN_SEQ.toString())));
		subUnidadesFuncionais.add(Restrictions.eqProperty("UNF.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), "SEM.".concat(MamPcSumExameMasc.Fields.UFE_UNF_SEQ.toString())));
		
		//projections das subquerys
		subExames.setProjection(Projections.property("EXA.".concat(AelExames.Fields.SIGLA.toString())));
		subMateriaisAnalise.setProjection(Projections.property("MAN.".concat(AelMateriaisAnalises.Fields.SEQ.toString())));
		subUnidadesFuncionais.setProjection(Projections.property("UNF.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())));
		
		// inner join ael_exames com mam_pc_sum_exame_masc
		dc.add(Subqueries.exists(subExames));
		// inner join ael_materiais_analises com mam_pc_sum_exame_masc
		dc.add(Subqueries.exists(subMateriaisAnalise));
		// inner join com agh_unidades_funcionais com mam_pc_sum_exame_masc
		dc.add(Subqueries.exists(subUnidadesFuncionais));
		
		//order by
		dc.addOrder(Order.asc(MamPcSumExameMasc.Fields.ORDEM_AGRUPAMENTO.toString()));
		dc.addOrder(Order.asc(MamPcSumExameMasc.Fields.ORDEM_RELATORIO.toString()));
		
		return executeCriteria(dc);
	}

}
