package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtCausaDiretaId;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MpmObtCausaDiretaDAO extends BaseDao<MpmObtCausaDireta> {

	private static final long serialVersionUID = -2039860496184357098L;

	@Override
	protected void obterValorSequencialId(MpmObtCausaDireta elemento) {
		
		if (elemento.getMpmAltaSumarios() == null) {
			
			throw new IllegalArgumentException("MpmObtCausaDireta nao esta associado corretamente a MpmAltaSumario.");
		
		}
		
		MpmObtCausaDiretaId id = new MpmObtCausaDiretaId();
		id.setAsuApaAtdSeq(elemento.getMpmAltaSumarios().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getMpmAltaSumarios().getId().getApaSeq());
		id.setAsuSeqp(elemento.getMpmAltaSumarios().getId().getSeqp());
		
		elemento.setId(id);
		
	}

	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtCausaDireta.class, "OCD");
		criteria.createAlias(MpmObtCausaDireta.Fields.CID_ATENDIMENTOS.toString(), "CIA");
		criteria.createAlias(MpmObtCausaDireta.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.ASU_APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.ASU_APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("OCD." + MpmObtCausaDireta.Fields.CID.toString()), "cid");
		pList.add(Projections.property("OCD." + MpmObtCausaDireta.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna MpmObtCausaDireta.
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmObtCausaDireta obterObtCausaDireta(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		return obterObtCausaDireta(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Retorna MpmObtCausaDireta.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 */
	public MpmObtCausaDireta obterObtCausaDireta(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtCausaDireta.class);
		
		criteria.createAlias(MpmObtCausaDireta.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmObtCausaDireta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
	
		
		return (MpmObtCausaDireta) executeCriteriaUniqueResult(criteria);
	}
	

}
