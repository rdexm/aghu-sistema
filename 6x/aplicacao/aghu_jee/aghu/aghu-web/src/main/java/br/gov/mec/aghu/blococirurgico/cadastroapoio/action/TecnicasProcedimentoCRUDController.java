package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.model.PdtTecnicaPorProcId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class TecnicasProcedimentoCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String TECNICAS_LIST = "tecnicasList";
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	private Boolean ativo;
	private Integer codigo;
	private PdtTecnica tecnica;
	private PdtProcDiagTerap procedimento;
	private PdtTecnicaPorProc tecnicaProcedimento;
	private List<PdtTecnicaPorProc> tecnicasProcedimentos;
	private Boolean ativaCampoDescricao; 
	private Boolean ativaBotaoGravarProcedimento;
	private Boolean ativaProcedimento;
	
	public void inicio() {

		tecnicaProcedimento = new PdtTecnicaPorProc();
		tecnicasProcedimentos = null;
		if (codigo != null && codigo > 0) {
			tecnica = blocoCirurgicoProcDiagTerapFacade.obterTecnicaPorChavePrimaria(codigo);
 			carregarListaProcedimentosParaTecnica();
			if (tecnica.getIndSituacao().equals(DominioSituacao.A)) {
				ativo = true;
			} else {
				ativo = false;
			}
			ativaCampoDescricao = false;
			ativaProcedimento = true;
		} else {
			tecnica = new PdtTecnica();
			codigo = null;
			ativo = true;
			ativaCampoDescricao = true;
			ativaProcedimento = false;
		}
		procedimento = null;
		ativaBotaoGravarProcedimento = false;
	
	}
	

	public void ativarBotaoGravarProcedimento() {
		ativaBotaoGravarProcedimento = true;
	}
	
	public void desativarBotaoGravarProcedimento() {
		ativaBotaoGravarProcedimento = false;
	}
	
	public void gravarTecnica() {
		try {
			if (ativo) {
				tecnica.setIndSituacao(DominioSituacao.A);
			} else {
				tecnica.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirPdtTecnica(tecnica);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		}
		ativaProcedimento = true;
	}
	
	public String voltar() {
		codigo = null;
		ativo = true;
		return TECNICAS_LIST;
	}
	
	public List<PdtProcDiagTerap> listarProcedimento(String descricao) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerap(descricao),listarProcedimentoCount(descricao));
	}
	
	public Long listarProcedimentoCount(String descricao) {
		return blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapCount(descricao);
	} 
	
	public void carregarListaProcedimentosParaTecnica() {
		tecnicasProcedimentos = blocoCirurgicoProcDiagTerapFacade.listarPdtTecnicaPorProc(tecnica.getSeq());
		blocoCirurgicoProcDiagTerapFacade.refreshPdtTecnicaPorProc(tecnicasProcedimentos);
	}
	
	protected enum TecnicasProcedimentoCRUDControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TECNICAS_PROCEDIMENTOS_PROCEDIMENTO_JA_CADASTRADO;
	}
	
	public void gravarProcedimento() {
		tecnicaProcedimento.setId(new PdtTecnicaPorProcId(tecnica.getSeq(),procedimento.getSeq()));
		try {
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirPdtTecnicaPorProc(tecnica, procedimento);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			tecnicaProcedimento = new PdtTecnicaPorProc();
			procedimento = null;
			carregarListaProcedimentosParaTecnica();
			desativarBotaoGravarProcedimento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(TecnicasProcedimentoCRUDControllerExceptionCode.MENSAGEM_TECNICAS_PROCEDIMENTOS_PROCEDIMENTO_JA_CADASTRADO));
		}
	}
	
	public void preparaExcluirProcedimento(PdtTecnicaPorProc tecnicaProcedimento) {
		this.tecnicaProcedimento = tecnicaProcedimento; 
	}
	
	public void excluirProcedimento() {
		String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.removerPdtTecnicaPorProc(tecnicaProcedimento);
		apresentarMsgNegocio(Severity.INFO,mensagemSucesso);
		tecnicaProcedimento = new PdtTecnicaPorProc();
		carregarListaProcedimentosParaTecnica();
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public PdtTecnica getTecnica() {
		return tecnica;
	}

	public void setTecnica(PdtTecnica tecnica) {
		this.tecnica = tecnica;
	}

	public PdtProcDiagTerap getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(PdtProcDiagTerap procedimento) {
		this.procedimento = procedimento;
	}

	public PdtTecnicaPorProc getTecnicaProcedimento() {
		return tecnicaProcedimento;
	}

	public void setTecnicaProcedimento(PdtTecnicaPorProc tecnicaProcedimento) {
		this.tecnicaProcedimento = tecnicaProcedimento;
	}

	public List<PdtTecnicaPorProc> getTecnicasProcedimentos() {
		return tecnicasProcedimentos;
	}

	public void setTecnicasProcedimentos(List<PdtTecnicaPorProc> tecnicasProcedimentos) {
		this.tecnicasProcedimentos = tecnicasProcedimentos;
	}

	public Boolean getAtivaBotaoGravarProcedimento() {
		return ativaBotaoGravarProcedimento;
	}

	public void setAtivaBotaoGravarProcedimento(Boolean ativaBotaoGravarProcedimento) {
		this.ativaBotaoGravarProcedimento = ativaBotaoGravarProcedimento;
	}

	public Boolean getAtivaProcedimento() {
		return ativaProcedimento;
	}

	public void setAtivaProcedimento(Boolean ativaProcedimento) {
		this.ativaProcedimento = ativaProcedimento;
	}

	public Boolean getAtivaCampoDescricao() {
		return ativaCampoDescricao;
	}

	public void setAtivaCampoDescricao(Boolean ativaCampoDescricao) {
		this.ativaCampoDescricao = ativaCampoDescricao;
	}

}