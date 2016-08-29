package br.gov.mec.aghu.emergencia.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisDAO;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class FinalizarConsultaRN extends BaseBusiness {

	private static final long serialVersionUID = -2168451805689500113L;
	private static final Log LOG = LogFactory.getLog(FinalizarConsultaRN.class);

	public enum FinalizarConsultaRNExceptionCode implements BusinessExceptionCode {	
		
		MENSAGEM_SERVICO_INDISPONIVEL_FINALIZAR_CONSULTA,
		MESSAGEM_ERRO_SERVICO,
		MCO_00620,
		MENSAGEM_ERRO_OBTER_PARAMETRO,
		ERRO_SITUACAO_EMERGENCIA_LISTA_ATENDIDO_VAZIO,
		ERRO_USUARIO_SEM_PERMISSAO_RELATORIO_DEFINITIVOS,
		ERRO_CONDUTA_COMPLEMENTO,
		MENSAGEM_USUARIO_MEDICO,
		MENSAGEM_SERVICO_INDISPONIVEL_VERIFICAR_PERMISSAO,
		MENSAGEM_ERRO_INSERIR_LOG_IMPRESSAO,
		ERRO_VERIFICAR_SE_EXISTE_CONDUTA_SEM_COMPLEMENTO,
		MENSAGEM_SERVICO_INDISPONIVEL;
	}	
	
	@EJB
	private IInternacaoService internacaoService;
	
	@EJB
	private IRegistroColaboradorService registroColaboradorService;
	
	@EJB
	private ICertificacaoDigitalService certificacaoDigitalService;
	
	@EJB
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@Inject 
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private McoPlanoIniciaisDAO mcoPlanoIniciaisDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}	
	
	public void finalizarConsulta(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, Integer matricula, Short vinCodigo, Date criadoEm, String hostName, Boolean isEmergenciaCustom) throws ApplicationBusinessException{
		try {	
			//Executa servico #37435
			verificarPermissaoUsuario(matricula, vinCodigo);
			
			if(!isEmergenciaCustom) {
				verificaSeExisteCondutaSemComplementoNaoCadastrada(gsoPacCodigo, gsoSeqp, conNumero);
			}
			
			List<Short> listaSegSeq = buscarListaSegSeqsMamCaractSitEmergs();
			
			//Obtem identificador da triagem.
			Long trgSeq = emergenciaFacade.obterTrgSeq(conNumero);
			
			MamTriagens mamTriagem = this.mamTriagensDAO.obterPorChavePrimaria(trgSeq);
			MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(trgSeq);
			MamSituacaoEmergencia situacao = this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(listaSegSeq.get(0));
			mamTriagem.setSituacaoEmergencia(situacao);
			emergenciaFacade.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
			
			//Executa servico #37865
			if(!isEmergenciaCustom) {
				internacaoService.finalizarConsulta(conNumero, gsoPacCodigo, matricula, vinCodigo, hostName);
				inserirLogImpressao(conNumero, gsoPacCodigo, gsoSeqp, DominioEventoLogImpressao.MCOR_CONSULTA_OBS.toString(), matricula, vinCodigo, criadoEm);		
			}
			
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MESSAGEM_ERRO_SERVICO, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_FINALIZAR_CONSULTA);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_FINALIZAR_CONSULTA);
		}
    }

	public boolean gerarPendenciaDeAssinaturaDigital(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		try{ 
			boolean gerarPendenciaDeAssinaturaDigital = false;
			String habilitaCertificacaoDigital = obtemParametroPorNome(EmergenciaParametrosEnum.P_HABILITA_CERTIF.toString());
			//Executa servico #37571 
			Boolean certificado = certificacaoDigitalService.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(matricula, vinCodigo);
			
			if(habilitaCertificacaoDigital.equalsIgnoreCase("S") && certificado){
				//Executa servico #37569
				verificarServidorMedico(matricula, vinCodigo);
				gerarPendenciaDeAssinaturaDigital = true;
			}
			return gerarPendenciaDeAssinaturaDigital;
			
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MESSAGEM_ERRO_SERVICO, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public void verificarIdadeGestacional(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		McoGestacoes gestacao = emergenciaFacade.pesquisarMcoGestacaoPorPacCodigoSeqp(gsoPacCodigo, gsoSeqp);
		
		verificarIdadeGestacional(gestacao);
	}

	private void verificarServidorMedico(Integer matricula, Short vinCodigo) throws ServiceBusinessException, ServiceException, ApplicationBusinessException {
		boolean servidorMedico = prescricaoMedicaService.verificarServidorMedico(matricula, vinCodigo);
		if(!servidorMedico){
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_USUARIO_MEDICO);
		}	
	}

	public void verificaSeExisteCondutaSemComplementoNaoCadastrada(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		boolean existe = mcoPlanoIniciaisDAO.verificaSeExisteCondutaSemComplemento(gsoPacCodigo, gsoSeqp, conNumero);
		if(existe){
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.ERRO_CONDUTA_COMPLEMENTO);
		}
	}
	
	public void verificaSeExisteCondutaSemComplementoNaoCadastrada(McoConduta conduta, McoPlanoIniciais planoIniciais) throws ApplicationBusinessException {
		if(conduta != null && StringUtils.isBlank(planoIniciais.getComplemento())){
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.ERRO_CONDUTA_COMPLEMENTO);
		}
	}

	public RapServidorConselhoVO verificarPermissaoUsuario(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException {
		boolean temPermissao = verificarPermissaoUsuarioImprimirRelatorioDefinitivo(matricula, vinCodigo);
		if(!temPermissao){
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.ERRO_USUARIO_SEM_PERMISSAO_RELATORIO_DEFINITIVOS);
		}
		
		return registroColaboradorService.usuarioRelatoriosDefinitivos(matricula, vinCodigo, new String[]{"CREMERS", "COREN"});
	}

	public boolean verificarPermissaoUsuarioImprimirRelatorioDefinitivo(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		try {	
			String siglaConselhoProfissional = obtemParametroPorNome(EmergenciaParametrosEnum.P_CONSELHO_PROFISSIONAL_MED_SOLIC.toString());
			return registroColaboradorService.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(matricula, vinCodigo, new String[] {siglaConselhoProfissional});
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_VERIFICAR_PERMISSAO);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_VERIFICAR_PERMISSAO);
		}
	}

	public void inserirLogImpressao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, String evento, Integer matricula, Short vinCodigo, Date criadoEm) throws ApplicationBusinessException {
		try {
			Integer seqp = emergenciaFacade.obterSeqpLogImpressoes(gsoPacCodigo, gsoSeqp);
			if(seqp == null || seqp == 0){
				seqp = 1;
			}
			
			emergenciaFacade.inserirLogImpressoes(gsoPacCodigo, gsoSeqp, seqp, conNumero, evento, matricula, vinCodigo, criadoEm);
		} catch(RuntimeException e){
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_ERRO_INSERIR_LOG_IMPRESSAO);
		}				
	}

	private List<Short> buscarListaSegSeqsMamCaractSitEmergs() throws ApplicationBusinessException {
		List<Short> listaSegSeq = emergenciaFacade.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.LISTA_ATENDIDO);		
		if(listaSegSeq == null || listaSegSeq.isEmpty()) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.ERRO_SITUACAO_EMERGENCIA_LISTA_ATENDIDO_VAZIO);
		}		
		return listaSegSeq;
	}

	private String obtemParametroPorNome(String nome) throws ApplicationBusinessException, ServiceException {
		Object parametro = parametroFacade.obterAghParametroPorNome(nome, "vlrTexto");
		if(parametro == null) {
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);			
		}
		return (String) parametro;
	}

	private void verificarIdadeGestacional(McoGestacoes gestacao) throws ApplicationBusinessException {
		Calendar hoje = Calendar.getInstance();
		DateUtil.zeraHorario(hoje);			
		if((gestacao.getDtInformadaIg() != null && !DateUtil.isDatasIguais(hoje.getTime(), gestacao.getDtInformadaIg())) || (gestacao.getDtInformadaIg() == null && gestacao.getIgPrimEco()!=null && gestacao.getIgPrimEco().intValue() >25)) {		
			throw new ApplicationBusinessException(FinalizarConsultaRNExceptionCode.MCO_00620);
		}
	}
}
