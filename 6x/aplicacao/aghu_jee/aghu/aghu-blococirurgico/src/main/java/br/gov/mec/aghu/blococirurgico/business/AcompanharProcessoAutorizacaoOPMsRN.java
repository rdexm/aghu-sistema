package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AcompanharProcessoAutorizacaoOPMsRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AcompanharProcessoAutorizacaoOPMsRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4482610666030602519L;
	
	protected enum AcompanharProcessoAutorizacaoOPMsRNExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_USUARIO_SEM_PERMISSAO_01, 
		MSG_ERRO_USUARIO_SEM_PERMISSAO_02,
		MSG_ERRO_USUARIO_SEM_PERMISSAO_03,
		MSG_ERRO_USUARIO_SEM_PERMISSAO_04;
	}

	// #35482 - RN05_ACAO_PROC
	public MbcAgendas validarUsuarioSituacaoAjustarProcedimento(RequisicoesOPMEsProcedimentosVinculadosVO consAjusProcItem, String usuarioLogado) throws ApplicationBusinessException{
		//			RapServidores usuario = this.obterUsuarioLogado(usuarioLogado);
		MbcAgendas agenda = mbcRequisicaoOpmesDAO.verificaTelaMedicoAdm(consAjusProcItem.getRequisicaoSeq());
		
		if (agenda != null) {
			mbcRequisicaoOpmesDAO.initialize(agenda.getSalaCirurgica());
		}
		
		return agenda;
	}
		
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	
}
