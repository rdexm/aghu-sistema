package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.model.AelSismamaMamoRes;

public class AelSismamaMamoResDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaMamoRes> {
	
	private static final long serialVersionUID = -9176885648543441517L;

	public AelSismamaMamoRes obterRespostaMamografia(Integer soeSeq, Short seqp, DominioSismamaMamoCadCodigo campo) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		dc.createAlias("RES.".concat(AelSismamaMamoRes.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");

		dc.add(Restrictions.eq("CAD.".concat(AelSismamaMamoCad.Fields.CODIGO.toString()), campo.toString()));
		
		Object obj = executeCriteriaUniqueResult(dc);
		
		if (obj != null) {
			return (AelSismamaMamoRes) obj;
		}
		return null;
	}
	
	public List<AelSismamaMamoRes> obterRespostasMamografia(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		dc.createAlias("RES.".concat(AelSismamaMamoRes.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");

		return executeCriteria(dc);
		
	}
	
	public List<AelSismamaMamoRes> obterRespostasMamografiaRespNull(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		dc.createAlias("RES.".concat(AelSismamaMamoRes.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");

		dc.add(Restrictions.isNull("RES.".concat(AelSismamaMamoRes.Fields.RESPOSTA.toString())));
		
		return executeCriteria(dc);
		
	}
	
	public Long obterRespostaMamografiaCount(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		return executeCriteriaCount(dc);
	}

	private DetachedCriteria criarCriteriaPesquisarRespostaMamografia(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "RES");
		
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoRes.Fields.ISE_SEQP.toString()), seqp));
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoRes.Fields.ISE_SOE_SEQ.toString()), soeSeq));
		return dc;
	}
	
	public List<AelSismamaMamoRes> pesquisarRespostasMamografias(Integer soeSeq, Short seqp) {
		return executeCriteria(criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp));
	}
	
	public List<AelSismamaMamoRes> pesquisarRespostaMamografiaPorItemSolicitacao(String s03Codigo, Integer cIseSoeSeq, Short cIseSeqp){
		
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(cIseSoeSeq, cIseSeqp);
		dc.createAlias("RES.".concat(AelSismamaMamoRes.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");
		dc.add(Restrictions.eq("CAD.".concat(AelSismamaMamoCad.Fields.CODIGO.toString()), s03Codigo));
		
		return executeCriteria(dc);
	}
}
