package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;

public class PdtTecnicaPorProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtTecnicaPorProc> {

	private static final long serialVersionUID = 7636044953023516453L;

	/**
	 * 
	 * Busca as Técnicas por Procedimento
	 * 
	 * @return Lista de técnicas
	 */	
	public List<PdtTecnicaPorProc> pesquisarPdtTecnicaPorProcPorDptSeq(Integer dptSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtTecnicaPorProc.class, "tpx");
		criteria.createAlias("tpx." + PdtTecnicaPorProc.Fields.PDT_TECNICAS.toString(), "dte");
		
		criteria.add(Restrictions.eq("tpx."+PdtTecnicaPorProc.Fields.DPT_SEQ.toString(), dptSeq));

		criteria.addOrder(Order.asc("dte." + PdtTecnica.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}

	public List<PdtTecnicaPorProc> listarPdtTecnicaPorProc(Integer dteSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtTecnicaPorProc.class,"tpp");
		criteria.add(Restrictions.eq("tpp." + PdtTecnicaPorProc.Fields.DTE_SEQ.toString(), dteSeq));
		criteria.createAlias("tpp." + PdtTecnicaPorProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "dpr");
		criteria.addOrder(Order.asc("dpr." + PdtProcDiagTerap.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
}
