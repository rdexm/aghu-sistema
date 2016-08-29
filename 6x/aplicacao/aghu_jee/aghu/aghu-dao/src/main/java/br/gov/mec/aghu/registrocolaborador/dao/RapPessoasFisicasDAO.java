package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockOptions;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * 
 * @modulo registrocolaborador
 *
 */

public class RapPessoasFisicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapPessoasFisicas> {
	
	private static final long serialVersionUID = 1242952552270291579L;
	
	public int obterNumeroDependentes(final Integer codigoPessoa) {
		if (codigoPessoa == null) {
			throw new IllegalArgumentException("parametro nulo");
		}
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapDependentes.class);
		criteria.add(Restrictions.eq(RapDependentes.Fields.PES_CODIGO.toString(), codigoPessoa));
		Long result = executeCriteriaCount(criteria);
		return result.intValue();
	}
	
	public List<RapPessoasFisicas> buscarPessoaFisica(String paramPesquisa) {
		return executeCriteria(obterCriteriaBuscarPessoaFisica(paramPesquisa), 0, 100, RapPessoasFisicas.Fields.CODIGO.toString(), true);
	}
	
	public Long buscarPessoaFisicaCount(Object paramPesquisa) {
		return executeCriteriaCount(obterCriteriaBuscarPessoaFisica(paramPesquisa));
	}

	private DetachedCriteria obterCriteriaBuscarPessoaFisica(Object paramPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class);

		final String strPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.like(RapPessoasFisicas.Fields.NOME.toString(),(strPesquisa).toUpperCase(), MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	/**
	 * Retorna pessoa física de acordo com o código ou nome informados
	 * 
	 * @dbtables RapPessoasFisicas select
	 * 
	 * @param codigo
	 *            ou nome
	 * @return pessoas físicas encontradas ou lista vazia se não encontrado
	 */
	public List<RapPessoasFisicas> pesquisarPessoaFisicaPorCodigoNome(
			Object pessoaFisica) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapPessoasFisicas.class);

		String stParametro = (String) pessoaFisica;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			codigo = Integer.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapPessoasFisicas.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(
					RapPessoasFisicas.Fields.NOME.toString(), stParametro,
					MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(RapPessoasFisicas.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public List<RapPessoasFisicas> pesquisarPessoaFisica(Integer codigo,
			String nome, Long cpf, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = montarCriteria(codigo, nome, cpf);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				true);

	}

	public Long pesquisarPessoaFisicaCount(Long cpf) {
		DetachedCriteria criteriaCpf = DetachedCriteria.forClass(RapPessoasFisicas.class);
		criteriaCpf.add(Restrictions.eq(RapPessoasFisicas.Fields.CPF.toString(), cpf));
		return executeCriteriaCount(criteriaCpf);
	}
	
	public Long pesquisarPessoaFisicaCount(Long cpf, Integer codigo) {
		DetachedCriteria criteriaCpf = DetachedCriteria.forClass(RapPessoasFisicas.class);
		criteriaCpf.add(Restrictions.and(
				Restrictions.eq(RapPessoasFisicas.Fields.CPF.toString(), cpf), 
				Restrictions.ne(RapPessoasFisicas.Fields.CODIGO.toString(),	codigo)));
		return executeCriteriaCount(criteriaCpf); 
	}
	
	public Long pesquisarPessoaFisicaCount(Integer codigoPaciente) {
		DetachedCriteria criteriaPac = DetachedCriteria.forClass(RapPessoasFisicas.class);
		criteriaPac.createAlias("aipPacientes", "aipPacientes");
		criteriaPac.add(Restrictions.eq(RapPessoasFisicas.Fields.PAC_CODIGO.toString(), codigoPaciente));
		return executeCriteriaCount(criteriaPac);
	}
	
	public Long pesquisarPessoaFisicaCount(Integer codigoPaciente, Integer codigo) {
		DetachedCriteria criteriaPac = DetachedCriteria
				.forClass(RapPessoasFisicas.class);
		criteriaPac.createAlias("aipPacientes", "aipPacientes");
		criteriaPac.add(Restrictions.and(Restrictions.eq(
				RapPessoasFisicas.Fields.PAC_CODIGO.toString(), codigoPaciente), 
				Restrictions.ne(RapPessoasFisicas.Fields.CODIGO.toString(), codigo)));
		return executeCriteriaCount(criteriaPac);
	}
	
	public Long pesquisarPessoaFisicaCount(Integer codigo, String nome,
			Long cpf) {

		DetachedCriteria criteria = montarCriteria(codigo, nome, cpf);

		return executeCriteriaCount(criteria);

	}
	
	public RapPessoasFisicas obterOld(RapPessoasFisicas pessoasFisica, boolean lock) {
		if (pessoasFisica == null || pessoasFisica.getCodigo() == null) {
			throw new IllegalArgumentException("Argumento obrigatorio");
		}
		this.desatachar(pessoasFisica);
		
		if (lock) {
			return (RapPessoasFisicas) this.getAndLock( pessoasFisica.getCodigo(), LockOptions.UPGRADE);
		}
		
		return this.obterPorChavePrimaria (pessoasFisica.getCodigo());
	}	
	
	private DetachedCriteria montarCriteria(Integer codigo, String nome, Long cpf) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapPessoasFisicas.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapPessoasFisicas.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(
					RapPessoasFisicas.Fields.NOME.toString(), nome,
					MatchMode.ANYWHERE));
		}

		if (cpf != null) {
			criteria.add(Restrictions.eq(
					RapPessoasFisicas.Fields.CPF.toString(), cpf));
		}

		return criteria;

	}	

	public List<RapPessoasFisicas> suggestionPessoasFisicasPorCPFNome(final Object objPesquisa){
		final DetachedCriteria criteria = obterCriteriaSuggestionPessoasFisicasPorCPFNome(objPesquisa);
		return executeCriteria(criteria, 0, 100, RapPessoasFisicas.Fields.NOME.toString(), true);
	}
	
	public Long suggestionPessoasFisicasPorCPFNomeCount(final Object objPesquisa){
		return executeCriteriaCount(obterCriteriaSuggestionPessoasFisicasPorCPFNome(objPesquisa));
	}

	private DetachedCriteria obterCriteriaSuggestionPessoasFisicasPorCPFNome(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class);
		
		if(objPesquisa != null && StringUtils.isNotBlank(objPesquisa.toString())){
			final String strPesquisa = objPesquisa.toString();
			if(CoreUtil.isNumeroLong(strPesquisa)){
				criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.CPF.toString(), Long.valueOf(strPesquisa)));
				
			} else {
				criteria.add(Restrictions.ilike(RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	/**
	 * Retorna as pessoas físicas de uma determinada nacionalidade
	 * @param nacionalidade
	 * @return
	 */
	public Long pesquisarPessoasFisicasPorNacionalidadeCount(AipNacionalidades nacionalidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class);
		criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.NACIONALIDADE.toString(), nacionalidade));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * @deprecated usar {@link #listarPessoasFisicasPorCodigoPaciente(Integer)}
	 * @param pacCodigo
	 * @return
	 */
	public List<RapPessoasFisicas> obterPessoaFisicaPorCodigoPaciente(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapPessoasFisicas.class);
		criteria.add(Restrictions.eq(
				RapPessoasFisicas.Fields.PAC_CODIGO.toString(), pacCodigo));
		return this
				.executeCriteria(criteria);
	}

	public List<RapPessoasFisicas> listarPessoasFisicasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapPessoasFisicas.class);
		
		criteria.add(Restrictions.eq(
				RapPessoasFisicas.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public RapPessoasFisicas obterRapPessoasFisicas(RapServidoresId id) {
		RapPessoasFisicas pf = null;
		List<RapPessoasFisicas> lstPF = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class);
		criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.SER_MATRICULA.toString(), id.getMatricula()));
		criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.SER_VIN_CODIGO.toString(), id.getVinCodigo()));
		lstPF = executeCriteria(criteria);

		if (lstPF != null && !lstPF.isEmpty()) {
			pf = lstPF.get(0);
		}

		return pf;
	}
	
	
	/**
	 * Retorna o código e o nome das pessoas físicas conforme a lista de conselhos profissionais
	 * @param  lista de conselho profissional
	 * @return lista de pessoas física ou lista vazia se não encontar
	 */
	public List<RapPessoasFisicas> listarRapPessoasFisicasPorConselhoProf(
			Object parametro, List<String> listaConselhoProfissional) {
		final DetachedCriteria criteria = criarCriteriaRapPessoaFisicaPorConselhoProf(parametro, listaConselhoProfissional);
		return executeCriteria(criteria, 0, 100, RapPessoasFisicas.Fields.NOME.toString(), true);
	}
	
	public Long listarRapPessoasFisicasPorConselhoProfCount(Object parametro, List<String> listaConselhoProfissional){
		return executeCriteriaCount(criarCriteriaRapPessoaFisicaPorConselhoProf(parametro, listaConselhoProfissional));
	}

	private DetachedCriteria criarCriteriaRapPessoaFisicaPorConselhoProf(
			Object parametro, List<String> listaConselhoProfissional) {
		String strParametro = (String) parametro;
		Integer codigo = null;
		
		String ponto	= ".";
		String aliasRPF	= "rpf";		// RAP_PESSOAS_FISICAS 			RPF
		String aliasQLF = "qlf";		// RAP_QUALIFICACOES 			QLF
		String aliasTQL = "tql";		// RAP_TIPOS_QUALIFICACAO 		TQL
		String aliasCPR	= "cpr";		// RAP_CONSELHOS_PROFISSIONAIS 	CPR
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class, aliasRPF);

		criteria.add(Subqueries.exists(
				DetachedCriteria.forClass(RapQualificacao.class, aliasQLF)
				
				.setProjection(Projections.property(aliasQLF + ponto + RapQualificacao.Fields.CODIGO.toString()))
				.createAlias(aliasQLF + ponto + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), aliasTQL)
				.createAlias(aliasTQL + ponto + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), aliasCPR)
				
				.add(Restrictions.eqProperty(aliasQLF + ponto + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), 
						aliasRPF + ponto + RapPessoasFisicas.Fields.CODIGO.toString()))
				.add(Restrictions.in(aliasCPR + ponto + RapConselhosProfissionais.Fields.SIGLA.toString(), listaConselhoProfissional))));
		
		if(strParametro != null && StringUtils.isNotBlank(strParametro)){
			
			if (CoreUtil.isNumeroInteger(strParametro)) {
				codigo = Integer.valueOf(strParametro);
			}
			
			if (codigo != null) {
				criteria.add(Restrictions.eq(aliasRPF + ponto +  RapPessoasFisicas.Fields.CODIGO.toString(), codigo));	
			}else{
				criteria.add(Restrictions.ilike(aliasRPF + ponto + RapPessoasFisicas.Fields.NOME.toString(), strParametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Retorna o chefe do servico de compras pelos parametros fixos 
	 * AghuParametrosEnum.P_MATR_CHEFE_CPRAS e AghuParametrosEnum.P_VIN_CHEFE_CPRAS
	 * 
	 * #24965 - C5
	 */
	public RapPessoasFisicas obterChefeServicoComprasPorAghParametros() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class, "psf");
		
		DetachedCriteria subAghParametro1 = DetachedCriteria.forClass(AghParametros.class, "pmt1");
		subAghParametro1.add(Restrictions.eq("pmt1." + AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_MATR_CHEFE_CPRAS.toString()));
		subAghParametro1.add(Restrictions.eqProperty("pmt1." + AghParametros.Fields.VLR_NUMERICO.toString(), "srv." + RapServidores.Fields.MATRICULA.toString()));
		subAghParametro1.setProjection(Projections.property("pmt1." + AghParametros.Fields.VLR_NUMERICO.toString()));
		
		
		DetachedCriteria servidor = DetachedCriteria.forClass(RapServidores.class, "srv");		
		servidor.add(Restrictions.eqProperty("psf." + RapPessoasFisicas.Fields.CODIGO, "srv." + RapServidores.Fields.PES_CODIGO.toString()));
		servidor.setProjection(Projections.property("srv." + RapServidores.Fields.PES_CODIGO.toString()));
		servidor.add(Subqueries.exists(subAghParametro1));
		
		
		DetachedCriteria subAghParametro2 = DetachedCriteria.forClass(AghParametros.class, "pmt2");
		subAghParametro2.add(Restrictions.eq("pmt2." + AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_VIN_CHEFE_CPRAS.toString()));
		subAghParametro2.add(Restrictions.eqProperty("pmt2." + AghParametros.Fields.VLR_NUMERICO.toString(), "srv." + RapServidores.Fields.VINCULO.toString()));
		subAghParametro2.setProjection(Projections.property("pmt2." + AghParametros.Fields.VLR_NUMERICO.toString()));
		servidor.add(Subqueries.exists(subAghParametro2));
		criteria.add(Subqueries.exists(servidor));
		
		return (RapPessoasFisicas)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Consulta pessoa fisica por matricula e vinculo do servidor
	 * #24965
	 * 
	 * @param id matricula e vinculo
	 * @return RapPessoasFisicas
	 */
	public RapPessoasFisicas obterRapPessoasFisicasPorServidor(RapServidoresId id) {
		RapPessoasFisicas pf = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class, "psf");
		
		DetachedCriteria servidor = DetachedCriteria.forClass(RapServidores.class, "srv");
		servidor.add(Restrictions.eqProperty("psf." + RapPessoasFisicas.Fields.CODIGO.toString(), "srv." + RapServidores.Fields.PES_CODIGO.toString()));
		servidor.add(Restrictions.eq("srv." + RapServidores.Fields.MATRICULA.toString(), id.getMatricula()));
		servidor.add(Restrictions.eq("srv." + RapServidores.Fields.VIN_CODIGO.toString(), id.getVinCodigo()));
		servidor.setProjection(Projections.property("srv." + RapServidores.Fields.PES_CODIGO.toString()));
		
		criteria.add(Subqueries.exists(servidor));
		
		pf = (RapPessoasFisicas)executeCriteriaUniqueResult(criteria);

		return pf;
	}
	
	

	/**
	 *  #36699 - Serviço para obter pessoa fisica
	 * @param vinculos
	 * @param matriculas
	 * @return
	 */
	public List<RapPessoasFisicas> obterRapPessoasFisicasPorMatriculaVinculo(List<Short> vinculos,List<Integer> matriculas) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class);
		if (vinculos !=null && vinculos.size()>0) {
			criteria.add(Restrictions.in(RapPessoasFisicas.Fields.SER_VIN_CODIGO.toString(), vinculos));
		}
		if (matriculas !=null && matriculas.size()>0) {
			criteria.add(Restrictions.in(RapPessoasFisicas.Fields.SER_MATRICULA.toString(), matriculas));
		}
		return executeCriteria(criteria);
	}
	
	public String buscarNomeResponsavelPorMatricula(Short codigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		criteria.setProjection(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		
		addMatriculaVinculo(codigo, matricula, criteria);
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	private void addMatriculaVinculo(Short vinCodigo, Integer matricula,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(
				RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(),
				matricula));
	}
	
	/**
	 * <p>#8674</p>
	 * 
	 * <p>Obtem lista de registros correspondentes aos filtros selecionados.</p>
	 * 
	 * @param profissional Filtro selecionado na tela de consulta.
	 * @param vinculo Filtro selecionado na tela de consulta.
	 * @param conselho Filtro selecionado na tela de consulta.
	 * @return Quantidade de registros encontrados.
	 * @author rafael.silvestre
	 */
	public List<ProfissionalHospitalVO> obterListaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho, 
		Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = montarCriteriaProfissionaisHospital(profissional, vinculo, conselho);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ProfissionalHospitalVO.Fields.PROFISSIONAL.toString())
				.add(Projections.property("VIN." + RapVinculos.Fields.DESCRICAO.toString()), ProfissionalHospitalVO.Fields.VINCULO.toString())
				.add(Projections.property("CPR." + RapConselhosProfissionais.Fields.SIGLA.toString()), ProfissionalHospitalVO.Fields.CONSELHO.toString())
				.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ProfissionalHospitalVO.Fields.MATRICULA.toString())
				.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), ProfissionalHospitalVO.Fields.VIN_CODIGO.toString())
				.add(Projections.property("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), ProfissionalHospitalVO.Fields.REGISTRO.toString()));
		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("VIN." + RapVinculos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ProfissionalHospitalVO.class));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * <p>#8674</p>
	 * 
	 * <p>Obtem quantidade de registros retornados para os filtros selecionados.</p>
	 * 
	 * @param profissional Filtro selecionado na tela de consulta.
	 * @param vinculo Filtro selecionado na tela de consulta.
	 * @param conselho Filtro selecionado na tela de consulta.
	 * @return Quantidade de registros encontrados.
	 * @author rafael.silvestre
	 */
	public Long obterCountProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho) {
		DetachedCriteria criteria = montarCriteriaProfissionaisHospital(profissional, vinculo, conselho);
		return executeCriteriaCount(criteria);
	}

	/**
	 * <p>#8674</p>
	 * 
	 * <p>Monta criteria com os alias e condicoes da consulta principal C3.</p>
	 * 
	 * @param profissional Selecionado na tela de consulta
	 * @param vinculo Selecionado na tela de consulta
	 * @param conselho Selecionado na tela de consulta
	 * @return criteria com parametros da consulta.
	 */
	private DetachedCriteria montarCriteriaProfissionaisHospital(RapServidores profissional, RapVinculos vinculo, RapConselhosProfissionais conselho) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class, "PES");
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.RAP_SERVIDORESES.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.VINCULO.toString(), "VIN", JoinType.INNER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.LEFT_OUTER_JOIN, 
				Restrictions.or(Restrictions.isNull("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), 
						Restrictions.isNotNull("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString())));
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQL", JoinType.INNER_JOIN, 
				Restrictions.and(Restrictions.eq("TQL." + RapTipoQualificacao.Fields.TIPO_QUALIFICACAO.toString(), DominioTipoQualificacao.CCC),
						Restrictions.in("TQL." + RapTipoQualificacao.Fields.CCC_NIVEL_CURSO.toString(), new Short[]{3, 7})));
		criteria.createAlias("QLF." + RapQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.isNull("SER." + RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt("SER." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		criteria.add(Subqueries.exists(subCriteriaProfissionalEspecialidade()));
		if (profissional != null && profissional.getPessoaFisica() != null && profissional.getPessoaFisica().getNome() != null) {
			criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), profissional.getPessoaFisica().getNome(), MatchMode.ANYWHERE));
		}
		if (vinculo != null && vinculo.getCodigo() != null) {
			criteria.add(Restrictions.eq("VIN." + RapVinculos.Fields.CODIGO.toString(), vinculo.getCodigo()));
		}
		if (conselho != null && conselho.getCodigo() != null) {
			criteria.add(Restrictions.eq("CPR." + RapConselhosProfissionais.Fields.CODIGO.toString(), conselho.getCodigo()));
		}
		return criteria;
	}
	
	/**
	 * <p>#8674</p>
	 * 
	 * <p>Monta subCriteria para restricao exists da consulta principal C3.</p>
	 */
	private DetachedCriteria subCriteriaProfissionalEspecialidade() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "PRE");
		criteria.setProjection(Projections.property("PRE." + AghProfEspecialidades.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("PRE." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), "SER." + RapServidores.Fields.VIN_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("PRE." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), "SER." + RapServidores.Fields.MATRICULA.toString()));
		return criteria;
	}
}
