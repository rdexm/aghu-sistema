package br.gov.mec.aghu.exames.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoProjetoPesquisa;
import br.gov.mec.aghu.model.AelProjetoEquipe;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.MbcCirurgias;

/**
 * 
 * @author diego.pacheco
 *
 */
public class AelProjetoPesquisasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProjetoPesquisas> {
	
	private static final long serialVersionUID = -6859841860790099084L;


	public List<AelProjetoPesquisas> pesquisarTodosProjetosPesquisa(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa)))
			);
		}
		
		criteria.addOrder(Order.asc(AelProjetoPesquisas.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarTodosProjetosPesquisaCount(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa)))
			);
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaPorNumeroOuNome(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa)));
		}
		
		criteria.addOrder(Order.asc(AelProjetoPesquisas.Fields.NUMERO.toString()));
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}

	public List<AelProjetoPesquisas> pesquisarProjetosPesquisa(String strPesquisa) {
		final Date dataAtual = new Date();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);		

		//(L_PJQ.dt_fim is null or L_PJQ.dt_fim > sysdate)
		Criterion criterionDataFimNull = Restrictions.isNull(AelProjetoPesquisas.Fields.DATA_FIM.toString());
		Criterion criterionDataFimMaiorQueAtual = Restrictions.gt(AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataAtual);
		criteria.add(Restrictions.or(criterionDataFimNull,criterionDataFimMaiorQueAtual));
		
		//L_PJQ.situacao in  ('01','11','12')
		DominioSituacaoProjetoPesquisa [] dominios = 
			{DominioSituacaoProjetoPesquisa.APROVADO,DominioSituacaoProjetoPesquisa.REAPROVADO,DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004};
		criteria.add(Restrictions.in(AelProjetoPesquisas.Fields.SITUACAO.toString(), dominios));
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa)))
			);
		}
		
		//(L_PJQ.dt_fim is null or L_PJQ.dt_fim > sysdate)
		criteria.add(Restrictions.or(criterionDataFimNull,criterionDataFimMaiorQueAtual));
		
		//L_PJQ.situacao in  ('01','11','12')
		criteria.add(Restrictions.in(AelProjetoPesquisas.Fields.SITUACAO.toString(), dominios));
		
		criteria.addOrder(Order.asc(AelProjetoPesquisas.Fields.NOME.toString()));
		
		//Executa primeira parte da query
		return executeCriteria(criteria);
	}
	
	public AelProjetoPesquisas obterProjetoPesquisaSituacaoData(Integer pjqSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		criteria.add(Restrictions.eq(AelProjetoPesquisas.Fields.SEQ.toString(),pjqSeq));
		
		DominioSituacaoProjetoPesquisa [] dominios = 
		{DominioSituacaoProjetoPesquisa.APROVADO,DominioSituacaoProjetoPesquisa.REAPROVADO,DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004};
		criteria.add(Restrictions.in(AelProjetoPesquisas.Fields.SITUACAO.toString(), dominios));
		Date dataAtualInicio = DateUtil.truncaData(new Date());
		Date dataAtualFim = DateUtil.truncaDataFim(new Date());
		criteria.add(Restrictions.le(AelProjetoPesquisas.Fields.DATA_INICIO.toString(),dataAtualFim));
		
		Criterion criterionDataFimNull = Restrictions.isNull(AelProjetoPesquisas.Fields.DATA_FIM.toString());
		Criterion criterionDataFimMaiorQueAtual = Restrictions.ge(AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataAtualInicio);
		criteria.add(Restrictions.or(criterionDataFimNull,criterionDataFimMaiorQueAtual));
		
		return (AelProjetoPesquisas) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa de projeto de pesquisa por descrição ou código
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(String strPesquisa, Integer codigoPaciente) {
		final Date dataAtual = new Date();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion cNome = Restrictions.or(
					Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(AelProjetoPesquisas.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			criteria.add(cNome);
		}

		criteria.add(Restrictions.eq(AelProjetoPesquisas.Fields.PERMITE_INTERNACAO.toString(), DominioSimNao.S));

		// (L_PJQ.dt_fim is null or L_PJQ.dt_fim > sysdate)
		Criterion criterionDataFimNull = Restrictions.isNull(AelProjetoPesquisas.Fields.DATA_FIM.toString());
		Criterion criterionDataFimMaiorQueAtual = Restrictions.gt(AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataAtual);
		criteria.add(Restrictions.or(criterionDataFimNull, criterionDataFimMaiorQueAtual));

		// L_PJQ.situacao in ('01','11','12')
		Criterion criterionSituacaoParte1 = Restrictions.or(
				Restrictions.eq(AelProjetoPesquisas.Fields.SITUACAO.toString(), DominioSituacaoProjetoPesquisa.APROVADO),
				Restrictions.eq(AelProjetoPesquisas.Fields.SITUACAO.toString(), DominioSituacaoProjetoPesquisa.REAPROVADO));
		Criterion criterionSituacaoParte2 = Restrictions.eq(AelProjetoPesquisas.Fields.SITUACAO.toString(),
				DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004);
		criteria.add(Restrictions.or(criterionSituacaoParte1, criterionSituacaoParte2));

		// and PPJ.pac_codigo = :INT.PAC_CODIGO
		DetachedCriteria criteriaProjetosPaciente = criteria.createCriteria("projetosPacientes", Criteria.INNER_JOIN);
		criteriaProjetosPaciente.add(Restrictions.eq(AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigoPaciente));

		// Executa primeira parte da query
		return executeCriteria(criteria);
	}

	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacaoNumero(String strPesquisa,
			DominioSituacaoProjetoPesquisa[] dominios) {
		final Date dataAtual = new Date();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa));
		}
		// (L_PJQ.dt_fim is null or L_PJQ.dt_fim > sysdate)
		Criterion criterionDataFimNull = Restrictions.isNull(AelProjetoPesquisas.Fields.DATA_FIM.toString());
		Criterion criterionDataFimMaiorQueAtual = Restrictions.gt(AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataAtual);
		criteria.add(Restrictions.or(criterionDataFimNull, criterionDataFimMaiorQueAtual));

		// L_PJQ.situacao in ('01','11','12')
		criteria.add(Restrictions.in(AelProjetoPesquisas.Fields.SITUACAO.toString(), dominios));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaProjetosPesquisaAgendaCirurgiaPorNumeroNome(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		String strPesquisa = objPesquisa != null ? objPesquisa.toString() : null; 
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (StringUtils.isNumeric(strPesquisa)) {
				criteria.add(Restrictions.eq(AelProjetoPesquisas.Fields.NUMERO.toString(), strPesquisa));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(AelProjetoPesquisas.Fields.NOME.toString(), strPesquisa.trim(), MatchMode.ANYWHERE),
						Restrictions.ilike(AelProjetoPesquisas.Fields.DESCRICAO.toString(), strPesquisa.trim(), MatchMode.ANYWHERE)));
			}
		}

		// (L_PJQ.dt_fim is null or L_PJQ.dt_fim > sysdate)
		Criterion criterionDataFimNull = Restrictions.isNull(AelProjetoPesquisas.Fields.DATA_FIM.toString());
		final Date dataAtual = new Date();
		Criterion criterionDataFimMaiorQueAtual = Restrictions.gt(AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataAtual);
		criteria.add(Restrictions.or(criterionDataFimNull, criterionDataFimMaiorQueAtual));

		// L_PJQ.situacao in ('01','11','12')
		DominioSituacaoProjetoPesquisa[] dominios = { DominioSituacaoProjetoPesquisa.APROVADO, DominioSituacaoProjetoPesquisa.REAPROVADO,
				DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004 };
		criteria.add(Restrictions.in(AelProjetoPesquisas.Fields.SITUACAO.toString(), dominios));

		return criteria;
	}
	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNome(Object strPesquisa) {
		DetachedCriteria criteria = this.obterCriteriaProjetosPesquisaAgendaCirurgiaPorNumeroNome(strPesquisa);
		return executeCriteria(criteria, 0, 100, AelProjetoPesquisas.Fields.NUMERO.toString(), true);
	}
	
	public Long pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNomeCount(Object strPesquisa) {
		DetachedCriteria criteria = this.obterCriteriaProjetosPesquisaAgendaCirurgiaPorNumeroNome(strPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	public AelProjetoPesquisas obterProjetosPesquisaPorSeqComConvenioSaude(Integer pjqSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class);
		criteria.createAlias(AelProjetoPesquisas.Fields.CONVENIO_SAUDE.toString(), AelProjetoPesquisas.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(AelProjetoPesquisas.Fields.SEQ.toString(), pjqSeq));
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return null;
		}
		else {
			return (AelProjetoPesquisas) obj;
		}
	}
	
	/**
	 * Efetua busca de AelProjetoPesquisas
	 * Consulta C8 #18527
	 * @param dcgCrgSeq
	 * @return
	 */
	public AelProjetoPesquisas buscarProjetoPesquisas(Integer dcgCrgSeq){
	
	DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class, "pjq");
	criteria.createAlias("pjq." + AelProjetoPesquisas.Fields.CIRURGIAS.toString(), "crg", Criteria.INNER_JOIN);
	criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.SEQ.toString(), dcgCrgSeq));
	
	return (AelProjetoPesquisas) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 *Consulta Projetos
	 *@ORADB AIPC_VER_MONITOR_PRJ.SQL
	 *Cursor: c_proj
	 *@param pacCodigo, matricula, vinCodigo, dataInicio, dataFim 
	 *@return ListaProjetoPesquisa 
	 */
	public List<AelProjetoPesquisas> pesquisarProjetosPacientes(Integer pacCodigo, Integer matricula, short vinCodigo, Date dataInicio, Date dataFim){
		
		Calendar today = Calendar.getInstance();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "ppj");
		criteria.createAlias("ppj." + AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "pjq");
		criteria.createAlias("pjq." + AelProjetoPesquisas.Fields.PROJETO_EQUIPES.toString(), "pje");
		
		criteria.add(Restrictions.eq("ppj."+AelProjetoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.le("ppj."+AelProjetoPacientes.Fields.DT_INICIO.toString(), dataInicio));
		// NVL(ppj.dt_fim,SYSDATE) >= c_dataf
		
		Criterion restricaoPaciente1 = Restrictions.and(Restrictions.isNotNull("ppj."+AelProjetoPacientes.Fields.DT_FIM.toString()), Restrictions.ge("ppj."+AelProjetoEquipe.Fields.DT_FIM.toString(), dataFim));
		Criterion restricaoPaciente2 = Restrictions.and(Restrictions.isNull("ppj."+AelProjetoPacientes.Fields.DT_FIM.toString()),Restrictions.sqlRestriction(today.getTime().getTime()+"  >= '" + dataFim.getTime()  +"'"));
		criteria.add(Restrictions.or(restricaoPaciente1, restricaoPaciente2));
		
		criteria.add(Restrictions.eq("pje."+AelProjetoEquipe.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("pje."+AelProjetoEquipe.Fields.SERVIDOR_VIN_CODIGO.toString(), vinCodigo));
		criteria.add(Restrictions.le("pje."+AelProjetoEquipe.Fields.DT_INICIO.toString(), dataInicio));
		//NVL(pje.dt_fim,SYSDATE) >= c_dataf
		
		Criterion restricaoEquipe1 = Restrictions.and(Restrictions.isNotNull("pje."+AelProjetoEquipe.Fields.DT_FIM.toString()), Restrictions.ge("pje."+AelProjetoEquipe.Fields.DT_FIM.toString(), dataFim));
		Criterion restricaoEquipe2 = Restrictions.and(Restrictions.isNull("pje."+AelProjetoEquipe.Fields.DT_FIM.toString()),Restrictions.sqlRestriction(today.getTime().getTime()+" >= '" + dataFim.getTime() +"'"));
		criteria.add(Restrictions.or(restricaoEquipe1, restricaoEquipe2));
		
		criteria.add(Restrictions.le("pjq."+AelProjetoPesquisas.Fields.DATA_INICIO.toString(), dataInicio));
		// NVL(pjq.dt_fim,SYSDATE) >= c_dataf
		Criterion restricaoPesquisa1 = Restrictions.and(Restrictions.isNotNull("pjq."+AelProjetoPesquisas.Fields.DATA_FIM.toString()), Restrictions.ge("pjq."+AelProjetoPesquisas.Fields.DATA_FIM.toString(), dataFim));
		Criterion restricaoPesquisa2 = Restrictions.and(Restrictions.isNull("pjq."+AelProjetoPesquisas.Fields.DATA_FIM.toString()), Restrictions.sqlRestriction(today.getTime().getTime()+"  >= '" + dataFim.getTime() +"'"));
		criteria.add(Restrictions.or(restricaoPesquisa1, restricaoPesquisa2));
		
		
		return executeCriteria(criteria);

	}

	public Long pesquisarProjetosPesquisaPorNumeroOuNomeCount(String parametro) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelProjetoPesquisas.class);

		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					AelProjetoPesquisas.Fields.NOME.toString(), parametro,
					MatchMode.ANYWHERE), Restrictions.eq(
					AelProjetoPesquisas.Fields.NUMERO.toString(), parametro)));
		}
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 *  #42229 P3 CURSOR c_pjq
	 */
	
	 
	public AelProjetoPesquisas pesquisarNrConsultaLibVoucher(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPesquisas.class, "APP");
		StringBuilder nroConsultaLib = new StringBuilder(100);

		boolean isOracle = isOracle();
		if(isOracle){
			nroConsultaLib.append(" NVL({alias}.NRO_CONSULTAS_LIB,0) nroConsultasLib");
		}else{
			nroConsultaLib.append(" COALESCE({alias}.NRO_CONSULTAS_LIB,0) nroConsultasLib");
		}
	    ProjectionList projList = Projections.projectionList();
		projList.add(Projections.sqlProjection(nroConsultaLib.toString(), new String [] {"nroConsultasLib"}, new Type[] {new IntegerType()}));
		projList.add(Projections.property("APP." + AelProjetoPesquisas.Fields.IND_VOUCHER_ELETRONICO.toString()), "voucherEletronico");
		projList.add(Projections.property("APP." + AelProjetoPesquisas.Fields.SEQ.toString()), "seq");
		criteria.setProjection(projList);
		criteria.add(Restrictions.eq("APP."+AelProjetoPesquisas.Fields.SEQ.toString(), numero));
		criteria.setResultTransformer(Transformers.aliasToBean(AelProjetoPesquisas.class));
		return (AelProjetoPesquisas) executeCriteriaUniqueResult(criteria);
	}
	
}
