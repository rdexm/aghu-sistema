package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaBloqNervoPlexos;
import br.gov.mec.aghu.model.MbcNervoPlexos;

public class MbcFichaBloqNervoPlexosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaBloqNervoPlexos> {

	private static final long serialVersionUID = 8408022674046179670L;

	public List<MbcFichaBloqNervoPlexos> pesquisarMbcFichaBloqNervoPlexosByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaBloqNervoPlexos.class);
		criteria.createAlias(MbcFichaBloqNervoPlexos.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaBloqNervoPlexos.Fields.MBC_TECN_BLOQ_NERVO_PLEXOS.toString(), "tbn", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcFichaBloqNervoPlexos.Fields.MBC_NERVO_PLEXOS.toString(), "npx", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc("npx." + MbcNervoPlexos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(MbcFichaBloqNervoPlexos.Fields.OUTRO_NERVO.toString()));
		
		return  executeCriteria(criteria);
	}




}
