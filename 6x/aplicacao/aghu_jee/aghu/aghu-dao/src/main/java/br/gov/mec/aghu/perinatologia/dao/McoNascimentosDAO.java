package br.gov.mec.aghu.perinatologia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;

import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.perinatologia.vo.DataNascimentoVO;

public class McoNascimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoNascimentos> {

	private static final long serialVersionUID = 1936643639945294641L;

	private DetachedCriteria obterCriteriaMcoNascimentos(Integer seqp, Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascimentos.class, "NAS");
		if (seqp != null) {
			criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.SEQP.toString(), seqp));
		}
		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));
		}
		if (sequence != null) {
			criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_SEQP.toString(), sequence));
		}
		return criteria;
	}

	public McoNascimentos obterMcoNascimento(Integer seqp, Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteriaUpdate = obterCriteriaMcoNascimentos(seqp, codigoPaciente, sequence);
		return (McoNascimentos) executeCriteriaUniqueResult(criteriaUpdate);
	}

	public List<McoNascimentos> listarNascimentos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, codigoPaciente, sequence);
		return executeCriteria(criteria);
	}

	public List<McoNascimentos> listarNascimentosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, codigoPaciente, null);
		return executeCriteria(criteria);
	}

	public List<McoNascimentos> pesquisarNascimentosPorGestacao(Integer pacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, pacCodigo, gsoSeqp);
		criteria.addOrder(Order.asc("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString()));
		return executeCriteria(criteria);
	}
	
	public Boolean verificaExisteNascimento(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, codigoPaciente, sequence);
		return executeCriteriaExists(criteria);
	}
	
	public Boolean verificaExisteNascimentoPorDtHrNascimento(Integer codigoPaciente, Short sequence, Date dtHrNascimento, boolean isDataIgual) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, codigoPaciente, sequence);
		if (isDataIgual) {
			criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
			
		} else {
			criteria.add(Restrictions.ne("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString(), dtHrNascimento));
		}
		return executeCriteriaExists(criteria);
	}
	
	public Boolean verificaExisteNascimentoPorSequencial(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(seqp, gsoPacCodigo, gsoSeqp);
		return executeCriteriaExists(criteria);
	}
	
	public DetachedCriteria montarCriteriaNascidosRecemNascidosUnion2(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, gsoPacCodigo, gsoSeqp);
		criteria.setProjection(Projections.property("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString()));
		return criteria;
	}
	
	public Integer obterMaxSeqpMcoNascimentos(Integer gsoPacCodigo, Short gsoSeqp) {
		
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, gsoPacCodigo, gsoSeqp);
		criteria.setProjection(Projections.max("NAS." + McoNascimentos.Fields.SEQP.toString()));
		
		Integer maxSeqP = (Integer) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return maxSeqP;
		}
		return 0;
	}
	
	public Date obterDtHrUltimoNascimento(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(null, gsoPacCodigo, gsoSeqp);
		criteria.setProjection(Projections.max("NAS." + McoNascimentos.Fields.DTHR_NASCIMENTO.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public McoNascimentos obterDadosNascimentoSelecionado(Integer seqp, Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = obterCriteriaMcoNascimentos(seqp, gsoPacCodigo, gsoSeqp);
		criteria.createAlias("NAS." + McoNascimentos.Fields.MCO_FORCIPES.toString(), "FCP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NAS." + McoNascimentos.Fields.MCO_CESARIANAS.toString(), "CSN", JoinType.LEFT_OUTER_JOIN);
		
		return (McoNascimentos) executeCriteriaUniqueResult(criteria);
	}
	
	// C13 (Parte 1) - #26325
	public List<McoNascimentos> obterDataHoraPrevInicio(Integer pacCodigo, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascimentos.class, "NAS");
		criteria.createAlias("NAS." + McoNascimentos.Fields.MCO_CESARIANAS.toString(), "CES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_SEQP.toString(), seqp));
		
		return executeCriteria(criteria); 
	}

	// C13 (Parte 2) - #26325
	public List<McoNascimentos> obterDataHoraNascimento(Integer pacCodigo, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascimentos.class, "NAS");
		criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.GSO_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("NAS." + McoNascimentos.Fields.TIPO.toString(), DominioTipoNascimento.P));
		return executeCriteria(criteria);
	}
	
	/**
	 * 39319(se internacaoAdmin e internacao for diferente de nulo) e 39320(se internacaoAdmin e internacao for igual a nulo ) 
	 */
	public McoNascimentos obterMcoNascimento(Integer codigoPaciente, Short sequence, Date internacao, Date internacaoAdmin) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascimentos.class);
		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoNascimentos.Fields.GSO_SEQP.toString(), sequence));
		
		if(internacaoAdmin != null && internacao != null){
			criteria.add(Restrictions.between(McoNascimentos.Fields.DTHR_NASCIMENTO.toString(), internacao, internacaoAdmin));
		}	
		
		return (McoNascimentos) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<DataNascimentoVO> buscarDatasDoNascimentos(Integer pacCodigo, Short gsoSeqp) {
//		
//		StringBuffer hql = new StringBuffer();
//		hql.append(" select          to_date(cesa."+McoCesarianas.Fields.DTHR_PREV_INICIO.toString()+", 'DD/MM/YYYY HH:MI') as dtNascimento from ")
//		.append(" "+McoNascimentos.class.getSimpleName()+" as nasc ")
//		.append(" left join nasc."+McoNascimentos.Fields.MCO_CESARIANAS.toString()+" as cesa ")
//		.append(" where ")
//		.append(" nasc."+McoNascimentos.Fields.GSO_PAC_CODIGO.toString()+" = "+pacCodigo+" ")
//		.append(" and nasc."+McoNascimentos.Fields.GSO_SEQP.toString()+" = "+gsoSeqp+" ")
//		.append(" union ")
//		.append(" nasc."+McoNascimentos.Fields.DTHR_NASCIMENTO.toString()+" - cast(SUBSTRING(LPAD(nasc."+McoNascimentos.Fields.PERIODO_EXPULSIVO.toString()+" ||'', 4, '0') FROM 1 FOR 2) || ':' || SUBSTRING(LPAD(nasc."+McoNascimentos.Fields.PERIODO_EXPULSIVO.toString()+" ||'', 4, '0') FROM 3 FOR 4) || ':00' as time) as dtNascimento ")
//		.append(" from ")
//		.append(" "+McoNascimentos.class.getSimpleName()+" nasc ")
//		.append(" where ")
//		.append(" nasc."+McoNascimentos.Fields.GSO_PAC_CODIGO.toString()+" = "+pacCodigo+" ")
//		.append(" and nasc."+McoNascimentos.Fields.GSO_SEQP.toString()+" = "+gsoSeqp+" ")
//		.append(" and nasc."+McoNascimentos.Fields.TIPO.toString()+" = '"+DominioTipoNascimento.P.toString()+"'");
//		
//		org.hibernate.Query q = createHibernateQuery(hql.toString());
//			
//		q.setResultTransformer(Transformers.aliasToBean(DataNascimentoVO.class));
//		return q.list();
		StringBuffer sql = new StringBuffer(1024);
		
		if(this.isOracle()){
//			sql.append(" SELECT ") 
//			.append(" cast(TO_CHAR(CES.DTHR_PREV_INICIO, 'DD/MM/YYYY HH:MI') as timestamp) as dtNascimento ") 
//			.append(" FROM agh.MCO_NASCIMENTOS NAS ") 
//			.append(" LEFT OUTER JOIN agh.MCO_CESARIANAS CES ON (NAS.GSO_PAC_CODIGO = CES.NAS_GSO_PAC_CODIGO AND NAS.GSO_SEQP = CES.NAS_GSO_SEQP) ") 
//			.append(" WHERE NAS.GSO_PAC_CODIGO = "+pacCodigo+" ")
//			.append(" AND NAS.GSO_SEQP = "+gsoSeqp+" ")
//			.append(" UNION ") 
//			.append(" SELECT ")  
//			.append(" cast(nasc.DTHR_NASCIMENTO - to_date(SUBSTR(LPAD(nasc.PERIODO_EXPULSIVO ||'', 4, '0'),1,2) || ':' || SUBSTR(LPAD(nasc.PERIODO_EXPULSIVO ||'', 4, '0'), 3, 2) || ':00', 'hh24:mi:ss') as timestamp)  as dtNascimento ") 
//			.append(" FROM agh.MCO_NASCIMENTOS  nasc ") 
//			.append(" WHERE nasc.GSO_PAC_CODIGO = "+pacCodigo+" ")
//			.append(" AND nasc.GSO_SEQP = "+gsoSeqp+" ") 
//			.append(" AND nasc.TIPO = 'P' ");
			return null;
		} else {
			sql.append(" SELECT ")
			.append(" cast(TO_CHAR(CES.DTHR_PREV_INICIO, 'DD/MM/YYYY HH:MI') as timestamp) as dtNascimento ")
			.append(" FROM agh.MCO_NASCIMENTOS NAS ")
			.append(" LEFT OUTER JOIN agh.MCO_CESARIANAS CES ON (NAS.GSO_PAC_CODIGO = CES.NAS_GSO_PAC_CODIGO AND NAS.GSO_SEQP = CES.NAS_GSO_SEQP) ")
			.append(" WHERE NAS.GSO_PAC_CODIGO = "+pacCodigo+" ")
			.append(" AND NAS.GSO_SEQP = "+gsoSeqp+" ")
			.append(" UNION ")
			.append(" SELECT  ")
			.append(" cast(nasc.DTHR_NASCIMENTO - cast(SUBSTRING(LPAD(nasc.PERIODO_EXPULSIVO ||'', 4, '0') FROM 1 FOR 2) || ':' || SUBSTRING(LPAD(nasc.PERIODO_EXPULSIVO ||'', 4, '0') FROM 3 FOR 4) || ':00' as time)as timestamp) as dtNascimento ") 
			.append(" FROM agh.MCO_NASCIMENTOS as nasc ")
			.append(" WHERE nasc.GSO_PAC_CODIGO = "+pacCodigo+" ")
			.append(" AND nasc.GSO_SEQP = "+gsoSeqp+" ")
			.append(" AND nasc.TIPO = 'P' ");
		}
		
		SQLQuery query =  createSQLQuery(sql.toString());
		
		final List<DataNascimentoVO> vos = query.addScalar("dtNascimento", DateType.INSTANCE)
				 .setResultTransformer(Transformers.aliasToBean(DataNascimentoVO.class)).list();

		return vos;
	}

}
