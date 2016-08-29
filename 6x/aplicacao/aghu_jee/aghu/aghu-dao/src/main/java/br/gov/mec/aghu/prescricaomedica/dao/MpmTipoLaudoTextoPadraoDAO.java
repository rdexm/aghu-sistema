package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadrao;

public class MpmTipoLaudoTextoPadraoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoLaudoTextoPadrao> {

	private static final long serialVersionUID = -850443755122516920L;

	public List<MpmTipoLaudoTextoPadrao> pesquisarTipoLaudoTextoPadrao(Short tipoLaudoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoLaudoTextoPadrao.class);
		
		criteria.createAlias(MpmTipoLaudoTextoPadrao.Fields.TIPO_LAUDO.toString(), "MTL");
		criteria.createAlias(MpmTipoLaudoTextoPadrao.Fields.TEXTO_PADRAO_LAUDO.toString(), "TPL");
		
		criteria.add(Restrictions.eq("MTL." + MpmTipoLaudo.Fields.SEQ.toString(), tipoLaudoSeq));
		
		return this.executeCriteria(criteria);
	}
	
}
