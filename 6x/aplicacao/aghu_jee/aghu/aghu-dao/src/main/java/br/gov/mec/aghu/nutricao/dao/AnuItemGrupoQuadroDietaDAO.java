package br.gov.mec.aghu.nutricao.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AnuGrupoQuadroDieta;
import br.gov.mec.aghu.model.AnuHabitoAlimUsual;
import br.gov.mec.aghu.model.AnuItemGrupoQuadroDieta;
import br.gov.mec.aghu.model.AnuRefeicao;


public class AnuItemGrupoQuadroDietaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AnuItemGrupoQuadroDieta> {

	private static final String IGD = "igd.";
	private static final long serialVersionUID = -9045096406271974214L;
	
	/**
	 * Busca grupo da dieta por refeição
	 * @param codigoRefeicao 
	 * @return Item e grupo que fazem parte da refeição
	 * @author jgugel
	 */
	public AnuItemGrupoQuadroDieta buscaGrupoDietaPorRefeicao(Short codigoRefeicao){
		return this.efetuaBuscaDietaPorRefeicaoOuDietaHabitoAlimentar(codigoRefeicao, true);
	}
	
	/**
	 * Busca grupo da dieta por hábito alimentar usual
	 * @param codigoHabitoAlimentar 
	 * @return Item e grupo que fazem parte do codigo alimentar
	 * @author jgugel
	 */
	public AnuItemGrupoQuadroDieta buscaGrupoDietaHabitoAlimentar(Short codigoHabitoAlimentar){
		return this.efetuaBuscaDietaPorRefeicaoOuDietaHabitoAlimentar(codigoHabitoAlimentar, false);
	}
	
	private AnuItemGrupoQuadroDieta efetuaBuscaDietaPorRefeicaoOuDietaHabitoAlimentar(Short codigo, boolean isBuscaDietaPorRefeicao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AnuItemGrupoQuadroDieta.class, "igd");
		criteria.createCriteria(IGD+AnuItemGrupoQuadroDieta.Fields.ANU_REFEICOES , "ref", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(IGD+AnuItemGrupoQuadroDieta.Fields.ANU_GRUPO_QUADRO_DIETAS , "ggd", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(IGD+AnuItemGrupoQuadroDieta.Fields.IND_SITUACAO, "A"));
		criteria.add(Restrictions.eq("ggd."+AnuGrupoQuadroDieta.Fields.IND_SITUACAO, "A"));	
		if(isBuscaDietaPorRefeicao){
			criteria.add(Restrictions.eq(IGD+AnuItemGrupoQuadroDieta.Fields.ANU_REFEICOES+"."+AnuRefeicao.Fields.SEQ.toString(), codigo));
		}else{
			criteria.add(Restrictions.eq(IGD+AnuItemGrupoQuadroDieta.Fields.ANU_HABITO_ALIM_USUAIS+"."+AnuHabitoAlimUsual.Fields.SEQ, codigo));
		}
		return (AnuItemGrupoQuadroDieta) executeCriteriaUniqueResult(criteria);

	}
	
}
