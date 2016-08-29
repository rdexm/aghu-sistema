package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.model.MpmAltaConsultoriaId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira - 29/10/2010
 * 
 */
public class MpmAltaConsultoriaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaConsultoria> {

	private static final long serialVersionUID = -6129976531812582795L;



	@Override
	public void obterValorSequencialId(MpmAltaConsultoria elemento) {
	    MpmAltaConsultoriaId id = elemento.getId();
	    if(id != null){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaConsultoria.class);
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString(), id.getAsuApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString(), id.getAsuApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_SEQP.toString(), id.getAsuSeqp()));
		criteria.setProjection(Projections.max(MpmAltaConsultoria.Fields.SEQP.toString()));
		
		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setSeqp(++seqp);
	    }
		if (elemento == null || elemento.getAltaSumario() == null || elemento.getAltaSumario().getId() == null) {

			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");

		}

		MpmAltaSumarioId idPai = elemento.getAltaSumario().getId();
		int seqp = this.recuperarMaxSeqMpmAltaConsultoria(idPai);

		Integer seq = seqp + 1;

		if (elemento.getId() == null) {

			elemento.setId(new MpmAltaConsultoriaId());

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
	 * @throws ApplicationBusinessException
	 */
	private Integer recuperarMaxSeqMpmAltaConsultoria(MpmAltaSumarioId id) {
		
		if (id == null || id.getApaAtdSeq() == null || id.getApaSeq() == null) {
			
			throw new IllegalArgumentException("Parametro invalido!!!");
			
		}
		
		Integer returnValue = null;
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(altaConsultoria.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmAltaConsultoria.class.getName()).append(" altaConsultoria");
		sql.append(" where altaConsultoria.").append(MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString()).append(" = :atdSeq ");
		sql.append(" and altaConsultoria.").append(MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString()).append(" = :apaSeq ");
		sql.append(" and altaConsultoria.").append(MpmAltaConsultoria.Fields.ASU_SEQP.toString()).append(" = :seqp ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("atdSeq", id.getApaAtdSeq());
		query.setParameter("apaSeq", id.getApaSeq());			
		query.setParameter("seqp", Short.valueOf(id.getSeqp()));		
		Object maxSeq = query.getSingleResult();
		
		if (maxSeq == null) {
			
			returnValue = Integer.valueOf(0);
			
		} else {
			
			returnValue = ((Short) maxSeq).intValue(); 
			
		}
		
		return returnValue;
		
	}

	/**
	 * Verifica se existe consultorias do sumário de alta
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean possuiAltaConsultoria(Integer altanAtdSeq, Integer altanApaSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaConsultoria.class);
		
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.IND_CARGA.toString(), true));
		
		List<MpmAltaConsultoria> retorno = this.executeCriteria(criteria);
		
		if (retorno != null && retorno.size() > 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Busca consultorias do sumário de alta ativas.<br>
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmAltaConsultoria> obterMpmAltaConsultoria(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return obterMpmAltaConsultoria(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca consultorias do sumário de alta.
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmAltaConsultoria> obterMpmAltaConsultoria(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaConsultoria.class);
		
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaConsultoria.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return this.executeCriteria(criteria);
	}


	
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaConsultoria.Fields.DTHR_CONSULTORIA.toString() + "," +
			MpmAltaConsultoria.Fields.DESC_CONSULTORIA.toString() + 
				") from MpmAltaConsultoria " + 
		   "where "+ MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaConsultoria.Fields.ASU_SEQP.toString() + "=:asuSeqp " +
		   " and " + MpmAltaConsultoria.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		
		Query query = createQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);		
		
		return query.getResultList();		
	}
	
	/**
	 * 
	 * Busca lista do objeto MpmAltaConsultoria pela FK de MpmAltaSumario.
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @return List<MpmAltaConsultoria>
	 */
	public List<MpmAltaConsultoria> obterListaAltaConsultoriaPeloAltaSumarioId(
			MpmAltaSumarioId altaSumarioId) {

		if (altaSumarioId == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaConsultoria.class);
		criteria.add(Restrictions.eq(
				MpmAltaConsultoria.Fields.ASU_APA_ATD_SEQ.toString(),
				altaSumarioId.getApaAtdSeq()));
		criteria.add(Restrictions.eq(
				MpmAltaConsultoria.Fields.ASU_APA_SEQ.toString(),
				altaSumarioId.getApaSeq()));
		criteria.add(Restrictions.eq(
				MpmAltaConsultoria.Fields.ASU_SEQP.toString(),
				altaSumarioId.getSeqp()));
		List<MpmAltaConsultoria> retorno = this.executeCriteria(criteria);

		return retorno;
	}
	
}
