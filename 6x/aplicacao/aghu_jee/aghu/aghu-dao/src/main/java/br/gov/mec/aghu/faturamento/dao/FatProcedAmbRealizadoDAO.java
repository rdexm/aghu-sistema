package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.ByteType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoProcedAmbVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadoVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadosVO;
import br.gov.mec.aghu.faturamento.vo.ParametrosGeracaoLaudoOtorrinoVO;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.internacao.vo.FatRelatorioProducaoPHIVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatArqEspelhoItemApac;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatEspelhoContaApac;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItemGrupoProcedHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatKitExsPreTrans;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedTratamento;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
public class FatProcedAmbRealizadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedAmbRealizado> {

	private static final long serialVersionUID = 4431006299701684938L;

	@Inject @New(FatProcedAmbRealizadoQueryBuilder.class) private Instance<FatProcedAmbRealizadoQueryBuilder> fatProcedAmbRealizadoQueryBuilder;
	
	/**
	 * Lista registros incluidos no mes para o caso de reprocessamento
	 * 
	 * @param indSituacao
	 * @param localCobranca
	 * @param phiSeq
	 * @param cspCnvCodigo
	 * @param cspSeq
	 * @param indOrigem
	 * @param dtHrInicio
	 * @param dtHrFim
	 */
	public List<FatProcedAmbRealizado> listarRegistrosMesProcessamento(final DominioSituacaoProcedimentoAmbulatorio[] indSituacao,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] localCobranca, final Integer[] phiSeq, final Byte cspSeq,
			final Short cspCnvCodigo, final DominioOrigemProcedimentoAmbulatorialRealizado indOrigem, final Date dtHrInicio,
			final Date dtHrFim, final Boolean isFatpCargaTriagem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString(), localCobranca));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), indOrigem));
		criteria.add(Restrictions.between(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dtHrInicio, dtHrFim));

		// se true no metodo fatpCargaTriagens()
		if (isFatpCargaTriagem) {
			criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));
			criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString()));
		}

		return executeCriteria(criteria);
	}

	/**
	 * Lista registros incluidos no mes para o caso de reprocessamento
	 * 
	 * @param indSituacao
	 * @param localCobranca
	 * @param cspSeq
	 * @param cspCnvCodigo
	 * @param indOrigem
	 * @param dtHrInicio
	 * @param dtHrFim
	 */
	public List<FatProcedAmbRealizado> listarRegistrosMesProcessamento(final DominioSituacaoProcedimentoAmbulatorio[] indSituacao,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] localCobranca, final Byte cspSeq, final Short cspCnvCodigo,
			final DominioOrigemProcedimentoAmbulatorialRealizado indOrigem, final Date dtHrInicio, final Date dtHrFim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString(), localCobranca));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString()));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME_SEQ.toString()));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString()));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), indOrigem));
		criteria.add(Restrictions.between(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dtHrInicio, dtHrFim));

		return executeCriteria(criteria);
	}

	/**
	 * Lista registros incluidos no mes para o caso de reprocessamento
	 * 
	 * @param indSituacao
	 * @param localCobranca
	 * @param phiSeq
	 * @param cspSeq
	 * @param cspCnvCodigo
	 * @param indOrigem
	 * @param dtHrInicio
	 * @param dtHrFim
	 * @return
	 */
	public List<FatProcedAmbRealizado> listarRegistrosMesProcessamento(final DominioSituacaoProcedimentoAmbulatorio[] indSituacao,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] localCobranca, final Byte cspSeq, final Short cspCnvCodigo,
			final DominioOrigemProcedimentoAmbulatorialRealizado indOrigem, final Integer phiSeq, final Date dtHrInicio, final Date dtHrFim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString(), localCobranca));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString()));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), indOrigem));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.between(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dtHrInicio, dtHrFim));

		return executeCriteria(criteria);
	}

	/**
	 * Listar registros com qtde zero
	 * 
	 * @param indSituacao
	 * @param localCobranca
	 * @param phiSeq
	 * @param cspSeq
	 * @param cspCnvCodigo
	 * @param indOrigem
	 */
	public List<FatProcedAmbRealizado> listarRegistrosMesComQtdeZero(final DominioSituacaoProcedimentoAmbulatorio indSituacao,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, final Integer[] phiSeq, final Byte cspSeq,
			final Short cspCnvCodigo, final DominioOrigemProcedimentoAmbulatorialRealizado indOrigem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString(), localCobranca));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), indOrigem));

		final Criterion qtd1 = Restrictions.eq(FatProcedAmbRealizado.Fields.QUANTIDADE.toString(), (short) 0);
		final Criterion qtd2 = Restrictions.isNull(FatProcedAmbRealizado.Fields.QUANTIDADE.toString());

		final LogicalExpression orExpr = Restrictions.or(qtd1, qtd2);
		criteria.add(orExpr);

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorConsulta(final Integer consultaNumero) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.NUMERO_CONSULTA.toString(), consultaNumero));
		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorPrhConsultaSituacao(final Integer consultaNumero) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), new DominioSituacaoProcedimentoAmbulatorio[] {
				DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, DominioSituacaoProcedimentoAmbulatorio.ENCERRADO }));

		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorSolicitacao(final Integer soeSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		result.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), soeSeq));

		return result;
	}

	public List<FatProcedAmbRealizado> obterListaPorSolicitacao(final Integer soeSeq) {

		List<FatProcedAmbRealizado> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacao(soeSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	protected DetachedCriteria obterCriteriaPorSolicitacaoConvenio(final Integer soeSeq, final Short cnvCodigo, final Byte seq) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorSolicitacao(soeSeq);
		result.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cnvCodigo));
		result.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), seq));

		return result;
	}

	public List<FatProcedAmbRealizado> obterListaPorSolicitacaoConvenio(final Integer soeSeq, final Short cnvCodigo, final Byte seq) {

		List<FatProcedAmbRealizado> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacaoConvenio(soeSeq, cnvCodigo, seq);
		result = this.executeCriteria(criteria);

		return result;
	}

	protected DetachedCriteria obterCriteriaPorSolicitacaoNaoPouE(final Integer soeSeq) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorSolicitacao(soeSeq);
		result.add(Restrictions.ne(FatProcedAmbRealizado.Fields.SITUACAO.toString(), DominioSituacaoProcedimentoAmbulatorio.APRESENTADO));
		result.add(Restrictions.ne(FatProcedAmbRealizado.Fields.SITUACAO.toString(), DominioSituacaoProcedimentoAmbulatorio.ENCERRADO));

		return result;
	}

	protected DetachedCriteria criarCriteriaPorAacConsultaProcedHospitalarOrigemSituacao(final Integer conNumero, final Integer phiSeq,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final DominioOrigemProcedimentoAmbulatorialRealizado origem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), origem));
		criteria.add(Restrictions.ne(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));
		return criteria;
	}

	public List<FatProcedAmbRealizado> buscarPorAacConsultaProcedHospitalarOrigemSituacao(final Integer conNumero, final Integer phiSeq,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final DominioOrigemProcedimentoAmbulatorialRealizado origem) {
		return executeCriteria(criarCriteriaPorAacConsultaProcedHospitalarOrigemSituacao(conNumero, phiSeq, situacao, origem));
	}

	public List<FatProcedAmbRealizado> obterPorSolicitacaoNaoPouE(final Integer soeSeq) {

		List<FatProcedAmbRealizado> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacaoNaoPouE(soeSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	public List<FatProcedAmbRealizado> buscarProcedimentosRealizadosPorAtendimento(final AghAtendimentos atendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(), atendimento));

		return this.executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorProcedHospInternosEItemSolicitacaoExameESituacao(final Short iseSeqp,
			final Integer iseSoeSeqp, final Integer phiSeq, final DominioSituacaoProcedimentoAmbulatorio situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorProcedHospInternosEItemSolicitacaoExame(final Short iseSeqp, final Integer iseSoeSeqp,
			final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		final DominioSituacaoProcedimentoAmbulatorio[] situacoes = { DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioSituacaoProcedimentoAmbulatorio.CANCELADO };
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));

		return executeCriteria(criteria);
	}

	public boolean existeFatProcedAmbRealizadoTransferida(final Short iseSeqp, final Integer iseSoeSeqp, final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME_SEQ.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), iseSoeSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeq));
		final DominioSituacaoProcedimentoAmbulatorio[] situacoes = { DominioSituacaoProcedimentoAmbulatorio.ENCERRADO,
				DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO };
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));

		return executeCriteria(criteria).size() > 0;
	}

	public List<FatProcedAmbRealizado> buscarPorItemSolicitacaoExame(final Short iseSeqp, final Integer iseSoeSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME_SEQ.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), iseSoeSeqp));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorItemSolicitacaoExameNaoFaturados(final Short iseSeqp, final Integer iseSoeSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME_SEQ.toString(), iseSeqp));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), iseSoeSeqp));
		final DominioSituacaoProcedimentoAmbulatorio[] situacoes = { DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioSituacaoProcedimentoAmbulatorio.CANCELADO };
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternos(final Integer conNumero, final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), phiSeq));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorNumeroConsultaEProcedHospInternosApresentadosExcluidos(final Integer conNumero,
			final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		final List<DominioSituacaoProcedimentoAmbulatorio> situacoes = new ArrayList<DominioSituacaoProcedimentoAmbulatorio>();
		situacoes.add(DominioSituacaoProcedimentoAmbulatorio.APRESENTADO);
		situacoes.add(DominioSituacaoProcedimentoAmbulatorio.ENCERRADO);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));

		return executeCriteria(criteria);
	}

	/**
	 * Realiza a busca por Procedimentos Abertos ou Transferidos através do número da Consulta e do código do Procedimento Interno.
	 * 
	 * @param conNumero - Número da Consulta
	 * @param phiSeq - Código do Procedimento Interno
	 * @return Lista de Procedimentos Abertos ou Transferidos
	 */
	public List<FatProcedAmbRealizado> pesquisarProcedimentosAbertosTransferidosPornumeroConsultaCodigoProcedimento(
			final Integer conNumero, final Integer phiSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		final List<DominioSituacaoProcedimentoAmbulatorio> situacoes = new ArrayList<DominioSituacaoProcedimentoAmbulatorio>();
		situacoes.add(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		situacoes.add(DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));
		
		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedimentosAmbulatoriaisRealizados(final Integer numeroConsulta,
			final DominioSituacaoProcedimentoAmbulatorio[] situacoesIgnoradas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.createAlias(FatProcedAmbRealizado.Fields.CONSULTA.toString(), FatProcedAmbRealizado.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));

		if (situacoesIgnoradas != null) {
			criteria.add(Restrictions.not(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoesIgnoradas)));
		}

		return executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaPendentesPorDtInicioAnoMes(final Date dataInicio, final Byte mes, final Short ano) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		/*
		 * where ind_situacao = 'A' and cpe_dt_hr_inicio = p_cpe_dt_hr_inicio
		 * and cpe_modulo = 'AMB' and cpe_mes = p_cpe_mes and cpe_ano =
		 * p_cpe_ano --and prh_con_numero is not null and ind_pendente = 'S';
		 */
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SITUACAO.toString(), DominioSituacaoProcedimentoAmbulatorio.ABERTO));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString(), dataInicio));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.AMB));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_MES.toString(), mes.intValue()));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_ANO.toString(), ano.intValue()));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_PENDENTE.toString(), true));
		return criteria;
	}

	public List<FatProcedAmbRealizado> buscarPendentesPorDtInicioAnoMes(final Date dataInicio, final Byte mes, final Short ano) {
		return executeCriteria(criarCriteriaPendentesPorDtInicioAnoMes(dataInicio, mes, ano));
	}

	@SuppressWarnings("unchecked")
	public List<FatProcedAmbRealizado> buscarAbertosPorDtInicioAnoMesUnid(final Date dataInicio, final Byte mes, final Short ano,
			final Date dataLiberadoEmergencia, final ConstanteAghCaractUnidFuncionais caracteristica) {
		/*
		 * from fat_proced_amb_realizados pmr, agh_caract_unid_funcionais cuf,
		 * aac_grade_agendamen_consultas grd, aac_consultas con where
		 * pmr.ind_situacao = 'A' and pmr.cpe_dt_hr_inicio = p_cpe_dt_hr_inicio
		 * and pmr.cpe_modulo = 'AMB' and pmr.cpe_mes = p_cpe_mes and
		 * pmr.cpe_ano = p_cpe_ano and pmr.prh_con_numero is not null and
		 * pmr.ind_pendente is null and pmr.dthr_realizado > p_dthr_ok and
		 * con.grd_seq = grd.seq and grd.usl_unf_seq = cuf.unf_seq and
		 * cuf.caracteristica = 'Unid Emergencia' and con.numero =
		 * pmr.prh_con_numero;
		 */
		final StringBuilder hql = new StringBuilder("select pmr from ").append(FatProcedAmbRealizado.class.getName()).append(" pmr, ")
				.append(AghCaractUnidFuncionais.class.getName()).append(" cuf where pmr.")
				.append(FatProcedAmbRealizado.Fields.CONSULTA.toString()).append('.')
				.append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()).append('.')
				.append(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString()).append(" = cuf.")
				.append(AghCaractUnidFuncionais.Fields.UNF_SEQ).append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" = :indSituacao and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dataInicio and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :modulo").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano and pmr.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" > :dataLiberadoEmergencia and pmr.")
				.append(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()).append(" is not null and pmr.")
				.append(FatProcedAmbRealizado.Fields.IND_PENDENTE.toString()).append(" is null ")
				.append(" and cuf." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString() + " = :caracteristica");
		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("indSituacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("modulo", DominioModuloCompetencia.AMB);
		query.setParameter("mes", mes.intValue());
		query.setParameter("ano", ano.intValue());
		query.setParameter("dataLiberadoEmergencia", dataLiberadoEmergencia);
		query.setParameter("caracteristica", caracteristica);

		return query.list();
	}

	public List<FatProcedAmbRealizadosVO> listarFatProcedAmbRealizado(final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento, final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame, final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq, final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem, final Short convenioId, final Byte planoId,
			final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {

		final DetachedCriteria criteria = obterCriteriaListarFatProcedAmbRealizado(competencia, paciente, procedimento, consulta,
				itemSolicitacaoExame, procCirurgia, situacao, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq, procedimentoAmbSeq,
				origem, convenioId, planoId);

		criteria.addOrder(Order.desc("PROC." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()));

		final List<FatProcedAmbRealizadosVO> result = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);

		return result;
	}

	public Long listarFatProcedAmbRealizadoCount(final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento, final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame, final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq, final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem, final Short convenioId, final Byte planoId) {

		return executeCriteriaCount(obterCriteriaListarFatProcedAmbRealizado(competencia, paciente, procedimento, consulta,
				itemSolicitacaoExame, procCirurgia, situacao, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq, procedimentoAmbSeq,
				origem, convenioId, planoId));
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private DetachedCriteria obterCriteriaListarFatProcedAmbRealizado(final FatCompetencia competencia, final AipPacientes paciente,
			final FatProcedHospInternos procedimento, final AacConsultaProcedHospitalar consulta,
			final AelItemSolicitacaoExames itemSolicitacaoExame, final MbcProcEspPorCirurgias procCirurgia,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final Short cpgCphCspCnvCodigo, final Short cpgGrcSeq,
			final Byte cpgCphCspSeq, final Short unfSeq, final Long procedimentoAmbSeq,
			final DominioOrigemProcedimentoAmbulatorialRealizado origem, final Short convenioId, final Byte planoId) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PROC");

		// Paciente
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.PACIENTE.toString(), "PAC", Criteria.LEFT_JOIN);

		// Unidade Funcional
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.UNIDADE_FUNCIONAL.toString(), "UF", Criteria.LEFT_JOIN);

		// Cirurgia
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString(), "PROC_ESP_CIR", Criteria.LEFT_JOIN);

		criteria.createAlias("PROC_ESP_CIR." + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "CIR", Criteria.LEFT_JOIN);

		criteria.createAlias("PROC_ESP_CIR." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "PROC_CIR", Criteria.LEFT_JOIN);

		// Solicitação Exame
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE", Criteria.LEFT_JOIN);

		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", Criteria.LEFT_JOIN);

		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAT", Criteria.LEFT_JOIN);

		// Consulta
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString(), "CPH", Criteria.LEFT_JOIN);
		criteria.createAlias("CPH." + AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString(), "CPH_PHI", Criteria.LEFT_JOIN);

		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI", Criteria.LEFT_JOIN);

		criteria.createAlias("PHI." + FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString(), "CGI", Criteria.LEFT_JOIN);

		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO, cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ, cpgCphCspSeq));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ, cpgGrcSeq));

		// Convenio
		criteria.createAlias("PROC." + FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", Criteria.LEFT_JOIN);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("PROC." + FatProcedAmbRealizado.Fields.SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.SEQ.toString())

				// Paciente
				.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()),
						FatProcedAmbRealizadosVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()),
						FatProcedAmbRealizadosVO.Fields.PAC_NOME.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()),
						FatProcedAmbRealizadosVO.Fields.PRONTUARIO.toString())

				.add(Projections.property("PROC." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()),
						FatProcedAmbRealizadosVO.Fields.DT_HR_REALIZADO.toString())
				.add(Projections.property("PROC." + FatProcedAmbRealizado.Fields.QUANTIDADE.toString()),
						FatProcedAmbRealizadosVO.Fields.QUANTIDADE.toString())

				// Consulta
				.add(Projections.property("CPH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString()),
						FatProcedAmbRealizadosVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("CPH_PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()),
						FatProcedAmbRealizadosVO.Fields.DESCRICAO_CONSULTA.toString())
				.add(Projections.property("CPH." + AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.PHI_SEQ_CONSULTA.toString())

				// Procedimento Realizado
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
						FatProcedAmbRealizadosVO.Fields.COD_TABELA.toString())
				.add(Projections.property("PHI." + FatProcedHospInternos.Fields.SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.PHI_SEQ_PROCED.toString())
				.add(Projections.property("PHI." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
						FatProcedAmbRealizadosVO.Fields.DESCRICAO_PROCED.toString())

				// Solic. Exame
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.SOE_SEQ.toString())
				.add(Projections.property("EXA." + AelExames.Fields.SIGLA.toString()), FatProcedAmbRealizadosVO.Fields.SIGLA.toString())
				.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO.toString()),
						FatProcedAmbRealizadosVO.Fields.DESCRICAO_EXAME.toString())
				.add(Projections.property("MAT." + AelMateriaisAnalises.Fields.SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.SEQ_MATERIAL.toString())

				// Cirurgia
				.add(Projections.property("CIR." + MbcCirurgias.Fields.SEQ.toString()),
						FatProcedAmbRealizadosVO.Fields.SEQ_CIRURGIA.toString())
				.add(Projections.property("PROC_CIR." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()),
						FatProcedAmbRealizadosVO.Fields.DESCRICAO_PROC_CIR.toString())

				// Unidade Funcional
				.add(Projections.property("UF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),
						FatProcedAmbRealizadosVO.Fields.SEQ_UF.toString())
				.add(Projections.property("UF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),
						FatProcedAmbRealizadosVO.Fields.DESCRICAO_UF.toString())

				.add(Projections.property("PROC." + FatProcedAmbRealizado.Fields.IND_ORIGEM.toString()),
						FatProcedAmbRealizadosVO.Fields.IND_ORIGEM.toString())

				.add(Projections.property("PROC." + FatProcedAmbRealizado.Fields.IND_SITUACAO.toString()),
						FatProcedAmbRealizadosVO.Fields.SITUACAO.toString())

		);

		if (competencia != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString(), competencia.getId()
					.getDtHrInicio()));
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CPE_MODULO.toString(), competencia.getId().getModulo()));

			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CPE_MES.toString(), competencia.getId().getMes()));
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CPE_ANO.toString(), competencia.getId().getAno()));
		}

		if (paciente != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PAC_CODIGO.toString(), paciente.getCodigo()));
		}

		if (procedimento != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), procedimento.getSeq()));
		}

		if (consulta != null && consulta.getId() != null && consulta.getId().getConNumero() != null) {
			criteria.add(Restrictions.eq("CPH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), consulta.getId().getConNumero()));
		}

		if (itemSolicitacaoExame != null && itemSolicitacaoExame.getId() != null) {
			if (itemSolicitacaoExame.getId().getSeqp() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME_SEQ.toString(),
						itemSolicitacaoExame.getId().getSeqp()));
			}

			if (itemSolicitacaoExame.getId().getSoeSeq() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), itemSolicitacaoExame
						.getId().getSoeSeq()));
			}
		}

		if (procCirurgia != null && procCirurgia.getId() != null) {
			if (procCirurgia.getCirurgia() != null && procCirurgia.getCirurgia().getSeq() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), procCirurgia
						.getCirurgia().getSeq()));
			}

			if (procCirurgia.getId().getEprPciSeq() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PPC_EPR_PCI_SEQ.toString(), procCirurgia.getId()
						.getEprPciSeq()));
			}

			if (procCirurgia.getId().getEprEspSeq() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PPC_EPR_ESP_SEQ.toString(), procCirurgia.getId()
						.getEprEspSeq()));
			}

			if (procCirurgia.getId().getIndRespProc() != null) {
				criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PPC_IND_RESP_PROC.toString(), procCirurgia.getId()
						.getIndRespProc()));
			}
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.UNF_SEQ, unfSeq));
		}

		if (procedimentoAmbSeq != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.SEQ, procedimentoAmbSeq));
		}

		if (origem != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.IND_ORIGEM, origem));
		}

		if (convenioId != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO, convenioId));
		}

		if (planoId != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CSP_SEQ, planoId));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedAmbRealizadosVO.class));

		return criteria;
	}

	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorSeq(final Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SEQ.toString(), seq));
		// PROCEDIMENTO_HOSPITALAR_INTERNO
		criteria.createAlias(FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(),
				FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), JoinType.LEFT_OUTER_JOIN);
		// CONVENIO_SAUDE_PLANO - CONVENIO_SAUDE
		criteria.createAlias(FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString(),
				FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString() + "."
				+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		// SERVIDOR_RESPONSAVEL - PESSOA_FISICA
		criteria.createAlias(FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString(),
				FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString(), JoinType.LEFT_OUTER_JOIN);
		// ESPECIALIDADE
		criteria.createAlias(FatProcedAmbRealizado.Fields.ESPECIALIDADE.toString(), FatProcedAmbRealizado.Fields.ESPECIALIDADE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		//UNIDADE FUNCIONAL
		criteria.createAlias(FatProcedAmbRealizado.Fields.UNIDADE_FUNCIONAL.toString(), FatProcedAmbRealizado.Fields.UNIDADE_FUNCIONAL.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		//ATENDIMENTO
		criteria.createAlias(FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(), FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		//CONSULTA HOSPITALAR 
		criteria.createAlias(FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString(), FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		// CID
		criteria.createAlias(FatProcedAmbRealizado.Fields.CID.toString(), FatProcedAmbRealizado.Fields.CID.toString(),
				JoinType.LEFT_OUTER_JOIN);
		// COMPETENCIA
		criteria.createAlias(FatProcedAmbRealizado.Fields.FAT_COMPETENCIA.toString(),
				FatProcedAmbRealizado.Fields.FAT_COMPETENCIA.toString(), JoinType.LEFT_OUTER_JOIN);
		// PACIENTE
		criteria.createAlias(FatProcedAmbRealizado.Fields.PACIENTE.toString(), FatProcedAmbRealizado.Fields.PACIENTE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		// ITEM_SOLICITACAO_EXAME - MATERIAL_ANALISE
		criteria.createAlias(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString(),
				FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString() + "."
				+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(),
				FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString() + "."
						+ AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), JoinType.LEFT_OUTER_JOIN);
		// PROC_ESP_POR_CIRURGIA - PROCEDIMENTO -
		criteria.createAlias(FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString(),
				FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString() + "."
				+ MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString()
				+ "." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), JoinType.LEFT_OUTER_JOIN);
		// PROC_ESP_POR_CIRURGIA - ESPECIALIDADE 
		criteria.createAlias(FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString() + "."
				+ MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString()
				+ "." + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), JoinType.LEFT_OUTER_JOIN);
		// PROC_ESP_POR_CIRURGIA - ESPECIALIDADE - ESPECIALIDADE
		criteria.createAlias(
				FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString() + "."
						+ MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString() + "."
						+ MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(),
				FatProcedAmbRealizado.Fields.PROC_ESP_POR_CIRURGIA.toString() + "."
						+ MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString() + "."
						+ MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), JoinType.LEFT_OUTER_JOIN);

		return (FatProcedAmbRealizado) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteriaPorAtendimento(final Integer seqAtendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(
				FatProcedAmbRealizado.Fields.ATENDIMENTO.toString() + "." + AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
		return criteria;
	}

	public List<FatProcedAmbRealizado> buscarPorAtendimento(final Integer seqAtendimento) {
		return executeCriteria(criarCriteriaPorAtendimento(seqAtendimento));
	}

	@SuppressWarnings("unchecked")
	public List<FatProcedAmbRealizado> buscarPorPacienteEmAtendimento(final Date dataInicio, final Byte mes, final Short ano,
			final Date dataLiberadoEmergencia) {
		/*
		 * from fat_proced_amb_realizados pmr, ain_atendimentos_urgencia atu
		 * where pmr.ind_situacao = 'A' and pmr.cpe_dt_hr_inicio =
		 * p_cpe_dt_hr_inicio and pmr.cpe_modulo = 'AMB' and pmr.cpe_mes =
		 * p_cpe_mes and pmr.cpe_ano = p_cpe_ano and pmr.prh_con_numero is not
		 * null and pmr.ind_pendente is null and atu.con_numero =
		 * pmr.prh_con_numero and atu.ind_paciente_em_atendimento = 'S' and
		 * atu.dt_alta_atendimento is null;
		 */
		final StringBuilder hql = new StringBuilder("select pmr from ").append(FatProcedAmbRealizado.class.getName()).append(" pmr, ")
				.append(AinAtendimentosUrgencia.class.getName()).append(" atu where pmr.")
				.append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" = :indSituacao").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dataInicio").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :modulo").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()).append(" is not null ").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.IND_PENDENTE.toString()).append(" is null ").append(" and atu.")
				.append(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString()).append(" = :indPacAtendimento")
				.append(" and atu.").append(AinAtendimentosUrgencia.Fields.DATA_ALTA_ATENDIMENTO.toString()).append(" is null ")
				.append(" and pmr.").append(FatProcedAmbRealizado.Fields.NUMERO_CONSULTA.toString()).append(" = atu.")
				.append(AinAtendimentosUrgencia.Fields.CONSULTA_NUMERO.toString());
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("indSituacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("modulo", DominioModuloCompetencia.AMB);
		query.setParameter("mes", mes.intValue());
		query.setParameter("ano", ano.intValue());
		query.setParameter("indPacAtendimento", true);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public Long buscarQuantidadeProcAmbPorDataRealizacaoConvenio(final Date dataInicio, final Date dtAtendimento, final Byte mes,
			final Short ano) {

		/*
		 * FROM fat_proced_amb_realizados pmr, fat_convenios_saude cnv WHERE (
		 * pmr.dthr_realizado > p_cpe_dthr_fim OR pmr.ind_pendente = 'S' ) AND
		 * pmr.ind_situacao = 'A' AND pmr.cpe_modulo = 'AMB' AND
		 * pmr.cpe_dt_hr_inicio = p_cpe_dthr_inicio AND pmr.cpe_mes = p_cpe_mes
		 * AND pmr.cpe_ano = p_cpe_ano AND pmr.csp_cnv_codigo = cnv.codigo AND
		 * cnv.grupo_convenio = 'S';
		 */
		final StringBuilder hql = new StringBuilder("select count(pmr) from ").append(FatProcedAmbRealizado.class.getName())
				.append(" pmr, ").append(FatConvenioSaude.class.getName()).append(" cnv where (pmr.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" > :dtAtendimento").append(" or COALESCE(pmr.")
				.append(FatProcedAmbRealizado.Fields.IND_PENDENTE.toString()).append(",'N') = :indPendente )").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" = :indSituacao").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :modulo").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dataInicio").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" = cnv.")
				.append(FatConvenioSaude.Fields.CODIGO.toString()).append(" and cnv.")
				.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtAtendimento", dtAtendimento, TimestampType.INSTANCE);
		query.setParameter("indPendente", true);
		query.setParameter("indSituacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("modulo", DominioModuloCompetencia.AMB);
		query.setParameter("dataInicio", dataInicio, TimestampType.INSTANCE);
		query.setParameter("mes", mes.intValue());
		query.setParameter("ano", ano.intValue());
		query.setParameter("grupoConvenio", DominioGrupoConvenio.S);

		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<FatProcedAmbRealizado> buscarPorDataRealizacaoConvenio(final Date dataInicio, final Date dtAtendimento, final Byte mes,
			final Short ano, final Integer primeiroRegistro, final Integer quantidadeMaxResultados) {

		/*
		 * FROM fat_proced_amb_realizados pmr, fat_convenios_saude cnv WHERE (
		 * pmr.dthr_realizado > p_cpe_dthr_fim OR pmr.ind_pendente = 'S' ) AND
		 * pmr.ind_situacao = 'A' AND pmr.cpe_modulo = 'AMB' AND
		 * pmr.cpe_dt_hr_inicio = p_cpe_dthr_inicio AND pmr.cpe_mes = p_cpe_mes
		 * AND pmr.cpe_ano = p_cpe_ano AND pmr.csp_cnv_codigo = cnv.codigo AND
		 * cnv.grupo_convenio = 'S';
		 */
		final StringBuilder hql = new StringBuilder(400).append("select pmr from ");

		hql.append(FatProcedAmbRealizado.class.getName()).append(" pmr, ").append(FatConvenioSaude.class.getName()).append(" cnv ")
				.append(" where ")

				.append(" (pmr.").append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" > :dtAtendimento or ")
				.append("  COALESCE(pmr.").append(FatProcedAmbRealizado.Fields.IND_PENDENTE.toString()).append(",'N') = :indPendente ) ")

				.append(" and pmr.").append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" = :indSituacao").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :modulo").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dataInicio").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano").append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" = cnv.")
				.append(FatConvenioSaude.Fields.CODIGO.toString()).append(" and cnv.")
				.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtAtendimento", dtAtendimento, TimestampType.INSTANCE);
		query.setParameter("indPendente", true);
		query.setParameter("indSituacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("modulo", DominioModuloCompetencia.AMB);
		query.setParameter("dataInicio", dataInicio, TimestampType.INSTANCE);
		query.setParameter("mes", mes.intValue());
		query.setParameter("ano", ano.intValue());
		query.setParameter("grupoConvenio", DominioGrupoConvenio.S);
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(quantidadeMaxResultados);

		return query.list();
	}

	// -- Talvez precise acrescentar a grade - pode haver mais de uma devido a
	// profissionais diferentes
	// -- na mesma especialidade, unidade, data e phi.
	public List<FatProcedAmbRealizadoVO> buscarPorDataGrupoCaracteristica(final Date dataInicio, final Date dataFim,
			final String caracteristica, final Short grpSeq, String formato) {
		/*
		 * SELECT pmr.seq, pmr.phi_seq , cip.VALOR_NUMERICO limite,
		 * pmr.csp_cnv_codigo, pmr.csp_seq, pmr.dthr_realizado, pmr.unf_seq,
		 * pmr.esp_seq FROM -- aac_grade_agendamen_consultas grd,
		 * FAT_TIPO_CARACT_ITENS TCT, FAT_CARACT_ITEM_PROC_HOSP CIP,
		 * fat_convenios_saude cnv, V_FAT_ASSOCIACAO_PROCEDIMENTOS VAS,
		 * fat_proced_amb_realizados pmr WHERE pmr.IND_SITUACAO = 'A' AND
		 * pmr.dthr_realizado BETWEEN p_data_ini AND p_data_fim AND
		 * pmr.ind_origem = 'CON' AND VAS.PHI_SEQ = PMR.PHI_SEQ AND
		 * VAS.CPG_CPH_CSP_CNV_CODIGO = PMR.CSP_CNV_CODIGO AND
		 * VAS.CPG_CPH_CSP_SEQ = PMR.CSP_SEQ AND VAS.IPH_IND_CONSULTA = 'S' --
		 * AND VAS.IPH_PHO_SEQ = 4 -- AMBULATORIO AND VAS.CPG_GRC_SEQ =
		 * V_GRC_SEQ AND CNV.CODIGO = PMR.CSP_CNV_CODIGO AND CNV.GRUPO_CONVENIO
		 * = 'S' -- sus AND CIP.IPH_SEQ = VAS.IPH_SEQ AND CIP.IPH_PHO_SEQ =
		 * VAS.IPH_PHO_SEQ AND TCT.SEQ = CIP.TCT_SEQ AND TCT.CARACTERISTICA =
		 * 'Quantidade minima grupo' ORDER BY pmr.phi_seq, pmr.dthr_realizado,
		 * pmr.unf_seq, pmr.esp_seq;
		 */

		/*
		 * final StringBuilder hql = new StringBuilder(
		 * "select pmr.seq as seq, pmr.procedimentoHospitalarInterno.seq as phiSeq, cip.valorNumerico as valorNumerico, pmr.convenioSaudePlano.id.cnvCodigo as cspCnvCodigo, pmr.convenioSaudePlano.id.seq as cspSeq, pmr.dthrRealizado as dthrRealizado, pmr.unidadeFuncional.seq as unfSeq, pmr.especialidade.seq as espSeq from "
		 * )
		 * .append(FatProcedAmbRealizado.class.getName()).append(" pmr, ").append
		 * (FatTipoCaractItens.class.getName()).append(" tct, ")
		 * .append(FatCaractItemProcHosp
		 * .class.getName()).append(" cip, ").append
		 * (FatConvenioSaude.class.getName()).append(" cnv, ")
		 * .append(VFatAssociacaoProcedimento
		 * .class.getName()).append(" vas where pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.SITUACAO.toString()).append(" = :situacao and pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.DTHR_REALIZADO.toString()).append
		 * (" between :dtInicio and :dtFim and pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.IND_ORIGEM.toString()).append(" = :indOrigem and pmr.")
		 * .append
		 * (FatProcedAmbRealizado.Fields.PHI_SEQ.toString()).append(" = vas.")
		 * .append
		 * (VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()).append(" and pmr."
		 * )
		 * .append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append
		 * (" = vas.")
		 * .append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO
		 * .toString()).append(" and pmr.")
		 * .append(FatProcedAmbRealizado.Fields.
		 * CSP_SEQ.toString()).append(" = vas.")
		 * .append(VFatAssociacaoProcedimento
		 * .Fields.CPG_CPH_CSP_SEQ.toString()).append(" and vas.")
		 * .append(VFatAssociacaoProcedimento
		 * .Fields.IPH_IND_CONSULTA.toString())
		 * .append(" = :indConsulta and vas.")
		 * .append(VFatAssociacaoProcedimento
		 * .Fields.CPG_GRC_SEQ.toString()).append(" = :grpSeq and pmr.")
		 * .append(
		 * FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append
		 * (" = cnv.").append(FatConvenioSaude.Fields.CODIGO.toString())
		 * .append(
		 * " and cnv.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString
		 * ()).append(" = :grpConvenio and vas.")
		 * .append(VFatAssociacaoProcedimento
		 * .Fields.IPH_SEQ.toString()).append(" = cip.")
		 * .append(FatCaractItemProcHosp
		 * .Fields.IPH_SEQ.toString()).append(" and vas.")
		 * .append(VFatAssociacaoProcedimento
		 * .Fields.IPH_PHO_SEQ.toString()).append(" = cip.")
		 * .append(FatCaractItemProcHosp
		 * .Fields.IPH_PHO_SEQ.toString()).append(" and cip.")
		 * .append(FatCaractItemProcHosp
		 * .Fields.TCT_SEQ.toString()).append(" = tct."
		 * ).append(FatTipoCaractItens.Fields.SEQ.toString())
		 * .append(" and tct."
		 * ).append(FatTipoCaractItens.Fields.CARACTERISTICA.toString
		 * ()).append(" = :caracteristica order by pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.PHI_SEQ.toString()).append(", pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.DTHR_REALIZADO.toString()).append(", pmr.")
		 * .append(FatProcedAmbRealizado
		 * .Fields.UNF_SEQ.toString()).append(", pmr."
		 * ).append(FatProcedAmbRealizado.Fields.ESP_SEQ.toString());
		 */

		StringBuffer hql = new StringBuffer(612);
		hql.append(" select ");
		hql.append("  pmr." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString() + " as phiSeq");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.SEQ.toString() + " as seq");
		hql.append(", cip." + FatCaractItemProcHosp.Fields.VALOR_NUMERICO.toString() + " as valorNumerico");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString() + " as cspCnvCodigo");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.CSP_SEQ.toString() + " as cspSeq");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString() + " as dthrRealizado");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.UNF_SEQ.toString() + " as unfSeq");
		hql.append(", pmr." + FatProcedAmbRealizado.Fields.ESP_SEQ.toString() + " as espSeq");

		// from
		hql.append(" from ");
		hql.append(FatTipoCaractItens.class.getName());
		hql.append(" as tct");

		hql.append(", ").append(FatCaractItemProcHosp.class.getName());
		hql.append(" as cip ");

		hql.append(", ").append(FatConvenioSaude.class.getName());
		hql.append(" as cnv ");

		hql.append(", ").append(VFatAssociacaoProcedimento.class.getName());
		hql.append(" as vas ");

		hql.append(", ").append(FatProcedAmbRealizado.class.getName());
		hql.append(" as pmr ");

		// where
		hql.append(" where pmr.");
		hql.append(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString());

		// originalmente era setado o parametro, contudo, rodando contra oracle
		// altera a ordem dos registro e isso influencia no resultado.
		// hql.append(" between :dtInicio and :dtFim");
		hql.append(" between to_date('");
		hql.append(DateUtil.obterDataFormatada(dataInicio, formato));
		hql.append("','dd/mm/yyyy hh24:mi:ss')");

		hql.append(" and to_date('");
		hql.append(DateUtil.obterDataFormatada(dataFim, formato));
		hql.append("','dd/mm/yyyy hh24:mi:ss')");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString());
		hql.append(" = :indOrigem");

		hql.append(" and vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString());
		hql.append(" = pmr.");
		hql.append(FatProcedAmbRealizado.Fields.PHI_SEQ.toString());

		hql.append(" and vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString());

		hql.append(" and vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CSP_SEQ.toString());

		hql.append(" and vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_IND_CONSULTA.toString());
		hql.append(" = :indConsulta");

		hql.append(" and vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :grpSeq");

		hql.append(" and cnv.");
		hql.append(FatConvenioSaude.Fields.CODIGO.toString());
		hql.append(" = pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString());

		hql.append(" and cnv.");
		hql.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString());
		hql.append(" = :grpConvenio");

		hql.append(" and cip.");
		hql.append(FatCaractItemProcHosp.Fields.IPH_SEQ.toString());
		hql.append(" = vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString());

		hql.append(" and cip.");
		hql.append(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString());
		hql.append(" = vas.");
		hql.append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString());

		hql.append(" and tct.");
		hql.append(FatTipoCaractItens.Fields.SEQ.toString());
		hql.append(" = cip.");
		hql.append(FatCaractItemProcHosp.Fields.TCT_SEQ.toString());

		hql.append(" and tct.");
		hql.append(FatTipoCaractItens.Fields.CARACTERISTICA.toString());
		hql.append(" = :caracteristica");

		// order by
		hql.append(" order by ");
		hql.append("  pmr.");
		hql.append(FatProcedAmbRealizado.Fields.PHI_SEQ.toString());
		hql.append(", pmr.");
		hql.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString());
		hql.append(", pmr.");
		hql.append(FatProcedAmbRealizado.Fields.UNF_SEQ.toString());
		hql.append(", pmr.");
		hql.append(FatProcedAmbRealizado.Fields.ESP_SEQ.toString());

		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("situacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		// query.setParameter("dtInicio", dataInicio, TimestampType.INSTANCE);
		// query.setParameter("dtFim", dataFim, TimestampType.INSTANCE);
		query.setParameter("indOrigem", DominioOrigemProcedimentoAmbulatorialRealizado.CON);
		query.setParameter("indConsulta", Boolean.TRUE);
		query.setParameter("grpSeq", grpSeq);
		query.setParameter("grpConvenio", DominioGrupoConvenio.S);
		query.setParameter("caracteristica", caracteristica);
		query.setResultTransformer(Transformers.aliasToBean(FatProcedAmbRealizadoVO.class));

		return query.list();
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	/**
	 * ORADB: CURSOR C_PROC_AMB PROCEDURE RN_PMRP_GERA_NEW
	 */
	public List<FatEspelhoProcedAmbVO> buscarFatEspelhoProcedAmbVO(final Date dthrRealizado, final DominioModuloCompetencia modulo,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final Date dtHrInicio, final Integer mes, final Integer ano,
			final DominioGrupoConvenio grupoConvenio) {

		final StringBuilder hql = new StringBuilder(1100);

		hql.append("SELECT ")
				.append("  ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString())
				.append(" as nroCartaoSaude ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.SEXO.toString())
				.append(" as sexo ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.DATA_NASCIMENTO.toString())
				.append(" as dtNascimento ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.COR.toString())
				.append(" as cor ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CID.toString())
				.append('.')
				.append(AghCid.Fields.CODIGO.toString())
				.append(" as codigoCid ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.SEQ.toString())
				.append(" as seq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString())
				.append(" as dthrRealizado ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString())
				.append(" as localCobranca ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.QUANTIDADE.toString())
				.append(" as quantidade ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.VALOR.toString())
				.append(" as valor ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString())
				.append(" as mes ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString())
				.append(" as ano ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString())
				.append(" as dtHrInicio ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString())
				.append('.')
				.append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
				.append(" as iseSoeSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.ITEM_SOLICITACAO_EXAME.toString())
				.append('.')
				.append(AelItemSolicitacaoExames.Fields.SEQP.toString())
				.append(" as iseSeqp ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString())
				.append('.')
				.append(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString())
				.append(" as prhConNumero ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString())
				.append('.')
				.append(AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString())
				.append(" as prhPhiSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PHI_SEQ.toString())
				.append(" as phiSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.UNIDADE_FUNCIONAL.toString())
				.append('.')
				.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
				.append(" as unfSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.ESPECIALIDADE.toString())
				.append('.')
				.append(AghEspecialidades.Fields.SEQ.toString())
				.append(" as espSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.CODIGO.toString())
				.append(" as codigoPac ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.NOME.toString())
				.append(" as nomePac ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString())
				.append('.')
				.append(FatConvenioSaudePlano.Fields.CODIGO.toString())
				.append(" as cspCnvCodigo ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.CONVENIO_SAUDE_PLANO.toString())
				.append('.')
				.append(FatConvenioSaudePlano.Fields.SEQ.toString())
				.append(" as cspSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString())
				.append(" as indOrigem ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString())
				.append('.')
				.append(RapServidores.Fields.MATRICULA.toString())
				.append(" as matriculaResp ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString())
				.append('.')
				.append(RapServidores.Fields.VINCULO.toString())
				.append('.')
				.append(RapVinculos.Fields.CODIGO.toString())
				.append(" as vinCodigoResp ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString())
				.append(" as ppcCrgSeq ")
				.append(" ,ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append('.')
				.append(AipPacientes.Fields.NACIONALIDADE_CODIGO.toString())
				.append(" as codigoNac ")
				.append(" ,trunc(ent.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString())
				.append(") as dthrRealizadoTruncado  ")

				.append(" FROM ")
				.append(FatProcedAmbRealizado.class.getName())
				.append(" as ent ")
				.append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append(" as pac  ")
				.append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.CID.toString())
				.append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString())
				.append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString())

				.append(" WHERE 1=1 ")

				// .append("pac." +
				// AipPacientes.Fields.NOME.toString()).append(" = 'MOACIR RCKASN MWUTCUN6' AND ")
				// .append("pac." +
				// AipPacientes.Fields.NOME.toString()).append(" = 'JURACI OXFHK XQWFNHK EVXLNRX JZLZVVZ3' AND ")
				// .append("ent." +
				// FatProcedAmbRealizado.Fields.SEQUENCE.toString()).append(" = 29811341 AND ")
				// .append("ent." +
				// FatProcedAmbRealizado.Fields.SEQUENCE.toString()).append(" in (29707926,29710663) AND ")
				// .append("ent." +
				// FatProcedAmbRealizado.Fields.PHI_SEQ.toString()).append(" = 14452 AND ")

				// TODO ESCHWEIGERT 26/06/2013 --> Criado para filtrar os
				// registros a serem encerrados
				// .append(" AND ent.").append(FatProcedAmbRealizado.Fields.PHI_SEQ.toString()).append(" = :PHI_SEQ")
				// .append(" AND TRUNC(ent.").append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(") = :DTHR_REALIZADO ")
				// .append(" AND ent.").append(FatProcedAmbRealizado.Fields.ESPECIALIDADE.toString())
				// .append('.').append(AghEspecialidades.Fields.SEQ.toString()).append(" = :ESP_SEQ and ")

				.append(" AND ent.").append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :modulo ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" <= :dthrRealizado ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString()).append(" = :situacao ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString()).append(" <> :localCobranca ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dtHrInicio ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano ").append(" AND ent.")
				.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" IN (SELECT cnv.codigo FROM ")
				.append(FatConvenioSaude.class.getName()).append(" cnv ").append(" WHERE ")
				.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio").append(" )")
				.append("  order by 21, 30 ");

		final Query query = createHibernateQuery(hql.toString());
		query.setTimestamp("dthrRealizado", dthrRealizado);
		query.setParameter("situacao", situacao);
		query.setParameter("localCobranca", localCobranca);
		query.setTimestamp("dtHrInicio", dtHrInicio);
		query.setParameter("mes", mes);
		query.setParameter("ano", ano);
		query.setParameter("grupoConvenio", grupoConvenio);
		query.setParameter("modulo", modulo);
		query.setResultTransformer(Transformers.aliasToBean(FatEspelhoProcedAmbVO.class));

		// TODO ESCHWEIGERT 26/06/2013 --> Criado para filtrar os registros a
		// serem encerrados
		// query.setInteger("PHI_SEQ", 3928);
		// query.setShort("ESP_SEQ", Short.valueOf("152"));
		// query.setDate("DTHR_REALIZADO",
		// DateUtil.truncaData(DateMaker.obterData(2012, 10, 06)));

		// Foi comentado por enquanto devido a problemas quando é encerramento
		// query.setFirstResult(firstResult);
		// query.setMaxResults(maxResult);

		return query.list();
	}

	public Long buscarFatEspelhoProcedAmbCount(final Date dthrRealizado, final DominioModuloCompetencia modulo,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final Date dtHrInicio, final Integer mes, final Integer ano,
			final DominioGrupoConvenio grupoConvenio) {
		/*
		 * CURSOR c_proc_amb(pp_dt_fim IN DATE) IS SELECT pac.nro_cartao_saude ,
		 * pac.sexo , pac.nome , pac.dt_nascimento
		 * ,DECODE(pac.cor,'B',1,'P',2,'M',3,'A',4,'I',5,'O',99,99) raca ,
		 * cid.codigo cid_codigo ,
		 * TRUNC(MONTHS_BETWEEN(PMR.DTHR_REALIZADO,pac.dt_nascimento)/12)
		 * idade_pac , pmr.SEQ , pmr.DTHR_REALIZADO , pmr.LOCAL_COBRANCA ,
		 * pmr.QUANTIDADE , pmr.VALOR , pmr.CPE_MES , pmr.CPE_ANO ,
		 * pmr.CPE_DT_HR_INICIO , pmr.ISE_SEQP , pmr.ISE_SOE_SEQ ,
		 * pmr.PRH_CON_NUMERO , pmr.PRH_PHI_SEQ , pmr.PHI_SEQ , pmr.UNF_SEQ ,
		 * pmr.ESP_SEQ , pmr.PAC_CODIGO , pmr.CSP_CNV_CODIGO , pmr.CSP_SEQ ,
		 * pmr.IND_ORIGEM , pmr.ser_matricula_resp , pmr.ser_vin_codigo_resp
		 * ,DECODE(aghc_ver_caract_unf(pmr.unf_seq,'Unid Emergencia'),'S', 2, 1)
		 * carater_atendimento ,pmr.ppc_crg_seq ,pac.NAC_CODIGO -- Marina
		 * 24/01/2011 FROM agh_cids cid, aip_pacientes pac,
		 * fat_proced_amb_realizados pmr WHERE pmr.dthr_realizado <= pp_dt_fim
		 * -- por hco AND pmr.ind_situacao = 'A' AND pmr.cpe_modulo = 'AMB' AND
		 * pmr.local_cobranca <> 'M' -- Marina 22/09/2009- Desprezar exames
		 * SISMAMA AND pmr.cpe_dt_hr_inicio = p_cpe_dt_hr_inicio AND pmr.cpe_mes
		 * = p_cpe_mes AND pmr.cpe_ano = p_cpe_ano AND pmr.csp_cnv_codigo IN
		 * (SELECT cnv.codigo FROM fat_convenios_saude cnv WHERE
		 * cnv.grupo_convenio = 'S') AND pac.codigo(+) = pmr.pac_codigo AND
		 * cid.seq(+) = pmr.cid_seq ORDER BY pmr.PAC_CODIGO,--pmr.PHI_SEQ,
		 * TRUNC(pmr.DTHR_REALIZADO); --marcia -- FOR UPDATE OF
		 * pmr.ind_situacao;
		 */
		final StringBuilder hql = new StringBuilder("SELECT count(*) FROM ").append(FatProcedAmbRealizado.class.getName())
				.append(" as ent LEFT OUTER JOIN ent.").append(FatProcedAmbRealizado.Fields.PACIENTE.toString())
				.append(" LEFT OUTER JOIN ent.").append(FatProcedAmbRealizado.Fields.CID.toString()).append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString()).append(" LEFT OUTER JOIN ent.")
				.append(FatProcedAmbRealizado.Fields.SERVIDOR_RESPONSAVEL.toString()).append(" WHERE ent.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" <= :dthrRealizado AND ent.")
				.append(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString()).append(" = :situacao AND ent.")
				.append(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString()).append(" <> :localCobranca AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dtHrInicio AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :mes AND ent.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :ano AND ent.")
				.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" IN (SELECT cnv.codigo FROM ")
				.append(FatConvenioSaude.class.getName()).append(" cnv WHERE ").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString())
				.append(" = :grupoConvenio)");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrRealizado", dthrRealizado);
		query.setParameter("situacao", situacao);
		query.setParameter("localCobranca", localCobranca);
		query.setParameter("dtHrInicio", dtHrInicio);
		query.setParameter("mes", mes);
		query.setParameter("ano", ano);
		query.setParameter("grupoConvenio", grupoConvenio);
		final Object obj = query.uniqueResult();
		// query.setResultTransformer(Transformers.aliasToBean(FatEspelhoProcedAmbVO.class));
		return (Long) obj;
	}

	// private List<FatProcedAmbRealizadoVO>
	// processaProjecaoFatProcedAmbRealizadoVO(@SuppressWarnings("rawtypes")
	// final List list) {
	// if (list == null) {
	// return new ArrayList<FatProcedAmbRealizadoVO>(0);
	// }
	// final List<FatProcedAmbRealizadoVO> retorno = new
	// ArrayList<FatProcedAmbRealizadoVO>(list.size());
	// for (final Object obj : list) {
	// final FatProcedAmbRealizadoVO ambRealizadVO = new
	// FatProcedAmbRealizadoVO();
	// final Object[] res = (Object[]) obj;
	// ambRealizadVO.setSeq((Long) res[0]);
	// ambRealizadVO.setPhiSeq((Integer) res[1]);
	// ambRealizadVO.setValorNumerico((Integer) res[2]);
	// ambRealizadVO.setCspCnvCodigo((Short) res[3]);
	// ambRealizadVO.setCspSeq((Byte) res[4]);
	// ambRealizadVO.setDthrRealizado((Date) res[5]);
	// ambRealizadVO.setUnfSeq((Short) res[6]);
	// ambRealizadVO.setEspSeq((Short) res[7]);
	// retorno.add(ambRealizadVO);
	// }
	// return retorno;
	// }

	private DetachedCriteria criarCriteriaPorSituacaoData(final Date dataInicio, final Date dataFim,
			final DominioSituacaoProcedimentoAmbulatorio situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.between(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dataInicio, dataFim));
		// criteria.add(Restrictions.sqlRestriction("{alias}.DTHR_REALIZADO > ?",
		// dataInicio, TimestampType.INSTANCE));
		// criteria.add(Restrictions.sqlRestriction("{alias}.DTHR_REALIZADO < ?",
		// dataFim, TimestampType.INSTANCE));
		// criteria.add(Restrictions.sqlRestriction(sql, value, type))
		// criteria.addOrder(Order.asc(FatProcedAmbRealizado.Fields.SEQ.toString()));
		return criteria;
	}

	public List<FatProcedAmbRealizado> buscarPorSituacaoData(final Date dataInicio, final Date dataFim,
			final DominioSituacaoProcedimentoAmbulatorio situacao) {
		return executeCriteria(criarCriteriaPorSituacaoData(dataInicio, dataFim, situacao));
	}

	private DetachedCriteria criarCriteriaPorSeqSituacao(final Long seq, final DominioSituacaoProcedimentoAmbulatorio situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));
		return criteria;
	}

	public FatProcedAmbRealizado buscarPorSeqSituacao(final Long seq,
			final DominioSituacaoProcedimentoAmbulatorio situacaoProcedimentoAmbulatorio) {
		final Object obj = executeCriteriaUniqueResult(criarCriteriaPorSeqSituacao(seq, situacaoProcedimentoAmbulatorio));
		if (obj == null) {
			return null;
		}
		return (FatProcedAmbRealizado) obj;
	}

	private DetachedCriteria criarCriteriabuscarEncerramentoPmr(final Long pmrSeq,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, final Date dataInicio, final Byte cpeMes,
			final Short cpeAno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SEQ.toString(), pmrSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.LOCAL_COBRANCA.toString(), localCobranca));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString(), dataInicio));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.AMB));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_MES.toString(), cpeMes.intValue()));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CPE_ANO.toString(), cpeAno.intValue()));

		return criteria;
	}

	public FatProcedAmbRealizado buscarEncerramentoPmr(final Long pmrSeq,
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca, final Date dataInicio, final Byte cpeMes,
			final Short cpeAno) {
		final Object obj = executeCriteriaUniqueResult(criarCriteriabuscarEncerramentoPmr(pmrSeq, localCobranca, dataInicio, cpeMes, cpeAno));
		if (obj == null) {
			return null;
		}
		return (FatProcedAmbRealizado) obj;
	}

	private DetachedCriteria criarCriteriaPorConsultaAtendimento(final Integer numero, final Integer seqAtendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.NUMERO_CONSULTA.toString(), numero));
		criteria.add(Restrictions.ne(FatProcedAmbRealizado.Fields.ATD_SEQ.toString(), seqAtendimento));
		return criteria;
	}

	public List<FatProcedAmbRealizado> buscarPorConsultaAtendimento(final Integer numero, final Integer seqAtendimento) {
		return executeCriteria(criarCriteriaPorConsultaAtendimento(numero, seqAtendimento));
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorCodigoPacientePrhConNumeroNulo(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizados(final Integer seqSolicitacaoExame,
			final DominioSituacaoProcedimentoAmbulatorio[] situacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SOLICITACAO_EXAME_SEQ.toString(), seqSolicitacaoExame));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacoes));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoPorSituacaoModuloDthrInicioMesAnoGrupoConvenio(
			final DominioSituacaoProcedimentoAmbulatorio situacao, final DominioModuloCompetencia modulo, final Date cpeDtHrInicio,
			final Integer mes, final Integer ano, final DominioGrupoConvenio grupoConvenio) {

		List<FatProcedAmbRealizado> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select pmr");

		// from
		hql.append(" from ");
		hql.append(FatProcedAmbRealizado.class.getName());
		hql.append(" as pmr");
		hql.append(", ").append(FatConvenioSaude.class.getName());
		hql.append(" as cnv ");

		// where
		hql.append(" where pmr.");
		hql.append(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString());
		hql.append(" = :modulo");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString());
		hql.append(" = :cpeDtHrInicio");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CPE_MES.toString());
		hql.append(" = :mes");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString());
		hql.append(" = :ano");

		hql.append(" and pmr.");
		hql.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString());
		hql.append(" = cnv.");
		hql.append(FatConvenioSaude.Fields.CODIGO.toString());

		hql.append(" and cnv.");
		hql.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString());
		hql.append(" = :grupoConvenio");

		// query
		query = createHibernateQuery(hql.toString());
		query.setParameter("situacao", situacao);
		query.setParameter("cpeDtHrInicio", cpeDtHrInicio);
		query.setParameter("ano", ano);
		query.setParameter("mes", mes);
		query.setParameter("modulo", modulo);
		query.setParameter("grupoConvenio", grupoConvenio);

		result = query.list();

		return result;
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadosPrhConNumeroNulo(final Integer pacCodigo, final AghAtendimentos atendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));

		return executeCriteria(criteria);
	}

	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorPrhConNumero(final Integer prhConNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), prhConNumero));

		return (FatProcedAmbRealizado) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Lista FatProcedAmbRealizado por Seq e Situacao
	 * 
	 * @param seqs
	 * @param situacao
	 * @return
	 */
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorSeqESituacao(final Object[] seqs,
			final DominioSituacaoProcedimentoAmbulatorio situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		if (seqs != null) {
			criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.SEQ.toString(), seqs));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.SITUACAO.toString(), situacao));
		}
		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoOrigemConsultaPorNumeroConsulta(final Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), DominioOrigemProcedimentoAmbulatorialRealizado.CON));
		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(final Integer conNumero,
			final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.IND_ORIGEM.toString(), DominioOrigemProcedimentoAmbulatorialRealizado.CON));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<PreviaDiariaFaturamentoVO> obterPreviaDiariaFaturamento(final FatCompetencia competencia, final boolean isPDF,
			final Short vUnf101, final Short vUnf33, final Short vCodCC, final String fcfCodigo) {
		if (isPDF) {
			final StringBuffer sql = new StringBuffer(500);

			sql.append(" select ").append("     SGR.GRP_SEQ   AS ").append(PreviaDiariaFaturamentoVO.Fields.GRUPO.toString())
					.append("   , GRP.DESCRICAO AS ").append(PreviaDiariaFaturamentoVO.Fields.GRUPO_DESC.toString())
					.append("   , SGR.SUB_GRUPO AS ").append(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO.toString())
					.append("   , SGR.DESCRICAO AS ").append(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO_DESC.toString())
					.append("   , FOG.CODIGO    AS ").append(PreviaDiariaFaturamentoVO.Fields.FOG_COD.toString())
					.append("   , FOG.DESCRICAO AS ").append(PreviaDiariaFaturamentoVO.Fields.FOG_DESC.toString())
					.append("   , SUM(AEA.QUANTIDADE) AS ").append(PreviaDiariaFaturamentoVO.Fields.QUANTIDADE.toString())
					.append("   , SUM(AEA.VLR_PROC) 	 AS ").append(PreviaDiariaFaturamentoVO.Fields.TOTAL.toString())
					.append("   , IPH.SEQ AS ").append(PreviaDiariaFaturamentoVO.Fields.IPH_SEQ.toString()).append("   , IPH.PHO_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.IPH_PHO_SEQ.toString()).append("   , IPH.FCF_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FCF_SEQ.toString()).append(" FROM ").append("     AGH.")
					.append(FatGrupo.class.getAnnotation(Table.class).name()).append(" GRP ").append("   , AGH.")
					.append(FatSubGrupo.class.getAnnotation(Table.class).name()).append(" SGR ").append("   , AGH.")
					.append(FatFormaOrganizacao.class.getAnnotation(Table.class).name()).append(" FOG ").append("   , AGH.")
					.append(FatItemGrupoProcedHosp.class.getAnnotation(Table.class).name()).append(" IGP ").append("   , AGH.")
					.append(FatArqEspelhoProcedAmb.class.getAnnotation(Table.class).name()).append(" AEA ")
					.append("     LEFT OUTER JOIN AGH.").append(FatCaractFinanciamento.class.getAnnotation(Table.class).name())
					.append(" FCF ON FCF.SEQ = AEA.FCF_SEQ ").append("   , AGH.")
					.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH ").append("   , AGH.")
					.append(FatCompetencia.class.getAnnotation(Table.class).name()).append(" CPE ")

					.append(" WHERE ")

					.append("  	 AEA.CPE_DT_HR_INICIO = CPE.DT_HR_INICIO ").append(" AND AEA.CPE_ANO = CPE.ANO ")
					.append(" AND AEA.CPE_MES  = CPE.MES ").append(" AND AEA.CPE_MODULO = CPE.MODULO ")
					.append(" AND IPH.PHO_SEQ = AEA.IPH_PHO_SEQ ").append(" AND IPH.SEQ = AEA.IPH_SEQ ")
					.append(" AND IGP.IPH_PHO_SEQ = IPH.PHO_SEQ ").append(" AND IGP.IPH_SEQ = IPH.SEQ ")
					.append(" AND FCF.CODIGO = :P_FCF_CODIGO ").append(" AND CPE.MODULO = :P_CPE_MODULO ")
					.append(" AND CPE.MES = :P_CPE_MES ")
					.append(" AND CPE.ANO = :P_CPE_ANO ")
					.append(" AND CPE.DT_HR_INICIO =  :P_CPE_DT_HR_INICIO ")
					.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ ")
					.append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO ")
					.append(" AND FOG.CODIGO = IPH.FOG_CODIGO ")
					.append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ ")
					.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO ")
					.append(" AND GRP.SEQ = SGR.GRP_SEQ ")

					.append("     GROUP BY ")
					.append("          SGR.GRP_SEQ ")
					.append("        , GRP.DESCRICAO ")
					.append("        , SGR.SUB_GRUPO ")
					.append("        , SGR.DESCRICAO ")
					.append("        , FOG.CODIGO ")
					.append("        , FOG.DESCRICAO ")
					.append("		, IPH.SEQ")
					.append("		, IPH.PHO_SEQ")
					.append("		, IPH.FCF_SEQ")

					.append(" UNION ")

					.append(" select ")
					.append("     SGR.GRP_SEQ   AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.GRUPO.toString())
					.append("   , GRP.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.GRUPO_DESC.toString())
					.append("   , SGR.SUB_GRUPO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO.toString())
					.append("   , SGR.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO_DESC.toString())
					.append("   , FOG.CODIGO    AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FOG_COD.toString())
					.append("   , FOG.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FOG_DESC.toString())
					.append("   , SUM(AEI.QUANTIDADE) AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.QUANTIDADE.toString())
					.append("   , SUM(AEI.VLR_PROC) 	 AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.TOTAL.toString())
					// <-- TODO: Problema: esse total eh Double mas esta
					// armazenando valor BigDecimal
					.append("   , IPH.SEQ AS ").append(PreviaDiariaFaturamentoVO.Fields.IPH_SEQ.toString()).append("   , IPH.PHO_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.IPH_PHO_SEQ.toString()).append("   , IPH.FCF_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FCF_SEQ.toString()).append(" FROM ").append("     AGH.")
					.append(FatGrupo.class.getAnnotation(Table.class).name()).append(" GRP ").append("   , AGH.")
					.append(FatSubGrupo.class.getAnnotation(Table.class).name()).append(" SGR ").append("   , AGH.")
					.append(FatFormaOrganizacao.class.getAnnotation(Table.class).name()).append(" FOG ").append("   , AGH.")
					.append(FatEspelhoContaApac.class.getAnnotation(Table.class).name()).append(" ECA ").append("   , AGH.")
					.append(FatCaractFinanciamento.class.getAnnotation(Table.class).name()).append(" FCF ")
					.append("     LEFT OUTER JOIN AGH.").append(FatArqEspelhoItemApac.class.getAnnotation(Table.class).name())
					.append("  AEI ON FCF.SEQ = AEI.FCF_SEQ ").append("   , AGH.")
					.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH ").append("   , AGH.")
					.append(FatItemGrupoProcedHosp.class.getAnnotation(Table.class).name()).append(" IGP ").append("   , AGH.")
					.append(FatCompetencia.class.getAnnotation(Table.class).name()).append(" CPE ")

					.append(" WHERE ").append("     IPH.PHO_SEQ = AEI.IPH_PHO_SEQ ").append(" AND IPH.SEQ = AEI.IPH_SEQ ")
					.append(" AND IGP.IPH_PHO_SEQ = IPH.PHO_SEQ ").append(" AND IGP.IPH_SEQ = IPH.SEQ ")
					.append(" AND AEI.CAP_ATM_NUMERO = ECA.CAP_ATM_NUMERO ").append(" AND AEI.CAP_SEQP = ECA.CAP_SEQP ")
					.append(" AND AEI.IEC_SEQP_CONTINUACAO = ECA.SEQP_CONTINUACAO ")
					.append(" AND ECA.CPE_DT_HR_INICIO =  CPE.DT_HR_INICIO ").append(" AND ECA.CPE_ANO = CPE.ANO ")
					.append(" AND ECA.CPE_MES = CPE.MES ").append(" AND ECA.CPE_MODULO = CPE.MODULO ")
					.append(" AND CPE.MODULO IN (:P_CPE_MODULO_LIST) ").append(" AND CPE.MES = :P_CPE_MES ")
					.append(" AND CPE.ANO = :P_CPE_ANO ").append(" AND FCF.CODIGO = :P_FCF_CODIGO ")
					.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ ").append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO ")
					.append(" AND FOG.CODIGO = IPH.FOG_CODIGO ").append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ ")
					.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO ").append(" AND GRP.SEQ = SGR.GRP_SEQ ")

					.append("     GROUP BY ").append("          SGR.GRP_SEQ ").append("        , GRP.DESCRICAO ")
					.append("        , SGR.SUB_GRUPO ").append("        , SGR.DESCRICAO ").append("        , FOG.CODIGO ")
					.append("        , FOG.DESCRICAO ").append("		, IPH.SEQ").append("		, IPH.PHO_SEQ").append("		, IPH.FCF_SEQ");

			final SQLQuery query = createSQLQuery(sql.toString());

			query.setString("P_CPE_MODULO", competencia.getId().getModulo().toString());
			query.setInteger("P_CPE_MES", competencia.getId().getMes());
			query.setInteger("P_CPE_ANO", competencia.getId().getAno());
			query.setTimestamp("P_CPE_DT_HR_INICIO", competencia.getId().getDtHrInicio());

			query.setString("P_FCF_CODIGO", fcfCodigo);
			final String[] modulos = { DominioModuloCompetencia.APAC.toString(), DominioModuloCompetencia.APEX.toString(),
					DominioModuloCompetencia.APAP.toString(), DominioModuloCompetencia.APAR.toString(),
					DominioModuloCompetencia.APAF.toString(), DominioModuloCompetencia.APAT.toString(),
					DominioModuloCompetencia.MAMA.toString() };
			query.setParameterList("P_CPE_MODULO_LIST", modulos, StringType.INSTANCE);

			final List<PreviaDiariaFaturamentoVO> result = query
					.addScalar(PreviaDiariaFaturamentoVO.Fields.GRUPO.toString(), ShortType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.GRUPO_DESC.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO.toString(), ByteType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO_DESC.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FOG_COD.toString(), ByteType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FOG_DESC.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.QUANTIDADE.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.TOTAL.toString(), BigDecimalType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FCF_SEQ.toString(), IntegerType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PreviaDiariaFaturamentoVO.class)).list();

			return result;

		} else {
			final StringBuffer sql = new StringBuffer(500);

			// TODO rever ZONA eschweigert 21/08/2012
			// DECODE(SUBSTR(UNF.DESCRICAO,1,4),'ZONA',DECODE(UNF.SEQ,33,33,UNF1.SEQ),UNF.SEQ)
			// Cod_Unidade,
			final String caseCodUnidade = " CASE WHEN SUBSTR( UNF.DESCRICAO,1,4) = 'ZONA' THEN" + "    CASE WHEN UNF.SEQ = " + vUnf33
					+ " THEN " + vUnf33 + "       ELSE UNF1.SEQ " + "    END " + "  ELSE UNF.SEQ " + " END ";

			// DECODE(SUBSTR(UNF.DESCRICAO,1,4),'ZONA',
			// DECODE(UNF.SEQ,33,UNF.DESCRICAO,UNF1.DESCRICAO), UNF.DESCRICAO)
			// Desc_Unidade,
			final String caseDescUnidade = " CASE WHEN SUBSTR( UNF.DESCRICAO,1,4) = 'ZONA' THEN" + "    CASE WHEN UNF.SEQ = " + vUnf33
					+ " THEN " + "          UNF.DESCRICAO " + "       ELSE " + "          UNF1.DESCRICAO " + "    END "
					+ "  ELSE UNF.DESCRICAO " + " END ";

			sql.append(" select ").append("     CCT.CODIGO    AS ").append(PreviaDiariaFaturamentoVO.Fields.COD_CENTRO_CUSTO.toString())
					.append("   , CCT.DESCRICAO AS ").append(PreviaDiariaFaturamentoVO.Fields.DESC_CENTRO_CUSTO.toString())
					.append("   , (").append(caseCodUnidade).append(")  AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.COD_UNIDADE.toString()).append("   , (").append(caseDescUnidade)
					.append(") AS ").append(PreviaDiariaFaturamentoVO.Fields.DESC_UNIDADE.toString()).append("   , ESP1.SIGLA AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.GENERICA.toString()).append("   , ESP.SIGLA AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.AGENDA.toString()).append("   , IPH.FOG_CODIGO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FOG_COD.toString()).append("   , IPH.FOG_SGR_SUB_GRUPO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO.toString()).append("   , IPH.FOG_SGR_GRP_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.GRUPO.toString()).append("   , EPA.PROCEDIMENTO_HOSP AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.SSM.toString()).append("   , IPH.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.DESCR_SSM.toString()).append("   , PHI.SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.PHI.toString()).append("   , PHI.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.DESCR_PHI.toString()).append("   , IPH.FCF_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FCF_SEQ.toString()).append("   , FCF.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FINANCIAMENTO.toString()).append("   , IPH.FCC_SEQ  AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.FCC_SEQ.toString()).append("   , FCC.DESCRICAO AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.COMPLEXIBILIDADE.toString())
					.append("   , SUM( COALESCE( EPA.VLR_ANESTES,0 ) ) AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.TOTAL_ANESTESIA.toString())
					.append("   , SUM( COALESCE( EPA.VLR_SERV_PROF,0 ) ) AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.TOTAL_SERV_PROF.toString())
					.append("   , SUM( COALESCE( EPA.VLR_PROC,0 ) ) AS ").append(PreviaDiariaFaturamentoVO.Fields.TOTAL.toString())
					.append("   , SUM( COALESCE( EPA.QUANTIDADE,0 ) ) AS ").append(PreviaDiariaFaturamentoVO.Fields.QUANTIDADE.toString())
					.append("   , IPH.SEQ AS ").append(PreviaDiariaFaturamentoVO.Fields.IPH_SEQ.toString()).append("   , IPH.PHO_SEQ AS ")
					.append(PreviaDiariaFaturamentoVO.Fields.IPH_PHO_SEQ.toString())

					.append(" FROM ").append("     AGH.").append(FatEspelhoProcedAmb.class.getAnnotation(Table.class).name())
					.append(" EPA ").append("   , AGH.").append(FccCentroCustos.class.getAnnotation(Table.class).name()).append(" CCT ")
					.append("   , AGH.").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF ")
					.append("   , AGH.").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF1 ")
					.append("   , AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH ")
					.append("   , AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI ")
					.append("   , AGH.").append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name()).append(" PMR ")
					.append("     LEFT JOIN AGH.").append(AghEspecialidades.class.getAnnotation(Table.class).name()).append(" ESP ")
					.append(" ON ESP.SEQ  = PMR.ESP_SEQ")

					.append("     LEFT JOIN AGH.").append(AghEspecialidades.class.getAnnotation(Table.class).name())
					.append(" 			ESP1 ON ESP1.SEQ = COALESCE( ESP.ESP_SEQ, ESP.SEQ ) ")

					.append("   , AGH.").append(FatCaractComplexidade.class.getAnnotation(Table.class).name()).append("  FCC ")
					.append("   , AGH.").append(FatCaractFinanciamento.class.getAnnotation(Table.class).name()).append(" FCF ")
					.append("   , AGH.").append(FatSubGrupo.class.getAnnotation(Table.class).name()).append(" SGR ").append("   , AGH.")
					.append(FatFormaOrganizacao.class.getAnnotation(Table.class).name()).append(" FOG ")

					.append(" WHERE ")

					.append("     CCT.CODIGO = ").append("             COALESCE ( ").append("             			( CASE ")
					.append("                             WHEN PMR.IND_ORIGEM = '")
					.append(DominioOrigemProcedimentoAmbulatorialRealizado.CIA.toString()).append("' THEN ").append(" ESP.CCT_CODIGO")

					.append("                             WHEN PMR.IND_ORIGEM = '")
					.append(DominioOrigemProcedimentoAmbulatorialRealizado.CON.toString()).append("' THEN ").append(" ESP.CCT_CODIGO")

					.append("                             WHEN PMR.IND_ORIGEM = '")
					.append(DominioOrigemProcedimentoAmbulatorialRealizado.DIG.toString()).append("' THEN ")
					.append(" CASE WHEN ESP.CCT_CODIGO IS NULL THEN UNF.CCT_CODIGO ELSE ESP.CCT_CODIGO END ")
					.append("                            ELSE UNF.CCT_CODIGO ").append("                          END ")
					.append("             			), :PRM_COD_CC ").append("                      ) ")

					.append("     AND EPA.IND_CONSISTENTE = :PMR_IND_CONSISTENTE ").append("     AND UNF1.SEQ = ")
					.append("              ( CASE WHEN SUBSTR(UNF.DESCRICAO,1,4) = 'ZONA' THEN ")
					.append("                   CASE WHEN UNF.SEQ = :PMR_33 then :PMR_33 ")
					.append("                     ELSE COALESCE(UNF.UNF_SEQ, :PMR_101) ").append("                   END ")
					.append("                 ELSE :PMR_101 ").append("                END ")
					.append("              ) ")

					.append("     AND IPH.PHO_SEQ  	 = EPA.IPH_PHO_SEQ ")
					.append("     AND IPH.SEQ      	 = EPA.IPH_SEQ ")
					.append("     AND PHI.SEQ      	 = PMR.PHI_SEQ ")
					.append("     AND PMR.SEQ      	 = EPA.PMR_SEQ ")
					.append("     AND FCC.SEQ 	   	 = IPH.FCC_SEQ ")
					.append("     AND FCF.SEQ 	     = IPH.FCF_SEQ ")
					.append("     AND FOG.CODIGO      = IPH.FOG_CODIGO ")
					.append("     AND FOG.SGR_GRP_SEQ   = IPH.FOG_SGR_GRP_SEQ ")
					.append("     AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO ")

					// and UNF.SEQ = aghc_busca_afn_ig(
					// NVL(PMR.UNF_SEQ,EPA.UNIDADE_FUNCIONAL) )
					/*
					 * TODO SCHWEIGERT 10/01/2011 ACORDADO COM ANALISTA E
					 * USUÁRIO HCPA, TAL REGRA ESTÁ PRESENTE NO DOCUMENTO DE
					 * ANÁLISE Após a execução da query principal, deve-se varer
					 * os resultados buscando o centro de custo e unidade
					 * corretas, uma vez que as mesmas podem estar associadas a
					 * outras (parents) sendo estas a serem exibidas:
					 * 
					 * A regra de execução deve validar o valor de
					 * Pmr.Ind_Origem for: - 'CIA' OU 'CON' --> PREVALECE O
					 * Cct.Codigo ORIGINAL - 'DIG' CASO Esp.Cct_Codigo != null
					 * PREVALECE O CENTRO DE CUSTO DA ESPEC. SENÃO EXECUTA A
					 * FUNCION - DEFAULT: EXECUTA A FUNCION
					 */
					.append("     AND UNF.SEQ = COALESCE(PMR.UNF_SEQ,EPA.UNIDADE_FUNCIONAL) ")

					.append("     AND EPA.CPE_MODULO = :PMR_CPE_MODULO ").append("     AND EPA.CPE_MES = :PMR_CPE_MES ")
					.append("     AND EPA.CPE_ANO = :PMR_CPE_ANO ").append("     AND EPA.CPE_DT_HR_INICIO = :PMR_CPE_DT_HR_INICIO ")

					.append("     GROUP BY ").append("          CCT.CODIGO ").append("        , CCT.DESCRICAO ").append("        , (")
					.append(caseCodUnidade).append(") ").append("        , (").append(caseDescUnidade).append(") ")
					.append("        , ESP1.SIGLA ").append("        , ESP.SIGLA ").append("        , IPH.FOG_CODIGO ")
					.append("        , IPH.FOG_SGR_SUB_GRUPO ").append("        , IPH.FOG_SGR_GRP_SEQ ")
					.append("        , EPA.PROCEDIMENTO_HOSP ").append("        , IPH.DESCRICAO ").append("        , PHI.SEQ ")
					.append("        , PHI.DESCRICAO ").append("        , IPH.FCF_SEQ ").append("        , FCF.DESCRICAO ")
					.append("        , IPH.FCC_SEQ ").append("        , FCC.DESCRICAO ").append("		, IPH.SEQ").append("		, IPH.PHO_SEQ");

			final SQLQuery query = createSQLQuery(sql.toString());

			query.setString("PMR_CPE_MODULO", competencia.getId().getModulo().toString());
			query.setInteger("PMR_CPE_MES", competencia.getId().getMes());
			query.setInteger("PMR_CPE_ANO", competencia.getId().getAno());
			query.setTimestamp("PMR_CPE_DT_HR_INICIO", competencia.getId().getDtHrInicio());
			query.setShort("PMR_101", vUnf101);
			query.setShort("PMR_33", vUnf33);
			query.setShort("PRM_COD_CC", vCodCC);
			query.setString("PMR_IND_CONSISTENTE", DominioSimNao.S.toString());

			final List<PreviaDiariaFaturamentoVO> result = query
					.addScalar(PreviaDiariaFaturamentoVO.Fields.COD_CENTRO_CUSTO.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.DESC_CENTRO_CUSTO.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.COD_UNIDADE.toString(), ShortType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.DESC_UNIDADE.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.GENERICA.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.AGENDA.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FOG_COD.toString(), ByteType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.SUB_GRUPO.toString(), ByteType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.GRUPO.toString(), ShortType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.SSM.toString(), LongType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.DESCR_SSM.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.PHI.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.DESCR_PHI.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FCF_SEQ.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FINANCIAMENTO.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.FCC_SEQ.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.COMPLEXIBILIDADE.toString(), StringType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.TOTAL_ANESTESIA.toString(), BigDecimalType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.TOTAL_SERV_PROF.toString(), BigDecimalType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.TOTAL.toString(), BigDecimalType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.QUANTIDADE.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE)
					.addScalar(PreviaDiariaFaturamentoVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PreviaDiariaFaturamentoVO.class)).list();

			return result;
		}
	}

	public List<FatRelatorioProducaoPHIVO> getRelatorioProducaoPHI(final List<Integer> phiSeqs, final Date dtInicio, final Date dtFinal) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "pmr");
		criteria.createAlias("pmr." + FatProcedAmbRealizado.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("pmr." + FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi");
		criteria.createAlias("pmr." + FatProcedAmbRealizado.Fields.CONSULTA.toString(), "con");
		criteria.createAlias("con." + AacConsultas.Fields.RETORNO.toString(), "ret");

		criteria.add(Restrictions.in("pmr." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), phiSeqs));
		criteria.add(Restrictions.between("pmr." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dtInicio, dtFinal));
		criteria.add(Restrictions.eq("ret." + AacRetornos.Fields.IND_FATURA_SUS.toString(), Boolean.TRUE));

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()).as(
						FatRelatorioProducaoPHIVO.Fields.PRONTUARIO.toString()))
				// .add(Projections.property("pac." +
				// AipPacientes.Fields.PRONTUARIO.toString()).as(FatRelatorioProducaoPHIVO.Fields.PRONTUARIO.toString()))
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString())
						.as(FatRelatorioProducaoPHIVO.Fields.NOME.toString()))
				.add(Projections.property("pmr." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString()).as(
						FatRelatorioProducaoPHIVO.Fields.PHI_SEQ.toString()))
				.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()).as(
						FatRelatorioProducaoPHIVO.Fields.DESCRICAO.toString()))
				.add(Projections.property("pmr." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).as(
						FatRelatorioProducaoPHIVO.Fields.DTHR_REALIZADO.toString()))
				.add(Projections.property("pac." + AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString()).as(
						FatRelatorioProducaoPHIVO.Fields.NRO_CARTAO_SAUDE.toString())));

		criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.PRONTUARIO.toString()));
		criteria.addOrder(Order.asc("pmr." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(FatRelatorioProducaoPHIVO.class));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorAtdSeq(final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ATD_SEQ.toString(), atdSeq));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorAtdSeqSemPrhConNumero(final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * ORADB: FATP_ATUALIZA_PMR_KITS_PRE CURSOR: C_PMR
	 */
	public List<FatProcedAmbRealizado> obterCursorCPmr(final Integer pacCodigo, final DominioSituacaoProcedimentoAmbulatorio situacao,
			final Date dtHoraInicio, final Integer mes, final Integer ano, final Integer tptSeq, final boolean indListaCandidato,
			final DominioGrupoConvenio grupoConvenio) {

		final StringBuilder sql = new StringBuilder(600);

		sql.append("SELECT {PMR.*} ")

		.append(" FROM ").append("   AGH.").append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name()).append(" PMR ")
				.append(" , AGH.").append(FatEspecialidadeTratamento.class.getAnnotation(Table.class).name()).append(" ETR ")
				.append(" , AGH.").append(AacGradeAgendamenConsultas.class.getAnnotation(Table.class).name()).append(" GRD ")
				.append(" , AGH.").append(AacConsultas.class.getAnnotation(Table.class).name()).append(" CON ").append(" , AGH.")
				.append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" ATD ").append(" , AGH.")
				.append(AelSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" SOE ").append(" , AGH.")
				.append(FatKitExsPreTrans.class.getAnnotation(Table.class).name()).append(" KEP ")

				.append(" WHERE 1=1").append("  AND PMR.").append(FatProcedAmbRealizado.Fields.PAC_CODIGO.name())
				.append(" = :P_PAC_CODIGO ").append("  AND PMR.").append(FatProcedAmbRealizado.Fields.IND_SITUACAO.name())
				.append(" = :P_IND_SITUACAO ").append("  AND PMR.").append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.name())
				.append(" = :P_DATA_HORA_INICIO ").append("  AND PMR.").append(FatProcedAmbRealizado.Fields.CPE_MES.name())
				.append(" = :P_MES ").append("  AND PMR.").append(FatProcedAmbRealizado.Fields.CPE_ANO.name()).append(" = :P_ANO ")
				.append("  AND PMR.").append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.name()).append(" IN ( ")
				.append(" SELECT cnv.codigo ").append(" FROM AGH.").append(FatConvenioSaude.class.getAnnotation(Table.class).name())
				.append(" CNV ").append(" WHERE CNV.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.name()).append(" = :P_GRUPO_CONVENIO")
				.append(')')

				.append("  AND KEP.").append(FatKitExsPreTrans.Fields.TPT_SEQ.name()).append(" = :P_TPT_SEQ ").append("  AND KEP.")
				.append(FatKitExsPreTrans.Fields.PHI_SEQ.name()).append(" = PMR.").append(FatProcedAmbRealizado.Fields.PHI_SEQ.name())
				.append("  AND SOE.").append(AelSolicitacaoExames.Fields.SEQ.name()).append(" = PMR.")
				.append(FatProcedAmbRealizado.Fields.ISE_SOE_SEQ.name()).append("  AND ATD.").append(AghAtendimentos.Fields.SEQ.name())
				.append(" = SOE.").append(AelSolicitacaoExames.Fields.ATD_SEQ.name()).append("  AND ATD.")
				.append(AghAtendimentos.Fields.CON_NUMERO.name()).append(" = CON.").append(AacConsultas.Fields.NUMERO.name())
				.append("  AND GRD.").append(AacGradeAgendamenConsultas.Fields.SEQ.name()).append(" = CON.")
				.append(AacConsultas.Fields.GRD_SEQ.name()).append("  AND ETR.").append(FatEspecialidadeTratamento.Fields.ESP_SEQ.name())
				.append(" = GRD.").append(AacGradeAgendamenConsultas.Fields.ESP_SEQ.name())

				.append("  AND ETR.").append(FatEspecialidadeTratamento.Fields.TPT_SEQ.name()).append(" = :P_TPT_SEQ ")
				.append("  AND ETR.").append(FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.name())
				.append(" = :P_IND_LISTA_CANDIDATO ");

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("P_PAC_CODIGO", pacCodigo);
		query.setString("P_IND_SITUACAO", situacao.getCodigo());
		query.setTimestamp("P_DATA_HORA_INICIO", dtHoraInicio);
		query.setInteger("P_MES", mes);
		query.setInteger("P_ANO", ano);
		query.setInteger("P_TPT_SEQ", tptSeq);
		query.setString("P_IND_LISTA_CANDIDATO", indListaCandidato ? "S" : "N");
		query.setString("P_GRUPO_CONVENIO", grupoConvenio.toString());

		query.addEntity("PMR", FatProcedAmbRealizado.class);

		return query.list();
	}

	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Integer ppcCrgSeq, Short cnvCodigo, Byte cspSeq) {
		if (ppcCrgSeq != null) {
			criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), ppcCrgSeq));
		}

		if (cnvCodigo != null) {
			criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString(), cnvCodigo));
		}
		if (cspSeq != null) {
			criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.CSP_SEQ.toString(), cspSeq));
		}
		return criteria;
	}

	public FatProcedAmbRealizado obterFatProcedAmbRealizadoPorCrgSeq(Integer ppcCrgSeq, Short cnvCodigo, Byte cspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria = this.obterCriterioConsulta(criteria, ppcCrgSeq, cnvCodigo, cspSeq);

		return (FatProcedAmbRealizado) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeq(Integer ppcCrgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria = this.obterCriterioConsulta(criteria, ppcCrgSeq, null, null);

		return this.executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorCrgSeqSituacao(Integer ppcCrgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria = this.obterCriterioConsulta(criteria, ppcCrgSeq, null, null);
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), new DominioSituacaoProcedimentoAmbulatorio[] {
				DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, DominioSituacaoProcedimentoAmbulatorio.ENCERRADO,
				DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO }));

		return this.executeCriteria(criteria);
	}

	/**
	 * Lista procedimentos realizados com competência no ambulatório para
	 * atualização do faturamento no bloco cirúrgico
	 * 
	 * @param p_crg_seq
	 * @param p_epr_esp_seq
	 * @param p_epr_pci_seq
	 * @param p_ind_resp_proc
	 * @return
	 */
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoComCompetenciaAberta(Integer p_crg_seq, Short p_epr_esp_seq,
			Integer p_epr_pci_seq, DominioIndRespProc p_ind_resp_proc) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), p_crg_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_EPR_ESP_SEQ.toString(), p_epr_esp_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_EPR_PCI_SEQ.toString(), p_epr_pci_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_IND_RESP_PROC.toString(), p_ind_resp_proc));

		// IND_SITUACAO IN ('A','C','5','T');
		DominioSituacaoProcedimentoAmbulatorio[] situacoesValidas = { DominioSituacaoProcedimentoAmbulatorio.ABERTO,
				DominioSituacaoProcedimentoAmbulatorio.CANCELADO, DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO_APAP,
				DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO };
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), situacoesValidas));

		return executeCriteria(criteria);
	}

	/**
	 * Lista procedimentos realizados no ambulatório para atualização do
	 * faturamento no bloco cirúrgico
	 * 
	 * @param p_crg_seq
	 * @param p_epr_esp_seq
	 * @param p_epr_pci_seq
	 * @param p_ind_resp_proc
	 * @return
	 */
	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoSemCompetenciaAberta(Integer p_crg_seq, Short p_epr_esp_seq,
			Integer p_epr_pci_seq, DominioIndRespProc p_ind_resp_proc) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);

		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), p_crg_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_EPR_ESP_SEQ.toString(), p_epr_esp_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_EPR_PCI_SEQ.toString(), p_epr_pci_seq));
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_IND_RESP_PROC.toString(), p_ind_resp_proc));

		return executeCriteria(criteria);
	}

	public List<FatProcedAmbRealizado> buscarPorCirurgia(Integer crgSeq, DominioSituacaoProcedimentoAmbulatorio[] situacoes) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), situacoes));
		return executeCriteria(criteria);
	}

	/**
	 * Realiza a busca por procedimentos relacionados a consultas liberadas por óbito.
	 * 
	 * @param numeroConsulta - Número da Consulta
	 * @return Lista de procedimentos
	 */
	public List<FatProcedAmbRealizado> pesquisarProcedimentosRealizadosPorConsultaLiberadaPorObito(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		
		criteria.createAlias(FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString(), "PRH");
		
		criteria.add(Restrictions.eq("PRH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq("PRH." + AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), DominioSimNao.S.isSim()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Realiza a busca por procedimentos relacionados a consultas liberadas por óbito, para serem excluídos, conforme filtros.
	 * 
	 * @param codigoAtendimento - Código do Atendimento
	 * @param codigoProcedimento - Código do Procedimento Interno
	 * @param codigoPaciente - Código do Paciente
	 * @param moduloCompetencia - Módulo da Competência
	 * @param anoCompetencia - Ano da Competência
	 * @param mesCompetencia - Mês da Competência
	 * @param dthrRealizado - Data e hora de realização do Procedimento
	 * @param dataInicioCompetencia - Data e hora de início da Competência
	 * @return Lista de procedimentos a serem excluídos
	 */
	public List<FatProcedAmbRealizado> pesquisarProcedimentosParaExclusaoPorObito(Integer codigoAtendimento, Integer codigoProcedimento,
			Integer codigoPaciente, DominioModuloCompetencia moduloCompetencia, Integer anoCompetencia, Integer mesCompetencia, Date dthrRealizado,
			Date dataInicioCompetencia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.FAT_COMPETENCIA.toString(), "CPE");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), codigoAtendimento));
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.SEQ.toString(), codigoProcedimento));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("CPE." + FatCompetencia.Fields.MODULO.toString(), moduloCompetencia));
		criteria.add(Restrictions.eq("CPE." + FatCompetencia.Fields.ANO.toString(), anoCompetencia));
		criteria.add(Restrictions.eq("CPE." + FatCompetencia.Fields.MES.toString(), mesCompetencia));
		criteria.add(Restrictions.eq("PMR." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dthrRealizado));
		criteria.add(Restrictions.eq("CPE." + FatCompetencia.Fields.DT_HR_INICIO.toString(), dataInicioCompetencia));
		
		return executeCriteria(criteria);
	}
	
	public List<FatProcedAmbRealizado> buscarProcedimentosCirurgicosParaSeremFaturados(
			final AipPacientes paciente, final MbcCirurgias cirurgia,final Short unfSeq, 
			final Short convenioId, final Byte planoId){
		
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PROC");
		
		if (paciente != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PAC_CODIGO.toString(), paciente.getCodigo()));
		}

		if (cirurgia != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.PPC_CRG_SEQ.toString(), cirurgia.getSeq()));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.UNF_SEQ, unfSeq));
		}
		
		if (convenioId != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO, convenioId));
		}
		
		if (planoId != null) {
			criteria.add(Restrictions.eq("PROC." + FatProcedAmbRealizado.Fields.CSP_SEQ, planoId));
		}		
		return  executeCriteria(criteria);
		
	}


	public List<FatProcedAmbRealizado> listarFatProcedAmbRealizadoPorPrhConNumero(final Integer numero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), numero));

		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaProcedAmbPorCirurgiaCancelada(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CIRURGIAS.toString(), "CIG");
		criteria.add(Restrictions.eq("CIG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.C));
		criteria.add(Restrictions.eq("CIG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		return criteria;
	}

	public List<FatProcedAmbRealizado> pesquisarProcedAmbPorCirurgiaCancelada(Integer crgSeq) {
		return executeCriteria(obterCriteriaProcedAmbPorCirurgiaCancelada(crgSeq));
	}

	public FatProcedAmbRealizado obterProcedAmbPorCirurgiaCancelada(Integer crgSeq) {
		return (FatProcedAmbRealizado) executeCriteriaUniqueResult(obterCriteriaProcedAmbPorCirurgiaCancelada(crgSeq));
	}
	/**
	 * 42010
	 */
	public FatProcedAmbRealizadosVO buscaFatProcedAmbRealizado(Integer numeroConsulta, Integer codigoTpt) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.CONSULTA_PROCED_HOSPITALAR.toString(), "PRH");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI");
		criteria.createAlias("PHI." + FatProcedHospInternos.Fields.PROCED_TRATAMENTOS.toString(), "PRT");
		criteria.createAlias("PRT." + FatProcedTratamento.Fields.TIPO_TRATAMENTO.toString(), "TPT");
		
		criteria.add(Restrictions.eq("PRH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq("TPT." + FatTipoTratamentos.Fields.CODIGO.toString(), codigoTpt));
		criteria.add(Restrictions.eq("PRH." + AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq("PMR." + FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), DominioSituacaoProcedimentoAmbulatorio.ABERTO));
		criteria.add(Restrictions.eq("CNV." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		criteria.add(Restrictions.eqProperty("PMR." + FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), "PRH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("PMR." + FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString(), "PRH." + AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.PAC_CODIGO.toString()), FatProcedAmbRealizadosVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()), FatProcedAmbRealizadosVO.Fields.DT_HR_REALIZADO.toString())
				.add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString()), FatProcedAmbRealizadosVO.Fields.PHI_SEQ_PROCED.toString())
				.add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.SEQ.toString()), FatProcedAmbRealizadosVO.Fields.SEQ.toString())
				.add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.ESP_SEQ.toString()), FatProcedAmbRealizadosVO.Fields.SEQ_ESPECIALIDADE.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedAmbRealizadosVO.class));
		
		return (FatProcedAmbRealizadosVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #42010
	 * 
	 * Consulta que retorna quantidade de aparelhos auditivos do paciente
	 * 
	 * @param pacCodigo
	 * @return FatProcedAmbRealizadosVO
	 */
	@SuppressWarnings("unchecked")
	public FatProcedAmbRealizadosVO buscaAparelho (Integer pacCodigo) {
//	SELECT  pmr.quantidade
//		FROM  fat_tipo_caract_itens tct,
//		  fat_caract_item_proc_hosp cpr,
//		  V_FAT_ASSOCIACAO_PROCEDIMENTOS VAPR,
//		  fat_convenios_saude cnv,
//		  fat_proced_amb_realizados pmr
//	WHERE   pmr.pac_codigo = c_pac_codigo
//	  AND   cnv.codigo = pmr.csp_cnv_codigo
//	  AND 	cnv.grupo_convenio = 'S'
//	  AND   vapr.phi_seq = pmr.phi_seq
//	  AND   vapr.CPG_CPH_CSP_CNV_CODIGO = pmr.csp_cnv_codigo
//	  AND   VAPR.CPG_CPH_CSP_SEQ = pmr.csp_seq
//	  AND   vapr.iph_pho_seq = 12 --4
//	  AND   cpr.iph_pho_seq = vapr.iph_pho_seq
//	  AND   cpr.iph_seq = vapr.iph_seq
//	  AND   tct.seq  = cpr.tct_seq
//	  AND   tct.caracteristica =  'Aparelho Auditivo';
		
		final StringBuilder sql = new StringBuilder(600);

		sql.append("select  pmr.").append(FatProcedAmbRealizado.Fields.QUANTIDADE.name()).append(" as ").append(FatProcedAmbRealizadosVO.Fields.QUANTIDADE.name())
	
			.append(" FROM ").append(" AGH.").append(FatTipoCaractItens.class.getAnnotation(Table.class).name()).append(" tct,")
							.append(" AGH.").append(FatCaractItemProcHosp.class.getAnnotation(Table.class).name()).append(" cpr,")
							.append(" AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAPR,")
							.append(" AGH.").append(FatConvenioSaude.class.getAnnotation(Table.class).name()).append(" cnv,")
							.append(" AGH.").append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name()).append(" pmr")
							
			.append(" WHERE pmr.").append(FatProcedAmbRealizado.Fields.PAC_CODIGO.name()).append(" = :PAC_CODIGO ")
			.append(" AND cnv.").append(FatConvenioSaude.Fields.CODIGO.name()).append(" = pmr.").append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.name())
			.append(" AND cnv.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.name()).append(" = 'S' ")
			
			.append(" AND VAPR.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = pmr.").append(FatProcedAmbRealizado.Fields.PHI_SEQ.name())
			.append(" AND VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = pmr.").append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.name())
			.append(" AND VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = pmr.").append(FatProcedAmbRealizado.Fields.CSP_SEQ.name())
			.append(" AND VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = 12")
			
			.append(" AND VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = cpr.").append(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.name())
			.append(" AND cpr.").append(FatCaractItemProcHosp.Fields.IPH_SEQ.name()).append(" = VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name())
			.append(" AND tct.").append(FatTipoCaractItens.Fields.SEQ.name()).append(" = cpr.").append(FatCaractItemProcHosp.Fields.TCT_SEQ.name())
			.append(" AND tct.").append(FatTipoCaractItens.Fields.CARACTERISTICA.name()).append(" = 'Aparelho Auditivo'");
			
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setInteger("PAC_CODIGO", pacCodigo);
		query.setResultTransformer(Transformers.aliasToBean(FatProcedAmbRealizadosVO.class));
		
		List<FatProcedAmbRealizadosVO> list = query.list();
		
		if(list!= null && !list.isEmpty()){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	
	/**
	 * 42803 
	 * C8
	 */
	public ParametrosGeracaoLaudoOtorrinoVO obterParametrosGeracaoLaudoOtorrino(Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("G." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "E", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("C." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PAC." + AipPacientes.Fields.FAT_CANDIDATOS_APAC_OTORRINO.toString(), "O", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("C." + AacConsultas.Fields.PAC_CODIGO.toString()).as(
						ParametrosGeracaoLaudoOtorrinoVO.Fields.CODIGO_PACIENTE.toString()))
				.add(Projections.property("C." + AacConsultas.Fields.DATA_CONSULTA.toString()).as(
						ParametrosGeracaoLaudoOtorrinoVO.Fields.DTHR_REALIZADO.toString()))
				.add(Projections.property("O." + FatCandidatosApacOtorrino.Fields.PHI_SEQ.toString()).as(
						ParametrosGeracaoLaudoOtorrinoVO.Fields.SEQ_PHI.toString()))
				.add(Projections.property("E." + AghEquipes.Fields.SEQ.toString()).as(
						ParametrosGeracaoLaudoOtorrinoVO.Fields.SEQ_EQUIPE.toString()))
				.add(Projections.property("C." + AacConsultas.Fields.NUMERO.toString()).as(
						ParametrosGeracaoLaudoOtorrinoVO.Fields.CON_NUMERO.toString()))
				.add(Projections.property("O." + FatCandidatosApacOtorrino.Fields.SEQ.toString()).as(ParametrosGeracaoLaudoOtorrinoVO.Fields.FAT_CAND_APAC_SEQ.toString())));

		criteria.add(Restrictions.eq("C." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eqProperty("O." + FatCandidatosApacOtorrino.Fields.DT_EVENTO.toString(), "C." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ParametrosGeracaoLaudoOtorrinoVO.class));
		return (ParametrosGeracaoLaudoOtorrinoVO) executeCriteriaUniqueResult(criteria);
	}
	
	/** #42803
	 *  Lista procedimentos ambulatorios realizados por numero da consulta
	 *  ORADB c_busca_pmr
	 *  @param numeroConsulta
	 */
	@SuppressWarnings("unchecked")
	public List<FatProcedAmbRealizado> listarProcedimentosHospitalaresPorConsulta(Integer numeroConsulta){
		FatProcedAmbRealizadoQueryBuilder builder = fatProcedAmbRealizadoQueryBuilder.get();
		return builder.build(numeroConsulta).list();	}	
	
	/**
	 * #42803
	 * @param numero pac, data realizado
	 * ORADB: c_busca_aparelho
	 * @return 
	 */
	
	public FatProcedAmbRealizadosVO buscaAparelho(Integer pacCodigo, Date dataRealizado){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		criteria.createAlias("PMR." + FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), "VAPR");
		criteria.createAlias("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), "CPR");
		criteria.createAlias("CPR." + FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), "TCT");
		
		criteria.add(Restrictions.eq("PMR." + FatProcedAmbRealizado.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne("PMR." + FatProcedAmbRealizado.Fields.IND_SITUACAO.toString(), DominioSituacaoProcedimentoAmbulatorio.CANCELADO));
		criteria.add(Restrictions.lt("PMR." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString(), dataRealizado));
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), 1));
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), 2));
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), 1));
		criteria.add(Restrictions.eqProperty("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString(),"CPR." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString()));
		criteria.add(Restrictions.ilike("TCT." + FatTipoCaractItens.Fields.CARACTERISTICA.toString() , "Aparelho Auditivo"));
		criteria.addOrder(Order.desc("PMR." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()));
		
		String consulta = null;
		if(isOracle()){
			consulta = "nvl(PM.'" + FatProcedAmbRealizado.Fields.QUANTIDADE.toString()+ "',0)";
		}else{
			consulta = "coalesce(PM.'" + FatProcedAmbRealizado.Fields.QUANTIDADE.toString()+ "',0)";
		}
		criteria.setProjection(Projections.projectionList()
				  .add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.QUANTIDADE.toString()),
						  FatProcedAmbRealizadosVO.Fields.QUANTIDADE.toString())
				  .add(Projections.property("PMR." + FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()),
								FatProcedAmbRealizadosVO.Fields.DT_HR_REALIZADO.toString())
				  .add(Projections.sqlProjection(consulta, new String[]{},new Type[]{}))
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedAmbRealizadosVO.class));
		return (FatProcedAmbRealizadosVO) executeCriteriaUniqueResult(criteria);
	}
	
	public FatProcedAmbRealizado buscaProcedAmbRealizados(Integer numCons){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class);
		criteria.add(Restrictions.eq(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString(), numCons));
		return (FatProcedAmbRealizado) executeCriteriaUniqueResult(criteria);
	}
	
	/**#37951
	 * Lista procedimentos relizados
	 * @param p_old_atd_seq
	 * @param p_new_atd_seq
	 * @return
	 */
	public List<FatProcedAmbRealizado> obterProcedimentosRelizado(Integer p_old_atd_seq, Integer p_new_atd_seq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "PMR");
		criteria.add(Restrictions.eq("PMR." + FatProcedAmbRealizado.Fields.ATD_SEQ.toString(), p_old_atd_seq));
		criteria.add(Restrictions.sqlRestriction(" this_.ise_soe_seq in (select soe.seq from agh.agh_atendimentos atd2, agh.ael_solicitacao_exames soe, agh.agh_atendimentos atd where atd.seq = "+ p_old_atd_seq + " and soe.atd_seq = atd.seq and atd2.pac_codigo = atd.pac_codigo and atd2.seq = " + p_new_atd_seq + " )"));
		return executeCriteria(criteria);
	}
	
}
