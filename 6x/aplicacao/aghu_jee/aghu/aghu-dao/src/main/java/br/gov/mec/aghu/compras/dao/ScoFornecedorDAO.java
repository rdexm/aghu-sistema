package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.compras.vo.CadastroContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.ContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.FornecedoresComEntregasPendentesVO;
import br.gov.mec.aghu.compras.vo.ScoFornecedorVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.suprimentos.vo.RelatorioESVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class ScoFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFornecedor> {
	private static final long serialVersionUID = 7972996021421662015L;
	
	private static final Log LOG = LogFactory.getLog(ScoFornecedorDAO.class);
	
	@Inject
	private RelatorioEntradasSemEmpenhoSemAssinaturaAFOracleQueryBuilder relatorioEntradasSemEmpenhoSemAssinaturaAFOracleQueryBuilder;
	
	@Inject
	private RelatorioEntradasSemEmpenhoSemAssinaturaAFPostgresQueryBuilder relatorioEntradasSemEmpenhoSemAssinaturaAFPostgresQueryBuilder;
	
	public ScoFornecedor pesquisarFornecedorPorRazaoSocial(String razaoSocial) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), razaoSocial));
		return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteriaFornecedoresAtivos(final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);

		criteria.add(Restrictions.eq(ScoFornecedor.Fields.SITUACAO.toString(), DominioSituacao.A));
		String strParametro = pesquisa.toString();
		
		if (strParametro == null || StringUtils.isBlank(strParametro)) {
			return criteria;
		}
		Integer seq = null;
		Long cpfOuCgc = null;
		
		if (CoreUtil.isNumeroLong(strParametro)) {
			cpfOuCgc = Long.valueOf(strParametro);
		}
		
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seq = Integer.valueOf(strParametro);  
		}
		
		if (seq != null && cpfOuCgc != null) {
			criteria.add(Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), cpfOuCgc),
					Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), cpfOuCgc),
							Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), seq))));
		} else if (cpfOuCgc != null) {
			criteria.add(Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), cpfOuCgc),
					Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), cpfOuCgc)));
		} else {
			criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),strParametro, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public Integer obterMaxForcenecor() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);		
		criteria.setProjection(Projections.max(ScoFornecedor.Fields.CRC.toString()));		
		return (Integer)executeCriteriaUniqueResult(criteria);
	}
	
	public Long listarFornecedoresAtivosCount(final Object pesquisa) {
		return executeCriteriaCount(criarCriteriaFornecedoresAtivos(pesquisa));
	}

	public List<ScoFornecedor> listarFornecedoresAtivos(final Object pesquisa,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		final DetachedCriteria criteria = criarCriteriaFornecedoresAtivos(pesquisa);
		criteria.addOrder(Order.asc(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		if (firstResult == null || maxResults == null) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, 100, orderProperty, asc);
		}
	}
	
	public List<FornecedorVO> pesquisarFornecedoresAtivosVO(final Object pesquisa,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		
		final DetachedCriteria criteria = criarCriteriaFornecedoresAtivos(pesquisa);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoFornecedor.Fields.NUMERO.toString())
					, FornecedorVO.Fields.NUMERO.toString())
				.add(Projections.property(ScoFornecedor.Fields.CGC.toString())
					, FornecedorVO.Fields.CGC.toString())
				.add(Projections.property(ScoFornecedor.Fields.CPF.toString())
					, FornecedorVO.Fields.CPF.toString())
				.add(Projections.property(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
					, FornecedorVO.Fields.RAZAO_SOCIAL.toString()));		
		criteria.addOrder(Order.asc(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FornecedorVO.class));
		
		if (firstResult == null || maxResults == null) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, 100, orderProperty, asc);
		}
	}
	
	public List<ScoFornecedor> listarFornecedoresAtivos(final Object pesquisa) {
		return listarFornecedoresAtivos(pesquisa, null, null, null, false);
	}

	/**
	 * Retorna ScoFornecedor de acordo com o numero (PK) informado
	 * @param numero
	 * @return
	 */
	public ScoFornecedor obterFornecedorPorNumero(final Integer numero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		criteria.createAlias(ScoFornecedor.Fields.CIDADE.toString(), "CITY", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(),numero));
		
		return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * select distinct frn.numero, frn.cgc, frn.cpf, frn.razao_social from
	 * sco_fornecedores frn, sco_propostas_fornecedores pfr,
	 * sco_item_propostas_fornecedor ipfr where frn.numero = pfr.frn_numero and
	 * pfr.lct_numero = ipfr.pfr_lct_numero and pfr.frn_numero =
	 * ipfr.pfr_frn_numero and ipfr.ind_escolhido = 'S' and pfr.lct_numero =
	 * :lct_numero and exists ( select 1 from sco_autorizacoes_forn afn where
	 * afn.pfr_lct_numero = pfr.lct_numero and afn.ind_exclusao = 'N' and
	 * afn.ind_situacao <> 'EF');
	 * @param param
	 * @param licitacao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ScoFornecedor> listarFornecedorPropostaAceita(
			final Object param, final ScoLicitacao licitacao) {
		final String pr = (String) param;
		final StringBuilder str = new StringBuilder(
				"select distinct frn from ScoFornecedor frn, ScoPropostaFornecedor pfr, ScoItemPropostaFornecedor ipfr")
				.append(" where frn.numero = pfr.id.frnNumero")
				.append(" and pfr.id.lctNumero = ipfr.id.pfrLctNumero")
				.append(" and pfr.id.frnNumero = ipfr.id.pfrFrnNumero");
		if (!StringUtils.isBlank(pr) && !CoreUtil.isNumeroInteger(pr)) {
			str.append(" and frn.razaoSocial like :INPUTPARAM");
		} else if (CoreUtil.isNumeroInteger(pr)){
			str.append(" and frn.numero =:INPUTPARAM");
		}
		str.append(" and ipfr.indEscolhido = :ITMPROP_ESC")
				.append(" and pfr.id.lctNumero = :LICNUM")
				.append(" and exists ( from ScoAutorizacaoForn afn")
				.append(" where afn.propostaFornecedor.id.lctNumero = pfr.id.lctNumero")
				.append(" and afn.exclusao = :AFN_IND_EX_PARAM")
				.append(" and afn.situacao <> :AFN_SIT_PARAM) order by frn.numero asc");
		final Query q = createHibernateQuery(str.toString());
		if (pr != null && !pr.equals("") && !CoreUtil.isNumeroInteger(pr)){
			q.setString("INPUTPARAM", "%" + pr.trim().toUpperCase() + "%");
		} else if (CoreUtil.isNumeroInteger(pr.trim())){
			q.setParameter("INPUTPARAM", Integer.parseInt(pr));
		}
		q.setParameter("ITMPROP_ESC", Boolean.TRUE);
		q.setParameter("LICNUM", licitacao.getNumero());
		q.setParameter("AFN_IND_EX_PARAM", Boolean.FALSE);
		q.setParameter("AFN_SIT_PARAM", DominioSituacaoAutorizacaoFornecimento.EF);
		return q.list();
	}
	public List<ScoFornecedor> obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(String param, Short almoxSeq, Integer materialCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		String strPesquisa = param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}

		StringBuffer sqlSub = new StringBuffer("{alias}.numero in (select 	eal.frn_numero")
									.append("					from    agh.sce_estq_almoxs  eal")
									.append("					where  	1=1");
						
		int objCounter = 0;
		int typeCounter = 0;
		Object[] values = new Object[2];
		Type[] types = new Type[2];
		
		if(almoxSeq!=null){
			sqlSub.append("					and		eal.alm_seq  =  ? ");
			values[objCounter++] = almoxSeq;
			types[typeCounter++] = ShortType.INSTANCE;
		}
		
		if(materialCodigo!=null){
			sqlSub.append("					and 	eal.mat_codigo = ? ");
			values[objCounter++] = materialCodigo;
			types[typeCounter++] = IntegerType.INSTANCE;
		}
		
		sqlSub.append(')');
		if(objCounter > 1){
			criteria.add(Restrictions.sqlRestriction(sqlSub.toString(), values, types));
		}else if(objCounter == 1){
			if(almoxSeq != null){
				criteria.add(Restrictions.sqlRestriction(sqlSub.toString(), (Object)almoxSeq, ShortType.INSTANCE));
			}else{
				criteria.add(Restrictions.sqlRestriction(sqlSub.toString(), (Object)materialCodigo, IntegerType.INSTANCE));
			}
		}
		return executeCriteria(criteria, 0, 100, "razaoSocial", true);
	}
	
	public List<ScoFornecedor> obterFornecedor(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		String strPesquisa = param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.parseInt(strPesquisa)));			
			} else {
				criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, "numero", true);
	}
	
	/**
	 * Retorna fornecedores de acordo com o número ou razão social
	 * @param pesquisa
	 * @return List<ScoFornecedor> 
	 */
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(Object pesquisa){
		return pesquisarFornecedoresPorNumeroRazaoSocial(pesquisa, false);
	}
	
	/**
	 * Retorna fornecedores de acordo com o número ou razão social
	 * @param pesquisa
	 * @param apenasAtivos 
	 * @return List<ScoFornecedor> 
	 */
	private List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(Object pesquisa, boolean apenasAtivos) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoFornecedor.class);
		String strParametro = (String) pesquisa;
		Integer numero = null;		
		if(apenasAtivos){
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.SITUACAO.toString(),
					DominioSituacao.A));	
		}

		if(CoreUtil.isNumeroInteger(strParametro)){
			numero = Integer.valueOf(strParametro);
		}
		if (numero != null) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), numero));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {				
				Criterion criterionRazaoSocial = Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),strParametro, MatchMode.ANYWHERE);
				Criterion criterionNomeFantasia = Restrictions.ilike(ScoFornecedor.Fields.NOME_FANTASIA.toString(),strParametro, MatchMode.ANYWHERE);				
				criteria.add(Restrictions.or(criterionRazaoSocial, criterionNomeFantasia));
			}
		}

		criteria.addOrder(Order.asc(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(Object parametro, int firstResult, int maxResults) {		
		DetachedCriteria criteria = montarCriteriaPorNumeroRazaoSocialNomeFantasia(parametro);
		return executeCriteria(criteria, firstResult, maxResults, null, true);
	}

	private DetachedCriteria montarCriteriaPorNumeroRazaoSocialNomeFantasia(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");
		String strParametro = (String) parametro;
		Integer numero = null;
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.NUMERO.toString()),ScoFornecedor.Fields.NUMERO.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),ScoFornecedor.Fields.RAZAO_SOCIAL.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.NOME_FANTASIA.toString()),ScoFornecedor.Fields.NOME_FANTASIA.toString());
		criteria.setProjection(p);
		
		if(CoreUtil.isNumeroInteger(strParametro)){
			numero = Integer.valueOf(strParametro);
		}

		if (numero != null) {
			criteria.add(Restrictions.eq("FORN." +ScoFornecedor.Fields.NUMERO.toString(), numero));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {				
				criteria.add(Restrictions.or(Restrictions.ilike("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),
						strParametro, MatchMode.ANYWHERE),Restrictions.ilike("FORN." + ScoFornecedor.Fields.NOME_FANTASIA.toString(),strParametro, MatchMode.ANYWHERE)));
			}
		}

		criteria.addOrder(Order.asc("FORN." +ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.addOrder(Order.asc("FORN." +ScoFornecedor.Fields.NOME_FANTASIA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ScoFornecedor.class));
		return criteria;
	}
	
	/**
	 * Pesquisa fornecedor por vários campos de fornecedor que estejam preenchidos.
	 */
	public List<ScoFornecedor> pesquisarFornecedorCompleta(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc,  ScoFornecedor fornecedor, String cpfCnpj){
		LOG.info("pesquisarFornecedorCompleta 1");
		//Se nenhuma propriedade for definida, ordena por numero
		if(orderProperty == null){
			orderProperty = ScoFornecedor.Fields.NUMERO.toString();
		}		
		//Ascendete se não especificado de outra forma
		if(asc == null){
			asc = true;
		}
		DetachedCriteria criteria = montarCriteriaPesquisaCompleta(fornecedor, cpfCnpj);

		criteria.createAlias("FORN."+ScoFornecedor.Fields.CIDADE.toString(), "CITY", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CITY."+AipCidades.Fields.UF.toString(), "UF", JoinType.LEFT_OUTER_JOIN);
		
		LOG.info("pesquisarFornecedorCompleta 3");
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	public Long pesquisarFornecedorCompletaCount(ScoFornecedor fornecedor, String cpfCnpj){
		return this.executeCriteriaCount(montarCriteriaPesquisaCompleta(fornecedor, cpfCnpj));
	}
		
	/**
	 * @return
	 */
	private String obterPadrao(String padrao, int tamanho) {
		StringBuilder retorno = new StringBuilder();		
		for(int i = 0; i < tamanho; i++) {
			retorno.append(padrao);
		}
		return retorno.toString();
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private DetachedCriteria montarCriteriaPesquisaCompleta(ScoFornecedor fornecedor, String cpfCnpj) {
		
		LOG.info("montarCriteriaPesquisaCompleta");
		LOG.info("cpfcnpj: " + cpfCnpj);
		LOG.info("fornecedor razaoSocial: " + fornecedor.getRazaoSocial());		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");
		
		if(fornecedor.getNumero() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.NUMERO.toString(), fornecedor.getNumero()));
		}
		
		if(fornecedor.getTipoFornecedor() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.TIPO_FORNECEDOR.toString(), fornecedor.getTipoFornecedor()));
		}
		
		if(fornecedor.getRazaoSocial() != null && !StringUtils.isEmpty(fornecedor.getRazaoSocial())){
			criteria.add(Restrictions.ilike("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), fornecedor.getRazaoSocial(),MatchMode.ANYWHERE));
		}
		
		if(fornecedor.getNomeFantasia() != null && !StringUtils.isEmpty(fornecedor.getNomeFantasia())){
			criteria.add(Restrictions.ilike("FORN." + ScoFornecedor.Fields.NOME_FANTASIA.toString(), fornecedor.getNomeFantasia(),MatchMode.ANYWHERE));
		}
		
		if(fornecedor.getSituacao() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.SITUACAO.toString(), fornecedor.getSituacao()));
		}
		
		if(cpfCnpj != null){
			
			String cpf = "";
			String cnpj= "";			
			Long valor = null;
			int tam = 0;
			
			if(cpfCnpj != null && !"".equals(cpfCnpj)) {
				tam = cpfCnpj.length();
				valor = Long.valueOf(cpfCnpj);				
			}
			
			if(valor != null) {
				cpf = valor.toString() + obterPadrao("_", 11 - tam);
				cnpj = valor.toString() + obterPadrao("_", 14 - tam);
			}

			if(cpfCnpj.length() != 0) {
				//verifica se tem tamanho 14, caso positivo, comparar só com a coluna CGC
				if(cpfCnpj.length() == 14){
					//criteria.add(Restrictions.like("FORN." + ScoFornecedor.Fields.CGC.toString(), cnpj));
					StringBuilder sqlRestriction = new StringBuilder(); 
					sqlRestriction.append(" to_char({alias}.");
					sqlRestriction.append(ScoFornecedor.Fields.CGC.toString());
					sqlRestriction.append(",'FM99999999999999') like '");
					sqlRestriction.append(cnpj ).append( '\'');
					criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
				} else {
					StringBuilder sqlRestriction = new StringBuilder(); 
					sqlRestriction.append("( to_char({alias}.");
					sqlRestriction.append(ScoFornecedor.Fields.CGC.toString());
					sqlRestriction.append(",'FM99999999999999') like '");
					sqlRestriction.append(cnpj);
					sqlRestriction.append("' OR to_char({alias}.");
					sqlRestriction.append(ScoFornecedor.Fields.CPF.toString());
					sqlRestriction.append(",'FM99999999999') like '");
					sqlRestriction.append(cpf ).append( "')");
					criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
				}			
			} 
		}
		
		if(fornecedor.getInscricaoEstadual() != null && !StringUtils.isEmpty(fornecedor.getInscricaoEstadual())){
			criteria.add(Restrictions.like("FORN." + ScoFornecedor.Fields.INSCRICAO_ESTADUAL.toString(), fornecedor.getInscricaoEstadual(),MatchMode.START));
		}		
		if(fornecedor.getClassificacaoEconomica() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.CLASSIFICACAO_ECONOMICA.toString(), fornecedor.getClassificacaoEconomica()));
		}		
		if(fornecedor.getDtValidadeFgts() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString(), new Date(), fornecedor.getDtValidadeFgts()));
		}		
		if(fornecedor.getDtValidadeInss() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString(), new Date(), fornecedor.getDtValidadeInss()));
		}		
		if(fornecedor.getDdd() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.DDD.toString(), fornecedor.getDdd()));
		}		
		if(fornecedor.getFone() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.FONE.toString(), fornecedor.getFone()));
		}		
		if(fornecedor.getCaixaPostal() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.CAIXA_POSTAL.toString(), fornecedor.getCaixaPostal()));
		}		
		if(fornecedor.getDtAlteracao() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.DT_ALTERACAO.toString(), fornecedor.getDtAlteracao()));
		}		
		if(fornecedor.getDtValidadeCrc() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_CRC.toString(), new Date(), fornecedor.getDtValidadeCrc()));
		}		
		if(fornecedor.getCrc() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.CRC.toString(), fornecedor.getCrc()));
		}		
		if(fornecedor.getClassificacao() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.CLASSIFICACAO.toString(), fornecedor.getClassificacao()));
		}		
		if(fornecedor.getCidade() != null){
			criteria.add(Restrictions.eq("FORN." + ScoFornecedor.Fields.CIDADE.toString(), fornecedor.getCidade()));
		}		
		if(fornecedor.getDtValidadeRecFed() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString(), new Date(), fornecedor.getDtValidadeRecFed()));
		}		
		if(fornecedor.getDtValidadeAvs() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_AVS.toString(), new Date(), fornecedor.getDtValidadeAvs()));
		}		
		if(fornecedor.getDtValidadeRecEst() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_RECEST.toString(), new Date(), fornecedor.getDtValidadeRecEst()));
		}		
		if(fornecedor.getDtValidadeRecMun() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_RECMUN.toString(), new Date(), fornecedor.getDtValidadeRecMun()));
		}		
		if(fornecedor.getDtValidadeBal() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_BAL.toString(), new Date(), fornecedor.getDtValidadeBal()));
		}		
		if(fornecedor.getDtValidadeCNDT() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_CNDT.toString(), new Date(), fornecedor.getDtValidadeCNDT()));
		}		
		if(fornecedor.getDtValidadeCBP() != null){
			criteria.add(Restrictions.between("FORN." + ScoFornecedor.Fields.DT_VALIDADE_CBP.toString(), new Date(), fornecedor.getDtValidadeCBP()));
		}		
		if(fornecedor.getIndiceLiquidezCorrente() != null){
			criteria.add(Restrictions.ge("FORN." + ScoFornecedor.Fields.INDICE_LIQUIDEZ_CORRENTE.toString(), fornecedor.getIndiceLiquidezCorrente()));
		}		
		if(fornecedor.getIndiceLiquidezGeral() != null){
			criteria.add(Restrictions.ge("FORN." + ScoFornecedor.Fields.INDICE_LIQUIDEZ_GERAL.toString(), fornecedor.getIndiceLiquidezGeral()));
		}		
		if(fornecedor.getValorPatrimLiq() != null){
			criteria.add(Restrictions.ge("FORN." + ScoFornecedor.Fields.VALOR_PATRIM_LIQ.toString(), fornecedor.getValorPatrimLiq()));
		}
		return criteria;
	}
	/**
	 * Pesquisa realizada pela suggestion de Fornecedor
	 * @param objPesquisa
	 * @param almSeq
	 * @param matCodigo
	 * @return
	 */
	public List<ScoFornecedor> pesquisarFornecedoresSaldoEstoque(Object objPesquisa, Short almSeq, Integer matCodigo, Integer maxResults) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FRN");

		String strParametro = (String) objPesquisa;
		Integer numero = null;
		
		if(CoreUtil.isNumeroInteger(strParametro)){
			numero = Integer.valueOf(strParametro);
		}

		if (numero != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numero));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {				
				Criterion criterionRazaoSocial = Restrictions.ilike("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),strParametro, MatchMode.ANYWHERE);
				Criterion criterionNomeFantasia = Restrictions.ilike("FRN." + ScoFornecedor.Fields.NOME_FANTASIA.toString(),strParametro, MatchMode.ANYWHERE);				
				criteria.add(Restrictions.or(criterionRazaoSocial, criterionNomeFantasia));
			}
		}
		
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.SITUACAO.toString(), DominioSituacao.A));	
		
		final DetachedCriteria subQuerie = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		subQuerie.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN2", JoinType.INNER_JOIN);
		subQuerie.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		subQuerie.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		subQuerie.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		subQuerie.setProjection(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()));
		subQuerie.add(Property.forName("FRN." + ScoFornecedor.Fields.NUMERO.toString()).eqProperty("FRN2." + ScoFornecedor.Fields.NUMERO.toString()));		
		criteria.add(Subqueries.exists(subQuerie));
		
		return executeCriteria(criteria, 0, maxResults, null, false);
	}

	private String formatCpf(Long cpf) {
		StringBuilder cpfSb = new StringBuilder(String.format("%011d", cpf));		
		cpfSb.insert(3, '.');
		cpfSb.insert(7, '.');
		cpfSb.insert(11, '-');		
		return cpfSb.toString();
	}
	
	public String montarRazaoSocialFornecedor(ScoFornecedor fornecedor) {
		StringBuilder razaoSocial = new StringBuilder();
		
		if (fornecedor.getCgc() != null) {
			razaoSocial.append(CoreUtil.formatarCNPJ(fornecedor.getCgc()));
		} else {
			razaoSocial.append(formatCpf(fornecedor.getCpf()));			
		}
		razaoSocial.append('-');
		razaoSocial.append(fornecedor.getRazaoSocial());
		
		return razaoSocial.toString();
	}
	
	public String obterCnpjCpfFornecedorFormatado(ScoFornecedor fornecedor) {
		if (fornecedor != null) {
			fornecedor = this.obterPorChavePrimaria(fornecedor.getNumero());
			return fornecedor.getCgc() != null ? CoreUtil.formatarCNPJ(fornecedor.getCgc()) : formatCpf(fornecedor.getCpf());
		}
		return "";
	}

	public String obterFornecedorDescricao(final Integer numero) {
		if (numero != null) {
			ScoFornecedor fornecedor = this.obterPorChavePrimaria(numero);
			return fornecedor.getNumero() + " - " + fornecedor.getRazaoSocial() + " - " + fornecedor.getNomeFantasia();
		}
		return "";
	}
	
	/**
	 * Lista os fornecedores com propostas para determinada licitacao para suggestions
	 * @return
	 */
	public List<ScoFornecedor> listarFornecedoresComProposta(Object pesquisa, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer numeroPac) {
			
		DetachedCriteria criteria = criarCriteriaFornecedoresAtivos(pesquisa);
		
    	DetachedCriteria subQueryProposta = DetachedCriteria.forClass(ScoPropostaFornecedor.class, "PROP");		
    	subQueryProposta.setProjection(Projections.property("PROP." + ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()));
    	
    	if (numeroPac != null) {
    		subQueryProposta.add(Restrictions.eq("PROP." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), numeroPac));
    	}
    	
    	subQueryProposta.add(Restrictions.eq("PROP." + ScoPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));			
		criteria.add(Subqueries.propertyIn(ScoFornecedor.Fields.NUMERO.toString(), subQueryProposta));
		criteria.addOrder(Order.asc(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		
		if (firstResult == null || maxResults == null) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResults,
					orderProperty, asc);
		}		
	}
	
	/**
	 * obter os fornecedores com propostas por af
	 */
	public ScoFornecedor obterFornecedorComPropostaPorAF(ScoAutorizacaoForn scoAutorizacaoForn) {			
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");		
    	DetachedCriteria subQueryAF = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AUT");
    	subQueryAF.createAlias("AUT." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP");	    
    	subQueryAF.add(Restrictions.eq("AUT." + ScoAutorizacaoForn.Fields.NUMERO.toString(), scoAutorizacaoForn.getNumero()));
    	subQueryAF.add(Property.forName("PROP." +  ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()).eqProperty("FORN." + ScoFornecedor.Fields.NUMERO.toString()));
	   	subQueryAF.setProjection(Projections.property("PROP." + ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()));
    	criteria.add(Subqueries.exists(subQueryAF));
    	
    	return (ScoFornecedor) executeCriteriaUniqueResult(criteria);    	
	}

	/**
	 * Obtem o fornecedor pelo numero da AF
	 * @param numeroAf
	 * @return ScoFornecedor
	 */
	public ScoFornecedor obterFornecedorPorNumeroAf(Integer numeroAf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");
		
    	DetachedCriteria subQueryAf = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
    	subQueryAf.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP");
    	subQueryAf.setProjection(Projections.property("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()));
    	subQueryAf.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAf));    	
    	criteria.add(Subqueries.propertyIn("FORN."+ScoFornecedor.Fields.NUMERO.toString(), subQueryAf));
    	
    	return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}

	public DetachedCriteria criarFornAtivosComPropostaAceitaAfsSemContratos() {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");
		criteria.createAlias("FORN." + ScoFornecedor.Fields.PROPOSTAS.toString(), "PFR");
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.ITEM.toString(), "IPF",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.ITENS_AUT_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), "LCT");
		
		criteria.add(Restrictions.eq("IPF."	+ ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(),Boolean.TRUE));
		
		final DetachedCriteria subQuerieAF = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");
		subQuerieAF.setProjection(Projections.property("AF."+ ScoAutorizacaoForn.Fields.LICITACAO.toString()));
		subQuerieAF.add(Property.forName("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).eqProperty("LCT." + ScoLicitacao.Fields.NUMERO.toString()));
		subQuerieAF.add(Restrictions.eq("AF."+ ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString(),Boolean.FALSE));
		subQuerieAF.add(Restrictions.eq("AF."+ ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(),DominioModalidadeEmpenho.CONTRATO));
		
		DominioSituacaoAutorizacaoFornecimento[] situacoesInvalidas = {DominioSituacaoAutorizacaoFornecimento.EX,DominioSituacaoAutorizacaoFornecimento.EF };
		Criterion in = Restrictions.in(ScoAutorizacaoForn.Fields.SITUACAO.toString(),situacoesInvalidas);
		subQuerieAF.add(Restrictions.not(in));
		
		final DetachedCriteria subQuerieAFContrato = DetachedCriteria.forClass(ScoAfContrato.class, "AFC");
		subQuerieAFContrato.setProjection(Projections.property("AFC."+ ScoAfContrato.Fields.SEQ.toString()));
		subQuerieAFContrato.add(Property.forName("AFC." + ScoAfContrato.Fields.AUT_FORN.toString()).eqProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subQuerieAF.add(Subqueries.notExists(subQuerieAFContrato));
		criteria.add(Restrictions.or(Restrictions.eq("AFN."+ ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(),DominioModalidadeEmpenho.CONTRATO), Subqueries.exists(subQuerieAF)));
		criteria.add(Subqueries.exists(criteria));
		
		return criteria;
	}

	/**
	 * Listar fornecedores com proposta aceita para a licitacao em andamento ou
	 * o fornecedores vinculados as Afs ainda nao associadas a um contrato.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<ScoFornecedor> listarFornAtivosComPropostaAceitaAfsSemContratos(
			final Object pesquisa) {

		final DetachedCriteria criteria = criarFornAtivosComPropostaAceitaAfsSemContratos();
		String strParametro = pesquisa.toString();
		Integer seq = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seq = Integer.valueOf(strParametro);
		}
		
		if (seq != null) {
			
			criteria.add(Restrictions.eq(
					"FORN." + ScoFornecedor.Fields.NUMERO.toString(), seq));
			
		} else {

			if (pesquisa == null || StringUtils.isBlank(pesquisa.toString())) {
				return null;
			}

			strParametro = pesquisa.toString();
			Long cpfOuCgc = null;

			if (CoreUtil.isNumeroLong(strParametro)) {
				
				cpfOuCgc = Long.valueOf(strParametro);
				
			}

			if (cpfOuCgc == null) {
				
				criteria.add(Restrictions.ilike("FORN."
						+ ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),
						strParametro, MatchMode.ANYWHERE));
			} else {
				
				criteria.add(Restrictions.or(Restrictions.eq("FORN."
						+ ScoFornecedor.Fields.CGC.toString(), cpfOuCgc),
						Restrictions.eq("FORN." + ScoFornecedor.Fields.CPF.toString(),cpfOuCgc)));
			}
		}
		
		return executeCriteria(criteria);
	}
	
	public List<ScoFornecedor> listarFornecedoresPorNumeroCnpjRazaoSocial(final Object param) {
		DetachedCriteria criteria = obterCriteriaFornecedorNumeroCnpjRazaoSocial(param);
		return executeCriteria(criteria, 0, 100, "numero", true);
	}
	
	public Long listarFornecedoresPorNumeroCnpjRazaoSocialCount(final Object param) {
		DetachedCriteria criteria = obterCriteriaFornecedorNumeroCnpjRazaoSocial(param);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaFornecedorNumeroCnpjRazaoSocial(final Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		String textoPesquisa = param.toString();
		if (CoreUtil.isNumeroInteger(textoPesquisa)) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.valueOf(textoPesquisa)));
		} else if (CoreUtil.isNumeroLong(textoPesquisa)) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), Long.valueOf(textoPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), textoPesquisa, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public List<ScoFornecedor> listarFornecedoresAtivosPorNumeroCnpjRazaoSocial(final Object param) {
		DetachedCriteria criteria = obterCriteriaFornecedorAtivosNumeroCnpjRazaoSocial(param);
		return executeCriteria(criteria, 0, 100, "numero", true);
	}
	
	public Long listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(final Object param) {
		DetachedCriteria criteria = obterCriteriaFornecedorAtivosNumeroCnpjRazaoSocial(param);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaFornecedorAtivosNumeroCnpjRazaoSocial(final Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		
		String textoPesquisa = param.toString();
		
		if (textoPesquisa.length() == 13) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), Long.valueOf(textoPesquisa)));
		} else if (CoreUtil.isNumeroInteger(textoPesquisa)) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.valueOf(textoPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), textoPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}

	/**
	 * Obtem o fornecedor pelo número AF
	 * @param numeroAutorizacaoFornecedor numero AF
	 * @return fornecedor
	 */
	public ScoFornecedor obterFornecedorPorAF(Integer numeroAutorizacaoFornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "FORN");
		
    	DetachedCriteria subQueryAF = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AUT");
    	subQueryAF.add(Restrictions.eq("AUT." + ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAutorizacaoFornecedor));
	   	subQueryAF.add(Restrictions.eqProperty("FORN." + ScoFornecedor.Fields.NUMERO.toString(), "AUT." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
	   	subQueryAF.setProjection(Projections.property("AUT." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
    	criteria.add(Subqueries.exists(subQueryAF));
    	
    	return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Dia favorável para entrega
	 * C7 de #5554 - Programação automática de Parcelas AF
	 * @param numero
	 * @return
	 */
	public Byte buscarDiaFavoravel(Integer numero) {
		ScoFornecedor fornecedor = super.obterPorChavePrimaria(numero);
		return fornecedor != null ? fornecedor.getDiaFavEntgMaterial() : null;
	}
	
	/**
	 * Restrição da listagem de datas de vencimento VO.
	 * @param fornecedor dados referente ao fornecedor
	 * @param dataAtual dataAtual
	 * @return coleção de data vencimento VO
	 */
	public List<DatasVencimentosFornecedorVO> listarDatasVencimentoFornecedor(VScoFornecedor fornecedor) {
		
		Date data = new Date();
	    // Tabelas de titulos - AGH.FCP_TITULOS
	    DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class,"TTL");
	    // Tabela de nota de recebimentos relacionada com tabela de titulos - AGH.SCE_NOTA_RECEBIMENTOS
	    criteria.createAlias("TTL."+FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);
	    // Tabela de documento fiscal de entrada relacionada com tabela de nota de recebimentos - AGH.SCE_DOCUMENTO_FISCAL_ENTRADAS
	    criteria.createAlias("NRS."+SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
	    // Tabela de fornecedores relacionada com tabela de documento fiscal de entrada - AGH.SCO_FORNECEDORES
	    criteria.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);  
	    // Projection, campos a serem retornados
	    Projection proj = Projections.projectionList()
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), DatasVencimentosFornecedorVO.Fields.CGC.toString())
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), DatasVencimentosFornecedorVO.Fields.CPF.toString())
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), DatasVencimentosFornecedorVO.Fields.RAZAO_SOCIAL.toString())
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString()), DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_FGTS.toString())
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString()), DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_INSS.toString())
	      .add(Projections.property("FRN." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString()), DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_RECEITA_FEDERAL.toString());
	    criteria.setProjection(Projections.distinct(proj));
	    // Where - Tipo de Fornecedor
	    criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.TIPO_FORNECEDOR.toString(), DominioTipoFornecedor.FNA)); 
	    // Where - CPF do Fornecedor
	    criteria.add(Restrictions.isNull("FRN."+ScoFornecedor.Fields.CPF.toString()));
	    // Where - Situação
	    criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.IND_SITUACAO.toString(), DominioSituacaoTitulo.APG));
	    // Where - Estorno
	    criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));
	    // Where - Data de Vencimento
	    criteria.add(Restrictions.le("TTL."+FcpTitulo.Fields.DT_VENCIMENTO.toString(), data)); 
	    // Where - Data
	    criteria.add(Restrictions.or(
	     Restrictions.or(Restrictions.isNull("FRN."+ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString()), Restrictions.lt("FRN."+ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString(), data)),
	       Restrictions.or(
	         Restrictions.or(Restrictions.isNull("FRN."+ScoFornecedor.Fields.DT_VALIDADE_INSS.toString()), Restrictions.lt("FRN."+ScoFornecedor.Fields.DT_VALIDADE_INSS.toString(), data)),
	         Restrictions.or(Restrictions.isNull("FRN."+ScoFornecedor.Fields.DT_VALIDADE_INSS.toString()), Restrictions.lt("FRN."+ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString(), data))
	       )
	     )
	    );
	    
	    // Where - Número do Fornecedor
	    if(fornecedor != null && fornecedor.getNumeroFornecedor() != null) {
	     criteria.add(Restrictions.le("FRN."+ScoFornecedor.Fields.NUMERO.toString(), fornecedor.getNumeroFornecedor()));
	    } 
	    // Ordenar por Razão Social
	    criteria.addOrder(Order.asc("FRN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
	    criteria.setResultTransformer(Transformers.aliasToBean(DatasVencimentosFornecedorVO.class));
	    return executeCriteria(criteria);
	}
	
	/**
	 * C1 - 27073
	 */
	public List<ScoFornecedorVO> buscarCertificadoDeRegistroDeCadastro(Integer numeroFornecedor){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, "scoFornecedor");
		criteria.createAlias("scoFornecedor."+ScoFornecedor.Fields.CIDADE.toString(), "aipCidades", Criteria.LEFT_JOIN);
		criteria.createAlias("scoFornecedor."+ScoFornecedor.Fields.SCO_FORN_RAMO_COMERCIAL.toString(), "scoFornRamoComercial");
		criteria.createAlias("scoFornRamoComercial."+ScoFornRamoComercial.Fields.SCO_RAMOS_COMERCIAIS, "scoRamosComercial");
		
		criteria.add(Restrictions.eq("scoFornecedor."+ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.CGC.toString()), ScoFornecedorVO.Fields.CGC.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.CPF.toString()), ScoFornecedorVO.Fields.CPF.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.CRC.toString()), ScoFornecedorVO.Fields.CRC.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_CRC.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_CRC.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_FGTS.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_INSS.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_RECEST.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_RECEST.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_RECFED.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_BAL.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_BAL.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_AVS.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_AVS.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_RECMUN.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_RECMUN.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.DT_VALIDADE_CNDT.toString()), ScoFornecedorVO.Fields.DT_VALIDADE_CNDT.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.IND_AFE.toString()), ScoFornecedorVO.Fields.IND_AFE.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ScoFornecedorVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.LOGRADOURO.toString()), ScoFornecedorVO.Fields.LOGRADOURO.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.NRO_LOGRADOURO.toString()), ScoFornecedorVO.Fields.NRO_LOGRADOURO.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.BAIRRO.toString()), ScoFornecedorVO.Fields.BAIRRO.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.CEP.toString()), ScoFornecedorVO.Fields.CEP.toString())
				.add(Projections.property("aipCidades." + AipCidades.Fields.NOME.toString()), ScoFornecedorVO.Fields.CIDADE.toString())
				.add(Projections.property("aipCidades." + AipCidades.Fields.UF_SIGLA.toString()), ScoFornecedorVO.Fields.UF_SIGLA.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.NUMERO.toString()), ScoFornecedorVO.Fields.NUMERO_FORNECEDOR.toString())
				.add(Projections.property("scoFornecedor." + ScoFornecedor.Fields.CLASSIFICACAO_ECONOMICA.toString()), ScoFornecedorVO.Fields.CLASSIFICACAO_ECONOMICA_FORNECEDOR.toString())
				.add(Projections.property("scoFornRamoComercial." + ScoFornRamoComercial.Fields.SCO_RAMOS_COMERCIAIS_CODIGO.toString()), ScoFornecedorVO.Fields.RMC_CODIGO.toString())
				.add(Projections.property("scoRamosComercial." + ScoRamoComercial.Fields.DESCRICAO.toString()), ScoFornecedorVO.Fields.DESCRICAO.toString()));
				
				
		criteria.setResultTransformer(Transformers.aliasToBean(ScoFornecedorVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	public List<ContasCorrentesFornecedorVO> buscarDadosContasCorrentesFornecedor(CadastroContasCorrentesFornecedorVO filtro) {
		DadosContasCorrentesFornecedorQueryProvider provider = new DadosContasCorrentesFornecedorQueryProvider();
		SQLQuery query = createSQLQuery(provider.getQueryForList(filtro));
		
		return  query.
				addScalar("fornecedorNumero",IntegerType.INSTANCE).
				addScalar("fornecedorCGC",LongType.INSTANCE).
				addScalar("fornecedorCPF",LongType.INSTANCE).
				addScalar("fornecedorRazaoSocial",StringType.INSTANCE).
				addScalar("bancoCodigo",IntegerType.INSTANCE).
				addScalar("bancoNome",StringType.INSTANCE).
				addScalar("agenciaCodigo",IntegerType.INSTANCE).
				addScalar("agenciaDescricao",StringType.INSTANCE).
				addScalar("contaCorrente",StringType.INSTANCE).
				addScalar("indPreferencial",new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, provider.getPropertieForDomain())).
				setResultTransformer(Transformers.aliasToBean(ContasCorrentesFornecedorVO.class)).list();
	}
	
	//#43473 C1
	public ScoFornecedor pesquisarFornecedorRetiradaBemPermaAceiteTec(AceiteTecnicoParaSerRealizadoVO itemRecebimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SLC");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC."+ScoFaseSolicitacao.Fields.ITEM_RECEBIMENTO_PROVISORIO.toString(), "IRP");
		criteria.createAlias("IRP."+SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP");
		criteria.createAlias("IRP."+SceItemRecebProvisorio.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "SIA");
		criteria.createAlias("SIA."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("SIA."+ScoItemAutorizacaoForn.Fields.SCO_FORNECEDOR, "FORN");
		
		criteria.add(Restrictions.ne("PIRP."+PtmItemRecebProvisorios.Fields.STATUS.toString(), 7));
		criteria.add(Restrictions.ne("PIRP."+PtmItemRecebProvisorios.Fields.STATUS.toString(), 8));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), itemRecebimento.getRecebimento()));
		criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento.getItemRecebimento()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("FORN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString())))
				.add(Projections.property("FORN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()).as(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()))
				.add(Projections.property("FORN."+ScoFornecedor.Fields.CGC.toString()).as(ScoFornecedor.Fields.CGC.toString()))
				.add(Projections.property("FORN."+ScoFornecedor.Fields.CPF.toString()).as(ScoFornecedor.Fields.CPF.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoFornecedor.class));
		
		return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoFornecedor obterFornecedorPorCNPJ(final Long cnpj) {
		return obterFornecedorPorCNPJouCPF(cnpj, true);
	}
	public ScoFornecedor obterFornecedorPorCPF(final Long cpf) {
		return obterFornecedorPorCNPJouCPF(cpf, false);
	}
	private ScoFornecedor obterFornecedorPorCNPJouCPF(final Long numero, final boolean isCnpj) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		if (numero == null) {
			throw new IllegalArgumentException();
		}
		if (isCnpj) {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), numero));
		} else {
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), numero));
		}
		return (ScoFornecedor) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * #35690 C3
	 * Monta a consulta para suggestionBox de Fornecedor
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @return Consulta para suggestionBox
	 */
	private DetachedCriteria montarCriteriaPesquisarFornecedorNumCNPJRazao(String filtro){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		if (filtro != null && StringUtils.isNotBlank(filtro)) {
			Disjunction ou = Restrictions.or(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),filtro, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroLong(filtro)) {
				ou.add(Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), Long.valueOf(filtro)),
						Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), Long.valueOf(filtro))));
			}
			if (CoreUtil.isNumeroInteger(filtro)) {
				ou.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.valueOf(filtro)));
			}
			criteria.add(ou);
		}
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoFornecedor.Fields.NUMERO.toString())
					, FornecedorVO.Fields.NUMERO.toString())
				.add(Projections.property(ScoFornecedor.Fields.CGC.toString())
					, FornecedorVO.Fields.CGC.toString())
				.add(Projections.property(ScoFornecedor.Fields.CPF.toString())
					, FornecedorVO.Fields.CPF.toString())
				.add(Projections.property(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
					, FornecedorVO.Fields.RAZAO_SOCIAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FornecedorVO.class));
		return criteria;
	}
	/**
	 * #35690 C3
	 * Realiza a consulta para suggestionBox de Fornecedor
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @return Lista de Fornecedores de acordo com o filtro
	 */
	public List<FornecedorVO> pesquisarFornecedorNumCNPJRazao(String filtro){
		final DetachedCriteria criteria = montarCriteriaPesquisarFornecedorNumCNPJRazao(filtro); 
		return executeCriteria(criteria, 0, 100, ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), true);
	}
	/**
	 * #35690 C3
	 * Count da consulta para suggestionBox de Fornecedor
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @return Quantidade de Fornecedor retornado de acordo com o filtro
	 */
	public Long pesquisarFornecedorNumCNPJRazaoCount(String filtro){
		DetachedCriteria criteria = montarCriteriaPesquisarFornecedorNumCNPJRazao(filtro);
		return executeCriteriaCount(criteria);
	}
	/**
	 * C4 - consulta fornecedores por Numero, CNPJ, CPF e Razao Social
	 * @param param
	 * @return
	 */
	public List<FornecedorVO> listarFornecedoresPorNumCnpjCpfRazaoSoc(final String param) {
		DetachedCriteria criteria = obterCriteriaFornecedorPorNumCnpjCpfRazaoSoc(param);
		return executeCriteria(criteria, 0, 100, ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), true);
	}
	public Long listarFornecedoresPorNumCnpjCpfRazaoSocCount(final String param) {
		DetachedCriteria criteria = obterCriteriaFornecedorPorNumCnpjCpfRazaoSoc(param);
		return executeCriteriaCount(criteria);
	}
	private DetachedCriteria obterCriteriaFornecedorPorNumCnpjCpfRazaoSoc(final String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		if (filtro != null && StringUtils.isNotBlank(filtro)) {
			Integer numero = null;
			Long cpfOuCgc = null;			
			if (CoreUtil.isNumeroLong(filtro)) {
				cpfOuCgc = Long.valueOf(filtro);
			}
			if (CoreUtil.isNumeroInteger(filtro)) {
				numero = Integer.valueOf(filtro);  
			}
			if (numero != null && cpfOuCgc != null) {
				criteria.add(Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), numero),
						Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), cpfOuCgc),
										Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), cpfOuCgc)),
					    Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),filtro, MatchMode.ANYWHERE)));
			} else if (cpfOuCgc != null) {
				criteria.add(Restrictions.or(Restrictions.eq(ScoFornecedor.Fields.CGC.toString(), cpfOuCgc),
						Restrictions.eq(ScoFornecedor.Fields.CPF.toString(), cpfOuCgc),
						Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),filtro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(),filtro, MatchMode.ANYWHERE));
		}
		}
		criteria.setProjection(Projections.projectionList().add(Projections.property(ScoFornecedor.Fields.NUMERO.toString()), FornecedorVO.Fields.NUMERO.toString()).add(Projections.property(ScoFornecedor.Fields.CGC.toString()), FornecedorVO.Fields.CGC.toString()).add(Projections.property(ScoFornecedor.Fields.CPF.toString()), FornecedorVO.Fields.CPF.toString()).add(Projections.property(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedorVO.Fields.RAZAO_SOCIAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FornecedorVO.class));
		return criteria;
	}
	/**
	 * #27143 - C1
	 * @author marcelo.deus <br/>
	 * Consulta para retornar os valores que irão preencher o relatório com as Entradas (NR) Sem Empenho ou Sem Assinatura da AF para Oracle
	 * @throws ApplicationBusinessException 
	 */
	public List<RelatorioESVO> listarRelatorioEntradasSemEmpenhoSemAssinaturaAF() throws ApplicationBusinessException{
		final StringBuilder hql = new StringBuilder();
		
		if(isOracle()){
			hql.append(relatorioEntradasSemEmpenhoSemAssinaturaAFOracleQueryBuilder.montarQueryListarRelatorioEntradasSemEmpenhoSemAssinaturaAFOracle());
		} else if(isPostgreSQL()){
			hql.append(relatorioEntradasSemEmpenhoSemAssinaturaAFPostgresQueryBuilder.montarQueryListarRelatorioEntradasSemEmpenhoSemAssinaturaAFPostgres());
		}

		final SQLQuery query = createSQLQuery(hql.toString());
		
		try {
			   query.addScalar(RelatorioESVO.Fields.SITUACAO.toString(), StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.AFN_NUMERO.toString(), IntegerType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.FORNECEDOR.toString(), StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.CAMPO021.toString(), StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.AF.toString(), StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.SIT_AF.toString(), StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.NR.toString(), IntegerType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.DTENTRADA.toString(), DateType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.DTEMISNF.toString(), DateType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.MODLLICT.toString(), StringType.INSTANCE )
					.addScalar(RelatorioESVO.Fields.ARTIGO.toString(),  StringType.INSTANCE)
					.addScalar(RelatorioESVO.Fields.INCISO.toString(), StringType.INSTANCE )
					.setResultTransformer(new AliasToBeanConstructorResultTransformer(RelatorioESVO.class.getConstructor(
										String.class, Integer.class, String.class, String.class, String.class, String.class,
										Integer.class, Date.class, Date.class, String.class, String.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		return query.list();
	}
	public FornecedorVO obterFornecedorVOPorRegistroDocumento(Integer numero){		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class);
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(),numero));		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoFornecedor.Fields.NUMERO.toString()), FornecedorVO.Fields.NUMERO.toString())
				.add(Projections.property(ScoFornecedor.Fields.CGC.toString()), FornecedorVO.Fields.CGC.toString())
				.add(Projections.property(ScoFornecedor.Fields.CPF.toString()), FornecedorVO.Fields.CPF.toString())
				.add(Projections.property(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedorVO.Fields.RAZAO_SOCIAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FornecedorVO.class));
		return (FornecedorVO) executeCriteriaUniqueResult(criteria);
	}
	public List<FornecedoresComEntregasPendentesVO> listarFornecedoresComEntregasPendentes(Integer arg0, Integer arg1, String arg2, 
			boolean arg3, FornecedoresComEntregasPendentesVO filtro) throws ApplicationBusinessException{
		FornecedoresComEntregasPendentesQueryBuilder fornecedoresComEntregasPendentesQueryBuilder = new FornecedoresComEntregasPendentesQueryBuilder();
		List<FornecedoresComEntregasPendentesVO> listaGeral = new ArrayList<FornecedoresComEntregasPendentesVO>();
		DetachedCriteria criteria = fornecedoresComEntregasPendentesQueryBuilder.montarCriteriaListarFornecedoresComEntregasPendentes(arg0, arg1, arg2, arg3, filtro, false);
		List<FornecedoresComEntregasPendentesVO> listaServico = new ArrayList<FornecedoresComEntregasPendentesVO>();
		listaGeral = executeCriteria(criteria);
		if(filtro.getEsl() != null || (filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null)){
			DetachedCriteria criteria2 = fornecedoresComEntregasPendentesQueryBuilder.unionParaEslEMaterial(filtro);
			List<FornecedoresComEntregasPendentesVO> listaEsl =  executeCriteria(criteria2);
			if(!listaGeral.isEmpty()){
				listaGeral.addAll(listaEsl);
			}
		}
		if(filtro.getServico() != null && filtro.getServico().getCodigo() != null){
			DetachedCriteria criteria3 = fornecedoresComEntregasPendentesQueryBuilder.unionParaServico(filtro);
			fornecedoresComEntregasPendentesQueryBuilder.unionParaServico(filtro);
			listaServico =  executeCriteria(criteria3);
			if(!listaGeral.isEmpty()){
				listaGeral.addAll(listaServico);
			}
		}
		return listaGeral;
	}
	public Long listarFornecedoresComEntregasPendentesCount(boolean countTrue, FornecedoresComEntregasPendentesVO filtro) throws ApplicationBusinessException{
		List<FornecedoresComEntregasPendentesVO> listaPraCount = listarFornecedoresComEntregasPendentes(null, null, StringUtils.EMPTY, false, filtro);
		if(listaPraCount != null){
			return Long.valueOf(listaPraCount.size());
		} else {
			return 0L;
		}
	}	
	public List<FornecedorVO> obterFornecedorVOPorNumero(String pesquisa){
		if (pesquisa != null && StringUtils.isNotEmpty(pesquisa) && !CoreUtil.isNumeroInteger(pesquisa)){
			return new ArrayList<FornecedorVO>();
		}		 
		return executeCriteria((new FornecedorVOPorNumeroQueryBuilder()).build(pesquisa), 0, 100, ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), true);
	}
	public Long obterFornecedorVOPorNumeroCount(String pesquisa){
		if (pesquisa != null && StringUtils.isNotEmpty(pesquisa) && !CoreUtil.isNumeroInteger(pesquisa)){
			return 0L;
		}
		return executeCriteriaCount((new FornecedorVOPorNumeroQueryBuilder()).build(pesquisa));
	}
	public RelacaoDeOrtesesProtesesVO buscarDadosFornecedoresICH(RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO) {
		DadosFornecedorICHQueryBuilder builder = new DadosFornecedorICHQueryBuilder();
		List<RelacaoDeOrtesesProtesesVO> lista = executeCriteria(builder.build(relacaoDeOrtesesProtesesVO));
		if (lista != null && !lista.isEmpty()) {
			Collections.sort(lista, Collections.reverseOrder(new Comparator<RelacaoDeOrtesesProtesesVO>(){
				@Override
				public int compare(RelacaoDeOrtesesProtesesVO o1, RelacaoDeOrtesesProtesesVO o2) {
					if (o1.getDatautl().compareTo(o2.getDatautl()) == 0) {
						Long cgcFornecedor = o1.getCgcfornecedor() == null ? 0 : o1.getCgcfornecedor();
						Long cgcFornecedor2 = o2.getCgcfornecedor() == null ? 0 : o2.getCgcfornecedor();
						return cgcFornecedor.compareTo(cgcFornecedor2); 
					}
					return o1.getDatautl().compareTo(o2.getDatautl());
				}
			}));
			return lista.get(0);
		}
		return null;
	}
}