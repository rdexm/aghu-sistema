package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOcorrFichaFluido;
import br.gov.mec.aghu.model.MbcAdministraFichaFluido;
import br.gov.mec.aghu.model.MbcFichaFluidoAdministrados;
import br.gov.mec.aghu.model.MbcOcorrFichaFluidoAdms;

public class MbcOcorrFichaFluidoAdmsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcOcorrFichaFluidoAdms> {

	private static final long serialVersionUID = -2286859790148191476L;

	public MbcOcorrFichaFluidoAdms obterMbcOcorrFichaFluidoAdm(
			Integer seqMbcFichaFluidoAdm,
			DominioOcorrFichaFluido ocorrFichaFluido, Boolean permaneceNoPos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrFichaFluidoAdms.class);
		criteria.createAlias(MbcOcorrFichaFluidoAdms.Fields.ADMINISTRA_FICA_FLUIDO.toString(), "aff");
		criteria.createAlias("aff." + MbcAdministraFichaFluido.Fields.FICHA_FLUIDO_ADMINISTRADO.toString(), "ffd");
		
		criteria.add(Restrictions.eq("ffd." + MbcFichaFluidoAdministrados.Fields.SEQ.toString(), seqMbcFichaFluidoAdm));
		criteria.add(Restrictions.eq(MbcOcorrFichaFluidoAdms.Fields.TIPO.toString(), ocorrFichaFluido));
		criteria.add(Restrictions.eq(MbcOcorrFichaFluidoAdms.Fields.PERMANECE_NO_POS.toString(), permaneceNoPos));
		
		
		List<MbcOcorrFichaFluidoAdms> adms = executeCriteria(criteria);
		if(adms != null && !adms.isEmpty()){
			return adms.get(0);
		}
		return null;
	}


}
