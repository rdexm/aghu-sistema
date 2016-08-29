package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;

public class AacGradeProcedHospitalarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacGradeProcedHospitalar> {
	
	private static final long serialVersionUID = 5391958763152463866L;

	public List<AacGradeProcedHospitalar> listarProcedimentosGrade(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeProcedHospitalar.class);
		criteria.createAlias(AacGradeProcedHospitalar.Fields.PHI.toString(), AacGradeProcedHospitalar.Fields.PHI.toString());
		criteria.add(Restrictions.eq(AacGradeProcedHospitalar.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.eq(AacGradeProcedHospitalar.Fields.PHI.toString()+"."+FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<AacGradeProcedHospitalar> listarProcedimentosGradeTodasSituacoes(Integer grdSeq, Integer phiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeProcedHospitalar.class);
		criteria.add(Restrictions.eq(AacGradeProcedHospitalar.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.eq(AacGradeProcedHospitalar.Fields.PHI_SEQ.toString(), phiSeq));
		return executeCriteria(criteria);
	}	
	
	public Long obterCountListaProcedimentosGradePorGrdSeq(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeProcedHospitalar.class);
		criteria.add(Restrictions.eq(AacGradeProcedHospitalar.Fields.GRD_SEQ.toString(), grdSeq));
		return executeCriteriaCount(criteria);
	}

}