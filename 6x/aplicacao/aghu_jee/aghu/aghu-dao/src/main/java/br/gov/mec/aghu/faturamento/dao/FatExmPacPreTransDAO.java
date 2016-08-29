package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatExmPacPreTrans;
import br.gov.mec.aghu.model.FatListaPacApac;

public class FatExmPacPreTransDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatExmPacPreTrans> {

	private static final long serialVersionUID = 8908129323020547609L;

	public List<FatExmPacPreTrans> obterFatExmPacPreTransPorPacienteETipoTratamento(final Integer pacCodigo, final Integer tptSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatExmPacPreTrans.class,"EPP");
		criteria.createAlias(FatExmPacPreTrans.Fields.FAT_LISTA_PAC_APAC.toString(), "LPP");
		
		criteria.add(Restrictions.eq("EPP."+FatExmPacPreTrans.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("LPP."+FatListaPacApac.Fields.TPT_SEQ.toString(), tptSeq));
		criteria.addOrder(Order.desc("EPP."+FatExmPacPreTrans.Fields.DATA_REALIZACAO.toString()));
		
		return executeCriteria(criteria);
	}
}