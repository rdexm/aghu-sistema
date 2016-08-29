package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AinInternacaoJn;

public class AinInternacaoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinInternacaoJn> {

	private static final long serialVersionUID = -2010081435436081534L;
	
	public List<AinInternacaoJn> verificaLeitoPossuiInternacaoExtornada(AinLeitosVO leito) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacaoJn.class);	
		criteria.add(Restrictions.eq(AinInternacaoJn.Fields.LTO_LTO_ID.toString(), leito.getLeitoID()));	
		return this.executeCriteria(criteria);

	}


}
