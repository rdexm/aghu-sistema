package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class LocalizacaoProcessoController extends ActionController {


	private static final Log LOG = LogFactory.getLog(LocalizacaoProcessoController.class);

	private static final long serialVersionUID = -7626137544256850184L;

	private static final String LOCALIZACAO_PROCESSO_LIST = "localizacaoProcessoList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	
	private ScoLocalizacaoProcesso locProcesso;
	private Short codigo;
	private Boolean indAtivo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setLocProcesso(new ScoLocalizacaoProcesso());
			this.getLocProcesso().setIndSituacao(DominioSituacao.A);
			
		} else {
			locProcesso = this.comprasCadastrosBasicosFacade.obterLocalizacaoProcesso(this.getCodigo());

			if(locProcesso == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (this.getLocProcesso().getIndSituacao() == DominioSituacao.A) {
				this.setIndAtivo(true);
			}
			else {
				this.setIndAtivo(false);
			}	
		}
		return null;
	
	}

	public String gravar() {
		try {
			
			if (this.getLocProcesso() != null) {
				
				if (this.indAtivo) {
					this.getLocProcesso().setIndSituacao(DominioSituacao.A);
				}
				else {
					this.getLocProcesso().setIndSituacao(DominioSituacao.I);
				}
				
				final boolean novo = this.getLocProcesso().getCodigo() == null;

				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirLocalizacaoProcesso(this.getLocProcesso());
					this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LOC_PROCESSO_INSERT_SUCESSO");
					
				} else {
					this.comprasCadastrosBasicosFacade.alterarLocalizacaoProcesso(this.getLocProcesso());
					this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LOC_PROCESSO_UPDATE_SUCESSO");
				}

				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}
	
	//Método para carregar Suggestion de ramais telefônicos
	public List<RapRamalTelefonico> pesquisarRamais(String objParam) {
		return cadastrosBasicosFacade.pesquisarRamalTelefonicoPorNroRamal(objParam);
	}

	//Método para carregar Suggestion Servidor Responsável
	public List<RapServidores> pesquisarServidorVinculados(String objPesquisa) {
		try {
			return this.registroColaboradorFacade.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}
	
	public String cancelar() {
		codigo = null;
		locProcesso = null;
		return LOCALIZACAO_PROCESSO_LIST;
	}

	public ScoLocalizacaoProcesso getLocProcesso() {
		return locProcesso;
	}

	public void setLocProcesso(ScoLocalizacaoProcesso locProcesso) {
		this.locProcesso = locProcesso;
	}
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}
}