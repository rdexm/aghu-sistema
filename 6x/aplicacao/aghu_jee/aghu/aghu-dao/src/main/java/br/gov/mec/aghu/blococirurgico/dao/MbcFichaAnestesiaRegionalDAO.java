package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcFichaAnestesiaRegional;
import br.gov.mec.aghu.model.MbcFichaAnestesias;

public class MbcFichaAnestesiaRegionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaAnestesiaRegional> {

	private static final long serialVersionUID = 8912556655029952151L;

	public List<MbcFichaAnestesiaRegional> pesquisarMbcFichaAnestesiaRegionalByFichaAnestesia(
			Long seqMbcFichaAnestesia, Boolean ivRegional,
			Boolean bloqueioNervoPlexo, Boolean intercostais, Boolean neuroeixo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaAnestesiaRegional.class);
		criteria.createAlias(MbcFichaAnestesiaRegional.Fields.FICHA_ANESTESIA.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		Disjunction or = Restrictions.disjunction();
		//Caso seja enviado null, estes params não são testados
		if (ivRegional != null) {
			or.add(Restrictions.eq(MbcFichaAnestesiaRegional.Fields.IV_REGIONAL.toString(), DominioSimNao.getInstance(ivRegional).name()));
		}
		
		if (bloqueioNervoPlexo != null) {
			or.add(Restrictions.eq(MbcFichaAnestesiaRegional.Fields.BLOQUEIO_NERVO_PLEXO.toString(), DominioSimNao.getInstance(bloqueioNervoPlexo).name()));
		}
		
		if (intercostais != null) {
			or.add(Restrictions.eq(MbcFichaAnestesiaRegional.Fields.INTERCOSTAIS.toString(), DominioSimNao.getInstance(intercostais).name()));
		}
		
		if (neuroeixo != null) {
			or.add(Restrictions.eq(MbcFichaAnestesiaRegional.Fields.NEUROEIXO.toString(), DominioSimNao.getInstance(neuroeixo).name()));
		}
		
		criteria.add(or);
		
		return  executeCriteria(criteria);
		
	}




}
