package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.estoque.vo.SceBoletimOcorrenciasVO;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceNotaRecebimento;

public class SceBoletimOcorrenciasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceBoletimOcorrencias> {

	private static final long serialVersionUID = 1297382069290150237L;

	public List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(Integer seqNotaRecebimento, DominioBoletimOcorrencias situacao, Boolean indSituacaoDiferente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceBoletimOcorrencias.class, "BO");
		criteria.createAlias("BO." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NR");
		criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), seqNotaRecebimento));

		if (situacao != null) {
			if (indSituacaoDiferente) {
				criteria.add(Restrictions.ne("BO." + SceBoletimOcorrencias.Fields.SITUACAO, situacao));
			} else {
				criteria.add(Restrictions.eq("BO." + SceBoletimOcorrencias.Fields.SITUACAO, situacao));
			}
		}
		criteria.addOrder(Order.asc("BO." + SceBoletimOcorrencias.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	public Long obterQtdeBoletimOcorrenciaPorNrMaterial(Integer seqNotaRecebimento, Integer codigoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceBoletimOcorrencias.class, "BO");
		criteria.createCriteria("BO." + SceBoletimOcorrencias.Fields.ITENS_BOLETIM_OCORRENCIA.toString(), "IBO");
		criteria.createAlias("BO." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NR");

		criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), seqNotaRecebimento));
		criteria.add(Restrictions.eq("IBO." + SceItemBoc.Fields.MAT_CODIGO.toString(), codigoMaterial));

		DominioBoletimOcorrencias[] situacao = { DominioBoletimOcorrencias.S, DominioBoletimOcorrencias.H };
		criteria.add(Restrictions.not(Restrictions.in("BO." + SceBoletimOcorrencias.Fields.SITUACAO.toString(), situacao)));

		criteria.setProjection(Projections.projectionList().add(Projections.sum("IBO." + SceItemBoc.Fields.QTDE.toString())));

		return (Long) executeCriteriaUniqueResult(criteria);
	}

	public Integer obterUltimoSeq() {
		StringBuffer sl = new StringBuffer(50);

		if (isOracle()) {
			sl.append("select agh.sce_boc_sq1.CURRVAL from dual");
		} else {
			sl.append("select currval('agh.sce_boc_sq1')");
		}

		List<?> listCurrVal = this.createNativeQuery(sl.toString()).getResultList();

		Number currVal = null;

		if (isOracle()) {
			currVal = (BigDecimal) listCurrVal.get(0);
		} else {
			currVal = (BigInteger) listCurrVal.get(0);
		}

		return currVal.intValue();
	}

	public SceBoletimOcorrenciasVO obterDadosBoletimTitulo(Integer notaRecebimentoNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceBoletimOcorrencias.class);

		criteria.createAlias(SceBoletimOcorrencias.Fields.ITENS_BOLETIM_OCORRENCIA.toString(), "BOC", JoinType.INNER_JOIN);
		criteria.createAlias(SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.groupProperty(SceBoletimOcorrencias.Fields.SEQ.toString()), SceBoletimOcorrenciasVO.Fields.SEQ.toString());
		pList.add(Projections.sum("BOC." + SceItemBoc.Fields.VALOR.toString()), SceBoletimOcorrenciasVO.Fields.VALOR.toString());
		pList.add(Projections.groupProperty(SceBoletimOcorrencias.Fields.SITUACAO.toString()), SceBoletimOcorrenciasVO.Fields.SITUACAO.toString());
		pList.add(Projections.groupProperty(SceBoletimOcorrencias.Fields.DESCRICAO.toString()), SceBoletimOcorrenciasVO.Fields.DESCRICAO.toString());
		criteria.setProjection(pList);

		criteria.add(Restrictions.eq("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), notaRecebimentoNumero));

		criteria.setResultTransformer(Transformers.aliasToBean(SceBoletimOcorrenciasVO.class));
		return (SceBoletimOcorrenciasVO) executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimento(Integer seqNotaRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceBoletimOcorrencias.class, "BO");
		criteria.createAlias("BO." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NR");
		criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), seqNotaRecebimento));
		criteria.addOrder(Order.asc("BO." + SceBoletimOcorrencias.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
}
