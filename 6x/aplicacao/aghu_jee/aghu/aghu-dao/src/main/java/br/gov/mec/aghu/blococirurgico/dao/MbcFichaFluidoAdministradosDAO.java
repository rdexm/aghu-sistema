package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoFluidoAdministrado;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaFluidoAdministrados;
import br.gov.mec.aghu.model.MbcFluidoAdministrados;

public class MbcFichaFluidoAdministradosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaFluidoAdministrados> {

	private static final long serialVersionUID = -271582269066893147L;

	public List<MbcFichaFluidoAdministrados> pesquisarMbcFichaFluidoAdministrado(Long seqMbcFichaAnest,
			DominioTipoFluidoAdministrado tipoFluidoAdministrado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFluidoAdministrados.class);
		
		criteria.createAlias(MbcFichaFluidoAdministrados.Fields.FICHA_ANESTESIA.toString(), "fic");
		criteria.createAlias(MbcFichaFluidoAdministrados.Fields.FLUIDO_ADMINISTRADO.toString(), "fla");
		criteria.createAlias("fla." + MbcFluidoAdministrados.Fields.MEDICAMENTO.toString(), "med", Criteria.LEFT_JOIN);
		criteria.createAlias("fla." + MbcFluidoAdministrados.Fields.COMPONENTE_SANGUINEO.toString(), "csa", Criteria.LEFT_JOIN);
		criteria.createAlias("fla." + MbcFluidoAdministrados.Fields.PROCED_ESPECIAL_DIVERSO.toString(), "ped", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		
		if(tipoFluidoAdministrado != null){
			criteria.add(Restrictions.eq("fla." + MbcFluidoAdministrados.Fields.TIPO.toString(), tipoFluidoAdministrado));
		}
		criteria.addOrder(Order.asc(MbcFichaFluidoAdministrados.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
		
	}

	public Long obterSomaVolumeTotalFluidoAdministrado(
			Long seqMbcFichaAnest, Boolean agrupaTipoFluidoAdm) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFluidoAdministrados.class);
		
		criteria.setProjection(Projections.sum(MbcFichaFluidoAdministrados.Fields.VOLUME_TOTAL.toString()));
		criteria.createAlias(MbcFichaFluidoAdministrados.Fields.FLUIDO_ADMINISTRADO.toString() , "fla");
		criteria.createAlias(MbcFichaFluidoAdministrados.Fields.FICHA_ANESTESIA.toString() , "fic");
		
		if(Boolean.TRUE.equals(agrupaTipoFluidoAdm)){//se null não aplica esta restriction
			criteria.add(Restrictions.in("fla." + MbcFluidoAdministrados.Fields.TIPO.toString(), Arrays.asList(DominioTipoFluidoAdministrado.CR, DominioTipoFluidoAdministrado.CO)));
		}else if(Boolean.FALSE.equals(agrupaTipoFluidoAdm)){
			criteria.add(Restrictions.in("fla." + MbcFluidoAdministrados.Fields.TIPO.toString(), Arrays.asList(DominioTipoFluidoAdministrado.HE)));
		}
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}


}
