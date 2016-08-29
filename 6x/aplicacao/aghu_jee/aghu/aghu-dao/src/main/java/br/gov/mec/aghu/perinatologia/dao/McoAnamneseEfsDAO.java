package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;

public class McoAnamneseEfsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoAnamneseEfs> {

	private static final long serialVersionUID = -7745637878388960281L;
	
	private static final String PONTO = ".";
	private static final String ALIAS_AEF = "AEF";

	public List<McoAnamneseEfs> listarAnamnesesEfsPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteriaMcoAnamneseEfs = DetachedCriteria.forClass(McoAnamneseEfs.class);

		criteriaMcoAnamneseEfs.add(Restrictions.eq(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteriaMcoAnamneseEfs);
	}

	public List<McoAnamneseEfs> listarAnamnesesEfs(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAnamneseEfs.class);

		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<HistObstetricaVO> pesquisarAnamnesesEfsPorGestacoesPaginada(Integer gsoPacCodigo, Short gsoSeqp, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		StringBuffer hql = new StringBuffer(415);								 
		hql.append(" select new br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO (");
		hql.append(" efi.");
		hql.append(McoAnamneseEfs.Fields.DATA_CONSULTA.toString());
		hql.append(" as dthrConsulta, ");
		hql.append(" efi.");
		hql.append(McoAnamneseEfs.Fields.GESTACAO.toString());
		hql.append(" as mcoGestacoes, ");
		hql.append(" atd.");
		hql.append(AghAtendimentos.Fields.SEQ.toString());
		hql.append(" as seqAtendimento, ");
		hql.append(" atd.");
		hql.append(AghAtendimentos.Fields.ORIGEM.toString());
		hql.append(" as origemAtendimento, ");
		hql.append(" efi.");
		hql.append(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString());
		hql.append(" as gsoPacCodigo, ");
		hql.append(" efi.");
		hql.append(McoAnamneseEfs.Fields.SEQUENCE.toString());
		hql.append(" as gsoSeqp, ");
		hql.append(" efi.");
		hql.append(McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString());
		hql.append(" as conNumero) ");

		hql.append(" from AghAtendimentos atd, McoAnamneseEfs efi, McoGestacoes gso");

		hql.append(" where efi.");
		hql.append(McoAnamneseEfs.Fields.GSO_PAC_CODIGO.toString());
		hql.append(" = :gsoPacCodigo ");
		hql.append(" and efi.");
		hql.append(McoAnamneseEfs.Fields.GSO_SEQP.toString());
		hql.append(" = :gsoSeqp ");

		hql.append(" and gso.");
		hql.append(McoGestacoes.Fields.CODIGO_PACIENTE.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and gso.");
		hql.append(McoGestacoes.Fields.SEQUENCE.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.SEQUENCE.toString());
		hql.append(" and atd.");
		hql.append(AghAtendimentos.Fields.NUMERO_CONSULTA.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString());
		
		hql.append(" ORDER BY efi.").append(McoAnamneseEfs.Fields.DATA_CONSULTA.toString());
		hql.append(" DESC ");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("gsoPacCodigo", gsoPacCodigo);
		query.setParameter("gsoSeqp", gsoSeqp);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return query.list();
//		return executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}
	
	public Long pesquisarAnamnesesEfsPorGestacoesPaginadaCount(Integer codigo, Short seqp) {
		StringBuffer hql = new StringBuffer(180);
		hql.append(" select ");
		hql.append(" count(*) ");

		hql.append(" from AghAtendimentos atd, McoAnamneseEfs efi, McoGestacoes gso");

		hql.append(" where efi.");
		hql.append(McoAnamneseEfs.Fields.GSO_PAC_CODIGO.toString());
		hql.append(" = :gsoPacCodigo ");
		hql.append(" and efi.");
		hql.append(McoAnamneseEfs.Fields.GSO_SEQP.toString());
		hql.append(" = :gsoSeqp ");

		hql.append(" and gso.");
		hql.append(McoGestacoes.Fields.CODIGO_PACIENTE.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and gso.");
		hql.append(McoGestacoes.Fields.SEQUENCE.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.SEQUENCE.toString());
		hql.append(" and atd.");
		hql.append(AghAtendimentos.Fields.NUMERO_CONSULTA.toString());
		hql.append(" = efi.").append(McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString());

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("gsoPacCodigo", codigo);
		query.setParameter("gsoSeqp", seqp);


		return (Long.valueOf(query.uniqueResult().toString())) ;
	}

	public McoAnamneseEfs obterAnamnesePorGestacaoEConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		 
		DetachedCriteria criteria = criarCriteriaAnamnesePorGestacaoEConsulta(pacCodigo, gsoSeqp, conNumero);

		criteria.createAlias(McoAnamneseEfs.Fields.DIAGNOSTICO.toString(), "diagnostico", JoinType.LEFT_OUTER_JOIN);	
		
		criteria.createAlias(McoAnamneseEfs.Fields.GESTACAO.toString(), "gestacao");
		criteria.createAlias("gestacao." + McoGestacoes.Fields.BOLSA_ROTA.toString(), "bolsaRota", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gestacao." + McoGestacoes.Fields.PACIENTE.toString(), "paciente");
		
		criteria.createAlias(McoAnamneseEfs.Fields.CONSULTA.toString(), "consulta");		
		criteria.createAlias("consulta." + AacConsultas.Fields.FAT_CONVENIO_SAUDE.toString(), "convenioCodigo");
		criteria.createAlias("consulta." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgenda");
		criteria.createAlias("gradeAgenda." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe");
		
		return (McoAnamneseEfs) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteriaAnamnesePorGestacaoEConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAnamneseEfs.class);
		
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString(), conNumero));
		return criteria;
	}
	
	public McoAnamneseEfs obterAnamnesePorPacienteSequenceGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		 
		DetachedCriteria criteria = criarCriteriaAnamnesePorGestacaoEConsulta(pacCodigo, gsoSeqp, conNumero);

		return (McoAnamneseEfs) executeCriteriaUniqueResult(criteria);
	}
	
	public McoAnamneseEfs obterAnamnesePorPacienteGsoSeqpConNumero(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		 
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAnamneseEfs.class, "efi");
		criteria.createAlias("efi."+McoAnamneseEfs.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("efi."+McoAnamneseEfs.Fields.CONSULTA.toString(), "con", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("con."+AacConsultas.Fields.FAT_CONVENIO_SAUDE.toString(), "cnv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("grd."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "eqp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("efi."+McoAnamneseEfs.Fields.DIAGNOSTICO.toString(), "dig", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		if(conNumero == null){
			criteria.add(Restrictions.or(
					Restrictions.isNull("efi."+McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString()), 
					Restrictions.and(Restrictions.eq("efi."+McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString(), conNumero), 
							Restrictions.eq("efi."+McoAnamneseEfs.Fields.SEQUENCE.toString(), gsoSeqp))));
		}else{
			criteria.add(Restrictions.and(Restrictions.eq("efi."+McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString(), conNumero), 
							Restrictions.eq("efi."+McoAnamneseEfs.Fields.SEQUENCE.toString(), gsoSeqp)));
		}

		return (McoAnamneseEfs) executeCriteriaUniqueResult(criteria);			
	}
	
	public McoAnamneseEfs obterAnamnesePorPaciente(Integer pacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAnamneseEfs.class);		
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoAnamneseEfs.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.addOrder(Order.desc(McoAnamneseEfs.Fields.CRIADO_EM.toString()));
		List<McoAnamneseEfs> listaMcoAnamneseEfs = executeCriteria(criteria);
		if(!listaMcoAnamneseEfs.isEmpty()){
			return listaMcoAnamneseEfs.get(0);
		}
		return null;
	}
	
	private DetachedCriteria getCriteriaMcoAnamneseEfsPorCodPacGestanteESeqGestante(Integer paciente, Short sequence) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAnamneseEfs.class , ALIAS_AEF);
		criteria.add(Restrictions.eq(ALIAS_AEF + PONTO + McoAnamneseEfs.Fields.CODIGO_PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(ALIAS_AEF + PONTO + McoAnamneseEfs.Fields.SEQUENCE.toString(), sequence));
		return criteria;
	}

	/**
	 * @param consulta numero da consulta
	 * @param paciente código da gestante
	 * @param sequence seqp da gestação
	 * @return Long
	 */
	public Long countMcoAnamneseEfsPorNrConsultaECodPacGestanteESeqGestante(Integer consulta, Integer paciente, Short sequence){
		final DetachedCriteria criteria = getCriteriaMcoAnamneseEfsPorCodPacGestanteESeqGestante(paciente, sequence);
		criteria.add(Restrictions.eq(ALIAS_AEF + PONTO + McoAnamneseEfs.Fields.NUMERO_CONSULTA.toString(), consulta));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * @param paciente código da gestante
	 * @param sequence seqp da gestação
	 * @return List<McoAnamneseEfs>
	 */
	public List<McoAnamneseEfs> listarMcoAnamneseEfsPorCodPacGestanteESeqGestante(Integer paciente, Short sequence){
		final DetachedCriteria criteria = getCriteriaMcoAnamneseEfsPorCodPacGestanteESeqGestante(paciente, sequence);
		criteria.createAlias(McoAnamneseEfs.Fields.CONSULTA.toString(), "CONS");
		return super.executeCriteria(criteria);
	}
}
