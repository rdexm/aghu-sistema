package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
class ConsultaMaterialProcedimentoOPMEVOQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final String ALIAS_ROP = "ROP";
	private static final String ALIAS_IRO = "IRO";
	private static final String ALIAS_MIO = "MIO";
	private static final String ALIAS_IPH = "IPH";
	private static final String ALIAS_PHI = "PHI";
	private static final String ALIAS_MAT = "MAT";
	private static final String PONTO = ".";
	
	private static final String ALIAS_IRO_HIB = "iro1_";
	private static final String ALIAS_IPH_HIB = "iph3_";
	private static final String ALIAS_MAT_HIB = "mat5_";

	private DetachedCriteria criteria;
	private Short requisicaoSelecionada;
	private List<DominioRequeridoItemRequisicao> requisicoes;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MbcRequisicaoOpmes.class, ALIAS_ROP);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
		setOrderBy();
		setResultTransformer();
	}

	private void setJoin() {
		criteria.createAlias(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.ITENS_REQUISICAO.toString(), ALIAS_IRO);
		criteria.createAlias(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.MATERIAIS_ITEM_OPME.toString(), ALIAS_MIO, Criteria.LEFT_JOIN);
		criteria.createAlias(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.ITENS_PROCED_HOSPITALAR.toString(), ALIAS_IPH, Criteria.LEFT_JOIN);
		criteria.createAlias(ALIAS_MIO + PONTO + MbcMateriaisItemOpmes.Fields.PROCED_HOSP_INTERNOS.toString(), ALIAS_PHI, Criteria.LEFT_JOIN);
		criteria.createAlias(ALIAS_MIO + PONTO + MbcMateriaisItemOpmes.Fields.MATERIAL.toString(), ALIAS_MAT, Criteria.LEFT_JOIN);
	}

	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.ID.toString(), requisicaoSelecionada));
		criteria.add(Restrictions.ne(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.toString(), DominioRequeridoItemRequisicao.NRQ));
		if(requisicoes != null){
			criteria.add(Restrictions.in(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.toString(), requisicoes));
		}
	}

	private void setProjecao() {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()), MateriaisProcedimentoOPMEVO.Fields.IPH_PHO_SEQ.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.SEQ.toString()), MateriaisProcedimentoOPMEVO.Fields.IPH_SEQ.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.COD_TABELA.toString()), MateriaisProcedimentoOPMEVO.Fields.COD_TABELA.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), MateriaisProcedimentoOPMEVO.Fields.DESCRICAO.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.IND_COMPATIVEL.toString()), MateriaisProcedimentoOPMEVO.Fields.IND_COMPATIVEL.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.toString()), MateriaisProcedimentoOPMEVO.Fields.REQUERIDO.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.QTD_SOLC.toString()), MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_SOLICITADA.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.QTD_AUTR_HSP.toString()), MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_AUTORIZADA_HOSPITAL.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.VLR_UNIT_IPH.toString()), MateriaisProcedimentoOPMEVO.Fields.VALOR_UNITARIO_IPH.toString())
				.add(Projections.property(ALIAS_PHI + PONTO + FatProcedHospInternosPai.Fields.SEQ.toString()), MateriaisProcedimentoOPMEVO.Fields.PHI_SEQ.toString())
				.add(Projections.property(ALIAS_MAT + PONTO + ScoMaterial.Fields.CODIGO.toString()), MateriaisProcedimentoOPMEVO.Fields.MATERIAL_CODIGO.toString())
				.add(Projections.property(ALIAS_MAT + PONTO + ScoMaterial.Fields.NOME.toString()), MateriaisProcedimentoOPMEVO.Fields.NOME.toString())
				.add(Projections.property(ALIAS_MAT + PONTO + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), MateriaisProcedimentoOPMEVO.Fields.UNIDADE_MEDIDA_CODIGO.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.SOLC_NOVO_MAT.toString()), MateriaisProcedimentoOPMEVO.Fields.SOLICITACAO_NOVO_MATERIAL.toString())
				.add(Projections.property(ALIAS_IRO + PONTO + MbcItensRequisicaoOpmes.Fields.ANEXO_ORCAMENTO.toString()), MateriaisProcedimentoOPMEVO.Fields.ANEXO_ORCAMENTO.toString())
			);
	}

	private void setOrderBy() {
		StringBuilder orderBy = new StringBuilder(100);
		orderBy.append("CASE ").append(ALIAS_IRO_HIB + PONTO + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO.name())
			.append(" WHEN '").append(DominioRequeridoItemRequisicao.NOV.name()).append("' THEN 1")
			.append(" WHEN '").append(DominioRequeridoItemRequisicao.ADC.name()).append("' THEN 2")
			.append(" ELSE 3")
			.append(" END ")
			.append(", ").append(ALIAS_IPH_HIB + PONTO + FatItensProcedHospitalar.Fields.COD_TABELA.name())
			.append(", ").append(ALIAS_MAT_HIB + PONTO + ScoMaterial.Fields.CODIGO.name());
		
		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
	}

	public void setResultTransformer() {
		criteria.setResultTransformer(Transformers.aliasToBean(MateriaisProcedimentoOPMEVO.class));
	}
	
	public DetachedCriteria build(final Short requisicaoSelecionada, List<DominioRequeridoItemRequisicao> requisicoes) {
		this.requisicaoSelecionada = requisicaoSelecionada;
		this.requisicoes = requisicoes;
		return super.build();
	}
}
