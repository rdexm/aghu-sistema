package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelNotasAdicionaisHist;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipPolMdtosAghuHist;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesHistoricoPOLVO;

public class AelItemSolicExameHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicExameHist> {

	private static final long serialVersionUID = -7987304197870955555L;

	@SuppressWarnings("unchecked")
	public List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimento(
			final Integer codigoPaciente, final Short unfSeq,
			final DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			final List<Integer> gruposMateriaisAnalises) {
		final StringBuilder hql = montaHqlComunBuscaExamesPeloCodigoPacienteSituacao();
		hql.append(" inner join sol." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString() + " atd ");
		//hql.append("where exists (");
		//hql.append(montaHqlSolicitacaoExamesAtd(situacaoItemExame, unfSeq));
		//hql.append(") ");
		
		hql.append(" where atd.").append(AghAtendimentos.Fields.PAC_CODIGO).append(" = :pCodigoPaciente ");
		if (unfSeq != null) {
			hql.append(" AND ise.").append(AelItemSolicExameHist.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
		}
		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "ise"));
		
		hql.append(" AND (adre.indAnulacaoDoc is null or adre.indAnulacaoDoc = ").append("'N')");
		if (gruposMateriaisAnalises != null && !gruposMateriaisAnalises.isEmpty()) {
			hql.append(" and (man.seq in (").append(" :pMateriaisXGruposAnalises ))");
		}
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("pCodigoPaciente", codigoPaciente);
		// verifica se o material análise pertence a lista de materiais análises
		// que são relacionados com gmaSeq
		if (gruposMateriaisAnalises != null && !gruposMateriaisAnalises.isEmpty()) {
			query.setParameterList("pMateriaisXGruposAnalises", gruposMateriaisAnalises);
		}
		if (unfSeq != null) {
			query.setParameter("pUfeUnfSeq", unfSeq);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ExamesHistoricoPOLVO.class));
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ExamesHistoricoPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(final Integer codigoPaciente, final Short unfSeq, final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final List<Integer> gruposMateriaisAnalises) {
		final StringBuilder hql = montaHqlComunBuscaExamesPeloCodigoPacienteSituacao();
		hql.append(" inner join ise." + AelItemSolicExameHist.Fields.ATENDIMENTO_DIVERSO.toString() + " atv ");
//		hql.append("where exists (");
//		hql.append(montaHqlSolicitacaoExamesAtv(situacaoItemExame, unfSeq));
//		hql.append(") ");
		
		hql.append(" WHERE atv.").append(AelAtendimentoDiversos.Fields.PAC_CODIGO).append(" = :pCodigoPaciente ");
		hql.append(" AND atv.").append(AelAtendimentoDiversos.Fields.PJQ_SEQ).append(" is not null ");
		if (unfSeq != null) {
			hql.append(" AND ise.").append(AelItemSolicExameHist.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
		}
		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "ise"));
		
		hql.append(" and (adre.indAnulacaoDoc is null or adre.indAnulacaoDoc = ").append("'N')");
		if (gruposMateriaisAnalises != null && !gruposMateriaisAnalises.isEmpty()) {
			hql.append(" and (man.seq in (").append(" :pMateriaisXGruposAnalises ))");
		}
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pCodigoPaciente", codigoPaciente);
		// verifica se o material análise pertence a lista de materiais análises
		// que são relacionados com gmaSeq
		if (gruposMateriaisAnalises != null && !gruposMateriaisAnalises.isEmpty()) {
			query.setParameterList("pMateriaisXGruposAnalises", gruposMateriaisAnalises);
		}
		if (unfSeq != null) {
			query.setParameter("pUfeUnfSeq", unfSeq);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ExamesHistoricoPOLVO.class));
		
		return query.list();
	}

	private StringBuilder montaHqlComunBuscaExamesPeloCodigoPacienteSituacao() {
		final StringBuilder hql = new StringBuilder(250);
		hql.append("select distinct ise as " ).append(ExamesHistoricoPOLVO.Fields.AEL_ITEM_SOLIC_EXAME_HIST.toString())
		.append(hqlMaxDtRecebimentoAelExtratoItemSolicHist())
		.append(hqlCountNotasAdicionaisHist())
		//.append(", adre as " ).append( ExamesHistoricoPOLVO.Fields.AEL_DOC_RESULTADO_EXAMES_HIST.toString()) //@FIXME:
		.append(" , man." ).append( AelMateriaisAnalises.Fields.DESCRICAO.toString() ).append( " as " ).append(  ExamesHistoricoPOLVO.Fields.DESCRICAO_MAT_ANALISE )
		.append(" , man." ).append( AelMateriaisAnalises.Fields.SEQ.toString() ).append( " as " ).append(  ExamesHistoricoPOLVO.Fields.SEQ_MAT_ANALISE )
		.append(" from ")
		.append(AelItemSolicExameHist.class.getSimpleName()).append(" ise ")
		.append(" inner join ise." ).append( AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString() ).append( " man ")
		.append(" inner join ise." ).append( AelItemSolicExameHist.Fields.AEL_EXAMES.toString() ).append( " exa ")
		.append(" inner join ise." ).append( AelItemSolicExameHist.Fields.UNIDADE_FUNCIONAL.toString() ).append( " unf ")
		.append(" inner join ise." ).append( AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString() ).append( " sol ")
		.append(" left outer join ise." ).append( AelItemSolicExameHist.Fields.DOC_RESULTADO_EXAME.toString() ).append( " adre ")
		.append(" left outer join ise." ).append( AelItemSolicExameHist.Fields.RESULTADO_EXAME.toString() ).append( " resExam ");
		/*hql.append(" inner join man."
				+ AelMateriaisAnalises.Fields.GRUPO_XMATERIAL
				+ " gxm ");
		
		hql.append(" inner join gxm."
				+ AelGrupoXMaterialAnalise.Fields.GRUPO_MATERIAL.toString()
				+ " gma ");*/
		
		return hql;
	}

	private String hqlCountNotasAdicionaisHist() {
		StringBuilder str = new StringBuilder(150);
		str.append(" , ( ")
		.append("select count(aelNotas.")
		.append(AelNotasAdicionaisHist.Fields.SEQP)
		.append(") ")
		.append("from ")
		.append(AelNotasAdicionaisHist.class.getSimpleName())
		.append(" aelNotas ")
		.append(" where aelNotas.")		
		.append(AelNotasAdicionaisHist.Fields.ISE_SOE_SEQ.toString())
		.append(" = ")
		.append("ise." ).append( AelItemSolicExameHist.Fields.SOE_SEQ.toString())
		.append(" and aelNotas.")
		.append(AelNotasAdicionaisHist.Fields.ISE_SEQP.toString())
		.append("  = ")
		.append("ise." ).append( AelItemSolicExameHist.Fields.SEQP.toString())
		.append(" ) ")
		.append(" as  " ).append( ExamesHistoricoPOLVO.Fields.QTDE_NOTA_ADICIONAL.toString());
		
		return str.toString();
	}

	private String hqlMaxDtRecebimentoAelExtratoItemSolicHist() {
		StringBuilder str = new StringBuilder(100);
		str.append(" , ( ")
		
		.append("select max(eis.")
		.append(AelExtratoItemSolicHist.Fields.DATA_HORA_EVENTO)
		.append(") ")
		.append("from ")
		.append(AelExtratoItemSolicHist.class.getSimpleName())
		.append(" eis ")
		.append(" where eis.")
		.append(AelExtratoItemSolicHist.Fields.ISE_SOE_SEQ.toString())
		.append(" = ")
		.append("ise." ).append( AelItemSolicExameHist.Fields.SOE_SEQ.toString())
		.append(" and eis.")
		.append(AelExtratoItemSolicHist.Fields.ISE_SEQP.toString())
		.append("  = ")
		.append("ise." ).append( AelItemSolicExameHist.Fields.SEQP.toString())
		.append(" and eis.")
		.append(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
		.append(" = '")
		.append(DominioSituacaoItemSolicitacaoExame.AE.name())

		.append("' ) ")
		.append(" as  " ).append( ExamesHistoricoPOLVO.Fields.DTHR_EVENTO_EXTRATO_ITEM.toString());
		
		return str.toString();
	}

//	private StringBuilder montaHqlSolicitacaoExamesAtd(final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final Short unfSeq) {
//		final StringBuilder hql = new StringBuilder();
//		hql.append("select isex.");
//		hql.append(AelItemSolicExameHist.Fields.SOE_SEQ.toString()).append(", ");
//		hql.append("isex.").append(AelItemSolicExameHist.Fields.SEQP.toString()).append(' ');
//		hql.append(" from ");
//		hql.append(AelItemSolicExameHist.class.getSimpleName()).append(" isex ");
//		hql.append(" inner join isex." + AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString() + " soe ");
//		hql.append(" inner join isex." + AelItemSolicExameHist.Fields.ATENDIMENTO.toString() + " atd ");
//		hql.append("where ");
//		hql.append("isex.").append(AelItemSolicExameHist.Fields.SOE_SEQ).append(" = ise.").append(AelItemSolicExameHist.Fields.SOE_SEQ);
//		hql.append(" AND isex.").append(AelItemSolicExameHist.Fields.SEQP).append(" = ise.").append(AelItemSolicExameHist.Fields.SEQP);
//		hql.append(" AND atd.").append(AghAtendimentos.Fields.CODIGO_PACIENTE).append(" = :pCodigoPaciente ");
//		if (unfSeq != null) {
//			hql.append(" AND isex.").append(AelItemSolicExameHist.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
//		}
//		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "isex"));
//		return hql;
//	}

//	private StringBuilder montaHqlSolicitacaoExamesAtv(final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final Short unfSeq) {
//		final StringBuilder hql = new StringBuilder();
//		hql.append("select isey.");
//		hql.append(AelItemSolicExameHist.Fields.SOE_SEQ.toString()).append(", ");
//		hql.append("isey.").append(AelItemSolicExameHist.Fields.SEQP.toString()).append(' ');
//		hql.append(" from ");
//		hql.append(AelItemSolicExameHist.class.getSimpleName()).append(" isey ");
//		hql.append(" inner join isey." + AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString() + " soe ");
//		hql.append(" inner join isey." + AelItemSolicExameHist.Fields.ATENDIMENTO_DIVERSO.toString() + " atv ");
//		hql.append("where ");
//		hql.append("isey.").append(AelItemSolicExameHist.Fields.SOE_SEQ).append(" = ise.").append(AelItemSolicExameHist.Fields.SOE_SEQ);
//		hql.append(" AND isey.").append(AelItemSolicExameHist.Fields.SEQP).append(" = ise.").append(AelItemSolicExameHist.Fields.SEQP);
//		hql.append(" AND atv.").append(AelAtendimentoDiversos.Fields.PAC_CODIGO).append(" = :pCodigoPaciente ");
//		hql.append(" AND atv.").append(AelAtendimentoDiversos.Fields.PJQ_SEQ).append(" is not null ");
//		if (unfSeq != null) {
//			hql.append(" AND isey.").append(AelItemSolicExameHist.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
//		}
//		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "isey"));
//		return hql;
//	}

	private StringBuilder montaHqlWhereCodigoSituacao(final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final String alias) {
		final StringBuilder hql = new StringBuilder();
		String sinal = " = ";
		final List<DominioSituacaoItemSolicitacaoExame> listSitCodigo = new ArrayList<DominioSituacaoItemSolicitacaoExame>();
		switch (situacaoItemExame) {
			case LI :
				sinal = " = ";
				listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.LI);
				break;
			case PE :
				sinal = " <> ";
				listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.CA);
				listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.PE);
				listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.LI);
				break;
			case CA :
				listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.CA);
				sinal = " = ";
				break;
		}
		for (final DominioSituacaoItemSolicitacaoExame sitCodigo : listSitCodigo) {
			hql.append(" AND ").append(alias).append('.').append(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO).append(sinal).append(" '").append(sitCodigo.name()).append("' ");
		}
		return hql;
	}

	public Boolean verificarSePacientePossuiDadoHistoricoPOLAghAtendimento(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class, "ise");
		criteria.setProjection(Projections.property("ise." + AelItemSolicExameHist.Fields.SEQP.toString()));
		
		criteria.createAlias(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString(), "atd");
		
		if(sitExame != null){
			criteria.add(Restrictions.eq("ise." + AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitExame.toString()));
		}
		
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		
		return executeCriteriaExists(criteria);		
	
	}
	
	
	public Boolean verificarSePacientePossuiDadoHistoricoPOLAelAtdDiverso(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class, "ise");
		criteria.setProjection(Projections.property("ise." + AelItemSolicExameHist.Fields.SEQP.toString()));
		
		criteria.createAlias(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO_DIVERSO.toString(), "atv");
		
		if(sitExame != null){
			criteria.add(Restrictions.eq("ise." + AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitExame.toString()));
		}
		
		criteria.add(Restrictions.eq("atv." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		
		return executeCriteriaExists(criteria);		
		
	}
	
	public Boolean verificarSePacientePossuiDadoHistoricoPOLAipMdtoHist(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAipPolMdtosAghuHist.class, "hist");
		criteria.setProjection(Projections.property("hist." + VAipPolMdtosAghuHist.Fields.COD_PACIENTE.toString()));
		
		criteria.add(Restrictions.eq("hist." + VAipPolMdtosAghuHist.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("hist." + VAipPolMdtosAghuHist.Fields.IND_ANTIMICROBIANO.toString(), Boolean.TRUE))
				.add(Restrictions.eq("hist." + VAipPolMdtosAghuHist.Fields.IND_QUIMIOTERAPICO.toString(), Boolean.TRUE))
				.add(Restrictions.eq("hist." + VAipPolMdtosAghuHist.Fields.IND_TUBERCULOSTATICO.toString(), Boolean.TRUE))
		);
		
		return executeCriteriaExists(criteria);	
		
	}
	
	public AelItemSolicExameHist obterPorIdOrigemPol(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class);

		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), "motivoCancelaExames", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.REGIAO_ANATOMICA.toString(), "regiaoAnatomica", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ufe."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ema."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.INTERVALO_COLETAS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR.toString() + "."+ RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + "."+ RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), FetchMode.JOIN);		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString() +"." + AghAtendimentos.Fields.PACIENTE.toString(), FetchMode.JOIN);
	
		return (AelItemSolicExameHist) executeCriteriaUniqueResult(criteria);
	}
	
	public AelItemSolicExameHist obterPorId(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class);

		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), "motivoCancelaExames", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.REGIAO_ANATOMICA.toString(), "regiaoAnatomica", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ufe."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ema."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
		criteria.createAlias(AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString(), "ama", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sis", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicExameHist.Fields.INTERVALO_COLETAS.toString(), "ico", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelItemSolicExameHist.Fields.ATENDIMENTO.toString(), "atend", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atend." + AghAtendimentos.Fields.PACIENTE.toString(), "pacien", JoinType.LEFT_OUTER_JOIN);
		
		return (AelItemSolicExameHist) executeCriteriaUniqueResult(criteria);
	}

	public List<AelItemSolicExameHist> pesquisarItensSolicitacoesExames(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class);
		
		if (soeSeq != null) {
			criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
		}
		
		if (seqp != null) {
			criteria.add(Restrictions.eq(AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
		}

		criteria.createAlias(AelItemSolicExameHist.Fields.AEL_EXAMES.toString(), "EXAME", JoinType.INNER_JOIN);
		criteria.createAlias(AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAT_ANA", JoinType.INNER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "uee", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("uee."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(AelItemSolicExameHist.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc(AelItemSolicExameHist.Fields.SEQP.toString()));
				
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que busca todas as informações do paciente, tem join soficiente para buscar tudo mesmo...tudo mesmo
	 * @param codigo
	 * @param seq_consulta
	 * @param filtro
	 * @return List<PesquisaExamesPacientesResultsVO>
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPacienteHist(Integer codigo, Integer seq_consulta, PesquisaExamesFiltroVO filtro){
		ExameSolicitadoPorPacienteQueryBuilder builder = new ExameSolicitadoPorPacienteQueryBuilder(true);
		
		builder.setCodigo(codigo);
		builder.setSeqConsulta(seq_consulta);
		builder.setFiltro(filtro);
		
		DetachedCriteria criteria = builder.build();
		List<Object[]> results = executeCriteria(criteria);
		
		List<PesquisaExamesPacientesResultsVO> exames = new ArrayList<PesquisaExamesPacientesResultsVO>();
		for (Object[] record : results) {
			PesquisaExamesPacientesResultsVO exame = new PesquisaExamesPacientesResultsVO();
			exame.setCodigoSoe((Integer)record[8]);
			exame.setDtHrProgramada((Date)record[0]);
			exame.setDtHrEvento((Date)record[1]);
			exame.setIseSeq((Short)record[2]);
			exame.setSituacaoItem((String)record[3]);
			exame.setTipoColeta(((DominioTipoColeta)record[4]).getDescricao());
			if(record[5]!=null){
				exame.setEtiologia(((MciEtiologiaInfeccao)record[5]).getCodigo());
			}
			exame.setExame((String)record[6]);
			exame.setOrigemAtendimento((DominioOrigemAtendimento)record[7]);
			exame.setExisteDocAnexado(((Integer)record[10])>0);
			exame.setSituacaoCodigo((String)record[11]);
			exames.add(exame);
		}

		return exames;
	}
	
	public Date buscaMaiorDataLiberacao(Integer soeSeq, Short unidadeFuncionalSeqp) {
		final StringBuilder hql = new StringBuilder(150);
		hql.append("select max(ise.").append(AelItemSolicExameHist.Fields.DTHR_LIBERADA).append(") ")
		.append("from ").append(AelItemSolicExameHist.class.getSimpleName()).append(" ise ")
		.append(" where ise.").append(AelItemSolicExameHist.Fields.SOE_SEQ.toString()).append(" = :soeSeq ")
		.append(" and ise.").append(AelItemSolicExameHist.Fields.UFE_UNF_SEQ.toString()).append("  = :ufeUnfSeqp ")
		.append(" and ise.").append(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
		.append(" = :situacaoItemSolicitacaoCodigo ");

		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("ufeUnfSeqp", unidadeFuncionalSeqp);
		query.setParameter("situacaoItemSolicitacaoCodigo", "LI");

		return (Date) query.uniqueResult();
	}
	


}