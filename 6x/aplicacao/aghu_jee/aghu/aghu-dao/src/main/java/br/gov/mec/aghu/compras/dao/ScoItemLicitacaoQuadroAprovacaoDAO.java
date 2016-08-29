package br.gov.mec.aghu.compras.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

/**
 * DAO de itens de licitação do quadro de aprovação.
 * 
 * @author mlcruz
 */
public class ScoItemLicitacaoQuadroAprovacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemLicitacao> {
	private static final long serialVersionUID = 104093318308198786L;

	// Aliases
	private static final String LCT = "LCT", ITL = "ITL", FSL = "FSL", SLC = "SLC", 
			SLS = "SLS", MAT = "MAT", SRV = "SRV", PFR = "PFR", IPF = "IPF", FRN = "FRN",
			CEP = "CEP", CDP = "CDP", FPG = "FPG", MLC = "MLC", GST = "GST", GPF = "GPF";
	
	/**
	 * Pesquisa itens sem proposta.
	 *
	 * @param pacIds ID's dos PAC's
	 * @param assinatura Flag para obter gestor.
	 * @return Itens sem proposta.
	 */
	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarItensSemProposta(
			Set<Integer> pacIds, Boolean assinatura) {
		DetachedCriteria criteria = getCriteria(pacIds, assinatura);		
		criteria.add(Subqueries.notExists(getPropostaCriteria(null)));		
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa itens com proposta.
	 * 
	 * @param pacIds ID's dos PAC's
	 * @param assinatura Flag para obter gestor.
	 * @return Itens sem proposta.
	 */
	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarItensComProposta(
			Set<Integer> pacIds, Boolean assinatura) {
		DetachedCriteria criteria = getCriteria(pacIds, assinatura);		
		criteria.add(Subqueries.exists(getPropostaCriteria(null)));		
		criteria.add(Subqueries.notExists(getPropostaCriteria(true)));		
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa itens com proposta escolhida.
	 * 
	 * @param pacIds ID's dos PAC's
	 * @param assinatura Flag para obter gestor.
	 * @return Itens sem proposta escolhida.
	 */
	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarItensComPropostaEscolhida(
			Set<Integer> pacIds, Boolean assinatura) {
		// Principal
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, IPF);
		
		criteria.setProjection(getProjection(assinatura)
				.add(Property.forName(FRN + "."
						+ ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.FORNECEDOR.toString())
				.add(Property.forName(CEP
						+ "." + ScoCriterioEscolhaProposta.Fields.DESCRICAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.CRITERIO.toString())
				.add(Property.forName(IPF
						+ "." + ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.CONDICAO.toString())
				.add(Property.forName(IPF
						+ "." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.VALOR_UNITARIO.toString())
				.add(Property.forName(IPF
						+ "." + ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.FATOR_CONVERSAO.toString())
				.add(Property.forName(FPG 
						+ "." + ScoFormaPagamento.Fields.DESCRICAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.FORMA_PAGAMENTO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemLicitacaoQuadroAprovacaoVO.class));
		
		// Joins
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), ITL);
		join(criteria, assinatura);
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), PFR);
		criteria.createAlias(PFR + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), FRN);
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.CRITERIO_ESCOLHA_PROPOSTA.toString(), CEP);
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), CDP);
		criteria.createAlias(CDP + "." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), FPG);
		
		// Itens de um PAC específico.
		restrictByPac(criteria, pacIds);
		
		// Item escolhido.
		criteria.add(Restrictions.eq(IPF + "." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), true));

		return executeCriteria(criteria);
	}
	
	private DetachedCriteria getCriteria(Set<Integer> pacIds, Boolean assinatura) {
		// Principal
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, ITL);
		
		criteria.setProjection(getProjection(assinatura)
				.add(Property.forName(ITL + "."
						+ ScoItemLicitacao.Fields.MOTIVO_CANCEL.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.MOTIVO_CANCELAMENTO
								.toString())
				.add(Property.forName(ITL + "."
						+ ScoItemLicitacao.Fields.SITUACAO_JULGAMENTO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.SITUACAO_JULGAMENTO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemLicitacaoQuadroAprovacaoVO.class));
		
		// Joins
		join(criteria, assinatura);
		
		// Itens de um PAC específico.
		restrictByPac(criteria, pacIds);
		
		return criteria;
	}
	
	private void restrictByPac(DetachedCriteria criteria, Set<Integer> pacIds) {		
		criteria.add(Restrictions.in(LCT + "." + ScoLicitacao.Fields.NUMERO.toString(), pacIds));
	}

	private ProjectionList getProjection(Boolean assinatura) {
		ProjectionList projection = Projections
				.projectionList()
				.add(Property.forName(LCT + "."
						+ ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.MODALIDADE
								.toString())
				.add(Property.forName(LCT + "."
						+ ScoLicitacao.Fields.NUMERO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.NUMERO_PAC
								.toString())
				.add(Property.forName(LCT + "."
						+ ScoLicitacao.Fields.DESCRICAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.DESCRICAO_PAC
								.toString())
				.add(Property.forName(ITL + "."
						+ ScoItemLicitacao.Fields.NUMERO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.NUMERO_ITEM
								.toString())
				.add(Property.forName(FSL + "."
						+ ScoFaseSolicitacao.Fields.TIPO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.TIPO.toString())
				.add(Property.forName(MAT + "."
						+ ScoMaterial.Fields.CODIGO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.CODIGO_MATERIAL
								.toString())		
				.add(Property.forName(MAT + "."
						+ ScoMaterial.Fields.NOME.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.MATERIAL
								.toString())
				.add(Property.forName(SRV + "."
						+ ScoServico.Fields.CODIGO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.CODIGO_SERVICO.toString())				
				.add(Property.forName(SRV + "."
						+ ScoServico.Fields.NOME.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.SERVICO.toString())
				.add(Property.forName(ITL + "."
						+ ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()),
						ItemLicitacaoQuadroAprovacaoVO.Field.EXCLUIDO
								.toString());
		
		if (assinatura) {
			projection.add(Property.forName(GPF + "."
					+ RapPessoasFisicas.Fields.NOME.toString()),
					ItemLicitacaoQuadroAprovacaoVO.Field.GESTOR.toString());
		}
		
		return projection;
	}

	private void join(DetachedCriteria criteria, Boolean assinatura) {
		criteria.createAlias(ITL + "." + ScoItemLicitacao.Fields.LICITACAO.toString(), LCT);
		criteria.createAlias(LCT + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), MLC);
		criteria.createAlias(ITL + "." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), FSL);
		
		if (assinatura) {
			criteria.createAlias(LCT + "."
					+ ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), GST,
					Criteria.LEFT_JOIN);
			
			criteria.createAlias(
					GST + "." + RapServidores.Fields.PESSOA_FISICA, GPF,
					Criteria.LEFT_JOIN);
		}
		
		criteria.createAlias(FSL + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), 
				SLC, Criteria.LEFT_JOIN);
		
		criteria.createAlias(SLC + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), 
				MAT, Criteria.LEFT_JOIN);
		
		criteria.createAlias(FSL + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), 
				SLS, Criteria.LEFT_JOIN);
		
		criteria.createAlias(SLS + "." + ScoSolicitacaoServico.Fields.SERVICO.toString(), 
				SRV, Criteria.LEFT_JOIN);
	}

	private DetachedCriteria getPropostaCriteria(Boolean escolhido) {		
		// Itens sem proposta.
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemPropostaFornecedor.class, IPF);
		
		criteria.setProjection(Projections.property(IPF + "."
				+ ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()));
		
		criteria.add(Restrictions.eqProperty(
				IPF + "." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), 
				ITL + "." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		
		criteria.add(Restrictions.eqProperty(
				IPF + "." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), 
				ITL + "." + ScoItemLicitacao.Fields.NUMERO.toString()));
		
		if (escolhido != null) {
			criteria.add(Restrictions.eq(
					IPF + "." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), escolhido));
		}
		
		return criteria;
	}
}