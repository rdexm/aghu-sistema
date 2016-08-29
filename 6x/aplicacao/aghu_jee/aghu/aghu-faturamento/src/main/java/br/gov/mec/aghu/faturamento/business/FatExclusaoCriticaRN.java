package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatExclusaoCriticaDAO;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatExclusaoCriticaRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4834030068483176375L;

	@Inject
	private FatExclusaoCriticaDAO fatExclusaoCriticaDAO;
	
	@EJB
	private IRegistroColaboradorFacade servidor;

	private static final Log LOG = LogFactory.getLog(FatExclusaoCriticaRN.class);

	private enum FatExclusaoCriticaRNExceptionCode implements BusinessExceptionCode {
		M2_RESTRICAO_INCLUSAO_CARACTERISTICA_DUPLICADA,
		M3_RESTRICAO_INCLUSAO_NAO_SUCEDIDA;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Validação da RN1 - Duplicidade de código no cadastro 
	 * @throws BaseException 
	 */
	public void validarRegistroDuplicado(FatExclusaoCritica filtro) throws BaseException{
		FatExclusaoCritica faExclusaoCritica = fatExclusaoCriticaDAO.pesquisarCodigoExclusaoCriticaDuplicado(filtro);
		if(faExclusaoCritica != null){
			throw new ApplicationBusinessException(FatExclusaoCriticaRNExceptionCode.M2_RESTRICAO_INCLUSAO_CARACTERISTICA_DUPLICADA, faExclusaoCritica.getCodigo());
		}
	}

	/**
	 * Método para gravar o registro cadastrado na base 
	 * 
	 */
	public void persistir(final FatExclusaoCritica fatExclusaoCritica) throws BaseException {
		validarRegistroDuplicado(fatExclusaoCritica);
		if(StringUtils.isNotBlank(fatExclusaoCritica.getCodigo())){
			fatExclusaoCriticaDAO.persistir(fatExclusaoCritica);		
		} else {
			throw new ApplicationBusinessException(FatExclusaoCriticaRNExceptionCode.M3_RESTRICAO_INCLUSAO_NAO_SUCEDIDA);
		}
	}
	
	/**
	 * Método para alterar o registro cadastrado na base 
	 * 
	 */
	public void alterar(final FatExclusaoCritica fatExclusaoCritica) throws BaseException {
			
		FatExclusaoCritica entity = fatExclusaoCriticaDAO.obterFatExclusaoCritica(fatExclusaoCritica);
		
		if (entity != null && fatExclusaoCritica.getSeq() != null && !fatExclusaoCritica.getSeq().equals(entity.getSeq())) {		
			throw new ApplicationBusinessException(FatExclusaoCriticaRNExceptionCode.M2_RESTRICAO_INCLUSAO_CARACTERISTICA_DUPLICADA, fatExclusaoCritica.getCodigo());		
		} else {
				fatExclusaoCriticaDAO.merge(fatExclusaoCritica);
		}
	}
		

	/**
	 * @author marcelo.deus
	 * @throws ApplicationBusinessException 
	 * Método para atualizar a situação da entidade na base 
	 */
	public void ativarDesativarSituacao(FatExclusaoCritica fatExclusaoCritica) throws ApplicationBusinessException {
		
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){
			fatExclusaoCriticaDAO.atualizar(fatExclusaoCritica);
		}

	}
	
	private RapServidores obterServidor() throws ApplicationBusinessException{
		return servidor.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
	}
	
	public void removerExclusaoCritica(final Short seq) throws BaseException {
			FatExclusaoCritica fatExclusaoCritica = this.fatExclusaoCriticaDAO.obterPorChavePrimaria(seq);
			this.fatExclusaoCriticaDAO.remover(fatExclusaoCritica);
	}

}
