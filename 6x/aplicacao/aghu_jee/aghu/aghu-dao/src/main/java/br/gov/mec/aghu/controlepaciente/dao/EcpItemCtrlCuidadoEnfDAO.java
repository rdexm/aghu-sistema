package br.gov.mec.aghu.controlepaciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoEnf;
import br.gov.mec.aghu.model.EpeCuidados;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
public class EcpItemCtrlCuidadoEnfDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpItemCtrlCuidadoEnf> {

	private static final long serialVersionUID = 120888556361813827L;

	public List<EpeCuidados> obterItensCuidadoEnfermagem(
			EcpItemControle itemControle) {

		DetachedCriteria criteria = obterCriteria();
		criteria.setProjection((Projections
				.property(EcpItemCtrlCuidadoEnf.Fields.EPE_CUIDADOS.toString())));
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoEnf.Fields.ECP_ITEM_CONTROLE.toString(),
				itemControle));

		return executeCriteria(criteria);
	}

	public EcpItemCtrlCuidadoEnf obterCuidadoEnfPorItemControleECuidado(
			EcpItemControle itemControle, EpeCuidados cuidado) {

		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoEnf.Fields.ECP_ITEM_CONTROLE.toString(),
				itemControle));
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoEnf.Fields.EPE_CUIDADOS.toString(), cuidado));

		List<EcpItemCtrlCuidadoEnf> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null;

	}	
	
	public Long obterNumeroRegistrosItemPorControle(EcpItemControle itemControle) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpItemCtrlCuidadoEnf.class);
		criteria.add(Restrictions.eq(
				EcpItemCtrlCuidadoEnf.Fields.ECP_ITEM_CONTROLE.toString()+"."+EcpItemControle.Fields.SEQ.toString(),
				itemControle.getSeq()));
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(EcpItemCtrlCuidadoEnf.class);
	}

}
