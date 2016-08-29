package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.view.VScoPacientesCUM;

/**
 * 
 * @modulo compras
 *
 */
public class VScoPacientesCUMDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VScoPacientesCUM>{
		


	/**
	 * 
	 */
	private static final long serialVersionUID = 4916793578530524924L;	
	

	public List<VScoPacientesCUM> pesquisarPacientesCUMPorAFeAFP(Integer afeAfnNumero, Integer afeNumero){

		DetachedCriteria criteria = DetachedCriteria.forClass(VScoPacientesCUM.class, "VCUM");
		
		
		criteria.add(Restrictions.eq(VScoPacientesCUM.Fields.AFE_AFN_NUMERO.toString(), afeAfnNumero));
		criteria.add(Restrictions.eq(VScoPacientesCUM.Fields.AFE_NUMERO.toString(), afeNumero));
		
				
		return executeCriteria(criteria);
		
	}
	
}