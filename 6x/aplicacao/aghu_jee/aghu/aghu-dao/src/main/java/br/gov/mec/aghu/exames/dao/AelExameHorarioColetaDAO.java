package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;

public class AelExameHorarioColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameHorarioColeta> {

	private static final long serialVersionUID = -4847540376973700011L;

	public List<AelExameHorarioColeta> pesquisarExameHorarioColetaPorSolicitacao(final Integer solicitacaoExameSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameHorarioColeta.class, "ehc");
		criteria.createAlias(AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString(), AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString());
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		subCriteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString());
		subCriteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString());
		subCriteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), solicitacaoExameSeq));
		subCriteria.add(Restrictions.eqProperty("ehc."+AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString()+"."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), 
				"ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString()+"."+AelExames.Fields.SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty("ehc."+AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString()+"."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), 
				"ise."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString()+"."+AelMateriaisAnalises.Fields.SEQ.toString()));
		
		
		subCriteria.setProjection(
				Projections.projectionList().add(Projections.property(AelItemSolicitacaoExames.Fields.SEQP.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())));
		criteria.add(Subqueries.exists(subCriteria));
		return this.executeCriteria(criteria);
	}
	
	public List<AelExameHorarioColeta> pesquisarExameHorarioColeta(String sigla, Integer matExame) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameHorarioColeta.class, "ehc");
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.MATERIAL.toString(), matExame));
		criteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria);
	}
	

}