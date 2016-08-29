package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelTopografiaCidOs;
import br.gov.mec.aghu.model.AelTopografiaLaudos;

public class AelTopografiaLaudosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaLaudos> {

    private static final long serialVersionUID = 8848336991483259808L;

    /**
     * Lista os laudos associados a um número de exame(laudo único/Ap),
     * mostrando código e descrição da Tabela Ael_topografia_CidOs.
     * 
     * @param seqExame
     * @return
     */
    public List<AelTopografiaLaudos> listarTopografiaLaudosPorSeqExame(Long seqExame) {
	final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaLaudos.class, "LDA");
	criteria.createAlias("LDA." + AelTopografiaLaudos.Fields.CIDOS.toString(), "CIDO");
	if (seqExame != null) {
	    criteria.add(Restrictions.eq("LDA." + AelTopografiaLaudos.Fields.SEQ_EXAME.toString(), seqExame));
	}
	criteria.addOrder(Order.desc("CIDO." + AelTopografiaCidOs.Fields.CODIGO.toString()));
	return executeCriteria(criteria);
    }

    /**
     * Verifica se já existe uma topografia cadastrada, nesse exame. Não pode
     * haver repetições.
     * 
     * @param codigo
     * @return
     */
    public AelTopografiaLaudos obterTopografiaLaudos(Long seqExame, String seqCidO) {
	final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaLaudos.class, "LDA");
	criteria.add(Restrictions.eq("LDA." + AelTopografiaLaudos.Fields.SEQ_EXAME.toString(), seqExame));
	criteria.add(Restrictions.eq("LDA." + AelTopografiaLaudos.Fields.SEQ_CIDOS.toString(), seqCidO));
	return (AelTopografiaLaudos) executeCriteriaUniqueResult(criteria);
    }
    
    public Long obterTopografiaLaudosCount(Long seqExame, Long seqCidO) {
   	final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaLaudos.class, "LDA");
   	criteria.add(Restrictions.eq("LDA." + AelTopografiaLaudos.Fields.SEQ_EXAME.toString(), seqExame));
   	criteria.add(Restrictions.eq("LDA." + AelTopografiaLaudos.Fields.SEQ_CIDOS.toString(), seqCidO));
   	return executeCriteriaCount(criteria);
   }
    
	public Long listarTopografiaLaudosCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaLaudos.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelTopografiaLaudos.Fields.SEQ_EXAME.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}
	
}
