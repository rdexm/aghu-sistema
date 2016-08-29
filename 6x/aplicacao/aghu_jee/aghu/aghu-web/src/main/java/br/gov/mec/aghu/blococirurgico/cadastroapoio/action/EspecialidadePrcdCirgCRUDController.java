package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EspecialidadePrcdCirgCRUDController extends ActionController {

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

	@EJB
	private IAghuFacade aghuFacade;

	private Integer pciSeq;

	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private AghEspecialidades especialidade;
	private MbcEspecialidadeProcCirgs especialidadePrcdCirg;
	
	private List<MbcEspecialidadeProcCirgs> lista;
	
	public void iniciar() {
	 

	 

		this.procedimentoCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		this.especialidadePrcdCirg = new MbcEspecialidadeProcCirgs();
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarEspecialidadesPeloSeqProcedimento(pciSeq);
		this.especialidadePrcdCirg = new MbcEspecialidadeProcCirgs();
	
	}
	

	
	public void alterarSituacao(MbcEspecialidadeProcCirgsId id) {
		MbcEspecialidadeProcCirgs especialidadePrcdCirg =  this.blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(id);
		Boolean situacao = Boolean.valueOf((especialidadePrcdCirg.getSituacao().isAtivo()));
		especialidadePrcdCirg.setSituacao(especialidadePrcdCirg.getSituacao().isAtivo() ? DominioSituacao.I : DominioSituacao.A);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirEspecialidadeProcedCirurgico(especialidadePrcdCirg, false);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarEspecialidadesPeloSeqProcedimento(pciSeq);
			
			this.apresentarMsgNegocio(Severity.INFO, situacao ? "MENSAGEM_INATIVADO_ESPECIALIDADE_PROCED_CIRURGICO" : "MENSAGEM_ATIVADO_ESPECIALIDADE_PROCED_CIRURGICO");
		} catch (BaseException e) {
			especialidadePrcdCirg.setSituacao(DominioSituacao.getInstance(situacao));
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar() {
		this.especialidadePrcdCirg.setId(new MbcEspecialidadeProcCirgsId(pciSeq, especialidade.getSeq()));
		this.especialidadePrcdCirg.setEspecialidade(especialidade);
		this.especialidadePrcdCirg.setSituacao(DominioSituacao.A);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirEspecialidadeProcedCirurgico(especialidadePrcdCirg, true);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarEspecialidadesPeloSeqProcedimento(pciSeq);

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_ESPECIALIDADE_PROCED_CIRURGICO");
			
			this.especialidadePrcdCirg = new MbcEspecialidadeProcCirgs();
			this.especialidade = null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return PCI_LIST;
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String filtro) {
		return this.aghuFacade.pesquisarEspecialidadePorSeqOuSiglaOuNome((String)filtro);
	}


	public Integer getPciSeq() {
		return pciSeq;
	}


	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}


	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}


	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}


	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}


	public MbcEspecialidadeProcCirgs getEspecialidadePrcdCirg() {
		return especialidadePrcdCirg;
	}


	public void setEspecialidadePrcdCirg(
			MbcEspecialidadeProcCirgs especialidadePrcdCirg) {
		this.especialidadePrcdCirg = especialidadePrcdCirg;
	}


	public List<MbcEspecialidadeProcCirgs> getLista() {
		return lista;
	}


	public void setLista(List<MbcEspecialidadeProcCirgs> lista) {
		this.lista = lista;
	}
}
