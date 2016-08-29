package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.faturamento.vo.ItemAlteracaoNptVO;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoProcedimentoVo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author rcorvalao
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class MpmPrescricaoProcedimentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoProcedimento> {

	private static final long serialVersionUID = -6000856739076179277L;

	/**
	 * 
	 * Metodo chamado no insert da Entidade.
	 */
	@Override
	public void obterValorSequencialId(MpmPrescricaoProcedimento elemento) {
		if (elemento == null || elemento.getPrescricaoMedica() == null
				|| !elemento.getPrescricaoMedica().isPkValida()) {
			throw new IllegalArgumentException(
					"PrescricaoMedica nao foi informada corretamente!!!");
		}
		Integer pmeAtdSeq = elemento.getPrescricaoMedica().getId().getAtdSeq();
		Long seq = Long.valueOf(this.obterValorSequencialId());
		MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId(
				pmeAtdSeq, seq);
		elemento.setId(id);
	}

	private Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.MPM_PPR_SQ1);
	}

	/**
	 * Retorna lista de procedimentos para serem apresentadas na lista do menu
	 * de prescrição. <br>
	 * Execução da criteria retorna lista de {@link MpmPrescricaoProcedimento}.
	 * 
	 * @param atendimento
	 *            configurado para
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MpmPrescricaoProcedimento> buscaPrescricaoProcedimentos(
			MpmPrescricaoMedicaId id, Date dthrFimPrescricao, Boolean listarTodas) {
		if (id == null || dthrFimPrescricao == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		/*
		 * select ppr.seq, ppr.quantidade, ppr.inf_complementares, ped.descricao
		 * desc_ped, pci.descricao desc_pci, mat.nome nome_mat, mat.umd_codigo
		 * from sco_materiais mat, mbc_procedimento_cirurgicos pci,
		 * mpm_proced_especial_diversos ped, mpm_prescricao_procedimentos ppr
		 * where ppr.atd_seq = p_atd_seq and ppr.dthr_fim == p_dthr_fim and
		 * ped.seq(+) = ppr.ped_seq and pci.seq(+) = ppr.pci_seq and
		 * mat.codigo(+) = ppr.mat_codigo;
		 */
		StringBuilder sql = new StringBuilder(100);
		sql.append("select proc ");
		sql.append("from ").append(MpmPrescricaoProcedimento.class.getName())
				.append(" proc ");
		sql.append("where proc.")
				.append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_MEDICA
						.toString()).append(" = :idPme ");
		if(!listarTodas) {
		sql.append("and proc.")
				.append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString())
				.append(" = :dthrFim ");
		}

		Query query = createQuery(sql.toString());
		query.setParameter("idPme", id);
		if(!listarTodas) {
			query.setParameter("dthrFim", dthrFimPrescricao);
		}

		List<MpmPrescricaoProcedimento> list = query.getResultList();

		return list;
	}

	/**
	 * Pesquisa os procedimentos de uma prescrição considerando datas de início
	 * e fim
	 * 
	 * @param pmeAtdSeq
	 * @param dtFimLida
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> pesquisarProcedimentosUltimaPrescricao(
			Date dtFimLida, MpmPrescricaoMedica prescricaoMedicaAnterior) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(),
				prescricaoMedicaAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PME_SEQ
				.toString().toString(), prescricaoMedicaAnterior.getId()
				.getSeq()));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(),
				prescricaoMedicaAnterior.getDthrFim()));
		criteria.addOrder(Order.asc(MpmPrescricaoProcedimento.Fields.SEQ
				.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém um procedimento pelo seu ID
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoProcedimento obterProcedimentoPeloId(Integer atdSeq,
			Long seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.SEQ.toString(), seq));
		MpmPrescricaoProcedimento retorno = (MpmPrescricaoProcedimento) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;
	}

	/**
	 * Método que retorna os proceidmentos de uma prescrição que tornarse-a
	 * pendente.
	 * 
	 * @param prescricao
	 * @param data
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> listarProcedimentosPrescricaoPendente(
			MpmPrescricaoMedica prescricao, Date data) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);

		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.PRESCRICAOMEDICA.toString(),
				prescricao));

		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.B));

		criteria.add(Restrictions.or(
				Restrictions.ge(
						MpmPrescricaoProcedimento.Fields.CRIADO_EM.toString(),
						data),
				Restrictions.ge(
						MpmPrescricaoProcedimento.Fields.ALTERADO_EM.toString(),
						data)));

		return super.executeCriteria(criteria);

	}

	public MpmPrescricaoProcedimento inserir(MpmPrescricaoProcedimento elemento) {
		if (elemento == null || elemento.getPrescricaoMedica() == null
				|| !elemento.getPrescricaoMedica().isPkValida()) {
			throw new IllegalArgumentException(
					"PrescricaoMedica nao foi informada corretamente!!!");
		}
		super.persistir(elemento);
		flush();
		return elemento;
	}

	/**
	 * 
	 * @param altanAtdSeq
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> obterProcedimentosPendentes(
			Integer altanAtdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		return executeCriteria(criteria);

	}

	/**
	 * Busca a menor data de inicio de prescricao de um material
	 * 
	 * @param altanAtdSeq
	 * @param matCodigo
	 * @return
	 */
	public Date obterDataInicioMinimaPorMaterial(Integer altanAtdSeq,
			Integer matCodigo) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.MAT_CODIGO.toString(),
				matCodigo));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.addOrder(Order
				.asc(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()));

		List<MpmPrescricaoProcedimento> procedimentos = executeCriteria(criteria);

		if (procedimentos != null && procedimentos.size() > 0) {
			return procedimentos.get(0).getDthrInicio();
		}

		return null;
	}

	/**
	 * Busca a menor data de inicio de prescricao por procedimento cirurgico
	 * 
	 * @param altanAtdSeq
	 * @param pciSeq
	 * @return
	 */
	public Date obterDataInicioMinimaPorProcedCirurgico(Integer altanAtdSeq,
			Integer pciSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.PCI_SEQ_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.addOrder(Order
				.asc(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()));

		List<MpmPrescricaoProcedimento> procedimentos = executeCriteria(criteria);

		if (procedimentos != null && procedimentos.size() > 0) {
			return procedimentos.get(0).getDthrInicio();
		}

		return null;
	}

	/**
	 * Busca a menor data de inicio de prescricao por procedimento diverso
	 * 
	 * @param altanAtdSeq
	 * @param pedSeq
	 * @return
	 */
	public Date obterDataInicioMinimaPorProcedDiverso(Integer altanAtdSeq,
			Short pedSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.PED_SEQ_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.addOrder(Order
				.asc(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()));

		List<MpmPrescricaoProcedimento> procedimentos = executeCriteria(criteria);

		if (procedimentos != null && procedimentos.size() > 0) {
			return procedimentos.get(0).getDthrInicio();
		}

		return null;
	}

	/**
	 * 
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> listarProcedimentosGeracaoLaudos(
			MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaParcialProcedimentosGeracaoLaudos(prescricao);

		criteria.add(Restrictions
				.isNotNull(MpmPrescricaoProcedimento.Fields.MAT_CODIGO
						.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> listarProcedimentosDiversosGeracaoLaudos(
			MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaParcialProcedimentosGeracaoLaudos(prescricao);

		criteria.add(Restrictions
				.isNotNull(MpmPrescricaoProcedimento.Fields.PED_SEQ.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> listarProcedimentosCirurgicosGeracaoLaudos(
			MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaParcialProcedimentosGeracaoLaudos(prescricao);

		criteria.add(Restrictions
				.isNotNull(MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString()));

		return this.executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaParcialProcedimentosGeracaoLaudos(
			MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(
				MpmPrescricaoProcedimento.class, "procedimento");
		
		//TODO: Conferir com o Vacaro se o filtro abaixo não altera a lógica de negócio
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PRESCRICAO_MEDICA
				.toString(), prescricao.getId()));

		criteria.add(Restrictions.or(Restrictions
				.isNull(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()),
				Restrictions.gt(
						MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(),
						prescricao.getDthrInicio())));

//		criteria.add(Restrictions.eq(
//				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
//				DominioIndPendenteItemPrescricao.N));

		
		//não filtrar pelor confirmados, mais sim pelos que VÃO ESTAR confirmados.
		criteria.add(Restrictions.in(MpmPrescricaoProcedimento.Fields.IND_PENDENTE
				.toString(), new DominioIndPendenteItemPrescricao[] {
				DominioIndPendenteItemPrescricao.P,
				DominioIndPendenteItemPrescricao.Y,
				DominioIndPendenteItemPrescricao.B,
				DominioIndPendenteItemPrescricao.E,
				DominioIndPendenteItemPrescricao.R }));

		
		Date dataTrabalho = null;

		if (prescricao.getDthrInicioMvtoPendente() != null) {
			dataTrabalho = prescricao.getDthrInicioMvtoPendente();
		} else {
			dataTrabalho = prescricao.getDthrMovimento();
		}		
		
		criteria.add(Restrictions.or(Restrictions.ge(
				MpmPrescricaoProcedimento.Fields.CRIADO_EM.toString(), dataTrabalho),
				Restrictions.ge(MpmPrescricaoProcedimento.Fields.ALTERADO_EM
						.toString(), dataTrabalho)));
		
		
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(
				MpmLaudo.class, "laudo");
		subCriteria.add(Restrictions.eqProperty("laudo."
				+ MpmLaudo.Fields.PRESCRICAO_PROCEDIMENTO_ID.toString(),
				"procedimento.id.seq"));
		subCriteria.add(Restrictions.eqProperty("laudo."
				+ MpmLaudo.Fields.PRESCRICAO_PROCEDIMENTO_ATD_ID.toString(),
				"procedimento.id.atdSeq"));
		subCriteria.add(Restrictions.ge("laudo."
				+ MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString(),
				prescricao.getDthrInicio()));
		subCriteria.setProjection(Property.forName("laudo.id").count());

		criteria.add(Subqueries.eq(0L, subCriteria));
		return criteria;
	}

	/**
	 * Pesquisa procedimentos para processar o cancelamento dos mesmos.
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> pesquisarPrescricaoProcedimentosParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.PME_SEQ.toString(), pmeSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);

		criteria.add(Restrictions.in(
				MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(),
				restricaoIn));

		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoProcedimento.Fields.CRIADO_EM.toString(),
				dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoProcedimento.Fields.ALTERADO_EM.toString(),
				dthrMovimento);

		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual,
				criterionAlteradoEmMaiorIgual));

		List<MpmPrescricaoProcedimento> retorno = executeCriteria(criteria);

		return retorno;
	}

	/**
	 * Método que pesquisa todos os procedimentos de uma prescrição médica
	 * 
	 * @param id
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> pesquisarTodosProcedimentosPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoProcedimento.Fields.PRESCRICAO_MEDICA.toString(),
				id));

		List<MpmPrescricaoProcedimento> list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Consulta pesquisa procedimentos por codigo do atendimento e se estiver
	 * confirmado.
	 * 
	 * Implementa parte do cursor cur_ppr;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA
	 * PLL: MPMF_SUMARIO_ALTA.pll  
	 * Function: MPMP_LISTA_OUT_PROCEDIMENTOS; 
	 * @param {Integer} atdSeq Seq do atendimento.
	 * @return Registros encontrados.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarPrescricaoProcedimentoConfirmadoPorAtendimento(
			Integer atdSeq) {

		StringBuilder hql = new StringBuilder(300);
		hql.append(" select distinct ppr."
				).append( MpmPrescricaoProcedimento.Fields.MAT_CODIGO.toString() ).append( ", ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.PCI_SEQ_SEQ.toString()
				).append( ", ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.PED_SEQ_SEQ.toString()
				).append( ", ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.PCI_SEQ_DESCRICAO.toString()
				).append( ", ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.PED_SEQ_DESCRICAO.toString()
				).append( ", ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.MATERIAL_NOME.toString()
				).append( ' ')
		.append(" from ")
		.append(" MpmPrescricaoProcedimento ppr ")
		.append(" left outer join ppr."
				).append( MpmPrescricaoProcedimento.Fields.MATERIAL.toString()
				).append( " mat ")
		.append(" left outer join ppr."
				).append( MpmPrescricaoProcedimento.Fields.PED_SEQ.toString() ).append( " ped ")
		.append(" left outer join ppr."
				).append( MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString() ).append( " pci ")
		.append(" where ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()
				).append( " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		return query.getResultList();

	}

	/**
	 * Consulta menor data inicio de procedimentos
	 * relacionado a procedimentos cirurgicos;
	 * 
	 * Implementa cursor cur_ppr_min_pci;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA
	 * PLL: MPMF_SUMARIO_ALTA.pll  
	 * Function: MPMP_LISTA_OUT_PROCEDIMENTOS;
	 * 
	 * @param {Integer} atdSeq
	 * @param {Integer} pciSeq
	 * @return
	 */
	public Date pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEProcedimentosCirurgicos(
			Integer atdSeq, Integer pciSeq) {

		Date response = null;

		StringBuilder hql = new StringBuilder(150);
		hql.append(" select min("
				+ MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()
				+ ") ");
		hql.append(" from ");
		hql.append(" MpmPrescricaoProcedimento ppr ");
		hql.append(" where ");
		hql.append(" ppr."
				+ MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()
				+ " = :pAtdSeq AND ");
		hql.append(" ppr."
				+ MpmPrescricaoProcedimento.Fields.PCI_SEQ_SEQ.toString()
				+ " = :pPciSeq AND ");
		hql.append(" ppr."
				+ MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()
				+ " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pPciSeq", pciSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		List<Object> lista = query.getResultList();
		if (lista != null && lista.size() > 0) {

			response = (Date) lista.get(0);

		}

		return response;

	}

	/**
	 * Consulta menor data inicio de procedimentos
	 * relacionado a procedimentos especiais diversos;
	 * 
	 * Implementa cursor cur_ppr_min_ped;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA
	 * PLL: MPMF_SUMARIO_ALTA.pll  
	 * Function: MPMP_LISTA_OUT_PROCEDIMENTOS; 
	 * 
	 * @param {Integer} atdSeq
	 * @param {Short} pedSeq
	 * @return
	 */
	public Date pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEProcedimentosDiversos(
			Integer atdSeq, Short pedSeq) {

		Date response = null;

		StringBuilder hql = new StringBuilder(200);
		hql.append(" select min("
				).append( MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()
				).append( ") ")
		.append(" from ")
		.append(" MpmPrescricaoProcedimento ppr ")
		.append(" where ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.PED_SEQ_SEQ.toString()
				).append( " = :pPedSeq AND ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()
				).append( " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pPedSeq", pedSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		List<Object> lista = query.getResultList();
		if (lista != null && lista.size() > 0) {

			response = (Date) lista.get(0);

		}

		return response;

	}

	/**
	 * Consulta menor data inicio de procedimentos
	 * relacionado a scomaterias;
	 * 
	 * Implementa cursor cur_ppr_min_mat;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA
	 * PLL: MPMF_SUMARIO_ALTA.pll  
	 * Function: MPMP_LISTA_OUT_PROCEDIMENTOS;  
	 * 
	 * @param {Integer} atdSeq
	 * @param {Integer} matCodigo
	 * @return
	 */
	public Date pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEMaterial(
			Integer atdSeq, Integer matCodigo) {

		Date response = null;

		StringBuilder hql = new StringBuilder(200);
		hql.append(" select min("
				).append( MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()
				).append( ") ")
		.append(" from ")
		.append(" MpmPrescricaoProcedimento ppr ")
		.append(" where ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.MAT_CODIGO.toString()
				).append( " = :pMatCodigo AND ")
		.append(" ppr."
				).append( MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()
				).append( " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pMatCodigo", matCodigo);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		query.getResultList();

		List<Object> lista = query.getResultList();
		if (lista != null && lista.size() > 0) {

			response = (Date) lista.get(0);

		}

		return response;

	}
	
	/**
	 * Método que pesquisa os procedimentos de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoProcedimento> obterPrescricoesProcedimentoParaSumarioMdto(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		List<MpmPrescricaoProcedimento> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoProcedimento.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;

	}
	
	/**
	 * Busca um VO com as informacoes que estao atualmente salva na base de dados
	 * para o Objeto informado no parametro.<br>
	 * 
	 * <code>MpmPrescricaoProcedimento</code>
	 * 
	 * @param prescProc
	 * @return
	 */
	public PrescricaoProcedimentoVo buscaPrescricaoProcedimentoVo(MpmPrescricaoProcedimento prescProc) {
		if (prescProc == null || prescProc.getId() == null 
				|| prescProc.getId().getAtdSeq() == null 
				|| prescProc.getId().getSeq() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!");
		}
		
		StringBuilder hql = new StringBuilder(170);
		//hql.append("select o.").append(MpmPrescricaoProcedimento.Fields.MATERIAL.toString());
		//hql.append(", o.").append(MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString());
		//hql.append(", o.").append(MpmPrescricaoProcedimento.Fields.PED_SEQ.toString());
		hql.append("select mat ")
		.append(", pci ")
		.append(", ped ")
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString())
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString())
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.JUSTIFICATIVA.toString())
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.QUANTIDADE.toString())
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.INF_COMPLEMENTARES.toString())
		.append(", o.").append(MpmPrescricaoProcedimento.Fields.DURACAO_TRAT_SOLICITADO.toString())
		.append(" from ").append(MpmPrescricaoProcedimento.class.getSimpleName()).append(" o ")
		.append(" left outer join o.").append(MpmPrescricaoProcedimento.Fields.MATERIAL.toString()).append(" mat ")
		.append(" left outer join o.").append(MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString()).append( " pci ")
		.append(" left outer join o.").append(MpmPrescricaoProcedimento.Fields.PED_SEQ.toString()).append(" ped ")
		.append(" where o.").append(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()).append(" = :atdSeq ")
		.append("and o.").append(MpmPrescricaoProcedimento.Fields.SEQ.toString()).append(" = :seq ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("atdSeq", prescProc.getId().getAtdSeq());
		query.setParameter("seq", prescProc.getId().getSeq());
		
		List<Object[]> lista = query.getResultList();
		PrescricaoProcedimentoVo vo = new PrescricaoProcedimentoVo();
		
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			
			for (Object[] listFileds : lista) {
				vo.setMatCodigo((ScoMaterial) listFileds[0]);
				vo.setPciSeq((MbcProcedimentoCirurgicos) listFileds[1]);
				vo.setPedSeq((MpmProcedEspecialDiversos) listFileds[2]);
				vo.setIndPendente((DominioIndPendenteItemPrescricao) listFileds[3]);
				vo.setDthrFim((Date) listFileds[4]);
				vo.setJustificativa((String) listFileds[5]);
				vo.setQuantidade((Short)listFileds[6]);
				vo.setInformacaoComplementar((String)listFileds[7]);
				vo.setDuracaoTratamentoSolicitado((Short)listFileds[8]);
			}
		}
		
		return vo;
	}

	/**
	 * Busca uma PrescricaoProcedimento, fazendo junção com ProcedEspecialDiversos, ProcedimentoCirurgicos e Materiais, filtrando por ID.
	 * @param seq
	 * @param atdSeq
	 * @return
	 */
	public MpmPrescricaoProcedimento obterPrescricaoProcedimentoPorId(Long seq, Integer atdSeq){
		if(seq == null || atdSeq == null){
			throw new IllegalArgumentException("Os parâmetros para o metodo obterPrescricaoProcedimentoPorId não foram informados.");
		}
		MpmPrescricaoProcedimento prescProced = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class, "PP");
		
		criteria.createAlias("PP."+ MpmPrescricaoProcedimento.Fields.PED_SEQ.toString(), "PED", Criteria.LEFT_JOIN);
		criteria.createAlias("PP."+ MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString(), "PCI", Criteria.LEFT_JOIN);
		criteria.createAlias("PP."+ MpmPrescricaoProcedimento.Fields.MATERIAL.toString(), "MAT", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString(), atdSeq));
		
		prescProced = (MpmPrescricaoProcedimento) executeCriteriaUniqueResult(criteria);
		
		return prescProced;
	}

	public List<MpmPrescricaoProcedimento> obterPrescricoesProcedimentoPai(
			Long seq, Integer seqAtendimento, Date dataHoraInicio,
			Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_ATD_SEQ.toString(), seqAtendimento));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString(), dataHoraFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(), dataHoraInicio)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoProcedimento.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna true quando existir prescrição com o procedimento especial e false caso contrário.
	 * 
	 * @param codigoProcedimento
	 * @return
	 */
	public boolean existePrescricaoComProcedimentoEspecial(Short codigoProcedimento) {
		if(codigoProcedimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class);
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PED_SEQ_SEQ.toString(), codigoProcedimento));

		return executeCriteriaCount(criteria) > 0;
	}

	public List<MpmPrescricaoProcedimento> obterItensPrescricaoProcedimento(
			Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio,
			Date dataFim) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoProcedimento.Fields.PME_SEQ.toString(), prescricaoMedicaSeq));
		criteria.add(Restrictions.lt(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString(), dataFim));
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()),
				Restrictions.and(Restrictions.gt(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(), new Date()), 
						Restrictions.gt(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString(), dataInicio))));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Cursor <code>c_proc</code>
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param pmeDthrIniMvto
	 * @return
	 */
	public  List<ItemAlteracaoNptVO> pesquisarProcedimentosEspeciaisDiversos(Integer atdSeq, Integer pmeSeq, Date pmeDthrIniMvto) {
		StringBuilder hql = new StringBuilder(500);
		hql.append("Select ")
		.append(" ppr.").append(MpmPrescricaoProcedimento.Fields.ALTERADO_EM.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.ALTERADO_EM.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PPR_ATD_SEQ.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PPR_PPR_SEQ.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.ATD_SEQ.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PPR_SEQ.toString())
		.append(", ppr1.").append(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PPR1_ATD_SEQ.toString())
		.append(", ppr1.").append(MpmPrescricaoProcedimento.Fields.SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PPR1_SEQ.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.PED_SEQ_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PED_SEQ.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.PCI_SEQ_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PCI_SEQ.toString())	
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.MAT_CODIGO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.MAT_CODIGO.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.SERVIDOR_VALIDACAO_MATRICULA.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.MATRICULA.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.SERVIDOR_VIN_CODIGO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.VIN_CODIGO.toString())
		.append(", pme.").append(MpmPrescricaoMedica.Fields.PAC_CODIGO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PAC_CODIGO.toString())
		.append(", ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.DTHR_INICIO.toString())	

		.append(" from ").append(MpmPrescricaoProcedimento.class.getName()).append(" ppr ")
		.append(" LEFT JOIN ppr.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO.toString()).append(" ppr1 ")
		.append(',').append(MpmPrescricaoMedica.class.getName()).append(" PME ")

		.append(" WHERE ppr.").append(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()).append(" = ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString())
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString()).append(" = :atdSeq")
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.SEQ.toString()).append(" = :pmeSeq")
		.append(" AND ppr.").append(MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()).append(" = :indPendente")
		.append(" AND (ppr.").append(MpmPrescricaoProcedimento.Fields.CRIADO_EM.toString()).append(" >= :pmeDthrIniMvto")
			.append(" OR ppr.").append(MpmPrescricaoProcedimento.Fields.ALTERADO_EM.toString()).append(" >= :pmeDthrIniMvto)")
		.append(" AND ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" < ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(" AND (ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" is null ")
			.append(" OR ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" > ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString())
			.append(" OR (").append("ppr." ).append( MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" = ")
			.append("ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" AND ")
			.append("ppr.").append(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" >= ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).append("))")
		.append(" AND (ppr.").append(MpmPrescricaoProcedimento.Fields.ATD_SEQ.toString()).append(",ppr.")
				.append(MpmPrescricaoProcedimento.Fields.SEQ.toString()).append(") NOT IN (").append(obterSubQueryProcedimentosEspeciaisDiversos())
					.append(')');
		
		//TODO Tive de importar desta forma, pois ha outra classe Query do pacote javax.persistence sendo utilizada nesta DAO.
		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("pmeSeq", pmeSeq);
		query.setParameter("pmeDthrIniMvto", pmeDthrIniMvto);
		query.setParameter("indPendente", DominioIndPendenteItemPrescricao.N);
		query.setResultTransformer(Transformers.aliasToBean(ItemAlteracaoNptVO.class));
		
		return query.list();
	}
	
	/**
	 * Cursor <code>c_proc</code>
	 * 
	 * @return
	 */
	private String obterSubQueryProcedimentosEspeciaisDiversos() {
		StringBuilder hql = new StringBuilder(250);
		hql.append("select a.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_ATD_SEQ.toString())
		.append(", a.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_SEQ.toString())
		.append(" FROM ").append(MpmPrescricaoProcedimento.class.getName()).append(" a ")
		.append(',').append(MpmPrescricaoMedica.class.getName()).append(" pme ")
		.append(" WHERE PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString()).append(" = :atdSeq")
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.SEQ.toString()).append(" = :pmeSeq")
		.append(" AND a.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_ATD_SEQ.toString()).append(" = ")
			.append(" PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString())
		.append(" AND a.").append(MpmPrescricaoProcedimento.Fields.PRESCRICAO_PROCEDIMENTO_SEQ.toString()).append(" = ")
		 	.append(" PPR.").append(MpmPrescricaoMedica.Fields.SEQ.toString())
		 .append(" AND a.").append(MpmPrescricaoProcedimento.Fields.IND_PENDENTE.toString()).append(" = :indPendente")
		 .append(" AND (a.").append(MpmPrescricaoProcedimento.Fields.CRIADO_EM.toString()).append(" >= :pmeDthrIniMvto")
			.append(" OR a.").append(MpmPrescricaoProcedimento.Fields.ALTERADO_EM.toString()).append(" >= :pmeDthrIniMvto)")
		.append(" AND a.").append(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" < ")
		.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(" AND (a.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" is null ")
			.append(" OR a.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" > ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString())
			.append(" OR (").append("a." ).append( MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" = ")
			.append("a.").append(MpmPrescricaoProcedimento.Fields.DTHR_FIM.toString()).append(" AND ")
			.append("a.").append(MpmPrescricaoProcedimento.Fields.DTHR_INICIO.toString()).append(" >= ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).append("))");
		
		return hql.toString();
	}

}
