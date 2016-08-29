package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoMaterialAnaliseON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GrupoMaterialAnaliseON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4957104638126784546L;


	public enum GrupoMaterialONExceptionCode implements BusinessExceptionCode {
		CAMPO_OBRIGATORIO;
	}
	
	public AelGrupoMaterialAnalise validarCampos(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException {
		if(grupoMaterialAnalise != null){
			if(StringUtils.isBlank(grupoMaterialAnalise.getDescricao())){
				throw new ApplicationBusinessException(GrupoMaterialONExceptionCode.CAMPO_OBRIGATORIO, "Nome");
			} else{
				grupoMaterialAnalise.setDescricao(StringUtils.trim(grupoMaterialAnalise.getDescricao()));
			}
		}
		return grupoMaterialAnalise;
	}
	
}
