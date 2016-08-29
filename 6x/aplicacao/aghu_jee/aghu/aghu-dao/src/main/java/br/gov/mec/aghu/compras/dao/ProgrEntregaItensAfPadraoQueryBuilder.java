package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ProgrEntregaItensAfPadraoQueryBuilder extends
		QueryBuilder<DetachedCriteria> {

	private static final String AFN = "AFN.";
	private static final String AFN_JN = "AFN_JN.";
	private static final String PAF = "PAF.";
	private static final String PFR = "PFR.";
	private static final long serialVersionUID = -7459532781182000968L;
	private Integer quantidadeDias; 
	private Integer diaDoMes;
	private boolean isCount = false;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		criteria.createAlias(AFN + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.INNER_JOIN);
		criteria.createAlias(PFR + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias(PFR + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", JoinType.INNER_JOIN);
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MDL", JoinType.LEFT_OUTER_JOIN);

		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.in(AFN + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), new DominioModalidadeEmpenho[] { DominioModalidadeEmpenho.ESTIMATIVA, DominioModalidadeEmpenho.CONTRATO}));
		criteria.add(Restrictions.eq(AFN + ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString(), true));
		DetachedCriteria criteriaAFAssinada = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFN_JN");
		criteriaAFAssinada.add(Restrictions.eqProperty(AFN_JN + ScoAutorizacaoFornJn.Fields.NUMERO.toString(), AFN + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteriaAFAssinada.add(Restrictions.isNotNull(AFN_JN + ScoAutorizacaoFornJn.Fields.MATRICULA_ASSINA_COORD.toString()));
		criteriaAFAssinada.add(Restrictions.isNotNull(AFN_JN + ScoAutorizacaoFornJn.Fields.DT_ASSINATURA_COORD.toString()));
		criteriaAFAssinada.setProjection(Projections.projectionList().add(Projections.property(AFN_JN + ScoAutorizacaoFornJn.Fields.NUMERO.toString())));
		criteria.add(Subqueries.propertyIn(AFN + ScoAutorizacaoForn.Fields.NUMERO.toString(), criteriaAFAssinada));
		DetachedCriteria criteriaParcelaNaoPlanjada = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PAF");
		criteriaParcelaNaoPlanjada.add(Restrictions.eq(PAF + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		criteriaParcelaNaoPlanjada.add(Restrictions.eq(PAF + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		if(diaDoMes > 20) {
			criteriaParcelaNaoPlanjada.add(Restrictions.lt(PAF + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.adicionaDias(DateUtil.obterUltimoDiaDoMes(DateUtil.adicionaMeses(new Date(), 1)), quantidadeDias)));
		}
		else {
			criteriaParcelaNaoPlanjada.add(Restrictions.lt(PAF + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.adicionaDias(DateUtil.obterUltimoDiaDoMes(new Date()), quantidadeDias)));
		}

		criteriaParcelaNaoPlanjada.setProjection(Projections.projectionList().add(Projections.property(PAF + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString())));
		criteria.add(Subqueries.propertyIn(AFN + ScoAutorizacaoForn.Fields.NUMERO.toString(), criteriaParcelaNaoPlanjada));
		
		criteria.addOrder(Order.asc(PFR + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()));
		criteria.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
		if(!isCount){
			criteria.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NUMERO_AF.toString())
					.add(Projections.property(PFR + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NUMERO_LICITACAO.toString())
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.COMPLEMENTO.toString())
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.SITUACAO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.SITUACAO.toString())
					.add(Projections.property("MDL." + ScoModalidadeLicitacao.Fields.CODIGO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.MODALIDADE_COMPRA.toString())
					.add(Projections.property("MDL." + ScoModalidadeLicitacao.Fields.DESCRICAO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.DESCRICAO_MODALIDADE_COMPRA.toString())
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.MODALIDADE_EMPENHO.toString())
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.VENCIMENTO_CONTRATO.toString())
					.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NRO_FORNECEDOR.toString())
					.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.NOME_FORNECEDOR.toString())
					.add(Projections.property(AFN + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), PesquisarPlanjProgrEntregaItensAfVO.Fields.PFR_LCT_NUMERO.toString())
					);
			criteria.setResultTransformer(Transformers.aliasToBean(PesquisarPlanjProgrEntregaItensAfVO.class));
		}
		
		
	}

	public DetachedCriteria build(Integer quantidadeDias, Integer diaDoMes, boolean count) {
		
		this.quantidadeDias = quantidadeDias;
		this.diaDoMes = diaDoMes;
		this.isCount = count;

		return super.build();
	}

}
