package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.model.VAbsMovimentoComponente;

public class VAbsMovimentoComponenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAbsMovimentoComponente> {

	
	private static final long serialVersionUID = 118357896326805352L;

	/**
	 * Metodo para listar as hemoterapias do paciente que possua indEstorno == false e mcoType == FPA, 
	 * atraves do codigo do paciente.
	 * @param pacCodigo
	 * @return
	 */
	public List<VAbsMovimentoComponente> listarHemoterapiasPaciente(Integer pacCodigo, Date dataBancoSangue) {
		
		DetachedCriteria criteria = obterCriteriaHemoterapiasPaciente(pacCodigo, dataBancoSangue);
		List<VAbsMovimentoComponente> lista = executeCriteria(criteria);
		
		
		return lista;
	}
	
	
	private DetachedCriteria obterCriteriaHemoterapiasPaciente(Integer pacCodigo,
			Date dataBancoSangue) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(VAbsMovimentoComponente.class);

		criteria.add(Restrictions.eq(
				VAbsMovimentoComponente.Fields.MCO_TYPE.toString(),
				DominioMcoType.FPA));
		criteria.add(Restrictions.eq(
				VAbsMovimentoComponente.Fields.IND_ESTORNO.toString(),
				Boolean.FALSE));
		criteria.add(Restrictions.eq(
				VAbsMovimentoComponente.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(
				VAbsMovimentoComponente.Fields.DTHR_MOVIMENTO.toString(),
				dataBancoSangue));
		
		return criteria;
	}
	
	public boolean verificarExisteHemoterapiasPaciente (Integer pacCodigo, Date dataBancoSangue){
		DetachedCriteria criteria = obterCriteriaHemoterapiasPaciente(pacCodigo, dataBancoSangue);
		
		return executeCriteriaExists(criteria);
	}

}
