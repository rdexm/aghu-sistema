package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaComplFarmaco;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira
 *
 */
public class MpmAltaComplFarmacoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaComplFarmaco> {

	private static final long serialVersionUID = -2645046850927537421L;

	@Override
	protected void obterValorSequencialId(MpmAltaComplFarmaco elemento) {

		if (elemento == null || elemento.getAltaSumario() == null) {
		
			throw new IllegalArgumentException("Parametro invalido!!!");
		
		}

		MpmAltaSumarioId altaSumarioId = elemento.getAltaSumario().getId();
//		MpmAltaComplFarmacoId altaComplFarmacoId = new MpmAltaComplFarmacoId();
//		altaComplFarmacoId.setAsuApaAtdSeq(altaSumarioId.getApaAtdSeq());
//		altaComplFarmacoId.setAsuApaSeq(altaSumarioId.getApaSeq());
//		altaComplFarmacoId.setAsuSeqp(altaSumarioId.getSeqp());
		
		elemento.setId(altaSumarioId);
		
	}
	
	/**
	 * Busca informações complementares para os farmacos
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaComplFarmaco obterMpmAltaComplFarmaco(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaComplFarmaco.class);
		criteria.add(Restrictions.eq(MpmAltaComplFarmaco.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaComplFarmaco.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaComplFarmaco.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return (MpmAltaComplFarmaco) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Busca informações complementares para os farmacos
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaComplFarmaco obterAltaComplempentoFarmacoPorSumarioAlta(MpmAltaSumario sumarioAlta) throws ApplicationBusinessException {
		MpmAltaComplFarmaco mpmAltaComplFarmaco = obterMpmAltaComplFarmaco(sumarioAlta.getId().getApaAtdSeq(), sumarioAlta.getId().getApaSeq(), sumarioAlta.getId().getSeqp());//super.executeCriteria(criteria);
	
		return mpmAltaComplFarmaco;
	}
	
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
		MpmAltaComplFarmaco.Fields.DESCRICAO.toString() +
		   ") from MpmAltaComplFarmaco " + 
		   " where "+ MpmAltaComplFarmaco.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaComplFarmaco.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaComplFarmaco.Fields.ASU_SEQP.toString() + "=:asuSeqp ";
		Query query = createHibernateQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		return query.list();
	}   	

}
