package br.gov.mec.aghu.controleinfeccao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamRespostaNotifInfeccaoDAO;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghInstituicoesHospitalaresDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.controleinfeccao.business.NotificacaoTopografiaON.NotificacaoTopografiaONExceptionCode;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MbcProcDescricoesId;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MciMvtoInfeccaoTopografiaBuilder extends BaseBusiness {
	
	private static final long serialVersionUID = 410258757697236414L;
	
	private static final Log LOG = LogFactory.getLog(MciMvtoInfeccaoTopografiaBuilder.class);

	@EJB
	public NotificacoesRN notificacoesRN;
	
	@EJB
	public ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	public MciMvtoInfeccaoTopografiaRN infeccaoTopografiaRN;
	
	@EJB
	public IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	public MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	@Inject
	public MciTopografiaProcedimentoDAO mciTopografiaProcedimentoDAO;
	
	@Inject
	public AghAtendimentoDAO atendimentoDAO;
	
	@Inject
	public AipPacientesDAO aipPacientesDAO;
	
	@Inject
	public MamRespostaNotifInfeccaoDAO mamRespostaNotifInfeccaoDAO;
	
	@Inject
	public AghUnidadesFuncionaisDAO unidadesFuncionaisDAO;
	
	@Inject
	public AinQuartosDAO ainQuartosDAO;
	
	@Inject
	public AinLeitosDAO leitosDAO;
	
	@Inject
	public MciEtiologiaInfeccaoDAO etiologiaInfeccaoDAO;
	
	@Inject
	public MciTopografiaProcedimentoDAO topografiaProcedimentoDAO;
	
	@Inject
	public AghInstituicoesHospitalaresDAO instituicoesHospitalaresDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public MciMvtoInfeccaoTopografias construir(NotificacaoTopografiasVO vo) throws ApplicationBusinessException {
		MciMvtoInfeccaoTopografias infeccaoTopografias = new MciMvtoInfeccaoTopografias();
		
		infeccaoTopografias.setSeq(vo.getSeq());
		
		controleInfeccaoRN.notNull(vo.getSeqAtendimento(), NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		infeccaoTopografias.setAtendimento(atendimentoDAO.obterPorChavePrimaria(vo.getSeqAtendimento()));
		
		controleInfeccaoRN.notNull(vo.getCodigoPaciente(), NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		infeccaoTopografias.setPaciente(aipPacientesDAO.obterPorChavePrimaria(vo.getCodigoPaciente()));
		
		controleInfeccaoRN.notNull(vo.getSeqUnidadesFuncional(), NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		infeccaoTopografias.setUnidadeFuncional(unidadesFuncionaisDAO.obterPorChavePrimaria(vo.getSeqUnidadesFuncional()));
		
		controleInfeccaoRN.notNull(vo.getSeqUnidadeFuncionalNotificada(), NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		infeccaoTopografias.setUnidadeFuncionalNotificada(unidadesFuncionaisDAO.obterPorChavePrimaria(vo.getSeqUnidadeFuncionalNotificada()));
		
		validarCodigoEtiologiaInfeccao(vo.getCodigoEtiologiaInfeccao());
		infeccaoTopografias.setEtiologiaInfeccao(etiologiaInfeccaoDAO.obterPorChavePrimaria(vo.getCodigoEtiologiaInfeccao()));
		
		infeccaoTopografias.setQuarto(obterQuarto(vo));
		infeccaoTopografias.setQuartoNotificado(obterQuartoNotificado(vo));
		infeccaoTopografias.setLeito(obterLeito(vo));
		infeccaoTopografias.setLeitoNotificado(obterLeitoNotificado(vo));
		infeccaoTopografias.setTopografiaProcedimento(obterTopografiaProcedimento(vo));
		infeccaoTopografias.setDataInicio(vo.getDataInicio());
		infeccaoTopografias.setInstituicaoHospitalar(obterInstituicaoHospitalar(vo));
		infeccaoTopografias.setRniPnnSeq(vo.getRniPnnSeq());
		infeccaoTopografias.setRniSeqp(vo.getRniSeqp());
		if (vo.getProcedimento() != null) {
			Integer dcgCrgSeq = vo.getProcedimento().getPodDcgCrgSeq();
			Short dcgSeqp = vo.getProcedimento().getPodDcgSeqp();
			Integer seqp = vo.getProcedimento().getPodSeqp();
			MbcProcDescricoesId id = new MbcProcDescricoesId(dcgCrgSeq, dcgSeqp, seqp);
			
			infeccaoTopografias.setProcDescricoes(this.blocoCirurgicoFacade.obterMbcProcDescricoesPorChavePrimaria(id));
		}
		infeccaoTopografias.setIndContaminacao(vo.getPotencialContaminacao());
		
		//confirmação
		controleInfeccaoRN.notNull(vo.getConfirmacaoCci(), NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		infeccaoTopografias.setConfirmacaoCci(vo.getConfirmacaoCci());
		
		// encerramento
		infeccaoTopografias.setDataFim(vo.getDataFim());
		infeccaoTopografias.setMotivoEncerramento(vo.getMotivoEncerramento());
		
		return infeccaoTopografias;
	}

	private void validarCodigoEtiologiaInfeccao(String  codigoEtiologiaInfeccao) throws ApplicationBusinessException {
		controleInfeccaoRN.notNull(codigoEtiologiaInfeccao, NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		if(StringUtils.isBlank(codigoEtiologiaInfeccao)){
			throw new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		}
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

	private MciTopografiaProcedimento obterTopografiaProcedimento(NotificacaoTopografiasVO vo) {
		return vo.getSeqTopografiaProcedimento() != null ? topografiaProcedimentoDAO.obterPorChavePrimaria(vo.getSeqTopografiaProcedimento()) : null;
	}

	private AghInstituicoesHospitalares obterInstituicaoHospitalar(NotificacaoTopografiasVO vo) {
		return vo.getSeqInstituicoaHospitalar() != null ? instituicoesHospitalaresDAO.obterPorChavePrimaria(vo.getSeqInstituicoaHospitalar()): null;
	}

}