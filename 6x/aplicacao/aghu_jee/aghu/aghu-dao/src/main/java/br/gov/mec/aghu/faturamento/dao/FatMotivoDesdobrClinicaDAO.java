package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;

public class FatMotivoDesdobrClinicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoDesdobrClinica>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1009058221111345816L;

	/**
	 * Pesquisa os registros de FatMotivoDesdobrClinica associados a uma clínica
	 * @param clinica
	 * @return
	 */
	public List<FatMotivoDesdobrClinica> pesquisarMotivoDesdobrClinicaPorClinica(AghClinicas clinica){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoDesdobrClinica.class);
		criteria.add(Restrictions.eq(FatMotivoDesdobrClinica.Fields.CLC_CODIGO.toString(), clinica.getCodigo()));
		return this.executeCriteria(criteria);
	}

}
