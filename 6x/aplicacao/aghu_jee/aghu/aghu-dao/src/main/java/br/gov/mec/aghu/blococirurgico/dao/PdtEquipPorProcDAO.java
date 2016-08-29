package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;

public class PdtEquipPorProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtEquipPorProc> {

	private static final long serialVersionUID = 345615660589398805L;

	/**
	 * 
	 * Busca os Equipamentos por Procedimento
	 * 
	 * @return Lista de equipamentos
	 */	
	public List<PdtEquipPorProc> pesquisarPdtEquipPorProcPorDptSeq(Integer dptSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtEquipPorProc.class, "relacao");
		criteria.createAlias("relacao." + PdtEquipPorProc.Fields.PDT_EQUIPAMENTOS.toString(), "equip", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("relacao."+PdtEquipPorProc.Fields.DPT_SEQ.toString(), dptSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<PdtEquipPorProc> listarPdtEquipPorProcAtivoPorEquipe(Short seqEquipamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtEquipPorProc.class, "epp");
		criteria.createAlias("epp."+ PdtEquipPorProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "pdt");
		criteria.add(Restrictions.eq("pdt." + PdtProcDiagTerap.Fields.SITUACAO.toString(), DominioSituacao.A));		
		criteria.add(Restrictions.eq("epp." + PdtEquipPorProc.Fields.DEG_SEQ.toString(), seqEquipamento));
		criteria.addOrder(Order.asc("pdt." + PdtProcDiagTerap.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}


}
