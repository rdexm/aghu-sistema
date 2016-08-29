package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaOutraEquipeSumr;
import br.gov.mec.aghu.model.MpmAltaOutraEquipeSumrId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

/**
 * 
 * @author lalegre
 *
 */
public class MpmAltaOutraEquipeSumrDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaOutraEquipeSumr> {

	private static final long serialVersionUID = 5593710149944668542L;

	@Override
	protected void obterValorSequencialId(MpmAltaOutraEquipeSumr elemento) {
		
		if (elemento == null || elemento.getMpmAltaSumarios() == null || elemento.getMpmAltaSumarios().getId() == null) {
			
			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");
		
		}
		
		MpmAltaSumarioId idPai = elemento.getMpmAltaSumarios().getId();
		int seqp = this.recuperarMaxSeqMpmAltaOutraEquipeSumr(idPai);
		
		Integer seq = seqp + 1;
		
		if (elemento.getId() == null) {
			
			elemento.setId(new MpmAltaOutraEquipeSumrId());
			
		}
		
		elemento.getId().setSeqp(seq.shortValue());
		elemento.getId().setAsuApaAtdSeq(idPai.getApaAtdSeq());
		elemento.getId().setAsuApaSeq(idPai.getApaSeq());
		elemento.getId().setAsuSeqp(idPai.getSeqp());
	}
	
	/**
	 * Retorna último seq da tabela
	 * @param id
	 * @return
	 */
	private Short recuperarMaxSeqMpmAltaOutraEquipeSumr(MpmAltaSumarioId id) {
		
		if (id == null || id.getApaAtdSeq() == null || id.getApaSeq() == null) {
			
			throw new IllegalArgumentException("Parametro invalido!!!");
			
		}
		
		Short returnValue = null;
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(altaOutraEquipeSumr.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmAltaOutraEquipeSumr.class.getName()).append(" altaOutraEquipeSumr");
		sql.append(" where altaOutraEquipeSumr.").append(MpmAltaOutraEquipeSumr.Fields.ASU_APA_ATD_SEQ.toString()).append(" = :atdSeq ");
		sql.append(" and altaOutraEquipeSumr.").append(MpmAltaOutraEquipeSumr.Fields.ASU_APA_SEQ.toString()).append(" = :apaSeq ");
		sql.append(" and altaOutraEquipeSumr.").append(MpmAltaOutraEquipeSumr.Fields.ASU_SEQP.toString()).append(" = :seqp ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("atdSeq", id.getApaAtdSeq());
		query.setParameter("apaSeq", id.getApaSeq());			
		query.setParameter("seqp", Short.valueOf(id.getSeqp()));		
		Object maxSeq = query.getSingleResult();
		
		if (maxSeq == null) {
			
			Short seq = 0;
			returnValue = seq;
			
		} else {
			
			returnValue = (Short) maxSeq; 
			
		}
		
		return returnValue;
		
	}
	
	/**
	 * retorna uma lista de MpmAltaOutraEquipeSumr
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param asuSeqp
	 * @return
	 */
	public List<MpmAltaOutraEquipeSumr> obterAltaOutraEquipeSumrs(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaOutraEquipeSumr.class);
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ASU_SEQP.toString(), asuSeqp));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * retorna uma lista de descNome de MpmAltaOutraEquipeSumr
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param asuSeqp
	 * @return
	 */
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaOutraEquipeSumr.Fields.DESC_SERVICO.toString() +
		   ") from MpmAltaOutraEquipeSumr " + 
		   " where "+ MpmAltaOutraEquipeSumr.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaOutraEquipeSumr.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaOutraEquipeSumr.Fields.ASU_SEQP.toString() + "=:asuSeqp "; 
		Query query = createQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
	
		return query.getResultList();		
	}	
	
	/**
	 * 
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param espSeq
	 * @return
	 */
	public boolean possuiAltaOutraEquipeSumr(Integer asuApaAtdSeq, Integer asuApaSeq, Short espSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaOutraEquipeSumr.class);
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaOutraEquipeSumr.Fields.ESP_SEQ.toString(), espSeq));
		
		List<MpmAltaOutraEquipeSumr> retorno = executeCriteria(criteria);
		
		if (retorno != null && retorno.size() > 0) {
			
			return true;
			
		}
		
		return false;
	}

}
