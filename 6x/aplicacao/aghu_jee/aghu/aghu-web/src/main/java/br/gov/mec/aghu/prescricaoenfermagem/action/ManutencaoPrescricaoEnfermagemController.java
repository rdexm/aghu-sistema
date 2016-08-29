package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

/**
 * Controller para a funcionalidade Manter Prescrição de Enfermagem. 
 * 
 * @author diego.pacheco
 *
 */

public class ManutencaoPrescricaoEnfermagemController extends ActionController {

	private static final String ENCERRAR_DIAGNOSTICOS = "prescricaoenfermagem-encerrarDiagnosticos";
	private static final String PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR = "prescricaomedica-selecionarPrescricaoConsultar";
	private static final String SELECIONAR_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-selecionarPrescricaoEnfermagem";
	private static final String MANTER_PRESCRICAO_CUIDADO = "prescricaoenfermagem-manterPrescricaoCuidado";
	private static final String SELECIONAR_DIAGNOSTICOS = "prescricaoenfermagem-selecionarDiagnosticos";
	private static final String INFORMAR_SINAIS_SINTOMAS = "prescricaoenfermagem-informarSinaisSintomas";
	private static final String PRESCRICAOMEDICA_SELECIONAR_CUIDADOS_ROTINA = "prescricaoenfermagem-selecionarCuidadosRotina";
	private static final long serialVersionUID = 1003903695419496338L;
	private static final Log LOG = LogFactory.getLog(ManutencaoPrescricaoEnfermagemController.class);
	private static final String RELATORIO_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-relatorioPrescricaoEnfermagem";
	private static final String ELABORACAO_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-elaboracaoPrescricaoEnfermagem";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private ICupsFacade cupsFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;	
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	@Inject
	private RelatorioPrescricaoEnfermagemController relatorioPrescricaoEnfermagemController;
	
	//@Out(required = false, scope = ScopeType.SESSION) TODO MIGRAÇÃO
	private PrescricaoMedicaVO prescMedicaVO;

	private Integer penSeqAtendimento;
	
	private Integer penSeq;
	
	private Boolean exibirConfirmacao = false;
	
	private String cabecalhoPrescricaoMedica;
	
	private EnumTipoImpressaoEnfermagem tipoImpressao;
	
	@Inject
	private DiagnosticoController diagnosticoController;
	
	@Inject
	private SinalSintomaController sinalSintomaController;
	

	public enum EnumTipoImpressaoEnfermagem {
		IMPRESSAO, SEM_IMPRESSAO;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		try {
			if (this.penSeqAtendimento != null && this.penSeq != null) {
				EpePrescricaoEnfermagemId prescricaoId = new EpePrescricaoEnfermagemId(this.penSeqAtendimento, this.penSeq);

				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(this.prescricaoEnfermagemFacade
					.buscarDadosCabecalhoPrescricaoEnfermagemVO(prescricaoId));

				List<CuidadoVO> listaCuidadoVO = null;
				listaCuidadoVO = this.prescricaoEnfermagemFacade.buscarCuidadosPrescricaoEnfermagem(prescricaoId, false);

				this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().setListaCuidadoVO(listaCuidadoVO);
				
				buscaItensPrescricaoMedica();
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	
	}
	
	/**
	 * Busca os itens relacionados à Prescrição Médica.
	 * O seq do atendimento é o mesmo para Prescrição de Enfermagem e Prescrição Médica
	 * 
	 * @throws AGHUNegocioException
	 */
	private void buscaItensPrescricaoMedica() throws ApplicationBusinessException{
				List<MpmPrescricaoMedica> listaPrescricaoMedica = this.prescricaoMedicaFacade
					.pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(
							this.penSeqAtendimento, this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getDthrFim());

				MpmPrescricaoMedica prescricaoMedica = null;
				
				if (!listaPrescricaoMedica.isEmpty()) {
					prescricaoMedica = listaPrescricaoMedica.get(0);
				}
				
				MpmPrescricaoMedicaId pemId = null;
					
				if (prescricaoMedica != null) {
					pemId = prescricaoMedica.getId();
					this.prescMedicaVO = this.prescricaoMedicaFacade.buscarDadosCabecalhoPrescricaoMedicaVO(pemId);

					List<ItemPrescricaoMedicaVO> itens = null;
					itens = this.prescricaoMedicaFacade.buscarItensPrescricaoMedica(pemId, false);
					
					List<ItemPrescricaoMedicaVO> itensNaoPendentes = new ArrayList<ItemPrescricaoMedicaVO>();
					for(ItemPrescricaoMedicaVO item : itens) {
						if(DominioIndPendenteItemPrescricao.N.equals(item.getSituacao())
								|| DominioIndPendenteItemPrescricao.A.equals(item.getSituacao())
								|| DominioIndPendenteItemPrescricao.E.equals(item.getSituacao())) {
							itensNaoPendentes.add(item);
						}
					}

					this.prescMedicaVO.setItens(itensNaoPendentes);
			//TODO Migração		
					//if(prescMedicaVO != null && prescMedicaVO.getPrescricaoMedica() != null){
						//this.prescricaoEnfermagemFacade.refresh(prescMedicaVO.getPrescricaoMedica());
					//}

					if(itensNaoPendentes.size() > 0) {
						cabecalhoPrescricaoMedica = montarCabecalhoPrescricaoMedica();
					} else {
						prescMedicaVO = null;
					}
				}else{
					this.prescMedicaVO = null;
				}
			} 

	/**
	 * Método que obtem o estilo da coluna dos ítens da tabela
	 * 
	 * @param cuidadoVO
	 * @return
	 */
	public String obterEstiloColuna(CuidadoVO cuidadoVO) {
		String retorno = "";
		if (cuidadoVO != null && cuidadoVO.getExcluir() != null && cuidadoVO.getExcluir()) {
			retorno = "background-color:#FF6347;";
		}
		return retorno;// + "max-width: 300px; word-wrap: break-word;";
	}	

	/**
	 * Retorna cabeçalho (descrição) formatado para
	 * identificar tabela de itens de Prescrição Médica.
	 * 
	 * @return
	 */
	public String montarCabecalhoPrescricaoMedica() {
		StringBuilder sb = new StringBuilder();		
		if (prescMedicaVO != null){
			String descricao = this.getBundle().getString("LABEL_PRESCRICAO_ENFERMAGEM_CABECALHO_PRESCRICAO_MEDICA");
			descricao = descricao.replace("{0}", 
					DateFormatUtil.fomataDiaMesAno(prescMedicaVO.getDthrInicio()) 
					+ " " + DateFormatUtil.formataHoraMinuto(prescMedicaVO.getDthrInicio()));
			descricao = descricao.replace("{1}", 
					DateFormatUtil.fomataDiaMesAno(prescMedicaVO.getDthrFim()) 
					+ " " + DateFormatUtil.formataHoraMinuto(prescMedicaVO.getDthrFim()));
			sb.append(descricao);
		}
		return sb.toString();
	}

	/**
	 * Desabilita botão Cuidado Rotina caso a prescrição 
	 * de enfermagem não possua nenhum cuidado associado.
	 * 
	 * @return
	 */
	public Boolean getDesabilitaCuidadoRotina() {
		if (this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO() != null && this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO().isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Chama método que verifica se existe algum item na lista de itens da
	 * prescrição que devem ser excluido, caso exista chama o metodo de exclusão
	 * e remove o objeto da lista.
	 */
	public void removerCuidadosSelecionados() {
		try {			
			prescricaoEnfermagemFacade.removerCuidadosSelecionados(this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO());	
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}
	
	public Boolean getDesabilitaBotaoRemoverCuidados() {
		if (this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO() != null) {
			for (CuidadoVO cuidadoVO : this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO()) {
				if (cuidadoVO.getExcluir() != null && cuidadoVO.getExcluir()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Cancela a prescrição
	 * 
	 * @return
	 */
	public String cancelarPrescricao() {
		try {			
			exibirConfirmacao = false;
			
		//TODO
			//if(prescMedicaVO != null && prescMedicaVO.getPrescricaoMedica() != null){
				//this.prescricaoEnfermagemFacade.evict(prescMedicaVO.getPrescricaoMedica());
		//	}
			
			this.prescricaoEnfermagemFacade.cancelaPrescricaoEnfermagem(penSeqAtendimento, penSeq);
			this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(null);
			this.prescMedicaVO = null;
			return ELABORACAO_PRESCRICAO_ENFERMAGEM;
		} catch (BaseException e) {
			LOG.error("Erro",e);
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	/**
	 * Solicita modal de confirmação para o cancelamento
	 */
	public void solicitarConfirmacao() {
		exibirConfirmacao = true;
		openDialog("modalConfirmacaoWG");
	}	
	
	/**
	 * Cancela a modal de confirmação
	 */
	public void cancelarModal() {
		exibirConfirmacao = false;
		closeDialog("modalConfirmacaoWG");
	}

	/**
	 * Ação do botão "Confirmar SEM Impressão"
	 * 
	 * @return
	 * @throws BaseException 
	 * @throws JRException 
	 */
	public String confirmarPrescricaoEnfermagemSemImpressao()
			throws BaseException, JRException {
		this.tipoImpressao = EnumTipoImpressaoEnfermagem.SEM_IMPRESSAO;
		return this.confirmarPrescricaoEnfermagem(false);
	}
	
	/**
	 * Ação do botão "Confirmar COM Impressão"
	 * 
	 * @return
	 * @throws BaseException 
	 * @throws JRException 
	 */
	public String confirmarPrescricaoEnfermagemComImpressao()
			throws BaseException, JRException {
		this.tipoImpressao = EnumTipoImpressaoEnfermagem.IMPRESSAO;
		return this.confirmarPrescricaoEnfermagem(false);
	}
	
	/**
	 * Método principal invocado após a confirmação da Prescrição Enfermagem.
	 * 
	 * @return String - Redireciona para tela que abre abas com PDFs.
	 * @throws JRException 
	 */
	private String confirmarPrescricaoEnfermagem(boolean bImprimir) throws JRException{
		String retorno = null;
		try{
			// Verifica se a lista está vazia, se estiver não faz a impressão nem confirma.
			prescricaoEnfermagemFacade.verificaListaCuidadosVO(this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO());
			
		//TODO migração
		//	if(prescMedicaVO != null && prescMedicaVO.getPrescricaoMedica() != null){
			//	this.prescricaoEnfermagemFacade.evict(prescMedicaVO.getPrescricaoMedica());
			//}
			
			prescricaoEnfermagemFacade.confirmarPrescricaoEnfermagem(this.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem());
			
			buscaItensPrescricaoMedica();
			
			// Lança evento de impressão direta que será tratado por todos os
			// controllers dos relatórios.
			LOG.info("Levantando evento para impressão direta.");
			
			this.relatorioPrescricaoEnfermagemController.setTipoImpressao(this.tipoImpressao);
			this.relatorioPrescricaoEnfermagemController.setPrescricaoEnfermagemVO(this.getPrescricaoEnfermagemVO());
			this.relatorioPrescricaoEnfermagemController.directPrint();
			
			if(bImprimir) {
				if(cupsFacade.isCupsAtivo()){
					retorno = ELABORACAO_PRESCRICAO_ENFERMAGEM;
				}else{
					retorno = RELATORIO_PRESCRICAO_ENFERMAGEM;
				}
			}else {
				retorno = ELABORACAO_PRESCRICAO_ENFERMAGEM;
			}
			this.apresentarMsgNegocio(Severity.INFO, "MSG_CONCLUSAO_PRESCRICAO_ENFERMAGEM");
		}catch (BaseException e){
			EpePrescricaoEnfermagemId id = this.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().getId();
			EpePrescricaoEnfermagem epePrescricaoEnfermagem = this.prescricaoEnfermagemFacade.obterPrescricaoEnfermagemPorId(id);
			this.getPrescricaoEnfermagemVO().setPrescricaoEnfermagem(epePrescricaoEnfermagem);
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}
	
	public String deixarPrescricaoPendente(){
		try{
			
			//TODO migração
			//if(prescMedicaVO != null && prescMedicaVO.getPrescricaoMedica() != null){
			//	this.prescricaoEnfermagemFacade.evict(prescMedicaVO.getPrescricaoMedica());
			//}
			
			this.prescricaoEnfermagemFacade
					.confirmarPendentePrescricaoEnfermagem(this
							.getPrescricaoEnfermagemVO()
							.getPrescricaoEnfermagem());

			buscaItensPrescricaoMedica();
			
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_PENDENTE_PRESC_ENFERMAGEM");
		}catch(BaseException e){
			this.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().setSituacao(DominioSituacaoPrescricao.U);
			this.apresentarMsgNegocio(Severity.INFO, "ERRO_PENDENTE_PRESC_ENFERMAGEM");
			apresentarExcecaoNegocio(e);
		}
		return ELABORACAO_PRESCRICAO_ENFERMAGEM;
	}
	
	
	public String informarSinaisSintomas(){
		sinalSintomaController.limparPesquisa();
		return INFORMAR_SINAIS_SINTOMAS; 
	}
	
	public String selecionarDiagnosticos(){
		diagnosticoController.limparPesquisa();
		return SELECIONAR_DIAGNOSTICOS; 
	}
	
	public String manterPrescricaoCuidado(){
		return MANTER_PRESCRICAO_CUIDADO; 
	}
	
	public String selecionarPrescricaoEnfermagem(){
		return SELECIONAR_PRESCRICAO_ENFERMAGEM; 
	}
	
	public String selecionarPrescricaoConsultar(){
		return PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR; 
	}
	
	public String selecionarCuidadosRotina(){
		return PRESCRICAOMEDICA_SELECIONAR_CUIDADOS_ROTINA; 
	}
	
	public String encerrarDiagnosticos(){
		return ENCERRAR_DIAGNOSTICOS;
	}
	
	public String manterCuidadoEnfermagem(){
		return "prescricaoenfermagem-manterCuidadoEnfermagem"; //TODO Arquivo xhtml não encontrado, mas o botão nunca é renderizado
	}
	
	/**
	 * Método para verificar se exite cuidado para a prescrição
	 * 
	 * @return Boolean informando se existe ou não cuidados para a prescrição
	 */	
	public Boolean getExisteCuidadosPrescritos(){
		Boolean retorno = false;
		if (this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO() != null && this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO() != null && !this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO().isEmpty()){
			retorno = true;
		}
		return retorno;
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(
			IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO();
	}

	public void setPrescricaoEnfermagemVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(prescricaoEnfermagemVO);
	}

	public Integer getPenSeqAtendimento() {
		return penSeqAtendimento;
	}

	public void setPenSeqAtendimento(Integer penSeqAtendimento) {
		this.penSeqAtendimento = penSeqAtendimento;
	}

	public Integer getPenSeq() {
		return penSeq;
	}

	public void setPenSeq(Integer penSeq) {
		this.penSeq = penSeq;
	}

	public Boolean getExibirConfirmacao() {
		return exibirConfirmacao;
	}

	public void setExibirConfirmacao(Boolean exibirConfirmacao) {
		this.exibirConfirmacao = exibirConfirmacao;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public PrescricaoMedicaVO getPrescMedicaVO() {
		return prescMedicaVO;
	}

	public void setPrescMedicaVO(PrescricaoMedicaVO prescMedicaVO) {
		this.prescMedicaVO = prescMedicaVO;
	}
	

	public void setCabecalhoPrescricaoMedica(String cabecalhoPrescricaoMedica) {
		this.cabecalhoPrescricaoMedica = cabecalhoPrescricaoMedica;
	}	
	
	public String getCabecalhoPrescricaoMedica() {
		return cabecalhoPrescricaoMedica;
	}
	
}
