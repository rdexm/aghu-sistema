package br.gov.mec.aghu.paciente.dao;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.AtendimentoPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoNotificacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.faturamento.vo.ContaNutricaoEnteralVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadosExamesHist;
import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.PesquisaFoneticaPaciente;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;
import br.gov.mec.aghu.paciente.vo.DadosAdicionaisVO;
import br.gov.mec.aghu.paciente.vo.PacientePortadorGermeVO;
import br.gov.mec.aghu.prescricaomedica.vo.AtendimentoJustificativaUsoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;

/**
 * @modulo paciente
 *
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse", "PMD.NcssTypeCount"})
public class AipPacientesDAO extends BaseDao<AipPacientes> {
	
	
	private static final long serialVersionUID = -1802798029857824002L;
	private static final String PAC = "PAC";
	private static final String PONTO = ".";

	private static final Log LOG = LogFactory.getLog(AipPacientesDAO.class);

	/**
	 * ORADB: Cursor C_PAC 
	 */
	public List<Integer> pesquisarCPac(final Integer pacCodigo){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.setProjection(Projections.property(AipPacientes.Fields.CODIGO.toString()));
		
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(AipPacientes.Fields.DT_ULT_INTERNACAO.toString()));
		
		criteria.add(Restrictions.or(
										Restrictions.isNull(AipPacientes.Fields.DT_ULT_ALTA.toString()), 
										Restrictions.and(
														  Restrictions.isNotNull(AipPacientes.Fields.DT_ULT_ALTA.toString()),
														  Restrictions.ltProperty(
																  				   AipPacientes.Fields.DT_ULT_ALTA.toString(),
																  				   AipPacientes.Fields.DT_ULT_INTERNACAO.toString()
																    			 )
														)

									)
				    );

		return executeCriteria(criteria, 0, 1, new HashMap<String, Boolean>());
	}
	
	

	/**
	 * Busca informações do paciente
	 * 
	 * ORADB CURSOR cur_paciente
	 * 
	 * @param codigo
	 * @return
	 */
	public List<Integer> executarCursorPaciente(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AipPacientes.Fields.CODIGO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}
	
	
	public AipPacientes obterHistoricoPorPacCodigo(Integer pacCodigo){
			
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class,PAC);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.NACIONALIDADE.toString(),"NAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.ENDERECOS.toString(), "ENP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.CIDADE.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.ETNIA.toString(), "ETN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.CENTRO_CUSTO_CADASTRO.toString(),"CCC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.SERVIDOR_CADASTRO.toString(),"SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER"+PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.CENTRO_CUSTO_RECADASTRO.toString(),"CCR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.SERVIDOR_RECADASTRO.toString(),"SRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRE"+PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), "pfr", JoinType.LEFT_OUTER_JOIN);
	
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * #42360 - Usada na Procedure 1
	 * Obtem o prontuario de um determinado paciente a partir do código do paciente.
	 * @param pacCodigo
	 * @return prontuario
	 */
	public Integer obterProntuarioPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()));
		criteria.setProjection(p);

		return (Integer)this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Busca informações do paciente
	 * 
	 * ORADB CURSOR cur_pac
	 * 
	 * @param codigo
	 * @return
	 */
	public List<AipPacientes> executarCursorPac(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));

		return this.executeCriteria(criteria);
	}
	
	private DetachedCriteria criteriaPacientesPorProntuario(
			Integer nroProntuario) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(),	nroProntuario));
		return criteria;
	}
	
	private DetachedCriteria criteriaPacientesPorCodigo(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),	pacCodigo));
		return criteria;
	}
	
	/**
	 * Obtem o nome de um paciente atraves de seu prontuario.
	 * 
	 * @param nroProntuario
	 * @return
	 */
	public String obterNomePacientePorProntuario(Integer nroProntuario) {
		if (nroProntuario == null) {
			return null;
		}
		DetachedCriteria criteria = this.criteriaPacientesPorProntuario(nroProntuario);
		criteria.setProjection(Projections.property(AipPacientes.Fields.NOME.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem o nome do paciente pelo código.
	 * @param pacCodigo
	 * @return paciente
	 */
	public AipPacientes obterNomePacientePorCodigo(Integer pacCodigo) {
		return super.obterPorChavePrimaria(pacCodigo);
	}
	
	public AipPacientes obterPacienteComAtendimentoPorProntuario(Integer nroProntuario){
		DetachedCriteria criteria = criteriaPacientesPorProntuario(nroProntuario);
		criteria.add(Restrictions.isNotEmpty(AipPacientes.Fields.ATENDIMENTOS.toString()));
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	

	/**
	 * Pesquisa de pacientes por codigo ou prontuario
	 * 
	 * @param filtros - dados a serem pesquisados (QUANDO LOAD, devera vir no formato [codigo,paciente]
	 * @param isLoad  - informa se é uma pesquisa de load de tela, neste caso retornara apenas um valor
	 * @return
	 */
	public List<AipPacientes> pesquisarAipPacientesPorCodigoOuProntuario(final Object filtro){
		if(filtro == null || filtro.toString().trim().isEmpty() || !CoreUtil.isNumeroInteger(filtro)){
			return new ArrayList<AipPacientes>();
		}
		
		final List<AipPacientes> result = executeCriteria(getCriteriaAipPacientesPorCodigoOuProntuario(filtro, false),0,100,AipPacientes.Fields.NOME.toString(), true);
		
		return result;
	}
	
	public Long pesquisarAipPacientesPorCodigoOuProntuarioCount(final Object filtro){
		if(filtro == null || filtro.toString().trim().isEmpty() || !CoreUtil.isNumeroInteger(filtro)){
			return 0L;
		}
		return executeCriteriaCount(getCriteriaAipPacientesPorCodigoOuProntuario(filtro, true));
	}
	
	private DetachedCriteria getCriteriaAipPacientesPorCodigoOuProntuario(final Object filtros, final boolean isCount) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.add( Restrictions.or(
										Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), Integer.valueOf(filtros.toString())),
										Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(),	Integer.valueOf(filtros.toString()))
									 )
					 );
		
		return criteria;
	}
	
	public Long pesquisarPacientesPorNacionalidadeCount(AipNacionalidades nacionalidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.NACIONALIDADE_CODIGO.toString(), nacionalidade.getCodigo()));
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarPacientesPorOcupacaoCount(AipOcupacoes ocupacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.OCUPACAO_CODIGO.toString(), ocupacao.getCodigo()));
		return executeCriteriaCount(criteria);
	}
	
	// 5465
	public List<AipPacientes> obterPacienteComAtendimentoPorProntuarioOUCodigo(Integer nroProntuario, Integer codigoPaciente, 
			List<DominioOrigemAtendimento> origemAtendimentos){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), AipPacientes.Fields.ATENDIMENTOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AipPacientes.Fields.INTERNACOES.toString(), AipPacientes.Fields.INTERNACOES.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.ATENDIMENTOS.toString() + "."+ AghAtendimentos.Fields.PAC_CODIGO.toString(),codigoPaciente));
		} else if (nroProntuario != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.ATENDIMENTOS.toString() + "."+ AghAtendimentos.Fields.PRONTUARIO.toString(),nroProntuario));
		}
		
		criteria.add(Restrictions.in(AipPacientes.Fields.ATENDIMENTOS.toString()+"."+AghAtendimentos.Fields.ORIGEM.toString(), origemAtendimentos));
		
		criteria.addOrder(Order.desc(AipPacientes.Fields.ATENDIMENTOS.toString()+"."+AghAtendimentos.Fields.DTHR_INICIO.toString()));
		
		List<AipPacientes> listaAtendimento = executeCriteria(criteria);
		
		if (listaAtendimento != null && listaAtendimento.size() != 0){
			return listaAtendimento;
		}
		
		return null;
	}
	//Fim 5465

	public List<Object[]> pesquisaReimpressaoEtiquetasUnion(List<Integer> prontuarios) {
		DetachedCriteria criteriaUnion = createPesquisaCriteria(prontuarios);
		return executeCriteria(criteriaUnion);
	}

	/**
	 * Prepara o criteria para a re-impressão de etiquetas.
	 * 
	 * @param prontuarios
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	private DetachedCriteria createPesquisaCriteria(List<Integer> prontuarios) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.createAlias(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO
				.toString(), AipPacientes.Fields.CENTRO_CUSTO_CADASTRO
				.toString());

		criteria.setProjection(Projections.projectionList()
				.add(
						Projections.property(AipPacientes.Fields.PRONTUARIO
								.toString())).add(
						Projections.property(AipPacientes.Fields.NOME
								.toString())).add(
						Projections.property(AipPacientes.Fields.LTO_LTO_ID
								.toString())).add(
						Projections.property(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO
								.toString() + "." + FccCentroCustos.Fields.CODIGO.toString())));

		criteria.add(Restrictions.in(AipPacientes.Fields.PRONTUARIO.toString(),
				prontuarios));

		criteria.addOrder(Order.asc(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO
				.toString() + "." + FccCentroCustos.Fields.CODIGO.toString()));

		return criteria;
	}

	public List<AipPacientes> obterPacientesParaPassivarProntuarioScrollableResults(String secao, Calendar cDthr, Integer prontuario,
			boolean incluiPassivos) throws ApplicationBusinessException {
		Query q = obterPacientesParaPassivarProntuario(secao, cDthr, prontuario, incluiPassivos);
		return q.list();
	}
	
	private Query obterPacientesParaPassivarProntuario(
			String secao, Calendar dthr, Integer prontuario, boolean incluiPassivos)
			throws ApplicationBusinessException {
		// Qualquer dúvida sobre a forma de implementação desta consulta,
		// verificar a procedure AIPP_PASSIVA_PAC

		LOG.debug("LOGPP: Entrou em OBTER PACIENTE PARA PASSIVAR");
		
		StringBuilder montaQuery = new StringBuilder(" SELECT ")
							.append(" pac ")
							.append(" FROM ")
							.append(" AipPacientes pac ")
							.append(" WHERE ")
							.append(" pac.prontuario >= :pront1 AND pac.prontuario <= :pront2 ")
							.append(" AND pac.prontuario < :valorMaximoProntuario ")
							.append(" AND substring(CAST(pac.prontuario as string), length(CAST(pac.prontuario as string)) - 2, 2) = :secao")
							.append(" AND pac.dtIdentificacao < :dthr ")
							.append(" AND ((pac.dtUltInternacao < :dthr OR pac.dtUltInternacao is null)")
							.append(" 	  AND (pac.dtUltProcedimento < :dthr OR pac.dtUltProcedimento is null))");
		if (!incluiPassivos){
			montaQuery.append(" AND pac.prntAtivo != :dominioPassivo ");
		}
		
		Query q = createHibernateQuery(montaQuery.toString()); 
			
		q.setParameter("secao", secao);
		q.setParameter("pront1", prontuario == null ? 0 : prontuario);
		q.setParameter("pront2", prontuario == null ? 99999999 : prontuario);
		q.setParameter("valorMaximoProntuario", AipPacientes.VALOR_MAXIMO_PRONTUARIO); 
		q.setParameter("dthr", dthr.getTime());
		
		if (!incluiPassivos){
			q.setParameter("dominioPassivo", DominioTipoProntuario.P);
		}

		return q;
	}

	/**
	 * <h1>Conversão do cursor c_busca_nome</h1> Realiza a busca de uma lista de
	 * registros na tabela AIP_PACIENTES utilizando como filtro de pesquisa o
	 * número do prontuário do paciente vinculado a tabela AIP_PACIENTES. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * cursor c_busca_nome is
	 * select nome from aip_pacientes
	 * where  prontuario = p_prontuario_origem;
	 * </code>
	 * 
	 * ORADB Cursor c_busca_nome, declarado na procedure insere_journal,
	 * encontrada dentro da procedure AIPP_SUBSTITUI_PRONT.
	 * 
	 * @param pProntuarioOrigem
	 *            Número do prontuário.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 **/
	public String cursorCBuscaNome(Integer pProntuarioOrigem) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(),
				pProntuarioOrigem));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(AipPacientes.Fields.NOME
				.toString()));
		criteria.setProjection(projectionList);

		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Busca Codigos de Pacientes conforme query abaixo:<br>
	 * <b>
	 * select distinct AinInternacao.pacCodigo<br>
	 * from AinInternacao<br>
	 * where AinInternacao.AinLeitos.AinQuartos.numero = <i>numeroQuarto</i><br>
	 * and AinInternacao.indPacienteInternado = true<br>
	 * </b>
	 * 
	 * @param numeroQuarto
	 * @return List of Integer.
	 */
	public List<Integer> pesquisarCodigoPacientesPorNumeroQuarto(Short numeroQuarto) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);
		
		cri.setProjection(
			Projections.distinct(
				Projections.projectionList().add(Projections.property(AinInternacao.Fields.PAC_CODIGO.toString()))
			)
		);
		cri.createAlias(AinInternacao.Fields.LEITO.toString(), "LTO");
		cri.createAlias("LTO."+AinLeitos.Fields.QUARTO.toString(), "QRT");
		cri.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		cri.add(Restrictions.or(
					Restrictions.eq(AinInternacao.Fields.QRT_NUMERO.toString(), numeroQuarto),
				 	Restrictions.eq("QRT."+AinQuartos.Fields.NUMERO.toString(), numeroQuarto)
				 )
		);
		
		List<Integer> resultadoPesquisa = this.executeCriteria(cri);
		
		return resultadoPesquisa;
	}
	
	public DominioSexo obterSexoAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.SEXO.toString())));
		DominioSexo sexoAnterior = (DominioSexo) this
				.executeCriteriaUniqueResult(criteria);

		return sexoAnterior;
	}
	
	public String obterNomeAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.NOME.toString())));
		return (String) this.executeCriteriaUniqueResult(criteria);
	}
	
	public String obterNomeMaeAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.NOMEMAE.toString())));
		return (String) this.executeCriteriaUniqueResult(criteria);
	}
	
	public Date obterDataNascimentoAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.DATA_NASCIMENTO
						.toString())));
		return (Date) this.executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterProntarioAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO
						.toString())));
		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}
	
	public DominioTipoProntuario obterPrntAtivoAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.PRN_ATIVO.toString())));
		return (DominioTipoProntuario) this.executeCriteriaUniqueResult(criteria);
	}
	
	public DominioSexo obterSexoBiologicoAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.SEXO_BIOLOGICO
						.toString())));
		return (DominioSexo) this.executeCriteriaUniqueResult(criteria);
	}

	public Short obterSeqUnidadeFuncionalPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.UNIDADE_FUNCIONAL_SEQ
						.toString())));
		return (Short) this.executeCriteriaUniqueResult(criteria);
	}

	public List<AipPacientes> listaPacientesPorProntuarioIgnorandoCodigo(Integer prontuario, Integer codigoIgnorado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.ne(AipPacientes.Fields.CODIGO.toString(), codigoIgnorado));
		
		return executeCriteria(criteria);
	}

	public List<DominioSexo> listarSexosDoQuarto(Short numeroQuarto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		DetachedCriteria criteriaInternacoes = criteria.createCriteria("internacoes");
		criteriaInternacoes.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		criteriaInternacoes.add(Restrictions.eq(AinInternacao.Fields.QRT_NUMERO.toString(), numeroQuarto));
		
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.SEXO.toString()))));
		
		return this.executeCriteria(criteria);
	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorEspecialidadeEPaciente(Short espec, Integer prontuario, List<String> situacoesIn, boolean historicos){
		/*
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);
		*/
		String schema, soe, ise, ree, pcl, ntc = null;
		
		if (!historicos){		
			schema="agh";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString();
			ise="soe."+AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicitacaoExames.Fields.NOTA_ADICIONAL.toString();			
		}else{
			schema="hist";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES_HIST.toString();
			ise="soe."+AelSolicitacaoExamesHist.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicExameHist.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadosExamesHist.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicExameHist.Fields.NOTA_ADICIONAL.toString();
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "pac");
		criteria.createCriteria("pac."+AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", Criteria.INNER_JOIN);
		
		criteria.createCriteria(soe, "soe", Criteria.INNER_JOIN);
		criteria.createCriteria(ise, "ise", Criteria.INNER_JOIN);
		criteria.createCriteria(ree, "ree", Criteria.INNER_JOIN);
		criteria.createCriteria(pcl, "pcl", Criteria.INNER_JOIN);
		//criteria.createCriteria("ree."+AelResultadoExame.Fields.ESPECIALIDADE_CAMPO_LAUDO.toString(), "ccl", Criteria.INNER_JOIN);

		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", Criteria.INNER_JOIN);
		criteria.createCriteria("cal."+AelCampoLaudo.Fields.ESPECIALIDADE_CAMPO_LAUDO.toString(), "ccl", Criteria.INNER_JOIN);
		criteria.createCriteria(ntc, "ntc", Criteria.LEFT_JOIN);

		/**
		 * Projections
		 * */
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.sqlProjection("substr(ccl7_.nome_sumario,1,25) as nome_sumario", new String[] { "nome_sumario" },new Type[] { StringType.INSTANCE}), "nome_sumario");

		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.SEQ.toString()),"cal_seq");
		projectionList.add(Property.forName("ccl."+AelEspecialidadeCampoLaudo.Fields.ORDEM.toString()),"ordem");
		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.DIVIDE_POR_MIL.toString()),"ind_divide_por_mil");
		projectionList.add(Property.forName("ree."+AelResultadoExame.Fields.VALOR.toString()),"valor");
		projectionList.add(Property.forName("pcl."+AelParametroCamposLaudo.Fields.QUANTIDADE_CASAS_DECIMAIS.toString()),"quantidade_casas_decimais");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()),"dthr_programada");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()),"dt_nascimento");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.SEXO.toString()),"sexo");

		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),"soe_seq");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()),"seqp");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()),"sit_codigo");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()),"dthr_liberada");

		projectionList.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
													"	from   	"+schema+".ael_extrato_item_solics eis" +
													"	where	eis.ise_soe_seq = ise3_.soe_seq" +
													"	and     eis.ise_seqp = ise3_.seqp" +
													"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
				new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), "dthr_evento");

		projectionList.add(Property.forName("ntc."+AelNotaAdicional.Fields.SEQP.toString()),"ntc_seqp");
		criteria.setProjection(projectionList);

		/**
		 * Restrictions
		 **/
		
		criteria.add(Restrictions.eq("ccl."+AelEspecialidadeCampoLaudo.Fields.ESP_SEQ.toString(), espec));
		criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		
		/*o código abaixo se refere a 'and   substr(ise.sit_codigo,1,2)in (c_sit_codigo_lib,c_sit_codigo_exe)' */		
		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacoesIn));

		/*o código abaixo se refere a 'and ree.ind_anulacao_laudo = 'N' */
		criteria.add(Restrictions.eq("ree."+AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));

		/*o código abaixo se refere a 'and ree.valor is not null' */
		criteria.add(Restrictions.isNotNull("ree."+AelResultadoExame.Fields.VALOR.toString()));

		/*o código abaixo se refere a 'and cal.tipo_campo in ('N','E')*/
		List<DominioTipoCampoCampoLaudo> tiposCampo = new ArrayList<DominioTipoCampoCampoLaudo>();
		tiposCampo.add(DominioTipoCampoCampoLaudo.N);
		tiposCampo.add(DominioTipoCampoCampoLaudo.E);
		criteria.add(Restrictions.in("cal."+AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tiposCampo));

		/*o código abaixo se refere a 'and cal.ind_fluxo = 'S'' */
		criteria.add(Restrictions.eq("cal."+AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));

		/*o código abaixo se refere a 'and ntc.seqp(+) = 1'*/
		criteria.add(Restrictions.sqlRestriction("coalesce(ntc8_.seqp,1) 	= 1"));
		criteria.add(Restrictions.sqlRestriction("ree4_.pcl_cal_seq 		=     ccl7_.cal_seq"));

		/**
		 * Order
		 * */
		criteria.addOrder(Order.desc("dthr_evento"));
		//criteria.addOrder(Order.desc("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		criteria.addOrder(Order.asc("ccl."+AelEspecialidadeCampoLaudo.Fields.ORDEM.toString()));

		List<Object[]> resultados = this.executeCriteria(criteria);

		List<FluxogramaLaborarorialDadosVO> examesFluxograma = new ArrayList<FluxogramaLaborarorialDadosVO>();

		for (Object[] resultado : resultados) {
			FluxogramaLaborarorialDadosVO exameFluxo = new FluxogramaLaborarorialDadosVO();

			exameFluxo.setNomeSumario(resultado[0].toString());
			exameFluxo.setCalSeq((Integer)resultado[1]);
			exameFluxo.setOrdem((Short)resultado[2]);
			exameFluxo.setIndDividePorMil((Boolean)resultado[3]);
			exameFluxo.setValor((Long)resultado[4]);
			//exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			
			if(resultado[5]!=null){
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			}else{
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf("0"));
			}
			
			exameFluxo.setDthrProgramada((Date)resultado[6]);
			exameFluxo.setDtNascimento((Date)resultado[7]);

			/*trunc(months_between(ise.dthr_programada,pac.dt_nascimento)/12)
			 * trocado por*/
			exameFluxo.setIdade(DateUtil.obterQtdAnosEntreDuasDatas(exameFluxo.getDtNascimento(), exameFluxo.getDthrProgramada()));

			exameFluxo.setSexo(((DominioSexo)resultado[8]).toString());
			exameFluxo.setSoeSeq((Integer)resultado[9]);
			exameFluxo.setSeqp((Short)resultado[10]);
			exameFluxo.setCodSituacao(resultado[11].toString());

			exameFluxo.setDthrLiberada((Date)resultado[12]);
			exameFluxo.setDthrEvento((Date)resultado[13]);
			exameFluxo.setNtcSeqp((Integer)resultado[14]);

			examesFluxograma.add(exameFluxo);
		}

		return examesFluxograma;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorServidorEPaciente(RapServidores servidor, Integer prontuario, List<String> situacoesIn, boolean historicos){
		
		/*
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);
		*/
		
		
		String schema, soe, ise, ree, pcl, ntc = null;
		
		if (!historicos){		
			schema="agh";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString();
			ise="soe."+AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicitacaoExames.Fields.NOTA_ADICIONAL.toString();			
		}else{
			schema="hist";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES_HIST.toString();
			ise="soe."+AelSolicitacaoExamesHist.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicExameHist.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadosExamesHist.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicExameHist.Fields.NOTA_ADICIONAL.toString();
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "pac");
		criteria.createCriteria("pac."+AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", Criteria.INNER_JOIN);
		
		criteria.createCriteria(soe, "soe", Criteria.INNER_JOIN);
		criteria.createCriteria(ise, "ise", Criteria.INNER_JOIN);
		criteria.createCriteria(ree, "ree", Criteria.INNER_JOIN);
		criteria.createCriteria(pcl, "pcl", Criteria.INNER_JOIN);
		//criteria.createCriteria("ree."+AelResultadoExame.Fields.ESPECIALIDADE_CAMPO_LAUDO.toString(), "ccl", Criteria.INNER_JOIN);

		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", Criteria.INNER_JOIN);
		criteria.createCriteria("cal."+AelCampoLaudo.Fields.SERVIDOR_CAMPO_LAUDO.toString(), "sla", Criteria.INNER_JOIN);
		criteria.createCriteria(ntc, "ntc", Criteria.LEFT_JOIN);		

		/**
		 * Projections
		 * */
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.sqlProjection("substr(sla7_.nome_sumario,1,25) as nome_sumario", new String[] { "nome_sumario" },new Type[] { StringType.INSTANCE}), "nome_sumario");

		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.SEQ.toString()),"cal_seq");
		projectionList.add(Property.forName("sla."+AelEspecialidadeCampoLaudo.Fields.ORDEM.toString()),"ordem");
		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.DIVIDE_POR_MIL.toString()),"ind_divide_por_mil");
		projectionList.add(Property.forName("ree."+AelResultadoExame.Fields.VALOR.toString()),"valor");
		projectionList.add(Property.forName("pcl."+AelParametroCamposLaudo.Fields.QUANTIDADE_CASAS_DECIMAIS.toString()),"quantidade_casas_decimais");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()),"dthr_programada");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()),"dt_nascimento");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.SEXO.toString()),"sexo");

		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),"soe_seq");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()),"seqp");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()),"sit_codigo");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()),"dthr_liberada");

		projectionList.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
													"	from   	"+schema+".ael_extrato_item_solics eis" +
													"	where	eis.ise_soe_seq = ise3_.soe_seq" +
													"	and     eis.ise_seqp = ise3_.seqp" +
													"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
				new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), "dthr_evento");

		projectionList.add(Property.forName("ntc."+AelNotaAdicional.Fields.SEQP.toString()),"ntc_seqp");
		criteria.setProjection(projectionList);

		/**
		 * Restrictions
		 **/
		criteria.add(Restrictions.eq("sla."+AelServidorCampoLaudo.Fields.SER_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("sla."+AelServidorCampoLaudo.Fields.SER_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));

		criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		
		/*o código abaixo se refere a 'and   substr(ise.sit_codigo,1,2)in (c_sit_codigo_lib,c_sit_codigo_exe)' */		
		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacoesIn));

		/*o código abaixo se refere a 'and ree.ind_anulacao_laudo = 'N' */
		criteria.add(Restrictions.eq("ree."+AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));

		/*o código abaixo se refere a 'and ree.valor is not null' */
		criteria.add(Restrictions.isNotNull("ree."+AelResultadoExame.Fields.VALOR.toString()));

		/*o código abaixo se refere a 'and cal.tipo_campo in ('N','E')*/
		List<DominioTipoCampoCampoLaudo> tiposCampo = new ArrayList<DominioTipoCampoCampoLaudo>();
		tiposCampo.add(DominioTipoCampoCampoLaudo.N);
		tiposCampo.add(DominioTipoCampoCampoLaudo.E);
		criteria.add(Restrictions.in("cal."+AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tiposCampo));

		/*o código abaixo se refere a 'and cal.ind_fluxo = 'S'' */
		criteria.add(Restrictions.eq("cal."+AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));

		/*o código abaixo se refere a 'and ntc.seqp(+) = 1'*/
		criteria.add(Restrictions.sqlRestriction("coalesce(ntc8_.seqp,1) 	= 1"));
		criteria.add(Restrictions.sqlRestriction("ree4_.pcl_cal_seq 		=     sla7_.cal_seq"));

		/**
		 * Order
		 * */
		criteria.addOrder(Order.desc("dthr_evento"));
		//criteria.addOrder(Order.desc("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		criteria.addOrder(Order.asc("sla."+AelServidorCampoLaudo.Fields.ORDEM.toString()));

		List<Object[]> resultados = this.executeCriteria(criteria);

		List<FluxogramaLaborarorialDadosVO> examesFluxograma = new ArrayList<FluxogramaLaborarorialDadosVO>();

		for (Object[] resultado : resultados) {
			FluxogramaLaborarorialDadosVO exameFluxo = new FluxogramaLaborarorialDadosVO();

			exameFluxo.setNomeSumario(resultado[0].toString());
			exameFluxo.setCalSeq((Integer)resultado[1]);
			exameFluxo.setOrdem((Short)resultado[2]);
			exameFluxo.setIndDividePorMil((Boolean)resultado[3]);
			exameFluxo.setValor((Long)resultado[4]);
			//exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			
			if(resultado[5]!=null){
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			}else{
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf("0"));
			}
			
			exameFluxo.setDthrProgramada((Date)resultado[6]);
			exameFluxo.setDtNascimento((Date)resultado[7]);

			/*trunc(months_between(ise.dthr_programada,pac.dt_nascimento)/12)
			 * trocado por*/
			exameFluxo.setIdade(DateUtil.obterQtdAnosEntreDuasDatas(exameFluxo.getDtNascimento(), exameFluxo.getDthrProgramada()));

			exameFluxo.setSexo(((DominioSexo)resultado[8]).toString());
			exameFluxo.setSoeSeq((Integer)resultado[9]);
			exameFluxo.setSeqp((Short)resultado[10]);
			exameFluxo.setCodSituacao(resultado[11].toString());

			exameFluxo.setDthrLiberada((Date)resultado[12]);
			exameFluxo.setDthrEvento((Date)resultado[13]);
			exameFluxo.setNtcSeqp((Integer)resultado[14]);

			examesFluxograma.add(exameFluxo);
		}

		return examesFluxograma;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorPaciente(Integer prontuario, List<String> situacoesIn, boolean historicos){
		
		/*
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);
		*/
		String schema, soe, ise, ree, pcl, ntc = null;
		
		if (!historicos){		
			schema="agh";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString();
			ise="soe."+AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicitacaoExames.Fields.NOTA_ADICIONAL.toString();			
		}else{
			schema="hist";
			soe="atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES_HIST.toString();
			ise="soe."+AelSolicitacaoExamesHist.Fields.ITENS_SOLICITACAO_EXAME.toString();
			ree="ise."+AelItemSolicExameHist.Fields.RESULTADO_EXAME.toString();
			pcl="ree."+AelResultadosExamesHist.Fields.PARAMETRO_CAMPO_LAUDO.toString();
			ntc="ise."+AelItemSolicExameHist.Fields.NOTA_ADICIONAL.toString();
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "pac");
		criteria.createCriteria("pac."+AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", Criteria.INNER_JOIN);
		
		criteria.createCriteria(soe, "soe", Criteria.INNER_JOIN);
		criteria.createCriteria(ise, "ise", Criteria.INNER_JOIN);
		criteria.createCriteria(ree, "ree", Criteria.INNER_JOIN);
		criteria.createCriteria(pcl, "pcl", Criteria.INNER_JOIN);
		//criteria.createCriteria("ree."+AelResultadoExame.Fields.ESPECIALIDADE_CAMPO_LAUDO.toString(), "ccl", Criteria.INNER_JOIN);

		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", Criteria.INNER_JOIN);
		criteria.createCriteria(ntc, "ntc", Criteria.LEFT_JOIN);			
		
		/**
		 * Projections
		 * */
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.sqlProjection("substr(cal6_.nome_sumario,1,25) as nome_sumario", new String[] { "nome_sumario" },new Type[] { StringType.INSTANCE}), "nome_sumario");
	
		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.SEQ.toString()),"cal_seq");
		projectionList.add(Property.forName("cal."+AelEspecialidadeCampoLaudo.Fields.ORDEM.toString()),"ordem");
		projectionList.add(Property.forName("cal."+AelCampoLaudo.Fields.DIVIDE_POR_MIL.toString()),"ind_divide_por_mil");
		projectionList.add(Property.forName("ree."+AelResultadoExame.Fields.VALOR.toString()),"valor");
		projectionList.add(Property.forName("pcl."+AelParametroCamposLaudo.Fields.QUANTIDADE_CASAS_DECIMAIS.toString()),"quantidade_casas_decimais");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()),"dthr_programada");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()),"dt_nascimento");
		projectionList.add(Property.forName("pac."+AipPacientes.Fields.SEXO.toString()),"sexo");
	
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),"soe_seq");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()),"seqp");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()),"sit_codigo");
		projectionList.add(Property.forName("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()),"dthr_liberada");
	
		projectionList.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
													"	from   	"+schema+".ael_extrato_item_solics eis" +
													"	where	eis.ise_soe_seq = ise3_.soe_seq" +
													"	and     eis.ise_seqp = ise3_.seqp" +
													"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
				new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), "dthr_evento");
	
		projectionList.add(Property.forName("ntc."+AelNotaAdicional.Fields.SEQP.toString()),"ntc_seqp");
		criteria.setProjection(projectionList);
	
		/**
		 * Restrictions
		 **/
		criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		
		/*o código abaixo se refere a 'and   substr(ise.sit_codigo,1,2)in (c_sit_codigo_lib,c_sit_codigo_exe)' */		
		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacoesIn));
	
		/*o código abaixo se refere a 'and ree.ind_anulacao_laudo = 'N' */
		criteria.add(Restrictions.eq("ree."+AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
	
		/*o código abaixo se refere a 'and ree.valor is not null' */
		criteria.add(Restrictions.isNotNull("ree."+AelResultadoExame.Fields.VALOR.toString()));
	
		/*o código abaixo se refere a 'and cal.tipo_campo in ('N','E')*/
		List<DominioTipoCampoCampoLaudo> tiposCampo = new ArrayList<DominioTipoCampoCampoLaudo>();
		tiposCampo.add(DominioTipoCampoCampoLaudo.N);
		tiposCampo.add(DominioTipoCampoCampoLaudo.E);
		criteria.add(Restrictions.in("cal."+AelCampoLaudo.Fields.TIPO_CAMPO.toString(), tiposCampo));
	
		/*o código abaixo se refere a 'and cal.ind_fluxo = 'S'' */
		criteria.add(Restrictions.eq("cal."+AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));
	
		/*o código abaixo se refere a 'and ntc.seqp(+) = 1'*/
		criteria.add(Restrictions.sqlRestriction("coalesce(ntc7_.seqp,1) 	= 1"));
	
		/**
		 * Order
		 * */
		criteria.addOrder(Order.desc("dthr_evento"));
		//criteria.addOrder(Order.desc("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		criteria.addOrder(Order.asc("cal."+AelCampoLaudo.Fields.ORDEM.toString()));
	
		List<Object[]> resultados = this.executeCriteria(criteria);
	
		List<FluxogramaLaborarorialDadosVO> examesFluxograma = new ArrayList<FluxogramaLaborarorialDadosVO>();
	
		for (Object[] resultado : resultados) {
			FluxogramaLaborarorialDadosVO exameFluxo = new FluxogramaLaborarorialDadosVO();
	
			exameFluxo.setNomeSumario(resultado[0].toString());
			exameFluxo.setCalSeq((Integer)resultado[1]);
			exameFluxo.setOrdem((Short)resultado[2]);
			exameFluxo.setIndDividePorMil((Boolean)resultado[3]);
			exameFluxo.setValor((Long)resultado[4]);
			//exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			
			if(resultado[5]!=null){
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf(resultado[5].toString()));
			}else{
				exameFluxo.setQuantidadeCasasDecimais(Byte.valueOf("0"));
			}
			
			exameFluxo.setDthrProgramada((Date)resultado[6]);
			exameFluxo.setDtNascimento((Date)resultado[7]);
	
			/*trunc(months_between(ise.dthr_programada,pac.dt_nascimento)/12)
			 * trocado por*/
			exameFluxo.setIdade(DateUtil.obterQtdAnosEntreDuasDatas(exameFluxo.getDtNascimento(), exameFluxo.getDthrProgramada()));
	
			exameFluxo.setSexo(((DominioSexo)resultado[8]).toString());
			exameFluxo.setSoeSeq((Integer)resultado[9]);
			exameFluxo.setSeqp((Short)resultado[10]);
			exameFluxo.setCodSituacao(resultado[11].toString());
	
			exameFluxo.setDthrLiberada((Date)resultado[12]);
			exameFluxo.setDthrEvento((Date)resultado[13]);
			exameFluxo.setNtcSeqp((Integer)resultado[14]);
	
			examesFluxograma.add(exameFluxo);
		}
	
		return examesFluxograma;
	}

	public List<DominioSexo> listarSexosDoLeito(Short numero) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		DetachedCriteria criateriInternacoes = criteria.createCriteria("internacoes");
		criateriInternacoes.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		DetachedCriteria criteriaLeitos = criateriInternacoes.createCriteria("leito");
		criteriaLeitos.add(Restrictions.eq(AinLeitos.Fields.QRT_NUMERO.toString(), numero));
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(
				Projections.property(AipPacientes.Fields.SEXO.toString()))));

		return this.executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Object[] obterDadosPacienteAnterior(Integer codigo) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AipPacientes.class);
		criteriaDadosAnteriores.add(Restrictions.eq(
				AipPacientes.Fields.CODIGO.toString(), codigo));
		criteriaDadosAnteriores
				.setProjection(Projections
						.projectionList()
						.add(Projections.property(AipPacientes.Fields.CODIGO
								.toString()))
						.add(Projections.property(AipPacientes.Fields.NOME
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.PRONTUARIO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.DATA_NASCIMENTO
										.toString()))
						.add(Projections.property(AipPacientes.Fields.PRN_ATIVO
								.toString()))
						.add(Projections.property(AipPacientes.Fields.VOLUMES
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.DATA_IDENTIFICACAO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.SERVIDOR_CADASTRO_MATRICULA
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.SERVIDOR_CADASTRO_VINCULO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.SERVIDOR_RECADASTRO_MATRICULA
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.SERVIDOR_RECADASTRO_VINCULO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO_CODIGO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.CENTRO_CUSTO_RECADASTRO_CODIGO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.SEXO_BIOLOGICO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.IND_PACIENTE_AGFA
										.toString()))
						.add(Projections.property(AipPacientes.Fields.NOMEMAE
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.CIDADE_CODIGO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.NACIONALIDADE_CODIGO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.OCUPACAO_CODIGO
										.toString()))
						.add(Projections.property(AipPacientes.Fields.UF_SIGLA
								.toString()))
						.add(Projections.property(AipPacientes.Fields.COR
								.toString()))
						.add(Projections.property(AipPacientes.Fields.SEXO
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.GRAU_INSTRUCAO
										.toString()))
						.add(Projections.property(AipPacientes.Fields.NOMEPAI
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.NATURALIDADE
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.DDD_FONE_RESIDENCIAL
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.FONE_RESIDENCIAL
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.DDD_FONE_RECADO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.FONE_RECADO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.ESTADO_CIVIL
										.toString()))
						.add(Projections.property(AipPacientes.Fields.CPF
								.toString()))
						.add(Projections.property(AipPacientes.Fields.RG
								.toString()))
						.add(Projections
								.property(AipPacientes.Fields.ORGAO_EMISSOR_RG
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.OBSERVACAO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.REG_NASCIMENTO
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.NUMERO_CARTAO_SAUDE
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.NUMERO_PIS
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.IND_PACIENTE_AGFA
										.toString()))
						.add(Projections
								.property(AipPacientes.Fields.IND_GERA_PRONTUARIO
										.toString())));
		
		return (Object[]) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);
	}

	/**
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param sexo
	 * @return
	 */
	private DetachedCriteria createPesquisaPacientesPorSexoDataObito(Date dtInicialReferencia, Date dtFinalReferencia, DominioSexo sexo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.add(Restrictions.ge(AipPacientes.Fields.DT_OBITO_EXTERNO.toString(), dtInicialReferencia));
		criteria.add(Restrictions.le(AipPacientes.Fields.DT_OBITO_EXTERNO.toString(), dtFinalReferencia));
		if (sexo != null && StringUtils.isNotBlank(sexo.getDescricao())) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.SEXO.toString(), sexo));
		}

		return criteria;
	}

	/**
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param sexo
	 * @return
	 */
	public List<AipPacientes> pesquisaPacientesPorSexoDataObito(Date dtInicialReferencia, Date dtFinalReferencia, DominioSexo sexo) {
		DetachedCriteria criteria = createPesquisaPacientesPorSexoDataObito(dtInicialReferencia, dtFinalReferencia, sexo);
		return executeCriteria(criteria);
	}
	public Date obterDtObitoAnterior(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AipPacientes.Fields.DT_OBITO.toString())));
		
		return (Date) this.executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public List<AipPacientes> obterPacientesPorFonemas(
			List<String> fonemasPaciente, List<String> fonemasMae,
			Calendar dataInicio, Calendar dataLimite, boolean respeitarOrdem,
			Integer firstResult, Integer maxResults, DominioListaOrigensAtendimentos listaOrigensAtendimentos, Boolean apenasInternados) {
		javax.persistence.Query query = createPesquisaFoneticaQuery(fonemasPaciente, fonemasMae,
				dataInicio, dataLimite, respeitarOrdem, firstResult,
				maxResults, false, listaOrigensAtendimentos, apenasInternados);

		return query.getResultList();
	}
	
	public List<AipPacientes> obterPacientesPorFonemas(PesquisaFoneticaPaciente paramPequisa,
			List<String> fonemasPaciente, List<String> fonemasMae, List<String> fonemasNomeSocial) {

		javax.persistence.Query query = createPesquisaFoneticaQuery(paramPequisa, fonemasPaciente, fonemasMae, fonemasNomeSocial);

		return query.getResultList();
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private javax.persistence.Query createPesquisaFoneticaQuery(PesquisaFoneticaPaciente paramPequisa, List<String> fonemasPaciente,
			List<String> fonemasMae, List<String> fonemasNomeSocial) {
		{
			StringBuilder sql = new StringBuilder(193);
			Boolean pesquisouPorPaciente = Boolean.FALSE;
			Boolean pesquisouPorMaePaciente = Boolean.FALSE;
			Boolean pesquisouPorNomeSocialPaciente = Boolean.FALSE;

			sql.append(" SELECT ");
			if (paramPequisa.getIsCount()) {
				sql.append(" COUNT(pac) ");
			} else {
				sql.append(" pac ");
			}
			sql.append(" FROM AipPacientes pac ");
			
			if(paramPequisa.getListaOrigensAtendimentos() != null){
				sql.append(" INNER JOIN pac.aghAtendimentos att ");
			}

			if (fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
				sql.append(" WHERE ");
				for (int i = 0; i < fonemasPaciente.size(); i++) {
					sql.append(" EXISTS ");
					sql.append(" (SELECT distinct f.aipPaciente.codigo FROM AipFonemaPacientes f WHERE f.aipPaciente.codigo = pac.codigo AND f.aipFonemas.fonema = :fonema"+i+")"); 
					if ((i+1) < fonemasPaciente.size()) {
						sql.append(" AND ");
					}
				}
				pesquisouPorPaciente = Boolean.TRUE;
			}
			
			if (fonemasMae != null && !fonemasMae.isEmpty()) {
				if (!pesquisouPorPaciente) {
					sql.append(" WHERE ");
				} else {
					sql.append(" AND ");
				}
				for (int i = 0; i < fonemasMae.size(); i++) {
					sql.append(" EXISTS ");
					sql.append(" (SELECT distinct fm.aipPaciente.codigo FROM AipFonemasMaePaciente fm WHERE fm.aipPaciente.codigo = pac.codigo AND fm.aipFonemas.fonema = :fonemaMae"+i+")"); 
					if ((i+1) < fonemasMae.size()) {
						sql.append(" AND ");
					}
				}
				pesquisouPorMaePaciente = Boolean.TRUE;
			}
			
			pesquisouPorNomeSocialPaciente = pesquisaNomeSocial(fonemasNomeSocial, sql, pesquisouPorPaciente, pesquisouPorNomeSocialPaciente);
			
			if (paramPequisa.getRespeitarOrdem() && pesquisouPorPaciente && !fonemasPaciente.isEmpty()) {
				sql.append(" AND ");
				for (int i = 0; i < (fonemasPaciente.size() - 1); i++) {
					sql.append(" EXISTS ");
					sql.append(" (SELECT distinct fpac.aipPaciente.codigo FROM AipFonemaPacientes fpac, AipPosicaoFonemasPacientes pfp1");
					sql.append("              WHERE pfp1.id.seq = fpac.seq");
					sql.append("                AND fpac.aipPaciente.codigo = pac.codigo");
					sql.append("                AND fpac.aipFonemas.fonema = :fonema");
					sql.append(i);
					sql.append("                AND pfp1.id.posicao <= (SELECT MAX(pfp2.id.posicao) FROM AipFonemaPacientes fpac2, AipPosicaoFonemasPacientes pfp2");
					sql.append("                                      WHERE pfp2.id.seq = fpac2.seq");
					sql.append("                                        AND pfp2.id.posicao >= pfp1.id.posicao");
					sql.append("                                        AND fpac2.aipPaciente.codigo = pac.codigo");
					sql.append("                                        AND fpac2.aipFonemas.fonema = :fonema");
					sql.append(i+1);
					sql.append(") )"); 
					if ((i+1) < (fonemasPaciente.size() - 1)) {
						sql.append(" AND ");
					}
				}
			}
			
			if (paramPequisa.getRespeitarOrdem() && pesquisouPorMaePaciente && !fonemasMae.isEmpty()) {
				sql.append(" AND ");
				for (int i = 0; i < (fonemasMae.size() - 1); i++) {
					sql.append(" EXISTS ");
					sql.append(" (SELECT distinct fmpac.aipPaciente.codigo FROM AipFonemasMaePaciente fmpac, AipPosicaoFonemasMaePaciente pfmp1");
					sql.append("              WHERE pfmp1.id.seq = fmpac.seq");
					sql.append("                AND fmpac.aipPaciente.codigo = pac.codigo");
					sql.append("                AND fmpac.aipFonemas.fonema = :fonemaMae");
					sql.append(i);
					sql.append("                AND pfmp1.id.posicao <= (SELECT MAX(pfmp2.id.posicao) FROM AipFonemasMaePaciente fmpac2, AipPosicaoFonemasMaePaciente pfmp2");
					sql.append("                                      WHERE pfmp2.id.seq = fmpac2.seq");
					sql.append("                                        AND pfmp2.id.posicao >= pfmp1.id.posicao");
					sql.append("                                        AND fmpac2.aipPaciente.codigo = pac.codigo");
					sql.append("                                        AND fmpac2.aipFonemas.fonema = :fonemaMae");
					sql.append(i+1);
					sql.append(") )"); 
					if ((i+1) < (fonemasMae.size() - 1)) {
						sql.append(" AND ");
					}
				}
			}
			
			pesquisaFoneticaNomeSocialPaciente(paramPequisa, fonemasNomeSocial, sql, pesquisouPorNomeSocialPaciente);

			if (paramPequisa.getInicioMesDataNascimento() != null && paramPequisa.getFimMesDataNascimento() != null) {
				sql.append(" AND pac.dtNascimento BETWEEN :dataInicio AND :dataLimite ");
			}
			
			if(paramPequisa.getListaOrigensAtendimentos() != null){
				sql.append(" AND att.origem IN (:origensPacComAtt) " );
			}
			
			if (!paramPequisa.getIsCount()) {
				sql.append(" ORDER BY pac.");
				sql.append(AipPacientes.Fields.NOME.toString());
				sql.append(" asc");
			}

			javax.persistence.Query query = this.createQuery(sql.toString());
			
			setParametroListaFonetica(fonemasPaciente, "fonema", query);
			setParametroListaFonetica(fonemasMae, "fonemaMae", query);
			setParametroListaFonetica(fonemasNomeSocial, "fonemaNomeSocial", query);

			if (paramPequisa.getInicioMesDataNascimento() != null && paramPequisa.getFimMesDataNascimento() != null) {
				query.setParameter("dataInicio", paramPequisa.getInicioMesDataNascimento());
				query.setParameter("dataLimite", paramPequisa.getFimMesDataNascimento());
			}

			if (!paramPequisa.getIsCount() && paramPequisa.getFirstResult() != null) {
				query.setFirstResult(paramPequisa.getFirstResult());
			}

			if (!paramPequisa.getIsCount() && paramPequisa.getMaxResults() != null) {
				query.setMaxResults(paramPequisa.getMaxResults());
			}
			
			if(paramPequisa.getListaOrigensAtendimentos() != null){
				query.setParameter("origensPacComAtt", paramPequisa.getListaOrigensAtendimentos().getOrigensAtendimento());
			}

			return query;
		}
	}


	private void pesquisaFoneticaNomeSocialPaciente(PesquisaFoneticaPaciente paramPequisa, List<String> fonemasNomeSocial, StringBuilder sql,
			Boolean pesquisouPorNomeSocialPaciente) {
		if (paramPequisa.getRespeitarOrdem() && pesquisouPorNomeSocialPaciente && !fonemasNomeSocial.isEmpty()) {
			sql.append(" AND ");
			for (int i = 0; i < (fonemasNomeSocial.size() - 1); i++) {
				sql.append(" EXISTS ");
				sql.append(" (SELECT distinct fmpac.aipPaciente.codigo FROM AipFonemaNomeSocialPacientes fmNomeSocPac, AipPosicaoFonemasNomeSocialPacientes pfmNomeSocP1");
				sql.append("              WHERE pfmNomeSocP1.id.seq = fmNomeSocPac.seq");
				sql.append("                AND fmNomeSocPac.aipPaciente.codigo = pac.codigo");
				sql.append("                AND fmNomeSocPac.aipFonemas.fonema = :fonemaNomeSocial");
				sql.append(i);
				sql.append("                AND pfmNomeSocP1.id.posicao <= (SELECT MAX(pfmp2.id.posicao) FROM AipFonemaNomeSocialPacientes fmNomeSocPac2, AipPosicaoFonemasNomeSocialPacientes pfmNomeSocP2");
				sql.append("                                      WHERE pfmNomeSocP2.id.seq = fmNomeSocPac2.seq");
				sql.append("                                        AND pfmNomeSocP2.id.posicao >= pfmp1.id.posicao");
				sql.append("                                        AND fmNomeSocPac2.aipPaciente.codigo = pac.codigo");
				sql.append("                                        AND fmNomeSocPac2.aipFonemas.fonema = :fonemaNomeSocial");
				sql.append(i+1);
				sql.append(") )"); 
				if ((i+1) < (fonemasNomeSocial.size() - 1)) {
					sql.append(" AND ");
				}
			}
		}
	}
	
	private Boolean pesquisaNomeSocial(List<String> fonemasNomeSocial, StringBuilder sql, Boolean pesquisouPorPaciente,
			Boolean pesquisouPorNomeSocialPaciente) {
		if (fonemasNomeSocial != null && !fonemasNomeSocial.isEmpty()) {
			if (!pesquisouPorPaciente) {
				sql.append(" WHERE ");
			} else {
				sql.append(" AND ");
			}
			for (int i = 0; i < fonemasNomeSocial.size(); i++) {
				sql.append(" EXISTS ");
				sql.append(" (SELECT distinct fm.aipPaciente.codigo FROM AipFonemaNomeSocialPacientes fm WHERE fm.aipPaciente.codigo = pac.codigo AND fm.aipFonemas.fonema = :fonemaNomeSocial");
				sql.append(i);
				sql.append(" )"); 
				if ((i+1) < fonemasNomeSocial.size()) {
					sql.append(" AND ");
				}
			}
			pesquisouPorNomeSocialPaciente = Boolean.TRUE;
		}
		return pesquisouPorNomeSocialPaciente;
	}


	
	public Long obterPacientesPorFonemasCount(List<String> fonemasPaciente,
			List<String> fonemasMae, Calendar dataInicio, Calendar dataLimite,
			boolean respeitarOrdem, DominioListaOrigensAtendimentos listaOrigensAtendimentos, Boolean apenasInternados) {
		javax.persistence.Query query = createPesquisaFoneticaQuery(fonemasPaciente, fonemasMae,
				dataInicio, dataLimite, respeitarOrdem, null, null, true, listaOrigensAtendimentos, apenasInternados);

		return ((Long) query.getSingleResult()).longValue();
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	@Deprecated
	private javax.persistence.Query createPesquisaFoneticaQuery(List<String> fonemasPaciente,
			List<String> fonemasMae, Calendar dataInicio, Calendar dataLimite,
			boolean respeitarOrdem, Integer firstResult, Integer maxResults,
			boolean isCount, DominioListaOrigensAtendimentos listaOrigensAtendimentos, Boolean apenasInternados) {
		StringBuilder sql = new StringBuilder(300);
		boolean pesquisouPorPaciente = false;
		boolean pesquisouPorMaePaciente = false;

		sql.append(" select ");
		if (isCount) {
			sql.append(" count(pac) ");
		} else {
			sql.append(" pac ");
		}
		sql.append(" from AipPacientes pac ");
		
		if(listaOrigensAtendimentos != null){
			sql.append(" INNER JOIN pac.aghAtendimentos att ");
		}
		if(apenasInternados){
			sql.append(" INNER JOIN att.unidadeFuncional unf ");
			sql.append(" LEFT OUTER JOIN att.especialidade esp ");
			sql.append(" LEFT OUTER JOIN att.leito lto ");
			sql.append(" LEFT OUTER JOIN att.quarto qrt ");
		}

		if (fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
			sql.append(" where ");
			for (int i = 0; i < fonemasPaciente.size(); i++) {
				sql.append(" exists ");
				sql.append(" (SELECT distinct f.aipPaciente.codigo FROM AipFonemaPacientes f WHERE f.aipPaciente.codigo = pac.codigo AND f.aipFonemas.fonema = :fonema"+i+')'); 
				if ((i+1) < fonemasPaciente.size()) {
					sql.append(" AND ");
				}
			}
			pesquisouPorPaciente = true;
		}
		
		if (fonemasMae != null && !fonemasMae.isEmpty()) {
			if (!pesquisouPorPaciente) {
				sql.append(" where ");
			} else {
				sql.append(" AND ");
			}
			for (int i = 0; i < fonemasMae.size(); i++) {
				sql.append(" exists ");
				sql.append(" (SELECT distinct fm.aipPaciente.codigo FROM AipFonemasMaePaciente fm WHERE fm.aipPaciente.codigo = pac.codigo AND fm.aipFonemas.fonema = :fonemaMae"+i+')'); 
				if ((i+1) < fonemasMae.size()) {
					sql.append(" AND ");
				}
			}
			pesquisouPorMaePaciente = true;
		}
		
		if (fonemasMae != null && !fonemasMae.isEmpty()) {
			if (!pesquisouPorPaciente) {
				sql.append(" where ");
			} else {
				sql.append(" AND ");
			}
			for (int i = 0; i < fonemasMae.size(); i++) {
				sql.append(" exists ");
				sql.append(" (SELECT distinct fm.aipPaciente.codigo FROM AipFonemasMaePaciente fm WHERE fm.aipPaciente.codigo = pac.codigo AND fm.aipFonemas.fonema = :fonemaMae"+i+")"); 
				if ((i+1) < fonemasMae.size()) {
					sql.append(" AND ");
				}
			}
			pesquisouPorMaePaciente = true;
		}
		
		if (respeitarOrdem && pesquisouPorPaciente && fonemasPaciente.size() > 1) {
			sql.append(" AND ");
			for (int i = 0; i < (fonemasPaciente.size() - 1); i++) {
				sql.append(" exists ")
				.append(" (SELECT distinct fpac.aipPaciente.codigo FROM AipFonemaPacientes fpac, AipPosicaoFonemasPacientes pfp1")
				.append("              WHERE pfp1.id.seq = fpac.seq")
				.append("                AND fpac.aipPaciente.codigo = pac.codigo")
				.append("                AND fpac.aipFonemas.fonema = :fonema").append(i)
				.append("                AND pfp1.id.posicao <= (SELECT MAX(pfp2.id.posicao) FROM AipFonemaPacientes fpac2, AipPosicaoFonemasPacientes pfp2")
				.append("                                      WHERE pfp2.id.seq = fpac2.seq")
				.append("                                        AND pfp2.id.posicao >= pfp1.id.posicao")
				.append("                                        AND fpac2.aipPaciente.codigo = pac.codigo")
				.append("                                        AND fpac2.aipFonemas.fonema = :fonema").append((i+1)).append(") )") ;
				if ((i+1) < (fonemasPaciente.size() - 1)) {
					sql.append(" AND ");
				}
			}
		}
		
		if (respeitarOrdem && pesquisouPorMaePaciente && fonemasMae.size() > 1) {
			sql.append(" AND ");
			for (int i = 0; i < (fonemasMae.size() - 1); i++) {
				sql.append(" exists ")
				.append(" (SELECT distinct fmpac.aipPaciente.codigo FROM AipFonemasMaePaciente fmpac, AipPosicaoFonemasMaePaciente pfmp1")
				.append("              WHERE pfmp1.id.seq = fmpac.seq")
				.append("                AND fmpac.aipPaciente.codigo = pac.codigo")
				.append("                AND fmpac.aipFonemas.fonema = :fonemaMae").append(i)
				.append("                AND pfmp1.id.posicao <= (SELECT MAX(pfmp2.id.posicao) FROM AipFonemasMaePaciente fmpac2, AipPosicaoFonemasMaePaciente pfmp2")
				.append("                                      WHERE pfmp2.id.seq = fmpac2.seq")
				.append("                                        AND pfmp2.id.posicao >= pfmp1.id.posicao")
				.append("                                        AND fmpac2.aipPaciente.codigo = pac.codigo")
				.append("                                        AND fmpac2.aipFonemas.fonema = :fonemaMae").append((i+1)).append(") )"); 
				if ((i+1) < (fonemasMae.size() - 1)) {
					sql.append(" AND ");
				}
			}
		}		
		
		javax.persistence.Query query = this.createQuery(sql.toString());
		
		setParametroListaFonetica(fonemasPaciente, "fonema", query);
		setParametroListaFonetica(fonemasMae, "fonemaMae", query);
		
		if (dataInicio != null && dataLimite != null) {
			sql.append(" AND pac.dtNascimento BETWEEN :dataInicio AND :dataLimite ");
		}
		
		if((listaOrigensAtendimentos != null && !apenasInternados)||(listaOrigensAtendimentos != null && apenasInternados && fonemasPaciente != null && !fonemasPaciente.isEmpty())){
			sql.append(" AND att.origem IN (:origensPacComAtt) " );
		} else if (listaOrigensAtendimentos != null && apenasInternados && (fonemasPaciente == null || fonemasPaciente.isEmpty())){
			sql.append(" WHERE att.origem IN (:origensPacComAtt) " );
		}
		
		if(apenasInternados){
			sql.append(" AND att.indPacAtendimento = :parametroPacAtendimento " );
		}
		
		if (!isCount) {
			sql.append(" ORDER BY pac.");
			sql.append(AipPacientes.Fields.NOME.toString());
			sql.append(" asc");
		}

		if (dataInicio != null && dataLimite != null) {
			query.setParameter("dataInicio", dataInicio.getTime());
			query.setParameter("dataLimite", dataLimite.getTime());
		}

		if (!isCount && firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (!isCount && maxResults != null) {
			query.setMaxResults(maxResults);
		}
		
		if(listaOrigensAtendimentos != null){
			query.setParameter("origensPacComAtt", listaOrigensAtendimentos.getOrigensAtendimento());
		}
		
		if(apenasInternados){
			query.setParameter("parametroPacAtendimento", DominioPacAtendimento.S);
		}

		return query;
	}
	
	private void setParametroListaFonetica(List<String> parametros, String nomeParametro, javax.persistence.Query query) {
		int size = 0;
		if (parametros != null && !parametros.isEmpty()) {
			for (String fonema : parametros) {
				query.setParameter(nomeParametro + size, fonema);
				size++;
			}
		}
	}
	
	public List<AipPacientes> pesquisarPacientePorCPF(Long numCPF) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CPF.toString(), numCPF));
		return executeCriteria(criteria);
	}
	
	public List<Object[]> pesquisaInformacoesPacienteEscalaCirurgias(Integer pacCodigo) {
		DetachedCriteria pacientes = DetachedCriteria.forClass(AipPacientes.class);
		
		pacientes.createAlias(AipPacientes.Fields.QUARTO.toString(), AipPacientes.Fields.QUARTO.toString(), Criteria.LEFT_JOIN);
		pacientes.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		pacientes.setProjection(Projections.projectionList().add(Projections.property(AipPacientes.Fields.LTO_LTO_ID.toString())).add( // 0
																																		// -
																																		// String
				Projections.property(AipPacientes.Fields.QUARTO.toString()+"."+AinQuartos.Fields.DESCRICAO.toString())).add( // 1
																						// -
																						// String
				Projections.property(AipPacientes.Fields.UNF_SEQ.toString())).add( // 2
																					// -
																					// Short
				Projections.property(AipPacientes.Fields.DT_ULT_INTERNACAO.toString())).add( // 3
																								// -
																								// Date
				Projections.property(AipPacientes.Fields.DT_ULT_ALTA.toString()))); // 4
																					// -
																					// Date

		return executeCriteria(pacientes);
	}
	
	public List<AipPacientes> pesquisarPacientePorNumeroCartaoSaude(BigInteger numCartaoSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString(), numCartaoSaude));
		return executeCriteria(criteria);
	}

	public List<AipPacientes> listarPacientesPorDadosRecemNascido(Integer recemNascidoGsoCodigoPaciente, Short recemNascidoGsoSequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
	
		criteria.add(Restrictions.eq(AipPacientes.Fields.RECEM_NASCIDO_GSO_CODIGO_PACIENTE.toString(), recemNascidoGsoCodigoPaciente));
		criteria.add(Restrictions.eq(AipPacientes.Fields.RECEM_NASCIDO_GSO_SEQUENCE.toString(), recemNascidoGsoSequence));

		return executeCriteria(criteria);
	}
	
	public List<AipPacientes> listarPacientesPorCodigoMae(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO_MAE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<Object[]> obterDadosProntuariosIdentificados(Date dtInicial,
			Date dtFinal, Integer codigoAreaConsiderar,
			Integer codigoAreaDesconsiderar) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);

		// WHERE PAC.CCT_CODIGO_CADASTRO = CCT.CODIGO
		criteria.createAlias("fccCentroCustosCadastro", "fcc");

		// CCT.CODIGO = NVL(:P_CCUSTO_CONSIDERAR,CCT.CODIGO)
		if (codigoAreaConsiderar != null && codigoAreaConsiderar > 0) {
			criteria.add(Restrictions.eq("fcc.codigo", codigoAreaConsiderar));
		}

		// CCT.CODIGO <> NVL(:P_CCUSTO_DESCONSIDERAR,'0')
		if (codigoAreaDesconsiderar != null && codigoAreaDesconsiderar > 0) {
			criteria.add(Restrictions.ne("fcc.codigo", codigoAreaDesconsiderar));
		} else {
			criteria.add(Restrictions.ne("fcc.codigo", 0)); // TODO: Java ->
		}

		// Data Inicial
		// AND PAC.DT_IDENTIFICACAO >= :P_DATA_INICIAL
		criteria.add(Restrictions.ge("dtIdentificacao", dtInicial));

		// Data Final
		// AND PAC.DT_IDENTIFICACAO <= NVL(:P_DATA_FINAL,SYSDATE)
		criteria.add(Restrictions.le("dtIdentificacao", dtFinal));

		// AND PAC.PRONTUARIO IS NOT NULL
		criteria.add(Restrictions.isNotNull("prontuario"));

		// AND PAC.PRONTUARIO < VALOR_MAXIMO_PRONTUARIO
		criteria.add(Restrictions.lt("prontuario", VALOR_MAXIMO_PRONTUARIO));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("fcc.codigo"))
				.add(Projections.property("fcc.descricao"))
				.add(Projections.property("prontuario"))
				.add(Projections.property("nome"))
				.add(Projections.property("dtUltInternacao"))
				.add(Projections.property("dtIdentificacao"))
				.add(Projections.property("leito.leitoID"))
				.add(Projections.property("codigo"))
				.add(Projections.property("nomeMae"))
				.add(Projections.property("dtNascimento")));

		criteria.addOrder(Order.asc("fcc.codigo"));
		criteria.addOrder(Order.asc("prontuario"));

		return executeCriteria(criteria);
	}

	public DominioSexo obterSexoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.property(AipPacientes.Fields.SEXO.toString()));
		return (DominioSexo) executeCriteriaUniqueResult(criteria);
	}

	public AipPacientes obterPacientePorCodigoEProntuario(Integer nroProntuario, Integer nroCodigo, Long cpf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		if (nroCodigo != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), nroCodigo));
		}
		if (nroProntuario != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));
		}

		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Limita a criteria pacientes para trazer apenas aquela com o respectivo
	 * numero de prontuario.
	 * 
	 * @param nroProntuario
	 *            ATENÇÃO: eh bom manter como inteiro para a camada de serviço
	 *            nao ficar tao presa com questoes de modelagem de banco de
	 *            dados.
	 * @return
	 */
	private DetachedCriteria obterCriteriaPacientesPorProntuario(Integer nroProntuario) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), AipPacientes.Fields.ATENDIMENTOS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AinInternacao.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias(AipPacientes.Fields.SERVIDOR_CADASTRO.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("SER"+ "."+ RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias(AipPacientes.Fields.SERVIDOR_RECADASTRO.toString(), "REC", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("REC"+ "."+ RapServidores.Fields.PESSOA_FISICA.toString(), "PEF", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));
		
		return criteria;
	}
	
	private DetachedCriteria obterCriteriaPacientesPorProntuarioLeito(Integer nroProntuario) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), AipPacientes.Fields.ATENDIMENTOS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LTO."+AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));

		return criteria;
	}
	
	private DetachedCriteria obterCriteriaPacientesPorProntuarioFamiliar(Integer nroProntuario, Integer nroCodigo, Integer nrProntuarioFamiliar) {
		// Essa query, além de ser mais flexivel por usar o detached criteria
		// permite utilizar o count quando necessario
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.createAlias(AipPacientes.Fields.GRUPO_FAMILIAR_PACIENTE.toString(), AipPacientes.Fields.GRUPO_FAMILIAR_PACIENTE.toString(),JoinType.LEFT_OUTER_JOIN);
		if(nroProntuario != null || nroCodigo != null){
			criteria.add(Restrictions.or(
					Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario), 
					Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), nroCodigo)));
		}
		criteria.add(Restrictions.eq(AipPacientes.Fields.COD_GRUPO_FAMILIAR_PACIENTE.toString(), nrProntuarioFamiliar));

		return criteria;
	}
	
	/*private DetachedCriteria obterCriteriaPacientesPorProntuarioPesquisaFonetica(Integer nroProntuario) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);			
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));

		return criteria;
	}*/
	
	
	public AipPacientes pesquisarPacientePorProntuarioSemDigito(Integer nroProntuario) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuarioLeito(nroProntuario);
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * @param nroProntuario
	 * @return
	 */
	/*private DetachedCriteria obterCriteriaPacientesPorProntuarioSemDigito(Integer nroProntuario) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.sqlRestriction("cast("
				+ AipPacientes.Fields.PRONTUARIO.toString() + " as varchar(8)) like '"
				+ nroProntuario + "_'"));

		return criteria;
	}*/
	
	public List<AipPacientes> pesquisarPacientePorProntuarioFamiliar(Integer nroProntuario,  Integer nroCodigo, Integer nrProntuarioFamiliar) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuarioFamiliar(nroProntuario, nroCodigo, nrProntuarioFamiliar);
		return  executeCriteria(criteria);
	}
	
	public Long pesquisarPacienteCount(Integer nroProntuario, Integer nroCodigo, Long cpf, BigInteger nroCartaoSaude)
			throws ApplicationBusinessException {
		 if (nroProntuario != null) {
			DetachedCriteria criteria = obterCriteriaPacientesPorProntuario(nroProntuario);
			return executeCriteriaCount(criteria);
		} else if (nroCodigo != null){
			DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), nroCodigo));
			return executeCriteriaCount(criteria);
		} else if (nroCartaoSaude != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
			criteria.add(Restrictions.eq(AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString(), nroCartaoSaude));
			return executeCriteriaCount(criteria);
		}else if(cpf != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
			criteria.add(Restrictions.eq(AipPacientes.Fields.CPF.toString(), cpf));
			return executeCriteriaCount(criteria);
		}return null;
	}

	public AipPacientes pesquisarPacientePorProntuario(Integer nroProntuario) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuario(nroProntuario);
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public AipPacientes pesquisarPacientePorProntuarioLeito(Integer nroProntuario) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuarioLeito(nroProntuario);
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}

	public List<AipPacientes> pesquisaPacientes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer numeroProntuario, Date periodoAltaInicio, Date periodoAltaFim, Date periodoConsultaInicio,
			Date periodoConsultaFim, Date periodoCirurgiaInicio, Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade, FccCentroCustos servico, AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito, AinLeitos ainLeito, List<String> fonemasPaciente) {
		Query query = createPesquisaPacientesQuery(numeroProntuario, periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, ainLeito, fonemasPaciente, firstResult, maxResults, orderProperty, asc, false);

		return query.list();
	}

	public Long pesquisaPacientesCount(Integer numeroProntuario, Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim, Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica, AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional, MbcProcedimentoCirurgicos procedimentoCirurgico, String leito, AinLeitos ainLeito,
			List<String> fonemasPaciente) {
		Query query = createPesquisaPacientesQuery(numeroProntuario, periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, ainLeito, fonemasPaciente, null, null, null, null, true);

		return (Long) query.uniqueResult();
	}

	public Boolean existePacientePorProntuario(Integer nroProntuario) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuario(nroProntuario);
		criteria.setProjection(Projections.rowCount());

		Long result = (Long) executeCriteriaUniqueResult(criteria);
		return result != null && result > 0;
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	private Query createPesquisaPacientesQuery(Integer numeroProntuario, Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim, Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica, AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional, MbcProcedimentoCirurgicos procedimentoCirurgico, AinLeitos ainLeito,
			List<String> fonemasPaciente, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc, Boolean isCount) {
		StringBuilder sql = new StringBuilder(200);

		sql.append(" select ");

		if (isCount) {
			sql.append("count(DISTINCT ");
		}else{
			sql.append(" DISTINCT ");
		}

		sql.append(" pac ");

		if (isCount) {
			sql.append(')');
		}

		sql.append(" from AipPacientes pac ");

		StringBuffer sqlWhere = new StringBuffer(230);

		if (numeroProntuario == null) {
			sqlWhere.append(" where pac."+AipPacientes.Fields.PRONTUARIO +" is not null ");
		} else {
			sqlWhere.append(" where pac."+AipPacientes.Fields.PRONTUARIO +" = :prontuario ");
		}

		if (StringUtils.isNotBlank(nomePaciente)) {
			sqlWhere.append(" and pac."+AipPacientes.Fields.CODIGO +" in ( ");

			int count = 0;
			if (fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
				count =  1 + fonemasPaciente.size();

				for (int i = 0; i < fonemasPaciente.size(); i++) {
					sqlWhere.append(" SELECT f");
					sqlWhere.append(i);
					sqlWhere.append(".aipPaciente."+AipPacientes.Fields.CODIGO +" ");
					sqlWhere.append(" FROM AipFonemaPacientes f");
					sqlWhere.append(i);

					sqlWhere.append(" WHERE f");
					sqlWhere.append(i);
					sqlWhere.append(".aipFonemas.fonema = :fonema");
					sqlWhere.append(i);

					sqlWhere.append("   AND f");
					sqlWhere.append(i);
					sqlWhere.append(".aipPaciente."+AipPacientes.Fields.CODIGO +" = pac."+AipPacientes.Fields.CODIGO +" ");
					
					sqlWhere.append("   AND f");
					sqlWhere.append(i);
					sqlWhere.append(".aipPaciente."+AipPacientes.Fields.CODIGO +" IN (");
				}
				sqlWhere.delete(sqlWhere.length() - 32, sqlWhere.length());// Retira
				// o
				// último
				// "   AND f.aipPaciente.codigo IN ("
			} else {
				sqlWhere.delete(sqlWhere.length() - 20, sqlWhere.length());// Retira
				// o
				// último
				// "   AND f.aipPaciente.codigo IN ("
			}

			// o
			// último
			// "   AND f.aipPaciente.codigo IN ("

			for (int i = 0; i < count - 1; i++) {
				sqlWhere.append(" ) ");
			}

		}

		if (periodoAltaInicio != null && periodoAltaFim != null) {
			sql.append(" join pac."+AipPacientes.Fields.ATENDIMENTOS +" as at ");

			sqlWhere.append(" and at."+AghAtendimentos.Fields.DTHR_FIM +" BETWEEN :periodoAltaInicio AND :periodoAltaFim ");

			if (especialidade != null) {
				sqlWhere.append(" and at."+AghAtendimentos.Fields.ESPECIALIDADE +" = :especialidade ");

				if (servico != null) {
					sqlWhere.append(" and at."+AghAtendimentos.Fields.ESPECIALIDADE +".centroCusto = :servico ");
				}
			}

			if (ainLeito != null) {
				sqlWhere.append(" and at."+AghAtendimentos.Fields.LEITO +".leitoID = :ltoLtoId ");
			}

			if (unidadeFuncional != null) {
				sqlWhere.append(" and at."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL +" = :unidadeFuncional ");
			}

			if (equipeMedica == null) {
				sqlWhere.append(" and ( at."+AghAtendimentos.Fields.ORIGEM +" = :origem1 or at."+AghAtendimentos.Fields.ORIGEM +" = :origem2 ) ");
			} else {
				sql.append(" join at."+AghAtendimentos.Fields.INTERNACAO +" int ");

				sqlWhere.append(" and at."+AghAtendimentos.Fields.ORIGEM +" = :origem ");
				sqlWhere.append(" and int.servidorProfessor.id.matricula = :serMatricula ");
				sqlWhere.append(" and int.servidorProfessor.id.vinCodigo = :serVinCodigo ");
			}
		}

		if (periodoConsultaInicio != null && periodoConsultaFim != null) {
			sql.append(" join pac."+AipPacientes.Fields.AAC_CONSULTAS +" as con ");

			sqlWhere.append(" and con."+AacConsultas.Fields.DATA_CONSULTA +" BETWEEN :periodoConsultaInicio AND :periodoConsultaFim ");

			if (unidadeFuncional != null) {
				sqlWhere.append(" and con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA +"."+AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA +".id.unfSeq = :unfSeq ");
			}

			if (especialidade != null) {
				sqlWhere.append(" and con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA +"."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE +" = :especialidade ");
			}

			if (servico != null) {
				sqlWhere.append(" and con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA +"."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE +"."+AghEspecialidades.Fields.CENTRO_CUSTO +" = :servico ");
			}

			if (equipeMedica != null) {
				sqlWhere.append(" and con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA +"."+AacGradeAgendamenConsultas.Fields.EQUIPE +" = :equipeMedica ");
			}
		}

		if (periodoCirurgiaInicio != null && periodoCirurgiaFim != null) {
			sql.append(" join pac."+AipPacientes.Fields.MBC_CIRURGIAS +" as cir ");

			sqlWhere.append(" and cir."+MbcCirurgias.Fields.DATA +" BETWEEN :periodoCirurgiaInicio AND :periodoCirurgiaFim ");
			sqlWhere.append(" and cir."+MbcCirurgias.Fields.SITUACAO+" = :situacaoCirurgia ");
			sqlWhere.append(" and cir."+MbcCirurgias.Fields.DIGITA_NOTA_SALA +" = 'S' ");

			if (unidadeFuncional != null) {
				sqlWhere.append(" and cir."+MbcCirurgias.Fields.UNIDADE_FUNCIONAL +" = :unidadeFuncional ");
			}

			if (especialidade != null) {
				sqlWhere.append(" and cir."+MbcCirurgias.Fields.ESPECIALIDADE +" = :especialidade ");
			}

			if (servico != null) {
				sqlWhere.append(" and cir."+MbcCirurgias.Fields.ESPECIALIDADE +".centroCusto = :servico ");
			}

			if (procedimentoCirurgico != null) {
				sql.append(" join cir."+MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS +" as pepc ");

				sqlWhere.append(" and pepc.procedimentoCirurgico = :procedimentoCirurgico ");

				if (especialidade != null) {
					sqlWhere.append(" and pepc.id.eprEspSeq = :eprEspSeq ");
				}
			}

			if (equipeMedica != null) {
				sql.append(" join cir."+MbcCirurgias.Fields.PROF_CIRURGIAS +" as profCir ");

				if (equipeMedica.getProfissionalResponsavel().getId().getMatricula() != null) {
					sqlWhere.append(" and profCir.id.pucSerMatricula = :pucSerMatriculaVinc ");
				}

				if (equipeMedica.getProfissionalResponsavel().getId().getVinCodigo() != null) {
					sqlWhere.append(" and profCir.id.pucSerVinCodigo = :pucSerVinCodigoVinc ");
				}

				sqlWhere.append(" and profCir.indResponsavel = :responsavel ");
			}
		}

		if (!isCount) {
			if (StringUtils.isNotBlank(orderProperty)) {
				sqlWhere.append(" ORDER BY pac.");
				sqlWhere.append(orderProperty);

				if (asc != null) {
					sqlWhere.append(asc ? " asc " : " desc ");
				}
			} else {
				sqlWhere.append(" ORDER BY pac.nome asc");
			}
		}

		sql.append(sqlWhere);
		Query query = createHibernateQuery(sql.toString());

		if (numeroProntuario != null) {
			query.setParameter("prontuario", numeroProntuario);
		}

		int size = 0;
		if (fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
			for (String fonema : fonemasPaciente) {
				query.setParameter("fonema" + size, fonema);
				size++;
			}
		}

		Calendar periodoInicio = Calendar.getInstance();
		Calendar periodoFim = Calendar.getInstance();

		if (periodoAltaInicio != null && periodoAltaFim != null) {
			periodoInicio.setTime(periodoAltaInicio);
			periodoInicio.set(Calendar.HOUR_OF_DAY, 0);
			periodoInicio.set(Calendar.MINUTE, 0);
			periodoInicio.set(Calendar.SECOND, 0);
			periodoInicio.set(Calendar.MILLISECOND, 0);

			periodoFim.setTime(periodoAltaFim);
			periodoFim.set(Calendar.HOUR_OF_DAY, 23);
			periodoFim.set(Calendar.MINUTE, 59);
			periodoFim.set(Calendar.SECOND, 59);
			periodoFim.set(Calendar.MILLISECOND, 999);

			query.setParameter("periodoAltaInicio", periodoInicio.getTime());
			query.setParameter("periodoAltaFim", periodoFim.getTime());

			if (unidadeFuncional != null) {
				query.setParameter("unidadeFuncional", unidadeFuncional);
			}

			if (especialidade != null) {
				query.setParameter("especialidade", especialidade);

				if (servico != null) {
					query.setParameter("servico", servico);
				}
			}

			if (ainLeito != null) {
				query.setParameter("ltoLtoId", ainLeito.getLeitoID());
			}

			if (equipeMedica == null) {
				query.setParameter("origem1", DominioOrigemAtendimento.U);
				query.setParameter("origem2", DominioOrigemAtendimento.I);
			} else {
				query.setParameter("origem", DominioOrigemAtendimento.I);

				query.setParameter("serMatricula", equipeMedica.getProfissionalResponsavel()
						.getId().getMatricula());
				query.setParameter("serVinCodigo", equipeMedica.getProfissionalResponsavel()
						.getId().getVinCodigo());
			}
		}

		if (periodoConsultaInicio != null && periodoConsultaFim != null) {
			periodoInicio.setTime(periodoConsultaInicio);
			periodoInicio.set(Calendar.HOUR_OF_DAY, 0);
			periodoInicio.set(Calendar.MINUTE, 0);
			periodoInicio.set(Calendar.SECOND, 0);
			periodoInicio.set(Calendar.MILLISECOND, 0);

			periodoFim.setTime(periodoConsultaFim);
			periodoFim.set(Calendar.HOUR_OF_DAY, 23);
			periodoFim.set(Calendar.MINUTE, 59);
			periodoFim.set(Calendar.SECOND, 59);
			periodoFim.set(Calendar.MILLISECOND, 999);

			query.setParameter("periodoConsultaInicio", periodoInicio.getTime());
			query.setParameter("periodoConsultaFim", periodoFim.getTime());

			if (unidadeFuncional != null) {
				query.setParameter("unfSeq", unidadeFuncional.getSeq());
			}

			if (especialidade != null) {
				query.setParameter("especialidade", especialidade);
			}

			if (servico != null) {
				query.setParameter("servico", servico);
			}

			if (equipeMedica != null) {
				query.setParameter("equipeMedica", equipeMedica);
			}
		}

		if (periodoCirurgiaInicio != null && periodoCirurgiaFim != null) {
			periodoInicio.setTime(periodoCirurgiaInicio);
			periodoInicio.set(Calendar.HOUR_OF_DAY, 0);
			periodoInicio.set(Calendar.MINUTE, 0);
			periodoInicio.set(Calendar.SECOND, 0);
			periodoInicio.set(Calendar.MILLISECOND, 0);

			periodoFim.setTime(periodoCirurgiaFim);
			periodoFim.set(Calendar.HOUR_OF_DAY, 23);
			periodoFim.set(Calendar.MINUTE, 59);
			periodoFim.set(Calendar.SECOND, 59);
			periodoFim.set(Calendar.MILLISECOND, 999);

			query.setParameter("periodoCirurgiaInicio", periodoInicio.getTime());
			query.setParameter("periodoCirurgiaFim", periodoFim.getTime());

			query.setParameter("situacaoCirurgia", DominioSituacaoCirurgia.RZDA);

			if (unidadeFuncional != null) {
				query.setParameter("unidadeFuncional", unidadeFuncional);
			}

			if (especialidade != null) {
				query.setParameter("especialidade", especialidade);
			}

			if (servico != null) {
				query.setParameter("servico", servico);
			}

			if (procedimentoCirurgico != null) {
				query.setParameter("procedimentoCirurgico", procedimentoCirurgico);

				if (especialidade != null) {
					query.setParameter("eprEspSeq", especialidade.getSeq());
				}
			}

			if (equipeMedica != null) {
				if (equipeMedica.getProfissionalResponsavel().getId().getMatricula() != null) {
					query.setParameter("pucSerMatriculaVinc", equipeMedica
							.getProfissionalResponsavel().getId().getMatricula());
				}

				if (equipeMedica.getProfissionalResponsavel().getId().getVinCodigo() != null) {
					query.setParameter("pucSerVinCodigoVinc", equipeMedica
							.getProfissionalResponsavel().getId().getVinCodigo());
				}

				query.setParameter("responsavel", DominioSimNao.S);
			}
		}

		if (!isCount && firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (!isCount && maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query;
	}
	
	public String pesquisarNomePaciente(Integer prontuario) {
		AipPacientes pac = null;
		String nomePac = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		pac = (AipPacientes) executeCriteriaUniqueResult(criteria);
		if (pac != null) {
			nomePac = pac.getNome();
		}
		return nomePac;
	}

	public List<AipPacientes> pesquisarPacientesPorListaProntuario(Collection<Integer> nroProntuarios) throws ApplicationBusinessException {
		DetachedCriteria criteria = obterCriteriaPacientesPorListaProntuario(nroProntuarios);
		return executeCriteria(criteria);
	}

	/**
	 * Limita a criteria pacientes para trazer apenas aquela com o respectivo
	 * numero de prontuario.
	 * 
	 * @param nroProntuario
	 *            ATENÇÃO: eh bom manter como inteiro para a camada de serviço
	 *            nao ficar tao presa com questoes de modelagem de banco de
	 *            dados.
	 * @return
	 */
	private DetachedCriteria obterCriteriaPacientesPorListaProntuario(
			Collection<Integer> nroProntuarios) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.in(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuarios));

		return criteria;
	}
	
	public List<AipPacientes> pesquisaProntuarioPaciente(Integer firstResult, Integer maxResults,
			Integer codigo, String nome, Integer prontuario) {
		DetachedCriteria criteria = createProntuarioPacienteCriteria(codigo, nome, prontuario);

		return executeCriteria(criteria, firstResult, maxResults, "nome", true);
	}

	private DetachedCriteria createProntuarioPacienteCriteria(Integer codigo, String nome,
			Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AipPacientes.Fields.NOME.toString(), nome,
					MatchMode.ANYWHERE));
		}

		if (prontuario != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}

		return criteria;
	}
	
	public List<AipPacientes> pesquisarSituacaoProntuario(Integer firstResult, Integer maxResults,
			Integer codigo, String nome, Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito) {
		DetachedCriteria criteria = createSituacaoProntuarioCriteria(codigo, nome, prontuario,
				indPacienteVip, indPacProtegido, consideraDigito);
		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		criteria.addOrder(Order.asc(AipPacientes.Fields.PRONTUARIO.toString()));

		return executeCriteria(criteria, firstResult, maxResults, null, false);
	}

	public Long pesquisaProntuarioPacienteCount(Integer codigo, String nome, Integer prontuario) {
		return executeCriteriaCount(createProntuarioPacienteCriteria(codigo, nome, prontuario));
	}
	
	public Long pesquisarSituacaoProntuarioCount(Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip, DominioSimNao indPacProtegido,
			Boolean consideraDigito) {
		DetachedCriteria criteria = createSituacaoProntuarioCriteria(codigo, nome, prontuario,
				indPacienteVip, indPacProtegido, consideraDigito);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createSituacaoProntuarioCriteria(Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip, DominioSimNao indPacProtegido,
			Boolean consideraDigito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AipPacientes.Fields.NOME.toString(), nome,
					MatchMode.ANYWHERE));
		}

		if (prontuario != null && consideraDigito) {
			// criteria.add(Restrictions.sqlRestriction("cast("+AipPacientes.Fields.PRONTUARIO.toString()+" as varchar(8)) like '%"
			// + prontuario + "%'"));
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
			criteria.add(Restrictions.gt(AipPacientes.Fields.PRONTUARIO.toString(), 0));
		}

		if (prontuario != null && !consideraDigito) {
			criteria.add(Restrictions.sqlRestriction("cast("
					+ AipPacientes.Fields.PRONTUARIO.toString() + " as varchar(8)) like '"
					+ prontuario + "_'"));
			criteria.add(Restrictions.gt(AipPacientes.Fields.PRONTUARIO.toString(), 0));
		}

		if (indPacienteVip != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.IND_PACIENTE_VIP.toString(),
					indPacienteVip));
		}

		if (indPacProtegido != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.IND_PAC_PROTEGIDO.toString(),
					indPacProtegido));
		}

		// Busca somente prontuarios maiores que 0 (existem prontuarios nulos no
		// banco de dados)
		criteria.add(Restrictions.gt(AipPacientes.Fields.PRONTUARIO.toString(), 0));

		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<AipPacientes> pesquisarPacientesPorProntuario(String strPesquisa) {
		List<AipPacientes> li = null;

		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para
		// tipos numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException).
		StringBuilder hql = new StringBuilder(100);
		hql.append("from AipPacientes p ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
			hql.append("where str(p.prontuario) like '%").append(strPesquisa).append("%' ");
		}

		hql.append("order by p.prontuario");
		li = createHibernateQuery(hql.toString()).list();

		return li;
	}

	@SuppressWarnings("unchecked")
	public List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		// Os FetchModes abaixo foram colocado para melhorar a performance da
		// query. Se não usados FetchMode é feito um SELECT para cada registro
		// de paciente retornado para buscar seus dados clinicos e dados de CNS.
		// criteria.setFetchMode("aipPacientesDadosCns", FetchMode.JOIN);
		// criteria.setFetchMode("aipPacienteDadoClinicos", FetchMode.JOIN);

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			StringBuilder hql = new StringBuilder();
			strPesquisa = strPesquisa.toUpperCase();
			hql.append(" (cast(").append(AipPacientes.Fields.PRONTUARIO.toString())
					// .append(" as varchar(10)) like '%").append(strPesquisa).append("%' ")
					.append(" as varchar(10)) = '").append(strPesquisa).append("' ")
					.append("or cast(").append(AipPacientes.Fields.CODIGO.toString())
					// .append(" as varchar(10)) like '%").append(strPesquisa).append("%') ");
					.append(" as varchar(10)) = '").append(strPesquisa).append("') ");

			criteria.add(Restrictions.sqlRestriction(hql.toString()));
			criteria.addOrder(Order.asc(AipPacientes.Fields.CODIGO.toString()));
		}

		// criteria.addOrder(Order.asc(AipPacientes.Fields.PRONTUARIO.toString()));
		
		return executeCriteria(criteria, 0, 100, null);
	}

	@SuppressWarnings("unchecked")
	public List<AipPacientes> pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
			String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		// Os FetchModes abaixo foram colocado para melhorar a performance da
		// query. Se não usados FetchMode é feito um SELECT para cada registro
		// de paciente retornado para buscar seus dados clinicos e dados de CNS.
		// criteria.setFetchMode("aipPacientesDadosCns", FetchMode.JOIN);
		// criteria.setFetchMode("aipPacienteDadoClinicos", FetchMode.JOIN);

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			StringBuilder hql = new StringBuilder();
			strPesquisa = strPesquisa.toUpperCase();
			hql.append(" (cast(").append(AipPacientes.Fields.PRONTUARIO.toString())
					// .append(" as varchar(10)) like '%").append(strPesquisa).append("%' ")
					.append(" as varchar(10)) = '").append(strPesquisa).append("' ")
					.append("or cast(").append(AipPacientes.Fields.CODIGO.toString())
					// .append(" as varchar(10)) like '%").append(strPesquisa).append("%') ");
					.append(" as varchar(10)) = '").append(strPesquisa).append("') ");

			criteria.add(Restrictions.sqlRestriction(hql.toString()));
			criteria.addOrder(Order.asc(AipPacientes.Fields.CODIGO.toString()));
		}
		criteria.add(Restrictions.isNotNull("prontuario"));

		return executeCriteria(criteria, 0, 100, null);
	}

	@SuppressWarnings("unchecked")
	public List<AipPacientes> obterPacientesPorProntuarioOuCodigo(String strPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		DetachedCriteria criteriaLike = DetachedCriteria.forClass(AipPacientes.class);

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			StringBuilder hql = new StringBuilder();
			strPesquisa = strPesquisa.toUpperCase();
			hql.append(" (cast(").append(AipPacientes.Fields.CODIGO.toString())
					.append(" as varchar(10)) = '").append(strPesquisa).append("') ");

			criteria.add(Restrictions.sqlRestriction(hql.toString()));
			criteria.addOrder(Order.asc(AipPacientes.Fields.CODIGO.toString()));
		}

		List<AipPacientes> list = executeCriteria(criteria, 0, 100, null);
		// se tiver um resultado (e somente um), retorna esse registro, conforme
		// o descrito na wiki da suggestion
		if (list.size() == 1) {
			return list;
		}

		// se não encontrou nada (ou mais de um), segue para pesquisa por like
		// no campo de prontuário.
		if (strPesquisa != null && !"".equals(strPesquisa)) {
			StringBuilder hql = new StringBuilder();
			strPesquisa = strPesquisa.toUpperCase();
			hql.append(" (cast(").append(AipPacientes.Fields.PRONTUARIO.toString())
					.append(" as varchar(10)) like '").append(strPesquisa).append("') ");

			criteriaLike.add(Restrictions.sqlRestriction(hql.toString()));
			criteriaLike.addOrder(Order.asc(AipPacientes.Fields.PRONTUARIO.toString()));
		}
		
		return executeCriteria(criteriaLike, 0, 100, null);
	}
	
	public AipPacientes obterPacientePorCodigoDtUltAltaEProntuarioNotNull(Integer codigoPaciente, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AipPacientes.Fields.DT_ULT_ALTA.toString(), data));
		criteria.add(Restrictions.isNotNull(AipPacientes.Fields.PRONTUARIO.toString()));

		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}

	public List<AipPacientes> recuperarPacientes(int firstResult, int maxResults, String orderProperty, boolean asc) {
		return executeCriteria(criarCriteriaPacientes(), firstResult, maxResults, orderProperty, asc);
	}

	public Long recuperarPacientesCount() {
		return executeCriteriaCount(criarCriteriaPacientes());
	}

	private DetachedCriteria criarCriteriaPacientes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		return criteria;
	}
	
	public List<AipPacientes> pesquisarPacientePorNomeDtNacimentoNomeMae(String nome, Date dataNacimento, String nomeMae){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		String normalizadoNomePaciente = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		criteria.add(Restrictions.sqlRestriction("TRANSLATE({alias}.nome,'áàâãäåaaaÁÂÃÄÅAAAÀéèêëeeeeeEEEÉEEÈìíîïìiiiÌÍÎÏÌIIIóôõöoooòÒÓÔÕÖOOOùúûüuuuuÙÚÛÜUUUUçÇñÑýÝ',"
				+ "'aaaaaaaaaAAAAAAAAAeeeeeeeeeEEEEEEEiiiiiiiiIIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnNyY') ILIKE '" + normalizadoNomePaciente.toUpperCase() + "'"));
		String normalizadoNomeMae = Normalizer.normalize(nomeMae, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		criteria.add(Restrictions.sqlRestriction("TRANSLATE({alias}.nome_Mae,'áàâãäåaaaÁÂÃÄÅAAAÀéèêëeeeeeEEEÉEEÈìíîïìiiiÌÍÎÏÌIIIóôõöoooòÒÓÔÕÖOOOùúûüuuuuÙÚÛÜUUUUçÇñÑýÝ',"
				+ "'aaaaaaaaaAAAAAAAAAeeeeeeeeeEEEEEEEiiiiiiiiIIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnNyY') ILIKE '" + normalizadoNomeMae.toUpperCase() + "'"));	
		criteria.add(Restrictions.eq(AipPacientes.Fields.DATA_NASCIMENTO.toString(), dataNacimento));
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa utilizada na geração da solicitação externa de exames. 
	 * Para recém-nascidos a data de nascimento contém hora, sendo necessário considerar as horas na data final
	 *  
	 * @param nome
	 * @param dataNacimento
	 * @param nomeMae
	 * @return
	 */
	public List<AipPacientes> pesquisarPacientePorNomeDtNacimentoComHoraNomeMae(String nome, Date dataNacimento, String nomeMae){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nome));
		criteria.add(Restrictions.eq(AipPacientes.Fields.NOMEMAE.toString(), nomeMae));
		
		Date dtInicio = DateUtil.truncaData(dataNacimento);
		Date dtFim = DateUtil.obterDataComHoraFinal(dataNacimento);
		
		criteria.add(Restrictions.or(Restrictions.gt(AipPacientes.Fields.DATA_NASCIMENTO.toString(), dtInicio),
				Restrictions.eq(AipPacientes.Fields.DATA_NASCIMENTO.toString(), dtInicio)));
		criteria.add(Restrictions.or(Restrictions.lt(AipPacientes.Fields.DATA_NASCIMENTO.toString(), dtFim),
				Restrictions.eq(AipPacientes.Fields.DATA_NASCIMENTO.toString(), dtFim)));
				 
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisar pacientes pelo nome e número do cartão saúde
	 * @param nome
	 * @param numCartaoSaude
	 * @return
	 */
	public List<AipPacientes> pesquisarPacientePorNomeENumeroCartaoSaude(String nome, BigInteger numCartaoSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nome));
		criteria.add(Restrictions.eq(AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString(), numCartaoSaude));
		return executeCriteria(criteria);
	}
	

	public List<AipPacientes> pesquisarPacientePorCartaoSaude(BigInteger nroCartaoSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.NRO_CARTAO_SAUDE.toString(), nroCartaoSaude));
		return executeCriteria(criteria);
	}
	
	public List<AipPacientes> obterPacientePorCartaoCpfCodigoPronturario(Integer nroProntuario, 
		Integer nroCodigo, Long cpf,BigInteger nroCartaoSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		if(nroProntuario != null){
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));
		}
		if(nroCodigo != null){
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), nroCodigo));
		}
		if(cpf != null){
			criteria.add(Restrictions.eq(AipPacientes.Fields.CPF.toString(), cpf));
		}
		if(nroCartaoSaude != null){
			criteria.add(Restrictions.eq(AipPacientes.Fields.NRO_CARTAO_SAUDE.toString(), nroCartaoSaude));
		}
		return executeCriteria(criteria);
	}
	
	public List<DadosAdicionaisVO> obterDadosAdicionais(Integer pacCodigo){
		StringBuilder hql=new StringBuilder(450);		
		
		hql.append("select new br.gov.mec.aghu.paciente.vo.DadosAdicionaisVO(");
		hql.append("  rna.").append(McoRecemNascidos.Fields.APGAR1.toString())
		   .append(" ,rna.").append(McoRecemNascidos.Fields.APGAR5.toString())
		   .append(" ,rna.").append(McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString())
		   .append(" ,efr.").append(McoExameFisicoRns.Fields.TEMPERATURA.toString())
		   .append(" ,efr.").append(McoExameFisicoRns.Fields.IG_FINAL.toString())
		   .append(" ,gso.").append(McoGestacoes.Fields.IG_ATUAL_SEMANAS.toString())
		   .append(" ,atd.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
		
		hql.append(") from ")
		.append(McoExameFisicoRns.class.getName()).append(" efr, ")
		.append(AghAtendimentos.class.getName()).append(" atd, ")
		.append(McoNascimentos.class.getName()).append(" nas, ")
		.append(McoGestacoes.class.getName()).append(" gso, ")
		.append(McoRecemNascidos.class.getName()).append(" rna ");
		
		hql.append(" where rna.").append(McoRecemNascidos.Fields.PAC_CODIGO.toString()).append(" = :p_pac_codigo ");
		hql.append(" and gso.").append(McoGestacoes.Fields.CODIGO_PACIENTE.toString()).append(" = rna.").append(McoRecemNascidos.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and gso.").append(McoGestacoes.Fields.SEQUENCE.toString()).append(" = rna.").append(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString());
		hql.append(" and nas.").append(McoNascimentos.Fields.GSO_SEQP.toString()).append(" = gso.").append(McoGestacoes.Fields.SEQUENCE.toString());
		hql.append(" and nas.").append(McoNascimentos.Fields.GSO_PAC_CODIGO.toString()).append(" = gso.").append(McoGestacoes.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and atd.").append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()).append(" = gso.").append(McoGestacoes.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and atd.").append(AghAtendimentos.Fields.GSO_SEQP.toString()).append(" =  gso.").append(McoGestacoes.Fields.SEQUENCE.toString());
		hql.append(" and atd.").append(AghAtendimentos.Fields.INT_SEQ.toString()).append(" IS NOT NULL ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.DTHR_INICIO.toString()).append(" <= nas.").append(McoNascimentos.Fields.DTHR_NASCIMENTO.toString());
		hql.append(" and (atd.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" IS NULL ");
		hql.append("        or atd.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" >= nas.")
							  .append(McoNascimentos.Fields.DTHR_NASCIMENTO.toString())
			   .append(" ) ");
		hql.append(" and efr.").append(McoExameFisicoRns.Fields.GESTACOES_CODIGO_PACIENTE.toString()).append(" = rna.").append(McoRecemNascidos.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and efr.").append(McoExameFisicoRns.Fields.GESTACOES_SEQUENCE.toString()).append(" = rna.").append(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString());
		hql.append(" and efr.").append(McoExameFisicoRns.Fields.SEQP.toString()).append(" = rna.").append(McoRecemNascidos.Fields.SEQP.toString());
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("p_pac_codigo", pacCodigo);
		return (List<DadosAdicionaisVO>) query.list();
	}

	public String obterNomeDoPacientePorCodigo(Integer pacCodigo) {
		final DetachedCriteria criteria = criteriaPacientesPorCodigo(pacCodigo);
		criteria.setProjection(Projections.property(AipPacientes.Fields.NOME.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * ORADB: CURSOR C_HABILITA_ENC_EXAME.C_PAC #24894
	 */
	public List<Integer> obtemCodPacienteComInternacaoNaoNulaEOutrasDatas(Integer pacCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(AipPacientes.Fields.DT_ULT_INTERNACAO.toString()));
		
		criteria.add(Restrictions.or( Restrictions.isNull(AipPacientes.Fields.DT_ULT_ALTA.toString()), 
									  Restrictions.and(Restrictions.isNotNull(AipPacientes.Fields.DT_ULT_ALTA.toString()),
											  			Restrictions.ltProperty( AipPacientes.Fields.DT_ULT_ALTA.toString(), 
											  									 AipPacientes.Fields.DT_ULT_INTERNACAO.toString()
											  									)
											          )
									 )
					);
		
		criteria.setProjection(Projections.property(AipPacientes.Fields.CODIGO.toString()));
		
		return executeCriteria(criteria, 0, 1, AipPacientes.Fields.CODIGO.toString(), true);
		/*  select 1
	  from aip_pacientes
	 where codigo							=	c_pac_codigo
	 	 and dt_ult_internacao 	is not null
		 and (dt_ult_alta 			is null or (dt_ult_alta is not null and dt_ult_alta  < dt_ult_internacao));	     
		 */
	}
	
	/**
	 * ORADB AELK_EXAME_PCT.AELC_TEM_PCT_NEO
	 * 
	 * Retorna true quando paciente menor de 4 meses (neo) já possui um exame PCT (deve solicitar somente um)
	 *  
	 * @return
	 */
	public Boolean verificarPacienteNeonatalPossuiExamePCT(Integer atdSeq,
			String exaSigla, Integer manSeq, Short ufeUnfSeq,
			String situacaoCancelado, Integer meses) {	

		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		criteria.createAlias("PAC." + AipPacientes.Fields.ATENDIMENTOS.toString(), "ATD");	

		if(isOracle()) {		
			criteria.add(Restrictions.sqlRestriction(
					" trunc(months_between(sysdate, {alias}.dt_nascimento)) < " + meses));
		} else {
			criteria.add(Restrictions.sqlRestriction(
					" extract(year FROM age(current_date, {alias}.dt_nascimento)) < " + meses));
		}
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));	

		DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteriaIse.createCriteria("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteriaIse.setProjection(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));	

		criteriaIse.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteriaIse.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), exaSigla));
		criteriaIse.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), manSeq));
		criteriaIse.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), ufeUnfSeq));
		criteriaIse.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacaoCancelado));	

		criteria.add(Subqueries.exists(criteriaIse));

		return executeCriteriaCount(criteria) > 0L;
	}	

	/**
	 * Buscar os dados da Gestante tendo como parâmetro o Prontuário
	 * 
	 * Web Service #36616
	 * 
	 * @param nroProntuario
	 * @return
	 */
	public AipPacientes obterDadosGestantePorProntuario(final Integer nroProntuario) {
		DetachedCriteria criteria = this.criteriaPacientesPorProntuario(nroProntuario);
		return (AipPacientes) super.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #2197 C1 
 	 * @param paramsNutricaoEnteral
	 * @return
	 */
	public List<ContaNutricaoEnteralVO> obterContaNutricaoEnteral(List<Integer> paramsNutricaoEnteral){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "pac");
		criteria.createAlias("pac." + AipPacientes.Fields.INTERNACOES.toString(), "int", JoinType.INNER_JOIN);
		criteria.createAlias("int." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(),  "coi", JoinType.INNER_JOIN);
		criteria.createAlias("coi." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "fch" , JoinType.INNER_JOIN);
		criteria.createAlias("fch." + FatContasHospitalares.Fields.ITENS_CONTA_HOSPITALAR.toString(), "ich" , JoinType.INNER_JOIN );
		
		criteria.add(Restrictions.in("ich." + FatItemContaHospitalar.Fields.PHI_SEQ.toString(),paramsNutricaoEnteral));
		criteria.add(Restrictions.in("ich." + FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoItenConta.V, DominioSituacaoItenConta.P)));
		criteria.add(Restrictions.in("fch." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoConta.E, DominioSituacaoConta.F)));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("fch."+ FatContasHospitalares.Fields.SEQ.toString()), ContaNutricaoEnteralVO.Fields.SEQ.toString());
		projectionList.add(Projections.property("fch."+ FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString()), ContaNutricaoEnteralVO.Fields.IND_AUTORIZADO_SMS.toString());
		projectionList.add(Projections.property("fch."+ FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()), ContaNutricaoEnteralVO.Fields.DT_ALTA_ADMINISTRATIVA.toString());
		projectionList.add(Projections.property("fch."+ FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()), ContaNutricaoEnteralVO.Fields.DT_INT_ADMINISTRATIVA.toString());
		projectionList.add(Projections.property("pac."+ AipPacientes.Fields.PRONTUARIO.toString()), ContaNutricaoEnteralVO.Fields.PRONTUARIO.toString());
		projectionList.add(Projections.property("pac."+ AipPacientes.Fields.NOME.toString()), ContaNutricaoEnteralVO.Fields.NOME.toString());
		
		projectionList.add(Projections.groupProperty("fch."+ FatContasHospitalares.Fields.SEQ.toString()));
		projectionList.add(Projections.groupProperty("fch."+ FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString()));
		projectionList.add(Projections.groupProperty("fch."+ FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()));
		projectionList.add(Projections.groupProperty("fch."+ FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()));
		projectionList.add(Projections.groupProperty("pac."+ AipPacientes.Fields.PRONTUARIO.toString()));
		projectionList.add(Projections.groupProperty("pac."+ AipPacientes.Fields.NOME.toString()));	
		projectionList.add(Projections.groupProperty("fch."+ FatContasHospitalares.Fields.SIA_MSP_SEQ.toString()));
		
		
		projectionList.add(Projections.sum("ich." + FatItemContaHospitalar.Fields.QUANTIDADE_REALIZADA.toString()), ContaNutricaoEnteralVO.Fields.QUANTIDADES_REALIZADAS.toString());
		criteria.setProjection(projectionList);
	
		criteria.addOrder(Order.asc("fch."+ FatContasHospitalares.Fields.SEQ));
		criteria.setResultTransformer(Transformers.aliasToBean(ContaNutricaoEnteralVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta Pacientes pelo Prontuario ou pelo Codigo
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param aipPacientes
	 * @return
	 */
	public List<AipPacientes> listarConsultasParaLiberarObito(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes aipPacientes) {
		DetachedCriteria criteria = getCriteriaProntuarioOuCodigo(aipPacientes);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		}

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * 
	 * @param aipPacientes
	 * @return
	 */
	public Long listarConsultasParaLiberarObitoCount(AipPacientes aipPacientes) {
		DetachedCriteria criteria = getCriteriaProntuarioOuCodigo(aipPacientes);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Criteria para listar os pacientes por prontuario ou codigo
	 * 
	 * @param filtro
	 * @return
	 */
	private DetachedCriteria getCriteriaProntuarioOuCodigo(AipPacientes filtro) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientes.class);

		criteria.add(Restrictions.isNotNull(AipPacientes.Fields.DT_OBITO
				.toString()));

		if (filtro != null && !("").equals(filtro)) {

			if (filtro.getProntuario() != null
					&& StringUtils.isNumeric(filtro.getProntuario().toString())) {

				criteria.add(Restrictions.eq(
						AipPacientes.Fields.PRONTUARIO.toString(),
						filtro.getProntuario()));
			}

			if (filtro.getCodigo() != null
					&& StringUtils.isNumeric(filtro.getCodigo().toString())) {

				criteria.add(Restrictions.eq(
						AipPacientes.Fields.CODIGO.toString(),
						filtro.getCodigo()));
			}

		}

		return criteria;
	}
	
	public Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro) {

		// PROJECTION COUNT
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) "); // COUNT

		// Critérios gerais
		sql.append(criarSqlPesquisarPacientesCCIH(filtro));

		// Executa consulta
		List<Number> resultadoConsulta = createNativeQuery(sql.toString()).getResultList();

		for (Number count : resultadoConsulta) {
			return count.longValue();
		}

		return 0L;
	}

	public List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroListaPacienteCCIHVO filtro) {
		StringBuilder sql = new StringBuilder(1500);
		// PROJECTIONS
		sql.append("SELECT PAC.CODIGO, " // 0: PROJECTION ADICIONAL
				+ "PAC.PRONTUARIO, " // 1: PROJECTION
				+ "PAC.NOME, " // 2: PROJECTION
				+ "ULTIMAINTERNACAO.SEQ, " // 3: PROJECTION
				+ "ULTIMAINTERNACAO.UNF_SEQ, " // 4: PROJECTION
				+ "UNF.DESCRICAO, " // 5: PROJECTION
				+ "ULTIMAINTERNACAO.LTO_LTO_ID, " // 6: PROJECTION
				+ "(SELECT PES.NOME FROM AGH.AGH_ATENDIMENTOS ATD "
				+ "INNER JOIN AGH.AGH_PROF_ESPECIALIDADES PEQ ON PEQ.SER_MATRICULA = ATD.PRE_SER_MATRICULA "
				+ "AND PEQ.SER_VIN_CODIGO = ATD.PRE_SER_VIN_CODIGO "
				+ "AND PEQ.ESP_SEQ = ATD.PRE_ESP_SEQ "
				+ "INNER JOIN AGH.RAP_SERVIDORES SER ON PEQ.SER_MATRICULA = ATD.PRE_SER_VIN_CODIGO "
				+ "AND PEQ.SER_VIN_CODIGO = ATD.PRE_SER_VIN_CODIGO "
				+ "INNER JOIN AGH.RAP_PESSOAS_FISICAS PES ON SER.PES_CODIGO = PES.CODIGO "
				+ "WHERE ATD.ORIGEM IN ('U', 'I', 'N') "
				+ "AND ATD.SEQ = ULTIMAINTERNACAO.SEQ) NOMEEQUIPE, " // 7: PROJECTION
				+ "(CASE WHEN (SELECT COUNT(*) FROM AGH.AGH_ATENDIMENTOS ATD3 "
				+ "WHERE ATD3.ORIGEM IN ('U', 'I', 'N') "
				+ "AND ATD3.IND_PAC_ATENDIMENTO = 'S' "
				+ "AND PAC.CODIGO = ATD3.PAC_CODIGO)>0 "
				+ "THEN 'S' ELSE 'N' END) IND_PACIENTE_INTERNADO, " // 8: PROJECTION
				+ "(CASE WHEN (SELECT COUNT(*) FROM AGH.MCI_MVTO_MEDIDA_PREVENTIVAS MMP "
				+ "WHERE MMP.IND_CONFIRMACAO_CCI = 'N' "
				+ "AND MMP.PAC_CODIGO = PAC.CODIGO )>0 "
				+ "OR (SELECT COUNT(*) FROM AGH.MCI_MVTO_INFECCAO_TOPOGRAFIAS MIT "
				+ "WHERE MIT.IND_CONFIRMACAO_CCI = 'N' "
				+ "AND MIT.PAC_CODIGO = PAC.CODIGO )>0 "
				+ "OR (SELECT COUNT(*) FROM AGH.MCI_MVTO_PROCEDIMENTO_RISCOS MRI "
				+ "WHERE MRI.IND_CONFIRMACAO_CCI = 'N' "
				+ "AND MRI.PAC_CODIGO = PAC.CODIGO)>0 "
				+ "THEN 'S' ELSE 'N' END) IND_NOTIF_NAO_CONFERIDAS, " // 9: PROJECTION
				+ "(CASE WHEN (SELECT COUNT(*) FROM AGH.MCI_NOTIFICACAO_GMR NGM "
				+ "WHERE NGM.IND_NOTIFICACAO_ATIVA = 'S' "
				+ "AND NGM.PAC_CODIGO = PAC.CODIGO )>0 "
				+ "THEN 'S' ELSE 'N' END) IND_PACIENTE_GMR "); // 10: PROJECTION

		// Critérios gerais
		sql.append(criarSqlPesquisarPacientesCCIH(filtro));
		
		// Executa consulta
		List<ListaPacientesCCIHVO> resultado = new ArrayList<ListaPacientesCCIHVO>();
		javax.persistence.Query query = this.createNativeQuery(sql.toString());
		
		// Controla paginação
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		// Executa consulta
		List<Object[]> resultadoConsulta = query.getResultList();
		for (Object[] object : resultadoConsulta) {
			resultado.add(popularItemListaPacientesCCIHVO(object));
		}

		return resultado;
	}

	private ListaPacientesCCIHVO popularItemListaPacientesCCIHVO(Object[] object) {
		ListaPacientesCCIHVO vo = new ListaPacientesCCIHVO();
		if (object[0] != null) { // PAC.CODIGO
			vo.setCodigo(((Number) object[0]).intValue());
		}
		if (object[1] != null) { // PAC.PRONTUARIO
			vo.setProntuario(((Number) object[1]).intValue());
		}
		if (object[2] != null) { // PAC.NOME
			vo.setNome(String.valueOf(object[2]));
		}
		if (object[3] != null) {// ULTIMAINTERNACAO.SEQ
			vo.setUltimaInternacaoSeq(((Number) object[3]).intValue());
		}
		if (object[4] != null) { // ULTIMAINTERNACAO.UNF_SEQ
			vo.setUltimaInternacaoUnfSeq(((Number) object[4]).shortValue());
		}
		if (object[5] != null) { // UNF.DESCRICAO
			vo.setUltimaInternacaoUnfDescricao(String.valueOf(object[5]));
		}
		if (object[6] != null) { // ULTIMAINTERNACAO.LTO_LTO_ID
			vo.setUltimaInternacaoLeitoID(String.valueOf(object[6]));
		}
		// Evita a violação de PMD NPathComplexity
		popularItemListaPacientesCCIHVONotificacoes(object, vo);
		return vo;
	}

	private void popularItemListaPacientesCCIHVONotificacoes(Object[] object, ListaPacientesCCIHVO vo) {
		if (object[7] != null) { // NOMEEQUIPE
			vo.setNomeEquipe(String.valueOf(object[7]));
		}
		if (object[8] != null) { // IND_PACIENTE_INTERNADO
			vo.setIndPacienteInternado(StringUtils.equalsIgnoreCase(String.valueOf(object[8]), DominioSimNao.S.toString()));
		}
		if (object[9] != null) { // IND_NOTIF_NAO_CONFERIDAS
			vo.setIndNotifNaoConferidas(StringUtils.equalsIgnoreCase(String.valueOf(object[9]), DominioSimNao.S.toString()));
		}
		if (object[10] != null) { // IND_PACIENTE_GMR
			vo.setIndPacienteGmr(StringUtils.equalsIgnoreCase(String.valueOf(object[10]), DominioSimNao.S.toString()));
		}
	}

	private String criarSqlPesquisarPacientesCCIH(final FiltroListaPacienteCCIHVO filtro){
		StringBuilder sql = new StringBuilder(600);
		// FROM
		sql.append("FROM AGH.AIP_PACIENTES PAC LEFT JOIN "
				+ "(SELECT ATD2.PAC_CODIGO, ATD2.SEQ, ATD2.DTHR_INICIO, ATD2.UNF_SEQ, ATD2.LTO_LTO_ID, ATD2.DTHR_INGRESSO_UNIDADE "
				+ "FROM AGH.AGH_ATENDIMENTOS ATD2 WHERE ATD2.DTHR_INICIO = "
				+ "(SELECT MAX(DTHR_INICIO) DTHR_INICIO FROM AGH.AGH_ATENDIMENTOS ATD1 "
				+ "WHERE ATD1.ORIGEM  IN ('U', 'I', 'N') "
				+ "AND ATD1.PAC_CODIGO = ATD2.PAC_CODIGO )) ULTIMAINTERNACAO ON PAC.CODIGO = ULTIMAINTERNACAO.PAC_CODIGO "
				+ "LEFT JOIN AGH.AGH_UNIDADES_FUNCIONAIS UNF ON UNF.SEQ = ULTIMAINTERNACAO.UNF_SEQ WHERE (1=1) ");		
		
		// CRITERIOS DE FILTRO POR ATENDIMENTO AMBULATORIAL. BLOCO HABILITADO SE NEM PRONTUARIO NEM CONSULTA FOR PREENCHIDA
		if(filtro.getDtInicioAtendimento() != null && filtro.getDtFimAtendimento() != null){
			sql.append("AND PAC.CODIGO IN "
					+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
					+ "WHERE ATD.ORIGEM IN ('A' , 'X', 'C') ");
				if (isOracle()) {
					sql.append("AND TRUNC(ATD.DTHR_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioAtendimento()) + " AND " + obterSqlData(filtro.getDtFimAtendimento()) + ") ");
		} else {
					sql.append("AND DATE(ATD.DTHR_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioAtendimento()) + " AND " + obterSqlData(filtro.getDtFimAtendimento()) + ") ");
		}
		}
		
		// CRITERIOS DE FILTRO POR CIRURGIA
		if(filtro.getEquipe() != null || filtro.getUnfCirurgia() != null || filtro.getCodigoProcedimento() != null){
			sql.append("AND PAC.CODIGO IN "
					+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.MBC_AGENDAS AGD "
					+ "INNER JOIN AGH.MBC_CIRURGIAS CRG ON CRG.AGD_SEQ = AGD.SEQ "
					+ "INNER JOIN AGH.AGH_ATENDIMENTOS ATD ON CRG.ATD_SEQ = ATD.SEQ "
					+ "INNER JOIN AGH.MBC_ESPECIALIDADE_PROC_CIRGS EPR ON EPR.PCI_SEQ  = AGD.EPR_PCI_SEQ "
					+ "AND EPR.ESP_SEQ = AGD.EPR_ESP_SEQ "
					+ "INNER JOIN AGH.MBC_PROCEDIMENTO_CIRURGICOS PCI ON PCI.SEQ = EPR.PCI_SEQ "
					+ "INNER JOIN AGH.MBC_PROF_ATUA_UNID_CIRGS PUC ON AGD.PUC_SER_MATRICULA = PUC.SER_MATRICULA "
					+ "AND AGD.PUC_SER_VIN_CODIGO  = PUC.SER_VIN_CODIGO "
					+ "AND AGD.PUC_UNF_SEQ = PUC.UNF_SEQ "
					+ "AND AGD.PUC_IND_FUNCAO_PROF = PUC.IND_FUNCAO_PROF "
					+ "INNER JOIN AGH.AGH_UNIDADES_FUNCIONAIS UNF ON CRG.UNF_SEQ = UNF.SEQ "
					+ "WHERE CRG.SITUACAO = 'RZDA' ");
			if (filtro.getEquipe() != null) {
				// TODO NOTIFICAR ERROS
				sql.append("AND PUC.SER_MATRICULA = " + filtro.getEquipe().getMatricula() + " AND PUC.SER_VIN_CODIGO = " + filtro.getEquipe().getVinCodigo() + " ");
			}
			if (filtro.getUnfCirurgia() != null) {
				sql.append("AND UNF.SEQ = " + filtro.getUnfCirurgia().getSeq() + " ");
			}
			if (filtro.getCodigoProcedimento() != null) {
				sql.append("AND PCI.SEQ = " + filtro.getCodigoProcedimento() + " ");
			}
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(CRG.DTHR_FIM_CIRG) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(CRG.DTHR_FIM_CIRG) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				}
			}
			sql.append(')');
		}

		// CRITERIOS DE FILTRO POR INTERNACAO
		if (filtro.getIndInternado() != null || filtro.getUnfInternacao() != null || filtro.getLeito() != null) {
		sql.append("AND PAC.CODIGO IN "
				+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
				+ "WHERE ATD.ORIGEM IN ('U', 'I', 'N') ");
			if (filtro.getIndInternado() != null) {
				sql.append("AND ATD.IND_PAC_ATENDIMENTO = '" + filtro.getIndInternado().toString() + "' ");
			}
			if (filtro.getUnfInternacao() != null) {
				sql.append("AND ATD.UNF_SEQ  = " + filtro.getUnfInternacao().getSeq() + " ");
			}
			if (filtro.getLeito() != null) {
				sql.append("AND ATD.LTO_LTO_ID  = '" + filtro.getLeito().getLeitoID() + "' ");
			}
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(ATD.DTHR_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(ATD.DTHR_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				}				
			}
			sql.append(')');
		}

		//CRITERIOS DE FILTRO POR NOTIFICACAO FILTRO MEDIDA PREVENTIVA (DOENCA/CONDICAO) SE CRITERIO ESTIVER MARCADO
		if (filtro.isDoencaCondicao() 
				&& (filtro.getSituacaoNotificacao() != null || filtro.getConferido() != null || filtro.getCodigoDoencaCondicao() != null)) {
			sql.append("AND PAC.CODIGO IN " + "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
					+ "INNER JOIN AGH.MCI_MVTO_MEDIDA_PREVENTIVAS MMP ON ATD.SEQ = MMP.ATD_SEQ WHERE (1=1) ");
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(MMP.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(MMP.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				}					
			}
			if (DominioSituacaoNotificacao.A.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MMP.DT_FIM IS NULL "); // ABERTA
			} else if (DominioSituacaoNotificacao.E.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MMP.DT_FIM IS NOT NULL "); // FECHADA
			}
			if (filtro.getConferido() != null) {
				sql.append("AND MMP.IND_CONFIRMACAO_CCI = '" + filtro.getConferido().toString() + "' ");
			}
			if (filtro.getCodigoDoencaCondicao() != null) {
				sql.append("AND MMP.PAI_SEQ = " + filtro.getCodigoDoencaCondicao() + " ");
			}
			sql.append(')');
		}

		 // FILTRO DE TOPOGRAFIA POR PROCEDIMENTO SE CRITERIO ESTIVER MARCADO
		if(filtro.isTopografiaInfeccao()
				&& (filtro.getSituacaoNotificacao() != null || filtro.getConferido() != null || filtro.getCodigoTopografia() != null)){
		sql.append("AND PAC.CODIGO IN "
				+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
				+ "INNER JOIN AGH.MCI_MVTO_INFECCAO_TOPOGRAFIAS MIT ON ATD.SEQ = MIT.ATD_SEQ WHERE (1=1) ");
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(MIT.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(MIT.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				}					
			}
			if (DominioSituacaoNotificacao.A.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MIT.DT_FIM IS NULL "); // ABERTA
			} else if (DominioSituacaoNotificacao.E.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MIT.DT_FIM IS NOT NULL "); // FECHADA
			}
			if (filtro.getConferido() != null) {
				sql.append("AND MIT.IND_CONFIRMACAO_CCI = '" + filtro.getConferido().toString() + "' ");
			}
			if (filtro.getCodigoTopografia() != null) { // FILTRO POR TOPOGRAFIA ESPECIFICA
				sql.append("AND MIT.TOP_SEQ = '" + filtro.getCodigoTopografia() + "' ");
			}
			sql.append(')');
		}

		// FILTRO PROCEDIMENTO DE RISCO SE CRITERIO ESTIVER PREENCHIDO
		if(filtro.isProcedimentoRisco() 
				&& (filtro.getSituacaoNotificacao() != null || filtro.getConferido() != null)){
		sql.append("AND PAC.CODIGO IN "
				+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
				+ "INNER JOIN AGH.MCI_MVTO_PROCEDIMENTO_RISCOS MRI ON ATD.SEQ = MRI.ATD_SEQ WHERE (1=1) ");
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(MRI.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(MRI.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
	}
			}
			if (DominioSituacaoNotificacao.A.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MRI.DT_FIM IS NULL "); // ABERTA
			} else if (DominioSituacaoNotificacao.E.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MRI.DT_FIM IS NOT NULL "); // FECHADA
			}
			if (filtro.getConferido() != null) {
				sql.append("AND MRI.IND_CONFIRMACAO_CCI = '" + filtro.getConferido().toString() + "' ");
			}
			sql.append(')');
		}

		// FILTRO FATORES PREDISPONENTES SE CRITERIO ESTIVER PREENCHIDO
		if(filtro.isFatoresPredisponente()
				&& filtro.getSituacaoNotificacao() != null){
		sql.append("AND PAC.CODIGO IN "
				+ "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD "
				+ "INNER JOIN AGH.MCI_MVTO_FATOR_PREDISPONENTES MFP ON ATD.SEQ = MFP.ATD_SEQ WHERE (1=1) ");
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(MFP.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(MFP.DT_INICIO) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				}				
			}
			if (DominioSituacaoNotificacao.A.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MFP.DT_FIM IS NULL "); // ABERTA
			} else if (DominioSituacaoNotificacao.E.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND MFP.DT_FIM IS NOT NULL "); // FECHADA
	}
			sql.append(')');
		}

		// FILTRO GMR SE CRITERIO ESTIVER PREENCHIDO
		if(filtro.isGmr() && filtro.getSituacaoNotificacao() != null){
		sql.append("AND PAC.CODIGO IN "
				+ "(SELECT DISTINCT NGM.PAC_CODIGO FROM AGH.MCI_NOTIFICACAO_GMR NGM WHERE (1=1) ");
			if (filtro.getDtInicioCriterios() != null && filtro.getDtFimCriterios() != null) {
				if (isOracle()) {
					sql.append("AND TRUNC(NGM.CRIADO_EM) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
				} else {
					sql.append("AND DATE(NGM.CRIADO_EM) BETWEEN " + obterSqlData(filtro.getDtInicioCriterios()) + " AND " + obterSqlData(filtro.getDtFimCriterios()));
	}
			}
			if (DominioSituacaoNotificacao.A.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND NGM.IND_NOTIFICACAO_ATIVA = 'S' "); // ABERTA
			} else if (DominioSituacaoNotificacao.E.equals(filtro.getSituacaoNotificacao())) {
				sql.append("AND NGM.IND_NOTIFICACAO_ATIVA = 'N' "); // FECHADA
			}
			sql.append(')');
		}
		// FILTRO POR CONSULTA OU PRONTUARIO ESPECIFICO. BLOCO HABILITADO SE PRONTUARIO OU CONSULTA FOR PREENCHIDA
		if(filtro.getConsulta() != null || filtro.getProntuario() != null){
			sql.append("AND PAC.CODIGO IN " + "(SELECT DISTINCT ATD.PAC_CODIGO FROM AGH.AGH_ATENDIMENTOS ATD WHERE (1=1) ");
			if (filtro.getConsulta() != null) {
				sql.append("AND ATD.CON_NUMERO =  " + filtro.getConsulta() + " ");
			}
			if (filtro.getProntuario() != null) {
				sql.append("AND ATD.PRONTUARIO =  " + filtro.getProntuario() + " ");
			}
			sql.append(')');
		}
		return sql.toString();
	}
	
	public AipPacientes obterPacientesComUnidadeFuncional(Integer pacCodigo ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "ate",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ate." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unf",JoinType.INNER_JOIN);
		criteria.createAlias("unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "ccc",JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.idEq(pacCodigo));			
		
		AipPacientes result = (AipPacientes) executeCriteriaUniqueResult(criteria);
		
		return result;
	} 
	
	/**
	 * #41772 - C1
	 * @author marcelo.deus
	 */
	public AipPacientes buscarPacientePorTransplanteSeq(Integer transplanteSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "AIP");
		
		criteria.createAlias("AIP." + AipPacientes.Fields.TRANSPLANTE_RECEPTOR.toString(), "TRP");
		criteria.add(Restrictions.eq("TRP." + MtxTransplantes.Fields.SEQ.toString(), transplanteSeq));
		
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AtendimentoJustificativaUsoMedicamentoVO> obterPacientePorAtendimentoComEndereco(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		criteria.createAlias("PAC." + AipPacientes.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("PAC." + AipPacientes.Fields.ENDERECOS.toString(), "ENP", JoinType.INNER_JOIN);
		criteria.createAlias("ENP." + AipEnderecosPacientes.Fields.CIDADE.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		Projection projection = Projections.projectionList()
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_NOME.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NOMEMAE.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_NOME_MAE.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_DT_NASCIMENTO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.CIDADE_CODIGO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_CDD_CODIGO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.OCUPACAO_CODIGO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_OCP_CODIGO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.UF_SIGLA.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_UF_SIGLA.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.COR.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_COR.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.SEXO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_SEXO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.GRAU_INSTRUCAO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_GRAU_INSTRUCAO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.DDD_FONE_RESIDENCIAL.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_DDD_FONE_RESIDENCIAL.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.FONE_RESIDENCIAL.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_FONE_RESIDENCIAL.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.ESTADO_CIVIL.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_ESTADO_CIVIL.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_PRONTUARIO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NRO_CARTAO_SAUDE.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_NRO_CARTAO_SAUDE.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.LOGRADOURO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_LOGRADOURO.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_NRO_LOGRADOURO.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.BAIRRO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_BAIRRO.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.COMPLEMENTO_LOGRADOURO.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_COMPL_LOGRADOURO.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.END_CIDADE.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_CIDADE.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.CEP.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_CEP.toString())
				.add(Projections.property("CID." + AipCidades.Fields.NOME.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.CDD_NOME.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.BCL_BAI_COD.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_BCL_BAI_CODIGO.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.BCL_CLO_CEP.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_BCL_CLO_CEP.toString())
				.add(Projections.property("ENP." + AipEnderecosPacientes.Fields.BCL_CLO_LGR_COD.toString()), AtendimentoJustificativaUsoMedicamentoVO.Fields.ENP_BCL_CLO_LGR_CODIGO.toString());
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentoJustificativaUsoMedicamentoVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * #5795 - C1
	 * @param codigo
	 * @param prontuario
	 * @return
	 */
	public PacienteAgendamentoPrescribenteVO obterCodigoAtendimentoInformacoesPaciente(Integer codigo, Integer prontuario){

		List<DominioOrigemAtendimento> collection = new ArrayList<DominioOrigemAtendimento>();
		collection.add(DominioOrigemAtendimento.I);
		collection.add(DominioOrigemAtendimento.H);
		collection.add(DominioOrigemAtendimento.U);
		collection.add(DominioOrigemAtendimento.N);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "pac");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("atd."+AghAtendimentos.Fields.SEQ.toString()), PacienteAgendamentoPrescribenteVO.Fields.SEQ.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.PRONTUARIO.toString()), PacienteAgendamentoPrescribenteVO.Fields.PRONTURARIO.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.CODIGO.toString()), PacienteAgendamentoPrescribenteVO.Fields.CODIGO.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()), PacienteAgendamentoPrescribenteVO.Fields.NOME.toString()));
		
		criteria.createAlias("pac."+AipPacientes.Fields.ATENDIMENTOS.toString(), "atd");
		
		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), collection));
		
		criteria.add(Restrictions.or(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario),
				Restrictions.eq("pac."+AipPacientes.Fields.CODIGO.toString(), codigo)));
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteAgendamentoPrescribenteVO.class));
		
		return (PacienteAgendamentoPrescribenteVO) executeCriteriaUniqueResult(criteria);
	}
	
	private String obterSqlData(final Date data) {
		String dataFormatada = DateUtil.obterDataFormatada(data, DateConstants.DATE_PATTERN_DDMMYYYY);
		return isOracle() ? "TO_DATE('" + dataFormatada + "')" : "DATE('" + dataFormatada + "')";
	}
	
	public AipPacientes pesquisarPacientePorCodigo(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #42801 
	 */
	public AipPacientes obterPacientePorProtuario(Integer codigo) {
		DetachedCriteria criteria = obterCriteriaPacientesPorProntuarioLeito(codigo);
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}

	
		
	public Date verificaDatas(Object data) { 
		if (data != null) {
			return (Date) data;
		}
		
		return null;
	}
	
	/**
	 * #43089 - C8
	 * @param atdSeq
	 * @return AipPacientes
	 */
	public AipPacientes obterPacientePorAtendimento(Integer atdSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), AipPacientes.Fields.NOME.toString());
		projList.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), AipPacientes.Fields.PRONTUARIO.toString());
		criteria.setProjection(projList);
		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipPacientes.class));
		
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AipPacientes> pesquisarPacientePorAtendimento(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class,"APS");
		
		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(),"AAT", JoinType.INNER_JOIN);
		if (seq != null) {
			criteria.add(Restrictions.eq("AAT."+ AghAtendimentos.Fields.SEQ.toString(),seq));
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * C1#41790
	 * @param strPesquisa
	 * @return
	 */
	public List<AipPacientes> pesquisarPacientePorNomeOuProntuario(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class,"APS");
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq("APS."+AipPacientes.Fields.PRONTUARIO.toString(), Integer.valueOf(strPesquisa)));
		}else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("APS."+AipPacientes.Fields.NOME.toString(),strPesquisa,MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.isNotNull("APS." +AipPacientes.Fields.PRONTUARIO.toString()));
		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		return executeCriteria(criteria,0,100,null,true);
	}
	
	public Long pesquisarPacientePorNomeOuProntuarioCount(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class,"APS");
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq("APS."+AipPacientes.Fields.PRONTUARIO.toString(), Integer.valueOf(strPesquisa)));
		}else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("APS."+AipPacientes.Fields.NOME.toString(),strPesquisa,MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.isNotNull("APS." +AipPacientes.Fields.PRONTUARIO.toString()));
		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		return executeCriteriaCount(criteria);
	}

	public Date buscarDataNascimentoPaciente(Integer pPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pPacCodigo));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(AipPacientes.Fields.DATA_NASCIMENTO.toString())));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public BigInteger obterCartaoSus(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.setProjection(Projections.property(AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString()));
		criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		return (BigInteger) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Consulta para obter dados do paciente 
	 * #47146 - C1
	 */
	public AipPacientes obterDadosPaciente(Integer seqTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias("TRP." + MtxTransplantes.Fields.RECEPTOR.toString(), "PAC", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
                .add(Projections.property("PAC."+ AipPacientes.Fields.PRONTUARIO.toString()).as(AipPacientes.Fields.PRONTUARIO.toString()))
                .add(Projections.property("PAC."+ AipPacientes.Fields.NOME.toString()).as(AipPacientes.Fields.NOME.toString()))
                .add(Projections.property("PAC."+ AipPacientes.Fields.CODIGO.toString()).as(AipPacientes.Fields.CODIGO.toString())));

		criteria.add(Restrictions.eq("TRP." + MtxTransplantes.Fields.SEQ.toString(), seqTransplante));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipPacientes.class));
		
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AipPacientes> pesquisarPacientePorNomeOuProntuarioOuCodigo(AipPacientes paciente, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
			
		
		/*Array para retornar determinado tipo de prontuario*/
		DominioTipoProntuario prontuarioAtivo[] = {DominioTipoProntuario.A, DominioTipoProntuario.P, DominioTipoProntuario.R, DominioTipoProntuario.E};
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.NOME.toString()) , AipPacientes.Fields.NOME.toString())
				.add(Projections.property(AipPacientes.Fields.NOMEMAE.toString()) , AipPacientes.Fields.NOMEMAE.toString())
				.add(Projections.property(AipPacientes.Fields.DATA_NASCIMENTO.toString()) , AipPacientes.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()) , AipPacientes.Fields.PRONTUARIO.toString())
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()) , AipPacientes.Fields.CODIGO.toString())	
		);
		
		criteria.add(Restrictions.isNull(AipPacientes.Fields.DT_OBITO.toString()));
		criteria.add(Restrictions.in(AipPacientes.Fields.PRN_ATIVO.toString(), prontuarioAtivo));
		
		if(paciente != null){
		
			if (paciente.getProntuario() != null) {
				criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(),paciente.getProntuario()));

			}

			if (paciente.getCodigo() != null) {
				criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),paciente.getCodigo()));
			}

			if(StringUtils.isNotEmpty(paciente.getNome())){
				criteria.add(Restrictions.like(AipPacientes.Fields.NOME.toString(),paciente.getNome(), MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipPacientes.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarPacientePorNomeOuProntuarioOuCodigoCount(AipPacientes paciente){
		
		/*Array para retornar determinado tipo de prontuario*/
		DominioTipoProntuario prontuarioAtivo[] = {DominioTipoProntuario.A, DominioTipoProntuario.P, DominioTipoProntuario.R, DominioTipoProntuario.E};
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.NOME.toString()) , AipPacientes.Fields.NOME.toString())
				.add(Projections.property(AipPacientes.Fields.NOMEMAE.toString()) , AipPacientes.Fields.NOMEMAE.toString())
				.add(Projections.property(AipPacientes.Fields.DATA_NASCIMENTO.toString()) , AipPacientes.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()) , AipPacientes.Fields.PRONTUARIO.toString())
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()) , AipPacientes.Fields.CODIGO.toString())	
		);
		criteria.add(Restrictions.isNull(AipPacientes.Fields.DT_OBITO.toString()));
		criteria.add(Restrictions.in(AipPacientes.Fields.PRN_ATIVO.toString(), prontuarioAtivo));
		
		if (paciente != null){
			if(paciente.getProntuario() != null){
				criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(),paciente.getProntuario()));
			}
			if(paciente.getCodigo() != null){
				criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),paciente.getCodigo()));
			}
			if(StringUtils.isNotEmpty(paciente.getNome())){
				criteria.add(Restrictions.like(AipPacientes.Fields.NOME.toString(),paciente.getNome(),MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));
		
		return executeCriteriaCount(criteria);
	}
	/***
	 * Obtém a lista de altas de um paciente
	 * 
	 * @ORADB P_POPULA_ALTAS - #47350
	 * 
	 * @param codPaciente Código do paciente
	 * @return Lista de {@link MamAltaSumario} 
	 */
	public List<MamAltaSumario> obterDadosAltaPaciente(Integer codPaciente){
		
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DATE, 1);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class, "mals");
		
		criteria.createAlias("mals." + MamAltaSumario.Fields.ESPECIALIDADE.toString(), "esp", JoinType.INNER_JOIN);
		criteria.createAlias("mals." + MamAltaSumario.Fields.SERVIDOR_VALIDA.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("mals." + MamAltaSumario.Fields.PAC_CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.isNull("mals." + MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		
		DetachedCriteria subQueryExisteAlta = DetachedCriteria.forClass(MamAltaSumario.class, "als");
		criteria.add(Subqueries.exists(subQueryExisteAlta));
		subQueryExisteAlta.setProjection(Projections.projectionList().add(Projections.property("als." + MamAltaSumario.Fields.SEQ.toString())));
		
		subQueryExisteAlta.add(Restrictions.eqProperty("als." + MamAltaSumario.Fields.PAC_CODIGO.toString(), "mals." + MamAltaSumario.Fields.PAC_CODIGO.toString()));
		subQueryExisteAlta.add(Restrictions.eqProperty("als." + MamAltaSumario.Fields.ESP_SEQ.toString(), "mals." + MamAltaSumario.Fields.ESP_SEQ.toString()));
		subQueryExisteAlta.add(Restrictions.eq("als." + MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		subQueryExisteAlta.add(Restrictions.isNull("als." + MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		
		DetachedCriteria subQueryCons = DetachedCriteria.forClass(AacConsultas.class, "ct1");
		subQueryExisteAlta.add(Subqueries.propertyIn("als." + MamAltaSumario.Fields.CON_NUMERO.toString(), subQueryCons));
		subQueryCons.createAlias("ct1." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gr1", JoinType.INNER_JOIN);
		subQueryCons.add(Restrictions.eqProperty("ct1." + AacConsultas.Fields.PAC_CODIGO.toString(), "als." + AacConsultas.Fields.PAC_CODIGO.toString()));
		subQueryCons.setProjection(Projections.projectionList().add(Projections.property("ct1." + AacConsultas.Fields.NUMERO.toString())));
		
		
		DetachedCriteria subQueryEsp = DetachedCriteria.forClass(MamAltaSumario.class, "suma");
		subQueryCons.add(Subqueries.propertyIn("gr1." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString() , subQueryEsp));
		subQueryEsp.setProjection(Projections.distinct(
			Projections.projectionList().add(Projections.property("suma." + MamAltaSumario.Fields.ESP_SEQ.toString()))
		));
		subQueryEsp.add(Restrictions.eqProperty("suma." + MamAltaSumario.Fields.PAC_CODIGO.toString() , "ct1." + AacConsultas.Fields.PAC_CODIGO.toString()));
		
		
		DetachedCriteria subQueryData = DetachedCriteria.forClass(AacConsultas.class, "ct2");
		subQueryCons.add(Subqueries.propertyIn("ct1." + AacConsultas.Fields.DATA_CONSULTA.toString(), subQueryData));
		subQueryData.setProjection(Projections.projectionList().add(
			Projections.max("ct2." + AacConsultas.Fields.DATA_CONSULTA.toString())
		));
		subQueryData.createAlias("ct2." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gr2", JoinType.INNER_JOIN);
		subQueryData.add(Restrictions.lt("ct2." + AacConsultas.Fields.DATA_CONSULTA.toString(), data.getTime()));
		subQueryData.add(Restrictions.eqProperty("ct2." + AacConsultas.Fields.PAC_CODIGO.toString(), "ct1." + AacConsultas.Fields.PAC_CODIGO.toString()));
		subQueryData.add(Restrictions.eqProperty("gr2." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), "gr1." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	/***
	 * Verifica se existe alta registrada para o paciente
	 * 
	 * @ORADB MAMC_PAC_TEM_ALTA - #47350
	 * 
	 * @param codPaciente Código do paciente
	 * @return Se existe alta registrada
	 */
	public Boolean verificarExisteAltaPaciente(Integer codPaciente){
		
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DATE, 1);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class, "als");
		criteria.setProjection(Projections.rowCount());
		
		criteria.add(Restrictions.eq("als." + MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull("als." + MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		
		DetachedCriteria subQueryCons = DetachedCriteria.forClass(AacConsultas.class, "ct1");
		criteria.add(Subqueries.propertyIn("als." + MamAltaSumario.Fields.CON_NUMERO.toString(), subQueryCons));
		subQueryCons.createAlias("ct1." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gr1", JoinType.INNER_JOIN);
		subQueryCons.add(Restrictions.eq("ct1." + AacConsultas.Fields.PAC_CODIGO.toString(), codPaciente));
		subQueryCons.setProjection(Projections.projectionList().add(Projections.property("ct1." + AacConsultas.Fields.NUMERO.toString())));
		
		
		DetachedCriteria subQueryEsp = DetachedCriteria.forClass(MamAltaSumario.class, "suma");
		subQueryCons.add(Subqueries.propertyIn("gr1." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString() , subQueryEsp));
		subQueryEsp.setProjection(Projections.distinct(
				Projections.projectionList().add(Projections.property("suma." + MamAltaSumario.Fields.ESP_SEQ.toString()))
			));
		subQueryEsp.add(Restrictions.eqProperty("suma." + MamAltaSumario.Fields.PAC_CODIGO.toString() , "ct1." + AacConsultas.Fields.PAC_CODIGO.toString()));
		
		
		DetachedCriteria subQueryData = DetachedCriteria.forClass(AacConsultas.class, "ct2");
		subQueryCons.add(Subqueries.propertyIn("ct1." + AacConsultas.Fields.DATA_CONSULTA.toString(), subQueryData));
		subQueryData.setProjection(Projections.projectionList().add(
				Projections.max("ct2." + AacConsultas.Fields.DATA_CONSULTA.toString())
			));
		subQueryData.createAlias("ct2." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gr2", JoinType.INNER_JOIN);
		subQueryData.add(Restrictions.lt("ct2." + AacConsultas.Fields.DATA_CONSULTA.toString(), data.getTime()));
		subQueryData.add(Restrictions.eqProperty("ct2." + AacConsultas.Fields.PAC_CODIGO.toString(), "ct1." + AacConsultas.Fields.PAC_CODIGO.toString()));
		subQueryData.add(Restrictions.eqProperty("gr2." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), "gr1." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
			
		Long result = (Long) executeCriteriaUniqueResult(criteria);

		return result != null && result > 0;
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_COR_PAC
	 * @param codigo
	 * @return
	 */
	public DominioCor obterCurCorPacPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.property(AipPacientes.Fields.COR.toString()));
		return (DominioCor) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_GRAU_PAC
	 * @param codigo
	 * @return
	 */
	public DominioGrauInstrucao obterCurGrauPacPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.property(AipPacientes.Fields.GRAU_INSTRUCAO.toString()));
		return (DominioGrauInstrucao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_NAC_PAC
	 * @param codigo
	 * @return
	 */
	public String obterCurNacionalidadePorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.NACIONALIDADE.toString(), "NAC");
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property("NAC."+AipNacionalidades.Fields.DESCRICAO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_NATUR_PAC
	 * @param codigo
	 * @return
	 */
	public String obterCurCidadePorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.CIDADE.toString(), "CID");
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property("CID."+AipCidades.Fields.NOME.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_PROF_PAC
	 * @param codigo
	 * @return
	 */
	public String obterCurProfissaoPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.createAlias(PAC+PONTO+AipPacientes.Fields.OCUPACOES.toString(), "OCU");
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property("OCU."+AipOcupacoes.Fields.DESCRICAO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_SEXO_PAC
	 * @param codigo
	 * @return
	 */
	public DominioSexo obterCurSexoPacPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property(PAC+PONTO+AipPacientes.Fields.SEXO.toString()));
		return (DominioSexo) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_NOME_PAC
	 * @param codigo
	 * @return
	 */
	public String obterNomePacientePorCodigoPac(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property(PAC+PONTO+AipPacientes.Fields.NOME.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_CIVIL_PAC
	 * @param codigo
	 * @return
	 */
	public CursorPacVO obterCurPacPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PAC+PONTO+AipPacientes.Fields.SEXO.toString()), CursorPacVO.Fields.SEXO.toString())
				.add(Projections.property(PAC+PONTO+AipPacientes.Fields.ESTADO_CIVIL.toString()), CursorPacVO.Fields.ESTADO_CIVIL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorPacVO.class));
		
		return (CursorPacVO) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_NOME_PAI
	 * @param codigo
	 * @return
	 */
	public String obterCurPacNomePaiPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property(PAC+PONTO+AipPacientes.Fields.NOMEPAI.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EDITA_NOME_MAE
	 * @param codigo
	 * @return
	 */
	public String obterCurPacNomeMaePorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.add(Restrictions.eq(PAC+PONTO+AipPacientes.Fields.CODIGO, codigo));
		criteria.setProjection(Projections.property(PAC+PONTO+AipPacientes.Fields.NOMEMAE.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Consulta pacientes portadores de germes multiresistentes.
	 * @param dataConsulta
	 * @param tufSeq
	 * @return List<PacientePortadorGermeVO>
	 */
	public List<PacientePortadorGermeVO> consultarPacientesPortadoresGermesMultiresistentes(Date dataConsulta, Integer tufSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		
		criteria.createAlias("PAC."+AipPacientes.Fields.AAC_CONSULTAS.toString(), "CON");
		criteria.createAlias("PAC."+AipPacientes.Fields.MCI_NOTIFICACAO_GMR.toString(), "NGM");
		criteria.createAlias("CON."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.TUFSEQ.toString(), tufSeq)); 
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.N));
		criteria.add(Restrictions.eq("NGM."+MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString(), Boolean.TRUE));
		
		if (isOracle()) {
            criteria.add(Restrictions.sqlRestriction("TRUNC(con1_.DT_CONSULTA) = ?", dataConsulta, new DateType()));                   
		} else{
            criteria.add(Restrictions.sqlRestriction("date_trunc('day', con1_.DT_CONSULTA) = ?", dataConsulta, new DateType()));
		}

		
		criteria.addOrder(OrderBySql.sql("unf6_.DESCRICAO, this_.NOME"));
		
		String projectionsHoraConsulta = "TO_CHAR(con1_.DT_CONSULTA, 'HH24:MI') as horaConsulta";
		
		String projectionCodBacteria = "";
		if (isOracle()) {
			projectionCodBacteria = "DECODE(ngm2_.BMR_SEQ, 27, ngm2_.BMR_SEQ,"
	                							.concat("79, ngm2_.BMR_SEQ,")
	                							.concat("null) as codBacteria");
		} else {
			projectionCodBacteria = "(case ngm2_.BMR_SEQ when 27 then ngm2_.BMR_SEQ "
                    						  .concat("when 79 then ngm2_.BMR_SEQ ")
                    						  .concat("else null ")
                    		 .concat("end) as codBacteria");
		}
				
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), PacientePortadorGermeVO.Fields.UNIDADE.toString())
				.add(Projections.sqlProjection(projectionsHoraConsulta, new String[] {PacientePortadorGermeVO.Fields.HORA_CONSULTA.toString()}, new Type[] {new StringType()}))
				.add(Projections.property("CON."+AacConsultas.Fields.NUMERO.toString()), PacientePortadorGermeVO.Fields.CONSULTA.toString())
				.add(Projections.property("GRD."+AacGradeAgendamenConsultas.Fields.USLSALA.toString()), PacientePortadorGermeVO.Fields.SALA.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString()), PacientePortadorGermeVO.Fields.AGENDA.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), PacientePortadorGermeVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()), PacientePortadorGermeVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.sqlProjection(projectionCodBacteria, new String[] {PacientePortadorGermeVO.Fields.COD_BACTERIA.toString()}, new Type[] {new StringType()}));
		
		criteria.setProjection(Projections.distinct(projectionList));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacientePortadorGermeVO.class));

		return executeCriteria(criteria);
	}
	public List<AtendimentoPacientesCCIHVO> pesquisarDetalhamentoPacientesBuscaAtiva(List<Integer> codigoPacientes, List<Integer> codigoAtendimentos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		criteria.createAlias("PAC." + AipPacientes.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("AIP." + AipPacientes.Fields.NOME.toString()), AtendimentoPacientesCCIHVO.Fields.NOME.toString())
				.add(Projections.property("AIP." + AipPacientes.Fields.PRONTUARIO.toString()), AtendimentoPacientesCCIHVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("AIP." + AipPacientes.Fields.CODIGO.toString()), AtendimentoPacientesCCIHVO.Fields.COD_PACIENTE.toString())
				.add(Projections.property("AIP." + AipPacientes.Fields.DT_NASCIMENTO.toString()), AtendimentoPacientesCCIHVO.Fields.DT_NASCIMENTO.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentoPacientesCCIHVO.Fields.DT_INICIO_INTERNACAO.toString())
				.add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString()), AtendimentoPacientesCCIHVO.Fields.LEITO_ID.toString())
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), AtendimentoPacientesCCIHVO.Fields.UNF_DESCRICAO.toString())
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), AtendimentoPacientesCCIHVO.Fields.UNF_SEQ.toString())
				);
		
		criteria.add(Restrictions.in("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPacientes));
		criteria.add(Restrictions.in("ATD." + AghAtendimentos.Fields.SEQ.toString(), codigoAtendimentos));
		
		criteria.addOrder(Order.asc("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("LTO." + AinLeitos.Fields.LTO_ID.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AtendimentoPacientesCCIHVO> pesquisarPacientesBuscaAtivaCCIH(FiltroListaPacienteCCIHVO filtro) {
		List<AtendimentoPacientesCCIHVO> resultado = new ArrayList<AtendimentoPacientesCCIHVO>();
		StringBuilder sql = new StringBuilder(1500);
		
		sql.append("SELECT ULTIMAINTERNACAO.SEQ AS ATDSEQ,"
			).append("PAC.PRONTUARIO AS PRONTUARIO,"
			).append("PAC.NOME AS NOME,"
			).append("PAC.DT_NASCIMENTO AS DTNASCIMENTO,"
			).append("ULTIMAINTERNACAO.DTHR_INGRESSO_UNIDADE AS DTHRINGRESSO,"
			).append("ULTIMAINTERNACAO.LTO_LTO_ID AS LEITOID,"
			).append("UNF.DESCRICAO AS UNFDESCRICAO,"
			).append("UNF.SEQ AS UNFSEQ,"
			).append("PAC.CODIGO AS PACCODIGO, "
			).append("ULTIMAINTERNACAO.DTHR_INICIO "
			).append(criarSqlPesquisarPacientesCCIH(filtro)
			).append(" ORDER BY UNF.SEQ, UNF.DESCRICAO, ULTIMAINTERNACAO.LTO_LTO_ID");
		
		javax.persistence.Query query = this.createNativeQuery(sql.toString());
		
		// Executa consulta
		List<Object[]> resultadoConsulta = query.getResultList();
		for (Object[] object : resultadoConsulta) {
			resultado.add(popularItemBuscaAtivaPacientes(object));
		}

		return resultado;
	}
	
	public AtendimentoPacientesCCIHVO popularItemBuscaAtivaPacientes(Object[] object) {
		AtendimentoPacientesCCIHVO vo = new AtendimentoPacientesCCIHVO();
		
		if(object[0] != null) { //atdSeq
			vo.setAtdSeq(Integer.valueOf(object[0].toString()));
		}
		if(object[1] != null) { //prontuario
			vo.setProntuario(Integer.valueOf(object[1].toString()));
		}
		if(object[2] != null) { //nome
			vo.setNome(object[2].toString());
		}

		vo.setDtNascimento(verificaDatas(object[3])); //dataNascimento
		vo.setDataHoraIngresso(verificaDatas(object[4])); //dataIngressoUnidade
		
		if(object[5] != null) { //leitoId
			vo.setLeitoId(object[5].toString());
		}
		if(object[6] != null) { //unfDescricao
			vo.setUnfDescricao(object[6].toString());
		}
		if(object[7] != null) { //unfSeq
			vo.setUnfSeq(Short.valueOf(object[7].toString()));
		}
		if(object[8] != null) { //pacCodigo
			vo.setCodPaciente(Integer.valueOf(object[8].toString()));
		}
		vo.setDtInicioInternacao(verificaDatas(object[9])); //dthr inicio internacao
		
		return vo;
	}
	
	public Date obterDataNascimentoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(),	pacCodigo));
		criteria.setProjection(Projections.property(AipPacientes.Fields.DATA_NASCIMENTO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AipPacientes.class));
		return ((AipPacientes) executeCriteriaUniqueResult(criteria)).getDtNascimento();
	}
	
	/**
	 * Obter lista de pacientes internados usando o leito como filtro
	 * #50931 - C2
	 * @param leitoID
	 * @return
	 */
	public List<AipPacientes> obterPacienteInternadoPorLeito(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ate");
		criteria.createAlias("ate." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("ate." + AghAtendimentos.Fields.LEITO.toString(), "lei");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ate." + AghAtendimentos.Fields.PRONTUARIO.toString()), AipPacientes.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.CODIGO.toString()), AipPacientes.Fields.CODIGO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), AipPacientes.Fields.NOME.toString()));
		
		
		criteria.add(Restrictions.eq("lei." + AinLeitos.Fields.LTO_ID.toString(), leitoId));
		criteria.add(Restrictions.eq("ate." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipPacientes.class));

		return executeCriteria(criteria);
	}

	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuariosPaciente(AipPacientes paciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		createCriteriaPesquisaMovimentacaoProntuario(paciente.getCodigo(), paciente.getProntuario(), paciente.getNome(), criteria);
		return executeCriteria(criteria);
	}

	private void createCriteriaPesquisaMovimentacaoProntuario(Integer codigo, Integer prontuarioPesquisa,
			String nomePesquisaPaciente, DetachedCriteria criteria) {
		if (codigo != null ) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigo));
		}
		if (prontuarioPesquisa != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuarioPesquisa));
		}
		if (nomePesquisaPaciente != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nomePesquisaPaciente));
		}		
		criteria.setProjection(
		Projections.projectionList()
			.add(Projections.property(AipPacientes.Fields.CODIGO.toString()), "pacCodigo")
			.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
			.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nomePaciente"));
		criteria.setResultTransformer(Transformers.aliasToBean(AipMovimentacaoProntuariosVO.class));
	}

	public Long pesquisaMovimentacoesDeProntuariosPacienteCount(
			Integer codigoPesquisaPaciente, Integer prontuarioPesquisa,
			String nomePesquisaPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		createCriteriaPesquisaMovimentacaoProntuario(codigoPesquisaPaciente, prontuarioPesquisa, nomePesquisaPaciente, criteria);
		return executeCriteriaCount(criteria);
	}
	
		
	
}
