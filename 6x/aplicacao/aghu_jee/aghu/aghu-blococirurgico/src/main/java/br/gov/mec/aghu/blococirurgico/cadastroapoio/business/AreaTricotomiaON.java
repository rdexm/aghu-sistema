package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;



import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas a Área de Tricotomia.
 * 
 * @author dpacheco
 * 
 */
@Stateless
public class AreaTricotomiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AreaTricotomiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -2820476506810246218L;

	public enum AreaTricotomiaONExceptionCode implements BusinessExceptionCode {
		ERRO_AREA_TRICOTOMIA_DESCRICAO_VAZIA
	}

	public MbcAreaTricotomia obterNovaAreaTricotomia(String descricao) {
		MbcAreaTricotomia areaTricotomia = new MbcAreaTricotomia();
		areaTricotomia.setDescricao(descricao);
		areaTricotomia.setSituacao(DominioSituacao.A);
		return areaTricotomia;
	}
	
	public void validarPreenchimentoDescricao(String descricao)
			throws ApplicationBusinessException {
		// Aqui não é validado quando há somente espaços em branco, 
		// pois esse tratamento é feito pelo input na view.
		if (StringUtils.isEmpty(descricao)) {
			throw new ApplicationBusinessException(
					AreaTricotomiaONExceptionCode.ERRO_AREA_TRICOTOMIA_DESCRICAO_VAZIA);
		}
	}

}
