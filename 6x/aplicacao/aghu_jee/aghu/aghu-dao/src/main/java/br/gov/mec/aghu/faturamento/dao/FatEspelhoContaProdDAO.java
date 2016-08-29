package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoContaProd;

public class FatEspelhoContaProdDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatEspelhoContaProd> {

	private static final long serialVersionUID = 1858787512685899309L;

	public FatEspelhoContaProd obterPorContaHospitalar(FatContasHospitalares contaHospitalar) {

		if (contaHospitalar != null) {
			
			DetachedCriteria criteria = DetachedCriteria
					.forClass(FatEspelhoContaProd.class);
						
			criteria.add(Restrictions.eq(
					FatEspelhoContaProd.Fields.CONTA_HOSPITALAR.toString(), contaHospitalar));

			List<FatEspelhoContaProd> espelhoContaProd = this.executeCriteria(criteria);
			
			if(espelhoContaProd != null && !espelhoContaProd.isEmpty()){
				return espelhoContaProd.get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	public FatEspelhoContaProd obterContaHospitalar(FatContasHospitalares contaHospitalar) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatEspelhoContaProd.class);

		if (contaHospitalar != null) {
			criteria.add(Restrictions.eq(
					FatEspelhoContaProd.Fields.CONTA_HOSPITALAR.toString(),
					contaHospitalar));
			return (FatEspelhoContaProd) this
					.executeCriteriaUniqueResult(criteria);
		} else {
			return null;
		}
	}

}
