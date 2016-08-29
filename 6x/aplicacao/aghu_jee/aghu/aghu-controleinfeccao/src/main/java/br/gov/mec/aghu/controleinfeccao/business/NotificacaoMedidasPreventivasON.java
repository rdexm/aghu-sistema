package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoMedidasPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;


@Stateless
public class NotificacaoMedidasPreventivasON extends BaseBusiness {

	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -91113664227909117L;
	private static final Log LOG = LogFactory.getLog(NotificacaoMedidasPreventivasON.class);
	
	@EJB
	private NotificacaoMedidasPreventivasRN notificacaoMedidasPreventivasRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserirNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseListException, BaseException {		
		notificacaoMedidasPreventivasRN.inserirNotificacaoMedidaPreventiva(vo);
	}
	
	public void atualizarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseListException, BaseException {
		notificacaoMedidasPreventivasRN.atualizarNotificacaoMedidaPreventiva(vo);
	}
	
	public void deletarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseException {
		notificacaoMedidasPreventivasRN.deletarNotificacaoMedidaPreventiva(vo);
	}
	
	public List<NotificacaoMedidasPreventivasVO> buscarNotificacoesMedidasPreventivasPorCodigoPaciente(Integer codigoPaciente) throws ApplicationBusinessException  {
		return notificacaoMedidasPreventivasRN.buscarNotificacoesMedidasPreventivasPorCodigoPaciente(codigoPaciente);
	}
	
	public void validarCadastroEdicaoNotificacao(NotificacaoMedidasPreventivasVO vo) throws BaseListException {
		notificacaoMedidasPreventivasRN.validarCadastroEdicaoNotificacao(vo);
	}
	
	public List<DoencaInfeccaoVO> listarInfeccoes(String param) {
		return notificacaoMedidasPreventivasRN.buscarDoencaInfeccao(param);
	}
	
	public Long listarInfeccoesCount(String param) {
		return notificacaoMedidasPreventivasRN.buscarDoencaInfeccaoCount(param);
	}

	public List<OrigemInfeccoesVO> listarOrigemInfeccoes(String strPesquisa) {	
		return notificacaoMedidasPreventivasRN.buscarOrigemInfeccao(strPesquisa);
	}
	
	public Long listarOrigemInfeccoesCount(String strPesquisa) {	
		return notificacaoMedidasPreventivasRN.buscarOrigemInfeccaoCount(strPesquisa);
	}
}
