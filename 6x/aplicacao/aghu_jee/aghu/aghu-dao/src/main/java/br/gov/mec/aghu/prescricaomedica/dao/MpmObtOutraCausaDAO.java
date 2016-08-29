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
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.model.MpmObtOutraCausaId;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmObtOutraCausaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmObtOutraCausa> {

	private static final long serialVersionUID = -8288435817006258323L;

	@Override
	public void obterValorSequencialId(MpmObtOutraCausa elemento) {
		
		if (elemento == null || elemento.getMpmAltaSumarios() == null || elemento.getMpmAltaSumarios().getId() == null) {

			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");

		}

		MpmObtOutraCausaId id = new MpmObtOutraCausaId();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtOutraCausa.class);
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_ATD_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_SEQP.toString(), elemento.getMpmAltaSumarios().getId().getSeqp()));
		criteria.setProjection(Projections.max(MpmObtOutraCausa.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setSeqp(++seqp);
		id.setAsuApaAtdSeq(elemento.getMpmAltaSumarios().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getMpmAltaSumarios().getId().getApaSeq());
		id.setAsuSeqp(elemento.getMpmAltaSumarios().getId().getSeqp());
		
		elemento.setId(id);
		
	}

	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtOutraCausa.class, "OCC");
		criteria.createAlias(MpmObtOutraCausa.Fields.CID_ATENDIMENTOS.toString(), "CIA");
		criteria.createAlias(MpmObtOutraCausa.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("OCC." + MpmObtOutraCausa.Fields.CID.toString()), "cid");
		pList.add(Projections.property("OCC." + MpmObtOutraCausa.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca MpmObtOutraCausa do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmObtOutraCausa> obterMpmObtOutraCausa(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return obterMpmObtOutraCausa(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca MpmObtOutraCausa do sumário.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 */
	public List<MpmObtOutraCausa> obterMpmObtOutraCausa(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtOutraCausa.class);
		
		criteria.createAlias(MpmObtOutraCausa.Fields.CID.toString(), "CID");
		
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmObtOutraCausa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return executeCriteria(criteria);
	}
	
}
