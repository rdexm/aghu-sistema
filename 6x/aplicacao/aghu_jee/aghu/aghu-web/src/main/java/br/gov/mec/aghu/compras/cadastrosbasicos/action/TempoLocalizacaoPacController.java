package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class TempoLocalizacaoPacController extends ActionController {

	private static final long serialVersionUID = -6876405164044629198L;

	private static final String TEMPO_LOCALIZACAO_PAC_LIST = "tempoLocalizacaoPacList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoTempoAndtPac tempoLocalizacaoPac;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

	 

		if(tempoLocalizacaoPac != null && tempoLocalizacaoPac.getId() != null){
			tempoLocalizacaoPac = comprasCadastrosBasicosFacade.obterPorChavePrimariaSemLazy(tempoLocalizacaoPac.getId());

			if(tempoLocalizacaoPac == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			tempoLocalizacaoPac = new ScoTempoAndtPac();
		}
		return null;
	
	}
	
	public String gravar() {
		try {
			if (this.getTempoLocalizacaoPac() != null) {

				if (this.tempoLocalizacaoPac.getVersion() == null) {
					ScoTemposAndtPacsId tempoLocalizacaoPacId = new ScoTemposAndtPacsId();
					tempoLocalizacaoPacId.setMlcCodigo(this.tempoLocalizacaoPac.getModalidadeLicitacao().getCodigo());
					tempoLocalizacaoPacId.setLcpCodigo(this.tempoLocalizacaoPac.getLocalizacaoProcesso().getCodigo());
					this.tempoLocalizacaoPac.setId(tempoLocalizacaoPacId);
					
					this.comprasCadastrosBasicosFacade.inserirTempoAndtPac(this.getTempoLocalizacaoPac());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TEMPO_LOC_PAC_INSERT_SUCESSO");
					
				} else {
					this.comprasCadastrosBasicosFacade.alterarTempoAndtPac(this.getTempoLocalizacaoPac());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TEMPO_LOC_PAC_UPDATE_SUCESSO");
				}

				return cancelar();
			}
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		tempoLocalizacaoPac = null;
		return TEMPO_LOCALIZACAO_PAC_LIST;
	}
	
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String modalidade) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(modalidade);
	}
		
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcesso(String localizacao) {
		return this.comprasCadastrosBasicosFacade.pesquisarLocalizacaoProcessoPorCodigoOuDescricao(localizacao, true);
	}
	
	public ScoTempoAndtPac getTempoLocalizacaoPac() {
		return tempoLocalizacaoPac;
	}

	public void setTempoLocalizacaoPac(ScoTempoAndtPac tempoLocalizacaoPac) {
		this.tempoLocalizacaoPac = tempoLocalizacaoPac;
	}
}