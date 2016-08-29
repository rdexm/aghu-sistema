package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmLaudo> {

	private static final long serialVersionUID = 8661492508473807602L;

	/**
	 * Busca uma lista de MpmLaudo associados ao AghAtendimento sem
	 * Justificativa para o tipo de laudo menor Permanencia.<br>
	 * 
	 * @param atendimento
	 * @param menorPermanencia
	 * @return
	 * @throws IllegalArgumentException
	 */
	public List<MpmLaudo> buscaLaudoMenorPermaneciaPendenteJustificativa(
			AghAtendimentos atendimento, Short menorPermanencia) {
		if (atendimento == null || menorPermanencia == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(),
				atendimento));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.TIPO_LAUDO_SEQ.toString(),
				menorPermanencia));
		criteria.add(Restrictions.isNull(MpmLaudo.Fields.JUSTIFICATIVA
				.toString()));

		return executeCriteria(criteria);
	}

	public boolean existePendenciaLaudo(AghAtendimentos atendimento) {

		if (atendimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(),
				atendimento));
		criteria.add(Restrictions.isNotNull(MpmLaudo.Fields.TIPO_LAUDO
				.toString()));
		criteria.add(Restrictions.isNull(MpmLaudo.Fields.JUSTIFICATIVA
				.toString()));

		return executeCriteriaCount(criteria) > 0;

	}

	/**
	 * Lista uma sequencia de laudos filtrando pelo identificador do
	 * atendimento.
	 * 
	 * @param atdSeq
	 *            (identificador do atendimento)
	 * @return retorna uma lista de laudos
	 */
	public List<MpmLaudo> listarLaudosPorAtendimento(Integer atdSeq) {
		List<MpmLaudo> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.setFetchMode(MpmLaudo.Fields.TIPO_LAUDO.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions
				.isNull(MpmLaudo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO
						.toString()));

		criteria.addOrder(Order.asc(MpmLaudo.Fields.DTHR_INICIO_VALIDADE
				.toString()));

		lista = executeCriteria(criteria);

		return lista;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<SubRelatorioLaudosProcSusVO> buscarJustificativasLaudoProcedimentoSUS(
			Integer seqAtendimento) throws ApplicationBusinessException {
		List<SubRelatorioLaudosProcSusVO> listaVO = new ArrayList<SubRelatorioLaudosProcSusVO>();
		List<SubRelatorioLaudosProcSusVO> listaVOresult = new ArrayList<SubRelatorioLaudosProcSusVO>();
		StringBuffer hql;
		Query query;

		hql = new StringBuffer();
		hql.append(" select distinct new br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO (");
		hql.append(" 	1, ");
		hql.append(" 	0l, ");
		hql.append(" 	tuo.");
		hql.append(MpmTipoLaudo.Fields.DESCRICAO.toString());
		hql.append(", ");
		hql.append(" 	lad.");
		hql.append(MpmLaudo.Fields.JUSTIFICATIVA.toString());
		hql.append(' ');
		hql.append(" ) ");
		hql.append(" from MpmLaudo lad ");
		hql.append(" 	join lad.");
		hql.append(MpmLaudo.Fields.TIPO_LAUDO.toString());
		hql.append(" as tuo ");
		hql.append(" where lad.");
		hql.append(MpmLaudo.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and tuo.");
		hql.append(MpmTipoLaudo.Fields.LAUDO_UNICO_ATEND.toString());
		hql.append(" = :laudoUnicoAtend ");

		query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("laudoUnicoAtend", true);

		listaVO = query.list();
		if (listaVO != null && !listaVO.isEmpty()) {
			listaVOresult.addAll(listaVO);
		}

		hql = new StringBuffer();
		hql.append(" select distinct new br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO (");
		hql.append(" 	2, ");
		hql.append(" 	lad.");
		hql.append(MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString());
		hql.append(", tuo.");
		hql.append(MpmTipoLaudo.Fields.DESCRICAO.toString());
		hql.append(", ");
		hql.append(" 	lad.");
		hql.append(MpmLaudo.Fields.JUSTIFICATIVA.toString());
		hql.append(" ) ");
		hql.append(" from MpmLaudo lad ");
		hql.append(" 	join lad.");
		hql.append(MpmLaudo.Fields.TIPO_LAUDO.toString());
		hql.append(" as tuo ");
		hql.append(" where lad.");
		hql.append(MpmLaudo.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and tuo.");
		hql.append(MpmTipoLaudo.Fields.LAUDO_UNICO_ATEND.toString());
		hql.append(" != :laudoUnicoAtend ");

		query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("laudoUnicoAtend", Boolean.TRUE);

		listaVO = query.list();
		if (listaVO != null && !listaVO.isEmpty()) {
			listaVOresult.addAll(listaVO);
		}

		hql = new StringBuffer();
		hql.append(" select distinct new br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO (");
		hql.append(" 	3, ");
		hql.append(" 	lad.");
		hql.append(MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString());
		hql.append(", phi.");
		hql.append(FatProcedHospInternos.Fields.DESCRICAO.toString());
		hql.append(", ");
		hql.append(" 	lad.");
		hql.append(MpmLaudo.Fields.JUSTIFICATIVA.toString());
		hql.append(" ) ");
		hql.append(" from MpmLaudo lad ");
		hql.append(" 	join lad.");
		hql.append(MpmLaudo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" where lad.");
		hql.append(MpmLaudo.Fields.ATD_SEQ.toString());
		hql.append("");
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and phi.");
		hql.append(FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL.toString());
		hql.append(" is not null ");

		query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);

		listaVO = query.list();
		if (listaVO != null && !listaVO.isEmpty()) {
			listaVOresult.addAll(listaVO);
		}

		return listaVOresult;
	}

	@SuppressWarnings("unchecked")
	public String buscaJustificativaItemLaudo(Integer atdSeq, Integer phiSeq) {
		String justificativa = null;

		StringBuffer hql = new StringBuffer(130);

		hql.append(" select lad.");
		hql.append(MpmLaudo.Fields.JUSTIFICATIVA.toString());
		hql.append(" from MpmLaudo lad ");
		hql.append(" 	join lad.");
		hql.append(MpmLaudo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" where lad.");
		hql.append(MpmLaudo.Fields.ATD_SEQ.toString());
		hql.append(" = :atdSeq ");
		hql.append(" 	and (phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :seq OR phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ
				.toString());
		hql.append(" = :phiSeq) ");
		hql.append(" order by lad.");
		hql.append(MpmLaudo.Fields.CRIADO_EM.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("seq", phiSeq);
		query.setParameter("phiSeq", phiSeq);

		List<String> justificativas = query.list();

		if (justificativas != null && !justificativas.isEmpty()) {
			justificativa = justificativas.get(0);
		}

		return justificativa;
	}

	@SuppressWarnings("unchecked")
	public List<ProcedimentoHospitalarInternoVO> buscarListaProcedimentosHospitalaresInterno(
			Integer seqAtendimento) {
		StringBuffer hql = new StringBuffer(200);
		hql.append(" select distinct new br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoHospitalarInternoVO (");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(", ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ
				.toString());
		hql.append(" ) ");
		hql.append(" from MpmLaudo lad ");
		hql.append(" 	join lad.");
		hql.append(MpmLaudo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" where lad.");
		hql.append(MpmLaudo.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and phi.");
		hql.append(FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL.toString());
		hql.append(" is null ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);

		return query.list();
	}

	public List<MpmLaudo> listarLaudosPorAtendimentoETipo(
			AghAtendimentos atendimento, MpmTipoLaudo tipoLaudo, Date dthrInicio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(),
				atendimento));

		criteria.add(Restrictions.eq(MpmLaudo.Fields.TIPO_LAUDO.toString(),
				tipoLaudo));

		criteria.add(Restrictions.or(Restrictions.and(Restrictions
				.isNotNull(MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString()),
				Restrictions.ge(MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString(),
						dthrInicio)), Restrictions
				.isNull(MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString())));

		criteria.addOrder(Order.asc(MpmLaudo.Fields.DTHR_INICIO_VALIDADE
				.toString()));

		return this.executeCriteria(criteria);
	}

	public Long obterCountLaudosPorTipoEAtendimento(
			AghAtendimentos atendimento, MpmTipoLaudo tipoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(),
				atendimento));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.TIPO_LAUDO.toString(),
				tipoLaudo));

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Busca um MpmLaudo pelo AghAtendimento e pelo Laudo de Menor permanencia.<br>
	 * 
	 * @param atendimento
	 * @param vLaudoMenorPerm
	 * @return
	 */
	public MpmLaudo buscaMpmLaudo(AghAtendimentos atendimento,
			Short vLaudoMenorPerm) {
		if (atendimento == null || vLaudoMenorPerm == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(),
				atendimento));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.TIPO_LAUDO_SEQ.toString(),
				vLaudoMenorPerm));

		List<MpmLaudo> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Busca laudo no período de validade pelo seq do atendimento
	 * 
	 * @param atdSeq
	 * @param pDtDesdobr
	 * @return
	 */
	public List<MpmLaudo> listarLaudosPorAtendimentoData(Integer atdSeq,
			Date pDtDesdobr) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.lt(
				MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString(), pDtDesdobr));

		//if (Calendar.getInstance().after(pDtDesdobr)) {
			criteria.add(Restrictions.or(Restrictions
					.isNull(MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString()),
					Restrictions.gt(
							MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString(),
							pDtDesdobr)));
		/*} else {
			criteria.add(Restrictions.gt(
					MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString(), pDtDesdobr));
		}*/

		return executeCriteria(criteria);
	}
	
	/**
	 * Busca laudos anteriores ordenados pela data de validade e de maneira descendente 
	 * @param atendimento
	 * @param tipoLaudo
	 * @return
	 */
	public List<MpmLaudo> listarLaudosAnteriores(final AghAtendimentos atendimento, final MpmTipoLaudo tipoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		
		criteria.createAlias(MpmLaudo.Fields.ATENDIMENTO.toString(), MpmLaudo.Fields.ATENDIMENTO.toString());

		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.TIPO_LAUDO.toString(), tipoLaudo));
		criteria.add(Restrictions.isNull(MpmLaudo.Fields.DTHR_FIM_VALIDADE.toString()));

		criteria.addOrder(Order.desc(MpmLaudo.Fields.DTHR_INICIO_VALIDADE.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Método que obtém os laudos não impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmLaudo> pesquisarLaudosNaoImpressosPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.IND_IMPRESSO.toString(), false));
		List<MpmLaudo> listaLaudos = executeCriteria(criteria);
		return listaLaudos;
	}

	/**
	 * Método que obtém os laudos impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmLaudo> pesquisarLaudosImpressosPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmLaudo.class);
		criteria.add(Restrictions.eq(MpmLaudo.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmLaudo.Fields.IND_IMPRESSO.toString(), true));
		List<MpmLaudo> listaLaudos = executeCriteria(criteria);
		return listaLaudos;
	}
}
