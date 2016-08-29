package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapConselhosProfissionaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapConselhosProfissionais> {
	
	private static final long serialVersionUID = 2082996720171791306L;

	public enum ConselhoProfissionalDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CONSELHO_PROFISSIONAL_NAO_INFORMADO,
		MENSAGEM_ERRO_CODIGO_CONSELHO_PROFISSIONAL_JA_EXISTE,
		MENSAGEM_ERRO_NOME_CONSELHO_PROFISSIONAL_JA_EXISTE,
		MENSAGEM_ERRO_SIGLA_CONSELHO_PROFISSIONAL_JA_EXISTE;
	}	

	protected RapConselhosProfissionaisDAO() {
	}
	
	
	public String obterSiglaConselho(Short codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);
		criteria.setProjection(Projections.property(RapConselhosProfissionais.Fields.SIGLA.toString()));
		criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(), codigo));
		return (String) executeCriteriaUniqueResult(criteria, true);
	}
	
	/**
	 * Método para salvar um novo ConselhoProfissional
	 */
	
	public void salvar(RapConselhosProfissionais conselhoProfissional ) throws ApplicationBusinessException {

		if (conselhoProfissional == null) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_CONSELHO_PROFISSIONAL_NAO_INFORMADO);
		}	

		if (obterPorChavePrimaria(conselhoProfissional.getCodigo()) != null) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_ERRO_CODIGO_CONSELHO_PROFISSIONAL_JA_EXISTE);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);
		criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.NOME.toString(), conselhoProfissional.getNome()));

		if (executeCriteriaCount(criteria)>0) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_ERRO_NOME_CONSELHO_PROFISSIONAL_JA_EXISTE);
		}
		
		DetachedCriteria criteria1 = DetachedCriteria.forClass(RapConselhosProfissionais.class);
		criteria1.add(Restrictions.eq(RapConselhosProfissionais.Fields.SIGLA.toString(), conselhoProfissional.getSigla()));

		if (executeCriteriaCount(criteria1)>0) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_ERRO_SIGLA_CONSELHO_PROFISSIONAL_JA_EXISTE);
		}
		
		persistir(conselhoProfissional);
		flush();
	}
	
	/**
	 * Método para alterar um ConselhoProfissional
	 */
	
	public void alterar(RapConselhosProfissionais conselhoProfissional) throws ApplicationBusinessException {
		if (conselhoProfissional == null) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_CONSELHO_PROFISSIONAL_NAO_INFORMADO);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);
		criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.NOME.toString(), conselhoProfissional.getNome()));
		criteria.add(Restrictions.not(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(), conselhoProfissional.getCodigo())));

		if (executeCriteriaCount(criteria)>0) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_ERRO_NOME_CONSELHO_PROFISSIONAL_JA_EXISTE);
		}

		DetachedCriteria criteria1 = DetachedCriteria.forClass(RapConselhosProfissionais.class);		
		criteria1.add(Restrictions.eq(RapConselhosProfissionais.Fields.SIGLA.toString(), conselhoProfissional.getSigla()));
		criteria1.add(Restrictions.not(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(), conselhoProfissional.getCodigo())));

		if (executeCriteriaCount(criteria1)>0) {
			throw new ApplicationBusinessException(
					ConselhoProfissionalDAOExceptionCode.MENSAGEM_ERRO_SIGLA_CONSELHO_PROFISSIONAL_JA_EXISTE);
		}
		
		merge(conselhoProfissional);
		flush();
	}
	
	/**
	 * Método para pesquisar ConselhoProfissional na base de dados
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosProfissionais( Short codigo, String nome, 
			String sigla,
			DominioSituacao indSituacao,String tituloMasculino,String tituloFeminino, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, List<String>  conselhosLimitados) {

		DetachedCriteria criteria = montarCriteria(codigo, nome, sigla,
				indSituacao, tituloMasculino,tituloFeminino, conselhosLimitados );

		return executeCriteria(criteria, firstResult, maxResult,
				RapConselhosProfissionais.Fields.NOME.toString(), true);
	}
	
	/**
	 * Método que monta as restrições presentes na criteria 
	 */
	private DetachedCriteria montarCriteria(Short codigo, String nome, String sigla,
			DominioSituacao  indSituacao , String tituloMasculino,String tituloFeminino, List<String>  conselhosLimitados ) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapConselhosProfissionais.class);

		// código
		if (codigo != null) {
			criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(),
					codigo));
		}

		// nome
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(RapConselhosProfissionais.Fields.NOME
					.toString(), nome, MatchMode.ANYWHERE));
		}
		
		// sigla
		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(RapConselhosProfissionais.Fields.SIGLA
					.toString(), sigla, MatchMode.ANYWHERE));
		}
		
		// tituloMasculino
		if (StringUtils.isNotBlank(tituloMasculino)) {
			criteria.add(Restrictions.like(RapConselhosProfissionais.Fields.TITULO_MASCULINO
					.toString(), tituloMasculino, MatchMode.ANYWHERE));
		}

		// tituloFeminino
		if (StringUtils.isNotBlank(tituloFeminino)) {
			criteria.add(Restrictions.like(RapConselhosProfissionais.Fields.TITULO_FEMININO
					.toString(), tituloFeminino, MatchMode.ANYWHERE));
		}

		// situação (Ativo/Inativo)
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), indSituacao));
			
		}
		
		if(conselhosLimitados != null && conselhosLimitados.size() > 0){
			criteria.add(Restrictions.in(RapConselhosProfissionais.Fields.SIGLA.toString(), conselhosLimitados));
		}

		return criteria;
	}
	
	/**
	 * Método para contar a quantidade de Conselho Profissionais pesquisados
	 */
	public Long pesquisarConselhosProfissionaisCount(Short codigo, String nome, String sigla,
			DominioSituacao  indSituacao , String tituloMasculino,String tituloFeminino , List<String>  conselhosLimitados) {
		DetachedCriteria criteria = montarCriteria( codigo, nome, sigla, 
				indSituacao, tituloMasculino,tituloFeminino, conselhosLimitados );
		return executeCriteriaCount(criteria);
	}

	public RapConselhosProfissionais obterConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		DetachedCriteria criteria = getCriteriaObterConselhoProfissionalComNroRegConselho(
				matricula, vinCodigo, situacao);
		
		List<RapConselhosProfissionais> conselhos = executeCriteria(criteria);
		if(conselhos != null && !conselhos.isEmpty()){
			return conselhos.get(0);
		}
		
		return null;
	}

	private DetachedCriteria getCriteriaObterConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class, "rcp");
		criteria.createAlias("rcp." + RapConselhosProfissionais.Fields.RAP_TIPOS_QUALIFICACAOS.toString(), "tql");
		criteria.createAlias("tql." + RapTipoQualificacao.Fields.QUALIFICACAO.toString(), "qlf");
		criteria.createAlias("qlf." + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), "pes");
		criteria.createAlias("pes." + RapPessoasFisicas.Fields.MATRICULAS.toString(), "ser");
		
		
		criteria.add(Restrictions.eq("rcp." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.isNotNull("qlf." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		criteria.addOrder(Order.asc("rcp." + RapConselhosProfissionais.Fields.SIGLA.toString()));
		return criteria;
	}

	public String obterNroRegConselhoProfissional(Integer matricula,
			Short vinCodigo, DominioSituacao situacao) {
		DetachedCriteria criteria = getCriteriaObterConselhoProfissionalComNroRegConselho(
				matricula, vinCodigo, situacao);
		criteria.setProjection(Projections.property("qlf." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		
		List<String> conselhos = executeCriteria(criteria);
		if(conselhos != null && !conselhos.isEmpty()){
			return conselhos.get(0);
		}
		
		return null;
	}	
	
	public List<RapConselhosProfissionais> listarConselhoProfissionalComNroRegConselho(
			Integer matricula, Short vinCodigo, DominioSituacao situacao) {
		DetachedCriteria criteria = getCriteriaObterConselhoProfissionalComNroRegConselho(
				matricula, vinCodigo, situacao);
		
		return executeCriteria(criteria);
	}
	

	public List<RapConselhosProfissionais> listarConselhoProfissionalComNroRegConselhoColabAtivo(
			Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class, "rcp");
		criteria.createAlias("rcp." + RapConselhosProfissionais.Fields.RAP_TIPOS_QUALIFICACAOS.toString(), "tql");
		criteria.createAlias("tql." + RapTipoQualificacao.Fields.QUALIFICACAO.toString(), "qlf");
		criteria.createAlias("qlf." + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), "pes");
		criteria.createAlias("pes." + RapPessoasFisicas.Fields.MATRICULAS.toString(), "ser");
		
		criteria.add(Restrictions.eq("rcp." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotNull("qlf." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));

		criteria.add(Restrictions.or(Restrictions.eq("ser." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				                     Restrictions.and(Restrictions.eq("ser." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
				                    		          Restrictions.ge("ser." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));		
		                                              
		criteria.addOrder(Order.asc("rcp." + RapConselhosProfissionais.Fields.SIGLA.toString()));
 

		
		return executeCriteria(criteria);
	}	
	
	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem lista de conselhos para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Lista de {@link RapConselhosProfissionais}.
	 */
	public List<RapConselhosProfissionais> obterListaConselhoPorCodigoOuSigla(String parametro) {
		DetachedCriteria criteria = obterCriteriaConselhoPorCodigoOuSigla(parametro);
		return executeCriteria(criteria, 0, 100, RapConselhosProfissionais.Fields.SIGLA.toString(), true);
	}

	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem count de conselhos para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Quantidade de Registros retornados.
	 */
	public Long obterCountConselhoPorCodigoOuSigla(String parametro) {
		DetachedCriteria criteria = obterCriteriaConselhoPorCodigoOuSigla(parametro);
		return executeCriteriaCount(criteria);
	}

	

	private DetachedCriteria obterCriteriaConselhoPorCodigoOuSigla(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);		
		if (StringUtils.isNotBlank(parametro)) {			
			if (CoreUtil.isNumeroShort(parametro)) {				
				criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(RapConselhosProfissionais.Fields.SIGLA.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	
}
