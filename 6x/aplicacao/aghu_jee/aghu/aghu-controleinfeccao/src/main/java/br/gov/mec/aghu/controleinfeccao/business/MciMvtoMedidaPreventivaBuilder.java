package br.gov.mec.aghu.controleinfeccao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MciMvtoMedidaPreventivaBuilder extends BaseBusiness {
	
	private static final long serialVersionUID = 410258757697236414L;
	
	private static final Log LOG = LogFactory.getLog(MciMvtoMedidaPreventivaBuilder.class);
	
	@EJB
	public ControleInfeccaoRN controleInfeccaoRN;

	@Inject
	public AghAtendimentoDAO atendimentoDAO;

	@Inject
	public AghUnidadesFuncionaisDAO unidadesFuncionaisDAO;
	
	@Inject
	public AinQuartosDAO ainQuartosDAO;
	
	@Inject
	public AinLeitosDAO leitosDAO;
	
	@Inject
	public MciEtiologiaInfeccaoDAO etiologiaInfeccaoDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MciMvtoMedidaPreventivaBuilderExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH
	}
	
	public MciMvtoMedidaPreventivas construir(NotificacaoTopografiasVO vo, MciMvtoMedidaPreventivas medidaPreventivas) throws ApplicationBusinessException {

		medidaPreventivas.setDataInicio(vo.getDataInicio());

		medidaPreventivas.setMciEtiologiaInfeccao(etiologiaInfeccaoDAO.obterPorChavePrimaria(vo.getCodigoEtiologiaInfeccao()));
		medidaPreventivas.setAtendimento(atendimentoDAO.obterPorChavePrimaria(vo.getSeqAtendimento()));
		medidaPreventivas.setQuarto(obterQuarto(vo));
		medidaPreventivas.setQuartoNotificado(obterQuartoNotificado(vo));
		medidaPreventivas.setLeito(obterLeito(vo));
		medidaPreventivas.setLeitoNotificado(obterLeitoNotificado(vo));
		
		controleInfeccaoRN.notNull(vo.getSeqUnidadesFuncional(), MciMvtoMedidaPreventivaBuilderExceptionCode.ERRO_PERSISTENCIA_CCIH);
		medidaPreventivas.setUnidadeFuncional(unidadesFuncionaisDAO.obterPorChavePrimaria(vo.getSeqUnidadesFuncional()));
		
		controleInfeccaoRN.notNull(vo.getSeqUnidadeFuncionalNotificada(), MciMvtoMedidaPreventivaBuilderExceptionCode.ERRO_PERSISTENCIA_CCIH);
		medidaPreventivas.setUnidadeFuncionalNotificada(unidadesFuncionaisDAO.obterPorChavePrimaria(vo.getSeqUnidadeFuncionalNotificada()));
		
		//confirmação
		controleInfeccaoRN.notNull(vo.getConfirmacaoCci(), MciMvtoMedidaPreventivaBuilderExceptionCode.ERRO_PERSISTENCIA_CCIH);
		medidaPreventivas.setConfirmacaoCci(vo.getConfirmacaoCci());
		
		// encerramento
		medidaPreventivas.setDataFim(vo.getDataFim());
		medidaPreventivas.setMotivoEncerramento(vo.getMotivoEncerramento());
		
		return medidaPreventivas;
	}

	private AinQuartos obterQuarto(NotificacaoTopografiasVO vo) {
		return vo.getNumeroQuarto() != null ? ainQuartosDAO.obterPorChavePrimaria(vo.getNumeroQuarto()) : null;
	}

	private AinQuartos obterQuartoNotificado(NotificacaoTopografiasVO vo) {
		return vo.getQuartoNotificado() != null ? ainQuartosDAO.obterPorChavePrimaria(vo.getQuartoNotificado()): null;
	}

	private AinLeitos obterLeito(NotificacaoTopografiasVO vo) {
		return vo.getLeito() != null ? leitosDAO.obterPorChavePrimaria(vo.getLeito()) : null;
	}

	private AinLeitos obterLeitoNotificado(NotificacaoTopografiasVO vo) {
		return vo.getLeitoNotificado() != null ? leitosDAO.obterPorChavePrimaria(vo.getLeitoNotificado()) : null;
	}

}