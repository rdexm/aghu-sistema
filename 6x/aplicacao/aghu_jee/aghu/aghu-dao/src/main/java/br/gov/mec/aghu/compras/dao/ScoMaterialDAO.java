package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.RelatorioMedicamentosCAPVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;
import br.gov.mec.aghu.core.search.Lucene;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.dominio.DominioBaseAnaliseReposicao;
import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.estoque.vo.MateriaisParalClassificacaoVO;
import br.gov.mec.aghu.estoque.vo.MaterialMDAFVO;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.estoque.vo.MaterialVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDiarioMateriaisComSaldoAteVinteDiasVO;
import br.gov.mec.aghu.faturamento.vo.DadosConciliacaoVO;
import br.gov.mec.aghu.faturamento.vo.DadosMateriaisConciliacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.model.VScoUltComprasMat;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.NcssTypeCount"})
public class ScoMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMaterial> {
	
	private static final Integer MAX_RESULTS = 50;

	@Inject
    private Lucene lucene;
    
    @Inject
	private DataAccessService dataAcess;
    
	private static final long serialVersionUID = 3496177418986914188L;

	public long pesquisarCount(ScoMaterial material) {
		DetachedCriteria criteria = this.pesquisarCriteria(material);
		return this.executeCriteriaCount(criteria);
	}

	public List<ScoMaterial> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoMaterial material) {

		DetachedCriteria criteria = this.pesquisarCriteria(material);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<ScoMaterial> obterMateriaisOrteseseProteses(BigDecimal paramVlNumerico, Object objPesquisa) {
		ScoMaterial material = new ScoMaterial();
		material.setIndSituacao(DominioSituacao.A);

		if (StringUtils.isNotBlank((String) objPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				material.setCodigo((Integer) objPesquisa);
			} else {
				material.setNome((String) objPesquisa);
			}
		}

		DetachedCriteria criteria = pesquisarCriteria(material);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		if (paramVlNumerico != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(),
					Integer.valueOf(paramVlNumerico.toString())));
		}

		return this.executeCriteria(criteria);
	}
	
	private void ordernarCodigoENome(DetachedCriteria criteria) {
		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

	}
	
	private void adicionaClausulaCodigoAlmoxarifado(Short almCodigo, DetachedCriteria cri) {
		if (almCodigo != null) {
			cri.add(Restrictions.eq(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString(), almCodigo));
		}
	}
	
	private void adicionaClausulaGrupoMaterial(Integer gmtCodigo, DetachedCriteria cri) {
		if (gmtCodigo != null) {
			cri.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		}
	}
	
	private void adicionaClausulaIndicadorEstocavel(Boolean indEstocavel, DetachedCriteria cri) {
		if (indEstocavel != null) {
			cri.add(Restrictions.eq(ScoMaterial.Fields.IND_ESTOCAVEL.toString(), indEstocavel));
		}
	}

    public List<ScoMaterial> obterMateriaisOrteseseProtesesPrescricao(BigDecimal paramVlNumerico, Object  objPesquisa) {

        DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

        if (objPesquisa != null && !objPesquisa.toString().equals("")) {
            if (CoreUtil.isNumeroInteger(objPesquisa)) {
                Integer codigo = Integer.valueOf(objPesquisa.toString());
                criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigo));
            } else {
                criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), "%" + objPesquisa + "%", MatchMode.ANYWHERE));
            }
        }

        criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
        if (paramVlNumerico != null) {
            criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(),
                    Integer.valueOf(paramVlNumerico.toString())));
        }

        criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
        return this.executeCriteria(criteria);
    }


    public List<ScoMaterial> listarScoMateriaisPorGrupo(final Object objPesquisa, final Boolean indEstocavel, final Boolean pesquisarDescricao,Integer codGrupoMaterial ) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaMaterialPorGrupo(objPesquisa,
				indEstocavel, pesquisarDescricao, codGrupoMaterial);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		lista = executeCriteria(criteria, 0, 100, null, true);
		return lista;
	}

	public Long listarScoMateriaisPorGrupoCount(final Object objPesquisa, final Boolean indEstocavel, final Boolean pesquisarDescricao,Integer codGrupoMaterial ) {
		DetachedCriteria criteria = montarCriteriaMaterialPorGrupo(objPesquisa,
				indEstocavel, pesquisarDescricao, codGrupoMaterial);
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria montarCriteriaMaterialPorGrupo(
			final Object objPesquisa, final Boolean indEstocavel,
			final Boolean pesquisarDescricao, Integer codGrupoMaterial) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, pesquisarDescricao);
		if(codGrupoMaterial != null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codGrupoMaterial));
		}
		return criteria;
	}
	
	public Long listarScoMateriaisAtivosCount(Object objPesquisa , Boolean indEstocavel, Boolean pesquisarDescricao) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, pesquisarDescricao);
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(),DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}

	public List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa) {
		return obterMateriaisOrteseseProtesesAgenda(paramVlNumerico, objPesquisa, ScoMaterial.Fields.NOME.toString());
	}
	
	public List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa, String ordenacao) {
		List<ScoMaterial> scoMaterials = null;
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			DetachedCriteria criteria = getCriteriaMateriaisOrtesesProtesesAgendaCodigo(paramVlNumerico, objPesquisa);
			
			criteria.addOrder(Order.asc("mat." + ordenacao));
			scoMaterials = executeCriteria(criteria, 0, 100, null, true);
		} else {
			DetachedCriteria criteria2 = getCriteriaMateriaisOrtesesProtesesAgendaNome(paramVlNumerico, objPesquisa);

			criteria2.addOrder(Order.asc("mat." + ordenacao));
			List<ScoMaterial> list = executeCriteria(criteria2, 0, 100, null, true);
			for (ScoMaterial scoMaterial : list) {
				scoMaterial = obterPorChavePrimaria(scoMaterial.getCodigo());
			}
			return list;
		}
		if (scoMaterials == null || scoMaterials.isEmpty()) {
			DetachedCriteria criteria2 = getCriteriaMateriaisOrtesesProtesesAgendaNome(paramVlNumerico, objPesquisa);
            
			criteria2.addOrder(Order.asc(ordenacao));
			List<ScoMaterial> list = executeCriteria(criteria2, 0, 100, null, true);
			for (ScoMaterial scoMaterial : list) {
				scoMaterial = obterPorChavePrimaria(scoMaterial.getCodigo());
			}
			return list;
		} else {
			for (ScoMaterial scoMaterial : scoMaterials) {
				scoMaterial = obterPorChavePrimaria(scoMaterial.getCodigo());
			}
			return scoMaterials;
		}
	}

	public List<MaterialOpmeVO> obterMateriaisOrteseseProtesesAgendaComValoreMarca(BigDecimal paramVlNumerico, String objPesquisa, String ordenacao, Date dtAgenda) {
		List<MaterialOpmeVO> materiais = null;
		
		StringBuilder SQL = new StringBuilder(9000);
		
		SQL.append(" SELECT mat.CODIGO 			   matCodigo , \n");
		SQL.append("  mat.NOME 					   matNome , \n");
		SQL.append("  mat.UMD_CODIGO 			   matUmd , \n");
		SQL.append("  iph.COD_TABELA               iphCodSus, \n");
		SQL.append("  mcm.CODIGO                   matMarcaCod, \n");
		SQL.append("  mcm.DESCRICAO                matMarca , \n");
		SQL.append("  iaf.VALOR_UNITARIO           iafValorUnit , \n");
		SQL.append("  NVL(vlr.VLR_SERV_HOSPITALAR,0) + NVL(vlr.VLR_SERV_PROFISSIONAL,0) + NVL(vlr.VLR_SADT,0) + NVL(vlr.VLR_PROCEDIMENTO,0) + NVL(vlr.VLR_ANESTESISTA,0) iphValorUnit \n");
		SQL.append(" FROM SCO_MATERIAIS mat \n");
		SQL.append(" JOIN agh.FAT_PROCED_HOSP_INTERNOS phi \n");
		SQL.append(" ON phi.MAT_CODIGO    = mat.CODIGO \n");
		SQL.append(" AND phi.IND_SITUACAO = 'A' \n");
		SQL.append(" JOIN agh.FAT_CONV_GRUPO_ITENS_PROCED cgi \n");
		SQL.append(" ON cgi.PHI_SEQ = phi.SEQ \n");
		SQL.append(" JOIN agh.FAT_ITENS_PROCED_HOSPITALAR iph \n");
		SQL.append(" ON iph.PHO_SEQ = cgi.IPH_PHO_SEQ \n");
		SQL.append(" AND iph.SEQ    = cgi.IPH_SEQ \n");
		SQL.append(" JOIN agh.FAT_VLR_ITEM_PROCED_HOSP_COMPS vlr \n");
		SQL.append(" ON vlr.IPH_PHO_SEQ = iph.PHO_SEQ \n");
		SQL.append(" AND vlr.IPH_SEQ    = iph.SEQ \n");
		SQL.append(" JOIN agh.SCO_SOLICITACOES_DE_COMPRAS slc \n");
		SQL.append(" ON mat.CODIGO = slc.MAT_CODIGO \n");
		SQL.append(" JOIN agh.SCO_FASES_SOLICITACOES fsc \n");
		SQL.append(" ON slc.NUMERO = fsc.SLC_NUMERO \n");
		SQL.append(" JOIN agh.SCO_ITENS_AUTORIZACAO_FORN iaf \n");
		SQL.append(" ON fsc.IAF_AFN_NUMERO = iaf.AFN_NUMERO \n");
		SQL.append(" AND fsc.IAF_NUMERO    = iaf.NUMERO \n");
		SQL.append(" JOIN agh.SCO_AUTORIZACOES_FORN afn \n");
		SQL.append(" ON iaf.AFN_NUMERO = afn.NUMERO \n");
		SQL.append(" JOIN agh.SCO_MARCAS_COMERCIAIS mcm \n");
		SQL.append(" ON iaf.MCM_CODIGO      = mcm.CODIGO \n");
		SQL.append(" WHERE mat.IND_SITUACAO = 'A' \n");
		SQL.append(" AND phi.IND_SITUACAO   = 'A' \n");
		SQL.append(" AND iph.IND_SITUACAO   = 'A' \n");
		
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			SQL.append(" AND mat.CODIGO = "+objPesquisa+" ");
		}else if (objPesquisa != null) {
			SQL.append(" AND UPPER(mat.NOME) like UPPER('%"+objPesquisa+"%') ");
		}
		
		SQL.append(" AND mat.GMT_CODIGO     = \n");
		SQL.append("  (SELECT vlr_numerico FROM agh_parametros WHERE nome = 'GRPO_MAT_ORT_PROT' \n");
		SQL.append("  ) \n");
		SQL.append(" AND iaf.IND_SITUACAO IN ('AE', 'PA') \n");
		SQL.append(" AND afn.IND_SITUACAO IN ('AE', 'PA') \n");
		SQL.append(" AND fsc.IND_EXCLUSAO  = 'N' \n");
		SQL.append(" AND  nvl(iaf.IND_CONTRATO,'N') = 'S' \n");
		SQL.append(" AND NVL(afn.DT_VENCTO_CONTRATO, \n");
		SQL.append("  '"+DateUtil.dataToString(dtAgenda,"dd-MM-YYYY")+"' ) >= \n");
		SQL.append("  '"+DateUtil.dataToString(dtAgenda,"dd-MM-YYYY")+"' \n");
		SQL.append(" AND vlr.DT_INICIO_COMPETENCIA <= SYSDATE \n");
		SQL.append(" AND vlr.DT_FIM_COMPETENCIA    IS NULL \n");
		SQL.append(" AND iph.PHO_SEQ                = \n");
		SQL.append("  (SELECT vlr_numerico \n");
		SQL.append("  FROM agh.agh_parametros \n");
		SQL.append("  WHERE nome = 'P_TABELA_FATUR_PADRAO' \n");
		SQL.append("  ) \n");
		SQL.append(" AND cgi.CPG_CPH_CSP_SEQ = \n");
		SQL.append("  (SELECT vlr_numerico \n");
		SQL.append("  FROM agh.agh_parametros \n");
		SQL.append("  WHERE nome = 'P_SUS_PLANO_INTERNACAO' \n");
		SQL.append("  ) \n");
		SQL.append(" AND cgi.CPG_CPH_CSP_CNV_CODIGO = \n");
		SQL.append("  (SELECT vlr_numerico \n");
		SQL.append("  FROM agh.agh_parametros \n");
		SQL.append("  WHERE nome = 'P_CONVENIO_SUS_PADRAO' \n");
		SQL.append("  ) \n");
		SQL.append(" AND iph.FOG_SGR_GRP_SEQ = \n");
		SQL.append("  ( SELECT vlr_numerico FROM agh.agh_parametros WHERE nome = 'P_GRUPO_OPM' \n");
		SQL.append("  ) \n");
		SQL.append(" AND rownum < 101  \n");
		SQL.append(" ORDER BY mat.CODIGO, \n");
		SQL.append("  mcm.DESCRICAO");
		
	
		
		
		SQLQuery query = createSQLQuery(SQL.toString());

		//query.setParameter("crgSeq", seq);			

		materiais = query
				.addScalar("matCodigo", IntegerType.INSTANCE)
				.addScalar("matNome", StringType.INSTANCE)
				.addScalar("matUmd", StringType.INSTANCE)
				.addScalar("iphCodSus", LongType.INSTANCE)
				.addScalar("iphValorUnit", BigDecimalType.INSTANCE)
				.addScalar("matMarcaCod", IntegerType.INSTANCE)
				.addScalar("matMarca", StringType.INSTANCE)
				.addScalar("iafValorUnit", DoubleType.INSTANCE)
				
				.setResultTransformer(Transformers.aliasToBean(MaterialOpmeVO.class)).list();

		
		return materiais;
	}

	
	public List<MaterialOpmeVO> obterValorMateriaisProcedimento(Short seq) {
		List<MaterialOpmeVO> materiais = null;
		
		StringBuilder SQL = new StringBuilder(9000);
		SQL.append(" SELECT NVL(vlr.VLR_SERV_HOSPITALAR,0) + NVL(vlr.VLR_SERV_PROFISSIONAL,0) + NVL(vlr.VLR_SADT,0) + NVL(vlr.VLR_PROCEDIMENTO,0) + NVL(vlr.VLR_ANESTESISTA,0) iphValorUnit \n")
		.append(" FROM MBC_REQUISICAO_OPMES rop \n")
		.append(" JOIN MBC_AGENDAS agd \n")
		.append(" ON rop.AGD_SEQ = agd.SEQ \n")
		.append(" JOIN agh.AIP_PACIENTES pac \n")
		.append(" ON agd.PAC_CODIGO = pac.CODIGO \n")
		.append(" JOIN MBC_ESPECIALIDADE_PROC_CIRGS epr \n")
		.append(" ON agd.EPR_PCI_SEQ  = epr.PCI_SEQ \n")
		.append(" AND agd.EPR_ESP_SEQ = epr.ESP_SEQ \n")
		.append(" JOIN MBC_PROCEDIMENTO_CIRURGICOS pci \n")
		.append(" ON epr.PCI_SEQ = pci.SEQ \n")
		.append(" JOIN FAT_ITENS_PROCED_HOSPITALAR iph \n")
		.append(" ON agd.IPH_PHO_SEQ = iph.PHO_SEQ \n")
		.append(" AND agd.IPH_SEQ    = iph.SEQ \n")
		.append(" JOIN FAT_VLR_ITEM_PROCED_HOSP_COMPS vlr \n")
		.append(" ON iph.PHO_SEQ                   = vlr.IPH_PHO_SEQ \n")
		.append(" AND iph.SEQ                      = vlr.IPH_SEQ \n")
		.append(" WHERE vlr.DT_INICIO_COMPETENCIA <= SYSDATE \n")
		.append(" AND vlr.DT_FIM_COMPETENCIA      IS NULL \n")
		.append(" AND rop.SEQ                      = "+seq+" ");
		
		SQLQuery query = createSQLQuery(SQL.toString());
	
		materiais = query
				.addScalar("iphValorUnit", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(MaterialOpmeVO.class)).list();

		return materiais;
	}

	public Long obterMateriaisOrteseseProtesesAgendaCount(BigDecimal paramVlNumerico, String objPesquisa) {
		List<ScoMaterial> scoMaterials = null;
		if (CoreUtil.isNumeroInteger(objPesquisa)) {
			DetachedCriteria criteria = getCriteriaMateriaisOrtesesProtesesAgendaCodigo(paramVlNumerico, objPesquisa);
			criteria.addOrder(Order.asc("mat." + ScoMaterial.Fields.NOME.toString()));
			scoMaterials = executeCriteria(criteria);
		} else {
			DetachedCriteria criteria2 = getCriteriaMateriaisOrtesesProtesesAgendaNome(paramVlNumerico, objPesquisa);
			return executeCriteriaCount(criteria2);
		}
		if (scoMaterials == null || scoMaterials.isEmpty()) {
			DetachedCriteria criteria2 = getCriteriaMateriaisOrtesesProtesesAgendaNome(paramVlNumerico, objPesquisa);
			return executeCriteriaCount(criteria2);
		} else {
			return Long.valueOf(scoMaterials.size());
		}
	}

	private DetachedCriteria getCriteriaMateriaisOrtesesProtesesAgendaCodigo(BigDecimal paramVlNumerico, String objPesquisa) {
		DetachedCriteria criteria = getComumCriteriaMateriaisOrtesesProtesesAgenda(paramVlNumerico);
		criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(objPesquisa)));
		return criteria;
	}

	private DetachedCriteria getCriteriaMateriaisOrtesesProtesesAgendaNome(BigDecimal paramVlNumerico, String objPesquisa) {
		DetachedCriteria criteria = getComumCriteriaMateriaisOrtesesProtesesAgenda(paramVlNumerico);
		criteria.add(Restrictions.ilike("mat." + ScoMaterial.Fields.NOME.toString(), objPesquisa, MatchMode.ANYWHERE));

		return criteria;
	}
	
	public ScoMaterial getMaterialPreferencialCUM(Integer codigo) {
		return (ScoMaterial)executeCriteriaUniqueResult(getCriteriaMateriaisOrtesesProtesesAgendaCodigo(null, codigo.toString()));
	}

	
	private DetachedCriteria getComumCriteriaMateriaisOrtesesProtesesAgenda(BigDecimal paramVlNumerico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "iaf");
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "afn");
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "fsc");
		criteria.createAlias("fsc." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "slc");
		criteria.createAlias("slc." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "mat");
		
		criteria.add(Restrictions.in("iaf." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), 
				new Object[]{DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA}));
		criteria.add(Restrictions.in("afn." + ScoAutorizacaoForn.Fields.SITUACAO.toString(),
				new Object[]{DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
		criteria.add(Restrictions.eq("fsc." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		//criteria.add(Restrictions.eq("iaf." + ScoItemAutorizacaoForn.Fields.IND_PREFERENCIAL_CUM.toString(), true));
		//criteria.add(Restrictions.lt("afn." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), new Date()));
				
		if (paramVlNumerico != null) {
			criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(),
					Integer.valueOf(paramVlNumerico.toString())));
		}
		
		criteria.setProjection(Projections.property("slc." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
		
		return criteria;
	}





	private DetachedCriteria pesquisarCriteria(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if (material.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}

		if (material.getNome() != null && StringUtils.isNotBlank(material.getNome())) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), "%" + material.getNome().trim() + "%", MatchMode.ANYWHERE));
		}

		if (material.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), material.getIndSituacao()));
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), Integer.valueOf("2")));

		return criteria;
	}

	private DetachedCriteria pesquisarCriteriaGmtParametros(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if (material.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}

		if (material.getNome() != null && StringUtils.isNotBlank(material.getNome())) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), "%" + material.getNome().trim() + "%", MatchMode.ANYWHERE));
		}

		if (material.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), material.getIndSituacao()));
		}

		if (material.getIndEstocavel() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.IND_ESTOCAVEL.toString(), material.getEstocavel()));
		}

		if(material.getGrupoMaterial() != null && material.getGrupoMaterial().getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), material.getGrupoMaterial().getCodigo()));
		}

		return criteria;
	}
	public List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome) {
		return obterMateriaisRMAutomatica(gmtCodigo, nome, 0);
	}
	
	public List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome, int limit) {
		ScoMaterial material = new ScoMaterial();
		material.setIndSituacao(DominioSituacao.A);
		material.setEstocavel(true);
		material.setGrupoMaterial(new ScoGrupoMaterial());
		material.getGrupoMaterial().setCodigo(gmtCodigo);
		if (StringUtils.isNotBlank(nome)) {
			material.setNome(nome);
		}

		DetachedCriteria criteria = pesquisarCriteriaGmtParametros(material);

		if(limit >0){
			return this.executeCriteria(criteria, 0, limit, ScoMaterial.Fields.DESCRICAO.toString(), true);
		} else {
			return this.executeCriteria(criteria); 
		}
	}

	/**
	 * Metodo que monta uma criteria para pesquisar ScoMateriais filtrando pela
	 * nome ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaScoMateriais(Object objPesquisa, Boolean indEstocavel,  Boolean pesquisarDescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		String strPesquisa = (String) objPesquisa;

		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UMD", JoinType.LEFT_OUTER_JOIN);
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			if (!pesquisarDescricao) {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
						Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}
		if (indEstocavel!=null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.IND_ESTOCAVEL.toString(), indEstocavel));
		}

		return criteria;
	}

	/**
	 * Metodo que monta uma criteria para pesquisar ScoMateriais filtrando pela
	 * nome ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaScoMateriaisAtivos(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		String strPesquisa = (String) objPesquisa;
		
		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "um", JoinType.LEFT_OUTER_JOIN);
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	public List<ScoMaterial> listarScoMateriais(Object objPesquisa, Boolean indEstocavel) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, false);
		criteria.setFetchMode(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	public List<ScoMaterial> listarScoMateriais(final Object objPesquisa, final Boolean indEstocavel, final Boolean pesquisarDescricao) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, pesquisarDescricao);

		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	public List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriaisAtivos(objPesquisa);

		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	public List<ScoMaterial> listarScoMateriaisAtivosOrderByCodigo(Object objPesquisa) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriaisAtivos(objPesquisa);

		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo e passando o campo de ordenação
	 * @param objPesquisa
	 * @param colunaOrdenacao
	 * @return
	 */
	public List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa,String colunaOrdenacao) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriaisAtivos(objPesquisa);

		criteria.addOrder(Order.asc(colunaOrdenacao));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}
	
	
	/**
	 * #44713 - Suggestion para o campo materiais
	 * filtrando pela descricao ou pelo codigo
	 * @param objPesquisa
	 * @param colunaOrdenacao
	 * @return
	 */
	public List<ScoMaterial> listarScoMateriaisSugestion(Object param) {
		DetachedCriteria criteria = listarScoMateriaisSugestionCriteria(param);
		return executeCriteria(criteria, 0, 100, ScoMaterial.Fields.NOME.toString(), true);
	}

	private DetachedCriteria listarScoMateriaisSugestionCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		if (param != null && StringUtils.isNotBlank(param.toString())) {
			if(CoreUtil.isNumeroInteger(param.toString())) {
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(),Integer.valueOf(param.toString())));
				if(!executeCriteriaExists(criteria)){
					criteria = DetachedCriteria.forClass(ScoMaterial.class);
					criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
				}
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	public Long listarScoMateriaisSugestionCount(Object param){
		DetachedCriteria criteria = listarScoMateriaisSugestionCriteria(param);
		return executeCriteriaCount(criteria);
	}
	
	
    public Long listarScoMatriaisAtivosCount(Object objPesquisa) {
        DetachedCriteria criteria = montarCriteriaScoMateriaisAtivos(objPesquisa);
        return executeCriteriaCount(criteria);
    }

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * Ordenado pela descricao do material
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	public List<ScoMaterial> pesquisarMateriais(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa,null, false);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}


	public List<ScoMaterial> listarScoMateriaisAtiva(Object objPesquisa, Boolean pesquisarDescricao) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa,null,  pesquisarDescricao);

		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		criteria.setFetchMode(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), FetchMode.JOIN);
		criteria.createAlias(ScoMaterial.Fields.ALMOXARIFADO.toString(), "almo");
		criteria.createAlias("almo." + SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "ccusto");
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}

	public List<ScoMaterial> listarMaterialAtivo(Object objPesquisa, Boolean indEstocavel, Integer gmtCodigo) {
		return listarMaterialAtivo(objPesquisa, indEstocavel, gmtCodigo, null);
	}

	public Long listarMaterialAtivoCount(String objPesquisa, Boolean indEstocavel) {	
		return listarMaterialCount(objPesquisa, indEstocavel, DominioSituacao.A);
	}	
	
	public List<ScoMaterial> listarMaterialAtivo(Object objPesquisa, Boolean indEstocavel, Integer gmtCodigo, Short almCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "med", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "gmt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		adicionaClausulaIndicadorEstocavel(indEstocavel, criteria);
		adicionaClausulaGrupoMaterial(gmtCodigo, criteria);
		adicionaClausulaCodigoAlmoxarifado(almCodigo, criteria);

		ordernarCodigoENome(criteria);

		String strPesquisa = (String)objPesquisa;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			
			Integer codigoMaterial = Integer.valueOf(strPesquisa);
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));					
			
		} else {
			
			criteria.add(Restrictions.or(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
			
		}
		
		return executeCriteria(criteria, 0, MAX_RESULTS, null, Boolean.FALSE);

	}

	public Long listarMaterialCount(String objPesquisa, Boolean indEstocavel, DominioSituacao situacao) {
	
		String strPesquisa = objPesquisa;
		if (CoreUtil.isNumeroInteger(strPesquisa)){
			DetachedCriteria cri = DetachedCriteria.forClass(ScoMaterial.class);
			Integer integerPesquisa = Integer.valueOf(strPesquisa);
			cri.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), integerPesquisa));
			
			if (situacao != null){
			    cri.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), situacao));
			}
			
			adicionaClausulaIndicadorEstocavel(indEstocavel, cri);
			return this.executeCriteriaCount(cri);
		}

		if (StringUtils.isNotBlank(strPesquisa)) {
			return (long) lucene.executeLuceneCount(ScoMaterial.Fields.NOME.toString(), ScoMaterial.Fields.NOME_FONETICO.toString(), strPesquisa, ScoMaterial.class, ScoMaterial.Fields.NOME_ORDENACAO.toString());
		} else {
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
			
			if (situacao != null){
				criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), situacao));
			}
			
			adicionaClausulaIndicadorEstocavel(indEstocavel, criteria);
			
			return executeCriteriaCount(criteria);
		}
	}
	
	public List<ScoMaterial> listarMaterialAtivoPorGrupoMaterial(Object param, Integer codGrupoMaterial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		
		if(!param.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(param)){
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(param.toString())));
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
			}
		}
		
		if(codGrupoMaterial != null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codGrupoMaterial));
		}
		
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
		
	}

	public Long listarMaterialAtivoPorGrupoMaterialCount(Object param, Integer codGrupoMaterial){

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if(!param.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(param)){
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(param.toString())));
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
			}
		}

		if(codGrupoMaterial != null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codGrupoMaterial));
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);

	}


	private DetachedCriteria obterCriteriaScoMateriaisGrupoAtiva (Object objPesquisa, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa,null, pesquisarDescricao);

		if(grupoMat!=null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString()+".codigo", grupoMat.getCodigo()));
		}
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}
	
	public List<ScoMaterial> listarScoMateriaisGrupoAtiva(Object objPesquisa, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao, Boolean orderNome) {
		List<ScoMaterial> lista = null;
		DetachedCriteria criteria = obterCriteriaScoMateriaisGrupoAtiva (objPesquisa, grupoMat, pesquisarDescricao);
		if (orderNome) {
			criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		} else {
			criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));
		}
		lista = executeCriteria(criteria, 0, 100, null, true);
		return lista;
	}

	public Long contarScoMateriaisGrupoAtiva(Object objPesquisa, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao) {		
		DetachedCriteria criteria = obterCriteriaScoMateriaisGrupoAtiva (objPesquisa, grupoMat, pesquisarDescricao);
		return executeCriteriaCount(criteria);
	}


	/**
	 * Uma das queries utilizadas para verificar se o material eh classificado como imobilizado
	 * @param matCodigo
	 * @return Boolean
	 */
	public Boolean verificarMaterialImobilizado(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRP");
		
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("GRP."+ScoGrupoMaterial.Fields.IND_GERA_MOV_COND_VLR.toString(), Boolean.TRUE));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarScoMateriaisCount(Object objPesquisa , Boolean indEstocavel) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, false);

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarScoMateriaisCount(final Object objPesquisa, final Boolean indEstocavel, final Boolean pesquisarDescricao) {
		DetachedCriteria criteria = montarCriteriaScoMateriais(objPesquisa, indEstocavel, pesquisarDescricao);

		return executeCriteriaCount(criteria);
	}

	public List<RelUltimasComprasPACVO> obterItensRelatorioUltimasComprasMaterial(Integer numLicitacao, List<Short> itens, List<String> itensModalidade, DominioAgrupadorItemFornecedorMarca agrupador) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VScoUltComprasMat.class, "VUC");

		// define os campos que serão retornados
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), RelUltimasComprasPACVO.Fields.NRO_ITEM.toString());
		//p.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
		//p.add(Projections.property("LIC." + ScoLicitacao.Fields.NUMERO.toString()),RelUltimasComprasPACVO.Fields.NRO_LICITACAO.toString());
		p.add(Projections.property("VUC_LIC." + ScoLicitacao.Fields.NUMERO.toString()),RelUltimasComprasPACVO.Fields.NRO_LICITACAO.toString()); 
		p.add(Projections.property("VUC_LIC." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString()),RelUltimasComprasPACVO.Fields.TIPO_MODALIDADE_LICITACAO.toString()); 
		
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()),RelUltimasComprasPACVO.Fields.COD_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()),RelUltimasComprasPACVO.Fields.DESC_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()),RelUltimasComprasPACVO.Fields.DESC_UNIDADE.toString());
		p.add(Projections.property("VUC_MARC." + ScoMarcaComercial.Fields.DESCRICAO.toString()),RelUltimasComprasPACVO.Fields.DESC_MARCA.toString());
		
		//p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.SOLICITACAO_NRO.toString()));
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.NUMERO_SOLICITACAO.toString()),RelUltimasComprasPACVO.Fields.NRO_SOLICITACAO.toString());
		//p.add(Projections.property("VUC_LIC." + ScoLicitacao.Fields.NUMERO.toString()));
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.DT_ABERTURA.toString()),RelUltimasComprasPACVO.Fields.DT_ABERTURA_PROPOSTA.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.NUM_COMPLEMENTO.toString()),RelUltimasComprasPACVO.Fields.NRO_COMPLEMENTO.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.NUM_SEQ.toString()),RelUltimasComprasPACVO.Fields.NRO_SEQ.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.DT_GERACAO.toString()),RelUltimasComprasPACVO.Fields.DT_GERACAO.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.NUM_NF.toString()),RelUltimasComprasPACVO.Fields.NRO_NF.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.FORMA_PGTO.toString()),RelUltimasComprasPACVO.Fields.FORMA_PAGTO.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.QUANTIDADE.toString()),RelUltimasComprasPACVO.Fields.QUANTIDADE.toString());
		p.add(Projections.property("VUC." + VScoUltComprasMat.Fields.VALOR.toString()),RelUltimasComprasPACVO.Fields.VALOR.toString());

		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),RelUltimasComprasPACVO.Fields.DESC_FORNECEDOR.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.FONE.toString()),RelUltimasComprasPACVO.Fields.FONE_FORNECEDOR.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.DDD.toString()),RelUltimasComprasPACVO.Fields.DDD_FORNECEDOR.toString());
		
		p.add(Projections.property("VUC_LIC." + ScoLicitacao.Fields.INCISO_ARTIGO_LICITACAO.toString()),RelUltimasComprasPACVO.Fields.INCISO.toString());
		
		// p.add(Projections.property("FRN." +
		// ScoFornecedor.Fields.FONE.toString() ));

		// TODO View do nosso ambiente não possui campo que faz join com marcas
		// comerciais
		// p.add(Projections.property("MCM." +
		// ScoMarcaComercial.Fields.DESCRICAO.toString() ));

		criteria.setProjection(p);

		criteria.createAlias("VUC." + VScoUltComprasMat.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);
		criteria.createAlias("VUC." + VScoUltComprasMat.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SLC", Criteria.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC", Criteria.INNER_JOIN);				
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL", Criteria.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.LICITACAO.toString(), "LIC", Criteria.INNER_JOIN);		
		criteria.createAlias("LIC." + ScoLicitacao.Fields.MODALIDADE_LICITACAO, "LIC_MOD", Criteria.INNER_JOIN);

		criteria.createAlias("VUC." + VScoUltComprasMat.Fields.LICITACAO.toString(), "VUC_LIC", Criteria.INNER_JOIN);
		criteria.createAlias("VUC." + VScoUltComprasMat.Fields.MARCA_COMERCIAL.toString(), "VUC_MARC", Criteria.INNER_JOIN);
	

		// Licitacao = licitacao da fase
		criteria.add(Restrictions.eq("LIC." + ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		// Licitacao <> licitacao da VUltmasCompras
		criteria.add(Restrictions.not(Restrictions.eq("VUC_LIC." + ScoLicitacao.Fields.NUMERO.toString(),
				numLicitacao)));
		// Nao excluida
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		// Adiciona itens
		if (itens != null) {
			criteria.add(Restrictions.in("ITL." + ScoItemLicitacao.Fields.NUMERO.toString(), itens));
		}

		if (itensModalidade != null) {
			criteria.add(Restrictions.in("LIC_MOD." + ScoModalidadeLicitacao.Fields.CODIGO.toString(), itensModalidade));
		}

		if(DominioAgrupadorItemFornecedorMarca.FORNECEDOR.equals(agrupador)){
			criteria.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));  
		}
		else if(DominioAgrupadorItemFornecedorMarca.MARCA.equals(agrupador)){			
			criteria.addOrder(Order.asc("VUC_MARC." + ScoMarcaComercial.Fields.DESCRICAO.toString()));
		}
		
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		criteria.addOrder(Order.desc("VUC." +  VScoUltComprasMat.Fields.DT_GERACAO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(RelUltimasComprasPACVO.class));

		return this.executeCriteria(criteria);
	}


	public List<ScoMaterial> pesquisarMaterialPorFiltro(Object _input) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		String nome = (String) _input;
		Integer codigo = null;


		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}	

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigo));
		}
		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));

		return executeCriteria(criteria);
	}

	public Long pesquisarMaterialPorFiltroCount(Object _input) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		String nome = (String) _input;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigo));
		}
		if (nome != null) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		return executeCriteriaCount(criteria);
	}

	public List<ScoMaterial> listarScoMateriaisPorGrupoEspecifico(Object objPesquisa, Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc,  Integer pGrFarmIndustrial, Integer pGrMatMedic) {

		DetachedCriteria criteria = getCriteriaListarScoMateriaisPorGrupoEspecifico(objPesquisa, pGrFarmIndustrial, pGrMatMedic);		
		
		//criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(),DominioSituacao.A));		

		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	private DetachedCriteria getCriteriaListarScoMateriaisPorGrupoEspecifico(Object objPesquisa, Integer pGrFarmIndustrial, Integer pGrMatMedic) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class,"t1");
		String strPesquisa = (String) objPesquisa;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					ScoMaterial.Fields.NOME.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(),DominioSituacao.A));

		// Integer codigo1 = new Integer ("2");--pGrMatMedic
				// Integer codigo2 = new Integer ("35");--pGrFarmIndustrial

		// Grupo Material codigo = 2  ou  (Grupo Material codigo = 35 and Material codigo in (subquerie))
		criteria.add(Restrictions.or(

				Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), pGrMatMedic),

				Restrictions.and(
						Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), pGrFarmIndustrial),
						Subqueries.propertyIn(ScoMaterial.Fields.CODIGO.toString(), subCriteriaMateriaisClassificacoes())))

		);
		return criteria;
	}

	private DetachedCriteria subCriteriaMateriaisClassificacoes() {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class,"t2");

		criteria.setProjection(Projections
				.projectionList().add(Projections.property(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString())));

		criteria.add(Restrictions.eqProperty(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), "t1.codigo"));


		// Oracle: ( substr(cn5_Numero,1,4) = gmt_codigo || '01'), onde gmt_codigo = 35 conforme criteria anterior "listarScoMateriaisPorGrupoEspecifico"   		

		BigDecimal inicio = new BigDecimal("350100000000");
		BigDecimal fim = new BigDecimal("350199999999");
		criteria.add(Restrictions.between(ScoMateriaisClassificacoes.Fields.CN5.toString(), inicio.longValue(), fim.longValue()));

		return criteria;
	}

	public Long getFatItensContaHospitalarComMateriaisOrteseseProteses(final Integer cthSeq, final Short seq, final Integer phiSeq, final Integer codGrupo) {
		/*
		 * 
		 * SELECT MAT.* FROM agh.fat_proced_hosp_internos PHI,
		 * agh.fat_itens_conta_hospitalar ICH, agh.SCO_MATERIAIS MAT WHERE
		 * PHI.SEQ = ICH.PHI_SEQ AND PHI.MAT_CODIGO = MAT.CODIGO AND
		 * MAT.gmt_codigo = 13 and ICH.PHI_SEQ = 11920 and ICH.cth_seq = 13608
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemContaHospitalar.class);
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.SEQ.toString(), seq));
		criteria.createAlias(FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI", Criteria.INNER_JOIN);
		criteria.createAlias("PHI." + FatProcedHospInternos.Fields.MATERIAIS.toString(), "MAT", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRP", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("GRP." + ScoGrupoMaterial.Fields.CODIGO, codGrupo));
		return executeCriteriaCount(criteria);
	}

	/**
	 * Obtem a criteria necessária para a consulta da estória #6619 - Manter Materiais
	 * @param material
	 * @return
	 */
	private DetachedCriteria getCriteriaManterMaterial(ScoMaterial material, VScoClasMaterial classificacaoMaterial){

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UMD", JoinType.INNER_JOIN);
		criteria.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(),"GRUPO", JoinType.INNER_JOIN);
		criteria.createAlias(ScoMaterial.Fields.ALMOXARIFADO.toString(),"ALOMOX", JoinType.INNER_JOIN);
		
		

		if (material.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}

		if (material.getNome() != null && StringUtils.isNotBlank(material.getNome())) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), StringUtils.trim(material.getNome()), MatchMode.ANYWHERE));
		}

		if (material.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), material.getIndSituacao()));
		}

		if (material.getGrupoMaterial() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), material.getGrupoMaterial()));
		}

		if (material.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.ALMOXARIFADO.toString(), material.getAlmoxarifado()));
		}

		if (material.getCodCatmat() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.COD_CATMAT.toString(), material.getCodCatmat()));
		}

		if (material.getCodMatAntigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.COD_MAT_ANTIGO.toString(), material.getCodMatAntigo()));
		}
		// #26669
		if (StringUtils.isNotBlank(material.getDescricao())) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), material.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (classificacaoMaterial != null) {
			criteria.createAlias(ScoMaterial.Fields.MATERIAIS_CLASSIFICACOES.toString(), "CLS");
			criteria.add(Restrictions.eq("CLS."+ScoMateriaisClassificacoes.Fields.CN5.toString(), classificacaoMaterial.getId().getNumero()));
		}

		criteria.add(Restrictions.eq("UMD." + ScoUnidadeMedida.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}
	
	public long pesquisarManterMaterialCount(ScoMaterial material, VScoClasMaterial classificacaoMaterial) {
		DetachedCriteria criteria = this.getCriteriaManterMaterial(material,classificacaoMaterial);
		return this.executeCriteriaCount(criteria);
	}

	public List<ScoMaterial> pesquisarManterMaterial(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoMaterial material, VScoClasMaterial classificacaoMaterial) {
		DetachedCriteria criteria = this.getCriteriaManterMaterial(material, classificacaoMaterial);
		criteria.setFetchMode(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoMaterial.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public ScoMaterial obterMaterialPorCodigoSituacao(ScoMaterial material) {

		if (material == null) {
			throw new IllegalArgumentException("Parâmetro material não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		return (ScoMaterial)executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem material por codigo
	 * @param codigoMaterial
	 * @return
	 */
	public ScoMaterial obterMaterialPorId(Integer codigoMaterial) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.setFetchMode(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		return (ScoMaterial)executeCriteriaUniqueResult(criteria);
	}



	/**
	 * Obtem a criteria para pesquisa de material estocavel com almoxarifado central
	 * @param codigoMaterial
	 * @param seqAlmoxarifado
	 * @param indEstocavel
	 * @return
	 */
	private DetachedCriteria obterCriteriaMaterialEstocavelPorAlmoxarifadoCentral(Integer codigoMaterial, Short seqAlmoxarifado, Boolean indEstocavel) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		if (seqAlmoxarifado == null) {
			throw new IllegalArgumentException("Parâmetro seqAlmoxarifado não foi informado.");
		}

		if (indEstocavel == null) {
			throw new IllegalArgumentException("Parâmetro indEstocavel não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class,"MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ALMOXARIFADO.toString(), "ALM", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifado));
		criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.IND_CENTRAL.toString(), Boolean.TRUE));

		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));

		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), indEstocavel));

		return criteria;
	}

	/**
	 * Verifica a existência de material estocavel com almoxarifado central
	 * @param codigoMaterial
	 * @return
	 */
	public Boolean existeMaterialEstocavelPorAlmoxarifadoCentral(Integer codigoMaterial, Short seqAlmoxarifado, Boolean indEstocavel) {
		return executeCriteriaCount(this.obterCriteriaMaterialEstocavelPorAlmoxarifadoCentral(codigoMaterial, seqAlmoxarifado, indEstocavel)) > 0;
	}

	public Long listarScoMateriaisPorGrupoEspecificoCount(Object objPesquisa,  Integer pGrFarmIndustrial, Integer pGrMatMedic) {
		DetachedCriteria criteria = getCriteriaListarScoMateriaisPorGrupoEspecifico(objPesquisa, pGrFarmIndustrial, pGrMatMedic);

		return executeCriteriaCount(criteria);
	}


	/**
	 * Obtém lista de materiais para catálogo
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	public List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Integer codigoMaterial,
			String nomeMaterial, DominioSituacao situacaoMaterial,
			DominioSimNao estocavel, DominioSimNao generico,
			String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ,
			Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {

		DetachedCriteria criteria = obterCriteriaPesquisarListaMateriaisParaCatalogo(codigoMaterial, nomeMaterial, situacaoMaterial,	
				estocavel, generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat, codMatAntigo, classificacaoMaterial);
		
		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * Retorna a quantidade de materiais da lista para catálogo
	 * 
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	public Long pesquisarListaMateriaisParaCatalogoCount(Integer codigoMaterial,
			String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel, 
			DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ,
			Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {

		DetachedCriteria criteria = obterCriteriaPesquisarListaMateriaisParaCatalogo(codigoMaterial, nomeMaterial, situacaoMaterial,	
				estocavel, generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat, codMatAntigo, classificacaoMaterial);

		return this.executeCriteriaCount(criteria);

	}

	/**
	 * Obtém a critéria da pesquisa de lista de materiais
	 * para catálogo
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria obterCriteriaPesquisarListaMateriaisParaCatalogo(Integer codigoMaterial,
			String nomeMaterial, DominioSituacao situacaoMaterial,	DominioSimNao estocavel, 
			DominioSimNao generico,	String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ,
			Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");

		criteria.createAlias("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UMD", JoinType.INNER_JOIN);
		criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRM", JoinType.INNER_JOIN);
		
		if (classificacaoMaterial != null) {
			criteria.createAlias("MAT."+ScoMaterial.Fields.MATERIAIS_CLASSIFICACOES.toString(), "CLS");
			criteria.add(Restrictions.eq("CLS."+ScoMateriaisClassificacoes.Fields.CN5.toString(), classificacaoMaterial.getId().getNumero()));
		}

//		ProjectionList pList = Projections.projectionList();
//
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ScoMaterial.Fields.CODIGO.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ScoMaterial.Fields.NOME.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString()), ScoMaterial.Fields.UNIDADE_MEDIDA.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString()), ScoMaterial.Fields.GRUPO_MATERIAL.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CLASSIF_XYZ.toString()), ScoMaterial.Fields.CLASSIF_XYZ.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.SITUACAO.toString()), ScoMaterial.Fields.SITUACAO.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString()), ScoMaterial.Fields.IND_ESTOCAVEL.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.GENERICO.toString()), ScoMaterial.Fields.GENERICO.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ScoMaterial.Fields.DESCRICAO.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.COD_CATMAT.toString()), ScoMaterial.Fields.COD_CATMAT.toString());
//		pList.add(Projections.property("MAT." + ScoMaterial.Fields.COD_MAT_ANTIGO.toString()), ScoMaterial.Fields.COD_MAT_ANTIGO.toString());
//
//		criteria.setProjection(pList);

		if (codigoMaterial != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		}
		if (nomeMaterial != null && !"".equals(nomeMaterial)) {
			criteria.add(Restrictions.like("MAT." + ScoMaterial.Fields.NOME.toString(), nomeMaterial, MatchMode.ANYWHERE).ignoreCase());
		}

		if (situacaoMaterial != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), situacaoMaterial));
		}

		if (estocavel != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), estocavel.isSim()));
		}

		if (generico != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GENERICO.toString(), generico));
		}

		if (codigoUnidadeMedida != null && !"".equals(codigoUnidadeMedida)) {
			criteria.add(Restrictions.like("UMD." + ScoUnidadeMedida.Fields.CODIGO.toString(), codigoUnidadeMedida,
					MatchMode.EXACT).ignoreCase());
		}
		if (classifyXYZ != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CLASSIF_XYZ.toString(), classifyXYZ));
		}

		if (codigoGrupoMaterial != null) {
			criteria.add(Restrictions.eq("GRM." + ScoGrupoMaterial.Fields.CODIGO.toString(),
					codigoGrupoMaterial));
		}
		
		if(codCatMat!=null){
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.COD_CATMAT.toString(),codCatMat));
		}
		if(codMatAntigo!=null){
				criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.COD_MAT_ANTIGO.toString(),codMatAntigo));
		}
			

//		criteria.setResultTransformer(Transformers.aliasToBean(ScoMaterial.class));

		return criteria;
	}

	/**
	 * Pesquisa usada em SB
	 * @param material
	 * @return
	 */
	public List<ScoMaterial> pesquisaMateriaisPorParamAlmox(Short almoxSeq, String paramPesq){

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "mat");
		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "unidade_medida", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "grupo_material", JoinType.INNER_JOIN);

		if(paramPesq != null && !"".equals(paramPesq)) {
			if(CoreUtil.isNumeroInteger(paramPesq)) {
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(paramPesq)));
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), paramPesq, MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		if(almoxSeq!=null){
			String sqlSub = "{alias}.codigo in (select 	eal.mat_codigo" +
			"					from    agh.sce_estq_almoxs  eal" +
			"					where  eal.alm_seq  =  ? )";

			Object[] values = { almoxSeq};
			Type[] types = { ShortType.INSTANCE};
			criteria.add(Restrictions.sqlRestriction(sqlSub, values, types));
		}

		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}


	public List<ScoMaterial> pesquisarMaterial(Object _input) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UM");
		String nome = (String) _input;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(nome)){
			codigo = Integer.valueOf(nome);
			nome = null;
		}	

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigo));
		}
		if (nome != null) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, true);

	}

	/**
	 * 
	 * @param matCodigo
	 * @param almSeq
	 * @return
	 */
	public Date recuperarUltimaDataGeracaoMovMaterial(Integer matCodigo, Short almSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class,"MMT");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.max("MMT."+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(), almSeq ));
		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString() + "." + SceTipoMovimento.Fields.SEQ.toString(), Short.valueOf("11")));
		criteria.setProjection(p);
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Este método está utilizando SQL nativo devido a necessidade de fazer um LEFT JOIN com tabelas que não possuem relacionamento.
	 **/
	@SuppressWarnings("unchecked")
	public List<DadosConciliacaoVO> pesquisarDadosConcilicacao(Integer seq){
		StringBuilder sql = new StringBuilder(9000);
			
			sql.append(" SELECT CASE iro.IND_REQUERIDO ")
			.append("             when 'NOV' then ")
			.append("                   case when COALESCE(mat.CODIGO, -1) = -1 ")
			.append("                        then COALESCE(iro.ESPEC_NOVO_MAT, iro.SOLC_NOVO_MAT) ")
			.append("                        else mat.CODIGO||' - '||mat.NOME ")
			.append("                   end ")
			.append("             when 'ADC' then mat.CODIGO||' - '||mat.NOME ")
			.append("             else case when COALESCE(mat.CODIGO, -1) = -1 ")
			.append("                        then iph.COD_TABELA || ' - ' || iph.DESCRICAO ")
			.append("                        else mat.CODIGO||' - '||mat.NOME ")
			.append("                   end ")
			.append("        END codENomeMat ")
			.append("       ,CASE iro.IND_compativel WHEN 'S' THEN 'Sim' ELSE 'Não' END compativel ")
			.append("       ,CASE iro.IND_autorizado WHEN 'S' THEN 'Sim' ELSE 'Não' END autorizado ")
			.append("       ,COALESCE(mio.QTD_SOLC, iro.QTD_SOLC)                       qtdSolicitada ")
			.append("       ,( SELECT COALESCE(SUM(ips.QUANTIDADE), 0) ")
			.append("          FROM   agh.SCE_RMR_PACIENTES rmp ")
			.append("          JOIN   agh.SCE_ITEM_RMPS     ips  ON ips.RMP_SEQ = rmp.SEQ ")
			.append("          JOIN   agh.SCE_ESTQ_ALMOXS   eal  ON eal.SEQ     = ips.EAL_SEQ ")
			.append("          JOIN   agh.SCO_MATERIAIS     mat2 ON mat2.CODIGO = eal.MAT_CODIGO ")
			.append("          WHERE  rmp.CRG_SEQ = crg.SEQ ")
			.append("          AND    mat2.CODIGO = mat.CODIGO ")
			.append("        ) qtdConsumida ")
			.append("       ,iro.VLR_UNIT                                               valorUnitario ")
			.append("       ,iro.VLR_UNIT * coalesce(mio.QTD_SOLC, iro.QTD_SOLC)        valorTotalSolicitado ")
			.append("       ,rop.JUST_CONSUMO_OPME                                      justificativa ")
			.append("       ,CASE iro.IND_REQUERIDO WHEN 'NOV' then 1 WHEN 'ADC' then 2 else 3 END as reqOrder ")
			.append("       ,rop.SEQ                                  SEQ_REQUISICAO ")
			.append("       ,iro.SEQ                                  SEQ_ITEM ")
			.append("       ,mio.SEQ                                  SEQ_MAT ")
			.append(" FROM   agh.MBC_CIRURGIAS                  crg ")
			.append(" JOIN   agh.MBC_AGENDAS                    agd ON agd.SEQ     = crg.AGD_SEQ ")
			.append(" JOIN   agh.MBC_REQUISICAO_OPMES           rop ON rop.AGD_SEQ = agd.SEQ ")
			.append(" JOIN   agh.MBC_ITENS_REQUISICAO_OPMES     iro ON iro.ROP_SEQ = rop.SEQ ")
			.append(" LEFT JOIN agh.FAT_ITENS_PROCED_HOSPITALAR iph ON iph.PHO_SEQ = iro.IPH_PHO_SEQ and iph.SEQ = iro.IPH_SEQ ")
			.append(" LEFT JOIN agh.MBC_MATERIAIS_ITEM_OPMES    mio ON mio.IRO_SEQ = iro.SEQ and mio.QTD_SOLC > 0 ")
			.append(" LEFT JOIN agh.SCO_MATERIAIS               mat ON mat.CODIGO  = mio.MAT_CODIGO ")
			.append(" WHERE  crg.SEQ = :crgSeq ")
			.append(" AND    iro.IND_REQUERIDO  <> 'NRQ' ")
			.append(" AND    iro.IND_autorizado = 'S' ")
			.append(" UNION ")
			.append(" SELECT codENomeMat ")
			.append("       ,compativel ")
			.append("       ,autorizado ")
			.append("       ,qtdSolicitada ")
			.append("       ,SUM(qtdConsumida_0) qtdConsumida ")
			.append("       ,valorUnitario ")
			.append("       ,valorTotalSolicitado ")
			.append("       ,justificativa ")
			.append("       ,reqOrder ")
			.append("       ,SEQ_REQUISICAO ")
			.append("       ,SEQ_ITEM ")
			.append("       ,SEQ_MAT ")
			.append(" FROM ")
			.append(" ( ")
			.append(" SELECT mat.CODIGO ||' - '|| mat.NOME    codENomeMat ")
			.append("       ,COALESCE(null,'Não')             compativel ")
			.append("       ,COALESCE(null,'Não')             autorizado ")
			.append("       ,0                                qtdSolicitada ")
			.append("       ,COALESCE(ips.QUANTIDADE, 0)      qtdConsumida_0 ")
			.append("       ,0                                valorUnitario ")
			.append("       ,0                                valorTotalSolicitado ")
			.append("       ,COALESCE(null,'')                justificativa ")
			.append("       ,4                                reqOrder ")
			.append("       ,0                                SEQ_REQUISICAO ")
			.append("       ,0                                SEQ_ITEM ")
			.append("       ,0                                SEQ_MAT ")
			.append(" FROM   agh.MBC_CIRURGIAS         crg ")
			.append(" JOIN   agh.SCE_RMR_PACIENTES     rmp ON rmp.CRG_SEQ = crg.SEQ ")
			.append(" JOIN   agh.SCE_ITEM_RMPS         ips ON ips.RMP_SEQ = rmp.SEQ ")
			.append(" JOIN   agh.SCE_ESTQ_ALMOXS       eal ON eal.SEQ     = ips.EAL_SEQ ")
			.append(" JOIN   agh.SCO_MATERIAIS         mat ON mat.CODIGO  = eal.MAT_CODIGO ")
			.append(" JOIN   agh.MBC_AGENDAS           agd ON crg.AGD_SEQ = agd.SEQ ")
			.append(" JOIN   agh.MBC_REQUISICAO_OPMES  rop ON rop.AGD_SEQ = agd.SEQ ")
			.append(" WHERE  crg.SEQ = :crgSeq ")
			.append(" AND    mat.GMT_CODIGO = ( SELECT VLR_NUMERICO FROM agh.AGH_PARAMETROS WHERE NOME = 'GRPO_MAT_ORT_PROT' ) ")
			.append(" AND    mat.CODIGO not in ( select mio.MAT_CODIGO ")
			.append("                            from   agh.MBC_ITENS_REQUISICAO_OPMES iro ")
			.append("                            join   agh.MBC_MATERIAIS_ITEM_OPMES   mio ON mio.IRO_SEQ = iro.SEQ and mio.QTD_SOLC > 0 ")
			.append("                            where  iro.ROP_SEQ = rop.SEQ ")
			.append("                          ) ")
			.append(" ) subq ")
			.append(" GROUP BY codENomeMat ")
			.append("       ,compativel ")
			.append("       ,autorizado ")
			.append("       ,qtdSolicitada ")
			.append("       ,valorUnitario ")
			.append("       ,valorTotalSolicitado ")
			.append("       ,justificativa ")
			.append("       ,SEQ_REQUISICAO ")
			.append("       ,SEQ_ITEM ")
			.append("       ,SEQ_MAT ")
			.append("       ,reqOrder ")
			.append(" ORDER BY reqOrder ")
			.append("         ,codENomeMat ");
			
			SQLQuery query = createSQLQuery(sql.toString());

			query.setParameter("crgSeq", seq);			

			List<DadosConciliacaoVO> listaVO = query.addScalar("codENomeMat", StringType.INSTANCE)
					.addScalar("compativel", StringType.INSTANCE)
					.addScalar("autorizado", StringType.INSTANCE)
					.addScalar("qtdSolicitada", IntegerType.INSTANCE)
					.addScalar("qtdConsumida", IntegerType.INSTANCE)
					.addScalar("valorUnitario", BigDecimalType.INSTANCE)
					.addScalar("valorTotalSolicitado", BigDecimalType.INSTANCE)
					.addScalar("justificativa", StringType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(DadosConciliacaoVO.class)).list();

			return listaVO;
	
	}
	
	@SuppressWarnings("unchecked")
	public List<DadosMateriaisConciliacaoVO> pesquisarDadosMaterialConcilicacao(Integer seq){
		StringBuilder sql = new StringBuilder(9000);

		sql.append(" SELECT agd.DT_AGENDA              dataProcedimento ");
		sql.append("       ,pci.SEQ || ' - ' ||  ");
		sql.append("        pci.DESCRICAO              procedimentoPrincipal ");
		sql.append("       ,iph.COD_TABELA || ' - ' || ");
		sql.append("        iph.DESCRICAO              procedimentoSus ");
		sql.append("       ,rop.JUST_REQUISICAO_OPME   justificativaReq ");
		sql.append("       ,rop.OBSERVACAO_OPME        observacoesGerais ");
		sql.append(" FROM   agh.MBC_CIRURGIAS crg  ");
		sql.append(" JOIN agh.MBC_AGENDAS agd ON crg.agd_seq = agd.seq ");
		sql.append(" JOIN   agh.MBC_PROCEDIMENTO_CIRURGICOS pci ON agd.EPR_PCI_SEQ = pci.SEQ ");
		sql.append(" JOIN   agh.FAT_ITENS_PROCED_HOSPITALAR iph ON agd.IPH_PHO_SEQ  = iph.PHO_SEQ AND agd.IPH_SEQ  = iph.SEQ ");
		sql.append(" LEFT JOIN agh.MBC_REQUISICAO_OPMES     rop ON agd.SEQ = rop.AGD_SEQ ");
		sql.append(" WHERE  crg.SEQ = :crgSeq ");

		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("crgSeq", seq);

		List<DadosMateriaisConciliacaoVO> listaVO = query.addScalar("dataProcedimento", DateType.INSTANCE)
				.addScalar("procedimentoPrincipal", StringType.INSTANCE).addScalar("procedimentoSus", StringType.INSTANCE)
				.addScalar("justificativaReq", StringType.INSTANCE).addScalar("observacoesGerais", StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(DadosMateriaisConciliacaoVO.class)).list();

		return listaVO;	
		
	}
	
	/**
	 * Obtem licitações para os campos 18 a 26 do relatório
	 * @return 
	 * @author jhonatan.rodrigues
	 * @since 05/08/2011
	 */	
	public RelatorioDiarioMateriaisComSaldoAteVinteDiasVO obterLicitacoesRelatorioMateriasAteVinteDias(Integer slcNumero){

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		ProjectionList p = Projections.projectionList();
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ILCT", Criteria.INNER_JOIN);
		criteria.createAlias("ILCT." + ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);

		p.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.SOLICITACAO_COMPRA_NUMERO.toString());
		p.add(Projections.property("LCT." + ScoLicitacao.Fields.NUMERO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.NUMERO_LICITACAO.toString());
		p.add(Projections.property("ILCT." + ScoItemLicitacao.Fields.NUMERO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.NUMERO_ITEM_LICITACAO.toString());
		p.add(Projections.property("LCT." + ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.DATA_LICITACAO.toString());
		criteria.setProjection(p);

		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),false));
		criteria.add(Subqueries.notExists(obterLicitacoesRelatorioMateriasAteVinteDiasSubQuery()));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), slcNumero));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.class));

		RelatorioDiarioMateriaisComSaldoAteVinteDiasVO result = null;
		if (executeCriteriaUniqueResult(criteria) != null) {
			result = (RelatorioDiarioMateriaisComSaldoAteVinteDiasVO) executeCriteriaUniqueResult(criteria);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	private DetachedCriteria obterLicitacoesRelatorioMateriasAteVinteDiasSubQuery(){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FSC2");
		criteria.add(Restrictions.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString() , "FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()));
		criteria.add(Restrictions.isNotNull("FSC2." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
		criteria.setProjection(Projections.id());
		return criteria;
	}

	/**
	 * Obtem licitações para os campos 18 a 26 do relatório (Union All)
	 * @return 
	 * @author jhonatan.rodrigues
	 * @since 05/08/2011
	 */
	public RelatorioDiarioMateriaisComSaldoAteVinteDiasVO pesquisarLicitacoesRelatorioMateriasAteVinteDiasUnionAll(Integer slcNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(),"IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(),"AFN", Criteria.LEFT_JOIN);		
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(),"IPF", Criteria.LEFT_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(),"PRO", Criteria.INNER_JOIN);
		criteria.createAlias("PRO." + ScoPropostaFornecedor.Fields.LICITACAO.toString(),"LCT", Criteria.INNER_JOIN);
		criteria.createAlias("PRO." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(),"FRN", Criteria.INNER_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.SOLICITACAO_COMPRA_NUMERO.toString());		
		p.add(Projections.property("PRO." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.NUMERO_LICITACAO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.NUMERO_ITEM_AUTORIZACAO.toString());
		p.add(Projections.property("LCT." + ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.DATA_LICITACAO.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.AFN_NUMERO_COMPLEMENTO.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.IAF_QUANTIDADE_SOLICITADA.toString());
		p.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.QTDE_RECEBIDA.toString()); 
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.DATA_PREVISAO_ENTREGA.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.DATA_ALTERACAO.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.IND_SITUACAO_AUTORIZACAO_FORN.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.RAZAO_SOCIAL.toString());
		criteria.setProjection(p);

		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), slcNumero));

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.class));
		return (RelatorioDiarioMateriaisComSaldoAteVinteDiasVO) executeCriteriaUniqueResult(criteria);
	}	

	/**
	 * 
	 * @param dataCompetencia
	 * @return
	 */
	public List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(Date dataCompetencia, Integer limiteDiasDuracaoEstoque, AghParametros paramClassificacaoComprasWeb) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.MAT_GMT_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.MAT_NOME.toString());
		p.add(Projections.property("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EGR_CLASSIF_ABC.toString());
		p.add(Projections.property("EGR." + SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EGR_SUB_CLASSIF_ABC.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EAL_ALM_SEQ.toString());		
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_PONTO_PEDIDO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EAL_QTDE_PONTO_PEDIDO.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EAL_QTDE_DISPONIVEL.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EAL_TEMPO_REPOSICAO.toString());
		p.add(Projections.property("EGR." + SceEstoqueGeral.Fields.QUANTIDADE.toString()), RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.Fields.EGR_QTDE.toString());

		criteria.setProjection(p);

		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.FORNECEDOR.toString()+ "." + ScoFornecedor.Fields.NUMERO.toString(), 1));
		criteria.add(Restrictions.isNull("MAT." + ScoMaterial.Fields.DATA_DESATIVACAO.toString()));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull("MAT." + ScoMaterial.Fields.PRODUCAO_INTERNA.toString()));		
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString()+"."+ ScoFornecedor.Fields.NUMERO.toString(), 1));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ, "MAT." + ScoMaterial.Fields.ALMOXARIFADO_SEQ));
		criteria.add(Subqueries.geAll(limiteDiasDuracaoEstoque,montarCriteriaSubQueryRestringirPrazoReposicaoRelatorioMateriaisAteVinteDiasInicio()));
		criteria.add(Restrictions.gt("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_PONTO_PEDIDO.toString(), 0));		

		// TODO
		// Codigo abaixo comentado conforme solicitação da Estoria do usuário:
		// #17137 - ajustar relatório de materiais com saldo até 20 dias.				
		//criteria.add(Subqueries.propertyNotIn("MAT." + ScoMaterial.Fields.CODIGO.toString(), montarCriteriaSubQuerySolicitacoesRelatorioMateriaisAteVinteDiasInicio()));
		//criteria.add(Subqueries.propertyNotIn("MAT." + ScoMaterial.Fields.CODIGO.toString(), montarCriteriaSubQuerySolicitacoesIAFRelatorioMateriaisAteVinteDiasInicio()));

		criteria.createAlias("EAL."	+ SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR", Criteria.INNER_JOIN);

		if (paramClassificacaoComprasWeb != null) {
			DetachedCriteria subQueryClass = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "MC");
			subQueryClass.setProjection(Projections.property("MC."+ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()));
			subQueryClass.add(Restrictions.eqProperty(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString(), "EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString()));
			
			DetachedCriteria subQueryClassN1 = DetachedCriteria.forClass(ScoClassifMatNiv1.class, "CMN1");
			subQueryClassN1.setProjection(Projections.property("CMN1."+ScoClassifMatNiv1.Fields.GMT_CODIGO.toString()));
			subQueryClassN1.add(Restrictions.eq("CMN1."+ScoClassifMatNiv1.Fields.DESCRICAO.toString(), paramClassificacaoComprasWeb.getVlrTexto()));
			if (isOracle()) {
				subQueryClassN1.add(Restrictions.sqlRestriction("TO_NUMBER(SUBSTR(LPAD(TO_CHAR(MC_.CN5_NUMERO),12,'0'),1,2)) = {alias}.GMT_CODIGO"));
				subQueryClassN1.add(Restrictions.sqlRestriction("TO_NUMBER(SUBSTR(LPAD(TO_CHAR(MC_.CN5_NUMERO),12,'0'),3,2)) = {alias}.CODIGO"));
			} else {
				subQueryClassN1.add(Restrictions.sqlRestriction("CAST(SUBSTR(LPAD(CAST(MC_.CN5_NUMERO AS VARCHAR),12,'0'),1,2) AS SMALLINT) = {alias}.GMT_CODIGO"));
				subQueryClassN1.add(Restrictions.sqlRestriction("CAST(SUBSTR(LPAD(CAST(MC_.CN5_NUMERO AS VARCHAR),12,'0'),3,2) AS INTEGER) = {alias}.CODIGO"));
			}
			
			subQueryClass.add(Subqueries.exists(subQueryClassN1));
			
			criteria.add(Subqueries.notExists(subQueryClass));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioDiarioMateriaisComSaldoAteVinteDiasVO.class));		
		return this.executeCriteria(criteria);
	}

	/**
	 * Monta a criteria SubSelect que restringe os materiais com reposição até 20 dias  - Relatório Materiais Até Vinte Dias    
	 * 
	 * Subquery do Inicio
	 * 
	 * @author rodrigo.figueiredo
	 * @since 15/09/2011
	 */	
	private DetachedCriteria montarCriteriaSubQueryRestringirPrazoReposicaoRelatorioMateriaisAteVinteDiasInicio() {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class,"EAL_S");
		ProjectionList p = Projections.projectionList();

		StringBuilder sql = new StringBuilder(200);
		sql.append("((SUM({alias}.QTDE_DISPONIVEL + {alias}.QTDE_BLOQUEADA) - " ).append(
		" (this_.qtde_ponto_pedido /10) ) * this_.tempo_reposicao / this_.qtde_ponto_pedido) qtde_disp_bloq");

		p.add(Projections.sqlProjection(sql.toString(),  new String[]{"qtde_disp_bloq"}, new Type[]{IntegerType.INSTANCE}));
		criteria.setProjection(p);	

		criteria.add(Restrictions.eqProperty("EAL_S." + SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), "MAT." + ScoMaterial.Fields.CODIGO.toString() ));
		criteria.add(Restrictions.eqProperty("EAL_S." +SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(), "EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("EAL_S." +SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return criteria ;
	}

	/**
	 * Obtem a quantidade bloqueada no estoque
	 * @param matCodigo
	 * @param EalSeq
	 * @author eduardo.franco
	 * @return
	 */
	public Long obterQuantidadeBloqueada(Integer matCodigo, Short EalSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QUANTIDADE_BLOQUEADA.toString()));
		criteria.setProjection(p);	
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(), EalSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * Obtem a quantidade total no estoque
	 * @param matCodigo
	 * @param EalSeq
	 * @author eduardo.franco
	 * @return
	 */
	public Long obterQuantidadeTotal(Integer matCodigo, Short EalSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		ProjectionList p = Projections.projectionList();

		StringBuilder sql = new StringBuilder(100);
		sql.append("SUM(QTDE_DISPONIVEL + QTDE_BLOQUEADA) qtde_disp_bloq");
		p.add(Projections.sqlProjection(sql.toString(),  new String[]{"qtde_disp_bloq"}, new Type[]{IntegerType.INSTANCE}));

		criteria.setProjection(p);	
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo ));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(), EalSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return (Long) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem a quantidade disponivel no estoque 
	 * @param matCodigo
	 * @param EalSeq
	 * @author eduardo.franco
	 * @return
	 */
	public Long obterQuantidadeDisponivel(Integer matCodigo, Short EalSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()));

		criteria.setProjection(p);	
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo ));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(), EalSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return (Long) executeCriteriaUniqueResult(criteria);
	}


	/**
	 * 
	 * @param gmtCodigo
	 * @param indEstoc
	 * @return
	 */
	public Long pesquisaScoMateriaisRelMensal(Integer gmtCodigo, DominioSimNao indEstoc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		boolean estocavel = indEstoc != null && indEstoc.isSim();
		criteria.add(Restrictions.eq(ScoMaterial.Fields.IND_ESTOCAVEL.toString(), estocavel));
		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa um material ativo por código ou nome em um conjunto de grupo de materiais
	 * @param objPesquisa
	 * @param listaGmtCodigo
	 * @return
	 */
	public List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa, List<Integer> listaGmtCodigo) {

		DetachedCriteria criteria = montaCriteriaMateriaisAtivosGrupoMaterial(
				objPesquisa, listaGmtCodigo);

		criteria.addOrder(Order.asc(ScoMaterial.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 100, null, true);

	}

	private DetachedCriteria montaCriteriaMateriaisAtivosGrupoMaterial(
			Object objPesquisa, List<Integer> listaGmtCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.createAlias(ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UN");
		
		String strPesquisa = (String) objPesquisa;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.in(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), listaGmtCodigo));
		return criteria;
	}

	public FsoNaturezaDespesa obterNaturezaDespesa(
			FsoGrupoNaturezaDespesa grupoNatureza, ScoMaterial material) {
		final String NTD = "ntd", GND = "gnd";
		DetachedCriteria criteria = DetachedCriteria.forClass(
				FsoNaturezaDespesa.class, NTD);

		criteria.createAlias(NTD + "."
				+ FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), GND);

		criteria.add(Restrictions.eq(NTD + "."
				+ FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(),
				grupoNatureza));

		final String M = "m", GM = "gm";
		criteria.add(Subqueries.exists(DetachedCriteria
				.forClass(ScoMaterial.class, M)
				.setProjection(Projections.id())
				.createAlias(
						M + "." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
						GM)
				.add(Restrictions.eq(
						M + "." + ScoMaterial.Fields.CODIGO.toString(),
						material.getCodigo()))
				.add(Restrictions.eqProperty(GM + "."
						+ ScoGrupoMaterial.Fields.NTD_CODIGO.toString(), NTD
						+ "." + FsoNaturezaDespesa.Fields.CODIGO.toString()))));

		criteria.add(Restrictions.eq(NTD + "."
				+ FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		
		criteria.add(Restrictions.eq(GND + "."
				+ FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return (FsoNaturezaDespesa) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoMaterial obterMaterialPorCodigoDescricaoUnidadeMedida(Integer matCodigo, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UNI");
		String strPesquisa = (String) param;
		if (matCodigo != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion criterionCodigo = Restrictions.ilike("UNI." + ScoUnidadeMedida.Fields.CODIGO.toString(), 
					StringUtils.trim(strPesquisa), MatchMode.EXACT);
			Criterion criterionDescricao = Restrictions.ilike("UNI." + ScoUnidadeMedida.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		return (ScoMaterial) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria obterCriteriaMovimento(Boolean count, Boolean sum, Date dtInicio, Date dtFim, Integer seqTmv, Integer matCodigo, Boolean isValor) {
		DetachedCriteria subQuery = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		if (count) {
			subQuery.setProjection(Projections.count("MMT."+SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()));
		}
		
		if (sum) {
			if (isValor) {
				subQuery.setProjection(Projections.sum("MMT."+SceMovimentoMaterial.Fields.VALOR.toString()));
			} else {
				subQuery.setProjection(Projections.sum("MMT."+SceMovimentoMaterial.Fields.QUANTIDADE.toString()));
			}
		}
		
		if (!count && !sum) {
			subQuery.setProjection(Projections.property("MMT."+SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()));
		}
		
		if (dtInicio != null) {
			subQuery.add(Restrictions.ge("MMT."+SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), DateUtil.truncaData(dtInicio)));
		}
		
		if (dtFim != null) {
			subQuery.add(Restrictions.le("MMT."+SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), DateUtil.truncaDataFim(dtFim)));
		}
		
		if (matCodigo == null) {
			subQuery.add(Restrictions.eqProperty("MMT."+SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
		} else {
			subQuery.add(Restrictions.eq("MMT."+SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		}
		subQuery.add(Restrictions.eq("MMT."+SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq("MMT."+SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), seqTmv.shortValue()));
		
		return subQuery;
	}
	
	private void criarFiltroEmLicitacao(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndEmPac() != null) {		
			if (filtro.getIndEmPac().isSim()) {			
				// pac nao esta em af
				DetachedCriteria subQuerySemAf = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL2");
				subQuerySemAf.setProjection(Projections.property("FSL2."+ScoFaseSolicitacao.Fields.NUMERO.toString()));
				subQuerySemAf.createAlias("FSL2."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC2");
				subQuerySemAf.add(Restrictions.eq("FSL2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuerySemAf.add(Restrictions.eq("SLC2."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuerySemAf.add(Restrictions.isNull("FSL2."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
				subQuerySemAf.add(Restrictions.eqProperty("SLC2."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
				subQuerySemAf.add(Restrictions.eq("SLC2."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				
				if (filtro.getIndComLicitacao() != null) {
					subQuerySemAf.createAlias("FSL2."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL2");
					subQuerySemAf.createAlias("ITL2."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT2");
					subQuerySemAf.createAlias("LCT2."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC2");
					subQuerySemAf.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.IND_LICITACAO.toString(), filtro.getIndComLicitacao().isSim()));
					subQuerySemAf.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));				
				}

				// existe PAC em AF com situacao AE ou PA
				DetachedCriteria subQueryAf = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF3");
				subQueryAf.setProjection(Projections.property("IAF3."+ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
				subQueryAf.createAlias("IAF3."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL3");
				subQueryAf.createAlias("FSL3."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC3");
				subQueryAf.add(Restrictions.in("IAF3." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
				subQueryAf.add(Restrictions.eq("FSL3."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQueryAf.add(Restrictions.eq("SLC3."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQueryAf.add(Restrictions.eqProperty("SLC3."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
				subQueryAf.add(Restrictions.isNotNull("FSL3."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
				
				if (filtro.getDataVigencia() != null) {
					subQuerySemAf.add(Subqueries.propertyNotIn("SLC2."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), obterCriteriaNumeroScContratoItemAf()));
				}

				if (filtro.getIndComLicitacao() != null) {
					subQueryAf.createAlias("SLC3."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSL3A");
					subQueryAf.createAlias("FSL3A."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL2");
					subQueryAf.createAlias("ITL2."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT2");
					subQueryAf.createAlias("LCT2."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC2");
					subQueryAf.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.IND_LICITACAO.toString(), filtro.getIndComLicitacao().isSim()));
					subQueryAf.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
					subQueryAf.add(Restrictions.eq("FSL3A."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				}
				
				if (filtro.getDataVigencia() != null) {
					subQueryAf.add(Subqueries.propertyNotIn("SLC3."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), obterCriteriaNumeroScContratoItemAf()));
				}

				criteria.add(Restrictions.or(Subqueries.exists(subQuerySemAf), Subqueries.exists(subQueryAf)));
			} else {
				DetachedCriteria subQuery = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC4");
				subQuery.setProjection(Projections.property("SLC4."+ScoFaseSolicitacao.Fields.NUMERO.toString()));
				subQuery.add(Restrictions.eq("SLC4."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuery.add(Restrictions.eq("SLC4."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
				subQuery.add(Restrictions.eqProperty("SLC4."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));

				DetachedCriteria subQueryFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL4");
				subQueryFases.setProjection(Projections.property("FSL4."+ScoFaseSolicitacao.Fields.NUMERO.toString()));
				subQueryFases.add(Restrictions.eq("FSL4."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQueryFases.add(Restrictions.eqProperty("FSL4."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "SLC4."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));

				if (filtro.getDataVigencia() != null) {
					subQuery.add(Subqueries.propertyNotIn("SLC4."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), obterCriteriaNumeroScContratoItemAf()));
				}
				
				subQuery.add(Subqueries.exists(subQueryFases));
				
				criteria.add(Subqueries.notExists(subQuery));
			}			
		}
	}

	private void criarFiltroEmAf(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndEmAf() != null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQuery.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL");
			subQuery.createAlias("FSL."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subQuery.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
			subQuery.add(Restrictions.eq("FSL."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.isNotNull("FSL."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));

			if (filtro.getIndItemContrato() != null) {
				subQuery.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtro.getIndItemContrato().isSim()));
			}
			
			if (filtro.getDataVigencia() != null) {
				subQuery.add(Subqueries.propertyNotIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), obterCriteriaNumeroScContratoItemAf()));
			}

			
			if (filtro.getIndComLicitacao() != null) {
				subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSL3A");
				subQuery.createAlias("FSL3A."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL2");
				subQuery.createAlias("ITL2."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT2");
				subQuery.createAlias("LCT2."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC2");
				subQuery.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.IND_LICITACAO.toString(), filtro.getIndComLicitacao().isSim()));
				subQuery.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
				subQuery.add(Restrictions.eq("FSL3A."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			}

			if (filtro.getIndAfContrato() != null) {
				subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
				if (filtro.getIndAfContrato().isSim()) {
					subQuery.add(Restrictions.in("AFN."+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), new DominioModalidadeEmpenho[] {DominioModalidadeEmpenho.CONTRATO, DominioModalidadeEmpenho.ESTIMATIVA}));
				} else {
					subQuery.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.ORDINARIO));
				}
			}
			
			if (filtro.getIndEmAf().isSim()) {
				criteria.add(Subqueries.exists(subQuery));
			} else {
				criteria.add(Subqueries.notExists(subQuery));
			}
		}
	}
	
	private void criarFiltroCcAplicacao(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getCentroCustoAplicacao() != null) {
			DetachedCriteria subQuery = this.obterCriteriaMovimento(false, false, filtro.getDataInicio(), filtro.getDataFim(), 5, null, true);
			subQuery.add(Restrictions.eq(SceMovimentoMaterial.Fields.CENTRO_CUSTO.toString(), filtro.getCentroCustoAplicacao()));
			criteria.add(Subqueries.exists(subQuery));
		}
	}
	
	private void criarFiltroClassificacaoABC(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getSemClassificacaoAbc() != null && filtro.getSemClassificacaoAbc()) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");
			subQuery.setProjection(Projections.property("EGR."+SceEstoqueGeral.Fields.MAT_CODIGO.toString()));
			subQuery.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), filtro.getCompetencia().getVlrData()));
			subQuery.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.FRN_NUMERO.toString(), filtro.getFornecedorPadrao().getVlrNumerico().intValue()));
			subQuery.add(Restrictions.isNull("EGR."+SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()));
			
			criteria.add(Subqueries.propertyIn("MAT."+ScoMaterial.Fields.CODIGO.toString(), subQuery));
		} else {
			if (filtro.getListaClassAbc() != null && !filtro.getListaClassAbc().isEmpty()) {
				DetachedCriteria subQuery = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");
				subQuery.setProjection(Projections.property("EGR."+SceEstoqueGeral.Fields.MAT_CODIGO.toString()));
				subQuery.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), filtro.getCompetencia().getVlrData()));
				subQuery.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.FRN_NUMERO.toString(), filtro.getFornecedorPadrao().getVlrNumerico().intValue()));
				subQuery.add(Restrictions.in("EGR."+SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString(), filtro.getListaClassAbc()));
				
				criteria.add(Subqueries.propertyIn("MAT."+ScoMaterial.Fields.CODIGO.toString(), subQuery));
			}
		}
	}
	
	private void criarFiltroNivelClassificacao(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getClassificacaoMaterial() != null) {
			
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "MCL");
			subQuery.setProjection(Projections.property("MCL."+ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()));
			subQuery.createAlias("MCL."+ScoMateriaisClassificacoes.Fields.MATERIAL.toString(), "MT5");
			subQuery.createAlias("MT5."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GM5");
			
			subQuery.add(Restrictions.eqProperty("MT5."+ScoMaterial.Fields.CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eq("MCL."+ScoMateriaisClassificacoes.Fields.CN5.toString(), filtro.getClassificacaoMaterial().getId().getNumero()));
			if (isOracle()) {
				subQuery.add(Restrictions.sqlRestriction("GM5X2_.CODIGO  = CASE WHEN MT5X1_.GMT_CODIGO < 9 THEN SUBSTR({alias}.CN5_NUMERO,1,1) ELSE SUBSTR({alias}.CN5_NUMERO,1,2) END "));
			} else {
				subQuery.add(Restrictions.sqlRestriction("CAST(GM5X2_.CODIGO AS VARCHAR)  = CASE WHEN MT5X1_.GMT_CODIGO < 9 THEN SUBSTR(CAST({alias}.CN5_NUMERO AS VARCHAR),1,1) ELSE SUBSTR(CAST({alias}.CN5_NUMERO AS VARCHAR),1,2) END "));
			}
			
			criteria.add(Subqueries.propertyIn("MAT."+ScoMaterial.Fields.CODIGO.toString(), subQuery));
		}
	}
	
	private void criarFiltroGrupoMaterial(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getGrupoMaterial() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), filtro.getGrupoMaterial()));
		}
	}
	
	private void criarFiltroDataVigencia(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getDataVigencia() != null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQuery.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL2");
			subQuery.createAlias("FSL2."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSL");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT2");
			subQuery.createAlias("MAT2."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL5");
			
			subQuery.add(Restrictions.eq("FSL."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("FSL2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("EAL5."+SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), "MAT2."+ScoMaterial.Fields.CODIGO.toString()));
						
			subQuery.add(Restrictions.isNull("FSL."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.isNotNull("FSL2."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.eqProperty("FSL2."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "FSL."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			subQuery.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
			
			DetachedCriteria subQueryAf = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN5");
			subQueryAf.setProjection(Projections.property("AFN5."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
			subQueryAf.add(Restrictions.eqProperty("AFN5."+ScoAutorizacaoForn.Fields.NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
			subQueryAf.add(Restrictions.or(
					Restrictions.gt("AFN5."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), filtro.getDataVigencia()), 
					Restrictions.isNull("AFN5."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString())));

			subQuery.add(Subqueries.propertyNotIn("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), subQueryAf));
			
			criteria.add(Subqueries.exists(subQuery));
		}
	}
	
	private void criarFiltroModalidadeCompra(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getModalidade() != null) {
				// materiais que estao em contrato
				DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF6");
				
				subQuery.createAlias("IAF6."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS6", Criteria.INNER_JOIN);
				subQuery.createAlias("FS6."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC6", Criteria.INNER_JOIN);
				
				subQuery.setProjection(Projections.property("SLC6."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));

				subQuery.add(Restrictions.eq("FS6."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuery.add(Restrictions.eq("SLC6."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuery.add(Restrictions.eq("IAF6."+ScoItemAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				subQuery.add(Restrictions.eq("IAF6."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), Boolean.TRUE));		
				subQuery.add(Restrictions.eqProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
				subQuery.add(Restrictions.eqProperty("EAL."+SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString(), "SLC6."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
				subQuery.add(Restrictions.in("IAF6." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
				
				// e para os mesmos existem outras solicitacoes de compras nao efetivadas da modalidade selecionada
				
				// ate ind_em_sc = S
				if ((filtro.getIndEmSc()!= null && filtro.getIndEmSc().isSim()) && 
						(filtro.getIndEmPac() == null || !filtro.getIndEmPac().isSim()) && 
						(filtro.getIndEmAf() == null || !filtro.getIndEmAf().isSim())) {
					DetachedCriteria subQueryMlc = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC8");
					subQueryMlc.setProjection(Projections.property("SLC8."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.add(Restrictions.eq("SLC8."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC8."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC8."+ScoSolicitacaoDeCompra.Fields.MODALIDADE_LICITACAO.toString(), filtro.getModalidade()));
					subQueryMlc.add(Restrictions.eqProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "SLC8."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.add(Restrictions.neProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SLC8."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
					
					subQuery.add(Subqueries.exists(subQueryMlc));
				}
				
				// ate ind_em_pac = S
				if ((filtro.getIndEmSc() != null && filtro.getIndEmSc().isSim()) && 
						(filtro.getIndEmPac() != null && filtro.getIndEmPac().isSim()) && 
						(filtro.getIndEmAf() == null || !filtro.getIndEmAf().isSim())) {
					DetachedCriteria subQueryMlc = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL7");
					subQueryMlc.setProjection(Projections.property("SLC7."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.createAlias("FSL7."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC7");
					subQueryMlc.createAlias("FSL7."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL7");
					subQueryMlc.createAlias("ITL7."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT7");
					subQueryMlc.add(Restrictions.eq("FSL7."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("LCT7."+ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC7."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC7."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("LCT7."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), filtro.getModalidade()));
					subQueryMlc.add(Restrictions.eqProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "SLC7."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.add(Restrictions.neProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SLC7."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
					
					subQuery.add(Subqueries.exists(subQueryMlc));
				}
				
				// ate ind_em_af = S
				if ((filtro.getIndEmSc() != null && filtro.getIndEmSc().isSim()) && 
						(filtro.getIndEmPac() != null && filtro.getIndEmPac().isSim()) && 
						(filtro.getIndEmAf() != null && filtro.getIndEmAf().isSim())) {
					DetachedCriteria subQueryMlc = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL7");
					subQueryMlc.setProjection(Projections.property("SLC7."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.createAlias("FSL7."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC7");
					subQueryMlc.createAlias("FSL7."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL7");
					subQueryMlc.createAlias("ITL7."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT7");
					subQueryMlc.add(Restrictions.eq("FSL7."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("LCT7."+ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC7."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("SLC7."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryMlc.add(Restrictions.eq("LCT7."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), filtro.getModalidade()));
					subQueryMlc.add(Restrictions.eqProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "SLC7."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryMlc.add(Restrictions.neProperty("SLC6."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SLC7."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
					
					DetachedCriteria subQueryFslAf = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL8");
					subQueryFslAf.setProjection(Projections.property("SLC8."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
					subQueryFslAf.createAlias("FSL8."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC8");
					subQueryFslAf.createAlias("FSL8."+ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF8");
					subQueryFslAf.add(Restrictions.eq("FSL8."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryFslAf.add(Restrictions.eq("SLC8."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
					subQueryFslAf.add(Restrictions.eq("SLC8."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					subQueryFslAf.add(Restrictions.in("IAF8." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));	
					subQueryFslAf.add(Restrictions.eqProperty("SLC7."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SLC8."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
					
					subQueryMlc.add(Subqueries.exists(subQueryFslAf));
					subQuery.add(Subqueries.exists(subQueryMlc));
				}
				
				criteria.add(Subqueries.exists(subQuery));
			}
	}
	
	private void criarFiltroTipoMaterial(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getTipoMaterial() != null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoMaterial.class, "MAT1");
			subQuery.setProjection(Projections.property("MAT1."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("MAT."+ScoMaterial.Fields.CODIGO.toString(), "MAT1."+ScoMaterial.Fields.CODIGO.toString()));
						
			if (filtro.getTipoMaterial().equals(DominioTipoMaterial.D)) {
				subQuery.add(Restrictions.eq("MAT1."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.FALSE));
			} else {
				subQuery.add(Restrictions.eq("MAT1."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
			}
			
			criteria.add(Subqueries.propertyIn("MAT."+ScoMaterial.Fields.CODIGO.toString(), subQuery));
		}
		
		if (filtro.getIndProducaoInterna() != null) {
			if (filtro.getIndProducaoInterna().isSim()) {
				criteria.add(Restrictions.isNotNull("MAT."+ScoMaterial.Fields.PRODUCAO_INTERNA.toString()));
			} else {
				criteria.add(Restrictions.isNull("MAT."+ScoMaterial.Fields.PRODUCAO_INTERNA.toString()));
			}
		}
	}
	
	private void criarFiltroFrequencia(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getFrequencia() != null) {
			if (filtro.getBase().equals(DominioBaseAnaliseReposicao.CA)) {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(true, false, filtro.getDataInicio(), filtro.getDataFim(), 11, null, true);
				criteria.add(Subqueries.le(filtro.getFrequencia(), subQuery));
			} else {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(true, false, filtro.getDataInicio(), filtro.getDataFim(), 5, null, true);
				criteria.add(Subqueries.le(filtro.getFrequencia(), subQuery));
			}
		}
	}
	
	private void criarFiltroFaixaValor(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getVlrInicial() != null || filtro.getVlrFinal() != null) {
			if (filtro.getBase().equals(DominioBaseAnaliseReposicao.CA)) {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(false, true, filtro.getDataInicio(), filtro.getDataFim(), 11, null, true);
				
				// infelizmente não temos um Subqueries.between
				if (filtro.getVlrInicial() != null) {
					criteria.add(Subqueries.le(filtro.getVlrInicial(), subQuery));
				}
				
				if (filtro.getVlrFinal() != null) {
					criteria.add(Subqueries.ge(filtro.getVlrFinal(), subQuery));
				}
			} else {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(false, true, filtro.getDataInicio(), filtro.getDataFim(), 5, null, true);

				// infelizmente não temos um Subqueries.between
				if (filtro.getVlrInicial() != null) {
					criteria.add(Subqueries.le(filtro.getVlrInicial(), subQuery));
				}
				
				if (filtro.getVlrFinal() != null) {
					criteria.add(Subqueries.ge(filtro.getVlrFinal(), subQuery));
				}
			}
		}
	}
	
	private void criarFiltroBasePeriodo(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getBase() != null && (filtro.getDataInicio() != null || filtro.getDataFim() != null) &&
				filtro.getFrequencia() == null && filtro.getVlrInicial() == null && filtro.getVlrFinal() == null) {
			if (filtro.getBase().equals(DominioBaseAnaliseReposicao.CA)) {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(false, false, filtro.getDataInicio(), filtro.getDataFim(), 11, null, true);
				criteria.add(Subqueries.exists(subQuery));
			} else {
				DetachedCriteria subQuery = this.obterCriteriaMovimento(false, false, filtro.getDataInicio(), filtro.getDataFim(), 5, null, true);
				criteria.add(Subqueries.exists(subQuery));
			}
		}
	}
	
	private void criarFiltroEmSc(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndEmSc() != null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
			subQuery.setProjection(Projections.property("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));			

			if (filtro.getDataVigencia() != null) {
				subQuery.add(Subqueries.propertyNotIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), obterCriteriaNumeroScContratoItemAf()));
			}
			
			if (filtro.getIndEmSc().isSim()) {
				criteria.add(Subqueries.exists(subQuery));	
			} else {
				criteria.add(Subqueries.notExists(subQuery));
			}
		}
	}
	
	public DetachedCriteria obterCriteriaNumeroScContratoItemAf() {
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF5");
		
		subQuery.createAlias("IAF5."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS5", JoinType.INNER_JOIN);
		subQuery.createAlias("FS5."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC5", JoinType.INNER_JOIN);
		
		subQuery.setProjection(Projections.property("SLC5."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));

		subQuery.add(Restrictions.eq("FS5."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq("SLC5."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq("IAF5."+ScoItemAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eqProperty("SLC5."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));

		return subQuery;
	}
	
	private void criarFiltroPontoPedido(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndPontoPedido() != null) {
			criteria.add(Restrictions.eq("EAL."+SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
			if (filtro.getIndPontoPedido().isSim()) {
				criteria.add(Restrictions.sqlRestriction("(coalesce(eal1_.qtde_disponivel,0) + coalesce(eal1_.qtde_bloqueada,0)) <= coalesce(eal1_.qtde_ponto_pedido,0)"));		
			} else {
				criteria.add(Restrictions.sqlRestriction("(coalesce(eal1_.qtde_disponivel,0) + coalesce(eal1_.qtde_bloqueada,0)) > coalesce(eal1_.qtde_ponto_pedido,0)"));				
			}
		}
	}

	private void criarFiltroAfContrato(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndAfContrato() != null && filtro.getIndEmAf() == null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQuery.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL");
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
			subQuery.createAlias("FSL."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subQuery.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
			subQuery.add(Restrictions.eq("FSL."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.isNotNull("FSL."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
				
			if (filtro.getIndAfContrato().isSim()) {
				subQuery.add(Restrictions.in("AFN."+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), new DominioModalidadeEmpenho[] {DominioModalidadeEmpenho.CONTRATO, DominioModalidadeEmpenho.ESTIMATIVA}));
			} else {
				subQuery.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.ORDINARIO));
			}
			
			criteria.add(Subqueries.exists(subQuery));
		}
	}
	
	private void criarFiltroItemContrato(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndItemContrato() != null && filtro.getIndEmAf() == null) {
			
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQuery.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL2");
			subQuery.createAlias("FSL2."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSL");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT2");
			subQuery.createAlias("MAT2."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL5");
			
			subQuery.add(Restrictions.eq("FSL."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("FSL2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("EAL5."+SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), "MAT2."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("EAL5."+SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString(), "SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			
			subQuery.add(Restrictions.isNull("FSL."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.isNotNull("FSL2."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.eqProperty("FSL2."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "FSL."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			subQuery.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
			subQuery.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtro.getIndItemContrato().isSim()));		
			
			criteria.add(Subqueries.exists(subQuery));
		}
	}
	
	private void criarFiltroAfVencida(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndAfVencida() != null && filtro.getIndEmAf() == null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
			subQuery.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN2");
			subQuery.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL2");
			subQuery.createAlias("FSL2."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSL");
			subQuery.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT2");
			subQuery.createAlias("MAT2."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL5");
			
			subQuery.add(Restrictions.eq("FSL."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("FSL2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("EAL5."+SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), "MAT2."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eqProperty("EAL5."+SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString(), "SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			
			subQuery.add(Restrictions.isNull("FSL."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.isNotNull("FSL2."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subQuery.add(Restrictions.eqProperty("FSL2."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "FSL."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			subQuery.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), Boolean.TRUE));
			subQuery.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA}));
			
			if (filtro.getIndAfVencida().isSim()) {
				subQuery.add(Restrictions.le("AFN2."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), DateUtil.truncaData(new Date())));
			} else {
				subQuery.add(
						Restrictions.or(Restrictions.gt("AFN2."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), DateUtil.truncaData(new Date())), 
								Restrictions.isNull("AFN2."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString())));
			}
			
			criteria.add(Subqueries.exists(subQuery));
		}
	}
	
	private void criarFiltroComLicitacao(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		if (filtro.getIndComLicitacao() != null && filtro.getIndEmPac() == null && filtro.getIndEmAf() == null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL2");
			subQuery.setProjection(Projections.property("FSL2."+ScoFaseSolicitacao.Fields.NUMERO.toString()));
			subQuery.createAlias("FSL2."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC2");
			subQuery.createAlias("FSL2."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL2");
			subQuery.createAlias("ITL2."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT2");
			subQuery.createAlias("LCT2."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC2");
			
			subQuery.add(Restrictions.eq("FSL2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eq("SLC2."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQuery.add(Restrictions.eqProperty("SLC2."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), "MAT."+ScoMaterial.Fields.CODIGO.toString()));
			subQuery.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.IND_LICITACAO.toString(), filtro.getIndComLicitacao().isSim()));
			subQuery.add(Restrictions.eq("MLC2."+ScoModalidadeLicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
			
			criteria.add(Subqueries.exists(subQuery));
		}
	}

	private void criarFiltroCobertura(DetachedCriteria criteria, FiltroReposicaoMaterialVO filtro) {
		
		if (filtro.getCobertura() != null) {
			// esta query foi originalmente escrita usando criteria. porem ao testar no postgres,
			// ocorre uma divisao por zero devido ao postgres nao garantir a ordem de avaliacao
			// das expressoes. A versao abaixo eh a versao final, que funciona em postgres e oracle.
			// amenegotto em 15/08/2014.
			StringBuilder sb = new StringBuilder();
			sb.append(filtro.getCobertura()).append("> (");
			sb.append("select ");
			sb.append("TRUNC(SUM((");
			sb.append("		  (COALESCE(eal99x1_.qtde_disponivel,0) + COALESCE(eal99x1_.qtde_bloqueada,0)) ");
			sb.append("		                       + COALESCE( ");
			sb.append("			 	  (SELECT SUM(COALESCE(qtde_aprovada,qtde_solicitada)) ");
			sb.append("		  FROM agh.sco_solicitacoes_de_compras slc99 ");
			sb.append("		  WHERE mat_codigo    = mat99_.codigo ");
			sb.append("				  AND ind_exclusao    = 'N' ");
			sb.append("				  AND dt_autorizacao IS NOT NULL ");
			sb.append("				  AND numero NOT     IN ");
			sb.append("				    (SELECT slc_numero ");
			sb.append("				    FROM agh.sco_fases_solicitacoes ");
			sb.append("				    WHERE slc_numero    = slc99.numero AND ind_exclusao = 'N' ");
			sb.append("				    AND iaf_afn_numero IS NOT NULL ");
			sb.append("				    ) ");
			sb.append("				  ),0) ");
			sb.append("				                               + COALESCE( ");
			sb.append("				  (SELECT SUM(COALESCE(iaf99.qtde_solicitada,0) - COALESCE(iaf99.qtde_recebida,0)) ");
			sb.append("				  FROM agh.sco_autorizacoes_forn afn99, ");
			sb.append("				    agh.sco_itens_autorizacao_forn iaf99, ");
			sb.append("				    agh.sco_fases_solicitacoes fsc99, ");
			sb.append("				    agh.sco_solicitacoes_de_compras slc ");
			sb.append("				  WHERE slc.mat_codigo        = mat99_.codigo AND slc.ind_exclusao = 'N' AND fsc99.ind_exclusao = 'N' ");
			sb.append("				  AND fsc99.slc_numero          = slc.numero ");
			sb.append("				  AND iaf99.afn_numero          = fsc99.iaf_afn_numero ");
			sb.append("				  AND iaf99.numero              = fsc99.iaf_numero ");
			sb.append("				  AND iaf99.ind_situacao       IN ('AE','PA') ");
			sb.append("				  AND afn99.numero              = iaf99.afn_numero ");
			sb.append("				  AND afn99.ind_situacao       IN ('AE','PA') ");
			sb.append("				  AND afn99.dt_vencto_contrato >= CURRENT_DATE "); 
			sb.append("				  ),0) ");
			sb.append("				  		                       + COALESCE( ");
			sb.append("				  (SELECT SUM(COALESCE(pea.qtde,0) - COALESCE(pea.qtde_entregue,0)) ");
			sb.append("				  FROM agh.sco_progr_entrega_itens_af pea, ");
			sb.append("				    agh.sco_autorizacoes_forn afn98, ");
			sb.append("				    agh.sco_itens_autorizacao_forn iaf98, ");
			sb.append("				    agh.sco_fases_solicitacoes fsc98, ");
			sb.append("				    agh.sco_solicitacoes_de_compras slc98 ");
			sb.append("				  WHERE slc98.mat_codigo       = mat99_.codigo AND fsc98.ind_exclusao = 'N' AND slc98.ind_exclusao = 'N' ");
			sb.append("				  AND fsc98.slc_numero         = slc98.numero ");
			sb.append("				  AND iaf98.afn_numero         = fsc98.iaf_afn_numero ");
			sb.append("				  AND iaf98.numero             = fsc98.iaf_numero ");
			sb.append("				  AND iaf98.ind_situacao      IN ('AE','PA') ");
			sb.append("				  AND afn98.numero             = iaf98.afn_numero ");
			sb.append("				  AND afn98.ind_situacao      IN ('AE','PA') ");
			sb.append("				  AND afn98.dt_vencto_contrato < CURRENT_DATE ");
			sb.append("				  AND pea.iaf_afn_numero     = iaf98.afn_numero ");
			sb.append("				  AND pea.iaf_numero         = iaf98.numero ");
			sb.append("				  AND pea.ind_planejamento   = 'S' ");
			sb.append("				  AND pea.ind_assinatura     = 'S' ");
			sb.append("				  AND pea.ind_cancelada      = 'N' ),");
			sb.append("			0)       ) / "); 
			sb.append("			case when ((eal99x1_.qtde_ponto_pedido / case when eal99x1_.tempo_reposicao <> 0 then eal99x1_.tempo_reposicao end)) <> 0 then ");	
			sb.append("	                (eal99x1_.qtde_ponto_pedido / case when eal99x1_.tempo_reposicao <> 0 then eal99x1_.tempo_reposicao end) end), ");
			sb.append("	                2) as cobertura_dias "); 
			sb.append("from ");
			sb.append("AGH.SCO_MATERIAIS MAT99_"); 
			sb.append("        inner join ");
			sb.append("(select * from AGH.SCE_ESTQ_ALMOXS a ");
			sb.append("where coalesce(a.qtde_ponto_pedido,0) > 0 "); 
			sb.append("and coalesce(a.tempo_reposicao,0) > 0 ) eal99x1_ "); 
			sb.append("on MAT99_.CODIGO=eal99x1_.MAT_CODIGO ");
			sb.append("inner join ");
			sb.append("AGH.SCO_FORNECEDORES frn99x3_ "); 
			sb.append("on eal99x1_.FRN_NUMERO=frn99x3_.NUMERO "); 
			sb.append("inner join ");
			sb.append("            agh.sce_estq_gerais egr99x2_ "); 
			sb.append("                on MAT99_.CODIGO=egr99x2_.mat_codigo "); 
			sb.append("        where ");
			sb.append("            frn99x3_.NUMERO= ").append(filtro.getFornecedorPadrao().getVlrNumerico().intValue());
			sb.append("            and eal99x1_.ALM_SEQ=this_.ALM_SEQ "); 
			sb.append("            AND EGR99X2_.FRN_NUMERO=").append(filtro.getFornecedorPadrao().getVlrNumerico().intValue());
			sb.append("            and egr99x2_.dt_competencia=TO_DATE('").append(DateUtil.dataToString(filtro.getCompetencia().getVlrData(), "dd/MM/yyyy HH:mm:ss") ).append("', 'DD/MM/YYYY HH24:MI:SS')"); 
			sb.append("            AND MAT99_.IND_SITUACAO='A' ");
			sb.append("            and MAT99_.IND_ESTOCAVEL='S' ");
			sb.append("            AND MAT99_.CODIGO=THIS_.CODIGO ");
			sb.append("        )");
			
			criteria.add(Restrictions.sqlRestriction(sb.toString()));
		}
	}
	
	public DetachedCriteria obterCriteriaReposicaoMaterial(FiltroReposicaoMaterialVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemReposicaoMaterialVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemReposicaoMaterialVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), ItemReposicaoMaterialVO.Fields.CODIGO_GRUPO.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), ItemReposicaoMaterialVO.Fields.NOME_GRUPO.toString());
		p.add(Projections.sqlProjection("CASE WHEN {alias}." + ScoMaterial.Fields.IND_ESTOCAVEL.name() + " = 'N' THEN '"+DominioTipoMaterial.D.getDescricao()+"' ELSE '"+DominioTipoMaterial.E.getDescricao()+"' END as " + ItemReposicaoMaterialVO.Fields.TIPO_MATERIAL.toString(), new String[]{ItemReposicaoMaterialVO.Fields.TIPO_MATERIAL.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()), ItemReposicaoMaterialVO.Fields.PONTO_PEDIDO.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString()), ItemReposicaoMaterialVO.Fields.TEMPO_REPOSICAO.toString());
		p.add(Projections.property("EGR." + SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString()), ItemReposicaoMaterialVO.Fields.CUSTO_MEDIO.toString());
		p.add(Projections.property("ALM." + SceAlmoxarifado.Fields.CCT_CODIGO.toString()), ItemReposicaoMaterialVO.Fields.CC_ALMOX.toString());
		
		criteria.setProjection(p);

		criteria.createAlias("MAT."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR");
		criteria.createAlias("MAT."+ScoMaterial.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EAL."+SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN");
		criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedorPadrao().getVlrNumerico().intValue()));
		criteria.add(Restrictions.eqProperty("EAL."+SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), "MAT."+ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()));
		criteria.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.FRN_NUMERO.toString(), filtro.getFornecedorPadrao().getVlrNumerico().intValue()));
		criteria.add(Restrictions.eq("EGR."+SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), filtro.getCompetencia().getVlrData()));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criarFiltroTipoMaterial(criteria, filtro);
		criarFiltroGrupoMaterial(criteria, filtro);
		criarFiltroNivelClassificacao(criteria, filtro);
		criarFiltroClassificacaoABC(criteria, filtro);
		criarFiltroCcAplicacao(criteria, filtro);
		criarFiltroEmLicitacao(criteria, filtro);
		criarFiltroBasePeriodo(criteria, filtro);
		criarFiltroDataVigencia(criteria, filtro);
		criarFiltroModalidadeCompra(criteria, filtro);
		criarFiltroFaixaValor(criteria, filtro);
		criarFiltroFrequencia(criteria, filtro);
		criarFiltroEmSc(criteria, filtro);
		criarFiltroEmAf(criteria, filtro);
		criarFiltroPontoPedido(criteria, filtro);
		criarFiltroCobertura(criteria, filtro);
		criarFiltroComLicitacao(criteria, filtro);
		criarFiltroAfContrato(criteria, filtro);
		criarFiltroAfVencida(criteria, filtro);
		criarFiltroItemContrato(criteria, filtro);
		
		return criteria;
	}
	
	public Integer obterMaxId(){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.setProjection(Projections.max(ScoMaterial.Fields.CODIGO.toString()));
		return (Integer) this.executeCriteriaUniqueResult(criteria);		
	}

	
	public Long pesquisarMaterialReposicaoCount(FiltroReposicaoMaterialVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(filtro);
		return this.executeCriteriaCount(criteria);
	}

	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroReposicaoMaterialVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(filtro);
		criteria.setResultTransformer(Transformers.aliasToBean(ItemReposicaoMaterialVO.class));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(FiltroReposicaoMaterialVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(filtro);
		criteria.setResultTransformer(Transformers.aliasToBean(ItemReposicaoMaterialVO.class));
		return this.executeCriteria(criteria);
	}
	
	public ItemReposicaoMaterialVO obterMaterialReposicao(FiltroReposicaoMaterialVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(filtro);
		criteria.setResultTransformer(Transformers.aliasToBean(ItemReposicaoMaterialVO.class));
		return (ItemReposicaoMaterialVO) this.executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterHistoricoConsumo(Date dtIni, Date dtFim, Integer matCodigo, FccCentroCustos ccAplic) {
		DetachedCriteria criteria = this.obterCriteriaMovimento(false, true, dtIni, dtFim, 5, matCodigo, false);
		
		if (ccAplic != null) {
			criteria.add(Restrictions.eq("MMT."+SceMovimentoMaterial.Fields.CENTRO_CUSTO.toString(), ccAplic));
		}
		
		return (Long) this.executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterHistoricoDevolucao(Date dtIni, Date dtFim, Integer matCodigo, FccCentroCustos ccAplic) {
		DetachedCriteria criteria = this.obterCriteriaMovimento(false, true, dtIni, dtFim, 3, matCodigo, false);
		
		if (ccAplic != null) {
			criteria.add(Restrictions.eq("MMT."+SceMovimentoMaterial.Fields.CENTRO_CUSTO.toString(), ccAplic));
		}
		
		return (Long) this.executeCriteriaUniqueResult(criteria);
	}
	
	public void unirTransacao(){
		joinTransaction();
	}
	
	/**
	 * Dia favorável para entrega
	 * 
	 * C7 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @return
	 */
	public DominioDiaSemanaMes buscarDiaFavoravel(Integer matCodigo){
		ScoMaterial material = super.obterPorChavePrimaria(matCodigo);
		return material != null && material.getGrupoMaterial() != null ? material.getGrupoMaterial().getDiaFavEntgMaterial() : null;
	}

	public ScoMaterial buscarMaterial(Integer numeroPac, Short item) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SLC");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroPac));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), item));
		return (ScoMaterial) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoMaterial> obterMateriaisOrteseseProtesesSB(BigDecimal paramVlNumerico, Object objPesquisa) {
		ScoMaterial material = new ScoMaterial();
		material.setIndSituacao(DominioSituacao.A);
		String objPesquisaStr = (String) objPesquisa;
		if (StringUtils.isNotBlank(objPesquisaStr)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				Integer codigo = Integer.valueOf(objPesquisaStr);
				material.setCodigo(codigo);
			} else {
				material.setNome(objPesquisaStr);
			}
		}

		DetachedCriteria criteria = pesquisarCriteriaMaterial(material);
		criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));

		if (paramVlNumerico != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(),
					Integer.valueOf(paramVlNumerico.toString())));
		}

		return this.executeCriteria(criteria);
	}


	private DetachedCriteria pesquisarCriteriaMaterial(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if (material.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}

		if (material.getNome() != null && StringUtils.isNotBlank(material.getNome())) {
			criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), "%" + material.getNome().trim() + "%", MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
//		criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), Integer.valueOf("2")));

		return criteria;
	}

	/**
	 * Verifica se existe uma ScoMaterial pelo código do ScoGrupoMaterial
	 * 
	 * C8 de #31584
	 * 
	 * @param gmtCodigo
	 * @return
	 */
	public boolean verificarExistenciaScoMaterialPorScoGrupoMaterial(Integer gmtCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), gmtCodigo));
		return super.executeCriteriaExists(criteria);
	}
	
	
	public ScoMaterial obterScoMaterialDetalhadoPorChavePrimaria(Integer codigo) {
		
		ScoMaterial material = this.obterPorChavePrimaria(codigo, true, 
				ScoMaterial.Fields.ORIGEM_PARECER_TECNICO, 
				ScoMaterial.Fields.GRUPO_MATERIAL, 
				ScoMaterial.Fields.ALMOXARIFADO, 
				ScoMaterial.Fields.ALM_SEQ_LOCAL_ESTQ, 
				ScoMaterial.Fields.UNIDADE_MEDIDA,
				ScoMaterial.Fields.MEDICAMENTO,
				ScoMaterial.Fields.MARCA_MODELO,
				ScoMaterial.Fields.SERVIDOR,
				ScoMaterial.Fields.SERVIDOR_ALTERADO,
				ScoMaterial.Fields.SERVIDOR_DESATIVADO,
				ScoMaterial.Fields.SERVIDOR_CAD_SAPIENS
		);
		
		if(material.getServidor()!=null && material.getServidor().getPessoaFisica()!=null){
			material.getServidor().getPessoaFisica().getNome();
		}
		if(material.getServidorAlteracao()!=null && material.getServidorAlteracao().getPessoaFisica()!=null){
			material.getServidorAlteracao().getPessoaFisica().getNome();
		}
		if(material.getServidorDesativado()!=null && material.getServidorDesativado().getPessoaFisica()!=null){
			material.getServidorDesativado().getPessoaFisica().getNome();
		}
		if(material.getServidorCadSapiens()!=null && material.getServidorCadSapiens().getPessoaFisica()!=null){
			material.getServidorCadSapiens().getPessoaFisica().getNome();
		}
		if(material.getMarcasModelos()!=null){
			material.getMarcasModelos().size();
		}
		return material;
//		TODO Não estava buscando o servidor através do criteria
//		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class,"mat");
//		criteria.createAlias("mat."+ScoMaterial.Fields.ORIGEM_PARECER_TECNICO.toString(), "opt", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "gp", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.ALM_SEQ_LOCAL_ESTQ.toString(), "asle", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "um", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.MEDICAMENTO.toString(), "med", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("mat."+ScoMaterial.Fields.MARCA_MODELO.toString(), "mm", JoinType.LEFT_OUTER_JOIN);
//				
//				criteria.createAlias("mat."+ScoMaterial.Fields.SERVIDOR.toString(), "serv", JoinType.INNER_JOIN);
//				criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);
//				
//				criteria.createAlias("mat."+ScoMaterial.Fields.SERVIDOR_ALTERADO.toString(), "serAlt", JoinType.LEFT_OUTER_JOIN);
//				criteria.createAlias("serAlt."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesAlt", JoinType.LEFT_OUTER_JOIN);
//				
//				criteria.createAlias("mat."+ScoMaterial.Fields.SERVIDOR_DESATIVADO.toString(), "serDes", JoinType.LEFT_OUTER_JOIN);
//				criteria.createAlias("serDes."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesDes", JoinType.LEFT_OUTER_JOIN);
//				
//				criteria.createAlias("mat."+ScoMaterial.Fields.SERVIDOR_CAD_SAPIENS.toString(), "serCad", JoinType.LEFT_OUTER_JOIN);
//				criteria.createAlias("serCad."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesCad", JoinType.LEFT_OUTER_JOIN);
				
				
//		criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), codigo));
//		return (ScoMaterial) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MateriaisParalClassificacaoVO> pesquisarClassificaMaterial(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class,"MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		
		if (material.getCodigo() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}
		
		if (material.getGrupoMaterial() != null && material.getGrupoMaterial().getCodigo()!=null) {
			criteria.add(Restrictions.eq("GMT."+ScoGrupoMaterial.Fields.CODIGO.toString(), material.getGrupoMaterial().getCodigo()));
		}
		
		if (material.getUmdCodigo() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString(), material.getUmdCodigo()));
		}
		
		if (material.getIndProducaoInterna() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.PRODUCAO_INTERNA.toString(), material.getIndProducaoInterna()));
		}
		
		if (material.getIndSituacao() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.SITUACAO.toString(), material.getIndSituacao()));
		}
		
		if (material.getEstocavel() != null) {
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), material.getEstocavel()));
		}
		criteria.setProjection(Projections.distinct(Projections.projectionList().
				add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()), MateriaisParalClassificacaoVO.Fields.COD_MATERIAL.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()), MateriaisParalClassificacaoVO.Fields.NOME_MATERIAL.toString()).
				add(Projections.property("GMT."+ScoGrupoMaterial.Fields.CODIGO.toString()), MateriaisParalClassificacaoVO.Fields.COD_GRUPO.toString()).
				add(Projections.property("GMT."+ScoGrupoMaterial.Fields.DESCRICAO.toString()), MateriaisParalClassificacaoVO.Fields.NOME_GRUPO.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.SITUACAO.toString()), MateriaisParalClassificacaoVO.Fields.ATIVO.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.PRODUCAO_INTERNA.toString()), MateriaisParalClassificacaoVO.Fields.PRODUCAO_INTERNA.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.IND_ESTOCAVEL.toString()), MateriaisParalClassificacaoVO.Fields.ESTOCAVEL.toString()).
				add(Projections.property("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), MateriaisParalClassificacaoVO.Fields.UNIDADE.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(MateriaisParalClassificacaoVO.class));
		
		return executeCriteria(criteria);
	}

	public Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa,
			List<Integer> listaGmtCodigo) {
		
		DetachedCriteria criteria = montaCriteriaMateriaisAtivosGrupoMaterial(
				objPesquisa, listaGmtCodigo);

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #35690 C2
	 * Monta a consulta para suggestionBox de Material
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @param codGrupoMaterial Código do grupo de materiais para filtro.
	 * @return Consulta para suggestionBox
	 */
	private DetachedCriteria montarCriteriaPesquisarMaterialCodDescricaoGrupo(String filtro, Integer codGrupoMaterial){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if(codGrupoMaterial != null){
			criteria.add(Restrictions.eq(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codGrupoMaterial));
		}
		
		if (filtro != null && StringUtils.isNotBlank(filtro)) {
			Disjunction ou = Restrictions.or(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroInteger(filtro)) {
				ou.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(filtro)));
			} 
			criteria.add(ou);
		}
		return criteria;
	}
	
	/**
	 * #35690 C2
	 * Realiza a consulta para suggestionBox de Material
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @param codGrupoMaterial Código do grupo de materiais para filtro.
	 * @return Lista de Material de acordo com o filtro
	 */
	public List<ScoMaterial> pesquisarMaterialCodDescricaoGrupo(String filtro, Integer codGrupoMaterial){
		DetachedCriteria criteria = montarCriteriaPesquisarMaterialCodDescricaoGrupo(filtro, codGrupoMaterial);
		return executeCriteria(criteria, 0, 100, ScoMaterial.Fields.NOME.toString(), true);
	}
	
	/**
	 * #35690 C2
	 * Count da consulta para suggestionBox de Material
	 * @param filtro O que for digitado no suggestionBox que será utilizado como filtro.
	 * @param codGrupoMaterial Código do grupo de materiais para filtro.
	 * @return Quantidade de Material retornado de acordo com o filtro
	 */
	public Long pesquisarMaterialCodDescricaoGrupoCount(String filtro, Integer codGrupoMaterial){
		DetachedCriteria criteria = montarCriteriaPesquisarMaterialCodDescricaoGrupo(filtro, codGrupoMaterial);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Obtem count de lista de {@link ScoMaterial} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @return {@link Long}
	 */
	public Long obterScoMaterialPorCodigoNomeDescricaoCount(final String parametro) {
		return executeCriteriaCount(obterCriteriaScoMaterialPorCodigoNomeDescricao(parametro));
	}
	
	/**
	 * Obtem lista de {@link ScoMaterial} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @return {@link List} de {@link ScoMaterial}
	 */
	public List<MaterialMDAFVO> obterScoMaterialPorCodigoNomeDescricao(final String parametro) {
		return executeCriteria(obterCriteriaScoMaterialPorCodigoNomeDescricao(parametro), 0, 100, ScoMaterial.Fields.NOME.toString(), true);
	}
	
	/**
	 * Obtem criteria para consulta de {@link ScoMaterial} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @param isCount {@link Boolean}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaScoMaterialPorCodigoNomeDescricao(final String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE),
						Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE),
						Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE)));
			}
		}
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoMaterial.Fields.CODIGO.toString()), MaterialMDAFVO.Fields.CODIGO.toString())
				.add(Projections.property(ScoMaterial.Fields.NOME.toString()), MaterialMDAFVO.Fields.NOME.toString())
				.add(Projections.property(ScoMaterial.Fields.DESCRICAO.toString()), MaterialMDAFVO.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MaterialMDAFVO.class));
		return criteria;
	}

	
	/**
	 * Consulta que atende as Functions F1, F2, F3, F4, F6, F7, F9, F10, F11 da
	 * estória #6635.
	 * @param dataGeracao
	 * @param tmvDocNr
	 * @return
	 */
	public List<MovimentoMaterialVO> consultarMovimentosMateriais(
			Date dataGeracao, String[] parametrosList, Boolean funcaoJoin,
			Boolean indPatrimonio, Boolean mes, Boolean fornecedor) {

		DetachedCriteria subQueryParam = DetachedCriteria.forClass(
				AghParametros.class, "AGP");

		subQueryParam.setProjection(Projections.property("AGP."
				+ AghParametros.Fields.VLR_NUMERICO.toString()));

		Disjunction disjunction = Restrictions.disjunction();
		for (String parametro : parametrosList) {
			disjunction.add(Restrictions.eq(
					"AGP." + AghParametros.Fields.NOME.toString(), parametro));
		}
		subQueryParam.add(disjunction);

		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceMovimentoMaterial.class, "MMT");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()), MovimentoMaterialVO.Fields.TIPO_MOVIMENTO_SEQ.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), MovimentoMaterialVO.Fields.VALOR.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()), MovimentoMaterialVO.Fields.IND_ESTORNO.toString());
		
		criteria.setProjection(p);
		
		if (funcaoJoin) {
			criteria.createAlias(
					"MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(),
					"MAT", JoinType.INNER_JOIN);
			criteria.createAlias(
					"MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
					"GMT", JoinType.INNER_JOIN);
		}
		if (fornecedor) {
			DetachedCriteria subQueryForn = DetachedCriteria.forClass(
					AghParametros.class, "AGP");

			subQueryForn.setProjection(Projections.property("AGP."
					+ AghParametros.Fields.VLR_NUMERICO.toString()));
			subQueryForn.add(Property.forName(
					"AGP." + AghParametros.Fields.NOME.toString()).eq(
					"P_NUMERO_FORNECEDOR_HU"));

			criteria.add(Subqueries.propertyEq("MMT."
					+ SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString(),
					subQueryForn));
		}

		if (parametrosList != null && parametrosList.length != 0) {
			criteria.add(Subqueries.propertyIn(
					"MMT."
							+ SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ
									.toString(), subQueryParam));
		}

		if (dataGeracao != null) {

			if (mes) {
				criteria.add(Restrictions.eq(
						"MMT."
								+ SceMovimentoMaterial.Fields.DATA_COMPETENCIA
										.toString(),
						DateUtil.obterDataInicioCompetencia(dataGeracao)));
				criteria.add(Restrictions.between("MMT."
						+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString(),
						DateUtil.obterDataInicioCompetencia(dataGeracao),
						DateUtil.obterDataComHoraFinal(dataGeracao)));
			} else {

				criteria.add(Restrictions.between("MMT."
						+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString(),
						DateUtil.truncaData(dataGeracao),
						DateUtil.obterDataComHoraFinal(dataGeracao)));
			}
		}

		if (funcaoJoin && !indPatrimonio) {
			criteria.add(Restrictions.or(Restrictions.isNull("GMT."
					+ ScoGrupoMaterial.Fields.IND_PATRIMONIO.toString()),
					Restrictions.eq(
							"GMT."
									+ ScoGrupoMaterial.Fields.IND_PATRIMONIO
											.toString(),
							DominioSimNao.N.isSim())));
		} else if (funcaoJoin && indPatrimonio) {
			criteria.add(Restrictions.or(Restrictions.isNull("GMT."
					+ ScoGrupoMaterial.Fields.IND_PATRIMONIO.toString()),
					Restrictions.eq(
							"GMT."
									+ ScoGrupoMaterial.Fields.IND_PATRIMONIO
											.toString(),
							DominioSimNao.S.isSim())));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MovimentoMaterialVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * #6625 Consulta F12, F13, F14, F15, F16, F17, F18, F19, F20 e F21
	 * @param dataGeracao
	 * @param parametro <P_TMV_DOC_NR> parametro do sistema
	 * @param codigoGrupoMaterial
	 * @param estocavel Identifica se é material estocável ou não.
	 * @return lista de movimento material de acordo com o filtro.
	 */
	@SuppressWarnings("unchecked")
	public List<MovimentoMaterialVO> gmtEntradaMateriaisFormula(
			Date dataGeracao, Integer codigoGrupoMaterial,
			String[] listParametro, Boolean estocavel, Boolean mes) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceMovimentoMaterial.class, "MMT");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()), MovimentoMaterialVO.Fields.TIPO_MOVIMENTO_SEQ.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), MovimentoMaterialVO.Fields.VALOR.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()), MovimentoMaterialVO.Fields.IND_ESTORNO.toString());
		
		criteria.setProjection(p);
		
		DetachedCriteria subQueryParam = DetachedCriteria.forClass(
				AghParametros.class, "AGP");

		subQueryParam.setProjection(Projections.property("AGP."
				+ AghParametros.Fields.VLR_NUMERICO.toString()));

		Disjunction disjunction = Restrictions.disjunction();
		for (String parametro : listParametro) {
			disjunction.add(Restrictions.eq(
					"AGP." + AghParametros.Fields.NOME.toString(), parametro));
		}

		subQueryParam.add(disjunction);

		criteria.createAlias(
				"MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT");

		criteria.add(Subqueries.propertyIn("MMT."
				+ SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(),
				subQueryParam))
				.add(Restrictions.eqProperty("MMT."
				+ SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(),
				"MAT." + ScoMaterial.Fields.CODIGO.toString()))
				.add(Restrictions.eq(
				"MAT." + ScoMaterial.Fields.GMT_CODIGO.toString(),
				codigoGrupoMaterial));

		criteria.add(Restrictions.eq(
				"MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), estocavel));

		if (mes) {
			criteria.add(Restrictions.eq("MMT."
					+ SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(),
					DateUtil.obterDataInicioCompetencia(dataGeracao)))
					.add(Restrictions.between("MMT."
					+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString(),
					DateUtil.obterDataInicioCompetencia(dataGeracao),
					DateUtil.obterDataComHoraFinal(dataGeracao)));

		} else {
			criteria.add(Restrictions.between("MMT."
					+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString(),
					DateUtil.truncaData(dataGeracao),
					DateUtil.obterDataComHoraFinal(dataGeracao)));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MovimentoMaterialVO.class));
		Criteria executableCriteria = dataAcess.createExecutableCriteria(criteria);
		executableCriteria.setFlushMode(FlushMode.MANUAL);
		executableCriteria.setReadOnly(true);
		return (List<MovimentoMaterialVO>)executableCriteria.list();
	}
	
	/**
	 * Estoria 29192
	 * @param pEstocavel
	 * @param flagIndConfaz
	 * @param indCapCmed
	 * @return
	 */
	 public List<RelatorioMedicamentosCAPVO> pesquisaScoMaterial(DominioSimNao pEstocavel,DominioSimNao indCapCmed,DominioSimNao indConfaz){
		 ScoPesquisaMaterialQueryBuilder builder = new ScoPesquisaMaterialQueryBuilder();
		 DetachedCriteria criteria = builder.build(pEstocavel, indCapCmed, indConfaz);		 
		 return executeCriteria(criteria);
	 }
	
	//#28830 
	
	/**
	 * #28830 SB4 C4
	 * Método utilizado criação de criteria para obter Materiais 
	 * utilizados no SB.
	 */
	private DetachedCriteria obterCriteriaMaterialVOPorCodigoNomeSB(Object object){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		String strPesquisa = (String)object;
		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroLong(strPesquisa)) {
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoMaterial.Fields.CODIGO.toString()), MaterialVO.Fields.CODIGO.toString())
				.add(Projections.property(ScoMaterial.Fields.NOME.toString()), MaterialVO.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MaterialVO.class));
		
		return criteria;
	}
	
	/**
	 * #28830 SB4 C4
	 * Método utilizado para obter lista de Materiais
	 * utilizados no SB.
	 */
	public List<MaterialVO> obterMaterialVOPorCodigoNomeSB(Object object){
		DetachedCriteria criteria = obterCriteriaMaterialVOPorCodigoNomeSB(object);
		return executeCriteria(criteria, 0, 100, MaterialVO.Fields.NOME.toString(), true);
	}
	
	/**
	 * Método utilizado para obter quantidade de Materiais encontrados 
	 */
	public Long obterMaterialVOPorCodigoNomeSBCount(Object object){
		DetachedCriteria criteria = obterCriteriaMaterialVOPorCodigoNomeSB(object);
		return executeCriteriaCount(criteria);
	}
	
	public List<RelatorioMedicamentosCAPVO> pesquisaScoMaterialPorDataCodigoMaterial(Integer codigo, Date dataInicial, Date dataFinal){
		DetachedCriteria criteria = null;
		 ScoPesquisaMaterialPorDataCodigoMatQueryBuilder builder = new ScoPesquisaMaterialPorDataCodigoMatQueryBuilder();
			criteria = builder.build(codigo, dataInicial, dataFinal);		 
		 return executeCriteria(criteria);
	 }

	/**
	 * Obtém count de lista de {@link ScoMaterial} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @return {@link Long}
	 */
	public Long obterMateriaisPorCodigoOuDescricaoCount(final String parametro) {
		return executeCriteriaCount(obterCriteriaMaterialPorCodigoOuDescricao(parametro, true));
	}
	
	/**
	 * Obtém lista de {@link ScoMaterial} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @return {@link List} de {@link ScoMaterial}
	 */
	public List<ScoMaterial> obterMateriaisPorCodigoOuDescricao(final String parametro) {
		return executeCriteria(obterCriteriaMaterialPorCodigoOuDescricao(parametro, false), 0, 100, null);
	}
	
	/**
	 * Obtém criteria para consulta de {@link ScoMaterial} para Suggestion Box.
	 * #43464
	 * @param parametro {@link String}
	 * @param isCount {@link Boolean}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaMaterialPorCodigoOuDescricao(final String parametro, final boolean isCount) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		if(!isCount){
			criteria.addOrder(Order.asc(ScoMaterial.Fields.NOME.toString()));
		}
		return criteria;
	}
	
	/**
	 * #46298 - Obtem Lista de Materiais Ativos por Codigo, Nome ou Descrição
	 */
	public List<ScoMaterial> obterListaMateriaisAtivosPorCodigoNomeOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				Criterion restrictionNome = Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), filter, MatchMode.ANYWHERE);
				Criterion restrictionDescricao = Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(restrictionNome, restrictionDescricao));
			}
		}
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, ScoMaterial.Fields.NOME.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Materiais Ativos por Codigo, Nome ou Descrição
	 */
	public Long obterCountMateriaisAtivosPorCodigoNomeOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				Criterion restrictionNome = Restrictions.ilike(ScoMaterial.Fields.NOME.toString(), filter, MatchMode.ANYWHERE);
				Criterion restrictionDescricao = Restrictions.ilike(ScoMaterial.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(restrictionNome, restrictionDescricao));
			}
		}
		criteria.add(Restrictions.eq(ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
}