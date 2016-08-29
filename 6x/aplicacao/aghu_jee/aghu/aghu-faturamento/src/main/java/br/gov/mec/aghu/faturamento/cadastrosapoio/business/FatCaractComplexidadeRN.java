package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCaractComplexidadeDAO;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatCaractComplexidadeRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5294416189432463363L;
	
	@Inject
	private FatCaractComplexidadeDAO fatCaractComplexidadeDAO;
		
	@EJB
	private IRegistroColaboradorFacade servidor;

	private static final Log LOG = LogFactory.getLog(FatCaractComplexidadeRN.class);

	private enum FatCaractComplexidadeRNExceptionCode implements BusinessExceptionCode {
		M1_RESTRICAO_CARACTERISTICA_COMPLEXIDADE_DUPLICADA;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * ValidaÃ§Ã£o da RN1 - Duplicidade de cÃ³digo no cadastro 
	 * @throws BaseException 
	 */
	private void validarRegistroDuplicado(FatCaractComplexidade entidade) throws BaseException{
		
		FatCaractComplexidade entidadeConsultada = new FatCaractComplexidade(null, entidade.getCodigo(), null, null, null);
		
		List<FatCaractComplexidade> lista = fatCaractComplexidadeDAO.pesquisarCaracteristicasDeComplexidade(0, 1, null, true, entidadeConsultada);
		
		if(lista != null && !lista.isEmpty()){
			throw new ApplicationBusinessException(FatCaractComplexidadeRNExceptionCode.M1_RESTRICAO_CARACTERISTICA_COMPLEXIDADE_DUPLICADA);
		}
	}

	/**
	 * MÃ©todo para gravar o registro cadastrado na base 
	 * 
	 */
	public void persistirCaracteristicasDeComplexidade(final FatCaractComplexidade entidade, Boolean alteracao) throws BaseException {

		if (!alteracao) {
			validarRegistroDuplicado(entidade);
		}
		
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){

			//soh pode persistir se existir servidor
			entidade.setAlteradoEm(new Date()); // Coluna Obrigatoria
			
			if (!alteracao) {
				entidade.setSerMatricula(servidorRecuperado.getId().getMatricula());
				entidade.setSerVinCodigo(servidorRecuperado.getId().getVinCodigo());
				entidade.setCriadoEm(new Date());
				fatCaractComplexidadeDAO.persistir(entidade);
			} else {
				entidade.setSerMatriculaAlterado(servidorRecuperado.getId().getMatricula());
				entidade.setSerVinCodigoAlterado(servidorRecuperado.getId().getVinCodigo());
				fatCaractComplexidadeDAO.atualizar(entidade);
			}
			
		}
	}

	private RapServidores obterServidor() throws ApplicationBusinessException{
		return servidor.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
	}
}