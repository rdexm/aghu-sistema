package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCadCidNascimentoDAO;
import br.gov.mec.aghu.model.FatCadCidNascimento;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatCadCidNascimentoRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 6594538691101118943L;

	private static final Log LOG = LogFactory.getLog(FatCadCidNascimentoRN.class);

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FatCadCidNascimentoDAO fatCadCidNascimentoDAO;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatCadCidNascimentoExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CID_DUPLICADO
	}

	/**
	 * Persiste ou atualiza a entidade informada.
	 * 
	 * @param fatCadCidNascimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public FatCadCidNascimento gravarCidPorNascimento(FatCadCidNascimento fatCadCidNascimento) throws ApplicationBusinessException {
		
		if (fatCadCidNascimento.getSeq() != null) {
			
			return alterar(fatCadCidNascimento);
			
		} else {
		
			return adicionar(fatCadCidNascimento);
		}
	}

	/**
	 * Método invocado para o caso de inclusão de novo registro.
	 * 
	 * @param fatCadCidNascimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private FatCadCidNascimento adicionar(FatCadCidNascimento fatCadCidNascimento) throws ApplicationBusinessException {
		
		FatCadCidNascimento entity = this.fatCadCidNascimentoDAO.obterFatCadCidNascimento(fatCadCidNascimento);

		if (entity == null) {
			
			fatCadCidNascimento.setCriadoEm(new Date());
			fatCadCidNascimento.setServidor(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
			
			this.fatCadCidNascimentoDAO.persistir(fatCadCidNascimento);
			
		} else {
			
			throw new ApplicationBusinessException(FatCadCidNascimentoExceptionCode.MENSAGEM_CID_DUPLICADO);
		}

		return fatCadCidNascimento;
	}
	
	/**
	 * Método invocado em caso de edição na entidade.
	 * 
	 * @param fatCadCidNascimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private FatCadCidNascimento alterar(FatCadCidNascimento fatCadCidNascimento) throws ApplicationBusinessException {
		
		FatCadCidNascimento entity = this.fatCadCidNascimentoDAO.obterFatCadCidNascimento(fatCadCidNascimento);

		if (entity != null && fatCadCidNascimento.getSeq() != null && !fatCadCidNascimento.getSeq().equals(entity.getSeq())) {
			
			throw new ApplicationBusinessException(FatCadCidNascimentoExceptionCode.MENSAGEM_CID_DUPLICADO);
			
		} else {
			
			fatCadCidNascimento.setAlteradoEm(new Date());
			fatCadCidNascimento.setServidorAltera(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
			
			this.fatCadCidNascimentoDAO.atualizar(fatCadCidNascimento);
		}

		return fatCadCidNascimento;
	}
}
