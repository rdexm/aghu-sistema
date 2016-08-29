package br.gov.mec.aghu.compras.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceFornecedorMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterFornecedorMaterialRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterFornecedorMaterialRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3731094598255387121L;

	public enum ManterMaterialFornecedorRNExceptionCode implements BusinessExceptionCode {
		USUARIO_NAO_SERVIDOR;
	}

	/**
	 * ORADB Trigger SCET_FMT_BRI
	 * 
	 * @param fornecedorMaterial
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesInserirFornecedorMaterial(SceFornecedorMaterial fornecedorMaterial) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fornecedorMaterial.setServidor(servidorLogado);
		fornecedorMaterial.setDataGeracao(new Date());
		
		//if :new.ser_matricula  is null then raise_application_error(-20000, 'Usuário não cadastrado como servidor');
		if(servidorLogado.getId().getMatricula() == null){
			throw new ApplicationBusinessException(ManterMaterialFornecedorRNExceptionCode.USUARIO_NAO_SERVIDOR);
		}
	}
	
	/**
	 * ORADB Trigger SCET_FMT_BRU
	 * 
	 * @param fornecedorMaterial
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesAtualizarFornecedorMaterial(SceFornecedorMaterial fornecedorMaterial) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fornecedorMaterial.setServidor(servidorLogado);
		fornecedorMaterial.setDataGeracao(new Date());
		
		//if :new.ser_matricula  is null then raise_application_error(-20000, 'Usuário não cadastrado como servidor');
		if(servidorLogado.getId().getMatricula() == null){
			throw new ApplicationBusinessException(ManterMaterialFornecedorRNExceptionCode.USUARIO_NAO_SERVIDOR);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
