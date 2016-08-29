package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioGrupoMbcPosicionamento;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaPosicionamento;
import br.gov.mec.aghu.model.MbcPosicionamento;

public class MbcFichaPosicionamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaPosicionamento> {

	private static final long serialVersionUID = -937519787435528107L;

	public List<MbcFichaPosicionamento> pesquisarMbcFichaPosicionamentosByFichaAnestesia(
			Long seqMbcFichaAnestesia, Boolean posicionamentoFicha,
			DominioGrupoMbcPosicionamento grupoMbcPosionamento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaPosicionamento.class);
		criteria.createAlias(MbcFichaPosicionamento.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaPosicionamento.Fields.MBC_POSICIONAMENTOS.toString(), "poi");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		if(posicionamentoFicha != null){
			criteria.add(Restrictions.eq(MbcFichaPosicionamento.Fields.SELECIONADO.toString(), posicionamentoFicha));
		}
		
		if(grupoMbcPosionamento != null){
			criteria.add(Restrictions.eq("poi." + MbcPosicionamento.Fields.GRUPO.toString(), grupoMbcPosionamento));
		}
		
		criteria.addOrder(Order.asc("poi." + MbcPosicionamento.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
		
	}
}