package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.core.utils.DateUtil;


public class EpePrescricaoEnfermagemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpePrescricaoEnfermagem> {

	private static final long serialVersionUID = 1564464039885609059L;

	
	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.EPE_PEN_SQ1);
	}	
	
	
	@Override

	protected void obterValorSequencialId(EpePrescricaoEnfermagem elemento) {
		if (elemento == null || elemento.getAtendimento() == null) {
			throw new IllegalArgumentException("O objeto EpePrescricaoEnfermagem não pode ser nulo ou não pode ter um Atendimento nulo");
		}	

		EpePrescricaoEnfermagemId id = new EpePrescricaoEnfermagemId();
		id.setAtdSeq(elemento.getAtendimento().getSeq());
		id.setSeq(this.obterProximoSequencialId(elemento.getAtendimento().getSeq()));
		elemento.setId(id);
	}	

	private Integer obterProximoSequencialId(Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.setProjection(Projections.max(EpePrescricaoEnfermagem.Fields.SEQ.toString()));
		final Object objMax = this.executeCriteriaUniqueResult(criteria);	

		Integer maxSeqp = (Integer) objMax;
		Integer seqp = 0;

		if (maxSeqp != null) {
			seqp = maxSeqp.intValue();
		}
		seqp = seqp + 1;
		return seqp;
	}	

	

	/**
	 * ORADB CURSOR c_epe
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<Date> executarCursorEpe(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ
				.toString(), atdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
				.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
				.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoVigentePorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString(), new Date()));
		criteria.add(Restrictions.gt(EpePrescricaoEnfermagem.Fields.DTHR_FIM.toString(), new Date()));
		return executeCriteria(criteria);
	}
	
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoFuturaPorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);

		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.gt(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString(), new Date()));
		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoDiferentePorAtendimento(Integer atdSeq, Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.ne(EpePrescricaoEnfermagem.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoDiferentePorAtendimentoEDataReferencia(Integer atdSeq, Integer seq, Date dataReferencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.ne(EpePrescricaoEnfermagem.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString(), dataReferencia));
		return executeCriteria(criteria);
	}
	/**
	 * Retorna as prescrições de um atendimento cuja dataFim seja maior que a
	 * data informada
	 * 
	 * @param atendimento
	 * @param data
	 * @return lista de prescrições
	 */
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoAtendimentoDataFim(
			final Integer atdSeq, final Date data) {

		List<EpePrescricaoEnfermagem> retorno = null;
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);

		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));

		if (data != null) {
			criteria.add(Restrictions.gt(EpePrescricaoEnfermagem.Fields.DTHR_FIM.toString(), data));
		}

		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString()));

		retorno = executeCriteria(criteria);
		
		for (EpePrescricaoEnfermagem prescricao : retorno){
			this.lockEntity(prescricao, LockModeType.READ);
		}
		
		return retorno;
	}	
	
	/**
	 * Retorna a última prescrição de enfermagem de um atendimento
	 * 
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @return
	 */
	public EpePrescricaoEnfermagem obterUltimaPrescricaoAtendimento(Integer atdSeq,
			Date dtReferencia, Date dataInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.le(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString(), dtReferencia));
		criteria.add(Restrictions.lt(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString(), dataInicio));
		criteria.addOrder(Order.desc(EpePrescricaoEnfermagem.Fields.DTHR_FIM.toString()));

		return verificaQuantidadePrescricaoEnfermagem(criteria);
		
	}
	
	private EpePrescricaoEnfermagem verificaQuantidadePrescricaoEnfermagem (
			DetachedCriteria criteria) {
		List<EpePrescricaoEnfermagem> lista = executeCriteria(criteria);
		if (!lista.isEmpty() && lista.size()>1 || !lista.isEmpty() && lista.size()==1) {
			return lista.get(0);
		} else {
			return null;
		}
	}
	
	public List<EpePrescricaoEnfermagem> listarPrescricoesEnfermagemInicio(
			Integer seqAtendimento, Date dataHoraInicio) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);

		if (seqAtendimento != null) {
			criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ
					.toString(), seqAtendimento));
		}

		if (dataHoraInicio != null) {
			criteria.add(Restrictions.le(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
					.toString(), dataHoraInicio));
			criteria.add(Restrictions.gt(EpePrescricaoEnfermagem.Fields.DTHR_FIM
					.toString(), dataHoraInicio));
		}

		return executeCriteria(criteria);
	}
	
	public List<EpePrescricaoEnfermagem> listarPrescricoesEnfermagemFim(
			Integer seqAtendimento, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);

		if (seqAtendimento != null) {
			criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ
					.toString(), seqAtendimento));
		}

		if (dataHoraFim != null) {
			criteria.add(Restrictions.lt(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
					.toString(), dataHoraFim));
			criteria.add(Restrictions.ge(EpePrescricaoEnfermagem.Fields.DTHR_FIM
					.toString(), dataHoraFim));
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * Metodo para obter a prescricao enfermagem com maior data de referencia,
	 *  filtrando pelo atendimendo e datas de inicio e fim da prescricao enfermagem.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @return
	 */
	public EpePrescricaoEnfermagem obterPrescricaoEnfermagemComMaiorDataReferenciaParaGerarSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		Date auxDate = DateUtils.truncate(dthrInicioPrescricao, Calendar.DAY_OF_MONTH); 
		auxDate = DateUtil.adicionaDias(auxDate, -1); 

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ
				.toString(), seqAtendimento));
		criteria.add(Restrictions
				.isNotNull(EpePrescricaoEnfermagem.Fields.DATA_IMP_SUMARIO
						.toString()));
		criteria.add(Restrictions.lt(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
				.toString(), dthrFimPrescricao));
		criteria.add(Restrictions
				.isNotNull(EpePrescricaoEnfermagem.Fields.SERVIDOR_VALIDA
						.toString()));
		criteria.add(Restrictions.between(
				EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString(), auxDate,
				DateUtils.truncate(dthrFimPrescricao, Calendar.DAY_OF_MONTH)));

		criteria.addOrder(Order.desc(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString()));
		
		List<EpePrescricaoEnfermagem> lista = executeCriteria(criteria, 0, 1, null, false);
		
		return lista != null && !lista.isEmpty() ? lista.get(0): null;
	}
	
	/**
	 * Metodo para listar prescricoes enfermagem do atendimento com data referencia menor que data inicio prescricao.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EpePrescricaoEnfermagem> listarPrescricoesEnfermagemDoAtendimentoComDataReferenciaMenorQueDataInicioPrescricao(Integer seqAtendimento, Date dthrInicioPrescricao){
		Date auxDthrInicioPrescricao = DateUtils.truncate(dthrInicioPrescricao, Calendar.DAY_OF_MONTH); 
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpePrescricaoEnfermagem.class);
		
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.isNotNull(EpePrescricaoEnfermagem.Fields.SERVIDOR_VALIDA.toString()));
		criteria.add(Restrictions.lt(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString(), auxDthrInicioPrescricao));
		
		criteria.addOrder(Order.desc(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString()));
		
		return executeCriteria(criteria, 0, 1, null, false); 
	}
	
	
	/**
	 * Metodo para listar prescricoes enfermagem para gerar sumario, com data imp nao nula, 
	 * filtrando pelo atendimendo e datas de inicio e fim da prescricao enfermagem.
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @return
	 */
	public List<EpePrescricaoEnfermagem> listarPrescricoesEnfermagemComDataImpSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		List<EpePrescricaoEnfermagem> lista = null;
		
		lista = this.executeCriteria(listarPrescricoesEnfermagemParaGerarSumario(seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao, true));
		
		return lista;
	}
	
	/**
	 * Metodo para listar prescricoes enfermagem para gerar sumario, 
	 * filtrando pelo atendimendo e datas de inicio e fim da prescricao enfermagem.
	 * 
	 * @param seqAtendimento
	 * @param dthrInicioPrescricao
	 * @param dthrFimPrescricao
	 * @param dataImpSumario
	 * @return
	 */
	private DetachedCriteria listarPrescricoesEnfermagemParaGerarSumario(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao, Boolean dataImpSumario){
		Date auxDate = DateUtil.adicionaDias(dthrInicioPrescricao, -1);
		auxDate = DateUtils.truncate(auxDate, Calendar.DAY_OF_MONTH);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), seqAtendimento));
		if(dataImpSumario){
			criteria.add(Restrictions.isNull(EpePrescricaoEnfermagem.Fields.DATA_IMP_SUMARIO.toString()));
		}

		criteria.add(Restrictions.lt(EpePrescricaoEnfermagem.Fields.DTHR_INICIO
				.toString(), dthrFimPrescricao));
		criteria.add(Restrictions.between(
				EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString(),
				auxDate, DateUtils.truncate(dthrFimPrescricao, Calendar.DAY_OF_MONTH)));
		criteria.add(Restrictions
				.isNotNull(EpePrescricaoEnfermagem.Fields.SERVIDOR_VALIDA
						.toString()));

		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.DT_REFERENCIA.toString()));
		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.SEQ.toString()));
		
		return criteria;
	}
	
	
	public List<EpePrescricaoEnfermagem> listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		List<EpePrescricaoEnfermagem> lista = null;
		
		lista = this.executeCriteria(listarPrescricoesEnfermagemParaGerarSumario(seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao, false));
		
		return lista;
	}
	
	/**
	 * Método que obtém Prescrições de Enfermagem pelo atendimento, 
	 * cuja data de fim da prescrição seja menor ou igual que a data
	 * de fim informada, e menor ou igual a data atual.
	 * 
	 * @param atdSeq
	 * @param dataFim
	 * @return EpePrescricaoEnfermagem
	 */
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimentoEDataFimAteDataAtual(
			Integer atdSeq, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.ge(EpePrescricaoEnfermagem.Fields.DTHR_FIM.toString(), dataFim));
		criteria.add(Restrictions.ge(EpePrescricaoEnfermagem.Fields.DTHR_FIM.toString(), new Date()));
		criteria.addOrder(Order.asc(EpePrescricaoEnfermagem.Fields.DTHR_INICIO.toString()));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * #34383 - Busca prescrição enfermagem de um atendimento
	 * @param atdSeq
	 * @return
	 */
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimento(
			Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricaoEnfermagem.class);
		criteria.add(Restrictions.eq(EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(EpePrescricaoEnfermagem.Fields.SERVIDOR_VALIDA.toString()));
		criteria.addOrder(Order.desc(EpePrescricaoEnfermagem.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	
}
