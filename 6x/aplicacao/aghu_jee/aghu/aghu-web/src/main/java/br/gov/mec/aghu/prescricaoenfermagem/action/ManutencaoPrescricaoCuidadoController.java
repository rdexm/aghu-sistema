package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author diego.pacheco
 *
 */

public class ManutencaoPrescricaoCuidadoController extends ActionController {

	private static final String ERRO = "Erro";

	private static final String SELECIONAR_DIAGNOSTICOS = "prescricaoenfermagem-selecionarDiagnosticos";

	private static final String INFORMAR_SINAIS_SINTOMAS = "prescricaoenfermagem-informarSinaisSintomas";

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManutencaoPrescricaoCuidadoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4434388367067411272L;
	
	private static final String STYLE_AMARELO = " background-color:#FFFF66; ";
	private static final String STYLE_VERMELHO = " background-color:#FF6347; ";
	private static final String STYLE_AZUL = " background-color:#51A2FF; ";
	private static final String STYLE_PADRAO = " max-width: 300px; word-wrap: break-word; ";
	
	private Integer penSeqAtendimento;
	
	private Integer penSeq;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;	
	
	//@Inject
	//private PrescricaoEnfermagemVO prescricaoEnfermagemVO;	
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	@Inject
	private SinalSintomaController sinalSintomaController;

	private String cameFrom;
	
	private CuidadoVO cuidadoVO;
	
	private String informacoesAdicionais;
	
	private String copiaInformacoesAdicionais;
	
	private Boolean exibeModalEditarInfoAdicional;
	
	private List<CuidadoVO> listaCuidadoVO  = new ArrayList<CuidadoVO>();
	
	private List<CuidadoVO> listaCuidadoDuplicadoVO  = new ArrayList<CuidadoVO>();
	
	private Boolean exibirModalPendente = Boolean.FALSE;;
	
	private Boolean exibirPendentes  = Boolean.FALSE;;
	
	private String acaoSairPrescricaoCuidado;
	
	private String titleModalPendente;
	
	private String messageModalPendente;
	
	private String descricoesTipoAprazamentoSemNumeroFrequencia;
	
	private int idConversacaoAnterior;
	
	private Boolean ocorreuErro = Boolean.FALSE;
	
	private MpmTipoFrequenciaAprazamento tpFreqAprazamento;

	public enum EnumPrescricaoCuidado{
		LABEL_PRESCREVER_SINAL_SINTOMA, LABEL_PRESCREVER_DIAGNOSTICO,
		LABEL_CONCLUIR, LABEL_CANCELAR,	MESSAGE_CUIDADO_ALTERADO_CONCLUIR,
		MESSAGE_CUIDADO_ALTERADO_SINAL_SINTOMA, MESSAGE_CUIDADO_ALTERADO_DIAGNOSTICO,
		MESSAGE_CUIDADO_ALTERADO_CANCELAR;
	}

	public void iniciar() {
	 

		this.listaCuidadoVO.clear();
		descricoesTipoAprazamentoSemNumeroFrequencia = montarDescricoesTipoAprazamentoSemNumeroFrequencia();
		try {		
			
//			if (SELECIONAR_DIAGNOSTICOS.equals(this.cameFrom) || "selecionarCuidadosRotina".equals(this.cameFrom)){
			if (SELECIONAR_DIAGNOSTICOS.equals(this.cameFrom) ){
				if (this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO()!= null) {
					this.penSeq = this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().getId().getSeq();
					this.penSeqAtendimento = this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().getId().getAtdSeq();
					
					List <Short> cuidadosInseridos = new ArrayList<Short>();
					for (CuidadoVO cuidadoVO : this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO()) {
						
						if(cuidadoVO.getMarcado()) {							
							if (this.prescricaoEnfermagemFacade.verificarCuidadoJaExistePrescricao(cuidadoVO.getCuidado().getSeq(),
									this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().getId())){
								
								this.apresentarMsgNegocio(Severity.WARN, "MSG_CUIDADO_JA_EXISTE_PRESCRICAO",cuidadoVO.getCuidado().getSeq());
								
							}else{
								this.desmarcarCuidado(cuidadoVO);
								//cuidadoVO.setMarcado(Boolean.FALSE);
								cuidadoVO.setPrescricaoEnfermagem(this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem());
								cuidadoVO.setPrescricaoCuidado(new EpePrescricoesCuidados());
								cuidadoVO.setCuidado(cuidadoVO.getCuidado());
								cuidadoVO.setDescricao(cuidadoVO.getCuidado().getDescricao());
								cuidadoVO.setListaTipoFrequenciaAprazamento(prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(true, null));
								cuidadoVO.setEmEdicao(Boolean.TRUE);

								//Controle de cuidado duplicado, exibir apenas um mas gravar as duas etiologias
								if (cuidadosInseridos.contains(cuidadoVO.getCuidado().getSeq())){
									this.listaCuidadoDuplicadoVO.add(cuidadoVO);
								}else{
									this.listaCuidadoVO.add(cuidadoVO);
								}
								cuidadosInseridos.add(cuidadoVO.getCuidado().getSeq());
							}
						}
					}
				}
				
				
				if (this.listaCuidadoVO.size()>1){
					this.apresentarMsgNegocio(Severity.INFO,
							"MSG_CUIDADOS_ENCONTRADOS", this.listaCuidadoVO.size());
				}
				if (this.listaCuidadoVO.size()==1){
					this.apresentarMsgNegocio(Severity.INFO,
							"MSG_CUIDADO_ENCONTRADO");
				}	
				
			}else if (MANTER_PRESCRICAO_ENFERMAGEM.equals(this.cameFrom) 
					&& this.penSeqAtendimento != null && this.penSeq != null) {
					
				EpePrescricaoEnfermagemId prescricaoId = new EpePrescricaoEnfermagemId(
						this.penSeqAtendimento, this.penSeq);

				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO( this.prescricaoEnfermagemFacade
						.buscarDadosCabecalhoPrescricaoEnfermagemVO(prescricaoId));
				
				this.listaCuidadoVO = this.prescricaoEnfermagemFacade.pesquisarCuidadosPrescEnfermagem(this.penSeq, this.penSeqAtendimento
										,this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getPrescricaoEnfermagem().getDthrFim());
			
			}
			this.exibeModalEditarInfoAdicional = false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	private void desmarcarCuidado(CuidadoVO cuidadoVO){
		for (CuidadoVO cuidVO : this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO()) {
			if (cuidVO.equals(cuidadoVO)) {
				cuidVO.setMarcado(Boolean.FALSE);	
			}
		}
	}
	
	public Boolean getDesabilitaBotaoRemoverCuidados() {
		Boolean retorno = Boolean.TRUE;
		if (prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO() != null) {
			for (CuidadoVO cuidadoVO : this.listaCuidadoVO) {
				if (cuidadoVO.getExcluir() != null && cuidadoVO.getExcluir()) {
					retorno = Boolean.FALSE;
				}
			}
		}
		return retorno;
	}
	
	public String verificarExibeModalAlteracaoPendente(){
		Boolean existeCuidadoEmEdicao = this.verificaExisteCuidadoEmEdicao();
		if (existeCuidadoEmEdicao){
			this.exibirPendentes = Boolean.TRUE; 
			this.exibirModalPendente = Boolean.TRUE;
			if("concluir".equals(this.acaoSairPrescricaoCuidado)){
				this.titleModalPendente = this.getMessage(EnumPrescricaoCuidado.LABEL_CONCLUIR);
				this.messageModalPendente = this.getMessage(EnumPrescricaoCuidado.MESSAGE_CUIDADO_ALTERADO_CONCLUIR);
			}else if("prescreverSinalSintoma".equals(this.acaoSairPrescricaoCuidado)){
				this.titleModalPendente = this.getMessage(EnumPrescricaoCuidado.LABEL_PRESCREVER_SINAL_SINTOMA);
				this.messageModalPendente = this.getMessage(EnumPrescricaoCuidado.MESSAGE_CUIDADO_ALTERADO_SINAL_SINTOMA);
			}else if("prescreverDiagnostico".equals(this.acaoSairPrescricaoCuidado)){
				this.titleModalPendente = this.getMessage(EnumPrescricaoCuidado.LABEL_PRESCREVER_DIAGNOSTICO);
				this.messageModalPendente = this.getMessage(EnumPrescricaoCuidado.MESSAGE_CUIDADO_ALTERADO_DIAGNOSTICO);
			}else if("cancelar".equals(this.acaoSairPrescricaoCuidado)){
				this.titleModalPendente = this.getMessage(EnumPrescricaoCuidado.LABEL_CANCELAR);
				this.messageModalPendente = this.getMessage(EnumPrescricaoCuidado.MESSAGE_CUIDADO_ALTERADO_CANCELAR);
			}
			openDialog("modalAlteracaoPendenteWG");
		}else{
			String ret = this.confirmarAlteracaoPendente();
			openDialog("modalAlteracaoPendenteWG");
			return ret;
		}
		return null;
	}
	
	private Boolean verificaExisteCuidadoEmEdicao(){
		Boolean retorno = Boolean.FALSE;
		for (CuidadoVO cuidadoVO : this.listaCuidadoVO){
			if (cuidadoVO.getEmEdicao()){
				retorno = Boolean.TRUE;
				break;
			}
		}			
		return retorno;
	}
	
	public String confirmarAlteracaoPendente(){
		String retorno = null;
		if(acaoSairPrescricaoCuidado.equals("concluir")){
			retorno = MANTER_PRESCRICAO_ENFERMAGEM;
		} else if(acaoSairPrescricaoCuidado.equals("prescreverSinalSintoma")){
			retorno = INFORMAR_SINAIS_SINTOMAS;
		} else if(acaoSairPrescricaoCuidado.equals("prescreverDiagnostico")){
			retorno = SELECIONAR_DIAGNOSTICOS;
		} else if(acaoSairPrescricaoCuidado.equals("cancelar")){
			retorno = MANTER_PRESCRICAO_ENFERMAGEM;
		}
		else{
			retorno = this.acaoSairPrescricaoCuidado;
		}
		return retorno;
	}
	
	public void cancelarAlteracaoPendente(){
		this.acaoSairPrescricaoCuidado = null;
		this.titleModalPendente = null;
		this.messageModalPendente = null;
		this.exibirModalPendente = Boolean.FALSE;
	}
	
	public void carregarInfoAdicional(CuidadoVO cuidadoVO) {
		this.cuidadoVO = cuidadoVO;
		this.informacoesAdicionais = cuidadoVO.getPrescricaoCuidado().getDescricao();
		this.copiaInformacoesAdicionais = informacoesAdicionais;
		this.exibeModalEditarInfoAdicional = Boolean.TRUE;
		openDialog("modalEditarInfoAdicionalWG");
	}
	
	public void confirmarEdicaoInfoAdicional() {
		this.cuidadoVO.getPrescricaoCuidado().setDescricao(informacoesAdicionais);
		this.informacoesAdicionais = null;
		this.cuidadoVO.setEmEdicao(Boolean.TRUE);
		this.exibeModalEditarInfoAdicional = Boolean.FALSE;
	}
	
	public void cancelarEdicaoInfoAdicional() {
		// Restaura informacoesAdicionais da cópia de informacoes adicionais
		this.cuidadoVO.getPrescricaoCuidado().setDescricao(copiaInformacoesAdicionais);
		this.informacoesAdicionais = null;
		this.exibeModalEditarInfoAdicional = Boolean.FALSE;
	}
	
	/**
	 * Descricao exibida na combo para selecao de tipoFrequenciaAprazamento.
	 * 
	 * @param tipoFrequenciaAprazamento
	 * @param frequencia
	 * @return
	 */
	public String obterDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento, Short frequencia) {
		
		if (tipoFrequenciaAprazamento != null) {
			if (tipoFrequenciaAprazamento.getSintaxe() == null || frequencia == null) {
				return tipoFrequenciaAprazamento.getDescricao();
			}
			else {
				return tipoFrequenciaAprazamento.getSintaxe().replace("#", String.valueOf(frequencia)).toUpperCase();
			}
		}
		
		return "";
	}
	
	public void rePopularComboTipoFreqAprazamento(CuidadoVO cuidVO){
		this.cuidadoVO = cuidVO;
		this.popularComboTipoFreqAprazamento(cuidadoVO);
		this.marcarCuidadoEmEdicao(cuidadoVO);
	}
	
	private void popularComboTipoFreqAprazamento(CuidadoVO cuidadoVO) {
		if (cuidadoVO.getPrescricaoCuidado().getFrequencia()==null){
			cuidadoVO.setListaTipoFrequenciaAprazamento(prescricaoMedicaFacade
					.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(true, null));
		}else{
			cuidadoVO.setListaTipoFrequenciaAprazamento(prescricaoMedicaFacade
					.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(false, null));
		}
	}
	
	private String montarDescricoesTipoAprazamentoSemNumeroFrequencia() {
		StringBuilder sb = new StringBuilder();
		List<MpmTipoFrequenciaAprazamento> listaTipoFreqApraz = 
			prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(true, null);
		for (Iterator<MpmTipoFrequenciaAprazamento> iterator = listaTipoFreqApraz.iterator(); iterator
				.hasNext();) {
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = (MpmTipoFrequenciaAprazamento) iterator
					.next();
			sb.append(tipoFrequenciaAprazamento.getDescricao());
			if (iterator.hasNext()) {
				sb.append(',');	
			}
		} 
		return sb.toString();
	}
	
	public void marcarCuidadoEmEdicao(CuidadoVO cuidadoVO) {
		List<CuidadoVO> listaCuidadoVO = this.listaCuidadoVO;
		for (CuidadoVO cuidVO : listaCuidadoVO) {
			if (cuidVO.equals(cuidadoVO)) {
				cuidVO.setEmEdicao(Boolean.TRUE);	
			}
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
		if(cuidadoVO != null){
			if (this.exibirPendentes && cuidadoVO.getEmEdicao() !=null && cuidadoVO.getEmEdicao()) {
				retorno = STYLE_AMARELO;
			}else if (cuidadoVO.getContemErro() != null && cuidadoVO.getContemErro()) {
				retorno = STYLE_VERMELHO;
			}else if(cuidadoVO.getMergeRealizado() != null && cuidadoVO.getMergeRealizado()){
				retorno = STYLE_AZUL;
			}
		}
		return retorno + STYLE_PADRAO;
	}	
	
	public String gravar() {
		
		this.ocorreuErro = Boolean.FALSE;
		
		for(CuidadoVO cuidadoVO : listaCuidadoVO){
			if (cuidadoVO.getPrescricaoCuidado().getId()!=null 
					&& cuidadoVO.getPrescricaoCuidado().getId().getSeq()!=null){
				alterarCuidado(cuidadoVO);
			}else{
				inserirCuidado(cuidadoVO);
			}
		}
		
		if(this.ocorreuErro){
			return null;//"";
		}
		sinalSintomaController.limparPesquisa();
		return confirmarAlteracaoPendente();//this.acaoSairPrescricaoCuidado;
	}
	
	public void gravar(CuidadoVO cuidadoVO) {
		if (cuidadoVO.getPrescricaoCuidado().getId()!=null 
				&& cuidadoVO.getPrescricaoCuidado().getId().getSeq()!=null){
			alterarCuidado(cuidadoVO);
		}else{
			inserirCuidado(cuidadoVO);
		}
	}

	public void removerCuidadosSelecionados() {
		try {			
			// O método a seguir além de remover do banco, também remove da lista de CuidadoVO
			this.prescricaoEnfermagemFacade.removerCuidadosSelecionados(this.listaCuidadoVO);
		} catch (BaseException me) {
			apresentarExcecaoNegocio(me);
			LOG.error(ERRO,me);
		}
	}
	
	public void inserirCuidado(CuidadoVO cuidadoVO){
		this.cuidadoVO = cuidadoVO;
		try {
			
			EpePrescricoesCuidados prescricaoCuidado = cuidadoVO.getPrescricaoCuidado();
			prescricaoCuidado.setPrescricaoEnfermagem(cuidadoVO.getPrescricaoEnfermagem());
			prescricaoCuidado.setCuidado(cuidadoVO.getCuidado());
			prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.P);
			prescricaoCuidado.setAtendimento(cuidadoVO.getPrescricaoEnfermagem().getAtendimento());

			this.prescricaoEnfermagemFacade.inserirPrescricaoCuidado(prescricaoCuidado, cuidadoVO.getDescricaoPrescricaoCuidadoBundle());

			if (!cuidadoVO.getCuidado().getIndRotina()){
				this.prescricaoEnfermagemFacade.inserirPrescCuidDiagnostico(prescricaoCuidado, cuidadoVO);
				for(CuidadoVO cuidadoDuplicadoVO : this.listaCuidadoDuplicadoVO){
					if (cuidadoDuplicadoVO.getCuidado().getSeq().equals(cuidadoVO.getCuidado().getSeq())){
						this.prescricaoEnfermagemFacade
							.inserirPrescCuidDiagnostico(prescricaoCuidado, cuidadoDuplicadoVO);
					}
				}
			}
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_CUIDADO_GRAVADO_SUCESSO", cuidadoVO.getDescricaoPrescricaoCuidadoBundle());
			this.cuidadoVO.setMergeRealizado(Boolean.TRUE);
			this.cuidadoVO.setContemErro(Boolean.FALSE);
			this.cuidadoVO.setEmEdicao(Boolean.FALSE);
		} catch (BaseException me) {
			this.ocorreuErro = Boolean.TRUE;
			this.cuidadoVO.setMergeRealizado(Boolean.FALSE);
			this.cuidadoVO.setContemErro(Boolean.TRUE);
			apresentarExcecaoNegocio(me);
		}
		this.cuidadoVO = null;
	}
	
	public void alterarCuidado(CuidadoVO cuidadoVO){
		this.cuidadoVO = cuidadoVO;
		try {
			
			EpePrescricoesCuidados prescricaoCuidado = cuidadoVO.getPrescricaoCuidado();
			prescricaoCuidado = this.prescricaoEnfermagemFacade.alterarPrescricaoCuidado(prescricaoCuidado, cuidadoVO.getDescricaoPrescricaoCuidadoBundle(), this.ocorreuErro);
			
			cuidadoVO.setPrescricaoCuidado(prescricaoCuidado);
			
			this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_CUIDADO_GRAVADO_SUCESSO", cuidadoVO.getDescricaoPrescricaoCuidadoBundle());
//			this.cuidadoVO.setMergeRealizado(Boolean.TRUE);
//			this.cuidadoVO.setContemErro(Boolean.FALSE);
//			this.cuidadoVO.setEmEdicao(Boolean.FALSE);
		} catch (BaseException me) {
			this.ocorreuErro = Boolean.TRUE;
			this.cuidadoVO.setMergeRealizado(Boolean.FALSE);
			this.cuidadoVO.setContemErro(Boolean.TRUE);
			apresentarExcecaoNegocio(me);
		}
		this.cuidadoVO = null;
	}
	
	public void removerCuidado(List<CuidadoVO> cuidadosVO){
		try {		
			this.prescricaoEnfermagemFacade.removerCuidadosSelecionados(cuidadosVO);
		} catch (BaseException me) {
			apresentarExcecaoNegocio(me);
			LOG.error(ERRO,me);
		}
	}
	
	/**
	 * Método chamado pela suggestion de pesquisa por tipo de Aprazamento
	 * 
	 * @param objParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmTipoFrequenciaAprazamento> pesquisarTiposAprazamento(Object objParam) throws ApplicationBusinessException {
		
		// iniciar uma lista para tratar a descricao
		List<MpmTipoFrequenciaAprazamento> listaRetorno = null;
		
		
		if (objParam!=null && !"".equalsIgnoreCase((String) objParam)){
			listaRetorno =  prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamento((String) objParam);
		}
	
		else if (cuidadoVO!=null && cuidadoVO.getPrescricaoCuidado().getFrequencia()!=null){
			listaRetorno = cuidadoVO.getListaTipoFrequenciaAprazamento();
					
		}
		else {
			// fazer o for para setar a descricao formatada
			listaRetorno = prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(true, null);
		}
		
		if (listaRetorno!=null && listaRetorno.size()>0){
			for (MpmTipoFrequenciaAprazamento tpFreqAp : listaRetorno){
				if (tpFreqAp.getSintaxe()!=null){
					tpFreqAp.getDescricaoSintaxeFormatada(cuidadoVO.getPrescricaoCuidado().getFrequencia());
					tpFreqAp.setDescricaoFormatada(tpFreqAp.getDescricaoFormatada());
				}
				else {
					tpFreqAp.setDescricaoFormatada(tpFreqAp.getDescricao());
				}
			}
		}
		
		return listaRetorno;
		
	}
	
	
	/**
	 * Método contador chamado pela suggestion de pesquisa por tipo de Aprazamento
	 * 
	 * @param objParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarTiposAprazamentoCount(Object objParam) throws ApplicationBusinessException {
		
		// iniciar uma lista para tratar a descricao
		Long retorno = null;
		
		
		if (objParam!=null && !"".equalsIgnoreCase((String) objParam)){
			return  prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoCount((String) objParam);
		}
	
		if (cuidadoVO!=null && cuidadoVO.getPrescricaoCuidado().getFrequencia()!=null){
			retorno = Long.valueOf(cuidadoVO.getListaTipoFrequenciaAprazamento().size());
					
		}
		else {
			// fazer o for para setar a descricao formatada
			retorno = prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoDigitaFrequenciaCount(true, null);
		}
			
		return retorno;
		
	}
	
	public void selecionarCuidado(CuidadoVO cuidadoVO){
		this.cuidadoVO = cuidadoVO;
	}
	
	
	private String getMessage(EnumPrescricaoCuidado mensagem){
		return this.getBundle().getString(mensagem.toString());
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

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(
			IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO();
	}

	public void setPrescricaoEnfermagemVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(prescricaoEnfermagemVO);
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public CuidadoVO getCuidadoVO() {
		return cuidadoVO;
	}

	public void setCuidadoVO(CuidadoVO cuidadoVO) {
		this.cuidadoVO = cuidadoVO;
	}

	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	public String getCopiaInformacoesAdicionais() {
		return copiaInformacoesAdicionais;
	}

	public void setCopiaInformacoesAdicionais(String copiaInformacoesAdicionais) {
		this.copiaInformacoesAdicionais = copiaInformacoesAdicionais;
	}

	public Boolean getExibeModalEditarInfoAdicional() {
		return exibeModalEditarInfoAdicional;
	}

	public void setExibeModalEditarInfoAdicional(
			Boolean exibeModalEditarInfoAdicional) {
		this.exibeModalEditarInfoAdicional = exibeModalEditarInfoAdicional;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public List<CuidadoVO> getListaCuidadoVO() {
		return listaCuidadoVO;
	}

	public void setListaCuidadoVO(List<CuidadoVO> listaCuidadoVO) {
		this.listaCuidadoVO = listaCuidadoVO;
	}	
	
	public Boolean getExibirModalPendente() {
		return exibirModalPendente;
	}

	public void setExibirModalPendente(Boolean exibirModalPendente) {
		this.exibirModalPendente = exibirModalPendente;
	}

	public String getAcaoSairPrescricaoCuidado() {
		return acaoSairPrescricaoCuidado;
	}

	public void setAcaoSairPrescricaoCuidado(String acaoSairPrescricaoCuidado) {
		this.acaoSairPrescricaoCuidado = acaoSairPrescricaoCuidado;
	}

	public String getTitleModalPendente() {
		return titleModalPendente;
	}

	public void setTitleModalPendente(String titleModalPendente) {
		this.titleModalPendente = titleModalPendente;
	}

	public String getMessageModalPendente() {
		return messageModalPendente;
	}

	public void setMessageModalPendente(String messageModalPendente) {
		this.messageModalPendente = messageModalPendente;
	}

	public String getDescricoesTipoAprazamentoSemNumeroFrequencia() {
		return descricoesTipoAprazamentoSemNumeroFrequencia;
	}

	public void setDescricoesTipoAprazamentoSemNumeroFrequencia(
			String descricoesTipoAprazamentoSemNumeroFrequencia) {
		this.descricoesTipoAprazamentoSemNumeroFrequencia = descricoesTipoAprazamentoSemNumeroFrequencia;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public Boolean getOcorreuErro() {
		return ocorreuErro;
	}

	public void setOcorreuErro(Boolean ocorreuErro) {
		this.ocorreuErro = ocorreuErro;
	}
	
	public MpmTipoFrequenciaAprazamento getTpFreqAprazamento() {
		return tpFreqAprazamento;
	}

	public void setTpFreqAprazamento(MpmTipoFrequenciaAprazamento tpFreqAprazamento) {
		this.tpFreqAprazamento = tpFreqAprazamento;
	}	
	
}
