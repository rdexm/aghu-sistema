package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmAltaDiagSecundarioId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira - 29/10/2010
 *
 */
public class MpmAltaDiagSecundarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaDiagSecundario> {

	private static final long serialVersionUID = 7098646698794777265L;

	@Override
	public void obterValorSequencialId(MpmAltaDiagSecundario elemento) {

		if (elemento == null || elemento.getMpmAltaSumarios() == null || elemento.getMpmAltaSumarios().getId() == null) {

			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");

		}

		MpmAltaDiagSecundarioId id = new MpmAltaDiagSecundarioId();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagSecundario.class);
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_ATD_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_SEQ.toString(), elemento.getMpmAltaSumarios().getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_SEQP.toString(), elemento.getMpmAltaSumarios().getId().getSeqp()));
		criteria.setProjection(Projections.max(MpmAltaDiagSecundario.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setAsuApaAtdSeq(elemento.getMpmAltaSumarios().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getMpmAltaSumarios().getId().getApaSeq());
		id.setAsuSeqp(elemento.getMpmAltaSumarios().getId().getSeqp());
		id.setSeqp(++seqp);

		elemento.setId(id);

	}

	public MpmAltaDiagSecundario obterMpmAltaDiagSecundarioVelho(MpmAltaDiagSecundario altaDiagSecundario) {
		this.desatachar(altaDiagSecundario);
		// Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
		// desatachado.
		return this.obterPorChavePrimaria(altaDiagSecundario.getId());
	}

	/**
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmAltaDiagSecundario> obterAltaDiagSecundario(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return obterAltaDiagSecundario(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}

	/**
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmAltaDiagSecundario> obterAltaDiagSecundario(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos)
			throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagSecundario.class);

		criteria.createAlias(MpmAltaDiagSecundario.Fields.CID_SEQ.toString(), "CID");
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_SEQP.toString(), altanAsuSeqp));

		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}

		return this.executeCriteria(criteria);
	}

	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagSecundario.class, "ADS");
		criteria.createAlias(MpmAltaDiagSecundario.Fields.CID_ATENDIMENTOS.toString(), "CIA");
		criteria.createAlias(MpmAltaDiagSecundario.Fields.CID_SEQ.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.ASU_APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("ADS." + MpmAltaDiagSecundario.Fields.CID_SEQ.toString()), "cid");
		pList.add(Projections.property("ADS." + MpmAltaDiagSecundario.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));

		return this.executeCriteria(criteria);
	}

	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		//incluido (CID)
		String hql = "select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(coalesce(" + MpmAltaDiagSecundario.Fields.DESC_CID.toString()
				+ ",'')||' '||coalesce(" + MpmAltaDiagSecundario.Fields.COMPLEMENTO_CID.toString() + ",'')) from MpmAltaDiagSecundario " + " where "
				+ MpmAltaDiagSecundario.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " + " and " + MpmAltaDiagSecundario.Fields.ASU_APA_SEQ.toString()
				+ "=:asuApaSeq " + " and " + MpmAltaDiagSecundario.Fields.ASU_SEQP.toString() + "=:asuSeqp " + " and "
				+ MpmAltaDiagSecundario.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		Query query = createHibernateQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);

		return query.list();
	}

	/**
	 * {@link IPrescricaoMedicaFacade#buscaAltasSecundariosPorAtendimento(AghAtendimentos)}
	 */
	public List<MpmAltaDiagSecundario> buscaAltasSecundariosPorAtendimento(AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagSecundario.class, "ads");
		criteria.createCriteria("ads." + MpmAltaDiagSecundario.Fields.ALTA_SUMARIO.toString(), "asu", JoinType.INNER_JOIN);
		criteria.createCriteria("ads." + MpmAltaDiagSecundario.Fields.CID_SEQ.toString(), "cid", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("asu." + MpmAltaSumario.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq("asu." + MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.eq(MpmAltaDiagSecundario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria);
	}
}
