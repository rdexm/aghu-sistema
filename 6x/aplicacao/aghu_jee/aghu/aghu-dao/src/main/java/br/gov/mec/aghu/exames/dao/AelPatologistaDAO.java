package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;


public class AelPatologistaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPatologista> {
	
	private static final long serialVersionUID = -6101747098570423842L;
	private final int SIZE_LISTAR_PATOLOGISTAS_CODIGO_NOME_FUNCAO = 100;
	
	public AelPatologista obterAelPatologistaAtivoPorServidorEFuncao(final RapServidores servidor, final DominioFuncaoPatologista ...funcao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		criteria.add(Restrictions.in(AelPatologista.Fields.FUNCAO.toString(), funcao));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
		
		return (AelPatologista) executeCriteriaUniqueResult(criteria);
	}

	public List<AelPatologista> listarPatologistasPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista ...funcao){
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorCodigoNomeFuncao(filtro, null, funcao);
		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, SIZE_LISTAR_PATOLOGISTAS_CODIGO_NOME_FUNCAO, null, false);
	}

	public Long listarPatologistasPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista ...funcao){
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorCodigoNomeFuncao(filtro, null, funcao);
		return executeCriteriaCount(criteria);
	}

	public List<AelPatologista> listarPatologistasAtivosPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista ...funcao){
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorCodigoNomeFuncao(filtro, true, funcao);
		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, SIZE_LISTAR_PATOLOGISTAS_CODIGO_NOME_FUNCAO, null, false);
	}

	public Long listarPatologistasAtivosPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista ...funcao){
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorCodigoNomeFuncao(filtro, true, funcao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarPatologistasPorCodigoNomeFuncao(
			final String filtro, final Boolean situacao, final DominioFuncaoPatologista... funcao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "servidor");
		criteria.add(Restrictions.in(AelPatologista.Fields.FUNCAO.toString(), funcao));

		if (situacao != null) {
			if (situacao) {
				criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
			}
			else {
				criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.I));

			}
		}
		
		if(filtro != null){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(filtro)));
				
			} else {
				criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	
	
	public Long obterQuantidadeFuncoesPeloServidorEFuncao(final Integer seq, final RapServidores servidor, final DominioFuncaoPatologista funcao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		criteria.add(Restrictions.ne(AelPatologista.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelPatologista.Fields.FUNCAO.toString(), funcao));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}

	public Long obterQuantidadeFuncoesPeloServidorEFuncaoDiferente(final Integer seq, final RapServidores servidor,
			final DominioFuncaoPatologista funcao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		criteria.add(Restrictions.ne(AelPatologista.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.ne(AelPatologista.Fields.FUNCAO.toString(), funcao));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}

	public List<AelPatologista> listarPatologistas(final Integer firstResult, final Integer maxResult, final Integer seq, final String nome,
			final DominioFuncaoPatologista funcao, final Boolean permiteLibLaudo, final DominioSituacao situacao, final RapServidoresId servidor,
			final String nomeParaLaudo) {

		final DetachedCriteria criteria = obterCriteriaPesquisaPatologista(seq, nome, funcao, permiteLibLaudo, situacao, servidor, nomeParaLaudo);

		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public Long listarPatologistasCount(final Integer seq, final String nome, final DominioFuncaoPatologista funcao,
			final Boolean permiteLibLaudo, final DominioSituacao situacao, final RapServidoresId servidor, final String nomeParaLaudo) {

		final DetachedCriteria criteria = obterCriteriaPesquisaPatologista(seq, nome, funcao, permiteLibLaudo, situacao, servidor, nomeParaLaudo);

		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisaPatologista(final Integer seq, final String nome, final DominioFuncaoPatologista funcao,
			final Boolean permiteLibLaudo, final DominioSituacao situacao, final RapServidoresId servidor, final String nomeParaLaudo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), seq));
		}

		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		if (funcao != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.FUNCAO.toString(), funcao));
		}

		if (permiteLibLaudo != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.PERMITE_LIB_LAUDO.toString(), permiteLibLaudo));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), situacao));
		}

		if (servidor != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.SERVIDOR_ID.toString(), servidor));
		}

		if (!StringUtils.isBlank(nomeParaLaudo)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME_PARA_LAUDO.toString(), nomeParaLaudo, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	public List<AelPatologista> pesquisarPatologistas(final Object valor) {
		return pesquisarPatologistas(valor, null, 100);
	}

	public List<AelPatologista> pesquisarPatologistasPorFuncao(final Object valor, final DominioFuncaoPatologista[] funcao) {
		return pesquisarPatologistasPorFuncao(valor, funcao, 100);
	}

	public List<AelPatologista> pesquisarPatologistas(final Object valor, final DominioSituacao indSituacao, final Integer max) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		if (CoreUtil.isNumeroInteger(valor)) {
			Integer seq;
			if (valor instanceof String) {
				seq = Integer.valueOf((String) valor);
			} else {
				seq = (Integer) valor;
			}
			criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), seq));
			
		} else if (valor instanceof String && !StringUtils.isBlank((String) valor)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), indSituacao));
		}
		
		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, max, null, false);
	}
	
	public List<AelPatologista> pesquisarPatologistasPorNomeESituacao(final Object valor, final DominioSituacao indSituacao, final Integer max) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf");

		if (valor != null) {
			DominioFuncaoPatologista filtroFuncao = null;
			
			if (valor.toString().equalsIgnoreCase(DominioFuncaoPatologista.P.getDescricao())) {
				filtroFuncao = DominioFuncaoPatologista.P;
			}
			else if (valor.toString().equalsIgnoreCase(DominioFuncaoPatologista.C.getDescricao())) {
				filtroFuncao = DominioFuncaoPatologista.C;
			}
			else if (valor.toString().equalsIgnoreCase(DominioFuncaoPatologista.R.getDescricao())) {
				filtroFuncao = DominioFuncaoPatologista.R;
			}
			
			if (filtroFuncao != null) {
				criteria.add(Restrictions.or(
						Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE),
						Restrictions.eq(AelPatologista.Fields.FUNCAO.toString(), filtroFuncao))
						);
			}
			else {
				criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE));
			}
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), indSituacao));
		}
		
		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, max, null, false);
	}

	public List<AelPatologista> pesquisarPatologistasPorFuncao(final Object valor, final DominioFuncaoPatologista[] funcao, final Integer max) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		if (CoreUtil.isNumeroInteger(valor)) {
			Integer seq;
			if (valor instanceof String) {
				seq = Integer.valueOf((String) valor);
			} else {
				seq = (Integer) valor;
			}
			criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), seq));
		} else if (valor instanceof String && !StringUtils.isBlank((String) valor)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(AelPatologista.Fields.FUNCAO.toString(), funcao));

		return executeCriteria(criteria, 0, max, null, false);
	}

	public List<AelPatologista> pesquisarPatologistasPorAnatomoPatologia(final Long lumSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.createAlias(AelPatologista.Fields.AEL_AP_X_PATOLOGISTAS.toString(), "ap");
		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf");
		if (lumSeq == null) {
			throw new IllegalArgumentException("Código do Anatomo Patológico é obrigatório.");
		}
		criteria.add(Restrictions.eq( "ap." + AelApXPatologista.Fields.LUM_SEQ.toString(),
				lumSeq));

		return executeCriteria(criteria);
	}

	public AelPatologista obterPatologistaAtivo(final Integer codigoPatologista) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		if (codigoPatologista == null) {
			throw new IllegalArgumentException("Código do Patologista é obrigatório.");
		}
		criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), codigoPatologista));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));

		return (AelPatologista) executeCriteriaUniqueResult(criteria);
	}
	
	public AelPatologista obterPatologista(final Integer codigoPatologista) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);

		criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), codigoPatologista));
		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);

		return (AelPatologista) executeCriteriaUniqueResult(criteria);
	}
	private DetachedCriteria obterCriteriaPatologistaAtivoPorServidor(final RapServidores servidor){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.add(Restrictions.eq(AelPatologista.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	public AelPatologista obterPatologistaAtivoPorServidor(final RapServidores servidor) {
		return (AelPatologista)executeCriteriaUniqueResult(obterCriteriaPatologistaAtivoPorServidor(servidor));
	}
	
	public boolean isPatologistaAtivoPorServidor(final RapServidores servidor) {
		return executeCriteriaCount(obterCriteriaPatologistaAtivoPorServidor(servidor)) > 0;
	}

	public List<AelPatologista> listarPatologistaPorSeqNome(final Object valor) {
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorSeqNome(valor, true);
		
		return executeCriteria(criteria);
	}
	
	public Long listarPatologistaPorSeqNomeCount(final Object valor) {
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorSeqNome(valor, false);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarPatologistasPorSeqNome(final Object valor, Boolean ordenar) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.add(Restrictions.in(AelPatologista.Fields.FUNCAO.toString(),
				new DominioFuncaoPatologista[]{DominioFuncaoPatologista.P, DominioFuncaoPatologista.C}));

		if (CoreUtil.isNumeroInteger(valor)) {
			Integer seq;
			if (valor instanceof String) {
				seq = Integer.valueOf((String) valor);
			} else {
				seq = (Integer) valor;
			}
			criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), seq));
			
		} else if (valor instanceof String && !StringUtils.isBlank((String) valor)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE));
		}
		if (ordenar.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));
		}
		
		return criteria;
	}

	//QUALIDADE
	/**
	 * Consulta patologistas responsaveis para popular suggestionbox na tela agruparExames.xhtml
	 * São consultados todos patologistas ativos em todas funcoes
	 * C3 #22049
	 * @param valor valor digitado na suggestionbox para ser utilizado na pesquisa
	 */
	public List<AelPatologista> pesquisarPatologistasResponsaveisPorSeqNomeTodasFuncoes(final Object valor) {
		DetachedCriteria criteria = obterCriteriaListarPatologistasPorSeqNomeTodasFuncoes(valor);
		criteria.createAlias(AelPatologista.Fields.SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf");
		criteria.addOrder(Order.asc(AelPatologista.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	//QUALIDADE
	/**
	 * Consulta total patologistas responsaveis para popular suggestionbox na tela agruparExames.xhtml
	 * São consultados todos patologistas ativos em todas funcoes
	 * C3 #22049
	 * @param valor valor digitado na suggestionbox para ser utilizado na pesquisa
	 */
	public Long pesquisarPatologistasResponsaveisPorSeqNomeTodasFuncoesCount(final Object valor) {
		final DetachedCriteria criteria = obterCriteriaListarPatologistasPorSeqNomeTodasFuncoes(valor);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaListarPatologistasPorSeqNomeTodasFuncoes(final Object valor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class);
		criteria.add(Restrictions.in(AelPatologista.Fields.FUNCAO.toString(),
				new DominioFuncaoPatologista[]{DominioFuncaoPatologista.P, DominioFuncaoPatologista.C, DominioFuncaoPatologista.R}));

		if (CoreUtil.isNumeroInteger(valor)) {
			Integer seq;
			if (valor instanceof String) {
				seq = Integer.valueOf((String) valor);
			} else {
				seq = (Integer) valor;
			}
			criteria.add(Restrictions.eq(AelPatologista.Fields.SEQ.toString(), seq));
			
		} else if (valor instanceof String && !StringUtils.isBlank((String) valor)) {
			criteria.add(Restrictions.ilike(AelPatologista.Fields.NOME.toString(), (String) valor, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	//#22049 - C8
	public AelPatologista obterPatologistaPorNumeroExame() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologista.class, "lui");
		
		criteria.createAlias("lui." + AelPatologista.Fields.AEL_AP_X_PATOLOGISTAS, "lo5");
		criteria.createAlias("lo5." + AelApXPatologista.Fields.AEL_ANATOMO_PATOLOGICOS, "lum");
		
		Object result = executeCriteriaUniqueResult(criteria);
		
		return result != null ?  (AelPatologista)result : null;
	}
	
	public String pesquisarUsuarioPorMatricula(Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		criteria.setProjection(Projections.property(RapServidores.Fields.USUARIO.toString()));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
}