package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.ArquivoSecretariaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameResuNotificacao;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.model.VAelSolicAtends;

public class VAelExameMatAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelExameMatAnalise> {

	private static final long serialVersionUID = -7325867271193999945L;

	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePelaSigla(final String siglaDescViewExaMatAnalise, final String indDependente) {

		final DetachedCriteria criteria = criarCriteriaVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise, indDependente);
		criteria.addOrder(Order.asc(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}

    public Long buscarVAelExameMatAnaliseCount(final String siglaDescViewExaMatAnalise) {

            Long resultadoCount = executeCriteriaCount(this.criarCriteriaVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise, null));

            if (resultadoCount == 0) {
                resultadoCount = executeCriteriaCount(this.criarVAelExameMatAnalisePorDescricao(siglaDescViewExaMatAnalise, null));
            }
            return resultadoCount;
        }

	private DetachedCriteria criarCriteriaVAelExameMatAnalisePelaSigla(final String siglaDescViewExaMatAnalise, final String indDependente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);

        if (StringUtils.isNotEmpty(siglaDescViewExaMatAnalise)) {
            final Object[] values = {siglaDescViewExaMatAnalise.toLowerCase(), siglaDescViewExaMatAnalise.toUpperCase()};
            criteria.add(Restrictions.in(VAelExameMatAnalise.Fields.SIGLA.toString(), values));
        }
		if (StringUtils.isNotEmpty(indDependente)) {
			criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.IND_DEPENDENTE.toString(), indDependente));
		}

		return criteria;
	}
	
	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePorDescricaoLimitado(String siglaDescViewExaMatAnalise, final String indDependente) {
		final DetachedCriteria criteria = criarVAelExameMatAnalisePorDescricao(siglaDescViewExaMatAnalise, indDependente);
		criteria.addOrder(Order.asc(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	private DetachedCriteria criarVAelExameMatAnalisePorDescricao(final String siglaDescViewExaMatAnalise, final String indDependente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		criteria.add(Restrictions.ilike(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString(), siglaDescViewExaMatAnalise, MatchMode.ANYWHERE));
		if(StringUtils.isNotEmpty(indDependente)){
			criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.IND_DEPENDENTE.toString(), indDependente));
		}
		
		return criteria;
	}
	
	public List<VAelExameMatAnalise> buscaVAelExameMatAnalisePorDescricao(String siglaDescViewExaMatAnalise, String indDependente) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		if (StringUtils.isNotEmpty(siglaDescViewExaMatAnalise)) {

            criteria.add(Restrictions.ilike(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString(), "%" + siglaDescViewExaMatAnalise + "%"));

        }
		if (StringUtils.isNotEmpty(indDependente)) {
			criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.IND_DEPENDENTE.toString(), indDependente));
		}
		criteria.addOrder(Order.asc(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()));
		
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public VAelExameMatAnalise buscarVAelExameMatAnalisePorExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		CoreUtil.validaParametrosObrigatorios(exameMaterialAnalise, exameMaterialAnalise.getId());
		return this.buscarVAelExameMatAnalisePelaSiglaESeq(exameMaterialAnalise.getId().getExaSigla(), exameMaterialAnalise.getId().getManSeq());
	}
	
	public VAelExameMatAnalise buscarVAelExameMatAnalisePelaSiglaESeq(String exaSigla, Integer manSeq) {
		if (exaSigla == null || "".equals(exaSigla.trim())) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.MAN_SEQ.toString(), manSeq));
		
		return (VAelExameMatAnalise)this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<VAelExameMatAnalise> pesquisarVAelExameMatAnalisePelaSiglaSeq(Object siglaSeq) {
		String strPesquisa = (String) siglaSeq;
						
		DetachedCriteria criteria = this.montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaSeq(strPesquisa);
		
		criteria.addOrder(Order.asc(VAelExameMatAnalise.Fields.SIGLA.toString()));
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarVAelExameMatAnalisePelaSiglaSeqCount(Object siglaSeq) {
		String strPesquisa = (String) siglaSeq;
		
		DetachedCriteria criteria = this.montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaSeq(strPesquisa);
		
		return this.executeCriteriaCount(criteria);
		
	}
	
	private DetachedCriteria montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaSeq(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.MAN_SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if(StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(
				Restrictions.ilike(VAelExameMatAnalise.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE),
				Restrictions.ilike(VAelExameMatAnalise.Fields.DESCRICAO_EXAME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria;
	}
	
	private DetachedCriteria montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaDescricao(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.MAN_SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if(StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.or(
					Restrictions.ilike(VAelExameMatAnalise.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(VAelExameMatAnalise.Fields.NOME_USUAL_EXAME.toString(), strPesquisa, MatchMode.ANYWHERE)), Restrictions.ilike(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria;
	}
	
	public List<String> pesquisarNomeUsualExameMatAnalisePorSiglaEMaterial(String sigla, Integer matExame){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()));
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(VAelExameMatAnalise.Fields.MAN_SEQ.toString(), matExame));
		
		return this.executeCriteria(criteria);
	}
	
	public List<VAelExameMatAnalise> buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(String siglaDescViewExaMatAnalise) {
		DetachedCriteria criteria = obterCriteriaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(siglaDescViewExaMatAnalise);
		
		return this.executeCriteria(criteria, 0, 100, VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString(), true);
	}

	public Long buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount(String siglaDescViewExaMatAnalise) {
		//return this.executeCriteria(obterCriteriaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(siglaDescViewExaMatAnalise)).size();
		return this.executeCriteriaCount(obterCriteriaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(siglaDescViewExaMatAnalise));
	}
	
	private DetachedCriteria obterCriteriaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise(String siglaDescViewExaMatAnalise) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExameMatAnalise.class);
		
		if (StringUtils.isNotEmpty(siglaDescViewExaMatAnalise)) {
			final Object[] values = { siglaDescViewExaMatAnalise.toLowerCase(), siglaDescViewExaMatAnalise.toUpperCase() };
			
			Criterion rest1 = Restrictions.in(VAelExameMatAnalise.Fields.SIGLA.toString(), values);
			Criterion rest2 = Restrictions.ilike(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString(), "%"+siglaDescViewExaMatAnalise+"%");
			
			criteria.add(Restrictions.or(rest1, rest2));
		}
		
		return criteria;
	}
	
	public List<VAelExameMatAnalise> pesquisarVAelExameMatAnalisePelaSiglaDescricao(Object siglaSeq) {
		String strPesquisa = (String) siglaSeq;
	
		DetachedCriteria criteria = this.montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaDescricao(strPesquisa);
		
		criteria.addOrder(Order.asc(VAelExameMatAnalise.Fields.NOME_USUAL_EXAME.toString()));
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarVAelExameMatAnalisePelaSiglaDescricaoCount(Object siglaSeq) {
		String strPesquisa = (String) siglaSeq;
	
		DetachedCriteria criteria = this.montarCriteriaPesquisarVAelExameMatAnalisePelaSiglaDescricao(strPesquisa);
		
		return this.executeCriteriaCount(criteria);
	}

	public List<VAelExameMatAnalise> buscarVAelExameMatAnalisePorSiglaDescViewExaMatAnalise(final String siglaDescViewExaMatAnalise, final String indDependente) throws ApplicationBusinessException {
		List<VAelExameMatAnalise> lista = null;
		if(StringUtils.isNotEmpty(siglaDescViewExaMatAnalise)) {
			lista = this.buscarVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise, indDependente);
		}
 		
		if (lista == null || lista.isEmpty()) {
			lista = this.buscarVAelExameMatAnalisePorDescricaoLimitado(siglaDescViewExaMatAnalise, indDependente);			
		}
		
		return lista;
	}
	
	public Long contarVAelExameMatAnalisePelaSiglaDescExameDescViewExaMatAnalise(final String valPesquisa, final String indDependente) {
		Long returno = 0l;
		if(StringUtils.isNotEmpty(valPesquisa)) {
			returno = this.executeCriteriaCount(criarCriteriaVAelExameMatAnalisePelaSigla(valPesquisa, indDependente));
		}
 		
		if (returno == 0) {
			returno = this.executeCriteriaCount(this.criarVAelExameMatAnalisePorDescricao(valPesquisa, indDependente));			
		}
		
		return returno;
	}
	
	@SuppressWarnings("unchecked")
	public List<ArquivoSecretariaVO> pesquisarDadosArquivoSecretaria(Date dataInicio, Date dataFim){
		
		StringBuilder sql = new StringBuilder(600);
		
		sql.append("SELECT ");
		sql.append("  vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.name())   .append(" as ").append(ArquivoSecretariaVO.Fields.EXAME.toString());
		sql.append(" ,soe.").append(AelSolicitacaoExames.Fields.SEQ.name())				 	 .append(" as ").append(ArquivoSecretariaVO.Fields.SOLICITACAO.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.ISE_SEQP.name())			     .append(" as ").append(ArquivoSecretariaVO.Fields.ITEM.toString());
		sql.append(" ,ern.").append(AelExameResuNotificacao.Fields.RCD_GTC_SEQ.name())       .append(" as ").append(ArquivoSecretariaVO.Fields.RCD_GTC_SEQ.toString());
		sql.append(" ,ern.").append(AelExameResuNotificacao.Fields.RCD_SEQP.name())		 	 .append(" as ").append(ArquivoSecretariaVO.Fields.RCD_SEQP.toString());
		sql.append(" ,pac.").append(AipPacientes.Fields.CODIGO.name())					 	 .append(" as ").append(ArquivoSecretariaVO.Fields.PAC_CODIGO.toString());
		sql.append(" ,ern.").append(AelExameResuNotificacao.Fields.RESULTADO_NUM_EXP.name()) .append(" as ").append(ArquivoSecretariaVO.Fields.RESULTADO_NUM_EXP.toString());
		sql.append(" ,ern.").append(AelExameResuNotificacao.Fields.RESULTADO_ALFANUM.name()) .append(" as ").append(ArquivoSecretariaVO.Fields.RESULTADO_ALFA_NUM.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.name())	 .append(" as ").append(ArquivoSecretariaVO.Fields.PCL_VEL_EMA_EXA_SIGLA.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.name())	 .append(" as ").append(ArquivoSecretariaVO.Fields.PCL_VEL_EMA_MAN_SEQ.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.PCL_VEL_SEQP.name())			 .append(" as ").append(ArquivoSecretariaVO.Fields.PCL_VEL_SEQP.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.PCL_CAL_SEQ.name())			 .append(" as ").append(ArquivoSecretariaVO.Fields.PCL_CAL_SEQ.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.PCL_SEQP.name())				 .append(" as ").append(ArquivoSecretariaVO.Fields.PCL_SEQP.toString());
		sql.append(" ,ree.").append(AelResultadoExame.Fields.SEQP.name())					 .append(" as ").append(ArquivoSecretariaVO.Fields.SEQP.toString());
		sql.append(" ,ise.").append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.name())	 .append(" as ").append(ArquivoSecretariaVO.Fields.DATA_HORA_LIBERADA.toString());
		
		sql.append(" FROM ");
		sql.append("  AGH.").append(VAelExameMatAnalise.class.getAnnotation(Table.class).name()).append(" vem ");
		sql.append(" ,AGH.").append(AipPacientes.class.getAnnotation(Table.class).name()).append(" pac ");
		sql.append(" ,AGH.").append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" atd ");
		sql.append(" ,AGH.").append(AelResultadoExame.class.getAnnotation(Table.class).name()).append(" ree ");
		sql.append(" ,AGH.").append(AelExameResuNotificacao.class.getAnnotation(Table.class).name()).append(" ern ");
		sql.append(" ,AGH.").append(AelExamesNotificacao.class.getAnnotation(Table.class).name()).append(" exn ");
		sql.append(" ,AGH.").append(AelItemSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" ise ");
		sql.append(" ,AGH.").append(AelSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" soe ");
		
		sql.append(" WHERE (");
		sql.append(" ise.").append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.name())		.append(" BETWEEN :dtInicio AND :dtFim)");
		sql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.name())		.append("='LI'");
		sql.append(" AND exn.").append(AelExamesNotificacao.Fields.EMA_EXA_SIGLA.name())		.append("= ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.name());
		sql.append(" AND exn.").append(AelExamesNotificacao.Fields.EMA_MAN_SEQ.name())			.append("= ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.name());
		sql.append(" AND exn.").append(AelExamesNotificacao.Fields.IND_SITUACAO.name())			.append("='").append(DominioSituacao.A).append('\'');
		sql.append(" AND ern.").append(AelExameResuNotificacao.Fields.EXN_EMA_EXA_SIGLA.name())	.append('=').append("exn.").append(AelExamesNotificacao.Fields.EMA_EXA_SIGLA.name());
		sql.append(" AND ern.").append(AelExameResuNotificacao.Fields.EXN_EMA_MAN_SEQ.name())	.append('=').append("exn.").append(AelExamesNotificacao.Fields.EMA_MAN_SEQ.name());
		sql.append(" AND ern.").append(AelExameResuNotificacao.Fields.EXN_CAL_SEQ.name())		.append('=').append("exn.").append(AelExamesNotificacao.Fields.CAL_SEQ.name());
		sql.append(" AND ern.").append(AelExameResuNotificacao.Fields.IND_SITUACAO.name())		.append("='").append(DominioSituacao.A).append('\'');
		sql.append(" AND ree.").append(AelResultadoExame.Fields.ISE_SOE_SEQ.name())				.append('=').append("ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name());
		sql.append(" AND ree.").append(AelResultadoExame.Fields.ISE_SEQP.name())				.append('=').append("ise.").append(AelItemSolicitacaoExames.Fields.SEQP.name());
		sql.append(" AND ree.").append(AelResultadoExame.Fields.IND_ANULACAO_LAUDO.name())			.append("='").append(DominioSimNao.N.toString()).append('\'');
		sql.append(" AND ree.").append(AelResultadoExame.Fields.PCL_CAL_SEQ.name())				.append('=').append("ern.").append(AelExameResuNotificacao.Fields.EXN_CAL_SEQ.name());
		sql.append(" AND soe.").append(AelSolicitacaoExames.Fields.SEQ.name())					.append('=').append("ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name());
		sql.append(" AND atd.").append(AghAtendimentos.Fields.SEQ.name())						.append('=').append("soe.").append(AelSolicitacaoExames.Fields.ATD_SEQ.name());
		sql.append(" AND PAC.").append(AipPacientes.Fields.CODIGO.name())						.append('=').append("atd.").append(AghAtendimentos.Fields.PAC_CODIGO.name());
		sql.append(" AND vem.").append(VAelExameMatAnalise.Fields.SIGLA.name())					.append('=').append("exn.").append(AelExamesNotificacao.Fields.EMA_EXA_SIGLA.name());
		sql.append(" AND vem.").append(VAelExameMatAnalise.Fields.MAN_SEQ.name())				.append('=').append("exn.").append(AelExamesNotificacao.Fields.EMA_MAN_SEQ.name());
		
		//order by data liberacao asc
		sql.append(" Order by ise.").append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.name());
		SQLQuery query = createSQLQuery(sql.toString());
		query.setTimestamp("dtInicio", DateUtil.obterDataComHoraInical(dataInicio));
		query.setTimestamp("dtFim", DateUtil.obterDataComHoraFinal(dataFim));

		query.setResultTransformer(Transformers.aliasToBean(ArquivoSecretariaVO.class));
		
		List<ArquivoSecretariaVO> result = query.addScalar(ArquivoSecretariaVO.Fields.EXAME.toString(), StringType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.SOLICITACAO.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.ITEM.toString(), ShortType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.RCD_GTC_SEQ.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.RCD_SEQP.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PAC_CODIGO.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.RESULTADO_NUM_EXP.toString(), LongType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.RESULTADO_ALFA_NUM.toString(), StringType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), StringType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PCL_VEL_EMA_MAN_SEQ.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PCL_VEL_SEQP.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PCL_CAL_SEQ.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.PCL_SEQP.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.SEQP.toString(), IntegerType.INSTANCE)
												.addScalar(ArquivoSecretariaVO.Fields.DATA_HORA_LIBERADA.toString(), DateType.INSTANCE)
												.list();
		return result;
	}
	
	public List<RelatorioMateriaisRecebidosNoDiaVO> pesquisarRelatorioMateriaisRecebidosNoDia(Date dataHoraEventoIni, Date dataHoraEventoFim, Short unfSeq, String codigoAelSitItemSolic){
		StringBuilder hql = new StringBuilder(600);
		 hql.append("SELECT ");
		 hql.append(" eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.DATA_HORA_EVENTO.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.SOE_SEQ.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.SEQP.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.ATD_PRONTUARIO.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.LEITO.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.PAC_NOME.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.PAC_NOME.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ORIGEM.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.ORIGEM.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.IND_EXIGE_DESC_MAT_ANLS.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.IND_EXIGE_DESC_MAT_ANLS.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.NOMEUSUALMATERIAL.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.DESCRICAO_EXAME.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.DESCRICAO_EXAME.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.DESC_MATERIAL_ANALISE.toString()).append(", ");
		 
		 hql.append("lum.").append(AelAnatomoPatologico.Fields.NUMERO_AP.toString()).append(" as ").append(RelatorioMateriaisRecebidosNoDiaVO.Fields.NUMERO_AP.toString());
		 
		 hql.append(" FROM ");
		 
		 hql.append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise,");
		 hql.append(VAelSolicAtends.class.getName()).append(" vas");
		 hql.append("	left join ise.exameMatAnalise vem " );
		 hql.append("	inner join ise.aelExtratoItemSolicitacao eis" );
		 hql.append("	left join ise.aelExameApItemSolic lul" );
		 hql.append("	left join lul.exameAp lux" );
		 hql.append("	left join lux.aelAnatomoPatologicos lum" );
		 hql.append(" where ");
		 
		 hql.append(" vas.").append(VAelSolicAtends.Fields.SEQ.toString()).append('=').append("ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		 hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()).append("=:unfSeq ");
		 hql.append(" AND eis.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()).append("=:codigoAelSitItemSolic ");
		 hql.append(" AND eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append(">= :dataHoraEventoIni ");
		 hql.append(" AND eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append("<= :dataHoraEventoFim ");
		 
		 hql.append(" group by ");
		 
		 hql.append(" eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.PAC_NOME.toString()).append(", ");
		 hql.append(" vas.").append(VAelSolicAtends.Fields.ORIGEM.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.IND_EXIGE_DESC_MAT_ANLS.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()).append(", ");
		 hql.append(" vem.").append(VAelExameMatAnalise.Fields.DESCRICAO_EXAME.toString()).append(", ");
		 hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()).append(", ");
		 
		 hql.append("lum.").append(AelAnatomoPatologico.Fields.NUMERO_AP.toString());
		 
		org.hibernate.Query q = createHibernateQuery(hql.toString());
		q.setTimestamp("dataHoraEventoIni", dataHoraEventoIni);
		q.setTimestamp("dataHoraEventoFim", dataHoraEventoFim);
		q.setInteger("unfSeq", unfSeq);
		q.setString("codigoAelSitItemSolic", codigoAelSitItemSolic);
			
		q.setResultTransformer(Transformers.aliasToBean(RelatorioMateriaisRecebidosNoDiaVO.class));
		return q.list();
	}
	
}
