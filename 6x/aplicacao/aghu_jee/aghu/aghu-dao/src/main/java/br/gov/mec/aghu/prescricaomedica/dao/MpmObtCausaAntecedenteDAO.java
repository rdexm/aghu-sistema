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
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaAntecedenteId;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmObtCausaAntecedenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmObtCausaAntecedente> {

	private static final long serialVersionUID = 8302116276942240471L;

	@Override
	public void obterValorSequencialId(MpmObtCausaAntecedente elemento) {
		
		if (elemento == null || elemento.getMpmAltaSumarios() == null || elemento.getMpmAltaSumarios().getId() == null) {

			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");

		}

		MpmObtCausaAntecedenteId id = new MpmObtCausaAntecedenteId();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtCausaAntecedente.class);
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_ATD_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_SEQP.toString(), elemento.getMpmAltaSumarios().getId().getSeqp()));
		criteria.setProjection(Projections.max(MpmObtCausaAntecedente.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setSeqp(++seqp);
		id.setAsuApaAtdSeq(elemento.getMpmAltaSumarios().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getMpmAltaSumarios().getId().getApaSeq());
		id.setAsuSeqp(elemento.getMpmAltaSumarios().getId().getSeqp());
		
		elemento.setId(id);

	}
	
	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtCausaAntecedente.class, "OCN");
		criteria.createAlias(MpmObtCausaAntecedente.Fields.CID_ATENDIMENTOS.toString(), "CIA");
		criteria.createAlias(MpmObtCausaAntecedente.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("OCN." + MpmObtCausaAntecedente.Fields.CID.toString()), "cid");
		pList.add(Projections.property("OCN." + MpmObtCausaAntecedente.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca MpmObtCausaAntecedente do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmObtCausaAntecedente> obterMpmObtCausaAntecedente(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return obterMpmObtCausaAntecedente(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca MpmObtCausaAntecedente do sumário.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmObtCausaAntecedente> obterMpmObtCausaAntecedente(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtCausaAntecedente.class);
		
		criteria.createAlias(MpmObtCausaAntecedente.Fields.CID.toString(), "CID");
		
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmObtCausaAntecedente.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return executeCriteria(criteria);
	}
	
}