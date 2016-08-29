package br.gov.mec.aghu.perinatologia.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;

/**
 * 
 * @author lalegre
 *
 */
public class McoRecemNascidosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoRecemNascidos> {

	private static final long serialVersionUID = 4549394426708438232L;

	/**
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public McoRecemNascidos obterMcoRecemNascidos(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", pacCodigo));		
		return (McoRecemNascidos) executeCriteriaUniqueResult(criteria);
	}
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	protected DetachedCriteria obterCriteriaPorGestacao(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class, "RNA");
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp));
		}
		if (pSeqp != null) {
			criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.SEQP.toString(), pSeqp.byteValue()));
		}
		return criteria;
	}
	
	public List<McoRecemNascidos> listarPorGestacao(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		return this.executeCriteria(criteria);
	}
	

	public List<McoRecemNascidos> listaRecemNascidosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class, "MRN");
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));		
		
		DetachedCriteria criteriaGestacoes = criteria.createCriteria("mcoGestacoes", Criteria.INNER_JOIN);
		criteriaGestacoes.createCriteria("mcoNascimentoses", Criteria.INNER_JOIN);
		
		criteria.createCriteria("mcoExameFisicoRns", Criteria.LEFT_JOIN);
		
//		Trecho comentado em função de não existir mais o relacionamento com McoGestacoes com AghAtendimentos	
//		DetachedCriteria criteriaAtendimentos = criteriaGestacoes.createCriteria("aghAtendimentos", Criteria.INNER_JOIN);
//		criteriaAtendimentos.add(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()));
				
		/* -----Trecho de código java que substitui o trecho comentado acima--- */
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class);
		subCriteria.add(Restrictions.eqProperty(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), "MRN." + McoRecemNascidos.Fields.GSO_PAC_CODIGO.toString()));
		subCriteria.add(Restrictions.eqProperty(AghAtendimentos.Fields.GSO_SEQP.toString(), "MRN." + McoRecemNascidos.Fields.GSO_SEQP.toString()));
		subCriteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		subCriteria.setProjection(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		criteria.add(Subqueries.exists(subCriteria));

		return this.executeCriteria(criteria);
	}
	
	public McoRecemNascidos obterRecemNascidoGestacoesPaciente(Integer pPAcCodigo, Short pGsoSeqp, Integer pSeqp) {
		DetachedCriteria criteriaUpdate = DetachedCriteria.forClass(McoRecemNascidos.class);
		criteriaUpdate.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString(), pPAcCodigo));
		criteriaUpdate.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString(), pGsoSeqp));
		criteriaUpdate.add(Restrictions.eq(McoRecemNascidos.Fields.SEQP.toString(), pSeqp.byteValue()));
		criteriaUpdate.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		return (McoRecemNascidos)executeCriteriaUniqueResult(criteriaUpdate);
	}
	
	public McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente){
		DetachedCriteria criteria= DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));		
		McoRecemNascidos recemNascido = (McoRecemNascidos) this.executeCriteriaUniqueResult(criteria);
		return recemNascido;
	}
	
	public Date obterDataNascimentoRecemNascidos(Integer codigoPaciente){
		DetachedCriteria criteria= DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));		
		criteria.setProjection(Projections.projectionList().add(Projections.property(McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString())));
		return (Date) this.executeCriteriaUniqueResult(criteria);
	}

	public List<McoRecemNascidos> listarRecemNascidosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));

		return executeCriteria(criteria);
	}

	public List<McoRecemNascidos> listarRecemNascidosPorGestacaoCodigoPaciente(Integer gsoPacCodigo) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, null, null);
		return executeCriteria(criteria);
	}
	
	public List<McoRecemNascidos> pesquisarRecemNascidosPorCodigoPacienteSeqp(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp)); 
		return executeCriteria(criteria);
	}

	private void getCriteriaObterRecemNascidoPorCodigoPacienteSeqp(
			Integer gsoPacCodigo, Short gsoSeqp, Byte seqp,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp)); 
		criteria.add(Restrictions.eq(McoRecemNascidos.Fields.SEQP.toString(), seqp));
	}

	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer pacCodigo, Short seqp) {
		DetachedCriteria dc = obterCriteriaPorGestacao(pacCodigo, null, seqp.intValue());
		
		dc.addOrder(Order.asc(McoRecemNascidos.Fields.SEQP.toString()));
		
		return executeCriteria(dc);
	}
	
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		StringBuffer hql = new StringBuffer(480);
		
		hql.append("SELECT rn FROM "+ McoRecemNascidos.class.getName() + " rn, \n ");
		hql.append(' ' + MbcFichaAnestesias.class.getName() + " ficha, \n ");
		hql.append(' ' + AipPacientes.class.getName() + " pac \n ");
		
		hql.append(" where ficha." +  MbcFichaAnestesias.Fields.SEQ + " = :seqMbcFichaAnestesia 		\n ");
		hql.append(" and ficha."+  MbcFichaAnestesias.Fields.GSO_PAC_CODIGO	+ " = rn." 	+  McoRecemNascidos.Fields.GSO_PAC_CODIGO + " 	\n ");
		hql.append(" and ficha."+  MbcFichaAnestesias.Fields.GSO_SEQUENCE  	+ " = rn." 	+  McoRecemNascidos.Fields.GSO_SEQP + " 		\n ");
		hql.append(" and rn." 	+  McoRecemNascidos.Fields.GSO_PAC_CODIGO 	+ " = pac." +  AipPacientes.Fields.CODIGO + " 				\n ");
		hql.append(" order by rn." +  McoRecemNascidos.Fields.GSO_PAC_CODIGO + " , \n");
		hql.append(" rn." +  McoRecemNascidos.Fields.GSO_SEQP 	+ ",  	\n ");
		hql.append(" rn." +  McoRecemNascidos.Fields.SEQP 		+ "		\n ");
		
		Query q = createHibernateQuery(hql.toString());
		q.setLong("seqMbcFichaAnestesia", seqMbcFichaAnestesia);
		
		//q.setResultTransformer(Transformers.aliasToBean(AfaDispensacaoMdtoVO.class));
		
		return q.list();
	}
	
	public McoRecemNascidos obterRecemNascidoPorPacCodigoGsoSeqpSeqp(Integer gsoPacCodigo, Short gsoSeqp, Byte seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class, "rna");
		getCriteriaObterRecemNascidoPorCodigoPacienteSeqp(gsoPacCodigo, gsoSeqp, seqp, criteria);
		
		criteria.createAlias("rna."+McoRecemNascidos.Fields.MCO_EXAME_FISICO_RNS.toString(), "efr", Criteria.LEFT_JOIN);
		criteria.createAlias("rna."+McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		
		return (McoRecemNascidos) executeCriteriaUniqueResult(criteria);
	}

	public boolean verificarExisteRecemNascido(Integer pacCodigo){	
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", pacCodigo));
		return this.executeCriteriaExists(criteria);
	}
	
	public Boolean verificaExisteRecemNascido(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		return executeCriteriaExists(criteria);
	}
	
	public Boolean verificaExisteRecemNascidoPorDtHrNascimento(Integer gsoPacCodigo, Short gsoSeqp, Date dtHrNascimento, boolean isDataIgual) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		if (isDataIgual) {
			criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
			
		} else {
			criteria.add(Restrictions.ne("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
		}
		return executeCriteriaExists(criteria);
	}
	
	public List<McoRecemNascidos> listarRecemNascidosSemRegistro(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(McoNascimentos.class, "NAS");
		subCriteria.setProjection(Projections.property("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString()));
		subCriteria.add(Restrictions.eqProperty("NAS." + McoNascimentos.Fields.GSO_PAC_CODIGO.toString(),
				"RNA." + McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString()));
		subCriteria.add(Restrictions.eqProperty("NAS." + McoNascimentos.Fields.GSO_SEQP.toString(),
				"RNA." + McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString()));
		
		criteria.add(Subqueries.propertyNotIn("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString(), subCriteria));
		criteria.addOrder(Order.asc("RNA." + McoRecemNascidos.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaNascidosRecemNascidosUnion1(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		criteria.setProjection(Projections.property("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString()));
		return criteria;
	}
	
	public Set<Date> obterRegistrosNascidosRecemNascidos(Integer gsoPacCodigo, Short gsoSeqp) {
		List<Date> listaDtHrRecemNascidos = executeCriteria(montarCriteriaNascidosRecemNascidosUnion1(gsoPacCodigo, gsoSeqp));
		List<Date> listaDtHrNascidos = executeCriteria(getMcoNascimentosDAO().montarCriteriaNascidosRecemNascidosUnion2(gsoPacCodigo, gsoSeqp));
		
		Set<Date> retornoSet = new HashSet<Date>();
		retornoSet.addAll(listaDtHrRecemNascidos);
		retornoSet.addAll(listaDtHrNascidos);
		
		return retornoSet;
	}
	
	public McoRecemNascidos obterRecemNascidoPorDtHrNascimento(Integer gsoPacCodigo, Short gsoSeqp, Date dtHrNascimento) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
		
		return (McoRecemNascidos) executeCriteriaUniqueResult(criteria);
	}
	
	public McoRecemNascidos obterRecemNascidoPorIdEDtHrNascimento(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp, Date dtHrNascimento) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, pSeqp);
		criteria.add(Restrictions.eq("RNA." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
		
		return (McoRecemNascidos) executeCriteriaUniqueResult(criteria);
	}
	
	public Byte obterMaxSeqpMcoRecemNascidos(Integer gsoPacCodigo, Short gsoSeqp) {
		
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		criteria.setProjection(Projections.max("RNA." + McoRecemNascidos.Fields.SEQP.toString()));
		
		Byte maxSeqP = (Byte) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return maxSeqP;
		}
		return 0;
	}
	
	public McoRecemNascidos obterMcoRecemNascidosPorId(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, pSeqp);
		return (McoRecemNascidos) executeCriteriaUniqueResult(criteria);
	}
	
	private McoNascimentosDAO getMcoNascimentosDAO() {
		return mcoNascimentosDAO;
	}
	
	public Long obterQuantidadeRecemNascidosPorGestante(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		return executeCriteriaCount(criteria);
	}
	
	public List<McoRecemNascidos> listarPorGestante(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaPorGestacao(gsoPacCodigo, gsoSeqp, null);
		criteria.addOrder(Order.asc("RNA." + McoRecemNascidos.Fields.SEQP.toString()));
		return this.executeCriteria(criteria);
	}


	/**
	 * C5 - #27490
	 * @param codigoPaciente
	 * @return
	 */
	public Byte obterApgar5RecemNascido(Integer codigoPaciente){
		DetachedCriteria criteria= DetachedCriteria.forClass(McoRecemNascidos.class);
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));	
		criteria.setProjection(Projections.property(McoRecemNascidos.Fields.APGAR5.toString()));
		return (Byte) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * C6 - #27490
	 * @param codigoPaciente
	 * @return
	 */
	public Byte obterIdadeGestacionalFinalRecemNascido(Integer codigoPaciente){
		DetachedCriteria criteria= DetachedCriteria.forClass(McoRecemNascidos.class, "RN");
		criteria.createAlias(McoRecemNascidos.Fields.MCO_EXAME_FISICO_RNS.toString(), "EFRNS");
		criteria.createAlias(McoRecemNascidos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac.codigo", codigoPaciente));	
		criteria.setProjection(Projections.property("EFRNS."+ McoExameFisicoRns.Fields.IG_FINAL.toString()));
		return (Byte) this.executeCriteriaUniqueResult(criteria);
	}
}
