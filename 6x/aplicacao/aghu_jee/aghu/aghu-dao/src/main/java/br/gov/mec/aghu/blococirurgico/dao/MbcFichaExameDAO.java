package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaExame;

public class MbcFichaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaExame> {

	private static final long serialVersionUID = -2938973947600201232L;

	public List<MbcFichaExame> pesquisarMbcFichasExamesComItemSolicitacaoExame(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaExame.class);
		
		criteria.createAlias(MbcFichaExame.Fields.AEL_ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias(MbcFichaExame.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		criteria.add(Restrictions.eq(MbcFichaExame.Fields.SITUACAO_EXAME.toString(), DominioSituacaoExame.R));
		
		criteria.addOrder(Order.asc("exa." + AelExames.Fields.DESCRICAO_USUAL.toString()));
		
		return executeCriteria(criteria);
	}





}
