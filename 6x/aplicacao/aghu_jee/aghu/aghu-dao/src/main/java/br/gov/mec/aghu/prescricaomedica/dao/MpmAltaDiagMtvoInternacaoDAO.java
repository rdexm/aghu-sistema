package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacaoId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmAltaDiagMtvoInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaDiagMtvoInternacao> {

	private static final long serialVersionUID = -6221329420257063518L;

	@Override
	public void obterValorSequencialId(MpmAltaDiagMtvoInternacao elemento) {
		
		if (elemento == null || elemento.getAltaSumario() == null || elemento.getAltaSumario().getId() == null) {
			
			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");
		
		}
		
		MpmAltaDiagMtvoInternacaoId id = new MpmAltaDiagMtvoInternacaoId();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagMtvoInternacao.class);
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString(), elemento.getAltaSumario().getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString(), elemento.getAltaSumario().getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_SEQP.toString(), elemento.getAltaSumario().getId().getSeqp()));
		criteria.setProjection(Projections.max(MpmAltaDiagMtvoInternacao.Fields.SEQP.toString()));

		Byte seqp = (Byte) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setAsuApaAtdSeq(elemento.getAltaSumario().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getAltaSumario().getId().getApaSeq());
		id.setAsuSeqp(elemento.getAltaSumario().getId().getSeqp());
		id.setSeqp(++seqp);

		elemento.setId(id);
	}

	public MpmAltaDiagMtvoInternacao obterMpmAltaDiagMtvoInternacaoVelho(MpmAltaDiagMtvoInternacao mpmAltaDiagMtvoInternacao){
//	    this.desatachar(mpmAltaDiagMtvoInternacao);
		
	    // Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
	    // desatachado.
//	    return this.obterPorChavePrimaria(mpmAltaDiagMtvoInternacao.getId());
		return obterOriginal(mpmAltaDiagMtvoInternacao.getId());
	}
	
	
	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. NAO deve ser usado nos metodos merge, persist, etc do entitymanager.
	 * TODO Objeto populado parcialmente, setar os valores necessarios por demanda.<br>
	 * 
	 * @param id
	 * @return
	 */
	/*public MpmAltaDiagMtvoInternacao obterOriginal(MpmAltaDiagMtvoInternacaoId id) {
		StringBuilder hql = new StringBuilder();
		
		hql.append("select o.").append(MpmAltaDiagMtvoInternacao.Fields.ID.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.CID_ATENDIMENTO.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.ALTASUMARIO.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.DESC_CID.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.IND_SITUACAO.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.CID.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.IND_CARGA.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.COMPLEMENTO_CID.toString());
		hql.append(", o.").append(MpmAltaDiagMtvoInternacao.Fields.DIAGNOSTICO.toString());

		hql.append(" from ").append(MpmAltaDiagMtvoInternacao.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(MpmAltaDiagMtvoInternacao.Fields.ALTASUMARIO.toString());
		hql.append(" left outer join o.").append(MpmAltaDiagMtvoInternacao.Fields.CID.toString());
		hql.append(" left outer join o.").append(MpmAltaDiagMtvoInternacao.Fields.DIAGNOSTICO.toString());
		hql.append(" left outer join o.").append(MpmAltaDiagMtvoInternacao.Fields.CID_ATENDIMENTO.toString());

		hql.append(" where o.").append(MpmAltaDiagMtvoInternacao.Fields.ID.toString()).append(" = :entityId ");

		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);


		MpmAltaDiagMtvoInternacao retorno = null;
		Object[] campos = null;
		List list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			campos = (Object[]) list.get(0);			
		}

		if(campos != null) {
			retorno = new MpmAltaDiagMtvoInternacao();

			retorno.setId(id);
			retorno.setCidAtendimento((MpmCidAtendimento) campos[1]);
			retorno.setAltaSumario((MpmAltaSumario) campos[2]);
			retorno.setDescCid((String) campos[3]);
			retorno.setIndSituacao((DominioSituacao) campos[4]);
			retorno.setCid((AghCid) campos[5]);
			retorno.setIndCarga((Boolean) campos[6]);
			retorno.setComplCid((String) campos[7]);
			retorno.setDiaSeq((MamDiagnostico) campos[8]);
		}		
		return retorno;
	}*/
	
	
	/**
	 * Busca mtvo internacao do sumario ATIVO.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaDiagMtvoInternacao> obterMpmAltaDiagMtvoInternacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return this.obterMpmAltaDiagMtvoInternacao(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca mtvo internacao do sumario.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return List of <code>MpmAltaDiagMtvoInternacao</code>
	 */
	public List<MpmAltaDiagMtvoInternacao> obterMpmAltaDiagMtvoInternacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagMtvoInternacao.class);
		
		criteria.createAlias(MpmAltaDiagMtvoInternacao.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return this.executeCriteria(criteria);		
	}

	
	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagMtvoInternacao.class, "DMI");
		criteria.createAlias(MpmAltaDiagMtvoInternacao.Fields.CID_ATENDIMENTO.toString() , "CIA");
		criteria.createAlias(MpmAltaDiagMtvoInternacao.Fields.CID.toString() , "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("DMI." + MpmAltaDiagMtvoInternacao.Fields.CID.toString()), "cid");
		pList.add(Projections.property("DMI." + MpmAltaDiagMtvoInternacao.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	public MpmAltaDiagMtvoInternacao obterAltaDiagMtvoInternacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, Byte seqp) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagMtvoInternacao.class);
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return (MpmAltaDiagMtvoInternacao) this.executeCriteriaUniqueResult(criteria);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		//incluido CID
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(coalesce(" +
		   MpmAltaDiagMtvoInternacao.Fields.DESC_CID.toString() + ",'')||' '||coalesce(" +
		   MpmAltaDiagMtvoInternacao.Fields.COMPL_CID.toString() + 
		   ",'')) from MpmAltaDiagMtvoInternacao " + 
		   " where "+ MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaDiagMtvoInternacao.Fields.ASU_SEQP.toString() + "=:asuSeqp " +
		   " and " + MpmAltaDiagMtvoInternacao.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		Query query = createHibernateQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);
		
		return query.list();
	}
	
	/**
	 * Método que verifica a validação
	 * do diagnóstico de mtvo, da internação 
	 * da alta do paciente. Deve pelo menos 
	 * ter um registro ativo associado ao 
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public List<Long> listAltaDiagMtvoInternacao(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagMtvoInternacao.class);
		
		criteria.setProjection(Projections.rowCount())
		.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_ATD_SEQ.toString(), altaSumarioId.getApaAtdSeq()))
		.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_APA_SEQ.toString(), altaSumarioId.getApaSeq()))
		.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.ASU_SEQP.toString(), altaSumarioId.getSeqp()))
		.add(Restrictions.eq(MpmAltaDiagMtvoInternacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
		
	}
	
}
