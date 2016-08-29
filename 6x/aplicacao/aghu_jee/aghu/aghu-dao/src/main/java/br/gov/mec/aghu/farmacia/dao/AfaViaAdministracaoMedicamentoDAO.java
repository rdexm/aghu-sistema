package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;

public class AfaViaAdministracaoMedicamentoDAO extends AbstractMedicamentoDAO<AfaViaAdministracaoMedicamento>{


	private static final long serialVersionUID = 7051493439317539592L;

	@Override 
	protected void obterValorSequencialId(AfaViaAdministracaoMedicamento elemento) {
		AfaViaAdministracaoMedicamentoId id = new AfaViaAdministracaoMedicamentoId();
		id.setMedMatCodigo(elemento.getMedicamento().getMatCodigo());
		id.setVadSigla(elemento.getViaAdministracao().getSigla());
		elemento.setId(id); 
	}
	
	public Boolean verificarSeMedicamentoPossuiViaSiglaDiferente(String sigla, Integer medMatCodigo){
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AfaViaAdministracaoMedicamento.class);
		criteria.add(Restrictions.ne(
		AfaViaAdministracaoMedicamento.Fields.VAD_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(
		AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), medMatCodigo));
		
		List result = executeCriteria(criteria);
		if(result != null && !result.isEmpty()){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * Verifica se uma via está associada a um medicamento. 
	 * @param medMatCodigo
	 * @param sigla
	 * @return true se estiver associada.
	 */
	public Boolean verificarViaAssociadaAoMedicamento(Integer medMatCodigo, String sigla){
		DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracaoMedicamento.class);
		
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), medMatCodigo));
		cri.add(Restrictions.ilike(AfaViaAdministracaoMedicamento.Fields.VAD_SIGLA.toString(), sigla, MatchMode.EXACT));
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaExists(cri);
	}

	/**
	 * Verifica se uma via está associada a um medicamento. 
	 * @param medMatCodigo
	 * @param sigla
	 * @return true se estiver associada.
	 */
	public Boolean verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(Integer medMatCodigo, String sigla){
		DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracaoMedicamento.class);
		
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), medMatCodigo));
		cri.add(Restrictions.ilike(AfaViaAdministracaoMedicamento.Fields.VAD_SIGLA.toString(), sigla, MatchMode.EXACT));
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.DEFAULT_BI.toString(), true));
		
		return executeCriteriaExists(cri);
	}

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaViaAdministracaoMedicamento.class);
		
		criteria.add(Restrictions.eq(
				AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		
		return criteria;
	}
	
	/**
	 * @author gandriotti -- 2011.02.03
	 * @param medicamento
	 * @return
	 */
	public List<AfaViaAdministracaoMedicamento> obterViaAdministracaoAtivas(AfaMedicamento medicamento) {
		
		List<AfaViaAdministracaoMedicamento> result = null;
		DetachedCriteria criteria = null;

		criteria = this.pesquisarCriteria(medicamento);
		criteria.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		result = this.executeCriteria(criteria);
		
		return result;
	}

}
