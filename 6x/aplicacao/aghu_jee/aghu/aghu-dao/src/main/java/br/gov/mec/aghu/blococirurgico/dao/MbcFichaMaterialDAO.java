package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

public class MbcFichaMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaMaterial> {

	private static final long serialVersionUID = 1422770013573461564L;

	public List<MbcFichaMaterial> pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(
			Long seqMbcFichaAnestesia, Boolean materialNeuroEixo, Boolean materialViaAerea) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaMaterial.class);
		criteria.createAlias(MbcFichaMaterial.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaMaterial.Fields.SCO_MATERIAL.toString(), "mat");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		if(materialNeuroEixo != null){
			criteria.add(Restrictions.eq(MbcFichaMaterial.Fields.MATERIAL_NEURO_EIXO.toString(), materialNeuroEixo));
		}
		if(materialViaAerea != null){
			criteria.add(Restrictions.eq(MbcFichaMaterial.Fields.MATERIAL_VIA_AEREA.toString(), materialViaAerea));
		}
		
		criteria.addOrder(Order.asc("mat." + ScoMaterial.Fields.NOME.toString()));
		
		return  executeCriteria(criteria);
	}
}