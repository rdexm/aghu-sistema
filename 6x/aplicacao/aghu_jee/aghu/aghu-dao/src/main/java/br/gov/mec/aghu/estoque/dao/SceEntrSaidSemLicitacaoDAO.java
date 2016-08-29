package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.MaterialConsultaVO;
import br.gov.mec.aghu.estoque.vo.MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO;
import br.gov.mec.aghu.estoque.vo.SceEntrSaidSemLicitacaoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * DAO da entidade {@link SceEntrSaidSemLicitacao}
 * 
 * @author luismoura
 * 
 */
public class SceEntrSaidSemLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceEntrSaidSemLicitacao> {
	private static final long serialVersionUID = -5850741817113149692L;
	
	
	/**
	 * Busca a sigla de um ESL
	 * #29051
	 * @param eslSeq
	 * @return
	 */
	public String pesquisarDescricaoTipoMovimento(Integer eslSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class, "ESL");
		criteria.createAlias("ESL." + SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO, "TMV");
		criteria.add(Restrictions.eq("ESL."+SceEntrSaidSemLicitacao.Fields.SEQ, eslSeq));
		criteria.setProjection(Projections.distinct(Projections.property("TMV."+SceTipoMovimento.Fields.DESCRICAO.toString())));
		return (String) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Verifica se exite ESL Pendente
	 * C20 de #29051
	 * @param afnNumero
	 * @return
	 */
	public Boolean existeEslPendenteParaAF(Integer afnNumero){
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class, "ESL");
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		subCriteria.add(Restrictions.eq("NRP."+ SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		subCriteria.setProjection(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.ESL_SEQ.toString()));
		subCriteria.add(Restrictions.eq("NRP."+	SceNotaRecebProvisorio.Fields.AFN_NUMERO.toString(), afnNumero));

		subCriteria.add(Restrictions.eqProperty("NRP."+ SceNotaRecebProvisorio.Fields.ESL_SEQ, "ESL." + SceEntrSaidSemLicitacao.Fields.SEQ));
		
		criteria.add(Subqueries.exists(subCriteria));
		criteria.add(Restrictions.eq("ESL."+ SceEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("ESL."+ SceEntrSaidSemLicitacao.Fields.IND_ENCERRADO.toString(), Boolean.FALSE));
		return executeCriteriaCount(criteria) > 0;
	}

	private DetachedCriteria obterCriteriaMaterial(MaterialConsultaVO material, DetachedCriteria criteria){
		if(material.getSeqTipoMovimento() != null){
			criteria.add(Restrictions.eq("esl."+ SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO_SEQ, material.getSeqTipoMovimento()));
		}
		if(material.getNumeroDocumento() != null){
			criteria.add(Restrictions.eq("esl."+ SceEntrSaidSemLicitacao.Fields.SEQ.toString(), material.getNumeroDocumento()));
		}
		if(material.getDtInicio() != null && material.getDtFim() != null){
			Date dataInicio = DateUtil.obterDataComHoraInical(material.getDtInicio());
			Date dataFim = DateUtil.obterDataComHoraFinal(material.getDtFim());
			criteria.add(Restrictions.between("esl."+ SceEntrSaidSemLicitacao.Fields.DATA_GERACAO.toString(), dataInicio, dataFim));
		}
		if(material.getNumeroFornecedor() != null){
			criteria.add(Restrictions.eq("esl."+ SceEntrSaidSemLicitacao.Fields.FRN_NUMERO.toString(), material.getNumeroFornecedor()));
		}
		if(material.getNumeroDocumentoFiscal() != null){
			criteria.add(Restrictions.eq("dfe."+ SceDocumentoFiscalEntrada.Fields.NUMERO, material.getNumeroDocumentoFiscal()));
		}
		if(material.getCodigoMaterial() != null){
			criteria.add(Restrictions.eq("isl." + SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString(), material.getCodigoMaterial()));
		}

		return criteria;
	}
	
	public DetachedCriteria obterCriteriaMaterialContinuacao(MaterialConsultaVO material, DetachedCriteria criteria){
		
		if(material.getNumeroAF() != null){
			criteria.add(Restrictions.eq("afn." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), material.getNumeroAF()));
		}
		if(material.getNumeroComplemento() != null){
			criteria.add(Restrictions.eq("afn." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO, material.getNumeroComplemento()));
		}
		if(material.getEncerrado() != null){
			criteria.add(Restrictions.eq("esl." + SceEntrSaidSemLicitacao.Fields.IND_ENCERRADO, material.getEncerrado().isSim()));
		}
		if(material.getEfetivado() != null){
			criteria.add(Restrictions.eq("esl." + SceEntrSaidSemLicitacao.Fields.IND_EFETIVADO, material.getEfetivado().name()));
		}
		if(material.getEstornado() != null){
			criteria.add(Restrictions.eq("esl." + SceEntrSaidSemLicitacao.Fields.IND_ESTORNO, material.getEstornado().isSim()));
		}
		if(material.getAdiantamento() != null){
			criteria.add(Restrictions.eq("esl." + SceEntrSaidSemLicitacao.Fields.IND_ADIANTAMENTO_AF, material.getAdiantamento().name()));
		}
		
		return criteria;
	}

	public List<MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO> obterListaMaterialSemAutorizacao(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, MaterialConsultaVO material){
		
		DetachedCriteria criteria = obterAliasProjection();
		criteria = obterCriteriaMaterial(material, criteria);
		criteria = obterCriteriaMaterialContinuacao(material, criteria);
		
		if(orderProperty == null){
			
			criteria.addOrder(Order.asc("esl."+SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO_SEQ.toString()));
			criteria.addOrder(Order.asc("esl."+SceEntrSaidSemLicitacao.Fields.SEQ.toString()));
			criteria.addOrder(Order.asc("esl."+SceEntrSaidSemLicitacao.Fields.DATA_GERACAO.toString()));
			criteria.addOrder(Order.asc("isl."+ SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString()));
		}
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long obterListaMaterialSemAutorizacaoCount(MaterialConsultaVO material){
		
		DetachedCriteria criteria = obterAliasProjection();
		criteria = obterCriteriaMaterial(material, criteria);
		criteria = obterCriteriaMaterialContinuacao(material, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterAliasProjection(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class, "esl");
		
		criteria.createAlias("esl." + SceEntrSaidSemLicitacao.Fields.ITEM_ESL.toString(), "isl", JoinType.INNER_JOIN);
		criteria.createAlias("esl." + SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.INNER_JOIN);
		criteria.createAlias("esl." + SceEntrSaidSemLicitacao.Fields.FORNECEDOR.toString(), "frn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("esl." + SceEntrSaidSemLicitacao.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("isl." + SceItemEntrSaidSemLicitacao.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.createAlias("isl." + SceItemEntrSaidSemLicitacao.Fields.MARCA_COMERCIAL.toString(), "mcm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("isl." + SceItemEntrSaidSemLicitacao.Fields.ITEM_AUTORIZACAO_FORN.toString(), "iaf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("iaf." + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), "afn", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
        
		projList.add(Projections.property("tmv." + SceTipoMovimento.Fields.SIGLA.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.SIGLA_MOVIMENTO.toString());
        projList.add(Projections.property("esl." + SceEntrSaidSemLicitacao.Fields.SEQ.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.NUMERO_ESL.toString());
        projList.add(Projections.property("esl." + SceEntrSaidSemLicitacao.Fields.DATA_GERACAO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.DT_GERACAO.toString());
        projList.add(Projections.property("frn." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.FORNECEDOR_RAZAO_SOCIAL.toString());
        projList.add(Projections.property("frn." + ScoFornecedor.Fields.CGC.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.FORNECEDOR_CNPJ.toString());
        projList.add(Projections.property("frn." + ScoFornecedor.Fields.CPF.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.FORNECEDOR_CPF.toString());
        projList.add(Projections.property("esl." + SceEntrSaidSemLicitacao.Fields.CONTATO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.CONTATO.toString());
        projList.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.DOC_FISCAL.toString());
        projList.add(Projections.property("mat." + ScoMaterial.Fields.NOME.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.MATERIAL.toString());
        projList.add(Projections.property("mat." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.UNIDADE.toString());
        projList.add(Projections.property("isl." + SceItemEntrSaidSemLicitacao.Fields.QUANTIDADE.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.QUANTIDADE.toString());
        projList.add(Projections.property("isl." + SceItemEntrSaidSemLicitacao.Fields.QTDE_DEVOLVIDA.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.QUANTIDADE_DEVOLVIDA.toString());
        projList.add(Projections.property("mcm." + ScoMarcaComercial.Fields.DESCRICAO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.MARCA.toString());
        projList.add(Projections.property("isl." + SceItemEntrSaidSemLicitacao.Fields.SC_NUMERO.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.SC.toString());
        projList.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.SEQ.toString()), 
        		MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.Fields.DFE_SEQ.toString());
        
        criteria.setProjection(projList);
        
        criteria.add(Restrictions.eq("tmv."+ SceTipoMovimento.Fields.IND_TRATA_DEVOLUCAO.toString(), Boolean.TRUE));
        criteria.add(Restrictions.eq("tmv."+ SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        criteria.setResultTransformer(Transformers.aliasToBean(MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO.class));
        
        return criteria;
	}
	
	public Long obterNumeroESLPorSiglaTipoMovimentoCount(String sigla, String siglaTipoMovimento) { 
		
		return executeCriteriaCount(obterCriteriaNumeroESLPorSiglaTipoMovimento(sigla, siglaTipoMovimento));
	}
	
	
	public List<SceEntrSaidSemLicitacaoVO> obterNumeroESLPorSiglaTipoMovimento(String sigla, String siglaTipoMovimento){ 
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class);
		
		criteria = obterCriteriaNumeroESLPorSiglaTipoMovimento(sigla, siglaTipoMovimento);
		
		return executeCriteria(criteria, 0, 100, SceEntrSaidSemLicitacao.Fields.SEQ.toString(), true);
	}
	
	public DetachedCriteria obterCriteriaNumeroESLPorSiglaTipoMovimento(String sigla, String siglaTipoMovimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class, "ESL");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.SEQ.toString()), "eslSeq");
		projList.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), "tmvSigla");
		projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString()), "matCodigo");
		projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.QUANTIDADE.toString()), "quantidade");
		projList.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), "numeroFornecedor");
		projList.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "razaoSocialFornecedor");
		projList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), "matNome");
		
		criteria.setProjection(projList);
		
		criteria.createAlias("ESL."+ SceEntrSaidSemLicitacao.Fields.ITEM_ESL.toString(), "ISL", JoinType.INNER_JOIN);
		criteria.createAlias("ESL."+ SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
		criteria.createAlias("ESL."+ SceEntrSaidSemLicitacao.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("ISL."+ SceItemEntrSaidSemLicitacao.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString(), DominioIndOperacaoBasica.CR));
		if(sigla!=null && !sigla.trim().isEmpty()) {
			criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.SIGLA.toString(), sigla));
		}else{
			criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.SIGLA.toString(), siglaTipoMovimento));
		}
		criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.IND_ESL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.IND_SITUACAO, DominioSituacao.A));
		criteria.add(Restrictions.eq("ESL."+ SceEntrSaidSemLicitacao.Fields.IND_EFETIVADO.toString(), DominioSimNao.N.toString()));
		criteria.add(Restrictions.eq("ESL."+ SceEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SceEntrSaidSemLicitacaoVO.class));
		
		return criteria; 
		
	}
}