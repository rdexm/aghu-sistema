package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelControleNumeroMapa;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AelControleNumeroMapaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelControleNumeroMapa> {
	
	private static final long serialVersionUID = -930740044565401151L;

	public List<AelControleNumeroMapa> obterPorDataNumeroUnicoEOrigem(final AelConfigMapa aelConfigMapa, final Date dtEmissao){
		
		DetachedCriteria criteria =  DetachedCriteria.forClass(AelControleNumeroMapa.class);
		
		criteria.add(Restrictions.eq(AelControleNumeroMapa.Fields.AEL_CONFIG_MAPAS.toString(), aelConfigMapa));
		criteria.add(Restrictions.between(AelControleNumeroMapa.Fields.DT_EMISSAO_MAPA.toString(), DateUtil.truncaData(dtEmissao), DateUtil.truncaDataFim(dtEmissao)));
		
		/*
		 select nvl(ult_mapa_impresso,0) ,nvl(ult_nro_interno,0),rowid
		 from ael_controle_numero_mapas
		 where cgm_seq              = c_cgm_seq
		 and trunc(dt_emissao_mapa) = trunc(c_dt_emissao_mapa)
		 for update of ult_mapa_impresso ,ult_nro_interno;
		 */
		
		
		return executeCriteria(criteria);
	}

}
