package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaHistoCad;
import br.gov.mec.aghu.model.AelSismamaHistoRes;

public class AelSismamaHistoResDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaHistoRes> {
	
	private static final long serialVersionUID = 7252153552042261066L;
	
	public AelSismamaHistoRes obterAelSismamaHistoResPorChavePrimaria(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class);		
		criteria.add(Restrictions.eq(AelSismamaHistoRes.Fields.SEQ.toString(), seq));		
		return (AelSismamaHistoRes) executeCriteriaUniqueResult(criteria);
	}

	public Long obterCountSismamaHistoRes(Long numeroAp, Integer lu2Seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class, "s02");
		
		criteria.createAlias("s02." + AelSismamaHistoRes.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("s02." + AelSismamaHistoRes.Fields.SISMAMA_HISTO_CAD.toString(), "s01");


		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AelSismamaHistoRes> pesquisarSismamaHistoResPorSoeSeqSeqpCodigoCampo(
			Integer soeSeq, Short seqp, String codigoCampo) {
		String aliasIse = "ise";
		String aliasShc = "shc";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class);
		criteria.createAlias(AelSismamaHistoRes.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(AelSismamaHistoRes.Fields.SISMAMA_HISTO_CAD.toString(), aliasShc);
		criteria.add(Restrictions.eq(aliasShc + separador + AelSismamaHistoCad.Fields.CODIGO.toString(), codigoCampo));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		return executeCriteria(criteria);
	}

	public List<AelSismamaHistoRes> pesquisarSismamaHistoResPorSoeSeqSeqp(Integer soeSeq, Short seqp) {
		String aliasIse = "ise";
		String aliasShc = "shc";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class);
		criteria.createAlias(AelSismamaHistoRes.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(AelSismamaHistoRes.Fields.SISMAMA_HISTO_CAD.toString(), aliasShc);
		criteria.add(Restrictions.isNotNull(AelSismamaHistoRes.Fields.RESPOSTA.toString()));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		return executeCriteria(criteria);
	}
	
	public Long obterRespostasSismamaHistoCountPorSoeSeqSeqp (
			Integer soeSeq, Short seqp) {
		String aliasIse = "ise";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class);
		criteria.createAlias(AelSismamaHistoRes.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(AelSismamaHistoRes.Fields.SISMAMA_HISTO_CAD.toString(), "shc");
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

		return executeCriteriaCount(criteria);
	}
	
	public List<AelSismamaHistoRes> obterRespostaSismamaHistoPorNumeroApECodigoCampo(Long numeroAp,
			String codigoCampo, Integer lu2Seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoRes.class, "s02");
		criteria.createAlias("s02." + AelSismamaHistoRes.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("s02." + AelSismamaHistoRes.Fields.SISMAMA_HISTO_CAD.toString(), "s01");
		
		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		criteria.add(Restrictions.eq("s01." + AelSismamaHistoCad.Fields.CODIGO.toString(), codigoCampo));

		return executeCriteria(criteria);
	}
	
}
