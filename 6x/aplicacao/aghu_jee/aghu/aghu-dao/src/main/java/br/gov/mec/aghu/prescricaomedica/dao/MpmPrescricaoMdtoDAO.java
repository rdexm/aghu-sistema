package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VAfaMdtoDescricao;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicamentoVO;
import br.gov.mec.aghu.view.VMpmDosagem;

public class MpmPrescricaoMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoMdto> {
	
	private static final long serialVersionUID = 1577399438370484873L;
	public static final String ALIAS = "pmd";//alias padrão para mpmPrescricaoMdto
	
	public MpmPrescricaoMdto obterMpmPrescricaoMdtoPorPK(MpmPrescricaoMdtoId id){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ID.toString(), id));

		criteria.createAlias(MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA.toString(), "PRESCRICAOMEDICA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString(), "VIA_ADM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmPrescricaoMdto.Fields.DILUENTE.toString(), "DILUENTE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmPrescricaoMdto.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "TIPO_FREQ_APRAZAMENTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmPrescricaoMdto.Fields.TIPO_VELOC_ADMINISTRACAO.toString(), "TIPO_VELOC_ADMINISTRACAO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), "ITENS_PRESCRICAO_MDTOS", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode(MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AfaMedicamento.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoMaterial.Fields.MOVIMENTOS_MATERIAL.toString(), FetchMode.JOIN);
		
		return (MpmPrescricaoMdto) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna lista de Medicamentos para serem apresentadas na lista do menu de
	 * prescrição. <br>
	 * 
	 * @param prescricao
	 * @param dtHrFimPrescricaoMedica
	 * @return Lista de <code>MpmPrescricaoMdto</code>
	 */
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica) {

		return obterListaMedicamentosPrescritosPelaChavePrescricao(prescricao,
				dtHrFimPrescricaoMedica, null, false);
	}

	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao, boolean listarTodas) {
		if (prescricao == null || dtHrFimPrescricaoMedica == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		List<MpmPrescricaoMdto> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA_ID.toString(),
				prescricao));

		if(!listarTodas) {
			criteria.add(Restrictions.or(Restrictions
					.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
					Restrictions.eq(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),
							dtHrFimPrescricaoMedica)));
		}
		
		if (isSolucao != null) {
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO
					.toString(), isSolucao));
		}

		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.INDSOLUCAO
				.toString()));
		list = super.executeCriteria(criteria);

		return list;
	}

	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		if (prescricao == null || dtHrFimPrescricaoMedica == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		List<MpmPrescricaoMdto> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		
		criteria.setFetchMode(MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA+"."+MpmPrescricaoMedica.Fields.ATENDIMENTO, FetchMode.JOIN);
		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA_ID.toString(),
				prescricao));

		criteria.add(Restrictions.or(Restrictions
				.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.eq(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),
						dtHrFimPrescricaoMedica)));

		if (isSolucao != null) {
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO
					.toString(), isSolucao));
		}
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE
				.toString(), DominioIndPendenteItemPrescricao.N));

		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.INDSOLUCAO
				.toString()));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		list = super.executeCriteria(criteria);

		return list;
	}
	
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritos(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		if (prescricao == null || dtHrFimPrescricaoMedica == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		List<MpmPrescricaoMdto> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.createAlias(
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString(),
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString(),
				Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA_ID.toString(),
				prescricao));

	/*	criteria.add(Restrictions.or(Restrictions
				.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.eq(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),
						dtHrFimPrescricaoMedica)));*/

		if (isSolucao != null) {
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO
					.toString(), isSolucao));
		}
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE
				.toString(), DominioIndPendenteItemPrescricao.N));

		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.INDSOLUCAO
				.toString()));
		list = super.executeCriteria(criteria);

		return list;
	}

	@Override
	protected void obterValorSequencialId(MpmPrescricaoMdto elemento) {
		if (elemento.getId() == null) {
			elemento.setId(new MpmPrescricaoMdtoId());
		}

		elemento.getId().setSeq(
				this.getNextVal(SequenceID.MPM_PMD_SQ1)
						.longValue());
	}

	/**
	 * 
	 * @param medMatCodigo
	 * @return
	 */
	public List<VMpmDosagem> buscarDosagensMedicamento(Integer medMatCodigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(VMpmDosagem.class);

		cri.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(),
				medMatCodigo));
		cri.add(Restrictions.eq(VMpmDosagem.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		cri.add(Restrictions.isNotNull((VMpmDosagem.Fields.SEQ_UNIDADE
				.toString())));

		cri.addOrder(Order.asc(VMpmDosagem.Fields.FORMA_DOSAGEM.toString()));

		List<VMpmDosagem> lista = executeCriteria(cri);

		return lista;
	}

	/**
	 * 
	 * @param seqAtendimento
	 * @param seqPrescricaoMedicamento
	 * @return
	 */
	public List<MpmPrescricaoMdto> listarPrescricoesMedicamentosDataHoraInicioTratamentoNulo(
			Integer seqAtendimento, Long seqPrescricaoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), seqAtendimento));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.SEQ.toString(),
				seqPrescricaoMedicamento));
		criteria.add(Restrictions
				.isNull(MpmPrescricaoMdto.Fields.DTHR_INICIO_TRATAMENTO
						.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa os medicamentos de uma prescrição considerando datas de início e
	 * fim
	 * 
	 * @param pmeAtdSeq
	 * @param dtFimLida
	 * @return
	 */
	public List<MpmPrescricaoMdto> pesquisarMedicamentosUltimaPrescricao(
			Date dtFimLida, MpmPrescricaoMedica prescricaoMedicaAnterior) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), prescricaoMedicaAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PME_SEQ.toString().toString(), prescricaoMedicaAnterior.getId().getSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), prescricaoMedicaAnterior.getDthrFim()));	
		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém o medicamento avaliado da prescrição quando houver
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoMdto obterMedicamentoAvaliacao(Integer atdSeq, Long seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.SEQ.toString(),
				seq));

		MpmPrescricaoMdto medicamento = (MpmPrescricaoMdto) this
				.executeCriteriaUniqueResult(criteria);
		MpmPrescricaoMdto retorno = medicamento;
		List<MpmItemPrescricaoMdto> itensMedicamentos = medicamento
				.getItensPrescricaoMdtos();
		for (MpmItemPrescricaoMdto itemMedicamento : itensMedicamentos) {
			if (itemMedicamento.getMedicamento() != null
					&& itemMedicamento.getMedicamento().getAfaTipoUsoMdtos() != null) {
				if (!itemMedicamento.getMedicamento().getAfaTipoUsoMdtos()
						.getIndAvaliacao()) {
					retorno = null;
					break;
				}
			} else {
				retorno = null;
				break;
			}
		}
		return retorno;
	}

	/**
	 * Obtém um medicamento pelo seu ID
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoMdto obterMedicamentoPeloId(Integer atdSeq, Long seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.SEQ.toString(),
				seq));
		MpmPrescricaoMdto retorno = (MpmPrescricaoMdto) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;
	}

	/**
	 * Método que verifica se medicamento já foi prescrito
	 * 
	 * @param pmdAtdSeq
	 * @param pmdSeq
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Boolean verificarMedicamentoJaPrescrito(Integer pmdAtdSeq,
			Long pmdSeq, Date dataInicio, Date dataFim) {
		Boolean retorno = false;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString(),
				pmdAtdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC
				.toString(), pmdSeq));
		criteria.add(Restrictions.ge(MpmPrescricaoMdto.Fields.DTHR_INICIO
				.toString(), dataInicio));
		criteria.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO
				.toString(), dataFim));

		List<MpmPrescricaoMdto> listaMedicamentos = executeCriteria(criteria);

		if (!listaMedicamentos.isEmpty()) {
			retorno = true;
		}

		return retorno;
	}

	/**
	 * ORADB: Procedure MPMP_PENDENTE_MDTO
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 * @param servidor
	 */
	public List<MpmPrescricaoMdto> listarPrescricaoMedicamentoPendente(
			MpmPrescricaoMedica prescricao, Date dthrTrabalho) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA
				.toString(), prescricao));

		criteria.add(Restrictions.or(Restrictions.ge(
				MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), dthrTrabalho),
				Restrictions.ge(
						MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(),
						dthrTrabalho)));

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PENDENTE.toString(), DominioIndPendenteItemPrescricao.B));

		return executeCriteria(criteria);
	}

	public PrescricaoMedicamentoVO obtemPrescricaoMedicmanetoVO(Integer atdSeq, Long seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class);

		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(MpmPrescricaoMdto.Fields.ATD_SEQ.toString()),PrescricaoMedicamentoVO.Fields.ATD_SEQ.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.SEQ.toString()),PrescricaoMedicamentoVO.Fields.SEQ.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.PRESCRICAOMDTOORIGEM_ATDSEQ.toString()),PrescricaoMedicamentoVO.Fields.PRESCRICAOMDTOORIGEM_ATDSEQ.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.VIAADMINISTRACAO_SIGLA.toString()),PrescricaoMedicamentoVO.Fields.VAD.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.INDSENECESSARIO.toString()),PrescricaoMedicamentoVO.Fields.INDSENECESSARIO.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.HORAINICIOADMINISTRACAO.toString()),PrescricaoMedicamentoVO.Fields.HORAINICIOADMINISTRACAO.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.OBSERVACAO.toString()),PrescricaoMedicamentoVO.Fields.OBSERVACAO.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.GOTEJO.toString()),PrescricaoMedicamentoVO.Fields.GOTEJO.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.QTDEHORASCORRER.toString()),PrescricaoMedicamentoVO.Fields.QTDEHORASCORRER.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.INDPENDENTE.toString()),PrescricaoMedicamentoVO.Fields.IND_PENDENTE.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.FREQUENCIA.toString()),PrescricaoMedicamentoVO.Fields.FREQUENCIA.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ_SEQ.toString()),PrescricaoMedicamentoVO.Fields.TIPO_FREQ_APRZ_SEQ.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString()),PrescricaoMedicamentoVO.Fields.DTHR_INICIO.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),PrescricaoMedicamentoVO.Fields.DTHR_FIM.toString())
						.add(Projections.property(MpmPrescricaoMdto.Fields.DURACAO_TRAT_SOL.toString()),PrescricaoMedicamentoVO.Fields.DURACAO_TRAT_SOL.toString())
		);

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.SEQ.toString(), seq));

		criteria.setResultTransformer(Transformers.aliasToBean(PrescricaoMedicamentoVO.class));
		
		return (PrescricaoMedicamentoVO)executeCriteriaUniqueResult(criteria);
	}
	
	public List<MpmItemPrescricaoMdto> listarItensPrescricaoMedicamentoFarmaciaMe(Integer atendimentoSeq, Date dthrInicio, Date dthrFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, "IME");
		criteria.createAlias("IME." + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), "PMD", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PMD." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atendimentoSeq));
		criteria.add(Restrictions.lt("PMD." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrFim));
		criteria.add(Restrictions.or(Restrictions.isNull("PMD." + MpmPrescricaoMdto.Fields.DTHR_FIM), 
				Restrictions.gt("PMD." + MpmPrescricaoMdto.Fields.DTHR_FIM, dthrInicio)));
		return executeCriteria(criteria);
	}
	
	public LocalDispensaVO obterLocalDispensaVO(Integer medMatCodigo, Integer atendimentoSeq){
		StringBuffer hql = new StringBuffer(360);
		hql.append(" select ");
		hql.append(" localDisp.unidadeFuncionalDispUsoDomiciliar.seq, ");
		hql.append(" localDisp.unidadeFuncionalDispDoseInt.seq, ");
		hql.append(" localDisp.unidadeFuncionalDispDoseFrac.seq, ");
		hql.append(" localDisp.unidadeFuncionalDispAlternativa.seq ");
		hql.append(" from ");
		hql.append(AfaLocalDispensacaoMdtos.class.getName()).append(" localDisp, ");
		hql.append(AghAtendimentos.class.getName()).append(" atd ");
		hql.append(" where ");
		hql.append(" localDisp.id.unfSeq  = atd.unidadeFuncional.seq ");
		hql.append(" and atd.seq  = :atendimentoSeq ");
		hql.append(" and localDisp.id.medMatCodigo  = :medMatCodigo ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("atendimentoSeq", atendimentoSeq);
		query.setParameter("medMatCodigo", medMatCodigo);
		
		Object [] result = (Object[]) query.uniqueResult();
		if(result != null){
			return new LocalDispensaVO((Short)result[0], (Short)result[1], (Short)result[2], (Short)result[3]);
		} else {
			return null;
		}
	}	
	
			
/**
	 * Pesquisa os medicamentos para processar o cancelamento dos mesmos.
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmPrescricaoMdto> pesquisarPrescricaoMedicamentosParaCancelar(Integer atdSeq, Integer pmeSeq, Date dthrMovimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "IME");
		criteria.createAlias("IME." + MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), "IPRM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PME_SEQ.toString(), pmeSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);
		
		criteria.add(Restrictions.in(MpmPrescricaoMdto.Fields.PENDENTE.toString(), restricaoIn));
		
		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), dthrMovimento);
		
		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual, criterionAlteradoEmMaiorIgual));
		
		List<MpmPrescricaoMdto> retorno = executeCriteria(criteria);
		
		return retorno;
	}
	
	/**
	 * Método que pesquisa todos os medicamentos de uma prescrição médica
	 * @param id
	 * @return
	 */
	public List<MpmPrescricaoMdto> pesquisarTodosMedicamentosPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA_ID.toString(),
				id));

		List<MpmPrescricaoMdto> list = super.executeCriteria(criteria);

		return list;
	}

	
	/**
	 *  Método que pesquisa os medicamentos de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoMdto> obterPrescricoesMdtoParaSumarioMdto(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
	 return this.obterPrescricoesDeMdtoOuSolucaoParaSumarioMdto(pmeAtdSeq, dataInicioPrescricao, dataFimPrescricao, false);
	}

	/**
	 *  Método que pesquisa os medicamentos de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoMdto> obterPrescricoesSolucaoParaSumarioMdto(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		return this.obterPrescricoesDeMdtoOuSolucaoParaSumarioMdto(pmeAtdSeq, dataInicioPrescricao, dataFimPrescricao, true);
	}
	
	private List<MpmPrescricaoMdto> obterPrescricoesDeMdtoOuSolucaoParaSumarioMdto(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao, Boolean isSolucao){
		List<MpmPrescricaoMdto> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoMdto.Fields.PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoMdto.Fields.SERVIDOR_VALIDACAO.toString()));

		if(isSolucao){
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), true));
		}else{
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), false));
		}
	
		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM.toString()));		
		
		lista = executeCriteria(criteria);
		
		return lista;

	}
	
	public String obterObservacaoDoBanco(MpmPrescricaoMdtoId id){
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ID.toString(),id));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MpmPrescricaoMdto.Fields.OBSERVACAO.toString())));
		String observacao = (String) this.executeCriteriaUniqueResult(criteria);
		
		return observacao;
	}

	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(Integer seqAtendimento) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmPrescricaoMdto.class);
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), seqAtendimento));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.IND_ITEM_RECOMENDADO_ALTA.toString(), true));

		return executeCriteria(criteria);
	}	
	
		/**
	 * Consulta codigo de material vinculado a prescricao medica
	 * confirmada.
	 * 
	 * @param {Integer} atdSeq Seq do atendimento.
	 * @return List<Object[]>.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarPrescricaoMdtoMaterialPorAtendimento(
			Integer atdSeq) {

		StringBuilder hql = new StringBuilder(200);
		hql.append(" select distinct ime."
				+ MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString() + ", ");
		hql.append(" ime."
				+ MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString() + ", ");
		hql.append(" b."
				+ VAfaMdtoDescricao.Fields.MED_DESCRICAO_CODIGO.toString() + ", ");
		hql.append(" b."
				+ VAfaMdtoDescricao.Fields.MEDICAMENTO.toString() + " ");
		hql.append(" from ");
		hql.append(" MpmPrescricaoMdto pmd, VAfaMdtoDescricao b ");
		hql.append(" inner join pmd."
				+ MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString()
				+ " ime ");
		hql.append(" where ");
		hql.append(" ime."
				+ MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()
				+ " = b."
				+ VAfaMdtoDescricao.Fields.MED_MAT_CODIGO.toString() + " AND ");
		hql.append(" pmd."
				+ MpmPrescricaoMdto.Fields.ATD_SEQ.toString()
				+ " = :pAtdSeq AND ");
		hql.append(" pmd."
				+ MpmPrescricaoMdto.Fields.INDPENDENTE.toString()
				+ " = :pIndPendente ");

		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		return query.getResultList();

	}

	public List<MpmPrescricaoMdto> obterPrescricoesMdtoPai(Long seq,
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim,
			boolean isSolucao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
        
        criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PRESCRICAOMDTOORIGEM_SEQ
				.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PRESCRICAOMDTOORIGEM_ATDSEQ
				.toString(), seqAtendimento));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoMdto.Fields.PENDENTE
				.toString(), restricaoIn));

		criteria.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO
				.toString(), dataHoraFim));

		criteria.add(Restrictions.or(Restrictions
				.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),
						dataHoraInicio)));

		criteria.add(Restrictions
				.isNotNull(MpmPrescricaoMdto.Fields.SERVIDOR_VALIDACAO
						.toString()));

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDSOLUCAO
				.toString(), isSolucao));

		criteria
				.addOrder(Order
						.asc(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM
								.toString()));

		return executeCriteria(criteria);
	}
	
	
	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoNovo(Integer pmdAtdSeq, Date pmdCriadoEm, Date pmdDthrInicio, Date pmdDthrFim){
		
		DetachedCriteria cri = getCriteriaPesquisarPrescricaoMdtoNovo(pmdAtdSeq, pmdCriadoEm, pmdDthrInicio, pmdDthrFim); 
		
		return executeCriteria(cri);
	}
	
	private DetachedCriteria getCriteriaPesquisarPrescricaoMdtoNovo(Integer pmdAtdSeq, Date pmdCriadoEm, Date pmdDthrInicio, Date pmdDthrFim){
		
		DetachedCriteria cri = DetachedCriteria.forClass(MpmPrescricaoMdto.class, ALIAS);
		
		cri.createAlias(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM.toString(), "origem",Criteria.LEFT_JOIN);
		
		cri.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), pmdAtdSeq));
		cri.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		
		cri.add(Restrictions.or(
				Restrictions.ge(MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), pmdCriadoEm),
				Restrictions.ge(MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), pmdCriadoEm)
		));
		
		cri.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), pmdDthrFim));
		
		
		cri.add(Restrictions.disjunction()
				.add(Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()))
				.add(Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), pmdDthrInicio))
				.add(
					Restrictions.and(
							Restrictions.eqProperty(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
							Restrictions.ge(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), pmdDthrInicio))
				));
		cri.add(Restrictions.and(
				Subqueries.propertyNotIn(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), 	getSubCriteriaPesquisarPrescricaoMdtoNovo(MpmPrescricaoMdto.Fields.ATD_SEQ, pmdAtdSeq, pmdCriadoEm, pmdDthrFim, pmdDthrInicio)), 
				Subqueries.propertyNotIn(MpmPrescricaoMdto.Fields.SEQ.toString(),		getSubCriteriaPesquisarPrescricaoMdtoNovo(MpmPrescricaoMdto.Fields.SEQ, 	pmdAtdSeq, pmdCriadoEm, pmdDthrFim, pmdDthrInicio))
				));
		
		return cri;
	}
	private DetachedCriteria getSubCriteriaPesquisarPrescricaoMdtoNovo(MpmPrescricaoMdto.Fields columnPesquisa, Integer pmdAtdSeq, Date pmdCriadoEm, Date pmdDthrFim, Date pmdDthrInicio) {
		
		String mpmPrescMdto = "mpmPrescMdto";
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class, mpmPrescMdto);
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM.toString()+"."+ columnPesquisa.toString()));
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM.toString()+"."+ MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), pmdAtdSeq));
		
		String seqMain = ALIAS + "." + MpmPrescricaoMdto.Fields.SEQ.toString();
		criteria.add(Restrictions.eqProperty(MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM.toString()+"."+  MpmPrescricaoMdto.Fields.SEQ.toString(), seqMain));
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		
		criteria.add(Restrictions.or(
				Restrictions.ge(MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), pmdCriadoEm),
				Restrictions.ge(MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), pmdCriadoEm)
			));
		
		criteria.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), pmdDthrFim));
		
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()))
				.add(Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), pmdDthrInicio))
				.add(
					Restrictions.and(
							Restrictions.eqProperty(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
							Restrictions.ge(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), pmdDthrInicio))
				));
		
		return criteria;
	}

	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoSitConfirmado(Integer pmeAtdSeq, Integer pmeSeq) {
		DetachedCriteria cri = DetachedCriteria.forClass(MpmPrescricaoMdto.class);
		
		cri.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		cri.add(Restrictions.eq(MpmPrescricaoMdto.Fields.PME_SEQ.toString(), pmeSeq));
		cri.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		
		cri.add(
				Restrictions.or(
						Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString()), 
						Restrictions.ltProperty(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), MpmPrescricaoMdto.Fields.DTHR_FIM.toString())
						));		
		cri.addOrder(Order.asc(MpmPrescricaoMdto.Fields.CRIADO_EM.toString()));
		return executeCriteria(cri);
	}
	
	public List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(Integer atdSeq) {
		return pesquisaMedicamentosPOL(atdSeq, null);
	}
	
	public List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(Integer atdSeq, DominioIndPendenteItemPrescricao dominio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class);
		criteria.createAlias(MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), "ITENS_PRESCRICAO_MDTOS", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode(MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AfaMedicamento.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoMaterial.Fields.MOVIMENTOS_MATERIAL.toString(), FetchMode.JOIN);

		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(),atdSeq));

		if(dominio != null){
			criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(),dominio));
			
		} else {
			criteria.add(Restrictions.in(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(),DominioIndPendenteItemPrescricao.values()));
		}
		
		criteria.addOrder(Order.asc(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MpmPrescricaoMdto> pesquisarMedicamentosPrescricaoCalculoQuantidade(
			MpmPrescricaoMedica prescricao, Date dataTrabalho) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoMdto.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(
				MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA.toString(),
				prescricao));		
		
		criteria.add(Restrictions.or(Restrictions.ge(
				MpmPrescricaoDieta.Fields.CRIADO_EM.toString(), dataTrabalho),
				Restrictions.ge(MpmPrescricaoDieta.Fields.ALTERADO_EM
						.toString(), dataTrabalho)));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);

		criteria.add(Restrictions.in(MpmPrescricaoMdto.Fields.PENDENTE
				.toString(), restricaoIn));
		
		
		return this.executeCriteria(criteria);

	}
	/**
	 * Obtem os medicamentos de uma prescrição médica.
	 * Consulta da function F9 da estória 45269.
	 * @return lista
	 */
	public List<MpmPrescricaoMdto> obterMpmPrescricaoMedicamentos(Integer curJumSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "PMD");
		criteria.createAlias("PMD." + MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), "IME", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("IME." + MpmItemPrescricaoMdto.Fields.JUS_SEQ.toString(), curJumSeq)); 
		criteria.add(Restrictions.eq("IME." + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim()));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.X);
		
		criteria.add(Restrictions.in("PMD." + MpmPrescricaoMdto.Fields.PENDENTE
				.toString(), restricaoIn));
		
		return executeCriteria(criteria);
	}
}
