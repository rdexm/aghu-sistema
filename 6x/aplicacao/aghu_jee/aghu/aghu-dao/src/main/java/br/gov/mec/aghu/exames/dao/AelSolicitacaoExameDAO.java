package br.gov.mec.aghu.exames.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Table;
import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.TransferirExamesVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioAgrupamentoTotaisExames;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.contratualizacao.vo.TotalItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ResultadoExamePim2VO;
import br.gov.mec.aghu.exames.vo.PendenciaExecucaoVO;
import br.gov.mec.aghu.exames.vo.RelatorioAgendamentoProfissionalVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.exames.vo.SolicitacoesAgendaColetaAmbulatorioVO;
import br.gov.mec.aghu.faturamento.vo.AtendimentoCargaColetaVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelDoadorRedome;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExames;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelSolicAtends;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class AelSolicitacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSolicitacaoExames> {

	private static final long serialVersionUID = 3234004981556533026L;
	private final static String SOE = "soe.";
	private final static String AMO = "amo.";

	

	/**
	 * @HIST AelSolicitacaoExameDAO.obterPeloIdHist
	 * @param seq
	 * @return
	 */
	public AelSolicitacaoExames obterPeloId(Integer seq) {
		if (seq == null) {
			return null;
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), seq));
		
		criteria.createAlias(AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "ATD_PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.QUARTO.toString(), "ATD_QTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LEITO.toString(), "ATD_LEI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "ATD_MAE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), "SER_RESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_RESP." + RapServidores.Fields.PESSOA_FISICA.toString(), "SER_RESP_PES", JoinType.LEFT_OUTER_JOIN);

		return (AelSolicitacaoExames) executeCriteriaUniqueResult(criteria);
	}
	
	public AelSolicitacaoExamesHist obterPeloIdHist(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExamesHist.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExamesHist.Fields.SEQ.toString(), seq));
		return (AelSolicitacaoExamesHist) executeCriteriaUniqueResult(criteria);
	}

	public boolean verificarExistenciaSolicitacoesExameComRetornoPeloNumConsulta(
			Integer numero) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias(
				"ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias(
				"CON." + AacConsultas.Fields.RETORNO.toString(), "RET");

		criteria.add(Restrictions.eq(
				"CON." + AacConsultas.Fields.NUMERO.toString(), numero));

		return executeCriteriaExists(criteria);
	}

	/**
	 * Verifica a existência de Solicitação de Exame para a Consulta informada.
	 * 
	 * @param numero - Número da Consulta
	 * @return Flag indicando a existência do registro
	 */
	public boolean verificarExistenciaSolicitacaoExamePorNumConsulta(Integer numero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numero));
		
		return executeCriteriaExists(criteria);
	}

	public List<AelSolicitacaoExames> buscarAelSolicitacaoExames(String seq) {
		List<AelSolicitacaoExames> lista = new ArrayList<AelSolicitacaoExames>();
		if (seq == null || seq.equals("")) {

			DetachedCriteria criteria = DetachedCriteria
					.forClass(AelSolicitacaoExames.class);
			// Projeção da Criteria PRINCIPAL
			criteria.setProjection(Projections.distinct(Property
					.forName(AelSolicitacaoExames.Fields.SEQ.toString())));

			List<Integer> listaSeq = executeCriteria(criteria, 0, 50, null,
					true);
			for (Integer seq2 : listaSeq) {
				AelSolicitacaoExames sol = new AelSolicitacaoExames();
				sol.setSeq(seq2);
				lista.add(sol);
			}
		} else {
			AelSolicitacaoExames solicitacao = super
					.obterPorChavePrimaria(Integer.parseInt(seq.trim()));
			if (solicitacao != null) {
				lista.add(solicitacao);
			}
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<String> buscaInformacoesClinicas(Integer seqAtendimento,
			Integer phiSeq) {
		StringBuffer hql = new StringBuffer(250);
		hql.append(" select soe.");
		hql.append(AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString());
		hql.append(" from AelSolicitacaoExames soe, ");
		hql.append(" 	AelItemSolicitacaoExames ise, ");
		hql.append(" 	FatProcedHospInternos phi ");
		hql.append(" where soe.");
		hql.append(AelSolicitacaoExames.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and (phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :seq or phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ
				.toString());
		hql.append(" = :phiSeq) ");
		hql.append(" 	and soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString());
		hql.append(" = ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ
				.toString());
		hql.append(" 	and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" 	and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString());
		hql.append(" order by soe.");
		hql.append(AelSolicitacaoExames.Fields.CRIADO_EM.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("seq", phiSeq);
		query.setParameter("phiSeq", phiSeq);

		return query.list();
	}

	/**
	 * Retorna Itens do Exame relacionados com Detalhe da Solicitação.
	 * 
	 * @param seqSoe
	 *            id solicitação do exame
	 * @return List<SolicitacaoExameVO>
	 */
	public List<AelItemSolicitacaoExames> buscarItensExames(Integer seqSoe) {
		List<AelItemSolicitacaoExames> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(
				AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), seqSoe));
		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP
				.toString()));
		
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);

		list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Retorna Itens do Exame para a consulta de Avisos de Solicitações de
	 * Internação para Unidades Fechadas.
	 * 
	 * @param seqSoe
	 *            id solicitação do exame
	 * @return List<SolicitacaoExameVO>
	 */
	public List<AelItemSolicitacaoExames> buscarItensExamesAExecutar(
			Integer seqSoe, Short unfSeq, String sitCodigo) {
		List<AelItemSolicitacaoExames> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelItemSolicitacaoExames.class);

		criteria.createAlias(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "SIT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(
				AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), seqSoe));// numero
																				// da
																				// solicitacao
																				// exame
		criteria.add(Restrictions.eq(
				AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
				sitCodigo));// situacao da solicitacao exame

		// Sub query usada em AELP_DEFINE_WHERE
		StringBuffer sql = new StringBuffer(700);
		sql.append("	(UFE_EMA_EXA_SIGLA||ufe_ema_man_seq||ufe_unf_seq)   ");
		sql.append("			in ( 		");
		sql.append("				 select 	PUS.UFE_EMA_EXA_SIGLA||PUS.ufe_ema_man_seq||PUS.ufe_unf_seq      ");
		sql.append("				  from  agh.ael_permissao_unid_solics pus,			 ");
		sql.append("				        agh.ael_solicitacao_exames soe			     ");
		sql.append("				 where	pus.ufe_ema_exa_sigla = UFE_EMA_EXA_SIGLA ");
		sql.append("				   and	pus.ufe_ema_man_seq = UFE_EMA_MAN_SEQ 	  ");
		sql.append("				   and	pus.ufe_unf_seq = UFE_UNF_SEQ			  ");
		sql.append("				   and  soe.seq = soe_seq			      ");
		sql.append("				   and  pus.unf_seq = soe.unf_seq			      ");
		sql.append("				   and  pus.unf_seq_avisa	in (				  ");
		sql.append("												select unf.seq  ");
		sql.append("												from agh.agh_unidades_funcionais unf    ");
		sql.append("												where (unf.unf_seq = ? or seq = ?)  ");
		sql.append("												)  ");
		sql.append("				) 						   ");

		Object[] values = { unfSeq, unfSeq };
		Type[] types = { ShortType.INSTANCE, ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP
				.toString()));

		list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Retorna Amostras dos Itens do Exame relacionados com Detalhe da
	 * Solicitação.
	 * 
	 * @param seqSoe
	 *            id solicitação do exame
	 * @return List<SolicitacaoExameVO>
	 */
	public List<AelAmostraItemExames> buscarAmostrasItemExame(Integer soeSeq,
			Short itemSeq) {
		List<AelAmostraItemExames> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelAmostraItemExames.class);

		criteria.add(Restrictions.eq(
				AelAmostraItemExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(
				AelAmostraItemExames.Fields.SEQP.toString(), itemSeq));
		
		criteria.setFetchMode(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString()+"."+AelAmostras.Fields.MATERIAL_ANALISE.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString()+"."+AelAmostras.Fields.RECIPIENTE_COLETA.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString()+"."+AelAmostras.Fields.ANTICOAGULANTE.toString(), FetchMode.JOIN);
		

		list = super.executeCriteria(criteria);

		return list;
	}
					
	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorAtendimento(
			Integer seqAtendimento, List<String> parametros) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString());
		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString());

		criteria.add(Restrictions.eq(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + "."
						+ AghAtendimentos.Fields.SEQ.toString(),
				seqAtendimento));

		List<String> resticao = new ArrayList<String>();
		for (String parametro : parametros) {
			resticao.add(DominioSituacaoItemSolicitacaoExame.valueOf(parametro)
					.toString());
		}

		criteria.add(Restrictions.in(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString()
						+ "."
						+ AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO
								.toString(), resticao));

		return this.executeCriteria(criteria);
	}

	/**
	 * Busca todas as solicitações de exames para um atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */
	public List<AelSolicitacaoExames> buscarSolicitacaoExamesPorAtendimento(
			AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.add(Restrictions.eq(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), atendimento));

		return this.executeCriteria(criteria);

	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<AelItemSolicitacaoExames> pesquisarRelatorioTicketExamesPaciente(
			Integer codSolicitacao, boolean verificaIndImpTicketPaciente, Short unfSeq, Set<Short> listaUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		
		
		criteria.createAlias("ise."	+ AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE2");
		criteria.createAlias("UFE2."+ AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(),	"EMA");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(),	"EXA");
		// criteria.createAlias("UFE2."+AelUnfExecutaExames.Fields.CARACT_UNID_FUNCIONAIS.toString(),
		// "cun", JoinType.LEFT_OUTER_JOIN);
		// criteria.createAlias("cun."+AghCaractUnidFuncionais.Fields.UNIDADE_FUNCIONAL.toString(),"UNF",
		// JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soeVAS");
		criteria.createAlias("soeVAS." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atdVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("soeVAS." + AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO.toString(), "cnvVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("soeVAS." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(),"atvVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdVAS."+ AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(),"unfVAS", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias("soeVAS." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdVAS." + AghAtendimentos.Fields.PACIENTE.toString(),"pacVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.PACIENTE.toString(),	"atvPacVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.PROJETO_PESQUISA.toString(), "atvProjVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.CAD_CTRL_QUALIDADES.toString(), "atvCtrlVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.LABORATORIO_EXTERNO.toString(), "atvLabExtVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.DADOS_CADAVERES.toString(), "atvDadCadVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvVAS." + AelAtendimentoDiversos.Fields.CANDIDATOS_DOADORES.toString(), "atvCandVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UFE2." + AelUnfExecutaExames.Fields.UNF_SEQ_COMPARECE.toString(), "UFE2Comp");

		criteria.createAlias("atdVAS." + AghAtendimentos.Fields.INTERNACAO.toString(), "atdIntVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdIntVAS." + AinInternacao.Fields.PROJETO_PESQUISA.toString(), "atdIntProjVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdVAS." + AghAtendimentos.Fields.CONSULTA.toString(), "atdConsVAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdConsVAS." + AacConsultas.Fields.PROJETO_PESQUISA.toString(),"atdConsProjVAS", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("cnvVAS." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "cnvConvVAS", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("soeVAS." + AelSolicitacaoExames.Fields.SEQ.toString(), codSolicitacao));

		if (verificaIndImpTicketPaciente) {
			criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.IND_IMP_TICKET_PACIENTE.toString(), Boolean.TRUE));
		}

		// AND SUBSTR(ISE.SIT_CODIGO,1,2) not in ('PE','CA')
		criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.SIT_CODIGO,1,2) not in ('PE','CA') "));

		if (unfSeq != null){
			criteria.add(Restrictions.eq("ise."	+ AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));	
		} else if (listaUnfSeq !=null && !listaUnfSeq.isEmpty()){
			criteria.add(Restrictions.in("ise." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString() , listaUnfSeq));
		}		

		criteria.addOrder(Order.asc("soeVAS."+ AelSolicitacaoExames.Fields.SEQ.toString()));
		// criteria.addOrder(Order.asc("UFE2."+AelUnfExecutaExames.Fields.UNF_SEQ_COMPARECE.toString()));
		criteria.addOrder(Order.desc("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	/*public AelSolicitacaoExames obterOriginal(Integer seq) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(
				AelSolicitacaoExames.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(
				AelSolicitacaoExames.Fields.SERVIDOR.toString());
		hql.append(", o.").append(
				AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO.toString());
		hql.append(" from ").append(AelSolicitacaoExames.class.getSimpleName())
				.append(" o ");
		hql.append(" where o.")
				.append(AelSolicitacaoExames.Fields.SEQ.toString())
				.append(" = :entityId ");

		javax.persistence.Query query = this.createQuery(
				hql.toString());
		query.setParameter("entityId", seq);

		AelSolicitacaoExames retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();

		if (campos != null) {
			retorno = new AelSolicitacaoExames();

			retorno.setSeq(seq);
			retorno.setCriadoEm((Date) campos[0]);
			retorno.setServidor((RapServidores) campos[1]);
			retorno.setConvenioSaudePlano((FatConvenioSaudePlano) campos[2]);
		}

		return retorno;
	}*/

	/**
	 * 
	 * @param unidadeExecutora
	 * @param solicitacaoExame
	 * @param seqp
	 * @return
	 * @throws BaseException
	 */
	public Boolean existeVAelArcoSolicitacaoCarregarArquivoLaudoResultadoExame(
			AelSolicitacaoExames solicitacaoExame, Short seqp)
			throws BaseException {

		DetachedCriteria criteria1 = DetachedCriteria.forClass(
				AelSolicitacaoExames.class, SOE.substring(0, 3));
		criteria1.setProjection(Projections.property(SOE
				+ AelSolicitacaoExames.Fields.SEQ.toString()));
		criteria1.createAlias(
				AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(),
				"UNF", JoinType.INNER_JOIN);
		criteria1.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD",
				JoinType.INNER_JOIN);
		criteria1.createAlias(
				AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString(),
				"CNV", JoinType.INNER_JOIN);
		criteria1.add(Restrictions.eq(
				SOE + AelSolicitacaoExames.Fields.SEQ.toString(),
				solicitacaoExame.getSeq()));
		List<AelSolicitacaoExames> list1 = executeCriteria(criteria1);

		DetachedCriteria criteria2 = DetachedCriteria.forClass(
				AelSolicitacaoExames.class, SOE.substring(0, 3));
		criteria2.setProjection(Projections
				.property(AelSolicitacaoExames.Fields.SEQ.toString()));
		criteria2.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(),
				"ATV", JoinType.INNER_JOIN);
		criteria2.add(Restrictions.eq(
				SOE + AelSolicitacaoExames.Fields.SEQ.toString(),
				solicitacaoExame.getSeq()));
		List<AelSolicitacaoExames> list2 = executeCriteria(criteria2);

		// Unindo resultados
		list1.addAll(list2);

		return !list1.isEmpty();

	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public List<SolicitacaoColetarVO> buscaMateriaisColetarInternacao(
			RelatorioMateriaisColetarInternacaoFiltroVO filtro)
			throws ApplicationBusinessException {

		List<SolicitacaoColetarVO> lstRetorno = new ArrayList<SolicitacaoColetarVO>();
		SimpleDateFormat sdfQuery = null;		
		boolean isOracle = isOracle();		
		if(isOracle){
			sdfQuery = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")); 
		}else{
			sdfQuery = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));
		}

		StringBuffer sqlPrincipal = new StringBuffer(6866);
		sqlPrincipal
				.append("SELECT	distinct cast(unf9_.seq as varchar(5))||' - '||unf9_.DESCRICAO DESCRICAO,");
		sqlPrincipal.append("    	unf9_.ANDAR as y1_,");
		sqlPrincipal.append("    	unf9_.IND_ALA as y2_,");
		sqlPrincipal.append("    	case ");
		sqlPrincipal
				.append("    	when RECEM_NASCIDO = 'S' then 'Recem Nascido'");
		sqlPrincipal.append("    	end as recem_nascido,");
		sqlPrincipal
				.append("    	case 	when soe3_.USA_ANTIMICROBIANOS = 'S' then");
		sqlPrincipal
				.append("    				'** Paciente em uso de antimicrobianos **  ' || soe3_.INFORMACOES_CLINICAS");
		sqlPrincipal.append("    	else ");
		sqlPrincipal.append("    		soe3_.INFORMACOES_CLINICAS");
		sqlPrincipal.append("    	end as INFORMACOES_CLINICAS,");
		sqlPrincipal
				.append("    	case 	when atd4_.lto_lto_id is not null then   'L:'||atd4_.lto_lto_id");
		sqlPrincipal
				.append("    	    	when atd4_.qrt_numero is not null then   'Q:'||quarto_.descricao  ");
		sqlPrincipal.append("    	    	when atd4_.unf_seq is not null then ");
		sqlPrincipal
				.append("    	    			(	select	'U:'||unf.andar||' '||unf.ind_ala||' - '||unf.descricao");
		sqlPrincipal
				.append("    						from	agh.agh_unidades_funcionais unf    ");
		sqlPrincipal.append("    						where	unf.seq = atd4_.unf_seq   )  ");
		sqlPrincipal.append("    			else    ");
		sqlPrincipal
				.append("    				'Não é possível fornecer a localização do paciente. Quarto, leito e unidade não estão informados. Contate GSIS'");
		sqlPrincipal.append("    	end as lto_lto_id,");
		sqlPrincipal.append("    	coalesce(aip6_.prontuario,");
		sqlPrincipal.append("    	aip2x7_.prontuario) as prontuario,");
		sqlPrincipal.append("    	coalesce(aip6_.nome,aip2x7_.nome) as nome,");
		sqlPrincipal.append("    	cnv10_.DESCRICAO as y8_,");
		sqlPrincipal.append("    	soe3_.CRIADO_EM as y9_,");
		sqlPrincipal
				.append("    	coalesce(soe3_.atd_seq,soe3_.atv_seq) as soe_atd_seq,");
		sqlPrincipal.append("    	ise2_.SIT_CODIGO as y11_,");
		sqlPrincipal.append("    	ise2_.SEQP as y12_,");
		sqlPrincipal.append("    	ise2_.DTHR_PROGRAMADA as y13_,");
		sqlPrincipal.append("    	ise2_.DTHR_PROGRAMADA as y14_,");
		sqlPrincipal.append("    	amo1_.SOE_SEQ as y15_,");
		sqlPrincipal.append("    	amo1_.SEQP as y16_,");
		sqlPrincipal.append("    	case ");
		sqlPrincipal
				.append("    	    	when amo1_.unid_tempo_intervalo_coleta is not null then cast(amo1_.tempo_intervalo_coleta as varchar(6))||' '||amo1_.unid_tempo_intervalo_coleta");
		sqlPrincipal.append("    	end as tempo,");
		sqlPrincipal
				.append("    	case 	when ema17_.ind_jejum = 'S' then   'J'");
		sqlPrincipal.append("    	else   	case ");
		sqlPrincipal
				.append("    	    		when ema17_.ind_npo = 'S' then     'N'");
		sqlPrincipal.append("    				else   	case ");
		sqlPrincipal
				.append("    	    	    	    	when ema17_.ind_dieta_diferenciada = 'S' then      'D'");
		sqlPrincipal.append("    						end   ");
		sqlPrincipal.append("    			end ");
		sqlPrincipal.append("    	end as ind ,");
		sqlPrincipal.append("    	rco12_.DESCRICAO as y19_,");
		sqlPrincipal.append("    	atc13_.descricao as y20_,");
		sqlPrincipal.append("    	exa18_.SIGLA as y21_,");
		sqlPrincipal.append("    	exa18_.DESCRICAO_USUAL || (case");
		sqlPrincipal
				.append("    				when amo1_.unid_tempo_intervalo_coleta is not null then ");
		sqlPrincipal
				.append("						case	when amo1_.tempo_intervalo_coleta = 0 then ");
		sqlPrincipal
				.append("									' '||ico8_.tipo_substancia||' '||cast(ico8_.volume_ingerido as varchar(10))||' '||ico8_.unid_medida_volume");
		sqlPrincipal.append("    							else ");
		sqlPrincipal.append("									'' ");
		sqlPrincipal.append("    							end ");
		sqlPrincipal.append("    				else ");
		sqlPrincipal.append("    				'' ");
		sqlPrincipal.append("    	end) as descricao_usual,");
		sqlPrincipal.append("    	ise2_.UFE_UNF_SEQ as y23_,");
		sqlPrincipal.append("    	ise2_.TIPO_COLETA as y24_,");
		sqlPrincipal.append("    	equ14_.DRIVER_ID as y25_,");
		sqlPrincipal.append("    	'E' as tipo_registro,");
		sqlPrincipal.append("    	amo1_.NRO_UNICO as y27_,");
		sqlPrincipal.append("    	'0' as she_seq, ");
		sqlPrincipal.append("    	'1' as origem, ");
		sqlPrincipal.append("    	aip6_.dt_nascimento, ");
		sqlPrincipal.append("    	pfisica_.nome as pessoa_fisica, ");
		sqlPrincipal.append("    	quali_.nro_reg_conselho, "); 
		sqlPrincipal.append("    	rcp_.sigla ");

		sqlPrincipal.append("FROM	AGH.AEL_AMOSTRA_ITEM_EXAMES 				this_ ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_AMOSTRAS 					amo1_ 	on this_.AMO_SOE_SEQ	= amo1_.SOE_SEQ 	and this_.AMO_SEQP		= 	amo1_.SEQP ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_ITEM_SOLICITACAO_EXAMES 	ise2_ 	on this_.ise_seqp		= ise2_.SEQP 		and this_.ise_soe_seq	= 	ise2_.SOE_SEQ ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_SOLICITACAO_EXAMES 			soe3_ 	on ise2_.SOE_SEQ		= soe3_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN agh.ael_anticoagulantes 			atc13_ 	on amo1_.ATC_SEQ		= atc13_.seq ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AEL_MATERIAIS_ANALISES 		man15_ 	on amo1_.MAN_SEQ		= man15_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AEL_TIPOS_AMOSTRA_EXAMES 	tae16_ 	on man15_.SEQ			= tae16_.MAN_SEQ ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_EXAMES_MATERIAL_ANALISE  	ema17_ 	on tae16_.EMA_EXA_SIGLA	= ema17_.EXA_SIGLA 	and tae16_.EMA_MAN_SEQ	= 	ema17_.MAN_SEQ ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_EXAMES 			 			exa18_ 	on ema17_.EXA_SIGLA		= exa18_.SIGLA  ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_UNF_EXECUTA_EXAMES 			ufe19_ 	on ema17_.EXA_SIGLA		= ufe19_.EMA_EXA_SIGLA and ema17_.MAN_SEQ	=	ufe19_.EMA_MAN_SEQ ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AEL_RECIPIENTES_COLETA 			rco12_ 	on amo1_.RCO_SEQ		= rco12_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AEL_EQUIPAMENTOS 			equ14_ 	on this_.EQU_SEQ		= equ14_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN agh.ael_intervalo_coletas 		ico8_ 	on ise2_.ICO_SEQ		= ico8_.seq  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AGH_ATENDIMENTOS 			atd4_ 	on soe3_.ATD_SEQ		= atd4_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AIN_QUARTOS 			        quarto_ 	on atd4_.qrt_numero		= quarto_.NUMERO  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AIP_PACIENTES 				aip6_ 	on atd4_.PAC_CODIGO		= aip6_.CODIGO  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AEL_ATENDIMENTO_DIVERSOS 	atv5_ 	on soe3_.ATV_SEQ		= atv5_.SEQ  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AIP_PACIENTES 				aip2x7_ on atv5_.PAC_CODIGO		= aip2x7_.CODIGO  ");
		sqlPrincipal
				.append("INNER 	JOIN 	AGH.FAT_CONVENIOS_SAUDE 			cnv10_ 	on soe3_.csp_cnv_codigo	= cnv10_.CODIGO  ");
		sqlPrincipal
				.append("LEFT 	OUTER JOIN AGH.AGH_UNIDADES_FUNCIONAIS  	unf9_ 	on atd4_.UNF_SEQ		= unf9_.SEQ  ");
		sqlPrincipal
				.append("INNER 	JOIN	AGH.AGH_CARACT_UNID_FUNCIONAIS 		cun11_ 	on unf9_.SEQ			= cun11_.UNF_SEQ ");
		sqlPrincipal
				.append("LEFT  OUTER JOIN	AGH.RAP_SERVIDORES ser_ on soe3_.SER_MATRICULA = ser_.MATRICULA ");
		sqlPrincipal
				.append("LEFT  OUTER JOIN	AGH.RAP_PESSOAS_FISICAS pfisica_ on ser_.PES_CODIGO = pfisica_.CODIGO ");
		sqlPrincipal
				.append("LEFT  OUTER JOIN	AGH.RAP_QUALIFICACOES quali_ on pfisica_.CODIGO = quali_.PES_CODIGO ");
		sqlPrincipal
				.append("LEFT  OUTER JOIN   AGH.RAP_TIPOS_QUALIFICACAO rtq_ on rtq_.CODIGO = quali_.TQL_CODIGO ");
		sqlPrincipal
				.append("LEFT  OUTER JOIN   AGH.RAP_CONSELHOS_PROFISSIONAIS rcp_ on rcp_.CODIGO = rtq_.CPR_CODIGO ");
		
		sqlPrincipal.append("WHERE	atd4_.IND_PAC_ATENDIMENTO='S' ");

		if (filtro.getCaracteristica() != null) {
			sqlPrincipal.append("AND 	cun11_.CARACTERISTICA='"
					+ filtro.getCaracteristica().getCodigo() + "' ");
		}

		if (filtro.getIndExecutaPlantao() != null) {
			sqlPrincipal.append("AND ufe19_.IND_EXECUTA_EM_PLANTAO = '"
					+ filtro.getIndExecutaPlantao() + "' ");
		}

		if (filtro.getUnidadeFuncionalSolicitante() != null) {
			sqlPrincipal.append("AND unf9_.seq = "
					+ filtro.getUnidadeFuncionalSolicitante().getSeq() + " ");
		}

		if (filtro.getAelSolicitacaoExames() != null) {
			sqlPrincipal.append("AND soe3_.seq = "
					+ filtro.getAelSolicitacaoExames().getSeq() + " ");
		}
		
		sqlPrincipal.append(((isOracle)? "	and	    ise2_.dthr_programada between  to_date('"+ sdfQuery.format(filtro.getDataInicialPesquisa()) +"','dd/mm/yyyy')    and    to_date('"+sdfQuery.format(filtro.getDataFinalPesquisa())+"','dd/mm/yyyy')":"	and	    ise2_.dthr_programada between  '"+ sdfQuery.format(filtro.getDataInicialPesquisa()) +" 00:00:00'    and    '"+sdfQuery.format(filtro.getDataFinalPesquisa())+" 23:59:59'"));
		
		/*
		sqlPrincipal.append("and ise2_.DTHR_PROGRAMADA between '"
				+ sdfQuery.format(filtro.getDataInicialPesquisa())
				+ " 00:00:00' and '"
				+ sdfQuery.format(filtro.getDataFinalPesquisa())
				+ " 23:59:59' ");
		*/
		
		sqlPrincipal.append("and ( tae16_.ORIGEM_ATENDIMENTO='T' or ( ");
		sqlPrincipal
				.append("										tae16_.ORIGEM_ATENDIMENTO = atd4_.ORIGEM ");
		sqlPrincipal
				.append("										or (atv5_.CAD_SEQ is not null and tae16_.ORIGEM_ATENDIMENTO='D') ");
		sqlPrincipal.append("										)) ");

		if (filtro.getTipoColeta() != null) {
			sqlPrincipal.append("and ise2_.tipo_coleta = '"
					+ filtro.getTipoColeta() + "' ");
		}

		sqlPrincipal
				.append("and ufe19_.EMA_EXA_SIGLA=ise2_.UFE_EMA_EXA_SIGLA ");
		sqlPrincipal.append("and ufe19_.EMA_MAN_SEQ=ise2_.UFE_EMA_MAN_SEQ ");
		sqlPrincipal.append("and this_.ISE_SOE_SEQ=ise2_.SOE_SEQ ");
		sqlPrincipal.append("and this_.ISE_SEQP=ise2_.SEQP ");
		sqlPrincipal.append("and this_.AMO_SOE_SEQ=amo1_.SOE_SEQ ");
		sqlPrincipal.append("and this_.AMO_SEQP=amo1_.SEQP ");
		sqlPrincipal.append("and tae16_.MAN_SEQ=amo1_.MAN_SEQ ");
		sqlPrincipal.append("and ufe19_.UNF_SEQ=ise2_.UFE_UNF_SEQ ");
		sqlPrincipal
				.append("and (	soe3_.UNF_SEQ in (	select	distinct this_.unf_seq as y0_");
		sqlPrincipal.append("							from	agh.AEL_PERMISSAO_UNID_SOLICS this_");
		sqlPrincipal.append("   						where	this_.unf_seq_avisa = ? ");
		sqlPrincipal
				.append("							and 	this_.ufe_ema_exa_sigla=ufe19_.EMA_EXA_SIGLA");
		sqlPrincipal
				.append("							and 	this_.ufe_ema_man_seq=ufe19_.EMA_MAN_SEQ ");
		sqlPrincipal.append("							and 	this_.ufe_unf_seq=ufe19_.UNF_SEQ");
		sqlPrincipal.append("						)or (	atd4_.SEQ is not null ");
		sqlPrincipal.append("								and soe3_.CSP_CNV_CODIGO not in (");
		sqlPrincipal
				.append("																	select	distinct this_.VLR_NUMERICO as y0_");
		sqlPrincipal.append("																	from	AGH.AGH_PARAMETROS this_ ");
		sqlPrincipal.append("																	where	this_.NOME='"
				+ filtro.getCpParConvenio() + "'");
		sqlPrincipal.append("																)	)	) ");
		sqlPrincipal.append("and tae16_.responsavel_coleta='"
				+ DominioResponsavelColetaExames.C + "' ");
		sqlPrincipal.append("and ise2_.SIT_CODIGO in (");
		sqlPrincipal.append("							select	distinct this_.VLR_TEXTO as y0_");
		sqlPrincipal.append("							from	AGH.AGH_PARAMETROS this_ ");
		sqlPrincipal.append("							where	this_.NOME='"
				+ filtro.getCpParColeta() + "'");
		sqlPrincipal.append("						) ");
		sqlPrincipal.append("and (	case	when atd4_.seq is not null then ");
		sqlPrincipal.append("					case	when atd4_.origem = 'N' then ");
		sqlPrincipal.append("							'I'");
		sqlPrincipal.append("					else  ");
		sqlPrincipal.append("						atd4_.origem");
		sqlPrincipal.append("					end ");
		sqlPrincipal
				.append("				when atv5_.cad_seq is not null then   'D'	end) = 'I' ");
		sqlPrincipal.append("order by	descricao asc,"
				+ "       	lto_lto_id asc," + "	        prontuario asc,"
				+ "	        nome asc," + "	        tipo_registro asc,"
				+ "	        amo1_.SOE_SEQ asc,"
				+ "	        ise2_.DTHR_PROGRAMADA asc,"
				+ "	        amo1_.SEQP asc");

		javax.persistence.Query queryP = createNativeQuery(
				sqlPrincipal.toString());
		queryP.setParameter(1, filtro.getUnidadeColeta().getSeq());

		List<Object[]> valores = queryP.getResultList();

		String sqlUnion = "	SELECT 	distinct cast(unf.seq as varchar(5))||' - '||UNF.DESCRICAO as descricao,"
				+ "			UNF.ANDAR,"
				+ "			UNF.IND_ALA,"
				+ "			null RECEM_NASCIDO, "
				+ "			'**** COLETAR SANGUE COM ANTICOAGULANTE EDTA E TAMPA ROXA PARA O BANCO DE SANGUE ****' INFORMACOES_CLINICAS,"
				+ "			case 	when atd.lto_lto_id is not null then"
				+ "						'L:'||atd.lto_lto_id"
				+ "					when atd.qrt_numero is not null then"
				+ "						'Q:'||QUARTO.descricao"
				+ "					when atd.unf_seq is not null then"
				+ "						(	select 	'U:'||unf.andar||' '||unf.ind_ala||' - '||unf.descricao"
				+ "							from 	agh.agh_unidades_funcionais unf"
				+ "							where 	unf.seq = atd.unf_seq"
				+ "						)"
				+ "					else"
				+ " 						'Não é possível fornecer a localização do paciente. Quarto, leito e unidade não estão informados. Contate GSIS'"
				+ "			end lto_lto_id,"
				+ "			PAC.PRONTUARIO,"
				+ "			PAC.NOME,"
				+ "			null convenio,"
				+ "			null criado_em,"
				+ "			atd.seq soe_atd_seq,"
				+ "			null sit_codigo,"
				+ "			0 ise_seqp,"
				+ "			null DTHR_PROGRAMADA,"
				+ "			null DTHR_PROGRAMADA_ORD,"
				+ "			0 SOE_SEQ,"
				+ "			0 SEQP,"
				+ "			null tempo,"
				+ "			null ind,"
				+ "			null DESCRICAO1,"
				+ "			'EDTA' DESCRICAO2,"
				+ "			null sigla,"
				+ "			null DESCRICAO_USUAL,"
				+ "			41 UFE_UNF_SEQ,"
				+ "			'N' TIPO_COLETA,"
				+ "			null DRIVER_ID,"
				+ "			'H' TIPO_REGISTRO,"
				+ "			null as nro_unico,"
				+ "			she.seq she_seq,"
				+ "			'2' origem  "
				+ "	FROM   	agh.AGH_UNIDADES_FUNCIONAIS UNF,"
				+ "	       	agh.AIP_PACIENTES PAC,"
				+ "	       	agh.AGH_ATENDIMENTOS ATD "
				+ "	       	LEFT OUTER JOIN agh.AIN_QUARTOS QUARTO on ATD.qrt_numero = QUARTO.numero,"
				+ "	       	agh.ABS_SOLICITACOES_HEMOTERAPICAS SHE"
				+ "	WHERE	she.ind_situacao_coleta = ? "
				+ "	and   	she.ind_responsavel_coleta = 'C'"
				+ "	and   	atd.seq = she.atd_seq"
				+ "	and   	atd.ind_pac_atendimento  = 'S'"
				+ "	and   	(	case  	when atd.origem = 'N' then"
				+ "						'I'"
				+ "      				else"
				+ "      					atd.origem"
				+ "      		end) = 'I'"
				+ "	and   	PAC.CODIGO = ATD.PAC_CODIGO"
				+ "	and   	unf.seq    = atd.unf_seq"
				+ "	ORDER BY 1,6,7,8,27,16,14,17";

		// Query query = createHibernateQuery(sqlUnion);
		javax.persistence.Query query = createNativeQuery(
				sqlUnion);
		query.setParameter(1, filtro.getpSituacaoAmostra());

		List<Object[]> valoresUnion = query.getResultList();
		valores.addAll(valoresUnion);
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM HH:mm", new Locale("pt", "BR"));

		if (valores != null && valores.size() > 0) {

			for (Object[] objects : valores) {

				SolicitacaoColetarVO vo = new SolicitacaoColetarVO();

				if (objects[0] != null) {
					vo.setDescricao(objects[0].toString());
				}
				if (objects[1] != null) {
					vo.setAndar(objects[1].toString());
				}
				if (objects[2] != null) {
					vo.setIndAla2(objects[2].toString());
				}
				if (objects[3] != null) {
					vo.setRecemNascido(objects[3].toString());
				}
				if (objects[4] != null) {
					vo.setInformacoesClinicas(objects[4].toString());
				}
				if (objects[5] != null) {
					vo.setLtoLtoId(objects[5].toString());
				}
				if (objects[6] != null) {
					vo.setProntuario(objects[6].toString());
				}
				if (objects[7] != null) {
					vo.setNome(objects[7].toString());
				}
				if (objects[8] != null) {
					vo.setConvenio(objects[8].toString());
				}
				if (objects[9] != null) {
					vo.setCriadoEm(sdf1.format((Date) objects[9]));
				} else if (objects[29].equals("2")) {
					vo.setCriadoEm(sdf1.format(new Date()));
				}
				if (objects[10] != null) {
					vo.setSoeAtdSeq(objects[10].toString());
				}
				if (objects[11] != null) {
					vo.setSitCodigo(objects[11].toString());
				}
				if (objects[12] != null) {
					vo.setIseSeqp(objects[12].toString());
				}
				if (objects[13] != null) {
					vo.setDthrProgramada(sdf1.format((Date) objects[13]));
				} else if (objects[29].equals("2")) {
					vo.setDthrProgramada(sdf1.format(new Date()));
				}
				if (objects[14] != null) {
					vo.setDthrProgramadaOrd(sdf2.format((Date) objects[14]));
				} else if (objects[29].equals("2")) {
					vo.setDthrProgramadaOrd(sdf2.format(new Date()));
				}
				if (objects[15] != null) {
					vo.setSoeSeq(objects[15].toString());
				}
				if (objects[16] != null) {
					vo.setSeqp(objects[16].toString());
				}
				if (objects[17] != null) {
					vo.setTempo(objects[17].toString());
				}
				if (objects[18] != null) {
					vo.setInd(objects[18].toString());
				}
				if (objects[19] != null) {
					vo.setDescricao1(objects[19].toString());
				}
				if (objects[20] != null) {
					vo.setDescricao2(objects[20].toString());
				}
				if (objects[21] != null) {
					vo.setSigla(objects[21].toString());
				}
				if (objects[22] != null) {
					vo.setDescricaoUsual(objects[22].toString());
				}
				if (objects[23] != null) {
					vo.setUfeUnfSeq(objects[23].toString());
				}
				if (objects[24] != null) {
					vo.setTipoColeta(objects[24].toString());
				}
				if (objects[25] != null) {
					vo.setDriverId(objects[25].toString());
				}
				if (objects[26] != null) {
					vo.setTipoRegistro(objects[26].toString());
				}
				if (objects[27] != null) {
					vo.setNroUnico(objects[27].toString());
				}
				if (objects[28] != null) {
					vo.setSheSeq(Integer.parseInt(objects[28].toString()));
				}
				if (objects[30] != null) {
					vo.setDtNascimento(sdf1.format((Date) objects[30]));
				}
				if (objects[31] != null) {
					vo.setNomePessoaFisica(objects[31].toString());
				}
				if (objects[32] != null) {
					vo.setNroRegConselho(objects[32].toString());
				}
				if (objects[33] != null) {
					vo.setSiglaConselhoRegional(objects[33].toString());
				}

				lstRetorno.add(vo);
			}
			
			// Ordenação para respeitar ordenação do comparable do VO
			Collections.sort(lstRetorno);
		}
		
		return lstRetorno;
	}
	
	
	public List<SolicitacoesAgendaColetaAmbulatorioVO> pesquisarAgendaColetaAmbulatorio(final RelatorioMateriaisColetarInternacaoFiltroVO filtro){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		

		/*Join com horarios agendados*/
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);
		
		
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), "hed", JoinType.INNER_JOIN);
		criteria.createAlias("hed."+AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "gae", JoinType.INNER_JOIN);
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "gex", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "see", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(),"ico", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(),"exa", JoinType.INNER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(),"ufe", JoinType.INNER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(),"aie", JoinType.INNER_JOIN);

		criteria.createAlias("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(),"ema", JoinType.INNER_JOIN);
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(),"amo", JoinType.INNER_JOIN);
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString(),"equ", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("amo."+AelAmostras.Fields.RECIPIENTE_COLETA.toString(),"rco", JoinType.INNER_JOIN);
		criteria.createAlias("amo."+AelAmostras.Fields.ANTICOAGULANTE.toString(),"atc", JoinType.LEFT_OUTER_JOIN);

		/*Paciente*/
		/*Os próximos 3 createAlias equivalem a V_AEL_SOLIC_ATENDS */
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		/*Atendimento*/
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		/*Atendimento Diverso*/
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		/*convenio*/
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO.toString(), "cnv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atv."+AelAtendimentoDiversos.Fields.PACIENTE.toString(), "pacDiv", JoinType.LEFT_OUTER_JOIN);
		/*****************************************************************************************************************/
		
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		
		/*******Projections*******/
		criteria.setProjection(Projections.projectionList()

				.add(Projections.groupProperty("iha."+AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()), "hed_gae_unf_seq")
				.add(Projections.groupProperty("iha."+AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()), "hed_gae_seqp")
				
				
				.add(Projections.sqlProjection("this_.HED_GAE_SEQP ||' - '|| coalesce(gex4_.descricao,exa7_.descricao) descricaoGrade", 
												new String[]{"descricaoGrade"},
												new Type[]{StringType.INSTANCE}), "descricaoGrade")				
				
				.add(Projections.min("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()), "hed_dthr_agenda")

				.add(Projections.groupProperty("ufe."+AelUnfExecutaExames.Fields.UNF_SEQ.toString()), "ufe_unf_seq")
				.add(Projections.groupProperty("soe."+AelSolicitacaoExames.Fields.SEQ.toString()), "soe_seq")
				.add(Projections.groupProperty("ise."+AelItemSolicitacaoExames.Fields.SEQP), "ise_seqp")

				.add(Projections.sqlProjection("coalesce(soe15_.atd_seq, soe15_.atv_seq) as soe_atd_seq", 
												new String[] { "soe_atd_seq" },
												new Type[] { IntegerType.INSTANCE }), "soe_atd_seq")

				.add(Projections.sqlProjection(	" (	case	when soe15_.atd_seq is not null then"
														+ "				case	when pac19_.PRONTUARIO is not null then"
														+ "					pac19_.prontuario"
														+ "				else"
														+ "					atd16_.prontuario"
														+ "				end"
														+ "			when soe15_.atv_seq is not null then"
														+ "				case	when pacdiv20_.PRONTUARIO is not null then"
														+ "					pacdiv20_.prontuario"
														+ "				else " 
														+ "					atv17_.prontuario"
														+ "				end" + "	end) as prontuario",
												new String[] { "prontuario" },
												new Type[] { IntegerType.INSTANCE }), "prontuario")

				.add(Projections.sqlGroupProjection(	" (	case	when soe15_.atd_seq is not null then"
														+ "				pac19_.nome"
														+ "			when soe15_.atv_seq is not null then"
														+ "				pacdiv20_.nome"
														+ "	end) as nome_paciente",
												"nome_paciente",
												new String[] { "nome_paciente" },
												new Type[] { StringType.INSTANCE }), "nome_paciente")

				.add(Projections.groupProperty("cnv."+FatConvenioSaude.Fields.DESCRICAO.toString()), "convenio_descricao")

				.add(Projections.groupProperty("exa."+AelExames.Fields.SIGLA.toString()), "sigla_exame")
				
				.add(Projections.sqlGroupProjection(	" exa7_.descricao_usual || coalesce(	case	when amo11_.unid_tempo_intervalo_coleta is not null then"
														+ "				case	when amo11_.tempo_intervalo_coleta = 0 then"
														+ "					' ' || ico6_.tipo_substancia || ' ' || ico6_.volume_ingerido || ' ' || ico6_.unid_medida_volume "
														+ "				end "
														+ "	end,'') as descricao_usual",
												"descricao_usual",
												new String[] { "descricao_usual" },
												new Type[] { StringType.INSTANCE }), "descricao_usual")
				
				.add(Projections.groupProperty("see."+AelSalasExecutorasExames.Fields.NUMERO), "numero_sala")
				.add(Projections.groupProperty("pes."+RapPessoasFisicas.Fields.NOME), "nome_pessoa")
				.add(Projections.groupProperty("amo."+AelAmostras.Fields.SEQP), "amostra_seqp")
				.add(Projections.groupProperty("amo."+AelAmostras.Fields.NRO_UNICO), "numero_unico")
				.add(Projections.groupProperty("amo."+AelAmostras.Fields.DT_NUMERO_UNICO), "dt_numero_unico")
				.add(Projections.groupProperty("rco."+AelRecipienteColeta.Fields.DESCRICAO), "descricao_reci")
				.add(Projections.groupProperty("atc."+AelAnticoagulante.Fields.DESCRICAO), "descricao_anti")
				
				.add(Projections.sqlProjection(	" coalesce((	case	when amo11_.unid_tempo_intervalo_coleta is not null then "
														+ "					cast(amo11_.tempo_intervalo_coleta as varchar(6))||' '||amo11_.unid_tempo_intervalo_coleta" 
														+ "	end),'') as tempo",
											new String[] { "tempo" },
											new Type[] { StringType.INSTANCE }), "tempo_intervalo_coleta")
				
				.add(Projections.sqlProjection(	"    	(case 	when ema10_.ind_jejum = 'S' then 'J' "
														+"				else   	" 
														+"					case 	when 	ema10_.ind_npo = 'S' then 'N' "
														+"    							else   	"
														+"									case 	when 	ema10_.ind_dieta_diferenciada = 'S' then "
														+"												'D'	"
														+"    								end  "
														+"    				end "
														+"    	end) as ind_jejum_dieta_dif",
												new String[] { "ind_jejum_dieta_dif" },
												new Type[] { StringType.INSTANCE }), "ind_jejum_dieta_dif")

				.add(Projections.groupProperty("ise."+AelItemSolicitacaoExames.Fields.TIPO_COLETA), "tipo_coleta")
				.add(Projections.groupProperty("equ."+AelEquipamentos.Fields.DRIVER_ID), "driver_id")

				.add(Projections.sqlGroupProjection(	"    	(case 	when soe15_.USA_ANTIMICROBIANOS = 'S' then "
														+"    				'** Paciente em uso de antimicrobianos **' "
														+"    			else "
														+"    				''	"
														+"    	end) as antimicrobiano",
												"soe15_.usa_antimicrobianos",
												new String[] { "antimicrobiano" },
												new Type[] { StringType.INSTANCE }), "antimicrobiano")

				.add(Projections.sqlGroupProjection(	" coalesce(ufe8_.ind_desativa_temp,'N') as ind_desativa_temp",
												"ufe8_.ind_desativa_temp",
												new String[] { "ind_desativa_temp" },
												new Type[] { StringType.INSTANCE}), "ind_desativa_temp")

				.add(Projections.groupProperty("ufe."+AelUnfExecutaExames.Fields.DTHR_REATIVA_TEMP.toString()), "dthr_reativa_temp")
				.add(Projections.groupProperty("ufe."+AelUnfExecutaExames.Fields.MOTIVO_DESATIVACAO.toString()), "motivo_desativacao")
				.add(Projections.groupProperty("ufe."+AelUnfExecutaExames.Fields.SITUACAO.toString()), "situacao")
				
				/*Colocados devido ao group by que foi obrigatório*/
				.add(Projections.groupProperty("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()))
				.add(Projections.groupProperty("gex."+AelGrupoExames.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO.toString()))
				.add(Projections.sqlGroupProjection("soe15_.atd_seq", "soe15_.atd_seq", new String[]{"atd_seq"},new Type[]{IntegerType.INSTANCE}))
				.add(Projections.sqlGroupProjection("soe15_.atv_seq", "soe15_.atv_seq", new String[]{"atv_seq"},new Type[]{IntegerType.INSTANCE}))
				.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("atv."+AelAtendimentoDiversos.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("pac."+AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("pac."+AipPacientes.Fields.NOME.toString()))
				.add(Projections.groupProperty("pacDiv."+AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("pacDiv."+AipPacientes.Fields.NOME.toString()))
				.add(Projections.groupProperty("amo."+AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString()))
				.add(Projections.groupProperty("amo."+AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString()))
				.add(Projections.groupProperty("ico."+AelIntervaloColeta.Fields.TIPO_SUBSTANCIA.toString()))
				.add(Projections.groupProperty("ico."+AelIntervaloColeta.Fields.VOLUME_INGERIDO.toString()))
				.add(Projections.groupProperty("ico."+AelIntervaloColeta.Fields.UNIDADE_MEDIDA_VOLUME.toString()))				
				.add(Projections.groupProperty("ema."+AelExamesMaterialAnalise.Fields.IND_JEJUM.toString()))
				.add(Projections.groupProperty("ema."+AelExamesMaterialAnalise.Fields.IND_NPO.toString()))
				.add(Projections.groupProperty("ema."+AelExamesMaterialAnalise.Fields.IND_DIETA_DIFERENCIADA.toString()))

		);

		/*******Restrictions*******/
		/*Filtro pela SITUACAO diferente de PENDENTE*/
		criteria.add(Restrictions.ne("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString() , DominioSituacaoItemSolicitacaoExame.PE.toString()));
		/*Filtro pela unidade EXECUTORA*/
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString() , filtro.getUnidadeColeta().getSeq()));
		/*Filtro pela GRADE*/
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString() , filtro.getGradeAgenda().getId().getSeqp()));

		/*Filtro pela DATA, pego os agendamentos de cert dia*/
		Calendar calendarDataFim = Calendar.getInstance();  
		calendarDataFim.setTime(filtro.getDataAgenda());  
		calendarDataFim.add(Calendar.HOUR_OF_DAY, 23);
		calendarDataFim.add(Calendar.MINUTE, 59);
		calendarDataFim.add(Calendar.SECOND, 59);

		criteria.add(Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), filtro.getDataAgenda(), calendarDataFim.getTime()));

		criteria.add(Restrictions.in("amo."+AelAmostras.Fields.SITUACAO.toString(), new Object[]{	DominioSituacaoAmostra.G, 
																									DominioSituacaoAmostra.C, 
																									DominioSituacaoAmostra.U, 
																									DominioSituacaoAmostra.M}));

		criteria.add(Restrictions.eq("hed."+AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString(), DominioSituacaoHorario.M));
		
		criteria.addOrder(Order.asc("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()));
		criteria.addOrder(Order.asc("soe."+AelSolicitacaoExames.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("amo."+AelAmostras.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("descricao_usual"));

		List<SolicitacoesAgendaColetaAmbulatorioVO> listaResul = new ArrayList<SolicitacoesAgendaColetaAmbulatorioVO>();

		List<Object[]> results = executeCriteria(criteria);
		
		for (Object[] objectResult : results) {

			SolicitacoesAgendaColetaAmbulatorioVO itemResult = new SolicitacoesAgendaColetaAmbulatorioVO();

			itemResult.setSoeSeq((Integer)objectResult[5]);
			itemResult.setAmoSeqp((Short)objectResult[15]);

			itemResult.setGradeSeqp((Integer)objectResult[1]);
			itemResult.setGradeDescricao((String)objectResult[2]);
			itemResult.setHoraColeta((Date)objectResult[3]);

			itemResult.setProntuario((Integer)objectResult[8]);
			itemResult.setNomePaciente((String)objectResult[9]);

			if(objectResult[16] != null){
				itemResult.setConvenio((String)objectResult[10]);
			}

			itemResult.setSalaColeta((String)objectResult[13]);

			itemResult.setResponsavelColeta((String)objectResult[14]);

			if(objectResult[16] != null){
				itemResult.setNroUnico((Integer)objectResult[16]);
			}
			if(objectResult[18] != null){
				itemResult.setRecipiente((String)objectResult[18]);
			}
			if(objectResult[19] != null){
				itemResult.setAntiCoagualante((String)objectResult[19]);
			}
			if(objectResult[25] != null){			
				itemResult.setDesativaTempColeta((String)objectResult[25]);
			}
			if(objectResult[26] != null){
				itemResult.setDthrReativaTemp((Date)objectResult[26]);
			}
			if(objectResult[27] != null){
				itemResult.setMotivoDesativacao((String)objectResult[27]);
			}
			
			itemResult.setExamesDescUsual((String)objectResult[12]);
		
			itemResult.setLaboratorio((Short)objectResult[4]);
			
			itemResult.setTempo((String)objectResult[20]);
			
			if(objectResult[21] != null){
				itemResult.setIndJejum((String)objectResult[21]);
			}
			if(objectResult[22] != null){
				itemResult.setTipoColeta(((DominioTipoColeta)objectResult[22]).toString());
			}
			
			listaResul.add(itemResult);
		}

		return listaResul;
	}
	

	/**
	 * 
	 * @param solicitacaoExame
	 * @return
	 * @throws BaseException
	 */
	public Boolean existeAelAmostrasPorSolicitacaoExame(
			AelSolicitacaoExames solicitacaoExame) throws BaseException {
		return existeAelAmostrasPorSolicitacaoExame(solicitacaoExame, null);

	}

	/**
	 * 
	 * @param solicitacaoExame
	 * @param seqp
	 * @return
	 * @throws BaseException
	 */
	public Boolean existeAelAmostrasPorSolicitacaoExame(
			AelSolicitacaoExames solicitacaoExame, Short amostraSeqp)
			throws BaseException {

		if (this.existeVAelArcoSolicitacaoCarregarArquivoLaudoResultadoExame(
				solicitacaoExame, amostraSeqp)) {

			DetachedCriteria criteria = DetachedCriteria.forClass(
					AelAmostras.class, AMO.substring(0, 3));
			criteria.setProjection(Projections.property(AMO
					+ AelAmostras.Fields.SOE_SEQ.toString()));
			criteria.add(Restrictions.eq(
					AMO + AelAmostras.Fields.SOE_SEQ.toString(),
					solicitacaoExame.getSeq()));

			if (amostraSeqp != null) {
				criteria.add(Restrictions.eq(
						AMO + AelAmostras.Fields.SEQP.toString(), amostraSeqp));
			}

			return executeCriteriaCount(criteria) > 0;
		}

		return false;
	}

	/**
	 * Verifica se existe uma solicitação para o mesmo atendimento que tenha o
	 * mesmo exame.
	 * 
	 * @param {Integer} pacCodigo
	 * @param {Integer} soeSeq
	 * @param {Integer} ufeEmaExaSigla
	 * @param {Integer} ufeEmaManSeq
	 * @param {Date} dataCalculadaAparecimentoSolicitacao
	 * @return {Integer} Sequencial da Solicitação
	 */
	public Integer buscaSolicitacaoExameSeqMax(Integer pacCodigo,
			Integer soeSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq,
			Date dataCalculadaAparecimentoSolicitacao) {

		/*
		 * select max(soe.seq) from ael_item_solicitacao_exames ise,
		 * ael_solicitacao_exames soe, agh_atendimentos atd where atd.pac_codigo
		 * = c_pac_codigo and soe.atd_seq = atd.seq and ise.soe_seq = soe.seq
		 * and ise.soe_seq <> c_soe_seq and substr(ise.ufe_ema_exa_sigla,1,5) =
		 * c_ufe_ema_exa_sigla and ise.ufe_ema_man_seq = c_ufe_ema_man_seq and
		 * substr(ise.sit_codigo,1,2) in (select codigo from
		 * ael_sit_item_solicitacoes where ind_alerta_exame_ja_solic = 'S') and
		 * ise.dthr_programada+0 >= c_dthr_calculada;
		 */

		StringBuffer hql = new StringBuffer(320);
		hql.append("select max(soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString()).append(") ");
		hql.append(" from ")
				.append(AelItemSolicitacaoExames.class.getSimpleName())
				.append(" ise ");
		hql.append(" inner join ise."
				+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()
				+ " soe ");
		hql.append(" inner join soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + " atd ");

		hql.append(" where atd.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = :pacCodigo ");

		if (soeSeq != null) {
			hql.append(" AND ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ
					.toString());
			hql.append(" <> :soeSeq ");
		}

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString());
		hql.append(" = :ufeEmaExaSigla ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ
				.toString());
		hql.append(" = :ufeEmaManSeq ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" IN (");
		hql.append(" select sit.");
		hql.append(AelSitItemSolicitacoes.Fields.CODIGO.toString());
		hql.append(" from ")
				.append(AelSitItemSolicitacoes.class.getSimpleName())
				.append(" sit ");
		hql.append(" where sit.");
		hql.append(AelSitItemSolicitacoes.Fields.ALERTA_EXAME_JA_SOLIC
				.toString());
		hql.append(" = :alertaExameJaSolicitado ");
		hql.append(") ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString())
				.append(" >= :dataCalculadaAparecimentoSolicitacao ");

		javax.persistence.Query query = createQuery(
				hql.toString());
		query.setParameter("pacCodigo", pacCodigo);

		if (soeSeq != null) {
			query.setParameter("soeSeq", soeSeq);
		}

		query.setParameter("ufeEmaExaSigla", ufeEmaExaSigla);
		query.setParameter("ufeEmaManSeq", ufeEmaManSeq);
		query.setParameter("alertaExameJaSolicitado", Boolean.TRUE);
		query.setParameter("dataCalculadaAparecimentoSolicitacao",
				dataCalculadaAparecimentoSolicitacao, TemporalType.TIMESTAMP);

		Integer objMax = (Integer) query.getSingleResult();

		return objMax;

	}

	/**
	 * Verifica se existe uma solicitação para o mesmo atendimento que tenha o
	 * mesmo exame, porém apenas para itens com situação diferente de Cancelado
	 * (CA).
	 * 
	 * @param {Integer} pacCodigo
	 * @param {Integer} soeSeq
	 * @param {Integer} ufeEmaExaSigla
	 * @param {Integer} ufeEmaManSeq
	 * @param {Date} dataCalculadaAparecimentoSolicitacao
	 * @return {Integer} Sequencial da Solicitação
	 */
	public Integer buscaSolicitacaoExameSeqMaxSituacaoDiferenteCA(
			Integer pacCodigo, Integer soeSeq, String ufeEmaExaSigla,
			Integer ufeEmaManSeq, Date dataCalculadaAparecimentoSolicitacao) {

		/*
		 * select max(soe.seq) from ael_item_solicitacao_exames ise,
		 * ael_solicitacao_exames soe, agh_atendimentos atd where atd.pac_codigo
		 * = c_pac_codigo and soe.atd_seq = atd.seq and ise.soe_seq = soe.seq
		 * and ise.soe_seq <> c_soe_seq and substr(ise.ufe_ema_exa_sigla,1,5) =
		 * c_ufe_ema_exa_sigla and ise.ufe_ema_man_seq = c_ufe_ema_man_seq and
		 * substr(ise.sit_codigo,1,2) <> 'CA' and ise.dthr_programada+0 >=
		 * c_dthr_calculada;
		 */

		StringBuffer hql = new StringBuffer(260);
		hql.append("select max(soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString()).append(") ");
		hql.append(" from ")
				.append(AelItemSolicitacaoExames.class.getSimpleName())
				.append(" ise ");
		hql.append(" inner join ise."
				+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()
				+ " soe ");
		hql.append(" inner join soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + " atd ");

		hql.append(" where atd.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = :pacCodigo ");

		if (soeSeq != null) {
			hql.append(" AND ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ
					.toString());
			hql.append(" <> :soeSeq ");
		}

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString());
		hql.append(" = :ufeEmaExaSigla ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ
				.toString());
		hql.append(" = :ufeEmaManSeq ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" <> :sitCodigo ");

		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString())
				.append(" >= :dataCalculadaAparecimentoSolicitacao ");

		javax.persistence.Query query = createQuery(
				hql.toString());
		query.setParameter("pacCodigo", pacCodigo);

		if (soeSeq != null) {
			query.setParameter("soeSeq", soeSeq);
		}

		query.setParameter("ufeEmaExaSigla", ufeEmaExaSigla);
		query.setParameter("ufeEmaManSeq", ufeEmaManSeq);
		query.setParameter("sitCodigo",
				DominioSituacaoItemSolicitacaoExame.CA.toString());
		query.setParameter("dataCalculadaAparecimentoSolicitacao",
				dataCalculadaAparecimentoSolicitacao, TemporalType.TIMESTAMP);

		Integer objMax = (Integer) query.getSingleResult();

		return objMax;

	}

	public List<Object[]> pesquisarRelatorioTicketAreaExecutora(
			Integer soeSeq, Short unfSeq) throws BaseException {

		return executeCriteria(obterCriteriaRelTicketAreaExecutora(
				soeSeq, unfSeq));
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private DetachedCriteria obterCriteriaRelTicketAreaExecutora(
			Integer soeSeq, Short unfSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(
				AelItemSolicitacaoExames.class, "ise");

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(
				Property.forName("ise."
						+ AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),
				"soeSeq");
		projectionList.add(
				Property.forName("soe."
						+ AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS
								.toString()), "infClinicas");
		projectionList.add(
				Property.forName("soe."
						+ AelSolicitacaoExames.Fields.CRIADO_EM.toString()),
				"criadoEm");
		projectionList.add(
				Property.forName("ise."
						+ AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()),
				"descMaterialAnalise");
		projectionList.add(Property.forName("cnv.descricao"), "convenio");
		projectionList.add(Property.forName("csp.descricao"), "convenioSaudePlano");
		projectionList.add(Property.forName("aip.ltoLtoId"), "lto_lto_id");
		projectionList.add(Property.forName("aip.prontuario"), "prontuario");
		projectionList.add(Property.forName("aip.nome"), "nome");
		projectionList.add(
				Property.forName("ise."
						+ AelItemSolicitacaoExames.Fields.SEQP.toString()),
				"seqp");
		projectionList.add(
				Property.forName("ise."
						+ AelItemSolicitacaoExames.Fields.TIPO_COLETA
								.toString()), "tipoColeta");
		projectionList.add(
				Property.forName("ise."
						+ AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA
								.toString()), "dthrProgramada");
		projectionList.add(Property.forName("ise.indUsoO2"), "indUso02");
		// --decode(ind_uso_o2,'S','Usa O2',null) IND_USO_O2
		projectionList.add(Property.forName("ise."
				+ AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE_UNIDADE
						.toString()), "tipoTransporte");
		projectionList.add(Property.forName("exa.descricaoUsual"),
				"descricaoUsual");
		projectionList.add(
				Property.forName("soe."
						+ AelSolicitacaoExames.Fields.IND_OBJETIVO_SOLIC
								.toString()), "indObjtSolic");
		// ISE.SEQP SEQP_IND_OBJ_SOLIC -- ISE.SEQP||'
		// '||DECODE(VAS.IND_OBJETIVO_SOLIC,'1','1º Exame','2','Exame
		// Comparativo') SEQP_IND_OBJ_SOLIC
		projectionList.add(Property.forName("aip.codigo"), "pacCodigo");
		projectionList.add(Property.forName("aip.sexo"), "sexo");
		projectionList
				.add(Property.forName("aip.dtNascimento"), "dtNascimento");
		projectionList
				.add(Property.forName("atd."
						+ AghAtendimentos.Fields.SEQ.toString()), "atdSeq");
		projectionList.add(
				Property.forName("atv."
						+ AelAtendimentoDiversos.Fields.SEQUENCE.toString()),
				"atvSeq");
		projectionList.add(
				Property.forName("soe."
						+ AelSolicitacaoExames.Fields.SERVIDOR_VIN_CODIGO.toString()),
				"serVinCodigo");
		projectionList.add(
				Property.forName("soe."
						+ AelSolicitacaoExames.Fields.SERVIDOR_MATRICULA.toString()),
				"serMatricula");
		projectionList.add(
				Property.forName(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString()+"."+RapServidores.Fields.CODIGO_VINCULO.toString()),
				"serVinCodigoResponsavel");
		projectionList.add(
				Property.forName(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString()+"."+RapServidores.Fields.MATRICULA.toString()),
				"serMatriculaResponsavel");
		
		projectionList.add(
				Property.forName("unf."
						+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),
				"unfSeq");
		projectionList.add(
				Property.forName("ran."
						+ AelRegiaoAnatomica.Fields.DESCRICAO.toString()),
				"descRegiaoAnatomica");
		projectionList.add(
				Property.forName("man."
						+ AelMateriaisAnalises.Fields.DESCRICAO.toString()),
				"descicaoMaterial");

		criteria.setProjection(projectionList);

		criteria.createCriteria("ise."
				+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),
				"soe", JoinType.INNER_JOIN);
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd",
				JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."
				+ AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(),
				"unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString(),
				"cnv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO.toString(),
				"csp", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(),
				"unf2", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(),
				"atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(
				"atd." + AghAtendimentos.Fields.PACIENTE.toString(), "aip",
				JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES
								.toString(), "UFE", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "man", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "ran", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria("ise."
				+ AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa",
				JoinType.INNER_JOIN);

		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(),
				AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (soeSeq != null) {
			criteria.add(Restrictions.eq("soe."
					+ AelSolicitacaoExames.Fields.SEQ, soeSeq));
		}
		criteria.add(Restrictions.not(Restrictions.eq("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO, "PE")));

		DetachedCriteria subPermissao = DetachedCriteria.forClass(
				AelPermissaoUnidSolic.class, "PUS");
		subPermissao.setProjection(Projections.property("PUS."
				+ AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		subPermissao.add(Restrictions.eqProperty("PUS."
				+ AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString(),
				"UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()));
		subPermissao.add(Restrictions.eqProperty("PUS."
				+ AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString(),
				"UFE." + AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()));
		subPermissao.add(Restrictions.eqProperty("PUS."
				+ AelPermissaoUnidSolic.Fields.UFE_UNF.toString(), "UFE."
				+ AelUnfExecutaExames.Fields.UNF_SEQ.toString()));

		// Sub query
		StringBuffer sql = new StringBuffer(140);
		sql.append("	UFE_UNF_SEQ  in (");
		sql.append("	( SELECT unfx.seq  from  agh.agh_unidades_funcionais unfx ");
		sql.append("	   WHERE unfx.seq = ?  or   unfx.unf_seq = ? )) ");

		Object[] values = { unfSeq.shortValue(), unfSeq };
		Type[] types = { ShortType.INSTANCE, ShortType.INSTANCE };
		subPermissao.add(Restrictions.sqlRestriction(sql.toString(), values,
				types));

		criteria.add(Subqueries.exists(subPermissao));

		// Ordena a lista
		criteria.addOrder(Order.asc("ise."
				+ AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("soe."
				+ AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString()));
		criteria.addOrder(Order.asc("soe."
				+ AelSolicitacaoExames.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc("cnv.descricao"));
		criteria.addOrder(Order.asc("csp.descricao"));
		criteria.addOrder(Order.asc("aip.ltoLtoId"));
		criteria.addOrder(Order.asc("aip.prontuario"));
		criteria.addOrder(Order.asc("aip.nome"));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("ise.indUsoO2"));
		criteria.addOrder(Order.asc("ise."+ AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE_UNIDADE.toString()));
		criteria.addOrder(Order.asc("exa.descricaoUsual"));
		
		
		return criteria;
	}

	// FIXME Apagar este método se não utilizado até o dia 01/09/2012
//	private DetachedCriteria obterCriteriaHorarioAgendado(Integer soeSeq,
//			Short iseSeqp) {
//
//		DetachedCriteria criteria = DetachedCriteria.forClass(
//				AelItemHorarioAgendado.class, "iha");
//		ProjectionList projectionList = Projections.projectionList();
//		projectionList
//				.add(Property.forName("iha."
//						+ AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString()),
//						"iseSoeSeq");
//		projectionList.add(
//				Property.forName("iha."
//						+ AelItemHorarioAgendado.Fields.ISE_SEQP.toString()),
//				"iseSeqp");
//		projectionList.add(
//				Property.forName("iha."
//						+ AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ
//								.toString()), "hedGaeUnfSeq");
//		projectionList.add(Property.forName("iha."
//				+ AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()),
//				"hedGaeSeqp");
//		projectionList.add(
//				Property.forName("iha."
//						+ AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA
//								.toString()), "hedDthrAgenda");
//		criteria.setProjection(projectionList);
//
//		StringBuffer sql = new StringBuffer();
//		sql.append(" hed_dthr_agenda =  ( select  min(ihax.hed_dthr_agenda)     ");
//		sql.append("			from  agh.ael_item_horario_agendados ihax      	    ");
//		sql.append("			where   ihax.ise_soe_seq         = ?        	");
//		sql.append("					 and  ihax.ise_seqp      = ?        	");
//		sql.append("   	 ) ");
//
//		Object[] values = { soeSeq, iseSeqp };
//		Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE };
//		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));
//
//		return criteria;
//	}

	public Long listarSolicitacoesExamesCount(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");

		criteria.createAlias(
				"atd." + AghAtendimentos.Fields.CONSULTA.toString(), "con");

		criteria.add(Restrictions.eq(
				"con." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));

		return executeCriteriaCount(criteria);
	}

	public Long listarSolicitacoesExamesCount(Integer numeroConsulta,
			String codigoSituacaoItemSolicitacaoExame) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");

		criteria.createAlias(
				"atd." + AghAtendimentos.Fields.CONSULTA.toString(), "con");

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),
				"ise");

		criteria.createAlias("ise."
				+ AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO,
				"sis");

		criteria.add(Restrictions.eq(
				"con." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));

		criteria.add(Restrictions.ne("sis."
				+ AelSitItemSolicitacoes.Fields.CODIGO.toString(),
				codigoSituacaoItemSolicitacaoExame));

		return executeCriteriaCount(criteria);
	}

	public List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacao(
			Integer numeroConsulta, String vSituacaoPendente,
			String vSituacaoCancelado) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq("atd."
				+ AghAtendimentos.Fields.NUMERO_CONSULTA.toString(),
				numeroConsulta));

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),
				"ise");
		criteria.add(Restrictions.ne("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
				vSituacaoCancelado));

		criteria.createAlias(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES
								.toString(), "ama");

		criteria.createAlias(
				"ama."
						+ AelMateriaisAnalises.Fields.AEL_EXAMES_MATERIAL_ANALISES
								.toString(), "ema");
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eq(
				"ema."
						+ AelExamesMaterialAnalise.Fields.IND_IMP_TICKET_PACIENTE
								.toString(), Boolean.TRUE));

		return executeCriteria(criteria);
	}

	public List<AelSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoPendente(
			Integer numeroConsulta, String vSituacaoPendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq("atd."
				+ AghAtendimentos.Fields.NUMERO_CONSULTA.toString(),
				numeroConsulta));

		// criteria.add(Restrictions.ne("ise." +
		// AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
		// vSituacaoPendente));

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),
				"ise");

		criteria.createAlias(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES
								.toString(), "ama");

		criteria.createAlias(
				"ama."
						+ AelMateriaisAnalises.Fields.AEL_EXAMES_MATERIAL_ANALISES
								.toString(), "ema");
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eq(
				"ema."
						+ AelExamesMaterialAnalise.Fields.IND_IMP_TICKET_PACIENTE
								.toString(), Boolean.TRUE));

		return executeCriteria(criteria);
	}

	public List<Integer> obterSeqSolicitacoesExamesPorConsulta(
			Integer numeroConsulta, String vSituacaoPendente,
			String vSituacaoCancelado) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq("atd."
				+ AghAtendimentos.Fields.NUMERO_CONSULTA.toString(),
				numeroConsulta));

		criteria.createAlias(
				AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),
				"ise");

		criteria.createAlias(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES
								.toString(), "ama");

		criteria.createAlias(
				"ama."
						+ AelMateriaisAnalises.Fields.AEL_EXAMES_MATERIAL_ANALISES
								.toString(), "ema");
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("ema."
				+ AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), "ise."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eq(
				"ema."
						+ AelExamesMaterialAnalise.Fields.IND_IMP_TICKET_PACIENTE
								.toString(), Boolean.TRUE));

		// Regra adicionada para não trazer os exames que estão com a situação
		// pendente ou cancelada.
		criteria.add(Restrictions.ne("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
				vSituacaoPendente));
		criteria.add(Restrictions.ne("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
				vSituacaoCancelado));

		criteria.setProjection(Projections.distinct(Projections
				.property(AelSolicitacaoExames.Fields.SEQ.toString())));

		return executeCriteria(criteria);
	}

	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorItemSolicitacaoExameParaCancelamento(
			Integer conNumero, Date dthrMovimento, Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);
		criteria.createAlias(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString());
		criteria.add(Restrictions.eq(
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + "."
						+ AghAtendimentos.Fields.CONSULTA.toString() + "."
						+ AacConsultas.Fields.NUMERO.toString(), conNumero));
		if (dthrMovimento != null) {
			Date dataMovimento = DateUtil.truncaData(dthrMovimento);
			criteria.add(Restrictions.ge(
					AelSolicitacaoExames.Fields.CRIADO_EM.toString(),
					dataMovimento));
		}
		if (soeSeq != null) {
			criteria.add(Restrictions.eq(
					AelSolicitacaoExames.Fields.SEQ.toString(), soeSeq));
		}

		return this.executeCriteria(criteria);
	}

	/**
	 * private AghUnidadesFuncionais unidadeExecutora; private
	 * AelGrupoExameTecnicas grupoExameTecnicas; private Date dtInicial; private
	 * Date dtFinal; private Integer numUnicoInicial; private Integer
	 * numUnicoFinal;
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<PendenciaExecucaoVO> pesquisaExamesPendentesExecucao(
			Short p_unf_seq, Integer p_grt_seq, Date dtInicial, Date dtFinal,
			Integer numUnicoInicial, Integer numUnicoFinal) {

		List<PendenciaExecucaoVO> listaRetorno = new ArrayList<PendenciaExecucaoVO>();

		/* Criterias */
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AelItemSolicitacaoExames.class, "ise");
		criteria.createCriteria(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES
								.toString(), "aie", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."
				+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),
				"soe", JoinType.INNER_JOIN);
		criteria.createCriteria("aie."
				+ AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo",
				JoinType.INNER_JOIN);

		criteria.createCriteria(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES
								.toString(), "ufe", JoinType.INNER_JOIN);
		criteria.createCriteria(
				"ufe."
						+ AelUnfExecutaExames.Fields.AEL_GRP_TECNICA_UNF_EXAMES
								.toString(), "tce", JoinType.INNER_JOIN);

		criteria.createCriteria(
				"tce."
						+ AelGrpTecnicaUnfExames.Fields.AEL_GRUPO_EXAME_TECNICAS
								.toString(), "grt", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(
				"ise."
						+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES
								.toString(), "man", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."
				+ AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa",
				JoinType.INNER_JOIN);
		
		/* VAtendsSolic */
		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd",
				JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(
				"atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac",
				JoinType.LEFT_OUTER_JOIN);

		criteria.createCriteria("soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(),
				"atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(
				"atv." + AelAtendimentoDiversos.Fields.PACIENTE.toString(),
				"pacDi", JoinType.LEFT_OUTER_JOIN);

		
		//join com ael_extrato_item_solics (eis)
		criteria.createCriteria(
				AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(),
				"EIS", JoinType.LEFT_OUTER_JOIN);

		/* GROUP BY */
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.distinct(Projections.groupProperty("grt."
						+ AelGrupoExameTecnicas.Fields.SEQ.toString())),
						"GRT_SEQ")
				.add(Projections.groupProperty("grt."
						+ AelGrupoExameTecnicas.Fields.DESCRICAO.toString()),
						"GRUPO")
				
				//DTHR_EVENTO
				.add(Projections.max("EIS.".concat(
						AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString())), 
						"DTHR_EVENTO")
						
				/*.add(Projections.sqlProjection(" (	SELECT MAX(eis.dthr_evento)"
						+ "	FROM   agh.ael_extrato_item_solics eis"
						+ "	WHERE  eis.ise_soe_seq = {alias}.soe_seq"
						+ "	AND    eis.ise_seqp = {alias}.seqp"
						+ "	AND    eis.sit_codigo = 'AE') as DTHR_EVENTO",
						new String[] { "DTHR_EVENTO" },
						new Type[] { TimestampType.INSTANCE }), "DTHR_EVENTO")*/

				.add(Projections.groupProperty("aie."
						+ AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()),
						"SOE_SEQ")
				.add(Projections.groupProperty("amo."
						+ AelAmostras.Fields.NRO_UNICO.toString()), "NRO_UNICO")
				.add(Projections.groupProperty("man."
						+ AelMateriaisAnalises.Fields.DESCRICAO.toString()),
						"MATERIAL")
				.add(Projections.groupProperty("exa."
						+ AelExames.Fields.DESCRICAO.toString()), "EXAME")
				.add(Projections.groupProperty("ise."
						+ AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ
								.toString()), "UFE_UNF_SEQ")
				.add(Projections.groupProperty("ise."
						+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA
								.toString()), "UFE_EMA_EXA_SIGLA")
				.add(Projections.groupProperty("ise."
						+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ
								.toString()), "UFE_EMA_MAN_SEQ")
				.add(Projections.groupProperty("soe."
						+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString()),
						"atd_seq")
				.add(Projections.groupProperty("soe."
						+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO
								.toString()), "atv_seq")

				.add(Projections
						.sqlProjection(
								" (	case	when soe2_.atd_seq is not null then"
										+ "				case	when pac10_.PRONTUARIO is not null then"
										+ "					pac10_.PRONTUARIO"
										+ "				else"
										+ "					atd9_.PRONTUARIO"
										+ "				end"
										+ "			when soe2_.ATV_SEQ is not null then"
										+ "				case	when pacdi12_.PRONTUARIO is not null then"
										+ "					pacdi12_.PRONTUARIO"
										+ "				else" + "					atv11_.PRONTUARIO"
										+ "				end" + "	end) as PRONTUARIO",
								new String[] { "PRONTUARIO" },
								new Type[] { IntegerType.INSTANCE }), "PRONTUARIO")

				/*.add(Projections.sqlGroupProjection(
						" (	SELECT MAX(eis.dthr_evento)"
								+ "	FROM   agh.ael_extrato_item_solics eis"
								+ "	WHERE  eis.ise_soe_seq = {alias}.soe_seq"
								+ "	AND    eis.ise_seqp = {alias}.seqp"
								+ "	AND    eis.sit_codigo = 'AE') ",
						" DTHR_EVENTO", new String[] { "DTHR_EVENTO" },
						new Type[] { TimestampType.INSTANCE }))*/
								
				
				.add(Projections.groupProperty("pac."
						+ AipPacientes.Fields.PRONTUARIO))
				.add(Projections.groupProperty("pacDi."
						+ AipPacientes.Fields.PRONTUARIO))
				.add(Projections.groupProperty("pac."
						+ AipPacientes.Fields.PRONTUARIO))
				.add(Projections.groupProperty("pacDi."
						+ AipPacientes.Fields.PRONTUARIO))

				.add(Projections.groupProperty("atd."
						+ AghAtendimentos.Fields.PRONTUARIO))
				.add(Projections.groupProperty("atv."
						+ AelAtendimentoDiversos.Fields.PRONTUARIO)));

		/* Restrictions */
		// ISE.SIT_CODIGO = 'AE'
		criteria.add(Restrictions.eq("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "AE"));

		// AELC_GET_DTHR_RECEBE(ISE.SOE_SEQ,ISE.SEQP) between
		// NVL(p_dthr_inicial,SYSDATE-360) and NVL(p_dthr_final,SYSDATE)
		if (dtInicial == null) {
			dtInicial = DateUtil.adicionaDias(new Date(), -360);
		}
		if (dtFinal == null) {
			dtFinal = new Date();
		}

		Object[] values = { dtInicial, dtFinal };
		Type[] types = { TimestampType.INSTANCE, TimestampType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(
				" DTHR_EVENTO "
				+ "	between ? and ? ",
				values, types));
		
		//condição retirada da consulta acima
		criteria.add(Restrictions.eq("EIS."
				+ AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "AE"));

		// ISE.UFE_UNF_SEQ+0 = p_unf_seq
		criteria.add(Restrictions.eq("ise."
				+ AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(),
				p_unf_seq));

		// AIE.SITUACAO = 'R'
		criteria.add(Restrictions.eq("aie."
				+ AelAmostraItemExames.Fields.SITUACAO.toString(),
				DominioSituacaoAmostra.R));

		// NVL(TRUNC(AMO.NRO_UNICO),9999) >= nvl(p_nro_int_inicial,0) AND
		// NVL(TRUNC(AMO.NRO_UNICO),0) <= nvl(p_nro_int_final,9999)
		if (numUnicoInicial != null && numUnicoFinal != null) {
			if (numUnicoInicial > numUnicoFinal) {
				criteria.add(Restrictions.between("amo."
						+ AelAmostras.Fields.NRO_UNICO.toString(),
						numUnicoFinal, numUnicoInicial));
			} else {
				criteria.add(Restrictions.between("amo."
						+ AelAmostras.Fields.NRO_UNICO.toString(),
						numUnicoInicial, numUnicoFinal));
			}
		}
		// TCE.GRT_SEQ = p_grt_seq
		criteria.add(Restrictions.eq(
				"grt." + AelGrupoExameTecnicas.Fields.SEQ.toString(), p_grt_seq));

		criteria.addOrder(Order.asc("exa."
				+ AelExames.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("DTHR_EVENTO"));
		criteria.addOrder(Order.asc("amo."
				+ AelAmostras.Fields.NRO_UNICO.toString()));

		List<Object[]> valores = this.executeCriteria(criteria);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		if (valores != null && valores.size() > 0) {

			for (Object[] objects : valores) {

				PendenciaExecucaoVO vo = new PendenciaExecucaoVO();

				vo.setGrupoSeq((Integer) objects[0]);
				vo.setGrupo((String) objects[1]);
				vo.setDthrEvento(sdf1.format(objects[2]));
				vo.setSoeSeq((Integer) objects[3]);
				vo.setNumUnico((Integer) objects[4]);
				vo.setMaterial((String) objects[5]);
				vo.setExame((String) objects[6]);
				vo.setUfeUnfSeq((Short) objects[7]);
				vo.setUfeEmaExaSigla((String) objects[8]);
				vo.setUfeEmaManSeq((Integer) objects[9]);
				vo.setProntuario((Integer) objects[12]);

				listaRetorno.add(vo);
			}
		}

		return listaRetorno;
	}

	public List<AelSolicitacaoExames> listarSolicitacoesExames(
			Integer seqAtendimento, Date dataMaiorIgualCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(
				AelSolicitacaoExames.Fields.ATD_SEQ.toString(),
				seqAtendimento));
		criteria.add(Restrictions.ge(
				AelSolicitacaoExames.Fields.CRIADO_EM.toString(),
				dataMaiorIgualCriadoEm));

		return executeCriteria(criteria);
	}

	public AtendimentoCargaColetaVO listarAtendimentoParaCargaColetaRD(
			Integer pSoeSeq, Boolean isAtdDiv) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AelSolicitacaoExames.class, "SOE");

		if (isAtdDiv) {
			criteria = this.obterCriteriaAtendimentoDiverso(criteria);
		} else {
			criteria = this.obterCriteriaAtendimento(criteria);
		}

		// where(restrictions)
		criteria.add(Restrictions.eq(
				AelSolicitacaoExames.Fields.SEQ.toString(), pSoeSeq));
		criteria.add(Restrictions.eq("CNV."
				+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(),
				DominioSimNao.S));

		List<Object[]> lista = this.executeCriteria(criteria);
		AtendimentoCargaColetaVO atendimentoVO = null;

		if (!lista.isEmpty()) {
			Object[] obj = lista.get(0);
			atendimentoVO = new AtendimentoCargaColetaVO();
			atendimentoVO.setAtdSeq((Integer) obj[0]);
			atendimentoVO.setPacCodigo((Integer) obj[1]);
			atendimentoVO.setEspSeq((Short) obj[2]);
		}

		return atendimentoVO;
	}

	DetachedCriteria obterCriteriaAtendimento(DetachedCriteria criteria) {

		criteria.createCriteria("SOE."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO, "ATD",
				JoinType.INNER_JOIN);
		criteria.createCriteria("SOE."
				+ AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO, "CSP",
				JoinType.INNER_JOIN);
		criteria.createCriteria("CSP."
				+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE, "CNV",
				JoinType.INNER_JOIN);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(AelSolicitacaoExames.Fields.ATD_SEQ
								.toString()))
				.add(Projections.property("ATD."
						+ AghAtendimentos.Fields.PAC_CODIGO.toString()))
				.add(Projections.property("ATD."
						+ AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString())));

		return criteria;
	}

	DetachedCriteria obterCriteriaAtendimentoDiverso(DetachedCriteria criteria) {

		criteria.createCriteria("SOE."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO, "ATD",
				JoinType.INNER_JOIN);
		criteria.createCriteria("SOE."
				+ AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO, "CSP",
				JoinType.INNER_JOIN);
		criteria.createCriteria("CSP."
				+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE, "CNV",
				JoinType.INNER_JOIN);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(AelSolicitacaoExames.Fields.ATD_SEQ
								.toString()))
				.add(Projections.property("ATD."
						+ AelAtendimentoDiversos.Fields.PAC_CODIGO.toString()))
				.add(Projections.property("ATD."
						+ AelAtendimentoDiversos.Fields.ESP_SEQ.toString())));

		return criteria;
	}
	
	public List<Integer> obterSeqSolicitacoesExamesPorConsultaCertificacaoDigital(Integer numeroConsulta, String vSituacaoPendente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq("atd."+ AghAtendimentos.Fields.NUMERO_CONSULTA.toString(),numeroConsulta));
		criteria.createAlias(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "ama");
		criteria.createAlias("ama."+ AelMateriaisAnalises.Fields.AEL_EXAMES_MATERIAL_ANALISES.toString(), "ema");
		criteria.add(Restrictions.eqProperty("ema."+ AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), "ise."+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("ema."+ AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), "ise."+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eq("ise."+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),vSituacaoPendente));
		criteria.setProjection(Projections.distinct(Projections.property(AelSolicitacaoExames.Fields.SEQ.toString())));

		return executeCriteria(criteria);
	}
	
	public AelSolicitacaoExames obterSolicitacaoExamePorAtendimento(
			Integer seqAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.ATD_SEQ.toString(), seqAtendimento));
		
		List<AelSolicitacaoExames> result = this.executeCriteria(criteria); 
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	public List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePacienteAtendimentoDiverso(final Integer soeSeq, final Short unfSeq, final Boolean indColetavel){
		return executeCriteria(criarCriteriaEtiquetaEnvelopePacienteAtendimentoDiverso(soeSeq, unfSeq, indColetavel), 0, 1, null, false);
	}
	
	private DetachedCriteria criarCriteriaEtiquetaEnvelopePacienteAtendimentoDiverso(final Integer soeSeq, final Short unfSeq, final Boolean indColetavel) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString(), "IHA");
		criteria.createAlias("IHA." + AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), "HED");
		criteria.createAlias("HED." + AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "GAE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE");
		criteria.createAlias("UFE." + AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "UNF");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EIS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("ATV." + AelAtendimentoDiversos.Fields.NOME_PACIENTE.toString()), "nomePaciente")
				.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SEQ.toString()), "soeSeq")
				.add(Projections.property("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()),
						AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString())
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "nomeUnidadeFuncional"));
		
		final DetachedCriteria criteriaSub = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "EIS1");
		criteriaSub.setProjection(Projections.max("EIS1." + AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString()));
		criteriaSub.add(Restrictions.eqProperty("EIS1." + AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), "EIS."
				+ AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()));
		criteriaSub.add(Restrictions.eqProperty("EIS1." + AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), "EIS."
				+ AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()));

		criteria.add(Subqueries.propertyEq("EIS." + AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString(), criteriaSub));

		criteria.add(Restrictions.eq("MAN." + AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), indColetavel));
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.SEQ.toString(), soeSeq));
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("GAE." + AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(EtiquetaEnvelopePacienteVO.class));
		return criteria;
	}

	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Date hedDthrAgenda) {
		
		
		List<AelSolicitacaoExames> retorno;
		
		String aliasIha = "iha";
		String aliasIse = "ise";
		String aliasSoe = "soe";
		String aliasAtd = "atd";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, aliasSoe);
		criteria.createAlias(aliasSoe + separador + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), aliasIse);		
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString(), aliasIha);
		criteria.createAlias(aliasSoe + separador + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), aliasAtd);
		
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), hedDthrAgenda));
		
		criteria.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		retorno =  executeCriteria(criteria);
		
		if (retorno.get(0).getAtendimento() != null) {
			retorno.get(0).getAtendimento().getPaciente().getNome();
		} else if (retorno.get(0).getAtendimentoDiverso() != null) {
			retorno.get(0).getAtendimentoDiverso().getAipPaciente().getNome();
		}	
		return retorno;
	}
	
	
	/**
	 * 
	 * @param dataReferenciaIni
	 * @param dataReferenciaFim
	 * @return
	 */
	public List<RelatorioAgendamentoProfissionalVO> pesquisarRelatorioAgendamentoProf(Date dataReferenciaIni, Date dataReferenciaFim){

		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");
		criteria.createCriteria("unf."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "car", JoinType.INNER_JOIN);

		SimpleDateFormat sdfQuery = null;

		boolean isOracle = isOracle();

		if(isOracle){
			sdfQuery = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")); 
		}else{
			sdfQuery = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));
		}
		
		/*******Projections*******/
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),"seq")
				.add(Projections.property("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"descricao")
				
				.add(Projections.sqlProjection("(	select  count(distinct soe.seq) " +
												"	from    agh."+AelItemSolicitacaoExames.class.getAnnotation(javax.persistence.Table.class).name() +" ise," +
												"			agh."+AelSolicitacaoExames.class.getAnnotation(javax.persistence.Table.class).name()+" soe," +
												"			agh."+AelExtratoItemSolicitacao.class.getAnnotation(javax.persistence.Table.class).name()+" eis" +
												"	where   ise.soe_seq 		= soe.seq" +
												"	and     soe.unf_seq 		= {alias}.seq" +
												"	and     eis.ser_vin_codigo 	= soe.ser_vin_codigo_eh_responsabili" +
												"	and     eis.ser_matricula 	= soe.ser_matricula_eh_responsabilid" +
												"	and     ise.soe_seq 		= eis.ise_soe_seq" +
												"	and     ise.seqp    		= eis.ise_seqp" +
												"	and     eis.sit_codigo 		= '" + DominioSituacaoItemSolicitacaoExame.AG.toString()+ "'" +
												
												((isOracle)? "	and	    ise.dthr_programada between  to_date('"+ sdfQuery.format(dataReferenciaIni) +"','dd/mm/yyyy')    and    to_date('"+sdfQuery.format(dataReferenciaFim)+"','dd/mm/yyyy')":"	and	    ise.dthr_programada between  '"+ sdfQuery.format(dataReferenciaIni) +" 00:00:00'    and    '"+sdfQuery.format(dataReferenciaFim)+" 23:59:59'") +
												"	and     eis.criado_em in (" +
												"								select  min(criado_em)" +
												"								from    agh."+AelExtratoItemSolicitacao.class.getAnnotation(javax.persistence.Table.class).name()+" eix" +
												"								where   eix.ise_soe_seq = ise.soe_seq" +
												"								and     eix.ise_seqp = ise.seqp" +
												"								and     eix.sit_codigo= '" + DominioSituacaoItemSolicitacaoExame.AG.toString()+ "')" +
												") as totalagendados", new String[] { "totalagendados" },
												new Type[] { IntegerType.INSTANCE }), "totalagendados")
				.add(Projections.sqlProjection("(	select  count(distinct soe.seq)" +
												"	from    agh."+AelItemSolicitacaoExames.class.getAnnotation(javax.persistence.Table.class).name()+" ise," +
												"			agh."+AelSolicitacaoExames.class.getAnnotation(javax.persistence.Table.class).name()+" soe" +
												"	where   ise.soe_seq = soe.seq" +
												"	and     soe.unf_seq = {alias}.seq" +
												((isOracle)? "	and	    ise.dthr_programada between  to_date('"+ sdfQuery.format(dataReferenciaIni) +"','dd/mm/yyyy')    and    to_date('"+sdfQuery.format(dataReferenciaFim)+"','dd/mm/yyyy')":"	and	    ise.dthr_programada between  '"+ sdfQuery.format(dataReferenciaIni) +" 00:00:00'    and    '"+sdfQuery.format(dataReferenciaFim)+" 23:59:59'") +
												") as solicitacoes", new String[] { "solicitacoes" },
												new Type[] { IntegerType.INSTANCE }), "solicitacoes"));

		/*******Retrictions*******/
		criteria.add(Restrictions.eq("car."+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO));
		
		
		/*******Order*******/
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

	
		List<RelatorioAgendamentoProfissionalVO> lstReturn = new ArrayList<RelatorioAgendamentoProfissionalVO>();

		List<Object[]> returnQuery = executeCriteria(criteria);
		for (Object[] registro : returnQuery) {
			if(!registro[2].equals(0) || !registro[3].equals(0)){
				lstReturn.add(new RelatorioAgendamentoProfissionalVO(registro[1].toString(), (Integer)registro[2], (Integer)registro[3]));
			}
		}

		return lstReturn;
	}	

	
	public AelSolicitacaoExames obterAelSolicitacaoExamePorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		
		List<AelSolicitacaoExames> solicitacaoExames = executeCriteria(criteria);
		
		if(solicitacaoExames.isEmpty()) {
			return null;
		} else {
			return solicitacaoExames.get(0);
		}
	}
	
	@SuppressWarnings({"unchecked","PMD.NPathComplexity"})
	public List<TotalItemSolicitacaoExamesVO> buscarTotaisExamesPorPeriodo(Date dataInicial,
			Date dataFinal, Short convenio, Byte plano,
			DominioSituacaoItemSolicitacaoExame situacao, Short unfSeq,
			String sigla, DominioAgrupamentoTotaisExames tipoRelatorio) {

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		
		StringBuilder hql = new StringBuilder(450);

		//SELECT
		hql.append(" select unf.");
		hql.append(AghUnidadesFuncionais.Fields.DESCRICAO.toString());
		hql.append(',');
		hql.append(" exa.");
		hql.append(AelExames.Fields.DESCRICAO.toString());
		hql.append(',');
		hql.append(" man.");
		hql.append(AelMateriaisAnalises.Fields.DESCRICAO.toString());
		hql.append(',');		
		
		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.S.equals(tipoRelatorio)) {
			hql.append(" ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
			hql.append(',');
		}

		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.D.equals(tipoRelatorio)) {
			hql.append(" trunc( ise.");
			hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()).append("),");
		}
		
		hql.append(" count(*)");
		
		//FROM
		hql.append(" from ");
		hql.append(AelSolicitacaoExames.class.getSimpleName()).append(" soe, ");
		hql.append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise, ");
		hql.append(AelUnfExecutaExames.class.getSimpleName()).append(" ufe, ");
		hql.append(AelExamesMaterialAnalise.class.getSimpleName()).append(" ema, ");
		hql.append(AelExames.class.getSimpleName()).append(" exa, ");
		hql.append(AelMateriaisAnalises.class.getSimpleName()).append(" man, ");
		hql.append(AghUnidadesFuncionais.class.getSimpleName()).append(" unf ");

		//WHERE
		hql.append(" where soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString());
		hql.append(" = ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ
				.toString());
		hql.append(" and soe.");
		hql.append(AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO_CODIGO
				.toString());
		hql.append(" = :convenio");
		hql.append(" and soe.");
		hql.append(AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO_SEQ
				.toString());
		hql.append(" = :plano");
		hql.append(" and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString());
		hql.append(" between :dataInicial and :dataFinal");
		if (situacao != null) {
			hql.append(" and ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
			hql.append(" = :situacao");
		}
		if (unfSeq != null) {
			hql.append(" and ise.");
			hql.append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
			hql.append(" = :unfSeq");
		}
		if (!StringUtils.isEmpty(sigla)) {
			hql.append(" and ise.");
			hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA
					.toString());
			hql.append(" = :sigla");
		}
		hql.append(" and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" = ufe.");
		hql.append(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" = ufe.");
		hql.append(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString());
		hql.append(" and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
		hql.append(" = ufe.");
		hql.append(AelUnfExecutaExames.Fields.UNF_SEQ.toString());
		hql.append(" and ufe.");
		hql.append(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" = ema.");
		hql.append(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString());
		hql.append(" and ufe.");
		hql.append(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString());
		hql.append(" = ema.");
		hql.append(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString());
		hql.append(" and ema.");
		hql.append(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString());
		hql.append(" = exa.");
		hql.append(AelExames.Fields.SIGLA.toString());
		hql.append(" and ema.");
		hql.append(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString());
		hql.append(" = man.");
		hql.append(AelMateriaisAnalises.Fields.SEQ.toString());
		hql.append(" and ufe.");
		hql.append(AelUnfExecutaExames.Fields.UNF_SEQ.toString());
		hql.append(" = unf.");
		hql.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
		
		//GROUP BY
		hql.append(" group by");
		hql.append(" unf.");
		hql.append(AghUnidadesFuncionais.Fields.DESCRICAO
				.toString());
		hql.append(", exa.");
		hql.append(AelExames.Fields.DESCRICAO
				.toString());
		hql.append(", man.");
		hql.append(AelMateriaisAnalises.Fields.DESCRICAO
				.toString());		

		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.S.equals(tipoRelatorio)) {
			hql.append(", ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		}

		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.D.equals(tipoRelatorio)) {
			hql.append(", trunc( ise.");
			hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString());
			hql.append(')');
		}
		
		//ORDER BY
		hql.append(" order by");
		hql.append(" unf.");
		hql.append(AghUnidadesFuncionais.Fields.DESCRICAO
				.toString());
		hql.append(", exa.");
		hql.append(AelExames.Fields.DESCRICAO
				.toString());
		hql.append(", man.");
		hql.append(AelMateriaisAnalises.Fields.DESCRICAO
				.toString());

		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.D.equals(tipoRelatorio)) {
			hql.append(", trunc( ise.");
			hql.append(
					AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString())
					.append(')');
		}
		
		if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)
				|| DominioAgrupamentoTotaisExames.S.equals(tipoRelatorio)) {
			hql.append(", ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		}
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("convenio", convenio);
		query.setParameter("plano", plano);
		query.setParameter("dataInicial", dataInicial);
		query.setParameter("dataFinal", dataFinal);
		if (situacao != null) {
			query.setParameter("situacao", situacao.toString());
		}
		if (unfSeq != null) {
			query.setParameter("unfSeq", unfSeq);
		}
		if (!StringUtils.isEmpty(sigla)) {
			query.setParameter("sigla", sigla);
		}
		
		List<Object[]> list = query.list();
		List<TotalItemSolicitacaoExamesVO> listaTotalVO = new ArrayList<TotalItemSolicitacaoExamesVO>();
		if(list != null && !list.isEmpty()){
			for(Object[] obj:list){
				
				TotalItemSolicitacaoExamesVO totalVO = new TotalItemSolicitacaoExamesVO();
				
				if(obj[0] != null){
					totalVO.setUnfDescricao(obj[0].toString());
				}
				
				if(obj[1] != null){
					totalVO.setExameDescricao(obj[1].toString());
				}
				
				if(obj[2] != null){
					totalVO.setMaterialAnaliseDescricao(obj[2].toString());
				}
				
				if (DominioAgrupamentoTotaisExames.C.equals(tipoRelatorio)) {
					if (obj[3] != null) {
						totalVO.setSituacaoCodigo(obj[3].toString());
					}

					if (obj[4] != null) {
						totalVO.setDataProgramada(sdf1.format((Date) obj[4]));
					}

					if (obj[5] != null) {
						totalVO.setTotalSituacao(Integer.parseInt(obj[5]
								.toString()));
					}
				} else if (DominioAgrupamentoTotaisExames.S
						.equals(tipoRelatorio)) {
					if (obj[3] != null) {
						totalVO.setSituacaoCodigo(obj[3].toString());
					}

					if (obj[4] != null) {
						totalVO.setTotalSituacao(Integer.parseInt(obj[4]
								.toString()));
					}
				} else if (DominioAgrupamentoTotaisExames.D
						.equals(tipoRelatorio)) {
					if (obj[3] != null) {
						totalVO.setDataProgramada(sdf1.format((Date) obj[3]));
					}

					if (obj[4] != null) {
						totalVO.setTotalSituacao(Integer.parseInt(obj[4]
								.toString()));
					}
				} else {
					if (obj[3] != null) {
						totalVO.setTotalSituacao(Integer.parseInt(obj[3]
								.toString()));
					}
				}
				
				listaTotalVO.add(totalVO);
				
			}
			
		}
				
		return listaTotalVO;
	}
	
	public List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePaciente(Integer codigoSolicitacao, Short unfSeq, Boolean indColetavel) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSolicAtends.class, "vas");
		criteria.createAlias("vas." + VAelSolicAtends.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString(), "iha", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("iha." + AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), "hed", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("hed." + AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "gae", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("vas." + VAelSolicAtends.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createAlias("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe", JoinType.INNER_JOIN);
		criteria.createAlias("ufe." + AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createAlias("ufe." + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema", JoinType.INNER_JOIN);
		criteria.createAlias("ema." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pac."+ AipPacientes.Fields.NOME.toString()), "nomePaciente")
				.add(Projections.property("vas."+ VAelSolicAtends.Fields.SEQ.toString()), "soeSeq")
				.add(Projections.property("pac."+ AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("ise."+ AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()), "dataHoraEvento")
				.add(Projections.property("unf."+ AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "nomeUnidadeFuncional"));
		
		criteria.add(Restrictions.eq("man"+"."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), indColetavel));
		criteria.add(Restrictions.eq("ise"+"."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), codigoSolicitacao));
		if(unfSeq != null) {
			criteria.add(Restrictions.eq("gae"+"."+AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(EtiquetaEnvelopePacienteVO.class));
		
		return executeCriteria(criteria, 0, 1, null, false);
	}
	
	/**
	 * Obtem lista das situações
	 */
	public List<AelSitItemSolicitacoes> obterListaSituacoesItemSolicitacoes() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelSitItemSolicitacoes.class);
		return executeCriteria(criteria);
	}
	
	public AelSolicitacaoExames obterAelSolicitacaoExameComAtendimentoPeloSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE", JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UNFEXEC");
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), seq));
		
		return (AelSolicitacaoExames)executeCriteriaUniqueResult(criteria);
	}

	public AelSolicitacaoExames obterAelSolicitacaoExameComAtendimentoDiversoPeloSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV", JoinType.INNER_JOIN);		
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), seq));
		
		return (AelSolicitacaoExames)executeCriteriaUniqueResult(criteria);
	}

	public AelSolicitacaoExames obterAelSolicitacaoExameAtendimentos(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), seq));
		
		return (AelSolicitacaoExames)executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * ORADB: CURSOR P_BUSCA_MATERIAL_EXAME.CUR_MEX ESTÓRIA: #24894
	 */
	public List<String> obterDescricaoMaterialAnalise(final Integer atdSeq){
		
		final StringBuilder sql = new StringBuilder(600);

		sql.append(" SELECT DISTINCT ")		
		   .append("   MAN.").append(AelMateriaisAnalises.Fields.DESCRICAO.name())	

		   .append(" FROM ")
		   .append("       AGH.").append(AelSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" SOE ")
		   .append("     , AGH.").append(AelItemSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" ISE ")
		   .append("     , AGH.").append(AelMateriaisAnalises.class.getAnnotation(Table.class).name()).append(" MAN ")
		   .append("     , AGH.").append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" ATD ")
		   
		   .append(" WHERE 1=1 ")
		   
		   .append("  AND SOE.").append(AelSolicitacaoExames.Fields.SEQ.name()).append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name())
		   .append("  AND ISE.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.name()).append(" = MAN.").append(AelMateriaisAnalises.Fields.SEQ.name())
		   .append("  AND ATD.").append(AghAtendimentos.Fields.SEQ.name()).append("= SOE.").append(AelSolicitacaoExames.Fields.ATD_SEQ.name())
		   .append("  AND SOE.").append(AelSolicitacaoExames.Fields.UNF_SEQ.name()).append(" = ATD.").append(AghAtendimentos.Fields.UNF_SEQ.name())
		   .append("  AND SOE.").append(AelSolicitacaoExames.Fields.ATD_SEQ.name()).append(" = :PRM_ATD_SEQ ");
		
		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("PRM_ATD_SEQ", atdSeq);

		List<String> result = query.list();
		return result;
	}
	
	public Boolean localizadorValido(String localizador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.LOCALIZADOR.toString(), localizador));
		
		List<AelSolicitacaoExames> solicitacaoExames = executeCriteria(criteria);
		
		if(solicitacaoExames.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * #34384 - Obter solicitação de exames	
	 * @param atdSeq
	 * @return
	 */
	
	public List<AelSolicitacaoExames> obterSolicitacaoExamesPorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(AelSolicitacaoExames.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}

	public boolean verificaPctPendente(Integer atdSeqSelecionado,
			Integer crgSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "PCT"));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), DominioSituacaoItemSolicitacaoExame.CS.toString()));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.IND_GERADO_AUTOMATICO.toString(), true));
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeqSelecionado));
		
		List<AelItemSolicitacaoExames> itemSolicitacaoExames = executeCriteria(criteria);		
	
		if(itemSolicitacaoExames.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}

	public boolean verificaPctRealizado(Integer pacCodigoSelecionado) {

//		select *
//		from ael_solicitacao_exames soe,
//		        ael_item_solicitacao_exames ise,
//		        agh_atendimentos atd
//		where ise.soe_seq=soe.seq
//		and atd.seq=soe.atd_seq
//		and ise.ufe_ema_exa_sigla='PCT'
//		and atd.pac_codigo=XXXXXXXX
//		and ise.sit_codigo in ('AC', 'AE', 'CO', 'CS', 'EC', EX);

		DetachedCriteria criteria = DetachedCriteria.forClass(
				AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "PCT"));
		criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), Arrays.asList(
				DominioSituacaoItemSolicitacaoExame.AC.toString(),
				DominioSituacaoItemSolicitacaoExame.AE.toString(),
				DominioSituacaoItemSolicitacaoExame.CO.toString(),
				DominioSituacaoItemSolicitacaoExame.CS.toString(),
				DominioSituacaoItemSolicitacaoExame.EC.toString(),
				DominioSituacaoItemSolicitacaoExame.EX.toString())));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.COD_PACIENTE.toString(), pacCodigoSelecionado));
		
		List<AelItemSolicitacaoExames> itemSolicitacaoExames = executeCriteria(criteria);

		if (itemSolicitacaoExames.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public Date obterDataPrimeiraSolicitacaoExamePeloNumConsulta(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);

		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.RETORNO.toString(), "RET");
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numero));
		criteria.setProjection(Property.forName(AelSolicitacaoExames.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc(AelSolicitacaoExames.Fields.CRIADO_EM.toString()));

		List<Date> result = executeCriteria(criteria);
		
		if (!result.isEmpty()){
			return result.get(0);
		}
		
		return null;
	}
	
	public ResultadoExamePim2VO obterExamePim2(Integer atdSeq, String[] listaCampos) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class,"SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("PIM." + MpmPim2.Fields.SERVIDOR.toString(), "SERV");
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PIM2.toString(), "PIM");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString(), "RES");
		criteria.createAlias("RES." + AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PAR");
		criteria.createAlias("PAR." + AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "CMP");

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "LI"));
		criteria.add(Restrictions.gtProperty("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString(), 
				"PIM." + MpmPim2.Fields.DTHR_INGRESSO_UNIDADE.toString()));
		criteria.add(Restrictions.eq("RES." + AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		criteria.add(Restrictions.in("CMP." + AelCampoLaudo.Fields.NOME.toString(), listaCampos));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), 
				ResultadoExamePim2VO.Fields.DTHR_LIBERADA.toString())
				.add(Projections.property("RES." + AelResultadoExame.Fields.VALOR.toString()), 
						ResultadoExamePim2VO.Fields.VALOR.toString())
				.add(Projections.property("PAR." + AelParametroCamposLaudo.Fields.QUANTIDADE_CASAS_DECIMAIS.toString()), 
						ResultadoExamePim2VO.Fields.QTD_CASAS_DECIMAIS.toString())
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), 
						ResultadoExamePim2VO.Fields.NOME.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoExamePim2VO.class));
		criteria.addOrder(Order.desc("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		
		List<ResultadoExamePim2VO> lista = executeCriteria(criteria, 0, 1, null, false);
		if(lista != null && !lista.isEmpty()) {
			return (ResultadoExamePim2VO)CollectionUtils.get(lista, 0);
		}
		
		return null;
	}
	/**
	 * #46078 localizar de internet no sumario de alta
	 * @param atendimentoSeq
	 * @return
	 */
	public String buscarLocalizadorExamesInternet(Integer atendimentoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atendimentoSeq));
		criteria.setProjection(Projections.property(AelSolicitacaoExames.Fields.LOCALIZADOR.toString()));
		criteria.addOrder(Order.desc(AelSolicitacaoExames.Fields.SEQ.toString()));
		List<String> listaLocalizador = executeCriteria(criteria);
		if(listaLocalizador != null && !listaLocalizador.isEmpty()) {
			return listaLocalizador.get(0);
		}
		
		return null;
	}
	
	/**
	 * #41772 - C2
	 * @author marcelo.deus
	 */
    public BigDecimal obterValorLeucocitosTotais(Integer pacCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
        DetachedCriteria subquery = DetachedCriteria.forClass(AelResultadoExame.class, "REE1");
		
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE")
				.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD")
				.createAlias("ISE." + AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString(), "REE")
				.createAlias("REE." + AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL")
				.createAlias("REE." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "RCD", JoinType.LEFT_OUTER_JOIN);
        criteria.setProjection(Projections.property("REE." + AelResultadoExame.Fields.VALOR.toString()));
//        criteria.setProjection(Projections.projectionList()
//                                        .add(Projections.sqlProjection("ree3_.valor / POWER(10, COALESCE(pcl4_.qtde_casas_decimais, 0)) valor", 
//                                        new String[]{AelResultadoExame.Fields.VALOR.toString()}, new Type[]{BigDecimalType.INSTANCE})));

		subquery.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.ISE_SEQP.toString(), 
						"ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()))
				.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), 
						"ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()))
                                        .add(Restrictions.eq("REE1." + AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
				.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), 
						"REE." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn("REE." + AelResultadoExame.Fields.SEQP.toString(), subquery));

        criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "LI"))
         .add(Restrictions.eq("REE." + AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
         .add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
        criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "L"));
//        if(isPostgreSQL()){
//                        criteria.add(Restrictions.sqlRestriction(" ise1_.UFE_EMA_EXA_SIGLA='L' LIMIT 1 "));
//        } else if(isOracle()){
//                        criteria.add(Restrictions.sqlRestriction(" ise1_.UFE_EMA_EXA_SIGLA='L' and rownum = 1 "));
//        }
        subquery.setProjection(Projections.projectionList().add(Projections.sqlProjection(" MAX(REE1_.seqp)", 
                               new String[]{"alias"}, new Type[]{BigDecimalType.INSTANCE})));
        criteria.addOrder(Order.desc("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		
        BigDecimal leucocitosTotais = BigDecimal.ZERO;
        if(executeCriteria(criteria) != null && !executeCriteria(criteria).isEmpty()){
        	leucocitosTotais = BigDecimal.valueOf( (Long) executeCriteria(criteria).get(0));
        }
		
		return leucocitosTotais;
	}
	
	/**
	 * #41772 - C3
	 * @author marcelo.deus
	 */
	public String obterValorCD34(Integer pacCodigo, Integer codMaterial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
		
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE")
				.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD")
				.createAlias("ISE." + AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString(), "REE")
				.createAlias("REE." + AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL")
				.createAlias("REE." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "RCD", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("REE." + AelResultadoExame.Fields.DESCRICAO)));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(AelResultadoExame.class, "REE1");
		
		subquery.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(" MAX(REE1_.seqp)", 
						new String[]{"alias"}, new Type[]{BigDecimalType.INSTANCE})));
		
		subquery.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.ISE_SEQP.toString(), 
						"ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()))
				.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), 
						"ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()))
				.add(Restrictions.eq("REE1." + AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
				.add(Restrictions.eqProperty("REE1." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), 
						"REE." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn(("REE." + AelResultadoExame.Fields.SEQP.toString()), subquery));
		
		criteria.add(Restrictions.eq("REE." + AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
				.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "ICD34"))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), 4926))
				.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		if(codMaterial != null){
			criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), codMaterial));
		}
		criteria.add(Restrictions.sqlRestriction(" SUBSTR(ise1_.sit_codigo,1,2)  = 'LI' "));
		criteria.addOrder(Order.desc("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		if(executeCriteria(criteria) != null && !executeCriteria(criteria).isEmpty()){
			return (String) executeCriteria(criteria).get(0);
		}
		return StringUtils.EMPTY;
	}
		
		
    public Integer listarDoadorRedome(Integer soeSeq) {
        final DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");

        criteria.createCriteria("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO, "ATD", JoinType.INNER_JOIN);
        criteria.createCriteria("ATD." + AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO, "APE", JoinType.INNER_JOIN);

        criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), soeSeq));
        criteria.setProjection(Projections.property("APE." + AghAtendimentosPacExtern.Fields.DOR_SEQ.toString()));

        List<Integer> listaDoadorRedome = executeCriteria(criteria);

        if (listaDoadorRedome != null && !listaDoadorRedome.isEmpty()){

            return listaDoadorRedome.get(0);
        }
        return null;

    }

    public AelDoadorRedome obterDoadorRedomePeloSeq(Integer seq) {
        final DetachedCriteria criteria = DetachedCriteria.forClass(AelDoadorRedome.class);

        criteria.add(Restrictions.eq(AelDoadorRedome.Fields.SEQ.toString(), seq));

        return (AelDoadorRedome)executeCriteriaUniqueResult(criteria);
    }

	/** #37951 Procedure AACP_TROCA_EXAMES - CURSOR C_SOE
	 * Retorna Count Atendimentos e Solicitação de Exame
	 * @return
	 */
	public Integer obterCountAtendimentosSolicitacaoExame(Integer atdSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		
		return (Integer) executeCriteriaCount(criteria).intValue();
	}
	
	/**#37951 Procedure AACP_TROCA_EXAMES
	 * Lista procedimentos relizados
	 * @param p_old_atd_seq
	 * @param p_new_atd_seq
	 * @return
	 */
	public List<AelSolicitacaoExames> obterProcedimentosRelizado(Integer p_old_atd_seq, Integer p_new_atd_seq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
		criteria.add(Restrictions.sqlRestriction(" this_.atd_seq in (select atd.seq from agh.agh_atendimentos atd2, agh.agh_atendimentos atd where atd.seq = "+ p_old_atd_seq + " and atd2.pac_codigo = atd.pac_codigo and atd2.seq = " + p_new_atd_seq + " )"));
		return executeCriteria(criteria);
	}
	
	
	/** #37951 Obter Solicitações de Exames
	 * @param numero
	 * @return
	 */
	public List<TransferirExamesVO> obterSolicitacoesExames(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "S");
		
		criteria.createAlias("S." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "U", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("S." + AelSolicitacaoExames.Fields.SEQ.toString()), TransferirExamesVO.Fields.SEQ.toString())
				.add(Projections.property("S." + AelSolicitacaoExames.Fields.UNF_SEQ.toString()), TransferirExamesVO.Fields.UNIDADE.toString())
				.add(Projections.property("U." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), TransferirExamesVO.Fields.DESCRICAO.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("S." + FatProcedAmbRealizado.Fields.ATD_SEQ.toString(), numero));
		
		criteria.addOrder(Order.asc("S." + AelSolicitacaoExames.Fields.SEQ));
		
		criteria.setResultTransformer(Transformers.aliasToBean(TransferirExamesVO.class));
		return executeCriteria(criteria);
	}

}