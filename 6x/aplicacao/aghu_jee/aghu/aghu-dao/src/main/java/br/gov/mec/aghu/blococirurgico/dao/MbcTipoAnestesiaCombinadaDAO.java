package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcTipoAnestesiaCombinada;
import br.gov.mec.aghu.model.MbcTipoAnestesias;

public class MbcTipoAnestesiaCombinadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTipoAnestesiaCombinada> {

	private static final long serialVersionUID = 7666727219920442639L;

	public List<MbcTipoAnestesiaCombinada> listarTiposAnestesiaCombinadas(final MbcTipoAnestesias tipoAnestesia){

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesiaCombinada.class);
		criteria.createAlias(MbcTipoAnestesiaCombinada.Fields.MBC_TIPO_ANESTESIAS_BY_TAN_SEQ_COMBINA.toString(), "C", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcTipoAnestesiaCombinada.Fields.MBC_TIPO_ANESTESIAS_BY_TAN_SEQ.toString(), "S", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("S."+MbcTipoAnestesias.Fields.SEQ.toString(), tipoAnestesia.getSeq()));

		return executeCriteria(criteria);
	}
}