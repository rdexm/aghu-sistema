package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioRestricaoUsuario;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
class ExameSolicitadoPorPacienteQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private boolean historico;
	private ExameSolicitadoPorPacienteFields fields;
	
	private Integer codigo;
	private Integer seq_consulta;
	private PesquisaExamesFiltroVO filtro;
	
	
	private void init(boolean b) {
		this.historico = b;
		this.fields = new ExameSolicitadoPorPacienteFields(b);
		
	}
	
	public ExameSolicitadoPorPacienteQueryBuilder() {
		super();
		this.init(false);
	}

	
	public ExameSolicitadoPorPacienteQueryBuilder(boolean isBuscaHistorico) {
		this.init(isBuscaHistorico);
	}
	

	@Override
	protected DetachedCriteria createProduct() {
		if (this.historico) {
			return DetachedCriteria.forClass(AelItemSolicExameHist.class);
		} else {
			return DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		}
	}

	@Override
	protected void doBuild(DetachedCriteria aProduct) {
		basicQuery(aProduct);
		filterConsulta(aProduct);
		filterPesquisaExame(aProduct);
		
		//P_SITUACAO_PEDENTE
		aProduct.add(Restrictions.ne(fields.obterSituacaoItemSolicitacaoCodigo(), filtro.getpSituacaoPendente()));

		setProjections(aProduct);
		
		//ordenação
		aProduct.addOrder(Order.desc(fields.obterDthrProgramada()));
		aProduct.addOrder(Order.asc(fields.obterSeqp()));
	}
	
	private void basicQuery(DetachedCriteria criteria) {
		criteria.createCriteria(fields.obterAelExames(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(fields.obterSolicitacaoExame(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria(fields.obterUnidadeFuncional(), "unf", JoinType.INNER_JOIN);
		criteria.createCriteria(fields.obterAelUnfExecutaExames(), "une", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(fields.obterExtratoItemSolic(), "eis", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(fields.obterSituacaoItemSolicitacao(), "sis", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."+fields.obterSolicitacaoAtendimento(), "atd", JoinType.INNER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));		
	}
	
	private void filterConsulta(DetachedCriteria criteria) {
		if (seq_consulta != null && seq_consulta != 0) {
			criteria.add(Restrictions.eq("cons."+AacConsultas.Fields.NUMERO.toString(), seq_consulta));
			/*subquery*/
			//  Pesquisar somente solicitações do atendimento correspondente a consulta
			//----------------------------------------------------------------------------------------------------------
			DetachedCriteria subCriteria = subCriteriaSolicitacaoExames();
			subCriteria.createAlias(fields.obterSolicitacaoAtendimento(), "atd", JoinType.INNER_JOIN);// join atendimentos
			//criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.INNER_JOIN);//itens solicitação exames
			subCriteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), seq_consulta));
			subCriteria.setProjection(Projections.distinct(Projections.property(fields.obterSolicitacaoSeq())));
			criteria.add(Property.forName(fields.obterSoeSeq()).in(subCriteria));
			//----------------------------------------------------------------------------------------------------------
		}
	}
	
	private void filterPesquisaExame(DetachedCriteria criteria) {
		/*	define_where_PAC;  Define a condição WHERE do Bloco PAC a partir do Prontuário
		def_where_solic := def_where_solic || 'Where soe_seq = :qms$ctrl.seq_solicitacao';	 */
		if(filtro.getNumeroSolicitacaoInfo()!=null && filtro.getNumeroSolicitacaoInfo() > 0){
			criteria.add(Restrictions.eq("soe."+fields.obterSolicitacaoSeq(), filtro.getNumeroSolicitacaoInfo()));
		}
	
		//IF  NAME_IN('QMS$CTRL.ORIGEM') is not null then
		if(filtro!=null && filtro.getOrigemAtendimentoInfo()!=null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), filtro.getOrigemAtendimentoInfo())); 
		}
	
		//IF  NAME_IN('QMS$CTRL.UNF_EXECUTORA') is not null then
		if(filtro!=null && filtro.getAelUnfExecutoraInfo()!=null){
			criteria.add(Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL, filtro.getAelUnfExecutoraInfo().getSeq()));
		}
	
		//IF  NAME_IN('QMS$CTRL.DSP_DESCRICAO_USUAL_EXAME') is not null then
		if(filtro!=null && filtro.getExameSolicitacaoInfo()!=null){
			criteria.add(Restrictions.eq("exa."+AelExames.Fields.SIGLA.toString(), filtro.getExameSolicitacaoInfo().getId().getSigla()));
		}
	
		//IF  NAME_IN('QMS$CTRL.CRITERIO_CONSULTA') is not null then 
		if(filtro!=null && filtro.getRestricao()!=null && filtro.getServidorLogado()!=null){
			addRestricao(criteria, filtro.getRestricao(), filtro.getServidorLogado().getId().getMatricula(), filtro.getServidorLogado().getId().getVinCodigo());
		}
	
		//P_SITUACAO_CANCELADO
		if(filtro.getIndMostraCanceladosInfo().equals(DominioSimNao.N)){
			criteria.add(Restrictions.ne(fields.obterSituacaoItemSolicitacaoCodigo(), filtro.getpSituacaoCancelado()));
		}
		
		filtraPatologia(criteria);

	}
	
	private void filtraPatologia(DetachedCriteria criteria) {
		
		if (filtro.getNumeroAp() != null || filtro.getConfigExame() != null) {
			criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul", JoinType.INNER_JOIN);
			criteria.createCriteria("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux", JoinType.INNER_JOIN);
			criteria.createCriteria("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum", JoinType.INNER_JOIN);

			if (filtro.getNumeroAp() != null) {
				criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), filtro.getNumeroAp()));
			}

			if (filtro.getConfigExame() != null) {
				criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), filtro.getConfigExame().getSeq()));
			}
		}
		
	}
		
	private void setProjections(DetachedCriteria criteria) {
		StringBuffer sqlDocs = new StringBuffer(250);
		sqlDocs.append("	(select 	count(*) as EXISTENTES ");
		sqlDocs.append("		from ").append(fields.obterSchema()).append(".ael_doc_resultado_exames doc ");
		sqlDocs.append("		where doc.ise_soe_seq = soe2_.seq ");
		sqlDocs.append("		and	doc.ise_seqp = {alias}.seqp ");
		sqlDocs.append("		and	(doc.ind_anulacao_doc = 'N' or doc.dthr_anulacao_doc is null)) as EXISTENTES ");
		
		//campos do resultado
		criteria.setProjection(Projections.projectionList()
			.add(Projections.sqlProjection(" max({alias}.DTHR_PROGRAMADA) as DTHR_PROGRAMADA",
				 new String[] { "DTHR_PROGRAMADA" }, new Type[] { TimestampType.INSTANCE }))

			.add(Projections.sqlProjection(" max(DTHR_EVENTO) as DTHR_EVENTO",
				new String[] { "DTHR_EVENTO" }, new Type[] { TimestampType.INSTANCE }))

			.add(Projections.groupProperty(fields.obterSeqp()))
			.add(Projections.groupProperty("sis."+AelSitItemSolicitacoes.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty(fields.obterTipoColeta()))
			.add(Projections.groupProperty(fields.obterEtiologia()))

			.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()))
			.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.ORIGEM.toString()))
			.add(Projections.groupProperty("soe."+fields.obterSolicitacaoSeq()))
			.add(Projections.groupProperty(fields.obterDthrProgramada()))
			.add(Projections.sqlProjection(sqlDocs.toString(), new String[] { "EXISTENTES" }, new Type[] { IntegerType.INSTANCE }))
			.add(Projections.groupProperty("sis."+AelSitItemSolicitacoes.Fields.CODIGO.toString()))
		);		
	}
	
	
	
	
	private void addRestricao(DetachedCriteria criteria, DominioRestricaoUsuario restricao, Integer matricula, Short vinCodigo){
		StringBuffer sql = new StringBuffer(180);
		sql.append("NOT EXISTS (SELECT 1 ");
		sql.append("	FROM ").append(fields.obterSchema()).append(".AEL_ITEM_SOLIC_CONSULTADOS ISC");
		sql.append("	WHERE ISC.ISE_SOE_SEQ = {alias}.SOE_SEQ ");
		sql.append("	AND ISC.ISE_SEQP    = {alias}.SEQP ");

		if(DominioRestricaoUsuario.NC.equals(restricao)){
			/*	parametros para a subquery	*/
			Object[] values = {};
			Type[] types = {};
			sql.append(" ) ");
			criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		}else if(DominioRestricaoUsuario.NCU.equals(restricao)){
			sql.append("		AND		ISC.SER_MATRICULA	= ? ");
			sql.append("		AND		ISC.SER_VIN_CODIGO	= ? )");

			/*	parametros para a subquery	*/
			Object[] values = {matricula, vinCodigo};
			Type[] types = {IntegerType.INSTANCE, ShortType.INSTANCE};
			criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));
		}
	}
	

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Integer c) {
		this.codigo = c;
	}

	/**
	 * @param seq_consulta the seq_consulta to set
	 */
	public void setSeqConsulta(Integer seqconsulta) {
		this.seq_consulta = seqconsulta;
	}

	/**
	 * @param filtro the filtro to set
	 */
	public void setFiltro(PesquisaExamesFiltroVO f) {
		this.filtro = f;
	}
	
	private DetachedCriteria subCriteriaSolicitacaoExames() {
		if (this.historico) {
			return DetachedCriteria.forClass(AelSolicitacaoExamesHist.class);
		} else {
			return DetachedCriteria.forClass(AelSolicitacaoExames.class);
		}
	}
	
	
	/* SQL de busca no Historico original.
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class);

		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "une", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.EXTRATO_ITEM_SOLIC.toString(), "eis", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sis", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.CODIGO_PACIENTE.toString(), codigo));
		if(seq_consulta != null && seq_consulta != 0){
			criteria.add(Restrictions.eq("cons."+AacConsultas.Fields.NUMERO.toString(), seq_consulta));
			//subquery
			//  Pesquisar somente solicitações do atendimento correspondente a consulta
			//----------------------------------------------------------------------------------------------------------
			DetachedCriteria subCriteria = DetachedCriteria.forClass(AelSolicitacaoExamesHist.class);
			subCriteria.createAlias(AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);// join atendimentos
			//criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.INNER_JOIN);//itens solicitação exames
			subCriteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), seq_consulta));
			subCriteria.setProjection(Projections.distinct(Projections.property(AelSolicitacaoExamesHist.Fields.SEQ.toString())));
			criteria.add(Property.forName(AelItemSolicExameHist.Fields.SOE_SEQ.toString()).in(subCriteria));
			//----------------------------------------------------------------------------------------------------------
		}

		//	define_where_PAC;  Define a condição WHERE do Bloco PAC a partir do Prontuário
		//	def_where_solic := def_where_solic || 'Where soe_seq = :qms$ctrl.seq_solicitacao';	
		if(filtro.getNumeroSolicitacaoInfo()!=null && filtro.getNumeroSolicitacaoInfo() > 0){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExamesHist.Fields.SEQ.toString(), filtro.getNumeroSolicitacaoInfo()));
		}

		//IF  NAME_IN('QMS$CTRL.ORIGEM') is not null then
		if(filtro!=null && filtro.getOrigemAtendimentoInfo()!=null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), filtro.getOrigemAtendimentoInfo())); 
		}

		//IF  NAME_IN('QMS$CTRL.UNF_EXECUTORA') is not null then
		if(filtro!=null && filtro.getAelUnfExecutoraInfo()!=null){
			criteria.add(Restrictions.eq("une."+AelUnfExecutaExames.Fields.UNF_SEQ, filtro.getAelUnfExecutoraInfo().getUnfSeq().getSeq()));
		}

		//IF  NAME_IN('QMS$CTRL.DSP_DESCRICAO_USUAL_EXAME') is not null then
		if(filtro!=null && filtro.getExameSolicitacaoInfo()!=null){
			criteria.add(Restrictions.eq("exa."+AelExames.Fields.SIGLA.toString(), filtro.getExameSolicitacaoInfo().getId().getSigla()));
		}

		//IF  NAME_IN('QMS$CTRL.CRITERIO_CONSULTA') is not null then 
		if(filtro!=null && filtro.getRestricao()!=null && filtro.getServidorLogado()!=null){
			addRestricao(criteria, filtro.getRestricao(), filtro.getServidorLogado().getId().getMatricula(), filtro.getServidorLogado().getId().getVinCodigo());
		}

		//P_SITUACAO_CANCELADO
		if(filtro.getIndMostraCanceladosInfo().equals(DominioSimNao.N)){
			criteria.add(Restrictions.ne(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), filtro.getpSituacaoCancelado().getVlrTexto()));
		}
		//P_SITUACAO_PEDENTE
		criteria.add(Restrictions.ne(AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), filtro.getpSituacaoPendente().getVlrTexto()));
		
		StringBuffer sqlDocs = new StringBuffer(250);
		sqlDocs.append("	(select 	count(*) as EXISTENTES ");
		sqlDocs.append("							from 	hist.ael_doc_resultado_exames doc ");
		sqlDocs.append("							where	doc.ise_soe_seq = soe2_.seq ");
		sqlDocs.append("							and	doc.ise_seqp = {alias}.seqp ");
		sqlDocs.append("							and	(doc.ind_anulacao_doc = 'N' or doc.dthr_anulacao_doc is null)) as EXISTENTES ");
		
		//campos do resultado
		criteria.setProjection(Projections.projectionList()
			.add(Projections.sqlProjection(" max({alias}.DTHR_PROGRAMADA) as DTHR_PROGRAMADA",
				 new String[] { "DTHR_PROGRAMADA" }, new Type[] { TimestampType.INSTANCE }))

			.add(Projections.sqlProjection(" max(DTHR_EVENTO) as DTHR_EVENTO",
				new String[] { "DTHR_EVENTO" }, new Type[] { TimestampType.INSTANCE }))

			.add(Projections.groupProperty(AelItemSolicExameHist.Fields.SEQP.toString()))
			.add(Projections.groupProperty("sis."+AelSitItemSolicitacoes.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty(AelItemSolicExameHist.Fields.TIPO_COLETA.toString()))
			.add(Projections.groupProperty(AelItemSolicExameHist.Fields.ETIOLOGIA.toString()))

			.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()))
			.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.ORIGEM.toString()))
			.add(Projections.groupProperty("soe."+AelSolicitacaoExamesHist.Fields.SEQ.toString()))
			.add(Projections.groupProperty(AelItemSolicExameHist.Fields.DTHR_PROGRAMADA.toString()))
			.add(Projections.sqlProjection(sqlDocs.toString(), new String[] { "EXISTENTES" }, new Type[] { IntegerType.INSTANCE }))
		);

		//ordenação
		criteria.addOrder(Order.desc(AelItemSolicExameHist.Fields.DTHR_PROGRAMADA.toString()));
		criteria.addOrder(Order.asc(AelItemSolicExameHist.Fields.SEQP.toString()));
	
	*/
}
