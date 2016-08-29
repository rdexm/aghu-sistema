package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoContratoController extends ActionController {

	private static final long serialVersionUID = -1990151600745189312L;

	private static final String PAGE_PESQUISAR_TIPO_CONTRATO = "pesquisarTipoContrato";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	private ScoTipoContratoSicon tipoContratoSicon = new ScoTipoContratoSicon();
	private Integer seqContrato;
	private Integer codigoSicon;
	private String descricao;
	private DominioSituacao situacao;
	private String origem;
	private boolean exibirNovo;
	private boolean alterar;
	private boolean aditivo;
	private boolean pesquisaAtivada;
	private Integer codigoFiltro;
	private String descricaoFiltro;
	private DominioSituacao situacaoFiltro;
	private boolean temVinculoContrato;
	private Integer seqTipoContrato;
	private List<ScoTipoContratoSicon> listaTipoContrato;
	private Boolean modalidade;
	private Boolean insereItens;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


		this.setTemVinculoContrato(false);

		

		if (this.codigoSicon != null) {

			this.tipoContratoSicon = this.cadastrosBasicosSiconFacade.obterTipoContratoSicon(this.seqTipoContrato);
			
			if(tipoContratoSicon != null){
				this.setDescricao(this.tipoContratoSicon.getDescricao());
				this.setSituacao(this.tipoContratoSicon.getSituacao());
				this.setAditivo(this.tipoContratoSicon.getIndAditivo());
				this.setModalidade(this.tipoContratoSicon.getIndModalidade());
				this.setInsereItens(this.tipoContratoSicon.getIndInsereItens());
	
				if (alterar) {
					// bloqueia os campos de edição na tela, caso o tipo de
					// contrato tenha algum vínculo com
					// contrato.
					this.setTemVinculoContrato(this.cadastrosBasicosSiconFacade.verificarVinculoContrato(tipoContratoSicon));
				}
			}else{				
				apresentarExcecaoNegocio(new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO));
				alterar = Boolean.FALSE;
			}
		} else {

			this.tipoContratoSicon = new ScoTipoContratoSicon();
			this.setSituacao(DominioSituacao.A);
			this.setCodigoSicon(null);
			this.setDescricao(null);			
			this.setAditivo(false);
			this.setModalidade(false);
			this.setInsereItens(true);
			alterar = Boolean.FALSE;
		}
		
	
	}

	public String salvar() {

		try {

			this.tipoContratoSicon.setCodigoSicon(this.getCodigoSicon());
			this.tipoContratoSicon.setDescricao(this.getDescricao().trim());
			this.tipoContratoSicon.setIndAditivo(this.isAditivo());
			this.tipoContratoSicon.setSituacao(getSituacao());
			this.tipoContratoSicon.setIndModalidade(this.getModalidade());
			this.tipoContratoSicon.setIndInsereItens(this.getInsereItens());

			if (isAlterar()) {
				this.cadastrosBasicosSiconFacade.alterarTipoContrato(this.tipoContratoSicon);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_CONTRATO");

			} else {
				this.cadastrosBasicosSiconFacade.inserirTipoContrato(this.tipoContratoSicon);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_TIPO_CONTRATO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		this.tipoContratoSicon = new ScoTipoContratoSicon();
		this.codigoSicon = null;
		this.descricao = null;
		this.exibirNovo = false;
		this.situacao = null;

		return PAGE_PESQUISAR_TIPO_CONTRATO;
	}

	public String voltar() {
		this.tipoContratoSicon = new ScoTipoContratoSicon();
		this.setCodigoSicon(null);
		this.setDescricao(null);

		return PAGE_PESQUISAR_TIPO_CONTRATO;

	}

	public ScoTipoContratoSicon getTipoContratoSicon() {
		return tipoContratoSicon;
	}

	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon) {
		this.tipoContratoSicon = tipoContratoSicon;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<ScoTipoContratoSicon> getListaTipoContrato() {
		return listaTipoContrato;
	}

	public void setListaTipoContrato(List<ScoTipoContratoSicon> listaTipoContrato) {
		this.listaTipoContrato = listaTipoContrato;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isPesquisaAtivada() {
		return pesquisaAtivada;
	}

	public void setPesquisaAtivada(boolean pesquisaAtivada) {
		this.pesquisaAtivada = pesquisaAtivada;
	}

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public boolean isAditivo() {
		return aditivo;
	}

	public void setAditivo(boolean aditivo) {
		this.aditivo = aditivo;
	}

	public Integer getCodigoFiltro() {
		return codigoFiltro;
	}

	public void setCodigoFiltro(Integer codigoFiltro) {
		this.codigoFiltro = codigoFiltro;
	}

	public String getDescricaoFiltro() {
		return descricaoFiltro;
	}

	public void setDescricaoFiltro(String descricaoFiltro) {
		this.descricaoFiltro = descricaoFiltro;
	}

	public DominioSituacao getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(DominioSituacao situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}

	public boolean isTemVinculoContrato() {
		return temVinculoContrato;
	}

	public void setTemVinculoContrato(boolean temVinculoContrato) {
		this.temVinculoContrato = temVinculoContrato;
	}

	public Integer getSeqTipoContrato() {
		return seqTipoContrato;
	}

	public void setSeqTipoContrato(Integer seqTipoContrato) {
		this.seqTipoContrato = seqTipoContrato;
	}

	public Boolean getModalidade() {
		return modalidade;
	}

	public void setModalidade(Boolean modalidade) {
		this.modalidade = modalidade;
	}

	public Boolean getInsereItens() {
		return insereItens;
	}

	public void setInsereItens(Boolean insereItens) {
		this.insereItens = insereItens;
	}

}
