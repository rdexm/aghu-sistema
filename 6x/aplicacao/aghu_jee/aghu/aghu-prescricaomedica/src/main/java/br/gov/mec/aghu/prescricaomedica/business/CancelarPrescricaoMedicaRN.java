package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.checagemeletronica.business.IChecagemEletronicaFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoOcorencia;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.EceOcorrencia;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.VMpmOcorrenciaPrcr;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CancelarPrescricaoMedicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CancelarPrescricaoMedicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@EJB
	private IChecagemEletronicaFacade checagemEletronicaFacade;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8824602764190551381L;


	public enum CancelarPrescricaoMedicaRNExceptionCode implements BusinessExceptionCode {
		MPM_01256, MPM_01257, IND_PENDENTE_DIETA_INVALIDO, IND_PENDENTE_CUIDADO_INVALIDO, IND_PENDENTE_MEDICAMENTO_INVALIDO
		, IND_PENDENTE_PROCEDIMENTO_INVALIDO, IND_PENDENTE_NPT_INVALIDO, ERRO_CANCELAR_PRESCRICAO, PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO,
		IND_PENDENTE_CONSULTORIA_INVALIDO, IND_PENDENTE_HEMOTERAPIA_INVALIDO, MPM_04035;
	}
	
	/**
	 * ORADB MPMK_CANCELA.MPMP_CANCELA
	 * Método que executa a rotina de cancelamento da prescrição médica
	 * @param prescricao
	 * @throws BaseException 
	 */
	public void cancelarPrescricao(MpmPrescricaoMedica prescricao, String nomeMicrocomputador)
			 throws BaseException{
		MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = getPrescricaoMedicaDAO();
		
		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getPrescricaoDietaDAO();
		MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO = getPrescricaoCuidadoDAO();
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtoDAO();
		MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getPrescricaoProcedimentoDAO();
		MpmPrescricaoNptDAO mpmPrescricaoNptDAO = getPrescricaoNptDAO();
		MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO = getSolicitacaoConsultoriaDAO();
		
		//Valida a prescrição
		this.validarPrescricaoMedica(prescricao);
		
		prescricao = mpmPrescricaoMedicaDAO.obterPrescricaoComAtendimentoPaciente(prescricao.getId().getAtdSeq(), prescricao.getId().getSeq());
		
		//Obtém a lista de dietas
		List<MpmPrescricaoDieta> listaDietas = mpmPrescricaoDietaDAO
		.pesquisarPrescricaoDietasParaCancelar(prescricao.getId().getAtdSeq(), prescricao.getId().getSeq(),
				prescricao.getDthrMovimento());
		
		//Obtém a lista de cuidados
		List<MpmPrescricaoCuidado> listaCuidados = mpmPrescricaoCuidadoDAO
		.pesquisarPrescricaoCuidadosParaCancelar(prescricao.getId().getAtdSeq(), prescricao.getId().getSeq(),
				prescricao.getDthrMovimento());

		
		//Obtém a lista de medicamentos
		List<MpmPrescricaoMdto> listaMedicamentos = mpmPrescricaoMdtoDAO
				.pesquisarPrescricaoMedicamentosParaCancelar(prescricao.getId()
						.getAtdSeq(), prescricao.getId().getSeq(), prescricao
						.getDthrMovimento());
		
		//Faz refresh para trocar valores do cache pelos do banco
		for (MpmPrescricaoMdto prescMdto: listaMedicamentos){
			mpmPrescricaoMdtoDAO.refresh(prescMdto);
		}
		
		//Obtém a lista de procedimentos
		List<MpmPrescricaoProcedimento> listaProcedimentos = mpmPrescricaoProcedimentoDAO
				.pesquisarPrescricaoProcedimentosParaCancelar(prescricao
						.getId().getAtdSeq(), prescricao.getId().getSeq(),
						prescricao.getDthrMovimento());
		
		//Obtém a lista de nutrição parental
		List<MpmPrescricaoNpt> listaNpt = mpmPrescricaoNptDAO
				.pesquisarPrescricaoNptParaCancelar(prescricao.getId()
						.getAtdSeq(), prescricao.getId().getSeq(), prescricao
						.getDthrMovimento());
		
		//Obtém a lista de consultorias
		List<MpmSolicitacaoConsultoria> listaConsultorias = mpmSolicitacaoConsultoriaDAO
				.pesquisarConsultoriasParaCancelar(prescricao
						.getId().getAtdSeq(), prescricao.getId().getSeq(),
						prescricao.getDthrMovimento());
		
		//Obtém a lista de hemoterapias
		List<AbsSolicitacoesHemoterapicas> listaHemoterapias = getBancoDeSangueFacade()
		.pesquisarHemoterapiasParaCancelar(prescricao
				.getId().getAtdSeq(), prescricao.getId().getSeq(),
				prescricao.getDthrMovimento());
		
		//Cancela os ítens que são copiados de uma prescrição para outra
		this.cancelarItens(listaDietas, nomeMicrocomputador);
		this.cancelarItens(listaCuidados, nomeMicrocomputador);
		this.cancelarItens(listaMedicamentos, nomeMicrocomputador);
		this.cancelarItens(listaProcedimentos, nomeMicrocomputador);
		this.cancelarItens(listaNpt, nomeMicrocomputador);
		
		//Cancela os ítens que não são copiados de uma prescrição para outra (consultorias e hemoterapias)
		this.cancelarItens(listaConsultorias, nomeMicrocomputador);
		this.cancelarItens(listaHemoterapias, nomeMicrocomputador);
		
		this.cancelarEce(prescricao.getAtendimento().getSeq(), prescricao.getDthrMovimento());
		
		mpmPrescricaoMedicaDAO.flush();
		
		//Cancela a prescrição
		this.cancelarTirarDeUso(prescricao);
		
		mpmPrescricaoMedicaDAO.flush();
		
	}
	
	/**
	 * 
	 * MPMK_CANCELA.MPMP_CANCELA_ECE
	 * 
	 * @param seqAtd
	 * @param dthrMovimento
	 *  
	 */
	private void cancelarEce(final Integer seqAtd, final Date dthrMovimento) throws ApplicationBusinessException {
		final IChecagemEletronicaFacade checagemEletronicaFacade = this.getChecagemEletronicaFacade();

		final List<VMpmOcorrenciaPrcr> mpmOcorrenciaPrcrs = checagemEletronicaFacade.buscarMpmOcorrenciaPrcr(seqAtd, dthrMovimento);
		for (final VMpmOcorrenciaPrcr vMpmOcorrenciaPrcr : mpmOcorrenciaPrcrs) {
			try {
				final EceOcorrencia eceOcorrencia = checagemEletronicaFacade.obterPorChavePrimaria(vMpmOcorrenciaPrcr.getOcoSeq());
				eceOcorrencia.setSituacao(DominioSituacaoOcorencia.S);
				checagemEletronicaFacade.alterarOrdemLocalizacao(eceOcorrencia);
			} catch (final Exception e) {
				throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.MPM_04035, e.getMessage());
			}
		}
		super.flush();
	}
	
	/**
	 * Método que cancela a prescrição médica
	 * ORADB Procedure MPMK_CANCELA.MPMP_CANC_TIRA_USO
	 * @param prescricao
	 * @throws ApplicationBusinessException 
	 */
	protected void cancelarTirarDeUso(MpmPrescricaoMedica prescricao) throws ApplicationBusinessException{
		try{
			boolean cancelarAtualizando = false;
			MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = getPrescricaoMedicaDAO();
			MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getPrescricaoDietaDAO();
			MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO = getPrescricaoCuidadoDAO();
			MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtoDAO();
			MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getPrescricaoProcedimentoDAO();
			MpmPrescricaoNptDAO mpmPrescricaoNptDAO = getPrescricaoNptDAO();
			MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO = getSolicitacaoConsultoriaDAO();
			
			if (prescricao.getServidorValida() != null ||
					(prescricao.getDthrInicioMvtoPendente() != null &&
							prescricao.getDthrMovimento().compareTo(prescricao.getDthrInicioMvtoPendente()) > 0)){
				
				
				cancelarAtualizando = true;
			}
			
			//Obtém a lista de dietas da prescrição
			List<MpmPrescricaoDieta> listaDietasPrescricao = mpmPrescricaoDietaDAO
					.pesquisarTodasDietasPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de cuidados da prescrição
			List<MpmPrescricaoCuidado> listaCuidadosPrescricao = mpmPrescricaoCuidadoDAO
					.pesquisarTodosCuidadosPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de medicamentos da prescrição
			List<MpmPrescricaoMdto> listaMedicamentosPrescricao = mpmPrescricaoMdtoDAO
					.pesquisarTodosMedicamentosPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de procedimentos da prescrição
			List<MpmPrescricaoProcedimento> listaProcedimentosPrescricao = mpmPrescricaoProcedimentoDAO
					.pesquisarTodosProcedimentosPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de nutrições parentais da prescrição
			List<MpmPrescricaoNpt> listaNptsPrescricao = mpmPrescricaoNptDAO
					.pesquisarTodosNptPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de consultorias da prescrição
			List<MpmSolicitacaoConsultoria> listaConsultoriasPrescricao = mpmSolicitacaoConsultoriaDAO
			.pesquisarTodasConsultoriasPrescricaoMedica(prescricao.getId());
			
			//Obtém a lista de hemoterapias da prescrição
			List<AbsSolicitacoesHemoterapicas> listaHemoterapiasPrescricao = getBancoDeSangueFacade()
			.pesquisarTodasHemoterapiasPrescricaoMedica(prescricao.getId());
			
			
			if (cancelarAtualizando 
					|| !listaDietasPrescricao.isEmpty()
					|| !listaCuidadosPrescricao.isEmpty()
					|| !listaMedicamentosPrescricao.isEmpty()
					|| !listaProcedimentosPrescricao.isEmpty()
					|| !listaNptsPrescricao.isEmpty()
					|| !listaConsultoriasPrescricao.isEmpty()
					|| !listaHemoterapiasPrescricao.isEmpty()) {
				
				this.cancelarAtualizandoPrescricao(prescricao);
			}
			else{
				//Remove a prescrição médica
				MpmPrescricaoMedica prescRemover = mpmPrescricaoMedicaDAO.obterPrescricaoPorId(prescricao.getId());
				mpmPrescricaoMedicaDAO.remover(prescRemover);
				mpmPrescricaoMedicaDAO.flush();
			}
		}
		catch(Exception e){
			logError(e.getClass().getName(),e);
			throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.ERRO_CANCELAR_PRESCRICAO);
		}
		


	}
	
	/**
	 * Método que cancela a prescrição realizando atualização
	 * @param prescricao
	 */
	protected void cancelarAtualizandoPrescricao(MpmPrescricaoMedica prescricao){
		Date novaDthrInicioMvtoPendente = null;
		if (prescricao.getDthrInicioMvtoPendente() != null && prescricao.getDthrMovimento() != null){
			if (prescricao.getDthrInicioMvtoPendente().compareTo(prescricao.getDthrMovimento()) != 0){
				novaDthrInicioMvtoPendente = prescricao.getDthrInicioMvtoPendente();
			}
		}
		else{
			if ((prescricao.getDthrInicioMvtoPendente() != null && prescricao.getDthrMovimento() == null) ||
					(prescricao.getDthrInicioMvtoPendente() == null && prescricao.getDthrMovimento() != null)){
				novaDthrInicioMvtoPendente = prescricao.getDthrInicioMvtoPendente();
			}
		}
		prescricao.setDthrMovimento(null);
		prescricao.setDthrInicioMvtoPendente(novaDthrInicioMvtoPendente);
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		mpmPrescricaoMedicaDAO.atualizar(prescricao);
	}
	
	public void verificarPrescricaoCancelada(MpmPrescricaoMedica prescricao) 
			throws ApplicationBusinessException {
		if(mpmPrescricaoMedicaDAO.obterOriginal(prescricao.getId()) == null) {
			throw new ApplicationBusinessException(
					CancelarPrescricaoMedicaRNExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
		}
	}
	
	
	/**
	 * Método que valida a prescrição médica
	 * @param prescricaoMedica
	 * @throws ApplicationBusinessException  
	 */
	protected void validarPrescricaoMedica(MpmPrescricaoMedica prescricao)
			 throws ApplicationBusinessException {
		this.validarPrescricaJaCancelada(prescricao);
		this.validarSituacaoPrescricao(prescricao);
		this.validarDthrMovimentoPrescricao(prescricao);

	}
	
	protected void validarPrescricaJaCancelada(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		if (prescricao == null){
			throw new ApplicationBusinessException(
					CancelarPrescricaoMedicaRNExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
		}
	}
	
	protected void validarSituacaoPrescricao(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		
		if (!DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())){
			throw new ApplicationBusinessException(
					CancelarPrescricaoMedicaRNExceptionCode.MPM_01256);
		}
	}
	
	protected void validarDthrMovimentoPrescricao(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		
		if (prescricao.getDthrMovimento() == null){
			throw new ApplicationBusinessException(
					CancelarPrescricaoMedicaRNExceptionCode.MPM_01257);
		}
	}
	
	/**
	 * Método que executa o cancelamento dos Ítens  de uma prescrição 
	 * (CUIDADOS, DIETAS, MEDICAMENTOS, PROCEDIMENTOS, NUTRIÇÃO PARENTAL, HEMOTERAPIA, CONSULTORIA).
	 * @param listaItens
	 * @throws ApplicationBusinessException 
	 *  
	 */
	protected void cancelarItens(
			List<? extends ItemPrescricaoMedica> listaItens, String nomeMicrocomputador)
			throws BaseException {	
		
		for (ItemPrescricaoMedica item: listaItens){
			switch (item.getIndPendente()) {
			case B:
				//Remove os sub-ítens
				this.verificarRemoverSubItens(item);
				removerItemFisicamente(item, nomeMicrocomputador);
				break;
			case A:
				//Remove o item logicamente
				removerItemLogicamente(item, nomeMicrocomputador);
				break;
			case E:
				//Remove o item logicamente
				removerItemLogicamente(item, nomeMicrocomputador);
				break;
			case P:
				//Remove os sub-ítens
				this.verificarRemoverSubItens(item);
				removerItemFisicamente(item, nomeMicrocomputador);
				break;
			case R:
				//Verifica se o item não é uma hemoterapia ou consultoria
				validarIndPendenteConsultoriaHemoterapia(item);
				//Remove os sub-ítens
				this.verificarRemoverSubItens(item);
				removerItemFisicamente(item, nomeMicrocomputador);
				break;
			case Y:
				//Verifica se o item não é uma hemoterapia ou consultoria
				validarIndPendenteConsultoriaHemoterapia(item);
				//Remove os sub-ítens
				this.verificarRemoverSubItens(item);
				removerItemFisicamente(item, nomeMicrocomputador);
				break;
			default:
				if (item instanceof MpmPrescricaoDieta){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_DIETA_INVALIDO);
				}
				else if (item instanceof MpmPrescricaoCuidado){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_CUIDADO_INVALIDO);
				}
				else if (item instanceof MpmPrescricaoMdto){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_MEDICAMENTO_INVALIDO);			
				}
				else if (item instanceof MpmPrescricaoProcedimento){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_PROCEDIMENTO_INVALIDO);			
				}
				else if (item instanceof MpmPrescricaoNpt){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_NPT_INVALIDO);
				}
				else if (item instanceof MpmSolicitacaoConsultoria){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_CONSULTORIA_INVALIDO);
				}
				else if (item instanceof AbsSolicitacoesHemoterapicas){
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_HEMOTERAPIA_INVALIDO);
				}
				else{
					throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.ERRO_CANCELAR_PRESCRICAO);
				}
				
			}
		}
	}
	
	/**
	 * Verifica se o indPendente está correto no caso de o item ser uma hemoterapia ou consultoria
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	protected void validarIndPendenteConsultoriaHemoterapia(
			ItemPrescricaoMedica item) throws ApplicationBusinessException {
		
		if (item instanceof MpmSolicitacaoConsultoria){
			throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_CONSULTORIA_INVALIDO);
		}
		else if (item instanceof AbsSolicitacoesHemoterapicas){
			throw new ApplicationBusinessException(CancelarPrescricaoMedicaRNExceptionCode.IND_PENDENTE_HEMOTERAPIA_INVALIDO);
		}
	}
	
	/**
	 * NÃO APAGAR ESTE MÉTODO AINDA
	 * Método que remove os sub-ítens dos ítens
	 * @param item
	 */
	protected void verificarRemoverSubItens(ItemPrescricaoMedica item){
		if (item instanceof MpmPrescricaoDieta){
			MpmPrescricaoDieta dieta = (MpmPrescricaoDieta) item;
			this.removerItensPrescricaoDietas(dieta);
		}
//		else if (item instanceof MpmPrescricaoMdto){
//			MpmPrescricaoMdto medicamento = (MpmPrescricaoMdto) item;
//			this.removerItensPrescricaoMedicamento(medicamento);
//		}
		else if (item instanceof MpmPrescricaoProcedimento){
			MpmPrescricaoProcedimento procedimento = (MpmPrescricaoProcedimento) item;
			this.removerItensPrescricaoProcedimento(procedimento);
		}
		else if (item instanceof MpmPrescricaoNpt){
			MpmPrescricaoNpt nutricaoParental = (MpmPrescricaoNpt) item;
			//Remove as composições
			this.removerComposicoesPrescricaoNpt(nutricaoParental);
		}
		else if (item instanceof AbsSolicitacoesHemoterapicas){
			AbsSolicitacoesHemoterapicas hemoterapia = (AbsSolicitacoesHemoterapicas) item;
			//Remove os ítens de Hemoterapia
			this.removerItensPrescricaoHemoterapia(hemoterapia);
		}
	}
	

	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO(){
		return mpmPrescricaoMedicaDAO;
	}
	
	protected MpmPrescricaoDietaDAO getPrescricaoDietaDAO(){
		return mpmPrescricaoDietaDAO;
	}
	
	protected MpmItemPrescricaoDietaDAO getItemPrescricaoDietaDAO(){
		return mpmItemPrescricaoDietaDAO;
	}
	
	protected MpmPrescricaoCuidadoDAO getPrescricaoCuidadoDAO(){
		return mpmPrescricaoCuidadoDAO;
	}
	
	protected MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoProcedimentoDAO getPrescricaoProcedimentoDAO(){
		return mpmPrescricaoProcedimentoDAO;
	}
	
	protected MpmPrescricaoNptDAO getPrescricaoNptDAO(){
		return mpmPrescricaoNptDAO;
	}
	
	protected MpmSolicitacaoConsultoriaDAO getSolicitacaoConsultoriaDAO(){
		return mpmSolicitacaoConsultoriaDAO;
	}
	
	protected MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedDAO(){
		return mpmModoUsoPrescProcedDAO;
	}
	
	protected MpmComposicaoPrescricaoNptDAO getMpmComposicaoPrescricaoNptDAO(){
		return mpmComposicaoPrescricaoNptDAO;		
	}
	
	protected MpmItemPrescricaoNptDAO getMpmItemPrescricaoNptDAO(){
		return mpmItemPrescricaoNptDAO;		
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade(){
		return bancoDeSangueFacade;
	}
	
	protected IChecagemEletronicaFacade getChecagemEletronicaFacade(){
		return checagemEletronicaFacade;
	}
	
	/**
	 * Método que remove os ítens de uma dieta
	 */
	protected void removerItensPrescricaoDietas(MpmPrescricaoDieta prescricaoDieta){
		MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO = getItemPrescricaoDietaDAO();
		List<MpmItemPrescricaoDieta> listaItens = mpmItemPrescricaoDietaDAO
				.pesquisarItensDieta(prescricaoDieta.getPrescricaoMedica()
						.getAtendimento().getSeq(), prescricaoDieta.getId().getSeq());
		
		for (MpmItemPrescricaoDieta item: listaItens){
			mpmItemPrescricaoDietaDAO.remover(item);
			mpmItemPrescricaoDietaDAO.flush();
		}
	}
	
	/**
	 * Método que remove os ítens de um procedimento
	 */
	protected void removerItensPrescricaoProcedimento(MpmPrescricaoProcedimento prescricaoProcedimento){
		MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO = getMpmModoUsoPrescProcedDAO();
		List<MpmModoUsoPrescProced> listaItens = mpmModoUsoPrescProcedDAO
				.pesquisarModoUsoPrescProcedimentosPorID(prescricaoProcedimento);
		
		for (MpmModoUsoPrescProced item: listaItens){
			mpmModoUsoPrescProcedDAO.remover(item);
			mpmModoUsoPrescProcedDAO.flush();
		}
	}
	
	/**
	 * Remove as composições de uma nutrição parental total
	 * @param prescricaoNpt
	 */
	protected void removerComposicoesPrescricaoNpt(MpmPrescricaoNpt prescricaoNpt){
		MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO = getMpmComposicaoPrescricaoNptDAO();
		List<MpmComposicaoPrescricaoNpt> listaComposicoes = mpmComposicaoPrescricaoNptDAO
				.pesquisarComposicoesPrescricaoNpt(prescricaoNpt.getPrescricaoMedica()
						.getAtendimento().getSeq(), prescricaoNpt.getId().getSeq());
		
		for (MpmComposicaoPrescricaoNpt comp: listaComposicoes){
			//Remove os ítens da composição
			this.removerItensPrescricaoNpt(comp);
			MpmComposicaoPrescricaoNptId id = new MpmComposicaoPrescricaoNptId(comp.getId().getPnpAtdSeq(), comp.getId().getPnpSeq(), comp.getId().getSeqp());
			MpmComposicaoPrescricaoNpt newCompo = mpmComposicaoPrescricaoNptDAO.obterPorChavePrimaria(id);
			//Remove a composiçãompm ComposicaoPrescricaoNptDAO.flush();
			mpmComposicaoPrescricaoNptDAO.remover(newCompo);
			mpmComposicaoPrescricaoNptDAO.flush();
		}
	}
	
	/**
	 * Remove os ítens de uma nutrição parental total
	 * @param prescricaoNpt
	 */
	protected void removerItensPrescricaoNpt(MpmComposicaoPrescricaoNpt composicao){
		MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO = getMpmItemPrescricaoNptDAO();
		
		for (MpmItemPrescricaoNpt item: composicao.getMpmItemPrescricaoNptses()){
			mpmItemPrescricaoNptDAO.remover(item);
			mpmItemPrescricaoNptDAO.flush();
		}
	}
	
	/**
	 * Remove os ítens de uma hemoterapia
	 * @param prescricaoHemoterapia
	 */
	protected void removerItensPrescricaoHemoterapia(AbsSolicitacoesHemoterapicas prescricaoHemoterapia){
		List<AbsItensSolHemoterapicas> listaItens = getBancoDeSangueFacade()
				.pesquisarItensHemoterapia(prescricaoHemoterapia
						.getPrescricaoMedica().getAtendimento().getSeq(),
						prescricaoHemoterapia.getId().getSeq());
		
		for (AbsItensSolHemoterapicas item: listaItens){
			//Remove as justificativas
			this.removerJustificativasPrescricaoHemoterapia(item);
			//Remove o item
			getBancoDeSangueFacade().removerAbsItensSolHemoterapicas(item);
		}
	}
	
	/**
	 * Remove as justificativas de uma hemoterapia
	 * @param prescricaoHemoterapia
	 */
	protected void removerJustificativasPrescricaoHemoterapia(AbsItensSolHemoterapicas itemSolicitacaoHemoterapica){
		for (AbsItemSolicitacaoHemoterapicaJustificativa just: itemSolicitacaoHemoterapica.getItemSolicitacaoHemoterapicaJustificativas()){
			getBancoDeSangueFacade().removerAbsItemSolicitacaoHemoterapicaJustificativa(just);
		}
	}
	
	
	
	/**
	 * Remove o item fisicamente (exclusão)
	 * @param itemPrescricaoMedica
	 * @throws BaseException
	 */
	public void removerItemFisicamente(ItemPrescricaoMedica itemPrescricaoMedica, String nomeMicrocomputador) throws BaseException{
		MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = getPrescricaoMedicaDAO();
		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getPrescricaoDietaDAO();
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
	//	try{
			//Remove o item fisicamente. Verifica antes se é um medicamento para rodar as triggers
			if (itemPrescricaoMedica instanceof MpmPrescricaoMdto){
				MpmPrescricaoMdto medicamento = (MpmPrescricaoMdto) itemPrescricaoMedica;
				prescricaoMedicaFacade.excluirPrescricaoMedicamento(medicamento);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoDieta){
				MpmPrescricaoDieta dieta = (MpmPrescricaoDieta) itemPrescricaoMedica;
				mpmPrescricaoDietaDAO.remover(dieta);
				//TODO: A trigger de remoção de dietas ainda não está implementada, porém
				//seu conteúdo só contempla a integração dos protocolos assistenciais
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoProcedimento){
				MpmPrescricaoProcedimento procedimento = (MpmPrescricaoProcedimento) itemPrescricaoMedica;
				prescricaoMedicaFacade.removerPrescricaoProcedimento(procedimento);
			}
			else if (itemPrescricaoMedica instanceof AbsSolicitacoesHemoterapicas){
				AbsSolicitacoesHemoterapicas hemoterapia = (AbsSolicitacoesHemoterapicas) itemPrescricaoMedica;
				prescricaoMedicaFacade.excluirSolicitacaoHemoterapica(hemoterapia, nomeMicrocomputador);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoCuidado){
				MpmPrescricaoCuidado cuidado = (MpmPrescricaoCuidado) itemPrescricaoMedica;
				prescricaoMedicaFacade.removerPrescricaoCuidado(cuidado);
			}
			else if (itemPrescricaoMedica instanceof MpmSolicitacaoConsultoria){
				MpmSolicitacaoConsultoria consultoria = (MpmSolicitacaoConsultoria) itemPrescricaoMedica;
				prescricaoMedicaFacade.excluirSolicitacaoConsultoria(consultoria);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoNpt){//NOPMD
				MpmPrescricaoNpt prescricaoNpt = (MpmPrescricaoNpt) itemPrescricaoMedica;
				prescricaoMedicaFacade.excluirPrescricaoNpt(prescricaoNpt, nomeMicrocomputador);
			}
			else{
				mpmPrescricaoMedicaDAO.removerItemPrescricaoMedica(itemPrescricaoMedica);					
			}
	//	catch(ApplicationBusinessException e){
			//Este catch é necessário porque caso alguma exception sem rollback seja lançada
			//durante a persistência de algum item, é preciso realizar rollback nos flushs que já
			//foram feitos até aqui. Resolvendo-se este problema aqui, não é preciso utilizar exceptions com rollback
			//na implementação da persistência de cada item, o que não impactará nas outras
			//operações que também utilizam estes métodos.
	//		throw new ApplicationBusinessException(e.getCode());
	//	}
	}
	

	protected void atualizarCancelamentoItemPrescricao(ItemPrescricaoMedica itemPrescricaoMedica){
		//Seta os dados conforme a tabela de cancelamento dos ítens da prescrição
		itemPrescricaoMedica.setAlteradoEm(null);
		itemPrescricaoMedica.setIndPendente(DominioIndPendenteItemPrescricao.N);
		itemPrescricaoMedica.setServidorMovimentado(null);
	}
	
	/**
	 * Remove o item logicamente (alteração)
	 * @param itemPrescricaoMedica
	 *  
	 */
	public void removerItemLogicamente(ItemPrescricaoMedica itemPrescricaoMedica, String nomeMicrocomputador) throws BaseException{
		MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = getPrescricaoMedicaDAO();
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
	//	try{
			if (itemPrescricaoMedica instanceof MpmPrescricaoMdto){	
				MpmPrescricaoMdto medicamentoOriginal = (MpmPrescricaoMdto) itemPrescricaoMedica;
				prescricaoMedicaFacade.desatachar(medicamentoOriginal);
				MpmPrescricaoMdto medicamento = (MpmPrescricaoMdto) itemPrescricaoMedica;
				prescricaoMedicaFacade.persistirPrescricaoMedicamentoCancelar(medicamento, nomeMicrocomputador,medicamentoOriginal);
				itemPrescricaoMedica.setDthrFim((Date) itemPrescricaoMedica.getPrescricaoMedica().getDthrFim().clone());
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				mpmPrescricaoMdtoDAO.merge((MpmPrescricaoMdto)itemPrescricaoMedica);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoCuidado){
				MpmPrescricaoCuidado cuidado = (MpmPrescricaoCuidado) itemPrescricaoMedica;
				prescricaoMedicaFacade.alterarPrescricaoCuidado(cuidado, nomeMicrocomputador, new Date());
				itemPrescricaoMedica.setDthrFim((Date) itemPrescricaoMedica.getPrescricaoMedica().getDthrFim().clone());
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				mpmPrescricaoCuidadoDAO.atualizar((MpmPrescricaoCuidado)itemPrescricaoMedica);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoDieta){
				MpmPrescricaoDieta dieta = (MpmPrescricaoDieta) itemPrescricaoMedica;
				prescricaoMedicaFacade.atualizarPrescricaoDieta(dieta, nomeMicrocomputador, new Date());
				itemPrescricaoMedica.setDthrFim((Date) itemPrescricaoMedica.getPrescricaoMedica().getDthrFim().clone());
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				mpmPrescricaoDietaDAO.atualizar((MpmPrescricaoDieta)itemPrescricaoMedica);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoProcedimento){
				MpmPrescricaoProcedimento procedimento = (MpmPrescricaoProcedimento) itemPrescricaoMedica;
				prescricaoMedicaFacade.atualizarPrescricaoProcedimento(procedimento, nomeMicrocomputador);
				itemPrescricaoMedica.setDthrFim((Date) itemPrescricaoMedica.getPrescricaoMedica().getDthrFim().clone());
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				mpmPrescricaoProcedimentoDAO.atualizar((MpmPrescricaoProcedimento)itemPrescricaoMedica);
			}
			else if (itemPrescricaoMedica instanceof MpmPrescricaoNpt){
				MpmPrescricaoNpt prescricaoNpt = (MpmPrescricaoNpt) itemPrescricaoMedica;
				prescricaoNpt.setIndPendente(DominioIndPendenteItemPrescricao.N);
				prescricaoNpt.setDthrFim(prescricaoNpt.getPrescricaoMedica().getDthrFim());
				Date dataFimVinculoServidor = servidorLogadoFacade.obterServidorLogado().getDtFimVinculo();
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				prescricaoMedicaFacade.atualizarPrescricaoNpt(prescricaoNpt, nomeMicrocomputador, dataFimVinculoServidor);
			}
			else if (itemPrescricaoMedica instanceof MpmSolicitacaoConsultoria){	
				MpmSolicitacaoConsultoria consultoria = (MpmSolicitacaoConsultoria) itemPrescricaoMedica;
				prescricaoMedicaFacade.verificarRegrasNegocioAtualizacaoConsultoria(consultoria);
				itemPrescricaoMedica.setDthrFim(null);
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				mpmSolicitacaoConsultoriaDAO.atualizar((MpmSolicitacaoConsultoria)itemPrescricaoMedica);
			}
			else if (itemPrescricaoMedica instanceof AbsSolicitacoesHemoterapicas){
				AbsSolicitacoesHemoterapicas hemoterapia = (AbsSolicitacoesHemoterapicas) itemPrescricaoMedica;
				prescricaoMedicaFacade.persistirSolicitacaoHemoterapica(hemoterapia, nomeMicrocomputador);
				itemPrescricaoMedica.setDthrFim(null);
				atualizarCancelamentoItemPrescricao(itemPrescricaoMedica);
				getBancoDeSangueFacade().atualizarSolicitacaoHemoterapica((AbsSolicitacoesHemoterapicas)itemPrescricaoMedica, nomeMicrocomputador);
			}

			mpmPrescricaoMedicaDAO.flush();
			
	//	catch(ApplicationBusinessException e){
			//Este catch é necessário porque caso alguma exception sem rollback seja lançada
			//durante a persistência de algum item, é preciso realizar rollback nos flushs que já
			//foram feitos até aqui. Resolvendo-se este problema aqui, não é preciso utilizar exceptions com rollback
			//na implementação da persistência de cada item, o que não impactará nas outras
			//operações que também utilizam estes métodos.
	//		throw new ApplicationBusinessException(e.getCode());
	//	}
		
	}

}
