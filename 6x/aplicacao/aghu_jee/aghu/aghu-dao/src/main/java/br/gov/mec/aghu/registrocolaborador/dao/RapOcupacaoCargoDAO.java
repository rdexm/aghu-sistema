package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapOcupacaoCargoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapOcupacaoCargo> {
	private static final long serialVersionUID = 7986449957881568181L;
	
	private static final Log LOG = LogFactory.getLog(RapOcupacaoCargoDAO.class);

	public enum OcupacaoCargoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CARGO_NAO_EXISTE, //
		MENSAGEM_OCUPACAO_CARGO_NAO_INFORMADA, //
		MENSAGEM_PARAMETRO_NAO_INFORMADO, //
		MENSAGEM_CODIGO_OCUPACAO_CARGO_OBRIGATORIO, //
		MENSAGEM_CARGO_CODIGO_OCUPACAO_CARGO_OBRIGATORIO, //
		MENSAGEM_DESCRICAO_OCUPACAO_CARGO_OBRIGATORIO, //
		MENSAGEM_CODIGO_SITUACAO_OCUPACAO_CARGO_JA_EXISTENTE, //
		MENSAGEM_CODIGO_OCUPACAO_CARGO_JA_EXISTENTE, //
		MENSAGEM_SITUACAO_OCUPACAO_CARGO_OBRIGATORIO, //
		MENSAGEM_CARGO_NAO_LOCALIZADO;
	}
	
	protected RapOcupacaoCargoDAO() {
	}
	
	@Override
	protected void obterValorSequencialId(RapOcupacaoCargo elemento) {
		LOG.info("A chave é informada pelo usuário, não usar");
	}

	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo,
			String carCodigo, String descricao, DominioSituacao situacao,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		DetachedCriteria criteria = montarConsulta(codigo, carCodigo,
				descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	
	private DetachedCriteria montarConsulta(Integer codigo, String carCodigo,
			String descricao, DominioSituacao situacao) {

		RapOcupacaoCargo example = new RapOcupacaoCargo();

		example.setCodigo(codigo);
		example.setDescricao(StringUtils.trimToNull(descricao));
		example.setIndSituacao(situacao);

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapOcupacaoCargo.class);
		criteria.add(Example.create(example).enableLike(MatchMode.ANYWHERE)
				.ignoreCase());

		if (StringUtils.isNotBlank(carCodigo)) {
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CARGO_CODIGO
					.toString(), carCodigo));
		}

		return criteria;	
	}
	
	
	public void incluir(RapOcupacaoCargo ocupacaoCargo) {
		persistir(ocupacaoCargo);
		flush();
	}

	
	public void alterar(RapOcupacaoCargo ocupacaoCargo) {		
		merge(ocupacaoCargo);
		flush();		
	}
	
	/**
	 * Retorna count pela chave primaria.
	 */
	public Long ocupacaoCargoPorCodigoCargoCount(RapOcupacaoCargo ocupacaoCargo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapOcupacaoCargo.class);
		
		criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CARGO_CODIGO.toString(), ocupacaoCargo.getId().getCargoCodigo()));
		criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CODIGO.toString(),ocupacaoCargo.getId().getCodigo()));

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna count por código e situação.
	 * 
	 * @param ocupacaoCargo
	 * @return
	 */
	public Long ocupacaoCargoPorCodigoSituacaoCount(
			RapOcupacaoCargo ocupacaoCargo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapOcupacaoCargo.class);

		RapOcupacaoCargo example = new RapOcupacaoCargo();

		example.setCodigo(ocupacaoCargo.getId().getCodigo());
		example.setIndSituacao(ocupacaoCargo.getIndSituacao());

		criteria.add(Example.create(example).enableLike(MatchMode.EXACT)
				.ignoreCase());

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna instância pelo id.
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RapOcupacaoCargo obterOcupacaoCargo(RapOcupacoesCargoId id)
			throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		RapOcupacaoCargo ocupacaoCargo = findByPK(
				RapOcupacaoCargo.class, id);

		return ocupacaoCargo;
	}
	
	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapCargos> pesquisarCargosPorDescricao(String valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapCargos.class);

		criteria.add(Restrictions.ilike(RapCargos.Fields.CODIGO.toString(),
				valor, MatchMode.EXACT));
		
		List<RapCargos> list = executeCriteria(criteria);
		if (list.isEmpty()){
			DetachedCriteria criteria2 = DetachedCriteria.forClass(RapCargos.class);

			criteria2.add(Restrictions.ilike(RapCargos.Fields.DESCRICAO.toString(),
					valor, MatchMode.ANYWHERE));
			list = executeCriteria(criteria2);
		}

		return list;
	}
	
	public Long pesquisarOcupacaoCargoCount(Integer codigo,
			String carCodigo, String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = montarConsulta(codigo, carCodigo,
				descricao, situacao);

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna os cargos encontrados
	 * 
	 * @param valor
	 * @return
	 */
	public List<OcupacaoCargoVO> pesquisarOcupacaoPorCodigo(
			String ocupacaoCargo, boolean somenteAtivos) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapOcupacaoCargo.class);

		String stParametro = ocupacaoCargo;
		Integer codigo = null;
		
		List<OcupacaoCargoVO> listaRetorno = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			codigo = Integer.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapOcupacaoCargo.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(
					RapOcupacaoCargo.Fields.DESCRICAO.toString(), stParametro,
					MatchMode.ANYWHERE));
		}

		if (somenteAtivos) {
			criteria.add(Restrictions.eq(
					RapOcupacaoCargo.Fields.SITUACAO.toString(),
					DominioSituacao.A));
		}
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Property.forName(RapOcupacaoCargo.Fields.ID_CODIGO.toString()),"codigoOcupacao");
		pList.add(Property.forName(RapOcupacaoCargo.Fields.DESCRICAO.toString()),"descricaoOcupacao");
		pList.add(Property.forName(RapOcupacaoCargo.Fields.ID_CARGO_CODIGO.toString()),"cargoCodigo");
		
		criteria.setProjection(pList);

		criteria.addOrder(Order.asc(RapOcupacaoCargo.Fields.DESCRICAO
				.toString()));
		criteria.setResultTransformer(Transformers
				.aliasToBean(OcupacaoCargoVO.class));
		
		listaRetorno = executeCriteria(criteria, 0, 100, null, false);
		
		return listaRetorno;
	}

	
	public List<RapOcupacaoCargo> listarOcupacaoPorCodigo(Integer codigoOcupacao) {
		DetachedCriteria _criteria = DetachedCriteria
				.forClass(RapOcupacaoCargo.class);

		_criteria.add(Restrictions.eq(
				RapOcupacaoCargo.Fields.CODIGO.toString(), codigoOcupacao));

		return executeCriteria(_criteria);
	}
	
	
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(String parametroPesquisa, DominioSituacao situacao){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(RapOcupacaoCargo.class);

		
		if (CoreUtil.isNumeroInteger(parametroPesquisa)) {
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CODIGO.toString(), Integer.valueOf(parametroPesquisa)));
			criteria.addOrder(Order.asc(RapOcupacaoCargo.Fields.CODIGO.toString()));
		} else {
			criteria.add(Restrictions.ilike(RapOcupacaoCargo.Fields.DESCRICAO.toString(), parametroPesquisa, MatchMode.ANYWHERE));
			criteria.addOrder(Order.asc(RapOcupacaoCargo.Fields.DESCRICAO.toString()));
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.SITUACAO.toString(),situacao));
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca os cargos de ocupação, conforme os filtros informados.
	 * @param codigo código do cargo
	 * @param descricao desrição do cargo
	 * @param carCodigo carCodigo do idRapOcupacaoCargo
	 * @param situacao ativo ou inativo
	 * @return List<RapOcupacaoCargo> com os requisitos satisfeitos trazendo os 100 primeiros retornos da consulta
	 */
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(Integer codigo, String descricao, String carCodigo, DominioSituacao situacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapOcupacaoCargo.class);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CODIGO.toString(), codigo));
		}
		
		if(descricao != null){
			criteria.add(Restrictions.ilike(RapOcupacaoCargo.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if(carCodigo != null && !carCodigo.isEmpty()){
			criteria.add(Restrictions.ilike(RapOcupacaoCargo.Fields.ID_CARGO_CODIGO.toString(), carCodigo, MatchMode.EXACT));				
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.SITUACAO.toString(),situacao));
		}		
		criteria.addOrder(Order.asc(RapOcupacaoCargo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	
	}
	
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargoFuncionario(Object filtro){
		DetachedCriteria criteria = montarQueryOcupacaoCargoFuncionario(filtro);
		criteria.addOrder(Order.asc(RapOcupacaoCargo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisarOcupacaoCargoFuncionarioCount(Object filtro){
		return executeCriteriaCount(montarQueryOcupacaoCargoFuncionario(filtro));
	}
	
	private DetachedCriteria montarQueryOcupacaoCargoFuncionario(Object ocupacaoCargo){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapOcupacaoCargo.class);

		String stParametro = (String) ocupacaoCargo;

		if (StringUtils.isNotBlank(stParametro) && StringUtils.isNumeric(stParametro)) {
			criteria.add(Restrictions.eq(RapOcupacaoCargo.Fields.CODIGO.toString(), Integer.valueOf(stParametro)));
		} else if(StringUtils.isNotBlank(stParametro)){
			criteria.add(Restrictions.ilike(RapOcupacaoCargo.Fields.DESCRICAO.toString(), stParametro,
					MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
}