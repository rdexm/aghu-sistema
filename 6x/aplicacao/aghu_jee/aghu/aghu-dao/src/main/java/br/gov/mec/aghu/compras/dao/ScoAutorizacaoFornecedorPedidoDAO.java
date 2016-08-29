package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.autfornecimento.vo.ScoDataEnvioFornecedorVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAFPVO;
import br.gov.mec.aghu.compras.vo.AFPFornecedoresVO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.compras.vo.PesquisaAutFornPedidosVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoCumXProgrEntrega;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgrAcessoFornAfp;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoAutorizacaoFornecedorPedidoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAutorizacaoFornecedorPedido>{
	
	private static final long serialVersionUID = 7560943552026609152L;
	
public List<ScoItemAFPVO> listarAfsPorPedidoENumAf(Integer afpNumero, Integer numeroPac, Short nroComplemento , int espEmpenho){		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class, "AFP");
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AF", Criteria.INNER_JOIN);
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "IAFP", Criteria.LEFT_JOIN);
		criteria.createAlias("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAUT");
		criteria.createAlias("IAUT." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPROS");
		criteria.createAlias("IPROS." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILIC", Criteria.LEFT_JOIN);
		criteria.createAlias("ILIC." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", Criteria.LEFT_JOIN);
				
		criteria.add(Restrictions.eq("AFP."+ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), afpNumero));
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroPac));
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()), ScoItemAFPVO.Fields.IAF_AFN_NUMERO.toString())
				.add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()), ScoItemAFPVO.Fields.IAF_NUMERO.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()), ScoItemAFPVO.Fields.SCO_COMPRA.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()), ScoItemAFPVO.Fields.SCO_SERVICO.toString())
				.add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), ScoItemAFPVO.Fields.NUMERO_PARCELA.toString())
				.add(Projections.property("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), ScoItemAFPVO.Fields.SEQ_ITEM_AFP.toString())
				.add(Projections.property("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString()), ScoItemAFPVO.Fields.AUT_FORN.toString());
				criteria.setProjection(projection);
				
		criteria.addOrder(Order.desc("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA));
		criteria.addOrder(Order.asc("IAFP." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA));
		criteria.addOrder(Order.asc("ILIC." + ScoItemLicitacao.Fields.NUMERO));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAFPVO.class));		
		return executeCriteria(criteria);
	}

    public List<AutorizacaoFornPedidosVO> pesquisarAutFornPedidosPorDefault(Integer first, Integer max){
    	DetachedCriteria criteria = this.obterCriteriaAutFornPedidos();
    	
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_AFP.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_AFN.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()), AutorizacaoFornPedidosVO.Fields.DT_ENVIO_FORNECEDOR.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO.toString()), AutorizacaoFornPedidosVO.Fields.IND_PUBLICADO.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO.toString()), AutorizacaoFornPedidosVO.Fields.DT_PUBLICACAO.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_PAC.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), AutorizacaoFornPedidosVO.Fields.NUM_COMPLEMENTO_AF.toString())
				.add(Projections.property("PFO." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()), AutorizacaoFornPedidosVO.Fields.FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUM_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), AutorizacaoFornPedidosVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), AutorizacaoFornPedidosVO.Fields.CGC.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), AutorizacaoFornPedidosVO.Fields.CPF.toString())
				.add(Projections.property("PEFG." + RapPessoasFisicas.Fields.NOME.toString()), AutorizacaoFornPedidosVO.Fields.NOME_GESTOR.toString())
					.add(Projections.property("PEFR." + RapPessoasFisicas.Fields.NOME.toString()), AutorizacaoFornPedidosVO.Fields.NOME_SERVIDOR.toString());
				criteria.setProjection(projection);
				
	 	Criterion criterion1 = Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString(), this.obterCriteriaSubQueryI());
		Criterion criterion2 = Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), this.obterCriteriaSubQueryII());
		criteria.add(Restrictions.disjunction().add(Restrictions.disjunction().add(criterion1).add(criterion2)));				
		criteria.add(Restrictions.eq("AFE." + ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO, DominioAfpPublicado.N));
		criteria.add(Subqueries.propertyNotIn("PFO." + ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), this.obterCriteriaSubQueryIII()));		
		criteria.addOrder(Order.asc("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteria.addOrder(Order.desc("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(AutorizacaoFornPedidosVO.class));
		return this.executeCriteria(criteria, first, max, null, true);
    }
    
	public List<AutorizacaoFornPedidosVO> pesquisarAutFornPedidosPorFiltro(Integer first, Integer max, PesquisaAutFornPedidosVO filtroVO){
		DetachedCriteria criteria = this.obterCriteriaAutFornPedidos();
		
		if (filtroVO.getIndEnviada() != null || filtroVO.getIndImpressa() != null) {
			criteria.createAlias("AFE" + "." + ScoAutorizacaoFornecedorPedido.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "PEI", Criteria.INNER_JOIN);
		}
				
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_AFP.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_AFN.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()), AutorizacaoFornPedidosVO.Fields.DT_ENVIO_FORNECEDOR.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO.toString()), AutorizacaoFornPedidosVO.Fields.IND_PUBLICADO.toString())
				.add(Projections.property("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO.toString()), AutorizacaoFornPedidosVO.Fields.DT_PUBLICACAO.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUMERO_PAC.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), AutorizacaoFornPedidosVO.Fields.NUM_COMPLEMENTO_AF.toString())
				.add(Projections.property("PFO." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()), AutorizacaoFornPedidosVO.Fields.FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), AutorizacaoFornPedidosVO.Fields.NUM_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), AutorizacaoFornPedidosVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), AutorizacaoFornPedidosVO.Fields.CGC.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), AutorizacaoFornPedidosVO.Fields.CPF.toString())
				.add(Projections.property("PEFG." + RapPessoasFisicas.Fields.NOME.toString()), AutorizacaoFornPedidosVO.Fields.NOME_GESTOR.toString())
				.add(Projections.property("PEFR." + RapPessoasFisicas.Fields.NOME.toString()), AutorizacaoFornPedidosVO.Fields.NOME_SERVIDOR.toString());
				criteria.setProjection(projection);
				
	 	this.obterCriteriaFiltrosI(filtroVO, criteria);	
		this.obterCriteriaFiltrosII(filtroVO, criteria);				
		criteria.addOrder(Order.asc("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteria.addOrder(Order.desc("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(AutorizacaoFornPedidosVO.class));
		return this.executeCriteria(criteria, first, max, null, true);
	}
	
	public Long pesquisarAutFornPedidosPorDefaultCount(){
		DetachedCriteria criteria = this.obterCriteriaAutFornPedidos();
		Criterion criterion1 = Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString(), this.obterCriteriaSubQueryI());
		Criterion criterion2 = Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), this.obterCriteriaSubQueryII());
		criteria.add(Restrictions.disjunction().add(Restrictions.disjunction().add(criterion1).add(criterion2)));				
		criteria.add(Restrictions.eq("AFE." + ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO, DominioAfpPublicado.N));
		criteria.add(Subqueries.propertyNotIn("PFO." + ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), this.obterCriteriaSubQueryIII()));		
		return executeCriteriaCount(criteria);
    }
	
	public Long pesquisarAutFornPedidosPorFiltroCount(PesquisaAutFornPedidosVO filtroVO){		
		DetachedCriteria criteria = this.obterCriteriaAutFornPedidos();		
		if (filtroVO.getIndEnviada() != null || filtroVO.getIndImpressa() != null) {
			criteria.createAlias("AFE" + "." + ScoAutorizacaoFornecedorPedido.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "PEI", Criteria.INNER_JOIN);
		}		
		this.obterCriteriaFiltrosI(filtroVO, criteria);	
		this.obterCriteriaFiltrosII(filtroVO, criteria);		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaAutFornPedidos() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class, "AFE");		
		criteria.createAlias("AFE" + "." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AFN", Criteria.INNER_JOIN);
		criteria.createAlias("AFN" + "." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFO", Criteria.INNER_JOIN);
		criteria.createAlias("PFO" + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		criteria.createAlias("AFN" + "." + ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), "SEG", Criteria.LEFT_JOIN);
		criteria.createAlias("SEG" + "." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEFG", Criteria.LEFT_JOIN);
		criteria.createAlias("AFN" + "." + ScoAutorizacaoForn.Fields.SERVIDOR.toString(), "SER", Criteria.LEFT_JOIN);
		criteria.createAlias("SER" + "." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEFR", Criteria.LEFT_JOIN);		
		return criteria;		
		
	}
	
	public DetachedCriteria obterCriteriaSubQueryI() {
		DetachedCriteria criteriaSubQueryI = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEI");
		criteriaSubQueryI.setProjection(Projections.property("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()));		
		criteriaSubQueryI.add(Restrictions.eqProperty("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), "AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteriaSubQueryI.add(Restrictions.eqProperty("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), "AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		criteriaSubQueryI.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA, Boolean.TRUE));
		criteriaSubQueryI.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA, Boolean.FALSE));
		criteriaSubQueryI.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO, Boolean.FALSE));
		criteriaSubQueryI.add(Restrictions.disjunction()
				.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S))
				.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.P))
				.add(Restrictions.isNotNull("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString())));		
		return criteriaSubQueryI;
	}
	
	public DetachedCriteria obterCriteriaSubQueryII() {
		DetachedCriteria criteriaSubQueryII = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEI");
		criteriaSubQueryII.setProjection(Projections.property("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()));		
		criteriaSubQueryII.add(Restrictions.eqProperty("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), "AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteriaSubQueryII.add(Restrictions.eqProperty("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), "AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		criteriaSubQueryII.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA, Boolean.TRUE));
		criteriaSubQueryII.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA, Boolean.FALSE));
		criteriaSubQueryII.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO, Boolean.FALSE));
		criteriaSubQueryII.add(Restrictions.disjunction()
				.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.S))
				.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), DominioAfEmpenhada.P))
				.add(Restrictions.isNotNull("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_JUSTIFICATIVA_CODIGO.toString())));	 	
	 	return criteriaSubQueryII;
	}
	
	public DetachedCriteria obterCriteriaSubQueryIII() {
		DetachedCriteria criteriaSubQueryIII = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class, "PCA");
		criteriaSubQueryIII.setProjection(Projections.property("PCA." + ScoProgrCodAcessoForn.Fields.NRO_FORNECEDOR.toString()));
		criteriaSubQueryIII.add(Restrictions.eqProperty("PCA." + ScoProgrCodAcessoForn.Fields.NRO_FORNECEDOR.toString(), "AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
		criteriaSubQueryIII.add(Restrictions.isNotNull("PCA." + ScoProgrCodAcessoForn.Fields.DT_ENVIO_FORNECEDOR.toString()));		
		return criteriaSubQueryIII;
	}
	
	public void obterCriteriaFiltrosI(PesquisaAutFornPedidosVO filtroVO, DetachedCriteria criteria) {
		
		if (filtroVO.getNumeroPAC() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtroVO.getNumeroPAC()));
		}
		
		if (filtroVO.getNroComplementoAF() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroVO.getNroComplementoAF()));
		}
				
		if (filtroVO.getNumeroAFP() != null) {
			criteria.add(Restrictions.eq("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), filtroVO.getNumeroAFP()));
		}

		if (filtroVO.getFornecedor() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtroVO.getFornecedor().getNumero()));
		}

	 	if (filtroVO.getIndEnviada() != null) {
	 		criteria.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), filtroVO.getIndEnviada()));
		}
	 	
	 	if (filtroVO.getIndImpressa() != null) {
	 		criteria.add(Restrictions.eq("PEI." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_IMPRESSA.toString(), filtroVO.getIndImpressa()));
		}
	}
	
	public void obterCriteriaFiltrosII(PesquisaAutFornPedidosVO filtroVO, DetachedCriteria criteria) {
		
		if (filtroVO.getDataInicioEnvio() != null) {
			criteria.add(Restrictions.ge("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString(), filtroVO.getDataInicioEnvio()));
		}
	 	if (filtroVO.getDataFimEnvio() != null) {
			criteria.add(Restrictions.le("AFE." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString(), filtroVO.getDataFimEnvio()));
		}

		if (filtroVO.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), filtroVO.getServidorGestor()));
		}
				
		if (filtroVO.getGrupoMaterial() != null || filtroVO.getMaterial() != null) {
			criteria.add(Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString(), this.obterCriteriaSubQueryMaterial(filtroVO)));
		}
				
		if (filtroVO.getGrupoServico() != null || filtroVO.getServico() != null) {
			criteria.add(Subqueries.propertyIn("AFE." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString(), this.obterCriteriaSubQueryServico(filtroVO)));
		}
	}
	
	public DetachedCriteria obterCriteriaSubQueryMaterial(PesquisaAutFornPedidosVO filtroVO) {		
		DetachedCriteria subQueryMaterial = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		subQueryMaterial.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		subQueryMaterial.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.INNER_JOIN);
		subQueryMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);		
		if (filtroVO.getGrupoMaterial() != null) {
			if (filtroVO.getGrupoMaterial().getCodigo() != null) {
				subQueryMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), filtroVO.getGrupoMaterial().getCodigo()));					
			}
		}		
		if (filtroVO.getMaterial() != null) {
			if (filtroVO.getMaterial().getCodigo() != null) {
				subQueryMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtroVO.getMaterial().getCodigo()));					
			}
		}	 	
	 	return subQueryMaterial;
	}
	
	public DetachedCriteria obterCriteriaSubQueryServico(PesquisaAutFornPedidosVO filtroVO) {		
		DetachedCriteria subQueryServico = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		subQueryServico.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
	
		subQueryServico.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.INNER_JOIN);
		subQueryServico.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.INNER_JOIN);
		
		if (filtroVO.getGrupoServico() != null) {
			if (filtroVO.getGrupoServico().getCodigo() != null) {
				subQueryServico.add(Restrictions.eq("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtroVO.getGrupoServico().getCodigo()));					
			}
		}		
		if (filtroVO.getServico() != null) {
			if (filtroVO.getServico().getCodigo() != null) {
				subQueryServico.add(Restrictions.eq("SRV." + ScoServico.Fields.CODIGO.toString(), filtroVO.getServico().getCodigo()));					
			}
		}		
		return subQueryServico;
	}
	
	public List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalAF(filtro, true);
		if (firstResult != null) {
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(criteria);
	}
	
	public Long listarAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalAF(filtro, false);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPrincipalAF(FiltroPesquisaGeralAFVO filtro, Boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),
					PesquisaGeralAFVO.Fields.PFR_LCT_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()),
					PesquisaGeralAFVO.Fields.PFR_FRN_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),
					PesquisaGeralAFVO.Fields.NRO_COMPLEMENTO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()),
					PesquisaGeralAFVO.Fields.SEQUENCIA_ALTERACAO.toString())
			.add(Projections.property("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString()),
					PesquisaGeralAFVO.Fields.MODALIDADE_LICITACAO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString()),
					PesquisaGeralAFVO.Fields.SITUACAO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString()),
					PesquisaGeralAFVO.Fields.DT_GERACAO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString()),
					PesquisaGeralAFVO.Fields.SER_MATRICULA_GESTOR.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString()),
					PesquisaGeralAFVO.Fields.SER_VIN_CODIGO_GESTOR.toString());
		criteria.setProjection(projection);
		obterCriteriaFiltrosBasicosAFParte1(filtro, criteria);
		obterCriteriaFiltrosBasicosAFParte2(filtro, criteria);
		if (filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals("")) {
			obterCriteriaTermoLivreAF(criteria, filtro);
		}
		obterCriteriaPesquisaAvancadaAFParte1(criteria, filtro);
		obterCriteriaPesquisaAvancadaAFParte2(criteria, filtro);
		if (ordenar.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.desc("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())).addOrder(
					Order.asc("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())).addOrder(
							Order.asc("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaGeralAFVO.class));
		return criteria;
	}

	private void obterCriteriaFiltrosBasicosAFParte1(FiltroPesquisaGeralAFVO filtro, DetachedCriteria criteria) {
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		if (filtro.getSequencia() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString(), filtro.getSequencia()));
		}
		if (filtro.getNumeroAFP() != null) {
			criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.SCO_AF_PEDIDOS.toString(), "AFP");
			criteria.add(Restrictions.eq("AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), filtro.getNumeroAFP()));
		}
		if(filtro.getItem() != null) {
			criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF");
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), filtro.getItem()));
		}
		if (filtro.getModalidadeCompra() != null) {
			criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), filtro.getModalidadeCompra().getCodigo()));
		}
		if (filtro.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString(),
					filtro.getServidorGestor().getId().getMatricula()));
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString(),
					filtro.getServidorGestor().getId().getVinCodigo()));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
	}

	private void obterCriteriaFiltrosBasicosAFParte2(FiltroPesquisaGeralAFVO filtro, DetachedCriteria criteria) {
		if (filtro.getDataGeracaoInicial() != null && filtro.getDataGeracaoFinal() != null) {
			criteria.add(Restrictions.between("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(),
					filtro.getDataGeracaoInicial(), filtro.getDataGeracaoFinal()));
		}else{
			if(filtro.getDataGeracaoInicial() != null){
				criteria.add(Restrictions.gt("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(),
						filtro.getDataGeracaoInicial()));
			}else if(filtro.getDataGeracaoFinal() != null){
				criteria.add(Restrictions.lt("AF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(),
						filtro.getDataGeracaoFinal()));
			}
		}
		if (filtro.getPrevisaoEntregaInicial() != null && filtro.getPrevisaoEntregaFinal() != null) {
			criteria.add(Restrictions.between("AF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(),
					filtro.getPrevisaoEntregaInicial(), filtro.getPrevisaoEntregaFinal()));
		}else{
			if(filtro.getPrevisaoEntregaInicial() != null){
				criteria.add(Restrictions.gt("AF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(),
						filtro.getPrevisaoEntregaInicial()));
			}else if(filtro.getPrevisaoEntregaFinal() != null){
				criteria.add(Restrictions.lt("AF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(),
						filtro.getPrevisaoEntregaFinal()));
			}
		}
		if (filtro.getModalidadeEmpenho() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), filtro.getModalidadeEmpenho()));
		}
		if (filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtro.getFornecedor().getNumeroFornecedor()));
		}
	}
	
	private void obterCriteriaTermoLivreAF(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria subCriteriaTermoLivre = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		subCriteriaTermoLivre.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", Criteria.INNER_JOIN);
		subCriteriaTermoLivre.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);
		subCriteriaTermoLivre.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.LEFT_JOIN);
		subCriteriaTermoLivre.setProjection(Projections.property("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subCriteriaTermoLivre.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		Criterion slcDescricaoEq = Restrictions.like("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion slsDescricaoEq = Restrictions.like("SLS." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion matNomeEq = Restrictions.like("MAT." + ScoMaterial.Fields.NOME.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion matDescricaoEq = Restrictions.like("MAT." + ScoMaterial.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion srvNomeEq = Restrictions.like("SRV." + ScoServico.Fields.NOME.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion srvDescricaoEq = Restrictions.like("SRV." + ScoServico.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion lctDescricaoEq = Restrictions.like("LCT." + ScoLicitacao.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		subCriteriaTermoLivre.add(Restrictions.or(slcDescricaoEq, Restrictions.or(slsDescricaoEq, Restrictions.or(matNomeEq,
				Restrictions.or(matDescricaoEq, Restrictions.or(srvNomeEq, Restrictions.or(srvDescricaoEq, lctDescricaoEq)))))));
		criteria.add(Subqueries.propertyIn("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaTermoLivre));
	}
	
	private void obterCriteriaPesquisaAvancadaAFParte1(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		if (filtro.getGrupoMaterial() != null || filtro.getMaterial() != null) {
			DetachedCriteria subCriteriaMaterial = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteriaMaterial.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			subCriteriaMaterial.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			if (filtro.getGrupoMaterial() != null) {
				subCriteriaMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(),
						filtro.getGrupoMaterial().getCodigo()));
			}
			subCriteriaMaterial.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaMaterial.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaMaterial.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			if (filtro.getMaterial() != null) {
				subCriteriaMaterial.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
			}
			criteria.add(Subqueries.propertyIn("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaMaterial));
		}
		if (filtro.getGrupoServico() != null || filtro.getServico() != null) {
			DetachedCriteria subCriteriaServico = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteriaServico.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
			subCriteriaServico.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
			if (filtro.getGrupoServico() != null) {
				subCriteriaServico.add(Restrictions.eq("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(),
						filtro.getGrupoServico().getCodigo()));
			}
			subCriteriaServico.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaServico.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaServico.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			if (filtro.getServico() != null) {
				subCriteriaServico.add(Restrictions.eq("SRV." + ScoServico.Fields.CODIGO.toString(), filtro.getServico().getCodigo()));
			}
			criteria.add(Subqueries.propertyIn("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaServico));
		}
		if (filtro.getCentroCustoSolicitante() != null || filtro.getCentroCustoAplicacao() != null) {
			DetachedCriteria subCriteriaCentroCusto = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteriaCentroCusto.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
			subCriteriaCentroCusto.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
			subCriteriaCentroCusto.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaCentroCusto.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaCentroCusto.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			if (filtro.getCentroCustoSolicitante() != null) {
				Criterion slsCodigoSolicitanteEq = Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO.toString(),
						filtro.getCentroCustoSolicitante().getCodigo());
				Criterion slcCodigoSolicitanteEq = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(),
						filtro.getCentroCustoSolicitante().getCodigo());
				subCriteriaCentroCusto.add(Restrictions.or(slsCodigoSolicitanteEq, slcCodigoSolicitanteEq));
			}
			if (filtro.getCentroCustoAplicacao() != null) {
				Criterion slsCodigoAplicacaoEq = Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString(),
						filtro.getCentroCustoAplicacao().getCodigo());
				Criterion slcCodigoAplicacaoEq = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(),
						filtro.getCentroCustoAplicacao().getCodigo());
				subCriteriaCentroCusto.add(Restrictions.or(slsCodigoAplicacaoEq, slcCodigoAplicacaoEq));
			}
			criteria.add(Subqueries.propertyIn("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaCentroCusto));
		}
	}
	
	private void obterCriteriaPesquisaAvancadaAFParte2(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		if (filtro.getVerbaGestao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.VBG_SEQ.toString(),
					filtro.getVerbaGestao().getSeq()));
		}
		if (filtro.getGrupoNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString(),
					filtro.getGrupoNaturezaDespesa().getCodigo()));
		}
		if (filtro.getNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString(),
					filtro.getNaturezaDespesa().getId().getCodigo()));
		}
		if (filtro.getNumeroContrato() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtro.getNumeroContrato()));
		}
		if (filtro.getEntregaAtrasada() != null && filtro.getEntregaAtrasada().equals(Boolean.TRUE)) {
			DetachedCriteria subCriteriaEntregaAtrasada = DetachedCriteria
				.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
			subCriteriaEntregaAtrasada.setProjection(Projections.property(
					"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
			subCriteriaEntregaAtrasada.add(Restrictions.eqProperty(
					"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(),
					"AF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
			subCriteriaEntregaAtrasada.add(Restrictions.eq(
					"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			subCriteriaEntregaAtrasada.add(Restrictions.eq(
					"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), Boolean.TRUE));
			subCriteriaEntregaAtrasada.add(Restrictions.eq(
					"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
			if (filtro.getDiasVencimento() != null) {
				Date dataCalculada = DateUtil.adicionaDiasFracao(new Date(), filtro.getDiasVencimento().floatValue());
				subCriteriaEntregaAtrasada.add(Restrictions.lt(
						"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataCalculada));
			} else {
				subCriteriaEntregaAtrasada.add(Restrictions.lt(
						"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
			}
			if (filtro.getNumeroAFP() != null) {
				subCriteriaEntregaAtrasada.add(Restrictions.eq(
						"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), filtro.getNumeroAFP()));
			}
			criteria.add(Subqueries.propertyIn("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaEntregaAtrasada));
		}
	}
	
	public List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalIAF(filtro, true);
		if (firstResult != null) {
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(criteria);
	}
	
	public Long listarItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalIAF(filtro, false);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPrincipalIAF(FiltroPesquisaGeralAFVO filtro, Boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", Criteria.INNER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
		if ((filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals(""))|| filtro.getGrupoMaterial() != null) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		}
		if ((filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals(""))|| filtro.getGrupoServico() != null) {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.LEFT_JOIN);
		}
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),PesquisaGeralIAFVO.Fields.PFR_LCT_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()),PesquisaGeralIAFVO.Fields.PFR_FRN_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),PesquisaGeralIAFVO.Fields.NRO_COMPLEMENTO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()),PesquisaGeralIAFVO.Fields.SEQUENCIA_ALTERACAO.toString())
			.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()),PesquisaGeralIAFVO.Fields.IAF_NUMERO.toString())
			.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()),PesquisaGeralIAFVO.Fields.MATERIAL.toString())
			.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString()),PesquisaGeralIAFVO.Fields.SERVICO.toString())
			.add(Projections.property("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString()),PesquisaGeralIAFVO.Fields.MODALIDADE_LICITACAO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString()),PesquisaGeralIAFVO.Fields.SITUACAO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString()),PesquisaGeralIAFVO.Fields.SER_MATRICULA_GESTOR.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString()),PesquisaGeralIAFVO.Fields.SER_VIN_CODIGO_GESTOR.toString())
			.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()),PesquisaGeralIAFVO.Fields.UNIDADE_CODIGO.toString())
			.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()),PesquisaGeralIAFVO.Fields.QUANTIDADE_SOLICITADA.toString())
			.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()),PesquisaGeralIAFVO.Fields.QUANTIDADE_RECEBIDA.toString())
			.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()),PesquisaGeralIAFVO.Fields.VALOR_UNITARIO.toString());
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		obterCriteriaFiltrosBasicosIAFParte1(filtro, criteria);
		obterCriteriaFiltrosBasicosAFParte2(filtro, criteria); // Método reaproveitado
		if (filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals("")) {
			obterCriteriaTermoLivreIAF(criteria, filtro);
		}
		obterCriteriaPesquisaAvancadaIAFParte1(criteria, filtro);
		obterCriteriaPesquisaAvancadaAFParte2(criteria, filtro); // Método reaproveitado
		if (ordenar.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.desc("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())).addOrder(
					Order.asc("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())).addOrder(
							Order.asc("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString())).addOrder(
									Order.asc("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaGeralIAFVO.class));
		return criteria;
	}
	
	private void obterCriteriaFiltrosBasicosIAFParte1(FiltroPesquisaGeralAFVO filtro, DetachedCriteria criteria) {
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		if (filtro.getSequencia() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString(), filtro.getSequencia()));
		}
		if (filtro.getNumeroAFP() != null) {
			criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.SCO_AF_PEDIDOS.toString(), "AFP");
			criteria.add(Restrictions.eq("AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), filtro.getNumeroAFP()));
		}
		if(filtro.getItem() != null) {
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), filtro.getItem()));
		}
		if (filtro.getModalidadeCompra() != null) {
			criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), filtro.getModalidadeCompra().getCodigo()));
		}
		if (filtro.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString(),filtro.getServidorGestor().getId().getMatricula()));
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString(),filtro.getServidorGestor().getId().getVinCodigo()));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
	}
	
	private void obterCriteriaTermoLivreIAF(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		Criterion slcDescricaoEq = Restrictions.like("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion slsDescricaoEq = Restrictions.like("SLS." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion matNomeEq = Restrictions.like("MAT." + ScoMaterial.Fields.NOME.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion matDescricaoEq = Restrictions.like("MAT." + ScoMaterial.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion srvNomeEq = Restrictions.like("SRV." + ScoServico.Fields.NOME.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion srvDescricaoEq = Restrictions.like("SRV." + ScoServico.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		Criterion lctDescricaoEq = Restrictions.like("LCT." + ScoLicitacao.Fields.DESCRICAO.toString(), filtro.getTermoLivre(), MatchMode.ANYWHERE);
		criteria.add(Restrictions.or(slcDescricaoEq, Restrictions.or(slsDescricaoEq, Restrictions.or(matNomeEq,Restrictions.or(matDescricaoEq, Restrictions.or(srvNomeEq, Restrictions.or(srvDescricaoEq, lctDescricaoEq)))))));
	}
	
	private void obterCriteriaPesquisaAvancadaIAFParte1(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		if (filtro.getMaterial() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), filtro.getMaterial().getCodigo()));
		}
		if (filtro.getGrupoMaterial() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
		}
		if (filtro.getServico() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString(), filtro.getServico().getCodigo()));
		}
		if (filtro.getGrupoServico() != null) {
			criteria.add(Restrictions.eq("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtro.getGrupoServico().getCodigo()));
		}
		if (filtro.getCentroCustoSolicitante() != null) {
			Criterion slsCodigoSolicitanteEq = Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO.toString(),filtro.getCentroCustoSolicitante().getCodigo());
			Criterion slcCodigoSolicitanteEq = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(),filtro.getCentroCustoSolicitante().getCodigo());
			criteria.add(Restrictions.or(slsCodigoSolicitanteEq, slcCodigoSolicitanteEq));
		}
		if (filtro.getCentroCustoAplicacao() != null) {
			Criterion slsCodigoAplicacaoEq = Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString(),filtro.getCentroCustoAplicacao().getCodigo());
			Criterion slcCodigoAplicacaoEq = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(),filtro.getCentroCustoAplicacao().getCodigo());
			criteria.add(Restrictions.or(slsCodigoAplicacaoEq, slcCodigoAplicacaoEq));
		}
	}
	
	public List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(Integer firstResult,Integer maxResults, String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalPAF(filtro, true);
		if (firstResult != null) {
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(criteria);
	}
	
	public Long listarPedidosAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalPAF(filtro, false);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPrincipalPAF(FiltroPesquisaGeralAFVO filtro, Boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class, "AFP");
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AF", Criteria.INNER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),PesquisaGeralPAFVO.Fields.PFR_LCT_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()),PesquisaGeralPAFVO.Fields.PFR_FRN_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),PesquisaGeralPAFVO.Fields.NRO_COMPLEMENTO.toString())
			.add(Projections.property("AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()),PesquisaGeralPAFVO.Fields.AFP_NUMERO.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString()),PesquisaGeralPAFVO.Fields.SER_MATRICULA_GESTOR.toString())
			.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString()),PesquisaGeralPAFVO.Fields.SER_VIN_CODIGO_GESTOR.toString())
			.add(Projections.property("AFP." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()),PesquisaGeralPAFVO.Fields.DT_ENVIO_FORNECEDOR.toString());
		criteria.setProjection(projection);
		obterCriteriaFiltrosBasicosPAFParte1(filtro, criteria);
		obterCriteriaFiltrosBasicosAFParte2(filtro, criteria); // Método reaproveitado
		if (filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals("")) {
			obterCriteriaTermoLivreAF(criteria, filtro); // Método reaproveitado
		}
		obterCriteriaPesquisaAvancadaAFParte1(criteria, filtro); // Método reaproveitado
		obterCriteriaPesquisaAvancadaAFParte2(criteria, filtro); // Método reaproveitado
		
		if (ordenar.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.desc("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())).addOrder(Order.asc("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())).addOrder(Order.asc("AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaGeralPAFVO.class));
		return criteria;
	}
	
	private void obterCriteriaFiltrosBasicosPAFParte1(FiltroPesquisaGeralAFVO filtro, DetachedCriteria criteria) {
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		if (filtro.getSequencia() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString(), filtro.getSequencia()));
		}
		if (filtro.getNumeroAFP() != null) {
			criteria.add(Restrictions.eq("AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString(), filtro.getNumeroAFP()));
		}
		if(filtro.getItem() != null) {
			criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF");
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), filtro.getItem()));
		}
		if (filtro.getModalidadeCompra() != null) {
			criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);
			criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), filtro.getModalidadeCompra().getCodigo()));
		}
		if (filtro.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString(),filtro.getServidorGestor().getId().getMatricula()));
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString(),filtro.getServidorGestor().getId().getVinCodigo()));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
	}
	
	public List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(Integer firstResult,Integer maxResults, String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalPIAF(filtro, true);
		if (firstResult != null) {
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(criteria);
	}
	
	public Long listarParcelasItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		DetachedCriteria criteria = obterCriteriaPrincipalPIAF(filtro, false);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPrincipalPIAF(FiltroPesquisaGeralAFVO filtro, Boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", Criteria.INNER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", Criteria.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", Criteria.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", Criteria.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
		if ((filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals(""))|| filtro.getGrupoMaterial() != null) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		}
		if ((filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals(""))|| filtro.getGrupoServico() != null) {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.LEFT_JOIN);
		}
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),PesquisaGeralPIAFVO.Fields.PFR_LCT_NUMERO.toString())
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()),PesquisaGeralPIAFVO.Fields.PFR_FRN_NUMERO.toString())
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),PesquisaGeralPIAFVO.Fields.NRO_COMPLEMENTO.toString())
		.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()),PesquisaGeralPIAFVO.Fields.IAF_NUMERO.toString())
		.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()),PesquisaGeralPIAFVO.Fields.PARCELA.toString())
		.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()),PesquisaGeralPIAFVO.Fields.MATERIAL.toString())
		.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString()),PesquisaGeralPIAFVO.Fields.SERVICO.toString())
		.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()),PesquisaGeralPIAFVO.Fields.UNIDADE_CODIGO.toString())
		.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()),PesquisaGeralPIAFVO.Fields.QTDE.toString())
		.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()),PesquisaGeralPIAFVO.Fields.QTDE_ENTREGUE.toString())
		.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()),PesquisaGeralPIAFVO.Fields.VALOR_EFETIVADO.toString())
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString()),PesquisaGeralPIAFVO.Fields.SER_MATRICULA_GESTOR.toString())
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString()),PesquisaGeralPIAFVO.Fields.SER_VIN_CODIGO_GESTOR.toString())
		.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString()),PesquisaGeralPIAFVO.Fields.SITUACAO.toString())
		.add(Projections.property("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString()),PesquisaGeralPIAFVO.Fields.MODALIDADE_LICITACAO.toString());
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		obterCriteriaFiltrosBasicosPIAFParte1(filtro, criteria);
		obterCriteriaFiltrosBasicosAFParte2(filtro, criteria); // Método reaproveitado
		if (filtro.getTermoLivre() != null && !filtro.getTermoLivre().equals("")) {
			obterCriteriaTermoLivreIAF(criteria, filtro); // Método reaproveitado
		}
		obterCriteriaPesquisaAvancadaIAFParte1(criteria, filtro); // Método reaproveitado
		obterCriteriaPesquisaAvancadaPIAFParte2(criteria, filtro);
		if (ordenar.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.desc("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())).addOrder(Order.asc("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())).addOrder(Order.asc("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString())).addOrder(Order.asc("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaGeralPIAFVO.class));
		return criteria;
	}
	
	private void obterCriteriaFiltrosBasicosPIAFParte1(FiltroPesquisaGeralAFVO filtro, DetachedCriteria criteria) {
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		if (filtro.getSequencia() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString(), filtro.getSequencia()));
		}
		if (filtro.getNumeroAFP() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), filtro.getNumeroAFP()));
		}
		if(filtro.getItem() != null) {
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), filtro.getItem()));
		}
		if (filtro.getModalidadeCompra() != null) {
			criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(), filtro.getModalidadeCompra().getCodigo()));
		}
		if (filtro.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString(),filtro.getServidorGestor().getId().getMatricula()));
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString(),filtro.getServidorGestor().getId().getVinCodigo()));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
	}
	
	private void obterCriteriaPesquisaAvancadaPIAFParte2(DetachedCriteria criteria, FiltroPesquisaGeralAFVO filtro) {
		if (filtro.getVerbaGestao() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.VBG_SEQ.toString(),filtro.getVerbaGestao().getSeq()));
		}
		if (filtro.getGrupoNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString(),filtro.getGrupoNaturezaDespesa().getCodigo()));
		}
		if (filtro.getNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString(),filtro.getNaturezaDespesa().getId().getCodigo()));
		}
		if (filtro.getNumeroContrato() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtro.getNumeroContrato()));
		}
		if (filtro.getEntregaAtrasada() != null && filtro.getEntregaAtrasada().equals(Boolean.TRUE)) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
			if (filtro.getDiasVencimento() != null) {
				Date dataCalculada = DateUtil.adicionaDiasFracao(new Date(), filtro.getDiasVencimento().floatValue());
				criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataCalculada));
			} else {
				criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
			}
			if (filtro.getNumeroAFP() != null) {
				criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), filtro.getNumeroAFP()));
			}
		}
	}
	
	public Long listarProgEntregaFornecedorCount(AcessoFornProgEntregaFiltrosVO filtro){
		Query query = getProgEntregaFornecedorHQL(filtro, true);
		return Long.parseLong(query.uniqueResult().toString());
	}
	
	public List<AFPFornecedoresVO> listarProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro, Integer firstResult, Integer maxResult){
		Query query = getProgEntregaFornecedorHQL(filtro, false);
		query.setResultTransformer(Transformers.aliasToBean(AFPFornecedoresVO.class));
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.list();
	}
	
	public Query getProgEntregaFornecedorHQL(AcessoFornProgEntregaFiltrosVO filtro, Boolean isCount){
		StringBuilder hql = new StringBuilder(200);
		if(isCount) {
			hql.append("SELECT COUNT(*) ");
		}else{
			hql.append("SELECT ").append("CASE ").append("WHEN ( SELECT COUNT(*) ").append("FROM ").append(ScoCumXProgrEntrega.class.getName()).append(" CPE, ").append(ScoProgEntregaItemAutorizacaoFornecimento.class.getName()).append(" PEA ")
			.append("WHERE ").append("PEA.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()).append(' ').append("= PAF.").append(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_AFN_NUMERO.toString()).append(' ')
			.append("AND PEA.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()).append(' ').append("= PAF.").append(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_NUMERO.toString()).append(' ')
			.append("AND CPE.").append(ScoCumXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO_IAF_AFN_NUMERO.toString()).append(' ').append("= PEA.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()).append(' ')
			.append("AND CPE.").append(ScoCumXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO_IAF_NUMERO.toString()).append(' ').append("= PEA.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()).append(' ')
			.append("AND CPE.").append(ScoCumXProgrEntrega.Fields.SCO_PROG_ENTREGA_ITEM_AUTORIZACAO_FORNECIMENTO_PEA_PARCELA.toString()).append(' ').append("= PEA.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()).append(' ')
			.append(") > 0 ").append("THEN 'CUM' ").append("ELSE ' '").append("END AS ").append(AFPFornecedoresVO.Fields.ORIGEM.toString()).append(", ").append(" AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.NUMERO_AF.toString()).append(", ")
		    .append(" AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.COMPLEMENTO.toString()).append(", ").append(" AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.NUMERO_AFP.toString()).append(", ")
		    .append(" AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.PUBLICACAO.toString()).append(", ").append(" AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.DATA_PUBLICACAO.toString()).append(", ")
		    .append(" PAF.").append(ScoProgrAcessoFornAfp.Fields.DT_ACESSO.toString()).append(' ').append("AS ").append(AFPFornecedoresVO.Fields.DATA_ACESSO.toString()).append(", ").append("CASE ").append("WHEN PAF.").append(ScoProgrAcessoFornAfp.Fields.TIPO_ACESSO.toString()).append(" = 'P' THEN 'AFP' ")
			.append("WHEN PAF.").append(ScoProgrAcessoFornAfp.Fields.TIPO_ACESSO.toString()).append(" = 'L' THEN 'Lista PAC' ").append("ELSE ' ' ").append("END AS ").append(AFPFornecedoresVO.Fields.ACESSO.toString()).append(", ").append("FRN.").append(ScoFornecedor.Fields.CGC.toString()).append(" || ' - ' || ").append("FRN.").append(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()).append(' ').append(" AS ").append(AFPFornecedoresVO.Fields.FORNECEDOR.toString());
		}
		hql.append(" FROM ").append(ScoAutorizacaoFornecedorPedido.class.getName()).append(" AFE, ").append(ScoAutorizacaoForn.class.getName()).append(" AFN, ").append(ScoFornecedor.class.getName()).append(" FRN, ").append(ScoProgrAcessoFornAfp.class.getName()).append(" PAF ")
		.append(" WHERE AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()).append(' ').append(" = AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(' ').append(" AND FRN.").append(ScoFornecedor.Fields.NUMERO.toString()).append(' ').append(" = AFN.").append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()).append(' ').append(" AND PAF.").append(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_NUMERO.toString()).append(' ')
		.append(" = AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()).append(' ').append(" AND PAF.").append(ScoProgrAcessoFornAfp.Fields.SCO_AUTORIZACAO_FORNECEDOR_PEDIDO_AFE_AFN_NUMERO.toString()).append(' ')
		.append(" = AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()).append(' ');
		hql = setFilters(filtro, hql);
		Query query = this.createHibernateQuery(hql.toString());
		return setParameters(filtro, query);
	}

	private StringBuilder setFilters(AcessoFornProgEntregaFiltrosVO filtro, StringBuilder hql) {
		if(filtro.getNumeroAF() != null){
			hql.append("AND AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(" = :numeroAF ");
		}
		if(filtro.getComplemento() != null)	{
			hql.append("AND AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(" = :complemento ");
		}
		if(filtro.getNumeroAFP() != null) {
			hql.append("AND AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()).append(" = :numeroAFP ");
		}
		if(filtro.getPublicacao() != null) {
			hql.append("AND AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO.toString()).append(" = :publicacao ");
		}
		if(isPostgreSQL()){
			hql.append(getFiltrosDataParaPostgres(filtro));
		}else if(isOracle()){
			hql.append(getFiltrosDataParaOracle(filtro));
		}
		
		if(filtro.getFornecedor() != null) {
			hql.append("AND FRN.").append(ScoFornecedor.Fields.NUMERO.toString()).append(" = :fornecedor ");
		}
		return hql;
	}

	private StringBuilder getFiltrosDataParaPostgres(AcessoFornProgEntregaFiltrosVO filtro) {
		StringBuilder hql = new StringBuilder(200);
		//Independente se existe informação na data final, deve utilizar sempre com data inicial e final conforme método setParameters
		if(filtro.getDataPublicacaoInicial() != null){
			hql.append(" AND DATE(AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO.toString()).append(") ").append(" BETWEEN DATE( :dataPublicacaoInicial ) AND DATE( :dataPublicacaoFinal ) ");
		}
		if(filtro.getDataAcessoInicial() != null){
			hql.append("AND DATE(PAF.").append(ScoProgrAcessoFornAfp.Fields.DT_ACESSO.toString()).append(") ").append(" BETWEEN DATE( :dataAcessoInicial ) AND DATE( :dataAcessoFinal )");
		}
		return hql;
	}

	private StringBuilder getFiltrosDataParaOracle(AcessoFornProgEntregaFiltrosVO filtro) {
		StringBuilder hql = new StringBuilder(200);
		//Independente se existe informação na data final, deve utilizar sempre com data inicial e final conforme método setParameters
		if(filtro.getDataPublicacaoInicial() != null){
			hql.append(" AND AFE.").append(ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO.toString()).append(" BETWEEN :dataPublicacaoInicial AND :dataPublicacaoFinal ");
		}
		if(filtro.getDataAcessoInicial() != null){
			hql.append("AND PAF.").append(ScoProgrAcessoFornAfp.Fields.DT_ACESSO.toString()).append(" BETWEEN :dataAcessoInicial AND :dataAcessoFinal ");
		}
		return hql;
	}

	private Query setParameters(AcessoFornProgEntregaFiltrosVO filtro, Query query) {
		if(filtro.getNumeroAF() != null){
			query.setParameter("numeroAF", filtro.getNumeroAF());
		}
		if(filtro.getComplemento() != null)	{
			query.setParameter("complemento", filtro.getComplemento());
		}
		if(filtro.getNumeroAFP() != null) {
			query.setParameter("numeroAFP", filtro.getNumeroAFP());
		}
		if(filtro.getPublicacao() != null) {
			query.setParameter("publicacao", filtro.getPublicacao());
		}
		
		if(filtro.getDataPublicacaoInicial() != null && filtro.getDataPublicacaoFinal() != null){
			query.setParameter("dataPublicacaoInicial", DateUtil.obterDataComHoraInical(filtro.getDataPublicacaoInicial()));
			query.setParameter("dataPublicacaoFinal", DateUtil.obterDataComHoraFinal(filtro.getDataPublicacaoFinal()));
		}else if(filtro.getDataPublicacaoInicial() != null) {
			//Quando selecionou somente a data inicial, utilizar a mesma data na final alterando somente a hora final
			query.setParameter("dataPublicacaoInicial", DateUtil.obterDataComHoraInical(filtro.getDataPublicacaoInicial()));
			query.setParameter("dataPublicacaoFinal", DateUtil.obterDataComHoraFinal(filtro.getDataPublicacaoInicial()));
		}
		if(filtro.getDataAcessoInicial() != null && filtro.getDataAcessoFinal() != null){
			query.setParameter("dataAcessoInicial", DateUtil.obterDataComHoraInical(filtro.getDataAcessoInicial()));
			query.setParameter("dataAcessoFinal", DateUtil.obterDataComHoraFinal(filtro.getDataAcessoFinal()));
		}else if(filtro.getDataAcessoInicial() != null) {
			//Quando selecionou somente a data inicial, utilizar a mesma data na final alterando somente a hora final
			query.setParameter("dataAcessoInicial", DateUtil.obterDataComHoraInical(filtro.getDataAcessoInicial()));
			query.setParameter("dataAcessoFinal", DateUtil.obterDataComHoraFinal(filtro.getDataAcessoInicial()));
		}
		if(filtro.getFornecedor() != null) {
			query.setParameter("fornecedor", filtro.getFornecedor().getNumeroFornecedor());
		}
		return query;
	}

	public Long verificarPendenciasFornecedor(Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class);
		criteria.createAlias(ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "scoAutorizacaoForn");
		criteria.createAlias("scoAutorizacaoForn.propostaFornecedor", "propForn");
		criteria.add(Restrictions.eq("propForn.id.frnNumero", numeroFornecedor));
		criteria.add(Restrictions.eq(ScoAutorizacaoFornecedorPedido.Fields.IND_PUBLICADO.toString(), DominioAfpPublicado.S));
		return executeCriteriaCount(criteria);
	}
	
	public ScoDataEnvioFornecedorVO consultarRegistroComDetalhesDasParcelas(ParcelaAfPendenteEntregaVO vo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class);
		criteria.createAlias(ScoAutorizacaoFornecedorPedido.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "PEA");
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), vo.getNumeroAfeAfn()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), vo.getNumeroAfp()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), vo.getNumeroIaf()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), vo.getParcela()));
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()), ScoDataEnvioFornecedorVO.Fields.DATA_ENVIO_FORNECEDOR.toString())
		.add(Projections.property(ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString()), ScoDataEnvioFornecedorVO.Fields.DATA_ENVIO.toString())
		.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ASSINATURA.toString()), ScoDataEnvioFornecedorVO.Fields.DATA_EMPENHO.toString());
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ScoDataEnvioFornecedorVO.class));		
		return (ScoDataEnvioFornecedorVO) executeCriteriaUniqueResult(criteria);
	}	
	
	public Date buscarDataMaisAntigaEnvioFornecedor(ProgrGeralEntregaAFVO parcelaAF) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornecedorPedido.class, "AFP");
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_PROGR_ENTREGA_ITENS_AFS.toString(), "PEA");
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), parcelaAF.getIafAfnNumero()));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), parcelaAF.getIafNumero()));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), parcelaAF.getParcela()));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString(), parcelaAF.getProgEntregaItemAFSeq()));
		criteria.setProjection(Projections.projectionList().add(Projections.min("AFP." + ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR.toString())));		
		return (Date)executeCriteriaUniqueResult(criteria);
	}	
	
	
}



