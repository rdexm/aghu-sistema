package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@Stateless
public class FatCaractFinanciamentoRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8985628714046073972L;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade servidor;

	private static final Log LOG = LogFactory.getLog(FatCaractFinanciamentoRN.class);

	private enum FatCaractFinanciamentoRNExceptionCode implements BusinessExceptionCode {

		M2_RESTRICAO_INCLUSAO_FAT_CARACT_DUPLICADA;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Validação da RN3 - Duplicidade de código no cadastro 
	 * @throws BaseException 
	 */
	public void validarRegistroDuplicado(String codigo) throws BaseException{
		FatCaractFinanciamento fatCaract = fatCaractFinanciamentoDAO.pesquisarCodigoCaracteristicaFinanciamentoDuplicado(codigo);
		
		if(fatCaract != null){
			throw new ApplicationBusinessException(FatCaractFinanciamentoRNExceptionCode.M2_RESTRICAO_INCLUSAO_FAT_CARACT_DUPLICADA);
		}
	}

	/**
	 * Método para gravar o registro cadastrado na base 
	 * 
	 */
	public void persistir(final FatCaractFinanciamento entidade) throws BaseException {
		validarRegistroDuplicado(entidade.getCodigo());
		
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){
			entidade.setSerMatricula(servidorRecuperado.getId().getMatricula());
			entidade.setSerVinCodigo(servidorRecuperado.getId().getVinCodigo());

			//só pode persistir se existir servidor
			entidade.setCriadoEm(new Date());
			entidade.setAlteradoEm(new Date()); //campo obrigatório na tabela
			fatCaractFinanciamentoDAO.persistir(entidade);		
		}
	}
	public void alterar(final FatCaractFinanciamento entidade) throws ApplicationBusinessException{
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){
			entidade.setSerMatricula(servidorRecuperado.getId().getMatricula());
			entidade.setSerVinCodigo(servidorRecuperado.getId().getVinCodigo());

			//só pode persistir se existir servidor
			entidade.setCriadoEm(new Date());
			entidade.setAlteradoEm(new Date()); //campo obrigatório na tabela
			fatCaractFinanciamentoDAO.merge(entidade);		
		}
	}

	/**
	 * Método para atualizar a situação da entidade na base 
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void ativarDesativarCaracteristica(FatCaractFinanciamento entidade) throws ApplicationBusinessException {
		
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){
			entidade.setSerMatriculaAlterado(servidorRecuperado.getId().getMatricula());
			entidade.setSerVinCodigoAlterado(servidorRecuperado.getId().getVinCodigo());
			
			//só persiste a alteração caso exista servidor vinculado
			entidade.setAlteradoEm(new Date());
			fatCaractFinanciamentoDAO.atualizar(entidade);
		}

	}
	
	private RapServidores obterServidor() throws ApplicationBusinessException{
		return servidor.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
	}
}