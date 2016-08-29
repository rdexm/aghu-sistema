package br.gov.mec.aghu.ambulatorio.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroEspecialidadeDisponivelVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.VAacConsTipo;
import br.gov.mec.aghu.core.utils.DateUtil;


public class VAacConsTipoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAacConsTipo> {

	private static final String CON_PONTO = "CON.";
	private static final String C_PONTO = "C.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4204856372487400L;

	public List<EspecialidadeDisponivelVO> obterListaEspecialidadesDisponiveis(FiltroEspecialidadeDisponivelVO filtro,Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc){
		DetachedCriteria criteria = filtroObterListaEspecialidadesDisponiveis(filtro);
		
		criteria.setResultTransformer(Transformers.aliasToBean(EspecialidadeDisponivelVO.class));
		criteria.addOrder(Order.asc(C_PONTO+VAacConsTipo.Fields.ESP_NOME.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	
	public Long obterListaEspecialidadesDisponiveisCount(FiltroEspecialidadeDisponivelVO filtro){
		DetachedCriteria criteria = filtroObterListaEspecialidadesDisponiveis(filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria filtroObterListaEspecialidadesDisponiveis(
			FiltroEspecialidadeDisponivelVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacConsTipo.class,"C");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.ESP_SIGLA.toString()),
						EspecialidadeDisponivelVO.Fields.SIGLA.toString())
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.ESP_NOME.toString()),
						EspecialidadeDisponivelVO.Fields.ESPECIALIDADE.toString())
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.CAA_DESCRICAO.toString()),
						EspecialidadeDisponivelVO.Fields.CONDICAO_ATENDIMENTO.toString())
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.PGD_DESCRICAO.toString()),
						EspecialidadeDisponivelVO.Fields.PAGADOR.toString())
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.TAG_DESCRICAO.toString()),
						EspecialidadeDisponivelVO.Fields.AUTORIZACAO.toString())
				.add(Projections.property(C_PONTO+VAacConsTipo.Fields.VISIT_IND_SIT_CONS.toString()),
						EspecialidadeDisponivelVO.Fields.SITUACAO.toString())
				.add(Projections.sqlProjection(
						sqlProjections(DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtInicial()), "dd/MM/yyyy"),
								DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtFinal()), "dd/MM/yyyy"))
						,new String[]{EspecialidadeDisponivelVO.Fields.QUANTIDADE.toString()}, new Type[]{LongType.INSTANCE})));
		
		if(filtro.getEspecialidade() != null){
			criteria.add(Restrictions.eq(C_PONTO+VAacConsTipo.Fields.ESP_SEQ.toString(),filtro.getEspecialidade().getSeq()));
		}
		if(filtro.getCondicaoAtendimento() != null){
			criteria.add(Restrictions.eq(C_PONTO+VAacConsTipo.Fields.V_CAA_SEQ.toString(),filtro.getCondicaoAtendimento().getSeq()));
		}
		if(filtro.getPagador() != null){
			criteria.add(Restrictions.eq(C_PONTO+VAacConsTipo.Fields.V_PGD_SEQ.toString(),filtro.getPagador().getSeq()));
		}
		if(filtro.getAutorizacao() != null){
			criteria.add(Restrictions.eq(C_PONTO+VAacConsTipo.Fields.V_TAG_SEQ.toString(),filtro.getAutorizacao().getSeq()));
		}
		if(filtro.getSituacao() != null){
			criteria.add(Restrictions.like(C_PONTO+VAacConsTipo.Fields.VISIT_IND_SIT_CONS,filtro.getSituacao()));
		}
		criteria.add(Subqueries.exists(subQueryExists(
				DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtInicial()), "dd/MM/yyyy"),
				DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtFinal()), "dd/MM/yyyy"))));
		return criteria;
	}
	
	private String sqlProjections(String dtInicial, String dtFinal){

		StringBuilder sql = new StringBuilder(600);
		sql.append("( select count(*) ")
		.append(" from agh.agh_especialidades esp, agh.aac_grade_agendamen_consultas grd, agh.aac_consultas con ")
		.append(" where ")
		.append(" con.dt_consulta between to_date('"+dtInicial+"','dd/mm/yyyy') and ( to_date('"+dtFinal+"','dd/mm/yyyy') + 1 ) ");
		if(isOracle()){
			sql.append(" and con.fag_caa_seq+0 = nvl(this_.v_caa_seq,con.fag_caa_seq) ")
			.append(" and con.fag_pgd_seq+0 = nvl(this_.v_pgd_seq,con.fag_pgd_seq) ")
			.append(" and con.fag_tag_seq+0 = nvl(this_.v_tag_seq,con.fag_tag_seq) ")
			.append(" and con.ind_sit_consulta = nvl(this_.vsit_ind_sit_cons,con.ind_sit_consulta) ");
		}
		else{
			sql.append(" and con.fag_caa_seq+0 = coalesce(this_.v_caa_seq,con.fag_caa_seq) ")
			.append(" and con.fag_pgd_seq+0 = coalesce(this_.v_pgd_seq,con.fag_pgd_seq) ")
			.append(" and con.fag_tag_seq+0 = coalesce(this_.v_tag_seq,con.fag_tag_seq) ")
			.append(" and con.ind_sit_consulta = coalesce(this_.vsit_ind_sit_cons,con.ind_sit_consulta) ");
		}
		sql.append(" and con.grd_seq = trunc(grd.seq) ")
		.append(" and esp.seq = grd.esp_seq ")
		.append(" and esp.seq = this_.v_esp_seq ")
		.append(" group by grd.esp_seq, esp.sigla, ")
		.append(" esp.nome_especialidade, esp.nome_reduzido, ")
		.append(" con.fag_caa_seq, con.fag_pgd_seq, ")
		.append(" con.fag_tag_seq, con.ind_sit_consulta ) as quantidade");
		return sql.toString();
	}
	
	private DetachedCriteria subQueryExists(String dtInicial, String dtFinal){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class,"GRD");	
		
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON");
		criteria.setProjection(Projections.projectionList().add(Projections.property(CON_PONTO+AacConsultas.Fields.FAG_CAA_SEQ.toString())));
		criteria.setProjection(Projections.projectionList().add(Projections.property(CON_PONTO+AacConsultas.Fields.FAG_PGD_SEQ.toString())));
		criteria.setProjection(Projections.projectionList().add(Projections.property(CON_PONTO+AacConsultas.Fields.FAG_TAG_SEQ.toString())));
		criteria.setProjection(Projections.projectionList().add(Projections.property(CON_PONTO+AacConsultas.Fields.IND_SIT_CONSULTA.toString())));
		criteria.setProjection(Projections.projectionList().add(Projections.property("GRD."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())));
		
		StringBuilder sql = new StringBuilder(113);
		sql.append(" con1_.dt_consulta between to_date('"+dtInicial+"','dd/mm/yyyy') and ( to_date('"+dtFinal+"','dd/mm/yyyy') + 1 ) ");
		criteria.add(Restrictions.sqlRestriction(sql.toString()));
		criteria.add(Restrictions.eqProperty(CON_PONTO+AacConsultas.Fields.FAG_CAA_SEQ.toString(),C_PONTO+VAacConsTipo.Fields.V_CAA_SEQ.toString()));
		criteria.add(Restrictions.eqProperty(CON_PONTO+AacConsultas.Fields.FAG_PGD_SEQ.toString(),C_PONTO+VAacConsTipo.Fields.V_PGD_SEQ.toString()));
		criteria.add(Restrictions.eqProperty(CON_PONTO+AacConsultas.Fields.FAG_TAG_SEQ.toString(),C_PONTO+VAacConsTipo.Fields.V_TAG_SEQ.toString()));
		criteria.add(Restrictions.eqProperty(CON_PONTO+AacConsultas.Fields.IND_SIT_CONSULTA.toString(),C_PONTO+VAacConsTipo.Fields.VISIT_IND_SIT_CONS.toString()));
		criteria.add(Restrictions.eqProperty("GRD."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(),C_PONTO+VAacConsTipo.Fields.ESP_SEQ.toString()));
		
		return criteria;
	}
	
	public List<BigDecimal> obterSomatorioQuantidade(FiltroEspecialidadeDisponivelVO filtro){
		
		StringBuilder hql = new StringBuilder(1200);
		hql.append("SELECT SUM("+EspecialidadeDisponivelVO.Fields.QUANTIDADE.toString()+") "+EspecialidadeDisponivelVO.Fields.QUANTIDADE_TOTAL.toString()+" FROM (");
		hql.append("SELECT COUNT(*) quantidade FROM agh.v_aac_cons_tipo this_, ");
		hql.append("AGH.AGH_ESPECIALIDADES ESP,");
		hql.append("AGH.AAC_GRADE_AGENDAMEN_CONSULTAS GRD,");
		hql.append("AGH.AAC_CONSULTAS CON");
		hql.append(" WHERE CON.DT_CONSULTA BETWEEN TO_DATE('"
				+DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtInicial()), "dd/MM/yyyy")+"','dd/mm/yyyy') AND");
		hql.append(" (TO_DATE('"+
				DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtFinal()), "dd/MM/yyyy")+"','dd/mm/yyyy') + 1)");
		if(isOracle()){
			hql.append(" AND con.fag_caa_seq+0 = nvl(this_.v_caa_seq,con.fag_caa_seq) ")
			.append(" AND con.fag_pgd_seq+0 = nvl(this_.v_pgd_seq,con.fag_pgd_seq) ")
			.append(" AND con.fag_tag_seq+0 = nvl(this_.v_tag_seq,con.fag_tag_seq) ")
			.append(" AND con.ind_sit_consulta = nvl(this_.vsit_ind_sit_cons,con.ind_sit_consulta) ");
		}
		else{
			hql.append(" AND con.fag_caa_seq+0 = coalesce(this_.v_caa_seq,con.fag_caa_seq) ")
			.append(" AND con.fag_pgd_seq+0 = coalesce(this_.v_pgd_seq,con.fag_pgd_seq) ")
			.append(" AND con.fag_tag_seq+0 = coalesce(this_.v_tag_seq,con.fag_tag_seq) ")
			.append(" AND con.ind_sit_consulta = coalesce(this_.vsit_ind_sit_cons,con.ind_sit_consulta) ");
		}
		hql.append(" AND con.grd_seq = grd.seq")
		.append(" AND esp.seq = grd.esp_seq")
		.append(" AND esp.seq = this_.v_esp_seq and");

		if(filtro.getEspecialidade()!= null){
			hql.append(" this_.v_esp_seq = "+filtro.getEspecialidade().getSeq()+" AND");
		}
		if(filtro.getCondicaoAtendimento()!=null){
			hql.append(" this_.V_CAA_SEQ = "+filtro.getCondicaoAtendimento().getSeq()+" AND");
		}
		if(filtro.getPagador() != null){
			hql.append(" this_.V_PGD_SEQ  = "+filtro.getPagador().getSeq()+" AND");
		}
		if(filtro.getAutorizacao() != null){
			hql.append(" this_.V_TAG_SEQ  = "+filtro.getAutorizacao().getSeq()+" AND");
		}
		if(filtro.getSituacao() != null){
			hql.append(" this_.vsit_ind_sit_cons like '"+filtro.getSituacao()+"'"+" AND");
		}
		hql.append(" EXISTS ( ")
		.append("SELECT grd_.esp_seq ")
		.append("FROM agh.aac_grade_agendamen_consultas grd_ ")
		.append("INNER JOIN agh.aac_consultas con1_ ")
		.append("ON grd_.seq=con1_.grd_seq");
		
		hql.append(" WHERE CON1_.DT_CONSULTA BETWEEN TO_DATE('"+DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtInicial()), "dd/MM/yyyy")+"','dd/mm/yyyy') AND");
		hql.append(" (TO_DATE('"+DateUtil.obterDataFormatada(DateUtil.truncaData(filtro.getDtFinal()), "dd/MM/yyyy")+"','dd/mm/yyyy') + 1)");
		
		hql.append(" AND con1_.fag_caa_seq=this_.v_caa_seq")
		.append(" AND con1_.fag_pgd_seq=this_.v_pgd_seq")
		.append(" AND con1_.ind_sit_consulta=this_.vsit_ind_sit_cons")
		.append(" AND grd_.esp_seq=this_.v_esp_seq)")
		.append(" GROUP BY grd.esp_seq, esp.sigla,")
		
		.append(" esp.nome_especialidade, esp.nome_reduzido,")
		.append(" con.fag_caa_seq, con.fag_pgd_seq,")
		.append(" con.fag_tag_seq, con.ind_sit_consulta) qtde ");
		final Query query = createSQLQuery(hql.toString());
		
		return query.list();
	}
}