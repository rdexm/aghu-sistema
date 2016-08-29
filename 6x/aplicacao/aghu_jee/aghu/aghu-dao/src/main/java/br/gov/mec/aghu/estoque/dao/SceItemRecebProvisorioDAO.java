package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.estoque.vo.GrupoMaterialNumeroSolicitacaoVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioRelVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO.NotaRecebimentoProvisorio;
import br.gov.mec.aghu.estoque.vo.SceItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.ValidaUnidadeMaterialVO;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.patrimonio.dao.RegistrarAceiteTecnicoQueryBuilder;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.QuantidadeDevolucaoBemPermanenteVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SceItemRecebProvisorioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemRecebProvisorio> {
	private static final String IRP = "IRP", NRP = "NRP", IAF = "IAF", PEA = "PEA";

	private static final long serialVersionUID = -9117628270334276966L;


	public List<SceItemRecebProvisorio> pesquisaItemRecebProvisorio(Integer nrpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class,"IRP");
		
		criteria.createCriteria("IRP."+SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq));
		
		return executeCriteria(criteria);
	}
	

	public List<ItemRecebimentoProvisorioVO> pesquisarItensNotaRecebimentoProvisorio(final Integer nrpSeq, final Boolean indExclusao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "IPE", Criteria.LEFT_JOIN);
		criteria.createAlias("IPE." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", Criteria.LEFT_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MCM", Criteria.LEFT_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "NC", Criteria.LEFT_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.LEFT_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC1", Criteria.LEFT_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.FASES_SOLICITACAO.toString(), "FSC2", Criteria.LEFT_JOIN);

		if (indExclusao != null) {
			criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao));
			criteria.add(Restrictions.or(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao),
					Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao)));
		}
		
		criteria.add(Restrictions.or(Restrictions.isNotNull("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()),
								Restrictions.isNotNull("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())));
		if (nrpSeq != null) {
			criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq));
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), ItemRecebimentoProvisorioVO.Fields.NRP_SEQ.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), ItemRecebimentoProvisorioVO.Fields.NRO_ITEM.toString())
				.add(Projections.property("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ItemRecebimentoProvisorioVO.Fields.ITL_NUMERO_MATERIAL.toString())
				.add(Projections.property("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ItemRecebimentoProvisorioVO.Fields.ITL_NUMERO_SERVICO.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.PEA_PARCELA.toString()),
						ItemRecebimentoProvisorioVO.Fields.PEA_PARCELA.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()),
						ItemRecebimentoProvisorioVO.Fields.QUANTIDADE.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.VALOR.toString()), ItemRecebimentoProvisorioVO.Fields.VALOR.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.PERC_VAR_PRECO.toString()),
						ItemRecebimentoProvisorioVO.Fields.PERC_VAR_PRECO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.PERC_VAR_PRECO.toString()),
						ItemRecebimentoProvisorioVO.Fields.PERC_VAR_PRECO_INICIAL.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()),
						ItemRecebimentoProvisorioVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_EFETIVADO.toString()),
						ItemRecebimentoProvisorioVO.Fields.VALOR_EFETIVADO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()),
						ItemRecebimentoProvisorioVO.Fields.AFN_NUMERO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ItemRecebimentoProvisorioVO.Fields.NUMERO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemRecebimentoProvisorioVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemRecebimentoProvisorioVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()),
						ItemRecebimentoProvisorioVO.Fields.CODIGO_UNIDADE_MEDIDA_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()),
						ItemRecebimentoProvisorioVO.Fields.SEQ_ALMOXARIFADO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ItemRecebimentoProvisorioVO.Fields.CODIGO_SERVICO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), ItemRecebimentoProvisorioVO.Fields.NOME_SERVICO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioVO.Fields.DESCRICAO_SERVICO.toString())
				.add(Projections.property("MCM." + ScoMarcaComercial.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioVO.Fields.DESCRICAO_MARCA.toString())
				.add(Projections.property("NC." + ScoNomeComercial.Fields.NOME.toString()), ItemRecebimentoProvisorioVO.Fields.NOME_COMERCIAL.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemRecebimentoProvisorioVO.class));
		
		criteria.addOrder(Order.asc("FSC1." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		criteria.addOrder(Order.asc("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		criteria.addOrder(Order.asc("IRP." + SceItemRecebProvisorio.Fields.PEA_PARCELA.toString()));
		return executeCriteria(criteria);
	}

	public SceItemRecebProvisorio pesquisaQtdeEntregue(Integer iafAfnNumero, Integer iafNumero, Integer nrpSeq) {
		
		SceItemRecebProvisorio retorno = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class,"IRP");
		criteria.createCriteria("IRP."+SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP", Criteria.INNER_JOIN);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("IRP."+SceItemRecebProvisorio.Fields.NRO_ITEM.toString() ));
		p.add(Projections.groupProperty("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString() ));
		p.add(Projections.sum("IRP."+SceItemRecebProvisorio.Fields.QUANTIDADE.toString()));
		p.add(Projections.sum("IRP."+SceItemRecebProvisorio.Fields.VALOR.toString()));
		
		criteria.setProjection(p);

		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString(), iafAfnNumero));
		criteria.add(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString(), iafNumero));
		
		criteria.add(Restrictions.not(Restrictions.eq("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), nrpSeq)));
		criteria.add(Restrictions.eq("NRP."+SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		
		List<Object[]> list = executeCriteria(criteria);
		
		if(list!= null && !list.isEmpty()){
			
			Object[] objects = list.get(0);
			
			retorno = new SceItemRecebProvisorio();
			SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
		//	id.setNroItem((Integer)objects[0]);
			id.setNrpSeq((Integer)objects[1]);
			
			retorno.setQuantidade((Integer) objects[2]);
			retorno.setValor((Double) objects[3]);
			
			retorno.setId(id);
		}
		
		
		return retorno;
	}

	/**
	 * Obtem o valor total de uma nota fiscal através da soma do valor dos seus
	 * itens.
	 * 
	 * @param seq
	 * @return
	 */
	public Double obterValorTotalNotaFiscal(final Integer seq) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), seq));
		criteria.setProjection(Projections.sum(SceItemRecebProvisorio.Fields.VALOR.toString()));
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem o valor total dos itens vinculados a um documento fiscal
	 * @param dfeSeq
	 * @return
	 */
	public Double obterValorTotalItemNotaFiscal(Integer dfeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class);
		criteria.createAlias(SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "nrp");
		criteria.add(Restrictions.eq("nrp."+SceNotaRecebProvisorio.Fields.DFE_SEQ.toString(), dfeSeq));
		criteria.add(Restrictions.eq("nrp."+SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), false));
		criteria.setProjection(Projections.sum(SceItemRecebProvisorio.Fields.VALOR.toString()));
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ValidaUnidadeMaterialVO> pesquisarUnidadeMaterialAfEstoque(SceNotaRecebProvisorio notaRecebimentoProvisorio, Integer parametroFatorConversao, Integer parametroFornecedorPadrao) {
		
		StringBuilder hql = new StringBuilder(500);
		hql.append(" SELECT new br.gov.mec.aghu.estoque.vo.ValidaUnidadeMaterialVO (")
		.append(" 	fsc2.")
		.append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
		.append(", ")
		.append(" 	mat.")
		.append(ScoMaterial.Fields.CODIGO.toString())
		.append(", ")
		.append(" 	iaf.")
		.append(ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString())
		.append(", ")
		.append(" 	eal.")
		.append(SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString())
		.append(" ) ")
		.append(" from  ").append(SceItemRecebProvisorio.class.getName()).append(" irp, ")
		.append(ScoItemAutorizacaoForn.class.getName()).append(" iaf, ")
		.append(ScoFaseSolicitacao.class.getName()).append(" fsc1, ")
		.append(ScoFaseSolicitacao.class.getName()).append(" fsc2, ")
		.append(ScoSolicitacaoDeCompra.class.getName()).append(" slc, ")
		.append(ScoMaterial.class.getName()).append(" mat, ")
		.append(SceEstoqueAlmoxarifado.class.getName()).append(" eal ")
		.append(" where irp.")
		.append(SceItemRecebProvisorio.Fields.NRP_SEQ.toString())
		.append(" = :nrpSeq ")
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString())
		.append(" = :fatorConversao ")
		.append(" and fsc2.")
		.append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" IS NULL ")
		.append(" and eal.")
		.append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString())
		.append(" = :fornecedorPadrao ")
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString())
		.append(" <> ")
		.append(" eal.")
		.append(SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString())
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
		.append(" = ")
		.append(" irp.")
		.append(SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString())
		.append(" and iaf.")
		.append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		.append(" = ")
		.append(" irp.")
		.append(SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString())
		
		
		.append(" and fsc1.")
		.append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" = ")
		.append(" iaf.")
		.append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
		
		.append(" and fsc1.")
		.append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
		.append(" = ")
		.append(" iaf.")
		.append(ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		
		.append(" and fsc2.")
		.append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(" = ")
		.append(" fsc1.")
		.append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		
		.append(" and slc.")
		.append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
		.append(" = ")
		.append(" fsc2.")
		.append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		
		.append(" and mat.")
		.append(ScoMaterial.Fields.CODIGO.toString())
		.append(" = ")
		.append(" slc.")
		.append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
		
		
		.append(" and eal.")
		.append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString())
		.append(" = ")
		.append(" mat.")
		.append(ScoMaterial.Fields.CODIGO.toString())
		
		.append(" and eal.")
		.append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString())
		.append(" = ")
		.append(" mat.")
		.append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString());
		
		
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("nrpSeq", notaRecebimentoProvisorio.getSeq());
		query.setParameter("fatorConversao", parametroFatorConversao);
		query.setParameter("fornecedorPadrao", parametroFornecedorPadrao);
		return query.list();
	}

	
	
	
	
	
	
	
	public List<ItemRecebimentoProvisorioRelVO> pesquisarRelatorioItemRecProv(final List<Integer> listaNrpSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");	
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SCO_AF_PEDIDO.toString(), "AF_PEDIDO");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UMD", Criteria.LEFT_JOIN);
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILIC", Criteria.LEFT_JOIN);
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString(), "MCM", Criteria.LEFT_JOIN);
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP_FRN");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", Criteria.LEFT_JOIN);
		
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", Criteria.LEFT_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", Criteria.LEFT_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", Criteria.LEFT_JOIN);
	
		criteria.add(Restrictions.in("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), listaNrpSeq));
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()), ItemRecebimentoProvisorioRelVO.Fields.SEQ_NOTA_RECEB_PROV.toString())
				.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.DFE_SEQ.toString()), ItemRecebimentoProvisorioRelVO.Fields.DFE_SEQ_NOTA_RECEB_PROV.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), ItemRecebimentoProvisorioRelVO.Fields.NUMERO_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), ItemRecebimentoProvisorioRelVO.Fields.CGC_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), ItemRecebimentoProvisorioRelVO.Fields.CPF_FORNECEDOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ItemRecebimentoProvisorioRelVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString())
				.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()), ItemRecebimentoProvisorioRelVO.Fields.DT_RECEBIMENTO_NOTA_REB_PROV.toString())
				.add(Projections.property("PROP_FRN." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), ItemRecebimentoProvisorioRelVO.Fields.LCT_NUMERO_AF.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), ItemRecebimentoProvisorioRelVO.Fields.NUMERO_COMPLEMENTO_AF.toString())
				.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ItemRecebimentoProvisorioRelVO.Fields.MATRICULA_SERVIDOR.toString())
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ItemRecebimentoProvisorioRelVO.Fields.NOME_SERV_PESSOA_FISICA.toString())
				
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), ItemRecebimentoProvisorioRelVO.Fields.NUMERO_DOC_FISCAL_ENTRADA.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.SERIE.toString()), ItemRecebimentoProvisorioRelVO.Fields.SERIE_DOC_FISCAL_ENTRADA.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.TIPO.toString()), ItemRecebimentoProvisorioRelVO.Fields.TIPO_DOC_FISCAL_ENTRADA.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.VALOR_TOTAL_NF.toString()), ItemRecebimentoProvisorioRelVO.Fields.VALOR_TOTAL_NF_DOC_FISCAL_ENTRADA.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()), ItemRecebimentoProvisorioRelVO.Fields.DT_EMIS_DOC_FISCAL_ENTRADA.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_ENTRADA.toString()), ItemRecebimentoProvisorioRelVO.Fields.DT_ENTR_DOC_FISCAL_ENTRADA.toString())
				
				.add(Projections.property("ILIC." + ScoItemLicitacao.Fields.NUMERO.toString()), ItemRecebimentoProvisorioRelVO.Fields.NUMERO_ITEM_LIC_IPF.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.PEA_PARCELA.toString()), ItemRecebimentoProvisorioRelVO.Fields.PARCELA_PEA_ITEM_NOTA_REC_PROV.toString())
				.add(Projections.property("AF_PEDIDO." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()), ItemRecebimentoProvisorioRelVO.Fields.NUMERO_AF_PEDIDO.toString())
				.add(Projections.property("MCM." + ScoMarcaComercial.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioRelVO.Fields.DESCRICAO_MARCA_IPF.toString())
				.add(Projections.property("UMD." + ScoUnidadeMedida.Fields.CODIGO.toString()), ItemRecebimentoProvisorioRelVO.Fields.CODIGO_UMD_IAF.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()), ItemRecebimentoProvisorioRelVO.Fields.QUANT_ITEM_NOTA_REC_PROV.toString())
				.add(Projections.property("IRP." + SceItemRecebProvisorio.Fields.VALOR.toString()), ItemRecebimentoProvisorioRelVO.Fields.VALOR_ITEM_NOTA_REC_PROV.toString())
				
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemRecebimentoProvisorioRelVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemRecebimentoProvisorioRelVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioRelVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ItemRecebimentoProvisorioRelVO.Fields.CODIGO_SERVICO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), ItemRecebimentoProvisorioRelVO.Fields.NOME_SERVICO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.DESCRICAO.toString()), ItemRecebimentoProvisorioRelVO.Fields.DESCRICAO_SERVICO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemRecebimentoProvisorioRelVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Date obterMaiorDataEstornoRecebimento(Date dataGeracaoRecebimento, ScoProgEntregaItemAutorizacaoFornecimentoId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class);
		criteria.createAlias(SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.createAlias(SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA");
		criteria.add(Restrictions.lt("NRP."+SceNotaRecebProvisorio.Fields.DT_GERACAO.toString(), dataGeracaoRecebimento));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), id.getIafNumero()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), id.getIafAfnNumero()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString(), id.getParcela()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString(), id.getSeq()));
		criteria.setProjection(Projections.max("NRP."+SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a um item de AF.
	 * 
	 * @param itemAf Item de AF
	 * @return Quantidade
	 */
	public Long somarQtdeItensNotaRecebProvisorio(
			ScoItemAutorizacaoForn itemAf) {
		DetachedCriteria criteria = getSomaQtdeItensNotaRecebProvisorioCriteria(true,true);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), PEA);
		
		criteria.add(Restrictions.eq(PEA + "." + 
				ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), itemAf));
		return (Long) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa notas de recebimento provisório associadas a um item de AF.
	 * 
	 * @param itemAf Item de AF
	 * @param maxRps 
	 * @return Notas de Recebimento Provisório
	 */
	public List<NotaRecebimentoProvisorio> pesquisarNotasRecebimentoProvisorio(
			ScoItemAutorizacaoForn itemAf, int maxRps) {
		DetachedCriteria criteria = getPesquisarNotasRecebimentoProvisorioCriteria();
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), PEA);
		
		criteria.add(Restrictions.eq(PEA + "." + 
				ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), itemAf));
		
		return executeCriteria(criteria, 0, maxRps, null, true);
	}

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento provisório associadas a uma parcela.
	 * 
	 * @param parcela Parcela
	 * @param maxRps Limite de notas de RP a serem retornadas.
	 * @return Quantidade
	 */
	public Integer somarQtdeItensNotaRecebProvisorio(
			ScoProgEntregaItemAutorizacaoFornecimento parcela) {
		DetachedCriteria criteria = getSomaQtdeItensNotaRecebProvisorioCriteria(true,true);
		
		criteria.add(Restrictions.eq(criteria.getAlias() + "." + 
				SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), parcela));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa notas de recebimento provisório associadas a uma parcela de item
	 * de AF.
	 * 
	 * @param parcela Parcela de Item de AF
	 * @param maxRps
	 * @return Notas de Recebimento Provisório
	 */
	public List<NotaRecebimentoProvisorio> pesquisarNotasRecebimentoProvisorio(
			ScoProgEntregaItemAutorizacaoFornecimento parcela, int maxRps) {
		DetachedCriteria criteria = getPesquisarNotasRecebimentoProvisorioCriteria();
		
		criteria.add(Restrictions.eq(criteria.getAlias() + "." + 
				SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), parcela));
		
		return executeCriteria(criteria, 0, maxRps, null, true);
	}

	/**
	 * @see SceItemRecebProvisorioDAO#somarQtdeItensNotaRecebProvisorio(SceEstoqueAlmoxarifado, Date)
	 */
	public Long somarQtdeItensNotaRecebProvisorio(
			SceEstoqueAlmoxarifado estoque) {
		return somarQtdeItensNotaRecebProvisorio(estoque, null);
	}

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a um material/almoxarifado.
	 * 
	 * @param estoque Relação material/almoxarifado.
	 * @param competencia Data competência.
	 * @return Quantidade
	 */
	public Long somarQtdeItensNotaRecebProvisorio(
			SceEstoqueAlmoxarifado estoque, Date competencia) {
		DetachedCriteria criteria = getSomaQtdeItensNotaRecebProvisorioCriteria(true,true);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), PEA);
		criteria.createAlias(PEA + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), IAF);
		criteria.add(Subqueries.exists(getMaterialAlmoxarifadoSubqueryCriteria(estoque)));
		
		if (competencia != null) {
			criteria.add(Restrictions.le(
					NRP + "." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString(),
					DateUtil.obterDataFimCompetencia(competencia)));
		}
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa notas de recebimento provisório associadas a um material/almoxarifado.
	 * 
	 * @param estoque Relação material/almoxarifado.
	 * @param maxRps
	 * @return Notas de Recebimento Provisório
	 */
	public List<NotaRecebimentoProvisorio> pesquisarNotasRecebimentoProvisorio(
			SceEstoqueAlmoxarifado estoque, int maxRps) {
		DetachedCriteria criteria = getPesquisarNotasRecebimentoProvisorioCriteria();
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), PEA);
		criteria.createAlias(PEA + "." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), IAF);
		criteria.add(Subqueries.exists(getMaterialAlmoxarifadoSubqueryCriteria(estoque)));
		return executeCriteria(criteria, 0, maxRps, null, true);
	}
	
	private DetachedCriteria getPesquisarNotasRecebimentoProvisorioCriteria() {
		DetachedCriteria criteria = getSomaQtdeItensNotaRecebProvisorioCriteria(false,true);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(NRP + "." + SceNotaRecebProvisorio.Fields.NRP_SEQ), 
						QtdeRpVO.NotaRecebimentoProvisorio.Field.ID.toString())
				.add(Projections.property(NRP + "." + SceNotaRecebProvisorio.Fields.DT_GERACAO), 
						QtdeRpVO.NotaRecebimentoProvisorio.Field.DATA_GERACAO.toString())
				.add(Projections.property(NRP + "." + SceNotaRecebProvisorio.Fields.IND_ESTORNO),
						QtdeRpVO.NotaRecebimentoProvisorio.Field.IND_ESTORNO.toString())
				.add(Projections.property(NRP + "." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA),
						QtdeRpVO.NotaRecebimentoProvisorio.Field.DOCUMENTO_FISCAL_ENTRADA.toString())
				.add(Projections.property("PROP" + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()),
								QtdeRpVO.NotaRecebimentoProvisorio.Field.FORNECEDOR.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QtdeRpVO.NotaRecebimentoProvisorio.class));
		criteria.addOrder(Order.asc(NRP + "." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString()));
		criteria.addOrder(Order.desc(NRP + "." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()));
		
		return criteria;
	}
	
	private DetachedCriteria getSomaQtdeItensNotaRecebProvisorioCriteria(Boolean removerEstornados, Boolean removerConfirmados) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, IRP);
		criteria.createAlias(IRP + "." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), NRP);
		criteria.createAlias(NRP + "." + SceNotaRecebProvisorio.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF" + "." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP" + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		criteria.setProjection(Projections.sum(IRP + "." + SceItemRecebProvisorio.Fields.QUANTIDADE.toString()));
		
		if (removerEstornados) {
			criteria.add(Restrictions.eq(NRP + "." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), false));
		}

		if (removerConfirmados) {
			criteria.add(Restrictions.eq(NRP + "." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), false));
		}

		return criteria;
	}

	private DetachedCriteria getMaterialAlmoxarifadoSubqueryCriteria(
			SceEstoqueAlmoxarifado estoque) {
		final String IAF2 = "IAF2", IPF = "IPF", ITL = "ITL", FSL = "FSL", SLC = "SLC", MAT = "MAT";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, IAF2);
		
		criteria.setProjection(Projections.property(IAF2 + "."
				+ ScoItemPropostaFornecedor.Fields.NUMERO.toString()));
		
		criteria.createAlias(IAF2 + "." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), IPF);
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), ITL);
		criteria.createAlias(ITL + "." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), FSL);
		criteria.createAlias(FSL + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), SLC);
		criteria.createAlias(SLC + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), MAT);
		 
		criteria.add(Restrictions.eqProperty(
				IAF2 + "." + ScoItemPropostaFornecedor.Fields.ID.toString(), 
				IAF + "." + ScoItemPropostaFornecedor.Fields.ID.toString()));
		
		criteria.add(Restrictions.eq(
				SLC + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), 
				estoque.getMaterial()));
		
		criteria.add(Restrictions.eq(
				MAT + "." + ScoMaterial.Fields.ALMOXARIFADO.toString(),
				estoque.getAlmoxarifado()));
		
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<SceItemRecebimentoProvisorioVO> pesquisarItemRecebProvisorioPorSeqNrp(Integer nroDevolucao) {
		StringBuilder hql = new StringBuilder(200);

		hql.append(" SELECT IRP.").append(SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString() ).append( 
						 ", IRP.").append(SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString() ).append( 
						 ", IRP.").append(SceItemRecebProvisorio.Fields.NRP_SEQ.toString() ).append( 
						 ", IRP.").append(SceItemRecebProvisorio.Fields.NRO_ITEM.toString()).append(
						 ", IDF.").append(SceItemDevolucaoFornecedor.Fields.QUANTIDADE.toString() ).append(
				" FROM ").append( SceItemRecebProvisorio.class.getSimpleName()).append( " IRP, " ).append(
							SceNotaRecebimento.class.getSimpleName() ).append( " NRS, " ).append(
							SceItemNotaRecebimentoDevolucaoFornecedor.class.getSimpleName() ).append( " NRD, " ).append(
							SceItemDevolucaoFornecedor.class.getSimpleName() ).append( " IDF " ).append(
					" WHERE IDF.").append(SceItemDevolucaoFornecedor.Fields.DFS_SEQ.toString() ).append( " = :dfsSeq" ).append(
							" AND NRD.").append(SceItemNotaRecebimentoDevolucaoFornecedor.Fields.IDF_DFS_SEQ.toString() ).append( " = IDF." ).append( SceItemDevolucaoFornecedor.Fields.DFS_SEQ.toString() ).append(
							" AND NRD.").append(SceItemNotaRecebimentoDevolucaoFornecedor.Fields.IDF_NUMERO.toString() ).append( " = IDF." ).append( SceItemDevolucaoFornecedor.Fields.NUMERO.toString() ).append(
							" AND NRS.").append(SceNotaRecebimento.Fields.NUMERO_NR.toString() ).append( " = NRD.").append(SceItemNotaRecebimentoDevolucaoFornecedor.Fields.INR_NRS_SEQ.toString() ).append( 
							" AND IRP.").append(SceItemRecebProvisorio.Fields.NRP_SEQ.toString() ).append( " = NRS.").append(SceNotaRecebimento.Fields.NRP_SEQ.toString() ).append(
							" AND IRP.").append(SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString() ).append( " = NRD.").append(SceItemNotaRecebimentoDevolucaoFornecedor.Fields.AFN_NUMERO.toString() ).append(
							" AND IRP.").append(SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString() ).append( " = NRD.").append(SceItemNotaRecebimentoDevolucaoFornecedor.Fields.IAF_NUMERO.toString());
							
		
		Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("dfsSeq", nroDevolucao);
		
		List<Object[]> lista = query.list();
		
		List<SceItemRecebimentoProvisorioVO> listVO = new ArrayList<SceItemRecebimentoProvisorioVO>();

		if (lista != null && !lista.isEmpty()) {
			for(Object[] obj : lista){
				SceItemRecebimentoProvisorioVO vo = new SceItemRecebimentoProvisorioVO();
				vo.setPeaIafAfnNumero((Integer) obj[0]);
				vo.setPeaIafNumero((Integer) obj[1]);
				vo.setIrpNrpSeq((Integer) obj[2]);
				vo.setIrpNroItem((Integer) obj[3]);
				vo.setIdfQuantidade((Integer) obj[4]);
				
				listVO.add(vo);
			}

		}

		return listVO;
	}
	
	
	/**
	 * Esta consulta tem o objetivo de identificar o valor já comprometido de um ou mais recebimentos para o Documento Fiscal informado.
	 * 
	 * C4 da #28585
	 * 
	 * @param seqDocFiscal
	 * @return
	 */
	public BigDecimal buscarValorComprometido(final Integer seqDocFiscal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.DFE_SEQ.toString(), seqDocFiscal));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), Boolean.FALSE));

		criteria.setProjection(Projections.sum("IRP." + SceItemRecebProvisorio.Fields.VALOR.toString()));

		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Consulta quantidade de materiais recebidos pelo Hospital, mas ainda não incorporados ao estoque.
	 * 
	 * C5 de #5554 - Programação automática de Parcelas AF 
	 * 
	 * @param matCodigo
	 * @return
	 */
	public Long obterQuantidadeRecParcelas(Integer matCodigo) {

		StringBuilder hql = new StringBuilder()
		.append("SELECT COALESCE(SUM(IRP.").append(SceItemRecebProvisorio.Fields.QUANTIDADE).append("), 0) ")
		.append("FROM ")
		.append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ")
		.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(" IAF, ")
		.append(ScoMaterial.class.getSimpleName()).append(" MAT, ")
		.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC1, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC2, ")
		.append(SceItemRecebProvisorio.class.getSimpleName()).append(" IRP, ")
		.append(SceNotaRecebProvisorio.class.getSimpleName()).append(" NRP ")
		.append("WHERE ")
		.append("IAF.").append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO).append(" = ").append("AFN.").append(ScoAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO).append(" = :IND_EXCLUSAO_FSC1")
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO).append(" = :IND_EXCLUSAO_FSC2")
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO).append(" = ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.AFN_NUMERO)
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO).append(" = ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO).append(" = ").append("FSC1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO)
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO).append(" IS NOT NULL ")
		.append(" AND ").append("SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO).append(" = ").append("FSC1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO)
		.append(" AND ").append("MAT.").append(ScoMaterial.Fields.CODIGO).append(" = ").append("SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO)
		.append(" AND ").append("NRP.").append(SceNotaRecebProvisorio.Fields.AFE_AFN_NUMERO).append(" = ").append("AFN.").append(ScoAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("NRP.").append(SceNotaRecebProvisorio.Fields.IND_CONFIRMADO).append(" = :IND_CONFIRMADO_NPR")
		.append(" AND ").append("NRP.").append(SceNotaRecebProvisorio.Fields.IND_ESTORNO).append(" = :IND_ESTORNO_NPR")
		.append(" AND ").append("IRP.").append(SceItemRecebProvisorio.Fields.NRP_SEQ).append(" = ").append("NRP.").append(SceNotaRecebProvisorio.Fields.NRP_SEQ)
		.append(" AND ").append("IRP.").append(SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO).append(" = ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("MAT.").append(ScoMaterial.Fields.CODIGO).append(" = :CODIGO_ITEM_AF");
				
		Query query = createHibernateQuery(hql.toString());
			
		query.setParameter("IND_EXCLUSAO_FSC1", Boolean.FALSE);
		query.setParameter("IND_EXCLUSAO_FSC2", Boolean.FALSE);
		
		query.setParameter("IND_CONFIRMADO_NPR", Boolean.FALSE);
		query.setParameter("IND_ESTORNO_NPR", Boolean.FALSE);
		
		query.setParameter("CODIGO_ITEM_AF", matCodigo);
		
		query.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Long> result = query.list();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Busca do código, grupo do material e número de sua solicitação de compra
	 * 
	 * C6 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param iafNumero
	 * @return
	 */
	public GrupoMaterialNumeroSolicitacaoVO obterCodigoGrupoMaterialNumeroSolicitacao(Integer afnNumero, Integer iafNumero) {
		StringBuilder hql = new StringBuilder()
		.append("SELECT new ").append(GrupoMaterialNumeroSolicitacaoVO.class.getName()).append(" (")
		.append("MAT.").append(ScoMaterial.Fields.CODIGO).append(", ")
		.append("MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO).append(", ")
		.append("SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO).append(' ')
		.append(" ) ")
		.append("FROM ")
		.append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ")
		.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(" IAF, ")
		.append(ScoMaterial.class.getSimpleName()).append(" MAT, ")
		.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC1, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC2 ")
		.append("WHERE ")
		.append("IAF.").append(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO).append(" = ").append("AFN.").append(ScoAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO).append(" = :IND_EXCLUSAO_FSC1")
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO).append(" = :IND_EXCLUSAO_FSC2")
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO).append(" = ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO)
		.append(" AND ").append("FSC1.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO).append(" = ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO)
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO).append(" = ").append("FSC1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO)
		.append(" AND ").append("FSC2.").append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO).append(" IS NOT NULL ")
		.append(" AND ").append("SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO).append(" = ").append("FSC1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO)
		.append(" AND ").append("MAT.").append(ScoMaterial.Fields.CODIGO).append(" = ").append("SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO)
		.append(" AND ").append("AFN.").append(ScoAutorizacaoForn.Fields.NUMERO).append(" = :AFN_NUMERO")
		.append(" AND ").append("IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO).append(" = :IAF_NUMERO");
				
		Query query = createHibernateQuery(hql.toString());
			
		query.setParameter("IND_EXCLUSAO_FSC1", Boolean.FALSE);
		query.setParameter("IND_EXCLUSAO_FSC2", Boolean.FALSE);
		
		query.setParameter("AFN_NUMERO", afnNumero);
		query.setParameter("IAF_NUMERO", iafNumero);
				
		query.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<GrupoMaterialNumeroSolicitacaoVO> result = query.list();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * Método que verifica a existência de AF para o Recebimento informado.
	 * 
	 * @param numeroRecebimento - Número do Recebimento
	 * @return flag indicando a existência de AF
	 */
	public Boolean verificarExistenciaAFRecebimento(Integer numeroRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		
		criteria.setProjection(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()));
		
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA");

		criteria.add(Restrictions.eq("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString(), numeroRecebimento));
		
		return executeCriteriaExists(criteria);
	}

	
	/**
	 * Consulta C3 da #43475.
	 * @param numRecebimento
	 * @return
	 *   
	 */
	public QuantidadeDevolucaoBemPermanenteVO obterQuantidadeItensEntrega(Integer numRecebimento, Integer itemRecebimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class,"SIRP");
		criteria.createAlias("SIRP." + SceItemRecebProvisorio.Fields.PTM_ITENS_RECEB_PROVISORIOS.toString(), "PIRP", JoinType.INNER_JOIN);
	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SIRP."+SceItemRecebProvisorio.Fields.QUANTIDADE.toString()),	QuantidadeDevolucaoBemPermanenteVO.Fields.QTD.toString())
				.add(Projections.property("PIRP."+PtmItemRecebProvisorios.Fields.QUANTIDADE_DISP.toString()),QuantidadeDevolucaoBemPermanenteVO.Fields.QTD_DISP.toString()));
		
		criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), numRecebimento));
		criteria.add(Restrictions.eq("PIRP."+PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadeDevolucaoBemPermanenteVO.class));
		
		List<QuantidadeDevolucaoBemPermanenteVO> listQtdDevolucaoBemPermanente = executeCriteria(criteria);
		
		if (listQtdDevolucaoBemPermanente != null && !listQtdDevolucaoBemPermanente.isEmpty()) {
			return  listQtdDevolucaoBemPermanente.get(0);
		}
		return null;
	}
	
	/**
	 * #43467 Consulta C8
	 * @param estoque
	 * @param maxRps
	 * @return
	 */
	public AvaliacaoTecnicaVO carregarCampoDaTela(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		RegistrarAceiteTecnicoQueryBuilder queryBuilder = new RegistrarAceiteTecnicoQueryBuilder();
		List<AvaliacaoTecnicaVO> listParte1 = executeCriteria(queryBuilder.carregarCampoDaTelaParte1(ptmItemRecebProvisorios));
		List<AvaliacaoTecnicaVO> listParte2 = executeCriteria(queryBuilder.carregarCampoDaTelaParte2(ptmItemRecebProvisorios));
		return queryBuilder.ordenarConsultaCampoDaTela(listParte1, listParte2);
	}
	
}