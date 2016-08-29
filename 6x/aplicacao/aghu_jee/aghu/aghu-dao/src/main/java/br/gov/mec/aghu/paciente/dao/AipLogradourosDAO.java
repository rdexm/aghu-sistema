package br.gov.mec.aghu.paciente.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioLadoEndereco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.paciente.vo.LogradouroVO;
import br.gov.mec.aghu.core.search.Lucene;

public class AipLogradourosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipLogradouros> {
    @Inject
    private Lucene lucene;

	private static final long serialVersionUID = 3451356695639298011L;

	public List<Integer> listarLogradouros(Integer cep, Integer codigoTipoLogradouro) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AipLogradouros.class);

		criteria.createAlias(AipLogradouros.Fields.TIPO.toString(),
				AipLogradouros.Fields.TIPO.toString());

		criteria.createAlias(AipLogradouros.Fields.CEPS.toString(),
				AipLogradouros.Fields.CEPS.toString());

		criteria.add(Restrictions.eq(AipLogradouros.Fields.CEPS.toString()
				+ "." + AipCepLogradouros.Fields.CEP.toString(), cep));

		criteria.add(Restrictions.eq(AipLogradouros.Fields.TIPO.toString()
				+ "." + AipTipoLogradouros.Fields.CODIGO.toString(),
				codigoTipoLogradouro));
		
		criteria.setProjection(Projections.property(AipTipoLogradouros.Fields.CODIGO.toString()));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * Monta uma criteria projetando as propriedades do LogradouroVO.
	 * @param nomeLogradouro Filtra a pesquisa pelo nome do logradouro.
	 * @return Criteria com os filtros de pesquisa.
	 * @author mtocchetto
	 * @param aipCidades 
	 */
	private DetachedCriteria criarCriteriaLogradouroVO(String nomeLogradouro, AipCidades aipCidades) {
		//TODO Campos finais devem ficar definidos como constantes estáticas no início da classe.
		final String ALIAS_TIPO = "aipTipoLogradouro"; 
		final String ALIAS_TITULO = "aipTituloLogradouro";
		final String ALIAS_CIDADE = "aipCidade";
		final String ALIAS_CEPS = "aipCepLogradouros";
		final String ALIAS_BAIRRO_CEP_LOGRADOUROS = "bairroCepLogradouros";
		final String ALIAS_BAIRRO = "bairro";
		
		DetachedCriteria criteriaLogradouro = DetachedCriteria.forClass(AipLogradouros.class);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.TIPO.toString(), ALIAS_TIPO);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.TITULO.toString(), ALIAS_TITULO, Criteria.LEFT_JOIN);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.CIDADE.toString(), ALIAS_CIDADE);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.CEPS.toString(), ALIAS_CEPS, Criteria.LEFT_JOIN);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.BAIRRO_CEP_LOGRADOUROS.toString(), ALIAS_BAIRRO_CEP_LOGRADOUROS, Criteria.LEFT_JOIN);
		criteriaLogradouro.createAlias(AipLogradouros.Fields.BAIRRO.toString(), ALIAS_BAIRRO, Criteria.LEFT_JOIN);
		
		if (StringUtils.isNotBlank(nomeLogradouro)){
			criteriaLogradouro.add(Restrictions.ilike(AipLogradouros.Fields.NOME.toString(), nomeLogradouro, MatchMode.ANYWHERE));			
		}
		
		if (aipCidades != null){
			criteriaLogradouro.add(Restrictions.eq(AipLogradouros.Fields.CIDADE.toString(), aipCidades));                        
        }		
		// Obs: Foram criadas constantes apenas para as propriedades referenciadas a partir do ALIAS (ver createAlias).
		//TODO Estes campos nao se tornam constantes, apenas variaveis imutaveis. Colocar como static final no inicio da classe.
		final String FIELD_TIPO_DESCRICAO = ALIAS_TIPO + "." + AipTipoLogradouros.Fields.DESCRICAO.toString();
		final String FIELD_TITULO_DESCRICAO = ALIAS_TITULO + "." + AipTituloLogradouros.Fields.DESCRICAO.toString();
		final String FIELD_CIDADE_CEP = ALIAS_CIDADE + "." + AipCidades.Fields.CEP.toString();
		final String FIELD_CEPS_CODIGO_LOGRADOURO = ALIAS_CEPS + "." + AipCepLogradouros.Fields.CODIGO_LOGRADOURO.toString();
		final String FIELD_CEPS_CEP = ALIAS_CEPS + "." + AipCepLogradouros.Fields.CEP.toString();
		final String FIELD_CEPS_NRO_INICIAL = ALIAS_CEPS + "." + AipCepLogradouros.Fields.NRO_INICIAL.toString();
		final String FIELD_CEPS_NRO_FINAL = ALIAS_CEPS + "." + AipCepLogradouros.Fields.NRO_FINAL.toString();
		final String FIELD_CEPS_LADO = ALIAS_CEPS + "." + AipCepLogradouros.Fields.LADO.toString();
		final String FIELD_BAIRRO_CODIGO = ALIAS_BAIRRO + "." + AipBairros.Fields.CODIGO.toString();
		final String FIELD_BAIRRO_DESCRICAO = ALIAS_BAIRRO + "." + AipBairros.Fields.DESCRICAO.toString();
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(AipLogradouros.Fields.CODIGO.toString()));
		projectionList.add(Projections.property(FIELD_TIPO_DESCRICAO));
		projectionList.add(Projections.property(FIELD_TITULO_DESCRICAO));
		projectionList.add(Projections.property(AipLogradouros.Fields.NOME.toString()));
		projectionList.add(Projections.property(FIELD_CIDADE_CEP));
		projectionList.add(Projections.property(FIELD_CEPS_CODIGO_LOGRADOURO));
		projectionList.add(Projections.property(FIELD_CEPS_CEP));
		projectionList.add(Projections.property(FIELD_CEPS_NRO_INICIAL));
		projectionList.add(Projections.property(FIELD_CEPS_NRO_FINAL));
		projectionList.add(Projections.property(FIELD_CEPS_LADO));
		projectionList.add(Projections.property(FIELD_BAIRRO_CODIGO));
		projectionList.add(Projections.property(FIELD_BAIRRO_DESCRICAO));
		
		criteriaLogradouro.setProjection(projectionList);
		// Nao usar Order em Count
//		criteriaLogradouro.addOrder(Order.asc(AipLogradouros.Fields.NOME.toString()));
//		criteriaLogradouro.addOrder(Order.asc(FIELD_CEPS_CEP));
//		criteriaLogradouro.addOrder(Order.asc(FIELD_BAIRRO_DESCRICAO));
		
		return criteriaLogradouro;
	}	
	
	private void adicionarOrderByCriteriaLogradouroVO(DetachedCriteria criteriaLogradouro) {
		final String ALIAS_BAIRRO = "bairro";
		final String FIELD_BAIRRO_DESCRICAO = ALIAS_BAIRRO + "." + AipBairros.Fields.DESCRICAO.toString();
		final String ALIAS_CEPS = "aipCepLogradouros";
		final String FIELD_CEPS_CEP = ALIAS_CEPS + "." + AipCepLogradouros.Fields.CEP.toString();
		
		criteriaLogradouro.addOrder(Order.asc(AipLogradouros.Fields.NOME.toString()));
		criteriaLogradouro.addOrder(Order.asc(FIELD_CEPS_CEP));
		criteriaLogradouro.addOrder(Order.asc(FIELD_BAIRRO_DESCRICAO));		
	}
	
	public List<LogradouroVO> pesquisarLogradouroVO(Integer firstResult, Integer maxResults, String nome, AipCidades aipCidades) {
		DetachedCriteria criteria = criarCriteriaLogradouroVO(nome, aipCidades);
		adicionarOrderByCriteriaLogradouroVO(criteria);
		
		List<Object[]> resultList = executeCriteria(criteria, firstResult, maxResults, null, true);
		return criarListaLogradouroVO(resultList);
	}
	
	private List<LogradouroVO> criarListaLogradouroVO(List<Object[]> resultList) {
		List<LogradouroVO> listaLogradouroVO = new ArrayList<LogradouroVO>(resultList.size());
		
		for (Object[] col : resultList) {
			DominioLadoEndereco ladoEndereco = (DominioLadoEndereco) col[9];
			String lado = ladoEndereco == null ? null : ladoEndereco.getDescricao();
			
			LogradouroVO logradouroVO = new LogradouroVO();			
			logradouroVO.setCodigoLogradouro((Integer) col[0]);
			logradouroVO.setDescricaoTipoLogradouro((String) col[1]);
			logradouroVO.setDescricaoTituloLogradouro((String) col[2]);
			logradouroVO.setNomeLogradouro((String) col[3]);
			logradouroVO.setCepCidade((Integer) col[4]);
			logradouroVO.setAipCLILgrCodigo((Integer) col[5]);
			logradouroVO.setAipCLICep((Integer) col[6]);
			logradouroVO.setNroInicial((String) col[7]);
			logradouroVO.setNroFinal((String) col[8]);
			logradouroVO.setLado(lado);
			logradouroVO.setCodigoBairro((Integer) col[10]);
			logradouroVO.setDescricaoBairro((String) col[11]);
			
			listaLogradouroVO.add(logradouroVO);
		}
		
		return listaLogradouroVO;
	}

	
	public void cancelaEdicaoOuInclusaoLogradouro(AipLogradouros logradouro){
		//A solução adotada, foi de fazer um evict no objeto em questão, ao cancelar a edição/inclusão
		//pois estava ocorrendo um erro ao icluir um novo logradouro, após cancelar a edição dos CEPs/Bairros de um outro. Issues 1449 e 1494 - redmine.
		//Session session = (Session) this.getEntityManager().getDelegate();
		desatachar(logradouro);
	}
	
	public Long obterLogradouroVOCount(String nome, AipCidades aipCidades) {
		DetachedCriteria criteria = criarCriteriaLogradouroVO(nome, aipCidades);
		return executeCriteriaCount(criteria);
	}
	
	public List<AipLogradouros> pesquisarLogradouros(String campoAnalisado, String campoFonetico, String valor, Class<AipLogradouros> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, sortFields);
	}

	/**
	 * Faz a pesquisa de logradouro por codigo da cidade e descricao (igual)
	 * @param codigoCidade
	 * @param descricao
	 * @return slita de {@link AipLogradouros}
	 */
	public List<AipLogradouros> pesquisarLogradouros(Integer codigoCidade, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogradouros.class);
		
		criteria.add(Restrictions.eq(AipLogradouros.Fields.NOME.toString(), descricao));
		criteria.add(Restrictions.eq(AipLogradouros.Fields.CDD_CODIGO.toString(), codigoCidade));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #44799 C2
	 * @param filtro
	 * @return
	 */
	public List<AipLogradouros> obterAipLogradourosPorNome(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipLogradourosPorNome(filtro);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipLogradouros.Fields.CODIGO.toString()).as(AipLogradouros.Fields.CODIGO.toString()))
				.add(Projections.property(AipLogradouros.Fields.NOME.toString()).as(AipLogradouros.Fields.NOME.toString())
				));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipLogradouros.class));
		criteria.addOrder(Order.asc(AipLogradouros.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long obterAipLogradourosPorNomeCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipLogradourosPorNome(filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaAipLogradourosPorNome(Object filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogradouros.class);
		criteria.add(Restrictions.eq(AipLogradouros.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		String filtroPesquisa = filtro.toString();
		
		if(StringUtils.isNotBlank(filtroPesquisa)){
			criteria.add(Restrictions.ilike(AipLogradouros.Fields.NOME.toString(), filtroPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * #52025  CONSULTA UTILIZADA EM FUNCTION P9 AIPC_PROCEDENCIA_PAC
	 * @param codigo
	 * @return
	 */
	public Integer obterCddCodigoPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLogradouros.class);
		criteria.add(Restrictions.eq(AipLogradouros.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.property(AipLogradouros.Fields.CDD_COD.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
}
