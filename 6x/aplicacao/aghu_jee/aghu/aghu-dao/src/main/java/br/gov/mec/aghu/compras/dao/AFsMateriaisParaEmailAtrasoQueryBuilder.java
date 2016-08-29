package br.gov.mec.aghu.compras.dao;


import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoItemFornecedorVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AFsMateriaisParaEmailAtrasoQueryBuilder extends QueryBuilder<Query> {

	private static final long serialVersionUID = -405783921098564193L;
	private Integer numeroAutorizacaoFornecimento; 
	private Integer numeroPedidoAF;
	

	private String makeQuery() {
		StringBuilder hqlMateriaisEmailAtraso = new StringBuilder(" select ")
		.append("afn.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO).append(" as propostaFornecedorLicitacaoNumero, \n")
		.append("afn.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO).append(" as numeroComplemento, \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO).append(" as afp, \n")
		.append("fsc2.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO).append(" as item, \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA).append(" as parc, \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA).append(" as prevEntrega, \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE).append(" as qtd, \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE).append(" as qtdEntregue, \n")
		.append("iaf.").append(ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO_FORN).append(" as unid, \n")
		.append("mat.").append(ScoMaterial.Fields.CODIGO).append(" as codigoMaterial, \n")
		.append("mat.").append(ScoMaterial.Fields.NOME).append(" as material, \n")
		.append("afn.").append(ScoAutorizacaoForn.Fields.SERVIDOR).append(" as servidor, \n")
		.append("afn.").append(ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR).append(" as servidorGestor \n")
		
		.append(" from \n ")

		.append(ScoMaterial.class.getSimpleName()).append(" mat, \n")
		.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" slc, \n")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" fsc2, \n")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" fsc1, \n")
		.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(" iaf, \n")
		.append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName()).append(" pea, \n")
		.append(ScoAutorizacaoForn.class.getSimpleName()).append(" afn \n")
		
		.append(" where \n")
		
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString())
		.append(" = :numeroAutorizacaoFornecimento ").append(" and \n")
		.append("pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString())
		.append(" = :numeroPedidoAF ").append(" and \n")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()).append("")
		.append(" = afn.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" and \n")
		
		.append(" coalesce(pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()).append(", 0) ")
		.append(" < ")
		.append(" coalesce(pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()).append(", 0) ")
		.append(" and \n ")

		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()).append(" || '' = 'N' and \n ")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()).append("<> 0 and \n ")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString()).append(" || '' = 'N' and \n ")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString()).append(" || '' = 'S' and \n ")
		.append(" iaf.").append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()).append("")
		.append(" = pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()).append(" and \n")
		.append(" iaf.").append(ScoItemAutorizacaoForn.Fields.NUMERO.toString()).append("")
		.append(" = pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()).append(" and \n")
		.append(" fsc1.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append("")
		.append(" = pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()).append(" and \n")
		.append(" fsc1.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()).append("")
		.append(" = pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()).append(" and \n")
		.append(" fsc2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append("")
		.append(" = fsc1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" and \n")
		.append(" fsc2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append("").append(" is not null and \n")
		.append(" fsc2.").append(ScoFaseSolicitacao.Fields.LICITACAO_NRO.toString()).append("").append(" is not null and \n")
		.append(" slc.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append("")
		.append(" = fsc2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" and \n")
		.append(" mat.").append(ScoMaterial.Fields.CODIGO.toString()).append("")
		.append(" = slc.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(" and \n")
		.append(" fsc1.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" || '' = 'N' and \n ")
		.append(" fsc2.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" || '' = 'N' ")
		.append(" order by ")
		.append(" afn.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(", \n")
		.append(" afn.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(", \n")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()).append(", \n")
		.append(" fsc2.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(", \n")
		.append(" pea.").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString());
		return hqlMateriaisEmailAtraso.toString();
	}
	
	@Override
	protected Query createProduct() {
		final String hql = this.makeQuery();
		
	    Query query = this.createHibernateQuery(hql);
		return query;
	}

	@Override
	protected void doBuild(Query query) {
	    query.setParameter("numeroAutorizacaoFornecimento", numeroAutorizacaoFornecimento);
		query.setParameter("numeroPedidoAF", numeroPedidoAF);
		query.setResultTransformer(Transformers.aliasToBean(AutorizacaoFornecimentoItemFornecedorVO.class));
	}

	public Query build(Integer numeroAutorizacaoFornecimento, Integer numeroPedidoAF) {
		
		this.numeroAutorizacaoFornecimento = numeroAutorizacaoFornecimento;
		this.numeroPedidoAF = numeroPedidoAF;

		return super.build();
	}

}
