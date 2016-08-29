package br.gov.mec.aghu.paciente.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.search.Lucene;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCaixaPostalComunitarias;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipGrandesUsuarios;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipUnidadeOperacao;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.model.VAipLocalidadeUc;

public class AipBairrosCepLogradouroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipBairrosCepLogradouro> {

	private static final long serialVersionUID = -8941939367531915155L;
	
	private static final int lIMITE_MAXIMO_OPERACORES_POSTGRES = 25000;
	  @Inject
	   private Lucene lucene;
	
	public List<AipBairrosCepLogradouro> buscarEnderecoPorCep(Integer cep) {
		DetachedCriteria bairroCepLogCriteria = DetachedCriteria.forClass(AipBairrosCepLogradouro.class);
		DetachedCriteria cepLogCriteria = bairroCepLogCriteria.createCriteria(AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO.toString());
		cepLogCriteria.add(Restrictions.eq(AipBairrosCepLogradouro.Fields.CEP.toString(), cep));

		return this.executeCriteria(bairroCepLogCriteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep,
			Integer codigoCidade) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer("select bcl  from AipBairrosCepLogradouro bcl join bcl.aipBairro bairro    ")
				.append("join bcl.cepLogradouro cepLog ")
				.append("join cepLog.logradouro as logradouro ")
				.append("join logradouro.aipCidade cidade ")
				.append("join logradouro.aipTipoLogradouro tipoLogradouro ")
				.append("left join logradouro.aipTituloLogradouro tituloLogradouro ")
				.append("where ")
				.append(" cidade.indSituacao = :situacaoCidade ")
				.append(" and cepLog.id.cep = :cep");

		if (codigoCidade != null) {
			hql.append(" and cidade.codigo = :codigoCidade");
		}
		
		Query consulta = this.createQuery(hql.toString());

		consulta.setParameter("cep", cep);
		consulta.setParameter("situacaoCidade", DominioSituacao.A);

		if (codigoCidade != null) {
			consulta.setParameter("codigoCidade", codigoCidade);
		}

		consulta.setMaxResults(30);
		return consulta.getResultList();
	}
	
	public Integer obterCountCeps(Integer cep,
			Integer codigoCidade) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer("select count(*)  from AipBairrosCepLogradouro bcl join bcl.aipBairro bairro    ")
				.append("join bcl.cepLogradouro cepLog ")
				.append("join cepLog.logradouro as logradouro ")
				.append("join logradouro.aipCidade cidade ")
				.append("join logradouro.aipTipoLogradouro tipoLogradouro ")
				.append("where ")
				.append(" cidade.indSituacao = :situacaoCidade ")
				.append(" and CAST(cepLog.id.cep as string) like '%").append(cep).append("%'");

		if (codigoCidade != null) {
			hql.append(" and cidade.codigo = :codigoCidade");
		}

		Query consulta = this.createQuery(hql.toString());
		
		consulta.setParameter("situacaoCidade", DominioSituacao.A);

		if (codigoCidade != null) {
			consulta.setParameter("codigoCidade", codigoCidade);
		}

	
		Long count = (Long) consulta.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<AipBairrosCepLogradouro> pesquisarCepExato(Integer cep) throws ApplicationBusinessException {
		StringBuffer hql = new StringBuffer("select bcl  from AipBairrosCepLogradouro bcl join fetch bcl.aipBairro bairro    ")
				.append("join fetch bcl.cepLogradouro cepLog ")
				.append("join fetch cepLog.logradouro as logradouro ")
				.append("join fetch logradouro.aipCidade cidade ")
				.append("join fetch logradouro.aipTipoLogradouro tipoLogradouro ")
				.append("left join logradouro.aipTituloLogradouro tituloLogradouro ")
				.append("where 1 = 1 ")
// Desconsiderar indLogradouro para corrigir incidente #28428
//				.append("and cidade.indLogradouro = :indLogradouro ")
				.append("and cepLog.id.cep = :cep ");
		
		Query consulta = this.createQuery(hql.toString());
		
		// Desconsiderar indLogradouro para corrigir incidente #28428
//		consulta.setParameter("indLogradouro", Boolean.TRUE);
		consulta.setParameter("cep", cep);
		
		consulta.setMaxResults(30);
		return consulta
				.getResultList();
	}

	public List<VAipLocalidadeUc> listarVAipLocalidadeUc(String param, Integer codigoCidade) {
		StringBuffer hql = new StringBuffer("select valu ")
									.append(" from VAipLocalidadeUc valu ")
									.append(" where valu.id.indSituacao = :situacaoCidade ");
			
			if (StringUtils.isNotBlank(param)) {
				hql.append(" and ( CAST(valu.cep as string) like '%").append(param).append("%' ");
				hql.append(" or lower(valu.id.nomeCidade) like :nomeCidade ");
				hql.append(" or lower(valu.descricaoBairro) like :descricaoBairro )");
			}
			
			if (codigoCidade != null) {
				hql.append(" and valu.id.codigoCidade = :codigoCidade");
			}

			hql.append(" order by valu.id.classificacao, valu.descricaoBairro, valu.id.nomeCidade ");
			
			Query consulta = this.createQuery(hql.toString());
			
			consulta.setParameter("situacaoCidade", DominioSituacao.A);
			
			if (StringUtils.isNotBlank(param)) {
				consulta.setParameter("nomeCidade", "%" + param.toLowerCase() + "%");
				consulta.setParameter("descricaoBairro", "%" + param.toLowerCase() + "%");
			}
			
			if (codigoCidade != null) {
				consulta.setParameter("codigoCidade", codigoCidade);
			}
			
			consulta.setMaxResults(100);
			
			return consulta.getResultList();
	}

	public Long listarVAipLocalidadeUcCount(String param, Integer codigoCidade) {
		StringBuffer hql = new StringBuffer("select count(*) from VAipLocalidadeUc valu ")
				.append(" where valu.id.indSituacao = :situacaoCidade ");
		
		if (StringUtils.isNotBlank(param)) {
			hql.append(" and ( CAST(valu.cep as string) like '%").append(param).append("%' ");
			hql.append(" or lower(valu.id.nomeCidade) like :nomeCidade ");
			hql.append(" or lower(valu.descricaoBairro) like :descricaoBairro ) ");
		}
		
		if (codigoCidade != null) {
			hql.append(" and valu.id.codigoCidade = :codigoCidade");
		}

		Query consulta = this.createQuery(hql.toString());

		consulta.setParameter("situacaoCidade", DominioSituacao.A);

		if (StringUtils.isNotBlank(param)) {
			consulta.setParameter("nomeCidade", "%" + param.toLowerCase()
					+ "%");
			consulta.setParameter("descricaoBairro", "%" + param.toLowerCase()
					+ "%");
		}
		
		if (codigoCidade != null) {
			consulta.setParameter("codigoCidade", codigoCidade);
		}

		return (Long) consulta.getSingleResult();
	}

	@SuppressWarnings({"unchecked", "PMD.NPathComplexity"})
	public List<AipBairrosCepLogradouro> pesquisarLogradourosCepPorDescricaoCidade(String descricao, Integer codigoCidade,
			List<AipLogradouros> logradourosList, List<AipTipoLogradouros> tipoLogradourosList) {
		StringBuffer hql = new StringBuffer("select bcl  from AipBairrosCepLogradouro bcl join bcl.aipBairro bairro    ")
				.append("join bcl.cepLogradouro cepLog ")
				.append("join cepLog.logradouro as logradouro ")
				.append("join logradouro.aipCidade cidade ")
				.append("join logradouro.aipTipoLogradouro tipoLogradouro ")
				.append("left join logradouro.aipTituloLogradouro tituloLogradouro ")
				.append("where 1 = 1 ");
				//.append("and cidade.indLogradouro = :indLogradouro "); Desconsiderar indLogradouro para corrigir incidente #28428
		if (!StringUtils.isBlank(descricao) && (!logradourosList.isEmpty()||!tipoLogradourosList.isEmpty()) ) {
			hql.append(" and ( ");
		}
		if (logradourosList == null) {
			logradourosList = new ArrayList<AipLogradouros>();
		}
		
		Query consulta;
		
		//consulta.setParameter("indLogradouro", Boolean.TRUE);
		List<AipBairrosCepLogradouro> resultado = new ArrayList<AipBairrosCepLogradouro>();
		
		Integer numeroRepeticoes = 1 + logradourosList.size() / lIMITE_MAXIMO_OPERACORES_POSTGRES;
		for (int i = 0; i < numeroRepeticoes; i++) {
		//Utilizei um HashMap para casos em que a logradourosList ultrapassa 1000 valores. Por algumas limitações de banco,
		//a query pode não ser processada utilizadno mais de 1000 valores no IN(....). Assim, quando passar este número,
		//os elementos serão subdivididos em outras listas e armazenados pelo HashMap para posteriormente serem setados
		//por parâmetro na query
		Map mapListas = new HashMap();
		if (!StringUtils.isBlank(descricao) && !logradourosList.isEmpty()) {
			if (logradourosList.size() > 1000){
				Integer numeroListas = 1 + logradourosList.size()/1000;
				List<AipLogradouros> logradourosListTemp = logradourosList.subList(0, 999);
				mapListas.put("map0", logradourosListTemp);
				hql.append(" cepLog.logradouro  in (:map0) ");	
				Integer inicio = 1000;
				for (int i1=1; i1<numeroListas; i1++){
					if (inicio + 999 > logradourosList.size()-1){
						logradourosListTemp = logradourosList.subList(inicio, logradourosList.size()-1);						
					}
					else {
						logradourosListTemp = logradourosList.subList(inicio, inicio + 999);		
					}
					mapListas.put("map" +i1, logradourosListTemp);
					inicio = inicio + 999;
					hql.append("or cepLog.logradouro in (:map" + i1 + ") ");
				}
			}
			else{
				hql.append(" cepLog.logradouro  in (:logradouroList) ");	
			}
		}
		if (!StringUtils.isBlank(descricao) && (!logradourosList.isEmpty() && !tipoLogradourosList.isEmpty()) ) {
			hql.append(" or ");
		}
		if (!StringUtils.isBlank(descricao) && !tipoLogradourosList.isEmpty()) {
			hql.append(" logradouro.aipTipoLogradouro in (:tipoLogradouroList) ");
		}
		if (!StringUtils.isBlank(descricao) && (!logradourosList.isEmpty()||!tipoLogradourosList.isEmpty()) ) {
			hql.append(" ) ");
		}
		
		if (codigoCidade != null) {
			hql.append(" and cidade.codigo = :codigoCidade");
		}
	
		consulta = this.createQuery(hql.toString());
	
		if (!StringUtils.isBlank(descricao) && !logradourosList.isEmpty() && logradourosList.size() <= 1000) {
			consulta.setParameter("logradouroList", logradourosList);
		}
		else {
			for (int i2 = 0; i2 < mapListas.size(); i2++) {
				consulta.setParameter("map" + i2, mapListas.get("map" + i2));			
			}
		}
	
		if (!StringUtils.isBlank(descricao) && !tipoLogradourosList.isEmpty()) {
			consulta.setParameter("tipoLogradouroList", tipoLogradourosList);
		}
		
		//consulta.setParameter("indLogradouro", Boolean.TRUE);
	
		if (codigoCidade != null) {
			consulta.setParameter("codigoCidade", codigoCidade);
		}
	
		consulta.setMaxResults(100);
		
		resultado.addAll(consulta.getResultList());
		} 
		
		return resultado;
	}
	
	public List<VAipCeps> pesquisaFoneticaVAipCepsPorLogradouroCidade(String campoAnalisado, String campoFonetico, String valor, Class<VAipCeps> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, sortFields);
	}

	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(String descricao, Integer codigoCidade,
			List<AipLogradouros> logradourosList, List<AipTipoLogradouros> tipoLogradourosList, List<Integer> cepList) {
		return pesquisarVAipCepsPorLogradouroCidade(descricao, codigoCidade, logradourosList, tipoLogradourosList, cepList, false);
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(String descricao, Integer codigoCidade,
			List<AipLogradouros> logradourosList, List<AipTipoLogradouros> tipoLogradourosList, List<Integer> cepList, boolean orderByLogradouro) {
		StringBuffer hql = new StringBuffer("select ceps  from VAipCeps ceps ")
				.append("left join ceps.aipLogradouro as logradouro ")
				.append("where ceps.indSituacao = :indSituacao ");
		
		if ((logradourosList != null && !logradourosList.isEmpty())
				|| (tipoLogradourosList != null && !tipoLogradourosList.isEmpty())
				|| (cepList != null && !cepList.isEmpty())) {
			hql.append(" and ( ");
		}

		//Utilizei um HashMap para casos em que a logradourosList ultrapassa 1000 valores. Por algumas limitações de banco,
		//a query pode não ser processada utilizadno mais de 1000 valores no IN(....). Assim, quando passar este número,
		//os elementos serão subdivididos em outras listas e armazenados pelo HashMap para posteriormente serem setados
		//por parâmetro na query
		Map mapListas = gerarMapLogradouroList(descricao, logradourosList, hql);

		setarFiltrosBuscaLogradouros(descricao, codigoCidade, logradourosList, tipoLogradourosList, cepList, hql);

		if ((logradourosList != null && !logradourosList.isEmpty())
				|| (tipoLogradourosList != null && !tipoLogradourosList.isEmpty())
				|| (cepList != null && !cepList.isEmpty())) {
			hql.append(" ) ");
		}

		if (StringUtils.isNotBlank(descricao)
				&& (logradourosList == null || logradourosList.isEmpty())
				&& (tipoLogradourosList == null || tipoLogradourosList.isEmpty())
				&& (cepList == null || cepList.isEmpty())){
			hql.append(" and (UPPER(ceps.logradouro)  like '%" + descricao + "%' ");
			hql.append(" or UPPER(ceps.nome)  like '%" + descricao + "%' )");
		}
		
		if (codigoCidade != null) {
			hql.append(" and ceps.codigoCidade = :codigoCidade");
		}
		
		if (orderByLogradouro) {
			hql.append(" order by ceps.logradouro asc ");
		}

		Query consulta = this.createQuery(hql.toString());

		setarParametrosBuscaLogradouros(descricao, codigoCidade,
				logradourosList, tipoLogradourosList, cepList, mapListas,
				consulta);
	
		consulta.setMaxResults(200);
		return inicializarRegistrosLazy(consulta);
	}
	
	private List<VAipCeps> inicializarRegistrosLazy(Query consulta){
		List<VAipCeps> retorno = consulta.getResultList();
		if(retorno != null){
			for (VAipCeps vAipCeps : retorno) {
				Hibernate.initialize(vAipCeps.getAipCidade());
			}
		}
		
		return retorno;
	}

	private void setarParametrosBuscaLogradouros(String descricao,
			Integer codigoCidade, List<AipLogradouros> logradourosList,
			List<AipTipoLogradouros> tipoLogradourosList,
			List<Integer> cepList, Map mapListas, Query consulta) {
		consulta.setParameter("indSituacao", DominioSituacao.A);
	
		if (!StringUtils.isBlank(descricao) && logradourosList != null && !logradourosList.isEmpty() && logradourosList.size() <= 1000) {
			consulta.setParameter("logradouroList", logradourosList);
		}
		else{
			for (int i=0; i<mapListas.size(); i++){
				consulta.setParameter("map" + i, mapListas.get("map" + i));				
			}
		}
	
		if (!StringUtils.isBlank(descricao) && tipoLogradourosList != null && !tipoLogradourosList.isEmpty()) {
			consulta.setParameter("tipoLogradouroList", tipoLogradourosList);
		}

		if (cepList != null && !cepList.isEmpty()) {
			consulta.setParameter("cepList", cepList);
		}
		
		if (codigoCidade != null) {
			consulta.setParameter("codigoCidade", codigoCidade);
		}
	}

	private void setarFiltrosBuscaLogradouros(String descricao,
			Integer codigoCidade, List<AipLogradouros> logradourosList,
			List<AipTipoLogradouros> tipoLogradourosList,
			List<Integer> cepList, StringBuffer hql) {

		if (!StringUtils.isBlank(descricao)
				&& logradourosList != null && tipoLogradourosList != null
				&& !logradourosList.isEmpty() && !tipoLogradourosList.isEmpty() ) {
			hql.append(" or ");
		}
		if (!StringUtils.isBlank(descricao) && tipoLogradourosList != null && !tipoLogradourosList.isEmpty()) {
			hql.append(" logradouro.aipTipoLogradouro in (:tipoLogradouroList) ");
		}
		if (StringUtils.isNotBlank(descricao) && cepList != null && !cepList.isEmpty() 
				&& ((logradourosList != null && !logradourosList.isEmpty()) || (tipoLogradourosList != null && !tipoLogradourosList.isEmpty()))){
			hql.append(" or ");
		}
		if (cepList != null && !cepList.isEmpty()){
			hql.append(" ceps.id.cep in (:cepList) ");
		}
	}

	private Map gerarMapLogradouroList(String descricao,
			List<AipLogradouros> logradourosList, StringBuffer hql) {
		Map mapListas = new HashMap();
		if (!StringUtils.isBlank(descricao) && logradourosList != null && !logradourosList.isEmpty()) {
			if (logradourosList.size() > 1000){
				Integer numeroListas = 1 + logradourosList.size()/1000;
				List<AipLogradouros> logradourosListTemp = logradourosList.subList(0, 999);
				mapListas.put("map0", logradourosListTemp);
				hql.append(" ceps.aipLogradouro  in (:map0) ");	
				Integer inicio = 1000;
				for (int i=1; i<numeroListas; i++){
					if (inicio + 999 > logradourosList.size()-1){
						logradourosListTemp = logradourosList.subList(inicio, logradourosList.size()-1);						
					}
					else{
						logradourosListTemp = logradourosList.subList(inicio, inicio + 999);		
					}
					mapListas.put("map"+i, logradourosListTemp);
					inicio = inicio + 999;
					hql.append("or ceps.aipLogradouro in (:map" + i + ") ");
				}
			}
			else{
				hql.append(" ceps.aipLogradouro  in (:logradouroList) ");	
			}
		}
		return mapListas;
	}
	
	/**
	 * Método que retorna um registro de bairroCepLogradouro pelo número do CEP
	 * @param cep
	 * @return
	 */
	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCep(Integer cep){
		AipBairrosCepLogradouro retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairrosCepLogradouro.class);
		criteria.add(Restrictions.eq(AipBairrosCepLogradouro.Fields.CEP.toString(), cep));
		List<AipBairrosCepLogradouro> listaCeps = executeCriteria(criteria);
		if (!listaCeps.isEmpty()){
			retorno = listaCeps.get(0);
		}
		return retorno;
	}

	/**
	 * Método que retorna um registro de bairroCepLogradouro pelo número do CEP
	 * @param cep
	 * @return
	 */
	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCepBairroLogradouro(Integer cep, Integer codBairro, Integer codLogradouro){
		AipBairrosCepLogradouro retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairrosCepLogradouro.class);
		criteria.add(Restrictions.eq(AipBairrosCepLogradouro.Fields.CEP.toString(), cep));
		if (codBairro != null){
			criteria.add(Restrictions.eq(AipBairrosCepLogradouro.Fields.CODIGO_BAIRRO.toString(), codBairro));
		}
		if(codLogradouro != null){
			criteria.add(Restrictions.eq(AipBairrosCepLogradouro.Fields.CODIGO_LOGRADOURO.toString(), codLogradouro));
		}
		List<AipBairrosCepLogradouro> listaCeps = executeCriteria(criteria);
		if (!listaCeps.isEmpty()){
			retorno = listaCeps.get(0);
		}
		return retorno;
	}


	public Long obterCountCepExato(Integer cep) {
		StringBuffer hql = new StringBuffer("select count(*)  from AipBairrosCepLogradouro bcl join bcl.aipBairro bairro    ")
				.append("join bcl.cepLogradouro cepLog ")
				.append("join cepLog.logradouro as logradouro ")
				.append("join logradouro.aipCidade cidade ")
				.append("join logradouro.aipTipoLogradouro tipoLogradouro ")
				.append("where ")
				.append(" cidade.indSituacao = :situacaoCidade ")
				.append(" and cepLog.id.cep = :cep");


		Query consulta = this.createQuery(hql.toString());
		
		consulta.setParameter("situacaoCidade", DominioSituacao.A);
		consulta.setParameter("cep", cep);

	
		return (Long) consulta.getSingleResult();
	}

	public VAipCeps obterVAipCeps(Integer cep, Integer codLogradouro,
			Integer codBairroLogradouro) {
		StringBuffer hql = new StringBuffer()
		.append("select vCeps from VAipCeps vCeps ")
		.append("where vCeps." + VAipCeps.Fields.CEP.toString() + " = :cep ");

		filtroObterVAipCeps(codLogradouro, codBairroLogradouro, hql);
		
		Query consulta = this.createQuery(hql.toString());
		consulta.setParameter("cep", cep);

		parametrosObterVAipCeps(codLogradouro, codBairroLogradouro, consulta);
		
		List<VAipCeps> result = consulta.getResultList();
		if(result != null){
			for(VAipCeps vAipCeps : result ){
				Hibernate.initialize(vAipCeps.getAipCidade().getAipUf());
			}
		}
		return result !=  null && !result.isEmpty() ? result.get(0): null;
	}

	private void parametrosObterVAipCeps(Integer codLogradouro,
			Integer codBairroLogradouro, Query consulta) {
		if(codLogradouro != null){
			consulta.setParameter("codigoLogradouro", codLogradouro);
		}
		if(codBairroLogradouro != null){
			consulta.setParameter("codigoBairroLogradouro", codBairroLogradouro);
		}
	}

	private void filtroObterVAipCeps(Integer codLogradouro,
			Integer codBairroLogradouro, StringBuffer hql) {
		if(codLogradouro != null){
			hql.append("and vCeps." + VAipCeps.Fields.LGR_CODIGO.toString() + " = :codigoLogradouro ");
		}
		
		if(codBairroLogradouro != null){
			hql.append("and vCeps." + VAipCeps.Fields.LGR_BAI_CODIGO.toString() + " = :codigoBairroLogradouro ");
		}
	}

	public List<VAipCeps> pesquisarVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException{
		StringBuffer hql = new StringBuffer("select vCeps from VAipCeps vCeps ");
		
		filtroPesquisarVAipCeps(cep, logradouro, cidade, hql);
		
		Query consulta = this.createQuery(hql.toString());

		parametrosPesquisarVAipCeps(cep, logradouro, cidade, consulta);		
		
		List<VAipCeps> result =  consulta.getResultList();
		if(result != null){
			for(VAipCeps vAipCeps : result ){
				Hibernate.initialize(vAipCeps.getAipCidade().getAipUf());
			}
		}
		return result;
	}
	
	public Integer obterCountVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException{
		StringBuffer hql = new StringBuffer("select count(*) from VAipCeps vCeps ");
		
		filtroPesquisarVAipCeps(cep, logradouro, cidade, hql);
		
		Query consulta = this.createQuery(hql.toString());

		parametrosPesquisarVAipCeps(cep, logradouro, cidade, consulta);	
		
		Long count = (Long)consulta.getSingleResult();
		return count !=  null ? count.intValue(): null;
	}

	private void parametrosPesquisarVAipCeps(Integer cep, String logradouro,
			String cidade, Query consulta) {
		consulta.setParameter("situacao", DominioSituacao.A);
		if(cep != null && cep > 0){
			consulta.setParameter("cep", cep);
		}
		if(StringUtils.isNotEmpty(logradouro)){
			consulta.setParameter("logradouro", logradouro);
		}
		if(StringUtils.isNotEmpty(cidade)){
			consulta.setParameter("cidade", cidade);
		}
	}

	private void filtroPesquisarVAipCeps(Integer cep, String logradouro,
			String cidade, StringBuffer hql) {
		hql.append("where vCeps." + VAipCeps.Fields.IND_SITUACAO.toString() +" = :situacao ");
		if(cep != null && cep > 0){
			hql.append("and vCeps." + VAipCeps.Fields.CEP.toString() + " = :cep ");
		}
		if(StringUtils.isNotEmpty(logradouro)){
			hql.append("and vCeps." + VAipCeps.Fields.LOGRADOURO.toString() + " = :logradouro ");
		}
		if(StringUtils.isNotEmpty(cidade)){
			hql.append("and vCeps." + VAipCeps.Fields.CIDADE.toString() + " = :cidade ");
		}
	}

	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(
			String logradouro, Integer codigoCidade) {
		StringBuffer hql = new StringBuffer()
		.append("select vCeps from VAipCeps vCeps ")
		.append("where vCeps." + VAipCeps.Fields.IND_SITUACAO.toString() +" = :situacao ")
		.append("and vCeps." + VAipCeps.Fields.LOGRADOURO.toString() +" like '%"+logradouro+"%' ")
		.append("and vCeps." + VAipCeps.Fields.CDD_CODIGO.toString() +" = :codigoCidade ");
		
		Query consulta = this.createQuery(hql.toString());
		consulta.setParameter("situacao", DominioSituacao.A);
		consulta.setParameter("codigoCidade", codigoCidade);
		
		List<VAipCeps> result = consulta.getResultList();
		return result;
	}

	public List<VAipCeps> pesquisarVaipCepUnicoPorCidade(Integer codigoCidade) {
		StringBuffer hql = new StringBuffer()
		.append("select vCeps from VAipCeps vCeps ")
		.append("where vCeps." + VAipCeps.Fields.IND_SITUACAO.toString() +" = :situacao ")
		.append("and vCeps." + VAipCeps.Fields.CLASSIFICACAO.toString() +" = :classificacao ")
		.append("and vCeps." + VAipCeps.Fields.CDD_CODIGO.toString() +" = :codigoCidade ");
		
		Query consulta = this.createQuery(hql.toString());
		consulta.setParameter("situacao", DominioSituacao.A);
		consulta.setParameter("classificacao", "1");
		consulta.setParameter("codigoCidade", codigoCidade);
		consulta.setMaxResults(2);
		List<VAipCeps> result = consulta.getResultList();
		return result;
	}

	public List<AipUnidadeOperacao> pesquisaFoneticaAipUnidadeOperacao(String campoAnalisado, String campoFonetico, String valor, Class<AipUnidadeOperacao> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, null);
	}

	public List<AipCaixaPostalComunitarias> pesquisaFoneticaAipCaixaPostalComunitaria(
			String campoAnalisado, String campoFonetico, String valor,
			Class<AipCaixaPostalComunitarias> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, null);
	}

	public List<AipGrandesUsuarios> pesquisaFoneticaAipGrandesUsuarios(
			String campoAnalisado, String campoFonetico, String valor,
			Class<AipGrandesUsuarios> modelClazz, String... sortFields) {
		return lucene.executeLuceneQuery(campoAnalisado, campoFonetico, valor, modelClazz, null, null);
	}	

	public List<AipUnidadeOperacao> pesquisaAipUnidadeOperacaoPorCodigoCidade(
			Integer codigoCidade) {
		DetachedCriteria criteriaUnidadeOperacao = DetachedCriteria.forClass(AipUnidadeOperacao.class);
		DetachedCriteria criteriaCidade = criteriaUnidadeOperacao.createCriteria(AipUnidadeOperacao.Fields.CIDADE.toString());
		criteriaCidade.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), codigoCidade));

		return this.executeCriteria(criteriaCidade);
	}

	public List<AipCaixaPostalComunitarias> pesquisaAipCaixaPostalComunitariaPorCodigoCidade(
			Integer codigoCidade) {
		DetachedCriteria criteriaCaixaPostalComunitarias = DetachedCriteria.forClass(AipCaixaPostalComunitarias.class);
		DetachedCriteria criteriaCidade = criteriaCaixaPostalComunitarias.createCriteria(AipCaixaPostalComunitarias.Fields.CIDADE.toString());
		criteriaCidade.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), codigoCidade));

		return this.executeCriteria(criteriaCidade);
	}

	public List<AipGrandesUsuarios> pesquisaAipGrandesUsuariosPorCodigoCidade(
			Integer codigoCidade) {
		DetachedCriteria criteriaGrandesUsuarios = DetachedCriteria.forClass(AipGrandesUsuarios.class);
		DetachedCriteria criteriaCidade = criteriaGrandesUsuarios.createCriteria(AipGrandesUsuarios.Fields.CIDADE.toString());
		criteriaCidade.add(Restrictions.eq(AipCidades.Fields.CODIGO.toString(), codigoCidade));

		return this.executeCriteria(criteriaCidade);
	}
	
	public AipBairrosCepLogradouro obterAipBairrosCepLogradouroPorId(AipBairrosCepLogradouroId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairrosCepLogradouro.class, "BCL");
		criteria.createAlias("BCL." + AipBairrosCepLogradouro.Fields.AIP_LOGRADOURO.toString(), "LGR", JoinType.INNER_JOIN);
		criteria.createAlias("BCL." + AipBairrosCepLogradouro.Fields.BAIRRO.toString(), "BAI", JoinType.INNER_JOIN);
		criteria.createAlias("LGR." + AipLogradouros.Fields.CIDADE.toString(), "CID", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("BCL." + AipBairrosCepLogradouro.Fields.ID.toString(), id));
		return (AipBairrosCepLogradouro) this.executeCriteriaUniqueResult(criteria);
		
	}
}
