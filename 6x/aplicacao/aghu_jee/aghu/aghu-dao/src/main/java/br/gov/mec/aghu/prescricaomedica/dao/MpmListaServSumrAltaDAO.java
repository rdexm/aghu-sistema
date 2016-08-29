package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmListaServSumrAlta;
import br.gov.mec.aghu.model.MpmListaServSumrAltaId;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @see MpmListaServSumrAlta
 * 
 * @author cvagheti
 * 
 */
public class MpmListaServSumrAltaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmListaServSumrAlta> {

	private static final long serialVersionUID = -2291327068401227078L;

	public List<MpmListaServSumrAlta> listaPesquisaListaSumAltaReimp(Integer firstResult, Integer maxResults,String orderProperty, boolean asc, Integer prontuario, Integer codigo) {
		
		return executeCriteria(this.createPesquisaListaSumAltaReimpCriteria(prontuario, codigo), firstResult, maxResults, orderProperty, asc);
		
	}	
	
	public Long countPesquisaListaSumAltaReimp(Integer prontuario, Integer codigo) {
		return executeCriteriaCount(this.createPesquisaListaSumAltaReimpCriteria(prontuario, codigo));
	}
	
	private DetachedCriteria createPesquisaListaSumAltaReimpCriteria(Integer prontuario, Integer codigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);

		if (prontuario != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(),prontuario));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(),codigo));
		}

		criteria.add(Restrictions.in(AghAtendimentos.Fields.ORIGEM.toString(), 
						new DominioOrigemAtendimento[]{ DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.N })
					);
		
		criteria.add(
				Restrictions.or(Restrictions.eq(AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA.toString(), DominioSituacaoSumarioAlta.I), Restrictions.isNull(AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA.toString()))
					);

		criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.N));
		
		criteria.add(	
				Restrictions.or(
						Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N), 
						Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.INT_SEQ.toString()), 
								Restrictions.or(Restrictions.isNotNull(AghAtendimentos.Fields.ATU_SEQ.toString()), Restrictions.isNotNull(AghAtendimentos.Fields.HOSPITAL_DIA_SEQ.toString()))))
					);
		
		return criteria;
	}
	
	/**
	 * Método para inserir um objeto com relação aos objetos servidor, atendimento e com a informação criadoEm
	 * 
	 * @param mpmListaServSumrAltaId
	 */
	public void inserirListaServidorSumarioAlta(MpmListaServSumrAltaId id) {
		MpmListaServSumrAlta listaServidorSumarioAlta = new MpmListaServSumrAlta();
		listaServidorSumarioAlta.setCriadoEm(new Date());
		listaServidorSumarioAlta.setId(id);
		
		this.persistir(listaServidorSumarioAlta);
	}
	
	/**
	 * Método para remover todos objetos de lista servidor sumário alta através
	 * do SEQ do atendimento
	 * 
	 * @param seqAtendimento
	 */
	public void removerListaServidorSumarioAltaPorAtendimento(
			Integer seqAtendimento) {
		String hql = "delete from MpmListaServSumrAlta where atendimento.seq = "
				+ seqAtendimento;

		this.createQuery(hql).executeUpdate();
	}
	
	/**
	 * Método para pesquisar lista de MpmPacAtendProfissional pelo atendimento do servidor.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmListaServSumrAlta> pesquisarSumariosPendentesPorAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmListaServSumrAlta.class);

		criteria.add(Restrictions.eq(MpmListaServSumrAlta.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));

		return this.executeCriteria(criteria);
	}
}