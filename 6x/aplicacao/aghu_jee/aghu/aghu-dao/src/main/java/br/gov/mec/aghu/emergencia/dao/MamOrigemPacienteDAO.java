package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.search.Lucene;

/**
 * DAO da entidade MamOrigemPaciente
 * 
 * @author felipe.rocha
 * 
 */
public class MamOrigemPacienteDAO extends BaseDao<MamOrigemPaciente> {
	private static final long serialVersionUID = 3734703115837214143L;
	
	@Inject
    private Lucene lucene;

	/**
	 * Pesquisa MamOrigemPaciente por descricao e/ou indSituacao
	 * 
	 * C1 #34957 - Consulta utilizada para pesquisar a origem do paciente.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param descricao
	 * @param indSituacao
	 * @param seq
	 * @param origem
	 * @return
	 */
	public List<MamOrigemPaciente> pesquisarOrigensPaciente(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String descricao, DominioSituacao indSituacao,
			Integer seq) {
		final DetachedCriteria criteria = this
				.montarCriteriaPesquisarOrigemPaciente(descricao, indSituacao, seq);
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	/**
	 * Cria uma criteria genérica para filtrar MamOrigemPaciente por descricao
	 * e/ou indSituacao
	 * 
	 * C1 #34957 - Consulta utilizada para pesquisar a origem do paciente.
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @param seq
	 * @return
	 */
	public DetachedCriteria montarCriteriaPesquisarOrigemPaciente(
			String descricao, DominioSituacao indSituacao, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamOrigemPaciente.class);
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					MamOrigemPaciente.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(
					MamOrigemPaciente.Fields.IND_SITUACAO.toString(),
					indSituacao));
		}

		if (seq != null) {
			criteria.add(Restrictions.eq(
					MamOrigemPaciente.Fields.SEQ.toString(), seq));
		}
		return criteria;
	}

	public Long pesquisarOrigemPacientePorCodigoDescricaoSituacaoCount(
			String descricao, DominioSituacao indSituacao, Integer seq) {
		final DetachedCriteria criteria = this
				.montarCriteriaPesquisarOrigemPaciente(descricao, indSituacao,
						seq);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa se existe MamOrigemPaciente por descricao
	 * 
	 * C2 #34957- Consulta utilizada para verificar se a origem do paciente já
	 * está cadastrada
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public Boolean isOrigemPacienteJaCadastrado(String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamOrigemPaciente.class);
		criteria.add(Restrictions.eq(
				MamOrigemPaciente.Fields.DESCRICAO.toString(), descricao));
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Pesquisa se existe acolhimentos para origem do paciente
	 * 
	 * C3 #34957- Consulta que verificar se existe algum acolhimento do paciente
	 * vinculado a origem do paciente selecionadaConsulta utilizada para
	 * verificar se a origem do paciente já está cadastrada
	 * 
	 * @param origemPaciente
	 * @param indSituacao
	 * @return
	 */
	public Boolean isAcolhimentoOrigemPaciente(MamOrigemPaciente origemPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamTriagens.class);
		criteria.add(Restrictions.eq(
				MamTriagens.Fields.ORIGEM_PACIENTE.toString(), origemPaciente));
		return super.executeCriteriaExists(criteria);
	}
	
	public List<MamOrigemPaciente> listarOrigemPaciente(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamOrigemPaciente.class);
		String strPesquisa = (String) pesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(MamOrigemPaciente.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		}else if(StringUtils.isNotBlank(strPesquisa)){
			return lucene.executeLuceneQueryParaSuggestionBox(MamOrigemPaciente.Fields.DESCRICAO.toString(), MamOrigemPaciente.Fields.DESCRICAO_FONETICA.toString(), strPesquisa, MamOrigemPaciente.class, MamOrigemPaciente.Fields.DESCRICAO_ORDENACAO.toString());
		}
		
		criteria.add(Restrictions.eq(MamOrigemPaciente.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(MamOrigemPaciente.Fields.SEQ.toString()))
			.addOrder(Order.asc(MamOrigemPaciente.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarOrigemPacienteCount(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamOrigemPaciente.class);
		String strPesquisa = (String) pesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(MamOrigemPaciente.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		}else if(StringUtils.isNotBlank(strPesquisa)){
			return (long) lucene.executeLuceneCount(MamOrigemPaciente.Fields.DESCRICAO.toString(), MamOrigemPaciente.Fields.DESCRICAO_FONETICA.toString(), strPesquisa, MamOrigemPaciente.class, MamOrigemPaciente.Fields.DESCRICAO_ORDENACAO.toString());
		}
		criteria.add(Restrictions.eq(MamOrigemPaciente.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return this.executeCriteriaCount(criteria);
	}
	
}