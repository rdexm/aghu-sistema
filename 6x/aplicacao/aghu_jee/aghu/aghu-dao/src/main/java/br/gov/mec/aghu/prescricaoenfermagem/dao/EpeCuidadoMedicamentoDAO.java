package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoMedicamento;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoMedicamentoVO;

public class EpeCuidadoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCuidadoMedicamento> {

	private static final long serialVersionUID = 7602279787254299824L;

	// #4961 (Manter medicamentos x cuidados)
	// C4
	public List<CuidadoMedicamentoVO> pesquisarCuidadosMedicamentos(Integer medMatCodigo, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = montarCriteriaMedicamento(medMatCodigo);
		criteria.addOrder(Order.asc(EpeCuidadoMedicamento.Fields.CUI_SEQ.toString()));

		ProjectionList projection = Projections.projectionList().add(Projections.property("cme." + EpeCuidadoMedicamento.Fields.CUI_SEQ.toString()), "cuiSeq")
				.add(Projections.property("cui." + EpeCuidados.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("cme." + EpeCuidadoMedicamento.Fields.HORAS_ANTES.toString()), "horasAntes")
				.add(Projections.property("cme." + EpeCuidadoMedicamento.Fields.HORAS_APOS.toString()), "horasApos")
				.add(Projections.property("cme." + EpeCuidadoMedicamento.Fields.SITUACAO.toString()), "situacao");
		criteria.setProjection(projection);

		List<Object[]> retorno = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		List<CuidadoMedicamentoVO> lista = new ArrayList<CuidadoMedicamentoVO>();
		for (Object[] objects : retorno) {
			CuidadoMedicamentoVO cuidadoMedicamentoVO = new CuidadoMedicamentoVO();
			cuidadoMedicamentoVO.setCuiSeq((Short) objects[0]);
			cuidadoMedicamentoVO.setDescricao((String) objects[1]);
			cuidadoMedicamentoVO.setHorasAntes((Integer) objects[2]);
			cuidadoMedicamentoVO.setHorasApos((Integer) objects[3]);
			cuidadoMedicamentoVO.setSituacao(((DominioSituacao) objects[4]).getDescricao());
			lista.add(cuidadoMedicamentoVO);
		}

		return lista;
	}

	// #4961 (Manter medicamentos x cuidados)
	// C4 Count
	public Long pesquisarCuidadosMedicamentosCount(Integer medMatCodigo) {
		DetachedCriteria criteria = montarCriteriaMedicamento(medMatCodigo);
		return executeCriteriaCount(criteria);
	}

	public DetachedCriteria montarCriteriaMedicamento(Integer medMatCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoMedicamento.class, "cme");

		criteria.createAlias("cme." + EpeCuidadoMedicamento.Fields.CUIDADO.toString(), "cui");
		if (medMatCodigo != null) {
			criteria.add(Restrictions.eq(EpeCuidadoMedicamento.Fields.MED_MAT_CODIGO.toString(), medMatCodigo));
		}

		return criteria;
	}

	public EpeCuidadoMedicamento obterPorCuiSeqMatCodigo(Short cuiSeq, Integer medMatCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoMedicamento.class);
		criteria.add(Restrictions.eq(EpeCuidadoMedicamento.Fields.MED_MAT_CODIGO.toString(), medMatCodigo));
		criteria.add(Restrictions.eq(EpeCuidadoMedicamento.Fields.CUI_SEQ.toString(), cuiSeq));
		return (EpeCuidadoMedicamento) executeCriteriaUniqueResult(criteria);
	}

}
