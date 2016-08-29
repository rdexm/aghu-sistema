package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;

import br.gov.mec.aghu.core.persistence.dao.DataAccessService;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmControlPrevAltas;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.sig.custos.vo.NutricaoParenteralVO;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class MpmPrescricaoMedicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoMedica> {

	private static final long serialVersionUID = 8867242183068088709L;
	
	private static final Log LOG = LogFactory.getLog(MpmPrescricaoMedicaDAO.class);
	
	
	
	@Inject
	private DataAccessService dataAcess;

	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.MPM_PME_SQ1);
	}

	/**
	 * Retorna as prescrições de um atendimento cuja dataFim seja maior que a
	 * data informada
	 * 
	 * @param atendimento
	 * @param data
	 * @return lista de prescrições
	 */
	public List<MpmPrescricaoMedica> prescricoesAtendimentoDataFim(final AghAtendimentos atendimento, final Date data) {

		List<MpmPrescricaoMedica> retorno = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);

		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), atendimento));

		if (data != null) {
			criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), data));
		}

		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));

		retorno = executeCriteria(criteria);
		
		for (MpmPrescricaoMedica prescricao : retorno){
			this.lockEntity(prescricao, LockModeType.READ);
		}
		return retorno;
	}

	/**
	 * Retorna true quando existir prescrição em dia e false caso contrário.
	 * 
	 * @param aghAtendimentosSeq
	 * @return
	 */
	public boolean existePrescricaoEmDia(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		DetachedCriteria criteria = montarCriteriaPrescricaoAtendimento(atendimento);
		return executeCriteriaCount(criteria) > 0;
	}

	/**
	 * Retorna true quando existir prescrição para data futura e false caso
	 * contrário.
	 * 
	 * @param atendimento
	 * @return
	 */
	public boolean existePrescricaoFutura(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		Date dataAtual = new Date();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);

		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(),"atendimento");
		criteria.createAlias("atendimento." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataAtual));
		criteria.add(Restrictions.sqlRestriction("to_char(hrio_validade_pme,'HH24:mi') = to_char({alias}.dthr_inicio,'HH24:mi')"));

		return executeCriteriaCount(criteria) > 0;
	}

	/**
	 * Montar a criteria base para os métodos existePrescricaoEmDia() e
	 * existePrescricaoFutura()
	 * 
	 * @param atendimento
	 * @return
	 */
	private DetachedCriteria montarCriteriaPrescricaoAtendimento(AghAtendimentos atendimento) {

		Date dataAtual = new Date();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento");
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataAtual));

		return criteria;
	}

	/**
	 * Retorna a última prescrição médica de um atendimento
	 * 
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @return
	 */
	public MpmPrescricaoMedica obterUltimaPrescricaoAtendimento(Integer atdSeq, Date dtReferencia, Date dataInicio, MpmPrescricaoMedica prescricaoMedicaNova) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), dtReferencia));
		criteria.add(Restrictions.lt(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataInicio));
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.DTHR_FIM.toString()));

		List<MpmPrescricaoMedica> lista = executeCriteria(criteria);
		if (lista.size() > 0) {
			MpmPrescricaoMedica ultima = lista.get(0); 
			//#41104 - no Oracle ao ocorrer o flush,o objeto é inserido e se for feito uma busca ele é retornado.No postgres não.
			//Essa teste evita que a prescricao que acabou se ser inserida retorne
			if(prescricaoMedicaNova != null && prescricaoMedicaNova.getId().equals(ultima.getId()) && lista.size()>1){
				return lista.get(1);
			}
			return lista.get(0);
		} else {
			return null;
		}
	}
	
	public MpmPrescricaoMedica obterUltimaPrescricaoAtendimento(Integer atdSeq, Date dtReferencia, Date dataInicio) {
		return obterUltimaPrescricaoAtendimento(atdSeq, dtReferencia, dataInicio, null);
	}

	public Boolean habilitarAltaSumario(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		return !(executeCriteriaCount(criteria) > 0);
	}

	public List<MpmPrescricaoMedica> obterMpmPrescricaoMedicaPorAghAtendimento(Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento));
		criteria.createAlias(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString(), "SV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SV."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmPrescricaoMedica.Fields.SERVIDOR_ATUALIZADA.toString(), "SA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SA."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesa", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria);
	}
	
	public List<MpmPrescricaoMedica> listarPrescricoesMedicasInicio(Integer seqAtendimento, Date dataHoraInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);

		if (seqAtendimento != null) {
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento));
		}

		if (dataHoraInicio != null) {
			criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataHoraInicio));
			criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataHoraInicio));
		}
		return executeCriteria(criteria);
	}

	public List<MpmPrescricaoMedica> listarPrescricoesMedicasFim(Integer seqAtendimento, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);

		if (seqAtendimento != null) {
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento));
		}

		if (dataHoraFim != null) {
			criteria.add(Restrictions.lt(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataHoraFim));
			criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataHoraFim));
		}
		return executeCriteria(criteria);
	}

	/**
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 * @return
	 */
	public List<ItemPrescricaoMedica> listarItensPrescricaoMedicaConfirmacao(MpmPrescricaoMedica prescricao, Date dataTrabalho) {

		DetachedCriteria criteria = obterCriteriaItensPrescricao(prescricao);

		criteria.add(Restrictions.in(MpmPrescricaoDieta.Fields.IND_PENDENTE.toString(), new DominioIndPendenteItemPrescricao[] {
				DominioIndPendenteItemPrescricao.P,
				DominioIndPendenteItemPrescricao.Y,
				DominioIndPendenteItemPrescricao.B,
				DominioIndPendenteItemPrescricao.E,
				DominioIndPendenteItemPrescricao.R }));

		criteria.add(Restrictions.or(Restrictions.ge(MpmPrescricaoDieta.Fields.CRIADO_EM.toString(), dataTrabalho),
				Restrictions.ge(MpmPrescricaoDieta.Fields.ALTERADO_EM.toString(), dataTrabalho)));

	

		return this.executeCriteria(criteria);

	}

	/**
	 * @param prescricao
	 * @return
	 */
	private DetachedCriteria obterCriteriaItensPrescricao(MpmPrescricaoMedica prescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ItemPrescricaoMedica.class);

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_MEDICA.toString(), prescricao));
		return criteria;
	}

	/**
	 * 
	 * Retorna todos os itens confirmados de uma prescrição.
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 * @return
	 */
	public List<ItemPrescricaoMedica> listarItensPrescricaoMedicaConfirmados(MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaItensPrescricao(prescricao);
		
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.IND_PENDENTE.toString(), DominioIndPendenteItemPrescricao.N));

		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
				Restrictions.eq(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(), prescricao.getDthrFim())));

		return this.executeCriteria(criteria);
	}

	/**
	 * Metodo para listar prescricoes medicas para gerar sumario, com data imp nao nula, 
	 * filtrando pelo atendimendo e datas de inicio e fim da prescricao medica.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoMedica> listarPrescricoesMedicasComDataImpSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		List<MpmPrescricaoMedica> lista = null;
		
		lista = this.executeCriteria(listarPrescricoesMedicasParaGerarSumario(seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao, true));
		
		return lista;
	}

	public List<MpmPrescricaoMedica> listarPrescricoesMedicasParaGerarSumarioDePrescricao	(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		List<MpmPrescricaoMedica> lista = null;
		lista = this.executeCriteria(listarPrescricoesMedicasParaGerarSumario(seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao, false));
		return lista;
	}
	
	/**
	 * Metodo para listar prescricoes medicas para gerar sumario, 
	 * filtrando pelo atendimendo e datas de inicio e fim da prescricao medica.
	 * 
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @param dataImpSumario
	 * @return
	 */
	private DetachedCriteria listarPrescricoesMedicasParaGerarSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao, Boolean dataImpSumario){
		Date auxDate = DateUtil.adicionaDias(dthrInicioPrescricao, -1);
		auxDate = DateUtils.truncate(auxDate, Calendar.DAY_OF_MONTH);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento));
		if(dataImpSumario){
			criteria.add(Restrictions.isNull(MpmPrescricaoMedica.Fields.DATA_IMP_SUMARIO.toString()));
		}
		criteria.add(Restrictions.lt(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dthrFimPrescricao));
		criteria.add(Restrictions.between(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(),
				auxDate, DateUtils.truncate(dthrFimPrescricao, Calendar.DAY_OF_MONTH)));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()));
		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.SEQ.toString()));
		return criteria;
	}

	/**
	 * Metodo para obter a prescricao medica com maior data de referencia,
	 *  filtrando pelo atendimendo e datas de inicio e fim da prescricao medica.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoMedicaComMaiorDataReferenciaParaGerarSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		Date auxDate = DateUtils.truncate(dthrInicioPrescricao, Calendar.DAY_OF_MONTH); 
		auxDate = DateUtil.adicionaDias(auxDate, -1); 

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento))
		.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.DATA_IMP_SUMARIO.toString()))
		.add(Restrictions.lt(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dthrFimPrescricao))
		.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()))
		.add(Restrictions.between(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), auxDate,
				DateUtils.truncate(dthrFimPrescricao, Calendar.DAY_OF_MONTH)));

		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()));
		
		List<MpmPrescricaoMedica> list = executeCriteria(criteria, 0, 1, null, false);
		
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}
	
	/**
	 * metodo para listar prescricoes medicas do atendimento com data referencia menor que data inicio prescricao.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MpmPrescricaoMedica> listarPrescricoesMedicasDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Integer seqAtendimento, Date dthrInicioPrescricao){
		Date auxDthrInicioPrescricao = DateUtils.truncate(dthrInicioPrescricao, Calendar.DAY_OF_MONTH); 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.add(Restrictions.lt(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), auxDthrInicioPrescricao));
		
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()));
		
		return executeCriteria(criteria, 0, 1, null, false); 
	}
	
	/**
	 * ORADB CURSOR c_prcr
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<Date> executarCursorPrCr(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * ORADB CURSOR c_prescricao
	 * 
	 * @return
	 */
	public Boolean executarCursorPrescricao(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);

		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA_MATRICULA.toString()));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.rowCount());

		Integer res = (Integer) this.executeCriteriaUniqueResult(criteria);

		return (res != null && res > 0) ? true : false;
	}

//	public List<MpmPrescricaoMedica> pesquisarPrescricaoMedicaSituacaoDispensacao(
//			Integer firstResult, Integer maxResult, String orderProperty,
//			boolean asc, String leitoId, Integer prontuario, String nome,
//			Date dtHrInicio, Date dtHrFim, Integer seqPrescricao) {
//		
//		DetachedCriteria criteria = obterCriteriaFiltroSituacaoDispensacao(
//				leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao);
//		
//		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.SEQ.toString()));
//		
//		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
//	}

//	public Long pesquisarPrescricaoMedicaSituacaoDispensacaoCount(
//			String leitoId, Integer prontuario, String nome, Date dtHrInicio,
//			Date dtHrFim, Integer seqPrescricao) {
//		
//		DetachedCriteria criteria = obterCriteriaFiltroSituacaoDispensacao(
//				leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao);
//		
//		return executeCriteriaCount(criteria);
//	}
	
	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacaoUnion1(String leitoId, Integer prontuario, String nome, Date dtHrInicio,
			Date dtHrFim, Integer seqPrescricao, Boolean indPacAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "mpm");
		
		criteria.createAlias("mpm."+MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		
 		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString()).as(MpmPrescricaoMedicaVO.Fields.LEITO.toString()))
		.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_PRONTUARIO.toString()))
		.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_NOME.toString()))
		.add(Projections.property("mpm." + MpmPrescricaoMedica.Fields.SEQ.toString()).as(MpmPrescricaoMedicaVO.Fields.MPM_SEQ.toString()))
		.add(Projections.property("mpm." + MpmPrescricaoMedica.Fields.SEQ.toString()).as(MpmPrescricaoMedicaVO.Fields.PRESCRICAO_INT.toString()))
		.add(Projections.property("mpm." + MpmPrescricaoMedica.Fields.IDATDSEQ.toString()).as(MpmPrescricaoMedicaVO.Fields.ATD_SEQ.toString()))
		.add(Projections.property("mpm." + MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_INICIO.toString()))
		.add(Projections.property("mpm." + MpmPrescricaoMedica.Fields.DTHR_FIM.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_FIM.toString()))
		;
 		
	criteria.setProjection(projectionList);
	
		if (seqPrescricao != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SEQ.toString(), seqPrescricao));
		}
		
		if (StringUtils.isNotBlank(leitoId)){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoId));
		}
		
		if (prontuario != null){
			criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		
		if (StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike("pac." + AipPacientes.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if (dtHrInicio != null){
			criteria.add(Restrictions.ge("mpm." + MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dtHrInicio));
		}
		
		if (dtHrFim != null){
			criteria.add(Restrictions.le("mpm." + MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dtHrFim));
		}
		
		if(indPacAtendimento){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MpmPrescricaoMedicaVO.class));
		
		return this.executeCriteria(criteria);
		
	}
		
	private DetachedCriteria obterCriteriaFiltroSituacaoDispensacao(String leitoId, Integer prontuario, String nome, Date dtHrInicio,
			Date dtHrFim, Integer seqPrescricao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		
		if (seqPrescricao != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SEQ.toString(), seqPrescricao));
		}
		
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento");
		criteria.add(Restrictions.eq("atendimento." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (StringUtils.isNotBlank(leitoId)){
			criteria.add(Restrictions.eq("atendimento." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoId));
		}
		
		criteria.createAlias("atendimento." + AghAtendimentos.Fields.PACIENTE.toString(), "paciente");
		if (prontuario != null){
			criteria.add(Restrictions.eq("paciente." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		
		if (StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike("paciente." + AipPacientes.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if (dtHrInicio != null){
			criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dtHrInicio));
		}
		
		if (dtHrFim != null){
			criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dtHrFim));
		}
		
		return criteria;
		
	}
	
	public Long obterQuantidadePrescricoesMedicasEmUsoPeloId(MpmPrescricaoMedicaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), id.getAtdSeq()));
		criteria.add(Restrictions.ne(MpmPrescricaoMedica.Fields.SEQ.toString(), id.getSeq()));
		criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), new Date()));
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SITUACAO.toString(), DominioSituacaoPrescricao.U));

		return executeCriteriaCount(criteria);
	}
	
	public MpmPrescricaoMedica obterPrescricaoPorId(MpmPrescricaoMedicaId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), id.getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SEQ.toString(), id.getSeq()));

		return (MpmPrescricaoMedica) executeCriteriaUniqueResult(criteria);
		
	}
	/*
	 * Estória #5387
	// Métodos em fase de construção Renato 31.05.2011

	public Long pesquisarItensPrescricaoMedicaCount(Integer unidadeSolicitanteID, String unidadeSolicitanteNome,
			Integer prontuario, String nome, Date dtHrInclusaoItem,
			Integer medicamentoID, Integer medicamentoNome, String situacao,
			Integer farmaciaID, String farmaciaNome) {
		
		DetachedCriteria criteria = obterCriteriaFiltroItensPrescricaoMedica(
				unidadeSolicitanteID, unidadeSolicitanteNome, prontuario, nome, dtHrInclusaoItem, medicamentoID, medicamentoNome, situacao, farmaciaID, farmaciaNome);
	
	//	return executeCriteriaCount(criteria);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaFiltroItensPrescricaoMedica(
			Integer unidadeSolicitanteID, String unidadeSolicitanteNome,
			Integer prontuario, String nome, Date dtHrInclusaoItem,
			Integer medicamentoID, Integer medicamentoNome, String situacao,
			Integer farmaciaID, String farmaciaNome) {

	

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		
		return criteria;
	}
*/
	
	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(Date dataEmissaoInicio, Date dataEmissaoFim) {
		String alias = "pme";
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, alias);

		criteria.setProjection(Projections.projectionList()
						.add(Projections.groupProperty(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()),QuantidadePrescricoesDispensacaoVO.Fields.DATA_EMISSAO.toString())
						.add(Projections.count(MpmPrescricaoMedica.Fields.SERVIDOR_CODIGO.toString()),QuantidadePrescricoesDispensacaoVO.Fields.QUANTIDADE_PRESCRICOES.toString())
		);

		criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), dataEmissaoInicio));
		
		if(dataEmissaoFim != null){
			criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString(), dataEmissaoFim));
		}
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA_MATRICULA.toString()));
		
		criteria.add(Restrictions.and(
				Subqueries.propertyIn(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), getAfaDispensacao(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ, alias)), 
				Subqueries.propertyIn(MpmPrescricaoMedica.Fields.SEQ.toString(), getAfaDispensacao(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ, alias))
				));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadePrescricoesDispensacaoVO.class));
		
		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria getAfaDispensacao(AfaDispensacaoMdtos.Fields coluna, String alias) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		cri.setProjection(Projections.property(coluna.toString()));
		String atdSeqMain = alias.concat(".".concat(MpmPrescricaoMedica.Fields.ATD_SEQ.toString()));
		String seqMain = alias.concat(".".concat(MpmPrescricaoMedica.Fields.SEQ.toString()));
		
		cri.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString(), seqMain));
		cri.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), atdSeqMain));
		cri.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoDispensacaoMdto.D, DominioSituacaoDispensacaoMdto.T)));
		
		return cri;
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR C_PRESC) obtém a prescrição
	 * médica
	 * 
	 * @param atdSeq
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoMedica(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		DetachedCriteria criteriaAtendimento = criteria.createCriteria(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString());
		criteriaAtendimento.add(Restrictions.eq(AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		List<MpmPrescricaoMedica> listaPrescricao = this.executeCriteria(criteria, 0, 1, null, true);
		if (listaPrescricao != null && !listaPrescricao.isEmpty()) {
			return listaPrescricao.get(0);
		}
		return null;
	}

	/**
	 * Método que obtém uma Prescrição Médica pelo id do atendimento e a data de
	 * Início
	 * 
	 * @param atdSeq
	 * @param dataInicio
	 * @return MpmPrescricaoMedicas
	 */
	public MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimentoEDataInicio(Integer atdSeq, Date dataInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataInicio));
		criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataInicio));

		List<MpmPrescricaoMedica> result = executeCriteria(criteria, 0, 1, null, true);
		return (result != null && !result.isEmpty()) ? (MpmPrescricaoMedica) result.get(0) : null;
	}

	/**
	 * Método que obtém uma Prescrição Médica pelo id do atendimento e a data de
	 * Fim
	 * 
	 * @param atdSeq
	 * @param dataFim
	 * @return MpmPrescricaoMedicas
	 */
	public MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimentoEDataFim(Integer atdSeq, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq("id.atdSeq", atdSeq));
		// para a data de fim, as restrições são diferentes
		criteria.add(Restrictions.lt("dthrInicio", dataFim));
		criteria.add(Restrictions.ge("dthrFim", dataFim));

		List<MpmPrescricaoMedica> result = executeCriteria(criteria, 0, 1, null, true);
		return (result != null && !result.isEmpty()) ? (MpmPrescricaoMedica) result.get(0) : null;
	}

	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,Integer numeroProntuario, AipPacientes paciente) {
		
		DetachedCriteria criteria = obterCriteriapesquisarAlterarDispensacaoDeMedicamentos(
				leito, numeroProntuario, paciente, dthrDataInicioValidade, dthrDataFimValidade, numeroPrescricao);
		
		return executeCriteriaCount(criteria);
	}

	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(Integer firstResult, Integer maxResult, String orderProperty,
			Boolean asc, AinLeitos leito, Integer numeroPrescricao, Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente) {
		
		DetachedCriteria criteria = obterCriteriapesquisarAlterarDispensacaoDeMedicamentos(
				leito, numeroProntuario, paciente, dthrDataInicioValidade, dthrDataFimValidade, numeroPrescricao);
		
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria obterCriteriapesquisarAlterarDispensacaoDeMedicamentos(AinLeitos leito, Integer numeroProntuario, AipPacientes paciente,
			Date dthrDataInicioValidade, Date dthrDataFimValidade, Integer numeroPrescricao) {
		String leitoID = leito != null ? leito.getLeitoID():null; 
		DetachedCriteria criteria = obterCriteriaFiltroSituacaoDispensacao(
				leitoID, numeroProntuario, null, dthrDataInicioValidade, dthrDataFimValidade, numeroPrescricao);
		
		if(paciente != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATENDIMENTO_PACIENTE.toString(), paciente));
		}
		
		return criteria;
	}
	
	/**
	 * Método que obtém Prescrições Médicas pelo atendimento, 
	 * cuja data de fim da prescrição seja menor ou igual que a data
	 * de fim informada, e menor ou igual a data atual.
	 * 
	 * @param atdSeq
	 * @param dataFim
	 * @return MpmPrescricaoMedicas
	 */
	public List<MpmPrescricaoMedica> pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(Integer atdSeq, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		
		if(atdSeq  != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		}
		
		if(dataFim != null){
			criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataFim));
			criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), new Date()));
		}
		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Método que remove um item da prescrição médica
	 * 
	 * @param item
	 */
	public void removerItemPrescricaoMedica(ItemPrescricaoMedica<Object> item) {
		dataAcess.remove(item);
	}
	
	public MpmPrescricaoMedica obterPrescricaoVigente(Integer atdSeq) {
		Date sysdate = new Date();
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), sysdate));
		criteria.add(Restrictions.gt(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), sysdate));

		List<MpmPrescricaoMedica> list = executeCriteria(criteria);
		if (list.size() > 1) {
			LOG.warn("Inconsistência ao obter a prescricao vigente, deve encontrar apenas uma e encontrou " + list.size());
			LOG.warn("para atdSeq=" + atdSeq + " e sysdate=" + sysdate);
		} else if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	* Busca uma lista de VOs de Nutricao Parenteral com prescricoes realizadas para o atendimento informado dentro
	* das datas do processamento informado. Utilizado no modulo de custos.
	* 
	* @param atendimento
	* @param processamento
	* @return Lista de VOs de Nutricao Parenteral
	* @author rhrosa
	*/
	public List<NutricaoParenteralVO> buscarNutricoesParenteraisPrescritas(Integer atendimento, Date dataInicioProcessamento, Date dataFimProcesasmento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "pmd");
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.groupProperty("npt."+MpmPrescricaoNpt.Fields.ID_SEQ.toString()), NutricaoParenteralVO.Fields.SEQ_NPT.toString())
		.add(Projections.groupProperty("pmd."+MpmPrescricaoMedica.Fields.ATD_SEQ), NutricaoParenteralVO.Fields.ATD_SEQ.toString())
		.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.INT_SEQ), NutricaoParenteralVO.Fields.INT_SEQ.toString())
		.add(Projections.groupProperty("npt."+MpmPrescricaoNpt.Fields.PED_SEQ), NutricaoParenteralVO.Fields.PED_SEQ.toString())
		.add(Projections.groupProperty("pmd."+MpmPrescricaoMedica.Fields.DTHR_INICIO), NutricaoParenteralVO.Fields.PRESCRICAO_MEDICA_DATA_INICIO.toString())
		.add(Projections.groupProperty("pmd."+MpmPrescricaoMedica.Fields.DTHR_FIM), NutricaoParenteralVO.Fields.PRESCRICAO_MEDICA_DATA_FIM.toString())
		.add(Projections.groupProperty("npt."+MpmPrescricaoNpt.Fields.DTHR_INICIO), NutricaoParenteralVO.Fields.PRESCRICAO_PARENTERAL_DATA_INICIO.toString())
		.add(Projections.groupProperty("npt."+MpmPrescricaoNpt.Fields.DTHR_FIM), NutricaoParenteralVO.Fields.PRESCRICAO_PARENTERAL_DATA_FIM.toString())
			.add(Projections.count("npt."+MpmPrescricaoNpt.Fields.ID_SEQ), NutricaoParenteralVO.Fields.QUANTIDADE.toString())
		);
		
		criteria.createCriteria("pmd."+MpmPrescricaoMedica.Fields.PRESCRICOES_NPT, "npt", JoinType.INNER_JOIN);
		criteria.setFetchMode("pmd."+MpmPrescricaoMedica.Fields.PRESCRICOES_NPT, FetchMode.JOIN);
		
		criteria.createCriteria("pmd."+MpmPrescricaoMedica.Fields.ATENDIMENTO, "atd", JoinType.INNER_JOIN);
		criteria.setFetchMode("pmd."+MpmPrescricaoMedica.Fields.ATENDIMENTO, FetchMode.JOIN);
		
		
		criteria.add(Restrictions.eq("pmd."+MpmPrescricaoMedica.Fields.ATD_SEQ, atendimento));
		criteria.add(Restrictions.between("pmd."+ MpmPrescricaoMedica.Fields.DTHR_INICIO, dataInicioProcessamento, dataFimProcesasmento));
		criteria.add(Restrictions.eqProperty("pmd."+MpmPrescricaoMedica.Fields.DTHR_FIM, "npt."+MpmPrescricaoNpt.Fields.DTHR_FIM));
		criteria.add(Restrictions.eq("npt."+MpmPrescricaoNpt.Fields.IND_PENDENTE, DominioSimNao.N));

		criteria.addOrder(Order.asc("pmd."+MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc("pmd."+MpmPrescricaoMedica.Fields.DTHR_FIM.toString()));
		criteria.addOrder(Order.asc("npt."+MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc("npt."+MpmPrescricaoNpt.Fields.DTHR_FIM.toString()));
		criteria.addOrder(Order.asc("pmd."+MpmPrescricaoMedica.Fields.ATD_SEQ.toString()));
		criteria.addOrder(Order.asc("npt."+MpmPrescricaoNpt.Fields.ID_SEQ.toString()));
		criteria.addOrder(Order.asc("atd."+AghAtendimentos.Fields.INT_SEQ.toString()));
		criteria.addOrder(Order.asc("npt."+MpmPrescricaoNpt.Fields.PED_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NutricaoParenteralVO.class));
		
		return executeCriteria(criteria);
	}
	
	public MpmPrescricaoMedica obterPrescricaoComAtendimentoPaciente(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "atdMae", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atdMae."+AghAtendimentos.Fields.PACIENTE.toString(), "pacMae", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.INTERNACAO.toString(), "int", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.CID_ATENDIMENTOS.toString(), "cidAtd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cidAtd."+MpmCidAtendimento.Fields.CID.toString(), "cid", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "uni", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.QUARTO.toString(), "quarto", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("esp."+AghEspecialidades.Fields.CLINICA.toString(), "cli", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SEQ.toString(), seq));
		return (MpmPrescricaoMedica) executeCriteriaUniqueResult(criteria);
	}

	public MpmPrescricaoMedica obterPrescricaoComFatConvenioSaude(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.PACIENTE.toString(), "paciente", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "atendimentoMae", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.INTERNACAO.toString(), "internacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.QUARTO.toString(), "quarto", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "atendimentoUrgencia", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.HOSPITAL_DIA.toString(), "hospitalDia", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), "atendimentoPacExterno", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.CONSULTA.toString(), "consulta", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.CIRURGIAS.toString(), "cirurgias", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("internacao."+AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenioSaudePlano", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("paciente."+"aipPesoPacienteses", "aipPesoPacienteses", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SEQ.toString(), seq));
		return (MpmPrescricaoMedica) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #34382 - Busca prescrição médica de um atendimento
	 * @param atdSeq
	 * @return
	 */
	
	public List<MpmPrescricaoMedica> obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #39002 - Busca Ultima Prescricao Medica
	 * @param atdSeq
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA.toString()));
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.CRIADO_EM.toString()));
		
		
 		List<MpmPrescricaoMedica> results = executeCriteria(criteria);
		
 		return results != null && results.size() > 0 ? results.get(0) : null;

	}
	
	/**
	 * @author renan_boni
	 *  Metodo incluido durante a migração de uma controller para a versão 6 
	 * */
	public List<MpmPrescricaoMedica> pesquisaPrescricoesMedicasPorAtendimento(Integer seqAtendimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), seqAtendimento));
		return executeCriteria(criteria);
	}
	
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicaNaoPendentes(Integer atdSeq, DominioSituacaoPrescricao situacaoPrescricao) {
		List<MpmPrescricaoMedica> retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		if(situacaoPrescricao != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.SITUACAO.toString(), situacaoPrescricao));	
		}
		criteria.add(Restrictions.isNull(MpmPrescricaoMedica.Fields.DTHR_INICIO_MVTO_PENDENTE.toString()));	
		criteria.addOrder(Order.asc(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));
		retorno = executeCriteria(criteria);
		for (MpmPrescricaoMedica prescricao : retorno){
			this.lockEntity(prescricao, LockModeType.READ);
		}
		return retorno;
	}
	
	public Object[] verificarPossuiPlanoAlta(Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class,"AAT");
		criteria.createAlias(AghAtendimentos.Fields.CONTROLE_PREVIA_ALTAS.toString(), "CPA");
		criteria.add(Restrictions.eq("AAT."+  AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq("AAT." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("CPA." + MpmControlPrevAltas.Fields.DT_FIM.toString())).
			add(Projections.property("CPA." + MpmControlPrevAltas.Fields.RESPOSTA.toString()));
		criteria.setProjection(projections);
		
		criteria.addOrder(Order.desc("CPA." + MpmControlPrevAltas.Fields.CRIADO_EM.toString()));
		
		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public List<MpmPrescricaoMedica> pesquisaPrescricoesMedicasPorAtendimentoDataSolicitacao(Integer atdSeq, Date dataSolicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString(), dataSolicitacao));
		criteria.add(Restrictions.ge(MpmPrescricaoMedica.Fields.DTHR_FIM.toString(), dataSolicitacao));
		return executeCriteria(criteria);
	}

	public MpmPrescricaoMedicaVO pesquisaPrescricaoMedicaPorAtendimentoESeq(Integer atdSeq, Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "prescricao");
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.INNER_JOIN);
		criteria.createAlias("atendimento." + AghAtendimentos.Fields.PACIENTE.toString(), "paciente", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("prescricao." + MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("prescricao." + MpmPrescricaoMedica.Fields.SEQ.toString(), seq));
 		ProjectionList projectionList = Projections.projectionList()
			.add(Projections.property("atendimento." + AghAtendimentos.Fields.PRONTUARIO.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_PRONTUARIO.toString()))
			.add(Projections.property("atendimento." + AghAtendimentos.Fields.PAC_CODIGO.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_CODIGO.toString()))
			.add(Projections.property("paciente." + AipPacientes.Fields.NOME.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_NOME.toString()))
			.add(Projections.property("prescricao." + MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_INICIO.toString()))
			.add(Projections.property("prescricao." + MpmPrescricaoMedica.Fields.DTHR_FIM.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_FIM.toString()));
 		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmPrescricaoMedicaVO.class));
		return (MpmPrescricaoMedicaVO) executeCriteriaUniqueResult(criteria);
	}

	public Boolean existePrescricaoAnteriorPaciente(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));

        if(isOracle()) {
            criteria.add(Restrictions.sqlRestriction("TRUNC({alias}." + MpmPrescricaoMedica.Fields.CRIADO_EM.name()+") <= ?", DateUtil.truncaData(DateUtil.adicionaDias(new Date(), -1)), DateType.INSTANCE));
        } else {
            criteria.add(Restrictions.sqlRestriction("DATE_TRUNC('DAY', {alias}." + MpmPrescricaoMedica.Fields.CRIADO_EM.name()+") <= ?", DateUtil.truncaData(DateUtil.adicionaDias(new Date(), -1)), DateType.INSTANCE));
        }

		return executeCriteriaCount(criteria) > 0 ? true : false;
	}

	/**
	 * Obtém Dados do Paciente selecionado para ser exibido na Central de Mensagens.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @param prescricaoSeq - Código da Prescrição Médica
	 * 
	 * @return Dados do Paciente
	 */
	public MpmPrescricaoMedica obterPacienteCentralMensagens(Integer atdSeq, Integer prescricaoSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class, "MPM");

		criteria.createAlias("MPM." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LEITO.toString(), "LTO");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.QUARTO.toString(), "QRT");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		criteria.setFetchMode("MPM." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode("ATD." + AghAtendimentos.Fields.LEITO.toString(), FetchMode.JOIN);
		criteria.setFetchMode("ATD." + AghAtendimentos.Fields.QUARTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		if (prescricaoSeq != null) {
			criteria.add(Restrictions.eq("MPM." + MpmPrescricaoMedica.Fields.SEQ.toString(), prescricaoSeq));
		}

		List<MpmPrescricaoMedica> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}
	
	public Long obterQuantidadePrescricoesVerificarAnamnese(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD" , JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.INTERNACAO.toString(), "AIN", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		List<MpmPrescricaoMedica> lista = executeCriteria(criteria);
		Set<Integer> listSeqPrescricaoMedica = new HashSet<>(); 
		for (MpmPrescricaoMedica pem : lista) {
			Date dataReferencia = pem.getDtReferencia();
			Date dataIngressoUnidade = pem.getAtendimento().getDthrIngressoUnidade();
			if (DateUtil.validaDataMaiorIgual(DateUtil.truncaData(dataReferencia), DateUtil.truncaData(dataIngressoUnidade))) {
				for(AinMovimentosInternacao movimento : pem.getAtendimento().getInternacao().getMovimentosInternacao()){
					Date dthrLancamento = movimento.getDthrLancamento();
					if(DateUtil.validaDataMaiorIgual(DateUtil.truncaData(dataReferencia), DateUtil.truncaData(dthrLancamento))){
						if(movimento.getTmiSeq().intValue() == 1 || 
								movimento.getTmiSeq().intValue() == 2 ||
								movimento.getTmiSeq().intValue() == 11 ||
								movimento.getTmiSeq().intValue() == 12 ){
							listSeqPrescricaoMedica.add(pem.getId().getSeq());
						}
					}
				}
			}
		}
		return (long) listSeqPrescricaoMedica.size();
	}

	public MpmPrescricaoMedica obterUltimaPrescricaoMedicaPorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteria.createAlias(MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.SEQ.toString()));
		List<MpmPrescricaoMedica> retorno = executeCriteria(criteria);
		if(retorno != null && !retorno.isEmpty()){
			return retorno.get(0);
		}else{
			return null;
		}
	}
}