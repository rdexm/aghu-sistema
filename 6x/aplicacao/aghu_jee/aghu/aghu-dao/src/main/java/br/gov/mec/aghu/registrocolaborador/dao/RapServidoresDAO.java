package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.controlepaciente.vo.DescricaoControlePacienteVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.exames.vo.PesquisaServidorSolicitacaoExameVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresEspecialidadesVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AfaMensCalculoNpt;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapAfastamento;
import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RapServidoresProcedimentoTerapeuticoVO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorCurPreVO;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * @see RapServidores
 * 
 * @modulo registrocolaborador
 * @author cvagheti
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount" })
public class RapServidoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapServidores> {
   
	private static final Log LOG = LogFactory.getLog(RapServidoresDAO.class);	
   
    @EJB
    private ICascaFacade cascaFacade;

	private static final long serialVersionUID = -176879896449951415L;
	
	public enum RapServidoresDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CENTRO_CUSTO_LOTACAO_NAO_INFORMADO,
		MENSAGEM_FIM_VINCULO_DENTRO_PERIODO_AFASTAMENTO,
		MENSAGEM_MATRICULA_JA_EXISTE,
		MENSAGEM_PESSOA_COM_VINCULO_DUPLICADO,
		ERRO_SERVIDOR_LOGIN_NAO_INFORMADO,
		ERRO_USUARIO_DUPLICADO_PARA_SERVIDOR, 
		ERRO_USUARIO_INVALIDO;
	}	

	private static final Object COMPONENTE_SOLICITAR_EXAMES = "elaborarSolicitacaoExame";
	private static final Object METODO_SOLICITAR_EXAMES = "elaborar";
	private final String stringSeparator = ".";
	private static final String ALIAS_PES 	= "PES";
	private static final String ALIAS_SER 	= "RS";
	private static final String ALIAS_PTI	= "RPTI";
	private static final String ALIAS_CBO	= "CBO";
	private static final String ALIAS_ROP   = "ROP";
	private static final String PONTO 		= ".";

	/**
	 * Retorna um servidor ativo conforme o seu login de usuário do AGHU.
	 * Caso deseje usar as otimizações de cache, vide método:
	 * 
	 * @see obterServidorAtivoPorUsuario(final String login, final Date
	 *      dtFimVinculo)
	 * @param login
	 *            Usuário do servidor no sistema AGHU
	 * @return Servidor ativo até com data de fim do vínculo até agora
	 * @throws ApplicationBusinessException
	 */
	public RapServidores obterServidorAtivoPorUsuario(final String loginUsuario) throws ApplicationBusinessException {
		return obterServidorAtivoPorUsuario(loginUsuario, new Date());
	}
	
	/**
	 * Retorna um servidor ativo conforme o seu login de usuário do AGHU e data
	 * com data de fim de vínculo menor ou igual a informada por parâmetro
	 * OBSERVAÇÃO: Utilize este método, informando sempre a MESMA data de fim de
	 * vínculo por parâmetro, caso deseje fazer uso das otimizações da Cache
	 * ATENCAO: SE VOCE PRECISAR DOS CENTROS DE CUSTOS ATUACAO/LOTACAO/PRODUCAO,
	 *          NAO UTILIZE ESTE METODO POIS O CACHE NAO GUARDA AS LISTAS FILHAS.
	 *          COMO NA MAIORIA DO SISTEMA ISTO NAO EH NECESSARIO, FOI CRIADO OUTRO
	 *          METODO, QUE NAO UTILIZA O CACHE E TRAZ AS LISTAS FILHAS. (obterServidorAtivoPorUsuarioSemCache)
	 * Logica de negocio duplicado no AghuBaseLoginModule.
	 * 
	 * @param login
	 *            Usuário do servidor no sistema AGHU
	 * @param dtFimVinculo
	 *            Data em que expira o vínculo do servidor com a instituição
	 * @return Servidor ativo até a data de fim do vínculo passada para esta
	 *         pesquisa
	 * @throws ApplicationBusinessException
	 */	
	public RapServidores obterServidorAtivoPorUsuario(final String loginUsuario, final Date dataFimVinculo) throws ApplicationBusinessException {
		if (loginUsuario == null) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_SERVIDOR_LOGIN_NAO_INFORMADO);
		}
		
		// trunca a partir de minutos para aproveitar o cache
		Date dataFimVinculoTrunc = DateUtils.truncate(dataFimVinculo, Calendar.HOUR_OF_DAY);
				
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.createAlias(RapServidores.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "ATU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATU."+FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "PROD1", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "LOT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LOT."+FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "PROD2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P), 
							Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), dataFimVinculoTrunc))));
		
		criteria.add(Restrictions.eq(RapServidores.Fields.USUARIO.toString(), loginUsuario).ignoreCase());	
		
		RapServidores servidor = null;
		
		try {
			servidor = (RapServidores) executeCriteriaUniqueResult(criteria);
		} catch (NonUniqueResultException e) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_USUARIO_DUPLICADO_PARA_SERVIDOR, loginUsuario);
		}
		
		if (servidor == null) {
			LOG.warn("Nenhum Servidor do HU encontrado associado ao usuário "+loginUsuario);		
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_USUARIO_INVALIDO, loginUsuario);
		}
		
		return servidor;
	}
	
	public RapServidores obterServidorAtivoPorUsuarioSemCache(final String loginUsuario, final Date dataFimVinculo) throws ApplicationBusinessException {
		if (loginUsuario == null) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_SERVIDOR_LOGIN_NAO_INFORMADO);
		}
		
		// trunca a partir de minutos para aproveitar o cache
		Date dataFimVinculoTrunc = DateUtils.truncate(dataFimVinculo, Calendar.HOUR_OF_DAY);
				
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "ATU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATU."+FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "PROD1", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "LOT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LOT."+FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "PROD2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P), 
							Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), dataFimVinculoTrunc))));
		
		criteria.add(Restrictions.eq(RapServidores.Fields.USUARIO.toString(), loginUsuario).ignoreCase());	
		
		RapServidores servidor = null;
		
		try {
			servidor = (RapServidores) executeCriteriaUniqueResult(criteria);
		} catch (NonUniqueResultException e) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_USUARIO_DUPLICADO_PARA_SERVIDOR, loginUsuario);
		}
		
		if (servidor == null) {
			LOG.warn("Nenhum Servidor do HU encontrado associado ao usuário "+loginUsuario);		
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_USUARIO_INVALIDO, loginUsuario);
		}
		
		return servidor;
	}
	
	/**
	 * Retorna um servidor no sistema conforme o seu login de usuário
	 * 
	 * @param loginUsuario Usuário do Servidor no sistema AGHU
	 * @return Servidor ativo ou inativo
	 */
	public RapServidores obterServidorPorUsuario(final String loginUsuario) throws ApplicationBusinessException {
		if (loginUsuario == null) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.ERRO_SERVIDOR_LOGIN_NAO_INFORMADO);
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.ilike(RapServidores.Fields.USUARIO.toString(), loginUsuario, MatchMode.EXACT));
		
		RapServidores servidor = (RapServidores) executeCriteriaUniqueResult(criteria);
		
		if (servidor == null) {
			LOG.warn("Nenhum Servidor do HU encontrado associado ao usuário "+loginUsuario);
		}
		
		return servidor;
	}
	
	public RapServidores obter(RapServidoresId id) {
		if (id == null) {
			throw new IllegalArgumentException("Argumento obrigatorio");
		}
		return this.obterPorChavePrimaria(id, true, RapServidores.Fields.PESSOA_FISICA);
	}
	
	public RapServidores obterOld(RapServidores servidor) {
		return obterOld(servidor, false);
	}
	
	public RapServidores obterOld(RapServidores servidor, boolean lock) {
		if (servidor == null || servidor.getId() == null) {
			throw new IllegalArgumentException("Argumento obrigatorio");
		}
		this.desatachar(servidor);
		
		if (lock) {
			return (RapServidores) this.getAndLock( servidor.getId(), LockOptions.UPGRADE);
		}
		
		return this.obterPorChavePrimaria (servidor.getId());
	}	

	private DetachedCriteria obterCriteriaPesquisarServidor(final Short tipoCBO) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, ALIAS_SER);
		
		criteria.createAlias(ALIAS_SER + PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.TIPO_INFORMACAO.toString(), ALIAS_PTI);
		criteria.createAlias(ALIAS_SER + PONTO + RapServidores.Fields.OCUPACAO_CARGO.toString(), ALIAS_ROP);
		
		criteria.add(Restrictions.isNotNull(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString()));
		criteria.add(Restrictions.eq(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), tipoCBO));
		criteria.add(Restrictions.or(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(
						Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(ALIAS_SER + PONTO + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())
						)
				));
		
		return criteria;

	}	
		
	private ProjectionList obterProjectionsBasicaPesquisarServidor() {
		return Projections.projectionList()
			.add(Projections.property(ALIAS_SER + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString()), RapServidoresVO.Fields.VINCULO.toString())
			.add(Projections.property(ALIAS_SER + PONTO + RapServidores.Fields.MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
			.add(Projections.property(ALIAS_SER + PONTO + RapServidores.Fields.IND_SITUACAO.toString()), RapServidoresVO.Fields.IND_SITUACAO.toString())
			.add(Projections.property(ALIAS_SER + PONTO + RapServidores.Fields.DATA_FIM_VINCULO.toString()), RapServidoresVO.Fields.DATA_FIM_VINCULO.toString())
			.add(Projections.property(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.NOME.toString()), RapServidoresVO.Fields.NOME.toString())
			.add(Projections.property(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.CODIGO.toString()), RapServidoresVO.Fields.CODIGO_PESSOA.toString())
			.add(Projections.property(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString()),RapServidoresVO.Fields.CBO.toString());
	}
	private ProjectionList obterProjectionsPesquisarServidor() {
		return obterProjectionsBasicaPesquisarServidor()
			.add(Projections.property(ALIAS_ROP + PONTO + RapOcupacaoCargo.Fields.DESCRICAO.toString()),RapServidoresVO.Fields.OCUPACAO.toString());
	}	
		
		
	private String obterSqlDescricaoCBO() {
		final String aliasPti = "rpti2_";
		StringBuilder sql =  new StringBuilder(512);

		sql.append("CASE ")
			.append("	WHEN (SELECT COUNT( " + ALIAS_CBO + PONTO + FatCbos.Fields.CODIGO.toString() + " ) " )
			.append("			FROM AGH.FAT_CBOS " + ALIAS_CBO)
			.append("  		   WHERE  1 = 1 ")
			.append("			 AND " + ALIAS_CBO + PONTO + FatCbos.Fields.CODIGO.toString() + " = " + aliasPti + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString())
			.append( " ) > 1 THEN  ")
			.append(" 		( SELECT " + ALIAS_CBO + PONTO + FatCbos.Fields.DESCRICAO.toString() )
			.append("			FROM AGH.FAT_CBOS " + ALIAS_CBO)
			.append("  		   WHERE  1 = 1 ")
			.append("			 AND " + ALIAS_CBO + PONTO + FatCbos.Fields.CODIGO.toString() + " = " + aliasPti + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString())
			.append("            AND " + ALIAS_CBO + PONTO + FatCbos.Fields.DT_FIM.name()  + " IS NULL ")
			.append("		 ) ")
			.append("	ELSE ")
			.append(" 		( SELECT " + ALIAS_CBO + PONTO + FatCbos.Fields.DESCRICAO.toString())
			.append("			FROM AGH.FAT_CBOS " + ALIAS_CBO)
			.append("  		   WHERE  1 = 1 ")
			.append("			 AND " + ALIAS_CBO + PONTO + FatCbos.Fields.CODIGO.toString() + " = " + aliasPti + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString())
			.append("		 ) ")
			.append("    END ");

		return sql.toString();
	}	
	
	private ProjectionList obterProjectionsPesquisarServidorSuggestionBox() {
		return obterProjectionsBasicaPesquisarServidor()
			.add(Projections.sqlProjection("("+
					obterSqlDescricaoCBO() + ") as " + FatCbos.Fields.DESCRICAO.name(), 
					new String[] { FatCbos.Fields.DESCRICAO.name()}, 
					new Type[] { StringType.INSTANCE }), RapServidoresVO.Fields.OCUPACAO.toString());
	}
	
	private Criterion obterRestrictionsFiltrosPesquisarServidorOcupacao(final String stParametro) {
		StringBuilder sql =  new StringBuilder(100);
		Object[] values = { stParametro };
		Type[] types = { StringType.INSTANCE};
		sql.append(" lower ( " + obterSqlDescricaoCBO() + " ) like lower ('%' || ? || '%') "); 
		
		return Restrictions.sqlRestriction(sql.toString(), values, types);
	}
	
	
	private void obterFiltroCriteriaPesquisarServidor(final Object servidor, final DetachedCriteria criteria) {

		String stParametro = null;
		
		if(servidor != null){
			stParametro = servidor.toString();
		}

		if (StringUtils.isNotBlank(stParametro)){
			if(CoreUtil.isNumeroInteger(stParametro) && CoreUtil.isNumeroShort(stParametro)) {
				criteria.add(
						Restrictions.or(
								Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(stParametro)),
								Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(stParametro)))
								);
								
				return;
				
			} else if(CoreUtil.isNumeroInteger(stParametro) && !CoreUtil.isNumeroShort(stParametro) ) {
				criteria.add(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(stParametro)));
				return;
			} else if(CoreUtil.isNumeroShort(stParametro)) {
				criteria.add(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(stParametro)));
				return;
			} else {
				criteria.add(
						Restrictions.or(obterRestrictionsFiltrosPesquisarServidorOcupacao(stParametro),
								Restrictions.or(
										Restrictions.ilike(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.NOME.toString(), stParametro, MatchMode.ANYWHERE),
										Restrictions.ilike(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString(), stParametro, MatchMode.ANYWHERE))
										)
						);
			}
		}
	}
	
	public List<RapServidoresVO> pesquisarServidor(final Object servidor, final Short tipoCBO) throws BaseException {
		
		final DetachedCriteria criteria = obterCriteriaPesquisarServidor(tipoCBO);
		
		obterFiltroCriteriaPesquisarServidor(servidor, criteria);
		
		criteria.addOrder(Order.asc(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.NOME.toString()));
		
		criteria.setProjection(obterProjectionsPesquisarServidorSuggestionBox());
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));

		List<RapServidoresVO> result =  executeCriteria(criteria, 0, 100, null, false);
		
		return result;
	}

	public Long pesquisarServidorCount(final Object servidor, final Short tipoCBO) throws BaseException {
		final DetachedCriteria criteria = obterCriteriaPesquisarServidor(tipoCBO);
		
		obterFiltroCriteriaPesquisarServidor(servidor, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<RapServidoresVO> pesquisarServidorPorCBO(final Object servidor, final Short tipoCBO, final List<String> cbos) {
		
		final DetachedCriteria criteria = obterCriteriaPesquisarServidor(tipoCBO);
		
		obterFiltroCriteriaPesquisarServidor(servidor, criteria);
		
		if (!cbos.isEmpty() && cbos.size() > 0) {
			criteria.add(Restrictions.in(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString(), cbos));
		}
		criteria.addOrder(Order.asc(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.NOME.toString()));
		
		criteria.setProjection(obterProjectionsPesquisarServidorSuggestionBox());
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		List<RapServidoresVO> result =  executeCriteria(criteria, 0, 100, null, false);
		return result;
	}
	
	public Long pesquisarServidorPorCBOCount(final Object servidor, final Short tipoCBO, final List<String> cbos) {
		
		final DetachedCriteria criteria = obterCriteriaPesquisarServidor(tipoCBO);
		
		obterFiltroCriteriaPesquisarServidor(servidor, criteria);
		
		if (!cbos.isEmpty() && cbos.size() > 0) {
			criteria.add(Restrictions.in(ALIAS_PTI + PONTO + RapPessoaTipoInformacoes.Fields.VALOR.toString(), cbos));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public RapServidoresVO obterServidorVO(final Integer pesCodigo, final Integer matricula, final Short vinCodigo, final Short tipoCBO) throws BaseException {
		final DetachedCriteria criteria = obterCriteriaPesquisarServidor(tipoCBO);
		
		if(vinCodigo != null){
			criteria.add(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		}

		if (pesCodigo != null){
			criteria.add(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.CODIGO_PESSOA_FISICA.toString(), pesCodigo ));
		}
		
		if (matricula != null){
			criteria.add(Restrictions.eq(ALIAS_SER + PONTO + RapServidores.Fields.MATRICULA.toString(), matricula ));
		}
		
		criteria.addOrder(Order.asc(ALIAS_PES + PONTO + RapPessoasFisicas.Fields.NOME.toString()));
		
		criteria.setProjection(obterProjectionsPesquisarServidor());
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		final List<RapServidoresVO> result =  executeCriteria(criteria, 0, 100, null, false);
		
		if(result != null && !result.isEmpty()){
			return result.get(0);
		} else {
			return null;
		}
	}
	
	public List<RapServidores> pesquisarServidorPorSituacaoAtivo(Object servidor) throws BaseException {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria = this.obterCriteriMetodoPesquisarServidorPorSituacaoAtivo(criteria, servidor);

		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString()+ "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public List<RapServidores> pesquisarServidorPorSituacaoAtivoComUsuario(Object servidor) throws BaseException {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria = this.obterCriteriMetodoPesquisarServidorPorSituacaoAtivo(criteria, servidor);

		criteria.add(Restrictions.isNotNull(RapServidores.Fields.USUARIO.toString()));
		
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString()+ "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarServidorPorSituacaoAtivoCount(Object servidor) throws BaseException {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria = this.obterCriteriMetodoPesquisarServidorPorSituacaoAtivo(criteria, servidor);
						
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria obterCriteriMetodoPesquisarServidorPorSituacaoAtivo(DetachedCriteria criteria, Object servidor) {
		String stParametro = (String) servidor;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),	RapServidores.Fields.PESSOA_FISICA.toString());
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), RapServidores.Fields.VINCULO.toString());

		
		if (StringUtils.isNotBlank(stParametro) && matricula==null) {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(),	stParametro, MatchMode.ANYWHERE));
		} else if(matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}

		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		
		return criteria;
	}
	

	public List<RapServidores> pesquisarServidorPorMatriculaOuNome(Object servidor) {

		DetachedCriteria criteria = criarCriteriaPesquisarServidor(servidor);
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA
				.toString() + "." + RapPessoasFisicas.Fields.NOME));
		return executeCriteria(criteria, 0, 100, null, false);
	}


	/**
	 * Pesquisa servidor por Ponto Servidor 
	 * @param servidor
	 * @return
	 */
	public List<RapServidores> pesquisarServidorPorMatriculaOuNomePontoServidor(Object servidor) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class,"SER");

		String stParametro = (String) servidor;
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), stParametro, MatchMode.ANYWHERE));
		}

		final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoPontoServidor.class, "PSR");
		subQuerie.createAlias("PSR." + ScoPontoServidor.Fields.SERVIDOR.toString(), "SER2", JoinType.INNER_JOIN);
		subQuerie.setProjection(Projections.property("PSR." + ScoPontoServidor.Fields.MATRICULA.toString()));
		
		subQuerie.add(Property.forName("SER." + RapServidores.Fields.MATRICULA.toString()).eqProperty("SER2." + RapServidores.Fields.MATRICULA.toString()));
		subQuerie.add(Property.forName("SER." + RapServidores.Fields.CODIGO_VINCULO.toString()).eqProperty("SER2." + RapServidores.Fields.CODIGO_VINCULO.toString()));

		
		criteria.add(Subqueries.exists(subQuerie));
		
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA
				.toString() + "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	
	
	
	
	public List<String> pesquisarServidorAtivo(Object paramPesquisa) {

		DetachedCriteria criteria = criarCriteriaPesquisarServidor(paramPesquisa);

		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=  sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO .toString(), DominioSituacaoVinculo.P),
						         Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));
		
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));

		criteria.setProjection(Projections.property(RapServidores.Fields.USUARIO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<String> pesquisarServidorAtivo(Integer matricula, Short vinCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));		
		
		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=  sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO .toString(), DominioSituacaoVinculo.P),
						         Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));
		
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));

		criteria.setProjection(Projections.property(RapServidores.Fields.USUARIO.toString()));

		return executeCriteria(criteria);
	}
	

	public List<RapServidores> pesquisarServidorSuggestion(Object servidor, String emaExaSigla, Integer emaManSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);

		String stParametro = (String) servidor;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), RapServidores.Fields.VINCULO.toString());

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), stParametro, MatchMode.ANYWHERE));
		}
		
		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=  sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO .toString(), DominioSituacaoVinculo.P),
						         Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		/*SubQuery que permite filtrar somente os que tem associação 
		 * a algum conselho que permita solicitar o tipo de exame em questão*/
		
		StringBuffer sql = new StringBuffer(360);
	    sql.append(	" exists ( 	select	1 ");
	    sql.append(	"			from	AGH.AEL_EXAME_CONSELHO_PROFS aelexameco,");
		sql.append(	"					AGH.V_RAP_SERVIDOR_CONSELHO vrapservid ");
		sql.append(	"			where	vrapservid.CPR_CODIGO 		= aelexameco.CPR_CODIGO ");
		sql.append(	"			and 	vrapservid.MATRICULA 		= {alias}.MATRICULA");
		sql.append(	"			and 	vrapservid.VIN_CODIGO 		= {alias}.VIN_CODIGO");
		sql.append(	"			and 	aelexameco.EMA_EXA_SIGLA 	= ? ");
		sql.append(	"			and 	aelexameco.EMA_MAN_SEQ 		= ? )");

		/*	parametros para a subquery	*/
		Object[] values = {emaExaSigla, emaManSeq};
		Type[] types = {StringType.INSTANCE, IntegerType.INSTANCE};
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));
		/*fim da subquery*/

		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString()+ "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	
	/**
	 * obj[0] - rap_pessoas_fisicas.nome 
	 * obj[1] - rap_pessoas_fisicas.sexo
	 * obj[2] - rap_vinculos.titulo_masculino 
	 * obj[3] - rap_vinculos.titulo_feminino 
	 * obj[4] - rap_conselhos_profissionais.titulo_masculino 
	 * obj[5] - rap_conselhos_profissionais.titulo_feminino
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 */
	public Object[] obtemDadosServidor(Short vinCodigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		// rap_pessoas_fisicas
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());
		
		// rap_vinculos
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(),
				RapServidores.Fields.VINCULO.toString());
		
		// rap_qualificacoes
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString(),
				RapPessoasFisicas.Fields.QUALIFICACOES.toString());
		
		// rap_tipos_qualificacao
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());
		
		// rap_conselhos_profissionais
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ stringSeparator
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString());
		
		addMatriculaVinculo(vinCodigo, matricula, criteria);
		
		criteria.add(Restrictions
				.isNotNull(RapPessoasFisicas.Fields.QUALIFICACOES.toString()
						+ stringSeparator
						+ RapQualificacao.Fields.NRO_REGISTRO_CONSELHO
								.toString()));
		
		criteria.setProjection(Projections.projectionList()
						.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()
												+ stringSeparator
												+ RapPessoasFisicas.Fields.NOME.toString()))
						.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()
												+ stringSeparator
												+ RapPessoasFisicas.Fields.SEXO.toString()))
						.add(Projections.property(RapServidores.Fields.VINCULO.toString()
												+ stringSeparator
												+ RapVinculos.Fields.TITULO_MASCULINO.toString()))
						.add(Projections.property(RapServidores.Fields.VINCULO.toString()
												+ stringSeparator
												+ RapVinculos.Fields.TITULO_FEMININO.toString()))
						.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
												+ stringSeparator
												+ RapConselhosProfissionais.Fields.TITULO_MASCULINO.toString()))
						.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
												+ stringSeparator
												+ RapConselhosProfissionais.Fields.TITULO_FEMININO.toString())));
		
		List<Object[]> retorno = executeCriteria(criteria);
		
		if (retorno != null && !retorno.isEmpty()) {
			
			return retorno.get(0);
			
		}

		return null;
		
	}
	
	private void addMatriculaVinculo(Short vinCodigo, Integer matricula,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(
				RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(),
				matricula));
	}

	private DetachedCriteria getCriteriaObterDadosServidor() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		// rap_pessoas_fisicas
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());
		
		// rap_qualificacoes
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString(),
				RapPessoasFisicas.Fields.QUALIFICACOES.toString());
		
		// rap_tipos_qualificacao
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());
		
		// rap_conselhos_profissionais
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ stringSeparator
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString());
		return criteria;
	}

	
	/**
	 * Busca o conselho profissional que possui Sigla e Registro,
	 * se nao estiver, busca o que possuir apenas a sigla se nao estiver item que possui a sigla retorna null
	 *  
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public ConselhoProfissionalServidorVO obterConselhoProfissional (	
			Integer matricula, Short vinCodigo) {
		
		List<ConselhoProfissionalServidorVO> lista = pesquisarConselhoProfissional(matricula, vinCodigo, Boolean.TRUE);
		
		ConselhoProfissionalServidorVO retorno = null;
		if(lista != null && !lista.isEmpty()){
			
			for (ConselhoProfissionalServidorVO conselhoProfissionalServidorVO : lista) {
				String sigla = conselhoProfissionalServidorVO.getSiglaConselho();
				String numeroRegistro = conselhoProfissionalServidorVO.getNumeroRegistroConselho();
				if(sigla != null && numeroRegistro != null){
					retorno = conselhoProfissionalServidorVO;
					break;
				}
				retorno = conselhoProfissionalServidorVO;
			}
		}
		return retorno;
	}
	
	public List<ConselhoProfissionalServidorVO> pesquisarConselhoProfissional (Integer matricula, Short vinCodigo) {
		return pesquisarConselhoProfissional(matricula, vinCodigo, Boolean.TRUE);
	}
	
	public List<ConselhoProfissionalServidorVO> pesquisarConselhoProfissionalAtivoInativo (Integer matricula, Short vinCodigo) {
		return pesquisarConselhoProfissional(matricula, vinCodigo, Boolean.FALSE);
	}
	
	public List<ConselhoProfissionalServidorVO> pesquisarConselhoProfissional (Integer matricula, Short vinCodigo, Boolean testaDataFimVinculo) {		
		DetachedCriteria criteria = getCriteriaObterDadosServidor();		
		getProjectionPesquisaConselhoProfissional(criteria);
		
		//Where
		addMatriculaVinculo(vinCodigo, matricula, criteria);
		
		if(testaDataFimVinculo){ //*
			criteria.add(Restrictions.or(
					Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
					Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(),
						 	  DateUtil.truncaData(new Date()))));
		}
		
		criteria.add(Restrictions.eq(RapPessoasFisicas.Fields.QUALIFICACOES.toString()
					+ stringSeparator
					+ RapQualificacao.Fields.SITUACAO.toString()
					, DominioSituacaoQualificacao.C));
		
		criteria.add(Restrictions.eq(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
				+ stringSeparator
				+ RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));		
		
		criteria.add(Restrictions.isNotNull(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
				+ stringSeparator
				+ RapConselhosProfissionais.Fields.SIGLA.toString()));
		
		criteria.addOrder(Order.asc(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
				+ stringSeparator
				+ RapConselhosProfissionais.Fields.SIGLA.toString()));
		
        criteria.addOrder(Order.asc(RapPessoasFisicas.Fields.QUALIFICACOES.toString() 
                + stringSeparator
                + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));

		
		criteria.setResultTransformer(Transformers.aliasToBean(ConselhoProfissionalServidorVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RapVinculos> obterVinculosPessoa(Integer codigoPessoa) {
		if (codigoPessoa == null) {
			throw new IllegalArgumentException("parametro nulo");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class)
			.setProjection(Property.forName(RapServidores.Fields.VINCULO.toString()));
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_PESSOA_FISICA.toString(), codigoPessoa));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Responsavel por buscar uma lista de RapServidores que contenham <b>nomeServidor</b> como parte do nome.<br>
	 * Busca migrada conforme view:<br>
	 * 
	 * ORADB view AGH.V_RAP_SERV_SOL_EXME.<br> 
	 * 
	 * Joins foram adaptados devido ao nao utilizacao das tabelas CSE_* que forma trocadas pra utilizacao de CSC_*.<br>
	 * 
	 * <b>Todas a regras do AGH atualmente foram migradas.</b><br>
	 * 
	 * <b>Melhoria Solicitada:</b>
	 * Regras pra Conselho e Qualificacao. Espera por definicao com Arquitetura.<br>
	 * 
	 * @param nomeServidor
	 * @param diasServidorFimVinculoPermitidoSolicitarExame default 365 dias.
	 * @return
	 */
	public List<RapServidores> pesquisarServidoresSolicitacaoExame(String param, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		Integer matricula = null;
		String nomeServidor = null;
		
		if (param != null){
			if (CoreUtil.isNumeroInteger(param)){
				matricula = Integer.valueOf(param);
			} else {
				nomeServidor = param;
			}			
		}		
		
		StringBuilder hql = this.getQueryPesquisaServidorSolicitacaoExame(null, matricula, nomeServidor, null, null);
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setMaxResults(30);		
		
		if (param != null){
			if (matricula != null) {
				query.setParameter("pMatricula", matricula);
			} else if (StringUtils.isNotBlank(nomeServidor)){
					query.setParameter("nomeServidor", param.toUpperCase() + "%");
			}			
		}
		
		this.doSetParamPesquisaServidoresSolicitacaoExame(query, diasServidorFimVinculoPermitidoSolicitarExame);
		
		//List<RapServidores> servidores = query.getResultList();
		List<PesquisaServidorSolicitacaoExameVO> listaPesquisaServidorSolicitacaoExameVO = this.getResultadoQueryPesquisaServidorSolicitacaoExame(query);
		
		List<RapServidores> retorno = new ArrayList<RapServidores>();
		//CascaService cs = getCascaService();
		for (PesquisaServidorSolicitacaoExameVO vo : listaPesquisaServidorSolicitacaoExameVO) {
			RapServidores servidor = this.obterRapServidorPorVinculoMatricula(vo.getMatricula(), vo.getVinCodigo());
			if (servidor.getUsuario() != null 
					&& cascaFacade.temPermissao(servidor.getUsuario(), COMPONENTE_SOLICITAR_EXAMES.toString(), METODO_SOLICITAR_EXAMES.toString())) {
				retorno.add(servidor);
			}
		}
		
		return retorno;
	}
	
	public List<RapServidores> pesquisarServidoresExameView(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		
		StringBuilder hql = this.getQueryPesquisaServidorSolicitacaoExame(vinculo, matricula, null, null, null);
		javax.persistence.Query query = this.createQuery(hql.toString());
		
		if (vinculo != null && matricula != null) {
			query.setParameter("pVinculo", vinculo);
			query.setParameter("pMatricula", matricula);
		}
		this.doSetParamPesquisaServidoresSolicitacaoExame(query, diasServidorFimVinculoPermitidoSolicitarExame);
		
		
		List<PesquisaServidorSolicitacaoExameVO> listaPesquisaServidorSolicitacaoExameVO = this.getResultadoQueryPesquisaServidorSolicitacaoExame(query);
		
		List<RapServidores> retorno = new ArrayList<RapServidores>();
		//CascaService cs = getCascaService();
		for (PesquisaServidorSolicitacaoExameVO vo : listaPesquisaServidorSolicitacaoExameVO) {
			RapServidores servidor = this.obterRapServidorPorVinculoMatricula(vo.getMatricula(), vo.getVinCodigo());
			if (servidor.getUsuario() != null) {
				retorno.add(servidor);
			}
		}
		
		
		return retorno;
	}
	
	public Integer pesquisarServidoresSolicitacaoExameCount(String nomeServidor, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		List<RapServidores> retorno = this.pesquisarServidoresSolicitacaoExame(nomeServidor, 
				diasServidorFimVinculoPermitidoSolicitarExame);
		if(retorno.isEmpty()) {
			return Integer.valueOf(0);
		}
		
		return retorno.size();
	}
	
	public List<RapServidores> pesquisarServidoresSolicitacaoExame(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SERVIDOR");
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.createAlias("SERVIDOR."+ RapServidores.Fields.PESSOA_FISICA.toString(), "PESFISICA", JoinType.INNER_JOIN);
		criteria.createAlias("PESFISICA."+ RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QUALIFICACAO", JoinType.INNER_JOIN);
		criteria.createAlias("QUALIFICACAO."+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TIPOQUALIF", JoinType.INNER_JOIN);
		criteria.createAlias("TIPOQUALIF."+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CONSELHO", JoinType.INNER_JOIN);
		
		if ((vinculo != null && matricula != null)) {
			criteria.add(Restrictions.and(Restrictions.eq("SERVIDOR."+RapServidores.Fields.MATRICULA.toString(), matricula),Restrictions.eq("SERVIDOR."+RapServidores.Fields.VIN_CODIGO.toString(), vinculo)));	
		}
		
		criteria.add(Restrictions.isNotNull("SERVIDOR."+RapServidores.Fields.USUARIO.toString()));
		criteria.add(Restrictions.isNotNull("QUALIFICACAO."+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));		
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A), 
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P), 
							Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), this.getDataFimVinculoPermitido(diasServidorFimVinculoPermitidoSolicitarExame)))));
		
		criteria.addOrder(Order.asc("PESFISICA."+RapPessoasFisicas.Fields.NOME.toString()));
		
		return executeCriteria(criteria, 0, 50, null, false);
	}
	
	public List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		
		StringBuilder hql = this.getQueryPesquisaQualificacoesServidorSolicitacaoExame(vinculo, matricula, null);
		javax.persistence.Query query = this.createQuery(hql.toString());
		
		if (vinculo != null && matricula != null) {
			query.setParameter("pVinculo", vinculo);
			query.setParameter("pMatricula", matricula);
		}
		
		query.setParameter("rangeIni", this.getDataFimVinculoPermitido(diasServidorFimVinculoPermitidoSolicitarExame));
		
		
		return  query.getResultList();
	}
	
	public RapServidores buscaVRapServSolExme(Short vinculo, Integer matricula, Integer diasServidorFimVinculoPermitidoSolicitarExame, String numeroConselho, String siglaConselho) throws ApplicationBusinessException{
		
		StringBuilder hql = this.getQueryPesquisaServidorSolicitacaoExame(vinculo, matricula, null, numeroConselho, siglaConselho);
		javax.persistence.Query query = this.createQuery(hql.toString());
		
		if (vinculo != null && matricula != null) {
			query.setParameter("pVinculo", vinculo);
			query.setParameter("pMatricula", matricula);
		}
		
		if (numeroConselho != null && siglaConselho != null) {
			
			query.setParameter("pNumeroConselho", numeroConselho);
			query.setParameter("pSiglaConselho", siglaConselho);
			
		}

		
		this.doSetParamPesquisaServidoresSolicitacaoExame(query, diasServidorFimVinculoPermitidoSolicitarExame);
		
		List<PesquisaServidorSolicitacaoExameVO> listaPesquisaServidorSolicitacaoExameVO = this.getResultadoQueryPesquisaServidorSolicitacaoExame(query);
		
		List<RapServidores> retorno = new ArrayList<RapServidores>();
		for (PesquisaServidorSolicitacaoExameVO vo : listaPesquisaServidorSolicitacaoExameVO) {
			RapServidores servidor = this.obterRapServidorPorVinculoMatricula(vo.getMatricula(), vo.getVinCodigo());
			retorno.add(servidor);
		}
		
		if (retorno != null && !retorno.isEmpty()) {
			
			return retorno.get(0);
			
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<PesquisaServidorSolicitacaoExameVO> getResultadoQueryPesquisaServidorSolicitacaoExame(javax.persistence.Query query) {

		final List<Object[]> list = query.getResultList();

		List<PesquisaServidorSolicitacaoExameVO> resultado = new LinkedList<PesquisaServidorSolicitacaoExameVO>();

		if (list != null && !list.isEmpty()) {

			for (Object[] listFields : list) {

				PesquisaServidorSolicitacaoExameVO vo = new PesquisaServidorSolicitacaoExameVO();

				vo.setMatricula((Integer) listFields[0]);
				vo.setVinCodigo((Short) listFields[1]);
				vo.setNome((String) listFields[2]);
				vo.setNomeUsual((String) listFields[3]);
				vo.setSigla((String) listFields[4]);
				vo.setNroRegConselho((String) listFields[5]);
				vo.setDtFimVinculo((Date) listFields[6]);

				resultado.add(vo);
				
			}
		}

		return resultado;
	}
	
	/**
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param nomeServidor
	 * @param numeroConselho
	 * @param siglaConselho
	 * @return
	 */
	private StringBuilder getQueryPesquisaServidorSolicitacaoExame(Short vinculo, Integer matricula, String nomeServidor, String numeroConselho, String siglaConselho) {
		StringBuilder hql = new StringBuilder(500);
		
		hql.append("select distinct ");
		hql.append("servidor.").append(RapServidores.Fields.MATRICULA.toString()).append(", ");
		hql.append("servidor.").append(RapServidores.Fields.VIN_CODIGO.toString()).append(", ");
		hql.append("pesFisica.").append(RapPessoasFisicas.Fields.NOME.toString()).append(", ");
		hql.append("pesFisica.").append(RapPessoasFisicas.Fields.NOME_USUAL.toString()).append(", ");
		hql.append("conselho.").append(RapConselhosProfissionais.Fields.SIGLA.toString()).append(", ");
		hql.append("qualificacao.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()).append(", ");
		hql.append("servidor.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString());
		
		hql.append(" from ").append(RapServidores.class.getSimpleName()).append(" servidor ");
		hql.append(" inner join servidor.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" pesFisica");
		hql.append(" inner join pesFisica.").append(RapPessoasFisicas.Fields.QUALIFICACOES.toString()).append(" qualificacao");
		hql.append(" inner join qualificacao.").append(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()).append(" tipoQualif");
		hql.append(" inner join tipoQualif.").append(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()).append(" conselho");
		
		// Se tiver (vinculo e matricula) ou (nome)
		if (matricula == null && !StringUtils.isNotBlank(nomeServidor)){
			hql.append(" where ");
		} else {
			if (matricula != null) { // Pesquisa pelo vinculo e matricula
				hql.append(" where ( servidor.").append(RapServidores.Fields.MATRICULA.toString());
				hql.append(" = :pMatricula ) ");
				
				if (vinculo != null){
					hql.append("and servidor.").append(RapServidores.Fields.CODIGO_VINCULO.toString());
					hql.append(" = :pVinculo ");
				}
			} else { // Pesquisa pelo nome
				hql.append(" where pesFisica.").append(RapPessoasFisicas.Fields.NOME.toString()).append(" like :nomeServidor ");
			}
			hql.append(" and ");
		}
		
		hql.append(" servidor.").append(RapServidores.Fields.USUARIO.toString()).append(" is not null ");
		hql.append(" and qualificacao.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()).append(" is not null ");
		hql.append(" and ( servidor.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" is null");
		hql.append(" or servidor.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" >= :rangeIni ) ");//Param 2
		if (numeroConselho != null && siglaConselho != null) {
			hql.append(" and conselho.sigla = :pSiglaConselho");
			hql.append(" and qualificacao.nroRegConselho = :pNumeroConselho");
		}
		hql.append(" order by pesFisica.nome");
		return hql;
	}
	
	private StringBuilder getQueryPesquisaQualificacoesServidorSolicitacaoExame(Short vinculo, Integer matricula, String nomeServidor) {
		StringBuilder hql = new StringBuilder(400);
		hql.append("select distinct qualificacao");
		hql.append(" from ").append(RapServidores.class.getSimpleName()).append(" servidor ");
		hql.append(" inner join servidor.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" pesFisica");
		hql.append(" inner join pesFisica.").append(RapPessoasFisicas.Fields.QUALIFICACOES.toString()).append(" qualificacao");
		hql.append(" inner join qualificacao.").append(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()).append(" tipoQualif");
		hql.append(" inner join tipoQualif.").append(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()).append(" conselho");
		hql.append(" left join fetch qualificacao.tipoQualificacao");
	
		// Se tiver (vinculo e matricula) ou (nome)
		if ((vinculo != null && matricula != null) || StringUtils.isNotBlank(nomeServidor)) {
			if (vinculo != null && matricula != null) { // Pesquisa pelo vinculo e matricula
				hql.append(" where ( servidor.").append(RapServidores.Fields.CODIGO_VINCULO.toString());
				hql.append(" = :pVinculo ");
				hql.append("and servidor.").append(RapServidores.Fields.MATRICULA.toString());
				hql.append(" = :pMatricula ) ");//Param 1				
			} else { // Pesquisa pelo nome
				hql.append(" where pesFisica.").append(RapPessoasFisicas.Fields.NOME.toString()).append(" like :nomeServidor ");//Param 1			
			}
			hql.append(" and ");
		} else {
			hql.append(" where ");
		}
		hql.append(" qualificacao.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()).append(" is not null ");
		hql.append(" and ( servidor.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" is null");
		hql.append(" or servidor.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" >= :rangeIni ) ");//Param 2
		
		
		return hql;
	}
	
	private Date getDataFimVinculoPermitido(Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		if (diasServidorFimVinculoPermitidoSolicitarExame == null) {
			diasServidorFimVinculoPermitidoSolicitarExame = 365;
		}

		Date periodoAnteriorPermitido = DateUtil.adicionaDias(new Date(), (diasServidorFimVinculoPermitidoSolicitarExame * -1));
		
		return periodoAnteriorPermitido;
	}
	
	private void  doSetParamPesquisaServidoresSolicitacaoExame(javax.persistence.Query query, Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		query.setParameter("rangeIni", this.getDataFimVinculoPermitido(diasServidorFimVinculoPermitidoSolicitarExame));
	}
	
	public RapServidores obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(Integer matricula, Short vinCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class,"servidor");
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		criteria.createAlias("servidor."+RapServidores.Fields.VINCULO.toString(), "vinculo");
		criteria.createAlias("servidor."+RapServidores.Fields.OCUPACAO_CARGO.toString(), "ocupacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.SERVIDOR.toString(), "serv_filho", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.RAMAL_TELEFONICO.toString(), "ramal", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "ccl", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv_filho."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf_filho", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.GRUPO_FUNCIONAL.toString(), "GRUPO_FUNCIONAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.CENTRO_CUSTO_DESEMPENHO.toString(), "CENTRO_CUSTO_DESEMPENHO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}

	
	public List<RapServidores> obterServidoresComPessoaFisicaPorNome(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "servidor");
		
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		criteria.add(Restrictions.or(
				Restrictions.isNull("servidor."+RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt("servidor."+RapServidores.Fields.DATA_FIM_VINCULO.toString(), DateUtil.truncaData(new Date()))
		));
		if (StringUtils.isNotEmpty(param)) {
			if (StringUtils.isNumeric(param) && param.length()<10){
				criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(param)));
				criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.MATRICULA.toString()));
			}else{
				criteria.add(Restrictions.ilike("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString(), param, MatchMode.ANYWHERE));
				criteria.addOrder(Order.asc("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString()));
			}	
		}else{
			criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.CODIGO_VINCULO.toString()));			
			criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.MATRICULA.toString()));			
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria, 0, 1000, null);
	}

	public List<RapServidores> pesquisarServidorLiberaExames(Object paramPesquisa, final String permissao) {

		List<RapServidores> servidoresComPermissao = new ArrayList<RapServidores>();

		List<String> servidores = pesquisarServidorAtivo(paramPesquisa);

		List<String> usuariosComPermissao = cascaFacade.
			pesquisarUsuariosComPermissao(StringUtils.trimToEmpty(permissao));

		if (servidores != null && !servidores.isEmpty()) {
			if (usuariosComPermissao != null && !usuariosComPermissao.isEmpty()) {

				for (String usuario : usuariosComPermissao) {
					if (servidores.contains(usuario.toUpperCase())) {
						try {
							servidoresComPermissao.add(obterServidorAtivoPorUsuarioSemCache(usuario.toUpperCase(), new Date()));
						} catch (ApplicationBusinessException e) {
							//engole silenciosamente a exceção pois neste caso
							//não é necessário para o negócio exibi-la uma 
							//vez que esta consulta é usada em um suggestion
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}

		}		

		return servidoresComPermissao;
	}
	
	public RapServidores pesquisarServidorLiberaExames(Integer matricula, Short vinCodigo, final String permissao) {
		
		List<String> servidores = pesquisarServidorAtivo(matricula, vinCodigo);
		
		if (servidores == null || servidores.isEmpty()) {
			return null;
		}

		List<String> usuariosComPermissao = cascaFacade.
			pesquisarUsuariosComPermissao(StringUtils.trimToEmpty(permissao));

		if (usuariosComPermissao != null && !usuariosComPermissao.isEmpty()) {
			List<RapServidores> servidoresComPermissao = new ArrayList<RapServidores>();
			
			for (String usuario : usuariosComPermissao) {
				if (servidores.contains(usuario.toUpperCase())) {
					try {
						servidoresComPermissao.add(obterServidorAtivoPorUsuario(usuario.toUpperCase()));
					} catch (ApplicationBusinessException e) {
						LOG.error(e.getMessage(), e);
						//engole silenciosamente a exceção pois neste caso
						//não é necessário para o negócio exibi-la uma 
						//vez que esta consulta é usada em um suggestion
					}
				}
			}
			
			if(!servidoresComPermissao.isEmpty()){
				return servidoresComPermissao.get(0);
			}			
		}
		
		return null;
	}
	
	public List<RapServidores> pesquisarServidoresOrdenadoPorVinCodigo(Object servidor) {
		DetachedCriteria criteria = criarCriteriaPesquisarServidor(servidor);
		criteria.addOrder(Order.asc(RapServidores.Fields.VIN_CODIGO.toString()));
		criteria.addOrder(Order.asc(RapServidores.Fields.MATRICULA.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	public List<RapServidores> pesquisarServidores(Object servidor) {

		DetachedCriteria criteria = criarCriteriaPesquisarServidor(servidor);

		criteria.addOrder(Order.asc(RapServidores.Fields.NOME_PESSOA_FISICA.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public List<RapServidores> listarTodosServidores(){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),RapServidores.Fields.PESSOA_FISICA.toString());
		return executeCriteria(criteria, 0, 200, null, true);
	}
	
	public Long pesquisarServidoresCount(Object servidor) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarServidor(servidor);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisarServidor(Object servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		String stParametro = (String) servidor;
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),RapServidores.Fields.PESSOA_FISICA.toString());
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), stParametro, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public Long pesquisarServidoresCount() {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		return executeCriteriaCount(criteria);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<RapServidores> pesquisarServidorCertificadoDigital(Object paramPesquisa, FccCentroCustos centroCusto) {
		
		String parametro = (String) paramPesquisa;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(parametro)) {
			matricula = Integer.valueOf(parametro);
		}
		
		StringBuilder hql = new StringBuilder(500);
		hql.append("select servidor ");
		hql.append("from RapServidores servidor left join fetch servidor.pessoaFisica left join fetch servidor.centroCustoLotacao left join fetch servidor.centroCustoAtuacao ");
		
		Query query = montaQueryPersquisarServidorCertificadoDigital(centroCusto, parametro, matricula, hql);	
		
		query.setMaxResults(100);
		return query.list();
	}

	private Query montaQueryPersquisarServidorCertificadoDigital(FccCentroCustos centroCusto,
			String parametro, Integer matricula, StringBuilder hql) {
		hql.append("where 1=1 ");
		
		// Condicao para matricula
		if(matricula != null){
			hql.append("and servidor.id.matricula = :matricula ");	
		
		// Condicao para nome
		}else if(StringUtils.isNotBlank(parametro)){
			hql.append("and upper(servidor.pessoaFisica.nome) like upper(:nome) ");	
		}
		
		if(centroCusto != null){
	       hql.append("and coalesce(servidor.centroCustoAtuacao, servidor.centroCustoLotacao) = :centroCusto ");
		}
		
		hql.append("and exists (select 1 from AghCertificadoDigital certificado ");
		hql.append("			where certificado.servidorResp = servidor) ");
		
		hql.append("order by servidor.pessoaFisica.nome ");
		
		//javax.persistence.Query query = this.createQuery(hql.toString());
		Query query = createHibernateQuery(hql.toString());
		
		// Parametro para matricula
		if(matricula != null){
			query.setParameter("matricula", matricula);
		}
		// Parametro para nome
		else if(StringUtils.isNotBlank(parametro)){
			query.setParameter("nome", parametro + "%");
		}
		
		if(centroCusto != null){
			query.setParameter("centroCusto", centroCusto);
		}	
		return query;
	}
		
	public Long pesquisarServidorCertificadoDigitalCount(Object paramPesquisa, FccCentroCustos centroCusto) {
		
		String parametro = (String) paramPesquisa;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(parametro)) {
			matricula = Integer.valueOf(parametro);
	}

		StringBuilder hql = new StringBuilder(500);
		hql.append("select count(*) ");
		hql.append("from RapServidores servidor left join servidor.pessoaFisica left join servidor.centroCustoLotacao left join servidor.centroCustoAtuacao ");

		Query query = montaQueryPersquisarServidorCertificadoDigital(centroCusto, parametro, matricula, hql);	
		
		return (Long) query.uniqueResult();
	}

	public void evict(RapServidores servidor) {
		this.desatachar(servidor);
	}
	
	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param nome
	 * 
	 * @return null se não encontrado
	 */
	public RapServidores buscarServidor(Short vinculo, Integer matricula,
			String nome) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());

		addMatriculaVinculo(vinculo, matricula, criteria);

		if (nome != null) {
			criteria.add(Restrictions.ilike(
					RapServidores.Fields.NOME_PESSOA_FISICA.toString(), nome,
					MatchMode.ANYWHERE));
		}

		List<RapServidores> servidores = executeCriteria(criteria, 0, 1, null,
				true);

		if (servidores.isEmpty()) {
			return null;
		}

		return servidores.get(0);
	}
	
	/**
	 * Retorna o primeiro servidor encontrado com os parâmetros fornecidos.
	 * 
	 * @param pesCodigo - codigo da Pessoa
	 * 
	 * @return null se não encontrado
	 */
	public RapServidores buscarServidor(Integer codigoPessoa) {
		List<RapServidores> servidores = pesquisarRapServidoresPorCodigoPessoa(codigoPessoa);

		if (servidores.isEmpty()) {
			return null;
		}

		return servidores.get(0);
	}
	
	public List<RapServidores> pesquisarRapServidoresPorCodigoPessoa(Integer codigoPessoa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), RapServidores.Fields.VINCULO.toString());
		criteria.createAlias(RapServidores.Fields.OCUPACAO_CARGO.toString(), RapServidores.Fields.OCUPACAO_CARGO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_PESSOA_FISICA.toString(), codigoPessoa));
		criteria.add(Restrictions.or(Restrictions.eq(
				RapServidores.Fields.IND_SITUACAO.toString(),
				DominioSituacaoVinculo.A), Restrictions.and(Restrictions.eq(
				RapServidores.Fields.IND_SITUACAO.toString(),
				DominioSituacaoVinculo.P), Restrictions.ge(
				RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		return executeCriteria(criteria);
	}
	

	public List<RapServidores> pesquisarServidor(Short vinCodigo, Integer matricula, DominioSituacaoVinculo indSituacao,
			RapPessoasFisicas pessoaFisica, String usuario,	FccCentroCustos codigoCCustoLotacao, FccCentroCustos codigoCCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao, OcupacaoCargoVO ocupacao,Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Date dataAtual, Date proximaData) {

		DetachedCriteria criteria = montarPesquisa(vinCodigo, matricula, indSituacao, pessoaFisica, usuario,codigoCCustoLotacao,
				codigoCCustoAtuacao, tipoRemuneracao, ocupacao, dataAtual, proximaData);

		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "CCL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.OCUPACAO_CARGO.toString(), "OC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), "VI", JoinType.LEFT_OUTER_JOIN);
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString()+ "." + RapPessoasFisicas.Fields.NOME));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}
	
	public Long pesquisarServidorCount(Short vinCodigo, Integer matricula,
			DominioSituacaoVinculo indSituacao, RapPessoasFisicas pessoaFisica,
			String usuario, FccCentroCustos codigoCCustoLotacao, FccCentroCustos codigoCCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao, OcupacaoCargoVO ocupacao, Date dataAtual, Date proximaData) {

		DetachedCriteria criteria = montarPesquisa(vinCodigo, matricula,
				indSituacao, pessoaFisica, usuario,codigoCCustoLotacao,
				codigoCCustoAtuacao, tipoRemuneracao, ocupacao, dataAtual, proximaData);

		return executeCriteriaCount(criteria);

	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria montarPesquisa(Short vinCodigo, Integer matricula,
			DominioSituacaoVinculo indSituacao, RapPessoasFisicas pessoaFisica,
			String usuario, FccCentroCustos codigoCCustoLotacao,FccCentroCustos codigoCCustoAtuacao,
			DominioTipoRemuneracao tipoRemuneracao, OcupacaoCargoVO ocupacao, Date dataAtual, Date proximaData) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");

		if (vinCodigo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		}

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		if (pessoaFisica != null && pessoaFisica.getCodigo()!=null) {
			criteria.add(Restrictions.eq("pessoaFisica."+RapPessoasFisicas.Fields.CODIGO.toString(),pessoaFisica.getCodigo()));
		}

		if (StringUtils.isNotBlank(usuario)) {
			criteria.add(Restrictions.ilike(RapServidores.Fields.USUARIO.toString(),usuario, MatchMode.ANYWHERE));
		}
		
		if (pessoaFisica!=null && !pessoaFisica.getNome().isEmpty()) {
			criteria.add(Restrictions.ilike("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString(),pessoaFisica.getNome(), MatchMode.ANYWHERE));
		}

		if (codigoCCustoLotacao != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_CENTRO_CUSTO_LOTACAO.toString(), codigoCCustoLotacao.getCodigo()));
		}

		if (codigoCCustoAtuacao != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_CENTRO_CUSTO_ATUACAO.toString(), codigoCCustoAtuacao.getCodigo()));
		}

		if (tipoRemuneracao != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.TIPO_REMINERACAO.toString(), tipoRemuneracao));
		}

		if (ocupacao!=null && ocupacao.getCodigoOcupacao() != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_OCUPACAO.toString(), ocupacao.getCodigoOcupacao()));
		}

		if (ocupacao!=null && !ocupacao.getCargoCodigo().isEmpty()) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_CARGO.toString(), ocupacao.getCargoCodigo()));
		}
		
		if(dataAtual != null & proximaData != null){
			criteria.add(Restrictions.between(RapServidores.Fields.DATA_FIM_VINCULO.toString(), dataAtual, proximaData));
		}

		return criteria;
	}	
	
	public List<RapServidores> pesquisarServidores(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) throws ApplicationBusinessException {

		DetachedCriteria criteria = montarConsulta(codigoCCLotacao, codVinculo, matricula, nomeServidor, codigoCCAtuacao);
		
		criteria.addOrder(Order.asc(RapServidores.Fields.CODIGO_VINCULO.toString()));
		criteria.addOrder(Order.asc(RapServidores.Fields.MATRICULA.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}	

	
	/**
	 * Retorna servidores de acordo com a matricula ou nome fornecido que estejam com situação Ativa ou Programado
	 * e com data de fim de vínculo maior ou igual a data atual do sistema.
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param servidor
	 *            matricula ou nome
	 * @return servidores encontrados em ordem alfabética ou lista vazia se não
	 *         encontrado
	 */
	public List<RapServidores> pesquisarServidorVinculoAtivoEProgramadoAtual(Object servidor) {

		DetachedCriteria criteria = criarCriteriaPesquisarServidor(servidor);
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), "VINC");

		
		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=  sysdate)));
		criteria
				.add(Restrictions.or(Restrictions.eq(
						RapServidores.Fields.IND_SITUACAO.toString(),
						DominioSituacaoVinculo.A), Restrictions.and(
						Restrictions.eq(RapServidores.Fields.IND_SITUACAO
								.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO
								.toString(), new Date()))));

		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));
		

		return executeCriteria(criteria, 0, 100, null, false);
	}	
	
	public Long pesquisarServidorCount(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao) throws ApplicationBusinessException {

		DetachedCriteria criteria = montarConsulta(codigoCCLotacao, codVinculo,
				matricula, nomeServidor, codigoCCAtuacao);		
		
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarServidorCount(RapRamalTelefonico ramalTelefonico) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);
		criteria.add(Restrictions.eq(
				RapServidores.Fields.RAMAL_TELEFONICO.toString(),
				ramalTelefonico));

		return executeCriteriaCount(criteria);
	}	

	public List<RapServidores> obterServidorPeloDependente(
			RapDependentes dependentes) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);

		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_PESSOA_FISICA.toString(), dependentes.getId()
				.getPesCodigo()));
		// criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(),
		// DominioSituacao.A));

		// RapServidores servidores = (RapServidores)
		// executeCriteriaUniqueResult(criteria);

		// return servidores;
		List<RapServidores> servidores = executeCriteria(criteria);

		if (servidores.isEmpty()) {
			return null;
		}

		return servidores;
	}
		
	private DetachedCriteria montarConsulta(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao) throws ApplicationBusinessException {

		if (codigoCCLotacao == null) {
			throw new ApplicationBusinessException(RapServidoresDAOExceptionCode.MENSAGEM_CENTRO_CUSTO_LOTACAO_NAO_INFORMADO);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "CCL", JoinType.LEFT_OUTER_JOIN);


		if (StringUtils.isNotBlank(nomeServidor)) {
			criteria.add(Restrictions.like("PF."+RapPessoasFisicas.Fields.NOME.toString(),
								StringUtils.trimToNull(nomeServidor.toUpperCase()),MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq("CCL."+FccCentroCustos.Fields.CODIGO.toString(),codigoCCLotacao));

		if (codigoCCAtuacao != null) {
			criteria.add(Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(),codigoCCAtuacao));
		}

		if (codVinculo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(),codVinculo.shortValue()));
		}
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}

		return criteria;
	}
	
	/**
	 * Consitência de duplicidade de pessoa realizada pela Procedure
	 * RAPP_ENFORCE_SER_RULES.
	 * 
	 * @param codigoPessoaFisica
	 * @param codigoVinculo
	 * @param indPermiteDuplicado
	 * @throws ApplicationBusinessException
	 */
	public void verificaDuplicidadePessoaVinculo(Integer codigoPessoaFisica,
			Short codigoVinculo, Integer matricula,
			DominioSimNao indPermiteDuplicado) throws ApplicationBusinessException {

		if (codigoPessoaFisica == null || codigoVinculo == null
				|| indPermiteDuplicado == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios");
		}

		if (indPermiteDuplicado == DominioSimNao.S) {
			return;
		}
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);
		criteria.add(Restrictions.eq(
				RapServidores.Fields.CODIGO_PESSOA_FISICA.toString(),
				codigoPessoaFisica));
		criteria.add(Restrictions.eq(
				RapServidores.Fields.CODIGO_VINCULO.toString(), codigoVinculo));
		criteria.add(Restrictions.or(Restrictions
				.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt(
						RapServidores.Fields.DATA_FIM_VINCULO.toString(),
						new Date())));
		
		if (executeCriteriaCount(criteria) > 0) {
			throw new ApplicationBusinessException(
					RapServidoresDAOExceptionCode.MENSAGEM_PESSOA_COM_VINCULO_DUPLICADO);
		}
	}
	
	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_AFAST
	 * 
	 * @param codigoVinculo
	 * @param codigoMatricula
	 * @param dataFimVinculo
	 * @throws ApplicationBusinessException
	 */
	public void verificarAfastamentoVigente(Short codigoVinculo,
			Integer codigoMatricula, Date dataFimVinculo)
			throws ApplicationBusinessException {

		if (codigoVinculo == null || codigoMatricula == null
				|| dataFimVinculo == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapAfastamento.class);
		criteria.add(Restrictions.eq(
				RapAfastamento.Fields.CODIGO_VINCULO_SERVIDOR.toString(),
				codigoVinculo));
		criteria.add(Restrictions.eq(
				RapAfastamento.Fields.MATRICULA_SERVIDOR.toString(),
				codigoMatricula));
		criteria.add(Restrictions.or(Restrictions
				.isNotNull(RapAfastamento.Fields.DT_FIM.toString()),
				Restrictions.gt(RapAfastamento.Fields.DT_FIM.toString(),
						dataFimVinculo)));

		if (executeCriteriaCount(criteria) > 0) {
			throw new ApplicationBusinessException(
					RapServidoresDAOExceptionCode.MENSAGEM_FIM_VINCULO_DENTRO_PERIODO_AFASTAMENTO);
		}
	}
	
	public RapServidores obterServidor(RapServidoresId rapServidoresId) {
		return obterPorChavePrimaria(rapServidoresId);
	}
	
	public RapServidores obterServidor(RapCodStarhLivres rapCodStarhLivres) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(RapServidores.class);

		if (rapCodStarhLivres != null && rapCodStarhLivres.getCodStarh() != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODSTARH.toString(), rapCodStarhLivres.getCodStarh()));
		}
		
		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Controle para não possibilitar código de matrícula já cadastrado
	 * Obs: Quando NÃO for HCPA o código da matrícula é informado manualmente
	 * 
	 * @param codigoMatricula
	 * @return
	 */
	public void matriculaJaExiste(Integer codigoMatricula, boolean validarMatriculaAtiva)
			throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(),
				codigoMatricula));

		if (validarMatriculaAtiva) {
			criteria.add(Restrictions.or(Restrictions
					.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
					Restrictions.gt(
							RapServidores.Fields.DATA_FIM_VINCULO.toString(),
							new Date())));
		}

		if (executeCriteriaCount(criteria) > 0) {
			throw new ApplicationBusinessException(
					RapServidoresDAOExceptionCode.MENSAGEM_MATRICULA_JA_EXISTE);
		}
	}
	
	public List<RapServidores> pesquisarServidorAtivoPorVinculos(Object paramPesquisa, List<Short> vinculos) {

		DetachedCriteria criteria = criarCriteriaPesquisarServidor(paramPesquisa);
		
		Criterion c1 = Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString());
		Criterion c2 = Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		criteria.add(Restrictions.or(c1, c2));
		
		if(vinculos != null && !vinculos.isEmpty()){
			criteria.add(Restrictions.in(RapServidores.Fields.CODIGO_VINCULO.toString(), vinculos));
		}
		
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public List<RapServidores> pesquisarServidoresAtivosPendentes(Object servidor){
		DetachedCriteria criteria  = DetachedCriteria.forClass(RapServidores.class);

		String stParametro = (String) servidor;
		Integer matricula = null;
		Short vinculo = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		if (CoreUtil.isNumeroShort(stParametro)) {
			vinculo = Short.valueOf(stParametro);
		}


		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);

		if (matricula != null || vinculo != null) {
			criteria.add(Restrictions.or(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula), 
					Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), vinculo)));
		}
		else if(stParametro!= null && !stParametro.isEmpty()) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(),	stParametro.toUpperCase(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(RapServidores.Fields.IND_SITUACAO.toString(), new Object[]{DominioSituacaoVinculo.A, DominioSituacaoVinculo.P}));

		criteria.add(Restrictions.or(Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()), Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString())));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarServidoresAtivosPendentesCount(Object servidor){
		DetachedCriteria criteria  = DetachedCriteria.forClass(RapServidores.class);

		String stParametro = (String) servidor;
		Integer matricula = null;
		Short vinculo = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		if (CoreUtil.isNumeroShort(stParametro)) {
			vinculo = Short.valueOf(stParametro);
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);

		if (matricula != null && vinculo != null) {
			criteria.add(Restrictions.or(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula), 
					Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), vinculo)));
		} else if(stParametro!= null && !stParametro.isEmpty()) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(),	stParametro.toUpperCase(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(RapServidores.Fields.IND_SITUACAO.toString(), new Object[]{DominioSituacaoVinculo.A, DominioSituacaoVinculo.P}));

		criteria.add(Restrictions.or(Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()), Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString())));
		
		return executeCriteriaCount(criteria);
	}
	
    public List<RapServidores> pesquisarServidoresAtivosPendentesMedicoProfessoreUnidade(Object servidor, short unfSeq){
        DetachedCriteria criteria = montarCriteriaServidoresAtivosPendentesMedicoProfessoreUnidade(servidor, unfSeq);
        criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
        return executeCriteria(criteria, 0, 100, null, false);
    }

	
    public Long pesquisarServidoresAtivosPendentesMedicoProfessoreUnidadeCount(Object servidor, short unfSeq){
        DetachedCriteria criteria = montarCriteriaServidoresAtivosPendentesMedicoProfessoreUnidade(servidor, unfSeq);
        return executeCriteriaCount(criteria);
    }
	
	private DetachedCriteria montarCriteriaServidoresAtivosPendentesMedicoProfessoreUnidade(Object servidor, short unfSeq){
		DetachedCriteria criteria  = DetachedCriteria.forClass(RapServidores.class, "rap");

		String stParametro = (String) servidor;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else if(stParametro!= null && !stParametro.isEmpty()) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(),	stParametro.toUpperCase(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(RapServidores.Fields.IND_SITUACAO.toString(), new Object[]{DominioSituacaoVinculo.A, DominioSituacaoVinculo.P}));

		criteria.add(Restrictions.or(Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()), Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString())));
		
		DetachedCriteria subCriteria  = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		subCriteria.setProjection(Projections.property("puc."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));
		subCriteria.add(Restrictions.eqProperty("puc."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), "rap."+RapServidores.Fields.MATRICULA.toString()));
		subCriteria.add(Restrictions.eqProperty("puc."+MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), "rap."+RapServidores.Fields.CODIGO_VINCULO.toString()));
		subCriteria.add(Restrictions.eq("puc."+MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), DominioFuncaoProfissional.MPF));
		subCriteria.add(Restrictions.eq("puc."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		return criteria;
	}
	
	public RapServidores obterServidorPeloProntuarioPeloVinculoEMatricula(Integer prontuario, Short vinCodigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		criteria.add(Restrictions.eq("PES."+RapPessoasFisicas.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		return (RapServidores)executeCriteriaUniqueResult(criteria);
	}
	
	
	
	/**
	 * Pesquisa todos os servidores a partir de um nome, ou parte dele, e por uma lista de usuários
	 * 
	 * Este método foi feito para reproduzir, em parte, a view V_FAT_MEDICOS_AUDITORES
	 * 
	 * SELECT DISTINCT ser.matricula, ser.vin_codigo, pfi.nome, pfi.nome_usual
	 *   FROM agh.cse_perfis_usuarios pus, agh.cse_usuarios usu, agh.rap_pessoas_fisicas pfi, agh.rap_servidores ser
	 *  WHERE pfi.codigo = ser.pes_codigo
	 *    AND ser.matricula = usu.ser_matricula
	 *    AND ser.vin_codigo = usu.ser_vin_codigo
	 *    AND usu.id = pus.usr_id
	 *    AND pus.per_nome = 'FATG_AUDITOR_SUS'
	 *  ORDER BY ser.matricula, ser.vin_codigo, pfi.nome, pfi.nome_usual
	 *  
	 *  Exemplo saída:
	 *  23689;959;"FULANO SILVA";"FULANO"
	 *  
	 * @param nome Nome, ou parte, de uma servidor
	 * @param listaUsuarios Lista contendo logins de usuários do sistema
	 * @param ativo Indicador que informa se deve retornar apenas os ativos (true), ou todos (false)
	 * @return Servidores que contemplem os filtros de pesquisa ou lista vazia caso contrário
	 */
	public List<RapServidores> pesquisarServidorPorNomeEUsuarios(String nome, Collection<String> listaUsuarios, Boolean ativo) {
		if ((nome == null || nome.isEmpty()) && (listaUsuarios == null || listaUsuarios.isEmpty())) {
			return new ArrayList<RapServidores>();
		}

		DetachedCriteria criteria = montarConsultaServidorPorNomeEUsuarios(nome, listaUsuarios, ativo);
				
		criteria.addOrder(Order.asc(RapServidores.Fields.MATRICULA.toString()));
		criteria.addOrder(Order.asc(RapServidores.Fields.VINCULO.toString()));
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA + "." + RapPessoasFisicas.Fields.NOME));
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA + "." + RapPessoasFisicas.Fields.NOME_USUAL));
		
		return executeCriteria(criteria, 0, 25, null, false);
	}
	
	public Long pesquisarServidorPorNomeEUsuariosCount(String nome, Collection<String> listaUsuarios, Boolean ativo) {
		if ((nome == null || nome.isEmpty()) && (listaUsuarios == null || listaUsuarios.isEmpty())) {
			return 0L;
		}

		DetachedCriteria criteria = montarConsultaServidorPorNomeEUsuarios(nome, listaUsuarios, ativo);
		
		return executeCriteriaCount(criteria);
	}	
	
	private DetachedCriteria montarConsultaServidorPorNomeEUsuarios(String nomeServidor, Collection<String> listaUsuarios, Boolean ativo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		if (!StringUtils.isBlank(nomeServidor)) {			
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), nomeServidor, MatchMode.ANYWHERE));
		}
		
		if (listaUsuarios != null && !listaUsuarios.isEmpty()) {	
			String[] arrayUsuarios = listaUsuarios.toArray(new String[listaUsuarios.size()]);
			String[] usuarios = new String[arrayUsuarios.length*2];
			// Considera tanto o login normal, como veio do CASCA, como em caixa alta
			for (int i = 0; i < arrayUsuarios.length; i = i+2) {			
				usuarios[i] = arrayUsuarios[i];
				usuarios[i+1] = arrayUsuarios[i].toUpperCase();				
			}			
			
			criteria.add(Restrictions.isNotNull(RapServidores.Fields.USUARIO.toString()));			
			criteria.add(Restrictions.in(RapServidores.Fields.USUARIO.toString(), usuarios));
		}		
		
		if (ativo) {
			Criterion c1 = Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString());
			Criterion c2 = Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
			criteria.add(Restrictions.or(c1, c2));
		}
		
		return criteria;		
	}	
	
	public String obterPrimeiroNroRegistroConselho(Integer matricula, Short vinculo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + stringSeparator + RapPessoasFisicas.Fields.QUALIFICACOES,
				RapPessoasFisicas.Fields.QUALIFICACOES.toString());

		criteria.createAlias(RapPessoasFisicas.Fields.QUALIFICACOES.toString() + stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());

		criteria.createAlias(RapQualificacao.Fields.TIPO_QUALIFICACAO.toString() + stringSeparator
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString());

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}

		if (vinculo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		}

		criteria.add(Restrictions.or(Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));

		criteria.add(Restrictions.isNotNull(RapPessoasFisicas.Fields.QUALIFICACOES.toString() + stringSeparator
				+ RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		criteria.add(Restrictions.in(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() + stringSeparator
				+ RapConselhosProfissionais.Fields.SIGLA.toString(), restricoes));

		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(RapPessoasFisicas.Fields.QUALIFICACOES.toString() + stringSeparator
						+ RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString())));

		String nroConselho = "";
		List<String> res = executeCriteria(criteria, 0, 1, null, true);
		if (res != null && !res.isEmpty()) {
			nroConselho = res.get(0);
		}
		return nroConselho;
	}
	
	/**
	 * ORADB Function RAPC_BUSC_NRO_R_CONS
	 */
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula) {
		return buscarNroRegistroConselho(vinCodigo, matricula, Boolean.TRUE);
	}
	
	/**
	 * ORADB Function RAPC_BUSC_NRO_R_CONS_CUSTOM
	 */
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula, Boolean verificaDataFimVinculo) {
		String nroRegConselho = null;
		final Date dataAtual = new Date();
		final String aliasQualificacoes = "qualificacoes";
		final String propertyNroRegConselho = aliasQualificacoes + "." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString();
		final List<String> listaSiglas = new ArrayList<String>();
		listaSiglas.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		listaSiglas.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		DetachedCriteria criteriaPessoasFisicas = criteria.createCriteria(RapServidores.Fields.PESSOA_FISICA.toString());
		DetachedCriteria criteriaQualificacoes = criteriaPessoasFisicas.createCriteria(
				RapPessoasFisicas.Fields.QUALIFICACOES.toString(), aliasQualificacoes);
		DetachedCriteria criteriaTiposQualificacao = criteriaQualificacoes.createCriteria(RapQualificacao.Fields.TIPO_QUALIFICACAO
				.toString());
		DetachedCriteria criteriaConselhos = criteriaTiposQualificacao.createCriteria(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL
				.toString());

		if(verificaDataFimVinculo){
			criteria.add(Restrictions.or(Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
					Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), dataAtual)));
		}
		
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteriaQualificacoes.add(Restrictions.isNotNull(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteriaConselhos.add(Restrictions.in(RapConselhosProfissionais.Fields.SIGLA.toString(), listaSiglas));

		criteria.setProjection(Projections.property(propertyNroRegConselho));

		List<String> listaNumerosConselho = executeCriteria(criteria, 0, 1, null, true);
		if (listaNumerosConselho != null && !listaNumerosConselho.isEmpty()) {
			nroRegConselho = listaNumerosConselho.get(0);
		} else {
			nroRegConselho = null;
		}

		return nroRegConselho;				
	}
	
	/**
	 * ORADB Function RAPC_BUS_NOM_NR_CONS
	 */
	public DescricaoControlePacienteVO buscarDescricaoAnotacaoControlePaciente(Short vinCodigo, Integer matricula) {
		
		final String aliasQualificacoes = "qualificacoes";
		final String aliasConselhos = "conselhos";

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		DetachedCriteria criteriaPessoasFisicas = criteria.createCriteria(RapServidores.Fields.PESSOA_FISICA.toString());
		DetachedCriteria criteriaQualificacoes = criteriaPessoasFisicas.createCriteria(
				RapPessoasFisicas.Fields.QUALIFICACOES.toString(), aliasQualificacoes);
		DetachedCriteria criteriaTiposQualificacao = criteriaQualificacoes.createCriteria(RapQualificacao.Fields.TIPO_QUALIFICACAO
				.toString());
		criteriaTiposQualificacao.createCriteria(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), aliasConselhos);
		
		criteria.add(Restrictions.or(Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		criteriaQualificacoes.add(Restrictions.eq(RapQualificacao.Fields.SITUACAO.toString(), DominioSituacaoQualificacao.C));
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		criteria.add(Restrictions.isNotNull(aliasQualificacoes + stringSeparator + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(
						aliasQualificacoes + stringSeparator + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), "nroRegistroConselho")
				.add(Projections.property(aliasConselhos + stringSeparator + RapConselhosProfissionais.Fields.SIGLA.toString()), "siglaConselho"));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DescricaoControlePacienteVO.class));
		
		criteria.addOrder(Order.desc(aliasQualificacoes + "." + RapQualificacao.Fields.DATA_FIM));
		
		List<DescricaoControlePacienteVO> listaNumerosConselhoSiglas = executeCriteria(criteria, 0, 1, null, true);
		if (listaNumerosConselhoSiglas != null && listaNumerosConselhoSiglas.size() > 0) {
			return listaNumerosConselhoSiglas.get(0);
		}
		
		return null;
	}

	public String obterNomeServidor(Integer matricula, Short vinculo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		if (vinculo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		}
		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + stringSeparator
						+ RapPessoasFisicas.Fields.NOME.toString())));

		String nomeServidor = "";
		List<String> res = executeCriteria(criteria, 0, 1, null, true);
		if (res != null && !res.isEmpty()) {
			nomeServidor = res.get(0);
		}
		return nomeServidor;
	}
	
	/**
	 * Retorna rapServidores de acordo com o codigo passado por parametro
	 * 
	 * @param codigo
	 * @return
	 */
	public RapServidores pesquisarResponsavel(Short codigo, Integer matricula, String nomeResponsavel) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		addMatriculaVinculo(codigo, matricula, criteria);

		criteria.add(Restrictions.ilike(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME,
				nomeResponsavel, MatchMode.ANYWHERE));

		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=
		// sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));

		List<RapServidores> servidores = executeCriteria(criteria, 0, 1, null, true);
		if (servidores != null && !servidores.isEmpty()) {
			return servidores.get(0);
		}
		return null;
	}
	
	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */
	public List<RapServidores> pesquisarResponsaveis(Object responsavel) {
		
		DetachedCriteria criteria = obterCriteriaPesquisarResponsaveis(responsavel);

		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(RapServidores.class);

		cri.setProjection(Projections.projectionList().add(Projections.property("QF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("PF.nome"), "nome").add(Projections.property("id.matricula"), "matricula")
				.add(Projections.property("id.vinCodigo"), "vinCodigo"));

		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES, "QF",
				JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES + "."
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQ", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(
				RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES + "."
						+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString() + "."
						+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.LEFT_OUTER_JOIN);

		if (strPesquisa != null && !"".equalsIgnoreCase(strPesquisa.toString())) {

			cri.add(Restrictions.or(
					Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa.toString(), MatchMode.ANYWHERE),
					Restrictions.eq("QF.nroRegConselho", strPesquisa)));
		}

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		cri.add(Restrictions.in("CP.sigla", restricoes));
		cri.add(Restrictions.eq("TQ.tipoQualificacao", DominioTipoQualificacao.CCC));
		cri.add(Restrictions.in("TQ.cccNivelCurso", new Object[] { Short.valueOf("3"), Short.valueOf("7") }));
		cri.add(Restrictions.or(Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		cri.setResultTransformer(Transformers.aliasToBean(ServidorConselhoVO.class));

		cri.addOrder(Order.asc("PF.nome"));

		return executeCriteria(cri, 0, 25, null, false);
	}
	
	public List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(RapServidores.class);

		cri.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("QF.nroRegConselho"), "nroRegConselho").add(Projections.property("PF.nome"), "nome")
				.add(Projections.property("id.matricula"), "matricula").add(Projections.property("id.vinCodigo"), "vinCodigo")
				.add(Projections.property("PF.nomeUsual"), "nomeUsual").add(Projections.property("PF.cpf"), "cpf")));

		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		cri.createAlias(RapServidores.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES, "QF",
				JoinType.INNER_JOIN);
		cri.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES + "."
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQ", JoinType.INNER_JOIN);
		cri.createAlias(
				RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.QUALIFICACOES + "."
						+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString() + "."
						+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.INNER_JOIN);

		if (strPesquisa != null && !"".equalsIgnoreCase(strPesquisa.toString())) {

			cri.add(Restrictions.or(
					Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa.toString(), MatchMode.ANYWHERE),
					Restrictions.eq("QF.nroRegConselho", strPesquisa)));
		}

		cri.add(Restrictions.isNotNull("QF.nroRegConselho"));

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		cri.add(Restrictions.in("CP.sigla", restricoes));
		cri.add(Restrictions.or(Restrictions.isNull(RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		cri.setResultTransformer(Transformers.aliasToBean(ServidoresCRMVO.class));

		cri.addOrder(Order.asc("PF.nome"));

		return executeCriteria(cri, 0, 25, null, false);
	}

	

	public RapServidores pesquisaRapServidorPorSituacaoPacienteVO(SituacaoPacienteVO situacaoPacienteVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		if (situacaoPacienteVO.getAipPacienteJn().getMatriculaServidorRecadastro() == null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getMatriculaServidorCadastro()));
		} else {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getMatriculaServidorRecadastro()));
		}

		if (situacaoPacienteVO.getAipPacienteJn().getVinCodigoServidorRecadastro() == null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getVinCodigoServidorCadastro()));
		} else {
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getVinCodigoServidorRecadastro()));
		}

		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}
	
	public List<RapServidores> pesquisarServidores(String paramPesquisa, AghEspecialidades especialidade, FatConvenioSaude convenio,
			Integer[] tipoQualificacao, Integer maxResults) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.INNER_JOIN);
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQF", JoinType.INNER_JOIN);
		criteria.createAlias(RapServidores.Fields.ESPECIALIDADE.toString(), "PRF_ESP", JoinType.INNER_JOIN);
		criteria.createAlias("PRF_ESP." + AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(), "PRF_ESP_CNV",
				JoinType.INNER_JOIN);

		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		criteria.add(Restrictions.in("TQF." + RapTipoQualificacao.Fields.CODIGO.toString(), tipoQualificacao));
		criteria.add(Restrictions.isNotNull("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO));
		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.IND_INTERNA, true));

		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.IND_SITUACAO, DominioSituacao.A));
		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.ID_CONVENIO_CODIGO.toString(),
				convenio.getCodigo()));
		if (especialidade != null) {
			criteria.add(Restrictions.eq("PRF_ESP." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), especialidade));
		}

		if (!StringUtils.isEmpty(paramPesquisa)) {
			criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), paramPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, maxResults, null, false);
	}
	
	public List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio, Integer[] tipoQualificacao, Integer maxResults) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "nome")
				.add(Projections.property("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), "nroRegConselho")
				.add(Projections.property(RapServidores.Fields.MATRICULA.toString()), "matricula")
				.add(Projections.property(RapServidores.Fields.CODIGO_VINCULO.toString()), "vinCodigo"));

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.INNER_JOIN);
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQF", JoinType.INNER_JOIN);
		criteria.createAlias(RapServidores.Fields.ESPECIALIDADE.toString(), "PRF_ESP", JoinType.INNER_JOIN);
		criteria.createAlias("PRF_ESP." + AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(), "PRF_ESP_CNV",
				JoinType.INNER_JOIN);

		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		criteria.add(Restrictions.in("TQF." + RapTipoQualificacao.Fields.CODIGO.toString(), tipoQualificacao));
		criteria.add(Restrictions.isNotNull("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.IND_INTERNA.toString(), true));

		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PRF_ESP_CNV." + AghProfissionaisEspConvenio.Fields.ID_CONVENIO_CODIGO.toString(),
				convenio.getCodigo()));
		if (especialidade != null) {
			criteria.add(Restrictions.eq("PRF_ESP." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), especialidade));
		}

		if (StringUtils.isNotEmpty(paramPesquisa)) {
			if (StringUtils.isNumeric(paramPesquisa)) {
				criteria.add(Restrictions.ilike("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(), paramPesquisa,
						MatchMode.ANYWHERE));
			} else {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), paramPesquisa, MatchMode.ANYWHERE));
			}
		}

		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresTransPacienteVO.class));

		return executeCriteria(criteria, 0, maxResults, null, false);
	}

	public RapServidores obterSubstituto(Short vinculoSubstituto, Integer matriculaSubstituto) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());
		addMatriculaVinculo(vinculoSubstituto, matriculaSubstituto, criteria);
		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >=
		// sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME));
		List<RapServidores> servidores = executeCriteria(criteria);
		if (servidores.size() != 0) {
			return servidores.get(0);
		}
		return null;
	}
	
	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	public List<RapServidores> pesquisarProfissionaisSubstitutos(Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV, Integer medicinaId, Integer odontologiaId) throws ApplicationBusinessException {
		if (especialidadeId == null || convenioId == null || data == null) {
			throw new IllegalArgumentException("Os argumentos especialidadeId, convenioId e data são obrigatórios.\n especialidadeId="
					+ especialidadeId + ", convenioId=" + convenioId + "e data=" + data);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		// criteria servidor - servidores ativos e com data programada futura
		java.sql.Date hoje = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		SimpleExpression ativos = Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A);
		LogicalExpression programados = Restrictions.and(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
				Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), hoje));
		criteria.add(Restrictions.or(ativos, programados));
		// criteria especialidadesProfissional - servidor interna na
		// especialidade
		DetachedCriteria profEspCriteria = criteria.createCriteria("aghProfEspecialidades");
		profEspCriteria.add(Restrictions.eq("indInterna", DominioSimNao.S));
		profEspCriteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(), especialidadeId));

		DetachedCriteria pessoaFisicaCriteria = criteria.createCriteria("pessoaFisica");

		// criteria qualificacoes - graduado em medicina ou odontologia com
		// registro no conselho profissional
		DetachedCriteria qualificacoesCriteria = pessoaFisicaCriteria.createCriteria("qualificacoes");
		qualificacoesCriteria.add(Restrictions.isNotNull(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));

		// graduados em medicina ou odontologia
		List<Integer> medicinaOuOdontologia = new ArrayList<Integer>();
		medicinaOuOdontologia.add(medicinaId);
		medicinaOuOdontologia.add(odontologiaId);
		qualificacoesCriteria.add(Restrictions.in(RapQualificacao.Fields.TIPO_QUALIFICACAO_CODIGO.toString(), medicinaOuOdontologia));

		// criteria escala -
		DetachedCriteria escalaCriteria = profEspCriteria.createCriteria("escalasProfissionaisInt");
		escalaCriteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), convenioId));
		escalaCriteria.add(Restrictions.le(AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), data));
		LogicalExpression dtFimNotNull = Restrictions.and(
				Restrictions.isNotNull(AinEscalasProfissionalInt.Fields.DATA_FIM.toString()),
				Restrictions.ge(AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), data));

		// se data menor que atual considera data fim null
		// if (data.before(hoje)) {
		Criterion dtFimNull = Restrictions.isNull(AinEscalasProfissionalInt.Fields.DATA_FIM.toString());
		// fim null e not null
		LogicalExpression or = Restrictions.or(dtFimNull, dtFimNotNull);
		escalaCriteria.add(or);
		// } else {
		// // apenas com data fim not null
		// escalaCriteria.add(dtFimNotNull);
		// }

		// /// Parâmetro da LOV
		if (StringUtils.isNotBlank((String) substitutoPesquisaLOV)) {

			String stParametro = (String) substitutoPesquisaLOV;
			Integer matricula = null;

			if (CoreUtil.isNumeroInteger(stParametro)) {
				matricula = Integer.valueOf(stParametro);
			}

			if (matricula != null) {
				criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
			} else {
				pessoaFisicaCriteria
						.add(Restrictions.ilike(RapPessoasFisicas.Fields.NOME.toString(), stParametro, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna um outro contrato, caso exista, do servidor informado no parâmetro,
	 * considerando que a pessoa física seja a mesma do servidor do parâmetro e
	 * o usuário do sistema seja diferente do servidor do parâmetro.
	 * 
	 * @param servidor
	 * @return Outro contrato do servidor
	 * @throws ApplicationBusinessException
	 */
	public RapServidores buscarOutroContrato(RapServidores servidor)
			throws ApplicationBusinessException {
		
		if (servidor == null || servidor.getId() == null 
				|| servidor.getUsuario() == null 
				|| servidor.getPessoaFisica() == null) {
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class,"servidor");
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		
		criteria.add(Restrictions.ne("servidor."+RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.ne("servidor."+RapServidores.Fields.CODIGO_VINCULO.toString(), servidor.getId().getVinCodigo()));
		criteria.add(Restrictions.isNotNull("servidor."+RapServidores.Fields.USUARIO.toString()));
		criteria.add(Restrictions.ne("servidor."+RapServidores.Fields.USUARIO.toString(), servidor.getUsuario()));
		criteria.add(Restrictions.eq("pessoaFisica."+RapPessoasFisicas.Fields.CODIGO.toString(), servidor.getPessoaFisica().getCodigo()));

		List<RapServidores> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		} 

		return null;
	}	
	
	public RapServidores obterServidorAtivoPorCpf(Long cpf){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		criteria.add(Restrictions.eq("PES." + RapPessoasFisicas.Fields.CPF.toString(), cpf));
		
		criteria.add(Restrictions.eq("SER."+RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		criteria.add(Restrictions.or(
				Restrictions.isNull("SER."+RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.lt("SER."+RapServidores.Fields.DATA_FIM_VINCULO.toString(),new Date())));
		
		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * @dbtables RapServidores select
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	public List<RapServidores> pesquisarRapServidores(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer matricula, Integer vinculo, String nome) {
		return executeCriteria(createPesquisaCriteria(matricula, vinculo, nome, false), firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * @dbtables RapServidores select
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	public Long pesquisarRapServidoresCount(Integer matricula, Integer vinculo, String nome) {		
		return (Long) executeCriteriaCount(createPesquisaCriteria(matricula, vinculo, nome, true));
	}

	
	private DetachedCriteria createPesquisaCriteria(Integer matricula, Integer vinculo, String nome, boolean count) {
		
		String sqlFragment ="(select count (*) from AGH.AGH_PROF_ESPECIALIDADES APE "				
				+ "where APE.SER_MATRICULA=MATRICULA and APE.SER_VIN_CODIGO=VIN_CODIGO) countProfEspecialidades";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		if (!count){
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("rap." + RapServidores.Fields.MATRICULA.toString()).as("matricula"))
					.add(Projections.property("rap." + RapServidores.Fields.CODIGO_VINCULO.toString()).as("vinCodigo"))
					.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()).as("nome"))
					.add(Projections.sqlProjection(sqlFragment,	new String [] {"countProfEspecialidades"}, new Type[] {new LongType()}))
				);
					
			criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresEspecialidadesVO.class));
		}			
		
		if (matricula != null) {
			criteria.add(Restrictions.eq("rap." + RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		if (vinculo != null) {
			criteria.add(Restrictions.eq("rap." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo.shortValue()));
		}		
		if(StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}		
		criteria.add(Restrictions.or(Restrictions.isNull("rap." + RapServidores.Fields.DATA_FIM_VINCULO.toString()),Restrictions.gt("rap." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		
		return criteria;
	}
	
	/**
	 * Obtém RapServidor por Vínculo ou Matrícula
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RapServidores obterRapServidorPorVinculoMatricula(Integer matricula,  Short vinCodigo) {
		
		CoreUtil.validaParametrosObrigatorios(matricula,vinCodigo);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		
		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método usado para alimentar a modal de servidores nas telas de centro de
	 * custo.
	 * @dbtables RapServidores select
	 * 
	 * @param param
	 * @return
	 */
	public List<RapServidores> pesquisarRapServidores(Object param) {
		String strParam = (String) param;

		DetachedCriteria criteria = obterCriteriaPesquisaRapServidor(strParam);
		return this.executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarRapServidoresCount(Object param) {
		return this.executeCriteriaCount(obterCriteriaPesquisaRapServidor((String) param));
	}
	
	/**
	 * Cria a pesquisa p/ alimentar a modal de servidores nas telas de centro de
	 * custo.
	 * 
	 * @param strParam
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaRapServidor(String strParam) {
		Integer intParam = null;
		Short shtParam = null;

		if (CoreUtil.isNumeroInteger(strParam)) {
			intParam = Integer.valueOf(strParam);
		}

		if (CoreUtil.isNumeroShort(strParam)) {
			shtParam = Short.valueOf(strParam);
		}

		final String pathNomePessoaFisica = RapServidores.Fields.PESSOA_FISICA
				.toString()
				+ "." + RapPessoasFisicas.Fields.NOME.toString();

		final String pathVinculo = RapServidores.Fields.VINCULO.toString()
				+ "." + RapVinculos.Fields.CODIGO.toString();

		final String pathVinculoDescricao = RapServidores.Fields.VINCULO
				.toString()
				+ "." + RapVinculos.Fields.DESCRICAO.toString();

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);

		criteria.createAlias(RapServidores.Fields.VINCULO.toString(),
				RapServidores.Fields.VINCULO.toString());

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());

		Criterion ilikeNomePessoaFisica = Restrictions.ilike(
				pathNomePessoaFisica, strParam, MatchMode.ANYWHERE);

		Criterion ilikeDescricaoVinculo = Restrictions.ilike(
				pathVinculoDescricao, strParam, MatchMode.ANYWHERE);

		Criterion eqMatricula = Restrictions.eq(RapServidores.Fields.MATRICULA
				.toString(), intParam);

		Criterion eqVinculo = Restrictions.eq(pathVinculo, shtParam);

		criteria.add(Restrictions
				.or(Restrictions.or(ilikeNomePessoaFisica,
						ilikeDescricaoVinculo), Restrictions.or(eqMatricula,
						eqVinculo)));

		return criteria;
	}

	public RapServidores obterServidor(RapServidores servidor) {
		DetachedCriteria cri = DetachedCriteria.forClass(RapServidores.class);
		cri.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
		cri.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), servidor.getId().getVinCodigo()));

		return (RapServidores) executeCriteriaUniqueResult(cri);
	}

	public List<RapServidores> pesquisarRapServidoresPorCodigoDescricao(
			Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		// Caso a string informada possa ser reconhecida como ID do RapServidor
		// (vinculo + matrícula), o mesmo deverá ser consultado pelo ID.
		if (StringUtils.isNotBlank(strPesquisa)) {
			String aux[] = strPesquisa.trim().split(" ");

			if (aux.length == 2 && CoreUtil.isNumeroShort(aux[0])
					&& CoreUtil.isNumeroInteger(aux[1])) {
				DetachedCriteria _criteria = DetachedCriteria
						.forClass(RapServidores.class);

				_criteria.add(Restrictions.eq(
						RapServidores.Fields.CODIGO_VINCULO.toString(),
						Short.valueOf(aux[0])));

				_criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA
						.toString(), Integer.valueOf(aux[1])));

				
				_criteria.add(Restrictions.or(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), 
								DominioSituacaoVinculo.A), Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), 
								DominioSituacaoVinculo.P)));

				List<RapServidores> list = executeCriteria(_criteria, 0, 100, null, false);

				if (list.size() > 0) {
					return list;
				}
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapServidores.class);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					RapServidores.Fields.NOME_PESSOA_FISICA.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO
				.toString(), DominioSituacaoVinculo.A));

		criteria.addOrder(Order.asc(RapServidores.Fields.NOME_PESSOA_FISICA
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	synchronized 

	@SuppressWarnings("unchecked")
	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao( Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		// Caso a string informada possa ser reconhecida como ID do RapServidor
		// (vinculo + matrícula), o mesmo deverá ser consultado pelo ID.
		if (StringUtils.isNotBlank(strPesquisa)) {
			String aux[] = strPesquisa.trim().split(" ");

			if (aux.length == 2 && CoreUtil.isNumeroShort(aux[0]) && CoreUtil.isNumeroInteger(aux[1])) {
				StringBuilder hql = new StringBuilder(400);
								
				hql.append(" select new br.gov.mec.aghu.vo.RapServidoresVO (")
				.append(" 	rs.id.vinCodigo, rs.id.matricula, pf.nome ")
				.append(" ) ")
				.append(" from RapServidores rs ")
				.append(" join rs.pessoaFisica as pf ")
				.append(" where ( rs.indSituacao = :indSituacaoA ")
				.append(" or ( rs.indSituacao = :indSituacaoP and rs.dtFimVinculo >= :dataAtual ) )")
				.append(" and rs.id.vinCodigo = :vinculo ")
				.append(" and rs.id.matricula = :matricula ");
				
				Query query = createHibernateQuery(hql.toString());
				query.setParameter("indSituacaoA", DominioSituacaoVinculo.A);
				query.setParameter("indSituacaoP", DominioSituacaoVinculo.P);
				Date dataAtual = new Date();
				query.setParameter("dataAtual", dataAtual);
				query.setParameter("vinculo", Short.valueOf(aux[0]));
				query.setParameter("matricula", Integer.valueOf(aux[1]));
				
				query.setFirstResult(0);
				query.setMaxResults(100);
				
				List<RapServidoresVO> list = query.list();
				
				if (list.size() > 0) {
					return list;
				}
			}
		}
		
		StringBuilder hql = new StringBuilder(400);
				
		hql.append(" select new br.gov.mec.aghu.vo.RapServidoresVO (");
		hql.append(" 	rs.id.vinCodigo, rs.id.matricula, pf.nome ");
		hql.append(" ) ");
		hql.append(" from RapServidores rs ");
		hql.append(" join rs.pessoaFisica as pf ");
		hql.append(" where ( rs.indSituacao = :indSituacaoA ");
		hql.append(" or ( rs.indSituacao = :indSituacaoP and rs.dtFimVinculo >= :dataAtual ) )");
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			hql.append(" 	and (lower(pf.nome) like lower('%' || :nome || '%'))");
		}
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("indSituacaoA", DominioSituacaoVinculo.A);
		query.setParameter("indSituacaoP", DominioSituacaoVinculo.P);
		Date dataAtual = new Date();
		query.setParameter("dataAtual", dataAtual);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("nome", strPesquisa);
		}
		
		query.setFirstResult(0);
		query.setMaxResults(100);
		
		return query.list();
	}

	/**
	 * @param param
	 * @param unfSeq
	 * @param seqp
	 * @return listaRapServidoresVO
	 */
	public List<RapServidoresVO> pesquisarRapServidoresPorUnidFuncESala(Object param, Short unfSeq, Short seqp) {
		if(unfSeq == null) {
			return null;
		}
		
		String aliasGae = "gae";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasSee = "see";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class, aliasGae);
		criteria.createAlias(aliasGae + separador + AelGradeAgendaExame.Fields.SERVIDOR.toString(), aliasSer);
		criteria.createAlias(aliasSer + separador + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.createAlias(aliasGae + separador + AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), aliasSee);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(aliasPes + separador + RapPessoasFisicas.Fields.NOME.toString()))
				.add(Projections.property(aliasSer + separador + RapServidores.Fields.MATRICULA.toString()))
				.add(Projections.property(aliasSer + separador + RapServidores.Fields.CODIGO_VINCULO.toString()))));
						
		criteria.add(Restrictions.eq(aliasSee + separador + AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), unfSeq));
		if(seqp != null) {
			criteria.add(Restrictions.eq(aliasSee + separador + AelSalasExecutorasExames.Fields.SEQP.toString(), seqp));
		}
		criteria.add(Restrictions.eq(aliasSer + separador + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		criteria.add(Restrictions.or(Restrictions.isNull(aliasSer + separador + RapServidores.Fields.DATA_FIM_VINCULO.toString()), Restrictions.gt(aliasSer + separador + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		
		String stParametro = (String) param;
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
			criteria.add(Restrictions.eq(aliasSer + separador + RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(aliasPes + separador + RapPessoasFisicas.Fields.NOME.toString(), stParametro, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(aliasPes + separador + RapPessoasFisicas.Fields.NOME.toString()));
		
		List<Object[]> listaArrayObject = executeCriteria(criteria);
		
		List<RapServidoresVO> listaRapServidoresVO = new ArrayList<RapServidoresVO>();
		
		for (Object[] arrayObject : listaArrayObject) {
			RapServidoresVO rapServidorVO = new RapServidoresVO();
			rapServidorVO.setNome((String) arrayObject[0]);
			rapServidorVO.setMatricula((Integer) arrayObject[1]);
			rapServidorVO.setVinculo((Short) arrayObject[2]);
			listaRapServidoresVO.add(rapServidorVO);
		}
		return listaRapServidoresVO;
	}

	public List<RapServidores> pesquisarServidorPorVinculo(Object servidor) {
		final DetachedCriteria criteria = criarCriteriaPesquisarServidorPorVinculo(servidor);
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES", JoinType.INNER_JOIN);
		return executeCriteria(criteria, 0, 100, null, false);		
	}

	public Long pesquisarServidorPorVinculoCount(Object servidor) {
		final DetachedCriteria criteria = criarCriteriaPesquisarServidorPorVinculo(servidor);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteriaPesquisarServidorPorVinculo(Object servidor) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		final String strPesquisa = (String) servidor;		
		if (StringUtils.isNotBlank(strPesquisa)) {				
			if (CoreUtil.isNumeroShort(StringUtils.trimToNull(servidor.toString()))) {
				final Short paramShort = Short.valueOf(StringUtils.trimToNull(servidor.toString()));
				criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), paramShort));
			}else{
				//Obriga que não retorne nada, pois codigo_vinculo not null
				criteria.add(Restrictions.isNull(RapServidores.Fields.CODIGO_VINCULO.toString()));
			}
		}
		return criteria;
	}

	public List<RapServidores> pesquisarServidorPorMatriculaNome(Object servidor) {
		final DetachedCriteria criteria = criarCriteriaPesquisarServidorPorMatriculaNome(servidor);	
		criteria.addOrder(Order.asc("PES."+RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);				 
	}
	
	public Long pesquisarServidorPorMatriculaNomeCount(Object servidor) {
		final DetachedCriteria criteria = criarCriteriaPesquisarServidorPorMatriculaNome(servidor);			
		return executeCriteriaCount(criteria);			 
	}
	
	private DetachedCriteria criarCriteriaPesquisarServidorPorMatriculaNome(Object servidor) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		final String strPesquisa = (String) servidor;		
		if (StringUtils.isNotBlank(strPesquisa)) {				
			if (CoreUtil.isNumeroInteger(StringUtils.trimToNull(servidor.toString()))) {
				final Integer paramInteger = Integer.valueOf(StringUtils.trimToNull(servidor.toString()));
				criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), paramInteger));
			} else {				
				criteria.add(Restrictions.ilike("PES."+RapPessoasFisicas.Fields.NOME.toString(),strPesquisa, MatchMode.ANYWHERE));					
			}
		}
		return criteria;
	}	
	
	/**
	 * Consulta para View V_RAP_SERV_CRM_AEL
	 * @param filtro, parametros
	 * @return
	 */
	public List<VRapServCrmAelVO> pesquisarViewRapServCrmAelVO(VRapServCrmAelVO filtro, String[] parametros) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF");
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQL");
		criteria.createAlias("TQL." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), "matricula")
				.add(Projections.property("SER." + RapServidores.Fields.CODIGO_VINCULO.toString()), "vinCodigo")
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "nome")
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), "nomeUsual")
				.add(Projections.property("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), "nroRegConselho"));
		
		criteria.add(Restrictions.or(Restrictions.isNull("SER." + RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt("SER." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())));
		criteria.add(Restrictions.isNotNull("QLF."+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.in("CPR." + RapConselhosProfissionais.Fields.SIGLA.toString(), parametros));
		
		if(filtro != null) {
			if(filtro.getMatricula() != null) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), filtro.getMatricula()));
			}
			if(filtro.getVinCodigo() != null) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.CODIGO_VINCULO.toString(), filtro.getVinCodigo()));
			}
			if(filtro.getNome() != null) {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), filtro.getNome()));
			}
			if(filtro.getNomeUsual() != null) {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString(), filtro.getNomeUsual()));
			}
			if(filtro.getNroRegConselho() != null) {
				criteria.add(Restrictions.ilike("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(), filtro.getNroRegConselho()));
			}
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(VRapServCrmAelVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RapServidores> pesquisarServidoresVinculados(Object servidor) throws BaseException {

		DetachedCriteria criteria = obterCriteriapesquisarServidoresVinculados(servidor);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	
	/**
	 * 
	 * @param objetoPesquisa
	 * @return
	 */
	private DetachedCriteria obterCriteriapesquisarServidoresVinculados(Object servidor) throws BaseException {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(RapServidores.class);

		String stParametro = (String) servidor;
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),	RapServidores.Fields.PESSOA_FISICA.toString());
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), RapServidores.Fields.VINCULO.toString());
		
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(),	stParametro, MatchMode.ANYWHERE));
		}
		
			
		criteria.add(Restrictions.or(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and( Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
								  Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))
								  ));
		
		
		return criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA.toString()+ "." + RapPessoasFisicas.Fields.NOME));
	}
	
	public List<ConselhoProfissionalServidorVO> pesquisarConselhoProfissionalRegConselhoNotNull (Integer matricula, Short vinCodigo, DominioSituacao dominioSituacaoConselho) {		
		DetachedCriteria criteria = getCriteriaObterDadosServidor();		
		
		getProjectionPesquisaConselhoProfissional(criteria);
		
		//Where
		addMatriculaVinculo(vinCodigo, matricula, criteria);
		
		criteria.add(Restrictions.isNotNull(RapPessoasFisicas.Fields.QUALIFICACOES.toString() + stringSeparator + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO));
		
		criteria.add(Restrictions.eq(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
				+ stringSeparator
				+ RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), dominioSituacaoConselho));		
		
		criteria.addOrder(Order.asc(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
				+ stringSeparator
				+ RapConselhosProfissionais.Fields.SIGLA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ConselhoProfissionalServidorVO.class));
		
		return super.executeCriteria(criteria);
	}
	
	private void getProjectionPesquisaConselhoProfissional(
			DetachedCriteria criteria) {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
						+ stringSeparator
						+ RapConselhosProfissionais.Fields.TITULO_FEMININO), 
						ConselhoProfissionalServidorVO.Fields.TITULO_FEMININO.toString()) // TITULO FEMININO				
				.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
						+ stringSeparator
						+ RapConselhosProfissionais.Fields.TITULO_MASCULINO), 
						ConselhoProfissionalServidorVO.Fields.TITULO_MASCULINO.toString()) // TITULO MASCULINO				
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() 
						+ stringSeparator
						+ RapPessoasFisicas.Fields.NOME.toString()), 
						ConselhoProfissionalServidorVO.Fields.NOME.toString()) // NOME
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()
						+ stringSeparator
						+ RapPessoasFisicas.Fields.CPF.toString()), 
						ConselhoProfissionalServidorVO.Fields.CPF.toString()) // CPF			
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()
						+ stringSeparator
						+ RapPessoasFisicas.Fields.SEXO.toString()), 
						ConselhoProfissionalServidorVO.Fields.SEXO.toString()) // SEXO				
				.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
						+ stringSeparator
						+ RapConselhosProfissionais.Fields.CODIGO.toString()), 
						ConselhoProfissionalServidorVO.Fields.CODIGO_CONSELHO.toString()) // CODIGO CONSELHO
				.add(Projections.property(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString()
						+ stringSeparator
						+ RapConselhosProfissionais.Fields.SIGLA.toString()), 
						ConselhoProfissionalServidorVO.Fields.SIGLA_CONSELHO.toString()) // SIGLA						
				.add(Projections.property(RapPessoasFisicas.Fields.QUALIFICACOES.toString()
						+ stringSeparator
						+ RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), 
						ConselhoProfissionalServidorVO.Fields.NUMERO_REG_CONSELHO.toString())); // NUMERO REGISTRO CONSELHO
	}

	public List<RapServidores> obterServidorPorCPF(Long cpf){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq("PES." + RapPessoasFisicas.Fields.CPF.toString(), cpf));

		return executeCriteria(criteria);
	}

    public RapServidores obterServidorComPessoaFisicaByUsuario(String login) {
        DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");

        criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),	RapServidores.Fields.PESSOA_FISICA.toString());
        criteria.createAlias(RapServidores.Fields.VINCULO.toString(), RapServidores.Fields.VINCULO.toString());

        criteria.add(Restrictions.eq(RapServidores.Fields.USUARIO.toString(), login).ignoreCase());
        RapServidores  servidor = (RapServidores) executeCriteriaUniqueResult(criteria);
        return servidor;
    }
	
	public Boolean existeQualificacoesUsuarioSemNumeroRegistroConselho(
			RapServidores servidorLogado,
			DominioTipoQualificacao tipoQualificacao) {
		StringBuffer hql = new StringBuffer(310);
		hql.append(" select count(*) ");
		hql.append(" from ").append(RapServidores.class.getSimpleName()).append(" ser ");
		hql.append(" 	join ser.").append(RapServidores.Fields.PESSOA_FISICA).append(" pes ");
		hql.append(" where ser = :servidorLogado ");
		hql.append("	and ( exists ( ");
		hql.append("		select 1 ");
		hql.append("		from ").append(RapQualificacao.class.getSimpleName()).append(" qua ");
		hql.append("			join qua.").append(RapQualificacao.Fields.TIPO_QUALIFICACAO).append(" tql ");
		hql.append("		where qua.").append(RapQualificacao.Fields.PESSOA_FISICA_ALIAS).append(" = pes ");
		hql.append("			and qua.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO).append(" is not null ");
		hql.append("			and tql.").append(RapTipoQualificacao.Fields.TIPO_QUALIFICACAO).append(" = :tipoQualificacao ");
		hql.append("  ) ) ");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("servidorLogado", servidorLogado);
		query.setParameter("tipoQualificacao", tipoQualificacao);
		
		return ((Long) query.uniqueResult()) > 0;
	}

	public String buscarNomeEquipePorPucSerMatricula(Integer serMatricula) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcProfAtuaUnidCirgs.class, "pau");
		criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), serMatricula));
		List<MbcProfAtuaUnidCirgs> list = executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			MbcProfAtuaUnidCirgs un = list.get(0);
			String nome = un.getRapServidores().getPessoaFisica().getNomeUsual() != null ? un.getRapServidores().getPessoaFisica().getNomeUsual() : un.getRapServidores().getPessoaFisica().getNome();
			return nome;
		}
		return "";
	}
	
	/**
	 * Pesquisa compradores ativos.
	 * 
	 * @param filter Filtro por matricula ou nome usual. 
	 */
	public List<RapServidores> pesquisarCompradoresAtivos(Object filter, 
			Integer first, Integer max, 
			String order, Boolean asc) {
		final String RS = "RS", PF = "PF", OC = "OC";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, RS);
		
		criteria.createAlias(
				path(RS, RapServidores.Fields.PESSOA_FISICA), 
				PF);
		
		criteria.createAlias(
				path(RS, RapServidores.Fields.OCUPACAO_CARGO),
				OC);
		
		restrictMatriculaNome(criteria, filter, PF);
		restrictAtivos(criteria);
		restrictCompradores(criteria, OC);
		
		return executeCriteria(criteria, first, max, order, asc);
	}

	/**
	 * Restringe servidores por matricula ou nome usual.
	 * 
	 * @param criteria
	 * @param filter
	 * @param pfAlias 
	 */
	private void restrictMatriculaNome(DetachedCriteria criteria,
			Object filter, String pfAlias) {
		String filterStr = (String) filter;
		
		if (StringUtils.isNotBlank(filterStr)) {
			if (CoreUtil.isNumeroInteger(filterStr)) {
				criteria.add(Restrictions.eq(
						path(criteria.getAlias(), RapServidores.Fields.MATRICULA), 
						Integer.valueOf(filterStr)));
			} else {				
				criteria.add(Restrictions.ilike(
						path(pfAlias, RapPessoasFisicas.Fields.NOME),
						filterStr, MatchMode.ANYWHERE));
			}
		}
	}

	/**
	 * Restringe servidores com perfil de comprador.
	 * 
	 * @param criteria Consulta
	 * @param ocupacaoCargoAlias 
	 */
	private void restrictCompradores(DetachedCriteria criteria, String ocupacaoCargoAlias) {
		// Aliases
		final String P = "P", C = "C", O = "O";
		
		// Subquery de Consulta a Parâmetro AGHU
		DetachedCriteria paramSubquery = DetachedCriteria.forClass(AghParametros.class, P);
		
		paramSubquery.setProjection(Projections.id());
		
		paramSubquery.add(Restrictions.eq(
				path(P, AghParametros.Fields.NOME),
				AghuParametrosEnum.P_OCUPACAO_COMPRADOR.toString()));
		
		paramSubquery.add(Restrictions.eqProperty(
				path(P, AghParametros.Fields.VLR_NUMERICO), 
				path(ocupacaoCargoAlias, RapOcupacaoCargo.Fields.CODIGO)));
		
		// Subquery de Consulta a Caracteristicas do Usuário		
		DetachedCriteria caractSubquery = DetachedCriteria.forClass(
				ScoCaracteristicaUsuarioCentroCusto.class, C);
		
		caractSubquery.setProjection(Projections.id());
		
		caractSubquery.createAlias(
				path(C, ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA), O);
		
		caractSubquery.add(Restrictions.eqProperty(
				path(C, 
						ScoCaracteristicaUsuarioCentroCusto.Fields.SERVIDOR,
						RapServidores.Fields.ID),
				path(criteria.getAlias(), RapServidores.Fields.ID)));
		
		caractSubquery.add(Restrictions.eq(
				path(O, ScoCaracteristica.Fields.CARACTERISTICA), "COMPRADOR"));
		
		// Restringe
		criteria.add(Restrictions.or(
				Subqueries.exists(paramSubquery),
				Subqueries.exists(caractSubquery)));
	}

	/**
	 * Restringe servidores ativos.
	 * 
	 * @param criteria Consulta
	 */
	private void restrictAtivos(DetachedCriteria criteria) {
		Criterion ativo = Restrictions.eq(
				path(criteria.getAlias(), RapServidores.Fields.IND_SITUACAO),
				DominioSituacaoVinculo.A);
		
		Criterion tempAtivo = Restrictions.and(
				Restrictions.eq(
					path(criteria.getAlias(), RapServidores.Fields.IND_SITUACAO),
					DominioSituacaoVinculo.P),
				Restrictions.ge(
					path(criteria.getAlias(), RapServidores.Fields.DATA_FIM_VINCULO),
					DateUtil.obterDataComHoraInical(new Date())));
		
		criteria.add(Restrictions.or(ativo, tempAtivo));
	}

	/**
	 * Obtem path para campos e relacionamentos dos POJO's
	 * 
	 * @param parts Partes do Path
	 * @return Path
	 */
	private String path(Object... parts) {
		StringBuffer path = new StringBuffer();
		
		for (int i = 0; i < parts.length; i ++) {
			path.append(parts[i]);
			
			if (i < parts.length - 1) {
				path.append('.');
			}
		}
		
		return path.toString();
	}
	
	
	public List<RapServidores> listarMbcConselho(Object parametro, Short unfSeq) {
		String stParametro = (String) parametro;
		Integer matricula = null;
		
		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}

		StringBuilder hql = new StringBuilder(300);
		hql.append("select rap ");
		
		hql.append(" from ");
		hql.append(VRapServidorConselho.class.getSimpleName()).append(" vcs, ");
		hql.append(MbcProfAtuaUnidCirgs.class.getSimpleName()).append(" puc ");
		hql.append(" inner join puc.").append(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()).append(" rap ");
		hql.append(" inner join fetch rap.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" fis ");		
		
		hql.append(" where puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString())
					.append(" = vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString())
		.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())
				.append(" = vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString())
		.append(" and  vcs.").append(VRapServidorConselho.Fields.SIGLA.toString())
					.append(" IN ('CRO','CREMERS')")
		.append(" and  puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()).append(" =:situacao")
		.append(" and  puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()).append(" =:funcaoProfissional ")
		.append(" and  puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()).append(" =:unfSeq");
		
		if (StringUtils.isNotBlank(stParametro) && !CoreUtil.isNumeroInteger(stParametro)) {
			hql.append(" and upper(vcs.").append(VRapServidorConselho.Fields.NOME.toString()).append(") like '%"+stParametro.toUpperCase()+"%'");
		}else if(matricula != null) {
			hql.append(" AND vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" =:matricula");
		}
		
		hql.append(" ORDER BY  vcs.").append(VRapServidorConselho.Fields.NOME.toString());
		
		Query query = this.createHibernateQuery(hql.toString());
		
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("funcaoProfissional", DominioFuncaoProfissional.MPF);

		if(matricula != null){
			query.setParameter("matricula", matricula);
		}

		@SuppressWarnings("rawtypes")
		List list = query.list();
		
		return (List<RapServidores>) list;
	}
	
	public Integer listarMbcConselhoCount(Object parametro, Short unfSeq) {
		return this.listarMbcConselho(parametro, unfSeq).size();
	}
	
	/**
	 * Implementa o cursor <code>c_get_cbo_exame</code>
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param codSit
	 * @return
	 * 
	 */
	public RapServidores buscaRapServidor(final Integer iseSoeSeq, final Short iseSeqp, final String codSit) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.createAlias(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), codSit));

		final List<AelExtratoItemSolicitacao> result = executeCriteria(criteria, 0, 1, AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), false);

		if (result != null && !result.isEmpty()) {
			return result.get(0).getServidorEhResponsabilide();
		}
		return null;
	}
	
	public Long buscaNroFuncionariosPorCentroCusto(FccCentroCustos centroCustos){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		//criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNotNull(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString()), Restrictions.eq(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), centroCustos)), 
		//		Restrictions.and(Restrictions.isNotNull(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()), Restrictions.eq(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), centroCustos))));
	
		criteria.add(Restrictions.sqlRestriction("coalesce({alias}.cct_codigo_atua, {alias}.cct_codigo) = ? " , centroCustos.getCodigo(), IntegerType.INSTANCE));
		
		return this.executeCriteriaCount(criteria);
	}
	
	public Long buscaNroLoginsPorCentroCusto(FccCentroCustos centroCustos){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotNull(RapServidores.Fields.USUARIO.toString()));
		
		criteria.add(Restrictions.sqlRestriction("coalesce({alias}.cct_codigo_atua, {alias}.cct_codigo) = ? " , centroCustos.getCodigo(), IntegerType.INSTANCE));
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna a qtde de rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select count
	 * 
	 * @param responsavel
	 * @return qtde
	 */
	public Long pesquisarResponsaveisCount(Object responsavel) {
		return executeCriteriaCount(obterCriteriaPesquisarResponsaveis(responsavel));
	}

	private DetachedCriteria obterCriteriaPesquisarResponsaveis(Object responsavel) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		String stParametro = (String) responsavel;
		Integer matricula = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			matricula = Integer.valueOf(stParametro);
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), RapServidores.Fields.PESSOA_FISICA.toString());

		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		} else {
			criteria.add(Restrictions.ilike(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME,
					stParametro, MatchMode.ANYWHERE));
		}

		// (ind_situacao = 'A' or (ind_situacao = 'P' and dt_fim_vinculo >= sysdate)));
		criteria.add(Restrictions.or(
				Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		return criteria;
	}
	
	public Long pesquisarServidoresPorTipoInformacaoCount(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao){
		final DetachedCriteria criteria = obterCriteriaPesquisarServidoresPorTipoInformacao(filtro, situacao, tipoInformacao);
		return executeCriteriaCount(criteria);
	}

	public List<RapServidores> pesquisarServidoresPorTipoInformacao(final String filtro, final DominioSituacaoVinculo situacao, final String tipoInformacao){

		final DetachedCriteria criteria = obterCriteriaPesquisarServidoresPorTipoInformacao(filtro, situacao, tipoInformacao);

		criteria.addOrder(Order.asc("PES."+RapPessoasFisicas.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarServidoresPorTipoInformacao(final String filtro, final DominioSituacaoVinculo situacao,
			final String tipoInformacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class,"SERV");

		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("PES."+RapPessoasFisicas.Fields.TIPO_INFORMACAO.toString(), "PES_INF");
		criteria.createAlias("PES_INF."+RapPessoaTipoInformacoes.Fields.TIPO_INFORMACAO.toString(), "INF");

		criteria.add(Restrictions.eq("INF."+RapTipoInformacoes.Fields.DESCRICAO.toString(), tipoInformacao));
		criteria.add(Restrictions.eq("SERV."+RapServidores.Fields.IND_SITUACAO.toString(), situacao));

		if (StringUtils.isNotBlank(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.eq("SERV."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retorna dados de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @param maxResults
	 * @return
	 */
	public List<RapServidores> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome, Integer maxResults) {

		DetachedCriteria criteria = this.montarCriteriaPesquisaServidoresAtivos(vinCodigo, matricula, nome);

		criteria.addOrder(Order.asc(RapServidores.Fields.NOME_PESSOA_FISICA.toString()));

		if (maxResults != null) {
			executeCriteria(criteria, 0, maxResults, null, true);
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna count de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @return
	 */
	public Long pesquisarServidoresAtivosCount(Short vinCodigo, Integer matricula, String nome) {
		
		DetachedCriteria criteria = this.montarCriteriaPesquisaServidoresAtivos(vinCodigo, matricula, nome);
				
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta a criteria de dados de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @return
	 */
	public DetachedCriteria montarCriteriaPesquisaServidoresAtivos(Short vinCodigo, Integer matricula, String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),	RapServidores.Fields.PESSOA_FISICA.toString());
		
		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		
		if (matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		if (vinCodigo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));
		}				
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), nome, MatchMode.ANYWHERE));
		}
				
		return criteria;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 */
	public List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome) {
		return this.pesquisarMedicosEmergencia(vinCodigos, matriculas, nome, null, null, null, true);
	}
	
	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<RapServidores> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = this.montarCriteriaPesquisaMedicosEmergencia(vinCodigos, matriculas, nome);
		
		criteria.addOrder(Order.asc(RapServidores.Fields.NOME_PESSOA_FISICA.toString()));
				
		if(firstResult != null && maxResults != null){
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna count de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 */
	public Long pesquisarMedicosEmergenciaCount(List<Short> vinCodigos, List<Integer> matriculas, String nome) {
		
		DetachedCriteria criteria = this.montarCriteriaPesquisaMedicosEmergencia(vinCodigos, matriculas, nome);
				
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta criteria de dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisaMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),	RapServidores.Fields.PESSOA_FISICA.toString());
		
		if(vinCodigos != null && !vinCodigos.isEmpty()){
			
			if(matriculas == null || matriculas.size() != vinCodigos.size()){
				throw new IllegalStateException("Quantidade de matrículas diferente da quantidade de vínculos");
			}
			
			Disjunction disjunction = Restrictions.disjunction();
			for (int i = 0; i < vinCodigos.size(); i++) {
				Short vinculo = vinCodigos.get(i);
				Integer matricula = matriculas.get(i);
				disjunction.add(
						Restrictions.and(
								Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula), 
								Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), vinculo)));
			}
			criteria.add(disjunction);
		}
		
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), nome, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	public List<RapServidores> listarUsuariosNotificaveis(Integer codigoCentroCusto) {
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		List<SigComunicacaoEventos> usuariosEspecificos = this.buscarUsuarioEspecifico(codigoCentroCusto);
		for (SigComunicacaoEventos comunicacaoEventos : usuariosEspecificos) {
			servidores.add(comunicacaoEventos.getServidorCadastro());
		}
		
		return servidores;
	}

	
	public Boolean verificarServidorHUCadastradoPorCpf(Long numCpf){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq("PES." + RapPessoasFisicas.Fields.CPF.toString(), numCpf));

		return executeCriteriaExists(criteria);
	}
	
	public Boolean verificarServidorHUCadastradoPorRg(String numRg){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq("PES." + RapPessoasFisicas.Fields.NR_IDENTIDADE.toString(), numRg));

		return executeCriteriaExists(criteria);
	}

	private List<SigComunicacaoEventos> buscarUsuarioEspecifico(Integer codigoCentroCusto) {
		DetachedCriteria criteria = this.criarCriteriaBuscaUsuario();
		
		if (codigoCentroCusto != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(),
					codigoCentroCusto));
		}
		return this.executeCriteria(criteria);
	}
		
	private DetachedCriteria criarCriteriaBuscaUsuario() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigComunicacaoEventos.class);
		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas){
        DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
        criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
        criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
        criteria.add(Restrictions.in(VRapServidorConselho.Fields.CPR_SIGLA.toString(), siglas));
        criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
          
        return executeCriteriaExists(criteria); 
    }

	public VRapServidorConselho usuarioRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas){
        DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
        criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
        criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
        criteria.add(Restrictions.in(VRapServidorConselho.Fields.CPR_SIGLA.toString(), siglas));
        criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
          
        return (VRapServidorConselho)executeCriteriaUniqueResult(criteria); 
    }

	public RapServidoresVO pesquisarProfissionalPorServidor(RapServidores servidor){
		StringBuffer sql = new StringBuffer(1200);
		montaConsultaProfissionaisParte3(sql);
		if(servidor != null && servidor.getId().getMatricula() != null){
			sql.append(" and ser.matricula=").append(servidor.getId().getMatricula());
		}
		if(servidor != null && servidor.getId().getVinCodigo() != null){
			sql.append(" and ser.vin_codigo=").append(servidor.getId().getVinCodigo());
		}
		SQLQuery query = createSQLQuery(sql.toString());
		query.addScalar("matricula", IntegerType.INSTANCE);
		query.addScalar("vinculo", ShortType.INSTANCE);
		query.addScalar("nome", StringType.INSTANCE);
		query.addScalar("nomeProfissional", StringType.INSTANCE);
		query.addScalar("siglaConselho", StringType.INSTANCE);
		query.addScalar("nroRegConselho", StringType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		return (RapServidoresVO) query.uniqueResult();
	}
	
	public Long pesquisarProfissionaisPorVinculoMatriculaNomeCount(String parametro, Integer count) {
		List<RapServidoresVO> servidores =  pesquisarProfissionaisPorVinculoMatriculaNome(parametro, count);
		return (long) servidores.size();
	}
	
	@SuppressWarnings("unchecked")
	public List<RapServidoresVO> pesquisarProfissionaisPorVinculoMatriculaNome(String parametro, Integer count){
		StringBuffer sql = new StringBuffer(1200);
		montaConsultaProfissionaisParte2(sql, parametro);
		sql.append(" order by");
		sql.append(" pes.nome asc,");
		sql.append(" ser.vin_codigo asc,");
		sql.append(" ser.matricula asc");
		SQLQuery query = createSQLQuery(sql.toString());
		query.addScalar("matricula", IntegerType.INSTANCE);
		query.addScalar("vinculo", ShortType.INSTANCE);
		query.addScalar("nome", StringType.INSTANCE);
		query.addScalar("nomeProfissional", StringType.INSTANCE);
		query.addScalar("siglaConselho", StringType.INSTANCE);
		query.addScalar("nroRegConselho", StringType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		if(count != null){
			query.setMaxResults(count);
		}
		return query.list();
	}

	private void montaConsultaProfissionaisParte2(StringBuffer sql, String parametro) {
		Short vinculo = null;
		Integer matricula = null;
		montaConsultaProfissionaisParte3(sql);
		if (parametro != null && StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				Integer valor = Integer.valueOf(parametro);
				if (valor <= Short.MAX_VALUE) {
					vinculo = Short.valueOf(parametro);
				}
				matricula = valor;
			} else {
				sql.append(" and pes.nome like '%").append(parametro.toUpperCase()).append("%'");
			}
		}
		if (vinculo != null || matricula != null) {
			sql.append(" and (");
			sql.append(" ser.matricula=").append(matricula);
			sql.append(" or ser.vin_codigo=").append(vinculo);
			sql.append(" )");
		}
	}

	private void montaConsultaProfissionaisParte3(StringBuffer sql) {
		sql.append(" select distinct ser.matricula as matricula, ser.vin_codigo as vinculo, pes.nome as nome, pes.nome_usual as nomeProfissional,");
		sql.append(" cpr.sigla as siglaConselho, qlf.nro_reg_conselho as nroRegConselho");
		sql.append(" from casca.csc_perfil pfl, casca.csc_perfis_usuarios pfu, casca.csc_usuario usr, casca.csc_perfis_permissoes pfp,");
		sql.append(" casca.csc_permissao per, agh.rap_servidores ser, agh.rap_pessoas_fisicas pes, agh.rap_qualificacoes qlf,");
		sql.append(" agh.rap_tipos_qualificacao tql, agh.rap_conselhos_profissionais cpr");
		sql.append(" where pes.codigo=ser.pes_codigo");
		sql.append(" and qlf.pes_codigo=pes.codigo");
		sql.append(" and tql.codigo=qlf.tql_codigo");
		sql.append(" and cpr.codigo=tql.cpr_codigo");
		sql.append(" and usr.id=pfu.id_usuario");
		sql.append(" and pfl.id=pfu.id_perfil");
		sql.append(" and pfl.id=pfp.id_perfil");
		sql.append(" and per.id=pfp.id_permisao");
		sql.append(" and ser.usuario=usr.login");
		sql.append(" and qlf.nro_reg_conselho is not null");
		if(isOracle()){
			sql.append(" and usr.ativo=1");
		}else{
			sql.append(" and usr.ativo=true");
		}
		sql.append(" and pfl.situacao='A'");
		sql.append(" and per.ativo='A'");
		sql.append(" and per.nome='confirmarPrescricaoMedica'");
		sql.append(" and (");
		sql.append(" ser.dt_fim_vinculo is null or");
		if(isOracle()){
			sql.append(" trunc(ser.dt_fim_vinculo)>=trunc(SYSDATE)");
		}else{
			sql.append(" date(ser.dt_fim_vinculo)>=date(now())");
		}
		sql.append(" )");
	}
	
	public List<RapServidores> pesquisarServidorPorVinculoMatriculaNome(Object servidor){

		

		DetachedCriteria criteria = obterCriteriaServidorPorVinculoMatriculaNome(servidor);

		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, false);

	}

	public Long pesquisarServidorPorVinculoMatriculaNomeCount(Object servidor){
		DetachedCriteria criteria = obterCriteriaServidorPorVinculoMatriculaNome(servidor);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaServidorPorVinculoMatriculaNome(Object servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		String stParametro = (String) servidor;
		Integer matricula = null;
		Short vinculo = null;
		if (stParametro != null  && stParametro.contains(" ")) {
			String[] parametros = stParametro.split(" ");
			
			if (CoreUtil.isNumeroInteger(parametros[1])) {
				matricula = Integer.valueOf(parametros[1]);
			}
			if (CoreUtil.isNumeroShort(parametros[0])) {
				vinculo = Short.valueOf(parametros[0]);
			}
		}

		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", DetachedCriteria.INNER_JOIN);

		if (matricula != null && vinculo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
			criteria.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), vinculo));
		} else if (stParametro != null && !stParametro.isEmpty()) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), stParametro.toUpperCase(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		return criteria;
	}
	
	public List<RapServidores> pesquisarServidoresParaSuggestion(Object objPesquisa, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaParaSuggestionServidor(objPesquisa);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarServidoresParaSuggestionCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaParaSuggestionServidor(objPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaParaSuggestionServidor(Object objPesquisa) {
		DetachedCriteria criteria = criarDetachedCriteria();
		String strPesquisa = (String) objPesquisa;
		if(isPesquisaPorNome(strPesquisa)){
			criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
			criteria.add(Restrictions.ilike("PES."+RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}else{
			String[] splittedString = strPesquisa.split(" ");
			criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), Short.parseShort(splittedString[0])));
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), Integer.parseInt(splittedString[1])));
		}
		criteria.add(Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		return criteria;
	}

	private boolean isPesquisaPorNome(String strPesquisa) {
		String[] splittedString = strPesquisa.split(" ");
		if(splittedString.length == 2 && CoreUtil.isNumeroShort(splittedString[0]) && CoreUtil.isNumeroInteger((splittedString[1]))){  
			return false;
		}
		return true;
	}
	
	
	public RapServidores obterHintAfaMensCalculoNpt(AfaMensCalculoNpt item){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RSE");
		criteria.createAlias("RSE."+RapServidores.Fields.PESSOA_FISICA.toString(), "RPF");
		criteria.add(Restrictions.eq("RSE."+RapServidores.Fields.CODIGO_VINCULO.toString(), item.getSerVinCodigo()));
		criteria.add(Restrictions.eq("RSE."+RapServidores.Fields.MATRICULA.toString(), item.getSerMatricula()));
				
		return (RapServidores) executeCriteriaUniqueResult(criteria);
	}

	public Boolean verificarServidorExiste(Short codigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		if(matricula != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		if(codigo != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), codigo));
		}
		return executeCriteriaExists(criteria);
	}

	public boolean verificarProfissionalPossuiCBOAnestesista(Short vinCodigo, Integer matricula, 
			String[] siglasConselhos, String[] codigosTipoInformacoes) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.LEFT_OUTER_JOIN); 
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQL", JoinType.INNER_JOIN);
		criteria.createAlias("TQL." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.TIPO_INFORMACAO.toString(), "RPI", JoinType.RIGHT_OUTER_JOIN);
		criteria.createAlias("RPI." + RapPessoaTipoInformacoes.Fields.FAT_COBS.toString(), "CBO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RPI." + RapPessoaTipoInformacoes.Fields.TIPO_INFORMACAO.toString(), "RTP", JoinType.LEFT_OUTER_JOIN);

		if(vinCodigo != null) {
			criteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));
		}
		if(matricula != null) {
			criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), matricula));
		}
		if(siglasConselhos != null) {
			criteria.add(Restrictions.in("CPR." + RapConselhosProfissionais.Fields.SIGLA.toString(), siglasConselhos));
		}		
		if(codigosTipoInformacoes != null) {
			criteria.add(Restrictions.in("RPI." + RapPessoaTipoInformacoes.Fields.VALOR.toString(), codigosTipoInformacoes));
		}		
		return executeCriteriaExists(criteria);
	}
	
	//#5550 C3
	public List<RapServidoresVO> pesquisarGestorPorNomeOuMatricula(String parametro){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RSE");
	
		criteria.createAlias("RSE."+RapServidores.Fields.PESSOA_FISICA.toString(), "RPF");
		
		
		if(parametro != null && !parametro.equals("") && StringUtils.isNumeric(parametro)){
			criteria.add(Restrictions.eq("RSE."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro)));
		}else if(parametro != null && !parametro.equals("")){
			criteria.add(Restrictions.ilike("RPF."+RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc("RPF."+RapPessoasFisicas.Fields.NOME.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RSE."+RapServidores.Fields.VIN_CODIGO.toString()), RapServidoresVO.Fields.VINCULO.toString())
				.add(Projections.property("RSE."+RapServidores.Fields.MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
				.add(Projections.property("RPF."+RapPessoasFisicas.Fields.NOME.toString()), RapServidoresVO.Fields.NOME.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		return  executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarGestorPorNomeOuMatriculaCount(String parametro) {
		return Long.valueOf(pesquisarGestorPorNomeOuMatricula(parametro).size());
	}

	/**
	 * Pesquisas do 8990 
	 */
	public List<RapServidoresVO> pesquisarFuncionarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			RapServidores filtro) {
		DetachedCriteria criteria = montarQueryFuncionarios(filtro);
		
		if(StringUtils.isBlank(orderProperty)){
			criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString())).addOrder(
					Order.asc("vin." + RapVinculos.Fields.CODIGO.toString())).addOrder(
							Order.asc("vin." + RapVinculos.Fields.DESCRICAO.toString())).addOrder(
									Order.asc("cc_l." + FccCentroCustos.Fields.CODIGO.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);		
	}

	public Long pesquisarFuncionariosCount(RapServidores filtro) {
		DetachedCriteria criteria = montarQueryFuncionarios(filtro);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarQueryFuncionarios(RapServidores filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.setProjection(obterProjectionsPesquisarServidorRamais());
		
        criteria.setResultTransformer(new AliasToBeanResultTransformer(RapServidoresVO.class));
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);
		criteria.createAlias(RapServidores.Fields.VINCULO.toString(), "vin", JoinType.INNER_JOIN);
		criteria.createAlias(RapServidores.Fields.OCUPACAO_CARGO.toString(), "oc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "cc_l", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "cc_a", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(RapServidores.Fields.RAMAL_TELEFONICO.toString(), "ram", JoinType.LEFT_OUTER_JOIN);
		
		inserirFiltrosParteI(criteria, filtro);
		inserirFiltrosParteII(criteria, filtro);
		
		return criteria;
	}	
	
	private ProjectionList obterProjectionsPesquisarServidorRamais() {
		return Projections.projectionList()
				.add(Projections.property(RapServidores.Fields.MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
				.add(Projections.property("pes" + PONTO + RapPessoasFisicas.Fields.NOME.toString()), RapServidoresVO.Fields.NOME.toString())
				.add(Projections.property("oc" + PONTO + RapOcupacaoCargo.Fields.DESCRICAO.toString()), RapServidoresVO.Fields.OCUPACAO.toString())				
				.add(Projections.property(RapServidores.Fields.DATA_INICIO_VINCULO.toString()), RapServidoresVO.Fields.DATA_INICIO_VINCULO.toString())
				.add(Projections.property(RapServidores.Fields.DATA_FIM_VINCULO.toString()), RapServidoresVO.Fields.DATA_FIM_VINCULO.toString())				
				.add(Projections.property(RapServidores.Fields.IND_SITUACAO.toString()), RapServidoresVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(RapServidores.Fields.NRO_RAMAL.toString()), RapServidoresVO.Fields.RAMAL.toString())				
				.add(Projections.property("cc_l" + PONTO + FccCentroCustos.Fields.CODIGO.toString()), RapServidoresVO.Fields.CODIGO_CENTRO_CUSTO_LOT.toString())
				.add(Projections.property("cc_l" + PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), RapServidoresVO.Fields.DESCRICAO_CENTRO_CUSTO_LOT.toString())				
				.add(Projections.property("cc_a" + PONTO + FccCentroCustos.Fields.CODIGO.toString()), RapServidoresVO.Fields.CODIGO_CENTRO_CUSTO_ATU.toString())
				.add(Projections.property("cc_a" + PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), RapServidoresVO.Fields.DESCRICAO_CENTRO_CUSTO_ATU.toString())
				.add(Projections.property(RapServidores.Fields.CODIGO_VINCULO.toString()), RapServidoresVO.Fields.VINCULO.toString())
				.add(Projections.property("vin" + PONTO + RapVinculos.Fields.DESCRICAO.toString()), RapServidoresVO.Fields.DESCRICAO_VINCULO.toString())
				.add(Projections.property(RapServidores.Fields.CODSTARH.toString()), RapServidoresVO.Fields.COD_STARH.toString());
	}

	
	private void inserirFiltrosParteI(DetachedCriteria criteria, RapServidores filtro) {
		if (filtro.getId().getMatricula() != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), filtro.getId().getMatricula()));
		}
		
		if (StringUtils.isNotBlank(filtro.getPessoaFisica().getNome())) {
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), this.replacePercente(filtro.getPessoaFisica().getNome()), MatchMode.ANYWHERE));
		}

		if(filtro.getRamalTelefonico() != null && filtro.getRamalTelefonico().getNumeroRamal() != null){
			criteria.add(Restrictions.eq(RapServidores.Fields.NRO_RAMAL.toString(), filtro.getRamalTelefonico().getNumeroRamal()));
		}
		
		if(filtro.getDtInicioVinculo() != null){	
			Date dataInicioPeriodo = DateUtil.obterDataInicioCompetencia(filtro.getDtInicioVinculo());
			Date dataFimPeriodo = DateUtil.obterDataFimCompetencia(filtro.getDtInicioVinculo());
			
			criteria.add(Restrictions.between(RapServidores.Fields.DATA_INICIO_VINCULO.toString(), dataInicioPeriodo, dataFimPeriodo));
		}		

	}
	
	private void inserirFiltrosParteII(DetachedCriteria criteria, RapServidores filtro){
		if(filtro.getCentroCustoLotacao() != null && filtro.getCentroCustoLotacao().getCodigo() != null){
			criteria.add(Restrictions.eq("cc_l." + FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustoLotacao().getCodigo()));
		}
		
		if(filtro.getCentroCustoAtuacao() != null && filtro.getCentroCustoAtuacao().getCodigo() != null){
			criteria.add(Restrictions.eq("cc_a." + FccCentroCustos.Fields.CODIGO.toString(), filtro.getCentroCustoAtuacao().getCodigo()));
		}
		
		if(filtro.getVinculo() != null && filtro.getVinculo().getCodigo() != null){
			criteria.add(Restrictions.eq("vin." + RapVinculos.Fields.CODIGO.toString(), filtro.getVinculo().getCodigo()));
		}
		
		if(filtro.getOcupacaoCargo() != null && filtro.getOcupacaoCargo().getCodigo() != null){
			criteria.add(Restrictions.eq("oc." + RapOcupacaoCargo.Fields.CODIGO.toString(), filtro.getOcupacaoCargo().getCodigo()));
		}
	}
	
	private String replacePercente(String descricao) {        
	       return descricao.replace("_", "\\_").replace("%", "\\%");
	}
	
	public List<RapServidores> consultarRamalChefia1(Integer codigoCC, boolean lotacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		
		if (codigoCC != null) {
			criteria.createAlias(RapServidores.Fields.RAMAL_TELEFONICO.toString(), "rm", JoinType.INNER_JOIN);
			criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);
			criteria.createAlias(RapServidores.Fields.VINCULO.toString(), "vin", JoinType.INNER_JOIN);
			criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "cc_l", JoinType.INNER_JOIN);
			criteria.createAlias(RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(), "cc_a", JoinType.INNER_JOIN);
			
			if(lotacao){
				criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_CENTRO_CUSTO_LOTACAO.toString(), codigoCC));
			}else{
				criteria.add(Restrictions.eq(RapServidores.Fields.CODIGO_CENTRO_CUSTO_ATUACAO.toString(), codigoCC));
			}	
		}
		
		return executeCriteria(criteria, 0, 1, null);
	}	

	/**
	 * #44249 C4 
	 * Consulta para a SuggestionBox Servidores
	 * @return
	 */
	public List<RapServidoresProcedimentoTerapeuticoVO> obterListaServidoresAtivos(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RPS");
		criteria.createAlias("RPS."+RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RPS."+RapServidores.Fields.MATRICULA.toString()), RapServidoresProcedimentoTerapeuticoVO.Fields.MATRICULA.toString())
				.add(Projections.property("RPS."+RapServidores.Fields.VIN_CODIGO.toString()), RapServidoresProcedimentoTerapeuticoVO.Fields.VIN_CODIGO.toString())
				.add(Projections.property("RPF."+RapPessoasFisicas.Fields.NOME.toString()), RapServidoresProcedimentoTerapeuticoVO.Fields.NOME.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresProcedimentoTerapeuticoVO.class));
		criteria = obterFiltroListaServidoresAtivo(criteria, descricao);
		criteria.addOrder(Order.asc("RPF."+RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 *  #44249 C4 
	 * Consulta para a SuggestionBox Servidores Count
	 * @return
	 */
	public Long obterListaServidoresAtivosCount(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RPS");
		criteria.createAlias("RPS."+RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.INNER_JOIN);
		criteria = obterFiltroListaServidoresAtivo(criteria, descricao);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterFiltroListaServidoresAtivo(DetachedCriteria criteria, String descricao){
		criteria.add(Restrictions.eq("RPS."+RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A));
		criteria.add(Restrictions.isNull("RPS."+RapServidores.Fields.DATA_FIM_VINCULO.toString()));
		if (StringUtils.isNotBlank(descricao)){
			if(CoreUtil.isNumeroInteger(descricao) && CoreUtil.isNumeroShort(descricao)) {
				criteria.add(Restrictions.or(Restrictions.eq("RPS."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(descricao)),
								Restrictions.eq("RPS."+RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(descricao)),
								Restrictions.ilike("RPF."+RapPessoasFisicas.Fields.NOME.toString(), descricao, MatchMode.ANYWHERE)));
			} else if(CoreUtil.isNumeroInteger(descricao) && !CoreUtil.isNumeroShort(descricao) ) {
				criteria.add(Restrictions.or(Restrictions.eq("RPS."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(descricao)),
						Restrictions.ilike("RPF."+RapPessoasFisicas.Fields.NOME.toString(), descricao, MatchMode.ANYWHERE)));
			} else if(CoreUtil.isNumeroShort(descricao)) {
				criteria.add(Restrictions.or(Restrictions.eq("RPS."+RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(descricao)),
						Restrictions.ilike("RPF."+RapPessoasFisicas.Fields.NOME.toString(), descricao)));
			} else {
				criteria.add(Restrictions.ilike("RPF."+RapPessoasFisicas.Fields.NOME.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}	

	/**
	 * * #43033 P1 
	 * Consultas para obter CURSOR cur_pre
	 * Substitui CURSOR cur_pre em P1
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public List<CursorCurPreVO> obterCursorCurPreVO(Integer matricula, Short vinCodigo){
		CursorCurPreVOQueryBuilder builder = new CursorCurPreVOQueryBuilder();
		return executeCriteria(builder.build(matricula, vinCodigo));
	}

	/**
	 * * #43033 P2 
	 * Consultas para obter CURSOR cur_rap
	 * Substitui CURSOR cur_rap em P2
	 * @param pMatricula
	 * @param pVinCodigo
	 * @return
	 */
	public List<CursorCurPreVO> obterCursorCurRapVO(Integer pMatricula, Short pVinCodigo){
		CursorCurPreVOQueryBuilder builder = new CursorCurPreVOQueryBuilder();
		return executeCriteria(builder.build(pMatricula, pVinCodigo));
	}
	
	/**
	 * 43443
	 */
	
	private DetachedCriteria criarCriteriaPesquisarRapServidor(Object servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		Disjunction or = Restrictions.disjunction();
		
		String stParametro = (String) servidor;
		
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString(),RapServidores.Fields.PESSOA_FISICA.toString());
				if(CoreUtil.isNumeroShort(stParametro)){
					or.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(stParametro)));
				}
				if(CoreUtil.isNumeroInteger(stParametro)){
					or.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(stParametro)));
				}
				or.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), stParametro, MatchMode.ANYWHERE));
		criteria.add(or);
		
		return criteria;
	}
	
	public List<RapServidores> pesquisarServidorPorVinculoOuMatriculaOuNome(Object servidor) {

		DetachedCriteria criteria = criarCriteriaPesquisarRapServidor(servidor);
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA
				.toString() + "." + RapPessoasFisicas.Fields.NOME));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarServidorPorVinculoOuMatriculaOuNomeCount(Object servidor) {
		return executeCriteriaCount(criarCriteriaPesquisarRapServidor(servidor));
	}
	
	public List<RapServidores> listarTodosServidoresOrdernadosPorNome(){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RS");
		criteria.createAlias("RS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		criteria.addOrder(Order.asc("PF." + RapPessoasFisicas.Fields.NOME.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public RapServidores pesquisaServidorCseUsuarioPorServidor(RapServidores servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.add(Restrictions.eq(RapServidores.Fields.SERVIDOR.toString(), servidor));
		criteria.createAlias(RapServidores.Fields.CSE_USUARIO.toString(), "cse");
		return (RapServidores)executeCriteriaUniqueResult(criteria);
	}

	public List<RapServidores> obterServidorTecnicoPorItemRecebimento(Integer recebimento, Integer itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RS");
		criteria.createAlias("RS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		criteria.createAlias("RS." + RapServidores.Fields.TECNICOS_ITEM_RECEBIMENTO.toString(), "TIR", JoinType.INNER_JOIN);
		criteria.createAlias("TIR." + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.INNER_JOIN);
		if (recebimento != null) {
			criteria.add(Restrictions.eq("IRP." + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if (itemRecebimento != null) {
			criteria.add(Restrictions.eq("IRP." + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria createPesquisaCriteriaInterconsulta(String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		criteria.add(Restrictions.or(
				Restrictions.isNull("SER."+RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				Restrictions.gt("SER."+RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())
				));
		
		if (StringUtils.isNotBlank(pesquisa)){
			if(CoreUtil.isNumeroInteger(pesquisa) && CoreUtil.isNumeroShort(pesquisa)) {
				criteria.add(
						Restrictions.or(
								Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(pesquisa)),
								Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(pesquisa)))
								);
			
			} else if(CoreUtil.isNumeroInteger(pesquisa) && !CoreUtil.isNumeroShort(pesquisa) ) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(pesquisa)));
				
			} else if(CoreUtil.isNumeroShort(pesquisa)) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(pesquisa)));
			
			} else {
				criteria.add(Restrictions.ilike("PF."+ RapPessoasFisicas.Fields.NOME.toString(), pesquisa, MatchMode.ANYWHERE));

			}
		}
	
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SER."+RapServidores.Fields.MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
				.add(Projections.property("SER."+RapServidores.Fields.VIN_CODIGO.toString()), RapServidoresVO.Fields.VINCULO.toString())
				.add(Projections.property("SER."+RapServidores.Fields.DATA_INICIO_VINCULO.toString()), RapServidoresVO.Fields.DATA_INICIO_VINCULO.toString())
				.add(Projections.property("SER."+RapServidores.Fields.DATA_FIM_VINCULO.toString()), RapServidoresVO.Fields.DATA_FIM_VINCULO.toString())
				.add(Projections.property("PF."+ RapPessoasFisicas.Fields.NOME.toString()), RapServidoresVO.Fields.RPF_NOME.toString())
				.add(Projections.property("PF."+ RapPessoasFisicas.Fields.CODIGO.toString()), RapServidoresVO.Fields.RPF_CODIGO.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		return criteria;
	}
	
	public List<RapServidoresVO> pesquisarServidorInterconsulta(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa);
		criteria.addOrder(Order.asc("PF."+ RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);

	}
	public Long pesquisarServidorInterconsultaCount(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #43464 - Obtem count de {@link RapServidores} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @return {@link Long} count
	 */
	public Long obterCountServidorPorMatriculaVinculoOuNome(String parametro) {
		return executeCriteriaCount(obterCriteriaServidorPorMatriculaVinculoOuNomCount(parametro));
	}
	
	/**
	 * #43464 - Obtem criteria para count de SuggestionBox de RapServidores.
	 */
	private DetachedCriteria obterCriteriaServidorPorMatriculaVinculoOuNomCount(String parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "FIS");

		if (StringUtils.isNotBlank(parametro)) {			
			if (CoreUtil.isNumeroShort(parametro)) {				
				Criterion restr1 = Restrictions.eq("SER."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro));
				Criterion restr2 = Restrictions.ilike("FIS."+RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE);
				Criterion restr3 = Restrictions.eq("SER."+RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(parametro));
				Disjunction disjunction = Restrictions.disjunction(); 
				disjunction.add(restr1);
				disjunction.add(restr2);
				disjunction.add(restr3);
				criteria.add(disjunction);
			} else if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq("SER."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike("FIS."+RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike("FIS."+RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	/**
	 * #43464 - Obtém lista de {@link RapServidores} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @return {@link List} de {@link RapServidores}
	 */
	public List<RapServidores> obterListaServidorPorMatriculaVinculoOuNome(String parametro) {
		return executeCriteria(obterCriteriaServidorPorMatriculaVinculoOuNom(parametro), 0, 100, RapServidores.Fields.NOME_PESSOA_FISICA.toString(), true);
	}
	
	/**
	 * #43464 - Obtém criteria para consulta de {@link RapServidores} para Suggestion Box.
	 * 
	 * @param parametro {@link String}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaServidorPorMatriculaVinculoOuNom(String parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);		

		if (StringUtils.isNotBlank(parametro)) {			
			if (CoreUtil.isNumeroShort(parametro)) {				
				Criterion restr1 = Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro));
				Criterion restr2 = Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), parametro, MatchMode.ANYWHERE);
				Criterion restr3 = Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(parametro));
				Disjunction disjunction = Restrictions.disjunction(); 
				disjunction.add(restr1);
				disjunction.add(restr2);
				disjunction.add(restr3);
				criteria.add(disjunction);
			} else if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(RapServidores.Fields.NOME_PESSOA_FISICA.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	//#43482 SB1
	public List <RapServidores> obterusuariosTecnicosPorVinculoMatNome(String parametro){
		DetachedCriteria criteria = criarCriteriaPesquisarRapServidor(parametro);
		criteria.addOrder(Order.asc(RapServidores.Fields.NOME_PESSOA_FISICA.toString()));
		
		return  executeCriteria(criteria, 0, 100, null, false);
	}

	//#43482 SB1
	public Long obterusuariosTecnicosPorVinculoMatNomeCount(String parametro){
		return executeCriteriaCount(criarCriteriaPesquisarRapServidor(parametro));
	}
	
	/**
	 * #43449 - Consulta para pesquisar responsável pela área técnica.
	 * @param area
	 * @return
	 */
	public RapServidores pesquisarServidorPorMatricula(PtmAreaTecAvaliacao area){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RS");
		criteria.createAlias("RS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(RapServidores.Fields.ID.toString(), area.getServidorCC().getId()));
		
		return (RapServidores)executeCriteriaUniqueResult(criteria);
	}

	public List<RapServidores> obterServidorPorAreaTecnicaAvaliacao(Integer ataSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RS");
		criteria.createAlias("RS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		criteria.createAlias("RS." + RapServidores.Fields.SERVIDORES_AREA_TEC_AVALIACAO.toString(), "SATA", JoinType.INNER_JOIN);
		criteria.createAlias("SATA." + PtmServAreaTecAvaliacao.Fields.AREA_TEC_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);
		if (ataSeq != null) {
			criteria.add(Restrictions.eq("ATA." + PtmAreaTecAvaliacao.Fields.SEQ.toString(), ataSeq));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Ajuste de Documentação #50618
	 * @param codigoCentroCusto
	 * @return
	 */
	public Object[] obterNomeMatriculaServidorCentroCusto(Integer codigoCentroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "FCC");
		
		criteria.createAlias("FCC"+PONTO+FccCentroCustos.Fields.SERVIDOR.toString(), ALIAS_SER, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_SER+PONTO+RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("FCC"+PONTO+FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCusto));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FccCentroCustos.Fields.SERVIDOR_MATRICULA.toString()))
				.add(Projections.property(ALIAS_PES+PONTO+RapPessoasFisicas.Fields.NOME.toString())));


		Object[] servidor = (Object[]) this.executeCriteriaUniqueResult(criteria);
		return servidor;
	}
	

	
	/**
	 * #44276
	 * C6 - Buscar usuário no sistema
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	
	public RapServidores buscarUsuarioSistema(Integer matriculaChefia, Short vinCodigoChefia) {
		
		RapServidoresId id = new RapServidoresId(matriculaChefia, vinCodigoChefia);
		if(id != null){
		return	obterPorChavePrimaria(id);
		}
		
		else {
			return null;
		}
 	}	
	
	/**
	 * #44713 - Consultar usuários SugestionBox
	 * @param objPesquisa
	 * @param colunaOrdenacao
	 * @return
	 */
	public List<RapServidores> consultarUsuariosSugestion(Object param) {
		DetachedCriteria criteria = consultarUsuariosSugestionCriteria(param);
		criteria.addOrder(Order.asc("PES."+RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria consultarUsuariosSugestionCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class,ALIAS_SER);
		criteria.createAlias(ALIAS_SER+PONTO+RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES, JoinType.INNER_JOIN);
		if (param != null && StringUtils.isNotBlank(param.toString())) {
			if(CoreUtil.isNumeroInteger(param.toString())) {
				criteria.add(Restrictions.eq(ALIAS_SER+PONTO+RapServidores.Fields.MATRICULA.toString(),Integer.valueOf(param.toString())));
			} else {
				criteria.add(Restrictions.ilike(ALIAS_PES+PONTO+RapPessoasFisicas.Fields.NOME.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	public Long consultarUsuariosSugestionCount(Object param){
		DetachedCriteria criteria = consultarUsuariosSugestionCriteria(param);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem lista de profissionais para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Lista de {@link RapServidores}.
	 */
	public List<RapServidores> obterListaProfissionalPorMatriculaVinculoOuNome(String parametro) {
		DetachedCriteria criteria = obterCriteriaProfissionalPorMatriculaVinculoNome(parametro, false);
		return executeCriteria(criteria, 0, 100, null, false);
	}

	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem count de profissionais para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Quantidade de Registros retornados.
	 */
	public Long obterCountProfissionalPorMatriculaVinculoOuNome(String parametro) {
		DetachedCriteria criteria = obterCriteriaProfissionalPorMatriculaVinculoNome(parametro, true);
		return executeCriteriaCount(criteria);
	}

	

	private DetachedCriteria obterCriteriaProfissionalPorMatriculaVinculoNome(String parametro, Boolean isCount) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "RS");		
		criteria.createAlias("RS." + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.INNER_JOIN);
		if (StringUtils.isNotBlank(parametro)) {			
			if (CoreUtil.isNumeroShort(parametro)) {				
				criteria.add(Restrictions.or(
						Restrictions.eq("RS." + RapServidores.Fields.VIN_CODIGO.toString(), Short.valueOf(parametro)),
						Restrictions.eq("RS." + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike("RPF." + RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE)));
			} else if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.or(
						Restrictions.eq("RS." + RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(parametro)),
						Restrictions.ilike("RPF." + RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE)));
			}  else {
				criteria.add(Restrictions.ilike("RPF." + RapPessoasFisicas.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		if(!isCount) {
			criteria.addOrder(Order.asc("RS." + RapServidores.Fields.VIN_CODIGO.toString()));			
			criteria.addOrder(Order.asc("RS." + RapServidores.Fields.MATRICULA.toString()));
		}
		return criteria;
	}
	
	public List<RapServidores> obterServidoresComPessoaFisicaPorEquipe(String param,AghEquipes equipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "servidor");
		
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");

		if(equipe != null){
			criteria.createAlias("servidor."+RapServidores.Fields.EQUIPES.toString(), "equipes" , JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("equipes."+AghEquipes.Fields.SEQ.toString(),equipe.getSeq()));
		}
		
		if (StringUtils.isNotEmpty(param)) {
			if (StringUtils.isNumeric(param) && param.length()<10){
				criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(param)));
				criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.MATRICULA.toString()));
			}else{
				criteria.add(Restrictions.ilike("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString(), param, MatchMode.ANYWHERE));
				criteria.addOrder(Order.asc("pessoaFisica."+RapPessoasFisicas.Fields.NOME.toString()));
			}	
		}else{
			criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.CODIGO_VINCULO.toString()));			
			criteria.addOrder(Order.asc("servidor."+RapServidores.Fields.MATRICULA.toString()));			
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria, 0, 1000, null);
	}
}