package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;

public class MpmPrescricaoCuidadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoCuidado> {

	private static final long serialVersionUID = -2190214815092661496L;

	@Override
	protected void obterValorSequencialId(MpmPrescricaoCuidado prescricaoCuidado) {
		if (prescricaoCuidado.getId() == null) {
			prescricaoCuidado.setId(new MpmPrescricaoCuidadoId());
		}
		prescricaoCuidado.getId().setSeq(
				obterValorSequencialId().longValue());
	}

	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.MPM_PCU_SQ1);
	}

	/**
	 * Lista os cuidados prescritos através chave da prescrição
	 * médica(pme_seq+atd_seq) onde a data de fim do cuidado seja igual a data
	 * da prescrição
	 * 
	 * @param pmeId
	 * @param dthrFim
	 * @return
	 */
	public List<MpmPrescricaoCuidado> pesquisarCuidadosMedicos(
			MpmPrescricaoMedicaId pmeId, Date dthrFim, Boolean listarTodas) {
		if (pmeId == null || dthrFim == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);

		criteria.setFetchMode(MpmPrescricaoCuidado.Fields.MPM_TIPO_FREQ_APRAZAMENTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmPrescricaoCuidado.Fields.CDU.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(
				MpmPrescricaoCuidado.Fields.PRESCRICAO_MEDICA_ID.toString(),
				pmeId));

		if(!listarTodas) {
			criteria.add(Restrictions.or(Restrictions
					.isNull(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString()),
					Restrictions.eq(
							MpmPrescricaoCuidado.Fields.DTHR_FIM.toString(),
							dthrFim)));
		}
		return executeCriteria(criteria);
	}

	/**
	 * Método que pesquisa a lista de cuidados da prescrição mais recente
	 * 
	 * @param pmeAtdSeq
	 * @param dtFimLida
	 * @return
	 */
	public List<MpmPrescricaoCuidado> pesquisarCuidadosMedicosUltimaPrescricao(
			Integer pmeAtdSeq, Date dtFimLida,
			MpmPrescricaoMedica prescricaoMedicaAnterior) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), prescricaoMedicaAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.PME_SEQ
				.toString(), prescricaoMedicaAnterior.getId().getSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.DTHR_FIM
				.toString(), prescricaoMedicaAnterior.getDthrFim()));
		criteria
				.addOrder(Order.asc(MpmPrescricaoCuidado.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obter cuidado da prescrição pelo seu ID.
	 * 
	 * @param pmeAtdSeq
	 * @param seq
	 * @return
	 */
	public List<MpmPrescricaoCuidado> listPrescricaoCuidadoPorId(Integer atdSeq,Long seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.SEQ.toString(), seq));

		return executeCriteria(criteria);
	}
	
	
	public MpmPrescricaoCuidado obterPrescricaoCuidadoPorId(Integer pmeAtdSeq,
			Long seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoCuidado.Fields.SEQ.toString(), seq));

		MpmPrescricaoCuidado prescricaoCuidado = (MpmPrescricaoCuidado) executeCriteriaUniqueResult(criteria);
		return prescricaoCuidado;
	}	

	/**
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 * @param servidor
	 */
	public List<MpmPrescricaoCuidado> listarPrescricaoCuidadoPendente(
			MpmPrescricaoMedica prescricao, Date dthrTrabalho) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);

		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(
				MpmPrescricaoCuidado.Fields.PRESCRICAO_MEDICA.toString(),
				prescricao));

		criteria.add(Restrictions.or(
				Restrictions.ge(MpmPrescricaoCuidado.Fields.CRIADO_EM
						.toString(), dthrTrabalho), Restrictions.ge(
						MpmPrescricaoCuidado.Fields.ALTERADO_EM.toString(),
						dthrTrabalho)));

		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.PENDENTE
				.toString(), DominioIndPendenteItemPrescricao.B));

		return executeCriteria(criteria);
	}

	
	/**
	 * Método para buscar prescrições com cuidados
	 * 
	 * @param prescricao
	 * @return
	 */
	public List<MpmPrescricaoCuidado> listarPrescricaoCuidadoPendenteCpa(
			MpmPrescricaoMedica prescricao) {
		
		// Busca ultima prescricao utilizada pelo maior seq
		DetachedCriteria criteriaPrescricao = DetachedCriteria.forClass(MpmPrescricaoMedica.class);
		criteriaPrescricao.add(Restrictions.eq(MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), prescricao.getAtendimento().getSeq()));
		criteriaPrescricao.addOrder(Order.desc(MpmPrescricaoMedica.Fields.SEQ.toString()));
		
		List<MpmPrescricaoMedica> listaPrescricao = this.executeCriteria(criteriaPrescricao);
		MpmPrescricaoMedica ultimaPrescricaoUtilizada = null;
		if (listaPrescricao != null && !listaPrescricao.isEmpty()) {
			ultimaPrescricaoUtilizada = listaPrescricao.get(0);
		}
		
		
		// Busca Cuidados da Prescricao
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		
		criteria.createAlias(MpmPrescricaoCuidado.Fields.CDU.toString(), "cuidadosUsuais");
		
		ArrayList<DominioIndPendenteItemPrescricao> listaPendenteItemPrescricao = new ArrayList<DominioIndPendenteItemPrescricao>();
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.N);
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.A);
		//listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.E);
		
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.P);
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.Y);
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.B);
		listaPendenteItemPrescricao.add(DominioIndPendenteItemPrescricao.R);
		
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.PME_SEQ
				.toString(), ultimaPrescricaoUtilizada.getId().getSeq()));
		
		criteria.add(Restrictions.eq("cuidadosUsuais." + MpmCuidadoUsual.Fields.IND_CPA.toString(), true));
		
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString(), ultimaPrescricaoUtilizada.getDthrFim()));
		
		// Restricao: pendente in (N, A, E)
		criteria.add(Restrictions.in(MpmPrescricaoCuidado.Fields.PENDENTE
				.toString(), listaPendenteItemPrescricao));

		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa os cuidados para processar o cancelamento dos mesmos
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmPrescricaoCuidado> pesquisarPrescricaoCuidadosParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.PME_SEQ
				.toString(), pmeSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);

		criteria.add(Restrictions.in(MpmPrescricaoCuidado.Fields.PENDENTE
				.toString(), restricaoIn));

		Criterion criterionCriadoEmMaiorIgual = Restrictions
				.ge(MpmPrescricaoCuidado.Fields.CRIADO_EM.toString(),
						dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoCuidado.Fields.ALTERADO_EM.toString(),
				dthrMovimento);

		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual,
				criterionAlteradoEmMaiorIgual));

		List<MpmPrescricaoCuidado> retorno = executeCriteria(criteria);

		return retorno;
	}
	

	/**
	 * Método que pesquisa todos os cuidados de uma prescrição médica
	 * 
	 * @param id
	 * @return
	 */
	public List<MpmPrescricaoCuidado> pesquisarTodosCuidadosPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions
				.eq(
						MpmPrescricaoCuidado.Fields.PRESCRICAO_MEDICA_ID
								.toString(), id));

		List<MpmPrescricaoCuidado> list = super.executeCriteria(criteria);

		return list;
	}

	public void desatacharCuidado(MpmPrescricaoCuidado prescricaoCuidado) {
		this.desatachar(prescricaoCuidado);
	}
	
	
	public List<MpmPrescricaoCuidado> buscarPrescricaoCuidadosSemItemRecomendaAltaPeloAtendimento(Integer seqAtendimento) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmPrescricaoCuidado.class);
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ
				.toString(), seqAtendimento));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.IND_ITEM_RECOMENDADO_ALTA.toString(), true));

		return executeCriteria(criteria);
	}	

	
	/**
	 *  Método que pesquisa os cuidados de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoCuidado> obterPrescricoesCuidadoParaSumarioCuidado(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		List<MpmPrescricaoCuidado> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoCuidado.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoCuidado.Fields.PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoCuidado.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoCuidado.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoCuidado.Fields.MPM_PRESCRICAO_CUIDADOS.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;
	}

	public List<MpmPrescricaoCuidado> obterPrescricoesCuidadoPai(Long seq,
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoCuidado.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.MPM_PRESCRICAO_CUIDADOS_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoCuidado.Fields.MPM_PRESCRICAO_CUIDADOS_ATD_SEQ.toString(), seqAtendimento));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoCuidado.Fields.PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoCuidado.Fields.DTHR_INICIO.toString(), dataHoraFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString(), dataHoraInicio)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoCuidado.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoCuidado.Fields.MPM_PRESCRICAO_CUIDADOS.toString()));
		
		return executeCriteria(criteria);
	}
	
}
