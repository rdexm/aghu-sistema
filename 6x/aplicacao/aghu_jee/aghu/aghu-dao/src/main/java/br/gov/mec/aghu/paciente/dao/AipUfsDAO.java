package br.gov.mec.aghu.paciente.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipUfs;

public class AipUfsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipUfs>{
	
	private static final long serialVersionUID = -4710829471635990874L;
	
	private static final Log LOG = LogFactory.getLog(AipUfsDAO.class);

	private DetachedCriteria createPesquisaUFsCriteria(String sigla,
			String nome, String siglaPais, DominioSimNao cadastraCidades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipUfs.class);
		
		criteria.createAlias(AipUfs.Fields.PAIS.toString(), "pais");
		
		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(AipUfs.Fields.SIGLA.toString(),
					sigla, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AipUfs.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(siglaPais)) {
			criteria.add(Restrictions.ilike(
					AipUfs.Fields.SIGLA_PAIS.toString(), siglaPais,
					MatchMode.ANYWHERE));
		}

		if (cadastraCidades != null) {
			criteria.add(Restrictions.eq(AipUfs.Fields.IND_CIDADE.toString(),
					cadastraCidades.isSim()));
		}

		return criteria;
	}

	public List<AipUfs> pesquisaUFs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String sigla, String nome,
			String siglaPais, DominioSimNao cadastraCidades) {
		DetachedCriteria criteria = createPesquisaUFsCriteria(sigla, nome,
				siglaPais, cadastraCidades);

		criteria.addOrder(Order.asc(AipUfs.Fields.SIGLA.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	
	public Long pesquisaUFsCount(String sigla, String nome,
			String siglaPais, DominioSimNao cadastraCidades) {
		return executeCriteriaCount(createPesquisaUFsCriteria(sigla, nome,
				siglaPais, cadastraCidades));
	}
	
	public AipUfs obterUF(String aipSiglaUF) {
		try{
			AipUfs ufRetorno = (AipUfs)find(AipUfs.class, aipSiglaUF); 
			
			return ufRetorno;
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public List<AipUfs> pesquisarUfsPorPais(AipPaises pais){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipUfs.class);
		criteria.add(Restrictions.eq(AipUfs.Fields.SIGLA_PAIS.toString(), pais.getSigla()));
		
		return executeCriteria(criteria);
	}
	
	public AipUfs obterUfSemLike(String aipSiglaUF) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipUfs.class);

		criteria.add(Restrictions.eq(AipUfs.Fields.SIGLA.toString(),
				aipSiglaUF));

		return (AipUfs) executeCriteriaUniqueResult(criteria);
	}

	
	/**
	 * Método para listar as UFs em um combo de acordo com o parâmetro
	 * informado. O parâmetro pode ser tanto a sigla quanto parte do nome da UF.
	 * A lista montada contém primeiro resultados pesquisado pela sigla, seguida
	 * dos resultados da pesquisa pelo nome.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public List<AipUfs> pesquisarPorSiglaNome(Object paramPesquisa) {
		String strPesquisa = (String) paramPesquisa;

		DetachedCriteria cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.ilike(AipUfs.Fields.SIGLA
				.toString(), strPesquisa, MatchMode.START));
		
		List<AipUfs> r = executeCriteria(cri);
		Set<AipUfs> li = new LinkedHashSet<AipUfs>(r);

		cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.ilike(
				AipUfs.Fields.NOME.toString(), strPesquisa,
				MatchMode.ANYWHERE));
		
		r = executeCriteria(cri);
		li.addAll(r);

		return new ArrayList<AipUfs>(li);
		
	}
	
	public List<AipUfs> pesquisarPorSiglaNomePermiteCidades(Object paramPesquisa) {
		String strPesquisa = (String) paramPesquisa;

		DetachedCriteria cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.ilike(AipUfs.Fields.SIGLA
				.toString(), strPesquisa, MatchMode.START));
		
		cri.add(Restrictions.eq(AipUfs.Fields.IND_CIDADE.toString(), true));
		
		List<AipUfs> r = executeCriteria(cri);
		Set<AipUfs> li = new LinkedHashSet<AipUfs>(r);

		cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.ilike(
				AipUfs.Fields.NOME.toString(), strPesquisa,
				MatchMode.ANYWHERE));
		cri.add(Restrictions.eq(AipUfs.Fields.IND_CIDADE.toString(), true));
		
		r = executeCriteria(cri);
		li.addAll(r);

		return new ArrayList<AipUfs>(li);
		
	}
	
	/**
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public Long obterCountUfPorSiglaNome(Object paramPesquisa) {
		String strPesquisa = (String) paramPesquisa;

		DetachedCriteria cri = DetachedCriteria
		.forClass(AipUfs.class);
		cri.add(Restrictions.or(Restrictions.ilike(AipUfs.Fields.SIGLA
				.toString(), strPesquisa, MatchMode.START), Restrictions.ilike(
						AipUfs.Fields.NOME.toString(), strPesquisa,
						MatchMode.ANYWHERE))  );
		
		Long count = executeCriteriaCount(cri);	

		return count;
		
	}
	
	/**
	 * Idem método anterior
	 * Se encontra valor por sigla, retorna este valor
	 * Caso contrário, pesquisa por nome
	 * @param valueOf
	 * @return
	 */
	public List<AipUfs> pesquisarPorSiglaEntaoNome(String valor) {
		DetachedCriteria criteria = criarCriteriaTodasUfs();
		if (valor != null) {
			criteria.add(Restrictions.ilike(AipUfs.Fields.SIGLA.toString(), valor, MatchMode.EXACT));
		}
		List<AipUfs> result = executeCriteria(criteria);
		if (result.size() == 1) {
			return result;
		}

		criteria = criarCriteriaTodasUfs();
		if (valor != null) {
			criteria.add(Restrictions.ilike(AipUfs.Fields.NOME.toString(), valor, MatchMode.ANYWHERE));
		}
		result = executeCriteria(criteria);
		
		return result;
	}
	
	public List<AipUfs> pesquisarPorSiglaNomeSemLike(Object paramPesquisa) {
		String strPesquisa = (String) paramPesquisa;

		DetachedCriteria cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.eq(AipUfs.Fields.SIGLA
				.toString(), strPesquisa));
		
		List<AipUfs> r = executeCriteria(cri);
		Set<AipUfs> li = new LinkedHashSet<AipUfs>(r);

		cri = criarCriteriaTodasUfs();
		cri.add(Restrictions.eq(
				AipUfs.Fields.NOME.toString(), strPesquisa));
		
		r = executeCriteria(cri);
		li.addAll(r);

		return new ArrayList<AipUfs>(li);
		
	}
	
	
	private DetachedCriteria criarCriteriaTodasUfs() {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AipUfs.class);
		
		cri.addOrder(Order.asc(AipUfs.Fields.NOME.toString()));
		
		return cri;
	}

	/**
	 * #44799 C3
	 * @param filtro
	 * @return
	 */
	public List<AipUfs> obterAipUfsPorSiglaNome(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipUfsPorSiglaNome(filtro);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipUfs.Fields.SIGLA.toString()).as(AipUfs.Fields.SIGLA.toString()))
				.add(Projections.property(AipUfs.Fields.NOME.toString()).as(AipUfs.Fields.NOME.toString()))
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipUfs.class));
		criteria.addOrder(Order.asc(AipUfs.Fields.SIGLA.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long obterAipUfsPorSiglaNomeCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipUfsPorSiglaNome(filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaAipUfsPorSiglaNome(Object filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipUfs.class);
		
		if(filtro != null){
			String filtroPesquisa = filtro.toString();
			if(StringUtils.isNotBlank(filtroPesquisa)){
				criteria.add(Restrictions.or(Restrictions.ilike(AipUfs.Fields.NOME.toString(), filtroPesquisa, MatchMode.ANYWHERE),
						Restrictions.ilike(AipUfs.Fields.SIGLA.toString(), filtroPesquisa, MatchMode.ANYWHERE)));
			}
		}
		
		
		return criteria;
	}
}

