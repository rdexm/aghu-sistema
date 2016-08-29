package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.VMpmPrescrPendente;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;

public class VMpmPrescrPendenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmPrescrPendente> {

	private static final long serialVersionUID = 3571211954932802482L;

	public List<JustificativaMedicamentoUsoGeralVO> obterListaMedicamentosUsoRestritoPorAtendimento(Integer atdSeq, String indAntiMicrobiano, Boolean indQuimioterapico, Short gupSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrPendente.class, "VPP");
		criteria.createAlias("VPP." + VMpmPrescrPendente.Fields.VIA_ADMINISTRACAO.toString(), "VAD");
		criteria.createAlias("VPP." + VMpmPrescrPendente.Fields.GRUPO_USO_MDTO.toString(), "GUP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.DESCRICAO_EDIT.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.DESCRICAO_EDIT.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.DOSE_EDIT.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.DOSE_EDIT.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.FREQ_EDIT.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.FREQ_EDIT.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.VAD_SIGLA.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.VAD_SIGLA.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.CSP_CNV_CODIGO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.CSP_CNV_CODIGO.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.CSP_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.CSP_SEQ.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.PMD_ATD_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.IME_PMD_ATD_SEQ.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.PMD_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.IME_PMD_SEQ.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.MED_MAT_CODIGO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.MED_MAT_CODIGO.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.SEQP.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.IME_SEQP.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.GUP_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.GUP_SEQ.toString())
				.add(Projections.property("GUP." + AfaGrupoUsoMedicamento.Fields.DESCRICAO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.GUP_DESC.toString())	
				.add(Projections.property("VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.VAD_DESCRICAO.toString()));
		
		criteria.add(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.PMD_ATD_SEQ.toString(), atdSeq));
		
		if (indQuimioterapico) {
			criteria.add(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.IND_QUIMIOTERAPICO.toString(), "S"));
		} else {
			criteria.add(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.IND_ANTIMICROBIANO.toString(), indAntiMicrobiano));
		}
		
		if (gupSeq != null) {
			criteria.add(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.GUP_SEQ.toString(), gupSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(JustificativaMedicamentoUsoGeralVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<JustificativaMedicamentoUsoGeralVO> obterMedicamentosIndicesRestritosPorAtendimento(Integer atdSeq, Short paramGupTb) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrPendente.class, "VPP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.IND_ANTIMICROBIANO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.IND_ANTIMICROBIANO.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.IND_QUIMIOTERAPICO.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.IND_QUIMIOTERAPICO.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.PMD_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.PMD_SEQ.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.GUP_SEQ.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.GUP_SEQ.toString())
				.add(Projections.property("VPP." + VMpmPrescrPendente.Fields.SEQP.toString())
						, JustificativaMedicamentoUsoGeralVO.Fields.SEQP.toString()));
		
		//desconsidera medicamentos tuberculostaticos pelo grupo
		if (paramGupTb != null) {
			criteria.add(Restrictions.not(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.GUP_SEQ.toString(), paramGupTb)));	
		}
		
		criteria.add(Restrictions.eq("VPP." + VMpmPrescrPendente.Fields.PMD_ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc("VPP." + VMpmPrescrPendente.Fields.IND_QUIMIOTERAPICO.toString()));
		criteria.addOrder(Order.asc("VPP." + VMpmPrescrPendente.Fields.IND_ANTIMICROBIANO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(JustificativaMedicamentoUsoGeralVO.class));
		return executeCriteria(criteria);
	}
}
