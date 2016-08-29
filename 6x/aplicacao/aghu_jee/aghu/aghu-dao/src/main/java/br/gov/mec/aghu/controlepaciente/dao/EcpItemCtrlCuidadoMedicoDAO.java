package br.gov.mec.aghu.controlepaciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoMedico;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
public class EcpItemCtrlCuidadoMedicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpItemCtrlCuidadoMedico> {

	private static final long serialVersionUID = -1313255179895724927L;

	public List<MpmCuidadoUsual> obterItensCuidadoMedico(
			EcpItemControle itemControle) {

		DetachedCriteria criteria = this.obterCriteria();
		criteria.setProjection(Projections
				.property(EcpItemCtrlCuidadoMedico.Fields.MPM_CUIDADO_USUAL
						.toString()));
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoMedico.Fields.ECP_ITEM_CONTROLE.toString(),
				itemControle));
		return executeCriteria(criteria);

	}
	
	public EcpItemCtrlCuidadoMedico obterCuidadoMedicoPorItemControleECuidado(
			EcpItemControle itemControle, MpmCuidadoUsual cuidado) {

		DetachedCriteria criteria = this.obterCriteria();
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoMedico.Fields.ECP_ITEM_CONTROLE.toString(),
				itemControle));
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoMedico.Fields.MPM_CUIDADO_USUAL.toString(),
				cuidado));

		List<EcpItemCtrlCuidadoMedico> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null;
	}
	
	public Long obterNumeroRegistrosItemPorControle(EcpItemControle itemControle) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpItemCtrlCuidadoMedico.class);
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoMedico.Fields.ECP_ITEM_CONTROLE.toString()+"."+EcpItemControle.Fields.SEQ.toString(),
				itemControle.getSeq()));
		return executeCriteriaCount(criteria);
	}	
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(EcpItemCtrlCuidadoMedico.class);
	}
	
}
