package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimentoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmAltaOtrProcedimentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaOtrProcedimento> {

	private static final long serialVersionUID = 311501907774272161L;

	@Override
	protected void obterValorSequencialId(MpmAltaOtrProcedimento elemento) {

		if ((elemento == null) || (elemento.getMpmAltaSumarios() == null)
				|| (elemento.getMpmAltaSumarios().getId() == null)) {

			throw new IllegalArgumentException(
					"Alta Sumário nao foi informado corretamente.");

		}

		MpmAltaSumarioId idPai = elemento.getMpmAltaSumarios().getId();
		int seqp = this.recuperarMaxSeqMpmAltaOtrProcedimento(idPai);

		Integer seq = seqp + 1;

		if (elemento.getId() == null) {

			elemento.setId(new MpmAltaOtrProcedimentoId());

		}

		elemento.getId().setSeqp(seq.shortValue());
		elemento.getId().setAsuApaAtdSeq(idPai.getApaAtdSeq());
		elemento.getId().setAsuApaSeq(idPai.getApaSeq());
		elemento.getId().setAsuSeqp(idPai.getSeqp());
	}

	/**
	 * Retorna último seq da tabela
	 * 
	 * @param id
	 * @return
	 */
	private Integer recuperarMaxSeqMpmAltaOtrProcedimento(MpmAltaSumarioId id) {

		if ((id == null) || (id.getApaAtdSeq() == null) || (id.getApaSeq() == null)) {

			throw new IllegalArgumentException("Parametro invalido!!!");

		}

		Integer returnValue = null;

		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(altaOtrProcedimento.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmAltaOtrProcedimento.class.getName())
				.append(" altaOtrProcedimento");
		sql.append(" where altaOtrProcedimento.")
				.append(MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString())
				.append(" = :atdSeq ");
		sql.append(" and altaOtrProcedimento.")
				.append(MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString())
				.append(" = :apaSeq ");
		sql.append(" and altaOtrProcedimento.")
				.append(MpmAltaOtrProcedimento.Fields.ASU_SEQP.toString())
				.append(" = :seqp ");

		Query query = this.createQuery(sql.toString());
		query.setParameter("atdSeq", id.getApaAtdSeq());
		query.setParameter("apaSeq", id.getApaSeq());
		query.setParameter("seqp", Short.valueOf(id.getSeqp()));
		Object maxSeq = query.getSingleResult();

		if (maxSeq == null) {

			returnValue = Integer.valueOf(0);

		} else {

			returnValue = ((Short) maxSeq).intValue();

		}

		return returnValue;

	}

	/**
	 * Busca um material no sumario de alta
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param matcodigo
	 * @return
	 */
	public boolean possuiMaterialSumarioAlta(Integer altanAtdSeq,
			Integer altanApaSeq, Integer matCodigo) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaOtrProcedimento.class);
		criteria.createAlias(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString(),
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString());
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(),
				altanApaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.MAT_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.ne(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString() + "."
						+ MpmAltaSumario.Fields.IND_TIPO.toString(),
				DominioIndTipoAltaSumarios.TRF));

		List<MpmAltaOtrProcedimento> procedimentos = this
				.executeCriteria(criteria);

		if ((procedimentos != null) && (procedimentos.size() > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * busca um procedimento no sumario de alta
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param pci_seq
	 * @return
	 */
	public boolean possuiProcedimentoCirurgico(Integer altanAtdSeq,
			Integer altanApaSeq, Integer pciSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaOtrProcedimento.class);
		criteria.createAlias(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString(),
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString());
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(),
				altanApaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.ne(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString() + "."
						+ MpmAltaSumario.Fields.IND_TIPO.toString(),
				DominioIndTipoAltaSumarios.TRF));

		List<MpmAltaOtrProcedimento> procedimentos = this
				.executeCriteria(criteria);

		if ((procedimentos != null) && (procedimentos.size() > 0)) {
			return true;
		}

		return false;
	}

	/**
	 * Busca um procedimento diverso no sumario de alta
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param pedSeq
	 * @return
	 */
	public boolean possuiProcedimentoDiverso(Integer altanAtdSeq,
			Integer altanApaSeq, Short pedSeq) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaOtrProcedimento.class);
		criteria.createAlias(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString(),
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString());
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(),
				altanApaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.ne(
				MpmAltaOtrProcedimento.Fields.ALTA_SUMARIO.toString() + "."
						+ MpmAltaSumario.Fields.IND_TIPO.toString(),
				DominioIndTipoAltaSumarios.TRF));

		List<MpmAltaOtrProcedimento> procedimentos = this
				.executeCriteria(criteria);

		if ((procedimentos != null) && (procedimentos.size() > 0)) {
			return true;
		}

		return false;

	}

	/**
	 * Busca otr procedimento do sumário ativo
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimento(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.obterMpmAltaOtrProcedimento(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca otr procedimento do sumário.<br>
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 */
	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimento(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaOtrProcedimento.class);
		
		criteria.add(Restrictions.eq(MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaOtrProcedimento.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaOtrProcedimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));			
		}
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca otr procedimento do sumário ativo
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimentoSliderProcedimentos(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmAltaOtrProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(),
				altanApaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		
		Disjunction disjuntion = Restrictions.disjunction();
		disjuntion.add(Restrictions.isNotNull(MpmAltaOtrProcedimento.Fields.PCI_SEQ.toString()));
		disjuntion.add(Restrictions.isNotNull(MpmAltaOtrProcedimento.Fields.PED_SEQ.toString()));
		disjuntion.add(Restrictions.isNotNull(MpmAltaOtrProcedimento.Fields.MAT_CODIGO_CODIGO.toString()));
		
		criteria.add(disjuntion);
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca otr procedimento do sumário ativo
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimentoSliderOutrosProcedimentos(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmAltaOtrProcedimento.class);
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString(),
				altanAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString(),
				altanApaSeq));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		criteria.add(Restrictions.eq(
				MpmAltaOtrProcedimento.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		
		criteria.add(Restrictions.isNull(MpmAltaOtrProcedimento.Fields.PCI_SEQ.toString()));
		criteria.add(Restrictions.isNull(MpmAltaOtrProcedimento.Fields.PED_SEQ.toString()));
		criteria.add(Restrictions.isNull(MpmAltaOtrProcedimento.Fields.MAT_CODIGO_CODIGO.toString()));
		
		return this.executeCriteria(criteria);
	}

	public MpmAltaOtrProcedimento obterAltaOtrProcedimentoOriginalDesatachado(
			MpmAltaOtrProcedimento altaOtrProcedimento) {
		// Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
		// desatachado.
		this.desatachar(altaOtrProcedimento);
		return this.obterPorChavePrimaria(altaOtrProcedimento.getId());
	}

	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaOtrProcedimento.Fields.DTHR_OUT_PROCEDIMENTO.toString() + "," +
			MpmAltaOtrProcedimento.Fields.DESC_OUT_PROCEDIMENTO.toString() + "," +
			MpmAltaOtrProcedimento.Fields.COMPL_OUT_PROCEDIMENTO.toString() + 
				") from MpmAltaOtrProcedimento " + 
		   "where "+ MpmAltaOtrProcedimento.Fields.ASU_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaOtrProcedimento.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaOtrProcedimento.Fields.ASU_SEQP.toString() + "=:asuSeqp " +
		   " and " + MpmAltaOtrProcedimento.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		
		Query query = createQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);		
		
		return query.getResultList();
	}		
	
}
