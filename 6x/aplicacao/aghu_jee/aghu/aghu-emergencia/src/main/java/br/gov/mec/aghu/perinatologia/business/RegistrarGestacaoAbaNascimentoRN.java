package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.blococirurgico.service.IBlocoCirurgicoService;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoLogImpressoesId;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

/**
 * 
 * @author Jean Couto (jccouto)
 * @since 08/09/2014
 */
@Stateless
public class RegistrarGestacaoAbaNascimentoRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2089325032763501061L;
	
//	private static final String MODULO_EXAMES = "agendamentoExames";
	private static final String ASSINATURA_DIGITAL = "ASSINATURA DIGITAL";
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@EJB
	private IConfiguracaoService configuracaoService;
	
	@EJB
	private IBlocoCirurgicoService blocoCirurgicoService;	
	
	@EJB
	private IAmbulatorioService ambulatorioService;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;	
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@EJB
	private ICertificacaoDigitalService certificacaoDigitalService;
	
	@EJB
	private IPacienteService pacienteService;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RegistrarGestacaoAbaNascimentoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_DESCRICAO_CIRURGICA, MCO_00737
	}
	
	// #26325 - RN01
	public boolean gravarSumarioDefinitivo(Integer pacCodigo, 
											Short seqp, 
											Integer conNumero, 
											String hostName, 
											DadosNascimentoVO nascimentoSelecionado,
											DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
											DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
//		if(this.cascaService.verificarSeModuloEstaAtivo(MODULO_BLOCO_CIRURGICO)){
			
			return this.verificarNascimentos(pacCodigo, seqp, 
										conNumero, hostName, 
										nascimentoSelecionado, 
										dadosNascimentoSelecionado, 
										dadosNascimentoSelecionadoOriginal);
			
//		} else {
//			
//			return Boolean.FALSE;
//			return this.validarGravarNascimentoSumario(pacCodigo, seqp, conNumero, hostName,
//														nascimentoSelecionado, 
//														dadosNascimentoSelecionado,
//														dadosNascimentoSelecionadoOriginal);
			 
//		}
		
	}
	
	private boolean verificarNascimentos(Integer pacCodigo, Short seqp, Integer conNumero, 
										String hostName, DadosNascimentoVO nascimentoSelecionado,
										DadosNascimentoSelecionadoVO dadosNascimentoSelecionado, DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException{
		
		// C16 - #26325
		List<McoNascimentos> listaNascimentos = this.mcoNascimentosDAO.pesquisarNascimentosPorGestacao(pacCodigo, seqp);
		
		if(listaNascimentos != null && !listaNascimentos.isEmpty()){
			
			for(McoNascimentos nascimento : listaNascimentos){
				
				if(nascimento.getTipo().equals(DominioTipoNascimento.C)){
					return this.verificarUnidFunc(pacCodigo, 
													seqp, 
													conNumero, 
													hostName, 
													nascimentoSelecionado, 
													dadosNascimentoSelecionado, 
													dadosNascimentoSelecionadoOriginal);
				}
			}
		} 
			
		return Boolean.FALSE;
		
	}
	
	private boolean verificarUnidFunc(Integer pacCodigo, Short seqp, Integer conNumero, 
									String hostName, DadosNascimentoVO nascimentoSelecionado,
									DadosNascimentoSelecionadoVO dadosNascimentoSelecionado, DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		// C2 - #26325
		List<Short> listaUnfSeq = this.configuracaoService.pesquisarUnidFuncExecutora();
		
		if(listaUnfSeq != null && !listaUnfSeq.isEmpty()){
			
			// C13 - #26325
			List<Date> listaDatas = this.obterDatasInicioProcedimentoCirurgico(pacCodigo, seqp);
			
			for(Date dataCirurgia : listaDatas){
				
				// C3 - #26325
				List<Object[]> listaDescCirurgica = this.blocoCirurgicoService.obterCirurgiaDescCirurgicaPaciente(pacCodigo, listaUnfSeq, dataCirurgia);
				
				if(listaDescCirurgica != null && !listaDescCirurgica.isEmpty()){
					
					for(Object[] objDesc : listaDescCirurgica){
						
						if(objDesc[1] != null || objDesc[2] != null){
							
							// RN02 - #26325
//							return this.validarGravarNascimentoSumario(pacCodigo, seqp, conNumero, 
//																		hostName, 
//																		nascimentoSelecionado, 
//																		dadosNascimentoSelecionado, 
//																		dadosNascimentoSelecionadoOriginal);
							//Encontrou - executar a RN02
							return Boolean.TRUE;
						} else {
							throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoRNExceptionCode.MENSAGEM_ERRO_DESCRICAO_CIRURGICA);
						}
					}
				} else {
					return Boolean.FALSE;
				}
			}
			
		} else {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoRNExceptionCode.MENSAGEM_ERRO_DESCRICAO_CIRURGICA);
		}
		
		return Boolean.FALSE;
	}

	private List<Date> obterDatasInicioProcedimentoCirurgico(Integer pacCodigo, Short seqp) {

		List<McoNascimentos> listaDthrPrevInicio = this.mcoNascimentosDAO.obterDataHoraPrevInicio(pacCodigo, seqp);
		
		List<McoNascimentos> listaDthrNascimento = this.mcoNascimentosDAO.obterDataHoraNascimento(pacCodigo, seqp);
		
		List<Date> listaDatas = new ArrayList<Date>();
		
		for(McoNascimentos prevInicio : listaDthrPrevInicio){
			if(prevInicio.getMcoCesarianas() != null){
				listaDatas.add(prevInicio.getMcoCesarianas().getDthrPrevInicio());
			}
		}
		
		for(McoNascimentos nascimento : listaDthrNascimento){
			Date dataNasc = nascimento.getDthrNascimento();
			Short periodo = nascimento.getPeriodoExpulsivo();
			
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(dataNasc);
			cal.add(Calendar.DAY_OF_MONTH, - periodo);
			
			listaDatas.add(cal.getTime());
		}
		
		return listaDatas;
	}
	
	// RN02 - #26325
//	private boolean validarGravarNascimentoSumario(Integer pacCodigo, Short seqp, Integer conNumero, 
//													String hostName, 
//													DadosNascimentoVO nascimentoSelecionado,
//													DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
//													DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException{
//		
//		// chamar metodo do botao gravar 
//		this.abaNascimentoON.gravarDadosAbaNascimento(conNumero, 
//														hostName, 
//														nascimentoSelecionado, 
//														dadosNascimentoSelecionado, 
//														dadosNascimentoSelecionadoOriginal);
//		
//		if(this.cascaService.verificarSeModuloEstaAtivo(MODULO_EXAMES)){
//			
//			try {
//				
//				String url = (String) this.configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_EXAME_VDRL.toString(), "vlrTexto");
//			
//				if(url != null && !url.isEmpty()){
//					Integer atdSeq = this.ambulatorioService.obterAtdSeqPorNumeroConsulta(conNumero);
//					
//					Boolean solicExames = this.examesService.verificarExameVDRLnaoSolicitado(atdSeq);
//					
//					if(!solicExames){
//						return Boolean.TRUE;
//					}
//					
//				} else {
//					throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoRNExceptionCode.MCO_00737);
//				}
//			
//			} catch (ServiceException e) {
//				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoRNExceptionCode.MCO_00737);
//			}
//			
//		} else {
//			this.executaImpressaoGeracaoPendenciaAssinatura(pacCodigo, seqp);			
//		}
//		
//		return Boolean.FALSE;
//	}

	// RN08 - #26325
	public void executaImpressaoGeracaoPendenciaAssinatura(Integer pacCodigo, Short seqp) {
		Integer seqImpressao = this.mcoLogImpressoesDAO.obterSeqpLogImpressao(pacCodigo, seqp);
		
		if(seqImpressao != null){
			this.insertLogImpressoes(seqImpressao, pacCodigo, seqp);
		} else {
			seqImpressao = 1;
			this.insertLogImpressoes(seqImpressao, pacCodigo, seqp);
		}
	}

	private void insertLogImpressoes(Integer seqImpressao, Integer pacCodigo, Short seqp) {

		McoLogImpressoesId id = new McoLogImpressoesId();
		id.setGsoPacCodigo(pacCodigo);
		id.setGsoSeqp(seqp);
		id.setSeqp(seqImpressao);
		
		McoLogImpressoes logImpressoes = new McoLogImpressoes();
		logImpressoes.setId(id);
		
		McoGestacoesId idGestacoesId = new McoGestacoesId();
		idGestacoesId.setPacCodigo(pacCodigo);
		idGestacoesId.setSeqp(seqp);
		
		McoGestacoes gestacoes = this.mcoGestacoesDAO.obterPorChavePrimaria(idGestacoesId);
		logImpressoes.setMcoGestacoes(gestacoes);
		
		logImpressoes.setCriadoEm(new Date());
		
		logImpressoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		
		logImpressoes.setEvento(DominioEventoLogImpressao.MCOR_NASCIMENTO.getDescricao());
		
		this.mcoLogImpressoesDAO.persistir(logImpressoes);
		
	}

	// RN06 - #26325
	public Integer solicitarExameVdrl(Integer conNumero) {
		return this.ambulatorioService.obterAtendimentoPorConNumero(conNumero);
	}
	
	// C6 - #26325
	public Object[] obterConselhoESiglaVRapServidorConselho(){
		return this.blocoCirurgicoService.obterConselhoESiglaVRapServidorConselho(this.usuario.getMatricula(), this.usuario.getVinculo());
	}

	// RN09 - #26325
	public Boolean gerarPendenciaAssinaturaDigital() throws ServiceException, ApplicationBusinessException {
		
		Object param = this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_HABILITA_CERTIF.toString(), "vlrTexto");
		
		if(param != null && param.toString().equalsIgnoreCase("S")){
			Boolean certif = this.certificacaoDigitalService.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado();
			if(certif){
				return this.pacienteService.verificarAcaoQualificacaoMatricula(ASSINATURA_DIGITAL);
			} else {
				return Boolean.FALSE;
			}
			
		}
		
		return Boolean.FALSE;
		
	}
	
}
