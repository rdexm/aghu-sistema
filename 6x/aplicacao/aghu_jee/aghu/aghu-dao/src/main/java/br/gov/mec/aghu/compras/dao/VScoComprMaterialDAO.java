package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.VScoComprMaterialVO;
import br.gov.mec.aghu.model.VScoComprMaterial;

public class VScoComprMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VScoComprMaterial> {

	private static final long serialVersionUID = 7972996021421662015L;

	public VScoComprMaterialVO pesquisaUltimaEntrega(Integer codigoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VScoComprMaterial.class, "VMT");
		criteria.add(Restrictions.eq("VMT." + VScoComprMaterial.Fields.MAT_CODIGO.toString(), codigoMaterial));
		criteria.addOrder(Order.desc(VScoComprMaterial.Fields.DT_GERACAO_MOVTO.toString()));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VScoComprMaterial.Fields.MAT_CODIGO.toString()), VScoComprMaterialVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.DT_GERACAO_MOVTO.toString()), VScoComprMaterialVO.Fields.DT_GERACAO_MOVTO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.FRN_NUMERO.toString()), VScoComprMaterialVO.Fields.FRN_NUMERO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.AFN_NUMERO.toString()), VScoComprMaterialVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.DT_GERACAO_NR.toString()), VScoComprMaterialVO.Fields.DT_GERACAO_NR.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.SLC_NUMERO.toString()), VScoComprMaterialVO.Fields.SLC_NUMERO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.DT_ABERTURA_PROPOSTA.toString()), VScoComprMaterialVO.Fields.DT_ABERTURA_PROPOSTA.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.NRS_SEQ.toString()), VScoComprMaterialVO.Fields.NRS_SEQ.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.LCT_NUMERO.toString()), VScoComprMaterialVO.Fields.LCT_NUMERO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.VALOR.toString()), VScoComprMaterialVO.Fields.VALOR.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.QUANTIDADE.toString()), VScoComprMaterialVO.Fields.QUANTIDADE.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.FORMA_PAG.toString()), VScoComprMaterialVO.Fields.FORMA_PAG.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.CUSTO_UNITARIO.toString()), VScoComprMaterialVO.Fields.CUSTO_UNITARIO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.DFE_SEQ.toString()), VScoComprMaterialVO.Fields.DFE_SEQ.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.NRO_COMPLEMENTO.toString()), VScoComprMaterialVO.Fields.NRO_COMPLEMENTO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.DFE_NUMERO.toString()), VScoComprMaterialVO.Fields.DFE_NUMERO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.MCM_CODIGO.toString()), VScoComprMaterialVO.Fields.MCM_CODIGO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.NC_MCM_CODIGO.toString()), VScoComprMaterialVO.Fields.NC_MCM_CODIGO.toString());
		p.add(Projections.property(VScoComprMaterial.Fields.NC_NUMERO.toString()), VScoComprMaterialVO.Fields.NC_NUMERO.toString());

		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(VScoComprMaterialVO.class));
		
		List<VScoComprMaterialVO> lista = executeCriteria(criteria);
		
		if(!lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
}
