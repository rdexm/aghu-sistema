package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.DetalhesExamesPacienteVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.transplante.vo.TiposExamesPacienteVO;

public class AelExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExames> {

	private static final long serialVersionUID = 8594577342436400229L;

	public AelExames obterPeloId(String sigla){
		if(sigla == null){
			return null;
		}
		return super.obterPorChavePrimaria(sigla);
	}
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelExames.class, "exa");
    }
	
	private DetachedCriteria criarCriteria(AelExames elemento, AghUnidadesFuncionais unidade){
		
		DetachedCriteria criteria = obterCriteria();
		
		if(StringUtils.isNotBlank(elemento.getSigla())){
			criteria.add(Restrictions.eq(criteria.getAlias() + "." + AelExames.Fields.SIGLA.toString(), elemento.getSigla().trim()));
		}
		
		if(StringUtils.isNotBlank(elemento.getDescricao())){
			criteria.add(Restrictions.ilike(criteria.getAlias() + "." + AelExames.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(elemento.getDescricaoUsual())){
			criteria.add(Restrictions.ilike(criteria.getAlias() + "." + AelExames.Fields.DESCRICAO_USUAL.toString(), elemento.getDescricaoUsual(), MatchMode.ANYWHERE));
		}
		
		if(elemento.getIndImpressao() != null){
			criteria.add(Restrictions.eq(criteria.getAlias() + "." + AelExames.Fields.IND_IMPRESSAO.toString(), elemento.getIndImpressao()));
		}
		
		if( elemento.getIndConsisteInterface() != null){
			criteria.add(Restrictions.eq(criteria.getAlias() + "." + AelExames.Fields.IND_CONSISTE_INTERFACE.toString(), elemento.getIndConsisteInterface()));
		}
		
		if(elemento.getIndPermiteAnexarDoc() != null){
			criteria.add(Restrictions.eq(criteria.getAlias() + "." + AelExames.Fields.IND_PERMITE_ANEXAR_DOC.toString(),elemento.getIndPermiteAnexarDoc()));
		}
		
		if(elemento.getIndSituacao() != null){
			criteria.add(Restrictions.eq(criteria.getAlias() + "." + AelExames.Fields.IND_SITUACAO.toString(), elemento.getIndSituacao()));
		}
		
		if(unidade != null) {
			DetachedCriteria criteria2 = DetachedCriteria.forClass(AelUnfExecutaExames.class, "ufe");
			
			criteria2.setProjection(Projections.property(criteria2.getAlias() + "." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()));
			criteria2.add(Restrictions.eqProperty(criteria2.getAlias() + "." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), criteria.getAlias()
					+ "." + AelExames.Fields.SIGLA.toString()));
			criteria2.add(Restrictions.eq(criteria2.getAlias() + "." + AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unidade.getSeq()));
			
			criteria.add(Subqueries.exists(criteria2));
			
		}
		
		return criteria;
	}
	
	public List<AelExames> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelExames elemento, AghUnidadesFuncionais unidade) {
		DetachedCriteria criteria = criarCriteria(elemento, unidade);
		return executeCriteria(criteria, firstResult, maxResult, AelExames.Fields.DESCRICAO_USUAL.toString(), true);
	}
	
	public Long pesquisarCount(AelExames elemento, AghUnidadesFuncionais unidade) {
		DetachedCriteria criteria = criarCriteria(elemento, unidade);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Verificar a existência de registros em outras entidades
	 * @param object
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItem(AelExames exames, Class class1, Enum field) {
		if (exames == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),exames.getSigla()));
		return (executeCriteriaCount(criteria) > 0);
	}

	public List<AelItemSolicitacaoExames> pesquisarRelatorioMateriaisColetaEnfermagem(AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "aie");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf2");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");		
		
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "man");
		
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.CSP_CNV_CODIGO.toString(), "conv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(),unidadeFuncional));
		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(),DominioPacAtendimento.S));
		
		List<DominioSituacaoAmostra> situacaoAmostraList = new ArrayList<DominioSituacaoAmostra>();
		situacaoAmostraList.add(DominioSituacaoAmostra.G);
		situacaoAmostraList.add(DominioSituacaoAmostra.M);		
		criteria.add(Restrictions.in("amo."+AelAmostras.Fields.SITUACAO.toString(),situacaoAmostraList));
		criteria.add(Restrictions.in("aie."+AelAmostraItemExames.Fields.SITUACAO.toString(),situacaoAmostraList));
		
		
		//AND decode(ISE.SIT_CODIGO, 'PE','N','S') = 'S'
		//criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.SIT_CODIGO,1,2) <> 'PE' "));
		criteria.add(Restrictions.ne("ise."+AelItemSolicitacaoExames.Fields.SITCODIGO.toString(),"PE"));	
		criteria.add(Restrictions.ne("ise."+AelItemSolicitacaoExames.Fields.SITCODIGO.toString(),"N"));
		
		DetachedCriteria criteriaTae = DetachedCriteria.forClass(AelTipoAmostraExame.class, "tae");
		criteria.add(Subqueries.exists(criteriaTae.setProjection(Property.forName("tae."
				+ AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString())).add(
						Restrictions.eqProperty("tae."+AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),"exa."+AelExames.Fields.SIGLA.toString())).add(
						Restrictions.eqProperty("tae."+AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),"man."+AelMateriaisAnalises.Fields.SEQ.toString())).add(
						Restrictions.eqProperty("tae."+AelTipoAmostraExame.Fields.MAN_SEQ.toString(),"man."+AelMateriaisAnalises.Fields.SEQ.toString())).add(
		                Restrictions.or(Restrictions.eq("tae."+AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T),
				        Restrictions.eqProperty("tae."+AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), "atd."+AghAtendimentos.Fields.ORIGEM.toString()))).add(
				        Restrictions.eq("tae."+AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), DominioResponsavelColetaExames.P))));

		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		// Distinct de consulta 
		Set<AelItemSolicitacaoExames> retornoDistinct = new HashSet<AelItemSolicitacaoExames>();
		List<AelItemSolicitacaoExames> retorno = new ArrayList<AelItemSolicitacaoExames>();
		
		retorno = executeCriteria(criteria);
		retornoDistinct.addAll(retorno);
		retorno.clear();
		retorno.addAll(retornoDistinct);
		
		return retorno;
	}

	public List<AelExames> listarExames(String strPesquisa) {
		if (StringUtils.isNotBlank(strPesquisa)) {
			AelExames exame = this.obterExamePorSeq(strPesquisa, AelExames.Fields.DESCRICAO.toString());
			
			if (exame != null) {
				List<AelExames> lista = new ArrayList<AelExames>(1);
				lista.add(exame);
				return lista;
			}
		}
		
		DetachedCriteria criteria = criarCriteriaListarExames(strPesquisa, AelExames.Fields.DESCRICAO.toString());
		return this.executeCriteria(criteria, 0, 50, null, true);
	}

	public Long listarExamesCount(String strPesquisa) {
		if (StringUtils.isNotBlank(strPesquisa)) {
			AelExames exame = this.obterExamePorSeq(strPesquisa);
			
			if (exame != null) {
				return 1l;
			}
		}
		
		DetachedCriteria criteria = criarCriteriaListarExames(strPesquisa);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarExames(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelExames.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	private DetachedCriteria criarCriteriaListarExames(String strPesquisa, String order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelExames.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(order));
		return criteria;
	}

	private AelExames obterExamePorSeq(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);

		criteria.add(Restrictions.ilike(AelExames.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT));

		return (AelExames) this.executeCriteriaUniqueResult(criteria);
	}
	
	private AelExames obterExamePorSeq(String strPesquisa, String order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);
		
		criteria.add(Restrictions.ilike(AelExames.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT));
		criteria.addOrder(Order.asc(order));
		
		return (AelExames) this.executeCriteriaUniqueResult(criteria);
	}
	
	public DetalhesExamesPacienteVO buscaResultadosExames(Integer soeSeq,
			Integer IseSoeSeq, Integer pNroSessao) {

		DetalhesExamesPacienteVO detalhesExamesPacienteVO = new DetalhesExamesPacienteVO();
		/*DetachedCriteria criteria = DetachedCriteria
				.forClass(AelExamesDAO.class, "exa");*/
		// TODO Implementar a query !!!

		return detalhesExamesPacienteVO;
	}
	
	public List<AelExames> sbBuscarExames(Object parametro) {
		return executeCriteria(this.montaCriteriaExames(parametro), 0, 100, null, false);
	}

	public Long sbBuscarExamesCount(Object parametro) {

		return executeCriteriaCount(this.montaCriteriaExames(parametro));
	}

	public DetachedCriteria montaCriteriaExames(Object parametro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);

		criteria.add(Restrictions.eq(AelExames.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.add(Restrictions.or(Restrictions.eq(
				AelExames.Fields.SIGLA.toString(), parametro.toString()),
				Restrictions.or(Restrictions.ilike(
						AelExames.Fields.DESCRICAO_USUAL.toString(),
						parametro.toString(), MatchMode.ANYWHERE), Restrictions
						.ilike(AelExames.Fields.DESCRICAO.toString(),
								parametro.toString(), MatchMode.ANYWHERE))));

		return criteria;
	}

	public List<AelExameAp> obterExamesEmAndamento(final Integer pacCodigo, final Short unidadeSeq,
			final String situacaoAreaExecutora, final DominioSituacaoExamePatologia etapasLaudo, boolean atendimentoDiverso) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class, "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "aie");

		criteria.setProjection(Projections.property("lux." + AelExameAp.Fields.SEQ.toString()));

		if (atendimentoDiverso) {
			criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv");
			criteria.add(Restrictions.eq("atv." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), pacCodigo));
		} else {
			criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}

		criteria.add(Restrictions.ne("lux." + AelExameAp.Fields.ETAPAS_LAUDO.toString(), etapasLaudo));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unidadeSeq));
		/*
		 * final DetachedCriteria subCriteria1 =
		 * DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise1");
		 * subCriteria1.createAlias("ise1." +
		 * AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(),
		 * "lul1"); subCriteria1.add(Restrictions.eqProperty("ise1." +
		 * AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), "soe." +
		 * AelSolicitacaoExames.Fields.SEQ.toString()));
		 * subCriteria1.add(Restrictions.eqProperty("lul1." +
		 * AelExameApItemSolic.Fields.LUX_SEQ.toString(), "lux." +
		 * AelExameAp.Fields.SEQ.toString()));
		 * subCriteria1.setProjection(Projections.max("ise1." +
		 * AelItemSolicitacaoExames.Fields.SEQP.toString()));
		 * criteria.add(Subqueries.propertyEq("ise." +
		 * AelItemSolicitacaoExames.Fields.SEQP.toString(), subCriteria1));
		 */
		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "eis1");
		subCriteria2.add(Restrictions.eqProperty("eis1." + AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subCriteria2.add(Restrictions.eqProperty("eis1." + AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(),
				"ise." + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		subCriteria2.add(Restrictions.eq("eis1." + AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacaoAreaExecutora));
		subCriteria2.setProjection(Projections.max("eis1." + AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()));
		criteria.add(Subqueries.propertyEq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), subCriteria2));

//		criteria.add(Restrictions.ge("aie." + AelAmostraItemExames.Fields.ALTERADO_EM.toString(), numeroHorasValidas));

		// filtra os que já possuem diagnóstico.
		final DetachedCriteria subCriteria3 = DetachedCriteria.forClass(AelDiagnosticoAps.class, "lun");
		subCriteria3.add(Restrictions.eqProperty("lun." + AelDiagnosticoAps.Fields.LUX_SEQ.toString(), "lux1." + AelExameAp.Fields.SEQ.toString()));
		subCriteria3.setProjection(Projections.property("lun." + AelDiagnosticoAps.Fields.LUX_SEQ.toString()));

		final DetachedCriteria superCriteria = DetachedCriteria.forClass(AelExameAp.class, "lux1");
		//		superCriteria.add(Restrictions.ne("lux1." + AelExameAp.Fields.ETAPAS_LAUDO.toString(), DominioSituacaoExamePatologia.LA));
		superCriteria.add(Subqueries.propertyIn("lux1." + AelExameAp.Fields.SEQ.toString(), criteria));
		superCriteria.add(Subqueries.notExists(subCriteria3));
		superCriteria.addOrder(Order.asc("lux1." + AelExameAp.Fields.SEQ.toString()));

		return executeCriteria(superCriteria);
	}

	//C2 AGRUPAR EXAMES
	/**
	 * Consulta exames em andamento dado um numero de solicitacao
	 * numeros de uma ou mais amostras, numero de prontuario e numero de horas validas da data de alteracao 
	 * C2 #22049
	 * 
	 * @param solicitacaoNumero numero de solicitacao do exame
	 * @param numero de uma ou maios amostras
	 * @param numero o prontuario
	 * @param numero de horas valida para consulta na alteracao do exame
	 */
	public List<ExameAndamentoVO> obterExamesEmAndamento(Integer solicitacaoNumero, List<Short> listaSeqpAmostrasRecebidas, Integer solicitacaoProntuario, 
			Date numeroHorasValidas, Short unidadeSeq, final String situacaoAreaExecutora) {
		StringBuilder hql = null;
		final DominioFuncaoPatologista[] funcoes = new DominioFuncaoPatologista[] { DominioFuncaoPatologista.C, DominioFuncaoPatologista.P };

		hql = criaHQLExamesAndamentoPrimeiroUnion(unidadeSeq, funcoes);
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoSolicitacao", solicitacaoNumero);
		query.setParameterList("iseSeqP", listaSeqpAmostrasRecebidas);
		query.setParameter("prontuario", solicitacaoProntuario);
		query.setParameter("numeroHorasValidas", numeroHorasValidas);
		query.setParameter("situacao", situacaoAreaExecutora);
		
		query.setReadOnly(true);
		
		List<ExameAndamentoVO> examesAndamento = query.list();
		
		hql = criaHQLExamesAndamentoSegundoUnion(unidadeSeq, funcoes);
		
		//EXECUCAO
		query = createHibernateQuery(hql.toString());
		query.setParameter("codigoSolicitacao", solicitacaoNumero);
		query.setParameterList("iseSeqP", listaSeqpAmostrasRecebidas);
		query.setParameter("prontuario", solicitacaoProntuario);
		query.setParameter("numeroHorasValidas", numeroHorasValidas);
		query.setParameter("situacao", situacaoAreaExecutora);
		query.setReadOnly(true);
		examesAndamento.addAll(query.list());
		return examesAndamento;
	}
	
	private StringBuilder criarHQLExamesAndamento(String complementoUnion, String tabelaAtendimento, Short unidadeSeq, DominioFuncaoPatologista[] funcoes) {
		StringBuilder subHql = new StringBuilder("( select eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO).append(" \n");
		subHql.append(" from \n ");
		subHql.append(AelExtratoItemSolicitacao.class.getSimpleName()).append(" eis \n");
		subHql.append(" where \n");
		subHql.append("eis.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString())
		.append(" = :codigoSolicitacao ").append(" and \n");
		subHql.append("eis.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString())
.append(" in (:iseSeqP) ").append(" and \n");
		subHql.append("eis.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
.append(" = :situacao ").append(" and \n");
		subHql.append("eis.").append(AelExtratoItemSolicitacao.Fields.SEQP.toString()).append(" in ( \n")
			.append("select max(eis1.").append(AelExtratoItemSolicitacao.Fields.SEQP.toString()).append(") \n")
			.append(" from ")
			.append(AelExtratoItemSolicitacao.class.getSimpleName()).append(" eis1 \n")
			.append(" where ")
			.append(" eis1.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()).append("")
			.append(" = eis.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()).append(" and \n")
			.append(" eis1.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()).append("")
			.append(" = eis.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()).append(" and \n")
			.append(" eis1.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()).append("")
			.append(" = eis.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()).append(" \n")
			.append(" ) ");
		subHql.append(')');
		
		StringBuilder hql = new StringBuilder(600).append("select new br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO (");
			hql.append("lum.").append(AelAnatomoPatologico.Fields.CONFIG_EXAME.toString()).append(", \n");
			hql.append("lum.").append(AelAnatomoPatologico.Fields.NUMERO_AP.toString()).append(", \n");
			hql.append("lum.").append(AelAnatomoPatologico.Fields.SEQ.toString()).append(", \n");
		    hql.append("lo5.").append(AelApXPatologista.Fields.AEL_PATOLOGISTAS.toString()).append(", \n");
//		hql.append(" null, \n");
			hql.append("soe.").append(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString()).append(", \n");
			hql.append(subHql);
		hql.append(" ) ");
		
		hql.append(" from \n");
		hql.append(AelSolicitacaoExames.class.getSimpleName()).append(" soe, \n");
		hql.append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise, \n");
		
		hql.append(tabelaAtendimento);
		
		hql.append(AelAnatomoPatologico.class.getSimpleName()).append(" lum, \n");
		hql.append(AelExameAp.class.getSimpleName()).append(" lux, \n");
		hql.append(AelExameApItemSolic.class.getSimpleName()).append(" lul, \n");
		hql.append(AelAmostraItemExames.class.getSimpleName()).append(" aie, \n");
		hql.append(AelAmostras.class.getSimpleName()).append(" amo, \n");
		hql.append(AelConfigExLaudoUnico.class.getSimpleName()).append(" lu2, \n");
		hql.append(AelApXPatologista.class.getSimpleName()).append(" lo5, \n");
		hql.append(AelPatologista.class.getSimpleName()).append(" lui \n");
		hql.append(" where \n");

		hql.append(complementoUnion);
		
		hql.append("ise.").append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString())
		.append(" = soe.").append(AelSolicitacaoExames.Fields.SEQ.toString()).append(" and \n");
		
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
		hql.append(" =  ").append(unidadeSeq).append(" and \n");
		hql.append("lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()).append(" and \n");
		hql.append("lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" and \n");
		
		hql.append("lum.").append(AelAnatomoPatologico.Fields.SEQ.toString())
		.append(" = lux.").append(AelExameAp.Fields.LUM_SEQ.toString()).append(" and ");
		hql.append("lux.").append(AelExameAp.Fields.SEQ.toString())
		.append(" = lul.").append(AelExameApItemSolic.Fields.LUX_SEQ.toString()).append(" and \n");
		hql.append("lux.").append(AelExameAp.Fields.LUM_SEQ.toString())
		.append(" = lum.").append(AelAnatomoPatologico.Fields.SEQ.toString()).append(" and \n");
		hql.append(" lux.").append(AelExameAp.Fields.ETAPAS_LAUDO.toString());
		hql.append(" <> '").append(DominioSituacaoExamePatologia.LA.toString()).append("' and \n");
		
		hql.append("soe.").append(AelSolicitacaoExames.Fields.ATD_SEQ.toString())
		.append(" = lum.").append(AelAnatomoPatologico.Fields.ATD_SEQ.toString()).append(" and \n");
		

		hql.append("lo5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
				.append(" = lum.").append(AelAnatomoPatologico.Fields.SEQ.toString()).append(" and \n");
		
		hql.append("lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
				.append(" = lui.").append(AelPatologista.Fields.SEQ.toString()).append(" and \n");
		hql.append("amo.").append(AelAmostras.Fields.SOE_SEQ.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" and \n");
		hql.append("amo.").append(AelAmostras.Fields.SEQP.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" and \n");
		hql.append("aie.").append(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" and \n");
		hql.append("aie.").append(AelAmostraItemExames.Fields.ISE_SEQP.toString())
		.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" and \n");
		hql.append("aie.").append(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString())
		.append(" = amo.").append(AelAmostras.Fields.SOE_SEQ.toString()).append(" and \n");
		hql.append("aie.").append(AelAmostraItemExames.Fields.AMO_SEQP.toString())
		.append(" = amo.").append(AelAmostras.Fields.SEQP.toString()).append(" and \n");
		hql.append("lum.").append(AelAnatomoPatologico.Fields.CONFIG_EXAME.toString())
.append(" = lu2.").append(AelConfigExLaudoUnico.Fields.SEQ.toString());
		if (funcoes != null && funcoes.length > 0) {
			hql.append(" and lui.").append(AelPatologista.Fields.FUNCAO.toString());
			hql.append(" in (");
			for (DominioFuncaoPatologista funcao : funcoes) {
				hql.append('\'').append(funcao.toString()).append("',");
			}
			hql.deleteCharAt(hql.length() - 1);
			hql.append(") \n");
		}
		return hql;
	}
	
	private StringBuilder criaHQLExamesAndamentoPrimeiroUnion(Short unidadeSeq, DominioFuncaoPatologista[] funcoes) {
		StringBuilder hqlUnion1 = new StringBuilder(100);
		hqlUnion1.append(" atd.").append(AghAtendimentos.Fields.PRONTUARIO.toString());
		hqlUnion1.append(" = :prontuario ").append(" and \n");
		hqlUnion1.append("lum.").append(AelAnatomoPatologico.Fields.ATD_SEQ.toString())
		.append(" = atd.").append(AghAtendimentos.Fields.SEQ.toString()).append(" and \n");
		
		hqlUnion1.append(" aie.").append(AelAmostraItemExames.Fields.ALTERADO_EM.toString());
		hqlUnion1.append(" >= :numeroHorasValidas ").append(" and \n");
		
		StringBuilder tabelaAtendimento = new StringBuilder();
		tabelaAtendimento.append(AghAtendimentos.class.getSimpleName()).append(" atd, ");
		
		hqlUnion1 = criarHQLExamesAndamento(hqlUnion1.toString(), tabelaAtendimento.toString(), unidadeSeq, funcoes);
		return hqlUnion1;
	}
	
	private StringBuilder criaHQLExamesAndamentoSegundoUnion(Short unidadeSeq, DominioFuncaoPatologista[] funcoes) {
		StringBuilder hqlUnion2 = new StringBuilder(100);
		hqlUnion2.append(" atv.").append(AelAtendimentoDiversos.Fields.PRONTUARIO.toString());
		hqlUnion2.append(" = :prontuario ").append(" and \n");
		hqlUnion2.append("lum.").append(AelAnatomoPatologico.Fields.ATV_SEQ.toString())
		.append(" = atv.").append(AelAtendimentoDiversos.Fields.SEQUENCE.toString()).append(" and \n");
		
		hqlUnion2.append(" aie.").append(AelAmostraItemExames.Fields.ALTERADO_EM.toString());
		hqlUnion2.append(" >= :numeroHorasValidas ").append(" and \n");
		
		StringBuilder tabelaAtendimento = new StringBuilder();
		tabelaAtendimento.append(AelAtendimentoDiversos.class.getSimpleName()).append(" atv, ");
		
		hqlUnion2 = criarHQLExamesAndamento(hqlUnion2.toString(), tabelaAtendimento.toString(), unidadeSeq, funcoes);
		return hqlUnion2;
	}
	
	public List<AelExames> obterDescricaoExame(Integer cSoeSeq, String cSituacaoItem){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class, "EXA");
		
		criteria.createAlias("EXA."+AelExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.add(Restrictions.eq("SOE."+AelSolicitacaoExames.Fields.SEQ.toString(), cSoeSeq));
		criteria.add(Restrictions.ne("ISE."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), cSituacaoItem));
		
		criteria.addOrder(Order.asc("EXA."+AelExames.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	public List<AelExames> obterAelExamesPorSiglaDescricao(String _filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelExames.Fields.SIGLA.toString()).as(AelExames.Fields.SIGLA.toString()))
				.add(Projections.property(AelExames.Fields.DESCRICAO.toString()).as(AelExames.Fields.DESCRICAO.toString()))
		);
		 
		
		if (StringUtils.isNotBlank(_filtro)) {
	        criteria.add(Restrictions.or(Restrictions.ilike(AelExames.Fields.DESCRICAO.toString(), _filtro, MatchMode.ANYWHERE),
	        							 Restrictions.eq(AelExames.Fields.SIGLA.toString(), _filtro.toUpperCase())));
	    }
		
		criteria.add(Restrictions.like(AelExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelExames.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelExames.class));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long obterAelExamesPorSiglaDescricaoCount(String _filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExames.class);
		
		
		if (StringUtils.isNotBlank(_filtro)) {
	        criteria.add(Restrictions.or(Restrictions.ilike(AelExames.Fields.DESCRICAO.toString(), _filtro, MatchMode.ANYWHERE),
	        							 Restrictions.eq(AelExames.Fields.SIGLA.toString(), _filtro.toUpperCase())));
	    }
		
		criteria.add(Restrictions.like(AelExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	/**#47146 - C3 - Consulta para obter os tipos exames do paciente*/
	public TiposExamesPacienteVO obterTiposExamesPaciente(Integer pacCodC1, Integer calSeqC2, String siglaC2, String resultF1) {
		DetachedCriteria criteria = criteriaObterTiposExamesPaciente(pacCodC1, calSeqC2, siglaC2);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()), TiposExamesPacienteVO.Fields.DESCRICAO_USUAL.toString());
		p.add(Projections.property("ISE."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), TiposExamesPacienteVO.Fields.DTHR_LIBERADA.toString());
		criteria.setProjection(p);
			
		List<Object[]> lista = executeCriteria(criteria);
		
		TiposExamesPacienteVO vo = null;
		if (lista != null && !lista.isEmpty()) {
			Object[] row1 = lista.get(0);
			vo = new TiposExamesPacienteVO();
			
			vo.setDescricaoUsual((String) row1[0]);
			vo.setDthrLiberada((Date) row1[1]);
			vo.setResultado(resultF1);
			
		}
		
		return vo;
	}
	
	/**#47146 */
	private DetachedCriteria criteriaObterTiposExamesPaciente(Integer pacCodC1, Integer calSeqC2, String siglaC2) {
		if (pacCodC1 == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!!"); 
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		criteria.createAlias("PCL."+AelParametroCamposLaudo.Fields.RESULTADOS_EXAMES.toString(), "REE");
		criteria.createAlias("REE."+AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("REE."+AelResultadoExame.Fields.EXAME.toString(), "EXA");
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodC1));
		
		
		if (calSeqC2 != null) {
			criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), calSeqC2));
		}
		
		if (siglaC2 != null && !siglaC2.isEmpty()) {
			criteria.add(Restrictions.like("EXA."+AelExames.Fields.SIGLA.toString(), siglaC2, MatchMode.EXACT));
		}
		
		criteria.addOrder(Order.desc("ISE."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));	
		return criteria;
	}
	
	/**#47146 Obtem parametro para a function F1 utilizada na estoria*/
	public AelResultadoExameId montarREETiposExamesPaciente(Integer pacCodC1, Integer calSeqC2, String siglaC2) {
		DetachedCriteria criteria = criteriaObterTiposExamesPaciente(pacCodC1, calSeqC2, siglaC2);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("REE."+AelResultadoExame.Fields.ID.toString()))
				.add(Projections.property("REE."+AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString()).as(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString()))
		);
		List<Object[]> lista = executeCriteria(criteria);
		
		AelResultadoExameId resultExId = null;
		if (lista != null && !lista.isEmpty()) {
			Object[] row1 = lista.get(0);
			resultExId = (AelResultadoExameId) row1[0];
		}
		
		return resultExId;
	}
	
	
	/**#47146 */
	private DetachedCriteria criteriaObterPCRReagente(String PS01, Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		criteria.createAlias("PCL."+AelParametroCamposLaudo.Fields.RESULTADOS_EXAMES.toString(), "REE");
		criteria.createAlias("REE."+AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.createAlias("REE."+AelResultadoExame.Fields.EXAME.toString(), "EXA");
		criteria.createAlias("EXA."+AelExames.Fields.LISTA_EUR.toString(), "EUR");
		criteria.add(Restrictions.eqProperty("REE."+AelResultadoExame.Fields.PCL_CAL_SEQ.toString(),
				"EUR."+MtxExameUltResults.Fields.CAMPO_LAUDO_SEQ.toString()));
		
		criteria.add(Restrictions.like("ISE."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "LI", MatchMode.EXACT));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		if(PS01 != null && !PS01.isEmpty()){
			criteria.add(Restrictions.like("EUR."+MtxExameUltResults.Fields.SIGLA.toString(), PS01));
		}
		if(pacCodigo != null){
			criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		criteria.addOrder(Order.desc("ISE."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		return criteria;
	}
	
	/**#47146 Obtem parametro para a function F1 utilizada na estoria */
	public AelResultadoExame montarREEPCRReagente(String PS01, Integer pacCodigo){
		DetachedCriteria criteria = criteriaObterPCRReagente(PS01, pacCodigo);
		
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("REE."+AelResultadoExame.Fields.ID.toString()), AelResultadoExame.Fields.ID.toString())
					.add(Projections.property("REE."+AelResultadoExame.Fields.VALOR.toString()), AelResultadoExame.Fields.VALOR.toString())
					.add(Projections.property("REE."+AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString()), AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString())
			);
		criteria.setResultTransformer(Transformers.aliasToBean(AelResultadoExame.class));
		List<AelResultadoExame> lista = executeCriteria(criteria);
		
		return !lista.isEmpty() ? lista.get(0) : null;
	}
	
	public FatProcedHospInternos phiJaCadastrado(AelExames exame,AelExamesMaterialAnalise materialAnalise) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternosPai.Fields.EMA_EXA_SIGLA.toString(), exame.getSigla()));
		criteria.add(Restrictions.eq(FatProcedHospInternosPai.Fields.EMA_MAN_SEQ.toString(), materialAnalise.getAelMateriaisAnalises().getSeq()));
		
		List<FatProcedHospInternos> listaProcedimentosInternos = executeCriteria(criteria);
		if (listaProcedimentosInternos != null && !listaProcedimentosInternos.isEmpty()) {
			return listaProcedimentosInternos.get(0);
		}
		return null;
	}	
}
