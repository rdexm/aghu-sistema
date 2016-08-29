package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaDefMonitorizacoes;
import br.gov.mec.aghu.model.MbcMonitorizacao;

public class MbcMonitorizacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMonitorizacao> {

	private static final long serialVersionUID = -4133859038318055457L;

	public List<MbcMonitorizacao> pesquisarMbcMonitorizacoesComFichaAnestesia(
			Long seqMbcFichaAnestesia, Boolean fichaDefMonitorado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMonitorizacao.class);
		
		criteria.createAlias(MbcMonitorizacao.Fields.FICHAS_DEF_MONITORIZACOES.toString(), "fdm");
		criteria.createAlias("fdm." + MbcFichaDefMonitorizacoes.Fields.FICHA_ANESTESIA.toString(), "fic");
		
		criteria.add(Restrictions.eq("fdm." + MbcFichaDefMonitorizacoes.Fields.MONITORADO.toString(), fichaDefMonitorado));
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc(MbcMonitorizacao.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}
}
