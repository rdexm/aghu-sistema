package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ScoRecebParcelasAutorizacaoFornDAO extends BaseDao<ScoAutorizacaoForn>{

    private static final long serialVersionUID = 1408998692677403418L;

    public List<ScoAutorizacaoForn> pesquisarAFNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, ScoMaterial material, ScoServico servico){
            List<ScoAutorizacaoForn> listaAF = new ArrayList<ScoAutorizacaoForn>();
            List<DominioTipoFaseSolicitacao> tiposSolicicao = new ArrayList<DominioTipoFaseSolicitacao>(); 
            tiposSolicicao.add(DominioTipoFaseSolicitacao.C);
            tiposSolicicao.add(DominioTipoFaseSolicitacao.S);

            for (DominioTipoFaseSolicitacao tipoSolicitacao : tiposSolicicao){
                    DetachedCriteria criteria = criteriaAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, tipoSolicitacao, material, servico);
                    criteria.addOrder(Order.asc(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()));
                    List<ScoAutorizacaoForn> listaAFAuxiliar = executeCriteria(criteria);
                    listaAF.addAll(listaAFAuxiliar);
            }

            return listaAF;
    }

    public List<ScoAutorizacaoForn> pesquisarComplementoNumAFNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, ScoMaterial material, ScoServico servico){
            List<ScoAutorizacaoForn> listaAF = new ArrayList<ScoAutorizacaoForn>();

            List<DominioTipoFaseSolicitacao> tiposSolicicao = new ArrayList<DominioTipoFaseSolicitacao>(); 
            tiposSolicicao.add(DominioTipoFaseSolicitacao.C);
            tiposSolicicao.add(DominioTipoFaseSolicitacao.S);

            for (DominioTipoFaseSolicitacao tipoSolicitacao : tiposSolicicao){
                    DetachedCriteria criteria = criteriaAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor,  tipoSolicitacao, material, servico);
                    criteria.addOrder(Order.asc(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));                                        
                    List<ScoAutorizacaoForn> listaAFAuxiliar = executeCriteria(criteria);
                    listaAF.addAll(listaAFAuxiliar);
            }

            return listaAF;                
    }

    public List<ScoFornecedor> pesquisarFornecedorNumAfNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Object fornFilter, ScoMaterial material, ScoServico servico){
            List<ScoFornecedor> listaFornecedor = new ArrayList<ScoFornecedor>();

            List<DominioTipoFaseSolicitacao> tiposSolicicao = new ArrayList<DominioTipoFaseSolicitacao>(); 
            tiposSolicicao.add(DominioTipoFaseSolicitacao.C);
            tiposSolicicao.add(DominioTipoFaseSolicitacao.S);

            for (DominioTipoFaseSolicitacao tipoSolicitacao : tiposSolicicao){
                    List<ScoFornecedor> listaFornecedorAux;

                    DetachedCriteria criteria = criteriaAFComplementoFornecedor(numeroAf, numComplementoAf, null,  tipoSolicitacao, material, servico);        
                    criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROPOSTA");                
                    criteria.setProjection(Projections.property("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()));

        			String strPesquisa = "";
        			if (fornFilter != null) {
        				strPesquisa = fornFilter.toString();
        			}

                    if (StringUtils.isNotBlank(strPesquisa)) {
                            if (CoreUtil.isNumeroInteger(strPesquisa)) {
                                    criteria.add(Restrictions.eq("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), Integer.parseInt(strPesquisa)));
                            } else {
                                    criteria.createAlias("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORNECEDOR");                
                                    criteria.add(Restrictions.ilike("FORNECEDOR." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE));
                            }
                    }
                    criteria.setProjection(Projections.distinct(Projections.property("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString())));
                    criteria.addOrder(Order.asc("PROPOSTA." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()));
                    //criteria.getExecutableCriteria(getSession()).setMaxResults(50);
                    
                    listaFornecedorAux = executeCriteria(criteria, 0, 50, null, false);
                    listaFornecedor.addAll(listaFornecedorAux);
            }

            return listaFornecedor;
    }

    public List<ScoMaterial> pesquisarMaterialaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String param){
            List<ScoMaterial> listaMaterial = new ArrayList<ScoMaterial>();

            DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");

            criteria.add(Restrictions.eq("FS." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
            criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);                                                
            criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
            criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);                        

            String strPesquisa = param;

            if (StringUtils.isNotBlank(strPesquisa)) {
                    if (CoreUtil.isNumeroInteger(strPesquisa)) {
                            criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(strPesquisa)));
                    } else {
                            criteria.add(Restrictions.ilike("MAT." + ScoMaterial.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
                    }                        
            }

            DetachedCriteria subQuery = criteriaAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, DominioTipoFaseSolicitacao.C, null, null);                        
            subQuery.setProjection(Projections.property("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));                
            criteria.add(Subqueries.propertyIn("FS."+ ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), subQuery));
            criteria.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()));
            
            criteria.setProjection(Projections.projectionList().add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()),ScoMaterial.Fields.CODIGO.toString()).add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()),ScoMaterial.Fields.NOME.toString()));

            criteria.setResultTransformer(Transformers.aliasToBean(ScoMaterial.class));
            listaMaterial = executeCriteria(criteria);                

            return listaMaterial;
    }

    public List<ScoServico> pesquisarServicoaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, Object param){                
            List<ScoServico> listaServicos = new ArrayList<ScoServico>();

            DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");

            criteria.add(Restrictions.eq("FS." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
            criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);        
            criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
            criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERV", JoinType.INNER_JOIN);
            String strPesquisa = (String) param;

            if (StringUtils.isNotBlank(strPesquisa)) {
                    if (CoreUtil.isNumeroInteger(strPesquisa)) {
                            criteria.add(Restrictions.eq("SERV." + ScoServico.Fields.CODIGO.toString(), Integer.parseInt(strPesquisa)));
                    } else {
                            criteria.add(Restrictions.ilike("SERV." + ScoServico.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
                    }                        
            }

            DetachedCriteria subQuery = criteriaAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, DominioTipoFaseSolicitacao.S, null, null);                        
            subQuery.setProjection(Projections.property("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));                
            criteria.add(Subqueries.propertyIn("FS."+ ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), subQuery));
            criteria.setProjection(Projections.property("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString()));

            criteria.setProjection(Projections.projectionList().add(Projections.property("SERV."+ScoServico.Fields.CODIGO.toString()),ScoServico.Fields.CODIGO.toString()).add(Projections.property("SERV."+ScoServico.Fields.NOME.toString()),ScoServico.Fields.NOME.toString()));

            criteria.setResultTransformer(Transformers.aliasToBean(ScoServico.class));

            listaServicos = executeCriteria(criteria);

            return listaServicos;
    }

    public DetachedCriteria criteriaAFComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, DominioTipoFaseSolicitacao tipoSolicitacao, ScoMaterial material, ScoServico servico){

            DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");        

            if (numeroAf != null){
                    criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
            }

            if (numComplementoAf != null){
                    criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), numComplementoAf));
            }

            if (numFornecedor != null){
                    criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), numFornecedor));
            }
            DetachedCriteria subQueryFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");

            subQueryFases.setProjection(Projections.property("FS." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
            subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);                        
            subQueryFases.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
            subQueryFases.add(Restrictions.ne("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecedor.EX));
            subQueryFases.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));

            if (material != null || servico != null) {
                    if (material != null){                                
                            subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
                            subQueryFases.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);                        
                            subQueryFases.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));                                
                    } else {
                            subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
                            subQueryFases.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SER", JoinType.INNER_JOIN);
                            subQueryFases.add(Restrictions.eq("SER." + ScoServico.Fields.CODIGO.toString(), servico.getCodigo()));                                        
                    }
            }
            criteria.add(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), subQueryFases));

            DetachedCriteria subQuery = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");

            subQuery.setProjection(Projections.property(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
            subQuery.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), "AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
            subQuery.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
            subQuery.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));

            // Cláusulas retiradas da consulta pelo chamado #30180
            // Quando incluir cláusulas novamente, incluir também na consulta ScoProgEntregaItemAutorizacaoFornecimentoDAO.pesquisarProgEntregaItensComSaldoPositivo()
//            subQuery.add(Restrictions.or(Restrictions.and(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), Boolean.TRUE),
//                                                              Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString(), Boolean.FALSE)),
//                                             Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString(), Boolean.TRUE)));                  
            subQuery.add(Restrictions.or(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S), 
                                                 Restrictions.isNotNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA.toString())));
            if (tipoSolicitacao.equals(DominioTipoFaseSolicitacao.C)){                        
                    subQuery.add(Restrictions.or(Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()),
                                                         Restrictions.ltProperty(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), 
                                                             ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString())));                         
            } else {
//        			subQuery.add(Restrictions.eq(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString(), 0));                    
                    subQuery.add(Restrictions.or(Restrictions.isNull(ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()),
                                                         Restrictions.gtProperty(ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString(), 
                                                             ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString())));                        
            }
            criteria.add(Subqueries.propertyIn(ScoAutorizacaoForn.Fields.NUMERO.toString(), subQuery));

            return criteria;
    }
	
}
