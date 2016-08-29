package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.vo.AinQuartosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * 
 * @modulo internacao
 *
 */

public class AinQuartosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinQuartos> {

	private static final long serialVersionUID = 2488002066328893224L;

	
	public AinQuartos obterQuartosLeitosPorId(Short numeroLeito){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(), numeroLeito));
		criteria.createAlias(AinQuartos.Fields.LEITOS.toString(), "leito", JoinType.LEFT_OUTER_JOIN);
		AinQuartos quarto = (AinQuartos) this.executeCriteriaUniqueResult(criteria);		
		return quarto;		
	}
	
	
	public List<Integer> obterListNumeroQuartoPorCodigoClinica(Integer codigoClinica) {
		
		List<Integer> result = null;
		DetachedCriteria criteria = null;
		
		criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.setProjection(Property.forName(AinQuartos.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(AinQuartos.Fields.CLINICA_CODIGO.toString(), codigoClinica));
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	/**
	 * Pesquisa AinQuartos filtrados com parametros passados.
	 * @param pesquisa
	 * @param unfSeq
	 * @param orderAsc, TRUE => Order.Asc, FALSE ==> Order.DESC, NULL=> não adiciona Order
	 * @param atributosOrder
	 * @return
	 */
	public List<AinQuartos> listarQuartosPorUnf(Object pesquisa,
			Short unfSeq, Boolean orderAsc, AinQuartos.Fields ...atributosOrder) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(
					AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		if (pesquisa != null && !"".equals(pesquisa)){
			criteria.add(Restrictions.eq(
					AinQuartos.Fields.NUMERO.toString(), Short.valueOf((String)pesquisa)));
		}
		for(AinQuartos.Fields attOrder: atributosOrder){
			if(orderAsc!=null && orderAsc) {
				criteria.addOrder(Order.asc(attOrder.toString()));
			} else if(orderAsc!=null && !orderAsc) {
				criteria.addOrder(Order.desc(attOrder.toString()));
			}
		}
		
		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa AinQuartos filtrados com parametros passados.
	 * @param pesquisa
	 * @param unfSeq
	 * @param orderAsc, TRUE => Order.Asc, FALSE ==> Order.DESC, NULL=> não adiciona Order
	 * @param atributosOrder
	 * @return
	 */
	public List<AinQuartos> listarQuartosPorUnidadeFuncionalEDescricao(String descricao,
			Short unfSeq, Boolean orderAsc, AinQuartos.Fields ...atributosOrder) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		
		criteria.createAlias(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);

		getCriteriaListarQuartosPorUnidadeFuncionalEDescricao(descricao,
				unfSeq, orderAsc, criteria);
		
		for(AinQuartos.Fields attOrder: atributosOrder){
			if(orderAsc!=null && orderAsc) {
				criteria.addOrder(Order.asc(attOrder.toString()));
			} else if(orderAsc!=null && !orderAsc) {
				criteria.addOrder(Order.desc(attOrder.toString()));
			}
		}
		
		return this.executeCriteria(criteria, 0, 100, AinQuartos.Fields.NUMERO.toString(), Boolean.TRUE);
	}

	private void getCriteriaListarQuartosPorUnidadeFuncionalEDescricao(
			String descricao, Short unfSeq, Boolean orderAsc,
			DetachedCriteria criteria) {
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(
					AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		if (descricao != null && !"".equals(descricao)){
			criteria.add(Restrictions.ilike(AinQuartos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
	}
	
	public List<AinQuartos> pesquisarDisponibilidadeQuartos(Integer firstResult, Integer maxResult, Short nroQuarto,
			Integer clinica, Short unidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.createAlias(AinQuartos.Fields.ALA.toString(), "ala", Criteria.LEFT_JOIN);
		criteria.setFetchMode(AinQuartos.Fields.CLINICA.toString(), FetchMode.JOIN);

		if (nroQuarto != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(), nroQuarto));
		}

		if (clinica != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.CLINICA.toString() + ".codigo", clinica));

		}

		if (unidade != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString() + ".seq", unidade));

		}

		criteria.addOrder(Order.asc(AinQuartos.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("ala." + AghAla.Fields.DESCRICAO.toString()));

		// Se não for passado o firstResult e maxResult, faz a busca normal
		// (usado pelo método do controller
		// que retorna o count para a interface)
		if (firstResult == 0 && maxResult == 0) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, null, true);
		}
	}
	
	public AinQuartos pesquisaQuartoPorNumero(Short qrtNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.createAlias(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF.".concat(AghUnidadesFuncionais.Fields.ALA.toString()), "ALA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(), qrtNumero));
		return (AinQuartos) this.executeCriteriaUniqueResult(criteria);
	}

	public AinQuartos pesquisaQuartoPorLeitoID(String ltoLtoId) {
		String aliasLeitos = "l";

		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class)
				.createAlias(AinQuartos.Fields.LEITOS.toString(), aliasLeitos)
				.add(Restrictions.eq(aliasLeitos + "." + AinLeitos.Fields.LTO_ID.toString(), ltoLtoId));
		return (AinQuartos) this.executeCriteriaUniqueResult(criteria);
	}
	
	
	
	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param numero
	 * @param clinica
	 * @param excInfec
	 * @param consCli
	 * @return
	 */
	public List<AinQuartos> pesquisaQuartos(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short numero, AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli, String descricao) {
		DetachedCriteria criteria = createPesquisaCriteria(numero, clinica, excInfec, consCli, descricao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	private static final String QUARTO = "QUARTO";
	private static final String ALA = "ALA";
	private static final String CLINICA = "CLINICA";
	private static final String UNIDADES_FUNCIONAIS = "UNIDADES_FUNCIONAIS";
	private static final String ACOMODACAO = "ACOMODACAO";

	private	static final String SQL_PROJECTION_CONTA_LEITOS = montaSQLProjectionContaLeitos();
	
	public List<AinQuartosVO> pesquisaQuartosNew(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short numero, AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli, String descricao) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class,QUARTO);
		
		criteria.createAlias(AinQuartos.Fields.ALA.toString(), ALA,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.CLINICA.toString(), CLINICA,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.ACOMODACAO.toString(), ACOMODACAO, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString(), UNIDADES_FUNCIONAIS, JoinType.LEFT_OUTER_JOIN);

		
		criteria.setProjection(Projections.projectionList()
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.NUMERO.toString()),AinQuartosVO.Fields.NUMERO.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.DESCRICAO.toString()),AinQuartosVO.Fields.DESCRICAO.toString())
								.add(Projections.property(ALA+'.'+AghAla.Fields.DESCRICAO.toString()),AinQuartosVO.Fields.DESCRICAO_ALA.toString())
								.add(Projections.property(CLINICA+'.'+AghClinicas.Fields.DESCRICAO.toString()),AinQuartosVO.Fields.DESCRICAO_CLINICA.toString())
								.add(Projections.property(UNIDADES_FUNCIONAIS+'.'+AghUnidadesFuncionais.Fields.DESCRICAO.toString()),AinQuartosVO.Fields.DESCRICAO_UNIDADE.toString())
								.add(Projections.property(ACOMODACAO+'.'+AinAcomodacoes.Fields.DESCRICAO.toString()),AinQuartosVO.Fields.DESCRICAO_ACOMODACAO.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.CAPAC_INTERNACAO.toString()),AinQuartosVO.Fields.CAPAC_INTERNACAO.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.SEXO_DETERMINANTE.toString()),AinQuartosVO.Fields.SEXO_DETERMINANTE.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.CONSISTE_CLI.toString()),AinQuartosVO.Fields.IND_CONS_CLIN.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.IND_EXCLUSIV_INFECCAO.toString()),AinQuartosVO.Fields.IND_EXCLUSIV_INFECCAO.toString())
								.add(Projections.property(QUARTO+'.'+AinQuartos.Fields.IND_CON_SEXO.toString()),AinQuartosVO.Fields.IND_CON_SEXO.toString())
								.add(Projections.sqlProjection(SQL_PROJECTION_CONTA_LEITOS , new String[]{AinQuartosVO.Fields.QT_QUARTOS.toString()}, new Type[]{IntegerType.INSTANCE}))
				               );

		criteria.setResultTransformer(Transformers.aliasToBean(AinQuartosVO.class));
		
		if (numero != null) {
			criteria.add(Restrictions.eq(QUARTO+'.'+AinQuartos.Fields.NUMERO.toString(),numero));
		}

		if (clinica != null && clinica.getCodigo() != null) {
			criteria.add(Restrictions.eq(QUARTO+'.'+AinQuartos.Fields.CLINICA.toString(),clinica));
		}

		if (excInfec != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.EXCLUSIVO_INFEC.toString(),excInfec));
		}

		if (consCli != null) {
			criteria.add(Restrictions.eq(QUARTO+'.'+AinQuartos.Fields.CONSISTE_CLI.toString(),consCli));
		}
		
		if (descricao != null){
			criteria.add(Restrictions.ilike(QUARTO+'.'+AinQuartos.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	private static String montaSQLProjectionContaLeitos() {
		return "(select count(*) from "+AinLeitos.class.getAnnotation(Table.class).schema() + '.'+AinLeitos.class.getAnnotation(Table.class).name() + " LTO "+
				" where LTO."+AinLeitos.Fields.QRT_NUMERO.name() + " = {alias}."+AinQuartos.Fields.NUMERO.toString()+ ") as "+AinQuartosVO.Fields.QT_QUARTOS.toString();
	}


	/**
	 * 
	 * @dbtables AinQuartos select
	 * 
	 * @param numero
	 * @param clinica
	 * @param excInfec
	 * @param consCli
	 * @return
	 */
	public Long pesquisaQuartosCount(Short numero, AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli, String descricao) {
		return executeCriteriaCount(createPesquisaCriteria(numero, clinica, excInfec, consCli, descricao));
	}

	private DetachedCriteria createPesquisaCriteria(Short numero, AghClinicas clinica, DominioSimNao excInfec, DominioSimNao consCli,String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinQuartos.class);
		
		criteria.createAlias(AinQuartos.Fields.ALA.toString(), "ALA",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.CLINICA.toString(), "CLINICA",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.ACOMODACAO.toString(), "ACOMODACAO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString(), "UNIDADES_FUNCIONAIS", JoinType.LEFT_OUTER_JOIN);


		if (numero != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(),
					numero));
		}

		if (clinica != null && clinica.getCodigo() != null) {
			criteria.add(Restrictions.eq(AinQuartos.Fields.CLINICA.toString(),
					clinica));
		}

		if (excInfec != null) {
			criteria.add(Restrictions.eq(
					AinQuartos.Fields.EXCLUSIVO_INFEC.toString(),
					excInfec));
		}

		if (consCli != null) {
			criteria.add(Restrictions.eq(
					AinQuartos.Fields.CONSISTE_CLI.toString(),
					consCli));
		}
		
		if (descricao != null){
			criteria.add(Restrictions.ilike(
					AinQuartos.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	/**
	 * 
	 * @dbtables AinQuartos select 
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	//@Restrict("#{s:hasPermission('quarto','pesquisar')}")
	public List<AinQuartos> pesquisarQuartos(String strPesquisa) {
		
		//Utilizado HQL, pois não é possível fazer pesquisar com LIKE para tipos numéricos (Integer, Byte, Short)
		//através de Criteria (ocorre ClassCastException).
		StringBuilder hql = new StringBuilder(100);
		hql.append("from AinQuartos q inner join fetch q.").append(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString()).append(" uf ");
		hql.append("inner join fetch uf.indAla ala ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			
			strPesquisa = CoreUtil.retirarCaracteresInvalidos(strPesquisa);
			
			strPesquisa = strPesquisa.toUpperCase();
			hql.append("where str(q.").append(
					AinQuartos.Fields.DESCRICAO.toString()).append(") like '%")
					.append(strPesquisa).append("%' ").append("or str(q.")
					.append(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString())
					.append('.').append(
							AghUnidadesFuncionais.Fields.ANDAR.toString())
					.append(") like '%").append(strPesquisa).append("%' ")
					.append("or q.").append(
							AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString())
					.append('.').append(
							AghUnidadesFuncionais.Fields.ALA.toString())
					.append(" like '%").append(strPesquisa).append("%' ")
					.append("or upper(q.").append(
							AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString())
					.append('.').append(
							AghUnidadesFuncionais.Fields.DESCRICAO.toString())
					.append(") like '%").append(strPesquisa).append("%' ");
		}
		
		hql.append(" order by q.").append(AinQuartos.Fields.NUMERO.toString());
		return (List<AinQuartos>) createHibernateQuery(hql.toString()).list();
	}
	
	public List<AinQuartos> pesquisarQuartoSolicitacaoInternacao(String strPesquisa, DominioSexo sexoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		
		criteria.add(Restrictions.ilike(AinQuartos.Fields.DESCRICAO.toString(),
				strPesquisa, MatchMode.ANYWHERE));
		
		if (sexoPaciente != null) {
			criteria.add(Restrictions.eq(
					AinQuartos.Fields.SEXO_OCUPACAO.toString(), sexoPaciente));
		}
		
		criteria.addOrder(Order.asc(AinQuartos.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria, 0, 25, null, false);
	}
	
	public DominioSexoDeterminante obterSexoDeterminanteQuarto(Short numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(),
				numero));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinQuartos.Fields.SEXO_DETERMINANTE
						.toString())));

		DominioSexoDeterminante sexoDeterminante = (DominioSexoDeterminante) this
				.executeCriteriaUniqueResult(criteria);
		return sexoDeterminante;
	}
	
	public DominioSimNao obterIndConSexoQuarto(Short numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(),
				numero));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinQuartos.Fields.IND_CON_SEXO
						.toString())));

		DominioSimNao indConSexo = (DominioSimNao) this
				.executeCriteriaUniqueResult(criteria);
		return indConSexo;
	}
	
	public Short buscarSeqUnidadeFuncionalSeqDoQuarto(Short qrtNumero) {
		if (qrtNumero != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
			criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(), qrtNumero));
			criteria.setProjection(Projections.property(AinQuartos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
			return (Short) executeCriteriaUniqueResult(criteria);
		}
		return null;
	}
	
	public Short buscarMaxSeqUnidadeFuncionalSeqDoQuarto(Short quartoNum) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);
		criteria.setProjection(Projections.max("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteria.createAlias(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString(), "UNF");
		criteria.add(Restrictions.eq(AinQuartos.Fields.NUMERO.toString(), quartoNum));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public AinQuartos obterQuartoDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);		
		criteria.add(Restrictions.eq(AinQuartos.Fields.DESCRICAO.toString(), descricao));		
		return (AinQuartos) this.executeCriteriaUniqueResult(criteria);
	}

	public Long listarQuartosPorUnidadeFuncionalEDescricaoCount(
			String descricao, Short unfSeq, Boolean orderAsc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinQuartos.class);

		getCriteriaListarQuartosPorUnidadeFuncionalEDescricao(descricao,
				unfSeq, orderAsc, criteria);
		
		return this.executeCriteriaCount(criteria);
	}
}
