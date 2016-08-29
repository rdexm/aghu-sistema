package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MpmAltaCirgRealizada;
import br.gov.mec.aghu.model.MpmAltaCirgRealizadaId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmAltaCirgRealizadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaCirgRealizada> {

	private static final long serialVersionUID = 1070044784615904352L;


	@Override
	protected void obterValorSequencialId(MpmAltaCirgRealizada elemento) {
		
		if (elemento == null || elemento.getAltaSumario() == null || elemento.getAltaSumario().getId() == null) {

			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");

		}

		MpmAltaSumarioId idPai = elemento.getAltaSumario().getId();
		int seqp = this.recuperarMaxSeqMpmAltaCirgRealizada(idPai);

		Integer seq = seqp + 1;

		if (elemento.getId() == null) {

			elemento.setId(new MpmAltaCirgRealizadaId());

		}

		elemento.getId().setSeqp(seq.shortValue());
		elemento.getId().setAsuApaAtdSeq(idPai.getApaAtdSeq());
		elemento.getId().setAsuApaSeq(idPai.getApaSeq());
		elemento.getId().setAsuSeqp(idPai.getSeqp());
		
	}
	
	/**
	 * Retorna último seq da tabela
	 * @param id
	 * @return
	 */
	private Integer recuperarMaxSeqMpmAltaCirgRealizada(MpmAltaSumarioId id) {
		
		if (id == null || id.getApaAtdSeq() == null || id.getApaSeq() == null) {
			
			throw new IllegalArgumentException("Parametro invalido!!!");
			
		}
		
		Integer returnValue = null;
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(altaCirgRealizada.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmAltaCirgRealizada.class.getName()).append(" altaCirgRealizada");
		sql.append(" where altaCirgRealizada.").append(MpmAltaCirgRealizada.Fields.ASU_APA_ATD_SEQ.toString()).append(" = :atdSeq ");
		sql.append(" and altaCirgRealizada.").append(MpmAltaCirgRealizada.Fields.ASU_APA_SEQ.toString()).append(" = :apaSeq ");
		sql.append(" and altaCirgRealizada.").append(MpmAltaCirgRealizada.Fields.ASU_SEQP.toString()).append(" = :seqp ");
		
		Query query = createQuery(sql.toString());
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
	 * Verifica se existe registro na alta cirg rzda já cadastrado
	 * @param procEspPorCirurgias
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @return
	 */
	public boolean possuiCirurgiaRealizada(MbcProcEspPorCirurgias procEspPorCirurgias, Integer altanAtdSeq, Integer altanApaSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaCirgRealizada.class);
		criteria.createAlias(MpmAltaCirgRealizada.Fields.ALTASUMARIO.toString(), MpmAltaCirgRealizada.Fields.ALTASUMARIO.toString());
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.DTHR_CIRURGIA.toString(), procEspPorCirurgias.getCirurgia().getData()));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.PROC_ESP_CIR.toString(), procEspPorCirurgias));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.IND_CARGA.toString(), true));
		criteria.add(Restrictions.ne(MpmAltaCirgRealizada.Fields.ALTASUMARIO.toString() + "." + MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
		
		List<MpmAltaCirgRealizada> retorno = executeCriteria(criteria);
		
		if (retorno != null && retorno.size() > 0) {
			
			return true;
			
		}
		
		return false;
	}
	
	/**
	 * Busca alta cirg realizada do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaCirgRealizada> obterMpmAltaCirgRealizada(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return obterMpmAltaCirgRealizada(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
	
	/**
	 * Busca alta cirg realizada do sumário.
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param apenasAtivos
	 * @return
	 */
	public List<MpmAltaCirgRealizada> obterMpmAltaCirgRealizada(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaCirgRealizada.class);
		
		criteria.createAlias("procedimentoEspCirurgia", "PROC_ESP_CIRURGIA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaCirgRealizada.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return executeCriteria(criteria);
	}
	
	public MpmAltaCirgRealizada obterAltaCirgRealizadaOriginalDesatachado(
			MpmAltaCirgRealizada altaCirgRealizada) {
		// Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
		// desatachado.
		this.desatachar(altaCirgRealizada);
		return this.obterPorChavePrimaria(altaCirgRealizada.getId());
	}

	
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaCirgRealizada.Fields.DTHR_CIRURGIA.toString() + "," +
			MpmAltaCirgRealizada.Fields.DESCRICAO.toString() + " , " + 
			MpmAltaCirgRealizada.Fields.ASU_APA_ATD_SEQ.toString() +
		   ") from MpmAltaCirgRealizada " + 
		   " where "+ MpmAltaCirgRealizada.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaCirgRealizada.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaCirgRealizada.Fields.ASU_SEQP.toString() + "=:asuSeqp " +
		   " and " + MpmAltaCirgRealizada.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		Query query = createQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);
		
		return query.getResultList();		
	}	
}
