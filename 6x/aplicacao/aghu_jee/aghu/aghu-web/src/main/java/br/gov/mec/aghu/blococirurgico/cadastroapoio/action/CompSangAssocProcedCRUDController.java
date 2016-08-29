package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CompSangAssocProcedCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3762669002342340391L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private static final String PCI_LIST = "procedimentoCirurgicoList";
	
	private Integer pciSeq;
	private Short seq;
	
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private MbcCompSangProcCirg componente;
	private AbsComponenteSanguineo componenteSanguineo;
	private AghEspecialidades especialidade;
	private Short qtdeUnidade;
	private Short qtdeMl;
	
	private Boolean inclusao;
	
	private List<MbcCompSangProcCirg> lista;

	public void iniciar() {
	 	//Busca informações
		this.procedimentoCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAsscComponenteSangPeloSeqProcedimento(pciSeq);
		
		//seta variaveis de controles de tela e inserções
		this.componente = new MbcCompSangProcCirg();
		this.componenteSanguineo = null;
		this.especialidade = null;
		this.qtdeMl = null;
		this.qtdeUnidade = null;
		this.inclusao = true;
		this.seq = null;
	}
	
	public void editar(Short seq) {
		this.seq = seq;
		this.componente =  this.blocoCirurgicoCadastroApoioFacade.buscarComponenteSanguineoEEspecialidadePorSeq(seq);
		this.componenteSanguineo = this.componente.getAbsComponenteSanguineo();
		this.especialidade = this.componente.getAghEspecialidades();
		this.qtdeMl = this.componente.getQtdeMl();
		this.qtdeUnidade = this.componente.getQtdeUnidade();
		this.inclusao = false;
	}

	public void remover(MbcCompSangProcCirg compExcluir) {
		MbcCompSangProcCirg componente =  this.blocoCirurgicoCadastroApoioFacade.buscarComponenteSanguineoEEspecialidadePorSeq(compExcluir.getSeq());
		this.blocoCirurgicoCadastroApoioFacade.removerCompSangProcedCirurgico(componente);
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_ASSC_COMP_SANG_PROCED_CIRURGICO");
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAsscComponenteSangPeloSeqProcedimento(pciSeq);
	}
	
	public void cancelarEdicao() {
		this.componente = new MbcCompSangProcCirg();
		this.componenteSanguineo = null;
		this.especialidade = null;
		this.qtdeMl = null;
		this.qtdeUnidade = null;
		this.inclusao = true;
		this.seq = null;
	}

	public void gravar() {
		
		if(especialidade != null) {
			this.componente.setAghEspecialidades(especialidade);
		}
		else {
			this.componente.setAghEspecialidades(null);
		}

		this.componente.setAghEspecialidades(especialidade);
		this.componente.setAbsComponenteSanguineo(componenteSanguineo);
		this.componente.setMbcProcedimentoCirurgicos(procedimentoCirurgico);
		this.componente.setQtdeMl(qtdeMl);
		this.componente.setQtdeUnidade(qtdeUnidade);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirCompSangProcedCirurgico(componente, inclusao);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAsscComponenteSangPeloSeqProcedimento(pciSeq);

			this.apresentarMsgNegocio(Severity.INFO,
					inclusao ? "MENSAGEM_SUCESSO_GRAVAR_ASSC_COMP_SANG_PROCED_CIRURGICO" : "MENSAGEM_SUCESSO_ALTERAR_ASSC_COMP_SANG_PROCED_CIRURGICO");
			
			this.componente = new MbcCompSangProcCirg();
			this.componenteSanguineo = null;
			this.especialidade = null;
			this.qtdeMl = null;
			this.qtdeUnidade = null;
			this.inclusao = true;
			this.seq = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return PCI_LIST;
	}
	
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(String objPesquisa){
		return this.returnSGWithCount(bancoDeSangueFacade.listarAbsComponenteSanguineosAtivos(objPesquisa),listarAbsComponenteSanguineosCount(objPesquisa));
	}

	public Long listarAbsComponenteSanguineosCount(String objPesquisa) {
		return bancoDeSangueFacade.listarAbsComponenteSanguineosAtivosCount(objPesquisa);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.listarEspecialidadePorNomeOuSigla((String) filtro),pesquisarEspecialidadesCount(filtro));
	}
	
	public Long pesquisarEspecialidadesCount(String filtro) {
		return this.aghuFacade.listarEspecialidadePorNomeOuSiglaCount((String) filtro);
	}
	
	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public MbcCompSangProcCirg getComponente() {
		return componente;
	}

	public void setComponente(MbcCompSangProcCirg componente) {
		this.componente = componente;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Boolean getInclusao() {
		return inclusao;
	}

	public void setInclusao(Boolean inclusao) {
		this.inclusao = inclusao;
	}

	public List<MbcCompSangProcCirg> getLista() {
		return lista;
	}

	public void setLista(List<MbcCompSangProcCirg> lista) {
		this.lista = lista;
	}

	public Short getQtdeUnidade() {
		return qtdeUnidade;
	}

	public void setQtdeUnidade(Short qtdeUnidade) {
		this.qtdeUnidade = qtdeUnidade;
	}

	public Short getQtdeMl() {
		return qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}
}
