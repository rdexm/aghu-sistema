package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.RapServidores;


public class AelExtratoItemCartasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoItemCartas> {
	
		
	private static final long serialVersionUID = 2419993118977740083L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemCartas.class);
		return criteria;
    }

	public List<AelExtratoItemCartas> buscarAelExtratoItemCartasPorItemCartas(
			AelItemSolicCartas aelItemSolicCartas) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoItemCartas.Fields.AEL_ITEM_SOLIC_CARTAS.toString(), aelItemSolicCartas));
		
		return executeCriteria(dc);
	}

	public List<AelExtratoItemCartas> buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(
			AelItemSolicCartas aelItemSolicCartas) {
	
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoItemCartas.Fields.AEL_ITEM_SOLIC_CARTAS.toString(), aelItemSolicCartas));
		
		dc.setFetchMode(AelExtratoItemCartas.Fields.AEL_MOTIVO_RETORNO.toString(), FetchMode.JOIN);
		dc.setFetchMode(AelExtratoItemCartas.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		
		dc.setFetchMode(AelExtratoItemCartas.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);		
		
		return executeCriteria(dc);
	}
}
