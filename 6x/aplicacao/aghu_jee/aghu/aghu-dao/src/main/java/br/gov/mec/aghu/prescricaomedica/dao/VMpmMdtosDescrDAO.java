package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMpmMdtosDescrDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmMdtosDescr> {
	
	private static final long serialVersionUID = 4402838390901377386L;

	public VMpmMdtosDescr obterVMpmMdtosDescrPorMedicamento(AfaMedicamento medicamento){
		VMpmMdtosDescr retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmMdtosDescr.class);
		
		criteria.setProjection(Projections.projectionList()
									.add(Projections.property(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()), "descricaoEdit")
									.add(Projections.property(VMpmMdtosDescr.Fields.MAT_CODIGO.toString()), "matCodigo")
									.add(Projections.property(VMpmMdtosDescr.Fields.TPR_SIGLA.toString()), "tprSigla")
									.add(Projections.property("id.descricao"), "descricao")
									.add(Projections.property("id.ummSeq"), "ummSeq")
				);
		
		criteria.add(Restrictions.eq(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		criteria.setResultTransformer(Transformers.aliasToBean(VMpmMdtosDescr.class));
		List<VMpmMdtosDescr> listaView = executeCriteria(criteria);
		if (listaView.size() > 0){
			retorno = listaView.get(0);
		}
		return retorno;
	}
	
	public List<VMpmMdtosDescr> obterSuggestionMedicamento(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmMdtosDescr.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()), "descricaoEdit")
				.add(Projections.property(VMpmMdtosDescr.Fields.MAT_CODIGO.toString()), "matCodigo")
				.add(Projections.property(VMpmMdtosDescr.Fields.TPR_SIGLA.toString()), "tprSigla")
				.add(Projections.property("id.descricao"), "descricao")
				.add(Projections.property("id.ummSeq"), "ummSeq")
		);
		
		if(StringUtils.isNotBlank(strPesquisa)){
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.or(Restrictions.eq(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), Integer.valueOf(strPesquisa))
						,Restrictions.ilike(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString(), strPesquisa, MatchMode.ANYWHERE)
						,Restrictions.ilike(VMpmMdtosDescr.Fields.TPR_SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE)));	
			}else{
				criteria.add(Restrictions.or(
						Restrictions.ilike(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString(), strPesquisa, MatchMode.ANYWHERE), 
						Restrictions.ilike(VMpmMdtosDescr.Fields.TPR_SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}
		

		criteria.addOrder(Order.asc(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VMpmMdtosDescr.class));
		return executeCriteria(criteria,0,100,null,false);
	}

}
