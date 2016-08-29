package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescPadraoId;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoPadraoPdtCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = -3566582839368074664L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private PdtDescPadrao descricaoPadrao;
	
	private boolean ativo;

	private Short seqp;
	
	private Short espSeq;

	private final String PAGE_LIST_DESCRICAO_PADRAO_PDT  = "descricaoPadraoPdtList";
	
	public void iniciar() {
	 

	 

		this.ativo = true;
		if(this.descricaoPadrao != null && this.descricaoPadrao.getId()!= null)   {
			this.ativo = false;
		} else {
			resetarDescricaoPadrao();
		}
	
	}
	
	
	
	//Metodo para Suggestion Box de especialidades
	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidades((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesCount((String) strPesquisa);
	}
	
	//Metodo para Suggestion Box de Procedimentos Diag. Terapeuticos
	public List<PdtProcDiagTerap> obterProcedimentoDiagTerapeuticos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerap(objPesquisa),obterProcedimentoDiagTerapeuticosCount(objPesquisa));
	}
	
	public Long obterProcedimentoDiagTerapeuticosCount(String objPesquisa){
		return this.blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapCount(objPesquisa);

	}
	
	
	private void resetarDescricaoPadrao() {
		descricaoPadrao = new PdtDescPadrao();
		PdtDescPadraoId id = new PdtDescPadraoId();
		id.setSeqp(null);
		id.setEspSeq(null);
		descricaoPadrao.setId(id);
		descricaoPadrao.setAghEspecialidades(null);
		descricaoPadrao.setDescTecPadrao(null);
		descricaoPadrao.setPdtProcDiagTerap(null);
		seqp = null;
	}
	
	/**
	 * Método chamado ao cancelar a tela de cadastro de descricao Padrao
	 */
	public String voltar() {
		descricaoPadrao = new PdtDescPadrao();
		this.setEspSeq(null);
		this.setSeqp(null);
		ativo = true;		
		this.setDescricaoPadrao(new PdtDescPadrao());
		return PAGE_LIST_DESCRICAO_PADRAO_PDT;
	}
	
	
	public String gravar() {
		try {
			String msgRetorno = this.blocoCirurgicoCadastroApoioFacade.persistirPdtDescPadrao(this.getDescricaoPadrao());
			this.apresentarMsgNegocio(Severity.INFO,msgRetorno,
					this.getDescricaoPadrao().getTitulo());			
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return this.voltar();
	}
	
	public void limpar() {
		resetarDescricaoPadrao();
	}
	
	public PdtDescPadrao getDescricaoPadrao() {
		return descricaoPadrao;
	}

	public void setDescricaoPadrao(PdtDescPadrao DescricaoPadrao) {
		this.descricaoPadrao = DescricaoPadrao;
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


	public Short getEspSeq() {
		return espSeq;
	}


	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	
}
