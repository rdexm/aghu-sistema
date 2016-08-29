package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoInducaoManutencao;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaInducaoManutencao;
import br.gov.mec.aghu.model.MbcInducaoManutencao;

public class MbcFichaInducaoManutencaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaInducaoManutencao> {

	private static final long serialVersionUID = -7222991085282784511L;

	public List<MbcFichaInducaoManutencao> pesquisarMbcInducaoManutencaoByFichaAnestesia(
			Long seqMbcFichaAnestesia, DominioTipoInducaoManutencao tipoInducaoManutencao, Boolean fichaInducaoManutSelecionado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaInducaoManutencao.class);
		criteria.createAlias(MbcFichaInducaoManutencao.Fields.MBC_INDUCAO_MANUTENCOES.toString(), "inm");
		criteria.createAlias(MbcFichaInducaoManutencao.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		if(fichaInducaoManutSelecionado != null){
			criteria.add(Restrictions.eq(MbcFichaInducaoManutencao.Fields.SELECIONADO.toString(), fichaInducaoManutSelecionado));
		}
		
		if(tipoInducaoManutencao != null){
			criteria.add(Restrictions.eq("inm." + MbcInducaoManutencao.Fields.TIPO.toString(), tipoInducaoManutencao));
		}
		
		criteria.addOrder(Order.asc("inm." + MbcInducaoManutencao.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
		
	}




}
