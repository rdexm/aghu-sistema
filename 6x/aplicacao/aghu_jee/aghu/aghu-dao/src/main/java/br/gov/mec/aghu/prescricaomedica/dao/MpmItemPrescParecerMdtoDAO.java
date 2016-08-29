package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;


public class MpmItemPrescParecerMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemPrescParecerMdto>{

	private static final long serialVersionUID = 1019225482474366899L;

	public List<MpmItemPrescParecerMdto> pesquisarItensParecerMedicamentos(
			Integer atdSeq, Long pmdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescParecerMdto.class);

		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString(), pmdSeq));
			
		return executeCriteria(criteria);
	}
	
	public MpmItemPrescParecerMdto obterParecerPorMedicamento(Integer atdSeq, Long pmdSeq, Short imeSeqp, Integer medMatCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescParecerMdto.class);
		
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString(), pmdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_SEQP.toString(), imeSeqp));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString(), medMatCodigo));
			
		return (MpmItemPrescParecerMdto) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * C1 Estoria #5302
	 * @param prontuario
	 * @param leitoID
	 * @return
	 */
	public List<MpmItemPrescParecerMdto> pesquisarMpmItemPrescParecerMdtoPorProtuarioLeito(Integer prontuario, Date parametroDataFim,Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescParecerMdto.class,"MIPPM");
		
		criteria.createAlias("MIPPM."+MpmItemPrescParecerMdto.Fields.MPM_PARECER_USO_MDTOS.toString(), "MPUM");
		criteria.createAlias("MPUM."+MpmParecerUsoMdto.Fields.MPM_TIPO_PARECER_USO_MDTOS.toString(), "MTPUM");
		criteria.createAlias("MIPPM."+MpmItemPrescParecerMdto.Fields.MPM_ITEM_PRESCRICAO_MDTOS.toString(), "MPUMD",JoinType.LEFT_OUTER_JOIN);

		
		criteria.createAlias("MPUMD."+MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString().toString(), "PRSMED",JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("MPUMD."+MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString().toString(), "AFMED",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFMED."+AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString().toString(), "MEDUMME",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFMED."+AfaMedicamento.Fields.TPR.toString().toString(), "TPR",JoinType.LEFT_OUTER_JOIN);
		//
		criteria.createAlias("MPUMD."+MpmItemPrescricaoMdto.Fields.FORMA_DOSAGEM.toString().toString(), "FORDOS",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FORDOS."+AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString().toString(), "FORUUMED",JoinType.LEFT_OUTER_JOIN);
		
  
	    criteria.add(Restrictions.eq("MPUMD."+MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeq));		
	    		   
			

			String sql = " (this_.IME_PMD_ATD_SEQ, this_.IME_PMD_SEQ,  this_.IME_MED_MAT_CODIGO,"+
							"this_.IME_SEQP) in (select ime.pmd_atd_seq,ime.pmd_seq,ime.med_mat_codigo,ime.seqp from mpm_item_prescricao_mdtos ime "+ 
                                           "where ime.pmd_atd_seq       = "+atdSeq+"and ime.ind_origem_justif   = 'S' )";
		criteria.add(Restrictions.sqlRestriction(sql));
		
		criteria.addOrder(Order.asc("MPUM."+MpmParecerUsoMdto.Fields.DTHR_PARECER.toString()));
				
		return  executeCriteria(criteria);
	}
	
	public Long obterCountItemPrescParecerPorParecerUso(BigDecimal pumSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescParecerMdto.class);
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.MPM_PARECER_USO_MDTOS_SEQ.toString(), pumSeq));
		return executeCriteriaCount(criteria);
	}

	public Long obterCountParecerPorItemPrescricaoMdto(Integer atdSeq, Long seq, Integer medMatCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescParecerMdto.class);
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString(), medMatCodigo));
		return executeCriteriaCount(criteria);
	}
}
