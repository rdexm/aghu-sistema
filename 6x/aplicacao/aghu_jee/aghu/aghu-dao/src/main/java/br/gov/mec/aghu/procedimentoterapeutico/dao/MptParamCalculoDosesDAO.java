package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptParamCalculoDosesDAO extends BaseDao<MptParamCalculoDoses> {

	private static final String PCD = "PCD.";
	private static final long serialVersionUID = -737233291609606171L;

	public List<ParametroDoseUnidadeVO> listarParametroDoseMedicamento(ProtocoloItensMedicamentoVO medicamento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptParamCalculoDoses.class, "PCD");
	//	criteria.createAlias(PCD + MptParamCalculoDoses.Fields.AFA_FORMA_DOSAGEM.toString(), "FDS");
	//			.createAlias("FDS." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "UNI");
		//criteria.add(Restrictions.eq("FDS." + AfaFormaDosagem.Fields.MEDICAMENTOS_MAT_CODIGO.toString(), medicamento.getMedMatCodigo()))
		criteria.add(Restrictions.eq(PCD + MptParamCalculoDoses.Fields.MPT_PROTOCOLO_ITEM_MEDICAMENTOS_SEQ.toString(), medicamento.getPimSeq()));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.SEQ.toString()), ParametroDoseUnidadeVO.Fields.SEQ.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.DOSE.toString()), ParametroDoseUnidadeVO.Fields.DOSE.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.UNID_BASE_CALCULO.toString()), ParametroDoseUnidadeVO.Fields.UNID_BASE_CALCULO.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.AFA_FORMA_DOSAGEM.toString()), ParametroDoseUnidadeVO.Fields.AFA_FORMA_DOSAGEM.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.TIPO_CALCULO.toString()), ParametroDoseUnidadeVO.Fields.TIPO_CALCULO.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.IDADE_MINIMA.toString()), ParametroDoseUnidadeVO.Fields.IDADE_MINIMA.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.IDADE_MAXIMA.toString()), ParametroDoseUnidadeVO.Fields.IDADE_MAXIMA.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.PESO_MINIMO.toString()), ParametroDoseUnidadeVO.Fields.PESO_MINIMO.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.PESO_MAXIMO.toString()), ParametroDoseUnidadeVO.Fields.PESO_MAXIMO.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.UNID_IDADE.toString()), ParametroDoseUnidadeVO.Fields.UNID_IDADE.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.DOSE_MAXIMA_UNITARIA.toString()), ParametroDoseUnidadeVO.Fields.DOSE_MAXIMA_UNITARIA.toString())
				.add(Projections.property(PCD + MptParamCalculoDoses.Fields.PMI_SEQ.toString()), ParametroDoseUnidadeVO.Fields.PMI_SEQ.toString()))
			//	.add(Projections.property("UNI." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), ParametroDoseUnidadeVO.Fields.DESCRICAO.toString()))
				;
		criteria.setResultTransformer(Transformers.aliasToBean(ParametroDoseUnidadeVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MptParamCalculoDoses> obterListaDoseMdtosPorSeqMdto(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptParamCalculoDoses.class, "PCD");
		criteria.add(Restrictions.eq(PCD + MptParamCalculoDoses.Fields.MPT_PROTOCOLO_ITEM_MEDICAMENTOS_SEQ.toString(), seq));
		
		return executeCriteria(criteria);
	}
}
