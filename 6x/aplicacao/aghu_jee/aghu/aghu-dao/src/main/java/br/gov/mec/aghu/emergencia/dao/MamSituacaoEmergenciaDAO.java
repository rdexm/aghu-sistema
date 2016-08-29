package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamSituacaoEmergencia
 * 
 * @author luismoura
 * 
 */
public class MamSituacaoEmergenciaDAO extends BaseDao<MamSituacaoEmergencia> {
	private static final long serialVersionUID = 3734703115837214143L;

	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * C1 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public List<MamSituacaoEmergencia> pesquisarSituacoesEmergencia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = this.montarPesquisaSituacoesEmergencia(codigo, descricao, indSituacao);

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Executa o count da pesquisa de situações de emergência
	 * 
	 * C1 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public Long pesquisarSituacoesEmergenciaCount(Short codigo, String descricao, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = this.montarPesquisaSituacoesEmergencia(codigo, descricao, indSituacao);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta a pesquisa de situações de emergência
	 * 
	 * C1 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	private DetachedCriteria montarPesquisaSituacoesEmergencia(Short codigo, String descricao, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamSituacaoEmergencia.class, "MamSituacaoEmergencia");

		if (codigo != null) {
			criteria.add(Restrictions.eq(MamSituacaoEmergencia.Fields.SEQ.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MamSituacaoEmergencia.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamSituacaoEmergencia.Fields.IND_SITUACAO.toString(), indSituacao));

		}

		return criteria;
	}
	
	public MamSituacaoEmergencia obterPorSeq(Short seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamSituacaoEmergencia.class);

		criteria.add(Restrictions.eq(MamSituacaoEmergencia.Fields.SEQ.toString(), seq));
		
		List<MamSituacaoEmergencia> listaSituacaoEmergencia =  executeCriteria(criteria);
		 
		if(listaSituacaoEmergencia != null && !listaSituacaoEmergencia.isEmpty()){
			return listaSituacaoEmergencia.get(0);
		}
		return null;
	}

}