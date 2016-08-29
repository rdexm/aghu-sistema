package br.gov.mec.aghu.configuracao.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioCategoriaTabela;
import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.model.AghTabelasSistema;
import br.gov.mec.aghu.model.RapServidores;

public class AghTabelasSistemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghTabelasSistema>{
	
	private static final long serialVersionUID = 5390279001691845096L;
	
	private static final Log LOG = LogFactory.getLog(AghTabelasSistemaDAO.class);

	/**
	 * Método da pesquisa paginada para TabelasSistema
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param nome
	 * @param cor
	 * @param categoria
	 * @param versao
	 * @return
	 */
	public List<AghTabelasSistema> pesquisarTabelasSistema(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String nome, AghCoresTabelasSistema cor,
			DominioCategoriaTabela categoria, String versao, String origem) {
		DetachedCriteria criteria = montarCriteriaPesquisaTabelasSistema(seq,
				nome, cor, categoria, versao, origem);
		criteria.addOrder(Order.asc(AghTabelasSistema.Fields.NOME.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public AghTabelasSistema obterTabelaSistemaPeloId(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghTabelasSistema.class);
		criteria.createAlias(AghTabelasSistema.Fields.COR.toString(), "COR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghTabelasSistema.Fields.MENU.toString(), "MENU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghTabelasSistema.Fields.SERVIDOR_RESPONSAVEL.toString(), "SRR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghTabelasSistema.Fields.SERVIDOR_ULT_ALTERACAO.toString(), "SUL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghTabelasSistema.Fields.SERVIDOR_CRIACAO.toString(), "SRC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRR."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SUL."+RapServidores.Fields.PESSOA_FISICA.toString(), "PUL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRC."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRC", JoinType.LEFT_OUTER_JOIN);
		
		
		if(seq != null){
			criteria.add(Restrictions.eq(AghTabelasSistema.Fields.SEQ.toString(), seq));
		}
		
		return (AghTabelasSistema) this.executeCriteriaUniqueResult(criteria);
	}

	
	/**
	 * Método para pesquisa de Count para TabelasSistema
	 * @param seq
	 * @param nome
	 * @param cor
	 * @param categoria
	 * @param versao
	 * @return
	 */
	public Long pesquisarTabelasSistemaCount(Integer seq, String nome,
			AghCoresTabelasSistema cor, DominioCategoriaTabela categoria,
			String versao, String origem) {
		DetachedCriteria criteria = montarCriteriaPesquisaTabelasSistema(seq,
				nome, cor, categoria, versao, origem);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Montagem de criteria para ambos os métodos acima
	 * 
	 * @param seq
	 * @param nome
	 * @param cor
	 * @param categoria
	 * @param versao
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisaTabelasSistema(Integer seq,
			String nome, AghCoresTabelasSistema cor,
			DominioCategoriaTabela categoria, String versao, String origem) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghTabelasSistema.class);
		criteria.createAlias(AghTabelasSistema.Fields.COR.toString(), "COR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghTabelasSistema.Fields.MENU.toString(), "MENU", JoinType.LEFT_OUTER_JOIN);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AghTabelasSistema.Fields.SEQ.toString(), seq));
		}
		if(nome != null && StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike(AghTabelasSistema.Fields.NOME.toString(), nome.toUpperCase(), MatchMode.ANYWHERE));
		}
		if(cor != null){
			criteria.add(Restrictions.eq(AghTabelasSistema.Fields.COR.toString(), cor));
		}
		if(categoria != null){
			criteria.add(Restrictions.eq(AghTabelasSistema.Fields.CATEGORIA.toString(), categoria));
		}
		if(versao != null && StringUtils.isNotBlank(versao)){
			criteria.add(Restrictions.ilike(AghTabelasSistema.Fields.VERSAO.toString(), versao.toUpperCase(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(origem)) {
			criteria.add(Restrictions.ilike(AghTabelasSistema.Fields.ORIGEM.toString(), origem, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * Verifica se já existe no banco uma tabela com o nome em questão 
	 * (que não seja a própria sendo editada)
	 * @param seq
	 * @param nome
	 * @return
	 */
	public Boolean verificarTabelaExistente(Integer seq, String nome){
		Boolean retorno = false;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghTabelasSistema.class);
		criteria.add(Restrictions.eq(AghTabelasSistema.Fields.NOME.toString(), nome));
		if (seq != null){
			criteria.add(Restrictions.ne(AghTabelasSistema.Fields.SEQ.toString(), seq));
		}
		List<AghTabelasSistema> listaTabelas = executeCriteria(criteria);
		if (!listaTabelas.isEmpty()){
			retorno = true;
		}
		
		return retorno;
	}

	public Integer countNumLinhasTabela(String nomeTabela) {
		Number result = 0;

		try {
			StringBuilder sql = new StringBuilder(50);
			sql.append("select count(*) from agh.");
			sql.append(nomeTabela);
			javax.persistence.Query query = this.createNativeQuery(sql.toString());
			if (!query.getResultList().isEmpty()) {
				if (isOracle()) {
					result = (BigDecimal) query.getSingleResult();
				} else {
					result = (BigInteger) query.getSingleResult();
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return result.intValue();
	}

	public boolean verificarTabelaExistente(String nomeTabela) {
		boolean result = false;
		StringBuilder sql = new StringBuilder(50);
		if (isOracle()) {
			sql.append("select table_name as nometabela ");
			sql.append("from all_tables ");
			sql.append("where upper(owner) = upper('agh') ");
			sql.append("and upper(table_name) = upper('" + nomeTabela + "')");
		} else {
			sql.append("select tablename as nometabela ");
			sql.append("from pg_catalog.pg_tables ");
			sql.append("where upper(schemaname) = upper('agh') ");
			sql.append("and upper(tablename) = upper('" + nomeTabela + "')");
		}

		javax.persistence.Query query = this.createNativeQuery(sql.toString());
		if (!query.getResultList().isEmpty()) {
			result = true;
		}
		return result;
	}
}
