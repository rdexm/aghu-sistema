package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.VMbcProfServidor;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMbcProfServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMbcProfServidor> {

	private static final long serialVersionUID = 8314239502314995398L;
	
	private final static String FROM = "from agh.v_mbc_prof_servidor profserv inner join agh.agh_prof_especialidades  profesp on profserv.SER_MATRICULA =  profesp.SER_MATRICULA and profserv.SER_VIN_CODIGO = profesp.SER_VIN_CODIGO inner join agh.agh_especialidades esp on esp.SEQ = profesp.ESP_SEQ ";
	private final static String WHERE = " where profserv.PUC_UNF_SEQ = :unfSeq and profserv.PUC_SITUACAO='A' and profserv.SER_MATRICULA= :serMat and profserv.SER_VIN_CODIGO= :serVin and profserv.PUC_IND_FUNCAO_PROF in ('MPF','MCO')";
	private final static String VPS = "vps.";
	
	public List<VMbcProfServidor> pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(String parametro, Short unfSeq, Integer maxResults) {
		DetachedCriteria criteria = montarCriteriaPesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(parametro, unfSeq);
		criteria.addOrder(Order.asc(VPS + VMbcProfServidor.Fields.NOME_USUAL_PESSOA.toString()));
	
		return executeCriteria(criteria, 0, maxResults, null, true);
	}
	
	public Long pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoaCount(String parametro, Short unfSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(parametro, unfSeq);		
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montarCriteriaPesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(String parametro, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProfServidor.class, "vps");
		
		criteria.add(Restrictions.eq(VPS + VMbcProfServidor.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in(VPS + VMbcProfServidor.Fields.FUNCAO.toString(),
				new Object[]{DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO}));
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(VPS + VMbcProfServidor.Fields.SER_MATRICULA.toString(), Integer.parseInt(parametro)));
			} else {
				criteria.add(Restrictions.ilike(VPS + VMbcProfServidor.Fields.NOME_PESSOA.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		 
		return criteria;
	}
	
	private void getCriteriaPesquisarProfServidor(Object pesquisaSuggestion,
			Short unfSeq, DominioSituacao situacao, DetachedCriteria criteria,
			DominioFuncaoProfissional... funcoesProfissionais) {
		getCriteriaPesquisaProfServidor(unfSeq, situacao, criteria,	funcoesProfissionais);
		
		getCriteriaPesquisaByMatriculaOrNomePessoa(pesquisaSuggestion, criteria);
	}
	
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByVMbcProfServ(Object pesquisaSuggestion, Short unfSeq,
			DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProfServidor.class, "vps");
		
		getCriteriaPesquisarProfServidor(pesquisaSuggestion, unfSeq, situacao, criteria, funcoesProfissionais);
		
		criteria.setProjection(getProjectionNomeMatVinCodUnf());
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		//criteria.addOrder(Order.asc(VMbcProfServidor.Fields.NOME_PESSOA.toString()));
		return executeCriteria(criteria, 0, 100, VMbcProfServidor.Fields.NOME_PESSOA.toString(), true);
	}
	
	public Long pesquisarNomeMatVinCodUnfByVMbcProfServCount(Object pesquisaSuggestion, Short unfSeq,
			DominioSituacao situacao,	DominioFuncaoProfissional... funcoesProfissionais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMbcProfServidor.class, "vps");
		
		getCriteriaPesquisarProfServidor(pesquisaSuggestion, unfSeq, situacao,
				criteria, funcoesProfissionais);
		
		criteria.setProjection(getProjectionNomeMatVinCodUnf());
		
		return executeCriteriaCount(criteria);
	}
	
	public Projection getProjectionNomeMatVinCodUnf(){
		return Projections.distinct(Projections.projectionList()
				.add(Projections.property(VMbcProfServidor.Fields.NOME_PESSOA	.toString()), 	LinhaReportVO.Fields.TEXTO1.toString())
				.add(Projections.property(VMbcProfServidor.Fields.SER_MATRICULA	.toString()), 	LinhaReportVO.Fields.NUMERO11.toString())
				.add(Projections.property(VMbcProfServidor.Fields.SER_VIN_CODIGO.toString()), 	LinhaReportVO.Fields.NUMERO4.toString())
				.add(Projections.property(VMbcProfServidor.Fields.UNF_SEQ		.toString()), 	LinhaReportVO.Fields.NUMERO5.toString())
				.add(Projections.property(VMbcProfServidor.Fields.IND_FUNCAO_PROF.toString()), 	LinhaReportVO.Fields.OBJECT.toString())
				);
	}
	
	private void getCriteriaPesquisaByMatriculaOrNomePessoa(
			Object pesquisaSuggestion, DetachedCriteria criteria) {
		if(pesquisaSuggestion != null && StringUtils.isNotBlank(pesquisaSuggestion.toString())){
			if (CoreUtil.isNumeroInteger(pesquisaSuggestion)) {
				criteria.add(Restrictions.eq(VPS + VMbcProfServidor.Fields.SER_MATRICULA.toString(), Integer.valueOf(pesquisaSuggestion.toString())));
			} else {
				criteria.add(Restrictions.ilike(VPS + VMbcProfServidor.Fields.NOME_PESSOA.toString(), (String)pesquisaSuggestion, MatchMode.ANYWHERE));
			}
		}
	}

	private void getCriteriaPesquisaProfServidor(Short unfSeq,
			DominioSituacao situacao, DetachedCriteria criteria,
			DominioFuncaoProfissional... funcoesProfissionais) {
		if(unfSeq != null){
			criteria.add(Restrictions.eq(VPS + VMbcProfServidor.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		if(funcoesProfissionais != null && funcoesProfissionais.length > 0){
			criteria.add(Restrictions.in(VPS + VMbcProfServidor.Fields.FUNCAO.toString(), funcoesProfissionais));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(VPS + VMbcProfServidor.Fields.SITUACAO.toString(), situacao));
		}
	}

	private SQLQuery criarSqlQueryEspecialidadeSubstituta(String param, LinhaReportVO equipeSubstituta, Boolean isCount) {
		StringBuilder sql = new StringBuilder(700);
		
		if(isCount){
			sql.append("select count(*) ");
		}else{
			sql.append("select esp.SIGLA as sigla , esp.NOME_ESPECIALIDADE as nomeEspecialidade, esp.SEQ as seq ");
		}
		
		sql.append(FROM)
		.append(WHERE);
		
		if(StringUtils.isNotBlank(param)){
			sql.append("and ((upper(esp.NOME_ESPECIALIDADE) like ") 
			.append("upper('%")
			.append(param)
			.append("%'))")
			.append("or (upper(esp.SIGLA) like upper('")
			.append(param)
			.append("'))) ");
			}

		if(isOracle() && !isCount){
			sql.append(" and rownum <= 100 ");
		}
		
		if(!isCount){
			sql.append(" order by profserv.PES_NOME asc ");
		}		
		 
		if(isPostgreSQL() && !isCount){
			sql.append(" limit  100 ");
		}
		
		SQLQuery query = createSQLQuery(sql.toString());
		
		Short unfSeq = -1;
		Integer serMat = -1;
		Short serVin = -1;
		
		if(equipeSubstituta != null){
			unfSeq = equipeSubstituta.getNumero5();
			serMat = equipeSubstituta.getNumero11();
			serVin = equipeSubstituta.getNumero4();
		}

		query.setShort("unfSeq", unfSeq);
		query.setInteger("serMat", serMat);
		query.setShort("serVin", serVin);

		if(!isCount){
			query.addScalar("sigla", StringType.INSTANCE);
			query.addScalar("nomeEspecialidade", StringType.INSTANCE);
			query.addScalar("seq", ShortType.INSTANCE);
					
			query.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		}
		
		return query;
	}
	
	public List<AghEspecialidades> pesquisarSiglaEspecialidadeSubstituta(String param, LinhaReportVO equipeSubstituta){
		SQLQuery query = criarSqlQueryEspecialidadeSubstituta(param, equipeSubstituta, Boolean.FALSE);
		return query.list();
	}

	public long pesquisarSiglaEspecialidadeSubstitutaCount(String param, LinhaReportVO equipeSubstituta){
		SQLQuery query = criarSqlQueryEspecialidadeSubstituta(param, equipeSubstituta, Boolean.TRUE);
				
		long longCount = 0L;
		
		Number count = (Number) query.uniqueResult();  
		
		if(count != null){
			longCount = count.longValue();
		}
		
		return longCount;
	}
	
}
