package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.MpmTipoParecerUsoMdto;
import br.gov.mec.aghu.model.VMpmPrescrMdtos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Classe responsável pelos métodos de acesso e persistência dos dados para a entidade MpmParecerUsoMdto.
 */
public class MpmParecerUsoMdtosDAO extends BaseDao<MpmParecerUsoMdto> {

	private static final long serialVersionUID = 8906277320909998423L;

	public List<MpmParecerUsoMdto> listaParecerPelaJustificativaUso(Integer jumSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParecerUsoMdto.class);
		criteria.createAlias(MpmParecerUsoMdto.Fields.JUSTIFICATIVA.toString(), "JUM");
		
		criteria.add(Restrictions.eq("JUM."+MpmJustificativaUsoMdto.Fields.SEQ.toString(), jumSeq));
		
		return executeCriteria(criteria);

	}
	
	
//	set PUM.IND_PARECER_VERIFICADO = 'S'
//			 FROM v_mpm_prescr_mdtos VPM 
//			JOIN mpm_item_presc_parecer_mdtos IPP 
//			    ON IPP.ime_pmd_atd_seq = VPM.atd_seq 
//			    AND IPP.ime_pmd_seq = VPM.seq 
//			    AND IPP.ime_med_mat_codigo = VPM.mat_codigo 
//			    AND IPP.ime_seqp = VPM.seq_item 
//			JOIN mpm_parecer_uso_mdtos PUM 
//			    ON IPP.pum_seq = PUM.seq 
//			JOIN mpm_tipo_parecer_uso_mdtos TPM 
//			    ON PUM.tpm_seq = TPM.seq
//			JOIN MPM_PRESCRICAO_MDTOS
//			    ON MPM_PRESCRICAO_MDTOS.ATD_SEQ = VPM.ATD_SEQ
//			    AND MPM_PRESCRICAO_MDTOS.SEQ = VPM.SEQ
//			JOIN MPM_ITEM_PRESCRICAO_MDTOS
//			    ON MPM_ITEM_PRESCRICAO_MDTOS.PMD_ATD_SEQ = MPM_PRESCRICAO_MDTOS.ATD_SEQ
//			    AND MPM_ITEM_PRESCRICAO_MDTOS.PMD_SEQ = MPM_PRESCRICAO_MDTOS.SEQ
//			WHERE PUM.IND_PARECER_VERIFICADO = 'N'
//			   AND TPM.IND_MOSTRA_PARECER_AUTOMATICO = 'S'
//			   AND MPM_ITEM_PRESCRICAO_MDTOS.PMD_ATD_SEQ = "CÓDIGO DE ATENDIMENTO DO PACIENTE";
//			WHERE PUM.IND_PARECER_VERIFICADO = 'N'
//			   AND TPM.IND_MOSTRA_PARECER_AUTOMATICO = 'S'
//			   AND MPM_ITEM_PRESCRICAO_MDTOS.PMD_ATD_SEQ = "CÓDIGO DE ATENDIMENTO DO PACIENTE";
	
	public List<MpmParecerUsoMdto> listarParecerVisualizacao(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParecerUsoMdto.class);
		
		criteria.createAlias(MpmParecerUsoMdto.Fields.MPM_ITEM_PRESC_PARECER_MDTOSES.toString(), "ITEM");
		criteria.createAlias(MpmParecerUsoMdto.Fields.MPM_TIPO_PARECER_USO_MDTOS.toString(), "TPO");
		criteria.createAlias("ITEM." + MpmItemPrescParecerMdto.Fields.V_MPM_PRESC_MTDOS, "VPM");
		criteria.createAlias("VPM." + VMpmPrescrMdtos.Fields.ITEM_PRESC_PARECER_MDTO.toString(), "IPP");

		//criteria.createAlias("ITEM." + MpmItemPrescParecerMdto.Fields.);
		
		criteria.add(Restrictions.eq(MpmParecerUsoMdto.Fields.IND_PARECER_VERIFICADO.toString(), DominioSimNao.N));
		criteria.add(Restrictions.eq("TPO." + MpmTipoParecerUsoMdto.Fields.IND_MOSTRA_PARECER_AUTOMATICO.toString(), DominioSimNao.S.toString()));
		criteria.add(Restrictions.eq("IPP." + MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString(), atdSeq));

		return executeCriteria(criteria);
		
	}

	public Long obterCountParecerPorSeqJustificativa(BigDecimal seq, Integer jumSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParecerUsoMdto.class);
		criteria.add(Restrictions.eq(MpmParecerUsoMdto.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmParecerUsoMdto.Fields.JUM_SEQ.toString(), new BigDecimal(jumSeq)));
		return executeCriteriaCount(criteria);
	}
			
			
}
