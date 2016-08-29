package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.prescricaomedica.vo.VMpmServConselhoGeralVO;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @modulo registrocolaborador
 */

public class RapQualificacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapQualificacao> {
	
	private static final long serialVersionUID = -4737780949030562489L;

	private static final String QLF = "QLF";
	private static final String QLF_DOT = "QLF.";
	private static final String TQL = "TQL";
	private static final String TQL_DOT = "TQL.";
	private static final String PES = "PES";
	private static final String PES_DOT = "PES.";
	private static final String CPR = "CPR";
	private static final String CPR_DOT = "CPR.";
	private static final String SER = "SER";
	private static final String SER_DOT = "SER.";
	private static final String CCT = "CCT";
	private static final String CCT_DOT = "CCT.";
	private static final String CCA = "CCA";
	private static final String CCA_DOT = "CCA.";
	private static final String GRF = "GRF";
	private static final String GRF_DOT = "GRF.";
	
	public enum RapQualificacaoDAOExceptionCode implements BusinessExceptionCode {
		/**
		 * RAP-00284 - Já existe este número de registro informado para o mesmo
		 * código de conselho profissional.
		 */
		MENSAGEM_NRO_REGISTRO_CONSELHO_JA_EXISTE;
	}	
	
	public void evict(RapQualificacao qualificacao) {
		this.desatachar(qualificacao);
	}
	
	public List<RapQualificacao> pesquisarQualificacoes(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho,
			Integer firstResult, Integer maxResults) {

		DetachedCriteria criteria = montarConsultaQualificacoes(codigoPessoa,
				nomePessoa, numRegistro, siglaConselho);
		criteria.addOrder(Order.asc(RapQualificacao.Fields.PESSOA_NOME_ALIAS.toString()));
		return executeCriteria(criteria, firstResult, maxResults, null, false);
	}
	
	public List<RapQualificacao> pesquisarQualificacoes(RapPessoasFisicas pessoaFisica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class);
		criteria.createAlias(RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), pessoaFisica));
		return executeCriteria(criteria);
	}
	
	
	private DetachedCriteria montarConsultaQualificacoes(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho) {

		RapQualificacao example = new RapQualificacao();
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapQualificacao.class);
		criteria.createAlias(RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO_ALIAS.toString(), RapQualificacao.Fields.TIPO_QUALIFICACAO_ALIAS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO_ALIAS.toString() + "." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA.toString(), RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (codigoPessoa != null) {
			criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA_CODIGO_ALIAS.toString(), codigoPessoa));
		}
		if (StringUtils.isNotEmpty(nomePessoa)) {
			criteria.add(Restrictions.ilike(RapQualificacao.Fields.PESSOA_NOME_ALIAS.toString(), StringUtils
					.trimToNull(nomePessoa), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(numRegistro)) {
			criteria.add(Restrictions.ilike(
					RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(),
					StringUtils.trimToNull(numRegistro), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(siglaConselho)) {
			criteria.add(Restrictions.ilike(
					RapTipoQualificacao.Fields.SIGLA_CONSELHO_ALIAS.toString(), StringUtils.trimToNull(siglaConselho),
					MatchMode.ANYWHERE));
		}
		criteria.add(Example.create(example).enableLike(MatchMode.ANYWHERE).ignoreCase());
		// RN03 Só mostra qualificações (cursos) onde o tipo de qualificação
		// possui conselho profissional não nulo
		criteria.add(Restrictions.isNotNull(RapQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()));
		return criteria;
	}
	
	/**
	 * Verifica se o registro no conselho profissional já foi usado para outro
	 * servidor.
	 * 
	 * ORADB: Procedure RN_QLFP_VER_DUPLIC
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 *             se o registro no conselho ja foi usado
	 */
	public void verificaRegistroConselho(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		if (qualificacao.getNroRegConselho() == null) {
			return;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapQualificacao.class);

		criteria.createAlias(
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());

		criteria.add(Restrictions.eq(
				RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(),
				qualificacao.getNroRegConselho()));
		criteria.add(Restrictions.eq(
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				qualificacao.getTipoQualificacao()));
		criteria.add(Restrictions.eq(
				RapQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				qualificacao.getTipoQualificacao().getConselhoProfissional()));

		List<RapQualificacao> list = executeCriteria(criteria);
		for (RapQualificacao q : list) {
			// se não for o próprio que está sendo alterado
			if (!q.equals(qualificacao)) {
				throw new ApplicationBusinessException(
						RapQualificacaoDAOExceptionCode.MENSAGEM_NRO_REGISTRO_CONSELHO_JA_EXISTE);
			}
		}
	}	
	
	public Long pesquisarQualificacoesCount(Integer codigoPessoa,
			String nomePessoa, String numRegistro, String siglaConselho) {

		DetachedCriteria criteria = montarConsultaQualificacoes(codigoPessoa,
				nomePessoa, numRegistro, siglaConselho);
		return executeCriteriaCount(criteria);
	}	
	
	public Long pesquisarQualificacoesCount(RapInstituicaoQualificadora instituicaoQualificadora) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapQualificacao.class);
		criteria.add(Restrictions.eq(
				RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA.toString(),
				instituicaoQualificadora));

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna criterios de pesquisa montados com os parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 *            is da pessoa fisica
	 * @param sequencia
	 *            sequencia parte da chave composta
	 * @param dtInicio
	 * @param dtFim
	 * @param situacao
	 * @param semestre
	 * @param dtAtualizacao
	 * @param nroRegConselho
	 * @param serMatricula
	 *            matricula do servidor da qualificacao
	 * @param serVinCodigo
	 *            vinculo do servidor da qualificacao
	 * @param intermedioHcpa
	 * @param cargaHoraria
	 * @param indCarga
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private DetachedCriteria montarConsulta(Integer pesCodigo, Short sequencia,
			Date dtInicio, Date dtFim, DominioSituacaoQualificacao situacao,
			String semestre, Date dtAtualizacao, String nroRegConselho,
			RapServidores servidor, Boolean intermedioHcpa,
			Short cargaHoraria, Boolean indCarga)  {

		RapQualificacao example = new RapQualificacao();

		example.setDtInicio(dtInicio);
		example.setDtFim(dtFim);
		example.setSituacao(situacao);
		example.setSemestre(StringUtils.trimToNull(semestre));
		example.setDtAtualizacao(dtAtualizacao);
		example.setNroRegConselho(StringUtils.trimToNull(nroRegConselho));
		example.setIntermedioHcpa(intermedioHcpa);
		example.setCargaHoraria(cargaHoraria);
		example.setIndCarga(indCarga);

		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class);
		criteria.add(Example.create(example).enableLike(MatchMode.EXACT).ignoreCase());

		//
		if (pesCodigo != null) {
			criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA_CODIGO.toString(), pesCodigo));
		}
		if (sequencia != null) {
			criteria.add(Restrictions.eq(RapQualificacao.Fields.SEQUENCIA.toString(), sequencia));
		}

		// busca servidor para adicionar criterio por pessoa fisica.
		if (servidor != null) {			
			criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA.toString(),servidor.getPessoaFisica()));
		}

		return criteria;
	}

	/**
	 * Adiciona restrições para pesquisa apenas de cursos do nível de graduação(terceiro grau).
	 */
	private void addRestrictionsGraduacoes(DetachedCriteria criteria) {
		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());

		// tipos CCC e CSC
		DominioTipoQualificacao[] tipos = {DominioTipoQualificacao.CCC, DominioTipoQualificacao.CSC };
		criteria.add(Restrictions.in(RapQualificacao.Fields.TIPO_QUALIFICACAO_TIPO.toString(), tipos));

		// cccNivelCurso ou cscNivelCurso == 3
		Disjunction orNivelCurso = Restrictions.disjunction();
		orNivelCurso.add(Restrictions.eq(RapQualificacao.Fields.CCC_NIVEL_CURSO.toString(), (short) 3));
		orNivelCurso.add(Restrictions.eq(RapQualificacao.Fields.CSC_NIVEL_CURSO.toString(), (short) 3));
		criteria.add(orNivelCurso);

		// somente aluno(discente)
		criteria.createAlias(RapQualificacao.Fields.TIPO_ATUACAO.toString(), RapQualificacao.Fields.TIPO_ATUACAO.toString());
		criteria.add(Restrictions.eq(RapQualificacao.Fields.TIPO_ATUACAO_DISCENTE.toString(), Boolean.TRUE));
	}
	
	/**
	 * Retorna a quantidade de registros de graduação encontrados com os parâmetros fornecidos.
	 */
	public Long pesquisarGraduacaoCount(Integer pesCodigo, RapServidores servidor) {

		final DetachedCriteria criteria = montarConsulta(pesCodigo, null, null, null, null, null, null, null, servidor, null, null, null);

		addRestrictionsGraduacoes(criteria);

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna as graduações com os parâmetros fornecidos.
	 */
	public List<RapQualificacao> pesquisarGraduacao(Integer pesCodigo, RapServidores servidor, Integer firstResult,
													Integer maxResult, String orderProperty, boolean asc) {

		final DetachedCriteria criteria = montarConsulta(pesCodigo, null, null, null, null, null, null, null, servidor, null, null, null);
		
		addRestrictionsGraduacoes(criteria);
		
		criteria.createAlias(RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), "PF", JoinType.INNER_JOIN);
		criteria.createAlias(RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA.toString(), "IQ", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString() +"."+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Atribui o próximo valor de sequência ao objeto a ser inserido.
	 * 
	 * @param qualificacao
	 */
	public void atribuiSequencia(RapQualificacao qualificacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapQualificacao.class);

		criteria.setProjection(Projections.max(RapQualificacao.Fields.SEQUENCIA
				.toString()));
		criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA.toString(),
				qualificacao.getId().getPessoaFisica()));
		Short sequencia = (Short) executeCriteriaUniqueResult(criteria);
		sequencia = (sequencia == null) ? (short) 0 : sequencia;

		qualificacao.getId().setSequencia(++sequencia);
	}
	
	public List<RapQualificacao> pesquisarRapQualificacaoPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class);
		
		if (servidor != null) {			
			RapServidores servidorAtachado = super.find(RapServidores.class, servidor.getId());
			criteria.createAlias(RapQualificacao.Fields.PESSOA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA.toString(), servidorAtachado.getPessoaFisica()));
		}
		
		return executeCriteria(criteria);
		
	}
	
	public RapQualificacao obterRapQualificacaoPorServidor(RapServidores servidor) {
		
		List<RapQualificacao> listaRapQualificacaoPorServidor =  this.pesquisarRapQualificacaoPorServidor(servidor);
		
		if(listaRapQualificacaoPorServidor !=null && !listaRapQualificacaoPorServidor.isEmpty()){
			return listaRapQualificacaoPorServidor.get(0); // Retorna o primeiro da lista
		}
		
		return null;
		
	}

	public List<RapQualificacao> pesquisarQualificacaoConselhoProfissionalPorServidor(Integer serMatricula, Short serVinCodigo) {
		DetachedCriteria criteria = obterQualificacaoConselhoProfissionalPorServidor(
				serMatricula, serVinCodigo);
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterQualificacaoConselhoProfissionalPorServidor(
			Integer serMatricula, Short serVinCodigo) {
		String aliasQlf = "qlf";
		String aliasPes = "pes";
		String aliasSer = "ser";
		String aliasTql = "tql";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class, aliasQlf);
		criteria.createAlias(aliasQlf + ponto + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), aliasPes, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasPes + ponto + RapPessoasFisicas.Fields.RAP_SERVIDORESES.toString(), aliasSer);
		criteria.createAlias(aliasQlf + ponto + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), aliasTql, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasTql + ponto + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "cp");
		
		criteria.add(Restrictions.eq(aliasSer + ponto + RapServidores.Fields.MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(aliasSer + ponto + RapServidores.Fields.VIN_CODIGO.toString(), serVinCodigo));
		return criteria;
	}
	
	private Query createProfissionalEspecialidadeHql(AghEspecialidades especialidade, String strPesquisa) {
		StringBuffer hql = new StringBuffer(640);

		StringBuffer conselhosMedicinaOdontologia = new StringBuffer(ConselhoRegionalMedicinaEnum.getValores());
//		conselhosMedicinaOdontologia.append(", ").append(ConselhoRegionalOdontologiaEnum.getValores());

		hql.append(" select distinct qlf.nroRegConselho, ");
		hql.append(" 		ser.pessoaFisica.nome ");
		hql.append(" from RapServidores ser, ");
		hql.append(" 	  AghProfEspecialidades pre, ");
		hql.append(" 	  RapQualificacao qlf ");
		hql.append(" where (ser.dtFimVinculo = null or ser.dtFimVinculo > :dataAtual)");
		hql.append(" 	and ser.id.matricula = pre.id.serMatricula ");
		hql.append(" 	and ser.id.vinCodigo = pre.id.serVinCodigo ");
		hql.append(" 	and ser.pessoaFisica.codigo = qlf.id.pessoaFisica.codigo");
		hql.append(" 	and qlf.nroRegConselho != null");
		hql.append(" 	and qlf.tipoQualificacao.conselhoProfissional.sigla IN (").append(conselhosMedicinaOdontologia).append(')');
		hql.append(" 	and pre.indInterna = :indInterna");
		hql.append(" 	and pre.id.espSeq = :seqEspecialidade");
		if (strPesquisa != null && strPesquisa.length() > 0) {
			hql.append(" 	and (ser.pessoaFisica.nome like :strPesquisa or qlf.nroRegConselho like :strPesquisa)");
		}
		hql.append(" order by ser.pessoaFisica.nome ");

		Query query = createHibernateQuery(hql.toString());

		query.setParameter("seqEspecialidade", especialidade.getSeq());
		query.setParameter("indInterna", DominioSimNao.S);
		if (strPesquisa != null && strPesquisa.length() > 0) {
			query.setParameter("strPesquisa", "%" + strPesquisa.toUpperCase() + "%");
		}
		Date dataAtual = new Date();
		query.setParameter("dataAtual", dataAtual);

		return query;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisaProfissionalEspecialidade(AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {
		Query query = createProfissionalEspecialidadeHql(especialidade, strPesquisa);
		return query.list();
	}
	
	/**
	 * Retorna o número de registro no conselho profissional.
	 * 
	 * @param pessoa
	 *            id da pessoa física
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscaNumeroRegistro(Integer pessoa, Integer[] tiposQualificacao) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class);
		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQF", Criteria.INNER_JOIN);
		criteria.add(Restrictions.in("TQF." + RapTipoQualificacao.Fields.CODIGO.toString(), tiposQualificacao));
		criteria.add(Restrictions.isNotNull(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		if (pessoa != null) {
			criteria.add(Restrictions.eq(RapQualificacao.Fields.PESSOA_CODIGO.toString(), pessoa));
		}
		criteria.setProjection(Projections.property(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));

		List<String> lista = executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<RapConselhosProfissionais> listarRapConselhosProfissionaisPorPessoaFisica(RapPessoasFisicas pessoaFisica) {
		StringBuffer hql = new StringBuffer(260);
		hql.append(" select qlf.tipoQualificacao.conselhoProfissional ");
		hql.append(" from RapQualificacao qlf");
		hql.append(" where qlf.id.pessoaFisica = :pessoa");
		hql.append(" 	and qlf.nroRegConselho != null");
		hql.append(" 	and qlf.tipoQualificacao.conselhoProfissional != null");
		hql.append(" order by qlf.tipoQualificacao.conselhoProfissional.sigla ");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("pessoa", pessoaFisica);

		return query.list();
	}
	
	public List<RapServidoresVO> listarRapConselhosProfissionaisPorServidor(List<RapServidoresId> servidores) {
		/*  select sv.matricula, sv.vin_codigo, pf.codigo, pf.sexo
			, con.titulo_masculino, con.titulo_feminino
			, vin.titulo_masculino, vin.titulo_feminino
			, pf.nome 
			from rap_pessoas_fisicas pf
			inner join rap_servidores sv on pf.codigo = sv.pes_codigo
			inner join RAP_VINCULOS vin on sv.vin_codigo = vin.CODIGO
			right join rap_qualificacoes qlf on pf.codigo = qlf.pes_codigo
			right join rap_tipos_qualificacao tql on qlf.tql_codigo = tql.codigo
			inner join RAP_CONSELHOS_PROFISSIONAIS con on tql.cpr_codigo = con.codigo
			where qlf.NRO_REG_CONSELHO is not null
			and sv.matricula in (301,17084,3836)
			and sv.vin_codigo in (952,1,955)*/
		
		List<Integer> matriculas = new ArrayList<Integer>();
		List<Integer> vinculos = new ArrayList<Integer>();
		for (RapServidoresId rapServidoresId : servidores) {
			matriculas.add(rapServidoresId.getMatricula());
			vinculos.add(rapServidoresId.getVinCodigo().intValue());
		}
		
		String filter = "servidor."+RapServidores.Fields.CODIGO_VINCULO.toString();
		String filtroINVinculo = CoreUtil.criarClausulaIN(filter, "", vinculos);
		filtroINVinculo = filtroINVinculo.replace("where", "");
		
		filter = "servidor."+RapServidores.Fields.MATRICULA.toString();
		String filtroINMatricula = CoreUtil.criarClausulaIN(filter, "", matriculas);
		//contorno pra problema gerado pelo CoreUtil.criarClausulaIN
		filtroINMatricula = filtroINMatricula.replace("where", "");
		
		StringBuilder hql = new StringBuilder(400);
		hql.append(" select servidor.").append(RapServidores.Fields.MATRICULA.toString()).append(", ")
		.append("servidor.").append(RapServidores.Fields.CODIGO_VINCULO.toString()).append(", ") 
		.append("pesFisica.").append(RapPessoasFisicas.Fields.CODIGO.toString()).append(", ")
		.append("pesFisica.").append(RapPessoasFisicas.Fields.NOME.toString()).append(", ")
		.append("pesFisica.").append(RapPessoasFisicas.Fields.SEXO.toString()).append(", ")
		.append("conselho.").append(RapConselhosProfissionais.Fields.TITULO_MASCULINO.toString()).append(", ")
		.append("conselho.").append(RapConselhosProfissionais.Fields.TITULO_FEMININO.toString()).append(',')
		.append("vinculo.").append(RapVinculos.Fields.TITULO_MASCULINO.toString()).append(", ")
		.append("vinculo.").append(RapVinculos.Fields.TITULO_FEMININO.toString());
		hql.append(" from ").append(RapServidores.class.getSimpleName()).append(" servidor ");
		hql.append(" inner join servidor.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" pesFisica ");
		hql.append(" inner join pesFisica.").append(RapPessoasFisicas.Fields.QUALIFICACOES.toString()).append(" qualificacao ");
		hql.append(" inner join qualificacao.").append(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()).append(" tipoQualif ");
		hql.append(" inner join tipoQualif.").append(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()).append(" conselho ");
		hql.append(" inner join servidor.").append(RapServidores.Fields.VINCULO.toString()).append(" vinculo ");
		hql.append(" where qualificacao.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()).append(" is not null ");
		hql.append(" and ").append(filtroINVinculo);
		hql.append(" and ").append(filtroINMatricula);
		
		
		javax.persistence.Query query = this.createQuery(hql.toString());
		
		List<RapServidoresVO> servidoresVO = new ArrayList<RapServidoresVO>();
		List<Object[]> list = query.getResultList();
		for (Object[] object : list) {
			RapServidoresVO vo = new RapServidoresVO();
			vo.setMatricula((Integer)object[0]);
			vo.setVinculo((Short)object[1]);
			vo.setCodigoPessoa((Integer)object[2]);
			vo.setNome((String)object[3]);
			vo.setSexo((DominioSexo)object[4]);
			if(vo.getSexo() == DominioSexo.F){
				vo.setTituloFemininoConselho((String)object[6]);
				vo.setTituloFemininoVinculo((String)object[8]);
			}else{
				vo.setTituloMasculinoConselho((String)object[5]);
				vo.setTituloMasculinoVinculo((String)object[7]);
			}
			
			servidoresVO.add(vo);
		}
		
		return servidoresVO;
	}
	
	public List<RapQualificacao> pesquisarConselhoProfissionalPorServidor(Integer serMatricula, Short serVinCodigo) {
		String aliasQlf = "qlf";
		String ponto = ".";
		
		DetachedCriteria criteria = obterQualificacaoConselhoProfissionalPorServidor(
				serMatricula, serVinCodigo);
		criteria.add(Restrictions.isNotNull(aliasQlf + ponto + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Obtém os dados da view V_MPM_SERV_CONSELHO_GERAL, usando como filtro o ID do Servidor
	 * 
	 * @param matricula - Matrícula do Servidor
	 * @param vinCodigo - Código de vínculo do Servidor
	 * 
	 * @return Dados do medico solicitante
	 */
	public VMpmServConselhoGeralVO obterServidorConselhoGeralPorIdServidor(Integer matricula, Short vinCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapQualificacao.class, QLF);

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property(SER_DOT + RapServidores.Fields.MATRICULA.toString()), VMpmServConselhoGeralVO.Fields.MATRICULA.toString());
		projections.add(Projections.property(SER_DOT + RapServidores.Fields.VIN_CODIGO.toString()), VMpmServConselhoGeralVO.Fields.VIN_CODIGO.toString());
		projections.add(Projections.property(CCT_DOT + FccCentroCustos.Fields.CODIGO.toString()), VMpmServConselhoGeralVO.Fields.CCT_CODIGO.toString());
		projections.add(Projections.property(CCA_DOT + FccCentroCustos.Fields.CODIGO.toString()), VMpmServConselhoGeralVO.Fields.CCT_CODIGO_ATUA.toString());
		projections.add(Projections.property(SER_DOT + RapServidores.Fields.OCA_CODIGO.toString()), VMpmServConselhoGeralVO.Fields.OCA_CODIGO.toString());
		projections.add(Projections.property(GRF_DOT + RapGrupoFuncional.Fields.CODIGO.toString()), VMpmServConselhoGeralVO.Fields.GRF_CODIGO.toString());
		projections.add(Projections.property(PES_DOT + RapPessoasFisicas.Fields.NOME.toString()), VMpmServConselhoGeralVO.Fields.NOME.toString());
		projections.add(Projections.property(CPR_DOT + RapConselhosProfissionais.Fields.SIGLA.toString()), VMpmServConselhoGeralVO.Fields.SIGLA.toString());
		projections.add(Projections.property(CPR_DOT + RapConselhosProfissionais.Fields.CODIGO.toString()),
				VMpmServConselhoGeralVO.Fields.CPR_CODIGO.toString());
		projections.add(Projections.property(QLF_DOT + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()),
				VMpmServConselhoGeralVO.Fields.NRO_REG_CONSELHO.toString());
		projections.add(Projections.property(SER_DOT + RapServidores.Fields.IND_SITUACAO.toString()), VMpmServConselhoGeralVO.Fields.IND_SITUACAO.toString());
		projections.add(Projections.property(SER_DOT + RapServidores.Fields.DATA_INICIO_VINCULO.toString()),
				VMpmServConselhoGeralVO.Fields.DT_INICIO_VINCULO.toString());
		projections.add(Projections.property(SER_DOT + RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				VMpmServConselhoGeralVO.Fields.DT_FIM_VINCULO.toString());

		criteria.setProjection(projections);

		criteria.createAlias(QLF_DOT + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), TQL, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(QLF_DOT + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), PES, JoinType.RIGHT_OUTER_JOIN);
		criteria.createAlias(TQL_DOT + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), CPR, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PES_DOT + RapPessoasFisicas.Fields.RAP_SERVIDORESES.toString(), SER);
		criteria.createAlias(SER_DOT + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), CCT, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SER_DOT + RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), CCA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SER_DOT + RapServidores.Fields.GRUPO_FUNCIONAL.toString(), GRF, JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(TQL_DOT + RapTipoQualificacao.Fields.TIPO_QUALIFICACAO.toString(), DominioTipoQualificacao.CCC));
		criteria.add(Restrictions.in(TQL_DOT + RapTipoQualificacao.Fields.CCC_NIVEL_CURSO.toString(), new Object[]{(short) 3, (short)7}));
		
		criteria.add(Restrictions.eq(SER_DOT + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(SER_DOT + RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));

		criteria.setResultTransformer(Transformers.aliasToBean(VMpmServConselhoGeralVO.class));
		
		List<VMpmServConselhoGeralVO> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}

}