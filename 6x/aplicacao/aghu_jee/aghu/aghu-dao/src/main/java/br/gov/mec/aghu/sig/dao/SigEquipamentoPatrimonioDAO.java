package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcMaterialPorCirurgia;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigEquipamentoPatrimonio;
import br.gov.mec.aghu.sig.custos.vo.MaterialCirurgiaVO;

public class SigEquipamentoPatrimonioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigEquipamentoPatrimonio> {

	private static final long serialVersionUID = -4608875100079120106L;

	public List<SigEquipamentoPatrimonio> pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio(String equipamentoCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigEquipamentoPatrimonio.class);
		criteria.createAlias(SigEquipamentoPatrimonio.Fields.EQUIPAMENTO_CIRURGICO.toString(), "eqc");
		criteria.add(Restrictions.eq(SigEquipamentoPatrimonio.Fields.CODIGO_PATRIMONIO.toString(), equipamentoCodigo));
		return this.executeCriteria(criteria);
	}
  
	public List<SigEquipamentoPatrimonio> buscaEquipametosCirurgicos(MbcEquipamentoCirurgico equipamentoCirurgico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigEquipamentoPatrimonio.class);
		criteria.add(Restrictions.eq(SigEquipamentoPatrimonio.Fields.EQUIPAMENTO_CIRURGICO.toString(), equipamentoCirurgico));
		return this.executeCriteria(criteria);

	}
	

	/**
	 * Efetua a busca de todos os materiais utilizados numa determinada cirurgia.
	 * @param cirurgias - Seq da cirurgia
	 * @return List<{@link MaterialCirurgiaVO}> 
	 * @author jgugel
	 */
	public List<MaterialCirurgiaVO> buscarMateriaisCirurgia(Integer cirurgias){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialPorCirurgia.class, "mns");
		
		criteria.createAlias("mns."+MbcMaterialPorCirurgia.Fields.SCO_MATERIAL, "mat", Criteria.LEFT_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.PROCED_HOSP_INTERNO, "phi", Criteria.LEFT_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.CONVERSAO_UNIDADE_CONSUMO, "cud", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("mns."+MbcMaterialPorCirurgia.Fields.MBC_CIRURGIAS.toString()+"."+MbcCirurgias.Fields.SEQ.toString(), cirurgias));
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("mat."+ScoMaterial.Fields.CODIGO.toString()), MaterialCirurgiaVO.Fields.MATERIAL_CODIGO.toString() )
		.add(Projections.property("phi."+FatProcedHospInternos.Fields.SEQ.toString()), MaterialCirurgiaVO.Fields.PHI.toString() )
		.add(Projections.sum("mns."+MbcMaterialPorCirurgia.Fields.QUANTIDADE.toString()), MaterialCirurgiaVO.Fields.SOMATORIO.toString() )
		.add(Projections.property("cud."+SceConversaoUnidadeConsumos.Fields.FATOR_CONVERSAO.toString()), MaterialCirurgiaVO.Fields.FATOR_CONVERSAO.toString() )
	
		.add(Projections.groupProperty("mat."+ScoMaterial.Fields.CODIGO.toString()), MaterialCirurgiaVO.Fields.MATERIAL_CODIGO.toString())
		.add(Projections.groupProperty("phi."+FatProcedHospInternos.Fields.SEQ.toString()), MaterialCirurgiaVO.Fields.MATERIAL_CODIGO.toString() )
		.add(Projections.groupProperty("cud."+SceConversaoUnidadeConsumos.Fields.FATOR_CONVERSAO.toString()), MaterialCirurgiaVO.Fields.MATERIAL_CODIGO.toString() );
				
		criteria.setProjection(projection);
		
		criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("phi."+FatProcedHospInternos.Fields.SEQ.toString()));
		
		List<Object[]> retorno = this.executeCriteria(criteria);
		List<MaterialCirurgiaVO> listMaterial = new ArrayList<MaterialCirurgiaVO>();
		
		for (Object[] objects : retorno) {
			listMaterial.add(new MaterialCirurgiaVO(objects));
		}
		return listMaterial;
	}


}
