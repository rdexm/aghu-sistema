package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpImpressora;

public class ImpImpressoraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpImpressora> {

	private static final long serialVersionUID = 2956266948216912191L;

	/**
	 * @param paramPesquisa
	 * @param soCodBarras - informa se deve pesquisar somente impressoras de código de barras
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa, boolean soCodBarras) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpImpressora.class);

		if (StringUtils.isNotBlank(paramPesquisa.toString())) {
			criteria.add(Restrictions.like(ImpImpressora.Fields.FILA.toString(), paramPesquisa.toString().toUpperCase(), MatchMode.ANYWHERE));
		}

		if(soCodBarras){
			criteria.add(Restrictions.eq(ImpImpressora.Fields.TIPO.toString(), DominioTipoImpressoraCups.COD_BARRAS));	
		}
		
		List<ImpImpressora> li = executeCriteria(criteria);

		return li;
	}
	
	
	/**
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa) {
		return pesquisarImpressora(paramPesquisa, false);
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filaImpressora
	 * @param tipoImpressora
	 * @param descricao
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora,
			String descricao) {

		List<ImpImpressora> objList = executeCriteria(
				criarCriteriaPesquisaImpressora(filaImpressora, tipoImpressora,
						descricao), firstResult, maxResult, orderProperty, asc);

		return objList;
	}

	/**
	 * @param filaImpressora
	 * @param tipoImpressora
	 * @param descricao
	 * @return
	 */
	public Long pesquisarImpressoraCount(String filaImpressora,
			DominioTipoImpressoraCups tipoImpressora, String descricao) {

		return executeCriteriaCount(criarCriteriaPesquisaImpressora(
				filaImpressora, tipoImpressora, descricao));
	}

	/**
	 * Verifica direcionamento.
	 * 
	 * @param id
	 * @return
	 */
	public List<ImpImpressora> verificaRedirecionamento(Integer id) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpImpressora.class);

		criteria.add(Restrictions.eq(
				ImpImpressora.Fields.REDIRECIONAMENTO.toString(), id));

		List<ImpImpressora> li = executeCriteria(criteria);

		return li;

	}

	/**
	 * Cria criteria para pesquisa de Impressora
	 * 
	 * @param ip
	 *            e/ou fila e/ou tipo e/ou descricao da impressora ou campos em
	 *            branco
	 * @return Um DetachedCriteria pronto pra pesquisa.
	 */
	private DetachedCriteria criarCriteriaPesquisaImpressora(
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora,
			String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpImpressora.class);

		if (!StringUtils.isBlank(filaImpressora)) {
			criteria.add(Restrictions.like("filaImpressora", filaImpressora,
					MatchMode.ANYWHERE));
		}

		if (tipoImpressora != null) {
			criteria.add(Restrictions.eq(ImpImpressora.Fields.TIPO.toString(),
					tipoImpressora));
			// criteria.add(Restrictions.eq("tipoImpressora", tipoImpressora));
		}

		if (!StringUtils.isBlank(descricao)) {
			criteria.add(Restrictions.like("descricao", descricao,
					MatchMode.ANYWHERE));
		}
		
		criteria.setFetchMode(ImpImpressora.Fields.IMPRESSORA_REDIRECIONAMENTO.toString(), FetchMode.JOIN);

		return criteria;
	}

	/**
	 * 
	 * @param idUsuario
	 * @param login
	 * @return
	 */
	public boolean isImpressoraExistente(Integer idImpressora,
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpImpressora.class);

		criteria.add(Restrictions.eq(ImpImpressora.Fields.FILA.toString(),
				filaImpressora));
		ImpImpressora impImpressora = (ImpImpressora) executeCriteriaUniqueResult(criteria);

		// validacao para alteracao
		if (impImpressora != null && idImpressora != null) {
			if (impImpressora.getId().equals(idImpressora)) {
				return false;
			}
			return true;
		}
		return impImpressora == null ? false : true;
	}

	/**
	 * Metodo para listar as Impressoras em um combo de acordo com o parametro
	 * informado. O parametro pode ser parte da descrição da impressora. Método
	 * não utilizado, não foi preparado para receber outros parâmetros, só o
	 * parâmetro digitado no próprio suggestionBox.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public List<ImpImpressora> pesquisarImpressoraPorFila(Object paramPesquisa) {

		DetachedCriteria cri = DetachedCriteria.forClass(ImpImpressora.class);

		cri.addOrder(Order.asc(ImpImpressora.Fields.FILA.toString()));

		if (StringUtils.isNotBlank((String) paramPesquisa)) {

			String strPesquisa = (String) paramPesquisa;
			cri.add(Restrictions.like(ImpImpressora.Fields.FILA.toString(),
					((String) strPesquisa).toUpperCase(),
					MatchMode.ANYWHERE));
		}

		List<ImpImpressora> li = executeCriteria(cri);
		return li;
	}

	/**
	 * Obter redirect.
	 * 
	 * @param impImpressora
	 * @param strPesquisa
	 * @return
	 */
	public List<ImpImpressora> obterImpressoraRedirecionamento(
			ImpImpressora impImpressora, String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpImpressora.class);
		criteria.createAlias("impRedireciona", "impRedireciona",
				Criteria.LEFT_JOIN);

		// Se for edicao, nao permitir redirecionamento para a mesma impressora
		if (impImpressora.getId() != null) {
			criteria.add(Restrictions.not(Restrictions.eq(
					ImpImpressora.Fields.ID.toString(), impImpressora.getId())));
		}

		// if (StringUtils.isNotBlank(ImpImpressora.Fields.TIPO.toString())) {
		if (impImpressora.getTipoImpressora() != null) {
			criteria.add(Restrictions.eq(ImpImpressora.Fields.TIPO.toString(),
					impImpressora.getTipoImpressora()));
		}

		if (!StringUtils.isBlank((String) strPesquisa)) {
			criteria.add(Restrictions.like("filaImpressora",
					((String) strPesquisa).toUpperCase(), MatchMode.ANYWHERE));// ////
		}

		List<ImpImpressora> li = executeCriteria(criteria);

		return li;
	}

}
