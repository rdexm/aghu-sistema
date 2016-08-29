package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmSalaRecuperacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PacientesEmSalaRecuperacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PacientesEmSalaRecuperacaoON.class);	

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4846091097177869676L;
	
	private static final String PREFIXO_CIRURGIA = "CRG.";
	private static final String PREFIXO_PACIENTE = "AIP.";
	
	public List<PacientesEmSalaRecuperacaoVO> listarPacientesEmSR(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr) throws ApplicationBusinessException {
		List<PacientesEmSalaRecuperacaoVO> listaPacientesEMSR = new ArrayList<PacientesEmSalaRecuperacaoVO>();
		Date dataAtual = DateUtil.truncaData(new Date());
		Date dataAnterior = recuperarDataAnterior(dataAtual);
		List<MbcCirurgias> listaCirurgiasPacientesEmSalaRecuperacao = null;
		boolean ordernarJava = isPropriedadeRecuperadaPorOutraBusca(orderProperty);
		if (ordernarJava)  {
			listaCirurgiasPacientesEmSalaRecuperacao = getMbcCirurgiasDAO().listarCirurgiasPacientesEmSR(firstResult, maxResult, 
					null, asc, unidadeFuncional, dataEntradaSr, dataAtual, dataAnterior);
		} else {
			listaCirurgiasPacientesEmSalaRecuperacao = getMbcCirurgiasDAO().listarCirurgiasPacientesEmSR(firstResult, maxResult, 
					parseOrderPropertyVoToEntity(orderProperty), asc, unidadeFuncional, dataEntradaSr, dataAtual, dataAnterior);
		}
		
		for	(MbcCirurgias item : listaCirurgiasPacientesEmSalaRecuperacao) {
				listaPacientesEMSR.add(criarVO(item));
		}
		
		if(ordernarJava) {
			CoreUtil.ordenarLista(listaPacientesEMSR, orderProperty, asc);
		}
		 return listaPacientesEMSR;
	}
	
	private boolean isPropriedadeRecuperadaPorOutraBusca(String propriedade) {
		return PacientesEmSalaRecuperacaoVO.Fields.LOCALIZACAO.toString().equals(propriedade) 
				 || PacientesEmSalaRecuperacaoVO.Fields.NOME_CIRURGIAO.toString().equals(propriedade);
	}

	private PacientesEmSalaRecuperacaoVO criarVO(MbcCirurgias item) {
		PacientesEmSalaRecuperacaoVO pacientesEMSR = new PacientesEmSalaRecuperacaoVO();
		pacientesEMSR.setNomeCirurgiao(setarCirurgiao(item.getSeq())); // C4
		pacientesEMSR.setPacCodigo(item.getPaciente().getCodigo());
		pacientesEMSR.setNomePaciente(item.getPaciente().getNome());
		pacientesEMSR.setProntuario(item.getPaciente().getProntuario());
		pacientesEMSR.setCrgSeq(item.getSeq());
		pacientesEMSR.setDataEntradaSr(item.getDataEntradaSr());
		pacientesEMSR.setDataSaidaSr(item.getDataSaidaSr());
		pacientesEMSR.setLocalizacao(setarLocalizacao(item.getPaciente().getCodigo())); //MBCC_LOCAL_AIP_PAC
		return pacientesEMSR;
	}
	
	
	private String parseOrderPropertyVoToEntity(String orderPropertyVO) {
		if(PacientesEmSalaRecuperacaoVO.Fields.PRONTUARIO.toString().equals(orderPropertyVO)) {
			return PREFIXO_PACIENTE + AipPacientes.Fields.PRONTUARIO.toString();
		}
		if(PacientesEmSalaRecuperacaoVO.Fields.CRG_SEQ.toString().equals(orderPropertyVO)) {
			return PREFIXO_CIRURGIA + MbcCirurgias.Fields.SEQ.toString();
		}
		if(PacientesEmSalaRecuperacaoVO.Fields.PAC_CODIGO.toString().equals(orderPropertyVO)) {
			return PREFIXO_PACIENTE + AipPacientes.Fields.CODIGO.toString();
		}
		if(PacientesEmSalaRecuperacaoVO.Fields.NOME_PACIENTE.toString().equals(orderPropertyVO)) {
			return PREFIXO_PACIENTE + AipPacientes.Fields.NOME.toString();
		}
		if(PacientesEmSalaRecuperacaoVO.Fields.DATA_ENTRADA_SR.toString().equals(orderPropertyVO)) {
			return PREFIXO_CIRURGIA + MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString();
		}
		if(PacientesEmSalaRecuperacaoVO.Fields.DTHR_SAIDA_SR.toString().equals(orderPropertyVO)) {
			return PREFIXO_CIRURGIA + MbcCirurgias.Fields.DTHR_SAIDA_SR.toString();
		}
		return PREFIXO_CIRURGIA + MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString();
	}
	
	
	
	public Long listarPacientesEmSRCount(AghUnidadesFuncionais unidadeFuncional, Date dataEntradaSr) throws ApplicationBusinessException {
		
		Date dataAtual = DateUtil.truncaData(new Date());
		
		Date dataAnterior = recuperarDataAnterior(dataAtual);
		
		return getMbcCirurgiasDAO().listarCirurgiasPacientesEmSRCount(unidadeFuncional, dataEntradaSr, dataAtual, dataAnterior);
	}

	protected Date recuperarDataAnterior(Date dataAtual) throws ApplicationBusinessException {
		
		IParametroFacade parametroFacade = this.getParametroFacade();		
		AghParametros parametros = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_DA_CIRURGIA_EM_SR);
		BigDecimal diasRetroativos = parametros.getVlrNumerico(); 
		Date dataAnterior = DateUtil.adicionaDias(dataAtual, -diasRetroativos.intValue());
		
		return dataAnterior;
	}
	
	//MBCC_LOCAL_AIP_PAC
	public String setarLocalizacao(Integer pacCodigo){
		return getEscalaCirurgiasON().pesquisaQuarto(pacCodigo);
	}
	
	// C4
	public String setarCirurgiao(Integer crgSeq) {
		MbcProfCirurgias profCirurgia = this.getMbcProfCirurgiasDAO().obterCirurgiaoEscalaCirurgicaPorCirurgia(crgSeq, null);
		return profCirurgia.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome(); // C4		
	}

	
	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
}
