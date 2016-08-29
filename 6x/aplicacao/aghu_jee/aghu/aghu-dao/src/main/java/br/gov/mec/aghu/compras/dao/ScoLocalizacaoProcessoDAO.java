package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoLocalizacaoProcessoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLocalizacaoProcesso> {

	private static final long serialVersionUID = -8606128315820488620L;
	
	/**
	 * Método de pesquisa para a página de lista. 
	 * @param codigo, descricao, indSituacao, ramal, servidorResponsavel, indLocalOriginario, indArquivoMorto, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 18/01/2013
	 */
	public List<ScoLocalizacaoProcesso> listarLocalizacaoProcesso(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoLocalizacaoProcesso scoLocalizacaoProcesso) {

		DetachedCriteria criteria = montarClausulaWhere(scoLocalizacaoProcesso);
		criteria.createAlias("SRE."+RapServidores.Fields.PESSOA_FISICA.toString(), "SRE_PF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(ScoLocalizacaoProcesso.Fields.RAMAL.toString(), "R", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(ScoLocalizacaoProcesso.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método count de pesquisa para a página de lista.
	 * @param codigo, descricao, indSituacao, ramal, servidorResponsavel, indLocalOriginario, indArquivoMorto
	 * @author dilceia.alves
	 * @since 18/01/2013
	 */
	public Long listarLocalizacaoProcessoCount(
			ScoLocalizacaoProcesso scoLocalizacaoProcesso) {

		DetachedCriteria criteria = montarClausulaWhere(scoLocalizacaoProcesso);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarClausulaWhere(ScoLocalizacaoProcesso scoLocalizacaoProcesso) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		criteria.createAlias(ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL.toString(), "SRE", JoinType.LEFT_OUTER_JOIN);
		
		if (scoLocalizacaoProcesso != null) {
			if (scoLocalizacaoProcesso.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoLocalizacaoProcesso.Fields.CODIGO.toString(),
						scoLocalizacaoProcesso.getCodigo()));
			}
		}

		if (StringUtils.isNotBlank(scoLocalizacaoProcesso.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(),
					scoLocalizacaoProcesso.getDescricao(), MatchMode.ANYWHERE));
		}

		if (scoLocalizacaoProcesso.getRamal() != null) {
			criteria.add(Restrictions.eq(
					ScoLocalizacaoProcesso.Fields.RAMAL.toString(),
					scoLocalizacaoProcesso.getRamal()));
		}
		
		if (scoLocalizacaoProcesso.getServidorResponsavel() != null) {
			criteria.add(Restrictions.eq(
					ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL.toString(),
					scoLocalizacaoProcesso.getServidorResponsavel()));
		}

		if (scoLocalizacaoProcesso.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoLocalizacaoProcesso.Fields.SITUACAO.toString(),
					scoLocalizacaoProcesso.getIndSituacao()));
		}

		if (scoLocalizacaoProcesso.getIndLocalOriginario() != null) {
			criteria.add(Restrictions.eq(
					ScoLocalizacaoProcesso.Fields.LOCAL_ORIGINARIO.toString(), 
					scoLocalizacaoProcesso.getIndLocalOriginario()));
		}

		if (scoLocalizacaoProcesso.getIndArquivoMorto() != null) {
			criteria.add(Restrictions.eq(
					ScoLocalizacaoProcesso.Fields.ARQUIVO_MORTO.toString(), 
					scoLocalizacaoProcesso.getIndArquivoMorto()));
		}

		
		return criteria;
	}

	/**
	 * Método para obter registro com ArquivoMorto(true).
	 * @param indArquivoMorto
	 * @author dilceia.alves
	 * @since 22/01/2013
	 */
	public ScoLocalizacaoProcesso obterLocalizacaoProcessoPorArquivoMorto(Boolean indArquivoMorto) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		criteria.add(Restrictions.eq(
				ScoLocalizacaoProcesso.Fields.ARQUIVO_MORTO.toString(), indArquivoMorto));
		
		return (ScoLocalizacaoProcesso) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método para obter registro com LocalOriginario(true) para o servidorResponsavel informado.
	 * @param servidorResponsavel
	 * @author dilceia.alves
	 * @since 22/01/2013
	 */
	public ScoLocalizacaoProcesso obterLocalizacaoProcessoPorResponsavelComLocalOriginario(RapServidores servidorResponsavel) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		criteria.add(Restrictions.eq(
				ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL.toString(), servidorResponsavel));
		criteria.add(Restrictions.eq(
				ScoLocalizacaoProcesso.Fields.LOCAL_ORIGINARIO.toString(), true));
		
		return (ScoLocalizacaoProcesso) this.executeCriteriaUniqueResult(criteria);
	}

	public ScoLocalizacaoProcesso obterLocalizacaoProcessoPeloResponsavelOuComLocalOriginario(RapServidores servidorResponsavel,
																						Short pLocalDefault) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		criteria.add(Restrictions.or(
				Restrictions.eq(ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL.toString(), servidorResponsavel), 
				Restrictions.eq(ScoLocalizacaoProcesso.Fields.LOCAL_ORIGINARIO.toString(), true))
		);
		
		List<ScoLocalizacaoProcesso> loclz = executeCriteria(criteria);
		if(!loclz.isEmpty()) {
			return loclz.get(0);
		}
		else {
			return this.obterPorChavePrimaria(pLocalDefault);
		}
	}
	
	private DetachedCriteria createPesquisarScoLocalizacaoProcesso(Object filtro, Integer max){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLocalizacaoProcesso.class);
		criteria.createAlias(
				ScoLocalizacaoProcesso.Fields.RAMAL.toString(), "RM");
		if (filtro != null) {
			String filtroStr = (String) filtro;
			Criterion restriction = Restrictions.ilike(
					ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(),
					filtroStr, MatchMode.ANYWHERE);
			if (CoreUtil.isNumeroShort(filtro)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(
						ScoLocalizacaoProcesso.Fields.CODIGO.toString(),
						Short.valueOf(filtroStr)));
			}
			
			criteria.add(restriction);
		}
		criteria.add(Restrictions.eq(
				ScoLocalizacaoProcesso.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		return criteria;
		
	}
	
	
	public Long pesquisarScoLocalizacaoProcessoCount(Object filtro){
		DetachedCriteria criteria = createPesquisarScoLocalizacaoProcesso(filtro, Integer.valueOf(0));
		return executeCriteriaCount(criteria);
	}
	

	/**
	 * Pesquisa localizações do PAC.
	 * 
	 * @param filtro Código ou Descrição
	 * @return Localizações do PAC
	 */
	public List<ScoLocalizacaoProcesso> pesquisarScoLocalizacaoProcesso(
			Object filtro, Integer max) {
		DetachedCriteria criteria = createPesquisarScoLocalizacaoProcesso(filtro, max);
		
		return executeCriteria(criteria, 0, max,
				ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * Pesquisa utilizada por suggestion box (retorna todos locais ou somente os ativos)
	 * @param parametro, indAtivo
	 * @return lista
	 * @author dilceia.alves
	 * @since 07/02/2013
	 */
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricao(Object parametro, Boolean indAtivo) {
		DetachedCriteria criteria = createPesquisarLocalizacaoCriteria(
				parametro, indAtivo);
		
		criteria.addOrder(Order.asc(ScoLocalizacaoProcesso.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria, 0, 100, ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), true);
	}

	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricaoOrderDescricao(Object parametro, Boolean indAtivo) {

		DetachedCriteria criteria = createPesquisarLocalizacaoCriteria(
				parametro, indAtivo);
		
		criteria.addOrder(Order.asc(ScoLocalizacaoProcesso.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria, 0, 100, ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), true);
	}
	
	
	public Long pesquisarLocalizacaoProcessoPorCodigoOuDescricaoCount(Object parametro, Boolean indAtivo) {
		DetachedCriteria criteria = createPesquisarLocalizacaoCriteria(
				parametro, indAtivo);
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria createPesquisarLocalizacaoCriteria(
			Object parametro, Boolean indAtivo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		
		String strParametro = (String) parametro;
		
		if (StringUtils.isNotBlank(strParametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(
						ScoLocalizacaoProcesso.Fields.CODIGO.toString(), Short.valueOf(strParametro)));
			} else {
				criteria.add(Restrictions.ilike(
						ScoLocalizacaoProcesso.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			}			
		}
		
		if(indAtivo != null) {
			if (indAtivo) {
				criteria.add(Restrictions.eq(ScoLocalizacaoProcesso.Fields.SITUACAO.toString(), DominioSituacao.A));
			}
		}
		
		return criteria;
	}
	
	public ScoLocalizacaoProcesso obterDadosLocalizacaoProcesso(Short lcpCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
		criteria.add(Restrictions.eq(ScoLocalizacaoProcesso.Fields.CODIGO.toString(), lcpCodigo));
		return (ScoLocalizacaoProcesso) executeCriteriaUniqueResult(criteria);
	}

    public ScoLocalizacaoProcesso obterLocalizacaoProcesso(Short codigo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoLocalizacaoProcesso.class);
        criteria.createAlias(ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL.toString(), "SRE", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("SRE."+RapServidores.Fields.PESSOA_FISICA.toString(), "SRE_PF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(ScoLocalizacaoProcesso.Fields.RAMAL.toString(), "R", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(ScoLocalizacaoProcesso.Fields.CODIGO.toString(), codigo));
        return (ScoLocalizacaoProcesso) executeCriteriaUniqueResult(criteria);
    }
}
