package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioLinguagemImpressora;
import br.gov.mec.aghu.dominio.DominioModoRelatorio;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnidsId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class ImpressoraPadraoUnidController extends ActionController {

	private static final String REGEX_CAMINHO_IMPRESSORA = "^\\\\{2}[\\w]+(\\\\[\\w]*)*$";

	/**
	 * 
	 */
	private static final long serialVersionUID = 261331007749288197L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;
	
	
	private AghImpressoraPadraoUnids impressoraPadrao = new AghImpressoraPadraoUnids();

	private List<AghImpressoraPadraoUnids> listaImpressorasUnidades = new ArrayList<AghImpressoraPadraoUnids>(
			0);

	private List<AghImpressoraPadraoUnids> listaImpressorasUnidadesOld = new ArrayList<AghImpressoraPadraoUnids>(
			0);

	private AghImpressoraPadraoUnidsId impressoraPadraoId;

	private AghUnidadesFuncionais unidadeFuncional;

	private TipoDocumentoImpressao cpTipoImpressao;

	private String cpNomeImpressao;

	private DominioModoRelatorio cpModoRelatorio;

	private DominioLinguagemImpressora cpTipoLinguagem;

	private Integer unfSeq;

	/**
	 * Impressora do CUPS.
	 */
	private ImpImpressora cpImpressora;

	/**
	 * Variável que indica o estado de edição de um AghImpressoraPadraoUnids.
	 */
	private Boolean isUpdate;
	
	private boolean formChanged;
	
	private boolean itemsChanged;
	
	private boolean confirmEditarRequired;

	private AghImpressoraPadraoUnids item2Edit;
	
	private boolean confirmVoltarRequired;
	
	private boolean confirmGravarRequired;
	
	private final String PAGE_UNIDADE_FUNCIONAL_LIST = "unidadeFuncionalList";

	@PostConstruct
	public void init() {
		begin(conversation);		
	}

	public void inicio() {
	 

		reset();
		setItemsChanged(false);
		
		//Metódo chamado pelo pages.xml ao carregar a view
		if (unidadeFuncional != null && unidadeFuncional.getSeq() != null) {
			
			this.listaImpressorasUnidades = new ArrayList<AghImpressoraPadraoUnids>(
					this.cadastrosBasicosInternacaoFacade.obterAghImpressoraPadraoUnids(unidadeFuncional.getSeq()));
			this.listaImpressorasUnidadesOld = new ArrayList<AghImpressoraPadraoUnids>(
					this.cadastrosBasicosInternacaoFacade.obterAghImpressoraPadraoUnids(unidadeFuncional.getSeq()));
			this.cpNomeImpressao = null;
			this.cpModoRelatorio = null;
			this.cpTipoImpressao = null;
			this.cpTipoLinguagem = null;
			this.cpImpressora = null;
			this.isUpdate = false;
		}else{
			this.setListaImpressorasUnidades(new ArrayList<AghImpressoraPadraoUnids>(0)) ;
			this.setListaImpressorasUnidadesOld(new ArrayList<AghImpressoraPadraoUnids>(0));
			
		}
		
	
	}
	
	/**
	 * Método para confirmar as impressoras editadas/adicionas e voltar para
	 * tela de pesquisa.
	 * 
	 * @return
	 */
	public String confirmar() {
		//Persistir ou atualizar dados
		if(this.listaImpressorasUnidades != null || this.listaImpressorasUnidadesOld != null){
			try {
			
				cadastrosBasicosInternacaoFacade.incluirImpressora(this.listaImpressorasUnidades,
						this.listaImpressorasUnidadesOld, this.unidadeFuncional);
		        
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CADASTRO_IMPRESSORA");
			
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		
			reset();
			this.impressoraPadrao = new AghImpressoraPadraoUnids();
			this.listaImpressorasUnidades = null;
			this.listaImpressorasUnidadesOld = null;
			this.cpNomeImpressao = null;
			this.cpModoRelatorio = null;
			this.cpTipoImpressao = null;
			this.cpTipoLinguagem = null;
			this.cpImpressora = null;
			
			this.isUpdate = false;
		}
		
		return PAGE_UNIDADE_FUNCIONAL_LIST;
	}
	
	/**
	 * Grava dados descartando alterações não salvas.
	 * 
	 * @return
	 */
	public String gravarEDescartarAlteracoes() {
		reset();
		return confirmar();
	}

	/**
	 * Metodo invocado ao clicar na edição de impressora.
	 * 
	 * @param impressora
	 */
	public void editarImpressora(AghImpressoraPadraoUnids impressora) {
		this.isUpdate = true;
		
		this.cpModoRelatorio = impressora.getModoRelatorio();
		this.cpNomeImpressao = impressora.getNomeImpressora();
		this.cpTipoImpressao = impressora.getTipoImpressao();
		this.cpTipoLinguagem = impressora.getTipoLinguagem();
		this.cpImpressora = impressora.getImpImpressora();
	
		this.impressoraPadrao = impressora;
	}
	
	/**
	 * Edita outra impressora, ignorando o item editado no momento.
	 */
	public void editarOutroItem() {
		reset();
		editarImpressora(item2Edit);
		item2Edit = null;
	}
	
	/**
	 * Método invocado ao confirmar a edição de uma impressora.
	 */
	public void confirmarEdicao(){
	    if (cpImpressora == null  && StringUtils.isEmpty(cpNomeImpressao)){
		    apresentarMsgNegocio("MSG_PREENCHER_INFO_IMPRESSORA");
	    } else {
		this.impressoraPadrao.setModoRelatorio(cpModoRelatorio);
		this.impressoraPadrao.setNomeImpressora(cpNomeImpressao);
		this.impressoraPadrao.setTipoImpressao(cpTipoImpressao);
		this.impressoraPadrao.setTipoLinguagem(cpTipoLinguagem);
		this.impressoraPadrao.setImpImpressora(cpImpressora);
		
		if(!this.listaImpressorasUnidades.contains(impressoraPadrao)){
			this.listaImpressorasUnidades.add(impressoraPadrao);
		}
				
		// Limpando a entidade

		this.impressoraPadrao = new AghImpressoraPadraoUnids();

		this.cpNomeImpressao = null;
		this.cpModoRelatorio = null;
		this.cpTipoImpressao = null;
		this.cpTipoLinguagem = null;
		this.cpImpressora = null;

		apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_IMPRESSORA");
		setItemsChanged(true);
		
		this.isUpdate = false;
	    }
	}
	
	public void cancelarEdicao(){
		this.impressoraPadrao = new AghImpressoraPadraoUnids();
		this.cpNomeImpressao = null;
		this.cpModoRelatorio = null;
		this.cpTipoImpressao = null;
		this.cpTipoLinguagem = null;
		this.cpImpressora = null;
		this.setFormChanged(false);
		this.setIsUpdate(false);
	}
	
	/**
	 * Método invocado para adicionar nova impressora.
	 */
	public void associarImpressoras(){
		if (cpImpressora == null  && StringUtils.isEmpty(cpNomeImpressao)){
		    apresentarMsgNegocio("MSG_PREENCHER_INFO_IMPRESSORA");
		} else if (cpNomeImpressao != null && !cpNomeImpressao.matches(REGEX_CAMINHO_IMPRESSORA)){
			apresentarMsgNegocio("MSG_ERRO_FORMATO_CAMINHO_IMPRESSORA");
		} else if(this.impressoraPadrao == null || this.impressoraPadrao.getId() == null ){
			
			this.impressoraPadrao = new AghImpressoraPadraoUnids();
			
			AghImpressoraPadraoUnidsId id = new AghImpressoraPadraoUnidsId();
			
			if(this.unidadeFuncional != null){
				id.setUnfSeq(unidadeFuncional.getSeq());
			}
			impressoraPadrao.setNomeImpressora(cpNomeImpressao);
			impressoraPadrao.setTipoImpressao(cpTipoImpressao);
			impressoraPadrao.setModoRelatorio(cpModoRelatorio);
			impressoraPadrao.setTipoLinguagem(cpTipoLinguagem);
			impressoraPadrao.setImpImpressora(cpImpressora);
			

			try {
				if(cpTipoImpressao != null) {
					cadastrosBasicosInternacaoFacade.validaCamposImpressoraPadrao(cpTipoImpressao);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
					
			
			if(impressoraPadrao.getTipoImpressao() != null && this.unidadeFuncional != null){
				if(impressoraPadrao.getTipoImpressao() != null){
					this.listaImpressorasUnidades.add(impressoraPadrao);
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ADICIONAR_IMPRESSORA",
							this.unidadeFuncional.getDescricao());
					setItemsChanged(true);
				}
			}
			
			// Limpando a entidade
			
			this.impressoraPadrao = new AghImpressoraPadraoUnids();
			
			this.cpNomeImpressao = null;
			this.cpModoRelatorio = null;
			this.cpTipoImpressao = null;
			this.cpTipoLinguagem = null;
			this.cpImpressora = null;
			setFormChanged(false);
			
		}
		
	}

	/**
	 * Remover Impressoras
	 */
	public void removerImpressora(AghImpressoraPadraoUnids impressora) {
		if (this.listaImpressorasUnidades.contains(impressora)) {
			this.listaImpressorasUnidades.remove(impressora);
			setItemsChanged(true);
		}
	}
	
	/**
	 * Botão cancelar.
	 * 
	 * @return
	 */
	public String cancelar() {
		if (isFormChanged() || isItemsChanged()) {
			setConfirmVoltarRequired(true);
			return null;
		}
		
		reset();
		this.cpNomeImpressao = null;
		this.cpModoRelatorio = null;
		this.cpTipoImpressao = null;
		this.cpTipoLinguagem = null;
		this.cpImpressora = null;
		this.isUpdate = false;
		
		return PAGE_UNIDADE_FUNCIONAL_LIST;
	}
	
	public String confirmaVoltar() {
		reset();
		setItemsChanged(false);
		return cancelar();
	}

	/**
	 * SB - Impressora
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa);
	}
	
	private void reset() {
		setFormChanged(false);
		setConfirmEditarRequired(false);
		setConfirmGravarRequired(false);
		setConfirmVoltarRequired(false);
	}

	public void pathImpressoraListener(AjaxBehaviorEvent e){
	     
	    if (cpNomeImpressao != null && !cpNomeImpressao.matches(REGEX_CAMINHO_IMPRESSORA)){
		apresentarMsgNegocio("MSG_ERRO_FORMATO_CAMINHO_IMPRESSORA");
	    }
	    
	    setFormChanged(true);
	}
	
	/**
	 * GETs & SETs
	 */
	public AghImpressoraPadraoUnids getImpressoraPadrao() {
		return impressoraPadrao;
	}

	public void setImpressoraPadrao(AghImpressoraPadraoUnids impressoraPadrao) {
		this.impressoraPadrao = impressoraPadrao;
	}

	public AghImpressoraPadraoUnidsId getImpressoraPadraoId() {
		return impressoraPadraoId;
	}

	public void setImpressoraPadraoId(AghImpressoraPadraoUnidsId impressoraPadraoId) {
		this.impressoraPadraoId = impressoraPadraoId;
	}

	public TipoDocumentoImpressao getCpTipoImpressao() {
		return cpTipoImpressao;
	}

	public void setCpTipoImpressao(TipoDocumentoImpressao cpTipoImpressao) {
		this.cpTipoImpressao = cpTipoImpressao;
	}

	public String getDescricaoTipoTimpressao() {
		return this.cpTipoLinguagem.getDescricao();
	}

	public String getCpNomeImpressao() {
		return cpNomeImpressao;
	}

	public void setCpNomeImpressao(String cpNomeImpressao) {
		this.cpNomeImpressao = cpNomeImpressao;
	}

	public List<AghImpressoraPadraoUnids> getListaImpressorasUnidades() {
		return listaImpressorasUnidades;
	}

	public void setListaImpressorasUnidades(List<AghImpressoraPadraoUnids> listaImpressorasUnidades) {
		this.listaImpressorasUnidades = listaImpressorasUnidades;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Integer getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Integer unfSeq) {
		this.unfSeq = unfSeq;
	}

	public void setListaImpressorasUnidadesOld(List<AghImpressoraPadraoUnids> listaImpressorasUnidadesOld) {
		this.listaImpressorasUnidadesOld = listaImpressorasUnidadesOld;
	}

	public List<AghImpressoraPadraoUnids> getListaImpressorasUnidadesOld() {
		return listaImpressorasUnidadesOld;
	}

	public DominioModoRelatorio getCpModoRelatorio() {
		return cpModoRelatorio;
	}

	public void setCpModoRelatorio(DominioModoRelatorio cpModoRelatorio) {
		this.cpModoRelatorio = cpModoRelatorio;
	}

	public DominioLinguagemImpressora getCpTipoLinguagem() {
		return cpTipoLinguagem;
	}

	public void setCpTipoLinguagem(DominioLinguagemImpressora cpTipoLinguagem) {
		this.cpTipoLinguagem = cpTipoLinguagem;
	}

	public ImpImpressora getCpImpressora() {
		return cpImpressora;
	}

	public void setCpImpressora(ImpImpressora cpImpressora) {
		this.cpImpressora = cpImpressora;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public boolean isFormChanged() {
		return formChanged;
	}

	public void setFormChanged(boolean formChanged) {
		this.formChanged = formChanged;
	}

	public void setFormChanged() {
		setFormChanged(true);
	}
	
	public boolean isItemsChanged() {
		return itemsChanged;
	}

	public void setItemsChanged(boolean itemsChanged) {
		this.itemsChanged = itemsChanged;
	}

	public boolean isConfirmEditarRequired() {
		return confirmEditarRequired;
	}

	public void setConfirmEditarRequired(boolean confirmEditarRequired) {
		this.confirmEditarRequired = confirmEditarRequired;
	}

	public boolean isConfirmVoltarRequired() {
		return confirmVoltarRequired;
	}

	public void setConfirmVoltarRequired(boolean confirmVoltarRequired) {
		this.confirmVoltarRequired = confirmVoltarRequired;
	}

	public boolean isConfirmGravarRequired() {
		return confirmGravarRequired;
	}

	public void setConfirmGravarRequired(boolean confirmGravarRequired) {
		this.confirmGravarRequired = confirmGravarRequired;
	}
}
