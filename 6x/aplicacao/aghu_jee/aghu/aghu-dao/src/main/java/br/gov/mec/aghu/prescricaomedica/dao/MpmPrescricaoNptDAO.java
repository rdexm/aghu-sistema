package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.faturamento.vo.ItemAlteracaoNptVO;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmPrescricaoNptDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoNpt> {

	

	private static final long serialVersionUID = -8222120261001011695L;

	/**
	 * obtém a Nutrição Parental pelo id.
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoNpt obterNutricaoParentaLPeloId(Integer atdSeq,	Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_SEQ.toString(),	seq));
		MpmPrescricaoNpt retorno = (MpmPrescricaoNpt) this.executeCriteriaUniqueResult(criteria);
		return retorno;
	}
	
	/**
	 * ORADB cursor c_prcr_npt, arquivo mpmf_vis_itens_prcr.pll, procedure
	 * MPMP_POPULA_NPT. Versão atualizada da consulta.
	 * 
	 * @author mtocchetto
	 * @param pmeAtdSeq
	 * @param pmeSeq
	 * @return
	 */
	public List<MpmPrescricaoNpt> pesquisarPrescricaoNptPorPME(
			MpmPrescricaoMedicaId pmeId, Date dthrFimPme, Boolean listarTodas) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_ID.toString(),
				pmeId));
		if(!listarTodas) {
			criteria.add(Restrictions.eq(
					MpmPrescricaoNpt.Fields.DTHR_FIM.toString(), dthrFimPme));
		}

		return executeCriteria(criteria);
	}

	/**
	 * obtém a Nutrição Parental Total pelo id.
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoNpt obterNutricaoParentalTotalPeloId(Integer atdSeq, Integer seq) {

		DetachedCriteria criteria = getCriteriaObterNPTPeloId(atdSeq, seq);
		MpmPrescricaoNpt retorno = (MpmPrescricaoNpt) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;
	}
	
	private DetachedCriteria getCriteriaObterNPTPeloId(Integer atdSeq,
			Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.createAlias(MpmPrescricaoNpt.Fields.AFA_FORMULA_NPT_PADRAO.toString(), "FNP");
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_SEQ.toString(),
				seq));
		return criteria;
	}

	/**
	 * Busca todos procedimentos de prescricao npt de um atendimento
	 * 
	 * @param altanAtdSeq
	 * @return
	 */
	public List<MpmPrescricaoNpt> obterProcedimentosPendentes(
			Integer altanAtdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		return executeCriteria(criteria);

	}

	/**
	 * Busca menor data de um procedimento prescrito npt
	 * 
	 * @param altanAtdSeq
	 * @param pedSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date obterDataInicioMinimaPrescricaoNpt(Integer altanAtdSeq,
			Short pedSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.addOrder(Order.asc(MpmPrescricaoNpt.Fields.DTHR_INICIO
				.toString()));

		List<MpmPrescricaoNpt> prescricaoNpt = executeCriteria(criteria);

		if (prescricaoNpt != null && prescricaoNpt.size() > 0) {
			return prescricaoNpt.get(0).getDthrInicio();
		}

		return null;
	}

	/**
	 * Pesquisa Nutrições Parentais para processar o cancelamento das mesmas.
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmPrescricaoNpt> pesquisarPrescricaoNptParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
				MpmPrescricaoNpt.Fields.PME_SEQ.toString(), pmeSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);

		criteria.add(Restrictions.in(
				MpmPrescricaoNpt.Fields.IND_PENDENTE.toString(), restricaoIn));

		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoNpt.Fields.CRIADO_EM.toString(), dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoNpt.Fields.ALTERADO_EM.toString(), dthrMovimento);

		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual,
				criterionAlteradoEmMaiorIgual));

		List<MpmPrescricaoNpt> retorno = executeCriteria(criteria);

		return retorno;
	}

	/**
	 * Método que pesquisa todas as nutrições parentais de uma prescrição médica
	 * 
	 * @param id
	 * @return
	 */
	public List<MpmPrescricaoNpt> pesquisarTodosNptPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_ID.toString(),
				id));

		List<MpmPrescricaoNpt> list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Consulta pesquisa NPT;
	 * 
	 * Implementa parte do cursor cur_pnp;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA;
	 * PLL: MPMF_SUMARIO_ALTA.pll 
     * MPMP_LISTA_OUT_PROCEDIMENTOS;
	 * 
	 * @param {Integer} atdSeq Seq do atendimento.
	 * @return Registros encontrados.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarPrescricaoNptConfirmadoPorAtendimento(
			Integer atdSeq) {

		StringBuilder hql = new StringBuilder(200);
		hql.append(" select distinct pnp."
				).append( MpmPrescricaoNpt.Fields.PED_SEQ.toString() ).append( ", ");
		hql.append(" pnp." ).append( MpmPrescricaoNpt.Fields.PED_DESCRICAO.toString()
				).append(' ');
		hql.append(" from ");
		hql.append(" MpmPrescricaoNpt pnp ");
		hql.append(" where ");
		hql.append(" pnp." ).append( MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ");
		hql.append(" pnp."
				).append( MpmPrescricaoNpt.Fields.IND_PENDENTE.toString()
				).append( " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		return query.getResultList();

	}

	/**
	 * Pega a menor data inicio;
	 * 
	 * Implementa cursor cur_pnp_min_ped;
	 * 
	 * FORM: MPMF_SUMARIO_ALTA;
	 * PLL: MPMF_SUMARIO_ALTA.pll;
	 * FUNCTION: MPMP_LISTA_OUT_PROCEDIMENTOS;
	 * 
	 * @param {Integer} atdSeq
	 * @param {Short} pedSeq
	 * @return
	 */
	public Date pesquisarMenorDataInicoNptConfirmadoPorAtendimentoEEspecialDiverso(
			Integer atdSeq, Short pedSeq) {

		Date response = null;

		StringBuilder hql = new StringBuilder(200);
		hql.append(" select min("
				).append( MpmPrescricaoNpt.Fields.DTHR_INICIO.toString() ).append( ") ");
		hql.append(" from ");
		hql.append(" MpmPrescricaoNpt pnp ");
		hql.append(" where ");
		hql.append(" pnp." ).append( MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ");
		hql.append(" pnp." ).append( MpmPrescricaoNpt.Fields.PED_SEQ.toString()
				).append( " = :pPedSeq AND ");
		hql.append(" pnp." ).append( MpmPrescricaoNpt.Fields.IND_PENDENTE.toString()
				).append( " = :pIndPendente ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pPedSeq", pedSeq);
		query.setParameter("pIndPendente", DominioIndPendenteItemPrescricao.N);

		query.getResultList();

		List<Object> lista = query.getResultList();
		if (lista != null && lista.size() > 0) {

			response = (Date) lista.get(0);

		}

		return response;

	}

	

	/**
	 * Método que pesquisa as nutricoes parentais de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoNpt> obterPrescricoesNptParaSumarioPrescricao(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		List<MpmPrescricaoNpt> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoNpt.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoDieta.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));
		
		criteria.add(Restrictions.isNotNull(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS.toString()));		
		
		lista = executeCriteria(criteria);
		
		return lista;

	}

	public List<MpmPrescricaoNpt> obterPrescricoesNptPai(Integer seq,
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_ATD_SEQ.toString(), seqAtendimento));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoNpt.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoDieta.Fields.DTHR_INICIO.toString(), dataHoraFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(), dataHoraInicio)));
		
		criteria.add(Restrictions.isNotNull(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS.toString()));		
		
		return executeCriteria(criteria);
	}
	
	public List<MpmPrescricaoNpt> obterItensPrescricaoNpt(Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_SEQ.toString(), prescricaoMedicaSeq));
		criteria.add(Restrictions.lt(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString(), dataFim));
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoNpt.Fields.DTHR_FIM.toString(), dataInicio)));
		
		return executeCriteria(criteria);
		
	}

	/**
	 * Implementacao do  
	 * Cursor <code>c_npts</code>
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param pmeDthrIniMvto
	 * @return
	 */
	public  List<ItemAlteracaoNptVO> pesquisarAlteracoesNpt(Integer atdSeq, Integer pmeSeq, Date pmeDthrIniMvto) {
		StringBuilder hql = new StringBuilder(500);
		hql.append("Select ")
		.append(" pnp.").append(MpmPrescricaoNpt.Fields.ALTERADO_EM.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.ALTERADO_EM.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PNP_ATD_SEQ.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PNP_SEQ.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.ATD_SEQ.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.ID_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.SEQ.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.PED_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PED_SEQ.toString())
		.append(", pnp1.").append(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PNP1_ATD_SEQ.toString())
		.append(", pnp1.").append(MpmPrescricaoNpt.Fields.ID_SEQ.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PNP1_SEQ.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO_MATRICULA.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.MATRICULA.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.SERVIDOR_VIN_CODIGO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.VIN_CODIGO.toString())
		.append(", pme.").append(MpmPrescricaoMedica.Fields.PAC_CODIGO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.PAC_CODIGO.toString())
		.append(", pnp.").append(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString())
			.append(" as ").append(ItemAlteracaoNptVO.Fields.DTHR_INICIO.toString())	

		.append(" from ").append(MpmPrescricaoNpt.class.getName()).append(" PNP ")
		.append(" LEFT JOIN pnp.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS.toString()).append(" PNP1 ")
		.append(',').append(MpmPrescricaoMedica.class.getName()).append(" PME ")

		.append(" WHERE PNP.").append(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString()).append(" = ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString())
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString()).append(" = :atdSeq")
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.SEQ.toString()).append(" = :pmeSeq")
		.append(" AND PNP.").append(MpmPrescricaoNpt.Fields.IND_PENDENTE.toString()).append(" = :indPendente")
		.append(" AND (PNP.").append(MpmPrescricaoNpt.Fields.CRIADO_EM.toString()).append(" >= :pmeDthrIniMvto")
			.append(" OR PNP.").append(MpmPrescricaoNpt.Fields.ALTERADO_EM.toString()).append(" >= :pmeDthrIniMvto)")
		.append(" AND PNP.").append(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" < ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(" AND (PNP.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" is null ")
			.append(" OR PNP.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" > ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString())
			.append(" OR (").append("PNP." ).append( MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" = ")
			.append("PNP.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" AND ")
			.append("PNP.").append(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" >= ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).append("))")
		.append(" AND (PNP.").append(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString()).append(",PNP.")
				.append(MpmPrescricaoNpt.Fields.ID_SEQ.toString()).append(") NOT IN (").append(obterSubQueryAlteracoesNpt())
					.append(')');
		
		//Tive de importar desta forma, pois ha outra classe Query do pacote javax.persistence sendo utilizada nesta DAO.
		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("pmeSeq", pmeSeq);
		query.setParameter("pmeDthrIniMvto", pmeDthrIniMvto);
		query.setParameter("indPendente", DominioIndPendenteItemPrescricao.N);
		query.setResultTransformer(Transformers.aliasToBean(ItemAlteracaoNptVO.class));
		
		return query.list();
	}
	
	/**
	 * ORADB Cursor c_npts
	 * 
	 * @return
	 */
	private String obterSubQueryAlteracoesNpt() {
		StringBuilder hql = new StringBuilder(300);
		hql.append("select a.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_ATD_SEQ.toString())
		.append(", a.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_SEQ.toString())
		.append(" FROM ").append(MpmPrescricaoNpt.class.getName()).append(" a ")
		.append(',').append(MpmPrescricaoMedica.class.getName()).append(" pme ")
		.append(" WHERE PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString()).append(" = :atdSeq")
		.append(" AND PME.").append(MpmPrescricaoMedica.Fields.SEQ.toString()).append(" = :pmeSeq")
		.append(" AND a.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_ATD_SEQ.toString()).append(" = ")
			.append(" PME.").append(MpmPrescricaoMedica.Fields.ATD_SEQ.toString())
		.append(" AND a.").append(MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_SEQ.toString()).append(" = ")
		 	.append(" PNP.").append(MpmPrescricaoMedica.Fields.SEQ.toString())
		 .append(" AND a.").append(MpmPrescricaoNpt.Fields.IND_PENDENTE.toString()).append(" = :indPendente")
		 .append(" AND (a.").append(MpmPrescricaoNpt.Fields.CRIADO_EM.toString()).append(" >= :pmeDthrIniMvto")
			.append(" OR a.").append(MpmPrescricaoNpt.Fields.ALTERADO_EM.toString()).append(" >= :pmeDthrIniMvto)")
		.append(" AND a.").append(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" < ")
		.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(" AND (a.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" is null ")
			.append(" OR a.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" > ")
			.append("PME." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString())
			.append(" OR (").append("a." ).append( MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" = ")
			.append("a.").append(MpmPrescricaoNpt.Fields.DTHR_FIM.toString()).append(" AND ")
			.append("a.").append(MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()).append(" >= ")
			.append("PME.").append(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).append("))")
		 ;
		
		return hql.toString();
	}
	
	public List <MpmPrescricaoNpt> informacoesPrescricaoNPTAntiga(MpmPrescricaoMedica prescricaoMedicaAnterior){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_SEQ.toString(), prescricaoMedicaAnterior.getId().getSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), prescricaoMedicaAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.DTHR_FIM.toString(), prescricaoMedicaAnterior.getDthrFim()));
		criteria.addOrder(Order.asc(MpmPrescricaoNpt.Fields.ID_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * obtém a Nutrição Parental Total pelo id e faz join com Servidor.
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoNpt obterNutricaoParenteralTotalPeloIdComServidor(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_SEQ.toString(),seq));
		criteria.createAlias(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		return (MpmPrescricaoNpt) this.executeCriteriaUniqueResult(criteria);
	}

	public RapServidoresId obterServidorValida(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = getCriteriaObterNPTPeloId(atdSeq, seq);
		criteria.createAlias(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO.toString(), "SERV");
		criteria.setProjection(Projections.projectionList().add(Projections.property(MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO_ID.toString())));
		RapServidoresId retorno = (RapServidoresId) this.executeCriteriaUniqueResult(criteria);
		return retorno;
	}
	
	// #990 C3
	public MpmPrescricaoNptVO buscarDadosPrescricaoNpt(Integer atdSeq, Integer pnpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class, "PNP");
		criteria.createAlias("PNP." + MpmPrescricaoNpt.Fields.AFA_FORMULA_NPT_PADRAO, "FNP", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PNP." + MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString() , atdSeq));
		criteria.add(Restrictions.eq("PNP." + MpmPrescricaoNpt.Fields.ID_SEQ.toString() , pnpSeq));
		criarProjectionCriteriaPrescricaoNpt(criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmPrescricaoNptVO.class));
		return (MpmPrescricaoNptVO) executeCriteriaUniqueResult(criteria);
	}

	private void criarProjectionCriteriaPrescricaoNpt(DetachedCriteria criteria) {
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString()), MpmPrescricaoNptVO.Fields.ATD_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.ID_SEQ.toString()), MpmPrescricaoNptVO.Fields.SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_ATD_SEQ.toString()), MpmPrescricaoNptVO.Fields.PNP_ATD_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PRESCRICAO_NPTS_SEQ.toString()), MpmPrescricaoNptVO.Fields.PNP_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_MATRICULA.toString()), MpmPrescricaoNptVO.Fields.MATRICULA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_VIN_CODIGO.toString()), MpmPrescricaoNptVO.Fields.VIN_CODIGO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_MOVIMENTADO_MATRICULA.toString()), MpmPrescricaoNptVO.Fields.MATRICULA_MVTO .toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_MOVIMENTADO_CODIGO.toString()), MpmPrescricaoNptVO.Fields.VIN_CODIGO_MVTO.toString())
			.add(Projections.property("FNP." + AfaFormulaNptPadrao.Fields.SEQ.toString()), MpmPrescricaoNptVO.Fields.FNP_SEQ.toString())
			.add(Projections.property("FNP." + AfaFormulaNptPadrao.Fields.DESCRICAO.toString()), MpmPrescricaoNptVO.Fields.DESCRICAO_FORMULA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PED_SEQ.toString()), MpmPrescricaoNptVO.Fields.PED_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.DTHR_INICIO.toString()), MpmPrescricaoNptVO.Fields.DTHR_INICIO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SEGUE_GOTEJO_PADRAO.toString()), MpmPrescricaoNptVO.Fields.IND_SEGUE_GOTEJO_PADRAO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.IND_PENDENTE.toString()), MpmPrescricaoNptVO.Fields.IND_PENDENTE.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.CRIADO_EM.toString()), MpmPrescricaoNptVO.Fields.CRIADO_EM.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.JUSTIFICATIVA.toString()), MpmPrescricaoNptVO.Fields.JUSTIFICATIVA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.DTHR_FIM.toString()), MpmPrescricaoNptVO.Fields.DTHR_FIM.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.OBSERVACAO.toString()), MpmPrescricaoNptVO.Fields.OBSERVACAO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.ALTERADO_EM.toString()), MpmPrescricaoNptVO.Fields.ALTERADO_EM.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_VALIDA_MOVIMENTACAO_CODIGO.toString()), MpmPrescricaoNptVO.Fields.SER_VIN_CODIGO_MVTO_VALIDA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_VALIDA_MOVIMENTACAO_MATRICULA.toString()), MpmPrescricaoNptVO.Fields.SER_MATRICULA_MVTO_VALIDA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO_CODIGO.toString()), MpmPrescricaoNptVO.Fields.SER_VIN_CODIGO_VALIDA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.SERVIDOR_VALIDACAO_MATRICULA.toString()), MpmPrescricaoNptVO.Fields.SER_MATRICULA_VALIDA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.JNP_SEQ.toString()), MpmPrescricaoNptVO.Fields.JNP_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PARAM_VOLUME_ML.toString()), MpmPrescricaoNptVO.Fields.PARAM_VOLUME_ML.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.TIPO_PARAM_VOLUME.toString()), MpmPrescricaoNptVO.Fields.TIPO_PARAM_VOLUME.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.VOLUME_CALCULADO.toString()), MpmPrescricaoNptVO.Fields.VOLUME_CALCULADO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.VOLUME_DESEJADO.toString()), MpmPrescricaoNptVO.Fields.VOLUME_DESEJADO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.TEMPO_H_INFUSAO_SOLUCAO.toString()), MpmPrescricaoNptVO.Fields.TEMPO_H_INFUSAO_SOLUCAO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.TEMPO_H_INFUSAO_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.TEMPO_H_INFUSAO_LIPIDIOS.toString())				
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.CALORIAS_DIA.toString()), MpmPrescricaoNptVO.Fields.CALORIAS_DIA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.CALORIAS_KG_DIA.toString()), MpmPrescricaoNptVO.Fields.CALORIAS_KG_DIA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.REL_CAL_N_PROT_NITROGENIO.toString()), MpmPrescricaoNptVO.Fields.REL_CAL_N_PROT_NITROGENIO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PERC_CAL_AMINOACIDOS.toString()), MpmPrescricaoNptVO.Fields.PERC_CAL_AMINOACIDOS.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PERC_CAL_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.PERC_CAL_LIPIDIOS.toString())				
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PERC_CAL_GLICOSE.toString()), MpmPrescricaoNptVO.Fields.PERC_CAL_GLICOSE.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.GLICOSE_REL_GLIC_LIPID.toString()), MpmPrescricaoNptVO.Fields.GLICOSE_REL_GLIC_LIPID.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.RELACAO_CALCIO_FOSFORO.toString()), MpmPrescricaoNptVO.Fields.RELACAO_CALCIO_FOSFORO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.CONC_GLIC_SEM_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.CONC_GLIC_SEM_LIPIDIOS.toString())				
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.CONC_GLIC_COM_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.CONC_GLIC_COM_LIPIDIOS.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.TAXA_INFUSAO_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.TAXA_INFUSAO_LIPIDIOS.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.OSMOLARIDADE_SEM_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.OSMOLARIDADE_SEM_LIPIDIOS.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.OSMOLARIDADE_COM_LIPIDIOS.toString()), MpmPrescricaoNptVO.Fields.OSMOLARIDADE_COM_LIPIDIOS.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PCA_ATD_SEQ.toString()), MpmPrescricaoNptVO.Fields.PCA_ATD_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PCA_CRIADO_EM.toString()), MpmPrescricaoNptVO.Fields.PCA_CRIADO_EM.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PARAM_TIPO_LIPIDIO.toString()), MpmPrescricaoNptVO.Fields.PARAM_TIPO_LIPIDIO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.DURACAO_TRAT_SOLICITADO.toString()), MpmPrescricaoNptVO.Fields.DURACAO_TRAT_SOLICITADO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.IND_BOMBA_INFUSAO.toString()), MpmPrescricaoNptVO.Fields.IND_BOMBA_INFUSAO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PME_ATD_SEQ.toString()), MpmPrescricaoNptVO.Fields.PME_ATD_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PME_SEQ.toString()), MpmPrescricaoNptVO.Fields.PME_SEQ.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.DTHR_VALIDA.toString()), MpmPrescricaoNptVO.Fields.DTHR_VALIDA.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.DTHR_VALIDA_MVTO.toString()), MpmPrescricaoNptVO.Fields.DTHR_VALIDA_MVTO.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PNP_ATD_SEQ_PRCR_ANT.toString()), MpmPrescricaoNptVO.Fields.PNP_ATD_SEQ_PRCR_ANT.toString())
			.add(Projections.property("PNP." + MpmPrescricaoNpt.Fields.PNP_SEQ_PRCR_ANT.toString()), MpmPrescricaoNptVO.Fields.PNP_SEQ_PRCR_ANT.toString())
		);
	}
	/**
	 * Verifica se a prescrição tem algum item de npt
	 */
	public Boolean verificarPrescricaoNpt(Integer pmeSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.createAlias(MpmPrescricaoNpt.Fields.PME.toString(), "PME", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PME."+MpmPrescricaoMedica.Fields.SEQ.toString(), pmeSeq));
		return executeCriteriaExists(criteria);
	}
	
	public List<MpmPrescricaoNpt> pesquisarNptsUltimaPrescricao(MpmPrescricaoMedica ultimaPrescricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.ID_ATD_SEQ.toString(), ultimaPrescricao.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.PME_SEQ.toString(), ultimaPrescricao.getId().getSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoNpt.Fields.DTHR_FIM.toString(), ultimaPrescricao.getDthrFim()));
		criteria.addOrder(Order.asc(MpmPrescricaoNpt.Fields.ID_SEQ.toString()));
		return executeCriteria(criteria);
	}
}