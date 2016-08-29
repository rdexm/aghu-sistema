package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExameEquipamento;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExecExamesMatAnaliseId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.BaseException;

public class AelExecExamesMatAnaliseDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExecExamesMatAnalise> {


	private static final long serialVersionUID = -450153305454027031L;

	public AelExecExamesMatAnalise buscarAelExecExamesMatAnaliseComEquipamentoPorAelItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao, DominioProgramacaoExecExames programacao) {
		if (itemSolicitacao == null || itemSolicitacao.getExame() == null
				|| itemSolicitacao.getMaterialAnalise() == null
				|| programacao == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExecExamesMatAnalise.class);
		criteria.createAlias(AelExecExamesMatAnalise.Fields.EQUIPAMENTOS.toString(),"EQU");
		
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_EXA_SIGLA.toString(), itemSolicitacao.getExame().getSigla()));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_MAN_SEQ.toString(), itemSolicitacao.getMaterialAnalise().getSeq()));
		criteria.add(Restrictions.in(AelExecExamesMatAnalise.Fields.PROGRAMACAO.toString(), new Object[]{programacao,DominioProgramacaoExecExames.A} ));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelEquipamentos.Fields.SITUACAO.toString(), DominioSituacao.A));

		
		List<AelExecExamesMatAnalise> retorno = executeCriteria(criteria);
		
		if (!retorno.isEmpty()) {
			
			return retorno.get(0);
			
		}
		
		return null;
	}
	
	/**
	 * Pesquisa AelExecExamesMatAnalise por AelExamesMaterialAnalise
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public List<AelExecExamesMatAnalise> pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(AelExamesMaterialAnaliseId id, Short ordem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExecExamesMatAnalise.class);

		criteria.createAlias(AelExecExamesMatAnalise.Fields.EQUIPAMENTOS.toString(), "EQU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExecExamesMatAnalise.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_MAN_SEQ.toString(), id.getManSeq()));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_EXA_SIGLA.toString(), id.getExaSigla()));
		if (ordem != null) {
			criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.ORDEM.toString(), ordem));
		}
		
		return executeCriteria(criteria);

	}
	

	/**
	 * Pesquisar Dependência em AelExameEquipamento
	 * @param id
	 * @return
	 * @throws BaseException
	 */
	public Boolean pesquisarDependenciaAelExameEquipamento(AelExecExamesMatAnaliseId id) throws BaseException{
	
		Boolean retorno = Boolean.FALSE;

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameEquipamento.class);
		
		criteria.add(Restrictions.eq(AelExameEquipamento.Fields.EEM_EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExameEquipamento.Fields.EEM_EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelExameEquipamento.Fields.EEM_PROGRAMACAO.toString(), id.getProgramacao()));
		criteria.add(Restrictions.eq(AelExameEquipamento.Fields.EEM_EQU_SEQ.toString(), id.getEquSeq()));
		
		List<AelExameEquipamento> listExameEquipamento = executeCriteria(criteria);
		
		if(listExameEquipamento!=null && !listExameEquipamento.isEmpty()){
		
			retorno = Boolean.TRUE;
			
		}
		
		
		return retorno;
		
		
	}
	
	/**
	 * Pesquisar Dependência em AelExameEquipamento
	 * @param id
	 * @return
	 * @throws BaseException
	 */
	public Boolean verificaAelExecExamesMatAnaliseCadastrado(AelExecExamesMatAnaliseId id) throws BaseException{
	
		Boolean retorno = Boolean.FALSE;

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExecExamesMatAnalise.class);
		
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.PROGRAMACAO.toString(), id.getProgramacao()));
		criteria.add(Restrictions.eq(AelExecExamesMatAnalise.Fields.EQUIPAMENTOS_SEQ.toString(), id.getEquSeq()));
		
		List<AelExecExamesMatAnalise> listExameEquipamento = executeCriteria(criteria);
		
		if(listExameEquipamento!=null && !listExameEquipamento.isEmpty()){
		
			retorno = Boolean.TRUE;
			
		}
		
		return retorno;
		
	}

}
