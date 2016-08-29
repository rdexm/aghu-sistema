package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmoExameConvId;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;


public class AelTipoAmoExameConvDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTipoAmoExameConv> {

	
	private static final long serialVersionUID = -3562429851711323941L;


	@Override
	protected void obterValorSequencialId(AelTipoAmoExameConv elemento) {
		if (elemento == null 
				|| (elemento.getTipoAmostraExame() == null 
						&& elemento.getConvSaudePlanos() == null)) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelTipoAmoExameConvId id = new AelTipoAmoExameConvId();
		id.setTaeEmaExaSigla(elemento.getTipoAmostraExame().getId().getEmaExaSigla());
		id.setTaeEmaManSeq(elemento.getTipoAmostraExame().getId().getEmaManSeq());
		id.setTaeManSeq(elemento.getTipoAmostraExame().getId().getManSeq());
		id.setTaeOrigemAtendimento(elemento.getTipoAmostraExame().getId().getOrigemAtendimento());
		id.setCspCnvCodigo(elemento.getConvSaudePlanos().getId().getCnvCodigo());
		id.setCspSeq(elemento.getConvSaudePlanos().getId().getSeq().shortValue());
		
		elemento.setId(id);
	}
	
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTipoAmoExameConv.class);
		return criteria;
    }
	
	/**
	 * Método que busca qual o responsável da coleta de um exame que tem origem atendimento conv igual á origem informada na tela ou 'T'.
	 * cursor c_amostra_exame_conv da pll AELP_VERIFICA_RESPONSAVEL_COLETA.
	 * @param id Id do tipo conv
	 * @return amostra de exame convenio com os valores carregados.
	 */
	public List<AelTipoAmoExameConv> obterAelTipoAmoExameConvPorID(AelTipoAmoExameConvId id) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_EXA_SIGLA.toString(), id.getTaeEmaExaSigla()));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_MAN_SEQ.toString(), id.getTaeEmaManSeq()));
		criteria.add(Restrictions.or(
					Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_ORIGEM_ATEND.toString(), id.getTaeOrigemAtendimento()), 
					Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_ORIGEM_ATEND.toString(), DominioOrigemAtendimento.T)));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.CSP_CNV_COD.toString(), id.getCspCnvCodigo()));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.CSP_SEQ.toString(), id.getCspSeq()));
		
		return executeCriteria(criteria);
	}

	public List<AelTipoAmoExameConv> obterAelTipoAmoExameConv(AelTipoAmoExameConvId id) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_EXA_SIGLA.toString(), id.getTaeEmaExaSigla()));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_MAN_SEQ.toString(), id.getTaeEmaManSeq()));
		criteria.add(Restrictions.or(
					Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_ORIGEM_ATEND.toString(), id.getTaeOrigemAtendimento()), 
					Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_ORIGEM_ATEND.toString(), DominioOrigemAtendimento.T)));
		
		return executeCriteria(criteria);
	}

	public List<AelTipoAmoExameConv> listarAelTipoAmoExameConvPorTipoAmostraExame(String emaExaSigla, Integer emaManSeq,
			Integer manSeq, DominioOrigemAtendimento origemAtendimento) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelTipoAmoExameConv.Fields.CONVENIO_SAUDE_PLANO.toString(), 
				"CNV", Criteria.LEFT_JOIN);
		criteria.createCriteria("CNV.".concat(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()),
				Criteria.LEFT_JOIN);
		
		criteria = this.obterCriterioConsulta(criteria, emaExaSigla, emaManSeq, manSeq, origemAtendimento);
		
		return this.executeCriteria(criteria);
	}
	
	
	
	private DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, 
			String emaExaSigla, Integer emaManSeq, Integer manSeq, DominioOrigemAtendimento origemAtendimento) {
		
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.TAE_ORIGEM_ATEND.toString(), origemAtendimento));
		
		return criteria;
	}
	
	
	public Boolean verificarAelTipoAmoExameConvExistente(String emaExaSigla, Integer emaManSeq,
			Integer manSeq, DominioOrigemAtendimento origemAtendimento, Short cspCnvCodigo, Short cspSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		
		criteria = this.obterCriterioConsulta(criteria, emaExaSigla, emaManSeq, manSeq, origemAtendimento);
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.CSP_CNV_COD.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(AelTipoAmoExameConv.Fields.CSP_SEQ.toString(), cspSeq));
		
		List<AelTipoAmoExameConv> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
}