package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SalaCirurgicaCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3566582839368074664L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private MbcSalaCirurgica salaCirurgica;
	
	private boolean ativo;
	
	private Short unfSeq;
	
	private Short seqp;
	
	private final String PAGE_LIST_SALA_CIRURGICA = "salaCirurgicaList";
	
	@Inject
	private SalaCirurgicaController salaCirurgicaController;
	
	public List<MbcTipoSala> listarTipoSala(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa),listarTipoSalaCount(objPesquisa));
	}
	
	public Long listarTipoSalaCount(String objPesquisa) {
		return blocoCirurgicoCadastroFacade.contarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa);
	}

	public void iniciar() {
	 

	 

		this.ativo = true;
		//salaCirurgicaAnterior = new MbcSalaCirurgica();
		
		if(salaCirurgica != null) {
			setSeqp(salaCirurgica.getId().getSeqp());
			setUnfSeq(salaCirurgica.getId().getUnfSeq());		
			
			if (DominioSituacao.I.equals(this.salaCirurgica.getSituacao())) {
				this.ativo = false;
			}
		} else {
			resetarSalaCirurgica();
		}
	
	}
	
	
	private void resetarSalaCirurgica() {
		salaCirurgica = new MbcSalaCirurgica();
		MbcSalaCirurgicaId id = new MbcSalaCirurgicaId();
		salaCirurgica.setVisivelMonitor(true);
		salaCirurgica.setId(id);
		seqp = null;
	}
	

	/**
	 * Método chamado ao cancelar a tela de cadastro de sala cirurgica
	 */
	public String voltar() {	
		this.salaCirurgicaController.pesquisar();
		return PAGE_LIST_SALA_CIRURGICA;
	}
	
	
	public String gravar() {
		boolean novo = this.getSalaCirurgica().getId() == null || this.getSalaCirurgica().getId().getUnfSeq() == null;
		try {
			//Seta a unidade cirurgica
			if (novo) {
				// carregar unidade funcional
				getSalaCirurgica().setUnidadeFuncional(aghuFacade.obterUnidadeFuncional(getUnfSeq()));
				getSalaCirurgica().getId().setUnfSeq(getUnfSeq());
			}
			if(this.ativo){
				getSalaCirurgica().setSituacao(DominioSituacao.A);
			}else{
				getSalaCirurgica().setSituacao(DominioSituacao.I);
			}
			this.blocoCirurgicoCadastroFacade.persistirMbcSalaCirurgica(this.getSalaCirurgica());
			
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_SUCESSO_GRAVAR_SALA_UNIDADE_CIRURGICA" : "MENSAGEM_SUCESSO_ALTERAR_SALA_UNIDADE_CIRURGICA",
					this.getSalaCirurgica().getNome());
		} catch (final BaseException e) {
			if(novo){
				this.getSalaCirurgica().getId().setUnfSeq(null); 
			}
			apresentarExcecaoNegocio(e);
		}
		
		this.salaCirurgicaController.pesquisar();
		
		return PAGE_LIST_SALA_CIRURGICA;
	}
	
	public void limpar() {
		resetarSalaCirurgica();
	}
	
	public void limparMotivoInativacao() {
		this.getSalaCirurgica().setMotivoInat(null);
	}
	
	
	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	

	
}
