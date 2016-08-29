package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaTecnicaEspecial;

public class MbcFichaTecnicaEspecialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaTecnicaEspecial> {

	private static final long serialVersionUID = -3797128766572088962L;

	public List<MbcFichaTecnicaEspecial> pesquisarMbcFichasTecnicasEspeciaisComTecnicaEspecial(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaTecnicaEspecial.class);
		criteria.createAlias(MbcFichaTecnicaEspecial.Fields.FICHA_ANESTESIA.toString(), "fic");
		criteria.createAlias(MbcFichaTecnicaEspecial.Fields.TECNOCA_ESPECIAL.toString(), "tnp");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ, seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}

}
