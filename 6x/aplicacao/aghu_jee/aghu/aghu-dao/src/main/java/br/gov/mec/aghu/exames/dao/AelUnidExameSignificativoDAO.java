package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnidExameSignificativo;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link AelUnidExameSignificativo}
 * 
 * @author luismoura
 * 
 */
public class AelUnidExameSignificativoDAO extends BaseDao<AelUnidExameSignificativo> {
	private static final long serialVersionUID = 3911091534654454788L;

	private final static String UES = "UES";
	private final static String EMA = "EMA";
	private final static String UNF = "UNF";
	private final static String MAN = "MAN";
	private final static String EXA = "EXA";

	/**
	 * Utilizado para auxiliar a montagem de prefixos nas queries
	 * 
	 * @param prefixo
	 * @param field
	 * @return
	 */
	private String prefixar(final String prefixo, final String field) {
		return new StringBuffer(prefixo).append('.').append(field).toString();
	}

	/**
	 * Criteria buscar os dados de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @return
	 */
	private DetachedCriteria montarCriteriaUnidadesFuncionaisExamesSignificativosPerinato(final Short unfSeq, final String siglaExame, final Integer seqMatAnls, final Boolean indCargaExame) {
		// FROM
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnidExameSignificativo.class, UES);
		// JOIN
		criteria.createAlias(prefixar(UES, AelUnidExameSignificativo.Fields.EXAME_MATERIAL_ANALISE.toString()), EMA);
		criteria.createAlias(prefixar(UES, AelUnidExameSignificativo.Fields.UNIDADE_FUNCIONAL.toString()), UNF);
		criteria.createAlias(prefixar(EMA, AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), MAN);
		criteria.createAlias(prefixar(EMA, AelExamesMaterialAnalise.Fields.EXAME.toString()), EXA);
		// WHERE
		criteria.add(Restrictions.eq(prefixar(EMA, AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		criteria.add(Restrictions.eq(prefixar(UNF, AghUnidadesFuncionais.Fields.SITUACAO.toString()), DominioSituacao.A));
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(prefixar(UNF, AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), unfSeq));
		}
		if (StringUtils.isNotBlank(siglaExame)) {
			criteria.add(Restrictions.eq(prefixar(EXA, AelExames.Fields.SIGLA.toString()), siglaExame));
		}
		if (seqMatAnls != null) {
			criteria.add(Restrictions.eq(prefixar(MAN, AelMateriaisAnalises.Fields.SEQ.toString()), seqMatAnls));
		}
		if (indCargaExame != null) {
			criteria.add(Restrictions.eq(prefixar(UES, AelUnidExameSignificativo.Fields.IND_CARGA_EXAME.toString()), indCargaExame));
		}

		return criteria;
	}

	/**
	 * Buscar os dados de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List<AelUnidExameSignificativo> pesquisarUnidadesFuncionaisExamesSignificativosPerinato(final Short unfSeq, final String siglaExame,
			final Integer seqMatAnls, final Boolean indCargaExame, final int firstResult, final int maxResults) {
		DetachedCriteria criteria = this.montarCriteriaUnidadesFuncionaisExamesSignificativosPerinato(unfSeq, siglaExame, seqMatAnls, indCargaExame);
		// ORDER
		criteria.addOrder(Order.asc(prefixar(UNF, AghUnidadesFuncionais.Fields.DESCRICAO.toString())));
		return super.executeCriteria(criteria, firstResult, maxResults, null, true);
	}

	/**
	 * Count de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @return
	 */
	public Long pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(final Short unfSeq, final String siglaExame, final Integer seqMatAnls, final Boolean indCargaExame) {
		DetachedCriteria criteria = this.montarCriteriaUnidadesFuncionaisExamesSignificativosPerinato(unfSeq, siglaExame, seqMatAnls, indCargaExame);
		return super.executeCriteriaCount(criteria);
	}
	
	/**
	 * Obter dados de exames significativos para uma unidade
	 * 
	 * @param unfSeq
	 * @param indCargaExame
	 * @return
	 */
	public List<AelUnidExameSignificativo> pesquisarAelUnidExameSignificativoPorUnfSeq(final Short unfSeq, final Boolean indCargaExame) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnidExameSignificativo.class);
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AelUnidExameSignificativo.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (indCargaExame != null) {
			criteria.add(Restrictions.eq(AelUnidExameSignificativo.Fields.IND_CARGA_EXAME.toString(), indCargaExame));
		}
		return super.executeCriteria(criteria);
	}
}
