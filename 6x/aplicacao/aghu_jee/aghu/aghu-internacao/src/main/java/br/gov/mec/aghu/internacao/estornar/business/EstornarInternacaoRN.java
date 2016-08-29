package br.gov.mec.aghu.internacao.estornar.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinCidsInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinDocsInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoJnDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMedicosAssistentesDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinObservacoesCensoDAO;
import br.gov.mec.aghu.internacao.dao.AinObservacoesPacInternadoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinDocsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinInternacaoJn;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMedicosAssistentes;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinObservacoesCenso;
import br.gov.mec.aghu.model.AinObservacoesPacInternado;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.MamPcIntItemParada;
import br.gov.mec.aghu.model.MamPcIntParada;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class EstornarInternacaoRN extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(EstornarInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AinObservacoesCensoDAO ainObservacoesCensoDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private AinMedicosAssistentesDAO ainMedicosAssistentesDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IResponsaveisPacienteFacade responsaveisPacienteFacade;
	
	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private AinCidsInternacaoDAO ainCidsInternacaoDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AinDocsInternacaoDAO ainDocsInternacaoDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AinObservacoesPacInternadoDAO ainObservacoesPacInternadoDAO;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@Inject
	private AinInternacaoJnDAO ainInternacaoJnDao;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1270898962969850131L;

	private enum EstornarInternacaoRNExceptionCode implements
	BusinessExceptionCode {
		AIN_ATENDIMENTO_NAO_ENCONTRADO, AIN_00781, AIN_00364, AIN_EXISTE_CONTA_CONVENIO, AIN_00780, ERRO_REMOVER_CID_INTERNACAO,
		ERRO_REMOVER_DOCS_INTERNACAO, ERRO_REMOVER_MOVIMENTOS_INTERNACAO, ERRO_REMOVER_CID_CONTA_HOSP_INTERNACAO,
		ERRO_DESVINCULAR_CIRURGIAS_INTERNACAO, ERRO_REMOVER_ATENDIMENTOS_INTERNACAO, ERRO_REMOVER_RM_ATENDIMENTOS_INTERNACAO, ERRO_REMOVER_INTERNACAO, 
		ERRO_REMOVER_ATENDIMENTOS_INTERNACAO_REGISTROS_DEPENDENTES,
		ERRO_REMOVER_OBSERVACOES_PAC_INTERNADO, ERRO_REMOVER_SUGESTAO_CONTA_DESDOBRE, ERRO_REMOVER_LAUDOS_INTERNACAO,
		AIN_00860_1, CONTA_ITENS_ATIVOS_NAO_EXCLUI, ERRO_REMOVER_ITENS_CONTA_HOSPITALAR, ERRO_ALTA_DIAGNOSTICO, ERRO_REMOVER_OBSERVACOES_CENSO_PAC_INTERNADO,
		AIN_CONTROLE_PACIENTE;
	}
	
	/**
	 * Verificar o Controle de Paciente.
	 * @param intSeq
	 * @param paciente
	 * @param indSitSumarioAlta
	 * @throws AGHUNegocioException
	 */
	public void verificarControlePaciente(Integer intSeq, AipPacientes paciente) throws ApplicationBusinessException {
		List<EcpControlePaciente> ecpControlePaciente = getControlePacienteFacade().pesquisarRegistrosPacientePorAtdEPaciente(intSeq, paciente);
		if (ecpControlePaciente.size() != 0){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_CONTROLE_PACIENTE);
		}
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO
	 * @param intSeq
	 * @param atdSeq
	 * @param indSitSumarioAlta
	 * @throws ApplicationBusinessException
	 */
	public void verificarPrescricao(Integer intSeq, Integer atdSeq, DominioSituacaoSumarioAlta indSitSumarioAlta) throws ApplicationBusinessException{
		AghAtendimentos atendimentoGestacao = obterAtendimentoGestacao(intSeq);
		MpmPrescricaoMedica prescricao = obterPrescricaoMedica(atdSeq);
		MpmAltaSumario altaSumario = null;
		
		if (atendimentoGestacao == null){
			if (prescricao != null){
				AghAtendimentos atendimento = prescricao.getAtendimento();
				if (atendimento.getAltasSumario() != null && atendimento.getAltasSumario().size() > 0){
					altaSumario = atendimento.getAltasSumario().get(0);
				}
				
				if (!DominioSituacaoSumarioAlta.M.equals(indSitSumarioAlta) 
						|| !(altaSumario != null) 
						|| DominioIndConcluido.S.equals(altaSumario.getConcluido())) {
					throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_00780);
				}
			}			
		}	
	}
		
	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR c_gestacoes)
	 * @param intSeq
	 * @return
	 */
	private AghAtendimentos obterAtendimentoGestacao(Integer intSeq){
		return getAghuFacade().obterAtendimentoGestacao(intSeq);		
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR C_PRESC)
	 * obtém a prescrição médica
	 * @param atdSeq
	 * @return
	 */
	private MpmPrescricaoMedica obterPrescricaoMedica(Integer atdSeq){
		return getPrescricaoMedicaFacade().obterPrescricaoMedica(atdSeq);		
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA 
	 * @param intSeq
	 * @throws ApplicationBusinessException 
	 */
	public void verificarEncerramentoConta(Integer intSeq) throws ApplicationBusinessException {
		
		CntaConv contaConv = obterCntaConv(intSeq);
		FatContasHospitalares contaHospitalar = obterContaHospitalar(intSeq);
	
		if (contaHospitalar != null && 
				contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_00364);
		}
		else if (contaConv != null){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_EXISTE_CONTA_CONVENIO);
		}
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 1)
	 * Obtém a CntaConv
	 * @param intSeq
	 * @return
	 */
	private CntaConv obterCntaConv(Integer intSeq) {
		return getFaturamentoFacade().obterCntaConv(intSeq);
	}
	
	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 2) Obtém a conta
	 * hospitalar para encerramento
	 * 
	 * @param intSeq
	 * @return
	 */
	private FatContasHospitalares obterContaHospitalar(Integer intSeq) {
		return getFaturamentoFacade().obterContaHospitalarPorInternacao(intSeq);
	}
	
	/**
	 * ORADB Forms Procedure AINP_VERIFICA_EXAME
	 * @param intSeq
	 * @throws ApplicationBusinessException 
	 */
	public AghAtendimentos verificarExame(Integer intSeq) throws ApplicationBusinessException{
		Byte cspSeq = 1;
		AghAtendimentos retorno = null;
		Integer vGso = 0;
		List<AghAtendimentos> listaAtdGestacoes = pesquisarAtendimentosGestacoes(intSeq);
		Integer soeSeq = 0;
		
		AghAtendimentos atendimento = obterAtendimentoExames(intSeq);
		
		if (atendimento != null){
			Set<AelSolicitacaoExames> solicitacoesExame = atendimento.getAelSolicitacaoExames();
			for (AelSolicitacaoExames exame: solicitacoesExame){
				if (exame.getConvenioSaudePlano() != null && exame.getConvenioSaudePlano().getId().getSeq().equals(cspSeq)){
					soeSeq = exame.getSeq();
					break;
				}
			}			
		}

		if (listaAtdGestacoes.size() == 0){
			vGso = 0;
		}
		else{
			vGso = 1;
		}
		if (atendimento == null){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_ATENDIMENTO_NAO_ENCONTRADO);
		}
		else{
			if (soeSeq > 0 && vGso == 0){
				throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_00781);
			}
			else{
				retorno = atendimento;			
			}
		}
		return retorno;
	}
	
	/**
	 * Pesquisa por atendimentos de gestações
	 * 
	 * @param intSeq
	 * @return
	 */
	private List<AghAtendimentos> pesquisarAtendimentosGestacoes(Integer intSeq) {
		return getAghuFacade().pesquisarAtendimentosGestacoes(intSeq);
	}
	
	
	private AghAtendimentos obterAtendimentoExames(Integer intSeq){
		return getAghuFacade().obterAtendimentoExames(intSeq);
	}
	
	/**
	 * ORADB Forms AINP_DESOCUPA_LEITO
	 * Método que realiza a desocupação do leito
	 * @param leito
	 * @throws ApplicationBusinessException 
	 */
	public void desocuparLeito(AinLeitos leito, AinInternacao internacao) throws ApplicationBusinessException{
		AghuParametrosEnum parametroNome = AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO;
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(parametroNome);
		Short codMvtoLeitoLivre = aghParametro.getVlrNumerico().shortValue();
		AinTiposMovimentoLeito tipoMovimentoLeito = obterTipoMovimentoLeito(codMvtoLeitoLivre);
		
		List<AinLeitos> listaLeitoPorSeqInternacao  = getAinLeitosDAO().obterListaLeitosPorSeqInternacao(internacao);
		List<AinLeitosVO> listaLeitos = new ArrayList<>();

		for (AinLeitos lista : listaLeitoPorSeqInternacao) {
			lista.setTipoMovimentoLeito(tipoMovimentoLeito);
			lista.setInternacao(null);
			listaLeitos.add(new AinLeitosVO(lista));
		}
		getCadastrosBasicosInternacaoFacade().persistirQuarto(leito.getQuarto(), listaLeitos);
	}
	/**
	 * ORADB Forms AINP_ATUALIZA_LEITO
	 * Método que realiza a atualização do leito
	 * @param leito
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarLeito(AinLeitos leito) throws ApplicationBusinessException {
		leito.setInternacao(null);
		List<AinLeitosVO> listaLeitos = new ArrayList<>();
		listaLeitos.add(new AinLeitosVO(leito));
		
		getCadastrosBasicosInternacaoFacade().persistirQuarto(leito.getQuarto(), listaLeitos);
	}
	
	/**
	 * ORADB Forms AINP_DELETA_INT
	 * Método que faz a remoção da internação
	 * @param internacao
	 * @throws ApplicationBusinessException 
	 */
	public void deletarInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		//Remove os acompanhantes do internado
		AinInternacao internacaoOriginal = getInternacaoFacade().obterInternacao(internacao.getSeq());
		getInternacaoFacade().removerAcompanhantes(internacaoOriginal.getAcompanhantesInternacao());
		//Remove os cids do internado
		removerCidsInternacao(internacaoOriginal.getCidsInternacao());
		//Remove as diárias autorizadas do internado
		getCadastrosBasicosInternacaoFacade().removerDiariasAutorizada(internacaoOriginal.getDiariasAutorizadas());
		//Remove os docs do internado
		removerDocsInternacao(internacaoOriginal.getDocsInternacao());
		//Remove os extratos leitos do internado
		getLeitosInternacaoFacade().removerExtratosLeitos(internacaoOriginal.getExtratosLeitos());
		//Remove os médicos assistentes (ain_medicos_assistentes)
		removerMedicosAssistentes(internacaoOriginal.getSeq());
		//Remove as observações do internado (ain_observacoes_pac_internado)
		removerObservacoesPacienteInternado(internacaoOriginal.getObservacoesPacInternados());
		//Remove as observações do censo do internado (ain_observacoes_censo)
		removerObservacoesCensoPacienteInternado(internacaoOriginal.getObservacoesCenso());

		//Remove os responsáveis de paciente
		List<AinResponsaveisPaciente> listaResponsaveisPaciente = getResponsaveisPacienteFacade().pesquisarResponsaveisPaciente(internacao.getSeq());
		getResponsaveisPacienteFacade().removerResponsaveisPacienteInternacao(listaResponsaveisPaciente);
		
		//Remove as solicitações de transferências do internado
		getLeitosInternacaoFacade().removerSolicitacoesTransferenciaPaciente(internacaoOriginal.getSoliciTransfPacientes());
		//Remove os laudos - delete_laudos
		removerLaudos(internacao.getSeq());
		//Remove Fichas Apache
		removerFichasApache(internacao.getSeq());
		//Remove contas
		removerContasInternacao(internacao.getSeq(), dataFimVinculoServidor);
		//Desvincula MbcCirugias
		desvincularCirurgias(internacao.getSeq());
		//Atualiza Justificativa da Internação
		atualizarJustificativaInternacao(internacaoOriginal, nomeMicrocomputador, dataFimVinculoServidor);
		//Remove os movimentos de internação
		removerMovimentosInternacao(internacaoOriginal.getMovimentosInternacao());
		//Remove os motivos de ingresso ctis
		removerMotivosIngressoCti(internacaoOriginal);
		//Remove os objetos MpmPim2
		removerMpmPim2(internacaoOriginal);
		//Remove os objetos MamPcIntItemParada
		removerMamPcIntItemParada(internacaoOriginal);
		//Remove os objetos MamPcIntParada
		removerMamIntParada(internacaoOriginal);
		//Atualiza e Remove os Atendimentos
		removerAtendimentos(internacaoOriginal);
		//Remove a internação
		removerInternacao(internacaoOriginal, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * Método que remove os objetos da entidade MpmPim2
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void removerMpmPim2(AinInternacao internacao) throws ApplicationBusinessException{
		List<MpmPim2> listaPim2 = getPrescricaoMedicaFacade().pesquisarPim2PorAtendimento(
				internacao.getAtendimento().getSeq(), null);
		for (MpmPim2 pim2: listaPim2){
			this.getPrescricaoMedicaFacade().removerMpmPim2(pim2);
		}
		this.flush();
	}
	
	/**
	 * Método que remove os objetos da entidade MamPcIntItemParada
	 * @param internacao
	 */
	private void removerMamPcIntItemParada(AinInternacao internacao){
		List<MamPcIntItemParada> listaItemParada = getPrescricaoMedicaFacade()
				.pesquisarItemParadaPorAtendimento(internacao.getAtendimento());
		for (MamPcIntItemParada itemParada: listaItemParada){
			this.getPrescricaoMedicaFacade().removerMamPcIntItemParada(itemParada);
		}
		this.flush();
	}
	
	/**
	 * Método que remove os objetos da entidade MamPcIntParada
	 * @param internacao
	 */
	private void removerMamIntParada(AinInternacao internacao){
		List<MamPcIntParada> listaPcIntParada = getPrescricaoMedicaFacade()
				.pesquisarPcIntParadaPorAtendimento(internacao.getAtendimento());
		for (MamPcIntParada pcIntParada: listaPcIntParada){
			this.getPrescricaoMedicaFacade().removerMamPcIntParada(pcIntParada);
		}
		this.flush();
	}
	
	
	/**
	 * Método que remove os motivos de ingresso CTI
	 * @param internacao
	 */
	private void removerMotivosIngressoCti(AinInternacao internacao) {
		List<MpmMotivoIngressoCti> listaMotivos = getPrescricaoMedicaFacade()
				.pesquisarMotivoIngressoCtisPorAtendimento(
						internacao.getAtendimento());
		for (MpmMotivoIngressoCti motivo: listaMotivos){
			this.getPrescricaoMedicaFacade().removerMpmMotivoIngressoCti(motivo, false);
		}
		this.flush();
	}
	
	/**
	 * Método que remove as fichas apaches de uma internação
	 * @param intSeq
	 */
	private void removerFichasApache(Integer intSeq){
		AghAtendimentos atendimento = obterAtendimentoInternacao(intSeq);
		if (atendimento != null){
			List<MpmFichaApache> listaFichasApache = pesquisarFichasApache(atendimento.getSeq());
			if (listaFichasApache.size() > 0){
				for (MpmFichaApache ficha: listaFichasApache){
					this.getPrescricaoMedicaFacade().removerMpmFichaApache(ficha, false);
				}
				this.flush();
			}
		}
	}
	
	/**
	 * Método que remove os laudos de uma internação
	 * @param intSeq
	 * @throws ApplicationBusinessException
	 */
	private void removerLaudos(Integer intSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = obterAtendimentoInternacao(intSeq);
		if (atendimento != null){
			List<MpmLaudo> listaLaudosImpressos = pesquisarLaudosImpressos(atendimento.getSeq());
			if (listaLaudosImpressos.size() > 0){
				throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.AIN_00860_1);
			}
			else{
				List<MpmLaudo> listaLaudosNaoImpressos = pesquisarLaudosNaoImpressos(atendimento.getSeq());
				if (listaLaudosNaoImpressos.size() > 0){
					for (MpmLaudo laudo: listaLaudosNaoImpressos){
						this.getPrescricaoMedicaFacade().removerMpmLaudo(laudo, false);
					}
					this.flush();
				}
			}
		}
	}
	
	/**
	 * Método que obtém as fichas apache de um atendimento
	 * @param atdSeq
	 * @return
	 */
	private List<MpmFichaApache> pesquisarFichasApache(Integer atdSeq){
		return getPrescricaoMedicaFacade().pesquisarFichasApachePorAtendimento(atdSeq);
	}
	
	/**
	 * Método que obtém os laudos não impressos de um atendimento
	 * @param atdSeq
	 * @return
	 */
	private List<MpmLaudo> pesquisarLaudosNaoImpressos(Integer atdSeq){
		return getPrescricaoMedicaFacade().pesquisarLaudosNaoImpressosPorAtendimento(atdSeq);
	}
	
	/**
	 * Método que obtém os laudos impressos de um atendimento
	 * @param atdSeq
	 * @return
	 */
	private List<MpmLaudo> pesquisarLaudosImpressos(Integer atdSeq){
		return getPrescricaoMedicaFacade().pesquisarLaudosImpressosPorAtendimento(atdSeq);
	}
	
	/**
	 * Método que obtém o atendimento da internação
	 * @param intSeq
	 * @return
	 */
	private AghAtendimentos obterAtendimentoInternacao(Integer intSeq){
		return getAghuFacade().obterAtendimentoInternacao(intSeq);
	}
	
	/**
	 * Método que remove a internação
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void removerInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		try{
			AinInternacaoDAO ainInternacaoDAO = this.getAinInternacaoDAO();
			ainInternacaoDAO.remover(internacao);
			ainInternacaoDAO.flush();
			journalDelete(internacao);
			
			getInternacaoFacade().enforceDelete(internacao, nomeMicrocomputador, dataFimVinculoServidor);
			getInternacaoFacade().posDelete(internacao, nomeMicrocomputador, dataFimVinculoServidor);
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_INTERNACAO, "");
			}
		}
	}
	
	
	/**
	 * Método que atualiza a justificativa da internação
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void atualizarJustificativaInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		getInternacaoFacade().atualizarInternacaoComFlush(internacao, nomeMicrocomputador, dataFimVinculoServidor, false);
	}
	
	private AinTiposMovimentoLeito obterTipoMovimentoLeito(Short codigo) {
		return this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Método que deleta os cids da internação
	 * @param cidsInternacao
	 * @throws ApplicationBusinessException
	 */
	private void removerCidsInternacao(Set<AinCidsInternacao> cidsInternacao) throws ApplicationBusinessException{
		
		try{
			for (AinCidsInternacao cidInternacao: cidsInternacao){
				AinCidsInternacaoDAO ainCidsInternacaoDAO = getAinCidsInternacaoDAO();
				AinCidsInternacao item = ainCidsInternacaoDAO.obterPorChavePrimaria(cidInternacao.getId());
				ainCidsInternacaoDAO.remover(item);
				ainCidsInternacaoDAO.flush();
			}
		}
		catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_CID_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_CID_INTERNACAO, "");
			}
		}
	}
	
	/**
	 * Método que remove os docs da internação
	 * @param docsInternacao
	 * @throws ApplicationBusinessException
	 */
	private void removerDocsInternacao(Set<AinDocsInternacao> docsInternacao) throws ApplicationBusinessException{
		
		try{
			AinDocsInternacaoDAO ainDocsInternacaoDAO = getAinDocsInternacaoDAO();
			for (AinDocsInternacao docInternacao: docsInternacao){
				AinDocsInternacao item = ainDocsInternacaoDAO.obterPorChavePrimaria(docInternacao.getId());
				ainDocsInternacaoDAO.remover(item);
				ainDocsInternacaoDAO.flush();
			}
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_DOCS_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_DOCS_INTERNACAO, "");
			}
		}
	}
	
	/**
	 * Método que remove os médicos assistentes da internação
	 * @param intSeq
	 */
	private void removerMedicosAssistentes(Integer intSeq){		
		AinMedicosAssistentesDAO ainMedicosAssistentesDAO = getAinMedicosAssistentesDAO();
		List<AinMedicosAssistentes> listaMedicosAssistentes = ainMedicosAssistentesDAO.pesquisarAinMedicosAssistentesPorInternacao(intSeq);
		for (AinMedicosAssistentes medico: listaMedicosAssistentes){
			ainMedicosAssistentesDAO.remover(medico);
		}
		ainMedicosAssistentesDAO.flush();
	}
	
	/**
	 * Método que remove as observações do paciente internado.
	 * @param observacoes
	 * @throws ApplicationBusinessException
	 */
	private void removerObservacoesPacienteInternado(Set<AinObservacoesPacInternado> observacoes) throws ApplicationBusinessException{
		
		try{
			AinObservacoesPacInternadoDAO ainObservacoesPacInternadoDAO = getAinObservacoesPacInternadoDAO();
			for (AinObservacoesPacInternado observacao: observacoes){				
				AinObservacoesPacInternado item = ainObservacoesPacInternadoDAO.obterPorChavePrimaria(observacao.getId());
				ainObservacoesPacInternadoDAO.remover(item);
			}
			ainObservacoesPacInternadoDAO.flush();
		}
		catch (Exception e) {	
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_OBSERVACOES_PAC_INTERNADO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_OBSERVACOES_PAC_INTERNADO, "");
			}
		}
	}
	
	
	/**
	 * Método que remove as observações do censo do paciente internado.
	 * @param observacoes
	 * @throws ApplicationBusinessException
	 */
	private void removerObservacoesCensoPacienteInternado(Set<AinObservacoesCenso> observacoesCenso) throws ApplicationBusinessException{
		
		try{
			AinObservacoesCensoDAO ainObservacoesCensoDAO = getAinObservacoesCensoDAO();
			for (AinObservacoesCenso observacao: observacoesCenso){
				AinObservacoesCenso item = ainObservacoesCensoDAO.obterPorChavePrimaria(observacao.getId());
				ainObservacoesCensoDAO.remover(item);
			}
			ainObservacoesCensoDAO.flush();
			
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_OBSERVACOES_CENSO_PAC_INTERNADO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_OBSERVACOES_CENSO_PAC_INTERNADO, "");
			}
		}
	}
	
	/**
	 * Método que remove os movimentos da internação
	 * @param movimentosInternacao
	 * @throws ApplicationBusinessException
	 */
	private void removerMovimentosInternacao(List<AinMovimentosInternacao> movimentosInternacao) throws ApplicationBusinessException{
		
		try{
			AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = getAinMovimentoInternacaoDAO();
			for (AinMovimentosInternacao movimento: movimentosInternacao){
				AinMovimentosInternacao item = ainMovimentoInternacaoDAO.obterPorChavePrimaria(movimento.getSeq());
				ainMovimentoInternacaoDAO.remover(item);
			}
			ainMovimentoInternacaoDAO.flush();
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_MOVIMENTOS_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_MOVIMENTOS_INTERNACAO, "");
			}
		}
	}
	
	/**
	 * Método que itera pelas contas da internação solicitando remoções
	 * A pesquisa deste método utiliza o select for update
	 * @param intSeq
	 * @throws ApplicationBusinessException 
	 */
	private void removerContasInternacao(Integer intSeq, final Date dataFimVinculoServidor) throws BaseException{
		
		List<FatContasInternacao> listaContasInternacao = getFaturamentoFacade().pesquisarContasInternacaoParaUpdate(intSeq);

		List<FatContasHospitalares> listaContasHospitalares = new ArrayList<FatContasHospitalares>();
		for (FatContasInternacao contaInternacao: listaContasInternacao){
			FatContasHospitalares contaHospitalar = contaInternacao.getContaHospitalar();
			//Remove os Cids da conta hospitalar
			removerCidContaHospitalar(contaHospitalar.getSeq());
			
			//Remove os itens da conta hospitalar
			removerItensContaHospitalar(contaHospitalar.getSeq());
			
			//Remove os FatContasSugestaoDesdobres 
			removerContasSugestaoDesdobres(contaHospitalar.getSeq());
			
			listaContasHospitalares.add(contaHospitalar);
			//Remove a conta hospitalar
			this.getFaturamentoFacade().removerFatContasInternacao(contaInternacao, false);
		}
		this.flush();		
		
		for (FatContasHospitalares contaHospitalar: listaContasHospitalares){			
			this.getFaturamentoFacade().removerContaHospitalar(contaHospitalar, contaHospitalar, dataFimVinculoServidor);		
		}
		this.flush();
	}
	
	/**
	 * Método que remove os ítens da conta hospitalar
	 * @param cthSeq
	 * @throws ApplicationBusinessException
	 */
	private void removerItensContaHospitalar(Integer cthSeq) throws ApplicationBusinessException{
		
		List<FatItemContaHospitalar> listaItensAtivos = pesquisarItensContaHospitalar(cthSeq, DominioSituacaoItenConta.A);
		if (listaItensAtivos.size() > 0){
			throw new ApplicationBusinessException(
					EstornarInternacaoRNExceptionCode.CONTA_ITENS_ATIVOS_NAO_EXCLUI);
		}
		else{
			try{
				List<FatItemContaHospitalar> listaItens = pesquisarItensContaHospitalar(cthSeq, null);
				if (listaItens.size() > 0){
					for (FatItemContaHospitalar item: listaItens){
						this.getFaturamentoFacade().removerFatItemContaHospitalar(item, false);
					}
					this.flush();
				}				
			}
			catch (Exception e) {	
				logError(EXCECAO_CAPTURADA, e);
				Throwable cause = ExceptionUtils.getCause(e);
				if (cause instanceof ConstraintViolationException) {
					throw new ApplicationBusinessException(
					EstornarInternacaoRNExceptionCode.ERRO_REMOVER_ITENS_CONTA_HOSPITALAR, ((ConstraintViolationException) cause).getConstraintName());
				}
				else{
					throw new ApplicationBusinessException(
							EstornarInternacaoRNExceptionCode.ERRO_REMOVER_ITENS_CONTA_HOSPITALAR, "");
				}
			}
		}
		
	}

	
	/**
	 * Método que pesquisa os ítens de conta hospitalar ativos
	 * @param cthSeq
	 * @return
	 */
	private List<FatItemContaHospitalar> pesquisarItensContaHospitalar(Integer cthSeq, DominioSituacaoItenConta situacaoConta){
		return getFaturamentoFacade().pesquisarItensContaHospitalarPorCthSituacao(cthSeq, situacaoConta);
	}
	
	/**
	 * Método que remove as sugestões de contas desdobres do internado.
	 * @param cthSeq
	 * @throws ApplicationBusinessException
	 */
	private void removerContasSugestaoDesdobres(Integer cthSeq) throws ApplicationBusinessException{
		List<FatContaSugestaoDesdobr> listaContasDesdobres = getFaturamentoFacade().pesquisarFatContaSugestaoDesdobrPorCthNaoConsidera(cthSeq);
		try{
			for (FatContaSugestaoDesdobr contaDesdobre: listaContasDesdobres){
				getFaturamentoFacade().removerFatContaSugestaoDesdobr(contaDesdobre, true);
			}
			
		}
		catch (Exception e) {	
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_SUGESTAO_CONTA_DESDOBRE, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_SUGESTAO_CONTA_DESDOBRE, "");
			}
		}
	}
	
	

	
	/**
	 * Método que remove os Cids da conta hospitalar
	 * @param cthSeq
	 * @throws ApplicationBusinessException
	 */
	private void removerCidContaHospitalar(Integer cthSeq) throws ApplicationBusinessException{
		
		List<FatCidContaHospitalar> listaCidContas = getFaturamentoFacade().pesquisarCidContaHospitalar(cthSeq);		
		try{
			for (FatCidContaHospitalar cidConta: listaCidContas){
				getFaturamentoFacade().removerFatCidContaHospitalar(cidConta, true);
			}
			
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_CID_CONTA_HOSP_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_REMOVER_CID_CONTA_HOSP_INTERNACAO, "");
			}
		}
	}
	
	/**
	 * Método que descincula as cirurgias da internação
	 * @param intSeq
	 * @throws ApplicationBusinessException 
	 */
	private void desvincularCirurgias(Integer intSeq) throws ApplicationBusinessException{
		try{
			AghAtendimentos atendimento = obterAtendimentoInternacao(intSeq);
			if (atendimento != null){
				for (MbcCirurgias cirurgia: atendimento.getCirurgias()){
					cirurgia.setAtendimento(null);					
					getBlocoCirurgicoFacade().atualizarCirurgia(cirurgia);
				}							
			}
			
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_DESVINCULAR_CIRURGIAS_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						EstornarInternacaoRNExceptionCode.ERRO_DESVINCULAR_CIRURGIAS_INTERNACAO, "");
			}
		}
	}
	
	/**
	 * Método que remove os atendimentos da internação
	 * @param intSeq
	 * @throws ApplicationBusinessException 
	 */
	private void removerAtendimentos(AinInternacao internacao) throws ApplicationBusinessException{
		try{
			//Obtém atendimento gestação
			AghAtendimentos atendimentoGestacao = obterAtendimentoGestacao(internacao.getSeq());
			
			//Obtém atendimento da internação
			AghAtendimentos atendimento = obterAtendimentoInternacao(internacao.getSeq());
			if (atendimento != null){
			    atualizarRMs(atendimento);
				if (atendimentoGestacao != null){
					atualizarAtendimentosGestacoes(atendimentoGestacao);
					atualizarRMs(atendimentoGestacao);
				}
				else if (DominioTipoPlano.A.equals(internacao.getConvenioSaudePlano().getIndTipoPlano())){
					atualizarAtendimentoInternacaoPlanoA(atendimento);
				}
				else if (atendimento != null && atendimento.getAtendimentoUrgencia() != null){
					atualizarAtendimentoInternacaoUrgencia(atendimento);
				}
				else{
					//Remove as altas sumários
					removerAltaSumarios(atendimento);
					//Remove os atendimentos do paciente
					removerAtendimentosPaciente(atendimento);
					//Remove o atendimento
					
					//this.aghuFacade.excluirAtendimento(atendimento);
					this.getAghuFacade().removerAghAtendimentos(atendimento, true);
				}
			}
		}
		catch (ApplicationBusinessException e) {
			logError("Exceção de negócio capturada: ", e);
			throw e;
		}
		catch (Exception e) {		
			logError(EXCECAO_CAPTURADA, e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
				EstornarInternacaoRNExceptionCode.ERRO_REMOVER_ATENDIMENTOS_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
				
			} else if(cause instanceof TransientObjectException){
				throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.ERRO_REMOVER_ATENDIMENTOS_INTERNACAO_REGISTROS_DEPENDENTES);
				
			} else {
				throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.ERRO_REMOVER_ATENDIMENTOS_INTERNACAO, e.getMessage());
			}
		}
	}
	
	private void atualizarRMs(AghAtendimentos atendimento) throws ApplicationBusinessException {
		List<SceReqMaterial> requisicoes = atendimento.getRequisicoesMaterial();
		List<Integer> rmsEfetivadas = new ArrayList<Integer>();
		
		for (SceReqMaterial sceReqMaterial : requisicoes) {
			if(DominioSituacaoRequisicaoMaterial.E.equals(sceReqMaterial.getIndSituacao()) && !sceReqMaterial.getEstorno()) {
				rmsEfetivadas.add(sceReqMaterial.getSeq());
			} else {
				sceReqMaterial.setAtendimento(null);
				sceReqMaterial.setObservacao(StringUtils.defaultString(sceReqMaterial.getObservacao()) + "(Atendimento Estornado ["+atendimento.getSeq()+"])");				
			}
		}
		if(rmsEfetivadas.size() > 0 ){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.ERRO_REMOVER_RM_ATENDIMENTOS_INTERNACAO, rmsEfetivadas);
		}
	}

	/**
	 * Remove os atendimentos do paciente
	 * @param atendimento
	 */
	private void removerAtendimentosPaciente(AghAtendimentos atendimento){
		List<AghAtendimentoPacientes> listaAtendimentosPaciente = getAghuFacade().pesquisarAghAtendimentoPacientesPorAtendimento(atendimento);
		for (AghAtendimentoPacientes atendimentoPaciente: listaAtendimentosPaciente){
			this.getAghuFacade().removerAghAtendimentoPacientes(atendimentoPaciente, false);
		}
		this.flush();			
	}
	
	/**
	 * Remove as altas sumários de um atendimento
	 * @param atendimento
	 */
	private void removerAltaSumarios(AghAtendimentos atendimento){
		if (atendimento != null){			
			List<MpmAltaSumario> listaAltaSumarios = getPrescricaoMedicaFacade().listarAltasSumarioPorAtendimento(atendimento.getSeq());
			for (MpmAltaSumario alta: listaAltaSumarios){
				this.getPrescricaoMedicaFacade().removerMpmAltaSumario(alta, false);
			}
			this.flush();			
		}
	}
	
	/**
	 * Atualiza um atendimento de urgência
	 * @param atendimento
	 */
	private void atualizarAtendimentoInternacaoUrgencia(AghAtendimentos atendimento){
		Date dthrAlta = null;
		if (atendimento.getAtendimentoUrgencia() != null){
			dthrAlta = atendimento.getAtendimentoUrgencia().getDtAltaAtendimento();
		}
		atendimento.setInternacao(null);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setDthrFim(dthrAlta);
		this.getAghuFacade().atualizarAghAtendimentos(atendimento, true);
		
	}
	
	/**
	 * Atualiza um atendimento cujo plano for A
	 * @param atendimento
	 */
	private void atualizarAtendimentoInternacaoPlanoA(AghAtendimentos atendimento){
		Date dthrAlta = null;
		if (atendimento.getAtendimentoUrgencia() != null){
			dthrAlta = atendimento.getAtendimentoUrgencia().getDtAltaAtendimento();
		}
		atendimento.setInternacao(null);
		atendimento.setAtendimentoUrgencia(null);
		atendimento.setDthrIngressoUnidade(null);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
		atendimento.setDthrFim(dthrAlta);
		this.getAghuFacade().atualizarAghAtendimentos(atendimento, true);
		
	}
	
	
	
	/**
	 * Atualiza os atendimentos gestações
	 * @param atendimentoGestacao
	 */
	private void atualizarAtendimentosGestacoes(AghAtendimentos atendimentoGestacao){
		AghAtendimentos atendimentoMae = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atendimentoGestacao.getAtendimentoMae().getSeq());
		AinLeitos leitoMae = null;
		AinQuartos quartoMae = null;
		AghUnidadesFuncionais unidadeFuncionalMae = null;
		
		if (atendimentoMae != null){
			leitoMae = atendimentoMae.getLeito();
			quartoMae = atendimentoMae.getQuarto();
			unidadeFuncionalMae = atendimentoMae.getUnidadeFuncional();
		}
		
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentosMaeGestacao(atendimentoGestacao);		
		for (AghAtendimentos atendimento: listaAtendimentos){
			atendimento.setLeito(leitoMae);
			atendimento.setQuarto(quartoMae);
			atendimento.setUnidadeFuncional(unidadeFuncionalMae);
			atendimento.setOrigem(DominioOrigemAtendimento.N);
			atendimento.setInternacao(null);
			atendimento.setDthrIngressoUnidade(null);
			this.getAghuFacade().atualizarAghAtendimentos(atendimento, false);
		}
		this.flush();
		
	}

	public void varificarDiagnosticos(AghAtendimentos atendimento) throws ApplicationBusinessException {
		if (!this.getAmbulatorioFacade().listardiagnosticosPorAtendimento(atendimento).isEmpty()){
			throw new ApplicationBusinessException(EstornarInternacaoRNExceptionCode.ERRO_ALTA_DIAGNOSTICO);
		}
		
	}
	
	
	private void journalDelete(AinInternacao internacao) {		
        
        RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
        
        final AinInternacaoJn internacaoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL , AinInternacaoJn.class, servidorLogado.getUsuario());

		internacaoJn.setSeq(internacao.getSeq());
		internacaoJn.setPacCodigo(internacao.getPaciente().getCodigo());
		internacaoJn.setEspSeq(internacao.getEspecialidade() == null ? null : internacao.getEspecialidade().getSeq());
		internacaoJn.setSerMatriculaDigita(internacao.getServidorDigita() == null ? null : internacao.getServidorDigita().getId().getMatricula());
		internacaoJn.setSerVinCodigoDigita(internacao.getServidorDigita() == null ? null : internacao.getServidorDigita().getId().getVinCodigo());
		populandoJNDeleteUm(internacao, internacaoJn);
		populandoJnDeleteDois(internacao, internacaoJn);

		this.getAinInternacaoJnDAO().persistir(internacaoJn); 
		this.getAinInternacaoJnDAO().flush();	

	}

	private void populandoJNDeleteUm(AinInternacao internacao,

			final AinInternacaoJn internacaoJn) {

		internacaoJn.setSerMatriculaProfessor(internacao.getServidorProfessor() == null ? null : internacao.getServidorProfessor().getId().getMatricula());
		internacaoJn.setSerVinCodigoProfessor(internacao.getServidorProfessor() == null ? null : internacao.getServidorProfessor().getId().getVinCodigo());
		internacaoJn.setDthrInternacao(internacao.getDthrInternacao());
		internacaoJn.setEnvProntUnidInt(internacao.getEnvProntUnidInt());
		populandoJnDeleteTres(internacao, internacaoJn);

	}

	private void populandoJnDeleteTres(AinInternacao internacao,

			final AinInternacaoJn internacaoJn) {

		internacaoJn.setTciSeq(internacao.getTipoCaracterInternacao() == null ? null : internacao.getTipoCaracterInternacao().getCodigo());
		internacaoJn.setCspCnvCodigo(internacao.getConvenioSaudePlano() == null ? null : internacao.getConvenioSaudePlano().getId().getCnvCodigo());
		internacaoJn.setCspSeq(internacao.getConvenioSaudePlano() == null ? null : internacao.getConvenioSaudePlano().getId().getSeq());
		internacaoJn.setOevSeq(internacao.getOrigemEvento() == null ? null : internacao.getOrigemEvento().getSeq());
		internacaoJn.setIndSaidaPac(internacao.getIndSaidaPac());

	}

	private void populandoJnDeleteDois(AinInternacao internacao,

			final AinInternacaoJn internacaoJn) {

		internacaoJn.setIndDifClasse(internacao.getIndDifClasse());
		internacaoJn.setIndPacienteInternado(internacao.getIndPacienteInternado());
		internacaoJn.setIndLocalPaciente(internacao.getIndLocalPaciente());
		internacaoJn.setLtoLtoId(internacao.getLeito() == null ? null : internacao.getLeito().getLeitoID());
		internacaoJn.setQrtNumero(internacao.getQuarto() == null ? null : internacao.getQuarto().getNumero());
		populandoDleteQuatro(internacao, internacaoJn);
		populandoDeleteCinco(internacao, internacaoJn);

	}

	 void populandoDleteQuatro(AinInternacao internacao,

			final AinInternacaoJn internacaoJn) {

		internacaoJn.setUnfSeq(internacao.getUnidadesFuncionais() == null ? null : internacao.getUnidadesFuncionais().getSeq());
		internacaoJn.setTamCodigo(internacao.getTipoAltaMedica() == null ? null : internacao.getTipoAltaMedica().getCodigo());
		internacaoJn.setAtuSeq(internacao.getAtendimentoUrgencia() == null ? null : internacao.getAtendimentoUrgencia().getSeq());
		internacaoJn.setDtPrevAlta(internacao.getDtPrevAlta());

	}

	private void populandoDeleteCinco(AinInternacao internacao,

			final AinInternacaoJn internacaoJn) {

		internacaoJn.setDthrAltaMedica(internacao.getDthrAltaMedica());
		internacaoJn.setDtSaidaPaciente(internacao.getDtSaidaPaciente());
		internacaoJn.setIhoSeqOrigem(internacao.getInstituicaoHospitalar() == null ? null : internacao.getInstituicaoHospitalar().getSeq());
		internacaoJn.setIhoSeqTransferencia(internacao.getInstituicaoHospitalarTransferencia() == null ? null : internacao.getInstituicaoHospitalarTransferencia().getSeq());
		internacaoJn.setJustificativaAltDel(internacao.getJustificativaAltDel());
		internacaoJn.setDthrPrimeiroEvento(internacao.getDthrPrimeiroEvento());
		internacaoJn.setDthrUltimoEvento(internacao.getDthrUltimoEvento());
		internacaoJn.setIphSeq(internacao.getItemProcedimentoHospitalar()== null ? null : internacao.getItemProcedimentoHospitalar().getId().getSeq());
		internacaoJn.setIphPhoSeq(internacao.getItemProcedimentoHospitalar() == null ? null : internacao.getItemProcedimentoHospitalar().getId().getPhoSeq());
		internacaoJn.setDthrAvisoSamis(internacao.getDataHoraAvisoSamis());
		internacaoJn.setTipoTransacao(DominioTransacaoAltaPaciente.ESTORNO_INTERNACAO.getDescricao());

	}

	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IControlePacienteFacade getControlePacienteFacade() {
		return controlePacienteFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IResponsaveisPacienteFacade getResponsaveisPacienteFacade() {
		return responsaveisPacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
	
	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}
	
	protected AinCidsInternacaoDAO getAinCidsInternacaoDAO() {
		return ainCidsInternacaoDAO;
	}
	
	protected AinDocsInternacaoDAO getAinDocsInternacaoDAO() {
		return ainDocsInternacaoDAO;
	}
	
	protected AinMedicosAssistentesDAO getAinMedicosAssistentesDAO() {
		return ainMedicosAssistentesDAO;
	}
	
	protected AinObservacoesPacInternadoDAO getAinObservacoesPacInternadoDAO() {
		return ainObservacoesPacInternadoDAO;
	}
	
	protected AinObservacoesCensoDAO getAinObservacoesCensoDAO() {
		return ainObservacoesCensoDAO;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}
	
	protected AinInternacaoJnDAO getAinInternacaoJnDAO() {
		return ainInternacaoJnDao;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AinLeitosDAO getAinLeitosDAO() {
		return this.ainLeitosDAO;
	}
}
