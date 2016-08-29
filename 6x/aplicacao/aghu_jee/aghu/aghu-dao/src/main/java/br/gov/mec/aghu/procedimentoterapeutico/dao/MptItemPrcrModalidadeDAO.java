package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptImagemPrcrModalidade;
import br.gov.mec.aghu.model.MptItemPrcrModalidade;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptTipoModalidade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImagemModalidadeOrientacaoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptItemPrcrModalidadeDAO extends BaseDao<MptItemPrcrModalidade> {
	
	private static final long serialVersionUID = -8924324832511509324L;
	
	private final String ALIAS_PTE = "PTE";
	private final String ALIAS_ITM = "ITM";
	private final String ALIAS_TMD = "TMD";
	private final String ALIAS_FIG = "FIG";
	private final String ALIAS_IIG = "IIG";
	private final String PONTO = ".";

	/**
	 * #43089 - C5
	 * @param atdSeq
	 * @param pteSeq
	 * @return List<ImagemModalidadeOrientacaoVO>
	 */
	public List<ImagemModalidadeOrientacaoVO> obterImagemModalidadeOrientacao(Integer atdSeq, Integer pteSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptItemPrcrModalidade.class, "ITM");
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(ALIAS_PTE + PONTO + MptPrescricaoPaciente.Fields.ATD_SEQ.toString()), ImagemModalidadeOrientacaoVO.Fields.ATD_SEQ_PRESCRICAO_PACIENTE.toString());
		projectionList.add(Projections.property(ALIAS_PTE + PONTO + MptPrescricaoPaciente.Fields.SEQ.toString()), ImagemModalidadeOrientacaoVO.Fields.SEQ_PRESCRICAO_PACIENTE.toString());
		projectionList.add(Projections.property(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.ID_SEQP.toString()), ImagemModalidadeOrientacaoVO.Fields.SEQP_ITEM_PRCR_MODALIDADE.toString());
		projectionList.add(Projections.property(ALIAS_TMD + PONTO + MptTipoModalidade.Fields.CLASSIFICACAO.toString()), ImagemModalidadeOrientacaoVO.Fields.CLASSIFICACAO_TIPO_MODALIDADE.toString());
		projectionList.add(Projections.property(ALIAS_TMD + PONTO + MptTipoModalidade.Fields.DESCRICAO.toString()), ImagemModalidadeOrientacaoVO.Fields.DESCRICAO_TIPO_MODALIDADE.toString());
		projectionList.add(Projections.property(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.NUM_VEZES_SEMANA.toString()), ImagemModalidadeOrientacaoVO.Fields.NUM_VEZES_SEMANA.toString());
		projectionList.add(Projections.property(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.ORIENTACOES.toString()), ImagemModalidadeOrientacaoVO.Fields.ORIENTACOES_ITEM_PRCR_MODALIDADE.toString());
		projectionList.add(Projections.property(ALIAS_IIG + PONTO + MptImagemPrcrModalidade.Fields.IMAGEM.toString()), ImagemModalidadeOrientacaoVO.Fields.IMAGEM.toString());
		
		criteria.setProjection(projectionList);
		
		criteria.createAlias(MptItemPrcrModalidade.Fields.MPT_PRESCRICAO_PACIENTE.toString(), ALIAS_PTE, JoinType.INNER_JOIN);
		criteria.createAlias(MptItemPrcrModalidade.Fields.MPT_TIPO_MODALIDADES.toString(), ALIAS_TMD, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MptItemPrcrModalidade.Fields.MAM_FIGURAS.toString(), ALIAS_FIG, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MptItemPrcrModalidade.Fields.MPT_IMAGEM_PRCR_MODALIDADES.toString(), ALIAS_IIG, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_PTE + PONTO + MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(ALIAS_PTE + PONTO + MptPrescricaoPaciente.Fields.SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.IND_SITUACAO_ITEM.toString(), "V"));
		criteria.add(Restrictions.isNull(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.ALTERADO_EM.toString()));
		criteria.add(Restrictions.in(ALIAS_TMD + PONTO + MptTipoModalidade.Fields.CLASSIFICACAO.toString(), new String[] {"M", "E"}));
		
		criteria.addOrder(Order.asc(ALIAS_ITM + PONTO + MptItemPrcrModalidade.Fields.ORDEM_EXECUCAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ImagemModalidadeOrientacaoVO.class));
		
		return executeCriteria(criteria);
	}
	
}
