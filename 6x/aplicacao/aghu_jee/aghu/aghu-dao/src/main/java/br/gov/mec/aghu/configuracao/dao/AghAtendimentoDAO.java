package br.gov.mec.aghu.configuracao.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaQuery;
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
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AghAtendimentosParametroVOQueryBuilder;
import br.gov.mec.aghu.ambulatorio.vo.QuantidadeAparelhoAuditivoVO;
import br.gov.mec.aghu.ambulatorio.vo.ReceitasGeralEspecialVO;
import br.gov.mec.aghu.ambulatorio.vo.TransferirExamesVO;
import br.gov.mec.aghu.blococirurgico.vo.CurTeiVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoSubVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.dominio.DominioTipoCertidao;
import br.gov.mec.aghu.dominio.DominioTipoLaminaLaringo;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.exames.solicitacao.vo.AtendimentoSolicExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.farmacia.vo.InformacaoEnviadaPrescribenteVO;
import br.gov.mec.aghu.faturamento.vo.ReimpressaoLaudosProcedimentosVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacProteseAuditiva;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.EcpServidorUnidFuncional;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmAltaPrincExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmListaServSumrAlta;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoAtendimentoEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.vo.AtendimentoPrescricaoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultarRetornoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosRegistroCivilVO;
import br.gov.mec.aghu.vo.AghAtendimentosPacienteCnsVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.vo.AtendimentoNascimentoVO;
import br.gov.mec.aghu.vo.ContagemQuimioterapiaVO;
import br.gov.mec.aghu.vo.DadosPacientesEmAtendimentoVO;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;


/**
 * 
 * @see AghAtendimentos
 * 
 * @author cvagheti
 * 
 */
@SuppressWarnings({ "PMD.NcssTypeCount", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
public class AghAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghAtendimentos> {

	private static final long serialVersionUID = 4431539038427213606L;
	
	private static final Log LOG = LogFactory.getLog(AghAtendimentoDAO.class);
	
	
	 @Inject @New(AtendimentoParaSolicitacaoExameCountQueryBuilder.class) 
	 private Instance<AtendimentoParaSolicitacaoExameCountQueryBuilder> atendimentoParaSolicitacaoExameCountBuilder;
	 
	 @Inject @New(AtendimentoParaSolicitacaoExameQueryBuilder.class) 
	 private Instance<AtendimentoParaSolicitacaoExameQueryBuilder> atendimentoParaSolicitacaoExameBuilder;		 
	
	protected Log doGetLogger() {
		return LOG;
	}	 

	public List<Integer> listarSeqAtdPorOrigem(Integer pacCodigo, DominioOrigemAtendimento[] origens) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));

		criteria.setProjection(Projections.distinct(Projections.property(AghAtendimentos.Fields.SEQ.toString())));

		return executeCriteria(criteria);
	}

	/**
	 * Retorna os atendimentos para apresentação na lista de pacientes do
	 * profissional fornecido <br>
	 * <b>Módulo:</b> Prescrição.
	 * 
	 * @param servidor
	 * @return
	 */
	public List<AghAtendimentos> listarPaciente(final RapServidores servidor, List<RapServidores> pacAtdEqpResp, boolean mostrarPacientesCpa) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		// base da lista de pacientes
		final DetachedCriteria criteria = criteriaBaseListaPaciente();

		// todas subqueries com clausula or
		final Disjunction disjunction = getDisjuntionSubqueriesConfiguracao(servidor, pacAtdEqpResp, mostrarPacientesCpa);
		criteria.add(disjunction);

		criteria.addOrder(Order.asc("indPacAtendimento"));
		criteria.addOrder(Order.asc("leito"));
		criteria.addOrder(Order.asc("quarto"));
		criteria.addOrder(Order.asc("unidadeFuncional"));

		return executeCriteria(criteria);

	}
	
	/*
	 * public List<AghAtendimentos> listarAtendimentosEnfermagem(final RapServidores servidor) { if (servidor == null) { throw new IllegalArgumentException("Parâmetro obrigatório"); }
	 * 
	 * final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atendimentos"); criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
	 * criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "paciente");
	 * 
	 * criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S)); criteria.add(Restrictions.or( Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(),
	 * DominioOrigemAtendimento.I), Restrictions.or( Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.H), Restrictions.or(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(),
	 * DominioOrigemAtendimento.U), Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N)))));
	 * 
	 * criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
	 * 
	 * final DetachedCriteria criteriaPacAtendProfissional = DetachedCriteria.forClass(MpmPacAtendProfissional.class, "atendProfissional");
	 * criteriaPacAtendProfissional.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()));
	 * criteriaPacAtendProfissional.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), servidor));
	 * criteriaPacAtendProfissional.add(Restrictions.eqProperty(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString(), "atendimentos." + AghAtendimentos.Fields.SEQUENCE.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorUnidadeFuncional = DetachedCriteria.forClass(MpmServidorUnidFuncional.class, "servidorUnidadeFuncional");
	 * criteriaServidorUnidadeFuncional.setProjection(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
	 * criteriaServidorUnidadeFuncional.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), servidor));
	 * criteriaServidorUnidadeFuncional.add(Restrictions.eqProperty(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "atendimentos." +
	 * AghAtendimentos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorEspecialidades = DetachedCriteria.forClass(MpmListaServEspecialidade.class, "servidorEspecialidade");
	 * criteriaServidorEspecialidades.setProjection(Projections.property(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.SEQ.toString()));
	 * criteriaServidorEspecialidades.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), servidor)); criteriaServidorEspecialidades.add(Restrictions.or( Restrictions.and(
	 * Restrictions.isNotNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.leProperty(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), "atendimentos." +
	 * AghAtendimentos.Fields.DTHR_FIM.toString())), Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
	 * Restrictions.le(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), new Date())))); criteriaServidorEspecialidades.add(Restrictions.eqProperty(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "." +
	 * AghEspecialidades.Fields.SEQ.toString(), "atendimentos." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorEquipes = DetachedCriteria.forClass(MpmListaServEquipe.class, "servidorEquipe"); criteriaServidorEquipes.setProjection(Projections.projectionList()
	 * .add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString())) .add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." +
	 * RapServidores.Fields.MATRICULA.toString()))); criteriaServidorEquipes.createAlias(MpmListaServEquipe.Fields.SERVIDOR.toString(), "servidor");
	 * criteriaServidorEquipes.add(Restrictions.eq(MpmListaServEquipe.Fields.SERVIDOR.toString(), servidor)); criteriaServidorEquipes.add(Restrictions.or( Restrictions.and( Restrictions.isNotNull("atendimentos." +
	 * AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.leProperty(MpmListaServEquipe.Fields.CRIADO_EM.toString(), "atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString())),
	 * Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.le(MpmListaServEquipe.Fields.CRIADO_EM.toString(), new Date()))));
	 * criteriaServidorEquipes.add(Restrictions.eqProperty(MpmListaServEquipe.Fields.SERVIDOR.toString(), "atendimentos." + AghAtendimentos.Fields.SERVIDOR.toString()));
	 * 
	 * final DetachedCriteria criteriaPacCpas = DetachedCriteria.forClass(MpmListaPacCpa.class, "pacCpa"); criteriaPacCpas.setProjection(Projections.property(MpmListaPacCpa.Fields.IND_PAC_CPA.toString()));
	 * criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.SERVIDOR.toString(), servidor)); criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.IND_PAC_CPA.toString(), true));
	 * criteriaPacCpas.add(Restrictions.eq("atendimentos." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S)); criteriaPacCpas.add(Restrictions.eq("atendimentos." +
	 * AghAtendimentos.Fields.IND_PAC_CPA.toString(), true));
	 * 
	 * criteria.add(Restrictions.or( Subqueries.exists(criteriaPacAtendProfissional), Restrictions.or( Subqueries.exists(criteriaServidorUnidadeFuncional),
	 * Restrictions.or(Subqueries.exists(criteriaServidorEspecialidades), Restrictions.or(Subqueries.exists(criteriaServidorEquipes), Subqueries.exists(criteriaPacCpas))))));
	 * 
	 * criteria.addOrder(Order.asc("leito")); criteria.addOrder(Order.asc("quarto")); criteria.addOrder(Order.asc("unidadeFuncional"));
	 * 
	 * return executeCriteria(criteria);
	 * 
	 * }
	 */
	public List<AghAtendimentos> listarPacientesEnfermagem(RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		// Criando criterio para busca dos Ids de Atendimentos.
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atendimentos");
		criteria.setProjection(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "paciente");

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		List<DominioOrigemAtendimento> atdOrigem = Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.H, DominioOrigemAtendimento.U,
				DominioOrigemAtendimento.N);
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), atdOrigem));

		// Exists: Paciente Atendimento Profissional
		final DetachedCriteria criteriaPacAtendProfissional = DetachedCriteria.forClass(MpmPacAtendProfissional.class, "atendProfissional");
		criteriaPacAtendProfissional.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()));
		criteriaPacAtendProfissional.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), servidor));
		criteriaPacAtendProfissional.add(Restrictions.eqProperty(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString(), "atendimentos."
				+ AghAtendimentos.Fields.SEQ.toString()));

		// Exists: Servidor Unidade funcional
		final DetachedCriteria criteriaServidorUnidadeFuncional = DetachedCriteria.forClass(MpmServidorUnidFuncional.class, "servidorUnidadeFuncional");
		criteriaServidorUnidadeFuncional.setProjection(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "."
				+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaServidorUnidadeFuncional.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorUnidadeFuncional.add(Restrictions.eqProperty(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "."
				+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "atendimentos." + AghAtendimentos.Fields.UNF_SEQ.toString()));

		// Exists: Lista Servidor Especialidade
		final DetachedCriteria criteriaServidorEspecialidades = DetachedCriteria.forClass(MpmListaServEspecialidade.class, "servidorEspecialidade");
		criteriaServidorEspecialidades.setProjection(Projections.property(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.SEQ.toString()));
		criteriaServidorEspecialidades.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorEspecialidades.add(Restrictions.or(
				Restrictions.and(
						Restrictions.isNotNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
						Restrictions.leProperty(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(),
								"atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString())),
								Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
										Restrictions.le(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), new Date()))));
		criteriaServidorEspecialidades.add(Restrictions.eqProperty(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.SEQ.toString(), "atendimentos." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));

		// Exists: Lista Servidor equipe
		final DetachedCriteria criteriaServidorEquipes = DetachedCriteria.forClass(MpmListaServEquipe.class, "servidorEquipe");
		criteriaServidorEquipes.setProjection(Projections.projectionList()
				.add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString()))
				.add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.MATRICULA.toString())));
		criteriaServidorEquipes.createAlias(MpmListaServEquipe.Fields.SERVIDOR.toString(), "servidor");
		criteriaServidorEquipes.add(Restrictions.eq(MpmListaServEquipe.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorEquipes.add(Restrictions.or(
				Restrictions.and(Restrictions.isNotNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
						Restrictions.leProperty(MpmListaServEquipe.Fields.CRIADO_EM.toString(), "atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString())),
						Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
								Restrictions.le(MpmListaServEquipe.Fields.CRIADO_EM.toString(), new Date()))));
		criteriaServidorEquipes.add(Restrictions.eqProperty(MpmListaServEquipe.Fields.SERVIDOR.toString(),
				"atendimentos." + AghAtendimentos.Fields.SERVIDOR.toString()));

		// Exists: Lista Paciente Cpa
		final DetachedCriteria criteriaPacCpas = DetachedCriteria.forClass(MpmListaPacCpa.class, "pacCpa");
		criteriaPacCpas.setProjection(Projections.property(MpmListaPacCpa.Fields.IND_PAC_CPA.toString()));
		criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.SERVIDOR.toString(), servidor));
		criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.IND_PAC_CPA.toString(), true));
		criteriaPacCpas.add(Restrictions.eq("atendimentos." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteriaPacCpas.add(Restrictions.eq("atendimentos." + AghAtendimentos.Fields.IND_PAC_CPA.toString(), true));

		// Criando os filtro com Exists
		criteria.add(Restrictions.or(
				Subqueries.exists(criteriaPacAtendProfissional),
				Restrictions.or(
						Subqueries.exists(criteriaServidorUnidadeFuncional),
						Restrictions.or(Subqueries.exists(criteriaServidorEspecialidades),
								Restrictions.or(Subqueries.exists(criteriaServidorEquipes), Subqueries.exists(criteriaPacCpas))))));

		//		criteria.addOrder(Order.asc("leito"));
		//		criteria.addOrder(Order.asc("quarto"));
		//		criteria.addOrder(Order.asc("unidadeFuncional"));

		List<Integer> atdSeqs = executeCriteria(criteria);

		if (atdSeqs != null && !atdSeqs.isEmpty()) {
			// Busca a lista de Atendimentos com a lista de ids.
			String fieldFilter = "atd." + AghAtendimentos.Fields.SEQ.toString();
			String filtroIN = CoreUtil.criarClausulaIN(fieldFilter, "where", atdSeqs);

			StringBuffer hql = new StringBuffer(250);
			hql.append(" select atd");
			hql.append(" from ").append(AghAtendimentos.class.getSimpleName());
			hql.append(" atd");			
			hql.append(filtroIN);
			hql.append(" order by");
			hql.append(" atd.").append(AghAtendimentos.Fields.LEITO.toString());
			hql.append(", atd.").append(AghAtendimentos.Fields.QUARTO.toString());
			hql.append(", atd.").append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());
			hql.append(", atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()); // apenas para forçar o load de Pacientes

			final javax.persistence.Query query = createQuery(hql.toString());

			return query.getResultList();
		} else {
			return new LinkedList<AghAtendimentos>();
		}
		//		final DetachedCriteria criteriaAtendimentos = DetachedCriteria.forClass(AghAtendimentos.class);
		//		criteriaAtendimentos.add(Restrictions.in(AghAtendimentos.Fields.SEQUENCE.toString(), atdSeqs));
		//		
		//		return executeCriteria(criteriaAtendimentos);
	}
	

	/**
	 * Retorna o atendimento vigente de um paciente. Retorna null caso nÃ£o
	 * encontre.
	 * 
	 * @param paciente
	 * @return atendimento
	 */
	public AghAtendimentos obterAtendimentoVigente(final AipPacientes paciente) {

		if (paciente == null) {
			return null;
		}

		final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);

	}
	
	public AghAtendimentos obterAtendimentoComPaciente(Integer seqAtendimento) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PAC."+AipPacientes.Fields.MAE_PACIENTE.toString(), "MAE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ALTA_SUMARIO.toString(), "ALTA_SUMARIO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public AghAtendimentos obterAtendimentoVigenteDetalhado(final AipPacientes paciente) {
		if (paciente != null) {
			final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();
			criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
//			FAVOR NÃƒO DESCOMENTAR LINHA ABAIXO
//			criteria.createAlias(AghAtendimentos.Fields.PRESCRICOES_MEDICAS.toString(),
//					"PrescricoesMedicas", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUA", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UF", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("UF."+AghUnidadesFuncionais.Fields.CARACTERISTICAS, "CARAC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() , "ESP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ESP."+AghEspecialidades.Fields.CLINICA.toString(), "CLIN", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
			return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
		}
		return null;
	}

	/**
	 * Retorna o atendimento vigente de um paciente. Retorna null caso nÃ£o
	 * encontre.
	 * 
	 * @param paciente
	 * @return atendimento
	 */
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagem(final AipPacientes paciente) {

		if (paciente == null) {
			return null;
		}

		final DetachedCriteria criteria = criarBaseCriteriaOrigemAtendimentosEnfermagem();

		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "uni", JoinType.LEFT_OUTER_JOIN );
		criteria.createAlias("uni."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "carac", JoinType.LEFT_OUTER_JOIN );
		criteria.setFetchMode(AghAtendimentos.Fields.PRESCRICOES_MEDICAS.toString(), FetchMode.JOIN );
		criteria.setFetchMode(AghAtendimentos.Fields.PRESCRICOES_ENFERMAGEM.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AghAtendimentos.Fields.QUARTO.toString(), FetchMode.JOIN );
		criteria.setFetchMode(AghAtendimentos.Fields.LEITO.toString(), FetchMode.JOIN );
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentosPorNumeroConsulta(final Integer numeroConsulta) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));

		return executeCriteria(criteria);

	}

	/**
	 * Retorna um List com o atendimento vigente do paciente. Em tese deveria
	 * sempre retornar 1 registro, porÃ©m, caso haja inconsistÃªncia do banco,
	 * mais registros serÃ£o retornados e o devido tratamento deve ser feito no
	 * local onde este mÃ©todo for chamado
	 * 
	 * @param leito
	 * @return List<AghAtendimentos>
	 */
	public List<AghAtendimentos> pesquisarAtendimentoVigente(final AinLeitos leito) {

		if (leito == null) {
			return new ArrayList<AghAtendimentos>();
		}

		final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.LEITO.toString(), leito));

		return executeCriteria(criteria);

	}

	/**
	 * Retorna um List com o atendimento vigente do paciente. Em tese deveria
	 * sempre retornar 1 registro, porÃ©m, caso haja inconsistÃªncia do banco,
	 * mais registros serÃ£o retornados e o devido tratamento deve ser feito no
	 * local onde este mÃ©todo for chamado
	 * 
	 * Utilizado na prescriÃ§Ã£o de enfermagem.
	 * 
	 * @param leito
	 * @return List<AghAtendimentos>
	 */
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(final AinLeitos leito) {

		if (leito == null) {
			return new ArrayList<AghAtendimentos>();
		}

		final DetachedCriteria criteria = criarBaseCriteriaOrigemAtendimentosEnfermagem();
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "uni", JoinType.LEFT_OUTER_JOIN );
		criteria.createAlias("uni."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "carac", JoinType.LEFT_OUTER_JOIN );
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.LEITO.toString(), leito));

		return executeCriteria(criteria);

	}
	
	/**
	 * Retorna o atendimento de internacao vigente do paciente. Em tese deveria
	 * sempre retornar 1 registro, porÃ©m, caso haja inconsistÃªncia do banco,
	 * mais registros serÃ£o retornados e o devido tratamento deve ser feito no
	 * local onde este mÃ©todo for chamado
	 * 
	 * @param paciente
	 * @return atendimento
	 */	
	public AghAtendimentos obterAtendimentoAtual(AipPacientes paciente) {

		if (paciente == null) {
			return null;
		}
		final DetachedCriteria criteria = criteriaBaseAtendimentoAtual();
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		List<AghAtendimentos> result = executeCriteria(criteria, 0, 1, AghAtendimentos.Fields.DTHR_INICIO.toString(), false);
		
		return result != null && !result.isEmpty() ? result.get(0) : null;
	}
	

	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorLeito(final AinLeitos leito) {
		if (leito == null) {
			return new ArrayList<AghAtendimentos>();
		}
		final DetachedCriteria criteria = criarBaseCriteriaOrigemAtendimentosExameLimitado();
		
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.LEITO.toString(), leito));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorProntuario(final Integer prontuario) {
		if (prontuario == null) {
			return new ArrayList<AghAtendimentos>();
		}
		final DetachedCriteria criteria = criarBaseCriteriaOrigemAtendimentosExameLimitado();
		
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		return executeCriteria(criteria);
	}
	
	public Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo) {
		final DetachedCriteria criteria = criarBaseCriteriaOrigemAtendimentosExameLimitado();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		return (executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * Retorna criteria base para atendimentos com os seguintes filtros:
	 * ind_pac_atendimento = 'S' and origem = 'I'
	 * 
	 * @return criteria
	 */
	private DetachedCriteria criteriaBaseAtendimentoAtual() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);

		// ind_pac_atendimento = 'S'
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		// origem in  ('I','H','U','N','A','C')
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.getOrigensPrescricaoInternacao()));

		return criteria;
	}
	
	/**
	 * Retorna criteria base para atendimentos com os seguintes filtros:
	 * ind_pac_atendimento = 'S' and origem in ('I','H','U','N')
	 * 
	 * @return criteria
	 */
	private DetachedCriteria criteriaBaseAtendimentoVigente() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		// ind_pac_atendimento = 'S'
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		// origem in ('I','H','U','N')
		criteria.add(Restrictions.in(
				AghAtendimentos.Fields.ORIGEM.toString(), 
				DominioOrigemAtendimento.getOrigensPrescricaoInternacao()
				));

		return criteria;
	}

	/**
	 * Retorna o atendimento de internacao vigente do paciente. Em tese deveria
	 * sempre retornar 1 registro, porÃ©m, caso haja inconsistÃªncia do banco,
	 * mais registros serÃ£o retornados e o devido tratamento deve ser feito no
	 * local onde este mÃ©todo for chamado
	 * 
	 * @param paciente
	 * @return atendimento
	 */
	public AghAtendimentos obterAtendimentoInternacaoAtual(AipPacientes paciente) {
		if (paciente == null) {
			return null;
		}
		final DetachedCriteria criteria = criteriaBaseAtendimentoInternacaoAtual();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DTHR_INICIO.toString()));
		List<AghAtendimentos> result = executeCriteria(criteria);
		return  result.get(0);
	}

	/**
	 * Retorna criteria base para atendimentos com os seguintes filtros:
	 * ind_pac_atendimento = 'S' and origem = 'I'
	 * 
	 * @return criteria
	 */
	private DetachedCriteria criteriaBaseAtendimentoInternacaoAtual() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);
		// ind_pac_atendimento = 'S'
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		// origem = ('I','H','U','N','A','C')
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.getOrigensPrescricaoInternacao()));
		return criteria;
	}
	
	
	/**
	 * Retorna disjuntion das subqueries criadas a partir das tabelas de
	 * configuração da lista de pacientes.
	 * 
	 * @param servidor
	 * @return
	 */
	private Disjunction getDisjuntionSubqueriesConfiguracao(final RapServidores servidor, List<RapServidores> pacAtdEqpResp, boolean mostrarPacientesCpa) {
		final Disjunction disjunction = Restrictions.disjunction();

		// condições adicionais as sub queries
		final Criterion emAndamento = Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S);
		LogicalExpression restrictions = null;

		// subqueries

		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}

		// 1. pacientes em atendimento na especialidade
		final DetachedCriteria criteriaEspecialidadeConfiguradaPara = DetachedCriteria.forClass(MpmListaServEspecialidade.class);
		criteriaEspecialidadeConfiguradaPara.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), servidor));
		criteriaEspecialidadeConfiguradaPara.setProjection(Projections.property("especialidade"));

		final Criterion subqueryEspecialidades = Property.forName("especialidade").in(criteriaEspecialidadeConfiguradaPara);
		restrictions = Restrictions.and(subqueryEspecialidades, emAndamento);
		disjunction.add(restrictions);

		// 2. pacientes em atendimento na unidade funcional		
		final DetachedCriteria criteriaUnidadeFuncionalConfiguradaPara = DetachedCriteria.forClass(MpmServidorUnidFuncional.class);
		criteriaUnidadeFuncionalConfiguradaPara.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), servidor));
		criteriaUnidadeFuncionalConfiguradaPara.setProjection(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString()));

		final Criterion subqueryUnidadesFuncionais = Property.forName("unidadeFuncional").in(criteriaUnidadeFuncionalConfiguradaPara);
		restrictions = Restrictions.and(subqueryUnidadesFuncionais, emAndamento);
		disjunction.add(restrictions);

		// 3. pacientes em atendimento na equipe
		// 3.1. pacientes em atendimento pelo responsÃ¡vel
		if (!pacAtdEqpResp.isEmpty()) {
			restrictions = Restrictions.and(Restrictions.in("servidor", pacAtdEqpResp), emAndamento);
			disjunction.add(restrictions);
		}

		// 4. pacientes em acompanhamento pelo profissional
		final DetachedCriteria criteriaAcompProfissionalConfiguradaPara = DetachedCriteria.forClass(MpmPacAtendProfissional.class);
		criteriaAcompProfissionalConfiguradaPara.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), servidor));
		criteriaAcompProfissionalConfiguradaPara.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO.toString()));

		final Criterion subqueryAcompanhamento = Property.forName("seq").in(criteriaAcompProfissionalConfiguradaPara);
		restrictions = Restrictions.and(subqueryAcompanhamento, emAndamento);
		disjunction.add(restrictions);

		// 5. pacientes com cuidados pÃ³s anestÃ©sicos(cpa)
		if (mostrarPacientesCpa) {
			final Criterion comCpa = Restrictions.eq(AghAtendimentos.Fields.IND_PAC_CPA.toString(), true);
			final LogicalExpression le = Restrictions.and(emAndamento, comCpa);
			disjunction.add(le);
		}

		// 6. pacientes com sumÃ¡rio de alta pendente apÃ³s alta administrativa
		final DetachedCriteria criteriaSumarioPendenteConfiguradaPara = DetachedCriteria.forClass(MpmListaServSumrAlta.class);
		criteriaSumarioPendenteConfiguradaPara.add(Restrictions.eq(MpmListaServSumrAlta.Fields.SERVIDOR.toString(), servidor));
		criteriaSumarioPendenteConfiguradaPara.setProjection(Projections.property(MpmListaServSumrAlta.Fields.ATENDIMENTO.toString()));

		final Criterion subquerySumarioPendente = Property.forName("seq").in(criteriaSumarioPendenteConfiguradaPara);
		disjunction.add(subquerySumarioPendente);

		return disjunction;
	}

	/**
	 * Retorna query dos atendimentos com sumÃ¡rio de alta pendente e origens
	 * internaÃ§Ã£o, hospital dia, urgÃªncia e nascimento.<br>
	 * Criteria base para criaÃ§Ã£o da lista de pacientes do profissional.
	 * 
	 * @return
	 */
	private DetachedCriteria criteriaBaseListaPaciente() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		// com sumario de alta pendente
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA.toString(), DominioSituacaoSumarioAlta.P));

		// em origens
		criteria.add(Restrictions.in(
				AghAtendimentos.Fields.ORIGEM.toString(), 
				DominioOrigemAtendimento.getOrigensPrescricaoInternacao()
				));
		return criteria;
	}

	/**
	 * Retorna criteria de pesquisa para atendimentos em origens internaÃ§Ã£o,
	 * hospital dia, urgÃªncia e nascimento. (Que permitem internaÃ§Ã£o)
	 * 
	 * Origens de atendimento sÃ£o para prescriÃ§Ã£o de enfermagem.
	 * 
	 * @return
	 */
	private DetachedCriteria criarBaseCriteriaOrigemAtendimentosEnfermagem() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I,
			DominioOrigemAtendimento.H, DominioOrigemAtendimento.U, DominioOrigemAtendimento.N }));
		return criteria;
	}

	private DetachedCriteria criarBaseCriteriaOrigemAtendimentosExameLimitado() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I,
			DominioOrigemAtendimento.U, DominioOrigemAtendimento.N }));
		return criteria;
	}

	public AghAtendimentos obterAtendimentoPeloSeq(final Integer atdSeq) {
		final DetachedCriteria criteria = obterCriteriaBasica(atdSeq);
		criteria.createAlias(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "AU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "ATM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATM." + AghAtendimentos.Fields.PACIENTE.toString(), "ATMPAC", JoinType.LEFT_OUTER_JOIN);
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
	
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		return criteria;
	}

	public AghAtendimentos obterAghAtendimentosComInternacaoEAtendimentoUrgencia(final Integer atdSeq) {
		final DetachedCriteria criteria = obterCriteriaBasica(atdSeq);
	
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT."+AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString(), "INT_AU", JoinType.LEFT_OUTER_JOIN);
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);

	}
	
	public AghAtendimentos obterAtendimentoPeloSeqComFccCentroCusto(final Integer atdSeq) {

		if (atdSeq == null) {
			return null;
		}
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("UNF." +AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(),"CC", JoinType.INNER_JOIN);		
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);

	}	

	public List<AghAtendimentos> pesquisarPorProntuarioPacienteLista(final Integer prontuario) {
		final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(final String nomePesquisaFonetica, final String leitoPesquisaFonetica,
			final String quartoPesquisaFonetica, final AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada) {
		final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();

		// TODO incluir criterios para pesquisa fonetica
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> buscaAtendimentosSumarioPrescricao(final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		final StringBuffer hql = new StringBuffer(180);
		hql.append(" select atd");
		hql.append(" from AghAtendimentos atd");
		hql.append(" where atd.prontuario is not null and");
		hql.append(" atd.origem in (:DominioN, :DominioI) and");
		hql.append(" atd.dthrFim >= :dataInicio and");
		hql.append(" atd.dthrFim <= :dataFim");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setParameter("DominioN", DominioOrigemAtendimento.N);
		query.setParameter("DominioI", DominioOrigemAtendimento.I);

		return query.list();
	}

	private DetachedCriteria obterCriteriaAtendimento() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		return criteria;
	}

	/*
	 * EstÃ³ria #882 Verifica se o atendimento estÃ¡ vigente e se Ã© vÃ¡lido para prescriÃ§Ã£o -- Pode ser acessada a partir do atendimento (atd), hospital dia (hod), -- internaÃ§Ã£o (int) ou atendimento urgÃªncia (atu) --
	 * Retorna atd_seq, hod_seq, int_seq, atu_seq e unf_seq
	 */
	public AghAtendimentos obterPorAtendimentoVigente(final Integer c_atd_seq) {
		if (c_atd_seq == null) {
			return null;
		}
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), c_atd_seq));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public AghAtendimentos obterAtendimentoPorHospDiaVigente(final Integer c_hod_seq) {
		if (c_hod_seq == null) {
			return null;
		}
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.HOSPITAL_DIA.toString(), c_hod_seq));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public AghAtendimentos obterAtendimentoPorInternacaoVigente(final Integer c_int_seq) {
		if (c_int_seq == null) {
			return null;
		}
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INTERNACAO.toString(), c_int_seq));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * @author andremachado
	 * @param c_int_seq
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPorIntSeq(final Integer c_int_seq) {
		if (c_int_seq == null) {
			return null;
		}

		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), c_int_seq));
		List<AghAtendimentos> result = executeCriteria(criteria, 0, 1, null, true);

		if (!result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}

	public AghAtendimentos obterAtendimentoPorAtendimentoUrgenciaVigente(final Integer c_atu_seq) {
		if (c_atu_seq == null) {
			return null;
		}
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), c_atu_seq));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * @param gsoPacCodigo
	 * @param seqp
	 * @return
	 */
	public AghAtendimentos obterAtendimentoRecemNascido(final Integer gsoPacCodigo, final Byte seqp) {
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		final List<DominioOrigemAtendimento> origem = new ArrayList<DominioOrigemAtendimento>();
		origem.add(DominioOrigemAtendimento.I);
		origem.add(DominioOrigemAtendimento.U);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), seqp.shortValue()));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origem));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.SEQ.toString()));

		final List<AghAtendimentos> atendimentos = executeCriteria(criteria);
		if (atendimentos != null && atendimentos.size() > 0) {
			return atendimentos.get(0);
		}

		return null;
	}
	public Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigo) {
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), 
				new DominioOrigemAtendimento [] {DominioOrigemAtendimento.I, DominioOrigemAtendimento.N}));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.setProjection(Projections.max(AghAtendimentos.Fields.SEQ.toString()));
		return (Integer) super.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * @param gsoPacCodigo
	 * @param seqp
	 * @return
	 */
	public AghAtendimentos obterAtendimentoRecemNascido(final Integer gsoPacCodigo) {
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.SEQ.toString()));

		final List<AghAtendimentos> atendimentos = executeCriteria(criteria);
		if (atendimentos != null && atendimentos.size() > 0) {
			return atendimentos.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<AghAtendimentos> pesquisarAtendimentoParaSolicitacaoExames(final SolicitacaoExameFilter filter, final List<String> fonemasPaciente,
			final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		
		AtendimentoParaSolicitacaoExameQueryBuilder builder = atendimentoParaSolicitacaoExameBuilder.get(); 
		builder.setParameters(filter, fonemasPaciente, true, orderProperty, asc);
		builder.build();
		final javax.persistence.Query query = builder.getResult();

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AghAtendimentos> pesquisarAtendimentosPacientesInternados(final SolicitacaoExameFilter filter, final List<String> fonemasPaciente,
			final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {

		Boolean jaInternado = filter.getInternado();

		AtendimentoParaSolicitacaoExameQueryBuilder builder = atendimentoParaSolicitacaoExameBuilder.get(); 

		filter.setInternado(Boolean.TRUE);

		builder.setParameters(filter, fonemasPaciente, true, orderProperty, asc);
		builder.build();
		final javax.persistence.Query query = builder.getResult();

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		List<AghAtendimentos> result = query.getResultList();
		filter.setInternado(jaInternado);

		return result;
	}
	
	public Long pesquisarAtendimentoParaSolicitacaoExamesCount(final SolicitacaoExameFilter filter, final List<String> fonemasPaciente) {
		
		AtendimentoParaSolicitacaoExameCountQueryBuilder builder = atendimentoParaSolicitacaoExameCountBuilder.get(); 
		
		builder.setParameters(filter, fonemasPaciente, false, null, false);
		builder.build();

		final javax.persistence.Query query = builder.getResult();

		return Long.valueOf(query.getSingleResult().toString());
	}

	public Long pesquisarAtendimentosPacienteInternadoCount(
			final SolicitacaoExameFilter filter,
			final List<String> fonemasPaciente) {

		Boolean jaInternado = filter.getInternado();

		AtendimentoParaSolicitacaoExameCountQueryBuilder builder = atendimentoParaSolicitacaoExameCountBuilder
				.get();

		filter.setInternado(Boolean.TRUE);

		builder.setParameters(filter, fonemasPaciente, false, null, false);
		builder.build();

		final javax.persistence.Query query = builder.getResult();

		Long result = Long.valueOf(query.getSingleResult().toString());

        filter.setInternado(jaInternado);

		return result;
	}

	/**
	 * Busca um AghAtendimentos pelo id, nao atachado.<br>
	 * NAO utiliza evict.<br>
	 * Objeto retornado contera os valores existentes na base.<br>
	 * 
	 * @param novoAtdSeq
	 * @return
	 */
	public AghAtendimentos obterAtendimentoOriginal(final Integer novoAtdSeq) {
		if (novoAtdSeq == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}

		
		DetachedCriteria criteria = obterCriteriaAtendimento();
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.SERVIDOR.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.SERVIDOR_MOVIMENTO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.IND_PAC_PREMATURO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.HOSPITAL_DIA.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.INTERNACAO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.DTHR_FIM.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.DTHR_ULT_IMPR_SUMR_PRESCR.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.TIPO_LAMINA_LARINGO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.LEITO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.QUARTO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.ESPECIALIDADE.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.ORIGEM.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.DTHR_INGRESSO_UNIDADE.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.CTRLSUMRALTAPENDENTE.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.ATD_SEQ_MAE.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.GSO_SEQP.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.PACIENTE.toString()));
		projection.add(Projections.property(AghAtendimentos.Fields.CONSULTA.toString()));
		
		criteria.setProjection(projection);		

		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR_MOVIMENTO.toString(), "SEM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.HOSPITAL_DIA.toString(), "DIA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "MAE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), novoAtdSeq));
		
		/*final StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AghAtendimentos.Fields.SEQUENCE.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.SERVIDOR_MOVIMENTO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.DTHR_INICIO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.IND_PAC_PREMATURO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.HOSPITAL_DIA.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.INTERNACAO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.DTHR_ULT_IMPR_SUMR_PRESCR.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.TIPO_LAMINA_LARINGO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.LEITO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.QUARTO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.ESPECIALIDADE.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.ORIGEM.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.DTHR_INGRESSO_UNIDADE.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.CTRLSUMRALTAPENDENTE.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.ATD_SEQ_MAE.toString());
criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString()
		//TODO: Tarefa #14529 - Trocado para receber os atributos do id composto ao invÃ©s do objeto McoGestacoes
		//ApÃ³s a implantaÃ§Ã£o da Perinatologia avaliar se deverÃ¡ ser trocado pelo McoGestacoes de novo
		hql.append(", o.").append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.GSO_SEQP.toString());

		hql.append(", o.").append(AghAtendimentos.Fields.PACIENTE.toString());
		hql.append(", o.").append(AghAtendimentos.Fields.CONSULTA.toString());
		hql.append(" from ").append(AghAtendimentos.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.SERVIDOR.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.SERVIDOR_MOVIMENTO.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.HOSPITAL_DIA.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.INTERNACAO.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.LEITO.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.QUARTO.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.ESPECIALIDADE.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.ATD_SEQ_MAE.toString());

		//TODO: Tarefa #14529 - Retirado o LEFT JOIN com McoGestacoes. ApÃ³s a implantaÃ§Ã£o da Perinatologia avaliar se deve retornar

		hql.append(" left outer join o.").append(AghAtendimentos.Fields.PACIENTE.toString());
		hql.append(" left outer join o.").append(AghAtendimentos.Fields.CONSULTA.toString());
		// hql.append(" left outer join o.").append(AghAtendimentos.Fields.TIPO_LAMINA_LARINGO.toString());
		// hql.append(" left outer join o.").append(AghAtendimentos.Fields.ORIGEM.toString());
		hql.append(" where o.").append(AghAtendimentos.Fields.SEQUENCE.toString()).append(" = :pAtdSeq ");

		super.logInfo(hql.toString());
		final javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", novoAtdSeq); 
		
		final List<Object[]> lista = query.getResultList();*/
		
		AghAtendimentos returnValue = null;
		
		List<Object[]> lista = executeCriteria(criteria);
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AghAtendimentos();
			for (final Object[] listFileds : lista) {
				returnValue.setSeq((Integer) listFileds[0]);
				returnValue.setServidor((RapServidores) listFileds[1]);
				returnValue.setServidorMovimento((RapServidores) listFileds[2]);
				returnValue.setDthrInicio((Date) listFileds[3]);
				returnValue.setIndPacPediatrico((Boolean) listFileds[4]);
				returnValue.setIndPacPrematuro((Boolean) listFileds[5]);
				returnValue.setIndPacAtendimento((DominioPacAtendimento) listFileds[6]);
				returnValue.setHospitalDia((AhdHospitaisDia) listFileds[7]);
				returnValue.setInternacao((AinInternacao) listFileds[8]);
				returnValue.setAtendimentoUrgencia((AinAtendimentosUrgencia) listFileds[9]);
				returnValue.setDthrFim((Date) listFileds[10]);
				returnValue.setDthrUltImprSumrPrescr((Date) listFileds[11]);
				returnValue.setTipoLaminaLaringo((DominioTipoLaminaLaringo) listFileds[12]);
				returnValue.setLeito((AinLeitos) listFileds[13]);
				returnValue.setQuarto((AinQuartos) listFileds[14]);
				returnValue.setUnidadeFuncional((AghUnidadesFuncionais) listFileds[15]);
				returnValue.setEspecialidade((AghEspecialidades) listFileds[16]);
				returnValue.setOrigem((DominioOrigemAtendimento) listFileds[17]);
				returnValue.setDthrIngressoUnidade((Date) listFileds[18]);
				returnValue.setIndTipoTratamento((DominioTipoTratamentoAtendimento) listFileds[19]);
				returnValue.setIndSitSumarioAlta((DominioSituacaoSumarioAlta) listFileds[20]);
				returnValue.setCtrlSumrAltaPendente((DominioControleSumarioAltaPendente) listFileds[21]);
				returnValue.setAtendimentoMae((AghAtendimentos) listFileds[22]);
				returnValue.setGsoPacCodigo((Integer) listFileds[23]);
				returnValue.setGsoSeqp((Short) listFileds[24]);
				returnValue.setPaciente((AipPacientes) listFileds[25]);
				returnValue.setConsulta((AacConsultas) listFileds[26]);
			}
		}

		return returnValue;
	}

	/**
	 * Obtem a data de fim do atendimento.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public Date obterDataFimAtendimento(final Integer atdSeq) {
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.setProjection(Projections.property(AghAtendimentos.Fields.DTHR_FIM.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem a data de inicial do atendimento.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public Date obterDataInicioAtendimento(final Integer atdSeq) {
		final DetachedCriteria criteria = obterCriteriaAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.setProjection(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem uma lista de atendimentos de mÃ£es com atendimento em andamento (S)
	 * e prontuÃ¡rio especÃ­fico
	 * 
	 * @param prontuario
	 * @return lista de atendimentos de mÃ£es
	 */
	public List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(final Integer prontuario) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATE");
		criteria.createAlias("ATE."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATE."+AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT."+AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT."+AinInternacao.Fields.CONVENIO.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT."+AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "CNS", JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.SEXO.toString(), DominioSexo.F));
		criteria.add(Restrictions.eq("PAC."+ AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.eq("ATE."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("ATE."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));

		return executeCriteria(criteria);
	}

	/**
	 * Obtem uma lista de atendimentos de recÃ©m-nascidos de um atendimento de
	 * mÃ£e especÃ­fico
	 * 
	 * @param prontuario
	 * @return lista de atendimentos de recÃ©m-nascidos
	 */
	public List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(final AghAtendimentos aghAtendimentos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), AghAtendimentos.Fields.PACIENTE.toString());
		criteria.createAlias(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), AghAtendimentos.Fields.ATD_SEQ_MAE.toString());

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATD_SEQ_MAE.toString().concat(".seq"), aghAtendimentos.getSeq()));
		return executeCriteria(criteria);
	}

	/**
	 * MÃ©todo para obter a lista de atendimentos de uma determinada internaÃ§Ã£o
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosPorInternacao(final Integer seqInternacao) {
		final DetachedCriteria criteria = this.criarCriteriaAtendimentoSolicitacaoExames();

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.SEQ.toString(), seqInternacao));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * MÃ©todo para obter a lista de atendimentos de uma determinada internaÃ§Ã£o e que 
	 * NÃƒO sÃ£o tambÃ©m de atendimentos de urgÃªncia
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosInternacaoNaoUrgencia(final Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATU_SEQ.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * MÃ©todo para criar criteria basica para pesquisa na entidade
	 * AghAtendimentos
	 * 
	 * @return
	 */
	private DetachedCriteria criarCriteriaAtendimentoSolicitacaoExames() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString(), "solicitacaoExame");
		criteria.createAlias("solicitacaoExame." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "itensSolicitacaoExame");

		return criteria;
	}

	/**
	 * MÃ©todo para pesquisar os atendimetnos por tipo de atendimento 29
	 * (terapeutico) com data final nula e trpSeq nÃ£o nulo.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentoPacienteTipoAtendimento(final Integer codigoPaciente) {
		final DetachedCriteria criteria = this.criarCriteriaTipoAtendimentoPaciente(codigoPaciente, DominioTipoTratamentoAtendimento.VALOR_29);

		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.TRP_SEQ.toString()));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarTipoAtendimento(final Integer codigoPaciente, final DominioTipoTratamentoAtendimento tipoTratamento,
			final Date dataInicio, final Date dataFim) {
		final DetachedCriteria criteria = this.criarCriteriaTipoAtendimentoPaciente(codigoPaciente, tipoTratamento);

		return this.executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaTipoAtendimentoPaciente(final Integer codigoPaciente, final DominioTipoTratamentoAtendimento tipoTratamento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString(), tipoTratamento));

		return criteria;
	}

	public List<AghAtendimentos> pesquisarAtendimentosPorPacienteGestacao(final Integer codigoPacienteGestacao, final Short seqGestacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), codigoPacienteGestacao));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), seqGestacao));

		return this.executeCriteria(criteria);
	}

	/**
	 * ORADB cursor c_atd_pac
	 * @param atdSeq
	 * @return
	 */
	public Integer executarCursorAtdPac(final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class);
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));

		final ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentoPacientes.Fields.SEQ.toString()));
		criteria.setProjection(p);

		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * ORADB CURSOR c_atd_mae
	 * 
	 * @param atqSeqMae
	 * @return
	 */
	public List executarCursorAtdMae(final Integer atqSeqMae) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atqSeqMae));
		criteria.addOrder(Order.asc(AghAtendimentos.Fields.SEQ.toString()));

		final ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.ORIGEM.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.INT_SEQ.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.ATU_SEQ.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.HOSPITAL_DIA_SEQ.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	/**
	 * Consulta atendimentos filtrando pelo id da conta hospitalar de
	 * determinada conta internacao.
	 * 
	 * @param seqCth
	 * @return List<AghAtendimentos>
	 */
	public List<AghAtendimentos> obterAtendimentosDeContasInternacaoPorContaHospitalar(final Integer seqCth) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(),
				AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString());

		criteria.add(Restrictions.eq(
				AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString() + "." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), seqCth));

		return executeCriteria(criteria);

	}

	/**
	 * ORADB: RN_CTHC_VER_DADPARTO.C_VERIFICA_ABO
	 */
	public DominioRNClassificacaoNascimento verificarAbo(final Integer pIntSeq) {
		final StringBuffer hql = new StringBuffer(120);

		hql.append(" select NAS.").append(McoNascimentos.Fields.RN_CLASSIFICACAO.toString())

		.append(" from ").append(AghAtendimentos.class.getName()).append("  as atd, ").append(McoNascimentos.class.getName()).append(" as nas ")

		.append(" where ").append("     atd.").append(AghAtendimentos.Fields.INT_SEQ.toString()).append(" = :pIntSeq ")

		.append(" and atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = atd.").append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString())

		.append(" and nas.").append(McoNascimentos.Fields.GSO_PAC_CODIGO.toString()).append(" = atd.")
		.append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString())

		.append(" and nas.").append(McoNascimentos.Fields.GSO_SEQP.toString()).append(" = atd.").append(AghAtendimentos.Fields.GSO_SEQP.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("pIntSeq", pIntSeq);

		final DominioRNClassificacaoNascimento rnclassificacaoNascimento = (DominioRNClassificacaoNascimento) query.uniqueResult();

		return rnclassificacaoNascimento;
	}

	/**
	 * ORADB: RN_CTHC_VER_DADPARTO.C_DADOS_REG_CIVIL
	 */
	public DadosRegistroCivilVO obterDadosRegistroCivil(final Integer pIntSeq) {
		final StringBuffer hql = new StringBuffer(180);

		hql.append(" select").append("   PDS.").append(AipPacientesDadosCns.Fields.NUMERO_DN.toString()).append(" as ")
		.append(DadosRegistroCivilVO.Fields.NUMERO_DN.toString()).append(" , PAC.").append(AipPacientes.Fields.REG_NASCIMENTO.toString())
		.append(" as ").append(DadosRegistroCivilVO.Fields.REG_NASCIMENTO.toString())

		.append(" from ").append(AghAtendimentos.class.getName()).append("  AS ATD, ").append(McoRecemNascidos.class.getName()).append(" AS RNA, ")
		.append(AipPacientesDadosCns.class.getName()).append(" AS PDS, ").append(AipPacientes.class.getName()).append(" as PAC ")

		.append(" where ").append("     atd.").append(AghAtendimentos.Fields.INT_SEQ.toString()).append(" = :pIntSeq ")

		.append(" and atd.").append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()).append(" = rna.")
		.append(McoRecemNascidos.Fields.GSO_PAC_CODIGO.toString())

		.append(" and atd.").append(AghAtendimentos.Fields.GSO_SEQP.toString()).append(" = rna.").append(McoRecemNascidos.Fields.GSO_SEQP.toString())

		.append(" and pds.").append(AipPacientesDadosCns.Fields.PAC_CODIGO.toString()).append(" = rna.")
		.append(McoRecemNascidos.Fields.PAC_CODIGO.toString())

		.append(" and pds.").append(AipPacientesDadosCns.Fields.CODIGO_PACIENTE.toString()).append(" = pac.")
		.append(AipPacientes.Fields.CODIGO.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("pIntSeq", pIntSeq);
		query.setResultTransformer(Transformers.aliasToBean(DadosRegistroCivilVO.class));

		final List<DadosRegistroCivilVO> resultados = query.list();
		if (resultados != null && !resultados.isEmpty()) {
			return resultados.get(0);
		}

		return null;
	}

	/**
	 * Consulta primeiro atendimento filtrando pelo id da conta hospitalar de
	 * determinada conta internacao.
	 * 
	 * @param seqCth
	 * @return AghAtendimentos
	 */
	public Integer obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(final Integer seqCth) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(),
				AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString());

		criteria.add(Restrictions.eq(
				AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString() + "." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), seqCth));

		criteria.setProjection(Projections.property(AghAtendimentos.Fields.SEQ.toString()));

		final List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;

	}

	/**
	 * Retorna os atendimentos apresentados na lista de pacientes internados <br>
	 * <b>MÃ³dulo:</b> Controle de Paciente
	 * 
	 * @param servidor
	 * @return
	 */
	public List<PacienteInternadoVO> listarControlePacientesInternados(final RapServidores servidor) {

		final DetachedCriteria criteria = this.criteriaBaseAtendimentoVigente();

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), AghAtendimentos.Fields.PACIENTE.toString());
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), AghAtendimentos.Fields.ESPECIALIDADE.toString());
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), AghAtendimentos.Fields.LEITO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), AghAtendimentos.Fields.QUARTO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());

		final ProjectionList pList = Projections.projectionList();
		pList.add(Property.forName(AghAtendimentos.Fields.SEQ.toString()), "atdSeq");
		pList.add(Property.forName(AghAtendimentos.Fields.PAC_CODIGO.toString()), "pacCodigo");
		pList.add(Property.forName(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()), "nome");
		pList.add(Property.forName(AghAtendimentos.Fields.PRONTUARIO.toString()), "prontuario");
		pList.add(Property.forName(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), "dataNascimento");
		pList.add(Property.forName(AghAtendimentos.Fields.DTHR_INICIO.toString()), "dataInicioAtendimento");
		pList.add(Property.forName(AghAtendimentos.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()), "especialidade");
		pList.add(Property.forName(AghAtendimentos.Fields.LEITO.toString() + "." + AinLeitos.Fields.LTO_ID.toString()), "leitoId");
		pList.add(Property.forName(AghAtendimentos.Fields.QUARTO.toString() + "." + AinQuartos.Fields.NUMERO.toString()), "nroQuarto");
		pList.add(Property.forName(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ANDAR.toString()), "andarUnidade");
		pList.add(Property.forName(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ALA.toString()), "alaUnidade");
		pList.add(Property.forName(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),
		"descricaoUnidade");

		pList.add(Property.forName(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), "unfSeq");

		criteria.setProjection(pList);

		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatÃ³rio");
		}
		DetachedCriteria criteriaEcpServidorUnidFuncional = DetachedCriteria.forClass(EcpServidorUnidFuncional.class);
		criteriaEcpServidorUnidFuncional.add(Restrictions.eq(EcpServidorUnidFuncional.Fields.RAP_SERVIDORES.toString(), servidor));
		criteriaEcpServidorUnidFuncional.setProjection(Projections.property(EcpServidorUnidFuncional.Fields.AGH_UNIDADES_FUNCIONAIS.toString()));

		criteria.add(Subqueries.propertyIn(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), criteriaEcpServidorUnidFuncional));

		criteria.addOrder(Order.asc(AghAtendimentos.Fields.LEITO.toString()));
		criteria.addOrder(Order.asc(AghAtendimentos.Fields.QUARTO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(PacienteInternadoVO.class));

		return executeCriteria(criteria);

	}

	public AghAtendimentos buscarAtendimentoContaParto(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "fci");
		criteria.createAlias("fci." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "cth");

		criteria.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.SEQ.toString(), cthSeq));

		//TODO: Tarefa #14529 - Trocado o isNotNull no field MCO_GESTACOES para o field GSO_PAC_CODIGO.
		//ApÃ³s a implantaÃ§Ã£o da Perinatologia avaliar se deve retornar MCO_GESTACOES
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));

		final List<AghAtendimentos> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Long verificaBebeInternadoCount(final Integer atdSeqMae) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "mae");

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "int");

		criteria.createAlias("int." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "fci");

		criteria.createAlias("fci." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "cth");

		criteria.add(Restrictions.eq("mae." + AghAtendimentos.Fields.SEQ.toString(), atdSeqMae));

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));

		criteria.add(Restrictions.neProperty(AghAtendimentos.Fields.PAC_CODIGO.toString(), AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));

		return executeCriteriaCount(criteria);
	}

	public Integer verificaSSMPartoCount(final Integer cthSeq, final Long[] codigosTabela) {
		final StringBuffer hql = new StringBuffer(330);

		hql.append(" select count(atd) ");
		hql.append(" from ");
		hql.append(AghAtendimentos.class.getName());
		hql.append(" as atd ");
		hql.append(" join atd.");
		hql.append(AghAtendimentos.Fields.INTERNACAO.toString());
		hql.append(" as int ");
		hql.append(" join int.");
		hql.append(AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString());
		hql.append(" as fci ");
		hql.append(" join fci.");
		hql.append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		hql.append(" as cth ");
		hql.append(" left join cth.");
		hql.append(FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString());
		hql.append(" as phi ");
		hql.append(" join phi.");
		hql.append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString());
		hql.append(" as cgi ");
		hql.append(" left join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and atd.");
		hql.append(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString());
		hql.append(" is not null ");
		hql.append(" and atd.");
		hql.append(AghAtendimentos.Fields.GSO_SEQP.toString());
		hql.append(" is not null ");
		hql.append(" and iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" in (:codigosTabela) ");
		hql.append(" and (cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" is null or cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = cth.");
		hql.append(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString());
		hql.append(" )");
		hql.append(" and (cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" is null or cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = cth.");
		hql.append(FatContasHospitalares.Fields.CSP_SEQ.toString());
		hql.append(" )");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameterList("codigosTabela", codigosTabela);

		final Long retValue = (Long) query.uniqueResult();

		return retValue != null ? retValue.intValue() : 0;
	}

	public AghAtendimentos buscarAtendimentosPorCodigoInternacao(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));

		final List<AghAtendimentos> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public boolean existeAtendimentoComSolicitacaoExame(final AacConsultas consulta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.CONSULTA.toString(), consulta));
		criteria.add(Restrictions.isNotEmpty(AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString()));

		return !executeCriteria(criteria).isEmpty();
	}

	/**
	 * 
	 * @param dominioOrigemAtendimento
	 * @param consultaNumero
	 * @return
	 */
	public List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(final DominioOrigemAtendimento dominioOrigemAtendimento, final Integer consultaNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), AghAtendimentos.Fields.CONSULTA.toString());
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.CONSULTA.toString() + "." + AacConsultas.Fields.NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), dominioOrigemAtendimento));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.LTO_LTO_ID.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATU_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.HOD_SEQ.toString()));
		return executeCriteria(criteria);

	}

	/**
	 * MÃ©todo que pesquisa atendimentos pela unidade funcional e pela data de
	 * referÃªncia Caso os atendimentos contenham prescriÃ§Ãµes mÃ©dicas, traz
 	 * somente as que tiverem a data de referÃªncia igual Ã  recebida por
	 * parÃ¢metro
	 * 
	 * @param unidadeFuncional
	 * @param dtReferencia
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosaPorUnidadeDataReferencia(final AghUnidadesFuncionais unidadeFuncional, final Date dtReferencia) {
		final StringBuffer hql = new StringBuffer(200);
		hql.append(" select atd ");
		hql.append(" from ").append(MpmPrescricaoMedica.class.getName()).append(" mpm ");
		hql.append(" inner join mpm.atendimento").append(" atd ");
		hql.append(" where ");
		hql.append(" atd.").append(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()).append(" = :pacAtendimento ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(" in( :origens )");
		hql.append(" and atd.").append(AghAtendimentos.Fields.UNF_SEQ.toString()).append(" = :unfSeq ");
		hql.append(" and mpm.").append(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()).append(" = to_date(:dtReferencia, 'yyyyMMdd')");
		//hql.append(" and atd.").append(AghAtendimentos.Fields.SEQ.toString()).append(" = mpm.");
		//hql.append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString());
	
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtReferencia", DateUtil.dataToString(dtReferencia, "yyyyMMdd"));
		query.setParameter("pacAtendimento", DominioPacAtendimento.S);
		query.setParameterList("origens", Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.H));
		query.setParameter("unfSeq", unidadeFuncional.getSeq());

		final List<AghAtendimentos> ret = query.list();
		for (final AghAtendimentos att : ret) {
			final Query filter = createFilterHibernate(att.getMpmPrescricaoMedicases(),
					"where this." + MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString() + " = to_date(:dtReferencia, 'yyyyMMdd')").setParameter("dtReferencia",
							DateUtil.dataToString(dtReferencia, "yyyyMMdd"));
			att.setMpmPrescricaoMedicases(new HashSet(filter.list()));
		}

		return ret;
	}

	/**
	 * 
	 * @param consultaNumero
	 * @return
	 */
	public DetachedCriteria criteriaAtendimentosPorConsulta(final Integer consultaNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		final String aliasConsulta = AghAtendimentos.Fields.CONSULTA.toString();
		criteria.createAlias(aliasConsulta, aliasConsulta);
		criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATU_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.APE_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.HOD_SEQ.toString()));
		return criteria;
	}

	/**
	 * 
	 * @param consultaNumero
	 * @return
	 */
	public List<AghAtendimentos> listarAtendimentosPorConsulta(final Integer consultaNumero) {
		DetachedCriteria criteria = this.criteriaAtendimentosPorConsulta(consultaNumero);
		return executeCriteria(criteria);

	}

	/**
	 * 
	 * @param consultaNumero
	 * @return
	 */
	public AghAtendimentos primeiroAtendimentosPorConsulta(final Integer consultaNumero) {
		DetachedCriteria criteria = this.criteriaAtendimentosPorConsulta(consultaNumero);
		List<AghAtendimentos> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ReimpressaoLaudosProcedimentosVO> listarReimpressaoLaudosProcedimentosVO(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer pacCodigo, final Integer pacProntuario) {
		final Query query = criarListarReimpressaoLaudosProcedimentosVOQuery(pacCodigo, pacProntuario, false, orderProperty, asc);

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResult != null) {
			query.setMaxResults(maxResult);
		}

		return query.list();
	}

	public Long listarReimpressaoLaudosProcedimentosVOCount(final Integer pacCodigo, final Integer pacProntuario) {
		final Query query = criarListarReimpressaoLaudosProcedimentosVOQuery(pacCodigo, pacProntuario, true, null, null);
		return ((Long) query.uniqueResult());
	}

	private Query criarListarReimpressaoLaudosProcedimentosVOQuery(final Integer pacCodigo, final Integer pacProntuario, final boolean isCount,
			final String orderProperty, final Boolean asc) {
		final StringBuffer hql = new StringBuffer(380);

		hql.append(" select ");

		if (isCount) {
			hql.append("count(*)");
		} else {
			hql.append(" new br.gov.mec.aghu.faturamento.vo.ReimpressaoLaudosProcedimentosVO( ");
			hql.append("atd.").append(AghAtendimentos.Fields.SEQ.toString());
			hql.append(", atd.").append(AghAtendimentos.Fields.DTHR_INICIO.toString());
			hql.append(", atd.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
			hql.append(", asu.").append(MpmAltaSumario.Fields.APA_SEQ.toString());
			hql.append(", asu.").append(MpmAltaSumario.Fields.SEQP.toString());
			hql.append(" ) ");
		}

		hql.append(" from ");
		hql.append(AghAtendimentos.class.getName());
		hql.append(" as atd ");
		hql.append(" left join atd.");
		hql.append(AghAtendimentos.Fields.ALTA_SUMARIO.toString());
		hql.append(" as asu with (");
		hql.append(" asu.").append(MpmAltaSumario.Fields.IND_CONCLUIDO.toString()).append(" = :indConcluido ");
		hql.append(" and asu.").append(MpmAltaSumario.Fields.IND_TIPO.toString()).append(" = :indTipo ");
		hql.append(" ) left join asu.");
		hql.append(MpmAltaSumario.Fields.CONVENIO_SAUDE.toString());
		hql.append(" as cnv with (");
		hql.append(" cnv.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio ");
		hql.append(" ) ");
		hql.append(" where ");
		hql.append(" atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(" = :origem ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.INT_SEQ.toString()).append(" in (");
		hql.append(" select int.").append(AinInternacao.Fields.SEQ.toString());
		hql.append(" from ");
		hql.append(AinInternacao.class.getName());
		hql.append(" as int ");
		hql.append(" join int.").append(AinInternacao.Fields.CONVENIO.toString()).append(" as cnv2 ");
		hql.append(" where ");
		hql.append(" int.").append(AinInternacao.Fields.COD_PACIENTE.toString()).append(" = atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" and cnv2.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio2 ");
		hql.append(" ) ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString()).append(" = :pacProntuario ");

		if (!isCount) {
			if (StringUtils.isNotBlank(orderProperty)) {
				hql.append(" ORDER BY ");
				hql.append(orderProperty);

				if (asc != null) {
					hql.append(asc ? " asc " : " desc ");
				}
			} else {
				hql.append(" ORDER BY atd.seq desc ");
			}
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("grupoConvenio", DominioGrupoConvenio.S);
		query.setParameter("grupoConvenio2", DominioGrupoConvenio.S);
		query.setParameter("indConcluido", DominioIndConcluido.S);
		query.setParameter("indTipo", DominioIndTipoAltaSumarios.ALT);
		query.setParameter("origem", DominioOrigemAtendimento.I);
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("pacProntuario", pacProntuario);

		return query;
	}

	public AghAtendimentos obterAtendimento(final Integer seq, final DominioPacAtendimento pacAtendimento, List<DominioOrigemAtendimento> origensAtendimento) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));
		if (pacAtendimento != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), pacAtendimento));
		}
		if (origensAtendimento != null && !origensAtendimento.isEmpty()) {
			criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origensAtendimento));
		}
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public List<AghAtendimentos> listarAtendimentosPorNumeroConsulta(final Integer consultaNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), consultaNumero));
		return executeCriteria(criteria);
	}
	
	public List<AghAtendimentos> listarAtendimentosPorNumeroConsultaOrdenado(final Integer consultaNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), consultaNumero));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarAtendimentosPorConsultaComInternacaoAtendumentoUrgenciaAtendumentoPacienteExternoNulo(final Integer numeroConsulta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));

		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString()));

		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INTERNACAO.toString()));

		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString()));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarPorAtendimentoPacienteExterno(final AghAtendimentosPacExtern atdPacExterno) {
		if (atdPacExterno == null || atdPacExterno.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), atdPacExterno));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarAtendimentoEmAnadamentoConsulta(final Integer numeroConsulta) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));

		criteria.add(Restrictions.or(
				Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.HOSPITAL_DIA.toString()),
						Restrictions.isNotNull(AghAtendimentos.Fields.DCA_BOL_NUMERO.toString())),
						Restrictions.isNotNull(AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString())));

		return executeCriteria(criteria);
	}

	/**
	 * Busca AghAtendimentos com o <b>codigoPaciente</b> informado;<br>
	 * E que tenha <b>origem</b> Internacao (I);<br>
	 * E <b>indicador de atendimento</b> Em andamento (S).<br>
	 * Algo semelhante a: <i>
	 * <p>
	 * select atd.seq, int.csp_seq plano, int.csp_cnv_codigo convenio from
	 * ain_internacoes int, agh_atendimentos atd where atd.pac_codigo =
	 * codigoPaciente and atd.ind_pac_atendimento = 'S' and atd.origem = 'I' and
	 * atd.int_seq = int.seq
	 * </p>
	 * </i>
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public List<AghAtendimentos> listarAtendimentosComInternacaoEmAndamentoPorPaciente(final Integer codigoPaciente) {
		if (codigoPaciente == null) {
			throw new IllegalArgumentException("Codigo do paciente deve ser informado!!!");
		}
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString(), JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaAtendimentoAmbCronologico() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC");
		criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP");

		criteria.add(Restrictions.sqlRestriction(isOracle() ? " trunc(this_.DTHR_INICIO) <= trunc(sysdate) "
				: "  date_trunc('day', this_.DTHR_INICIO) <= date_trunc('day', now())"));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.APE_SEQ.toString()));

		DetachedCriteria subQuery = DetachedCriteria.forClass(MamEmgEspecialidades.class, "EM_ESP");
		subQuery.add(Restrictions.eqProperty("EM_ESP." + MamEmgEspecialidades.Fields.ESP_SEQ.toString(),
				"GAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		subQuery.setProjection(Projections.projectionList().add(Projections.property("EM_ESP." + MamEmgEspecialidades.Fields.ESP_SEQ.toString())));
		criteria.add(Subqueries.propertyNotIn("GAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), subQuery));
		
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DTHR_INICIO.toString()));

		return criteria;
	}

	private DetachedCriteria montarCriteriaAtendimentoAmb() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC");		
		
		criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "PSR", JoinType.LEFT_OUTER_JOIN);	//
		criteria.createAlias("PSR." + RapServidores.Fields.PESSOA_FISICA.toString(), "PSF", JoinType.LEFT_OUTER_JOIN);                ///  
		
		criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP");
		criteria.createAlias("EQP." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "PRS", JoinType.LEFT_OUTER_JOIN); // 
		criteria.createAlias("PRS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PFS", JoinType.LEFT_OUTER_JOIN);         ///

		criteria.add(Restrictions.sqlRestriction(isOracle() ? " trunc(this_.DTHR_INICIO) <= trunc(sysdate) "
				: "  date_trunc('day', this_.DTHR_INICIO) <= date_trunc('day', now())"));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.APE_SEQ.toString()));

		DetachedCriteria subQuery = DetachedCriteria.forClass(MamEmgEspecialidades.class, "MEE_INNER");
		subQuery.add(Restrictions.eqProperty("MEE_INNER." + MamEmgEspecialidades.Fields.ESP_SEQ.toString(),
				"GAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		subQuery.setProjection(Projections.projectionList().add(Projections.property("MEE_INNER." + MamEmgEspecialidades.Fields.ESP_SEQ.toString())));
		criteria.add(Subqueries.propertyNotIn("GAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), subQuery));

		return criteria;
	}

	public List<AghAtendimentos> pesquisarAtendimentoAmbCronologicoPorPacCodigo(Integer codigo) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmbCronologico();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));

		return executeCriteria(criteria);
	}
	
	public List<AghAtendimentos> pesquisarAtendimentoAmbCronologicoPorProntuario(Integer codigo) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmbCronologico();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), codigo));
		return executeCriteria(criteria);
	}	

	public Long pesquisarAtendimentoAmbPorPacCodigoCount(Integer codigo, String seqs) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmb();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		
		if (!StringUtils.isBlank(seqs)) {
			seqs = seqs.substring(1);
			List<String> seqStr = Arrays.asList(seqs.split("\\s*,\\s*"));
			List<Integer> seqList = new ArrayList<Integer>();
			for (String s : seqStr) {
				seqList.add(Integer.valueOf(s));
			}
			criteria.add(Restrictions.in(AghAtendimentos.Fields.SEQ.toString(), seqList));
		}
		
		return executeCriteriaCount(criteria);
	}
	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String seqs) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmb();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ESP."+AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "ESP_GEN", JoinType.LEFT_OUTER_JOIN);

		if (!StringUtils.isBlank(seqs)) {
			seqs = seqs.substring(1);
			List<String> seqStr = Arrays.asList(seqs.split("\\s*,\\s*"));
			List<Integer> seqList = new ArrayList<Integer>();
			for (String s : seqStr) {
				seqList.add(Integer.valueOf(s));
			}
			criteria.add(Restrictions.in(AghAtendimentos.Fields.SEQ.toString(), seqList));

		}

		criteria.addOrder(new OrderSQL(isOracle() ? "trunc(this_.DTHR_INICIO) desc" : "date_trunc('day', this_.DTHR_INICIO) desc"));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim(Integer codigo, Date dtIni, Date dtFim) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmb();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));

		criteria.add(Restrictions.sqlRestriction(isOracle() ? " trunc(this_.DTHR_INICIO) between trunc(to_date('"
				+ DateUtil.dataToString(dtIni, "ddMMyyyy") + "', 'DDMMYYYY')) AND trunc(to_date('" + DateUtil.dataToString(dtFim, "ddMMyyyy")
				+ "', 'DDMMYYYY')) " : "  date_trunc('day', this_.DTHR_INICIO) between date_trunc('day', to_date('" + DateUtil.dataToString(dtIni, "ddMMyyyy")
				+ "', 'DDMMYYYY')) AND date_trunc('day', to_date('" + DateUtil.dataToString(dtFim, "ddMMyyyy") + "', 'DDMMYYYY'))"));

		criteria.addOrder(new OrderSQL(isOracle() ? " trunc(this_.DTHR_INICIO) asc" : " date_trunc('day', this_.DTHR_INICIO) asc"));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentoAmbPorProntuarioEntreDtInicioEDtFim(Integer codigo, Date dtIni, Date dtFim) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmb();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), codigo));

		criteria.add(Restrictions.sqlRestriction(isOracle() ? " trunc(this_.DTHR_INICIO) between trunc(to_date('"
				+ DateUtil.dataToString(dtIni, "ddMMyyyy") + "', 'DDMMYYYY')) AND trunc(to_date('" + DateUtil.dataToString(dtFim, "ddMMyyyy")
				+ "', 'DDMMYYYY')) " : "  date_trunc('day', this_.DTHR_INICIO) between date_trunc('day', to_date('" + DateUtil.dataToString(dtIni, "ddMMyyyy")
				+ "', 'DDMMYYYY')) AND date_trunc('day', to_date('" + DateUtil.dataToString(dtFim, "ddMMyyyy") + "', 'DDMMYYYY'))"));

		criteria.addOrder(new OrderSQL(isOracle() ? " trunc(this_.DTHR_INICIO) asc" : " date_trunc('day', this_.DTHR_INICIO) asc"));

		return executeCriteria(criteria);
	}	

	public boolean verificarExisteAtendimentoAmbCronologicoPorPacCodigo(Integer codigo) {

		DetachedCriteria criteria = montarCriteriaAtendimentoAmbCronologico();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		
		return this.executeCriteriaExists(criteria);
	}
	

	public boolean verificarExisteAtendimentoAmbCronologicoPorProntuario(Integer codigo) {

		DetachedCriteria criteria = montarCriteriaAtendimentoAmbCronologico();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), codigo));
		
		return this.executeCriteriaExists(criteria);
	}

	public Long pesquisarAtendimentoAmbPorProntuarioCount(Integer codigo, String seqs) {
		DetachedCriteria criteria = montarCriteriaAtendimentoAmb();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), codigo));

		if (!StringUtils.isBlank(seqs)) {
			seqs = seqs.substring(1);
			List<String> seqStr = Arrays.asList(seqs.split("\\s*,\\s*"));
			List<Integer> seqList = new ArrayList<Integer>();
			for (String s : seqStr) {
				seqList.add(Integer.valueOf(s));
			}
			criteria.add(Restrictions.in(AghAtendimentos.Fields.SEQ.toString(), seqList));
		}

		return executeCriteriaCount(criteria);
	}	


	//SoluÃ§Ã£o exclusiva para o AGHU 3.0 em funÃ§Ã£o da impossibilidade de atualizaÃ§Ã£o do jar do mec-infra
	public class OrderSQL extends Order {

		private static final long serialVersionUID = -4440566154138899586L;
		private String orderSQL;

		/**
		 * Constructor for Order.
		 * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
		 */
		public OrderSQL(String orderSQL) {
			super(orderSQL, true);
			this.orderSQL = orderSQL;
		}

		public String toString() {
			return orderSQL;
		}

		public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			return orderSQL;
		}
	}

	public List<Date> listarDthrInicioAtendimentosPorCodigoInternacao(final Integer intSeq, final Integer firstResult, final Integer maxResults) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));

		criteria.setProjection(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()));

		if (firstResult != null && maxResults != null) {
			return executeCriteria(criteria, firstResult, maxResults, null, false);
		} else {
			return executeCriteria(criteria);
		}
	}

	// 5465
	public AghAtendimentos obterUltimoAtendimentoPorCodigoEOrigem(final Integer codPaciente, final DominioOrigemAtendimento... origemAtendimentos) {

		final DetachedCriteria criteria = getCriteriaObterAtendimentoPorCodigoEOrigem(codPaciente, origemAtendimentos);

		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DTHR_INICIO.toString()));

		final List<AghAtendimentos> listaAtendimento = executeCriteria(criteria);

		if (listaAtendimento != null && listaAtendimento.size() != 0) {
			return listaAtendimento.get(0);
		}

		return null;
	}
	
	public List<AghAtendimentos> obterAtendimentoPorCodigoEOrigem(final Integer codPaciente) {
		
		DominioOrigemAtendimento[] origemAtendimentos = new DominioOrigemAtendimento[]{DominioOrigemAtendimento.U,
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.N};
		
		final DetachedCriteria criteria = getCriteriaObterAtendimentoPorCodigoEOrigem(codPaciente, origemAtendimentos);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DTHR_INICIO.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaObterAtendimentoPorCodigoEOrigem(Integer codPaciente, DominioOrigemAtendimento[] origemAtendimentos) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origemAtendimentos));
		return criteria;
	}

	// Fim 5465
	@SuppressWarnings("PMD.NPathComplexity")
	public Long pesquisaPacientesComAltaCount(final Date dataInicial, final Date dataFinal, final boolean altaAdministrativa,
			final DominioTipoAlta tipoAlta, final Short unidFuncSeq, final Short espSeq, final String tamCodigo) {
		Long count = 0l;
		final StringBuilder hql = new StringBuilder(600);
		hql.append(" SELECT ");
		hql.append(" count (*) ");
		hql.append(" FROM ");

		hql.append(" AghAtendimentos 			atd ");
		hql.append(" join atd.unidadeFuncional 	vuf ");
		hql.append(" join atd.internacao 		int ");
		hql.append(" left join atd.leito 		lei ");
		hql.append(" join int.especialidade 	esp ");
		hql.append(" join int.paciente			pac ");
		hql.append(" left outer join atd.altasSumario asu  with (asu.concluido = :indConcl )");
		hql.append(" left outer join int.observacaoPacienteAlta obs ");

		hql.append(" WHERE ");

		if (altaAdministrativa) {
			hql.append(" int.dtSaidaPaciente >= :dataInicio ");
			hql.append(" AND int.dtSaidaPaciente <= :dataFim ");
		} else {
			hql.append(" asu.dthrAlta >= :dataInicio ");
			hql.append(" AND asu.dthrAlta <= :dataFim ");
			hql.append(" and int.indPacienteInternado = :indPacienteInternado ");
		}
		if (DominioTipoAlta.O.equals(tipoAlta)) {
			hql.append(" AND asu.tipo = :tipoAlta");
		} else if (DominioTipoAlta.E.equals(tipoAlta)) {
			hql.append(" AND (asu.tipo is null OR asu.tipo != :tipoAlta)");
		}
		if (unidFuncSeq != null) {
			hql.append(" AND vuf.seq = :unidFuncSeq ");
		}
		if (espSeq != null) {
			hql.append(" AND  esp.seq = :espSeq ");
		}
		if (tamCodigo != null) {
			hql.append(" AND int.tipoAltaMedica.codigo = :tamCodigo ");
		}

		final javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("indConcl", DominioIndConcluido.S);

		query.setParameter("dataInicio", dataInicial);
		query.setParameter("dataFim", dataFinal);
		if (!altaAdministrativa) {
			query.setParameter("indPacienteInternado", true);
		}

		if (DominioTipoAlta.O.equals(tipoAlta) || DominioTipoAlta.E.equals(tipoAlta)) {// Se for 'O' ou 'E'
			query.setParameter("tipoAlta", DominioIndTipoAltaSumarios.OBT);
		}
		if (unidFuncSeq != null) {
			query.setParameter("unidFuncSeq", unidFuncSeq);
		}
		if (espSeq != null) {
			query.setParameter("espSeq", espSeq);
		}
		if (tamCodigo != null) {
			query.setParameter("tamCodigo", tamCodigo);
		}

		count = (Long) query.getSingleResult();

		return count.longValue();

	}

	public AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATEN");
		subCriteria.createAlias("ATEN." + AghAtendimentos.Fields.PACIENTE.toString(), "PACI");
		subCriteria.add(Restrictions.eq("PACI." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		subCriteria.add(Restrictions.eq("ATEN." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		subCriteria.setProjection(Projections.max("ATEN."+ AghAtendimentos.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyEq("ATD." + AghAtendimentos.Fields.SEQ.toString(), subCriteria));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * ORADB View V_AIN_ALTAS
	 * 
	 * @return List
	 */
	private String obterViewAinAltas() {
		final StringBuilder hql = new StringBuilder(1000);
		hql.append("SELECT ");
		hql.append(" new br.gov.mec.aghu.internacao.vo.VAinAltasVO( ");
		hql.append(" pac.prontuario, ");
		hql.append(" pac.nome, ");
		hql.append(" servProf.id.matricula, ");
		hql.append(" servProf.id.vinCodigo, ");
		hql.append(" clin.codigo, ");
		hql.append(" esp.seq, ");
		hql.append(" esp.sigla, ");
		hql.append(" atd.dthrInicio, ");
		hql.append(" qrt.numero, ");
		hql.append(" lei.leitoID, ");
		hql.append(" vuf.andar, ");
		hql.append(" vuf.indAla, ");
		hql.append(" asu.dthrAlta , ");
		hql.append(" int.dtSaidaPaciente, ");
		hql.append(" asu.tipo, ");
		hql.append(" int.indPacienteInternado, ");
		hql.append(" vuf.seq, ");
		hql.append(" tam.descricao, ");
		hql.append(" convSaude.codigo, ");
		hql.append(" convSaudePlano.id.seq, ");
		hql.append(" obs.codigo, ");
		hql.append(" obs.descricao, ");
		hql.append(" int.seq ");
		hql.append(" ) ");

		hql.append(" FROM ");
		hql.append(" AghAtendimentos 					atd ");
		hql.append(" left join atd.quarto				qrt ");
		hql.append(" join atd.unidadeFuncional 			vuf ");
		hql.append(" join atd.internacao 				int ");
		hql.append(" left join atd.leito 				lei ");
		hql.append(" join int.especialidade 			esp ");
		hql.append(" join int.paciente					pac ");
		hql.append(" left join int.tipoAltaMedica		tam ");
		hql.append(" left join int.convenioSaude		convSaude ");
		hql.append(" left join int.convenioSaudePlano	convSaudePlano ");
		hql.append(" left join int.servidorProfessor	servProf ");
		hql.append(" left join esp.clinica				clin");
		hql.append(" left outer join atd.altasSumario asu  with (asu.concluido = 'S' ) ");
		hql.append(" left outer join int.observacaoPacienteAlta obs ");

		return hql.toString();
	}

	@SuppressWarnings({ "PMD.NPathComplexity" })
	public List<VAinAltasVO> pesquisaPacientesComAlta(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Date dataInicial, final Date dataFinal, final boolean altaAdministrativa, final DominioTipoAlta tipoAlta,
			final DominioOrdenacaoPesquisaPacComAlta ordenacao, final Short unidFuncSeq, final Short espSeq, final String tamCodigo) {

		final StringBuilder hql = new StringBuilder(300);

		hql.append(this.obterViewAinAltas());

		hql.append(" WHERE ");

		if (altaAdministrativa) {
			hql.append(" int.dtSaidaPaciente >= :dataInicio ");
			hql.append(" AND int.dtSaidaPaciente <= :dataFim ");
		} else {
			hql.append(" asu.dthrAlta >= :dataInicio ");
			hql.append(" AND asu.dthrAlta <= :dataFim ");
			hql.append(" and int.indPacienteInternado = :indPacienteInternado ");
		}
		if (DominioTipoAlta.O.equals(tipoAlta)) {
			hql.append(" AND asu.tipo = :tipoAlta");
		} else if (DominioTipoAlta.E.equals(tipoAlta)) {
			hql.append(" AND (asu.tipo is null OR asu.tipo != :tipoAlta) ");
		}
		if (unidFuncSeq != null) {
			hql.append(" AND vuf.seq = :unidFuncSeq ");
		}
		if (espSeq != null) {
			hql.append(" AND  esp.seq = :espSeq ");
		}
		if (tamCodigo != null) {
			hql.append(" AND int.tipoAltaMedica.codigo = :tamCodigo ");
		}

		if (DominioOrdenacaoPesquisaPacComAlta.U.equals(ordenacao)) {
			hql.append(" ORDER BY vuf.seq, asu.dthrAlta ");
		} else {
			hql.append(" ORDER BY pac.nome ");
		}

		final javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("dataInicio", dataInicial);
		query.setParameter("dataFim", dataFinal);
		if (!altaAdministrativa) {
			query.setParameter("indPacienteInternado", true);
		}

		if (DominioTipoAlta.O.equals(tipoAlta) || DominioTipoAlta.E.equals(tipoAlta)) {// Se for 'O' ou 'E'
			query.setParameter("tipoAlta", DominioIndTipoAltaSumarios.OBT);
		}
		if (unidFuncSeq != null) {
			query.setParameter("unidFuncSeq", unidFuncSeq);
		}
		if (espSeq != null) {
			query.setParameter("espSeq", espSeq);
		}
		if (tamCodigo != null) {
			query.setParameter("tamCodigo", tamCodigo);
		}

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		@SuppressWarnings("unchecked")
		final List<VAinAltasVO> listaAltas = query.getResultList();

		return listaAltas;
	}

//	public List<AghAtendimentos> obterUnidadesAlta(final Integer codigo, final Date dataUltInternacao) {
//		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
//		criteria.add(Restrictions.eq(AghAtendimentos.Fields.CODIGO_PACIENTE.toString(), codigo));
//		criteria.add(Restrictions.lt(AghAtendimentos.Fields.DATA_HORA_INICIO.toString(), dataUltInternacao));
//		criteria.add(Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()),
//				Restrictions.isNotNull(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString())));
//		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
//
//		return executeCriteria(criteria);
//	}
	
	
	public List<AghAtendimentos> obterUnidadesAlta(final Integer codigo, final Date dataUltInternacao, Boolean buscarDtInt) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		// Feito teste para garantir que 'buscarDtInt' nao eh nulo, pois em producao ocorreu nullpointer nesta validacao
		if(buscarDtInt != null && buscarDtInt){
			criteria.add(Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), dataUltInternacao));
			criteria.add(Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), dataUltInternacao));
			criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U)));
		} else {
			criteria.add(Restrictions.lt(AghAtendimentos.Fields.DATA_HORA_INICIO.toString(), dataUltInternacao));
			criteria.add(Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()),
			Restrictions.isNotNull(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString())));
		}
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion19(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status) {

		final StringBuffer hql = new StringBuffer(800);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	urg.dtAtendimento, esp.sigla, tpa.codigo,");
		hql.append(" 	urg.seq, pac.dtNascimento, tpa.descricao, esp.nomeEspecialidade, atd.seq");
		hql.append(" from AghAtendimentos atd");
		hql.append(" left join atd.internacao as int ");
		hql.append(" join atd.atendimentoUrgencia as urg ");
		hql.append(" left join urg.especialidade as esp ");
		hql.append(" join urg.paciente as pac ");
		hql.append(" join atd.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join urg.tipoAltaMedica as tpa");
		hql.append(" left join urg.quarto as qrt");
		hql.append(" left join urg.leito as lto");
		hql.append(" where atd.indPacAtendimento = :indPacAtd ");
		hql.append(" and int.seq is null");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unf.unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("indPacAtd", DominioPacAtendimento.S);

		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	public AghAtendimentos obterAtendimentoPorPaciente(final Integer codigoPaciente, final DominioPacAtendimento indPacAtendimento, final String caracteristica) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), indPacAtendimento));
		final DetachedCriteria criteriaUnidadeFuncional = criteria.createCriteria("unidadeFuncional", JoinType.INNER_JOIN);
		final DetachedCriteria criteriaCaracteristica = criteriaUnidadeFuncional.createCriteria("caracteristicas", JoinType.INNER_JOIN);
		criteriaCaracteristica.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristica));

		return (AghAtendimentos) this.executeCriteriaUniqueResult(criteria);
	}

	public List<AghAtendimentos> obterAtendimentosPorPaciente(
			final Integer codigoPaciente,Integer atdSeq,
			final DominioPacAtendimento indPacAtendimento,
			ConstanteAghCaractUnidFuncionais ... caracteristicas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		if(codigoPaciente != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
		if(atdSeq != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}
		if (indPacAtendimento != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), indPacAtendimento));
		}
		final DetachedCriteria criteriaUnidadeFuncional = criteria.createCriteria("unidadeFuncional", JoinType.INNER_JOIN);
		final DetachedCriteria criteriaCaracteristica = criteriaUnidadeFuncional.createCriteria("caracteristicas", JoinType.INNER_JOIN);
		criteriaCaracteristica.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicas));

		return this.executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentosPorPaciente(final Integer codigoPaciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * @param convSusPadrao
	 * @return
	 */
	private DetachedCriteria createPesquisaProcedenciaPacienteCriteria(final Short convSusPadrao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		// AIP_PACIENTES
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), AghAtendimentos.Fields.PACIENTE.toString());

		// AGH_UNIDADES_FUNCIONAIS
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());

		// AIN_QUARTOS
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), AghAtendimentos.Fields.QUARTO.toString(), JoinType.LEFT_OUTER_JOIN);

		// RAP_SERVIDORES
		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR.toString(), AghAtendimentos.Fields.SERVIDOR.toString());

		// RAP_PESSOAS_FISICAS
		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA,
				RapServidores.Fields.PESSOA_FISICA.toString());

		// AIN_INTERNACOES
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), AghAtendimentos.Fields.INTERNACAO.toString());

		// AGH_ESPECIALIDADES
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), AghAtendimentos.Fields.ESPECIALIDADE.toString());

		criteria.setProjection(Projections.projectionList()

				// 0-Integer
				.add(Projections.property(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString()))

				// 1-Integer
				.add(Projections.property(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO.toString()))

				// 2-String
				.add(Projections.property(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()))

				// 3-String
				.add(Projections.property(AghAtendimentos.Fields.LTO_LTO_ID.toString()))

				// 4-String
				.add(Projections.property(AghAtendimentos.Fields.QUARTO.toString() + "." + AinQuartos.Fields.DESCRICAO.toString()))

				// 5-Byte
				.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ANDAR.toString()))

				// 6-AghAla
				.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ALA.toString()))

				// 7-String
				.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()))

				// 8-Short
				.add(Projections.property(AghAtendimentos.Fields.UNF_SEQ.toString()))

				// 9-String
				.add(Projections.property(AghAtendimentos.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.SIGLA.toString()))

				// 10-String
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME_USUAL.toString()))

				// 11-String
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME.toString()))

				// 12-Date
				.add(Projections.property(AghAtendimentos.Fields.DATA_HORA_INICIO.toString())));

		// Solicitado em Homologação AGHU para filtrar apenas pacientes do SUS
		if (convSusPadrao != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.CONVENIO_CODIGO.toString(), convSusPadrao));
		}
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		final List<DominioOrigemAtendimento> origem = new ArrayList<DominioOrigemAtendimento>();
		origem.add(DominioOrigemAtendimento.I);
		origem.add(DominioOrigemAtendimento.U);
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origem));

		return criteria;
	}

	/**
	 * @param convSusPadrao
	 * @return
	 */
	public List<Object[]> pesquisarProcedenciaPaciente(final Short convSusPadrao) {
		final DetachedCriteria criteria = this.createPesquisaProcedenciaPacienteCriteria(convSusPadrao);
		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoPorNumeroConsulta(final Integer pConNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), pConNumero));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public AghAtendimentos obterAtendimentoPorNumeroConsultaLeitoProntuario(Integer pProntuario, Integer pConNumero, String leitoID) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "uf", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "le", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "qa", JoinType.LEFT_OUTER_JOIN);

		if (pProntuario != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), pProntuario));
		}

		if (pConNumero != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), pConNumero));
		}

		if (leitoID != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoID));
		}

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		subCriteria.setProjection(Projections.max("atd." + AghAtendimentos.Fields.SEQ.toString()));
		//subCriteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (pProntuario != null) {
			subCriteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), pProntuario));
		}

		if (pConNumero != null) {
			subCriteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), pConNumero));
		}

		if (leitoID != null) {
			subCriteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoID));
		}

		criteria.add(Subqueries.propertyEq(AghAtendimentos.Fields.SEQ.toString(), subCriteria));

		/* Em atendimento */
		//criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));


		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria createCriteriaPesquisaAtendimento(final Integer prontuario, final String leitoID) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoID));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (prontuario != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		}
		return criteria;
	}

	public List<AghAtendimentos> pesquisaAtendimento(final Integer prontuario, final String leitoID) throws ApplicationBusinessException {
		final DetachedCriteria criteria = createCriteriaPesquisaAtendimento(prontuario, leitoID);
		return executeCriteria(criteria);
	}

	/**
	 * Metodo auxiliar para validacao de datas.
	 * 
	 * @param pacCodigo
	 * @return Date
	 */
	public Date getDataHoraFim(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I,
			DominioOrigemAtendimento.U }));

		criteria.setProjection(Projections.projectionList().add(Projections.property(AghAtendimentos.Fields.DTHR_FIM.toString())));

		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DTHR_INICIO.toString()));

		final List<Object> list = this.executeCriteria(criteria, 0, 1, null, true);

		if (list != null && !list.isEmpty()) {
			final Date dtHrFim = (Date) list.get(0);
			if (dtHrFim == null) {
				return new Date();
			} else {
				return dtHrFim;
			}
		}
		return null;
	}

	public List<AghAtendimentos> listarAtendimentos(final Integer pacCodigo, final Byte seqTipoTratamento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString(), seqTipoTratamento));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.DTHR_FIM.toString()));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarAtendimentosPorSeqInternacao(final Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), seqInternacao));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarAtendimentosPorSeqHospitalDia(final Integer seqHospitalDia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.HOSPITAL_DIA_SEQ.toString(), seqHospitalDia));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> listarAtendimentosPorSeqAtendimentoUrgencia(final Integer seqAtendimentoUrgencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATU_SEQ.toString(), seqAtendimentoUrgencia));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(final AipPacientes paciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N // Nascimento
		));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S // Em
				// Andamento
		));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(final Integer seqAtendimentoUrgencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATU_SEQ.toString(), seqAtendimentoUrgencia));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqInternacao(final Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), seqInternacao));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(final Integer seqHospitalDia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.HOSPITAL_DIA_SEQ.toString(), seqHospitalDia));

		return executeCriteria(criteria);
	}

	public Integer recuperarCodigoPaciente(final Integer altanAtdSeq) throws ApplicationBusinessException {
		Integer pacCodigo = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), altanAtdSeq));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghAtendimentos.Fields.PAC_CODIGO.toString())));
		pacCodigo = (Integer) executeCriteriaUniqueResult(criteria);
		return pacCodigo;
	}

	public List<AghAtendimentos> listarAtendimentosPorMcoGestacoes(final Integer gsoPacCodigo, final Short gsoSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));

		return executeCriteria(criteria);
	}

	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR c_gestacoes)
	 * 
	 * @param intSeq
	 * @return
	 */
	public AghAtendimentos obterAtendimentoGestacao(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));
		criteria.add(Restrictions.neProperty(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), AghAtendimentos.Fields.PAC_CODIGO.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.ATD_SEQ_MAE.toString()));
		final List<AghAtendimentos> listaAtendimentosGestacoes = executeCriteria(criteria, 0, 1, null, true);
		if (listaAtendimentosGestacoes != null && !listaAtendimentosGestacoes.isEmpty()) {
			return listaAtendimentosGestacoes.get(0);
		}
		return null;
	}

	/**
	 * Pesquisa por atendimentos de gestações
	 * 
	 * @param intSeq
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosGestacoes(final Integer intSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));
		criteria.add(Restrictions.neProperty(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), AghAtendimentos.Fields.PAC_CODIGO.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.ATD_SEQ_MAE.toString()));

		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoExames(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método que obtém o atendimento da internação
	 * 
	 * @param intSeq
	 * @return
	 */
	public AghAtendimentos obterAtendimentoInternacao(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		final List<AghAtendimentos> listaAtendimentos = executeCriteria(criteria, 0, 1, null, true);
		if (listaAtendimentos.size() > 0) {
			return listaAtendimentos.get(0);
		}
		return null;
	}

	public List<AghAtendimentos> pesquisarAtendimentosMaeGestacao(final AghAtendimentos atendimentoGestacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), atendimentoGestacao));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), atendimentoGestacao.getGsoPacCodigo()));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), atendimentoGestacao.getGsoSeqp()));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAghAtendimentos(final Integer pacCodigo, final Boolean origemCirurgia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		if (origemCirurgia) {
			// o atendimento e a pmr estão sendo atualizados por regras do AAC
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.C));
		}

		return executeCriteria(criteria);
	}

	/**
	 * Obtem os dados do AhdHospitaisDia do paciente através de seu código,
	 * ordenadado decrescentemente pela data de início do atendimento.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 */
	public AghAtendimentos obterUltimoAtendimento(final Integer aipPacientesCodigo) {
		AghAtendimentos aghAtendimentos = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), aipPacientesCodigo));

		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));

		final List<AghAtendimentos> listaAghAtendimentos = executeCriteria(criteria);

		if (listaAghAtendimentos.size() > 0) {
			aghAtendimentos = listaAghAtendimentos.get(0);
		}
		return aghAtendimentos;
	}

	public List<AghAtendimentos> pesquisarAtendimentosGestacoesEmAtendimento(final Integer gsoPacCodigo, final Short gsoSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentosGestacoesFinalizados(final Integer gsoPacCodigo, final Short gsoSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.N));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.DTHR_FIM.toString()));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisarAtendimentosEmAtendimentoPeloAtendimentoMae(final AghAtendimentos atendimentoMae) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), atendimentoMae));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<AinInternacao> listarInternacoesDoAtendimento(final Integer seqAtendimento, final Date novaData) {
		final StringBuffer hql = new StringBuffer(190);
		hql.append(" select int");
		hql.append(" from AghAtendimentos atd ");
		hql.append(" 	join atd.internacao as int ");
		hql.append(" where atd.seq = :seqAtendimento ");
		hql.append(" 	and (");
		hql.append(" 		int.dthrAltaMedica >= :novaData or int.dthrAltaMedica is null ");
		hql.append(" 	) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("novaData", novaData);

		return query.list();
	}

	public List<Object[]> pesquisarDadosPacienteAtendimento(final Integer codPaciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), AghAtendimentos.Fields.PACIENTE.toString());

		if (codPaciente != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(), codPaciente));
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		}

		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString())).add(// 1
						Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())).add(// 2
								Projections.property(AghAtendimentos.Fields.DTHR_INGRESSO_UNIDADE.toString())));

		return executeCriteria(criteria);
	}

	public boolean possuiRecemNascido(final AghAtendimentos atendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), atendimento));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		if (executeCriteriaCount(criteria) > 0) {
			return true;
		}
		return false;
	}

	public AghAtendimentos obtemAtendimentoPaciente(final AipPacientes paciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		final List<AghAtendimentos> lAghAtendimentos = executeCriteria(criteria);

		if (lAghAtendimentos.size() == 0) {
			return null;
		}

		return lAghAtendimentos.get(0);
	}

	public AghAtendimentos buscaAtendimentoPorNumeroConsulta(final Integer numeroConsulta) {
		AghAtendimentos atendimento = null;

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "uni", JoinType.LEFT_OUTER_JOIN );
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));
		final List<AghAtendimentos> atendimentos = executeCriteria(criteria);

		if (atendimentos != null && !atendimentos.isEmpty()) {
			atendimento = atendimentos.get(0);
		}

		return atendimento;
	}

	public List<AghAtendimentos> pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(final Integer numeroConsulta, final Date dthrRealizado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		final String aliasConsulta = "con";
		final String separador = ".";
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), aliasConsulta);
		criteria.add(Restrictions.eq(aliasConsulta + separador + AacConsultas.Fields.NUMERO, numeroConsulta));
		criteria.add(Restrictions.or(Restrictions.lt(aliasConsulta + separador + AacConsultas.Fields.DATA_CONSULTA, dthrRealizado),
				Restrictions.eq(aliasConsulta + separador + AacConsultas.Fields.DATA_CONSULTA, dthrRealizado)));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("deprecation")
	public void inserirCentroObstetrico(final Integer pAtdSeq, final Integer pCth, final Date pDtInter, final Date pDtAlta, final Integer pOpcao,
			final String usuarioLogado) throws ApplicationBusinessException {
		if (!isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH.toString() + "." + ObjetosBancoOracleEnum.RN_FATP_INS_CENTRO_OBSTETRICO.toString();
		try {
			// FATK_INTERFACE_MCO.RN_FATP_INS_CENTRO_OBSTETRICO(v_atd_seq,
			// l_cth_row_new.seq, l_cth_saved_row.dt_int_administrativa,
			// l_cth_row_new.dt_alta_administrativa, v_opcao);

			this.doWork(new AghWork(usuarioLogado) {
				@Override
				public void executeAghProcedure(final Connection connection) throws SQLException {

					CallableStatement cs = null;
					try {
						cs = connection.prepareCall("{call " + nomeObjeto + "(?,?,?,?,?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, pAtdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, pCth);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, pDtInter);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, pDtAlta);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER, pOpcao);
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
		} catch (final Exception e) {
			final String valores = CoreUtil.configurarValoresParametrosCallableStatement(pAtdSeq, pCth, pDtInter, pDtAlta, pOpcao);
			LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
	}

	/**
	 * Retorna o atendimento da última gestação da mãe
	 * @param pacienteMae
	 * @return
	 */
	public AghAtendimentos obterUltimoAtendimentoGestacao(AipPacientes pacienteMae) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PACIENTE.toString(), pacienteMae));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString()));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.GSO_SEQP.toString()));
		List<AghAtendimentos> lista = executeCriteria(criteria);
		if (lista.isEmpty()) {
			return null;
		}
		return lista.get(0);

	}

	/**
	 * ORADB V_AIN_SITUACAO_LEITOS
	 * 
	 * @param clinica
	 * @return
	 */
	public SituacaoLeitosVO pesquisaSituacaoSemLeitos() {
		SituacaoLeitosVO situacoesLeitos = new SituacaoLeitosVO();
		situacoesLeitos.setQuantidade(0);

		DetachedCriteria cri = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		cri.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);

		cri.setProjection(Projections.projectionList().add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.count("UNF." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString()), "quantidade")
				.add(Projections.groupProperty("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString())));

		cri.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		cri.add(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I }));

		cri.add(Restrictions.isNull("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString()));
		List<Object[]> ocupados = this.executeCriteria(cri);

		Iterator<Object[]> it = ocupados.iterator();
		//consulta pronta caso queira buscar as quantidades por Unidades funcionais
		while (it.hasNext()) {
			Object[] ocupado = it.next();
			if (ocupado[1] != null) {
				//situacoesLeitos.setQuantidade((Integer) ocupado[1] + situacoesLeitos.getQuantidade());
				situacoesLeitos.setQuantidade(Integer.valueOf(ocupado[1].toString()) + situacoesLeitos.getQuantidade());
			}
		}
		return situacoesLeitos;
	}

	public DetachedCriteria obterCriteriaAtendimentos(Integer prontuario, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "att");
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), data));
		return criteria;
	}

	public List<AtendimentosVO> pesquisarAtendimentosInternacao(Integer prontuario, Date data, Short vlrOrigem) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
						.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
								AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
								.add(Projections.property("internacao" + "." + AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString()),
										AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR.toString())
										.add(Projections.property("internacao" + "." + AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString()),
												AtendimentosVO.Fields.MATRICULA_SERVIDOR.toString())));

		//Joins com tabelas AinInternacoes, AghEspecialidades e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "internacao");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");

		// Where
		criteria.add(Restrictions.ne("internacao" + "." + AinInternacao.Fields.ORIGEM_EVENTO.toString() + "." + AghOrigemEventos.Fields.SEQ.toString(),
				vlrOrigem));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	public List<AtendimentosVO> pesquisarAtendimentosInternacaoEmergencia(Integer prontuario, Date data, Short vlrOrigem) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
						.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
								AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
								.add(Projections.property("internacao" + "." + AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString()),
										AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR.toString())
										.add(Projections.property("internacao" + "." + AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString()),
												AtendimentosVO.Fields.MATRICULA_SERVIDOR.toString())));

		//Joins com tabelas AinInternacoes, AghEspecialidades e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "internacao");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");

		// Where
		criteria.add(Restrictions.eq("internacao" + "." + AinInternacao.Fields.ORIGEM_EVENTO.toString() + "." + AghOrigemEventos.Fields.SEQ.toString(),
				vlrOrigem));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	public List<AtendimentosVO> pesquisarAtendimentosSO(Integer prontuario, Date data) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
						.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
								AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
								.add(Projections.property("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()),
										AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR.toString())
										.add(Projections.property("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()),
												AtendimentosVO.Fields.MATRICULA_SERVIDOR.toString())
												.add(Projections.property("equipe" + "." + AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString()),
														AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR_2.toString())
														.add(Projections.property("equipe" + "." + AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString()),
																AtendimentosVO.Fields.MATRICULA_SERVIDOR_2.toString())));

		//Joins com tabelas AacConsultas, AghEspecialidades, AacGradeAgendamenConsultas, AghEquipes e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "atendimentoUrgencia");
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "consulta");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");
		criteria.createAlias("consulta" + "." + AacConsultas.Fields.RETORNO.toString(), "retorno");
		criteria.createAlias("consulta" + "." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgendamenConsulta");
		criteria.createAlias("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe");

		// Where
		criteria.add(Restrictions.eq("retorno" + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.ATU_SEQ.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	public List<AtendimentosVO> pesquisarAtendimentosConsultas(Integer prontuario, Date data) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
						.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
								AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
								.add(Projections.property("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()),
										AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR.toString())
										.add(Projections.property("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()),
												AtendimentosVO.Fields.MATRICULA_SERVIDOR.toString())
												.add(Projections.property("equipe" + "." + AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString()),
														AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR_2.toString())
														.add(Projections.property("equipe" + "." + AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString()),
																AtendimentosVO.Fields.MATRICULA_SERVIDOR_2.toString())));

		//Joins com tabelas AacConsultas, AghEspecialidades, AacGradeAgendamenConsultas, AghEquipes e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "consulta");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");
		criteria.createAlias("consulta" + "." + AacConsultas.Fields.RETORNO.toString(), "retorno");
		criteria.createAlias("consulta" + "." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgendamenConsulta");
		criteria.createAlias("gradeAgendamenConsulta" + "." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe");

		// Where
		criteria.add(Restrictions.eq("retorno" + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.APE_SEQ.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));

		// RESTRICAO VINDA DA FUNCAO AACC_VER_SIT_GRADE 
		criteria.add(Subqueries.exists(getCriteriaListarVerificaSituacaoGrade()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaListarVerificaSituacaoGrade() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.setProjection(Projections.property(AacGradeSituacao.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.eqProperty(AacGradeSituacao.Fields.GRD_SEQ.toString(), "consulta" + "." + AacConsultas.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.leProperty(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(),
				"consulta" + "." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.add(Restrictions.or(Restrictions.isNull(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
				Restrictions.geProperty(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString(), "consulta" + "." + AacConsultas.Fields.DATA_CONSULTA.toString())));
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<AtendimentosVO> pesquisarAtendimentosCirurgias(Integer prontuario, Date data) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
				.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
						AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
				.add(Projections.property("profCirurgias" + "." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()),
						AtendimentosVO.Fields.VIN_CODIGO_SERVIDOR.toString())
				.add(Projections.property("profCirurgias" + "." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()),
						AtendimentosVO.Fields.MATRICULA_SERVIDOR.toString())

		));

		//Joins com tabelas AacConsultas, AghEspecialidades, AacGradeAgendamenConsultas, AghEquipes e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");
		criteria.createAlias(AghAtendimentos.Fields.CIRURGIAS.toString(), "cirurgias");
		criteria.createAlias("cirurgias" + "." + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "profCirurgias");

		// Where
		criteria.add(Restrictions.eq("cirurgias" + "." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), true));
		criteria.add(Restrictions.eq("profCirurgias" + "." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.APE_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.NUMERO_CONSULTA.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	public List<AtendimentosVO> pesquisarAtendimentosPacExterno(Integer prontuario, Date data) {

		DetachedCriteria criteria = obterCriteriaAtendimentos(prontuario, data);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosVO.Fields.DATA_ATENDIMENTO.toString())
				.add(Projections.property("especialidade" + "." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()),
						AtendimentosVO.Fields.NOME_REDUZIDO_ESPECIALIDADE.toString())
						.add(Projections.property("centroCusto" + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
								AtendimentosVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())));

		//Joins com tabelas AacConsultas, AghEspecialidades, AacGradeAgendamenConsultas, AghEquipes e FccCentroCustos
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");

		// Where
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.NUMERO_CONSULTA.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.PRONTUARIO.toString()));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.APE_SEQ.toString()));

		//COMENTAR SEGUNDO O FRED
		//criteria.add(Restrictions.isNull(AghAtendimentos.Fields.ATEND_PAC_EXTERNO_SEQ.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosVO.class));

		return executeCriteria(criteria);
	}

	public List<AtendimentosVO> pesquisarAtendimentosNascimentos(Integer prontuario, Date data) {

		StringBuffer hql = new StringBuffer(330);
		hql.append(" select new br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO (");
		hql.append(" na.");
		hql.append(McoNascimentos.Fields.DTHR_NASCIMENTO.toString());
		hql.append(", ");
		hql.append(" eq.");
		hql.append(AghEquipes.Fields.NOME.toString());
		hql.append(" ) ");
		hql.append(" from AghAtendimentos at, McoNascimentos na, McoRecemNascidos rn, AghEquipes eq");
		hql.append(" where at.");
		hql.append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString());
		hql.append(" = :prontuario ");
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.DTHR_NASCIMENTO.toString());
		hql.append(" <= :data ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString());
		hql.append(" is not null ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.ORIGEM.toString());
		hql.append(" = '" + DominioOrigemAtendimento.N + "' ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.GSO_PAC_CODIGO.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.GSO_SEQP.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.SEQP.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.SEQP.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.SEQP.toString());
		hql.append(" = eq.").append(AghEquipes.Fields.CODIGO.toString());
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("prontuario", prontuario);
		query.setParameter("data", data);

		return query.list();
	}

	public List<AtendimentosVO> pesquisarAtendimentosNeonatologia(Integer prontuario, Date data) {

		StringBuffer hql = new StringBuffer(350);
		hql.append(" select new br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO (");
		hql.append(" rn.");
		hql.append(McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString());
		hql.append(", ");
		hql.append(" at.");
		hql.append(AghAtendimentos.Fields.SERVIDOR.toString()).append('.').append(RapServidores.Fields.CODIGO_VINCULO.toString());
		hql.append(", ");
		hql.append(" at.");
		hql.append(AghAtendimentos.Fields.SERVIDOR.toString()).append('.').append(RapServidores.Fields.MATRICULA.toString());
		hql.append(" ) ");
		hql.append(" from AghAtendimentos at, McoRecemNascidos rn");
		hql.append(" where at.");
		hql.append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString());
		hql.append(" = :prontuario ");
		hql.append(" and rn.");
		hql.append(McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString());
		hql.append(" <= :data ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString());
		hql.append(" is not null ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.ORIGEM.toString());
		hql.append(" = '").append(DominioOrigemAtendimento.N).append("' ");
		hql.append(" and at.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.CODIGO_PACIENTE.toString());
		hql.append(" and not exists");
		hql.append(" (select 1 ");
		hql.append(" from McoNascimentos na");
		hql.append(" where na.");
		hql.append(McoNascimentos.Fields.GSO_PAC_CODIGO.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.GSO_SEQP.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.GESTACOES_SEQUENCE.toString());
		hql.append(" and na.");
		hql.append(McoNascimentos.Fields.SEQP.toString());
		hql.append(" = rn.").append(McoRecemNascidos.Fields.SEQP.toString());
		hql.append(')');
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("prontuario", prontuario);
		query.setParameter("data", data);

		return query.list();
	}

	public List<Object[]> listarAtendimentosPacienteEmAndamentoPorCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I,
			DominioOrigemAtendimento.U, DominioOrigemAtendimento.N }));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.ORIGEM.toString()));

		criteria.setProjection(p);

		return executeCriteria(criteria);
	}

	public List<Object[]> listarAtendimentosPacienteTratamentoPorCodigo(Integer pacCodigo, Integer pTipoTratamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.TIPO_TRATAMENTO.toString(), pTipoTratamento));
		criteria.add(Restrictions.isNull(AghAtendimentos.Fields.DTHR_FIM.toString()));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		p.add(Projections.property(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()));

		criteria.setProjection(p);

		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoEmAndamento(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "t1");

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {

		DetachedCriteria criteria = getCriteriaPesquisarAtendimentosEmergenciaPOL(codigo, dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);

		ProjectionList projection = Projections
		.projectionList()
		.add(Projections.property("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString()), AtendimentosEmergenciaPOLVO.Fields.DTHR_INICIO.toString())
		.add(Projections.property("especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()),
				AtendimentosEmergenciaPOLVO.Fields.ESP_NOME_ESPECIALIDADE.toString())//, mpmc_minusculo(esp.nome_especialidade, 2)
				.add(Projections.property("especialidade." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
						AtendimentosEmergenciaPOLVO.Fields.ESP_SEQ_PAI.toString())//, esp1.seq
						.add(Projections.property("consulta." + AacConsultas.Fields.NUMERO.toString()), AtendimentosEmergenciaPOLVO.Fields.CON_NUMERO.toString());

		criteria.setProjection(projection);

		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentosEmergenciaPOLVO.class));

		return this.executeCriteria(criteria);//, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria getCriteriaPesquisarAtendimentosEmergenciaPOL(Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta,
			Short seqEspecialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "t1");

		//Joins com tabelas AacConsultas, AacGradeAgendamenConsultas, AghEquipes e AghEspecialidades
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "consulta");
		criteria.createAlias("consulta." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgendaConsulta");
		criteria.createAlias("gradeAgendaConsulta." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe");

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "especialidade1", JoinType.LEFT_OUTER_JOIN);

		// Where
		criteria.add(Restrictions.eq("t1." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.isNull("t1." + AghAtendimentos.Fields.APE_SEQ.toString()));		

		Date dataTrunc = DateUtil.truncaData(new Date());
		Date data = DateUtil.truncaData(DateUtil.adicionaDias(dataTrunc, 1));
		criteria.add(Restrictions.le("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString(), data));//???????????? TRUNC DTHR_INICIO

		criteria.add(Subqueries.exists(subCriteriaGradeAgendamentoConsulta()));

		if (dthrInicio != null) {
			criteria.add(Restrictions.ge("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrInicio));
		}

		if (dthrFim != null) {
			criteria.add(Restrictions.le("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrFim));
		}

		if (numeroConsulta != null) {
			criteria.add(Restrictions.eq("t1." + AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));
		}

		if (seqEspecialidade != null) {
			criteria.add(Restrictions.or(Restrictions.eq("especialidade1." + AghEspecialidades.Fields.SEQ.toString(), seqEspecialidade),
					Restrictions.eq("especialidade." + AghEspecialidades.Fields.SEQ.toString(), seqEspecialidade)));
		}
		return criteria;
	}

	private DetachedCriteria subCriteriaGradeAgendamentoConsulta() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "t2");

		criteria.setProjection(Projections.projectionList().add(Projections.property("t2." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())));

		criteria.add(Restrictions.eqProperty("t2." + AacGradeAgendamenConsultas.Fields.SEQ.toString(), "gradeAgendaConsulta.seq"));
		criteria.add(Subqueries.exists(subCriteriaEmgEspecialidade()));

		return criteria;
	}

	private DetachedCriteria subCriteriaEmgEspecialidade() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class, "t3");

		criteria.setProjection(Projections.projectionList().add(Projections.property("t3." + MamEmgEspecialidades.Fields.ESP_SEQ.toString())));

		criteria.add(Restrictions.eqProperty("t3." + MamEmgEspecialidades.Fields.ESP_SEQ.toString(), "t2.especialidade.seq"));

		return criteria;
	}

	public Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {
		DetachedCriteria criteria = getCriteriaPesquisarAtendimentosEmergenciaPOL(codigo, dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);

		return this.executeCriteriaCount(criteria);
	}

	public AghAtendimentos obterAtendimentoPorTriagem(Long trgSeq) {

		StringBuilder hql = new StringBuilder(200);

		hql.append("select atd ");
		hql.append("from AghAtendimentos atd, ");
		hql.append("MamTrgEncInterno tei ");
		hql.append("where  ");
		hql.append("tei." ).append( MamTrgEncInterno.Fields.TRG_SEQ.toString() ).append( " = :trgSeq ");
		hql.append("and tei." ).append( MamTrgEncInterno.Fields.DTHR_ESTORNO.toString() ).append( " is null ");
		hql.append("and atd." ).append( AghAtendimentos.Fields.CONSULTA.toString() ).append( " = tei." ).append( MamTrgEncInterno.Fields.CONSULTA.toString() ).append(' ');
		hql.append("order by tei." ).append( MamTrgEncInterno.Fields.SEQ_P.toString() ).append( " desc ");

		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("trgSeq", trgSeq);

		List<AghAtendimentos> lista = query.getResultList();

		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			return lista.get(0);
		}

		return null;
	}

	/**
	 * se paciente tiver um exame agendado x dias para trás ou dias para frente
	 * P_PAC_EM_ATENDIMENTO
	 * CURSOR c_exame_agendado
	 * 
	 * @param pacCodigo
	 * @return Lista AghAtendimentos
	 * 
	 */
	public List<AghAtendimentos> verificarSePacienteTemAtendimento(Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo) {

		if (numDiasFuturo == null && numDiasPassado == null) {
			numDiasFuturo = 0;
			numDiasPassado = 0;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd.".concat(AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString()), "soe");
		criteria.createAlias("soe.".concat(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString()), "ise");
		criteria.createAlias("ise.".concat(AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString()), "iha");

		criteria.add(Restrictions.ge("iha.".concat(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()),
				DateUtil.obterDataComHoraInical(DateUtil.adicionaDias(new Date(), (-1) * numDiasPassado))));
		criteria.add(Restrictions.le("iha.".concat(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()),
				DateUtil.obterDataComHoraFinal(DateUtil.adicionaDias(new Date(), numDiasFuturo))));
		criteria.add(Restrictions.eq("atd.".concat(AghAtendimentos.Fields.PAC_CODIGO.toString()), pacCodigo));

		return executeCriteria(criteria);
	}

	/**
	 * CURSOR c_internado
	 * FUNCTION P_PAC_EM_ATENDIMENTO
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public List<AghAtendimentos> buscarPacienteInternado(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		return executeCriteria(criteria);
	}

	/**
	 * CURSOR c_alta
	 * FUNCTION P_PAC_EM_ATENDIMENTO
	 * 
	 * @param pacCodigo
	 * @param pNroDiasPas 
	 * @return
	 */
	public List<AghAtendimentos> buscarPacientesAlta(Integer pacCodigo, Integer pNroDiasPas) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		Date dt = new Date();
		if (pNroDiasPas != null) {
			//dt = DateUtil.adicionaDias(dt, pNroDiasPas);
			dt = DateUtil.adicionaDias(dt, pNroDiasPas * -1);
		}

		criteria.add(Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), DateUtil.obterDataComHoraInical(dt)));
		criteria.add(Restrictions.le(AghAtendimentos.Fields.DTHR_FIM.toString(), DateUtil.obterDataComHoraFinal(new Date())));

		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.N));

		return executeCriteria(criteria);
	}

	public List<NodoAtendimentoEmergenciaPOLVO> pesquisarNodosMenuAtendimentosEmergenciaPol(Integer pacCodigo) {

		StringBuffer hql = new StringBuffer(500);

		String vinculoGrade = "grd." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString();
		String matricuGrade = "grd." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString();
		String vinculoEquipe = "eqp." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString();
		String matricuEquipe = "eqp." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString() + "." + RapServidores.Fields.MATRICULA.toString();

		hql.append("select \n");
		hql.append("atd." + AghAtendimentos.Fields.SEQ + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.ATD_SEQ + " ,\n");
		hql.append("atd." + AghAtendimentos.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.PAC_CODIGO
				+ " , \n");
		hql.append("atd." + AghAtendimentos.Fields.PRONTUARIO + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.PRONTUARIO + " , \n");
		hql.append("atd." + AghAtendimentos.Fields.DTHR_INICIO + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.DTHR_INICIO + " , \n");
		hql.append("atd." + AghAtendimentos.Fields.CONSULTA + "." + AacConsultas.Fields.NUMERO + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.CON_NUMERO
				+ " , \n");
		hql.append("esp1." + AghEspecialidades.Fields.NOME_REDUZIDO + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.NOME_REDUZIDO + " , \n");
		hql.append("esp1." + AghEspecialidades.Fields.NOME_ESPECIALIDADE + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.NOME_ESPECIALIDADE + " ,\n");
		hql.append("coalesce(" + vinculoGrade + ", " + vinculoEquipe + ") as " + NodoAtendimentoEmergenciaPOLVO.Fields.SER_VIN_CODIGO + " ,\n");
		hql.append("coalesce(" + matricuGrade + ", " + matricuEquipe + ") as " + NodoAtendimentoEmergenciaPOLVO.Fields.SER_VIN_MATRICULA + " ,\n");
		hql.append("to_char(atd." + AghAtendimentos.Fields.DTHR_INICIO + ", 'yyyymmdd') as " + NodoAtendimentoEmergenciaPOLVO.Fields.DTHT_INICIO_FORMATADA
				+ " , \n");
		hql.append("esp1." + AghEspecialidades.Fields.SIGLA + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.SIGLA + ", \n");
		hql.append("esp1." + AghEspecialidades.Fields.SEQ + " as " + NodoAtendimentoEmergenciaPOLVO.Fields.SEQ_ESPECIALIDADE + "\n");
		hql.append("from  " + MamEmgEspecialidades.class.getName() + " eep, \n");
		hql.append(AghEspecialidades.class.getName()).append(" esp1, \n");
		hql.append(AghAtendimentos.class.getName()).append(" atd \n");
		hql.append("join atd." + AghAtendimentos.Fields.CONSULTA + " con \n");
		hql.append("join con." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA + " grd \n");
		hql.append("join grd." + AacGradeAgendamenConsultas.Fields.EQUIPE + " eqp \n");
		hql.append("join atd." + AghAtendimentos.Fields.ESPECIALIDADE + " esp \n");
		hql.append("where con." + AghAtendimentos.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO + " = :pacCodigo \n");
		hql.append("and atd." + AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO + " is null \n");
		hql.append("and esp1." + AghEspecialidades.Fields.SEQ + " = coalesce(esp." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA + " , esp.seq) \n");
		hql.append("and eep.espSeq = grd." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ + "\n");
		//hql.append("and to_date(to_char(atd.dthrInicio, 'ddmmmyyyy'),'ddmmmyyyy') <= to_date(to_char(:sysdate, 'ddmmmyyyy'),'ddmmmyyyy') " + " \n");
		hql.append("ORDER BY \n");
		hql.append("      10 DESC \n");
		hql.append("    , 7 ASC \n");
		hql.append("    , 4 DESC \n");
		hql.append("    , 8 ASC \n");
		hql.append("    , 9 ASC \n");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		//query.setParameter("sysdate", new Date());

		query.setResultTransformer(Transformers.aliasToBean(NodoAtendimentoEmergenciaPOLVO.class));

		return query.list();
	}

	public List<AghAtendimentos> pesquisarAghAtendimentosPorPacienteEConsulta(Integer codPaciente, Integer numConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numConsulta));

		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoPorCodigoPacienteNumeroConsulta(Integer pPacCodigo, Integer pConNumero) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "ATD");

		if (pPacCodigo != null) {
			dc.add(Restrictions.eq("ATD.".concat(AghAtendimentos.Fields.PAC_CODIGO.toString()), pPacCodigo));
		}
		if (pConNumero != null) {
			dc.add(Restrictions.eq("ATD.".concat(AghAtendimentos.Fields.NUMERO_CONSULTA.toString()), pConNumero));
		}

		return (AghAtendimentos) executeCriteriaUniqueResult(dc);
	}

	public List<AghAtendimentos> listarAtendimentosPorPacCodGestacaoSeq(Integer gsoPacCodigo, Short gsoSeqp, Integer pepPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pepPacCodigo));

		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoComUnidadeFuncional(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd.".concat(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()), "unf");
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public AghAtendimentos obterAtendimentoPorPacienteEOrigem(Integer pacCodigo) {

		DominioOrigemAtendimento[] origens = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U };

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		List<AghAtendimentos> list = executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * ORADB RN_CRGP_ATU_ATEND
	 * @param pacCodigo
	 * @param dthrFimCirurg
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPorPacienteEDthr(Integer pacCodigo, Date dthrFimCirurg) {

		DominioOrigemAtendimento[] origens = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U };

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));

		List<AghAtendimentos> listaAtendimentos = executeCriteria(criteria);

		for (AghAtendimentos atendimentos : listaAtendimentos) {

			if (DateUtil.validaDataMaior(dthrFimCirurg, atendimentos.getDthrInicio()) && DateUtil.validaDataMaior(atendimentos.getDthrFim(), dthrFimCirurg)) {

				return atendimentos;

			}

		}

		return null;

	}

	public List<AghAtendimentos> obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(DominioPacAtendimento pacAtendimento, Integer pacCodigo, List<DominioOrigemAtendimento> origens) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		if(pacAtendimento != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), pacAtendimento));
		}
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.APE_SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**c_exames_alta #5945
	 * Obtém AghAtendimentos por seq material analise e seq item material exame
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(Integer seq, String sitCodigoLi, String sitCodigoAe) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");

		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EIS");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA");
		criteria.add(Restrictions.in("EMA." + AelExamesMaterialAnalise.Fields.PERTENCE_SUMARIO.toString(), obterListagemSumario()));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), sitCodigoLi));
		criteria.add(Restrictions.eq("EIS." + AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigoAe));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), seq));
		criteria.addOrder(Order.asc("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString()));
		criteria.addOrder(Order.asc("SOE." + AelSolicitacaoExames.Fields.RECEM_NASCIDO.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));

		//		ProjectionList p = Projections.projectionList();
		//    
		//		p.add(Projections.property("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString()));
		//		p.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()));
		//		p.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.RECEM_NASCIDO.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())); 		
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()));
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA_SEQ.toString()));
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()));
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString()));
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.ISE_SEQP.toString())); 
		//		p.add(Projections.property("EMA." + AelExamesMaterialAnalise.Fields.PERTENCE_SUMARIO.toString()));
		//		p.add(Projections.property("EMA." + AelExamesMaterialAnalise.Fields.IND_DEPENDENTE.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString())); 
		//		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		//		p.add(Projections.max("EIS." + AelExtratoItemSolicitacao.Fields.DATA_HORA_EVENTO.toString()));
		//		
		//		p.add(Projections.groupProperty("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString()));
		//		p.add(Projections.groupProperty("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()));
		//		p.add(Projections.groupProperty("SOE." + AelSolicitacaoExames.Fields.RECEM_NASCIDO.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));		
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA_SEQ.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString()));	
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()));
		//		p.add(Projections.groupProperty("EMA." + AelExamesMaterialAnalise.Fields.PERTENCE_SUMARIO.toString()));
		//		p.add(Projections.groupProperty("EMA." + AelExamesMaterialAnalise.Fields.IND_DEPENDENTE.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()));		
		//		p.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		//		p.add(Projections.groupProperty("EIS." + AelExtratoItemSolicitacao.Fields.DATA_HORA_EVENTO.toString()));

		return executeCriteria(criteria);
	}

	private List<DominioSumarioExame> obterListagemSumario() {
		final List<DominioSumarioExame> listSitCodigo = new ArrayList<DominioSumarioExame>();
		listSitCodigo.add(DominioSumarioExame.B);
		listSitCodigo.add(DominioSumarioExame.E);
		listSitCodigo.add(DominioSumarioExame.G);
		listSitCodigo.add(DominioSumarioExame.H);
		listSitCodigo.add(DominioSumarioExame.S);
		return listSitCodigo;
	}
	
	/**
	 * ORADB: CURSOR: MAMK_PC_EXAMES.MAMP_ALTA_SUM_EXAMES.C_EXAMES_ALTA 
	 * @param sitCodigoLi
	 * @param sitCodigoAe
	 * @param ppeSeqp
	 * @param ppePleSeq
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param apeSeqp
	 * @return
	 */
	public List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo( final String sitCodigoLi,
																						    final String sitCodigoAe,
																						    final Integer ppeSeqp,
																						    final Integer ppePleSeq,
																						    final Integer asuApaAtdSeq,
																						    final Integer asuApaSeq,
																						    final Short apeSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");

		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE."+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EIS");
		criteria.createAlias("ISE."+ AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA");
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), sitCodigoLi));
		criteria.add(Restrictions.eq("EIS."+AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigoAe));
		
		// Por ser temporÃ¡rio nÃ£o Ã© inserido, logo, nÃ£o se pode filtrar.
//		final DetachedCriteria subCriteriaPPE = DetachedCriteria.forClass(MamPcPaciente.class, "PPE");
//		subCriteriaPPE.add(Restrictions.eq("PPE."+MamPcPaciente.Fields.SEQP.toString(), ppeSeqp));
//		subCriteriaPPE.add(Restrictions.eq("PPE."+MamPcPaciente.Fields.PLE_SEQ.toString(), ppePleSeq));
//		subCriteriaPPE.add(Restrictions.eqProperty("PPE."+MamPcPaciente.Fields.PAC_CODIGO.toString(), "ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString()));
//		subCriteriaPPE.setProjection(Projections.property("PPE."+MamPcPaciente.Fields.SEQP.toString()));
//		criteria.add(Subqueries.exists(subCriteriaPPE));


		final DetachedCriteria subCriteriaAXM = DetachedCriteria.forClass(MpmAltaPrincExame.class, "AXM");
		subCriteriaAXM.add(Restrictions.eq("AXM."+MpmAltaPrincExame.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		subCriteriaAXM.add(Restrictions.eq("AXM."+MpmAltaPrincExame.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		subCriteriaAXM.add(Restrictions.eq("AXM."+MpmAltaPrincExame.Fields.ASU_SEQP.toString(), apeSeqp));
//		subCriteriaAXM.add(Restrictions.eq("AXM."+MpmAltaPrincExame.Fields.SEQP.toString(), apeSeqp));
		subCriteriaAXM.add(Restrictions.eqProperty("AXM."+MpmAltaPrincExame.Fields.ISE_SOE_SEQ.toString(), "ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subCriteriaAXM.add(Restrictions.eqProperty("AXM."+MpmAltaPrincExame.Fields.ISE_SEQP.toString(), "ISE."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		subCriteriaAXM.add(Restrictions.isNotNull("AXM."+MpmAltaPrincExame.Fields.DTHR_LIBERACAO.toString()));
		subCriteriaAXM.add(Restrictions.sqlRestriction("COALESCE({alias}."+MpmAltaPrincExame.Fields.IND_IMPRIME.name()+",'N') = 'S'" ));
		subCriteriaAXM.setProjection(Projections.property("AXM."+MpmAltaPrincExame.Fields.SEQP.toString()));

		criteria.add(Subqueries.exists(subCriteriaAXM));

		
		criteria.addOrder(Order.asc("ATD."+AghAtendimentos.Fields.PRONTUARIO.toString()));
		criteria.addOrder(Order.asc("SOE."+ AelSolicitacaoExames.Fields.RECEM_NASCIDO.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		

		return executeCriteria(criteria);
	}
	
	
	
	/**
	 * c_atd #5978
	 * @param pacCodigo
	 * @param atd
	 * @return
	 */
	public List<AghAtendimentos> buscarAtendimentosPaciente(Integer pacCodigo, Integer atd) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atd));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna a data final do atendimento
	 * @param atdseq - atendimento a ser pesquisado
	 */
	public Date obterDthrFimAtendimento(Integer atdseq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.setProjection(Projections.property(AghAtendimentos.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdseq));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Realiza a consulta do atendimento atravÃ©s do codigo do paciente para atualizar "mbc_cirurgias"
	 * @param aipPacientesCodigo
	 * @return
	 */
	public AghAtendimentos obterAtendimentoContrEscCirurg(Integer aipPacientesCodigo) {
		AghAtendimentos aghAtendimentos = null;

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), aipPacientesCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));// S = Em andamento

		DominioOrigemAtendimento[] origemAtendimento = { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U };
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origemAtendimento));

		final List<AghAtendimentos> listaAghAtendimentos = executeCriteria(criteria);

		if (listaAghAtendimentos.size() > 0) {
			aghAtendimentos = listaAghAtendimentos.get(0);
		}

		return aghAtendimentos;
	}

	public AghAtendimentos obterAtendimentoPorPacienteEOrigemDthrFim(Integer pacCodigo, DominioOrigemAtendimento origem, Date dthrFimCirg) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), origem));

		if (dthrFimCirg == null && origem.equals(DominioOrigemAtendimento.U)) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		} else if (dthrFimCirg != null) {

			criteria.add(Restrictions.or(
					Restrictions.and(
							Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S),
							Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrFimCirg)
							),
							Restrictions.and(
								Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrFimCirg),
								Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), dthrFimCirg)
							)
					)
			);
		}

		List<AghAtendimentos> resultado = executeCriteria(criteria);
		return resultado.isEmpty() ? null : resultado.get(0);

	}

	public Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo, final DominioOrigemAtendimento[] origens,
			final DominioPacAtendimento indPacAtendimento) {
		/*
		 * SELECT distinct seq FROM agh_atendimentos WHERE pac_codigo = c_pac_codigo AND ind_pac_atendimento = 'S' AND origem IN ('I','U');
		 */

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), indPacAtendimento));

		criteria.setProjection(Projections.distinct(Projections.property(AghAtendimentos.Fields.SEQ.toString())));

		final List<Integer> result = executeCriteria(criteria);
		if (!result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}

	public List<AghAtendimentos> pesquisaAtendimentoPacienteInternadoUrgencia(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I,
			DominioOrigemAtendimento.U }));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisaAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento origem, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), origem));

		if (dthrInicio != null) {

			final SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/aaaa");
			criteria.add(Restrictions.sqlRestriction("To_char(" + "DTHR_INICIO" + ",'dd/mm/yyyy') = ?", formatador.format(dthrInicio), StandardBasicTypes.STRING));

		}

		return executeCriteria(criteria);
	}

	/**
	 * {@link IAghuFacade#buscaInternacoesDentroIntervaloNaoCalculado(SigProcessamentoCusto)}
	 */
	public List<AghAtendimentos> buscaInternacoesDentroIntervaloNaoCalculado(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");

		Criterion betweenDtInicio = Restrictions.between(AghAtendimentos.Fields.DTHR_INICIO.toString(), processamentoCusto.getDataInicio(), processamentoCusto.getDataFim());
		Criterion nullDtFim = Restrictions.and(
				Restrictions.isNull(AghAtendimentos.Fields.DTHR_FIM.toString()),
				Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), processamentoCusto.getDataFim())
		);
		Criterion betweenDtFim = Restrictions.between(AghAtendimentos.Fields.DTHR_FIM.toString(), processamentoCusto.getDataInicio(),processamentoCusto.getDataFim());

		Criterion processamentoBetweenAtendimento = Restrictions.and(
				Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), processamentoCusto.getDataInicio()),
				Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), processamentoCusto.getDataFim())
		);

		criteria.add(Restrictions.or(betweenDtInicio, nullDtFim, betweenDtFim, processamentoBetweenAtendimento));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.INTERNACAO.toString()));

		DetachedCriteria criteriaIn = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cap");
		criteriaIn.setProjection(Projections.property(SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString()));
		criteriaIn.add(Restrictions.eq(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));

		criteria.add(Subqueries.propertyNotIn("atd." + AghAtendimentos.Fields.SEQ.toString(), criteriaIn));

		return executeCriteria(criteria);
	}

	public AghAtendimentos obterAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento... origem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origem));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	private List<AghAtendimentos> pesquisarAtendimentoRegistroCirurgiaRealizada(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg, final Integer limiteResultados){

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + AinInternacao.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		if(atdSeq != null){
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}

		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		// or
		List<DominioOrigemAtendimento> origens = Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I, DominioOrigemAtendimento.C);
		Criterion cOrigemUIC = Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origens);

		// and
		Criterion cOrigemA = Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A);
		Criterion cDataInicioMaior = Restrictions.lt("ATD." + AghAtendimentos.Fields.DATA_HORA_INICIO.toString(), dthrInicioCirg);
		Criterion cOrOrigemADataInicioMaior = Restrictions.and(cOrigemA, cDataInicioMaior);

		DetachedCriteria subQueryUnfCaract = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "A");
		subQueryUnfCaract.setProjection(Projections.property("A." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQueryUnfCaract.add(Restrictions.eq("A." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA));
		subQueryUnfCaract.add(Property.forName("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).eqProperty("A." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));

		Criterion cInSubQueryUnfCaract = Subqueries.exists(subQueryUnfCaract);
		Criterion cOrOrigemADataInicioMaiorUnfCaract = Restrictions.and(cOrOrigemADataInicioMaior, cInSubQueryUnfCaract);

		criteria.add(Restrictions.or(cOrigemUIC, cOrOrigemADataInicioMaiorUnfCaract));
		
		criteria.addOrder(Order.desc("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString()));

		List<AghAtendimentos> resultadoParcial  = null;

		if(limiteResultados == null){
			resultadoParcial = this.executeCriteria(criteria);
		} else{
			resultadoParcial = this.executeCriteria(criteria, 0, limiteResultados, AghAtendimentos.Fields.SEQ.toString(), true);
		}

		List<AghAtendimentos> resultado = new LinkedList<AghAtendimentos>();
		for (AghAtendimentos atendimento : resultadoParcial) {

			boolean isUIC = origens.contains(atendimento.getOrigem());
			boolean isAmbulatorioDatas = DominioOrigemAtendimento.A.equals(atendimento.getOrigem()) 
			&& DateUtil.validaDataMaior(dthrInicioCirg, atendimento.getDthrInicio())
			&& (DateUtil.calcularDiasEntreDatas(dthrInicioCirg, atendimento.getDthrInicio()) < 1);

			if(isUIC || isAmbulatorioDatas){
				resultado.add(atendimento);
			}

		}	

		return resultado;
	}	

	public List<AghAtendimentos> pesquisarAtendimentoRegistroCirurgiaRealizada(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg){
		return this.pesquisarAtendimentoRegistroCirurgiaRealizada(atdSeq, pacCodigo, dthrInicioCirg, 100);
	}

	public AghAtendimentos buscarDadosPacientePorAtendimento(Integer seqAtendimento, DominioOrigemAtendimento... origem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origem));
		
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public Integer pesquisarAtendimentoRegistroCirurgiaRealizadaCount(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg){
		List<AghAtendimentos> resultado = this.pesquisarAtendimentoRegistroCirurgiaRealizada(atdSeq, pacCodigo, dthrInicioCirg, null);
		return resultado.size();
	}

	public AghAtendimentos buscarAtendimentoPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "aghAtend");
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));
		criteria.createAlias("aghAtend." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("aghAtend." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	public Boolean verificarUnidadeFuncionalAtendimentos(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()),
				Restrictions.isNotNull(AghAtendimentos.Fields.ATU_SEQ.toString())));

		return executeCriteriaExists(criteria);
	}

	// #27200 - ImpressÃ£o das etiquetas de identificaÃ§Ã£o relacionada ao paciente
	// C3
	public List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica, Date dataCirurgia) {
		List<Object[]> retorno = new ArrayList<Object[]>();
		
		// Consulta 1
		retorno = executeCriteria(pesquisarRelatorioEtiquetasIdentificacaoUnion1(unfSeq, unfSeqMae, pacCodigoFonetica));
		List<RelatorioEtiquetasIdentificacaoVO> retornoUnion1 = popularRelatorioEtiquetasIdentificacaoVO(retorno, 1);
		
		// Consulta 2
		retorno = executeCriteria(pesquisarRelatorioEtiquetasIdentificacaoUnion2(unfSeq, unfSeqMae, pacCodigoFonetica));
		List<RelatorioEtiquetasIdentificacaoVO> retornoUnion2 = popularRelatorioEtiquetasIdentificacaoVO(retorno, 2);
		
		// Consulta 3
		List<RelatorioEtiquetasIdentificacaoVO> retornoUnion3 = null;
		if(dataCirurgia != null){
			retorno = executeCriteria(pesquisarRelatorioEtiquetasIdentificacaoUnion3(unfSeq, unfSeqMae, pacCodigoFonetica, dataCirurgia));
			retornoUnion3 = popularRelatorioEtiquetasIdentificacaoVO(retorno, 3);
		}
		
		List<RelatorioEtiquetasIdentificacaoVO> listaRetorno = new ArrayList<RelatorioEtiquetasIdentificacaoVO>();
		if(dataCirurgia != null && retornoUnion3 != null && !retornoUnion3.isEmpty()){
			listaRetorno.addAll(retornoUnion3);
		}
		
		listaRetorno.addAll(retornoUnion1);
		listaRetorno.addAll(retornoUnion2);
		
		return listaRetorno;
	}

	private DetachedCriteria pesquisarRelatorioEtiquetasIdentificacaoUnion1(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNP");

		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).as("unfSeq"))
				.add(Projections.property("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString()).as("prontuario"))
				.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()).as("pacCodigo"))
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()).as("nome"))
				.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()).as("atendimento"))
				.add(Projections.property("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString()).as("ltoLtoId"));
		
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq("UNF." + "indSitUnidFunc", DominioSituacao.A));
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		}
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioSimNao.S));
		if (unfSeqMae != null) {
			criteria.add(Restrictions.eq("UNP." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqMae));
		}
		if (pacCodigoFonetica != null) {
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigoFonetica));
		}

		return criteria;
	}

	private DetachedCriteria pesquisarRelatorioEtiquetasIdentificacaoUnion2(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ARN");

		criteria.createAlias("ARN." + "atendimentoMae", "AMA");
		criteria.createAlias("ARN." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ARN." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNR");
		criteria.createAlias("UNR." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNM");

		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("ARN." + AghAtendimentos.Fields.PRONTUARIO.toString()).as("prontuario"))
				.add(Projections.property("ARN." + AghAtendimentos.Fields.PAC_CODIGO.toString()).as("pacCodigo"))
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()).as("nome"))
				.add(Projections.property("ARN." + AghAtendimentos.Fields.SEQ.toString()).as("atendimento"))
				.add(Projections.property("AMA." + AghAtendimentos.Fields.QRT_NUMERO.toString()).as("ltoLtoId"));
		
		criteria.setProjection(projectionList);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq("ARN." + AghAtendimentos.Fields.UNF_SEQ.toString(), unfSeq));
		}
		criteria.add(Restrictions.eq("ARN." + AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString(), true));
		criteria.add(Restrictions.eq("ARN." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (pacCodigoFonetica != null) {
			criteria.add(Restrictions.eq("ARN." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigoFonetica));
		}
		if (unfSeqMae != null) {
			criteria.add(Restrictions.eq("UNM." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqMae));
		}

		return criteria;
	}

	private DetachedCriteria pesquisarRelatorioEtiquetasIdentificacaoUnion3(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica, Date dataCirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");

		criteria.createAlias("PAC." + AipPacientes.Fields.MBC_CIRURGIAS.toString(), "CRG");
		criteria.createAlias("PAC." + "unidadeFuncional", "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNP", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()).as("prontuario"))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString()).as("pacCodigo"))
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()).as("nome"))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString()).as("atendimento"))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.SCI_SEQP.toString()).as("ltoLtoId_part1"))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_INICIO_ORDEM.toString()).as("ltoLtoId_part2"))
				.add(Projections.property("CRG." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()).as("ltoLtoId_part3"));
		
		criteria.setProjection(Projections.distinct(projectionList));

		criteria.add(Restrictions.ne("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		if (dataCirurgia != null) {
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		}
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (pacCodigoFonetica != null) {
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigoFonetica));
		}
		if (unfSeqMae != null) {
			criteria.add(Restrictions.eq("UNP." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqMae));
		}

		return criteria;
	}

	private List<RelatorioEtiquetasIdentificacaoVO> popularRelatorioEtiquetasIdentificacaoVO(List<Object[]> retorno, Integer numeroSubConsulta) {
		List<RelatorioEtiquetasIdentificacaoVO> lista = new ArrayList<RelatorioEtiquetasIdentificacaoVO>();
		DecimalFormat sciFormatter = new DecimalFormat("000");
		for (Object[] objects : retorno) {
			RelatorioEtiquetasIdentificacaoSubVO relatorioEtiquetasIdentificacaoVO = new RelatorioEtiquetasIdentificacaoSubVO();
			int i = 0;
			if (numeroSubConsulta.equals(1)) {
				relatorioEtiquetasIdentificacaoVO.setUnfSeq((Short) objects[i++]);
			}
			relatorioEtiquetasIdentificacaoVO.setNumeroSubConsulta(numeroSubConsulta);
			relatorioEtiquetasIdentificacaoVO.setProntuario((Integer) objects[i++]);
			relatorioEtiquetasIdentificacaoVO.setPacCodigo((Integer) objects[i++]);
			relatorioEtiquetasIdentificacaoVO.setNome((String) objects[i++]);
			relatorioEtiquetasIdentificacaoVO.setAtendimento((Integer) objects[i++]);
			if (numeroSubConsulta.equals(3)) {
				StringBuilder sb = new StringBuilder();
				String sciSeqp = objects[i++].toString();
				sb.append(sciFormatter.format(Long.valueOf(sciSeqp)));
				SimpleDateFormat formatador = new SimpleDateFormat("HHmm");
				Date dthrInicioOrdem = (Date) objects[i++];
				Date dthrInicioCirg = (Date) objects[i];
				if (dthrInicioCirg != null) {
					sb.append(formatador.format(dthrInicioCirg));
				}
				if (dthrInicioOrdem != null) {
					sb.append(formatador.format(dthrInicioOrdem));
				}
				
				String leito = sb.toString();
				if (StringUtils.isNotBlank(leito)) {
					relatorioEtiquetasIdentificacaoVO.setLtoLtoId(leito);
				}
			} else {
				relatorioEtiquetasIdentificacaoVO.setLtoLtoId((objects[i] != null) ? objects[i].toString() : null);
			}
			
			lista.add(relatorioEtiquetasIdentificacaoVO);
		}
		return lista;
	}
	
	public Integer obterAtendimentoPorConNumero(Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.CON_NUMERO.toString(), conNumero));

		criteria.setProjection(Projections.distinct(Projections.property(AghAtendimentos.Fields.SEQ.toString())));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	

	public AghAtendimentos buscarAtendimentoPorConNumero(Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.CON_NUMERO.toString(), conNumero));
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	} 

	public List<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion1(Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp, DominioOrigemAtendimento origem, Short espSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.SERVIDOR.toString(), "serv", JoinType.INNER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projectionList = Projections.projectionList()
			.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_SIGLA.toString()))
			.add(Projections.property("esp." + AghEspecialidades.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_NOME.toString()))
			.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()).as(PacientesEmAtendimentoVO.Fields.UNF_DESCRICAO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.ATD_SEQ.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.ORIGEM.toString()).as(PacientesEmAtendimentoVO.Fields.ORIGEM.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()).as(PacientesEmAtendimentoVO.Fields.IND_PAC_ATENDIMENTO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()).as(PacientesEmAtendimentoVO.Fields.DTHR_INICIO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.CON_NUMERO.toString()).as(PacientesEmAtendimentoVO.Fields.CON_NUMERO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.SERVIDOR.toString()+ ".id.matricula").as(PacientesEmAtendimentoVO.Fields.MATRICULA_RESP.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.SERVIDOR.toString()+ ".id.vinCodigo").as(PacientesEmAtendimentoVO.Fields.VIN_CODIGO_RESP.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_SEQ.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.UNF_SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.UNF_SEQ.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.CODIGO.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_CODIGO.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_PRONTUARIO.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_NOME.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString()).as(PacientesEmAtendimentoVO.Fields.LEITO.toString()))
			.add(Projections.property("pf." + RapPessoasFisicas.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.NOME_RESP.toString()));
		criteria.setProjection(Projections.distinct(projectionList));
		
		DominioOrigemAtendimento[] origens = { DominioOrigemAtendimento.N, DominioOrigemAtendimento.I, DominioOrigemAtendimento.U };
		criteria.add(Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), origens));
		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		if(pacCodigo != null){
			criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		}
		if(StringUtils.isNotBlank(leito)){
			criteria.add(Restrictions.ilike("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leito, MatchMode.ANYWHERE));
		}
		if(unfSeq != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if(matriculaResp != null && vinCodigoResp != null){
			criteria.add(
					Restrictions.and(
						Restrictions.eq("atd." + AghAtendimentos.Fields.SERVIDOR.toString()+ ".id.matricula", matriculaResp),
						Restrictions.eq("atd." + AghAtendimentos.Fields.SERVIDOR.toString()+ ".id.vinCodigo", vinCodigoResp)
					));
		}
		if(origem != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), origem));
		}
		if(espSeq != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesEmAtendimentoVO.class));

		return executeCriteria(criteria);
	}
	
	public List<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion2(Date dataInicio, Date dataFim, Integer pacCodigo, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp, DominioOrigemAtendimento origem, Short espSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.INNER_JOIN);
		criteria.createAlias("cons."+AacConsultas.Fields.CONTROLE.toString(), "ctrl", JoinType.INNER_JOIN);
		criteria.createAlias("ctrl."+MamControles.Fields.EXTRATO_CONTROLES.toString(), "extCtrl",JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias("extCtrl."+MamExtratoControles.Fields.SERVIDOR.toString(), "serv", JoinType.INNER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projectionList = Projections.projectionList()
			.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_SIGLA.toString()))
			.add(Projections.property("esp." + AghEspecialidades.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_NOME.toString()))
			.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()).as(PacientesEmAtendimentoVO.Fields.UNF_DESCRICAO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.ATD_SEQ.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.ORIGEM.toString()).as(PacientesEmAtendimentoVO.Fields.ORIGEM.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()).as(PacientesEmAtendimentoVO.Fields.IND_PAC_ATENDIMENTO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()).as(PacientesEmAtendimentoVO.Fields.DTHR_INICIO.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.CON_NUMERO.toString()).as(PacientesEmAtendimentoVO.Fields.CON_NUMERO.toString()))
			.add(Projections.property("extCtrl." + MamExtratoControles.Fields.SERVIDOR_MATRICULA.toString()).as(PacientesEmAtendimentoVO.Fields.MATRICULA_RESP.toString()))
			.add(Projections.property("extCtrl." + MamExtratoControles.Fields.SERVIDOR_VIN_CODIGO.toString()).as(PacientesEmAtendimentoVO.Fields.VIN_CODIGO_RESP.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.ESP_SEQ.toString()))
			.add(Projections.property("atd." + AghAtendimentos.Fields.UNF_SEQ.toString()).as(PacientesEmAtendimentoVO.Fields.UNF_SEQ.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.CODIGO.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_CODIGO.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_NOME.toString()))
			.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()).as(PacientesEmAtendimentoVO.Fields.PAC_PRONTUARIO.toString()))
			.add(Projections.property("pf." + RapPessoasFisicas.Fields.NOME.toString()).as(PacientesEmAtendimentoVO.Fields.NOME_RESP.toString()));
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A));
		criteria.add(Restrictions.between("atd."+AghAtendimentos.Fields.DATA_HORA_INICIO.toString(), dataInicio, dataFim));
		criteria.add(Restrictions.eq("ctrl."+MamControles.Fields.SITUACAO_ATENDIMENTOS_SEQ.toString(), Short.parseShort("7")));
		criteria.add(Restrictions.eq("extCtrl."+MamExtratoControles.Fields.SITUACAO_ATENDIMENTO_SEQ.toString(), Short.parseShort("7")));

		if(pacCodigo != null){
			criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		}
		if(unfSeq != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if(matriculaResp != null && vinCodigoResp != null){
			criteria.add(Restrictions.eq("extCtrl." + MamExtratoControles.Fields.SERVIDOR_MATRICULA.toString(), matriculaResp));
			criteria.add(Restrictions.eq("extCtrl." + MamExtratoControles.Fields.SERVIDOR_VIN_CODIGO.toString(), vinCodigoResp));
		}
		if(origem != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), origem));
		}
		if(espSeq != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesEmAtendimentoVO.class));

		return executeCriteria(criteria);
	}	
	
	public List<AghAtendimentos> buscarAtendimentos(Integer pacCodigo, Date dataPrevisao){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "inter", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.add(Restrictions.le(AghAtendimentos.Fields.DTHR_INICIO.toString(), dataPrevisao));
		
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.isNull(AghAtendimentos.Fields.DTHR_FIM.toString()))
				.add(Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), dataPrevisao)));
		
		return executeCriteria(criteria);
		
	}
	
	public List<ContagemQuimioterapiaVO> buscarCuidadosPrescricaoQuimioterapia(final Date dataInicioProcessamento, final Date dataFimProcessamento) {
		StringBuilder sql = new StringBuilder(1800);
		
		sql.append("SELECT ATD.SEQ AS atdPaciente,  "); 
		sql.append("	   ATD.PAC_CODIGO AS paciente, ");
		sql.append("       ATD.TRP_SEQ AS tratamentoTerapeuticos, ");
		sql.append("       AGP.UNF_SEQ AS unidade, ");
		sql.append("       UNF.CCT_CODIGO AS centroCusto, ");
		sql.append("       PHI.SEQ AS phi, ");
		sql.append("       OCV.SEQ AS ocv, ");
		sql.append("       MPT.DT_PREV_EXECUCAO AS dtPrevExecucao, ");
		sql.append("       PCO.CDU_SEQ AS cuidado, ");
		sql.append("       PCO.TFQ_SEQ AS freqAprazamento, ");
		sql.append("       PCO.FREQUENCIA AS frequencia, ");
		sql.append("       PCO.QUANTIDADE_UNIDADE AS quantidadeUnidade, ");
		sql.append("       PCO.UMM_SEQ AS unMedida, ");
		sql.append("       COUNT(*) AS qtdCuidados ");
		sql.append("  FROM AGH.AGH_ATENDIMENTOS ATD, ");
		sql.append("       AGH.MPT_PRESCRICAO_PACIENTES MPT, ");
		sql.append("       AGH.MPT_PRESCRICAO_CUIDADOS PCO, ");
		sql.append("       AGH.MPT_AGENDA_PRESCRICOES AGP, ");
		sql.append("       AGH.AGH_UNIDADES_FUNCIONAIS UNF, ");
		sql.append("       AGH.FAT_PROCED_HOSP_INTERNOS PHI ");
		sql.append("       LEFT JOIN AGH.SIG_OBJETO_CUSTO_PHIS OCP ON PHI.SEQ = OCP.PHI_SEQ ");
		sql.append("       LEFT JOIN AGH.SIG_OBJETO_CUSTO_VERSOES OCV ON OCP.OCV_SEQ = OCV.SEQ ");
		sql.append(" WHERE (ATD.IND_TIPO_TRATAMENTO = 29 OR ATD.TPT_SEQ = 6) ");
		sql.append("   AND ATD.TRP_SEQ IS NOT NULL ");
		sql.append("   AND ATD.SEQ = MPT.ATD_SEQ ");
		
		//MPT_PRESCRICAO_PACIENTES possuem auto-relacionamento, nÃ£o monta a query de forma correta com HQL.
		
		sql.append("   AND MPT.ATD_SEQ = AGP.PTE_ATD_SEQ ");
		sql.append("   AND MPT.SEQ = AGP.PTE_SEQ ");
		
		/**
		 * NÃ£o possui mapeamento com  AGH.MPT_PRESCRICAO_CUIDADOS PCO, nÃ£o sendo posivel executar com criteria.
		 */
		sql.append("   AND MPT.ATD_SEQ = PCO.PTE_ATD_SEQ ");
		sql.append("   AND MPT.SEQ = PCO.PTE_SEQ ");
	
		
		sql.append("   AND AGP.IND_SITUACAO = 'A' ");
		sql.append("   AND AGP.UNF_SEQ = UNF.SEQ ");
		sql.append("   AND PCO.IND_SITUACAO_ITEM IN ('V', 'A', 'E') ");
		sql.append("   AND PCO.CDU_SEQ = PHI.CDU_SEQ ");
		sql.append("   AND DATE_TRUNC('day', MPT.DT_PREV_EXECUCAO) BETWEEN :dataInicio AND :dataFim ");
		sql.append("GROUP BY ATD.PAC_CODIGO, ATD.SEQ, ATD.TRP_SEQ, AGP.UNF_SEQ, UNF.CCT_CODIGO, ");
		sql.append("PHI.SEQ, OCV.SEQ, MPT.DT_PREV_EXECUCAO, PCO.CDU_SEQ, PCO.TFQ_SEQ, PCO.FREQUENCIA, ");
		sql.append("PCO.QUANTIDADE_UNIDADE, PCO.UMM_SEQ ");
		sql.append("ORDER BY MPT.DT_PREV_EXECUCAO ASC, ATD.PAC_CODIGO, ATD.SEQ, ATD.TRP_SEQ, ");
		sql.append("AGP.UNF_SEQ, UNF.CCT_CODIGO, PHI.SEQ, OCV.SEQ, PCO.CDU_SEQ, PCO.TFQ_SEQ, ");
		sql.append("PCO.FREQUENCIA, PCO.QUANTIDADE_UNIDADE, PCO.UMM_SEQ ");

		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setDate("dataInicio", dataInicioProcessamento);
		query.setDate("dataFim", dataFimProcessamento);
		query.setResultTransformer(Transformers.aliasToBean(ContagemQuimioterapiaVO.class));
		
		query.addScalar("atdPaciente", IntegerType.INSTANCE);
		query.addScalar("paciente", IntegerType.INSTANCE);
		
		return query.list();
	}
	
	/**
	 * Busca os atendimentos acordando com os filtros da tela.
	 * 
	 * @param paciente
	 * @param cids
	 * @param cCUstos
	 * @param especialidades
	 * @param profissionaisResponsaveis
	 * @return
	 */
	public List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosPacienteTelaVisualizarCustoPaciente (Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente, List<AghCid> cids, 
			List<FccCentroCustos> cCUstos,
			List<AghEspecialidades> especialidades,
			List<RapServidores> profissionaisResponsaveis) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		
		if(paciente != null && paciente.getCodigo() != null){
			criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.CODIGO.toString(), paciente.getCodigo()));
		}	
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.distinct(Projections.property("atd." + AghAtendimentos.Fields.PRONTUARIO.toString())), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.DT_NASCIMENTO_PACIENTE.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOMEMAE.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.NOME_PACIENTE_MAE.toString())
		);
		
		if((cids != null && !cids.isEmpty())  
				|| (cCUstos != null && !cCUstos.isEmpty())
				|| (especialidades != null && !especialidades.isEmpty())
				|| (profissionaisResponsaveis != null && !profissionaisResponsaveis.isEmpty())){
			
			criteria.add(Subqueries.propertyIn("atd."+AghAtendimentos.Fields.SEQ.toString(), 
					this.subqueryDaObterAghAtendimentosPorFiltrosPacienteTelaVisualizarCustoPaciente(
							cids, 
							cCUstos,
							especialidades,
							profissionaisResponsaveis		
					)));
			
		}
		
		criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghAtendimentosVO.class));
		
		if(firstResult==null && maxResult==null && orderProperty==null){
		return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}
	}
	
	private DetachedCriteria subqueryDaObterAghAtendimentosPorFiltrosPacienteTelaVisualizarCustoPaciente(List<AghCid> cids, 
			List<FccCentroCustos> cCUstos,
			List<AghEspecialidades> especialidades,
			List<RapServidores> profissionaisResponsaveis){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cap");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("cap." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString())), SigCalculoAtdPaciente.Fields.ATD_SEQ.toString()));
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.LISTA_CALCULOS_CIDS.toString(), "cids", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cam", JoinType.INNER_JOIN);
		criteria.createAlias("cam."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", JoinType.INNER_JOIN);
		
		
		//Todos filtros preenchidos
		if(!cids.isEmpty() && !especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos))));
		}
		//3 filtros
		else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!cids.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!especialidades.isEmpty()&&!cids.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cids.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		} 
		//2 filtros
		else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)));
		} else if(!especialidades.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} 
		else if(!especialidades.isEmpty()&&!cids.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		} else if(!especialidades.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		}
		else if (!especialidades.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades));
		} else if (!profissionaisResponsaveis.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis));
		} else if(!cCUstos.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos));
		} else if(!cids.isEmpty()){
			criteria.add(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids));
		}	
		
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		
		return criteria;
	}
	
	public List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPaciente(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto competenciaSelecionada, List<AghCid> cids, 
			List<FccCentroCustos> cCUstos,
			List<AghEspecialidades> especialidades,
			List<RapServidores> profissionaisResponsaveis, 
			Boolean pacienteComAlta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.distinct(Projections.property("atd." + AghAtendimentos.Fields.PRONTUARIO.toString())), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.DT_NASCIMENTO_PACIENTE.toString())
				.add(Projections.property("pac." + AipPacientes.Fields.NOMEMAE.toString()), br.gov.mec.aghu.vo.AghAtendimentosVO.Fields.NOME_PACIENTE_MAE.toString())
		);
		
		criteria.add(Subqueries.propertyIn("atd."+AghAtendimentos.Fields.SEQ.toString(), 
				this.subqueryDaObterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPaciente(
						competenciaSelecionada,
						cids, 
						cCUstos,
						especialidades,
						profissionaisResponsaveis,
						pacienteComAlta
				)));
			
		criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AghAtendimentosVO.class));
		
		if(firstResult==null && maxResult==null && orderProperty==null){
		return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}
	}

	private DetachedCriteria subqueryDaObterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPaciente(SigProcessamentoCusto competenciaSelecionada, List<AghCid> cids, List<FccCentroCustos> cCUstos, List<AghEspecialidades> especialidades, List<RapServidores> profissionaisResponsaveis, Boolean pacienteComAlta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cap");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("cap." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString())), SigCalculoAtdPaciente.Fields.ATD_SEQ.toString()));
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.LISTA_CALCULOS_CIDS.toString(), "cids", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cam", JoinType.INNER_JOIN);
		criteria.createAlias("cam."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
		
		if(competenciaSelecionada != null && competenciaSelecionada.getSeq() != null){
			criteria.add(Restrictions.eq("cap."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), competenciaSelecionada.getSeq()));
		
			if(pacienteComAlta){
				criteria.add(Restrictions.eq("cap."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA));
					criteria.add(Restrictions.isNotNull("atd."+AghAtendimentos.Fields.DTHR_FIM.toString()));
					criteria.add(Restrictions.between("atd."+AghAtendimentos.Fields.DTHR_FIM.toString(), competenciaSelecionada.getDataInicio(), competenciaSelecionada.getDataFim()));
			}
		}
		
		if(cids != null && !cids.isEmpty()){
			criteria.add(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids));
		}
		
		//Todos filtros preenchidos
		if(!cids.isEmpty() && !especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos))));
		}
		//3 filtros
		else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!cids.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!especialidades.isEmpty()&&!cids.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()&&!cids.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		} 
		//2 filtros
		else if(!especialidades.isEmpty()&&!profissionaisResponsaveis.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis)));
		} else if(!especialidades.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!especialidades.isEmpty()&&!cids.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		} else if(!profissionaisResponsaveis.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if(!profissionaisResponsaveis.isEmpty()&&!cids.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis),Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids)));
		} else if(!cids.isEmpty()&&!cCUstos.isEmpty()){
			criteria.add(Restrictions.or(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids),Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos)));
		} else if (!especialidades.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades));
		} else if (!profissionaisResponsaveis.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis));
		} else if(!cCUstos.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos));
		} else if(!cids.isEmpty()){
			criteria.add(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids));
		}	
		return criteria;
	}

	/**
 	 * Retorna Atendimento pela PK, apenas se este tiver
	 * a devida associação com:
	 * Unidade Funcional e Centro de Custo.
	 * 
	 * @param seq
	 * @return
	 */
	public AghAtendimentos obterDetalhadoPorChavePrimaria(Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "CCAPLIC", JoinType.INNER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CARACTS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "CON",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.CONTROLE.toString(), "CNT",JoinType.LEFT_OUTER_JOIN);
		
		AghAtendimentos  atendimento = (AghAtendimentos) executeCriteriaUniqueResult(criteria);
		return atendimento;
	}
	
	/**
	 * Buscar os dados da Gestante tendo como parÃ¢metro o nÃºmero da consulta
	 * 
	 * Web Service #36608
	 * 
	 * @param nroConsulta
	 * @return
	 */
	public AghAtendimentos obterDadosGestantePorConsulta(final Integer nroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.SEXO.toString(), DominioSexo.F));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), nroConsulta));
		return (AghAtendimentos) super.executeCriteriaUniqueResult(criteria);
	}

	private void getCriteriaComAtdSeqECodigo(Integer atdSeq,
			Integer pacCodigo, DetachedCriteria criteria) {
		if(atdSeq != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}
		
		if(pacCodigo != null){
			criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "paciente");
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
	}
	
	private String truncDateHql(String coluna){
		StringBuffer buffer = new StringBuffer();
		if(isOracle()){
			buffer.append(" trunc( ");
			buffer.append(coluna);
			buffer.append(" ) ");
		}else{
			buffer.append(" date_trunc('day', ");
			buffer.append(coluna);
			buffer.append(" ) ");
		}
		
		return buffer.toString();
	}
	
	private void adicionaRestricaoHorarioAtendimentoTruncado(
			DetachedCriteria criteria,
			DominioOrigemAtendimento origemAtendimento, String coluna,
			Integer qtdHorasIntervaloAtd) {
		if(origemAtendimento != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), origemAtendimento));
		}
		
		StringBuffer sqlRestric = new StringBuffer(57);
		sqlRestric.append(isOracle() ? truncDateHql("sysdate") : truncDateHql("now()"));
		sqlRestric.append(" between ");
		sqlRestric.append(truncDateHql(coluna));
		sqlRestric.append(" and ");
		
		String columnAdd;
		if(isOracle()){
			double qtdH = qtdHorasIntervaloAtd / 24;
			columnAdd = truncDateHql(coluna + " + " + qtdH);
		}else{
			columnAdd = truncDateHql(coluna + 
					" + interval '".concat(qtdHorasIntervaloAtd.toString()).concat(" hour '"));
		}
		
		sqlRestric.append(columnAdd);
		
		criteria.add(Restrictions.sqlRestriction(sqlRestric.toString()));
		
	}
	
	public List<AghAtendimentos> pesquisarAtendimento(Integer pacCodigo, Integer atdSeq, 
			DominioPacAtendimento pacAtendimento, List<DominioOrigemAtendimento> origens) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
			criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUARTO", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEITO", JoinType.LEFT_OUTER_JOIN);
            
			getCriteriaComAtdSeqECodigo(atdSeq, pacCodigo, criteria);
			
			if(pacAtendimento != null){
				criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), pacAtendimento));
			}
			
			if(origens != null && !origens.isEmpty()){
				criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));
			}
			
			return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de atendimentos de origem Externo('X') que permitem prescricao mÃ©dica, restrito a horÃ¡rio do atendimento
	 * @param atdSeq
	 * @param prontuario
	 * @param qtdHorasPermitePrescricaoMedica
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosExternoPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq, 
			Integer qtdHorasIntervaloAtd) {
		DetachedCriteria criteria = this.criarCriteriaAtendimentoSolicitacaoExames();
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUARTO", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEITO", JoinType.LEFT_OUTER_JOIN);
		
		getCriteriaComAtdSeqECodigo(atdSeq, pacCodigo, criteria);
		adicionaRestricaoHorarioAtendimentoTruncado(criteria, DominioOrigemAtendimento.X, "this_.DTHR_INICIO", qtdHorasIntervaloAtd);
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria);
	}
	
	public List<AghAtendimentos> pesquisarAtendimentosCirurgicosPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq, Integer qtdHorasIntervaloAtd) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUARTO", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEITO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AghAtendimentos.Fields.CIRURGIAS.toString(), "cirurgias");
		criteria.add(Restrictions.not(
				Restrictions.in("cirurgias."+MbcCirurgias.Fields.SITUACAO, 
						Arrays.asList(DominioSituacaoCirurgia.AGND, DominioSituacaoCirurgia.CANC))));
		getCriteriaComAtdSeqECodigo(atdSeq, pacCodigo, criteria);
		//cirurgias1_.DTHR_INICIO_CIRG
		adicionaRestricaoHorarioAtendimentoTruncado(criteria, DominioOrigemAtendimento.C, "this_.DTHR_INICIO", qtdHorasIntervaloAtd);
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de atendimentos de origem Ambulatorial ('A') que permitem prescricao mÃ©dica, restrito a horÃ¡rio em MamExtrattoControles
	 * @param atdSeq
	 * @param prontuario
	 * @param qtdHorasPermitePrescricaoMedica
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosAmbulatoriaisPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq, Integer qtdHorasIntervaloAtd) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.QUARTO.toString(), "QUARTO", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEITO", JoinType.LEFT_OUTER_JOIN);
		
		getCriteriaComAtdSeqECodigo(atdSeq, pacCodigo, criteria);
		adicionaRestricaoHorarioAtendimentoTruncado(criteria, DominioOrigemAtendimento.A, "this_.DTHR_INICIO", qtdHorasIntervaloAtd);
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Buscar a Ãºltima consulta da gestante tendo como parÃ¢metro o sequencial da gestaÃ§Ã£o e o cÃ³digo do paciente
	 * 
	 * Web Service #36620
	 * 
	 * @param gsoSeqp
	 * @param pacCodigo
	 * @return
	 */
	public Integer obterDadosGestantePorGestacaoPaciente(final Short gsoSeqp, final Integer gsoPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		}
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		}
		criteria.add(Restrictions.eqProperty("ATD." + AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), "PAC." + AipPacientes.Fields.CODIGO.toString()));
		criteria.setProjection(Projections.max("CON." + AacConsultas.Fields.NUMERO.toString()));
		return (Integer) super.executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(
			Integer atdSeq, 
			List<DominioOrigemAtendimento> origensInternacao,
			List<DominioOrigemAtendimento> origensAmbulatorio, 
			Integer prontuario,	Integer qtdHorasLimiteAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		if(atdSeq != null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}
		
		StringBuffer sqlRestric = new StringBuffer(57);
		sqlRestric.append(isOracle() ? " sysdate " : " now() ");
		sqlRestric.append(" between ");
		sqlRestric.append(" this_.DTHR_INICIO");
		sqlRestric.append(" and this_.DTHR_INICIO");
		
		if(isOracle()){
			sqlRestric.append(" + ");
			double horasEmDias = qtdHorasLimiteAtendimento / 24;
			sqlRestric.append(horasEmDias);
		}else{
			sqlRestric.append(" + interval '");
			sqlRestric.append(qtdHorasLimiteAtendimento);
			sqlRestric.append(" hour ' ");
		}
		
		if(origensInternacao != null && origensAmbulatorio != null &&
				!origensInternacao.isEmpty() && !origensAmbulatorio.isEmpty()){
			criteria.add(Restrictions.or(
					Restrictions.and(
							Restrictions.sqlRestriction(sqlRestric.toString()),
							Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origensAmbulatorio)),
					Restrictions.and(
						Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S),
						Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origensInternacao))));
		}else{
			if(origensAmbulatorio != null && !origensAmbulatorio.isEmpty()){
				criteria.add(Restrictions.and(
						Restrictions.sqlRestriction(sqlRestric.toString()),
						Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origensAmbulatorio)));
			}
		}
		
		if(prontuario != null){
			criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "paciente");
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}
		criteria.addOrder(Order.desc(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AghAtendimentos> pesquisarAtendimentosPorItemSolicitacaoExame(
			Integer iseSoeSeq, Short iseSeqp, List<DominioOrigemAtendimento> origensAtendimento) {
		DetachedCriteria criteria = this.criarCriteriaAtendimentoSolicitacaoExames();
		
		if(iseSoeSeq != null){
			criteria.add(Restrictions.eq("itensSolicitacaoExame."+AelItemSolicitacaoExames.Fields.SEQP.toString(), iseSeqp));
		}
		
		if(iseSeqp != null){
			criteria.add(Restrictions.eq("itensSolicitacaoExame."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		}
		
		if(origensAtendimento != null && !origensAtendimento.isEmpty()){
			criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origensAtendimento));
		}
		
		return this.executeCriteria(criteria);
	}

	//#1297 - C5
	public List<AghAtendimentos> obterAghAtendimentoPorDadosPaciente(Integer codigoPaciente, Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UND", JoinType.INNER_JOIN);
		
		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq("PAC."	+ AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		}
		if (prontuario != null) {
			criteria.add(Restrictions.eq("PAC."	+ AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		criteria.addOrder(Order.desc("ATD." + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		return executeCriteria(criteria);
	}
	
	public AghAtendimentos obterAghAtendimentoPorSeq(Integer seq) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
			criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
			criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UND", JoinType.INNER_JOIN);
			
			criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), seq));
			
			return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	
	
	public DadosPacientesEmAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString())
						, DadosPacientesEmAtendimentoVO.Fields.DT_CONSULTA.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.SER_MATRICULA.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ATD_SER_MATRICULA.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.SER_VIN_CODIGO.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ATD_SER_VIN_CODIGO.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.UNF_SEQ.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ATD_UNF_SEQ.toString())
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SEQ.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ESP_SEQ.toString())
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SIGLA.toString())
						, DadosPacientesEmAtendimentoVO.Fields.ESP_SIGLA.toString()));
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DadosPacientesEmAtendimentoVO.class));
		List<DadosPacientesEmAtendimentoVO> listResult = executeCriteria(criteria);
		return listResult != null && !listResult.isEmpty()? listResult.get(0) : null;
	}
	
	/**
	 * # 39006 - ServiÃ§o que obtem AghAtendimentos
	 * @param seq
	 * @return
	 */
	public AghAtendimentos obterAghAtendimentosPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Web Service #40702
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPorPacienteDataInicio(final Integer pacCodigo, final Date dthrInicio) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrInicio));

		DominioOrigemAtendimento[] origem = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.N, DominioOrigemAtendimento.I };
		criteria.add(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origem));

		List<AghAtendimentos> result = super.executeCriteria(criteria, 0, 1, null, false);

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public Boolean existePacienteInternadoListarPacientesCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");

		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I, DominioOrigemAtendimento.N)));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));

		return executeCriteriaExists(criteria);
	}
	
	public List<Integer> pesquisarAtendimentosUltimaInternacaoPacientes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		DetachedCriteria subquery = DetachedCriteria.forClass(AghAtendimentos.class, "ATD2");
		subquery.setProjection(Projections.max("ATD2." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
		subquery.add(Restrictions.in("ATD2." + AghAtendimentos.Fields.ORIGEM.toString(), Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I, DominioOrigemAtendimento.N)));
		subquery.add(Restrictions.eqProperty("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), "ATD2." + AghAtendimentos.Fields.PAC_CODIGO.toString()));

		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), subquery));
		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.SEQ.toString(), subquery));
		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString(), subquery));
		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.UNF_SEQ.toString(), subquery));
		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), subquery));
		criteria.add(Subqueries.propertyIn("ATD." + AghAtendimentos.Fields.DTHR_INGRESSO_UNIDADE.toString(), subquery));
		
		criteria.setProjection(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	// InformaÃ§Ã£o enviada prescribente
	public List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacaoEDataDe(Integer internacaoSeq, final Integer codigoPacienteGestacao, final Short seqGestacao, Date data1, Date data2) {
		
		StringBuffer sql = new StringBuffer()
		
		.append(" SELECT ATD.GSO_PAC_CODIGO as gsoPacCodigo, ") 
		.append(" 	ATD.GSO_SEQP as gsoSeqp,  ")
		.append(" 	ATD.PAC_CODIGO as pacCodigo, ")
		.append(" 	ATD.INT_SEQ as internacaoSeq,  ")
		.append(" 	ATD.DTHR_INICIO as dataInicio, ")
		.append(" 	NAS.EQP_SEQ as equipeSeq, ") 
		.append(" 	NAS.TIPO as tipo, ") 
		.append(" 	NAS.RN_CLASSIFICACAO as rnClassificacao, ") 
		.append(" 	NAS.DTHR_NASCIMENTO as dataNascimento ") 
		.append(" FROM AGH_ATENDIMENTOS ATD, MCO_GESTACOES GSO, MCO_NASCIMENTOS NAS ")
		.append(" WHERE ATD.INT_SEQ = "+internacaoSeq+" ")
		.append(" 	AND ATD.PAC_CODIGO = ATD.GSO_PAC_CODIGO ")
		.append(" 	AND GSO.PAC_CODIGO =  ATD.GSO_PAC_CODIGO ")
		.append(" 	AND GSO.SEQP = ATD.GSO_SEQP ")
		.append(" 	AND  NAS.GSO_PAC_CODIGO = ATD.GSO_PAC_CODIGO ")
		.append(" 	AND  NAS.GSO_SEQP = ATD.GSO_SEQP ")
		
		//Não está no documento mas por analise, deve ser isso...
		.append(" 	AND ATD.GSO_PAC_CODIGO = "+codigoPacienteGestacao+" ")
		.append(" 	AND  ATD.GSO_SEQP = "+seqGestacao+" ")
		
		.append(" 	AND NAS.DTHR_NASCIMENTO BETWEEN :data1 AND :data2 ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setDate("data1", data1);
		query.setDate("data2", data2);
		
		query.setResultTransformer(Transformers.aliasToBean(AtendimentoNascimentoVO.class));
		
		query.addScalar("gsoPacCodigo", IntegerType.INSTANCE);
		query.addScalar("gsoSeqp", IntegerType.INSTANCE);
		query.addScalar("pacCodigo", IntegerType.INSTANCE);
		query.addScalar("internacaoSeq", IntegerType.INSTANCE);
		query.addScalar("dataInicio", DateType.INSTANCE);
		query.addScalar("equipeSeq", IntegerType.INSTANCE);
		query.addScalar("tipo", StringType.INSTANCE);
		query.addScalar("rnClassificacao", StringType.INSTANCE);
		query.addScalar("dataNascimento", DateType.INSTANCE);
		
		return query.list();
	}
	
	public List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacao(Integer internacaoSeq, final Integer codigoPacienteGestacao, final Short seqGestacao){
		
		StringBuffer sql = new StringBuffer()
		
		.append(" SELECT NAS.SEQP  as gsoSeqp, ") 
		.append(" NAS.RN_CLASSIFICACAO as rnClassificacao ")
		.append(" FROM AGH_ATENDIMENTOS ATD,  ")
		.append(" MCO_NASCIMENTOS NAS ")
		.append(" WHERE ATD.INT_SEQ = "+internacaoSeq) 
		.append(" AND ATD.PAC_CODIGO = ATD.GSO_PAC_CODIGO  ")
		.append(" AND  NAS.GSO_PAC_CODIGO = ATD.GSO_PAC_CODIGO  ")
		.append(" AND  NAS.GSO_SEQP = ATD.GSO_SEQP  ")
		
		//Não está no documento mas por analise, deve ser isso...
		.append(" 	AND ATD.GSO_PAC_CODIGO = "+codigoPacienteGestacao+" ")
		.append(" 	AND  ATD.GSO_SEQP = "+seqGestacao+" ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setResultTransformer(Transformers.aliasToBean(AtendimentoNascimentoVO.class));
		
		query.addScalar("gsoSeqp", IntegerType.INSTANCE);
		query.addScalar("rnClassificacao", StringType.INSTANCE);
		
		return query.list();
	}
	
	public AghAtendimentos obterAghAtendimentosAnamneseEvolucao(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), seq));
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PACIENTE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR.toString(), "SERVIDOR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERVIDOR."+RapServidores.Fields.PESSOA_FISICA, "PESSOA_FISICA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString() , "UNIDADE_FUNCIONAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() , "ESPECIALIDADE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString() , "INTERNACAO", JoinType.LEFT_OUTER_JOIN);
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);		
	}
	
	
	public List<AghAtendimentosPacienteCnsVO> buscarAtendimentosFetchAipPacientesDadosCns(Integer adtSeq){
		StringBuffer sql = new StringBuffer()
		
		.append(" SELECT 	ATD.PRONTUARIO as prontuario,  ")
		.append(" 			ATD.PAC_CODIGO as pacCodigo,  ")
		.append(" 			PDS.SER_MATRICULA as matricula,  ")
		.append(" 			PDS.SER_VIN_CODIGO as vinCodigo,  ")
		.append(" 			PDS.CRIADO_EM as criadoEm")
		.append(" FROM 	AGH_ATENDIMENTOS ATD, ") 
		.append(" 			AIP_PACIENTES_DADOS_CNS PDS ")
		.append(" WHERE ")  
		.append(" 			ATD.ATD_SEQ_MAE = "+adtSeq) 
		.append(" 			AND ATD.PAC_CODIGO = PDS.PAC_CODIGO ")
		.append(" 			AND PDS.TIPO_CERTIDAO = "+DominioTipoCertidao.NASCIMENTO.getCodigo())
		.append(" 			AND PDS.NOME_CARTORIO IS NOT NULL ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setResultTransformer(Transformers.aliasToBean(AghAtendimentosPacienteCnsVO.class));
		
		query.addScalar("prontuario", IntegerType.INSTANCE);
		query.addScalar("pacCodigo", IntegerType.INSTANCE);
		query.addScalar("matricula", IntegerType.INSTANCE);
		query.addScalar("vinCodigo", ShortType.INSTANCE);
		query.addScalar("criadoEm", DateType.INSTANCE);
		
		return query.list();
	}
	
	public AghAtendimentos pesquisarAtendimentosComOrigemUrgencia(final Integer pacCodigo, final Short gsoSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		criteria.add(
				Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()),
						Restrictions.and(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.U), 
						Restrictions.and(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), pacCodigo), 
								Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp))
						)
						
				)
		);
		return (AghAtendimentos)executeCriteriaUniqueResult(criteria);
	}
	
	public AghAtendimentos pesquisarAtendimentosComOrigemAmbulatorial(final Integer pacCodigo, final Short gsoSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));
		
		return (AghAtendimentos)executeCriteriaUniqueResult(criteria);
	}
		public List<InformacaoEnviadaPrescribenteVO> pesquisarInformacaoEnviadaPrescribente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
				final InformacaoEnviadaPrescribenteVO filtro) {
			DetachedCriteria criteria = montarQuery(filtro, true);
			List<InformacaoEnviadaPrescribenteVO> lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
			return lista;
		}
		
		public Long pesquisarInformacaoEnviadaPrescribenteCount(InformacaoEnviadaPrescribenteVO filtro) {
			DetachedCriteria criteria = montarQuery(filtro, false);
			return this.executeCriteriaCount(criteria);
		}
		
		
		public DetachedCriteria montarQuery(final InformacaoEnviadaPrescribenteVO filtro, boolean ordenado) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MpmInformacaoPrescribente.class, "ipr");
			
			criteria.createAlias("ipr."+MpmInformacaoPrescribente.Fields.ATENDIMENTO.toString(), "ate", JoinType.INNER_JOIN );
			criteria.createAlias("ate."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);		
	        criteria.createAlias("ipr."+MpmInformacaoPrescribente.Fields.UNIDADE_FUNCIONAL.toString(), "uni", JoinType.INNER_JOIN );
	        criteria.createAlias("ipr."+MpmInformacaoPrescribente.Fields.SERVIDOR.toString(), "ser", JoinType.INNER_JOIN );
	        criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN );
	        criteria.setProjection(Projections.projectionList()
	    			.add(Projections.property("ate." + AghAtendimentos.Fields.PRONTUARIO.toString())
	    					, InformacaoEnviadaPrescribenteVO.Fields.PRONTUARIO.toString())
	    			.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString())
	    					, InformacaoEnviadaPrescribenteVO.Fields.PACIENTE_NOME.toString())
	    			.add(Projections.property("uni." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
	    					, InformacaoEnviadaPrescribenteVO.Fields.SEQ_UNIDADES_FUNCIONAIS.toString())
	    			.add(Projections.property("uni." + AghUnidadesFuncionais.Fields.DESCRICAO.toString())
	    					, InformacaoEnviadaPrescribenteVO.Fields.DESCRICAO_UNIDADES_FUNCIONAIS.toString())
	    			.add(Projections.property("ipr." + MpmInformacaoPrescribente.Fields.CRIADO_EM.toString())
	    					, InformacaoEnviadaPrescribenteVO.Fields.CRIADO_EM.toString()) 
	    			.add(Projections.property("ipr." + MpmInformacaoPrescribente.Fields.IND_INF_VERIFICADA.toString())
	    	    			, InformacaoEnviadaPrescribenteVO.Fields.INFORMACAO_VERIFICADA.toString()) 
	    	    			.add(Projections.property("ipr." + MpmInformacaoPrescribente.Fields.SEQ.toString()),
	    	    					InformacaoEnviadaPrescribenteVO.Fields.SEQ.toString()) 
	    	    	.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString())
	    	    	    	, InformacaoEnviadaPrescribenteVO.Fields.RESPONSAVEL.toString()) 
	    					
	    	);   
	        inserirFiltros(criteria, filtro, ordenado);
	        criteria.setResultTransformer(Transformers.aliasToBean(InformacaoEnviadaPrescribenteVO.class));
	        return (criteria);	
	    }
		
		private void inserirFiltros(DetachedCriteria criteria, final InformacaoEnviadaPrescribenteVO filtro, boolean ordenado) {
			if (filtro.getProntuario() != null){
				criteria.add(Restrictions.eq("ate." + AghAtendimentos.Fields.PRONTUARIO.toString(), filtro.getProntuario()));
			}
			if (filtro.getSeqUnidadesFuncionais()!= null){
				criteria.add(Restrictions.eq("uni." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), filtro.getSeqUnidadesFuncionais()));
			}
			if (filtro.getCriadoEm()!= null){
				Date dataInicio = DateUtil.obterDataComHoraInical(filtro.getCriadoEm());
				Date dataFim = DateUtil.obterDataComHoraFinal(filtro.getCriadoEm());
				criteria.add(Restrictions.between("ipr." + MpmInformacaoPrescribente.Fields.CRIADO_EM.toString(), dataInicio, dataFim));
			}
			if (filtro.getIndInfVerificada()!= null){
				criteria.add(Restrictions.eq("ipr." + MpmInformacaoPrescribente.Fields.IND_INF_VERIFICADA.toString(), filtro.getIndInfVerificada()));
			}
		}	

	/**
	 * #44183 - C1
	 * @author marcelo.deus
	 */
	public Boolean verificarExistenciaProntuarioOuPacienteComDietaOpcional(Integer prontuario, Integer pacCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.AGH_ATENDIMENTO_PACIENTES.toString(), "APA")
				.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC")
				.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF")
				.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CUN");
		
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A))
				.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario))
				.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), 
									Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I, DominioOrigemAtendimento.N)), 
									Restrictions.eq("CUN." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA)),
									Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), 
									Arrays.asList(DominioOrigemAtendimento.A, DominioOrigemAtendimento.C, DominioOrigemAtendimento.X)))); 
		
		return executeCriteriaExists(criteria);
	}

	/**#45341 C2 - Retorna apenas um resultado com o nome, nÃºmero do prontuÃ¡rio e codigo do paciente **/
	public ConsultarRetornoConsultoriaVO obterPacienteNroProntuario(Integer pAtdSeq){
		DetachedCriteria criteria  = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");		
		criteria.add(Restrictions.and(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), pAtdSeq), 
				Restrictions.eqProperty("PAC."+AipPacientes.Fields.CODIGO.toString(),"ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString())));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString()), ConsultarRetornoConsultoriaVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("ATD."+AghAtendimentos.Fields.PRONTUARIO.toString()), ConsultarRetornoConsultoriaVO.Fields.COD_PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()),ConsultarRetornoConsultoriaVO.Fields.NOME_PACIENTE.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultarRetornoConsultoriaVO.class));
		
		return (ConsultarRetornoConsultoriaVO)executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterUltimoSeqAtdPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.N)));
		criteria.setProjection(Projections.max(AghAtendimentos.Fields.SEQ.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public AghAtendimentos obterUltimoAtendimentoPacienteListaGermes(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		DetachedCriteria subQueryAtp = DetachedCriteria.forClass(AghAtendimentos.class, "ATP");
		subQueryAtp.createAlias("ATP." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		subQueryAtp.setProjection(Projections.max("ATP." + AghAtendimentos.Fields.SEQ.toString()));
		subQueryAtp.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		
		Collection<DominioOrigemAtendimento> origens = Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.N);
		subQueryAtp.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));

		criteria.add(Subqueries.propertyIn(AghAtendimentos.Fields.SEQ.toString(), subQueryAtp));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarPacienteInternadoCaracteristicaControlePrevisao(List<Short> unidadesFuncionaisSeq, Integer atendimentoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.add(Restrictions.in(AghAtendimentos.Fields.UNF_SEQ.toString(), unidadesFuncionaisSeq));
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atendimentoSeq));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * #44179 - CURSOR P01 cur_atd
	 * @author marcelo.deus
	 * return UNF_SEQ
	 */
	public Short obterUnfSeqPorAtendimentoSeq(Integer pAtdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.setProjection(Projections.property("ATD."+AghAtendimentos.Fields.UNF_SEQ.toString()));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), pAtdSeq));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44179 - CURSOR cur_atd
	 * @author thiago.cortes
	 * return Origem
	 */
	public String obterOrigemPorSeq(Integer cAtdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.setProjection(Projections.property("ATD."+AghAtendimentos.Fields.ORIGEM.toString()));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), cAtdSeq));
		
		DominioOrigemAtendimento origem = (DominioOrigemAtendimento) executeCriteriaUniqueResult(criteria);
		
		if(origem != null){
			return origem.getDescricao();
		} else {
			return null;
		}
	}	
	
	/**
	 * #44179 - F02 CURSOR cur_tei
	 * @author thiago.cortes
	 * return ATD_SEQ
	 */
	public CurTeiVO curTei(Long trgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.CONSULTA.toString(),"CON");
		criteria.createAlias("CON."+AacConsultas.Fields.MAM_TRG_ENC_INTERNOS.toString(),"TEI");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.SEQ_P.toString()),CurTeiVO.Fields.SEQ_P.toString())
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()),CurTeiVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("ATD."+AghAtendimentos.Fields.SEQ.toString()),CurTeiVO.Fields.ATD_SEQ.toString()));
		
		if(trgSeq != null){
			criteria.add(Restrictions.eq("TEI."+MamTrgEncInterno.Fields.TRG_SEQ.toString(), trgSeq));
		}
		criteria.add(Restrictions.isNull("TEI."+MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.addOrder(Order.desc("TEI."+MamTrgEncInterno.Fields.SEQ_P.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CurTeiVO.class));
		return (CurTeiVO) executeCriteriaUniqueResult(criteria);
	}
	/**#44179
	 * MAMC_EMG_GET_UNF.cur_tei
	 * @param trgSeq
	 * @return unf_seq
	 */
	public CurTeiVO obterUnidadePorTriagem(Long trgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON."+AacConsultas.Fields.TRG_ENC_INTERNO.toString(),"TEI");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.SEQ_P.toString()),CurTeiVO.Fields.SEQ_P.toString())
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()),CurTeiVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("ATD."+AghAtendimentos.Fields.UNF_SEQ.toString()),CurTeiVO.Fields.UNF_SEQ.toString()));
		
		if(trgSeq != null){
			criteria.add(Restrictions.eq("TEI."+MamTrgEncInterno.Fields.TRG_SEQ.toString(), trgSeq));
		}
		criteria.add(Restrictions.isNull("TEI."+MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.add(Restrictions.eqProperty("CON."+AacConsultas.Fields.NUMERO.toString(), "TEI."+MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()));	
		criteria.add(Restrictions.eqProperty("ATD."+AghAtendimentos.Fields.CON_NUMERO.toString(), "CON."+AacConsultas.Fields.NUMERO.toString()));
		
		criteria.addOrder(Order.desc("TEI."+MamTrgEncInterno.Fields.SEQ_P.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CurTeiVO.class));
		return (CurTeiVO) executeCriteriaUniqueResult(criteria);
	}
	
	public AghAtendimentos obterAghAtendimentoParaSolicitacaoConsultoria(Integer chavePrimaria){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias("ESP."+AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "ESP2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), chavePrimaria));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44682 C3
	 * @param codigoPaciente
	 * @return
	 */
	public QuantidadeAparelhoAuditivoVO verificarQuantidadeAparelhosAuditivos(Integer codigoPaciente){
		DetachedCriteria criteriaProteses = DetachedCriteria.forClass(AacProteseAuditiva.class);
		DetachedCriteria subCritAtendimentos = DetachedCriteria.forClass(AghAtendimentos.class);
		if(codigoPaciente != null){
			subCritAtendimentos.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		}
		subCritAtendimentos.setProjection(Projections.projectionList().add(Projections.property(AghAtendimentos.Fields.SEQ.toString())));
		
		criteriaProteses.add(Restrictions.ilike(AacProteseAuditiva.Fields.SITUACAO.toString(), "r", MatchMode.EXACT));
		criteriaProteses.add(Subqueries.propertyIn(AacProteseAuditiva.Fields.ATD_SEQ.toString(), subCritAtendimentos));
		
		criteriaProteses.setProjection(Projections.projectionList().add(Projections.rowCount(), QuantidadeAparelhoAuditivoVO.Fields.QUANTIDADE.toString()));	
		
		criteriaProteses.setResultTransformer(Transformers.aliasToBean(QuantidadeAparelhoAuditivoVO.class));
		
		return (QuantidadeAparelhoAuditivoVO) executeCriteriaUniqueResult(criteriaProteses);
	}
	
	
	/**
	 * @ORADB P_CHAMA_ATESTADOS - CURSOR c_atd
	 */
	public Integer obterParametroProcedure(Integer codConsulta){
		AghAtendimentosParametroVOQueryBuilder builder = new AghAtendimentosParametroVOQueryBuilder();
		DetachedCriteria criteria = builder.build(codConsulta);
		
		if (criteria == null) {
            return null;
		}
		
		return  (Integer) executeCriteriaUniqueResult(criteria);
	}
        
        public AtendimentoSolicExameVO obterAtendimentoSolicExameVO(Integer atdSeq) {
            	StringBuilder sql = new StringBuilder(6500);
            
            	sql.append(" SELECT DISTINCT atd.seq as atdSeq, ")
            		.append("   pac.prontuario as prontuario,                                                   ")
            		.append("   pac.nome                                              AS nomePaciente,          ")
            		.append("   TRUNC((months_between(sysdate,pac.dt_nascimento))/12) AS idade ,                ")
            		.append("   atd.dthr_inicio                                       AS dataAtendimento,       ")
            		.append("   esp.nome_especialidade as nomeEspecialidade,                                    ")
            		.append("   atd.origem as origem,                                                           ")
            		.append("   CASE                                                                            ")
            		.append("     WHEN atd.lto_lto_id IS NOT NULL                                               ")
            		.append("     THEN concat('L:', atd.lto_lto_id)                                             ")
            		.append("     WHEN atd.qrt_numero IS NOT NULL                                               ")
            		.append("     THEN concat('Q:',atd.qrt_numero)                                              ")
            		.append("     ELSE 'U:'                                                                     ")
            		.append("       ||unf.andar                                                                 ")
            		.append("       || ' '                                                                      ")
            		.append("       ||unf.ind_ala                                                               ")
            		.append("       || ' - '                                                                    ")
            		.append("       || unf.descricao                                                            ")
            		.append("   END            AS localDescricao,                                               ")
            		.append("   atd.lto_lto_id AS leito,                                                        ")
            		.append("   pac.codigo     AS codPaciente,                                                  ")
            		.append("   CASE                                                                            ")
            		.append("     WHEN (conv.grupo_convenio = 'S')                                              ")
            		.append("     THEN 'true'                                                                   ")
            		.append("     ELSE 'false'                                                                  ")
            		.append("   END         AS isSus,                                                           ")
            		.append("   CASE                                                                            ")
            		.append("     WHEN (conv.grupo_convenio = 'S'                                               ")
            		.append("     AND atd.origem            = 'X')                                              ")
            		.append("     THEN 'true'                                                                   ")
            		.append("     ELSE 'false'                                                                  ")
            		.append("   END         AS indTransplantado,                                                ")
            		.append("   con.numero  AS numeroConsulta,                                                  ")
            		.append("   atd.unf_seq AS unfSeq,                                                          ")
            		.append("   CASE                                                                            ")
            		.append("     WHEN (grd.PRE_SER_MATRICULA IS NOT NULL                                       ")
            		.append("     AND grd.PRE_SER_VIN_CODIGO  IS NOT NULL )                                     ")
            		.append("     THEN (grd.PRE_SER_MATRICULA                                                   ")
            		.append("       || '-'                                                                      ")
            		.append("       || grd.PRE_SER_VIN_CODIGO )                                                 ")
            		.append("     ELSE                                                                          ")
            		.append("       (SELECT eqp.SER_MATRICULA                                                   ")
            		.append("         || '-'                                                                    ")
            		.append("         || eqp.SER_VIN_CODIGO                                                     ")
            		.append("       FROM agh_equipes eqp                                                        ")
            		.append("       WHERE eqp.seq = grd.eqp_seq                                                 ")
            		.append("       )                                                                           ")
            		.append("   END AS responsavel                                                              ")
            		.append(" FROM agh_atendimentos atd                                                         ")
            		.append(" INNER JOIN aip_pacientes pac                                                      ")
            		.append(" ON (pac.codigo = atd.pac_codigo)                                                  ")
            		.append(" LEFT OUTER JOIN ain_quartos qua                                                   ")
            		.append(" ON (qua.numero = atd.qrt_numero)                                                  ")
            		.append(" LEFT OUTER JOIN ain_leitos lto                                                    ")
            		.append(" ON (lto.lto_id = atd.lto_lto_id)                                                  ")
            		.append(" LEFT OUTER JOIN agh_especialidades esp                                            ")
            		.append(" ON(esp.seq = atd.esp_seq)                                                         ")
            		.append(" INNER JOIN agh_unidades_funcionais unf                                            ")
            		.append(" ON (unf.seq = atd.unf_seq)                                                        ")
            		.append(" INNER JOIN FCC_CENTRO_CUSTOS cct                                                  ")
            		.append(" ON (unf.cct_codigo = cct.codigo)                                                  ")
            		.append(" LEFT OUTER JOIN agh_caract_unid_funcionais cuf                                    ")
            		.append(" ON (unf.seq = cuf.unf_seq)                                                        ")
            		.append(" LEFT OUTER JOIN AIN_INTERNACOES INT                                               ")
            		.append(" ON (atd.int_seq = int.seq)                                                        ")
            		.append(" LEFT OUTER JOIN aac_consultas con                                                 ")
            		.append(" ON (con.numero = atd.con_numero)                                                  ")
            		.append(" LEFT OUTER JOIN mam_controles ctrl                                                ")
            		.append(" ON (ctrl.con_numero = con.numero)                                                 ")
            		.append(" LEFT OUTER JOIN fat_convenios_saude conv                                          ")
            		.append(" ON ( (atd.origem                      <> 'N'                                      ")
            		.append(" AND AELC_GET_CONVENIO(atd.seq)         = conv.codigo)                             ")
            		.append(" OR (atd.origem                         = 'N'                                      ")
            		.append(" AND AELC_GET_CONVENIO(atd.atd_seq_mae) = conv.codigo) )                           ")
            		.append(" LEFT OUTER JOIN AAC_GRADE_AGENDAMEN_CONSULTAS grd                                 ")
            		.append(" ON (grd.seq   = con.grd_seq)                                                      ")
            		.append(" WHERE atd.seq = :atendimentoSeq                                                   ");
            
            	final SQLQuery query = createSQLQuery(sql.toString());
             
            	query.setInteger("atendimentoSeq", atdSeq);
            	query.setResultTransformer(Transformers.aliasToBean(AtendimentoSolicExameVO.class));
            	
		query.addScalar("atdSeq", IntegerType.INSTANCE);
		query.addScalar("prontuario", IntegerType.INSTANCE);
		query.addScalar("codPaciente", IntegerType.INSTANCE);
		query.addScalar("nomePaciente", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("idade", IntegerType.INSTANCE);
		query.addScalar("dataAtendimento", org.hibernate.type.TimestampType.INSTANCE);
		query.addScalar("nomeEspecialidade", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("origem", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("localDescricao", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("leito", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("indTransplantado", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("numeroConsulta", IntegerType.INSTANCE);
		query.addScalar("unfSeq", org.hibernate.type.ShortType.INSTANCE);
		query.addScalar("responsavel", org.hibernate.type.StringType.INSTANCE);
		query.addScalar("isSus", org.hibernate.type.StringType.INSTANCE);
	
		AtendimentoSolicExameVO result = (AtendimentoSolicExameVO) query.uniqueResult();
            	return result;
        }

	public Integer obterUltimaConsultaDaPaciente(Integer pacCodigo, List<ConstanteAghCaractUnidFuncionais> caracts) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CAR");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.max("ATD."+AghAtendimentos.Fields.CON_NUMERO.toString())));
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.in("CAR."+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracts));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * C3 estoria 5302
	 * @param leitoID
	 * @return
	 */
	public AghAtendimentos obterAghAtendimentosPorLeitoID(String leitoID,boolean filtarIndPacAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		if(filtarIndPacAtendimento){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
			criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		}
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoID));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	

	/**
	 * C4 estoria 5302
	 * @param leitoID
	 * @return
	 */
	public AghAtendimentos obterAghAtendimentosPorProntuario(Integer prontuario,Date parametroDataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LEI");
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		DominioOrigemAtendimento[] origens =  new DominioOrigemAtendimento[2];
		origens[0]= DominioOrigemAtendimento.U;
		origens[1]= DominioOrigemAtendimento.I;
		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), origens));
		criteria.add(Restrictions.ge(AghAtendimentos.Fields.DTHR_FIM.toString(), parametroDataFim));
		criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
		
	/** #41787 C4
	 * Consulta para obter a conta do paciente
	 * @param pacCodigo
	 * @return
	 */
	public List<FatContasHospitalares> obterContaPaciente(final Integer pacCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CHO."+FatContasHospitalares.Fields.SEQ.toString()).as(FatContasHospitalares.Fields.SEQ.toString()))
				);

		criteria.createAlias("ATD."+AghAtendimentos.Fields.INTERNACAO.toString(), "AIN");
		criteria.createAlias("AIN."+AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "CTI");
		criteria.createAlias("CTI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "CHO");
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("CHO."+FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatContasHospitalares.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * @param pacCodigo
	 * @param dthrInicio
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPorPacienteDataInicioOrigem(final Integer pacCodigo, final Date dthrInicio, DominioOrigemAtendimento ... dominioOrigemAtendimento) {
		final DetachedCriteria criteria = obterCriteriaAtendimentoPorPacienteDataInicioOrigem(pacCodigo, dthrInicio, dominioOrigemAtendimento);

		List<AghAtendimentos> result = super.executeCriteria(criteria, 0, 1, null, false);

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		
		return null;
	}
	
	private DetachedCriteria obterCriteriaAtendimentoPorPacienteDataInicioOrigem(final Integer pacCodigo, final Date dthrInicio, DominioOrigemAtendimento ... dominioOrigemAtendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString(), dthrInicio));

		DominioOrigemAtendimento[] origem = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.N, DominioOrigemAtendimento.I };
		criteria.add(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origem));
		return criteria;
	}
	
        
	/**
         * #41792
         * @return
         *  
         */
        public Timestamp consultarDataUltimoAtendimentoUnion(Integer prontuario){
        	StringBuilder sql = new StringBuilder(6500);
        	
        	sql.append("SELECT  MAX(DT_ULT_ATEND) FROM ");
        	if(isOracle()){
        		sql.append("(SELECT MAX(NVL(ATD.DTHR_FIM, ATD.DTHR_INICIO)) DT_ULT_ATEND ");
        	}else{
        		sql.append("(SELECT MAX(CASE WHEN ATD.DTHR_FIM is null THEN ATD.DTHR_INICIO END) as DT_ULT_ATEND ");
        	}
        	sql.append("FROM AGH_ATENDIMENTOS ATD, ");
        	sql.append("MAM_CONTROLES    CTL ");
        	sql.append("WHERE ATD.PRONTUARIO = :prontuario ");
        	sql.append("AND CTL.CON_NUMERO = ATD.CON_NUMERO ");
        	sql.append("UNION ");
        	
        	if(isOracle()){
        		sql.append("SELECT MAX(NVL(ATD.DTHR_FIM, ATD.DTHR_INICIO)) DT_ULT_ATEND ");
        	}else{
        		sql.append("SELECT MAX(CASE WHEN ATD.DTHR_FIM is null THEN ATD.DTHR_INICIO END) as DT_ULT_ATEND ");
        	}
        	sql.append("FROM AGH_ATENDIMENTOS ATD ");
        	sql.append("WHERE ATD.PRONTUARIO = :prontuario ");
        	sql.append("AND ORIGEM <> 'X' ");
        	sql.append("AND CON_NUMERO IS NULL ");
        	sql.append("UNION "); 
        	if(isOracle()){
        		sql.append("SELECT MAX(NVL(ATD.DTHR_FIM, SYSDATE)) DT_ULT_ATEND ");
        	}else{
        		sql.append("SELECT MAX(CASE WHEN ATD.DTHR_FIM is null THEN SYSDATE END) as DT_ULT_ATEND ");
        	}
        	sql.append("FROM AGH_ATENDIMENTOS ATD ");
       		sql.append("WHERE ATD.PRONTUARIO = :prontuario ");
        	sql.append("AND ORIGEM = 'I' ");
       		sql.append("UNION ");
        	sql.append("SELECT MAX(EIS.DTHR_EVENTO) ");
        	sql.append("FROM AEL_SOLICITACAO_EXAMES SOE, ");
        	sql.append("AGH_ATENDIMENTOS ATD, ");
        	sql.append("AEL_ITEM_SOLICITACAO_EXAMES ISE, ");
        	sql.append("AEL_EXTRATO_ITEM_SOLICS EIS ");    
        	sql.append("WHERE SOE.ATD_SEQ = ATD.SEQ ");
        	sql.append("AND ATD.PRONTUARIO = :prontuario ");
        	sql.append("AND ORIGEM = 'X' ");
        	sql.append("AND SOE.SEQ = ISE.SOE_SEQ ");
        	sql.append("AND EIS.ISE_SEQP = ISE.SEQP ");
        	sql.append("AND EIS.ISE_SOE_SEQ = ISE.SOE_SEQ ");
        	sql.append("AND EIS.SIT_CODIGO IN ('AE', 'RE') )");

        	Query q = createSQLQuery(sql.toString());
        	q.setInteger("prontuario",prontuario);
        	
        	return (Timestamp) q.uniqueResult();
        }
        
        
        
       

	
	/**
	 * C6 estoria 5302
	 * @param prontuario
	 * @return
	 */
	public List<AghAtendimentosVO> pesquisarAghAtendimentosPorProntuario(Integer prontuario,String leitoID,Date parametroDataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"AGH");
		criteria.createAlias("AGH."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("AGH."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("AGH."+AghAtendimentos.Fields.LEITO.toString(), "LEI",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AGH."+AghAtendimentos.Fields.QUARTO.toString(), "QRTO",JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("AGH."+AghAtendimentos.Fields.SEQ.toString()),AghAtendimentosVO.Fields.SEQ.toString());
		projection.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()),AghAtendimentosVO.Fields.NOME_PACIENTE.toString());
		projection.add(Projections.property("PAC."+AipPacientes.Fields.CODIGO.toString()),AghAtendimentosVO.Fields.CODIGO.toString());
		projection.add(Projections.property("AGH."+AghAtendimentos.Fields.PRONTUARIO.toString()),AghAtendimentosVO.Fields.PRONTUARIO.toString());
		
		projection.add(Projections.property("QRTO."+AinQuartos.Fields.NUMERO.toString()),AghAtendimentosVO.Fields.QRT_NUMERO.toString());
		projection.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.ANDAR.toString()),AghAtendimentosVO.Fields.AGH_UNIDADES_FUNCIONAIS_ANDAR.toString());
		projection.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()),AghAtendimentosVO.Fields.AGH_UNIDADES_FUNCIONAIS_DESCRICAO.toString());
		projection.add(Projections.property("AGH."+AghAtendimentos.Fields.DTHR_FIM.toString()),AghAtendimentosVO.Fields.DTHR_FIM.toString());
		projection.add(Projections.property("AGH."+AghAtendimentos.Fields.DTHR_INICIO.toString()),AghAtendimentosVO.Fields.DTHR_INICIO.toString());
		projection.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.IND_ALA.toString()),AghAtendimentosVO.Fields.AGH_UNIDADES_FUNCIONAIS_IND_ALA.toString());
		projection.add(Projections.property("LEI."+AinLeitos.Fields.LTO_ID.toString()),AghAtendimentosVO.Fields.LTO_LTO_ID.toString());
		
		
		DominioOrigemAtendimento[] origens =  new DominioOrigemAtendimento[2];
		origens[0]= DominioOrigemAtendimento.U;
		origens[1]= DominioOrigemAtendimento.I;
		criteria.add(Restrictions.in("AGH."+AghAtendimentos.Fields.ORIGEM.toString(), origens));
	
		
		criteria.add(Restrictions.or(Restrictions.eq("AGH."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S),
				Restrictions.ge("AGH."+AghAtendimentos.Fields.DTHR_FIM, parametroDataFim)));		
	
		if(prontuario != null){
			criteria.add(Restrictions.eq("AGH."+AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		}
		
		if(leitoID != null){
			criteria.add(Restrictions.eq("AGH."+AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoID));
		}
		
		criteria.addOrder(Order.desc("AGH."+AghAtendimentos.Fields.DTHR_INICIO.toString()));
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(AghAtendimentosVO.class));
		return  executeCriteria(criteria);
	}
	
	
	

	/**
	 * Obtém informações de Atendimento relacionado a uma Justificativa de Uso a partir do Id do Atendimento.
	 * @ORADB CGFK$QRY_JUM_MPM_JUM_ATD_FK1
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Informações de Atendimento
	 */
	public AghAtendimentos obterAtendimentoJustificativaUsoPorId(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		criteria.createAlias("ATD." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ESP." + AghEspecialidades.Fields.CLINICA.toString(), "CLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.V_UNIDADE_FUNCIONAL.toString(), "VUF", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		List<AghAtendimentos> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}
	
	
	public List<AghAtendimentos> pesquisarPacientesInternados(final AipPacientes paciente, final AinQuartos quarto,
			final AinLeitos leito, final AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria criteria = criteriaBaseAtendimentoVigente();

		// TODO incluir criterios para pesquisa fonetica
		return executeCriteria(criteria);
	}
	
	/**
	 * #43089 - C1
	 * @param conNumero
	 * @param tptSeqFis
	 * @return AtendimentoPrescricaoPacienteVO
	 */
	public AtendimentoPrescricaoPacienteVO obterSequenciaisAtendimentoPrescricaoPaciente(Integer conNumero, Integer tptSeqFis) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		criteria.createAlias(AghAtendimentos.Fields.MPT_PRESCRICAO_PACIENTES.toString(), "PTE");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()), AtendimentoPrescricaoPacienteVO.Fields.SEQ_ATD.toString());
		projList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString()), AtendimentoPrescricaoPacienteVO.Fields.SEQ_PTE.toString());
		projList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.PRESCRICAO_IMPRESSAO.toString()), AtendimentoPrescricaoPacienteVO.Fields.IND_PRCR_IMPRESSAO.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNotNull("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()));
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.TIPO_TRATAMENTO.toString(), tptSeqFis));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AtendimentoPrescricaoPacienteVO.class));
		
		return (AtendimentoPrescricaoPacienteVO) executeCriteriaUniqueResult(criteria);
		
	}
	
	public List<TransferirExamesVO> obterDeConsulta(Integer numeroConsulta, Integer codigoPaciente, boolean paraConsulta){

		StringBuilder sqlNotExists = new StringBuilder(500);
		sqlNotExists.append(" NOT EXISTS ( SELECT 1 FROM FAT_PROCED_AMB_REALIZADOS WHERE PRH_CON_NUMERO = THIS_.CON_NUMERO AND IND_SITUACAO IN ('E', 'P') ");
		sqlNotExists.append(" UNION ");
		sqlNotExists.append(" SELECT 1 FROM FAT_ITENS_CONTA_APAC WHERE PRH_CON_NUMERO = THIS_.CON_NUMERO AND IND_SITUACAO IN ('E', 'P') ) ");
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "A");
		
		criteria.createAlias("A." + AghAtendimentos.Fields.PACIENTE.toString(), "P", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList()
				.add(Projections.property("A." + AghAtendimentos.Fields.SEQ.toString()), TransferirExamesVO.Fields.SEQ.toString())
				.add(Projections.property("A." + AghAtendimentos.Fields.CON_NUMERO.toString()), TransferirExamesVO.Fields.CONSULTA_NUMERO.toString())
				.add(Projections.property("A." + AghAtendimentos.Fields.PAC_CODIGO.toString()), TransferirExamesVO.Fields.CODIGO_PACIENTE.toString())
				.add(Projections.property("P." + AipPacientes.Fields.PRONTUARIO.toString()), TransferirExamesVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("P." + AipPacientes.Fields.NOME.toString()), TransferirExamesVO.Fields.NOME_PACIENTE.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.isNull("A." + AghAtendimentos.Fields.ATU_SEQ.toString()))
				.add(Restrictions.isNull("A." + AghAtendimentos.Fields.INT_SEQ.toString()))
				.add(Restrictions.sqlRestriction(sqlNotExists.toString()));
		
		if (numeroConsulta != null) {
			criteria.add(Restrictions.eq("A." + AghAtendimentos.Fields.CON_NUMERO.toString(), numeroConsulta));
		}
		
		if (paraConsulta) {
			
			criteria.add(Restrictions.gt("A." + AghAtendimentos.Fields.CON_NUMERO.toString(), 0));
			
			if (codigoPaciente != null) {
				criteria.add(Restrictions.eq("A." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
			}
			criteria.addOrder(Order.asc("P." + AipPacientes.Fields.NOME)).addOrder(Order.desc("A." + AghAtendimentos.Fields.CON_NUMERO));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(TransferirExamesVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Integer obterUltimaConsultaGestantePorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CAR");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.COD_PACIENTE.toString(), pacCodigo));

		criteria.add(Restrictions.in("CAR." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
			new ConstanteAghCaractUnidFuncionais[]{ConstanteAghCaractUnidFuncionais.CO, ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA}));
		
		criteria.setProjection(Projections.max("ATD." + AghAtendimentos.Fields.CON_NUMERO.toString()));
		return (Integer) super.executeCriteriaUniqueResult(criteria);
	}
	
	/**C1
	 * Obtém informações de Atendimento relacionado a um prontuario.
	 * @param prontuario - Código do prontuario
	 * @return Ultimo seq do Atendimento
	 */
	public Integer obterAghAtendimentoPorProntuario(Integer prontuario) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.isNotNull("ATD." + AghAtendimentos.Fields.INTSEQ.toString()));
		
		
		criteria.setProjection(Projections.property("ATD." + AghAtendimentos.Fields.INTSEQ.toString()));
		
		criteria.addOrder(Order.desc("ATD." + AghAtendimentos.Fields.SEQ));

		return (Integer) executeCriteria(criteria).get(0);
	}
	

	@SuppressWarnings("unchecked")
	public List<ReceitasGeralEspecialVO> gerarDados(Integer pacCodigo){
		
		StringBuilder query = new StringBuilder(1044);
		
		query.append("select")
			.append("  atd.seq as ").append(ReceitasGeralEspecialVO.Fields.ATD_SEQ.toString())
			.append(" ,atd.paciente.codigo as ").append(ReceitasGeralEspecialVO.Fields.PAC_CODIGO.toString())
			.append(" ,atd.prontuario as ").append(ReceitasGeralEspecialVO.Fields.PRONTUARIO.toString())
			.append(" ,atd.dthrInicio as ").append(ReceitasGeralEspecialVO.Fields.DATA_HORA_CRIACAO.toString())
			.append(" ,atd.consulta.numero as ").append(ReceitasGeralEspecialVO.Fields.CON_NUMERO.toString())
			.append(" ,COALESCE(esp_mae.nomeReduzido, esp.nomeReduzido) as ").append(ReceitasGeralEspecialVO.Fields.NOME_REDUZIDO.toString())
			.append(" ,COALESCE(esp_mae.nomeEspecialidade, esp.nomeEspecialidade) as ").append(ReceitasGeralEspecialVO.Fields.NOME_ESPECIALIDADE.toString())
			.append(" ,COALESCE(grd.profEspecialidade.id.serVinCodigo, eqp.profissionalResponsavel.id.vinCodigo) as ").append(ReceitasGeralEspecialVO.Fields.VINCULO.toString())
			.append(" ,COALESCE(grd.profEspecialidade.id.serMatricula, eqp.profissionalResponsavel.id.matricula) as ").append(ReceitasGeralEspecialVO.Fields.MATRICULA.toString())
			.append(" ,to_char(atd.dthrInicio, 'yyyymmdd') as ").append(ReceitasGeralEspecialVO.Fields.DATA_ORD.toString())
			.append(" ,'1' as ").append(ReceitasGeralEspecialVO.Fields.TIPO.toString())
			.append(" , COALESCE(esp.especialidade.seq, esp.seq)  as ").append(ReceitasGeralEspecialVO.Fields.ESP_SEQ.toString())
			.append(" from AghAtendimentos as atd")
			.append(" inner join atd.consulta as con")
			.append(" inner join atd.especialidade as esp")
			.append(" inner join con.retorno as ret")
			.append(" inner join con.gradeAgendamenConsulta as grd")
			.append(" inner join grd.equipe as eqp")
			.append(" left join esp.especialidade as esp_mae")
			.append(" where")
			.append(" con.paciente.codigo = :pacCodigo")
			.append(" and ret.absenteismo = 'R'")
			.append(" and trunc(atd.dthrInicio) <= :dataAtual")
			.append(" and atd.intSeq is null")
			.append(" and atd.atendimentoPacienteExterno.seq is null")
			.append(" and exists")
			.append(" (")
			.append(" select rec.conNumero")
			.append(" from VMamReceitas rec")
			.append(" where rec.conNumero = con.numero")
			.append(" and rec.tipo in ('G','E'))");
		
		Query consultaTipo1 = createHibernateQuery(query.toString());
		consultaTipo1.setInteger("pacCodigo", pacCodigo);
		consultaTipo1.setDate("dataAtual", new Date());
		consultaTipo1.setResultTransformer(Transformers.aliasToBean(ReceitasGeralEspecialVO.class));		
		
		Set<ReceitasGeralEspecialVO> retornoTipo1 = new HashSet<ReceitasGeralEspecialVO>(consultaTipo1.list());
		
		query = new StringBuilder(800);
		
		query.append("select ")
			.append(" atd.seq as ").append(ReceitasGeralEspecialVO.Fields.ATD_SEQ.toString())
			.append(" ,atd.paciente.codigo as ").append(ReceitasGeralEspecialVO.Fields.PAC_CODIGO.toString())
			.append(" ,atd.prontuario as ").append(ReceitasGeralEspecialVO.Fields.PRONTUARIO.toString())
			.append(" ,rct.dthrCriacao as ").append(ReceitasGeralEspecialVO.Fields.DATA_HORA_CRIACAO.toString())
			.append(" ,esp.nomeReduzido as ").append(ReceitasGeralEspecialVO.Fields.NOME_REDUZIDO.toString())
			.append(" ,esp.nomeEspecialidade as ").append(ReceitasGeralEspecialVO.Fields.NOME_ESPECIALIDADE.toString())
			.append(" ,atd.servidor.id.vinCodigo as ").append(ReceitasGeralEspecialVO.Fields.VINCULO.toString())
			.append(" ,atd.servidor.id.matricula as ").append(ReceitasGeralEspecialVO.Fields.MATRICULA.toString())
			.append(" ,to_char(rct.dthrCriacao, 'yyyymmdd') as ").append(ReceitasGeralEspecialVO.Fields.DATA_ORD.toString())
			.append(" ,'2' as ").append(ReceitasGeralEspecialVO.Fields.TIPO.toString())
			.append(" ,esp.seq as ").append(ReceitasGeralEspecialVO.Fields.ESP_SEQ.toString())
			.append(" from ")
			.append("  MamReceituarios as rct,")
			.append("  AghAtendimentos as atd ")
			.append("  inner join atd.especialidade as esp ")
			.append(" where atd.paciente.codigo = :pacCodigo")
			.append(" and rct.paciente.codigo = atd.paciente.codigo")
			.append(" and (rct.atendimentos.seq = atd.seq")
			.append(" or rct.mpmAltaSumario.id.apaAtdSeq = atd.seq)")
			.append(" and esp.seq = atd.especialidade.seq");
		
		
		Query consultaTipo2 = createHibernateQuery(query.toString());
		consultaTipo2.setInteger("pacCodigo", pacCodigo);
		consultaTipo2.setResultTransformer(Transformers.aliasToBean(ReceitasGeralEspecialVO.class));		
		
		retornoTipo1.addAll(consultaTipo2.list());
		
		List<ReceitasGeralEspecialVO> listaRetorno = new ArrayList<ReceitasGeralEspecialVO>(retornoTipo1);
		
		return listaRetorno;
	}
	
	private DetachedCriteria criteriaLocalizarAtendimento() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		// ind_pac_atendimento = 'S'
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		// origem in ('I','N')
		criteria.add(Restrictions.in(
				AghAtendimentos.Fields.ORIGEM.toString(), 
				DominioOrigemAtendimento.getOrigensAtendimentoInternacaoNascimento()
				));

		return criteria;
	}
	
	public List<AghAtendimentos> localizarAtendimentoPorPaciente(Integer codigoPaciente) {
		final DetachedCriteria criteria = criteriaLocalizarAtendimento();
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obter atendimento de paciente internado com informação do leito
	 * #50931 - C1 e C3
	 * @param nroProntuario
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPacienteInternadoPorProntuario(Integer nroProntuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ate");
		criteria.createAlias("ate." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("ate." + AghAtendimentos.Fields.LEITO.toString(), "lei");
		
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));
		criteria.add(Restrictions.eq("ate." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in("ate." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.getOrigensAtendimentoInternacaoNascimento()));

		List<AghAtendimentos> listaRetorno = executeCriteria(criteria);
		
		if (!listaRetorno.isEmpty()){
			return listaRetorno.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Obter informação do serviço e médico de um atendimento
	 * #50931 - P1
	 * @param atdSeq
	 * @return
	 */
	public Object[] obterServicoMedicoDoAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ate");
		criteria.createAlias("ate." + AghAtendimentos.Fields.SERVIDOR.toString(), "serv");
		criteria.createAlias("ate." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "cct");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("cct."+FccCentroCustos.Fields.DESCRICAO.toString()))
				.add(Projections.property("serv."+RapServidores.Fields.MATRICULA.toString()))
				.add(Projections.property("serv."+RapServidores.Fields.CODIGO_VINCULO.toString())));
		
		
		criteria.add(Restrictions.eq("ate." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	public AghAtendimentos buscarLeitoPorUnidFuncional(AghAtendimentos atendimento, AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
	     	criteria.createAlias("ATD." + AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
	        criteria.createAlias("LTO." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
	        criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF2", JoinType.LEFT_OUTER_JOIN);
	        criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atendimento.getSeq()));
		        SimpleExpression r1 = Restrictions.eq("LTO." + AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unidadeFuncional.getSeq());
		        SimpleExpression r2 = Restrictions.eq("LTO." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A);
		        SimpleExpression r3 = Restrictions.eq("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional);
	        
		        criteria.add(Restrictions.or(Restrictions.and(r1, r2), r3));
		
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
  	
}