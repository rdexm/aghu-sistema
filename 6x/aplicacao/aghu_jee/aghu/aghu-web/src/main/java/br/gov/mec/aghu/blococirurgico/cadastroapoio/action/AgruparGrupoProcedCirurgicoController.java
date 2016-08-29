package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupoId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AgruparGrupoProcedCirurgicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -966313022600395008L;

	private static final String AGRUPAR_PROCED = "agruparProcedimentoCirurgico";

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private MbcProcedimentoCirurgicos procedCirurgico;
	
	private MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico;
	
	private List<MbcProcedimentoPorGrupo> lista;
	
	private Short seqMbcGrupoProcedCirurgico;

	public void inicio() {

		this.procedCirurgico = null;
		this.mbcGrupoProcedCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterMbcGrupoProcedCirurgicoPorChavePrimaria(seqMbcGrupoProcedCirurgico);
		this.lista = this.blocoCirurgicoCadastroApoioFacade.listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
	}
	

	public void gravar() {
		try {
			final MbcProcedimentoPorGrupo mppg = new MbcProcedimentoPorGrupo();
			mppg.setId(new MbcProcedimentoPorGrupoId(mbcGrupoProcedCirurgico.getSeq(),procedCirurgico.getSeq()));
			mppg.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			mppg.setMbcProcedimentoCirurgicos(procedCirurgico);
			
			this.blocoCirurgicoCadastroApoioFacade.persistirMbcProcedimentoPorGrupo(mppg);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPO_PROCED_CIRURGICO_INSERT_SUCESSO",
														   this.procedCirurgico.getDescricao());
			
			this.procedCirurgico = null;
			this.lista = null;

			this.inicio();
			
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void excluir(Integer seqExcluir) {
		final MbcProcedimentoPorGrupo mppg = blocoCirurgicoCadastroApoioFacade.obterMbcProcedimentoPorGrupoPorChavePrimaria(
													new MbcProcedimentoPorGrupoId(mbcGrupoProcedCirurgico.getSeq(), seqExcluir));
		
		String descricao = "";
		
		MbcProcedimentoCirurgicos mpci = blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(mppg.getId().getPciSeq());
		
		if (mpci != null) {
			descricao = mpci.getDescricao();
		}
		this.blocoCirurgicoCadastroApoioFacade.removerMbcProcedimentoPorGrupo(mppg);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPO_PROCED_CIRURGICO_DELETE_SUCESSO", descricao);
		this.lista = null;
		this.inicio();
	}

	
	// Suggestion Convênio
	public List<MbcProcedimentoCirurgicos> pesquisarMbcProcedimentoCirurgicos(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro),pesquisarMbcProcedimentoCirurgicosCount(filtro));
	}
	
	// Suggestion Convênio
	public Long pesquisarMbcProcedimentoCirurgicosCount(String filtro) {
		return this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicosCount(filtro);
	}

	public String voltar() {
		return AGRUPAR_PROCED;
	}
	
	public MbcProcedimentoCirurgicos getProcedCirurgico() {
		return procedCirurgico;
	}

	public void setProcedCirurgico(MbcProcedimentoCirurgicos procedCirurgico) {
		this.procedCirurgico = procedCirurgico;
	}

	public MbcGrupoProcedCirurgico getMbcGrupoProcedCirurgico() {
		return mbcGrupoProcedCirurgico;
	}

	public void setMbcGrupoProcedCirurgico(
			MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) {
		this.mbcGrupoProcedCirurgico = mbcGrupoProcedCirurgico;
	}

	public List<MbcProcedimentoPorGrupo> getLista() {
		return lista;
	}

	public void setLista(List<MbcProcedimentoPorGrupo> lista) {
		this.lista = lista;
	}

	public Short getSeqMbcGrupoProcedCirurgico() {
		return seqMbcGrupoProcedCirurgico;
	}

	public void setSeqMbcGrupoProcedCirurgico(Short seqMbcGrupoProcedCirurgico) {
		this.seqMbcGrupoProcedCirurgico = seqMbcGrupoProcedCirurgico;
	}
	
	
	
	
	
}
