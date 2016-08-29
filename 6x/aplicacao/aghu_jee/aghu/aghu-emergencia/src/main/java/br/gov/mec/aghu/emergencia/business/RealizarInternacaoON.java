package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.ambulatorio.vo.LaudoAihVO;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;

/**
 * #27542 - Realizar internação
 * 
 * @author luismoura
 * 
 */
@Stateless
public class RealizarInternacaoON extends BaseBusiness {
	private static final long serialVersionUID = 5926226131354894194L;

	private static final Log LOG = LogFactory.getLog(RealizarInternacaoON.class);

	@Inject
	private IAmbulatorioService ambulatorioService;
	
	@Inject
	private IExamesService examesService;
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private IInternacaoService internacaoService;
	
	@Inject 
	private IEmergenciaFacade emergenciaFacade;

	@Inject 
	private McoGestacoesDAO gestacoesDAO;
	
	@Inject 
	private MamTrgEncInternoDAO trgEncInternoDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO situacaoEmergenciaDAO;
	
	@Inject
	private MamTriagensDAO triagensDAO; 
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return LOG;
	}

	private enum RealizarInternacaoONExceptionCode implements BusinessExceptionCode {
		ERRO_OBRIGATORIO_LAUDO_AIH_PARA_INTERNAR, 
		MESSAGEM_ERRO_SERVICO, 
		MENSAGEM_SERVICO_INDISPONIVEL, 
		MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO,  
		MENSAGEM_ERRO_PARAMETRO
		;
	}

	//RN01 - Estória #27542
	public void realizarInternacao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		try {
			// Executa C1, já desenvolvida na RN01 da estória #26171.
			emergenciaFacade.verificarIdadeGestacional(gsoPacCodigo, gsoSeqp);
		
			//Executa Servico #38473 - C3
			verificarExistenciaDeLaudos(conNumero, gsoPacCodigo);		
		
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	//Se return false exibe modal com a mensagem MENSAGEM_SOLICITAR_EXAMES
	public boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException{
		try {
			//Executa Servico #38474 - C4 Verifica se foi solicitado exame VDRL para a paciente.
			return examesService.verificarExameVDRLnaoSolicitado(atdSeq);
			
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}		
	}

	//Executa Serviço #38475 - C5 Obtém código do atendimento. 
	public Integer obterSeqAtendimentoPorConNumero(Integer conNumero) throws ApplicationBusinessException {
		try {
			Integer atdSeq = configuracaoService.buscarSeqAtendimentoPorConNumero(conNumero);
			if(atdSeq == null){
				throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO, conNumero);
			}
			return atdSeq;
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}			
	}
	
	//Executa C2 - Estória #27542 - Se true exibe a mensagem informativa MCO_00744 em uma modal. RN01 
	public boolean verificarTipoDeGravidez(Integer pacCodigo, Short seqp) {
		McoGestacoes gestacao = gestacoesDAO.obterPorChavePrimaria(new McoGestacoesId(pacCodigo, seqp));
		if(gestacao != null && gestacao.getGravidez() != DominioGravidez.GCO){
			return true;
		}
		return false;
	}

	private void verificarExistenciaDeLaudos(Integer conNumero,	Integer gsoPacCodigo) throws ServiceException, ApplicationBusinessException {
		List<LaudoAihVO> laudosAihVO = ambulatorioService.pesquisarLaudosAihPorConsultaPaciente(conNumero, gsoPacCodigo);
		if(laudosAihVO.isEmpty()){
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.ERRO_OBRIGATORIO_LAUDO_AIH_PARA_INTERNAR);
		}
	}
	
	public void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException{
		try{			
			//Executa Serviço #38819
			internacaoService.realizarInternacaoPacienteAutomaticamente(matricula, vinCodigo, pacCodigo, seqp, numeroConsulta, hostName, trgSeq);
			
			//RN07 #27542
			atualizarSituacaoPacienteParaInternado(matricula, vinCodigo, numeroConsulta, hostName);			
			
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MESSAGEM_ERRO_SERVICO, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	//RN07 #27542 - Regras para atualizar a situação do paciente para "Internado"
	private void atualizarSituacaoPacienteParaInternado(Integer matricula, Short vinCodigo, Integer numeroConsulta, String hostName) throws ApplicationBusinessException {
		//Executa Serviço #34780
		BigDecimal situacaoInternacao = obtemParametroNumericoPorNome(EmergenciaParametrosEnum.P_SIT_INTERNACAO.toString());

		//Executa C8 - #27542
		List<MamTrgEncInterno> listaTrgEncInterno = trgEncInternoDAO.obterTriagemPorNumeroDaConsulta(numeroConsulta);
		
		//C9 #27542 - Consulta utilizada para obter uma situação da emergência
		MamSituacaoEmergencia situacaoEmergencia = situacaoEmergenciaDAO.obterPorSeq(situacaoInternacao.shortValue());
		
		if(listaTrgEncInterno !=null && !listaTrgEncInterno.isEmpty() && situacaoEmergencia != null){
			for (MamTrgEncInterno mamTrgEncInterno : listaTrgEncInterno) {
				if(mamTrgEncInterno.getTriagem() != null){
					MamTriagens mamTriagem = mamTrgEncInterno.getTriagem();
					mamTriagem.setSituacaoEmergencia(situacaoEmergencia);
					
					MamTriagens mamTriagemOriginal = triagensDAO.obterOriginal(mamTriagem);
										
					mamTriagem.setServidorSituacao(servidorDAO.obter(new RapServidoresId(matricula,vinCodigo)));
					
					emergenciaFacade.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
				}
			}
		}
	}
	
	private BigDecimal obtemParametroNumericoPorNome(String nome) throws ApplicationBusinessException {
		Object parametro = parametroFacade.obterAghParametroPorNome(nome, "vlrNumerico");
		if(parametro == null) {
			throw new ApplicationBusinessException(RealizarInternacaoONExceptionCode.MENSAGEM_ERRO_PARAMETRO, nome);			
		}
		return (BigDecimal) parametro;
	}

}
