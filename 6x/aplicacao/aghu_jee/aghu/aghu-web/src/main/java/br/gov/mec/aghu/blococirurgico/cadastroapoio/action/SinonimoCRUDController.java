package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcSinonimoProcCirg;
import br.gov.mec.aghu.model.MbcSinonimoProcCirgId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SinonimoCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3762669677142340391L;
	private static final String PCI_LIST = "procedimentoCirurgicoList";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	private Integer pciSeq;
	private Short seqp;
	
	private MbcSinonimoProcCirgId id;
	private MbcSinonimoProcCirg sinonimo;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private Boolean situacao;
	
	private Boolean inclusao;
	
	private List<MbcSinonimoProcCirg> lista;
	
	
	public void iniciar() {
	 

	 

		this.procedimentoCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		this.seqp = this.blocoCirurgicoCadastroApoioFacade.buscaMenorSeqpSinonimosPeloSeqProcedimento(pciSeq);
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscaSinonimosPeloSeqProcedimento(pciSeq);
		this.sinonimo = new MbcSinonimoProcCirg();
		this.situacao = true;
		this.inclusao = true;
	
	}
	

	public void editar(MbcSinonimoProcCirgId id) {
		this.id = id;
		this.sinonimo =  this.blocoCirurgicoCadastroApoioFacade.obterSinonimoProcedimentoCirurgico(id);
		this.situacao = this.sinonimo.getSituacao().isAtivo();
		this.inclusao = false;
	}
	
	public void alterarSituacao(MbcSinonimoProcCirgId id) {
		MbcSinonimoProcCirg sinonimo =  this.blocoCirurgicoCadastroApoioFacade.obterSinonimoProcedimentoCirurgico(id);
		Boolean situacao = sinonimo.getSituacao().isAtivo();
		sinonimo.setSituacao(sinonimo.getSituacao().isAtivo() ? DominioSituacao.I : DominioSituacao.A);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirSinonimoProcedCirurgico(sinonimo, false);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscaSinonimosPeloSeqProcedimento(pciSeq);
			this.id = null;
			
			this.apresentarMsgNegocio(Severity.INFO, situacao ? "MENSAGEM_INATIVADO_SINONIMO_PROCED_CIRURGICO" : "MENSAGEM_ATIVADO_SINONIMO_PROCED_CIRURGICO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicao() {
		this.sinonimo = new MbcSinonimoProcCirg();
		this.situacao = true;
		this.id = null;
		this.inclusao = true;
	}
	
	public String voltar() {
		return PCI_LIST;
	}
	
	public void gravar() {
		if(this.inclusao) {
			this.sinonimo.setId(new MbcSinonimoProcCirgId(pciSeq, null));
		}
		this.sinonimo.setSituacao(Boolean.TRUE.equals(situacao) ? DominioSituacao.A : DominioSituacao.I);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirSinonimoProcedCirurgico(sinonimo, inclusao);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscaSinonimosPeloSeqProcedimento(pciSeq);

			this.apresentarMsgNegocio(Severity.INFO,
					inclusao ? "MENSAGEM_SUCESSO_GRAVAR_SINONIMO_PROCED_CIRURGICO" : "MENSAGEM_SUCESSO_ALTERAR_SINONIMO_PROCED_CIRURGICO");
			
			this.sinonimo = new MbcSinonimoProcCirg();
			this.situacao = true;
			this.inclusao = true;
			this.id = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public MbcSinonimoProcCirgId getId() {
		return id;
	}

	public void setId(MbcSinonimoProcCirgId id) {
		this.id = id;
	}

	public MbcSinonimoProcCirg getSinonimo() {
		return sinonimo;
	}

	public void setSinonimo(MbcSinonimoProcCirg sinonimo) {
		this.sinonimo = sinonimo;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public List<MbcSinonimoProcCirg> getLista() {
		return lista;
	}

	public void setLista(List<MbcSinonimoProcCirg> lista) {
		this.lista = lista;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Boolean getInclusao() {
		return inclusao;
	}

	public void setInclusao(Boolean inclusao) {
		this.inclusao = inclusao;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
}
