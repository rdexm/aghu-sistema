package br.gov.mec.aghu.blococirurgico.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcBloqSalaCirurgicaDAO extends BaseDao<MbcBloqSalaCirurgica> {

	/**
	 * @author fpalma
	 */
	private static final long serialVersionUID = 6627769024765617246L;

	@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})	
	public List<MbcBloqSalaCirurgica> buscarBloqSalaPorCaractSalaEsp(MbcSalaCirurgica sala, MbcProfAtuaUnidCirgs prof) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class, "bsc");
		
		criteria.setProjection(Projections.property("bsc." + MbcBloqSalaCirurgica.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq("bsc.".concat(MbcBloqSalaCirurgica.Fields.MBC_SALA_CIRURGICA.toString()),sala));
		criteria.add(Restrictions.eq("bsc.".concat(MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()),prof));
		criteria.add(Restrictions.sqlRestriction("( ? BETWEEN {alias}.DT_INICIO and {alias}.DT_FIM" + " OR {alias}.DT_INICIO > ? )",
				new Object[]{DateUtil.truncaData(new Date()), DateUtil.truncaData(new Date())}, new Type[]{DateType.INSTANCE, DateType.INSTANCE}));
		
		criteria.add(Restrictions.eq("bsc.".concat(MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcBloqSalaCirurgica> pesquisarBloqueioSalaCirugicaInserirAgenda(Short sciUnfSeq, Short sciSeqp, Date dataCirurgia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class);

		criteria.createAlias(MbcBloqSalaCirurgica.Fields.MBC_SALA_CIRURGICA.toString(), "sci", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.UNF_SEQ.toString()), sciUnfSeq));
		criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.SEQP.toString()), sciSeqp));

		// AND TRUNC(C_DATA) BETWEEN BSC.DT_INICIO AND BSC.DT_FIM
		criteria.add(Restrictions.le(MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), dataCirurgia));
		criteria.add(Restrictions.ge(MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), dataCirurgia));
		

		// ATENÇÃO: A parte da pesquisa de profissionais da unidade cirúrgica foi externalizada na ON. Vide: NVL(BSC.PUC_SER_MATRICULA,C_PUC_SER_MATRICULA)

		criteria.add(Restrictions.eq(MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<MbcBloqSalaCirurgica> pesquisarBloqueioSalaPorSalaDataProf(Short salaSeqp, Short salaUnfSeq, Date dataBase, DominioDiaSemanaSigla diaSemana,			String turno, MbcProfAtuaUnidCirgs atuaUnidCirgs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class, "bsc");
		criteria.createAlias("bsc." + MbcBloqSalaCirurgica.Fields.MBC_SALA_CIRURGICA.toString(), "sla");
		
		criteria.add(Restrictions.eq("sla." + MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), salaUnfSeq));
		criteria.add(Restrictions.eq("sla." + MbcSalaCirurgica.Fields.ID_SEQP.toString(), salaSeqp));
		criteria.add(Restrictions.le(MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.ge(MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString()),
				Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString(), diaSemana)));
		criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString()),
				Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString(), turno)));
		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString()),
					Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula())));
			criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString()),
					Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo())));
			criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString()),
					Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq())));
			criteria.add(Restrictions.or(Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString()),
					Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf())));
		}
		criteria.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcBloqSalaCirurgica> pesquisarBloqueioSala(MbcSalaCirurgica salaCirg, Date dataBase){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class, "bsc");
		criteria.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_SALA_CIRURGICA.toString(), salaCirg));
		criteria.add(Restrictions.le(MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.ge(MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	
	
	public List<MbcBloqSalaCirurgica> pesquisarBloqSalaCirurgica(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana, String turno,
			Short vinCodigo, Integer matricula, Short especialidade) {
		StringBuilder  sqlQuery = new StringBuilder(70);
		sqlQuery.append(" select distinct bsc.*, esp.sigla \n");
		getSqlPesquisarBloqSalaCirurgica(unfSigla, seqp, dtInicio, dtFim,
				diaSemana, turno, vinCodigo, matricula, especialidade,sqlQuery);
		
		sqlQuery.append(" order by bsc.DT_INICIO desc \n");
		
		Query query = this.createNativeQuery(sqlQuery.toString(), MbcBloqSalaCirurgica.class);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return (List<MbcBloqSalaCirurgica>)query.getResultList();
	}

	@SuppressWarnings({"PMD.ConsecutiveLiteralAppends", "PMD.AvoidDuplicateLiterals"})
	private void getSqlPesquisarBloqSalaCirurgica(String unfSigla, Short seqp,
			Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana,
			String turno, Short vinCodigo, Integer matricula, Short especialidade,
			StringBuilder sqlQuery) {
		/**
		 * Este método está utilizando SQL nativo devido a necessidade de fazer um LEFT JOIN com tabelas que não possuem relacionamento.
		 * Isto não é possível fazer nem com HQL e nem com Criteria.
		 * Neste caso específico, o relacionamento entre MbcBloqSalaCirurgica e MbcProfAtuaUnidCirgs é 
		 * feito atráves de uma chave composta por 4 atributos (PUC_SER_MATRICULA, PUC_SER_VIN_CODIGO, PUC_UNF_SEQ, PUC_IND_FUNCAO_PROF)
		 * O LEFT JOIN para esta consulta precisa utilizar somente 3 destes atributos.
		 */
		String schema = MbcBloqSalaCirurgica.class.getAnnotation(Table.class).schema() + ".";
		sqlQuery.append(" from "+schema+MbcBloqSalaCirurgica.class.getAnnotation(Table.class).name()+" bsc \n"); 
		sqlQuery.append(" left join "+schema+MbcProfAtuaUnidCirgs.class.getAnnotation(Table.class).name()+" mpau \n");
		sqlQuery.append(" on mpau.SER_MATRICULA = bsc.puc_ser_matricula \n");
		sqlQuery.append(" and mpau.SER_VIN_CODIGO = bsc.puc_ser_vin_codigo \n");
		sqlQuery.append(" and mpau.UNF_SEQ = bsc.puc_unf_seq \n");
		
		
		sqlQuery.append(" left join "+schema+MbcCaractSalaEsp.class.getAnnotation(Table.class).name()+" prof \n");
		sqlQuery.append(" on prof.puc_ser_matricula = bsc.puc_ser_matricula \n");
		sqlQuery.append(" and prof.puc_SER_VIN_CODIGO = bsc.puc_ser_vin_codigo   \n");
		
		sqlQuery.append(" left join "+schema+AghEspecialidades.class.getAnnotation(Table.class).name()+" esp \n");
		sqlQuery.append(" on esp.seq = bsc.pre_esp_seq \n");
		
		
		sqlQuery.append(" left join "+schema+MbcTurnos.class.getAnnotation(Table.class).name()+" TUR on tur.turno = bsc.turno \n");
		sqlQuery.append(" ,agh."+MbcSalaCirurgica.class.getAnnotation(Table.class).name()+" sci, \n");
		sqlQuery.append(schema).append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" unf \n");
		sqlQuery.append(" WHERE sci.unf_seq = unf.seq \n");
		sqlQuery.append(" and bsc.sci_seqp = sci.seqp \n");
		sqlQuery.append(" and bsc.sci_unf_seq = sci.unf_seq \n");
		
		if(unfSigla != null){
			sqlQuery.append(" and unf.sigla = '" + unfSigla + "'  \n");
		}
		if(seqp != null){
			sqlQuery.append(" and sci.SEQP= " + seqp +"  \n");
		}
		if(dtInicio != null){
			sqlQuery.append(" and bsc.DT_INICIO >=" + getSqlData(dtInicio) + "  \n");
		}
		if(dtFim != null){
			sqlQuery.append(" and bsc.DT_FIM <=" + getSqlData(dtFim) + "  \n");
		}
		if(diaSemana != null){
			sqlQuery.append(" and bsc.DIA_SEMANA='" + diaSemana.name() + "'  \n");
		}
		if(turno != null){
			sqlQuery.append(" and tur.TURNO='" + turno + "'  \n");
		}
		if(matricula != null){
			sqlQuery.append(" and bsc.PUC_SER_MATRICULA=" + matricula + " \n");
		}
		if(vinCodigo != null){
			sqlQuery.append(" and bsc.PUC_SER_VIN_CODIGO=" + vinCodigo + "  \n");
		}
		if(especialidade != null){
			sqlQuery.append(" and bsc.pre_esp_seq=" + especialidade + "  \n");
		}
		
	}
	
	public Long pesquisarBloqSalaCirurgicaCount(String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana,
			String turno, Short vinCodigo, Integer matricula, Short especialidade) {
		StringBuilder  sqlQuery = new StringBuilder(50);
		sqlQuery.append(" select distinct  bsc.*   ,  esp.sigla \n");
		getSqlPesquisarBloqSalaCirurgica(unfSigla, seqp, dtInicio, dtFim,
				diaSemana, turno, vinCodigo, matricula, especialidade, sqlQuery);
		
		Query query = this.createNativeQuery(sqlQuery.toString());
		return Long.valueOf(query.getResultList().size());
		
	}
	private static String getSqlData(Date data){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final String dataFormatada = dateFormat.format(data);
		return "TO_DATE('" + dataFormatada + "', 'dd/mm/yyyy hh24:mi:ss')";
	}
	
}