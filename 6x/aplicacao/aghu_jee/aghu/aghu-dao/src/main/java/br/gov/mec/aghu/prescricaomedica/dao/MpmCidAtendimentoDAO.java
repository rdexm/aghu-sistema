package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.CidAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;

public class MpmCidAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmCidAtendimento> {

	private static final long serialVersionUID = 8994749473264428614L;

	/**
	 * Lista todos os diagnósticos correspondentes ao atendimento.
	 * 
	 * @param atendimento
	 *            Atendimento vigente.
	 * @return Lista de diagnósticos encontrados.
	 */
	public List<MpmCidAtendimento> listar(AghAtendimentos atendimento) {

		if (atendimento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCidAtendimento.class);
		criteria.setFetchMode(MpmCidAtendimento.Fields.CID.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(
				MpmCidAtendimento.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.isNull(MpmCidAtendimento.Fields.DTHR_FIM
				.toString()));

		return executeCriteria(criteria);
	}
	
	
	
	/**
	 * Lista todos os diagnósticos correspondentes ao atendimento.
	 * 
	 * @param atendimento
	 *            Atendimento vigente.
	 * @return Lista de diagnósticos encontrados.
	 */
	public Long contarDiagnosticosAtendimento(AghAtendimentos atendimento) {

		if (atendimento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCidAtendimento.class);
		criteria.add(Restrictions.eq(
				MpmCidAtendimento.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.isNull(MpmCidAtendimento.Fields.DTHR_FIM
				.toString()));

		return executeCriteriaCount(criteria);
	}
	
	
	public List<MpmCidAtendimento> listarCidAtendimentosPorAtendimento(
			AghAtendimentos atendimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCidAtendimento.class);

		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.ATENDIMENTO
				.toString(), atendimento));

		criteria.add(Restrictions.or(Restrictions
				.isNull(MpmCidAtendimento.Fields.INC_CID_PACIENTE.toString()),
				Restrictions.eq(MpmCidAtendimento.Fields.ALT_CID_PACIENTE
						.toString(), false)));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Lista MpmCidAtendimento que possuam DTHR_FIM nula, IND_PRINCIPAL_ALTA 'S' e filtrando pelo seqAtendimento. 
	 * @param seq
	 * @return List<MpmCidAtendimento>
	 */
	public List<MpmCidAtendimento> listarCidAtendimentosPorSeqAtendimento(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class);
		
		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.ATD_SEQ.toString(), seq));
		
		criteria.add(Restrictions.isNull(MpmCidAtendimento.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.IND_PRINCIPAL_ALTA.toString(), true));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Lista o histórico de diagnosticos para um atendimento
	 * @param atendimento
	 * @return
	 */
	public List<MpmCidAtendimento> listarHistorico(AghAtendimentos atendimento) {
		String aliasSer = "ser";
		String aliasSerMov = "sermov";
		String ponto = ".";
		
		if (atendimento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class);
		criteria.createAlias(MpmCidAtendimento.Fields.CID.toString(), "cid", JoinType.INNER_JOIN);
		criteria.createAlias(MpmCidAtendimento.Fields.SERVIDOR.toString(), aliasSer, JoinType.INNER_JOIN);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA, "pes", JoinType.INNER_JOIN);
		criteria.createAlias(MpmCidAtendimento.Fields.SERVIDOR_MOVIMENTADO.toString(), aliasSerMov, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasSerMov + ponto + RapServidores.Fields.PESSOA_FISICA, "pesmov", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.addOrder(Order.asc(MpmCidAtendimento.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc(MpmCidAtendimento.Fields.DTHR_FIM.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Obtém cid atendimento pelo seu ID.
	 * 
	 * bsoliveira 29/10/2010
	 * 
	 * @param {Integer} seq
	 * 
	 * @return MpmCidAtendimento
	 */
	public MpmCidAtendimento obterCidAtendimentoPeloId(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCidAtendimento.class);
		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.SEQ.toString(),
				seq));
		MpmCidAtendimento retorno = (MpmCidAtendimento) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;

	}
	
	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class, "CIA");
		criteria.createAlias(MpmCidAtendimento.Fields.ATENDIMENTO.toString(),"ATD");
		criteria.createAlias(MpmCidAtendimento.Fields.CID.toString(),"CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.CID.toString()), "cid");
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.COMPLEMENTO.toString()), "complemento");
		criteria.setProjection(pList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return executeCriteria(criteria);
	}

	public MpmCidAtendimento pesquisaCidAtendimentoPrincipal(AghAtendimentos atendimento) {
		DetachedCriteria cri = DetachedCriteria.forClass(MpmCidAtendimento.class);

		cri.add(Restrictions.eq(MpmCidAtendimento.Fields.ATENDIMENTO.toString(), atendimento));
		cri.add(Restrictions.eq(MpmCidAtendimento.Fields.IND_PRINCIPAL_ALTA.toString(), true));

		return (MpmCidAtendimento) executeCriteriaUniqueResult(cri);
	}
	
	public List<CidAtendimentoVO> pesquisarCidAtendimentoEmAndamentoOrigemInternacaoPorPacCodigo(Integer pacCodigo) {
		String aliasAtd = "atd";
		String aliasCia = "cia";
		String aliasCid = "cid";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class, aliasCia);
		
		criteria.createAlias(aliasCia + separador + MpmCidAtendimento.Fields.ATENDIMENTO.toString(), aliasAtd);
		criteria.createAlias(aliasCia + separador + MpmCidAtendimento.Fields.CID.toString(), aliasCid);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(aliasAtd + separador + AghAtendimentos.Fields.SEQ.toString()), CidAtendimentoVO.Fields.ATD_SEQ.toString())
				.add(Projections.property(aliasAtd + separador + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()), CidAtendimentoVO.Fields.IND_PAC_ATENDIMENTO.toString())
				.add(Projections.property(aliasAtd + separador + AghAtendimentos.Fields.ORIGEM.toString()), CidAtendimentoVO.Fields.ORIGEM.toString())
				.add(Projections.property(aliasCia + separador + MpmCidAtendimento.Fields.CID_SEQ.toString()), CidAtendimentoVO.Fields.CID_SEQ.toString())
				));
		
		criteria.add(Restrictions.eq(aliasAtd + separador + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(aliasAtd + separador + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq(aliasAtd + separador + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq(aliasCid + separador + AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CidAtendimentoVO.class));
		
		return executeCriteria(criteria);		
	}

	public List<Integer> listarSeqsCidAtendimentoPorCodigoAtendimento(Integer newAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class);
		criteria.add(Restrictions.eq(MpmCidAtendimento.Fields.ATD_SEQ.toString(), newAtdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmCidAtendimento.Fields.SEQ.toString()));
		criteria.setProjection(p);

		return executeCriteria(criteria);
	}
	
	
	public List<MpmCidAtendimento> listarCidAtendimentoPorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class, "MCA");
		criteria.createAlias("MCA." + MpmCidAtendimento.Fields.CID.toString(), "CID"); 
		criteria.createAlias("CID." + AghCid.Fields.CID.toString(), "CID1", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("MCA." + MpmCidAtendimento.Fields.ATD_SEQ.toString(), atdSeq));

		return executeCriteria(criteria);
	}
	
	public List<MpmCidAtendimento> listarCidAtendimentosValidosPorAtdSeq(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class, "CIA");

		criteria.createAlias("CIA." + MpmCidAtendimento.Fields.CID.toString(), "CID"); 
		criteria.createAlias("CID." + AghCid.Fields.CID.toString(), "CIDP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("CIA." + MpmCidAtendimento.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull("CIA." + MpmCidAtendimento.Fields.DTHR_FIM.toString()));
		
		return executeCriteria(criteria);
	}

}
