package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.ListarAmostrasSolicitacaoRecebimentoRN.ListarAmostrasSolicitacaoRecebimentoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoApsDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaApDAO;
import br.gov.mec.aghu.exames.dao.AelMacroscopiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialApDAO;
import br.gov.mec.aghu.exames.dao.AelOcorrenciaExameApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaApDAO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


/**
 * ORADB PROCEDURE AELP_VOLTAR_SIT_SOLICITACAO
 * @author aghu
 *
 */
// TODO Refatorar para ON
@Stateless
public class VoltarTodasAmostrasSolicitacaoRN extends BaseBusiness {


@EJB
private ListarAmostrasSolicitacaoRecebimentoRN listarAmostrasSolicitacaoRecebimentoRN;

private static final Log LOG = LogFactory.getLog(VoltarTodasAmostrasSolicitacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelExtratoAmostrasDAO aelExtratoAmostrasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9143473677102030072L;
	
	public enum VoltarTodasAmostrasSolicitacaoRNExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_LAUDO_EM_ANDAMENTO;
	}	
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;
	
	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;
	
	@Inject
	private AelOcorrenciaExameApDAO aelOcorrenciaExameApDAO;
	
	@Inject
	private AelPatologistaApDAO aelPatologistaApDAO;
	
	@Inject
	private AelExtratoExameApDAO aelExtratoExameApDAO;
	
	@Inject
	private AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO;
	
	@Inject
	private AelExameApDAO aelExameApDAO;
	
	@Inject
	private AelMacroscopiaApsDAO aelMacroscopiaApsDAO;   
	
	@Inject
	private AelDiagnosticoApsDAO aelDiagnosticoApsDAO; 
	
	@Inject
	private AelMaterialApDAO aelMaterialApDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_SOLICITACAO
	 * @param unidadeExecutora
	 * @param amostra
	 * @param nroFrascoFabricante
	 * @param numeroApOrigem
	 * @throws BaseException
	 */
	public boolean voltarSituacaoAmostraSolicitacao(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador) throws BaseException{
		
		// Valida parâmetros obrigatórios
		CoreUtil.validaParametrosObrigatorios(unidadeExecutora, amostra);
		
		amostra = getAelAmostrasDAO().obterPorChavePrimaria(amostra.getId());

		// Unidade Executora Coleta
		if(this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)){
			return this.voltarAmostraUnidadeColeta(amostra, nomeMicrocomputador);
		}else 
		// Central Recebimento Materiais
		if(this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS)){
			return voltarAmostraUnidadeCentralRecebimentoMateriais(unidadeExecutora, amostra, nomeMicrocomputador);
		}else
		// Unidade Executora Exames
		if(this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)){
			return voltarAmostraUnidadeExecutoraExames(unidadeExecutora, amostra, nomeMicrocomputador);
		} 
		
		
		return false;
		
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_SOLICITACAO - Unidade Coleta
	 * 
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeColeta(final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		
		// Indica se ocorreu o recebimento de no minimo uma amostra
		int amostrasModificadas = 0;

		List<AelAmostraItemExames> listaAmostraItemExamesUnidadeColeta = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamePorSolicitacaoESituacao(amostra, DominioSituacaoAmostra.U);
		
		for (AelAmostraItemExames amostraItemExamesUnidadeColeta : listaAmostraItemExamesUnidadeColeta) {

			// Obtém extratos de amostras com a situacao DIFERENTE de U (Recebida
			// Unidade de Coleta) da amostra informada
			final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
					amostra.getId().getSoeSeq(),amostraItemExamesUnidadeColeta.getId().getAmoSeqp().shortValue(), DominioSituacaoAmostra.U);

			// Verifica a existencia de itens
			if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {
	
				// Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra"
				boolean isAmostraVoltou = this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExamesUnidadeColeta, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
				if(isAmostraVoltou){
					amostrasModificadas++;
				}

			} else {
				// Informa que a solicitacao de exames gerada pela area executora,
				// nao possui situacao anterior a ser restaurada
				throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);
			}
		}
		
		return amostrasModificadas > 0;
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_SOLICITACAO - Unidade Executora Exames
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeExecutoraExames(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador)
			throws BaseException {
		
		// Indica se ocorreu a volta de no minimo uma amostra
		boolean isRetornouAmostraUnidade = false;

		List<AelAmostraItemExames> listaAmostraUnidadeExecutora = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamePorSolicitacaoESituacao(amostra, DominioSituacaoAmostra.R);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		for (AelAmostraItemExames amostraItemExamesUnidadeExecutora : listaAmostraUnidadeExecutora) {
			
			verificaSeApagaAnatomopatologico(amostraItemExamesUnidadeExecutora.getAelItemSolicitacaoExames(), servidorLogado);			
			
			// Compara a unidade executora com a unidade funcional das amostras
			if (this.getListarAmostrasSolicitacaoRecebimentoRN().validarUnidadeFuncionalAmostra(amostra.getId().getSeqp(), amostraItemExamesUnidadeExecutora.getAelItemSolicitacaoExames().getUnidadeFuncional(), unidadeExecutora)) {


				// Obtém extratos de amostras com a situacao DIFERENTE de R (Recebida)
				// da amostra informada
				final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
						amostra.getId().getSoeSeq(),amostraItemExamesUnidadeExecutora.getId().getAmoSeqp().shortValue(), DominioSituacaoAmostra.R);

				// Verifica a existencia de itens
				if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {

					// Define o tipo de pesquisa atraves da
					// "unidade executora de exames"
					//List<AelAmostraItemExames> listaAmostraItemExamesUnidadeExecutora = this.getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoVoltarAmostra(amostra);

					// Percorre os "itens de amostra de exames" da "amostra"
					//for (final AelAmostraItemExames amostraItemExameUnidadeExecutora : listaAmostraItemExamesUnidadeExecutora) {
						
					// Pesquisa UPDATE! IMPORTANTE: Neste ponto é necessário consultar com a amostra do item e não a amostra passada ao método
						AelAmostraItemExames amostraItemExameAtualizar = this.getAelAmostraItemExamesDAO().obterAmostraSolicitacaoVoltarAmostraUnidadeExecutoraExamesCentralERecebimentoMateriais(amostraItemExamesUnidadeExecutora, amostraItemExamesUnidadeExecutora.getAelAmostras());
						
						if(amostraItemExameAtualizar != null){
							// Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra"
							this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExameAtualizar, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
							isRetornouAmostraUnidade = true;
						}

					//}

				} else {
					// Informa que a solicitacao de exames gerada pela area executora,
					// nao possui situacao anterior a ser restaurada
					throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);

				}

				
			}

		}
		
		// Caso NENHUMA amostra tenha voltado
		if (!isRetornouAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01089, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + " - "
					+ unidadeExecutora.getDescricao());
		}
		
		return isRetornouAmostraUnidade;
	}
	
	/**
	 * Metodo novo que nao tem no AGHWeb
	 * 
	 * Verifica se está voltando amostras que geraram Anatomopatológico (AP, TO, IH)
	 * Caso seja verdadeiro apaga o Anatomopatológico para ficar integro com as tabelas de exames.
	 * 
	 * @param aelItemSolicitacaoExames
	 * @param servidorLogado
	 * @throws MECBaseException
	 */
	public void verificaSeApagaAnatomopatologico(AelItemSolicitacaoExames aelItemSolicitacaoExames, RapServidores servidorLogado) throws BaseException {
		
		List<AelExameApItemSolic> listaAelExameApItemSolic = getAelExameApItemSolicDAO().listarAelExameApItemSolicPorItemSolicitacaoExame(aelItemSolicitacaoExames.getId().getSoeSeq(), aelItemSolicitacaoExames.getId().getSeqp());
		
		if (!listaAelExameApItemSolic.isEmpty()) {
			Long luxSeq = null;
			//verifica se já foi feito alguma coisa no laudo unico
			for (AelExameApItemSolic aelExameApItemSolic : listaAelExameApItemSolic) {
				luxSeq = aelExameApItemSolic.getId().getLuxSeq();
				if (getAelMacroscopiaApsDAO().obterAelMacroscopiaApsPorAelExameAps(luxSeq) != null ||
						getAelDiagnosticoApsDAO().obterAelDiagnosticoApsPorAelExameAps(luxSeq) != null) {
					throw new ApplicationBusinessException(VoltarTodasAmostrasSolicitacaoRNExceptionCode.MSG_ERRO_LAUDO_EM_ANDAMENTO);
				}
				
				List<AelMaterialAp> listaMaterialAp = getAelMaterialApDAO().listarAelMaterialApPorItemSolic(aelExameApItemSolic.getId().getIseSoeSeq(), aelExameApItemSolic.getId().getIseSeqp());
				
				for (AelMaterialAp aelMaterialAp : listaMaterialAp) {
					examesPatologiaFacade.excluirAelMaterialAp(aelMaterialAp);
				}
				
				aelExameApItemSolicDAO.remover(aelExameApItemSolic);				
				
			}
			
			aelExameApItemSolicDAO.flush(); //precisa do flush para poder pesquisar
			
			listaAelExameApItemSolic = aelExameApItemSolicDAO.listarAelExameApItemSolicPorLuxSeq(luxSeq);
			if (listaAelExameApItemSolic.isEmpty()) { //se nao tem item solics de outras solicitacoes apaga o resto das tabelas
				removeRegistrosTabelasPatologia(luxSeq, servidorLogado);
			}
					
			//se nao tiver mais que um item na ael_exam_ap_item_solics apaga o resto do AP.
		}
		
	}


	private void removeRegistrosTabelasPatologia(Long luxSeq, RapServidores servidorLogado) throws BaseException {
		
		AelExtratoExameApDAO aelExtratoExameApDAO = getAelExtratoExameApDAO();
		List<AelExtratoExameAp> listaAelExtratoExameAp = aelExtratoExameApDAO.listarAelExtratoExameApPorLuxSeq(luxSeq);
		for (AelExtratoExameAp aelExtratoExameAp : listaAelExtratoExameAp) {
			aelExtratoExameApDAO.remover(aelExtratoExameAp);
		}
		
		AelOcorrenciaExameApDAO aelOcorrenciaExameApDAO = getAelOcorrenciaExameApDAO();
		List<AelOcorrenciaExameAp> listaAelOcorrenciaExameAp = aelOcorrenciaExameApDAO.buscarAelOcorrenciaExameApPorSeqExameAp(luxSeq);
		for (AelOcorrenciaExameAp aelOcorrenciaExameAp : listaAelOcorrenciaExameAp) {
			aelOcorrenciaExameApDAO.remover(aelOcorrenciaExameAp); //nao chama triggers pq essa tabela grava "lixo" 
		}
		
		AelPatologistaApDAO aelPatologistaApDAO = getAelPatologistaApDAO();
		List<AelPatologistaAps> listaAelPatologistaAps = aelPatologistaApDAO.listarPatologistaApPorLuxSeq(luxSeq);
		for (AelPatologistaAps aelPatologistaAps : listaAelPatologistaAps) {
			examesPatologiaFacade.excluir(aelPatologistaAps);
		}
		
		AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO = getAelInformacaoClinicaApDAO();
		AelInformacaoClinicaAP aelInformacaoClinicaAP = aelInformacaoClinicaApDAO.obterAelInformacaoClinicaApPorAelExameAps(luxSeq);
		examesPatologiaFacade.excluir(aelInformacaoClinicaAP, servidorLogado.getUsuario());
		
		AelExameApDAO aelExameApDAO = getAelExameApDAO();
		AelExameAp aelExameAp = aelExameApDAO.obterPorChavePrimaria(luxSeq);
		Long lumSeq = aelExameAp.getAelAnatomoPatologicos().getSeq();
		examesPatologiaFacade.excluir(aelExameAp, servidorLogado.getUsuario()); 
		
		AelApXPatologistaDAO aelApXPatologistaDAO = getAelApXPatologistaDAO();
		List<AelApXPatologista> listaAelApXPatologista = aelApXPatologistaDAO.listarAelApXPatologistaPorLumSeq(lumSeq);
		for (AelApXPatologista aelApXPatologista : listaAelApXPatologista) {
			examesPatologiaFacade.excluirAelApXPatologista(aelApXPatologista);
		}
		
		AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO = getAelAnatomoPatologicoDAO();
		AelAnatomoPatologico aelAnatomoPatologico = aelAnatomoPatologicoDAO.obterPorChavePrimaria(lumSeq);
		examesPatologiaFacade.excluir(aelAnatomoPatologico, servidorLogado.getUsuario());
		
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_VOLTAR_SIT_SOLICITACAO - Central Recebimento Materiais
	 * 
	 * @param unidadeExecutora
	 * @param amostra
	 * @throws BaseException
	 */
	public boolean voltarAmostraUnidadeCentralRecebimentoMateriais(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador)
			throws BaseException {
		
		// Indica se ocorreu o recebimento de no minimo uma amostra
		boolean isRetornouAmostraUnidade = false;

		List<AelAmostraItemExames> listaAmostraItemExamesCentralRecebimento = this.getAelAmostraItemExamesDAO().buscarAmostrasSolicitacaoVoltarAmostraCentralRecebimentoMateriaisPorSolicitacao(amostra, unidadeExecutora);
		
		for (AelAmostraItemExames amostraItemExamesCentralRecebimento : listaAmostraItemExamesCentralRecebimento) {

			// Obtém extratos de amostras com a situacao DIFERENTE de R (Recebida)
			// da amostra informada
			final List<AelExtratoAmostras> extratoAmostrasExamesSituacao = this.getAelExtratoAmostrasDAO().buscarExtratosAmostrasPorSituacaoDiferenteInformada(
					amostra.getId().getSoeSeq(),amostraItemExamesCentralRecebimento.getId().getAmoSeqp().shortValue(), DominioSituacaoAmostra.R);

			// Verifica a existencia de itens
			if (extratoAmostrasExamesSituacao != null && !extratoAmostrasExamesSituacao.isEmpty()) {

				// Pesquisa UPDATE! IMPORTANTE: Neste ponto é necessário consultar com a amostra do item e não a amostra passada ao método
				AelAmostraItemExames amostraItemExameAtualizar = this.getAelAmostraItemExamesDAO().obterAmostraSolicitacaoVoltarAmostraUnidadeExecutoraExamesCentralERecebimentoMateriais(amostraItemExamesCentralRecebimento, amostraItemExamesCentralRecebimento.getAelAmostras());
				
				if(amostraItemExameAtualizar != null){
					// Atualiza informacoes de um "item de amostra de exames" atraves de um "extrato de amostra"
					this.atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExameAtualizar, extratoAmostrasExamesSituacao.get(0).getSituacao(), nomeMicrocomputador);
					isRetornouAmostraUnidade = true;
				}


			} else {
				// Informa que a solicitacao de exames gerada pela area executora,
				// nao possui situacao anterior a ser restaurada
				throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01087);

			}
			
		}
		
		// Caso NENHUMA amostra tenha voltado
		if (!isRetornouAmostraUnidade) {
			// Nenhuma amostra a receber
			throw new ApplicationBusinessException(ListarAmostrasSolicitacaoRecebimentoRNExceptionCode.AEL_01089, amostra.getId().getSeqp(), unidadeExecutora.getSeq() + " - "
					+ unidadeExecutora.getDescricao());
		}

		return isRetornouAmostraUnidade;
	}
	
	
	
	
	/**
	 * Atualiza informacoes de um "item de amostra de exames" atraves de um
	 * "extrato de amostra" de uma "amostra" cujo recebimento voltou
	 * 
	 * @param amostraItemExame
	 * @param extratoAmostra
	 * @return
	 */
	public boolean atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(final AelAmostraItemExames amostraItemExame,
			final DominioSituacaoAmostra situacao, String nomeMicrocomputador) throws BaseException {
		return this.getListarAmostrasSolicitacaoRecebimentoRN().atualizarAmostraItemExameVoltouSituacaoExtratoAmostra(amostraItemExame, situacao, nomeMicrocomputador);
	}
	

	/*
	 * Getters para RNs e DAOs
	 */

	protected ListarAmostrasSolicitacaoRecebimentoRN getListarAmostrasSolicitacaoRecebimentoRN(){
		return listarAmostrasSolicitacaoRecebimentoRN;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}
	
	protected AelExtratoAmostrasDAO getAelExtratoAmostrasDAO() {
		return aelExtratoAmostrasDAO;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}
	
	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}

	public AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}

	public AelOcorrenciaExameApDAO getAelOcorrenciaExameApDAO() {
		return aelOcorrenciaExameApDAO;
	}

	public AelPatologistaApDAO getAelPatologistaApDAO() {
		return aelPatologistaApDAO;
	}

	public AelExtratoExameApDAO getAelExtratoExameApDAO() {
		return aelExtratoExameApDAO;
	}

	public AelInformacaoClinicaApDAO getAelInformacaoClinicaApDAO() {
		return aelInformacaoClinicaApDAO;
	}

	public AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	public AelMacroscopiaApsDAO getAelMacroscopiaApsDAO() {
		return aelMacroscopiaApsDAO;
	}

	public AelDiagnosticoApsDAO getAelDiagnosticoApsDAO() {
		return aelDiagnosticoApsDAO;
	}


	public AelMaterialApDAO getAelMaterialApDAO() {
		return aelMaterialApDAO;
	}
}
