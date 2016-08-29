package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoFluidoPerdido;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaFluidoPerdido;
import br.gov.mec.aghu.model.MbcFluidoPerdido;

public class MbcFichaFluidoPerdidoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaFluidoPerdido> {

	private static final long serialVersionUID = 4198959305416492331L;

	public List<MbcFichaFluidoPerdido> pesquisarFichasFluidosPerdidosByFichaAnestesia(
			Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFluidoPerdido.class);
		criteria.createAlias(MbcFichaFluidoPerdido.Fields.FICHA_ANESTESIA.toString(), "fic");
		criteria.createAlias(MbcFichaFluidoPerdido.Fields.FLUIDO_PERDIDO.toString() , "fdd");
		
		if(tipoFluidoPerdido != null){
			criteria.add(Restrictions.eq("fdd." + MbcFluidoPerdido.Fields.TIPO.toString(), tipoFluidoPerdido));
		}
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		
		return executeCriteria(criteria);
	}

	public Long obterSomaVolumeTotalFluidoPerdido(Long seqMbcFichaAnest,
			DominioTipoFluidoPerdido tipoFluidoPerdido) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFluidoPerdido.class);
		
		criteria.setProjection(Projections.sum(MbcFichaFluidoPerdido.Fields.VOLUME_TOTAL.toString()));
		criteria.createAlias(MbcFichaFluidoPerdido.Fields.FLUIDO_PERDIDO.toString() , "fdd");
		criteria.add(Restrictions.eq("fdd." + MbcFluidoPerdido.Fields.TIPO.toString(), tipoFluidoPerdido));
		criteria.add(Restrictions.eq(MbcFichaFluidoPerdido.Fields.SEQ_FICHA_ANESTESIA.toString(), seqMbcFichaAnest));
		
		return (Long) executeCriteriaUniqueResult(criteria);
		
	}


}
