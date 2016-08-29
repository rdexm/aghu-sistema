package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrumental;

public class PdtInstrPorEquipDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtInstrPorEquip> {

	private static final long serialVersionUID = -6307760157345639728L;
	
	public List<PdtInstrPorEquip> listarPdtInstrPorEquipAtivoPorEquip(Short seqEquipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrPorEquip.class, "ipe");
		criteria.createAlias("ipe."+ PdtInstrPorEquip.Fields.PDT_INSTRUMENTAIS.toString(), "pin");
		criteria.add(Restrictions.eq("pin." + PdtInstrumental.Fields.IND_SITUACAO.toString(), DominioSituacao.A));		
		criteria.add(Restrictions.eq("ipe." + PdtInstrPorEquip.Fields.DEQ_SEQ.toString(), seqEquipe));
		criteria.addOrder(Order.asc("pin." + PdtInstrumental.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<PdtInstrPorEquip> listarPdtInstrPorEquip(Integer seqInstrumento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrPorEquip.class, "ieq");
		criteria.createAlias("ieq."+ PdtInstrPorEquip.Fields.PDT_INSTRUMENTAIS.toString(), "equ");
		criteria.createAlias("ieq."+ PdtInstrPorEquip.Fields.PDT_EQUIPAMENTOS.toString(), "equ2", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ieq." + PdtInstrPorEquip.Fields.PIN_SEQ.toString(), seqInstrumento));
		criteria.addOrder(Order.asc("equ." + PdtEquipamento.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
}
