package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.model.SceMotivoMovimentoId;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterMotivoTipoMovimentoController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5424762516697634781L;
	
	private static final Integer TAMANHO_MAXIMO_DESCRICAO = 105;
	private static final Integer ACRESCIMO_RETICENCIAS = 3;
	private static final String STYLE_BUTTON_EDIT = "bt_ok icon-yes";
	private static final String STYLE_BUTTON_ADD = "bt_ok icon-add";
	
	private Boolean ativo;
	@Inject @Paginator
	private DynamicDataModel<SceMotivoMovimento> dataModel;
	
	public enum ManterMotivoTipoMovimentoControllerExceptionCode {
		MSG_MOTIVO_MOVIMENTO_M1, 
		MSG_MOTIVO_MOVIMENTO_M2, 
		MSG_MOTIVO_MOVIMENTO_M4, 
		MSG_MOTIVO_MOVIMENTO_M5, 
		MSG_MOTIVO_MOVIMENTO_M6
	}
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private SceTipoMovimento filtroTipoMovimento;
	private SceMotivoMovimento motivoMovimento;
	private SceMotivoMovimento motivoMovimentoExclusao;
	private Boolean modoEdicao = Boolean.FALSE;
	private String novaDescricao; 
	private Boolean novaSituacao;
	private String styleButton;
	
	private List<SceMotivoMovimento> listaMotivoMovimento;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		if(filtroTipoMovimento == null){
			this.apresentarMsgNegocio(Severity.WARN, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M1.toString());
		} else {
			this.ativo = Boolean.TRUE;
			this.styleButton = STYLE_BUTTON_ADD;
			this.novaSituacao = Boolean.TRUE;
			
			this.dataModel.reiniciarPaginator();
		}
	}

	public void limparPesquisa() {
		this.ativo = Boolean.FALSE;
		this.filtroTipoMovimento = null;
		this.cancelarEdicao();
	}

	public void persistirMotivoMovimento() {
		if (modoEdicao) {
			motivoMovimento.setDescricao(novaDescricao);
			motivoMovimento.setIndSituacao(DominioSituacao.getInstance(novaSituacao));
			try {
				this.estoqueFacade.salvarMotivoMovimento(motivoMovimento);
				this.apresentarMsgNegocio(Severity.INFO, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M5.toString());
				this.cancelarEdicao();
				this.dataModel.reiniciarPaginator();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {

			motivoMovimento = new SceMotivoMovimento();

			Short seq = filtroTipoMovimento != null ? filtroTipoMovimento.getId().getSeq() : null;
			Byte complemento = filtroTipoMovimento != null ? filtroTipoMovimento.getId().getComplemento() : null;

			motivoMovimento.setDescricao(novaDescricao);
			motivoMovimento.setIndSituacao(DominioSituacao.getInstance(novaSituacao));

			SceMotivoMovimentoId id = new SceMotivoMovimentoId(seq, complemento, null);
			motivoMovimento.setId(id);

			try {
				this.estoqueFacade.salvarMotivoMovimento(motivoMovimento);
				this.apresentarMsgNegocio(Severity.INFO, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M4.toString());
				this.cancelarEdicao();
				this.dataModel.reiniciarPaginator();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void editar(SceMotivoMovimento edicaoMotivoMovimento) {
		this.motivoMovimento = edicaoMotivoMovimento;
		this.styleButton = STYLE_BUTTON_EDIT;
		this.modoEdicao = Boolean.TRUE;
		this.novaDescricao = this.motivoMovimento.getDescricao();
		this.novaSituacao = this.motivoMovimento.getIndSituacao().isAtivo();
	}

	public void excluir() {
		try {
			this.estoqueFacade.removerMotivoMovimento(motivoMovimentoExclusao);
			this.apresentarMsgNegocio(Severity.INFO, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M6.toString());
			this.cancelarEdicao();
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void cancelarEdicao() {
		this.setStyleButton(STYLE_BUTTON_ADD);
		this.setNovaDescricao(null);
		this.setNovaSituacao(Boolean.TRUE);
		this.setModoEdicao(Boolean.FALSE);
	}
	
	public List<SceMotivoMovimento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		Short seq = filtroTipoMovimento != null ?  filtroTipoMovimento.getId().getSeq() : null;
		Byte complemento = filtroTipoMovimento != null ? filtroTipoMovimento.getId().getComplemento() : null;
	
		if(seq == null && complemento == null){
			this.apresentarMsgNegocio(Severity.WARN, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M1.toString());
			this.ativo = Boolean.FALSE;
			return new ArrayList<SceMotivoMovimento>(); 
		}
		
		if (StringUtils.isBlank(orderProperty)) {
			orderProperty = SceMotivoMovimento.Fields.DESCRICAO.toString();
			asc = true;
		}
		
		this.listaMotivoMovimento = estoqueFacade.listarMotivoMovimento(firstResult, maxResult, orderProperty, asc, seq, complemento);
		
		if(this.listaMotivoMovimento == null && this.listaMotivoMovimento.isEmpty()){
			this.apresentarMsgNegocio(Severity.WARN, ManterMotivoTipoMovimentoControllerExceptionCode.MSG_MOTIVO_MOVIMENTO_M1.toString());
			this.ativo = Boolean.FALSE;
		} 
		
		return this.listaMotivoMovimento;
	}

	public Long recuperarCount() {
		
		Short seq = filtroTipoMovimento != null ?  filtroTipoMovimento.getId().getSeq() : null;
		Byte complemento = filtroTipoMovimento != null ? filtroTipoMovimento.getId().getComplemento() : null;
		
		return estoqueFacade.listarMotivoMovimentoCount(seq, complemento);
	}

	public String formataDescricaoMotivoMovimento(String descricao){
		if(StringUtils.isNotBlank(descricao) && descricao.trim().length() >= TAMANHO_MAXIMO_DESCRICAO){
			descricao = descricao.substring(0, TAMANHO_MAXIMO_DESCRICAO - ACRESCIMO_RETICENCIAS).concat("...");
		}
		return descricao;
	}
	
	public Boolean exibeHintDescricaoMotivoMovimento(String descricao){
		return StringUtils.isNotBlank(descricao) && descricao.trim().length() >= TAMANHO_MAXIMO_DESCRICAO;
	}
	
	public String formatarSituacao(DominioSituacao situacao){
		return situacao.getDescricao();
	}
	
	public void setFiltroTipoMovimento(SceTipoMovimento filtroTipoMovimento) {
		this.filtroTipoMovimento = filtroTipoMovimento;
	}

	public SceTipoMovimento getFiltroTipoMovimento() {
		return filtroTipoMovimento;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public String getNovaDescricao() {
		return novaDescricao;
	}

	public void setNovaDescricao(String novaDescricao) {
		this.novaDescricao = novaDescricao;
	}

	public Boolean getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(Boolean novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

	public void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	public void setMotivoMovimento(SceMotivoMovimento motivoMovimento) {
		this.motivoMovimento = motivoMovimento;
	}

	public SceMotivoMovimento getMotivoMovimento() {
		return motivoMovimento;
	}

	public void setListaMotivoMovimento(List<SceMotivoMovimento> listaMotivoMovimento) {
		this.listaMotivoMovimento = listaMotivoMovimento;
	}

	public List<SceMotivoMovimento> getListaMotivoMovimento() {
		return listaMotivoMovimento;
	}

	public void setMotivoMovimentoExclusao(SceMotivoMovimento motivoMovimentoExclusao) {
		this.motivoMovimentoExclusao = motivoMovimentoExclusao;
	}

	public SceMotivoMovimento getMotivoMovimentoExclusao() {
		return motivoMovimentoExclusao;
	}
	
	public List<SceTipoMovimento> pesquisarTipoMovimento(String descricao) {
		return this.returnSGWithCount(estoqueFacade.listarTipoMovimento(descricao),pesquisarTipoMovimentoCount(descricao));
	}

	public Long pesquisarTipoMovimentoCount(String descricao) {
		return estoqueFacade.listarTipoMovimentoCount(descricao);
	}

	public void setStyleButton(String styleButton) {
		this.styleButton = styleButton;
	}

	public String getStyleButton() {
		return styleButton;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public DynamicDataModel<SceMotivoMovimento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceMotivoMovimento> dataModel) {
		this.dataModel = dataModel;
	}

}
