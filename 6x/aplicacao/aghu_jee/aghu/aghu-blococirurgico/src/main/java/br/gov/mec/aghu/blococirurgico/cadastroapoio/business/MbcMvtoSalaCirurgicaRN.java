package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaCirurgicaDAO;
import br.gov.mec.aghu.model.MbcMvtoSalaCirurgica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcMvtoSalaCirurgicaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcMvtoSalaCirurgicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMvtoSalaCirurgicaDAO mbcMvtoSalaCirurgicaDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5534334219937594711L;

	/**
	 * 
	 */
	

	public enum MbcMvtoSalaCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MBC_01279; 
	}

	/**@throws BaseException 
	 * @ORADB MBCT_MSC_BRI
	 * 
	 */
	private void preInserir(MbcMvtoSalaCirurgica salaCirurgica, String obterLoginUsuarioLogado) throws BaseException  {
		salaCirurgica.setCriadoEm(new Date());
		salaCirurgica.setRapServidoresByMbcMscSerFk1(getRegistroColaboradorFacade().obterServidorPorUsuario(obterLoginUsuarioLogado));
	}
	
	private IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	/**@throws BaseException 
	 * @ORADB MBCT_MSC_BRU
	 * 
	 */
	private void preAtualizar(MbcMvtoSalaCirurgica salaCirurgica, String obterLoginUsuarioLogado) throws BaseException  {
		salaCirurgica.setAlteradoEm(new Date());
		salaCirurgica.setRapServidoresByMbcMscSerFk2(getRegistroColaboradorFacade().obterServidorPorUsuario(obterLoginUsuarioLogado));
		verificarDadosAtualizados(salaCirurgica);
	}

	private void verificarDadosAtualizados(MbcMvtoSalaCirurgica salaCirurgica) throws BaseException {
		MbcMvtoSalaCirurgica salaCirurgicaOriginal = getMbcMvtoSalaCirurgicaDAO().obterOriginal(salaCirurgica);

		if (CoreUtil.modificados(salaCirurgica.getMbcSalaCirurgica(),salaCirurgicaOriginal.getMbcSalaCirurgica())
				|| CoreUtil.modificados(salaCirurgica.getSituacao(), salaCirurgicaOriginal.getSituacao())
				|| CoreUtil.modificados(salaCirurgica.getIndEspecial(), salaCirurgicaOriginal.getIndEspecial())
				|| CoreUtil.modificados(salaCirurgica.getNome(), salaCirurgicaOriginal.getNome())
				|| CoreUtil.modificados(salaCirurgica.getMotivoInat(), salaCirurgicaOriginal.getMotivoInat())) {
			throw new ApplicationBusinessException(MbcMvtoSalaCirurgicaRNExceptionCode.MBC_01279);
		}


	}

	public void persistirMbcMvtoSalaCirurgica(MbcMvtoSalaCirurgica mbcSalaCirurgica) throws BaseException {
		if (mbcSalaCirurgica.getSeq() == null) {
			inserir(mbcSalaCirurgica);
		} else {
			atualizar(mbcSalaCirurgica);
		}
	}
	
	public void inserir(MbcMvtoSalaCirurgica salaCirurgica) throws BaseException {
		preInserir(salaCirurgica, servidorLogadoFacade.obterServidorLogado().getUsuario());
		this.getMbcMvtoSalaCirurgicaDAO().persistir(salaCirurgica);
	}
	

	
	private MbcMvtoSalaCirurgicaDAO getMbcMvtoSalaCirurgicaDAO() {
		return mbcMvtoSalaCirurgicaDAO;
	}

	public void atualizar(MbcMvtoSalaCirurgica salaCirurgica) throws BaseException {
		preAtualizar(salaCirurgica, servidorLogadoFacade.obterServidorLogado().getUsuario());
		this.getMbcMvtoSalaCirurgicaDAO().atualizar(salaCirurgica);
	}
}
