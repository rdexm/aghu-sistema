package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcEspecificacaoIntubacoes;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEspecifIntubacoes;

public class MbcFichaEspecifIntubacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaEspecifIntubacoes> {

	private static final long serialVersionUID = -6033633303096585957L;

	public List<MbcFichaEspecifIntubacoes> pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(
			Long seqMbcFichaAnestesia, String tipoEspecificacaoIntubacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaEspecifIntubacoes.class);
		criteria.createAlias(MbcFichaEspecifIntubacoes.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		
		criteria.createAlias(MbcFichaEspecifIntubacoes.Fields.MBC_ESPECIFICACAO_INTUBACOES.toString(), "eit");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		criteria.add(Restrictions.eq("eit." + MbcEspecificacaoIntubacoes.Fields.TIPO.toString(), tipoEspecificacaoIntubacao));
		
		criteria.addOrder(Order.asc("eit." + MbcEspecificacaoIntubacoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
}