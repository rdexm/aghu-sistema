package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAgrupamentoMaterialDAO;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoAgrupamentoMaterialRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoAgrupamentoMaterialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoAgrupamentoMaterialDAO scoAgrupamentoMaterialDAO;
	
	private static final long serialVersionUID = 8819019283260364920L;

	public enum ScoAgrupamentoMaterialRNExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG, MENSAGEM_AGRUP_MATERIAL_RN1; }

	public void persistir(ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException {

		if (scoAgrupMaterial == null) {
			throw new ApplicationBusinessException(ScoAgrupamentoMaterialRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN01 - Não pode existir mais de um agrupamento com a mesma descrição
		ScoAgrupamentoMaterial achouRegistro = getScoAgrupamentoMaterialDAO()
				.pesquisarAgrupamentoMaterialPorDescricao(scoAgrupMaterial.getDescricao());
		
		if (achouRegistro != null && achouRegistro.getCodigo() != null) {
				throw new ApplicationBusinessException(ScoAgrupamentoMaterialRNExceptionCode.MENSAGEM_AGRUP_MATERIAL_RN1);
		}
		
		this.getScoAgrupamentoMaterialDAO().persistir(scoAgrupMaterial);
	}

	public void atualizar(ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException {

		if (scoAgrupMaterial == null) {
			throw new ApplicationBusinessException(ScoAgrupamentoMaterialRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN01 - Não pode existir mais de um agrupamento com a mesma descrição
		ScoAgrupamentoMaterial achouRegistro = getScoAgrupamentoMaterialDAO()
				.pesquisarAgrupamentoMaterialPorDescricao(scoAgrupMaterial.getDescricao());
		
		if (achouRegistro != null) {
			if (!achouRegistro.getCodigo().equals(scoAgrupMaterial.getCodigo())) {
				throw new ApplicationBusinessException(ScoAgrupamentoMaterialRNExceptionCode.MENSAGEM_AGRUP_MATERIAL_RN1);
			}
		}
		
		this.getScoAgrupamentoMaterialDAO().merge(scoAgrupMaterial);
	}
	
	private ScoAgrupamentoMaterialDAO getScoAgrupamentoMaterialDAO() {
		return scoAgrupamentoMaterialDAO;
	}
}